package com.pennant.backend.model.financemanagement.bankorcorpcreditreview;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class FinCreditRevSubCategory extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 3557119742009775415L;
	private String subCategoryCode;
	private long  categoryId;
	private int subCategorySeque;
	private String subCategoryDesc;
	private String subCategoryItemType;
	private String itemsToCal;
	private String itemRule;	
	private String calcSeque;
	private boolean isCreditCCY;
	private boolean format;
	private boolean percentCategory;
	private boolean grand;
	private String mainSubCategoryCode;
	private boolean newRecord = false;
	private String lovValue;
	private FinCreditRevSubCategory befImage;
	private LoggedInUser userDetails;
	
	private String remarks;
	
	public FinCreditRevSubCategory() {
		super();
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

	public FinCreditRevSubCategory getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinCreditRevSubCategory beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getRemarks() {
    	return remarks;
    }
	public void setRemarks(String remarks) {
    	this.remarks = remarks;
    }
	public long getCategoryId() {
    	return categoryId;
    }
	public void setCategoryId(long categoryId) {
    	this.categoryId = categoryId;
    }
	public int getSubCategorySeque() {
    	return subCategorySeque;
    }
	public void setSubCategorySeque(int subCategorySeque) {
    	this.subCategorySeque = subCategorySeque;
    }
	public String getSubCategoryDesc() {
    	return subCategoryDesc;
    }
	public void setSubCategoryDesc(String subCategoryDesc) {
    	this.subCategoryDesc = subCategoryDesc;
    }
	public String getSubCategoryItemType() {
    	return subCategoryItemType;
    }
	public void setSubCategoryItemType(String subCategoryItemType) {
    	this.subCategoryItemType = subCategoryItemType;
    }
	public String getItemsToCal() {
    	return itemsToCal;
    }
	public void setItemsToCal(String itemsToCal) {
    	this.itemsToCal = itemsToCal;
    }
	public String getItemRule() {
    	return itemRule;
    }
	public void setItemRule(String itemRule) {
    	this.itemRule = itemRule;
    }
	public boolean isIsCreditCCY() {
    	return isCreditCCY;
    }
	public void setIsCreditCCY(boolean isCreditCCY) {
    	this.isCreditCCY = isCreditCCY;
    }
	public void setSubCategoryCode(String subCategoryCode) {
	    this.subCategoryCode = subCategoryCode;
    }
	public String getSubCategoryCode() {
	    return subCategoryCode;
    }
	public void setMainSubCategoryCode(String mainSubCategoryCode) {
	    this.mainSubCategoryCode = mainSubCategoryCode;
    }
	public String getMainSubCategoryCode() {
	    return mainSubCategoryCode;
    }
	public void setFormat(boolean format) {
	    this.format = format;
    }
	public boolean isFormat() {
	    return format;
    }
	
	public boolean isPercentCategory() {
    	return percentCategory;
    }
	public void setPercentCategory(boolean percentCategory) {
    	this.percentCategory = percentCategory;
    }
	public void setGrand(boolean grand) {
	    this.grand = grand;
    }
	public boolean isGrand() {
	    return grand;
    }
	public void setCalcSeque(String calcSeque) {
	    this.calcSeque = calcSeque;
    }
	public String getCalcSeque() {
	    return calcSeque;
    }	
	
	public boolean isNew(){
		return isNewRecord();
	}
	public String getId() {
		return subCategoryCode;
	}
	
	public void setId (String id) {
		this.subCategoryCode = id;
	}

}
