package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;

public class RecalculateServiceImpl extends GenericService<FinServiceInstruction> implements RecalculateService {
	private static Logger logger = Logger.getLogger(RecalculateServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

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

		// validate RecalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
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
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
				return auditDetail;
			}
		}

		// validate reCalToDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91108", "", valueParm), lang));
				return auditDetail;
			} else if(finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = "RecalFromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91121", "", valueParm), lang));
			}
		}

		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if(schedules != null) {
			for(FinanceScheduleDetail schDetail: schedules) {
/*				if(DateUtility.compare(fromDate, schDetail.getSchDate()) == 0) {
					isValidFromDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
						return auditDetail;
					}
				}*/
				if(DateUtility.compare(finServiceInstruction.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
				if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if(DateUtility.compare(finServiceInstruction.getRecalToDate(), schDetail.getSchDate()) == 0) {
						isValidRecalToDate = true;
						if(checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
							return auditDetail;
						}
					}
				}
			}
			
/*			if(!isValidFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "FromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
			}*/
			if(!isValidRecalFromDate && (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
			}
			if(!isValidRecalToDate && (StringUtils.equals(finServiceInstruction.getRecalType(), 
					CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	
	/**
	 * Method for validate current schedule date is valid schedule or not
	 * 
	 * @param auditDetail
	 * @param curSchd
	 * @param label
	 * @return
	 */
	private AuditDetail checkIsValidRepayDate(AuditDetail auditDetail, FinanceScheduleDetail curSchd, String label) {
		if (!((curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) 
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate() 
				&& !curSchd.isSchPftPaid()) || (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
				&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90261", "", valueParm)));
			return auditDetail;
		}
		
		if(curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90262", "", valueParm)));
			return auditDetail;
		}
		return null;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
