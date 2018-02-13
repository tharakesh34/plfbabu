package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Notes;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class CustomerFinanceDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	
	private long custId = Long.MIN_VALUE;
	private String finReference;
	private String finEvent;
	private String finBranch;
 	private String custCIF;
	private String custShrtName;
	private String custDocType;
	private String custDocTitle;
	private String phoneNumber;

	private String phoneTypeCode;
	private String lovDescPhoneTypeCodeName;
	private String phoneCountryCode;
	private String lovDescPhoneCountryName;
	private String phoneAreaCode;
	
	private String custEMailTypeCode;
	private String lovDescCustEMailTypeCode;
	private int custEMailPriority;
	private String custEMail;
	public String deptDesc;
	
	private String finType;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private BigDecimal feeChargeAmt = BigDecimal.ZERO;
	private Date finStartDate;
	private String usrFName;
	private String lastMntByUser;
	private String finCcy;
	private String finTypeDesc;
	
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

	private CustomerFinanceDetail befImage;
 	private LoggedInUser userDetails;

	private String prvRoleDesc="";
	private String nextRoleDesc= "";
	
	private List<AuditTransaction> auditTransactionsList;
	private List<Notes> notesList;
	
  
	public CustomerFinanceDetail(){
		super();
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("usrFName");
		return excludeFields;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//	
 	
	public long getCustId() {
    	return custId;
    }

	public void setCustId(long custId) {
    	this.custId = custId;
    }

	public String getFinReference() {
    	return finReference;
    }

	public void setFinReference(String finReference) {
    	this.finReference = finReference;
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

	public String getCustDocType() {
    	return custDocType;
    }

	public void setCustDocType(String custDocType) {
    	this.custDocType = custDocType;
    }

	public String getCustDocTitle() {
    	return custDocTitle;
    }

	public void setCustDocTitle(String custDocTitle) {
    	this.custDocTitle = custDocTitle;
    }

	public String getPhoneNumber() {
    	return phoneNumber;
    }

	public void setPhoneNumber(String phoneNumber) {
    	this.phoneNumber = phoneNumber;
    }

	public String getPhoneTypeCode() {
    	return phoneTypeCode;
    }

	public void setPhoneTypeCode(String phoneTypeCode) {
    	this.phoneTypeCode = phoneTypeCode;
    }

	public String getLovDescPhoneTypeCodeName() {
    	return lovDescPhoneTypeCodeName;
    }

	public void setLovDescPhoneTypeCodeName(String lovDescPhoneTypeCodeName) {
    	this.lovDescPhoneTypeCodeName = lovDescPhoneTypeCodeName;
    }

	public String getPhoneCountryCode() {
    	return phoneCountryCode;
    }

	public void setPhoneCountryCode(String phoneCountryCode) {
    	this.phoneCountryCode = phoneCountryCode;
    }

	public String getLovDescPhoneCountryName() {
    	return lovDescPhoneCountryName;
    }

	public void setLovDescPhoneCountryName(String lovDescPhoneCountryName) {
    	this.lovDescPhoneCountryName = lovDescPhoneCountryName;
    }

	public String getPhoneAreaCode() {
    	return phoneAreaCode;
    }

	public void setPhoneAreaCode(String phoneAreaCode) {
    	this.phoneAreaCode = phoneAreaCode;
    }

	public String getCustEMailTypeCode() {
    	return custEMailTypeCode;
    }

	public void setCustEMailTypeCode(String custEMailTypeCode) {
    	this.custEMailTypeCode = custEMailTypeCode;
    }

	public String getLovDescCustEMailTypeCode() {
    	return lovDescCustEMailTypeCode;
    }

	public void setLovDescCustEMailTypeCode(String lovDescCustEMailTypeCode) {
    	this.lovDescCustEMailTypeCode = lovDescCustEMailTypeCode;
    }

	public int getCustEMailPriority() {
    	return custEMailPriority;
    }

	public void setCustEMailPriority(int custEMailPriority) {
    	this.custEMailPriority = custEMailPriority;
    }

	public String getCustEMail() {
    	return custEMail;
    }

	public void setCustEMail(String custEMail) {
    	this.custEMail = custEMail;
    }

	public String getDeptDesc() {
    	return deptDesc;
    }

	public void setDeptDesc(String deptDesc) {
    	this.deptDesc = deptDesc;
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

	public String getLovDescCustRecordType() {
    	return lovDescCustRecordType;
    }

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
    	this.lovDescCustRecordType = lovDescCustRecordType;
    }

	public String getLovDescCustCIF() {
    	return lovDescCustCIF;
    }

	public void setLovDescCustCIF(String lovDescCustCIF) {
    	this.lovDescCustCIF = lovDescCustCIF;
    }

	public CustomerFinanceDetail getBefImage() {
    	return befImage;
    }

	public void setBefImage(CustomerFinanceDetail befImage) {
    	this.befImage = befImage;
    }

	public LoggedInUser getUserDetails() {
    	return userDetails;
    }

	public void setUserDetails(LoggedInUser userDetails) {
    	this.userDetails = userDetails;
    }

	public String getPrvRoleDesc() {
    	return prvRoleDesc;
    }

	public void setPrvRoleDesc(String prvRoleDesc) {
    	this.prvRoleDesc = prvRoleDesc;
    }

	public String getNextRoleDesc() {
    	return nextRoleDesc;
    }

	public void setNextRoleDesc(String nextRoleDesc) {
    	this.nextRoleDesc = nextRoleDesc;
    }

	public boolean isChanged() {
		boolean changed =false;
		
		if(befImage==null){
			changed=true;
		}else{
			
		}
		return changed;
	}

	public List<AuditTransaction> getAuditTransactionsList() {
    	return auditTransactionsList;
    }

	public void setAuditTransactionsList(List<AuditTransaction> auditTransactionsList) {
    	this.auditTransactionsList = auditTransactionsList;
    }

	public List<Notes> getNotesList() {
    	return notesList;
    }

	public void setNotesList(List<Notes> notesList) {
    	this.notesList = notesList;
    }

	public void setFinType(String finType) {
	    this.finType = finType;
    }

	public String getFinType() {
	    return finType;
    }

	public void setFinAmount(BigDecimal finAmount) {
	    this.finAmount = finAmount;
    }

	public BigDecimal getFinAmount() {
	    return finAmount;
    }

	public void setFinStartDate(Date finStartDate) {
	    this.finStartDate = finStartDate;
    }

	public Date getFinStartDate() {
	    return finStartDate;
    }

	public void setLastMntByUser(String lastMntByUser) {
	    this.lastMntByUser = lastMntByUser;
    }

	public String getLastMntByUser() {
	    return lastMntByUser;
    }

	public void setFinCcy(String finCcy) {
	    this.finCcy = finCcy;
    }

	public String getFinCcy() {
	    return finCcy;
    }

	public void setFinTypeDesc(String finTypeDesc) {
	    this.finTypeDesc = finTypeDesc;
    }

	public String getFinTypeDesc() {
	    return finTypeDesc;
    }

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public String getUsrFName() {
		return usrFName;
	}

	public void setUsrFName(String usrFName) {
		this.usrFName = usrFName;
	}

	public BigDecimal getDownPayment() {
		return downPayment;
	}

	public void setDownPayment(BigDecimal downPayment) {
		this.downPayment = downPayment;
	}

	public BigDecimal getFeeChargeAmt() {
		return feeChargeAmt;
	}

	public void setFeeChargeAmt(BigDecimal feeChargeAmt) {
		this.feeChargeAmt = feeChargeAmt;
	}

}
