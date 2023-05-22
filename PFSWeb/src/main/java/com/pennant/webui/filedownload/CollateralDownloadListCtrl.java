/**
 * ' * Copyright 2011 - Pennant Technologies
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
 * * FileName : CollateralDownloadListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-08-2019 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-08-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.filedownload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.filedownload.CollateralDownloadService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/filedownload/CollateralDownloadListCtrl.zul<br>
 * ************************************************************<br>
 * 
 */
public class CollateralDownloadListCtrl extends GFCBaseListCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;

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
	protected Datebox toDate;
	protected Datebox fromDate;

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

	private ExtendedFieldDetailsService extendedFieldDetailsService;

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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CollateralDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CollateralDownloadList, borderLayout_CollateralDownloadList, listBoxCollateralDownload,
				pagingCollateralDownloadList);
		setItemRender(new CollateralDownloadListModelItemRenderer());

		registerButton(button_Search);
		/*
		 * registerField("depositorCif", listheader_DepositorCif, SortOrder.ASC, depositorCif,
		 * sortOperator_DepositorCif, Operators.STRING); registerField("collateralRef", listheader_CollateralRef,
		 * SortOrder.ASC, collateralRef, sortOperator_CollateralRef, Operators.STRING); registerField("collateralType",
		 * listheader_CollateralType, SortOrder.ASC, collateralType, sortOperator_CollateralType, Operators.STRING);
		 * registerField("collateralCcy", listheader_CollateralCcy, SortOrder.ASC, collateralCcy,
		 * sortOperator_CollateralCcy, Operators.STRING); registerField("expiryDate", listheader_ExpiryDate,
		 * SortOrder.ASC, expiryDate, sortOperator_ExpiryDate, Operators.DATE); registerField("nextReviewDate",
		 * listheader_NextReviewDate, SortOrder.ASC, nextReviewDate, sortOperator_NextReviewDate, Operators.DATE);
		 * registerField("nextRoleCode"); registerField("finReference"); registerField("status");
		 */

		// Render the page and display no data when the page loaded for the
		// first time.
		doRenderPage();
		this.collateralDownloadMap.clear();
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

	public void onClick$btnDownload(Event event) {
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
			// Iterating and collection list of custCif to limitRef
			collateralList.stream().forEach(s -> collateralRef.add(s.getCollateralRef()));
			try {
				String filePath = collateralDownloadService.processDownload(collateralRef);
				downloadFromServer(filePath);
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
		doRemoveValidation();
		this.toDate.setValue(null);
		this.fromDate.setValue(null);
		renderCollaterals();
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
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
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

	public void onClick_listHeaderCheckBox(ForwardEvent event) {
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

	public void onClick_listCellCheckBox(ForwardEvent event) {
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
		searchObject.addField("lastMntOn");
		searchObject.addTabelName(this.tableName);

		doSetValidations();

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

		List<CollateralSetup> searchList = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		Map<String, String> retailMap = null;
		Map<String, String> corpMap = null;
		Map<String, String> smeMap = null;
		retailMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_INDIV, "", "", false);
		corpMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_CORP, "", "", false);
		smeMap = getExtendedFieldDetailsService().getAllExtndedFieldDetails("CUSTOMER",
				PennantConstants.PFF_CUSTCTG_SME, "", "", false);
		List<CollateralSetup> renderList = new ArrayList<CollateralSetup>();
		if (CollectionUtils.isNotEmpty(searchList)) {
			for (CollateralSetup customer : searchList) {
				if (retailMap != null && retailMap.containsKey(customer.getDepositorCif())) {
					renderList.add(customer);
					continue;
				}
				if (corpMap != null && corpMap.containsKey(customer.getDepositorCif())) {
					renderList.add(customer);
					continue;
				}
				if (smeMap != null && smeMap.containsKey(customer.getDepositorCif())) {
					renderList.add(customer);
					continue;
				}
			}
		}

		this.listbox.setItemRenderer(new CollateralDownloadListModelItemRenderer());
		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().initList(renderList, this.listBoxCollateralDownload, this.paging);
		logger.debug(Literal.LEAVING);
		return renderList;
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
		public void render(Listitem item, CollateralSetup collateralSetup, int count) {
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
			lc = new Listcell(DateUtil.format(collateralSetup.getExpiryDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(collateralSetup.getNextReviewDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(collateralSetup.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(
					PennantAppUtil.formateDate(collateralSetup.getLastMntOn(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);
			item.setAttribute("collateralSetup", collateralSetup);
			// ComponentsCtrl.applyForward(item,
			// "onDoubleClick=onCollateralSetupItemDoubleClicked");
		}
	}

	public String downloadFromServer(String filePath) throws FileNotFoundException, IOException {
		String Path = App.getProperty("external.interface.glems.Colletral.path");
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

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}
}