package com.pennant.pff.noc.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
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

	private GenerateLetterDAO generateLetterDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	protected FinExcessAmountDAO finExcessAmountDAO;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;
	private LoanLetterUploadDAO loanLetterUploadDAO;

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
	public FinanceMain getFinanceMainByRef(String finReferece, String type, boolean isWIF) {
		return financeMainDAO.getFinanceMainByRef(finReferece, "", false);
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

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		// TODO Auto-generated method stub
		return auditDetail;
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
				saveFees(gl);
				generateLetterDAO.save(gl, TableType.MAIN_TAB);
				saveLoanLetterdetails(gl);
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

	private void saveLoanLetterdetails(GenerateLetter gl) {
		String finType = gl.getFinanceDetail().getFinScheduleData().getFinanceType().getFinType();
		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO.getLetterMapping(finType);

		for (LoanTypeLetterMapping ltlp : letterMapping) {

			if (!ltlp.getLetterType().equals(gl.getLetterType())) {
				continue;
			}

			gl.setAgreementTemplate(ltlp.getAgreementCodeId());
			gl.setModeofTransfer(ltlp.getLetterMode());
		}
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

	private void processfees(GenerateLetter gl) {
		FinReceiptData rd = new FinReceiptData();
		FinanceDetail fd = gl.getFinanceDetail();
		FinReceiptHeader frh = new FinReceiptHeader();
		frh.setPartPayAmount(BigDecimal.ZERO);
		rd.setFinanceDetail(fd);
		rd.setTdPriBal(fd.getFinScheduleData().getFinPftDeatil().getTdSchdPriBal());
		rd.setReceiptHeader(frh);
		setMapDetails(gl);
	}

	private void setMapDetails(GenerateLetter gl) {
		Date appDate = SysParamUtil.getAppDate();
		FinanceDetail fd = gl.getFinanceDetail();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		LoanLetter letter = new LoanLetter();
		Customer customer = fd.getCustomerDetails().getCustomer();

		List<GenerateLetter> letterInfo = generateLetterDAO.getLoanLetterInfo(fm.getFinID(), gl.getLetterType());

		letter.setClosureType(fm.getClosureType());
		letter.setCustCtgCode(customer.getCustCtgCode());
		letter.setCustGenderCode(customer.getCustGenderCode());
		letter.setCustomerType(customer.getCustTypeCode());
		letter.setLoanClosureAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));
		letter.setLoanCancellationAge(DateUtil.getDaysBetween(fm.getClosedDate(), appDate));
		if (CollectionUtils.isNotEmpty(letterInfo)) {
			letter.setSequenceNo(letterInfo.size());
			letter.setStatusOfpreviousletters(letterInfo.get(letterInfo.size()).getStatus());
		}

		letter.setSequenceNo(0);
		letter.setStatusOfpreviousletters("");

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

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_View"));
		}
		List<GenerateLetter> letterInfo = generateLetterDAO.getLoanLetterInfo(fm.getFinID(), gl.getLetterType());

		if (CollectionUtils.isNotEmpty(letterInfo)) {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), getLetterType(gl), "_AView", false,
					FinanceConstants.MODULEID_FINTYPE));
		}

		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));
		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), getLetterType(gl), "_AView", false,
				FinanceConstants.MODULEID_FINTYPE));
		schdData.setFeeEvent(gl.getLetterType());
		FinanceSummary summary = new FinanceSummary();

		summary.setFinID(finID);
		summary.setFinReference(finReference);
		prepareProfitDetailSummary(summary, schdData);
		prepareODSummary(fd, summary);

		schdData.setFinanceSummary(summary);

		gl.setFinanceDetail(fd);
		processfees(gl);

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

		if (odDetails == null) {
			return;
		}

		BigDecimal overDuePrincipal = BigDecimal.ZERO;
		BigDecimal overDueProfit = BigDecimal.ZERO;
		BigDecimal overDueCharges = BigDecimal.ZERO;
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal totPenaltyBal = BigDecimal.ZERO;
		int odInst = 0;

		for (FinODDetails odDetail : odDetails) {
			overDuePrincipal = overDuePrincipal.add(odDetail.getFinCurODPri());
			overDueProfit = overDueProfit.add(odDetail.getFinCurODPft());
			overDueCharges = overDueCharges.add(odDetail.getTotPenaltyAmt());
			totPenaltyBal = totPenaltyBal.add(odDetail.getTotPenaltyBal());
			latePayPftBal = latePayPftBal.add(odDetail.getLPIBal());
			if (odDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
				odInst++;
			}
		}

		summary.setOverDuePrincipal(overDuePrincipal);
		summary.setOverDueProfit(overDueProfit);
		summary.setOverDueCharges(overDueCharges);
		summary.setTotalOverDue(overDuePrincipal.add(overDueProfit));
		summary.setDueCharges(totPenaltyBal.add(latePayPftBal));
		summary.setTotalOverDueIncCharges(summary.getTotalOverDue().add(summary.getDueCharges()));
		summary.setFinODDetail(odDetails);
		summary.setOverDueInstlments(odInst);
		summary.setOverDueAmount(summary.getTotalOverDueIncCharges());
		summary.setTotalPriSchd(SchdUtil.getTotalPrincipalSchd(schedules));

		schdData.setFinODDetails(odDetails);
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
}