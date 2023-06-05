package com.pennanttech.external.silien.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienFileWritingService extends TextFileUtil
		implements InterfaceConstants, ErrorCodesConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(LienFileWritingService.class);

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private FileInterfaceConfig lienConfig;

	public void processSILienMarkingRequest(Date appDate) {
		logger.debug(Literal.ENTERING);
		try {

			// Fetch lien config from main configuration
			lienConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_LIEN_REQ);

			if (lienConfig == null) {
				logger.debug(
						"Ext_Warning: No configuration found for type LIEN. So returning without generating the request file.");
				return;
			}

			// Fetch records where INTERFACESTATUS = PENDING (Not written to request file)
			List<LienMarkDetail> lienMarkingList = externalLienMarkingDAO.fetchRecordsForLienFileWriting(LIEN_PENDING);

			if (lienConfig.getNoOfRecords() > 0) {
				int fileCount = 0;
				int requiredRecordCount = (int) lienConfig.getNoOfRecords();
				if (!lienMarkingList.isEmpty()) {
					int listSize = lienMarkingList.size();
					fileCount = (int) Math.ceil((double) listSize / requiredRecordCount);

					int frmIndx = 0;
					int toIndx = requiredRecordCount;
					for (int i = 0; i < fileCount; i++) {
						writeDataToFile(lienMarkingList.subList(frmIndx, toIndx), appDate);
						frmIndx = toIndx;
						toIndx = toIndx + (requiredRecordCount - 1);
					}
				}
			}

		} catch (Exception e) {
			logger.debug("Exception caught {}" + e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void writeDataToFile(List<LienMarkDetail> lienMarkingList, Date appDate) throws Exception {
		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		if (!lienMarkingList.isEmpty()) {

			// Header line for item data
			itemList.add(new StringBuilder("Installment Loan"));

			// Prepare record item data
			for (LienMarkDetail lienMarkDetail : lienMarkingList) {
				String errCode = "";
				try {
					// Validate Account number in the record
					if (lienMarkDetail.getAccNumber() == null || "".equals(lienMarkDetail.getAccNumber())) {
						logger.debug("EXT_SILIEN:Account number received is empty/null, So not Processing");
						continue;
					}

					// Validate Lien mark in the record
					if ("".equals(StringUtils.stripToEmpty(lienMarkDetail.getLienMark()))) {
						logger.debug("EXT_SILIEN:Lien Status received is empty/null, So not Processing");
						continue;
					}

					StringBuilder item = new StringBuilder();
					item.append(StringUtils.rightPad(lienMarkDetail.getAccNumber(), 14));
					item.append(StringUtils.rightPad(" ", 6));
					item.append(lienMarkDetail.getLienMark());
					// Add item to list
					itemList.add(item);

					// update record status as in-process
					lienMarkDetail.setInterfaceStatus(LIEN_AWAITING_CONF);
					externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail);

				} catch (Exception e) {
					logger.debug("Exception caught {}" + e);
					// update record status as in-process
					errCode = F605;
					lienMarkDetail.setInterfaceStatus(LIEN_FAILED);
					lienMarkDetail.setErrCode(errCode);
					lienMarkDetail.setErrMsg(e.getMessage());

					String errorDesc = StringUtils.trimToEmpty(e.getMessage());
					if (errorDesc.length() > 255) {
						errorDesc = errorDesc.substring(0, 255);
					}

					lienMarkDetail.setInterfaceReason(errorDesc);
					externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail);
				}
			}
		}

		// Now iterate items list and write data to the request file
		if (itemList.size() > 0) {

			long fileSeq = externalLienMarkingDAO.getSeqNumber(SEQ_SILIEN);
			String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 6, "0");
			String baseFilePath = App.getResourcePath(lienConfig.getFileLocation());

			String fileName = baseFilePath + File.separator + lienConfig.getFilePrepend()
					+ new SimpleDateFormat(lienConfig.getDateFormat()).format(appDate) + fileSeqName
					+ lienConfig.getFileExtension();

			super.writeDataToFile(fileName, itemList);

		}
	}

	public void setExternalLienMarkingDAO(ExtLienMarkingDAO externalLienMarkingDAO) {
		this.externalLienMarkingDAO = externalLienMarkingDAO;
	}
}
