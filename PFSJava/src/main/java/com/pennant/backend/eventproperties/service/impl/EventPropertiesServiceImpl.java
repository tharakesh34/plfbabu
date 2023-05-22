package com.pennant.backend.eventproperties.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.util.DateUtil;

public class EventPropertiesServiceImpl implements EventPropertiesService {
	private PresentmentDAO presentmentDAO;

	public enum EventType {
		EOD(1), PRESENTMENT_BATCH_APPROVE(2), PRESENTMENT_RESPONSE_UPLOAD(3);

		int value;

		EventType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	@Override
	public EventProperties getEventProperties(EventType type) {
		switch (type.getValue()) {
		case 1:
			return getEODParameters();

		case 2:
			return getPresentmentBatchApprovalParameters();

		case 3:
			return getPresentmentResponseUploadParameters();

		default:
			return new EventProperties();
		}

	}

	private EventProperties getEODParameters() {
		EventProperties ep = new EventProperties();

		setSystemDates(ep);
		setRoundingModes(ep);
		setFeeTypeExcempted(ep);

		int amzPostingEvent = SysParamUtil.getValueAsInt(AccountConstants.AMZ_POSTING_EVENT);
		int accrualCalOn = SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON);
		String allowZeroPostings = SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_ZERO_POSTINGS);

