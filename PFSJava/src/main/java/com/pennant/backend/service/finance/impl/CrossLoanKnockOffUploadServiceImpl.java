package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
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
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;

public class CrossLoanKnockOffUploadServiceImpl extends AUploadServiceImpl<CrossLoanKnockoffUpload> {
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffUploadServiceImpl.class);

	private CrossLoanKnockOffUploadDAO crossLoanKnockoffUploadDAO;
	private CrossLoanKnockOffUploadProcessRecord crossLoanKnockOffUploadProcessRecord;
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private CrossLoanKnockOffService crossLoanKnockOffService;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public CrossLoanKnockOffUploadServiceImpl() {
		super();
	}

	@Override
	protected CrossLoanKnockoffUpload getDetail(Object object) {
		if (object instanceof CrossLoanKnockoffUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object detail) {
		CrossLoanKnockoffUpload clk = getDetail(detail);

		if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
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
			List<FinReceiptHeader> frh = finReceiptHeaderDAO.getReceiptHeadersByRef(clk.getToFinReference(), "_Temp");

			doReceiptValidations(clk, frh);

			if (clk.getErrorCode() != null) {
				clk.setProgress(EodConstants.PROGRESS_FAILED);
			} else {
				clk.setProgress(EodConstants.PROGRESS_SUCCESS);
				clk.setErrorCode("");
				clk.setErrorDesc("");
			}
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

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
						if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(clk.getExcessType())) {
							List<ManualAdvise> mbList = manualAdviseDAO.getManualAdviseByRefAndFeeCode(
									fromFm.getFinID(), AdviseType.PAYABLE.id(), clk.getFeeTypeCode());

							if (CollectionUtils.isEmpty(mbList)) {
								setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
								return;
							}

							clk.setAdvises(mbList);

							if (ManualAdviceUtil.getBalanceAmount(mbList).compareTo(clk.getExcessAmount()) < 0) {
								setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
								return;
							}
						}
					}

					doValidate(header, clk);

					LoggedInUser userDetails = header.getUserDetails();

					if (userDetails == null) {
						userDetails = new LoggedInUser();
						userDetails.setLoginUsrID(header.getApprovedBy());
						userDetails.setUserName(header.getApprovedByName());
					}

					clk.setUserDetails(userDetails);

					if (clk.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						createReceipt(clk, fromFm, toFm);
					}

					header.getUploadDetails().add(clk);

					if (clk.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					crossLoanKnockoffUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

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

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			crossLoanKnockoffUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> {
				h1.setRemarks(ERR_DESC);
				h1.getUploadDetails().addAll(crossLoanKnockoffUploadDAO.loadRecordData(h1.getId()));
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

	private void createReceipt(CrossLoanKnockoffUpload clk, FinanceMain fromFm, FinanceMain toFm) {
		AuditHeader ah = getAuditHeader(getCrossLoanKnockOffBean(clk, fromFm, toFm), PennantConstants.TRAN_WF);

		TransactionStatus txStatus = getTransactionStatus();

		try {
			ah = crossLoanKnockOffService.doApprove(ah);
			transactionManager.commit(txStatus);
		} catch (AppException e) {
			clk.setProgress(EodConstants.PROGRESS_FAILED);
			clk.setErrorCode(ERR_CODE);
			clk.setErrorDesc(e.getMessage());

			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
			return;
		}

		if (ah == null) {
			clk.setProgress(EodConstants.PROGRESS_FAILED);
			clk.setErrorCode(ERR_CODE);
			clk.setErrorCode("Audit Header is null.");
			return;
		}

		if (ah.getErrorMessage() != null) {
			clk.setProgress(EodConstants.PROGRESS_FAILED);
			clk.setErrorDesc(ah.getErrorMessage().get(0).getMessage());
			clk.setErrorCode(ah.getErrorMessage().get(0).getCode());
		} else {
			clk.setCrossLoanId(((CrossLoanKnockOff) ah.getAuditDetail().getModelData()).getId());
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

		if (RepayConstants.EXAMOUNTTYPE_PAYABLE.equals(clk.getExcessType())
				&& StringUtils.isEmpty(clk.getFeeTypeCode())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_021);
		}

	}

	private void doAllocationTypeValidations(CrossLoanKnockoffUpload clk) {

		if (!(AllocationType.AUTO.equals(clk.getAllocationType())
				|| AllocationType.MANUAL.equals(clk.getAllocationType()))) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_006);
			return;
		}

		if (AllocationType.AUTO.equals(clk.getAllocationType()) && !clk.getAllocations().isEmpty()) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_007);
			return;
		}

		if (AllocationType.MANUAL.equals(clk.getAllocationType()) && !isValidAlloc(clk.getAllocations())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_022);
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
		}
	}

	private void doExcessAmountValidations(CrossLoanKnockoffUpload clk) {
		List<CrossLoanKnockoffUpload> allocations = clk.getAllocations();
		List<FinExcessAmount> excessList = clk.getExcessList();
		List<ManualAdvise> ma = clk.getAdvises();

		BigDecimal balanceAmount = getBalanceAmount(clk, excessList, ma);

		if (balanceAmount != null && clk.getExcessAmount().compareTo(balanceAmount) > 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_014);
			return;
		}

		if (balanceAmount != null && clk.getExcessAmount().compareTo(balanceAmount) > 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_014);
			return;
		}

		if (AllocationType.MANUAL.equals(clk.getAllocationType()) && allocations != null
				&& !(clk.getExcessAmount().equals(getSumOfAllocations(allocations)))) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_011);
		}
	}

	private BigDecimal getBalanceAmount(CrossLoanKnockoffUpload clk, List<FinExcessAmount> excessList,
			List<ManualAdvise> maList) {

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clk.getExcessType())) {
			if (CollectionUtils.isEmpty(excessList)) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_013);
				return BigDecimal.ZERO;
			}

			return getExcessAmount(excessList);
		}

		if (maList == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_019);
			return BigDecimal.ZERO;
		}

		for (ManualAdvise maa : maList) {
			if (maa.getAdviseType() != AdviseType.PAYABLE.id()) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
				return BigDecimal.ZERO;
			}
		}

		return ManualAdviceUtil.getBalanceAmount(maList);
	}

	private void doReceiptValidations(CrossLoanKnockoffUpload clk, List<FinReceiptHeader> frh) {
		if (CollectionUtils.isNotEmpty(frh)) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_012);
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
		Date receiptDt = clku.getAppDate();

		clko.setCrossLoanTransfer(getCrossLoanTransferBean(clku, frmFm, toFm));

		if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(clku.getExcessType())) {
			CrossLoanTransfer clt = clko.getCrossLoanTransfer();

			if (clt != null && clt.getValueDate() != null) {
				receiptDt = clt.getValueDate();
				Date schDate = financeScheduleDetailDAO.getSchdDateForKnockOff(toFm.getFinID(), clt.getValueDate());

				if (DateUtil.compare(receiptDt, schDate) < 0) {
					receiptDt = schDate;
				}
			}
		}

		clko.setUserDetails(clku.getUserDetails());
		clko.setPostDate(clku.getAppDate());
		clko.setFinReceiptData(getFinReceiptDataBean(clku, toFm));
		clko.setReceiptDate(receiptDt);
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

		List<ManualAdvise> advises = clku.getAdvises();

		prepareExcess(clku, excessList, clt);
		prepareManualAdvise(clku, clt, advises);

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

	private BigDecimal getExcessAmount(List<FinExcessAmount> excessList) {
		BigDecimal balanceAmount = BigDecimal.ZERO;
		for (FinExcessAmount fea : excessList) {
			balanceAmount = balanceAmount.add(fea.getBalanceAmt());
		}

		return balanceAmount;
	}

	private void prepareExcess(CrossLoanKnockoffUpload clku, List<FinExcessAmount> excessList, CrossLoanTransfer clt) {
		if (CollectionUtils.isEmpty(excessList)) {
			return;
		}

		clt.setExcessId(getExcessId(excessList));
		clt.setUtiliseAmount(getUtilizedAmount(clku, excessList, BigDecimal.ZERO));
		clt.setExcessAmount(getAmount(clku));
		clt.setReserveAmount(getReservedAmount(excessList, BigDecimal.ZERO));
		clt.setAvailableAmount(getBalanceAmount(clku, getExcessAmount(excessList)));
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
	}

	private void prepareManualAdvise(CrossLoanKnockoffUpload clku, CrossLoanTransfer clt, List<ManualAdvise> advises) {
		if (CollectionUtils.isEmpty(advises)) {
			return;
		}

		clt.setExcessId(ManualAdviceUtil.getAdviseId(advises));
		clt.setExcessAmount(getAmountForExcess(clku));
		clt.setReserveAmount(ManualAdviceUtil.getReservedAmount(advises));
		clt.setUtiliseAmount(getAdviseUtilizedAmount(advises, BigDecimal.ZERO));
		clt.setAvailableAmount(ManualAdviceUtil.getBalanceAmount(advises).subtract(clku.getExcessAmount()));
		clt.setManualAdvisesList(advises);

		for (ManualAdvise ma : advises) {
			if (ma.getAdviseID() == clt.getExcessId()) {
				if (ma.getValueDate() == null) {
					clt.setValueDate(clku.getAppDate());
				} else {
					clt.setValueDate(ma.getValueDate());
				}
			}
		}
	}

	private BigDecimal getUtilizedAmount(CrossLoanKnockoffUpload clku, List<FinExcessAmount> excessList,
			BigDecimal utilAmt) {

		for (FinExcessAmount fea : excessList) {
			utilAmt = utilAmt.add(fea.getUtilisedAmt());
		}

		utilAmt = utilAmt.add(clku.getExcessAmount());

		return utilAmt;
	}

	private BigDecimal getBalanceAmount(CrossLoanKnockoffUpload clku, BigDecimal excessAmount) {
		return excessAmount.subtract(clku.getExcessAmount());
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
	public void uploadProcess() {
		uploadProcess(UploadTypes.CROSS_LOAN_KNOCKOFF.name(), crossLoanKnockOffUploadProcessRecord, this,
				"CrossLoanKnockOffUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return crossLoanKnockoffUploadDAO.getSqlQuery();
	}

	private BigDecimal getAmountForExcess(CrossLoanKnockoffUpload clku) {
		BigDecimal amt = BigDecimal.ZERO;

		for (ManualAdvise ma : clku.getAdvises()) {
			amt = amt.add(ma.getAdviseAmount());
		}

		return amt;
	}

	private BigDecimal getAdviseUtilizedAmount(List<ManualAdvise> adviseList, BigDecimal utilAmt) {
		for (ManualAdvise ma : adviseList) {
			utilAmt = utilAmt.add(ma.getPaidAmount());
		}

		return utilAmt;
	}

	private BigDecimal getEmiAmount(List<CrossLoanKnockoffUpload> allocations) {
		BigDecimal emiAmount = BigDecimal.ZERO;
		for (CrossLoanKnockoffUpload detail : allocations) {
			if (Allocation.EMI.equals(detail.getCode())) {
				emiAmount = emiAmount.add(detail.getAmount());
			}
		}

		return emiAmount;
	}

	private boolean isValidAlloc(List<CrossLoanKnockoffUpload> allocations) {
		BigDecimal emiAmount = getEmiAmount(allocations);

		if (emiAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return true;
		}

		for (CrossLoanKnockoffUpload detail : allocations) {
			String alloc = detail.getCode();
			if (detail.getAmount().compareTo(BigDecimal.ZERO) > 0
					&& (Allocation.PRI.equals(alloc) || Allocation.PFT.equals(alloc))) {
				return false;
			}
		}

		return true;
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

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		// Implemented for process record
	}

}