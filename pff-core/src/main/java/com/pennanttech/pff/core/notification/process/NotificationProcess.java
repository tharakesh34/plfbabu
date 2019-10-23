package com.pennanttech.pff.core.notification.process;

import com.pennant.backend.model.finance.PaymentTransaction;

public interface NotificationProcess {

	void invokePaymentsNotifications(PaymentTransaction paymentTransaction);

}
