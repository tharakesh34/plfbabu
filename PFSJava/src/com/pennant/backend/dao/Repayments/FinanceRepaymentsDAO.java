package com.pennant.backend.dao.Repayments;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;

public interface FinanceRepaymentsDAO {

	long save(FinanceRepayments financeRepayments, String type);
	void initialize(FinanceRepayments financeRepayments);
	void refresh(FinanceRepayments entity);
	List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc, String type);
	void deleteRpyDetailbyLinkedTranId(long linkedTranId, String finReference);
	
	//Manual Repayment Details :  Finance Repay Header Details & Finance Repay Schedule Details
	FinRepayHeader getFinRepayHeader(String finReference, String type);
	void saveFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	
	List<RepayScheduleDetail> getRpySchdList(String finReference, String type);
	void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, String type);
	void deleteRpySchdList(String finReference, String type);
	void deleteRpyDetailbyMaxPostDate(Date finPostDate, String finReference);
	FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type);
	void deleteFinRepayHeaderByTranId(String finReference, long linkedTranId, String string);
	void deleteFinRepaySchListByTranId(String finReference, long linkedTranId, String string);
	BigDecimal getPaidPft(String finReference, Date finPostDate);

}
