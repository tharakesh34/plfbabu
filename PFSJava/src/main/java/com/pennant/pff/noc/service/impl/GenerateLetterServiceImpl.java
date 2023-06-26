package com.pennant.pff.noc.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.letter.CourierStatus;
import com.pennant.pff.letter.LetterMode;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.letter.service.LetterService;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.pff.noc.upload.dao.LoanLetterUploadDAO;
import com.pennant.pff.receipt.ClosureType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.SchdUtil;

public class GenerateLetterServiceImpl extends GenericFinanceDetailService implements GenerateLetterService {
	private static Logger logger = LogManager.getLogger(GenerateLetterServiceImpl.class);

	public GenerateLetterServiceImpl() {
		super();
	}

	private static final String BLOCKED_MSG = "Letter Generation Blocked";

	private GenerateLetterDAO generateLetterDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	protected FinExcessAmountDAO finExcessAmountDAO;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;
	private LoanLetterUploadDAO loanLetterUploadDAO;
	private AutoLetterGenerationDAO autoLetterGenerationDAO;
	private LetterService letterService;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	@Override
	public List<GenerateLetter> getResult(ISearch searchFilters, List<String> roleCodes) {
		return generateLetterDAO.getResult(searchFilters, roleCodes);
	}

	@Override
	public List<ReportListDetail> getPrintLetters(List<String> workFlowRoles) {
		return generateLetterDAO.getPrintLetters(workFlowRoles);
	}

	@Override
	public GenerateLetter getLetter(long id) {
		return generateLetterDAO.getLetter(id);
	}

	@Override
	public List<GenerateLetter> getGenerateLetters(List<String> getGenerateLetters) {
		return generateLetterDAO.getGenerateLetters(getGenerateLetters);
	}

	@Override
	public boolean isReferenceExist(String reference) {
		return generateLetterDAO.isReferenceExist(reference);
	}

	@Override
	public List<LoanTypeLetterMapping> getFinTypeMap(String finType) {
		return loanTypeLetterMappingDAO.getLetterMapping(finType);
	}

