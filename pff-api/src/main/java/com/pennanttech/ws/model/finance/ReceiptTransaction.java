package com.pennanttech.ws.model.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "finReference", "receiptId", "UTRNumber" })
@XmlAccessorType(XmlAccessType.NONE)
public class ReceiptTransaction extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 2803331023129230226L;

	private long finID;
	private String finReference;
	private long receiptId;
	private String utrNumber;
	private String status;
	private String receiptMode;

	public ReceiptTransaction() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	@XmlElement
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getReceiptId() {
		return receiptId;
	}

	@XmlElement
	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	@XmlElement(name = "UTRNumber")
	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public String getStatus() {
		return status;
	}

	@XmlElement
	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceiptMode() {
		return receiptMode;
	}

	public void setReceiptMode(String receiptMode) {
		this.receiptMode = receiptMode;
	}

}
