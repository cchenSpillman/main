package org.ojbc.intermediaries.sn.topic.arrest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.ojbc.intermediaries.sn.SubscriptionNotificationConstants;

import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;

public class ArrestUnSubscriptionRequestTest {

	Map<String, String> namespaceUris;
	
	@Before
	public void setup() {
		namespaceUris = new HashMap<String, String>();
		namespaceUris.put("wsnb2", "http://docs.oasis-open.org/wsn/b-2");
		namespaceUris.put("um", "http://ojbc.org/IEPD/Exchange/UnsubscriptionMessage/1.0");
		namespaceUris.put("smext", "http://ojbc.org/IEPD/Extensions/Subscription/1.0");
		namespaceUris.put("nc20", "http://niem.gov/niem/niem-core/2.0");
		namespaceUris.put("jxdm41", "http://niem.gov/niem/domains/jxdm/4.1");
	}
	
	@Test
	public void test() throws Exception {
		Message message = Mockito.mock(Message.class);
		
		Mockito.when(message.getBody(Document.class)).thenReturn(getMessageBody());
		
		ArrestUnSubscriptionRequest request = new ArrestUnSubscriptionRequest(message);
		
		assertThat(request.getSubscriptionQualifier(), is("1234578"));
		Assert.assertNotNull(request.getEmailAddresses());
		assertThat(request.getTopic(), is("{http://ojbc.org/wsn/topics}:person/arrest"));
		assertThat(request.getSubjectIdentifiers().size(), is(2));
		assertThat(request.getSubjectIdentifiers().get(SubscriptionNotificationConstants.SID), is("A9999999"));
		assertThat(request.getSubjectIdentifiers().get(SubscriptionNotificationConstants.SUBSCRIPTION_QUALIFIER), is("1234578"));
	}

	private Document getMessageBody() throws Exception {
		
		File inputFile = new File("src/test/resources/xmlInstances/unSubscribeSoapRequest.xml");

		DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
		docBuilderFact.setNamespaceAware(true);
		DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
		Document document = docBuilder.parse(inputFile);
		
		return document;
	}
}