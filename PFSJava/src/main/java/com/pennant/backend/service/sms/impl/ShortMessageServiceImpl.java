package com.pennant.backend.service.sms.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.sms.ShortMessageService;
import com.pennanttech.pff.external.SMSService;

public class ShortMessageServiceImpl implements ShortMessageService {
    
	private static final Logger logger = Logger.getLogger(ShortMessageServiceImpl.class);

	@Autowired(required = false)
	private SMSService sMSService; 

	/**
	 * 
	 * 
	 */
	@Override
	public void sendMessage(List<String> mobiles, String content) {
		sMSService.sendSms(mobiles, content);
	}

	@Override
	public void sendMessage(List<CustomerPhoneNumber> custPhoneNoList, List<String> smsList) {
		logger.debug("Entering");
		
	}
}
