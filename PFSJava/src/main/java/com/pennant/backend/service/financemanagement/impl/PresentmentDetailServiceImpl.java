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
 * FileName    		:  PresentmentDetailServiceImpl.java                                                   * 	  
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
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
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceMainDAO financeMainDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	@Autowired(required = false)
	private PresentmentRequest presentmentRequest;

	@Autowired
	private RepaymentProcessUtil repaymentProcessUtil;
	@Autowired
	private CustomerDetailsService customerDetailsService;
	@Autowired
	private FinanceProfitDetailDAO profitDetailsDAO;
	@Autowired
	private FinanceTypeDAO financeTypeDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public RepaymentPostingsUtil getRepaymentPostingsUtil() {
		return repaymentPostingsUtil;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public ChequeDetailDAO getChequeDetailDAO() {
		return chequeDetailDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Override
	public PresentmentHeader getPresentmentHeader(long id) {
		return this.getPresentmentDetailDAO().getPresentmentHeader(id, "_View");
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type) {
		return getPresentmentDetailDAO().getPresentmentDetailsList(presentmentId, isExclude, isApprove, type);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		getPresentmentDetailDAO().updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		getPresentmentDetailDAO().updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		getPresentmentDetailDAO().updatePresentmentIdAsZero(presentmentId);
	}

	@Override
	public long getSeqNumber(String tableNme) {
		return getPresentmentDetailDAO().getSeqNumber(tableNme);
	}

	@Override
	public String getPaymenyMode(String presentmentRef) {
		return getPresentmentDetailDAO().getPaymenyMode(presentmentRef);
	}

	@Override
	public void updateFinanceDetails(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail detail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef,
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
			presentmentDetail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef, "_PDCview");
		} else {
			presentmentDetail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef, "_View");
		}
		return presentmentDetail;
	}

	private void updateChequeStatus(long chequeDetailsId, String status) {
		chequeDetailDAO.updateChequeStatus(chequeDetailsId, status);
	}

	@Override
	public void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction,
			long presentmentId, long partnerBankId, LoggedInUser userDetails, boolean isPDC) throws Exception {
		logger.debug(Literal.ENTERING);

		if ("Save".equals(userAction)) {
			savePresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Submit".equals(userAction)) {
			submitPresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Approve".equals(userAction)) {

			// update presentment id as zero
			List<PresentmentDetail> listPrstitems = this.presentmentDetailDAO.getExcludeDetails(presentmentId);
			if (listPrstitems != null && !listPrstitems.isEmpty()) {
				for (PresentmentDetail presentmentDetail : listPrstitems) {
					updatePresentmentIdAsZero(presentmentDetail.getId());
				}
			}
			approvePresentments(presentmentId, userDetails, isPDC);
		} else if ("Resubmit".equals(userAction)) {
			resubmitPresentments(presentmentId, partnerBankId);
		} else if ("Cancel".equals(userAction)) {
			cancelPresentments(presentmentId);
		}
		logger.debug(Literal.LEAVING);
	}

	private void savePresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void submitPresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_AWAITING_CONF,
				partnerBankId);
	}

	private void resubmitPresentments(long presentmentId, long partnerBankId) {
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void approvePresentments(long presentmentId, LoggedInUser userDetails, boolean isPDC) throws Exception {

		//update presentment id as zero
		List<PresentmentDetail> listPrstitems = getPresentmentDetailDAO().getExcludeDetails(presentmentId);
		if (listPrstitems != null && !listPrstitems.isEmpty()) {
			for (PresentmentDetail presentmentDetail : listPrstitems) {
				updatePresentmentIdAsZero(presentmentDetail.getId());
			}
		}

		processDetails(presentmentId, userDetails, isPDC);
	}

	private void cancelPresentments(long presentmentId) {
		List<PresentmentDetail> list = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentId, false);
		if (list != null && !list.isEmpty()) {
			for (PresentmentDetail item : list) {
				if (item.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
				}
				updatePresentmentIdAsZero(item.getId());

				String paymentMode = this.getPresentmentDetailDAO().getPaymenyMode(item.getPresentmentRef());
				if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
					updateChequeStatus(item.getMandateId(), PennantConstants.CHEQUESTATUS_NEW);
				}
			}
		}
		this.getPresentmentDetailDAO().deletePresentmentDetails(presentmentId);
		this.getPresentmentDetailDAO().deletePresentmentHeader(presentmentId);
	}

	@Override
	public PresentmentDetail presentmentCancellation(String presentmentRef, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentDetail presentmentDetail = null;
		try {
			String paymentMode = this.getPresentmentDetailDAO().getPaymenyMode(presentmentRef);
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
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(
				getPresentmentDetailDAO(), finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePDCPresentments(presentmentHeader);
	}

	private String savePresentments(PresentmentHeader presentmentHeader) throws Exception {
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(
				getPresentmentDetailDAO(), finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePresentments(presentmentHeader);
	}

	// Processing the presentment details
	public void processDetails(long presentmentId, LoggedInUser userDetails, boolean isPDC) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Long> idList = new ArrayList<Long>();
		List<Long> idExcludeEmiList = new ArrayList<Long>();
		boolean isError = false;
		List<PresentmentDetail> detailList = getPresentmentDetailDAO().getPresentmentDetail(presentmentId, true);
		if (detailList != null && !detailList.isEmpty()) {
			for (PresentmentDetail detail : detailList) {
				if (DateUtility.compare(DateUtility.getAppDate(), detail.getSchDate()) >= 0) {
					try {

						if (detail.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
							processReceipts(detail, true);
							idExcludeEmiList.add(detail.getId());
						} else {
							processReceipts(detail, false);
							idList.add(detail.getId());
						}

					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						isError = true;
						throw e;
					}

				} else {
					if (detail.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
						idExcludeEmiList.add(detail.getId());
					} else {
						idList.add(detail.getId());
					}
				}
			}

			// Storing the presentment data into bajaj inteface tables

			try {
				presentmentRequest.sendReqest(idList, idExcludeEmiList, presentmentId, isError, isPDC);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				throw e;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Create a new Receipt
	 * 
	 * @param detail
	 * @param userDetails
	 * @param header
	 * @throws Exception
	 */
	public void processReceipts(PresentmentDetail presentmentDetail) throws Exception {

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();

		Date appDate = DateUtility.getAppDate();

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

		if (presentmentDetail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getPresentmentAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetails.add(receiptDetail);

		}

		if (presentmentDetail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getAdvanceAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetail.setNoReserve(false);
			receiptDetails.add(receiptDetail);
		}

		header.setReceiptDetails(receiptDetails);

		header.setRemarks("");
		finReceiptData.setReceiptHeader(header);
		finReceiptData.setFinReference(presentmentDetail.getFinReference());
		finReceiptData.setSourceId("");
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(presentmentDetail.getFinReference(), "_AView",
				false);
		CustomerDetails custDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true,
				"_AView");
		List<FinanceScheduleDetail> scheduleDetails = financeScheduleDetailDAO
				.getFinScheduleDetails(presentmentDetail.getFinReference(), "_AView", false);
		FinanceProfitDetail profitDetail = profitDetailsDAO
				.getFinProfitDetailsById(presentmentDetail.getFinReference());
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(financeMain.getFinType(), "_AView");
		repaymentProcessUtil.calcualteAndPayReceipt(financeMain, custDetails.getCustomer(), scheduleDetails, null,
				profitDetail, header, financeType, presentmentDetail.getSchDate(), appDate);
		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			getPresentmentDetailDAO().updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}
	}

	/**
	 * Fetching the Finance and customer details for sending the bounce notification to customer.
	 */
	@Override
	public FinanceDetail getFinanceDetailsByRef(String finReference) {
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "_AView", false);
		CustomerDetails customerDetails = customerDetailsService.getCustomerChildDetails(financeMain.getCustID(),
				"_AView");

		finScheduleData.setFinanceMain(financeMain);
		financeDetail.setCustomerDetails(customerDetails);
		financeDetail.setFinScheduleData(finScheduleData);
		return financeDetail;
	}

	public void processReceipts(PresentmentDetail presentmentDetail, boolean isFullEMIPresent) throws Exception {

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();

		Date appDate = DateUtility.getAppDate();

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

		if (presentmentDetail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getPresentmentAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetails.add(receiptDetail);

		}

		if (presentmentDetail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getAdvanceAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetail.setNoReserve(false);
			receiptDetails.add(receiptDetail);
		}

		header.setReceiptDetails(receiptDetails);

		header.setRemarks("");
		finReceiptData.setReceiptHeader(header);
		finReceiptData.setFinReference(presentmentDetail.getFinReference());
		finReceiptData.setSourceId("");
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(presentmentDetail.getFinReference(), "_AView",
				false);
		CustomerDetails custDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true,
				"_AView");
		List<FinanceScheduleDetail> scheduleDetails = financeScheduleDetailDAO
				.getFinScheduleDetails(presentmentDetail.getFinReference(), "_AView", false);
		FinanceProfitDetail profitDetail = profitDetailsDAO
				.getFinProfitDetailsById(presentmentDetail.getFinReference());
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(financeMain.getFinType(), "_AView");
		repaymentProcessUtil.calcualteAndPayReceipt(financeMain, custDetails.getCustomer(), scheduleDetails, null,
				profitDetail, header, financeType, presentmentDetail.getSchDate(), appDate);
		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			presentmentDetailDAO.updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}
	}

}