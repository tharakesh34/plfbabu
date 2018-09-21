package com.pennanttech.pff.notifications.dao;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.Notifications.SystemNotifications;

public interface SystemNotificationsDAO {

	List<SystemNotifications> getConfiguredSystemNotifications();

	List<Map<String, Object>> executeTriggerQuery(String query);
}
