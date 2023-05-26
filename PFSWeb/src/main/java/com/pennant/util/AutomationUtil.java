package com.pennant.util;

public class AutomationUtil {

	private static AutomationUtil singleInstance = null;

	public boolean recordXpath;

	private AutomationUtil() {
	}

	public static synchronized AutomationUtil getSingleton() {
		if (singleInstance == null) {
			synchronized (AutomationUtil.class) {
				singleInstance = new AutomationUtil();
			}
		}
		return singleInstance;
	}

	public boolean isRecordXpath() {
		return recordXpath;
	}

	public void setRecordXpath(boolean recordXpath) {
		this.recordXpath = recordXpath;
	}

}
