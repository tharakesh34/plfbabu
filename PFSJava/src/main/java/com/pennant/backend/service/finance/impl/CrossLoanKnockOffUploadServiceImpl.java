package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffUploadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.crossloanknockoff.CrossLoanKnockoffUpload;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.crossloanknockoff.service.error.CrossLoanKnockOffUploadError;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class CrossLoanKnockOffUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffUploadServiceImpl.class);

	private CrossLoanKnockOffUploadDAO crossLoanKnockoffUploadDAO;
	private CrossLoanKnockOffUploadProcessRecord crossLoanKnockOffUploadProcessRecord;
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private CrossLoanKnockOffService crossLoanKnockOffService;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object detail) {

		CrossLoanKnockoffUpload clk = null;

		if (detail instanceof CrossLoanKnockoffUpload) {
			clk = (CrossLoanKnockoffUpload) detail;
		}

		if (clk == null) {
			throw new AppException("Invalid Data transferred...");
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<CrossLoanKnockoffUpload> details = crossLoanKnockoffUploadDAO.loadRecordData(header.getId());
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (CrossLoanKnockoffUpload clk : details) {
					clk.setAppDate(appDate);

					clk.setAllocations(crossLoanKnockoffUploadDAO.getAllocations(clk.getId(), clk.getHeaderId()));

					FinanceMain fromFm = financeMainDAO.getFinanceMainByRef(clk.getFromFinReference(), "", false);
					FinanceMain toFm = financeMainDAO.getFinanceMainByRef(clk.getToFinReference(), "", false);

					if (fromFm == null || toFm == null) {
						setError(clk, CrossLoanKnockOffUploadError.CLKU_018);
					} else {
						clk.setFromFm(fromFm);
						clk.setFromFinID(fromFm.getFinID());
						clk.setToFm(toFm);
						clk.setToFinID(toFm.getFinID());
					}

					clk.setEntityCode(header.getEntityCode());

					if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clk.getExcessType())) {
						clk.setExcessList(finExcessAmountDAO.getExcessAmountsByRefAndType(fromFm.getFinID(),
								clk.getExcessType()));
					} else {
						clk.setManualAdvise(manualAdviseDAO.getManualAdviseById(clk.getAdviseId(), ""));
					}

					if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
						doValidate(header, clk);
						doBasicValidations(clk);
					}

					if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
						doAllocationTypeValidations(clk);
					}

					if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
						doFinanceValidations(clk);
					}

					if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
						doExcessAmountValidations(clk);
					}

					if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
						List<FinReceiptHeader> frh = finReceiptHeaderDAO.getReceiptHeadersByRef(clk.getToFinReference(),
								"_Temp");

						doReceiptValidations(clk, frh);

						if (clk.getErrorCode() != null) {
							clk.setProgress(EodConstants.PROGRESS_FAILED);
						} else {
							clk.setProgress(EodConstants.PROGRESS_SUCCESS);
							clk.setErrorCode("");
							clk.setErrorDesc("");
						}
					}

					clk.setUserDetails(header.getUserDetails());

					if (clk.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						AuditHeader ah = getAuditHeader(getCrossLoanKnockOffBean(clk, fromFm, toFm),
								PennantConstants.TRAN_WF);
						txStatus = transactionManager.getTransaction(txDef);

						AuditHeader cah = crossLoanKnockOffService.doApprove(ah);

						if (cah.getErrorMessage() != null) {
							clk.setProgress(EodConstants.PROGRESS_FAILED);
							clk.setErrorDesc(cah.getErrorMessage().get(0).getMessage().toString());
							clk.setErrorCode(cah.getErrorMessage().get(0).getCode().toString());
						} else {
							clk.setCrossLoanId(((CrossLoanKnockOff) cah.getAuditDetail().getModelData()).getId());
						}

						transactionManager.commit(txStatus);
					}

					if (clk.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					crossLoanKnockoffUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);

					updateHeader(headerList, true);

					logger.info("Cross Loan KnockOff Process is Initiated");

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				logger.info("Processed the File {}", header.getFileName());
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

			crossLoanKnockoffUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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

	private void setError(CrossLoanKnockoffUpload detail, CrossLoanKnockOffUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	private BigDecimal getSumOfAllocations(List<CrossLoanKnockoffUpload> alloc) {
		BigDecimal sum = BigDecimal.ZERO;
		for (CrossLoanKnockoffUpload clk : alloc) {
			sum = sum.add(clk.getAmount());
		}

		return sum;
	}

	private void doBasicValidations(CrossLoanKnockoffUpload clk) {
		if (StringUtils.isBlank(clk.getFromFinReference()) || StringUtils.isBlank(clk.getToFinReference())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_001);
			return;
		}

		if (StringUtils.isBlank(clk.getExcessType())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_002);
			return;
		}

		if (!(RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clk.getExcessType())
				|| RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(clk.getExcessType()))) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_003);
			return;
		}

		if (clk.getExcessAmount() == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_004);
			return;
		}

		if (StringUtils.isBlank(clk.getAllocationType())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_005);
			return;
		}

		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(clk.getExcessType()) && clk.getAdviseId() == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_021);
		}

	}

	private void doAllocationTypeValidations(CrossLoanKnockoffUpload clk) {

		if (!(AllocationType.AUTO.equals(clk.getAllocationType())
				|| AllocationType.MANUAL.equals(clk.getAllocationType()))) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_006);
			return;
		}

		if (AllocationType.AUTO.equals(clk.getAllocationType()) && clk.getAllocations().size() > 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_007);
			return;
		}
	}

	private void doFinanceValidations(CrossLoanKnockoffUpload clk) {
		FinanceMain frmFin = clk.getFromFm();
		FinanceMain toFin = clk.getToFm();

		if (frmFin == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_008);
			return;
		}

		if (toFin == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_009);
			return;
		}

		if (frmFin.getCustID() != toFin.getCustID()) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_010);
			return;
		}

		if (!(toFin.isFinIsActive() || toFin.isWriteoffLoan())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_015);
			return;
		}

		if (frmFin.isWriteoffLoan()) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_016);
			return;
		}
	}

	private void doExcessAmountValidations(CrossLoanKnockoffUpload clk) {
		BigDecimal balanceAmount = BigDecimal.ZERO;
		List<CrossLoanKnockoffUpload> allocations = clk.getAllocations();
		List<FinExcessAmount> excessList = clk.getExcessList();
		ManualAdvise ma = clk.getManualAdvise();

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clk.getExcessType())) {
			if (CollectionUtils.isEmpty(excessList)) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_013);
				return;
			}

			balanceAmount = getExcessAmount(excessList, balanceAmount);
		} else {
			if (ma == null) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_019);
				return;
			}

			if (ma.getAdviseType() != AdviseType.PAYABLE.id()) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
				return;
			}

			balanceAmount = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));
		}

		if (balanceAmount != null && clk.getExcessAmount().compareTo(balanceAmount) > 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_014);
			return;
		}

		if (AllocationType.MANUAL.equals(clk.getAllocationType()) && allocations != null) {
			if (!(clk.getExcessAmount().equals(getSumOfAllocations(allocations)))) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_011);
				return;
			}
		}
	}

	private void doReceiptValidations(CrossLoanKnockoffUpload clk, List<FinReceiptHeader> frh) {
		if (frh != null && frh.size() > 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_012);
			return;
		}

	}

	private AuditHeader getAuditHeader(CrossLoanKnockOff clk, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, clk);
		return new AuditHeader(clk.getCrossLoanTransfer().getFromFinReference(), null, null, null, auditDetail,
				clk.getUserDetails(), null);
	}

	private CrossLoanKnockOff getCrossLoanKnockOffBean(CrossLoanKnockoffUpload clku, FinanceMain frmFm,
			FinanceMain toFm) {

		CrossLoanKnockOff clko = new CrossLoanKnockOff();

		clko.setUserDetails(clku.getUserDetails());
		clko.setCrossLoanTransfer(getCrossLoanTransferBean(clku, frmFm, toFm));
		clko.setPostDate(clku.getAppDate());
		clko.setFinReceiptData(getFinReceiptDataBean(clku, toFm));
		clko.setReceiptDate(clku.getAppDate());
		clko.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		clko.setReceiptAmount(clku.getExcessAmount());
		clko.setToFinReference(clku.getToFinReference());
		clko.setFromFinReference(clku.getFromFinReference());
		clko.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		clko.setRequestSource(RequestSource.UPLOAD.name());
		clko.setNewRecord(true);
		clko.setCrossLoanKnockoffUpload(clku);
		clko.setValueDate(clko.getCrossLoanTransfer().getValueDate());

		return clko;
	}

	private CrossLoanTransfer getCrossLoanTransferBean(CrossLoanKnockoffUpload clku, FinanceMain frmFm,
			FinanceMain toFm) {

		List<FinExcessAmount> excessList = clku.getExcessList();
		CrossLoanTransfer clt = new CrossLoanTransfer();

		ManualAdvise ma = clku.getManualAdvise();

		if (CollectionUtils.isNotEmpty(excessList)) {
			clt.setExcessId(getExcessId(excessList));
			clt.setExcessAmount(getAmount(clku));
			clt.setUtiliseAmount(getUtilizedAmount(clku, excessList, BigDecimal.ZERO));
			clt.setReserveAmount(getReservedAmount(excessList, BigDecimal.ZERO));
			clt.setAvailableAmount(
					getBalanceAmount(clku, BigDecimal.ZERO, getExcessAmount(excessList, BigDecimal.ZERO)));
			clt.setFinExcessAmountList(excessList);
			for (FinExcessAmount fea : excessList) {
				if (fea.getExcessID() == clt.getExcessId()) {
					if (fea.getValueDate() == null) {
						clt.setValueDate(clku.getAppDate());
					} else {
						clt.setValueDate(fea.getValueDate());
					}
				}
			}
		} else {
			clt.setExcessAmount(ma.getAdviseAmount());
			clt.setValueDate(ma.getValueDate());
			clt.setUtiliseAmount(BigDecimal.ZERO);
			clt.setReserveAmount(ma.getReservedAmt());
			clt.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount())));
		}

		clt.setCustId(frmFm.getCustID());
		clt.setCustShrtName(frmFm.getCustShrtName());
		clt.setFromFinID(frmFm.getFinID());
		clt.setToFinID(toFm.getFinID());
		clt.setFromFinReference(frmFm.getFinReference());
		clt.setToFinReference(toFm.getFinReference());
		clt.setTransferAmount(clku.getExcessAmount());
		clt.setFromFinType(frmFm.getFinType());
		clt.setToFinType(toFm.getFinType());
		clt.setReceiptDate(clku.getAppDate());
		clt.setReceiptAmount(clku.getExcessAmount());
		clt.setUserDetails(clku.getUserDetails());
		clt.setExcessType(clku.getExcessType());
		clt.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		return clt;
	}

	private FinReceiptData getFinReceiptDataBean(CrossLoanKnockoffUpload clku, FinanceMain fm) {
		Map<String, BigDecimal> allocationMap = new HashMap<>();
		FinReceiptData frd = new FinReceiptData();
		FinanceDetail fd = new FinanceDetail();

		frd.setFinReference(clku.getToFinReference());
		frd.setFinID(fm.getFinID());
		frd.setTotReceiptAmount(clku.getExcessAmount());
		for (CrossLoanKnockoffUpload clkualloc : clku.getAllocations()) {
			allocationMap.put(clkualloc.getCode(), clkualloc.getAmount());
		}
		frd.setValueDate(clku.getAppDate());
		frd.setReceiptHeader(getReceiptHeaderBean(clku, fm));
		frd.setUserDetails(clku.getUserDetails());
		frd.setExcessType(clku.getExcessType());
		getFinanceDetialBean(clku, fd, fm);
		frd.setFinanceDetail(fd);

		return frd;
	}

	private void getFinanceDetialBean(CrossLoanKnockoffUpload clku, FinanceDetail fd, FinanceMain fm) {
		FinScheduleData schdData = fd.getFinScheduleData();
		ReceiptPurpose rptPurpose = ReceiptPurpose.purpose("SchdlRepayment");

		fd.setUserDetails(clku.getUserDetails());
		fd.setNewRecord(true);
		fm.setAppDate(clku.getAppDate());
		fm.setReceiptPurpose(rptPurpose.code());
		schdData.setFinanceMain(fm);
		schdData.setFeeEvent(AccountingEvent.REPAY);
		fd.setFinScheduleData(schdData);
	}

	private FinReceiptHeader getReceiptHeaderBean(CrossLoanKnockoffUpload clku, FinanceMain fm) {
		FinReceiptHeader frh = new FinReceiptHeader();
		List<FinReceiptDetail> frd = finReceiptDetailDAO.getFinReceiptDetailByReference(fm.getFinReference());

		frh.setReceiptDate(clku.getAppDate());
		frh.setFinID(fm.getFinID());
		frh.setReference(clku.getToFinReference());
		frh.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		frh.setFinType(fm.getFinType());
		frh.setFinTypeDesc(fm.getLovDescFinTypeName());
		frh.setFinBranch(fm.getFinBranch());
		frh.setFinBranchDesc(fm.getLovDescFinBranchName());
		frh.setFinCcy(fm.getFinCcy());
		frh.setCustID(fm.getCustID());
		frh.setCustShrtName(fm.getCustShrtName());
		frh.setAllocationType(clku.getAllocationType());
		frh.setReceiptAmount(clku.getExcessAmount());
		frh.setFinIsActive(fm.isFinIsActive());
		frh.setUserDetails(clku.getUserDetails());
		frh.setReceiptDetails(frd);
		frh.setDedupCheckRequired(false);
		frh.setWorkflowId(1);
		frh.setValueDate(clku.getAppDate());
		frh.setKnockOffType(KnockOffType.CROSS_LOAN.code());

		return frh;
	}

	private BigDecimal getExcessAmount(List<FinExcessAmount> excessList, BigDecimal balanceAmount) {
		for (FinExcessAmount fea : excessList) {
			balanceAmount = balanceAmount.add(fea.getBalanceAmt());
		}

		return balanceAmount;
	}

	private BigDecimal getUtilizedAmount(CrossLoanKnockoffUpload clku, List<FinExcessAmount> excessList,
			BigDecimal utilAmt) {

		for (FinExcessAmount fea : excessList) {
			utilAmt = utilAmt.add(fea.getUtilisedAmt());
		}

		utilAmt = utilAmt.add(clku.getExcessAmount());

		return utilAmt;
	}

	private BigDecimal getBalanceAmount(CrossLoanKnockoffUpload clku, BigDecimal balanceAmt, BigDecimal excessAmount) {
		balanceAmt = excessAmount.subtract(clku.getExcessAmount());

		return balanceAmt;
	}

	private BigDecimal getReservedAmount(List<FinExcessAmount> excessList, BigDecimal reserveAmount) {
		for (FinExcessAmount fea : excessList) {
			reserveAmount = reserveAmount.add(fea.getReservedAmt());
		}

		return reserveAmount;

	}

	private long getExcessId(List<FinExcessAmount> excessList) {
		List<Long> excessId = new ArrayList<>();

		for (FinExcessAmount fea : excessList) {
			if (fea.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
				excessId.add(fea.getExcessID());
			}
		}

		return Collections.max(excessId);
	}

	private BigDecimal getAmount(CrossLoanKnockoffUpload clku) {
		BigDecimal amt = BigDecimal.ZERO;

		for (FinExcessAmount fea : clku.getExcessList()) {
			amt = amt.add(fea.getAmount());
		}
		return amt;
	}

	@Override
	public String getSqlQuery() {
		return crossLoanKnockoffUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setCrossLoanKnockoffUploadDAO(CrossLoanKnockOffUploadDAO crossLoanKnockoffUploadDAO) {
		this.crossLoanKnockoffUploadDAO = crossLoanKnockoffUploadDAO;
	}

	@Override
	public CrossLoanKnockOffUploadProcessRecord getProcessRecord() {
		return crossLoanKnockOffUploadProcessRecord;
	}

	@Autowired
	public void setCrossLoanKnockOffUploadProcessRecord(
			CrossLoanKnockOffUploadProcessRecord crossLoanKnockOffUploadProcessRecord) {
		this.crossLoanKnockOffUploadProcessRecord = crossLoanKnockOffUploadProcessRecord;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
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
	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
