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
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantStaticListUtil;

public class RateChangeServiceImpl extends GenericService<FinServiceInstruction> implements RateChangeService {
	private static final Logger logger = Logger.getLogger(RateChangeServiceImpl.class);
	
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * Method for perform the Rate change action
	 * 
	 * @param finScheduleData
	 * @param finServiceInst
	 */
	public FinScheduleData getRateChangeDetails(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// Profit Days Basis Setting for Calculation Process
		List<FinanceScheduleDetail> schDetailList = finScheduleData.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchd : schDetailList) {
			if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) >= 0
					&& curSchd.getSchDate().compareTo(financeMain.getEventToDate()) < 0) {
				curSchd.setPftDaysBasis(finServiceInst.getPftDaysBasis());
			}
		}
		FinScheduleData finSchData = null;

		finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
		finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());

		finSchData = ScheduleCalculator.changeRate(finScheduleData, finServiceInst.getBaseRate(), finServiceInst.getSplRate(),
				finServiceInst.getMargin() == null ? BigDecimal.ZERO : finServiceInst.getMargin(),
						finServiceInst.getActualRate() == null ? BigDecimal.ZERO : finServiceInst.getActualRate(), true);

		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Validate service instruction data against addratechange service.
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
		
		if(DateUtility.compare(finServiceInstruction.getFromDate(), DateUtility.getAppDate()) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:"+DateUtility.formatToLongDate(DateUtility.getAppDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("30509", "", valueParm), lang));
			return auditDetail;
		}
		// validate from date with finStart date and maturity date
		if(finServiceInstruction.getFromDate().compareTo(financeMain.getFinStartDate()) < 0
				|| finServiceInstruction.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "finance start date:"+DateUtility.formatToShortDate(financeMain.getFinStartDate());
			valueParm[2] = "maturity date:"+DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm), lang));
			return auditDetail;
		}
		
		// validate to date
		if(finServiceInstruction.getToDate().compareTo(financeMain.getMaturityDate()) >= 0 
				|| finServiceInstruction.getToDate().compareTo(finServiceInstruction.getFromDate()) < 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
			valueParm[2] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91102", "", valueParm), lang));
			return auditDetail;
		}
		
		// validate profit days basis
		if (StringUtils.isNotBlank(finServiceInstruction.getPftDaysBasis())) {
			List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
			boolean profitDaysSts = false;
			for (ValueLabel value : profitDayBasis) {
				if (StringUtils.equals(value.getValue(), finServiceInstruction.getPftDaysBasis())) {
					profitDaysSts = true;
					break;
				}
			}
			if (!profitDaysSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getPftDaysBasis();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91103", "", valueParm), lang));
			}
		}
		
		// validate recalType
		if(StringUtils.isNotBlank(finServiceInstruction.getRecalType())) {
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
		if(StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT) ||
				StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finServiceInstruction.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finServiceInstruction.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91105", "", valueParm), lang));
				return auditDetail;
			} else if(finServiceInstruction.getRecalFromDate().compareTo(finServiceInstruction.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = "FromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91125", "", valueParm), lang));
			} else if(finServiceInstruction.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91114", "", valueParm), lang));
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
				valueParm[0] = DateUtility.formatToShortDate(finServiceInstruction.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finServiceInstruction.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91109", "", valueParm), lang));
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
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
			}
			if(!isValidToDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "ToDate:"+DateUtility.formatToShortDate(finServiceInstruction.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm), lang));
			}
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
		return null;
	}
	
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
}
