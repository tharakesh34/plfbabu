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
 * FileName    		:  MandateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mandate.mandate;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.eod.BatchFileUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.MultiLineMessageBox;

/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateRegistrationListCtrl extends GFCBaseListCtrl<Mandate> implements Serializable {

	private static final long			serialVersionUID	= 1L;
	private final static Logger			logger				= Logger.getLogger(MandateRegistrationListCtrl.class);

	protected Window					window_MandateRegistrationList;
	protected Borderlayout				borderLayout_MandateList;
	protected Paging					pagingMandateList;
	protected Listbox					listBoxMandateRegistration;

	protected Listheader				listheader_CustCIF;
	protected Listheader				listheader_MandateType;
	protected Listheader				listheader_BankName;
	protected Listheader				listheader_AccNumber;
	protected Listheader				listheader_AccType;
	protected Listheader				listheader_Amount;
	protected Listheader				listheader_ExpiryDate;
	protected Listheader				listheader_Status;
	protected Listheader				listheader_InputDate;

	protected Listheader 				listHeader_CheckBox_Name;
	protected Listcell 					listCell_Checkbox;
	protected Listitem 					listItem_Checkbox;
	protected Checkbox 					listHeader_CheckBox_Comp;
	
	protected Checkbox 					list_CheckBox;
	
	protected Button					button_MandateList_NewMandate;
	protected Button					button_MandateList_MandateSearch;

	protected Longbox					mandateID;
	protected Combobox					mandateType;
	protected Textbox					custCIF;
	protected Textbox					bankName;
	protected Combobox					status;
	protected Textbox					accNumber;
	protected Combobox					accType;
	protected Datebox					expiryDate;
	protected Datebox					inputDate;

	protected Listbox					sortOperator_MandateID;
	protected Listbox					sortOperator_CustCIF;
	protected Listbox					sortOperator_MandateType;
	protected Listbox					sortOperator_BankName;
	protected Listbox					sortOperator_AccNumber;
	protected Listbox					sortOperator_AccType;
	protected Listbox					sortOperator_ExpiryDate;
	protected Listbox					sortOperator_Status;
	protected Listbox					sortOperator_InputDate;

	private transient MandateService	mandateService;
	private MandateStatusDAO			mandateStatusDAO;
	private MandateDAO					mandateDAO;
	
	private Map<Long, String> mandateIdMap = new HashMap<Long, String>();

	/**
	 * default constructor.<br>
	 */
	public MandateRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Mandate";
		super.pageRightName = "MandateList";
		super.tableName = "Mandates_AView";
		super.queueTableName = "Mandates_AView";
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		searchObject.addFilterEqual("active", 1);
		searchObject.addFilterEqual("Status", MandateConstants.STATUS_NEW);
		//OrgReference
		searchObject.addFilter(Filter.isNotNull("OrgReference"));
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_MandateRegistrationList, borderLayout_MandateList, listBoxMandateRegistration, pagingMandateList);
		setItemRender(new MandateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MandateList_MandateSearch);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");
		fillComboBox(this.accType, "", PennantStaticListUtil.getAccTypeList(), "");
		fillComboBox(this.status, "", PennantStaticListUtil.getStatusTypeList(), Collections.singletonList(MandateConstants.STATUS_FIN));

		registerField("mandateID", mandateID, SortOrder.ASC, sortOperator_MandateID, Operators.NUMERIC);
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType, Operators.STRING);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("accNumber", listheader_AccNumber, SortOrder.NONE, accNumber, sortOperator_AccNumber, Operators.STRING);
		registerField("accType", listheader_AccType, SortOrder.NONE, accType, sortOperator_AccType, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.NONE, expiryDate, sortOperator_ExpiryDate, Operators.DATE);
		registerField("bankName", listheader_BankName, SortOrder.NONE, bankName, sortOperator_BankName, Operators.STRING);
		registerField("inputDate",listheader_InputDate,SortOrder.NONE, inputDate, sortOperator_InputDate, Operators.DATE);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("maxLimit", listheader_Amount);
		// Render the page and display the data.
		doRenderPage();
		this.mandateIdMap.clear();
		search();
		
		doSetFieldProperties();
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
	}

	/**
	 * Filling the MandateIdMap details and  based on checked and unchecked events of
	 * listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		for (int i = 0; i < listBoxMandateRegistration.getItems().size(); i++) {
			Listitem listitem = listBoxMandateRegistration.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}
		
		if (listHeader_CheckBox_Comp.isChecked()) {
			List<Long> mandateIdList = getMandateList();
			if(mandateIdList != null){
				for (Long mandateId : mandateIdList) {
					mandateIdMap.put(mandateId, null);
				}
			}
		} else {
			mandateIdMap.clear();
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
		if(checkBox.isChecked()){
			mandateIdMap.put(Long.valueOf(checkBox.getValue().toString()), checkBox.getValue().toString());
		} else {
			mandateIdMap.remove(Long.valueOf(checkBox.getValue().toString()));
		}

		if (mandateIdMap.size() == this.pagingMandateList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class MandateListModelItemRenderer implements ListitemRenderer<Mandate>, Serializable {
		private static final long serialVersionUID = 1L;
	
		@Override
		public void render(Listitem item, Mandate mandate, int count) throws Exception {
			
			Listcell lc;
			
			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setValue(mandate.getId());
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(mandateIdMap.containsKey(mandate.getId()));
			}
			lc.setParent(item);

			lc = new Listcell(mandate.getMandateType());
			lc.setParent(item);
			lc = new Listcell(mandate.getCustCIF());
			lc.setParent(item);
			lc = new Listcell(mandate.getBankName());
			lc.setParent(item);
			lc = new Listcell(mandate.getAccNumber());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.getlabelDesc(mandate.getAccType(), PennantStaticListUtil.getAccTypeList()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(mandate.getMaxLimit(),CurrencyUtil.getFormat(mandate.getMandateCcy())));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(mandate.getExpiryDate()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.getlabelDesc(mandate.getStatus(), PennantStaticListUtil.getStatusTypeList()));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(mandate.getInputDate()));
			lc.setParent(item);
			lc = new Listcell(mandate.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(mandate.getRecordType()));
			lc.setParent(item);
			
			item.setAttribute("id", mandate.getId());
	
			ComponentsCtrl.applyForward(item, "onDoubleClick=onMandateItemDoubleClicked");
		}
	}
	
	
	
	/**
	 * Getting the mandate list using JdbcSearchObject with search criteria..
	 */
	private List<Long> getMandateList() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();

		searchObject.addFilterEqual("active", 1);
		searchObject.addFilterEqual("Status", MandateConstants.STATUS_NEW);
		searchObject.addFilter(Filter.isNotNull("OrgReference"));
		searchObject.addField("mandateID");
		searchObject.addTabelName(this.tableName);
		searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		List<Long> mandateLst = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				mandateLst.add(Long.parseLong(String.valueOf(map.get("mandateID"))));
			}
		}
		return mandateLst;

	}
	
	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_MandateList_MandateSearch(Event event) {
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		search();
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
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onMandateItemDoubleClicked(ForwardEvent event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = (Listitem) event.getOrigin().getTarget();
		Mandate mandate = null;
		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		mandate = mandateService.getMandateById(id);

		if (mandate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND MandateID='" + mandate.getMandateID() + "' AND version=" + mandate.getVersion() + " ";

		if (doCheckAuthority(mandate, whereCond)) {
			// Since workflow is not applicable for mandate registration
			if (isWorkFlowEnabled() && mandate.getWorkflowId() == 0) {
				mandate.setWorkflowId(0);
			}
			doShowDialogPage(mandate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param mandate
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Mandate mandate) {
		logger.debug("Entering");

		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandate", mandate);
		arg.put("mandateRegistrationListCtrl", this);
		arg.put("enqModule", enqiryModule);
		arg.put("fromLoan", false);
		arg.put("registration", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Mandate/MandateDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onClick$btnDownload(Event event) throws Exception {

		logger.debug("Entering" + event.toString());

		List<Long> mandateIdList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			mandateIdList = getMandateList();
		} else {
			mandateIdList = new ArrayList<Long>(mandateIdMap.keySet());
		}
		
		if (mandateIdList.isEmpty()) {
			MessageUtil.showErrorMessage(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}
		
		// Show a confirm box
		String msg = " Out of " + this.pagingMandateList.getTotalSize() + " records, " + this.mandateIdMap.size() + " records are ready for download.\n Are you sure to download? ";
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Conformation"), MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true);
		if (conf == MultiLineMessageBox.NO) {
			return;
		}

		//File file = new File(PathUtil.getPath(PathUtil.DOWNLOAD) + "/" + "MandateData.txt");
		File file = new File("D:/Data-Engine/PFF/Bajaj/Mandate/MandateData.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter filewriter = BatchFileUtil.getFileWriter(file);

		try {
			StringBuilder builder = new StringBuilder();
//			addField(builder, "MandateID");
//			addField(builder, "CustCIF");
//			addField(builder, "CustShrtName");
//			addField(builder, "MandateType");
//			addField(builder, "BranchCode");
//			addField(builder, "MICR");
//			addField(builder, "AccNumber");
//			addField(builder, "StartDate");
//			addField(builder, "ExpiryDate");
//			builder.append("MaxLimit");
//			BatchFileUtil.writeline(filewriter, builder.toString());
			for (Long mandateId : mandateIdList) {
				Mandate mandate = mandateService.getMandateById(mandateId);
				StringBuilder builder1 = new StringBuilder();
				addField(builder1, String.valueOf(mandate.getMandateID()));
				addField(builder1, mandate.getCustCIF());
				addField(builder1, mandate.getCustShrtName());
				addField(builder1, mandate.getMandateType());
				addField(builder1, mandate.getBranchCode());
				addField(builder1, mandate.getMICR());
				addField(builder1, mandate.getAccNumber());
				addField(builder1, DateUtility.format(mandate.getStartDate(), PennantConstants.DBDateFormat));
				addField(builder1, DateUtility.format(mandate.getExpiryDate(), PennantConstants.DBDateFormat));
				builder.append(String.valueOf(mandate.getMaxLimit()));
				BatchFileUtil.writeline(filewriter, builder1.toString());
				mandate.setUserDetails(getUserWorkspace().getLoggedInUser());
				mandateService.processDownload(mandate);
			}
			MessageUtil.showInfoMessage(Labels.getLabel("MandateDataList_Request", new String[] { String.valueOf(mandateIdList.size()) }));
		} catch (Exception e) {
			logger.error("Exception", e);
		} finally {
			filewriter.close();
			this.mandateIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
		}
	}
	
	
	public void addField(StringBuilder line, String value) {
		line.append(value).append(BatchFileUtil.DELIMITER);
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public MandateStatusDAO getMandateStatusDAO() {
		return mandateStatusDAO;
	}

	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

}