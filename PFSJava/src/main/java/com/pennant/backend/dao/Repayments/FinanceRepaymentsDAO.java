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

	void deleteRpyDetailbyLinkedTranId(long linkedTranId, long finID);

	// Manual Repayment Details : Finance Repay Header Details & Finance Repay Schedule Details
	FinRepayHeader getFinRepayHeader(long finID, String type);

	Long saveFinRepayHeader(FinRepayHeader finRepayHeader, TableType tableType);

	void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type);

	void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type);

	List<RepayScheduleDetail> getRpySchdList(long finID, String type);

	List<RepayScheduleDetail> getRpySchdListByRepayID(long repayId, String type);

	void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, TableType tableType);

	void deleteRpySchdList(long finID, String type);

	void deleteRpyDetailbyMaxPostDate(Date finPostDate, long finID);

	FinRepayHeader getFinRepayHeader(long finID, long linkedTranId, String type);

	void deleteFinRepayHeaderByTranId(long finID, long linkedTranId, String string);

	void deleteFinRepaySchListByTranId(long finID, long linkedTranId, String string);

	BigDecimal getPaidPft(long finID, Date finPostDate);

	List<FinanceRepayments> getByFinRefAndSchdDate(long finID, Date finSchdDate);

	List<FinanceRepayments> getFinRepayments(long finID, List<Long> receiptList);

	// Receipts : Repay Header List & Repayment Schedule Detail list
	List<FinRepayHeader> getFinRepayHeadersByRef(long finID, String type);

	FinRepayHeader getFinRepayHeadersByReceipt(long receiptId, String type);

	void deleteByRef(long finID, TableType tableType);

	void deleteByReceiptId(long receiptId, TableType tableType);

	List<RepayScheduleDetail> getRpySchedulesForDate(long finID, Date schDate);

	void updateFinReference(String finReference, String extReference, String type);

	List<FinanceRepayments> getInProcessRepaymnets(long finID, List<Long> receiptList);

	long getNewRepayID();

	List<Long> getLinkedTranIdByReceipt(long receiptId, String type);

	Date getMaxValueDate(long finID);

	Date getFinSchdDateByReceiptId(long receiptid, String type);

	List<FinanceRepayments> getFinRepayList(long finID);

	List<FinanceRepayments> getFinRepayListByLinkedTranID(long finID);
}
