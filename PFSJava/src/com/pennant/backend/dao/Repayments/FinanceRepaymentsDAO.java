package com.pennant.backend.dao.Repayments;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;

public interface FinanceRepaymentsDAO {

	public FinanceRepayments getFinanceRepaymentsById(String finRef,Date finSchdDate, int finPaySeq, String type);
	public void update(FinanceRepayments financeRepayments, String type);
	public void delete(FinanceRepayments financeRepayments, String type);
	public long save(FinanceRepayments financeRepayments, String type);
	public void initialize(FinanceRepayments financeRepayments);
	public void refresh(FinanceRepayments entity);
	public void deleteByFinRef(String finref, String type);
	public List<FinanceRepayments> getFinRepayListById(String finRef, Date finSchdDate, String type);
	List<FinanceRepayments> getFinRepayListByFinRef(String finRef, String type);
	public void saveRepayList(List<FinanceRepayments> repaymentList,String type);

}
