package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class TaxAmountSplit implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal cGST = BigDecimal.ZERO;
	private BigDecimal sGST = BigDecimal.ZERO;
	private BigDecimal uGST = BigDecimal.ZERO;
	private BigDecimal iGST = BigDecimal.ZERO;
	private BigDecimal tGST = BigDecimal.ZERO;
	private BigDecimal netAmount = BigDecimal.ZERO;
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private BigDecimal totRecv = BigDecimal.ZERO;
	private BigDecimal inProcAmount = BigDecimal.ZERO;
	private String taxType = null;

	public TaxAmountSplit() {
		super();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getcGST() {
		return cGST;
	}

	public void setcGST(BigDecimal cGST) {
		this.cGST = cGST;
	}

	public BigDecimal getsGST() {
		return sGST;
	}

	public void setsGST(BigDecimal sGST) {
		this.sGST = sGST;
	}

	public BigDecimal getuGST() {
		return uGST;
	}

	public void setuGST(BigDecimal uGST) {
		this.uGST = uGST;
	}

	public BigDecimal getiGST() {
		return iGST;
	}

	public void setiGST(BigDecimal iGST) {
		this.iGST = iGST;
	}

	public BigDecimal gettGST() {
		return tGST;
	}

	public void settGST(BigDecimal tGST) {
		this.tGST = tGST;
	}

	public BigDecimal getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(BigDecimal netAmount) {
		this.netAmount = netAmount;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public BigDecimal getInProcAmount() {
		return inProcAmount;
	}

	public void setInProcAmount(BigDecimal inProcAmount) {
		this.inProcAmount = inProcAmount;
	}

	public BigDecimal getTotRecv() {
		return totRecv;
	}

	public void setTotRecv(BigDecimal totRecv) {
		this.totRecv = totRecv;
	}

}
