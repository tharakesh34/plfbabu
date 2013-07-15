/*package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class AmountCodeDetail {

	private String finReference;
	private Date lastRepayPftDate;
	private Date nextRepayPftDate;
	private Date lastRepayRvwDate;
	private Date nextRepayRvwDate;
	private BigDecimal totalProfit = new BigDecimal(0);
	private BigDecimal actualTotalProfit = new BigDecimal(0);
	
	private int cPNoOfDays = 0;
	private BigDecimal cPProfitCalc = new BigDecimal(0);
	private BigDecimal cPProfitSchd = new BigDecimal(0);
	private BigDecimal cPPrincipalSchd = new BigDecimal(0);
	private BigDecimal cPRepayAmount = new BigDecimal(0);
	private BigDecimal cPDisbAmount = new BigDecimal(0);
	private BigDecimal cPDownPaymentAmount = new BigDecimal(0);
	private BigDecimal cPCpzAmount = new BigDecimal(0);
	private BigDecimal cPDefRepaySchd = new BigDecimal(0);
	private BigDecimal cPDefProfitSchd = new BigDecimal(0);
	private BigDecimal cPDefPrincipalSchd = new BigDecimal(0);
	private BigDecimal cPSchdPftPaid = new BigDecimal(0);
	private BigDecimal cPSchdPriPaid = new BigDecimal(0);
	
	private int tPPNoOfDays =0;
	private BigDecimal tPPProfitCalc = new BigDecimal(0);
	private BigDecimal tPPProfitSchd = new BigDecimal(0);
	private BigDecimal tPPPrincipalSchd = new BigDecimal(0);
	private BigDecimal tPPRepayAmount = new BigDecimal(0);
	private BigDecimal tPPDisbAmount = new BigDecimal(0);
	private BigDecimal tPPDownPaymentAmount = new BigDecimal(0);
	private BigDecimal tPPCpzAmount = new BigDecimal(0);
	private BigDecimal tPPDefRepaySchd = new BigDecimal(0);
	private BigDecimal tPPDefProfitSchd = new BigDecimal(0);
	private BigDecimal tPPDefPrincipalSchd = new BigDecimal(0);
	private BigDecimal tPPSchdPftPaid = new BigDecimal(0);
	private BigDecimal tPPSchdPriPaid = new BigDecimal(0);
	
	private int tENoOfDays = 0;
	private BigDecimal tEProfitCalc = new BigDecimal(0);
	private BigDecimal tEProfitSchd = new BigDecimal(0);
	private BigDecimal tEPrincipalSchd = new BigDecimal(0);
	private BigDecimal tERepayAmount = new BigDecimal(0);
	private BigDecimal tEDisbAmount = new BigDecimal(0);
	private BigDecimal tEDownPaymentAmount = new BigDecimal(0);
	private BigDecimal tECpzAmount = new BigDecimal(0);
	private BigDecimal tEDefRepaySchd = new BigDecimal(0);
	private BigDecimal tEDefProfitSchd = new BigDecimal(0);
	private BigDecimal tEDefPrincipalSchd = new BigDecimal(0);
	private BigDecimal tESchdPftPaid = new BigDecimal(0);
	private BigDecimal tESchdPriPaid = new BigDecimal(0);
	
	private BigDecimal rPProfitCalc = new BigDecimal(0);
	private BigDecimal rPProfitSchd = new BigDecimal(0);
	private BigDecimal rPSchdPftPaid = new BigDecimal(0);
	
	private BigDecimal rPPProfitCalc = new BigDecimal(0);
	private BigDecimal rPPProfitSchd = new BigDecimal(0);
	private BigDecimal rPPSchdPftPaid = new BigDecimal(0);
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	
	public Date getLastRepayPftDate() {
		return lastRepayPftDate;
	}
	public void setLastRepayPftDate(Date lastRepayPftDate) {
		this.lastRepayPftDate = lastRepayPftDate;
	}
	
	public Date getNextRepayPftDate() {
		return nextRepayPftDate;
	}
	public void setNextRepayPftDate(Date nextRepayPftDate) {
		this.nextRepayPftDate = nextRepayPftDate;
	}
	
	public Date getLastRepayRvwDate() {
		return lastRepayRvwDate;
	}
	public void setLastRepayRvwDate(Date lastRepayRvwDate) {
		this.lastRepayRvwDate = lastRepayRvwDate;
	}
	
	public Date getNextRepayRvwDate() {
		return nextRepayRvwDate;
	}
	public void setNextRepayRvwDate(Date nextRepayRvwDate) {
		this.nextRepayRvwDate = nextRepayRvwDate;
	}
	
	public BigDecimal getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}
	
	public BigDecimal getActualTotalProfit() {
		return actualTotalProfit;
	}
	public void setActualTotalProfit(BigDecimal actualTotalProfit) {
		this.actualTotalProfit = actualTotalProfit;
	}
	
	public int getCPNoOfDays() {
		return cPNoOfDays;
	}
	public void setCPNoOfDays(int cPNoOfDays) {
		this.cPNoOfDays = cPNoOfDays;
	}
	
	public BigDecimal getCPProfitCalc() {
		return cPProfitCalc;
	}
	public void setCPProfitCalc(BigDecimal cPProfitCalc) {
		this.cPProfitCalc = cPProfitCalc;
	}
	
	public BigDecimal getCPProfitSchd() {
		return cPProfitSchd;
	}
	public void setCPProfitSchd(BigDecimal cPProfitSchd) {
		this.cPProfitSchd = cPProfitSchd;
	}
	
	public BigDecimal getCPPrincipalSchd() {
		return cPPrincipalSchd;
	}
	public void setCPPrincipalSchd(BigDecimal cPPrincipalSchd) {
		this.cPPrincipalSchd = cPPrincipalSchd;
	}
	
	public BigDecimal getCPRepayAmount() {
		return cPRepayAmount;
	}
	public void setCPRepayAmount(BigDecimal cPRepayAmount) {
		this.cPRepayAmount = cPRepayAmount;
	}
	
	public BigDecimal getCPDisbAmount() {
		return cPDisbAmount;
	}
	public void setCPDisbAmount(BigDecimal cPDisbAmount) {
		this.cPDisbAmount = cPDisbAmount;
	}
	
	public BigDecimal getCPDownPaymentAmount() {
		return cPDownPaymentAmount;
	}
	public void setCPDownPaymentAmount(BigDecimal cPDownPaymentAmount) {
		this.cPDownPaymentAmount = cPDownPaymentAmount;
	}
	
	public BigDecimal getCPCpzAmount() {
		return cPCpzAmount;
	}
	public void setCPCpzAmount(BigDecimal cPCpzAmount) {
		this.cPCpzAmount = cPCpzAmount;
	}
	
	public BigDecimal getCPDefRepaySchd() {
		return cPDefRepaySchd;
	}
	public void setCPDefRepaySchd(BigDecimal cPDefRepaySchd) {
		this.cPDefRepaySchd = cPDefRepaySchd;
	}
	
	public BigDecimal getCPDefProfitSchd() {
		return cPDefProfitSchd;
	}
	public void setCPDefProfitSchd(BigDecimal cPDefProfitSchd) {
		this.cPDefProfitSchd = cPDefProfitSchd;
	}
	
	public BigDecimal getCPDefPrincipalSchd() {
		return cPDefPrincipalSchd;
	}
	public void setCPDefPrincipalSchd(BigDecimal cPDefPrincipalSchd) {
		this.cPDefPrincipalSchd = cPDefPrincipalSchd;
	}
	
	public BigDecimal getCPSchdPftPaid() {
		return cPSchdPftPaid;
	}
	public void setCPSchdPftPaid(BigDecimal cPSchdPftPaid) {
		this.cPSchdPftPaid = cPSchdPftPaid;
	}
	
	public BigDecimal getCPSchdPriPaid() {
		return cPSchdPriPaid;
	}
	public void setCPSchdPriPaid(BigDecimal cPSchdPriPaid) {
		this.cPSchdPriPaid = cPSchdPriPaid;
	}
	
	public int getTPPNoOfDays() {
		return tPPNoOfDays;
	}
	public void setTPPNoOfDays(int tPPNoOfDays) {
		this.tPPNoOfDays = tPPNoOfDays;
	}
	
	public BigDecimal getTPPProfitCalc() {
		return tPPProfitCalc;
	}
	public void setTPPProfitCalc(BigDecimal tPPProfitCalc) {
		this.tPPProfitCalc = tPPProfitCalc;
	}
	
	public BigDecimal getTPPProfitSchd() {
		return tPPProfitSchd;
	}
	public void setTPPProfitSchd(BigDecimal tPPProfitSchd) {
		this.tPPProfitSchd = tPPProfitSchd;
	}
	
	public BigDecimal getTPPPrincipalSchd() {
		return tPPPrincipalSchd;
	}
	public void setTPPPrincipalSchd(BigDecimal tPPPrincipalSchd) {
		this.tPPPrincipalSchd = tPPPrincipalSchd;
	}
	
	public BigDecimal getTPPRepayAmount() {
		return tPPRepayAmount;
	}
	public void setTPPRepayAmount(BigDecimal tPPRepayAmount) {
		this.tPPRepayAmount = tPPRepayAmount;
	}
	
	public BigDecimal getTPPDisbAmount() {
		return tPPDisbAmount;
	}
	public void setTPPDisbAmount(BigDecimal tPPDisbAmount) {
		this.tPPDisbAmount = tPPDisbAmount;
	}
	
	public BigDecimal getTPPDownPaymentAmount() {
		return tPPDownPaymentAmount;
	}
	public void setTPPDownPaymentAmount(BigDecimal tPPDownPaymentAmount) {
		this.tPPDownPaymentAmount = tPPDownPaymentAmount;
	}
	
	public BigDecimal getTPPCpzAmount() {
		return tPPCpzAmount;
	}
	public void setTPPCpzAmount(BigDecimal tPPCpzAmount) {
		this.tPPCpzAmount = tPPCpzAmount;
	}
	
	public BigDecimal getTPPDefRepaySchd() {
		return tPPDefRepaySchd;
	}
	public void setTPPDefRepaySchd(BigDecimal tPPDefRepaySchd) {
		this.tPPDefRepaySchd = tPPDefRepaySchd;
	}
	
	public BigDecimal getTPPDefProfitSchd() {
		return tPPDefProfitSchd;
	}
	public void setTPPDefProfitSchd(BigDecimal tPPDefProfitSchd) {
		this.tPPDefProfitSchd = tPPDefProfitSchd;
	}
	
	public BigDecimal getTPPDefPrincipalSchd() {
		return tPPDefPrincipalSchd;
	}
	public void setTPPDefPrincipalSchd(BigDecimal tPPDefPrincipalSchd) {
		this.tPPDefPrincipalSchd = tPPDefPrincipalSchd;
	}
	
	public BigDecimal getTPPSchdPftPaid() {
		return tPPSchdPftPaid;
	}
	public void setTPPSchdPftPaid(BigDecimal tPPSchdPftPaid) {
		this.tPPSchdPftPaid = tPPSchdPftPaid;
	}
	
	public BigDecimal getTPPSchdPriPaid() {
		return tPPSchdPriPaid;
	}
	public void setTPPSchdPriPaid(BigDecimal tPPSchdPriPaid) {
		this.tPPSchdPriPaid = tPPSchdPriPaid;
	}
	
	public int getTENoOfDays() {
		return tENoOfDays;
	}
	public void setTENoOfDays(int tENoOfDays) {
		this.tENoOfDays = tENoOfDays;
	}
	
	public BigDecimal getTEProfitCalc() {
		return tEProfitCalc;
	}
	public void setTEProfitCalc(BigDecimal tEProfitCalc) {
		this.tEProfitCalc = tEProfitCalc;
	}
	
	public BigDecimal getTEProfitSchd() {
		return tEProfitSchd;
	}
	public void setTEProfitSchd(BigDecimal tEProfitSchd) {
		this.tEProfitSchd = tEProfitSchd;
	}
	
	public BigDecimal getTEPrincipalSchd() {
		return tEPrincipalSchd;
	}
	public void setTEPrincipalSchd(BigDecimal tEPrincipalSchd) {
		this.tEPrincipalSchd = tEPrincipalSchd;
	}
	
	public BigDecimal getTERepayAmount() {
		return tERepayAmount;
	}
	public void setTERepayAmount(BigDecimal tERepayAmount) {
		this.tERepayAmount = tERepayAmount;
	}
	
	public BigDecimal getTEDisbAmount() {
		return tEDisbAmount;
	}
	public void setTEDisbAmount(BigDecimal tEDisbAmount) {
		this.tEDisbAmount = tEDisbAmount;
	}
	
	public BigDecimal getTEDownPaymentAmount() {
		return tEDownPaymentAmount;
	}
	public void setTEDownPaymentAmount(BigDecimal tEDownPaymentAmount) {
		this.tEDownPaymentAmount = tEDownPaymentAmount;
	}
	
	public BigDecimal getTECpzAmount() {
		return tECpzAmount;
	}
	public void setTECpzAmount(BigDecimal tECpzAmount) {
		this.tECpzAmount = tECpzAmount;
	}
	
	public BigDecimal getTEDefRepaySchd() {
		return tEDefRepaySchd;
	}
	public void setTEDefRepaySchd(BigDecimal tEDefRepaySchd) {
		this.tEDefRepaySchd = tEDefRepaySchd;
	}
	
	public BigDecimal getTEDefProfitSchd() {
		return tEDefProfitSchd;
	}
	public void setTEDefProfitSchd(BigDecimal tEDefProfitSchd) {
		this.tEDefProfitSchd = tEDefProfitSchd;
	}
	
	public BigDecimal getTEDefPrincipalSchd() {
		return tEDefPrincipalSchd;
	}
	public void setTEDefPrincipalSchd(BigDecimal tEDefPrincipalSchd) {
		this.tEDefPrincipalSchd = tEDefPrincipalSchd;
	}
	
	public BigDecimal getTESchdPftPaid() {
		return tESchdPftPaid;
	}
	public void setTESchdPftPaid(BigDecimal tESchdPftPaid) {
		this.tESchdPftPaid = tESchdPftPaid;
	}
	
	public BigDecimal getTESchdPriPaid() {
		return tESchdPriPaid;
	}
	public void setTESchdPriPaid(BigDecimal tESchdPriPaid) {
		this.tESchdPriPaid = tESchdPriPaid;
	}
	
	public BigDecimal getRPProfitCalc() {
		return rPProfitCalc;
	}
	public void setRPProfitCalc(BigDecimal rPProfitCalc) {
		this.rPProfitCalc = rPProfitCalc;
	}
	
	public BigDecimal getRPProfitSchd() {
		return rPProfitSchd;
	}
	public void setRPProfitSchd(BigDecimal rPProfitSchd) {
		this.rPProfitSchd = rPProfitSchd;
	}
	
	public BigDecimal getRPSchdPftPaid() {
		return rPSchdPftPaid;
	}
	public void setRPSchdPftPaid(BigDecimal rPSchdPftPaid) {
		this.rPSchdPftPaid = rPSchdPftPaid;
	}
	
	public BigDecimal getRPPProfitCalc() {
		return rPPProfitCalc;
	}
	public void setRPPProfitCalc(BigDecimal rPPProfitCalc) {
		this.rPPProfitCalc = rPPProfitCalc;
	}
	
	public BigDecimal getRPPProfitSchd() {
		return rPPProfitSchd;
	}
	public void setRPPProfitSchd(BigDecimal rPPProfitSchd) {
		this.rPPProfitSchd = rPPProfitSchd;
	}
	
	public BigDecimal getRPPSchdPftPaid() {
		return rPPSchdPftPaid;
	}
	public void setRPPSchdPftPaid(BigDecimal rPPSchdPftPaid) {
		this.rPPSchdPftPaid = rPPSchdPftPaid;
	}

}
*/