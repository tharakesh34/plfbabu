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
	FinanceMain getFinanceMainById(String id, boolean isWIF);

	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);

	int getFinanceCountById(String finReference, long mandateID);

	int loanMandateSwapping(String finReference, long newMandateId, String repayMethod, String type);

	int getFinanceCountById(String finReference, boolean isWIF);

	int updateFinanceBasicDetails(FinanceMain financeMain);

	List<FinanceMain> getFinanceByCustId(long custId, String type);

	List<FinanceMain> getFinanceByCollateralRef(String collateralRef);

	List<Long> getFinReferencesByMandateId(long mandateId);

	List<String> getFinReferencesByCustID(long custId, String finActiveStatus);

	List<String> getFinanceMainbyCustId(long custID, String type);

	FinanceMain getFinanceMainByFinRef(String finRefernce);

	List<LoanPendingData> getCustomerODLoanDetails(long userID);

	FinanceMain getFinanceByFinReference(String reference, String type);

	String getFinanceTypeFinReference(String reference, String type);

	ErrorDetail rescheduleValidation(Date receiptDate, String finReference, Date startDate);

	long getLoanWorkFlowIdByFinRef(String loanReference, String type);

	FinanceMain getFinanceMain(String finReference, String[] columns, String type);

	Date getClosedDateByFinRef(String finReference);

	Date getFinClosedDate(String finReference);

}