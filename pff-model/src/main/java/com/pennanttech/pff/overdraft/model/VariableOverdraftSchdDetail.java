package com.pennanttech.pff.overdraft.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VariableOverdraftSchdDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id = Long.MIN_VALUE;
	private Long headerId;
	private Date schDate;
	private BigDecimal droplineAmount = BigDecimal.ZERO;
	private String status;
	private String reason;
	private String strSchDate;
	private String strDroplineAmount;

	public VariableOverdraftSchdDetail() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Long headerId) {
		this.headerId = headerId;
	}

	public Date getSchDate() {
		return schDate;
	}

	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public BigDecimal getDroplineAmount() {
		return droplineAmount;
	}

	public void setDroplineAmount(BigDecimal droplineAmount) {
		this.droplineAmount = droplineAmount;
	}

	public String getStrSchDate() {
		return strSchDate;
	}

	public void setStrSchDate(String strSchDate) {
		this.strSchDate = strSchDate;
	}

	public String getStrDroplineAmount() {
		return strDroplineAmount;
	}

	public void setStrDroplineAmount(String strDroplineAmount) {
		this.strDroplineAmount = strDroplineAmount;
	}

}
