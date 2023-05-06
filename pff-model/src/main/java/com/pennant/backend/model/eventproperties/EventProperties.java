package com.pennant.backend.model.eventproperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventProperties implements Serializable {
	private static final long serialVersionUID = -2785381430191820055L;

	private Date appDate;
	private Date postDate;
	private Date appValueDate;
	private Date valueDate;
	private Date nextDate;
	private Date lastDate;
	private Date businessDate;
	private Date monthEndDate;
	private Date monthStartDate;
	private Date prvMonthEndDate;
	private int amzPostingEvent;
	private String taxRoundMode;
	private int taxRoundingTarget;
	private boolean acEffValDate;
	private boolean acEffPostDate;
	private boolean eomOnEOD;
	private int accrualCalOn;
	private String allowZeroPostings;
	private boolean calAccrualFromStart = true;
	private String tdsRoundMode;
	private int tdsRoundingTarget;
	private BigDecimal tdsPerc;
	private BigDecimal tdsMultiplier;
	private BigDecimal ignoringBucket;
	private String provRule;
	private boolean skipLatePay;
	private boolean schRecalLock;
	private boolean accrualReversalReq;
	private String appCurrency;
	private boolean dpdCalIncludeExcess;
	private String lmsServiceLogReq;
	private String provisionBooks;
	private String npaTagging;
	private String provEffPostDate;
	private boolean monthEndAccCallReq;
	private String entityCode;
	private String pftInvFeeCode;
	private String priInvFeeCode;
	private String fpftInvFeeCode;
	private String fpriInvFeeCode;
	private boolean invAddrEntityBasis;
	private boolean bpiPaidOnInstDate;
	private boolean advTdsIncsUpf;
	private boolean bpiTdsDeductOnOrg;
	private String localCcy;
	private boolean allowProvEod;
	private String thresholdValue;
	private boolean cpzPosIntact;
	private String npaRepayHierarchy;
	private int dpdBucket;
	private boolean alwDiffRepayOnNpa;
	private boolean gstInvOnDue;
	private int eodThreadCount;
	private String phase;
	private boolean covenantModule;
	private int overDraftMonthlyLimit;
	private Map<String, String> upfrontBounceCodes = new HashMap<>();
	private int autoRefundDaysForClosed;
	private int autoRefundDaysForActive;
	private int autoRefundCheckDPD;
	private boolean autoRefundOverdueCheck;
	private boolean autoRefundByCheque;
	private boolean allowOTSOnEOD;
	private boolean parameterLoaded;
	private boolean cacheLoaded;

	public EventProperties() {
		super();
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getAppValueDate() {
		return appValueDate;
	}

	public void setAppValueDate(Date appValueDate) {
		this.appValueDate = appValueDate;
	}

	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public Date getMonthEndDate() {
		return monthEndDate;
	}

	public void setMonthEndDate(Date monthEndDate) {
		this.monthEndDate = monthEndDate;
	}

	public Date getMonthStartDate() {
		return monthStartDate;
	}

	public void setMonthStartDate(Date monthStartDate) {
		this.monthStartDate = monthStartDate;
	}

	public Date getPrvMonthEndDate() {
		return prvMonthEndDate;
	}

	public void setPrvMonthEndDate(Date prvMonthEndDate) {
		this.prvMonthEndDate = prvMonthEndDate;
	}

	public int getAmzPostingEvent() {
		return amzPostingEvent;
	}

	public void setAmzPostingEvent(int amzPostingEvent) {
		this.amzPostingEvent = amzPostingEvent;
	}

	public String getTaxRoundMode() {
		return taxRoundMode;
	}

	public void setTaxRoundMode(String taxRoundMode) {
		this.taxRoundMode = taxRoundMode;
	}

	public int getTaxRoundingTarget() {
		return taxRoundingTarget;
	}

	public void setTaxRoundingTarget(int taxRoundingTarget) {
		this.taxRoundingTarget = taxRoundingTarget;
	}

	public boolean getAcEffValDate() {
		return acEffValDate;
	}

	public void setAcEffValDate(boolean acEffValDate) {
		this.acEffValDate = acEffValDate;
	}

	public boolean isAcEffPostDate() {
		return acEffPostDate;
	}

	public void setAcEffPostDate(boolean acEffPostDate) {
		this.acEffPostDate = acEffPostDate;
	}

	public boolean isEomOnEOD() {
		return eomOnEOD;
	}

	public void setEomOnEOD(boolean eomOnEOD) {
		this.eomOnEOD = eomOnEOD;
	}

	public int getAccrualCalOn() {
		return accrualCalOn;
	}

	public void setAccrualCalOn(int accrualCalOn) {
		this.accrualCalOn = accrualCalOn;
	}

	public String getAllowZeroPostings() {
		return allowZeroPostings;
	}

	public void setAllowZeroPostings(String allowZeroPostings) {
		this.allowZeroPostings = allowZeroPostings;
	}

	public boolean isCalAccrualFromStart() {
		return calAccrualFromStart;
	}

	public void setCalAccrualFromStart(boolean calAccrualFromStart) {
		this.calAccrualFromStart = calAccrualFromStart;
	}

	public String getTdsRoundMode() {
		return tdsRoundMode;
	}

	public void setTdsRoundMode(String tdsRoundMode) {
		this.tdsRoundMode = tdsRoundMode;
	}

	public int getTdsRoundingTarget() {
		return tdsRoundingTarget;
	}

	public void setTdsRoundingTarget(int tdsRoundingTarget) {
		this.tdsRoundingTarget = tdsRoundingTarget;
	}

	public BigDecimal getTdsPerc() {
		return tdsPerc;
	}

	public void setTdsPerc(BigDecimal tdsPerc) {
		this.tdsPerc = tdsPerc;
	}

	public BigDecimal getTdsMultiplier() {
		return tdsMultiplier;
	}

	public void setTdsMultiplier(BigDecimal tdsMultiplier) {
		this.tdsMultiplier = tdsMultiplier;
	}

	public BigDecimal getIgnoringBucket() {
		return ignoringBucket;
	}

	public void setIgnoringBucket(BigDecimal ignoringBucket) {
		this.ignoringBucket = ignoringBucket;
	}

	public String getProvRule() {
		return provRule;
	}

	public void setProvRule(String provRule) {
		this.provRule = provRule;
	}

	public boolean isSchRecalLock() {
		return schRecalLock;
	}

	public void setSchRecalLock(boolean schRecalLock) {
		this.schRecalLock = schRecalLock;
	}

	public boolean isSkipLatePay() {
		return skipLatePay;
	}

	public void setSkipLatePay(boolean skipLatePay) {
		this.skipLatePay = skipLatePay;
	}

	public boolean isAccrualReversalReq() {
		return accrualReversalReq;
	}

	public void setAccrualReversalReq(boolean accrualReversalReq) {
		this.accrualReversalReq = accrualReversalReq;
	}

	public String getAppCurrency() {
		return appCurrency;
	}

	public void setAppCurrency(String appCurrency) {
		this.appCurrency = appCurrency;
	}

	public boolean isDpdCalIncludeExcess() {
		return dpdCalIncludeExcess;
	}

	public void setDpdCalIncludeExcess(boolean dpdCalIncludeExcess) {
		this.dpdCalIncludeExcess = dpdCalIncludeExcess;
	}

	public String getLmsServiceLogReq() {
		return lmsServiceLogReq;
	}

	public void setLmsServiceLogReq(String lmsServiceLogReq) {
		this.lmsServiceLogReq = lmsServiceLogReq;
	}

	public String getProvisionBooks() {
		return provisionBooks;
	}

	public void setProvisionBooks(String provisionBooks) {
		this.provisionBooks = provisionBooks;
	}

	public String getNpaTagging() {
		return npaTagging;
	}

	public void setNpaTagging(String npaTagging) {
		this.npaTagging = npaTagging;
	}

	public String getProvEffPostDate() {
		return provEffPostDate;
	}

	public void setProvEffPostDate(String provEffPostDate) {
		this.provEffPostDate = provEffPostDate;
	}

	public boolean isMonthEndAccCallReq() {
		return monthEndAccCallReq;
	}

	public void setMonthEndAccCallReq(boolean monthEndAccCallReq) {
		this.monthEndAccCallReq = monthEndAccCallReq;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getPftInvFeeCode() {
		return pftInvFeeCode;
	}

	public void setPftInvFeeCode(String pftInvFeeCode) {
		this.pftInvFeeCode = pftInvFeeCode;
	}

	public String getPriInvFeeCode() {
		return priInvFeeCode;
	}

	public void setPriInvFeeCode(String priInvFeeCode) {
		this.priInvFeeCode = priInvFeeCode;
	}

	public String getFpftInvFeeCode() {
		return fpftInvFeeCode;
	}

	public void setFpftInvFeeCode(String fpftInvFeeCode) {
		this.fpftInvFeeCode = fpftInvFeeCode;
	}

	public String getFpriInvFeeCode() {
		return fpriInvFeeCode;
	}

	public void setFpriInvFeeCode(String fpriInvFeeCode) {
		this.fpriInvFeeCode = fpriInvFeeCode;
	}

	public boolean isInvAddrEntityBasis() {
		return invAddrEntityBasis;
	}

	public void setInvAddrEntityBasis(boolean invAddrEntityBasis) {
		this.invAddrEntityBasis = invAddrEntityBasis;
	}

	public boolean isBpiPaidOnInstDate() {
		return bpiPaidOnInstDate;
	}

	public void setBpiPaidOnInstDate(boolean bpiPaidOnInstDate) {
		this.bpiPaidOnInstDate = bpiPaidOnInstDate;
	}

	public boolean isAdvTdsIncsUpf() {
		return advTdsIncsUpf;
	}

	public void setAdvTdsIncsUpf(boolean advTdsIncsUpf) {
		this.advTdsIncsUpf = advTdsIncsUpf;
	}

	public boolean isBpiTdsDeductOnOrg() {
		return bpiTdsDeductOnOrg;
	}

	public void setBpiTdsDeductOnOrg(boolean bpiTdsDeductOnOrg) {
		this.bpiTdsDeductOnOrg = bpiTdsDeductOnOrg;
	}

	public String getLocalCcy() {
		return localCcy;
	}

	public void setLocalCcy(String localCcy) {
		this.localCcy = localCcy;
	}

	public boolean isAllowProvEod() {
		return allowProvEod;
	}

	public void setAllowProvEod(boolean allowProvEod) {
		this.allowProvEod = allowProvEod;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public boolean isCpzPosIntact() {
		return cpzPosIntact;
	}

	public void setCpzPosIntact(boolean cpzPosIntact) {
		this.cpzPosIntact = cpzPosIntact;
	}

	public String getNpaRepayHierarchy() {
		return npaRepayHierarchy;
	}

	public void setNpaRepayHierarchy(String npaRepayHierarchy) {
		this.npaRepayHierarchy = npaRepayHierarchy;
	}

	public int getDpdBucket() {
		return dpdBucket;
	}

	public void setDpdBucket(int dpdBucket) {
		this.dpdBucket = dpdBucket;
	}

	public boolean isAlwDiffRepayOnNpa() {
		return alwDiffRepayOnNpa;
	}

	public void setAlwDiffRepayOnNpa(boolean alwDiffRepayOnNpa) {
		this.alwDiffRepayOnNpa = alwDiffRepayOnNpa;
	}

	public boolean isGstInvOnDue() {
		return gstInvOnDue;
	}

	public void setGstInvOnDue(boolean gstInvOnDue) {
		this.gstInvOnDue = gstInvOnDue;
	}

	public int getEodThreadCount() {
		return eodThreadCount;
	}

	public void setEodThreadCount(int eodThreadCount) {
		this.eodThreadCount = eodThreadCount;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public boolean isCovenantModule() {
		return covenantModule;
	}

	public void setCovenantModule(boolean covenantModule) {
		this.covenantModule = covenantModule;
	}

	public int getOverDraftMonthlyLimit() {
		return overDraftMonthlyLimit;
	}

	public void setOverDraftMonthlyLimit(int overDraftMonthlyLimit) {
		this.overDraftMonthlyLimit = overDraftMonthlyLimit;
	}

	public boolean isParameterLoaded() {
		return parameterLoaded;
	}

	public void setParameterLoaded(boolean parameterLoaded) {
		this.parameterLoaded = parameterLoaded;
	}

	public boolean isCacheLoaded() {
		return cacheLoaded;
	}

	public void setCacheLoaded(boolean cacheLoaded) {
		this.cacheLoaded = cacheLoaded;
	}

	public Map<String, String> getUpfrontBounceCodes() {
		return upfrontBounceCodes;
	}

	public void setUpfrontBounceCodes(Map<String, String> upfrontBounceCodes) {
		this.upfrontBounceCodes = upfrontBounceCodes;
	}

	public int getAutoRefundDaysForClosed() {
		return autoRefundDaysForClosed;
	}

	public void setAutoRefundDaysForClosed(int autoRefundDaysForClosed) {
		this.autoRefundDaysForClosed = autoRefundDaysForClosed;
	}

	public int getAutoRefundDaysForActive() {
		return autoRefundDaysForActive;
	}

	public void setAutoRefundDaysForActive(int autoRefundDaysForActive) {
		this.autoRefundDaysForActive = autoRefundDaysForActive;
	}

	public int getAutoRefundCheckDPD() {
		return autoRefundCheckDPD;
	}

	public void setAutoRefundCheckDPD(int autoRefundCheckDPD) {
		this.autoRefundCheckDPD = autoRefundCheckDPD;
	}

	public boolean isAutoRefundOverdueCheck() {
		return autoRefundOverdueCheck;
	}

	public void setAutoRefundOverdueCheck(boolean autoRefundOverdueCheck) {
		this.autoRefundOverdueCheck = autoRefundOverdueCheck;
	}

	public boolean isAutoRefundByCheque() {
		return autoRefundByCheque;
	}

	public void setAutoRefundByCheque(boolean autoRefundByCheque) {
		this.autoRefundByCheque = autoRefundByCheque;
	}

	public boolean isAllowOTSOnEOD() {
		return allowOTSOnEOD;
	}

	public void setAllowOTSOnEOD(boolean allowOTSOnEOD) {
		this.allowOTSOnEOD = allowOTSOnEOD;
	}

}
