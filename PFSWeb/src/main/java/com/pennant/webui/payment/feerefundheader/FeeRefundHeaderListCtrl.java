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
 * * FileName : PaymentHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.payment.feerefundheader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.feerefund.impl.FeeRefundApprovalProcess;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

/**
 * This is the controller class for the /WEB-INF/pages/FeeRefund/FeeRefundHeaderList.zul file.
 * 
 */
public class FeeRefundHeaderListCtrl extends GFCBaseListCtrl<FeeRefundHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_FeeRefundHeaderList;
	protected Borderlayout borderLayout_FeeRefundHeaderList;
	protected Paging pagingFeeRefundHeaderList;
	protected Listbox listBoxFeeRefundHeader;

	// List headers
	protected Listheader listheader_FeeRefundHeaderID;
	protected Listheader listheader_FeeRefundCustCif;
	protected Listheader listheader_FeeRefundCustName;
	protected Listheader listheader_FeeRefundFinRef;
	protected Listheader listheader_FeeRefundPayAmount;
	protected Listheader listheader_FeeRefundFinType;
	protected Listheader listheader_FeeRefundBranchName;

	// checkRights
	protected Button button_FeeRefundHeaderList_NewFeeRefundHeader;
	protected Button button_FeeRefundHeaderList_FeeRefundHeaderSearch;
	protected Button button_FeeRefundHeaderList_PrintList;

	// Search Fields
	protected Uppercasebox custCif;
	protected Textbox custName;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox entityCode;
	protected ExtendedCombobox branchCode;
	protected ExtendedCombobox clusterName;

	protected Listbox sortOperator_FeeRefundCustCif;
	protected Listbox sortOperator_FeeRefundCustName;
	protected Listbox sortOperator_FeeRefundFinRef;
	protected Listbox sortOperator_FeeRefundFinType;
	protected Listbox sortOperator_FeeRefundEntity;
	protected Listbox sortOperator_FeeRefundBranchCode;
	protected Listbox sortOperator_FeeRefundClusterName;

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private Map<Long, String> feeRefundIdMap = new HashMap<>();

	// module
	boolean isApprovalMenu = false;
	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;

	protected Button btnReject;
	protected Button btnApprove;
	protected Button btndownload;
	protected Checkbox list_CheckBox;

	private transient FeeRefundHeaderService feeRefundHeaderService;
	private transient FeeRefundApprovalProcess feeRefundApprovalProcess;

	/**
	 * default constructor.<br>
	 */
	public FeeRefundHeaderListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (isApprovalMenu) {
			this.searchObject.addWhereClause(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		} else {
			this.searchObject.addWhereClause(
					" RECORDSTATUS not in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "') and APPROVALSTATUS = 0");
		}
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FeeRefundHeader";
		super.pageRightName = "FeeRefundHeaderList";
		super.tableName = "Fee_Refund_Header_AView";
		super.queueTableName = "Fee_Refund_Header_View";
		super.enquiryTableName = "Fee_Refund_Header_View";

		if (StringUtils.equals(getArgument("approval"), "Y")) {
			this.isApprovalMenu = true;
			this.enqiryModule = true;
		} else {
			this.isApprovalMenu = false;
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FeeRefundHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FeeRefundHeaderList, borderLayout_FeeRefundHeaderList, listBoxFeeRefundHeader,
				pagingFeeRefundHeaderList);
		setItemRender(new FeeRefundHeaderListModelItemRenderer());

		// Register buttons and fields.
		doSetFieldProperties();
		registerButton(button_FeeRefundHeaderList_FeeRefundHeaderSearch);
		registerButton(button_FeeRefundHeaderList_NewFeeRefundHeader, "button_FeeRefundHeaderList_NewFeeRefundHeader",
				true);
		registerButton(print, "button_FeeRefundHeaderList_PrintList");

		if (enqiryModule) {
			registerButton(button_FeeRefundHeaderList_NewFeeRefundHeader,
					"button_FeeRefundHeaderList_NewFeeRefundHeader", false);
		}

		registerField("ID", listheader_FeeRefundHeaderID);
		registerField("FinID");
		registerField("CustCif", listheader_FeeRefundCustCif, SortOrder.NONE, custCif, sortOperator_FeeRefundCustCif,
				Operators.NUMERIC);
		registerField("CustShrtName", listheader_FeeRefundCustName, SortOrder.NONE, custName,
				sortOperator_FeeRefundCustName, Operators.STRING);
		registerField("FinReference", listheader_FeeRefundFinRef, SortOrder.NONE, finReference,
				sortOperator_FeeRefundFinRef, Operators.STRING);
		registerField("FinType", listheader_FeeRefundFinType, SortOrder.NONE, finType, sortOperator_FeeRefundFinType,
				Operators.STRING);
		registerField("PaymentAmount");
		registerField("ApprovalStatus");
		registerField("BranchDesc");
		registerField("BranchCode", branchCode, SortOrder.NONE, sortOperator_FeeRefundBranchCode, Operators.STRING);
		registerField("EntityCode", entityCode, SortOrder.NONE, sortOperator_FeeRefundEntity, Operators.STRING);
		registerField("ClusterCode", clusterName, SortOrder.NONE, sortOperator_FeeRefundClusterName, Operators.NUMERIC);

		// Render the page and display the data.
		doRenderPage();

		if (isApprovalMenu) {
			this.feeRefundIdMap.clear();
			doSetFields();
			if (listBoxFeeRefundHeader.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
			}
		} else {
			// rendering the list page data required or not.
			search();
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFields() {

		// button visible
		this.btnApprove.setVisible(true);
		this.btndownload.setVisible(true);
		this.btnReject.setVisible(true);
		this.print.setVisible(true);
		this.listHeader_CheckBox_Name.setVisible(true);
		this.button_FeeRefundHeaderList_NewFeeRefundHeader.setVisible(false);

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

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.finType.setMaxlength(20);
		this.finType.setTextBoxWidth(120);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.branchCode.setMaxlength(20);
		this.branchCode.setTextBoxWidth(120);
		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });

		this.clusterName.setMaxlength(50);
		this.clusterName.setTextBoxWidth(120);
		this.clusterName.setModuleName("Cluster");
		this.clusterName.setValueColumn("Code");
		this.clusterName.setDescColumn("Name");
		this.clusterName.setValidateColumns(new String[] { "Code", "Name" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FeeRefundHeaderList_FeeRefundHeaderSearch(Event event) {
		if (isApprovalMenu) {
			this.feeRefundIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();

			if (listBoxFeeRefundHeader.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
				listBoxFeeRefundHeader.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			}
		} else {
			search();
		}
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		if (isApprovalMenu) {
			doReset();
			this.feeRefundIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			this.listbox.getItems().clear();

			if (listBoxFeeRefundHeader.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
				listBoxFeeRefundHeader.setEmptyMessage("");

			}
			this.pagingFeeRefundHeaderList.setTotalSize(0);

		} else {

			doReset();
			search();
		}
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCif.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCif.setValue(customer.getCustCIF());
		} else {
			this.custCif.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FeeRefundHeaderList_NewFeeRefundHeader(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FeeRefundHeader frh = new FeeRefundHeader();
		frh.setNewRecord(true);
		frh.setRefundType("M");
		frh.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("feeRefundHeader", frh);
		arg.put("feeRefundHeaderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FeeRefund/SelectFeeRefundHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onFeeRefundHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFeeRefundHeader.getSelectedItem();
		final long feeRefundId = (long) selectedItem.getAttribute("feeRefundId");
		FeeRefundHeader frh = feeRefundHeaderService.getFeeRefundHeader(feeRefundId);

		if (frh == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  ID =? ");

		if (doCheckAuthority(frh, whereCond.toString(), new Object[] { frh.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && frh.getWorkflowId() == 0) {
				frh.setWorkflowId(getWorkFlowId());
			}

			logUserAccess("menu_Item_FeeRefundMaker", frh.getFinReference());

			doShowDialogPage(frh);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param paymentheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FeeRefundHeader feeRefundHeader) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = feeRefundHeaderService.getFinanceDetails(feeRefundHeader.getFinID());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("feeRefundHeader", feeRefundHeader);
		arg.put("feeRefundHeaderListCtrl", this);
		arg.put("financeMain", financeMain);
		arg.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/FeeRefund/FeeRefundHeaderDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		for (int i = 0; i < listBoxFeeRefundHeader.getItems().size(); i++) {
			Listitem listitem = listBoxFeeRefundHeader.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked()) {
			List<Long> feeRefundIdList = getFeeRefundHeaderList();
			if (feeRefundIdList != null) {
				for (Long feeRefundId : feeRefundIdList) {
					feeRefundIdMap.put(feeRefundId, null);
				}
			}
		} else {
			feeRefundIdMap.clear();
		}

		logger.debug("Leaving");
	}

	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		if (checkBox.isChecked()) {
			feeRefundIdMap.put(Long.valueOf(checkBox.getValue().toString()), checkBox.getValue().toString());
		} else {
			feeRefundIdMap.remove(Long.valueOf(checkBox.getValue().toString()));
		}

		if (feeRefundIdMap.size() == this.pagingFeeRefundHeaderList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug("Leaving");
	}

	private List<Long> getFeeRefundHeaderList() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		searchObject.addField("ID");
		searchObject.addTabelName(this.queueTableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		searchObject.addWhereClause(whereClause.toString());

		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		List<Long> feeRefundIdList = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				feeRefundIdList.add(Long.parseLong(String.valueOf(map.get("Id"))));
			}
		}
		return feeRefundIdList;
	}

	public class FeeRefundHeaderListModelItemRenderer implements ListitemRenderer<FeeRefundHeader>, Serializable {

		private static final long serialVersionUID = 1L;

		public FeeRefundHeaderListModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, FeeRefundHeader frh, int count) {

			Listcell lc;

			if (isApprovalMenu) {
				lc = new Listcell();
				list_CheckBox = new Checkbox();
				list_CheckBox.setValue(frh.getId());
				list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
				lc.appendChild(list_CheckBox);
				if (listHeader_CheckBox_Comp.isChecked()) {
					list_CheckBox.setChecked(true);
				} else {
					list_CheckBox.setChecked(feeRefundIdMap.containsKey(frh.getId()));
				}
				lc.setParent(item);
			} else {
				lc = new Listcell(String.valueOf(frh.getId()));
				lc.setParent(item);
			}

			lc = new Listcell(String.valueOf(frh.getId()));
			lc.setParent(item);

			lc = new Listcell(frh.getCustCif());
			lc.setParent(item);

			lc = new Listcell(frh.getCustShrtName());
			lc.setParent(item);

			lc = new Listcell(frh.getFinReference());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(frh.getPaymentAmount(),
					CurrencyUtil.getFormat(frh.getFinCcy())));
			lc.setParent(item);

			lc = new Listcell(frh.getFinType());
			lc.setParent(item);

			lc = new Listcell(frh.getBranchDesc());
			lc.setParent(item);

			lc = new Listcell(frh.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(PennantJavaUtil.getLabel(frh.getRecordType()));
			lc.setParent(item);
			item.setAttribute("feeRefundId", frh.getId());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onFeeRefundHeaderItemDoubleClicked");
		}
	}

	public void onClick$btnApprove(Event event) {
		logger.debug(Literal.ENTERING);

		List<Long> frhList = getListofFeeRefundHeader();

		if (frhList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("FeeRefundHeaderDataList_NoEmpty"));
			return;
		}

		Collections.sort(frhList);

		List<FeeRefundHeader> listRefundHeader = doCheckAuthority(frhList, true);

		if (listRefundHeader.isEmpty()) {
			return;
		}

		/*
		 * String notDownloadIds = checkFileDownloaded(frhList);
		 * 
		 * if (StringUtils.isNotEmpty(notDownloadIds)) { MessageUtil.showError(Labels.getLabel("DOWNLOAD_MANDATORY", new
		 * Object[] { notDownloadIds })); return; }
		 */

		boolean processCompleted = true;
		List<String> listFrh = new ArrayList<>();
		for (FeeRefundHeader frh : listRefundHeader) {

			listFrh.add(String.valueOf(frh.getId()));

			// call dosaveProgress
			if (processCompleted) {
				processCompleted = doProcess(frh, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_APPROVED);
			}
		}

		if (processCompleted) {
			doApprove(listFrh);
		}

		doRefresh();

		if (processCompleted) {
			Clients.showNotification("Fee Refund Process Approved.", "info", null, null, -1);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doApprove(List<String> listFrh) {

		try {

			String feeRefundIds = String.join(",", listFrh);
			Thread thread = new Thread(new FeeRefundApprovalThread(feeRefundIds));
			thread.start();
			Thread.sleep(1000);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public class FeeRefundApprovalThread implements Runnable {
		String feeRefundId;

		public FeeRefundApprovalThread(String feeRefundId) {
			super();
			this.feeRefundId = feeRefundId;
		}

		@Override
		public void run() {

			String[] list = StringUtils.trimToEmpty(feeRefundId).split(",");

			List<Long> feeRefundIdList = new ArrayList<>();
			for (String item : list) {
				feeRefundIdList.add(Long.parseLong(item));
			}

			feeRefundApprovalProcess.approveFeeRefunds(feeRefundIdList, getUserWorkspace().getLoggedInUser());

		}
	}

	private List<Long> getListofFeeRefundHeader() {

		List<Long> feeRefundIdList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			feeRefundIdList = getListofFeeRefundHeaderList();
		} else {
			feeRefundIdList = new ArrayList<Long>(feeRefundIdMap.keySet());
		}
		return feeRefundIdList;
	}

	private List<Long> getListofFeeRefundHeaderList() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		searchObject.addField("Id");
		searchObject.addTabelName(this.queueTableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				searchObject.addFilter(filter);
			}
		}

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" RECORDSTATUS in ('" + PennantConstants.RCD_STATUS_SUBMITTED + "')");
		searchObject.addWhereClause(whereClause.toString());

		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		List<Long> feeRefundHeaderList = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				feeRefundHeaderList.add(Long.parseLong(String.valueOf(map.get("Id"))));
			}
		}
		return feeRefundHeaderList;
	}

	protected boolean doProcess(FeeRefundHeader frh, String tranType, String recStatus) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		frh.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		frh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		frh.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			frh.setRecordStatus(recStatus);

			if (PennantConstants.RCD_STATUS_APPROVED.equals(frh.getRecordStatus())) {
				frh.setStatus(RepayConstants.PAYMENT_APPROVE);
				frh.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			}

			if ("Save".equals(recStatus)) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(frh.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, frh);
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}
			frh.setTaskId(taskId);
			frh.setNextTaskId(nextTaskId);
			frh.setRoleCode(getRole());
			frh.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(frh, tranType);
			String operationRefs = getServiceOperations(taskId, frh);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(frh, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(frh, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = feeRefundHeaderService.delete(auditHeader);
				} else {
					auditHeader = feeRefundHeaderService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = feeRefundHeaderService.doApprove(auditHeader);
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = feeRefundHeaderService.doReject(auditHeader);
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FeeRefundHeaderList, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_FeeRefundHeaderList, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	private void doRefresh() {
		if (isApprovalMenu) {
			doReset();
			this.feeRefundIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			this.listbox.getItems().clear();

			if (listBoxFeeRefundHeader.getItems().size() > 0) {
				listHeader_CheckBox_Comp.setDisabled(false);
			} else {
				listHeader_CheckBox_Comp.setDisabled(true);
				listBoxFeeRefundHeader.setEmptyMessage("");

			}
			this.pagingFeeRefundHeaderList.setTotalSize(0);

		} else {
			doReset();
		}

		search();
	}

	private AuditHeader getAuditHeader(FeeRefundHeader frh, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, frh.getBefImage(), frh);
		return new AuditHeader(getReference(frh), null, null, null, auditDetail, frh.getUserDetails(), getOverideMap());
	}

	protected String getReference(FeeRefundHeader frh) {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(frh.getId()));
		return referenceBuffer.toString();
	}

	private List<FeeRefundHeader> doCheckAuthority(List<Long> feeRefundIdList, boolean getSucessRecords) {

		List<FeeRefundHeader> frhList = new ArrayList<>();

		for (long id : feeRefundIdList) {
			FeeRefundHeader frh = feeRefundHeaderService.getFeeRefundHeader(id);

			// Check whether the user has authority to change/view the record.
			String whereCond = " ID= ?";

			if (doCheckAuthority(frh, whereCond, new Object[] { frh.getId() })) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && frh.getWorkflowId() == 0) {
					frh.setWorkflowId(getWorkFlowId());
				}
				doLoadWorkFlow(isWorkFlowEnabled(), frh.getWorkflowId(), frh.getNextTaskId());
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				return new ArrayList<FeeRefundHeader>();

			}
			frhList.add(frh);
		}

		return frhList;
	}

	public void onClick$btnReject(Event event) {

		logger.debug(Literal.ENTERING);
		List<Long> feeRefundIdList = getListofFeeRefundHeader();

		if (feeRefundIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("FeeRefundHeaderDataList_NoEmpty"));
			return;
		}

		List<FeeRefundHeader> frhList = doCheckAuthority(feeRefundIdList, false);

		if (frhList.isEmpty()) {
			return;
		}

		for (FeeRefundHeader frh : frhList) {
			doProcess(frh, PennantConstants.TRAN_ADD, PennantConstants.RCD_STATUS_REJECTED);
		}

		doRefresh();
		Clients.showNotification("Rejected successfully.", "info", null, null, -1);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btndownload(Event event) throws IOException {
		logger.debug(Literal.ENTERING);

		List<Long> feeRefundIdList = getListofFeeRefundHeader();

		if (feeRefundIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("FeeRefundHeaderDataList_NoEmpty"));
			return;
		}

		List<FeeRefundHeader> feeRefundHeaderList = doCheckAuthority(feeRefundIdList, false);

		if (feeRefundHeaderList.isEmpty()) {
			return;
		}

		if (feeRefundIdList.size() > 1) {

			try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
				try (ZipOutputStream out = new ZipOutputStream(arrayOutputStream)) {
					for (Long headerID : feeRefundIdList) {
						ByteArrayOutputStream outputStream = doDownloadFiles(headerID);
						out.putNextEntry(new ZipEntry("Fee_Refund_" + headerID + ".xls"));
						out.write(outputStream.toByteArray());
						out.closeEntry();

						this.feeRefundHeaderService.updateApprovalStatus(headerID,
								PennantConstants.FEE_REFUND_APPROVAL_DOWNLOADED);
					}
				}

				String zipfileName = "Fee_Refund.zip";
				byte[] tobytes = arrayOutputStream.toByteArray();

				Filedownload.save(new AMedia(zipfileName, "zip", "application/*", tobytes));
			}

		} else {
			for (long headerID : feeRefundIdList) {
				ByteArrayOutputStream os = doDownloadFiles(headerID);
				Filedownload.save(
						new AMedia("Fee_Refund_" + headerID, "xls", "application/vnd.ms-excel", os.toByteArray()));
				this.feeRefundHeaderService.updateApprovalStatus(headerID,
						PennantConstants.FEE_REFUND_APPROVAL_DOWNLOADED);
			}
		}

		doRefresh();
		Clients.showNotification(Labels.getLabel("label_DataExtractionList_DownloadedSuccess.value"), "info", null,
				null, -1);

		logger.debug(Literal.LEAVING);
	}

	private ByteArrayOutputStream doDownloadFiles(long headerID) {
		logger.debug(Literal.ENTERING);

		String whereCond = " Where frh.ID = " + headerID;
		StringBuilder searchCriteria = new StringBuilder(" ");

		String reportName = "";

		reportName = "FeeRefundApprovalReport";
		ByteArrayOutputStream outputStream = null;
		outputStream = generateReport(getUserWorkspace().getLoggedInUser().getFullName(), reportName, whereCond,
				searchCriteria, this.window_FeeRefundHeaderList, true, String.valueOf(headerID));

		logger.debug(Literal.LEAVING);
		return outputStream;
	}

	private String checkFileDownloaded(List<Long> feeRefundIdList) {
		StringBuilder builder = new StringBuilder();

		for (long id : feeRefundIdList) {

			boolean isDownloaded = this.feeRefundHeaderService.isFileDownloaded(id,
					PennantConstants.FEE_REFUND_APPROVAL_DOWNLOADED);

			if (!isDownloaded) {

				if (builder.length() > 0) {
					builder.append(", ");
				}

				builder.append(id);
			}
		}
		return builder.toString();
	}

	public ByteArrayOutputStream generateReport(String userName, String reportName, String whereCond,
			StringBuilder searchCriteriaDesc, Window window, boolean createExcel, String id) {
		logger.debug("Entering");

		Connection connection = null;
		DataSource dataSourceObj = null;
		ByteArrayOutputStream outputStream = null;
		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
			reportArgumentsMap.put("userName", userName);
			reportArgumentsMap.put("reportHeading", reportName);
			reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
			reportArgumentsMap.put("appDate", SysParamUtil.getAppDate());
			reportArgumentsMap.put("appCcy", SysParamUtil.getValueAsString("APP_DFT_CURR"));
			reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
			reportArgumentsMap.put("unitParam", "Pff");
			reportArgumentsMap.put("whereCondition", whereCond);
			reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
			reportArgumentsMap.put("bankName", Labels.getLabel("label_ClientName"));
			reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());
			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/" + reportName + ".jasper";

			Connection con = null;
			DataSource reportDataSourceObj = null;

			try {
				File file = new File(reportSrc);
				if (file.exists()) {

					logger.debug("Buffer started");

					reportDataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
					con = reportDataSourceObj.getConnection();

					String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);

					JRXlsExporter excelExporter = new JRXlsExporter();
					excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
							Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
					excelExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, id);

					outputStream = new ByteArrayOutputStream();
					excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);

					excelExporter.exportReport();
				}
			} catch (JRException e) {
				logger.error(e.getMessage());
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			connection = null;
			dataSourceObj = null;
		}

		logger.debug("Leaving");
		return outputStream;
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

	public void setFeeRefundApprovalProcess(FeeRefundApprovalProcess feeRefundApprovalProcess) {
		this.feeRefundApprovalProcess = feeRefundApprovalProcess;
	}

}