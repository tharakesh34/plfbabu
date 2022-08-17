package com.pennanttech.pff.provision;

public enum ProvisionReversalStage {
	SOM(0, "Start of the month"), EOM(1, "End of the month");

	private final int stage;
	private final String description;

	private ProvisionReversalStage(int stage, String description) {
		this.stage = stage;
		this.description = description;
	}

	public int stage() {
		return stage;
	}

	public String descriptin() {
		return description;
	}
}
