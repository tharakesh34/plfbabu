package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.payment.PaymentInstructionUploadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.paymentupload.exception.PaymentUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.file.UploadContants.Status;

public class PaymentInstructionUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(PaymentInstructionUploadServiceImpl.class);

	private PaymentInstructionUploadDAO paymentInstructionUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private PaymentHeaderDAO paymentHeaderDAO;
	private FeeTypeDAO feeTypeDAO;
	private ManualAdviseDAO manualAdviseDAO;

	private PaymentInstUploadApprovalProcess paymentInstUploadApprovalProcess;
	private FeeRefundHeaderService feeRefundHeaderService;

	protected static final String ERR_CODE = "9999";
	protected static final String ERR_DESC = "User rejected the record";

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<PaymentInstUploadDetail> details = paymentInstructionUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				List<String> key = new ArrayList<>();

				for (PaymentInstUploadDetail detail : details) {

					String reference = detail.getReference();
					String excessType = detail.getExcessType();

					String keyRef = reference.concat("_").concat(excessType);

					if ("P".equals(excessType)) {
						keyRef = keyRef.concat("_").concat(detail.getFeeType());
					}

					if (key.contains(keyRef)) {
						setError(detail, PaymentUploadError.REFUP014);
						failRecords++;
						continue;
					}

					key.add(keyRef);

					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					paymentInstructionUploadDAO.update(details);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);

				StringBuilder remarks = new StringBuilder("Process Completed");

				if (failRecords > 0) {
					remarks.append(" with exceptions, ");
				}

				remarks.append(" Total Records : ").append(header.getTotalRecords());
				remarks.append(" Success Records : ").append(sucessRecords);
				remarks.append(" Failed Records : ").append(failRecords);

				logger.info("Processed the File {}", header.getFileName());
			}

			logger.info("Payment Instruction Process is Initiated");

			paymentInstUploadApprovalProcess.approvePaymentInst(headerIdList);

			for (Long headerId : headerIdList) {
				int[] statuscount = getHeaderStatusCnt(headerId);
				FileUploadHeader header = new FileUploadHeader();
				header.setId(headerId);
				header.setRecordType("");
				header.setWorkflowId(0);
				header.setTaskId(null);
				header.setNextTaskId(null);
				header.setRoleCode(null);
				header.setNextRoleCode(null);
				header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				header.setApprovedBy(header.getLastMntBy());
				header.setApprovedOn(header.getLastMntOn());
				header.setProgress(Status.APPROVED.getValue());
				header.setSuccessRecords(statuscount[0]);
				header.setFailureRecords(statuscount[1]);
				header.setTotalRecords(statuscount[0] + statuscount[1]);
				paymentInstructionUploadDAO.uploadHeaderStatusCnt(header);
			}

		}).start();
	}

	public int[] getHeaderStatusCnt(long UploadId) {

		List<Integer> statusCount = paymentInstructionUploadDAO.getHeaderStatusCnt(UploadId);
		int sucessCount = 0;
		int failCount = 0;
		for (Integer uploadStatus : statusCount) {
			if (uploadStatus == EodConstants.PROGRESS_FAILED) {
				failCount++;
			} else {
				sucessCount++;
			}
		}
		int[] val = new int[2];
		val[0] = sucessCount;
		val[1] = failCount;
		return val;
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {

			txStatus = transactionManager.getTransaction(txDef);

			paymentInstructionUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));

			updateHeader(headers, false);

			transactionManager.commit(txStatus);

		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		PaymentInstUploadDetail detail = null;

		if (object instanceof PaymentInstUploadDetail) {
			detail = (PaymentInstUploadDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		logger.info("Validating the Data for the reference {}", detail.getReference());

		detail.setPayAmount(detail.getPayAmount().multiply(new BigDecimal(100)));

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, PaymentUploadError.REFUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PaymentUploadError.REFUP002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, PaymentUploadError.REFUP003);
			return;
		}

		if (!"N".equals(detail.getOverRide()) && !"Y".equals(detail.getOverRide())) {
			setError(detail, PaymentUploadError.REFUP013);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		if (!(RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())
				|| RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType()))) {
			setError(detail, PaymentUploadError.REFUP004);
			return;
		}

		String feeTypeCode = StringUtils.trimToNull(detail.getFeeType());
		Long feeTypeId = null;
		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(detail.getExcessType())) {
			feeTypeId = feeTypeDAO.getFeeTypeId(feeTypeCode);

			if (feeTypeId == null) {
				setError(detail, PaymentUploadError.REFUP005);
				return;
			}
		} else if (feeTypeCode != null) {
			setError(detail, PaymentUploadError.REFUP012);
			return;
		}

		BigDecimal dueAganistLoan = paymentHeaderDAO.getDueAgainstLoan(fm.getFinID());
		BigDecimal dueAganistCustomer = paymentHeaderDAO.getDueAgainstCustomer(fm.getCustID());

		if (detail.getOverRide().equals("N") && (dueAganistLoan.compareTo(BigDecimal.ZERO) > 0
				|| dueAganistCustomer.compareTo(BigDecimal.ZERO) > 0)) {
			setError(detail, PaymentUploadError.REFUP009);
			return;
		}

		boolean payInstInProgess = paymentInstructionDAO.isInProgress(fm.getFinID());
		if (payInstInProgess) {
			setError(detail, PaymentUploadError.REFUP006);
			return;
		}

		boolean feerefundInProgess = this.feeRefundHeaderService.isInProgress(fm.getFinID());
		if (feerefundInProgess) {
			setError(detail, PaymentUploadError.REFUP011);
			return;
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(detail.getExcessType())) {
			balanceAmount = finExcessAmountDAO.getExcessBalance(fm.getFinID());
		} else {
			balanceAmount = manualAdviseDAO.getPayableBalance(fm.getFinID(), feeTypeId);
		}

		if (balanceAmount.compareTo(detail.getPayAmount()) < 0) {
			setError(detail, PaymentUploadError.REFUP008);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	private void setError(PaymentInstUploadDetail detail, PaymentUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public String getSqlQuery() {
		return paymentInstructionUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setPaymentInstructionUploadDAO(PaymentInstructionUploadDAO paymentInstructionUploadDAO) {
		this.paymentInstructionUploadDAO = paymentInstructionUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	@Autowired
	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setPaymentInstUploadApprovalProcess(PaymentInstUploadApprovalProcess paymentInstUploadApprovalProcess) {
		this.paymentInstUploadApprovalProcess = paymentInstUploadApprovalProcess;
	}

	@Autowired
	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

}
