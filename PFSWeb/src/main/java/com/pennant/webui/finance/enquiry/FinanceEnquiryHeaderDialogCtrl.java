/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LoanEnquiryDialogCtrl.java                                           * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :   1-02-2011    														*
 *                                                                  						*
 * Modified Date    :   1-02-2011      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  1-02-2011  s       Pennant	                 0.1                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinMainReportData;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceDeviationsService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.configuration.vasrecording.VASRecordingDialogCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Reports/FinanceEnquiryHeaderDialogCtrl.zul.
 */
public class FinanceEnquiryHeaderDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long	         serialVersionUID	    = -6646226859133636932L;
	private static final Logger	         logger	                = Logger.getLogger(FinanceEnquiryHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window	                 window_FinEnqHeaderDialog;	                                                  // autoWired
	protected Borderlayout	             borderlayoutFinEnqHeader;	                                                      // autoWired
	public Grid	                         grid_BasicDetails;	                                                          // autoWired
	protected Tabpanel	                 tabPanel_dialogWindow;	                                                      // autoWired

	protected Textbox	                 finReference_header;	                                                          // autoWired
	protected Textbox	                 finStatus_Reason;	  
	protected Textbox	                 finStatus_header;	                                                              // autoWired
	protected Textbox	                 finType_header;	                                                              // autoWired
	protected Textbox	                 finCcy_header;	                                                              // autoWired
	protected Textbox	                 scheduleMethod_header;	                                                      // autoWired
	protected Textbox	                 profitDaysBasis_header;	                                                      // autoWired
	protected Textbox	                 finBranch_header;	                                                              // autoWired
	protected Textbox	                 custCIF_header;	                                                              // autoWired
	protected Label 					 custShrtName; 		                                                              // autoWired
	protected Label                      label_window_FinEnqHeaderDialog;   											// autoWired
	
	protected Label	                     label_FinEnqHeader_Filter;	                                                  // autoWired
	protected Space	                     space_menubar;	                                                              // autoWired
	protected Menu	                     menu_filter;	                                                                  // autoWired
	protected Menupopup	                 menupopup_filter;	                                                              // autoWired
	protected Button	                 btnPrint;	                                                                      // autoWired
	protected Component	                 childWindow;
    protected Menubar                    menubar;
	
	protected String	                 enquiryType	        = "";
	protected String	                 finReference	        = "";

	// not auto wired variables
	private FinanceDetailService			financeDetailService;
	private EligibilityDetailService		eligibilityDetailService;
	private AgreementDetailService			agreementDetailService;
	private ScoringDetailService			scoringDetailService;
	private CheckListDetailService			checkListDetailService;
	private FinCovenantTypeService			finCovenantTypeService;

	private ManualPaymentService			manualPaymentService;
	private OverdueChargeRecoveryService	overdueChargeRecoveryService;
	private SuspenseService					suspenseService;
	private FinanceDeviationsService		deviationDetailsService;
	private FinFeeDetailService				finFeeDetailService;

	private List<ValueLabel>	         enquiryList	        = PennantStaticListUtil.getEnquiryTypes();
	private List<ValueLabel>	         mandateList	        = PennantStaticListUtil.getMandateTypeList();
	private FinanceEnquiryListCtrl	     financeEnquiryListCtrl	= null;
	private VASRecordingDialogCtrl	     vASRecordingDialogCtrl	= null;
	private FinScheduleData	             finScheduleData;
	private FinanceEnquiry	             financeEnquiry;
	private FinanceSummary 				 financeSummary;

	int	                                 listRows;
    private String assetCode = "";
	private transient Object 			 childWindowDialogCtrl = null;
	private boolean fromApproved;
	private boolean childDialog;
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
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinEnqHeaderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinEnqHeaderDialog);

		try {
			
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
				this.setFinanceEnquiry((FinanceEnquiry) arguments
						.get("financeEnquiry"));
				this.finReference = getFinanceEnquiry().getFinReference();
			}

			if (arguments.containsKey("financeEnquiryListCtrl")) {
				this.setFinanceEnquiryListCtrl((FinanceEnquiryListCtrl) arguments
						.get("financeEnquiryListCtrl"));
			}
			
			if (arguments.containsKey("VASRecordingDialog")) {
				this.setvASRecordingDialogCtrl((VASRecordingDialogCtrl) arguments
						.get("VASRecordingDialog"));
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
	 * @throws InterruptedException
	 */
	public void doFillDialogWindow() throws InterruptedException {
		logger.debug("Entering");

		FinanceEnquiry enquiry = getFinanceEnquiry();

		this.finReference_header.setValue(enquiry.getFinReference());
		this.finStatus_header.setValue(enquiry.getFinStatus());

		if (enquiry.isFinIsActive()) {
			this.finStatus_header.setValue("Active");
		} else {
			this.finStatus_header.setValue("Matured");
		}
		
		String closingStatus = StringUtils.trimToEmpty(enquiry.getClosingStatus());
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Normal");
		} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Cancelled");
		} else if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Written-Off");
		}else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(closingStatus)) {
			this.finStatus_Reason.setValue("Settled");
		}
		
		
		this.custCIF_header.setValue(enquiry.getLovDescCustCIF());
		this.custShrtName.setValue(enquiry.getLovDescCustShrtName());
		this.finType_header.setValue(enquiry.getFinType() + "-" + enquiry.getLovDescFinTypeName());
		this.finCcy_header.setValue(enquiry.getFinCcy());
		this.scheduleMethod_header.setValue(enquiry.getScheduleMethod());
		this.profitDaysBasis_header.setValue(enquiry.getProfitDaysBasis());
		this.finBranch_header.setValue(enquiry.getFinBranch() == null ? "" : enquiry.getFinBranch() + "-" + enquiry.getLovDescFinBranchName());

		if (childWindow != null) {
			tabPanel_dialogWindow.getChildren().clear();
			tabPanel_dialogWindow.appendChild(this.grid_BasicDetails);
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeEnquiryHeaderDialogCtrl", this);
		String path = "";
		this.grid_BasicDetails.setVisible(true);
		this.btnPrint.setVisible(false);

		if ("FINENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinanceEnquiry.value"));
			this.grid_BasicDetails.setVisible(false);
			if(fromApproved){
				finScheduleData = getFinanceDetailService().getFinSchDataById(this.finReference, "_AView", true);
				if (finScheduleData.getFinanceMain() == null) {
					Clients.showNotification("Finance Reference Is Not Valid Create New Finance Reference");
					this.window_FinEnqHeaderDialog.onClose();
				}
				FinanceSummary summary = finScheduleData.getFinanceSummary();
				List<FinFeeDetail> feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finReference, false, "");
				calculateFeeChargeDetails(feeDetails, summary);
			}else{
				finScheduleData = getFinanceDetailService().getFinSchDataById(this.finReference, "_View",true);
				if(finScheduleData != null) {
					FinanceSummary summary = finScheduleData.getFinanceSummary();
					List<FinFeeDetail> feeDetails = getFinFeeDetailService().getFinFeeDetailById(this.finReference, false, "_View");
					calculateFeeChargeDetails(feeDetails, summary);
				}
			}
			if(finScheduleData.getFinanceMain()!=null){
				FinContributorHeader contributorHeader = getFinanceDetailService().getFinContributorHeaderById(this.finReference);
				financeSummary = getFinanceDetailService().getFinanceProfitDetails(this.finReference);
				map.put("financeSummary", financeSummary);

				// finance Contract Asset Details
				List<ContractorAssetDetail> assetDetails = null;
				if(FinanceConstants.PRODUCT_ISTISNA.equals(finScheduleData.getFinanceMain().getLovDescProductCodeName())){
					assetDetails = getFinanceDetailService().getContractorAssetDetailList(finReference);
				}

				map.put("assetDetailList", assetDetails);
				map.put("finScheduleData", finScheduleData);
				map.put("contributorHeader", contributorHeader);
				map.put("fromApproved", fromApproved);
				path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";
				this.btnPrint.setVisible(true);
			}
		} else if ("SCHENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ScheduleEnquiry.value"));
			if(fromApproved){
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_AView",0);
			}else{
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_View",0);
			}
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScheduleDetailsEnquiryDialog.zul";
			this.btnPrint.setVisible(true);

		} else if ("DOCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DocumentEnquiry.value"));
			List<FinAgreementDetail> finAgreements;
			List<DocumentDetails> finDocuments;
			if (fromApproved) {
				finAgreements = getAgreementDetailService().getFinAgrByFinRef(this.finReference, "_AView");
				finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "", "");
			} else {
				finAgreements = getAgreementDetailService().getFinAgrByFinRef(this.finReference, "_View");
				finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "", "_View");
			}

			map.put("finAgreements", finAgreements);
			map.put("finDocuments", finDocuments);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/DocumentEnquiryDialog.zul";

		} else if ("PSTENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_PostingsEnquiry.value"));
			map.put("finReference", this.finReference);
			map.put("enquiry", enquiry);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/PostingsEnquiryDialog.zul";

		} else if ("RPYENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RepaymentEnuiry.value"));
			List<FinanceRepayments> financeRepayments = getManualPaymentService().getFinRepayListByFinRef(this.finReference, false,"");
			map.put("financeRepayments", financeRepayments);
			map.put("finAmountformatter", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/RepayInquiry/RepayEnquiryDialog.zul";

		} else if ("ODCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverdueChargeEnquiry.value"));
			map.put("finReference", this.finReference);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/OverDueInquiry/OverdueDetailList.zul";

		} else if ("SUSENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_SuspenseEnquiry.value"));
			FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(this.finReference, true, "","");
			map.put("suspHead", suspHead);
			path = "/WEB-INF/pages/Enquiry/SuspInquiry/SuspDetailEnquiryDialog.zul";

		} else if ("CHKENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_CheckListEnquiry.value"));
			List<FinanceCheckListReference> financeCheckListReference;
			if(fromApproved){
				 financeCheckListReference = getCheckListDetailService().getCheckListByFinRef(this.finReference, "_AView");
			}else{
				 financeCheckListReference = getCheckListDetailService().getCheckListByFinRef(this.finReference, "_View");
			}
			
			map.put("FinanceCheckListReference", financeCheckListReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/CheckListEnquiry.zul";

		} else if ("ELGENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_EligibilityListEnquiry.value"));
			List<FinanceEligibilityDetail> eligibilityDetails = getEligibilityDetailService().getFinElgDetailList(this.finReference);
			map.put("eligibilityList", eligibilityDetails);
			map.put("finAmountformatter", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityEnquiryDialog.zul";

		} else if ("SCRENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ScoringListEnquiry.value"));
			List<Object> scoreDetails = getScoringDetailService().getFinScoreDetailList(this.finReference);
			map.put("scoringList", scoreDetails);
			map.put("custTypeCtg", enquiry.getCustTypeCtg());
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScoringEnquiryDialog.zul";

		} else if ("PFTENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ProfitListEnquiry.value"));
			FinanceSummary financeSummary = getFinanceDetailService().getFinanceProfitDetails(this.finReference);
			map.put("financeSummary", financeSummary);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceProfitEnquiryDialog.zul";

		} else if ("CHQPRNT".equals(this.enquiryType)) {

			//this.window_FinEnqHeaderDialog.setTitle(Labels.getLabel("label_ChequePrintingDialog_Title.value"));
			this.btnPrint.setVisible(false);
			this.menubar.setVisible(false);
			this.space_menubar.setWidth("240px");
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ChequePrintingDialog.value"));
			this.label_FinEnqHeader_Filter.setVisible(false);
			
			if(fromApproved){
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_AView", 0);
			}else{
				finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_View", 0);
			}
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ChequePrintingDialog.zul";

		} else if ("RECENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RecommendationsEnquiry.value"));
			map.put("notes", getNotes());
			map.put("control", this);
			map.put("enquiry", true);
			map.put("isFinanceNotes", true);
			path = "/WEB-INF/pages/notes/notes.zul";
			
		} else if ("DEVENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DeviationEnquiry"));
			List<FinanceDeviations> approvalEnqList = getDeviationDetailsService().getApprovedFinanceDeviations(this.finReference);
			map.put("approvalEnquiry", "");			
			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);			
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("approvalEnqList", approvalEnqList);
			path = "/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul";

		}else if ("DDAENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DDAEnquiry"));
			
			JdbcSearchObject<DDAProcessData> jdbcSearchObject=new JdbcSearchObject<DDAProcessData>();
			jdbcSearchObject.addTabelName("DDAReferenceLog");
			jdbcSearchObject.addFilterEqual("FinRefence", this.finReference);
			jdbcSearchObject.setSearchClass(DDAProcessData.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<DDAProcessData> list = pagedListService.getBySearchObject(jdbcSearchObject);
			
			map.put("approvalEnquiry", "");			
			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);			
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("list", list);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/DDAEnquiryDialog.zul";
		}else if ("FINMANDENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinMandateEnquiry"));
			
			JdbcSearchObject<Mandate> jdbcSearchObject=new JdbcSearchObject<Mandate>();
			jdbcSearchObject.addTabelName("Mandates_View");
			jdbcSearchObject.addFilterEqual("MandateID", enquiry.getMandateID());
			jdbcSearchObject.setSearchClass(Mandate.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<Mandate> list = pagedListService.getBySearchObject(jdbcSearchObject);
			if(!list.isEmpty()){
				map.put("mandate", list.get(0));
				map.put("fromLoanEnquiry", true);		
				map.put("tabPaneldialogWindow", tabPanel_dialogWindow);			
				path = "/WEB-INF/pages/Enquiry/FinanceInquiry/MandateEnquiryDialog.zul";
			}
		}else if ("ODENQ".equals(this.enquiryType)) {
            this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverdueEnquiry"));
			
			JdbcSearchObject<FinODDetails> jdbcSearchObject=new JdbcSearchObject<FinODDetails>();
			jdbcSearchObject.addTabelName("FinODDetails");
			jdbcSearchObject.addFilterEqual("FinReference", this.finReference);
			jdbcSearchObject.setSearchClass(FinODDetails.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<FinODDetails> list = pagedListService.getBySearchObject(jdbcSearchObject);
			
			map.put("approvalEnquiry", "");			
			map.put("tabPaneldialogWindow", tabPanel_dialogWindow);			
			map.put("ccyformat", CurrencyUtil.getFormat(enquiry.getFinCcy()));
			map.put("list", list);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/OverdueEnquiryDialog.zul";

		}  else if ("LTPPENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_LatepayProfitRecovery"));
			map.put("finReference", this.finReference);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.financeEnquiry.getFinCcy()));
			path = "/WEB-INF/pages/Enquiry/LatepayProfitEnquiry/LatepayProfitRecoveryList.zul";

		} else if ("COVENQ".equals(this.enquiryType)) {
			
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_CovenantEnquiry.value"));
			List<FinCovenantType> finCovenants;
			if(fromApproved){
				 finCovenants = getFinCovenantTypeService().getFinCovenantTypeById(this.finReference, "_EAView",true);
			}else{
				 finCovenants = getFinCovenantTypeService().getFinCovenantTypeById(this.finReference, "_EView",true);
			}
			map.put("finCovenants", finCovenants);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/CovenantEnquiryDialog.zul";

		}		
		if (StringUtils.isNotEmpty(path)) {

			//Child Window Calling
			childWindow = Executions.createComponents(path, tabPanel_dialogWindow, map);
		
			doFillFilterList();
			
			if(childDialog){
				this.window_FinEnqHeaderDialog.setHeight("80%");
				this.window_FinEnqHeaderDialog.setWidth("90%");
				setDialog(DialogType.MODAL);
			}else{
				setDialog(DialogType.EMBEDDED);
			}
		}
		logger.debug("Leaving");
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
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
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
		
		boolean mandate=isMandate(StringUtils.trimToEmpty(getFinanceEnquiry().getFinRepayMethod()),mandateList);
		
		if (enquiryList != null && enquiryList.size() > 0) {
			Menuitem menuitem = null;
			for (ValueLabel enquiry : enquiryList) {
				if(this.finReference.endsWith("_DP") && 
						("ASSENQ".equals(enquiry.getValue()) || "ELGENQ".equals(enquiry.getValue()) ||
								"SCRENQ".equals(enquiry.getValue()) || "CHKENQ".equals(enquiry.getValue()))){
					continue;
				}
				if ("FINMANDENQ".equals(enquiry.getValue()) && !mandate) {
					continue;
				}
				if(!("ASSENQ".equalsIgnoreCase(enquiry.getValue()) && "NOTAPP".equalsIgnoreCase(StringUtils.trimToEmpty(assetCode)))){
					menuitem = new Menuitem();
					menuitem.setImage("/images/icons/Old/arrow_blue_right_16x16.gif");
					menuitem.setLabel(enquiry.getLabel());
					menuitem.setValue(enquiry.getValue());
					menuitem.setStyle("font-weight:bold;");
					menuitem.addForward("onClick", this.window_FinEnqHeaderDialog, "onFilterMenuItem", enquiry);
					if (this.enquiryType.equals(enquiry.getValue())) {
						this.menu_filter.setLabel(enquiry.getLabel());
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
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onFilterMenuItem(ForwardEvent event) throws InterruptedException {
		if (event.getData() != null) {
			ValueLabel enquiry = (ValueLabel) event.getData();
			this.enquiryType = enquiry.getValue();
			doFillDialogWindow();
			doFillFilterList();
			this.menu_filter.setLabel(enquiry.getLabel());
		}
	}

	/**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		if ("FINENQ".equals(this.enquiryType)) {

			List<Object> list = new ArrayList<Object>();
			FinScheduleListItemRenderer finRender;
			if (finScheduleData != null) {
				finRender = new FinScheduleListItemRenderer();
				List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(finScheduleData);
				list.add(subList1);
				
				FinMainReportData reportData = new FinMainReportData();
				reportData = reportData.getFinMainReportData(finScheduleData,financeSummary);
				
				ReportGenerationUtil.generateReport("FINENQ_FinanceBasicDetail", reportData, list, true, 1,
						getUserWorkspace().getLoggedInUser().getFullName(), window_FinEnqHeaderDialog);
			}

		} else if ("SCHENQ".equals(this.enquiryType)) {

			List<Object> list = new ArrayList<Object>();
			FinScheduleListItemRenderer finRender;
			if (finScheduleData != null) {
				
				// Find Out Fee charge Details on Schedule
				Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
				if(finScheduleData.getFeeRules() != null && finScheduleData.getFeeRules().size() > 0){
					feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();

					for (FeeRule fee : finScheduleData.getFeeRules()) {
						if(feeChargesMap.containsKey(fee.getSchDate())){
							ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						}else{
							ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
							feeChargeList.add(fee);
							feeChargesMap.put(fee.getSchDate(), feeChargeList);
						}
					}
				}
				
				// Find Out Finance Repayment Details & Penalty Details on Schedule
				Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
				Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
				if(finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0){
					rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();
					penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

					//Repayment Details
					for (FinanceRepayments rpyDetail : finScheduleData.getRepayDetails()) {
						if(rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())){
							ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
							rpyDetailList.add(rpyDetail);
							rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
						}else{
							ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
							rpyDetailList.add(rpyDetail);
							rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
						}
					}
					
					// Penalty Details
					for (OverdueChargeRecovery penaltyDetail : finScheduleData.getPenaltyDetails()) {
						if(penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())){
							ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail.getFinODSchdDate());
							penaltyDetailList.add(penaltyDetail);
							penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
						}else{
							ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
							penaltyDetailList.add(penaltyDetail);
							penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
						}
					}
				}
				
				// Customer CIF Setting
				finScheduleData.getFinanceMain().setLovDescCustCIF(this.custCIF_header.getValue());
				
				finRender = new FinScheduleListItemRenderer();
				List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(finScheduleData);
				list.add(subList1);
				List<FinanceScheduleReportData> subList = finRender.getPrintScheduleData(finScheduleData,rpyDetailsMap, penaltyDetailsMap, true);
				list.add(subList);
				String reportName = "FINENQ_ScheduleDetail";
				 if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,finScheduleData.getFinanceMain().getProductCategory())) {
					reportName = "ODFINENQ_ScheduleDetail";
				}
				ReportGenerationUtil.generateReport(reportName, finScheduleData.getFinanceMain(), list,
						true, 1, getUserWorkspace().getLoggedInUser().getFullName(), window_FinEnqHeaderDialog);
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
			
			if(childDialog){
				closeDialog();
			}else{
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
	public void setEligibilityDetailService(
			EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}
	
	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}
	public void setAgreementDetailService(
			AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
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
	
	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

}
