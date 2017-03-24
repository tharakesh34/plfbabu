package com.pennant.datamigration.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.datamigration.service.DMTransactionService;

public class DMTransactionServiceImpl implements DMTransactionService {
	private final Logger logger = LoggerFactory.getLogger(DMTransactionServiceImpl.class);
	
	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;  
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private OverdueChargeRecoveryDAO recoveryDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinStatusDetailDAO finStatusDetailDAO;
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private FinanceTypeDAO financeTypeDAO;
	private RepayInstructionDAO repayInstructionDAO;

	public FinScheduleData getFinanceDetails(String finReference, String type) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finReference);
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails( finReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		finSchData.setRepayDetails(getFinanceRepaymentsDAO().getFinRepayListByFinRef(finReference, false, type));
		finSchData.setPenaltyDetails(getRecoveryDAO().getFinancePenaltysByFinRef(finReference, type));
		
		logger.debug("Leaving");
		return finSchData;
	}

	public List<String> getFinanceReferenceList() {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFinanceMainDAO().getFinanceReferenceList();
	}

	public int updateFinanceDetails(FinScheduleData finScheduleData, String type) {
		logger.debug("Entering");
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		getFinanceMainDAO().save(financeMain, type, false);		
		listSave(finScheduleData, type, false);

		logger.debug("Leaving");
		return 0;
	}
	
	/**
	 * Method to save what if inquiry lists
	 */
	public void listSave(FinScheduleData finDetail, String tableType, boolean isWIF) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
		}

		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, isWIF);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay =  SysParamUtil.getValueAsDate(SysParamUtil.Param.APP_DFT_CURR.name());
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, isWIF);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, isWIF);

		//Finance Overdue Penalty Rates
		FinODPenaltyRate penaltyRate = finDetail.getFinODPenaltyRate();
		if (penaltyRate == null) { 
			penaltyRate = new FinODPenaltyRate();
			penaltyRate.setApplyODPenalty(false);
			penaltyRate.setODIncGrcDays(false);
			penaltyRate.setODChargeType("");
			penaltyRate.setODChargeAmtOrPerc(BigDecimal.ZERO);
			penaltyRate.setODChargeCalOn("");
			penaltyRate.setODGraceDays(0);
			penaltyRate.setODAllowWaiver(false);
			penaltyRate.setODMaxWaiverPerc(BigDecimal.ZERO);
		}
		penaltyRate.setFinReference(finDetail.getFinReference());
		penaltyRate.setFinEffectDate(DateUtility.getSysDate());
		getFinODPenaltyRateDAO().save(penaltyRate, tableType);

		logger.debug("Leaving ");
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(
			FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinODPenaltyRateDAO getFinODPenaltyRateDAO() {
		return finODPenaltyRateDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
		return financeSuspHeadDAO;
	}

	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
		this.financeSuspHeadDAO = financeSuspHeadDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}
}
