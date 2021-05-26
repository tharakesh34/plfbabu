package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class InstBasedSchdDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6234931333270161797L;
	private long id;
	private long batchId;
	private String finReference;
	private long disbId;
	private Date realizedDate;
	private String status;
	private String errorDesc;
	private long userId;
	private String paymentType;
	private BigDecimal disbAmount;
	private Date downloadedOn;
	private long linkedTranId;

	public InstBasedSchdDetails() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getDisbId() {
		return disbId;
	}

	public void setDisbId(long disbId) {
		this.disbId = disbId;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRealizedDate() {
		return realizedDate;
	}

	public void setRealizedDate(Date realizedDate) {
		this.realizedDate = realizedDate;
	}

	public Date getDownloadedOn() {
		return downloadedOn;
	}

	public void setDownloadedOn(Date downloadedOn) {
		this.downloadedOn = downloadedOn;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

}
