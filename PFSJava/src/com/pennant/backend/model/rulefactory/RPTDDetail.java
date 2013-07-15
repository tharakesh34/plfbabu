package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class RPTDDetail {

	private BigDecimal rPPProfitCalc= new BigDecimal(0);
	private BigDecimal rPPProfitSchd= new BigDecimal(0);
	private BigDecimal rPPSchdPftPaid= new BigDecimal(0);
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
