package com.pennant.backend.dao.Repayments;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennanttech.pff.core.TableType;

public interface FinanceRepaymentsDAO {

	long save(FinanceRepayments financeRepayments, String type);

	void save(List<FinanceRepayments> list, String type);

	List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc, String type);

	void deleteRpyDetailbyLinkedTranId(long linkedTranId, String finReference);

	//Manual Repayment Details :  Finance Repay Header Details & Finance Repay Schedule Details
	FinRepayHeader getFinRepayHeader(String finReference, String type);

	Long saveFinRepayHeader(FinRepayHeader finRepayHeader, TableType tableType);

	void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type);

	void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type);

	List<RepayScheduleDetail> getRpySchdList(String finReference, String type);

	List<RepayScheduleDetail> getRpySchdList(long repayId, String type);

	void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, TableType tableType);

	void deleteRpySchdList(String finReference, String type);

	void deleteRpyDetailbyMaxPostDate(Date finPostDate, String finReference);

	FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type);

	void deleteFinRepayHeaderByTranId(String finReference, long linkedTranId, String string);

	void deleteFinRepaySchListByTranId(String finReference, long linkedTranId, String string);

	BigDecimal getPaidPft(String finReference, Date finPostDate);

	List<FinanceRepayments> getByFinRefAndSchdDate(String finReference, Date finSchdDate);

	List<FinanceRepayments> getFinRepayments(String finReference, List<Long> receiptList);

	// Receipts : Repay Header List & Repayment Schedule Detail list
	List<FinRepayHeader> getFinRepayHeadersByRef(String finReference, String type);

	FinRepayHeader getFinRepayHeadersByReceipt(long receiptId, String type);

	void deleteByRef(String finReference, TableType tableType);

	List<RepayScheduleDetail> getDMRpySchdList(String finReference, String type);

	void deleteByReceiptId(long receiptId, TableType tableType);

	List<RepayScheduleDetail> getRpySchedulesForDate(String finReference, Date schDate);

	void updateFinReference(String finReference, String extReference, String type);

	List<FinanceRepayments> getInProcessRepaymnets(String finReference, List<Long> receiptList);

	long getNewRepayID();

	Long getLinkedTranIdByReceipt(long receiptId, String type);

	Date getMaxValueDate(String finReference);
}
