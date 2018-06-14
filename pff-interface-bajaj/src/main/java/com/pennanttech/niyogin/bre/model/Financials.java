package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "salesYr1", "salesYr2", "netSalesOrReceiptsGrossReturns", "expenseYr1", "expenseYr2",
		"netPurchasesGrossReturns", "directExpenses", "indirectExpensesSellingAndAdminAndGeneral",
		"quasiInterestFriendsOrRelativesIfAny", "depreciationYr1", "depreciationYr2", "depreciation",
		"interestToBanksOrFinancialInstitutionsOrFinanciers", "interestOnCapitalToPartners",
		"partnersOrDirectorsRemuneration", "incomeTax", "netProfitOrLoss", "equityAndPreferrenceShareCapital",
		"quasiEquityDirectorsFriendsAndRelatives", "reservesAndSurPlus", "borrowingFromGroupCompanies", "totalLoans",
		"sundryCreditorsForTradeAndExpensesBillsPayable", "fixedassetYr1", "fixedassetYr2", "fixedAssetsNetBlock",
		"sundryDebtorsLessThan6M", "sundrydebtorsgreatorthan6M", "itReturnFilingDate",
		"liveFinancialObligationsInEmiCcodBaleetc", "sumOfEmiAllLoans", "interestObligation", "cashCreditorOverdraft",
		"dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M", "equityTotAanGiblenetWorth", "postFundingDSCR",
		"currentRatio", "workingCapitalGap", "turnOverYr1", "turnOverYr2", "turnOverGrowthLastYr",
		"turnOverGrowthLastToLastYr", "minYrlyTurnOver", "avgValueOfTurnoverOfL3Yrs", "utilizationLimitOnODorCC",
		"networth", "oldestLoanDisbursedDate", "currentAssets", "currentLiabilittes", "retainedEarnings", "equity",
		"existingLoanObligation", "debt", "totalAssets", "totalLiabilities", "borrowings", "profitYr1", "profitYr2",
		"operationProfit", "workingCapitalCycle", "fundsReceived", "appliedTenor", "stock", "latestDebtors", "cash",
		"bank", "investment", "shorttermBorrowings", "sundryDebtors", "provisions", "vatServiceTaxForm26as" })
@XmlRootElement(name = "FINANCIALS")
@XmlAccessorType(XmlAccessType.FIELD)
public class Financials {

	@XmlElement(name = "SALESYR1")
	private BigDecimal			salesYr1											= BigDecimal.ZERO;

	@XmlElement(name = "SALESYR2")
	private BigDecimal			salesYr2											= BigDecimal.ZERO;

	@XmlElement(name = "NETSALESORRECEIPTSGROSSRETURNS")
	private BigDecimal			netSalesOrReceiptsGrossReturns						= BigDecimal.ZERO;

	@XmlElement(name = "EXPENSEYEAR1")
	private BigDecimal			expenseYr1											= BigDecimal.ZERO;

	@XmlElement(name = "EXPENSEYEAR2")
	private BigDecimal			expenseYr2											= BigDecimal.ZERO;

	@XmlElement(name = "NETPURCHASESGROSSRETURNS")
	private BigDecimal			netPurchasesGrossReturns							= BigDecimal.ZERO;

	@XmlElement(name = "DIRECTEXPENSES")
	private BigDecimal			directExpenses										= BigDecimal.ZERO;

	@XmlElement(name = "INDIRECTEXPENSESSELLINGANDADMINANDGENERAL")
	private BigDecimal			indirectExpensesSellingAndAdminAndGeneral			= BigDecimal.ZERO;

	@XmlElement(name = "QUASIINTERESTFRIENDSORRELATIVESIFANY")
	private BigDecimal			quasiInterestFriendsOrRelativesIfAny				= BigDecimal.ZERO;

	@XmlElement(name = "DEPRECIATIONYR1")
	private BigDecimal			depreciationYr1										= BigDecimal.ZERO;

	@XmlElement(name = "DEPRECIATIONYR2")
	private BigDecimal			depreciationYr2										= BigDecimal.ZERO;

