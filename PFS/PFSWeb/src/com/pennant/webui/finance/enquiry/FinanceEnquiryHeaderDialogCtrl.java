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
 * FileName    		:  LoanEnquiryDialogCtrl.java                                              * 	  
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
 *  1-02-2011  s       Pennant	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

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
	protected Label 					 custShrtName; 						// autoWired

	protected Label	                     label_FinEnqHeader_Filter;	                                                  // autoWired
	protected Space	                     space_menubar;	                                                              // autoWired
	protected Menu	                     menu_filter;	                                                                  // autoWired
	protected Menupopup	                 menupopup_filter;	                                                              // autoWired
	protected Button	                 btnPrint;	                                                                      // autoWired
	protected Button	                 btnDelete;	                                                                  // autoWired
	protected Button	                 btnClose;	                                                                      // autoWired
	protected Component	                 childWindow;

	protected String	                 enquiryType	        = "";
	protected String	                 finReference	        = "";
	protected boolean	                 isCancelFinance	    = false;

	// not auto wired variables
	private FinanceDetailService	     financeDetailService;
	private FinanceScheduleDetailService	financeScheduleDetailService;
	private ManualPaymentService	     manualPaymentService;
	private OverdueChargeRecoveryService	overdueChargeRecoveryService;
	private SuspenseService	             suspenseService;


	private List<ValueLabel>	         enquiryList	        = PennantAppUtil.getEnquiryTypes();
	private FinanceEnquiryListCtrl	     financeEnquiryListCtrl	= null;
	private FinScheduleData	             finScheduleData;
	private FinanceEnquiry	             financeEnquiry;

	int	                                 listRows;

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

		if (args.containsKey("isCancelFinance")) {
			this.isCancelFinance = (Boolean) args.get("isCancelFinance");
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

		if (enquiry.isBlacklisted()) {
			this.finStatus_header.setValue("Write-Off");
		} else if (enquiry.isFinIsActive()) {
			this.finStatus_header.setValue("Active");
		} else {
			this.finStatus_header.setValue("In-Active");
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

		if ("FINENQ".equals(this.enquiryType)) {

			this.grid_BasicDetails.setVisible(false);
			finScheduleData = getFinanceDetailService().getFinSchDataById(this.finReference, "_AView");
			FinContributorHeader contributorHeader = getFinanceDetailService().getFinContributorHeaderById(this.finReference);
			
			map.put("finScheduleData", finScheduleData);
			map.put("contributorHeader", contributorHeader);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul";

		} else if ("SCHENQ".equals(this.enquiryType)) {

			finScheduleData = getFinanceDetailService().getFinSchDataByFinRef(this.finReference, "_AView");

			map.put("finScheduleData", finScheduleData);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/ScheduleDetailsEnquiryDialog.zul";

		} else if ("DOCENQ".equals(this.enquiryType)) {

			List<FinAgreementDetail> finAgreements = getFinanceDetailService().getFinAgrByFinRef(this.finReference, "_AView");
			List<DocumentDetails> finDocuments = getFinanceDetailService().getFinDocByFinRef(this.finReference, "");

			map.put("finAgreements", finAgreements);
			map.put("finDocuments", finDocuments);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/DocumentEnquiryDialog.zul";

		} else if ("PSTENQ".equals(this.enquiryType)) {

			map.put("finReference", this.finReference);
			path = "/WEB-INF/pages/Enquiry/FinanceInquiry/PostingsEnquiryDialog.zul";

		} else if ("RPYENQ".equals(this.enquiryType)) {

			List<FinanceRepayments> financeRepayments = getManualPaymentService().getFinRepayListByFinRef(this.finReference, "");

			map.put("financeRepayments", financeRepayments);
			path = "/WEB-INF/pages/Enquiry/RepayInquiry/RepayEnquiryDialog.zul";

		} else if ("ODCENQ".equals(this.enquiryType)) {

			map.put("finReference", this.finReference);
			path = "/WEB-INF/pages/Enquiry/OverDueInquiry/OverdueDetailList.zul";

		} else if ("SUSENQ".equals(this.enquiryType)) {

			FinanceSuspHead suspHead = getSuspenseService().getFinanceSuspHeadById(this.finReference, true);

			map.put("suspHead", suspHead);
			path = "/WEB-INF/pages/Enquiry/SuspInquiry/SuspDetailEnquiryDialog.zul";

		}

		if (isCancelFinance) {
			//this.menu_filter.setVisible(false);
			//this.label_FinEnqHeader_Filter.setVisible(false);
			this.btnDelete.setVisible(true);
			this.space_menubar.setWidth("200px");
		}

		if (!path.equals("")) {

			//Child Window Calling
			childWindow = Executions.createComponents(path, tabPanel_dialogWindow, map);

			doFillFilterList();
			setDialog(this.window_FinEnqHeaderDialog);
		}
		logger.debug("Leaving");
	}

	private void doFillFilterList() {
		logger.debug("Entering");
		this.menupopup_filter.getChildren().clear();
		if (enquiryList != null && enquiryList.size() > 0) {
			Menuitem menuitem = null;
			for (ValueLabel enquiry : enquiryList) {
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
				ReportGenerationUtil.generateReport("FINENQ_FinanceBasicDetail", finScheduleData.getFinanceMain(), list, true, 1,
				        getUserWorkspace().getUserDetails().getUsername(), window_FinEnqHeaderDialog);
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
				List<FinanceScheduleReportData> subList = finRender.getScheduleData(finScheduleData,rpyDetailsMap, feeChargesMap);
				list.add(subList);
				ReportGenerationUtil.generateReport("FINENQ_ScheduleDetail", finScheduleData.getFinanceMain(), list, true, 1, getUserWorkspace().getUserDetails().getUsername(),
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
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	private void doDelete() throws InterruptedException {

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + this.finReference;
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			List<FinanceRepayments> listFinanceRepayments = new ArrayList<FinanceRepayments>();
			listFinanceRepayments = getFinanceDetailService().getFinanceRepaymentsByFinRef(this.finReference);
			if (listFinanceRepayments != null && listFinanceRepayments.size() > 0) {
				PTMessageUtils.showErrorMessage("Cannot Delete this Finance");
			} else {
				try {
					if (getFinanceDetailService().inActivateFinance(this.finReference,getUserWorkspace().getLoginUserDetails())) {
						Events.postEvent("onClick$button_Search", financeEnquiryListCtrl.window_FinanceEnquiry, null);			
						this.tabPanel_dialogWindow.getChildren().clear();
						closeDialog(this.window_FinEnqHeaderDialog, "FinanceEnquiryHeaderDialog");
					} else {
						PTMessageUtils.showErrorMessage("Posting Process failed");
					}
				} catch (Exception e) {
					logger.debug(e);
				}

			}
		}

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

}
