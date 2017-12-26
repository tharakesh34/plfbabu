package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "cif", "applicationId", "appDate", "noOfBusLoansOpenedLast6m", "pcttotovdhicrl1m",
		"maxPerAmtPaidSecLn1m", "monsincel30povrall12m", "minPerPaidUnsecLn1m", "clsoesLnDisAmt", "maxUnSecDisbamt12m",
		"totalEnquiries", "liveTradelines", "restructuredFlag", "sfFlag", "wdFlag", "woFlag", "settledFlag",
		"isApplicant90PlusDpdInLastSixMonths", "loansTakenPostFinYrInBureau", "maximumTenure", "prodIndexal",
		"prodIndexblod", "prodIndexcc", "prodIndexclptl", "prodIndexHL", "applicant", "coApplicants" })
@XmlRootElement(name = "BRE")
@XmlAccessorType(XmlAccessType.FIELD)
public class BreData {

	private long				cif;

	private String				applicationId;

	@XmlElement(name = "applicationDate")
	private String				appDate;

	@XmlElement(name = "noofbusloansopenedl6m")
	private int					noOfBusLoansOpenedLast6m;

	@XmlElement(name = "pcttotovdhicrl1m")
	private BigDecimal			pcttotovdhicrl1m		= BigDecimal.ZERO;

	@XmlElement(name = "almaxpctpaidsecl1m")
	private BigDecimal			maxPerAmtPaidSecLn1m	= BigDecimal.ZERO;

	@XmlElement(name = "monsincel30povrall12m")
	private int					monsincel30povrall12m;

	@XmlElement(name = "Minpctpaidunsecl1m")
	private BigDecimal			minPerPaidUnsecLn1m		= BigDecimal.ZERO;

	@XmlElement(name = "Clstotdisbamt")
	private BigDecimal			clsoesLnDisAmt			= BigDecimal.ZERO;

	@XmlElement(name = "maxunsecdisbamtl12m")
	private BigDecimal			maxUnSecDisbamt12m		= BigDecimal.ZERO;

	@XmlElement(name = "totalenquiries")
	private int					totalEnquiries;

	@XmlElement(name = "livetradelines")
	private String				liveTradelines;

	@XmlElement(name = "restructuredflag")
	private String				restructuredFlag;

	@XmlElement(name = "sfflag")
	private String				sfFlag;

	@XmlElement(name = "wdflag")
	private String				wdFlag;

	@XmlElement(name = "woflag")
	private String				woFlag;

	@XmlElement(name = "settledflag")
	private String				settledFlag;

	@XmlElement(name = "IsApplicant90PlusDpdInLastSixMonths")
	private String				isApplicant90PlusDpdInLastSixMonths;

	@XmlElement(name = "loanstakenpostfinancialyearinbureau")
	private String				loansTakenPostFinYrInBureau;

	@XmlElement(name = "maximumtenure")
	private String				maximumTenure;

	@XmlElement(name = "prodindexal")
	private String				prodIndexal;

	@XmlElement(name = "prodindexblod")
	private String				prodIndexblod;

	@XmlElement(name = "prodindexcc")
	private String				prodIndexcc;

	@XmlElement(name = "prodindexclptl")
	private String				prodIndexclptl;

	@XmlElement(name = "prodindexhl")
	private String				prodIndexHL;

	@XmlElement(name = "Applicant")
	private Applicant			applicant;

	@XmlElement(name = "CoApplicants")
	private List<CoApplicant>	coApplicants;

	public long getCif() {
		return cif;
	}

