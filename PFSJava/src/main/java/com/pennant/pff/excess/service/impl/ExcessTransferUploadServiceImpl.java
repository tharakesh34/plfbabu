package com.pennant.pff.excess.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.ExcessHead;
import com.pennant.pff.excess.ExcessTransferError;
import com.pennant.pff.excess.dao.ExcessTransferUploadDAO;
import com.pennant.pff.excess.model.ExcessTransferUpload;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennant.pff.excess.service.ExcessTransferService;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class ExcessTransferUploadServiceImpl extends AUploadServiceImpl<ExcessTransferUpload> {
	private static final Logger logger = LogManager.getLogger(ExcessTransferUploadServiceImpl.class);

	private ExcessTransferUploadDAO excessTransferUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ExcessTransferService excessTransferService;

	public ExcessTransferUploadServiceImpl() {
		super();
	}

	@Override
	protected ExcessTransferUpload getDetail(Object object) {
		if (object instanceof ExcessTransferUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ExcessTransferUpload detail = getDetail(object);

		String reference = detail.getReference();
		detail.setReference(reference);

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, ExcessTransferError.EXT_001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, ExcessTransferError.EXT_002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, ExcessTransferError.EXT_003);
			return;
		}

		detail.setReferenceID(fm.getFinID());

		String transferFrom = detail.getTransferFromType();
		String transferToType = detail.getTransferToType();

		if (transferFrom.equals(transferToType)) {
			setError(detail, ExcessTransferError.EXT_006);
			return;
		}

		if (!ExcessHead.isValidExcessTransferHead(transferFrom)) {
			setError(detail, ExcessTransferError.EXT_007);
			return;
		}

		if (!ExcessHead.isValidExcessTransferHead(transferToType)) {
			setError(detail, ExcessTransferError.EXT_007);
			return;
		}

		if (excessTransferUploadDAO.isDuplicateExists(reference, transferFrom, detail.getHeaderId())) {
			setError(detail, ExcessTransferError.EXT_005);
			return;
		}

		BigDecimal balanceAmount = excessTransferUploadDAO.getBalanceAmount(fm.getFinID(), transferFrom);

		if (balanceAmount.compareTo(detail.getTransferAmount()) < 0) {
			setError(detail, ExcessTransferError.EXT_004);
			return;
		}

		setSuccesStatus(detail);
		detail.setStatus("C");
	}

	private void setError(ExcessTransferUpload detail, ExcessTransferError error) {
		setFailureStatus(detail, error.name(), error.description());
		detail.setStatus("R");
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<ExcessTransferUpload> details = excessTransferUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);
				List<ExcessTransferUpload> process = new ArrayList<>();

				for (ExcessTransferUpload fc : details) {
					fc.setAppDate(appDate);
					doValidate(header, fc);

					if (fc.getProgress() == EodConstants.PROGRESS_FAILED) {
						setFailureStatus(fc);
					} else {
						setSuccesStatus(fc);
						process.add(fc);
					}
				}

				TransactionStatus txStatus = getTransactionStatus();
				try {
					process(process);
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				try {
					excessTransferUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);

					updateHeader(headerList, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

				logger.info("Processed the File {}", header.getFileName());
			}

		}).start();

	}

	private void process(List<ExcessTransferUpload> process) {
		for (ExcessTransferUpload exc : process) {
			List<FinExcessTransfer> transferList = new ArrayList<>();

			List<FinExcessAmount> existingExcess = finExcessAmountDAO.getExcessAmountsByRefAndType(exc.getReferenceID(),
					exc.getTransferFromType());

			List<FinExcessAmount> excessList = existingExcess.stream()
					.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate())).toList();

			BigDecimal transferAmount = exc.getTransferAmount();
			for (FinExcessAmount excess : excessList) {
				BigDecimal amount = excess.getBalanceAmt();

				if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}

				if (amount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				if (transferAmount.compareTo(amount) >= 0) {
					transferAmount = transferAmount.subtract(amount);
				} else {
					amount = transferAmount;
					transferAmount = BigDecimal.ZERO;
				}

				FinExcessTransfer transfer = new FinExcessTransfer();

				transfer.setId(excessTransferUploadDAO.getNextValue());
				transfer.setFinId(exc.getReferenceID());
				transfer.setFinReference(exc.getReference());
				transfer.setTransferFromType(exc.getTransferFromType());
				transfer.setTransferToType(exc.getTransferToType());
				transfer.setTransferDate(excess.getValueDate() == null ? exc.getAppDate() : excess.getValueDate());
				transfer.setTransferAmount(amount);
				transfer.setTransferFromId(excess.getExcessID());
				transfer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				transfer.setLastMntBy(exc.getApprovedBy());
				transfer.setLastMntOn(exc.getApprovedOn());
				transfer.setCreatedBy(exc.getCreatedBy());
				transfer.setCreatedOn(exc.getCreatedOn());
				transfer.setApprovedBy(exc.getApprovedBy());
				transfer.setApprovedOn(exc.getApprovedOn());

				transferList.add(transfer);

				if (transferList.size() == PennantConstants.CHUNK_SIZE) {
					approveExcess(exc, transferList);
				}
			}

			approveExcess(exc, transferList);
		}
	}

	private void approveExcess(ExcessTransferUpload exc, List<FinExcessTransfer> transferList) {
		for (FinExcessTransfer fet : transferList) {
			try {
				excessTransferService.doApprove(getAuditHeader(fet, PennantConstants.TRAN_WF));
			} catch (Exception e) {
				setFailureStatus(exc, getErrorMessage(e));
				exc.setStatus("R");
			}
		}
	}

	private AuditHeader getAuditHeader(FinExcessTransfer transfer, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, transfer.getBefImage(), transfer);
		return new AuditHeader(transfer.getFinReference(), null, null, null, ad, transfer.getUserDetails(), null);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(excessTransferUploadDAO.getDetails(h1.getId()));
			});

			excessTransferUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public void uploadProcess() {
		uploadProcess(UploadTypes.EXCESS_TRANSFER.name(), this, "ExcessTransferUpload");
	}

	@Override
	public String getSqlQuery() {
		return excessTransferUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		ExcessTransferUpload transfer = (ExcessTransferUpload) ObjectUtil.valueAsObject(paramSource,
				ExcessTransferUpload.class);

		transfer.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		transfer.setHeaderId(header.getId());
		transfer.setAppDate(header.getAppDate());

		doValidate(header, transfer);

		updateProcess(header, transfer, paramSource);

		header.getUploadDetails().add(transfer);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setExcessTransferUploadDAO(ExcessTransferUploadDAO excessTransferUploadDAO) {
		this.excessTransferUploadDAO = excessTransferUploadDAO;
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
	public void setExcessTransferService(ExcessTransferService excessTransferService) {
		this.excessTransferService = excessTransferService;
	}

}