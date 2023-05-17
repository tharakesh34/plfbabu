package com.pennanttech.external.collectionreceipt.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.external.config.model.FileInterfaceConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.util.ExtSFTPUtil;
import com.pennanttech.external.util.TextFileUtil;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtCollectionFileService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtCollectionFileService.class);

	private static final String REJECTED_RECORDS_HEADER = "REJECTED RECORDS:||||||||||||||||||||||||||||||||||";
	private static final String UNDERLINE_HEADER = "-----------------||||||||||||||||||||||||||||||||||";
	private static final String MAIN_HEADER = "AGREEMENTNO|RECEIPTNO|RECEIPT_CHANNEL|AGENCYID|CHEQUE NO.|DEALINGBANKID|DRAWNON|TOWARDS|RECEIPT AMT|CHEQUEDATE|CITY|RECEIPT DATE|RECEIPT";
	private static final String VALID_RECORDS_HEADER = "REJECTED RECORDS:||||||||||||||||||||||||||||||||||";

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

			int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;
			String qualifiedChk = rejectRowNum + "" + totalChk;

			totalRChecksum = totalRChecksum + Integer.parseInt(qualifiedChk);

			rejectRowNum = rejectRowNum + 1;
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

			int agreementCHK = generateChecksum(String.valueOf(collectionReceiptData.getAgreementNumber()));
			int grTotalCHK = generateChecksum(String.valueOf(collectionReceiptData.getGrandTotal()));
			int chqDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getChequeDate()));
			int receiptDateCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptDate()));
			int chqTypeCHK = generateChecksum(String.valueOf(collectionReceiptData.getReceiptType()));

			int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;
			String qualifiedChk = successRowNum + "" + totalChk;

			totalSChecksum = totalSChecksum + Integer.parseInt(qualifiedChk);

			successRowNum = successRowNum + 1;
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
		FtpClient ftpClient;
		if (respConfig == null) {
			logger.debug("EXT_COLLECTION: CONFIG_COLLECTION_RESP Configuration not found, so returning.");
			return;
		}
		ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(respConfig);
		ftpClient = extSFTPUtil.getSFTPConnection();
		try {
			// Now upload file to SFTP of client location as per configuration
			File mainFile = new File(fileName);
			String remPath = respConfig.getFileSftpLocation();
			ftpClient.upload(mainFile, remPath);
			logger.debug("EXT_COLLECTION:Resp File upload Successful to Destination");
		} catch (Exception e) {
			logger.debug("EXT_COLLECTION:Unable to upload files from local path to destination.", e);
			return;
		}
	}

	private StringBuilder getRejectEndItem(int rejectRowNum, int totalRChecksum) {
		StringBuilder item = new StringBuilder();
		item.append(rejectRowNum);
		item.append(pipeSeperator);
		item.append(rejectRowNum + "" + totalRChecksum);
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		return item;

	}

	private StringBuilder getSuccessItem(ExtCollectionReceiptData detail, Long receiptId, Date appDate,
			int successRowNum, String qualifiedChk) {
		StringBuilder item = new StringBuilder();
		item.append(detail.getAgreementNumber());
		item.append(pipeSeperator);
		item.append(receiptId);
		item.append(pipeSeperator);
		item.append(detail.getReceiptChannel());
		item.append(pipeSeperator);
		item.append(detail.getAgencyId());
		item.append(pipeSeperator);
		item.append(detail.getChequeNumber());
		item.append(pipeSeperator);
		item.append(detail.getDealingBankId());
		item.append(pipeSeperator);
		item.append(detail.getDrawnOn());
		item.append(pipeSeperator);
		item.append(detail.getTowards());
		item.append(pipeSeperator);
		item.append(detail.getGrandTotal());
		item.append(pipeSeperator);
		item.append(detail.getChequeDate());
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append(detail.getReceiptDate());
		item.append(pipeSeperator);
		item.append(detail.getReceiptType());
		item.append(pipeSeperator);
		item.append(detail.getReceiptNumber());
		item.append(pipeSeperator);
		item.append(detail.getChequeStatus());
		item.append(pipeSeperator);
		item.append(detail.getAutoAlloc());
		item.append(pipeSeperator);
		item.append(detail.getEmiAmount());
		item.append(pipeSeperator);
		item.append(detail.getLppAmount());
		item.append(pipeSeperator);
		item.append(detail.getBccAmount());
		item.append(pipeSeperator);
		item.append(detail.getExcessAmount());
		item.append(pipeSeperator);
		item.append(detail.getOthercharge1());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt1());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge2());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt2());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge3());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt3());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge4());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt4());
		item.append(pipeSeperator);
		item.append(detail.getRemarks());
		item.append(pipeSeperator);
		item.append("");// USER ID
		item.append(pipeSeperator);
		item.append(appDate);
		item.append(pipeSeperator);
		item.append("");// Reason
		item.append(pipeSeperator);
		item.append("");// Redepositing flag
		item.append(pipeSeperator);
		item.append(successRowNum);
		item.append(pipeSeperator);
		item.append(qualifiedChk);// Checksum
		return item;
	}

	private StringBuilder getSuccessEndItem(int rows, int chksum) {
		StringBuilder item = new StringBuilder();
		item.append(rows);
		item.append(pipeSeperator);
		item.append(rows + "" + chksum);
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append("");
		return item;
	}

	private StringBuilder getRejectItem(ExtCollectionReceiptData detail, Date appDate, String rejectReason,
			int rejectRowNum, CollReceiptHeader errorReceiptHeader) {
		StringBuilder item = new StringBuilder();
		item.append(detail.getAgreementNumber());
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append(detail.getReceiptChannel());
		item.append(pipeSeperator);
		item.append(detail.getAgencyId());
		item.append(pipeSeperator);
		item.append(detail.getChequeNumber());
		item.append(pipeSeperator);
		item.append(detail.getDealingBankId());
		item.append(pipeSeperator);
		item.append(detail.getDrawnOn());
		item.append(pipeSeperator);
		item.append(detail.getTowards());
		item.append(pipeSeperator);
		item.append(detail.getGrandTotal());
		item.append(pipeSeperator);
		item.append(detail.getChequeDate());
		item.append(pipeSeperator);
		item.append("");
		item.append(pipeSeperator);
		item.append(detail.getReceiptDate());
		item.append(pipeSeperator);
		item.append(detail.getReceiptType());
		item.append(pipeSeperator);
		item.append(detail.getReceiptNumber());
		item.append(pipeSeperator);
		item.append(detail.getChequeStatus());
		item.append(pipeSeperator);
		item.append(detail.getAutoAlloc());
		item.append(pipeSeperator);
		item.append(detail.getEmiAmount());
		item.append(pipeSeperator);
		item.append(detail.getLppAmount());
		item.append(pipeSeperator);
		item.append(detail.getBccAmount());
		item.append(pipeSeperator);
		item.append(detail.getExcessAmount());
		item.append(pipeSeperator);
		item.append(detail.getOthercharge1());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt1());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge2());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt2());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge3());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt3());
		item.append(pipeSeperator);
		item.append(detail.getOtherCharge4());
		item.append(pipeSeperator);
		item.append(detail.getOtherAmt4());
		item.append(pipeSeperator);
		item.append(detail.getRemarks());
		item.append(pipeSeperator);
		item.append("");// USER ID
		item.append(pipeSeperator);
		item.append(appDate);
		item.append(pipeSeperator);

		String reason = "";
		if (!"".equals(StringUtils.stripToEmpty(errorReceiptHeader.getErrorMessage()))) {
			reason = errorReceiptHeader.getErrorMessage();
		} else {
			reason = rejectReason;
		}
		reason = StringUtils.stripToEmpty(reason);
		item.append(reason);// Reason
		item.append(pipeSeperator);
		item.append("");// Redepositing flag
		item.append(pipeSeperator);
		item.append(rejectRowNum);
		item.append(pipeSeperator);

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
}
