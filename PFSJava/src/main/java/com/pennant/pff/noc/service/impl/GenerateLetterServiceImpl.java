package com.pennant.pff.noc.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.FeeCalculator;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public class GenerateLetterServiceImpl extends GenericFinanceDetailService implements GenerateLetterService {
	private static Logger logger = LogManager.getLogger(GenerateLetterServiceImpl.class);

	public GenerateLetterServiceImpl() {
		super();
	}

	private GenerateLetterDAO generateLetterDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	protected FinExcessAmountDAO finExcessAmountDAO;
	private FeeCalculator feeCalculator;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;

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
				processfees(gl);
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

	private void processfees(GenerateLetter gl) {
		FinReceiptData rd = new FinReceiptData();
		FinReceiptHeader frh = new FinReceiptHeader();
		frh.setPartPayAmount(BigDecimal.ZERO);
		rd.setFinanceDetail(gl.getFinanceDetail());
		rd.setTdPriBal(gl.getFinanceDetail().getFinScheduleData().getFinPftDeatil().getTdSchdPriBal());
		rd.setReceiptHeader(frh);
		feeCalculator.calculateFees(rd);

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
	public FinanceDetail getFinanceDetailById(String finReference, String letterType) {
		FinanceDetail fd = new FinanceDetail();

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "_View", false);

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
		schdData.setFinPftDeatil(profitDetailsDAO.getFinProfitDetailsById(finID));
		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), letterType, "_AView", false,
				FinanceConstants.MODULEID_FINTYPE));

		FinanceSummary summary = new FinanceSummary();

		summary.setFinID(finID);
		summary.setFinReference(finReference);
		prepareProfitDetailSummary(summary, schdData);
		prepareODSummary(fd, summary);

		schdData.setFinanceSummary(summary);

		return fd;
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

		schdData.setFinODDetails(odDetails);
	}

	@Override
	public void saveClosedLoanLetterGenerator(FinanceMain fm, Date appDate) {
		List<LoanTypeLetterMapping> loanTypeLetterMapping = loanTypeLetterMappingDAO
				.getLoanTypeLettterMappingListByLoanType(fm.getFinType());

		for (LoanTypeLetterMapping ltlp : loanTypeLetterMapping) {
			if (ltlp.getLetterType().equals(NOCConstants.TYPE_CAN_LTR)) {
				continue;
			}

			GenerateLetter gl = new GenerateLetter();

			gl.setFinID(fm.getFinID());
			gl.setFinReference(fm.getFinReference());
			gl.setCustCoreBank(fm.getCustCoreBank());
			gl.setFinBranch(fm.getFinBranch());
			gl.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			gl.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			gl.setRequestType("A");
			gl.setLetterType(ltlp.getLetterType());
			gl.setCreatedBy(ltlp.getCreatedBy());
			gl.setModeofTransfer(ltlp.getLetterMode());
			gl.setGeneratedBy(ltlp.getApprovedBy());
			gl.setAdviseID(null);
			gl.setAgreementTemplate(ltlp.getAgreementCodeId());

			generateLetterDAO.save(gl, TableType.MAIN_TAB);
		}
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
	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}

}