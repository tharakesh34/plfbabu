package com.pennant.backend.service.sms.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.service.sms.ShortMessageService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SMSService;

public class ShortMessageServiceImpl implements ShortMessageService {
    
	private static final Logger logger = Logger.getLogger(ShortMessageServiceImpl.class);

	@Autowired(required = false)
	private SMSService sMSService; 

	/**
	 * Method for call the ExternalServiceTask to send SMS.
	 * 
	 * @param custPhoneNoList
	 * @param smsContent
	 * @return 
	 */

	@Override
	public void sendMessage(List<MailTemplate> smsList, String finReference) {
		logger.debug(Literal.ENTERING);
		sMSService.sendSms(smsList, finReference);
		logger.debug(Literal.LEAVING);
	}
}
