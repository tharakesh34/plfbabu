package com.pennant.backend.dao.collateral;

import java.util.List;

import com.pennant.backend.model.collateral.CollateralThirdParty;

public interface CollateralThirdPartyDAO {

	CollateralThirdParty getCollThirdPartyDetails(String collateralRef, long customerId, String type);

	void save(CollateralThirdParty collateralThirdParty, String tableType);

	void update(CollateralThirdParty collateralThirdParty, String tableType);

	void delete(CollateralThirdParty collateralThirdParty, String tableType);

	List<CollateralThirdParty> getCollThirdPartyDetails(String collateralRef, String tableType);

	void deleteList(CollateralThirdParty collateralThirdParty, String tableType);

	boolean isThirdPartyUsed(String collateralRef, long custId);

}
