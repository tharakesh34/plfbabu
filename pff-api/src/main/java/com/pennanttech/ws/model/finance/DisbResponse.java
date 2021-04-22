package com.pennanttech.ws.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "" })
@XmlAccessorType(XmlAccessType.NONE)
public class DisbResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private Long paymentId;
	@XmlElement
	private Long disbursement;
	@XmlElement
	private Date disbDate;
	@XmlElement
	private BigDecimal disbAmount;
	@XmlElement
	private String accountNo;
	@XmlElement
	private String status;
	@XmlElement
	private String type;

	public DisbResponse() {
		super();
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Long getDisbursement() {
		return disbursement;
	}

	public void setDisbursement(Long disbursement) {
		this.disbursement = disbursement;
	}

	public Date getDisbDate() {
		return disbDate;
	}

	public void setDisbDate(Date disbDate) {
		this.disbDate = disbDate;
	}

	public BigDecimal getDisbAmount() {
		return disbAmount;
	}

	public void setDisbAmount(BigDecimal disbAmount) {
		this.disbAmount = disbAmount;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
