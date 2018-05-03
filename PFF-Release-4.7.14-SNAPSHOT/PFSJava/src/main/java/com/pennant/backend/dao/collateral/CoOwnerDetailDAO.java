package com.pennant.backend.dao.collateral;

import java.util.List;

import com.pennant.backend.model.collateral.CoOwnerDetail;

public interface CoOwnerDetailDAO {

	long save(CoOwnerDetail coOwnerDetail, String tableType);

	void update(CoOwnerDetail coOwnerDetail, String tableType);

	void delete(CoOwnerDetail coOwnerDetail, String tableType);

	List<CoOwnerDetail> getCoOwnerDetailByRef(String collateralRef, String tableType);

	CoOwnerDetail getCoOwnerDetailByRef(String collateralRef, int coOwnerId, String tableType);

	int getVersion(String collateralRef, String tableType);

	void deleteList(CoOwnerDetail coOwnerDetail, String tableType);

}
