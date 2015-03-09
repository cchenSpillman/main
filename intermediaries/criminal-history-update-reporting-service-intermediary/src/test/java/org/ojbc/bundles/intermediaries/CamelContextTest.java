package org.ojbc.bundles.intermediaries;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.MessageImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ojbc.util.camel.helper.OJBUtils;
import org.ojbc.util.xml.XmlUtils;
import org.springframework.test.context.ContextConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@UseAdviceWith	// NOTE: this causes Camel contexts to not start up automatically
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:META-INF/spring/camel-context.xml", 
		"classpath:META-INF/spring/cxf-endpoints.xml",
		"classpath:META-INF/spring/extensible-beans.xml",		
		"classpath:META-INF/spring/local-osgi-context.xml",
		"classpath:META-INF/spring/properties-context.xml"}) 
public class CamelContextTest {

    @Resource
    private ModelCamelContext context;
    
    @Produce
    protected ProducerTemplate template;
    
    @EndpointInject(uri = "mock:cxf:bean:notificationBrokerService")
    protected MockEndpoint notificationBrokerMockEndpoint;

    @EndpointInject(uri = "mock:log:org.ojbc.intermediaries.crimhistoryupdate")
    protected MockEndpoint loggingEndpoint;

	public static final String CXF_OPERATION_NAME_CRIMINAL_HISTORY = "ReportCycleTrackingIdentifierAssignment";
	public static final String CXF_OPERATION_NAMESPACE_CRIMINAL_HISTORY = "http://ojbc.org/Services/WSDL/CriminalHistoryUpdateReportingService/1.0";

	private static final Log log = LogFactory.getLog( CamelContextTest.class );
	
    @Test
    public void testApplicationStartup()
    {
    	assertTrue(true);
    }	
    
	@Before
	public void setUp() throws Exception {
		
    	//We replace the 'from' web service endpoint with a direct endpoint we call in our test
    	context.getRouteDefinition("CriminalHistoryUpdateReportingServiceHandlerRoute").adviceWith(context, new AdviceWithRouteBuilder() {
    	    @Override
    	    public void configure() throws Exception {
    	    	// The line below allows us to bypass CXF and send a message directly into the route
    	    	replaceFromWith("direct:criminalHistoryUpdatedReportingService");
    	    	mockEndpoints("log:org.ojbc.intermediaries.crimhistoryupdate*");
    	    }              
    	});

    	//We mock the web service endpoints here
    	context.getRouteDefinition("callNotificationBrokerRoute").adviceWith(context, new AdviceWithRouteBuilder() {
    	    @Override
    	    public void configure() throws Exception {
    	    	
    	    	//We mock the notification broker endpoint
    	    	mockEndpointsAndSkip("cxf:bean:notificationBrokerService*");
    	    	
    	    }              
    	});

    	context.start();		
    	
	}	
	
    @Test
    public void testNotificationBroker() throws Exception {
    
    	
    	notificationBrokerMockEndpoint.expectedMessageCount(1);
    	loggingEndpoint.expectedMessageCount(1);
    	
    	//Create a new exchange
    	Exchange senderExchange = new DefaultExchange(context);
				
    	//Set the WS-Address Message ID
		Map<String, Object> requestContext = OJBUtils.setWSAddressingMessageID("123456789");
		
		//Set the operation name and operation namespace for the CXF exchange
		senderExchange.getIn().setHeader(Client.REQUEST_CONTEXT , requestContext);
		
		Document doc = createDocument();
		List<SoapHeader> soapHeaders = new ArrayList<SoapHeader>();
		soapHeaders.add(makeSoapHeader(doc, "http://www.w3.org/2005/08/addressing", "MessageID", "12345"));
		senderExchange.getIn().setHeader(Header.HEADER_LIST , soapHeaders);
		
		org.apache.cxf.message.Message message = new MessageImpl();

		senderExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_MESSAGE, message);
 	    senderExchange.getIn().setHeader(CxfConstants.OPERATION_NAME, CXF_OPERATION_NAME_CRIMINAL_HISTORY);
	    senderExchange.getIn().setHeader(CxfConstants.OPERATION_NAMESPACE, CXF_OPERATION_NAMESPACE_CRIMINAL_HISTORY);
    	
	    //Read the firearm search request file from the file system
	    File inputFile = new File("src/test/resources/xmlInstances/cycleTrackingIdentifierAssignmentReport/Cycle-Tracking-Identifier-Assignment-Report.xml");
	    String inputStr = FileUtils.readFileToString(inputFile);

	    assertNotNull(inputStr);
	    
	    log.debug(inputStr);
	    
	    //Set it as the message message body
	    senderExchange.getIn().setBody(inputStr);
	    
	    //Send the one-way exchange.  Using template.send will send an one way message
		Exchange returnExchange = template.send("direct:criminalHistoryUpdatedReportingService", senderExchange);
		
		//Use getException to see if we received an exception
		if (returnExchange.getException() != null)
		{	
			throw new Exception(returnExchange.getException());
		}	

		//Sleep while a response is generated
		Thread.sleep(3000);
		
		//Assert that the mock endpoint expectations are satisfied
		notificationBrokerMockEndpoint.assertIsSatisfied();
		loggingEndpoint.assertIsSatisfied();
		
		//Get the first exchange (the only one)
		Exchange ex = notificationBrokerMockEndpoint.getExchanges().get(0);
		
		String opName = (String)ex.getIn().getHeader("operationName");
		assertEquals("Notify", opName);
		
		String opNamespace = (String)ex.getIn().getHeader("operationNamespace");
		assertEquals("http://docs.oasis-open.org/wsn/brw-2", opNamespace);

		Document returnDocumentNotificationBroker = ex.getIn().getBody(Document.class);

		//Do some very basic assertions to assure the message is transformed.
		//The XSLT test does a more complete examination of the transformation.
		Node notifyNode = XmlUtils.xPathNodeSearch(returnDocumentNotificationBroker, "/b-2:Notify");
		Node notifyMesssageNode = XmlUtils.xPathNodeSearch(notifyNode, "b-2:NotificationMessage");
		
		Node messageNode = XmlUtils.xPathNodeSearch(notifyMesssageNode, "b-2:Message");
		assertNotNull(messageNode);

		//Get the first exchange (the only one) to the logger
		//This is what would be sent to the member specific route
		Exchange exMemberSpecific = loggingEndpoint.getExchanges().get(0);

		Document returnDocumentMemberSpecific = exMemberSpecific.getIn().getBody(Document.class);

		//Make sure the root node here is the message to the orignal exchange
		Node rootNode = XmlUtils.xPathNodeSearch(returnDocumentMemberSpecific, "/crimhistory-update-exch:CycleTrackingIdentifierAssignmentReport");
		assertNotNull(rootNode);

		//XmlUtils.printNode(returnDocumentMemberSpecific);
    }

	private SoapHeader makeSoapHeader(Document doc, String namespace, String localName, String value) {
		Element messageId = doc.createElementNS(namespace, localName);
		messageId.setTextContent(value);
		SoapHeader soapHeader = new SoapHeader(new QName(namespace, localName), messageId);
		return soapHeader;
	}	
	
	public static Document createDocument() throws Exception{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument();

		return doc;
	}
}