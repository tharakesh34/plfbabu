package com.pennant.backend.dao.dashboard;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.pennant.backend.model.dashboard.DetailStatisticsHeader;


public interface DetailStatisticsHeaderDAO {

	List<DetailStatisticsHeader> getDetailStatisticsHeaderByRoleCode(String roleCode);
	List<DetailStatisticsHeader> getDetailStatisticsHeaderByModuleName(String moduleName);
	void save(DetailStatisticsHeader detailStatisticsHeader); 
	boolean isExists(DetailStatisticsHeader detailStatisticsHeader);
	void update(DetailStatisticsHeader statisticsHeader, boolean decrease)throws DataAccessException;
	List<DetailStatisticsHeader> getDetailStatisticsHeaderGroupByRole();
	List<DetailStatisticsHeader> getDetailStsHeaderGroupByModule(String roles);
}
