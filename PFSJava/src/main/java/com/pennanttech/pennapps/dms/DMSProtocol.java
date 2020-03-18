package com.pennanttech.pennapps.dms;

public enum DMSProtocol {
	FTP, SFTP, AMAZON_S3;

	public static DMSProtocol getProtocol(String protocol) {
		for (DMSProtocol dmsProtocol : DMSProtocol.values()) {
			if (dmsProtocol.name().equals(protocol)) {
				return dmsProtocol;
			}
		}
		return null;
	}

	public static String getProtocol(DMSProtocol dmsProtocol) {
		return dmsProtocol == null ? "" : dmsProtocol.name();
	}
}
