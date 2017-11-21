package com.pennanttech.framework.security.ldap;

public enum UserAttributes {
	EMAIL("mail"), MOBILE("mobile"), FIRST_NAME("givenName"), LAST_NAME("sn"), MIDDLE_NAME("middleName");

	private String attribute;

	UserAttributes(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	public static String[] getAttributes() {
		return new String[] { EMAIL.getAttribute(), MOBILE.getAttribute(), FIRST_NAME.getAttribute(),
				LAST_NAME.getAttribute(), MIDDLE_NAME.getAttribute() };
	}
}
