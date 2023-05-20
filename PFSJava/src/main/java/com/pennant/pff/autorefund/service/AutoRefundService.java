package com.pennant.pff.autorefund.service;

import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.AutoRefundLoan;

public interface AutoRefundService {

	AutoRefundLoan getAutoRefundDetails(long finID, EventProperties ep);

	void executeRefund(AutoRefundLoan arl);

	void updateRefunds(AutoRefundLoan arl);

}