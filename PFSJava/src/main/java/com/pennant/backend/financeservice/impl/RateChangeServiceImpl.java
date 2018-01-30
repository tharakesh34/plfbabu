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
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.model.ErrorDetail;
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
	private FinanceStepDetailDAO		financeStepDetailDAO;
	private FinanceMainDAO financeMainDAO;

	/**
	 * Method for perform the Rate change action
	 * 
	 * @param finScheduleData
	 * @param finServiceInst
	 */
	public FinScheduleData getRateChangeDetails(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst, String moduleDefiner) {
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
		
		// Step POS Case , setting Step Details to Object
		boolean isCalSchedule = true;
		if(StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPPOS)){
			if(StringUtils.isNotEmpty(moduleDefiner)){
			finScheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finScheduleData.getFinReference(),
					"", false));
			}
			isCalSchedule = false;
		}

		FinScheduleData finSchData = null;
		finSchData = ScheduleCalculator.changeRate(finScheduleData, finServiceInst.getBaseRate(), finServiceInst.getSplRate(),
				finServiceInst.getMargin() == null ? BigDecimal.ZERO : finServiceInst.getMargin(),
						finServiceInst.getActualRate() == null ? BigDecimal.ZERO : finServiceInst.getActualRate(), isCalSchedule);

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

		FinanceMain financeMain = financeMainDAO.getFinanceBasicDetailByRef(finReference, isWIF);

		// validate from date with finStart date and maturity date
		if(DateUtility.compare(finSrvInst.getFromDate(), DateUtility.getAppDate()) < 0
				|| finSrvInst.getFromDate().compareTo(financeMain.getMaturityDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "From date";
			valueParm[1] = "application date:"+DateUtility.formatToShortDate(DateUtility.getAppDate());
			valueParm[2] = "maturity date:"+DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), lang));
			return auditDetail;
		}

		// validate to date
		if(finSrvInst.getToDate().compareTo(financeMain.getMaturityDate()) >= 0 
				|| finSrvInst.getToDate().compareTo(finSrvInst.getFromDate()) < 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "ToDate";
			valueParm[1] = DateUtility.formatToShortDate(finSrvInst.getFromDate());
			valueParm[2] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91102", "", valueParm), lang));
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
		if(StringUtils.isNotBlank(finSrvInst.getRecalType())) {
			if(!StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
					&& !StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				String[] valueParm = new String[1];
				valueParm[0] = finSrvInst.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91104", "", valueParm), lang));
			}
		}

		// validate reCalFromDate
		if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT) ||
				StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finSrvInst.getRecalFromDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finSrvInst.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91105", "", valueParm), lang));
				return auditDetail;
			} else if(finSrvInst.getRecalFromDate().compareTo(finSrvInst.getFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
				valueParm[1] = "FromDate:"+DateUtility.formatToShortDate(finSrvInst.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			} else if(finSrvInst.getRecalFromDate().compareTo(financeMain.getMaturityDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
				valueParm[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91114", "", valueParm), lang));
			}
		}

		// validate reCalToDate
		if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
			if(finSrvInst.getRecalToDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finSrvInst.getRecalType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91108", "", valueParm), lang));
				return auditDetail;
			} else if(finSrvInst.getRecalToDate().compareTo(finSrvInst.getRecalFromDate()) <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = DateUtility.formatToShortDate(finSrvInst.getRecalToDate());
				valueParm[1] = DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91109", "", valueParm), lang));
			}
		}

		boolean isValidFromDate = false;
		boolean isValidToDate = false;
		boolean isValidRecalFromDate = false;
		boolean isValidRecalToDate = false;
		if(!financeMain.isRateChgAnyDay()) {
			List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
			if(schedules != null) {
				for(FinanceScheduleDetail schDetail: schedules) {
					// FromDate
					if(DateUtility.compare(finSrvInst.getFromDate(), schDetail.getSchDate()) == 0) {
						isValidFromDate = true;
						if(checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
							return auditDetail;
						}
					}
					// ToDate
					if(DateUtility.compare(finSrvInst.getToDate(), schDetail.getSchDate()) == 0) {
						isValidToDate = true;
					}
					// RecalFromDate
					if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
							|| StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
						if(DateUtility.compare(finSrvInst.getRecalFromDate(), schDetail.getSchDate()) == 0) {
							isValidRecalFromDate = true;
							if(checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
								return auditDetail;
							}
						}
					}
					// RecalToDate
					if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
						if(DateUtility.compare(finSrvInst.getRecalToDate(), schDetail.getSchDate()) == 0) {
							isValidRecalToDate = true;
							if(checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
								return auditDetail;
							}
						}
					}
				}

				if(!isValidFromDate) {
					String[] valueParm = new String[1];
					valueParm[0] = "FromDate:"+DateUtility.formatToShortDate(finSrvInst.getFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if(!isValidToDate) {
					String[] valueParm = new String[1];
					valueParm[0] = "ToDate:"+DateUtility.formatToShortDate(finSrvInst.getToDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if(!isValidRecalFromDate && (StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
						|| StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
				if(!isValidRecalToDate && (StringUtils.equals(finSrvInst.getRecalType(), 
						CalculationConstants.RPYCHG_TILLDATE))) {
					String[] valueParm = new String[1];
					valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finSrvInst.getRecalToDate());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				}
			}
		} else {
			if(!StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", isWIF);
				if(schedules != null) {
					for(FinanceScheduleDetail schDetail: schedules) {
						// RecalFromDate
						if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
								|| StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
							if(DateUtility.compare(finSrvInst.getRecalFromDate(), schDetail.getSchDate()) == 0) {
								isValidRecalFromDate = true;
								if(checkIsValidRepayDate(auditDetail, schDetail, "RecalFromDate") != null) {
									return auditDetail;
								}
							}
						}
						// RecalToDate
						if(StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
							if(DateUtility.compare(finSrvInst.getRecalToDate(), schDetail.getSchDate()) == 0) {
								isValidRecalToDate = true;
								if(checkIsValidRepayDate(auditDetail, schDetail, "RecalToDate") != null) {
									return auditDetail;
								}
							}
						}
					}
					
					if(!isValidRecalFromDate && (StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)
							|| StringUtils.equals(finSrvInst.getRecalType(), CalculationConstants.RPYCHG_TILLDATE))) {
						String[] valueParm = new String[1];
						valueParm[0] = "RecalFromDate:"+DateUtility.formatToShortDate(finSrvInst.getRecalFromDate());
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
					}
					if(!isValidRecalToDate && (StringUtils.equals(finSrvInst.getRecalType(), 
							CalculationConstants.RPYCHG_TILLDATE))) {
						String[] valueParm = new String[1];
						valueParm[0] = "RecalToDate:"+DateUtility.formatToShortDate(finSrvInst.getRecalToDate());
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
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
	
	public FinanceStepDetailDAO getFinanceStepDetailDAO() {
		return financeStepDetailDAO;
	}
	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}
	
}
