package com.pennanttech.pff.provision;

public enum ProvisionBook {
	NO_PROVISION(0, "No Provision"), REGULATORY(1, "Regulatory"), INTERNAL(2, "Internal");

	private final int book;
	private final String description;

	private ProvisionBook(int book, String description) {
		this.book = book;
		this.description = description;
	}

	public int book() {
		return book;
	}

	public String description() {
		return description;
	}
}
