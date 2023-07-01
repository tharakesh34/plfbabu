package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
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
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
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
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

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
	private ReceiptService receiptService;
	private FeeTypeDAO feeTypeDAO;

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
			doBasicValidations(clk, header.getEntityCode());
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
				setFailureStatus(clk);
			} else {
				setSuccesStatus(clk);
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
				header.getUploadDetails().addAll(details);

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

					if (ExcessType.EXCESS.equals(clk.getExcessType())) {
						clk.setExcessList(finExcessAmountDAO.getExcessAmountsByRefAndType(fromFm.getFinID(),
								clk.getExcessType()));
					} else {
						if (ExcessType.PAYABLE.equals(clk.getExcessType())) {
							List<ManualAdvise> mbList = manualAdviseDAO.getManualAdviseByRefAndFeeCode(
									fromFm.getFinID(), AdviseType.PAYABLE.id(), clk.getFeeTypeCode());

							mbList = mbList.stream()
									.filter(ma -> !PennantConstants.MANUALADVISE_CANCEL.equals(ma.getStatus()))
									.collect(Collectors.toList());

							if (CollectionUtils.isEmpty(mbList)) {
								setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
							}

							clk.setAdvises(mbList);

							if (ManualAdviceUtil.getBalanceAmount(mbList).compareTo(clk.getExcessAmount()) < 0) {
								setError(clk, CrossLoanKnockOffUploadError.CLKU_020);
							}
						}
					}

					doValidate(header, clk);
					prepareUserDetails(header, clk);

					if (clk.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						createReceipt(header, clk, fromFm, toFm);
					}
				}

				try {
					crossLoanKnockoffUploadDAO.update(details);

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
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(crossLoanKnockoffUploadDAO.loadRecordData(h1.getId()));
			});

			crossLoanKnockoffUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	private void createReceipt(FileUploadHeader header, CrossLoanKnockoffUpload clk, FinanceMain fromFm,
			FinanceMain toFm) {
		List<AuditHeader> ahList = new ArrayList<>();

		clk.setBalanceAmount(clk.getExcessAmount());

		FinanceType finType = finReceiptHeaderDAO.getRepayHierarchy(toFm);

		if (finType == null) {
			setFailureStatus(clk, "Loan Type is invalid.");
			return;
		}

		clk.setFinType(finType);
		prepareUserDetails(header, clk);
		if (ExcessType.EXCESS.equals(clk.getExcessType())) {
			ahList.addAll(prepareCLOForExcess(header, clk, fromFm, toFm));
		}

		if (ExcessType.PAYABLE.equals(clk.getExcessType())) {
			ahList.addAll(prepareCLOForAdvises(header, clk, fromFm, toFm));
		}

		TransactionStatus txStatus = getTransactionStatus();

		try {
			for (AuditHeader ah : ahList) {
				ah = crossLoanKnockOffService.doApprove(ah);

				if (ah.getErrorMessage() != null) {
					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}

					setFailureStatus(clk, ah.getErrorMessage().get(0));
					return;
				}
			}

			setSuccesStatus(clk);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(clk, e.getMessage());
		}
	}

	private List<AuditHeader> prepareCLOForExcess(FileUploadHeader header, CrossLoanKnockoffUpload clk,
			FinanceMain fromFm, FinanceMain toFm) {
		List<AuditHeader> ahList = new ArrayList<>();

		List<FinExcessAmount> excessList = clk.getExcessList().stream()
				.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
				.collect(Collectors.toList());

		for (FinExcessAmount fea : excessList) {
			Date receiptDt = clk.getAppDate();

			BigDecimal payableAmount = fea.getBalanceAmt();

			if (payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (clk.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (fea.getValueDate() != null) {
				receiptDt = fea.getValueDate();
			}

			receiptDt = receiptService.getExcessBasedValueDate(receiptDt, clk.getFromFinID(), receiptDt, fea,
					FinServiceEvent.SCHDRPY);

			if (payableAmount.compareTo(clk.getBalanceAmount()) >= 0) {
				payableAmount = clk.getBalanceAmount();
				clk.setBalanceAmount(BigDecimal.ZERO);
			} else {
				clk.setBalanceAmount(clk.getBalanceAmount().subtract(payableAmount));
			}

			CrossLoanTransfer clt = prepareTransferBean(clk, fromFm, toFm, receiptDt, payableAmount);
			clt.setExcessId(fea.getExcessID());
			clt.setUtiliseAmount(fea.getUtilisedAmt().subtract(payableAmount));
			clt.setExcessAmount(fea.getAmount());
			clt.setReserveAmount(fea.getReservedAmt());
			clt.setAvailableAmount(fea.getBalanceAmt().subtract(payableAmount));
			clt.setExcessValueDate(fea.getValueDate() != null ? fea.getValueDate() : receiptDt);

			CrossLoanKnockOff clko = prepareKnockOffBean(header, clk, toFm, receiptDt, payableAmount);
			clko.setCrossLoanTransfer(clt);
			clko.setValueDate(clt.getValueDate());
			clko.setExcessValueDate(fea.getValueDate() != null ? fea.getValueDate() : receiptDt);
			clko.getFinServiceInstruction().setAdviseId(fea.getExcessID());

			if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
				ahList.add(getAuditHeader(clko, PennantConstants.TRAN_WF));
			}

		}

		return ahList;
	}

	private List<AuditHeader> prepareCLOForAdvises(FileUploadHeader header, CrossLoanKnockoffUpload clk,
			FinanceMain fromFm, FinanceMain toFm) {
		List<AuditHeader> ahList = new ArrayList<>();

		List<ManualAdvise> advises = clk.getAdvises().stream()
				.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
				.collect(Collectors.toList());

		for (ManualAdvise ma : advises) {
			Date receiptDt = clk.getAppDate();

			if (clk.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			BigDecimal payableAmount = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));

			if (payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (payableAmount.compareTo(clk.getBalanceAmount()) >= 0) {
				payableAmount = clk.getBalanceAmount();
				clk.setBalanceAmount(BigDecimal.ZERO);
			} else {
				clk.setBalanceAmount(clk.getBalanceAmount().subtract(payableAmount));
			}

			CrossLoanTransfer clt = prepareTransferBean(clk, fromFm, toFm, receiptDt, payableAmount);
			clt.setExcessId(ma.getAdviseID());
			clt.setExcessAmount(ma.getAdviseAmount());
			clt.setReserveAmount(ma.getReservedAmt());
			clt.setUtiliseAmount(ma.getPaidAmount().add(ma.getWaivedAmount()));
			clt.setAvailableAmount(ma.getBalanceAmt().subtract(payableAmount));

			CrossLoanKnockOff clko = prepareKnockOffBean(header, clk, toFm, receiptDt, payableAmount);
			clko.setCrossLoanTransfer(clt);
			clko.setValueDate(clt.getValueDate());
			clko.setExcessValueDate(clt.getValueDate());
			clko.getFinServiceInstruction().setAdviseId(ma.getAdviseID());

			if (clk.getProgress() != EodConstants.PROGRESS_FAILED) {
				ahList.add(getAuditHeader(clko, PennantConstants.TRAN_WF));
			}
		}

		return ahList;
	}

	private CrossLoanKnockOff prepareKnockOffBean(FileUploadHeader header, CrossLoanKnockoffUpload clk,
			FinanceMain toFm, Date receiptDt, BigDecimal payableAmount) {
		CrossLoanKnockOff clko = new CrossLoanKnockOff();

		clko.setUserDetails(clk.getUserDetails());
		clko.setPostDate(clk.getAppDate());
		clko.setFinReceiptData(getFinReceiptDataBean(clk, toFm));
		clko.setReceiptDate(receiptDt);
		clko.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		clko.setReceiptAmount(payableAmount);
		clko.setToFinReference(clk.getToFinReference());
		clko.setFromFinReference(clk.getFromFinReference());
		clko.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		clko.setRequestSource(RequestSource.UPLOAD.name());
		clko.setNewRecord(true);
		clko.setCrossLoanKnockoffUpload(clk);

		String receiptMode = ExcessType.EXCESS.equals(clk.getExcessType()) ? ReceiptMode.EXCESS : ReceiptMode.PAYABLE;

		clko.setFinServiceInstruction(getFSI(header, clk, payableAmount, receiptMode, receiptDt));
		return clko;
	}

	private CrossLoanTransfer prepareTransferBean(CrossLoanKnockoffUpload clk, FinanceMain fromFm, FinanceMain toFm,
			Date receiptDt, BigDecimal payableAmount) {
		CrossLoanTransfer clt = new CrossLoanTransfer();

		clt.setValueDate(receiptDt);
		clt.setCustId(fromFm.getCustID());
		clt.setCustShrtName(fromFm.getCustShrtName());
		clt.setFromFinID(fromFm.getFinID());
		clt.setToFinID(toFm.getFinID());
		clt.setFromFinReference(fromFm.getFinReference());
		clt.setToFinReference(toFm.getFinReference());
		clt.setTransferAmount(payableAmount);
		clt.setFromFinType(fromFm.getFinType());
		clt.setToFinType(toFm.getFinType());
		clt.setReceiptDate(clk.getAppDate());
		clt.setReceiptAmount(payableAmount);
		clt.setUserDetails(clk.getUserDetails());
		clt.setExcessType(clk.getExcessType());
		clt.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		return clt;
	}

	private void setError(CrossLoanKnockoffUpload detail, CrossLoanKnockOffUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	private BigDecimal getSumOfAllocations(List<CrossLoanKnockoffUpload> alloc) {
		BigDecimal sum = BigDecimal.ZERO;
		for (CrossLoanKnockoffUpload clk : alloc) {
			sum = sum.add(clk.getAmount());
		}

		return sum;
	}

	private void doBasicValidations(CrossLoanKnockoffUpload clk, String entityCode) {
		if (StringUtils.isBlank(clk.getFromFinReference()) || StringUtils.isBlank(clk.getToFinReference())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_001);
			return;
		}

		FinanceMain toFm = financeMainDAO.getFinanceMain(clk.getToFinReference(), entityCode);

		FinanceMain fromFm = financeMainDAO.getFinanceMain(clk.getFromFinReference(), entityCode);

		if (fromFm == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_023);
			return;
		}

		if (toFm == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_024);
			return;
		}

		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fromFm.getClosingStatus())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_027);
			return;

		}

		clk.setFromFm(fromFm);
		clk.setFromFinID(fromFm.getFinID());
		clk.setToFm(toFm);
		clk.setToFinID(toFm.getFinID());

		if (StringUtils.isBlank(clk.getExcessType())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_002);
			return;
		}

		if (!(ExcessType.EXCESS.equals(clk.getExcessType()) || ExcessType.PAYABLE.equals(clk.getExcessType()))) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_003);
			return;
		}

		if (clk.getExcessAmount() == null) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_004);
			return;
		}

		if (clk.getExcessAmount().compareTo(BigDecimal.ZERO) <= 0) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_028);
			return;
		}

		if (StringUtils.isBlank(clk.getAllocationType())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_005);
			return;
		}

		if (ExcessType.PAYABLE.equals(clk.getExcessType()) && StringUtils.isEmpty(clk.getFeeTypeCode())) {
			setError(clk, CrossLoanKnockOffUploadError.CLKU_021);
			return;
		}

		if (ExcessType.PAYABLE.equals(clk.getExcessType())) {
			Long feeTypeId = feeTypeDAO.getPayableFeeTypeID(clk.getFeeTypeCode());

			if (feeTypeId == null || feeTypeId <= 0) {
				setError(clk, CrossLoanKnockOffUploadError.CLKU_029);
			}
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

		if (toFin.isWriteoffLoan()) {
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
			return;
		}
	}

	private BigDecimal getBalanceAmount(CrossLoanKnockoffUpload clk, List<FinExcessAmount> excessList,
			List<ManualAdvise> maList) {

		if (ExcessType.EXCESS.equals(clk.getExcessType())) {
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
			return;
		}
	}

	private AuditHeader getAuditHeader(CrossLoanKnockOff clk, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, clk);
		return new AuditHeader(clk.getCrossLoanTransfer().getFromFinReference(), null, null, null, auditDetail,
				clk.getUserDetails(), null);
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

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.CROSS_LOAN_KNOCKOFF.name(), crossLoanKnockOffUploadProcessRecord, this,
				"CrossLoanKnockOffUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return crossLoanKnockoffUploadDAO.getSqlQuery();
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

	private FinServiceInstruction getFSI(FileUploadHeader header, CrossLoanKnockoffUpload fc, BigDecimal receiptAmount,
			String receiptMode, Date valueDate) {
		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		rud.setReference(fc.getReference());
		rud.setFinID(fc.getReferenceID());
		rud.setAllocationType(fc.getAllocationType());
		rud.setReceiptAmount(receiptAmount);
		rud.setValueDate(valueDate);
		rud.setRealizationDate(valueDate);
		rud.setReceivedDate(valueDate);
		rud.setExcessAdjustTo(ExcessType.EXCESS);
		rud.setReceiptMode(receiptMode);
		rud.setReceiptPurpose("SP");
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);
		rud.setLoggedInUser(fc.getUserDetails());

		FinReceiptData frd = new FinReceiptData();
		frd.setPresentment(false);
		FinReceiptHeader frh = new FinReceiptHeader();
		frh.setValueDate(valueDate);
		frd.setReceiptHeader(frh);

		String repayHierarchy = "";

		try {
			repayHierarchy = FinanceUtil.getRepayHierarchy(frd, fc.getToFm(), fc.getFinType());
		} catch (AppException e) {
			setFailureStatus(fc, e.getMessage());
			return new FinServiceInstruction();
		}

		rud.setListAllocationDetails(prepareAllocations(fc, receiptAmount, repayHierarchy, valueDate));

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, header.getEntityCode());

		List<ErrorDetail> errorDetails = rud.getErrorDetails();

		if (!errorDetails.isEmpty()) {
			setFailureStatus(fc, errorDetails.get(0));
			return new FinServiceInstruction();
		}

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setKnockOffReceipt(true);
		fsi.setUploadAllocationDetails(rud.getListAllocationDetails());
		fsi.setKnockoffType(KnockOffType.CROSS_LOAN.code());

		return fsi;
	}

	private List<UploadAlloctionDetail> prepareAllocations(CrossLoanKnockoffUpload fc, BigDecimal receiptAmount,
			String repayHierarchy, Date valueDate) {

		CrossLoanKnockoffUpload emiAlloc = null;
		boolean isEmi = false;
		BigDecimal emiAmt = BigDecimal.ZERO;
		List<UploadAlloctionDetail> list = new ArrayList<>();

		for (CrossLoanKnockoffUpload alloc : fc.getAllocations()) {
			if (alloc.getCode().equals(Allocation.EMI)) {
				emiAmt = alloc.getAmount();
				emiAlloc = alloc;
				isEmi = true;
				break;
			}
		}

		BigDecimal[] emiSplit = new BigDecimal[3];

		if (isEmi) {
			try {
				emiSplit = receiptService.getEmiSplitForManualAlloc(fc.getToFm(), valueDate, emiAmt);
			} catch (AppException e) {
				setFailureStatus(emiAlloc, "EMI_999", e.getMessage());
				return new ArrayList<>();
			}
		}

		String[] relHierarchy = repayHierarchy.split("\\|");

		for (String rh : relHierarchy) {
			String[] hier = rh.split(",");
			for (String hi : hier) {

				if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}

				for (CrossLoanKnockoffUpload alloc : fc.getAllocations()) {
					if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}

					String allocType = Allocation.getCode(alloc.getCode());
					if (hi.equals(allocType)) {
						if ((Allocation.getCode(Allocation.PRI).equals(hi)
								|| Allocation.getCode(Allocation.PFT).equals(hi)) && isEmi) {
							continue;
						}

						UploadAlloctionDetail uad = new UploadAlloctionDetail();
						uad.setAllocationType(allocType);
						uad.setReferenceCode(alloc.getCode());

						BigDecimal paidAmount = receiptAmount;
						if (receiptAmount.compareTo(alloc.getBalanceAmount()) > 0) {
							paidAmount = alloc.getBalanceAmount();
						}

						receiptAmount = receiptAmount.subtract(paidAmount);
						alloc.setBalanceAmount(alloc.getBalanceAmount().subtract(paidAmount));

						uad.setStrPaidAmount(String.valueOf(paidAmount));
						uad.setPaidAmount(paidAmount);

						list.add(uad);
						continue;
					} else if (Allocation.getCode(Allocation.EMI).equals(allocType)) {
						UploadAlloctionDetail uad = new UploadAlloctionDetail();

						BigDecimal amount = BigDecimal.ZERO;
						if (Allocation.getCode(Allocation.PRI).equals(hi)) {
							uad.setAllocationType(hi);
							uad.setReferenceCode(Allocation.PRI);
							amount = emiSplit[0];
						} else if (Allocation.getCode(Allocation.PFT).equals(hi)) {
							uad.setAllocationType(hi);
							uad.setReferenceCode(Allocation.PFT);
							amount = emiSplit[1];
						} else {
							continue;
						}

						BigDecimal paidAmount = receiptAmount;
						if (receiptAmount.compareTo(amount) > 0) {
							paidAmount = amount;
						}

						receiptAmount = receiptAmount.subtract(amount);
						alloc.setBalanceAmount(alloc.getBalanceAmount().subtract(amount));

						uad.setStrPaidAmount(String.valueOf(paidAmount));
						uad.setPaidAmount(paidAmount);

						list.add(uad);
					}
				}
			}
		}

		return list;
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

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		// Implemented for process record
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}
}