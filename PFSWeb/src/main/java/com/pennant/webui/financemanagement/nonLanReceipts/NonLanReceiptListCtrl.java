package com.pennant.webui.financemanagement.nonLanReceipts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class NonLanReceiptListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 778410382420505812L;

	protected Window window_NonLanReceiptList;
	protected Borderlayout borderLayout_ReceiptList;
	protected Paging pagingReceiptList;
	protected Listbox listBoxReceipts;

	protected Button btnClear;
	protected Button button_ReceiptList_NewReceipt;
	protected Button button_ReceiptList_ReceiptSearchDialog;
	protected Button button_ReceiptList_Submit;
	protected Button button_ReceiptList_Resubmit;
	protected Button button_ReceiptList_Approve;

	protected Longbox receiptId;
	protected Datebox receiptDate;
	protected Decimalbox receiptAmount;
	protected Combobox receiptMode;
	protected Combobox receiptSource;
	protected ExtendedCombobox partnerBank;
	protected ExtendedCombobox custCIF;
	protected Textbox transactionRef;
	protected Textbox reference;

	protected Listheader listheader_ReceiptId;
	protected Listheader listheader_ReceiptDate;
	protected Listheader listheader_ReceiptMode;
	protected Listheader listheader_ReceiptSource;
	protected Listheader listheader_Reference;
	protected Listheader listheader_ReceiptAmount;
	protected Listheader listheader_PartnerBank;
	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_TrnRef;
	protected Listheader listheader_PayTypeRef;
	protected Listheader listheader_DepositDate;
	protected Listheader listheader_RealizationDate;
	protected Listheader listheader_ReceiptModeStatus;
	protected Listheader listheader_NextRoleCode;
	protected Listheader listheader_CustCIF;

	protected Listbox sortOperator_receiptId;
	protected Listbox sortOperator_receiptDate;
	protected Listbox sortOperator_receiptMode;
	protected Listbox sortOperator_Reference;
	protected Listbox sortOperator_receiptSource;
	protected Listbox sortOperator_receiptAmount;
	protected Listbox sortOperator_partnerBank;
	protected Listbox sortOperator_transactionRef;
	protected Listbox sortOperator_payTypeRef;
	protected Listbox sortOperator_custCIF;

	protected Row row_1;
	protected Row row_2;
	protected Row row_3;
	protected Row row_4;
	protected Row row_5;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	private String roleCode;
	private String nextRoleCode;
	private String recordAction;

	private Map<Long, FinReceiptHeader> recHeaderMap = new HashMap<Long, FinReceiptHeader>();
	private Map<Long, FinReceiptHeader> headerMap = new HashMap<Long, FinReceiptHeader>(); // it has all data
	private String module;
	private String menuItemName = null;
	private ReceiptService receiptService;
	private NonLanReceiptService nonLanReceiptService;

	private String workflowCode = FinServiceEvent.RECEIPT;
	private transient FinanceWorkFlowService financeWorkFlowService;

	private transient WorkFlowDetails workFlowDetails = null;

	private List<ValueLabel> receiptModeList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_MODE);
	private List<ValueLabel> receiptSourceList = PennantAppUtil.getActiveFieldCodeList(RepayConstants.RECEIPT_SOURCE);

	/**
	 * default constructor.<br>
	 */
	public NonLanReceiptListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "NonLanReceipt";
		super.pageRightName = "FinReceiptHeader";
		this.module = getArgument("module");
		if ("Y".equals(getArgument("enqiryModule"))) {
			this.enqiryModule = true;
		}
		super.tableName = "NonLanFinReceiptHeader_View";
		super.queueTableName = "NonLanFinReceiptHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NonLanReceiptList(Event event) {
		logger.debug("Entering " + event.toString());

		menuItemName = getMenuItemName(event, menuItemName);

		// Set the page level components.
		setPageComponents(window_NonLanReceiptList, borderLayout_ReceiptList, listBoxReceipts, pagingReceiptList);
		setItemRender(new ReceiptListModelItemRenderer());

		// Register buttons and fields.
		if (StringUtils.equals(this.module, FinanceConstants.RECEIPT_MAKER)) {
			registerButton(button_ReceiptList_NewReceipt, "button_ReceiptList_NewReceipt", true);
			this.button_ReceiptList_Submit.setVisible(false);
		}

		registerButton(button_ReceiptList_ReceiptSearchDialog);

		registerField("receiptID", listheader_ReceiptId, SortOrder.ASC, receiptId, sortOperator_receiptId,
				Operators.DEFAULT);
		registerField("receiptDate", listheader_ReceiptDate, SortOrder.NONE, receiptDate, sortOperator_receiptDate,
				Operators.DATE);
		registerField("receiptAmount", listheader_ReceiptAmount, SortOrder.NONE, receiptAmount,
				sortOperator_receiptAmount, Operators.NUMERIC);
		registerField("receiptSource", listheader_ReceiptSource, SortOrder.NONE, receiptSource,
				sortOperator_receiptSource, Operators.STRING);
		registerField("reference", listheader_Reference, SortOrder.NONE, reference, sortOperator_Reference,
				Operators.STRING);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF, Operators.NUMERIC);
		registerField("transactionRef", listheader_PayTypeRef, SortOrder.NONE, transactionRef,
				sortOperator_transactionRef, Operators.STRING);
		registerField("depositDate", listheader_DepositDate);
		registerField("realizationDate", listheader_RealizationDate);
		registerField("partnerBankCode", listheader_PartnerBank, SortOrder.NONE, partnerBank, sortOperator_partnerBank,
				Operators.DEFAULT);
		registerField("receiptMode", listheader_ReceiptMode, SortOrder.NONE, receiptMode, sortOperator_receiptMode,
				Operators.DEFAULT);

		registerField("recordStatus", listheader_RecordStatus);
		registerField("receiptModeStatus", listheader_ReceiptModeStatus);
		registerField("nextRoleCode", listheader_NextRoleCode);
		registerField("recAgainst");
		registerField("extReference");
		registerField("workFlowId");
		registerField("subReceiptMode");
		registerField("entityCode");
		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();
		search();

		if (enqiryModule) {
			this.button_ReceiptList_Approve.setVisible(false);
			this.button_ReceiptList_NewReceipt.setVisible(false);
			this.button_ReceiptList_Resubmit.setVisible(false);
			this.button_ReceiptList_Submit.setVisible(false);
			setWorkFlowEnabled(false);
		}

		logger.debug("Leaving " + event.toString());
	}

	public void addRegisteredFilters() {
		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {

				if (filter.getProperty().equals("receiptAmount")) {
					filter.setValue(CurrencyUtil.unFormat((BigDecimal) filter.getValue(), 2));
				}

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
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");
		this.listHeader_CheckBox_Comp.setChecked(false);
		recHeaderMap.clear();
		headerMap.clear();
		this.searchObject.clearFilters();
		addRegisteredFilters();

		if (!enqiryModule) {
			if (StringUtils.equals(module, FinanceConstants.REALIZATION_APPROVER)
					|| StringUtils.equals(module, FinanceConstants.RECEIPT_APPROVER)) {
				List<String> filterList = new ArrayList<>();
				filterList.add(FinanceConstants.REALIZATION_APPROVER);
				filterList.add(FinanceConstants.RECEIPT_APPROVER);
				searchObject.addFilterIn("NEXTROLECODE", filterList);

			} else if (StringUtils.equals(module, FinanceConstants.REALIZATION_MAKER)) {
				searchObject.addWhereClause(
						" ((RECEIPTMODESTATUS IN ('R', 'D')  AND RECEIPTPURPOSE = 'N' and (NEXTROLECODE is null or NEXTROLECODE = '')) OR NEXTROLECODE ='"
								+ module + "')");

			} else {
				searchObject.addFilterEqual("NEXTROLECODE", module);
			}

			// Receipts in Multi Threading Process
			List<Long> receiptIdList = receiptService.getInProcessMultiReceiptRecord();
			if (receiptIdList != null && CollectionUtils.isNotEmpty(receiptIdList)) {
				Filter fil = new Filter("ReceiptID", receiptIdList, Filter.OP_NOT_IN);
				searchObject.addFilter(fil);
			}
		}
		logger.debug("Leaving");
	}

	public void doSetFieldProperties() {
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

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		// TODO CH : Static List method should be changed to Receipt Modes and
		// Sub receipt mode should be available for filter and list box
		fillComboBox(receiptMode, "", receiptModeList, "");
		fillComboBox(receiptSource, "", receiptSourceList, "");

		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });

		if (StringUtils.equals(this.module, FinanceConstants.REALIZATION_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.DEPOSIT_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.DEPOSIT_APPROVER)) {
			listheader_DepositDate.setVisible(true);
			this.button_ReceiptList_Submit.setVisible(true);
			listHeader_CheckBox_Name.setVisible(true);
		} else if (StringUtils.equals(this.module, FinanceConstants.RECEIPT_APPROVER)
				|| StringUtils.equals(this.module, FinanceConstants.REALIZATION_APPROVER)) {
			listheader_RealizationDate.setVisible(true);
			listheader_DepositDate.setVisible(true);
			this.button_ReceiptList_Approve.setVisible(true);
			this.button_ReceiptList_Resubmit.setVisible(true);
			listHeader_CheckBox_Name.setVisible(true);
		}

		listheader_ReceiptModeStatus.setVisible(true);
		listheader_NextRoleCode.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ReceiptList_ReceiptSearchDialog(Event event) {
		search();
	}

	// TODO CH : To be changed to a single zul and controller
	public void onClick$button_ReceiptList_NewReceipt(Event event) {
		logger.debug("Entering ");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("module", this.module);
		map.put("nonLanReceiptListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/NonLanReceipt/SelectNonLanReceiptPaymentDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doRefresh();
	}

	public void doRefresh() {
		recHeaderMap.clear();
		headerMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		doReset();
		search();
		// doSearch(true);
	}

	public void doSearch(boolean isFilterSearch) {

	}

	public void setSearchObj(JdbcSearchObject<FinReceiptHeader> searchObj) {
		this.searchObject = searchObj;
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
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxReceipts.getItems().size(); i++) {
			Listitem listitem = listBoxReceipts.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxReceipts.getItems().size() > 0) {
			recHeaderMap.putAll(headerMap);
		} else {
			recHeaderMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		FinReceiptHeader finReceiptHeader = (FinReceiptHeader) checkBox.getAttribute("finReceiptHeader");

		if (checkBox.isChecked()) {
			recHeaderMap.put(finReceiptHeader.getReceiptID(), finReceiptHeader);
		} else {
			recHeaderMap.remove(finReceiptHeader.getReceiptID());
		}

		if (recHeaderMap.size() == this.pagingReceiptList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public class ReceiptListModelItemRenderer implements ListitemRenderer<FinReceiptHeader>, Serializable {
		private static final long serialVersionUID = 8848425569301884635L;

		public ReceiptListModelItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, FinReceiptHeader finReceiptHeader, int count) {

			headerMap.put(finReceiptHeader.getReceiptID(), finReceiptHeader); // Setting all FinReceiptHeader into Map

			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("finReceiptHeader", finReceiptHeader);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(recHeaderMap.containsKey(finReceiptHeader.getReceiptID()));
			}
			lc.setParent(item);

			lc = new Listcell(String.valueOf(finReceiptHeader.getReceiptID()));
			lc.setParent(item);

			lc = new Listcell(
					PennantAppUtil.formateDate(finReceiptHeader.getReceiptDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getReceiptMode());
			lc.setParent(item);

			// TODO CH : Receipt Purpose in Filter and List are different. To be corrected
			lc = new Listcell(finReceiptHeader.getReceiptSource());
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getCustCIF());
			lc.setParent(item);

			if (RepayConstants.NONLAN_RECEIPT_CUSTOMER.equals(finReceiptHeader.getRecAgainst())) {
				lc = new Listcell(finReceiptHeader.getExtReference());
				lc.setParent(item);
			} else {
				lc = new Listcell(finReceiptHeader.getReference());
				lc.setParent(item);
			}

			lc = new Listcell(finReceiptHeader.getTransactionRef());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(finReceiptHeader.getReceiptAmount(),
					PennantConstants.defaultCCYDecPos));
			lc.setStyle("text-align:right");
			lc.setParent(item);

			lc = new Listcell(
					PennantAppUtil.formateDate(finReceiptHeader.getDepositDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getPartnerBankCode());
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.formateDate(finReceiptHeader.getRealizationDate(),
					DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getRecordStatus());
			lc.setParent(item);

			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_APPROVED)) {
				lc = new Listcell("Approved");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_FEES)) {
				lc = new Listcell("Fees");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
				lc = new Listcell("Realized");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_BOUNCE)) {
				lc = new Listcell("Bounce");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_CANCEL)) {
				lc = new Listcell("Cancel");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_DEPOSITED)) {
				lc = new Listcell("Deposited");
				lc.setParent(item);
			}
			if (StringUtils.equals(finReceiptHeader.getReceiptModeStatus(), RepayConstants.PAYSTATUS_INITIATED)) {
				lc = new Listcell("Initiated");
				lc.setParent(item);
			}
			if (StringUtils.isEmpty(finReceiptHeader.getReceiptModeStatus())) {
				lc = new Listcell("");
				lc.setParent(item);
			}

			lc = new Listcell(finReceiptHeader.getNextRoleCode());
			lc.setParent(item);

			item.setAttribute("finReceiptHeader", finReceiptHeader);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptDetailItemDoubleClicked");
		}

	}

	public void onReceiptDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceipts.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		FinReceiptHeader finRcptHeader = (FinReceiptHeader) selectedItem.getAttribute("finReceiptHeader");

		setWorkflowDetails(finRcptHeader.getWorkflowId(), false);
		if (workFlowDetails == null && !enqiryModule) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		FinReceiptHeader finReceiptHeader = nonLanReceiptService
				.getNonLanFinReceiptHeaderById(finRcptHeader.getReceiptID(), false, "_View");

		if (finReceiptHeader == null) {
			MessageUtil.showError("Record is not found with the receipt id " + finRcptHeader.getReceiptID());
			return;
		}

		if (enqiryModule) {
			FinReceiptData finReceiptData = new FinReceiptData();
			finReceiptData.setReceiptHeader(finReceiptHeader);
			doShowReceiptView(finReceiptHeader, finReceiptData);
			return;
		}

		finReceiptHeader.setValueDate(finReceiptHeader.getReceiptDate());
		// Role Code State Checking
		String userRole = finReceiptHeader.getNextRoleCode();

		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = finReceiptHeader.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = finReceiptHeader.getReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetail.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug("Leaving");
			return;
		}

		String whereCond = " External Reference='" + finReceiptHeader.getReference() + "'";
		FinReceiptData finReceiptData = null;
		if (isWorkFlowEnabled()) {

			finReceiptData = new FinReceiptData();
			finReceiptData.setReceiptHeader(finReceiptHeader);

			if (finReceiptData.getReceiptHeader().getWorkflowId() == 0 && isWorkFlowEnabled()) {
				finReceiptData.getReceiptHeader().setWorkflowId(workFlowDetails.getWorkFlowId());
			}

			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			if (doCheckAuthority(finReceiptHeader, whereCond)
					|| StringUtils.equals(finReceiptHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
				doShowReceiptView(finReceiptHeader, finReceiptData);
			} else {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			}
		} else {
			if (doCheckAuthority(finReceiptHeader, whereCond)
					|| StringUtils.equals(finReceiptHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
				doShowReceiptView(finReceiptHeader, finReceiptData);
			} else {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug("Leaving");
	}

	private void setWorkflowDetails(long workFlowId, boolean isPromotion) {

		// Finance Maintenance Workflow Check & Assignment
		if (workFlowId != 0) {
			workFlowDetails = WorkFlowUtil.getWorkflow(workFlowId);
		} else if (StringUtils.equals(module, "REALIZATION_MAKER")) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReceiptRealization");

		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void doShowReceiptView(FinReceiptHeader finReceiptHeader, FinReceiptData finReceiptData) {

		if (finReceiptHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			finReceiptHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		logUserAccess(menuItemName, finReceiptHeader.getReference());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("finReceiptHeader", finReceiptHeader);
		map.put("receiptData", finReceiptData);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("nonLanReceiptListCtrl", this);
		map.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/NonLanReceipt/NonLanReceiptDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$button_ReceiptList_Submit(Event event) {
		logger.debug("Entering");
		recordAction = PennantConstants.RCD_STATUS_SUBMITTED;
		doShowMultiReceipt();
		logger.debug("Leaving");
	}

	public void onClick$button_ReceiptList_Resubmit(Event event) {
		logger.debug("Entering");
		recordAction = PennantConstants.RCD_STATUS_RESUBMITTED;
		doShowMultiReceipt();
		logger.debug("Leaving");
	}

	public void onClick$button_ReceiptList_Approve(Event event) {
		logger.debug("Entering");
		recordAction = PennantConstants.RCD_STATUS_APPROVED;
		doShowMultiReceipt();
		logger.debug("Leaving");
	}

	public void doShowMultiReceipt() {
		logger.debug("Entering");

		if (recHeaderMap.isEmpty()) {
			MessageUtil.showError("Please Select at least 1 record to proceed");
			return;
		}

		if (StringUtils.equals(this.module, FinanceConstants.DEPOSIT_MAKER)) {
			FinReceiptHeader firstRecord = recHeaderMap.values().stream().findFirst().orElse(null);
			if (firstRecord != null) {
				// Cheque Or DD Validation For Partner Bank
				FinReceiptHeader recordFound = recHeaderMap.values().stream()
						.filter(header -> !StringUtils.equals(header.getReceiptMode(), firstRecord.getReceiptMode()))
						.findFirst().orElse(null);
				if (recordFound != null) {
					MessageUtil.showError("Selected Receipts' Receipt Mode should be same.");
					return;
				}

				// Entity Code Validation For Partner Bank
				recordFound = recHeaderMap.values().stream()
						.filter(header -> !StringUtils.equals(header.getEntityCode(), firstRecord.getEntityCode()))
						.findFirst().orElse(null);
				if (recordFound != null) {
					MessageUtil.showError("Selected Receipts' Entity Code should be same.");
					return;
				}
			}
		}

		Set<Long> recId = recHeaderMap.keySet();
		for (long receiptId : recId) {
			FinReceiptHeader finReceiptHeader = nonLanReceiptService.getNonLanFinReceiptHeaderById(receiptId, false,
					"_View");

			if (finReceiptHeader == null) {
				continue;
			}

			finReceiptHeader.setValueDate(finReceiptHeader.getReceiptDate());
			setWorkflowDetails(finReceiptHeader.getWorkflowId(), false);

			String whereCond = " Reference='" + finReceiptHeader.getReference() + "'";
			if (!(doCheckAuthority(finReceiptHeader, whereCond)
					|| StringUtils.equals(finReceiptHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED))) {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (finReceiptHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
				finReceiptHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
				roleCode = workFlowDetails.getFirstTaskOwner();
			} else {
				roleCode = finReceiptHeader.getNextRoleCode();
			}
			recHeaderMap.put(receiptId, finReceiptHeader);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recHeaderMap", recHeaderMap);
		// map.put("finReceiptHeader", finReceiptHeader);
		map.put("nonLanReceiptListCtrl", this);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("roleCode", roleCode);
		// map.put("nextRoleCode", nextRoleCode);
		map.put("recordAction", recordAction);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/NonLanReceipt/SelectNonLanReceiptDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public NonLanReceiptService getNonLanReceiptService() {
		return nonLanReceiptService;
	}

	public void setNonLanReceiptService(NonLanReceiptService nonLanReceiptService) {
		this.nonLanReceiptService = nonLanReceiptService;
	}

}
