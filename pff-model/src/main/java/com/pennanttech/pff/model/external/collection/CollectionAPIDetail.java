package com.pennanttech.pff.model.external.collection;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "receiptID", "finReference", "amount", "returnStatus" })
public class CollectionAPIDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private long apiID = Long.MIN_VALUE;
	@XmlElement
	private long receiptID = Long.MIN_VALUE;
	@XmlElement
	private String finReference;
	@XmlElement
	private BigDecimal amount = BigDecimal.ZERO;
	private long linkedTranId = Long.MIN_VALUE;
	@XmlElement
	private String serviceName;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElement
	private String moduleCode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getApiID() {
		return apiID;
	}

	public void setApiID(long apiID) {
		this.apiID = apiID;
	}

	public long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(long receiptID) {
		this.receiptID = receiptID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

}
