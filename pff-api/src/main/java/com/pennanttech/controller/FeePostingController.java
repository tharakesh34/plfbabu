package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.ValueLabel;
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
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.TaxDetail;
import com.pennanttech.ws.model.manualAdvice.ManualAdviseResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FeePostingController extends ExtendedTestClass {
	private final Logger logger = LogManager.getLogger(getClass());

	private FeePostingService feePostingService;
	private FeeTypeDAO feeTypeDAO;
	private FinanceDetailService financeDetailService;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinFeeDetailService finFeeDetailService;
	private ManualAdviseService manualAdviseService;

	/**
	 * Method for create FeePostings in PLF system.
	 * 
	 * @param feePostings
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */
	public WSReturnStatus doFeePostings(FeePostings feePostings) {
		logger.debug(Literal.ENTERING);
		// for logging purpose
		APIErrorHandlerService.logReference(feePostings.getFeeTyeCode());
		try {
			doSetPrepareData(feePostings);
			AuditHeader auditHeader = getAuditHeader(feePostings, PennantConstants.TRAN_WF);

			auditHeader = feePostingService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			} else {
				return APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public ManualAdviseResponse doCreateAdvise(ManualAdvise manualAdvise) {
		logger.debug(Literal.ENTERING);

		ManualAdviseResponse response = new ManualAdviseResponse();
		LoggedInUser userDetails = null;
		AuditHeader auditHeader = null;

		FeeType feeType = feeTypeDAO.getApprovedFeeTypeByFeeCode(manualAdvise.getFeeTypeCode());
		if (feeType == null) {
			String[] param = new String[2];
			param[0] = "feeType";
			param[1] = manualAdvise.getFeeTypeCode();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", param));
			return response;
		}
		boolean taxApplicable = feeType.isTaxApplicable();
		String taxComp = feeType.getTaxComponent();
		manualAdvise.setFeeTypeID(feeType.getFeeTypeID());

		// set BalanceAmt only when advise type is Payable
		if (StringUtils.equals(String.valueOf(manualAdvise.getAdviseType()),
				String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE))) {
			manualAdvise.setBalanceAmt(manualAdvise.getAdviseAmount());
		}

		// if tax applicable , calculate GST Details
		if (taxApplicable) {
			response = calculateGST(manualAdvise, taxApplicable, taxComp);
		}

		userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		manualAdvise.setVersion(1);
		manualAdvise.setPostDate(SysParamUtil.getAppDate());
		manualAdvise.setUserDetails(userDetails);
		manualAdvise.setLastMntBy(userDetails.getUserId());
		manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		manualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		manualAdvise.setNewRecord(true);

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);

		// set Workflow details
		if (!manualAdvise.isStp()) {
			String workflowType = ModuleUtil.getWorkflowType("ManualAdvise");
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);
			WorkflowEngine engine = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
			manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			manualAdvise.setWorkflowId(workFlowDetails.getWorkflowId());
			manualAdvise.setRoleCode(workFlowDetails.getFirstTaskOwner());
			manualAdvise.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
			manualAdvise.setTaskId(engine.getUserTaskId(manualAdvise.getRoleCode()));
			manualAdvise.setNextTaskId(engine.getUserTaskId(manualAdvise.getNextRoleCode()) + ";");

			auditHeader = getAuditHeader(manualAdvise, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = manualAdviseService.saveOrUpdate(auditHeader);
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().isEmpty()) {
				for (ErrorDetail error : auditHeader.getOverideMessage()) {
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(error.getCode(), error.getError()));
					return response;
				}
			}
		} else {
			manualAdvise.setFinSource(PennantConstants.FINSOURCE_ID_API);
			manualAdvise.setFinSourceId(PennantConstants.FINSOURCE_ID_API);
			manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			auditHeader = getAuditHeader(manualAdvise, PennantConstants.TRAN_ADD);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = manualAdviseService.doApprove(auditHeader);
			if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().isEmpty()) {
				for (ErrorDetail error : auditHeader.getOverideMessage()) {
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(error.getCode(), error.getError()));
					return response;
				}
			}
		}

		// set Advise ID and GST detail to response
		ManualAdvise advise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		response.setAdviseId(advise.getAdviseID());

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * Validate Payable/Receivable advise detail
	 * 
	 * @param manualAdvise
	 * @return {@link FinFeeDetail}
	 */
	public ManualAdviseResponse validateAdviseDetail(ManualAdvise manualAdvise) {
		ManualAdviseResponse error = new ManualAdviseResponse();

		// Validate Advise Type
		if (manualAdvise.getAdviseType() <= 0) {
			String[] errorParam = new String[1];
			errorParam[0] = "AdviseType ";
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", errorParam));
			return error;
		} else {
			boolean isAdvise = false;
			List<ValueLabel> adviseTypes = PennantStaticListUtil.getManualAdviseTypes();
			for (ValueLabel adviseType : adviseTypes) {
				if (manualAdvise.getAdviseType() == Integer.valueOf(adviseType.getValue())) {
					isAdvise = true;
					break;
				}
			}
			if (!isAdvise) {
				String[] errorParam = new String[2];
				errorParam[0] = "AdviseType ";
				errorParam[1] = String.valueOf(manualAdvise.getAdviseType());
				error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", errorParam));
				return error;
			}
		}

		// Mandatory validation for FeeTypeCode
		if (StringUtils.isBlank(manualAdvise.getFeeTypeCode())) {
			String[] errorParam = new String[1];
			errorParam[0] = "FeeTypeCode ";
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", errorParam));
			return error;
		} else {
			// validate FeeTypeCode
			long feeId = feeTypeDAO.getFinFeeTypeIdByFeeType(manualAdvise.getFeeTypeCode(), "_AView");
			if (feeId == Long.MIN_VALUE) {
				String[] param = new String[2];
				param[0] = "feeType";
				param[1] = manualAdvise.getFeeTypeCode();
				error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90224", param));
				return error;
			}

			// Validate FeeTypeCode is Applicable for AdviseType or not
			boolean isFeeType = false;
			List<FeeType> feeTypes = feeTypeDAO.getManualAdviseFeeType(manualAdvise.getAdviseType(), "_AView");
			for (FeeType feeType : feeTypes) {
				if (StringUtils.equals(manualAdvise.getFeeTypeCode(), feeType.getFeeTypeCode())) {
					isFeeType = true;
					break;
				}
			}
			if (!isFeeType) {
				String[] errorParam = new String[2];
				errorParam[0] = manualAdvise.getFeeTypeCode();
				errorParam[1] = "AdviseType: " + manualAdvise.getAdviseType();
				error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90329", errorParam));
				return error;
			}
		}

		// validate Advise Amount
		if (manualAdvise.getAdviseAmount() == null || manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] errorParam = new String[1];
			errorParam[0] = "AdviseAmount ";
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", errorParam));
			return error;
		}

		// validate Value Date
		if (manualAdvise.getValueDate() == null) {
			String[] errorParam = new String[1];
			errorParam[0] = "ValueDate ";
			error.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", errorParam));
			return error;
		}

		return error;
	}

	/**
	 * For Calculating the GST amount, converting fees as FinFeeDetail and FinTypeFees and this is for inquiry purpose
	 * only, these values are not saving. GST Calculation is having FinFeeDetailService, Return finFeeDetail object with
	 * GST details.
	 */

	private ManualAdviseResponse calculateGST(ManualAdvise manualAdvise, boolean taxApplicable, String taxComp) {
		logger.debug(Literal.ENTERING);
		FinFeeDetail finFeeDetail = new FinFeeDetail();
		ManualAdviseResponse mar = new ManualAdviseResponse();
		FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(manualAdvise.getFinReference(), "",
				false);

		if (financeDetail == null) {
			return mar;
		}

		FinScheduleData schdData = financeDetail.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String finReference = fm.getFinReference();

		financeDetail.setFinanceTaxDetail(financeTaxDetailService.getApprovedFinanceTaxDetail(finReference));

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finReference);

		finFeeDetail.setCalculatedAmount(manualAdvise.getAdviseAmount());

		finFeeDetail.setTaxComponent(taxComp);
		finFeeDetail.setTaxApplicable(taxApplicable);

		FinTypeFees finTypeFee = new FinTypeFees();
		finTypeFee.setTaxComponent(taxComp);
		finTypeFee.setTaxApplicable(taxApplicable);
		finTypeFee.setAmount(manualAdvise.getAdviseAmount());

		finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, taxPercentages);
		finFeeDetailService.calculateFees(finFeeDetail, schdData, taxPercentages);

		String taxComponent = "";

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())) {
			taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
		}

		TaxDetail taxDetail = new TaxDetail();

		TaxHeader taxHeader = finFeeDetail.getTaxHeader();
		BigDecimal totalGstAmount = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		for (Taxes taxes : taxDetails) {
			if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
				cgstTax = taxes;
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
				sgstTax = taxes;
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
				igstTax = taxes;
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
				ugstTax = taxes;
			} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
				cessTax = taxes;
			}
		}

		// Total GST Amount
		totalGstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax()).add(ugstTax.getNetTax())
				.add(cessTax.getNetTax());

		taxDetail.setNetCGST(cgstTax.getNetTax());
		taxDetail.setNetSGST(sgstTax.getNetTax());
		taxDetail.setNetIGST(igstTax.getNetTax());
		taxDetail.setNetUGST(ugstTax.getNetTax());
		taxDetail.setNetCESS(cessTax.getNetTax());
		taxDetail.setNetTGST(totalGstAmount);

		// Total Amount include GST
		totalAmount = finFeeDetail.getNetAmountOriginal().add(totalGstAmount);
		taxDetail.setTotal(totalAmount);

		taxDetail.setAdviseAmount(finFeeDetail.getNetAmountOriginal());
		taxDetail.setGstType(taxComponent);
		mar.setTaxDetail(taxDetail);

		logger.debug(Literal.LEAVING);
		return mar;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aManualAdvise
	 * @param tranType
	 * @return AuditHeader
	 */

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(aManualAdvise.getFinReference(), null, null, null, auditDetail,
				aManualAdvise.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFeePostings
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FeePostings aFeePostings, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeePostings.getBefImage(), aFeePostings);
		return new AuditHeader(String.valueOf(aFeePostings.getId()), String.valueOf(aFeePostings.getId()), null, null,
				auditDetail, aFeePostings.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void doSetPrepareData(FeePostings feePostings) {
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		feePostings.setUserDetails(userDetails);
		feePostings.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		feePostings.setNewRecord(true);
		feePostings.setVersion(1);
		feePostings.setPostDate(SysParamUtil.getAppDate());
		feePostings.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		feePostings.setLastMntBy(userDetails.getUserId());
		feePostings.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		feePostings.setSourceId(APIConstants.FINSOURCE_ID_API);
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

}
