package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface FinExcessAmountDAO {

	List<FinExcessAmount> getExcessAmountsByRef(long finID);

	void saveExcess(FinExcessAmount excess);

	void saveExcessList(List<FinExcessAmount> excess);

	void updateUtilise(long payAgainstID, BigDecimal amount);

	void saveExcessMovement(FinExcessMovement movement);

	void updateExcessReserve(long payAgainstID, BigDecimal reserveAmt);

	FinExcessAmountReserve getExcessReserve(long receiptID, long payAgainstID, String paymentType);

	void saveExcessReserveLog(long receiptID, long payAgainstID, BigDecimal reserveAmt, String paymentType);

	void updateExcessReserveLog(long receiptID, long payAgainstID, BigDecimal diffInReserve, String paymentType);

	void deleteExcessReserve(long receiptID, long payAgainstID, String paymentType);

	FinExcessAmount getExcessAmountsByRefAndType1(long finID, String amountType);

	List<FinExcessAmount> getExcessAmountsByRefAndType(long finID, Date valueDate, String amountType);

	void updateExcessAmount(long excessID, String amountType, BigDecimal amount);

	void updateExcessBal(long excessID, BigDecimal amount);

	void deductExcessReserve(long excessID, BigDecimal amount);

	int updateExcessReserveByRef(long finID, String amountType, BigDecimal amount);

	int updateExcessBalByRef(long finID, String amountType, BigDecimal amount);

	int updExcessAfterRealize(long finID, String amountType, BigDecimal amount, long receiptID);

	List<FinExcessAmountReserve> getExcessReserveList(long receiptID);

	void updateExcessAmount(long excessID, BigDecimal advanceAmount);

	void updateUtiliseOnly(long excessID, BigDecimal amount);

	List<FinExcessAmount> getAllExcessAmountsByRef(long finID, String type);

	void updateUtilizedAndBalance(FinExcessAmount excessAmount);

	void updateExcess(FinExcessAmount excess);

	FinExcessAmount getFinExcessAmount(long finID, String amountType);

	FinExcessMovement getFinExcessMovement(long excessID, String movementFrom, Date schDate);

	int updateExcessReserve(FinExcessAmount excessMovement);

	int updateReserveUtilization(FinExcessAmount excessMovement);

	List<FinExcessMovement> getFinExcessAmount(long presentmentid);

	FinExcessAmount getFinExcessByID(long excessID);

	int deleteMovemntByPrdID(long presentmentId);

	void saveExcessMovementList(List<FinExcessMovement> movements);

	FinExcessAmount getFinExcessAmount(long finID, long receiptId);

	void batchUpdateExcessAmount(List<PresentmentDetail> presentmentDetails);

	int updateExcessAmtList(List<FinExcessAmount> excess);

	int updateExcessReserveList(List<FinExcessAmount> excessRevarsal);

	long saveExcessMovements(List<FinExcessMovement> excessMovement);

	int updateExcessEMIAmount(List<FinExcessAmount> emiInAdvance, String amtType);

	boolean isFinExcessAmtExists(long finID);

	List<FinExcessAmount> getExcessAmountsByRefAndType(long finID);

	List<FinExcessAmount> getFinExcessByRefForAutoRefund(long finID);

	List<FinExcessAmount> getExcessAmountsByRefAndType(long finID, String amountType);

	FinExcessAmount getExcessAmountsByReceiptId(long receiptId);

	FinExcessAmount getExcessAmountsByReceiptId(long finID, String amountType, long receiptId);

	List<FinExcessMovement> getExcessMovementList(long id, String movementType);

	int updateTerminationExcess(long excessID, BigDecimal excessAmt, BigDecimal balns, BigDecimal reserved);

	BigDecimal getExcessBalance(long finID);

	List<FinExcessAmount> getExcessRcdList(long finID, Date activeNDate);

	BigDecimal getSettlementAmountReceived(long finId);

	FinExcessAmount getFinExcessAmountById(long excessID, String type);

	void updateExcessreserved(long receiptID, BigDecimal excessAmt);

	BigDecimal getBalAdvIntAmt(String finReference);

	List<FinExcessAmount> getExcessList(long finID);
}
