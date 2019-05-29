package com.pennant.backend.service.lenderupload;

import com.pennant.backend.model.lenderdataupload.LenderDataUpload;

public interface LenderDataService {

	int update(LenderDataUpload lenderDataUpload, String tableName, String type);

	boolean isLenderExist(String finReference, String tableName, String type);
}
