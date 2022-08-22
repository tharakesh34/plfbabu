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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.PennantConstants;
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
	private ProvisionDAO provisionDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private AccountEngineExecution engineExecution;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceTypeDAO financeTypeDAO;

	public ProvisionCalculationUtil() {
		super();
	}

	public ErrorDetail processProvCalculations(Provision procProvision, Date dateValueDate, boolean isProvRelated,
			boolean isScrnLvlProc, boolean isFromCore)
			throws IllegalAccessException, InvocationTargetException, InterfaceException {

		logger.debug(Literal.ENTERING);

		BigDecimal provCalculated = BigDecimal.ZERO;

		long finID = procProvision.getFinID();
		String finReference = procProvision.getFinReference();
		FinanceProfitDetail pftDetail = financeProfitDetailDAO.getFinProfitDetailsByRef(finID);
		FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(finID);
		List<FinanceScheduleDetail> schdDetails = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);

		AEEvent aeEvent = AEAmounts.procAEAmounts(financeMain, schdDetails, pftDetail, AccountingEvent.PROVSN,
				dateValueDate, procProvision.getProvisionDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		/*
		 * amountCodes.setProvDue( procProvision.getProvisionDue() == null ? BigDecimal.ZERO :
		 * procProvision.getProvisionDue()); amountCodes.setProvAmt( procProvision.getProvisionedAmt() == null ?
		 * BigDecimal.ZERO : procProvision.getProvisionedAmt());
		 */

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
		aeEvent.setDataMap(dataMap);

		if (isFromCore) {
			// provCalculated = procProvision.getProvisionAmtCal();
		} else {
			/*
			 * if (isScrnLvlProc && procProvision.isUseNFProv()) { //provCalculated = procProvision.getNonFormulaProv();
			 * } else { provCalculated = getEngineExecution().getProvisionExecResults(dataMap); }
			 */
		}
		// Search For Provision Record in case of OverDue
		Provision provision = null;
		boolean isRcdFound = true;
		if (!isProvRelated) {
			// provision = getProvisionDAO().getProvisionById(procProvision.getFinReference(), "");
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
			BigDecimal prvProvCalAmt = null;
			if (!isProvRelated) {
				// prvProvCalAmt = provision.getProvisionAmtCal();
			} else {
				// prvProvCalAmt = procProvision.getProvisionAmtCal();
			}
			provision = procProvision;

			/*
			 * if (!provision.isUseNFProv() && prvProvCalAmt.compareTo(provCalculated) != 0) { provision =
			 * prepareProvisionData(provision, dateValueDate, provCalculated, isScrnLvlProc, amountCodes, 2);
			 * isProceedFurthur = true; }
			 * 
			 * if (provision.isAutoReleaseNFP() && provCalculated.compareTo(BigDecimal.ZERO) == 0) { provision =
			 * prepareProvisionData(provision, dateValueDate, provCalculated, isScrnLvlProc, amountCodes, 3);
			 * isProceedFurthur = true; }
			 * 
			 * if (provision.getProvisionedAmt().compareTo(provision.getNonFormulaProv()) < 0) { provision =
			 * prepareProvisionData(provision, dateValueDate, provCalculated, isScrnLvlProc, amountCodes, 4);
			 * isProceedFurthur = true; }
			 * 
			 * if (isScrnLvlProc && provision.getProvisionedAmt().compareTo(provision.getNonFormulaProv()) > 0) {
			 * provision = prepareProvisionData(provision, dateValueDate, provCalculated, isScrnLvlProc, amountCodes,
			 * 4); isProceedFurthur = true; }
			 */

		}

		ProvisionMovement movement = null;
		ErrorDetail errorDetails = null;
		if (isProceedFurthur) {
			// Provision Movement record update
			/*
			 * if (provision.getProvisionDue().compareTo(BigDecimal.ZERO) == 0) { // Nothing to do , loop repetation }
			 * else { movement = prepareProvisionMovementData(provision, dateValueDate); }
			 */

			// Provision Posting Process for Screen Level Process
			boolean isPostingsSuccess = true;
			if ((isScrnLvlProc || isFromCore) && movement != null) {
				amountCodes
						.setProvDue(movement.getProvisionDue() == null ? BigDecimal.ZERO : movement.getProvisionDue());

				aeEvent.setAccountingEvent(AccountingEvent.PROVSN);
				aeEvent.setValueDate(dateValueDate);
				// aeEvent.setSchdDate(procProvision.getProvisionCalDate());
				Date dateAppDate = SysParamUtil.getAppDate();
				aeEvent.setAppDate(dateAppDate);

				dataMap = new HashMap<String, Object>();
				dataMap = amountCodes.getDeclaredFieldValues();
				aeEvent.setDataMap(dataMap);

				try {
					aeEvent = postingsPreparationUtil.processPostingDetails(aeEvent);
				} catch (AccountNotFoundException e) {
					logger.error(Literal.EXCEPTION, e);
				}

				isPostingsSuccess = aeEvent.isPostingSucess();

				if (!isPostingsSuccess) {
					errorDetails = new ErrorDetail("", PennantConstants.ERR_9999, "E",
							"Provision Posting Details are failed...", null, null);
				} else {
					movement.setProvisionedAmt(movement.getProvisionedAmt().add(movement.getProvisionDue()));
					movement.setProvisionDue(BigDecimal.ZERO);
					movement.setProvisionPostSts("C");
					movement.setLinkedTranId(aeEvent.getLinkedTranId());
					movement.setUserDetails(procProvision.getUserDetails());

					// Update Provision Movement Details
					if (!isFromCore) {
						// getProvisionDAO().updateProvAmt(movement, "");
						provisionMovementDAO.update(movement, "");
					}

				}
			}

			// Provision Details Save or Update
			if (isPostingsSuccess || !isScrnLvlProc) {

				if (isPostingsSuccess && isScrnLvlProc && movement != null) {
					provision.setProvisionedAmt(movement.getProvisionedAmt().add(movement.getProvisionDue()));
					// provision.setProvisionDue(BigDecimal.ZERO);
				}

				if (isFromCore) {
					provision.setFinBranch(financeMain.getFinBranch());
					provision.setFinType(financeMain.getFinType());
					provision.setCustID(financeMain.getCustID());

				}

				if (!isRcdFound) {
					// getProvisionDAO().save(provision, "");
					if (isFromCore) {
						// getProvisionDAO().saveProcessedProvisions(provision);
					}
				} else {
					// getProvisionDAO().update(provision, "");
				}

				// Provision Movement Details
				if (movement != null) {

					if (isScrnLvlProc && movement != null) {
						movement.setProvisionedAmt(movement.getProvisionedAmt().add(movement.getProvisionDue()));
						movement.setProvisionDue(BigDecimal.ZERO);
						movement.setProvisionPostSts("C");
						movement.setLinkedTranId(aeEvent.getLinkedTranId());
					}

					provisionMovementDAO.save(movement, "");
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
			// provision.setProvisionAmtCal(provCalculated);
			// provision.setProvisionDue(provCalculated);
			// provision.setNonFormulaProv(BigDecimal.ZERO);
			if (!isScrnLvlProc) {
				// provision.setUseNFProv(false);
				// provision.setAutoReleaseNFP(false);
			}
		}

		// Scenario 2
		if (scenarioSeq == 2) {
			// provision.setProvisionAmtCal(provCalculated);
			// provision.setProvisionDue(provision.getProvisionAmtCal().subtract(provision.getProvisionedAmt()));
		}

		// Scenario 3
		if (scenarioSeq == 3) {
			// provision.setProvisionAmtCal(BigDecimal.ZERO);
			// provision.setProvisionDue(BigDecimal.ZERO.subtract(provision.getProvisionedAmt()));
		}

		// Scenario 4
		if (scenarioSeq == 4) {
			// provision.setProvisionAmtCal(provision.getNonFormulaProv());
			// provision.setProvisionDue(provision.getProvisionAmtCal().subtract(provision.getProvisionedAmt()));
		}

		// Common Changes for all Scenario's
		// provision.setProvisionCalDate(valueDate);
		// provision.setPrincipalDue(aeAmountCodes.getPriAB());
		// provision.setProfitDue(aeAmountCodes.getPftAB());
		Date curBussDate = SysParamUtil.getAppDate();
		provision.setDueFromDate(DateUtil.addDays(curBussDate, -aeAmountCodes.getODDays()));
		provision.setLastFullyPaidDate(DateUtil.addDays(curBussDate, -aeAmountCodes.getDaysFromFullyPaid()));

		logger.debug(Literal.LEAVING);
		return provision;
	}

	/**
	 * Method for Preparation for Provision Movement Details
	 * 
	 * @param provision
	 * @param valueDate
	 */
	private ProvisionMovement prepareProvisionMovementData(Provision provision, Date valueDate) {
		logger.debug(Literal.ENTERING);

		long finID = provision.getFinID();
		String finReference = provision.getFinReference();
		ProvisionMovement provisionMovement = provisionMovementDAO.getProvisionMovementById(finID, valueDate, "");

		ProvisionMovement movement = new ProvisionMovement();
		movement.setFinReference(finReference);
		movement.setProvMovementDate(valueDate);

		// If Record is not exist in database
		if (provisionMovement == null) {
			movement.setProvMovementSeq(1);
		} else {

			/*
			 * if (provision.getProvisionAmtCal().compareTo(provisionMovement.getProvisionAmtCal()) == 0) {
			 * logger.debug(Literal.LEAVING); return provisionMovement; }
			 */

			movement.setProvMovementSeq(provisionMovement.getProvMovementSeq() + 1);
		}
		movement.setProvCalDate(valueDate);
		movement.setProvisionedAmt(provision.getProvisionedAmt());
		// movement.setProvisionAmtCal(provision.getProvisionAmtCal());
		// movement.setProvisionDue(provision.getProvisionDue());
		movement.setProvisionPostSts("R");
		// movement.setNonFormulaProv(provision.getNonFormulaProv());
		// movement.setUseNFProv(provision.isUseNFProv());
		// movement.setAutoReleaseNFP(provision.isAutoReleaseNFP());
		// movement.setPrincipalDue(provision.getPrincipalDue());
		// movement.setProfitDue(provision.getProfitDue());
		movement.setDueFromDate(provision.getDueFromDate());
		movement.setLastFullyPaidDate(provision.getLastFullyPaidDate());
		movement.setLinkedTranId(0);

		logger.debug(Literal.LEAVING);
		return movement;
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

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

}
