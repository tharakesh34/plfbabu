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
 * FileName    		:  TreasuaryFinanceService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface TreasuaryFinanceService {

	InvestmentFinHeader getTreasuaryFinance();
	InvestmentFinHeader getNewTreasuaryFinance();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	InvestmentFinHeader getTreasuaryFinanceById(String id);
	InvestmentFinHeader getApprovedTreasuaryFinanceById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException;
	AuditHeader doConfirm(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<FinanceDetail> getFinanceDetails(InvestmentFinHeader investmentFinHeader);
	FinanceDetail getFinanceDetailById(FinanceDetail financeDetail, String finReference); 
	InvestmentFinHeader getTreasuaryFinHeader(String finReference, String tableType); 
	void setDocumentDetails(FinanceDetail financeDetail) ;
	void setFeeCharges(FinanceDetail financeDetail, String type) ;
	void setFinanceDetails(FinanceDetail financeDetail, String strTab, String userRole);
	ErrorDetails  investmentDealValidations(FinanceDetail aFinanceDetail,InvestmentFinHeader investmentFinHeader , String usrLanguage);
	ErrorDetails  treasuryFinHeaderDialogValidations(InvestmentFinHeader investmentFinHeader , String usrLanguage);
	AuditHeader saveOrUpdateDeal(AuditHeader auditHeader);
	String getCustStatusByMinDueDays();
}