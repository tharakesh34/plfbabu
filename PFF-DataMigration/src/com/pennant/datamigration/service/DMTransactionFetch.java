package com.pennant.datamigration.service;

import java.util.List;

import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennant.datamigration.model.SourceDataSummary;


public interface DMTransactionFetch {
	
	public MigrationData getFinanceDetailsFromSource(String finReference, ReferenceID rid, String type);
	public SourceDataSummary setSourceSummary(MigrationData md, String type);
	public void deleteFromSourceSummary();
	public void cleanDestination();
	public List <FinanceType> getFinTypeList(String type);
	public List <FeeTypeVsGLMapping> getFeeVsGLList();
}