	@XmlElement(name = "DEPRECIATION")
	private BigDecimal			depreciation										= BigDecimal.ZERO;

	@XmlElement(name = "INTERESTTOBANKSORFINANCIALINSTITUTIONSORFINANCIERS")
	private BigDecimal			interestToBanksOrFinancialInstitutionsOrFinanciers	= BigDecimal.ZERO;

	@XmlElement(name = "INTERESTONCAPITALTOPARTNERS")
	private BigDecimal			interestOnCapitalToPartners							= BigDecimal.ZERO;

	@XmlElement(name = "PARTNERSORDIRECTORSREMUNERATION")
	private BigDecimal			partnersOrDirectorsRemuneration						= BigDecimal.ZERO;

	@XmlElement(name = "INCOMETAX")
	private BigDecimal			incomeTax											= BigDecimal.ZERO;

	@XmlElement(name = "NETPROFITORLOSS")
	private BigDecimal			netProfitOrLoss										= BigDecimal.ZERO;

	@XmlElement(name = "EQUITYANDPREFERRENCESHARECAPITAL")
	private BigDecimal			equityAndPreferrenceShareCapital					= BigDecimal.ZERO;

	@XmlElement(name = "QUASIEQUITYDIRECTORSFRIENDSANDRELATIVES")
	private BigDecimal			quasiEquityDirectorsFriendsAndRelatives				= BigDecimal.ZERO;

	@XmlElement(name = "RESERVESANDSURPLUS")
	private BigDecimal			reservesAndSurPlus									= BigDecimal.ZERO;

	@XmlElement(name = "BORROWINGFROMGROUPCOMPANIES")
	private BigDecimal			borrowingFromGroupCompanies							= BigDecimal.ZERO;

	@XmlElement(name = "TOTALLOANS")
	private BigDecimal			totalLoans											= BigDecimal.ZERO;

	@XmlElement(name = "SUNDRYCREDITORSFORTRADEANDEXPENSESBILLSPAYABLE")
	private BigDecimal			sundryCreditorsForTradeAndExpensesBillsPayable		= BigDecimal.ZERO;

	@XmlElement(name = "FIXEDASSETYR1")
	private BigDecimal			fixedassetYr1										= BigDecimal.ZERO;

	@XmlElement(name = "FIXEDASSETYR2")
	private BigDecimal			fixedassetYr2										= BigDecimal.ZERO;

	@XmlElement(name = "FIXEDASSETSNETBLOCK")
	private BigDecimal			fixedAssetsNetBlock									= BigDecimal.ZERO;

	@XmlElement(name = "SUNDRYDEBTORSLESSTHAN6MONTHS")
	private BigDecimal			sundryDebtorsLessThan6M								= BigDecimal.ZERO;

	@XmlElement(name = "SUNDRYDEBTORSGREATORTHAN6MONTHS")
	private BigDecimal			sundrydebtorsgreatorthan6M							= BigDecimal.ZERO;

	@XmlElement(name = "ITRETURNFILINGDATE")
	private String				itReturnFilingDate;

	@XmlElement(name = "LIVEFINANCIALOBLIGATIONSINEMICCODBALANCEETC")
	private BigDecimal			liveFinancialObligationsInEmiCcodBaleetc			= BigDecimal.ZERO;

	@XmlElement(name = "SUMOFEMIALLLOANS")
	private BigDecimal			sumOfEmiAllLoans									= BigDecimal.ZERO;

	@XmlElement(name = "INTERESTOBLIGATION")
	private BigDecimal			interestObligation									= BigDecimal.ZERO;

	@XmlElement(name = "CASHCREDITOROVERDRAFT")
	private BigDecimal			cashCreditorOverdraft								= BigDecimal.ZERO;

	@XmlElement(name = "DEBITPOSOFALLTHEOUTSTANDINGLOANSWHICHARENOTCLOSINGINTHENEXT3MONTHS")
	private BigDecimal			dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M	= BigDecimal.ZERO;

	@XmlElement(name = "EQUITYTOTALTANGIBLENETWORTH")
	private BigDecimal			equityTotAanGiblenetWorth							= BigDecimal.ZERO;

