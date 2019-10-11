package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;

public interface PushNotificationsService {

	void getPushNotifications(AuditHeader auditHeader);

}
