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
import com.pennanttech.external.app.util.ExtSFTPUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.gst.dao.ExtGSTDao;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ftp.FtpClient;
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
			item.append(data.getRequestType());
			item.append(pipeSeperator);
			item.append(data.getCustomerId());
			item.append(pipeSeperator);
			item.append(data.getAccountId());
			item.append(pipeSeperator);
			item.append(data.getGstin());
			item.append(pipeSeperator);
			item.append(data.getServiceCode());
			item.append(pipeSeperator);
			item.append(data.getHsn());
			item.append(pipeSeperator);
			item.append(data.getTransactionCode());
			item.append(pipeSeperator);
			item.append(data.getTransactionVolume());
			item.append(pipeSeperator);
			item.append(data.getTransactionValue());
			item.append(pipeSeperator);
			item.append(data.getTransactionPricedCharge());
			item.append(pipeSeperator);
			item.append(data.getChargeInclusiveOfTax());
			item.append(pipeSeperator);
			item.append(data.getTaxAmount());
			item.append(pipeSeperator);
			item.append(data.getTransactionDate());
			item.append(pipeSeperator);
			item.append(data.getTransactionUid());
			item.append(pipeSeperator);
			item.append(data.getSourceBranch());
			item.append(pipeSeperator);
			item.append(data.getSourceState());
			item.append(pipeSeperator);
			item.append(data.getDestinationBranch());
			item.append(pipeSeperator);
			item.append(data.getDestinationState());
			item.append(pipeSeperator);
			item.append(data.getCurrencyCode());
			item.append(pipeSeperator);
			item.append(data.getChannel());
			item.append(pipeSeperator);
			item.append(data.getSourceSystem());
			item.append(pipeSeperator);
			item.append(data.getCustomerExempt());
			item.append(pipeSeperator);
			item.append(data.getAccountExempt());
			item.append(pipeSeperator);
			item.append(data.getBranchExempt());
			item.append(pipeSeperator);
			item.append(data.getServiceChargeExempt());
			item.append(pipeSeperator);
			item.append(data.getTransactionExempt());
			item.append(pipeSeperator);
			item.append(data.getRelatedEntity());
			item.append(pipeSeperator);
			item.append(data.getStandardFee());
			item.append(pipeSeperator);
			item.append(data.getReversalTransactionFlag());
			item.append(pipeSeperator);
			item.append(data.getOriginalTransactionId());
			item.append(pipeSeperator);
			item.append(data.getTaxSplitInsourceSystem());
			item.append(pipeSeperator);
			item.append(data.getCgstValue());
			item.append(pipeSeperator);
			item.append(data.getSgstValue());
			item.append(pipeSeperator);
			item.append(data.getIgstValue());
			item.append(pipeSeperator);
			item.append(data.getUtGstValue());
			item.append(pipeSeperator);
			item.append(data.getCgstSgstUtgstState());
			item.append(pipeSeperator);
			item.append(data.getGlAccountId());
			item.append(pipeSeperator);
			item.append(data.getGstInvoiceNumber());
			item.append(pipeSeperator);
			item.append(data.getUsercField1());
			item.append(pipeSeperator);
			item.append(data.getUsercField2());
			item.append(pipeSeperator);
			item.append(data.getUsercField3());
			item.append(pipeSeperator);
			item.append(data.getUsercField4());
			item.append(pipeSeperator);
			item.append(data.getUsercField5());
			item.append(pipeSeperator);
			item.append(data.getUsernField1());
			item.append(pipeSeperator);
			item.append(data.getUsernField2());
			item.append(pipeSeperator);
			item.append(data.getUserdField1());
			item.append(pipeSeperator);
			item.append(data.getUserdField2());
			item.append(pipeSeperator);
			item.append(data.getActualUserId());
			item.append(pipeSeperator);
			item.append(data.getUserDepartment());
			item.append(pipeSeperator);
			item.append(data.getRequestDate());
			item.append(pipeSeperator);
			item.append(data.getRevenueAccountNumber());
			item.append(pipeSeperator);
			item.append(data.getIgstAccountNumber());
			item.append(pipeSeperator);
			item.append(data.getCgstAccountNumber());
			item.append(pipeSeperator);
			item.append(data.getSgstAccountNumber());
			item.append(pipeSeperator);
			item.append(data.getUtgstAccountNumber());
			item.append(pipeSeperator);
			item.append(data.getCgstCess1());
			item.append(pipeSeperator);
			item.append(data.getCgstCess2());
			item.append(pipeSeperator);
			item.append(data.getCgstCess3());
			item.append(pipeSeperator);
			item.append(data.getSgstCess1());
			item.append(pipeSeperator);
			item.append(data.getSgstCess2());
			item.append(pipeSeperator);
			item.append(data.getSgstCess3());
			item.append(pipeSeperator);
			item.append(data.getIgstCess1());
			item.append(pipeSeperator);
			item.append(data.getIgstCess2());
			item.append(pipeSeperator);
			item.append(data.getIgstCess3());
			item.append(pipeSeperator);
			item.append(data.getUtgstCess1());
			item.append(pipeSeperator);
			item.append(data.getUtgstCess2());
			item.append(pipeSeperator);
			item.append(data.getUtgstCess3());
			item.append(pipeSeperator);
			item.append(data.getGstRate());
			item.append(pipeSeperator);
			item.append(data.getSgstRate());
			item.append(pipeSeperator);
			item.append(data.getCgstRate());
			item.append(pipeSeperator);
			item.append(data.getIgstRate());
			item.append(pipeSeperator);
			item.append(data.getUtgstRate());
			item.append(pipeSeperator);
			item.append(data.getCustomerName());
			item.append(pipeSeperator);
			item.append(data.getCessApplicable());
			item.append(pipeSeperator);
			item.append(data.getBusinessUnit());
			item.append(pipeSeperator);
			item.append(data.getGstrField1());
			item.append(pipeSeperator);
			item.append(data.getGstrField2());
			item.append(pipeSeperator);
			item.append(data.getGstrField3());
			item.append(pipeSeperator);
			item.append(data.getGstrField4());
			item.append(pipeSeperator);
			item.append(data.getGstrField5());
			item.append(pipeSeperator);
			item.append(data.getGstrField6());
			item.append(pipeSeperator);
			item.append(data.getGstrField7());
			item.append(pipeSeperator);
			item.append(data.getGstrField8());
			item.append(pipeSeperator);
			item.append(data.getGstrField9());
			item.append(pipeSeperator);
			item.append(data.getGstrField10());
			item.append(pipeSeperator);
			item.append(data.getGstrField11());
			item.append(pipeSeperator);
			item.append(data.getGstrField12());
			item.append(pipeSeperator);
			item.append(data.getGstrField13());
			item.append(pipeSeperator);
			item.append(data.getGstrField14());
			item.append(pipeSeperator);
			item.append(data.getGstrField15());
			item.append(pipeSeperator);
			item.append(data.getUsercField6());
			item.append(pipeSeperator);
			item.append(data.getUsercField7());
			item.append(pipeSeperator);
			item.append(data.getUsercField8());
			item.append(pipeSeperator);
			item.append(data.getUsercField9());
			item.append(pipeSeperator);
			item.append(data.getUsercField10());
			item.append(pipeSeperator);
			item.append(data.getUsercField11());
			item.append(pipeSeperator);
			item.append(data.getUsercField12());
			item.append(pipeSeperator);
			item.append(data.getUsercField13());
			item.append(pipeSeperator);
			item.append(data.getUsercField14());
			item.append(pipeSeperator);
			item.append(data.getUsercField15());
			item.append(pipeSeperator);
			item.append(data.getUsernField3());
			item.append(pipeSeperator);
			item.append(data.getUsernField4());
			item.append(pipeSeperator);
			item.append(data.getUserdField3());
			item.append(pipeSeperator);
			item.append(data.getUserdField4());
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

	private String writeDoneFile(FileInterfaceConfig doneConfig, String fileName) throws Exception {
		String completeFileName = fileName + doneConfig.getFilePostpend();
		List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
		emptyList.add(new StringBuilder(""));
		super.writeDataToFile(completeFileName, emptyList);
		logger.debug(Literal.LEAVING);
		return completeFileName;
	}

	private void uploadToClientLocation(FileInterfaceConfig reqConfig, String fileName, String baseFilePath,
			String CompleteFileName) {
		ExtSFTPUtil extSFTPUtil = new ExtSFTPUtil(reqConfig);
		FtpClient ftpClient = extSFTPUtil.getSFTPConnection();
		try {
			// Now upload file to SFTP of client
			File mainFile = new File(baseFilePath + File.separator + fileName);
			ftpClient.upload(mainFile, reqConfig.getFileSftpLocation());
			logger.debug("Ext_GST:ReqFile upload Successful to Destination");

			// Now upload done file to SFTP of client location as per configuration
			ftpClient = extSFTPUtil.getSFTPConnection();
			File completeFileToUpload = new File(CompleteFileName);
			ftpClient.upload(completeFileToUpload, reqConfig.getFileSftpLocation());
			logger.debug("Ext_GST:Completefile upload Sucessful to Destination");
			fileBackup(reqConfig, mainFile, completeFileToUpload);
		} catch (Exception e) {
			logger.debug("Ext_GST:Unable to upload files from local path to destination.", e);
			return;
		}
	}

	private void fileBackup(FileInterfaceConfig serverConfig, File mainFile, File completeFileToUpload)
			throws IOException {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = serverConfig.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("Ext_GST: Local backup location not configured, so returning.");
			return;
		}
		String localBackupLocation = App.getResourcePath(serverConfig.getFileLocalBackupLocation());
		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());
		File completeFileBkp = new File(localBackupLocation + File.separator + completeFileToUpload.getName());
		Files.copy(mainFile, mainFileBkp);
		Files.copy(completeFileToUpload, completeFileBkp);
		logger.debug("Ext_GST: MainFile & Completefile backup Successful");
		logger.debug(Literal.LEAVING);
	}
}
