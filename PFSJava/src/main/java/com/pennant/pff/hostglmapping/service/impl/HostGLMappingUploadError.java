package com.pennant.pff.hostglmapping.service.impl;

public enum HostGLMappingUploadError {
	HGL01("AccountType is Mandatory."),

	HGL02("Host GL Code is Mandatory."),

	HGL03("In-valid Account Type."),

	HGL04("In-valid Loan Type."),

	HGL05("In-valid Cost Centre."),

	HGL06("In-valid Profit Centre."),

	HGL08("Possible values should be 'N','D','C' Or 'B'."),

	HGL09("GL Description is invalid.Allowed Characters are [a-z A-Z 0-9]."),

	HGL10("Future Date is not allowed."),

	HGL11("Host GL Code is invalid.Allowed Characters are [a-z A-Z 0-9].");

	private String description;

	private HostGLMappingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
