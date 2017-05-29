package com.pennant.eod.dao;

import java.util.Date;
import java.util.List;

import com.pennant.eod.beans.PaymentRecoveryDetail;

public interface PaymentRecoveryDetailDAO {
	
	void save(List<PaymentRecoveryDetail> detail);
	void update(List<PaymentRecoveryDetail> detail);
	List<PaymentRecoveryDetail> getPaymentRecoveryDetails(String batchReferenceNumber);
	List<PaymentRecoveryDetail> getPaymentRecoveryByCustomer(String bathRef, String customerID);
	List<PaymentRecoveryDetail> getPaymentRecoveryByid(String bathRef, String finreference, Date scheduleDate, String finEvent);
	void update(PaymentRecoveryDetail detail);

}
