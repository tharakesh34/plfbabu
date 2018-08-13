package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.pennant.backend.service.finance.NotificationLogDetailsService;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.sms.SmsEngine;


public class NotificationLogDetailsServiceImpl implements NotificationLogDetailsService{
	@Autowired
	private EmailEngine emailEngine;
	@Autowired
	private SmsEngine smsEngine;
	
	
	@Override
	public List<Notification> getNotificationLogDetailList(String finReference) {
		List<Notification> emailMessageLst=null; 
		emailMessageLst =emailEngine.getNotifications(finReference);
		
		return emailMessageLst;
	}
	
	@Override
	public List<Notification> getNotificationLogDetailSmsList(String finReference) {
		List<Notification> smsMessageLst=null;
		smsMessageLst =smsEngine.getNotifications(finReference);

		return smsMessageLst;
	}

	
	
}
