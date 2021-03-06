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
package org.ojbc.adapters.rapbackdatastore.dao;

import java.util.List;

import org.ojbc.adapters.rapbackdatastore.dao.model.CivilFbiSubscriptionRecord;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilFingerPrints;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilInitialRapSheet;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilInitialResults;
import org.ojbc.adapters.rapbackdatastore.dao.model.CriminalFbiSubscriptionRecord;
import org.ojbc.adapters.rapbackdatastore.dao.model.CriminalFingerPrints;
import org.ojbc.adapters.rapbackdatastore.dao.model.CriminalInitialResults;
import org.ojbc.adapters.rapbackdatastore.dao.model.FbiRapbackSubscription;
import org.ojbc.adapters.rapbackdatastore.dao.model.IdentificationTransaction;
import org.ojbc.adapters.rapbackdatastore.dao.model.Subject;
import org.ojbc.adapters.rapbackdatastore.dao.model.SubsequentResults;


public interface RapbackDAO {
	
	public Integer saveSubject(final Subject subject);
	public void saveIdentificationTransaction(IdentificationTransaction identificationTransaction);
	public Integer saveCivilFbiSubscriptionRecord(final CivilFbiSubscriptionRecord civilFbiSubscriptionRecord);
	public Integer saveCriminalFbiSubscriptionRecord(final CriminalFbiSubscriptionRecord criminalFbiSubscriptionRecord);
	public Integer saveCivilFingerPrints(final CivilFingerPrints civilFingerPrints);
	public Integer saveCriminalFingerPrints(final CriminalFingerPrints criminalFingerPrints);
	public Integer saveCivilInitialRapSheet(final CivilInitialRapSheet civilInitialRapSheet);
	public Integer saveCivilInitialResults(final CivilInitialResults civilInitialResults);
	public Integer saveCriminalInitialResults(final CriminalInitialResults criminalInitialResults);
	public Integer saveSubsequentResults(final SubsequentResults subsequentResults);
	public Integer saveFbiRapbackSubscription(final FbiRapbackSubscription fbiRapbackSubscription);
	
	public Subject getSubject(Integer id);
	public IdentificationTransaction getIdentificationTransaction(String transactionNumber);
	public List<CivilInitialResults> getCivilInitialResults(String ori);
	
	public void updateSubject(Subject subject);
}
