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

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.MultiLineMessageBox;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateRegistrationListCtrl extends GFCBaseListCtrl<Mandate> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(MandateRegistrationListCtrl.class);

	protected Window window_MandateRegistrationList;
	protected Borderlayout borderLayout_MandateList;
	protected Paging pagingMandateList;
	protected Listbox listBoxMandateRegistration;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_MandateType;
	protected Listheader listheader_BankName;
	protected Listheader listheader_AccNumber;
	protected Listheader listheader_AccType;
	protected Listheader listheader_Amount;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_Status;
	protected Listheader listheader_InputDate;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;

	protected Checkbox list_CheckBox;

	protected Button button_MandateList_NewMandate;
	protected Button button_MandateList_MandateSearch;
	protected Button btnDownload;

	protected Longbox mandateID;
	protected Combobox mandateType;
	protected Textbox custCIF;
	protected Textbox bankName;
	protected Combobox status;
	protected Textbox accNumber;
	protected Combobox accType;
	protected Datebox expiryDate;

	protected Datebox fromDate;
	protected Datebox toDate;
	protected Uppercasebox branchDetails;
	protected Button btnbranchDetails;

	protected Listbox sortOperator_MandateID;
	protected Listbox sortOperator_CustCIF;
	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_BankName;
	protected Listbox sortOperator_AccNumber;
	protected Listbox sortOperator_AccType;
	protected Listbox sortOperator_ExpiryDate;
	protected Listbox sortOperator_Status;
	protected Listbox sortOperator_btnbranchDetails;

	private transient MandateService mandateService;
	private DataSource dataSource;
	private transient boolean validationOn;

	private Map<Long, String> mandateIdMap = new HashMap<Long, String>();

	/**
	 * default constructor.<br>
	 */
	public MandateRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MandateRegistration";
		super.pageRightName = "MandateRegistration";
		super.tableName = "Mandates_AView";
		super.queueTableName = "Mandates_AView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_MandateRegistrationList, borderLayout_MandateList, listBoxMandateRegistration,
				pagingMandateList);
		setItemRender(new MandateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MandateList_MandateSearch);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");
		fillComboBox(this.accType, "", PennantStaticListUtil.getAccTypeList(), "");
		fillComboBox(this.status, "", PennantStaticListUtil.getStatusTypeList(),
				Collections.singletonList(MandateConstants.STATUS_FIN));

		registerField("FromDate");
		registerField("ToDate");

		registerField("BranchCode", branchDetails, SortOrder.ASC, sortOperator_btnbranchDetails, Operators.MULTISELECT);
		registerField("mandateID", mandateID, SortOrder.ASC, sortOperator_MandateID, Operators.NUMERIC);
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType,
				Operators.STRING);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("accNumber", listheader_AccNumber, SortOrder.NONE, accNumber, sortOperator_AccNumber,
				Operators.STRING);
		registerField("accType", listheader_AccType, SortOrder.NONE, accType, sortOperator_AccType, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.NONE, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("bankName", listheader_BankName, SortOrder.NONE, bankName, sortOperator_BankName,
				Operators.STRING);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("maxLimit", listheader_Amount);
		// Render the page and display the data.
		doRenderPage();
		this.mandateIdMap.clear();
		doSetFieldProperties();

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetFieldProperties() {

		this.fromDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.toDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.LONG_DATE.getPattern());

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
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
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
			if (mandateIdList != null) {
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
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		if (checkBox.isChecked()) {
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
			lc = new Listcell(PennantApplicationUtil.amountFormate(mandate.getMaxLimit(),
					CurrencyUtil.getFormat(mandate.getMandateCcy())));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(mandate.getExpiryDate()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.getlabelDesc(mandate.getStatus(),
					PennantStaticListUtil.getStatusTypeList()));
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
		// searchObject.addFilter(Filter.isNotNull("OrgReference"));
		searchObject.addField("mandateID");
		searchObject.addTabelName(this.tableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
		String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);

		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(INPUTDATE >= ").append("'").append(fromDate).append("'").append(" AND INPUTDATE <= ")
				.append("'").append(toDate).append("'").append(")");
		searchObject.addWhereClause(whereClause.toString());

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
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_MandateList_MandateSearch(Event event) {
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		doSetValidations();
		search();

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	@Override
	public void search() {
		logger.debug("Entering");

		this.searchObject.clearFilters();

		if (paging != null) {
			this.paging.setActivePage(0);
		}

		if (enqiryModule) {
			if (fromApproved != null && fromWorkFlow != null) {
				if (fromApproved.isChecked()) {
					this.searchObject.addTabelName(tableName);
				} else if (fromWorkFlow.isChecked()) {
					this.searchObject.addTabelName(enquiryTableName);
				} else {
					this.searchObject.addTabelName(tableName);
				}
			}
		}

		doAddFilters();

		String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
		String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);

		searchObject.addFilterEqual("active", 1);
		searchObject.addFilterEqual("Status", MandateConstants.STATUS_NEW);
		// OrgReference
		// searchObject.addFilter(Filter.isNotNull("OrgReference"));

		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(INPUTDATE >= ").append("'").append(fromDate).append("'").append(" AND INPUTDATE <= ")
				.append("'").append(toDate).append("'").append(")");
		this.searchObject.addWhereClause(whereClause.toString());

		this.listbox.setItemRenderer(new MandateListModelItemRenderer());

		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().init(this.searchObject, this.listbox, this.paging);

		logger.debug("Leaving");
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (App.DATABASE == Database.ORACLE && "recordType".equals(filter.getProperty())
						&& Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					this.searchObject.addFilterOr(filters);
				} else {
					this.searchObject.addFilter(filter);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnbranchDetails(Event event) {
		logger.debug("Entering  " + event.toString());

		setSearchValue(sortOperator_btnbranchDetails, this.branchDetails, "DataEngine");

		logger.debug("Leaving" + event.toString());
	}

	private void doSetValidations() {

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.fromDate.isDisabled())
				this.fromDate.setConstraint(new PTDateValidator("From Date", true));
			this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.toDate.isDisabled())
				this.toDate.setConstraint(new PTDateValidator("To Date", true));
			this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
			throw new WrongValueException(this.toDate, "To date should be greater than or equal to From date.");
		}

	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");

		logger.debug("Leaving ");

	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		this.listBoxMandateRegistration.getItems().clear();
		this.fromDate.setValue(null);
		this.toDate.setValue(null);

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
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
		String msg = "You have selected " + this.mandateIdMap.size() + " Mandate(s) out of "
				+ this.pagingMandateList.getTotalSize() + ".\nDo you want to continue?";
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Conformation"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true);
		if (conf == MultiLineMessageBox.NO) {
			return;
		}
		try {
			btnDownload.setDisabled(true);
			DataEngineExport dataEngine = null;
			dataEngine = new DataEngineExport(dataSource, getUserWorkspace().getLoggedInUser().getLoginUsrID(),
					App.DATABASE.name());

			Map<String, Object> filterMap = new HashMap<>();
			Map<String, Object> parameterMap = new HashMap<>();
			filterMap.put("MANDATEID", mandateIdList);
			filterMap.put("FROMDATE", this.fromDate.getValue());
			filterMap.put("TODATE", this.toDate.getValue());
			parameterMap.put("USER_NAME", getUserWorkspace().getLoggedInUser().getUserName());

			if (StringUtils.trimToNull(this.branchDetails.getValue()) != null) {
				filterMap.put("BRANCHCODE", Arrays.asList(this.branchDetails.getValue().split(",")));
			}

			dataEngine.setFilterMap(filterMap);
			dataEngine.setParameterMap(parameterMap);
			dataEngine.setUserName(getUserWorkspace().getLoggedInUser().getUserName());
			dataEngine.exportData("MANDATES_EXPORT");

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("module", "MANDATES");

			MessageUtil.showMessage("File Download process initiated.");
			createNewPage("/WEB-INF/pages/InterfaceBajaj/FileDownloadList.zul", "menu_Item_MandatesFileDownlaods", args);

		} catch (Exception e) {
			logger.error("Exception :", e);
		} finally {
			this.mandateIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
			btnDownload.setDisabled(false);
			logger.debug("Leaving");
		}
	}

	protected void createNewPage(String uri, String tabName, Map<String, Object> args) {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Center center = bl.getCenter();
		final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
				.getFellow("tabsIndexCenter");

		Tab tab = null;
		if (tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_")) != null) {
			tab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
			if (tab != null) {
				tab.close();
			}
		}
		tab = new Tab();
		tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
		tab.setLabel(Labels.getLabel(tabName));
		tab.setClosable(true);
		tab.setParent(tabs);
		tab.setLabel("Mandate File Control");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}
}