package com.pennant.pff.noc.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.letter.service.LetterService;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.pff.noc.upload.dao.LoanLetterUploadDAO;
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

	@Override
	public List<GenerateLetter> getResult(ISearch searchFilters) {
		return generateLetterDAO.getResult(searchFilters);
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
			gl.setModeofTransfer(ltlp.getLetterMode());

			return autoLetterGenerationDAO.save(gl);
		}
		return 0;
	}

	private void saveFees(GenerateLetter gl) {
		List<FinFeeDetail> fees = gl.getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (CollectionUtils.isNotEmpty(fees)) {
			for (FinFeeDetail fee : fees) {
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
			letter.setStatusOfpreviousletters(letterInfo.get(letterInfo.size() - 1).getStatus());
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

		schdData.setFeeEvent(gl.getLetterType());
		FinanceSummary summary = new FinanceSummary();

		summary.setFinID(finID);
		summary.setFinReference(finReference);
		prepareProfitDetailSummary(summary, schdData);
		prepareODSummary(fd, summary);

		schdData.setFinanceSummary(summary);

		if (CollectionUtils.isNotEmpty(letterInfo)) {
			gl.getFinanceDetail().setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(),
					getLetterType(gl), "_AView", false, FinanceConstants.MODULEID_FINTYPE));
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

		LoanLetter letter = letterService.generate(letterID, SysParamUtil.getAppDate());

		letter.setFeeID(gl.getFeeID());

		if (letter.isBlocked()) {
			letter.setGenerated(-1);
			letter.setStatus("B");
			letter.setRemarks(BLOCKED_MSG);
		} else {
			letterService.createAdvise(letter);

			letter.setGenerated(1);
			letter.setStatus("S");
		}

		if ("Submit".equals(gl.getRecordType())) {
			letterService.update(letter);

			generateLetterDAO.delete(gl, TableType.MAIN_TAB);
		} else {
			autoLetterGenerationDAO.deleteFromStage(letterID);

			if (gl.getFeeID() != null) {
				finFeeDetailDAO.getFinFeeDetail(gl.getFeeID());
			}

		}

		return letter;
	}

	@Override
	public List<ManualAdvise> getManualAdvises(long finID) {
		return manualAdviseDAO.getManualAdvise(finID, true);
	}

	private String getLetterType(GenerateLetter gl) {
		String letterType = null;

		switch (gl.getLetterType()) {
		case "NOC": {
			letterType = "NOCLTR";
			break;
		}
		case "CLOSURE": {
			letterType = "CLOSELTR";
			break;
		}
		case "CANCELLATION": {
			letterType = "CANCLLTR";
			break;
		}
		default:
			break;
		}
		return letterType;
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
	public boolean isLetterInitiated(long finID, String letterType) {
		return generateLetterDAO.isLetterInitiated(finID, letterType);
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
}