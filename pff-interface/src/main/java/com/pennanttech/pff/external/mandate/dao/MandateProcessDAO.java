package com.pennanttech.pff.external.mandate.dao;

import java.util.List;

public interface MandateProcessDAO {
	public long saveMandateRequests(List<Long> mandateIds);

	public List<Long> getMandateList(String entityCode);

	public List<String> getEntityCodes();

	public void deleteMandateRequests(List<Long> selectedMandateIds);

	public void deleteMandateStatus(List<Long> selectedMandateIds);

	public List<Long> getMandateList(String entityCode, String partnerBankCode);

	public List<String> getPartnerBankCodeByEntity(String entityCode);
}