	@XmlElement(name = "POSTFUNDINGDSCR")
	private BigDecimal			postFundingDSCR										= BigDecimal.ZERO;

	@XmlElement(name = "CURRENTRATIO")
	private BigDecimal			currentRatio										= BigDecimal.ZERO;

	@XmlElement(name = "WORKINGCAPITALGAP")
	private BigDecimal			workingCapitalGap									= BigDecimal.ZERO;

	@XmlElement(name = "TURNOVERYR1")
	private BigDecimal			turnOverYr1											= BigDecimal.ZERO;

	@XmlElement(name = "TURNOVERYR2")
	private BigDecimal			turnOverYr2											= BigDecimal.ZERO;

	@XmlElement(name = "TURNOVERGROWTHLASTYEAR")
	private BigDecimal			turnOverGrowthLastYr								= BigDecimal.ZERO;

	@XmlElement(name = "TURNOVERGROWTHLASTTOLASTYEAR")
	private BigDecimal			turnOverGrowthLastToLastYr							= BigDecimal.ZERO;

	@XmlElement(name = "MINIMUMYEARLYTURNOVER")
	private BigDecimal			minYrlyTurnOver										= BigDecimal.ZERO;

	@XmlElement(name = "AVERAGEVALUEOFTURNOVEROFLAST3YEARS")
	private BigDecimal			avgValueOfTurnoverOfL3Yrs							= BigDecimal.ZERO;

	@XmlElement(name = "UTILIZATIONLIMITONODORCC")
	private BigDecimal			utilizationLimitOnODorCC							= BigDecimal.ZERO;

	@XmlElement(name = "NETWORTH")
	private BigDecimal			networth											= BigDecimal.ZERO;

	@XmlElement(name = "OLDESTLOANDISBURSEDDATE")
	private String				oldestLoanDisbursedDate;

	//TODO:
	@XmlElement(name = "CURRENTASSETS")
	private CurrentAssests		currentAssets;

	//TODO:
	@XmlElement(name = "CURRENTLIABILITTES")
	private CurrentLiabilities	currentLiabilittes;

	@XmlElement(name = "RETAINEDEARNINGS")
	private BigDecimal			retainedEarnings									= BigDecimal.ZERO;

	@XmlElement(name = "EQUITY")
	private BigDecimal			equity												= BigDecimal.ZERO;

	@XmlElement(name = "EXISTINGLOANOBLIGATION")
	private BigDecimal			existingLoanObligation								= BigDecimal.ZERO;

	@XmlElement(name = "DEBT")
	private BigDecimal			debt												= BigDecimal.ZERO;

	@XmlElement(name = "TOTALASSETS")
	private BigDecimal			totalAssets											= BigDecimal.ZERO;

	@XmlElement(name = "TOTALLIABILITIES")
	private BigDecimal			totalLiabilities									= BigDecimal.ZERO;

	@XmlElement(name = "BORROWINGS")
	private BigDecimal			borrowings											= BigDecimal.ZERO;

	@XmlElement(name = "PROFITYEAR1")
	private BigDecimal			profitYr1											= BigDecimal.ZERO;

	@XmlElement(name = "PROFITYEAR2")
	private BigDecimal			profitYr2											= BigDecimal.ZERO;

	@XmlElement(name = "OPERATIONPROFIT")
	private BigDecimal			operationProfit										= BigDecimal.ZERO;

	@XmlElement(name = "WORKINGCAPITALCYCLE")
	private BigDecimal			workingCapitalCycle									= BigDecimal.ZERO;

	@XmlElement(name = "FUNDSRECEIVED")
	private BigDecimal			fundsReceived										= BigDecimal.ZERO;

	@XmlElement(name = "APPLIEDTENOR")
	private BigDecimal			appliedTenor										= BigDecimal.ZERO;

	@XmlElement(name = "STOCK")
	private BigDecimal			stock												= BigDecimal.ZERO;

	@XmlElement(name = "LATESTDEBTORS")
	private BigDecimal			latestDebtors										= BigDecimal.ZERO;

	@XmlElement(name = "CASH")
	private BigDecimal			cash												= BigDecimal.ZERO;

