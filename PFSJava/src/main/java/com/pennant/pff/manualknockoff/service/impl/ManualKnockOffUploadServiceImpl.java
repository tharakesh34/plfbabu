package com.pennant.pff.manualknockoff.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennant.pff.manualknockoff.dao.ManualKnockOffUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.knockoff.ManualKnockOffUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.receipt.upload.ReceiptDataValidator;

public class ManualKnockOffUploadServiceImpl extends AUploadServiceImpl<ManualKnockOffUpload> {
	private static final Logger logger = LogManager.getLogger(ManualKnockOffUploadServiceImpl.class);

	private ManualKnockOffUploadDAO manualKnockOffUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private ManualKnockOffUploadProcessRecord manualKnockOffUploadProcessRecord;
	private ReceiptDataValidator receiptDataValidator;
	private ReceiptService receiptService;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	public ManualKnockOffUploadServiceImpl() {
		super();
	}

	@Override
	protected ManualKnockOffUpload getDetail(Object object) {
		if (object instanceof ManualKnockOffUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		ManualKnockOffUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, ManualKnockOffUploadError.MKOU_101);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, ManualKnockOffUploadError.MKOU_102);
			return;
		}

		detail.setReferenceID(fm.getFinID());
		detail.setFinanceMain(fm);
		if (detail.getAllocationType() == null) {
			setError(detail, ManualKnockOffUploadError.MKOU_104);
			return;
		}

		if (finReceiptHeaderDAO.isReceiptExists(reference, "_Temp")) {
			setError(detail, ManualKnockOffUploadError.MKOU_1015);
			return;
		}

		if (manualKnockOffUploadDAO.isInProgress(header.getId(), reference)) {
			setError(detail, ManualKnockOffUploadError.MKOU_1016);
			return;
		}

		String allocationType = StringUtils.upperCase(detail.getAllocationType());

		if ("MANUAL".equals(allocationType)) {
			allocationType = AllocationType.MANUAL;
		} else if ("AUTO".equals(allocationType)) {
			allocationType = AllocationType.AUTO;
		}

		detail.setAllocationType(allocationType);

		if (!AllocationType.MANUAL.equals(allocationType) && !AllocationType.AUTO.equals(allocationType)) {
			setError(detail, ManualKnockOffUploadError.MKOU_105);
			return;
		}

		List<ManualKnockOffUpload> allocations = detail.getAllocations();
		if (AllocationType.MANUAL.equals(allocationType) && allocations.isEmpty()) {
			setError(detail, ManualKnockOffUploadError.MKOU_106);
			return;
		}

		if (AllocationType.AUTO.equals(allocationType) && !allocations.isEmpty()) {
			setError(detail, ManualKnockOffUploadError.MKOU_107);
			return;
		}

		if (AllocationType.MANUAL.equals(allocationType) && !isValidAlloc(detail.getAllocations())) {
			setError(detail, ManualKnockOffUploadError.MKOU_1014);
			return;
		}

		String excessType = detail.getExcessType();

		if ("P".equals(excessType) && StringUtils.isEmpty(detail.getFeeTypeCode())) {
			setError(detail, ManualKnockOffUploadError.MKOU_1013);
			return;
		}

		if ((!"E".equals(excessType) && !"A".equals(excessType)) && StringUtils.isEmpty(detail.getFeeTypeCode())) {
			setError(detail, ManualKnockOffUploadError.MKOU_108);
			return;
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		if ("E".equals(excessType) || "A".equals(excessType)) {
			List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRefAndType(fm.getFinID(),
					excessType);

			if (CollectionUtils.isEmpty(excessList)) {
				setError(detail, ManualKnockOffUploadError.MKOU_109);
				return;
			}

			detail.setExcessList(excessList);

			for (FinExcessAmount fea : excessList) {
				balanceAmount = balanceAmount.add(fea.getBalanceAmt());
			}
		} else {
			List<ManualAdvise> maList = manualAdviseDAO.getManualAdviseByRefAndFeeCode(fm.getFinID(),
					AdviseType.PAYABLE.id(), detail.getFeeTypeCode());

			maList = maList.stream().filter(ma -> !PennantConstants.MANUALADVISE_CANCEL.equals(ma.getStatus()))
					.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(maList)) {
				setError(detail, ManualKnockOffUploadError.MKOU_109);
				return;
			}

			detail.setAdvises(maList);

			balanceAmount = ManualAdviceUtil.getBalanceAmount(maList);
		}

		if (balanceAmount.compareTo(detail.getReceiptAmount()) < 0) {
			setError(detail, ManualKnockOffUploadError.MKOU_1010);
			return;
		}

		BigDecimal alcamount = BigDecimal.ZERO;

		for (ManualKnockOffUpload alloc : allocations) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();

			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(allocationType);
			uad.setReferenceCode(alloc.getCode());
			uad.setStrPaidAmount(String.valueOf(PennantApplicationUtil.formateAmount(alloc.getAmount(), 2)));

			receiptDataValidator.validateAllocations(uad);

			alcamount = alcamount.add(uad.getPaidAmount());

			if (!uad.getErrorDetails().isEmpty()) {
				setFailureStatus(detail, uad.getErrorDetails().get(0));
				return;
			}
		}

		if (alcamount.compareTo(detail.getReceiptAmount()) > 0) {
			setError(detail, ManualKnockOffUploadError.MKOU_1017);
			return;
		}

		setSuccesStatus(detail);
	}

	private void setError(ManualKnockOffUpload detail, ManualKnockOffUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<ManualKnockOffUpload> details = manualKnockOffUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				for (ManualKnockOffUpload fc : details) {
					fc.setAppDate(appDate);
					fc.setAllocations(manualKnockOffUploadDAO.getAllocations(fc.getId(), header.getId()));
					doValidate(header, fc);
					prepareUserDetails(header, fc);

					if (fc.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						createReceipt(fc, header);
					}
				}

				try {
					manualKnockOffUploadDAO.update(details);

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

	private void createReceipt(ManualKnockOffUpload fc, FileUploadHeader header) {
		fc.setBalanceAmount(fc.getReceiptAmount());

		FinanceType finType = finReceiptHeaderDAO.getRepayHierarchy(fc.getFinanceMain());

		if (finType == null) {
			setFailureStatus(fc, "Loan Type is invalid.");
			return;
		}

		fc.setFinType(finType);
		prepareUserDetails(header, fc);

		List<FinServiceInstruction> fsiList = new ArrayList<>();
		if (ExcessType.EXCESS.equals(fc.getExcessType())) {
			fsiList.addAll(prepareRCDForExcess(header, fc));
		}

		if (ExcessType.PAYABLE.equals(fc.getExcessType())) {
			fsiList.addAll(prepareRCDForPayable(header, fc));
		}

		TransactionStatus txStatus = getTransactionStatus();

		try {
			for (FinServiceInstruction fsi : fsiList) {
				FinanceDetail fd = receiptService.receiptTransaction(fsi);

				FinScheduleData schd = fd.getFinScheduleData();
				if (!schd.getErrorDetails().isEmpty()) {
					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}

					setFailureStatus(fc, schd.getErrorDetails().get(0));
					return;
				}
			}

			setSuccesStatus(fc);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(fc, e.getMessage());
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(manualKnockOffUploadDAO.getDetails(h1.getId()));
			});

			manualKnockOffUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public void uploadProcess() {
		uploadProcess(UploadTypes.MANUAL_KNOCKOFF.name(), manualKnockOffUploadProcessRecord, this, "ManualKnockOff");
	}

	@Override
	public String getSqlQuery() {
		return manualKnockOffUploadDAO.getSqlQuery();
	}

	@Override
	public ProcessRecord getProcessRecord() {
		return manualKnockOffUploadProcessRecord;
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return manualKnockOffUploadDAO.isInProgress(headerID, (String) args[0]);
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		// Implemented for process record.
	}

	private BigDecimal getEmiAmount(List<ManualKnockOffUpload> allocations) {
		BigDecimal emiAmount = BigDecimal.ZERO;
		for (ManualKnockOffUpload detail : allocations) {
			if (Allocation.EMI.equals(detail.getCode())) {
				emiAmount = emiAmount.add(detail.getAmount());
			}
		}

		return emiAmount;
	}

	private boolean isValidAlloc(List<ManualKnockOffUpload> allocations) {
		BigDecimal emiAmount = getEmiAmount(allocations);

		if (emiAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return true;
		}

		for (ManualKnockOffUpload detail : allocations) {
			String alloc = detail.getCode();
			if (detail.getAmount().compareTo(BigDecimal.ZERO) > 0
					&& (Allocation.PRI.equals(alloc) || Allocation.PFT.equals(alloc))) {
				return false;
			}
		}

		return true;
	}

	private List<FinServiceInstruction> prepareRCDForExcess(FileUploadHeader header, ManualKnockOffUpload fc) {
		List<FinServiceInstruction> fsiList = new ArrayList<>();

		List<FinExcessAmount> excessList = fc.getExcessList().stream()
				.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
				.collect(Collectors.toList());

		for (FinExcessAmount fea : excessList) {
			BigDecimal payableAmount = fea.getBalanceAmt();

			if (fc.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (payableAmount.compareTo(fc.getBalanceAmount()) >= 0) {
				payableAmount = fc.getBalanceAmount();
				fc.setBalanceAmount(BigDecimal.ZERO);
			} else {
				fc.setBalanceAmount(fc.getBalanceAmount().subtract(payableAmount));
			}

			Date valueDate = receiptService.getExcessBasedValueDate(fc.getAppDate(), fea.getFinID(), fc.getAppDate(),
					fea, FinServiceEvent.SCHDRPY);
			FinServiceInstruction fsi = getFSI(header, fc, payableAmount, ReceiptMode.EXCESS, valueDate);
			fsi.setAdviseId(fea.getExcessID());
			fsi.getReceiptDetail().setPayAgainstID(fea.getExcessID());
			fsiList.add(fsi);
		}

		return fsiList;
	}

	private List<FinServiceInstruction> prepareRCDForPayable(FileUploadHeader header, ManualKnockOffUpload fc) {
		List<FinServiceInstruction> fsiList = new ArrayList<>();

		List<ManualAdvise> advises = fc.getAdvises().stream()
				.sorted((l1, l2) -> DateUtil.compare(l1.getValueDate(), l2.getValueDate()))
				.collect(Collectors.toList());

		for (ManualAdvise ma : advises) {
			BigDecimal payableAmount = ma.getAdviseAmount().subtract(ma.getPaidAmount().add(ma.getWaivedAmount()));

			if (payableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (fc.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			if (payableAmount.compareTo(fc.getBalanceAmount()) >= 0) {
				payableAmount = fc.getBalanceAmount();
				fc.setBalanceAmount(BigDecimal.ZERO);
			} else {
				fc.setBalanceAmount(fc.getBalanceAmount().subtract(payableAmount));
			}

			FinServiceInstruction fsi = getFSI(header, fc, payableAmount, ReceiptMode.PAYABLE, ma.getValueDate());
			fsi.setAdviseId(ma.getAdviseID());
			fsi.getReceiptDetail().setPayAgainstID(ma.getAdviseID());
			fsiList.add(fsi);
		}

		return fsiList;
	}

	private FinServiceInstruction getFSI(FileUploadHeader header, ManualKnockOffUpload fc, BigDecimal receiptAmount,
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
			repayHierarchy = FinanceUtil.getRepayHierarchy(frd, fc.getFinanceMain(), fc.getFinType());
		} catch (AppException e) {
			setFailureStatus(fc, e.getMessage());
			return new FinServiceInstruction();
		}

		rud.setListAllocationDetails(prepareAllocations(fc, receiptAmount, repayHierarchy, valueDate));

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, header.getEntityCode());
		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setKnockOffReceipt(true);
		fsi.setUploadAllocationDetails(rud.getListAllocationDetails());
		fsi.setKnockoffType(KnockOffType.MANUAL.code());

		return fsi;
	}

	private List<UploadAlloctionDetail> prepareAllocations(ManualKnockOffUpload fc, BigDecimal receiptAmount,
			String repayHierarchy, Date valueDate) {

		ManualKnockOffUpload emiAlloc = null;
		boolean isEmi = false;
		BigDecimal emiAmt = BigDecimal.ZERO;
		List<UploadAlloctionDetail> list = new ArrayList<>();
		for (ManualKnockOffUpload alloc : fc.getAllocations()) {
			if (alloc.getCode().equals(Allocation.EMI)) {
				emiAmt = alloc.getAmount();
				emiAlloc = alloc;
				isEmi = true;
			}
		}

		BigDecimal[] emiSplit = new BigDecimal[3];

		if (isEmi) {
			try {
				emiSplit = receiptService.getEmiSplitForManualAlloc(fc.getFinanceMain(), valueDate, emiAmt);
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

				for (ManualKnockOffUpload alloc : fc.getAllocations()) {
					if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}

					String allocType = Allocation.getCode(alloc.getCode());
					if (hi.equals(allocType)) {
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
	public void setManualKnockOffUploadDAO(ManualKnockOffUploadDAO manualKnockOffUploadDAO) {
		this.manualKnockOffUploadDAO = manualKnockOffUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setManualKnockOffUploadProcessRecord(
			ManualKnockOffUploadProcessRecord manualKnockOffUploadProcessRecord) {
		this.manualKnockOffUploadProcessRecord = manualKnockOffUploadProcessRecord;
	}

	@Autowired
	public void setReceiptDataValidator(ReceiptDataValidator receiptDataValidator) {
		this.receiptDataValidator = receiptDataValidator;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}
}