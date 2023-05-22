package com.pennanttech.ws.model.financetype;

import java.io.Serializable;

public class StepConfiguration implements Serializable {

	private static final long serialVersionUID = 7907132962063203943L;

	public StepConfiguration() {
	    super();
	}

	private boolean stepFinance;
	private String alwdStepPolicies;
	private boolean steppingMandatory;
	private String dftStepPolicy;
	private boolean alwManualSteps;

	public boolean isStepFinance() {
		return stepFinance;
	}

	public void setStepFinance(boolean stepFinance) {
		this.stepFinance = stepFinance;
	}

	public String getAlwdStepPolicies() {
		return alwdStepPolicies;
	}

	public void setAlwdStepPolicies(String alwdStepPolicies) {
		this.alwdStepPolicies = alwdStepPolicies;
	}

	public boolean isSteppingMandatory() {
		return steppingMandatory;
	}

	public void setSteppingMandatory(boolean steppingMandatory) {
		this.steppingMandatory = steppingMandatory;
	}

	public String getDftStepPolicy() {
		return dftStepPolicy;
	}

	public void setDftStepPolicy(String dftStepPolicy) {
		this.dftStepPolicy = dftStepPolicy;
	}

	public boolean isAlwManualSteps() {
		return alwManualSteps;
	}

	public void setAlwManualSteps(boolean alwManualSteps) {
		this.alwManualSteps = alwManualSteps;
	}
}
