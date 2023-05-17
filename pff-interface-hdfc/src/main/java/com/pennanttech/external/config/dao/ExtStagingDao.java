package com.pennanttech.external.config.dao;

import java.util.List;

import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.external.extractions.model.BaselOne;
import com.pennanttech.external.extractions.model.BaselTwoExtract;
import com.pennanttech.external.extractions.model.RPMSExtract;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;

public interface ExtStagingDao {
	List<ExtPresentmentFile> getStagingPresentment(String flag);

	void updatePickupStatus(String pickFlag, long agreementId, String chequeSno);

	public void updateErrorDetails(long agreementId, String chequeSno, String errorFlag, String errorDesc);

	void truncateTable(String tableName);

	void saveBaselOneExtractionDataToTable(BaselOne baselOne);

	void saveAlmExtractionDataToTable(AlmExtract almExtract);

	void saveBaselTwoExtractionDataToTable(BaselTwoExtract baselTwo);

	void saveRPMSExtractExtractionDataToTable(RPMSExtract rpmsExtract);
}
