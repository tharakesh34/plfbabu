package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeScheduleCalculator;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepayCalculator;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.financeservice.AddRepaymentService;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.financeservice.PostponementService;
import com.pennant.backend.financeservice.RateChangeService;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.financeservice.RecalculateService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.fees.FeeDetailService;
import com.pennant.backend.service.finance.FeeReceiptService;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.payorderissue.PayOrderIssueService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;
import com.rits.cloning.Cloner;

public class FinServiceInstController extends SummaryDetailService {
	private static final Logger logger = LogManager.getLogger(FinServiceInstController.class);

	private FinanceDetailService financeDetailService;
	private RateChangeService rateChangeService;
	private AddRepaymentService addRepaymentService;
	private RecalculateService recalService;
	private ChangeProfitService changeProfitService;
	private AddDisbursementService addDisbursementService;
	private ChangeFrequencyService changeFrequencyService;
	private ReScheduleService reScheduleService;
	private PostponementService postponementService;
	private RemoveTermsService rmvTermsService;
	private FinanceMainService financeMainService;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FeeDetailService feeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private BankBranchService bankBranchService;
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private ReceiptService receiptService;
	private FinTypePartnerBankService finTypePartnerBankService;
	private PartnerBankDAO partnerBankDAO;
	private ManualPaymentService manualPaymentService;
	private RepayCalculator repayCalculator;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ChangeScheduleMethodService changeScheduleMethodService;
	private FinanceTypeDAO financeTypeDAO;
	private FeeReceiptService feeReceiptService;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	protected transient WorkflowEngine workFlow = null;
	private FinanceTaxDetailService financeTaxDetailService;
	private FinAdvancePaymentsDAO finAdvancePaymensDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private BankDetailDAO bankDetailDAO;
	private BankDetailService bankDetailService;
	private CovenantsService covenantsService;
	private CovenantsDAO covenantsDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private ChequeHeaderService chequeHeaderService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private PayOrderIssueService payOrderIssueService;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private NonLanReceiptService nonLanReceiptService;
	private FeeWaiverHeaderService feeWaiverHeaderService;
	private RestructureService restructureService;

	public FinanceDetail doAddRateChange(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		fm.setEventFromDate(fsi.getFromDate());
		fm.setEventToDate(fsi.getToDate());
		fm.setRecalFromDate(fsi.getRecalFromDate());
		fm.setRecalType(fsi.getRecalType());
		fm.setRecalSchdMethod(fm.getScheduleMethod());
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.RATECHG);

		if (CalculationConstants.RPYCHG_TILLMDT.equals(fsi.getRecalType())) {
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(fsi.getRecalType())) {
			fm.setRecalToDate(fsi.getRecalToDate());
		}
		if (StringUtils.isBlank(fsi.getPftDaysBasis())) {
			fsi.setPftDaysBasis(fm.getProfitDaysBasis());
		}

		fsi.setModuleDefiner(FinServiceEvent.RATECHG);
		fd.setModuleDefiner(FinServiceEvent.RATECHG);
		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			// call schedule calculator for Rate change
			schdData = rateChangeService.getRateChangeDetails(schdData, fsi, FinServiceEvent.RATECHG);

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private void executeFeeCharges(FinanceDetail fd, FinServiceInstruction fsi, String eventCode) throws AppException {

		List<FinFeeDetail> feeList = fsi.getFinFeeDetails();

		if (feeList == null) {
			return;
		}

		if (APIConstants.REQTYPE_INQUIRY.equals(fsi.getReqType()) && (feeList.isEmpty())) {
			feeDetailService.doProcessFeesForInquiry(fd, eventCode, fsi, true);
		} else {
			FinScheduleData schdData = fd.getFinScheduleData();
			for (FinFeeDetail fee : feeList) {
				if (!AccountingEvent.RESTRUCTURE.equals(eventCode)) {
					fee.setFinEvent(eventCode);
					schdData.getFinFeeDetailList().add(fee);
					fee.setFeeScheduleMethod(PennantConstants.List_Select);
				}
			}
			feeDetailService.doExecuteFeeCharges(fd, eventCode, fsi, false);

			if (fd.isStp()) {
				for (FinFeeDetail feeDetail : schdData.getFinFeeDetailList()) {
					feeDetail.setWorkflowId(0);
				}
			}
		}

	}

	public FinanceDetail doAddRepayment(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		String recalType = fsi.getRecalType();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setEventFromDate(fsi.getFromDate());
		fm.setEventToDate(fsi.getToDate());
		// financeMain.setScheduleMethod(finServiceInst.getSchdMethod());
		// financeMain.setRecalSchdMethod(finServiceInst.getSchdMethod());
		fsi.setSchdMethod(fm.getScheduleMethod());
		fm.setRcdMaintainSts(FinServiceEvent.CHGRPY);

		fm.setRecalType(recalType);
		fm.setAdjTerms(fsi.getTerms());

		if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_ADJMDT.equals(recalType)
				|| CalculationConstants.RPYCHG_ADDTERM.equals(recalType)) {
			fm.setRecalFromDate(fsi.getFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fsi.getRecalToDate());
		} else if (CalculationConstants.RPYCHG_ADDRECAL.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		}

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fsi.setModuleDefiner(FinServiceEvent.CHGRPY);
		fd.setModuleDefiner(FinServiceEvent.CHGRPY);

		try {
			executeFeeCharges(fd, fsi, eventCode);

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			// Call Schedule calculator for Rate change
			schdData = addRepaymentService.getAddRepaymentDetails(schdData, fsi, FinServiceEvent.CHGRPY);

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);

