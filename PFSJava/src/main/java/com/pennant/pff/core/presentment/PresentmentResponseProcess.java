package com.pennant.pff.core.presentment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.pff.eod.cache.BounceConfigCache;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.presentment.dao.ConsecutiveBounceDAO;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.PresentmentImportProcess;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.presentment.model.ConsecutiveBounce;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennattech.pff.receipt.model.ReceiptDTO;

/***
 * The entire logic is moved to Presentment Engine.
 * 
 * use {@link PresentmentEngine}
 * 
 * @author gopal.p
 *
 */
@Deprecated
public class PresentmentResponseProcess implements Runnable {
	private static final Logger logger = LogManager.getLogger(PresentmentResponseProcess.class);

	/* DAO's */
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ConsecutiveBounceDAO consecutiveBounceDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;

	/* Service's */
	private ReceiptPaymentService receiptPaymentService;
	private PresentmentDetailService presentmentDetailService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ReceiptCancellationService receiptCancellationService;
	private NotificationService notificationService;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private ReceiptCalculator receiptCalculator;
	private PresentmentImportProcess presentmentImportProcess;
	private FinMandateService finMandateService;

	private DataSourceTransactionManager transactionManager;
	private DataEngineStatus deStatus;
	private EventProperties eventProperties;

	private long headerId;
	private int threadId;
	private Date appDate;

