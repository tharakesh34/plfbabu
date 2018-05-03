package com.pennant.eod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.beans.PaymentRecoveryDetail;
import com.pennant.eod.beans.PaymentRecoveryHeader;

public class EODResponseSimulator {

	private static Logger		logger				= Logger.getLogger(EODResponseSimulator.class);

	public static final String	DELIMITER			= "|";

	public static final String	HEADER				= "H";
	public static final String	DETAILS				= "B";
	public static final String	FOOTER				= "T";

	public static final String	BATCH_CODE			= "T24_ACCT_POST";
	public static final String	FILE_EXT			= ".txt";
	public static final String	REQ_FILE			= "AccountPost_Daily_";
	public static final String	RSP_FILE			= "AccountPost_Resp_Daily_";
	public static final String	FILE_DATE_FORMAT	= "YYYYMMdd";

	// attributes
	public static final String	REQUEST_DATA		= "RequestData";
	public static final String	RESPONSE_DATA		= "ResponseData";

	public static void main(String[] args) throws Exception {
		EODResponseSimulator.writeFile();
	}

	/**
	 * @param header
	 * @throws Exception 
	 */
	public static void writeFile() throws Exception {

		logger.debug(" Entering ");

		try {
			

			String roothpath = "D:/configlocation/PFF/AHB/Downloads/EOD/";

			File file = new File(roothpath);

			String[] listfiles = file.list();

			for (String string : listfiles) {

				BufferedReader reader = new BufferedReader(new FileReader(roothpath + string));

				PaymentRecoveryHeader header = readData(reader);

				reader.close();

				String filename = header.getFileName().replace(REQ_FILE, RSP_FILE);
				header.setFileName(filename);

				File wfile = new File("D:/configlocation/PFF/AHB/Downloads/EOD/" + filename);
				if (!wfile.exists()) {
					wfile.createNewFile();
				}
				List<String> lines = new ArrayList<>();

				lines.add(writeHeader(header));

				lines.addAll(writeDetails(header));

				lines.add(writeFooter(header));
				
				FileWriter fileWriter=new FileWriter(wfile);
				
				String newLine = System.getProperty("line.separator");
				
				for (String adada : lines) {
					fileWriter.write(adada+newLine);
				}
				fileWriter.close();

				//writeFile(wfile.toPath(), lines);
			}

			logger.debug(" Leaving ");
		} catch (Exception e) {
			logger.debug(e);
			throw e;

		}

		logger.debug(" Leaving ");

	}

	/**
	 * @param header
	 * @throws Exception 
	 */
	public static PaymentRecoveryHeader readData(BufferedReader reader) throws Exception {
		logger.debug(" Entering ");

		try {

			PaymentRecoveryHeader header = new PaymentRecoveryHeader();
			header.setPaymentRecoveryDetails(new ArrayList<PaymentRecoveryDetail>(1));

			String line = null;
			while ((line = reader.readLine()) != null) {

				String[] details = line.split("[" + DELIMITER + "]");

				String recordIdentifier = details[0];

				switch (recordIdentifier) {
				case HEADER:
					readHeader(details, header);
					break;
				case FOOTER:
					readFooter(details, header);
					break;
				case DETAILS:
					readDetails(details, header);
					break;

				default:
					break;
				}

			}

			reader.close();
			logger.debug(" Leaving ");
			return header;
		} catch (Exception e) {
			logger.debug(e);
			throw e;

		}
	}

