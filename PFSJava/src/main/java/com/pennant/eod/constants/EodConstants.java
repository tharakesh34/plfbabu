package com.pennant.eod.constants;

public interface EodConstants {
	String	STATUS_SUCCESS		= "SUCCESS";
	String	STATUS_FAILED		= "FAILED";
	String	STATUS_COMPLETED	= "COMPLETED";
	String	STATUS_STARTED		= "STARTED";
	String	STATUS_RUNNING		= "RUNNING";

	String	PROGRESS_START		= "STARTED";
	String	PROGRESS_COMPLETED	= "COMPLETED";
	String	MICRO_EOD			= "MICROEOD";

	// Collateral De-Marking
	String	FIN_CLOSESTS		= "M";
	String	COLLT_MARKSTS		= "MARK";
	String	COLLT_DEMARKSTS		= "DEMARK";
	String	THREAD				= "PFSEOD";
}
