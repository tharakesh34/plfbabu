package com.pennanttech.pff.baja;

import com.pennanttech.dataengine.model.DataEngineStatus;

public class InterfaceConstants {

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

	public static DataEngineStatus	autoDisbResFileStatus	= new DataEngineStatus();
}
