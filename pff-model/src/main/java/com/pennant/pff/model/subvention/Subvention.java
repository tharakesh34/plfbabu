package com.pennant.pff.model.subvention;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennanttech.pennapps.core.model.ErrorDetail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Subvention implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	@XmlElement
	private String finReference;
	private Long batchId;
	@XmlElement
	private String referenceCode;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String finType;
	@XmlElement
	private String customerName;
	@XmlElement
	private Date postDate;
	@XmlElement
	private Date valueDate;
	@XmlElement
	private String transref;
	@XmlElement
	private Long partnerBankId;
	@XmlElement
	private String partnerAccNo;
	private Long linkedTranId;
	private String remarks;
	private String status;
	private FinanceMain fm;
	private FinFeeDetail subvensionFee;
	private FinFeeDetail processingFee;
	private PartnerBank partnerBank;
	private BigDecimal cgstAmt = BigDecimal.ZERO;
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	private BigDecimal ugstAmt = BigDecimal.ZERO;
	private BigDecimal igstAmt = BigDecimal.ZERO;
	private BigDecimal cessAmt = BigDecimal.ZERO;
	private BigDecimal procFeeAmt = BigDecimal.ZERO;
	private List<ErrorDetail> errors = new ArrayList<>();

	public Subvention() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getTransref() {
		return transref;
	}

	public void setTransref(String transref) {
		this.transref = transref;
	}

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}

	public String getPartnerAccNo() {
		return partnerAccNo;
	}

	public void setPartnerAccNo(String partnerAccNo) {
		this.partnerAccNo = partnerAccNo;
	}

	public Long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(Long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FinanceMain getFinanceMain() {
		return fm;
	}

	public void setFinanceMain(FinanceMain fm) {
		this.fm = fm;
	}

	public FinFeeDetail getSubvensionFee() {
		return subvensionFee;
	}

	public void setSubvensionFee(FinFeeDetail fee) {
		this.subvensionFee = fee;
	}

	public FinFeeDetail getProcessingFee() {
		return processingFee;
	}

	public void setProcessingFee(FinFeeDetail processingFee) {
		this.processingFee = processingFee;
	}

	public PartnerBank getPartnerBank() {
		return partnerBank;
	}

	public void setPartnerBank(PartnerBank partnerBank) {
		this.partnerBank = partnerBank;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errors;
	}

	public void setErrorDetail(ErrorDetail errorDetails) {
		if (errorDetails == null) {
			return;
		}
		errors.add(errorDetails);
	}

	public void setErrorDetail(long errorCode) {
		setErrorDetail(new ErrorDetail("Key", String.valueOf(errorCode), null, null));
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public BigDecimal getUgstAmt() {
		return ugstAmt;
	}

	public void setUgstAmt(BigDecimal ugstAmt) {
		this.ugstAmt = ugstAmt;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public BigDecimal getProcFeeAmt() {
		return procFeeAmt;
	}

	public void setProcFeeAmt(BigDecimal procFeeAmt) {
		this.procFeeAmt = procFeeAmt;
	}

}
