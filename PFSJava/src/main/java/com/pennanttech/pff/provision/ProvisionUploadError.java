package com.pennanttech.pff.provision;

public enum ProvisionUploadError {
	PROVSN_01("FinReference is not exist or not active."),

	PROVSN_02("Override Provision should be either 'Y' or 'N'."),

	PROVSN_03("Manual Provision% should be greaterthan 1 and less than or equal to 100."),

	PROVSN_04("Manual Asset Classification is not valid."),

	PROVSN_05("Manual Asset Sub-Classification is not valid."),

	PROVSN_06("Manual Provision% should be zero when Override Provision is 'N'."),

	PROVSN_07("Asset Classification is not found, Manual Provision upload is allowed after execution of EOD."),

	PROVSN_08("Manual Asset Classification can't be upgarded");

	private String description;

	private ProvisionUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
