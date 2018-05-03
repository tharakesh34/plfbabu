package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class FinCreditReviewSummary extends AbstractWorkflowEntity implements Entity{

	private static final long serialVersionUID = 3557119742009775415L;
	private long summaryId  = Long.MIN_VALUE;
	private long detailId;
	private String subCategoryCode;
	private long lovDescCategoryID;
	private String lovDescCategoryDesc;
	private String lovDescSubCategoryDesc;
	private String lovDescCreditRevCode;
	private BigDecimal itemValue;	
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditReviewSummary befImage;
	private LoggedInUser userDetails;

	private BigDecimal lovDescConversionRate;
	private String lovDescBankName;
	private int lovDescNoOfShares;
	private BigDecimal lovDescMarketPrice;
	private String auditYear;
	private String creditRevCode;
	private int lovDescCcyEditField;
	
	
	public BigDecimal getLovDescConversionRate() {
    	return lovDescConversionRate;
    }

	public void setLovDescConversionRate(BigDecimal lovDescConversionRate) {
    	this.lovDescConversionRate = lovDescConversionRate;
    }

	public String getLovDescBankName() {
    	return lovDescBankName;
    }

	public void setLovDescBankName(String lovDescBankName) {
    	this.lovDescBankName = lovDescBankName;
    }

	public String getLovDescCreditRevCode() {
    	return lovDescCreditRevCode;
    }

	public void setLovDescCreditRevCode(String lovDescCreditRevCode) {
    	this.lovDescCreditRevCode = lovDescCreditRevCode;
    }

	public FinCreditReviewSummary(){
		super();
	}
	
	public boolean isNew() {
		return isNewRecord();
	}
	public long getSummaryId() {
    	return this.summaryId;
    }
	public void setSummaryId(long summaryId) {
    	this.summaryId = summaryId;
    }
	public long getDetailId() {
    	return detailId;
    }
	public String getLovDescSubCategoryDesc() {
    	return lovDescSubCategoryDesc;
    }

	public void setLovDescSubCategoryDesc(String lovDescSubCategoryDesc) {
    	this.lovDescSubCategoryDesc = lovDescSubCategoryDesc;
    }

	public void setDetailId(long detailId) {
    	this.detailId = detailId;
    }	
	public BigDecimal getItemValue() {
    	return itemValue;
    }
	public void setItemValue(BigDecimal itemValue) {
    	this.itemValue = itemValue;
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

	public FinCreditReviewSummary getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditReviewSummary beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	@Override
    public long getId() {
	   
	    return this.summaryId;
    }
	@Override
    public void setId(long id) {
	    this.summaryId = id;
    }

	public void setSubCategoryCode(String subCategoryCode) {
	    this.subCategoryCode = subCategoryCode;
    }

	public String getSubCategoryCode() {
	    return subCategoryCode;
    }

	public void setLovDescNoOfShares(int lovDescNoOfShares) {
	    this.lovDescNoOfShares = lovDescNoOfShares;
    }

	public int getLovDescNoOfShares() {
	    return lovDescNoOfShares;
    }

	public void setLovDescMarketPrice(BigDecimal lovDescMarketPrice) {
	    this.lovDescMarketPrice = lovDescMarketPrice;
    }

	public BigDecimal getLovDescMarketPrice() {
	    return lovDescMarketPrice;
    }

	public long getLovDescCategoryID() {
    	return lovDescCategoryID;
    }

	public void setLovDescCategoryID(long lovDescCategoryID) {
    	this.lovDescCategoryID = lovDescCategoryID;
    }

	public String getLovDescCategoryDesc() {
    	return lovDescCategoryDesc;
    }

	public void setLovDescCategoryDesc(String lovDescCategoryDesc) {
    	this.lovDescCategoryDesc = lovDescCategoryDesc;
    }

	public void setAuditYear(String auditYear) {
	    this.auditYear = auditYear;
    }

	public String getAuditYear() {
	    return auditYear;
    }

	public void setCreditRevCode(String creditRevCode) {
	    this.creditRevCode = creditRevCode;
    }

	public String getCreditRevCode() {
	    return creditRevCode;
    }

	public int getLovDescCcyEditField() {
    	return lovDescCcyEditField;
    }

	public void setLovDescCcyEditField(int lovDescCcyEditField) {
    	this.lovDescCcyEditField = lovDescCcyEditField;
    }

}
