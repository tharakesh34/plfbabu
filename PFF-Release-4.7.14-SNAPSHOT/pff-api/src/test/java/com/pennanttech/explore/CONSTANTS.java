package com.pennanttech.explore;
//Testing values

public enum CONSTANTS {
	ChannelId("ChannelId"), User("User"), Device("Device"), AuthKey("Authorization"), Token("Token");
	
	private String value ;
	
	CONSTANTS(String value) {
		this.value = value;
	}
	
	public String get() {
		return value;
	}
}
