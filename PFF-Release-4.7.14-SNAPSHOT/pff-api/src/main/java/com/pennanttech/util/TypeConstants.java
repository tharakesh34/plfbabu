package com.pennanttech.util;

public enum TypeConstants {
	REQUEST(1), RESPONSE(2);
	private int value ;
	
	TypeConstants(int value) {
		this.value = value;
	}
	
	public int get() {
		return value;
	}
}
