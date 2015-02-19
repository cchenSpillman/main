package org.ojbc.intermediaries.sn.topic.arrest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ojbc.intermediaries.sn.SubscriptionNotificationConstants;
import org.ojbc.intermediaries.sn.notification.NotificationRequest;
import org.ojbc.intermediaries.sn.notification.Offense;
import org.ojbc.intermediaries.sn.util.NotificationBrokerUtils;
import org.ojbc.util.xml.XmlUtils;

import org.apache.camel.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ArrestNotificationRequest extends NotificationRequest {
	
	private static final Log log = LogFactory.getLog( ArrestNotificationRequest.class );
	
	private String attorneyGeneralIndicator;

	private String incidentId;
	private String incidentDate;
	
	private String incidentLocationCity;
	private String incidentLocationState;
	private String incidentLocationStreet;
	private String incidentLocationStreetNumber;
	private String incidentLocationStreetFullText;

	private String arresteeLastKnownAddressCity;
	private String arresteeLastKnownAddressState;
	private String arresteeLastKnownAddressStreet;
	private String arresteeLastKnownAddressStreetNumber;
	private String arresteeLastKnownAddressStreetFullText;
	
	private List<Offense> arrestOffenses = new ArrayList<Offense>();
	
	public ArrestNotificationRequest(Message message) throws Exception{
	    this(message.getBody(Document.class));
	}

	public ArrestNotificationRequest(Document document) throws Exception {
	    super(document);
		String sid = XmlUtils.xPathStringSearch(document,
				"/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/jxdm41:Person[@s:id=../nc:ActivityInvolvedPersonAssociation/nc:PersonReference/@s:ref]/jxdm41:PersonAugmentation/jxdm41:PersonStateFingerprintIdentification/nc:IdentificationID");
		
		log.debug("The SID in the notification message is: " + sid);
		
		buildSubjectIdMap(sid);
		
		attorneyGeneralIndicator = XmlUtils.xPathStringSearch(document,"/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/notfm-ext:NotifyingActivityExtensions/hawaii-ext:AttorneyGeneralCaseDetails/hawaii-ext:AttorneyGeneralCaseIndicator");
		incidentId = XmlUtils.xPathStringSearch(document,   "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Incident/nc:ActivityIdentification/nc:IdentificationID");
		incidentDate = XmlUtils.xPathStringSearch(document, "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Incident/nc:ActivityDate/nc:DateTime");
		
		Node incidentLocationNode = XmlUtils.xPathNodeSearch(document, "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Incident/nc:IncidentLocation/nc:LocationAddress/nc:StructuredAddress");

		if (incidentLocationNode != null)
		{
			incidentLocationCity = XmlUtils.xPathStringSearch(incidentLocationNode, "nc:LocationCityName").trim();
			incidentLocationState=XmlUtils.xPathStringSearch(incidentLocationNode, "nc:LocationStateName").trim();
			incidentLocationStreetNumber=XmlUtils.xPathStringSearch(incidentLocationNode, "nc:LocationStreet/nc:StreetNumberText").trim();
			incidentLocationStreet=XmlUtils.xPathStringSearch(incidentLocationNode, "nc:LocationStreet/nc:StreetName").trim();
			incidentLocationStreetFullText=XmlUtils.xPathStringSearch(incidentLocationNode, "nc:LocationStreet/nc:StreetFullText").trim();
		}	

		if (StringUtils.isBlank(incidentLocationStreetFullText))
		{
			StringBuilder addressFullText = new StringBuilder("");
			
			if (StringUtils.isNotBlank(incidentLocationStreetNumber))
			{	
				addressFullText.append(incidentLocationStreetNumber);
				addressFullText.append(" ");
			}

			if (StringUtils.isNotBlank(incidentLocationStreet))
			{	
				addressFullText.append(incidentLocationStreet);
			}
			
			incidentLocationStreetFullText = addressFullText.toString().trim();
		}	
		
		Node arresteeLastKnowAddressNode = XmlUtils.xPathNodeSearch(document, "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/nc:ContactInformation[@s:id=/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/nc:PersonContactInformationAssociation[nc:PersonReference/@s:ref=/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/jxdm41:Person/@s:id[.= /b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/jxdm41:ArrestSubject/nc:RoleOfPersonReference/@s:ref]]/nc:ContactInformationReference/@s:ref]/nc:ContactMailingAddress/nc:StructuredAddress");

		if (arresteeLastKnowAddressNode != null)
		{
			arresteeLastKnownAddressCity = XmlUtils.xPathStringSearch(arresteeLastKnowAddressNode, "nc:LocationCityName").trim();
			arresteeLastKnownAddressState=XmlUtils.xPathStringSearch(arresteeLastKnowAddressNode, "nc:LocationStateName").trim();
			arresteeLastKnownAddressStreetNumber=XmlUtils.xPathStringSearch(arresteeLastKnowAddressNode, "nc:LocationStreet/nc:StreetNumberText").trim();
			arresteeLastKnownAddressStreet=XmlUtils.xPathStringSearch(arresteeLastKnowAddressNode, "nc:LocationStreet/nc:StreetName").trim();
			arresteeLastKnownAddressStreetFullText=XmlUtils.xPathStringSearch(arresteeLastKnowAddressNode, "nc:LocationStreet/nc:StreetFullText").trim();
		}	

		if (StringUtils.isBlank(arresteeLastKnownAddressStreetFullText))
		{
			StringBuilder addressFullText = new StringBuilder("");
			
			if (StringUtils.isNotBlank(arresteeLastKnownAddressStreetNumber))
			{	
				addressFullText.append(arresteeLastKnownAddressStreetNumber);
				addressFullText.append(" ");
			}

			if (StringUtils.isNotBlank(arresteeLastKnownAddressStreet))
			{	
				addressFullText.append(arresteeLastKnownAddressStreet);
			}
			
			arresteeLastKnownAddressStreetFullText = addressFullText.toString().trim();
		}	

		
        NodeList offenseNodes = XmlUtils.xPathNodeListSearch(requestDocument, "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Offense");
        
        arrestOffenses = NotificationBrokerUtils.returnOffenseNodes(offenseNodes);

	}
	
	public String getPersonBookingName() {
	    StringBuffer personBookingName = new StringBuffer();

        personBookingName.append(getPersonLastName());

        if (StringUtils.isNotEmpty(getPersonNameSuffix())) {
            personBookingName.append(" " + getPersonNameSuffix());
        }

        personBookingName.append(", " + getPersonFirstName());

        if (StringUtils.isNotEmpty(getPersonMiddleName())) {
            personBookingName.append(" " + getPersonMiddleName());
        }
        return personBookingName.toString();
	}
	
	public String getBookingDateTimeDisplay() {
	    String bookingDateTimeDisplay = NotificationBrokerUtils.returnFormattedNotificationEventDate(getNotificationEventDate(),
                isNotificationEventDateInclusiveOfTime());
	    return bookingDateTimeDisplay;
	}
	
	public List<String> getArrestCharges() throws Exception {
	    
	    NodeList arrestCharges = XmlUtils.xPathNodeListSearch(getRequestDocument(), "//notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/jxdm41:ArrestCharge");
        
        List<String> arrestChargeStrings = new ArrayList<String>();

        if (arrestCharges != null && arrestCharges.getLength() > 0) {
            for (int i = 0; i < arrestCharges.getLength(); i++) {
                if (arrestCharges.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    StringBuffer arrestChargeText = new StringBuffer();
                    String statuteDescriptionText = XmlUtils.xPathStringSearch(arrestCharges.item(i), "jxdm41:ChargeDescriptionText");
                    String chargeSeverity = XmlUtils.xPathStringSearch(arrestCharges.item(i), "jxdm41:ChargeSeverityText");
                    String arn = XmlUtils.xPathStringSearch(arrestCharges.item(i), "jxdm41:ChargeIdentification/nc:IdentificationID");

                    arrestChargeText.append("Description: ");
                    arrestChargeText.append(statuteDescriptionText);

                    arrestChargeText.append(" Severity: ");
                    arrestChargeText.append(chargeSeverity);

                    arrestChargeText.append(", ");

                    arrestChargeText.append("ARN: ");
                    arrestChargeText.append(arn);
                    
                    arrestChargeStrings.add(arrestChargeText.toString());

                }
            }
        }
        
        return Collections.unmodifiableList(arrestChargeStrings);
        
	}
	
	private void buildSubjectIdMap(String sid) {
		subjectIdentifiers = new HashMap<String, String>();
		subjectIdentifiers.put(SubscriptionNotificationConstants.SID, sid);
	}

	@Override
	protected String getNotificationEventDateRootXpath() {

		return "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/nc:ActivityDate";
	}

	@Override
	protected String getNotifyingAgencyXpath() {

		return "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/jxdm41:ArrestAgency/nc:OrganizationName";
	}

	@Override
	protected String getNotificationEventIdentifierXpath() {

		return "/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/nc:ActivityIdentification/nc:IdentificationID";
	}

	public String getAttorneyGeneralIndicator() {
		return attorneyGeneralIndicator;
	}

    public List<String> getOffenseStrings() {
    	return NotificationBrokerUtils.getOffenseStrings(arrestOffenses);
    }
	
	@Override
	protected String getNotificationAgencyPhoneNumberXpath() {
		// TODO Auto-generated method stub
		// TODO Add this to IEPD if required in notification message
		return "''";
	}

	@Override
	protected String getNotifyingSystemNameXPath() {
		// TODO Auto-generated method stub
		// TODO Add this to IEPD if required in notification message
		return  "''";
	}

	@Override
	public String getDescriptiveSubjectIdentifier() {
	    log.debug("Requesting descriptive subject identifier: " + getSubjectIdentifiers());
		return getSubjectIdentifiers().get(SubscriptionNotificationConstants.SID);
	}

	@Override
	protected String getOfficerNameReferenceXPath() {
		return	"/b-2:Notify/b-2:NotificationMessage/b-2:Message/notfm-exch:NotificationMessage/notfm-ext:NotifyingArrest/jxdm41:Arrest/jxdm41:ArrestOfficial/nc:RoleOfPersonReference/@s:ref";
	}

	public String getIncidentId() {
		return incidentId;
	}

	public String getIncidentDate() {
		return incidentDate;
	}

	public String getIncidentLocationCity() {
		return incidentLocationCity;
	}

	public String getIncidentLocationState() {
		return incidentLocationState;
	}

	public String getIncidentLocationStreet() {
		return incidentLocationStreet;
	}

	public String getIncidentLocationStreetNumber() {
		return incidentLocationStreetNumber;
	}

	public String getArresteeLastKnownAddressCity() {
		return arresteeLastKnownAddressCity;
	}

	public String getArresteeLastKnownAddressState() {
		return arresteeLastKnownAddressState;
	}

	public String getArresteeLastKnownAddressStreet() {
		return arresteeLastKnownAddressStreet;
	}

	public String getArresteeLastKnownAddressStreetNumber() {
		return arresteeLastKnownAddressStreetNumber;
	}

	public String getIncidentLocationStreetFullText() {
		return incidentLocationStreetFullText;
	}

	public String getArresteeLastKnownAddressStreetFullText() {
		return arresteeLastKnownAddressStreetFullText;
	}

	public String getInicidentLocationCompleteAddress() {
		
		StringBuilder ret = new StringBuilder("");
		
		if (StringUtils.isNotBlank(incidentLocationStreetFullText))
		{
			ret.append(incidentLocationStreetFullText);
			ret.append(" ");
		}

		if (StringUtils.isNotBlank(incidentLocationCity))
		{
			ret.append(incidentLocationCity);
			ret.append(" ");
		}

		if (StringUtils.isNotBlank(incidentLocationState))
		{
			ret.append(incidentLocationState);
		}

		return ret.toString().trim();
	}
	
	public String getArresteeLastKnownAddressCompleteAddress() {
		
		StringBuilder ret = new StringBuilder("");
		
		if (StringUtils.isNotBlank(arresteeLastKnownAddressStreetFullText))
		{
			ret.append(arresteeLastKnownAddressStreetFullText);
			ret.append(" ");
		}

		if (StringUtils.isNotBlank(arresteeLastKnownAddressCity))
		{
			ret.append(arresteeLastKnownAddressCity);
			ret.append(" ");
		}

		if (StringUtils.isNotBlank(arresteeLastKnownAddressState))
		{
			ret.append(arresteeLastKnownAddressState);
		}

		return ret.toString().trim();
	}

}