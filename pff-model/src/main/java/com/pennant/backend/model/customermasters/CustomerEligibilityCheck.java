package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerEligibilityCheck implements Serializable {
	private static final Logger logger = LogManager.getLogger(CustomerEligibilityCheck.class);

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
	private String custMaritalSts = "";
	private int noOfDependents = 0;
	private String custNationality = "";
	private String reqProduct = "";
	private String custWorstSts = "";
	private String custTypeCode = "";
	private String custCtgCode;
	private String custIndustry;
	private String custOtherIncome;
	private BigDecimal custOtherIncomeAmt = BigDecimal.ZERO;

	private int noOfTerms;
	private String finRepayMethod;
	private boolean stepFinance;
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
	private String reqFinCcy;
	private String custEmpName = "";

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
	private String recordStatus;

	private boolean ddaModifiedCheck;
	private BigDecimal refundAmount;
	private String custEmpType;
	private BigDecimal currentAssetValue = BigDecimal.ZERO;
	private BigDecimal installmentAmount = BigDecimal.ZERO;
	private BigDecimal foir = BigDecimal.ZERO;
	private BigDecimal ltv = BigDecimal.ZERO;
	private BigDecimal disbursedAmount = BigDecimal.ZERO;
	private String eligibilityMethod;
	private boolean disbOnGrace = false;
	private int activeLoansOnFinType = 0;
	private int totalLoansOnFinType = 0;

	// Payment type check
	private boolean chequeOrDDAvailable;
	private boolean neftAvailable; // If NEFT/IMPS/RTGS Available

	private BigDecimal cibilScore;
	private String custCity;
	private String bureauDelinquency;
	private BigDecimal calculatedAnnualNetSalary = BigDecimal.ZERO;
	private Map<String, Object> dataMap;

	/*
	 * private String custCIF; private String custSubSector = ""; private String custCOB = ""; private String
	 * custDftBranch; private long custGroupID = 0; private String custGroupSts; private boolean custIsBlocked = false;
	 * private boolean custIsActive = false; private boolean custIsClosed = false; private boolean custIsDecease =
	 * false; private boolean custIsDormant = false; private boolean custIsDelinquent = false; private boolean
	 * custIsTradeFinCust = false; private boolean custIsStaff = false; private String custProfession; private boolean
	 * custIsRejected = false; private String custParentCountry; private String custResdCountry; private String
	 * custRiskCountry;
	 * 
	 * private String reqFinType; private Date reqFinStartDate; private Date reqMaturity;
	 * 
	 * private String reqCampaign; private int reqTerms = 0; private int custLiveFinCount = 0; private int
	 * custReqFtpCount = 0; private BigDecimal reqFinAmount = BigDecimal.ZERO; private BigDecimal reqFinRepay =
	 * BigDecimal.ZERO; private BigDecimal custPastDueCount = BigDecimal.ZERO; private BigDecimal custReqFtpAmount =
	 * BigDecimal.ZERO; private BigDecimal reqFinAssetVal = BigDecimal.ZERO; private BigDecimal reqPftRate =
	 * BigDecimal.ZERO;
	 * 
	 * private BigDecimal custPDHist30D = BigDecimal.ZERO; private BigDecimal custPDHist60D = BigDecimal.ZERO; private
	 * BigDecimal custPDHist90D = BigDecimal.ZERO; private BigDecimal custPDHist120D = BigDecimal.ZERO; private
	 * BigDecimal custPDHist180D = BigDecimal.ZERO; private BigDecimal custPDHist180DP = BigDecimal.ZERO; private
	 * BigDecimal custPDLive30D = BigDecimal.ZERO; private BigDecimal custPDLive60D = BigDecimal.ZERO; private
	 * BigDecimal custPDLive90D = BigDecimal.ZERO; private BigDecimal custPDLive120D = BigDecimal.ZERO; private
	 * BigDecimal custPDLive180D = BigDecimal.ZERO; private BigDecimal custPDLive180DP = BigDecimal.ZERO;
	 */
	private BigDecimal netLoanAmount;
	Map<String, Object> extendedFields = new HashMap<>();
	private String loanFlag;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("cibilScore");
		excludeFields.add("bureaDelinquency");
		excludeFields.add("calculatedAnnualNetSalary");
		excludeFields.add("custCity");
		return excludeFields;
	}

	public CustomerEligibilityCheck() {
	    super();
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

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> customerEligibityMap = new HashMap<String, Object>();

		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				if ("extendedFields".equals(this.getClass().getDeclaredFields()[i].getName())) {
					customerEligibityMap.putAll(extendedFields);
				} else if (!"serialVersionUID".equals(this.getClass().getDeclaredFields()[i].getName())) {
					customerEligibityMap.put(this.getClass().getDeclaredFields()[i].getName(),
							this.getClass().getDeclaredFields()[i].get(this));
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error(Literal.EXCEPTION, e);
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

	public String getReqFinType() {
		return reqFinType;
	}

	public void setReqFinType(String reqFinType) {
		this.reqFinType = reqFinType;
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

	public String getEligibilityMethod() {
		return eligibilityMethod;
	}

	public void setEligibilityMethod(String eligibilityMethod) {
		this.eligibilityMethod = eligibilityMethod;
	}

	public void addExtendedField(String fieldName, Object value) {
		this.extendedFields.put(fieldName, value);
	}

	public void setExtendedFields(Map<String, Object> ruleMap) {
		this.extendedFields.putAll(ruleMap);
	}

	public Object getExtendedValue(String fieldName) {
		return this.extendedFields.get(fieldName);
	}

	public boolean isDisbOnGrace() {
		return disbOnGrace;
	}

	public void setDisbOnGrace(boolean disbOnGrace) {
		this.disbOnGrace = disbOnGrace;
	}

	public int getActiveLoansOnFinType() {
		return activeLoansOnFinType;
	}

	public void setActiveLoansOnFinType(int activeLoansOnFinType) {
		this.activeLoansOnFinType = activeLoansOnFinType;
	}

	public int getTotalLoansOnFinType() {
		return totalLoansOnFinType;
	}

	public void setTotalLoansOnFinType(int totalLoansOnFinType) {
		this.totalLoansOnFinType = totalLoansOnFinType;
	}

	public boolean isChequeOrDDAvailable() {
		return chequeOrDDAvailable;
	}

	public void setChequeOrDDAvailable(boolean chequeOrDDAvailable) {
		this.chequeOrDDAvailable = chequeOrDDAvailable;
	}

	public boolean isNeftAvailable() {
		return neftAvailable;
	}

	public void setNeftAvailable(boolean neftAvailable) {
		this.neftAvailable = neftAvailable;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public BigDecimal getNetLoanAmount() {
		return netLoanAmount;
	}

	public void setNetLoanAmount(BigDecimal netLoanAmount) {
		this.netLoanAmount = netLoanAmount;
	}

	public BigDecimal getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(BigDecimal cibilScore) {
		this.cibilScore = cibilScore;
	}

	public String getBureauDelinquency() {
		return bureauDelinquency;
	}

	public void setBureauDelinquency(String bureauDelinquency) {
		this.bureauDelinquency = bureauDelinquency;
	}

	public BigDecimal getCalculatedAnnualNetSalary() {
		return calculatedAnnualNetSalary;
	}

	public void setCalculatedAnnualNetSalary(BigDecimal calculatedAnnualNetSalary) {
		this.calculatedAnnualNetSalary = calculatedAnnualNetSalary;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public String getLoanFlag() {
		return loanFlag;
	}

	public void setLoanFlag(String loanFlag) {
		this.loanFlag = loanFlag;
	}

}
