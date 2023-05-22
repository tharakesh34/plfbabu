package com.pennant.webservice.services;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.RequestDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.externalinput.ExtFinanceData;
import com.pennant.externalinput.service.ExtFinanceUploadService;
import com.pennant.webservice.schema.ExtFinanceDetails;
import com.pennant.webservice.schema.PFSLoanCreationRequest;
import com.pennant.webservice.schema.PFSLoanCreationResponse;
import com.pennant.webservice.schema.StatusType;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * @author pennant
 * 
 */
public class ExtFinanceService {
	private Logger logger = LogManager.getLogger(ExtFinanceService.class);
	private ExtFinanceUploadService extFinanceUploadService;

	PFSLoanCreationResponse response;

	/**
	 * Processes the request and returns the response model for the specified operation
	 * 
	 * @param request
	 * @param header
	 * @param operation
	 * @return
	 * @throws UPPException
	 */
	public Object processRequest(Object request, RequestDetail header) throws Exception {
		logger.debug("Entering");

		Object result = null;

		try {
			// rqUID = ((PFSLoanCreationRequest) request).getRqUID();
			logger.debug("Making the process Call");
			logger.debug("Generting Response");
			result = processReq(request);
		} catch (Exception e) {
			logger.error("Exception: ", e);

			throw e;
		}

		logger.debug("Leaving");
		return result;
	}

	/**
	 * Prepares response model for the specified program call document and operation
	 * 
	 * @param document
	 * @param rqUID
	 * @param operation
	 * @return
	 * @throws UPPException
	 */
	private Object processReq(Object request) throws Exception {
		logger.debug("Entering");

		ExtFinanceDetails extFinanceDetails = null;
		ExtFinanceData extFinanceData = new ExtFinanceData();
		StatusType statusType = new StatusType();
		PFSLoanCreationResponse result = new PFSLoanCreationResponse();
		LoggedInUser userDetails = new LoggedInUser();
		userDetails.setLoginUsrID(1000);
		userDetails.setUsrLanguage("EN");
		try {

			extFinanceDetails = ((PFSLoanCreationRequest) request).getExtFinanceDetails();
			extFinanceData.setFinType(extFinanceDetails.getFinType());
			extFinanceData.setFinReference(extFinanceDetails.getFinReference());
			extFinanceData.setFinCcy(extFinanceDetails.getFinCcy());
			// extFinanceData.setAllowGrcCpz(extFinanceDetails.get(i).isAllowGrcCpz());
			// extFinanceData.setAllowGrcPeriod(extFinanceDetails.get(i).isAllowGrcPeriod());
			extFinanceData.setRepayRateBasis(extFinanceDetails.getRepayRateBasis());
			extFinanceData.setScheduleMethod(extFinanceDetails.getScheduleMethod());
			extFinanceData.setGrcSchdMthd(extFinanceDetails.getGrcSchdMthd());
			extFinanceData.setProfitDaysBasis(extFinanceDetails.getProfitDaysBasis());
			extFinanceData.setLovDescCustCIF(extFinanceDetails.getLovDescCustCIF());
			extFinanceData.setFinStartDate(convertFromXMLTime(extFinanceDetails.getFinStartDate()));
			extFinanceData.setFinAmount(extFinanceDetails.getFinAmount());
			extFinanceData.setDpToBank(extFinanceDetails.getDpToBank());
			extFinanceData.setDpToSupplier(extFinanceDetails.getDpToSupplier());
			extFinanceData.setExpGracePft(extFinanceDetails.getExpGracePft());
			extFinanceData.setExpGraceCpz(extFinanceDetails.getExpGraceCpz());
			extFinanceData.setExpGorssGracePft(extFinanceDetails.getExpGorssGracePft());
			extFinanceData.setExpRepayPft(extFinanceDetails.getExpRepayPft());
			extFinanceData.setExpTotalPft(extFinanceDetails.getExpTotalPft());
			extFinanceData.setExpFirstInst(extFinanceDetails.getExpFirstInst());
			extFinanceData.setExpLastInst(extFinanceDetails.getExpLastInst());
			extFinanceData.setExpLastInstPft(extFinanceDetails.getExpLastInstPft());
			extFinanceData.setExpRateAtStart(extFinanceDetails.getExpRateAtStart());
			extFinanceData.setExpRateAtGrcEnd(extFinanceDetails.getExpRateAtGrcEnd());
			extFinanceData.setDownPayment(extFinanceData.getDpToBank().add(extFinanceData.getDpToSupplier()));
			extFinanceData.setFinBranch("1010");

			// Status
			extFinanceData.setRecordStatus("");
			extFinanceData.setErrDesc("");
			extFinanceData = extFinanceUploadService.validateExtFinanceDatafromWebservice(extFinanceData,
					new FinanceMain());

			if ("E".equals(extFinanceData.getRecordStatus())) {
				// TODO
				logger.fatal("Need to Decide");
				statusType.setStatusCode("9999");
				statusType.setSeverity("Error");
				statusType.setStatusDesc(extFinanceData.getErrDesc());
			} else {
				// Save Finance Data into DataBase
				extFinanceUploadService.processExtFinanceData(userDetails, extFinanceData);
				statusType.setStatusCode("0000");
				statusType.setSeverity("Info");
				statusType.setStatusDesc("Success");
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			statusType.setStatusCode("9999");
			statusType.setSeverity("Error");
			throw new Exception("9002");
		}
		result.setExtFinanceDetails(extFinanceDetails);
		result.setStatus(statusType);
		setResponse(result);
		logger.debug("Leaving");
		return result;
	}

	private java.util.Date convertFromXMLTime(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar == null) {
			return null;
		}

		return new java.util.Date(xmlCalendar.toGregorianCalendar().getTimeInMillis());
	}

	public PFSLoanCreationResponse getResponse() {
		return response;
	}

	public void setResponse(PFSLoanCreationResponse response) {
		this.response = response;
	}

	public ExtFinanceUploadService getExtFinanceUploadService() {
		return extFinanceUploadService;
	}

	public void setExtFinanceUploadService(ExtFinanceUploadService extFinanceUploadService) {
		this.extFinanceUploadService = extFinanceUploadService;
	}

}
