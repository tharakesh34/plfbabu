package com.pennant.webui.financemanagement.receipts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.knockoff.KnockOffType;
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
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CrossLoanKnockOffListCtrl extends GFCBaseListCtrl<CrossLoanKnockOff> {
	private static final long serialVersionUID = 778410382420505812L;

	protected Window windowCrossLoanKnockOffList;
	protected Borderlayout crossLoanKnockOffBL;
	protected Paging crossLoanListPaging;
	protected Listbox crossLoanListBox;
	protected Button btnClear;
	protected Button btnNew;
	protected Button btnSearch;
	protected Button btnSubmit;
	protected Button btnReSubmit;
	protected Button btnApprove;
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
	protected Listheader lhCustCIF;
	protected Listheader lhFinType;
	protected Listheader lhFromFinReference;
	protected Listheader lhToFinReference;
	protected Listheader lhReceiptId;
	protected Listheader lhReceiptDate;
	protected Listheader lh_ReceiptPurpose;
	protected Listheader lhKnockOffType;
	protected Listheader lhReceiptAmount;
	protected Listheader lhExcessType;
	protected Listheader lhReceiptModeStatus;
	protected Listheader lhNextRoleCode;
	protected Listbox soCustCIF;
	protected Listbox soFinType;
	protected Listbox soReceiptID;
	protected Listbox soReceiptDate;
	protected Listbox soReceiptAmount;
	protected Listbox soFromLoanReference;
	protected Listbox soToLoanReference;
	protected Row row2;
	protected Row row4;
	protected Listcell lcCheckbox;
	protected Listitem liCheckbox;
	protected Checkbox cbCheckBoxComp;

	private Map<Long, FinReceiptHeader> recHeaderMap = new HashMap<>();
	private Map<Long, FinReceiptHeader> headerMap = new HashMap<>();
	private String module;
	private boolean isCancel = false;

	private transient ReceiptService receiptService;
	private transient WorkFlowDetails workFlowDetails = null;
	private transient CrossLoanKnockOffService crossLoanKnockOffService;
	private FinExcessAmountDAO finExcessAmountDAO;

	public CrossLoanKnockOffListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CrossLoanKnockOff";
		super.pageRightName = "CrossLoanKnockOffHeader";
		super.tableName = "CROSS_LOAN_KNOCKOFF_TVIEW";
		super.queueTableName = "CROSS_LOAN_KNOCKOFF_TVIEW";

		this.module = getArgument("module");
		if (FinanceConstants.CROSS_LOAN_KNOCKOFF_ENQUIRY.equals(this.module)) {
			enqiryModule = true;
			super.tableName = "CROSS_LOAN_KNOCKOFF_AVIEW";
			super.queueTableName = "CROSS_LOAN_KNOCKOFF_AVIEW";
		}
		if (FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_MAKER.equals(this.module)) {
			isCancel = true;
			super.moduleCode = "CancelCrossLoanKnockOff";
			super.tableName = "CROSS_LOAN_KNOCKOFF_LVIEW";
			super.queueTableName = "CROSS_LOAN_KNOCKOFF_LVIEW";
		}

		if (FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER.equals(this.module)) {
			isCancel = true;
			super.moduleCode = "CancelCrossLoanKnockOff";
			super.tableName = "CROSS_LOAN_KNOCKOFF_TVIEW";
			super.queueTableName = "CROSS_LOAN_KNOCKOFF_TVIEW";
		}
	}

	public void onCreate$windowCrossLoanKnockOffList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowCrossLoanKnockOffList, crossLoanKnockOffBL, crossLoanListBox, crossLoanListPaging);
		setItemRender(new CrossLoanListModelItemRenderer());

		if (FinanceConstants.RECEIPT_MAKER.equals(this.module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER.equals(this.module)
				|| FinanceConstants.CLOSURE_MAKER.equals(this.module)) {
			registerButton(btnNew, "button_ReceiptList_NewReceipt", true);
		}

		registerButton(btnSearch);
		registerField("receiptID", lhReceiptId, SortOrder.ASC, receiptId, soReceiptID, Operators.DEFAULT);
		registerField("receiptDate", lhReceiptDate, SortOrder.NONE, receiptDate, soReceiptDate, Operators.DATE);
		registerField("receiptPurpose", lh_ReceiptPurpose, SortOrder.NONE);
		registerField("custCIF", lhCustCIF, SortOrder.NONE, customer, soCustCIF, Operators.STRING);
		registerField("finType", lhFinType, SortOrder.NONE, finType, soFinType, Operators.STRING);
		registerField("receiptAmount", lhReceiptAmount, SortOrder.NONE, receiptAmount, soReceiptAmount,
				Operators.NUMERIC);
		registerField("FromFinReference", lhFromFinReference, SortOrder.NONE, fromFinReference, soFromLoanReference,
				Operators.STRING);
		registerField("ToFinReference", lhToFinReference, SortOrder.NONE, toFinReference, soToLoanReference,
				Operators.STRING);

		if (!FinanceConstants.CLOSURE_MAKER.equals(this.module)
				&& !FinanceConstants.CLOSURE_APPROVER.equals(this.module)) {
			registerField("receiptMode");
			registerField("paymentType", lhExcessType);
		}

		if (isKnockOffypeRequired()) {
			registerField("knockoffType", lhKnockOffType, SortOrder.NONE);
			this.lhKnockOffType.setVisible(true);
		}

		registerField("nextRoleCode", lhNextRoleCode);
		registerField("ID");
		registerField("recordStatus", listheader_RecordStatus);
		registerField("receiptModeStatus", lhReceiptModeStatus);

		doSetFieldProperties();
		doRenderPage();

		search();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private boolean isKnockOffypeRequired() {
		return FinanceConstants.KNOCKOFFCAN_MAKER.equals(this.module)
				|| FinanceConstants.KNOCKOFFCAN_APPROVER.equals(this.module) || StringUtils.equals(this.module, null)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_MAKER.equals(this.module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER.equals(this.module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_ENQUIRY.equals(this.module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER.equals(this.module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_APPROVER.equals(this.module);
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");
		this.searchObject.clearFilters();
		addRegisteredFilters();

		StringBuilder whereClause = new StringBuilder();

		if (FinanceConstants.CROSS_LOAN_KNOCKOFF_MAKER.equals(this.module)) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("NextRoleCode", "%MAKER%", Filter.OP_LIKE);
			this.searchObject.addFilters(filters);
		} else if (FinanceConstants.CROSS_LOAN_KNOCKOFF_APPROVER.equals(this.module)) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("NextRoleCode", "%APPROVER%", Filter.OP_LIKE);
			this.searchObject.addFilters(filters);
		} else if (FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER.equals(this.module)) {

			Filter[] filters = new Filter[3];
			filters[0] = new Filter("RECEIPTPURPOSE", ReceiptPurpose.SCHDRPY.code(), Filter.OP_EQUAL);
			filters[1] = new Filter("NextRoleCode", "%APPROVER%", Filter.OP_LIKE);
			filters[2] = new Filter("ReceiptModeStatus", "C");
			this.searchObject.addFilters(filters);
		} else if (FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_MAKER.equals(this.module)) {

			whereClause.append(
					"  RECEIPTPURPOSE = 'SchdlRepayment' and ((RECEIPTMODESTATUS = 'R' and (NEXTROLECODE is null Or NEXTROLECODE = '')) or NEXTROLECODE like '%MAKER')  and (KnockOffType = '"
							+ KnockOffType.CROSS_LOAN.code() + "' or KnockOffType  is null" + " or "
							+ "KnockOffType = '" + KnockOffType.AUTO_CROSS_LOAN.code() + "')");
		}

		// Filtering added based on user branch and division
		if (StringUtils.isNotBlank(whereClause.toString())) {
			whereClause.append(" ) AND ( ");
		}

		whereClause.append(getUsrFinAuthenticationQry(false));
		searchObject.addWhereClause(whereClause.toString());

		logger.debug("Leaving");
	}

	public void addRegisteredFilters() {
		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();

			if (filter == null) {
				continue;
			}

			String property = filter.getProperty();

			if (property.equals("receiptAmount")) {
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

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		liCheckbox = new Listitem();
		lcCheckbox = new Listcell();
		cbCheckBoxComp = new Checkbox();
		lcCheckbox.appendChild(cbCheckBoxComp);
		cbCheckBoxComp.addForward("onClick", self, "onClickLHCheckBox");
		liCheckbox.appendChild(lcCheckbox);

		this.receiptDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		// this.receiptAmount.setProperties(false, PennantConstants.defaultCCYDecPos);

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

		lhExcessType.setVisible(true);

		row2.setVisible(true);
		row4.setVisible(false);
		if (enqiryModule) {
			lhReceiptModeStatus.setVisible(true);
			lhNextRoleCode.setVisible(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		search();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNew(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Map<String, Object> map = new HashMap<>();
		map.put("module", this.module);
		map.put("crossLoanKnockOffListCtrl", this);
		map.put("isForeClosure", false);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PaymentMode/SelectCrossLoanKnockOffDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doRefresh();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doRefresh() {
		recHeaderMap.clear();
		headerMap.clear();
		this.cbCheckBoxComp.setChecked(false);
		doReset();
		search();
	}

	public void doSearch(boolean isFilterSearch) {

	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doPrintResults();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public class CrossLoanListModelItemRenderer implements ListitemRenderer<CrossLoanKnockOff>, Serializable {
		private static final long serialVersionUID = 8848425569301884635L;

		public CrossLoanListModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, CrossLoanKnockOff clk, int count) throws Exception {
			Listcell lc;

			lc = new Listcell(String.valueOf(clk.getReceiptID()));
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.formateDate(clk.getReceiptDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(KnockOffType.getDesc(clk.getKnockoffType()));
			lc.setParent(item);

			lc = new Listcell(clk.getReceiptMode());
			lc.setParent(item);

			String receiptPurpose = clk.getReceiptPurpose();
			if (FinServiceEvent.EARLYRPY.equals(receiptPurpose)) {
				receiptPurpose = "Partial Payment";
			}
			lc = new Listcell(receiptPurpose);
			lc.setParent(item);

			lc = new Listcell(clk.getCustCIF());
			lc.setParent(item);

			lc = new Listcell(clk.getFinType());
			lc.setParent(item);

			lc = new Listcell(clk.getFromFinReference());
			lc.setParent(item);

			lc = new Listcell(clk.getToFinReference());
			lc.setParent(item);

			lc = new Listcell(clk.getPaymentType());
			lc.setParent(item);

			lc = new Listcell(
					PennantApplicationUtil.amountFormate(clk.getReceiptAmount(), PennantConstants.defaultCCYDecPos));
			lc.setStyle("text-align:right");
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.formateDate(clk.getRealizationDate(), DateFormat.SHORT_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(clk.getRecordStatus());
			lc.setParent(item);

			String mode = clk.getReceiptMode();

			switch (clk.getReceiptModeStatus()) {
			case RepayConstants.PAYSTATUS_APPROVED:
				lc = new Listcell("Approved");
				lc.setParent(item);
				break;
			case RepayConstants.PAYSTATUS_FEES:
				lc = new Listcell("Fees");
				lc.setParent(item);
				break;
			case RepayConstants.PAYSTATUS_REALIZED:
				if (ReceiptMode.EXCESS.equals(mode) || ReceiptMode.EMIINADV.equals(mode)
						|| ReceiptMode.PAYABLE.equals(mode) || ReceiptMode.CASHCLT.equals(mode)
						|| ReceiptMode.DSF.equals(mode)) {
					lc = new Listcell("Adjusted");
					lc.setParent(item);
				} else {
					lc = new Listcell("Realized");
					lc.setParent(item);
				}
				break;
			case RepayConstants.PAYSTATUS_BOUNCE:
				lc = new Listcell("Bounce");
				lc.setParent(item);
				break;
			case RepayConstants.PAYSTATUS_CANCEL:
				lc = new Listcell("Cancel");
				lc.setParent(item);
				break;
			case RepayConstants.PAYSTATUS_DEPOSITED:
				lc = new Listcell("Deposited");
				lc.setParent(item);
				break;
			case RepayConstants.PAYSTATUS_INITIATED:
				lc = new Listcell("Initiated");
				lc.setParent(item);
				break;
			default:
				lc = new Listcell("");
				lc.setParent(item);
				break;
			}

			lc = new Listcell(clk.getNextRoleCode());
			lc.setParent(item);

			item.setAttribute("crossLoanKnockOffHeader", clk);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		}

	}

	public void onClickLHCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		for (int i = 0; i < crossLoanListBox.getItems().size(); i++) {
			Listitem listitem = crossLoanListBox.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(cbCheckBoxComp.isChecked());
		}

		if (cbCheckBoxComp.isChecked() && !crossLoanListBox.getItems().isEmpty()) {
			recHeaderMap.putAll(headerMap);
		} else {
			recHeaderMap.clear();
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		Listitem selectedItem = this.crossLoanListBox.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		CrossLoanKnockOff header = (CrossLoanKnockOff) selectedItem.getAttribute("crossLoanKnockOffHeader");

		if (enqiryModule || isCancel) {
			setWorkflowDetails(header.getFinType(), false);
			if (workFlowDetails == null) {
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
				return;
			}
		}

		CrossLoanKnockOff clk = crossLoanKnockOffService.getCrossLoanHeaderById(header.getId(), "_View");
		CrossLoanTransfer clt = crossLoanKnockOffService.getCrossLoanTransferById(clk.getTransferID(), "_View");
		clk.setCrossLoanTransfer(clt);

		long id = clt.getReceiptId();
		FinReceiptHeader frh = receiptService.getFinReceiptHeaderById(id, false, "_View");

		frh.setValueDate(frh.getValueDate());
		frh.setUserDetails(getUserWorkspace().getLoggedInUser());

		String userRole = clk.getNextRoleCode();

		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		String nextroleCode = clk.getNextRoleCode();
		if (!enqiryModule && StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = frh.getReference();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			ErrorDetail errorDetail = ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
					getUserWorkspace().getUserLanguage());
			MessageUtil.showError(errorDetail.getError());

			Events.sendEvent(Events.ON_CLICK, this.btnClear, null);
			logger.debug(Literal.LEAVING);
			return;
		}

		FinReceiptData frd = null;
		String eventCode = "";

		if (isWorkFlowEnabled() || enqiryModule) {
			String receiptPurp = frh.getReceiptPurpose();
			if (FinServiceEvent.SCHDRPY.equals(receiptPurp)) {
				eventCode = AccountingEvent.REPAY;
			} else if (FinServiceEvent.EARLYRPY.equals(receiptPurp)) {
				eventCode = AccountingEvent.EARLYPAY;
			} else if (FinServiceEvent.EARLYSETTLE.equals(receiptPurp)) {
				eventCode = AccountingEvent.EARLYSTL;
			}

			frd = receiptService.getFinReceiptDataByReceiptId(id, eventCode, FinServiceEvent.RECEIPT, userRole);

			FinReceiptHeader rch = frd.getReceiptHeader();
			rch.setReceiptMode(rch.getReceiptDetails().get(0).getPaymentType());
			rch.setKnockOffRefId(rch.getReceiptDetails().get(0).getPayAgainstID());
			rch.setUserDetails(getUserWorkspace().getLoggedInUser());

			clk.setExcessValueDate(clk.getValueDate());
			clt.setExcessValueDate(clk.getValueDate());

			FinExcessAmount fea = finExcessAmountDAO.getFinExcessByID(rch.getKnockOffRefId());
			if (fea != null && fea.getValueDate() != null) {
				clk.setExcessValueDate(fea.getValueDate());
				clt.setExcessValueDate(fea.getValueDate());
			}

			if (rch.getWorkflowId() == 0 && isWorkFlowEnabled()) {
				rch.setWorkflowId(workFlowDetails.getWorkFlowId());
			}
		}

		if (PennantConstants.RCD_STATUS_SAVED.equals(clk.getRecordStatus())
				|| doCheckAuthority(clk, " FinReference = ?", new Object[] { frh.getReference() })) {
			doShowReceiptView(clk, frd);
		} else {
			MessageUtil.showError(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	private void setWorkflowDetails(String finType, boolean isPromotion) {
		if (FinanceConstants.REALIZATION_MAKER.equals(module)) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("ReceiptRealization");
		} else if (FinanceConstants.KNOCKOFFCAN_MAKER.equals(module)
				|| FinanceConstants.KNOCKOFFCAN_APPROVER.equals(module)) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails(this.moduleCode);
		} else if (FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_MAKER.equals(module)
				|| FinanceConstants.CROSS_LOAN_KNOCKOFF_CANCEL_APPROVER.equals(module)) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails(this.moduleCode);
		} else {
			String workflowTye = financeWorkFlowService.getFinanceWorkFlowType(finType, FinServiceEvent.RECEIPT,
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

	private void doShowReceiptView(CrossLoanKnockOff clk, FinReceiptData frd) {
		if (clk.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			clk.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		if (isCancel && PennantConstants.RCD_STATUS_APPROVED.equals(clk.getRecordStatus())) {
			clk.setNewRecord(true);
		}

		clk.setCancelProcess(isCancel);
		clk.setFinReceiptData(frd);

		Map<String, Object> map = new HashMap<>();

		map.put("crossLoanHeader", clk);
		map.put("receiptData", frd);
		map.put("module", module);
		map.put("moduleCode", moduleCode);
		map.put("crossLoanKnockOffListCtrl", this);
		map.put("isKnockOff", true);
		map.put("isForeClosure", false);
		map.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/PaymentMode/CrossLoanKnockOffDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$btnSubmit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		doShowMultiReceipt(PennantConstants.RCD_STATUS_SUBMITTED);

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onClick$btnReSubmit(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		doShowMultiReceipt(PennantConstants.RCD_STATUS_RESUBMITTED);

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onClick$btnApprove(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		doShowMultiReceipt(PennantConstants.RCD_STATUS_APPROVED);

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void doShowMultiReceipt(String recordAction) {
		logger.debug(Literal.ENTERING);

		if (recHeaderMap.isEmpty()) {
			MessageUtil.showError("Please Select at least 1 record to proceed");
			return;
		}

		String roleCode = null;
		Set<Long> recId = recHeaderMap.keySet();

		for (long id : recId) {
			FinReceiptHeader rch = receiptService.getFinReceiptHeaderById(id, false, "_View");
			rch.setValueDate(rch.getValueDate());
			for (FinReceiptDetail rcd : rch.getReceiptDetails()) {
				String paymentType = rcd.getPaymentType();
				if (!(RepayConstants.PAYTYPE_EMIINADV.equals(paymentType)
						|| RepayConstants.PAYTYPE_EXCESS.equals(paymentType)
						|| RepayConstants.PAYTYPE_PAYABLE.equals(paymentType))) {
					rch.setDepositDate(rcd.getDepositDate());
				}
			}

			if (!receiptService.canProcessReceipt(rch.getReceiptID()) && !enqiryModule) {
				String[] valueParm = new String[1];
				valueParm[0] = "Unable to process the request, loan is in in-active state";
				MessageUtil.showMessage(valueParm[0]);

				logger.debug(Literal.LEAVING);
				return;
			}

			setWorkflowDetails(rch.getFinType(), false);

			String whereCond = " Reference = ?";
			if (!(doCheckAuthority(rch, whereCond, new Object[] { rch.getReference() })
					|| PennantConstants.RCD_STATUS_SAVED.equals(rch.getRecordStatus()))) {
				MessageUtil.showError(Labels.getLabel("info.not_authorized"));
				return;
			}

			if (rch.getWorkflowId() == 0 && isWorkFlowEnabled()) {
				rch.setWorkflowId(workFlowDetails.getWorkFlowId());
				roleCode = workFlowDetails.getFirstTaskOwner();
			} else {
				roleCode = rch.getNextRoleCode();
			}
			recHeaderMap.put(id, rch);
		}

		Map<String, Object> map = new HashMap<>();

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

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

}
