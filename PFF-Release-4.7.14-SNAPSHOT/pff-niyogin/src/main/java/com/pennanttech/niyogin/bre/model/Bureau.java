package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "noOfBusLoansOpenedL6M", "loanType", "prodIndexHL", "prodIndexAL", "prodIndexCC",
		"prodIndexBLOD", "prodIndexCLPTL", "clsTotDisbAmtL12M", "minPctPaidUnSecL1M", "pctTotOvdhicrL1M",
		"alMaxPctPaidSecL1M", "monSinceL30pOvralL12M", "alWorstAmtOvrdueSecL1M", "balDisbAmtRatioL1M", "clsTotDisbAmt",
		"maxDelinquencySecL12M", "noOf90DpdActvOvralL12M", "noOf90DpdOverallL12M", "minLoanamount", "maxLoanamount",
		"minTenure", "maxTenure", "noPrvsLoansAsOfAppDate", "isApplicant90PlusDpdinL6M", "isApplicantSubStandardinL6M",
		"isApplicantReportedAsLossinL6M", "isApplicantDoubtfulinL6M", "isApplicantMentionedAsSMA",
		"lastUpdateDtInBureau", "notenoughInfo", "loansTakenPostFinancialyrInBureau", "curBusLoanDisbAmt",
		"maxPctPaidUnSecL1M", "actTotDisbAmt", "alWorstCurBalOverallL1M", "worstCurBalOverallL1M", "maxPctPaidOvralL1M",
		"clPctTotBalHicrL1M", "totDisbAmt", "alAvgCurBalOverallL1M", "alCurBalOvralL1M", "worstCurBalSecL1M",
		"avgCurBalSecL1M", "curBalSecL1M", "alAvgCurBalSecL1M", "alWorstCurBalSecL1M", "worstCurBalUnSecL1M",
		"avgCurBalOverallL1M", "pctTotBalHicrL1M", "clMaxPctPaidUnSecL1M", "clMinPctPaidUnSecL1M", "avgCurBalUnSecL1M",
		"curBalUnSecL1M", "alMinPctPaidUnSecL1M", "alPctTotOvdHicrL1M", "pctTotOvdBalL1M", "clMinPctPaidOvralL1M",
		"alMaxPctPaidUnSecL1M", "ageOldestPrev", "alWorstCurtBalUnSecL1M", "alMaxPctPaidOvralL1M",
		"alAvgCurBalUnSecL1M", "worstAmtOvedueUnSecL1M", "avgAmtOvrdueUnSecL1M", "curOverdueUnSecL1M",
		"alPctTotoVdbalL1M", "clWorPrevBalUnSecL1M", "alPctTotBalHicrL1M", "ratioActvTotLoans", "clMaxPctPaidovralL1M",
		"worstAmtOvrdueOvralL1M", "avgAmtOvrdueOvralL1M", "curOverdueOvralL1M", "maxUnSecDisbAmtL12M",
		"maxSecDisbAmtL12M", "maxUnSecDisbAmtL6M", "minPctPaidOvralL1M", "clWorPrevBalOvralL1M", "ageOfCustomer",
		"alWorstAmtOvedueUnSecL1M", "alAvgAmtOvrdueUnSecL1M", "alCurOverdueUnSecL1M", "alWorstAmtOvrdueOvralL1M",
		"alAvgAmtOvrdueOvralL1M", "alCurOverdueOvralL1M", "totPrevLoans", "maxSecDisbAmtL6M", "alMinPctPaidOvralL1M",
		"clAvgCurBalUnSecL1M", "clCurBalUnSecL1M", "alMinPctPaidSecL1M", "maxDelinQuencyUnSecL12M", "minPctPaidSecL1M",
		"maxUnSecDisbAmtL3M", "maxSecDisbAmtL3M", "noOfActvLoansOnDisbdt", "clAvgCurBalOverallL1M", "clCurBalOvralL1M",
		"noOfClosedLoansL12M", "maxDelinQuencyOverallL12M", "clPctTotOvdBalL1M", "maxPctPaidSecL1M", "ageLattestprev",
		"monSinceL30pOvralL6M", "clWorstCurBalUnSecL1M", "noOfClosedLoansL6M", "noOf30DpdOverallL12M",
		"maxUnSecDisbAmtL1M", "maxDelinQuencyOverallL6M", "noOf30DpdOverallL6M", "clWorstCurBalOverallL1M", "score",
		"assetClassification", "totalEnquiries", "liveTradelines", "restructuredFlag", "sfFlag", "wdFlag", "woFlag",
		"settledFlag" })
