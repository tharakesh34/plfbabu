package com.pennant.webui.financemanagement.paymentMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
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
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
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
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ReceiptListCtrl extends GFCBaseListCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = 778410382420505812L;
	private static final Logger logger = Logger.getLogger(ReceiptListCtrl.class);

	protected Window window_ReceiptList;
	protected Borderlayout borderLayout_ReceiptList;
	protected Paging pagingReceiptList;
	protected Listbox listBoxReceipts;

	protected Button btnClear;
	protected Button button_ReceiptList_NewReceipt;
	protected Button button_ReceiptList_ReceiptSearchDialog;

	protected Longbox receiptId;
	protected Datebox receiptDate;
	protected Decimalbox receiptAmount;
	protected Combobox receiptMode;
	protected Combobox receiptPurpose;
	protected ExtendedCombobox partnerBank;
	protected ExtendedCombobox finType;
	protected Textbox transactionRef;
	protected ExtendedCombobox customer;
	protected ExtendedCombobox finReference;
	private boolean isKnockOff;
	private boolean isForeClosure;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_ReceiptId;
	protected Listheader listheader_ReceiptDate;
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

	protected Row row_1;
	protected Row row_2;
	protected Row row_3;
	protected Row row_4;
	protected Row row_5;

	private String module;
	private ReceiptService receiptService;

	private String workflowCode = FinanceConstants.FINSER_EVENT_RECEIPT;;
	private transient FinanceWorkFlowService financeWorkFlowService;

	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public ReceiptListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "FinReceiptHeader";
		super.pageRightName = "FinReceiptHeader";
		if (StringUtils.equals("Y", getArgument("enqiryModule"))) {
			super.tableName = "RECEIPTDETAILS_VIEW";
			super.queueTableName = "RECEIPTDETAILS_VIEW";
		} else {
			super.tableName = "RECEIPTDETAILS_TVIEW";
			super.queueTableName = "RECEIPTDETAILS_TVIEW";
		}
		this.module = getArgument("module");
		if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_MAKER)) {
			super.tableName = "RECEIPT_Realization_VIEW";
			super.queueTableName = "RECEIPT_Realization_VIEW";
		}
		if (StringUtils.equals(module, FinanceConstants.KNOCKOFF_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.KNOCKOFF_MAKER)) {
			isKnockOff = true;
		}
		if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_MAKER)) {
			isKnockOff = true;
			super.moduleCode = "ReceiptKnockOffCancel";
			super.tableName = "RECEIPTDETAILS_VIEW";
			super.queueTableName = "RECEIPTDETAILS_VIEW";
		}
		if (StringUtils.equals(module, FinanceConstants.CLOSURE_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.CLOSURE_MAKER)) {
			isForeClosure = true;
			super.tableName = "FORECLOSURE_TVIEW";
			super.queueTableName = "FORECLOSURE_TVIEW";
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReceiptList(Event event) {
		logger.debug("Entering " + event.toString());

		// Set the page level components.
		setPageComponents(window_ReceiptList, borderLayout_ReceiptList, listBoxReceipts, pagingReceiptList);
		setItemRender(new ReceiptListModelItemRenderer());

		// Register buttons and fields.
		if (StringUtils.equals(this.module, FinanceConstants.RECEIPT_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)) {
			registerButton(button_ReceiptList_NewReceipt, "button_ReceiptList_NewReceipt", true);
		}

		registerButton(button_ReceiptList_ReceiptSearchDialog);

		registerField("receiptID", listheader_ReceiptId, SortOrder.ASC, receiptId, sortOperator_receiptId,
				Operators.NUMERIC);
		registerField("receiptDate", listheader_ReceiptDate, SortOrder.NONE, receiptDate, sortOperator_receiptDate,
				Operators.DATE);
		registerField("receiptPurpose", listheader_ReceiptPurpose, SortOrder.NONE, receiptPurpose,
				sortOperator_receiptPurpose, Operators.DEFAULT);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, customer, sortOperator_customer, Operators.STRING);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);

		registerField("receiptAmount", listheader_ReceiptAmount, SortOrder.NONE, receiptAmount,
				sortOperator_receiptAmount, Operators.NUMERIC);
		registerField("reference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_loanReference,
				Operators.STRING);

		if (!StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)
				&& !StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)
				&& !StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)
				&& !StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_APPROVER)) {
			registerField("transactionRef", listheader_PayTypeRef, SortOrder.NONE, transactionRef,
					sortOperator_transactionRef, Operators.STRING);
			registerField("depositDate", listheader_DepositDate);
			registerField("realizationDate", listheader_RealizationDate);
			registerField("partnerBankCode", listheader_PartnerBank, SortOrder.NONE, partnerBank,
					sortOperator_partnerBank, Operators.DEFAULT);
			registerField("paymentType", listheader_ExcessType);
			registerField("receiptMode", listheader_ReceiptMode, SortOrder.NONE, receiptMode, sortOperator_receiptMode,
					Operators.DEFAULT);
		}
		registerField("recordStatus", listheader_RecordStatus);
		registerField("receiptModeStatus", listheader_ReceiptModeStatus);
		registerField("nextRoleCode", listheader_NextRoleCode);
		// Render the page and display the data.
		doSetFieldProperties();
		doRenderPage();
		search();
		logger.debug("Leaving " + event.toString());
	}

	public void addRegisteredFilters() {
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
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");

		this.searchObject.clearFilters();
		addRegisteredFilters();

		if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_APPROVER)
				|| StringUtils.equals(module, FinanceConstants.RECEIPT_APPROVER)) {
			List<String> filterList = new ArrayList<>();
			filterList.add(FinanceConstants.RECEIPTREALIZE_APPROVER);
			filterList.add(FinanceConstants.RECEIPT_APPROVER);
			searchObject.addFilterIn("NEXTROLECODE", filterList);
		} else if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_MAKER)) {
			List<String> filterList = new ArrayList<>();
			filterList.add("R");
			filterList.add("D");
			searchObject.addFilterOr(Filter.equalTo("NEXTROLECODE", module),
					Filter.in("RECEIPTMODESTATUS", filterList));
		}
		if (enqiryModule) {
			List<String> filterList = new ArrayList<>();
			filterList.add(FinanceConstants.FINSER_EVENT_FEEPAYMENT);
			searchObject.addFilterNotIn("RECEIPTPURPOSE", filterList);
			searchObject.addWhereClause(" PAYAGAINSTID = 0");
		} else if (!enqiryModule) {
			if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_APPROVER)
					|| StringUtils.equals(module, FinanceConstants.RECEIPT_APPROVER)) {
				List<String> filterList = new ArrayList<>();
				filterList.add(FinanceConstants.RECEIPTREALIZE_APPROVER);
				filterList.add(FinanceConstants.RECEIPT_APPROVER);
				searchObject.addFilterIn("NEXTROLECODE", filterList);
				searchObject.addWhereClause(" PAYAGAINSTID = 0");

			} else if (StringUtils.equals(module, FinanceConstants.RECEIPTREALIZE_MAKER)) {
				searchObject.addWhereClause(
						" PAYAGAINSTID = 0 AND ((RECEIPTMODESTATUS IN ('R', 'D')  AND RECEIPTPURPOSE = 'SchdlRepayment') OR NEXTROLECODE ='"
								+ module + "')");

			} else if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_MAKER)) {
				searchObject.addWhereClause(
						" PAYAGAINSTID > 0 And RECEIPTPURPOSE = 'SchdlRepayment' and ((NEXTROLECODE is null and ReceiptModeStatus != 'C')"
								+ "OR NEXTROLECODE='" + module + "')");
			} else if (StringUtils.equals(module, FinanceConstants.KNOCKOFFCAN_APPROVER)) {
				searchObject.addWhereClause(
						" PAYAGAINSTID > 0 And RECEIPTPURPOSE = 'SchdlRepayment'  and (NEXTROLECODE='" + module + "')");
			} else {
				searchObject.addFilterEqual("NEXTROLECODE", module);
				if (!isKnockOff && !isForeClosure) {
					searchObject.addWhereClause(" PAYAGAINSTID = 0");
				}
			}
		}
		logger.debug("Leaving");
	}

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		// TODO CH : Static List method should be changed to Receipt Modes and
		// Sub receipt mode should be available for filter and list box
		fillComboBox(receiptMode, "", PennantStaticListUtil.getReceiptPaymentModes(), "");
		fillComboBox(receiptPurpose, "", PennantStaticListUtil.getReceiptPurpose(), ",FeePayment,");

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.customer.setModuleName("Customer");
		this.customer.setValueColumn("CustCIF");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

		if (StringUtils.equals(this.module, FinanceConstants.RECEIPTREALIZE_MAKER)) {
			listheader_DepositDate.setVisible(true);
		} else if (StringUtils.equals(this.module, FinanceConstants.RECEIPT_APPROVER)
				|| StringUtils.equals(this.module, FinanceConstants.RECEIPTREALIZE_APPROVER)) {
			listheader_RealizationDate.setVisible(true);
			listheader_DepositDate.setVisible(true);
		} else if (StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.KNOCKOFF_APPROVER)) {
			listheader_DepositDate.setVisible(false);
			listheader_RealizationDate.setVisible(false);
			listheader_ReceiptMode.setVisible(false);
			listheader_PayTypeRef.setVisible(false);
			listheader_PartnerBank.setVisible(false);
			listheader_ExcessType.setVisible(true);

			row_2.setVisible(true);
			row_4.setVisible(false);
			row_5.setVisible(false);
		} else if (StringUtils.equals(this.module, FinanceConstants.CLOSURE_MAKER)
				|| StringUtils.equals(this.module, FinanceConstants.CLOSURE_APPROVER)) {
			listheader_DepositDate.setVisible(false);
			listheader_RealizationDate.setVisible(false);
			listheader_ReceiptPurpose.setVisible(false);
			listheader_ReceiptMode.setVisible(false);
			row_2.setVisible(true);
			row_5.setVisible(false);
		} else if (enqiryModule) {
			listheader_ReceiptModeStatus.setVisible(true);
			listheader_NextRoleCode.setVisible(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ReceiptList_ReceiptSearchDialog(Event event) {
		search();
	}

	// TODO CH : To be changed to a single zul and controller
	public void onClick$button_ReceiptList_NewReceipt(Event event) {
		logger.debug("Entering ");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("module", this.module);
		map.put("receiptListCtrl", this);
		map.put("isForeClosure", isForeClosure);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/SelectReceiptPaymentDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doRefresh();
	}

	private void doRefresh() {
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
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	public class ReceiptListModelItemRenderer implements ListitemRenderer<FinReceiptHeader>, Serializable {
		private static final long serialVersionUID = 8848425569301884635L;

		public ReceiptListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, FinReceiptHeader finReceiptHeader, int count) throws Exception {

			Listcell lc;

			lc = new Listcell();

			lc = new Listcell(String.valueOf(finReceiptHeader.getReceiptID()));
			lc.setParent(item);

			lc = new Listcell(
					PennantAppUtil.formateDate(finReceiptHeader.getReceiptDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getReceiptMode());
			lc.setParent(item);

			// TODO CH : Receipt Purpose in Filter and List are different. To be
			// corrected
			lc = new Listcell(finReceiptHeader.getReceiptPurpose());
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getCustCIF());
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getFinType());
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getReference());
			lc.setParent(item);

			// TODO CH -Should show Excess/Payable FeeType/BOUNCE
			lc = new Listcell(finReceiptHeader.getPaymentType());
			lc.setParent(item);

			lc = new Listcell(finReceiptHeader.getTransactionRef());
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.amountFormate(finReceiptHeader.getReceiptAmount(),
					PennantConstants.defaultCCYDecPos));
			lc.setStyle("text-align:right");
			lc.setParent(item);

			lc = new Listcell(
					PennantAppUtil.formateDate(finReceiptHeader.getDepositeDate(), DateFormat.SHORT_DATE.getPattern()));
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

		setWorkflowDetails(finRcptHeader.getFinType(), false);
		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		FinReceiptHeader finReceiptHeader = receiptService.getFinReceiptHeaderById(finRcptHeader.getReceiptID(), false,
				"_View");
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

		String whereCond = " FinReference='" + finReceiptHeader.getReference() + "'";
		FinReceiptData finReceiptData = null;
		if (isWorkFlowEnabled()) {
			String eventCode = "";

			if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_REPAY;

			} else if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYPAY;

			} else if (StringUtils.equals(finReceiptHeader.getReceiptPurpose(),
					FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				eventCode = AccountEventConstants.ACCEVENT_EARLYSTL;

			}

			finReceiptData = receiptService.getFinReceiptDataByReceiptId(finRcptHeader.getReceiptID(), eventCode,
					FinanceConstants.FINSER_EVENT_RECEIPT, userRole);
			if (isKnockOff) {
				FinReceiptHeader rch = finReceiptData.getReceiptHeader();
				rch.setKnockOffRefId(rch.getReceiptDetails().get(0).getPayAgainstID());
			}
			if (!enqiryModule && finReceiptData.isCalReq()) {
				ErrorDetail errorDetail = receiptService.doInstrumentValidation(finReceiptData);
				if (errorDetail != null) {
					ErrorDetail errorDtl = ErrorUtil.getErrorDetail(errorDetail);
					MessageUtil.showError(errorDtl.getError());

					Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
					logger.debug("Leaving");
					return;
				}
			}

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

	private void doShowReceiptView(FinReceiptHeader finReceiptHeader, FinReceiptData finReceiptData) {

		if (finReceiptHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			finReceiptHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("finReceiptHeader", finReceiptHeader);
		map.put("receiptData", finReceiptData);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("receiptListCtrl", this);
		map.put("isKnockOff", isKnockOff);
		map.put("isForeClosure", isForeClosure);

		try {
			if (enqiryModule) {
				map.put("enqiryModule", enqiryModule);
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptsEnquiryDialog.zul", null,
						map);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/ReceiptDialog.zul", null, map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
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

}
