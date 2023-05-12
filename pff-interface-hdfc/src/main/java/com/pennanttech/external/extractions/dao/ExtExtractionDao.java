package com.pennanttech.external.extractions.dao;

import java.util.Date;

public interface ExtExtractionDao {

	long getSeqNumber(String tableName);

	public String executeSp(String spName);

	public String executeSp(String spName, String fileName);

	void truncateTable(String tableName);

	String executeSp(String spName, Date appDate);

}
