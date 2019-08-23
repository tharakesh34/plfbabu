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
 * FileName    		:  LimitDownloadListCtrl.java                                        	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  																		*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-08-2019       Pennant	                 0.1                                            * 
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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.filedownload.LimitDownloadService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/filedownload/LimitDownloadListCtrl.zul<br>
 * ************************************************************<br>
 * 
 */
public class LimitDownloadListCtrl extends GFCBaseListCtrl<LimitHeader> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LimitDownloadListCtrl.class);

	protected Window window_LimitDownloadList;
	protected Borderlayout borderLayout_limitDownloadList;
	protected Listbox listBoxLimitDownload;
	protected Paging pagingLimitDownloadList;

	// List headers
	protected Listheader listheader_Name;
	protected Listheader listheader_Id;
	protected Listheader listheader_ResponsibleBranch;
	protected Listheader listheader_LimitStructureCode;
	protected Listheader listheader_Currency;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_ReviewDate;
	protected Listheader listheader_Active;
	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_RecordType;

	protected Textbox name;
	protected Textbox id;
	protected Textbox responsibleBranch;
	protected Textbox limitStructureCode;
	protected Textbox currency;
	protected Datebox expiryDate;
	protected Datebox reviewDate;
	protected Checkbox active;
	protected Textbox recordStatus;
	protected Listbox recordType;

	protected Listbox sortOperator_Name;
	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_ResponsibleBranch;
	protected Listbox sortOperator_LimitStructureCode;
	protected Listbox sortOperator_Currency;
	protected Listbox sortOperator_ExpiryDate;
	protected Listbox sortOperator_ReviewDate;
	protected Listbox sortOperator_active;
	protected Listbox sortOperator_RecordStatus;
	protected Listbox sortOperator_RecordType;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	protected Button button_Search;
	protected Button btnDownload;
	protected Button btnFinType;
	protected int oldVar_sortOperator_finType;

	private Map<Long, LimitHeader> limitDownloadMap = new HashMap<Long, LimitHeader>();
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	@Autowired
	private LimitDownloadService limitDownloadService;

	public LimitDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LimitHeader";
		super.pageRightName = "CustomerLimitDetailsList";
		super.tableName = "LimitHeader_AView";
		super.queueTableName = "LimitHeader_View";
		super.enquiryTableName = "LimitHeader_AView";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LimitDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LimitDownloadList, borderLayout_limitDownloadList, listBoxLimitDownload,
				pagingLimitDownloadList);
		setItemRender(new LimitDownloadListModelItemRenderer());

		registerButton(button_Search);

		//id.setType(DataType.LONG.name());
		registerField("customerId");
		registerField("CustCIF", listheader_Id, SortOrder.ASC, id, sortOperator_Id, Operators.STRING);
		registerField("CustShrtName", listheader_Name, SortOrder.NONE, name, sortOperator_Name, Operators.STRING);
		registerField("responsibleBranchName", listheader_ResponsibleBranch, SortOrder.ASC, responsibleBranch,
				sortOperator_ResponsibleBranch, Operators.STRING);
		registerField("limitCcy", listheader_Currency, SortOrder.NONE, currency, sortOperator_Currency,
				Operators.STRING);
		registerField("limitStructureCode", listheader_LimitStructureCode, SortOrder.NONE, limitStructureCode,
				sortOperator_LimitStructureCode, Operators.STRING);
		registerField("limitExpiryDate", listheader_ExpiryDate, SortOrder.NONE, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("LimitRvwDate", listheader_ReviewDate, SortOrder.NONE, reviewDate, sortOperator_ReviewDate,
				Operators.DATE);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);
		registerField("RecordStatus");
		registerField("RecordType");

		// Render the page and display no data when the page loaded for the
		// first time.
		doRenderPage();
		this.limitDownloadMap.clear();
		doSetFieldProperties();
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.reviewDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());

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

		this.limitDownloadMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		renderLimits();

		if (listBoxLimitDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listBoxLimitDownload.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		List<LimitHeader> limitList;
		List<Long> limitRef = new ArrayList<>();

		if (listHeader_CheckBox_Comp.isChecked()) {
			limitDownloadMap.clear();
			limitDownloadMap = getLimitDetails();
			limitList = new ArrayList<LimitHeader>(limitDownloadMap.values());
		} else {
			limitList = new ArrayList<LimitHeader>(limitDownloadMap.values());
		}

		if (limitList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}

		if (CollectionUtils.isNotEmpty(limitList)) {
			//Iterating and collection list of custCif to limitRef 
			limitList.stream().forEach(s -> limitRef.add(s.getHeaderId()));
			try {
				limitDownloadService.processDownload(limitRef);
				MessageUtil.showMessage("file downloaded successfully");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError("file downloaded failed");
			}
		}
		if (listBoxLimitDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		renderLimits();
	}

	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxLimitDownload.getItems().size(); i++) {
			Listitem listitem = listBoxLimitDownload.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxLimitDownload.getItems().size() > 0) {
			limitDownloadMap = getLimitDetails();
		} else {
			limitDownloadMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		LimitHeader limitHeader = (LimitHeader) checkBox.getAttribute("limitHeader");

		if (checkBox.isChecked()) {
			limitDownloadMap.put(limitHeader.getHeaderId(), limitHeader);
		} else {
			limitDownloadMap.remove(limitHeader.getHeaderId());
		}

		if (limitDownloadMap.size() == this.pagingLimitDownloadList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private Map<Long, LimitHeader> getLimitDetails() {
		logger.debug(Literal.ENTERING);

		List<LimitHeader> list = renderLimits();

		Map<Long, LimitHeader> limitHeaderDownloadMap = new HashMap<Long, LimitHeader>();

		if (CollectionUtils.isEmpty(list)) {
			return limitHeaderDownloadMap;
		} else {
			limitHeaderDownloadMap = list.stream()
					.collect(Collectors.toMap(LimitHeader::getHeaderId, Function.identity()));

		}
		for (LimitHeader limitHeader : list) {
			limitHeaderDownloadMap.put(limitHeader.getHeaderId(), limitHeader);
		}
		logger.debug(Literal.LEAVING);
		return limitHeaderDownloadMap;
	}

	private List<LimitHeader> renderLimits() {
		logger.debug(Literal.ENTERING);
		JdbcSearchObject<LimitHeader> searchObject = new JdbcSearchObject<LimitHeader>(LimitHeader.class);
		searchObject.addField("custShrtName");
		searchObject.addField("customerId");
		searchObject.addField("headerId");
		searchObject.addField("custCIF");
		searchObject.addField("responsibleBranchName");
		searchObject.addField("limitCcy");
		searchObject.addField("limitStructureCode");
		searchObject.addField("limitExpiryDate");
		searchObject.addField("limitRvwDate");
		searchObject.addField("active");
		searchObject.addTabelName(this.tableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filters = searchControl.getFilter();
			if (filters != null) {
				searchObject.addFilter(filters);
			}
		}

		List<LimitHeader> searchList = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		this.listbox.setItemRenderer(new LimitDownloadListModelItemRenderer());
		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().initList(searchList, this.listBoxLimitDownload, this.paging);
		logger.debug(Literal.LEAVING);
		return searchList;
	}

	public LimitDownloadService getLimitDownloadService() {
		return limitDownloadService;
	}

	public void setLimitDownloadService(LimitDownloadService limitDownloadService) {
		this.limitDownloadService = limitDownloadService;
	}

	private class LimitDownloadListModelItemRenderer implements ListitemRenderer<LimitHeader>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, LimitHeader limitHeader, int count) throws Exception {
			Listcell lc;
			Label lb;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("limitHeader", limitHeader);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {

			}
			lc.setParent(item);

			lc = new Listcell(String.valueOf(limitHeader.getCustCIF()));
			lc.setParent(item);

			lc = new Listcell();
			lb = new Label();
			lb.setValue(limitHeader.getCustShrtName());
			lb.setParent(lc);
			lc.setParent(item);
			lc.setTooltip(limitHeader.getCustShrtName());
			lc = new Listcell(limitHeader.getLimitStructureCode());
			lc.setParent(item);
			lc = new Listcell(limitHeader.getResponsibleBranchName());
			lc.setParent(item);
			lc = new Listcell(limitHeader.getLimitCcy());
			lc.setParent(item);
			lc = new Listcell(
					PennantAppUtil.formateDate(limitHeader.getLimitExpiryDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.formateDate(limitHeader.getLimitRvwDate(), PennantConstants.dateFormat));
			lc.setParent(item);

			lc = new Listcell();
			Checkbox ckActive = new Checkbox();
			ckActive.setChecked(limitHeader.isActive());
			ckActive.setDisabled(true);
			ckActive.setParent(lc);
			lc.setParent(item);

			lc = new Listcell(limitHeader.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(limitHeader.getRecordType()));
			lc.setParent(item);
			item.setAttribute("limitHeader", limitHeader);
		}
	}

	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		doSearchCustomerCIF();
		logger.debug(Literal.LEAVING);
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug(Literal.LEAVING);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.id.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.id.setValue(customer.getCustCIF());
		} else {
			this.id.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

}