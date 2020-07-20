/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PresentmentDetailServiceImpl.java                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.PresentmentRequest;

/**
 * Service implementation for methods that depends on <b>PresentmentHeader</b>.<br>
 */
public class PresentmentDetailServiceImpl extends GenericService<PresentmentHeader>
		implements PresentmentDetailService {
	private static final Logger logger = Logger.getLogger(PresentmentDetailServiceImpl.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ReceiptCancellationService receiptCancellationService;
	private ReceiptCalculator receiptCalculator;
	private ReceiptService receiptService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceMainDAO financeMainDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private PresentmentRequest defaultPresentmentRequest;
	private PresentmentRequest presentmentRequest;
	private RepaymentProcessUtil repaymentProcessUtil;
	private CustomerDetailsService customerDetailsService;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CustomerDAO customerDAO;

	@Override
	public PresentmentHeader getPresentmentHeader(long id) {
		return this.presentmentDetailDAO.getPresentmentHeader(id, "_View");
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type) {
		return presentmentDetailDAO.getPresentmentDetailsList(presentmentId, isExclude, isApprove, type);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		presentmentDetailDAO.updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		presentmentDetailDAO.updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		presentmentDetailDAO.updatePresentmentIdAsZero(presentmentId);
	}

	@Override
	public void updatePresentmentIdAsZero(List<Long> presentmentIds) {
		List<List<Long>> idList = new ArrayList<>(1);

		if (presentmentIds.size() > PennantConstants.BULKPROCESSING_SIZE) {
			idList = ListUtils.partition(presentmentIds, PennantConstants.BULKPROCESSING_SIZE);
		} else {
			idList.add(presentmentIds);
		}

		for (List<Long> list : idList) {
			presentmentDetailDAO.updatePresentmentIdAsZero(list);
		}
	}

	@Override
	public long getSeqNumber(String tableNme) {
		return presentmentDetailDAO.getSeqNumber(tableNme);
	}

	@Override
	public String getPaymenyMode(String presentmentRef) {
		return presentmentDetailDAO.getPaymenyMode(presentmentRef);
	}

	@Override
	public void updateFinanceDetails(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail detail = this.presentmentDetailDAO.getPresentmentDetail(presentmentRef,
				TableType.MAIN_TAB.getSuffix());
		List<FinanceScheduleDetail> list = financeScheduleDetailDAO.getFinScheduleDetails(detail.getFinReference(),
				TableType.MAIN_TAB.getSuffix(), false);
		boolean isFinactive = repaymentPostingsUtil.isSchdFullyPaid(detail.getFinReference(), list);

		if (isFinactive) {
			financeMainDAO.updateMaturity(detail.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED, false);
			profitDetailsDAO.updateFinPftMaturity(detail.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED,
					false);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public PresentmentDetail getPresentmentDetailsByMode(String presentmentRef, String paymentMode) {
		PresentmentDetail presentmentDetail;
		if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
			presentmentDetail = this.presentmentDetailDAO.getPresentmentDetail(presentmentRef, "_PDCview");
		} else {
			presentmentDetail = this.presentmentDetailDAO.getPresentmentDetail(presentmentRef, "_View");
		}
		return presentmentDetail;
	}

	private void updateChequeStatus(long chequeDetailsId, String status) {
		chequeDetailDAO.updateChequeStatus(chequeDetailsId, status);
	}

	@Override
	public void updatePresentmentDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);

		if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
			throw new AppException(Labels.getLabel("Amortization_EOD_Check"));
		}

		long presentmentId = presentmentHeader.getId();
		long partnerBankId = presentmentHeader.getPartnerBankId();
		String userAction = presentmentHeader.getUserAction();

		if ("Save".equals(userAction)) {
			savePresentmentData(presentmentHeader);
		} else if ("Submit".equals(userAction)) {
			submitPresentments(presentmentHeader);
		} else if ("Approve".equals(userAction)) {
			approvePresentments(presentmentHeader);
		} else if ("Resubmit".equals(userAction)) {
			resubmitPresentments(presentmentId, partnerBankId);
		} else if ("Cancel".equals(userAction)) {
			cancelPresentments(presentmentId);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveModifiedPresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId) {
		if (CollectionUtils.isNotEmpty(includeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (CollectionUtils.isNotEmpty(excludeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void savePresentmentData(PresentmentHeader presentmentHeader) {
		List<Long> includeList = presentmentHeader.getIncludeList();
		List<Long> excludeList = presentmentHeader.getExcludeList();
		long presentmentId = presentmentHeader.getId();
		long partnerBankId = presentmentHeader.getPartnerBankId();

		if (CollectionUtils.isNotEmpty(includeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, includeList, 0);
		}

		if (CollectionUtils.isNotEmpty(excludeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}

		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void submitPresentments(PresentmentHeader presentmentHeader) {
		List<Long> includeList = presentmentHeader.getIncludeList();
		List<Long> excludeList = presentmentHeader.getExcludeList();
		long presentmentId = presentmentHeader.getId();
		long partnerBankId = presentmentHeader.getPartnerBankId();

		if (CollectionUtils.isNotEmpty(includeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, includeList, 0);
		}

		if (CollectionUtils.isNotEmpty(excludeList)) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}

		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_AWAITING_CONF,
				partnerBankId);
	}

	private void resubmitPresentments(long presentmentId, long partnerBankId) {
		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void approvePresentments(PresentmentHeader presentmentHeader) {
		List<Long> excludeList = presentmentHeader.getExcludeList();
		long presentmentId = presentmentHeader.getId();

		try {
			if (CollectionUtils.isEmpty(excludeList)) {
				processDetails(presentmentHeader);
				return;
			}

			updatePresentmentIdAsZero(excludeList);

			List<PresentmentDetail> excludePresentmentList = presentmentDetailDAO
					.getPresentmensByExcludereason(presentmentId, RepayConstants.PEXC_MANUAL_EXCLUDE);

			if (CollectionUtils.isEmpty(excludePresentmentList)) {
				processDetails(presentmentHeader);
				return;
			}

			List<PresentmentDetail> excessAmountList = new ArrayList<>();
			List<Long> chequeStatusList = new ArrayList<>();

			for (PresentmentDetail item : excludePresentmentList) {
				if (item.getExcessID() != 0) {
					excessAmountList.add(item);

					if (excessAmountList.size() == PennantConstants.BULKPROCESSING_SIZE) {
						finExcessAmountDAO.batchUpdateExcessAmount(excessAmountList);
						excessAmountList = new ArrayList<>();
					}
				}

				if (MandateConstants.TYPE_PDC.equals(presentmentHeader.getMandateType())) {
					chequeStatusList.add(item.getMandateId());

					if (chequeStatusList.size() == PennantConstants.BULKPROCESSING_SIZE) {
						chequeDetailDAO.batchUpdateChequeStatus(chequeStatusList, PennantConstants.CHEQUESTATUS_NEW);
						chequeStatusList = new ArrayList<>();
					}
				}
			}

			if (!excessAmountList.isEmpty()) {
				finExcessAmountDAO.batchUpdateExcessAmount(excessAmountList);
			}

			if (!chequeStatusList.isEmpty()) {
				chequeDetailDAO.batchUpdateChequeStatus(chequeStatusList, PennantConstants.CHEQUESTATUS_NEW);
			}

			processDetails(presentmentHeader);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	private void cancelPresentments(long presentmentId) {
		List<PresentmentDetail> list = this.presentmentDetailDAO.getPresentmentDetail(presentmentId, false);

		if (list != null && !list.isEmpty()) {
			for (PresentmentDetail item : list) {
				if (item.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
				}
				updatePresentmentIdAsZero(item.getId());

				String paymentMode = this.presentmentDetailDAO.getPaymenyMode(item.getPresentmentRef());
				if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
					updateChequeStatus(item.getMandateId(), PennantConstants.CHEQUESTATUS_NEW);
				}
			}
		}
		//reverse the excess movement
		reverseExcessMovements(presentmentId);

		this.presentmentDetailDAO.deletePresentmentDetails(presentmentId);
		this.presentmentDetailDAO.deletePresentmentHeader(presentmentId);
	}

	private void reverseExcessMovements(long presentmentId) {
		List<FinExcessMovement> advlist = finExcessAmountDAO.getFinExcessAmount(presentmentId);
		if (advlist != null && !advlist.isEmpty()) {
			for (FinExcessMovement finExcessAmount : advlist) {
				//update reserver and delete the movement
				FinExcessAmount movement = finExcessAmountDAO.getFinExcessByID(finExcessAmount.getExcessID());

				movement.setReservedAmt(movement.getReservedAmt().subtract(finExcessAmount.getAmount()));
				BigDecimal amount = movement.getAmount();
				BigDecimal reservedAmt = movement.getReservedAmt();
				BigDecimal utilisedAmt = movement.getUtilisedAmt();
				movement.setBalanceAmt(amount.subtract(reservedAmt).subtract(utilisedAmt));
				finExcessAmountDAO.updateReserveUtilization(movement);
			}
		}
		finExcessAmountDAO.deleteMovemntByPrdID(presentmentId);
	}

	@Override
	public PresentmentDetail presentmentCancellation(String presentmentRef, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentDetail presentmentDetail = null;
		try {
			String paymentMode = this.presentmentDetailDAO.getPaymenyMode(presentmentRef);
			presentmentDetail = getPresentmentDetailsByMode(presentmentRef, paymentMode);

			if (presentmentDetail == null) {
				throw new Exception(PennantJavaUtil.getLabel("label_Presentmentdetails_Notavailable") + presentmentRef);
			}
			updatePresentmentIdAsZero(presentmentDetail.getId());
			if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
				updateChequeStatus(presentmentDetail.getMandateId(), PennantConstants.CHEQUESTATUS_BOUNCE);
			}
			presentmentDetail = this.receiptCancellationService.presentmentCancellation(presentmentDetail, returnCode);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

		return presentmentDetail;
	}

	/*
	 * Extracting the Presentments from Various tables and saving into Presentments PresentmentHeader presentmentHeader
	 */
	@Override
	public String savePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception {
		if (MandateConstants.TYPE_PDC.equals(presentmentHeader.getMandateType())) {
			return savePDCPresentments(presentmentHeader);
		}
		return savePresentments(presentmentHeader);
	}

	private String savePDCPresentments(PresentmentHeader presentmentHeader) throws Exception {
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(presentmentDetailDAO,
				finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePDCPresentments(presentmentHeader);
	}

	private String savePresentments(PresentmentHeader presentmentHeader) throws Exception {
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(presentmentDetailDAO,
				finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePresentments(presentmentHeader);
	}

	private void processDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		long presentmentId = presentmentHeader.getId();

		List<PresentmentDetail> detailList = presentmentDetailDAO.getPresentmentDetail(presentmentId, true);

		if (CollectionUtils.isEmpty(detailList)) {
			return;
		}

		List<Long> idList = new ArrayList<>();
		List<Long> idExcludeEmiList = new ArrayList<>();

		boolean isError = false;

		Date appDate = SysParamUtil.getAppDate();

		for (PresentmentDetail detail : detailList) {
			long detailID = detail.getId();
			detail.setAppDate(appDate);

			if (DateUtil.compare(appDate, detail.getSchDate()) >= 0) {
				try {

					if (detail.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
						processAdvaceEMI(detail, false);
						idExcludeEmiList.add(detailID);
					} else {
						if (detail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
							processReceipts(detail, false);
						}
						if (detail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
							processAdvaceEMI(detail, false);
						}

						idList.add(detailID);
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					isError = true;
				}

			} else {
				if (detail.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
					idExcludeEmiList.add(detailID);
				} else {
					idList.add(detailID);
				}
			}
		}

		try {
			String presentmentRef = presentmentHeader.getReference();
			String bankAccNo = presentmentHeader.getPartnerAcctNumber();
			boolean isPDC = MandateConstants.TYPE_PDC.equals(presentmentHeader.getMandateType());

			getPresentmentRequest().sendReqest(idList, idExcludeEmiList, presentmentId, isError, isPDC, presentmentRef,
					bankAccNo);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void processReceipts(PresentmentDetail presentmentDetail) throws Exception {

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);
		header.setReference(presentmentDetail.getFinReference());
		header.setReceiptDate(presentmentDetail.getSchDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(PennantConstants.List_Select);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(presentmentDetail.getPresentmentAmt().add(presentmentDetail.getAdvanceAmt()));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		header.setLogSchInPresentment(true);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail receiptDetail = new FinReceiptDetail();
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
		receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
		receiptDetail.setAmount(presentmentDetail.getPresentmentAmt());
		receiptDetail.setValueDate(presentmentDetail.getSchDate());
		receiptDetail.setReceivedDate(SysParamUtil.getAppDate());
		receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
		receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
		receiptDetails.add(receiptDetail);

		header.setReceiptDetails(receiptDetails);

		header.setRemarks("");
		finReceiptData.setReceiptHeader(header);
		finReceiptData.setFinReference(presentmentDetail.getFinReference());
		finReceiptData.setSourceId("");
		finReceiptData.setValueDate(presentmentDetail.getSchDate());
		finReceiptData = receiptCalculator.recalAutoAllocation(finReceiptData, presentmentDetail.getSchDate(), true);
		finReceiptData = receiptService.calculateRepayments(finReceiptData, true);
		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			presentmentDetailDAO.updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}
	}

	/**
	 * Fetching the Finance and customer details for sending the bounce notification to customer.
	 */
	@Override
	public FinanceDetail getFinanceDetailsByRef(String finReference) {
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
		CustomerDetails customerDetails = customerDetailsService.getCustomerChildDetails(financeMain.getCustID(),
				"_AView");

		finScheduleData.setFinanceMain(financeMain);
		financeDetail.setCustomerDetails(customerDetails);
		financeDetail.setFinScheduleData(finScheduleData);
		return financeDetail;
	}

	private void processReceipts(PresentmentDetail presentmentDetail, boolean isFullEMIPresent) throws Exception {
		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();
		Date appDate = presentmentDetail.getAppDate();

		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);
		String finReference = presentmentDetail.getFinReference();

		header.setReference(finReference);
		header.setReceiptDate(presentmentDetail.getSchDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(presentmentDetail.getPresentmentAmt().add(presentmentDetail.getAdvanceAmt()));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		header.setLogSchInPresentment(true);
		header.setActFinReceipt(true);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail receiptDetail = new FinReceiptDetail();

		if (presentmentDetail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getPresentmentAmt());
			receiptDetail.setDueAmount(presentmentDetail.getPresentmentAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetails.add(receiptDetail);
		}

		header.setReceiptDetails(receiptDetails);
		header.setRemarks("");

		finReceiptData.setReceiptHeader(header);
		finReceiptData.setFinReference(finReference);
		finReceiptData.setSourceId("");
		List<FinanceScheduleDetail> scheduleDetails = null;

		FinanceMain fm = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
		Customer customer = customerDAO.getCustomerForPresentment(fm.getCustID());
		scheduleDetails = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false);
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		String repayHierarchy = financeTypeDAO.getRepayHierarchy(fm.getFinType());

		repaymentProcessUtil.calcualteAndPayReceipt(fm, customer, scheduleDetails, null, profitDetail, header,
				repayHierarchy, presentmentDetail.getSchDate(), appDate);

		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			presentmentDetailDAO.updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}
	}

	private void processAdvaceEMI(PresentmentDetail presentmentDetail, boolean isFullEMIPresent) throws Exception {

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();
		BigDecimal advanceAmt = presentmentDetail.getAdvanceAmt();
		String finReference = presentmentDetail.getFinReference();
		Date schDate = presentmentDetail.getSchDate();
		Date appDate = presentmentDetail.getAppDate();
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);

		header.setReference(finReference);
		header.setReceiptDate(schDate);
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(presentmentDetail.getPresentmentAmt().add(advanceAmt));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		header.setLogSchInPresentment(true);
		header.setActFinReceipt(true);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
			FinReceiptDetail rd = new FinReceiptDetail();
			rd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rd.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
			rd.setPayAgainstID(presentmentDetail.getExcessID());
			rd.setAmount(advanceAmt);
			rd.setDueAmount(advanceAmt);
			rd.setValueDate(schDate);
			rd.setReceivedDate(appDate);
			rd.setPartnerBankAc(presentmentDetail.getAccountNo());
			rd.setPartnerBankAcType(presentmentDetail.getAcType());
			rd.setNoReserve(false);
			receiptDetails.add(rd);

			XcessPayables xp = new XcessPayables();
			xp.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
			xp.setAmount(advanceAmt);
			xp.setTotPaidNow(advanceAmt);
			header.getXcessPayables().add(xp);
		}

		header.setReceiptDetails(receiptDetails);
		header.setRemarks("");

		finReceiptData.setReceiptHeader(header);
		finReceiptData.setFinReference(finReference);
		finReceiptData.setSourceId("");
		List<FinanceScheduleDetail> scheduleDetails = null;

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
		Customer customer = customerDAO.getCustomerForPresentment(financeMain.getCustID());
		scheduleDetails = financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false);
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		String repayHierarchy = financeTypeDAO.getRepayHierarchy(financeMain.getFinType());

		repaymentProcessUtil.calcualteAndPayReceipt(financeMain, customer, scheduleDetails, null, profitDetail, header,
				repayHierarchy, schDate, appDate);
	}

	@Override
	public PresentmentDetail getPresentmentDetail(String presentmentRef) {
		return presentmentDetailDAO.getPresentmentDetail(presentmentRef, "");
	}

	private PresentmentRequest getPresentmentRequest() {
		return presentmentRequest == null ? defaultPresentmentRequest : presentmentRequest;
	}

	@Override
	public PresentmentDetail getPresentmentDetailByFinRefAndPresID(String finReference, long presentmentId) {
		return presentmentDetailDAO.getPresentmentDetailByFinRefAndPresID(finReference, presentmentId, "");
	}

	@Override
	public boolean searchIncludeList(long presentmentId, int excludereason) {
		return presentmentDetailDAO.searchIncludeList(presentmentId, excludereason);
	}

	@Override
	public List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude) {
		return presentmentDetailDAO.getExcludePresentmentDetailIdList(presentmentId, isExclude);

	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setDefaultPresentmentRequest(PresentmentRequest defaultPresentmentRequest) {
		this.defaultPresentmentRequest = defaultPresentmentRequest;
	}

	@Autowired(required = false)
	@Qualifier(value = "presentmentRequest")
	public void setPresentmentRequest(PresentmentRequest presentmentRequest) {
		this.presentmentRequest = presentmentRequest;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Override
	public List<PresentmentHeader> getPresenmentHeaderList(Date fromDate, Date toDate, int status) {
		return this.presentmentDetailDAO.getPresentmentHeaderList(fromDate, toDate, status);
	}

	@Override
	public List<Long> getIncludeList(long id) {
		return this.presentmentDetailDAO.getIncludeList(id);
	}

	@Override
	public List<Long> getExcludeList(long id) {
		return this.presentmentDetailDAO.getExcludeList(id);
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}