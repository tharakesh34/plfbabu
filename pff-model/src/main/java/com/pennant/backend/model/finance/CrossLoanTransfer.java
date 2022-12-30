package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CrossLoanTransfer extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 4414905784442720643L;

	private long crossLoanId = 0;
	private String custCif;
	private String custShrtName;
	private long custId = 0;
	private String fromFinReference;
	private String toFinReference;
	private long receiptId;
	private long excessId;
	private long toExcessId;
	private String transactionRef;
	private BigDecimal transferAmount = BigDecimal.ZERO;
	private BigDecimal excessAmount = BigDecimal.ZERO;
	private BigDecimal utiliseAmount = BigDecimal.ZERO;
	private BigDecimal reserveAmount = BigDecimal.ZERO;
	private BigDecimal availableAmount = BigDecimal.ZERO;
	private String fromFinType;
	private String toFinType;
	private long toLinkedTranId;
	private long fromLinkedTranId;
	private List<FinExcessAmount> finExcessAmountList = new ArrayList<>();
	private FinExcessAmount finExcessAmount;
	private Date receiptDate;
	private BigDecimal receiptAmount = BigDecimal.ZERO;
	private String moduleType;
	private boolean process;
	private boolean newRecord;
	private String lovValue;
	private CrossLoanTransfer befImage;
	private LoggedInUser userDetails;
	private Date valueDate;
	private String source;
	private String excessType;

	public CrossLoanTransfer() {
		super();
	}

	public CrossLoanTransfer(long crossLoanId) {
		super();
		this.crossLoanId = crossLoanId;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("fromFinType");
		excludeFields.add("toFinType");
		excludeFields.add("finExcessAmountList");
		excludeFields.add("finExcessAmount");
		excludeFields.add("transactionRef");
		excludeFields.add("receiptAmount");
		excludeFields.add("receiptDate");
		excludeFields.add("moduleType");
		excludeFields.add("process");
		excludeFields.add("valueDate");
		return excludeFields;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return this.crossLoanId;
	}

	@Override
	public void setId(long id) {
		this.crossLoanId = id;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFromFinReference() {
		return fromFinReference;
	}

	public void setFromFinReference(String fromFinReference) {
		this.fromFinReference = fromFinReference;
	}

	public String getToFinReference() {
		return toFinReference;
	}

	public void setToFinReference(String toFinReference) {
		this.toFinReference = toFinReference;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public long getCrossLoanId() {
		return crossLoanId;
	}

	public void setCrossLoanId(long crossLoanId) {
		this.crossLoanId = crossLoanId;
	}

	public String getFromFinType() {
		return fromFinType;
	}

	public String getToFinType() {
		return toFinType;
	}

	public void setFromFinType(String fromFinType) {
		this.fromFinType = fromFinType;
	}

	public List<FinExcessAmount> getFinExcessAmountList() {
		return finExcessAmountList;
	}

	public void setFinExcessAmountList(List<FinExcessAmount> finExcessAmountList) {
		this.finExcessAmountList = finExcessAmountList;
	}

	public void setToFinType(String toFinType) {
		this.toFinType = toFinType;
	}

	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public BigDecimal getUtiliseAmount() {
		return utiliseAmount;
	}

	public BigDecimal getReserveAmount() {
		return reserveAmount;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	public void setUtiliseAmount(BigDecimal utiliseAmount) {
		this.utiliseAmount = utiliseAmount;
	}

	public void setReserveAmount(BigDecimal reserveAmount) {
		this.reserveAmount = reserveAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public long getExcessId() {
		return excessId;
	}

	public void setExcessId(long excessId) {
		this.excessId = excessId;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public FinExcessAmount getFinExcessAmount() {
		return finExcessAmount;
	}

	public void setFinExcessAmount(FinExcessAmount finExcessAmount) {
		this.finExcessAmount = finExcessAmount;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public long getToLinkedTranId() {
		return toLinkedTranId;
	}

	public void setToLinkedTranId(long toLinkedTranId) {
		this.toLinkedTranId = toLinkedTranId;
	}

	public long getFromLinkedTranId() {
		return fromLinkedTranId;
	}

	public void setFromLinkedTranId(long fromLinkedTranId) {
		this.fromLinkedTranId = fromLinkedTranId;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public long getToExcessId() {
		return toExcessId;
	}

	public void setToExcessId(long toExcessId) {
		this.toExcessId = toExcessId;
	}

	public CrossLoanTransfer getBefImage() {
		return befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public void setBefImage(CrossLoanTransfer befImage) {
		this.befImage = befImage;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean getProcess() {
		return process;
	}

	public void setProcess(boolean process) {
		this.process = process;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getSource() {
		return source;
	}

	public String getExcessType() {
		return excessType;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setExcessType(String excessType) {
		this.excessType = excessType;
	}

}
