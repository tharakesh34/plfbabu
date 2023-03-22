package com.pennanttech.extrenal.ucic.service;

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
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtUcicCust;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtUcicWeekFileService extends TextFileUtil implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtUcicWeekFileService.class);

	private static final SimpleDateFormat CUST_DOB_SDF = new SimpleDateFormat("DD-MMM-YYYY");
	private static final SimpleDateFormat CUST_CREATION_DATE_SDF = new SimpleDateFormat("DDMMMYYYYHH24MMSS");
	private static final SimpleDateFormat CUST_UPDATION_DATE_SDF = new SimpleDateFormat("DDMMMYYYYHH24MMSS");

	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;

	public void processWeeklyFileRequest(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();
		// Fetch lien config from main configuration
		ExternalConfig ucicWeeklyConfig = getDataFromList(mainConfig, CONFIG_UCIC_WEEKLY_FILE);

		if (ucicWeeklyConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC Weekly request file. So returning without generating the request file.");
			return;
		}

		// Fetch records where FILE_STATUS = 2 (Written to EOD request file)
		List<ExtUcicCust> custList = extUcicDao.fetchCustomersForWeeklyFile(COMPLETED);

		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		long totalLoansCount = 0;
		if (!custList.isEmpty()) {
			for (ExtUcicCust customer : custList) {

				if (customer.getCustCtgCode() != null) {
					if ("RETAIL".equalsIgnoreCase(customer.getCustCtgCode())) {
						customer.setCustomertype("I");
					} else {
						customer.setCustomertype("C");
					}
				}

				if ("I".equals(customer.getCustCtgCode())) {
					customer.setCustCtgCode("INDIVIDUAL");
				} else {
					customer.setCustCtgCode("CORPORATE");
				}

				totalLoansCount = totalLoansCount + 1;
				itemList.add(getItemRecord(customer));
			}
		}

		// Now iterate items list and write data to the request file
		if (itemList.size() > 0) {
			StringBuilder header = new StringBuilder();
			header.append("HDR|" + totalLoansCount);
			itemList.add(0, header);

			StringBuilder firstRow = new StringBuilder();
			firstRow.append(
					"Source system~Unique id  - Customer ID~Blocked code Flag~UCIC~PAN No~UID (Aadhaar Number)~Customer Category (Class) (individual / corporate)~Customer Type (minor senior citizen Y Z)~Date of Birth~Customer Full Name~Mobile #~Email ID~Record Creation Date~Last record Push Date(From Source)");
			itemList.add(1, firstRow);

			StringBuilder footer = new StringBuilder();
			footer.append("EOF");
			itemList.add(footer);

			String baseFilePath = App.getResourcePath(ucicWeeklyConfig.getFileLocation());

			String fileName = baseFilePath + File.separator + ucicWeeklyConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicWeeklyConfig.getDateFormat()).format(appDate)
					+ ucicWeeklyConfig.getFileExtension();

			super.writeDataToFile(fileName, itemList);
		}
		logger.debug(Literal.LEAVING);
	}

	private StringBuilder getItemRecord(ExtUcicCust customer) {
		StringBuilder sb = new StringBuilder();

		sb.append(getEmptyString(customer.getSourceSystem()));
		sb.append(fileSeperator);
		sb.append(getEmptyString("" + customer.getCustId()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getCustIsBlocked()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getCustCoreBank()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getPan()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getAadhaar()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getCustCtgCode()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getCustomertype()));
		sb.append(fileSeperator);
		sb.append(getDateFormatString(customer.getCustDob(), CUST_DOB_SDF));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getCustShrtName()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getMobile1()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(customer.getEmail1()));
		sb.append(fileSeperator);
		sb.append(getEmptyString(getDateFormatString(customer.getLastMntOn(), CUST_CREATION_DATE_SDF)));
		sb.append(fileSeperator);
		sb.append(getEmptyString(getDateFormatString(customer.getLastMntOn(), CUST_UPDATION_DATE_SDF)));

		return sb;
	}

	private String getDateFormatString(Date dateStr, SimpleDateFormat sdf) {
		if (dateStr != null) {
			try {
				return sdf.format(dateStr);
			} catch (Exception e) {

			}
		}
		return "";
	}

	private String getEmptyString(String str) {
		return "\"" + StringUtils.stripToEmpty(str) + "\"";
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