	public PresentmentResponseProcess(DataSource dataSource, long headerId, int threadId,
			EventProperties eventProperties) {
		super();
		this.headerId = headerId;
		this.threadId = threadId;
		this.eventProperties = eventProperties;
		this.appDate = this.eventProperties.getAppDate();

		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

	@Override
	public void run() {
		List<PresentmentDetail> presentmentDetails = presentmentDetailDAO.getPresentmentDetails(headerId, threadId);

		for (PresentmentDetail pd : presentmentDetails) {
			try {
				pd.setAppDate(this.appDate);
				processPresentment(pd);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		PresentmentDetailExtract.processedThreads.incrementAndGet();
	}

	private void processPresentment(PresentmentDetail pd) throws Exception {
		logger.info(Literal.ENTERING);

		PresentmentDetailExtract.recordCount.incrementAndGet();
		String presentmentReference = pd.getPresentmentRef();

		long finID = pd.getFinID();
		String finReference = StringUtils.trimToEmpty(pd.getFinReference());
		String finType = pd.getFinType();
		String mandateType = pd.getMandateType();
		Long mandateID = pd.getMandateId();
		Long receiptID = pd.getReceiptID();

		String clearingStatus = pd.getClearingStatus();
		String bounceCode = StringUtils.trimToEmpty(pd.getBounceCode());
		String bounceRemarks = pd.getBounceRemarks();
		boolean finIsActive = pd.isFinisActive();
		String status = RepayConstants.PEXC_SUCCESS;

		String fateCorrection = pd.getFateCorrection();

		if (presentmentImportProcess != null) {
			presentmentReference = presentmentImportProcess.getPresentmentRef(presentmentReference);
			status = presentmentImportProcess.getStatus(status);
			bounceCode = presentmentImportProcess.getReasonCode(bounceCode);
			if ((status == null || status.equals("")) && (bounceCode != null && !bounceCode.equals(""))) {
				status = "F";
			} else if ((status == null || status.equals("")) && (bounceCode == null || bounceCode.equals(""))) {
				status = "S";
			}
		}

		StringBuilder info = new StringBuilder();
		info.append("\nPresement Reference: ").append(presentmentReference);
		info.append("\nFinReference: ").append(finReference);
		info.append("\nFinType: ").append(finType);
		info.append("\nFinIsActive: ").append(finIsActive);
		info.append("\nMandate-Type: ").append(mandateType);
		info.append("\nMandate-ID: ").append(mandateID);
		info.append("\nReceipt-ID: ").append(receiptID);
		info.append("\nPresement Response Status: ").append(clearingStatus);
		info.append("\nBounce Reason Code: ").append(bounceCode);
		info.append("\nBounceRemarks: ").append(bounceRemarks);
		info.append("\nFate Correction: ").append(fateCorrection);

		logger.info(info.toString());

		/* Validations */
		String errorMessage = validate(pd, presentmentReference, clearingStatus, bounceCode, bounceRemarks);

		if (errorMessage != null) {
			logRespDetails(headerId, pd, RepayConstants.PEXC_FAILURE, errorMessage);
			PresentmentDetailExtract.failedCount.incrementAndGet();
			logger.info(Literal.LEAVING);
			return;
		}

		if (RepayConstants.PEXC_PAID.equals(clearingStatus)) {
			pd.setStatus(RepayConstants.PEXC_SUCCESS);
		}

		CustEODEvent custEODEvent = new CustEODEvent();
		custEODEvent.setEodDate(pd.getAppDate());
		setLoanDetails(custEODEvent, finID, finIsActive);
		FinEODEvent finEODEvent = custEODEvent.getFinEODEvents().get(0);

		boolean processReceipt = false;
		Long linkedTranId;

		if (RepayConstants.PEXC_BOUNCE.equals(pd.getStatus()) && RepayConstants.PEXC_SUCCESS.equals(clearingStatus)
				&& "Y".equals(pd.getFateCorrection())) {
			receiptID = 0L;
		}

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			if (receiptID == 0) {
				if (!PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
					createPresentmentReceipt(pd);
				} else if (!finIsActive) {
					processReceipt = processInactiveLoan(custEODEvent, pd);
				}

				logger.info("Re-loading finance data...");

				setLoanDetails(custEODEvent, finID, finIsActive);

				finEODEvent = custEODEvent.getFinEODEvents().get(0);

				receiptID = pd.getReceiptID();
			}

			String checkStatus = null;
			FinReceiptHeader rh = null;

			pd.setBounceID(0);
			pd.setManualAdviseId(null);

			if (RepayConstants.PEXC_SUCCESS.equals(clearingStatus)) {
				if (ImplementationConstants.PRESENTMENT_STAGE_ACCOUNTING_REQ) {
					AEEvent event = doPresentmentStageAccounting(pd);
					linkedTranId = event.getLinkedTranId();
					pd.setLinkedTranId(linkedTranId);
				}

				if (!processReceipt) {
					rh = getFinReceiptHeader(receiptID);
				}

				if (InstrumentType.isPDC(mandateType)) {
					checkStatus = ChequeSatus.REALISED;
				}
			} else {
				if (InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType)) {
					if (StringUtils.trimToNull(pd.getErrorDesc()) == null) {
						checkStatus = ChequeSatus.BOUNCE;
					} else {
						checkStatus = ChequeSatus.FAILED;
					}
				}
			}

			if (RepayConstants.PEXC_SUCCESS.equals(clearingStatus)) {
				updateFinanceDetails(finEODEvent, pd);
				if (rh != null) {
					updateFinReceiptHeader(rh);
				}
				status = RepayConstants.PEXC_SUCCESS;
				pd.setStatus(status);

				if (!InstrumentType.isPDC(mandateType)) {
					unHoldMandate(pd);
				}
			} else {
				status = RepayConstants.PEXC_BOUNCE;
				pd.setStatus(status);

				if ("N".equals(fateCorrection)) {
					pd = receiptCancellationService.presentmentCancellation(pd, custEODEvent);
				} else {
					if (finIsActive) {
						pd = receiptCancellationService.presentmentCancellation(pd, custEODEvent);
					} else {
						throw new AppException("Loan is closed and cannot update bounce");
					}
				}

				if (StringUtils.trimToNull(pd.getErrorDesc()) != null) {
					pd.setSchdVersion(pd.getSchdVersion() + 1);
					updatePresentmentDetail(pd);
					throw new AppException(pd.getErrorDesc());
				}

				presentmentDetailDAO.updatePresentmentIdAsZero(pd.getId());

				if (!(InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType))) {
					holdMandate(pd);
				}
			}

			if ((InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType)) && checkStatus != null) {
				presentmentDetailDAO.updateChequeStatus(mandateID, checkStatus);
			}

			String errorCode = StringUtils.trimToEmpty(bounceCode);
			if (StringUtils.isNotEmpty(pd.getBounceCode())) {
				errorCode = errorCode.concat("-").concat(pd.getBounceCode());
			}

			pd.setErrorDesc(errorCode);

			updatePresentmentDetail(pd);

			finMandateService.autoSwapingFromPDC(finID);

			PresentmentDetailExtract.successCount.incrementAndGet();

			transactionManager.commit(transactionStatus);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			transactionManager.rollback(transactionStatus);

			status = RepayConstants.PEXC_FAILURE;
			pd.setStatus(status);
			pd.setErrorCode("PR0002");
			pd.setErrorDesc(e.getMessage());

			// updatePresentmentDetail(pd);
			logRespDetails(headerId, pd, status, e.getMessage());
			PresentmentDetailExtract.failedCount.incrementAndGet();
		}

