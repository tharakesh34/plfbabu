package com.pennattech.pff.cd.dao;

import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.core.TableType;

public interface ManufacturerDAO extends BasicCrudDao<Manufacturer> {

	Manufacturer getManufacturer(long id, String type);

	boolean isDuplicateKey(Manufacturer commodityType, TableType tableType);

	public Map<String, Object> getGSTDataMapForManufac(long oEMID);

	public Manufacturer getDetails(long oEMID);

}
