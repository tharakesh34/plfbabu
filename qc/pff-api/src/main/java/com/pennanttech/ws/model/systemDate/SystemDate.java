package com.pennanttech.ws.model.systemDate;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "appDate", "valueDate", "returnStatus" })
@XmlRootElement(name = "systemDate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SystemDate implements Serializable {
	private static final long serialVersionUID = -2329587803168853708L;

	public SystemDate() {
		super();
	}

	private Date appDate;
	private Date valueDate;
	private WSReturnStatus returnStatus;

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
}
