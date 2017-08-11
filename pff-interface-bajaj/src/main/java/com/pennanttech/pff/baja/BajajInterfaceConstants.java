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

	public static DataEngineStatus	DISB_STP_IMPORT_STATUS		= new DataEngineStatus("DISB_HDFC_IMPORT");
	public static DataEngineStatus	DISB_OTHER_IMPORT_STATUS	= new DataEngineStatus("DISB_OTHER_IMPORT");
	public static DataEngineStatus	MANDATE_INMPORT_STATUS		= new DataEngineStatus("MANDATES_IMPORT");
	

	public static DataEngineStatus POSIDEX_RESPONSE_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
	
}
