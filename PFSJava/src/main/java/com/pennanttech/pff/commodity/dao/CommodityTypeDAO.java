package com.pennanttech.pff.commodity.dao;

import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.core.TableType;

public interface CommodityTypeDAO extends BasicCrudDao<CommodityType> {

	CommodityType getCommodityType(long id, String type);

	boolean isDuplicateKey(CommodityType commodityType, TableType tableType);

	Map<String, Long> getCommodityTypeData();

}
