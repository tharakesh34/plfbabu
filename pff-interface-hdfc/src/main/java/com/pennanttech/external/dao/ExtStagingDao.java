package com.pennanttech.external.dao;

import java.util.List;

import com.pennant.backend.model.finance.AlmExtract;
import com.pennant.backend.model.finance.BaselOne;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;

public interface ExtStagingDao {
	List<ExtPresentmentFile> getStagingPresentment(String flag);

	void updatePickupStatus(String pickFlag, long agreementId, String chequeSno);

	public void updateErrorDetails(long agreementId, String chequeSno, String errorFlag, String errorDesc);

	void truncateTable(String tableName);

	void saveBaselOneExtractionDataToTable(BaselOne baselOne);

	void saveAlmExtractionDataToTable(AlmExtract almExtract);
}
