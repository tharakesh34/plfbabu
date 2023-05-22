package com.pennant.backend.service.finance;

import java.io.Serializable;
import java.math.BigDecimal;

public class EligibilityRule implements Serializable {

	private static final long serialVersionUID = 1480102644001246839L;

	private String ruleCode;
	private String ruleCodeDesc;
	private String finType;
	private String finTypeDesc;
	private String ruleReturnType;
	private BigDecimal elgAmount;

	public EligibilityRule() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}

	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinType() {
		return finType;
	}

	public void setRuleReturnType(String ruleReturnType) {
		this.ruleReturnType = ruleReturnType;
	}

	public String getRuleReturnType() {
		return ruleReturnType;
	}

	public BigDecimal getElgAmount() {
		return elgAmount;
	}

	public void setElgAmount(BigDecimal elgAmount) {
		this.elgAmount = elgAmount;
	}

	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}

}
