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
 * FileName : NPAService.java *
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.NPAProvisionDetailDAO;
import com.pennant.backend.dao.applicationmaster.NPAProvisionHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ProvisionConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

public class NPAService extends ServiceHelper {
	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = LogManager.getLogger(NPAService.class);

	private NPAProvisionHeaderDAO nPAProvisionHeaderDAO;
	private NPAProvisionDetailDAO nPAProvisionDetailDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;

	/**
	 * Default constructor
	 */
	public NPAService() {
		super();
	}

	/**
	 * Processing NPA provision.
	 * 
	 * @param custEODEvent
	 * @return
	 * @throws Exception
	 */
	public CustEODEvent processProvisions(CustEODEvent custEODEvent) throws Exception {
		String strCustId = custEODEvent.getCustIdAsString();
		logger.info("Provision Calculation started fro the Customer ID {}", strCustId);

		if (!(DateUtil.compare(custEODEvent.getEodDate(), DateUtil.getMonthEnd(custEODEvent.getEodDate())) == 0)) {
			return custEODEvent;
		}

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		String provisionBooks = custEODEvent.getProvisionBooks();

		boolean customerProvision = custEODEvent.isCustomerProvision();
		if (customerProvision) {
			Provision provision = getMaxProvisionAsset(finEODEvents, provisionBooks);

			if (provision == null) {
				return custEODEvent;
			}

			for (FinEODEvent finEODEvent : finEODEvents) {
				Provision provisionDet = new Provision();

				provisionDet.setPrvovisionRate(provision.getPrvovisionRate());
				provisionDet.setAssetCode(provision.getAssetCode());
				provisionDet.setAssetStageOrdr(provision.getAssetStageOrdr());
				provisionDet.setNpa(provision.isNpa());
				provisionDet.setManualProvision(false);

				findProvision(finEODEvent, valueDate, provisionBooks, provision);
			}
		} else {
			for (FinEODEvent finEODEvent : finEODEvents) {
				findProvision(finEODEvent, valueDate, provisionBooks, null);
			}
		}

		logger.info("Provision Calculation completd fro the Customer ID {}", strCustId);
		return custEODEvent;

	}

	private FinEODEvent findProvision(FinEODEvent finEODEvent, Date valueDate, String provisionBooks,
			Provision provision) throws SQLException {
		String finReference = finEODEvent.getFinProfitDetail().getFinReference();

		logger.info("Provision Calculation started fro the FinReference  {}", finReference);

		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		Provision oldProvision = provisionDAO.getProvisionById(finReference, TableType.MAIN_TAB.getSuffix());
		if (oldProvision != null && oldProvision.isManualProvision()) {
			return finEODEvent;
		}

		if (provision == null) {
			provision = getProvision(finEODEvent, provisionBooks);
		}

		if (provision == null) {
			logger.info("Provision Calculation completed fro the FinReference  {}", finReference);

			return finEODEvent;
		}

		provision.setFinReference(pftDetail.getFinReference());
		provision.setFinBranch(pftDetail.getFinBranch());
		provision.setFinType(pftDetail.getFinType());
		provision.setCustID(pftDetail.getCustId());
		provision.setProvisionCalDate(valueDate);
		provision.setPftBal(pftDetail.getTotalPftBal());
		provision.setPriBal(pftDetail.getTotalPriBal());
		provision.setPrincipalDue(pftDetail.getODPrincipal());
		provision.setProfitDue(pftDetail.getODProfit());
		Date lastFullypaid = pftDetail.getFullPaidDate();

		if (lastFullypaid == null) {
			lastFullypaid = pftDetail.getFinStartDate();
		}

		provision.setLastFullyPaidDate(lastFullypaid);
		provision.setDueFromDate(pftDetail.getPrvODDate());
		provision.setDueDays(pftDetail.getCurODDays());

		//Calculating Provision Amount
		BigDecimal provisonAmt = (provision.getPriBal().multiply(provision.getPrvovisionRate()))
				.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

		provision.setProvisionAmtCal(provisonAmt);
		provision.setProvisionedAmt(provisonAmt);

		//Executing accounting process
		setAssetMovement(finEODEvent, provision, oldProvision, valueDate);

		provision.setDpdBucketID(0);
		provision.setLastMntBy(1000);
		provision.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		provision.setProvisionMovement(getProvisionMovement(provision));
		finEODEvent.getFinProfitDetail().setProvision(true);
		finEODEvent.getProvisions().add(provision);

		logger.info("Provision Calculation completed fro the FinReference  {}", finReference);

		return finEODEvent;
	}

