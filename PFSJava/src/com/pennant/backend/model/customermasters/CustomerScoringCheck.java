package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class CustomerScoringCheck implements Serializable {
	
    private static final long serialVersionUID = 2679603084860727800L;
    
	private int custAge = 0;	
	private Date custDOB;
	private String custCOB;
	private boolean custIsMinor = false;
	private String custGenderCode;
	private String custSector;
	private String custSubSector;
	private String custMaritalSts;
	private int noOfDependents = 0;
	private String custEmpSts;
	private String custEmpDesg;
	private String custEmpAloc = "";
	private boolean custIsBlackListed = false;
	private String custNationality;
	private String custWorstSts = "";
	private String custTypeCode;
	
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custLiveFinAmount = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custPastDueAmt = BigDecimal.ZERO;
	
	private BigDecimal tenure = BigDecimal.ZERO;
	private int blackListExpPeriod = 0;
	private BigDecimal reqFinAmount = BigDecimal.ZERO;
	private BigDecimal DSCR = BigDecimal.ZERO;
	
	/*private String custCIF;	
	private String custCtgCode;
	private String custTypeCode;
	private String custDftBranch;
	private long custGroupID = 0;
	private String custSts;
	private String custGroupSts;
	private boolean custIsActive = false;
	private boolean custIsBlocked = false;
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
	private int custLiveFinCount = 0;
	private BigDecimal custPastDueCount = BigDecimal.ZERO;
	
	//Not in eligibility
	private BigDecimal custFinAmountBank = BigDecimal.ZERO;
	private BigDecimal custFinAmountOther = BigDecimal.ZERO;*/

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

	public boolean isCustIsMinor() {
    	return custIsMinor;
    }
	public void setCustIsMinor(boolean custIsMinor) {
    	this.custIsMinor = custIsMinor;
    }

	public String getCustGenderCode() {
    	return custGenderCode;
    }
	public void setCustGenderCode(String custGenderCode) {
    	this.custGenderCode = custGenderCode;
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
	
	public String getCustEmpSts() {
    	return custEmpSts;
    }
	public void setCustEmpSts(String custEmpSts) {
    	this.custEmpSts = custEmpSts;
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

	public boolean isCustIsBlackListed() {
    	return custIsBlackListed;
    }
	public void setCustIsBlackListed(boolean custIsBlackListed) {
    	this.custIsBlackListed = custIsBlackListed;
    }

	public String getCustNationality() {
    	return custNationality;
    }
	public void setCustNationality(String custNationality) {
    	this.custNationality = custNationality;
    }

	public String getCustWorstSts() {
    	return custWorstSts;
    }
	public void setCustWorstSts(String custWorstSts) {
    	this.custWorstSts = custWorstSts;
    }
	
	public BigDecimal getCustTotalIncome() {
    	return custTotalIncome;
    }
	public void setCustTotalIncome(BigDecimal custTotalIncome) {
    	this.custTotalIncome = custTotalIncome;
    }

	public BigDecimal getCustTotalExpense() {
    	return custTotalExpense;
    }
	public void setCustTotalExpense(BigDecimal custTotalExpense) {
    	this.custTotalExpense = custTotalExpense;
    }

	public BigDecimal getCustLiveFinAmount() {
    	return custLiveFinAmount;
    }
	public void setCustLiveFinAmount(BigDecimal custLiveFinAmount) {
    	this.custLiveFinAmount = custLiveFinAmount;
    }

	public BigDecimal getCustRepayBank() {
    	return custRepayBank;
    }
	public void setCustRepayBank(BigDecimal custRepayBank) {
    	this.custRepayBank = custRepayBank;
    }

	public BigDecimal getCustRepayOther() {
    	return custRepayOther;
    }
	public void setCustRepayOther(BigDecimal custRepayOther) {
    	this.custRepayOther = custRepayOther;
    }

	public BigDecimal getCustPastDueAmt() {
    	return custPastDueAmt;
    }
	public void setCustPastDueAmt(BigDecimal custPastDueAmt) {
    	this.custPastDueAmt = custPastDueAmt;
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
	
	public BigDecimal getReqFinAmount() {
    	return reqFinAmount;
    }
	public void setReqFinAmount(BigDecimal reqFinAmount) {
    	this.reqFinAmount = reqFinAmount;
    }
	
	public BigDecimal getDSCR() {
    	return DSCR;
    }
	public void setDSCR(BigDecimal dSCR) {
    	DSCR = dSCR;
    }
	
	public void setCustTypeCode(String custTypeCode) {
	    this.custTypeCode = custTypeCode;
    }
	public String getCustTypeCode() {
	    return custTypeCode;
    }

	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerScoringMap = new HashMap<String, Object>();	
		try {
			for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
				customerScoringMap.put(this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerScoringMap;
	}
	
}
