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
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
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
		logger.info("Provision Calculation started for the Customer ID {}", strCustId);

		Date eodDate = custEODEvent.getEodDate();
		Date monthEnd = DateUtil.getMonthEnd(eodDate);
		if (!(DateUtil.compare(eodDate, monthEnd) == 0)) {
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

				provisionDet.setProvisionRate(provision.getProvisionRate());
				provisionDet.setAssetCode(provision.getAssetCode());
				provisionDet.setAssetStageOrder(provision.getAssetStageOrder());
				provisionDet.setCollateralValue(provision.getCollateralValue());
				provisionDet.setNpa(provision.isNpa());
				provisionDet.setManualProvision(false);
				provisionDet.getProvisionAmounts().addAll(provision.getProvisionAmounts());

				findProvision(finEODEvent, valueDate, provisionBooks, provisionDet);
			}
		} else {
			for (FinEODEvent finEODEvent : finEODEvents) {
				findProvision(finEODEvent, valueDate, provisionBooks, null);
			}
		}

		logger.info("Provision Calculation completd for the Customer ID {}", strCustId);
		return custEODEvent;

	}

	private FinEODEvent findProvision(FinEODEvent finEODEvent, Date valueDate, String provisionBooks,
			Provision provision) throws SQLException {
		String finReference = finEODEvent.getFinProfitDetail().getFinReference();

		logger.info("Provision Calculation started for the FinReference  {}", finReference);

		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		Provision oldProvision = provisionDAO.getProvisionById(finReference, TableType.MAIN_TAB, false);
		if (oldProvision != null && oldProvision.isManualProvision()) {
			return finEODEvent;
		}

		if (provision == null) {
			provision = getProvision(finEODEvent, provisionBooks);
		}

		if (provision == null) {
			logger.info("Provision Calculation completed for the FinReference  {}", finReference);

			return finEODEvent;
		}

		provision.setFinReference(pftDetail.getFinReference());
		provision.setFinBranch(pftDetail.getFinBranch());
		provision.setFinType(pftDetail.getFinType());
		provision.setCustID(pftDetail.getCustId());
		provision.setProvisionDate(valueDate);

		Date lastFullypaid = pftDetail.getFullPaidDate();

		if (lastFullypaid == null) {
			lastFullypaid = pftDetail.getFinStartDate();
		}

		provision.setLastFullyPaidDate(lastFullypaid);
		provision.setDueFromDate(pftDetail.getPrvODDate());
		provision.setDueDays(pftDetail.getCurODDays());

		provision.setOutStandPrincipal(pftDetail.getTotalPriBal());
		provision.setOutStandProfit(pftDetail.getTotalPftBal());

		List<FinanceScheduleDetail> schdules = finEODEvent.getFinanceScheduleDetails();
		calculateProvisionAmount(valueDate, schdules, provision);

		provision.setProfitAccruedAndNotDue(pftDetail.getAmzTillLBD());
		// Executing accounting process
		setAssetMovement(provision, oldProvision, valueDate);

		provision.setCurrBucket(pftDetail.getDueBucket());
		provision.setDpd(pftDetail.getCurODDays());
		provision.setLastMntBy(1000);
		provision.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		finEODEvent.getFinProfitDetail().setProvision(true);
		finEODEvent.getProvisions().add(provision);

		logger.info("Provision Calculation completed for the FinReference  {}", finReference);

		return finEODEvent;
	}

	private void calculateProvisionAmount(Date valueDate, List<FinanceScheduleDetail> schdules, Provision provision) {
		Date monthStart = DateUtil.getMonthStart(valueDate);
		Date monthEnd = DateUtil.getMonthEnd(valueDate);

		BigDecimal provisionedAmt = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schdules) {
			if (schd.getSchDate().compareTo(monthStart) >= 0 && schd.getSchDate().compareTo(monthEnd) <= 0) {
				provisionedAmt = schd.getClosingBalance();
				break;
			}
		}

		BigDecimal principalDue = BigDecimal.ZERO;
		BigDecimal profitAccruedAndDue = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schdules) {
			if (schd.getSchDate().compareTo(valueDate) <= 0) {
				principalDue = principalDue.add(schd.getPrincipalSchd());
				principalDue = principalDue.subtract(schd.getSchdPriPaid());

				profitAccruedAndDue = profitAccruedAndDue.add(schd.getProfitSchd());
				profitAccruedAndDue = profitAccruedAndDue.subtract(schd.getSchdPftPaid());
				profitAccruedAndDue = profitAccruedAndDue.subtract(schd.getSchdPftWaiver());

			}
		}
		provisionedAmt = provisionedAmt.add(principalDue).add(profitAccruedAndDue);
		provision.setClosingBalance(provisionedAmt);
		provision.setProfitAccruedAndDue(profitAccruedAndDue);

		// Calculating Provision Amount
		for (ProvisionAmount provisonAmt : provision.getProvisionAmounts()) {
			BigDecimal provisionAmtCal = provisionedAmt.multiply(provisonAmt.getProvisionPer())
					.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
			provisonAmt.setProvisionAmtCal(provisionAmtCal);
		}

		provision.setProvisionedAmt(provisionedAmt.multiply(provision.getProvisionRate()).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN));

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

		// If Provision details are not available against the loan type.
		if (provisionHeader == null) {
			return provision;
		}

		List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

		// If Provision Percentages are not available against the loan type.
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

		// Getting Provision Rate
		setProvisionAmounts(provisionDetail, provision);

		provision = setProvisionRate(finEODEvent, provision, provisionDetail, provisionBooks);

		provision.setAssetCode(provisionDetail.getAssetCode());
		provision.setAssetStageOrder(provisionDetail.getAssetStageOrder());
		provision.setNpa(provisionDetail.isnPAActive());
		provision.setManualProvision(false);

		return provision;
	}

	private void setProvisionAmounts(NPAProvisionDetail provisionDetail, Provision provision) {

		ProvisionAmount pa = null;

		pa = new ProvisionAmount();
		pa.setProvisionType(ProvisionConstants.PROVISION_BOOKS_REG_SEC);
		pa.setProvisionPer(provisionDetail.getRegSecPerc());
		pa.setAssetCode(provisionDetail.getAssetCode());
		provision.getProvisionAmounts().add(pa);

		pa = new ProvisionAmount();
		pa.setProvisionType(ProvisionConstants.PROVISION_BOOKS_REG_UN_SEC);
		pa.setProvisionPer(provisionDetail.getRegUnSecPerc());
		pa.setAssetCode(provisionDetail.getAssetCode());
		provision.getProvisionAmounts().add(pa);

		pa = new ProvisionAmount();
		pa.setProvisionType(ProvisionConstants.PROVISION_BOOKS_INT_SEC);
		pa.setProvisionPer(provisionDetail.getIntSecPerc());
		pa.setAssetCode(provisionDetail.getAssetCode());
		provision.getProvisionAmounts().add(pa);

		pa = new ProvisionAmount();
		pa.setProvisionType(ProvisionConstants.PROVISION_BOOKS_INT_UN_SEC);
		pa.setProvisionPer(provisionDetail.getIntUnSecPerc());
		pa.setAssetCode(provisionDetail.getAssetCode());
		provision.getProvisionAmounts().add(pa);

	}

	/**
	 * Getting Provision Rate
	 * 
	 * @param finEODEvent
	 * @param provisionDetail
	 * @param provisionBooks
	 * @return
	 */
	private Provision setProvisionRate(FinEODEvent finEODEvent, Provision provision, NPAProvisionDetail provisionDetail,
			String provisionBooks) {

		boolean isSecured = this.collateralAssignmentDAO.isSecuredLoan(finEODEvent.getFinanceMain().getFinReference(),
				TableType.MAIN_TAB);

		if (StringUtils.equals(ProvisionConstants.PROVISION_BOOKS_REG, provisionBooks)) {
			if (isSecured) {
				provision.setProvisionRate(provisionDetail.getRegSecPerc());
			} else {
				provision.setProvisionRate(provisionDetail.getRegUnSecPerc());
			}
		} else if (StringUtils.equals(ProvisionConstants.PROVISION_BOOKS_INT, provisionBooks)) {
			if (isSecured) {
				provision.setProvisionRate(provisionDetail.getIntSecPerc());
			} else {
				provision.setProvisionRate(provisionDetail.getIntUnSecPerc());
			}
		}

		if (isSecured) {
			provision = setCollateralValue(finEODEvent, provision);
		} else {
			provision.setCollateralValue(BigDecimal.ZERO);
		}

		return provision;
	}

	private void setAssetMovement(Provision provision, Provision oldProvision, Date eodValueDate) {
		// Getting Old Provision details
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
	 * Set Collateral Value
	 * 
	 * @param finEODEvent
	 * @param provision
	 */
	private Provision setCollateralValue(FinEODEvent finEODEvent, Provision provision) {
		provision.setCollateralValue(
				this.collateralAssignmentDAO.getCollateralValue(finEODEvent.getFinanceMain().getFinReference()));
		return provision;
	}

	/**
	 * Checking the If NPA repay Heirarchy Required or not against the loan type
	 * 
	 * @param finReference
	 * @return
	 */
	public boolean isNAPRepayHierarchyReq(String finReference) {
		logger.debug(Literal.ENTERING);

		Provision provision = provisionDAO.getProvisionById(finReference, TableType.MAIN_TAB, false);
		if (provision == null) {
			return false;
		}

		NPAProvisionHeader provisionHeader = nPAProvisionHeaderDAO.getNPAProvisionByFintype(provision.getFinType(),
				TableType.MAIN_TAB);

		// If Provision details are not available against the loan type.
		if (provisionHeader == null) {
			return false;
		}

		List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

		// If Provision Percentages are not available against the loan type.
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

			if (provision.getAssetStageOrder() > maxProvision.getAssetStageOrder()) {
				maxProvision.setAssetCode(provision.getAssetCode());
				maxProvision.setAssetStageOrder(provision.getAssetStageOrder());
				maxProvision.setNpa(provision.isNpa());
				maxProvision.setProvisionRate(provision.getProvisionRate());
				maxProvision.setCollateralValue(provision.getCollateralValue());
				maxProvision.getProvisionAmounts().clear();
				maxProvision.getProvisionAmounts().addAll(provision.getProvisionAmounts());
			} else if (provision.getAssetStageOrder() == maxProvision.getAssetStageOrder()) {
				if (provision.isNpa() && !maxProvision.isNpa()) {
					maxProvision.setAssetCode(provision.getAssetCode());
					maxProvision.setAssetStageOrder(provision.getAssetStageOrder());
					maxProvision.setProvisionRate(provision.getProvisionRate());
					maxProvision.setNpa(provision.isNpa());
					maxProvision.setCollateralValue(provision.getCollateralValue());
					maxProvision.getProvisionAmounts().clear();
					maxProvision.getProvisionAmounts().addAll(provision.getProvisionAmounts());
				}
			}
		}

		if (StringUtils.trimToNull(maxProvision.getAssetCode()) == null) {
			return null;
		}
		return maxProvision;
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

				// Process Provision Accounting.
				finEODEvent = processProvAccounting(finEODEvent, custEODEvent, provision);

				// Process Provision Accounting Reversal.
				finEODEvent = processProvAccountingReversal(finEODEvent, custEODEvent, provision);

				// Process NPAChange Accounting.
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
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent = postAccountingEOD(aeEvent);

		provision.setLinkedTranId(aeEvent.getLinkedTranId());
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
		if (aeEvent.getValueDate() == null) {
			aeEvent.setValueDate(custEODEvent.getEodValueDate());
		}

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent = postAccountingEOD(aeEvent);

		provision.setLinkedTranId(aeEvent.getLinkedTranId());
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

		provision.setChgLinkedTranId(aeEvent.getLinkedTranId());
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
		// Old
		// -- PFTAMZ(amz) - TDSCHDPFT(pftS)
		// --TDSCHDPFTBAL(PftSB)
		// --TDSCHDPFTBAL(pftSB)+TOTALPRIBAL(priAB)
		// --TOTALPRIBAL(priAB)-TDSCHDPRI(priS)

		// New
		// TDSCHDPFTBAL(pftSB)+TDSCHDPRIBAL(PriSB) //1 Leg
		// TDSCHDPFTBAL(PftSB) //2 Leg
		// TOTALPRISCHD(pri) - TDSCHDPRI(priS)//3 Leg

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		return aeEvent;
	}

	// Getters and setters
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