	public static String getRequestFileName() {
		Date appdate = DateUtility.getAppDate();
		StringBuilder filename = new StringBuilder();
		filename.append(REQ_FILE);
		filename.append(DateUtility.format(appdate, EODResponseSimulator.FILE_DATE_FORMAT));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	public static String getResponseFileName() {
		Date appdate = DateUtility.getAppDate();
		StringBuilder filename = new StringBuilder();
		filename.append(RSP_FILE);
		filename.append(DateUtility.format(appdate, EODResponseSimulator.FILE_DATE_FORMAT));
		filename.append(FILE_EXT);
		return filename.toString();
	}

	/**
	 * @param details
	 * @param header
	 * @throws ParseException 
	 */
	private static void readHeader(String[] details, PaymentRecoveryHeader header) throws ParseException {
		logger.debug(" Entering ");

		header.sethRecordIdentifier(details[0]);
		header.setBatchType(details[1]);
		header.setBatchRefNumber(details[2]);
		header.setFileName(details[3]);
		header.setFileCreationDate(DateUtility.parse(details[4], DateUtility.DateFormat.SHORT_DATE_TIME));

		logger.debug(" Leaving ");
	}

	/**
	 * @param details
	 * @param header
	 */
	private static void readFooter(String[] details, PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		header.settRecordIdentifier(details[0]);
		header.setNumberofRecords(Integer.parseInt(details[1]));

		logger.debug(" Leaving ");

	}

	/**
	 * @param details
	 * @param heade
	 * @throws ParseException 
	 */
	private static void readDetails(String[] details, PaymentRecoveryHeader header) throws ParseException {
		logger.debug(" Entering ");

		PaymentRecoveryDetail recoveryDetail = new PaymentRecoveryDetail();

		recoveryDetail.setRecordIdentifier(details[0]);
		recoveryDetail.setTransactionReference(details[1]);
		recoveryDetail.setPrimaryDebitAccount(details[2]);
		recoveryDetail.setSecondaryDebitAccounts(details[3]);
		recoveryDetail.setCreditAccount(details[4]);
		recoveryDetail.setScheduleDate(DateUtility.parse(details[5],PennantConstants.DBDateFormat));
		recoveryDetail.setFinanceReference(details[6]);
		recoveryDetail.setCustomerReference(details[7]);
		recoveryDetail.setDebitCurrency(details[8]);
		recoveryDetail.setCreditCurrency(details[9]);
		recoveryDetail.setPaymentAmount(new BigDecimal(details[10]));
		recoveryDetail.setTransactionPurpose(details[11]);
		recoveryDetail.setFinanceBranch(details[12]);
		recoveryDetail.setFinanceType(details[13]);
		recoveryDetail.setFinancePurpose(details[14]);
//		recoveryDetail.setSysTranRef(details[15]);
//		recoveryDetail.setPrimaryAcDebitAmt(details[16]);
//		recoveryDetail.setSecondaryDebitAccounts(details[17]);
//		recoveryDetail.setPaymentStatus(details[18]);

		header.getPaymentRecoveryDetails().add(recoveryDetail);
		logger.debug(" Leaving ");

	}

	/**
	 * @param header
	 * @return
	 */
	private static String writeHeader(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		builder.append(header.gethRecordIdentifier());
		builder.append(DELIMITER);
		builder.append(header.getBatchType());
		builder.append(DELIMITER);
		builder.append(header.getBatchRefNumber());
		builder.append(DELIMITER);
		builder.append(header.getFileName());
		builder.append(DELIMITER);
		builder.append(DateUtility.format(header.getFileCreationDate(),DateUtility.DateFormat.SHORT_DATE_TIME));

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param header
	 * @return
	 */
	private static String writeFooter(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		builder.append(header.gettRecordIdentifier());
		builder.append(DELIMITER);
		builder.append(header.getNumberofRecords());

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param header
	 * @return
	 */
	private static List<String> writeDetails(PaymentRecoveryHeader header) {
		logger.debug(" Entering ");

		List<PaymentRecoveryDetail> list = header.getPaymentRecoveryDetails();
		List<String> lines = new ArrayList<>();

		for (PaymentRecoveryDetail recoveryDetail : list) {
			StringBuilder builder = new StringBuilder();
			builder.append(recoveryDetail.getRecordIdentifier());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getTransactionReference());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getPrimaryDebitAccount());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getSecondaryDebitAccounts());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getCreditAccount());
			builder.append(DELIMITER);
			builder.append(DateUtility.format(recoveryDetail.getScheduleDate(),PennantConstants.DBDateFormat));
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getFinanceReference());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getCustomerReference());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getDebitCurrency());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getCreditCurrency());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getPaymentAmount());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getTransactionPurpose());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getFinanceBranch());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getFinanceType());
			builder.append(DELIMITER);
			builder.append(recoveryDetail.getFinancePurpose());
			builder.append(DELIMITER);
			// dummy response
			builder.append("T24Reference");
			builder.append(DELIMITER);
//			recoveryDetail.setPaymentAmount(BigDecimal.ZERO);//over due Check
			builder.append(recoveryDetail.getPaymentAmount());
			builder.append(DELIMITER);
			builder.append(0);
			builder.append(DELIMITER);
			builder.append("0");
			lines.add(builder.toString());
		}

		logger.debug(" Leaving ");
		return lines;
	}

	/**
	 * @param path
	 * @param list
	 * @throws IOException
	 */
	public static void writeFile(Path path, List<String> list) throws IOException {
		logger.debug(" Entering ");

		Files.write(path, list, Charset.forName("UTF-8"));

		logger.debug(" Leaving ");
	}

}
