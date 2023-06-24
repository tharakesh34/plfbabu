package com.pennanttech.external.presentment.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.AmountUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.InterfaceErrorCodeUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ACHService extends TextFileUtil implements InterfaceConstants, ErrorCodesConstants {
	private static final Logger logger = LogManager.getLogger(ACHService.class);

	public static final String ACH_SUCCESS_RECORD = "67";
	public static final String ACH_REJECT_RECORD = "44";
	public static final String ACH_HEADER_LINE_CODE = "56";

	public final SimpleDateFormat ACH_DATEFORMATTER = new SimpleDateFormat("ddMMyyyy");
	public final SimpleDateFormat ACH_HEADER_DATEFORMATTER = new SimpleDateFormat("yyyyMMdd");
	private static final int ach_ccyFromat = 0;

	private ExtPresentmentDAO externalPresentmentDAO;

	public void processACHRequest(FileInterfaceConfig config, List<ExtPresentmentFile> presentmentList, Date dueDate,
			String batchRef, Date appDate) {
		logger.debug(Literal.ENTERING);

		try {
			List<StringBuilder> itemList = new ArrayList<StringBuilder>();

			// HEADER ITEM
			StringBuilder item = new StringBuilder();

			// New presentment list is taken to filter valid records into it
			List<ExtPresentmentFile> presentmentListNew = new ArrayList<ExtPresentmentFile>();

			// DETAILS FOR REQUEST FILE, ITERATE PRESENTMENT LIST AND VALIDATE EACH RECORD
			for (ExtPresentmentFile data : presentmentList) {

				item = new StringBuilder();
				item.append("67");// default
				item.append(StringUtils.rightPad(EMPTY, 9));// 9
				item.append(StringUtils.rightPad(data.getAcType(), 2));// 2
				item.append(StringUtils.rightPad(EMPTY, 3));// 3
				item.append(StringUtils.rightPad(EMPTY, 15));// 15
				item.append(StringUtils.rightPad(data.getCustomerName(), 40));// 40
				item.append(StringUtils.rightPad(EMPTY, 9));// 9
				item.append(StringUtils.rightPad(EMPTY, 7));// 7
				item.append(StringUtils.rightPad("HDFC BANK LIMITED", 20));// 20
				item.append(StringUtils.rightPad(EMPTY, 13));// 13
				item.append(StringUtils
						.rightPad(prependZeros(AmountUtil.convertAmount(data.getSchAmtDue(), ach_ccyFromat), 13), 13));// 13
				item.append(StringUtils.rightPad(EMPTY, 10));// 10
				item.append(StringUtils.rightPad(EMPTY, 10));// 10
				item.append(StringUtils.rightPad(EMPTY, 1));// 1
				item.append(StringUtils.rightPad(EMPTY, 2));// 2
				item.append(StringUtils.rightPad(data.getBankCode(), 11));// 11
				item.append(StringUtils.rightPad(data.getAccountNo(), 35));// 35
				item.append(StringUtils.rightPad("400240015", 11));// 11
				item.append(StringUtils.rightPad("HDFC00017000001103", 18));// 18
				item.append(StringUtils.rightPad(String.valueOf(data.getId()), 30));// 30
				item.append(StringUtils.rightPad("10", 3));// 3 (Product type is default)
				item.append(StringUtils.rightPad(EMPTY, 15));// 15
				item.append(StringUtils.rightPad(data.getUtrNumber(), 20));// 20
				item.append(StringUtils.rightPad(EMPTY, 7));// 7
				itemList.add(item);
				presentmentListNew.add(data);

			}

			// ACH Header line starts here
			if (itemList.size() > 0) {

				BigDecimal totalAmount = presentmentListNew.stream().map(ExtPresentmentFile::getSchAmtDue)
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				item = new StringBuilder();
				item.append(ACH_HEADER_LINE_CODE);// 2 , default
				item.append(StringUtils.rightPad(EMPTY, 7));// 7
				item.append(StringUtils.rightPad("HDFC BANK LIMITED", 40));// 40
				item.append(StringUtils.rightPad(EMPTY, 14));// 14
				item.append(StringUtils.rightPad(EMPTY, 9));// 9
				item.append(StringUtils.rightPad(EMPTY, 9));// 9
				item.append(StringUtils.rightPad(EMPTY, 15));// 15
				item.append(StringUtils.rightPad(EMPTY, 3));// 3
				item.append(StringUtils.rightPad("0005000000000", 13));// 13
				item.append(StringUtils.rightPad(prependZeros(AmountUtil.convertAmount(totalAmount, ach_ccyFromat), 13),
						13));// 13
				item.append(StringUtils.rightPad(ACH_DATEFORMATTER.format(dueDate), 8));// 8
				item.append(StringUtils.rightPad(EMPTY, 10));// 10
				item.append(StringUtils.rightPad(EMPTY, 10));// 10
				item.append(StringUtils.rightPad(EMPTY, 3));// 3
				item.append(StringUtils.rightPad("HDFC00017000001103", 18));// 18
				// item.append(StringUtils.rightPad(ach_headerDateFormatter.format(dueDate) + "-" + batchRef, 18));// 18
				item.append(StringUtils.rightPad(batchRef, 18));// 18
				item.append(StringUtils.rightPad("400240015", 11));// 11
				item.append(StringUtils.rightPad("02402970000393", 35));// 35
				item.append(StringUtils.rightPad("" + presentmentListNew.size(), 9));// 9
				item.append(StringUtils.rightPad(EMPTY, 2));
				item.append(StringUtils.rightPad(EMPTY, 57));
				itemList.add(0, item); // Add at index 0, because this is header item
			}

			if (!itemList.isEmpty()) {

				StringBuilder emptyLine = new StringBuilder();
				itemList.add(emptyLine);

				long fileSeq = externalPresentmentDAO.getSeqNumber(SEQ_PRMNT_ACH);
				String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 6, "0");

				String baseFilePath = App.getResourcePath(config.getFileLocation());
				logger.debug(Literal.LEAVING);
				String fileName = baseFilePath + File.separator + config.getFilePrepend()
						+ new SimpleDateFormat(config.getDateFormat()).format(appDate) + config.getFilePostpend()
						+ fileSeqName + config.getFileExtension();

				super.writeDataToFile(fileName, itemList);

				if ("Y".equals(StringUtils.stripToEmpty(config.getFileTransfer()))) {
					FileTransferUtil fileTransferUtil = new FileTransferUtil(config);
					fileTransferUtil.uploadToSFTP(baseFilePath, new File(fileName).getName());
				}
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);

		}
		logger.debug(Literal.LEAVING);
	}

	public ExtPresentmentFile prepareResponseObject(FileInterfaceConfig config, String eachRecord) {
		logger.debug(Literal.ENTERING);

		ExtPresentmentFile presentment = new ExtPresentmentFile();

		int i = 0;
		int j = 0;

		i = 0;
		j = i + 2;
		String recordStatus = getData(eachRecord, i, j);

		// Expecting record status as Success/Bounced
		if (recordStatus != null && !"".equals(recordStatus) && (config.getSuccessIndicator().equals(recordStatus)
				|| config.getFailIndicator().equals(recordStatus))) {

			String recordFlag = getData(eachRecord, 153, 154);

			if (config.getSuccessIndicator().equals(recordStatus)) {
				if ("1".equals(recordFlag)) {
					presentment.setStatus(SUCCESS);
				} else {
					presentment.setErrorCode(PR1015);
					presentment.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1015));
				}
			}
			if (config.getFailIndicator().equals(recordStatus)) {
				if ("0".equals(recordFlag)) {
					presentment.setStatus(FAIL);
				} else {
					presentment.setErrorCode(PR1016);
					presentment.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1016));
				}

			}

			i = 154;
			j = i + 2;
			String reasonCode = getData(eachRecord, i, j);
			presentment.setBounceRetrunCode(reasonCode);

			i = 231;
			j = i + 30;
			String presentmentId = eachRecord.subSequence(i, j).toString().trim();
			presentment.setTxnReference(getLongValue(presentmentId));

		} else {
			presentment.setErrorCode(PR1017);
			presentment.setErrorMessage(InterfaceErrorCodeUtil.getErrorMessage(PR1017));
		}
		logger.debug(Literal.LEAVING);
		return presentment;
	}

	private static Long getLongValue(String strLong) {
		try {
			return Long.parseLong(strLong);
		} catch (Exception e) {
			return (long) -1;
		}
	}

	private String getData(String eachRecord, int startIndex, int endIntex) {
		if (eachRecord.toString().length() >= endIntex) {
			return eachRecord.toString().subSequence(startIndex, endIntex).toString();
		}
		return "";
	}

	public String prependZeros(String temp, int length) {
		int count = length - temp.length();
		for (int i = 0; i < count; i++) {
			temp = 0 + temp;
		}
		return temp;
	}

	public void setExternalPresentmentDAO(ExtPresentmentDAO externalPresentmentDAO) {
		this.externalPresentmentDAO = externalPresentmentDAO;
	}

}
