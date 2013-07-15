package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class CustomerSubHeadRuleCheck {

	private String ReqFinType ;
	private String ReqProduct;
	private String ReqCampaign;
	private String ReqFinAcType;
	private String ReqFinCcy;
	private boolean isDebitOrCredit;
	private String ReqFinBranch;
	private String ReqGLHead;
	private String ReqSubHeadRule;
	private String Cust;
	private String CustCtg;
	private String CustType;
	private Date CustCOB;
	private boolean CustIsStaff;
	private String CustIndustry;
	private String CustSector;
	private String CustSubSector;
	private BigDecimal CustTotalIncome;
	private String CustSegment;
	private String CustSubSegment;
	private String CustParentCountry;
	private String CustResdCountry;
	private String CustRiskCountry;
	private String CustNationality;

	public String getReqFinType() {
		return ReqFinType;
	}

	public void setReqFinType(String reqFinType) {
		ReqFinType = reqFinType;
	}

	public String getReqProduct() {
		return ReqProduct;
	}

	public void setReqProduct(String reqProduct) {
		ReqProduct = reqProduct;
	}

	public String getReqCampaign() {
		return ReqCampaign;
	}

	public void setReqCampaign(String reqCampaign) {
		ReqCampaign = reqCampaign;
	}

	public String getReqFinAcType() {
		return ReqFinAcType;
	}

	public void setReqFinAcType(String reqFinAcType) {
		ReqFinAcType = reqFinAcType;
	}

	public String getReqFinCcy() {
		return ReqFinCcy;
	}

	public void setReqFinCcy(String reqFinCcy) {
		ReqFinCcy = reqFinCcy;
	}

	public boolean isDebitOrCredit() {
		return isDebitOrCredit;
	}

	public void setDebitOrCredit(boolean isDebitOrCredit) {
		this.isDebitOrCredit = isDebitOrCredit;
	}

	public String getReqFinBranch() {
		return ReqFinBranch;
	}

	public void setReqFinBranch(String reqFinBranch) {
		ReqFinBranch = reqFinBranch;
	}

	public String getReqGLHead() {
		return ReqGLHead;
	}

	public void setReqGLHead(String reqGLHead) {
		ReqGLHead = reqGLHead;
	}

	public String getReqSubHeadRule() {
		return ReqSubHeadRule;
	}

	public void setReqSubHeadRule(String reqSubHeadRule) {
		ReqSubHeadRule = reqSubHeadRule;
	}

	public String getCust() {
		return Cust;
	}

	public void setCust(String cust) {
		Cust = cust;
	}

	public String getCustCtg() {
		return CustCtg;
	}

	public void setCustCtg(String custCtg) {
		CustCtg = custCtg;
	}

	public String getCustType() {
		return CustType;
	}

	public void setCustType(String custType) {
		CustType = custType;
	}

	public Date getCustCOB() {
		return CustCOB;
	}

	public void setCustCOB(Date custCOB) {
		CustCOB = custCOB;
	}

	public boolean isCustIsStaff() {
		return CustIsStaff;
	}

	public void setCustIsStaff(boolean custIsStaff) {
		CustIsStaff = custIsStaff;
	}

	public String getCustIndustry() {
		return CustIndustry;
	}

	public void setCustIndustry(String custIndustry) {
		CustIndustry = custIndustry;
	}

	public String getCustSector() {
		return CustSector;
	}

	public void setCustSector(String custSector) {
		CustSector = custSector;
	}

	public String getCustSubSector() {
		return CustSubSector;
	}

	public void setCustSubSector(String custSubSector) {
		CustSubSector = custSubSector;
	}

	public BigDecimal getCustTotalIncome() {
		return CustTotalIncome;
	}

	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		CustTotalIncome = custTotalIncome;
	}

	public String getCustSegment() {
		return CustSegment;
	}

	public void setCustSegment(String custSegment) {
		CustSegment = custSegment;
	}

	public String getCustSubSegment() {
		return CustSubSegment;
	}

	public void setCustSubSegment(String custSubSegment) {
		CustSubSegment = custSubSegment;
	}

	public String getCustParentCountry() {
		return CustParentCountry;
	}

	public void setCustParentCountry(String custParentCountry) {
		CustParentCountry = custParentCountry;
	}

	public String getCustResdCountry() {
		return CustResdCountry;
	}

	public void setCustResdCountry(String custResdCountry) {
		CustResdCountry = custResdCountry;
	}

	public String getCustRiskCountry() {
		return CustRiskCountry;
	}

	public void setCustRiskCountry(String custRiskCountry) {
		CustRiskCountry = custRiskCountry;
	}

	public String getCustNationality() {
		return CustNationality;
	}

	public void setCustNationality(String custNationality) {
		CustNationality = custNationality;
	}

	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerSubHeadRuleMap = new HashMap<String, Object>();
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				customerSubHeadRuleMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerSubHeadRuleMap;
	}

	public HashMap<String, String> getDeclaredFieldsTypes() {
		HashMap<String, String> customerEligibityMap = new HashMap<String, String>();
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				String Type=this.getClass().getDeclaredFields()[i].getType().toString();
				customerEligibityMap.put(this.getClass().getDeclaredFields()[i].getName(),Type );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerEligibityMap;
	}


}
