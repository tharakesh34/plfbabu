package com.pennanttech.pff.process;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.PushPullControlConstants;
import com.pennanttech.backend.dao.ExtractCustomerDataDAO;
import com.pennanttech.backend.model.external.control.PushPullControl;
import com.pennanttech.external.control.service.PushPullControlService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.model.CustomerStaging;

public class ExtractCustomerData {
	private static Logger logger = LogManager.getLogger(ExtractCustomerData.class);

	int count = 0;

	private ExtractCustomerDataDAO extractCustomerDataDAO;
	private PushPullControlService pushPullControlService;

	public synchronized void processDownloadCustomers() {
		logger.debug(Literal.ENTERING);

		Timestamp curTime = new Timestamp(System.currentTimeMillis());
		Timestamp prevTime = null;

		String processType = "P";

		String name = PushPullControlConstants.PORTAL_CUSTOMER_EXTRACT;
		String type = PushPullControlConstants.TYPE_PUSH;

		PushPullControl pushPullControl = pushPullControlService.getValueByName(name, type);

		if (pushPullControl == null) {
			processType = "F";

			pushPullControl = new PushPullControl();

			pushPullControl.setName(name);
			pushPullControl.setType(type);
			pushPullControl.setStatus("I");
			pushPullControl.setLastRunDate(curTime);

			long id = pushPullControlService.save(pushPullControl);
			pushPullControl.setID(id);
		}

		if ("F".equals(processType)) {
			processExtract(curTime, processType);
			pushPullControl.setStatus("S");
		} else {
			try {
				Date runDate = pushPullControl.getLastRunDate();

				prevTime = new Timestamp(runDate.getTime());

				processFinApprovedCustomer(prevTime, curTime, processType);
				processFinClosedCustomer(prevTime, curTime, processType);

				pushPullControl.setStatus("S");
				pushPullControl.setLastRunDate(curTime);
			} catch (Exception e) {
				pushPullControl.setStatus("F");
			}
		}

		pushPullControlService.update(pushPullControl);

		logger.debug(Literal.LEAVING);
	}

	private void processExtract(Timestamp curTime, String processType) {
		logger.debug(Literal.ENTERING);

		try {
			count = 0;

			long headerId = extractCustomerDataDAO.saveDownloadheader(processType, "F");
			List<Long> extractCustomers = extractCustomerDataDAO.extractCustomers(curTime);

			extractCustomers.forEach(custId -> processCustomer(custId, headerId));

			extractCustomerDataDAO.updateDownloadheader(headerId, count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processFinApprovedCustomer(Timestamp prevTime, Timestamp curTime, String processType) {
		logger.debug(Literal.ENTERING);

		try {
			count = 0;

			long headerId = extractCustomerDataDAO.saveDownloadheader(processType, "A");
			List<Long> customerIDs = extractCustomerDataDAO.getFinApprovedCustomers(prevTime, curTime);

			customerIDs.forEach(custId -> processCustomer(custId, headerId));

			extractCustomerDataDAO.updateDownloadheader(headerId, count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private void processFinClosedCustomer(Timestamp prevTime, Timestamp curTime, String processType) {
		logger.debug(Literal.ENTERING);

		try {
			count = 0;

			long headerId = extractCustomerDataDAO.saveDownloadheader(processType, "C");
			List<Long> closedCustomers = extractCustomerDataDAO.getFinClosedCustomers(prevTime, curTime, processType);

			if (!closedCustomers.isEmpty()) {
				List<Long> activeCustomers = extractCustomerDataDAO.getFinActiveCustomers();

				List<Long> closedData = closedCustomers.stream().filter(item -> !activeCustomers.contains(item))
						.collect(Collectors.toList());

				closedData.forEach(custID -> processCustomer(custID, headerId));
			}

			extractCustomerDataDAO.updateDownloadheader(headerId, count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void processCustomer(long custId, long headerId) {
		logger.debug(Literal.ENTERING);

		CustomerStaging custData = extractCustomerDataDAO.getCustomerDetailsById(custId);
		custData.setHeaderId(headerId);
		extractCustomerDataDAO.setCustAddressDetails(custId, custData);
		extractCustomerDataDAO.setCustPhoneDetails(custId, custData);
		extractCustomerDataDAO.setCustEmailDetails(custId, custData);
		extractCustomerDataDAO.setCustDocDetails(custId, custData);

		extractCustomerDataDAO.saveCustomerStaging(custData);
		count = count + 1;

		logger.debug(Literal.LEAVING);
	}

	public void setExtractCustomerDataDAO(ExtractCustomerDataDAO extractCustomerDataDAO) {
		this.extractCustomerDataDAO = extractCustomerDataDAO;
	}

	public void setPushPullControlService(PushPullControlService pushPullControlService) {
		this.pushPullControlService = pushPullControlService;
	}
}