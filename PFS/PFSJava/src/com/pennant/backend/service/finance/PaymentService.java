package com.pennant.backend.service.finance;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.finance.PaymentHeader;

public interface PaymentService {
	
	public PaymentHeader getPaymentHeader(FinanceMain financeMain, Date date);

	public long save(FinanceRepayments financeRepayments, String type);

	public void maintainWorkSchedules(String finReference, long userId, List<FinanceScheduleDetail> financeScheduleDetails);

	public void updateScheduleDetails(FinanceScheduleDetail financeScheduleDetail);

	public void delete(FinanceRepayments financeRepayments, String type);

	public void deleteWorkByFinRef(String finref);

	public PaymentDetails getPaymentDetails(String finref, Date date);

	public void update(FinanceRepayments financeRepayments, String type);

	public void updateMainScheduleDetails(FinanceScheduleDetail financeScheduleDetail);
	public Map<Date,List<FinanceRepayments>> getFinanceRepaymentsByFinRef(String finRef,String type);
}
