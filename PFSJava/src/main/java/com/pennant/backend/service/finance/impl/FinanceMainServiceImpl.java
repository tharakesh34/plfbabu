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
 * * FileName : FinanceMainServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * *
 * Modified Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.
 */
public class FinanceMainServiceImpl extends GenericService<FinanceMain> implements FinanceMainService {
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public FinanceMainServiceImpl() {
		super();
	}

	@Override
	public FinanceMain getFinanceMainById(long finID, boolean isWIF) {
		return financeMainDAO.getFinanceMainById(finID, "_View", isWIF);
	}

	@Override
	public FinanceMain getFinanceMainByRef(String finReference, boolean isWIF) {
		return financeMainDAO.getFinanceMainByRef(finReference, "_View", isWIF);
	}

	@Override
	public FinanceMain getFinanceMainByFinRef(long finID) {
		return financeMainDAO.getFinanceMainById(finID, "", false);
	}

	@Override
	public FinanceMain getFinanceMainByFinRef(String finReference) {
		return financeMainDAO.getFinanceMainByRef(finReference, "", false);
	}

	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		return financeMainDAO.getFinanceDetailsByCustId(custId);
	}

	@Override
	public int loanMandateSwapping(long finID, long newMandateID, String repayMethod, String type,
			boolean securityMandate) {
		return financeMainDAO.loanMandateSwapping(finID, newMandateID, repayMethod, type, securityMandate);
	}

	@Override
	public int updateFinanceBasicDetails(FinanceMain financeMain) {
		return financeMainDAO.updateFinanceBasicDetails(financeMain, "");
	}

	@Override
	public List<FinanceMain> getFinanceByCustId(long custId, String type) {
		return financeMainDAO.getFinanceByCustId(custId, type);
	}

	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		return financeMainDAO.getFinanceByCollateralRef(collateralRef);
	}

	@Override
	public List<Long> getFinReferencesByMandateId(long mandateId) {
		return financeMainDAO.getFinReferencesByMandateId(mandateId);
	}

	@Override
	public List<Long> getFinIDList(String custCIF, String closingStatus) {
		return financeMainDAO.getFinIDList(custCIF, closingStatus);
	}

	@Override
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		return financeMainDAO.getCustomerODLoanDetails(userID);
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, TableType tableType) {
		return financeMainDAO.getFinanceMain(finReference, tableType);
	}

	@Override
	public Date getFinClosedDate(long finID) {
		return financeMainDAO.getClosedDate(finID);
	}

	@Override
	public ErrorDetail rescheduleValidation(Date receiptDate, long finID, Date startDate) {
		return null;
	}

	@Override
	public long getLoanWorkFlowIdByFinRef(String finReference, String type) {
		return financeMainDAO.getLoanWorkFlowIdByFinRef(finReference, type);
	}

	@Override
	public FinanceMain getFinanceMain(long finID, String[] columns, String type) {
		return financeMainDAO.getFinanceMain(finID, columns, type);
	}

	@Override
	public Date getClosedDateByFinRef(long finID) {
		return financeMainDAO.getClosedDateByFinRef(finID);
	}

	@Override
	public Long getFinID(String finRefernce) {
		return financeMainDAO.getFinID(finRefernce);
	}

	@Override
	public Long getFinID(String finReference, TableType tableType) {
		return financeMainDAO.getFinID(finReference, tableType);
	}

	@Override
	public String getFinanceType(String finRefernce, TableType tableType) {
		return financeMainDAO.getFinanceType(finRefernce, tableType);
	}

	@Override
	public FinanceMain getFinanceMainForAdviseUpload(String finRefernce) {
		return financeMainDAO.getFinanceMainForAdviseUpload(finRefernce);
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Override
	public String getFinanceMainByRcdMaintenance(long finID) {
		return financeMainDAO.getFinanceMainByRcdMaintenance(finID);
	}
}