		String provRule = SysParamUtil.getValueAsString(SMTParameterConstants.PROVISION_RULE);
		String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);

		String provisionBooks = SysParamUtil.getValueAsString(SMTParameterConstants.PROVISION_BOOKS);
		String npaTagging = SysParamUtil.getValueAsString(SMTParameterConstants.NPA_TAGGING);
		String provEffPostDate = SysParamUtil.getValueAsString(SMTParameterConstants.PROVISION_EFF_POSTDATE);

		boolean invAddrEntityBasis = SysParamUtil.isAllowed(SMTParameterConstants.INVOICE_ADDRESS_ENTITY_BASIS);
		String thresholdValue = (String) SysParamUtil.getValue(SMTParameterConstants.AUTO_KNOCKOFF_THRESHOLD);
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);

		ep.setEomOnEOD(SysParamUtil.isAllowed(SMTParameterConstants.EOM_ON_EOD));
		ep.setAcEffValDate(SysParamUtil.isAllowed(SMTParameterConstants.ACC_EFF_VALDATE));
		ep.setAcEffPostDate(SysParamUtil.isAllowed(SMTParameterConstants.ACCREV_EFF_POSTDATE));

		boolean isCalAccrualFromStart = true;
		// if the flag is 'Y' projected Accrual calculated from loan started without checking previous month data.
		if ("Y".equals(SysParamUtil.getValueAsString(SMTParameterConstants.MONTHENDACC_FROMFINSTARTDATE))) {
			isCalAccrualFromStart = false;
		}

		ep.setCalAccrualFromStart(isCalAccrualFromStart);
		ep.setSkipLatePay(SysParamUtil.isAllowed(SMTParameterConstants.EOD_SKIP_LATE_PAY_MARKING));
		ep.setSchRecalLock(SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK));
		ep.setAccrualReversalReq(SysParamUtil.isAllowed(SMTParameterConstants.ACCRUAL_REVERSAL_REQ));
		ep.setAppCurrency(SysParamUtil.getAppCurrency());
		ep.setDpdCalIncludeExcess(SysParamUtil.isAllowed(SMTParameterConstants.DPD_CALC_INCLUDE_EXCESS));
		ep.setMonthEndAccCallReq(SysParamUtil.isAllowed(AmortizationConstants.MONTHENDACC_CALREQ));

		ep.setBpiPaidOnInstDate(SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
		ep.setAdvTdsIncsUpf(SysParamUtil.isAllowed(SMTParameterConstants.ADVANCE_TDS_INCZ_UPF));
		ep.setBpiTdsDeductOnOrg(SysParamUtil.isAllowed(SMTParameterConstants.BPI_TDS_DEDUCT_ON_ORG));
		ep.setLocalCcy(CurrencyUtil.getCcyNumber(PennantConstants.LOCAL_CCY));
		ep.setAllowProvEod(SysParamUtil.isAllowed(SMTParameterConstants.ALW_PROV_EOD));
		ep.setCpzPosIntact(SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT));
		ep.setNpaRepayHierarchy(SysParamUtil.getValueAsString(SMTParameterConstants.RPYHCY_ON_NPA));
		ep.setDpdBucket(SysParamUtil.getValueAsInt(SMTParameterConstants.RPYHCY_ON_DPD_BUCKET));
		ep.setAlwDiffRepayOnNpa(SysParamUtil.isAllowed(SMTParameterConstants.ALW_DIFF_RPYHCY_NPA));
		ep.setGstInvOnDue(SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE));
		ep.setEodThreadCount(SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_THREAD_COUNT));
		ep.setCovenantModule(SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE));
		ep.setOverDraftMonthlyLimit(SysParamUtil.getValueAsInt(SMTParameterConstants.OVERDRAFT_LOANS_MONTHLY_LIMIT));

		BigDecimal ignoringBucket = BigDecimal.ZERO;
		Object object = SysParamUtil.getValue(SMTParameterConstants.IGNORING_BUCKET);
		if (object != null) {
			ignoringBucket = (BigDecimal) object;
		}
		ep.setIgnoringBucket(ignoringBucket);

		ep.setAmzPostingEvent(amzPostingEvent);
		ep.setAccrualCalOn(accrualCalOn);
		ep.setAllowZeroPostings(allowZeroPostings);

		ep.setProvRule(provRule);
		ep.setLmsServiceLogReq(lmsServiceLogReq);

		ep.setProvisionBooks(provisionBooks);
		ep.setNpaTagging(npaTagging);
		ep.setProvEffPostDate(provEffPostDate);

		ep.setInvAddrEntityBasis(invAddrEntityBasis);
		ep.setThresholdValue(thresholdValue);
		ep.setPhase(phase);

		String entityCode = SysParamUtil.getValueAsString(SMTParameterConstants.ENTITY_CODE);
		if (entityCode == null) {
			entityCode = "";
		}
		ep.setEntityCode(entityCode);

		ep.setUpfrontBounceCodes(presentmentDAO.getUpfrontBounceCodes());

		/**
		 * Auto Refund Parameters
		 */
		ep.setAutoRefundDaysForClosed(
				SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_CLOSED_LAN) - 1);
		ep.setAutoRefundDaysForActive(
				SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_N_DAYS_ACTIVE_LAN) - 1);
		ep.setAutoRefundCheckDPD(SysParamUtil.getValueAsInt(SMTParameterConstants.AUTO_REFUND_HOLD_DPD) - 1);
		ep.setAutoRefundOverdueCheck(SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_OVERDUE_CHECK));
		ep.setAutoRefundByCheque(SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_THROUGH_CHEQUE));
		ep.setAllowOTSOnEOD(SysParamUtil.isAllowed(SMTParameterConstants.ALW_OTS_ON_EOD));

		ep.setParameterLoaded(true);

		return ep;
	}

	private void setFeeTypeExcempted(EventProperties ep) {
		String pftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PFT_EXEMPTED);
		String priInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PRI_EXEMPTED);
		String fpftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPFT_EXEMPTED);
		String fpriInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_FPRI_EXEMPTED);

		ep.setPftInvFeeCode(pftInvFeeCode);
		ep.setPriInvFeeCode(priInvFeeCode);
		ep.setFpftInvFeeCode(fpftInvFeeCode);
		ep.setFpriInvFeeCode(fpriInvFeeCode);
	}

	private void setRoundingModes(EventProperties ep) {
		BigDecimal big100 = new BigDecimal(100);
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		BigDecimal tdsMultiplier = big100.divide(big100.subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
		int taxRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TAX_ROUNDINGTARGET);
		String taxRoundMode = SysParamUtil.getValue(CalculationConstants.TAX_ROUNDINGMODE).toString();

		ep.setTaxRoundMode(taxRoundMode);
		ep.setTaxRoundingTarget(taxRoundingTarget);
		ep.setTdsRoundMode(tdsRoundMode);
		ep.setTdsRoundingTarget(tdsRoundingTarget);
		ep.setTdsPerc(tdsPerc);
		ep.setTdsMultiplier(tdsMultiplier);
	}

	private void setSystemDates(EventProperties ep) {
		Date appDate = SysParamUtil.getAppDate();
		Date postDate = SysParamUtil.getPostDate();
		Date appValueDate = SysParamUtil.getAppValueDate();
		Date lastDate = SysParamUtil.getLastBusinessdate();
		Date nextDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);

		Date businessDate = DateUtil.addDays(appDate, 1);
		Date monthEndDate = DateUtil.getMonthEnd(appDate);
		Date monthStartDate = DateUtil.getMonthStart(appDate);
		Date prvMonthEndDate = DateUtil.addDays(monthStartDate, -1);

		ep.setAppDate(appDate);
		ep.setPostDate(postDate);
		ep.setAppValueDate(appValueDate);
		ep.setValueDate(appValueDate);
		ep.setNextDate(nextDate);
		ep.setLastDate(lastDate);

		ep.setBusinessDate(businessDate);
		ep.setMonthEndDate(monthEndDate);
		ep.setMonthStartDate(monthStartDate);
		ep.setPrvMonthEndDate(prvMonthEndDate);
	}

	private EventProperties getPresentmentBatchApprovalParameters() {
		EventProperties eventProperties = new EventProperties();

		return eventProperties;
	}

	private EventProperties getPresentmentResponseUploadParameters() {
		EventProperties ep = new EventProperties();

		ep.setParameterLoaded(true);
		ep.setCacheLoaded(true);

		setSystemDates(ep);

		ep.setDpdCalIncludeExcess(SysParamUtil.isAllowed(SMTParameterConstants.DPD_CALC_INCLUDE_EXCESS));
		ep.setIgnoringBucket(SysParamUtil.getValueAsBigDecimal(SMTParameterConstants.IGNORING_BUCKET));
		ep.setAccrualCalOn(SysParamUtil.getValueAsInt(SMTParameterConstants.ACCRUAL_CAL_ON));
		ep.setAllowZeroPostings(SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_ZERO_POSTINGS));

		setRoundingModes(ep);

		return ep;
	}

	@Autowired
	public void setPresentmentDAO(PresentmentDAO presentmentDAO) {
		this.presentmentDAO = presentmentDAO;
	}
}
