package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class CPDetail {

	private int cPNoOfDays=0;
	private BigDecimal cPProfitCalc= BigDecimal.ZERO;
	private BigDecimal cPProfitSchd= BigDecimal.ZERO;
	private BigDecimal cPPrincipalSchd= BigDecimal.ZERO;
	private BigDecimal cPRepayAmount= BigDecimal.ZERO;
	private BigDecimal cPDisbAmount= BigDecimal.ZERO;
	private BigDecimal cPDownPaymentAmount= BigDecimal.ZERO;
	private BigDecimal cPCpzAmount= BigDecimal.ZERO;
	private BigDecimal cPDefRepaySchd= BigDecimal.ZERO;
	private BigDecimal cPDefProfitSchd= BigDecimal.ZERO;
	private BigDecimal cPDefPrincipalSchd= BigDecimal.ZERO;
	private BigDecimal cPSchdPftPaid= BigDecimal.ZERO;
	private BigDecimal cPSchdPriPaid= BigDecimal.ZERO;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
		
}
