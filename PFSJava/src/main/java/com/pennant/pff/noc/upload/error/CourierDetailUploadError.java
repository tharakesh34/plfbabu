package com.pennant.pff.noc.upload.error;

public enum CourierDetailUploadError {
	LCD_001("Loan Reference is not valid."),

	LCD_003("Invalid data provided for letter type."),

	LCD_004("Invalid data provided for delivery status."),

	LCD_005("Delivery status is mandatory."),

	LCD_006("Invalid data No Record found against Loan Reference, Letter Type, Letter Date."),

	LCD_007("Mode is Configured as Email for the uploaded Loan Reference."),

	LCD_008("Letter Details are not Available");

	private String description;

	private CourierDetailUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}