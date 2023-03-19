package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinOverDueChargeMovement;
import com.pennant.backend.model.finance.FinOverDueCharges;

public interface FinODCAmountDAO {

	List<FinOverDueCharges> getFinODCAmtByFinRef(long finID, Date schdDate, String chargeType);

	void updateFinODCAmts(List<FinOverDueCharges> updateList);

	void saveFinODCAmts(List<FinOverDueCharges> saveList);

	long saveFinODCAmt(FinOverDueCharges odc);

	void saveMovement(List<FinOverDueChargeMovement> movements);

	void updateFinODCBalAmts(List<FinOverDueCharges> finODCAmounts);

	List<FinOverDueChargeMovement> getFinODCMovements(long receiptID);

	void updateReversals(List<FinOverDueCharges> updatedODAmt);

	void updateMovenantStatus(long receiptID, String receiptModeStatus);

	List<FinOverDueCharges> getFinODCAmtByRef(long finID, String chargeType);
}
