package com.pennant.pff.branchchange.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
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

	private List<ReturnDataSet> prepareReturnDataSet(Long finID, String oldBranch, Date appDate) {
		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, true);

		String finReference = fm.getFinReference();
		List<Accounts> accountsList = branchMigrationDAO.getAccounts(finReference, oldBranch);

		List<Accounts> accBalList = new ArrayList<>();

		Set<String> accset = new HashSet<>();

		for (Accounts account : accountsList) {
			String acNumber = account.getAcNumber();
			if (!accset.contains(acNumber)) {
				accset.add(acNumber);
				accBalList.add(account);
			}
		}

		long linkedTranId = postingsPreparationUtil.getLinkedTranID();

		List<ReturnDataSet> list = new ArrayList<>();

		String tranCode = "";
		String revTranCode = "";
		String drOrCr = "";
		for (Accounts account : accBalList) {
			BigDecimal acBalance = account.getAcBalance();

			if (acBalance.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			ReturnDataSet rds = new ReturnDataSet();

			rds.setEntityCode(account.getEntityCode());
			rds.setAccount(account.getAcNumber());
			rds.setAccountType(account.getAcType());
			rds.setTranDesc(account.getAcTypeDesc());
			rds.setAcCcy(account.getAcCcy());

			rds.setLinkedTranId(linkedTranId);
			rds.setFinID(finID);
			rds.setFinReference(finReference);
			rds.setFinEvent(AccountingEvent.BRNCHG);
			rds.setUserBranch(AccountingEvent.BRNCHG);
			rds.setPostDate(appDate);
			rds.setAppDate(appDate);
			rds.setValueDate(appDate);
			rds.setAppValueDate(appDate);
			rds.setAmountType("D");
			rds.setPostStatus("S");
			rds.setPostToSys("E");
			rds.setDerivedTranOrder(0);
			rds.setExchangeRate(BigDecimal.ZERO);
			rds.setShadowPosting(false);

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

			rds.setPostAmount(acBalance);
			rds.setPostAmountLcCcy(acBalance);

			rds.setDrOrCr(drOrCr);
			rds.setTranCode(tranCode);
			rds.setRevTranCode(revTranCode);
			rds.setPostBranch(oldBranch);
			rds.setPostref(oldBranch + "-" + account.getAcType() + "-" + account.getAcCcy());

			list.add(rds);
		}

		return list;
	}

	private void doBranchChange(Long finID, String oldBranch, String newBranch, Date appDate) {
		List<ReturnDataSet> list = prepareReturnDataSet(finID, oldBranch, appDate);

		List<ReturnDataSet> sortedList = list.stream()
				.sorted((rds1, rds2) -> StringUtils.compare(rds2.getDrOrCr(), rds1.getDrOrCr())).toList();

		list.clear();

		int transOrder = 1;
		int transOrderID = 0;

		String tranCode = "";
		String revTranCode = "";
		String drOrCr = "";

		for (ReturnDataSet rds : sortedList) {
			String finReference = rds.getFinReference();

			transOrderID = transOrderID + 10;

			rds.setTranOrderId(String.valueOf(transOrderID));
			rds.setTransOrder(transOrder++);
			rds.setPostingId(
					finReference.concat("/").concat(AccountingEvent.BRNCHG).concat("/").concat(rds.getTranOrderId()));

			if (rds.getDrOrCr().equals("C")) {
				drOrCr = "D";
				tranCode = "010";
				revTranCode = "510";
			} else {
				drOrCr = "C";
				tranCode = "510";
				revTranCode = "010";
			}

			ReturnDataSet newRds = ObjectUtil.clone(rds);
			transOrderID = transOrderID + 10;

			newRds.setDrOrCr(drOrCr);
			newRds.setTranCode(tranCode);
			newRds.setRevTranCode(revTranCode);
			newRds.setPostBranch(newBranch);
			newRds.setPostref(newBranch + "-" + newRds.getAccountType() + "-" + newRds.getAcCcy());
			newRds.setTranOrderId(String.valueOf(transOrderID++));
			newRds.setTransOrder(transOrder++);
			newRds.setPostingId(finReference.concat("/").concat(AccountingEvent.BRNCHG).concat("/")
					.concat(newRds.getTranOrderId()));

			if (rds.getDrOrCr().equals("D")) {
				list.add(rds);
			} else if (newRds.getDrOrCr().equals("D")) {
				list.add(newRds);
			}

			if (rds.getDrOrCr().equals("C")) {
				list.add(rds);
			}
			if (newRds.getDrOrCr().equals("C")) {
				list.add(newRds);
			}

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

}