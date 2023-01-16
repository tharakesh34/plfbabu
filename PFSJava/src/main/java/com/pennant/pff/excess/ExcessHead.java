package com.pennant.pff.excess;

public enum ExcessHead {

	EMIINADV("EMIINADV"),

	ADVINT("ADVINT"),

	ADVEMI("ADVEMI"),

	CASHCLT("CASHCLT"),

	DSF("DSF"),

	TEXCESS("T");

	private String code;

	private ExcessHead(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

}
