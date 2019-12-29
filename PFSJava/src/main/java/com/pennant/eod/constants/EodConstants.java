package com.pennant.eod.constants;

public interface EodConstants {

	int PROGRESS_WAIT = 0;
	int PROGRESS_IN_PROCESS = 1;
	int PROGRESS_SUCCESS = 2;
	int PROGRESS_FAILED = 3;

	String DATA_CUSTOMERCOUNT = "CustomerCount";
	String DATA_COMPLETED = "Completed";
	String DATA_TOTALCUSTOMER = "TotalCustomer";
	String DATA_FAILED_CUSTOMER = "FailedCustomer";

	// Collateral De-Marking
	String FIN_CLOSESTS = "M";
	String COLLT_MARKSTS = "MARK";
	String COLLT_DEMARKSTS = "DEMARK";
	String THREAD = "PFSEOD";
	String STATUS = "STATUS";

	String RECORD_INSERT = "I";
	String RECORD_UPDATE = "U";

	String SQL_NOLOCK = " with (nolock) ";
}
