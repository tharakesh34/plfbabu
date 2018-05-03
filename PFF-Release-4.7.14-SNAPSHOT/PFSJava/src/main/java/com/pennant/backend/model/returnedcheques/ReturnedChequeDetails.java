package com.pennant.backend.model.returnedcheques;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ReturnedChequeDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -306657295035931426L;

	private String   		custCIF;
	private String 			chequeNo;
	private Date			returnDate;
	private BigDecimal 		amount;
	private String 			returnReason;
	private String			currency;
	private String			ccyDesc;
	private String			custShrtName;
	private int 			ccyEditField;
	private boolean			newRecord=false;
	private LoggedInUser userDetails;
	private ReturnedChequeDetails  befImage;

	public ReturnedChequeDetails(){
		super();
	}
	
	public boolean isNew(){
		return isNewRecord();
	}

	public ReturnedChequeDetails(String id){
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("ccyDesc");
		excludeFields.add("custShrtName");
		excludeFields.add("ccyEditField");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId(){
		return custCIF;
	}
	public void setId(String id){
		this.custCIF = id;
	}
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getReturnReason() {
		return returnReason;
	}
	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public ReturnedChequeDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(ReturnedChequeDetails befImage) {
		this.befImage = befImage;
	}

	public String getCcyDesc() {
		return ccyDesc;
	}

	public void setCcyDesc(String ccyDesc) {
		this.ccyDesc = ccyDesc;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public int getCcyEditField() {
	    return ccyEditField;
    }

	public void setCcyEditField(int ccyEditField) {
	    this.ccyEditField = ccyEditField;
    }

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
