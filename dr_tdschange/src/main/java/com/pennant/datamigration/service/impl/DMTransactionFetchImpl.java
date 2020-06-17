// Decompiled by Procyon v0.5.36
//

package com.pennant.datamigration.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.service.DMTransactionFetch;

public class DMTransactionFetchImpl implements DMTransactionFetch {
	private static Logger logger = Logger.getLogger(DMTransactionFetchImpl.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceTypeDAO financeTypeDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private CustomerDAO customerDAO;
	private FinanceProfitDetailDAO profitDetailDAO;

	private boolean idReq;
	private String idValue;
	private Date appDate;

	public DMTransactionFetchImpl() {
		this.idReq = false;
		this.idValue = "ID";
		this.appDate = null;
	}

	public CustomerDAO getCustomerDAO() {
		return this.customerDAO;
	}

	public void setCustomerDAO(final CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public List<FinanceType> getFinTypeList(final String type) {
		return null;
	}

	public MigrationData getEHFinanceDetails(final String finReference, final ReferenceID rid) {
		MigrationData sMD = new MigrationData();
		rid.setSkipCorrection(false);
		rid.setiDRFrom(0);

		sMD.setFinanceMain(this.financeMainDAO.getEHFinanceMain(finReference));

		List<FinanceScheduleDetail> fsdList = this.financeScheduleDetailDAO.getFinScheduleDetails(finReference, "",
				false);
		sMD.setFinScheduleDetails(fsdList);
		sMD.setOldFinScheduleDetails(fsdList);

		List<FinanceDisbursement> fddList = this.financeDisbursementDAO.getFinanceDisbursementDetails(finReference, "",
				false);
		sMD.setFinDisbursements(fddList);
		sMD.setFinType(this.financeTypeDAO.getFinanceTypeByID(sMD.getFinanceMain().getFinType(), ""));
		sMD.setFinProfitDetails(this.profitDetailDAO.getFinProfitDetailsById(finReference));
		sMD.setPresentmentDetails(this.presentmentDetailDAO.getDMPresentmentDetailsByRef(finReference, ""));
		sMD.setRepayInstructions(this.repayInstructionDAO.getRepayInstructions(finReference, "", false));
		sMD.setFinODDetails(this.finODDetailsDAO.getFinODDByFinRef(finReference, null));
		sMD.setPenaltyrate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, ""));
		return sMD;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailDAO() {
		return profitDetailDAO;
	}

	public void setProfitDetailDAO(FinanceProfitDetailDAO profitDetailDAO) {
		this.profitDetailDAO = profitDetailDAO;
	}

}