	@XmlElement(name = "BANK")
	private BigDecimal			bank												= BigDecimal.ZERO;

	@XmlElement(name = "INVESTMENT")
	private BigDecimal			investment											= BigDecimal.ZERO;

	@XmlElement(name = "SHORTTERMBORROWINGS")
	private BigDecimal			shorttermBorrowings									= BigDecimal.ZERO;

	@XmlElement(name = "SUNDRYDEBTORS")
	private BigDecimal			sundryDebtors										= BigDecimal.ZERO;

	@XmlElement(name = "PROVISIONS")
	private BigDecimal			provisions											= BigDecimal.ZERO;

	//TODO:
	@XmlElement(name = "VATSERVICETAXFORM26AS")
	private VatServiceTaxForms	vatServiceTaxForm26as;

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

	public BigDecimal getNetSalesOrReceiptsGrossReturns() {
		return netSalesOrReceiptsGrossReturns;
	}

	public void setNetSalesOrReceiptsGrossReturns(BigDecimal netSalesOrReceiptsGrossReturns) {
		this.netSalesOrReceiptsGrossReturns = netSalesOrReceiptsGrossReturns;
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

	public BigDecimal getNetPurchasesGrossReturns() {
		return netPurchasesGrossReturns;
	}

	public void setNetPurchasesGrossReturns(BigDecimal netPurchasesGrossReturns) {
		this.netPurchasesGrossReturns = netPurchasesGrossReturns;
	}

	public BigDecimal getDirectExpenses() {
		return directExpenses;
	}

	public void setDirectExpenses(BigDecimal directExpenses) {
		this.directExpenses = directExpenses;
	}

	public BigDecimal getIndirectExpensesSellingAndAdminAndGeneral() {
		return indirectExpensesSellingAndAdminAndGeneral;
	}

	public void setIndirectExpensesSellingAndAdminAndGeneral(BigDecimal indirectExpensesSellingAndAdminAndGeneral) {
		this.indirectExpensesSellingAndAdminAndGeneral = indirectExpensesSellingAndAdminAndGeneral;
	}

	public BigDecimal getQuasiInterestFriendsOrRelativesIfAny() {
		return quasiInterestFriendsOrRelativesIfAny;
	}

	public void setQuasiInterestFriendsOrRelativesIfAny(BigDecimal quasiInterestFriendsOrRelativesIfAny) {
		this.quasiInterestFriendsOrRelativesIfAny = quasiInterestFriendsOrRelativesIfAny;
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

	public BigDecimal getDepreciation() {
		return depreciation;
	}

	public void setDepreciation(BigDecimal depreciation) {
		this.depreciation = depreciation;
	}

	public BigDecimal getInterestToBanksOrFinancialInstitutionsOrFinanciers() {
		return interestToBanksOrFinancialInstitutionsOrFinanciers;
	}

	public void setInterestToBanksOrFinancialInstitutionsOrFinanciers(
			BigDecimal interestToBanksOrFinancialInstitutionsOrFinanciers) {
		this.interestToBanksOrFinancialInstitutionsOrFinanciers = interestToBanksOrFinancialInstitutionsOrFinanciers;
	}

	public BigDecimal getInterestOnCapitalToPartners() {
		return interestOnCapitalToPartners;
	}

	public void setInterestOnCapitalToPartners(BigDecimal interestOnCapitalToPartners) {
		this.interestOnCapitalToPartners = interestOnCapitalToPartners;
	}

	public BigDecimal getPartnersOrDirectorsRemuneration() {
		return partnersOrDirectorsRemuneration;
	}

	public void setPartnersOrDirectorsRemuneration(BigDecimal partnersOrDirectorsRemuneration) {
		this.partnersOrDirectorsRemuneration = partnersOrDirectorsRemuneration;
	}

	public BigDecimal getIncomeTax() {
		return incomeTax;
	}

	public void setIncomeTax(BigDecimal incomeTax) {
		this.incomeTax = incomeTax;
	}

	public BigDecimal getNetProfitOrLoss() {
		return netProfitOrLoss;
	}

	public void setNetProfitOrLoss(BigDecimal netProfitOrLoss) {
		this.netProfitOrLoss = netProfitOrLoss;
	}

	public BigDecimal getEquityAndPreferrenceShareCapital() {
		return equityAndPreferrenceShareCapital;
	}

	public void setEquityAndPreferrenceShareCapital(BigDecimal equityAndPreferrenceShareCapital) {
		this.equityAndPreferrenceShareCapital = equityAndPreferrenceShareCapital;
	}

	public BigDecimal getQuasiEquityDirectorsFriendsAndRelatives() {
		return quasiEquityDirectorsFriendsAndRelatives;
	}

	public void setQuasiEquityDirectorsFriendsAndRelatives(BigDecimal quasiEquityDirectorsFriendsAndRelatives) {
		this.quasiEquityDirectorsFriendsAndRelatives = quasiEquityDirectorsFriendsAndRelatives;
	}

	public BigDecimal getReservesAndSurPlus() {
		return reservesAndSurPlus;
	}

	public void setReservesAndSurPlus(BigDecimal reservesAndSurPlus) {
		this.reservesAndSurPlus = reservesAndSurPlus;
	}

	public BigDecimal getBorrowingFromGroupCompanies() {
		return borrowingFromGroupCompanies;
	}

	public void setBorrowingFromGroupCompanies(BigDecimal borrowingFromGroupCompanies) {
		this.borrowingFromGroupCompanies = borrowingFromGroupCompanies;
	}

	public BigDecimal getTotalLoans() {
		return totalLoans;
	}

	public void setTotalLoans(BigDecimal totalLoans) {
		this.totalLoans = totalLoans;
	}

	public BigDecimal getSundryCreditorsForTradeAndExpensesBillsPayable() {
		return sundryCreditorsForTradeAndExpensesBillsPayable;
	}

	public void setSundryCreditorsForTradeAndExpensesBillsPayable(
			BigDecimal sundryCreditorsForTradeAndExpensesBillsPayable) {
		this.sundryCreditorsForTradeAndExpensesBillsPayable = sundryCreditorsForTradeAndExpensesBillsPayable;
	}

	public BigDecimal getFixedassetYr1() {
		return fixedassetYr1;
	}

	public void setFixedassetYr1(BigDecimal fixedassetYr1) {
		this.fixedassetYr1 = fixedassetYr1;
	}

	public BigDecimal getFixedassetYr2() {
		return fixedassetYr2;
	}

	public void setFixedassetYr2(BigDecimal fixedassetYr2) {
		this.fixedassetYr2 = fixedassetYr2;
	}

	public BigDecimal getFixedAssetsNetBlock() {
		return fixedAssetsNetBlock;
	}

	public void setFixedAssetsNetBlock(BigDecimal fixedAssetsNetBlock) {
		this.fixedAssetsNetBlock = fixedAssetsNetBlock;
	}

	public BigDecimal getSundryDebtorsLessThan6M() {
		return sundryDebtorsLessThan6M;
	}

	public void setSundryDebtorsLessThan6M(BigDecimal sundryDebtorsLessThan6M) {
		this.sundryDebtorsLessThan6M = sundryDebtorsLessThan6M;
	}

	public BigDecimal getSundrydebtorsgreatorthan6M() {
		return sundrydebtorsgreatorthan6M;
	}

	public void setSundrydebtorsgreatorthan6M(BigDecimal sundrydebtorsgreatorthan6m) {
		sundrydebtorsgreatorthan6M = sundrydebtorsgreatorthan6m;
	}

	public String getItReturnFilingDate() {
		return itReturnFilingDate;
	}

	public void setItReturnFilingDate(String itReturnFilingDate) {
		this.itReturnFilingDate = itReturnFilingDate;
	}

	public BigDecimal getLiveFinancialObligationsInEmiCcodBaleetc() {
		return liveFinancialObligationsInEmiCcodBaleetc;
	}

	public void setLiveFinancialObligationsInEmiCcodBaleetc(BigDecimal liveFinancialObligationsInEmiCcodBaleetc) {
		this.liveFinancialObligationsInEmiCcodBaleetc = liveFinancialObligationsInEmiCcodBaleetc;
	}

	public BigDecimal getSumOfEmiAllLoans() {
		return sumOfEmiAllLoans;
	}

	public void setSumOfEmiAllLoans(BigDecimal sumOfEmiAllLoans) {
		this.sumOfEmiAllLoans = sumOfEmiAllLoans;
	}

	public BigDecimal getInterestObligation() {
		return interestObligation;
	}

	public void setInterestObligation(BigDecimal interestObligation) {
		this.interestObligation = interestObligation;
	}

	public BigDecimal getCashCreditorOverdraft() {
		return cashCreditorOverdraft;
	}

	public void setCashCreditorOverdraft(BigDecimal cashCreditorOverdraft) {
		this.cashCreditorOverdraft = cashCreditorOverdraft;
	}

	public BigDecimal getDbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M() {
		return dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M;
	}

	public void setDbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M(
			BigDecimal dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M) {
		this.dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M = dbtPosOfAlTheOutStndgLoansWhichAreNotClsInTheNext3M;
	}

	public BigDecimal getEquityTotAanGiblenetWorth() {
		return equityTotAanGiblenetWorth;
	}

	public void setEquityTotAanGiblenetWorth(BigDecimal equityTotAanGiblenetWorth) {
		this.equityTotAanGiblenetWorth = equityTotAanGiblenetWorth;
	}

	public BigDecimal getPostFundingDSCR() {
		return postFundingDSCR;
	}

	public void setPostFundingDSCR(BigDecimal postFundingDSCR) {
		this.postFundingDSCR = postFundingDSCR;
	}

	public BigDecimal getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(BigDecimal currentRatio) {
		this.currentRatio = currentRatio;
	}

	public BigDecimal getWorkingCapitalGap() {
		return workingCapitalGap;
	}

	public void setWorkingCapitalGap(BigDecimal workingCapitalGap) {
		this.workingCapitalGap = workingCapitalGap;
	}

	public BigDecimal getTurnOverYr1() {
		return turnOverYr1;
	}

	public void setTurnOverYr1(BigDecimal turnOverYr1) {
		this.turnOverYr1 = turnOverYr1;
	}

	public BigDecimal getTurnOverYr2() {
		return turnOverYr2;
	}

	public void setTurnOverYr2(BigDecimal turnOverYr2) {
		this.turnOverYr2 = turnOverYr2;
	}

	public BigDecimal getTurnOverGrowthLastYr() {
		return turnOverGrowthLastYr;
	}

	public void setTurnOverGrowthLastYr(BigDecimal turnOverGrowthLastYr) {
		this.turnOverGrowthLastYr = turnOverGrowthLastYr;
	}

	public BigDecimal getTurnOverGrowthLastToLastYr() {
		return turnOverGrowthLastToLastYr;
	}

	public void setTurnOverGrowthLastToLastYr(BigDecimal turnOverGrowthLastToLastYr) {
		this.turnOverGrowthLastToLastYr = turnOverGrowthLastToLastYr;
	}

	public BigDecimal getMinYrlyTurnOver() {
		return minYrlyTurnOver;
	}

	public void setMinYrlyTurnOver(BigDecimal minYrlyTurnOver) {
		this.minYrlyTurnOver = minYrlyTurnOver;
	}

	public BigDecimal getAvgValueOfTurnoverOfL3Yrs() {
		return avgValueOfTurnoverOfL3Yrs;
	}

	public void setAvgValueOfTurnoverOfL3Yrs(BigDecimal avgValueOfTurnoverOfL3Yrs) {
		this.avgValueOfTurnoverOfL3Yrs = avgValueOfTurnoverOfL3Yrs;
	}

	public BigDecimal getUtilizationLimitOnODorCC() {
		return utilizationLimitOnODorCC;
	}

	public void setUtilizationLimitOnODorCC(BigDecimal utilizationLimitOnODorCC) {
		this.utilizationLimitOnODorCC = utilizationLimitOnODorCC;
	}

	public BigDecimal getNetworth() {
		return networth;
	}

	public void setNetworth(BigDecimal networth) {
		this.networth = networth;
	}

	public String getOldestLoanDisbursedDate() {
		return oldestLoanDisbursedDate;
	}

	public void setOldestLoanDisbursedDate(String oldestLoanDisbursedDate) {
		this.oldestLoanDisbursedDate = oldestLoanDisbursedDate;
	}

	public CurrentAssests getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(CurrentAssests currentAssets) {
		this.currentAssets = currentAssets;
	}

	public CurrentLiabilities getCurrentLiabilittes() {
		return currentLiabilittes;
	}

	public void setCurrentLiabilittes(CurrentLiabilities currentLiabilittes) {
		this.currentLiabilittes = currentLiabilittes;
	}

	public BigDecimal getRetainedEarnings() {
		return retainedEarnings;
	}

	public void setRetainedEarnings(BigDecimal retainedEarnings) {
		this.retainedEarnings = retainedEarnings;
	}

	public BigDecimal getEquity() {
		return equity;
	}

	public void setEquity(BigDecimal equity) {
		this.equity = equity;
	}

	public BigDecimal getExistingLoanObligation() {
		return existingLoanObligation;
	}

	public void setExistingLoanObligation(BigDecimal existingLoanObligation) {
		this.existingLoanObligation = existingLoanObligation;
	}

	public BigDecimal getDebt() {
		return debt;
	}

	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}

	public BigDecimal getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}

