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
 * * FileName : PaymentHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.payment.feerefundheader;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.feerefund.FeeRefundDetailService;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.feerefund.FeeRefundUtil;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.CustomerPaymentTxnsListCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.payment.paymentheader.PaymentInstructionDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.autorefund.RefundBeneficiary;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/payment/PaymentHeader/paymentHeaderDialog.zul file. <br>
 */
public class FeeRefundHeaderDialogCtrl extends GFCBaseCtrl<FeeRefundHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeeRefundHeaderDialogCtrl.class);
	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeeRefundHeaderDialog;
	protected Borderlayout borderlayoutFeeRefundHeader;
	protected Label windowTitle;

	protected Grid grid_Basicdetails;
	protected Tabs tabsIndexCenter;
	protected Tab tabDisbInstructions;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab payTypeInstructions;
	protected Listbox listBoxFeeRefundTypeInstructions;
	protected Tabpanel tabDisbInstructionsTabPanel;
	protected Decimalbox paymentAmount = null;
	protected Decimalbox totAmount = null;

	protected Label lbl_LoanReference;
	protected Label lbl_LoanType;
	protected Label lbl_CustCIF;
	protected Label lbl_Currency;
	protected Label lbl_startDate;
	protected Label lbl_MaturityDate;
	protected Label lbl_ODAgainstLoan;
	protected Label lbl_ODAgainstCustomer;

	protected Textbox tranModule;
	protected Textbox tranReference;
	protected Textbox tranBatch;
	protected Longbox paymentId;
	protected Textbox statusCode;
	protected Textbox statusDesc;
	protected Combobox tranStatus;
	protected Row row_tranStatus;
	protected Groupbox gb_TransactionDetails;

	protected Listheader listheader_PaymentHeaderDialog_AvailableAmount;
	protected Listheader listheader_PaymentHeaderDialog_Balance;

	private FeeRefundHeader feeRefundHeader;
	private FinanceMain fm;

	private transient FeeRefundHeaderListCtrl feeRefundHeaderListCtrl;
	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient FeeRefundHeaderService feeRefundHeaderService;
	private transient FinFeeDetailService finFeeDetailService;
	private transient FinODDetailsDAO finODDetailsDAO;
	private transient ReceiptCalculator receiptCalculator;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private transient PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl;

	private int ccyFormatter = 0;
	private List<FeeRefundDetail> feeRefundDetailList = new ArrayList<>();
	protected String selectMethodName = "onSelectTab";
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private boolean isAccountingExecuted = false;

	private Grid grid_basicDetails;
	private Map<String, BigDecimal> taxPercMap = null;
	private FeeTypeService feeTypeService;
	private RefundBeneficiary refundBeneficiary;
	private FeeRefundDetailService feeRefundDetailService;

	/**
	 * default constructor.<br>
	 */
	public FeeRefundHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeRefundHeaderDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.feeRefundHeader.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FeeRefundHeaderDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FeeRefundHeaderDialog);
		try {
			// Get the required arguments.
			this.feeRefundHeader = (FeeRefundHeader) arguments.get("feeRefundHeader");
			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}
			this.feeRefundHeaderListCtrl = (FeeRefundHeaderListCtrl) arguments.get("feeRefundHeaderListCtrl");
			this.fm = (FinanceMain) arguments.get("financeMain");

			if (arguments.containsKey("customerPaymentTxnsListCtrl")) {
				customerPaymentTxnsListCtrl = (CustomerPaymentTxnsListCtrl) arguments
						.get("customerPaymentTxnsListCtrl");
			}

			if (this.feeRefundHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			FeeRefundHeader frh = new FeeRefundHeader();
			BeanUtils.copyProperties(this.feeRefundHeader, frh);
			this.feeRefundHeader.setBefImage(frh);
			// Render the page and display the data.
			doLoadWorkFlow(this.feeRefundHeader.isWorkflow(), this.feeRefundHeader.getWorkflowId(),
					this.feeRefundHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}
			if (enqiryModule) {
				listBoxFeeRefundTypeInstructions.setHeight("350px");
				this.borderlayoutFeeRefundHeader.setHeight(getBorderLayoutHeight());
				this.windowTitle.setValue(Labels.getLabel("window_FeeRefundHeaderDialog_Approver.title"));
			}

			ccyFormatter = CurrencyUtil.getFormat(this.fm.getFinCcy());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.feeRefundHeader);
			this.listBoxFeeRefundTypeInstructions
					.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount() + 3));

		} catch (Exception e) {
			closeDialog();
			if (disbursementInstructionsDialogCtrl != null) {
				disbursementInstructionsDialogCtrl.closeDialog();
			}
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FeeRefundHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeeRefundHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FeeRefundHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FeeRefundHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());

		if (disbursementInstructionsDialogCtrl != null) {
			disbursementInstructionsDialogCtrl.closeDialog();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.feeRefundHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		if (feeRefundHeaderListCtrl != null) {
			feeRefundHeaderListCtrl.search();
		}
		if (customerPaymentTxnsListCtrl != null) {
			customerPaymentTxnsListCtrl.search();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.feeRefundHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param paymentHeader
	 * 
	 */
	public void doWriteBeanToComponents(FeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);

		this.lbl_LoanReference.setValue(this.fm.getFinReference());
		this.lbl_LoanType.setValue(this.fm.getFinType() + "-" + this.fm.getLovDescFinTypeName());
		this.lbl_CustCIF.setValue(this.fm.getLovDescCustCIF() + "-" + this.fm.getLovDescCustShrtName());
		this.lbl_Currency.setValue(this.fm.getFinCcy() + "- " + CurrencyUtil.getCcyDesc(this.fm.getFinCcy()));
		this.lbl_startDate.setValue(DateUtil.format(this.fm.getFinStartDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate.setValue(DateUtil.format(this.fm.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_ODAgainstLoan.setValue(CurrencyUtil.format(frh.getOverDueAgainstLoan()));
		this.lbl_ODAgainstCustomer.setValue(CurrencyUtil.format(frh.getOverDueAgainstCustomer()));

		// Disbursement Instructions tab.
		appendDisbursementInstructionTab(frh);

		// Fill PaymentType Instructions.
		calculatePaymentDetail(frh);

		this.recordStatus.setValue(frh.getRecordStatus());

		// Accounting Details Tab Addition
		if (getWorkFlow() != null && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(frh, true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSelectTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doClearMessage();

		if (StringUtils.equals(module, AssetConstants.UNIQUE_ID_ACCOUNTING)) {
			doWriteComponentsToBean(feeRefundHeader);
			appendAccountingDetailTab(this.feeRefundHeader, false);
		}
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private void appendDisbursementInstructionTab(FeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);

		FeeRefundInstruction fri = frh.getFeeRefundInstruction();

		PaymentInstruction payIns = null;
		if (fri == null) {
			Date appDate = SysParamUtil.getAppDate();
			payIns = refundBeneficiary.getBeneficiary(this.fm.getFinID(), appDate, true);

			if (payIns == null) {
				payIns = new PaymentInstruction();
			}
		} else {
			payIns = FeeRefundUtil.getPI(fri);
		}

		PaymentHeader ph = new PaymentHeader();

		ph.setWorkflowId(frh.getWorkflowId());
		ph.setNextTaskId(frh.getNextTaskId());
		ph.setNextRoleCode(frh.getNextRoleCode());
		ph.setNewRecord(frh.isNewRecord());

		Map<String, Object> map = new HashMap<>();
		map.put("paymentInstruction", payIns);
		map.put("roleCode", getRole());
		map.put("paymentHeader", ph);
		map.put("feeRefundHeaderDialogCtrl", this);
		map.put("financeMain", this.fm);
		map.put("tab", this.tabDisbInstructions);
		map.put("ccyFormatter", ccyFormatter);
		map.put("enqiryModule", this.enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Payment/PaymentInstructionDialog.zul",
					tabDisbInstructionsTabPanel, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void appendAccountingDetailTab(FeeRefundHeader frh, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		FeeRefundInstruction fri = frh.getFeeRefundInstruction();
		if (fri == null) {
			fri = new FeeRefundInstruction();
		}

		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}

		Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING);
		if (tabpanel != null) {
			tabpanel.setHeight(getListBoxHeight(7));

		}
		if (!onLoadProcess) {
			long accountsetId = AccountingConfigCache.getAccountSetID(this.fm.getFinType(), AccountingEvent.PAYMTINS,
					FinanceConstants.MODULEID_FINTYPE);
			final Map<String, Object> map = new HashMap<>();
			map.put("feeRefundInstruction", fri);
			map.put("acSetID", accountsetId);
			map.put("enqModule", enqiryModule);
			map.put("dialogCtrl", this);
			map.put("isNotFinanceProcess", true);
			map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);

		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.debug(Literal.LEAVING);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		for (Listitem item : listBoxFeeRefundTypeInstructions.getItems()) {
			if (!"Y".equals(item.getAttribute("index"))) {
				continue;
			}

			List<Listcell> listCells = item.getChildren();

			Decimalbox paidAmount = (Decimalbox) listCells.get(4).getChildren().get(0);
			Decimalbox prvRefAmount = (Decimalbox) listCells.get(5).getChildren().get(0);
			Decimalbox payAmt = (Decimalbox) listCells.get(6).getChildren().get(0);

			BigDecimal balanceAmt = paidAmount.getValue().subtract(prvRefAmount.getValue());
			BigDecimal curRefundAmt = payAmt.getValue();

			Clients.clearWrongValue(payAmt);

			if ((balanceAmt.compareTo(curRefundAmt)) == -1) {
				throw new WrongValueException(payAmt,
						Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
			}
		}

		if (this.totAmount != null) {
			this.totAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PaymentHeaderDialog_totalpaymentAmount.value"), ccyFormatter, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		if (this.totAmount != null) {
			this.totAmount.setConstraint("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPaymentHeaderd
	 */
	public void doWriteComponentsToBean(FeeRefundHeader frh) {
		logger.debug(Literal.LEAVING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			frh.setFinID(this.fm.getFinID());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Reference
		try {
			frh.setFinReference(this.fm.getFinReference());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			frh.setPaymentAmount(PennantApplicationUtil.unFormateAmount(this.totAmount.getValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (frh.isNewRecord()) {
				frh.setStatus(RepayConstants.PAYMENT_INTIATED);
				frh.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			this.payTypeInstructions.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (disbursementInstructionsDialogCtrl != null) {
			frh.setFeeRefundInstruction(FeeRefundUtil.getFRI(frh, disbursementInstructionsDialogCtrl.onSave()));
		}

		saveFeeRefundDetails(frh);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param paymentHeader The entity that need to be render.
	 */
	public void doShowDialog(FeeRefundHeader feeRefundHeader) {
		logger.debug(Literal.LEAVING);

		if (feeRefundHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(feeRefundHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(feeRefundHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FeeRefundHeader frh = new FeeRefundHeader();
		BeanUtils.copyProperties(this.feeRefundHeader, frh);

		doDelete(String.valueOf(frh.getId()), frh);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.feeRefundHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.feeRefundHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final FeeRefundHeader frh = new FeeRefundHeader();
		BeanUtils.copyProperties(this.feeRefundHeader, frh);
		boolean isNew = false;

		doSetValidation();

		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			boolean validate = false;
			validate = validateAccounting(validate);
			// Accounting Details Validations
			if (validate) {
				if (!isAccountingExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (!this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") && accountingDetailDialogCtrl
						.getDisbCrSum().compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
		}

		// #Bug Fix 153031
		if (totAmount == null) {
			MessageUtil.showError(Labels.getLabel("label_PaymentHeaderDialog_NoPayInstruction.value"));
			return;
		}

		doWriteComponentsToBean(frh);

		isNew = frh.isNewRecord();

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(frh.getRecordType())) {
				frh.setVersion(frh.getVersion() + 1);
				if (isNew) {
					frh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					frh.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					frh.setNewRecord(true);
				}
			}
		} else {
			frh.setVersion(frh.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(frh, tranType)) {
				refreshList();
				String msg = PennantApplicationUtil.getSavingStatus(frh.getRoleCode(), frh.getNextRoleCode(),
						frh.getFinReference(), " Fee Refund Instructions ", frh.getRecordStatus(),
						frh.getNextRoleCode());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
				if (disbursementInstructionsDialogCtrl != null) {
					disbursementInstructionsDialogCtrl.closeDialog();
				}

				// User Notifications Message/Alert
				if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					publishNotification(Notify.ROLE, frh.getFinReference(), frh);
				} else {
					publishNotification(Notify.ROLE, frh.getFinReference(), frh, fm.getFinPurpose(), fm.getFinBranch());
				}

			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		} catch (final InterfaceException e) {
			logger.error(e);
			MessageUtil.showError(e);
		} catch (final ConcurrencyException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean validateAccounting(boolean validate) {
		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
				|| this.userAction.getSelectedItem().getLabel().contains("Reject")
				|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			validate = false;
		} else {
			validate = true;
		}
		return validate;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FeeRefundHeader frh, String tranType) throws InterfaceException {
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
			frh.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if (PennantConstants.RCD_STATUS_APPROVED.equals(frh.getRecordStatus())) {
				frh.setStatus(RepayConstants.PAYMENT_APPROVE);
				frh.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			}

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(frh.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, frh);
				}

				if (isNotesMandatory(taskId, frh)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
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

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FeeRefundHeader frh = (FeeRefundHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = feeRefundHeaderService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = feeRefundHeaderService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = feeRefundHeaderService.doApprove(auditHeader);
					if (frh.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = feeRefundHeaderService.doReject(auditHeader);
					if (frh.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FeeRefundHeaderDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_FeeRefundHeaderDialog, auditHeader);

			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.feeRefundHeader), true);
				}
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				frh.setOverride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FeeRefundHeader frh, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, frh.getBefImage(), frh);
		return new AuditHeader(getReference(), null, null, null, auditDetail, frh.getUserDetails(), getOverideMap());
	}

	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();

		postingsPreparationUtil.getAccounting(aeEvent);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(aeEvent.getReturnDataSet());
			isAccountingExecuted = true;
		}

		List<FeeRefundDetail> feeRefundDetailsList = this.feeRefundHeader.getFeeRefundDetailList();
		List<Long> feeTypeCodes = new ArrayList<>();
		List<FeeType> feeTypesList = new ArrayList<>();

		for (FeeRefundDetail frd : feeRefundDetailsList) {
			feeTypeCodes.add(frd.getReceivableFeeTypeID());
		}

		if (feeTypeCodes != null && !feeTypeCodes.isEmpty()) {
			feeTypesList = feeTypeService.getFeeTypeListByIds(feeTypeCodes, "");
			aeEvent.setFeesList(feeTypesList);
		}

		logger.debug(Literal.LEAVING);
	}

	private void calculatePaymentDetail(FeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);

		FeeRefundDetail frd = null;
		long finID = this.fm.getFinID();
		List<FeeRefundDetail> detailList = new ArrayList<>();

		List<ManualAdvise> manualAdviseList = this.feeRefundHeaderService.getManualAdvise(finID);
		List<FinFeeDetail> finFeeDetailList = finFeeDetailService.getFinFeeDetailByFinRef(finID, false, "");
		FinODDetails fod = finODDetailsDAO.getFinODSummary(finID);

		for (ManualAdvise ma : manualAdviseList) {
			frd = new FeeRefundDetail();

			FeeType pFeeType = feeTypeService.getPayableFeeType(ma.getFeeTypeCode());

			if (pFeeType == null || !pFeeType.isRefundable()) {
				continue;
			}

			BigDecimal receiptPaidAmt = feeRefundDetailService.getCanelReceiptAmt(finID, Allocation.MANADV,
					String.valueOf(ma.getAdviseID()));

			frd.setNewRecord(true);
			frd.setReceivableID(ma.getAdviseID());

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(ma.getTaxComponent())) {
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finID);
				TaxAmountSplit taxAmountSplit = GSTCalculator.getExclusiveGST(ma.getAdviseAmount(), taxPercentages);
				BigDecimal totWaivedGSTAmount = CalculationUtil.getTotalWaivedGST(ma);
				frd.setAdviseAmount(ma.getAdviseAmount().subtract(ma.getWaivedAmount()).subtract(totWaivedGSTAmount)
						.add(taxAmountSplit.gettGST()));
			} else {
				frd.setAdviseAmount(ma.getAdviseAmount().subtract(ma.getWaivedAmount()));
			}

			frd.setAvailableAmount(ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));
			frd.setPayableFeeTypeID(pFeeType.getFeeTypeID());
			frd.setPayableFeeTypeCode(pFeeType.getRecvFeeTypeCode());
			frd.setPayableFeeTypeDesc(pFeeType.getRecvFeeTypeDesc());

			frd.setReceivableFeeTypeID(ma.getFeeTypeID());
			frd.setReceivableFeeTypeCode(ma.getFeeTypeCode());
			frd.setReceivableFeeTypeDesc(ma.getFeeTypeDesc());

			frd.setReceivableType(Allocation.MANADV);

			BigDecimal totPaidGSTAmount = CalculationUtil.getTotalPaidGST(ma);
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(ma.getTaxComponent())) {
				frd.setPaidAmount(ma.getPaidAmount().subtract(receiptPaidAmt).add(totPaidGSTAmount));
			} else {
				frd.setPaidAmount(ma.getPaidAmount().subtract(receiptPaidAmt));
			}
			frd.setPrevRefundAmount(feeRefundDetailService.getPreviousRefundAmt(finID, frd));

			detailList.add(frd);
		}

		for (FinFeeDetail fee : finFeeDetailList) {
			frd = new FeeRefundDetail();

			FeeType rFeeType = feeTypeService.getApprovedFeeTypeById(fee.getFeeTypeID());

			FeeType pFeeType = feeTypeService.getPayableFeeType(rFeeType.getFeeTypeCode());

			if (pFeeType == null || !pFeeType.isRefundable()) {
				continue;
			}

			BigDecimal receiptPaidAmt = feeRefundDetailService.getCanelReceiptAmt(finID, Allocation.FEE,
					String.valueOf(-1 * fee.getFeeTypeID()));

			frd.setNewRecord(true);
			frd.setReceivableID(fee.getFeeID());
			frd.setAvailableAmount(fee.getActualAmount().subtract(fee.getPaidAmount()).subtract(fee.getWaivedAmount()));
			frd.setAdviseAmount(fee.getActualAmount());
			frd.setPaidAmount(fee.getPaidAmount().subtract(receiptPaidAmt));

			frd.setPayableFeeTypeID(pFeeType.getFeeTypeID());
			frd.setPayableFeeTypeCode(pFeeType.getRecvFeeTypeCode());
			frd.setPayableFeeTypeDesc(pFeeType.getRecvFeeTypeDesc());

			frd.setReceivableFeeTypeID(rFeeType.getFeeTypeID());
			frd.setReceivableFeeTypeCode(rFeeType.getFeeTypeCode());
			frd.setReceivableFeeTypeDesc(rFeeType.getFeeTypeDesc());

			frd.setReceivableType(Allocation.FEE);

			frd.setPrevRefundAmount(feeRefundDetailService.getPreviousRefundAmt(finID, frd));

			detailList.add(frd);
		}

		if (fod != null) {
			FeeType rFeeType = feeTypeService.getApprovedFeeTypeByFeeCode("ODC");
			FeeType pFeeType = feeTypeService.getPayableFeeType(rFeeType.getFeeTypeCode());

			if (pFeeType != null && pFeeType.isRefundable()) {

				BigDecimal receiptPaidAmt = feeRefundDetailService.getCanelReceiptAmt(finID, Allocation.ODC,
						Allocation.ODC);

				frd = new FeeRefundDetail();
				frd.setNewRecord(true);
				frd.setReceivableID(rFeeType.getFeeTypeID());
				frd.setAvailableAmount(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
				frd.setPaidAmount(fod.getTotPenaltyPaid().subtract(receiptPaidAmt));
				frd.setAdviseAmount(fod.getTotPenaltyAmt());

				frd.setPayableFeeTypeID(pFeeType.getFeeTypeID());
				frd.setPayableFeeTypeCode(pFeeType.getRecvFeeTypeCode());
				frd.setPayableFeeTypeDesc(pFeeType.getRecvFeeTypeDesc());

				frd.setReceivableFeeTypeID(rFeeType.getFeeTypeID());
				frd.setReceivableFeeTypeCode(rFeeType.getFeeTypeCode());
				frd.setReceivableFeeTypeDesc(rFeeType.getFeeTypeDesc());

				frd.setReceivableType(Allocation.ODC);

				frd.setPrevRefundAmount(feeRefundDetailService.getPreviousRefundAmt(finID, frd));

				detailList.add(frd);
			}
		}

		if (fod != null) {
			FeeType rFeeType = feeTypeService.getApprovedFeeTypeByFeeCode("LPFT");
			FeeType pFeeType = feeTypeService.getPayableFeeType(rFeeType.getFeeTypeCode());

			if (pFeeType != null && pFeeType.isRefundable()) {

				BigDecimal receiptPaidAmt = feeRefundDetailService.getCanelReceiptAmt(finID, Allocation.LPFT,
						Allocation.LPFT);

				frd = new FeeRefundDetail();
				frd.setNewRecord(true);
				frd.setReceivableID(rFeeType.getFeeTypeID());
				frd.setAvailableAmount(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));
				frd.setReceivableFeeTypeID(rFeeType.getFeeTypeID());
				frd.setPaidAmount(fod.getLPIPaid().subtract(receiptPaidAmt));
				frd.setAdviseAmount(fod.getLPIAmt());

				frd.setPayableFeeTypeID(pFeeType.getFeeTypeID());
				frd.setPayableFeeTypeCode(pFeeType.getRecvFeeTypeCode());
				frd.setPayableFeeTypeDesc(pFeeType.getRecvFeeTypeDesc());

				frd.setReceivableID(rFeeType.getFeeTypeID());
				frd.setReceivableFeeTypeCode(rFeeType.getFeeTypeCode());
				frd.setReceivableFeeTypeDesc(rFeeType.getFeeTypeDesc());

				frd.setReceivableType(Allocation.LPFT);

				frd.setPrevRefundAmount(feeRefundDetailService.getPreviousRefundAmt(finID, frd));

				detailList.add(frd);
			}
		}

		if (frh.isNewRecord()) {
			for (FeeRefundDetail detail : detailList) {
				feeRefundDetailList.add(detail);
			}
		} else {
			updatePaybleAmounts(detailList, frh.getFeeRefundDetailList());
		}

		doFillHeaderList(feeRefundDetailList);

		logger.debug(Literal.LEAVING);
	}

	private void updatePaybleAmounts(List<FeeRefundDetail> newList, List<FeeRefundDetail> oldList) {
		logger.debug(Literal.ENTERING);

		List<FeeRefundDetail> tempList = new ArrayList<>();
		tempList.addAll(newList);

		for (FeeRefundDetail oldDetail : oldList) {
			for (FeeRefundDetail newDetail : newList) {
				if (Long.compare(oldDetail.getReceivableID(), newDetail.getReceivableID()) == 0) {
					oldDetail.setAvailableAmount(newDetail.getAvailableAmount());
					oldDetail.setAdviseAmount(newDetail.getAdviseAmount());
					oldDetail.setNewRecord(false);
					oldDetail.setPaidAmount(newDetail.getPaidAmount());
					oldDetail.setPayableFeeTypeCode(newDetail.getPayableFeeTypeCode());
					oldDetail.setPayableFeeTypeDesc(newDetail.getPayableFeeTypeDesc());
					oldDetail.setReceivableFeeTypeCode(newDetail.getReceivableFeeTypeCode());
					oldDetail.setReceivableFeeTypeDesc(newDetail.getReceivableFeeTypeDesc());
					oldDetail.setReceivableType(newDetail.getReceivableType());
					oldDetail.setPrevRefundAmount(newDetail.getPrevRefundAmount());

					feeRefundDetailList.add(oldDetail);

					tempList.remove(newDetail);
				}
			}
		}

		for (FeeRefundDetail newDetail : tempList) {
			feeRefundDetailList.add(newDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onPayAmountChange(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox paymentAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmt);
		Clients.clearWrongValue(this.totAmount);
		BigDecimal amt1 = BigDecimal.ZERO;
		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmt.getValue(), ccyFormatter);

		String receivableType = (String) paymentAmt.getAttribute("ReceivableType");

		BigDecimal avaAmount = BigDecimal.ZERO;
		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (!receivableType.equals(detail.getReceivableType())) {
				continue;
			}

			avaAmount = detail.getPaidAmount().add(avaAmount);
		}

		if ((amount.compareTo(BigDecimal.ZERO)) < 0) {
			throw new WrongValueException(paymentAmt,
					Labels.getLabel("label_PaymentHeaderDialog_payAmountErrorMsg.value"));
		}

		if ((amount.compareTo(avaAmount)) > 0) {
			amount = avaAmount;
			paymentAmt.setValue(PennantApplicationUtil.formateAmount(avaAmount, ccyFormatter));
		}

		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (!receivableType.equals(detail.getReceivableType())) {
				continue;
			}

			BigDecimal balAmount = detail.getPaidAmount().subtract(detail.getPrevRefundAmount());

			if (balAmount.compareTo(amount) >= 0) {
				detail.setRefundAmount(amount);
				amount = BigDecimal.ZERO;
			} else if (balAmount.compareTo(amount) == -1) {
				amt1 = amount;
				amt1 = amt1.subtract(balAmount);
				if (amt1.compareTo(balAmount) <= 1) {
					detail.setRefundAmount(balAmount);
				} else {
					detail.setRefundAmount(BigDecimal.ZERO);
				}
				amount = amt1;
			}
		}

		doFillHeaderList(feeRefundDetailList);

		logger.debug("Leaving");
	}

	private void saveFeeRefundDetails(FeeRefundHeader frh) {
		logger.debug("Entering");

		List<FeeRefundDetail> list = new ArrayList<FeeRefundDetail>();
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		long userId = loggedInUser.getUserId();

		if (frh.isNewRecord()) {
			for (FeeRefundDetail detail : feeRefundDetailList) {
				if (detail.getRefundAmount() != null && (BigDecimal.ZERO.compareTo(detail.getRefundAmount()) == 0)) {
					continue;
				}
				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);

				detail.setLastMntBy(userId);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(loggedInUser);
				list.add(detail);
			}
		} else {
			for (FeeRefundDetail detail : feeRefundDetailList) {
				detail.setLastMntBy(userId);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(loggedInUser);
				if (detail.isNewRecord()) {
					if (detail.getTotalAmount() != null && (BigDecimal.ZERO.compareTo(detail.getTotalAmount()) == 0)) {
						continue;
					}
					detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setNewRecord(true);
				} else {
					if (detail.getTotalAmount() != null && (BigDecimal.ZERO.compareTo(detail.getTotalAmount()) == 0)) {
						detail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						detail.setNewRecord(false);
					} else {
						if (!enqiryModule) {
							detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
							detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							detail.setNewRecord(false);
						}
					}
				}
				list.add(detail);
			}
		}
		frh.setFeeRefundDetailList(list);
		logger.debug("Leaving");
	}

	private void addFooter(BigDecimal totalPayAmt) {
		Listitem item = new Listitem();
		Listcell lc = new Listcell("");
		lc.setParent(item);

		/* Receivable Type */
		lc = new Listcell("Total");
		lc.setParent(item);

		/* Payable Type */
		lc = new Listcell("");
		lc.setParent(item);

		/* Total Amount */
		lc = new Listcell("");
		lc.setParent(item);

		/* Paid Amount */
		lc = new Listcell("");
		lc.setParent(item);

		/* Previous Refund Amount */
		lc = new Listcell("");
		lc.setParent(item);

		/* Current Refund Amount */
		lc = new Listcell();
		totAmount = new Decimalbox();
		totAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		totAmount.setStyle("text-align:right;");
		totAmount.setReadonly(true);
		totAmount.setValue(PennantApplicationUtil.formateAmount(totalPayAmt, ccyFormatter));
		lc.appendChild(totAmount);
		lc.setParent(item);

		// Balance Amount
		lc = new Listcell("");
		lc.setParent(item);

		if (disbursementInstructionsDialogCtrl != null) {
			totalPayAmt = PennantApplicationUtil.formateAmount(totalPayAmt, ccyFormatter);
			disbursementInstructionsDialogCtrl.paymentAmount.setValue(totalPayAmt);
		}

		this.listBoxFeeRefundTypeInstructions.appendChild(item);
	}

	public void doFillHeaderList(List<FeeRefundDetail> frdList) {
		this.listBoxFeeRefundTypeInstructions.getItems().clear();

		if (CollectionUtils.isEmpty(frdList)) {
			return;
		}

		Map<String, List<FeeRefundDetail>> map = new HashMap<>();

		for (FeeRefundDetail pd : frdList) {
			String receivableType = pd.getReceivableType();

			List<FeeRefundDetail> list = map.get(receivableType);

			if (list == null) {
				list = new ArrayList<>();
				map.put(receivableType, list);
			}

			list.add(pd);
		}

		BigDecimal totalPayAmt = BigDecimal.ZERO;

		for (Entry<String, List<FeeRefundDetail>> paymentDetail : map.entrySet()) {
			totalPayAmt = totalPayAmt.add(doFillHeaderList(paymentDetail.getKey(), paymentDetail.getValue()));
		}

		addFooter(totalPayAmt);

	}

	public BigDecimal doFillHeaderList(String receivableType, List<FeeRefundDetail> pdList) {
		logger.debug(Literal.ENTERING);

		boolean isReadOnly = isReadOnly("FeeRefundHeaderDialog_currRefundAmount");

		BigDecimal totalPayAmt = BigDecimal.ZERO;

		BigDecimal adviseAmount = BigDecimal.ZERO;
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal prevRefundAmount = BigDecimal.ZERO;
		BigDecimal currRefundAmount = BigDecimal.ZERO;

		FeeRefundDetail temp = null;
		for (FeeRefundDetail pd : pdList) {

			if (temp == null) {
				temp = pd;
			}

			adviseAmount = adviseAmount.add(pd.getAdviseAmount());
			paidAmount = paidAmount.add(pd.getPaidAmount());
			prevRefundAmount = prevRefundAmount.add(pd.getPrevRefundAmount());
			currRefundAmount = currRefundAmount.add(pd.getRefundAmount());

			totalPayAmt = currRefundAmount;

		}

		Button button = new Button();
		if (temp.isExpand()) {
			button.setImage("/images/icons/delete.png");
			button.setStyle("background:white;border:0px;");
			button.addForward("onClick", self, "onExpand");
		} else {
			button.setImage("/images/icons/add.png");
			button.setStyle("background:#FFFFFF;border:0px;onMouseOver ");
			button.addForward("onClick", self, "onCollapse");
		}

		button.setAttribute("pd", temp);

		Listitem item = new Listitem();
		item.setAttribute("index", "Y");

		Listcell lc = new Listcell();
		lc.appendChild(button);
		lc.setParent(item);

		/* Receivable Type */
		lc = new Listcell(receivableType);
		lc.setParent(item);

		/* Payable Type */
		lc = new Listcell("");
		lc.setParent(item);

		/* Total Amount */
		lc = new Listcell();
		lc.appendChild(getDecimalbox(adviseAmount, true));
		lc.setParent(item);

		/* Paid Amount */
		lc = new Listcell();
		lc.appendChild(getDecimalbox(paidAmount, true));
		lc.setParent(item);

		/* Previous Refund Amount */
		lc = new Listcell();
		lc.appendChild(getDecimalbox(prevRefundAmount, true));
		lc.setParent(item);

		/* Current Refund Amount */
		lc = new Listcell();
		Decimalbox paymentAmount = getDecimalbox(currRefundAmount, isReadOnly);
		paymentAmount.setAttribute("ReceivableType", receivableType);
		paymentAmount.addForward("onChange", self, "onPayAmountChange");
		lc.appendChild(paymentAmount);
		lc.setParent(item);

		// Balance Amount
		BigDecimal balanceAmount = paidAmount.subtract(currRefundAmount).subtract(prevRefundAmount);
		lc = new Listcell();
		lc.appendChild(getDecimalbox(balanceAmount, true));
		lc.setParent(item);

		this.listBoxFeeRefundTypeInstructions.appendChild(item);

		if (temp.isExpand() && !temp.isCollapse()) {
			doFillChildDetail(pdList);
		}

		logger.debug("Leaving");

		return totalPayAmt;
	}

	private void doFillChildDetail(List<FeeRefundDetail> paymentDetail) {
		logger.debug("Entering");

		boolean isReadOnly = isReadOnly("FeeRefundHeaderDialog_currRefundAmount");

		BigDecimal totalPayAmt = BigDecimal.ZERO;

		for (FeeRefundDetail pd : paymentDetail) {
			Listitem item = new Listitem();

			Listcell lc = new Listcell();
			lc.setParent(item);

			BigDecimal aviseAmount = pd.getAdviseAmount();
			BigDecimal paidAmount = pd.getPaidAmount();
			BigDecimal prevRefundAmount = pd.getPrevRefundAmount();
			BigDecimal currRefundAmount = pd.getRefundAmount();

			/* Receivable Type */
			lc = new Listcell(pd.getReceivableFeeTypeCode() + "-" + pd.getReceivableFeeTypeDesc());
			lc.setParent(item);

			/* Payable Type */
			lc = new Listcell(pd.getPayableFeeTypeCode() + "-" + pd.getPayableFeeTypeDesc());
			lc.setParent(item);

			/* Total Amount */
			lc = new Listcell();
			lc.appendChild(getDecimalbox(aviseAmount, true));
			lc.setParent(item);

			/* Paid Amount */
			lc = new Listcell();
			lc.appendChild(getDecimalbox(paidAmount, true));
			lc.setParent(item);

			/* Previous Refund Amount */
			lc = new Listcell();
			lc.appendChild(getDecimalbox(prevRefundAmount, true));
			lc.setParent(item);

			/* Current Refund Amount */
			lc = new Listcell();
			Decimalbox paymentAmount = getDecimalbox(currRefundAmount, isReadOnly);
			paymentAmount.addForward("onChange", self, "onPayAmountForEventChanges");
			paymentAmount.setAttribute("object", pd);
			paymentAmount.setConstraint("NO NEGATIVE");
			lc.appendChild(paymentAmount);
			lc.setParent(item);

			// Balance Amount
			BigDecimal balanceAmount = paidAmount.subtract(currRefundAmount).subtract(prevRefundAmount);
			lc = new Listcell();
			lc.appendChild(getDecimalbox(balanceAmount, true));
			lc.setParent(item);

			this.listBoxFeeRefundTypeInstructions.appendChild(item);
		}

		if (disbursementInstructionsDialogCtrl != null) {
			disbursementInstructionsDialogCtrl.paymentAmount.setValue(totalPayAmt);
		}

		logger.debug("Leaving");
	}

	private Decimalbox getDecimalbox(BigDecimal amount, boolean isReadOnly) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		decimalbox.setStyle("text-align:right; ");
		decimalbox.setReadonly(isReadOnly);
		decimalbox.setValue(PennantApplicationUtil.formateAmount(amount, ccyFormatter));

		return decimalbox;
	}

	public void onExpand(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		FeeRefundDetail pd = (FeeRefundDetail) button.getAttribute("pd");

		pd.setExpand(false);
		pd.setCollapse(true);

		doFillHeaderList(feeRefundDetailList);
	}

	public void onCollapse(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		FeeRefundDetail pd = (FeeRefundDetail) button.getAttribute("pd");

		pd.setExpand(true);
		pd.setCollapse(false);

		doFillHeaderList(feeRefundDetailList);
	}

	public void onPayAmountForEventChanges(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Decimalbox paymentAmount = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmount);
		Clients.clearWrongValue(this.totAmount);

		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmount.getValue(), ccyFormatter);

		if (BigDecimal.ZERO.compareTo(amount) == 1) {
			amount = BigDecimal.ZERO;
		}

		FeeRefundDetail frd = (FeeRefundDetail) paymentAmount.getAttribute("object");

		BigDecimal avaAmount = BigDecimal.ZERO;
		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (detail.getReceivableID() == null) {
				continue;
			}

			if (Long.compare(frd.getReceivableID(), detail.getReceivableID()) == 0) {

				avaAmount = detail.getPaidAmount().subtract(detail.getPrevRefundAmount());

				if ((amount.compareTo(avaAmount)) > 0) {
					amount = avaAmount;
					paymentAmount.setValue(PennantApplicationUtil.formateAmount(avaAmount, ccyFormatter));
				}

				detail.setRefundAmount(amount);
			}
		}

		doFillHeaderList(feeRefundDetailList);

		logger.debug(Literal.LEAVING);
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public Map<String, BigDecimal> getTaxPercMap() {
		return taxPercMap;
	}

	public void setTaxPercMap(Map<String, BigDecimal> taxPercMap) {
		this.taxPercMap = taxPercMap;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public void setFeeRefundHeaderListCtrl(FeeRefundHeaderListCtrl feeRefundHeaderListCtrl) {
		this.feeRefundHeaderListCtrl = feeRefundHeaderListCtrl;
	}

	public void setFeeRefundHeaderService(FeeRefundHeaderService feeRefundHeaderService) {
		this.feeRefundHeaderService = feeRefundHeaderService;
	}

	public void setDisbursementInstructionsDialogCtrl(PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl) {
		this.disbursementInstructionsDialogCtrl = disbursementInstructionsDialogCtrl;
	}

	public void setFeeRefundDetailList(List<FeeRefundDetail> feeRefundDetailList) {
		this.feeRefundDetailList = feeRefundDetailList;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

	public void setFeeRefundDetailService(FeeRefundDetailService feeRefundDetailService) {
		this.feeRefundDetailService = feeRefundDetailService;
	}

}
