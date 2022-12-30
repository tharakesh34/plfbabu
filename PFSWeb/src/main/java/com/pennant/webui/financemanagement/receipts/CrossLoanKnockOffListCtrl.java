package com.pennant.webui.financemanagement.receipts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
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
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CrossLoanKnockOffListCtrl extends GFCBaseListCtrl<CrossLoanKnockOffHeader> {

	private static final long serialVersionUID = 778410382420505812L;
	private static final Logger logger = LogManager.getLogger(CrossLoanKnockOffListCtrl.class);

	protected Window window_CrossLoanKnockOffList;
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
	protected Combobox receiptPurpose;
	protected ExtendedCombobox partnerBank;
	protected ExtendedCombobox finType;
	protected Textbox transactionRef;
	protected ExtendedCombobox customer;
	protected ExtendedCombobox fromFinReference;
	protected ExtendedCombobox toFinReference;
	private boolean isKnockOff;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FromFinReference;
	protected Listheader listheader_ToFinReference;
	protected Listheader listheader_ReceiptId;
	protected Listheader listheader_ReceiptDate;
	protected Listheader listheader_KnockOffType;
	protected Listheader listheader_ReceiptMode;
	protected Listheader listheader_ReceiptAmount;
	protected Listheader listheader_ReceiptPurpose;
	protected Listheader listheader_PartnerBank;
	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_ExcessType;
	protected Listheader listheader_TrnRef;
	protected Listheader listheader_PayTypeRef;
	protected Listheader listheader_DepositDate;
	protected Listheader listheader_RealizationDate;
	protected Listheader listheader_ReceiptModeStatus;
	protected Listheader listheader_NextRoleCode;

	protected Listbox sortOperator_customer;
	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_loanReference;
	protected Listbox sortOperator_receiptId;
	protected Listbox sortOperator_receiptDate;
	protected Listbox sortOperator_receiptMode;
	protected Listbox sortOperator_receiptPurpose;
	protected Listbox sortOperator_receiptAmount;
	protected Listbox sortOperator_partnerBank;
	protected Listbox sortOperator_transactionRef;
	protected Listbox sortOperator_payTypeRef;
	protected Listbox sortOperator_fromloanReference;
	protected Listbox sortOperator_toloanReference;

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
	private String recordAction;

	private Map<Long, FinReceiptHeader> recHeaderMap = new HashMap<Long, FinReceiptHeader>();
	private Map<Long, FinReceiptHeader> headerMap = new HashMap<Long, FinReceiptHeader>(); // it has all data
	private String module;
	private ReceiptService receiptService;

	private String workflowCode = FinServiceEvent.RECEIPT;
	private transient FinanceWorkFlowService financeWorkFlowService;

	private transient WorkFlowDetails workFlowDetails = null;
	private CrossLoanKnockOffService crossLoanKnockOffService;

	/**
	 * default constructor.<br>
	 */
	public CrossLoanKnockOffListCtrl() {
		super();
	}

	protected void doSetProperties() {

		super.moduleCode = "CrossLoanKnockOffHeader";
		super.pageRightName = "CrossLoanKnockOffHeader";
		super.tableName = "CROSSLOANKNOCKOFFHEADER_TVIEW";
		super.queueTableName = "CROSSLOANKNOCKOFFHEADER_TVIEW";

		this.module = getArgument("module");
		if (StringUtils.equals(module, FinanceConstants.CROSSLOANKNOCKOFF_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)) {

			isKnockOff = true;
			super.moduleCode = "CrossLoanKnockOffHeader";
			super.tableName = "CROSSLOANKNOCKOFFHEADER_TVIEW";
			super.queueTableName = "CROSSLOANKNOCKOFFHEADER_TVIEW";
		}

		if (StringUtils.equals(module, FinanceConstants.CROSSLOANKNOCKOFF_ENQUIRY)) {
			enqiryModule = true;
			isKnockOff = true;
			super.moduleCode = "CrossLoanKnockOffHeader";
			super.tableName = "CROSSLOANKNOCKOFFHEADER_AVIEW";
			super.queueTableName = "CROSSLOANKNOCKOFFHEADER_AVIEW";
		}

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CrossLoanKnockOffList(Event event) {
		logger.debug("Entering " + event.toString());

		// Set the page level components.
		setPageComponents(window_CrossLoanKnockOffList, borderLayout_ReceiptList, listBoxReceipts, pagingReceiptList);
		setItemRender(new CrossLoanListModelItemRenderer());

		// Register buttons and fields.
		if (StringUtils.equals(this.module, FinanceConstants.RECEIPT_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.CROSSLOANKNOCKOFF_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)) {
			registerButton(button_ReceiptList_NewReceipt, "button_ReceiptList_NewReceipt", true);
		}

		registerButton(button_ReceiptList_ReceiptSearchDialog);

		registerField("receiptID", listheader_ReceiptId, SortOrder.ASC, receiptId, sortOperator_receiptId,
				Operators.DEFAULT);
		registerField("receiptDate", listheader_ReceiptDate, SortOrder.NONE, receiptDate, sortOperator_receiptDate,
				Operators.DATE);
		registerField("receiptPurpose");
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, customer, sortOperator_customer, Operators.STRING);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);

		registerField("receiptAmount", listheader_ReceiptAmount, SortOrder.NONE, receiptAmount,
				sortOperator_receiptAmount, Operators.NUMERIC);
		registerField("FROMFINREFERENCE", listheader_FromFinReference, SortOrder.NONE, fromFinReference,
				sortOperator_fromloanReference, Operators.STRING);
		registerField("TOFINREFERENCE", listheader_ToFinReference, SortOrder.NONE, toFinReference,
				sortOperator_toloanReference, Operators.STRING);

		if (!StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)
				&& !StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)) {
			registerField("receiptMode");
			registerField("paymentType", listheader_ExcessType);
		}

		if (StringUtils.equals(this.module, FinanceConstants.KNOCKOFFCAN_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.KNOCKOFFCAN_APPROVER)
				|| StringUtils.equals(this.module, null)) {

			registerField("knockOffType", listheader_KnockOffType);
			this.listheader_KnockOffType.setVisible(true);
		}

		registerField("recordStatus", listheader_RecordStatus);
		registerField("nextRoleCode", listheader_NextRoleCode);
		registerField("CrossLoanHeaderId");

		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();

		// PSD ID : 157792, to avoid performance issue
		// search();

		logger.debug("Leaving " + event.toString());
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");
		this.listHeader_CheckBox_Comp.setChecked(false);
		recHeaderMap.clear();
		headerMap.clear();
		this.searchObject.clearFilters();
		addRegisteredFilters();

		StringBuilder whereClause = new StringBuilder();

		if (enqiryModule) {
			List<String> filterList = new ArrayList<>();
			filterList.add(FinServiceEvent.FEEPAYMENT);
			searchObject.addFilterNotIn("RECEIPTPURPOSE", filterList);
			// whereClause = new StringBuilder(" PAYAGAINSTID = 0");
		} else if (!enqiryModule) {
			if (StringUtils.equals(module, FinanceConstants.REALIZATION_APPROVER)
					|| StringUtils.equals(module, FinanceConstants.RECEIPT_APPROVER)) {
				List<String> filterList = new ArrayList<>();
				filterList.add(FinanceConstants.REALIZATION_APPROVER);
				filterList.add(FinanceConstants.RECEIPT_APPROVER);
				searchObject.addFilterIn("NEXTROLECODE", filterList);
				whereClause = new StringBuilder(" RECEIPTMODE !='EXCESS'");

			} else if (StringUtils.equals(module, FinanceConstants.REALIZATION_MAKER)) {
				whereClause = new StringBuilder(
						" RECEIPTMODE !='EXCESS' AND ((RECEIPTMODESTATUS IN ('R', 'D')  AND RECEIPTPURPOSE = 'SchdlRepayment' and (NEXTROLECODE is null or NEXTROLECODE='REALIZATION_MAKER')) OR NEXTROLECODE ='"
								+ module + "')");

			} else if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_MAKER)) {
				whereClause = new StringBuilder( // PSD : 162156
						"  RECEIPTPURPOSE = 'SchdlRepayment'  and RECEIPTMODE='EXCESS'  and ((NEXTROLECODE is null and RECEIPTMODESTATUS = 'R') or NEXTROLECODE='"
								+ module + "')");
			} else if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_APPROVER)) {
				whereClause = new StringBuilder(
						"  RECEIPTPURPOSE = 'SchdlRepayment'  and (NEXTROLECODE='" + module + "')");
			} else {
				// searchObject.addFilterEqual("NEXTROLECODE", module);
				if (!isKnockOff) {
					whereClause = new StringBuilder(" RECEIPTMODE != 'EXCESS'");
				}
			}
			List<Long> receiptIdList = receiptService.getInProcessMultiReceiptRecord();
			if (receiptIdList != null && CollectionUtils.isNotEmpty(receiptIdList)) {
				Filter fil = new Filter("ReceiptID", receiptIdList, Filter.OP_NOT_IN);
				searchObject.addFilter(fil);
			}
		}

		// Filtering added based on user branch and division
		if (StringUtils.isNotBlank(whereClause.toString())) {
			whereClause.append(" ) AND ( ");
		}
		whereClause.append(getUsrFinAuthenticationQry(false));

		searchObject.addWhereClause(whereClause.toString());

		/*
		 * searchObject.addFilter( new Filter("ReceiptPurpose", RepayConstants.NONLAN_RECEIPT_NOTAPPLICABLE,
		 * Filter.OP_NOT_EQUAL));
		 */

		logger.debug("Leaving");
	}

	public void addRegisteredFilters() {
		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {

				if (filter.getProperty().equals("receiptAmount")) {
					filter.setValue(PennantApplicationUtil.unFormateAmount((BigDecimal) filter.getValue(),
							PennantConstants.defaultCCYDecPos));
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

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.customer.setModuleName("Customer");
		this.customer.setValueColumn("CustCIF");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setValidateColumns(new String[] { "CustCIF" });

		this.fromFinReference.setModuleName("FinanceMain");
		this.fromFinReference.setValueColumn("FinReference");
		this.fromFinReference.setDescColumn("FinType");
		this.fromFinReference.setValidateColumns(new String[] { "FinReference" });

		this.toFinReference.setModuleName("FinanceMain");
		this.toFinReference.setValueColumn("FinReference");
		this.toFinReference.setDescColumn("FinType");
		this.toFinReference.setValidateColumns(new String[] { "FinReference" });

		listheader_ExcessType.setVisible(true);

		row_2.setVisible(true);
		row_4.setVisible(false);
		if (enqiryModule) {
			listheader_ReceiptModeStatus.setVisible(true);
			listheader_NextRoleCode.setVisible(true);
		}

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
		map.put("crossLoanKnockOffListCtrl", this);
		map.put("isForeClosure", false);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PaymentMode/SelectCrossLoanKnockOffDialog.zul", null, map);
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

	public void setSearchObj(JdbcSearchObject<CrossLoanKnockOffHeader> searchObj) {
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

	public class CrossLoanListModelItemRenderer implements ListitemRenderer<CrossLoanKnockOffHeader>, Serializable {
		private static final long serialVersionUID = 8848425569301884635L;

		public CrossLoanListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, CrossLoanKnockOffHeader crossLoanHeader, int count) throws Exception {

			Listcell lc;

			lc = new Listcell(String.valueOf(crossLoanHeader.getReceiptId()));
			lc.setParent(item);

			lc = new Listcell(
					PennantAppUtil.formateDate(crossLoanHeader.getReceiptDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			if (StringUtils.equals(crossLoanHeader.getKnockoffType(), RepayConstants.KNOCKOFF_TYPE_MANUAL)) {
				lc = new Listcell(RepayConstants.KNOCKOFF_TYPE_MANUAL);
				lc.setParent(item);
			} else if (StringUtils.equals(crossLoanHeader.getKnockoffType(), RepayConstants.KNOCKOFF_TYPE_AUTO)) {
				lc = new Listcell(RepayConstants.KNOCKOFF_TYPE_AUTO);
				lc.setParent(item);
			} else {
				lc = new Listcell("");
				lc.setParent(item);
			}

			lc = new Listcell(crossLoanHeader.getReceiptMode());
			lc.setParent(item);

			String receiptPurpose = crossLoanHeader.getReceiptPurpose();
			if (FinServiceEvent.EARLYRPY.equals(crossLoanHeader.getReceiptPurpose())) {
				receiptPurpose = FinanceConstants.PARTIALSETTLEMENT;
			}
			lc = new Listcell(receiptPurpose);
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getCustCif());
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getFinType());
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getFromFinreference());
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getToFinreference());
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getPaymentType());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(crossLoanHeader.getReceiptAmount(),
					PennantConstants.defaultCCYDecPos));
			lc.setStyle("text-align:right");
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(crossLoanHeader.getNextRoleCode());
			lc.setParent(item);

			item.setAttribute("crossLoanKnockOffHeader", crossLoanHeader);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCrossLoanItemDoubleClicked");
		}

	}

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) throws Exception {
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
	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
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

	public void onCrossLoanItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxReceipts.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		CrossLoanKnockOffHeader crossLoanHeader = (CrossLoanKnockOffHeader) selectedItem
				.getAttribute("crossLoanKnockOffHeader");

		if (!!enqiryModule) {
			setWorkflowDetails(crossLoanHeader.getFinType(), false);
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
		}

		CrossLoanKnockOffHeader crossLoanKnockOffHeader = crossLoanKnockOffService
				.getCrossLoanHeaderById(crossLoanHeader.getCrossLoanHeaderId(), "_View");
		CrossLoanTransfer crossLoanTransfer = crossLoanKnockOffService
				.getCrossLoanTransferById(crossLoanKnockOffHeader.getCrossLoanId(), "_View");
		crossLoanKnockOffHeader.setCrossLoanTransfer(crossLoanTransfer);

		FinReceiptHeader finReceiptHeader = receiptService
				.getFinReceiptHeaderById(crossLoanKnockOffHeader.getKnockOffReceiptId(), false, "_View");

		finReceiptHeader.setValueDate(finReceiptHeader.getValueDate());
		// Role Code State Checking
		String userRole = crossLoanKnockOffHeader.getNextRoleCode();

		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = crossLoanKnockOffHeader.getNextRoleCode();
		if (!enqiryModule && StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
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

		String whereCond = " FinReference='" + finReceiptHeader.getReference() + "'";
		FinReceiptData finReceiptData = null;
		String eventCode = "";
		if (isWorkFlowEnabled() || enqiryModule) {

			if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(), FinServiceEvent.SCHDRPY)) {
				eventCode = AccountingEvent.REPAY;

			} else if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYRPY)) {
				eventCode = AccountingEvent.EARLYPAY;

			} else if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE)) {
				eventCode = AccountingEvent.EARLYSTL;

			}

			finReceiptData = receiptService.getFinReceiptDataByReceiptId(crossLoanKnockOffHeader.getKnockOffReceiptId(),
					eventCode, FinServiceEvent.RECEIPT, userRole);

			if (isKnockOff) {
				FinReceiptHeader rch = finReceiptData.getReceiptHeader();
				rch.setReceiptMode(rch.getReceiptDetails().get(0).getPaymentType());
				rch.setKnockOffRefId(rch.getReceiptDetails().get(0).getPayAgainstID());
			}

			if (finReceiptData.getReceiptHeader().getWorkflowId() == 0 && isWorkFlowEnabled()) {
				finReceiptData.getReceiptHeader().setWorkflowId(workFlowDetails.getWorkFlowId());
			}

			if (StringUtils.isEmpty(userRole)) {
				userRole = workFlowDetails.getFirstTaskOwner();
			}

			if (doCheckAuthority(crossLoanKnockOffHeader, whereCond) || StringUtils
					.equals(crossLoanKnockOffHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
				doShowReceiptView(crossLoanKnockOffHeader, finReceiptData);
			} else {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			}
		} else {
			if (doCheckAuthority(crossLoanKnockOffHeader, whereCond)
					|| StringUtils.equals(finReceiptHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SAVED)) {
				doShowReceiptView(crossLoanKnockOffHeader, finReceiptData);
			} else {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
			}
		}

		logger.debug("Leaving");
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {

		// Finance Maintenance Workflow Check & Assignment
		if (StringUtils.equals(module, "REALIZATION_MAKER")) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReceiptRealization");

		} else if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_MAKER)
				|| StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_APPROVER)) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails(this.moduleCode);
		} else if (StringUtils.isNotEmpty(workflowCode)) {
			String workflowTye = getFinanceWorkFlowService().getFinanceWorkFlowType(finType, workflowCode,
					isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(workflowTye);
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void doShowReceiptView(CrossLoanKnockOffHeader crossLoanKnockOffHeader, FinReceiptData finReceiptData) {

		if (crossLoanKnockOffHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			crossLoanKnockOffHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		crossLoanKnockOffHeader.setFinReceiptData(finReceiptData);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("crossLoanHeader", crossLoanKnockOffHeader);
		map.put("receiptData", finReceiptData);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("crossLoanKnockOffListCtrl", this);
		map.put("isKnockOff", isKnockOff);
		map.put("isForeClosure", false);

		try {
			if (enqiryModule) {
				map.put("enqiryModule", enqiryModule);
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptsEnquiryDialog.zul", null,
						map);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/CrossLoanKnockOffDialog.zul",
						null, map);
			}
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

		Set<Long> recId = recHeaderMap.keySet();
		for (long receiptId : recId) {
			FinReceiptHeader finReceiptHeader = receiptService.getFinReceiptHeaderById(receiptId, false, "_View");
			finReceiptHeader.setValueDate(finReceiptHeader.getValueDate());
			for (FinReceiptDetail receiptDetail : finReceiptHeader.getReceiptDetails()) {
				if (!(RepayConstants.PAYTYPE_EMIINADV.equals(receiptDetail.getPaymentType())
						|| RepayConstants.PAYTYPE_EXCESS.equals(receiptDetail.getPaymentType())
						|| RepayConstants.PAYTYPE_PAYABLE.equals(receiptDetail.getPaymentType()))) {
					finReceiptHeader.setDepositDate(receiptDetail.getDepositDate());
				}
			}

			boolean canProcessReceipt = receiptService.canProcessReceipt(finReceiptHeader.getReceiptID());

			if (!canProcessReceipt && !enqiryModule) {
				String[] valueParm = new String[1];
				valueParm[0] = "Unable to process the request, loan is in in-active state";
				MessageUtil.showMessage(valueParm[0]);

				logger.debug("Leaving");
				return;
			}

			setWorkflowDetails(finReceiptHeader.getFinType(), false);

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
		map.put("crossLoanKnockOffListCtrl", this);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("roleCode", roleCode);
		map.put("recordAction", recordAction);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/SelectReceiptDialog.zul", null, map);
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

	public CrossLoanKnockOffService getCrossLoanKnockOffService() {
		return crossLoanKnockOffService;
	}

	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

}
