package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;

public class RPTDDetail {

	private BigDecimal rPPProfitCalc= BigDecimal.ZERO;
	private BigDecimal rPPProfitSchd= BigDecimal.ZERO;
	private BigDecimal rPPSchdPftPaid= BigDecimal.ZERO;
	
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
