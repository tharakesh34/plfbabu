package com.pennanttech.controller;

import java.math.BigDecimal;
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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
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
import com.pennant.backend.dao.apicollecetiondetails.CollectionAPIDetailDAO;
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
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
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
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.mandate.ChequeSatus;
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
import com.pennanttech.pff.model.external.collection.CollectionAPIDetail;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.collection.CollectionAccountDetails;
import com.pennanttech.ws.model.finance.DisbRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

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
	private LimitDetailDAO limitDetailDAO;
	private CollectionAPIDetailDAO collectionAPIDetailDAO;
	private PostingsDAO postingsDAO;

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
			fsi.setFinEvent(FinServiceEvent.ADDDISB);
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
				schdData.setDisbursementDetails(list);
				List<ErrorDetail> errors = finAdvancePaymentsService.validateFinAdvPayments(fd, true);
				schdData.setDisbursementDetails(new ArrayList<>());
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
				int paymentSeq = finAdvancePaymentsService.getCountByFinReference(finID) + 1;
				for (FinAdvancePayments advPayment : advancePayments) {
					advPayment.setFinID(finID);
					advPayment.setFinReference(fm.getFinReference());
					advPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					advPayment.setNewRecord(true);
					advPayment.setLastMntBy(userDetails.getUserId());
					advPayment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					advPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					advPayment.setUserDetails(fm.getUserDetails());
					advPayment.setPaymentSeq(paymentSeq++);
					advPayment.setDisbCCy(fm.getFinCcy());

					String paymentType = advPayment.getPaymentType();
					if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_RTGS.equals(paymentType)
							|| DisbursementConstants.PAYMENT_TYPE_IFT.equals(paymentType)) {

						String ifsc = advPayment.getiFSC();
						String micr = advPayment.getMicr();
						String bankCode = advPayment.getBankCode();
						String branchCode = advPayment.getBranchCode();

						BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

						if (bankBranch.getError() != null) {
							fd.getFinScheduleData().getErrorDetails().add(bankBranch.getError());
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

		FinanceDetail fd = getFinanceDetails(fsi, eventCode);

		if (fd == null) {
			fd = new FinanceDetail();
			fd.setReturnStatus(APIErrorHandlerService.getFailedStatus());

			logger.debug(Literal.LEAVING);
			return fd;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String repayFrq = fm.getRepayFrq();
		String frqday = String.valueOf(fsi.getFrqDay());
		frqday = frqday.length() == 1 ? "0".concat(frqday) : frqday;
		frqday = StringUtils.substring(repayFrq, 0, repayFrq.length() - 2).concat(frqday);

		fsi.setRepayFrq(frqday);

		int rpyTermsCompleted = 0;
		int adjRepayTerms = 0;
		int totRepayTerms = 0;
		boolean isFromDateFound = false;
		Date fromDate = DateUtil.getDatePart(fsi.getFromDate());

		fsi.setFromDate(fromDate);

		if (fm.isAllowGrcPeriod() && DateUtil.compare(fromDate, fm.getGrcPeriodEndDate()) <= 0) {
			repayFrq = fm.getGrcPftFrq();
		}

		int repayFrqDay = FrequencyUtil.getIntFrequencyDay(repayFrq);
		int changeFrqDay = FrequencyUtil.getIntFrequencyDay(frqday);

		boolean bpiRecal = CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fsi.getRecalType());

		if (bpiRecal && changeFrqDay < repayFrqDay) {
			StringBuilder errMsg = new StringBuilder("");
			errMsg.append("Additional BPI is not allowed from new frequency ");
			errMsg.append(changeFrqDay);
			errMsg.append(" to old frequency ");
			errMsg.append(repayFrqDay);

			return getAPIError("9998", errMsg.toString());
		}

		if (bpiRecal && fm.isStepFinance()) {
			return getAPIError("9998", "Additional BPI is not allowed for step loans");
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		Date grcPrdEndDate = fm.getGrcPeriodEndDate();

		for (FinanceScheduleDetail curSchd : schedules) {
			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
					isFromDateFound = true;
				}

				totRepayTerms = totRepayTerms + 1;
				if (!isFromDateFound) {
					if (curSchd.getSchDate().compareTo(grcPrdEndDate) > 0) {
						rpyTermsCompleted = rpyTermsCompleted + 1;
					}
				}
			}
		}

		adjRepayTerms = totRepayTerms - rpyTermsCompleted;

		fsi.setAdjRpyTerms(adjRepayTerms);
		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.CHGFRQ);
		fsi.setModuleDefiner(FinServiceEvent.CHGFRQ);
		fd.setModuleDefiner(FinServiceEvent.CHGFRQ);

		try {
			// execute fee charges
			executeFeeCharges(fd, fsi, eventCode);
			List<ErrorDetail> errors = schdData.getErrorDetails();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail ed = errors.get(0);
				return getAPIError(ed.getCode(), ed.getError());
			}

			// call change frequency service
			fd.setFinScheduleData(changeFrequencyService.doChangeFrequency(schdData, fsi));

			// Get the response
			fd = getResponse(fd, fsi);

		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			return getAPIError("9998", ex.getMessage());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			FinanceDetail response = new FinanceDetail();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		return fd;
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

	public WSReturnStatus updateLoanPenaltyDetails(FinODPenaltyRate pr) {
		logger.debug(Literal.ENTERING);
		try {
			List<FinODPenaltyRate> list = finODPenaltyRateDAO.getFinODPenaltyRateByRef(pr.getFinID(), "");

			FinODPenaltyRate effectiveDue = null;
			FinODPenaltyRate otherDue = null;

			for (FinODPenaltyRate penaltyRate : list) {
				if ("E".equals(penaltyRate.getODChargeType())) {
					effectiveDue = penaltyRate;
					if (effectiveDue != null && effectiveDue.getFinEffectDate().compareTo(pr.getFinEffectDate()) == 0) {
						finODPenaltyRateDAO.saveLog(effectiveDue, "_Log");
						finODPenaltyRateDAO.update(pr, "");
					} else {
						finODPenaltyRateDAO.save(pr, "");
					}
				} else {
					otherDue = penaltyRate;
				}
			}

			if (otherDue != null) {
				finODPenaltyRateDAO.saveLog(otherDue, "_Log");
				finODPenaltyRateDAO.update(pr, "");
				logger.debug(Literal.LEAVING);
				return APIErrorHandlerService.getSuccessStatus();
			}

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

		if (geDate.before(DateUtil.addDays(appDate, 1))) {
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
		Date appDate = SysParamUtil.getAppDate();
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtil.addDays(appDate, 1))) {
			String[] valueParm = new String[2];
			valueParm[0] = param + "EndDate: "
					+ financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			valueParm[1] = "AppDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", valueParm)));
		}
		if (financeDetail.getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()
				.before(DateUtil.addDays(appDate, 1))) {
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
			if (curSchd.getSchDate().compareTo(appDate) < 0) {
				validFrom = appDate;
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

		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();

		if (StringUtils.equals(finServiceInst.getReqType(), APIConstants.REQTYPE_POST)) {
			int version = financeDetail.getFinScheduleData().getFinanceMain().getVersion();
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(version + 1);
			financeDetail.getFinScheduleData().setSchduleGenerated(true);
			financeDetail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));

			AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
			FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			aFinanceDetail = prepareInstructionObject(aFinanceDetail);

			if (CollectionUtils.isNotEmpty(stepPolicyDetails)
					&& AccountingEvent.RESTRUCTURE.equals(aFinanceDetail.getAccountingEventCode())) {
				aFinanceDetail.getFinScheduleData().setStepPolicyDetails(stepPolicyDetails);
			}

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
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
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
		if (!StringUtils.equals(receiptMode, ReceiptMode.CASH) && !StringUtils.equals(receiptMode, ReceiptMode.CHEQUE)
				&& !StringUtils.equals(receiptMode, ReceiptMode.DD)
				&& !StringUtils.equals(receiptMode, ReceiptMode.NEFT)
				&& !StringUtils.equals(receiptMode, ReceiptMode.RTGS)
				&& !StringUtils.equals(receiptMode, ReceiptMode.IMPS)
				&& !StringUtils.equals(receiptMode, ReceiptMode.ESCROW)
				&& !StringUtils.equals(receiptMode, ReceiptMode.ONLINE)
				&& !StringUtils.equals(receiptMode, ReceiptMode.DIGITAL)) {

			valueParm = new String[2];
			valueParm[0] = "Receipt mode";
			valueParm[1] = ReceiptMode.CASH + "," + ReceiptMode.CHEQUE + "," + ReceiptMode.DD + "," + ReceiptMode.NEFT
					+ "," + ReceiptMode.RTGS + "," + ReceiptMode.IMPS + "," + ReceiptMode.ESCROW + ReceiptMode.ONLINE
					+ "," + ReceiptMode.DIGITAL;
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
			if (DateUtil.compare(fsi.getReceiptDetail().getReceivedDate(), appDate) > 0) {
				valueParm = new String[1];
				valueParm[0] = DateUtil.formatToLongDate(appDate);
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

		if (ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode)) {

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
				if (DateUtil.compare(finReceiptDetail.getValueDate(), appDate) > 0) {
					valueParm = new String[1];
					valueParm[0] = DateUtil.formatToLongDate(appDate);
					return APIErrorHandlerService.getFailedStatus("RU0007", valueParm);
				}
			}
			if (fsi.isNonStp()) {
				if (fsi.getRealizationDate() == null) {
					valueParm = new String[1];
					valueParm[0] = "realizationDate";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);
				} else {
					if (DateUtil.compare(fsi.getRealizationDate(), finReceiptDetail.getValueDate()) < 0) {
						valueParm = new String[1];
						valueParm[0] = DateUtil.formatToLongDate(finReceiptDetail.getValueDate());
						return APIErrorHandlerService.getFailedStatus("RU0019", valueParm);
					}
					if (DateUtil.compare(fsi.getRealizationDate(), appDate) > 0) {
						valueParm = new String[2];
						valueParm[0] = "realizationDate";
						valueParm[1] = DateUtil.formatToLongDate(appDate);
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
			// IFSC code (bank branch) validation
			String ifsc = finReceiptDetail.getiFSC();
			String micr = finReceiptDetail.getMicr();
			String bankCode = finReceiptDetail.getBankCode();
			String branchCode = finReceiptDetail.getBankBranchCode();

			BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

			if (bankBranch.getError() != null) {
				WSReturnStatus status = new WSReturnStatus();
				status.setReturnCode(bankBranch.getError().getCode());
				status.setReturnText("");

				ErrorDetail ed = ErrorUtil.getErrorDetailById(bankBranch.getError().getCode());

				if (ed != null) {
					status.setReturnText(
							ErrorUtil.getErrorMessage(ed.getMessage(), bankBranch.getError().getParameters()));
				}

				return status;
			}

			finReceiptDetail.setBankBranchID(bankBranch.getBankBranchID());
		} else {
			// need to empty the data wich is not req
			fsi.setRealizationDate(null);
			finReceiptDetail.setFavourName("");
			finReceiptDetail.setFavourNumber("");
			finReceiptDetail.setChequeAcNo("");
		}
		// In Case of online mode transactionRef is mandatory
		if (StringUtils.equals(receiptMode, ReceiptMode.NEFT) || StringUtils.equals(receiptMode, ReceiptMode.RTGS)
				|| StringUtils.equals(receiptMode, ReceiptMode.IMPS)
				|| StringUtils.equals(receiptMode, ReceiptMode.ESCROW)
				|| StringUtils.equals(receiptMode, ReceiptMode.DIGITAL)) {
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
		String reference = StringUtils.isNotBlank(finReference) ? finReference : fsi.getExternalReference();
		if (StringUtils.isBlank(reference)) {
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

				PostingDTO postingDTO = new PostingDTO();
				postingDTO.setFinanceDetail(fd);
				postingDTO.setUserBranch(fm.getFinBranch());

				AccountingEngine.post(AccountingEvent.DISBINS, postingDTO);

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

	public FinanceDetail doProcessNonLanReceipt(FinReceiptData receiptData, String receiptPurpose) {
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

		if (ReceiptMode.ONLINE.equals(receiptMode)) {
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
				ErrorUtil.setError(finScheduleData, auditErrorDetail.getCode(), auditErrorDetail.getError());
				return financeDetail;
			}
		}

		receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		finServiceInst.setReceiptId(receiptData.getReceiptHeader().getReceiptID());
		financeDetail.setReceiptId(receiptData.getReceiptHeader().getReceiptID());

		List<FinServiceInstruction> finServInstList = new ArrayList<>();
		Date appDate = SysParamUtil.getAppDate();
		Date sysDate = DateUtil.getSysDate();

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

		List<FeeWaiverDetail> updatedFeeWaivers = new ArrayList<>();

		for (FeeWaiverDetail feeWaiverDetail : feeWaiverDetails) {

			for (FeeWaiverDetail fwd : actaulfeeWaiverDetails) {
				if (StringUtils.equals(feeWaiverDetail.getFeeTypeCode(), fwd.getFeeTypeCode())) {
					BigDecimal currWaiverAmount = feeWaiverDetail.getCurrWaiverAmount();
					fwd.setCurrWaiverAmount(currWaiverAmount);
					prepareGST(fwd, currWaiverAmount);
					fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
					updatedFeeWaivers.add(fwd);
					actaulfeeWaiverDetails.remove(fwd);
					break;
				}
			}

		}

		feeWaiver.setFeeWaiverDetails(updatedFeeWaivers);

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
		logger.debug(Literal.ENTERING);

		FinanceMain fm = new FinanceMain();
		fm.setFinID(actualWaiverDetail.getFinID());

		Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(fm);

		actualWaiverDetail.setWaivedAmount(waiverAmount);
		actualWaiverDetail.setCurrWaiverAmount(waiverAmount);
		actualWaiverDetail.setCurrActualWaiver(waiverAmount);
		actualWaiverDetail.setCurrWaiverGST(BigDecimal.ZERO);

		if (!actualWaiverDetail.isTaxApplicable()) {
			logger.debug(Literal.LEAVING);
			return;
		}

		/* always taking as Inclusive case here */
		TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(waiverAmount, gstPercentages);
		actualWaiverDetail.setCurrActualWaiver(waiverAmount.subtract(taxSplit.gettGST()));
		actualWaiverDetail.setCurrWaiverGST(taxSplit.gettGST());

		if (actualWaiverDetail.getTaxHeader() == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<Taxes> taxDetails = actualWaiverDetail.getTaxHeader().getTaxDetails();

		if (CollectionUtils.isEmpty(taxDetails)) {
			logger.debug(Literal.LEAVING);
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

		logger.debug(Literal.LEAVING);
	}

	protected AuditHeader getAuditHeader(FeeWaiverHeader feeWaiver, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, feeWaiver);
		return new AuditHeader(feeWaiver.getFinReference(), null, null, null, auditDetail, feeWaiver.getUserDetails(),
				new HashMap<>());
	}

	public WSReturnStatus processChequeDetail(FinanceDetail fd, String tableType) {
		logger.debug(Literal.ENTERING);

		ChequeHeader ch = fd.getChequeHeader();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		if (StringUtils.isNotBlank(tableType)) {
			ch.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			ch.setNewRecord(true);
			ch.setVersion(1);
		} else {
			ch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			ch.setNewRecord(false);
			ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}

		prepareChequeHeader(ch, fm, loggedInUser);

		List<ChequeDetail> cheques = ch.getChequeDetailList();
		int serialNum = Integer.valueOf(ch.getChequeSerialNumber());

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		for (ChequeDetail cheque : cheques) {
			prepareChequeDetails(tableType, ch, fm, loggedInUser, ccy, cheque);

			cheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cheque.setNewRecord(true);
			serialNum = serialNum + 1;
			cheque.setChequeSerialNumber(StringUtils.leftPad("" + serialNum, 6, "0"));

			ch.setTotalAmount(ch.getTotalAmount().add(cheque.getAmount()));
		}

		return processCheques(tableType, ch);

	}

	public WSReturnStatus updateCheque(FinanceDetail fd, String tableType) {
		ChequeHeader ch = fd.getChequeHeader();

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		if (StringUtils.isNotBlank(tableType)) {
			ch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		} else {
			ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}

		ch.setNewRecord(false);
		ch.setTotalAmount(BigDecimal.ZERO);
		prepareChequeHeader(ch, fm, loggedInUser);

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		List<ChequeDetail> cheques = ch.getChequeDetailList();
		int serialNum = Integer.valueOf(ch.getChequeSerialNumber());

		for (ChequeDetail cheque : cheques) {
			if (cheque.isDelete()) {
				processChequeDelete(ch, cheque);
			} else {
				prepareChequeDetails(tableType, ch, fm, loggedInUser, ccy, cheque);

				serialNum = serialNum + 1;
				cheque.setChequeSerialNumber(StringUtils.leftPad("" + serialNum, 6, "0"));
				ch.setTotalAmount(ch.getTotalAmount().add(cheque.getAmount()));
			}
		}

		return processCheques(tableType, ch);
	}

	public WSReturnStatus updateChequeDetailsinMaintainence(FinanceDetail fd, String type) {
		ChequeHeader ch = fd.getChequeHeader();

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		ch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		ch.setNewRecord(false);
		ch.setTotalAmount(BigDecimal.ZERO);

		prepareChequeHeader(ch, fm, loggedInUser);

		List<ChequeDetail> existingCheques = chequeDetailDAO.getChequeDetailList(ch.getHeaderID(), type);

		List<ChequeDetail> cheques = ch.getChequeDetailList();

		String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		for (ChequeDetail cheque : cheques) {
			if (cheque.isDelete()) {
				processChequeDelete(ch, cheque);
			} else {
				prepareChequeDetails(type, ch, fm, loggedInUser, ccy, cheque);

				for (ChequeDetail chqDetail : existingCheques) {
					if (chqDetail.getChequeDetailsID() == cheque.getChequeDetailsID()) {
						cheque.setChequeSerialNumber((chqDetail.getChequeSerialNumber()));
					}
				}

				ch.setTotalAmount(ch.getTotalAmount().add(cheque.getAmount()));
			}
		}

		return processCheques(type, ch);
	}

	private void prepareChequeHeader(ChequeHeader ch, FinanceMain fm, LoggedInUser loggedInUser) {
		ch.setLastMntBy(loggedInUser.getUserId());
		ch.setRecordStatus(fm.getRecordStatus());
		ch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ch.setTaskId(fm.getTaskId());
		ch.setNextTaskId(fm.getNextTaskId());
		ch.setRoleCode(fm.getRoleCode());
		ch.setNextRoleCode(fm.getNextRoleCode());
		ch.setWorkflowId(fm.getWorkflowId());
		ch.setActive(true);
		ch.setSourceId(PennantConstants.FINSOURCE_ID_API);
		ch.setFinID(fm.getFinID());
		ch.setFinReference(fm.getFinReference());
	}

	private void prepareChequeDetails(String type, ChequeHeader ch, FinanceMain fm, LoggedInUser loggedInUser,
			String ccy, ChequeDetail cheque) {
		if (StringUtils.isNotBlank(type)) {
			cheque.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			cheque.setNewRecord(true);
		} else {
			cheque.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			cheque.setNewRecord(false);
		}

		cheque.setLastMntBy(loggedInUser.getUserId());
		cheque.setRecordStatus(fm.getRecordStatus());
		cheque.setVersion(fm.getVersion());
		cheque.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		cheque.setTaskId(fm.getTaskId());
		cheque.setNextTaskId(fm.getNextTaskId());
		cheque.setRoleCode(fm.getRoleCode());
		cheque.setNextRoleCode(fm.getNextRoleCode());
		cheque.setWorkflowId(fm.getWorkflowId());

		cheque.setBankBranchID(ch.getBankBranchID());
		cheque.setAccHolderName(ch.getAccHolderName());
		cheque.setAccountNo(ch.getAccountNo());

		cheque.setStatus(ChequeSatus.NEW);
		cheque.setChequeStatus(ChequeSatus.NEW);
		cheque.setChequeCcy(ccy);
		cheque.setActive(true);
	}

	private void processChequeDelete(ChequeHeader ch, ChequeDetail cheque) {
		ch.setNoOfCheques(ch.getNoOfCheques() - 1);
		ch.setTotalAmount(ch.getTotalAmount().subtract(cheque.getAmount()));

		cheque.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		cheque.setActive(false);
		cheque.setNewRecord(false);
		cheque.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
		cheque.setChequeCcy(SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
		cheque.setStatus(ChequeSatus.NEW);

		chequeHeaderService.doApprove(getAuditHeader(ch, PennantConstants.TRAN_WF));
	}

	private WSReturnStatus processCheques(String tableType, ChequeHeader ch) {
		AuditHeader auditHeader = getAuditHeader(ch, PennantConstants.TRAN_WF);

		if (StringUtils.isNotBlank(tableType)) {
			auditHeader = chequeHeaderService.saveOrUpdate((auditHeader));
		} else {
			auditHeader = chequeHeaderService.doApprove(auditHeader);
		}

		List<ErrorDetail> errors = auditHeader.getErrorMessage();
		if (CollectionUtils.isEmpty(errors)) {
			return APIErrorHandlerService.getSuccessStatus();
		}

		ErrorDetail error = errors.get(errors.size() - 1);

		return APIErrorHandlerService.getFailedStatus(error.getCode(), error.getError());
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
				payOrderIssueByHeader.getUserDetails(), new HashMap<>());
	}

	protected AuditHeader getAuditHeader(ChequeHeader ch, String tranType) {
		AuditHeader ah = new AuditHeader(ch.getFinReference(), null, null, null, new AuditDetail(tranType, 1, null, ch),
				ch.getUserDetails(), new HashMap<>());

		ah.setApiHeader(PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY));

		return ah;
	}

	public WSReturnStatus reqCashierEntry(CollectionAccountDetails detail, String type) {
		logger.debug(Literal.ENTERING);

		CollectionAPIDetail cad = new CollectionAPIDetail();

		long receiptId = detail.getReceiptId();
		String finReference = detail.getFinReference();
		long finID = detail.getFinID();

		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeader(receiptId);
		// bean preparation
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		cad.setAmount(rch.getReceiptAmount());
		cad.setFinReference(finReference);
		cad.setApiID(reqHeaderDetails.getSeqId());
		cad.setReceiptID(receiptId);
		cad.setServiceName(type);
		cad.setModuleCode(detail.getModuleType());

		// Save to data base for logging purpose
		collectionAPIDetailDAO.save(cad);
		long linkedTranId = executeAccountingProcess(finID, rch.getReceiptAmount(), rch.getReceiptMode(), type,
				receiptId);

		if (linkedTranId > 0) {
			logger.debug(Literal.LEAVING);
			collectionAPIDetailDAO.update(linkedTranId, type, receiptId);
			if (AccountingEvent.CSH2BANK.equals(type)) {
				finReceiptDetailDAO.updatePartnerBankByReceiptId(receiptId, detail.getPartnerBankId());
			}
			return APIErrorHandlerService.getSuccessStatus();
		} else {
			return APIErrorHandlerService.getFailedStatus();
		}

	}

	private long executeAccountingProcess(long finID, BigDecimal receiptAmount, String receiptMode, String type,
			long receiptId) {

		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = prepareAccSetData(finID, receiptAmount, receiptMode, type);
		aeEvent.setPostRefId(receiptId);
		long postingId = postingsDAO.getPostingId();
		aeEvent.setPostingId(postingId);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please ");
		}
		logger.debug(Literal.LEAVING);

		return aeEvent.getLinkedTranId();

	}

	private AEEvent prepareAccSetData(long finID, BigDecimal receiptAmount, String receiptMode, String type) {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(type);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Finance main
		FinanceMain fm = manualAdviseDAO.getFinanceDetails(finID);
		amountCodes.setFinType(fm.getFinType());

		aeEvent.setPostingUserBranch(fm.getFinBranch());
		Date appDate = SysParamUtil.getAppDate();
		aeEvent.setValueDate(appDate);
		aeEvent.setPostDate(appDate);
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();

		eventMapping.put("PB_ReceiptAmount", receiptAmount);
		eventMapping.put("ae_receiptmode", receiptMode);
		aeEvent.setDataMap(eventMapping);
		long accountsetId = AccountingConfigCache.getAccountSetID(fm.getFinType(), type,
				FinanceConstants.MODULEID_FINTYPE);
		aeEvent.getAcSetIDList().add(accountsetId);

		logger.debug(Literal.LEAVING);

		return aeEvent;
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
		fm.setOldSchedules(financeDetailService.getFinScheduleList(fm.getFinID()));

		fm.setFinSourceID(APIConstants.FINSOURCE_ID_API);
		fm.setRcdMaintainSts(FinServiceEvent.RESTRUCTURE);
		fd.setUserAction("");
		fsi.setBaseRate(rd.getBaseRate());
		fsi.setMargin(rd.getMargin());
		fsi.setActualRate(rd.getRepayProfitRate());
		String restructureType = rd.getRestructureType();
		fsi.setRestructuringType(restructureType);
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
				if (Allocation.FEE.equals(rstChrg.getAlocType())) {
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

				// Service details calling for Schedule calculation
				fsd.setRestructureDetail(rd);
				fsd.getFinanceMain().setDevFinCalReq(false);

				if ("9".equals(restructureType) || "10".equals(restructureType) || "11".equals(restructureType)) {
					fm.setNoOfSteps(rd.getNoOfSteps());
					fm.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
					if (!fm.isStepFinance()) {
						fm.setCalcOfSteps(rd.getCalcOfSteps());
						fm.setStepsAppliedFor(rd.getStepsAppliedFor());
						fm.setAlwManualSteps(rd.isAlwManualSteps());
						fm.setStepFinance(true);
					}
					fsd.setStepPolicyDetails(rd.getStepPolicyDetails());
				}

				fsd = restructureService.doRestructure(fsd, fsi);

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
				case Allocation.PFT:
				case Allocation.PRI:
				case "BPI":
					if (!finalChrgList.contains(rstChrg)) {
						finalChrgList.add(rstChrg);
					}
					break;
				case Allocation.MANADV:
					if (Allocation.MANADV.equals(newAllocType) && newFeeCode.equals(rsFeeCode) && newCap) {
						if (!finalChrgList.contains(rstChrg)) {
							rstChrg.setCapitalized(newCap);
							finalChrgList.add(rstChrg);
						}
					}
					break;
				case Allocation.FEE:
					if (!finalChrgList.contains(rstChrg) && Allocation.FEE.equals(newAllocType)
							&& newFeeCode.equals(rsFeeCode) && newCap) {
						rstChrg.setActualAmount(newRstChrg.getActualAmount());
						finalChrgList.add(rstChrg);
					}
					break;
				case Allocation.BOUNCE:
				case Allocation.LPFT:
				case Allocation.ODC:
					if ((Allocation.ODC.equals(newAllocType) || Allocation.LPFT.equals(newAllocType))
							&& rsFeeCode == null) {
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

	private FinanceDetail getAPIError(String code, String message) {
		FinanceDetail response = new FinanceDetail();
		doEmptyResponseObject(response);
		response.setReturnStatus(APIErrorHandlerService.getFailedStatus(code, message));
		return response;
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

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public void setCollectionAPIDetailDAO(CollectionAPIDetailDAO collectionAPIDetailDAO) {
		this.collectionAPIDetailDAO = collectionAPIDetailDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}
}
