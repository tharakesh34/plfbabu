package com.pennanttech.pff.service.sampling;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinanceDetail;
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

	Map<String, List<ExtendedFieldData>> getCollateralFields(String type, String linkId, String snapLinkId);

	boolean isExist(String finReference, String type);

	void saveSnap(Sampling sampling);

	long getCollateralLinkId(String collateralRef, long id, String string);

	void saveOnReSubmit(Sampling sampling);
	
	List<CustomerIncome> getIncomesByCustId(long samplingId,long custId, String type);
	
	void reCalculate(FinanceDetail financeDetail);
}
