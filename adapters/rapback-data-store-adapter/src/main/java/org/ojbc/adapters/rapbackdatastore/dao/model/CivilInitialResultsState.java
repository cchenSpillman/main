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
package org.ojbc.adapters.rapbackdatastore.dao.model;

public enum CivilInitialResultsState {
	Archived("Archived"), 
	Available("Available for subscription"), 
	Subscibed("Subscribed");
	
	private String description; 
	private CivilInitialResultsState(String description){
		this.description = description; 
	}
	
	public String toString(){
		return this.description; 
	}
	
	public static CivilInitialResultsState valueOfDesc(String desc){
		for (CivilInitialResultsState civilInitialResultsState: CivilInitialResultsState.values()){
			if (civilInitialResultsState.toString().equalsIgnoreCase(desc)){
				return civilInitialResultsState; 
			}
		}
		
		return null;
	}
}
