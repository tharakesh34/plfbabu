package com.pennanttech.external.gst.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.gst.dao.ExtGSTDao;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtGSTService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtGSTService.class);

	private ExtGSTDao extGSTDao;

	public void processRequestFile(FileInterfaceConfig reqConfig, FileInterfaceConfig doneConfig, Date appDate) {

		// Dump GST Vouchers to create unique reference for each entry
		extGSTDao.extractDetailsFromFinFeeDetail();
		extGSTDao.extractDetailsFromManualadvise();

		// Dump data as per the request file format
		extGSTDao.saveExtractedDetailsToRequestTable();

		// Write the file
		List<GSTRequestDetail> gstComputationRecords = extGSTDao.fetchRecords(UNPROCESSED);
		if (gstComputationRecords != null && !gstComputationRecords.isEmpty()) {
			writeRequestFile(gstComputationRecords, reqConfig, doneConfig, appDate);
		}

	}

	public void writeRequestFile(List<GSTRequestDetail> gstRequestDetailList, FileInterfaceConfig reqConfig,
			FileInterfaceConfig doneConfig, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		StringBuilder item = new StringBuilder();
		// DETAIL ITEM FOR REQUEST FILE
		for (GSTRequestDetail data : gstRequestDetailList) {
			append(item, data.getRequestType());
			appendSeperator(item, data.getCustomerId());
			appendSeperator(item, data.getAccountId());
			appendSeperator(item, data.getGstin());
			appendSeperator(item, data.getServiceCode());
			appendSeperator(item, data.getHsn());
			appendSeperator(item, data.getTransactionCode());
			appendSeperator(item, data.getTransactionVolume());
			appendSeperator(item, data.getTransactionValue());
			appendSeperator(item, data.getTransactionPricedCharge());
			appendSeperator(item, data.getChargeInclusiveOfTax());
			appendSeperator(item, data.getTaxAmount());
			appendSeperator(item, data.getTransactionDate());
			appendSeperator(item, data.getTransactionUid());
			appendSeperator(item, data.getSourceBranch());
			appendSeperator(item, data.getSourceState());
			appendSeperator(item, data.getDestinationBranch());
			appendSeperator(item, data.getDestinationState());
			appendSeperator(item, data.getCurrencyCode());
			appendSeperator(item, data.getChannel());
			appendSeperator(item, data.getSourceSystem());
			appendSeperator(item, data.getCustomerExempt());
			appendSeperator(item, data.getAccountExempt());
			appendSeperator(item, data.getBranchExempt());
			appendSeperator(item, data.getServiceChargeExempt());
			appendSeperator(item, data.getTransactionExempt());
			appendSeperator(item, data.getRelatedEntity());
			appendSeperator(item, data.getStandardFee());
			appendSeperator(item, data.getReversalTransactionFlag());
			appendSeperator(item, data.getOriginalTransactionId());
			appendSeperator(item, data.getTaxSplitInsourceSystem());
			appendSeperator(item, data.getCgstValue());
			appendSeperator(item, data.getSgstValue());
			appendSeperator(item, data.getIgstValue());
			appendSeperator(item, data.getUtGstValue());
			appendSeperator(item, data.getCgstSgstUtgstState());
			appendSeperator(item, data.getGlAccountId());
			appendSeperator(item, data.getGstInvoiceNumber());
			appendSeperator(item, data.getUsercField1());
			appendSeperator(item, data.getUsercField2());
			appendSeperator(item, data.getUsercField3());
			appendSeperator(item, data.getUsercField4());
			appendSeperator(item, data.getUsercField5());
			appendSeperator(item, data.getUsernField1());
			appendSeperator(item, data.getUsernField2());
			appendSeperator(item, data.getUserdField1());
			appendSeperator(item, data.getUserdField2());
			appendSeperator(item, data.getActualUserId());
			appendSeperator(item, data.getUserDepartment());
			appendSeperator(item, data.getRequestDate());
			appendSeperator(item, data.getRevenueAccountNumber());
			appendSeperator(item, data.getIgstAccountNumber());
			appendSeperator(item, data.getCgstAccountNumber());
			appendSeperator(item, data.getSgstAccountNumber());
			appendSeperator(item, data.getUtgstAccountNumber());
			appendSeperator(item, data.getCgstCess1());
			appendSeperator(item, data.getCgstCess2());
			appendSeperator(item, data.getCgstCess3());
			appendSeperator(item, data.getSgstCess1());
			appendSeperator(item, data.getSgstCess2());
			appendSeperator(item, data.getSgstCess3());
			appendSeperator(item, data.getIgstCess1());
			appendSeperator(item, data.getIgstCess2());
			appendSeperator(item, data.getIgstCess3());
			appendSeperator(item, data.getUtgstCess1());
			appendSeperator(item, data.getUtgstCess2());
			appendSeperator(item, data.getUtgstCess3());
			appendSeperator(item, data.getGstRate());
			appendSeperator(item, data.getSgstRate());
			appendSeperator(item, data.getCgstRate());
			appendSeperator(item, data.getIgstRate());
			appendSeperator(item, data.getUtgstRate());
			appendSeperator(item, data.getCustomerName());
			appendSeperator(item, data.getCessApplicable());
			appendSeperator(item, data.getBusinessUnit());
			appendSeperator(item, data.getGstrField1());
			appendSeperator(item, data.getGstrField2());
			appendSeperator(item, data.getGstrField3());
			appendSeperator(item, data.getGstrField4());
			appendSeperator(item, data.getGstrField5());
			appendSeperator(item, data.getGstrField6());
			appendSeperator(item, data.getGstrField7());
			appendSeperator(item, data.getGstrField8());
			appendSeperator(item, data.getGstrField9());
			appendSeperator(item, data.getGstrField10());
			appendSeperator(item, data.getGstrField11());
			appendSeperator(item, data.getGstrField12());
			appendSeperator(item, data.getGstrField13());
			appendSeperator(item, data.getGstrField14());
			appendSeperator(item, data.getGstrField15());
			appendSeperator(item, data.getUsercField6());
			appendSeperator(item, data.getUsercField7());
			appendSeperator(item, data.getUsercField8());
			appendSeperator(item, data.getUsercField9());
			appendSeperator(item, data.getUsercField10());
			appendSeperator(item, data.getUsercField11());
			appendSeperator(item, data.getUsercField12());
			appendSeperator(item, data.getUsercField13());
			appendSeperator(item, data.getUsercField14());
			appendSeperator(item, data.getUsercField15());
			appendSeperator(item, data.getUsernField3());
			appendSeperator(item, data.getUsernField4());
			appendSeperator(item, data.getUserdField3());
			appendSeperator(item, data.getUserdField4());
			itemList.add(item);
		}

		if (itemList.size() > 0) {
			try {
				StringBuilder footer = new StringBuilder();
				footer.append("EOF");
				footer.append(pipeSeperator);
				footer.append(itemList.size());
				itemList.add(footer);
				long fileSeq = extGSTDao.getSeqNumber(SEQ_GST_INTF);
				String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 5, "0");
				String baseFilePath = reqConfig.getFileLocation();
				String fileName = baseFilePath + File.separator + reqConfig.getFilePrepend()
						+ new SimpleDateFormat(reqConfig.getDateFormat()).format(appDate) + fileSeqName
						+ reqConfig.getFileExtension();
				super.writeDataToFile(fileName, itemList);

				String doneFile = writeDoneFile(doneConfig, fileName);

				// Save Request file details and get headerId
				long req_header_id = extGSTDao.saveGSTRequestFileData(fileName, reqConfig.getFileLocation());
				// Get all gst voucher id's as a list
				List<Long> txnUidList = new ArrayList<Long>();
				for (GSTRequestDetail data : gstRequestDetailList) {
					txnUidList.add(data.getTransactionUid());
				}
				// Update gst vouchers with request header id
				extGSTDao.updateGSTVoucherWithReqHeaderId(txnUidList, req_header_id);

				// Uploading to HDFC SFTP
				if ("Y".equals(reqConfig.getIsSftp())) {
					uploadToClientLocation(reqConfig, new File(fileName).getName(), baseFilePath, doneFile);
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void append(StringBuilder item, Object data) {
		item.append(data);
	}

	private void appendSeperator(StringBuilder item, Object data) {
		item.append(pipeSeperator);
		item.append(data);

	}

	private String writeDoneFile(FileInterfaceConfig doneConfig, String fileName) throws Exception {
		String completeFileName = fileName + doneConfig.getFilePostpend();
		List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
		emptyList.add(new StringBuilder(""));
		super.writeDataToFile(completeFileName, emptyList);
		logger.debug(Literal.LEAVING);
		return completeFileName;
	}

	private void uploadToClientLocation(FileInterfaceConfig reqConfig, String fileName, String baseFilePath,
			String completeFileName) {
		FileTransferUtil fileTransferUtil = new FileTransferUtil(reqConfig);
		try {
			// Now upload file to SFTP of client
			fileTransferUtil.uploadToSFTP(baseFilePath, fileName);
			logger.debug("Ext_GST:ReqFile upload Successful to Destination");

			// Now upload done file to SFTP of client location as per configuration
			fileTransferUtil.uploadToSFTP(baseFilePath, completeFileName);
			logger.debug("Ext_GST:Completefile upload Sucessful to Destination");
			fileBackup(reqConfig, fileName, completeFileName);
		} catch (Exception e) {
			logger.debug("Ext_GST:Unable to upload files from local path to destination.", e);
			return;
		}
	}

	private void fileBackup(FileInterfaceConfig serverConfig, String mainFilePath, String completeFilePath)
			throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = serverConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("Ext_GST: Local backup location not configured, so returning.");
			return;
		}
		File mainFile = new File(mainFilePath);
		File completeFileToUpload = new File(completeFilePath);

		String localBackupLocation = App.getResourcePath(serverConfig.getFileLocalBackupLocation());
		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		File completeFileBkp = new File(localBackupLocation + File.separator + completeFileToUpload.getName());
		Files.copy(mainFile, mainFileBkp);
		Files.copy(completeFileToUpload, completeFileBkp);
		logger.debug("Ext_GST: MainFile & Completefile backup Successful");
		logger.debug(Literal.LEAVING);
	}
}
