/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LoanEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-02-2011 * * Modified
 * Date : 1-02-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-02-2011 s Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinMainReportData;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.finance.DPDEnquiryService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.NotificationLogDetailsService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.finance.putcall.FinOptionService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.service.tds.receivables.TdsReceivablesTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.configuration.vasrecording.VASRecordingDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.pff.finsampling.service.FinSamplingService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.service.ProvisionService;

/**
 * This is the controller class for the /WEB-INF/pages/Reports/FinanceEnquiryHeaderDialogCtrl.zul.
 */
public class FinanceEnquiryHeaderDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -6646226859133636932L;
	private static final Logger logger = LogManager.getLogger(FinanceEnquiryHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinEnqHeaderDialog;
	protected Borderlayout borderlayoutFinEnqHeader;
	public Grid grid_BasicDetails;
	protected Tabpanel tabPanel_dialogWindow;

	protected Textbox finReference_header;
	protected Textbox finStatus_Reason;
	protected Textbox finStatus_header;
	protected Textbox finType_header;
	protected Textbox finCcy_header;
	protected Textbox scheduleMethod_header;
	protected Textbox profitDaysBasis_header;
	protected Textbox finBranch_header;
	protected Textbox custCIF_header;
	protected Label custShrtName;
	protected Label label_window_FinEnqHeaderDialog;
	protected Checkbox reqRePayment;
	protected Row row_ReqRePayment;

	protected Label label_FinEnqHeader_Filter;
	protected Space space_menubar;
	protected Menu menu_filter;
	protected Menupopup menupopup_filter;
	protected Button btnPrint;
	protected Component childWindow;
	protected Menubar menubar;

	protected String enquiryType = "";
	private long finID;
	protected String finReference = "";
	protected String module = "";

	// not auto wired variables
	private FinanceDetailService financeDetailService;
	private EligibilityDetailService eligibilityDetailService;
	private ScoringDetailService scoringDetailService;
	private CheckListDetailService checkListDetailService;
	private FinCovenantTypeService finCovenantTypeService;
	private CollateralSetupService collateralSetupService;
	private FinOptionService finOptionService;
	private OverdrafLoanService overdrafLoanService;

	private ManualPaymentService manualPaymentService;
	private OverdueChargeRecoveryService overdueChargeRecoveryService;
	private SuspenseService suspenseService;
	private FinanceDeviationsService deviationDetailsService;
	private FinFeeDetailService finFeeDetailService;
	private UploadHeaderService uploadHeaderService;

	@Autowired
	private FinSamplingService finSamplingService;

	private List<ValueLabel> enquiryList = PennantStaticListUtil.getEnquiryTypes();
	private List<ValueLabel> mandateList = MandateUtil.getInstrumentTypes();
	private FinanceEnquiryListCtrl financeEnquiryListCtrl = null;
	private VASRecordingDialogCtrl vASRecordingDialogCtrl = null;
	private FinScheduleData finScheduleData;
	private FinanceEnquiry financeEnquiry;
	private FinanceSummary financeSummary;
	private DPDEnquiryService dpdEnquiryService;
	private AssetClassificationService assetClassificationService;
	private ProvisionService provisionService;
	private TdsReceivablesTxnService tdsReceivablesTxnService;
	protected boolean customer360;
	private boolean isModelWindow = false;
	private VASRecordingDAO vASRecordingDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDao;
	private ChequeHeaderService chequeHeaderService;

	int listRows;
	private String assetCode = "";
	private transient Object childWindowDialogCtrl = null;
	private boolean fromApproved;
	private boolean childDialog;
	@Autowired
	private CustomerDataService customerDataService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;

	private NotificationLogDetailsService notificationLogDetailsService;

	@Autowired
	private CovenantsService covenantsService;
	@Autowired
	private FinExcessAmountDAO finExcessAmountDAO;
	@Autowired
	private FinOCRHeaderService finOCRHeaderService;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private JointAccountDetailService jointAccountDetailService;
	@Autowired
	private CreditApplicationReviewService creditApplicationReviewService;
	@Autowired
	private ManualAdviseDAO manualAdviseDAO;

	/**
	 * default constructor.<br>
	 */
	public FinanceEnquiryHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceEnquiryHeaderDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Academic object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinEnqHeaderDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinEnqHeaderDialog);

		try {
			if (arguments.containsKey("isModelWindow")) {
				isModelWindow = (Boolean) arguments.get("isModelWindow");
			}
			if (arguments.containsKey("enquiryType")) {
				this.enquiryType = (String) arguments.get("enquiryType");
			}
			if (arguments.containsKey("fromApproved")) {
				this.fromApproved = (Boolean) arguments.get("fromApproved");
			}
			if (arguments.containsKey("childDialog")) {
				this.childDialog = (Boolean) arguments.get("childDialog");
			}
			if (arguments.containsKey("financeEnquiry")) {
				this.setFinanceEnquiry((FinanceEnquiry) arguments.get("financeEnquiry"));
				this.finID = getFinanceEnquiry().getFinID();
				this.finReference = getFinanceEnquiry().getFinReference();
			}

			if (arguments.containsKey("financeEnquiryListCtrl")) {
				this.setFinanceEnquiryListCtrl((FinanceEnquiryListCtrl) arguments.get("financeEnquiryListCtrl"));
			}

			if (arguments.containsKey("VASRecordingDialog")) {
				this.setvASRecordingDialogCtrl((VASRecordingDialogCtrl) arguments.get("VASRecordingDialog"));
			}

			if (arguments.containsKey("customer360")) {
				customer360 = true;
			}

			if (arguments.containsKey("isModelWindow")) {
				isModelWindow = (Boolean) arguments.get("isModelWindow");
			}

			// Method for recall Enquiries
			doFillDialogWindow();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinEnqHeaderDialog.onClose();
		}
		logger.debug("Leavinging" + event.toString());

	}

	/**
	 * Method for appending Child window on selection of Menu Enquiry
	 * 
	 * @throws InterruptedException
	 */
	public void doFillDialogWindow() throws InterruptedException {
		logger.debug("Entering");

		FinanceEnquiry enquiry = getFinanceEnquiry();

		this.finReference_header.setValue(enquiry.getFinReference());

		this.setModule("LOAN");
		this.finStatus_header.setValue(enquiry.getFinStatus());

		if (enquiry.isFinIsActive()) {
			this.finStatus_header.setValue("Active");
		} else {
			this.finStatus_header.setValue("Matured");
		}

		if (StringUtils.contains(enquiry.getRecordStatus(), "Reject") && !enquiry.isFinIsActive()) {
			this.finStatus_header.setValue(Labels.getLabel("label_Rejected"));
		}

		String closingStatus = StringUtils.trimToEmpty(enquiry.getClosingStatus());
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Normal");
		} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Cancelled");
		} else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Settled");
		}
		if (enquiry.isWriteoffLoan()) {
			this.finStatus_Reason.setValue(Labels.getLabel("label_Written-Off"));
		}

		this.custCIF_header.setValue(enquiry.getLovDescCustCIF());
		this.custShrtName.setValue(enquiry.getLovDescCustShrtName());
		this.custShrtName.setStyle("margin-left:10px; display:inline-block; padding-top:5px; white-space:nowrap;");
		this.finType_header.setValue(enquiry.getFinType() + "-" + enquiry.getLovDescFinTypeName());
		this.finCcy_header.setValue(enquiry.getFinCcy());
		this.scheduleMethod_header.setValue(enquiry.getScheduleMethod());
		this.profitDaysBasis_header.setValue(enquiry.getProfitDaysBasis());
		this.finBranch_header.setValue(
				enquiry.getFinBranch() == null ? "" : enquiry.getFinBranch() + "-" + enquiry.getLovDescFinBranchName());

		if (childWindow != null) {
			tabPanel_dialogWindow.getChildren().clear();
			tabPanel_dialogWindow.appendChild(this.grid_BasicDetails);
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeEnquiryHeaderDialogCtrl", this);
		map.put("isModelWindow", isModelWindow);

		String path = "";
		this.grid_BasicDetails.setVisible(true);
		this.btnPrint.setVisible(!customer360);
		this.row_ReqRePayment.setVisible(false);

		if ("FINENQ".equals(this.enquiryType)) {

			FinanceDetail financeDetail = new FinanceDetail();
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinanceEnquiry.value"));
			this.grid_BasicDetails.setVisible(false);
			if (fromApproved) {
				finScheduleData = financeDetailService.getFinSchDataById(this.finID, "_AView", true);
				if (finScheduleData.getFinanceMain() == null) {
					Clients.showNotification("Finance Reference Is Not Valid Create New Finance Reference");
					this.window_FinEnqHeaderDialog.onClose();
				}
				FinanceSummary summary = finScheduleData.getFinanceSummary();
				List<FinFeeDetail> feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finID, false, "");
				calculateFeeChargeDetails(feeDetails, summary);
			} else {
				finScheduleData = getFinanceDetailService().getFinSchDataById(this.finID, "_View", true);
				if (finScheduleData != null) {
					FinanceSummary summary = finScheduleData.getFinanceSummary();
					List<FinFeeDetail> feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finID, false,
							"_View");
					calculateFeeChargeDetails(feeDetails, summary);
				}
			}
			// Collateral Details
			List<CollateralAssignment> collateralAssignmentByFinRef;
			collateralAssignmentByFinRef = getCollateralSetupService().getCollateralAssignmentByFinRef(
					this.finReference, FinanceConstants.MODULE_NAME, fromApproved ? "_AView" : "_View");
			// If Collateral is created from loan and it is not approved
			if (financeDetail.getCollateralAssignmentList() != null) {
				collateralAssignmentByFinRef.addAll(getCollateralSetupService()
						.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_CTView"));

			}
			financeDetail.setCollateralAssignmentList(collateralAssignmentByFinRef);
			financeDetail.setFinAssetTypesList(
					getFinanceDetailService().getFinAssetTypesByFinRef(this.finReference, "_TView"));
			financeDetail.setFinScheduleData(finScheduleData);
			if (finScheduleData.getFinanceMain() != null) {
				financeSummary = getFinanceDetailService().getFinanceProfitDetails(this.finID);
				map.put("financeSummary", financeSummary);

				finScheduleData.getFinanceMain().setLovDescProductCodeName(enquiry.getLovDescProductCodeName());
				map.put("finScheduleData", finScheduleData);
				map.put("financeDetail", financeDetail);
				map.put("fromApproved", fromApproved);
				map.put("enquiryType", this.enquiryType);
				path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";
			}
		} else if ("SCHENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ScheduleEnquiry.value"));
			if (fromApproved) {
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finID, "_AView", 0);
			} else {
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finID, "_View", 0);
			}
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScheduleDetailsEnquiryDialog.zul";
			this.btnPrint.setVisible(true);
			this.row_ReqRePayment.setVisible(true);
			this.reqRePayment.setChecked(true);

		} else if ("DOCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DocumentEnquiry.value"));
			List<DocumentDetails> finDocuments;
			if (fromApproved) {
				finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "", "");
			} else {
				finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "", "_View");
			}

			map.put("finDocuments", finDocuments);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/DocumentEnquiryDialog.zul";
			this.btnPrint.setVisible(false);

		} else if ("PSTENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_PostingsEnquiry.value"));
			this.btnPrint.setVisible(false);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("enquiry", enquiry);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/PostingsEnquiryDialog.zul";

		} else if ("RPYENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RepaymentEnuiry.value"));
			this.btnPrint.setVisible(false);
			List<FinanceRepayments> financeRepayments = manualPaymentService.getFinRepayList(this.finID);
			map.put("financeRepayments", financeRepayments);
			map.put("finAmountformatter", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/RepayInquiry/RepayEnquiryDialog.zul";

		} else if ("ODCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverdueChargeEnquiry.value"));
			this.btnPrint.setVisible(false);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));

			if (finScheduleData != null) {
				map.put("PenaltyRate", this.finScheduleData.getFinODPenaltyRate());
			}

			path = "/WEB-INF/pages/Enquiry/OverDueInquiry/OverdueDetailList.zul";

		} else if ("SUSENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_SuspenseEnquiry.value"));
			FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(this.finID, true, "", "");
			map.put("suspHead", suspHead);
			path = "/WEB-INF/pages/Enquiry/SuspInquiry/SuspDetailEnquiryDialog.zul";

		} else if ("CHKENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_CheckListEnquiry.value"));
			List<FinanceCheckListReference> financeCheckListReference;
			if (fromApproved) {
				financeCheckListReference = getCheckListDetailService().getCheckListByFinRef(this.finID, "_AView");
			} else {
				financeCheckListReference = getCheckListDetailService().getCheckListByFinRef(this.finID, "_View");
			}

			this.btnPrint.setVisible(false);
			map.put("FinanceCheckListReference", financeCheckListReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/CheckListEnquiry.zul";

		} else if ("ELGENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_EligibilityListEnquiry.value"));
			List<FinanceEligibilityDetail> eligibilityDetails = getEligibilityDetailService()
					.getFinElgDetailList(this.finID);
			map.put("eligibilityList", eligibilityDetails);
			map.put("finAmountformatter", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			this.btnPrint.setVisible(false);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityEnquiryDialog.zul";

		} else if ("SCRENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ScoringListEnquiry.value"));
			btnPrint.setVisible(false);
			List<Object> scoreDetails = getScoringDetailService().getFinScoreDetailList(this.finReference);
			map.put("scoringList", scoreDetails);
			map.put("custTypeCtg", enquiry.getCustTypeCtg());
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScoringEnquiryDialog.zul";

		} else if ("NTFLENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_NotificationLogListEnquiry.value"));
			List<Notification> notificationDetails = getNotificationDetailsService()
					.getNotificationLogDetailList(this.finReference, this.module);
			this.btnPrint.setVisible(false);
			map.put("list", notificationDetails);
			map.put("finID", this.finID);
			map.put("finReference", finReference);
			map.put("module", module);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/NotificationDetailsLogDialog.zul";

		} else if ("PFTENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ProfitListEnquiry.value"));
			FinanceSummary financeSummary = getFinanceDetailService().getFinanceProfitDetails(this.finID);
			map.put("financeSummary", financeSummary);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceProfitEnquiryDialog.zul";

		} else if ("CHQPRNT".equals(this.enquiryType)) {

			// this.window_FinEnqHeaderDialog.setTitle(Labels.getLabel("label_ChequePrintingDialog_Title.value"));
			this.btnPrint.setVisible(false);
			this.menubar.setVisible(false);
			this.space_menubar.setWidth("240px");
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ChequePrintingDialog.value"));
			this.label_FinEnqHeader_Filter.setVisible(false);

			if (fromApproved) {
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finID, "_AView", 0);
			} else {
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finID, "_View", 0);
			}
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ChequePrintingDialog.zul";

		} else if ("RECENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RecommendationsEnquiry.value"));
			map.put("notes", getNotes());
			map.put("control", this);
			map.put("enquiry", true);
			map.put("isFinanceNotes", true);
			map.put("enqiryModule", true);
			path = "/WEB-INF/pages/notes/notes.zul";

		} else if ("DEVENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DeviationEnquiry"));
			List<FinanceDeviations> approvalLoanEnqList = getDeviationDetailsService()
					.getApprovedFinanceDeviations(this.finID);
			List<FinanceDeviations> inprocessLoanEnqList = getDeviationDetailsService()
					.getFinanceDeviations(this.finID);
			map.put("loanEnquiry", "");
			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("approvalLoanEnqList", approvalLoanEnqList);
			map.put("inprocessLoanEnqList", inprocessLoanEnqList);
			path = "/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul";

		} else if ("FINMANDENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinMandateEnquiry"));

			JdbcSearchObject<Mandate> jdbcSearchObject = new JdbcSearchObject<Mandate>();
			jdbcSearchObject.addTabelName("Mandates_View");
			jdbcSearchObject.addFilterEqual("MandateID", enquiry.getMandateID());
			jdbcSearchObject.setSearchClass(Mandate.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<Mandate> list = pagedListService.getBySearchObject(jdbcSearchObject);
			if (!list.isEmpty()) {
				map.put("mandate", list.get(0));
				map.put("fromLoanEnquiry", true);
				map.put("tabPaneldialogWindow", tabPanel_dialogWindow);
				path = "/WEB-INF/pages/Mandate/MandateDialog.zul";
			}
		} else if ("FINSECMANDENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinSecurityMandateEnquiry"));

			JdbcSearchObject<Mandate> jdbcSearchObject = new JdbcSearchObject<Mandate>();
			jdbcSearchObject.addTabelName("Mandates_AView");
			jdbcSearchObject
					.addFilters(new Filter[] { new Filter("OrgReference", enquiry.getFinReference(), Filter.OP_EQUAL),
							new Filter("SecurityMandate", 1, Filter.OP_EQUAL) });
			jdbcSearchObject.setSearchClass(Mandate.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<Mandate> list = pagedListService.getBySearchObject(jdbcSearchObject);
			if (!list.isEmpty()) {
				map.put("mandate", list.get(0));
				map.put("fromLoanEnquiry", true);
				map.put("tabPaneldialogWindow", tabPanel_dialogWindow);
				path = "/WEB-INF/pages/Mandate/SecurityMandateDialog.zul";
			}
		} else if ("ODENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverdueEnquiry"));

			JdbcSearchObject<FinODDetails> jdbcSearchObject = new JdbcSearchObject<FinODDetails>();
			jdbcSearchObject.addTabelName("FinODDetails");
			jdbcSearchObject.addFilterEqual("FinReference", this.finReference);
			jdbcSearchObject.setSearchClass(FinODDetails.class);
			jdbcSearchObject.addSortAsc("FinODSchdDate");
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<FinODDetails> list = pagedListService.getBySearchObject(jdbcSearchObject);

			this.btnPrint.setVisible(false);
			map.put("approvalEnquiry", "");
			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("list", list);
			map.put("FinReference", this.finReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/OverdueEnquiryDialog.zul";

		} else if ("TDSCERENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_TdsCertificateEnquiry"));

			List<TdsReceivablesTxn> tdsReceivablesTxnsList = tdsReceivablesTxnService
					.getTdsReceivablesTxnsByFinRef(this.finReference, TableType.MAIN_TAB);

			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("finReference", this.finReference);
			map.put("list", tdsReceivablesTxnsList);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/TdsCertificateEnquiryDialog.zul";

		} else if ("LTPPENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_LatepayProfitRecovery"));
			this.btnPrint.setVisible(false);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/LatepayProfitEnquiry/LatepayProfitRecoveryList.zul";

		} else if ("COVENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_CovenantEnquiry.value"));
			this.btnPrint.setVisible(false);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/CovenantEnquiryDialog.zul";

			if (ImplementationConstants.COVENANT_MODULE_NEW) {
				List<Covenant> covenants;
				covenants = covenantsService.getCovenants(this.finReference, "Loan", TableType.VIEW);

				FinanceMain financeMain = new FinanceMain();
				financeMain.setFinStartDate(enquiry.getFinStartDate());
				financeMain.setMaturityDate(enquiry.getMaturityDate());

				FinanceDetail financeDetail = new FinanceDetail();

				financeDetail.getFinScheduleData().setFinID(financeMain.getFinID());
				financeDetail.getFinScheduleData().setFinReference(this.finReference);
				financeDetail.getFinScheduleData().setFinanceMain(financeMain);

				financeDetail.setCovenants(covenants);
				map.put("financeDetail", financeDetail);
				map.put("enqiryModule", true);

				path = "/WEB-INF/pages/Finance/Covenant/CovenantsList.zul";
			} else {
				List<FinCovenantType> finCovenants;
				String tableType = "_EView";
				if (fromApproved) {
					tableType = "_EAView";
				}

				finCovenants = finCovenantTypeService.getFinCovenantTypeById(this.finReference, tableType, true);
				map.put("finCovenants", finCovenants);
				map.put("enqModule", true);
				map.put("module", module);
			}
		} else if ("FEEENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinFeeEnquiry.value"));
			List<FinFeeDetail> feeDetails;
			if (fromApproved) {
				feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finID, false, "_AView");
			} else {
				feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finID, false, "_View");
			}

			feeDetails.forEach(ffd -> {
				if (RepayConstants.EXAMOUNTTYPE_ADVEMI.equals(ffd.getFeeTypeCode())) {
					ffd.setTerms(finScheduleData.getFinanceMain().getAdvTerms());
				}
			});

			map.put("feeDetails", feeDetails);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FeeEnquiryDialog.zul";
		} else if ("EXPENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ExpenseEnquiry.value"));
			List<FinExpenseDetails> finExpenseDetails;

			finExpenseDetails = getUploadHeaderService().getFinExpenseDetailById(this.finReference);

			map.put("finExpenseDetails", finExpenseDetails);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ExpenseEnquiryDialog.zul";
		} else if ("LOANEXTDET".equals(this.enquiryType)) {
			logger.debug("Entering");

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ExtendedFieldsEnquiry"));
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			map.put("finaceEnquiry", getFinanceEnquiry());
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ExtendedFieldsEnquiryDialog.zul";

			logger.debug("Leaving");
		} else if ("SAMENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_SamplingEnquiry.value"));
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.setSampling(finSamplingService.getSamplingDetails(this.finReference, "_aview"));

			map.put("financeDetail", financeDetail);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("enqiryModule", true);
			path = "/WEB-INF/pages/Finance/FinanceMain/Sampling/FinSamplingDialog.zul";
		} else if ("VERENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_VerificationEnquiry.value"));
			FinanceDetail fd = new FinanceDetail();
			FinanceMain fm = new FinanceMain();
			fm.setFinCcy(enquiry.getFinCcy());
			fm.setFinID(this.finID);
			fm.setFinReference(this.finReference);
			fd.getFinScheduleData().setFinID(this.finID);
			fd.getFinScheduleData().setFinReference(this.finReference);
			fd.getFinScheduleData().setFinanceMain(fm);
			if (enquiry.getCustID() != 0 && enquiry.getCustID() != Long.MIN_VALUE) {
				fd.setCustomerDetails(customerDataService.getCustomerDetailsbyID(enquiry.getCustID(), true, "_AView"));
			}
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(this.finReference,
					FinanceConstants.MODULE_NAME, FinServiceEvent.ORG, "_TView");
			if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
				fd.getDocumentDetailsList().addAll(documentList);
			} else {
				fd.setDocumentDetailsList(documentList);
			}
			fd.setCollateralAssignmentList(getCollateralSetupService()
					.getCollateralAssignmentByFinRef(this.finReference, FinanceConstants.MODULE_NAME, "_TView"));
			map.put("financeDetail", fd);
			map.put("enqiryModule", true);
			path = "/WEB-INF/pages/Verification/FieldInvestigation/VerificationEnquiryDialog.zul";
		} else if ("FINOPT".equals(this.enquiryType)) {

			FinanceMain fm = new FinanceMain();
			fm.setFinStartDate(enquiry.getFinStartDate());
			fm.setMaturityDate(enquiry.getMaturityDate());

			FinanceDetail fd = new FinanceDetail();
			fd.getFinScheduleData().setFinID(this.finID);
			fd.getFinScheduleData().setFinReference(this.finReference);
			fd.getFinScheduleData().setFinanceMain(fm);

			List<FinOption> finOptions = finOptionService.getFinOptions(this.finID, TableType.VIEW);

			fd.setFinOptions(finOptions);

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinOptionEnquiry.value"));
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("financeDetail", fd);
			map.put("enqiryModule", true);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Finance/FinOption/FinOptionList.zul";

		} else if ("DPDENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DPDEnquiry.value"));
			List<FinStatusDetail> finStatusDetails;

			finStatusDetails = dpdEnquiryService.getFinStatusDetailByRefId(this.finID);

			map.put("finStatusDetails", finStatusDetails);
			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/DPDEnquiryDialog.zul";
		} else if ("EXCESSENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ExcessEnquiry"));
			List<FinExcessAmount> excessDetails = getFinExcessAmountDAO().getExcessAmountsByRef(this.finID);
			List<ManualAdvise> payables = getManualAdviseDAO().getAdvisesList(this.finID, AdviseType.PAYABLE.id(),
					"_View");
			map.put("excessDetails", excessDetails);
			map.put("payables", payables);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ExcessEnquiryDialog.zul";
		} else if ("OCRENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OCREnquiry.value"));
			FinanceDetail financeDetail = new FinanceDetail();
			FinanceMain financeMain = financeMainDAO.getFinanceMain(this.finID, new String[] { "FinType",
					"FinID, FinReference", "FinCcy", "ParentRef", "FinAmount", "FinAssetValue", "FinOcrRequired" }, "");
			FinOCRHeader finOcrHeader = finOCRHeaderService.getFinOCRHeaderByRef(this.finID, "_View");
			if (finOcrHeader != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
				FinOCRHeader parentFinOcrHeader = finOCRHeaderService.getFinOCRHeaderByRef(this.finID, "_View");
				if (parentFinOcrHeader != null) {
					finOcrHeader.setOcrDetailList(parentFinOcrHeader.getOcrDetailList());
				}
			}
			// Finance OCR Details
			financeDetail.setFinOCRHeader(finOcrHeader);
			financeDetail.setFinScheduleData(finScheduleData);
			map.put("financeDetail", financeDetail);
			map.put("enqiryModule", true);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Finance/FinanceMain/FinOCRDialog.zul";
		} else if ("CREENQ".equals(this.enquiryType)) {

			FinanceDetail financeDetail = new FinanceDetail();
			FinanceMain financeMain = financeMainDAO.getFinanceMain(this.finID,
					new String[] { "FinIsActive, FinID, FinReference, FinCategory, LovEligibilityMethod" }, "_View");
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			if (enquiry.getCustID() != 0 && enquiry.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(
						customerDataService.getCustomerDetailsbyID(enquiry.getCustID(), true, "_View"));
			}
			CreditReviewData creditReviewData = null;

			CreditReviewDetails creditReviewDetail = getCreditReviewConfiguration(financeDetail, financeMain);

			if (creditReviewDetail == null) {
				MessageUtil.showMessage(Labels.getLabel("label_Configuraion_NotAvailable.value"));
			} else {
				creditReviewData = this.creditApplicationReviewService.getCreditReviewDataByRef(financeMain.getFinID(),
						creditReviewDetail.getTemplateName(), creditReviewDetail.getTemplateVersion());
				if (creditReviewData == null) {
					MessageUtil.showMessage(Labels.getLabel("label_Data_NotAvailable.value"));
					return;
				}

				List<JointAccountDetail> jointAccountDetailList = new ArrayList<JointAccountDetail>();
				if (fromApproved) {
					jointAccountDetailList = this.jointAccountDetailService.getJoinAccountDetail(financeMain.getFinID(),
							"_AView");
				} else {
					jointAccountDetailList = this.jointAccountDetailService.getJoinAccountDetail(financeMain.getFinID(),
							"_View");
				}

				creditReviewDetail.setExtLiabilitiesjointAccDetails(jointAccountDetailList);
				map.put("financeDetail", financeDetail);
				map.put("creditReviewData", creditReviewData);
				map.put("creditReviewDetails", creditReviewDetail);
				map.put("enqiryModule", true);
				map.put("isReadOnly", true);

				path = "/WEB-INF/pages/Finance/FinanceMain/FinanceSpreadSheet.zul";
			}
		} else if ("RSTENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RestructureEnquiry.value"));
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail = getFinanceDetailService().getServicingFinance(finID, null, FinServiceEvent.RESTRUCTURE,
					SessionUserDetails.getLogiedInUser().getUsername());

			if (financeDetail.getFinScheduleData().getRestructureDetail() == null) {
				closeDialog();
				MessageUtil.showMessage("Restructure Details are not available");
				return;
			}

			map.put("finID", this.finID);
			map.put("finReference", this.finReference);
			map.put("enquirymode", true);
			map.put("financeDetail", financeDetail);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/RestructureEnquiryDialog.zul";
		} else if ("NPAENQ".equals(this.enquiryType)) {
			this.btnPrint.setVisible(false);
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_NPAEnquiry.value"));

			map.put("finReference", this.finReference);
			map.put("assetClassification", assetClassificationService.getAssetClassification(this.finID));
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/npa/NPAEnquiryDialog.zul";
		} else if ("PROVSNENQ".equals(this.enquiryType)) {
			this.btnPrint.setVisible(false);
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ProvisionEnquiry"));

			map.put("enquiry", true);
			map.put("finReference", this.finReference);

			Provision provision = provisionService.getProvisionDetail(this.finReference);
			map.put("provision", provision);
			if (provision == null) {
				MessageUtil.showMessage("Provision Details are not available.");
				provision = new Provision();
			}

			path = "/WEB-INF/pages/FinanceManagement/Provision/ManualProvisioningDialog.zul";
		} else if ("LMTENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverDraftLimitEnquiry.value"));
			List<OverdraftLimitTransation> trnactions = overdrafLoanService.getTransactions(this.finID);

			map.put("trsnsactions", trnactions);
			map.put("finReference", this.finReference);
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			path = "/WEB-INF/pages/Finance/Overdraft/OverdraftTransactionsDialog.zul";
		} else if ("FINCHECKENQ".equals(this.enquiryType)) {
			this.btnPrint.setVisible(false);
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_Cheque_Enquiry.value"));

			ChequeHeader ch = chequeHeaderService.getApprovedChequeHeaderForEnq(this.finID);

			if (ch != null) {
				List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(this.finID,
						"_AView", false);
				map.put("financeSchedules", schedules);
				map.put("chequeHeader", ch);
				map.put("chequeDetail", ch.getChequeDetailList());
				map.put("finID", this.finID);
				map.put("enqiryModule", true);
				map.put("parentTabPanel", tabPanel_dialogWindow);
				map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
				path = "/WEB-INF/pages/Finance/PDC/ChequeDetailDialog.zul";

			}
		}

		if (StringUtils.isNotEmpty(path)) {

			// Child Window Calling
			childWindow = Executions.createComponents(path, tabPanel_dialogWindow, map);

			doFillFilterList();

			if (childDialog) {
				this.window_FinEnqHeaderDialog.setHeight("90%");
				this.window_FinEnqHeaderDialog.setWidth("100%");
				setDialog(DialogType.MODAL);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		}
		logger.debug("Leaving");
	}

	private CreditReviewDetails getCreditReviewConfiguration(FinanceDetail financeDetail, FinanceMain financeMain) {
		CreditReviewDetails creditReviewDetail = new CreditReviewDetails();
		String parameters = SysParamUtil.getValueAsString(SMTParameterConstants.CREDIT_ELG_PARAMS);
		if (StringUtils.isNotBlank(parameters)) {
			if (StringUtils.containsIgnoreCase(parameters, "FinType")) {
				creditReviewDetail.setProduct(financeMain.getFinCategory());
			}
			if (StringUtils.containsIgnoreCase(parameters, "EligibilityMethod")) {
				creditReviewDetail.setEligibilityMethod(financeMain.getLovEligibilityMethod());
			}
			if (StringUtils.containsIgnoreCase(parameters, "EmploymentType")) {
				creditReviewDetail.setEmploymentType(financeDetail.getCustomerDetails().getCustomer().getSubCategory());
			}
		}

		CreditReviewDetails newCreditReviewDetail = this.creditApplicationReviewService
				.getCreditReviewDetailsByLoanType(creditReviewDetail);
		if (newCreditReviewDetail == null && "DEFAULT".equals(financeMain.getLovEligibilityMethod())) {
			creditReviewDetail.setEmploymentType(null);
			newCreditReviewDetail = this.creditApplicationReviewService
					.getCreditReviewDetailsByLoanType(creditReviewDetail);
		}
		return newCreditReviewDetail;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * 
	 * @param feeDetails
	 * @param summary
	 */
	private void calculateFeeChargeDetails(List<FinFeeDetail> feeDetails, FinanceSummary summary) {
		if (feeDetails != null) {
			BigDecimal totPaidFee = BigDecimal.ZERO;
			for (FinFeeDetail feeDetail : feeDetails) {
				summary.setTotalFees(summary.getTotalFees().add(feeDetail.getActualAmount()));
				summary.setTotalWaiverFee(summary.getTotalWaiverFee().add(feeDetail.getWaivedAmount()));
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					totPaidFee = totPaidFee.add(feeDetail.getActualAmount().subtract(feeDetail.getWaivedAmount()));
				} else {
					totPaidFee = totPaidFee.add(feeDetail.getPaidAmount());
				}
				summary.setTotalPaidFee(totPaidFee);
			}
		}
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(this.finReference);
		return notes;
	}

	private void doFillFilterList() {
		logger.debug("Entering");
		this.menupopup_filter.getChildren().clear();
		Menuitem menuitem = null;

		boolean mandate = isMandate(StringUtils.trimToEmpty(getFinanceEnquiry().getFinRepayMethod()), mandateList);

		ChequeHeader chequeHeader = chequeHeaderDAO.getChequeHeaderForEnq(this.finID);
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(chequeDetailDao.getChequeDetailList(chequeHeader.getHeaderID(), "_AView"));
		}

		if (enquiryList != null && enquiryList.size() > 0) {
			for (ValueLabel enquiry : enquiryList) {
				String value = enquiry.getValue();
				if (this.finReference.endsWith("_DP") && ("ASSENQ".equals(value) || "ELGENQ".equals(value)
						|| "SCRENQ".equals(value) || "CHKENQ".equals(value))) {
					continue;
				}
				if ("FINMANDENQ".equals(value) && !mandate) {
					continue;
				}

				if ("FINSECMANDENQ".equals(value) && getFinanceEnquiry().getSecurityMandateID() == null) {
					continue;
				}

				if ("FINCHECKENQ".equals(value) && chequeHeader == null) {
					continue;
				}

				// skipping the OCR Enquiry menu if not applicable
				if ("OCRENQ".equals(value) && !getFinanceEnquiry().isFinOcrRequired()) {
					continue;
				}
				if (!("ASSENQ".equalsIgnoreCase(value)
						&& "NOTAPP".equalsIgnoreCase(StringUtils.trimToEmpty(assetCode)))) {
					menuitem = new Menuitem();
					menuitem.setImage("/images/icons/Old/arrow_blue_right_16x16.gif");
					String label = enquiry.getLabel();
					menuitem.setLabel(label);
					menuitem.setValue(value);
					menuitem.setStyle("font-weight:bold;");
					menuitem.addForward("onClick", this.window_FinEnqHeaderDialog, "onFilterMenuItem", enquiry);
					if (this.enquiryType.equals(value)) {
						this.menu_filter.setLabel(label);
					} else {
						this.menupopup_filter.appendChild(menuitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	private boolean isMandate(String repaymthd, List<ValueLabel> mandateList2) {
		for (ValueLabel valueLabel : mandateList2) {
			if (valueLabel.getValue().equals(repaymthd)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method for OnClick Event on Menu Item Enqiries
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFilterMenuItem(ForwardEvent event) throws InterruptedException {
		if (event.getData() != null) {
			ValueLabel enquiry = (ValueLabel) event.getData();
			this.enquiryType = enquiry.getValue();
			doFillDialogWindow();
			doFillFilterList();
			String label = enquiry.getLabel();
			this.menu_filter.setLabel(label);
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean generateReport(String reportName, Object object, List listData, boolean isRegenerate, int reportType,
			String userName, Window window) throws InterruptedException {

		if (isRegenerate) {
			try {
				createReport(reportName, object, listData, userName, window);
			} catch (AppException e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError(e.getMessage());
				ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
			}
		}

		return false;
	}

	private void createReport(String reportName, Object object, List listData, String userName, Window dialogWindow) {
		logger.debug("Entering");
		try {
			byte[] buf = ReportsUtil.generatePDF(reportName, object, listData, userName);

			boolean reportView = true;
			// Assignments
			if ("AssignmentUploadDetails".equals(reportName) || "AccountMappingUpload".equals(reportName)) {
				reportView = false;
			}
			if (reportView) {
				final Map<String, Object> auditMap = new HashMap<String, Object>();
				auditMap.put("reportBuffer", buf);
				String genReportName = Labels.getLabel(reportName);
				auditMap.put("reportName", StringUtils.isBlank(genReportName) ? reportName : genReportName);
				if (dialogWindow != null) {
					auditMap.put("dialogWindow", dialogWindow);
				}
				if (customer360) {
					auditMap.put("Customer360", true);
				}
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
			}
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e.getMessage());
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "button_Print" button
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		if ("FINENQ".equals(this.enquiryType)) {

			List<Object> list = new ArrayList<Object>();

			Map<String, Object> aruments = new HashMap<>();
			aruments.put("isModelWindow", isModelWindow);
			list.add(aruments);

			FinScheduleListItemRenderer finRender;
			if (finScheduleData != null) {
				finRender = new FinScheduleListItemRenderer();
				List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(finScheduleData);
				list.add(subList1);

				FinMainReportData reportData = new FinMainReportData();
				reportData = reportData.getFinMainReportData(finScheduleData, financeSummary);
				if (customer360) {
					generateReport("FINENQ_FinanceBasicDetail", reportData, list, true, 1, userName,
							window_FinEnqHeaderDialog);
				} else {
					ReportsUtil.generatePDF("FINENQ_FinanceBasicDetail", reportData, list, userName,
							window_FinEnqHeaderDialog);
				}
			}

		} else if ("SCHENQ".equals(this.enquiryType)) {

			List<Object> list = new ArrayList<>();

			FinScheduleListItemRenderer finRender;
			if (finScheduleData != null) {

				// Find Out Fee charge Details on Schedule
				Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
				if (finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0) {
					feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();

					for (FeeRule fee : finScheduleData.getFeeRules()) {
						if (feeChargesMap.containsKey(fee.getSchDate())) {
							ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						} else {
							ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						}
					}
				}

				// Find Out Finance Repayment Details & Penalty Details on
				// Schedule
				Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
				Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
				if (finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0) {
					rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();
					penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

					// Repayment Details
					if (!this.reqRePayment.isChecked()) {
						for (FinanceRepayments rpyDetail : finScheduleData.getRepayDetails()) {
							if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
								ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap
										.get(rpyDetail.getFinSchdDate());
								rpyDetailList.add(rpyDetail);
								rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
							} else {
								ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
								rpyDetailList.add(rpyDetail);
								rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
							}
						}
					}

					// Penalty Details
					for (OverdueChargeRecovery penaltyDetail : finScheduleData.getPenaltyDetails()) {
						if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
							ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
									.get(penaltyDetail.getFinODSchdDate());
							penaltyDetailList.add(penaltyDetail);
							penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
						} else {
							ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
							penaltyDetailList.add(penaltyDetail);
							penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
						}
					}
				}

				// Customer CIF && Customer Name Setting
				FinanceMain financeMain = finScheduleData.getFinanceMain();
				financeMain.setLovDescCustCIF(this.custCIF_header.getValue() + " - " + this.custShrtName.getValue());

				BigDecimal effectiveRateOfReturn = financeMain.getEffectiveRateOfReturn();

				if (financeMain.getFinCategory() != null) {
					if (financeMain.getFinCategory().equals(FinanceConstants.PRODUCT_CD)) {
						financeMain.setEffectiveRateOfReturn(financeMain.getRepayProfitRate());
					}
				}

				finRender = new FinScheduleListItemRenderer();
				List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(finScheduleData);
				list.add(subList1);

				List<FinFeeDetail> feeList = finScheduleData.getFinFeeDetailList();

				for (FinFeeDetail fee : feeList) {
					if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
						// PSD#183407
						fee.setFeeTypeDesc(fee.getVasReference());
						/*
						 * String productCode = vASRecordingDAO.getProductCodeByReference(fee.getFinReference(),
						 * fee.getVasReference()); fee.setFeeTypeDesc(productCode);
						 */
					}
				}
				List<FinanceScheduleReportData> subList = finRender.getPrintScheduleData(finScheduleData, rpyDetailsMap,
						penaltyDetailsMap, true, false, false);
				list.add(subList);
				String reportName = "FINENQ_ScheduleDetail";
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
					reportName = "ODFINENQ_ScheduleDetail";
				}

				Map<String, Object> aruments = new HashMap<>();
				aruments.put("isModelWindow", isModelWindow);
				list.add(aruments);

				ReportsUtil.generatePDF(reportName, financeMain, list, userName, window_FinEnqHeaderDialog);
				if (financeMain.getFinCategory() != null) {
					if (financeMain.getFinCategory().equals(FinanceConstants.PRODUCT_CD)) {
						financeMain.setEffectiveRateOfReturn(effectiveRateOfReturn);
					}
				}

			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {

			if (childDialog) {
				closeDialog();
			} else {
				this.tabPanel_dialogWindow.getChildren().clear();
				closeDialog();
			}
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceEnquiryListCtrl(FinanceEnquiryListCtrl financeEnquiryListCtrl) {
		this.financeEnquiryListCtrl = financeEnquiryListCtrl;
	}

	public FinanceEnquiryListCtrl getFinanceEnquiryListCtrl() {
		return financeEnquiryListCtrl;
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public SuspenseService getSuspenseService() {
		return suspenseService;
	}

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return overdueChargeRecoveryService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public ScoringDetailService getScoringDetailService() {
		return scoringDetailService;
	}

	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public void setManualPaymentService(ManualPaymentService manualPaymentService) {
		this.manualPaymentService = manualPaymentService;
	}

	public ManualPaymentService getManualPaymentService() {
		return manualPaymentService;
	}

	public void setFinanceEnquiry(FinanceEnquiry financeEnquiry) {
		this.financeEnquiry = financeEnquiry;
	}

	public FinanceEnquiry getFinanceEnquiry() {
		return financeEnquiry;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setChildWindowDialogCtrl(Object childWindowDialogCtrl) {
		this.childWindowDialogCtrl = childWindowDialogCtrl;
	}

	public Object getChildWindowDialogCtrl() {
		return childWindowDialogCtrl;
	}

	public FinanceDeviationsService getDeviationDetailsService() {
		return deviationDetailsService;
	}

	public void setDeviationDetailsService(FinanceDeviationsService deviationDetailsService) {
		this.deviationDetailsService = deviationDetailsService;
	}

	public VASRecordingDialogCtrl getvASRecordingDialogCtrl() {
		return vASRecordingDialogCtrl;
	}

	public void setvASRecordingDialogCtrl(VASRecordingDialogCtrl vASRecordingDialogCtrl) {
		this.vASRecordingDialogCtrl = vASRecordingDialogCtrl;
	}

	public FinCovenantTypeService getFinCovenantTypeService() {
		return finCovenantTypeService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public NotificationLogDetailsService getNotificationDetailsService() {
		return notificationLogDetailsService;
	}

	public void setNotificationLogDetailsService(NotificationLogDetailsService notificationLogDetailsService) {
		this.notificationLogDetailsService = notificationLogDetailsService;
	}

	public FinOptionService getFinOptionService() {
		return finOptionService;
	}

	public void setFinOptionService(FinOptionService finOptionService) {
		this.finOptionService = finOptionService;
	}

	public DPDEnquiryService getDpdEnquiryService() {
		return dpdEnquiryService;
	}

	public void setDpdEnquiryService(DPDEnquiryService dpdEnquiryService) {
		this.dpdEnquiryService = dpdEnquiryService;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setAssetClassificationService(AssetClassificationService assetClassificationService) {
		this.assetClassificationService = assetClassificationService;
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	@Autowired
	public void setTdsReceivablesTxnService(TdsReceivablesTxnService tdsReceivablesTxnService) {
		this.tdsReceivablesTxnService = tdsReceivablesTxnService;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	@Autowired
	public void setChequeDetailDao(ChequeDetailDAO chequeDetailDao) {
		this.chequeDetailDao = chequeDetailDao;
	}

	@Autowired
	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

}
