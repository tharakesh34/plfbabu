package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.District;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>District model</b> class.<br>
 * 
 */
public interface DistrictDAO extends BasicCrudDao<District> {

	boolean isDuplicateKey(String code, TableType tableType);

	public District getDistrictById(long id, String type);

	District getDistrictByCity(String cityCode);

	boolean isExistDistrictCode(long id);

}
