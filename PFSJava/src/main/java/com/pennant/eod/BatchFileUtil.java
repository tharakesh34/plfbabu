package com.pennant.eod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BatchFileUtil {

	public static final String DELIMITER = "|";
	public static final String SEMI_COLON = ";";

	public static final String HEADER = "H";
	public static final String DETAILS = "B";
	public static final String FOOTER = "T";
	public static final String FILE_EXT = ".txt";
	public static final String TXT_QUA = "'";

	// Date Formats
	public static final String DATE_FORMAT_YMd = "yyyyMMdd";
	public static final String DATE_FORMAT_YMD = "yyyyMMDD";
	public static final String DATE_FORMAT_DMYT = "DDMMyyyykkmm";
	public static final String DATE_FORMAT_MDY = "MMddyyyy";

	// Auto Payments
	public static final String BATCH_CODE = "T24_ACCT_POST";

	// Salary Postings
	public static final String SLARY_POST_BATCH_TYPE = "T24_FIN_INST";

	// Constants for SMS
	public static final String SERVICE = "MBM.SMS.V.1.1";
	public static final String SMS = "SMS";
	public static final String SERVICEID_1023 = "1023";
	public static final String SERVICEID_1024 = "1024";

	/********** File Name Methods **********/

	// Auto Payments
	public static String getAutoPayReqFileName() {
		StringBuilder filename = new StringBuilder();
		filename.append("AccountPost_Daily_");
		filename.append(DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_YMd));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	public static String getAutoPayResFileName() {
		StringBuilder filename = new StringBuilder();
		filename.append("AccountPost_Resp_Daily_");
		filename.append(DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_YMd));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	// Salary Postings
	public static String getSlaryPostingFileName() {
		StringBuilder filename = new StringBuilder();
		filename.append("Fin_Installments_Daily_");
		filename.append(DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_YMd));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	// Limit Utilization File
	public static String getLimitUtilFileName() {
		StringBuilder filename = new StringBuilder();
		filename.append("PFF_FIN_DTL_ACP_");
		filename.append(DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_DMYT));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	// ERP Postings
	public static String getERPFileName() {
		StringBuilder filename = new StringBuilder();
		filename.append("PFF_ERP_FT_");
		filename.append(DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_DMYT));
		filename.append(FILE_EXT);
		return "/" + filename.toString();
	}

	/********** Common Methods **********/

	public static String getBatchReference() {
		return DateUtil.format(SysParamUtil.getAppDate(), BatchFileUtil.DATE_FORMAT_YMd);
	}

	public static File getBatchFile(String fileName) throws IOException {
		return getFile(fileName, true);
	}

	private static File getFile(String fileName, boolean create) throws IOException {
		File file = getFile(fileName);
		if (!file.exists()) {
			if (create) {
				file.createNewFile();
			}
		}
		return file;
	}

	public static File getFile(String fileName) throws IOException {
		File file = new File(PathUtil.getPath(PathUtil.EOD_FILE_FOLDER) + "/" + fileName);
		return file;
	}

	public static FileWriter getFileWriter(File file) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		return fileWriter;
	}

	public static void writeline(FileWriter fileWriter, String line) throws IOException {
		String newLine = System.getProperty("line.separator");
		fileWriter.write(line + newLine);
	}

}
