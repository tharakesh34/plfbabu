package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import com.pennant.app.util.DateUtility;

public class CustomerEligibilityCheck implements Serializable {

    private static final long serialVersionUID = -2098118727197998806L;
    
	private int custAge = 0;
	private Date custDOB = DateUtility.getUtilDate();
	private String custCOB = "";
	private String custEmpDesg = "";
	private String custEmpAloc = "";
	private String custEmpSts = "";
	private String custGenderCode = "";
	private boolean custIsBlackListed = false;
	private boolean custIsMinor = false;
	private String custMaritalSts ="";
	private int noOfDependents = 0;
	private String custNationality = "";
	private String custSector = "";
	private String custSubSector = "";
	private String custCtgType = "";
	private String reqProduct = "";
	private String custWorstSts = "";
	private String custTypeCode = "";
	
	private BigDecimal tenure = BigDecimal.ZERO;
	private int blackListExpPeriod = 0;
	
	private BigDecimal reqFinAmount = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custLiveFinAmount = BigDecimal.ZERO;
	private BigDecimal custPastDueAmt = BigDecimal.ZERO;
	private BigDecimal curFinRepayAmt = BigDecimal.ZERO;
	private BigDecimal DSCR = BigDecimal.ZERO;
	
	/*private String custCIF;	
	private String custCtgCode;
	private String custDftBranch;
	private long custGroupID = 0;
	private String custGroupSts;
	private boolean custIsBlocked = false;
	private boolean custIsActive = false;
	private boolean custIsClosed = false;
	private boolean custIsDecease = false;
	private boolean custIsDormant = false;
	private boolean custIsDelinquent = false;
	private boolean custIsTradeFinCust = false;
	private boolean custIsStaff = false;
	private String custIndustry;
	private String custProfession;
	private boolean custIsRejected = false;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;

	private String reqFinType;
	private Date reqFinStartDate;
	private Date reqMaturity;
	private String reqFinccy;
	
	private String reqCampaign;
	private int reqTerms = 0;
	private int custLiveFinCount = 0;
	private int custReqFtpCount = 0;
	private BigDecimal reqFinAmount = BigDecimal.ZERO;
	private BigDecimal reqFinRepay = BigDecimal.ZERO;
	private BigDecimal custPastDueCount = BigDecimal.ZERO;
	private BigDecimal custReqFtpAmount = BigDecimal.ZERO;
	private BigDecimal reqFinAssetVal = BigDecimal.ZERO;
	private BigDecimal reqPftRate = BigDecimal.ZERO;
	
	private BigDecimal  custPDHist30D = BigDecimal.ZERO;
	private BigDecimal  custPDHist60D = BigDecimal.ZERO;
	private BigDecimal  custPDHist90D = BigDecimal.ZERO;
	private BigDecimal  custPDHist120D = BigDecimal.ZERO;
	private BigDecimal  custPDHist180D = BigDecimal.ZERO;
	private BigDecimal  custPDHist180DP = BigDecimal.ZERO;
	private BigDecimal  custPDLive30D = BigDecimal.ZERO;
	private BigDecimal  custPDLive60D = BigDecimal.ZERO;
	private BigDecimal  custPDLive90D = BigDecimal.ZERO;
	private BigDecimal  custPDLive120D = BigDecimal.ZERO;
	private BigDecimal  custPDLive180D = BigDecimal.ZERO;
	private BigDecimal  custPDLive180DP = BigDecimal.ZERO;*/

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public int getCustAge() {
    	return custAge;
    }
	public void setCustAge(int custAge) {
    	this.custAge = custAge;
    }

	public Date getCustDOB() {
    	return custDOB;
    }
	public void setCustDOB(Date custDOB) {
    	this.custDOB = custDOB;
    }

	public String getCustCOB() {
    	return custCOB;
    }
	public void setCustCOB(String custCOB) {
    	this.custCOB = custCOB;
    }

	public String getCustEmpDesg() {
    	return custEmpDesg;
    }
	public void setCustEmpDesg(String custEmpDesg) {
    	this.custEmpDesg = custEmpDesg;
    }

	public String getCustEmpAloc() {
    	return custEmpAloc;
    }
	public void setCustEmpAloc(String custEmpAloc) {
    	this.custEmpAloc = custEmpAloc;
    }
	
	public String getCustEmpSts() {
    	return custEmpSts;
    }
	public void setCustEmpSts(String custEmpSts) {
    	this.custEmpSts = custEmpSts;
    }

