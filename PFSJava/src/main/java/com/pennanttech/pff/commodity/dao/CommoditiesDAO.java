package com.pennanttech.pff.commodity.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.core.TableType;

public interface CommoditiesDAO extends BasicCrudDao<Commodity> {

	Commodity getCommodities(long id, String type);

	boolean isDuplicateKey(Commodity commodities, TableType tableType);

	void saveCommoditiesLog(MapSqlParameterSource mapdata);

	Commodity getQueryOperation(Commodity commodities);

}
