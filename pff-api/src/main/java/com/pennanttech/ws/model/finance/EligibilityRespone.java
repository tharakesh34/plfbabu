package com.pennanttech.ws.model.finance;

import java.io.Serializable;

import com.pennant.backend.model.WSReturnStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class EligibilityRespone implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement
	private String result;
	private String value;
	@XmlElement
	private String ruleCode;
	@XmlElement
	private String reuleName;
	@XmlElement
	private String resultValue;
	@XmlElement
	private String deviation;
	@XmlElement
	private WSReturnStatus returnStatus;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getReuleName() {
		return reuleName;
	}

	public void setReuleName(String reuleName) {
		this.reuleName = reuleName;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public String getDeviation() {
		return deviation;
	}

	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}

}
