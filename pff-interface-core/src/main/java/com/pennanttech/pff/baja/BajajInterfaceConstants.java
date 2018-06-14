package com.pennanttech.pff.baja;

import com.pennanttech.dataengine.model.DataEngineStatus;

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

	public static final String		DBDateFormat		= "yyyy-MM-dd";

	public static boolean			autoDisbResFileJob;
	public static String			autoDisbFileLoaction;
	public static boolean			autoMandateResFileJob;

	public static DataEngineStatus POSIDEX_RESPONSE_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
	
}
