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
 * * FileName : FeeWaiverHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * *
 * Modified Date : 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FeeWaiverHeaderDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FeeWaiverCancelService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FeeWaiverHeader</b>.<br>
 * 
 */
public class FeeWaiverCancelServiceImpl implements FeeWaiverCancelService {
	private static Logger logger = LogManager.getLogger(FeeWaiverCancelServiceImpl.class);

	private FeeWaiverHeaderDAO feeWaiverHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private PostingsDAO postingsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	@Override
	public void processFeeWaiverCancellation(long waiverId) {
		logger.debug(Literal.ENTERING);
		// Fetch waiver Data
		FeeWaiverHeader fwh = feeWaiverHeaderDAO.getFeeWaiverHeaderById(waiverId, "");
		if (fwh == null) {
			return;
		}

		postReversalTransactions(fwh, SysParamUtil.getAppDate());

		List<ManualAdviseMovements> movements = manualAdviseDAO.getAdviseMovementsByWaiver(waiverId, "");
		List<FinanceRepayments> repayments = financeRepaymentsDAO.getByFinRefAndWaiverId(fwh.getFinID(), waiverId);
		List<FinanceScheduleDetail> schdDtls = new ArrayList<FinanceScheduleDetail>();
		List<FinODDetails> odDtls = new ArrayList<FinODDetails>();

		// Reverse manual advise waivers
		for (ManualAdviseMovements mov : movements) {
			ManualAdvise ma = new ManualAdvise();
			ma.setAdviseID(mov.getAdviseID());
			ma.setWaivedAmount(ma.getWaivedAmount().negate());
			ma.setWaivedCESS(ma.getWaivedCESS().negate());
			ma.setWaivedCGST(ma.getWaivedCGST().negate());
			ma.setWaivedIGST(ma.getWaivedIGST().negate());
			ma.setWaivedSGST(ma.getWaivedSGST().negate());
			ma.setWaivedUGST(ma.getWaivedUGST().negate());
			manualAdviseDAO.updateAdvPayment(ma, TableType.MAIN_TAB);
		}

		// logic to build list for PFT,PRI,LPP,LPI waivers
		for (FinanceRepayments repay : repayments) {
			if (repay.getFinSchdPriPaid().add(repay.getFinSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0) {
				FinanceScheduleDetail schd = new FinanceScheduleDetail();
				schd.setFinID(fwh.getFinID());
				schd.setSchDate(repay.getFinSchdDate());
				schd.setSchdPftWaiver(repay.getFinSchdPftPaid().negate());
				schd.setSchdPftPaid(repay.getFinSchdPftPaid().negate());
				schd.setSchdPriPaid(repay.getFinSchdPriPaid().negate());
				schd.setTDSPaid(repay.getFinSchdTdsPaid().negate());
				schdDtls.add(schd);
			}
			if (repay.getLpftWaived().add(repay.getPenaltyWaived()).compareTo(BigDecimal.ZERO) > 0) {
				FinODDetails od = new FinODDetails();
				od.setFinID(fwh.getFinID());
				od.setFinODSchdDate(repay.getFinSchdDate());
				od.setTotWaived(repay.getPenaltyWaived().negate());
				od.setTotPenaltyBal(repay.getPenaltyWaived());
				od.setLPIWaived(repay.getLpftWaived().negate());
				od.setLPIBal(repay.getLpftWaived());
				odDtls.add(od);
			}
			if (repay.getPriPenaltyWaived().compareTo(BigDecimal.ZERO) > 0
					|| repay.getPftPenaltyWaived().compareTo(BigDecimal.ZERO) > 0) {
				FinODDetails od = new FinODDetails();
				od.setFinID(fwh.getFinID());
				od.setFinODSchdDate(repay.getFinSchdDate());
				od.setTotWaived((repay.getPriPenaltyWaived().add(repay.getPftPenaltyWaived())).negate());
				od.setTotPenaltyBal(repay.getPriPenaltyWaived().add(repay.getPftPenaltyWaived()));
				od.setPriPenaltyWaived(repay.getPriPenaltyWaived().negate());
				od.setPftPenaltyWaived(repay.getPftPenaltyWaived().negate());
				od.setPriPenaltyBal(repay.getPriPenaltyWaived());
				od.setPftPenaltyBal(repay.getPftPenaltyWaived());
				odDtls.add(od);
			}

		}

		if (CollectionUtils.isNotEmpty(odDtls)) {
			finODDetailsDAO.updateFinODTotals(odDtls);
		}
		if (CollectionUtils.isNotEmpty(schdDtls)) {
			financeScheduleDetailDAO.updateSchdTotals(schdDtls);
		}
		feeWaiverHeaderDAO.updateWaiverStatus(waiverId, "F");
		logger.debug(Literal.LEAVING);

	}

	private long postReversalTransactions(FeeWaiverHeader rh, Date appDate) {
		String waiverId = String.valueOf(rh.getWaiverId());
		List<ReturnDataSet> rdSet = null;

		long postingId = postingsDAO.getPostingId();

		rdSet = postingsPreparationUtil.postReversalsByPostRef(waiverId, postingId, appDate);

		if (CollectionUtils.isNotEmpty(rdSet)) {
			return rdSet.get(0).getLinkedTranId();
		}

		return 0;
	}

	@Override
	public void processConditionalWaiver(Date promisedDate) {

		// Fetch waiver records
		List<FeeWaiverHeader> fwhList = feeWaiverHeaderDAO
				.fetchPromisedFeeWaivers(DateUtility.addDays(SysParamUtil.getAppDate(), -1));
		for (FeeWaiverHeader fwh : fwhList) {
			BigDecimal receiptAmount = finReceiptHeaderDAO.getReceiptAmount(DateUtility.addDays(promisedDate, -1),
					promisedDate);
			if (receiptAmount.compareTo(fwh.getWaiverFullFillAmount()) >= 0) {
				feeWaiverHeaderDAO.updateWaiverStatus(fwh.getWaiverId(), "P");
			} else {
				processFeeWaiverCancellation(fwh.getWaiverId());
			}

		}

	}

	@Autowired
	public void setFeeWaiverHeaderDAO(FeeWaiverHeaderDAO feeWaiverHeaderDAO) {
		this.feeWaiverHeaderDAO = feeWaiverHeaderDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}