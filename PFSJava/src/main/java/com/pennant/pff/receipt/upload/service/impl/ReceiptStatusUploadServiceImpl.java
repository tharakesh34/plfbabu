package com.pennant.pff.receipt.upload.service.impl;

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
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.receiptstatus.upload.ReceiptStatusUpload;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.status.dao.ReceiptStatusUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ReceiptStatusUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(ReceiptStatusUploadServiceImpl.class);

	private ReceiptStatusUploadDAO receiptStatusUploadDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;
	private RejectDetailDAO rejectDetailDAO;
	private ReceiptService receiptService;
	private ValidateRecord receiptStatusUploadValidateRecord;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private CustomerDAO customerDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ReceiptStatusUpload detail = null;

		if (object instanceof ReceiptStatusUpload) {
			detail = (ReceiptStatusUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		long receiptID = detail.getReceiptId();

		logger.info("Validating the Data for the receiptID {}", receiptID);

		detail.setHeaderId(header.getId());
		long receiptid = detail.getReceiptId();

		if (StringUtils.isBlank(String.valueOf(receiptid))) {
			setError(detail, ReceiptStatusUploadError.RU01);
			return;
		}

		FinReceiptHeader frh = finReceiptHeaderDAO.getReceiptById(receiptid, "_View");

		if (frh == null) {
			setError(detail, ReceiptStatusUploadError.RU02);
			return;
		}

		String receiptmodestatus = frh.getReceiptModeStatus();

		if (receiptmodestatus == null || StringUtils.isBlank(receiptmodestatus)) {
			setError(detail, ReceiptStatusUploadError.RU02);
			return;
		}

		if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(receiptmodestatus)
				|| ReceiptUploadConstants.RECEIPT_CANCEL.equals(receiptmodestatus)) {
			setError(detail, ReceiptStatusUploadError.RU03);
			return;
		}

		String status = StringUtils.upperCase(detail.getStatusRM());
		Date appDate = SysParamUtil.getAppDate();
		Date realizedDate = detail.getRealizationDate();

		if (StringUtils.isBlank(status)) {
			setError(detail, ReceiptStatusUploadError.RU04);
			return;
		}

		if (!ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)
				&& !ReceiptUploadConstants.RECEIPT_CANCEL.equals(status)
				&& !ReceiptUploadConstants.RECEIPT_REALIZED.equals(status)) {
			setError(detail, ReceiptStatusUploadError.RU05);
			return;
		}

		if (ReceiptUploadConstants.RECEIPT_REALIZED.equals(receiptmodestatus)) {
			if (receiptmodestatus.equals(status)) {
				setError(detail, ReceiptStatusUploadError.RU06);
				return;
			}
		}

		String receiptmode = frh.getReceiptMode();

		if (!DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(receiptmode)
				&& !DisbursementConstants.PAYMENT_TYPE_DD.equals(receiptmode)) {
			if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)) {
				setError(detail, ReceiptStatusUploadError.RU07);
				return;
			}

			if (realizedDate != null) {
				setError(detail, ReceiptStatusUploadError.RU08);
				return;
			}
		}

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(receiptmode)
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(receiptmode)) {

			if (realizedDate == null) {
				setError(detail, ReceiptStatusUploadError.RU09);
				return;
			}

			Date depositDate = frh.getDepositDate();

			if (depositDate != null) {
				if (DateUtil.compare(realizedDate, depositDate) < 0) {
					setError(detail, ReceiptStatusUploadError.RU010);
					return;
				}
			}
		}

		Date bounceDate = detail.getBounceDate();

		if (!ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)) {
			if (bounceDate != null) {
				setError(detail, ReceiptStatusUploadError.RU016);
				return;
			}
		}

		if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)) {
			if (bounceDate == null) {
				setError(detail, ReceiptStatusUploadError.RU017);
				return;
			}

			if (DateUtil.compare(bounceDate, realizedDate) < 0) {
				setError(detail, ReceiptStatusUploadError.RU018);
				return;
			}

			if (DateUtil.compare(appDate, bounceDate) < 0) {
				setError(detail, ReceiptStatusUploadError.RU019);
				return;
			}
		}

		String borCReason = detail.getBorcReason();
		String borCRemarks = detail.getBorcRemarks();
		if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)
				|| ReceiptUploadConstants.RECEIPT_CANCEL.equals(status)) {
			if (StringUtils.isBlank(borCReason)) {
				setError(detail, ReceiptStatusUploadError.RU011);
				return;
			}

			if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(status)) {
				if (bounceReasonDAO.getBounceCodeCount(borCReason) == 0) {
					setError(detail, ReceiptStatusUploadError.RU013);
					return;
				}
			}

			if (ReceiptUploadConstants.RECEIPT_CANCEL.equals(status)) {
				if (rejectDetailDAO.getRejectCodeCount(borCReason) == 0) {
					setError(detail, ReceiptStatusUploadError.RU014);
					return;
				}
			}

		} else {
			if (StringUtils.isNotBlank(borCReason)) {
				setError(detail, ReceiptStatusUploadError.RU012);
				return;
			}
		}

		if (StringUtils.isNotBlank(borCRemarks)) {
			if (borCRemarks.length() > 50) {
				setError(detail, ReceiptStatusUploadError.RU012);
				return;
			}
		}

	}

	private void setError(ReceiptStatusUpload detail, ReceiptStatusUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
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

				List<ReceiptStatusUpload> details = receiptStatusUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (ReceiptStatusUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
						detail.setUserDetails(header.getUserDetails());

						setFinReceiptDetail(detail);
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

					receiptStatusUploadDAO.update(details);

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

	private void setFinReceiptDetail(ReceiptStatusUpload detail) {
		FinReceiptData fd = new FinReceiptData();
		AuditHeader auditHeader = null;

		long receiptId = detail.getReceiptId();

		FinReceiptHeader frh = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "_View");
		List<FinReceiptDetail> frd = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "_View");
		List<ReceiptAllocationDetail> allocations = receiptAllocationDetailDAO.getAllocationsByReceiptID(receiptId,
				"_View");

		frh.setReceiptID(detail.getReceiptId());
		frh.setReceiptModeStatus(detail.getStatusRM());
		frh.setRealizationDate(detail.getRealizationDate());
		frh.setReceiptDetails(frd);
		frh.setUserDetails(detail.getUserDetails());
		if (allocations != null) {
			frh.setAllocations(allocations);
		}
		if (ReceiptUploadConstants.RECEIPT_BOUNCE.equals(detail.getStatusRM())) {
			frh.setBounceDate(detail.getBounceDate());
			frh.setBounceDate(detail.getBounceDate());
		}
		if (ReceiptUploadConstants.RECEIPT_CANCEL.equals(detail.getStatusRM())) {
			frh.setCancelReason(detail.getBorcReason());
		}

		fd.setFinID(frh.getFinID());
		fd.setFinReference(frh.getReference());
		fd.setReceiptHeader(frh);
		fd.setUserDetails(detail.getUserDetails());

		long finid = fd.getFinID();
		FinanceMain financeMain = financeMainDAO.getFinanceMainForBatch(finid);
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finid, "_AView", false);
		FinanceType ft = financeTypeDAO.getFinanceTypeByFinType(financeMain.getFinType());
		FinanceProfitDetail fpd = financeProfitDetailDAO.getFinProfitDetailsById(finid);

		FinScheduleData schdData = new FinScheduleData();
		FinanceDetail financeDetail = new FinanceDetail();
		Customer cst = customerDAO.getCustomer(financeMain.getCustID());

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(cst);
		customerDetails.setCustID(cst.getCustID());

		financeMain.setFinReference(fd.getFinReference());
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);

		schdData.setFinanceScheduleDetails(schedules);
		schdData.setFinanceType(ft);
		schdData.setFinanceMain(financeMain);
		if (fpd != null) {
			schdData.setFinPftDeatil(fpd);
		}
		financeDetail.setFinScheduleData(schdData);
		financeDetail.setCustomerDetails(customerDetails);
		fd.setFinanceDetail(financeDetail);

		auditHeader = getAuditHeader(fd, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(fd);

		AuditHeader rs = receiptService.doApprove(auditHeader);

		if (rs.getErrorMessage() != null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(rs.getErrorMessage().get(0).getMessage().toString());
			detail.setErrorCode(rs.getErrorMessage().get(0).getCode().toString());
		} else {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode("");
			detail.setErrorDesc("");
		}
	}

	private AuditHeader getAuditHeader(FinReceiptData fd, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, fd);
		return new AuditHeader(fd.getFinReference(), null, null, null, auditDetail,
				fd.getReceiptHeader().getUserDetails(), null);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			receiptStatusUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public String getSqlQuery() {
		return receiptStatusUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setReceiptStatusUploadDAO(ReceiptStatusUploadDAO receiptStatusUploadDAO) {
		this.receiptStatusUploadDAO = receiptStatusUploadDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setRejectDetailDAO(RejectDetailDAO rejectDetailDAO) {
		this.rejectDetailDAO = rejectDetailDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return receiptStatusUploadValidateRecord;
	}

	@Autowired
	public void setReceiptStatusUploadValidateRecord(ValidateRecord receiptStatusUploadValidateRecord) {
		this.receiptStatusUploadValidateRecord = receiptStatusUploadValidateRecord;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

}