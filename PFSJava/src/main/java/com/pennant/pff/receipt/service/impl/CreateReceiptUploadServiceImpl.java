package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.receipt.dao.CreateReceiptUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennant.pff.receipt.validate.CreateReceiptUploadProcessRecord;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CreateReceiptUploadServiceImpl extends AUploadServiceImpl<CreateReceiptUpload> {
	private static final Logger logger = LogManager.getLogger(CreateReceiptUploadServiceImpl.class);

	private ReceiptService receiptService;
	private CreateReceiptUploadDAO createReceiptUploadDAO;
	private CreateReceiptUploadProcessRecord createReceiptUploadProcessRecord;

	public CreateReceiptUploadServiceImpl() {
		super();
	}

	@Override
	protected CreateReceiptUpload getDetail(Object object) {
		if (object instanceof CreateReceiptUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CreateReceiptUpload detail = getDetail(object);

		createReceiptUploadProcessRecord.validate(detail, header);
		createReceiptUploadProcessRecord.validateAllocations(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();
			boolean dedupCheck = SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTUPLOAD_DEDUPCHECK);

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<CreateReceiptUpload> details = createReceiptUploadDAO.getDetails(header.getId());
				int sucessRecords = 0;
				int failRecords = 0;

				for (CreateReceiptUpload receipt : details) {
					receipt.setAppDate(appDate);
					receipt.setDedupCheck(dedupCheck);
					receipt.setAllocations(createReceiptUploadDAO.getAllocations(receipt.getId(), header.getId()));
					doValidate(header, receipt);
					receipt.setUserDetails(header.getUserDetails());

					if (receipt.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						createReceipt(receipt, header);
					}

					header.getUploadDetails().add(receipt);

					if (receipt.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					try {
						createReceiptUploadDAO.update(details);

						header.setSuccessRecords(sucessRecords);
						header.setFailureRecords(failRecords);

						List<FileUploadHeader> headerList = new ArrayList<>();
						headerList.add(header);

						updateHeader(headerList, true);

					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
				}

				logger.info("Processed the File {}", header.getFileName());
			}
		}).start();
	}

	private void createReceipt(CreateReceiptUpload detail, FileUploadHeader header) {
		String entityCode = header.getEntityCode();

		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		rud.setReference(detail.getReference());
		rud.setFinID(detail.getReferenceID());
		rud.setAllocationType(detail.getAllocationType());
		rud.setValueDate(detail.getAppDate());
		rud.setRealizationDate(detail.getAppDate());
		rud.setReceivedDate(detail.getAppDate());
		rud.setReceiptAmount(detail.getReceiptAmount());
		rud.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rud.setReceiptMode(detail.getReceiptMode());
		rud.setSubReceiptMode(detail.getSubReceiptMode());
		rud.setReceiptPurpose(detail.getReceiptPurpose());
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);
		rud.setDepositDate(detail.getDepositDate());
		rud.setBankCode(detail.getBankCode());
		rud.setEffectSchdMethod(detail.getEffectSchdMethod());
		String receiptMode = detail.getReceiptMode();
		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {
			rud.setTransactionRef(detail.getChequeNumber());
			rud.setFavourNumber(detail.getChequeNumber());
		}

		List<UploadAlloctionDetail> list = new ArrayList<>();

		Map<String, BigDecimal> waivedAmounts = new HashMap<>();
		for (CreateReceiptUpload alloc : detail.getAllocations()) {
			if (alloc.getCode().contains("_W")) {
				String code = alloc.getCode().split("_")[0];

				waivedAmounts.put(code, alloc.getAmount());
			}
		}

		if (AllocationType.MANUAL.equals(detail.getAllocationType())) {
			for (CreateReceiptUpload alloc : detail.getAllocations()) {
				UploadAlloctionDetail uad = new UploadAlloctionDetail();

				uad.setRootId(String.valueOf(alloc.getFeeId()));
				uad.setAllocationType(Allocation.getCode(alloc.getCode()));
				uad.setReferenceCode(alloc.getCode());
				uad.setStrPaidAmount(String.valueOf(alloc.getAmount()));
				uad.setPaidAmount(alloc.getAmount());
				if (waivedAmounts.get(alloc.getCode()) != null) {
					uad.setWaivedAmount(waivedAmounts.get(alloc.getCode()));
				}

				list.add(uad);
			}
		}

		rud.setListAllocationDetails(list);

		if (AllocationType.MANUAL.equals(detail.getAllocationType()) && list != null
				&& !(detail.getReceiptAmount().equals(getSumOfAllocations(list)))) {
			setFailureStatus(detail, "", "RECEIPT Amount and Allocations amount should be same");
			return;
		}

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		LoggedInUser userDetails = detail.getUserDetails();

		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		fsi.setLoggedInUser(userDetails);

		fsi.setKnockOffReceipt(true);
		if (FinanceConstants.EARLYSETTLEMENT.equals(detail.getReceiptPurpose())) {
			fsi.setClosureType(detail.getClosureType());
		}

		FinanceDetail fd = null;

		TransactionStatus txStatus = getTransactionStatus();

		try {
			fd = receiptService.receiptTransaction(fsi);
			transactionManager.commit(txStatus);
		} catch (AppException e) {
			setFailureStatus(detail, e.getMessage());

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			return;
		}

		if (fd == null) {
			setFailureStatus(detail, "Finance Detail is null.");
			return;
		}

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			setFailureStatus(detail, schd.getErrorDetails().get(0));
		} else {
			detail.setReceiptID(fd.getReceiptId());
			setSuccesStatus(detail);
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(createReceiptUploadDAO.getDetails(h1.getId()));
			});

			createReceiptUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	private BigDecimal getSumOfAllocations(List<UploadAlloctionDetail> list) {
		BigDecimal sum = BigDecimal.ZERO;
		for (UploadAlloctionDetail cru : list) {
			sum = sum.add(cru.getPaidAmount());
		}

		return sum;
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.CREATE_RECEIPT.name(), createReceiptUploadProcessRecord, this,
				"CreateReceiptUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		// Implemented in process record.
	}

	@Override
	public CreateReceiptUploadProcessRecord getProcessRecord() {
		return createReceiptUploadProcessRecord;
	}

	@Override
	public String getSqlQuery() {
		return createReceiptUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setCreateReceiptUploadDAO(CreateReceiptUploadDAO createReceiptUploadDAO) {
		this.createReceiptUploadDAO = createReceiptUploadDAO;
	}

	@Autowired
	public void setCreateReceiptUploadProcessRecord(CreateReceiptUploadProcessRecord createReceiptUploadProcessRecord) {
		this.createReceiptUploadProcessRecord = createReceiptUploadProcessRecord;
	}
}