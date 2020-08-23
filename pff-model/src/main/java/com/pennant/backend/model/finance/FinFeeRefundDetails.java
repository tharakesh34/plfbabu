package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinFeeRefundDetails extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long headerId;
	private long feeId;
	//With GST
	private BigDecimal refundAmount = BigDecimal.ZERO;
	//GST
	private BigDecimal refundAmtGST = BigDecimal.ZERO;
	//Without GST
	private BigDecimal refundAmtOriginal = BigDecimal.ZERO;

	private String feeTypeCode;
	private boolean taxApplicable;
	private String taxComponent;
	private String finEvent;

	private boolean newRecord = false;
	private FinFeeRefundDetails befImage;
	private LoggedInUser userDetails;

	public FinFeeRefundDetails() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeTypeCode");
		excludeFields.add("taxApplicable");
		excludeFields.add("taxComponent");
		excludeFields.add("finEvent");
		return excludeFields;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public long getFeeId() {
		return feeId;
	}

	public void setFeeId(long feeId) {
		this.feeId = feeId;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public BigDecimal getRefundAmtGST() {
		return refundAmtGST;
	}

	public void setRefundAmtGST(BigDecimal refundAmtGST) {
		this.refundAmtGST = refundAmtGST;
	}

	public BigDecimal getRefundAmtOriginal() {
		return refundAmtOriginal;
	}

	public void setRefundAmtOriginal(BigDecimal refundAmtOriginal) {
		this.refundAmtOriginal = refundAmtOriginal;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinFeeRefundDetails getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinFeeRefundDetails beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
