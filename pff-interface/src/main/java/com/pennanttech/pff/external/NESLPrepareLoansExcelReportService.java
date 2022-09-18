package com.pennanttech.pff.external;

public interface NESLPrepareLoansExcelReportService {

	public String prepareExcelFileForLoansNESLReport(String date, String entity, String reportFormat,
			String customerCategory);

	public void updateFileDownloadCount(long id);

}
