package com.pennattech.pff.mmfl.cd.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.Manufacturer;

public interface ManufacturerDAO extends BasicCrudDao<Manufacturer> {

	Manufacturer getManufacturer(long id, String type);

	boolean isDuplicateKey(Manufacturer commodityType, TableType tableType);

}
