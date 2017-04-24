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
 * FileName    		:  DisbursementRegistrationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2017    														*
 *                                                                  						*
 * Modified Date    :  19-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2017       Pennant	                 0.1                                            * 
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

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.MultiLineMessageBox;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.services.disbursement.DisbursementService;
/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class DisbursementRegistrationListCtrl extends GFCBaseListCtrl<FinAdvancePayments> implements Serializable {

	private static final long			serialVersionUID	= 1L;
	private final static Logger			logger				= Logger.getLogger(DisbursementRegistrationListCtrl.class);

	protected Window					window_DisbursementRegistrationList;
	protected Borderlayout				borderLayout_DisbursementList;
	protected Paging					pagingDisbursementList;
	protected Listbox					listBoxDisbursementRegistration;

	protected Listheader				listheader_Disbursement_DisbTypes;
	protected Listheader				listheader_Disbursement_FromDate;
	protected Listheader				listheader_Disbursement_ToDate;
	protected Listheader				listheader_Disbursement_FinType;
	protected Listheader				listheader_Disbursement_Branch;
	
	protected Combobox					disbTypes;
	protected ExtendedCombobox			partnerBank;
	protected Datebox					fromDate;
	protected Datebox					toDate;
	protected ExtendedCombobox			finType;
	protected ExtendedCombobox			branch;
	protected Checkbox					qdp;

	protected Listbox					sortOperator_DisbType;
	protected Listbox					sortOperator_PartnerBank;
	protected Listbox					sortOperator_FromDate;
	protected Listbox					sortOperator_ToDate;
	protected Listbox					sortOperator_FinType;
	protected Listbox					sortOperator_Branch;

	protected Listheader 				listHeader_CheckBox_Name;
	protected Listcell 					listCell_Checkbox;
	protected Listitem 					listItem_Checkbox;
	protected Checkbox 					listHeader_CheckBox_Comp;
	protected Checkbox 					list_CheckBox;
	
	protected Button					button_Search;
	protected Button					btnDownload;
	
	private Map<Long, FinAdvancePayments> disbursementMap = new HashMap<Long, FinAdvancePayments>();
	
	@Autowired
	private DisbursementService disbursementService;

	/**
	 * default constructor.<br>
	 */
	public DisbursementRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DisbursementRegistration";
		super.pageRightName = "DisbursementRegistration";
		super.tableName = "INT_DISBURSEMENT_EXPORT_VIEW";
		super.queueTableName = "INT_DISBURSEMENT_EXPORT_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DisbursementRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_DisbursementRegistrationList, borderLayout_DisbursementList, listBoxDisbursementRegistration, pagingDisbursementList);
		setItemRender(new DisbursementListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_Search);
		registerField("paymentid");
		registerField("BranchDesc");
		registerField("LLDATE");
		
		registerField("PartnerBankCode", partnerBank, SortOrder.NONE, sortOperator_PartnerBank, Operators.STRING);
		registerField("PaymentType", listheader_Disbursement_DisbTypes, SortOrder.NONE, disbTypes, sortOperator_DisbType, Operators.STRING);
		registerField("FromDate", listheader_Disbursement_FromDate, SortOrder.NONE, fromDate, sortOperator_FromDate, Operators.DATE);
		registerField("ToDate ", listheader_Disbursement_ToDate, SortOrder.NONE, toDate, sortOperator_ToDate, Operators.DATE);
		registerField("FINTYPE", listheader_Disbursement_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("BranchCode", listheader_Disbursement_Branch, SortOrder.NONE, branch, sortOperator_Branch, Operators.STRING);
			
		// Render the page and display the data.
		doRenderPage();
		this.disbursementMap.clear();
		doSetFieldProperties();
		
		if(listBoxDisbursementRegistration.getItems().size() > 0){
			listHeader_CheckBox_Comp.setDisabled(false);
		} else  {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetFieldProperties() {

		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		if (listHeader_CheckBox_Name.getChildren() != null) {
			listHeader_CheckBox_Name.getChildren().clear();
		}
		listHeader_CheckBox_Name.appendChild(listHeader_CheckBox_Comp);
		
		this.finType.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		this.finType.setMandatoryStyle(true);
		
		fillComboBox(this.disbTypes, "", PennantStaticListUtil.getPaymentTypes(true), "");
		
		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setDisplayStyle(2);
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
		this.partnerBank.setMandatoryStyle(true);
		
		this.branch.setModuleName("BankBranch");
		this.branch.setDisplayStyle(2);
		this.branch.setValueColumn("BranchCode");
		this.branch.setDescColumn("BranchDesc");
		this.branch.setValidateColumns(new String[] { "BranchCode" });
	}

	/**
	 * Filling the MandateIdMap details and  based on checked and unchecked events of
	 * listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		for (int i = 0; i < listBoxDisbursementRegistration.getItems().size(); i++) {
			Listitem listitem = listBoxDisbursementRegistration.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}
		
		if (listHeader_CheckBox_Comp.isChecked() && listBoxDisbursementRegistration.getItems().size() > 0) {
			disbursementMap = getDisbursementDetails();
		} else {
			disbursementMap.clear();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of
	 * listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");
		
		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		
		FinAdvancePayments advancePayments = (FinAdvancePayments) checkBox.getAttribute("finAdvancePayments");
		
		if(checkBox.isChecked()){
			disbursementMap.put(advancePayments.getPaymentId(), advancePayments);
		} else {
			disbursementMap.remove(advancePayments.getPaymentId());
		}

		if (disbursementMap.size() == this.pagingDisbursementList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class DisbursementListModelItemRenderer implements ListitemRenderer<FinAdvancePayments>, Serializable {
		private static final long serialVersionUID = 1L;
	
		@Override
		public void render(Listitem item, FinAdvancePayments payments, int count) throws Exception {
			
			Listcell lc;
			
			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("finAdvancePayments", payments);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(disbursementMap.containsKey(payments.getPaymentId()));
			}
			lc.setParent(item);
			
			lc = new Listcell(payments.getPaymentType());
			lc.setParent(item);
				
			lc = new Listcell(DateUtility.formatToLongDate(payments.getLlDate()));
			lc.setParent(item);
			
			lc = new Listcell(DateUtility.formatToLongDate(payments.getLlDate()));
			lc.setParent(item);
			
			lc = new Listcell(payments.getFinType());
			lc.setParent(item);
			
			lc = new Listcell(payments.getBranchDesc());
			lc.setParent(item);
			
			item.setAttribute("finAdvancePayments", payments);
	
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDisbursementDoubleClicked");
		}
	}

	 
	/**
	 * Getting the Disbursement List using JdbcSearchObject with search criteria..
	 */
	private Map<Long, FinAdvancePayments> getDisbursementDetails() {

		JdbcSearchObject<Map<Long, FinAdvancePayments>> searchObject = new JdbcSearchObject<>();
		FinAdvancePayments finAdvancePayments = null;
		searchObject.addField("paymentid");
		searchObject.addField("PaymentType");
		searchObject.addField("PartnerBankId");
		searchObject.addField("LlDate");
		searchObject.addField("FinType");
		searchObject.addField("BranchCode");
		searchObject.addField("BranchDesc");
		searchObject.addField("PARTNERBANKCODE");
		searchObject.addTabelName(this.tableName);
		
		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}
		
		List<Map<Long, FinAdvancePayments>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		Map<Long, FinAdvancePayments> disbMap = new HashMap<Long, FinAdvancePayments>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				finAdvancePayments = new FinAdvancePayments();
				Map<Long, FinAdvancePayments> map = (Map<Long, FinAdvancePayments>) list.get(i);
				long paymentid = Long.parseLong(String.valueOf(map.get("paymentid")));
				finAdvancePayments.setPaymentId(paymentid);
				finAdvancePayments.setPaymentType(String.valueOf(map.get("PaymentType")));
				finAdvancePayments.setPartnerBankID(Long.parseLong(String.valueOf(map.get("PartnerBankId"))));
				finAdvancePayments.setPartnerbankCode(String.valueOf(map.get("PARTNERBANKCODE")));
				
				disbMap.put(paymentid, finAdvancePayments);
			}
		}
		return disbMap;
	}
	
	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_Search(Event event) {
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		doSetValidations();
		search();

		if(listBoxDisbursementRegistration.getItems().size() > 0){
			listHeader_CheckBox_Comp.setDisabled(false);
		} else  {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}
	
	private void doSetValidations() {
	
		Clients.clearWrongValue(this.partnerBank);
		Clients.clearWrongValue(this.finType);
		this.partnerBank.setErrorMessage("");
		this.finType.setErrorMessage("");
	
		if(StringUtils.trimToNull(this.partnerBank.getValue()) == null){
			throw new WrongValueException(this.partnerBank, "Partner Bank should be mandatory. ");
		}
		
		if(StringUtils.trimToNull(this.finType.getValue()) == null){
			throw new WrongValueException(this.finType, "Loan Type should be mandatory. ");
		}
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		this.listBoxDisbursementRegistration.getItems().clear();

		if(listBoxDisbursementRegistration.getItems().size() > 0){
			listHeader_CheckBox_Comp.setDisabled(false);
		} else  {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}
	

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onClick$btnDownload(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		List<FinAdvancePayments> disbushmentList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			disbursementMap.clear();
			disbursementMap = getDisbursementDetails();
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		} else {
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		}
		 
		if(listBoxDisbursementRegistration.getItems().size() > 0){
			listHeader_CheckBox_Comp.setDisabled(false);
		} else  {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		
		if (disbushmentList.isEmpty()) {
			MessageUtil.showErrorMessage(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}
		
		// Show a confirm box
		
		String msg = " " + this.pagingDisbursementList.getTotalSize() + "/" + this.disbursementMap.size() + " Disbursements selected for process.\n Do you want to continue? ";
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Conformation"), MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true);
		if (conf == MultiLineMessageBox.NO) {
			return;
		}
		try {
			btnDownload.setDisabled(true);
			disbursementService.processDisbursements(this.finType.getValue(), disbushmentList, getUserWorkspace().getLoggedInUser().getLoginUsrID());
			Map<String,Object> args = new HashMap<String, Object>();
			args.put("module", "DISBURSEMENT");
			
			MessageUtil.showMessage("File download process initiated.");
			createNewPage("/WEB-INF/pages/InterfaceBajaj/FileDownloadList.zul", "menu_Item_FileDownlaods", args);
			
		} finally {
			this.disbursementMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
			btnDownload.setDisabled(false);
			logger.debug("Leaving");
		}
	}
	
	protected void createNewPage(String uri, String tabName, Map<String, Object> args) {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Center center = bl.getCenter();
		final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter");	
		
		Tab tab = null;
		if(tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_"))  != null) {
			tab = (Tab)tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));		
			if(tab != null) {
				tab.close();
			}
		}
		tab = new Tab();
		tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
		tab.setLabel(Labels.getLabel(tabName));
		tab.setClosable(true);
		tab.setParent(tabs);
		tab.setLabel("Download");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);
			
		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

}