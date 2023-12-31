package com.pennant.backend.model.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlAccessorType(XmlAccessType.NONE)
public class ZIPCodeDetails extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -9134566802522550432L;

	private long pinCodeId = Long.MIN_VALUE;
	@XmlElement
	private String pinCode;
	@XmlElement
	private String city;
	@XmlElement
	private String state;
	@XmlElement
	private WSReturnStatus returnStatus = null;

	public long getPinCodeId() {
		return pinCodeId;
	}

	public void setPinCodeId(long pinCodeId) {
		this.pinCodeId = pinCodeId;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getId() {
		return pinCodeId;
	}

	public void setId(long id) {
		this.pinCodeId = id;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
