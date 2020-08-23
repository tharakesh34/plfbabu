package com.pennant.eod;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.ext.ExtractDataDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtractDataExecution {

	private static final Logger logger = Logger.getLogger(ExtractDataExecution.class);

	private ExtractDataDAO extractDataDAO;

	public void processExtractCustomerDetails(Timestamp currentTime) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	public void processExtractMasterDetails(Timestamp currentTime) {
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
	}

	public ExtractDataDAO getExtractDataDAO() {
		return extractDataDAO;
	}

	public void setExtractDataDAO(ExtractDataDAO extractDataDAO) {
		this.extractDataDAO = extractDataDAO;
	}
}
