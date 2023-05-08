package com.pennanttech.pff.provision.service;

import java.util.Date;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;

public interface ProvisionService {

	long prepareQueueForSOM();

	long prepareQueueForEOM();

	long getQueueCount();

	int updateThreadID(long from, long to, int i);

	void updateProgress(long finID, int progressInProcess);

	Long getLinkedTranId(long finID);

	void doReversal(long linkedTranId);

	Provision getProvision(long finID, Date appDate, Provision mp);

	void doPost(Provision p);

	void save(Provision p);

	void update(Provision p);

	Provision getProvisionDetail(long finID);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	void executeProvisionRule(ProvisionRuleData provisionData, Provision p);

	boolean isRecordExists(long finID);
}
