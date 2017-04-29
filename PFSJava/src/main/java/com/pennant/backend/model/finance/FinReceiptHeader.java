package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinReceiptHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = -58727889587717168L;
	
	private long receiptID = 0;// Auto Generated Sequence
	private Date receiptDate;
	private String receiptType;
	private String recAgainst;
	private String reference;
	private String receiptPurpose;
	private String receiptMode;
	private String excessAdjustTo;
	private String finType;
	private String finBranch;
	private String finCcy;
	private String custCIF;
	private String custShrtName;
	private String allocationType;
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String effectSchdMethod;
	private String receiptModeStatus;
	
	private boolean newRecord;
	private String lovValue;
	private FinReceiptHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	private List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>(1);
	private List<FinExcessAmount> excessAmounts = new ArrayList<FinExcessAmount>(1);
	private List<ReceiptAllocationDetail> allocations = new ArrayList<ReceiptAllocationDetail>(1);
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("receiptDetails");
		excludeFields.add("excessAmounts");
		excludeFields.add("allocations");
		excludeFields.add("finType");
		excludeFields.add("finCcy");
		excludeFields.add("finBranch");
		excludeFields.add("custCIF");
		excludeFields.add("custShrtName");

		
		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public FinReceiptHeader() {
		super();
	}

	public FinReceiptHeader(long id) {
		super();
		this.setId(id);
	}

	public long getId() {
		return receiptID;
	}

	public void setId(long id) {
		this.receiptID = id;
	}
	
	public long getReceiptID() {
		return receiptID;
	}
	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public String getRecAgainst() {
		return recAgainst;
	}
	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getReceiptPurpose() {
		return receiptPurpose;
	}
	public void setReceiptPurpose(String receiptPurpose) {
		this.receiptPurpose = receiptPurpose;
	}

	public String getExcessAdjustTo() {
		return excessAdjustTo;
	}
	public void setExcessAdjustTo(String excessAdjustTo) {
		this.excessAdjustTo = excessAdjustTo;
	}

	public String getAllocationType() {
		return allocationType;
	}
	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getEffectSchdMethod() {
		return effectSchdMethod;
	}
	public void setEffectSchdMethod(String effectSchdMethod) {
		this.effectSchdMethod = effectSchdMethod;
	}

	public List<FinReceiptDetail> getReceiptDetails() {
		return receiptDetails;
	}
	public void setReceiptDetails(List<FinReceiptDetail> receiptDetails) {
		this.receiptDetails = receiptDetails;
	}

	public String getReceiptMode() {
		return receiptMode;
	}
	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

	public List<FinExcessAmount> getExcessAmounts() {
		return excessAmounts;
	}
	public void setExcessAmounts(List<FinExcessAmount> excessAmounts) {
		this.excessAmounts = excessAmounts;
	}

	public List<ReceiptAllocationDetail> getAllocations() {
		return allocations;
	}
	public void setAllocations(List<ReceiptAllocationDetail> allocations) {
		this.allocations = allocations;
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

	public FinReceiptHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinReceiptHeader beforeImage) {
		this.befImage = beforeImage;
	}
	
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public String getReceiptModeStatus() {
		return receiptModeStatus;
	}
	public void setReceiptModeStatus(String receiptModeStatus) {
		this.receiptModeStatus = receiptModeStatus;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinBranch() {
		return finBranch;
	}

	public void setFinBranch(String finBranch) {
		this.finBranch = finBranch;
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

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

}
