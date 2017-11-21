package com.pennant.backend.dao.collateral;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.extendedfields.ExtendedFieldRender;

public interface ExtendedFieldRenderDAO {

	ExtendedFieldRender getExtendedFieldDetails(String reference, int seqNo, String tableName, String tableType);

	void update(String reference, int seqNo, Map<String, Object> map, String type, String tableName);

	void save(Map<String, Object> mappedValues, String type, String tableName);

	void delete(String reference, int seqNo, String type, String tableName);

	List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type);

	int getMaxSeqNoByRef(String reference, String tableName);

	void deleteList(String reference, String tableName, String tableType);

	Map<String, Object> getExtendedField(String reference, String tableName, String type);

	int validateMasterData(String tableName, String column, String filterColumn, String fieldValue);
}
