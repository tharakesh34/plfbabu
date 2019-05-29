package com.pennant.backend.dao.lenderupload;

import com.pennant.backend.model.lenderdataupload.LenderDataUpload;

public interface LenderDataDAO {

	int update(LenderDataUpload dataUpload, String tableName, String type);

	boolean isLenderExist(String finReference, String tableName, String type);
}
