package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserActions implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("UserActions")
	List<ValueLabel> valueLabel;

	private WSReturnStatus returnStatus;

	public UserActions() {
		super();
	}

	public List<ValueLabel> getValueLabel() {
		return valueLabel;
	}

	public void setValueLabel(List<ValueLabel> valueLabel) {
		this.valueLabel = valueLabel;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
