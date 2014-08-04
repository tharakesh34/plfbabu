package com.pennant.app.eod.service;

import java.util.Date;

public interface AmortizationService {
	public void doAccrualCalculation(Object object, Date valueDate, String isDummy) throws Exception;
	public void doAccrualPosting(Object object, Date dateValueDate, String postBranch , String isDummy)throws Exception;
}
