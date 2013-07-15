package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.HashMap;

public class SubHeadRule {
	
	private String custCIF;
	private String custCOB;
	private String custCtgCode;
	private String custIndustry;
	private boolean custIsStaff;
	private String custNationality;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;
	private String custSector;
	private String custSegment;
	private String custSubSector;
	private String custSubSegment;
	private String custTypeCode;
	private String debitOrCredit;
	private String reqCampaign;
	private String reqFinAcType;
	private String reqFinBranch;
	private String reqFinCcy;
	private String reqFinType;
	private String reqGLHead;
	private String reqProduct;
	private long earlyDays;
	private BigDecimal REFUND;
	private boolean isProcessed= false;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getCustCOB() {
		return custCOB;
	}
	public void setCustCOB(String custCOB) {
		this.custCOB = custCOB;
	}
	
	public String getCustCtgCode() {
		return custCtgCode;
	}
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}
	
	public String getCustIndustry() {
		return custIndustry;
	}
	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}
	
	public boolean isCustIsStaff() {
		return custIsStaff;
	}
	public void setCustIsStaff(boolean custIsStaff) {
		this.custIsStaff = custIsStaff;
	}
	
	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}
	
	public String getCustParentCountry() {
		return custParentCountry;
	}
	public void setCustParentCountry(String custParentCountry) {
		this.custParentCountry = custParentCountry;
	}
	
	public String getCustResdCountry() {
		return custResdCountry;
	}
	public void setCustResdCountry(String custResdCountry) {
		this.custResdCountry = custResdCountry;
	}
	
	public String getCustRiskCountry() {
		return custRiskCountry;
	}
	public void setCustRiskCountry(String custRiskCountry) {
		this.custRiskCountry = custRiskCountry;
	}
	
	public String getCustSector() {
		return custSector;
	}
	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}
	
	public String getCustSegment() {
		return custSegment;
	}
	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}
	
	public String getCustSubSector() {
		return custSubSector;
	}
	public void setCustSubSector(String custSubSector) {
		this.custSubSector = custSubSector;
	}
	
	public String getCustSubSegment() {
		return custSubSegment;
	}
	public void setCustSubSegment(String custSubSegment) {
		this.custSubSegment = custSubSegment;
	}
	
	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}
	
	public String getDebitOrCredit() {
		return debitOrCredit;
	}
	public void setDebitOrCredit(String debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}
	
	public String getReqCampaign() {
		return reqCampaign;
	}
	public void setReqCampaign(String reqCampaign) {
		this.reqCampaign = reqCampaign;
	}
	
	public String getReqFinAcType() {
		return reqFinAcType;
	}
	public void setReqFinAcType(String reqFinAcType) {
		this.reqFinAcType = reqFinAcType;
	}
	
	public String getReqFinBranch() {
		return reqFinBranch;
	}
	public void setReqFinBranch(String reqFinBranch) {
		this.reqFinBranch = reqFinBranch;
	}
	
	public String getReqFinCcy() {
		return reqFinCcy;
	}
	public void setReqFinCcy(String reqFinCcy) {
		this.reqFinCcy = reqFinCcy;
	}
	
	public String getReqFinType() {
		return reqFinType;
	}
	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
	}
	
	public String getReqGLHead() {
		return reqGLHead;
	}
	public void setReqGLHead(String reqGLHead) {
		this.reqGLHead = reqGLHead;
	}
	
	public String getReqProduct() {
		return reqProduct;
	}
	public void setReqProduct(String reqProduct) {
		this.reqProduct = reqProduct;
	}
	
	public long getEarlyDays() {
    	return earlyDays;
    }
	public void setEarlyDays(long earlyDays) {
    	this.earlyDays = earlyDays;
    }
	
	public BigDecimal getREFUND() {
    	return REFUND;
    }
	public void setREFUND(BigDecimal rEFUND) {
    	REFUND = rEFUND;
    }
	
	public boolean isProcessed() {
		return isProcessed;
	}
	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	
	//Set values into Map
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> subHeadRuleMap = new HashMap<String, Object>();	
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				subHeadRuleMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subHeadRuleMap;
	}
}
