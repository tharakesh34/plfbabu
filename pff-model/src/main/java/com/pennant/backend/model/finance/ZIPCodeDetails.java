package com.pennant.backend.model.finance;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ZIPCodeDetails extends AbstractWorkflowEntity implements Entity {

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

	@Override
	public long getId() {
		return pinCodeId;
	}

	@Override
	public void setId(long id) {
		this.pinCodeId = id;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	@Override
	public boolean isNew() {
		return false;
	}

}
