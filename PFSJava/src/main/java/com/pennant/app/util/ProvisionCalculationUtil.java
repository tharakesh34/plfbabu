/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : ProvisionCalculationUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class ProvisionCalculationUtil implements Serializable {
	private static final long serialVersionUID = 193855810060181970L;
	private static Logger logger = LogManager.getLogger(ProvisionCalculationUtil.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;

	public ProvisionCalculationUtil() {
		super();
	}

	public ErrorDetail processProvCalculations(Provision procProvision, Date dateValueDate, boolean isProvRelated,
			boolean isScrnLvlProc, boolean isFromCore)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug(Literal.ENTERING);

		BigDecimal provCalculated = BigDecimal.ZERO;

		long finID = procProvision.getFinID();

		FinanceProfitDetail pftDetail = financeProfitDetailDAO.getFinProfitDetailsByRef(finID);
		FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(finID);
		List<FinanceScheduleDetail> schdDetails = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);

		AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, schdDetails, pftDetail, AccountingEvent.PROVSN,
				dateValueDate, procProvision.getProvisionDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		// Search For Provision Record in case of OverDue
		Provision provision = null;
		boolean isRcdFound = true;
		if (!isProvRelated) {
			if (provision == null) {
				isRcdFound = false;
			}
		}

		boolean isProceedFurthur = true;
		// Case for Provision Record not Found
		if (!isRcdFound) {

			if (provCalculated.compareTo(BigDecimal.ZERO) == 0) {
				if (!isScrnLvlProc) {
					isProceedFurthur = false;
				}
			}

			if (isProceedFurthur) {
				provision = procProvision;
				provision = prepareProvisionData(provision, dateValueDate, provCalculated, isScrnLvlProc, amountCodes,
						1);
			}
		} else {
			isProceedFurthur = false;

			provision = procProvision;
		}

		ErrorDetail errorDetails = null;
		if (isProceedFurthur) {
			// Provision Posting Process for Screen Level Process
			boolean isPostingsSuccess = true;

			// Provision Details Save or Update
			if (isPostingsSuccess || !isScrnLvlProc) {
				if (isFromCore) {
					provision.setFinBranch(financeMain.getFinBranch());
					provision.setFinType(financeMain.getFinType());
					provision.setCustID(financeMain.getCustID());

				}
			}
		}

		amountCodes = null;

		logger.debug(Literal.LEAVING);

		return errorDetails;
	}

	/**
	 * Method for Preparation for Provision Details
	 * 
	 * @param provision
	 * @param details
	 * @param valueDate
	 * @param provCalculated
	 * @param aeAmountCodes
	 * @param scenarioSeq
	 * @return
	 */
	private Provision prepareProvisionData(Provision provision, Date valueDate, BigDecimal provCalculated,
			boolean isScrnLvlProc, AEAmountCodes aeAmountCodes, int scenarioSeq) {
		logger.debug(Literal.ENTERING);

		// Scenario 1
		if (scenarioSeq == 1) {
			provision.setProvisionedAmt(BigDecimal.ZERO);
		}

		// Common Changes for all Scenario's
		Date curBussDate = SysParamUtil.getAppDate();
		provision.setDueFromDate(DateUtil.addDays(curBussDate, -aeAmountCodes.getODDays()));
		provision.setLastFullyPaidDate(DateUtil.addDays(curBussDate, -aeAmountCodes.getDaysFromFullyPaid()));

		logger.debug(Literal.LEAVING);
		return provision;
	}

	public static void setLogger(Logger logger) {
		ProvisionCalculationUtil.logger = logger;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
}
