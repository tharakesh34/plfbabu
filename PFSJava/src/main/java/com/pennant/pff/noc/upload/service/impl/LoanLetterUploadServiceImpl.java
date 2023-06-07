package com.pennant.pff.noc.upload.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.upload.dao.LoanLetterUploadDAO;
import com.pennant.pff.noc.upload.error.LoanLetterUploadError;
import com.pennant.pff.noc.upload.model.LoanLetterUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.LoanCancelationUtil;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class LoanLetterUploadServiceImpl extends AUploadServiceImpl<LoanLetterUpload> {
	private static final Logger logger = LogManager.getLogger(LoanLetterUploadServiceImpl.class);

	private LoanLetterUploadDAO loanLetterUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private CustomerDetailsService customerDetailsService;
	private FeeCalculator feeCalculator;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeprofitDetailsDAO;
	private AutoLetterGenerationDAO autoLetterGenerationDAO;
	private GenerateLetterDAO generateLetterDAO;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;

	public LoanLetterUploadServiceImpl() {
		super();
	}

	protected LoanLetterUpload getDetail(Object object) {
		if (object instanceof LoanLetterUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LoanLetterUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(reference, "", false);

		if (fm == null) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_01);
			return;
		}

		detail.setReferenceID(fm.getFinID());
		List<LoanTypeLetterMapping> ltrmap = loanTypeLetterMappingDAO.getLetterMapping(fm.getFinType());

		LetterType letterType = LetterType.getType(detail.getLetterType());
		if ((LetterType.NOC != letterType) && (LetterType.CLOSURE != letterType)
				&& (LetterType.CANCELLATION != letterType)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_02);
			return;
		}

		if (!ltrmap.stream().anyMatch(l -> l.getLetterType().equals(detail.getLetterType()))) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_08);
			return;
		}

		if (StringUtils.isBlank(detail.getModeOfTransfer())) {
			LoanTypeLetterMapping ltlm = ltrmap.stream().filter(l -> l.getLetterType().equals(detail.getLetterType()))
					.findFirst().orElse(null);
			if (ltlm != null) {
				detail.setModeOfTransfer(ltlm.getLetterMode());
			} else {
				setError(detail, LoanLetterUploadError.LOAN_LTR_14);
				return;
			}

		}

		String mode = detail.getModeOfTransfer();
		if (!NOCConstants.MODE_COURIER.equals(mode) && !NOCConstants.MODE_EMAIL.equals(mode)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_03);
			return;
		}

		String waiverCharges = detail.getWaiverCharges();
		if (!PennantConstants.YES.equals(waiverCharges) && !PennantConstants.NO.equals(waiverCharges)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_04);
			return;
		}

		if (fm.isFinIsActive() && fm.getClosingStatus() == null) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_05);
			return;
		}

		if ((LetterType.CLOSURE == letterType || LetterType.NOC == letterType)
				&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_06);
			return;
		}

		if (LetterType.CANCELLATION == letterType) {
			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())
					|| FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
				setError(detail, LoanLetterUploadError.LOAN_LTR_07);
				return;
			}
		}

		FinTypeFees ftf = loanLetterUploadDAO.getFeeWaiverAllowed(fm.getFinType(), detail.getLetterType());

		if (PennantConstants.YES.equals(waiverCharges) && ftf == null
				|| PennantConstants.YES.equals(waiverCharges) && BigDecimal.ZERO == ftf.getMaxWaiverPerc()) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_09);
			return;
		}

		String cancelType = loanLetterUploadDAO.getCanceltype(reference);
		if (LoanCancelationUtil.LOAN_CANCEL_REBOOK.equals(cancelType)) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_10);
			return;
		}

		if (loanLetterUploadDAO.getByReference(detail.getReferenceID(), detail.getLetterType())) {
			setError(detail, LoanLetterUploadError.LOAN_LTR_11);
			return;
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<LoanLetterUpload> details = loanLetterUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				header.setAppDate(appDate);

				for (LoanLetterUpload detail : details) {
					doValidate(header, detail);

					FinanceMain fm = financeMainDAO.getFinanceMainByRef(detail.getReference(), "_View", false);

					GenerateLetter gl = new GenerateLetter();
					long finID = fm.getFinID();
					List<GenerateLetter> letterInfo = generateLetterDAO.getLoanLetterInfo(fm.getFinID(),
							detail.getLetterType());

					if (CollectionUtils.isNotEmpty(letterInfo)) {
						long custID = fm.getCustID();
						List<FinTypeFees> list = finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(),
								NOCConstants.getLetterType(detail.getLetterType()), "_AView", false,
								FinanceConstants.MODULEID_FINTYPE);

						FinanceDetail fd = new FinanceDetail();
						FinScheduleData fsd = new FinScheduleData();
						fsd.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView"));
						fsd.setFinanceScheduleDetails(
								financeScheduleDetailDAO.getFinScheduleDetails(finID, "_AView", false));
						fsd.setFinanceMain(fm);
						fsd.setFinPftDeatil(financeprofitDetailsDAO.getFinProfitDetailsById(finID));

						if (custID > 0) {
							fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
						}

						fd.setFinTypeFeesList(list);
						fd.setFinScheduleData(fsd);

						gl.setFinReference(detail.getReference());
						gl.setFinID(detail.getReferenceID());
						gl.setLetterType(detail.getLetterType());
						gl.setFinanceDetail(fd);

						setMapDetails(gl, letterInfo);

						FinReceiptHeader frh = new FinReceiptHeader();
						FinReceiptData rd = new FinReceiptData();

						rd.setTdPriBal(fd.getFinScheduleData().getFinPftDeatil().getTdSchdPriBal());
						rd.setReceiptHeader(frh);
						frh.setPartPayAmount(BigDecimal.ZERO);
						rd.setFinanceDetail(fd);

						rd = feeCalculator.calculateFees(rd);
						List<FinFeeDetail> ffd = rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
						if (CollectionUtils.isNotEmpty(ffd)) {
							for (FinFeeDetail fee : ffd) {
								if (PennantConstants.YES.equals(detail.getWaiverCharges())) {
									fee.setRemainingFee(BigDecimal.ZERO);
									fee.setRemainingFeeGST(BigDecimal.ZERO);
									fee.setRemainingFeeGST(BigDecimal.ZERO);
									fee.setWaivedAmount(fee.getActualAmount());
								}
								fee.setFeeID(finFeeDetailDAO.save(fee, false, ""));
								gl.setFeeID(fee.getFeeID());
							}
						}
					}

					gl.setFinID(finID);
					gl.setRequestType("M");
					gl.setCreatedDate(appDate);
					gl.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					gl.setModeofTransfer(detail.getModeOfTransfer());
					gl.setLetterType(detail.getLetterType());

					List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO
							.getLetterMapping(fm.getFinType());

					for (LoanTypeLetterMapping ltlp : letterMapping) {
						if (ltlp.getLetterType().equals(gl.getLetterType())) {
							gl.setAgreementTemplate(ltlp.getAgreementCodeId());
							gl.setEmailTemplate(ltlp.getEmailTemplateId());
						}
					}

					autoLetterGenerationDAO.save(gl);

					if (detail.getErrorCode() != null) {
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);
					}

					detail.setUserDetails(header.getUserDetails());
				}

				try {
					loanLetterUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();
	}

	private void setMapDetails(GenerateLetter gl, List<GenerateLetter> letterInfo) {
		Date appDate = SysParamUtil.getAppDate();
		FinanceDetail fd = gl.getFinanceDetail();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		LoanLetter letter = new LoanLetter();
		Customer customer = fd.getCustomerDetails().getCustomer();

		letter.setClosureType(fm.getClosureType());
		letter.setCustCtgCode(customer.getCustCtgCode());
		letter.setCustGenderCode(customer.getCustGenderCode());
		letter.setCustomerType(customer.getCustTypeCode());
		letter.setLoanClosureAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));
		letter.setLoanCancellationAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));

		if (CollectionUtils.isNotEmpty(letterInfo)) {
			letter.setSequenceNo(letterInfo.size());
			letter.setStatusOfpreviousletters(letterInfo.get(0).getStatus());
		}

		fm.setLoanLetter(letter);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(loanLetterUploadDAO.getDetails(h1.getId()));
			});

			loanLetterUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	private void setError(LoanLetterUpload detail, LoanLetterUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public String getSqlQuery() {
		return loanLetterUploadDAO.getSqlQuery();
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLoanLetterUploadDAO(LoanLetterUploadDAO loanLetterUploadDAO) {
		this.loanLetterUploadDAO = loanLetterUploadDAO;
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.LOAN_LETTER.name(), this, "LoanLetterUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		String finReference = ObjectUtil.valueAsString(paramSource.getValue("finReference"));

		LoanLetterUpload transfer = (LoanLetterUpload) ObjectUtil.valueAsObject(paramSource, LoanLetterUpload.class);

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		transfer.setHeaderId(header.getId());
		transfer.setAppDate(header.getAppDate());
		transfer.setReference(finReference);

		if (isInProgress(headerID, finReference)) {
			setFailureStatus(transfer, "Record is already initiated, unable to proceed.");
			updateProcess(header, transfer, paramSource);
			return;
		}

		doValidate(header, transfer);

		updateProcess(header, transfer, paramSource);

		header.getUploadDetails().add(transfer);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return loanLetterUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setFinanceprofitDetailsDAO(FinanceProfitDetailDAO financeprofitDetailsDAO) {
		this.financeprofitDetailsDAO = financeprofitDetailsDAO;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setAutoLetterGenerationDAO(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}

	@Autowired
	public void setGenerateLetterDAO(GenerateLetterDAO generateLetterDAO) {
		this.generateLetterDAO = generateLetterDAO;
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}
}