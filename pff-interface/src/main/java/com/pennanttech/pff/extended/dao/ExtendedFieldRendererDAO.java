package com.pennanttech.pff.extended.dao;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.extendedfield.ExtendedFieldRender;

public interface ExtendedFieldRendererDAO {

	public Map<String, Object> getExtendedField(String reference, String tableName, String type);

	public Map<String, List<ExtendedFieldRender>> getCollateralExtendedFields(String reference, String tableName,
			String type);
}
