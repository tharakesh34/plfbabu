package com.pennanttech.external.collectionreceipt.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.icu.text.SimpleDateFormat;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionFileService extends TextFileUtil implements InterfaceConstants, CollectionReceiptDataSplit {
	private static final Logger logger = LogManager.getLogger(ExtCollectionFileService.class);

	private static final String REJECTED_RECORDS_HEADER = "REJECTED RECORDS:||||||||||||||||||||||||||||||||||";
	private static final String UNDERLINE_HEADER = "-----------------||||||||||||||||||||||||||||||||||";
	private static final String MAIN_HEADER = "AGREEMENTNO|RECEIPTNO|RECEIPT_CHANNEL|AGENCYID|CHEQUE NO.|DEALINGBANKID|DRAWNON|TOWARDS|RECEIPT AMT|CHEQUEDATE|CITY|RECEIPT DATE|RECEIPT";
	private static final String VALID_RECORDS_HEADER = "VALID RECORDS:||||||||||||||||||||||||||||||||||";

	public void processCollectionResponseFileWriting(String fileName, Date appDate,
			List<CollReceiptDetail> fileRecordsList, CollReceiptHeader errorReceiptHeader,
			FileInterfaceConfig respConfig) {
		logger.debug(Literal.ENTERING);

		if (fileRecordsList == null || fileRecordsList.isEmpty()) {
			return;
		}

		List<CollReceiptDetail> successList = new ArrayList<>();
		List<CollReceiptDetail> failedList = new ArrayList<>();

		for (CollReceiptDetail collectionDetail : fileRecordsList) {
			if (collectionDetail.getReceiptId() > 0) {
				successList.add(collectionDetail);
			} else {
				failedList.add(collectionDetail);
			}
		}

		writeReponseFile(successList, failedList, fileName, appDate, respConfig, errorReceiptHeader);

		logger.debug(Literal.LEAVING);
	}

	private void writeReponseFile(List<CollReceiptDetail> successList, List<CollReceiptDetail> failedList,
			String fileName, Date appDate, FileInterfaceConfig respConfig, CollReceiptHeader errorReceiptHeader) {
		logger.debug(Literal.ENTERING);

		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		StringBuilder firstRow = new StringBuilder();
		firstRow.append(REJECTED_RECORDS_HEADER);
		itemList.add(firstRow);

		StringBuilder lineRow = new StringBuilder();
		lineRow.append(UNDERLINE_HEADER);
		itemList.add(lineRow);

		StringBuilder headingRow = new StringBuilder();
		headingRow.append(MAIN_HEADER);
		itemList.add(headingRow);

		int rejectRowNum = 0;
		int totalRChecksum = 0;
		for (CollReceiptDetail rejectDetail : failedList) {
			ExtCollectionReceiptData collectionReceiptData = splitAndSetData(rejectDetail.getRecordData());

			int agreementCHK = generateChecksum(String.valueOf(collectionReceiptData.getAgreementNumber()));
			int grTotalCHK = generateChecksum(String.valueOf(collectionReceiptData.getGrandTotal()));
			int chqDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getChequeDate()));
			int receiptDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptDate()));
			int chqTypeCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptType()));
			rejectRowNum = rejectRowNum + 1;
			int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;
			String qualifiedChk = rejectRowNum + "" + totalChk;

			totalRChecksum = totalRChecksum + Integer.parseInt(qualifiedChk);

			StringBuilder itemStr = getRejectItem(collectionReceiptData, appDate, rejectDetail.getErrorMessage(),
					rejectRowNum, errorReceiptHeader);
			itemList.add(itemStr);
		}

		if (rejectRowNum > 0) {
			StringBuilder itemLast = getRejectEndItem(rejectRowNum, totalRChecksum);
			itemList.add(itemLast);
		}

		StringBuilder validRow = new StringBuilder();
		validRow.append(VALID_RECORDS_HEADER);
		itemList.add(validRow);

		StringBuilder validLineRow = new StringBuilder();
		validLineRow.append(UNDERLINE_HEADER);
		itemList.add(validLineRow);

		headingRow = new StringBuilder();
		headingRow.append(MAIN_HEADER);
		itemList.add(headingRow);

		int successRowNum = 0;
		int totalSChecksum = 0;
		for (CollReceiptDetail successDetail : successList) {
			ExtCollectionReceiptData collectionReceiptData = splitAndSetData(successDetail.getRecordData());
			successRowNum = successRowNum + 1;
			int agreementCHK = generateChecksum(String.valueOf(collectionReceiptData.getAgreementNumber()));
			int grTotalCHK = generateChecksum(String.valueOf(collectionReceiptData.getGrandTotal()));
			int chqDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getChequeDate()));
			int receiptDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptDate()));
			int chqTypeCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptType()));

			int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;
			String qualifiedChk = successRowNum + "" + totalChk;

			totalSChecksum = totalSChecksum + Integer.parseInt(qualifiedChk);

			StringBuilder itemStr = getSuccessItem(collectionReceiptData, successDetail.getReceiptId(),
					successDetail.getReceiptCreatedDate(), successRowNum, qualifiedChk);
			itemList.add(itemStr);

		}

		if (successRowNum > 0) {
			StringBuilder itemLast = getSuccessEndItem(successRowNum, totalSChecksum);
			itemList.add(itemLast);
		}

		if (itemList.size() > 0) {
			try {
				super.writeDataToFile(fileName, itemList);
				// Uploading to HDFC SFTP
				if ("Y".equals(respConfig.getIsSftp())) {
					uploadToClientLocation(appDate, respConfig, fileName, respConfig.getFileLocation());
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void uploadToClientLocation(Date appDate, FileInterfaceConfig respConfig, String fileName,
			String baseFilePath) {
		if (respConfig == null) {
			logger.debug("EXT_COLLECTION: CONFIG_COLLECTION_RESP Configuration not found, so returning.");
			return;
		}
		FileTransferUtil fileTransferUtil = new FileTransferUtil(respConfig);
		try {
			// Now upload file to SFTP of client location as per configuration
			fileTransferUtil.uploadToSFTP(baseFilePath, fileName);
			logger.debug("EXT_COLLECTION:Resp File upload Successful to Destination");
		} catch (Exception e) {
			logger.debug("EXT_COLLECTION:Unable to upload files from local path to destination.", e);
			return;
		}
	}

	private StringBuilder getRejectEndItem(int rejectRowNum, int totalRChecksum) {
		StringBuilder item = new StringBuilder();
		append(item, rejectRowNum);
		appendSeperator(item, rejectRowNum + "" + totalRChecksum);
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		return item;

	}

	private StringBuilder getSuccessItem(ExtCollectionReceiptData detail, Long receiptId, Date appDate,
			int successRowNum, String qualifiedChk) {
		StringBuilder item = new StringBuilder();
		append(item, detail.getAgreementNumber());
		appendSeperator(item, receiptId);
		appendSeperator(item, detail.getReceiptChannel());
		appendSeperator(item, detail.getAgencyId());
		appendSeperator(item, detail.getChequeNumber());
		appendSeperator(item, detail.getDealingBankId());
		appendSeperator(item, detail.getDrawnOn());
		appendSeperator(item, detail.getTowards());
		appendSeperator(item, detail.getGrandTotal());
		appendSeperator(item, detail.getChequeDate());
		appendSeperator(item, "");
		appendSeperator(item, detail.getReceiptDate());
		appendSeperator(item, detail.getReceiptType());
		appendSeperator(item, detail.getReceiptNumber());
		appendSeperator(item, detail.getChequeStatus());
		appendSeperator(item, detail.getAutoAlloc());
		appendSeperator(item, detail.getEmiAmount());
		appendSeperator(item, detail.getLppAmount());
		appendSeperator(item, detail.getBccAmount());
		appendSeperator(item, detail.getExcessAmount());
		appendSeperator(item, detail.getOthercharge1());
		appendSeperator(item, detail.getOtherAmt1());
		appendSeperator(item, detail.getOtherCharge2());
		appendSeperator(item, detail.getOtherAmt2());
		appendSeperator(item, detail.getOtherCharge3());
		appendSeperator(item, detail.getOtherAmt3());
		appendSeperator(item, detail.getOtherCharge4());
		appendSeperator(item, detail.getOtherAmt4());
		appendSeperator(item, detail.getRemarks());
		appendSeperator(item, "1000");// USING Admin UserId
		appendSeperator(item, new SimpleDateFormat("dd-MMM-yy").format(appDate));
		appendSeperator(item, "");// Reason
		appendSeperator(item, "");// Redepositing flag
		appendSeperator(item, successRowNum);
		appendSeperator(item, qualifiedChk);// Checksum
		return item;
	}

	private StringBuilder getSuccessEndItem(int rows, int chksum) {
		StringBuilder item = new StringBuilder();
		append(item, rows);
		appendSeperator(item, rows + "" + chksum);
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		appendSeperator(item, "");
		return item;
	}

	private StringBuilder getRejectItem(ExtCollectionReceiptData detail, Date appDate, String rejectReason,
			int rejectRowNum, CollReceiptHeader errorReceiptHeader) {
		StringBuilder item = new StringBuilder();
		append(item, detail.getAgreementNumber());
		appendSeperator(item, "");
		appendSeperator(item, detail.getReceiptChannel());
		appendSeperator(item, detail.getAgencyId());
		appendSeperator(item, detail.getChequeNumber());
		appendSeperator(item, detail.getDealingBankId());
		appendSeperator(item, detail.getDrawnOn());
		appendSeperator(item, detail.getTowards());
		appendSeperator(item, detail.getGrandTotal());
		appendSeperator(item, detail.getChequeDate());
		appendSeperator(item, "");
		appendSeperator(item, detail.getReceiptDate());
		appendSeperator(item, detail.getReceiptType());
		appendSeperator(item, detail.getReceiptNumber());
		appendSeperator(item, detail.getChequeStatus());
		appendSeperator(item, detail.getAutoAlloc());
		appendSeperator(item, detail.getEmiAmount());
		appendSeperator(item, detail.getLppAmount());
		appendSeperator(item, detail.getBccAmount());
		appendSeperator(item, detail.getExcessAmount());
		appendSeperator(item, detail.getOthercharge1());
		appendSeperator(item, detail.getOtherAmt1());
		appendSeperator(item, detail.getOtherCharge2());
		appendSeperator(item, detail.getOtherAmt2());
		appendSeperator(item, detail.getOtherCharge3());
		appendSeperator(item, detail.getOtherAmt3());
		appendSeperator(item, detail.getOtherCharge4());
		appendSeperator(item, detail.getOtherAmt4());
		appendSeperator(item, detail.getRemarks());
		appendSeperator(item, "1000");// System Admin Id
		appendSeperator(item, new SimpleDateFormat("dd-MMM-yy").format(appDate));

		String reason = "";
		if (!"".equals(StringUtils.stripToEmpty(errorReceiptHeader.getErrorMessage()))) {
			reason = errorReceiptHeader.getErrorMessage();
		} else {
			reason = rejectReason;
		}
		reason = StringUtils.stripToEmpty(reason);
		appendSeperator(item, reason);// Reason
		appendSeperator(item, "");// Redepositing flag
		appendSeperator(item, rejectRowNum);

		int agreementCHK = generateChecksum(String.valueOf(detail.getAgreementNumber()));
		int grTotalCHK = generateChecksum(String.valueOf(detail.getGrandTotal()));
		int chqDateCHK = generateChecksum(String.valueOf(detail.getChequeDate()));
		int receiptDateCHK = generateChecksum(String.valueOf(detail.getReceiptDate()));
		int chqTypeCHK = generateChecksum(String.valueOf(detail.getReceiptType()));

		int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;
		String qualifiedChk = rejectRowNum + "" + totalChk;

		item.append(qualifiedChk);// Checksum
		return item;
	}

	private void append(StringBuilder item, Object data) {
		item.append(data);
	}

	private void appendSeperator(StringBuilder item, Object data) {
		item.append(pipeSeperator);
		item.append(data);

	}

	private int generateChecksum(String data) {
		int rcdCS = 0;
		for (int i = 0; i < data.length(); i++) {
			char digit = data.charAt(i);
			int asciiCode = (int) digit;
			rcdCS = rcdCS + asciiCode;
		}
		return rcdCS;
	}
}
