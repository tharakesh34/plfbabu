package com.pennant.datamigration.dao;

import com.pennant.datamigration.model.SourceDataSummary;

public interface SourceDataSummaryDAO {

	void saveSummary(SourceDataSummary dataSummary);

	void deleteSummary(SourceDataSummary dataSummary);
}
