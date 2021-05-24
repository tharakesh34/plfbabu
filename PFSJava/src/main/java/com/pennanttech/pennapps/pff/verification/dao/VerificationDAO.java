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
 * FileName    		:  VerificationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-03-2018    														*
 *                                                                  						*
 * Modified Date    :  24-03-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-03-2018       PENNANT	                 0.1                                            * 
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
package com.pennanttech.pennapps.pff.verification.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface VerificationDAO extends BasicCrudDao<Verification> {

	/**
	 * Fetch the Record Verification by key field
	 * 
	 * @param id
	 *            id of the Verification.
	 * @param tableType
	 *            The type of the table.
	 * @return Verification
	 */
	List<Verification> getVeriFications(String keyReference, int verificationType);

	void updateVerifiaction(long verificationId, Date verificationDate, int status);

	void updateReInit(Verification verification, TableType tableType);

	Long getVerificationIdByReferenceFor(String finReference, String referenceFor, int verificationType);

	Long getVerificationIdByReferenceFor(String finReference, String referenceFor, int verificationType,
			int requestType, int verificationCategory);

	Verification getVerificationById(long id);

	Verification getLastStatus(Verification verification);

	List<Verification> getCollateralDetails(String[] collaterals);

	List<Integer> getVerificationTypes(String keyReference);

	void updateDocumentId(DocumentDetails documentDetails, Long verificationId, TableType stageTab);

	List<Long> getRCUVerificationId(String finReference, int verificationType, String referencetype);

	void updateRCUReference(DocumentDetails documentDetails, Long verificationId);

	List<Long> getVerificationIds(String finReference, int verificationType, int requestType);

	List<Verification> getVerificationCount(String finReference, String collateralReference, int verificationType,
			Integer tvStatus);

	Verification getVerificationStatus(String reference, int verificationType, String addressType, String custCif);

	List<String> getAprrovedLVVerifications(int decision, int verificationType);

	List<Verification> getVerifications(String finReference, int verificationType, int requestType);

	boolean isVerificationIdExists(String finReference, String referenceFor, String reference, int verificationType,
			String referenceType);

	boolean isInitiatedVerfication(VerificationType verificationType, long verificationId, String type);

	Long isVerificationExist(String finReference, String referenceFor, String reference, int verificationType,
			String referenceType);

}