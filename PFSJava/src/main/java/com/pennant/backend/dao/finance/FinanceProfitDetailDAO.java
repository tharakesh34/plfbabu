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
 * * FileName : FinanceProfitDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-02-2012 * *
 * Modified Date : 09-02-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-02-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceProfitDetail;

/**
 * DAO methods declaration for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public interface FinanceProfitDetailDAO {

	FinanceProfitDetail getFinProfitDetailsById(long finID);

	void update(FinanceProfitDetail pd, boolean isRpyProcess);

	void save(FinanceProfitDetail pd);

	BigDecimal getAccrueAmount(long finID);

	FinanceProfitDetail getFinProfitDetailsByRef(long finID);

	void updateCpzDetail(List<FinanceProfitDetail> pdList);

	FinanceProfitDetail getProfitDetailForWriteOff(long finID);

	FinanceProfitDetail getPftDetailForEarlyStlReport(long finID);

	void updateLatestRpyDetails(FinanceProfitDetail pd);

	FinanceProfitDetail getFinProfitDetailsForSummary(long finID);

	List<FinanceProfitDetail> getFinProfitDetailsByCustId(Customer customer);

	FinanceProfitDetail getFinProfitDetailsByFinRef(long finID, boolean isActive);

	void updateEOD(FinanceProfitDetail pd, boolean posted, boolean monthend);

	void UpdateActiveSts(long finID, boolean isActive);

	int getCurOddays(long finID);

	boolean isSuspenseFinance(long finID);

	BigDecimal getTotalCustomerExposre(long custId);

	BigDecimal getTotalCoApplicantsExposre(String finReference);

	Date getFirstRePayDateByFinRef(long finID);

	BigDecimal getMaxRpyAmount(long finID);

	List<FinanceProfitDetail> getFinProfitListByFinRefList(List<Long> finRefList);

	void updateAssignmentBPIAmounts(FinanceProfitDetail pd);

	void updateFinPftMaturity(long finID, String closingStatus, boolean finIsActive);

	// IND AS One Time Activity
	List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart);

	FinanceProfitDetail getFinProfitForAMZ(long finID);

	void updateAMZMethod(long finID, String amzMethod);

	void updateSchPaid(FinanceProfitDetail pd);

	void updateClosingSts(long finID, boolean writeoffLoan);

	FinanceProfitDetail getFinProfitDetailsByFinRef(long finID);

	BigDecimal getOverDueAmount(long finID);

	Date getMaturityDate(long finID, Date appDate);
}
