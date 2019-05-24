package com.pennant.Interface.service.impl;

import java.math.BigDecimal;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.ManagerChequeInterfaceService;
import com.pennant.equation.process.ManagerChequeProcess;

public class ManagerChequeInterfaceServiceEquationImpl implements ManagerChequeInterfaceService {
	private static Logger logger = Logger.getLogger(ManagerChequeInterfaceServiceEquationImpl.class);

	protected ManagerChequeProcess managerChequeProcess;

	/**
	 * Method for validate the Cheque Number
	 * 
	 * @param accountNum
	 * @param chequeNum
	 * @throws AccountNotFoundException
	 */
	public void validateChequeNumber(String accountNum, String chequeNum) throws AccountNotFoundException {
		logger.debug("Entering");
		try {
			// Connecting to CoreBanking Interface
			if (managerChequeProcess != null) {
				managerChequeProcess.validateChequeNumber(accountNum, chequeNum);
			}
		} catch (AccountNotFoundException e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for validate the Cheque Number
	 * 
	 * @param accountNum
	 * @param chequeNo
	 * @param chqAmount
	 * @param draftCcy
	 * @throws AccountNotFoundException
	 */
	public String addStopOrderInEquation(String accountNum, String chequeNo, BigDecimal chqAmount, String draftCcy)
			throws AccountNotFoundException {
		logger.debug("Entering");
		/*
		 * try { //Connecting to CoreBanking Interface return getManagerChequeProcess().addStopOrder(accountNum,
		 * chequeNo, chqAmount, draftCcy); } catch (AccountNotFoundException e) { logger.error("Exception: ", e); throw
		 * e; } finally { logger.debug("Leaving"); }
		 */

		return "0099";// AHB
	}

	@Autowired(required = false)
	public void setManagerChequeProcess(ManagerChequeProcess managerChequeProcess) {
		this.managerChequeProcess = managerChequeProcess;
	}

}
