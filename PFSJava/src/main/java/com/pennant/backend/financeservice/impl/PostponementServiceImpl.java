package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;

public class PostponementServiceImpl extends GenericService<FinServiceInstruction> implements PostponementService {

	private static Logger logger = Logger.getLogger(PostponementServiceImpl.class);

	private FinanceMainDAO financeMainDAO;
	
	/**
	 * Method for Processing Postponement for the selected period by adding new terms
	 * @param finscheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doPostponement(FinScheduleData finscheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod) {
		logger.debug("Entering");
		
		finscheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finscheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finscheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finscheduleData.getFinanceMain().setAdjTerms(serviceInstruction.getTerms());
		finscheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finscheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());
		
		finscheduleData = ScheduleCalculator.postpone(finscheduleData, BigDecimal.ZERO, scheduleMethod);
		logger.debug("Leaving");
		return finscheduleData;
	}
	
	/**
	 * Method for Processing Unplanned EMI holidays for the selected period
	 * @param finscheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doUnPlannedEMIH(FinScheduleData finscheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod) {
		logger.debug("Entering");
		
		finscheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finscheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finscheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finscheduleData.getFinanceMain().setAdjTerms(serviceInstruction.getTerms());
		finscheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finscheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());
		
		finscheduleData = ScheduleCalculator.unPlannedEMIH(finscheduleData, BigDecimal.ZERO, scheduleMethod);
		logger.debug("Leaving");
		return finscheduleData;
	}
	
	/**
	 * Method for Processing Re-Aging for the selected period by adding new terms
	 * @param finscheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doReAging(FinScheduleData finscheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod) {
		logger.debug("Entering");
		
		finscheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finscheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finscheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finscheduleData.getFinanceMain().setAdjTerms(serviceInstruction.getTerms());
		finscheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finscheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());
		
		finscheduleData = ScheduleCalculator.reAging(finscheduleData, BigDecimal.ZERO, scheduleMethod);
		logger.debug("Leaving");
		return finscheduleData;
	}

	/**
	 * Method for validate defferment instructions.
	 * 
	 * @param finServiceInstruction
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = PennantConstants.default_Language;

		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "", isWIF);

		// validate from date
		if (finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(finServiceInstruction.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91101", "", valueParm), lang));
		}

		// validate recalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			List<ValueLabel> recalTypes = PennantStaticListUtil.getSchCalCodes();
			boolean recalTypeSts = false;
			for (ValueLabel value : recalTypes) {
				if (StringUtils.equals(value.getValue(), finServiceInstruction.getRecalType())) {
					recalTypeSts = true;
					break;
				}
			}
			if (!recalTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91104", "", valueParm), lang));
			}
		}
		
		// validate reCalFromDate
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
			} else if (finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91106", "", valueParm), lang));
			}
		}
		
		// validate reCalToDate
		if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91108", "", valueParm), lang));
			} else if (finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91109", "", valueParm), lang));
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
