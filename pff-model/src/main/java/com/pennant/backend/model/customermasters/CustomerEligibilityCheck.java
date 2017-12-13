package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class CustomerEligibilityCheck implements Serializable {

    private static final long serialVersionUID = -2098118727197998806L;
    
	private BigDecimal custAge = BigDecimal.ZERO;
	private Date custDOB;
	private String custEmpDesg = "";
	private String custEmpSts = "";
	private String custEmpSector = "";
	private String custGenderCode = "";
	private boolean custIsBlackListed = false;
	private boolean salariedCustomer = false;
	private boolean custIsMinor = false;
	private String custMaritalSts ="";
	private int noOfDependents = 0;
	private String custNationality = "";
	private String reqProduct = "";
	private String custWorstSts = "";
	private String custTypeCode = "";
	private String custCtgCode;
	private String custIndustry;
	private String custOtherIncome;
	private BigDecimal custOtherIncomeAmt = BigDecimal.ZERO;
	
	private String sellerType;
	private String vehicleCtg;
	private String emiratesReg;
	private boolean thirdPartyReg = false;
	private String vehicleFinFor;
	private String agreeName;
	private String assetProduct;
	private String propertyCategory;
	private String assetPurpose;
	private boolean approvedDealerExists;
	private boolean nonApprovedDealerExists;
	
	private int noOfTerms;
	private String finRepayMethod;
	private boolean stepFinance;
	private boolean alwDPSP;
	private boolean alwPlannedDefer;
	
	private BigDecimal tenure = BigDecimal.ZERO;
	private int blackListExpPeriod = 0;
	
	private BigDecimal reqFinAmount = BigDecimal.ZERO;
	private BigDecimal downpayBank = BigDecimal.ZERO;
	private BigDecimal downpaySupl = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custProcRepayBank = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custMonthlyIncome = BigDecimal.ZERO;
	private BigDecimal custLiveFinAmount = BigDecimal.ZERO;
	private BigDecimal custPastDueAmt = BigDecimal.ZERO;
	private BigDecimal curFinRepayAmt = BigDecimal.ZERO;
	private BigDecimal finProfitRate = BigDecimal.ZERO;
	private BigDecimal DSCR = BigDecimal.ZERO;
	private BigDecimal finValueRatio = BigDecimal.ZERO;
	private String 	   reqFinCcy;
	private String 	   custEmpName = "";
	
	private BigDecimal coAppRepayBank = BigDecimal.ZERO;
	private BigDecimal coAppIncome = BigDecimal.ZERO;
	private BigDecimal coAppExpense = BigDecimal.ZERO;
	private BigDecimal coAppCurFinEMI = BigDecimal.ZERO;
	
	private BigDecimal custYearOfExp = BigDecimal.ZERO;
	private boolean lpoReissue;
	private String reqFinPurpose;
	private String propertyType = "";
	private String propertyDetail = "";
	private String financingType = "";
	private String productType = "";
	private int graceTenure = 0;
	private String custSector = "";
	private String custEmpAloc = "";
	
	private String buildSts;
	private String ConstructStage;
	private String usage;
	private String mortgageSts;
	private String mainCollateralType;
	private String reqFinType;

	private boolean ddaModifiedCheck;
	private BigDecimal refundAmount; 
	private String custEmpType;
	private BigDecimal currentAssetValue = BigDecimal.ZERO; 
	private BigDecimal installmentAmount = BigDecimal.ZERO;
	private BigDecimal foir = BigDecimal.ZERO;
	private BigDecimal ltv = BigDecimal.ZERO;
	private BigDecimal disbursedAmount = BigDecimal.ZERO;
	/*private String custCIF;	
	private String custSubSector = "";
	private String custCOB = "";
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
	private String custProfession;
	private boolean custIsRejected = false;
	private String custParentCountry;
	private String custResdCountry;
	private String custRiskCountry;

	private String reqFinType;
	private Date reqFinStartDate;
	private Date reqMaturity;
	
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

	public CustomerEligibilityCheck() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public BigDecimal getCustAge() {
    	return custAge;
    }
	public void setCustAge(BigDecimal custAge) {
    	this.custAge = custAge;
    }

	public Date getCustDOB() {
    	return custDOB;
    }
	public void setCustDOB(Date custDOB) {
    	this.custDOB = custDOB;
    }

	public String getCustEmpDesg() {
    	return custEmpDesg;
    }
	public void setCustEmpDesg(String custEmpDesg) {
    	this.custEmpDesg = custEmpDesg;
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

	public BigDecimal getCustMonthlyIncome() {
		return custMonthlyIncome;
	}
	public void setCustMonthlyIncome(BigDecimal custMonthlyIncome) {
		this.custMonthlyIncome = custMonthlyIncome;
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
	
	public BigDecimal getFinValueRatio() {
		return finValueRatio;
	}
	public void setFinValueRatio(BigDecimal finValueRatio) {
		this.finValueRatio = finValueRatio;
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
	
	public String getCustCtgCode() {
	    return custCtgCode;
    }
	public void setCustCtgCode(String custCtgCode) {
	    this.custCtgCode = custCtgCode;
    }
	
	public String getSellerType() {
	    return sellerType;
    }
	public void setSellerType(String sellerType) {
	    this.sellerType = sellerType;
    }
	
	public boolean isThirdPartyReg() {
	    return thirdPartyReg;
    }
	public void setThirdPartyReg(boolean thirdPartyReg) {
	    this.thirdPartyReg = thirdPartyReg;
    }
	
	public BigDecimal getFinProfitRate() {
	    return finProfitRate;
    }
	public void setFinProfitRate(BigDecimal finProfitRate) {
	    this.finProfitRate = finProfitRate;
    }
	
	public boolean isStepFinance() {
		return stepFinance;
	}
	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}
	
	public boolean isAlwDPSP() {
		return alwDPSP;
	}
	public void setAlwDPSP(boolean alwDPSP) {
		this.alwDPSP = alwDPSP;
	}
	
	public boolean isAlwPlannedDefer() {
		return alwPlannedDefer;
	}
	public void setAlwPlannedDefer(boolean alwPlannedDefer) {
		this.alwPlannedDefer = alwPlannedDefer;
	}
	
	public int getNoOfTerms() {
	    return noOfTerms;
    }
	public void setNoOfTerms(int noOfTerms) {
	    this.noOfTerms = noOfTerms;
    }
	
	public String getCustEmpSector() {
	    return custEmpSector;
    }
	public void setCustEmpSector(String custEmpSector) {
	    this.custEmpSector = custEmpSector;
    }
	
	public String getFinRepayMethod() {
	    return finRepayMethod;
    }
	public void setFinRepayMethod(String finRepayMethod) {
	    this.finRepayMethod = finRepayMethod;
    }
	
	public BigDecimal getDownpayBank() {
	    return downpayBank;
    }
	public void setDownpayBank(BigDecimal downpayBank) {
	    this.downpayBank = downpayBank;
    }
	
	public BigDecimal getDownpaySupl() {
	    return downpaySupl;
    }
	public void setDownpaySupl(BigDecimal downpaySupl) {
	    this.downpaySupl = downpaySupl;
    }
	
	public String getVehicleCtg() {
	    return vehicleCtg;
    }
	public void setVehicleCtg(String vehicleCtg) {
	    this.vehicleCtg = vehicleCtg;
    }

	public String getEmiratesReg() {
	    return emiratesReg;
    }
	public void setEmiratesReg(String emiratesReg) {
	    this.emiratesReg = emiratesReg;
    }
	
	public BigDecimal getCustYearOfExp() {
		return custYearOfExp;
	}
	public void setCustYearOfExp(BigDecimal custYearOfExp) {
		this.custYearOfExp = custYearOfExp;
	}

	public boolean isSalariedCustomer() {
	    return salariedCustomer;
    }
	public void setSalariedCustomer(boolean salariedCustomer) {
	    this.salariedCustomer = salariedCustomer;
    }
	public String getReqFinCcy() {
	    return reqFinCcy;
    }
	public void setReqFinCcy(String reqFinCcy) {
	    this.reqFinCcy = reqFinCcy;
    }
	
	public String getCustEmpName() {
		return custEmpName;
	}
	public void setCustEmpName(String custEmpName) {
		this.custEmpName = custEmpName;
	}

	public String getCustOtherIncome() {
	    return custOtherIncome;
    }
	public void setCustOtherIncome(String custOtherIncome) {
	    this.custOtherIncome = custOtherIncome;
    }
	
	public boolean isLpoReissue() {
	    return lpoReissue;
    }
	public void setLpoReissue(boolean lpoReissue) {
	    this.lpoReissue = lpoReissue;
    }
	public String getReqFinPurpose() {
	    return reqFinPurpose;
    }
	public void setReqFinPurpose(String reqFinPurpose) {
	    this.reqFinPurpose = reqFinPurpose;
    }
	
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getPropertyDetail() {
		return propertyDetail;
	}
	public void setPropertyDetail(String propertyDetail) {
		this.propertyDetail = propertyDetail;
	}

	public String getFinancingType() {
		return financingType;
	}
	public void setFinancingType(String financingType) {
		this.financingType = financingType;
	}

	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

	public int getGraceTenure() {
		return graceTenure;
	}
	public void setGraceTenure(int graceTenure) {
		this.graceTenure = graceTenure;
	}

	public String getCustSector() {
		return custSector;
	}
	public void setCustSector(String custSector) {
		this.custSector = custSector;
	}

	public String getCustEmpAloc() {
	    return custEmpAloc;
    }
	public void setCustEmpAloc(String custEmpAloc) {
	    this.custEmpAloc = custEmpAloc;
    }
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> customerEligibityMap = new HashMap<String, Object>();

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				customerEligibityMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return customerEligibityMap;
	}

	public String getBuildSts() {
	    return buildSts;
    }

	public void setBuildSts(String buildSts) {
	    this.buildSts = buildSts;
    }

	
	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getMortgageSts() {
	    return mortgageSts;
    }

	public void setMortgageSts(String mortgageSts) {
	    this.mortgageSts = mortgageSts;
    }

	public String getMainCollateralType() {
		return mainCollateralType;
	}
	public void setMainCollateralType(String mainCollateralType) {
		this.mainCollateralType = mainCollateralType;
	}

	public String getConstructStage() {
	    return ConstructStage;
    }

	public void setConstructStage(String constructStage) {
	    ConstructStage = constructStage;
    }

	public String getVehicleFinFor() {
	    return vehicleFinFor;
    }

	public void setVehicleFinFor(String vehicleFinFor) {
	    this.vehicleFinFor = vehicleFinFor;
    }

	public String getAgreeName() {
		return agreeName;
	}

	public void setAgreeName(String agreeName) {
		this.agreeName = agreeName;
	}

	public String getReqFinType() {
		return reqFinType;
	}

	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
	}

	public String getPropertyCategory() {
		return propertyCategory;
	}
	public void setPropertyCategory(String propertyCategory) {
		this.propertyCategory = propertyCategory;
	}

	public boolean isDdaModifiedCheck() {
		return ddaModifiedCheck;
	}
	public void setDdaModifiedCheck(boolean ddaModifiedCheck) {
		this.ddaModifiedCheck = ddaModifiedCheck;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getAssetProduct() {
		return assetProduct;
	}
	public void setAssetProduct(String assetProduct) {
		this.assetProduct = assetProduct;
	}

	public String getAssetPurpose() {
		return assetPurpose;
	}
	public void setAssetPurpose(String assetPurpose) {
		this.assetPurpose = assetPurpose;
	}

	public boolean isApprovedDealerExists() {
		return approvedDealerExists;
	}
	public void setApprovedDealerExists(boolean approvedDealerExists) {
		this.approvedDealerExists = approvedDealerExists;
	}

	public boolean isNonApprovedDealerExists() {
		return nonApprovedDealerExists;
	}
	public void setNonApprovedDealerExists(boolean nonApprovedDealerExists) {
		this.nonApprovedDealerExists = nonApprovedDealerExists;
	}

	public BigDecimal getCoAppRepayBank() {
		return coAppRepayBank;
	}
	public void setCoAppRepayBank(BigDecimal coAppRepayBank) {
		this.coAppRepayBank = coAppRepayBank;
	}

	public BigDecimal getCoAppIncome() {
		return coAppIncome;
	}
	public void setCoAppIncome(BigDecimal coAppIncome) {
		this.coAppIncome = coAppIncome;
	}

	public BigDecimal getCoAppExpense() {
		return coAppExpense;
	}
	public void setCoAppExpense(BigDecimal coAppExpense) {
		this.coAppExpense = coAppExpense;
	}

	public BigDecimal getCoAppCurFinEMI() {
		return coAppCurFinEMI;
	}
	public void setCoAppCurFinEMI(BigDecimal coAppCurFinEMI) {
		this.coAppCurFinEMI = coAppCurFinEMI;
	}

	public BigDecimal getCustProcRepayBank() {
		return custProcRepayBank;
	}
	public void setCustProcRepayBank(BigDecimal custProcRepayBank) {
		this.custProcRepayBank = custProcRepayBank;
	}

	public BigDecimal getCustOtherIncomeAmt() {
		return custOtherIncomeAmt;
	}
	public void setCustOtherIncomeAmt(BigDecimal custOtherIncomeAmt) {
		this.custOtherIncomeAmt = custOtherIncomeAmt;
	}

	public String getCustEmpType() {
		return custEmpType;
	}
	public void setCustEmpType(String custEmpType) {
		this.custEmpType = custEmpType;
	}

	public BigDecimal getCurrentAssetValue() {
		return currentAssetValue;
	}
	public void setCurrentAssetValue(BigDecimal currentAssetValue) {
		this.currentAssetValue = currentAssetValue;
	}

	public BigDecimal getInstallmentAmount() {
		return installmentAmount;
	}
	public void setInstallmentAmount(BigDecimal installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	public BigDecimal getFoir() {
		return foir;
	}
	public void setFoir(BigDecimal foir) {
		this.foir = foir;
	}

	public BigDecimal getLtv() {
		return ltv;
	}
	public void setLtv(BigDecimal ltv) {
		this.ltv = ltv;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public String getCustIndustry() {
		return custIndustry;
	}

	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}
	
}
