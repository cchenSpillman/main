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
package org.ojbc.web.portal.controllers.helpers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ojbc.web.SearchFieldMetadata;
import org.ojbc.web.model.person.search.PersonSearchRequest;
import org.ojbc.web.portal.controllers.dto.PersonSearchCommand;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:static-configuration-demostate.xml"})
public class SimpleSearchParserTest {

	private static final Log log = LogFactory.getLog(SimpleSearchParserTest.class);
	
	@Resource
    SimpleSearchParser unit;
	private BindingResult errors;
	private PersonSearchCommand personSearchCommand;

	@Before
	public void setup() {
		errors = mock(BindingResult.class);
		personSearchCommand = new PersonSearchCommand();
	}

	@Test
	public void nullSearchTerms() {
		personSearchCommand.setSimpleSearch(null);
		unit.validateAndParseSimpleSearch(personSearchCommand, errors);

		verify(errors).rejectValue("simpleSearch", "emptySearch", "Search terms cannot be empty");
	}

	@Test
	public void emptySearchTerms() {
		personSearchCommand.setSimpleSearch("");
		unit.validateAndParseSimpleSearch(personSearchCommand, errors);

		verify(errors).rejectValue("simpleSearch", "emptySearch", "Search terms cannot be empty");
	}

	@Test
	public void invalidTerm() {
		personSearchCommand.setSimpleSearch("firstName \"last name\" 123-45");

		unit.validateAndParseSimpleSearch(personSearchCommand, errors);

		verify(errors).rejectValue("simpleSearch", "invalidTokens", "Unable to parse the following terms: [123-45]");
	}

	@Test
	public void searchMustHaveSearchSystemSelected() {

		unit.validateAndParseSimpleSearch(personSearchCommand, errors);

		verify(errors).rejectValue("simpleSearch","missingSourceSystems",
                        "No Source Systems to search are selected.");                                           
	}

	@Test
	public void searchMustHaveALastNameOrIdentifier() {
		personSearchCommand.setSimpleSearch("12/22/1983");
		
		unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		
		verify(errors).rejectValue("simpleSearch","missingRequiredInput",
				"Search must have either a last name or an identifier (SSN, SID, DL, or FBI Number)");
	}

	@Test
	public void extractSSN() {
		personSearchCommand.setSimpleSearch("555-66-7777");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSocialSecurityNumber(), is("555-66-7777"));

