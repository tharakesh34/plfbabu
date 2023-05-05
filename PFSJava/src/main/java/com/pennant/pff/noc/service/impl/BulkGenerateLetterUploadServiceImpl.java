package com.pennant.pff.noc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.noc.upload.BulkGenerateLetterUpload;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.BulkGenerateLetterUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BulkGenerateLetterUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(BulkGenerateLetterUploadServiceImpl.class);

	public BulkGenerateLetterUploadServiceImpl() {
		super();
	}

	private BulkGenerateLetterUploadDAO bulkGenerateLetterUploadDAO;
	private BulkGenerateLetterUploadValidateRecord bulkGenerateLetterUploadValidateRecord;
	private FinanceMainDAO financeMainDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BulkGenerateLetterUpload detail = null;

		if (object instanceof BulkGenerateLetterUpload) {
			detail = (BulkGenerateLetterUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getFinReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(reference, "", false);

		if (fm == null) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC01);
			return;
		}

		detail.setReferenceID(fm.getFinID());

		String letterType = detail.getLetterType();
		if (!NOCConstants.LETTERTYPE_CANCELLETTER.equals(letterType)
				&& !NOCConstants.LETTERTYPE_CLOSURE.equals(letterType)
				&& !NOCConstants.LETTERTYPE_NOC.equals(letterType)) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC02);
			return;
		}

		String mode = detail.getModeOfTransfer();
		if (!NOCConstants.MODE_COURIER.equals(mode) && !NOCConstants.MODE_EMAIL.equals(letterType)) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC03);
			return;
		}

		String waiverCharges = detail.getWaiverCharges();
		if (!PennantConstants.YES.equals(waiverCharges) && !PennantConstants.NO.equals(waiverCharges)) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC04);
			return;
		}

		if (fm.isFinIsActive() && fm.getClosingStatus() == null) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC05);
			return;
		}

		// int finTypeLtrmap = nocBulkGenerateLetterDAO.getFinTypeLtrMap(reference);
		// if (finTypeLtrmap < 0) {
		// setError(detail, BulkGenerateLetterUploadErrorCode.NOC08);
		// return;
		// }

		if (NOCConstants.LETTERTYPE_CLOSURE.equals(letterType) || NOCConstants.LETTERTYPE_NOC.equals(letterType)) {
			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
				setError(detail, BulkGenerateLetterUploadErrorCode.NOC06);
				return;
			}
		}

		if (NOCConstants.LETTERTYPE_CANCELLETTER.equals(letterType)) {
			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
				setError(detail, BulkGenerateLetterUploadErrorCode.NOC07);
				return;
			}
		}

		String finEvent = null;

		switch (letterType) {
		case NOCConstants.LETTERTYPE_NOC:
			finEvent = NOCConstants.FEE_TYPE_NOCLTR;
			break;
		case NOCConstants.LETTERTYPE_CLOSURE:
			finEvent = NOCConstants.FEE_TYPE_CLOSELTR;
			break;
		case NOCConstants.LETTERTYPE_CANCELLETTER:
			finEvent = NOCConstants.FEE_TYPE_CANCLLTR;
			break;

		default:
			break;
		}

		FinTypeFees ftf = bulkGenerateLetterUploadDAO.getFeeWaiverAllowed(fm.getFinType(), finEvent);

		if (PennantConstants.YES.equals(waiverCharges) && ftf == null
				|| PennantConstants.YES.equals(waiverCharges) && BigDecimal.ZERO == ftf.getMaxWaiverPerc()) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC09);
			return;
		}

		String cancelType = bulkGenerateLetterUploadDAO.getCanceltype(reference);
		if (NOCConstants.LOAN_CANCEL_REBOOK.equals(cancelType)) {
			setError(detail, BulkGenerateLetterUploadErrorCode.NOC10);
			return;
		}

		BulkGenerateLetterUpload noc = bulkGenerateLetterUploadDAO.getByReference(detail.getFinReference());
		Date ValueDate = DateUtil.getSysDate();
		String srtValueDate = DateUtil.format(ValueDate, "dd-MM-yy");
		if (noc != null) {
			String approvedOn = DateUtil.format(noc.getApprovedOn(), "dd-MM-yy");
			if (EodConstants.PROGRESS_SUCCESS == noc.getProgress()) {
				if (noc.getFinReference().equals(detail.getFinReference())
						&& noc.getLetterType().equals(detail.getLetterType())
						&& noc.getModeOfTransfer().equals(detail.getModeOfTransfer())
						&& noc.getWaiverCharges().equals(detail.getWaiverCharges())
						&& srtValueDate.equals(approvedOn)) {
					setError(detail, BulkGenerateLetterUploadErrorCode.NOC11);
					return;
				}
			}
		}

	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<BulkGenerateLetterUpload> details = bulkGenerateLetterUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (BulkGenerateLetterUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
						detail.setUserDetails(header.getUserDetails());
					}

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
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

					txStatus = transactionManager.getTransaction(txDef);

					bulkGenerateLetterUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}
			}
		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			bulkGenerateLetterUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private void setError(BulkGenerateLetterUpload detail, BulkGenerateLetterUploadErrorCode error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public String getSqlQuery() {
		return bulkGenerateLetterUploadDAO.getSqlQuery();
	}

	public static Logger getLogger() {
		return logger;
	}

	@Override
	public BulkGenerateLetterUploadValidateRecord getValidateRecord() {
		return bulkGenerateLetterUploadValidateRecord;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setBulkGenerateLetterUploadDAO(BulkGenerateLetterUploadDAO bulkGenerateLetterUploadDAO) {
		this.bulkGenerateLetterUploadDAO = bulkGenerateLetterUploadDAO;
	}

	@Autowired
	public void setBulkGenerateLetterUploadValidateRecord(
			BulkGenerateLetterUploadValidateRecord bulkGenerateLetterUploadValidateRecord) {
		this.bulkGenerateLetterUploadValidateRecord = bulkGenerateLetterUploadValidateRecord;
	}
}