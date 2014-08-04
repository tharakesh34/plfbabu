package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class RPDetail {

	private BigDecimal rPProfitCalc= BigDecimal.ZERO;
	private BigDecimal rPProfitSchd= BigDecimal.ZERO;
	private BigDecimal rPSchdPftPaid= BigDecimal.ZERO;
	
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
