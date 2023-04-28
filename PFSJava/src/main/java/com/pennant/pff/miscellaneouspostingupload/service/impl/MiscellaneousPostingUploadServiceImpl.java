package com.pennant.pff.miscellaneouspostingupload.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.miscellaneousposting.upload.MiscellaneousPostingUpload;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.accounting.PostAgainst;
import com.pennant.pff.miscellaneouspostingupload.dao.MiscellaneousPostingUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;

public class MiscellaneousPostingUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(MiscellaneousPostingUploadServiceImpl.class);

	private MiscellaneousPostingUploadDAO miscellaneousPostingUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private AccountMappingDAO accountMappingDAO;
	private JVPostingService jVPostingService;
	private JVPostingDAO jVPostingDAO;
	private MiscellaneousPostingUploadValidateRecord miscellaneousPostingUploadValidateRecord;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		MiscellaneousPostingUpload detail = null;

		if (object instanceof MiscellaneousPostingUpload) {
			detail = (MiscellaneousPostingUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		Long finID = financeMainDAO.getFinID(reference);
		detail.setReferenceID(finID);
		BigDecimal txnamt = detail.getTxnAmount();

		if (StringUtils.isNotBlank(reference) && finID == null) {
			setError(detail, MiscellaneousPostingUploadError.MP02);
			return;
		}

		String batchnameregex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM);
		if (StringUtils.isNotBlank(detail.getBatchName())
				&& !Pattern.compile(batchnameregex).matcher(detail.getBatchName()).matches()) {
			setError(detail, MiscellaneousPostingUploadError.MP08);
			return;
		}

		if (StringUtils.isNotBlank(reference)) {
			validateLoan(detail, reference);
		}

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		if (!accountMappingDAO.isValidAccount(detail.getCreditGL())) {
			setError(detail, MiscellaneousPostingUploadError.MP04);
			return;
		}

		if (!accountMappingDAO.isValidAccount(detail.getDebitGL())) {
			setError(detail, MiscellaneousPostingUploadError.MP05);
			return;
		}

		if (txnamt == null) {
			setError(detail, MiscellaneousPostingUploadError.MP09);
			return;
		}

		if (BigDecimal.ZERO.compareTo(txnamt) >= 0) {
			setError(detail, MiscellaneousPostingUploadError.MP010);
			return;
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();
			String currency = SysParamUtil.getValue("APP_DFT_CURR").toString();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<MiscellaneousPostingUpload> details = miscellaneousPostingUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;
				List<MiscellaneousPostingUpload> mList = new ArrayList<>();

				for (MiscellaneousPostingUpload detail : details) {
					detail.setAppDate(appDate);
					detail.setCurrencyParm(currency);

					doValidate(header, detail);

					if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
						failRecords++;
						continue;
					}

					detail.setProgress(EodConstants.PROGRESS_SUCCESS);
					detail.setErrorCode("");
					detail.setErrorDesc("");
					detail.setUserDetails(header.getUserDetails());

					mList.add(detail);
					sucessRecords++;
				}

				int failureCount = process(mList);

				try {
					header.setSuccessRecords(sucessRecords - failureCount);
					header.setFailureRecords(failRecords + failureCount);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					txStatus = transactionManager.getTransaction(txDef);

					miscellaneousPostingUploadDAO.update(details);

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

	private int process(List<MiscellaneousPostingUpload> mList) {
		Map<String, List<MiscellaneousPostingUpload>> map = new LinkedHashMap<>();

		for (MiscellaneousPostingUpload mpus : mList) {
			List<MiscellaneousPostingUpload> newList = new ArrayList<>();
			MiscellaneousPostingUpload mpu = new MiscellaneousPostingUpload();

			mpu.setBatchName(mpus.getBatchName());
			mpu.setBatchPurpose(mpus.getBatchPurpose());
			mpu.setReference(mpus.getReference());
			mpu.setAppDate(mpus.getAppDate());
			mpu.setCurrencyParm(mpus.getCurrencyParm());
			mpu.setCreditGL(mpus.getCreditGL());
			mpu.setDebitGL(mpus.getDebitGL());
			mpu.setTxnAmount(mpus.getTxnAmount());
			mpu.setValueDate(mpus.getValueDate());
			mpu.setNarrLine1(mpus.getNarrLine1());
			mpu.setNarrLine2(mpus.getNarrLine2());
			mpu.setNarrLine3(mpus.getNarrLine3());
			mpu.setNarrLine4(mpus.getNarrLine4());
			mpu.setId(mpus.getId());
			mpu.setUserDetails(mpus.getUserDetails());

			String returncode = mpus.getBatchName().concat("$").concat(mpus.getBatchPurpose()).concat("$")
					.concat(mpus.getReference());

			if (map.containsKey(returncode)) {
				newList = map.get(returncode);
			} else {
				newList = new ArrayList<>();
			}

			newList.add(mpu);
			map.put(returncode, newList);
		}

		List<AuditHeader> auditList = new ArrayList<>();

		for (Map.Entry<String, List<MiscellaneousPostingUpload>> entry : map.entrySet()) {

			List<JVPostingEntry> jVPostingEntryList = new ArrayList<>();

			List<MiscellaneousPostingUpload> mpuList = entry.getValue();
			JVPosting jVPosting = new JVPosting();
			int newRecord = 0;
			Long batchReference = jVPostingDAO.createBatchReference();

			miscellaneousPostingUploadDAO.updateBatchReference(mpuList, batchReference);

			for (MiscellaneousPostingUpload mpus : mpuList) {
				String postingDivision = financeMainDAO.getLovDescFinDivisionByReference(mpus.getReference());
				FinanceMain fm = financeMainDAO.getEntityByRef(mpus.getReference());
				mpus.setBatchReference(batchReference);
				jVPostingEntryList.add(getJV(mpus, mpus.getDebitGL(), AccountConstants.TRANCODE_DEBIT,
						AccountConstants.TRANTYPE_DEBIT));
				jVPostingEntryList.add(getJV(mpus, mpus.getCreditGL(), AccountConstants.TRANCODE_CREDIT,
						AccountConstants.TRANTYPE_CREDIT));

				if (newRecord == 0) {
					jVPosting.setJVPostingEntrysList(jVPostingEntryList);

					String currency = mpus.getCurrencyParm();
					Date postingDate = mpus.getAppDate();

					jVPosting.setBatchReference(mpus.getBatchReference());
					jVPosting.setReference(mpus.getReference());
					jVPosting.setBatch(mpus.getBatchName());
					jVPosting.setCurrency(currency);
					jVPosting.setBatchPurpose(mpus.getBatchPurpose());
					jVPosting.setBranch(fm.getFinBranch());
					jVPosting.setPostingDate(postingDate);
					jVPosting.setPostAgainst(PostAgainst.LOAN.code());
					jVPosting.setPostingDivision(postingDivision);
					jVPosting.setRequestSource(RequestSource.UPLOAD);
					jVPosting.setUserDetails(mpus.getUserDetails());
					jVPosting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					jVPosting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					jVPosting.setUploadID(mpus.getId());
					jVPosting.setLastMntBy(mpus.getUserDetails().getUserId());
					jVPosting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				}

				newRecord++;
			}

			jVPosting.setCreditsCount(getTxnsSize(jVPostingEntryList, AccountConstants.TRANCODE_CREDIT));
			jVPosting.setTotCreditsByBatchCcy(getTxnAmount(jVPostingEntryList, AccountConstants.TRANCODE_CREDIT));
			jVPosting.setDebitCount(getTxnsSize(jVPostingEntryList, AccountConstants.TRANCODE_DEBIT));
			jVPosting.setTotDebitsByBatchCcy(getTxnAmount(jVPostingEntryList, AccountConstants.TRANCODE_DEBIT));

			AuditHeader auditHeader = getAuditHeader(jVPosting, PennantConstants.TRAN_WF);
			auditHeader.getAuditDetail().setModelData(jVPosting);

			auditList.add(auditHeader);
		}

		int failureCount = 0;
		for (AuditHeader auditHeader : auditList) {
			AuditHeader ah = jVPostingService.doApprove(auditHeader);

			JVPosting upload = (JVPosting) auditHeader.getModelData();

			List<Long> headerIDs = new ArrayList<>();

			if (ah.getErrorMessage() != null) {
				headerIDs.add(upload.getUploadID());
				ErrorDetail ed = ah.getErrorMessage().get(0);
				miscellaneousPostingUploadDAO.update(headerIDs, ed.getCode(), ed.getMessage(),
						EodConstants.PROGRESS_FAILED);
				failureCount++;
			}
		}

		return failureCount;
	}

	private BigDecimal getTxnAmount(List<JVPostingEntry> list, String txnCode) {
		BigDecimal amount = BigDecimal.ZERO;
		for (JVPostingEntry entry : list) {
			if (txnCode.equals(entry.getTxnCode())) {
				amount = amount.add(entry.getTxnAmount());
			}
		}

		return amount;
	}

	private int getTxnsSize(List<JVPostingEntry> list, String txnCode) {
		int count = 0;
		for (JVPostingEntry entry : list) {
			if (txnCode.equals(entry.getTxnCode())) {
				count = count + 1;
			}
		}

		return count;
	}

	private AuditHeader getAuditHeader(JVPosting jvp, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, jvp.getBefImage(), jvp);
		return new AuditHeader(null, null, null, null, auditDetail, jvp.getUserDetails(), new HashMap<>());
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			miscellaneousPostingUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private JVPostingEntry getJV(MiscellaneousPostingUpload detail, String account, String txnCode, String txnEntry) {
		String currency = detail.getCurrencyParm();
		Date postingDate = detail.getAppDate();

		AccountMapping am = accountMappingDAO.getAccountMapping(account, "_AView");

		JVPostingEntry entry = new JVPostingEntry();
		entry.setBatchReference(detail.getBatchReference());
		entry.setAccount(account);
		entry.setAccCCy(currency);
		entry.setTxnCCy(currency);
		entry.setTxnAmount(detail.getTxnAmount());
		entry.setTxnAmount_Ac(detail.getTxnAmount().negate());
		entry.setTxnCode(txnCode);
		entry.setTxnEntry(txnEntry);
		entry.setValueDate(detail.getValueDate());
		entry.setPostingDate(postingDate);
		entry.setNarrLine1(detail.getNarrLine1());
		entry.setNarrLine2(detail.getNarrLine2());
		entry.setNarrLine3(detail.getNarrLine3());
		entry.setNarrLine4(detail.getNarrLine4());
		entry.setAcType(am.getAccountType());
		entry.setAccountName(am.getAccountTypeDesc());
		entry.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		return entry;
	}

	@Override
	public String getSqlQuery() {
		return miscellaneousPostingUploadDAO.getSqlQuery();
	}

	private void validateLoan(MiscellaneousPostingUpload detail, String reference) {
		FinanceMain fm = financeMainDAO.getFinanceMain(reference, TableType.MAIN_TAB);

		if (fm == null) {
			setError(detail, MiscellaneousPostingUploadError.MP02);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, MiscellaneousPostingUploadError.MP03);
			return;
		}
	}

	private void setError(MiscellaneousPostingUpload detail, MiscellaneousPostingUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.MISCELLANEOUS_POSTING.name(), miscellaneousPostingUploadValidateRecord, this,
				"MiscellaneousPostingUploadHeader");

	}

	@Autowired
	public void setMiscellaneousPostingUploadDAO(MiscellaneousPostingUploadDAO miscellaneousPostingUploadDAO) {
		this.miscellaneousPostingUploadDAO = miscellaneousPostingUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setAccountMappingDAO(AccountMappingDAO accountMappingDAO) {
		this.accountMappingDAO = accountMappingDAO;
	}

	@Autowired
	public void setJVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	@Override
	public MiscellaneousPostingUploadValidateRecord getValidateRecord() {
		return miscellaneousPostingUploadValidateRecord;
	}

	@Autowired
	public void setMiscellaneousPostingUploadValidateRecord(
			MiscellaneousPostingUploadValidateRecord miscellaneousPostingUploadValidateRecord) {
		this.miscellaneousPostingUploadValidateRecord = miscellaneousPostingUploadValidateRecord;
	}

	@Autowired
	public void setjVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

}
