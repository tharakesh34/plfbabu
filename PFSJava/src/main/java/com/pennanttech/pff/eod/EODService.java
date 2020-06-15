package com.pennanttech.pff.eod;

public interface EODService {

	void startEOD();

	void stopEOD();

	String getCronExpression();

	boolean isAutoRequired();
}
