package com.pennant.pff.branchchange.service.impl;

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

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.pff.branchchange.dao.BranchChangeUploadDAO;
import com.pennant.pff.branchchange.dao.BranchMigrationDAO;
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

		String rcdMntnSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID);

		if (StringUtils.isNotEmpty(financeMainDAO.getFinanceMainByRcdMaintenance(finID))) {
			setError(detail, BranchChangeUploadError.BC_07, rcdMntnSts);
			return;
		}

		if (branchChangeUploadDAO.isInSettlement(finID, "_temp")) {
			setError(detail, BranchChangeUploadError.BC_08);
			return;
		}

		if (branchChangeUploadDAO.isInlinkingDelinking(finID, "_temp")) {
			setError(detail, BranchChangeUploadError.BC_09);
			return;
		}

		if (branchChangeUploadDAO.getReceiptQueueList(finID)) {
			setError(detail, BranchChangeUploadError.BC_10);
			return;
		}

		if (!branchDAO.isActiveBranch(branchcode)) {
			setError(detail, BranchChangeUploadError.BC_05);
			return;
		}
		FinanceMain fm = financeMainDAO.getFinBasicDetails(finID, "");

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
		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, true);

		String finReference = fm.getFinReference();
		List<Accounts> accountsList = branchMigrationDAO.getAccounts(finReference, oldBranch);

		long linkedTranId = postingsPreparationUtil.getLinkedTranID();

		List<ReturnDataSet> list = new ArrayList<>();

		int transOrder = 1;
		int transOrderID = 10;
		String tranCode = "";
		String revTranCode = "";
		String drOrCr = "";
		for (Accounts account : accountsList) {
			ReturnDataSet oldRds = new ReturnDataSet();

			transOrderID = transOrderID + 10;

			oldRds.setEntityCode(account.getEntityCode());
			oldRds.setAccount(account.getAcNumber());
			oldRds.setAccountType(account.getAcType());
			oldRds.setTranDesc(account.getAcTypeDesc());
			oldRds.setAcCcy(account.getAcCcy());

			oldRds.setLinkedTranId(linkedTranId);
			oldRds.setFinID(finID);
			oldRds.setFinReference(finReference);
			oldRds.setFinEvent(AccountingEvent.BRNCHG);
			oldRds.setUserBranch(AccountingEvent.BRNCHG);
			oldRds.setPostDate(appDate);
			oldRds.setValueDate(appDate);
			oldRds.setAmountType("D");
			oldRds.setPostStatus("S");
			oldRds.setPostToSys("E");
			oldRds.setDerivedTranOrder(0);
			oldRds.setExchangeRate(BigDecimal.ZERO);
			oldRds.setShadowPosting(false);

			BigDecimal acBalance = account.getAcBalance();

			if (acBalance.compareTo(BigDecimal.ZERO) < 0) {
				drOrCr = "C";
				tranCode = "510";
				revTranCode = "010";
				acBalance = acBalance.negate();
			} else {
				drOrCr = "D";
				tranCode = "010";
				revTranCode = "510";
			}

			oldRds.setPostAmount(acBalance);
			oldRds.setPostAmountLcCcy(acBalance);

			oldRds.setDrOrCr(drOrCr);
			oldRds.setTranCode(tranCode);
			oldRds.setRevTranCode(revTranCode);
			oldRds.setPostBranch(oldBranch);
			oldRds.setPostref(oldBranch + "-" + account.getAcType() + "-" + account.getAcCcy());
			oldRds.setTranOrderId(String.valueOf(transOrderID));
			oldRds.setTransOrder(transOrder++);
			oldRds.setPostingId(
					finReference.concat("/").concat(AccountingEvent.BRNCHG).concat(oldRds.getTranOrderId()));

			list.add(oldRds);

			ReturnDataSet newRds = ObjectUtil.clone(oldRds);
			transOrderID = transOrderID + 10;

			if (drOrCr.equals("C")) {
				drOrCr = "D";
				tranCode = "010";
				revTranCode = "510";
			}

			newRds.setDrOrCr(drOrCr);
			newRds.setTranCode(tranCode);
			newRds.setRevTranCode(revTranCode);
			newRds.setPostBranch(newBranch);
			newRds.setPostref(newBranch + "-" + account.getAcType() + "-" + account.getAcCcy());
			newRds.setTranOrderId(String.valueOf(transOrderID++));
			newRds.setTransOrder(transOrder++);
			newRds.setPostingId(
					finReference.concat("/").concat(AccountingEvent.BRNCHG).concat(newRds.getTranOrderId()));

		}

		postingsPreparationUtil.saveAccountingEOD(list);
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

	protected void setError(BranchChangeUpload detail, BranchChangeUploadError error, String arg) {
		setFailureStatus(detail, error.name(), error.description().concat(arg));
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