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
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class PostponementServiceImpl extends GenericService<FinServiceInstruction> implements PostponementService {

	private static Logger logger = Logger.getLogger(PostponementServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	
	/**
	 * Method for Processing Postponement for the selected period by adding new terms
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doPostponement(FinScheduleData finScheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod) {
		logger.debug("Entering");
		
		finScheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finScheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finScheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finScheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());
		
		finScheduleData = ScheduleCalculator.postpone(finScheduleData);
		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Processing Unplanned EMI holidays for the selected period
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doUnPlannedEMIH(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		
		String scheduleMethod = finScheduleData.getFinanceMain().getScheduleMethod();
		
		if(finScheduleData.getFinServiceInstructions() != null) {
			for(FinServiceInstruction serviceInst: finScheduleData.getFinServiceInstructions()) {
				finScheduleData.getFinanceMain().setRecalType(serviceInst.getRecalType());
				finScheduleData.getFinanceMain().setRecalFromDate(serviceInst.getRecalFromDate());
				finScheduleData.getFinanceMain().setRecalToDate(serviceInst.getRecalToDate());
				finScheduleData.getFinanceMain().setAdjTerms(serviceInst.getTerms());
				finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
				finScheduleData.getFinanceMain().setPftIntact(serviceInst.isPftIntact());
			}
		}
		
		// call schedule calculator
		finScheduleData = ScheduleCalculator.unPlannedEMIH(finScheduleData, BigDecimal.ZERO, scheduleMethod);
		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Processing Re-Aging for the selected period by adding new terms
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doReAging(FinScheduleData finScheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod) {
		logger.debug("Entering");
		
		finScheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finScheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finScheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finScheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());
		
		finScheduleData = ScheduleCalculator.reAging(finScheduleData);
		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
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
		String lang = "EN";
		
		// validate Instruction details
		boolean isWIF = finServiceInstruction.isWif();
		String finReference = finServiceInstruction.getFinReference();
		
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "", isWIF);
		
		// validate from date with finStart date and maturity date
		if(finServiceInstruction.getFromDate().compareTo(financeMain.getFinStartDate()) < 0
				|| finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "finance start date:"+DateUtility.formatToShortDate(financeMain.getFinStartDate());
			valueParm[2] = "maturity date:"+DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}
		
		// validate to date
		if(finServiceInstruction.getToDate().compareTo(financeMain.getMaturityDate()) >= 0 
				|| finServiceInstruction.getToDate().compareTo(finServiceInstruction.getFromDate()) < 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
			valueParm[2] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91102", "", valueParm), lang));
			return auditDetail;
		}
		
		// validate RecalType
		if (StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
			if(!StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT) 
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					&& !StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm), lang));
			}
		}
		
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)) {
			if(finServiceInstruction.getTerms() <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Number of Terms";
				valueParm[1] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91112", "", valueParm), lang));
			}
		}

		// validate reCalFromDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm), lang));
				return auditDetail;
			} else if(finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getToDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91106", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm), lang));
			}
		}
		
		// validate reCalToDate
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm), lang));
				return auditDetail;
			} else if(finServiceInstruction.getRecalToDate().compareTo(finServiceInstruction.getRecalFromDate()) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91109", "", valueParm), lang));
			}
		}
		
		boolean isValidFromDate = false;
		boolean isValidToDate = false;
		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
		if(schedules != null) {
			for(FinanceScheduleDetail schDetail: schedules) {
				// FromDate
				if(DateUtility.compare(finServiceInstruction.getFromDate(), schDetail.getSchDate()) == 0) {
					isValidFromDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
						return auditDetail;
					}
				}
				// ToDate
				if(DateUtility.compare(finServiceInstruction.getToDate(), schDetail.getSchDate()) == 0) {
					isValidToDate = true;
					if(checkIsValidRepayDate(auditDetail, schDetail, "ToDate") != null) {
						return auditDetail;
					}
				}
				// RecalFromDate
				if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
						|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if(DateUtility.compare(finServiceInstruction.getRecalFromDate(), schDetail.getSchDate()) == 0) {
						isValidRecalFromDate = true;
						if(checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
							return auditDetail;
						}
					}
				}
				// RecalToDate
				if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if(DateUtility.compare(finServiceInstruction.getRecalToDate(), schDetail.getSchDate()) == 0) {
						isValidRecalToDate = true;
						if(checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
							return auditDetail;
						}
					}
				}
			}

			if(!isValidFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "FromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			}
			if(!isValidToDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "ToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			}
			if(!isValidRecalFromDate && (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
			}
			if(!isValidRecalToDate && (StringUtils.equals(finServiceInstruction.getRecalType(), 
					CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
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
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}
		return null;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
