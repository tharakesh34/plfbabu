package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;

@XmlAccessorType(XmlAccessType.FIELD)
public class UserActions implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "UserActions")
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
