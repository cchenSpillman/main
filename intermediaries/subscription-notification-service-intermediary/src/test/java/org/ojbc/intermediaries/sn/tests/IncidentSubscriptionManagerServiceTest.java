package org.ojbc.intermediaries.sn.tests;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

public class IncidentSubscriptionManagerServiceTest  extends AbstractSubscriptionNotificationTest {
	
    @Resource  
    private DataSource dataSource;  
    
    @Value("${publishSubscribe.subscriptionManagerEndpoint}")
    private String subscriptionManagerUrl;
    
	@Before
	public void setUp() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), getDataSet());
	}
	
	@After
	public void tearDown() throws Exception {
        //DatabaseOperation.DELETE_ALL.execute(getConnection(), getDataSet());
	}
	
    private IDataSet getDataSet() throws Exception{  
        // get insert data  
    	return new FlatXmlDataSetBuilder().build(new FileInputStream(
				"src/test/resources/xmlInstances/dbUnit/subscriptionDataSet_afterSubscribeIncident.xml"));
    }  

	private IDatabaseConnection getConnection() throws Exception {
        Connection con = dataSource.getConnection();  
        IDatabaseConnection connection = new DatabaseConnection(con);

        return connection;  
	}

	@Test
	public void unsubscribe() throws Exception {
	    File subscriptionInputFile = new File("src/test/resources/xmlInstances/unSubscribeSoapRequest-incident.xml");
	    String subscriptionBody = FileUtils.readFileToString(subscriptionInputFile);
	    
		String response = callServiceViaHttp(subscriptionBody);
		
		//System.out.println(response);
		assertThat(response, containsString("<wsnt:UnsubscribeResponse xmlns:wsnt=\"http://docs.oasis-open.org/wsn/b-2\"/>"));
	    
		// verify DB contents after msg send
        // Fetch database data after executing your code
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable filteredActualSubscriptionTable =  getFilteredTableFromDataset(databaseDataSet, "subscription");
		ITable filteredActualNotficationMechanismTable = getFilteredTableFromDataset(databaseDataSet, "notification_mechanism");
		ITable filteredActualSubjectIdentiferTable = getFilteredTableFromDataset(databaseDataSet, "subscription_subject_identifier");
        
		// Load expected data from an XML dataset
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("src/test/resources/xmlInstances/dbUnit/subscriptionDataSet_afterUnSubscribeIncident.xml"));
        ITable filteredExpectedSubscriptionTable = getFilteredTableFromDataset(expectedDataSet, "subscription");
        ITable filteredExpectedNotficationMechanismTable = getFilteredTableFromDataset(expectedDataSet, "notification_mechanism");
        ITable filteredExpectedSubjectIdentiferTable = getFilteredTableFromDataset(expectedDataSet, "subscription_subject_identifier");
        
        // Assert actual database table match expected table
        Assertion.assertEquals(filteredExpectedSubscriptionTable, filteredActualSubscriptionTable);
        Assertion.assertEquals(filteredExpectedNotficationMechanismTable, filteredActualNotficationMechanismTable);
        Assertion.assertEquals(filteredExpectedSubjectIdentiferTable, filteredActualSubjectIdentiferTable);
	}
	
	private ITable getFilteredTableFromDataset(IDataSet dataSet, String tableName) throws Exception {
        ITable table = dataSet.getTable(tableName);
        ITable filteredTable = DefaultColumnFilter.excludedColumnsTable(table, new String[]{"id*", "subscriptionId", "*date", "timestamp"});
        
        return filteredTable;
	}

	private String callServiceViaHttp(String msgBody) throws Exception {
		HttpClient client = new DefaultHttpClient();
		
		HttpPost post = new HttpPost(subscriptionManagerUrl);
		post.setEntity(new StringEntity(msgBody));
		HttpResponse response = client.execute(post);
		HttpEntity reply = response.getEntity();
		return IOUtils.readStringFromStream(reply.getContent());
	}
}