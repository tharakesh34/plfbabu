package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "vatOrServiceTaxOrForm26As", "reservesAndSurplus", "intrestObligation", "currentRatio",
		"turnOverYr2", "turnOverGrowthLastYr", "turnOverGrowthLastToLastYr", "depreciationYr1", "depreciationYr2",
		"currentAssets", "currentLiabilittes", "sumAllEMILoans", "salesYr1", "salesYr2", "equity",
		"equityAndPreferrenceShareCapital", "totalAssets", "expenseYr1", "expenseYr2", "totalLiabilities", "profitYr1",
		"profitYr2", "fixedAssetYr1", "fixedAssetYr2", "incomeTax", "totalLoans", "liveFinObligationsInEMICodBaletc",
		"workingCapitalCycle", "interestOnCapitalToPartners", "netProfitOrLoss", "utilizationLimitOnodorcc",
		"quasiEquityDirectorsFrndsAndRelatives" })
@XmlRootElement(name = "Financials")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicantFinancials {

	@XmlElement(name = "VatOrServiceTaxOrForm26As")
	private BigDecimal	vatOrServiceTaxOrForm26As				= BigDecimal.ZERO;

	@XmlElement(name = "reservesandsurplus")
	private String		reservesAndSurplus;

	@XmlElement(name = "interestobligation")
	private String		intrestObligation;

	@XmlElement(name = "currentratio")
	private String		currentRatio;

	@XmlElement(name = "Turnoveryr2")
	private String		turnOverYr2;

	@XmlElement(name = "turnovergrowthlastyear")
	private String		turnOverGrowthLastYr;

	@XmlElement(name = "turnovergrowthlasttolastyear")
	private String		turnOverGrowthLastToLastYr;

	@XmlElement(name = "depreciationyr1")
	private BigDecimal	depreciationYr1							= BigDecimal.ZERO;

	@XmlElement(name = "depreciationyr2")
	private BigDecimal	depreciationYr2							= BigDecimal.ZERO;

	@XmlElement(name = "CurrentAssets")
	private String		currentAssets;

	@XmlElement(name = "CurrentLiabilittes")
	private String		currentLiabilittes;

	@XmlElement(name = "sumofemiallloans")
	private BigDecimal	sumAllEMILoans							= BigDecimal.ZERO;

	@XmlElement(name = "salesyr1")
	private BigDecimal	salesYr1								= BigDecimal.ZERO;

	@XmlElement(name = "salesyr2")
	private BigDecimal	salesYr2								= BigDecimal.ZERO;

	@XmlElement(name = "Equity")
	private String		equity;

	@XmlElement(name = "equityandpreferrencesharecapital")
	private BigDecimal	equityAndPreferrenceShareCapital		= BigDecimal.ZERO;

	@XmlElement(name = "totalassets")
	private BigDecimal	totalAssets								= BigDecimal.ZERO;

	@XmlElement(name = "expenseyear1")
	private BigDecimal	expenseYr1								= BigDecimal.ZERO;

	@XmlElement(name = "expenseyear2")
	private BigDecimal	expenseYr2								= BigDecimal.ZERO;

	@XmlElement(name = "totalliabilities")
	private BigDecimal	totalLiabilities						= BigDecimal.ZERO;

	@XmlElement(name = "profityear1")
	private BigDecimal	profitYr1								= BigDecimal.ZERO;

	@XmlElement(name = "profityear2")
	private BigDecimal	profitYr2								= BigDecimal.ZERO;

	@XmlElement(name = "fixedassetyr1")
	private BigDecimal	fixedAssetYr1							= BigDecimal.ZERO;

	@XmlElement(name = "fixedassetyr2")
	private BigDecimal	fixedAssetYr2							= BigDecimal.ZERO;

	@XmlElement(name = "incometax")
	private BigDecimal	incomeTax								= BigDecimal.ZERO;

	@XmlElement(name = "TotalLoans")
	private String		totalLoans;

	@XmlElement(name = "livefinancialobligationsinemiccodbalanceetc")
	private String		liveFinObligationsInEMICodBaletc;

	@XmlElement(name = "workingcapitalcycle")
	private String		workingCapitalCycle;

	@XmlElement(name = "interestoncapitaltopartners")
	private BigDecimal	interestOnCapitalToPartners				= BigDecimal.ZERO;

	@XmlElement(name = "netprofitorloss")
	private String		netProfitOrLoss;

	@XmlElement(name = "utilizationlimitonodorcc")
	private BigDecimal	utilizationLimitOnodorcc				= BigDecimal.ZERO;

	@XmlElement(name = "quasiequitydirectorsfriendsandrelatives")
	private BigDecimal	quasiEquityDirectorsFrndsAndRelatives	= BigDecimal.ZERO;

	public BigDecimal getVatOrServiceTaxOrForm26As() {
		return vatOrServiceTaxOrForm26As;
	}

	public void setVatOrServiceTaxOrForm26As(BigDecimal vatOrServiceTaxOrForm26As) {
		this.vatOrServiceTaxOrForm26As = vatOrServiceTaxOrForm26As;
	}

	public String getReservesAndSurplus() {
		return reservesAndSurplus;
	}

	public void setReservesAndSurplus(String reservesAndSurplus) {
		this.reservesAndSurplus = reservesAndSurplus;
	}

	public String getIntrestObligation() {
		return intrestObligation;
	}

	public void setIntrestObligation(String intrestObligation) {
		this.intrestObligation = intrestObligation;
	}

	public String getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(String currentRatio) {
		this.currentRatio = currentRatio;
	}

	public String getTurnOverYr2() {
		return turnOverYr2;
	}

	public void setTurnOverYr2(String turnOverYr2) {
		this.turnOverYr2 = turnOverYr2;
	}

	public String getTurnOverGrowthLastYr() {
		return turnOverGrowthLastYr;
	}

	public void setTurnOverGrowthLastYr(String turnOverGrowthLastYr) {
		this.turnOverGrowthLastYr = turnOverGrowthLastYr;
	}

	public String getTurnOverGrowthLastToLastYr() {
		return turnOverGrowthLastToLastYr;
	}

	public void setTurnOverGrowthLastToLastYr(String turnOverGrowthLastToLastYr) {
		this.turnOverGrowthLastToLastYr = turnOverGrowthLastToLastYr;
	}

	public BigDecimal getDepreciationYr1() {
		return depreciationYr1;
	}

	public void setDepreciationYr1(BigDecimal depreciationYr1) {
		this.depreciationYr1 = depreciationYr1;
	}

	public BigDecimal getDepreciationYr2() {
		return depreciationYr2;
	}

	public void setDepreciationYr2(BigDecimal depreciationYr2) {
		this.depreciationYr2 = depreciationYr2;
	}

	public String getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(String currentAssets) {
		this.currentAssets = currentAssets;
	}

	public String getCurrentLiabilittes() {
		return currentLiabilittes;
	}

	public void setCurrentLiabilittes(String currentLiabilittes) {
		this.currentLiabilittes = currentLiabilittes;
	}

	public BigDecimal getSumAllEMILoans() {
		return sumAllEMILoans;
	}

	public void setSumAllEMILoans(BigDecimal sumAllEMILoans) {
		this.sumAllEMILoans = sumAllEMILoans;
	}

	public BigDecimal getSalesYr1() {
		return salesYr1;
	}

	public void setSalesYr1(BigDecimal salesYr1) {
		this.salesYr1 = salesYr1;
	}

	public BigDecimal getSalesYr2() {
		return salesYr2;
	}

	public void setSalesYr2(BigDecimal salesYr2) {
		this.salesYr2 = salesYr2;
	}

	public String getEquity() {
		return equity;
	}

	public void setEquity(String equity) {
		this.equity = equity;
	}

	public BigDecimal getEquityAndPreferrenceShareCapital() {
		return equityAndPreferrenceShareCapital;
	}

	public void setEquityAndPreferrenceShareCapital(BigDecimal equityAndPreferrenceShareCapital) {
		this.equityAndPreferrenceShareCapital = equityAndPreferrenceShareCapital;
	}

	public BigDecimal getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}

	public BigDecimal getExpenseYr1() {
		return expenseYr1;
	}

	public void setExpenseYr1(BigDecimal expenseYr1) {
		this.expenseYr1 = expenseYr1;
	}

	public BigDecimal getExpenseYr2() {
		return expenseYr2;
	}

	public void setExpenseYr2(BigDecimal expenseYr2) {
		this.expenseYr2 = expenseYr2;
	}

	public BigDecimal getTotalLiabilities() {
		return totalLiabilities;
	}

	public void setTotalLiabilities(BigDecimal totalLiabilities) {
		this.totalLiabilities = totalLiabilities;
	}

	public BigDecimal getProfitYr1() {
		return profitYr1;
	}

	public void setProfitYr1(BigDecimal profitYr1) {
		this.profitYr1 = profitYr1;
	}

	public BigDecimal getProfitYr2() {
		return profitYr2;
	}

	public void setProfitYr2(BigDecimal profitYr2) {
		this.profitYr2 = profitYr2;
	}

	public BigDecimal getFixedAssetYr1() {
		return fixedAssetYr1;
	}

	public void setFixedAssetYr1(BigDecimal fixedAssetYr1) {
		this.fixedAssetYr1 = fixedAssetYr1;
	}

	public BigDecimal getFixedAssetYr2() {
		return fixedAssetYr2;
	}

	public void setFixedAssetYr2(BigDecimal fixedAssetYr2) {
		this.fixedAssetYr2 = fixedAssetYr2;
	}

	public BigDecimal getIncomeTax() {
		return incomeTax;
	}

	public void setIncomeTax(BigDecimal incomeTax) {
		this.incomeTax = incomeTax;
	}

	public String getTotalLoans() {
		return totalLoans;
	}

	public void setTotalLoans(String totalLoans) {
		this.totalLoans = totalLoans;
	}

	public String getLiveFinObligationsInEMICodBaletc() {
		return liveFinObligationsInEMICodBaletc;
	}

	public void setLiveFinObligationsInEMICodBaletc(String liveFinObligationsInEMICodBaletc) {
		this.liveFinObligationsInEMICodBaletc = liveFinObligationsInEMICodBaletc;
	}

	public String getWorkingCapitalCycle() {
		return workingCapitalCycle;
	}

	public void setWorkingCapitalCycle(String workingCapitalCycle) {
		this.workingCapitalCycle = workingCapitalCycle;
	}

	public BigDecimal getInterestOnCapitalToPartners() {
		return interestOnCapitalToPartners;
	}

	public void setInterestOnCapitalToPartners(BigDecimal interestOnCapitalToPartners) {
		this.interestOnCapitalToPartners = interestOnCapitalToPartners;
	}

	public String getNetProfitOrLoss() {
		return netProfitOrLoss;
	}

	public void setNetProfitOrLoss(String netProfitOrLoss) {
		this.netProfitOrLoss = netProfitOrLoss;
	}

	public BigDecimal getUtilizationLimitOnodorcc() {
		return utilizationLimitOnodorcc;
	}

	public void setUtilizationLimitOnodorcc(BigDecimal utilizationLimitOnodorcc) {
		this.utilizationLimitOnodorcc = utilizationLimitOnodorcc;
	}

	public BigDecimal getQuasiEquityDirectorsFrndsAndRelatives() {
		return quasiEquityDirectorsFrndsAndRelatives;
	}

	public void setQuasiEquityDirectorsFrndsAndRelatives(BigDecimal quasiEquityDirectorsFrndsAndRelatives) {
		this.quasiEquityDirectorsFrndsAndRelatives = quasiEquityDirectorsFrndsAndRelatives;
	}

}
