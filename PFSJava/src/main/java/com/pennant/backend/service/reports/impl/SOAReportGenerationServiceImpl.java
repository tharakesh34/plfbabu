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
 *
 * FileName    		: SOAReportGenerationServiceImpl.java							        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
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
package com.pennant.backend.service.reports.impl;

import java.util.List;

import com.pennant.backend.dao.reports.SOAReportGenerationDAO;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentInstruction;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.reports.SOAReportGenerationService;

public class SOAReportGenerationServiceImpl extends GenericService<StatementOfAccount> implements SOAReportGenerationService{
	//private static Logger logger = Logger.getLogger(SOAReportGenerationServiceImpl .class);
	
	private SOAReportGenerationDAO soaReportGenerationDAO;

	public SOAReportGenerationServiceImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceMain(String finReference) {
		return this.soaReportGenerationDAO.getFinanceMain(finReference);
	}
	
	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinScheduleDetails(finReference);
	}
	
	@Override
	public List<FinAdvancePayments> getFinAdvancePayments(String finReference) {
		return this.soaReportGenerationDAO.getFinAdvancePayments(finReference);
	}
	
	@Override
	public List<PaymentInstruction> getPaymentInstructions(String finReference) {
		return this.soaReportGenerationDAO.getPaymentInstructions(finReference);
	}
	
	@Override
	public List<FinODDetails> getFinODDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinODDetails(finReference);
	}
	
	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		return this.soaReportGenerationDAO.getManualAdvise(finReference);
	}
	
	@Override
	public List<SOATransactionReport> getFinFeeScheduleDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinFeeScheduleDetails(finReference);
	}

	@Override
	public List<SOATransactionReport> getManualAdviseMovements(String finReference) {
		return this.soaReportGenerationDAO.getManualAdviseMovements(finReference);
	}
	
	@Override
	public List<SOATransactionReport> getPresentmentDetails(String finReference) {
		return this.soaReportGenerationDAO.getPresentmentDetails(finReference);
	}
	
	@Override
	public List<Long> getPresentmentReceiptIds() {
		return this.soaReportGenerationDAO.getPresentmentReceiptIds();
	}
	
	@Override
	public List<SOATransactionReport> getReceiptAllocationDetails(String finReference) {
		return this.soaReportGenerationDAO.getReceiptAllocationDetails(finReference);
	}
	
	@Override
	public List<FinReceiptHeader> getFinReceiptHeaders(String finReference) {
		return this.soaReportGenerationDAO.getFinReceiptHeaders(finReference);
	}
	
	@Override
	public List<FinReceiptDetail> getFinReceiptDetails(List<Long> finReceiptIds) {
		return this.soaReportGenerationDAO.getFinReceiptDetails(finReceiptIds);
	}
	
	@Override
	public StatementOfAccount getSOALoanDetails(String finReference) {
		return this.soaReportGenerationDAO.getSOALoanDetails(finReference);
	}
	
	@Override
	public FinanceProfitDetail getFinanceProfitDetails(String finReference) {
		return this.soaReportGenerationDAO.getFinanceProfitDetails(finReference);
	}

	@Override
	public int getFinanceProfitDetailActiveCount(long finProfitDetailActiveCount, boolean active) {
		return this.soaReportGenerationDAO.getFinanceProfitDetailActiveCount(finProfitDetailActiveCount, active);
	}
	
	@Override
	public StatementOfAccount getSOACustomerDetails(long custId) {
		return this.soaReportGenerationDAO.getSOACustomerDetails(custId);
	}
	
	@Override
	public StatementOfAccount getSOAProductDetails(String finBranch, String finType) {
		return this.soaReportGenerationDAO.getSOAProductDetails(finBranch, finType);
	}
	
	@Override
	public SOASummaryReport getFinExcessAmountOfSummaryReport(String finReference) {
		return this.soaReportGenerationDAO.getFinExcessAmountOfSummaryReport(finReference);
	}
	
	@Override
	public List<SOATransactionReport> getFinRepayscheduledetails(String finReference) {
		return this.soaReportGenerationDAO.getFinRepayscheduledetails(finReference);
	}
	
	@Override
	public List<SOATransactionReport> getOrgFinFeedetails(String finReference) {
		return this.soaReportGenerationDAO.getOrgFinFeedetails(finReference);
	}
	
	@Override
	public List<SOATransactionReport> getFinFeedetails(String finReference) {
		return this.soaReportGenerationDAO.getFinFeedetails(finReference);
	}
	
	public void setSoaReportGenerationDAO(SOAReportGenerationDAO soaReportGenerationDAO) {
		this.soaReportGenerationDAO = soaReportGenerationDAO;
	}


}