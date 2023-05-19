package com.pennant.pff.hostglmapping.service.impl;

public enum HostGLMappingUploadError {
	HGL01("[Account Type] is Mandatory."),

	HGL02("[Host GL Code] is Mandatory."),

	HGL03("[Account Type] is not valid."),

	HGL04("[Loan Type] is not valid."),

	HGL05("[Cost Centre] is not valid."),

	HGL06("[Profit Centre] is not valid."),

	HGL08("[Allow Manual Entries] is not valid, possible value should be either of the following {N/D/C/B}."),

	HGL10("[Opened Date] is not allowed Future Date."),

	HGL11("[Host GL Code] is not valid. Allowed Characters are {a-z A-Z 0-9}.");

	private String description;

	private HostGLMappingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
