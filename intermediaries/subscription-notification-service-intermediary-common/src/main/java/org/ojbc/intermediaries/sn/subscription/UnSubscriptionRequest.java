package org.ojbc.intermediaries.sn.subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ojbc.util.xml.XmlUtils;

import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class UnSubscriptionRequest {
	protected Document document;
	
	protected String topic;
	protected List<String> emailAddresses;
	protected String systemName;
	protected String subscriptionQualifier;
	protected String subscriptionSystemId;
	protected Map<String, String> subjectIdentifiers;
	
	public UnSubscriptionRequest(Message message) throws Exception{
		//Get the message body as a DOM
		document = message.getBody(Document.class);
		
		topic = XmlUtils.xPathStringSearch(document, "//b-2:Unsubscribe/b-2:TopicExpression");
		topic = StringUtils.replace(topic, "topics:", "{http://ojbc.org/wsn/topics}:");
		
		Node subscriptionMsg = XmlUtils.xPathNodeSearch(document,"//unsubmsg-exch:UnsubscriptionMessage");
		
		NodeList emailNodes = XmlUtils.xPathNodeListSearch(subscriptionMsg,"nc:ContactEmailID");
		
	    if (emailNodes != null && emailNodes.getLength() > 0) {
	        for (int i = 0; i < emailNodes.getLength(); i++) {
	            if (emailNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
	    			if (emailAddresses == null)
	    			{
	    				emailAddresses = new ArrayList<String>();
	    			}	
	    			
	    			emailAddresses.add(emailNodes.item(i).getTextContent());
	            	
	            }
	        }
	    }
		
		systemName = XmlUtils.xPathStringSearch(subscriptionMsg,"submsg-ext:SystemName");
		subscriptionQualifier = XmlUtils.xPathStringSearch(subscriptionMsg,"submsg-ext:SubscriptionQualifierIdentification/nc:IdentificationID");
		subscriptionSystemId = XmlUtils.xPathStringSearch(subscriptionMsg,"submsg-ext:SubscriptionIdentification/nc:IdentificationID");

		// subjectIdentifier intentionally left out - should be populated by derived class 
	}
	
	public UnSubscriptionRequest(String topic, List<String> emailAddresses, String systemName, String subscriptionQualifier) {
		this.topic = topic;
		this.emailAddresses = emailAddresses;
		this.systemName = systemName;
		this.subscriptionQualifier = subscriptionQualifier;
	}
	
	public String getTopic() {
		return topic;
	}
		
	public String getSystemName() {
		return systemName;
	}
	
	public String getSubscriptionQualifier() {
		return subscriptionQualifier;
	}
	
	public Map<String, String> getSubjectIdentifiers() {
		return subjectIdentifiers;
	}

	public String getSubscriptionSystemId() {
		return subscriptionSystemId;
	}

	public List<String> getEmailAddresses() {
		return emailAddresses;
	}
	
}