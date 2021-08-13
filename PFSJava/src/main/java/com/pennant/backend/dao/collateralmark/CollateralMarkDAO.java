package com.pennant.backend.dao.collateralmark;

import java.util.List;

import com.pennant.backend.model.collateral.FinCollateralMark;

public interface CollateralMarkDAO {

	int save(FinCollateralMark finCollateralMark);

	FinCollateralMark getCollateralById(String depositId);

	FinCollateralMark getCollatDeMarkStatus(long finID, String markStatus);

	List<FinCollateralMark> getCollateralList(long finID);

}
