package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class RecalculateServiceImpl extends GenericService<FinServiceInstruction> implements RecalculateService {
	private static Logger logger = LogManager.getLogger(RecalculateServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * 
	 * @param finScheduleData
	 */
	public FinScheduleData getRecalculateSchdDetails(FinScheduleData finScheduleData, String module) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();

		// Schedule Recalculation Locking Period Applicability
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {

			Date recalLockTill = finScheduleData.getFinanceMain().getRecalFromDate();
			if (recalLockTill == null) {
				recalLockTill = finScheduleData.getFinanceMain().getMaturityDate();
			}

			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {

				curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// TODO: PV 19JAN17 schdMethod to be added
		if (StringUtils.isNotBlank(module) && StringUtils.equals(module, FinServiceEvent.ADDTERM)) {
			finSchdData = ScheduleCalculator.addTerm(finScheduleData, finScheduleData.getFinanceMain().getAdjTerms());
		} else {
			finSchdData = ScheduleCalculator.reCalSchd(finScheduleData, "");
		}

		BigDecimal newTotalPft = finSchdData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchdData.setPftChg(pftDiff);
		finSchdData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");

		return finSchdData;
	}

	public FinScheduleData getRecalChangeProfit(FinScheduleData finScheduleData, BigDecimal adjustedPft) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		finSchdData = ScheduleCalculator.changeProfit(finScheduleData, adjustedPft);
		finSchdData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchdData;
	}

	/**
	 * 
	 * @param fsi
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		String finReference = fsi.getFinReference();

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "", isWIF);

		Date appDate = SysParamUtil.getAppDate();

		if (DateUtil.compare(fsi.getRecalFromDate(), appDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Recal From date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(appDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm)));
			return auditDetail;
		}

		// validate RecalType
		if (StringUtils.isNotBlank(fsi.getRecalType())) {
			if (!StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm)));
			}

			if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				if (fsi.getTerms() <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = "Number of Terms";
					valueParm[1] = fsi.getRecalType();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91112", "", valueParm)));
				}
			}
		}

		// validate reCalFromDate
		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (fsi.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm)));
				return auditDetail;
			} else if (fsi.getRecalFromDate().compareTo(fm.getFinStartDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = "FinanceStartDate:" + DateUtil.formatToShortDate(fm.getFinStartDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			} else if (fsi.getRecalFromDate().compareTo(fm.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = DateUtil.formatToShortDate(fm.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm)));
			}
		}

		// validate reCalToDate
		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (fsi.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm)));
				return auditDetail;
			} else if (fsi.getRecalToDate().compareTo(fsi.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalToDate:" + DateUtil.formatToShortDate(fsi.getRecalToDate());
				valueParm[1] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			}
		}

		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(fm.getFinID(), "",
				isWIF);
		if (schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				/*
				 * if(DateUtility.compare(fromDate, schDetail.getSchDate()) == 0) { isValidFromDate = true;
				 * if(checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) { return auditDetail; } }
				 */
				if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
				if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
					if (DateUtil.compare(fsi.getRecalToDate(), schDetail.getSchDate()) == 0) {
						isValidRecalToDate = true;
						if (checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
							return auditDetail;
						}
					}
				}
			}

			/*
			 * if(!isValidFromDate) { String[] valueParm = new String[1]; valueParm[0] =
			 * "FromDate:"+DateUtility.formatToShortDate(finServiceInstruction.getFromDate());
			 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("91111", "", valueParm))); }
			 */
			if (!isValidRecalFromDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm)));
			}
			if (!isValidRecalToDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
				String[] valueParm = new String[1];
				valueParm[0] = "RecalToDate:" + DateUtil.formatToShortDate(fsi.getRecalToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm)));
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
		if (!((curSchd.isRepayOnSchDate()
				|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate()
						&& !curSchd.isSchPftPaid())
						|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
								&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}

		if (curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90262", "", valueParm)));
			return auditDetail;
		}
		return null;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
