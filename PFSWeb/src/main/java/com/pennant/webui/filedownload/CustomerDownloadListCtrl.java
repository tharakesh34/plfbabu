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
 * FileName    		:  CustomerDownloadListCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-07-2019 															*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-07-2019       Pennant	                 0.1                                            * 
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

package com.pennant.webui.filedownload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.filedownload.CustomerDownloadService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;	

/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/CustomerDownloadList.zul<br>
 * ************************************************************<br>
 * 
 */
public class CustomerDownloadListCtrl extends GFCBaseListCtrl<Customer> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CustomerDownloadListCtrl.class);

	protected Window window_CustomerDownloadList;
	protected Borderlayout borderLayout_CustomerDownloadList;
	protected Listbox listBoxCustomerDownload;
	protected Paging pagingCustomerDownloadList;

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
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CustomerDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CustomerDownloadList, borderLayout_CustomerDownloadList, listBoxCustomerDownload,
				pagingCustomerDownloadList);
		setItemRender(new CustomerDownloadListModelItemRenderer());

		registerButton(button_Search);

		registerField("custId");
		registerField("custCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.SIMPLESTRING);
		registerField("custCoreBank", listheader_CustCoreBank, SortOrder.NONE, custCoreBank, sortOperator_custCoreBank,
				Operators.STRING);
		registerField("custShrtName", listheader_CustShrtName, SortOrder.NONE, custShrtName, sortOperator_custShrtName,
				Operators.SIMPLESTRING);
		registerField("custDftBranch", listheader_CustDftBranch, SortOrder.NONE, custDftBranch,
				sortOperator_custDftBranch, Operators.STRING);

		fillComboBox(this.custCtgCode, "", custCtgCodeList, "");
		registerField("custCtgCode", listheader_CustCtgCode, SortOrder.NONE, custCtgCode, sortOperator_custCtgCode,
				Operators.STRING);

		registerField("lovDescCustCtgCodeName");
		registerField("lovDescCustTypeCodeName", listheader_CustTypeCode, SortOrder.NONE, custTypeCode,
				sortOperator_custTypeCode, Operators.STRING);
		registerField("LovDescRequestStage", listheader_RequestStage);

		// Render the page and display no data when the page loaded for the
		// first time.
		doRenderPage();
		this.customerDownloadMap.clear();
		doSetFieldProperties();
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
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

	public void onClick$btnDownload(Event event) throws Exception {
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

	public void onClick$btnRefresh(Event event) {
		doReset();
		renderCustomers();
	}

	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
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

	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
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
		searchObject.addTabelName(this.tableName);

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
				PennantConstants.PFF_CUSTCTG_INDIV, "", "");
		corpMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_CORP, "", "");
		smeMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_SME, "", "");
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

	private class CustomerDownloadListModelItemRenderer implements ListitemRenderer<Customer>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, Customer customer, int count) throws Exception {
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
			lc = new Listcell(PennantJavaUtil.getLabel(customer.getRecordType()));
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
	 * @param customerDownloadService
	 *            the customerDownloadService to set
	 */
	public void setCustomerDownloadService(CustomerDownloadService customerDownloadService) {
		this.customerDownloadService = customerDownloadService;
	}

}