package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DepositMovements extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -58727889587717168L;

	private long movementId = Long.MIN_VALUE; // Auto Generated Sequence
	private long depositId = 0;
	private String transactionType;
	private BigDecimal reservedAmount = BigDecimal.ZERO;
	private String depositSlipNumber;
	private Date transactionDate;
	private long partnerBankId = 0;
	private String partnerBankCode;
	private String partnerBankName;
	private long receiptId = 0;
	private long linkedTranId = 0;
	private DepositMovements befImage;

	private String branchCode;
	private String branchDesc;
	private String depositType;

	@XmlTransient
	private LoggedInUser userDetails;

	private List<CashDenomination> denominationList = null;
	private List<DepositCheques> depositChequesList = null;

	public DepositMovements() {
		super();
	}

	public DepositMovements(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		excludeFields.add("partnerBankCode");
		excludeFields.add("partnerBankName");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("depositType");

		return excludeFields;
	}

	public long getDepositId() {
		return depositId;
	}

	public void setDepositId(long depositId) {
		this.depositId = depositId;
	}

	public DepositMovements getBefImage() {
		return befImage;
	}

	public void setBefImage(DepositMovements befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getId() {
		return this.movementId;
	}

	public void setId(long id) {
		this.movementId = id;
	}

	public long getMovementId() {
		return movementId;
	}

	public void setMovementId(long movementId) {
		this.movementId = movementId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public String getDepositSlipNumber() {
		return depositSlipNumber;
	}

	public void setDepositSlipNumber(String depositSlipNumber) {
		this.depositSlipNumber = depositSlipNumber;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public List<CashDenomination> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<CashDenomination> denominationList) {
		this.denominationList = denominationList;
	}

	public long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(long partnerBankId) {
		this.partnerBankId = partnerBankId;
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

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchDesc() {
		return branchDesc;
	}

	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}

	public List<DepositCheques> getDepositChequesList() {
		return depositChequesList;
	}

	public void setDepositChequesList(List<DepositCheques> depositChequesList) {
		this.depositChequesList = depositChequesList;
	}

	public String getDepositType() {
		return depositType;
	}

	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}
}
