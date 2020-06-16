package com.pennanttech.pff.external.mandate.dao;

import java.util.List;

public interface MandateProcessDAO {
	public long saveMandateRequests(List<Long> mandateIds);

	public List<Long> getMandateList(String entityCode);

	public List<String> getEntityCodes();
}
