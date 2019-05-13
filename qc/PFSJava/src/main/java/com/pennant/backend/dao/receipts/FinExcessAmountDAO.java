package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;

public interface FinExcessAmountDAO {

	List<FinExcessAmount> getExcessAmountsByRef(String finReference);

	void saveExcess(FinExcessAmount excess);

	void updateUtilise(long payAgainstID, BigDecimal amount);

	void saveExcessMovement(FinExcessMovement movement);

	void updateExcessReserve(long payAgainstID, BigDecimal reserveAmt);

	FinExcessAmountReserve getExcessReserve(long receiptID, long payAgainstID);

	void saveExcessReserveLog(long receiptID, long payAgainstID, BigDecimal reserveAmt, String paymentType);

	void updateExcessReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve, String paymentType);

	void deleteExcessReserve(long receiptID, long payAgainstID, String paymentType);

	FinExcessAmount getExcessAmountsByRefAndType(String finReference, String amountType);

	void updateExcessAmount(long excessID, String amountType, BigDecimal amount);

	void updateExcessBal(long excessID, BigDecimal amount);

	void deductExcessReserve(long excessID, BigDecimal amount);

	int updateExcessReserveByRef(String reference, String amountType, BigDecimal amount);

	int updateExcessBalByRef(String reference, String amountType, BigDecimal amount);

	int updExcessAfterRealize(String reference, String amountType, BigDecimal amount);

	List<FinExcessAmountReserve> getExcessReserveList(long receiptID);

	void updateExcessAmount(long excessID, BigDecimal advanceAmount);

	void updateUtiliseOnly(long excessID, BigDecimal amount);

	List<FinExcessAmount> getAllExcessAmountsByRef(String finReference, String type);

	void updateUtilizedAndBalance(FinExcessAmount excessAmount);

	void updateExcess(FinExcessAmount excess);

	FinExcessAmount getFinExcessAmount(String finreference, String amountType);
	
	FinExcessMovement getFinExcessMovement(long excessID, String movementFrom, Date schDate);

	int updateExcessReserve(FinExcessAmount excessMovement);
	
	int updateReserveUtilization(FinExcessAmount excessMovement);

}
