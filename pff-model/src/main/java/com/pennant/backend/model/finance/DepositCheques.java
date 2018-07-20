package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DepositCheques extends AbstractWorkflowEntity implements Entity {

	private static final long	serialVersionUID	= -58727889587717168L;

	private long				id					= Long.MIN_VALUE;		// Auto Generated Sequence
	private long				movementId			= 0;
	private long				receiptId			= 0;
	private String				receiptMode;
	private BigDecimal			amount				= BigDecimal.ZERO;
	private long				revLinkedTranId;
	private String				status;
	
	private boolean				newRecord			= false;
	private DepositCheques		befImage;

	private String				finReference;
	private String				receiptpurpose;
	private String				favourNumber;
	private String				custShrtName;
	private String				remarks;
	private Date				receivedDate;
	private long				fundingAc;

	private String				partnerBankCode;
	private String				partnerBankName;
	private boolean				visible				= false;		// For Display purpose

	@XmlTransient
	private LoggedInUser		userDetails;

	public DepositCheques() {
		super();
	}

	public DepositCheques(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		excludeFields.add("finReference");
		excludeFields.add("receiptpurpose");
		excludeFields.add("favourNumber");
		excludeFields.add("custShrtName");
		excludeFields.add("remarks");
		excludeFields.add("receivedDate");
		excludeFields.add("fundingAc");
		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("visible");
		
		return excludeFields;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public DepositCheques getBefImage() {
		return befImage;
	}

	public void setBefImage(DepositCheques befImage) {
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

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public long getMovementId() {
		return movementId;
	}

	public void setMovementId(long movementId) {
		this.movementId = movementId;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getReceiptType() {
		return receiptMode;
	}

	public void setReceiptType(String receiptType) {
		this.receiptMode = receiptType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getReceiptpurpose() {
		return receiptpurpose;
	}

	public void setReceiptpurpose(String receiptpurpose) {
		this.receiptpurpose = receiptpurpose;
	}

	public String getFavourNumber() {
		return favourNumber;
	}

	public void setFavourNumber(String favourNumber) {
		this.favourNumber = favourNumber;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public long getFundingAc() {
		return fundingAc;
	}

	public void setFundingAc(long fundingAc) {
		this.fundingAc = fundingAc;
	}

	public String getPartnerBankCode() {
		return partnerBankCode;
	}

	public void setPartnerBankCode(String partnerBankCode) {
		this.partnerBankCode = partnerBankCode;
	}

	public String getPartnerBankName() {
		return partnerBankName;
	}

	public void setPartnerBankName(String partnerBankName) {
		this.partnerBankName = partnerBankName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public long getRevLinkedTranId() {
		return revLinkedTranId;
	}

	public void setRevLinkedTranId(long revLinkedTranId) {
		this.revLinkedTranId = revLinkedTranId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
