package com.pennanttech.niyogin.communication.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Sms;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SMSService;
import com.pennanttech.pff.external.service.NiyoginService;

public class SMSServiceImpl extends NiyoginService implements SMSService {
	private static final Logger	logger	= Logger.getLogger(SMSServiceImpl.class);

	private String				serviceUrl;

	/**
	 * Method to send the sms for the given mobile numbers.
	 * 
	 * @param custPhoneNos
	 * @param smsContent
	 * 
	 */
	@Override
	public void sendSms(List<CustomerPhoneNumber> custPhoneNos, List<String> smsContent) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		
		if(custPhoneNos != null && !custPhoneNos.isEmpty() && smsContent != null && !smsContent.isEmpty()) {
			for (CustomerPhoneNumber custPhone : custPhoneNos) {
				for(String sms:smsContent) {
					send(custPhone.getPhoneNumber(), sms);
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
	private void send(String mobileNo, String content) {
		logger.debug(Literal.ENTERING);
		Sms smsRequest = prepareRequest(mobileNo, content);
		Sms smsResponse = null;
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String response=client.post(serviceUrl, smsRequest);
			logger.info("Response : " + response.toString());
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
