package com.pennant.backend.dao.Repayments;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;

public interface FinanceRepaymentsDAO {

	public long save(FinanceRepayments financeRepayments, String type);
	public void initialize(FinanceRepayments financeRepayments);
	public void refresh(FinanceRepayments entity);
	public List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc, String type);
	public void deleteRpyDetailbyLinkedTranId(long linkedTranId, String finReference);
	
	//Manual Repayment Details :  Finance Repay Header Details & Finance Repay Schedule Details
	public FinRepayHeader getFinRepayHeader(String finReference, String type);
	public void saveFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	public void updateFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	public void deleteFinRepayHeader(FinRepayHeader finRepayHeader, String type);
	
	public List<RepayScheduleDetail> getRpySchdList(String finReference, String type);
	public void saveRpySchdList(List<RepayScheduleDetail> repaySchdList, String type);
	public void deleteRpySchdList(String finReference, String type);
	public void deleteRpyDetailbyMaxPostDate(Date finPostDate, String finReference);
	public FinRepayHeader getFinRepayHeader(String finReference, long linkedTranId, String type);
	public void deleteFinRepayHeaderByTranId(String finReference, long linkedTranId, String string);
	public void deleteFinRepaySchListByTranId(String finReference, long linkedTranId, String string);

}
