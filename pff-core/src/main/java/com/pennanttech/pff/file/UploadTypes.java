package com.pennanttech.pff.file;

public enum UploadTypes {
	RE_PRESENTMENT("Representment Upload Details Screen");

	private String description;

	private UploadTypes(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}
}