	@Override
	public String getCanceltype(String finReference) {
		return loanLetterUploadDAO.getCanceltype(finReference);
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.info(Literal.ENTERING);

		ah = businessValidation(ah, "saveOrUpdate");

		if (!ah.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return ah;
		}

		GenerateLetter gl = (GenerateLetter) ah.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (gl.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (gl.isNewRecord()) {
			gl.setId(generateLetterDAO.save(gl, tableType));
			ah.getAuditDetail().setModelData(gl);
			ah.setAuditReference(String.valueOf(gl.getId()));
		} else {
			generateLetterDAO.update(gl, tableType);
		}

		auditHeaderDAO.addAudit(ah);
		logger.info(Literal.LEAVING);
		return ah;
	}

	private AuditHeader businessValidation(AuditHeader ah, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail ad = validation(ah.getAuditDetail(), ah.getUsrLanguage());
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());
		ah = nextProcess(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditDetail validation(AuditDetail ah, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		GenerateLetter gl = (GenerateLetter) ah.getModelData();

		List<FinFeeDetail> fees = gl.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		for (FinFeeDetail fee : fees) {
			BigDecimal maxWaiverPer = fee.getMaxWaiverPerc();
			BigDecimal waiverAmt = (fee.getActualAmount().multiply(maxWaiverPer)).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);

			if (fee.getWaivedAmount().compareTo(waiverAmt) > 0) {
				String[] valueParm = new String[3];
				valueParm[0] = "Waiver amount";
				valueParm[1] = "Actual waiver amount:" + CurrencyUtil.format(waiverAmt);
				valueParm[2] = fee.getFeeTypeCode();
				ah.setErrorDetail(new ErrorDetail("90257", valueParm));
			}
		}

		ah.setErrorDetails(ErrorUtil.getErrorDetails(ah.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader delete(AuditHeader ah) {
		logger.info(Literal.ENTERING);

		ah = businessValidation(ah, "delete");
		if (!ah.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return ah;
		}

		GenerateLetter gl = (GenerateLetter) ah.getAuditDetail().getModelData();
		generateLetterDAO.delete(gl, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(ah);

		logger.info(Literal.LEAVING);
		return ah;
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		ah = businessValidation(ah, "doApprove");

		if (!ah.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return ah;
		}

		GenerateLetter gl = new GenerateLetter();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), gl);

		generateLetterDAO.delete(gl, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(gl.getRecordType())) {
			ah.getAuditDetail().setBefImage(generateLetterDAO.getLetter(gl.getId()));
		}

		if (gl.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			generateLetterDAO.delete(gl, TableType.MAIN_TAB);
		} else {
			gl.setRoleCode("");
			gl.setNextRoleCode("");
			gl.setTaskId("");
			gl.setNextTaskId("");
			gl.setWorkflowId(0);

			if (gl.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				gl.setRecordType("");
				gl.setAgreementTemplate(0);
				gl.setModeofTransfer(gl.getRequestType());
				gl.setApprovedBy(gl.getLastMntBy());
				gl.setApprovedOn(new Timestamp(System.currentTimeMillis()));

				generateLetter(gl);
				generateLetterDAO.save(gl, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				gl.setRecordType("");
				generateLetterDAO.update(gl, TableType.MAIN_TAB);
			}

		}

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(gl);
		auditHeaderDAO.addAudit(ah);

		logger.info(Literal.LEAVING);
		return ah;
	}

	private long saveLoanLetterdetails(GenerateLetter gl) {
		String finType = gl.getFinanceDetail().getFinScheduleData().getFinanceType().getFinType();
		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO.getLetterMapping(finType);

		for (LoanTypeLetterMapping ltlp : letterMapping) {

			if (!ltlp.getLetterType().equals(gl.getLetterType())) {
				continue;
			}

			gl.setAgreementTemplate(ltlp.getAgreementCodeId());
			gl.setEmailTemplate(ltlp.getEmailTemplateId());
			gl.setModeofTransfer(LetterMode.OTC.name());

			Long letterId = autoLetterGenerationDAO.getAutoLetterId(gl.getFinID(), gl.getLetterType());
			if (letterId == null) {
				return autoLetterGenerationDAO.save(gl);
			} else {
				autoLetterGenerationDAO.update(letterId, LetterMode.OTC.name());
			}

			return letterId;
		}
		return 0;
	}

	private void saveFees(GenerateLetter gl) {
		List<FinFeeDetail> fees = gl.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (CollectionUtils.isNotEmpty(fees)) {
			for (FinFeeDetail fee : fees) {
				fee.setFinReference(gl.getFinReference());
				fee.setFinID(gl.getFinID());
				fee.setFeeID(finFeeDetailDAO.save(fee, false, ""));
				gl.setFeeID(fee.getFeeID());
			}
		}
	}

	private void processfees(GenerateLetter gl, List<GenerateLetter> letterInfo) {
		FinReceiptData rd = new FinReceiptData();
		FinanceDetail fd = gl.getFinanceDetail();
		FinReceiptHeader frh = new FinReceiptHeader();
		frh.setPartPayAmount(BigDecimal.ZERO);
		rd.setFinanceDetail(fd);
		rd.setTdPriBal(fd.getFinScheduleData().getFinPftDeatil().getTdSchdPriBal());
		rd.setReceiptHeader(frh);
		setMapDetails(gl, letterInfo);
	}

	@Override
	public void setMapDetails(GenerateLetter gl, List<GenerateLetter> letterInfo) {
		Date appDate = SysParamUtil.getAppDate();
		FinanceDetail fd = gl.getFinanceDetail();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		LoanLetter letter = new LoanLetter();
		Customer customer = fd.getCustomerDetails().getCustomer();

		String loanClosureType = finReceiptHeaderDAO.getClosureTypeValue(fm.getFinID());

		if (loanClosureType == null) {
			loanClosureType = ClosureType.CLOSURE.code();
		}

		letter.setClosureType(loanClosureType);
		letter.setCustCtgCode(customer.getCustCtgCode());
		letter.setCustGenderCode(customer.getCustGenderCode());
		letter.setCustomerType(customer.getCustTypeCode());
		letter.setLoanClosureAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));
		letter.setLoanCancellationAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));

		if (CollectionUtils.isNotEmpty(letterInfo)) {
			letter.setSequenceNo(letterInfo.size() + 1);
			String deliveryStatus = letterInfo.get(letterInfo.size() - 1).getDeliveryStatus();

			CourierStatus courier = CourierStatus.getCourier(deliveryStatus);

			if (courier != null) {
				deliveryStatus = courier.getCode();
			}

			letter.setPrvLetterCourierDeliveryStatus(deliveryStatus);
		} else {
			letter.setSequenceNo(1);
		}

		fm.setLoanLetter(letter);
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		logger.info(Literal.ENTERING);

		ah = businessValidation(ah, "doApprove");
		if (!ah.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return ah;
		}

		GenerateLetter gl = (GenerateLetter) ah.getAuditDetail().getModelData();

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		generateLetterDAO.delete(gl, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);

		logger.info(Literal.LEAVING);
		return ah;
	}

