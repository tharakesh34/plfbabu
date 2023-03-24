package com.pennanttech.external.extractions.dao;

public interface ExtExtractionDao {
	public String executeExtractionSp(String spName);

	public String executeRequestFileSp(String fileName);

}
