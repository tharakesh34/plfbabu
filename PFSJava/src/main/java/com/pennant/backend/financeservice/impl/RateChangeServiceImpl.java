package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class RateChangeServiceImpl extends GenericService<FinServiceInstruction> implements RateChangeService {
	private static final Logger logger = LogManager.getLogger(RateChangeServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceStepDetailDAO financeStepDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	/**
	 * Method for perform the Rate change action
	 * 
	 * @param finScheduleData
	 * @param finServiceInst
	 */
	public FinScheduleData getRateChangeDetails(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst,
			String moduleDefiner) {
		logger.debug("Entering");

		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail curSchd = null;

		Date recalLockTill = finScheduleData.getFinanceMain().getRecalFromDate();
		if (recalLockTill == null) {
			recalLockTill = finScheduleData.getFinanceMain().getMaturityDate();
		}

		for (int i = 0; i <= sdSize - 1; i++) {
			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			// Profit Days Basis Setting for Calculation Process
			if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) >= 0
					&& curSchd.getSchDate().compareTo(financeMain.getEventToDate()) < 0
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				curSchd.setPftDaysBasis(finServiceInst.getPftDaysBasis());
			}

			// Schedule Recalculation Locking Period Applicability
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
				if (DateUtility.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// Step POS Case , setting Step Details to Object
		boolean isCalSchedule = true;
		if (StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPPOS)) {
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				finScheduleData.setStepPolicyDetails(getFinanceStepDetailDAO()
						.getFinStepDetailListByFinRef(finScheduleData.getFinReference(), "", false));
			}
			isCalSchedule = false;
		}

		//if (StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPINST)) {
		if (StringUtils.isNotEmpty(moduleDefiner) && finScheduleData.getFinanceMain().isStepFinance()) {
			finScheduleData.setStepPolicyDetails(getFinanceStepDetailDAO()
					.getFinStepDetailListByFinRef(finScheduleData.getFinReference(), "", false));
		}
		//}

		FinScheduleData finSchData = null;
		finSchData = ScheduleCalculator.changeRate(finScheduleData, finServiceInst.getBaseRate(),
				finServiceInst.getSplRate(),
				finServiceInst.getMargin() == null ? BigDecimal.ZERO : finServiceInst.getMargin(),
				finServiceInst.getActualRate() == null ? BigDecimal.ZERO : finServiceInst.getActualRate(),
				isCalSchedule);

		BigDecimal newTotalPft = finSchData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Validate service instruction data against addratechange service.
	 * 
	 * @param finSrvInst
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction finSrvInst) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = finSrvInst.isWif();
		String finReference = finSrvInst.getFinReference();

		if (finServiceInstructionDAO.isFinServiceInstExists(finReference, "_Temp")) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;//Some one else processed this record {finReference} 
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41005", "", valueParm), lang));
			return auditDetail;
		}

		Date appDate = SysParamUtil.getAppDate();
		FinanceMain financeMain = financeMainDAO.getFinanceBasicDetailByRef(finReference, isWIF);

		if (StringUtils.equals(UploadConstants.RATE_CHANGE_UPLOAD, finSrvInst.getReqFrom())) {
			finSrvInst.setPftDaysBasis(financeMain.getProfitDaysBasis());

			if (finSrvInst.getToDate() == null) {
				finSrvInst.setToDate(financeMain.getMaturityDate());
			}

			if (finSrvInst.getFromDate() == null) {
				finSrvInst.setFromDate(appDate);
			}

			if (StringUtils.isBlank(finSrvInst.getBaseRate())) {
				finSrvInst.setBaseRate(null);
			}
		}

		// validate from date with finStart date and maturity date
		if (DateUtil.compare(finSrvInst.getFromDate(), appDate) < 0
				|| finSrvInst.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToShortDate(appDate);
			valueParm[2] = "maturity date:" + DateUtil.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		// validate to date
		if (finSrvInst.getToDate().compareTo(financeMain.getMaturityDate()) > 0
				|| finSrvInst.getToDate().compareTo(finSrvInst.getFromDate()) <= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = "From date:" + DateUtil.formatToShortDate(finSrvInst.getFromDate());
			valueParm[2] = "Maturity date:" + DateUtil.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm), lang));
			return auditDetail;
		}

		// validate profit days basis
		if (StringUtils.isNotBlank(finSrvInst.getPftDaysBasis())) {
			List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
			boolean profitDaysSts = false;
			for (ValueLabel value : profitDayBasis) {
				if (StringUtils.equals(value.getValue(), finSrvInst.getPftDaysBasis())) {
					profitDaysSts = true;
					break;
				}
			}
			if (!profitDaysSts) {
				String[] valueParm = new String[1];
				valueParm[0] = finSrvInst.getPftDaysBasis();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91103", "", valueParm), lang));
			}
		}

		// validate recalType
		String recalType = finSrvInst.getRecalType();
		if (StringUtils.isNotBlank(recalType)) {
			if (!CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
					&& !CalculationConstants.RPYCHG_ADJMDT.equals(recalType)
					&& !CalculationConstants.RPYCHG_TILLDATE.equals(recalType)
					&& !CalculationConstants.RPYCHG_ADJTERMS.equals(recalType)
					&& !CalculationConstants.RPYCHG_CURPRD.equals(recalType)) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm), lang));
			}
		}

		// Over Draft Loan Type validation for recalType
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(financeMain.getProductCategory())) {
			// validate recalType
			if (StringUtils.isNotBlank(recalType)) {
				if (!CalculationConstants.RPYCHG_ADJMDT.equals(recalType)) {
					String[] valueParm = new String[1];
					valueParm[0] = recalType;
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm), lang));
				}
			}
		}

		// validate reCalFromDate
		if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
				|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			if (finSrvInst.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm), lang));
				return auditDetail;
			} else if (finSrvInst.getRecalFromDate().compareTo(finSrvInst.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(finSrvInst.getRecalFromDate());
				valueParm[1] = "FromDate:" + DateUtil.formatToShortDate(finSrvInst.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			} else if (finSrvInst.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(finSrvInst.getRecalFromDate());
				valueParm[1] = DateUtil.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm), lang));
			}
		}

		// validate reCalToDate
		if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			if (finSrvInst.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm), lang));
				return auditDetail;
			} else if (finSrvInst.getRecalToDate().compareTo(finSrvInst.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "To Date:" + DateUtil.formatToShortDate(finSrvInst.getRecalToDate());
				valueParm[1] = " From Date:" + DateUtil.formatToShortDate(finSrvInst.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			}
		}

		boolean isValidFromDate = false;
		boolean isValidToDate = false;
		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		if (!financeMain.isRateChgAnyDay()) {
			List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "",
					isWIF);
			if (schedules != null) {
				for (FinanceScheduleDetail schDetail : schedules) {
					// FromDate
					if (DateUtility.compare(finSrvInst.getFromDate(), schDetail.getSchDate()) == 0) {
						isValidFromDate = true;
						if (checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
							return auditDetail;
						}
					}
					// ToDate
					if (DateUtility.compare(finSrvInst.getToDate(), schDetail.getSchDate()) == 0) {
						isValidToDate = true;
					}
					// RecalFromDate
					if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
							|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
						if (DateUtility.compare(finSrvInst.getRecalFromDate(), schDetail.getSchDate()) == 0) {
							isValidRecalFromDate = true;
							if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
								return auditDetail;
							}
						}
					}
					// RecalToDate
					if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
						if (DateUtility.compare(finSrvInst.getRecalToDate(), schDetail.getSchDate()) == 0) {
							isValidRecalToDate = true;
							if (checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
								return auditDetail;
							}
						}
					}
				}

				if (!isValidFromDate) {
					String[] valueParm = new String[1];
					valueParm[0] = "FromDate:" + DateUtility.formatToShortDate(finSrvInst.getFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if (!isValidToDate) {
					String[] valueParm = new String[1];
					valueParm[0] = "ToDate:" + DateUtility.formatToShortDate(finSrvInst.getToDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if (!isValidRecalFromDate && (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
						|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalFromDate:" + DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if (!isValidRecalToDate && (CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalToDate:" + DateUtility.formatToShortDate(finSrvInst.getRecalToDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
			}
		} else {
			if (!CalculationConstants.RPYCHG_ADJMDT.equals(recalType)) {
				List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "",
						isWIF);
				if (schedules != null) {
					for (FinanceScheduleDetail schDetail : schedules) {
						// RecalFromDate
						if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
								|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
							if (DateUtility.compare(finSrvInst.getRecalFromDate(), schDetail.getSchDate()) == 0) {
								isValidRecalFromDate = true;
								if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
									return auditDetail;
								}
							}
						}
						// RecalToDate
						if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
							if (DateUtility.compare(finSrvInst.getRecalToDate(), schDetail.getSchDate()) == 0) {
								isValidRecalToDate = true;
								if (checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
									return auditDetail;
								}
							}
						}
					}

					if (!isValidRecalFromDate && (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
							|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
						String[] valueParm = new String[1];
						valueParm[0] = "RecalFromDate:" + DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
						auditDetail.setErrorDetail(
								ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
					}
					if (!isValidRecalToDate && (CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
						String[] valueParm = new String[1];
						valueParm[0] = "RecalToDate:" + DateUtility.formatToShortDate(finSrvInst.getRecalToDate());
						auditDetail.setErrorDetail(
								ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
					}
				}
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
		return null;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceStepDetailDAO getFinanceStepDetailDAO() {
		return financeStepDetailDAO;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}