	public BigDecimal getCustRepayOther() {
    	return custRepayOther;
    }
	public void setCustRepayOther(BigDecimal custRepayOther) {
    	this.custRepayOther = custRepayOther;
    }

	public BigDecimal getCustRepayBank() {
    	return custRepayBank;
    }
	public void setCustRepayBank(BigDecimal custRepayBank) {
    	this.custRepayBank = custRepayBank;
    }

	public BigDecimal getCustTotalExpense() {
    	return custTotalExpense;
    }
	public void setCustTotalExpense(BigDecimal custTotalExpense) {
    	this.custTotalExpense = custTotalExpense;
    }
	
	public String getCustGenderCode() {
    	return custGenderCode;
    }
	public void setCustGenderCode(String custGenderCode) {
    	this.custGenderCode = custGenderCode;
    }

	public boolean isCustIsBlackListed() {
    	return custIsBlackListed;
    }
	public void setCustIsBlackListed(boolean custIsBlackListed) {
    	this.custIsBlackListed = custIsBlackListed;
    }

	public boolean isCustIsMinor() {
    	return custIsMinor;
    }
	public void setCustIsMinor(boolean custIsMinor) {
    	this.custIsMinor = custIsMinor;
    }

	public BigDecimal getCustLiveFinAmount() {
    	return custLiveFinAmount;
    }
	public void setCustLiveFinAmount(BigDecimal custLiveFinAmount) {
    	this.custLiveFinAmount = custLiveFinAmount;
    }

	public String getCustMaritalSts() {
    	return custMaritalSts;
    }
	public void setCustMaritalSts(String custMaritalSts) {
    	this.custMaritalSts = custMaritalSts;
    }

	public int getNoOfDependents() {
    	return noOfDependents;
    }
	public void setNoOfDependents(int noOfDependents) {
    	this.noOfDependents = noOfDependents;
    }
	
	public String getCustNationality() {
    	return custNationality;
    }
	public void setCustNationality(String custNationality) {
    	this.custNationality = custNationality;
    }

	public BigDecimal getCustPastDueAmt() {
    	return custPastDueAmt;
    }
	public void setCustPastDueAmt(BigDecimal custPastDueAmt) {
    	this.custPastDueAmt = custPastDueAmt;
    }

	public BigDecimal getCustTotalIncome() {
    	return custTotalIncome;
    }
	public void setCustTotalIncome(BigDecimal custTotalIncome) {
    	this.custTotalIncome = custTotalIncome;
    }

	public String getReqProduct() {
    	return reqProduct;
    }
	public void setReqProduct(String reqProduct) {
    	this.reqProduct = reqProduct;
    }

	public String getCustWorstSts() {
    	return custWorstSts;
    }
	public void setCustWorstSts(String custWorstSts) {
    	this.custWorstSts = custWorstSts;
    }
	
	public String getCustSector() {
    	return custSector;
    }
	public void setCustSector(String custSector) {
    	this.custSector = custSector;
    }

	public String getCustSubSector() {
    	return custSubSector;
    }
	public void setCustSubSector(String custSubSector) {
    	this.custSubSector = custSubSector;
    }

	public String getCustCtgType() {
    	return custCtgType;
    }
	public void setCustCtgType(String custCtgType) {
    	this.custCtgType = custCtgType;
    }
	
	public BigDecimal getCurFinRepayAmt() {
    	return curFinRepayAmt;
    }
	public void setCurFinRepayAmt(BigDecimal curFinRepayAmt) {
    	this.curFinRepayAmt = curFinRepayAmt;
    }
	
	public BigDecimal getDSCR() {
    	return DSCR;
    }
	public void setDSCR(BigDecimal dSCR) {
    	DSCR = dSCR;
    }
	
	public BigDecimal getReqFinAmount() {
    	return reqFinAmount;
    }
	public void setReqFinAmount(BigDecimal reqFinAmount) {
    	this.reqFinAmount = reqFinAmount;
    }
	
	public BigDecimal getTenure() {
    	return tenure;
    }
	public void setTenure(BigDecimal tenure) {
    	this.tenure = tenure;
    }
	
	public int getBlackListExpPeriod() {
    	return blackListExpPeriod;
    }
	public void setBlackListExpPeriod(int blackListExpPeriod) {
    	this.blackListExpPeriod = blackListExpPeriod;
    }
	
	public void setCustTypeCode(String custTypeCode) {
	    this.custTypeCode = custTypeCode;
    }
	public String getCustTypeCode() {
	    return custTypeCode;
    }
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerEligibityMap = new HashMap<String, Object>();

		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				customerEligibityMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerEligibityMap;
	}
	
}
