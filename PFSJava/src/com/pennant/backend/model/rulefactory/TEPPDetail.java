package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class TEPPDetail {

	private int tPPNoOfDays =0;
	private BigDecimal tPPProfitCalc= new BigDecimal(0);
	private BigDecimal tPPProfitSchd= new BigDecimal(0);
	private BigDecimal tPPPrincipalSchd= new BigDecimal(0);
	private BigDecimal tPPRepayAmount= new BigDecimal(0);
	private BigDecimal tPPDisbAmount= new BigDecimal(0);
	private BigDecimal tPPDownPaymentAmount= new BigDecimal(0);
	private BigDecimal tPPCpzAmount= new BigDecimal(0);
	private BigDecimal tPPDefRepaySchd= new BigDecimal(0);
	private BigDecimal tPPDefProfitSchd= new BigDecimal(0);
	private BigDecimal tPPDefPrincipalSchd= new BigDecimal(0);
	private BigDecimal tPPSchdPftPaid= new BigDecimal(0);
	private BigDecimal tPPSchdPriPaid= new BigDecimal(0);
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
}
