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
 * * FileName : CustomerDownloadListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-07-2019 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-07-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.filedownload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.filedownload.CustomerDownloadService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.GLEMSCustomerProcess;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerDownloadList.zul<br>
 * ************************************************************<br>
 * 
 */
public class CustomerDownloadListCtrl extends GFCBaseListCtrl<Customer> {
	private static final long serialVersionUID = 1L;

	protected Window window_CustomerDownloadList;
	protected Borderlayout borderLayout_CustomerDownloadList;
	protected Listbox listBoxCustomerDownload;
	protected Paging pagingCustomerDownloadList;

	@Autowired
	private GLEMSCustomerProcess glemsCustomerProcess;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_CustCoreBank;
	protected Listheader listheader_CustShrtName;
	protected Listheader listheader_CustDftBranch;
	protected Listheader listheader_CustCtgCode;
	protected Listheader listheader_CustTypeCode;
	protected Listheader listheader_RequestStage;

	protected Textbox custCIF;
	protected Textbox custCoreBank;
	protected Combobox custCtgCode;
	protected Textbox custTypeCode;
	protected Textbox custShrtName;
	protected Textbox custDftBranch;
	protected Datebox toDate;
	protected Datebox fromDate;

	protected Listbox sortOperator_custDftBranch;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_custCoreBank;
	protected Listbox sortOperator_custCtgCode;
	protected Listbox sortOperator_custTypeCode;
	protected Listbox sortOperator_custShrtName;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	protected Button button_Search;
	protected Button btnDownload;
	protected Button btnFinType;
	protected int oldVar_sortOperator_finType;

	private Map<Long, Customer> customerDownloadMap = new HashMap<Long, Customer>();

	private final List<ValueLabel> custCtgCodeList = PennantAppUtil.getcustCtgCodeList();
	@Autowired
	private CustomerDownloadService customerDownloadService;

	private ExtendedFieldDetailsService extendedFieldDetailsService;

