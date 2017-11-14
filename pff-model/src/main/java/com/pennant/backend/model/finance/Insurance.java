package com.pennant.backend.model.finance;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "insType", "insDesc", "insReq", "provider", "providerDesc", "providerRate", 
						"payType", "reference", "waiverReason", "calcFrq", "amount", "formula" })
@XmlAccessorType(XmlAccessType.NONE)
public class Insurance extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4454984955540575190L;

	public Insurance() {
		super();
	}

	@XmlElement
	private String insType;
	@XmlElement
	private String insDesc;
	@XmlElement
	private boolean insReq;
	@XmlElement
	private String provider;
	@XmlElement
	private String providerDesc;
	@XmlElement
	private String payType;
	@XmlElement
	private BigDecimal providerRate = BigDecimal.ZERO;
	@XmlElement
	private String reference;
	@XmlElement
	private String waiverReason;
	@XmlElement
	private String calcFrq;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String formula;

	public boolean isInsReq() {
		return insReq;
	}

	public void setInsReq(boolean insReq) {
		this.insReq = insReq;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public BigDecimal getProviderRate() {
		return providerRate;
	}

	public void setProviderRate(BigDecimal providerRate) {
		this.providerRate = providerRate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getWaiverReason() {
		return waiverReason;
	}

	public void setWaiverReason(String waiverReason) {
		this.waiverReason = waiverReason;
	}

	public String getCalcFrq() {
		return calcFrq;
	}

	public void setCalcFrq(String calcFrq) {
		this.calcFrq = calcFrq;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getInsType() {
		return insType;
	}

	public void setInsType(String insType) {
		this.insType = insType;
	}

	public String getInsDesc() {
		return insDesc;
	}

	public void setInsDesc(String insDesc) {
		this.insDesc = insDesc;
	}

	public String getProviderDesc() {
		return providerDesc;
	}

	public void setProviderDesc(String providerDesc) {
		this.providerDesc = providerDesc;
	}
}
