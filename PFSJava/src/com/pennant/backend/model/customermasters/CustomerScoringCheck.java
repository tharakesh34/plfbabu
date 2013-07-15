package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;


public class CustomerScoringCheck {
	private int custAge;	
	private String custCIF;	
	private String custCtgCode;
	private String custTypeCode;
	private String custDftBranch;
	private String custGenderCode;
	private Date custDOB;
	private String custCOB;
	private boolean custIsMinor;
	private long custGroupID;
	private String custSts;
	private String custGroupSts;
	private boolean custIsBlocked;
	private boolean custIsActive;
	private boolean custIsClosed;	
	private boolean custIsDecease;
	private boolean custIsDormant;
	private boolean custIsDelinquent;
	private boolean custIsTradeFinCust;
	private boolean custIsStaff;
	private String custIndustry;
	private String custSector;
	private String custSubSector;
	private String custProfession;
	private BigDecimal custTotalIncome;
	private String custMaritalSts;
	private String custEmpSts;
	private String custSegment;
	private String custSubSegment;
	private boolean custIsBlackListed;
	private boolean custIsRejected;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;
	private String custNationality;	
	private int custLiveFinCount;
	private BigDecimal custLiveFinAmount;	
	private BigDecimal custRepayBank;
	private BigDecimal custRepayOther;
	private BigDecimal custRepayTot;
	private BigDecimal custPastDueCount;
	private BigDecimal custPastDueAmt;
	//Not in eligibility
	private String custEmpDesg;
	private BigDecimal custFinAmountBank;
	private BigDecimal custFinAmountOther;	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}
	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustTypeCode() {
		return custTypeCode;
	}
	public void setCustTypeCode(String custTypeCode) {
		this.custTypeCode = custTypeCode;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}
	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustGenderCode() {
		return custGenderCode;
	}
	public void setCustGenderCode(String custGenderCode) {
		this.custGenderCode = custGenderCode;
	}

	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public int getCustAge() {
		return custAge;
	}
	public void setCustAge(int custAge) {
		this.custAge = custAge;
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

	public long getCustGroupID() {
		return custGroupID;
	}
	public void setCustGroupID(long custGroupID) {
		this.custGroupID = custGroupID;
	}

	public String getCustSts() {
		return custSts;
	}
	public void setCustSts(String custSts) {
		this.custSts = custSts;
	}

	public String getCustGroupSts() {
		return custGroupSts;
	}
	public void setCustGroupSts(String custGroupSts) {
		this.custGroupSts = custGroupSts;
	}

	public boolean isCustIsBlocked() {
		return custIsBlocked;
	}
	public void setCustIsBlocked(boolean custIsBlocked) {
		this.custIsBlocked = custIsBlocked;
	}

	public boolean isCustIsActive() {
		return custIsActive;
	}
	public void setCustIsActive(boolean custIsActive) {
		this.custIsActive = custIsActive;
	}

	public boolean isCustIsClosed() {
		return custIsClosed;
	}
	public void setCustIsClosed(boolean custIsClosed) {
		this.custIsClosed = custIsClosed;
	}

	public boolean isCustIsDecease() {
		return custIsDecease;
	}
	public void setCustIsDecease(boolean custIsDecease) {
		this.custIsDecease = custIsDecease;
	}

	public boolean isCustIsDormant() {
		return custIsDormant;
	}
	public void setCustIsDormant(boolean custIsDormant) {
		this.custIsDormant = custIsDormant;
	}

	public boolean isCustIsDelinquent() {
		return custIsDelinquent;
	}
	public void setCustIsDelinquent(boolean custIsDelinquent) {
		this.custIsDelinquent = custIsDelinquent;
	}

	public boolean isCustIsTradeFinCust() {
		return custIsTradeFinCust;
	}
	public void setCustIsTradeFinCust(boolean custIsTradeFinCust) {
		this.custIsTradeFinCust = custIsTradeFinCust;
	}

	public boolean isCustIsStaff() {
		return custIsStaff;
	}
	public void setCustIsStaff(boolean custIsStaff) {
		this.custIsStaff = custIsStaff;
	}

	public String getCustIndustry() {
		return custIndustry;
	}
	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
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

	public String getCustProfession() {
		return custProfession;
	}
	public void setCustProfession(String custProfession) {
		this.custProfession = custProfession;
	}

	public BigDecimal getCustTotalIncome() {
		return custTotalIncome;
	}
	public void setCustTotalIncome(BigDecimal custTotalIncome) {
		this.custTotalIncome = custTotalIncome;
	}

	public String getCustMaritalSts() {
		return custMaritalSts;
	}
	public void setCustMaritalSts(String custMaritalSts) {
		this.custMaritalSts = custMaritalSts;
	}

	public String getCustEmpSts() {
		return custEmpSts;
	}
	public void setCustEmpSts(String custEmpSts) {
		this.custEmpSts = custEmpSts;
	}

	public String getCustSegment() {
		return custSegment;
	}
	public void setCustSegment(String custSegment) {
		this.custSegment = custSegment;
	}

	public String getCustSubSegment() {
		return custSubSegment;
	}
	public void setCustSubSegment(String custSubSegment) {
		this.custSubSegment = custSubSegment;
	}

	public boolean isCustIsBlackListed() {
		return custIsBlackListed;
	}
	public void setCustIsBlackListed(boolean custIsBlackListed) {
		this.custIsBlackListed = custIsBlackListed;
	}

	public boolean isCustIsRejected() {
		return custIsRejected;
	}
	public void setCustIsRejected(boolean custIsRejected) {
		this.custIsRejected = custIsRejected;
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

	public String getCustNationality() {
		return custNationality;
	}
	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public int getCustLiveFinCount() {
		return custLiveFinCount;
	}
	public void setCustLiveFinCount(int custLiveFinCount) {
		this.custLiveFinCount = custLiveFinCount;
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

	public BigDecimal getCustRepayTot() {
		return custRepayTot;
	}
	public void setCustRepayTot(BigDecimal custRepayTot) {
		this.custRepayTot = custRepayTot;
	}

	public BigDecimal getCustFinAmountBank() {
		return custFinAmountBank;
	}
	public void setCustFinAmountBank(BigDecimal custFinAmountBank) {
		this.custFinAmountBank = custFinAmountBank;
	}

	public BigDecimal getCustFinAmountOther() {
		return custFinAmountOther;
	}
	public void setCustFinAmountOther(BigDecimal custFinAmountOther) {
		this.custFinAmountOther = custFinAmountOther;
	}

	public BigDecimal getCustPastDueCount() {
		return custPastDueCount;
	}
	public void setCustPastDueCount(BigDecimal custPastDueCount) {
		this.custPastDueCount = custPastDueCount;
	}

	public BigDecimal getCustPastDueAmt() {
		return custPastDueAmt;
	}
	public void setCustPastDueAmt(BigDecimal custPastDueAmt) {
		this.custPastDueAmt = custPastDueAmt;
	}

	public String getCustEmpDesg() {
		return custEmpDesg;
	}
	public void setCustEmpDesg(String custEmpDesg) {
		this.custEmpDesg = custEmpDesg;
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
