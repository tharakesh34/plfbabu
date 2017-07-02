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
	public static DataEngineStatus	ALM_EXTRACT_STATUS		= new DataEngineStatus("ALM_REQUEST");
	public static DataEngineStatus	CONTROL_DUMP_REQUEST_STATUS		= new DataEngineStatus("CONTROL_DUMP_REQUEST");

	public static DataEngineStatus POSIDEX_RESPONSE_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
	public static DataEngineStatus POSIDEX_REQUEST_STATUS = new DataEngineStatus("POSIDEX_CUSTOMER_UPDATE_REQUEST");
	public static DataEngineStatus DATA_MART_STATUS = new DataEngineStatus("DATA_MART_REQUEST");
	public static DataEngineStatus TRAIL_BALANCE_EXPORT_STATUS = new DataEngineStatus("GL_TRAIL_BALANCE_EXPORT");
	public static DataEngineStatus CIBIL_EXPORT_STATUS = new DataEngineStatus("CIBIL_EXPORT_STATUS");
	public static DataEngineStatus GST_TAXDOWNLOAD_STATUS = new DataEngineStatus("GST_TAXDOWNLOAD_DETAILS");

}
