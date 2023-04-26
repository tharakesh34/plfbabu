package com.pennanttech.external.constants;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;

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
	String CONFIG_SI_RESP = "SI_RESP";

	String CONFIG_IPDC_REQ = "IPDC";
	String CONFIG_IPDC_RESP = "IPDC_RESP";

	String CONFIG_NACH_REQ = "NACH";
	String CONFIG_NACH_RESP = "NACH_RESP";

	String CONFIG_PDC_REQ = "PDC";

	String CONFIG_LIEN_REQ = "SILIEN";
	String CONFIG_LIEN_RESP = "SILIEN_RESP";

	String CONFIG_UCIC_REQ = "UCIC_REQ";
	String CONFIG_UCIC_REQ_COMPLETE = "UCIC_REQ_COMPLETE";
	String CONFIG_UCIC_RESP = "UCIC_RESP";
	String CONFIG_UCIC_RESP_COMPLETE = "UCIC_RESP_COMPLETE";
	String CONFIG_UCIC_ACK = "UCIC_ACK";
	String CONFIG_UCIC_ACK_CONF = "UCIC_ACK_CONF";
	String CONFIG_PLF_DB_SERVER = "PLF_DB_SERVER";

	String CONFIG_BASEL_ONE = "BASEL1";
	String CONFIG_BASEL_TWO = "BASEL2";
	String CONFIG_FINCON_GL = "FINCONGL";
	String CONFIG_ALM = "ALM";
	String CONFIG_RPMS = "RPMS";

	String CONFIG_UCIC_WEEKLY_FILE = "UCIC_WEEKLY";

	String SIHOLD = "SIHOLD";

	String SEQ_PRMNT_SI = "SEQ_PRMNT_SI";
	String SEQ_PRMNT_SI_INTERNAL = "SEQ_PRMNT_SI_INTERNAL";
	String SEQ_PRMNT_ACH = "SEQ_PRMNT_ACH";
	String SEQ_PRMNT_PDC = "SEQ_PRMNT_PDC";
	String SEQ_SILIEN = "SEQ_SILIEN";

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

	int ACK_PENDING = 0;
	int ACK_SENT = 1;

	int UCIC_UPDATE_SUCCESS = 0;
	int UCIC_UPDATE_FAIL = 1;

	int PROGRESS_INIT = 0;
	int PROGRESS_DONE = 1;

	// ----------------------------- ERROR CODES ------------------------------
	String F900 = "F900";
	String F901 = "F901";
	String F902 = "F902";
	String F801 = "F801";
	String F802 = "F802";
	String F803 = "F803";
	String F804 = "F804";
	String F702 = "F702";
	String F703 = "F703";
	String F704 = "F704";
	String F903 = "F903";
	String F600 = "F600";
	String F601 = "F601";
	String F602 = "F602";
	String F603 = "F603";
	String F604 = "F604";
	String F605 = "F605";
	String F606 = "F606";
	String F607 = "F607";
	// ----------------------------- ERROR CODES ------------------------------

	default ExternalConfig getDataFromList(List<ExternalConfig> mainConfig, String key) {

		for (ExternalConfig externalConfig : mainConfig) {
			if (externalConfig.getInterfaceName().equals(key)) {
				return externalConfig;
			}
		}
		return null;
	}

	default String convertAmount(BigDecimal amount, int ccy) {
		String newAmount = parseString(amount, ccy);
		BigDecimal amt = new BigDecimal(newAmount);
		if (ccy == 0) {
			return String.valueOf(amt);
		}
		DecimalFormat f = new DecimalFormat("##0.00");
		return f.format(amt);
	}

	public static String parseString(BigDecimal amount, int decimals) {

		return (parse(amount, decimals)).toPlainString();
	}

	public static BigDecimal parse(BigDecimal amount, int decimals) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(BigDecimal.valueOf(Math.pow(10, decimals)));
		}
		return bigDecimal;
	}

	default InterfaceErrorCode getErrorFromList(List<InterfaceErrorCode> interfaceErrorCodes, String key) {

		for (InterfaceErrorCode interfaceErrorCode : interfaceErrorCodes) {
			if (interfaceErrorCode.getErrorCode().equals(key)) {
				return interfaceErrorCode;
			}
		}
		return null;
	}

	default FtpClient getftpClientConnection(ExternalConfig serverConfig) {
		FtpClient ftpClient = null;
		String host = serverConfig.getHostName();
		int port = serverConfig.getPort();
		String accessKey = serverConfig.getAccessKey();
		String secretKey = serverConfig.getSecretKey();
		try {
			ftpClient = new SftpClient(host, port, accessKey, secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ftpClient;
	}

	default void uploadToSFTP(String localFileWithPath, ExternalConfig config) {
		if (config.getFileSftpLocation() == null || "".equals(config.getFileSftpLocation())) {
			return;
		}

		FtpClient ftpClient = null;
		try {
			ftpClient = getftpClientConnection(config);
			ftpClient.upload(new File(localFileWithPath), config.getFileSftpLocation());
		} catch (Exception e) {
			//
		} finally {
			if (ftpClient != null) {
				ftpClient.disconnect();
			}
		}
	}
}
