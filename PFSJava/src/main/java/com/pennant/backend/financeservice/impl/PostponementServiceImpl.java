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
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PostponementServiceImpl extends GenericService<FinServiceInstruction> implements PostponementService {

	private static Logger logger = LogManager.getLogger(PostponementServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * Method for Processing Postponement for the selected period by adding new terms
	 * 
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doPostponement(FinScheduleData finScheduleData, FinServiceInstruction serviceInstruction,
			String scheduleMethod) {
		logger.debug("Entering");

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();

		finScheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finScheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finScheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finScheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());

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
					if (DateUtil.compare(curSchd.getSchDate(),
							finScheduleData.getFinanceMain().getEventFromDate()) >= 0
							&& DateUtil.compare(curSchd.getSchDate(),
									finScheduleData.getFinanceMain().getEventToDate()) <= 0) {
						curSchd.setRecalLock(false);
					} else {
						curSchd.setRecalLock(true);
					}
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		finScheduleData = ScheduleCalculator.postpone(finScheduleData);

		BigDecimal newTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finScheduleData.setPftChg(pftDiff);

		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Processing Unplanned EMI holidays for the selected period
	 * 
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doUnPlannedEMIH(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		String scheduleMethod = finScheduleData.getFinanceMain().getScheduleMethod();

		if (finScheduleData.getFinServiceInstructions() != null) {
			for (FinServiceInstruction serviceInst : finScheduleData.getFinServiceInstructions()) {
				finScheduleData.getFinanceMain().setRecalType(serviceInst.getRecalType());
				finScheduleData.getFinanceMain().setRecalFromDate(serviceInst.getRecalFromDate());
				finScheduleData.getFinanceMain().setRecalToDate(serviceInst.getRecalToDate());
				finScheduleData.getFinanceMain().setAdjTerms(serviceInst.getTerms());
				finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
				finScheduleData.getFinanceMain().setPftIntact(serviceInst.isPftIntact());
			}
		}

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
					if (DateUtil.compare(curSchd.getSchDate(),
							finScheduleData.getFinanceMain().getEventFromDate()) >= 0
							&& DateUtil.compare(curSchd.getSchDate(),
									finScheduleData.getFinanceMain().getEventToDate()) <= 0) {
						curSchd.setRecalLock(false);
					} else {
						curSchd.setRecalLock(true);
					}
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// call schedule calculator
		finScheduleData = ScheduleCalculator.unPlannedEMIH(finScheduleData, BigDecimal.ZERO, scheduleMethod);

		BigDecimal newTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finScheduleData.setPftChg(pftDiff);
		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Processing Re-Aging for the selected period by adding new terms
	 * 
	 * @param finScheduleData
	 * @return FinScheduleData
	 */
	@Override
	public FinScheduleData doReAging(FinScheduleData finScheduleData, FinServiceInstruction serviceInstruction,
			String scheduleMethod) {
		logger.debug("Entering");

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		finScheduleData.getFinanceMain().setRecalType(serviceInstruction.getRecalType());
		finScheduleData.getFinanceMain().setRecalFromDate(serviceInstruction.getRecalFromDate());
		finScheduleData.getFinanceMain().setRecalToDate(serviceInstruction.getRecalToDate());
		finScheduleData.getFinanceMain().setRecalSchdMethod(scheduleMethod);
		finScheduleData.getFinanceMain().setPftIntact(serviceInstruction.isPftIntact());

		// Schedule Recalculation Locking Period Applicability
		// ImplementationConstants moved to SMT Parameter
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
					if (DateUtil.compare(curSchd.getSchDate(),
							finScheduleData.getFinanceMain().getEventFromDate()) >= 0
							&& DateUtil.compare(curSchd.getSchDate(),
									finScheduleData.getFinanceMain().getEventToDate()) <= 0) {
						curSchd.setRecalLock(false);
					} else {
						curSchd.setRecalLock(true);
					}
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		finScheduleData = ScheduleCalculator.reAging(finScheduleData);

		BigDecimal newTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finScheduleData.setPftChg(pftDiff);
		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for validate defferment instructions.
	 * 
	 * @param fsi
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		String finReference = fsi.getFinReference();

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "", isWIF);

		long finID = fm.getFinID();

		// validate from date with finStart date and maturity date
		if (fsi.getFromDate().compareTo(fm.getFinStartDate()) < 0
				|| fsi.getFromDate().compareTo(fm.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "finance start date:" + DateUtil.formatToShortDate(fm.getFinStartDate());
			valueParm[2] = "maturity date:" + DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		// validate to date
		if (fsi.getToDate().compareTo(fm.getMaturityDate()) >= 0 || fsi.getToDate().compareTo(fsi.getFromDate()) < 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtil.formatToShortDate(fsi.getFromDate());
			valueParm[2] = DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91102", "", valueParm), lang));
			return auditDetail;
		}

		if (fsi.getToDate().compareTo(fsi.getFromDate()) <= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtil.formatToShortDate(fsi.getFromDate());
			valueParm[2] = DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91102", "", valueParm), lang));
			return auditDetail;
		}

		// validate RecalType
		if (StringUtils.isNotBlank(fsi.getRecalType())) {
			if (!StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
					&& !StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm), lang));
			}
		}

		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDTERM)) {
			if (fsi.getTerms() <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Number of Terms";
				valueParm[1] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91112", "", valueParm), lang));
			}
		}

		// validate reCalFromDate
		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (fsi.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm), lang));
				return auditDetail;
			} else if (fsi.getRecalFromDate().compareTo(fsi.getToDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = DateUtil.formatToShortDate(fsi.getToDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91106", "", valueParm), lang));
			} else if (fsi.getRecalFromDate().compareTo(fm.getMaturityDate()) >= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = DateUtil.formatToShortDate(fm.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm), lang));
			}
		}

		// validate reCalToDate
		if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if (fsi.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm), lang));
				return auditDetail;
			} else if (fsi.getRecalToDate().compareTo(fsi.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(fsi.getRecalToDate());
				valueParm[1] = DateUtil.formatToShortDate(fsi.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91109", "", valueParm), lang));
			}
		}

		boolean isValidFromDate = false;
		boolean isValidToDate = false;
		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);

		for (FinanceScheduleDetail schDetail : schedules) {
			// FromDate
			if (DateUtil.compare(fsi.getFromDate(), schDetail.getSchDate()) == 0) {
				isValidFromDate = true;
				if (checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
					return auditDetail;
				}
			}
			// ToDate
			if (DateUtil.compare(fsi.getToDate(), schDetail.getSchDate()) == 0) {
				isValidToDate = true;
				if (checkIsValidRepayDate(auditDetail, schDetail, "ToDate") != null) {
					return auditDetail;
				}
			}
			// RecalFromDate
			if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
					isValidRecalFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
						return auditDetail;
					}
				}
			}
			// RecalToDate
			if (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				if (DateUtil.compare(fsi.getRecalToDate(), schDetail.getSchDate()) == 0) {
					isValidRecalToDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
						return auditDetail;
					}
				}
			}
		}

		if (!isValidFromDate) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
		}
		if (!isValidToDate) {
			String[] valueParm = new String[1];
			valueParm[0] = "ToDate:" + DateUtil.formatToShortDate(fsi.getToDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
		}
		if (!isValidRecalFromDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
			String[] valueParm = new String[1];
			valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
		}
		if (!isValidRecalToDate && (StringUtils.equals(fsi.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
			String[] valueParm = new String[1];
			valueParm[0] = "RecalToDate:" + DateUtil.formatToShortDate(fsi.getRecalToDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
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
		return null;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}
