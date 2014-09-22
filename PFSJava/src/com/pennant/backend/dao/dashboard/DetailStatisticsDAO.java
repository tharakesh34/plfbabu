package com.pennant.backend.dao.dashboard;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboard.DetailStatistics;
import com.pennant.fusioncharts.ChartSetElement;

public interface DetailStatisticsDAO {

	List<DetailStatistics> getAuditDetails() ;
	List<DetailStatistics> getDetailStatisticsList(DetailStatistics detailStatistics);
	void updateDetailStaticAudit(DetailStatistics detailStatistics) throws DataAccessException;
	void updateCompleteStatus(DetailStatistics detailStatistics) throws DataAccessException;
	void save(DetailStatistics detailStatistics) throws DataAccessException;
	DetailStatistics getAuditDetail(DetailStatistics detailStatistics)  throws DataAccessException;
	List<ChartSetElement> getLabelAndValues(DashboardConfiguration aDashboardConfiguration);
}
