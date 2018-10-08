package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
@XmlAccessorType(XmlAccessType.NONE)
public class OverDraftMaintenance extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 2803331023129230226L;

	public OverDraftMaintenance() {
		super();
	}
	
	public boolean isNew() {
		return isNewRecord();
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	private long  serviceSeqId =  Long.MIN_VALUE;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal oDLimit = BigDecimal.ZERO;
	@XmlElement
	private Date valueDate;
	@XmlElement
	private int tenor = 0;
	@XmlElement
	private Date maturityDate;
	@XmlElement
	private String droplineFrq;
	@XmlElement
	private Date droplineDate;
	@XmlElement
	private String repayFrq;
	@XmlElement
	private String rateReviewFrq;
	@XmlElement
	private String 	serviceReqNo;
	@XmlElement
	private String	remarks;
	
	private boolean newRecord;

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@Override
	public long getId() {
		return serviceSeqId;
	}
	
	@Override
	public void setId(long id) {
		this.serviceSeqId = id;
	}

	public long getServiceSeqId() {
		return serviceSeqId;
	}
	public void setServiceSeqId(long serviceSeqId) {
		this.serviceSeqId = serviceSeqId;
	}
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public BigDecimal getoDLimit() {
		return oDLimit;
	}
	public void setoDLimit(BigDecimal oDLimit) {
		this.oDLimit = oDLimit;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	public int getTenor() {
		return tenor;
	}
	public void setTenor(int tenor) {
		this.tenor = tenor;
	}
	public Date getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	public String getDroplineFrq() {
		return droplineFrq;
	}
	public void setDroplineFrq(String droplineFrq) {
		this.droplineFrq = droplineFrq;
	}
	public Date getDroplineDate() {
		return droplineDate;
	}
	public void setDroplineDate(Date droplineDate) {
		this.droplineDate = droplineDate;
	}
	public String getRepayFrq() {
		return repayFrq;
	}
	public void setRepayFrq(String repayFrq) {
		this.repayFrq = repayFrq;
	}
	public String getRateReviewFrq() {
		return rateReviewFrq;
	}
	public void setRateReviewFrq(String rateReviewFrq) {
		this.rateReviewFrq = rateReviewFrq;
	}
	public String getServiceReqNo() {
		return serviceReqNo;
	}
	public void setServiceReqNo(String serviceReqNo) {
		this.serviceReqNo = serviceReqNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
