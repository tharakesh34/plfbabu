/*package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.util.Date;

public class AmountCodeDetail {

	private String finReference;
	private Date lastRepayPftDate;
	private Date nextRepayPftDate;
	private Date lastRepayRvwDate;
	private Date nextRepayRvwDate;
	private BigDecimal totalProfit = BigDecimal.ZERO;
	private BigDecimal actualTotalProfit = BigDecimal.ZERO;
	
	private int cPNoOfDays = 0;
	private BigDecimal cPProfitCalc = BigDecimal.ZERO;
	private BigDecimal cPProfitSchd = BigDecimal.ZERO;
	private BigDecimal cPPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal cPRepayAmount = BigDecimal.ZERO;
	private BigDecimal cPDisbAmount = BigDecimal.ZERO;
	private BigDecimal cPDownPaymentAmount = BigDecimal.ZERO;
	private BigDecimal cPCpzAmount = BigDecimal.ZERO;
	private BigDecimal cPDefRepaySchd = BigDecimal.ZERO;
	private BigDecimal cPDefProfitSchd = BigDecimal.ZERO;
	private BigDecimal cPDefPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal cPSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal cPSchdPriPaid = BigDecimal.ZERO;
	
	private int tPPNoOfDays =0;
	private BigDecimal tPPProfitCalc = BigDecimal.ZERO;
	private BigDecimal tPPProfitSchd = BigDecimal.ZERO;
	private BigDecimal tPPPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal tPPRepayAmount = BigDecimal.ZERO;
	private BigDecimal tPPDisbAmount = BigDecimal.ZERO;
	private BigDecimal tPPDownPaymentAmount = BigDecimal.ZERO;
	private BigDecimal tPPCpzAmount = BigDecimal.ZERO;
	private BigDecimal tPPDefRepaySchd = BigDecimal.ZERO;
	private BigDecimal tPPDefProfitSchd = BigDecimal.ZERO;
	private BigDecimal tPPDefPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal tPPSchdPftPaid = BigDecimal.ZERO;
	private BigDecimal tPPSchdPriPaid = BigDecimal.ZERO;
	
	private int tENoOfDays = 0;
	private BigDecimal tEProfitCalc = BigDecimal.ZERO;
	private BigDecimal tEProfitSchd = BigDecimal.ZERO;
	private BigDecimal tEPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal tERepayAmount = BigDecimal.ZERO;
	private BigDecimal tEDisbAmount = BigDecimal.ZERO;
	private BigDecimal tEDownPaymentAmount = BigDecimal.ZERO;
	private BigDecimal tECpzAmount = BigDecimal.ZERO;
	private BigDecimal tEDefRepaySchd = BigDecimal.ZERO;
	private BigDecimal tEDefProfitSchd = BigDecimal.ZERO;
	private BigDecimal tEDefPrincipalSchd = BigDecimal.ZERO;
	private BigDecimal tESchdPftPaid = BigDecimal.ZERO;
	private BigDecimal tESchdPriPaid = BigDecimal.ZERO;
	
	private BigDecimal rPProfitCalc = BigDecimal.ZERO;
	private BigDecimal rPProfitSchd = BigDecimal.ZERO;
	private BigDecimal rPSchdPftPaid = BigDecimal.ZERO;
	
	private BigDecimal rPPProfitCalc = BigDecimal.ZERO;
	private BigDecimal rPPProfitSchd = BigDecimal.ZERO;
	private BigDecimal rPPSchdPftPaid = BigDecimal.ZERO;
	
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