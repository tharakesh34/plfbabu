/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  VerificationsRestService.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-02-2021    														*
 *                                                                  						*
 * Modified Date    :  08-02-2021    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-02-2021       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennanttech.pffws;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.ws.model.VerificationCustomerAddress.VerificationDetails;

@Produces(MediaType.APPLICATION_JSON)
public interface VerificationsRestService {

	//Common API for All the verification Types
	@POST
	@Path("/verificationsService/getVerificationDetails")
	public VerificationDetails getVerificationDetails(Verification verification) throws ServiceException;

	@POST
	@Path("/verificationsService/initiateFIVerification")
	Verification initiateFIVerification(Verification verification);

	//Common API for All the verification Types
	@POST
	@Path("/verificationsService/getVerificationIds")
	public List<Verification> getVerificationIds(Verification verification) throws ServiceException;

	@POST
	@Path("/verificationsService/recordFIVerification")
	FieldInvestigation recordFiVerification(FieldInvestigation fieldInvestigation);

	@POST
	@Path("/verificationsService/initiatePDVerification")
	Verification initiatePDVerification(Verification verification);

	@POST
	@Path("/verificationsService/recordPDVerification")
	public PersonalDiscussion recordPDVerification(PersonalDiscussion personalDiscussion) throws ServiceException;

	@POST
	@Path("/verificationsService/recordTVVerification")
	public Verification recordTVVerification(TechnicalVerification technicalVerification) throws ServiceException;

	@POST
	@Path("/verificationsService/initiateTVVerification")
	Verification initiateTVVerification(Verification verification);

	@POST
	@Path("/verificationsService/initiateRCUVerification")
	Verification initiateRCUVerification(Verification verification);

	@POST
	@Path("/verificationsService/recordRCUVerification")
	public Verification recordRCUVerification(RiskContainmentUnit riskContainmentUnit) throws ServiceException;

	@POST
	@Path("/verificationsService/initiateLVVerification")
	Verification initiateLVVerification(Verification verification);
	
	@POST
	@Path("/verificationsService/recordLVVerification")
	public Verification recordLVVerification(LegalVerification legalVerification) throws ServiceException;

}
