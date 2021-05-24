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
 * FileName    		:  VerificationService.java                                                   * 	  
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
package com.pennanttech.pennapps.pff.verification.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

public interface VerificationService {

	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, VerificationType verificationType, String auditTranType,
			boolean isInitTab);

	List<Verification> getVerifications(String keyReference, int verificationType);

	Verification getApprovedVerification(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	void saveLegalVerification(Verification verification);

	void setLVDetails(List<Verification> verifications);

	Verification getVerificationById(long id, VerificationType type);

	void savereInitLegalVerification(FinanceDetail financeDetail, Verification verification);

	List<Verification> getCollateralDetails(String[] collaterals);

	void setLastStatus(Verification verification);

	boolean isVerificationInRecording(Verification verification, VerificationType tv);

	List<Integer> getVerificationTypes(String keyReference);

	List<Verification> getCollateralDocumentsStatus(String collateralReference);

	List<Verification> getVerificationsForAggrement(String finReference);

	void deleteVerification(Verification verification, TableType tableType);

	void updateReferenceIds(FinanceDetail financeDetail);

	List<Verification> getVerificationCount(String finReference, String collateralReference, int verificationType,
			Integer tvStatus);

	Verification getVerificationStatus(String reference, int verificationType, String addressType, String custCif);

	List<String> getApprovedLVVerifications(int decision, int verificationType);

	void saveLegalVetting(Verification verification);

	void savereInitLegalVetting(FinanceDetail financeDetail, Verification verification);

	void setVettingDetails(List<Verification> verifications);

}