package com.pennant.backend.model.finance;

import java.util.Date;
import java.util.List;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class FinanceDedup {
	
	private long custId = Long.MIN_VALUE;
	private String finReference;
	private String custCIF;
	private String custFName;
	private String custMName;
	private String custLName;
	private String custShrtName;
	private String custMotherMaiden;
	private String likeCustFName;
	private String likeCustMName;
	private String likeCustLName;
	private String custNationality;
	private String custParentCountry;
	private Date custDOB;
	
	private String custPassportNo;
	private String custCPRNo;
	private String custCRNo;

	private FinanceDedup befImage;
	private List<FinanceDedup> dedupList;
	private String dedupFields;
	
	public FinanceDedup(){
		super();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustId(long custId) {
	    this.custId = custId;
    }
	public long getCustId() {
	    return custId;
    }
	
	public void setFinReference(String finReference) {
	    this.finReference = finReference;
    }
	public String getFinReference() {
	    return finReference;
    }

	public String getCustCIF() {
    	return custCIF;
    }
	public void setCustCIF(String custCIF) {
    	this.custCIF = custCIF;
    }

	public String getCustFName() {
    	return custFName;
    }
	public void setCustFName(String custFName) {
    	this.custFName = custFName;
    }

	public String getCustMName() {
    	return custMName;
    }
	public void setCustMName(String custMName) {
    	this.custMName = custMName;
    }

	public String getCustLName() {
    	return custLName;
    }
	public void setCustLName(String custLName) {
    	this.custLName = custLName;
    }

	public void setCustShrtName(String custShrtName) {
	    this.custShrtName = custShrtName;
    }
	public String getCustShrtName() {
	    return custShrtName;
    }

	public String getCustMotherMaiden() {
    	return custMotherMaiden;
    }
	public void setCustMotherMaiden(String custMotherMaiden) {
    	this.custMotherMaiden = custMotherMaiden;
    }

	public String getLikeCustFName() {
    	return likeCustFName;
    }
	public void setLikeCustFName(String likeCustFName) {
    	this.likeCustFName = likeCustFName;
    }

	public String getLikeCustMName() {
    	return likeCustMName;
    }
	public void setLikeCustMName(String likeCustMName) {
    	this.likeCustMName = likeCustMName;
    }

	public String getLikeCustLName() {
    	return likeCustLName;
    }
	public void setLikeCustLName(String likeCustLName) {
    	this.likeCustLName = likeCustLName;
    }

	public String getCustPassportNo() {
    	return custPassportNo;
    }
	public void setCustPassportNo(String custPassportNo) {
    	this.custPassportNo = custPassportNo;
    }
	
	public String getCustCPRNo() {
    	return custCPRNo;
    }
	public void setCustCPRNo(String custCPRNo) {
    	this.custCPRNo = custCPRNo;
    }

	public String getCustCRNo() {
    	return custCRNo;
    }
	public void setCustCRNo(String custCRNo) {
    	this.custCRNo = custCRNo;
    }

	public String getCustNationality() {
    	return custNationality;
    }
	public void setCustNationality(String custNationality) {
    	this.custNationality = custNationality;
    }

	public void setCustParentCountry(String custParentCountry) {
	    this.custParentCountry = custParentCountry;
    }
	public String getCustParentCountry() {
	    return custParentCountry;
    }

	public Date getCustDOB() {
    	return custDOB;
    }
	public void setCustDOB(Date custDOB) {
    	this.custDOB = custDOB;
    }

	public FinanceDedup getBefImage() {
		return befImage;
	}
	public void setBefImage(FinanceDedup befImage) {
		this.befImage = befImage;
	}
	
	public List<FinanceDedup> getDedupList() {
		return dedupList;
	}
	public void setDedupList(List<FinanceDedup> dedupList) {
		this.dedupList = dedupList;
	}
	
	public String getDedupFields() {
		return dedupFields;
	}
	public void setDedupFields(String dedupFields) {
		this.dedupFields = dedupFields;
	}
	
	public boolean isChanged() {
		boolean changed =false;
		
		if(befImage==null){
			changed=true;
		}else{
			
		}
		return changed;
	}

}
