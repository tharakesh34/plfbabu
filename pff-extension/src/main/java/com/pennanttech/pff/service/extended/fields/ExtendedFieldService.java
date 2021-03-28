package com.pennanttech.pff.service.extended.fields;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.agreement.InterestCertificate;

public interface ExtendedFieldService {
	public void setExtendedFields(InterestCertificate interestCertificate);

	public void setExtendedFields(Object object, String moduleName, List<Map<String, Object>> details);

	void update(String reference, int seqNo, Map<String, Object> mappedValues, String type, String tableName);

	void save(Map<String, Object> mappedValues, String type, String tableName);

	Map<String, Object> getExtendedField(String reference, String tableName, String type);

	public void processExtendedFields(String tableName, Object extendedField);

}
