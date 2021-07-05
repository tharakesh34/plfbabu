package com.pennant.app.constants;

public enum AccountingEvent {
	DISBINS(AccountEventConstants.ACCEVENT_DISBINS), VASFEE(AccountEventConstants.ACCEVENT_VAS_FEE);

	private String code;
	private String description;

	private AccountingEvent(String code) {

	}
}