	public BigDecimal getTotalLiabilities() {
		return totalLiabilities;
	}

	public void setTotalLiabilities(BigDecimal totalLiabilities) {
		this.totalLiabilities = totalLiabilities;
	}

	public BigDecimal getBorrowings() {
		return borrowings;
	}

	public void setBorrowings(BigDecimal borrowings) {
		this.borrowings = borrowings;
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

	public BigDecimal getOperationProfit() {
		return operationProfit;
	}

	public void setOperationProfit(BigDecimal operationProfit) {
		this.operationProfit = operationProfit;
	}

	public BigDecimal getWorkingCapitalCycle() {
		return workingCapitalCycle;
	}

	public void setWorkingCapitalCycle(BigDecimal workingCapitalCycle) {
		this.workingCapitalCycle = workingCapitalCycle;
	}

	public BigDecimal getFundsReceived() {
		return fundsReceived;
	}

	public void setFundsReceived(BigDecimal fundsReceived) {
		this.fundsReceived = fundsReceived;
	}

	public BigDecimal getAppliedTenor() {
		return appliedTenor;
	}

	public void setAppliedTenor(BigDecimal appliedTenor) {
		this.appliedTenor = appliedTenor;
	}

	public BigDecimal getStock() {
		return stock;
	}

	public void setStock(BigDecimal stock) {
		this.stock = stock;
	}

	public BigDecimal getLatestDebtors() {
		return latestDebtors;
	}

	public void setLatestDebtors(BigDecimal latestDebtors) {
		this.latestDebtors = latestDebtors;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getBank() {
		return bank;
	}

	public void setBank(BigDecimal bank) {
		this.bank = bank;
	}

	public BigDecimal getInvestment() {
		return investment;
	}

	public void setInvestment(BigDecimal investment) {
		this.investment = investment;
	}

	public BigDecimal getShorttermBorrowings() {
		return shorttermBorrowings;
	}

	public void setShorttermBorrowings(BigDecimal shorttermBorrowings) {
		this.shorttermBorrowings = shorttermBorrowings;
	}

	public BigDecimal getSundryDebtors() {
		return sundryDebtors;
	}

	public void setSundryDebtors(BigDecimal sundryDebtors) {
		this.sundryDebtors = sundryDebtors;
	}

	public BigDecimal getProvisions() {
		return provisions;
	}

	public void setProvisions(BigDecimal provisions) {
		this.provisions = provisions;
	}

	public VatServiceTaxForms getVatServiceTaxForm26as() {
		return vatServiceTaxForm26as;
	}

	public void setVatServiceTaxForm26as(VatServiceTaxForms vatServiceTaxForm26as) {
		this.vatServiceTaxForm26as = vatServiceTaxForm26as;
	}

}
