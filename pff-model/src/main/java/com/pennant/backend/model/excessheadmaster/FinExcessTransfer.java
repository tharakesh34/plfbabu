package com.pennant.backend.model.excessheadmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinExcessTransfer extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4364815634437557784L;

	private long id;
	private String finReference;
	private long finId;
	private Date transferDate;
	private String transferToType;
	private long transferToId;
	private String transferFromType;
	private long transferFromId;
	private BigDecimal transferAmount;
	private long linkedTranId;
	private long excessReferenceDesc;
	private String status;
	private long createdBy;
	private Timestamp createdOn;
	private long approvedBy;
	private Timestamp approvedOn;

	private Customer customer;
	private String custCIF;
	private long custId;
	private String finBranch;
	private String finType;

	private String lovValue;
	private FinExcessTransfer befImage;
	private FinExcessAmount finExcessAmount;
	private LoggedInUser userDetails;

	public FinExcessTransfer() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("customer");
		excludeFields.add("custCIF");
		excludeFields.add("custId");
		excludeFields.add("finBranch");
		excludeFields.add("finType");
		excludeFields.add("transferAmount");
		excludeFields.add("excessReferenceDesc");
		excludeFields.add("finExcessAmount");

		return excludeFields;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getFinId() {
		return finId;
	}

	public void setFinId(long finId) {
		this.finId = finId;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public String getTransferToType() {
		return transferToType;
	}

	public void setTransferToType(String transferToType) {
		this.transferToType = transferToType;
	}

	public long getTransferToId() {
		return transferToId;
	}

	public void setTransferToId(long transferToId) {
		this.transferToId = transferToId;
	}

	public String getTransferFromType() {
		return transferFromType;
	}

	public void setTransferFromType(String transferFromType) {
		this.transferFromType = transferFromType;
	}

	public long getTransferFromId() {
		return transferFromId;
	}

	public void setTransferFromId(long transferFromId) {
		this.transferFromId = transferFromId;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public FinExcessTransfer getBefImage() {
		return befImage;
	}

	public void setBefImage(FinExcessTransfer befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getExcessReferenceDesc() {
		return excessReferenceDesc;
	}

	public void setExcessReferenceDesc(long excessReferenceDesc) {
		this.excessReferenceDesc = excessReferenceDesc;
	}

	public FinExcessAmount getFinExcessAmount() {
		return finExcessAmount;
	}

	public void setFinExcessAmount(FinExcessAmount finExcessAmount) {
		this.finExcessAmount = finExcessAmount;
	}

	public long getId() {
		return id;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public long getApprovedBy() {
		return approvedBy;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setApprovedBy(long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public final Timestamp getApprovedOn() {
		return approvedOn == null ? null : (Timestamp) approvedOn.clone();
	}

	public final void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn == null ? null : (Timestamp) approvedOn.clone();
	}

	public final Timestamp getCreatedOn() {
		return createdOn == null ? null : (Timestamp) createdOn.clone();
	}

	public final void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn == null ? null : (Timestamp) createdOn.clone();
	}

}
