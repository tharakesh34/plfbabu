package com.pennant.pff.autorefund.service;

import com.pennant.backend.model.finance.CustEODEvent;

public interface AutoRefundService {

	void loadAutoRefund(CustEODEvent cee);

	void executeRefund(CustEODEvent cee);

	void updateRefunds(CustEODEvent cee);

}