		personSearchCommand.setSimpleSearch("  \"  777-88-9999  \"  ");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSocialSecurityNumber(), is("777-88-9999"));
	}

	@Test
	public void extractSID() {
		personSearchCommand.setSimpleSearch("A123456");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSID(), is("A123456"));

		personSearchCommand.setSimpleSearch("   \"  A5555555   \"");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSID(), is("A5555555"));
	}

	@Test
	public void extractDOB() {
		personSearchCommand.setSimpleSearch("05/03/1946");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getYear(), is(1946));
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getMonthOfYear(), is(5));
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getDayOfMonth(), is(3));

		personSearchCommand.setSimpleSearch("  \"  05/03/1946   \"  ");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getYear(), is(1946));
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getMonthOfYear(), is(5));
		assertThat(expectedPersonSearchRequest.getPersonDateOfBirth().getDayOfMonth(), is(3));
	}
	
	@Test
	public void extractFBIID() {
		personSearchCommand.setSimpleSearch("1234567");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonFBINumber(), is("1234567"));

		personSearchCommand.setSimpleSearch("  \"  345678VD4   \"  ");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonFBINumber(), is("345678VD4"));
	}

	@Test
	public void extractSurNameExactMatch() {
		personSearchCommand.setSimpleSearch("lastName");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSurName(), is("lastName"));
		assertThat(expectedPersonSearchRequest.getPersonSurNameMetaData(), is(SearchFieldMetadata.ExactMatch));

		personSearchCommand.setSimpleSearch("  \" my last Name  \"");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSurName(), is("my last Name"));
		assertThat(expectedPersonSearchRequest.getPersonSurNameMetaData(), is(SearchFieldMetadata.ExactMatch));
	}

	@Test
	public void extractSurNameStartsWith() {
		personSearchCommand.setSimpleSearch("lastName*");
		PersonSearchRequest expectedPersonSearchRequest = unit
				.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSurName(), is("lastName"));
		assertThat(expectedPersonSearchRequest.getPersonSurNameMetaData(), is(SearchFieldMetadata.StartsWith));
	}

	@Test
	public void extractGivenNameExactMatch() {
		personSearchCommand.setSimpleSearch("firstName lastName");
		PersonSearchRequest expectedPersonSearchRequest = unit
		        .validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonGivenName(), is("firstName"));
		assertThat(expectedPersonSearchRequest.getPersonGivenNameMetaData(), is(SearchFieldMetadata.ExactMatch));

		personSearchCommand.setSimpleSearch("\"my first name\" lastName");
		expectedPersonSearchRequest = unit.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonGivenName(), is("my first name"));
		assertThat(expectedPersonSearchRequest.getPersonGivenNameMetaData(), is(SearchFieldMetadata.ExactMatch));
	}

	@Test
	public void extractGivenNameStartsWith() {
		personSearchCommand.setSimpleSearch("firstName* lastName");
		PersonSearchRequest expectedPersonSearchRequest = unit
				.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonGivenName(), is("firstName"));
		assertThat(expectedPersonSearchRequest.getPersonGivenNameMetaData(), is(SearchFieldMetadata.StartsWith));
	}
	
	@Test
	public void extractDriverLicense() {
	    personSearchCommand.setSimpleSearch("VA-VA9220223");
	    PersonSearchRequest expectedPersonSearchRequest = unit
        .validateAndParseSimpleSearch(personSearchCommand, errors);
	    assertThat(expectedPersonSearchRequest.getPersonDriversLicenseNumber(),is("VA9220223"));
        assertThat(expectedPersonSearchRequest.getPersonDriversLicenseIssuer(),is("VA"));
	}

	//TODO: Figure out why this fails on CI server
	@Test
	@Ignore("This test is failing in CI, but not on dev machines. Need to troubleshoot.")
	public void extractSurNameAndDriverLicense() {
		personSearchCommand.setSimpleSearch("lastName WA1234567");
		PersonSearchRequest expectedPersonSearchRequest = unit
				.validateAndParseSimpleSearch(personSearchCommand, errors);
		
		log.info("Person Search Request: " + expectedPersonSearchRequest);
		
		assertThat(expectedPersonSearchRequest.getPersonSurName(), is("lastName"));
		assertThat(expectedPersonSearchRequest.getPersonDriversLicenseNumber(),is("WA1234567"));
		assertThat(expectedPersonSearchRequest.getPersonDriversLicenseIssuer(),is("WA"));
	}
	
	@Test
	public void extractSurNameAndFBIID() {
		personSearchCommand.setSimpleSearch("lastName 044445Z9");
		PersonSearchRequest expectedPersonSearchRequest = unit
				.validateAndParseSimpleSearch(personSearchCommand, errors);
		assertThat(expectedPersonSearchRequest.getPersonSurName(), is("lastName"));
		assertThat(expectedPersonSearchRequest.getPersonFBINumber(),is("044445Z9"));
	}
	
	@Test
	public void validateAndParsePreservesSearchPurposeAndOnBehalfOf() {
		PersonSearchRequest advanceSearch = new PersonSearchRequest();
		advanceSearch.setOnBehalfOf("onBehalfOf");
		advanceSearch.setPurpose("purpose");
		personSearchCommand.setSimpleSearch("lastName WA-JONESHT234B");
		personSearchCommand.setAdvanceSearch(advanceSearch);
		
		PersonSearchRequest expectedPersonSearchRequest = unit
				.validateAndParseSimpleSearch(personSearchCommand, errors);
		
		assertThat(expectedPersonSearchRequest.getOnBehalfOf(), is("onBehalfOf"));
		assertThat(expectedPersonSearchRequest.getPurpose(), is("purpose"));
	}
}
