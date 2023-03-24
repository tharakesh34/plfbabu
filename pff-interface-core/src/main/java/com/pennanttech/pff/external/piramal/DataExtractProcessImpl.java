package com.pennanttech.pff.external.piramal;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DataExportProcess;

public class DataExtractProcessImpl implements DataExportProcess {
	private static final Logger logger = LogManager.getLogger(DataExtractProcessImpl.class);

	private ExtractDataDAO extractDataDAO;

	public void export(String exportType) {

		if ("CUSROMER".equals(exportType)) {
			exportCustomerData();
		} else if ("MASTER".equals(exportType)) {
			exportMasterData();
		}
	}

	private void exportCustomerData() {
		//current date starting from midnight
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
		if (ImplementationConstants.IS_DATA_SYNC_REQ_BY_APP_DATE) {
			//getting last business date, since app date is updated to next business day(ex: EOD on 1-1-2020 then Appdate is updated as 2-1-2020)
			currentTime = getTimestamp(SysParamUtil.getLastBusinessdate());
		}
		logger.debug("START: Customer Data preparation : " + currentTime);
		try {
			boolean flag = false;
			//extract customers table data to plfext.customers
			flag = extractDataDAO.extractDetails(currentTime, Customer.class, "customers");
			if (flag) {
				flag = false;
				//extract customerPhonenumbers table data to plfext.customerPhonenumbers
				flag = extractDataDAO.extractDetails(currentTime, CustomerPhoneNumber.class, "customerPhonenumbers");
			}
			if (flag) {
				flag = false;
				//extract customerEmails table data to plfext.customerEmails
				flag = extractDataDAO.extractDetails(currentTime, CustomerEMail.class, "customerEmails");
			}
			if (flag) {
				//extract customerAddresses table data to plfext.customerAddresses
				extractDataDAO.extractDetails(currentTime, CustomerAddres.class, "customerAddresses");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + " While executing customer data extraction");
		}
		logger.debug("COMPLETE: Customer Data preparation :" + currentTime);
	}

	private void exportMasterData() {
		//current date starting from midnight
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
		if (ImplementationConstants.IS_DATA_SYNC_REQ_BY_APP_DATE) {
			//getting last business date, since app date is updated to next business day(ex: EOD on 1-1-2020 then Appdate is updated as 2-1-2020)
			currentTime = getTimestamp(SysParamUtil.getLastBusinessdate());
		}
		logger.debug("START: Master Data preparation : " + currentTime);
		try {

			logger.debug(Literal.ENTERING);
			boolean flag = false;
			//extract buildergroup table data to plfext.buildergroup
			flag = extractDataDAO.extractDetails(currentTime, BuilderGroup.class, "buildergroup");
			if (flag) {
				flag = false;
				//extract buildercompany table data to plfext.buildercompany
				flag = extractDataDAO.extractDetails(currentTime, BuilderCompany.class, "buildercompany");
			}
			if (flag) {
				flag = false;
				//extract builderprojcet table data to plfext.builderprojcet
				flag = extractDataDAO.extractDetails(currentTime, BuilderProjcet.class, "builderprojcet");
			}
			if (flag) {
				//extract projectunits table data to plfext.projectunits
				extractDataDAO.extractDetails(currentTime, ProjectUnits.class, "projectunits");
			}
			logger.debug(Literal.LEAVING);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + " While executing master data extraction");
		}
		logger.debug("COMPLETE: Master Data preparation :" + currentTime);

	}

	private static Timestamp getTimestamp(java.util.Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

}
