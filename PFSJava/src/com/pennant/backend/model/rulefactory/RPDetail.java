package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class RPDetail {

	private BigDecimal rPProfitCalc= new BigDecimal(0);
	private BigDecimal rPProfitSchd= new BigDecimal(0);
	private BigDecimal rPSchdPftPaid= new BigDecimal(0);
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
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
	
}