		logger.info(Literal.LEAVING);
	}

	private boolean processInactiveLoan(CustEODEvent custEODEvent, PresentmentDetail pd) {
		logger.info(Literal.ENTERING);
		Long receiptID = pd.getReceiptID();

		FinEODEvent finEODEvent = custEODEvent.getFinEODEvents().get(0);
		Customer customer = custEODEvent.getCustomer();

		ReceiptDTO receiptDTO = new ReceiptDTO();

		receiptDTO.setFinType(finEODEvent.getFinType());
		receiptDTO.setPresentmentDetail(pd);
		receiptDTO.setBussinessDate(pd.getSchDate());
		receiptDTO.setCustomer(customer);
		receiptDTO.setFinanceMain(finEODEvent.getFinanceMain());
		receiptDTO.setProfitDetail(finEODEvent.getFinProfitDetail());
		receiptDTO.setSchedules(finEODEvent.getFinanceScheduleDetails());
		receiptDTO.setNoReserve(false);
		receiptDTO.setPdDetailsExits(true);
		receiptDTO.setValuedate(pd.getSchDate());
		receiptDTO.setPostDate(pd.getSchDate());

		logger.info("Creating presentment receipt for inactive loan.");
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			receiptPaymentService.createReceipt(receiptDTO);
			receiptID = pd.getReceiptID();

			transactionManager.commit(transactionStatus);

			logger.info("Presentment receipt creation completed with the Receipt-ID: {}", receiptID);

		} catch (Exception e) {
			logger.error("Unable to create the recipt for inactive loan");
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			pd.setStatus(RepayConstants.PEXC_FAILURE);
			pd.setErrorCode("PR0000");
			pd.setErrorDesc(e.getMessage());

			logRespDetails(headerId, pd, pd.getStatus(), e.getMessage());
			updatePresentmentDetail(pd);

			PresentmentDetailExtract.failedCount.incrementAndGet();

			logger.info(Literal.LEAVING);
			return false;
		}

		logger.info(Literal.LEAVING);
		return true;

	}

	private void setLoanDetails(CustEODEvent custEODEvent, long finID, boolean finIsActive) {
		logger.info("Loading finance data...");

		FinEODEvent finEODEvent = new FinEODEvent();

		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, finIsActive);

		if (fm == null) {
			fm = financeMainDAO.getFinMainsForEODByFinRef(finID, false);
		}

		fm.setEventProperties(this.eventProperties);

		FinanceType financeType = FinanceConfigCache.getCacheFinanceType(fm.getFinType());
		Customer customer = custEODEvent.getCustomer();

		if (customer == null) {
			customer = customerDAO.getCustomerEOD(fm.getCustID());
		}

		List<FinODDetails> finODDetails = finODDetailsDAO.getFinODBalByFinRef(finID);
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		/* The last parameter false will get the records irrespective of status */
		FinanceProfitDetail fpd = financeProfitDetailDAO.getFinProfitDetailsByFinRef(finID);

		finEODEvent.setFinType(financeType);
		finEODEvent.setFinanceMain(fm);
		finEODEvent.setFinProfitDetail(fpd);
		finEODEvent.setFinanceScheduleDetails(schedules);
		finEODEvent.setFinODDetails(finODDetails);

		List<FinEODEvent> list = new ArrayList<>();
		list.add(finEODEvent);

		custEODEvent.setCustomer(customer);

		custEODEvent.setFinEODEvents(list);

		logger.info("Finance data loading completed.");

	}

	private String validate(PresentmentDetail pd, String presentmentReference, String clearingStatus, String bounceCode,
			String bounceRemarks) {

		if (StringUtils.trimToNull(clearingStatus) == null) {
			return "Status should not be empty.";
		}

		if (RepayConstants.PEXC_SUCCESS.equals(pd.getStatus()) && RepayConstants.PEXC_SUCCESS.equals(clearingStatus)) {
			return "Presentment response already marked as success.";
		} else if (RepayConstants.PEXC_BOUNCE.equals(pd.getStatus()) && "N".equals(pd.getFateCorrection())) {
			return "Presentment response already marked as bounce.";
		} else if (RepayConstants.PEXC_BOUNCE.equals(pd.getStatus())
				&& RepayConstants.PEXC_BOUNCE.equals(clearingStatus)) {
			return "Presentment response already marked as bounce.";
		}

		// Bounce Remarks
		if (RepayConstants.PEXC_BOUNCE.equals(clearingStatus)
				&& ImplementationConstants.PRESENT_RESP_BOUNCE_REMARKS_MAN) {
			if (StringUtils.isNotEmpty(bounceCode) && StringUtils.trimToNull(bounceRemarks) == null) {
				return "Bounce remarks are mandatory for the bounce reason code: " + bounceCode;
			}
			if (bounceRemarks != null && bounceRemarks.length() > 100) {
				return "Bounce Remarks length should be less than or equal to 100.";
			}
		}

		if (pd.getAppDate().compareTo(pd.getSchDate()) < 0) {
			return "The presentment not proceed with schedule date greater than application bussiness date";
		}

		return null;
	}

	private AEEvent doPresentmentStageAccounting(PresentmentDetail pd) {
		long finID = pd.getFinID();
		String finReference = StringUtils.trimToEmpty(pd.getFinReference());
		Date schDate = pd.getSchDate();

		FinanceMain fm = presentmentDetailService.getDefualtPostingDetails(finID, schDate);

		String finType = fm.getFinType();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setFinID(finID);
		aeEvent.setFinReference(finReference);
		aeEvent.setCustID(fm.getCustID());

		aeEvent.setFinType(finType);
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(finType);
		aeEvent.setValueDate(pd.getSchDate());
		aeEvent.setPostDate(pd.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setAccountingEvent(AccountingEvent.PRSNTRSP);
		aeEvent.setPostRefId(pd.getReceiptID());

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())
				&& ImplementationConstants.PENALTY_CALC_ON_REPRESENTATION) {
			aeEvent.setValueDate(pd.getRepresentmentDate());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(finType);
		amountCodes.setPartnerBankAc(fm.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(fm.getPartnerBankAcType());

		BigDecimal presentmentAmt = pd.getPresentmentAmt();
		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		dataMap.put("ae_presentmentAmt", presentmentAmt);
		aeEvent.setDataMap(dataMap);

		try {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finType, AccountingEvent.PRSNTRSP,
					FinanceConstants.MODULEID_FINTYPE));
			aeEvent.setDataMap(dataMap);
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			aeEvent.setErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetail("Accounting Engine",
					PennantConstants.ERR_UNDEF, "E", "Accounting Engine Failed to Create Postings:" + e.getMessage(),
					new String[] {}, new String[] {})).getMessage());
			return aeEvent;
		}

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Presentment response accounting postings failed.");
		}

		return aeEvent;
	}

	private void updateFinanceDetails(FinEODEvent finEODEvent, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = finEODEvent.getFinanceMain();
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		List<FinODDetails> overDueList = finEODEvent.getFinODDetails();

		long finID = pd.getFinID();

		try {
			fm = repaymentPostingsUtil.updateStatus(fm, appDate, schedules, pftDetail, overDueList, null);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		if (!fm.isFinIsActive()) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, pd.getSchDate());
			financeProfitDetailDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

		if (CollectionUtils.isNotEmpty(overDueList)) {
			FinScheduleData scheduleData = new FinScheduleData();
			scheduleData.setFinanceMain(fm);
			scheduleData.setFinanceScheduleDetails(schedules);
			overDueList = receiptCalculator.calPenalty(scheduleData, null, appDate, overDueList);
			finODDetailsDAO.updateList(overDueList);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateFinReceiptHeader(FinReceiptHeader rh) {
		logger.info(Literal.ENTERING);

		long receiptID = rh.getReceiptID();
		String excessAdjustTo = rh.getExcessAdjustTo();

		List<FinReceiptDetail> receiptDetails = rh.getReceiptDetails();

		List<FinRepayHeader> repayHeaders = new ArrayList<>();
		for (FinReceiptDetail rd : receiptDetails) {
			repayHeaders.addAll(rd.getRepayHeaders());
		}

		for (FinRepayHeader rph : repayHeaders) {
			if (FinServiceEvent.SCHDRPY.equals(rh.getReceiptPurpose())) {
				if (rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updExcessAfterRealize(rph.getFinID(), excessAdjustTo, rph.getExcessAmount(),
							rh.getReceiptID());
				}
			}
		}

		finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(receiptID, RepayConstants.PAYSTATUS_REALIZED,
				appDate);
		finReceiptDetailDAO.updateReceiptStatusByReceiptId(receiptID, RepayConstants.PAYSTATUS_REALIZED);

		logger.info(Literal.LEAVING);
	}

	private FinReceiptHeader getFinReceiptHeader(long receiptId) {
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "");
		if (rch == null) {
			return rch;
		}

		List<FinReceiptDetail> receiptDetails = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "");
		rch.setReceiptDetails(receiptDetails);

		for (FinReceiptDetail rd : receiptDetails) {
			FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(rd.getReceiptSeqID(), "");
			if (rph != null) {
				rd.getRepayHeaders().add(rph);
			}
		}

		return rch;
	}

	private void createPresentmentReceipt(PresentmentDetail pd) throws Exception {
		if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
			pd.setAdvanceAmt(BigDecimal.ZERO);
			presentmentDetailService.executeReceipts(pd, false, false);
		}
	}

	private void logRespDetails(long headerId, PresentmentDetail pd, String errorCode, String errorDesc) {
		if (StringUtils.trimToNull(errorDesc) != null) {
			errorDesc = (errorDesc.length() >= 2000) ? errorDesc.substring(0, 1998) : errorDesc;
		}
		presentmentDetailDAO.logRespDetailError(headerId, pd.getId(), errorCode, errorDesc);

		if (deStatus.getId() > 0) {
			presentmentDetailDAO.updateDataEngineLog(deStatus.getId(), pd.getPresentmentRef(), errorCode, errorDesc);
		} else {
			DataEngineLog dataEngineLog = new DataEngineLog();
			dataEngineLog.setReason(errorDesc);
			deStatus.getDataEngineLogList().add(dataEngineLog);

		}
	}

	private void updatePresentmentDetail(PresentmentDetail pd) {
		String errorDesc = pd.getErrorDesc();
		if (StringUtils.trimToNull(errorDesc) != null) {
			errorDesc = (errorDesc.length() >= 1000) ? errorDesc.substring(0, 998) : errorDesc;
		}
		presentmentDetailDAO.updatePresentmentDetail(pd);
	}

	private void holdMandate(PresentmentDetail pd) {
		BounceReason bounceReason = BounceConfigCache.getCacheBounceReason(pd.getBounceCode());

		if (bounceReason == null) {
			return;
		}

		int consecutiveBounceCount = bounceReason.getHoldMarkBounceCount();

		Long mandateId = pd.getMandateId();
		Long bounceId = bounceReason.getBounceID();
		Date schdDate = pd.getSchDate();

		ConsecutiveBounce consecBounces = consecutiveBounceDAO.getBounces(mandateId);

		if (consecBounces == null) {
			consecutiveBounceDAO.create(mandateId, bounceId, schdDate);
		}

		consecBounces = consecutiveBounceDAO.getBounces(mandateId);
		long lastBounceId = consecBounces.getBounceID();
		int bounceCount = consecBounces.getBounceCount();
		Date lastBounceDate = consecBounces.getLastBounceDate();

		if (lastBounceId != bounceId) {
			consecutiveBounceDAO.resetConter(mandateId, bounceId, schdDate);
		}

		if (DateUtil.getMonthsBetween(lastBounceDate, schdDate) == 1) {
			bounceCount = bounceCount + 1;
			consecutiveBounceDAO.update(mandateId, schdDate, bounceCount);
		}

		if (bounceCount < consecutiveBounceCount) {
			return;
		}

		String reason = MandateExtension.CONSECUTIVE_HOLD_REASON;

		mandateDAO.holdMandate(mandateId, reason);

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandateId);
		mandateStatus.setStatus(MandateStatus.HOLD);
		mandateStatus.setReason(reason);
		mandateStatus.setChangeDate(DateUtil.getSysDate());

		mandateStatusDAO.save(mandateStatus, "");
	}

	private void unHoldMandate(PresentmentDetail pd) {
		long mandateId = pd.getMandateId();
		ConsecutiveBounce consecBounces = consecutiveBounceDAO.getBounces(mandateId);

		if (consecBounces == null) {
			return;
		}

		consecutiveBounceDAO.delete(mandateId);

		mandateDAO.unHoldMandate(mandateId);

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandateId);
		mandateStatus.setStatus(MandateStatus.APPROVED);
		mandateStatus.setReason("Un-Hold Mandate");
		mandateStatus.setChangeDate(DateUtil.getSysDate());

		mandateStatusDAO.save(mandateStatus, "");
	}

	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setConsecutiveBounceDAO(ConsecutiveBounceDAO consecutiveBounceDAO) {
		this.consecutiveBounceDAO = consecutiveBounceDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	public DataEngineStatus getDeStatus() {
		return deStatus;
	}

	public void setDeStatus(DataEngineStatus deStatus) {
		this.deStatus = deStatus;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public PresentmentImportProcess getPresentmentImportProcess() {
		return presentmentImportProcess;
	}

	public void setPresentmentImportProcess(PresentmentImportProcess presentmentImportProcess) {
		this.presentmentImportProcess = presentmentImportProcess;
	}

	public EventProperties getEventProperties() {
		return eventProperties;
	}

	public void setEventProperties(EventProperties eventProperties) {
		this.eventProperties = eventProperties;
	}

	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

}
