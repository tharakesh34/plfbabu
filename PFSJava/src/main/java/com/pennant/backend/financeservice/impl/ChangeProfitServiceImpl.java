package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;

public class ChangeProfitServiceImpl extends GenericService<FinServiceInstruction> implements ChangeProfitService {

	private static Logger logger = Logger.getLogger(ChangeProfitServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * 
	 * 
	 */
	public FinScheduleData getChangeProfitDetails(FinScheduleData finScheduleData, BigDecimal amount) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		
		finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
		finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());
		
		finSchdData = ScheduleCalculator.changeProfit(finScheduleData, amount);

		finSchdData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchdData;
	}

	/**
	 * Validate Change profit service instruction fields.
	 * 
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";
		
		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "", isWIF);

		// validate from date
		Date fromDate = finServiceInstruction.getFromDate();
		
		// It should be valid schedule date
		boolean isFromDateExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, fromDate, isWIF);
		if(!isFromDateExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// It shouldn't be greater than or equals to maturity date
		if (isFromDateExists && fromDate.compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// It shouldn't be past date when compare to appdate
		if(isFromDateExists && fromDate.compareTo(DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// validate To date
		Date toDate = finServiceInstruction.getToDate();
		
		// ToDate should be valid schedule date
		boolean isToDateExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, toDate, isWIF);
		if(!isToDateExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// ToDate shouldn't be greater than maturity date
		if (isToDateExists && fromDate.compareTo(financeMain.getMaturityDate()) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// ToDate shouldn't be past date when compare to appdate
		if(isToDateExists && fromDate.compareTo(DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		logger.debug("Leaving");
		return auditDetail;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
