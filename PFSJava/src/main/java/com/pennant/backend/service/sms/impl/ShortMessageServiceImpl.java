package com.pennant.backend.service.sms.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.sms.ShortMessageService;
import com.pennanttech.pff.external.SMSService;

public class ShortMessageServiceImpl implements ShortMessageService {

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
}