	private Provision getProvision(FinEODEvent finEODEvent, String provisionBooks) throws SQLException {
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain financeMain = finEODEvent.getFinanceMain();
		String finType = financeMain.getFinType();
		int dpdDays = pftDetail.getCurODDays();
		NPAProvisionDetail provisionDetail = null;
		Provision provision = null;

		NPAProvisionHeader provisionHeader = nPAProvisionHeaderDAO.getNPAProvisionByFintype(finType,
				TableType.MAIN_TAB);

		//If Provision details are not available against the loan type.
		if (provisionHeader == null) {
			return provision;
		}

		List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

		//If Provision Percentages are not available against the loan type.
		if (CollectionUtils.isEmpty(provisionDetails)) {
			return provision;
		}

		for (int i = 0; i < provisionDetails.size() - 1; i++) {

			if (dpdDays == provisionDetails.get(i + 1).getDPDdays()) {
				provisionDetail = provisionDetails.get(i + 1);
				break;

			}

			if (dpdDays == provisionDetails.get(i).getDPDdays()) {
				provisionDetail = provisionDetails.get(i);
				break;

			}

			if (i == 0 && dpdDays < provisionDetails.get(i).getDPDdays()) {
				provisionDetail = provisionDetails.get(i);
				break;

			}

			if (dpdDays > provisionDetails.get(i).getDPDdays() && dpdDays < provisionDetails.get(i + 1).getDPDdays()) {
				provisionDetail = provisionDetails.get(i + 1);
				break;
			}
		}

		if (provisionDetail == null) {
			return provision;
		}

		provision = new Provision();

		//Getting Provision Rate
		BigDecimal provisionRate = getProvisionRate(finEODEvent, provisionDetail, provisionBooks);
		provision.setPrvovisionRate(provisionRate);
		provision.setAssetCode(provisionDetail.getAssetCode());
		provision.setAssetStageOrdr(provisionDetail.getAssetStageOrder());
		provision.setNpa(provisionDetail.isnPAActive());
		provision.setManualProvision(false);

		return provision;
	}

	private BigDecimal getProvisionRate(FinEODEvent finEODEvent, NPAProvisionDetail provisionDetail,
			String provisionBooks) {

		String finReference = finEODEvent.getFinanceMain().getFinReference();
		boolean isSecured = this.collateralAssignmentDAO.isSecuredLoan(finReference, TableType.MAIN_TAB);

		BigDecimal provisionRate = BigDecimal.ZERO;
		if (ProvisionConstants.PROVISION_BOOKS_REG.equals(ProvisionConstants.PROVISION_BOOKS_REG)) {
			if (isSecured) {
				provisionRate = provisionDetail.getRegSecPerc();
			} else {
				provisionRate = provisionDetail.getRegUnSecPerc();
			}
		} else if (ProvisionConstants.PROVISION_BOOKS_INT.equals(provisionBooks)) {
			if (isSecured) {
				provisionRate = provisionDetail.getIntSecPerc();
			} else {
				provisionRate = provisionDetail.getIntUnSecPerc();
			}
		}

		return provisionRate;
	}

	private void setAssetMovement(FinEODEvent finEODEvent, Provision provision, Provision oldProvision,
			Date eodValueDate) {
		//Getting Old Provision details 
		if (oldProvision == null) {
			provision.setAssetFwdMov(provision.isNpa());
		} else {
			if (!oldProvision.isNpa() && provision.isNpa()) {
				provision.setAssetFwdMov(true);
			} else if (oldProvision.isNpa() && !provision.isNpa()) {
				provision.setAssetBkwMov(true);
			}
		}
	}

