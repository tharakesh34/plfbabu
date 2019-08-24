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
 * FileName    		:  CollateralDownloadListCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-08-2019 															*
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.filedownload.CollateralDownloadService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/filedownload/CollateralDownloadListCtrl.zul<br>
 * ************************************************************<br>
 * 
 */
public class CollateralDownloadListCtrl extends GFCBaseListCtrl<CollateralSetup> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CollateralDownloadListCtrl.class);

	protected Window window_CollateralDownloadList;
	protected Borderlayout borderLayout_CollateralDownloadList;
	protected Listbox listBoxCollateralDownload;
	protected Paging pagingCollateralDownloadList;

	// List headers
	protected Listheader listheader_CollateralRef;
	protected Listheader listheader_DepositorCif;
	protected Listheader listheader_CollateralType;
	protected Listheader listheader_CollateralCcy;
	protected Listheader listheader_MaxCollateralValue;
	protected Listheader listheader_SpecialLTV;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_ReviewFrequency;
	protected Listheader listheader_NextReviewDate;

	protected Textbox collateralRef;
	protected Textbox collateralType;
	protected Textbox collateralCcy;
	protected Datebox expiryDate;
	protected Datebox nextReviewDate;
	protected Textbox depositorCif;

	protected Listbox sortOperator_CollateralRef;
	protected Listbox sortOperator_DepositorCif;
	protected Listbox sortOperator_CollateralType;
	protected Listbox sortOperator_CollateralCcy;
	protected Listbox sortOperator_ExpiryDate;
	protected Listbox sortOperator_NextReviewDate;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	protected Button button_Search;
	protected Button btnDownload;
	protected Button btnFinType;
	protected int oldVar_sortOperator_finType;

	private Map<String, CollateralSetup> collateralDownloadMap = new HashMap<String, CollateralSetup>();

	protected JdbcSearchObject<Customer> custCIFSearchObject;

	@Autowired
	private CollateralDownloadService collateralDownloadService;

	public CollateralDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CollateralSetup";
		super.pageRightName = "CollateralSetupList";
		super.tableName = "CollateralSetup_AView";
		super.queueTableName = "CollateralSetup_AView";
		super.enquiryTableName = "CollateralSetup_AView";
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
	public void onCreate$window_CollateralDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CollateralDownloadList, borderLayout_CollateralDownloadList, listBoxCollateralDownload,
				pagingCollateralDownloadList);
		setItemRender(new CollateralDownloadListModelItemRenderer());

		registerButton(button_Search);
		registerField("depositorCif", listheader_DepositorCif, SortOrder.ASC, depositorCif, sortOperator_DepositorCif,
				Operators.STRING);
		registerField("collateralRef", listheader_CollateralRef, SortOrder.ASC, collateralRef,
				sortOperator_CollateralRef, Operators.STRING);
		registerField("collateralType", listheader_CollateralType, SortOrder.ASC, collateralType,
				sortOperator_CollateralType, Operators.STRING);
		registerField("collateralCcy", listheader_CollateralCcy, SortOrder.ASC, collateralCcy,
				sortOperator_CollateralCcy, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.ASC, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("nextReviewDate", listheader_NextReviewDate, SortOrder.ASC, nextReviewDate,
				sortOperator_NextReviewDate, Operators.DATE);
		registerField("nextRoleCode");
		registerField("finReference");
		registerField("status");

		// Render the page and display no data when the page loaded for the
		// first time.
		doRenderPage();
		this.collateralDownloadMap.clear();
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

		this.collateralDownloadMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		renderCollaterals();

		if (listBoxCollateralDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listBoxCollateralDownload.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		List<CollateralSetup> collateralList;
		List<String> collateralRef = new ArrayList<>();

		if (listHeader_CheckBox_Comp.isChecked()) {
			collateralDownloadMap.clear();
			collateralDownloadMap = getCollateralDetails();
			collateralList = new ArrayList<CollateralSetup>(collateralDownloadMap.values());
		} else {
			collateralList = new ArrayList<CollateralSetup>(collateralDownloadMap.values());
		}

		if (collateralList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}

		if (CollectionUtils.isNotEmpty(collateralList)) {
			//Iterating and collection list of custCif to limitRef 
			collateralList.stream().forEach(s -> collateralRef.add(s.getCollateralRef()));
			try {
				collateralDownloadService.processDownload(collateralRef);
				MessageUtil.showMessage("file downloaded successfully");
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError("file downloaded failed");
			}
		}
		if (listBoxCollateralDownload.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		renderCollaterals();
	}

	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxCollateralDownload.getItems().size(); i++) {
			Listitem listitem = listBoxCollateralDownload.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxCollateralDownload.getItems().size() > 0) {
			collateralDownloadMap = getCollateralDetails();
		} else {
			collateralDownloadMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		CollateralSetup collateralSetup = (CollateralSetup) checkBox.getAttribute("CollateralSetup");

		if (checkBox.isChecked()) {
			collateralDownloadMap.put(collateralSetup.getCollateralRef(), collateralSetup);
		} else {
			collateralDownloadMap.remove(collateralSetup.getCollateralRef());
		}

		if (collateralDownloadMap.size() == this.pagingCollateralDownloadList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private Map<String, CollateralSetup> getCollateralDetails() {
		logger.debug(Literal.ENTERING);

		List<CollateralSetup> list = renderCollaterals();

		Map<String, CollateralSetup> collateralDownloadMap = new HashMap<String, CollateralSetup>();

		if (list == null || list.isEmpty()) {
			return collateralDownloadMap;
		}

		for (CollateralSetup collateralSetup : list) {
			collateralDownloadMap.put(collateralSetup.getCollateralRef(), collateralSetup);
		}
		logger.debug(Literal.LEAVING);
		return collateralDownloadMap;
	}

	private List<CollateralSetup> renderCollaterals() {
		logger.debug(Literal.ENTERING);
		JdbcSearchObject<CollateralSetup> searchObject = new JdbcSearchObject<CollateralSetup>(CollateralSetup.class);

		searchObject.addField("depositorCif");
		searchObject.addField("collateralRef");
		searchObject.addField("collateralType");
		searchObject.addField("collateralCcy");
		searchObject.addField("expiryDate");
		searchObject.addField("nextReviewDate");
		searchObject.addField("nextRoleCode");
		searchObject.addField("nextReviewDate");
		searchObject.addField("nextRoleCode");
		searchObject.addField("finReference");
		searchObject.addField("status");
		searchObject.addTabelName(this.tableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filters = searchControl.getFilter();
			if (filters != null) {
				searchObject.addFilter(filters);
			}
		}

		List<CollateralSetup> searchList = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);

		this.listbox.setItemRenderer(new CollateralDownloadListModelItemRenderer());
		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().initList(searchList, this.listBoxCollateralDownload, this.paging);
		logger.debug(Literal.LEAVING);
		return searchList;
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
		this.depositorCif.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.depositorCif.setValue(customer.getCustCIF());
		} else {
			this.depositorCif.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	public CollateralDownloadService getCollateralDownloadService() {
		return collateralDownloadService;
	}

	public void setCollateralDownloadService(CollateralDownloadService collateralDownloadService) {
		this.collateralDownloadService = collateralDownloadService;
	}

	private class CollateralDownloadListModelItemRenderer implements ListitemRenderer<CollateralSetup>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, CollateralSetup collateralSetup, int count) throws Exception {
			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("CollateralSetup", collateralSetup);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {

			}
			lc.setParent(item);

			lc = new Listcell(collateralSetup.getDepositorCif());
			lc.setParent(item);
			lc = new Listcell(collateralSetup.getCollateralRef());
			lc.setParent(item);
			lc = new Listcell(collateralSetup.getCollateralCcy());
			lc.setParent(item);
			lc = new Listcell(collateralSetup.getCollateralType());
			lc.setParent(item);
			lc = new Listcell(DateUtility.format(collateralSetup.getExpiryDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(DateUtility.format(collateralSetup.getNextReviewDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(collateralSetup.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(collateralSetup.getRecordType()));
			lc.setParent(item);
			item.setAttribute("collateralSetup", collateralSetup);
			//ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralSetupItemDoubleClicked");
		}
	}

}