package com.pennant.pff.receipt.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinFeeConfigDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.loanclosure.LoanClosureUpload;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.impl.ManualAdviceUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.core.loan.util.LoanClosureCalculator;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.pff.receipt.dao.LoanClosureUploadDAO;
import com.pennant.pff.receipt.error.LoanClosureUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class LoanClosureUploadServiceImpl extends AUploadServiceImpl<LoanClosureUpload> {
	private static final Logger logger = LogManager.getLogger(LoanClosureUploadServiceImpl.class);

	private LoanClosureUploadDAO loanClosureUploadDAO;
	private ProcessRecord loanClosureUploadProcessRecord;
	private FinanceMainDAO financeMainDAO;
	private ReceiptService receiptService;
	protected FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CustomerDAO customerDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinFeeConfigDAO finFeeConfigDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	protected LoanClosureUpload getDetail(Object object) {
		if (object instanceof LoanClosureUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LoanClosureUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, LoanClosureUploadError.LCU_01);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, LoanClosureUploadError.LCU_02);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, LoanClosureUploadError.LCU_03);
			return;
		}

		String exisClosureType = finReceiptHeaderDAO.getClosureTypeValue(fm.getFinID());

		if (StringUtils.isNotEmpty(exisClosureType)) {
			setError(detail, LoanClosureUploadError.LCU_06);
			return;
		}

		String closureType = detail.getClosureType();

		if (closureType != null && !(ClosureType.isValid(closureType) || closureType.equalsIgnoreCase("SETTLEMENT"))
				&& StringUtils.isNotBlank(closureType)) {
			setError(detail, LoanClosureUploadError.LCU_04);
			return;
		}

		String reason = detail.getReasonCode();

		if (StringUtils.isNotBlank(reason)) {
			if (!loanClosureUploadDAO.getReason(reason)) {
				setError(detail, LoanClosureUploadError.LCU_05);
				return;
			}
		}

		BigDecimal balanceAmount = BigDecimal.ZERO;

		List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRef(fm.getFinID());
		for (FinExcessAmount fea : excessList) {
			balanceAmount = balanceAmount.add(fea.getBalanceAmt());
		}

		List<ManualAdvise> maList = manualAdviseDAO.getPaybleAdvises(fm.getFinID(), "_AVIEW");
		balanceAmount = balanceAmount.add(ManualAdviceUtil.getBalanceAmount(maList));

		detail.setExcessList(excessList);
		detail.setAdvises(maList);
		detail.setAmount(balanceAmount);
		detail.setReferenceID(fm.getFinID());

		setSuccesStatus(detail);
	}

	protected void setError(LoanClosureUpload detail, LoanClosureUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<LoanClosureUpload> details = loanClosureUploadDAO.loadRecordData(header.getId());
				header.setAppDate(appDate);
				header.getUploadDetails().addAll(details);

				for (LoanClosureUpload lcu : details) {
					lcu.setAppDate(appDate);
					doValidate(header, lcu);

					if (lcu.getProgress() == EodConstants.PROGRESS_SUCCESS) {
						lcu.setAllocations(loanClosureUploadDAO.getAllocations(lcu.getId(), header.getId()));
						createReceipt(lcu, header);
					}
				}

				try {
					loanClosureUploadDAO.update(details);

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

	private void createReceipt(LoanClosureUpload lcu, FileUploadHeader header) {
		String entityCode = header.getEntityCode();

		Date appDate = header.getAppDate();
		ReceiptUploadDetail rud = new ReceiptUploadDetail();
		if (CollectionUtils.isNotEmpty(lcu.getExcessList())) {
			List<FinExcessAmount> excessList = lcu.getExcessList().stream()
					.sorted((l1, l2) -> DateUtil.compare(l2.getValueDate(), l1.getValueDate()))
					.collect(Collectors.toList());
			appDate = receiptService.getExcessBasedValueDate(appDate, lcu.getReferenceID(), appDate, excessList.get(0),
					FinServiceEvent.EARLYSETTLE);
		}
		rud.setReference(lcu.getReference());
		rud.setFinID(lcu.getReferenceID());
		rud.setValueDate(appDate);
		rud.setRealizationDate(appDate);
		rud.setReceivedDate(appDate);
		rud.setDepositDate(appDate);
		rud.setReceiptAmount(lcu.getAmount());
		rud.setExcessAdjustTo(ExcessType.EXCESS);
		rud.setReceiptPurpose(ReceiptPurpose.EARLYSETTLE.code());
		rud.setReceiptMode(PennantConstants.List_Select);
		rud.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rud.setReceiptChannel(PennantConstants.List_Select);

		List<UploadAlloctionDetail> list = new ArrayList<>();

		BigDecimal totalWaivedAmount = BigDecimal.ZERO;

		for (LoanClosureUpload alloc : lcu.getAllocations()) {
			UploadAlloctionDetail uad = new UploadAlloctionDetail();
			uad.setRootId(String.valueOf(alloc.getFeeId()));
			uad.setAllocationType(Allocation.getCode(getAllocationCode(alloc)));
			uad.setReferenceCode(getAllocationCode(alloc));
			uad.setWaivedAmount(alloc.getAmount());

			totalWaivedAmount = totalWaivedAmount.add(uad.getWaivedAmount());
			list.add(uad);
		}

		rud.setListAllocationDetails(list);

		if (!list.isEmpty()) {
			rud.setAllocationType(AllocationType.MANUAL);
		}

		prepareUserDetails(header, lcu);

		BigDecimal receiptAmount = lcu.getAmount().add(totalWaivedAmount);

		validateReceiptAmount(lcu, appDate, receiptAmount);

		if (lcu.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, entityCode);

		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setClosureReceipt(true);
		fsi.setRequestSource(RequestSource.UPLOAD);
		fsi.setClosureType(lcu.getClosureType());
		fsi.setClosureWithFullWaiver(!list.isEmpty() && rud.getReceiptAmount().compareTo(BigDecimal.ZERO) == 0);

		fsi.setLoggedInUser(lcu.getUserDetails());
		fsi.setKnockOffReceipt(true);
		fsi.setReceiptDetail(null);
		fsi.setReceiptDetails(receiptService.prepareRCDForExcess(lcu.getExcessList(), rud));
		fsi.getReceiptDetails().addAll(receiptService.prepareRCDForMA(lcu.getAdvises(), rud));
		FinanceDetail fd = null;

		TransactionStatus txStatus = getTransactionStatus();

		try {
			fd = receiptService.receiptTransaction(fsi);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(lcu, e.getMessage());
			return;
		}

		if (fd == null) {
			setFailureStatus(lcu, "Finance Detail is null.");
			return;
		}

		FinScheduleData schd = fd.getFinScheduleData();
		if (!schd.getErrorDetails().isEmpty()) {
			setFailureStatus(lcu, schd.getErrorDetails().get(0));
		} else {
			lcu.setReceiptID(fd.getReceiptId());
			setSuccesStatus(lcu);
		}
	}

	private void validateReceiptAmount(LoanClosureUpload lcu, Date appDate, BigDecimal receiptAmount) {
		FinReceiptData rd = new FinReceiptData();
		FinanceDetail fd = new FinanceDetail();

		rd.setFinID(lcu.getReferenceID());
		rd.setFinReference(lcu.getReference());
		rd.setUserDetails(lcu.getUserDetails());
		rd.setValueDate(appDate);

		FinReceiptHeader rch = new FinReceiptHeader();
		rch.setReceiptPurpose(ReceiptPurpose.EARLYSETTLE.code());
		rch.setReceivableAdvises(manualAdviseDAO.getReceivableAdvises(lcu.getReferenceID(), "_AVIEW"));
		rch.setValueDate(appDate);
		rch.setPartPayAmount(BigDecimal.ZERO);

		FinScheduleData fsd = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainForLMSEvent(lcu.getReferenceID());
		fm.setClosureType(lcu.getClosureType());
		fm.setPenaltyRates(finODPenaltyRateDAO.getFinODPenaltyRateByRef(fm.getFinID(), ""));
		fsd.setFinanceMain(fm);
		fsd.setFinPftDeatil(financeProfitDetailDAO.getFinProfitDetailsByFinRef(lcu.getReferenceID()));
		fsd.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), TableType.MAIN_TAB.getSuffix()));
		fsd.setFinanceScheduleDetails(financeScheduleDetailDAO.getSchedulesForLMSEvent(lcu.getReferenceID()));
		fsd.setFeeEvent(AccountingEvent.EARLYSTL);

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesForLMSEvent(fm.getFinType(), AccountingEvent.EARLYSTL));
		fd.setFinFeeConfigList(
				finFeeConfigDAO.getFinFeeConfigList(fm.getFinID(), AccountingEvent.EARLYSTL, false, "_View"));

		rd.setTdPriBal(fsd.getFinPftDeatil().getTdSchdPriBal());
		rd.setFinanceDetail(fd);
		rd.setReceiptHeader(rch);

		Customer customer = null;
		if (CustomerExtension.CUST_CORE_BANK_ID) {
			customer = customerDAO.getCustomerEOD(fm.getCustCoreBank());
		} else {
			customer = customerDAO.getCustomerEOD(fm.getCustID());
		}

		CustomerDetails cd = new CustomerDetails();
		cd.setCustomer(customer);
		fd.setCustomerDetails(cd);

		ReceiptDTO receiptDTO = receiptService.prepareReceiptDTO(rd);

		receiptDTO.setCustomer(customer);

		BigDecimal calcClosureAmt = LoanClosureCalculator.computeClosureAmount(receiptDTO, true);

		if (calcClosureAmt.compareTo(receiptAmount) > 0) {
			setFailureStatus(lcu, "", "Receipt amount is in sufficient for closure. Excepted amount is :"
					+ CurrencyUtil.format(calcClosureAmt, 2));
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(loanClosureUploadDAO.loadRecordData(h1.getId()));
			});

			loanClosureUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public boolean isInProgress(Long headerID, Object... args) {
		return loanClosureUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_CLOSURE.name(), loanClosureUploadProcessRecord, this, "LoanClosureUpload");
	}

	@Override
	public String getSqlQuery() {
		return loanClosureUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		//
	}

	private String getAllocationCode(LoanClosureUpload alloc) {
		String code = alloc.getCode();

		code = code.replace("_W", "");
		code = code.replace("_w", "");

		if (code.equalsIgnoreCase("PRINCIPAL")) {
			code = "PRI";
		}

		if (code.equalsIgnoreCase("INTEREST")) {
			code = "PFT";
		}

		if (code.equalsIgnoreCase("FTINTEREST")) {
			code = "FUTPFT";
		}

		if (code.equalsIgnoreCase("FTPRINCIPAL")) {
			code = "FUTPRI";
		}

		if (code.equalsIgnoreCase("LPP")) {
			code = "ODC";
		}

		if (code.equalsIgnoreCase("FC")) {
			code = "FEE";
		}

		return code;
	}

	@Override
	public ProcessRecord getProcessRecord() {
		return loanClosureUploadProcessRecord;
	}

	@Autowired
	public void setLoanClosureUploadProcessRecord(ProcessRecord loanClosureUploadProcessRecord) {
		this.loanClosureUploadProcessRecord = loanClosureUploadProcessRecord;
	}

	@Autowired
	public void setLoanClosureUploadDAO(LoanClosureUploadDAO loanClosureUploadDAO) {
		this.loanClosureUploadDAO = loanClosureUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
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
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	@Autowired
	public void setFinFeeConfigDAO(FinFeeConfigDAO finFeeConfigDAO) {
		this.finFeeConfigDAO = finFeeConfigDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

}
