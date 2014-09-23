/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

package com.pennant.backend.dao.notifications;

import java.util.List;

import com.pennant.backend.model.rulefactory.Notifications;




/**
 * DAO methods declaration for the <b>Notifications model</b> class.<br>
 * 
 */
public interface NotificationsDAO {

	Notifications getNotifications();
	Notifications getNewNotifications();
	Notifications getNotificationsById(String ruleCode,String type);
	List<Notifications> getNotificationsByModule(String ruleModule,String type);
	void update(Notifications notifications,String type);
	void delete(Notifications notifications,String type);
	String save(Notifications notifications,String type);
	void initialize(Notifications notifications);
	void refresh(Notifications entity);
	Notifications getNotifications(String notificationsLevel, String notificationsDecipline,String type);
}