	public CustomerDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Customer";
		super.pageRightName = "CustomerList";
		super.tableName = "Customers_AView";
		super.queueTableName = "Customers_AView";
		super.enquiryTableName = "Customers_AView";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CustomerDownloadList, borderLayout_CustomerDownloadList, listBoxCustomerDownload,
				pagingCustomerDownloadList);
		setItemRender(new CustomerDownloadListModelItemRenderer());

		registerButton(button_Search);

		/*
		 * registerField("custId"); registerField("custCIF", listheader_CustCIF, SortOrder.ASC, custCIF,
		 * sortOperator_custCIF, Operators.SIMPLESTRING); registerField("custCoreBank", listheader_CustCoreBank,
		 * SortOrder.NONE, custCoreBank, sortOperator_custCoreBank, Operators.STRING); registerField("custShrtName",
		 * listheader_CustShrtName, SortOrder.NONE, custShrtName, sortOperator_custShrtName, Operators.SIMPLESTRING);
		 * registerField("custDftBranch", listheader_CustDftBranch, SortOrder.NONE, custDftBranch,
		 * sortOperator_custDftBranch, Operators.STRING);
		 * 
		 * fillComboBox(this.custCtgCode, "", custCtgCodeList, ""); registerField("custCtgCode", listheader_CustCtgCode,
		 * SortOrder.NONE, custCtgCode, sortOperator_custCtgCode, Operators.STRING);
		 * 
		 * registerField("lovDescCustCtgCodeName"); registerField("lovDescCustTypeCodeName", listheader_CustTypeCode,
		 * SortOrder.NONE, custTypeCode, sortOperator_custTypeCode, Operators.STRING);
		 * registerField("LovDescRequestStage", listheader_RequestStage);
		 */

		// Render the page and display no data when the page loaded for the
		// first time.
		doRenderPage();
		this.customerDownloadMap.clear();
		doSetFieldProperties();
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());

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
		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_Search(Event event) {
		logger.debug(Literal.ENTERING);

		this.customerDownloadMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		renderCustomers();

		if (listBoxCustomerDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listBoxCustomerDownload.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		List<Customer> customerList;
		List<Long> custId = new ArrayList<>();

		if (listHeader_CheckBox_Comp.isChecked()) {
			customerDownloadMap.clear();
			customerDownloadMap = getCustomersDetails();
			customerList = new ArrayList<Customer>(customerDownloadMap.values());
		} else {
			customerList = new ArrayList<Customer>(customerDownloadMap.values());
		}

		if (customerList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}

		if (CollectionUtils.isNotEmpty(customerList)) {
			for (Customer customer : customerList) {
				custId.add(customer.getCustID());
			}
			try {

				this.customerDownloadService.processDownload(custId);
				String filePath = glemsCustomerProcess.getFilePath();
				downloadFromServer(filePath);

				MessageUtil.showMessage("file downloaded successfully");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError("file downloaded failed");
			}
		}
		if (listBoxCustomerDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public String downloadFromServer(String filePath) throws IOException {
		String Path = App.getProperty("external.interface.glems.customer.path");
		String CustomerPath = Path.concat(File.separator);
		String fileName = StringUtils.substringAfter(filePath, CustomerPath);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		InputStream inputStream = new FileInputStream(filePath);
		int data;
		while ((data = inputStream.read()) >= 0) {
			stream.write(data);
		}
		inputStream.close();
		inputStream = null;
		Filedownload.save(stream.toByteArray(), "application/octet-stream", fileName);
		stream.close();
		/*
		 * FileDelete delete = new FileDelete(); delete.delete(Path);
		 */
		return filePath;
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
		tab.setLabel("Disbursement File Control");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		doRemoveValidation();
		this.toDate.setValue(null);
		this.fromDate.setValue(null);
		renderCustomers();
	}

	public void onClick_listHeaderCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxCustomerDownload.getItems().size(); i++) {
			Listitem listitem = listBoxCustomerDownload.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxCustomerDownload.getItems().size() > 0) {
			customerDownloadMap = getCustomersDetails();
		} else {
			customerDownloadMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick_listCellCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		Customer customer = (Customer) checkBox.getAttribute("Customer");

		if (checkBox.isChecked()) {
			customerDownloadMap.put(customer.getCustID(), customer);
		} else {
			customerDownloadMap.remove(customer.getCustID());
		}

		if (customerDownloadMap.size() == this.pagingCustomerDownloadList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private Map<Long, Customer> getCustomersDetails() {
		logger.debug(Literal.ENTERING);

		List<Customer> list = renderCustomers();

		Map<Long, Customer> customerDownloadMap = new HashMap<Long, Customer>();

		if (list == null || list.isEmpty()) {
			return customerDownloadMap;
		}

		for (Customer customer : list) {
			customerDownloadMap.put(customer.getCustID(), customer);
		}
		logger.debug(Literal.LEAVING);
		return customerDownloadMap;
	}

	private List<Customer> renderCustomers() {
		logger.debug(Literal.ENTERING);

		doSetValidations();
		List<Customer> renderList = searchCustomer();

		logger.debug(Literal.LEAVING);
		return renderList;
	}

	private List<Customer> searchCustomer() {
		logger.debug(Literal.ENTERING);
		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);

		searchObject.addField("custId");
		searchObject.addField("custCIF");
		searchObject.addField("custCoreBank");
		searchObject.addField("custShrtName");
		searchObject.addField("custDftBranch");
		searchObject.addField("custCtgCode");
		searchObject.addField("lovDescCustCtgCodeName");
		searchObject.addField("lovDescCustTypeCodeName");
		searchObject.addField("LovDescRequestStage");
		searchObject.addField("recordStatus");
		searchObject.addField("recordType");
		searchObject.addField("lastMntOn");
		searchObject.addTabelName(this.tableName);

		if (fromDate.getValue() != null && toDate.getValue() != null) {
			String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
			String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);

			StringBuilder whereClause = new StringBuilder();
			whereClause.append("(LASTMNTON >= ").append("'").append(fromDate).append("'").append(" AND LASTMNTON <= ")
					.append("'").append(toDate).append("'").append(")");
			searchObject.addWhereClause(whereClause.toString());
		}

		for (SearchFilterControl searchControl : searchControls) {
			Filter filters = searchControl.getFilter();
			if (filters != null) {
				searchObject.addFilter(filters);
			}
		}

		List<Customer> searchList = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		Map<String, String> retailMap = null;
		Map<String, String> corpMap = null;
		Map<String, String> smeMap = null;
		retailMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_INDIV, "", "", true);
		corpMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_CORP, "", "", true);
		smeMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_SME, "", "", true);
		List<Customer> renderList = new ArrayList<Customer>();
		if (CollectionUtils.isNotEmpty(searchList)) {
			for (Customer customer : searchList) {
				if (retailMap != null && retailMap.containsKey(customer.getCustCIF())) {
					renderList.add(customer);
					continue;
				}
				if (corpMap != null && corpMap.containsKey(customer.getCustCIF())) {
					renderList.add(customer);
					continue;
				}
				if (smeMap != null && smeMap.containsKey(customer.getCustCIF())) {
					renderList.add(customer);
					continue;
				}
			}
		}
		this.listbox.setItemRenderer(new CustomerDownloadListModelItemRenderer());
		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().initList(renderList, this.listBoxCustomerDownload, this.paging);
		logger.debug(Literal.LEAVING);
		return renderList;
	}

	private void doSetValidations() {

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (toDate.getValue() != null || fromDate.getValue() != null) {
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
			if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
				throw new WrongValueException(this.toDate, "To date should be greater than or equal to From date.");
			}
		} else {
			this.toDate.setConstraint("");
			this.fromDate.setConstraint("");
			this.fromDate.setErrorMessage("");
			this.toDate.setErrorMessage("");
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		logger.debug("Leaving ");

	}

	private class CustomerDownloadListModelItemRenderer implements ListitemRenderer<Customer>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, Customer customer, int count) {
			Listcell lc;
			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("Customer", customer);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {

			}
			lc.setParent(item);

			lc = new Listcell(customer.getCustCIF().trim());
			lc.setParent(item);
			lc = new Listcell(customer.getCustCoreBank());
			lc.setParent(item);
			lc = new Listcell(customer.getCustShrtName());
			lc.setParent(item);
			lc = new Listcell(customer.getCustDftBranch());
			lc.setParent(item);
			lc = new Listcell(customer.getLovDescCustCtgCodeName());
			lc.setParent(item);
			lc = new Listcell(customer.getLovDescCustTypeCodeName());
			lc.setParent(item);
			lc = new Listcell(StringUtils.equals(customer.getLovDescRequestStage(), ",") ? ""
					: customer.getLovDescRequestStage());
			lc.setParent(item);
			lc = new Listcell(customer.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateDate(customer.getLastMntOn(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			item.setAttribute("id", customer.getCustID());
			item.setAttribute("data", customer);
		}
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	/**
	 * @param customerDownloadService the customerDownloadService to set
	 */
	public void setCustomerDownloadService(CustomerDownloadService customerDownloadService) {
		this.customerDownloadService = customerDownloadService;
	}

}