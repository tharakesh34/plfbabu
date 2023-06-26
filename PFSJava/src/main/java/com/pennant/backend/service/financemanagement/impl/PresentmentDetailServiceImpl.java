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
 * * FileName : PresentmentDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * *
 * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
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
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.presentment.dao.ConsecutiveBounceDAO;
import com.pennanttech.external.ExternalPresentmentHook;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.PresentmentImportProcess;
import com.pennanttech.pff.external.PresentmentRequest;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennattech.pff.receipt.model.ReceiptDTO;

/**
 * Service implementation for methods that depends on <b>PresentmentHeader</b>.<br>
 */
public class PresentmentDetailServiceImpl extends GenericService<PresentmentHeader>
		implements PresentmentDetailService {
	private static final Logger logger = LogManager.getLogger(PresentmentDetailServiceImpl.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private CustomerDAO customerDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ConsecutiveBounceDAO consecutiveBounceDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;
	private NotificationService notificationService;
	private ReceiptPaymentService receiptPaymentService;
	private PresentmentImportProcess presentmentImportProcess;
	private EventPropertiesService eventPropertiesService;
	private OverdrafLoanService overdrafLoanService;
	private ReceiptCancellationService receiptCancellationService;
	private ReceiptCalculator receiptCalculator;
	private PresentmentRequest defaultPresentmentRequest;
	private PresentmentRequest presentmentRequest;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private RepaymentProcessUtil repaymentProcessUtil;
	private PostingsPreparationUtil postingsPreparationUtil;
	private CustomerDataService customerDataService;
	private FinMandateService finMandateService;
	private ExternalPresentmentHook externalPresentmentHook;

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
	public void updatePresentmentIdAsZero(long presentmentId) {
		presentmentDetailDAO.updatePresentmentIdAsZero(presentmentId);
	}

	@Override
	public void updatePresentmentIdAsZero(List<Long> presentmentIds) {
		List<List<Long>> idList = new ArrayList<>(1);

		if (presentmentIds.size() > PennantConstants.CHUNK_SIZE) {
			idList = ListUtils.partition(presentmentIds, PennantConstants.CHUNK_SIZE);
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
	public void updatePresentmentDetails(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);

		if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
			throw new AppException(Labels.getLabel("EOD_RUNNING"));
		}

		long presentmentId = ph.getId();
		Long partnerBankId = ph.getPartnerBankId();
		String userAction = ph.getUserAction();

		switch (userAction) {
		case "Save":
			savePresentmentData(ph);
			break;
		case "Submit":
			submitPresentments(ph);
			break;
		case "Approve":
			approvePresentments(ph);
			break;
		case "Resubmit":
			resubmitPresentments(presentmentId, partnerBankId);
			break;
		case "Cancel":
			cancelPresentments(presentmentId);
			break;
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

	@Override
	public String savePresentmentDetails(PresentmentHeader ph) {
		if (InstrumentType.isPDC(ph.getMandateType()) || InstrumentType.isIPDC(ph.getMandateType())) {
			return savePDCPresentments(ph);
		}
		return savePresentments(ph);
	}

	/**
	 * Fetching the Finance and customer details for sending the bounce notification to customer.
	 */
	@Override
	public FinanceDetail getFinanceDetailsByRef(long finID) {
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData finScheduleData = new FinScheduleData();

		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finID, "_AView", false);
		CustomerDetails customerDetails = customerDataService.getCustomerChildDetails(financeMain.getCustID(),
				"_AView");

		finScheduleData.setFinanceMain(financeMain);
		financeDetail.setCustomerDetails(customerDetails);
		financeDetail.setFinScheduleData(finScheduleData);
		return financeDetail;
	}

	@Override
	public List<Long> getIncludeList(long id) {
		return this.presentmentDetailDAO.getIncludeList(id);
	}

	@Override
	public void processSuccessPresentments(long receiptId) {
		repaymentProcessUtil.processSuccessPresentment(receiptId);
	}

	@Override
	public void executeReceipts(PresentmentDetail detail, boolean isExcessNoReserve, boolean isRealized)
			throws Exception {
		logger.debug(Literal.ENTERING);

		if (detail.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			processEmiInAdvance(detail, false, false);
		}
		if (detail.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			processReceipts(detail, false, true, isRealized);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getExcludeList(long id) {
		return this.presentmentDetailDAO.getExcludeList(id);
	}

	@Override
	public void updatePresentmentDetail(long id, String pexcSuccess, String utrNumber) {
		presentmentDetailDAO.updatePresentmentDetail(id, pexcSuccess, utrNumber);
	}

	@Override
	public void updatePresentmentDetail(long id, String status, Long linkedTranId, String utrNumber) {
		presentmentDetailDAO.updatePresentmentDetail(id, status, linkedTranId, utrNumber);
	}

	@Override
	public PresentmentDetail getRePresentmentDetails(String presentmentRef) {
		return presentmentDetailDAO.getRePresentmentDetails(presentmentRef);
	}

	@Override
	public List<Long> getManualExcludeList(long id) {
		return this.presentmentDetailDAO.getManualExcludeList(id);
	}

	@Override
	public void setProperties(PresentmentDetailExtract pde) {
		/* DAO's */
		pde.setFinanceRepaymentsDAO(financeRepaymentsDAO);
		pde.setPostingsPreparationUtil(postingsPreparationUtil);
		pde.setPresentmentDetailDAO(presentmentDetailDAO);
		pde.setFinReceiptHeaderDAO(finReceiptHeaderDAO);
		pde.setFinanceMainDAO(financeMainDAO);
		pde.setCustomerDAO(customerDAO);
		pde.setFinanceScheduleDetailDAO(financeScheduleDetailDAO);
		pde.setFinanceProfitDetailDAO(financeProfitDetailDAO);
		pde.setFinODDetailsDAO(finODDetailsDAO);
		pde.setFinReceiptDetailDAO(finReceiptDetailDAO);
		pde.setFinExcessAmountDAO(finExcessAmountDAO);
		pde.setConsecutiveBounceDAO(consecutiveBounceDAO);
		pde.setMandateDAO(mandateDAO);
		pde.setMandateStatusDAO(mandateStatusDAO);

		/* Service's */
		pde.setPresentmentDetailService(this);
		pde.setNotificationService(notificationService);
		pde.setRepaymentPostingsUtil(repaymentPostingsUtil);
		pde.setReceiptCalculator(receiptCalculator);
		pde.setReceiptPaymentService(receiptPaymentService);
		pde.setReceiptCancellationService(receiptCancellationService);
		pde.setPresentmentImportProcess(presentmentImportProcess);
		pde.setEventPropertiesService(eventPropertiesService);
		pde.setFinMandateService(finMandateService);
	}

	private void savePresentmentData(PresentmentHeader ph) {
		List<Long> includeList = ph.getIncludeList();
		List<Long> excludeList = ph.getExcludeList();
		long id = ph.getId();
		Long partnerBankId = ph.getPartnerBankId();

		int count = 0;
		if (CollectionUtils.isNotEmpty(includeList)) {
			count = count + this.presentmentDetailDAO.updatePresentmentDetials(id, includeList, 0);
		}

		if (CollectionUtils.isNotEmpty(excludeList)) {
			count = count + this.presentmentDetailDAO.updatePresentmentDetials(id, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}

		this.presentmentDetailDAO.updatePresentmentHeader(id, RepayConstants.PEXC_BATCH_CREATED, partnerBankId);
	}

	private void updateChequeStatus(long chequeDetailsId, String status) {
		chequeDetailDAO.updateChequeStatus(chequeDetailsId, status);
	}

	private void submitPresentments(PresentmentHeader ph) {
		List<Long> includeList = ph.getIncludeList();
		List<Long> excludeList = ph.getExcludeList();
		long id = ph.getId();
		Long partnerBankId = ph.getPartnerBankId();

		int count = 0;
		if (CollectionUtils.isNotEmpty(includeList)) {
			count = count + this.presentmentDetailDAO.updatePresentmentDetials(id, includeList, 0);
		}

		if (CollectionUtils.isNotEmpty(excludeList)) {
			count = count + this.presentmentDetailDAO.updatePresentmentDetials(id, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.presentmentDetailDAO.updatePresentmentHeader(id, RepayConstants.PEXC_AWAITING_CONF, partnerBankId);
	}

	private void resubmitPresentments(long presentmentId, Long partnerBankId) {
		this.presentmentDetailDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
				partnerBankId);
	}

	private void approvePresentments(PresentmentHeader ph) {
		List<Long> excludeList = ph.getExcludeList();
		long presentmentId = ph.getId();

		try {
			if (CollectionUtils.isEmpty(excludeList)) {
				processDetails(ph);
				return;
			}

			updatePresentmentIdAsZero(excludeList);

			List<PresentmentDetail> excludePresentmentList = presentmentDetailDAO
					.getPresentmensByExcludereason(presentmentId, RepayConstants.PEXC_MANUAL_EXCLUDE);

			if (CollectionUtils.isEmpty(excludePresentmentList)) {
				processDetails(ph);
				return;
			}

			List<PresentmentDetail> excessAmountList = new ArrayList<>();
			List<Long> chequeStatusList = new ArrayList<>();

			for (PresentmentDetail item : excludePresentmentList) {
				if (item.getExcessID() != 0) {
					excessAmountList.add(item);

					if (excessAmountList.size() == PennantConstants.CHUNK_SIZE) {
						finExcessAmountDAO.batchUpdateExcessAmount(excessAmountList);
						excessAmountList = new ArrayList<>();
					}
				}
				if (InstrumentType.isPDC(ph.getMandateType()) || InstrumentType.isIPDC(ph.getMandateType())) {
					chequeStatusList.add(item.getMandateId());

					if (chequeStatusList.size() == PennantConstants.CHUNK_SIZE) {
						chequeDetailDAO.batchUpdateChequeStatus(chequeStatusList, ChequeSatus.NEW);
						chequeStatusList = new ArrayList<>();
					}
				}
			}

			if (!excessAmountList.isEmpty()) {
				finExcessAmountDAO.batchUpdateExcessAmount(excessAmountList);
			}

			if (!chequeStatusList.isEmpty()) {
				chequeDetailDAO.batchUpdateChequeStatus(chequeStatusList, ChequeSatus.NEW);
			}

			processDetails(ph);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	private void cancelPresentments(long presentmentId) {
		List<PresentmentDetail> list = this.presentmentDetailDAO.getPresentmentDetail(presentmentId, false);

		for (PresentmentDetail pd : list) {
			if (pd.getExcessID() != 0) {
				finExcessAmountDAO.updateExcessAmount(pd.getExcessID(), pd.getAdvanceAmt());
			}
			updatePresentmentIdAsZero(pd.getId());

			String paymentMode = this.presentmentDetailDAO.getPaymenyMode(pd.getPresentmentRef());
			if (InstrumentType.isPDC(paymentMode) || InstrumentType.isIPDC(paymentMode)) {
				updateChequeStatus(pd.getMandateId(), ChequeSatus.NEW);
			}

			if (ImplementationConstants.OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE) {
				overdrafLoanService.cancelCharges(pd.getId());
			}
		}
		// reverse the excess movement
		reverseExcessMovements(presentmentId);

		this.presentmentDetailDAO.deletePresentmentDetails(presentmentId);
		this.presentmentDetailDAO.deletePresentmentHeader(presentmentId);
	}

	private void reverseExcessMovements(long presentmentId) {
		List<FinExcessMovement> advlist = finExcessAmountDAO.getFinExcessAmount(presentmentId);
		if (advlist != null && !advlist.isEmpty()) {
			for (FinExcessMovement finExcessAmount : advlist) {
				// update reserver and delete the movement
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

	private String savePDCPresentments(PresentmentHeader ph) {
		PresentmentDetailExtractService ps = new PresentmentDetailExtractService();

		ps.setPresentmentDetailDAO(presentmentDetailDAO);
		ps.setFinExcessAmountDAO(finExcessAmountDAO);
		ps.setChequeDetailDAO(chequeDetailDAO);
		ps.setOverdrafLoanService(overdrafLoanService);

		return ps.extarctPDCPresentments(ph);
	}

	private String savePresentments(PresentmentHeader ph) {
		PresentmentDetailExtractService ps = new PresentmentDetailExtractService();

		ps.setPresentmentDetailDAO(presentmentDetailDAO);
		ps.setFinExcessAmountDAO(finExcessAmountDAO);
		ps.setChequeDetailDAO(chequeDetailDAO);
		ps.setOverdrafLoanService(overdrafLoanService);

		return ps.extarctPresentments(ph);
	}

	private void processDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		long headerId = presentmentHeader.getId();

		List<PresentmentDetail> presements = presentmentDetailDAO.getPresentmentDetail(headerId, true);

		if (CollectionUtils.isEmpty(presements)) {
			return;
		}

		List<Long> idList = new ArrayList<>();
		List<Long> idExcludeEmiList = new ArrayList<>();

		boolean isError = false;

		Date appDate = SysParamUtil.getAppDate();

		for (PresentmentDetail pd : presements) {
			long detailID = pd.getId();
			pd.setAppDate(appDate);
			pd.setPresentmentType(presentmentHeader.getPresentmentType());

			if (DateUtil.compare(appDate, pd.getSchDate()) >= 0) {
				try {

					if (pd.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
						processAdvaceEMI(pd, false);
						idExcludeEmiList.add(detailID);
					} else {
						if (PresentmentExtension.DUE_DATE_RECEIPT_CREATION
								&& pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
							processReceipts(presentmentHeader, pd, false);
						}
						if (pd.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
							processAdvaceEMI(pd, false);
						}

						idList.add(detailID);
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					isError = true;
				}

			} else {
				if (pd.getExcludeReason() == RepayConstants.PEXC_EMIINADVANCE) {
					idExcludeEmiList.add(detailID);
				} else {
					idList.add(detailID);
				}
			}
		}

		try {
			String presentmentRef = presentmentHeader.getReference();
			String bankAccNo = presentmentHeader.getPartnerAcctNumber();
			String backOfficeName = presentmentDetailDAO
					.getBackOfficeNameByBranchCode(presentmentHeader.getUserDetails().getBranchCode());

			String branchCode = presentmentHeader.getMandateType() + " Presentment Download/" + backOfficeName;

			if (externalPresentmentHook != null) {
				externalPresentmentHook.processPresentmentRequest(presentmentHeader);
			} else {
				getPresentmentRequest().sendReqest(idList, headerId, isError, presentmentHeader.getMandateType(),
						presentmentRef, bankAccNo, branchCode);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void processReceipts(PresentmentHeader ph, PresentmentDetail pd, boolean isFullEMIPresent)
			throws Exception {
		FinReceiptData finReceiptData = new FinReceiptData();
		FinReceiptHeader rh = new FinReceiptHeader();
		Date appDate = pd.getAppDate();
		String presentmentType = pd.getPresentmentType();

		long receiptId = finReceiptHeaderDAO.generatedReceiptID(rh);

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();

		rh.setFinID(finID);
		rh.setReference(finReference);
		rh.setReceivedDate(appDate);
		rh.setReceiptDate(pd.getSchDate());
		rh.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rh.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rh.setReceiptID(receiptId);
		rh.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rh.setExcessAdjustTo(ExcessType.EXCESS);
		rh.setAllocationType(AllocationType.AUTO);
		rh.setReceiptAmount(pd.getPresentmentAmt());
		rh.setEffectSchdMethod(PennantConstants.List_Select);
		rh.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
		rh.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		rh.setLogSchInPresentment(true);
		rh.setActFinReceipt(true);
		rh.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rh.setRealizationDate(appDate);

		List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail rd = new FinReceiptDetail();

		boolean penalCalReq = isPenalCalcReq(presentmentType);

		if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			rd = new FinReceiptDetail();
			rd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rd.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			rd.setPayAgainstID(pd.getExcessID());
			rd.setAmount(pd.getPresentmentAmt());
			rd.setDueAmount(pd.getPresentmentAmt());
			rd.setValueDate(pd.getSchDate());
			rd.setReceivedDate(appDate);
			rd.setPartnerBankAc(pd.getAccountNo());
			rd.setPartnerBankAcType(pd.getAcType());

			if (penalCalReq) {
				rd.setValueDate(appDate);
			} else {
				rd.setValueDate(pd.getSchDate());
			}

			receiptDetails.add(rd);
		}

		rh.setReceiptDetails(receiptDetails);
		rh.setRemarks("");

		if (penalCalReq) {
			rh.setReceiptDate(appDate);
			rh.setPresentmentType(presentmentType);
		} else {
			rh.setReceiptDate(pd.getSchDate());
		}

		finReceiptData.setReceiptHeader(rh);
		finReceiptData.setFinID(finID);
		finReceiptData.setFinReference(finReference);
		finReceiptData.setSourceId("");

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_AView", false);
		Customer customer = customerDAO.getCustomerForPresentment(fm.getCustID());
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		FinanceProfitDetail profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(customer);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rh);
		receiptDTO.setPresentmentHeader(ph);
		receiptDTO.setPresentmentDetail(pd);
		receiptDTO.setValuedate(pd.getSchDate());
		receiptDTO.setPostDate(appDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);

		if (pd.getId() != Long.MIN_VALUE) {
			pd.setReceiptID(rh.getReceiptID());
			presentmentDetailDAO.updateReceptIdAndAmounts(pd);
		}
	}

	private static boolean isPenalCalcReq(String presentmentType) {
		return PennantConstants.PROCESS_REPRESENTMENT.equals(presentmentType)
				&& ImplementationConstants.PENALTY_CALC_ON_REPRESENTATION;
	}

	private void processAdvaceEMI(PresentmentDetail pd, boolean isFullEMIPresent) throws Exception {
		FinReceiptData rd = new FinReceiptData();

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();

		BigDecimal advanceAmt = pd.getAdvanceAmt();
		Date schDate = pd.getSchDate();
		Date appDate = pd.getAppDate();

		FinReceiptHeader rch = new FinReceiptHeader();
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(rch);

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setReceiptDate(schDate);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptID(receiptId);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(ExcessType.EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setReceiptAmount(advanceAmt);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(RepayConstants.PAYTYPE_EXCESS);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setLogSchInPresentment(true);
		rch.setActFinReceipt(true);

		List<FinReceiptDetail> receiptDetails = new ArrayList<>();

		if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
			FinReceiptDetail rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
			rcd.setPayAgainstID(pd.getExcessID());
			rcd.setAmount(advanceAmt);
			rcd.setDueAmount(advanceAmt);
			rcd.setValueDate(schDate);
			rcd.setReceivedDate(appDate);
			rcd.setPartnerBankAc(pd.getAccountNo());
			rcd.setPartnerBankAcType(pd.getAcType());
			rcd.setNoReserve(false);
			receiptDetails.add(rcd);

			XcessPayables xp = new XcessPayables();
			xp.setPayableType(ExcessType.EMIINADV);
			xp.setAmount(advanceAmt);
			xp.setTotPaidNow(advanceAmt);
			rch.getXcessPayables().add(xp);
		}

		rch.setReceiptDetails(receiptDetails);
		rch.setRemarks("");

		rd.setReceiptHeader(rch);
		rd.setFinID(finID);
		rd.setFinReference(finReference);
		rd.setSourceId("");

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_AView", false);
		Customer customer = customerDAO.getCustomerForPresentment(fm.getCustID());
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		FinanceProfitDetail profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(customer);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rch);
		receiptDTO.setPresentmentHeader(null);
		receiptDTO.setPresentmentDetail(null);
		receiptDTO.setValuedate(schDate);
		receiptDTO.setPostDate(appDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
	}

	private void processEmiInAdvance(PresentmentDetail pd, boolean isExcessNoReserve, boolean updateReceiptId)
			throws Exception {
		logger.debug("Proccessing Adv Emi for ref: {}", pd.getFinReference());

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();

		FinReceiptData rd = new FinReceiptData();

		Date appDate = SysParamUtil.getAppDate();
		Date valueDate = pd.getSchDate();

		FinReceiptHeader rch = new FinReceiptHeader();
		long receiptId = finReceiptHeaderDAO.generatedReceiptID(rch);

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setPresentmentSchDate(pd.getSchDate());

		if (PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
			rch.setReceiptDate(pd.getSchDate());
		} else {
			rch.setReceiptDate(appDate);
			if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())) {
				valueDate = appDate;
			}
		}

		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptID(receiptId);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(ExcessType.EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		rch.setReceiptAmount(pd.getPresentmentAmt());
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(ReceiptMode.EXCESS);
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setRealizationDate(appDate);
		rch.setLogSchInPresentment(true);
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		rch.setTransactionRef(pd.getUtrNumber());

		List<FinReceiptDetail> rcdList = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail rcd = new FinReceiptDetail();

		if (pd.getAdvanceAmt().compareTo(BigDecimal.ZERO) > 0) {
			rcd = new FinReceiptDetail();
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setPaymentType(ReceiptMode.EMIINADV);
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			rcd.setPayAgainstID(pd.getExcessID());
			rcd.setAmount(pd.getAdvanceAmt());
			rcd.setDueAmount(pd.getAdvanceAmt());
			XcessPayables xcessPayable = new XcessPayables();
			xcessPayable.setPayableType(ExcessType.EMIINADV);
			xcessPayable.setAmount(pd.getAdvanceAmt());
			xcessPayable.setTotPaidNow(pd.getAdvanceAmt());
			rch.getXcessPayables().add(xcessPayable);
			rch.setReceiptAmount(pd.getAdvanceAmt());
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
			rcd.setValueDate(pd.getSchDate());
			if (PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
				rcd.setValueDate(pd.getSchDate());
			} else if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())) {
				rcd.setValueDate(appDate);
			}
			rcd.setReceivedDate(rcd.getValueDate());
			rcd.setValueDate(pd.getSchDate());
			rcd.setPartnerBankAc(pd.getAccountNo());
			rcd.setPartnerBankAcType(pd.getAcType());
			rcd.setNoReserve(isExcessNoReserve);
			rcd.setTransactionRef(pd.getUtrNumber());
			rcdList.add(rcd);

		}

		rch.setReceiptDetails(rcdList);

		rch.setRemarks("");
		rd.setReceiptHeader(rch);
		rd.setFinID(finID);
		rd.setFinReference(finReference);
		rd.setSourceId("");

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_AView", false);
		CustomerDetails custDetails = customerDataService.getCustomerDetailsbyID(fm.getCustID(), true, "_AView");
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "_AView", false);
		FinanceProfitDetail profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(custDetails.getCustomer());
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rch);
		receiptDTO.setPresentmentHeader(null);
		receiptDTO.setPresentmentDetail(null);
		receiptDTO.setValuedate(valueDate);
		receiptDTO.setPostDate(appDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
		if (pd.getId() != Long.MIN_VALUE && updateReceiptId) {
			pd.setReceiptID(rch.getReceiptID());
			presentmentDetailDAO.updateReceptId(pd);
		}

		pd.setReceiptID(receiptId);

		logger.debug("Proccessing Adv Emi completed for ref: {}", pd.getFinReference());
	}

	private void processReceipts(PresentmentDetail pd, boolean isExcessNoReserve, boolean updateReceiptId,
			boolean isRealized) throws Exception {

		long finID = pd.getFinID();
		String finReference = pd.getFinReference();

		FinReceiptData rd = new FinReceiptData();

		Date appDate = SysParamUtil.getAppDate();

		FinReceiptHeader rch = new FinReceiptHeader();

		long receiptId = finReceiptHeaderDAO.generatedReceiptID(rch);

		rch.setFinID(finID);
		rch.setReference(finReference);
		rch.setPresentmentSchDate(pd.getSchDate());

		if (PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
			rch.setReceiptDate(pd.getSchDate());
		} else {
			rch.setReceiptDate(appDate);
		}

		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setReceiptID(receiptId);
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(ExcessType.EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setReceivedFrom(RepayConstants.RECEIVED_CUSTOMER);
		rch.setReceiptAmount(pd.getPresentmentAmt());
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);

		if (isRealized) {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		} else {
			rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_DEPOSITED);
		}

		rch.setLogSchInPresentment(true);
		rch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		rch.setLastMntBy(pd.getLastMntBy());
		rch.setVersion(rch.getVersion() + 1);

		List<FinReceiptDetail> rcdList = new ArrayList<FinReceiptDetail>();

		FinReceiptDetail rcd = new FinReceiptDetail();

		if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			rcd = new FinReceiptDetail();
			rch.setReceiptAmount(pd.getPresentmentAmt());
			rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			rcd.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			rcd.setPayAgainstID(pd.getExcessID());
			rcd.setAmount(pd.getPresentmentAmt());
			rcd.setDueAmount(pd.getPresentmentAmt());
			rcd.setValueDate(pd.getSchDate());

			if (PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
				rcd.setValueDate(pd.getSchDate());
			} else if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())) {
				rcd.setValueDate(appDate);
			}

			rcd.setReceivedDate(rcd.getValueDate());
			rcd.setPartnerBankAc(pd.getAccountNo());
			rcd.setPartnerBankAcType(pd.getAcType());
			rcd.setTransactionRef(pd.getUtrNumber());
			rcdList.add(rcd);

		}

		rch.setReceiptDetails(rcdList);

		rch.setRemarks("");
		rd.setReceiptHeader(rch);
		rd.setFinID(finID);
		rd.setFinReference(finReference);
		rd.setSourceId("");

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_AView", false);
		CustomerDetails custDetails = customerDataService.getCustomerDetailsbyID(fm.getCustID(), true, "_AView");
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "_AView", false);
		FinanceProfitDetail profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(finID);

		ReceiptDTO receiptDTO = new ReceiptDTO();
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setCustomer(custDetails.getCustomer());
		receiptDTO.setSchedules(schedules);
		receiptDTO.setFees(null);
		receiptDTO.setProfitDetail(profitDetail);
		receiptDTO.setFinReceiptHeader(rch);
		receiptDTO.setPresentmentHeader(null);
		receiptDTO.setPresentmentDetail(null);
		receiptDTO.setValuedate(pd.getSchDate());
		receiptDTO.setPostDate(appDate);

		repaymentProcessUtil.calcualteAndPayReceipt(receiptDTO);
		if (pd.getId() != Long.MIN_VALUE && updateReceiptId) {
			pd.setReceiptID(rch.getReceiptID());
			presentmentDetailDAO.updateReceptId(pd);
		}
	}

	@Override
	public FinanceMain getDefualtPostingDetails(long finID, Date schDate) {
		return presentmentDetailDAO.getDefualtPostingDetails(finID, schDate);
	}

	private PresentmentRequest getPresentmentRequest() {
		return presentmentRequest == null ? defaultPresentmentRequest : presentmentRequest;
	}

	@Override
	public PresentmentDetail getPresentmentDetailByFinRefAndPresID(long finID, long presentmentId) {
		return presentmentDetailDAO.getPresentmentDetailByFinRefAndPresID(finID, presentmentId, "");
	}

	@Override
	public boolean searchIncludeList(long presentmentId, int excludereason) {
		return presentmentDetailDAO.searchIncludeList(presentmentId, excludereason);
	}

	@Override
	public List<Long> getExcludePresentmentDetailIdList(long presentmentId, boolean isExclude) {
		return presentmentDetailDAO.getExcludePresentmentDetailIdList(presentmentId, isExclude);
	}

	@Override
	public List<PresentmentHeader> getPresenmentHeaderList(Date fromDate, Date toDate, int status) {
		return this.presentmentDetailDAO.getPresentmentHeaderList(fromDate, toDate, status);
	}

	@Override
	public Long getFinID(String finreference) {
		return financeMainDAO.getFinID(finreference, TableType.MAIN_TAB);
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
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

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setConsecutiveBounceDAO(ConsecutiveBounceDAO consecutiveBounceDAO) {
		this.consecutiveBounceDAO = consecutiveBounceDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	@Autowired(required = false)
	@Qualifier(value = "presentmentImportProcess")
	public void setPresentmentImportProcess(PresentmentImportProcess presentmentImportProcess) {
		this.presentmentImportProcess = presentmentImportProcess;
	}

	@Autowired
	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	@Autowired
	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

	@Autowired(required = false)
	public void setExternalPresentmentHook(ExternalPresentmentHook externalPresentmentHook) {
		this.externalPresentmentHook = externalPresentmentHook;
	}

}