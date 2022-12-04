package com.pennant.pff.presentment.exception;

public class PresentmentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;

	public PresentmentException(PresentmentError error) {
		super(error.description());

		this.code = error.code();
		this.description = error.description();
	}

	public PresentmentException(String code, String description) {
		super(description);

		this.code = code;
		this.description = description;
	}

	public String code() {
		return code;
	}

	public String description() {
		return description;
	}
}
