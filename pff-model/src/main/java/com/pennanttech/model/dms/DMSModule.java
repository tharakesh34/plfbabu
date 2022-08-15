package com.pennanttech.model.dms;

public enum DMSModule {
	CUSTOMER, FINANCE, COLLATERAL, QUERY_MGMT, COVENANT, VERIFICATION, LEGAL, VAS, CHEQUE, MANDATE, COMMITMENT,
	SAMPLING, FI, TV, PD, RCU, OCR, RECEIPT, DISBINST, LV, DMS;

	public static DMSModule getModule(String module) {
		for (DMSModule dmsModule : DMSModule.values()) {
			if (dmsModule.name().equals(module)) {
				return dmsModule;
			}
		}
		return null;
	}

	public static String getModule(DMSModule dmsModule) {
		return dmsModule == null ? "" : dmsModule.name();
	}
}
