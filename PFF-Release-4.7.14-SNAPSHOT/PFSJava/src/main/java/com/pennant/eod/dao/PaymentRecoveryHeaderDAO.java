package com.pennant.eod.dao;

import com.pennant.eod.beans.PaymentRecoveryHeader;

public interface PaymentRecoveryHeaderDAO {
	
	void save(PaymentRecoveryHeader header);
	
	PaymentRecoveryHeader getPaymentRecoveryHeader(String batchReferenceNumber);

	void updateCount(PaymentRecoveryHeader header);

}
