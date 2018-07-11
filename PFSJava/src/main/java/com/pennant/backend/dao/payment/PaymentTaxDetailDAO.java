package com.pennant.backend.dao.payment;

import com.pennant.backend.model.payment.PaymentTaxDetail;
import com.pennanttech.pff.core.TableType;

public interface PaymentTaxDetailDAO {

	PaymentTaxDetail getTaxDetailByID(long paymentDetailId, String type);

	void save(PaymentTaxDetail taxDetail, TableType tableType);

	void delete(long paymentDetailId, TableType tableType);

	void deleteByPaymentID(long paymentId, TableType tableType);

}
