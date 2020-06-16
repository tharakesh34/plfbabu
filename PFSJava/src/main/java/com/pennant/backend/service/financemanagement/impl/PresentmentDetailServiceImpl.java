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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
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
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
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
	private ReceiptCalculator receiptCalculator;
	private ReceiptService receiptService;
	private FinanceDetailService financeDetailService;
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
		idList.clear();
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
	public void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction,
			long presentmentId, long partnerBankId, LoggedInUser userDetails, boolean isPDC, String presentmentRef,
			String partnerBankAccNo) throws Exception {
		logger.debug(Literal.ENTERING);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
			throw new AppException(Labels.getLabel("Amortization_EOD_Check"));
		}

		if ("Save".equals(userAction)) {
			savePresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Submit".equals(userAction)) {
			submitPresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Approve".equals(userAction)) {
			approvePresentments(excludeList, presentmentId, userDetails, isPDC, presentmentRef, partnerBankAccNo);
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

	private void savePresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void submitPresentments(List<Long> excludeList, List<Long> includeList, long presentmentId,
			long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.presentmentDetailDAO.updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
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

	private void approvePresentments(List<Long> excludeList, long presentmentId, LoggedInUser userDetails,
			boolean isPDC, String presentmentRef, String bankAccNo) throws Exception {

		if (excludeList != null && !excludeList.isEmpty()) {
			updatePresentmentIdAsZero(excludeList);
			List<PresentmentDetail> excludePresentmentList = presentmentDetailDAO
					.getPresentmensByExcludereason(presentmentId, RepayConstants.PEXC_MANUAL_EXCLUDE);
			if (excludePresentmentList != null && !excludePresentmentList.isEmpty()) {
				for (PresentmentDetail item : excludePresentmentList) {
					//in case of partial amount is reserved and that presentment moved to manualexclude
					//then reverse that amount.
					if (item.getExcessID() != 0) {
						finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
					}
					//in case of PDC Change the cheque the status to new
					if (isPDC) {
						updateChequeStatus(item.getMandateId(), PennantConstants.CHEQUESTATUS_NEW);
					}
				}
			}
		}

		processDetails(presentmentId, userDetails, isPDC, presentmentRef, bankAccNo);
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

	// Processing the presentment details
	public void processDetails(long presentmentId, LoggedInUser userDetails, boolean isPDC, String presentmentRef,
			String bankAccNo) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Long> idList = new ArrayList<Long>();
		List<Long> idExcludeEmiList = new ArrayList<Long>();
		boolean isError = false;
		List<PresentmentDetail> detailList = presentmentDetailDAO.getPresentmentDetail(presentmentId, true);
		if (detailList != null && !detailList.isEmpty()) {
			for (PresentmentDetail detail : detailList) {
				if (DateUtility.compare(SysParamUtil.getAppDate(), detail.getSchDate()) >= 0) {
					try {

						if (detail.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
							processAdvaceEMI(detail, false);
							idExcludeEmiList.add(detail.getId());
						} else {
							if (detail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
								processReceipts(detail, false);
							}
							if (detail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
								processAdvaceEMI(detail, false);
							}

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
				getPresentmentRequest().sendReqest(idList, idExcludeEmiList, presentmentId, isError, isPDC,
						presentmentRef, bankAccNo);
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
	public void processReceipts(PresentmentDetail detail, LoggedInUser userDetails) throws Exception {

		PresentmentHeader header = presentmentDetailDAO.getPresentmentHeader(detail.getPresentmentId(), "_Aview");

		AuditHeader auditHeader = doCreateReceipts(detail, userDetails, header);

		FinReceiptData finReceipt = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		long receiptId = finReceipt.getReceiptHeader().getReceiptID();
		if (receiptId == 0 || receiptId == Long.MIN_VALUE) {
			if (!auditHeader.isNextProcess()) {
				String errMsg = getErrorMsg(auditHeader);
				throw new Exception(errMsg);
			} else {
				throw new Exception(PennantJavaUtil.getLabel("label_FinReceiptHeader_Not_Created"));
			}
		}
		presentmentDetailDAO.updateReceptId(detail.getId(), receiptId);
	}

	// Creating the receipts If Schedule data is lessthan or equal to
	// Application date.
	private AuditHeader doCreateReceipts(PresentmentDetail presentmentDetail, LoggedInUser userDetails,
			PresentmentHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			// FinanceDetail
			FinanceDetail financeDetail = new FinanceDetail();
			FinScheduleData finScheduleData = financeDetailService
					.getFinSchDataForReceipt(presentmentDetail.getFinReference(), TableType.MAIN_TAB.getSuffix());

			Date appDate = SysParamUtil.getAppDate();

			finScheduleData.getFinanceMain().setRecordType("");
			finScheduleData.getFinanceMain().setVersion(finScheduleData.getFinanceMain().getVersion() + 1);
			finScheduleData.getFinanceMain().setUserDetails(userDetails);
			if (finScheduleData.getFinFeeDetailList() != null) {
				for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
					if (StringUtils.equals(finFeeDetail.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
						finFeeDetail.setRecordType("");
					}
				}
			}
			financeDetail.setFinScheduleData(finScheduleData);
			FinReceiptData finReceiptData = new FinReceiptData();
			FinReceiptHeader receiptHeader = new FinReceiptHeader();
			receiptHeader.setReference(presentmentDetail.getFinReference());
			receiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EMIINADV);
			receiptHeader.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptHeader.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
			receiptHeader.setReceiptDate(presentmentDetail.getSchDate());
			receiptHeader.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
			receiptHeader.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
			receiptHeader
					.setReceiptAmount(presentmentDetail.getPresentmentAmt().add(presentmentDetail.getAdvanceAmt()));
			receiptHeader.setReceiptMode(RepayConstants.RECEIPTMODE_PRESENTMENT);
			receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			receiptHeader.setNewRecord(true);
			receiptHeader.setLastMntBy(userDetails.getUserId());
			receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			receiptHeader.setUserDetails(userDetails);
			receiptHeader.setActFinReceipt(true);

			receiptHeader.setValueDate(null);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			finReceiptData.setReceiptHeader(receiptHeader);
			finReceiptData.setFinanceDetail(financeDetail);
			finReceiptData.setFinReference(financeMain.getFinReference());
			finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

			finReceiptData.setFinanceDetail(financeDetail);
			finReceiptData.setBuildProcess("I");

			finReceiptData.setValueDate(presentmentDetail.getSchDate());
			finReceiptData.setReceiptHeader(receiptHeader);
			finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, true);

			// calculate allocations
			finReceiptData = receiptCalculator.recalAutoAllocation(finReceiptData, presentmentDetail.getSchDate(),
					true);
			FinReceiptDetail receiptDetail = null;
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
				receiptDetail.setFundingAc(header.getPartnerBankId());
				receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
				receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
				receiptHeader.getReceiptDetails().add(receiptDetail);

			}

			if (presentmentDetail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
				receiptDetail = new FinReceiptDetail();
				receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
				receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
				receiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_EMIINADV);
				receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
				receiptDetail.setAmount(presentmentDetail.getAdvanceAmt());
				receiptDetail.setDueAmount(presentmentDetail.getAdvanceAmt());
				receiptDetail.setValueDate(presentmentDetail.getSchDate());
				receiptDetail.setReceivedDate(appDate);
				receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
				receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
				receiptDetail.setNoReserve(false);
				XcessPayables xcessPayable = new XcessPayables();
				xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
				xcessPayable.setAmount(presentmentDetail.getAdvanceAmt());
				xcessPayable.setTotPaidNow(presentmentDetail.getAdvanceAmt());
				receiptHeader.getXcessPayables().clear();
				receiptHeader.getXcessPayables().add(xcessPayable);
				receiptHeader.getReceiptDetails().add(receiptDetail);
			}

			//Receiptid creation #15-06-2018
			long receiptId = finReceiptHeaderDAO.generatedReceiptID(receiptHeader);
			receiptHeader.setReceiptID(receiptId);
			for (FinReceiptDetail rcDetail : receiptHeader.getReceiptDetails()) {
				rcDetail.setReceiptID(receiptId);
			}
			finReceiptData = receiptService.calculateRepayments(finReceiptData, true);
			receiptHeader.setReceiptAmount(presentmentDetail.getPresentmentAmt());
			AuditHeader auditHeader = getAuditHeader(finReceiptData, PennantConstants.TRAN_WF);
			auditHeader = receiptService.doApprove(auditHeader);
			logger.debug(Literal.LEAVING);

			return auditHeader;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private String getErrorMsg(AuditHeader auditHeader) {
		String msg = "";
		if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
			for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}

		if (auditHeader.getAuditDetail().getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}
		return msg;
	}

	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail,
				repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
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

	public void processReceipts(PresentmentDetail presentmentDetail, boolean isFullEMIPresent) throws Exception {
		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();

		Date appDate = SysParamUtil.getAppDate();

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

		FinanceMain fm = financeMainDAO.getFinanceMainById(finReference, "_AView", false);
		CustomerDetails custDetails = customerDetailsService.getCustomerDetailsById(fm.getCustID(), true, "_AView");
		List<FinanceScheduleDetail> scheduleDetails = financeScheduleDetailDAO.getFinScheduleDetails(finReference,
				"_AView", false);
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView");
		repaymentProcessUtil.calcualteAndPayReceipt(fm, custDetails.getCustomer(), scheduleDetails, null, profitDetail,
				header, financeType.getRpyHierarchy(), presentmentDetail.getSchDate(), SysParamUtil.getAppDate());

		if (presentmentDetail.getId() != Long.MIN_VALUE) {
			presentmentDetailDAO.updateReceptId(presentmentDetail.getId(), header.getReceiptID());
		}
	}

	public void processAdvaceEMI(PresentmentDetail presentmentDetail, boolean isFullEMIPresent) throws Exception {

		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader header = new FinReceiptHeader();

		Date appDate = SysParamUtil.getAppDate();

		long receiptId = finReceiptHeaderDAO.generatedReceiptID(header);
		header.setReference(presentmentDetail.getFinReference());
		header.setReceiptDate(presentmentDetail.getSchDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setReceiptID(receiptId);
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setReceiptAmount(presentmentDetail.getPresentmentAmt().add(presentmentDetail.getAdvanceAmt()));
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		header.setLogSchInPresentment(true);
		header.setActFinReceipt(true);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail receiptDetail = new FinReceiptDetail();

		if (presentmentDetail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			receiptDetail = new FinReceiptDetail();
			receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
			receiptDetail.setPayAgainstID(presentmentDetail.getExcessID());
			receiptDetail.setAmount(presentmentDetail.getAdvanceAmt());
			receiptDetail.setDueAmount(presentmentDetail.getAdvanceAmt());
			receiptDetail.setValueDate(presentmentDetail.getSchDate());
			receiptDetail.setReceivedDate(appDate);
			receiptDetail.setPartnerBankAc(presentmentDetail.getAccountNo());
			receiptDetail.setPartnerBankAcType(presentmentDetail.getAcType());
			receiptDetail.setNoReserve(false);
			receiptDetails.add(receiptDetail);
			XcessPayables xcessPayable = new XcessPayables();
			xcessPayable.setPayableType(RepayConstants.EXAMOUNTTYPE_EMIINADV);
			xcessPayable.setAmount(presentmentDetail.getAdvanceAmt());
			xcessPayable.setTotPaidNow(presentmentDetail.getAdvanceAmt());
			header.getXcessPayables().add(xcessPayable);
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
				profitDetail, header, financeType.getRpyHierarchy(), presentmentDetail.getSchDate(),
				SysParamUtil.getAppDate());
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

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
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

}