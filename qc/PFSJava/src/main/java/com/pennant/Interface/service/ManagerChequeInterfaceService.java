package com.pennant.Interface.service;

import java.math.BigDecimal;

import javax.security.auth.login.AccountNotFoundException;

public interface ManagerChequeInterfaceService {
	void validateChequeNumber(String accountNum, String chequeNum) throws AccountNotFoundException;

	String addStopOrderInEquation(String accountNum, String chequeNo, BigDecimal chqAmount, String draftCcy)
			throws AccountNotFoundException;
}
