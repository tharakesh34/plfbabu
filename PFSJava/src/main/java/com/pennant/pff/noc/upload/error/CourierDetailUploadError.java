package com.pennant.pff.noc.upload.error;

public enum CourierDetailUploadError {
	LCD_001("Loan Reference is not valid."),

	LCD_002("Loan Reference is not in active."),

	LCD_003("Invalid data provided for letter type."),

	LCD_004("Invalid data provided for delivery status."),

	LCD_005("Delivery status is mandatory.");

	private String description;

	private CourierDetailUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}