package com.pennant.datamigration.dao;

import java.util.List;

import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.datamigration.model.BasicLoanRecon;
import com.pennant.datamigration.model.SourceReport;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;

public interface BasicLoanReconDAO {
	
	void saveRecon(BasicLoanRecon basicLoanRecon);
	void cleanDestination();
	List<FinanceType> getDMFinTypes(String type);
	SourceReport getSourceReportDetails(String lnno);
	List <FeeTypeVsGLMapping> getFeeTypeVsGLMappings();
}
