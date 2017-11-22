package com.pennanttech.niyogin.communication.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennanttech.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Sms;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SendSMS;
import com.pennanttech.pff.external.service.NiyoginService;

public class SendSmsService extends NiyoginService implements SendSMS {
	private static final Logger	logger	= Logger.getLogger(SendSmsService.class);

	private String				serviceUrl;

	/**
	 * Method to send the sms for the given list of mobile numbers.
	 * 
	 * @param mobiles
	 * @param content
	 * 
	 */
	@Override
	public void sendSms(List<String> mobiles, String content) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		for (String mobileNo : mobiles) {
			send(mobileNo, content);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for send the SMS.
	 * 
	 * @param mobileNo
	 * @param content
	 */
	private void send(String mobileNo, String content) {
		logger.debug(Literal.ENTERING);
		Sms smsRequest = prepareRequest(mobileNo, content);
		Sms smsResponse = null;
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			smsResponse = (Sms) client.postProcess(serviceUrl, "", smsRequest, Sms.class);
			logger.info("Response : " + smsResponse.toString());
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
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
}
