package com.pennanttech.niyogin.communication.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Sms;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.SMSService;
import com.pennanttech.pff.external.service.NiyoginService;

public class SMSServiceImpl extends NiyoginService implements SMSService {
	private static final Logger	logger	= Logger.getLogger(SMSServiceImpl.class);

	private JSONClient			client;
	private String				serviceUrl;

	/**
	 * Method to send the sms for the given mobile numbers.
	 * 
	 * @param custPhoneNos
	 * @param smsContent
	 * 
	 */
	@Override
	public void sendSms(List<MailTemplate> smsList, String finReference) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (smsList != null && !smsList.isEmpty()) {
			for (MailTemplate mailTemplate : smsList) {
				List<String> listnumbers = mailTemplate.getLovDescMobileNumbers();
				if (listnumbers != null && !listnumbers.isEmpty()) {
					for (String string : listnumbers) {
						send(string, mailTemplate.getLovDescSMSContent(), finReference);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for send the SMS.
	 * 
	 * @param mobileNo
	 * @param content
	 */
	private void send(String mobileNo, String content, String finReference) {
		logger.debug(Literal.ENTERING);
		Sms smsRequest = prepareRequest(mobileNo, content);
		// logging fields Data
		// logging fields Data
		String errorCode = null;
		String errorDesc = null;
		String reuestString = null;
		String jsonResponse = null;
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			reuestString = client.getRequestString(smsRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			doInterfaceLogging(finReference, reuestString, jsonResponse, errorCode, errorDesc, reqSentOn);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			errorDesc = getWriteException(e);
			errorDesc = getTrimmedMessage(errorDesc);
			doExceptioLogging(finReference, reuestString, jsonResponse, errorDesc, reqSentOn);
			throw new InterfaceException("9999", e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for prepare the SMS request object.
	 * 
	 * @param mobileNo
	 * @param content
	 * @return sms
	 */
	private Sms prepareRequest(String mobileNo, String content) {
		logger.debug(Literal.ENTERING);
		Sms sms = new Sms();
		sms.setMobileNumber(mobileNo);
		sms.setMessageBody(content);
		sms.setReturnCode(null);
		sms.setReturnText(null);
		logger.debug(Literal.LEAVING);
		return sms;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setClient(JSONClient client) {
		this.client = client;
	}

	/**
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 * @param reqSentOn
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for failure logging.
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 * @param reqSentOn
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc,
			Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}
}
