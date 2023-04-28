package com.pennant.pff.branchchange.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.branchchange.dao.BranchChangeUploadDAO;
import com.pennant.pff.branchchange.dao.BranchMigrationDAO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.file.UploadTypes;

public class BranchChangeUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(BranchChangeUploadServiceImpl.class);
	private BranchChangeUploadDAO branchChangeUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private BranchDAO branchDAO;
	private BranchMigrationDAO branchMigrationDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ValidateRecord branchChangeUploadValidateRecord;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BranchChangeUpload detail = null;
		boolean isFinActive = false;

		if (object instanceof BranchChangeUpload) {
			detail = (BranchChangeUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();
		String branchcode = detail.getBranchCode();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		Long finID = financeMainDAO.getFinID(reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, BranchChangeUploadError.BC_01);
			return;
		}

		if (finID == null) {
			setError(detail, BranchChangeUploadError.BC_02);
			return;
		}

		if (!financeMainDAO.isFinActive(finID)) {
			setError(detail, BranchChangeUploadError.BC_03);
			return;
		}

		if (StringUtils.isBlank(branchcode)) {
			setError(detail, BranchChangeUploadError.BC_06);
			return;
		}

		detail.setReferenceID(finID);

		FinanceMain fm = financeMainDAO.getFinBasicDetails(finID, "");
		if (fm.getRcdMaintainSts() != null || fm.isUnderSettlement()) {
			setError(detail, BranchChangeUploadError.BC_07);
			return;
		}

		if (!branchDAO.isActiveBranch(branchcode)) {
			setError(detail, BranchChangeUploadError.BC_05);
			return;
		}

		if (fm.getFinBranch().equals(branchcode)) {
			setError(detail, BranchChangeUploadError.BC_04);
			return;
		}

		detail.setOldBranch(fm.getFinBranch());
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

				List<BranchChangeUpload> details = branchChangeUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (BranchChangeUpload bcu : details) {
					doValidate(header, bcu);

					if (bcu.getErrorCode() != null) {
						bcu.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						bcu.setProgress(EodConstants.PROGRESS_SUCCESS);
						bcu.setErrorCode("");
						bcu.setErrorDesc("");
						bcu.setUserDetails(header.getUserDetails());

						Long referenceID = bcu.getReferenceID();
						String reference = bcu.getReference();
						String branchCode = bcu.getBranchCode();

						branchMigrationDAO.updateFinanceMain(referenceID, branchCode);
						branchMigrationDAO.updateFinODDetails(referenceID, branchCode);
						branchMigrationDAO.updateFinRepayDeatils(referenceID, branchCode);
						branchMigrationDAO.updateFinRpyQueue(referenceID, branchCode);
						branchMigrationDAO.updateFinPFTDetails(referenceID, branchCode);
						branchMigrationDAO.updatePaymentRecoveryDetails(reference, branchCode);
						branchMigrationDAO.updateFinSuspHead(referenceID, branchCode);
						branchMigrationDAO.updateFinSuspDetails(referenceID, branchCode);
						branchMigrationDAO.updateLegalDetails(reference, branchCode);

						doBranchChange(referenceID, bcu.getOldBranch(), branchCode, appDate);
					}

					if (bcu.getProgress() == EodConstants.PROGRESS_FAILED) {
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

					branchChangeUploadDAO.update(details);

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

			branchChangeUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
		return branchChangeUploadDAO.getSqlQuery();
	}

	private void doBranchChange(Long finID, String oldBranch, String newBranch, Date appDate) {
		String eventCode = AccountingEvent.BRNCHG;
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, true);

		Long accountingID = AccountingEngine.getAccountSetID(fm.getFinType(), eventCode, moduleID);

		if (accountingID == null || accountingID <= 0) {
			logger.debug("Accounting Set not found with {} Event and {} Loan Type", eventCode, fm.getFinType());
			return;
		}

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCode = new AEAmountCodes();
		aeEvent.setAeAmountCodes(amountCode);

		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostDate(appDate);
		aeEvent.setValueDate(appDate);

		aeEvent.setBranch(newBranch);
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setCustID(fm.getCustID());

		amountCode.setFinType(aeEvent.getFinType());

		Map<String, Object> dataMap = amountCode.getDeclaredFieldValues();

		List<Accounts> accountsList = branchMigrationDAO.getAccounts(fm.getFinReference(), oldBranch);

		for (Accounts account : accountsList) {
			if (AccountingEvent.LIABILITY.equals(account.getGroupCode())) {
				dataMap.put("L_" + account.getAcType() + "_old_branch".toUpperCase(), account.getAcBalance());
				dataMap.put("L_" + account.getAcType() + "_new_branch".toUpperCase(), account.getAcBalance());
			}

			if ("ASSET".equals(account.getGroupCode())) {
				dataMap.put("A_" + account.getAcType() + "_old_branch".toUpperCase(), account.getAcBalance());
				dataMap.put("A_" + account.getAcType() + "_new_branch".toUpperCase(), account.getAcBalance());
			}
		}

		aeEvent.setDataMap(dataMap);
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(appDate);

		postingsPreparationUtil.postAccountingEOD(aeEvent);

		postingsPreparationUtil.saveAccountingEOD(aeEvent.getReturnDataSet());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.BRANCH_CHANGE.name(), branchChangeUploadValidateRecord, this,
				"BranchChangeUploadHeader");

	}

	private void setError(BranchChangeUpload detail, BranchChangeUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setBranchChangeUploadDAO(BranchChangeUploadDAO branchChangeUploadDAO) {
		this.branchChangeUploadDAO = branchChangeUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	@Autowired
	public void setBranchMigrationDAO(BranchMigrationDAO branchMigrationDAO) {
		this.branchMigrationDAO = branchMigrationDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return branchChangeUploadValidateRecord;
	}

	@Autowired
	public void setBranchChangeUploadValidateRecord(ValidateRecord branchChangeUploadValidateRecord) {
		this.branchChangeUploadValidateRecord = branchChangeUploadValidateRecord;
	}

}