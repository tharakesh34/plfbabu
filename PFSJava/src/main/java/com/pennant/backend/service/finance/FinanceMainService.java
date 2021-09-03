/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceMainService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FinanceMainService {
	FinanceMain getFinanceMainById(long finID, boolean isWIF);

	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);

	int getFinanceCountById(long finID, long mandateID);

	int loanMandateSwapping(long finID, long newMandateId, String repayMethod, String type);

	int getFinanceCountById(long finID, boolean isWIF);

	int updateFinanceBasicDetails(FinanceMain financeMain);

	List<FinanceMain> getFinanceByCustId(long custId, String type);

	List<FinanceMain> getFinanceByCollateralRef(String collateralRef);

	List<Long> getFinReferencesByMandateId(long mandateId);

	List<Long> getFinReferencesByCustID(long custId, String finActiveStatus);

	List<Long> getFinanceMainbyCustId(long custID, String type);

	FinanceMain getFinanceMainByFinRef(long finID);

	List<LoanPendingData> getCustomerODLoanDetails(long userID);

	FinanceMain getFinanceByFinReference(long finID, String type);

	String getFinanceTypeFinReference(long finID, String type);

	ErrorDetail rescheduleValidation(Date receiptDate, long finID, Date startDate);

	long getLoanWorkFlowIdByFinRef(long finID, String type);

	FinanceMain getFinanceMain(long finID, String[] columns, String type);

	Date getClosedDateByFinRef(long finID);

	Date getFinClosedDate(long finID);

}