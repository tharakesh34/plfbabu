package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class TEDetail {

	private int tENoOfDays = 0;
	private BigDecimal tEProfitCalc= new BigDecimal(0);
	private BigDecimal tEProfitSchd= new BigDecimal(0);
	private BigDecimal tEPrincipalSchd= new BigDecimal(0);
	private BigDecimal tERepayAmount= new BigDecimal(0);
	private BigDecimal tEDisbAmount= new BigDecimal(0);
	private BigDecimal tEDownPaymentAmount= new BigDecimal(0);
	private BigDecimal tECpzAmount= new BigDecimal(0);
	private BigDecimal tEDefRepaySchd= new BigDecimal(0);
	private BigDecimal tEDefProfitSchd= new BigDecimal(0);
	private BigDecimal tEDefPrincipalSchd= new BigDecimal(0);
	private BigDecimal tESchdPftPaid= new BigDecimal(0);
	private BigDecimal tESchdPriPaid= new BigDecimal(0);
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
}