	public void setCif(long cif) {
		this.cif = cif;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public int getNoOfBusLoansOpenedLast6m() {
		return noOfBusLoansOpenedLast6m;
	}

	public void setNoOfBusLoansOpenedLast6m(int noOfBusLoansOpenedLast6m) {
		this.noOfBusLoansOpenedLast6m = noOfBusLoansOpenedLast6m;
	}

	public BigDecimal getPcttotovdhicrl1m() {
		return pcttotovdhicrl1m;
	}

	public void setPcttotovdhicrl1m(BigDecimal pcttotovdhicrl1m) {
		this.pcttotovdhicrl1m = pcttotovdhicrl1m;
	}

	public BigDecimal getMaxPerAmtPaidSecLn1m() {
		return maxPerAmtPaidSecLn1m;
	}

	public void setMaxPerAmtPaidSecLn1m(BigDecimal maxPerAmtPaidSecLn1m) {
		this.maxPerAmtPaidSecLn1m = maxPerAmtPaidSecLn1m;
	}

	public int getMonsincel30povrall12m() {
		return monsincel30povrall12m;
	}

	public void setMonsincel30povrall12m(int monsincel30povrall12m) {
		this.monsincel30povrall12m = monsincel30povrall12m;
	}

	public BigDecimal getMinPerPaidUnsecLn1m() {
		return minPerPaidUnsecLn1m;
	}

	public void setMinPerPaidUnsecLn1m(BigDecimal minPerPaidUnsecLn1m) {
		this.minPerPaidUnsecLn1m = minPerPaidUnsecLn1m;
	}

	public BigDecimal getClsoesLnDisAmt() {
		return clsoesLnDisAmt;
	}

	public void setClsoesLnDisAmt(BigDecimal clsoesLnDisAmt) {
		this.clsoesLnDisAmt = clsoesLnDisAmt;
	}

	public BigDecimal getMaxUnSecDisbamt12m() {
		return maxUnSecDisbamt12m;
	}

	public void setMaxUnSecDisbamt12m(BigDecimal maxUnSecDisbamt12m) {
		this.maxUnSecDisbamt12m = maxUnSecDisbamt12m;
	}

	public int getTotalEnquiries() {
		return totalEnquiries;
	}

	public void setTotalEnquiries(int totalEnquiries) {
		this.totalEnquiries = totalEnquiries;
	}

	public String getLiveTradelines() {
		return liveTradelines;
	}

	public void setLiveTradelines(String liveTradelines) {
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

	public String getIsApplicant90PlusDpdInLastSixMonths() {
		return isApplicant90PlusDpdInLastSixMonths;
	}

	public void setIsApplicant90PlusDpdInLastSixMonths(String isApplicant90PlusDpdInLastSixMonths) {
		this.isApplicant90PlusDpdInLastSixMonths = isApplicant90PlusDpdInLastSixMonths;
	}

	public String getLoansTakenPostFinYrInBureau() {
		return loansTakenPostFinYrInBureau;
	}

	public void setLoansTakenPostFinYrInBureau(String loansTakenPostFinYrInBureau) {
		this.loansTakenPostFinYrInBureau = loansTakenPostFinYrInBureau;
	}

	public String getMaximumTenure() {
		return maximumTenure;
	}

	public void setMaximumTenure(String maximumTenure) {
		this.maximumTenure = maximumTenure;
	}

	public String getProdIndexal() {
		return prodIndexal;
	}

	public void setProdIndexal(String prodIndexal) {
		this.prodIndexal = prodIndexal;
	}

	public String getProdIndexblod() {
		return prodIndexblod;
	}

	public void setProdIndexblod(String prodIndexblod) {
		this.prodIndexblod = prodIndexblod;
	}

	public String getProdIndexcc() {
		return prodIndexcc;
	}

	public void setProdIndexcc(String prodIndexcc) {
		this.prodIndexcc = prodIndexcc;
	}

	public String getProdIndexclptl() {
		return prodIndexclptl;
	}

	public void setProdIndexclptl(String prodIndexclptl) {
		this.prodIndexclptl = prodIndexclptl;
	}

	public String getProdIndexHL() {
		return prodIndexHL;
	}

	public void setProdIndexHL(String prodIndexHL) {
		this.prodIndexHL = prodIndexHL;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public List<CoApplicant> getCoApplicants() {
		return coApplicants;
	}

	public void setCoApplicants(List<CoApplicant> coApplicants) {
		this.coApplicants = coApplicants;
	}

}
