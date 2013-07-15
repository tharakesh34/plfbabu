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
 * FileName    		:  FinanceMainService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance;

import java.util.HashMap;
import java.util.List;

import com.pennant.app.model.CustomerCalData;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface FinanceDetailService {
	FinanceDetail getFinanceDetail(boolean isWIF);
	FinanceDetail getNewFinanceDetail(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) throws AccountNotFoundException;
	FinanceDetail getFinanceDetailById(String financeReference,boolean isWIF, String eventCode);
	FinanceDetail getApprovedFinanceDetailById(String financeReference,boolean isWIF);
	FinanceDetail refresh(FinanceDetail financeDetail);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) throws AccountNotFoundException;
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
	List<ScoringMetrics> getScoringMetricsList(long id, String type);
	List<FinanceReferenceDetail>  getCheckListByFinRef(final String finType);
	List<FinanceCheckListReference> getFinanceCheckListReferenceByFinRef(final String id, String type);
	FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole,String screenCode, String eventCode);
	boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF);
	CustomerCalData getCalculatedData(CustomerCalData calData, String type);
	public void maintainWorkSchedules(String finReference, long userId,
			List<FinanceScheduleDetail> financeScheduleDetails );
	FinanceDetail getStaticFinanceDetailById(String financeReference, String type);
	FinScheduleData getFinSchDataByFinRef(String financeReference, String type);
	AgreementDetail getAgreementDetail(FinanceMain main, HashMap<String, Object> extendedFields);
	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal);
	FinScheduleData getFinSchDataById(String finReference, String type);
	AuditHeader doCheckLimits(AuditHeader auditHeader);
	void updateCustCIF(long custID, String finReference);
	void updateFinBlackListStatus(String finReference);
	FinContributorHeader getFinContributorHeaderById(String finReference);
	List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String type);
	List<DocumentDetails> getFinDocByFinRef(String finReference, String type);
	FinAgreementDetail getFinAgrDetailByAgrId(String finReference,long agrId);
	DocumentDetails getFinDocDetailByDocId(long docId);
	
	FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType, 
			String userRole, List<String> groupIds);
	List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id);
	boolean inActivateFinance(String finReference, LoginUserDetails userDetails);
}