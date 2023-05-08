package com.pennant.pff.writeoffupload.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennant.pff.writeoffupload.dao.WriteOffUploadDAO;
import com.pennant.pff.writeoffupload.exception.WriteOffUploadError;
import com.pennant.pff.writeoffupload.model.WriteOffUploadDetail;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class WriteOffUploadServiceImpl extends AUploadServiceImpl<WriteOffUploadDetail> {
	private static final Logger logger = LogManager.getLogger(WriteOffUploadServiceImpl.class);

	private WriteOffUploadDAO writeOffUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceWriteoffService financeWriteoffService;
	private ReceiptService receiptService;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceDetailService financeDetailService;
	private FinServiceInstrutionDAO finServiceInstructionDAO;

	public WriteOffUploadServiceImpl() {
		super();
	}

	@Override
	protected WriteOffUploadDetail getDetail(Object object) {
		if (object instanceof WriteOffUploadDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
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

				List<WriteOffUploadDetail> details = writeOffUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				List<String> key = new ArrayList<>();

				// Validating Writeoff Prepared loan details
				for (WriteOffUploadDetail detail : details) {

					String reference = detail.getReference();

					if (key.contains(reference)) {
						setError(detail, WriteOffUploadError.WOUP009);
						failRecords++;
						continue;
					}

					key.add(reference);

					// Validate Details
					doValidate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				logger.info("WriteOff Upload Process is Initiated for the Header ID {}", header.getId());

				processWriteOffLoan(header, details);

				logger.info("WriteOff Upload Process is Completed for the Header ID {}", header.getId());

				header.getUploadDetails().addAll(details);

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

					// Success Status Update on Upload Table
					writeOffUploadDAO.update(details);

					// Update Header Details Count
					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headerList, true);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

			}
		}).start();

	}

	private void processWriteOffLoan(FileUploadHeader header, List<WriteOffUploadDetail> details) {

		// Rendering Write off Loan Details
		for (WriteOffUploadDetail detail : details) {

			if (detail.getProgress() != EodConstants.PROGRESS_SUCCESS) {
				continue;
			}

			FinanceWriteoffHeader fwh = financeWriteoffService.getFinanceWriteoffDetailById(detail.getReferenceID(),
					"_View", null, FinServiceEvent.WRITEOFFPAY);
			setWriteOffTotals(fwh);

			AuditHeader auditHeader = getAuditHeader(fwh, PennantConstants.TRAN_WF);

			fwh.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);
			TransactionStatus transactionStatus = getTransactionStatus();

			try {
				this.financeWriteoffService.doApprove(auditHeader);
				saveLog(detail, header);
				this.transactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				if (transactionStatus != null) {
					transactionManager.rollback(transactionStatus);
				}

				String error = StringUtils.trimToEmpty(e.getMessage());

				if (error.length() > 1999) {
					error = error.substring(0, 1999);
				}

				detail.setProgress(EodConstants.PROGRESS_FAILED);
				detail.setErrorDesc(error);
				this.writeOffUploadDAO.update(detail);
			}

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				updateFailRecords(1, 1, detail.getHeaderId());
			}
		}
	}

	private void saveLog(WriteOffUploadDetail detail, FileUploadHeader header) {
		detail.setReceiptId(finReceiptHeaderDAO.getMaxReceiptIdFinRef(detail.getReference()));
		detail.setEvent(UploadTypes.WRITE_OFF.name());

		writeOffUploadDAO.saveLog(detail, header);
	}

	private void setWriteOffTotals(FinanceWriteoffHeader header) {
		FinanceWriteoff fw = header.getFinanceWriteoff();
		if (fw.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) == 0
				&& fw.getWriteoffProfit().compareTo(BigDecimal.ZERO) == 0
				&& fw.getWriteoffSchFee().compareTo(BigDecimal.ZERO) == 0) {

			fw.setWriteoffPrincipal(fw.getUnPaidSchdPri());
			fw.setWriteoffProfit(fw.getUnPaidSchdPft());
			fw.setWriteoffSchFee(fw.getUnpaidSchFee());
		}

		try {
			header.getFinanceDetail().getFinScheduleData()
					.setFinanceScheduleDetails(calScheduleWriteOffDetails(header));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private List<FinanceScheduleDetail> calScheduleWriteOffDetails(FinanceWriteoffHeader financeWriteoffHeader) {
		FinanceWriteoff fw = financeWriteoffHeader.getFinanceWriteoff();

		BigDecimal woPriAmt = fw.getWriteoffPrincipal();
		BigDecimal woPftAmt = fw.getWriteoffProfit();
		BigDecimal woSchFee = fw.getWriteoffSchFee();

		List<FinanceScheduleDetail> effectedFinSchDetails = financeWriteoffHeader.getFinanceDetail()
				.getFinScheduleData().getFinanceScheduleDetails();

		if (CollectionUtils.isNotEmpty(effectedFinSchDetails)) {
			for (int i = 0; i < effectedFinSchDetails.size(); i++) {

				FinanceScheduleDetail curSchdl = effectedFinSchDetails.get(i);

				if (woPriAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPriBal = curSchdl.getPrincipalSchd().subtract(curSchdl.getSchdPriPaid())
							.subtract(curSchdl.getWriteoffPrincipal());
					if (schPriBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPriAmt.compareTo(schPriBal) >= 0) {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(schPriBal));
							woPriAmt = woPriAmt.subtract(schPriBal);
						} else {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(woPriAmt));
							woPriAmt = BigDecimal.ZERO;
						}
					}
				}

				if (woPftAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPftBal = curSchdl.getProfitSchd().subtract(curSchdl.getSchdPftPaid())
							.subtract(curSchdl.getWriteoffProfit());
					if (schPftBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPftAmt.compareTo(schPftBal) >= 0) {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(schPftBal));
							woPftAmt = woPftAmt.subtract(schPftBal);
						} else {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(woPftAmt));
							woPftAmt = BigDecimal.ZERO;
						}
					}
				}

				if (woSchFee.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schFee = curSchdl.getFeeSchd()
							.subtract(curSchdl.getSchdFeePaid().subtract(curSchdl.getWriteoffSchFee()));
					if (schFee.compareTo(BigDecimal.ZERO) > 0) {
						if (woSchFee.compareTo(schFee) >= 0) {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(schFee));
							woSchFee = woSchFee.subtract(schFee);
						} else {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(woSchFee));
							woSchFee = BigDecimal.ZERO;
						}
					}
				}
			}
		}

		return effectedFinSchDetails;
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			writeOffUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(writeOffUploadDAO.getDetails(h1.getId()));
			});

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		WriteOffUploadDetail detail = getDetail(object);

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, WriteOffUploadError.WOUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, WriteOffUploadError.WOUP002);
			return;
		}

		long finID = fm.getFinID();
		boolean isPending = receiptService.isReceiptsPending(finID, Long.MIN_VALUE);
		boolean receiptsQueue = receiptUploadDetailDAO.isReceiptsQueue(reference);
		boolean presentmentsInQueue = finReceiptHeaderDAO.checkPresentmentsInQueue(finID);

		if (isPending || receiptsQueue || presentmentsInQueue) {
			setError(detail, WriteOffUploadError.WOUP004);
			return;
		}

		boolean finExcessAmtExists = finExcessAmountDAO.isFinExcessAmtExists(finID);
		if (finExcessAmtExists) {
			setError(detail, WriteOffUploadError.WOUP005);
			return;
		}

		// check if payable amount present
		List<ManualAdvise> manualAdvise = financeWriteoffService.getPayableAdvises(finID, "");
		if (CollectionUtils.isNotEmpty(manualAdvise)) {
			setError(detail, WriteOffUploadError.WOUP005);
			return;
		}

		String rcdMaintainSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);
		if (StringUtils.isNotEmpty(rcdMaintainSts) && !StringUtils.equals(rcdMaintainSts, FinServiceEvent.WRITEOFF)) {
			String reason = Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts);
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode("WOUP006");
			detail.setErrorDesc(reason);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, WriteOffUploadError.WOUP007);
			return;
		}

		List<String> finEvents = finServiceInstructionDAO.getFinEventByFinRef(reference, "_Temp");
		if (CollectionUtils.isNotEmpty(finEvents)) {
			rcdMaintainSts = finEvents.get(0);
			if (!rcdMaintainSts.equals(FinServiceEvent.WRITEOFF)) {
				String reason = Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts);
				detail.setProgress(EodConstants.PROGRESS_FAILED);
				detail.setErrorCode("WOUP006");
				detail.setErrorDesc(reason);
				return;
			}
		}

		if (fm.isWriteoffLoan()) {
			setError(detail, WriteOffUploadError.WOUP008);
			return;
		}

		boolean isWriteOffInQueue = financeWriteoffService.isWriteoffLoan(finID, "_Temp");

		if (isWriteOffInQueue) {
			setError(detail, WriteOffUploadError.WOUP0010);
			return;
		}

		detail.setReferenceID(finID);

		logger.info("Validating the Data for the reference {}", detail.getReference());
		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		logger.info("Validated the Data for the reference {}", detail.getReference());
	}

	@Override
	public String getSqlQuery() {
		return writeOffUploadDAO.getSqlQuery();
	}

	private void setError(WriteOffUploadDetail detail, WriteOffUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	private AuditHeader getAuditHeader(FinanceWriteoffHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, header);
		return new AuditHeader(String.valueOf(header.getFinReference()), String.valueOf(header.getFinReference()), null,
				null, auditDetail, header.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<>());
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return writeOffUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.WRITE_OFF.name(), this, "WriteOffUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String finReference = ObjectUtil.valueAsString(paramSource.getValue("finReference"));
		boolean recordExist = isInProgress(headerID, finReference);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		WriteOffUploadDetail detail = new WriteOffUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(finReference);
		detail.setRemarks(ObjectUtil.valueAsString(paramSource.getValue("remarks")));

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setWriteOffUploadDAO(WriteOffUploadDAO writeOffUploadDAO) {
		this.writeOffUploadDAO = writeOffUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceWriteoffService(FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

}