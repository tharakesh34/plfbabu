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
import com.pennanttech.pennapps.core.resource.Literal;
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
	 * @param schdData
	 * @param fsi
	 */
	public FinScheduleData getRateChangeDetails(FinScheduleData schdData, FinServiceInstruction fsi,
			String moduleDefiner) {
		logger.debug(Literal.ENTERING);

		BigDecimal oldTotalPft = schdData.getFinanceMain().getTotalGrossPft();
		FinanceMain financeMain = schdData.getFinanceMain();

		int sdSize = schdData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail curSchd = null;

		Date recalLockTill = schdData.getFinanceMain().getRecalFromDate();
		if (recalLockTill == null) {
			recalLockTill = schdData.getFinanceMain().getMaturityDate();
		}

		for (int i = 0; i <= sdSize - 1; i++) {
			curSchd = schdData.getFinanceScheduleDetails().get(i);

			// Profit Days Basis Setting for Calculation Process
			if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) >= 0
					&& curSchd.getSchDate().compareTo(financeMain.getEventToDate()) < 0
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				curSchd.setPftDaysBasis(fsi.getPftDaysBasis());
			}

			// Schedule Recalculation Locking Period Applicability
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
				if (DateUtil.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// Step POS Case , setting Step Details to Object
		boolean isCalSchedule = true;
		if (StringUtils.equals(schdData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPPOS)) {
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				schdData.setStepPolicyDetails(
						getFinanceStepDetailDAO().getFinStepDetailListByFinRef(schdData.getFinID(), "", false));
			}
			isCalSchedule = false;
		}

		if (financeMain.isManualSchedule()) {
			isCalSchedule = false;
		}

		// if (StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(),
		// CalculationConstants.RPYCHG_STEPINST)) {
		if (StringUtils.isNotEmpty(moduleDefiner) && schdData.getFinanceMain().isStepFinance()) {
			schdData.setStepPolicyDetails(
					getFinanceStepDetailDAO().getFinStepDetailListByFinRef(schdData.getFinID(), "", false));
		}
		// }

		FinScheduleData finSchData = null;
		finSchData = ScheduleCalculator.changeRate(schdData, fsi.getBaseRate(), fsi.getSplRate(),
				fsi.getMargin() == null ? BigDecimal.ZERO : fsi.getMargin(),
				fsi.getActualRate() == null ? BigDecimal.ZERO : fsi.getActualRate(), isCalSchedule);

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
	 * @param fsi
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail();
		String lang = "EN";

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		long finID = fsi.getFinID();
		String finReference = fsi.getFinReference();

		if (finServiceInstructionDAO.isFinServiceInstExists(finID, "_Temp")) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;// Some one else processed this record {finReference}
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41005", "", valueParm), lang));
			return auditDetail;
		}

		Date sysDate = SysParamUtil.getAppDate();
		Date appDate = DateUtil.addDays(sysDate,
				-SysParamUtil.getValueAsInt(SMTParameterConstants.RATE_CHANGE_FROM_DATE_BACK_DAYS));

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", isWIF);

		if (StringUtils.equals(UploadConstants.RATE_CHANGE_UPLOAD, fsi.getReqFrom())) {
			fsi.setPftDaysBasis(fm.getProfitDaysBasis());

			if (fsi.getToDate() == null) {
				fsi.setToDate(fm.getMaturityDate());
			}

			if (fsi.getFromDate() == null) {
				fsi.setFromDate(appDate);
			}

			if (StringUtils.isBlank(fsi.getBaseRate())) {
				fsi.setBaseRate(null);
			}
		}

		// validate from date with finStart date and maturity date
		if (DateUtil.compare(fsi.getFromDate(), appDate) < 0
				|| fsi.getFromDate().compareTo(fm.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToShortDate(appDate);
			valueParm[2] = "maturity date:" + DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		// validate to date
		if (fsi.getToDate().compareTo(fm.getMaturityDate()) > 0 || fsi.getToDate().compareTo(fsi.getFromDate()) <= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = "From date:" + DateUtil.formatToShortDate(fsi.getFromDate());
			valueParm[2] = "Maturity date:" + DateUtil.formatToShortDate(fm.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm), lang));
			return auditDetail;
		}

		// validate profit days basis
		if (StringUtils.isNotBlank(fsi.getPftDaysBasis())) {
			List<ValueLabel> profitDayBasis = PennantStaticListUtil.getProfitDaysBasis();
			boolean profitDaysSts = false;
			for (ValueLabel value : profitDayBasis) {
				if (StringUtils.equals(value.getValue(), fsi.getPftDaysBasis())) {
					profitDaysSts = true;
					break;
				}
			}
			if (!profitDaysSts) {
				String[] valueParm = new String[1];
				valueParm[0] = fsi.getPftDaysBasis();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91103", "", valueParm), lang));
			}
		}

		// validate recalType
		String recalType = fsi.getRecalType();
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
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
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
			if (fsi.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm), lang));
				return auditDetail;
			} else if (fsi.getRecalFromDate().compareTo(fsi.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = "FromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			} else if (fsi.getRecalFromDate().compareTo(fm.getMaturityDate()) >= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[1] = DateUtil.formatToShortDate(fm.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm), lang));
			}
		}

		// validate reCalToDate
		if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			if (fsi.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = recalType;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm), lang));
				return auditDetail;
			} else if (fsi.getRecalToDate().compareTo(fm.getMaturityDate()) >= 0
					|| fsi.getRecalToDate().compareTo(fsi.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "To Date: " + DateUtil.formatToShortDate(fsi.getRecalToDate());
				valueParm[1] = " From Date: " + DateUtil.formatToShortDate(fsi.getRecalFromDate());
				valueParm[2] = " Maturity Date: " + DateUtil.formatToShortDate(fsi.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			}
		}

		boolean isValidFromDate = false;
		boolean isValidToDate = false;
		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		// If presentment Extracted
		for (FinanceScheduleDetail schDetail : schedules) {
			if (schDetail.getPresentmentId() > 0) {
				if (DateUtil.compare(fsi.getFromDate(), schDetail.getSchDate()) < 0) {
					String[] valueParm = new String[3];
					valueParm[0] = "FromDate :" + DateUtil.formatToShortDate(fsi.getFromDate());
					valueParm[1] = DateUtil.formatToShortDate(schDetail.getSchDate());
					valueParm[2] = "Maturitydate:" + DateUtil.formatToShortDate(fm.getMaturityDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
				}
			}
		}

		if (!fm.isRateChgAnyDay()) {
			if (schedules != null) {
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
					}
					// RecalFromDate
					if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
							|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
						if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
							isValidRecalFromDate = true;
							if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
								return auditDetail;
							}
						}
					}
					// RecalToDate
					if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
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
				if (!isValidRecalFromDate && (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
						|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if (!isValidRecalToDate && (CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalToDate:" + DateUtil.formatToShortDate(fsi.getRecalToDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
			}
		} else {
			if (!CalculationConstants.RPYCHG_ADJMDT.equals(recalType)) {
				if (schedules != null) {
					for (FinanceScheduleDetail schDetail : schedules) {
						// RecalFromDate
						if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)
								|| CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
							if (DateUtil.compare(fsi.getRecalFromDate(), schDetail.getSchDate()) == 0) {
								isValidRecalFromDate = true;
								if (checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
									return auditDetail;
								}
							}
						}
						// RecalToDate
						if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
							if (DateUtil.compare(fsi.getRecalToDate(), schDetail.getSchDate()) == 0) {
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
						valueParm[0] = "RecalFromDate:" + DateUtil.formatToShortDate(fsi.getRecalFromDate());
						auditDetail.setErrorDetail(
								ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
					}
					if (!isValidRecalToDate && (CalculationConstants.RPYCHG_TILLDATE.equals(recalType))) {
						String[] valueParm = new String[1];
						valueParm[0] = "RecalToDate:" + DateUtil.formatToShortDate(fsi.getRecalToDate());
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
	private AuditDetail checkIsValidRepayDate(AuditDetail ad, FinanceScheduleDetail schedule, String label) {
		String[] valueParm = new String[1];
		valueParm[0] = label;

		boolean isrepay = schedule.isRepayOnSchDate();

		if (isrepay && (!schedule.isPftOnSchDate() || schedule.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0)) {
			ad.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return ad;
		}

		boolean isPftPaid = schedule.getProfitSchd().compareTo(schedule.getSchdPftPaid()) >= 0;
		boolean schPftPaid = schedule.isSchPftPaid();

		if (isPftPaid && (!isrepay || !schPftPaid)) {
			return null;
		}

		boolean isPrincipalPaid = schedule.getPrincipalSchd().compareTo(schedule.getSchdPriPaid()) >= 0;
		boolean schPriPaid = schedule.isSchPriPaid();
		if (isPrincipalPaid && (!isrepay || !schPriPaid)) {
			return null;
		}

		ad.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
		return ad;
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
