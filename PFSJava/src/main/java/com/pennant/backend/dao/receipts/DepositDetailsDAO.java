package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennanttech.pff.core.TableType;

public interface DepositDetailsDAO {

	//Deposit Details
	DepositDetails getDepositDetailsById(long depositId, String type);
	DepositDetails getDepositDetails(String depositType, String branchCode, String type);
	long save(DepositDetails depositDetails,TableType type);
	boolean isDuplicateKey(long depositId, TableType tableType);
	void update(DepositDetails depositDetails,TableType type);
	void updateActualAmount(long depositId, BigDecimal actualAmount, boolean increese, String type);
	void delete(DepositDetails depositDetails,TableType type);
	
	//Deposit Movements
	DepositMovements getDepositMovementsByDepositId(long depositId, String type);
	DepositMovements getDepositMovementsById(long movementId, String type);
	long saveDepositMovements(DepositMovements depositMovements, String type);
	void updateDepositMovements(DepositMovements depositMovements, String type);
	void deleteDepositMovements(DepositMovements depositMovements, String type);
	void deleteMovementsByDepositId(long depositId, String type);
	void updateLinkedTranIdByMovementId(long movementId, long likedTranId, String type);
	boolean isDuplicateKey(String depositSlipNumber, TableType tableType);
	DepositMovements getDepositMovementsByReceiptId(long receiptId, String type);
	void reverseMovementTranType(long movementId);
}