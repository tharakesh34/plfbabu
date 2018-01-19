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
import com.pennanttech.pff.external.SMSService;
import com.pennanttech.pff.external.service.NiyoginService;

public class SMSServiceImpl extends NiyoginService implements SMSService {
	private static final Logger	logger			= Logger.getLogger(SMSServiceImpl.class);

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
	public void sendSms(List<MailTemplate> smsList,String finReference) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (smsList!=null && !smsList.isEmpty()) {
			for (MailTemplate mailTemplate : smsList) {
				List<String> listnumbers = mailTemplate.getLovDescMobileNumbers();
				if (listnumbers!=null && !listnumbers.isEmpty()) {
					for (String string : listnumbers) {
						send(string, mailTemplate.getLovDescSMSContent(),finReference);
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
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference=finReference;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String reuestString = client.getRequestString(smsRequest);
			jsonResponse = client.post(serviceUrl, reuestString);
			logger.info("Response : " + jsonResponse.toString());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, smsRequest);
			throw new InterfaceException("9999", e.getMessage());
		}
		// success case logging
		doInterfaceLogging(smsRequest, finReference);
				
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
	 * Method for prepare data and logging
	 * 
	 * @param smsRequest
	 * @param reference
	 */
	private void doInterfaceLogging(Sms smsRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, smsRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}
}
