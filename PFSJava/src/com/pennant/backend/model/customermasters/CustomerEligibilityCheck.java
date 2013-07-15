package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;


public class CustomerEligibilityCheck {

	private int custAge = 0;
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
	private BigDecimal custTotalIncome = new BigDecimal(0);
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

	private String reqFinType;
	private Date reqFinStartDate;
	private Date reqMaturity;
	private String reqFinccy;
	private String reqProduct;
	private String reqCampaign;
	private int reqTerms = 0;
	private int custLiveFinCount = 0;
	private int custReqFtpCount = 0;
	private BigDecimal reqFinAmount = new BigDecimal(0);
	private BigDecimal reqFinRepay = new BigDecimal(0);
	private BigDecimal custLiveFinAmount = new BigDecimal(0);
	private BigDecimal custReqFtpAmount = new BigDecimal(0);
	private BigDecimal custRepayBank = new BigDecimal(0);
	private BigDecimal custRepayOther = new BigDecimal(0);
	private BigDecimal custRepayTot = new BigDecimal(0);
	private BigDecimal reqFinAssetVal = new BigDecimal(0);
	private BigDecimal reqPftRate = new BigDecimal(0);
	
	
	private BigDecimal custPastDueCount;
	private BigDecimal custPastDueAmt;
	private BigDecimal  custPDHist30D;
	private BigDecimal  custPDHist60D;
	private BigDecimal  custPDHist90D;
	private BigDecimal  custPDHist120D;
	private BigDecimal  custPDHist180D;
	private BigDecimal  custPDHist180DP;
	private BigDecimal  custPDLive30D;
	private BigDecimal  custPDLive60D;
	private BigDecimal  custPDLive90D;
	private BigDecimal  custPDLive120D;
	private BigDecimal  custPDLive180D;
	private BigDecimal  custPDLive180DP;

	
	public int getCustAge() {
		return custAge;
	}
	public void setCustAge(int custAge) {
		this.custAge = custAge;
	}

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


	public String getReqFinType() {
		return reqFinType;
	}


	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
	}


	public Date getReqFinStartDate() {
		return reqFinStartDate;
	}


	public void setReqFinStartDate(Date reqFinStartDate) {
		this.reqFinStartDate = reqFinStartDate;
	}


	public Date getReqMaturity() {
		return reqMaturity;
	}


	public void setReqMaturity(Date reqMaturity) {
		this.reqMaturity = reqMaturity;
	}


	public String getReqFinccy() {
		return reqFinccy;
	}


	public void setReqFinccy(String reqFinccy) {
		this.reqFinccy = reqFinccy;
	}


	public String getReqProduct() {
		return reqProduct;
	}


	public void setReqProduct(String reqProduct) {
		this.reqProduct = reqProduct;
	}


	public String getReqCampaign() {
		return reqCampaign;
	}


	public void setReqCampaign(String reqCampaign) {
		this.reqCampaign = reqCampaign;
	}


	public int getReqTerms() {
		return reqTerms;
	}


	public void setReqTerms(int reqTerms) {
		this.reqTerms = reqTerms;
	}


	public int getCustLiveFinCount() {
		return custLiveFinCount;
	}


	public void setCustLiveFinCount(int custLiveFinCount) {
		this.custLiveFinCount = custLiveFinCount;
	}


	public int getCustReqFtpCount() {
		return custReqFtpCount;
	}


	public void setCustReqFtpCount(int custReqFtpCount) {
		this.custReqFtpCount = custReqFtpCount;
	}


	public BigDecimal getReqFinAmount() {
		return reqFinAmount;
	}


	public void setReqFinAmount(BigDecimal reqFinAmount) {
		this.reqFinAmount = reqFinAmount;
	}


	public BigDecimal getReqFinRepay() {
		return reqFinRepay;
	}


	public void setReqFinRepay(BigDecimal reqFinRepay) {
		this.reqFinRepay = reqFinRepay;
	}


	public BigDecimal getCustLiveFinAmount() {
		return custLiveFinAmount;
	}


	public void setCustLiveFinAmount(BigDecimal custLiveFinAmount) {
		this.custLiveFinAmount = custLiveFinAmount;
	}


	public BigDecimal getCustReqFtpAmount() {
		return custReqFtpAmount;
	}


	public void setCustReqFtpAmount(BigDecimal custReqFtpAmount) {
		this.custReqFtpAmount = custReqFtpAmount;
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


	public BigDecimal getReqFinAssetVal() {
		return reqFinAssetVal;
	}


	public void setReqFinAssetVal(BigDecimal reqFinAssetVal) {
		this.reqFinAssetVal = reqFinAssetVal;
	}


	public BigDecimal getReqPftRate() {
		return reqPftRate;
	}


	public void setReqPftRate(BigDecimal reqPftRate) {
		this.reqPftRate = reqPftRate;
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


	public BigDecimal getCustPDHist30D() {
		return custPDHist30D;
	}


	public void setCustPDHist30D(BigDecimal custPDHist30D) {
		this.custPDHist30D = custPDHist30D;
	}


	public BigDecimal getCustPDHist60D() {
		return custPDHist60D;
	}


	public void setCustPDHist60D(BigDecimal custPDHist60D) {
		this.custPDHist60D = custPDHist60D;
	}


	public BigDecimal getCustPDHist90D() {
		return custPDHist90D;
	}


	public void setCustPDHist90D(BigDecimal custPDHist90D) {
		this.custPDHist90D = custPDHist90D;
	}


	public BigDecimal getCustPDHist120D() {
		return custPDHist120D;
	}


	public void setCustPDHist120D(BigDecimal custPDHist120D) {
		this.custPDHist120D = custPDHist120D;
	}


	public BigDecimal getCustPDHist180D() {
		return custPDHist180D;
	}


	public void setCustPDHist180D(BigDecimal custPDHist180D) {
		this.custPDHist180D = custPDHist180D;
	}


	public BigDecimal getCustPDHist180DP() {
		return custPDHist180DP;
	}


	public void setCustPDHist180DP(BigDecimal custPDHist180DP) {
		this.custPDHist180DP = custPDHist180DP;
	}


	public BigDecimal getCustPDLive30D() {
		return custPDLive30D;
	}


	public void setCustPDLive30D(BigDecimal custPDLive30D) {
		this.custPDLive30D = custPDLive30D;
	}


	public BigDecimal getCustPDLive60D() {
		return custPDLive60D;
	}


	public void setCustPDLive60D(BigDecimal custPDLive60D) {
		this.custPDLive60D = custPDLive60D;
	}


	public BigDecimal getCustPDLive90D() {
		return custPDLive90D;
	}


	public void setCustPDLive90D(BigDecimal custPDLive90D) {
		this.custPDLive90D = custPDLive90D;
	}


	public BigDecimal getCustPDLive120D() {
		return custPDLive120D;
	}


	public void setCustPDLive120D(BigDecimal custPDLive120D) {
		this.custPDLive120D = custPDLive120D;
	}


	public BigDecimal getCustPDLive180D() {
		return custPDLive180D;
	}


	public void setCustPDLive180D(BigDecimal custPDLive180D) {
		this.custPDLive180D = custPDLive180D;
	}


	public BigDecimal getCustPDLive180DP() {
		return custPDLive180DP;
	}


	public void setCustPDLive180DP(BigDecimal custPDLive180DP) {
		this.custPDLive180DP = custPDLive180DP;
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
