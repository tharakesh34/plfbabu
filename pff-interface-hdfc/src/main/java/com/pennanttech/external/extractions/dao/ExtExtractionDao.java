package com.pennanttech.external.extractions.dao;

import java.util.Date;

import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.external.extractions.model.BaselOne;
import com.pennanttech.external.extractions.model.BaselTwoExtract;
import com.pennanttech.external.extractions.model.RPMSExtract;

public interface ExtExtractionDao {

	long getSeqNumber(String tableName);

	public String executeSp(String spName);

	public String executeSp(String spName, String fileName);

	void truncateTable(String tableName);

	String executeSp(String spName, Date appDate);

	void saveBaselOneExtractionDataToTable(BaselOne baselOne);

	void saveAlmExtractionDataToTable(AlmExtract almExtract);

	void saveBaselTwoExtractionDataToTable(BaselTwoExtract baselTwo);

	void saveRPMSExtractExtractionDataToTable(RPMSExtract rpmsExtract);

	void truncateStageTable(String tableName);

}
