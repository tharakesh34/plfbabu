package com.pennant.backend.service.finance;

import java.util.List;

import com.pennanttech.pennapps.notification.Notification;


public interface NotificationLogDetailsService {
	
	List<Notification> getNotificationLogDetailList(String finReference);

	List<Notification> getNotificationLogDetailSmsList(String finReference);
	
	
	
	
}
