package com.pennanttech.extrenal.ucic.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.config.ExternalConfig;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.dao.ExtInterfaceDao;
import com.pennanttech.external.fileutil.TextFileUtil;
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtUcicData;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicResponseAckFileWriter extends TextFileUtil implements InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtUcicResponseAckFileWriter.class);

	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;

	public void processUcicResponseAckFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();

		writeAcknowledgeFile(mainConfig, appDate);
		logger.debug(Literal.LEAVING);
	}

	private void writeAcknowledgeFile(List<ExternalConfig> mainConfig, Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);

		ExternalConfig ucicAckConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK);
		ExternalConfig ucicAckConfConfig = getDataFromList(mainConfig, CONFIG_UCIC_ACK_CONF);

		if (ucicAckConfig == null || ucicAckConfConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC ack file. So returning without generating the ack file.");
			return;
		}

		List<ExtUcicData> ackRecordList = extUcicDao.fetchListOfAckRecords(COMPLETED, ACK_PENDING);
		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		StringBuilder header = new StringBuilder();
		header.append("HDR|" + ackRecordList.size() + "|1");
		itemList.add(header);

		StringBuilder firstRow = new StringBuilder();
		firstRow.append("CUSTOMER ID |UCIC|STATUS|REASON REJECTION");
		itemList.add(firstRow);

		if (!ackRecordList.isEmpty()) {
			for (ExtUcicData ucicData : ackRecordList) {
				itemList.add(getItemRecord(ucicData));
			}
		}

		StringBuilder footer = new StringBuilder();
		footer.append("EOF");
		itemList.add(footer);

		if (itemList.size() > 0) {

			String baseFilePath = App.getResourcePath(ucicAckConfig.getFileLocation());

			String fileName = baseFilePath + File.separator + ucicAckConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicAckConfig.getDateFormat()).format(appDate)
					+ StringUtils.stripToEmpty(ucicAckConfig.getFilePostpend()) + ucicAckConfig.getFileExtension();

			super.writeDataToFile(fileName, itemList);

			baseFilePath = App.getResourcePath(ucicAckConfConfig.getFileLocation());
			String completeFileName = baseFilePath + File.separator + ucicAckConfConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicAckConfConfig.getDateFormat()).format(appDate)
					+ StringUtils.stripToEmpty(ucicAckConfConfig.getFilePostpend())
					+ ucicAckConfConfig.getFileExtension();

			List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
			emptyList.add(new StringBuilder(""));
			extUcicDao.updateAckFileRecordsStatus(ackRecordList);
			super.writeDataToFile(completeFileName, emptyList);
		}

		logger.debug(Literal.LEAVING);
	}

	private StringBuilder getItemRecord(ExtUcicData ucicData) {
		StringBuilder sb = new StringBuilder();
		sb.append(getEmptyString(ucicData.getCustId()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(ucicData.getUcicId()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(getStatusString(ucicData.getProcessStatus())));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(ucicData.getProcessDesc()));

		return sb;
	}

	private String getStatusString(int processStatus) {
		if (processStatus == UCIC_UPDATE_SUCCESS) {
			return "SUCCESS";
		}
		return "REJECT";
	}

	private String getEmptyString(String str) {
		return StringUtils.stripToEmpty(str);
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
