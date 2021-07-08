package com.pennant.backend.model.insurance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class InsuranceDetails extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long batchId = Long.MIN_VALUE;
	private long vASProviderId;
	private String reference;
	private String finReference;
	private long linkedTranId;
	private Date startDate;
	private Date endDate;
	private int term = 0;
	private BigDecimal coverageAmount = BigDecimal.ZERO;
	private String policyNumber;
	private Date issuanceDate;
	private String issuanceStatus;
	private BigDecimal partnerPremium = BigDecimal.ZERO;
	private Date partnerReceivedDate;
	private String aWBNo1;
	private String aWBNo2;
	private String aWBNo3;
	private String dispatchStatus1;
	private String dispatchStatus2;
	private String dispatchStatus3;
	private String reasonOfRTO1;
	private String reasonOfRTO2;
	private String reasonOfRTO3;
	private Date dispatchDateAttempt1;
	private Date dispatchDateAttempt2;
	private Date dispatchDateAttempt3;
	private boolean medicalStatus;
	private String pendencyReasonCategory;
	private String pendencyReason;
	private boolean insPendencyResReq;
	private String fPR;
	private String policyStatus;
	private Date formHandoverDate;
	private String nomineeName;
	private String nomineeRelation;
	private String reconStatus;
	private int updateCount = 0;
	private boolean newRecord;
	private String lovValue;
	private InsuranceDetails befImage;
	private BigDecimal tolaranceAmount = BigDecimal.ZERO;
	@XmlTransient
	private LoggedInUser userDetails;

	private int freeLockPeriod;
	// For Enquire
	private String productCtg;
	private String vasProviderDesc;
	private String paymentMode;

	// Manual Reconciliation
	private String manualReconRemarks;
	private String manualReconResCategory;
	private String custCIF;
	private String custShrtName;
	private String finType;
	private String finTypeName;
	private int flpDays;
	private String postingAgainst;
	private BigDecimal insurancePremium = BigDecimal.ZERO;

	// File Related Fields//
	private String dispatchStatusF;
	private String termF;
	private String pendencyResReqF;
	private String aWBNoF;
	private String reasonOfRTOF;
	private String medicalStatusF;
	private String companyName;
	private BigDecimal payableAmount = BigDecimal.ZERO;

	//Accounting
	private BigDecimal adjAmount = BigDecimal.ZERO;
	private String entityCode;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");
		excludeFields.add("finType");
		excludeFields.add("finTypeName");
		excludeFields.add("flpDays");
		excludeFields.add("postingAgainst");
		excludeFields.add("pendencyResReqF");
		excludeFields.add("dispatchStatusF");
		excludeFields.add("aWBNoF");
		excludeFields.add("reasonOfRTOF");
		excludeFields.add("medicalStatusF");
		excludeFields.add("insurancePremium");
		excludeFields.add("updateCount");
		excludeFields.add("companyName");
		excludeFields.add("payableAmount");
		excludeFields.add("adjAmount");
		excludeFields.add("entityCode");
		excludeFields.add("freeLockPeriod");
		excludeFields.add("vasProviderDesc");
		excludeFields.add("paymentMode");
		excludeFields.add("productCtg");
		excludeFields.add("batchId");
		excludeFields.add("termF");
		return excludeFields;
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> detailsMap) {
		detailsMap.put("id_adjAmount", getAdjAmount());
		return detailsMap;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public InsuranceDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(InsuranceDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public BigDecimal getCoverageAmount() {
		return coverageAmount;
	}

	public void setCoverageAmount(BigDecimal coverageAmount) {
		this.coverageAmount = coverageAmount;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public String getIssuanceStatus() {
		return issuanceStatus;
	}

	public void setIssuanceStatus(String issuanceStatus) {
		this.issuanceStatus = issuanceStatus;
	}

	public BigDecimal getPartnerPremium() {
		return partnerPremium;
	}

	public void setPartnerPremium(BigDecimal partnerPremium) {
		this.partnerPremium = partnerPremium;
	}

	public Date getPartnerReceivedDate() {
		return partnerReceivedDate;
	}

	public void setPartnerReceivedDate(Date partnerReceivedDate) {
		this.partnerReceivedDate = partnerReceivedDate;
	}

	public String getaWBNo1() {
		return aWBNo1;
	}

	public void setaWBNo1(String aWBNo1) {
		this.aWBNo1 = aWBNo1;
	}

	public String getaWBNo2() {
		return aWBNo2;
	}

	public void setaWBNo2(String aWBNo2) {
		this.aWBNo2 = aWBNo2;
	}

	public String getaWBNo3() {
		return aWBNo3;
	}

	public void setaWBNo3(String aWBNo3) {
		this.aWBNo3 = aWBNo3;
	}

	public String getDispatchStatus1() {
		return dispatchStatus1;
	}

	public void setDispatchStatus1(String dispatchStatus1) {
		this.dispatchStatus1 = dispatchStatus1;
	}

	public String getDispatchStatus2() {
		return dispatchStatus2;
	}

	public void setDispatchStatus2(String dispatchStatus2) {
		this.dispatchStatus2 = dispatchStatus2;
	}

	public String getDispatchStatus3() {
		return dispatchStatus3;
	}

	public void setDispatchStatus3(String dispatchStatus3) {
		this.dispatchStatus3 = dispatchStatus3;
	}

	public String getReasonOfRTO1() {
		return reasonOfRTO1;
	}

	public void setReasonOfRTO1(String reasonOfRTO1) {
		this.reasonOfRTO1 = reasonOfRTO1;
	}

	public String getReasonOfRTO2() {
		return reasonOfRTO2;
	}

	public void setReasonOfRTO2(String reasonOfRTO2) {
		this.reasonOfRTO2 = reasonOfRTO2;
	}

	public String getReasonOfRTO3() {
		return reasonOfRTO3;
	}

	public void setReasonOfRTO3(String reasonOfRTO3) {
		this.reasonOfRTO3 = reasonOfRTO3;
	}

	public Date getDispatchDateAttempt1() {
		return dispatchDateAttempt1;
	}

	public void setDispatchDateAttempt1(Date dispatchDateAttempt1) {
		this.dispatchDateAttempt1 = dispatchDateAttempt1;
	}

	public Date getDispatchDateAttempt2() {
		return dispatchDateAttempt2;
	}

	public void setDispatchDateAttempt2(Date dispatchDateAttempt2) {
		this.dispatchDateAttempt2 = dispatchDateAttempt2;
	}

	public Date getDispatchDateAttempt3() {
		return dispatchDateAttempt3;
	}

	public void setDispatchDateAttempt3(Date dispatchDateAttempt3) {
		this.dispatchDateAttempt3 = dispatchDateAttempt3;
	}

	public boolean isMedicalStatus() {
		return medicalStatus;
	}

	public void setMedicalStatus(boolean medicalStatus) {
		this.medicalStatus = medicalStatus;
	}

	public String getPendencyReasonCategory() {
		return pendencyReasonCategory;
	}

	public void setPendencyReasonCategory(String pendencyReasonCategory) {
		this.pendencyReasonCategory = pendencyReasonCategory;
	}

	public String getPendencyReason() {
		return pendencyReason;
	}

	public void setPendencyReason(String pendencyReason) {
		this.pendencyReason = pendencyReason;
	}

	public boolean isInsPendencyResReq() {
		return insPendencyResReq;
	}

	public void setInsPendencyResReq(boolean insPendencyResReq) {
		this.insPendencyResReq = insPendencyResReq;
	}

	public String getfPR() {
		return fPR;
	}

	public void setfPR(String fPR) {
		this.fPR = fPR;
	}

	public String getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}

	public Date getFormHandoverDate() {
		return formHandoverDate;
	}

	public void setFormHandoverDate(Date formHandoverDate) {
		this.formHandoverDate = formHandoverDate;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeRelation() {
		return nomineeRelation;
	}

	public void setNomineeRelation(String nomineeRelation) {
		this.nomineeRelation = nomineeRelation;
	}

	public String getReconStatus() {
		return reconStatus;
	}

	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public String getDispatchStatusF() {
		return dispatchStatusF;
	}

	public void setDispatchStatusF(String dispatchStatusF) {
		this.dispatchStatusF = dispatchStatusF;
	}

	public String getPendencyResReqF() {
		return pendencyResReqF;
	}

	public void setPendencyResReqF(String pendencyResReqF) {
		this.pendencyResReqF = pendencyResReqF;
	}

	public String getaWBNoF() {
		return aWBNoF;
	}

	public void setaWBNoF(String aWBNoF) {
		this.aWBNoF = aWBNoF;
	}

	public String getReasonOfRTOF() {
		return reasonOfRTOF;
	}

	public void setReasonOfRTOF(String reasonOfRTOF) {
		this.reasonOfRTOF = reasonOfRTOF;
	}

	public String getMedicalStatusF() {
		return medicalStatusF;
	}

	public void setMedicalStatusF(String medicalStatusF) {
		this.medicalStatusF = medicalStatusF;
	}

	public long getvASProviderId() {
		return vASProviderId;
	}

	public void setvASProviderId(long vASProviderId) {
		this.vASProviderId = vASProviderId;
	}

	public String getManualReconRemarks() {
		return manualReconRemarks;
	}

	public void setManualReconRemarks(String manualReconRemarks) {
		this.manualReconRemarks = manualReconRemarks;
	}

	public String getManualReconResCategory() {
		return manualReconResCategory;
	}

	public void setManualReconResCategory(String manualReconResCategory) {
		this.manualReconResCategory = manualReconResCategory;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeName() {
		return finTypeName;
	}

	public void setFinTypeName(String finTypeName) {
		this.finTypeName = finTypeName;
	}

	public int getFlpDays() {
		return flpDays;
	}

	public void setFlpDays(int flpDays) {
		this.flpDays = flpDays;
	}

	public String getPostingAgainst() {
		return postingAgainst;
	}

	public void setPostingAgainst(String postingAgainst) {
		this.postingAgainst = postingAgainst;
	}

	public BigDecimal getInsurancePremium() {
		return insurancePremium;
	}

	public void setInsurancePremium(BigDecimal insurancePremium) {
		this.insurancePremium = insurancePremium;
	}

	public BigDecimal getTolaranceAmount() {
		return tolaranceAmount;
	}

	public void setTolaranceAmount(BigDecimal tolaranceAmount) {
		this.tolaranceAmount = tolaranceAmount;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public BigDecimal getAdjAmount() {
		return adjAmount;
	}

	public void setAdjAmount(BigDecimal adjAmount) {
		this.adjAmount = adjAmount;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public int getFreeLockPeriod() {
		return freeLockPeriod;
	}

	public void setFreeLockPeriod(int freeLockPeriod) {
		this.freeLockPeriod = freeLockPeriod;
	}

	public String getVasProviderDesc() {
		return vasProviderDesc;
	}

	public void setVasProviderDesc(String vasProviderDesc) {
		this.vasProviderDesc = vasProviderDesc;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getProductCtg() {
		return productCtg;
	}

	public void setProductCtg(String productCtg) {
		this.productCtg = productCtg;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getTermF() {
		return termF;
	}

	public void setTermF(String termF) {
		this.termF = termF;
	}

}
