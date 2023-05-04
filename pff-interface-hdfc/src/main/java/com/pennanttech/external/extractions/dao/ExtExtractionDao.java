package com.pennanttech.external.extractions.dao;

public interface ExtExtractionDao {

	long getSeqNumber(String tableName);

	public String executeSp(String spName);

	public String executeSp(String spName, String fileName);

}
