package com.pennant.backend.model.payment;

import java.math.BigDecimal;

public class PaymentTaxDetail {
	
	private long		paymentDetailID		= 0;
	private long		paymentID			= 0;
	private String  	taxComponent;
	
	private BigDecimal 	dueGST = BigDecimal.ZERO;
	private BigDecimal 	dueCGST = BigDecimal.ZERO;
	private BigDecimal 	dueSGST = BigDecimal.ZERO;
	private BigDecimal 	dueUGST = BigDecimal.ZERO;
	private BigDecimal 	dueIGST = BigDecimal.ZERO;
	
	private BigDecimal 	paidCGST = BigDecimal.ZERO;
	private BigDecimal 	paidSGST = BigDecimal.ZERO;
	private BigDecimal 	paidUGST = BigDecimal.ZERO;
	private BigDecimal 	paidIGST = BigDecimal.ZERO;
	private BigDecimal 	totalGST = BigDecimal.ZERO;
	

	public long getPaymentDetailID() {
		return paymentDetailID;
	}

	public void setPaymentDetailID(long paymentDetailID) {
		this.paymentDetailID = paymentDetailID;
	}

	public long getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(long paymentID) {
		this.paymentID = paymentID;
	}

	public String getTaxComponent() {
		return taxComponent;
	}
	
	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}
	
	public BigDecimal getPaidCGST() {
		return paidCGST;
	}
	
	public void setPaidCGST(BigDecimal paidCGST) {
		this.paidCGST = paidCGST;
	}
	
	public BigDecimal getPaidSGST() {
		return paidSGST;
	}
	
	public void setPaidSGST(BigDecimal paidSGST) {
		this.paidSGST = paidSGST;
	}
	
	public BigDecimal getPaidUGST() {
		return paidUGST;
	}
	
	public void setPaidUGST(BigDecimal paidUGST) {
		this.paidUGST = paidUGST;
	}
	
	public BigDecimal getPaidIGST() {
		return paidIGST;
	}
	
	public void setPaidIGST(BigDecimal paidIGST) {
		this.paidIGST = paidIGST;
	}
	
	public BigDecimal getTotalGST() {
		return totalGST;
	}
	
	public void setTotalGST(BigDecimal totalGST) {
		this.totalGST = totalGST;
	}

	public BigDecimal getDueGST() {
		return dueGST;
	}

	public void setDueGST(BigDecimal dueGST) {
		this.dueGST = dueGST;
	}

	public BigDecimal getDueCGST() {
		return dueCGST;
	}

	public void setDueCGST(BigDecimal dueCGST) {
		this.dueCGST = dueCGST;
	}

	public BigDecimal getDueSGST() {
		return dueSGST;
	}

	public void setDueSGST(BigDecimal dueSGST) {
		this.dueSGST = dueSGST;
	}

	public BigDecimal getDueUGST() {
		return dueUGST;
	}

	public void setDueUGST(BigDecimal dueUGST) {
		this.dueUGST = dueUGST;
	}

	public BigDecimal getDueIGST() {
		return dueIGST;
	}

	public void setDueIGST(BigDecimal dueIGST) {
		this.dueIGST = dueIGST;
	}

}
