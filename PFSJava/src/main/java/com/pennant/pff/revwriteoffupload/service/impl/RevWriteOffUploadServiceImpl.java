package com.pennant.pff.revwriteoffupload.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.revwriteoffupload.dao.RevWriteOffUploadDAO;
import com.pennant.pff.revwriteoffupload.model.RevWriteOffUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennant.pff.writeoffupload.exception.WriteOffUploadError;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennapps.core.util.ObjectUtil;

public class RevWriteOffUploadServiceImpl extends AUploadServiceImpl<RevWriteOffUploadDetail> {
	private static final Logger logger = LogManager.getLogger(RevWriteOffUploadServiceImpl.class);

	private RevWriteOffUploadDAO revWriteOffUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FeeTypeDAO feeTypeDAO;

	public RevWriteOffUploadServiceImpl() {
		super();
	}

	@Override
	protected RevWriteOffUploadDetail getDetail(Object object) {
		if (object instanceof RevWriteOffUploadDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<RevWriteOffUploadDetail> details = revWriteOffUploadDAO.getDetails(header.getId());

				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				List<String> key = new ArrayList<>();

				for (RevWriteOffUploadDetail detail : details) {
					String reference = detail.getReference();
					detail.setAppDate(appDate);
					if (key.contains(reference)) {
						setError(detail, WriteOffUploadError.WOUP009);
						continue;
					}

					key.add(reference);

					doValidate(header, detail);
				}

				logger.info("Reverse WriteOff Upload Process is Initiated for the Header ID {}", header.getId());

				processRevWriteOffLoan(header, details);

				logger.info("Reverse WriteOff Upload Process is Completed for the Header ID {}", header.getId());

				try {
					logger.info("Processed the File {}", header.getFileName());

					revWriteOffUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headerList, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();

	}

	/**
	 * Method for processing Success Writeoff Records for Writeoff reversal Execution
	 * 
	 * @param header
	 */
	private void processRevWriteOffLoan(FileUploadHeader header, List<RevWriteOffUploadDetail> details) {
		for (RevWriteOffUploadDetail detail : details) {
			if (detail.getProgress() != EodConstants.PROGRESS_SUCCESS) {
				continue;
			}

			long finId = detail.getReferenceID();

			TransactionStatus txnStatus = getTransactionStatus();

			try {
				if (!executeAccountingProcess(detail, finId)) {
					setFailureStatus(detail, "",
							"Postings Execution failed for Loan Reference : " + detail.getReference());
					return;
				}

				updateWriteOffStatus(finId);

				saveLog(detail, header);

				this.transactionManager.commit(txnStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				if (txnStatus != null) {
					transactionManager.rollback(txnStatus);
				}

				setFailureStatus(detail, e.getMessage());
			}
		}
	}

	/**
	 * Method for preparing Accounting and executing against Writeoff Loan amount reversals to Normal state
	 * 
	 * @param detail
	 * @param finId
	 */
	private boolean executeAccountingProcess(RevWriteOffUploadDetail detail, long finId) {
		String reference = detail.getReference();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finId, "", false);

		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finId, "", false);
		FinanceProfitDetail fpd = profitDetailsDAO.getFinProfitDetailsById(finId);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAppDate(detail.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent = AEAmounts.procAEAmounts(fm, schedules, fpd, AccountingEvent.REV_WRITEOFF, aeEvent.getAppDate(),
				aeEvent.getAppDate());

		aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(fm.getFinType(),
				AccountingEvent.REV_WRITEOFF, FinanceConstants.MODULEID_FINTYPE));

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		long receiptId = revWriteOffUploadDAO.getReceiptIdByRef(reference);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap.putAll(preparePaidAmount(reference, receiptId));
		dataMap.putAll(prepareAdviseMovements(reference, receiptId));

		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		return aeEvent.isPostingSucess();
	}

	private Map<String, BigDecimal> prepareAdviseMovements(String reference, long receiptId) {
		List<ManualAdviseMovements> mamList = ManualAdviceUtil
				.getMovements(manualAdviseDAO.getAdvisePaidAmount(receiptId, reference));

		String bounceComponent = feeTypeDAO.getTaxComponent(Allocation.BOUNCE);

		return ManualAdviceUtil.prepareMovementMap(mamList, bounceComponent);
	}

	private Map<String, BigDecimal> preparePaidAmount(String reference, long receiptId) {
		List<ReceiptAllocationDetail> radList = receiptAllocationDetailDAO.getReceiptPaidAmount(receiptId, reference);

		Map<String, BigDecimal> alocMap = new HashMap<>();

		for (ReceiptAllocationDetail rad : radList) {
			String allocType = rad.getAllocationType();
			switch (allocType) {
			case Allocation.PRI:
				alocMap.put("ae_priPaidWO", rad.getPaidAmount());
				break;
			case Allocation.PFT:
				alocMap.put("ae_pftPaidWO", rad.getPaidAmount());
				break;
			default:
				break;
			}
		}
		return alocMap;
	}

	/**
	 * Method for Updating write off Amount Details & status against Loan
	 * 
	 * @param detail
	 * @param finId
	 */
	private void updateWriteOffStatus(long finId) {
		financeScheduleDetailDAO.updateWriteOffDetail(finId);
		financeMainDAO.updateWriteOffStatus(finId, false);
		profitDetailsDAO.updateClosingSts(finId, false);

		revWriteOffUploadDAO.save(financeWriteoffDAO.getFinanceWriteoffById(finId, ""), "");
		financeWriteoffDAO.delete(finId, "");
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(revWriteOffUploadDAO.getDetails(h1.getId()));
			});

			revWriteOffUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	private void saveLog(RevWriteOffUploadDetail detail, FileUploadHeader header) {
		detail.setEvent(UploadTypes.REV_WRITE_OFF.name());
		revWriteOffUploadDAO.saveLog(detail, header);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		RevWriteOffUploadDetail detail = getDetail(object);

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, WriteOffUploadError.WOUP001);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, WriteOffUploadError.WOUP002);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, WriteOffUploadError.WOUP0011);
			return;
		}

		if (!fm.isWriteoffLoan()) {
			setError(detail, WriteOffUploadError.WOUP0012);
			return;
		}

		detail.setReferenceID(fm.getFinID());

		setSuccesStatus(detail);

		logger.info("Validated the Data for the reference {}", reference);
	}

	@Override
	public String getSqlQuery() {
		return revWriteOffUploadDAO.getSqlQuery();
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.REV_WRITE_OFF.name(), this, "RevWriteOffUploadHeader");
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

		RevWriteOffUploadDetail detail = new RevWriteOffUploadDetail();
		detail.setHeaderId(headerID);
		detail.setReference(finReference);
		detail.setRemarks(ObjectUtil.valueAsString(paramSource.getValue("remarks")));

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	private void setError(RevWriteOffUploadDetail detail, WriteOffUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return revWriteOffUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Autowired
	public void setRevWriteOffUploadDAO(RevWriteOffUploadDAO revWriteOffUploadDAO) {
		this.revWriteOffUploadDAO = revWriteOffUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Autowired
	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	@Autowired
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}