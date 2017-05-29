package com.pennant.backend.ws.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.channeldetails.APIChannel;
import com.pennant.ws.exception.APIException;

public interface APIChannelService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader) ;

	AuditHeader delete(AuditHeader auditHeader);

	APIChannel getChannelDetailsById(long id);

	APIChannel getChannelDetails();

	APIChannel getNewChannelDetails();

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
	
	long getChannelId(String channelId, String channelIp) throws APIException;
}
