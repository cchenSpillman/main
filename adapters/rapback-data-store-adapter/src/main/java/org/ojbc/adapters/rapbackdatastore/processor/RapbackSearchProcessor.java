/*
 * Unless explicitly acquired and licensed from Licensor under another license, the contents of
 * this file are subject to the Reciprocal Public License ("RPL") Version 1.5, or subsequent
 * versions as allowed by the RPL, and You may not copy or use this file in either source code
 * or executable form, except in compliance with the terms and conditions of the RPL
 *
 * All software distributed under the RPL is provided strictly on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND LICENSOR HEREBY DISCLAIMS ALL SUCH
 * WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific language
 * governing rights and limitations under the RPL.
 *
 * http://opensource.org/licenses/RPL-1.5
 *
 * Copyright 2012-2015 Open Justice Broker Consortium
 */
package org.ojbc.adapters.rapbackdatastore.processor;

import static org.ojbc.util.xml.OjbcNamespaceContext.NS_INTEL;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_JXDM_41;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_NC;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_NC_30;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_INTEL;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_JXDM_41;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_NC;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_SEARCH_REQUEST_ERROR_REPORTING;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_SEARCH_RESULTS_METADATA_EXT;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_STRUCTURES;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_PREFIX_WSN_BROKERED;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_SEARCH_REQUEST_ERROR_REPORTING;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_SEARCH_RESULTS_METADATA_EXT;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_STRUCTURES;
import static org.ojbc.util.xml.OjbcNamespaceContext.NS_WSN_BROKERED;

