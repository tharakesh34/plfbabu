package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.accounting.PostAgainst;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.TaxDetail;
import com.pennanttech.ws.model.manualAdvice.ManualAdviseResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FeePostingController extends AbstractController {
	private FeePostingService feePostingService;
	private FinanceDetailService financeDetailService;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinFeeDetailService finFeeDetailService;
	private ManualAdviseService manualAdviseService;

	private FeeTypeDAO feeTypeDAO;
	private FinanceMainDAO financeMainDAO;

	private static final String ERR_90502 = "90502";
	private static final String ERR_90224 = "90224";

	public WSReturnStatus doFeePostings(FeePostings fp) {
		logger.debug(Literal.ENTERING);

		logReference(fp.getFeeTyeCode());

		try {
			prepareFeePostings(fp);

			AuditHeader auditHeader = feePostingService.doApprove(getAuditHeader(fp, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = auditHeader.getErrorMessage();

			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(0);
				return getFailedStatus(error.getCode(), error.getError());
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			return APIErrorHandlerService.getFailedStatus();
		}

		return APIErrorHandlerService.getSuccessStatus();
	}

	public ManualAdviseResponse doCreateAdvise(ManualAdvise ma) {
		logger.debug(Literal.ENTERING);

		ManualAdviseResponse response = new ManualAdviseResponse();
		AuditHeader auditHeader = null;

		FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(ma.getFeeTypeCode());
		if (feeType == null) {
			response.setReturnStatus(getFailedStatus(ERR_90224, "feeType", ma.getFeeTypeCode()));
			return response;
		}

		boolean taxApplicable = feeType.isTaxApplicable();
		String taxComp = feeType.getTaxComponent();

		BigDecimal balanceAmt = ma.getBalanceAmt();

		if (AdviseType.isPayable(ma.getAdviseType())) {
			balanceAmt = ma.getAdviseAmount();
		}

		ma.setFeeTypeID(feeType.getFeeTypeID());
		ma.setBalanceAmt(balanceAmt);

		if (taxApplicable) {
			response.setTaxDetail(calculateGST(ma, taxComp));
		}

		prepareManualAdvise(ma);

		auditHeader = getAuditHeader(ma, PennantConstants.TRAN_WF);

		if (!ma.isStp()) {
			manualAdviseService.saveOrUpdate(auditHeader);
		} else {
			manualAdviseService.doApprove(auditHeader);
		}

		List<ErrorDetail> errors = auditHeader.getOverideMessage();

		if (CollectionUtils.isNotEmpty(errors)) {
			ErrorDetail error = errors.get(0);
			response.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));
			return response;
		}

		ManualAdvise advise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		response.setAdviseId(advise.getAdviseID());

		logger.debug(Literal.LEAVING);
		return response;
	}

	public ManualAdviseResponse validateAdviseDetail(ManualAdvise ma) {
		ManualAdviseResponse response = new ManualAdviseResponse();

		if (ma.getAdviseType() <= 0) {
			response.setReturnStatus(getFailedStatus(ERR_90502, "AdviseType "));
			return response;
		}

		if (ma.getValueDate() == null) {
			response.setReturnStatus(getFailedStatus(ERR_90502, "ValueDate "));
			return response;
		}

		if (!(AdviseType.isPayable(ma.getAdviseType()) || AdviseType.isReceivable(ma.getAdviseType()))) {
			response.setReturnStatus(getFailedStatus(ERR_90224, "AdviseType ", String.valueOf(ma.getAdviseType())));
			return response;
		}

		BigDecimal adviseAmount = ma.getAdviseAmount();
		if (adviseAmount == null) {
			adviseAmount = BigDecimal.ZERO;
		}

		if (adviseAmount.compareTo(BigDecimal.ZERO) <= 0) {
			response.setReturnStatus(getFailedStatus(ERR_90502, "AdviseAmount "));
			return response;
		}

		String feeTypeCode = ma.getFeeTypeCode();

		if (StringUtils.isBlank(feeTypeCode)) {
			response.setReturnStatus(getFailedStatus(ERR_90502, "FeeTypeCode "));
			return response;
		}

		if (!feeTypeDAO.isValidFee(feeTypeCode, ma.getAdviseType())) {
			response.setReturnStatus(getFailedStatus("90329", feeTypeCode, "AdviseType: " + ma.getAdviseType()));
			return response;
		}

		FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(feeTypeCode);

		if (feeType == null) {
			response.setReturnStatus(getFailedStatus(ERR_90224, "feeType", feeTypeCode));
			return response;
		}

		BigDecimal eligibleAmount = manualAdviseService.getEligibleAmount(ma, feeType);

		if (adviseAmount.compareTo(eligibleAmount) > 0) {
			String adviseAmt = "Advise Amount : " + adviseAmount;
			String eligibleAmt = "Eligible Amount : " + eligibleAmount;
			response.setReturnStatus(getFailedStatus("12723", adviseAmt, eligibleAmt));
			return response;
		}

		return response;
	}

	private void prepareFeePostings(FeePostings fp) {
		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		fp.setUserDetails(loggedInUser);
		fp.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fp.setNewRecord(true);
		fp.setVersion(1);
		fp.setPostDate(SysParamUtil.getAppDate());
		fp.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		fp.setLastMntBy(loggedInUser.getUserId());
		fp.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fp.setSourceId(APIConstants.FINSOURCE_ID_API);

		if (PostAgainst.isLoan(fp.getPostAgainst())) {
			fp.setPostingDivision(financeMainDAO.getLovDescFinDivisionByReference(fp.getReference()));
		}
	}

	private void prepareManualAdvise(ManualAdvise ma) {
		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		ma.setVersion(1);
		ma.setPostDate(SysParamUtil.getAppDate());
		ma.setUserDetails(loggedInUser);
		ma.setLastMntBy(loggedInUser.getUserId());
		ma.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ma.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ma.setNewRecord(true);

		if (!ma.isStp()) {
			String workflowType = ModuleUtil.getWorkflowType("ManualAdvise");
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);
			WorkflowEngine engine = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
			ma.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			ma.setWorkflowId(workFlowDetails.getWorkflowId());
			ma.setRoleCode(workFlowDetails.getFirstTaskOwner());
			ma.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
			ma.setTaskId(engine.getUserTaskId(ma.getRoleCode()));
			ma.setNextTaskId(engine.getUserTaskId(ma.getNextRoleCode()) + ";");
		} else {
			ma.setFinSource(PennantConstants.FINSOURCE_ID_API);
			ma.setFinSourceId(PennantConstants.FINSOURCE_ID_API);
			ma.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
	}

	private TaxDetail calculateGST(ManualAdvise ma, String taxComp) {
		logger.debug(Literal.ENTERING);

		FinFeeDetail fee = new FinFeeDetail();

		FinanceDetail fd = financeDetailService.getFinSchdDetailById(ma.getFinID(), "", false);

		if (fd == null) {
			return null;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		fd.setFinanceTaxDetail(financeTaxDetailService.getApprovedFinanceTaxDetail(finID));

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);

		fee.setCalculatedAmount(ma.getAdviseAmount());
		fee.setTaxComponent(taxComp);
		fee.setTaxApplicable(true);

		FinTypeFees finTypeFee = new FinTypeFees();
		finTypeFee.setTaxComponent(taxComp);
		finTypeFee.setTaxApplicable(true);
		finTypeFee.setAmount(ma.getAdviseAmount());

		finFeeDetailService.convertGSTFinTypeFees(fee, finTypeFee, fd, taxPercentages);
		finFeeDetailService.calculateFees(fee, schdData, taxPercentages);

		String taxComponent = "";
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(fee.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
		}

		TaxDetail taxDetail = new TaxDetail();

		TaxHeader taxHeader = fee.getTaxHeader();

		BigDecimal totalGST = BigDecimal.ZERO;

		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		for (Taxes taxes : taxDetails) {
			BigDecimal netTax = taxes.getNetTax();
			totalGST = totalGST.add(netTax);

			switch (taxes.getTaxType()) {
			case RuleConstants.CODE_CGST:
				taxDetail.setNetCGST(netTax);
				break;
			case RuleConstants.CODE_SGST:
				taxDetail.setNetSGST(netTax);
				break;
			case RuleConstants.CODE_IGST:
				taxDetail.setNetIGST(netTax);
				break;
			case RuleConstants.CODE_UGST:
				taxDetail.setNetUGST(netTax);
				break;
			case RuleConstants.CODE_CESS:
				taxDetail.setNetCESS(netTax);
				break;
			default:
				break;
			}
		}

		taxDetail.setNetTGST(totalGST);
		taxDetail.setTotal(fee.getNetAmountOriginal().add(totalGST));
		taxDetail.setAdviseAmount(fee.getNetAmountOriginal());
		taxDetail.setGstType(taxComponent);

		logger.debug(Literal.LEAVING);
		return taxDetail;
	}

	private AuditHeader getAuditHeader(ManualAdvise ma, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, ma.getBefImage(), ma);

		String finReference = ma.getFinReference();
		LoggedInUser userDtls = ma.getUserDetails();

		AuditHeader ah = new AuditHeader(finReference, null, null, null, ad, userDtls, new HashMap<>());
		ah.setApiHeader(PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY));

		return ah;
	}

	private AuditHeader getAuditHeader(FeePostings fp, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, fp.getBefImage(), fp);
		String id = String.valueOf(fp.getId());

		return new AuditHeader(id, id, null, null, ad, fp.getUserDetails(), new HashMap<>());
	}

	public void setFeePostingService(FeePostingService feePostingService) {
		this.feePostingService = feePostingService;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
