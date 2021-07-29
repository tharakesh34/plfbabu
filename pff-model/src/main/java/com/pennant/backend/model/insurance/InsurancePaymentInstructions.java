package com.pennant.backend.model.insurance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class InsurancePaymentInstructions extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private long id = Long.MIN_VALUE;
	private String entityCode;
	private long providerId;
	private BigDecimal paymentAmount;
	private BigDecimal payableAmount;
	private BigDecimal receivableAmount;
	private BigDecimal partnerPremiumAmt;
	private long linkedTranId;
	private long dataEngineStatusId;
	private int noOfInsurances;
	private int noOfPayments;
	private int noOfReceivables;
	private boolean adjustedReceivable;
	private long partnerBankId;
	private Date paymentDate;
	private String paymentType;
	private String transactionRef;
	private String rejectReason;
	private String remarks;
	private Date realizationDate;
	private Date respDate;
	private Date approvedDate;
	private String paymentCCy;
	private String status;
	private Map<Long, String> adviseRefMap = new LinkedHashMap<>();
	private List<VASRecording> vasRecordindList = new ArrayList<>();
	private InsurancePaymentInstructions befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private String finReference;
	private String vasReference;

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> detailsMap) {
		detailsMap.put("id_payAmount", getPayableAmount());// Total payable amount
		detailsMap.put("id_recAmount", getReceivableAmount());// Total receivable amount
		detailsMap.put("id_totPayAmount", getPaymentAmount());// Total payment amount to the partner after adjusting the
																// receivables
		detailsMap.put("id_partnerPremiumAmt", getPartnerPremiumAmt());// Total payment amount to the partner after
																		// adjusting the receivables
		return detailsMap;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("finReference");
		excludeFields.add("vasReference");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public long getProviderId() {
		return providerId;
	}

	public void setProviderId(long providerId) {
		this.providerId = providerId;
	}

	public long getDataEngineStatusId() {
		return dataEngineStatusId;
	}

	public void setDataEngineStatusId(long dataEngineStatusId) {
		this.dataEngineStatusId = dataEngineStatusId;
	}

	public int getNoOfInsurances() {
		return noOfInsurances;
	}

	public void setNoOfInsurances(int noOfInsurances) {
		this.noOfInsurances = noOfInsurances;
	}

	public int getNoOfPayments() {
		return noOfPayments;
	}

	public void setNoOfPayments(int noOfPayments) {
		this.noOfPayments = noOfPayments;
	}

	public int getNoOfReceivables() {
		return noOfReceivables;
	}

	public void setNoOfReceivables(int noOfReceivables) {
		this.noOfReceivables = noOfReceivables;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public InsurancePaymentInstructions getBefImage() {
		return befImage;
	}

	public void setBefImage(InsurancePaymentInstructions befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getReceivableAmount() {
		return receivableAmount;
	}

	public void setReceivableAmount(BigDecimal receivableAmount) {
		this.receivableAmount = receivableAmount;
	}

	public boolean isAdjustedReceivable() {
		return adjustedReceivable;
	}

	public void setAdjustedReceivable(boolean adjustedReceivable) {
		this.adjustedReceivable = adjustedReceivable;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getRealizationDate() {
		return realizationDate;
	}

	public void setRealizationDate(Date realizationDate) {
		this.realizationDate = realizationDate;
	}

	public Date getRespDate() {
		return respDate;
	}

	public void setRespDate(Date respDate) {
		this.respDate = respDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getPaymentCCy() {
		return paymentCCy;
	}

	public void setPaymentCCy(String paymentCCy) {
		this.paymentCCy = paymentCCy;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public Map<Long, String> getAdviseRefMap() {
		return adviseRefMap;
	}

	public void setAdviseRefMap(Map<Long, String> adviseRefMap) {
		this.adviseRefMap = adviseRefMap;
	}

	public List<VASRecording> getVasRecordindList() {
		return vasRecordindList;
	}

	public void setVasRecordindList(List<VASRecording> vasRecordindList) {
		this.vasRecordindList = vasRecordindList;
	}

	public BigDecimal getPartnerPremiumAmt() {
		return partnerPremiumAmt;
	}

	public void setPartnerPremiumAmt(BigDecimal partnerPremiumAmt) {
		this.partnerPremiumAmt = partnerPremiumAmt;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

}