import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.ExchangeException;
import org.apache.camel.Header;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.Message;
import org.ojbc.adapters.rapbackdatastore.dao.RapbackDAO;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilInitialResults;
import org.ojbc.adapters.rapbackdatastore.dao.model.Subject;
import org.ojbc.util.camel.security.saml.SAMLTokenUtils;
import org.ojbc.util.model.saml.SamlAttribute;
import org.ojbc.util.xml.XmlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class RapbackSearchProcessor {
    private static final String SYSTEM_NAME = "RapbackDataStore";

	private static final String SOURCE_SYSTEM_NAME_TEXT = 
    		"http://ojbc.org/Services/WSDL/Organization_Identification_Results_Search_Request_Service/Subscriptions/1.0}RapbackDatastore";

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	private final Log log = LogFactory.getLog(this.getClass());

    @Resource
    private RapbackDAO rapbackDAO;

    private DocumentBuilder documentBuilder;

    @Value("${system.searchResultsExceedThreshold}")
    private Integer maxResultCount;

    @Value("${system.name}")
    private String systemName;

    public RapbackSearchProcessor() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    /**
     * 
     * @param federationId
     * @return a XML string response abide by the Access Control Response
     *         schema.
     */
    public Document returnRapbackSearchResponse(@Header(CxfConstants.CAMEL_CXF_MESSAGE) Message cxfMessage) {
    	
    	List<CivilInitialResults> civilInitialResults = null; 
    	
        if (cxfMessage != null) {
            String federationId = SAMLTokenUtils.getSamlAttributeFromCxfMessage(cxfMessage, SamlAttribute.FederationId);
            String employerOri = SAMLTokenUtils.getSamlAttributeFromCxfMessage(cxfMessage, SamlAttribute.EmployerORI); 

            log.debug("Processing rapback search request for federationId:" + StringUtils.trimToEmpty(federationId));
            log.debug("Employer ORI : " + StringUtils.trimToEmpty(employerOri)); 
            
            if (StringUtils.isNotBlank(federationId)) {
                //Pass the ORIs in the SAML assertion to the DAO method. 
                civilInitialResults = rapbackDAO.getCivilInitialResults(employerOri);
                log.info("Get rapback search results count");
            } else {
                throw new IllegalArgumentException(
                        "Either request is missing SAML assertion or the federation "
                        + "ID or Employer ORI is missing in the SAML assertion. ");
            }
        } else {
            throw new IllegalArgumentException(
                    "Invalid request. CXF message is not found.");
        }

        Document rapbackSearchResponseDocument = buildRapbackSearchResponse(civilInitialResults);

        return rapbackSearchResponseDocument;
    }
    
    private Document buildRapbackSearchResponse(List<CivilInitialResults> civilInitialResults) {

        Document document = documentBuilder.newDocument();
        Element rootElement = createRapbackSearchResponseRootElement(document);
        
        for (CivilInitialResults civilInitialResult : civilInitialResults){
        	Element organizationIdentificationResultsSearchResultElement = 
        			XmlUtils.appendElement(rootElement, NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, 
        					"OrganizationIdentificationResultsSearchResult");
        	appendIdentifiedPersonElement(organizationIdentificationResultsSearchResultElement, civilInitialResult);
        	appdendStatusElement(organizationIdentificationResultsSearchResultElement,
					civilInitialResult);
        	//TODO add subscripion info if available. 
        	
        	appendSourceSystemNameTextElement(organizationIdentificationResultsSearchResultElement);
        	
        	Element systemIdentifierElement = XmlUtils.appendElement(
        			organizationIdentificationResultsSearchResultElement, NS_INTEL, "SystemIdentifierElement");
        	Element identificationIdElement = XmlUtils.appendElement(systemIdentifierElement, NS_NC, "IdentificationID"); 
        	identificationIdElement.setTextContent("9876543"); //Find out what value should be put here. 
        	Element systemNameElement = XmlUtils.appendElement(systemIdentifierElement, NS_INTEL, "SystemName");
        	systemNameElement.setTextContent(SYSTEM_NAME);
        }
        return document;
    }

	private void appendSourceSystemNameTextElement(
			Element organizationIdentificationResultsSearchResultElement) {
		Element sourceSystemNameText = XmlUtils.appendElement(organizationIdentificationResultsSearchResultElement, 
				NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, "SourceSystemNameText");
		sourceSystemNameText.setTextContent(SOURCE_SYSTEM_NAME_TEXT);
	}

	private void appdendStatusElement(
			Element organizationIdentificationResultsSearchResultElement,
			CivilInitialResults civilInitialResult) {
		Element identificationResultStatusCode = XmlUtils.appendElement(
				organizationIdentificationResultsSearchResultElement, 
				NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, "IdentificationResultStatusCode");
		identificationResultStatusCode.setTextContent(civilInitialResult.getCurrentState().toString());
	}

	private void appendIdentifiedPersonElement(Element organizationIdentificationResultsSearchResultElement,
			CivilInitialResults civilInitialResult) {
		
		Subject subject = civilInitialResult.getIdentificationTransaction().getSubject();
		Element identifiedPerson = XmlUtils.appendElement(organizationIdentificationResultsSearchResultElement, 
				NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, "IdentifiedPerson");
		
		if (subject.getDob() != null){
			Element personBirthDateElement = 
					XmlUtils.appendElement(identifiedPerson, NS_NC, "PersonBirthDate");
			Element dateElement = XmlUtils.appendElement(personBirthDateElement, NS_NC, "Date");
			dateElement.setTextContent(subject.getDob().toString(YYYY_MM_DD));
		}
		
		Element personNameElement = XmlUtils.appendElement(identifiedPerson, NS_NC, "PersonName"); 
		Element personFullNameElement = XmlUtils.appendElement(personNameElement, NS_NC, "PersonFullName");
		personFullNameElement.setTextContent(subject.getFullName());
		
		Element identifiedPersonTrackingIdentification = XmlUtils.appendElement(identifiedPerson, 
				NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, "IdentifiedPersonTrackingIdentification");
		Element identificationIdElement = XmlUtils.appendElement(
				identifiedPersonTrackingIdentification, NS_NC, "IdentificationID");
		identificationIdElement.setTextContent(civilInitialResult.getIdentificationTransaction().getOtn());
	}

    private Element createRapbackSearchResponseRootElement(Document document) {
        Element rootElement = document.createElementNS(
        		NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS,
        		NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS +"OrganizationIdentificationResultsSearchResults");
        rootElement.setAttribute("xmlns:"+NS_PREFIX_STRUCTURES, NS_STRUCTURES);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS, 
        		NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT, 
        		NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS_EXT);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_INTEL, NS_INTEL);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_JXDM_41, NS_JXDM_41);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_NC, NS_NC);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_WSN_BROKERED, NS_WSN_BROKERED);
        document.appendChild(rootElement);
		return rootElement;
	}

    public Document buildErrorResponse(@ExchangeException Exception exception) {
        Document document = documentBuilder.newDocument();
        Element rootElement = createErrorResponseRootElement(document);

        Element searchResultsMetadataNode = XmlUtils.appendElement(rootElement,
        		NS_SEARCH_RESULTS_METADATA_EXT, "SearchResultsMetadata");

        Element searchRequestErrorNode = XmlUtils.appendElement(searchResultsMetadataNode, 
        		NS_SEARCH_REQUEST_ERROR_REPORTING, "SearchRequestError");

        Element errorTextNode = XmlUtils.appendElement(searchRequestErrorNode,
        		NS_SEARCH_REQUEST_ERROR_REPORTING, "ErrorText");
        errorTextNode.setTextContent(exception.getMessage());

        Element systemNameNode = XmlUtils.appendElement(searchRequestErrorNode, NS_INTEL,
                "SystemName");
        systemNameNode.setTextContent(systemName);
        return document;
    }

    private Element createErrorResponseRootElement(Document document) {
        Element rootElement = document.createElementNS(
        		NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS,
        		NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS +"OrganizationIdentificationResultsSearchResults");
        rootElement.setAttribute("xmlns:"+NS_PREFIX_STRUCTURES, NS_STRUCTURES);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS, 
        		NS_ORGANIZATION_IDENTIFICATION_RESULTS_SEARCH_RESULTS);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_INTEL, NS_INTEL);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_SEARCH_RESULTS_METADATA_EXT, NS_SEARCH_RESULTS_METADATA_EXT);
        rootElement.setAttribute("xmlns:"+NS_PREFIX_SEARCH_REQUEST_ERROR_REPORTING, NS_SEARCH_REQUEST_ERROR_REPORTING);

        document.appendChild(rootElement);
        return rootElement;
    }


}