	@Override
	public void getFinanceDetailById(GenerateLetter gl) {
		FinanceDetail fd = new FinanceDetail();

		gl.setFinanceDetail(fd);

		String finReference = gl.getFinReference();
		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "_View", false);

		if (fm == null) {
			return;
		}

		FinScheduleData schdData = fd.getFinScheduleData();

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView"));
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "_AView", false));
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, "_AView", false));
		schdData.setFinODDetails(finODDetailsDAO.getFinODDetailsByFinRef(fm.getFinID()));

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}
		List<GenerateLetter> letterInfo = generateLetterDAO.getLoanLetterInfo(fm.getFinID(), gl.getLetterType());

		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));
		schdData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_View"));

		if (gl.getActualAmt().compareTo(BigDecimal.ZERO) > 0 || gl.getWaiverAmt().compareTo(BigDecimal.ZERO) > 0) {
			List<FinFeeDetail> fees = new ArrayList<>();

			FinFeeDetail fee = new FinFeeDetail();

			fee.setFinEvent(NOCConstants.getLetterType(gl.getLetterType()));
			fee.setActualAmount(gl.getActualAmt());
			fee.setWaivedAmount(gl.getWaiverAmt());

			fees.add(fee);
			fd.setFinFeeDetails(fees);
			fd.setModuleDefiner("GenerateLetter");
		}

		schdData.setFeeEvent(NOCConstants.getLetterType(gl.getLetterType()));
		FinanceSummary summary = new FinanceSummary();

		summary.setFinID(finID);
		summary.setFinReference(finReference);
		prepareProfitDetailSummary(summary, schdData);
		prepareODSummary(fd, summary);

		schdData.setFinanceSummary(summary);

		if (CollectionUtils.isNotEmpty(letterInfo)) {
			gl.getFinanceDetail()
					.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(),
							NOCConstants.getLetterType(gl.getLetterType()), "_AView", false,
							FinanceConstants.MODULEID_FINTYPE));
			processfees(gl, letterInfo);
		}

		if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(fm.getClosingStatus())) {
			gl.setReasonCode(StringUtils.trimToEmpty(generateLetterDAO.getReasonCode(finID)));
		}
		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			gl.setReasonCode(StringUtils.trimToEmpty(generateLetterDAO.getCancelReasons(finReference)));
		}
	}

	@Override
	public List<ReceiptAllocationDetail> getPrinAndPftWaiver(String finReference) {
		return generateLetterDAO.getPrinAndPftWaiver(finReference);
	}

	@Override
	public List<FinExcessAmount> getExcessAvailable(long finID) {
		return finExcessAmountDAO.getExcessAmountsByRef(finID);
	}

	@Override
	public List<GenerateLetter> getLetterInfo(GenerateLetter gl) {
		return generateLetterDAO.getLetterInfo(gl.getFinID());
	}

	@Override
	public LoanLetter generateLetter(GenerateLetter gl) {
		saveFees(gl);

		long letterID = saveLoanLetterdetails(gl);

		gl.setLetterID(letterID);

		LoanLetter letter = letterService.generate(letterID, SysParamUtil.getAppDate());

		if (LetterMode.OTC.name().equals(gl.getModeofTransfer())) {
			letter.setModeofTransfer(LetterMode.OTC.name());
			letter.setApprovedBy(gl.getApprovedBy());
			letter.setGeneratedBy(gl.getApprovedBy());
		}

		letter.setFeeID(gl.getFeeID());

		if ("A".equals(gl.getModeofTransfer()) && letter.isBlocked()) {
			letter.setGenerated(-1);
			letter.setStatus("B");
			letter.setRemarks(BLOCKED_MSG);
		} else {
			letter.setGenerated(1);
			letter.setStatus("S");

			letterService.createAdvise(letter);
			letterService.update(letter);
		}

		return letter;
	}

	@Override
	public List<ManualAdvise> getManualAdvises(long finID) {
		return manualAdviseDAO.getManualAdvise(finID, true);
	}

	@Override
	public List<ManualAdvise> getpayableAdvises(long finID) {
		return manualAdviseDAO.getPaybleAdvises(finID, "");
	}

	private void prepareProfitDetailSummary(FinanceSummary summary, FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();

		FinanceProfitDetail fpd = new FinanceProfitDetail();

		fpd = accrualService.calProfitDetails(fm, schdData.getFinanceScheduleDetails(), fpd, fm.getAppDate());
		fm.setRepayProfitRate(fpd.getCurReducingRate());

		summary.setTotalCpz(fpd.getTotalPftCpz());
		summary.setTotalProfit(fpd.getTotalPftSchd());
		summary.setTotalRepayAmt(fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()));
		summary.setNumberOfTerms(fpd.getNOInst());
		summary.setMaturityDate(fpd.getMaturityDate());
		summary.setFirstEmiAmount(fpd.getFirstRepayAmt());
		summary.setNextSchDate(fpd.getNSchdDate());
		summary.setNextRepayAmount(fpd.getNSchdPri().add(fpd.getNSchdPft()));
		summary.setFutureInst(fpd.getFutureInst());
		summary.setFutureTenor(DateUtil.getMonthsBetween(fpd.getNSchdDate(), fpd.getMaturityDate()));
		summary.setFirstInstDate(fpd.getFirstRepayDate());
		summary.setSchdPriPaid(fpd.getTotalPriPaid());
		summary.setSchdPftPaid(fpd.getTotalPftPaid());
		summary.setPaidTotal(fpd.getTotalPriPaid().add(fpd.getTotalPftPaid()));
		summary.setFinLastRepayDate(fpd.getPrvRpySchDate());
		summary.setOutStandPrincipal(fpd.getTotalPriBal());
		summary.setOutStandProfit(fpd.getTotalPftBal());
		summary.setTotalOutStanding(fpd.getTotalPriBal().add(fpd.getTotalPftBal()));
		summary.setPrincipal(fpd.getTdSchdPriBal());
		summary.setFuturePrincipal(fpd.getTotalPriBal().subtract(fpd.getTdSchdPriBal()));
		summary.setInterest(fpd.getTdSchdPftBal());

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			summary.setUtilizedAmt(fpd.getTotalPriBal());
			summary.setAvailableAmt(summary.getSanctionAmt().subtract(summary.getUtilizedAmt()));
		}
	}

	private void prepareODSummary(FinanceDetail fd, FinanceSummary summary) {
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinanceScheduleDetail> schedules = fd.getFinScheduleData().getFinanceScheduleDetails();

		List<FinODDetails> odDetails = schdData.getFinODDetails();
		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		if (odDetails == null && fees == null) {
			return;
		}

		BigDecimal totalLPP = BigDecimal.ZERO;
		BigDecimal totalLPI = BigDecimal.ZERO;
		BigDecimal lppPaid = BigDecimal.ZERO;
		BigDecimal lppWaived = BigDecimal.ZERO;
		BigDecimal lpiPaid = BigDecimal.ZERO;
		BigDecimal lpiWaived = BigDecimal.ZERO;

		BigDecimal actaulFee = BigDecimal.ZERO;
		BigDecimal feePaid = BigDecimal.ZERO;
		BigDecimal feeWaived = BigDecimal.ZERO;
		int odInst = 0;

		if (CollectionUtils.isNotEmpty(odDetails)) {
			for (FinODDetails odDetail : odDetails) {
				totalLPP = totalLPP.add(odDetail.getTotPenaltyAmt());
				totalLPI = totalLPI.add(odDetail.getLPIAmt());
				lppPaid = lppPaid.add(odDetail.getTotPenaltyPaid());
				lppWaived = lppWaived.add(odDetail.getTotWaived());
				lpiPaid = lpiPaid.add(odDetail.getLPIPaid());
				lpiWaived = lpiWaived.add(odDetail.getLPIWaived());

			}
		}

		if (CollectionUtils.isNotEmpty(fees)) {
			for (FinFeeDetail fee : fees) {
				actaulFee = actaulFee.add(fee.getActualAmount());
				feePaid = feePaid.add(fee.getPaidAmount().add(fee.getPaidTDS()));
				feeWaived = feeWaived.add(fee.getWaivedAmount().add(fee.getWaivedGST()));
			}
		}

		summary.setTotalFees(actaulFee);
		summary.setTotalPaidFee(feePaid);
		summary.setTotalWaiverFee(feeWaived);
		summary.setFinODTotPenaltyAmt(totalLPP);
		summary.setFinODTotWaived(lppWaived);
		summary.setFinODTotPenaltyPaid(lppPaid);
		summary.setTotalLPI(totalLPI);
		summary.setLpiPaid(lpiPaid);
		summary.setLpiWaived(lpiWaived);
		summary.setTotalOverDueIncCharges(summary.getTotalOverDue().add(summary.getDueCharges()));
		summary.setFinODDetail(odDetails);
		summary.setOverDueInstlments(odInst);
		summary.setOverDueAmount(summary.getTotalOverDueIncCharges());
		summary.setTotalPriSchd(SchdUtil.getTotalPrincipalSchd(schedules));
		schdData.setFinODDetails(odDetails);
		schdData.setFinFeeDetailList(null);
	}

	@Autowired
	public void setGenerateLetterDAO(GenerateLetterDAO generateLetterDAO) {
		this.generateLetterDAO = generateLetterDAO;
	}

	@Autowired
	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}

	@Autowired
	public void setLoanLetterUploadDAO(LoanLetterUploadDAO loanLetterUploadDAO) {
		this.loanLetterUploadDAO = loanLetterUploadDAO;
	}

	@Override
	public long getInitiatedLoan(long finID, String letterType) {
		return generateLetterDAO.getInitiatedLoan(finID, letterType);
	}

	@Override
	public boolean letterIsInQueu(long finID, String letterType) {
		return generateLetterDAO.letterIsInQueu(finID, letterType);
	}

	@Override
	public void deleteAutoLetterGeneration(long finID, String letterType) {
		this.generateLetterDAO.deleteAutoLetterGeneration(finID, letterType);
	}

	@Autowired
	public void setAutoLetterGenerationDAO(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}

	@Autowired
	public void setLetterService(LetterService letterService) {
		this.letterService = letterService;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

}