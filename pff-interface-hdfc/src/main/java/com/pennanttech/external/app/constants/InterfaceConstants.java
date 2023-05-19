package com.pennanttech.external.app.constants;

public interface InterfaceConstants {

	String PLF_SI = "SI";
	String PLF_IPDC = "IPDC";
	String PLF_NACH = "NACH";
	String PLF_PDC = "PDC";
	String PLF_ECS = "ECS";
	String PLF_E_NACH = "ENACH";
	String PLF_E_MANDATE = "EMANDATE";

	String SIHOLD = "SIHOLD";

	String SEQ_PRMNT_SI = "SEQ_PRMNT_SI";
	String SEQ_PRMNT_SI_INTERNAL = "SEQ_PRMNT_SI_INTERNAL";
	String SEQ_PRMNT_ACH = "SEQ_PRMNT_ACH";
	String SEQ_PRMNT_PDC = "SEQ_PRMNT_PDC";
	String SEQ_SILIEN = "SEQ_SILIEN";
	String SEQ_FINCON_GL = "SEQ_FINCON_GL";
	String SEQ_COLLECTION_RECEIPT = "SEQ_COLLECTION_RECEIPT";
	String SEQ_GST_INTF = "SEQ_GST_INTF";

	int ccyFromat = 2;
	String fileSeperator = "~";
	String pipeSeperator = "|";
	String EMPTY = " ";

	int FILE_NOT_WRITTEN = 0;
	int FILE_WRITTEN = 1;

	String LIEN_MARK = "Y";
	String LIEN_REMOVE = "N";

	String SUCCESS = "S";
	String FAIL = "F";

	int BULK_RECORD_COUNT = 1000;

	int STATE_INACTIVE = 0;
	int STATE_ACTIVE = 1;

	String STATUS_COMPLETED = "SUCCESS";
	String ERR_SKIPPED = "SKIPPED";
	String ERR_EXCEPTION = "EXCEPTION";
	String STATUS_COMPLETED_MSG = "SUCCESS";

	int UNPROCESSED = 0;
	int INPROCESS = 1;
	int COMPLETED = 2;
	int FAILED = 3;
	int EXCEPTION = 4;

	int ENABLED = 1;
	int DISABLED = 0;

	int ACK_PENDING = 0;
	int ACK_SENT = 1;

	int UCIC_UPDATE_SUCCESS = 0;
	int UCIC_UPDATE_FAIL = 1;

	int PROGRESS_INIT = 0;
	int PROGRESS_DONE = 1;

	String LIEN_PENDING = "PENDING";
	String LIEN_SUCCESS = "SUCCESS";
	String LIEN_AWAITING_CONF = "AC";
	String LIEN_FAILED = "FAILED";

}