package com.pennant.pff.branchchange.service.impl;

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

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.pff.branchchange.dao.BranchChangeUploadDAO;
import com.pennant.pff.branchchange.dao.BranchMigrationDAO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class BranchChangeUploadServiceImpl extends AUploadServiceImpl<BranchChangeUpload> {
	private static final Logger logger = LogManager.getLogger(BranchChangeUploadServiceImpl.class);
	private BranchChangeUploadDAO branchChangeUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private BranchDAO branchDAO;
	private BranchMigrationDAO branchMigrationDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinServiceInstrutionDAO finServiceInstrutionDAO;

	@Override
	protected BranchChangeUpload getDetail(Object object) {
		if (object instanceof BranchChangeUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BranchChangeUpload detail = getDetail(object);

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
		boolean isundermaintainance = finServiceInstrutionDAO.isFinServiceInstExists(finID, "_temp");

		if (isundermaintainance || fm.getRcdMaintainSts() != null) {
			setError(detail, BranchChangeUploadError.BC_07);
			return;
		}

		boolean isInSettlement = branchChangeUploadDAO.isInSettlement(finID, "_temp");

		if (isInSettlement) {
			setError(detail, BranchChangeUploadError.BC_08);
			return;
		}

		boolean isInlinkingDelinking = branchChangeUploadDAO.isInlinkingDelinking(finID, "_temp");

		if (isInlinkingDelinking) {
			setError(detail, BranchChangeUploadError.BC_09);
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

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<BranchChangeUpload> details = branchChangeUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);

				for (BranchChangeUpload bcu : details) {
					doValidate(header, bcu);

					if (bcu.getErrorCode() != null) {
						setFailureStatus(bcu);
					} else {
						setSuccesStatus(bcu);
						bcu.setUserDetails(header.getUserDetails());

						process(appDate, bcu);
					}

					header.getUploadDetails().add(bcu);
				}

				logger.info("Processed the File {}", header.getFileName());

				branchChangeUploadDAO.update(details);

				List<FileUploadHeader> headerList = new ArrayList<>();
				headerList.add(header);
				updateHeader(headers, true);
			}
		}).start();
	}

	private void process(Date appDate, BranchChangeUpload bcu) {
		Long referenceID = bcu.getReferenceID();
		String reference = bcu.getReference();
		String branchCode = bcu.getBranchCode();

		TransactionStatus txStatus = getTransactionStatus();

		try {
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

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(bcu, e.getMessage());
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(branchChangeUploadDAO.getDetails(h1.getId()));
			});

			branchChangeUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
		uploadProcess(UploadTypes.BRANCH_CHANGE.name(), this, "BranchChangeUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource parameterSource) throws Exception {
		logger.debug(Literal.ENTERING);

		BranchChangeUpload detail = (BranchChangeUpload) ObjectUtil.valueAsObject(parameterSource,
				BranchChangeUpload.class);

		detail.setReference(ObjectUtil.valueAsString(parameterSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		Long recordSeq = (Long) parameterSource.getValue("RecordSeq");

		detail.setHeaderId(header.getId());
		detail.setAppDate(header.getAppDate());
		detail.setRecordSeq(recordSeq);

		doValidate(header, detail);

		updateProcess(header, detail, parameterSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	private void setError(BranchChangeUpload detail, BranchChangeUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
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

	@Autowired
	public void setFinServiceInstrutionDAO(FinServiceInstrutionDAO finServiceInstrutionDAO) {
		this.finServiceInstrutionDAO = finServiceInstrutionDAO;
	}

}