		return fd;
	}

	public FinanceDetail doDefferment(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setEventFromDate(fsi.getFromDate());
		fm.setEventToDate(fsi.getToDate());
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.UNPLANEMIH);

		String recalType = fsi.getRecalType();

		if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fsi.getToDate());
		} else if (CalculationConstants.RPYCHG_ADJMDT.equals(recalType)
				|| CalculationConstants.RPYCHG_ADDTERM.equals(recalType)) {
			fm.setRecalFromDate(fsi.getFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fsi.getRecalToDate());
		} else if (CalculationConstants.RPYCHG_ADDRECAL.equals(recalType)) {
			fm.setRecalFromDate(fsi.getFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
			// financeMain.setScheduleRegenerated(true);
		}
		fsi.setModuleDefiner(FinServiceEvent.UNPLANEMIH);
		schdData.setFinServiceInstruction(fsi);

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			// call deferment service
			schdData = postponementService.doUnPlannedEMIH(schdData);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.ENTERING, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail addTerms(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		fm.setEventFromDate(fsi.getRecalFromDate());
		fm.setRecalFromDate(fsi.getRecalFromDate());
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setAdjTerms(fsi.getTerms());
		fm.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fsi.setFinEvent(FinServiceEvent.RECALCULATE);
		fm.setRcdMaintainSts(FinServiceEvent.ADDTERM);

		fsi.setModuleDefiner(FinServiceEvent.ADDTERM);
		fd.setModuleDefiner(FinServiceEvent.ADDTERM);

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			// Call Schedule calculator for Rate change
			schdData = recalService.getRecalculateSchdDetails(schdData, "");

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail doRecalculate(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		// fetch finance data
		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setAdjTerms(fsi.getTerms());
		fm.setRecalType(fsi.getRecalType());
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.RECALCULATE);
		fsi.setModuleDefiner(FinServiceEvent.RECALCULATE);
		fd.setModuleDefiner(FinServiceEvent.RECALCULATE);

		switch (fsi.getRecalType()) {
		case CalculationConstants.RPYCHG_TILLMDT:
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
			break;
		case CalculationConstants.RPYCHG_TILLDATE:
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fsi.getRecalToDate());
			break;
		case CalculationConstants.RPYCHG_ADJMDT:
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
			break;
		case CalculationConstants.RPYCHG_ADDRECAL:
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
			fm.setScheduleRegenerated(true);
			break;
		default:
			break;
		}

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			// Call Schedule calculator for Rate change
			schdData = recalService.getRecalculateSchdDetails(schdData, "");

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);

		return fd;
	}

	public FinanceDetail doChangeProfit(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setEventFromDate(fsi.getFromDate());
		fm.setEventToDate(fsi.getToDate());
		fm.setPftIntact(true);
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.CHGPFT);
		fsi.setModuleDefiner(FinServiceEvent.CHGPFT);
		fd.setModuleDefiner(FinServiceEvent.CHGPFT);

		// profit amount
		BigDecimal amount = fsi.getAmount();
		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			// Call Schedule calculator for Rate change
			schdData = changeProfitService.getChangeProfitDetails(schdData, amount);

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail doAddDisbursement(FinServiceInstruction fsi, FinanceDetail fd, String eventCode) {
		logger.debug(Literal.ENTERING);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		FinanceType financeType = schdData.getFinanceType();

		String recalType = fsi.getRecalType();
		Date appDate = SysParamUtil.getAppDate();

		fm.setEventFromDate(fsi.getFromDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setAdjTerms(fsi.getTerms());
		fm.setRecalSchdMethod(fm.getScheduleMethod());
		fm.setRecalType(recalType);
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.ADDDISB);

		boolean posIntProcess = false;
		Date maturityDate = null;

		if (CalculationConstants.SCHMTHD_POS_INT.equals(financeType.getFinSchdMthd())) {
			Date startDate = fsi.getFromDate();
			maturityDate = FrequencyUtil
					.getNextDate(fm.getRepayFrq(), 1, startDate, HolidayHandlerTypes.MOVE_NONE, false)
					.getNextFrequencyDate();

			posIntProcess = true;
		}

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			if (CollectionUtils.isEmpty(schdData.getFinanceScheduleDetails())) {
				fd.setFinScheduleData(ScheduleGenerator.getNewSchd(fd.getFinScheduleData()));
			}

			if (posIntProcess) {
				fm.setRecalType(CalculationConstants.RPYCHG_TILLDATE);
				fm.setRecalFromDate(maturityDate);
				fm.setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);

				// Schedule Details
				List<FinanceScheduleDetail> schList = schdData.getFinanceScheduleDetails();
				Date schDateAfterCurInst = null;
				for (FinanceScheduleDetail schd : schList) {

					// Schedule Date Finding after new disbursement Date & before Maturity Date
					if (DateUtil.compare(schd.getSchDate(), maturityDate) > 0) {
						schDateAfterCurInst = schd.getSchDate();
						break;
					}

					if (DateUtil.compare(schd.getSchDate(), maturityDate) == 0) {
						schDateAfterCurInst = null;
						schd.setPftOnSchDate(true);
						schd.setRepayOnSchDate(true);
					}
				}

				// Repay Instructions Setting
				boolean rpyInstFound = false;
				boolean futureRpyInst = false;
				List<RepayInstruction> rpyInstructions = schdData.getRepayInstructions();
				for (RepayInstruction ri : rpyInstructions) {
					if (DateUtil.compare(maturityDate, ri.getRepayDate()) == 0) {
						ri.setRepayAmount(ri.getRepayAmount().add(fsi.getAmount()));
						rpyInstFound = true;
					} else if (DateUtil.compare(maturityDate, ri.getRepayDate()) < 0) {
						futureRpyInst = true;
						break;
					}

				}

				// If instruction not found then add with Disbursement amount
				if (!rpyInstFound) {
					RepayInstruction ri = new RepayInstruction();
					ri.setRepayDate(maturityDate);
					ri.setRepayAmount(fsi.getAmount());
					ri.setRepaySchdMethod(CalculationConstants.SCHMTHD_PRI);
					schdData.getRepayInstructions().add(ri);
				}

				// If Schedule instruction not found then add with Zero amount
				if (!futureRpyInst && schDateAfterCurInst != null) {
					RepayInstruction ri = new RepayInstruction();
					ri.setRepayDate(schDateAfterCurInst);
					ri.setRepayAmount(BigDecimal.ZERO);
					ri.setRepaySchdMethod(CalculationConstants.SCHMTHD_PRI);
					schdData.getRepayInstructions().add(ri);
				}

				sortRepayInstructions(schdData.getRepayInstructions());

			} else {
				fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				fm.setEventFromDate(fsi.getFromDate());
				fm.setRecalFromDate(fsi.getFromDate());
				fm.setRecalToDate(maturityDate);
			}

			fm.setRecalToDate(maturityDate);
			fsi.setRecalToDate(maturityDate);
			fm.setEventFromDate(fsi.getFromDate());
			fsi.setFromDate(fsi.getFromDate());
		}

		if (CalculationConstants.RPYCHG_TILLMDT.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_ADJMDT.equals(recalType)) {
			fm.setRecalFromDate(fsi.getFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fsi.getRecalToDate());
		} else if (CalculationConstants.RPYCHG_ADDTERM.equals(recalType)) {
			fm.setRecalFromDate(fsi.getFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
		} else if (CalculationConstants.RPYCHG_ADDRECAL.equals(recalType)) {
			fm.setRecalFromDate(fsi.getRecalFromDate());
			fm.setRecalToDate(fm.getMaturityDate());
			fm.setScheduleRegenerated(true);
		}

		BigDecimal amount = fsi.getAmount();
		fm.setCurDisbursementAmt(amount);
		fm.setFinCurrAssetValue(fm.getFinCurrAssetValue().add(amount));

		if (fsi.isFlexiDisb()) {
			schdData.setFlexiDisb(true);
		} else {
			fm.setFlexiAmount(fm.getFlexiAmount().add(amount));
		}

		// Removed because it is wrong convention
		// set finAssetValue = FinCurrAssetValue when there is no
		// maxDisbCheck
		/*
		 * FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
		 * if(!finType.isAlwMaxDisbCheckReq()) { financeMain.setFinAssetValue(financeMain.getFinAmount()); }
		 */

		fsi.setModuleDefiner(FinServiceEvent.ADDDISB);
		fd.setModuleDefiner(FinServiceEvent.ADDDISB);
		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (fd.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail errorDetail : fd.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			// added new disbursement details
			int seqNo = schdData.getDisbursementDetails().size() + 1;
			FinanceDisbursement disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(fsi.getFromDate());
			disbursementDetails.setDisbAmount(amount);
			disbursementDetails.setDisbSeq(seqNo);
			disbursementDetails.setDisbReqDate(appDate);
			disbursementDetails.setFeeChargeAmt(fm.getFeeChargeAmt());
			List<FinanceDisbursement> list = new ArrayList<>();
			list.add(disbursementDetails);

			if (fd.getAdvancePaymentsList() != null && !fd.getAdvancePaymentsList().isEmpty()) {
				for (FinAdvancePayments advPayments : fd.getAdvancePaymentsList()) {
					if (advPayments.getDisbSeq() == 0) {
						advPayments.setDisbSeq(seqNo);
					}
				}

				// validate disbursement instructions
				List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(fd.getAdvancePaymentsList(),
						list, schdData.getFinanceMain(), true);
				for (ErrorDetail ed : errors) {
					schdData.setErrorDetail(
							ErrorUtil.getErrorDetail(new ErrorDetail(ed.getCode(), ed.getParameters())));
				}
			}

			// Call Schedule calculator for add disbursement
			if (schdData.getErrorDetails() == null || schdData.getErrorDetails().isEmpty()) {
				schdData = addDisbursementService.getAddDisbDetails(schdData, amount, BigDecimal.ZERO, false,
						FinServiceEvent.ADDDISB);
			}

			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);

			// process disbursement details
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			// financeDetail.getFinScheduleData().getFinanceMain().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			List<FinAdvancePayments> advancePayments = fd.getAdvancePaymentsList();
			if (advancePayments != null) {
				for (FinAdvancePayments advPayment : advancePayments) {
					int paymentSeq = finAdvancePaymentsService.getCountByFinReference(finID);
					advPayment.setFinID(finID);
					advPayment.setFinReference(fm.getFinReference());
					advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					advPayment.setNewRecord(true);
					advPayment.setLastMntBy(userDetails.getUserId());
					advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					advPayment.setUserDetails(fm.getUserDetails());
					advPayment.setPaymentSeq(paymentSeq + 1);
					advPayment.setDisbCCy(fm.getFinCcy());

					String paymentType = advPayment.getPaymentType();
					if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {

						BankBranch bankBranch = new BankBranch();
						if (StringUtils.isNotBlank(advPayment.getiFSC())) {
							bankBranch = bankBranchService.getBankBrachByIFSC(advPayment.getiFSC());
						} else if (StringUtils.isNotBlank(advPayment.getBranchBankCode())
								&& StringUtils.isNotBlank(advPayment.getBranchCode())) {
							bankBranch = bankBranchService.getBankBrachByCode(advPayment.getBranchBankCode(),
									advPayment.getBranchCode());
						}

						if (bankBranch != null) {
							advPayment.setiFSC(bankBranch.getIFSC());
							advPayment.setBranchBankCode(bankBranch.getBankCode());
							advPayment.setBranchCode(bankBranch.getBranchCode());
							advPayment.setBankBranchID(bankBranch.getBankBranchID());
						}
					}
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

			// set Last disbursement date for Inquiry service
			if (StringUtils.equals(fsi.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
				fd.getFinScheduleData().getFinanceSummary().setLastDisbDate(disbursementDetails.getDisbDate());
			}

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail doChangeFrequency(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(fsi, eventCode);

		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
			String repayFrq = finScheduleData.getFinanceMain().getRepayFrq();
			String frqday = String.valueOf(fsi.getFrqDay());
			frqday = frqday.length() == 1 ? "0".concat(frqday) : frqday;
			fsi.setRepayFrq(StringUtils.substring(repayFrq, 0, repayFrq.length() - 2).concat(frqday));

			int rpyTermsCompleted = 0;
			int adjRepayTerms = 0;
			int totRepayTerms = 0;
			boolean isFromDateFound = false;
			Date fromDate = DateUtility.getDBDate(DateUtility.format(fsi.getFromDate(), PennantConstants.DBDateFormat));
			fsi.setFromDate(fromDate);
			List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {
					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
						if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
							isFromDateFound = true;
						}

						totRepayTerms = totRepayTerms + 1;
						if (!isFromDateFound) {
							if (curSchd.getSchDate()
									.compareTo(finScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0) {
								rpyTermsCompleted = rpyTermsCompleted + 1;
							}
						}
					}
				}
				adjRepayTerms = totRepayTerms - rpyTermsCompleted;
			}

			fsi.setAdjRpyTerms(adjRepayTerms);
			finScheduleData.getFinanceMain().setFinSourceID(APIConstants.FINSOURCE_ID_API);
			finScheduleData.getFinanceMain().setRcdMaintainSts(FinServiceEvent.CHGFRQ);
			fsi.setModuleDefiner(FinServiceEvent.CHGFRQ);
			financeDetail.setModuleDefiner(FinServiceEvent.CHGFRQ);
			try {
				// execute fee charges
				executeFeeCharges(financeDetail, fsi, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// call change frequency service
				finScheduleData = changeFrequencyService.doChangeFrequency(finScheduleData, fsi);
				financeDetail.setFinScheduleData(finScheduleData);

				// Get the response
				financeDetail = getResponse(financeDetail, fsi);

			} catch (InterfaceException ex) {
				logger.error("InterfaceException", ex);
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				APIErrorHandlerService.logUnhandledException(e);
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				return response;
			}
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for schedule terms
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInst, String eventCode) {
		logger.debug(Literal.ENTERING);

		// fetch finance data
		FinanceDetail financeDetail = getFinanceDetails(finServiceInst, eventCode);
		if (financeDetail != null) {
			FinScheduleData finScheduleData = financeDetail.getFinScheduleData();

			FinanceMain financeMain = finScheduleData.getFinanceMain();
			financeMain.setEventFromDate(finServiceInst.getFromDate());
			financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			financeMain.setRecalType(finServiceInst.getRecalType());
			financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
			financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
			financeMain.setRcdMaintainSts(FinServiceEvent.RMVTERM);
			finServiceInst.setModuleDefiner(FinServiceEvent.RMVTERM);
			financeDetail.setModuleDefiner(FinServiceEvent.RMVTERM);

			if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getRecalFromDate());
			} else if (StringUtils.equals(finServiceInst.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				financeMain.setRecalFromDate(finServiceInst.getFromDate());
			}

			try {
				// execute fee charges
				executeFeeCharges(financeDetail, finServiceInst, eventCode);
				if (financeDetail.getFinScheduleData().getErrorDetails() != null) {
					for (ErrorDetail errorDetail : financeDetail.getFinScheduleData().getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						doEmptyResponseObject(response);
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
						return response;
					}
				}
				// Call Schedule calculator for Rate change
				finScheduleData = rmvTermsService.getRmvTermsDetails(finScheduleData);
				if (finScheduleData.getErrorDetails() != null) {
					for (ErrorDetail errorDetail : finScheduleData.getErrorDetails()) {
						FinanceDetail response = new FinanceDetail();
						response.setReturnStatus(
								APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));

						return response;
					}
				}
				financeDetail.setFinScheduleData(finScheduleData);
				// Get the response
				financeDetail = getResponse(financeDetail, finServiceInst);

			} catch (InterfaceException ex) {
				logger.error("InterfaceException", ex);
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				APIErrorHandlerService.logUnhandledException(e);
				FinanceDetail response = new FinanceDetail();
				doEmptyResponseObject(response);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				return response;
			}
		} else {
			financeDetail = new FinanceDetail();
			financeDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for process and do Reschedule action
	 * 
	 * @param finServiceInstruction
	 * @param eventCode
	 * @return FinanceDetail
	 */
	public FinanceDetail doReSchedule(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		// fetch finance data
		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();

		// tempStartDate
		List<FinanceScheduleDetail> financeScheduleDetails = null;
		financeScheduleDetails = schdData.getFinanceScheduleDetails();
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					if (fsi.getFromDate().compareTo(curSchd.getSchDate()) == 0) {
						break;
					}
				}
			}
		}

		FinanceMain financeMain = schdData.getFinanceMain();
		financeMain.setRecalFromDate(fsi.getFromDate());
		financeMain.setEventFromDate(fsi.getFromDate());
		financeMain.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		financeMain.setRcdMaintainSts(FinServiceEvent.RESCHD);
		fsi.setModuleDefiner(FinServiceEvent.RESCHD);

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			// Call Schedule calculator for Rate change
			schdData = reScheduleService.doReSchedule(schdData, fsi);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail doReceiptTransaction(FinReceiptData receiptData, String eventCode) {
		logger.debug(Literal.ENTERING);

		BigDecimal bounce = BigDecimal.ZERO;
		FinanceDetail fd = receiptData.getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug("Leaving - doReceiptTransaction");
			return fd;
		}
		FinServiceInstruction finServiceInstruction = schdData.getFinServiceInstruction();
		if (StringUtils.equals(schdData.getFinServiceInstruction().getModuleDefiner(), FinServiceEvent.EARLYSETTLE)) {
			receiptData.getReceiptHeader().setReasonCode(finServiceInstruction.getEarlySettlementReason());
		}
		if (StringUtils.equals(finServiceInstruction.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			if (finServiceInstruction.getToDate() == null) {
				finServiceInstruction.setToDate(schdData.getFinanceMain().getMaturityDate());
			}

		}

		String receiptPurpose = schdData.getFinServiceInstruction().getModuleDefiner();

		if (!RepayConstants.ALLOCATIONTYPE_MANUAL.equals(finServiceInstruction.getAllocationType())) {
			fd = validateFees(fd, receiptData);
		}

		if (schdData.getErrorDetails() != null && !schdData.getErrorDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
			return fd;
		}

		try {
			fd = doProcessReceipt(receiptData, receiptPurpose);

			if (StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
				if (schdData.getErrorDetails() == null || !schdData.getErrorDetails().isEmpty()) {
					FinanceSummary summary = schdData.getFinanceSummary();
					// summary.setFinStatus("M");
				}
			}

			if (APIConstants.REQTYPE_INQUIRY.equals(finServiceInstruction.getReqType())) {
				List<ReceiptAllocationDetail> receiptAllocationDetails = schdData.getReceiptAllocationList();
				List<ReceiptAllocationDetail> newreceiptAllocationDetails = new ArrayList<ReceiptAllocationDetail>();
				ReceiptAllocationDetail bounceAllocation = new ReceiptAllocationDetail();
				for (ReceiptAllocationDetail receiptallocation : receiptAllocationDetails) {
					if (RepayConstants.ALLOCATION_BOUNCE.equals(receiptallocation.getAllocationType())) {
						bounce = bounce.add(receiptallocation.getTotalDue());
					} else {
						newreceiptAllocationDetails.add(receiptallocation);
					}
				}
				if (bounce != BigDecimal.ZERO) {
					bounceAllocation.setAllocationType(RepayConstants.ALLOCATION_BOUNCE);
					bounceAllocation.setDueAmount(bounce);
					newreceiptAllocationDetails.add(bounceAllocation);
				}
				schdData.setReceiptAllocationList(newreceiptAllocationDetails);

				List<FinReceiptHeader> receiptAmt = new ArrayList<>();
				BigDecimal receiptProcessedAmt = BigDecimal.ZERO;
				receiptAmt = finReceiptHeaderDAO.getInProcessReceipts(fd.getFinReference());
				if (CollectionUtils.isNotEmpty(receiptAmt)) {
					for (FinReceiptHeader finReceiptHeader : receiptAmt) {
						receiptProcessedAmt = receiptProcessedAmt.add(finReceiptHeader.getReceiptAmount());
					}
					fd.setReceiptProcessingAmt(receiptProcessedAmt);
				}

			}
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			schdData = receiptService.setErrorToFSD(schdData, "9998", ex.getMessage());
			return fd;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			schdData = receiptService.setErrorToFSD(schdData, "9999", appEx.getMessage());
			return fd;
		} catch (Exception e) {
			logger.error("Exception", e);
			WSReturnStatus returnStatus = APIErrorHandlerService.getFailedStatus();
			schdData = receiptService.setErrorToFSD(schdData, returnStatus.getReturnCode(),
					returnStatus.getReturnText());
			return fd;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceDetail validateFees(FinanceDetail fd, FinReceiptData receiptData) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		FinanceMain fm = schdData.getFinanceMain();
		String roundingMode = schdData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = schdData.getFinanceMain().getRoundingTarget();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		List<ReceiptAllocationDetail> allocationList = rch.getAllocations();

		// FIXME: PV AS OF NOW, PNLY ONE FEE IS HANDLED. AFTER FIRST RELEASE
		// MULTI FEES TO BE DEVELOPED.
		// GST to be tested
		boolean isAPIFeeRequested = false;
		boolean isEventFeeRequired = false;
		boolean isFee = false;
		String apiFeeCode = null;
		BigDecimal apiActualFee = BigDecimal.ZERO;
		BigDecimal apiPaidFee = BigDecimal.ZERO;
		BigDecimal apiWaived = BigDecimal.ZERO;
		String eventFeeCode = null;
		BigDecimal eventActualFee = BigDecimal.ZERO;
		BigDecimal maxWaiver = BigDecimal.ZERO;
		BigDecimal maxWaiverAllowed = BigDecimal.ZERO;

		// Validate Fees
		if (schdData.getFinFeeDetailList() != null && !schdData.getFinFeeDetailList().isEmpty()) {
			isEventFeeRequired = true;
			eventFeeCode = schdData.getFinFeeDetailList().get(0).getFeeTypeCode();
			eventActualFee = schdData.getFinFeeDetailList().get(0).getActualAmount();
			maxWaiver = schdData.getFinFeeDetailList().get(0).getMaxWaiverPerc();

			if (maxWaiver.compareTo(BigDecimal.valueOf(100)) == 0) {
				maxWaiverAllowed = eventActualFee;
			} else if (maxWaiver.compareTo(BigDecimal.ZERO) > 0) {
				maxWaiverAllowed = eventActualFee.multiply(maxWaiver).divide(BigDecimal.valueOf(100), 0,
						RoundingMode.HALF_DOWN);
				maxWaiverAllowed = CalculationUtil.roundAmount(maxWaiverAllowed, roundingMode, roundingTarget);
			}
		}

		if (StringUtils.equals(fsi.getReqType(), APIConstants.REQTYPE_INQUIRY)) {
			return fd;
		}

		if (fsi.getFinFeeDetails() != null && !fsi.getFinFeeDetails().isEmpty()) {
			isAPIFeeRequested = true;
			apiFeeCode = fsi.getFinFeeDetails().get(0).getFeeTypeCode().toUpperCase();
			apiActualFee = fsi.getFinFeeDetails().get(0).getActualAmount();
			apiPaidFee = fsi.getFinFeeDetails().get(0).getPaidAmount();
			apiWaived = fsi.getFinFeeDetails().get(0).getWaivedAmount();
		}

		// Event fees not applicable and API not requested.
		if (!isAPIFeeRequested && !isEventFeeRequired) {
			return fd;
		}

		// Fee is Mandatory but API does not requested
		if (!isAPIFeeRequested && isEventFeeRequired) {
			schdData = receiptService.setErrorToFSD(schdData, "65019", eventFeeCode);
			return fd;
		}

		// Mismatch in the Fees requirement.
		if (isAPIFeeRequested && !isEventFeeRequired) {
			schdData = receiptService.setErrorToFSD(schdData, "90245", null);
			return fd;
		}

		// Mismatch in the Fees requirement.
		if (!StringUtils.equalsIgnoreCase(apiFeeCode, eventFeeCode)) {
			schdData = receiptService.setErrorToFSD(schdData, "90247", null);
			return fd;
		}

		// Negative Amounts
		if (apiActualFee.compareTo(BigDecimal.ZERO) < 0 || apiPaidFee.compareTo(BigDecimal.ZERO) < 0
				|| apiWaived.compareTo(BigDecimal.ZERO) < 0) {
			schdData = receiptService.setErrorToFSD(schdData, "90259", apiFeeCode);
			return fd;
		}

		String parm0 = null;
		String parm1 = null;
		int formatter = CurrencyUtil.getFormat(schdData.getFinanceMain().getFinCcy());

		// Waiver Exceeds the limit
		if (apiWaived.compareTo(maxWaiverAllowed) > 0) {
			parm0 = "Fee Waiver";
			parm1 = PennantApplicationUtil.amountFormate(maxWaiverAllowed, formatter);
			schdData = receiptService.setErrorToFSD(schdData, "90257", parm0, parm1, apiFeeCode);
			return fd;
		}

		FinODPenaltyRate finODPenaltyRate = schdData.getFinODPenaltyRate();
		if (finODPenaltyRate == null) {
			finODPenaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(fm.getFinID(), "_AView");
		}

		if (CollectionUtils.isNotEmpty(allocationList)) {

			for (ReceiptAllocationDetail allocate : allocationList) {
				if (allocate.getAllocationType() == RepayConstants.ALLOCATION_ODC) {
					if (fm.istDSApplicable() && finODPenaltyRate.isoDTDSReq()
							&& schdData.getFinFeeDetailList().get(0).getFeeTypeCode() == allocate.getFeeTypeCode()) {
						eventActualFee = allocate.getPaidAmount();
						isFee = true;
						break;
					}
				}

				if (allocate.getAllocationType() == RepayConstants.ALLOCATION_FEE) {
					if (fm.istDSApplicable()
							&& schdData.getFinFeeDetailList().get(0).getFeeTypeCode() == allocate.getFeeTypeCode()) {
						eventActualFee = allocate.getPaidAmount();
						isFee = true;
						break;
					}

				}
			}
		}

		apiActualFee = fsi.getFinFeeDetails().get(0).getActualAmount();
		if (isFee) {
			if ((apiActualFee.compareTo(eventActualFee) != 0)) {
				parm0 = "Fee Amount";
				parm1 = PennantApplicationUtil.amountFormate(eventActualFee, formatter);
				schdData = receiptService.setErrorToFSD(schdData, "90258", parm0, parm1, apiFeeCode);
				return fd;
			}
			if ((apiActualFee.subtract(apiPaidFee).subtract(apiWaived)).compareTo(BigDecimal.ZERO) != 0) {
				parm0 = "Fee Amount - Fee Waived";
				parm1 = "Fee Paid";
				schdData = receiptService.setErrorToFSD(schdData, "90258", parm0, parm1, apiFeeCode);
				return fd;
			}
		} else {
			// API Actual Amount <> EVENT Actual Amount
			if ((apiActualFee.compareTo(eventActualFee) != 0)) {
				parm0 = "Fee Amount";
				parm1 = PennantApplicationUtil.amountFormate(eventActualFee, formatter);
				schdData = receiptService.setErrorToFSD(schdData, "90258", parm0, parm1, apiFeeCode);
				return fd;
			}

			// Actual Amount - Paid - Waived <> 0
			if ((apiActualFee.subtract(apiPaidFee).subtract(apiWaived)).compareTo(BigDecimal.ZERO) != 0) {
				parm0 = "Fee Amount - Fee Waived";
				parm1 = "Fee Paid";
				schdData = receiptService.setErrorToFSD(schdData, "90258", parm0, parm1, apiFeeCode);
				return fd;
			}
		}

		return fd;
	}

	private FinanceDetail doProcessReceipt(FinReceiptData receiptData, String receiptPurpose) throws Exception {
		logger.debug(Literal.ENTERING);
		try {

			FinanceDetail fd = receiptData.getFinanceDetail();
			FinReceiptHeader rch = receiptData.getReceiptHeader();
			FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
			FinScheduleData schdData = fd.getFinScheduleData();
			FinServiceInstruction fsi = schdData.getFinServiceInstruction();
			FinanceMain fm = schdData.getFinanceMain();

			Date appDate = SysParamUtil.getAppDate();
			Date sysDate = DateUtility.getSysDate();

			int receiptPurposeCtg = receiptCalculator.setReceiptCategory(receiptPurpose);
			receiptData.setTotalPastDues(receiptCalculator.getTotalNetPastDue(receiptData));

			if (receiptPurposeCtg == 2) {
				rch.getReceiptDetails().clear();
				receiptService.createXcessRCD(receiptData);
			}

			BigDecimal amount = rch.getReceiptAmount().subtract(receiptData.getExcessAvailable());

			if (receiptData.getTotalPastDues().compareTo(amount) >= 0) {
				rcd.setDueAmount(amount);
				receiptData.setTotalPastDues(receiptData.getTotalPastDues().subtract(amount));
			} else {
				rcd.setDueAmount(receiptData.getTotalPastDues());
				receiptData.setTotalPastDues(BigDecimal.ZERO);
			}

			if (receiptPurposeCtg == 2) {
				rch.getReceiptDetails().add(rcd);
			}

			if (fsi.isReceiptUpload() && APIConstants.REQTYPE_POST.equals(fsi.getReqType())
					&& receiptService.dedupCheckRequest(rch, receiptPurpose)) {
				long rchID = receiptService.CheckDedupSP(rch, receiptPurpose);

				if (rchID != 0) {
					finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(rchID, RepayConstants.PAYSTATUS_REALIZED,
							rch.getRealizationDate());
					finReceiptDetailDAO.updateReceiptStatusByReceiptId(rchID, RepayConstants.PAYSTATUS_REALIZED);
					WSReturnStatus returnStatus = APIErrorHandlerService.getSuccessStatus();
					receiptService.setErrorToFSD(schdData, returnStatus.getReturnCode(), returnStatus.getReturnText());
					return fd;
				}
			}

			if (APIConstants.FINSOURCE_ID_API.equalsIgnoreCase(receiptData.getSourceId())) {
				if (CollectionUtils.isNotEmpty(rch.getAllocations())) {
					fd.getFinScheduleData().setReceiptAllocationList(rch.getAllocations());
				}
			}

			if (APIConstants.REQTYPE_POST.equals(fsi.getReqType())) {
				// FIXME: PV. IS IT REQUIRED HERE? VALIDATION AL;READY DONE IN
				// RECEIPT SERVICE.
				String receiptMode = fsi.getPaymentMode();

				if (RepayConstants.RECEIPTMODE_ONLINE.equals(receiptMode)) {
					receiptMode = fsi.getSubReceiptMode();
				}

				if (!RepayConstants.RECEIPTMODE_CASH.equals(receiptMode)
						&& !RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode)) {
					long fundingAccount = fsi.getReceiptDetail().getFundingAc();
					fsi.setFundingAc(fundingAccount);
					int count = finTypePartnerBankService.getPartnerBankCount(fm.getFinType(), receiptMode,
							AccountConstants.PARTNERSBANK_RECEIPTS, fundingAccount);
					if (count <= 0) {
						schdData = receiptService.setErrorToFSD(schdData, "90263", null);
						return fd;
					}

					// fetch partner bank details
					PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
					if (partnerBank != null) {
						rcd.setPartnerBankAc(partnerBank.getAccountNo());
						rcd.setPartnerBankAcType(partnerBank.getAcType());
					}
				}

				int version = 0;
				// Receipt upload process
				if (fsi.isReceiptdetailExits()) {
					FinReceiptData oldReceiptData = this.receiptService.getFinReceiptDataById(fsi.getFinReference(),
							AccountingEvent.REPAY, FinServiceEvent.RECEIPT, FinanceConstants.REALIZATION_MAKER);
					receiptData = oldReceiptData;

					version = fd.getFinScheduleData().getFinanceMain().getVersion();
					fm.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					fm.setRecordType("");
					schdData.setSchduleGenerated(true);
					receiptData.getReceiptHeader().setRealizationDate(fsi.getRealizationDate());
				} else {
					// Set Version value
					version = fm.getVersion();
					fm.setVersion(version + 1);
					fm.setRecordType("");
					schdData.setSchduleGenerated(true);
				}

				// Save the Schedule details
				AuditHeader auditHeader = getAuditHeader(receiptData, PennantConstants.TRAN_WF);

				// ### ticket id:124998,
				// setting to temp table
				if (fsi.isReceiptUpload() && receiptPurposeCtg != 0 && StringUtils.equals(fsi.getStatus(), "A")
						&& (StringUtils.equals(fsi.getPaymentMode(), "CHEQUE")
								|| StringUtils.equals(fsi.getPaymentMode(), "DD"))) {

					WorkFlowDetails workFlowDetails = null;
					String roleCode = FinanceConstants.DEPOSIT_APPROVER;
					String nextRolecode = FinanceConstants.REALIZATION_MAKER;
					String taskid = null;
					String nextTaskId = null;
					long workFlowId = 0;

					String finEvent = FinServiceEvent.RECEIPT;
					FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
							fm.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);

					if (financeWorkFlow != null) {
						workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
						if (workFlowDetails != null) {
							workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
							taskid = workFlow.getUserTaskId(roleCode);
							workFlowId = workFlowDetails.getWorkFlowId();
							nextTaskId = workFlow.getUserTaskId(nextRolecode);
						}

						fm.setWorkflowId(workFlowId);
						fm.setTaskId(taskid);
						fm.setRoleCode(roleCode);
						fm.setNextRoleCode(nextRolecode);
						fm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						fm.setNextTaskId(nextTaskId + ";");
						fm.setNewRecord(true);
						fm.setVersion(version + 1);
						fm.setRcdMaintainSts(FinServiceEvent.RECEIPT);
						fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						fm.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);

						String eventCode = null;
						if (receiptPurposeCtg == 2) {
							eventCode = AccountingEvent.EARLYSTL;
						} else {
							eventCode = AccountingEvent.EARLYPAY;
						}
					}

					fd.getFinScheduleData().setFinanceMain(fm);
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					auditHeader.setApiHeader(reqHeaderDetails);

					auditHeader = receiptService.saveOrUpdate(auditHeader);

				} else {

					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					auditHeader.setApiHeader(reqHeaderDetails);
					BigDecimal earlyPayAmount = receiptData.getRemBal();
					String recalType = rch.getEffectSchdMethod();
					fm.setReceiptPurpose(receiptPurpose);
					if (receiptPurposeCtg == 1) {
						schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData, rch.getValueDate(), null,
								earlyPayAmount, recalType);
						receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
					}

					Cloner cloner = new Cloner();
					FinReceiptData tempReceiptData = cloner.deepClone(receiptData);
					fd.setFinScheduleData(schdData);
					receiptData.getReceiptHeader().setValueDate(rch.getValueDate());

					receiptData.setDueAdjusted(true);
					if (receiptPurposeCtg == 2) {
						boolean duesAdjusted = receiptService
								.checkDueAdjusted(receiptData.getReceiptHeader().getAllocations(), receiptData);
						if (!duesAdjusted) {
							receiptData = receiptService.adjustToExcess(receiptData);
							receiptData.setDueAdjusted(false);
						}
					}
					if (receiptData.isDueAdjusted()) {
						for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
							allocate.setPaidAvailable(allocate.getPaidAmount());
							allocate.setWaivedAvailable(allocate.getWaivedAmount());
							allocate.setPaidAmount(BigDecimal.ZERO);
							allocate.setPaidGST(BigDecimal.ZERO);
							allocate.setTotalPaid(BigDecimal.ZERO);
							allocate.setBalance(allocate.getTotalDue());
							allocate.setWaivedAmount(BigDecimal.ZERO);
							allocate.setWaivedGST(BigDecimal.ZERO);
							allocate.setTdsPaid(BigDecimal.ZERO);
							allocate.setTdsWaived(BigDecimal.ZERO);
						}

						receiptData.setBuildProcess("R");
						receiptData = receiptCalculator.initiateReceipt(receiptData, false);

					}
					receiptData.getFinanceDetail().getFinScheduleData().setFinanceScheduleDetails(
							tempReceiptData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());

					if (fsi.isNonStp()) {
						auditHeader = receiptService.doApprove(auditHeader);
					} else {

						WorkFlowDetails workFlowDetails = null;
						String roleCode = fsi.getProcessStage();
						String nextRolecode = fsi.getProcessStage();

						String taskid = null;
						String nextTaskId = null;
						long workFlowId = 0;
						String finEvent = FinServiceEvent.RECEIPT;

						FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
								fm.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);

						if (financeWorkFlow == null) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90339"));
							return response;
						}

						workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
						String[] workFlowRoles = workFlowDetails.getWorkFlowRoles().split(";");

						if (StringUtils.isBlank(fsi.getProcessStage())) {
							roleCode = workFlowDetails.getFirstTaskOwner();
							nextRolecode = roleCode;
						}
						boolean roleNotFound = false;
						for (String workFlowRole : workFlowRoles) {
							if (StringUtils.equals(workFlowRole, roleCode)) {
								roleNotFound = true;
								break;
							}
						}

						if (!roleNotFound) {
							FinanceDetail response = new FinanceDetail();
							doEmptyResponseObject(response);
							String[] valueParm = new String[1];
							valueParm[0] = roleCode;
							response.setReturnStatus(APIErrorHandlerService.getFailedStatus("API004", valueParm));
							return response;
						}

						if (fsi.getPaymentMode().equals("CASH")) {
							if (!roleCode.equals("RECEIPT_MAKER") && !roleCode.equals("REALIZATION_APPROVER")) {
								FinanceDetail response = new FinanceDetail();
								doEmptyResponseObject(response);
								String[] valueParm = new String[1];
								valueParm[0] = "CASH PAYMENT MODE";
								response.setReturnStatus(APIErrorHandlerService.getFailedStatus("30556", valueParm));
								return response;
							}
						}

						if (workFlowDetails != null) {
							workFlow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
							taskid = workFlow.getUserTaskId(roleCode);
							workFlowId = workFlowDetails.getWorkFlowId();
							nextTaskId = workFlow.getUserTaskId(nextRolecode);
						}

						fm.setWorkflowId(workFlowId);
						fm.setTaskId(taskid);
						fm.setRoleCode(roleCode);
						fm.setNextRoleCode(nextRolecode);
						fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						fm.setNextTaskId(nextTaskId + ";");
						fm.setNewRecord(true);
						fm.setVersion(1);
						fm.setRcdMaintainSts(FinServiceEvent.RECEIPT);
						fm.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						fm.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);

						receiptData.getReceiptHeader();
						receiptData.getReceiptHeader().setTaskId(taskid);
						receiptData.getReceiptHeader().setNextTaskId(nextTaskId + ";");
						receiptData.getReceiptHeader().setRoleCode(roleCode);
						receiptData.getReceiptHeader().setNextRoleCode(nextRolecode);
						receiptData.getReceiptHeader().setWorkflowId(workFlowId);
						receiptData.getReceiptHeader().setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
						receiptData.getReceiptHeader().setFinType(fm.getFinType());

						auditHeader = receiptService.saveOrUpdate(auditHeader);
					}
				}

				if (auditHeader.getErrorMessage() != null) {
					for (ErrorDetail auditErrorDetail : auditHeader.getErrorMessage()) {
						receiptService.setErrorToFSD(schdData, auditErrorDetail.getCode(), auditErrorDetail.getError());
						return fd;
					}
				}

				receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
				// FIXME: PV re-look at it
				fd = getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());
				rch = receiptData.getReceiptHeader();
				fd.getFinScheduleData().getFinServiceInstruction().setReceiptId(rch.getReceiptID());

				List<FinServiceInstruction> finServInstList = new ArrayList<>();
				for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
					for (FinRepayHeader rpyHeader : recDtl.getRepayHeaders()) {
						FinServiceInstruction finServInst = new FinServiceInstruction();
						finServInst.setFinID(fm.getFinID());
						finServInst.setFinReference(fm.getFinReference());
						finServInst.setFinEvent(rpyHeader.getFinEvent());
						finServInst.setAmount(rpyHeader.getRepayAmount());
						finServInst.setAppDate(appDate);
						finServInst.setSystemDate(sysDate);
						finServInst.setMaker(auditHeader.getAuditUsrId());
						finServInst.setMakerAppDate(appDate);
						finServInst.setMakerSysDate(sysDate);
						finServInst.setChecker(auditHeader.getAuditUsrId());
						finServInst.setCheckerAppDate(appDate);
						finServInst.setCheckerSysDate(sysDate);
						finServInst.setReference(String.valueOf(rch.getReceiptID()));
						finServInstList.add(finServInst);
					}
				}

				// set receipt id in data
				if (fsi.isReceiptUpload() && !fsi.isReceiptResponse()) {
					this.receiptUploadDetailDAO.updateReceiptId(fsi.getUploadDetailId(), rcd.getReceiptID());
				}

				// set receipt id response job
				if (fsi.isReceiptUpload() && fsi.isReceiptResponse()) {
					this.receiptResponseDetailDAO.updateReceiptResponseId(fsi.getRootId(), rcd.getReceiptID());
				}

			} else {

				BigDecimal earlyPayAmount = receiptData.getRemBal();
				String recalType = rch.getEffectSchdMethod();
				schdData.getFinanceMain().setReceiptPurpose(receiptPurpose);
				if (receiptPurposeCtg == 1) {
					schdData = ScheduleCalculator.recalEarlyPaySchedule(schdData, rch.getValueDate(), null,
							earlyPayAmount, recalType);
					receiptData = receiptCalculator.addPartPaymentAlloc(receiptData);
				}
				receiptData.getFinanceDetail().setFinScheduleData(schdData);

				for (ReceiptAllocationDetail allocate : receiptData.getReceiptHeader().getAllocations()) {
					allocate.setPaidAvailable(allocate.getPaidAmount());
					allocate.setFeeTypeCode(allocate.getFeeTypeCode());
					allocate.setWaivedAvailable(allocate.getWaivedAmount());
					allocate.setPaidAmount(BigDecimal.ZERO);
					allocate.setPaidGST(BigDecimal.ZERO);
					allocate.setTotalPaid(BigDecimal.ZERO);
					allocate.setBalance(allocate.getTotalDue());
					allocate.setWaivedAmount(BigDecimal.ZERO);
					allocate.setWaivedGST(BigDecimal.ZERO);
				}
				receiptData.setBuildProcess("R");
				receiptData = receiptCalculator.initiateReceipt(receiptData, false);
				fd = getServiceInstResponse(receiptData.getFinanceDetail().getFinScheduleData());
				FinanceSummary summary = fd.getFinScheduleData().getFinanceSummary();
				summary.setFinODDetail(rch.getFinODDetails());
				fd.getFinScheduleData().setFinODDetails(rch.getFinODDetails());
			}

			logger.debug(Literal.LEAVING);
			return fd;
		} catch (AppException ex) {
			logger.error("AppException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	private Map<String, String> validateRepayAmount(FinScheduleData schdData, FinServiceInstruction fsi,
			BigDecimal totReceiptAmt) {

		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("ReturnCode", "");
		returnMap.put("ReturnText", "");
		returnMap.put("partPaidAmt", "0");

		// validate repayment amount
		BigDecimal pftBalance = BigDecimal.ZERO;
		BigDecimal priBalance = BigDecimal.ZERO;
		BigDecimal schFeeBal = BigDecimal.ZERO;
		BigDecimal tdsReturns = BigDecimal.ZERO;

		Date curBussniessDate = fsi.getReceiptDetail().getReceivedDate();
		boolean partAccrualReq = true;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (totReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
			if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSTLENQ)
					|| StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSETTLE)) {
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90330");
				returnMap.put("ReturnCode", status.getReturnCode());
				returnMap.put("ReturnText", status.getReturnText());
			} else if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYRPY)
					|| StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.SCHDRPY)) {
				WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90331");
				returnMap.put("ReturnCode", status.getReturnCode());
				returnMap.put("ReturnText", status.getReturnText());
			}
			if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYRPY)) {
				boolean isInSubvention = receiptService.isInSubVention(fm, curBussniessDate);
				if (isInSubvention) {
					String[] valueParm = new String[1];
					valueParm[0] = "Not allowed to do Partial Settlement in Subvention Period.";
					WSReturnStatus status = APIErrorHandlerService.getFailedStatus("30550", valueParm);
					returnMap.put("ReturnCode", status.getReturnCode());
					returnMap.put("ReturnText", status.getReturnText());
				}
			}
		}

		if (!StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.SCHDRPY)) {
			FinanceMain financeMain = fm;
			BigDecimal tdsMultiplier = BigDecimal.ONE;
			if (TDSCalculator.isTDSApplicable(financeMain)) {
				BigDecimal tdsPerc = new BigDecimal(
						SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
					tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
							RoundingMode.HALF_DOWN);
				}
			}

			List<FinanceScheduleDetail> tempScheduleDetails = schdData.getFinanceScheduleDetails();
			for (int i = 0; i < tempScheduleDetails.size(); i++) {
				curSchd = tempScheduleDetails.get(i);
				if (i != 0) {
					prvSchd = tempScheduleDetails.get(i - 1);
				}
				Date schdDate = curSchd.getSchDate();

				if (curSchd.isTDSApplicable()) {
					BigDecimal tdsPerc = new BigDecimal(
							SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
					if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
						tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
								RoundingMode.HALF_DOWN);
					}
				}

				schFeeBal = schFeeBal.add(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));

				if (DateUtility.compare(schdDate, curBussniessDate) < 0) {
					pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
					priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					if (curSchd.isTDSApplicable()) {
						BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
						BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						tdsReturns = tdsReturns.add(pft.subtract(actualPft));
						tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
								financeMain.getRoundingTarget());
					}
				} else if (DateUtility.compare(curBussniessDate, schdDate) == 0) {

					if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSETTLE)
							|| StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSTLENQ)) {

						BigDecimal remPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid());
						pftBalance = pftBalance.add(curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid()));
						if (prvSchd != null) {
							remPft = remPft.add(prvSchd.getProfitBalance());
							pftBalance = pftBalance.add(prvSchd.getProfitBalance());
						}

						priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance()))
								.subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());

						if (curSchd.isTDSApplicable()) {
							BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(remPft.subtract(actualPft));
							tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
						}
						partAccrualReq = false;
					} else {
						pftBalance = pftBalance.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
						priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
						if (curSchd.isTDSApplicable()) {
							BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							tdsReturns = tdsReturns.add(pft.subtract(actualPft));
							tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
						}
					}
				} else {
					if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSETTLE)
							|| StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSTLENQ)) {
						if (partAccrualReq && prvSchd != null) {
							partAccrualReq = false;
							BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), curBussniessDate,
									curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(),
									prvSchd.getCalculatedRate());
							accruedPft = accruedPft.add(prvSchd.getProfitFraction());
							accruedPft = CalculationUtil.roundAmount(accruedPft, financeMain.getCalRoundingMode(),
									financeMain.getRoundingTarget());
							pftBalance = pftBalance.add(accruedPft).add(prvSchd.getProfitBalance());

							priBalance = priBalance.add(prvSchd.getClosingBalance());

							if (curSchd.isTDSApplicable()) {
								BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance()))
										.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
								tdsReturns = tdsReturns
										.add(accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft));
								tdsReturns = CalculationUtil.roundAmount(tdsReturns, financeMain.getCalRoundingMode(),
										financeMain.getRoundingTarget());
							}
						} else {
							priBalance = priBalance.add(curSchd.getDisbAmount());
						}
					} else {
						break;
					}
				}
			}

			// Fetching Actual Late Payments based on Value date passing
			BigDecimal latePayPftBal = BigDecimal.ZERO;
			BigDecimal penaltyBal = BigDecimal.ZERO;
			List<FinODDetails> overdueList = null;
			if (DateUtility.compare(curBussniessDate, SysParamUtil.getAppDate()) == 0) {
				overdueList = finODDetailsDAO.getFinODDByFinRef(finID, null);
			} else {
				// Calculate overdue Penalties
				overdueList = receiptService.getValueDatePenalties(schdData, totReceiptAmt, curBussniessDate, null,
						true);
			}

			// Calculating Actual Sum of Penalty Amount & Late Pay Interest
			if (overdueList != null && !overdueList.isEmpty()) {
				for (int i = 0; i < overdueList.size(); i++) {
					FinODDetails finODDetail = overdueList.get(i);
					if (finODDetail.getFinODSchdDate().compareTo(curBussniessDate) > 0) {
						continue;
					}
					latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
					penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
				}
			}

			List<ManualAdvise> manualAdviseFees = manualAdviseDAO.getManualAdviseByRef(finID,
					FinanceConstants.MANUAL_ADVISE_RECEIVABLE, " ");
			BigDecimal bounceCharge = BigDecimal.ZERO;
			if (manualAdviseFees != null && !manualAdviseFees.isEmpty()) {
				for (ManualAdvise advisedFees : manualAdviseFees) {
					bounceCharge = bounceCharge
							.add(advisedFees.getAdviseAmount().subtract(advisedFees.getPaidAmount()));
				}
			}
			BigDecimal remBal = priBalance.add(pftBalance).add(schFeeBal).add(latePayPftBal).add(penaltyBal)
					.add(bounceCharge).subtract(tdsReturns);
			BigDecimal partialPaidAmt = totReceiptAmt.subtract(
					priBalance.add(pftBalance).add(schFeeBal).add(latePayPftBal).add(penaltyBal).subtract(tdsReturns));
			returnMap.put("partPaidAmt", String.valueOf(partialPaidAmt));

			// calculate Remaining balance after
			if (StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSTLENQ)
					|| StringUtils.equals(fsi.getModuleDefiner(), FinServiceEvent.EARLYSETTLE)) {
				if (totReceiptAmt.compareTo(remBal) < 0) {
					WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90330");
					returnMap.put("ReturnCode", status.getReturnCode());
					returnMap.put("ReturnText", status.getReturnText());
				}
			} /*
				 * else if (StringUtils.equals(finServiceInst.getModuleDefiner(),
				 * FinanceConstants.FINSER_EVENT_EARLYRPY)) { if (totReceiptAmt.compareTo(remBal) <= 0) { WSReturnStatus
				 * status = APIErrorHandlerService.getFailedStatus("90332"); returnMap.put("ReturnCode",
				 * status.getReturnCode()); returnMap.put("ReturnText", status.getReturnText()); } }
				 */
		}
		return returnMap;
	}

	private BigDecimal getTotalFeePaid(List<FinFeeDetail> finFeeDetailList) {
		BigDecimal totFeeAmount = BigDecimal.ZERO;
		if (finFeeDetailList != null) {
			for (FinFeeDetail feeDetail : finFeeDetailList) {
				if (!feeDetail.isOriginationFee()) {
					totFeeAmount = totFeeAmount.add(feeDetail.getPaidAmount());
				}
			}
		}
		return totFeeAmount;
	}

	/**
	 * Method for updateLoanBasicDetails
	 * 
	 * @param financeMain
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateLoanBasicDetails(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		// update the Finance Basic Details
		int count = financeMainService.updateFinanceBasicDetails(financeMain);
		if (count > 0) {
			logger.debug(Literal.LEAVING);
			return APIErrorHandlerService.getSuccessStatus();
		} else {
			return APIErrorHandlerService.getFailedStatus();
		}

	}

	/**
	 * Method for updateLoanPenaltyDetails
	 * 
	 * @param finODPenaltyRate
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateLoanPenaltyDetails(FinODPenaltyRate finODPenaltyRate) {
		logger.debug(Literal.ENTERING);
		try {
			// save the OdPenaltyDetais
			FinODPenaltyRate oldFinODPenaltyRate = finODPenaltyRateDAO
					.getFinODPenaltyRateByRef(finODPenaltyRate.getFinID(), "");
			finODPenaltyRateDAO.saveLog(oldFinODPenaltyRate, "_Log");
			finODPenaltyRateDAO.update(finODPenaltyRate, "");
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();

	}

	public FinanceDetail doChangeScheduleMethod(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			logger.debug(Literal.LEAVING);
			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();

		/*
		 * // tempStartDate List<FinanceScheduleDetail> financeScheduleDetails = null; financeScheduleDetails =
		 * financeDetail.getFinScheduleData().getFinanceScheduleDetails(); if (financeScheduleDetails != null) { for
		 * (int i = 0; i < financeScheduleDetails.size(); i++) { FinanceScheduleDetail curSchd =
		 * financeScheduleDetails.get(i); if (curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() &&
		 * curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) { if
		 * (finServiceInst.getFromDate().compareTo(curSchd.getSchDate()) == 0) { break; } } } }
		 */

		FinanceMain fm = schdData.getFinanceMain();

		fsi.setFromDate(fsi.getFromDate());
		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(fm.getFinReference());

		fm.setEventFromDate(fsi.getFromDate());
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.CHGSCHDMETHOD);
		fm.setRecalSchdMethod(fsi.getSchdMethod());
		fm.setDevFinCalReq(false);
		fsi.setFinEvent(FinServiceEvent.CHGSCHDMETHOD);

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			// Call Schedule calculator for Rate change
			schdData = changeScheduleMethodService.doChangeScheduleMethod(schdData, fsi);
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;

	}

	public FinanceDetail doChangeGestationPeriod(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		AuditDetail auditDetail = doChangeGestationValidations(fd, fsi);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

				return fd;
			}
		}

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date appDate = SysParamUtil.getAppDate();
		int fddLockPeriod = financeType.getFddLockPeriod();

		if (fm.isAllowGrcPeriod() && !ImplementationConstants.APPLY_FDDLOCKPERIOD_AFTERGRACE) {
			fddLockPeriod = 0;
		}

		if (fm.isAlwFlexi()) {
			int oldGrcTerms = fm.getGraceTerms();
			int newGrcTerms = fsi.getGrcTerms();
			int numberOfTerms = newGrcTerms - oldGrcTerms;
			fm.setNumberOfTerms(fm.getNumberOfTerms() - numberOfTerms);
		}

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.CHGGRCEND);

		fsi.setFinID(finID);
		fsi.setFinReference(finReference);

		fm.setGraceTerms(fsi.getGrcTerms());

		// GraceEndDate Calculation
		fm.setCalGrcTerms(fm.getGraceTerms());
		List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(fm.getGrcPftFrq(), fm.getGraceTerms(),
				fm.getFinStartDate(), HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
				.getScheduleList();

		Date geDate = null;
		if (scheduleDateList != null) {
			Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
			geDate = DateUtil.getDatePart(calendar.getTime());
		}

		if (geDate.before(DateUtility.addDays(appDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = "CalGrcEndDate: " + geDate;
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetail ed : auditDetail.getErrorDetails()) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

				return fd;
			}
		}

		fm.setEventFromDate(fm.getGrcPeriodEndDate());
		fm.setGrcPeriodEndDate(geDate);

		fm.setNextRepayDate(FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fm.getGrcPeriodEndDate(),
				HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

		if ((fddLockPeriod != 0) && !ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
			fm.setNextRepayRvwDate(FrequencyUtil.getNextDate(fm.getRepayRvwFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, 0).getNextFrequencyDate());
		} else {
			fm.setNextRepayRvwDate(FrequencyUtil.getNextDate(fm.getRepayRvwFrq(), 1, fm.getGrcPeriodEndDate(),
					HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());
		}

		fm.setNextRepayPftDate(FrequencyUtil.getNextDate(fm.getRepayPftFrq(), 1, fm.getGrcPeriodEndDate(),
				HolidayHandlerTypes.MOVE_NONE, false, fddLockPeriod).getNextFrequencyDate());

		if (!fm.isAlwFlexi()) {
			List<Calendar> dateList = null;
			dateList = FrequencyUtil.getNextDate(fm.getRepayFrq(), fm.getNumberOfTerms(), fm.getNextRepayDate(),
					HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();
			if (dateList != null) {
				Calendar calendar = dateList.get(dateList.size() - 1);
				fm.setMaturityDate(calendar.getTime());
			}
		}

		fm.setDevFinCalReq(false);
		fsi.setFinEvent(FinServiceEvent.CHGGRCEND);

		try {
			executeFeeCharges(fd, fsi, eventCode);

			if (fd.getFinScheduleData().getErrorDetails() != null) {
				for (ErrorDetail ed : fd.getFinScheduleData().getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			// For HybridFlexi
			fm.setChgDropLineSchd(true);

			// Call Schedule calculator for graceEndDate
			fd.setFinScheduleData(ScheduleCalculator.changeGraceEnd(fd.getFinScheduleData()));
			if (schdData.getErrorDetails() != null) {
				for (ErrorDetail ed : schdData.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}
			fd.setFinScheduleData(schdData);
			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;

	}

	private AuditDetail doChangeGestationValidations(FinanceDetail financeDetail,
			FinServiceInstruction finServiceInstruction) {

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		AuditDetail auditDetail = new AuditDetail();

		String param = "Grace";
		boolean alwFlexi = false;
		if (financeDetail.getFinScheduleData().getFinanceMain().isAlwFlexi()) {
			alwFlexi = true;
			param = "PureFlexi";
		}

		if (!alwFlexi) {
			String[] valueParm = new String[2];
			valueParm[0] = "Change Gestation";
			valueParm[1] = "LoanType: " + financeType.getFinType();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90329", valueParm)));
			return auditDetail;
		}

		if (financeType.getMinGrcTerms() > 0 && financeType.getMaxGrcTerms() > 0) {
			if (finServiceInstruction.getGrcTerms() < financeType.getMinGrcTerms()
					|| finServiceInstruction.getGrcTerms() > financeType.getMaxGrcTerms()) {
				String[] valueParm = new String[3];
				valueParm[0] = param + "Terms";
				valueParm[1] = String.valueOf(financeType.getMinGrcTerms());
				valueParm[2] = String.valueOf(financeType.getMaxGrcTerms());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90272", valueParm)));
				return auditDetail;
			}
		}
		Date curBussDate = DateUtility.getAppDate();
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtility.addDays(curBussDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = param + "EndDate: "
					+ financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtility.addDays(curBussDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = param + "EndDate: "
					+ financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		Date validFrom = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
		List<FinanceScheduleDetail> scheduelist = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		for (int i = 1; i < scheduelist.size(); i++) {

			FinanceScheduleDetail curSchd = scheduelist.get(i);
			if (curSchd.getSchDate().compareTo(DateUtility.getAppDate()) < 0) {
				validFrom = DateUtility.getAppDate();
				continue;
			}
			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				validFrom = curSchd.getSchDate();
				continue;
			}

			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {

				validFrom = curSchd.getSchDate();
				continue;
			}
		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate().compareTo(validFrom) <= 0) {
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41019", null)));
		}

		return auditDetail;

	}

	/**
	 * Method for prepare finance detail response object
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinanceDetail getServiceInstResponse(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		FinanceDetail response = new FinanceDetail();
		response.setFinReference(finScheduleData.getFinReference());
		response.setFinScheduleData(finScheduleData);

		// Finance Summary details i.e Basic Calculator details
		FinanceSummary summaryDetail = getFinanceSummary(finScheduleData);
		response.getFinScheduleData().setFinanceSummary(summaryDetail);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		if (finScheduleData.getFinFeeDetailList() != null) {
			List<FinFeeDetail> srvFeeList = new ArrayList<FinFeeDetail>();
			for (FinFeeDetail feeDetail : finScheduleData.getFinFeeDetailList()) {
				if (!feeDetail.isOriginationFee()) {
					srvFeeList.add(feeDetail);
				}
			}
			finScheduleData.setFinFeeDetailList(srvFeeList);
			// summaryDetail.setFeeChargeAmt(summaryDetail.getFeeChargeAmt().add(totFeeAmount));
		}

		// Resetting Maturity Terms & Summary details rendering in case of
		// Reduce maturity cases
		resetScheduleDetail(finScheduleData);

		finScheduleData.setFinanceMain(null);
		finScheduleData.setDisbursementDetails(null);
		finScheduleData.setFinReference(null);
		finScheduleData.setRepayInstructions(null);
		finScheduleData.setRateInstruction(null);
		finScheduleData.setStepPolicyDetails(null);
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setApiPlanEMIHDates(null);
		finScheduleData.setApiplanEMIHmonths(null);

		logger.debug(Literal.ENTERING);
		return response;
	}

	private FinanceSummary getFinanceSummary(FinScheduleData finScheduleData) {
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(finScheduleData);
		return getFinanceSummary(financeDetail);
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinReceiptData finReceiptData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, finReceiptData);
		return new AuditHeader(finReceiptData.getFinReference(), null, null, null, auditDetail,
				finReceiptData.getReceiptHeader().getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * 
	 * @param financeDetail
	 * @param finServiceInst
	 * @return
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	public FinanceDetail getResponse(FinanceDetail financeDetail, FinServiceInstruction finServiceInst)
			throws JaxenException, InterfaceException {
		logger.debug(Literal.ENTERING);

		// fees calculation
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinServiceInstruction> finInstList = new ArrayList<>();
		finInstList.add(finServiceInst);
		finScheduleData.setFinServiceInstructions(finInstList);
		if (!finScheduleData.getFinFeeDetailList().isEmpty()) {
			finScheduleData = FeeScheduleCalculator.feeSchdBuild(finScheduleData);
		}

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			financeDetail.getFinScheduleData().setSchduleGenerated(true);
			financeDetail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			aFinanceDetail = prepareInstructionObject(aFinanceDetail);

			// get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = financeDetailService.doApprove(auditHeader, finServiceInst.isWif());
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}
			financeDetail = getServiceInstResponse(financeDetail.getFinScheduleData());
		} else {
			financeDetail = getServiceInstResponse(financeDetail.getFinScheduleData());
		}
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for prepare fianceDetail object.<br>
	 * - Nullify the unnecessary data
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	private FinanceDetail prepareInstructionObject(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		finScheduleData.getFinanceMain().setRecordType("");

		finScheduleData.setStepPolicyDetails(new ArrayList<FinanceStepPolicyDetail>(1));
		finScheduleData.setFinODPenaltyRate(null);
		finScheduleData.setFeeRules(new ArrayList<FeeRule>());

		aFinanceDetail.setTatDetail(null);
		aFinanceDetail.setFinanceCheckList(new ArrayList<FinanceCheckListReference>(1));
		aFinanceDetail.setCheckList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setAggrementList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setEligibilityRuleList(new ArrayList<FinanceReferenceDetail>(1));
		aFinanceDetail.setFinElgRuleList(new ArrayList<FinanceEligibilityDetail>(1));
		aFinanceDetail.setGurantorsDetailList(new ArrayList<GuarantorDetail>(1));
		aFinanceDetail.setJointAccountDetailList(new ArrayList<JointAccountDetail>(1));
		aFinanceDetail.setFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setApprovedFinanceDeviations(new ArrayList<FinanceDeviations>());
		aFinanceDetail.setFinanceCollaterals(new ArrayList<FinCollaterals>(1));
		aFinanceDetail.setCollateralAssignmentList(new ArrayList<CollateralAssignment>(1));
		aFinanceDetail.setFinAssetTypesList(new ArrayList<FinAssetTypes>(1));
		aFinanceDetail.setExtendedFieldRenderList(new ArrayList<ExtendedFieldRender>(1));
		finScheduleData.setVasRecordingList(new ArrayList<VASRecording>(1));
		aFinanceDetail.setCovenantTypeList(null);
		aFinanceDetail.setMandate(null);
		aFinanceDetail.setFinFlagsDetails(null);

		logger.debug(Literal.LEAVING);
		return aFinanceDetail;
	}

	public FinanceDetail getFinanceDetails(FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;

		long finID = fsi.getFinID();
		if (!fsi.isWif()) {
			fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");
		} else {
			fd = financeDetailService.getWIFFinance(finID, false, null);
		}

		/*
		 * List<FinFeeDetail> finServicingFeeList = finFeeDetailService.getFinFeeDetailById(finReference, false,
		 * "_TView", eventCode); financeDetail.getFinScheduleData().setFinFeeDetailList( finServicingFeeList);
		 */

		List<FinFeeDetail> newList = new ArrayList<FinFeeDetail>();
		if (fd != null) {
			if (fd.getFinScheduleData().getFinFeeDetailList() != null) {
				for (FinFeeDetail feeDetail : fd.getFinScheduleData().getFinFeeDetailList()) {
					if (feeDetail.isOriginationFee()) {
						feeDetail.setOriginationFee(true);
						feeDetail.setRcdVisible(false);
						feeDetail.setRecordType(PennantConstants.RCD_UPD);
						feeDetail.setRecordStatus(PennantConstants.RECORD_TYPE_UPD);
						newList.add(feeDetail);
					}
				}
			}
			fd.getFinScheduleData().setFinFeeDetailList(newList);
			fd.setAccountingEventCode(eventCode);
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			fd.getFinScheduleData().getFinanceMain().setUserDetails(userDetails);
		}

		logger.debug(Literal.LEAVING);

		return fd;
	}

	/**
	 * 
	 * @param detail
	 */
	private void doEmptyResponseObject(FinanceDetail detail) {
		detail.setFinScheduleData(null);
		detail.setDocumentDetailsList(null);
		detail.setJointAccountDetailList(null);
		detail.setGurantorsDetailList(null);
		detail.setCollateralAssignmentList(null);
		detail.setReturnDataSetList(null);
	}

	/**
	 * Sort Schedule Details
	 * 
	 * @param schedules
	 * @return
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> schedules) {
		if (schedules != null && schedules.size() > 0) {
			Collections.sort(schedules, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return schedules;
	}

	public Date getFirstInstDate(List<FinanceScheduleDetail> schedules) {
		Date firstInstDate = null;
		for (FinanceScheduleDetail schedule : schedules) {
			BigDecimal repayAmt = schedule.getProfitSchd().add(schedule.getPrincipalSchd())
					.subtract(schedule.getPartialPaidAmt());

			// InstNumber issue with Partial Settlement before first installment
			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				firstInstDate = schedule.getSchDate();
				break;
			}
		}
		return firstInstDate;
	}

	public FinanceDetail doFeePayment(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		String finReference = fsi.getFinReference();
		String[] valueParm = null;

		WSReturnStatus returnStatus = null;
		Long finID = financeMainDAO.getFinID(finReference, TableType.TEMP_TAB);

		if (finID == null) {
			valueParm = new String[1];
			valueParm[0] = fsi.getFinReference();
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		FinanceDetail fd = null;
		if (returnStatus != null) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		returnStatus = validateReceiptData(fsi);
		if (returnStatus != null) {
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(returnStatus);
			return fd;
		}

		List<ErrorDetail> errorDetails = upfrontFeeValidations(fsi);
		if (errorDetails != null) {
			for (ErrorDetail ed : errorDetails) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
		}

		try {
			ErrorDetail ed = feeReceiptService.processFeePayment(fsi);
			if (ed != null) {
				fd = new FinanceDetail();
				doEmptyResponseObject(fd);
				fd.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
				return fd;
			}
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			fd.setReceiptId(fsi.getReceiptId());
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			APIErrorHandlerService.logUnhandledException(ex);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return fd;
		} catch (AppException appEx) {
			logger.error("AppException", appEx);
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus("9999", appEx.getMessage()));
			APIErrorHandlerService.logUnhandledException(appEx);
			return fd;
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			fd = new FinanceDetail();
			doEmptyResponseObject(fd);
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return fd;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private List<ErrorDetail> upfrontFeeValidations(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errorDetails = new ArrayList<>();
		if (StringUtils.isBlank(fsi.getExternalReference())) {
			return errorDetails;
		}
		FinanceType finType = financeTypeDAO.getFinanceTypeByFinType(fsi.getFinType());
		if (finType == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "finType " + fsi.getFinType() + " is invalid";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			return errorDetails;
		}

		BigDecimal feePaidAmount = BigDecimal.ZERO;
		List<String> processedFees = new ArrayList<>(fsi.getFinFeeDetails().size());
		for (FinFeeDetail finFeeDetail : fsi.getFinFeeDetails()) {
			// In case of req contain duplicate fees.
			String feeCode = StringUtils.trimToEmpty(finFeeDetail.getFeeTypeCode());
			if (processedFees.contains(feeCode.toLowerCase())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Fees : " + feeCode;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
			}
			processedFees.add(feeCode.toLowerCase());
			if (StringUtils.isNotBlank(finFeeDetail.getFeeScheduleMethod())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Fee Schedule Method";
				valueParm[1] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90269", valueParm)));
			}
			if (finFeeDetail.getActualAmount() == null) {
				finFeeDetail.setActualAmount(BigDecimal.ZERO);
			}
			if (finFeeDetail.getWaivedAmount() == null) {
				finFeeDetail.setWaivedAmount(BigDecimal.ZERO);
			}
			if (finFeeDetail.getPaidAmount() == null) {
				finFeeDetail.setPaidAmount(BigDecimal.ZERO);
			}

			// validate negative values
			if (finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) < 0
					|| finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) < 0
					|| finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finFeeDetail.getFeeTypeCode();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90259", valueParm)));
			}
			// validate actual amount and paid amount
			BigDecimal amount = finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount());
			if (finFeeDetail.getPaidAmount().compareTo(amount) != 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finFeeDetail.getFeeTypeCode() + " Paid amount must be  " + amount;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30550", valueParm)));
			}

			feePaidAmount = feePaidAmount.add(finFeeDetail.getPaidAmount());
		}
		if (errorDetails.size() > 0) {
			return errorDetails;
		}

		if (fsi.getAmount().compareTo(feePaidAmount) < 0) {
			String valueParm[] = new String[2];
			valueParm[0] = "amount : " + fsi.getAmount();
			valueParm[1] = "total fees paid : " + feePaidAmount;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
			return errorDetails;
		}

		boolean isOrigination = true;
		List<FinTypeFees> finTypeFeeDetail = financeDetailService.getFinTypeFees(finType.getFinType(),
				AccountingEvent.ADDDBSP, isOrigination, FinanceConstants.MODULEID_FINTYPE);
		if (CollectionUtils.isEmpty(finTypeFeeDetail)) {
			String[] valueParm = new String[1];
			valueParm[0] = fsi.getFinType();
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90245", valueParm)));
			return errorDetails;
		}
		fsi.setFinTypeFeeList(finTypeFeeDetail);

		List<FinFeeDetail> prvsFees = finFeeDetailService.getFinFeeDetailsByTran(fsi.getExternalReference(), false,
				TableType.MAIN_TAB.getSuffix());
		List<FinFeeDetail> feelist = new ArrayList<>();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		for (FinFeeDetail reqFee : fsi.getFinFeeDetails()) {
			boolean isFeeCodeFound = false;
			for (FinTypeFees finTypeFee : finTypeFeeDetail) {
				if (StringUtils.equals(reqFee.getFeeTypeCode(), finTypeFee.getFeeTypeCode())) {
					isFeeCodeFound = true;
					FinFeeDetail fee = getFeeByFeeType(prvsFees, finTypeFee.getFeeTypeID());
					if (fee == null) {
						// setting req data
						fee = new FinFeeDetail();
						fee.setNewRecord(true);
						fee.setFeeTypeID(finTypeFee.getFeeTypeID());
						fee.setOriginationFee(finTypeFee.isOriginationFee());
						fee.setFinEvent(finTypeFee.getFinEvent());
						fee.setFinEventDesc(finTypeFee.getFinEventDesc());
						fee.setFeeOrder(finTypeFee.getFeeOrder());
						fee.setAlwPreIncomization(finTypeFee.isAlwPreIncomization());
						fee.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
						fee.setCalculationType(finTypeFee.getCalculationType());
						fee.setRuleCode(finTypeFee.getRuleCode());
						fee.setFixedAmount(finTypeFee.getAmount());
						fee.setPercentage(finTypeFee.getPercentage());
						fee.setCalculateOn(finTypeFee.getCalculateOn());
						fee.setAlwDeviation(finTypeFee.isAlwDeviation());
						fee.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
						fee.setAlwModifyFee(finTypeFee.isAlwModifyFee());
						fee.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
						fee.setCalculatedAmount(finTypeFee.getAmount());
						fee.setTaxApplicable(finTypeFee.isTaxApplicable());
						fee.setTaxComponent(finTypeFee.getTaxComponent());
						fee.setActualAmountOriginal(reqFee.getActualAmount());
						fee.setNetAmount(reqFee.getActualAmount());
						fee.setWaivedAmount(reqFee.getWaivedAmount());
						fee.setVersion(1);
						fee.setFinReference(fsi.getExternalReference());
						fee.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						fee.setLastMntBy(userDetails.getUserId());
						fee.setTransactionId(fee.getFinReference());
					} else {
						fee.setNetAmount(fee.getActualAmount().add(reqFee.getActualAmount()));
						fee.setWaivedAmount(fee.getWaivedAmount().add(reqFee.getWaivedAmount()));
						fee.setTransactionId(fee.getFinReference());
					}
					fee.setFeeTypeCode(finTypeFee.getFeeTypeCode());
					fee.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
					FinFeeReceipt finFeeReceipt = new FinFeeReceipt();
					finFeeReceipt.setFeeID(fee.getFeeID());
					finFeeReceipt.setPaidAmount(reqFee.getPaidAmount());
					// fee.setPaidAmount(BigDecimal.ZERO);
					fee.getFinFeeReceipts().add(finFeeReceipt);
					feelist.add(fee);
				}
			}
			if (!isFeeCodeFound) {
				String[] valueParm = new String[1];
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90247", valueParm)));
				return errorDetails;
			}
		}
		if (errorDetails.size() == 0) {
			fsi.setFinFeeDetails(feelist);
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	private FinFeeDetail getFeeByFeeType(List<FinFeeDetail> prvsFees, Long feeTypeID) {
		logger.debug(Literal.ENTERING);
		for (FinFeeDetail finFeeDetail : prvsFees) {
			if (finFeeDetail.getFeeTypeID() == feeTypeID) {
				return finFeeDetail;
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for validate the Receipt related data.
	 * 
	 * 
	 * @param fsi
	 * @return
	 */
	private WSReturnStatus validateReceiptData(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);
		String finReference = fsi.getFinReference();
		String[] valueParm = null;

		Date appDate = SysParamUtil.getAppDate();

		// better to check any unpaid fess us there or not

		// Valid Receipt Mode
		String receiptMode = fsi.getPaymentMode();
		if (!StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CASH)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_CHEQUE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DD)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_NEFT)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_RTGS)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_IMPS)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ESCROW)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ONLINE)
				&& !StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DIGITAL)) {

			valueParm = new String[2];
			valueParm[0] = "Receipt mode";
			valueParm[1] = RepayConstants.RECEIPTMODE_CASH + "," + RepayConstants.RECEIPTMODE_CHEQUE + ","
					+ RepayConstants.RECEIPTMODE_DD + "," + RepayConstants.RECEIPTMODE_NEFT + ","
					+ RepayConstants.RECEIPTMODE_RTGS + "," + RepayConstants.RECEIPTMODE_IMPS + ","
					+ RepayConstants.RECEIPTMODE_ESCROW + RepayConstants.RECEIPTMODE_ONLINE + ","
					+ RepayConstants.RECEIPTMODE_DIGITAL;
			return APIErrorHandlerService.getFailedStatus("90281", valueParm);
		}
		FinReceiptDetail finReceiptDetail = fsi.getReceiptDetail();

		if (finReceiptDetail.getFundingAc() <= 0) {
			valueParm = new String[1];
			valueParm[0] = "fundingAccount";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(finReceiptDetail.getFundingAc(), "");
			if (partnerBank != null) {
				fsi.getReceiptDetail().setPartnerBankAc(partnerBank.getAccountNo());
				fsi.getReceiptDetail().setPartnerBankAcType(partnerBank.getAcType());
			} else {
				valueParm = new String[2];
				valueParm[0] = "fundingAccount";
				valueParm[1] = String.valueOf(finReceiptDetail.getFundingAc());
				return APIErrorHandlerService.getFailedStatus("90224", valueParm);
			}
		}

		if (finReceiptDetail.getReceivedDate() == null) {
			valueParm = new String[1];
			valueParm[0] = "receivedDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		} else {
			if (DateUtility.compare(fsi.getReceiptDetail().getReceivedDate(), appDate) > 0) {
				valueParm = new String[1];
				valueParm[0] = DateUtility.formatToLongDate(appDate);
				return APIErrorHandlerService.getFailedStatus("RU0006", valueParm);
			}
		}

		if (StringUtils.isNotBlank(finReceiptDetail.getPaymentRef())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM));
			Matcher matcher = pattern.matcher(finReceiptDetail.getPaymentRef());
			if (matcher.matches() == false) {
				valueParm = new String[1];
				valueParm[0] = "paymentRef";
				return APIErrorHandlerService.getFailedStatus("90347", valueParm);
			}
		}

		if (RepayConstants.RECEIPTMODE_CHEQUE.equals(receiptMode)
				|| RepayConstants.RECEIPTMODE_DD.equals(receiptMode)) {

			if (StringUtils.isBlank(finReceiptDetail.getFavourName())) {
				valueParm = new String[1];
				valueParm[0] = "favourName";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			// CHEQUE / DD number
			if (StringUtils.isBlank(finReceiptDetail.getFavourNumber())) {
				valueParm = new String[1];
				valueParm[0] = "favourNumber";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			if (!StringUtils.isNumeric(finReceiptDetail.getFavourNumber())) {
				valueParm = new String[1];
				valueParm[0] = "favourNumber";
				return APIErrorHandlerService.getFailedStatus("90242", valueParm);
			}
			if (finReceiptDetail.getFavourNumber().length() != 6) {
				valueParm = new String[2];
				valueParm[0] = "favourNumber size";
				valueParm[1] = "six";
				return APIErrorHandlerService.getFailedStatus("90277", valueParm);
			}
			// Cheque Acc No {0} is lessthan or equals to {1} .
			if (StringUtils.length(finReceiptDetail.getChequeAcNo()) > 50) {
				valueParm = new String[2];
				valueParm[0] = "chequeAcNo";
				valueParm[1] = "50";
				return APIErrorHandlerService.getFailedStatus("90220", valueParm);
			}
			// value Date
			if (finReceiptDetail.getValueDate() == null) {
				valueParm = new String[1];
				valueParm[0] = "valueDate";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				if (DateUtility.compare(finReceiptDetail.getValueDate(), appDate) > 0) {
					valueParm = new String[1];
					valueParm[0] = DateUtility.formatToLongDate(appDate);
					return APIErrorHandlerService.getFailedStatus("RU0007", valueParm);
				}
			}
			if (fsi.isNonStp()) {
				if (fsi.getRealizationDate() == null) {
					valueParm = new String[1];
					valueParm[0] = "realizationDate";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);
				} else {
					if (DateUtility.compare(fsi.getRealizationDate(), finReceiptDetail.getValueDate()) < 0) {
						valueParm = new String[1];
						valueParm[0] = DateUtility.formatToLongDate(finReceiptDetail.getValueDate());
						return APIErrorHandlerService.getFailedStatus("RU0019", valueParm);
					}
					if (DateUtility.compare(fsi.getRealizationDate(), appDate) > 0) {
						valueParm = new String[2];
						valueParm[0] = "realizationDate";
						valueParm[1] = DateUtility.formatToLongDate(appDate);
						return APIErrorHandlerService.getFailedStatus("30568", valueParm);
					}
				}
			}

			if (StringUtils.isBlank(finReceiptDetail.getBankCode())) {
				valueParm = new String[1];
				valueParm[0] = "bankCode";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				// Bank Details should be configured
				BankDetail bankDetail = bankDetailService.getBankDetailById(finReceiptDetail.getBankCode());
				if (bankDetail == null) {
					valueParm = new String[2];
					valueParm[0] = "bankCode";
					valueParm[1] = finReceiptDetail.getBankCode();
					return APIErrorHandlerService.getFailedStatus("90224", valueParm);
				}
			}
			// IFSC code (bank branch) validation
			if (!StringUtils.isBlank(finReceiptDetail.getBankCode())
					&& !StringUtils.isBlank(finReceiptDetail.getiFSC())) {
				BankBranch bankBranch = null;
				bankBranch = bankBranchService.getBankBrachByIFSC(finReceiptDetail.getiFSC());
				if (bankBranch == null) {
					valueParm = new String[2];
					valueParm[0] = "ifsc";
					valueParm[1] = finReceiptDetail.getiFSC();
					return APIErrorHandlerService.getFailedStatus("90224", valueParm);
				} else if (!StringUtils.equals(finReceiptDetail.getBankCode(), bankBranch.getBankCode())) {
					valueParm = new String[1];
					valueParm[0] = finReceiptDetail.getiFSC();
					return APIErrorHandlerService.getFailedStatus("99020", valueParm);
				}
				// setting the bank branch ID for cheque and DD
				finReceiptDetail.setBankBranchID(bankBranch.getBankBranchID());
			}
		} else {
			// need to empty the data wich is not req
			fsi.setRealizationDate(null);
			finReceiptDetail.setFavourName("");
			finReceiptDetail.setFavourNumber("");
			finReceiptDetail.setChequeAcNo("");
		}
		// In Case of online mode transactionRef is mandatory
		if (StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_NEFT)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_RTGS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_IMPS)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_ESCROW)
				|| StringUtils.equals(receiptMode, RepayConstants.RECEIPTMODE_DIGITAL)) {
			if (StringUtils.isBlank(finReceiptDetail.getTransactionRef())) {
				valueParm = new String[1];
				valueParm[0] = "transactionRef";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM));
			Matcher matcher = pattern.matcher(finReceiptDetail.getTransactionRef());
			if (matcher.matches() == false) {
				valueParm = new String[1];
				valueParm[0] = "transactionRef";
				return APIErrorHandlerService.getFailedStatus("90347", valueParm);
			}
		}

		if (CollectionUtils.isEmpty(fsi.getFinFeeDetails())) {
			valueParm = new String[1];
			valueParm[0] = "fees";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// check any UpFront is in process
		String reference = finReference;
		if (StringUtils.isNotBlank(fsi.getExternalReference())) {
			reference = Objects.toString(fsi.getCustID(), "");
		}
		boolean isInProgRec = finReceiptHeaderDAO.isReceiptsInProcess(reference, FinServiceEvent.FEEPAYMENT,
				Long.MIN_VALUE, "_Temp");
		if (isInProgRec) {
			valueParm = new String[1];
			valueParm[0] = FinServiceEvent.FEEPAYMENT;
			return APIErrorHandlerService.getFailedStatus("IMD002", valueParm);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for enabling the DepositProcess<br>
	 * Based on the workflow and roleCode checks weather deposit process required or not.
	 * 
	 * @param userID
	 * @return CustomerODLoanDetails
	 */
	public List<LoanPendingData> getCustomerODLoanDetails(long userID) {
		return financeMainService.getCustomerODLoanDetails(userID);
	}

	public FinanceTaxDetail getFinanceTaxDetails(long finID) {
		logger.info(Literal.ENTERING);

		FinanceTaxDetail financeTaxDetail = financeTaxDetailService.getApprovedFinanceTaxDetail(finID);

		logger.info(Literal.LEAVING);

		return financeTaxDetail;
	}

	private AuditHeader prepareAuditHeader(final FinanceTaxDetail financeTaxDetail, String tranType) {

		logger.info(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail(tranType, 1, financeTaxDetail.getBefImage(), financeTaxDetail);
		AuditHeader auditHeader = new AuditHeader(String.valueOf(financeTaxDetail.getTaxCustId()),
				String.valueOf(financeTaxDetail.getTaxCustId()), null, null, auditDetail,
				financeTaxDetail.getUserDetails(), new HashMap<String, List<ErrorDetail>>());

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		logger.info(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * Method <b>saveGSTDetails(FinanceTaxDetail)</b> - Saves GST Details for a finReference
	 * 
	 * @param financeTaxDetail - {@link FinanceTaxDetail}
	 * @return {@link AuditHeader}
	 */
	public WSReturnStatus saveGSTDetails(final FinanceTaxDetail financeTaxDetail) {

		WSReturnStatus returnStatus = new WSReturnStatus();

		logger.info(Literal.ENTERING);

		financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		financeTaxDetail.setNewRecord(true);
		financeTaxDetail.setVersion(1);
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeTaxDetail.setUserDetails(userDetails);
		financeTaxDetail.setLastMntBy(userDetails.getUserId());
		financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeTaxDetail.setSourceId(PennantConstants.FINSOURCE_ID_API);

		AuditHeader auditHeader = prepareAuditHeader(financeTaxDetail, PennantConstants.TRAN_WF);
		AuditHeader savedGSTDetails = financeTaxDetailService.doApprove(auditHeader);

		if (savedGSTDetails.getAuditError() != null) {
			for (ErrorDetail errorDetail : savedGSTDetails.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else
			returnStatus = APIErrorHandlerService.getSuccessStatus();

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	/**
	 * Method <b>rejuvenateGSTDetails(FinanceTaxDetail)</b> - updates GST Details for a finReference
	 * 
	 * @param financeTaxDetail - {@link FinanceTaxDetail}
	 * @param i
	 * @return {@link AuditHeader}
	 */
	public WSReturnStatus rejuvenateGSTDetails(FinanceTaxDetail financeTaxDetail, int version) {

		WSReturnStatus returnStatus = new WSReturnStatus();

		logger.info(Literal.ENTERING);

		financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		financeTaxDetail.setNewRecord(false);
		financeTaxDetail.setVersion(version + 1);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		financeTaxDetail.setUserDetails(userDetails);
		financeTaxDetail.setLastMntBy(userDetails.getLoginLogId());
		financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		financeTaxDetail.setSourceId(PennantConstants.FINSOURCE_ID_API);

		AuditHeader auditHeader = prepareAuditHeader(financeTaxDetail, PennantConstants.TRAN_WF);
		AuditHeader revisedGSTDetails = financeTaxDetailService.doApprove(auditHeader);

		if (revisedGSTDetails.getAuditError() != null) {
			for (ErrorDetail errorDetail : revisedGSTDetails.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else
			returnStatus = APIErrorHandlerService.getSuccessStatus();

		logger.info(Literal.LEAVING);

		return returnStatus;
	}

	public WSReturnStatus approveDisbursementResponse(DisbRequest disbRequest) {
		logger.info(Literal.ENTERING);

		FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		long paymentId = disbRequest.getPaymentId();
		finAdvancePayments.setPaymentId(paymentId);
		String PAID_STATUS = "E";
		String REJECTED_STATUS = "R";

		String finReference = disbRequest.getFinReference();

		Long finID = financeMainDAO.getFinID(finReference);

		String type = disbRequest.getType();
		if (StringUtils.isNotBlank(type) && DisbursementConstants.CHANNEL_DISBURSEMENT.equals(type)) {
			int count = finAdvancePaymensDAO.getCountByPaymentId(finID, paymentId);
			if (count <= 0) {
				String[] valueParam = new String[1];
				valueParam[0] = "PaymentId";
				return APIErrorHandlerService.getFailedStatus("90405", valueParam);
			}
			FinAdvancePayments finAdv = finAdvancePaymensDAO.getFinAdvancePaymentsById(finAdvancePayments, "_AView");

			if (finAdv == null) {
				String[] valueParam = new String[1];
				valueParam[0] = "PaymentId";
				return APIErrorHandlerService.getFailedStatus("90405", valueParam);
			}
			String status = finAdv.getStatus();

			if (!DisbursementConstants.STATUS_AWAITCON.equals(status)) {
				String[] valueParam = new String[2];
				valueParam[0] = "Disbursement status already updated";
				return APIErrorHandlerService.getFailedStatus("21005", valueParam);
			}

			if (ImplementationConstants.HOLD_DISB_INST_POST) {
				FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);
				if (DisbursementConstants.STATUS_AWAITCON.equals(status)) {
					finAdv.setStatus(DisbursementConstants.STATUS_APPROVED);
				} else {
					finAdv.setStatus(DisbursementConstants.STATUS_AWAITCON);
				}
				fm.setEntityCode(financeMainDAO.getLovDescEntityCode(finID, "_View"));
				fm.setLovDescEntityCode(fm.getEntityCode());
				FinanceDetail fd = new FinanceDetail();
				List<FinAdvancePayments> finAdvList = new ArrayList<FinAdvancePayments>();

				finAdvList.add(finAdv);
				fd.setAdvancePaymentsList(finAdvList);
				fd.getFinScheduleData().setFinanceMain(fm);

				List<FinAdvancePayments> advancePaymentsList = fd.getAdvancePaymentsList();
				AccountingEngine.post(AccountingEvent.DISBINS, fd, fm.getFinBranch());

				List<FinAdvancePayments> advPayList = advancePaymentsList;

				// loop through the disbursements.
				if (CollectionUtils.isNotEmpty(advPayList)) {
					for (FinAdvancePayments advPayment : advPayList) {
						finAdvancePaymensDAO.updateLinkedTranId(advPayment);
					}
				}
			}

			finAdvancePayments.setPaymentId(paymentId);
			finAdvancePayments.setStatus(disbRequest.getStatus());
			finAdvancePayments.setFinID(finID);
			finAdvancePayments.setFinReference(finReference);
			finAdvancePayments.setClearingDate(disbRequest.getClearingDate());
			if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbRequest.getDisbType())
					|| DisbursementConstants.PAYMENT_TYPE_DD.equals(disbRequest.getDisbType())) {
				finAdvancePayments.setClearingDate(disbRequest.getDisbDate());
				finAdvancePayments.setLLReferenceNo(disbRequest.getChequeNo());
			}
			finAdvancePayments.setRejectReason(disbRequest.getRejectReason());
			finAdvancePayments.setTransactionRef(disbRequest.getTransactionRef());

			if (REJECTED_STATUS.equals(disbRequest.getStatus()) && !ImplementationConstants.HOLD_DISB_INST_POST) {
				postingsPreparationUtil.postReversalsByLinkedTranID(finAdv.getLinkedTranId());
				finAdvancePayments.setStatus(DisbursementConstants.STATUS_REJECTED);
			} else {
				finAdvancePayments.setStatus(DisbursementConstants.STATUS_PAID);
			}

			finAdvancePaymensDAO.updateDisbursmentStatus(finAdvancePayments);
		}

		logger.info(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	public FinanceDetail doProcessNonLanReceipt(FinReceiptData receiptData, String receiptPurpose) throws Exception {
		logger.info(Literal.ENTERING);

		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		FinReceiptHeader rch = receiptData.getReceiptHeader();
		FinReceiptDetail rcd = rch.getReceiptDetails().get(0);
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinServiceInstruction finServiceInst = finScheduleData.getFinServiceInstruction();

		rcd.setDueAmount(rch.getReceiptAmount());

		if (!APIConstants.REQTYPE_POST.equals(finServiceInst.getReqType())) {
			return financeDetail;
		}

		String paymentMode = finServiceInst.getPaymentMode();
		String receiptMode = paymentMode;

		if (RepayConstants.RECEIPTMODE_ONLINE.equals(receiptMode)) {
			receiptMode = finServiceInst.getSubReceiptMode();
		}

		long fundingAccount = finServiceInst.getReceiptDetail().getFundingAc();
		finServiceInst.setFundingAc(fundingAccount);

		// setting the SourceID
		rch.setSourceId(PennantConstants.FINSOURCE_ID_API);

		PartnerBank partnerBank = partnerBankDAO.getPartnerBankById(rcd.getFundingAc(), "");
		if (partnerBank != null) {
			rcd.setPartnerBankAc(partnerBank.getAccountNo());
			rcd.setPartnerBankAcType(partnerBank.getAcType());
		}

		AuditHeader auditHeader = getAuditHeader(receiptData, PennantConstants.TRAN_WF);

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		if (finServiceInst.isReceiptUpload() && "A".equals(finServiceInst.getStatus())
				&& ("CHEQUE".equals(paymentMode) || "DD".equals(paymentMode))) {

			auditHeader = nonLanReceiptService.saveOrUpdate(auditHeader);
		} else {
			receiptData.getReceiptHeader().setValueDate(rch.getValueDate());

			auditHeader = nonLanReceiptService.doApprove(auditHeader);
		}

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail auditErrorDetail : auditHeader.getErrorMessage()) {
				receiptService.setErrorToFSD(finScheduleData, auditErrorDetail.getCode(), auditErrorDetail.getError());
				return financeDetail;
			}
		}

		receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		finServiceInst.setReceiptId(receiptData.getReceiptHeader().getReceiptID());
		financeDetail.setReceiptId(receiptData.getReceiptHeader().getReceiptID());

		List<FinServiceInstruction> finServInstList = new ArrayList<>();
		Date appDate = SysParamUtil.getAppDate();
		Date sysDate = DateUtility.getSysDate();

		for (FinReceiptDetail recDtl : rch.getReceiptDetails()) {
			for (FinRepayHeader rpyHeader : recDtl.getRepayHeaders()) {
				FinServiceInstruction finServInst = new FinServiceInstruction();
				finServInst.setFinID(finServiceInst.getFinID());
				finServInst.setFinReference(finServiceInst.getFinReference());
				finServInst.setFinEvent(rpyHeader.getFinEvent());
				finServInst.setAmount(rpyHeader.getRepayAmount());
				finServInst.setAppDate(appDate);
				finServInst.setSystemDate(sysDate);
				finServInst.setMaker(auditHeader.getAuditUsrId());
				finServInst.setMakerAppDate(appDate);
				finServInst.setMakerSysDate(sysDate);
				finServInst.setChecker(auditHeader.getAuditUsrId());
				finServInst.setCheckerAppDate(appDate);
				finServInst.setCheckerSysDate(sysDate);
				finServInst.setReference(String.valueOf(rch.getReceiptID()));
				finServInstList.add(finServInst);
			}
		}

		// set receipt id in data
		if (finServiceInst.isReceiptUpload() && !finServiceInst.isReceiptResponse()) {
			this.receiptUploadDetailDAO.updateReceiptId(finServiceInst.getUploadDetailId(), rcd.getReceiptID());
		}

		// set receipt id response job
		if (finServiceInst.isReceiptUpload() && finServiceInst.isReceiptResponse()) {
			this.receiptResponseDetailDAO.updateReceiptResponseId(finServiceInst.getRootId(), rcd.getReceiptID());
		}

		logger.info(Literal.LEAVING);
		return financeDetail;
	}

	public WSReturnStatus processCovenants(FinanceMain financeMain, List<Covenant> covenantsList, boolean origination) {
		logger.debug(Literal.ENTERING);

		String finReference = financeMain.getFinReference();
		Date appDate = SysParamUtil.getAppDate();
		String covModule = APIConstants.COVENANT_MODULE_NAME;
		int docSize = 0;

		List<Covenant> aCovenants = covenantsDAO.getCovenants(finReference, covModule, TableType.VIEW);

		Map<Long, Covenant> covenantsMap = aCovenants.stream()
				.collect(Collectors.toMap(Covenant::getCovenantTypeId, covenant -> covenant));

		for (Covenant covenant : covenantsList) {
			Map<String, DocumentDetails> doctypeMap = new HashMap<>();
			covenant.setKeyReference(finReference);
			covenant.setModule(covModule);

			LoggedInUser userDetails = financeMain.getUserDetails();
			if (covenantsMap.containsKey(covenant.getCovenantTypeId())) {
				// validating Documnets
				long id = covenantsMap.get(covenant.getCovenantTypeId()).getId();
				List<CovenantDocument> covenantDocuments = covenantsDAO.getCovenantDocuments(id, TableType.VIEW);
				docSize = covenantDocuments.size();
				for (CovenantDocument cd : covenantDocuments) {
					DocumentDetails docDetails = documentDetailsDAO.getDocumentDetails(cd.getDocumentId(), "_View");
					doctypeMap.put(docDetails.getDocCategory(), docDetails);
				}

				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
					if (doctypeMap.containsKey(covenantDocument.getDoctype())) {
						DocumentDetails existingDoc = doctypeMap.get(covenantDocument.getDoctype());
						existingDoc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						existingDoc.setVersion(1);
						existingDoc.setRecordType("");
						existingDoc.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						existingDoc.setLastMntBy(userDetails.getUserId());
						existingDoc.setNewRecord(false);
						existingDoc.setCustId(financeMain.getCustID());
						existingDoc.setDocCategory(covenantDocument.getDoctype());

						covenantDocument.setRecordType("");
						covenantDocument.setNewRecord(false);
						covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						covenantDocument.setLastMntBy(userDetails.getUserId());
						covenantDocument.setDocumentDetail(existingDoc);
					} else {
						DocumentDetails documentDetails = new DocumentDetails();
						documentDetails.setDocCategory(covenantDocument.getDoctype());
						setDocumentProperties(financeMain, appDate, documentDetails, covenantDocument);

						covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						covenantDocument.setDocumentDetail(documentDetails);
						covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						covenantDocument.setLastMntBy(userDetails.getUserId());
						covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						covenantDocument.setDocumentReceivedDate(appDate);
					}
					covenantDocument.setCovenantType(covenant.getCovenantType());
					covenant.getDocumentDetails().add(covenantDocument.getDocumentDetail());
				}

				covenant.setId(id);
				covenant.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				covenant.setNewRecord(false);
			} else {
				for (CovenantDocument covenantDocument : covenant.getCovenantDocuments()) {
					DocumentDetails documentDetails = new DocumentDetails();
					documentDetails.setDocCategory(covenantDocument.getDoctype());
					setDocumentProperties(financeMain, appDate, documentDetails, covenantDocument);

					covenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					covenantDocument.setCovenantType(covenant.getCovenantType());
					covenantDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					covenantDocument.setLastMntBy(userDetails.getUserId());
					covenantDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					covenantDocument.setDocumentDetail(documentDetails);
					if (origination) {
						covenantDocument.setNewRecord(true);
					}
					covenant.getDocumentDetails().add(covenantDocument.getDocumentDetail());
				}

				covenant.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (origination) {
					covenant.setNewRecord(true);
				}
			}

			covenant.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			covenant.setVersion(1);
			covenant.setLastMntBy(userDetails.getUserId());
			covenant.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE) && origination) {
			covenantsService.saveOrUpdate(covenantsList, TableType.TEMP_TAB, PennantConstants.TRAN_WF);
		} else {
			covenantsService.doApprove(covenantsList, TableType.MAIN_TAB, PennantConstants.TRAN_WF, docSize);
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	protected AuditHeader getAuditHeader(FinanceMain financeMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, financeMain);
		return new AuditHeader(financeMain.getFinReference(), null, null, null, auditDetail,
				financeMain.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private void setDocumentProperties(FinanceMain financeMain, Date appDate, DocumentDetails documentDetails,
			CovenantDocument covenantDocument) {
		documentDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		documentDetails.setVersion(1);
		documentDetails.setDocModule(FinanceConstants.MODULE_NAME);
		documentDetails.setDocName(covenantDocument.getDocName());
		documentDetails.setReferenceId(financeMain.getFinReference());
		documentDetails.setDocReceived(true);
		covenantDocument.setDocumentReceivedDate(appDate);
		documentDetails.setFinReference(financeMain.getFinReference());
		documentDetails.setCustId(financeMain.getCustID());
		documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		documentDetails.setNewRecord(true);
		String docName = covenantDocument.getDocName();
		String extension = docName.substring(docName.lastIndexOf("."));

		if (extension.equalsIgnoreCase(DocType.JPEG.getExtension())
				|| extension.equalsIgnoreCase(DocType.JPG.getExtension())
				|| extension.equalsIgnoreCase(DocType.PNG.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_IMAGE);
		} else if (extension.equalsIgnoreCase(DocType.DOC.getExtension())
				|| extension.equalsIgnoreCase(DocType.DOCX.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_WORD);
		} else if (extension.equalsIgnoreCase(DocType.XLS.getExtension())
				|| extension.equalsIgnoreCase(DocType.XLSX.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_EXCEL);
		} else if (extension.equalsIgnoreCase(DocType.PDF.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_PDF);
		} else if (extension.equalsIgnoreCase(DocType.MSG.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_MSG);
		} else if (extension.equalsIgnoreCase(DocType.ZIP.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_ZIP);
		} else if (extension.equalsIgnoreCase(DocType.RAR.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_RAR);
		} else if (extension.equalsIgnoreCase(DocType.Z7.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_7Z);
		} else if (extension.equalsIgnoreCase(DocType.TXT.getExtension())) {
			documentDetails.setDoctype(PennantConstants.DOC_TYPE_TXT);
		}
		documentDetails.setDocImage(covenantDocument.getDocImage());
	}

	public WSReturnStatus processFeeWaivers(FeeWaiverHeader feeWaiverHeader, FeeWaiverHeader feeWaiver) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		List<FeeWaiverDetail> actaulfeeWaiverDetails = feeWaiver.getFeeWaiverDetails();

		List<FeeWaiverDetail> feeWaiverDetails = feeWaiverHeader.getFeeWaiverDetails();

		for (FeeWaiverDetail feeWaiverDetail : feeWaiverDetails) {

			for (FeeWaiverDetail fwd : actaulfeeWaiverDetails) {
				if (StringUtils.equals(feeWaiverDetail.getFeeTypeCode(), fwd.getFeeTypeCode())) {
					BigDecimal currWaiverAmount = feeWaiverDetail.getCurrWaiverAmount();
					fwd.setCurrWaiverAmount(currWaiverAmount);
					// Preparing GST
					prepareGST(fwd, currWaiverAmount);
					fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
				}
			}

		}

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(feeWaiver, PennantConstants.TRAN_WF);
		auditHeader.setApiHeader(reqHeaderDetails);
		feeWaiver.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		feeWaiver.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
		feeWaiver.setPostingDate(SysParamUtil.getAppDate());
		feeWaiver.setValueDate(feeWaiverHeader.getValueDate());
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		feeWaiver.setUserDetails(userDetails);
		feeWaiver.setVersion(1);
		feeWaiver.setEvent(FinServiceEvent.FEEWAIVERS);
		feeWaiver.setLastMntBy(userDetails.getUserId());
		feeWaiver.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		feeWaiver.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		try {
			auditHeader = feeWaiverHeaderService.doApprove(auditHeader);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			returnStatus = APIErrorHandlerService.getFailedStatus();
			return returnStatus;
		}
		logger.debug(Literal.LEAVING);
		returnStatus = APIErrorHandlerService.getSuccessStatus();
		return returnStatus;
	}

	private void prepareGST(FeeWaiverDetail actualWaiverDetail, BigDecimal waiverAmount) {
		Map<String, BigDecimal> gstPercentages = getTaxPercentages(actualWaiverDetail.getFinID());

		actualWaiverDetail.setWaivedAmount(waiverAmount);
		actualWaiverDetail.setCurrWaiverAmount(waiverAmount);
		actualWaiverDetail.setCurrActualWaiver(waiverAmount);
		actualWaiverDetail.setCurrWaiverGST(BigDecimal.ZERO);

		if (!actualWaiverDetail.isTaxApplicable()) {
			return;
		}

		/* always taking as Inclusive case here */
		TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(waiverAmount, gstPercentages);
		actualWaiverDetail.setCurrActualWaiver(waiverAmount.subtract(taxSplit.gettGST()));
		actualWaiverDetail.setCurrWaiverGST(taxSplit.gettGST());

		if (actualWaiverDetail.getTaxHeader() == null) {
			return;
		}

		List<Taxes> taxDetails = actualWaiverDetail.getTaxHeader().getTaxDetails();

		if (CollectionUtils.isEmpty(taxDetails)) {
			return;
		}

		for (Taxes tax : taxDetails) {
			switch (tax.getTaxType()) {
			case RuleConstants.CODE_CGST:
				tax.setWaivedTax(taxSplit.getcGST());
				break;
			case RuleConstants.CODE_SGST:
				tax.setWaivedTax(taxSplit.getsGST());
				break;
			case RuleConstants.CODE_UGST:
				tax.setWaivedTax(taxSplit.getuGST());
				break;
			case RuleConstants.CODE_IGST:
				tax.setWaivedTax(taxSplit.getiGST());
				break;
			case RuleConstants.CODE_CESS:
				tax.setWaivedTax(taxSplit.getCess());
				break;
			default:
				break;
			}

			tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
			tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));
		}
	}

	private Map<String, BigDecimal> getTaxPercentages(long finID) {
		Map<String, BigDecimal> taxPercentages = null;
		if (taxPercentages == null) {
			taxPercentages = GSTCalculator.getTaxPercentages(finID);
		}

		return taxPercentages;
	}

	protected AuditHeader getAuditHeader(FeeWaiverHeader feeWaiver, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, feeWaiver);
		return new AuditHeader(feeWaiver.getFinReference(), null, null, null, auditDetail, feeWaiver.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	public WSReturnStatus processChequeDetail(FinanceDetail financeDetail, String tableType) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = null;
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		try {

			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			// defaulting record values to cheque Header

			chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			if (StringUtils.isNotBlank(tableType)) {
				chequeHeader.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				chequeHeader.setNewRecord(true);
				chequeHeader.setVersion(1);
			} else {
				chequeHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				chequeHeader.setNewRecord(false);
				chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);

			}
			chequeHeader.setLastMntBy(userDetails.getUserId());
			chequeHeader.setRecordStatus(financeMain.getRecordStatus());
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setTaskId(financeMain.getTaskId());
			chequeHeader.setNextTaskId(financeMain.getNextTaskId());
			chequeHeader.setRoleCode(financeMain.getRoleCode());
			chequeHeader.setNextRoleCode(financeMain.getNextRoleCode());
			chequeHeader.setWorkflowId(financeMain.getWorkflowId());
			chequeHeader.setActive(true);
			chequeHeader.setSourceId(PennantConstants.FINSOURCE_ID_API);

			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			int chequeSerialNum = chequeHeader.getChequeSerialNo();
			chequeHeader.setTotalAmount(BigDecimal.ZERO);
			for (ChequeDetail chequeDetail : chequeDetails) {
				// defaulting record values to cheque detail
				chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				chequeDetail.setNewRecord(true);
				chequeDetail.setLastMntBy(userDetails.getUserId());
				chequeDetail.setRecordStatus(financeMain.getRecordStatus());
				chequeDetail.setVersion(2);
				chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				chequeDetail.setTaskId(financeMain.getTaskId());
				chequeDetail.setNextTaskId(financeMain.getNextTaskId());
				chequeDetail.setRoleCode(financeMain.getRoleCode());
				chequeDetail.setNextRoleCode(financeMain.getNextRoleCode());
				chequeDetail.setWorkflowId(financeMain.getWorkflowId());

				chequeDetail.setChequeSerialNo(chequeSerialNum);
				chequeSerialNum++;

				// setting the values to the cheque Detail by excluding at the cheque header
				chequeDetail.setBankBranchID(chequeHeader.getBankBranchID());
				chequeDetail.setAccHolderName(chequeHeader.getAccHolderName());
				chequeDetail.setAccountNo(chequeHeader.getAccountNo());

				// setting the reference
				chequeHeader.setFinID(financeMain.getFinID());
				chequeHeader.setFinReference(financeMain.getFinReference());
				chequeHeader.setTotalAmount(chequeDetail.getAmount().add(chequeHeader.getTotalAmount()));
				// setting the default values
				chequeDetail.setStatus(PennantConstants.CHEQUESTATUS_NEW);
				chequeDetail.setChequeStatus(PennantConstants.CHEQUESTATUS_NEW);
				chequeDetail.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
				chequeDetail.setActive(true);

			}
			response = new WSReturnStatus();

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(chequeHeader, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			if (StringUtils.isNotBlank(tableType)) {
				auditHeader = chequeHeaderService.saveOrUpdate(auditHeader);
			} else {
				auditHeader = chequeHeaderService.doApprove(auditHeader);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateCheque(FinanceDetail financeDetail, String tableType) {

		ChequeHeader chequeHeader = financeDetail.getChequeHeader();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<ErrorDetail> errorDetails = null;
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		WSReturnStatus response = null;
		try {
			int count = 1;
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			if (StringUtils.isNotBlank(tableType)) {
				chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}
			// defaulting record values to cheque Header
			chequeHeader.setNewRecord(false);
			chequeHeader.setLastMntBy(userDetails.getUserId());
			// chequeHeader.setVersion(1);
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setTaskId(financeMain.getTaskId());
			chequeHeader.setNextTaskId(financeMain.getNextTaskId());
			chequeHeader.setRoleCode(financeMain.getRoleCode());
			chequeHeader.setNextRoleCode(financeMain.getNextRoleCode());
			chequeHeader.setWorkflowId(financeMain.getWorkflowId());
			chequeHeader.setActive(true);
			chequeHeader.setSourceId(PennantConstants.FINSOURCE_ID_API);

			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			int chequeSerialNum = chequeHeader.getChequeSerialNo();
			chequeHeader.setTotalAmount(BigDecimal.ZERO);
			for (ChequeDetail chequeDetail : chequeDetails) {
				// defaulting record values to cheque detail

				if (chequeDetail.isDelete()) {
					// subtracting the count to reduce the No.Of cheques in cheque Header

					int result = chequeHeader.getNoOfCheques() - count;

					chequeHeader.setFinID(financeMain.getFinID());
					chequeHeader.setFinReference(financeMain.getFinReference());

					// setting the default values if record type is delete
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					chequeDetail.setActive(false);
					chequeDetail.setNewRecord(false);
					chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
					chequeDetail.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
					chequeDetail.setStatus(PennantConstants.CHEQUESTATUS_NEW);
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					AuditHeader auditHeader = getAuditHeader(chequeHeader, PennantConstants.TRAN_WF);
					auditHeader.setApiHeader(reqHeaderDetails);
					// deleting the record from the cheque details
					chequeHeader.setNoOfCheques(result);
					chequeHeaderService.doApprove(auditHeader);

					// updating the record in the cheque Header

				} else {

					if (StringUtils.isNotBlank(tableType)) {
						chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						chequeDetail.setNewRecord(true);
					} else {
						chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						chequeDetail.setNewRecord(false);
					}
					chequeDetail.setLastMntBy(userDetails.getUserId());
					chequeDetail.setRecordStatus(financeMain.getRecordStatus());
					chequeDetail.setVersion(financeMain.getVersion());
					chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					chequeDetail.setTaskId(financeMain.getTaskId());
					chequeDetail.setNextTaskId(financeMain.getNextTaskId());
					chequeDetail.setRoleCode(financeMain.getRoleCode());
					chequeDetail.setNextRoleCode(financeMain.getNextRoleCode());
					chequeDetail.setWorkflowId(financeMain.getWorkflowId());

					chequeDetail.setChequeSerialNo(chequeSerialNum);
					chequeSerialNum++;

					// setting the values to the cheque Detail by excluding at the cheque header
					chequeDetail.setBankBranchID(chequeHeader.getBankBranchID());
					chequeDetail.setAccHolderName(chequeHeader.getAccHolderName());
					chequeDetail.setAccountNo(chequeHeader.getAccountNo());

					// setting the reference
					chequeHeader.setFinReference(financeMain.getFinReference());
					chequeHeader.setTotalAmount(chequeDetail.getAmount().add(chequeHeader.getTotalAmount()));
					// setting the default values
					chequeDetail.setStatus(PennantConstants.CHEQUESTATUS_NEW);
					chequeDetail.setChequeStatus(PennantConstants.CHEQUESTATUS_NEW);
					chequeDetail.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
					chequeDetail.setActive(true);
				}
			}

			response = new WSReturnStatus();
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(chequeHeader, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			if (StringUtils.isNotBlank(tableType)) {
				auditHeader = chequeHeaderService.saveOrUpdate((auditHeader));
			} else {
				auditHeader = chequeHeaderService.doApprove(auditHeader);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			response = APIErrorHandlerService.getSuccessStatus();

		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);

		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateChequeDetailsinMaintainence(FinanceDetail financeDetail, String tableType) {

		ChequeHeader chequeHeader = financeDetail.getChequeHeader();

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<ErrorDetail> errorDetails = null;
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		WSReturnStatus response = null;
		try {
			int count = 1;
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			// defaulting record values to cheque Header
			chequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			chequeHeader.setNewRecord(false);
			chequeHeader.setLastMntBy(userDetails.getUserId());
			// chequeHeader.setVersion(1);
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setTaskId(financeMain.getTaskId());
			chequeHeader.setNextTaskId(financeMain.getNextTaskId());
			chequeHeader.setRoleCode(financeMain.getRoleCode());
			chequeHeader.setNextRoleCode(financeMain.getNextRoleCode());
			chequeHeader.setWorkflowId(financeMain.getWorkflowId());
			chequeHeader.setActive(true);
			chequeHeader.setSourceId(PennantConstants.FINSOURCE_ID_API);

			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			int chequeSerialNum = chequeHeader.getChequeSerialNo();
			chequeHeader.setTotalAmount(BigDecimal.ZERO);

			List<ChequeDetail> dbChqueDetailList = chequeDetailDAO.getChequeDetailList(chequeHeader.getHeaderID(),
					tableType);

			for (ChequeDetail chequeDetail : chequeDetails) {
				// defaulting record values to cheque detail

				if (chequeDetail.isDelete()) {
					// subtracting the count to reduce the No.Of cheques in cheque Header

					int result = chequeHeader.getNoOfCheques() - count;

					chequeHeader.setFinID(financeMain.getFinID());
					chequeHeader.setFinReference(financeMain.getFinReference());

					// setting the default values if record type is delete
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					chequeDetail.setActive(false);
					chequeDetail.setNewRecord(false);
					chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
					chequeDetail.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
					chequeDetail.setStatus(PennantConstants.CHEQUESTATUS_NEW);
					APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
							.get(APIHeader.API_HEADER_KEY);
					AuditHeader auditHeader = getAuditHeader(chequeHeader, PennantConstants.TRAN_WF);
					auditHeader.setApiHeader(reqHeaderDetails);
					// deleting the record from the cheque details
					chequeHeader.setNoOfCheques(result);
					chequeHeaderService.doApprove(auditHeader);
					// updating the record in the cheque Header
				} else {

					if (StringUtils.isNotBlank(tableType)) {
						chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						chequeDetail.setNewRecord(true);
					} else {
						chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						chequeDetail.setNewRecord(false);
					}
					chequeDetail.setLastMntBy(userDetails.getUserId());
					chequeDetail.setRecordStatus(financeMain.getRecordStatus());
					chequeDetail.setVersion(financeMain.getVersion());
					chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					chequeDetail.setTaskId(financeMain.getTaskId());
					chequeDetail.setNextTaskId(financeMain.getNextTaskId());
					chequeDetail.setRoleCode(financeMain.getRoleCode());
					chequeDetail.setNextRoleCode(financeMain.getNextRoleCode());
					chequeDetail.setWorkflowId(financeMain.getWorkflowId());

					// setting the values to the cheque Detail by excluding at the cheque header
					chequeDetail.setBankBranchID(chequeHeader.getBankBranchID());
					chequeDetail.setAccHolderName(chequeHeader.getAccHolderName());
					chequeDetail.setAccountNo(chequeHeader.getAccountNo());

					chequeHeader.setFinID(financeMain.getFinID());
					// setting the reference
					chequeHeader.setFinReference(financeMain.getFinReference());
					chequeHeader.setTotalAmount(chequeDetail.getAmount().add(chequeHeader.getTotalAmount()));
					// setting the default values
					chequeDetail.setStatus(PennantConstants.CHEQUESTATUS_NEW);
					chequeDetail.setChequeStatus(PennantConstants.CHEQUESTATUS_NEW);
					chequeDetail.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
					chequeDetail.setActive(true);

					for (ChequeDetail chqDetail : dbChqueDetailList) {
						if (chqDetail.getChequeDetailsID() == chequeDetail.getChequeDetailsID()) {
							chequeDetail.setChequeSerialNo((chqDetail.getChequeSerialNo()));

						}

					}

				}
			}

			response = new WSReturnStatus();
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(chequeHeader, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			if (StringUtils.isNotBlank(tableType)) {
				auditHeader = chequeHeaderService.saveOrUpdate((auditHeader));
			} else {
				auditHeader = chequeHeaderService.doApprove(auditHeader);
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					return response;
				}
			}

			response = APIErrorHandlerService.getSuccessStatus();

		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);

		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus doCancelDisbursementInstructions(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		FinScheduleData schd = fd.getFinScheduleData();
		FinanceMain fm = schd.getFinanceMain();
		long finID = fm.getFinID();

		List<FinAdvancePayments> advancePayments = fd.getAdvancePaymentsList();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		int paymentSeq = finAdvancePaymensDAO.getCountByFinReference(finID);
		for (FinAdvancePayments advPayment : advancePayments) {
			if (DisbursementConstants.STATUS_PAID.equals(advPayment.getStatus())) {
				advPayment.setRecordType(PennantConstants.RCD_DEL);
				advPayment.setVersion(advPayment.getVersion() + 1);
				continue;
			}

			advPayment.setFinID(fm.getFinID());
			advPayment.setFinReference(fm.getFinReference());
			advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			advPayment.setNewRecord(true);
			advPayment.setLastMntBy(userDetails.getUserId());
			advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			advPayment.setUserDetails(fm.getUserDetails());
			advPayment.setVersion(1);
			advPayment.setPaymentSeq(paymentSeq + 1);
			paymentSeq++;
			advPayment.setDisbCCy(fm.getFinCcy());

			String paymentType = advPayment.getPaymentType();
			if (isOnlinePayment(paymentType)) {
				BankBranch bankBranch = new BankBranch();
				String ifscCode = advPayment.getiFSC();
				if (StringUtils.isNotBlank(ifscCode)) {
					bankBranch = bankBranchService.getBankBrachByIFSC(ifscCode);
				} else {
					String bankCode = advPayment.getBranchBankCode();
					String branchCode = advPayment.getBranchCode();
					if (StringUtils.isNotBlank(bankCode) && StringUtils.isNotBlank(branchCode)) {
						bankBranch = bankBranchService.getBankBrachByCode(bankCode, branchCode);
					}
				}

				if (bankBranch != null) {
					advPayment.setiFSC(bankBranch.getIFSC());
					advPayment.setBranchBankCode(bankBranch.getBankCode());
					advPayment.setBranchCode(bankBranch.getBranchCode());
					advPayment.setBankBranchID(bankBranch.getBankBranchID());
				}
			}
		}

		PayOrderIssueHeader poih = payOrderIssueHeaderDAO.getPayOrderIssueByHeaderRef(finID, "");

		poih.setVersion(poih.getVersion() + 1);
		poih.setFinAdvancePaymentsList(advancePayments);
		AuditHeader auditHeader = getAuditHeader(poih, PennantConstants.TRAN_WF);

		auditHeader = payOrderIssueService.doApprove(auditHeader);

		if (auditHeader.getOverideMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else {
			return APIErrorHandlerService.getSuccessStatus();
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private boolean isOnlinePayment(String paymentType) {
		return DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
				|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType);
	}

	protected AuditHeader getAuditHeader(PayOrderIssueHeader payOrderIssueByHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, payOrderIssueByHeader);
		return new AuditHeader(payOrderIssueByHeader.getFinReference(), null, null, null, auditDetail,
				payOrderIssueByHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	protected AuditHeader getAuditHeader(ChequeHeader chequeHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, chequeHeader);
		return new AuditHeader(chequeHeader.getFinReference(), null, null, null, auditDetail,
				chequeHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> ri) {
		if (CollectionUtils.isNotEmpty(ri)) {
			Collections.sort(ri, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtil.compare(detail1.getRepayDate(), detail2.getRepayDate());
				}
			});
		}
		return ri;
	}

	public FinanceDetail doRestructuring(RestructureDetail rd, FinServiceInstruction fsi, String eventCode) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return fd;
		}

		FinScheduleData fsd = fd.getFinScheduleData();
		FinanceMain fm = fsd.getFinanceMain();

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.RESTRUCTURE);
		fd.setUserAction("");
		fsi.setBaseRate(rd.getBaseRate());
		fsi.setMargin(rd.getMargin());
		fsi.setActualRate(rd.getRepayProfitRate());
		fsi.setRestructuringType(rd.getRestructureType());
		fsi.setTerms(rd.getTotNoOfRestructure());
		fsi.setFromDate(rd.getRestructureDate());
		fsi.setRecalType(rd.getRecalculationType());
		fsi.setReqType(rd.getReqType());
		fsi.setModuleDefiner(FinServiceEvent.RESTRUCTURE);

		if (CalculationConstants.RST_RECAL_ADJUSTTENURE.equals(fsi.getRecalType())) {
			rd.setTenorChange(true);
			rd.setEmiRecal(false);
		} else if (CalculationConstants.RST_RECAL_RECALEMI.equals(fsi.getRecalType())) {
			rd.setTenorChange(false);
			rd.setEmiRecal(true);
		} else {
			rd.setTenorChange(true);
			rd.setEmiRecal(true);
		}

		fd.setModuleDefiner(FinServiceEvent.RESTRUCTURE);

		try {
			List<FinFeeDetail> finFeeDetailList = fsd.getFinFeeDetailList();
			if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
				for (FinFeeDetail ffd : finFeeDetailList) {
					if (AccountingEvent.VAS_FEE.equals(ffd.getFinEvent())) {
						finFeeDetailList.remove(ffd);
					}
					break;
				}
			}

			List<RestructureCharge> chargeList = rd.getChargeList();
			for (RestructureCharge rstChrg : chargeList) {
				if (RepayConstants.ALLOCATION_FEE.equals(rstChrg.getAlocType())) {
					List<FinTypeFees> finTypeFees = financeDetailService.getFinTypeFees(fm.getFinType(), eventCode,
							false, FinanceConstants.MODULEID_FINTYPE);
					for (FinTypeFees fee : finTypeFees) {
						if (StringUtils.equals(fee.getFeeTypeCode(), rstChrg.getFeeCode())) {
							FinFeeDetail ffd = new FinFeeDetail();
							ffd.setFinEvent(fee.getFinEvent());
							ffd.setFeeTypeCode(rstChrg.getFeeCode());
							ffd.setActualAmount(rstChrg.getActualAmount());
							ffd.setOriginationFee(fee.isOriginationFee());
							ffd.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_SALE_PRICE);
							ffd.setAlwModifyFee(fee.isAlwModifyFee());
							fsd.getFinFeeDetailList().add(ffd);
							fsi.getFinFeeDetails().add(ffd);
						}
					}
				}
			}

			executeFeeCharges(fd, fsi, eventCode);

			List<ErrorDetail> errorDetails = fd.getFinScheduleData().getErrorDetails();

			if (errorDetails != null) {
				for (ErrorDetail ed : errorDetails) {
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				List<RestructureCharge> newChrgList = rd.getChargeList();
				List<RestructureCharge> defChrgList = restructureService.getRestructureChargeList(fsd,
						rd.getRestructureDate());

				if (CollectionUtils.isEmpty(newChrgList)) {
					rd.setChargeList(defChrgList);
				} else {
					rd.setChargeList(getFinalChargeList(newChrgList, defChrgList));
				}

				if (!restructureService.checkLoanDues(rd.getChargeList())) {
					String[] valueParm = new String[1];
					valueParm[0] = Labels.getLabel("label_Restructure_LoanDues_Validation");
					ErrorDetail ed = ErrorUtil.getErrorDetail(new ErrorDetail("92021", valueParm));
					FinanceDetail response = new FinanceDetail();
					doEmptyResponseObject(response);
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));

					return response;
				}
			}

			fsd.setRestructureDetail(rd);
			fsd.getFinanceMain().setDevFinCalReq(false);
			fsd = restructureService.doRestructure(fsd, fsi);

			if (fsd.getErrorDetails() != null) {
				for (ErrorDetail ed : fsd.getErrorDetails()) {
					FinanceDetail response = new FinanceDetail();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError()));
					return response;
				}
			}

			fd.setFinScheduleData(fsd);
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("9998", ex.getMessage()));
			return response;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public List<RestructureCharge> getFinalChargeList(List<RestructureCharge> newChrgList,
			List<RestructureCharge> defChrgList) {
		logger.debug(Literal.ENTERING);

		List<RestructureCharge> finalChrgList = new ArrayList<>();

		for (RestructureCharge newRstChrg : newChrgList) {
			String newAllocType = StringUtils.trimToEmpty(newRstChrg.getAlocType());
			String newFeeCode = StringUtils.trimToEmpty(newRstChrg.getFeeCode());
			boolean newCap = newRstChrg.isCapitalized();

			for (RestructureCharge rstChrg : defChrgList) {
				String rsAllocType = rstChrg.getAlocType();
				String rsFeeCode = rstChrg.getFeeCode();

				switch (rsAllocType) {
				case RepayConstants.ALLOCATION_PFT:
				case RepayConstants.ALLOCATION_PRI:
				case "BPI":
					if (!finalChrgList.contains(rstChrg)) {
						finalChrgList.add(rstChrg);
					}
					break;
				case RepayConstants.ALLOCATION_MANADV:
					if (RepayConstants.ALLOCATION_MANADV.equals(newAllocType) && newFeeCode.equals(rsFeeCode)
							&& newCap) {
						if (!finalChrgList.contains(rstChrg)) {
							rstChrg.setCapitalized(newCap);
							finalChrgList.add(rstChrg);
						}
					}
					break;
				case RepayConstants.ALLOCATION_FEE:
					if (!finalChrgList.contains(rstChrg) && RepayConstants.ALLOCATION_FEE.equals(newAllocType)
							&& newFeeCode.equals(rsFeeCode) && newCap) {
						rstChrg.setActualAmount(newRstChrg.getActualAmount());
						finalChrgList.add(rstChrg);
					}
					break;
				case RepayConstants.ALLOCATION_BOUNCE:
				case RepayConstants.ALLOCATION_LPFT:
				case RepayConstants.ALLOCATION_ODC:
					if ((RepayConstants.ALLOCATION_ODC.equals(newAllocType)
							|| RepayConstants.ALLOCATION_LPFT.equals(newAllocType)) && rsFeeCode == null) {
						rstChrg.setFeeCode(newFeeCode);
						rsFeeCode = rstChrg.getFeeCode();
					}
					if (newFeeCode.equals(rsFeeCode) && !finalChrgList.contains(rstChrg) && newCap) {
						rstChrg.setCapitalized(newCap);
						finalChrgList.add(rstChrg);
					}
					break;
				default:
					break;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return finalChrgList;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRateChangeService(RateChangeService rateChangeService) {
		this.rateChangeService = rateChangeService;
	}

	public void setAddRepaymentService(AddRepaymentService addRepaymentService) {
		this.addRepaymentService = addRepaymentService;
	}

	public void setRmvTermsService(RemoveTermsService rmvTermsService) {
		this.rmvTermsService = rmvTermsService;
	}

	public void setRecalService(RecalculateService recalService) {
		this.recalService = recalService;
	}

	public void setChangeProfitService(ChangeProfitService changeProfitService) {
		this.changeProfitService = changeProfitService;
	}

	public void setAddDisbursementService(AddDisbursementService addDisbursementService) {
		this.addDisbursementService = addDisbursementService;
	}

	public void setChangeFrequencyService(ChangeFrequencyService changeFrequencyService) {
		this.changeFrequencyService = changeFrequencyService;
	}

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setPostponementService(PostponementService postponementService) {
		this.postponementService = postponementService;
	}

	public void setFeeDetailService(FeeDetailService feeDetailService) {
		this.feeDetailService = feeDetailService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	public void setRepayCalculator(RepayCalculator repayCalculator) {
		this.repayCalculator = repayCalculator;
	}

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	@Override
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFeeReceiptService(FeeReceiptService feeReceiptService) {
		this.feeReceiptService = feeReceiptService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Autowired
	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	@Autowired
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	@Autowired
	public void setFinAdvancePaymensDAO(FinAdvancePaymentsDAO finAdvancePaymensDAO) {
		this.finAdvancePaymensDAO = finAdvancePaymensDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public void setPayOrderIssueService(PayOrderIssueService payOrderIssueService) {
		this.payOrderIssueService = payOrderIssueService;
	}

	public void setNonLanReceiptService(NonLanReceiptService nonLanReceiptService) {
		this.nonLanReceiptService = nonLanReceiptService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	public void setRestructureService(RestructureService restructureService) {
		this.restructureService = restructureService;
	}

	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

}
