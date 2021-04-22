package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinAssetAmtMovement;

public interface FinAssetAmtMovementDAO {

	void saveFinAssetAmtMovement(FinAssetAmtMovement assetAmtMovt);

	public List<FinAssetAmtMovement> getFinAssetAmtMovements(String finReference, String movementType);
}
