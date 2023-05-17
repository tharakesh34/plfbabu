package com.pennanttech.external.constants;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.external.config.model.FileInterfaceConfig;
import com.pennanttech.external.config.model.InterfaceErrorCode;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
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
	String F500 = "F500";
	String F400 = "F400";
	String F401 = "F401";
	String F402 = "F402";
	String F403 = "F403";
	String F404 = "F404";
	// ----------------------------- ERROR CODES ------------------------------

	default ExtCollectionReceiptData splitAndSetData(String lineData) {
		try {
			ExtCollectionReceiptData collectionData = new ExtCollectionReceiptData();
			String[] dataArray = lineData.toString().split("\\|");
			collectionData.setAgreementNumber(setLongData(dataArray, 1, collectionData));
			collectionData.setCollection(setStringData(dataArray, 2, collectionData));
			collectionData.setReceiptChannel(setStringData(dataArray, 3, collectionData));
			collectionData.setAgencyId(setLongData(dataArray, 4, collectionData));
			collectionData.setChequeNumber(setLongData(dataArray, 5, collectionData));
			collectionData.setDealingBankId(setLongData(dataArray, 6, collectionData));
			collectionData.setDrawnOn(setStringData(dataArray, 7, collectionData));
			collectionData.setTowards(setStringData(dataArray, 8, collectionData));
			collectionData.setGrandTotal(setBigDecimalData(dataArray, 9, collectionData));
			collectionData.setReceiptDate(setDateData(dataArray, 10, collectionData));
			collectionData.setChequeDate(setDateData(dataArray, 11, collectionData));
			collectionData.setReceiptType(setStringData(dataArray, 12, collectionData));
			collectionData.setReceiptNumber(setLongData(dataArray, 13, collectionData));
			collectionData.setChequeStatus(setStringData(dataArray, 14, collectionData));
			collectionData.setAutoAlloc(setStringData(dataArray, 15, collectionData));
			collectionData.setEmiAmount(setBigDecimalData(dataArray, 16, collectionData));
			collectionData.setLppAmount(setBigDecimalData(dataArray, 17, collectionData));
			collectionData.setBccAmount(setBigDecimalData(dataArray, 18, collectionData));
			collectionData.setExcessAmount(setBigDecimalData(dataArray, 19, collectionData));
			collectionData.setOthercharge1(setLongData(dataArray, 20, collectionData));
			collectionData.setOtherAmt1(setBigDecimalData(dataArray, 21, collectionData));
			collectionData.setOtherCharge2(setLongData(dataArray, 22, collectionData));
			collectionData.setOtherAmt2(setBigDecimalData(dataArray, 23, collectionData));
			collectionData.setOtherCharge3(setLongData(dataArray, 24, collectionData));
			collectionData.setOtherAmt3(setBigDecimalData(dataArray, 25, collectionData));
			collectionData.setOtherCharge4(setLongData(dataArray, 26, collectionData));
			collectionData.setOtherAmt4(setBigDecimalData(dataArray, 27, collectionData));
			collectionData.setRemarks(setStringData(dataArray, 28, collectionData));
			collectionData.setBatched(setLongData(dataArray, 29, collectionData));
			collectionData.setRedepositionflg(setStringData(dataArray, 30, collectionData));
			collectionData.setRowNum(setLongData(dataArray, 31, collectionData));
			collectionData.setChecksum(setStringData(dataArray, 32, collectionData));
			return collectionData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String setStringData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			return dataArray[position - 1];
		}
		return "";
	}

	private long setLongData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			if (!"".equals(dataArray[position - 1])) {
				return Long.parseLong(dataArray[position - 1]);
			}
		}
		return 0L;
	}

	@SuppressWarnings("deprecation")
	private Date setDateData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		try {
			if (dataArray.length >= position) {
				return new Date(dataArray[position - 1]);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private BigDecimal setBigDecimalData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			if (!"".equals(dataArray[position - 1])) {
				return new BigDecimal(dataArray[position - 1]);
			}
		}
		return new BigDecimal(0);
	}

	default int generateChecksum(String data) {
		int rcdCS = 0;
		for (int i = 0; i < data.length(); i++) {
			char digit = data.charAt(i);
			int asciiCode = (int) digit;
			rcdCS = rcdCS + asciiCode;
		}
		return rcdCS;
	}

	default FileInterfaceConfig getDataFromList(List<FileInterfaceConfig> mainConfig, String key) {

		for (FileInterfaceConfig externalConfig : mainConfig) {
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

	default FtpClient getftpClientConnection(FileInterfaceConfig serverConfig) {
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

	default void uploadToSFTP(String localFileWithPath, FileInterfaceConfig config) {
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

	default List<String> getFileNameList(String pathname, String hostName, int port, String accessKey,
			String secretKey) {
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(accessKey, hostName, port);
			session.setPassword(secretKey);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e1) {
			e1.printStackTrace();
		}
		channelSftp = (ChannelSftp) channel;
		LsEntry entry = null;
		List<String> fileNames = new ArrayList<String>();
		Vector filelist = null;
		try {
			filelist = ((ChannelSftp) channel).ls(pathname);
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
		for (int i = 0; i < filelist.size(); i++) {
			entry = (LsEntry) filelist.get(i);
			if (StringUtils.isNotEmpty(FilenameUtils.getExtension(entry.getFilename()))
					&& !entry.getFilename().startsWith(".")) {
				fileNames.add(entry.getFilename());
			}
		}
		return fileNames;
	}

	default List<String> fetchRespFiles(FileInterfaceConfig reqConfig) {
		List<String> respFileNames = new ArrayList<String>();
		String reqFolderPath = App.getResourcePath(reqConfig.getFileLocation());
		if (reqFolderPath != null && !"".equals(reqFolderPath)) {
			File reqDirPath = new File(reqFolderPath);
			if (reqDirPath.isDirectory()) {
				// Fetch the list of request files from configured folder
				File filesList[] = reqDirPath.listFiles();
				if (filesList != null && filesList.length > 0) {
					for (File file : filesList) {
						respFileNames.add(file.getName());
					}
				}
			}
		}
		return respFileNames;
	}
}