package com.pennant.backend.dao.collateral;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.extendedfield.ExtendedFieldRender;

public interface ExtendedFieldRenderDAO {

	ExtendedFieldRender getExtendedFieldDetails(String reference, int seqNo, String tableName, String tableType);

	void update(String reference, int seqNo, Map<String, Object> map, String type, String tableName);

	void save(Map<String, Object> mappedValues, String type, String tableName);

	void delete(String reference, int seqNo, String type, String tableName);

	List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type);

	int getMaxSeqNoByRef(String reference, String tableName);

	void deleteList(String reference, String tableName, String tableType);

	Map<String, Object> getExtendedField(String reference, String tableName, String type);

	int validateMasterData(String tableName, String column, String filterColumn, Object fieldValue);

	boolean isExists(String reference, int seqNo, String tableName);

	int validateExtendedComboBoxData(String tableName, String lovField, Object[][] filters, String fieldValue);

	List<Map<String, Object>> getExtendedFieldMap(long id, String tableName, String type);

	List<Map<String, Object>> getExtendedFieldMapByVerificationId(long verificationId, String tableName);

	String getCategory(String reference);

	Map<String, Object> getCollateralMap(String reference, String tableName, String type);

	Map<String, Object> getCollateralMap(String reference, int seq, String tableName, String type);
}
