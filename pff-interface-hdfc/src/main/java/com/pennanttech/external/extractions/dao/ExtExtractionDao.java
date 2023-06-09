package com.pennanttech.external.extractions.dao;

import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.external.extractions.model.BaselOne;
import com.pennanttech.external.extractions.model.BaselTwoExtract;
import com.pennanttech.external.extractions.model.RPMSExtract;

public interface ExtExtractionDao {

	long getSeqNumber(String tableName);

	void truncateTable(String tableName);

	void saveBaselOneExtractionDataToTable(BaselOne baselOne);

	void saveAlmExtractionDataToTable(AlmExtract almExtract);

	void saveBaselTwoExtractionDataToTable(BaselTwoExtract baselTwo);

	void saveRPMSExtractExtractionDataToTable(RPMSExtract rpmsExtract);

	void truncateStageTable(String tableName);

}