@XmlRootElement(name = "BUREAU")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bureau {
	@XmlElement(name = "NOOFBUSLOANSOPENEDL6M")
	private int			noOfBusLoansOpenedL6M;

	@XmlElement(name = "LOANTYPE")
	private String		loanType;

	@XmlElement(name = "PRODINDEXHL")
	private int			prodIndexHL;

	@XmlElement(name = "PRODINDEXAL")
	private int			prodIndexAL;

	@XmlElement(name = "PRODINDEXCC")
	private int			prodIndexCC;

	@XmlElement(name = "PRODINDEXBLOD")
	private int			prodIndexBLOD;

	@XmlElement(name = "PRODINDEXCLPTL")
	private int			prodIndexCLPTL;

	@XmlElement(name = "CLSTOTDISBAMTL12M")
	private BigDecimal	clsTotDisbAmtL12M					= BigDecimal.ZERO;

	@XmlElement(name = "MINPCTPAIDUNSECL1M")
	private BigDecimal	minPctPaidUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "PCTTOTOVDHICRL1M")
	private BigDecimal	pctTotOvdhicrL1M					= BigDecimal.ZERO;

	@XmlElement(name = "ALMAXPCTPAIDSECL1M")
	private BigDecimal	alMaxPctPaidSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MONSINCEL30POVRALL12M")
	private int			monSinceL30pOvralL12M;

	@XmlElement(name = "ALWORSTAMTOVRDUESECL1M")
	private BigDecimal	alWorstAmtOvrdueSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "BALDISBAMTRATIOL1M")
	private BigDecimal	balDisbAmtRatioL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLSTOTDISBAMT")
	private BigDecimal	clsTotDisbAmt						= BigDecimal.ZERO;

	@XmlElement(name = "MAXDELINQUENCYSECL12M")
	private BigDecimal	maxDelinquencySecL12M				= BigDecimal.ZERO;

	@XmlElement(name = "NOOF90DPDACTVOVRALL12M")
	private int			noOf90DpdActvOvralL12M;

	@XmlElement(name = "NOOF90DPDOVERALLL12M")
	private int			noOf90DpdOverallL12M;

	@XmlElement(name = "MINIMUMLOANAMOUNT")
	private BigDecimal	minLoanamount						= BigDecimal.ZERO;

	@XmlElement(name = "MAXIMUMLOANAMOUNT")
	private BigDecimal	maxLoanamount						= BigDecimal.ZERO;

	@XmlElement(name = "MINIMUMTENURE")
	private BigDecimal	minTenure							= BigDecimal.ZERO;

	@XmlElement(name = "MAXIMUMTENURE")
	private BigDecimal	maxTenure							= BigDecimal.ZERO;

	@XmlElement(name = "NOPREVIOUSLOANSASOFAPPLICATIONDATE")
	private String		noPrvsLoansAsOfAppDate;

	@XmlElement(name = "ISAPPLICANT90PLUSDPDINLASTSIXMONTHS")
	private String		isApplicant90PlusDpdinL6M;

	@XmlElement(name = "ISAPPLICANTSUBSTANDARDINLASTSIXMONTHS")
	private String		isApplicantSubStandardinL6M;

	@XmlElement(name = "ISAPPLICANTREPORTEDASLOSSINLASTSIXMONTHS")
	private String		isApplicantReportedAsLossinL6M;

	@XmlElement(name = "ISAPPLICANTDOUBTFULINLASTSIXMONTHS")
	private String		isApplicantDoubtfulinL6M;

	@XmlElement(name = "ISAPPLICANTMENTIONEDASSMA")
	private String		isApplicantMentionedAsSMA;

	@XmlElement(name = "LASTUPDATEDATEINBUREAU")
	private String		lastUpdateDtInBureau;

	@XmlElement(name = "NOTENOUGHINFORMATION")
	private String		notenoughInfo;

	@XmlElement(name = "LOANSTAKENPOSTFINANCIALYEARINBUREAU")
	private BigDecimal	loansTakenPostFinancialyrInBureau	= BigDecimal.ZERO;

	@XmlElement(name = "CURBUSLOANDISBAMT")
	private BigDecimal	curBusLoanDisbAmt					= BigDecimal.ZERO;

	@XmlElement(name = "MAXPCTPAIDUNSECL1M")
	private BigDecimal	maxPctPaidUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "ACTVTOTDISBAMT")
	private BigDecimal	actTotDisbAmt						= BigDecimal.ZERO;

	@XmlElement(name = "ALWORSTCURBALOVERALLL1M")
	private BigDecimal	alWorstCurBalOverallL1M				= BigDecimal.ZERO;

	@XmlElement(name = "WORSTCURRENTBALOVERALLL1M")
	private BigDecimal	worstCurBalOverallL1M				= BigDecimal.ZERO;

	@XmlElement(name = "MAXPCTPAIDOVRALL1M")
	private BigDecimal	maxPctPaidOvralL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLPCTTOTBALHICRL1M")
	private BigDecimal	clPctTotBalHicrL1M					= BigDecimal.ZERO;

	@XmlElement(name = "TOTDISBAMT")
	private BigDecimal	totDisbAmt							= BigDecimal.ZERO;

	@XmlElement(name = "ALAVGCURBALOVERALLL1M")
	private BigDecimal	alAvgCurBalOverallL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALCURRENTBALOVRALL1M")
	private BigDecimal	alCurBalOvralL1M					= BigDecimal.ZERO;

	@XmlElement(name = "WORSTCURRENTBALSECL1M")
	private BigDecimal	worstCurBalSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "AVGCURRENTBALSECL1M")
	private BigDecimal	avgCurBalSecL1M						= BigDecimal.ZERO;

	@XmlElement(name = "CURRENTBALSECL1M")
	private BigDecimal	curBalSecL1M						= BigDecimal.ZERO;

	@XmlElement(name = "ALAVGCURBALSECL1M")
	private BigDecimal	alAvgCurBalSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "ALWORSTCURBALSECL1M")
	private BigDecimal	alWorstCurBalSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "WORSTCURRENTBALUNSECL1M")
	private BigDecimal	worstCurBalUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "AVGCURRENTBALOVERALLL1M")
	private BigDecimal	avgCurBalOverallL1M					= BigDecimal.ZERO;

	@XmlElement(name = "PCTTOTBALHICRL1M")
	private BigDecimal	pctTotBalHicrL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLMAXPCTPAIDUNSECL1M")
	private BigDecimal	clMaxPctPaidUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "CLMINPCTPAIDUNSECL1M")
	private BigDecimal	clMinPctPaidUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "AVGCURRENTBALUNSECL1M")
	private BigDecimal	avgCurBalUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CURRENTBALUNSECL1M")
	private BigDecimal	curBalUnSecL1M						= BigDecimal.ZERO;

	@XmlElement(name = "ALMINPCTPAIDUNSECL1M")
	private BigDecimal	alMinPctPaidUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALPCTTOTOVDHICRL1M")
	private BigDecimal	alPctTotOvdHicrL1M					= BigDecimal.ZERO;

	@XmlElement(name = "PCTTOTOVDBALL1M")
	private BigDecimal	pctTotOvdBalL1M						= BigDecimal.ZERO;

	@XmlElement(name = "CLMINPCTPAIDOVRALL1M")
	private BigDecimal	clMinPctPaidOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALMAXPCTPAIDUNSECL1M")
	private BigDecimal	alMaxPctPaidUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "AGEOLDESTPREV")
	private BigDecimal	ageOldestPrev						= BigDecimal.ZERO;

	@XmlElement(name = "ALWORSTCURTBALUNSECL1M")
	private BigDecimal	alWorstCurtBalUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALMAXPCTPAIDOVRALL1M")
	private BigDecimal	alMaxPctPaidOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALAVGCURBALUNSECL1M")
	private BigDecimal	alAvgCurBalUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "WORSTAMTOVEDUEUNSECL1M")
	private BigDecimal	worstAmtOvedueUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "AVGAMTOVRDUEUNSECL1M")
	private BigDecimal	avgAmtOvrdueUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "CURRENTOVERDUEUNSECL1M")
	private BigDecimal	curOverdueUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "ALPCTTOTOVDBALL1M")
	private BigDecimal	alPctTotoVdbalL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLWORPREVBALUNSECL1M")
	private BigDecimal	clWorPrevBalUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALPCTTOTBALHICRL1M")
	private BigDecimal	alPctTotBalHicrL1M					= BigDecimal.ZERO;

	@XmlElement(name = "RATIOACTVTOTLOANS")
	private BigDecimal	ratioActvTotLoans					= BigDecimal.ZERO;

	@XmlElement(name = "CLMAXPCTPAIDOVRALL1M")
	private BigDecimal	clMaxPctPaidovralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "WORSTAMTOVRDUEOVRALL1M")
	private BigDecimal	worstAmtOvrdueOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "AVGAMTOVRDUEOVRALL1M")
	private BigDecimal	avgAmtOvrdueOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "CURRENTOVERDUEOVRALL1M")
	private BigDecimal	curOverdueOvralL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXUNSECDISBAMTL12M")
	private BigDecimal	maxUnSecDisbAmtL12M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXSECDISBAMTL12M")
	private BigDecimal	maxSecDisbAmtL12M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXUNSECDISBAMTL6M")
	private BigDecimal	maxUnSecDisbAmtL6M					= BigDecimal.ZERO;

	@XmlElement(name = "MINPCTPAIDOVRALL1M")
	private BigDecimal	minPctPaidOvralL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLWORPREVBALOVRALL1M")
	private BigDecimal	clWorPrevBalOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "AGEOFCUSTOMER")
	private int			ageOfCustomer;

	@XmlElement(name = "ALWORSTAMTOVEDUEUNSECL1M")
	private BigDecimal	alWorstAmtOvedueUnSecL1M			= BigDecimal.ZERO;

	@XmlElement(name = "ALAVGAMTOVRDUEUNSECL1M")
	private BigDecimal	alAvgAmtOvrdueUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALCURRENTOVERDUEUNSECL1M")
	private BigDecimal	alCurOverdueUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALWORSTAMTOVRDUEOVRALL1M")
	private BigDecimal	alWorstAmtOvrdueOvralL1M			= BigDecimal.ZERO;

	@XmlElement(name = "ALAVGAMTOVRDUEOVRALL1M")
	private BigDecimal	alAvgAmtOvrdueOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "ALCURRENTOVERDUEOVRALL1M")
	private BigDecimal	alCurOverdueOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "TOTALPREVLOANS")
	private BigDecimal	totPrevLoans						= BigDecimal.ZERO;

	@XmlElement(name = "MAXSECDISBAMTL6M")
	private BigDecimal	maxSecDisbAmtL6M					= BigDecimal.ZERO;

	@XmlElement(name = "ALMINPCTPAIDOVRALL1M")
	private BigDecimal	alMinPctPaidOvralL1M				= BigDecimal.ZERO;

	@XmlElement(name = "CLAVGCURBALUNSECL1M")
	private BigDecimal	clAvgCurBalUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "CLCURRENTBALUNSECL1M")
	private BigDecimal	clCurBalUnSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "ALMINPCTPAIDSECL1M")
	private BigDecimal	alMinPctPaidSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXDELINQUENCYUNSECL12M")
	private BigDecimal	maxDelinQuencyUnSecL12M				= BigDecimal.ZERO;

	@XmlElement(name = "MINPCTPAIDSECL1M")
	private BigDecimal	minPctPaidSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXUNSECDISBAMTL3M")
	private BigDecimal	maxUnSecDisbAmtL3M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXSECDISBAMTL3M")
	private BigDecimal	maxSecDisbAmtL3M					= BigDecimal.ZERO;

	@XmlElement(name = "NOOFACTVLOANSONDISBDT")
	private BigDecimal	noOfActvLoansOnDisbdt				= BigDecimal.ZERO;

	@XmlElement(name = "CLAVGCURBALOVERALLL1M")
	private BigDecimal	clAvgCurBalOverallL1M				= BigDecimal.ZERO;

	@XmlElement(name = "CLCURRENTBALOVRALL1M")
	private BigDecimal	clCurBalOvralL1M					= BigDecimal.ZERO;

	@XmlElement(name = "NOOFCLOSEDLOANSL12M")
	private BigDecimal	noOfClosedLoansL12M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXDELINQUENCYOVERALLL12M")
	private BigDecimal	maxDelinQuencyOverallL12M			= BigDecimal.ZERO;

	@XmlElement(name = "CLPCTTOTOVDBALL1M")
	private BigDecimal	clPctTotOvdBalL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXPCTPAIDSECL1M")
	private BigDecimal	maxPctPaidSecL1M					= BigDecimal.ZERO;

	@XmlElement(name = "AGELATESTPREV")
	private BigDecimal	ageLattestprev						= BigDecimal.ZERO;

	@XmlElement(name = "MONSINCEL30POVRALL6M")
	private BigDecimal	monSinceL30pOvralL6M				= BigDecimal.ZERO;

	@XmlElement(name = "CLWORSTCURBALUNSECL1M")
	private BigDecimal	clWorstCurBalUnSecL1M				= BigDecimal.ZERO;

	@XmlElement(name = "NOOFCLOSEDLOANSL6M")
	private BigDecimal	noOfClosedLoansL6M					= BigDecimal.ZERO;

	@XmlElement(name = "NOOF30DPDOVERALLL12M")
	private BigDecimal	noOf30DpdOverallL12M				= BigDecimal.ZERO;

	@XmlElement(name = "MAXUNSECDISBAMTL1M")
	private BigDecimal	maxUnSecDisbAmtL1M					= BigDecimal.ZERO;

	@XmlElement(name = "MAXDELINQUENCYOVERALLL6M")
	private BigDecimal	maxDelinQuencyOverallL6M			= BigDecimal.ZERO;

	@XmlElement(name = "NOOF30DPDOVERALLL6M")
	private BigDecimal	noOf30DpdOverallL6M					= BigDecimal.ZERO;

	@XmlElement(name = "CLWORSTCURBALOVERALLL1M")
	private BigDecimal	clWorstCurBalOverallL1M				= BigDecimal.ZERO;

	@XmlElement(name = "SCORE")
	private BigDecimal	score								= BigDecimal.ZERO;

	@XmlElement(name = "ASSETCLASSIFICATION")
	private BigDecimal	assetClassification					= BigDecimal.ZERO;

	@XmlElement(name = "TOTALENQUIRIES")
	private int			totalEnquiries;

	@XmlElement(name = "LIVETRADELINES")
	private BigDecimal	liveTradelines						= BigDecimal.ZERO;

	@XmlElement(name = "RESTRUCTUREDFLAG")
	private String		restructuredFlag;

	@XmlElement(name = "SFFLAG")
	private String		sfFlag;

	@XmlElement(name = "WDFLAG")
	private String		wdFlag;

	@XmlElement(name = "WOFLAG")
	private String		woFlag;

	@XmlElement(name = "SETTLEDFLAG")
	private String		settledFlag;

	public int getNoOfBusLoansOpenedL6M() {
		return noOfBusLoansOpenedL6M;
	}

	public void setNoOfBusLoansOpenedL6M(int noOfBusLoansOpenedL6M) {
		this.noOfBusLoansOpenedL6M = noOfBusLoansOpenedL6M;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public int getProdIndexHL() {
		return prodIndexHL;
	}

	public void setProdIndexHL(int prodIndexHL) {
		this.prodIndexHL = prodIndexHL;
	}

	public int getProdIndexAL() {
		return prodIndexAL;
	}

	public void setProdIndexAL(int prodIndexAL) {
		this.prodIndexAL = prodIndexAL;
	}

	public int getProdIndexCC() {
		return prodIndexCC;
	}

	public void setProdIndexCC(int prodIndexCC) {
		this.prodIndexCC = prodIndexCC;
	}

	public int getProdIndexBLOD() {
		return prodIndexBLOD;
	}

	public void setProdIndexBLOD(int prodIndexBLOD) {
		this.prodIndexBLOD = prodIndexBLOD;
	}

	public int getProdIndexCLPTL() {
		return prodIndexCLPTL;
	}

	public void setProdIndexCLPTL(int prodIndexCLPTL) {
		this.prodIndexCLPTL = prodIndexCLPTL;
	}

	public BigDecimal getClsTotDisbAmtL12M() {
		return clsTotDisbAmtL12M;
	}

	public void setClsTotDisbAmtL12M(BigDecimal clsTotDisbAmtL12M) {
		this.clsTotDisbAmtL12M = clsTotDisbAmtL12M;
	}

	public BigDecimal getMinPctPaidUnSecL1M() {
		return minPctPaidUnSecL1M;
	}

	public void setMinPctPaidUnSecL1M(BigDecimal minPctPaidUnSecL1M) {
		this.minPctPaidUnSecL1M = minPctPaidUnSecL1M;
	}

	public BigDecimal getPctTotOvdhicrL1M() {
		return pctTotOvdhicrL1M;
	}

	public void setPctTotOvdhicrL1M(BigDecimal pctTotOvdhicrL1M) {
		this.pctTotOvdhicrL1M = pctTotOvdhicrL1M;
	}

	public BigDecimal getAlMaxPctPaidSecL1M() {
		return alMaxPctPaidSecL1M;
	}

	public void setAlMaxPctPaidSecL1M(BigDecimal alMaxPctPaidSecL1M) {
		this.alMaxPctPaidSecL1M = alMaxPctPaidSecL1M;
	}

	public int getMonSinceL30pOvralL12M() {
		return monSinceL30pOvralL12M;
	}

	public void setMonSinceL30pOvralL12M(int monSinceL30pOvralL12M) {
		this.monSinceL30pOvralL12M = monSinceL30pOvralL12M;
	}

	public BigDecimal getAlWorstAmtOvrdueSecL1M() {
		return alWorstAmtOvrdueSecL1M;
	}

	public void setAlWorstAmtOvrdueSecL1M(BigDecimal alWorstAmtOvrdueSecL1M) {
		this.alWorstAmtOvrdueSecL1M = alWorstAmtOvrdueSecL1M;
	}

	public BigDecimal getBalDisbAmtRatioL1M() {
		return balDisbAmtRatioL1M;
	}

	public void setBalDisbAmtRatioL1M(BigDecimal balDisbAmtRatioL1M) {
		this.balDisbAmtRatioL1M = balDisbAmtRatioL1M;
	}

	public BigDecimal getClsTotDisbAmt() {
		return clsTotDisbAmt;
	}

	public void setClsTotDisbAmt(BigDecimal clsTotDisbAmt) {
		this.clsTotDisbAmt = clsTotDisbAmt;
	}

	public BigDecimal getMaxDelinquencySecL12M() {
		return maxDelinquencySecL12M;
	}

	public void setMaxDelinquencySecL12M(BigDecimal maxDelinquencySecL12M) {
		this.maxDelinquencySecL12M = maxDelinquencySecL12M;
	}

	public int getNoOf90DpdActvOvralL12M() {
		return noOf90DpdActvOvralL12M;
	}

	public void setNoOf90DpdActvOvralL12M(int noOf90DpdActvOvralL12M) {
		this.noOf90DpdActvOvralL12M = noOf90DpdActvOvralL12M;
	}

	public int getNoOf90DpdOverallL12M() {
		return noOf90DpdOverallL12M;
	}

	public void setNoOf90DpdOverallL12M(int noOf90DpdOverallL12M) {
		this.noOf90DpdOverallL12M = noOf90DpdOverallL12M;
	}

	public BigDecimal getMinLoanamount() {
		return minLoanamount;
	}

	public void setMinLoanamount(BigDecimal minLoanamount) {
		this.minLoanamount = minLoanamount;
	}

	public BigDecimal getMaxLoanamount() {
		return maxLoanamount;
	}

	public void setMaxLoanamount(BigDecimal maxLoanamount) {
		this.maxLoanamount = maxLoanamount;
	}

	public BigDecimal getMinTenure() {
		return minTenure;
	}

	public void setMinTenure(BigDecimal minTenure) {
		this.minTenure = minTenure;
	}

	public BigDecimal getMaxTenure() {
		return maxTenure;
	}

	public void setMaxTenure(BigDecimal maxTenure) {
		this.maxTenure = maxTenure;
	}

	public String getNoPrvsLoansAsOfAppDate() {
		return noPrvsLoansAsOfAppDate;
	}

	public void setNoPrvsLoansAsOfAppDate(String noPrvsLoansAsOfAppDate) {
		this.noPrvsLoansAsOfAppDate = noPrvsLoansAsOfAppDate;
	}

	public String getIsApplicant90PlusDpdinL6M() {
		return isApplicant90PlusDpdinL6M;
	}

	public void setIsApplicant90PlusDpdinL6M(String isApplicant90PlusDpdinL6M) {
		this.isApplicant90PlusDpdinL6M = isApplicant90PlusDpdinL6M;
	}

	public String getIsApplicantSubStandardinL6M() {
		return isApplicantSubStandardinL6M;
	}

	public void setIsApplicantSubStandardinL6M(String isApplicantSubStandardinL6M) {
		this.isApplicantSubStandardinL6M = isApplicantSubStandardinL6M;
	}

	public String getIsApplicantReportedAsLossinL6M() {
		return isApplicantReportedAsLossinL6M;
	}

	public void setIsApplicantReportedAsLossinL6M(String isApplicantReportedAsLossinL6M) {
		this.isApplicantReportedAsLossinL6M = isApplicantReportedAsLossinL6M;
	}

	public String getIsApplicantDoubtfulinL6M() {
		return isApplicantDoubtfulinL6M;
	}

	public void setIsApplicantDoubtfulinL6M(String isApplicantDoubtfulinL6M) {
		this.isApplicantDoubtfulinL6M = isApplicantDoubtfulinL6M;
	}

	public String getIsApplicantMentionedAsSMA() {
		return isApplicantMentionedAsSMA;
	}

	public void setIsApplicantMentionedAsSMA(String isApplicantMentionedAsSMA) {
		this.isApplicantMentionedAsSMA = isApplicantMentionedAsSMA;
	}

	public String getLastUpdateDtInBureau() {
		return lastUpdateDtInBureau;
	}

	public void setLastUpdateDtInBureau(String lastUpdateDtInBureau) {
		this.lastUpdateDtInBureau = lastUpdateDtInBureau;
	}

	public String getNotenoughInfo() {
		return notenoughInfo;
	}

	public void setNotenoughInfo(String notenoughInfo) {
		this.notenoughInfo = notenoughInfo;
	}

	public BigDecimal getLoansTakenPostFinancialyrInBureau() {
		return loansTakenPostFinancialyrInBureau;
	}

	public void setLoansTakenPostFinancialyrInBureau(BigDecimal loansTakenPostFinancialyrInBureau) {
		this.loansTakenPostFinancialyrInBureau = loansTakenPostFinancialyrInBureau;
	}

	public BigDecimal getCurBusLoanDisbAmt() {
		return curBusLoanDisbAmt;
	}

	public void setCurBusLoanDisbAmt(BigDecimal curBusLoanDisbAmt) {
		this.curBusLoanDisbAmt = curBusLoanDisbAmt;
	}

	public BigDecimal getMaxPctPaidUnSecL1M() {
		return maxPctPaidUnSecL1M;
	}

	public void setMaxPctPaidUnSecL1M(BigDecimal maxPctPaidUnSecL1M) {
		this.maxPctPaidUnSecL1M = maxPctPaidUnSecL1M;
	}

	public BigDecimal getActTotDisbAmt() {
		return actTotDisbAmt;
	}

	public void setActTotDisbAmt(BigDecimal actTotDisbAmt) {
		this.actTotDisbAmt = actTotDisbAmt;
	}

	public BigDecimal getAlWorstCurBalOverallL1M() {
		return alWorstCurBalOverallL1M;
	}

	public void setAlWorstCurBalOverallL1M(BigDecimal alWorstCurBalOverallL1M) {
		this.alWorstCurBalOverallL1M = alWorstCurBalOverallL1M;
	}

	public BigDecimal getWorstCurBalOverallL1M() {
		return worstCurBalOverallL1M;
	}

	public void setWorstCurBalOverallL1M(BigDecimal worstCurBalOverallL1M) {
		this.worstCurBalOverallL1M = worstCurBalOverallL1M;
	}

	public BigDecimal getMaxPctPaidOvralL1M() {
		return maxPctPaidOvralL1M;
	}

	public void setMaxPctPaidOvralL1M(BigDecimal maxPctPaidOvralL1M) {
		this.maxPctPaidOvralL1M = maxPctPaidOvralL1M;
	}

	public BigDecimal getClPctTotBalHicrL1M() {
		return clPctTotBalHicrL1M;
	}

	public void setClPctTotBalHicrL1M(BigDecimal clPctTotBalHicrL1M) {
		this.clPctTotBalHicrL1M = clPctTotBalHicrL1M;
	}

	public BigDecimal getTotDisbAmt() {
		return totDisbAmt;
	}

	public void setTotDisbAmt(BigDecimal totDisbAmt) {
		this.totDisbAmt = totDisbAmt;
	}

	public BigDecimal getAlAvgCurBalOverallL1M() {
		return alAvgCurBalOverallL1M;
	}

	public void setAlAvgCurBalOverallL1M(BigDecimal alAvgCurBalOverallL1M) {
		this.alAvgCurBalOverallL1M = alAvgCurBalOverallL1M;
	}

	public BigDecimal getAlCurBalOvralL1M() {
		return alCurBalOvralL1M;
	}

	public void setAlCurBalOvralL1M(BigDecimal alCurBalOvralL1M) {
		this.alCurBalOvralL1M = alCurBalOvralL1M;
	}

	public BigDecimal getWorstCurBalSecL1M() {
		return worstCurBalSecL1M;
	}

	public void setWorstCurBalSecL1M(BigDecimal worstCurBalSecL1M) {
		this.worstCurBalSecL1M = worstCurBalSecL1M;
	}

	public BigDecimal getAvgCurBalSecL1M() {
		return avgCurBalSecL1M;
	}

	public void setAvgCurBalSecL1M(BigDecimal avgCurBalSecL1M) {
		this.avgCurBalSecL1M = avgCurBalSecL1M;
	}

	public BigDecimal getCurBalSecL1M() {
		return curBalSecL1M;
	}

	public void setCurBalSecL1M(BigDecimal curBalSecL1M) {
		this.curBalSecL1M = curBalSecL1M;
	}

	public BigDecimal getAlAvgCurBalSecL1M() {
		return alAvgCurBalSecL1M;
	}

	public void setAlAvgCurBalSecL1M(BigDecimal alAvgCurBalSecL1M) {
		this.alAvgCurBalSecL1M = alAvgCurBalSecL1M;
	}

	public BigDecimal getAlWorstCurBalSecL1M() {
		return alWorstCurBalSecL1M;
	}

	public void setAlWorstCurBalSecL1M(BigDecimal alWorstCurBalSecL1M) {
		this.alWorstCurBalSecL1M = alWorstCurBalSecL1M;
	}

	public BigDecimal getWorstCurBalUnSecL1M() {
		return worstCurBalUnSecL1M;
	}

	public void setWorstCurBalUnSecL1M(BigDecimal worstCurBalUnSecL1M) {
		this.worstCurBalUnSecL1M = worstCurBalUnSecL1M;
	}

	public BigDecimal getAvgCurBalOverallL1M() {
		return avgCurBalOverallL1M;
	}

	public void setAvgCurBalOverallL1M(BigDecimal avgCurBalOverallL1M) {
		this.avgCurBalOverallL1M = avgCurBalOverallL1M;
	}

	public BigDecimal getPctTotBalHicrL1M() {
		return pctTotBalHicrL1M;
	}

	public void setPctTotBalHicrL1M(BigDecimal pctTotBalHicrL1M) {
		this.pctTotBalHicrL1M = pctTotBalHicrL1M;
	}

	public BigDecimal getClMaxPctPaidUnSecL1M() {
		return clMaxPctPaidUnSecL1M;
	}

	public void setClMaxPctPaidUnSecL1M(BigDecimal clMaxPctPaidUnSecL1M) {
		this.clMaxPctPaidUnSecL1M = clMaxPctPaidUnSecL1M;
	}

	public BigDecimal getClMinPctPaidUnSecL1M() {
		return clMinPctPaidUnSecL1M;
	}

	public void setClMinPctPaidUnSecL1M(BigDecimal clMinPctPaidUnSecL1M) {
		this.clMinPctPaidUnSecL1M = clMinPctPaidUnSecL1M;
	}

	public BigDecimal getAvgCurBalUnSecL1M() {
		return avgCurBalUnSecL1M;
	}

	public void setAvgCurBalUnSecL1M(BigDecimal avgCurBalUnSecL1M) {
		this.avgCurBalUnSecL1M = avgCurBalUnSecL1M;
	}

	public BigDecimal getCurBalUnSecL1M() {
		return curBalUnSecL1M;
	}

	public void setCurBalUnSecL1M(BigDecimal curBalUnSecL1M) {
		this.curBalUnSecL1M = curBalUnSecL1M;
	}

	public BigDecimal getAlMinPctPaidUnSecL1M() {
		return alMinPctPaidUnSecL1M;
	}

	public void setAlMinPctPaidUnSecL1M(BigDecimal alMinPctPaidUnSecL1M) {
		this.alMinPctPaidUnSecL1M = alMinPctPaidUnSecL1M;
	}

	public BigDecimal getAlPctTotOvdHicrL1M() {
		return alPctTotOvdHicrL1M;
	}

	public void setAlPctTotOvdHicrL1M(BigDecimal alPctTotOvdHicrL1M) {
		this.alPctTotOvdHicrL1M = alPctTotOvdHicrL1M;
	}

	public BigDecimal getPctTotOvdBalL1M() {
		return pctTotOvdBalL1M;
	}

	public void setPctTotOvdBalL1M(BigDecimal pctTotOvdBalL1M) {
		this.pctTotOvdBalL1M = pctTotOvdBalL1M;
	}

	public BigDecimal getClMinPctPaidOvralL1M() {
		return clMinPctPaidOvralL1M;
	}

	public void setClMinPctPaidOvralL1M(BigDecimal clMinPctPaidOvralL1M) {
		this.clMinPctPaidOvralL1M = clMinPctPaidOvralL1M;
	}

	public BigDecimal getAlMaxPctPaidUnSecL1M() {
		return alMaxPctPaidUnSecL1M;
	}

	public void setAlMaxPctPaidUnSecL1M(BigDecimal alMaxPctPaidUnSecL1M) {
		this.alMaxPctPaidUnSecL1M = alMaxPctPaidUnSecL1M;
	}

	public BigDecimal getAgeOldestPrev() {
		return ageOldestPrev;
	}

	public void setAgeOldestPrev(BigDecimal ageOldestPrev) {
		this.ageOldestPrev = ageOldestPrev;
	}

	public BigDecimal getAlWorstCurtBalUnSecL1M() {
		return alWorstCurtBalUnSecL1M;
	}

	public void setAlWorstCurtBalUnSecL1M(BigDecimal alWorstCurtBalUnSecL1M) {
		this.alWorstCurtBalUnSecL1M = alWorstCurtBalUnSecL1M;
	}

	public BigDecimal getAlMaxPctPaidOvralL1M() {
		return alMaxPctPaidOvralL1M;
	}

	public void setAlMaxPctPaidOvralL1M(BigDecimal alMaxPctPaidOvralL1M) {
		this.alMaxPctPaidOvralL1M = alMaxPctPaidOvralL1M;
	}

	public BigDecimal getAlAvgCurBalUnSecL1M() {
		return alAvgCurBalUnSecL1M;
	}

	public void setAlAvgCurBalUnSecL1M(BigDecimal alAvgCurBalUnSecL1M) {
		this.alAvgCurBalUnSecL1M = alAvgCurBalUnSecL1M;
	}

	public BigDecimal getWorstAmtOvedueUnSecL1M() {
		return worstAmtOvedueUnSecL1M;
	}

	public void setWorstAmtOvedueUnSecL1M(BigDecimal worstAmtOvedueUnSecL1M) {
		this.worstAmtOvedueUnSecL1M = worstAmtOvedueUnSecL1M;
	}

	public BigDecimal getAvgAmtOvrdueUnSecL1M() {
		return avgAmtOvrdueUnSecL1M;
	}

	public void setAvgAmtOvrdueUnSecL1M(BigDecimal avgAmtOvrdueUnSecL1M) {
		this.avgAmtOvrdueUnSecL1M = avgAmtOvrdueUnSecL1M;
	}

	public BigDecimal getCurOverdueUnSecL1M() {
		return curOverdueUnSecL1M;
	}

	public void setCurOverdueUnSecL1M(BigDecimal curOverdueUnSecL1M) {
		this.curOverdueUnSecL1M = curOverdueUnSecL1M;
	}

	public BigDecimal getAlPctTotoVdbalL1M() {
		return alPctTotoVdbalL1M;
	}

	public void setAlPctTotoVdbalL1M(BigDecimal alPctTotoVdbalL1M) {
		this.alPctTotoVdbalL1M = alPctTotoVdbalL1M;
	}

	public BigDecimal getClWorPrevBalUnSecL1M() {
		return clWorPrevBalUnSecL1M;
	}

	public void setClWorPrevBalUnSecL1M(BigDecimal clWorPrevBalUnSecL1M) {
		this.clWorPrevBalUnSecL1M = clWorPrevBalUnSecL1M;
	}

	public BigDecimal getAlPctTotBalHicrL1M() {
		return alPctTotBalHicrL1M;
	}

	public void setAlPctTotBalHicrL1M(BigDecimal alPctTotBalHicrL1M) {
		this.alPctTotBalHicrL1M = alPctTotBalHicrL1M;
	}

	public BigDecimal getRatioActvTotLoans() {
		return ratioActvTotLoans;
	}

	public void setRatioActvTotLoans(BigDecimal ratioActvTotLoans) {
		this.ratioActvTotLoans = ratioActvTotLoans;
	}

	public BigDecimal getClMaxPctPaidovralL1M() {
		return clMaxPctPaidovralL1M;
	}

	public void setClMaxPctPaidovralL1M(BigDecimal clMaxPctPaidovralL1M) {
		this.clMaxPctPaidovralL1M = clMaxPctPaidovralL1M;
	}

	public BigDecimal getWorstAmtOvrdueOvralL1M() {
		return worstAmtOvrdueOvralL1M;
	}

	public void setWorstAmtOvrdueOvralL1M(BigDecimal worstAmtOvrdueOvralL1M) {
		this.worstAmtOvrdueOvralL1M = worstAmtOvrdueOvralL1M;
	}

	public BigDecimal getAvgAmtOvrdueOvralL1M() {
		return avgAmtOvrdueOvralL1M;
	}

	public void setAvgAmtOvrdueOvralL1M(BigDecimal avgAmtOvrdueOvralL1M) {
		this.avgAmtOvrdueOvralL1M = avgAmtOvrdueOvralL1M;
	}

	public BigDecimal getCurOverdueOvralL1M() {
		return curOverdueOvralL1M;
	}

	public void setCurOverdueOvralL1M(BigDecimal curOverdueOvralL1M) {
		this.curOverdueOvralL1M = curOverdueOvralL1M;
	}

	public BigDecimal getMaxUnSecDisbAmtL12M() {
		return maxUnSecDisbAmtL12M;
	}

	public void setMaxUnSecDisbAmtL12M(BigDecimal maxUnSecDisbAmtL12M) {
		this.maxUnSecDisbAmtL12M = maxUnSecDisbAmtL12M;
	}

	public BigDecimal getMaxSecDisbAmtL12M() {
		return maxSecDisbAmtL12M;
	}

	public void setMaxSecDisbAmtL12M(BigDecimal maxSecDisbAmtL12M) {
		this.maxSecDisbAmtL12M = maxSecDisbAmtL12M;
	}

	public BigDecimal getMaxUnSecDisbAmtL6M() {
		return maxUnSecDisbAmtL6M;
	}

	public void setMaxUnSecDisbAmtL6M(BigDecimal maxUnSecDisbAmtL6M) {
		this.maxUnSecDisbAmtL6M = maxUnSecDisbAmtL6M;
	}

	public BigDecimal getMinPctPaidOvralL1M() {
		return minPctPaidOvralL1M;
	}

	public void setMinPctPaidOvralL1M(BigDecimal minPctPaidOvralL1M) {
		this.minPctPaidOvralL1M = minPctPaidOvralL1M;
	}

	public BigDecimal getClWorPrevBalOvralL1M() {
		return clWorPrevBalOvralL1M;
	}

	public void setClWorPrevBalOvralL1M(BigDecimal clWorPrevBalOvralL1M) {
		this.clWorPrevBalOvralL1M = clWorPrevBalOvralL1M;
	}

	public int getAgeOfCustomer() {
		return ageOfCustomer;
	}

	public void setAgeOfCustomer(int ageOfCustomer) {
		this.ageOfCustomer = ageOfCustomer;
	}

	public BigDecimal getAlWorstAmtOvedueUnSecL1M() {
		return alWorstAmtOvedueUnSecL1M;
	}

	public void setAlWorstAmtOvedueUnSecL1M(BigDecimal alWorstAmtOvedueUnSecL1M) {
		this.alWorstAmtOvedueUnSecL1M = alWorstAmtOvedueUnSecL1M;
	}

	public BigDecimal getAlAvgAmtOvrdueUnSecL1M() {
		return alAvgAmtOvrdueUnSecL1M;
	}

	public void setAlAvgAmtOvrdueUnSecL1M(BigDecimal alAvgAmtOvrdueUnSecL1M) {
		this.alAvgAmtOvrdueUnSecL1M = alAvgAmtOvrdueUnSecL1M;
	}

	public BigDecimal getAlCurOverdueUnSecL1M() {
		return alCurOverdueUnSecL1M;
	}

	public void setAlCurOverdueUnSecL1M(BigDecimal alCurOverdueUnSecL1M) {
		this.alCurOverdueUnSecL1M = alCurOverdueUnSecL1M;
	}

	public BigDecimal getAlWorstAmtOvrdueOvralL1M() {
		return alWorstAmtOvrdueOvralL1M;
	}

	public void setAlWorstAmtOvrdueOvralL1M(BigDecimal alWorstAmtOvrdueOvralL1M) {
		this.alWorstAmtOvrdueOvralL1M = alWorstAmtOvrdueOvralL1M;
	}

	public BigDecimal getAlAvgAmtOvrdueOvralL1M() {
		return alAvgAmtOvrdueOvralL1M;
	}

	public void setAlAvgAmtOvrdueOvralL1M(BigDecimal alAvgAmtOvrdueOvralL1M) {
		this.alAvgAmtOvrdueOvralL1M = alAvgAmtOvrdueOvralL1M;
	}

	public BigDecimal getAlCurOverdueOvralL1M() {
		return alCurOverdueOvralL1M;
	}

	public void setAlCurOverdueOvralL1M(BigDecimal alCurOverdueOvralL1M) {
		this.alCurOverdueOvralL1M = alCurOverdueOvralL1M;
	}

	public BigDecimal getTotPrevLoans() {
		return totPrevLoans;
	}

	public void setTotPrevLoans(BigDecimal totPrevLoans) {
		this.totPrevLoans = totPrevLoans;
	}

	public BigDecimal getMaxSecDisbAmtL6M() {
		return maxSecDisbAmtL6M;
	}

	public void setMaxSecDisbAmtL6M(BigDecimal maxSecDisbAmtL6M) {
		this.maxSecDisbAmtL6M = maxSecDisbAmtL6M;
	}

	public BigDecimal getAlMinPctPaidOvralL1M() {
		return alMinPctPaidOvralL1M;
	}

	public void setAlMinPctPaidOvralL1M(BigDecimal alMinPctPaidOvralL1M) {
		this.alMinPctPaidOvralL1M = alMinPctPaidOvralL1M;
	}

	public BigDecimal getClAvgCurBalUnSecL1M() {
		return clAvgCurBalUnSecL1M;
	}

	public void setClAvgCurBalUnSecL1M(BigDecimal clAvgCurBalUnSecL1M) {
		this.clAvgCurBalUnSecL1M = clAvgCurBalUnSecL1M;
	}

	public BigDecimal getClCurBalUnSecL1M() {
		return clCurBalUnSecL1M;
	}

	public void setClCurBalUnSecL1M(BigDecimal clCurBalUnSecL1M) {
		this.clCurBalUnSecL1M = clCurBalUnSecL1M;
	}

	public BigDecimal getAlMinPctPaidSecL1M() {
		return alMinPctPaidSecL1M;
	}

	public void setAlMinPctPaidSecL1M(BigDecimal alMinPctPaidSecL1M) {
		this.alMinPctPaidSecL1M = alMinPctPaidSecL1M;
	}

	public BigDecimal getMaxDelinQuencyUnSecL12M() {
		return maxDelinQuencyUnSecL12M;
	}

	public void setMaxDelinQuencyUnSecL12M(BigDecimal maxDelinQuencyUnSecL12M) {
		this.maxDelinQuencyUnSecL12M = maxDelinQuencyUnSecL12M;
	}

	public BigDecimal getMinPctPaidSecL1M() {
		return minPctPaidSecL1M;
	}

	public void setMinPctPaidSecL1M(BigDecimal minPctPaidSecL1M) {
		this.minPctPaidSecL1M = minPctPaidSecL1M;
	}

	public BigDecimal getMaxUnSecDisbAmtL3M() {
		return maxUnSecDisbAmtL3M;
	}

	public void setMaxUnSecDisbAmtL3M(BigDecimal maxUnSecDisbAmtL3M) {
		this.maxUnSecDisbAmtL3M = maxUnSecDisbAmtL3M;
	}

	public BigDecimal getMaxSecDisbAmtL3M() {
		return maxSecDisbAmtL3M;
	}

	public void setMaxSecDisbAmtL3M(BigDecimal maxSecDisbAmtL3M) {
		this.maxSecDisbAmtL3M = maxSecDisbAmtL3M;
	}

	public BigDecimal getNoOfActvLoansOnDisbdt() {
		return noOfActvLoansOnDisbdt;
	}

	public void setNoOfActvLoansOnDisbdt(BigDecimal noOfActvLoansOnDisbdt) {
		this.noOfActvLoansOnDisbdt = noOfActvLoansOnDisbdt;
	}

	public BigDecimal getClAvgCurBalOverallL1M() {
		return clAvgCurBalOverallL1M;
	}

	public void setClAvgCurBalOverallL1M(BigDecimal clAvgCurBalOverallL1M) {
		this.clAvgCurBalOverallL1M = clAvgCurBalOverallL1M;
	}

	public BigDecimal getClCurBalOvralL1M() {
		return clCurBalOvralL1M;
	}

	public void setClCurBalOvralL1M(BigDecimal clCurBalOvralL1M) {
		this.clCurBalOvralL1M = clCurBalOvralL1M;
	}

	public BigDecimal getNoOfClosedLoansL12M() {
		return noOfClosedLoansL12M;
	}

	public void setNoOfClosedLoansL12M(BigDecimal noOfClosedLoansL12M) {
		this.noOfClosedLoansL12M = noOfClosedLoansL12M;
	}

	public BigDecimal getMaxDelinQuencyOverallL12M() {
		return maxDelinQuencyOverallL12M;
	}

	public void setMaxDelinQuencyOverallL12M(BigDecimal maxDelinQuencyOverallL12M) {
		this.maxDelinQuencyOverallL12M = maxDelinQuencyOverallL12M;
	}

	public BigDecimal getClPctTotOvdBalL1M() {
		return clPctTotOvdBalL1M;
	}

	public void setClPctTotOvdBalL1M(BigDecimal clPctTotOvdBalL1M) {
		this.clPctTotOvdBalL1M = clPctTotOvdBalL1M;
	}

	public BigDecimal getMaxPctPaidSecL1M() {
		return maxPctPaidSecL1M;
	}

	public void setMaxPctPaidSecL1M(BigDecimal maxPctPaidSecL1M) {
		this.maxPctPaidSecL1M = maxPctPaidSecL1M;
	}

	public BigDecimal getAgeLattestprev() {
		return ageLattestprev;
	}

	public void setAgeLattestprev(BigDecimal ageLattestprev) {
		this.ageLattestprev = ageLattestprev;
	}

	public BigDecimal getMonSinceL30pOvralL6M() {
		return monSinceL30pOvralL6M;
	}

	public void setMonSinceL30pOvralL6M(BigDecimal monSinceL30pOvralL6M) {
		this.monSinceL30pOvralL6M = monSinceL30pOvralL6M;
	}

	public BigDecimal getClWorstCurBalUnSecL1M() {
		return clWorstCurBalUnSecL1M;
	}

	public void setClWorstCurBalUnSecL1M(BigDecimal clWorstCurBalUnSecL1M) {
		this.clWorstCurBalUnSecL1M = clWorstCurBalUnSecL1M;
	}

	public BigDecimal getNoOfClosedLoansL6M() {
		return noOfClosedLoansL6M;
	}

	public void setNoOfClosedLoansL6M(BigDecimal noOfClosedLoansL6M) {
		this.noOfClosedLoansL6M = noOfClosedLoansL6M;
	}

	public BigDecimal getNoOf30DpdOverallL12M() {
		return noOf30DpdOverallL12M;
	}

	public void setNoOf30DpdOverallL12M(BigDecimal noOf30DpdOverallL12M) {
		this.noOf30DpdOverallL12M = noOf30DpdOverallL12M;
	}

	public BigDecimal getMaxUnSecDisbAmtL1M() {
		return maxUnSecDisbAmtL1M;
	}

	public void setMaxUnSecDisbAmtL1M(BigDecimal maxUnSecDisbAmtL1M) {
		this.maxUnSecDisbAmtL1M = maxUnSecDisbAmtL1M;
	}

	public BigDecimal getMaxDelinQuencyOverallL6M() {
		return maxDelinQuencyOverallL6M;
	}

	public void setMaxDelinQuencyOverallL6M(BigDecimal maxDelinQuencyOverallL6M) {
		this.maxDelinQuencyOverallL6M = maxDelinQuencyOverallL6M;
	}

	public BigDecimal getNoOf30DpdOverallL6M() {
		return noOf30DpdOverallL6M;
	}

	public void setNoOf30DpdOverallL6M(BigDecimal noOf30DpdOverallL6M) {
		this.noOf30DpdOverallL6M = noOf30DpdOverallL6M;
	}

	public BigDecimal getClWorstCurBalOverallL1M() {
		return clWorstCurBalOverallL1M;
	}

	public void setClWorstCurBalOverallL1M(BigDecimal clWorstCurBalOverallL1M) {
		this.clWorstCurBalOverallL1M = clWorstCurBalOverallL1M;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getAssetClassification() {
		return assetClassification;
	}

	public void setAssetClassification(BigDecimal assetClassification) {
		this.assetClassification = assetClassification;
	}

	public int getTotalEnquiries() {
		return totalEnquiries;
	}

	public void setTotalEnquiries(int totalEnquiries) {
		this.totalEnquiries = totalEnquiries;
	}

	public BigDecimal getLiveTradelines() {
		return liveTradelines;
	}

	public void setLiveTradelines(BigDecimal liveTradelines) {
		this.liveTradelines = liveTradelines;
	}

	public String getRestructuredFlag() {
		return restructuredFlag;
	}

	public void setRestructuredFlag(String restructuredFlag) {
		this.restructuredFlag = restructuredFlag;
	}

	public String getSfFlag() {
		return sfFlag;
	}

	public void setSfFlag(String sfFlag) {
		this.sfFlag = sfFlag;
	}

	public String getWdFlag() {
		return wdFlag;
	}

	public void setWdFlag(String wdFlag) {
		this.wdFlag = wdFlag;
	}

	public String getWoFlag() {
		return woFlag;
	}

	public void setWoFlag(String woFlag) {
		this.woFlag = woFlag;
	}

	public String getSettledFlag() {
		return settledFlag;
	}

	public void setSettledFlag(String settledFlag) {
		this.settledFlag = settledFlag;
	}

}
