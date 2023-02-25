package com.pennant.pff.autorefund.service;

import com.pennant.app.core.CustEODEvent;

public interface AutoRefundService {

	void loadAutoRefund(CustEODEvent cee);

	void executeRefund(CustEODEvent cee);

	void updateRefunds(CustEODEvent cee);

}