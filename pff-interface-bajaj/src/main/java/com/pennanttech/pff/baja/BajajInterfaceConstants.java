package com.pennanttech.pff.baja;


public class BajajInterfaceConstants {

	/**
	 * <p>
	 * Defines supported Status of the records
	 * </p>
	 * 
	 */
	public enum Status {
		N, Y, C, AC, E, R, I
	}

	public static final String		DBDateFormat			= "yyyy-MM-dd";

	public static boolean			autoDisbResFileJob;
	public static String			autoDisbFileLoaction;
	public static boolean           autoMandateResFileJob;
}
