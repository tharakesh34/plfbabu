package com.pennanttech.pff.commodity.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.core.TableType;

public interface CommoditiesDAO extends BasicCrudDao<Commodity> {

	Commodity getCommodities(long id, String type);

	boolean isDuplicateKey(Commodity commodity, TableType tableType);

	boolean isDuplicateCode(Commodity commodity, TableType tableType);

	boolean isDuplicateHSNCode(Commodity commodity, TableType tableType);

	void saveCommoditiesLog(MapSqlParameterSource mapdata);

	Commodity getCommodity(Commodity commodity);

	void updateCommodity(Commodity commodity);

	String getCommodityHSNCode(String code);

	String getCommodityCode(String hsnCode);

}
