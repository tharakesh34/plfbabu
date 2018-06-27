package com.pennanttech.pff.service.sampling;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;

public interface SamplingService {

	void save(Sampling sampling);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	Sampling getSampling(Sampling sampling, String type);

	List<AuditDetail> processingCustIncomeDetails(List<AuditDetail> auditDetails, Sampling sampling, String type,
			long custId);

	List<AuditDetail> processingObligations(List<AuditDetail> auditDetails, Sampling sampling, String type,
			long custId);

	Sampling getSampling(String keyReference, String type);

	void calculateEligilibity(Sampling sampling);
	
	long getCollateralLinkId(long samplingId, String reference);
	
	Map<String, List<ExtendedFieldData>> getCollateralFields(String type, String reference, long sequence);

	boolean isExist(String finReference,String type);

}
