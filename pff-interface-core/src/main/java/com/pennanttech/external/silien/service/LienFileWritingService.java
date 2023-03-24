package com.pennanttech.external.silien.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.config.ExtErrorCodes;
import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.config.InterfaceErrorCode;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class LienFileWritingService extends TextFileUtil implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(LienFileWritingService.class);

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private ExtInterfaceDao extInterfaceDao;
	private ExternalConfig lienConfig;

	public void processSILienMarkingRequest(Date appDate) {
		logger.debug(Literal.ENTERING);
		try {

			// get error codes handy
			if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
				List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
				ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
			}

			// Get main configuration for External Interfaces
			List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

			// Fetch lien config from main configuration
			lienConfig = getDataFromList(mainConfig, CONFIG_LIEN_REQ);

			if (lienConfig == null) {
				logger.debug(
						"Ext_Warning: No configuration found for type LIEN. So returning without generating the request file.");
				return;
			}

			// Fetch records where FILE_STATUS = 0 (Not written to request file)
			List<LienMarkDetail> lienMarkingList = externalLienMarkingDAO
					.fetchRecordsForLienFileWriting(FILE_NOT_WRITTEN);

			List<StringBuilder> itemList = new ArrayList<StringBuilder>();

			if (!lienMarkingList.isEmpty()) {

				// Header line for item data
				itemList.add(new StringBuilder("Installment Loan"));

				// Prepare record item data
				for (LienMarkDetail lienMarkDetail : lienMarkingList) {
					try {
						// update record status as in-process
						externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), INPROCESS, "", "");

						// Validate Account number in the record
						if (lienMarkDetail.getAccNumber() == null || "".equals(lienMarkDetail.getAccNumber())) {
							InterfaceErrorCode interfaceErrorCode = getErrorFromList(
									ExtErrorCodes.getInstance().getInterfaceErrorsList(), F600);
							externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), COMPLETED,
									interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
							continue;
						}

						// Validate Lien mark in the record
						if (lienMarkDetail.getLienMark() == null || "".equals(lienMarkDetail.getLienMark())) {
							InterfaceErrorCode interfaceErrorCode = getErrorFromList(
									ExtErrorCodes.getInstance().getInterfaceErrorsList(), F601);
							externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), COMPLETED,
									interfaceErrorCode.getErrorCode(), interfaceErrorCode.getErrorMessage());
							continue;
						}

						StringBuilder item = new StringBuilder();
						item.append(StringUtils.rightPad(lienMarkDetail.getAccNumber(), 14));
						item.append(StringUtils.rightPad(" ", 6));
						item.append(lienMarkDetail.getLienMark());
						// Add item to list
						itemList.add(item);

						// update record status as success
						externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), COMPLETED, "", "");

					} catch (Exception e) {
						logger.debug("Exception caught {}" + e);
						// update record status as in-process
						InterfaceErrorCode interfaceErrorCode = getErrorFromList(
								ExtErrorCodes.getInstance().getInterfaceErrorsList(), F605);
						externalLienMarkingDAO.updateLienRecordStatus(lienMarkDetail.getAccNumber(), UNPROCESSED,
								interfaceErrorCode.getErrorCode(), e.getMessage());
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
		} catch (Exception e) {
			logger.debug("Exception caught {}" + e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setExternalLienMarkingDAO(ExtLienMarkingDAO externalLienMarkingDAO) {
		this.externalLienMarkingDAO = externalLienMarkingDAO;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
