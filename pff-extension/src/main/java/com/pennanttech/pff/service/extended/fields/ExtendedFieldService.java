package com.pennanttech.pff.service.extended.fields;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.agreement.InterestCertificate;

public interface ExtendedFieldService {
	public void setExtendedFields(InterestCertificate interestCertificate);

	public void setExtendedFields(Object object, String moduleName, List<Map<String, Object>> details);

	Map<String, Object> getExtendedField(String reference, String tableName, String type);

	public void processExtendedFields(String tableName, Object extendedField);

}
