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
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
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
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.feerefund.FeeRefundHeaderService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.applicationmaster.customerPaymentTransactions.CustomerPaymentTxnsListCtrl;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
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
	private FinanceMain financeMain;

	private transient FeeRefundHeaderListCtrl feeRefundHeaderListCtrl;
	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient FeeRefundHeaderService feeRefundHeaderService;
	private transient FinFeeDetailService finFeeDetailService;
	private transient FinODDetailsDAO finODDetailsDAO;
	private transient ReceiptCalculator receiptCalculator;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private transient FeeRefundInstructionDialogCtrl feeRefundInstructionDialogCtrl;

	private int ccyFormatter = 0;
	private List<FeeRefundDetail> feeRefundDetailList = new ArrayList<FeeRefundDetail>();
	protected String selectMethodName = "onSelectTab";
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private boolean isAccountingExecuted = false;
	private long accountsetId;

	private Listheader listheader_FeeRefundHeaderDialog_button;
	private Grid grid_basicDetails;
	private Map<String, BigDecimal> taxPercMap = null;
	private FeeTypeService feeTypeService;
	private ReceiptService receiptService;
	private ManualAdviseService manualAdviseService;
	private RefundBeneficiary refundBeneficiary;

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
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.feeRefundHeader.getFeeRefundId()));
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
			this.financeMain = (FinanceMain) arguments.get("financeMain");

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
			}
			ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.feeRefundHeader);
			this.listBoxFeeRefundTypeInstructions
					.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount() + 3));

		} catch (Exception e) {
			closeDialog();
			if (feeRefundInstructionDialogCtrl != null) {
				feeRefundInstructionDialogCtrl.closeDialog();
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

		if (feeRefundInstructionDialogCtrl != null) {
			feeRefundInstructionDialogCtrl.closeDialog();
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

		this.lbl_LoanReference.setValue(this.financeMain.getFinReference());
		this.lbl_LoanType.setValue(this.financeMain.getFinType() + "-" + this.financeMain.getLovDescFinTypeName());
		this.lbl_CustCIF
				.setValue(this.financeMain.getLovDescCustCIF() + "-" + this.financeMain.getLovDescCustShrtName());
		this.lbl_Currency
				.setValue(this.financeMain.getFinCcy() + "- " + CurrencyUtil.getCcyDesc(this.financeMain.getFinCcy()));
		this.lbl_startDate
				.setValue(DateUtility.format(this.financeMain.getFinStartDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate
				.setValue(DateUtility.format(this.financeMain.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_ODAgainstLoan.setValue(PennantApplicationUtil.amountFormate(frh.getOdAgainstLoan(), ccyFormatter));
		this.lbl_ODAgainstCustomer
				.setValue(PennantApplicationUtil.amountFormate(frh.getOdAgainstCustomer(), ccyFormatter));

		// Disbursement Instructions tab.
		appendDisbursementInstructionTab(frh);

		// Fill PaymentType Instructions.
		if (this.enqiryModule) {
			calculatePaymentDetail(frh);
			setFeeRefundDetailList(frh.getFeeRefundDetailList());
		} else {
			calculatePaymentDetail(frh);
		}

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

	/**
	 * Method for Append Disbursement Instruction Tab
	 */
	private void appendDisbursementInstructionTab(FeeRefundHeader frh) {
		try {
			FeeRefundInstruction fri = frh.getFeeRefundInstruction();
			Date appDate = SysParamUtil.getAppDate();
			boolean alwRefundByCheque = SysParamUtil.isAllowed(SMTParameterConstants.AUTO_REFUND_THROUGH_CHEQUE);
			if (fri == null) {
				PaymentInstruction payIns = null;
				fri = new FeeRefundInstruction();
				payIns = refundBeneficiary.fetchBeneficiaryForRefund(this.financeMain.getFinID(), appDate,
						alwRefundByCheque);
				if (payIns != null) {
					fri.setBankBranchId(payIns.getBankBranchId());
					fri.setBankBranchCode(payIns.getBankBranchCode());
					fri.setBranchDesc(payIns.getBranchDesc());
					fri.setBankName(payIns.getBankName());
					fri.setBankBranchIFSC(payIns.getBankBranchIFSC());
					fri.setpCCityName(payIns.getpCCityName());
					fri.setAccountNo(payIns.getAccountNo());
					fri.setAcctHolderName(payIns.getAcctHolderName());
					fri.setPartnerBankId(payIns.getPartnerBankId());
					fri.setPartnerBankCode(payIns.getPartnerBankCode());
					fri.setPartnerBankName(payIns.getPartnerBankName());
					fri.setPhoneNumber(payIns.getPhoneNumber());
					fri.setIssuingBank(payIns.getIssuingBank());
					fri.setIssuingBankName(payIns.getIssuingBankName());
					fri.setPartnerBankAcType(payIns.getPartnerBankAc());
					fri.setPartnerBankAc(payIns.getPartnerBankAc());
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("feeRefundInstruction", fri);
			map.put("roleCode", getRole());
			map.put("feeRefundHeader", frh);
			map.put("feeRefundHeaderDialogCtrl", this);
			map.put("financeMain", this.financeMain);
			map.put("tab", this.tabDisbInstructions);
			map.put("ccyFormatter", ccyFormatter);
			map.put("enqiryModule", this.enqiryModule);

			Executions.createComponents("/WEB-INF/pages/FeeRefund/FeeRefundInstructionDialog.zul",
					tabDisbInstructionsTabPanel, map);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(FeeRefundHeader frh, boolean onLoadProcess) {
		logger.debug("Entering");

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
			accountsetId = AccountingConfigCache.getAccountSetID(this.financeMain.getFinType(),
					AccountingEvent.PAYMTINS, FinanceConstants.MODULEID_FINTYPE);
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
		logger.debug("Leaving");
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

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
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
		logger.debug("Leaving");
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
		logger.debug("Entering ");

		if (this.listBoxFeeRefundTypeInstructions != null
				&& this.listBoxFeeRefundTypeInstructions.getItems().size() > 0) {

			for (int i = 0; i < listBoxFeeRefundTypeInstructions.getItems().size() - 1; i++) {
				List<Listcell> listCells = listBoxFeeRefundTypeInstructions.getItems().get(i).getChildren();
				Listcell avaibleAmtCell = listCells.get(7);
				Listcell payAmtCell = listCells.get(6);
				Decimalbox avaibleAmt = (Decimalbox) avaibleAmtCell.getChildren().get(0);
				Decimalbox payAmt = (Decimalbox) payAmtCell.getChildren().get(0);
				Clients.clearWrongValue(payAmt);
				if ((avaibleAmt.getValue().compareTo(payAmt.getValue())) == -1) {
					throw new WrongValueException(payAmt,
							Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
				}
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

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Id
		try {
			frh.setFinID(this.financeMain.getFinID());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			frh.setCustCif(this.financeMain.getLovDescCustCIF());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Reference
		try {
			frh.setFinReference(this.financeMain.getFinReference());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			frh.setFinType(this.financeMain.getFinType());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			frh.setBranchName(this.financeMain.getFinBranch());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Type
		try {
			frh.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Amount
		try {
			frh.setPaymentAmount(PennantApplicationUtil.unFormateAmount(this.totAmount.getValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Status
		try {
			if (frh.isNewRecord()) {
				frh.setStatus(RepayConstants.PAYMENT_INTIATED);
				frh.setCreatedOn(SysParamUtil.getAppDate());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			this.payTypeInstructions.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (feeRefundInstructionDialogCtrl != null) {
			FeeRefundInstruction fri = feeRefundInstructionDialogCtrl.onSave();
			frh.setFeeRefundInstruction(fri);
		}
		// Save PaymentDetails
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

		doDelete(String.valueOf(frh.getFeeRefundId()), frh);

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
				if (feeRefundInstructionDialogCtrl != null) {
					feeRefundInstructionDialogCtrl.closeDialog();
				}

				// User Notifications Message/Alert
				if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					publishNotification(Notify.ROLE, frh.getFinReference(), frh);
				} else {
					publishNotification(Notify.ROLE, frh.getFinReference(), frh, financeMain.getFinPurpose(),
							financeMain.getFinBranch());
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
				frh.setApprovedOn(SysParamUtil.getAppDate());
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
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FeeRefundHeader frh, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, frh.getBefImage(), frh);
		return new AuditHeader(getReference(), null, null, null, auditDetail, frh.getUserDetails(), getOverideMap());
	}

	/****************************************************
	 * Account Executing * ***************************************************
	 */

	/**
	 * Method for Executing Accountng Details
	 */
	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();

		// feeRefundHeaderService.executeAccountingProcess(aeEvent, this.feeRefundHeader);
		postingsPreparationUtil.getAccounting(aeEvent);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(aeEvent.getReturnDataSet());
			isAccountingExecuted = true;
		}

		List<FeeRefundDetail> feeRefundDetailsList = this.feeRefundHeader.getFeeRefundDetailList();
		List<String> feeTypeCodes = new ArrayList<>();
		List<FeeType> feeTypesList = new ArrayList<>();

		for (FeeRefundDetail frd : feeRefundDetailsList) {
			feeTypeCodes.add(frd.getFeeTypeCode());
		}

		if (feeTypeCodes != null && !feeTypeCodes.isEmpty()) {
			feeTypesList = feeTypeService.getFeeTypeListByCodes(feeTypeCodes, "");
			aeEvent.setFeesList(feeTypesList);
		}

		logger.debug(Literal.LEAVING);
	}

	/***************************************************
	 * Payment details Saving, Processing...............* **************************************************
	 */
	private void calculatePaymentDetail(FeeRefundHeader frh) {
		logger.debug(Literal.ENTERING);

		FeeRefundDetail frd = null;
		long finID = this.financeMain.getFinID();
		List<FeeRefundDetail> detailList = new ArrayList<FeeRefundDetail>();

		List<ManualAdvise> manualAdviseList = this.feeRefundHeaderService.getManualAdvise(finID);
		List<FinFeeDetail> finFeeDetailList = finFeeDetailService.getFinFeeDetailByFinRef(finID, false, "");
		FinODDetails fod = finODDetailsDAO.getFinODSummary(finID);

		if (CollectionUtils.isNotEmpty(manualAdviseList)) {
			for (ManualAdvise ma : manualAdviseList) {
				frd = new FeeRefundDetail();
				if (!(ma.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
					continue;
				}

				FeeType feeType = feeTypeService.getRecvFees(ma.getFeeTypeCode());

				if (feeType == null || !feeType.isRefundable()) {
					continue;
				}

				List<ReceiptAllocationDetail> radList = this.receiptService
						.getReceiptAllocDetail(this.financeMain.getFinID(), Allocation.MANADV);

				BigDecimal receiptPaidAmt = BigDecimal.ZERO;

				if (CollectionUtils.isNotEmpty(radList)) {
					for (ReceiptAllocationDetail rad : radList) {
						if (StringUtils.equals(String.valueOf(rad.getAllocationTo()),
								String.valueOf(ma.getAdviseID()))) {
							receiptPaidAmt = rad.getPaidAmount();
						}
					}
				}

				frd.setNewRecord(true);
				frd.setReceivableRefId(ma.getAdviseID());
				frd.setAvailableAmount(
						ma.getAdviseAmount().subtract(ma.getPaidAmount()).subtract(ma.getWaivedAmount()));

				if (ma.getBounceID() > 0) {
					frd.setReceivableType(RepayConstants.DUETYPE_BOUNCE + "_" + ma.getFeeTypeID());
				} else {
					frd.setReceivableType(RepayConstants.DUETYPE_MANUALADVISE + "_" + ma.getFeeTypeID());
				}
				frd.setFeeTypeCode(ma.getFeeTypeCode());
				frd.setFeeTypeDesc(ma.getFeeTypeDesc());
				frd.setAdviseAmount(ma.getAdviseAmount());
				BigDecimal paidAmount = ma.getPaidAmount().subtract(receiptPaidAmt);
				BigDecimal paidTGST = ma.getPaidCGST().add(ma.getPaidSGST()).add(ma.getPaidIGST()).add(ma.getPaidUGST())
						.add(ma.getPaidCESS());
				frd.setPaidAmount(paidAmount.add(paidTGST));

				BigDecimal prvRefundAmt = frd.getPaidAmount().subtract(setEligibleAmount(feeType));

				frd.setPrevRefundAmount(prvRefundAmt);

				if (feeType != null) {
					frd.setPayableFeeTypeCode(feeType.getRecvFeeTypeCode());
					frd.setPayableFeeTypeDesc(feeType.getRecvFeeTypeDesc());
				}

				BigDecimal waivedTGST = ma.getWaivedCGST().add(ma.getWaivedSGST()).add(ma.getWaivedIGST())
						.add(ma.getWaivedUGST()).add(ma.getWaivedCESS());

				frd.setPrvGST(paidTGST.add(waivedTGST));
				frd.setManualAdvise(ma);
				// GST Field details
				frd.setTaxApplicable(ma.isTaxApplicable());
				frd.setTaxComponent(ma.getTaxComponent());

				detailList.add(frd);
			}
		}

		if (CollectionUtils.isNotEmpty(finFeeDetailList)) {
			for (FinFeeDetail ffd : finFeeDetailList) {
				frd = new FeeRefundDetail();
				if (!(ffd.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
					continue;
				}

				FeeType ft = feeTypeService.getApprovedFeeTypeById(ffd.getFeeTypeID());
				FeeType feeType = feeTypeService.getRecvFees(ft.getFeeTypeCode());
				if (feeType == null || !feeType.isRefundable()) {
					continue;
				}

				List<ReceiptAllocationDetail> radList = this.receiptService.getReceiptAllocDetail(finID,
						Allocation.FEE);

				BigDecimal receiptPaidAmt = BigDecimal.ZERO;

				if (CollectionUtils.isNotEmpty(radList)) {
					for (ReceiptAllocationDetail rad : radList) {
						if (StringUtils.equals(String.valueOf(rad.getAllocationTo()),
								String.valueOf(ffd.getFeeTypeID()))) {
							receiptPaidAmt = rad.getPaidAmount();
						}
					}
				}

				frd.setNewRecord(true);
				frd.setReceivableRefId(ffd.getFeeID());
				frd.setAvailableAmount(
						ffd.getActualAmount().subtract(ffd.getPaidAmount()).subtract(ffd.getWaivedAmount()));
				frd.setReceivableType(RepayConstants.DUETYPE_FEES + "_" + ffd.getFinEvent() + "_" + ffd.getFeeTypeID());
				frd.setFeeTypeCode(ft.getFeeTypeCode());
				frd.setFeeTypeDesc(ft.getFeeTypeDesc());
				frd.setAdviseAmount(ffd.getActualAmount());
				frd.setPaidAmount(ffd.getPaidAmount().subtract(receiptPaidAmt));

				BigDecimal prvRefundAmt = frd.getPaidAmount().subtract(setEligibleAmount(feeType));
				frd.setPrevRefundAmount(prvRefundAmt);
				frd.setPayableFeeTypeCode(feeType.getRecvFeeTypeCode());
				frd.setPayableFeeTypeDesc(feeType.getRecvFeeTypeDesc());
				frd.setTaxApplicable(ffd.isTaxApplicable());
				frd.setTaxComponent(ffd.getTaxComponent());

				detailList.add(frd);
			}
		}

		if (fod != null && fod.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) > 0) {
			FeeType ft = feeTypeService.getApprovedFeeTypeByFeeCode("ODC");
			FeeType feeType = feeTypeService.getRecvFees(ft.getFeeTypeCode());

			if (feeType != null && feeType.isRefundable()) {

				List<ReceiptAllocationDetail> radList = this.receiptService.getReceiptAllocDetail(finID,
						Allocation.ODC);

				BigDecimal receiptPaidAmt = BigDecimal.ZERO;

				if (CollectionUtils.isNotEmpty(radList)) {
					for (ReceiptAllocationDetail rad : radList) {
						if (StringUtils.equals(String.valueOf(rad.getAllocationType()), Allocation.ODC)) {
							receiptPaidAmt = rad.getPaidAmount();
						}
					}
				}

				frd = new FeeRefundDetail();
				frd.setNewRecord(true);
				frd.setReceivableRefId(ft.getFeeTypeID());
				frd.setAvailableAmount(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
				frd.setPaidAmount(fod.getTotPenaltyPaid().subtract(receiptPaidAmt));
				frd.setReceivableType(RepayConstants.DUETYPE_ODC);

				frd.setFeeTypeCode(ft.getFeeTypeCode());
				frd.setFeeTypeDesc(ft.getFeeTypeDesc());
				frd.setAdviseAmount(fod.getTotPenaltyAmt());

				BigDecimal prvRefundAmt = frd.getPaidAmount().subtract(setEligibleAmount(feeType));
				frd.setPrevRefundAmount(prvRefundAmt);
				frd.setPayableFeeTypeCode(feeType.getRecvFeeTypeCode());
				frd.setPayableFeeTypeDesc(feeType.getRecvFeeTypeDesc());
				frd.setTaxApplicable(ft.isTaxApplicable());
				frd.setTaxComponent(ft.getTaxComponent());

				detailList.add(frd);
			}
		}

		if (fod != null && fod.getLPIPaid().compareTo(BigDecimal.ZERO) > 0) {
			FeeType ft = feeTypeService.getApprovedFeeTypeByFeeCode("LPFT");
			FeeType feeType = feeTypeService.getRecvFees(ft.getFeeTypeCode());

			if (feeType != null && feeType.isRefundable()) {

				List<ReceiptAllocationDetail> radList = this.receiptService.getReceiptAllocDetail(finID,
						Allocation.LPFT);

				BigDecimal receiptPaidAmt = BigDecimal.ZERO;

				if (CollectionUtils.isNotEmpty(radList)) {
					for (ReceiptAllocationDetail rad : radList) {
						if (StringUtils.equals(String.valueOf(rad.getAllocationType()), Allocation.LPFT)) {
							receiptPaidAmt = rad.getPaidAmount();
						}
					}
				}

				frd = new FeeRefundDetail();
				frd.setNewRecord(true);
				frd.setReceivableRefId(ft.getFeeTypeID());
				frd.setAvailableAmount(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));
				frd.setPaidAmount(fod.getLPIPaid().subtract(receiptPaidAmt));
				frd.setReceivableType(RepayConstants.DUETYPE_LPFT);

				frd.setFeeTypeCode(ft.getFeeTypeCode());
				frd.setFeeTypeDesc(ft.getFeeTypeDesc());
				frd.setAdviseAmount(fod.getLPIAmt());

				BigDecimal prvRefundAmt = frd.getPaidAmount().subtract(setEligibleAmount(feeType));
				frd.setPrevRefundAmount(prvRefundAmt);
				frd.setPayableFeeTypeCode(feeType.getRecvFeeTypeCode());
				frd.setPayableFeeTypeDesc(feeType.getRecvFeeTypeDesc());
				frd.setTaxApplicable(ft.isTaxApplicable());
				frd.setTaxComponent(ft.getTaxComponent());

				detailList.add(frd);
			}
		}

		if (frh.isNewRecord()) {
			for (FeeRefundDetail detail : detailList) {
				if (BigDecimal.ZERO.compareTo(detail.getPaidAmount()) == -1) {
					feeRefundDetailList.add(detail);
				}
			}
		} else {
			updatePaybleAmounts(detailList, frh.getFeeRefundDetailList());
		}

		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (!AdviseType.isPayable(detail.getReceivableType())) {
				continue;
			}

			if (detail.isTaxApplicable()) {

				if (taxPercMap == null) {
					FinanceDetail financeDetail = new FinanceDetail();
					financeDetail.getFinScheduleData().setFinanceMain(financeMain);
					taxPercMap = GSTCalculator.getTaxPercentages(financeMain);
				}

				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				Taxes cgstTax = null;
				Taxes sgstTax = null;
				Taxes igstTax = null;
				Taxes ugstTax = null;
				Taxes cessTax = null;
				if (taxHeader == null) {
					taxHeader = new TaxHeader();
					taxHeader.setNewRecord(true);
					taxHeader.setRecordType(PennantConstants.RCD_ADD);
					taxHeader.setVersion(taxHeader.getVersion() + 1);
					detail.setTaxHeader(taxHeader);
				}
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {

						switch (taxes.getTaxType()) {
						case RuleConstants.CODE_CGST:
							cgstTax = taxes;
							break;
						case RuleConstants.CODE_IGST:
							igstTax = taxes;
							break;
						case RuleConstants.CODE_SGST:
							sgstTax = taxes;
							break;
						case RuleConstants.CODE_UGST:
							ugstTax = taxes;
							break;
						case RuleConstants.CODE_CESS:
							cessTax = taxes;
							break;
						default:
							break;
						}
					}
				}

				BigDecimal cGSTPerc = taxPercMap.get(RuleConstants.CODE_CGST);
				BigDecimal sGSTPerc = taxPercMap.get(RuleConstants.CODE_SGST);
				BigDecimal iGSTPerc = taxPercMap.get(RuleConstants.CODE_IGST);
				BigDecimal uGSTPerc = taxPercMap.get(RuleConstants.CODE_UGST);
				BigDecimal cessPerc = taxPercMap.get(RuleConstants.CODE_CESS);

				if (taxHeader.getTaxDetails() == null) {
					taxHeader.setTaxDetails(new ArrayList<>());
				}

				// CGST
				if (cgstTax == null) {
					cgstTax = getTaxDetail(RuleConstants.CODE_CGST, cGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(cgstTax);
				} else {
					cgstTax.setTaxPerc(cGSTPerc);
				}

				// SGST
				if (sgstTax == null) {
					sgstTax = getTaxDetail(RuleConstants.CODE_SGST, sGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(sgstTax);
				} else {
					sgstTax.setTaxPerc(sGSTPerc);
				}

				// IGST
				if (igstTax == null) {
					igstTax = getTaxDetail(RuleConstants.CODE_IGST, iGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(igstTax);
				} else {
					igstTax.setTaxPerc(iGSTPerc);
				}

				// UGST
				if (ugstTax == null) {
					ugstTax = getTaxDetail(RuleConstants.CODE_UGST, uGSTPerc, taxHeader);
					taxHeader.getTaxDetails().add(ugstTax);
				} else {
					ugstTax.setTaxPerc(uGSTPerc);
				}

				// CESS percentage
				if (cessTax == null) {
					cessTax = getTaxDetail(RuleConstants.CODE_CESS, cessPerc, taxHeader);
					taxHeader.getTaxDetails().add(cessTax);
				} else {
					cessTax.setTaxPerc(cessPerc);
				}

				TaxAmountSplit taxSplit = null;

				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
					taxSplit = GSTCalculator.getExclusiveGST(detail.getAvailableAmount(), taxPercMap);
				} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(detail.getTaxComponent())) {
					taxSplit = GSTCalculator.getInclusiveGST(detail.getAvailableAmount(), taxPercMap);
				}

				getActualGST(detail, taxSplit);

				if (taxSplit != null) {
					cgstTax.setActualTax(taxSplit.getcGST());
					sgstTax.setActualTax(taxSplit.getsGST());
					igstTax.setActualTax(taxSplit.getiGST());
					ugstTax.setActualTax(taxSplit.getuGST());
					cessTax.setActualTax(taxSplit.getCess());

					cgstTax.setNetTax(taxSplit.getcGST());
					sgstTax.setNetTax(taxSplit.getsGST());
					igstTax.setNetTax(taxSplit.getiGST());
					ugstTax.setNetTax(taxSplit.getuGST());
					cessTax.setNetTax(taxSplit.getCess());
				}

			}
		}

		doFillHeaderList(feeRefundDetailList, null, false);
		logger.debug(Literal.LEAVING);
	}

	private BigDecimal setEligibleAmount(FeeType ft) {
		String linkTo = ft.getPayableLinkTo();

		BigDecimal amount = BigDecimal.ZERO;
		if (Allocation.ADHOC.equals(linkTo)) {
			amount = BigDecimal.ZERO;
		} else if (isValidPayableLink(linkTo, ft.getAdviseType())) {
			ManualAdvise ma = new ManualAdvise();

			ma.setFinReference(this.financeMain.getFinReference());
			ma.setValueDate(SysParamUtil.getAppDate());

			amount = manualAdviseService.getEligibleAmount(ma, ft);
		}
		return amount;
	}

	private boolean isValidPayableLink(String linkTo, int adviseType) {
		if (AdviseType.isReceivable(adviseType) || linkTo == null) {
			return false;
		}

		switch (linkTo) {
		case Allocation.PFT:
		case Allocation.PRI:
		case Allocation.MANADV:
		case Allocation.BOUNCE:
		case Allocation.ODC:
		case Allocation.LPFT:
			return true;
		default:
			return false;
		}
	}

	private void getActualGST(FeeRefundDetail frd, TaxAmountSplit taxSplit) {
		if (taxSplit == null) {
			return;
		}

		if (frd.getAdviseAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		ManualAdvise ma = frd.getManualAdvise();

		if (ma == null) {
			return;
		}

		TaxAmountSplit adviseSplit = null;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(frd.getTaxComponent())) {
			adviseSplit = GSTCalculator.getExclusiveGST(frd.getAdviseAmount(), taxPercMap);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(frd.getTaxComponent())) {
			adviseSplit = GSTCalculator.getInclusiveGST(frd.getAdviseAmount(), taxPercMap);
		}

		BigDecimal diffGST = BigDecimal.ZERO;

		BigDecimal payableGST = taxSplit.gettGST().add(frd.getPrvGST());

		if (payableGST.compareTo(adviseSplit.gettGST()) > 0) {
			diffGST = payableGST.subtract(adviseSplit.gettGST());
			taxSplit.settGST(taxSplit.gettGST().subtract(diffGST));
		}

		if (diffGST.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		BigDecimal prvCGst = ma.getPaidCGST().add(ma.getWaivedCGST());
		BigDecimal prvSGst = ma.getPaidSGST().add(ma.getWaivedSGST());
		BigDecimal prvIGst = ma.getPaidIGST().add(ma.getWaivedIGST());
		BigDecimal prvUGst = ma.getPaidUGST().add(ma.getWaivedUGST());
		BigDecimal prvCess = ma.getPaidCESS().add(ma.getWaivedCESS());

		BigDecimal diffCGST = taxSplit.getcGST().add(prvCGst).subtract(adviseSplit.getcGST());
		BigDecimal diffSGST = taxSplit.getsGST().add(prvSGst).subtract(adviseSplit.getsGST());
		BigDecimal diffIGST = taxSplit.getiGST().add(prvIGst).subtract(adviseSplit.getiGST());
		BigDecimal diffUGST = taxSplit.getuGST().add(prvUGst).subtract(adviseSplit.getuGST());
		BigDecimal diffCESS = taxSplit.getCess().add(prvCess).subtract(adviseSplit.getCess());

		taxSplit.setcGST(taxSplit.getcGST().subtract(diffCGST));
		taxSplit.setsGST(taxSplit.getsGST().subtract(diffSGST));
		taxSplit.setiGST(taxSplit.getiGST().subtract(diffIGST));
		taxSplit.setuGST(taxSplit.getuGST().subtract(diffUGST));
		taxSplit.setCess(taxSplit.getCess().subtract(diffCESS));
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, TaxHeader taxHeader) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	// Update the latest balance amount..
	private void updatePaybleAmounts(List<FeeRefundDetail> newList, List<FeeRefundDetail> oldList) {
		logger.debug(Literal.ENTERING);

		List<FeeRefundDetail> tempList = new ArrayList<>();
		tempList.addAll(newList);

		for (FeeRefundDetail oldDetail : oldList) {
			for (FeeRefundDetail newDetail : newList) {
				if (oldDetail.getReceivableRefId() == newDetail.getReceivableRefId()) {

					BigDecimal amount = oldDetail.getAvailableAmount();
					if (oldDetail.getTaxHeader() != null
							&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(oldDetail.getTaxComponent())) {
						// GST Calculations
						TaxHeader taxHeader = oldDetail.getTaxHeader();
						List<Taxes> taxDetails = taxHeader.getTaxDetails();
						BigDecimal gstAmount = BigDecimal.ZERO;
						if (CollectionUtils.isNotEmpty(taxDetails)) {
							for (Taxes taxes : taxDetails) {
								gstAmount = gstAmount.add(taxes.getPaidTax());
							}
						}
						amount = amount.subtract(gstAmount);
					}

					String amountType = oldDetail.getReceivableType();
					if (RepayConstants.EXAMOUNTTYPE_EXCESS.equals(amountType)
							|| RepayConstants.EXAMOUNTTYPE_EMIINADV.equals(amountType)
							|| RepayConstants.EXAMOUNTTYPE_ADVINT.equals(amountType)
							|| RepayConstants.EXAMOUNTTYPE_CASHCLT.equals(amountType)
							|| RepayConstants.EXAMOUNTTYPE_DSF.equals(amountType)) {

						oldDetail.setAvailableAmount(amount.add(newDetail.getAvailableAmount()));
					} else {
						oldDetail.setAvailableAmount(newDetail.getAvailableAmount());
					}

					oldDetail.setAdviseAmount(newDetail.getAdviseAmount());
					oldDetail.setManualAdvise(newDetail.getManualAdvise());
					oldDetail.setPrvGST(newDetail.getPrvGST());
					oldDetail.setNewRecord(false);
					oldDetail.setFeeTypeCode(newDetail.getFeeTypeCode());
					oldDetail.setFeeTypeDesc(newDetail.getFeeTypeDesc());
					oldDetail.setTaxApplicable(newDetail.isTaxApplicable());
					oldDetail.setTaxComponent(newDetail.getTaxComponent());
					feeRefundDetailList.add(oldDetail);
					tempList.remove(newDetail);
				}
			}
		}
		for (FeeRefundDetail newDetail : tempList) {
			if (BigDecimal.ZERO.compareTo(newDetail.getAvailableAmount()) == -1) {
				feeRefundDetailList.add(newDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for action Forward event for changing PayAmountChange
	 * 
	 * @param event
	 */
	public void onPayAmountChange(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox paymentAmt = (Decimalbox) event.getOrigin().getTarget();
		String recType = (String) event.getData();
		Clients.clearWrongValue(paymentAmt);
		Clients.clearWrongValue(this.totAmount);
		BigDecimal amt1 = BigDecimal.ZERO;
		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmt.getValue(), ccyFormatter);

		if (BigDecimal.ZERO.compareTo(amount) == 1) {
			amount = BigDecimal.ZERO;
		}

		BigDecimal avaAmount = BigDecimal.ZERO;
		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (!StringUtils.equals(recType, detail.getReceivableType())) {
				continue;
			}
			avaAmount = detail.getPaidAmount().add(avaAmount);
			if (detail.getTaxHeader() != null
					&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				BigDecimal gstAmount = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						gstAmount = gstAmount.add(taxes.getNetTax());
					}
				}
				avaAmount = avaAmount.add(gstAmount);
			}
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
			if (!StringUtils.equals(recType, detail.getReceivableType())) {
				continue;
			}
			BigDecimal balAmount = detail.getPaidAmount().subtract(detail.getPrevRefundAmount());
			if (detail.getTaxHeader() != null
					&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {
				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				List<Taxes> taxDetails = taxHeader.getTaxDetails();
				BigDecimal gstAmount = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						gstAmount = gstAmount.add(taxes.getNetTax());
					}
				}
				balAmount = balAmount.add(gstAmount);
			}

			if (balAmount.compareTo(amount) >= 0) {
				detail.setCurrRefundAmount(amount);
				amount = BigDecimal.ZERO;
			} else if (balAmount.compareTo(amount) == -1) {
				amt1 = amount;
				amt1 = amt1.subtract(balAmount);
				if (amt1.compareTo(balAmount) <= 1) {
					detail.setCurrRefundAmount(balAmount);
				} else {
					detail.setCurrRefundAmount(BigDecimal.ZERO);
				}
				amount = amt1;
			}
		}
		doFillHeaderList(feeRefundDetailList, null, false);

		logger.debug("Leaving");
	}

	private void saveFeeRefundDetails(FeeRefundHeader frh) {
		logger.debug("Entering");

		List<FeeRefundDetail> list = new ArrayList<FeeRefundDetail>();
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		long userId = loggedInUser.getUserId();

		if (frh.isNewRecord()) {
			for (FeeRefundDetail detail : feeRefundDetailList) {
				if (detail.getCurrRefundAmount() != null
						&& (BigDecimal.ZERO.compareTo(detail.getCurrRefundAmount()) == 0)) {
					continue;
				}
				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);

				detail.setLastMntBy(userId);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(loggedInUser);
				detail = calTaxDetail(detail);
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
				detail = calTaxDetail(detail);
				list.add(detail);
			}
		}
		frh.setFeeRefundDetailList(list);
		logger.debug("Leaving");
	}

	/**
	 * Method for Reset or calculate GST amounts based on amounts adjusted
	 * 
	 * @param detail
	 * @return
	 */
	private FeeRefundDetail calTaxDetail(FeeRefundDetail detail) {

		if (!AdviseType.isPayable(detail.getReceivableType())
				|| detail.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
			detail.setTaxHeader(null);
			return detail;
		}

		TaxHeader taxHeader = detail.getTaxHeader();
		if (detail.isTaxApplicable() && taxHeader != null) {

			if (taxPercMap == null) {
				taxPercMap = GSTCalculator.getTaxPercentages(financeMain);
			}

			// GST Calculations
			Taxes cgstTax = null;
			Taxes sgstTax = null;
			Taxes igstTax = null;
			Taxes ugstTax = null;
			Taxes cessTax = null;
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					switch (taxes.getTaxType()) {
					case RuleConstants.CODE_CGST:
						cgstTax = taxes;
						break;
					case RuleConstants.CODE_SGST:
						sgstTax = taxes;
						break;
					case RuleConstants.CODE_IGST:
						igstTax = taxes;
						break;
					case RuleConstants.CODE_UGST:
						ugstTax = taxes;
						break;
					case RuleConstants.CODE_CESS:
						cessTax = taxes;
						break;
					default:
						break;
					}
				}
			}

			TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(detail.getTotalAmount(), taxPercMap);
			getActualGST(detail, taxSplit);
			cgstTax.setPaidTax(taxSplit.getcGST());
			sgstTax.setPaidTax(taxSplit.getsGST());
			igstTax.setPaidTax(taxSplit.getiGST());
			ugstTax.setPaidTax(taxSplit.getuGST());
			cessTax.setPaidTax(taxSplit.getCess());

		}

		return detail;
	}

	// Filling paymeny details list...
	public void doFillHeaderList(List<FeeRefundDetail> frdList, String rcvType, boolean expandClick) {
		logger.debug("Entering");

		this.listBoxFeeRefundTypeInstructions.getItems().clear();
		this.listheader_FeeRefundHeaderDialog_button.setVisible(false);

		// Total Avaliable Amount for ManualAdvise
		if (CollectionUtils.isNotEmpty(frdList)) {

			Map<String, List<FeeRefundDetail>> refmap = new HashMap<String, List<FeeRefundDetail>>();

			List<FeeRefundDetail> refList = null;
			for (FeeRefundDetail frd : frdList) {
				if (refmap.containsKey(frd.getReceivableType())) {
					refList = refmap.get(frd.getReceivableType());
					refList.add(frd);
					refmap.remove(frd.getReceivableType());
				} else {
					refList = new ArrayList<FeeRefundDetail>();
					refList.add(frd);
				}
				refmap.put(frd.getReceivableType(), refList);
			}

			BigDecimal totalPayAmt = BigDecimal.ZERO;
			for (String key : refmap.keySet()) {

				List<FeeRefundDetail> frds = refmap.get(key);
				boolean groupReq = false;
				BigDecimal avaAmount = BigDecimal.ZERO;
				BigDecimal dueGST = BigDecimal.ZERO;
				BigDecimal dueGSTExclusive = BigDecimal.ZERO;
				BigDecimal payAmount = BigDecimal.ZERO;
				BigDecimal prvRefundAmt = BigDecimal.ZERO;
				BigDecimal adviseAmt = BigDecimal.ZERO;
				BigDecimal currRefundAmt = BigDecimal.ZERO;
				String rcvFeeType = null;
				String payFeeType = null;
				long refID = 0;

				for (FeeRefundDetail frd : frds) {

					this.listheader_FeeRefundHeaderDialog_button.setVisible(true);

					rcvFeeType = frd.getFeeTypeDesc();
					payFeeType = frd.getPayableFeeTypeDesc();
					avaAmount = frd.getAvailableAmount().add(avaAmount);
					payAmount = frd.getPaidAmount().add(payAmount);
					prvRefundAmt = frd.getPrevRefundAmount().add(prvRefundAmt);
					currRefundAmt = frd.getCurrRefundAmount().add(currRefundAmt);
					adviseAmt = frd.getAdviseAmount().add(adviseAmt);

					// GST Calculations in case of Exclusive Case
					if (StringUtils.equals(frd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {

						TaxHeader taxHeader = frd.getTaxHeader();
						if (taxHeader != null) {
							List<Taxes> taxDetails = taxHeader.getTaxDetails();
							if (taxHeader != null && CollectionUtils.isNotEmpty(taxDetails)) {
								for (Taxes taxes : taxDetails) {
									dueGST = dueGST.add(taxes.getActualTax());
									dueGSTExclusive = dueGSTExclusive.add(taxes.getActualTax());
								}
							}
						}
					}

					if (frds.size() > 1) {
						groupReq = true;
					} else {
						refID = frd.getReceivableRefId();
					}
				}

				FeeRefundDetail frTemp = new FeeRefundDetail();

				frTemp.setFeeTypeDesc(rcvFeeType);
				frTemp.setPayableFeeTypeDesc(payFeeType);
				frTemp.setAdviseAmount(adviseAmt);
				frTemp.setPaidAmount(payAmount);
				frTemp.setPrevRefundAmount(prvRefundAmt);
				frTemp.setCurrRefundAmount(currRefundAmt);
				frTemp.setTotalAmount(totalPayAmt);
				frTemp.setReceivableType(frds.get(0).getReceivableType());
				frTemp.setReceivableRefId(refID);

				// Manual Advise
				if (groupReq) {

					totalPayAmt = addGroupRecord(frTemp, true, expandClick);

					if (expandClick && StringUtils.equals(rcvType, frds.get(0).getReceivableType())) {
						doFillChildDetail(frds);
					}
				} else {
					totalPayAmt = addGroupRecord(frTemp, false, false);
				}

			}
			addFooter(totalPayAmt);
		}
		logger.debug("Leaving");
	}

	private BigDecimal addGroupRecord(FeeRefundDetail frTemp, boolean expandReq, boolean expandClick) {
		Button button = new Button();
		Listitem item = new Listitem();
		Listcell lc;
		boolean isReadOnly = isReadOnly("FeeRefundHeaderDialog_currRefundAmount");
		BigDecimal totalPayAmt = frTemp.getTotalAmount();

		if (expandReq) {
			if (expandClick) {
				lc = new Listcell();
				button.setImage("/images/icons/delete.png");
				button.setStyle("background:white;border:0px;");
				button.addForward("onClick", self, "onClickCollapse", frTemp.getReceivableType());
				lc.appendChild(button);
				lc.setParent(item);
			} else {
				lc = new Listcell();
				button.setImage("/images/icons/add.png");
				button.setStyle("background:#FFFFFF;border:0px;onMouseOver ");
				button.addForward("onClick", self, "onClickExpand", frTemp.getReceivableType());
				lc.appendChild(button);
				lc.setParent(item);
			}
		} else {
			lc = new Listcell();
			lc.setParent(item);
		}

		// Receivable Type
		lc = new Listcell(
				!expandReq ? frTemp.getReceivableRefId() + "-" + frTemp.getFeeTypeDesc() : frTemp.getFeeTypeDesc());
		lc.setParent(item);

		// Payable Type
		lc = new Listcell(frTemp.getPayableFeeTypeDesc());
		lc.setParent(item);

		// Total Amount
		lc = new Listcell();
		Decimalbox totalAmt = new Decimalbox();
		totalAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		totalAmt.setStyle("text-align:right; ");
		totalAmt.setReadonly(true);
		totalAmt.setValue(PennantApplicationUtil.formateAmount(frTemp.getAdviseAmount(), ccyFormatter));
		lc.appendChild(totalAmt);
		lc.setParent(item);

		// Paid Amount
		lc = new Listcell();
		Decimalbox paidAmt = new Decimalbox();
		paidAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		paidAmt.setStyle("text-align:right; ");
		paidAmt.setReadonly(true);
		paidAmt.setValue(PennantApplicationUtil.formateAmount(frTemp.getPaidAmount(), ccyFormatter));
		lc.appendChild(paidAmt);
		lc.setParent(item);

		// Prev Refund Amount
		lc = new Listcell();
		Decimalbox prevRfdAmt = new Decimalbox();
		prevRfdAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		prevRfdAmt.setStyle("text-align:right; ");
		prevRfdAmt.setReadonly(true);
		prevRfdAmt.setValue(PennantApplicationUtil.formateAmount(frTemp.getPrevRefundAmount(), ccyFormatter));
		lc.appendChild(prevRfdAmt);
		lc.setParent(item);

		lc = new Listcell();
		paymentAmount = new Decimalbox();
		paymentAmount.setReadonly(isReadOnly);
		paymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		paymentAmount.setStyle("text-align:right; ");
		paymentAmount.setValue(PennantApplicationUtil.formateAmount(frTemp.getCurrRefundAmount(), ccyFormatter));
		totalPayAmt = totalPayAmt.add(frTemp.getCurrRefundAmount());
		paymentAmount.addForward("onChange", self, "onPayAmountChange", frTemp.getReceivableType());
		lc.appendChild(paymentAmount);
		lc.setParent(item);

		lc = new Listcell();
		Decimalbox balanceAmount = new Decimalbox();
		balanceAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		balanceAmount.setStyle("text-align:right; ");
		balanceAmount.setReadonly(true);
		balanceAmount.setValue(PennantApplicationUtil.formateAmount(
				frTemp.getPaidAmount().subtract(frTemp.getCurrRefundAmount()).subtract(frTemp.getPrevRefundAmount()),
				ccyFormatter));
		lc.appendChild(balanceAmount);
		lc.setParent(item);
		this.listBoxFeeRefundTypeInstructions.appendChild(item);
		return totalPayAmt;
	}

	private void addFooter(BigDecimal totalPayAmt) {

		// Total Amount
		Listitem item = new Listitem();
		Listcell lc;
		if (enqiryModule) {
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell(" Total Refund Amount ");
			lc.setStyle("font-weight:bold;");

		} else {
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell();
		}
		item.appendChild(lc);
		lc = new Listcell(" Total Refund Amount ");
		lc.setSpan(4);
		lc.setStyle("font-weight:bold;");
		item.appendChild(lc);
		lc = new Listcell();
		totAmount = new Decimalbox();
		totAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		totAmount.setStyle("text-align:right; ");
		totAmount.setReadonly(true);
		totalPayAmt = PennantApplicationUtil.formateAmount(totalPayAmt, ccyFormatter);
		totAmount.setValue(totalPayAmt);
		lc.appendChild(totAmount);
		lc.setParent(item);
		lc = new Listcell();
		lc.setParent(item);
		if (feeRefundInstructionDialogCtrl != null) {
			feeRefundInstructionDialogCtrl.paymentAmount.setValue(totalPayAmt);
		}
		this.listBoxFeeRefundTypeInstructions.appendChild(item);
	}

	/**
	 * Method for action Forward event for changing PayAmountChangeForExcessAndEMI
	 * 
	 * @param event
	 */
	public void onPayAmountChangeForExcessAndEMI(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox paymentAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmt);
		Clients.clearWrongValue(this.totAmount);
		BigDecimal amount = PennantApplicationUtil.unFormateAmount(paymentAmt.getValue(), ccyFormatter);

		if (BigDecimal.ZERO.compareTo(amount) == 1) {
			amount = BigDecimal.ZERO;
		}

		FeeRefundDetail frd = (FeeRefundDetail) paymentAmt.getAttribute("object");
		for (FeeRefundDetail detail : feeRefundDetailList) {
			if (frd.getReceivableRefId() == detail.getReceivableRefId()) {
				if ((amount.compareTo(BigDecimal.ZERO)) < 0) {
					paymentAmt.setValue(BigDecimal.ZERO);
					throw new WrongValueException(paymentAmt,
							Labels.getLabel("label_PaymentHeaderDialog_payAmountErrorMsg.value"));
				}

				if ((detail.getAvailableAmount().compareTo(amount)) == -1) {
					throw new WrongValueException(paymentAmt,
							Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
				} else {
					detail.setTotalAmount(amount);
				}
			}
		}
		doFillHeaderList(feeRefundDetailList, null, false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the Plus button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClickExpand(Event event) {
		logger.debug("Entering " + event.toString());
		String rcvType = (String) event.getData();
		doFillHeaderList(feeRefundDetailList, rcvType, true);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the minus button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClickCollapse(Event event) {
		logger.debug("Entering " + event.toString());
		String rcvType = (String) event.getData();
		doFillHeaderList(feeRefundDetailList, rcvType, false);
		logger.debug("Leaving " + event.toString());
	}

	// Filling paymeny details list for Manual Advise
	public List<Listitem> doFillChildDetail(List<FeeRefundDetail> frdList) {
		logger.debug("Entering");

		boolean isReadOnly = isReadOnly("FeeRefundHeaderDialog_currRefundAmount");
		BigDecimal totalPayAmt = BigDecimal.ZERO;
		List<Listitem> items = new ArrayList<Listitem>();
		Listitem item = null;
		if (frdList != null && !frdList.isEmpty()) {

			for (FeeRefundDetail frd : frdList) {
				item = new Listitem();
				Listcell lc;
				lc = new Listcell();
				lc.setParent(item);

				BigDecimal calGST = BigDecimal.ZERO;
				BigDecimal availAmount = frd.getAvailableAmount();
				BigDecimal paidAmount = frd.getPaidAmount();
				BigDecimal prvRefundAmt = frd.getPrevRefundAmount();
				BigDecimal currRefundAmt = frd.getCurrRefundAmount();
				BigDecimal adviseAmt = frd.getAdviseAmount();
				String desc = frd.getReceivableRefId() + "-" + frd.getFeeTypeDesc();
				String paybleFeeDesc = frd.getPayableFeeTypeDesc();

				// GST Calculations
				TaxHeader taxHeader = frd.getTaxHeader();
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							calGST = calGST.add(taxes.getActualTax());
						}

						if (StringUtils.equals(frd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
							availAmount = availAmount.subtract(calGST);
							desc = desc.concat(" (Inclusive)");
						} else if (StringUtils.equals(frd.getTaxComponent(),
								FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
							desc = desc.concat(" (Exclusive)");
						}
					}
				}

				lc = new Listcell();
				Hbox hbox = new Hbox();
				Space space = new Space();
				space.setSpacing("20px");
				space.setParent(hbox);
				Label label = new Label();
				label.setValue(desc);
				label.setParent(hbox);
				lc.appendChild(hbox);
				lc.setParent(item);

				lc = new Listcell();
				Hbox hbox1 = new Hbox();
				Space space1 = new Space();
				space1.setSpacing("20px");
				space1.setParent(hbox1);
				Label label1 = new Label();
				label1.setValue(paybleFeeDesc);
				label1.setParent(hbox1);
				lc.appendChild(hbox1);
				lc.setParent(item);

				// Total Amount
				lc = new Listcell();
				Decimalbox totalAmt = new Decimalbox();
				totalAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				totalAmt.setStyle("text-align:right; ");
				totalAmt.setReadonly(true);
				totalAmt.setValue(PennantApplicationUtil.formateAmount(adviseAmt, ccyFormatter));
				lc.appendChild(totalAmt);
				lc.setParent(item);

				// Paid Amount
				lc = new Listcell();
				Decimalbox paidAmt = new Decimalbox();
				paidAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				paidAmt.setStyle("text-align:right; ");
				paidAmt.setReadonly(true);
				paidAmt.setValue(PennantApplicationUtil.formateAmount(paidAmount, ccyFormatter));
				lc.appendChild(paidAmt);
				lc.setParent(item);

				// Prev Refund Amount
				lc = new Listcell();
				Decimalbox prevRfdAmt = new Decimalbox();
				prevRfdAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				prevRfdAmt.setStyle("text-align:right; ");
				prevRfdAmt.setReadonly(true);
				prevRfdAmt.setValue(PennantApplicationUtil.formateAmount(prvRefundAmt, ccyFormatter));
				lc.appendChild(prevRfdAmt);
				lc.setParent(item);

				lc = new Listcell();
				paymentAmount = new Decimalbox();
				paymentAmount.setReadonly(isReadOnly);
				paymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				paymentAmount.setStyle("text-align:right; ");
				paymentAmount.setValue(PennantApplicationUtil.formateAmount(currRefundAmt, ccyFormatter));
				paymentAmount.addForward("onChange", self, "onPayAmountForEventChanges");
				paymentAmount.setAttribute("object", frd);
				paymentAmount.setConstraint("NO NEGATIVE");
				lc.appendChild(paymentAmount);
				lc.setParent(item);
				lc = new Listcell();
				Decimalbox balanceAmount = new Decimalbox();
				balanceAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				balanceAmount.setStyle("text-align:right; ");
				balanceAmount.setReadonly(true);
				balanceAmount.setValue(PennantApplicationUtil.formateAmount(
						paidAmount.add(calGST).subtract(currRefundAmt).subtract(prvRefundAmt), ccyFormatter));
				lc.appendChild(balanceAmount);
				lc.setParent(item);
				this.listBoxFeeRefundTypeInstructions.appendChild(item);
			}
			if (feeRefundInstructionDialogCtrl != null) {
				feeRefundInstructionDialogCtrl.paymentAmount.setValue(totalPayAmt);
			}

		}
		logger.debug("Leaving");
		return items;

	}

	/**
	 * Method for action Forward event for changing onPayAmountFoEventChanges
	 * 
	 * @param event
	 */
	public void onPayAmountForEventChanges(ForwardEvent event) {
		logger.debug("Entering");

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
			if (frd.getReceivableRefId() == detail.getReceivableRefId()) {
				avaAmount = detail.getPaidAmount().subtract(detail.getPrevRefundAmount());

				// GST Calculations
				TaxHeader taxHeader = detail.getTaxHeader();
				if (taxHeader != null) {
					List<Taxes> taxDetails = taxHeader.getTaxDetails();
					if (taxHeader != null
							&& StringUtils.equals(detail.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)
							&& CollectionUtils.isNotEmpty(taxDetails)) {
						for (Taxes taxes : taxDetails) {
							avaAmount = avaAmount.add(taxes.getActualTax());
						}
					}
				}

				if ((amount.compareTo(avaAmount)) > 0) {
					amount = avaAmount;
					paymentAmount.setValue(PennantApplicationUtil.formateAmount(avaAmount, ccyFormatter));
				}
				detail.setCurrRefundAmount(amount);
			}
		}

		doFillHeaderList(feeRefundDetailList, null, true);
		logger.debug("Leaving");
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

	public void setFeeRefundInstructionDialogCtrl(FeeRefundInstructionDialogCtrl feeRefundInstructionDialogCtrl) {
		this.feeRefundInstructionDialogCtrl = feeRefundInstructionDialogCtrl;
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

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setRefundBeneficiary(RefundBeneficiary refundBeneficiary) {
		this.refundBeneficiary = refundBeneficiary;
	}

}
