package com.pennanttech.external.app.constants;

import java.util.List;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;

public interface InterfaceConstants {

	String PLF_SI = "SI";
	String PLF_IPDC = "IPDC";
	String PLF_NACH = "NACH";
	String PLF_PDC = "PDC";
	String PLF_ECS = "ECS";
	String PLF_E_NACH = "ENACH";
	String PLF_E_MANDATE = "EMANDATE";

	// ----------------

	String CONFIG_SI_REQ = "SI";
	String CONFIG_IPDC_REQ = "IPDC";
	String CONFIG_NACH_REQ = "NACH";
	String CONFIG_PDC_REQ = "PDC";
	String CONFIG_LIEN_REQ = "SILIEN_REQ";
	String CONFIG_UCIC_REQ = "UCIC_REQ";
	String CONFIG_UCIC_REQ_COMPLETE = "UCIC_REQ_COMPLETE";
	String CONFIG_UCIC_RESP = "UCIC_RESP";
	String CONFIG_UCIC_RESP_COMPLETE = "UCIC_RESP_COMPLETE";
	String CONFIG_UCIC_ACK = "UCIC_ACK";
	String CONFIG_UCIC_ACK_CONF = "UCIC_ACK_CONF";
	String CONFIG_PLF_DB_SERVER = "PLF_DB_SERVER";
	String CONFIG_FINCONGL = "FINCONGL";
	String CONFIG_COLLECTION_REQ_CONF = "COLLECTION_RECEIPT_REQ";
	String CONFIG_COLLECTION_RESP_CONF = "COLLECTION_RECEIPT_RESP";
	String CONFIG_GST_REQ = "GST_REQ";
	String CONFIG_GST_REQ_DONE = "GST_REQ_DONE";
	String CONFIG_GST_RESP = "GST_RESP";
	String CONFIG_GST_RESP_DONE = "GST_RESP_DONE";

	String CONFIG_BASEL_ONE = "BASEL1";
	String CONFIG_BASEL_TWO = "BASEL2";
	String CONFIG_FINCON_GL = "FINCONGL";
	String CONFIG_ALM = "ALM";
	String CONFIG_RPMS = "RPMS";

	String SP_BASEL_ONE = "SP_EXT_BASEL_ONE";
	String SP_ALM_REPORT = "SP_ALM_REPORT";
	String SP_FINCON_GL = "SP_FINCON_GL";
	String SP_FINCON_WRITE_FILE = "SP_FINCON_WRITE_FILE";

	String CONFIG_UCIC_WEEKLY_FILE = "UCIC_WEEKLY";

	String CONFIG_SI_RESP = "SI_RESP";
	String CONFIG_IPDC_RESP = "IPDC_RESP";
	String CONFIG_NACH_RESP = "NACH_RESP";
	String CONFIG_LIEN_RESP = "SILIEN_RESP";

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

	default FileInterfaceConfig getDataFromList(List<FileInterfaceConfig> mainConfig, String key) {

		for (FileInterfaceConfig externalConfig : mainConfig) {
			if (externalConfig.getInterfaceName().equals(key)) {
				return externalConfig;
			}
		}
		return null;
	}

}