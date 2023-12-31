package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class RestructureDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3793133053909214411L;

	private long id;
	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private Date restructureDate;
	private Date appDate;
	@XmlElement
	private int emiHldPeriod = 0;
	@XmlElement
	private int priHldPeriod = 0;
	@XmlElement
	private int emiPeriods = 0;
	@XmlElement
	private String restructureType;
	@XmlElement
	private String restructureReason;
	private boolean tenorChange;
	private boolean emiRecal;
	private int totNoOfRestructure = 0;
	@XmlElement
	private String recalculationType;
	private String serviceRequestNo;
	private String remark;
	private int oldBucket = 0;
	private int newBucket = 0;
	private int oldDpd = 0;
	private int newDpd = 0;
	private BigDecimal oldEmiOs = BigDecimal.ZERO;
	private BigDecimal newEmiOs = BigDecimal.ZERO;
	private int oldBalTenure = 0;
	private int newBalTenure = 0;
	private Date oldMaturity;
	private Date newMaturity;
	private Date lastBilledDate;
	private int lastBilledInstNo = 0;
	private BigDecimal actLoanAmount = BigDecimal.ZERO;
	private int oldTenure = 0;
	private int newTenure = 0;
	private BigDecimal oldInterest = BigDecimal.ZERO;
	private BigDecimal newInterest = BigDecimal.ZERO;
	private BigDecimal oldCpzInterest = BigDecimal.ZERO;
	private BigDecimal newCpzInterest = BigDecimal.ZERO;
	private int oldMaxUnplannedEmi = 0;
	private int newMaxUnplannedEmi = 0;
	private int oldAvailedUnplanEmi = 0;
	private int newAvailedUnplanEmi = 0;
	private BigDecimal oldFinalEmi = BigDecimal.ZERO;
	private BigDecimal newFinalEmi = BigDecimal.ZERO;
	private String lovValue;
	private RestructureDetail befImage;
	private LoggedInUser userDetails;
	private Date emiHldStartDate;
	private Date emiHldEndDate;
	private Date priHldStartDate;
	private Date priHldEndDate;
	private BigDecimal oldPOsAmount = BigDecimal.ZERO;
	private BigDecimal newPOsAmount = BigDecimal.ZERO;
	private BigDecimal oldEmiOverdue = BigDecimal.ZERO;
	private BigDecimal newEmiOverdue = BigDecimal.ZERO;
	private BigDecimal bounceCharge = BigDecimal.ZERO;
	private BigDecimal oldPenaltyAmount = BigDecimal.ZERO;
	private BigDecimal newPenaltyAmount = BigDecimal.ZERO;
	private BigDecimal otherCharge = BigDecimal.ZERO;
	private BigDecimal restructureCharge = BigDecimal.ZERO;
	private String rstTypeCode;
	private String rstTypeDesc;
	private BigDecimal finCurrAssetValue = BigDecimal.ZERO;
	private int oldExtOdDays = 0;
	private int newExtOdDays = 0;
	@XmlElement
	private BigDecimal repayProfitRate = BigDecimal.ZERO;
	private BigDecimal grcMaxAmount = BigDecimal.ZERO;
	@XmlElement
	private String baseRate;
	private String splRate;
	@XmlElement
	private BigDecimal margin = BigDecimal.ZERO;
	private long receiptID = 0;
	@XmlElement
	private List<RestructureCharge> chargeList = new ArrayList<>();
	@XmlElement
	private String reqType;
	@XmlElement
	private boolean alwManualSteps;
	@XmlElement
	private int noOfSteps;
	@XmlElement
	private String stepType;
	@XmlElement
	private String stepPolicy;
	@XmlElement
	private String calcOfSteps;
	@XmlElement
	private String stepsAppliedFor;
	@XmlElement
	private List<FinanceStepPolicyDetail> stepPolicyDetails = new ArrayList<>();

	public RestructureDetail copyEntity() {
		RestructureDetail entity = new RestructureDetail();
		entity.setId(this.id);
		entity.setFinID(this.finID);
		entity.setFinReference(this.finReference);
		entity.setRestructureDate(this.restructureDate);
		entity.setAppDate(this.appDate);
		entity.setEmiHldPeriod(this.emiHldPeriod);
		entity.setPriHldPeriod(this.priHldPeriod);
		entity.setEmiPeriods(this.emiPeriods);
		entity.setRestructureType(this.restructureType);
		entity.setRestructureReason(this.restructureReason);
		entity.setTenorChange(this.tenorChange);
		entity.setEmiRecal(this.emiRecal);
		entity.setTotNoOfRestructure(this.totNoOfRestructure);
		entity.setRecalculationType(this.recalculationType);
		entity.setServiceRequestNo(this.serviceRequestNo);
		entity.setRemark(this.remark);
		entity.setOldBucket(this.oldBucket);
		entity.setNewBucket(this.newBucket);
		entity.setOldDpd(this.oldDpd);
		entity.setNewDpd(this.newDpd);
		entity.setOldEmiOs(this.oldEmiOs);
		entity.setNewEmiOs(this.newEmiOs);
		entity.setOldBalTenure(this.oldBalTenure);
		entity.setNewBalTenure(this.newBalTenure);
		entity.setOldMaturity(this.oldMaturity);
		entity.setNewMaturity(this.newMaturity);
		entity.setLastBilledDate(this.lastBilledDate);
		entity.setLastBilledInstNo(this.lastBilledInstNo);
		entity.setActLoanAmount(this.actLoanAmount);
		entity.setOldTenure(this.oldTenure);
		entity.setNewTenure(this.newTenure);
		entity.setOldInterest(this.oldInterest);
		entity.setNewInterest(this.newInterest);
		entity.setOldCpzInterest(this.oldCpzInterest);
		entity.setNewCpzInterest(this.newCpzInterest);
		entity.setOldMaxUnplannedEmi(this.oldMaxUnplannedEmi);
		entity.setNewMaxUnplannedEmi(this.newMaxUnplannedEmi);
		entity.setOldAvailedUnplanEmi(this.oldAvailedUnplanEmi);
		entity.setNewAvailedUnplanEmi(this.newAvailedUnplanEmi);
		entity.setOldFinalEmi(this.oldFinalEmi);
		entity.setNewFinalEmi(this.newFinalEmi);
		entity.setLovValue(this.lovValue);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setEmiHldStartDate(this.emiHldStartDate);
		entity.setEmiHldEndDate(this.emiHldEndDate);
		entity.setPriHldStartDate(this.priHldStartDate);
		entity.setPriHldEndDate(this.priHldEndDate);
		entity.setOldPOsAmount(this.oldPOsAmount);
		entity.setNewPOsAmount(this.newPOsAmount);
		entity.setOldEmiOverdue(this.oldEmiOverdue);
		entity.setNewEmiOverdue(this.newEmiOverdue);
		entity.setBounceCharge(this.bounceCharge);
		entity.setOldPenaltyAmount(this.oldPenaltyAmount);
		entity.setNewPenaltyAmount(this.newPenaltyAmount);
		entity.setOtherCharge(this.otherCharge);
		entity.setRestructureCharge(this.restructureCharge);
		entity.setRstTypeCode(this.rstTypeCode);
		entity.setRstTypeDesc(this.rstTypeDesc);
		entity.setFinCurrAssetValue(this.finCurrAssetValue);
		entity.setOldExtOdDays(this.oldExtOdDays);
		entity.setNewExtOdDays(this.newExtOdDays);
		entity.setRepayProfitRate(this.repayProfitRate);
		entity.setGrcMaxAmount(this.grcMaxAmount);
		entity.setBaseRate(this.baseRate);
		entity.setSplRate(this.splRate);
		entity.setMargin(this.margin);
		entity.setReceiptID(this.receiptID);
		this.chargeList.stream().forEach(e -> entity.getChargeList().add(e == null ? null : e.copyEntity()));
		entity.setReqType(this.reqType);
		entity.setAlwManualSteps(this.alwManualSteps);
		entity.setNoOfSteps(this.noOfSteps);
		entity.setStepType(this.stepType);
		entity.setStepPolicy(this.stepPolicy);
		entity.setCalcOfSteps(this.calcOfSteps);
		entity.setStepsAppliedFor(this.stepsAppliedFor);
		this.stepPolicyDetails.stream()
				.forEach(e -> entity.getStepPolicyDetails().add(e == null ? null : e.copyEntity()));
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("rstTypeCode");
		excludeFields.add("rstTypeDesc");
		excludeFields.add("reqType");
		excludeFields.add("alwManualSteps");
		excludeFields.add("noOfSteps");
		excludeFields.add("stepType");
		excludeFields.add("stepPolicy");
		excludeFields.add("noOfGrcSteps");
		excludeFields.add("calcOfSteps");
		excludeFields.add("stepsAppliedFor");
		excludeFields.add("stepPolicyDetails");
		return excludeFields;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRestructureType() {
		return restructureType;
	}

	public void setRestructureType(String restructureType) {
		this.restructureType = restructureType;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public RestructureDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(RestructureDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getTotNoOfRestructure() {
		return totNoOfRestructure;
	}

	public void setTotNoOfRestructure(int totNoOfRestructure) {
		this.totNoOfRestructure = totNoOfRestructure;
	}

	public String getRecalculationType() {
		return recalculationType;
	}

	public void setRecalculationType(String recalculationType) {
		this.recalculationType = recalculationType;
	}

	public String getServiceRequestNo() {
		return serviceRequestNo;
	}

	public void setServiceRequestNo(String serviceRequestNo) {
		this.serviceRequestNo = serviceRequestNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getOldBucket() {
		return oldBucket;
	}

	public void setOldBucket(int oldBucket) {
		this.oldBucket = oldBucket;
	}

	public int getNewBucket() {
		return newBucket;
	}

	public void setNewBucket(int newBucket) {
		this.newBucket = newBucket;
	}

	public BigDecimal getOldEmiOs() {
		return oldEmiOs;
	}

	public void setOldEmiOs(BigDecimal oldEmiOs) {
		this.oldEmiOs = oldEmiOs;
	}

	public BigDecimal getNewEmiOs() {
		return newEmiOs;
	}

	public void setNewEmiOs(BigDecimal newEmiOs) {
		this.newEmiOs = newEmiOs;
	}

	public int getOldBalTenure() {
		return oldBalTenure;
	}

	public void setOldBalTenure(int oldBalTenure) {
		this.oldBalTenure = oldBalTenure;
	}

	public int getNewBalTenure() {
		return newBalTenure;
	}

	public void setNewBalTenure(int newBalTenure) {
		this.newBalTenure = newBalTenure;
	}

	public Date getOldMaturity() {
		return oldMaturity;
	}

	public void setOldMaturity(Date oldMaturity) {
		this.oldMaturity = oldMaturity;
	}

	public Date getNewMaturity() {
		return newMaturity;
	}

	public void setNewMaturity(Date newMaturity) {
		this.newMaturity = newMaturity;
	}

	public Date getLastBilledDate() {
		return lastBilledDate;
	}

	public void setLastBilledDate(Date lastBilledDate) {
		this.lastBilledDate = lastBilledDate;
	}

	public int getLastBilledInstNo() {
		return lastBilledInstNo;
	}

	public void setLastBilledInstNo(int lastBilledInstNo) {
		this.lastBilledInstNo = lastBilledInstNo;
	}

	public BigDecimal getActLoanAmount() {
		return actLoanAmount;
	}

	public void setActLoanAmount(BigDecimal actLoanAmount) {
		this.actLoanAmount = actLoanAmount;
	}

	public int getOldTenure() {
		return oldTenure;
	}

	public void setOldTenure(int oldTenure) {
		this.oldTenure = oldTenure;
	}

	public int getNewTenure() {
		return newTenure;
	}

	public void setNewTenure(int newTenure) {
		this.newTenure = newTenure;
	}

	public BigDecimal getOldInterest() {
		return oldInterest;
	}

	public void setOldInterest(BigDecimal oldInterest) {
		this.oldInterest = oldInterest;
	}

	public BigDecimal getNewInterest() {
		return newInterest;
	}

	public void setNewInterest(BigDecimal newInterest) {
		this.newInterest = newInterest;
	}

	public int getOldMaxUnplannedEmi() {
		return oldMaxUnplannedEmi;
	}

	public void setOldMaxUnplannedEmi(int oldMaxUnplannedEmi) {
		this.oldMaxUnplannedEmi = oldMaxUnplannedEmi;
	}

	public int getNewMaxUnplannedEmi() {
		return newMaxUnplannedEmi;
	}

	public void setNewMaxUnplannedEmi(int newMaxUnplannedEmi) {
		this.newMaxUnplannedEmi = newMaxUnplannedEmi;
	}

	public int getOldAvailedUnplanEmi() {
		return oldAvailedUnplanEmi;
	}

	public void setOldAvailedUnplanEmi(int oldAvailedUnplanEmi) {
		this.oldAvailedUnplanEmi = oldAvailedUnplanEmi;
	}

	public int getNewAvailedUnplanEmi() {
		return newAvailedUnplanEmi;
	}

	public void setNewAvailedUnplanEmi(int newAvailedUnplanEmi) {
		this.newAvailedUnplanEmi = newAvailedUnplanEmi;
	}

	public BigDecimal getOldFinalEmi() {
		return oldFinalEmi;
	}

	public void setOldFinalEmi(BigDecimal oldFinalEmi) {
		this.oldFinalEmi = oldFinalEmi;
	}

	public BigDecimal getNewFinalEmi() {
		return newFinalEmi;
	}

	public void setNewFinalEmi(BigDecimal newFinalEmi) {
		this.newFinalEmi = newFinalEmi;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getRestructureDate() {
		return restructureDate;
	}

	public void setRestructureDate(Date restructureDate) {
		this.restructureDate = restructureDate;
	}

	public boolean isTenorChange() {
		return tenorChange;
	}

	public void setTenorChange(boolean tenorChange) {
		this.tenorChange = tenorChange;
	}

	public int getEmiHldPeriod() {
		return emiHldPeriod;
	}

	public void setEmiHldPeriod(int emiHldPeriod) {
		this.emiHldPeriod = emiHldPeriod;
	}

	public int getPriHldPeriod() {
		return priHldPeriod;
	}

	public void setPriHldPeriod(int priHldPeriod) {
		this.priHldPeriod = priHldPeriod;
	}

	public int getEmiPeriods() {
		return emiPeriods;
	}

	public void setEmiPeriods(int emiPeriods) {
		this.emiPeriods = emiPeriods;
	}

	public boolean isEmiRecal() {
		return emiRecal;
	}

	public void setEmiRecal(boolean emiRecal) {
		this.emiRecal = emiRecal;
	}

	public String getRestructureReason() {
		return restructureReason;
	}

	public void setRestructureReason(String restructureReason) {
		this.restructureReason = restructureReason;
	}

	public int getOldDpd() {
		return oldDpd;
	}

	public void setOldDpd(int oldDpd) {
		this.oldDpd = oldDpd;
	}

	public int getNewDpd() {
		return newDpd;
	}

	public void setNewDpd(int newDpd) {
		this.newDpd = newDpd;
	}

	public BigDecimal getOldCpzInterest() {
		return oldCpzInterest;
	}

	public void setOldCpzInterest(BigDecimal oldCpzInterest) {
		this.oldCpzInterest = oldCpzInterest;
	}

	public BigDecimal getNewCpzInterest() {
		return newCpzInterest;
	}

	public void setNewCpzInterest(BigDecimal newCpzInterest) {
		this.newCpzInterest = newCpzInterest;
	}

	public Date getEmiHldStartDate() {
		return emiHldStartDate;
	}

	public void setEmiHldStartDate(Date emiHldStartDate) {
		this.emiHldStartDate = emiHldStartDate;
	}

	public Date getEmiHldEndDate() {
		return emiHldEndDate;
	}

	public void setEmiHldEndDate(Date emiHldEndDate) {
		this.emiHldEndDate = emiHldEndDate;
	}

	public Date getPriHldStartDate() {
		return priHldStartDate;
	}

	public void setPriHldStartDate(Date priHldStartDate) {
		this.priHldStartDate = priHldStartDate;
	}

	public Date getPriHldEndDate() {
		return priHldEndDate;
	}

	public void setPriHldEndDate(Date priHldEndDate) {
		this.priHldEndDate = priHldEndDate;
	}

	public BigDecimal getOldPOsAmount() {
		return oldPOsAmount;
	}

	public void setOldPOsAmount(BigDecimal oldPOsAmount) {
		this.oldPOsAmount = oldPOsAmount;
	}

	public BigDecimal getNewPOsAmount() {
		return newPOsAmount;
	}

	public void setNewPOsAmount(BigDecimal newPOsAmount) {
		this.newPOsAmount = newPOsAmount;
	}

	public BigDecimal getOldEmiOverdue() {
		return oldEmiOverdue;
	}

	public void setOldEmiOverdue(BigDecimal oldEmiOverdue) {
		this.oldEmiOverdue = oldEmiOverdue;
	}

	public BigDecimal getNewEmiOverdue() {
		return newEmiOverdue;
	}

	public void setNewEmiOverdue(BigDecimal newEmiOverdue) {
		this.newEmiOverdue = newEmiOverdue;
	}

	public BigDecimal getBounceCharge() {
		return bounceCharge;
	}

	public void setBounceCharge(BigDecimal bounceCharge) {
		this.bounceCharge = bounceCharge;
	}

	public BigDecimal getOldPenaltyAmount() {
		return oldPenaltyAmount;
	}

	public void setOldPenaltyAmount(BigDecimal oldPenaltyAmount) {
		this.oldPenaltyAmount = oldPenaltyAmount;
	}

	public BigDecimal getNewPenaltyAmount() {
		return newPenaltyAmount;
	}

	public void setNewPenaltyAmount(BigDecimal newPenaltyAmount) {
		this.newPenaltyAmount = newPenaltyAmount;
	}

	public BigDecimal getOtherCharge() {
		return otherCharge;
	}

	public void setOtherCharge(BigDecimal otherCharge) {
		this.otherCharge = otherCharge;
	}

	public BigDecimal getRestructureCharge() {
		return restructureCharge;
	}

	public void setRestructureCharge(BigDecimal restructureCharge) {
		this.restructureCharge = restructureCharge;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public String getRstTypeCode() {
		return rstTypeCode;
	}

	public void setRstTypeCode(String rstTypeCode) {
		this.rstTypeCode = rstTypeCode;
	}

	public String getRstTypeDesc() {
		return rstTypeDesc;
	}

	public void setRstTypeDesc(String rstTypeDesc) {
		this.rstTypeDesc = rstTypeDesc;
	}

	public BigDecimal getFinCurrAssetValue() {
		return finCurrAssetValue;
	}

	public void setFinCurrAssetValue(BigDecimal finCurrAssetValue) {
		this.finCurrAssetValue = finCurrAssetValue;
	}

	public int getOldExtOdDays() {
		return oldExtOdDays;
	}

	public void setOldExtOdDays(int oldExtOdDays) {
		this.oldExtOdDays = oldExtOdDays;
	}

	public int getNewExtOdDays() {
		return newExtOdDays;
	}

	public void setNewExtOdDays(int newExtOdDays) {
		this.newExtOdDays = newExtOdDays;
	}

	public BigDecimal getRepayProfitRate() {
		return repayProfitRate;
	}

	public void setRepayProfitRate(BigDecimal repayProfitRate) {
		this.repayProfitRate = repayProfitRate;
	}

	public BigDecimal getGrcMaxAmount() {
		return grcMaxAmount;
	}

	public void setGrcMaxAmount(BigDecimal grcMaxAmount) {
		this.grcMaxAmount = grcMaxAmount;
	}

	public String getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(String baseRate) {
		this.baseRate = baseRate;
	}

	public String getSplRate() {
		return splRate;
	}

	public void setSplRate(String splRate) {
		this.splRate = splRate;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public List<RestructureCharge> getChargeList() {
		return chargeList;
	}

	public void setChargeList(List<RestructureCharge> chargeList) {
		this.chargeList = chargeList;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public boolean isAlwManualSteps() {
		return alwManualSteps;
	}

	public void setAlwManualSteps(boolean alwManualSteps) {
		this.alwManualSteps = alwManualSteps;
	}

	public int getNoOfSteps() {
		return noOfSteps;
	}

	public void setNoOfSteps(int noOfSteps) {
		this.noOfSteps = noOfSteps;
	}

	public String getStepType() {
		return stepType;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public String getStepPolicy() {
		return stepPolicy;
	}

	public void setStepPolicy(String stepPolicy) {
		this.stepPolicy = stepPolicy;
	}

	public String getCalcOfSteps() {
		return calcOfSteps;
	}

	public void setCalcOfSteps(String calcOfSteps) {
		this.calcOfSteps = calcOfSteps;
	}

	public String getStepsAppliedFor() {
		return stepsAppliedFor;
	}

	public void setStepsAppliedFor(String stepsAppliedFor) {
		this.stepsAppliedFor = stepsAppliedFor;
	}

	public List<FinanceStepPolicyDetail> getStepPolicyDetails() {
		return stepPolicyDetails;
	}

	public void setStepPolicyDetails(List<FinanceStepPolicyDetail> stepPolicyDetails) {
		this.stepPolicyDetails = stepPolicyDetails;
	}

}