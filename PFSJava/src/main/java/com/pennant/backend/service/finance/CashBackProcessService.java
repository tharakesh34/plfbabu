package com.pennant.backend.service.finance;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.Promotion;

public interface CashBackProcessService {

	void createCashBackAdvice(FinanceMain finMain, Promotion promotion, Date appDate);

}
