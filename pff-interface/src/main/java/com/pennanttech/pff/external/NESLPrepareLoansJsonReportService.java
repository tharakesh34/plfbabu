package com.pennanttech.pff.external;

public interface NESLPrepareLoansJsonReportService {

	public String prepareJsonFileForLoansNESLReport(String date, String entity, String reportFormat,
			String customerCategory);

	public int getFileGenerationStatusBasedOnMonth(String date, String entity, String reportFormat,
			String customerCategory);

	public void updateFileDownloadCount(long id);

}
