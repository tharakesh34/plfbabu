package com.pennanttech.pff.sod.service;

import com.pennant.backend.model.finance.CustEODEvent;

public interface SODService {
	void calculateClosureAmt(CustEODEvent custEODEvent);

}
