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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
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

import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinMainReportData;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Reports/LoanEnquiryDialog.zul. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceEnquiryHeaderDialogCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {

	private static final long	         serialVersionUID	    = -6646226859133636932L;
	private final static Logger	         logger	                = Logger.getLogger(FinanceEnquiryHeaderDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window	                 window_FinEnqHeaderDialog;	                                                  // autoWired
	protected Borderlayout	             borderlayoutFinEnqHeader;	                                                      // autoWired
	public Grid	                         grid_BasicDetails;	                                                          // autoWired
	protected Tabpanel	                 tabPanel_dialogWindow;	                                                      // autoWired

	protected Textbox	                 finReference_header;	                                                          // autoWired
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
	protected Button	                 btnDelete;	                                                                  // autoWired
	protected Button	                 btnClose;	                                                                      // autoWired
	protected Component	                 childWindow;
    protected Menubar                    menubar;
	
	protected String	                 enquiryType	        = "";
	protected String	                 finReference	        = "";

	// not auto wired variables
	private FinanceDetailService	     financeDetailService;
	private EligibilityDetailService 	 eligibilityDetailService;
	private AgreementDetailService       agreementDetailService;
	private ScoringDetailService	     scoringDetailService;
	private CheckListDetailService	     checkListDetailService;
	
	private FinanceScheduleDetailService	financeScheduleDetailService;
	private ManualPaymentService	     manualPaymentService;
	private OverdueChargeRecoveryService	overdueChargeRecoveryService;
	private SuspenseService	             suspenseService;

	private List<ValueLabel>	         enquiryList	        = PennantStaticListUtil.getEnquiryTypes();
	private FinanceEnquiryListCtrl	     financeEnquiryListCtrl	= null;
	private FinScheduleData	             finScheduleData;
	private FinanceEnquiry	             financeEnquiry;
	private FinanceSummary 				 financeSummary;

	int	                                 listRows;
    private String assetCode = "";
	/**
	 * default constructor.<br>
	 */
	public FinanceEnquiryHeaderDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinEnqHeaderDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("enquiryType")) {
			this.enquiryType = (String) args.get("enquiryType");
		}

		if (args.containsKey("financeEnquiry")) {
			this.setFinanceEnquiry((FinanceEnquiry) args.get("financeEnquiry"));
			this.finReference = getFinanceEnquiry().getFinReference();
		}

		if (args.containsKey("financeEnquiryListCtrl")) {
			this.setFinanceEnquiryListCtrl((FinanceEnquiryListCtrl) args.get("financeEnquiryListCtrl"));
		}

		//Method for recall Enquiries
		doFillDialogWindow();

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
			if (StringUtils.trimToEmpty(enquiry.getClosingStatus()).equals("W")) {
				this.finStatus_header.setValue("Written-Off");
			} else if (StringUtils.trimToEmpty(enquiry.getClosingStatus()).equals("P")) {
				this.finStatus_header.setValue("Pay-Off");
			} else {
				this.finStatus_header.setValue("In-Active");
			}
		}
		this.custCIF_header.setValue(enquiry.getLovDescCustCIF());
		this.custShrtName.setValue(enquiry.getLovDescCustShrtName());
		this.finType_header.setValue(enquiry.getFinType() + "-" + enquiry.getLovDescFinTypeName());
		this.finCcy_header.setValue(enquiry.getFinCcy() + "-" + enquiry.getLovDescFinCcyName());
		this.scheduleMethod_header.setValue(enquiry.getScheduleMethod() + "-" + enquiry.getLovDescScheduleMethodName());
		this.profitDaysBasis_header.setValue(enquiry.getProfitDaysBasis() + "-" + enquiry.getLovDescProfitDaysBasisName());
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
		
		final FinanceDetail financeDetail = getFinanceDetailService().getFinAssetDetails(this.finReference,"_View");
		assetCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescAssetCodeName();
		
		if ("FINENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_FinanceEnquiry.value"));
			this.grid_BasicDetails.setVisible(false);
			finScheduleData = getFinanceDetailService().getFinSchDataById(this.finReference, "_AView",true);
			FinContributorHeader contributorHeader = getFinanceDetailService().getFinContributorHeaderById(this.finReference);
			financeSummary = getFinanceDetailService().getFinanceProfitDetails(this.finReference);
			map.put("financeSummary", financeSummary);
			
			// finance Contract Asset Details
			List<ContractorAssetDetail> assetDetails = null;
			if(PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(finScheduleData.getFinanceMain().getLovDescProductCodeName())){
				assetDetails = getFinanceDetailService().getContractorAssetDetailList(finReference);
			}
			
			map.put("assetDetailList", assetDetails);
			map.put("finScheduleData", finScheduleData);
			map.put("contributorHeader", contributorHeader);
			map.put("assetCode", enquiry.getAssetCode());
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";
			this.btnPrint.setVisible(true);

		} else if ("SCHENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_ScheduleEnquiry.value"));
			finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_AView",0);
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScheduleDetailsEnquiryDialog.zul";
			this.btnPrint.setVisible(true);

		} else if ("DOCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_DocumentEnquiry.value"));
			List<FinAgreementDetail> finAgreements = getAgreementDetailService().getFinAgrByFinRef(this.finReference, "_AView");
			List<DocumentDetails> finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "");

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
			map.put("finAmountformatter", enquiry.getLovDescFinFormatter());
			path = "/WEB-INF/pages/Enquiry/RepayInquiry/RepayEnquiryDialog.zul";

		} else if ("ODCENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_OverdueEnquiry.value"));
			map.put("finReference", this.finReference);
			map.put("ccyFormatter", this.financeEnquiry.getLovDescFinFormatter());
			path = "/WEB-INF/pages/Enquiry/OverDueInquiry/OverdueDetailList.zul";

		} else if ("SUSENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_SuspenseEnquiry.value"));
			FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(this.finReference, true);
			map.put("suspHead", suspHead);
			path = "/WEB-INF/pages/Enquiry/SuspInquiry/SuspDetailEnquiryDialog.zul";
			
		} else if ("CHKENQ".equals(this.enquiryType)) {

			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_CheckListEnquiry.value"));
			List<FinanceCheckListReference> financeCheckListReference = getCheckListDetailService().getCheckListByFinRef(this.finReference, "_AView");
			map.put("FinanceCheckListReference", financeCheckListReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/CheckListEnquiry.zul";

		} else if ("ELGENQ".equals(this.enquiryType)) {
			
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_EligibilityListEnquiry.value"));
			List<FinanceEligibilityDetail> eligibilityDetails = getEligibilityDetailService().getFinElgDetailList(this.finReference);
			map.put("eligibilityList", eligibilityDetails);
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
			
			finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_AView", 0);
			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ChequePrintingDialog.zul";

		} else if ("RECENQ".equals(this.enquiryType)) {
			this.label_window_FinEnqHeaderDialog.setValue(Labels.getLabel("label_RecommendationsEnquiry.value"));
			map.put("notes", getNotes());
			map.put("control", this);
			map.put("enquiry", true);
			map.put("isFinanceNotes", true);
			path = "/WEB-INF/pages/notes/notes.zul";
		}else if ("ASSENQ".equals(this.enquiryType)) {
		
			CarLoanDetail carLoanDetail = null;
			EducationalLoan educationalLoan = null;
			HomeLoanDetail homeLoanDetail = null;
			MortgageLoanDetail mortgageLoanDetail = null;

			
			try {
				map.put("roleCode", getRole());
				map.put("financeMainDialogCtrl", this);
				map.put("ccyFormatter", financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

				String finReference = financeDetail.getFinScheduleData().getFinReference();
				
				String tabLabel = "";
				
				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {

					tabLabel = Labels.getLabel("CarLoanDetail");
					if (financeDetail.getCarLoanDetail() == null) {
						carLoanDetail = new CarLoanDetail();
						carLoanDetail.setNewRecord(true);
						carLoanDetail.setLoanRefNumber(finReference);
					} else {
						carLoanDetail = financeDetail.getCarLoanDetail();
					}
					map.put("carLoanDetail", carLoanDetail);
					path = "/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.EDUCATON)) {

					tabLabel = Labels.getLabel("EducationalLoan");
					if (financeDetail.getEducationalLoan() == null) {
						educationalLoan = new EducationalLoan();
						educationalLoan.setNewRecord(true);
						educationalLoan.setLoanRefNumber(finReference);
					} else {
						educationalLoan = financeDetail.getEducationalLoan();
					}
					map.put("educationalLoan", educationalLoan);
					map.put("isEnquiry", true);
					path = "/WEB-INF/pages/LMTMasters/EducationalLoan/EducationalLoanDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {

					tabLabel = Labels.getLabel("HomeLoanDetail");
					if (financeDetail.getHomeLoanDetail() == null) {
						homeLoanDetail = new HomeLoanDetail();
						homeLoanDetail.setNewRecord(true);
						homeLoanDetail.setLoanRefNumber(finReference);
					} else {
						homeLoanDetail = financeDetail.getHomeLoanDetail();
					}
					map.put("homeLoanDetail", homeLoanDetail);
					path = "/WEB-INF/pages/LMTMasters/HomeLoanDetail/HomeLoanDetailDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {

					tabLabel = Labels.getLabel("MortgageLoanDetail");
					if (financeDetail.getMortgageLoanDetail() == null) {
						mortgageLoanDetail = new MortgageLoanDetail();
						mortgageLoanDetail.setNewRecord(true);
						mortgageLoanDetail.setLoanRefNumber(finReference);
					} else {
						mortgageLoanDetail = financeDetail.getMortgageLoanDetail();
					}
					map.put("mortgageLoanDetail", mortgageLoanDetail);
					path = "/WEB-INF/pages/LMTMasters/MortgageLoanDetail/MortgageLoanDetailDialog.zul";

				}else if (assetCode.equalsIgnoreCase(PennantConstants.GOODS)) {

					tabLabel = Labels.getLabel("GoodsLoanDetail");
					map.put("financedetail", financeDetail);
					map.put("isEnquiry", true);
					path = "/WEB-INF/pages/LMTMasters/GoodsLoanDetail/FinGoodsLoanDetailList.zul";

				}else if (assetCode.equalsIgnoreCase(PennantConstants.GENGOODS)) {

					tabLabel = Labels.getLabel("GenGoodsLoanDetail");
					map.put("financedetail", financeDetail);
					map.put("isEnquiry", true);
					path = "/WEB-INF/pages/LMTMasters/GenGoodsLoanDetail/FinGenGoodsLoanDetailList.zul";

				}else if (assetCode.equalsIgnoreCase(PennantConstants.COMMIDITY)) {

						tabLabel = Labels.getLabel("CommidityLoanDetail");
						map.put("financedetail", financeDetail);
						map.put("isEnquiry", true);
						path = "/WEB-INF/pages/LMTMasters/CommidityLoanDetail/FinCommidityLoanDetailList.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.SHARES)) { 

						tabLabel = Labels.getLabel("SharesDetail");
						map.put("financedetail", financeDetail);
						path = "/WEB-INF/pages/LMTMasters/SharesDetail/FinSharesDetailList.zul";

				}
				this.label_window_FinEnqHeaderDialog.setValue(tabLabel);
				if (!path.equals("")) {
				this.grid_BasicDetails.setVisible(false);
				}
		}catch (Exception e) {
			logger.error(e);
		}
	}
		if (!path.equals("")) {

			//Child Window Calling
			childWindow = Executions.createComponents(path, tabPanel_dialogWindow, map);

			doFillFilterList();
			setDialog(this.window_FinEnqHeaderDialog);
		}
		logger.debug("Leaving");
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
		if (enquiryList != null && enquiryList.size() > 0) {
			Menuitem menuitem = null;
			for (ValueLabel enquiry : enquiryList) {
				if(!(enquiry.getValue().equalsIgnoreCase("ASSENQ") && StringUtils.trimToEmpty(assetCode).equalsIgnoreCase("NOTAPP"))){
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
				
				SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
				String usrName = (securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName()).trim();
				
				ReportGenerationUtil.generateReport("FINENQ_FinanceBasicDetail", reportData, list, true, 1,
						usrName, window_FinEnqHeaderDialog);
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
				
				// Find Out Finance Repayment Details on Schedule
				Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
				if(finScheduleData.getRepayDetails() != null && finScheduleData.getRepayDetails().size() > 0){
					rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

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
				}
				
				finRender = new FinScheduleListItemRenderer();
				List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(finScheduleData);
				list.add(subList1);
				List<FinanceScheduleReportData> subList = finRender.getScheduleData(finScheduleData,rpyDetailsMap, feeChargesMap,true);
				list.add(subList);
				
				SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
				String usrName = securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName();
				
				ReportGenerationUtil.generateReport("FINENQ_ScheduleDetail", finScheduleData.getFinanceMain(), list, true, 1, usrName,
				        window_FinEnqHeaderDialog);
			}
		}

		/*if(getLoanEnquiry().getFinanceMainList()!=null && getLoanEnquiry().getFinanceMainList().size()>0){
			ReportGenerationUtil.generateReport("LoanEnquiry", getLoanEnquiry(),
					getLoanEnquiry().getFinanceMainList(),true, 1, getUserWorkspace().getUserDetails().getUsername(),null);
		}*/
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
			this.tabPanel_dialogWindow.getChildren().clear();
			closeDialog(this.window_FinEnqHeaderDialog, "FinanceEnquiryHeaderDialog");
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setFinanceScheduleDetailService(FinanceScheduleDetailService financeScheduleDetailService) {
		this.financeScheduleDetailService = financeScheduleDetailService;
	}
	public FinanceScheduleDetailService getFinanceScheduleDetailService() {
		return financeScheduleDetailService;
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

}
