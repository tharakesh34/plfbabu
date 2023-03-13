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

public class ExtUcicRequestFile extends TextFileUtil implements InterfaceConstants, ExtUciccomparator {
	private static final Logger logger = LogManager.getLogger(ExtUcicRequestFile.class);
	private ExtUcicDao extUcicDao;
	private ExtInterfaceDao extInterfaceDao;
	private ExternalConfig ucicReqConfig;
	private ExternalConfig ucicReqCompleteConfig;

	private static final SimpleDateFormat DOB_SDF = new SimpleDateFormat("dd-MMM-yyyy");
	private static final SimpleDateFormat UPDATE_DATE_SDF = new SimpleDateFormat("DDMMMYYYY000000");

	private static final String MODIFIED_CUST = "M";
	private static final String NEW_CUST = "A";

	public void processUcicRequestFile(Date appDate) throws Exception {
		logger.debug(Literal.ENTERING);
		// get error codes handy
		if (ExtErrorCodes.getInstance().getInterfaceErrorsList().isEmpty()) {
			List<InterfaceErrorCode> interfaceErrorsList = extInterfaceDao.fetchInterfaceErrorCodes();
			ExtErrorCodes.getInstance().setInterfaceErrorsList(interfaceErrorsList);
		}

		// Get main configuration for External Interfaces
		List<ExternalConfig> mainConfig = extInterfaceDao.getExternalConfig();
		// Fetch lien config from main configuration
		ucicReqConfig = getDataFromList(mainConfig, CONFIG_UCIC_REQ);
		ucicReqCompleteConfig = getDataFromList(mainConfig, CONFIG_UCIC_REQ_COMPLETE);

		if (ucicReqConfig == null || ucicReqCompleteConfig == null) {
			logger.debug(
					"Ext_Warning: No configuration found for type UCIC request file generation. So returning without generating the request file.");
			return;
		}

		// Fetch records where FILE_STATUS = 0 and UNPROCESSED (Not written to request file)
		List<ExtUcicCust> custList = extUcicDao.fetchCustomersForCoreBankId(FILE_NOT_WRITTEN, UNPROCESSED);

		List<StringBuilder> itemList = new ArrayList<StringBuilder>();

		long newLoansCount = 0;
		long modifiedLoansCount = 0;
		long totalLoansCount = 0;
		if (!custList.isEmpty()) {
			for (ExtUcicCust customer : custList) {
				totalLoansCount = totalLoansCount + 1;
				if (NEW_CUST.equalsIgnoreCase(customer.getInsertUpdateFlag())) {
					newLoansCount = newLoansCount + 1;
				}
				if (MODIFIED_CUST.equalsIgnoreCase(customer.getInsertUpdateFlag())) {
					modifiedLoansCount = modifiedLoansCount + 1;
				}
				extUcicDao.updateRecordProcessingFlagAndFileStatus(customer, COMPLETED, COMPLETED);
			}

			StringBuilder header = new StringBuilder();
			header.append("HDR|" + totalLoansCount + "|A|" + newLoansCount + "|M|" + modifiedLoansCount);
			itemList.add(header);

			StringBuilder firstRow = new StringBuilder();
			firstRow.append(
					"UCIC|Source system|Unique id|Customer Category|Customer Type|Customer Type Desc|Customer Full Name|DOB|Address1 Type|Address1|Address1 City|Address1 State|Address1 Pincode|EMail 1|EMail 2|EMail 3|Mobile No1|Mobile No2|Mobile No3|Land Phone1|Land Phone2|Land Phone3|PAN No|Aadhar Number|Mothers Maiden Name|Creation Date|Updation date|Blocked Flag|Blocked Code and Desc|Blocked eff. date|Insert Update Flag|Address2 Type|Address2|Address2 City|Address2 State|Address2 Pincode|Address3 Type|Address3|Address3 City|Address3 State|Address3 Pincode|Employer Name|Voter ID|Passport No|Drining Lic No|Casa AccountNo|Loan Number");
			itemList.add(firstRow);

			for (ExtUcicCust customer : custList) {
				itemList.add(getItemRecord(customer));
			}

			StringBuilder footer = new StringBuilder();
			footer.append("EOF");
			itemList.add(footer);
		}

		// Now iterate items list and write data to the request file
		if (itemList.size() > 0) {

			String baseFilePath = App.getResourcePath(ucicReqConfig.getFileLocation());

			String fileName = baseFilePath + File.separator + ucicReqConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicReqConfig.getDateFormat()).format(appDate)
					+ ucicReqConfig.getFileExtension();

			super.writeDataToFile(fileName, itemList);

			baseFilePath = App.getResourcePath(ucicReqCompleteConfig.getFileLocation());
			String completeFileName = baseFilePath + File.separator + ucicReqCompleteConfig.getFilePrepend()
					+ new SimpleDateFormat(ucicReqCompleteConfig.getDateFormat()).format(appDate)
					+ ucicReqCompleteConfig.getFileExtension();

			List<StringBuilder> emptyList = new ArrayList<StringBuilder>();
			emptyList.add(new StringBuilder(""));

			super.writeDataToFile(completeFileName, emptyList);
		}
		logger.debug(Literal.LEAVING);
	}

	private StringBuilder getItemRecord(ExtUcicCust customer) {
		StringBuilder sb = new StringBuilder();
		sb.append(getEmptyString(customer.getCustCoreBank()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getSourceSystem()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString("" + customer.getCustId()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(getCustType(customer.getCustCtgCode())));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getCustomertype()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getSubCategory()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getCustShrtName()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(getDateFormatString(customer.getCustDob(), DOB_SDF)));
		sb.append(pipeSeperator);

		sb.append(customer.getAddr1Type());
		sb.append(pipeSeperator);
		String addrs = "";
		if (!"".equals(customer.getAddr1Line1())) {
			addrs = addrs + customer.getAddr1Line1();
		}
		if (!"".equals(customer.getAddr1Line2())) {
			addrs = addrs + "," + customer.getAddr1Line2();
		}
		if (!"".equals(customer.getAddr1Line3())) {
			addrs = addrs + "," + customer.getAddr1Line3();
		}
		if (!"".equals(customer.getAddr1Line4())) {
			addrs = addrs + "," + customer.getAddr1Line4();
		}
		sb.append(addrs);
		sb.append(pipeSeperator);
		sb.append(customer.getAddr1City());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr1State());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr1Pin());
		sb.append(pipeSeperator);

		sb.append(getEmptyString(customer.getEmail1()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getEmail2()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getEmail3()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getMobile1()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getMobile2()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getMobile3()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getLandLine1()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getLandLine2()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getLandLine3()));
		sb.append(pipeSeperator);

		sb.append(getEmptyString(customer.getPan()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getAadhaar()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getCustMotherMaiden()));
		sb.append(pipeSeperator);
		sb.append("");// creation date
		sb.append(pipeSeperator);
		sb.append(getEmptyString(getDateFormatString(customer.getLastMntOn(), UPDATE_DATE_SDF)));
		sb.append(pipeSeperator);

		sb.append(getEmptyString(customer.getClosingStatus()));
		sb.append(pipeSeperator);

		sb.append("");// blocked/closed desc
		sb.append(pipeSeperator);

		sb.append(getEmptyString("" + customer.getCloseDate()));
		sb.append(pipeSeperator);

		sb.append(getEmptyString("" + customer.getCloseDate()));
		sb.append(pipeSeperator);

		sb.append(getEmptyString(customer.getInsertUpdateFlag()));
		sb.append(pipeSeperator);

		sb.append(customer.getAddr2Type());
		sb.append(pipeSeperator);
		String addrs2 = "";
		if (!"".equals(customer.getAddr2Line1())) {
			addrs2 = addrs2 + customer.getAddr2Line1();
		}
		if (!"".equals(customer.getAddr2Line2())) {
			addrs2 = addrs2 + "," + customer.getAddr2Line2();
		}
		if (!"".equals(customer.getAddr2Line3())) {
			addrs2 = addrs2 + "," + customer.getAddr2Line3();
		}
		if (!"".equals(customer.getAddr2Line4())) {
			addrs2 = addrs2 + "," + customer.getAddr2Line4();
		}
		sb.append(addrs2);
		sb.append(pipeSeperator);
		sb.append(customer.getAddr2City());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr2State());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr2Pin());
		sb.append(pipeSeperator);

		sb.append(customer.getAddr3Type());
		sb.append(pipeSeperator);
		String addrs3 = "";
		if (!"".equals(customer.getAddr3Line1())) {
			addrs3 = addrs3 + customer.getAddr1Line1();
		}
		if (!"".equals(customer.getAddr3Line2())) {
			addrs3 = addrs3 + "," + customer.getAddr1Line2();
		}
		if (!"".equals(customer.getAddr3Line3())) {
			addrs3 = addrs3 + "," + customer.getAddr1Line3();
		}
		if (!"".equals(customer.getAddr3Line4())) {
			addrs3 = addrs3 + "," + customer.getAddr1Line4();
		}
		sb.append(addrs3);
		sb.append(pipeSeperator);
		sb.append(customer.getAddr3City());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr3State());
		sb.append(pipeSeperator);
		sb.append(customer.getAddr3Pin());
		sb.append(pipeSeperator);

		sb.append(getEmptyString(customer.getCompanyName()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getVoterId()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getPassport()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getDrivingLicence()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getAccNumber()));
		sb.append(pipeSeperator);
		sb.append(getEmptyString(customer.getFinreference()));

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
		return StringUtils.stripToEmpty(str);
	}

	private String getCustType(String custCategory) {
		if (custCategory != null && "RETAIL".equalsIgnoreCase(custCategory)) {
			return "I";
		}
		return "C";
	}

	public void setExtUcicDao(ExtUcicDao extUcicDao) {
		this.extUcicDao = extUcicDao;
	}

	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
