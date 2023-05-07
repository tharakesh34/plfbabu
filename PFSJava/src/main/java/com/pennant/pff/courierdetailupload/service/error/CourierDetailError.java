package com.pennant.pff.courierdetailupload.service.error;

public enum CourierDetailError {

	LCD_001("Fin Reference is not valid."),

	LCD_002("Fin Reference is not in active."),

	LCD_003("Invalid data, provided for Letter Type."),

	LCD_004("Invalid data, provided for Delivery Status."),

	LCD_005("Delivery Status is Mandatory.");

	private String description;

	private CourierDetailError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

}
