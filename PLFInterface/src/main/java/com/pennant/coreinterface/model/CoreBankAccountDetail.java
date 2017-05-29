package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CoreBankAccountDetail implements Serializable {

	private static final long serialVersionUID = -5109328909531296518L;

	public CoreBankAccountDetail() {
    	super();
    }
	
	private String custCIF;
	private String acBranch;
	private String acCcy;
	private String acType;
	private long   acCustId;
	private String acFullName;
	private String acShrtName;
	private String tranAc;
	private boolean internalAc;
	private boolean createNew;
	private boolean createIfNF;
	private String amountSign;
	private String transOrder;

	private String custShrtName;
	private String accountNumber;
	private BigDecimal acBal;

	private String openStatus;
	private String errorCode;
	private String errorMessage;

	private String acSPCode;
	private int reqRefId;
	private int reqRefSeq;

	private String division;
	
	//additional fields
	private String referenceNumber;
	private String customerType;
	private String productCode;
	private String accountOfficer;
	private String jointHolderID;
	private String jointRelationCode;
	private String relationNotes;
	private String modeOfOperation;
	private String minNoOfSignatory;
	private String introducer;
	private String powerOfAttorneyFlag;
	private String powerOfAttorneyCIF;
	private String shoppingCardIssue;
	private String IBAN;
	private String CIN;
	private String UIN;
	private List<CoreBankAccountDetail> accSummary;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getAcBranch() {
		return acBranch;
	}

	public void setAcBranch(String acBranch) {
		this.acBranch = acBranch;
	}

	public String getAcCcy() {
		return acCcy;
	}

	public void setAcCcy(String acCcy) {
		this.acCcy = acCcy;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcFullName() {
		return acFullName;
	}

	public void setAcFullName(String acFullName) {
		this.acFullName = acFullName;
	}

	public String getAcShrtName() {
		return acShrtName;
	}

	public void setAcShrtName(String acShrtName) {
		this.acShrtName = acShrtName;
	}

	public String getTranAc() {
		return tranAc;
	}

	public void setTranAc(String tranAc) {
		this.tranAc = tranAc;
	}

	public boolean getInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public boolean getCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public boolean getCreateIfNF() {
		return createIfNF;
	}

	public void setCreateIfNF(boolean createIfNF) {
		this.createIfNF = createIfNF;
	}

	public String getAmountSign() {
		return amountSign;
	}

	public void setAmountSign(String amountSign) {
		this.amountSign = amountSign;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAcBal() {
		return acBal;
	}

	public void setAcBal(BigDecimal acBal) {
		this.acBal = acBal;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAcSPCode() {
		return acSPCode;
	}

	public void setAcSPCode(String acSPCode) {
		this.acSPCode = acSPCode;
	}

	public int getReqRefId() {
		return reqRefId;
	}

	public void setReqRefId(int reqRefId) {
		this.reqRefId = reqRefId;
	}

	public int getReqRefSeq() {
		return reqRefSeq;
	}

	public void setReqRefSeq(int reqRefSeq) {
		this.reqRefSeq = reqRefSeq;
	}

	public void setTransOrder(String transOrder) {
		this.transOrder = transOrder;
	}

	public String getTransOrder() {
		return transOrder;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}

	public String getJointHolderID() {
		return jointHolderID;
	}

	public void setJointHolderID(String jointHolderID) {
		this.jointHolderID = jointHolderID;
	}

	public String getJointRelationCode() {
		return jointRelationCode;
	}

	public void setJointRelationCode(String jointRelationCode) {
		this.jointRelationCode = jointRelationCode;
	}

	public String getRelationNotes() {
		return relationNotes;
	}

	public void setRelationNotes(String relationNotes) {
		this.relationNotes = relationNotes;
	}

	public String getModeOfOperation() {
		return modeOfOperation;
	}

	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}

	public String getMinNoOfSignatory() {
		return minNoOfSignatory;
	}

	public void setMinNoOfSignatory(String minNoOfSignatory) {
		this.minNoOfSignatory = minNoOfSignatory;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getPowerOfAttorneyFlag() {
		return powerOfAttorneyFlag;
	}

	public void setPowerOfAttorneyFlag(String powerOfAttorneyFlag) {
		this.powerOfAttorneyFlag = powerOfAttorneyFlag;
	}

	public String getPowerOfAttorneyCIF() {
		return powerOfAttorneyCIF;
	}

	public void setPowerOfAttorneyCIF(String powerOfAttorneyCIF) {
		this.powerOfAttorneyCIF = powerOfAttorneyCIF;
	}

	public String getShoppingCardIssue() {
		return shoppingCardIssue;
	}

	public void setShoppingCardIssue(String shoppingCardIssue) {
		this.shoppingCardIssue = shoppingCardIssue;
	}

	public String getIBAN() {
		return IBAN;
	}

	public void setIBAN(String iBAN) {
		IBAN = iBAN;
	}

	public String getCIN() {
		return CIN;
	}

	public void setCIN(String cIN) {
		CIN = cIN;
	}

	public String getUIN() {
		return UIN;
	}

	public void setUIN(String uIN) {
		UIN = uIN;
	}

	public List<CoreBankAccountDetail> getAccSummary() {
		return accSummary;
	}

	public void setAccSummary(List<CoreBankAccountDetail> accSummary) {
		this.accSummary = accSummary;
	}
	
	public long getAcCustId() {
		return acCustId;
	}

	public void setAcCustId(long acCustId) {
		this.acCustId = acCustId;
	}

}
