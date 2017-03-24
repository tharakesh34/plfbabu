package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;

public class RecalculateServiceImpl extends GenericService<FinServiceInstruction> implements RecalculateService {
	private static Logger logger = Logger.getLogger(RecalculateServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * 
	 * @param finScheduleData
	 */
	public FinScheduleData getRecalculateSchdDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		//TODO: PV 19JAN17 schdMethod to be added
		finSchdData = ScheduleCalculator.reCalSchd(finScheduleData, "");

		logger.debug("Leaving");

		return finSchdData;
	}

	public FinScheduleData getRecalChangeProfit(FinScheduleData finScheduleData, BigDecimal adjustedPft) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		finSchdData = ScheduleCalculator.changeProfit(finScheduleData, adjustedPft);

		logger.debug("Leaving");
		return finSchdData;
	}

	/**
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

		FinanceMain financeMain = financeMainDAO.getFinanceDetailsForService(finReference, "", isWIF);

		// validate from date
		Date fromDate = finServiceInstruction.getFromDate();
		
		// It should be valid schedule date
		boolean isExists = financeScheduleDetailDAO.getFinScheduleCountByDate(finReference, fromDate, isWIF);
		if(!isExists) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// It shouldn't be greater than or equals to maturity date
		if (isExists && fromDate.compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// It shouldn't be past date when compare to appdate
		if(isExists && fromDate.compareTo(DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:"+DateUtility.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
		}
		
		// validate RecalType
		if (isExists && StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			if(!StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL) 
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91104", "", valueParm), lang));
			}

			if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				if(finServiceInstruction.getTerms() <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Number of Terms";
					valueParm[1] = finServiceInstruction.getRecalType();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91112", "", valueParm), lang));
				}
			}
		}
		
		// validate reCalFromDate
		if(isExists && StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
			}
		}
		
		// validate reCalToDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91108", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91109", "", valueParm), lang));
			}
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