	/**
	 * Checking the If NPA repay Heirarchy Required or not against the loan type
	 * 
	 * @param finReference
	 * @return
	 */
	public boolean isNAPRepayHierarchyReq(String finReference) {
		logger.debug(Literal.ENTERING);

		Provision provision = provisionDAO.getProvisionById(finReference, TableType.MAIN_TAB.getSuffix());
		if (provision == null) {
			return false;
		}

		NPAProvisionHeader provisionHeader = nPAProvisionHeaderDAO.getNPAProvisionByFintype(provision.getFinType(),
				TableType.MAIN_TAB);

		//If Provision details are not available against the loan type.
		if (provisionHeader == null) {
			return false;
		}

		List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

		//If Provision Percentages are not available against the loan type.
		if (CollectionUtils.isEmpty(provisionDetails)) {
			return false;
		}

		NPAProvisionDetail npaProvisionDetail = null;
		for (NPAProvisionDetail detail : provisionDetails) {
			if (StringUtils.equals(detail.getAssetCode(), provision.getAssetCode())) {
				npaProvisionDetail = detail;
				break;
			}
		}

		if (npaProvisionDetail == null) {
			return false;
		}

		if (StringUtils.equals(PennantConstants.YES, npaProvisionDetail.getNPARepayApprtnmnt())) {
			return true;
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	/**
	 * Getting Max Provision Assets list
	 * 
	 * @param finEODEvents
	 * @param provisionBooks
	 * @return
	 * @throws SQLException
	 */
	private Provision getMaxProvisionAsset(List<FinEODEvent> finEODEvents, String provisionBooks) throws SQLException {
		Provision maxProvision = new Provision();

		for (FinEODEvent finEODEvent : finEODEvents) {
			Provision provision = getProvision(finEODEvent, provisionBooks);

			if (provision == null) {
				continue;
			}

			if (provision.getAssetStageOrdr() > maxProvision.getAssetStageOrdr()) {
				maxProvision.setAssetCode(provision.getAssetCode());
				maxProvision.setAssetStageOrdr(provision.getAssetStageOrdr());
				maxProvision.setPrvovisionRate(provision.getPrvovisionRate());
				maxProvision.setNpa(provision.isNpa());
			} else if (provision.getAssetStageOrdr() == maxProvision.getAssetStageOrdr()) {
				if (provision.isNpa() && !maxProvision.isNpa()) {
					maxProvision.setAssetCode(provision.getAssetCode());
					maxProvision.setAssetStageOrdr(provision.getAssetStageOrdr());
					maxProvision.setPrvovisionRate(provision.getPrvovisionRate());
					maxProvision.setNpa(provision.isNpa());
				}
			}
		}

		if (StringUtils.trimToNull(maxProvision.getAssetCode()) == null) {
			return null;
		}
		return maxProvision;
	}

	private ProvisionMovement getProvisionMovement(Provision provision) {
		ProvisionMovement movement = new ProvisionMovement();

		movement.setProvCalDate(provision.getProvisionCalDate());
		movement.setFinReference(provision.getFinReference());
		movement.setProvMovementDate(provision.getProvisionCalDate());
		movement.setProvMovementSeq(1);
		movement.setProvisionedAmt(provision.getProvisionedAmt());
		movement.setProvisionAmtCal(provision.getProvisionedAmt());
		movement.setProvisionDue(provision.getProvisionDue());
		movement.setProvisionPostSts("R");
		movement.setNonFormulaProv(provision.getNonFormulaProv());
		movement.setUseNFProv(provision.isUseNFProv());
		movement.setAutoReleaseNFP(provision.isAutoReleaseNFP());
		movement.setPrincipalDue(provision.getPrincipalDue());
		movement.setProfitDue(provision.getProfitDue());
		movement.setDueFromDate(provision.getDueFromDate());
		movement.setLastFullyPaidDate(provision.getLastFullyPaidDate());
		movement.setDueDays(provision.getDueDays());
		movement.setPriBal(provision.getPriBal());

		movement.setAssetCode(provision.getAssetCode());
		movement.setAssetStageOrdr(provision.getAssetStageOrdr());
		movement.setNpa(provision.isNpa());
		movement.setPrvovisionRate(provision.getPrvovisionRate());
		movement.setManualProvision(provision.isManualProvision());

		return movement;
	}

	public CustEODEvent processAccounting(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			List<Provision> provisions = finEODEvent.getProvisions();

			if (CollectionUtils.isEmpty(provisions)) {
				continue;
			}

			for (Provision provision : provisions) {
				Date eodDate = custEODEvent.getEodDate();
				Date monthEnd = DateUtil.getMonthEnd(eodDate);
				if ((DateUtil.compare(eodDate, monthEnd) != 0)) {
					continue;
				}

				//Process Provision Accounting.
				finEODEvent = processProvAccounting(finEODEvent, custEODEvent, provision);

				//Process Provision Accounting Reversal.
				finEODEvent = processProvAccountingReversal(finEODEvent, custEODEvent, provision);

				//Process NPAChange Accounting.
				finEODEvent = processNPAChgAccounting(finEODEvent, custEODEvent, provision);
			}
		}
		return custEODEvent;
	}

	private FinEODEvent processProvAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Provision provision)
			throws Exception {

		String eventCode = AccountEventConstants.ACCEVENT_PROVSN;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return finEODEvent;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		aeEvent.setValueDate(SysParamUtil.getPostDate());
		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvDue(provision.getProvisionDue());
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent = postAccountingEOD(aeEvent);

		provision.setProvLinkedTranId(aeEvent.getLinkedTranId());
		provision.getProvisionMovement().setLinkedTranId(aeEvent.getLinkedTranId());
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private FinEODEvent processProvAccountingReversal(FinEODEvent finEODEvent, CustEODEvent custEODEvent,
			Provision provision) throws Exception {

		String eventCode = AccountEventConstants.ACCEVENT_PRVSN_MN;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return finEODEvent;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		aeEvent.setValueDate(custEODEvent.getProvisionEffectiveDate());

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvDue(provision.getProvisionDue());
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent = postAccountingEOD(aeEvent);

		provision.setProvLinkedTranId(aeEvent.getLinkedTranId());
		provision.getProvisionMovement().setLinkedTranId(aeEvent.getLinkedTranId());
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private FinEODEvent processNPAChgAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Provision provision)
			throws Exception {

		if (!provision.isAssetFwdMov()) {
			return finEODEvent;
		}

		AEEvent aeEvent = executeNPAAccounting(finEODEvent, custEODEvent, provision);

		if (aeEvent == null) {
			return finEODEvent;
		}

		provision.setProvChgLinkedTranId(aeEvent.getLinkedTranId());
		provision.getProvisionMovement().setProvChgLinkedTranId(aeEvent.getLinkedTranId());
		aeEvent = postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private AEEvent executeNPAAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Provision provision)
			throws Exception {

		String eventCode = AccountEventConstants.ACCEVENT_PROVCHG;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		long accountingID = getAccountingID(main, eventCode);
		if (accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return null;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());
		//Old
		//-- PFTAMZ(amz) - TDSCHDPFT(pftS)
		//--TDSCHDPFTBAL(PftSB)
		//--TDSCHDPFTBAL(pftSB)+TOTALPRIBAL(priAB)
		//--TOTALPRIBAL(priAB)-TDSCHDPRI(priS)

		//New 
		//TDSCHDPFTBAL(pftSB)+TDSCHDPRIBAL(PriSB)	//1 Leg
		//TDSCHDPFTBAL(PftSB)	//2 Leg
		//TOTALPRISCHD(pri) - TDSCHDPRI(priS)//3 Leg  

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvDue(provision.getProvisionDue());
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		return aeEvent;
	}

	//Getters and setters
	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setnPAProvisionHeaderDAO(NPAProvisionHeaderDAO nPAProvisionHeaderDAO) {
		this.nPAProvisionHeaderDAO = nPAProvisionHeaderDAO;
	}

	public void setnPAProvisionDetailDAO(NPAProvisionDetailDAO nPAProvisionDetailDAO) {
		this.nPAProvisionDetailDAO = nPAProvisionDetailDAO;
	}
}
