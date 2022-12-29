package com.pennanttech.pff.sod.service;

import com.pennant.app.core.CustEODEvent;

public interface SODService {
	void calculateClosureAmt(CustEODEvent custEODEvent);

}
