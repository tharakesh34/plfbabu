package com.pennanttech.ws.model.collection;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "finReference", "receiptId" })
@XmlRootElement(name = "finance")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionAccountDetails {

	private long finID;
	@XmlElement
	private String finReference;
	@XmlElement
	private long receiptId;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	@XmlElement
	private String moduleType;
	@XmlElement
	private Long partnerBankId;

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public Long getPartnerBankId() {
		return partnerBankId;
	}

	public void setPartnerBankId(Long partnerBankId) {
		this.partnerBankId = partnerBankId;
	}
}
