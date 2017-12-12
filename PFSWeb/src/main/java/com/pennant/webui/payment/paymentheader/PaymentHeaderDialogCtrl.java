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
 * FileName    		:  PaymentHeaderDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.payment.paymentheader;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.core.EventManager;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/payment/PaymentHeader/paymentHeaderDialog.zul file. <br>
 */
public class PaymentHeaderDialogCtrl extends GFCBaseCtrl<PaymentHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PaymentHeaderDialogCtrl.class);
	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PaymentHeaderDialog;
	protected Borderlayout borderlayoutPaymentHeader;

	protected Grid grid_Basicdetails;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab tabDisbInstructions;
	protected Tab payTypeInstructions;
	protected Tabpanel tabDisbInstructionsTabPanel;
	protected Listbox listBoxPaymentTypeInstructions;
	protected Decimalbox paymentAmount = null;
	protected Decimalbox totAmount = null;

	protected Label lbl_LoanReference;
	protected Label lbl_LoanType;
	protected Label lbl_CustCIF;
	protected Label lbl_Currency;
	protected Label lbl_startDate;
	protected Label lbl_MaturityDate;
	
	
	protected Listheader listheader_PaymentHeaderDialog_AvailableAmount;
	protected Listheader listheader_PaymentHeaderDialog_Balance;

	private PaymentHeader paymentHeader;
	private FinanceMain financeMain;

	private transient PaymentHeaderListCtrl paymentHeaderListCtrl;
	private transient PaymentHeaderService paymentHeaderService;
	private EventManager eventManager;
	private transient PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl;
	private int ccyFormatter = 0;
	private List<PaymentDetail> paymentDetailList = new ArrayList<PaymentDetail>();
	protected String selectMethodName	= "onSelectTab";
	private AccountingSetService accountingSetService;
	private transient AccountingDetailDialogCtrl	accountingDetailDialogCtrl;
	private AccountEngineExecution	engineExecution;
	private boolean isAccountingExecuted	= false;
	private long accountsetId;
	
	/**
	 * default constructor.<br>
	 */
	public PaymentHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PaymentHeaderDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.paymentHeader.getPaymentId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PaymentHeaderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PaymentHeaderDialog);
		try {
			// Get the required arguments.
			this.paymentHeader = (PaymentHeader) arguments.get("paymentHeader");
			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}
			this.paymentHeaderListCtrl = (PaymentHeaderListCtrl) arguments.get("paymentHeaderListCtrl");
			this.financeMain = (FinanceMain) arguments.get("financeMain");

			if (this.paymentHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			PaymentHeader paymentHeader = new PaymentHeader();
			BeanUtils.copyProperties(this.paymentHeader, paymentHeader);
			this.paymentHeader.setBefImage(paymentHeader);
			// Render the page and display the data.
			doLoadWorkFlow(this.paymentHeader.isWorkflow(), this.paymentHeader.getWorkflowId(), this.paymentHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}
			if(enqiryModule){
				listBoxPaymentTypeInstructions.setHeight("250px");
				this.borderlayoutPaymentHeader.setHeight(getBorderLayoutHeight());
			}
			ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.paymentHeader);

		} catch (Exception e) {
			closeDialog();
			if (getDisbursementInstructionsDialogCtrl() != null) {
				getDisbursementInstructionsDialogCtrl().closeDialog();
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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PaymentHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		if (getDisbursementInstructionsDialogCtrl() != null) {
			getDisbursementInstructionsDialogCtrl().closeDialog();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.paymentHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		paymentHeaderListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.paymentHeader.getBefImage());
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
	public void doWriteBeanToComponents(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		this.lbl_LoanReference.setValue(this.financeMain.getFinReference());
		this.lbl_LoanType.setValue(this.financeMain.getFinType() + "-" + this.financeMain.getLovDescFinTypeName());
		this.lbl_CustCIF.setValue(this.financeMain.getLovDescCustCIF() + "-"
				+ this.financeMain.getLovDescCustShrtName());
		this.lbl_Currency.setValue(this.financeMain.getFinCcy() + "- "
				+ CurrencyUtil.getCcyDesc(this.financeMain.getFinCcy()));
		this.lbl_startDate.setValue(DateUtility.formateDate(this.financeMain.getFinStartDate(),
				DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate.setValue(DateUtility.formateDate(this.financeMain.getMaturityDate(),
				DateFormat.LONG_DATE.getPattern()));

		// Disbursement Instructions tab.
		appendDisbursementInstructionTab(aPaymentHeader);
		
		// Fill PaymentType Instructions.
		if (this.enqiryModule) {
			doFillPaymentDetails(aPaymentHeader.getPaymentDetailList());
		} else {
			doFillPaymentDetails(aPaymentHeader);
		}

		this.recordStatus.setValue(aPaymentHeader.getRecordStatus());
		
		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(aPaymentHeader,true);
		}

		logger.debug(Literal.LEAVING);
	}
	
	
	public void onSelectTab(ForwardEvent event) throws Exception {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doClearMessage();

		if (StringUtils.equals(module, AssetConstants.UNIQUE_ID_ACCOUNTING)) {
			doWriteComponentsToBean(paymentHeader);
			appendAccountingDetailTab(this.paymentHeader, false);
		}
	}
	
	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	/**
	 * Method for Append Disbursement Instruction Tab
	 */
	private void appendDisbursementInstructionTab(PaymentHeader aPaymentHeader) {
		try {
			PaymentInstruction paymentInstruction = aPaymentHeader.getPaymentInstruction();
			if (paymentInstruction == null) {
				paymentInstruction = new PaymentInstruction();
			}
			Map<String, Object> map = new HashMap<>();
			map.put("paymentInstruction", paymentInstruction);
			map.put("roleCode", getRole());
			map.put("paymentHeader", aPaymentHeader);
			map.put("paymentHeaderDialogCtrl", this);
			map.put("financeMain", this.financeMain);
			map.put("tab", this.tabDisbInstructions);
			map.put("ccyFormatter", ccyFormatter);
			map.put("enqiryModule", this.enqiryModule);

			Executions.createComponents("/WEB-INF/pages/Payment/PaymentInstructionDialog.zul",
					tabDisbInstructionsTabPanel, map);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(PaymentHeader aPaymentHeader, boolean onLoadProcess) {
		logger.debug("Entering");

		PaymentInstruction paymentInstruction = aPaymentHeader.getPaymentInstruction();
		if (paymentInstruction == null) {
			paymentInstruction = new PaymentInstruction();
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
		if(tabpanel!=null){
			tabpanel.setHeight(getListBoxHeight(7));
			
		}
		if (!onLoadProcess) {
			accountsetId=accountingSetService.getAccountingSetId(AccountEventConstants.ACCEVENT_PAYMTINS, AccountEventConstants.ACCEVENT_PAYMTINS);
			final HashMap<String, Object> map = new HashMap<>();
				map.put("paymentInstruction", paymentInstruction);
				map.put("acSetID", accountsetId);
				map.put("enqModule", enqiryModule);
				map.put("dialogCtrl", this);
				map.put("isNotFinanceProcess", true);
				map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
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
		
		if (this.listBoxPaymentTypeInstructions != null && this.listBoxPaymentTypeInstructions.getItems().size() > 0) {
			
			for (int i = 0; i < listBoxPaymentTypeInstructions.getItems().size()-1; i++) {
				List<Listcell> listCells = listBoxPaymentTypeInstructions.getItems().get(i).getChildren();
				Listcell avaibleAmtCell = listCells.get(1);
				Listcell payAmtCell = listCells.get(2);
				Decimalbox avaibleAmt = (Decimalbox) avaibleAmtCell.getChildren().get(0);
				Decimalbox payAmt = (Decimalbox) payAmtCell.getChildren().get(0);
				Clients.clearWrongValue(payAmt);
				if ((avaibleAmt.getValue().compareTo(payAmt.getValue())) == -1) {
					throw new WrongValueException(payAmt, Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
				}
			}
		}
		
		if (this.totAmount != null) {
			this.totAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_PaymentHeaderDialog_totalpaymentAmount.value"), ccyFormatter, true));
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
	 * @param aPaymentHeader
	 */
	public void doWriteComponentsToBean(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Reference
		try {
			aPaymentHeader.setFinReference(this.financeMain.getFinReference());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Type
		try {
			aPaymentHeader.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Amount
		try {
			aPaymentHeader.setPaymentAmount(PennantApplicationUtil.unFormateAmount(this.totAmount.getValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Status
		try {
			if (aPaymentHeader.isNewRecord()) {
				aPaymentHeader.setStatus(RepayConstants.PAYMENT_INTIATED);
				aPaymentHeader.setCreatedOn(DateUtility.getAppDate());
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

		if (getDisbursementInstructionsDialogCtrl() != null) {
			PaymentInstruction paymentInstruction = getDisbursementInstructionsDialogCtrl().doSave();
			aPaymentHeader.setPaymentInstruction(paymentInstruction);
		}
		// Save PaymentDetails
		savePaymentDetails(aPaymentHeader);

		logger.debug(Literal.LEAVING);
	}


	/**
	 * Displays the dialog page.
	 * 
	 * @param paymentHeader
	 *            The entity that need to be render.
	 */
	public void doShowDialog(PaymentHeader paymentHeader) {
		logger.debug(Literal.LEAVING);

		if (paymentHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(paymentHeader.getRecordType())) {
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
		
		doWriteBeanToComponents(paymentHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a PaymentHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final PaymentHeader aPaymentHeader = new PaymentHeader();
		BeanUtils.copyProperties(this.paymentHeader, aPaymentHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aPaymentHeader.getPaymentId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aPaymentHeader.getRecordType()).equals("")) {
				aPaymentHeader.setVersion(aPaymentHeader.getVersion() + 1);
				aPaymentHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPaymentHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aPaymentHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aPaymentHeader.getNextTaskId(),
							aPaymentHeader);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aPaymentHeader, tranType)) {
					refreshList();
					closeDialog();
					if (getDisbursementInstructionsDialogCtrl() != null) {
						getDisbursementInstructionsDialogCtrl().closeDialog();
					}
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.paymentHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.paymentHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		this.listheader_PaymentHeaderDialog_AvailableAmount.setVisible(!enqiryModule);
		this.listheader_PaymentHeaderDialog_Balance.setVisible(!enqiryModule);
		
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
		final PaymentHeader aPaymentHeader = new PaymentHeader();
		BeanUtils.copyProperties(this.paymentHeader, aPaymentHeader);
		boolean isNew = false;

		doSetValidation();
		
		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			boolean validate = false;
			validate = validateAccounting(validate);
			// Accounting Details Validations
			if (validate && !isAccountingExecuted) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
		}
		
		doWriteComponentsToBean(aPaymentHeader);
		
		isNew = aPaymentHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPaymentHeader.getRecordType())) {
				aPaymentHeader.setVersion(aPaymentHeader.getVersion() + 1);
				if (isNew) {
					aPaymentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPaymentHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPaymentHeader.setNewRecord(true);
				}
			}
		} else {
			aPaymentHeader.setVersion(aPaymentHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(aPaymentHeader, tranType)) {
				refreshList();
				closeDialog();
				if (getDisbursementInstructionsDialogCtrl() != null) {
					getDisbursementInstructionsDialogCtrl().closeDialog();
				}
				
				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						if (StringUtils.isNotEmpty(aPaymentHeader.getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(aPaymentHeader.getRecordStatus())) {
								String[] to = aPaymentHeader.getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(aPaymentHeader.getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with Reference" + ":" + aPaymentHeader.getFinReference();
								getEventManager().publish(message, to, financeMain.getFinPurpose(), financeMain.getFinBranch());
							}
						}
					}
					
					String msg = PennantApplicationUtil.getSavingStatus(aPaymentHeader.getRoleCode(),
							aPaymentHeader.getNextRoleCode(), aPaymentHeader.getFinReference(),
							" Payment Instructions ", aPaymentHeader.getRecordStatus(), aPaymentHeader.getNextRoleCode());
					Clients.showNotification(msg, "info", null, null, -1);
					
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		} catch (final DataAccessException e) {
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PaymentHeader aPaymentHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPaymentHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPaymentHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPaymentHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPaymentHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if (PennantConstants.RCD_STATUS_APPROVED.equals(aPaymentHeader.getRecordStatus())) {
				aPaymentHeader.setStatus(RepayConstants.PAYMENT_APPROVE);
				aPaymentHeader.setApprovedOn(DateUtility.getAppDate());
			}

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPaymentHeader.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPaymentHeader);
				}

				if (isNotesMandatory(taskId, aPaymentHeader)) {
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
			aPaymentHeader.setTaskId(taskId);
			aPaymentHeader.setNextTaskId(nextTaskId);
			aPaymentHeader.setRoleCode(getRole());
			aPaymentHeader.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(aPaymentHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aPaymentHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPaymentHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPaymentHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PaymentHeader aPaymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = paymentHeaderService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = paymentHeaderService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = paymentHeaderService.doApprove(auditHeader);
						if (aPaymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = paymentHeaderService.doReject(auditHeader);
						if (aPaymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PaymentHeaderDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_PaymentHeaderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.paymentHeader), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
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
	private AuditHeader getAuditHeader(PaymentHeader aPaymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPaymentHeader.getBefImage(), aPaymentHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPaymentHeader.getUserDetails(),
				getOverideMap());
	}

	
	/****************************************************
	 * Account Executing								*
	 * ***************************************************
	 */
	
	/**
	 * Method for Executing Accountng Details
	 * 
	 * @throws Exception
	 */
	public void executeAccounting() throws Exception {
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_PAYMTINS);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		amountCodes.setFinType(financeMain.getFinType());
		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCustID(financeMain.getCustID());
			
		PaymentInstruction paymentInstruction = this.paymentHeader.getPaymentInstruction();
		
		if (paymentInstruction != null) {
			amountCodes.setPartnerBankAc(paymentInstruction.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(paymentInstruction.getPartnerBankAcType());
		}

		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		
		BigDecimal paybleAdviseAmt = BigDecimal.ZERO;
		BigDecimal excessAmount = BigDecimal.ZERO;
		BigDecimal emiInAdavance = BigDecimal.ZERO;

		for (PaymentDetail paymentDetail : getPaymentDetailList()) {
			if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
				paybleAdviseAmt = paybleAdviseAmt.add(paymentDetail.getAmount());
			} else if ("E".equals(paymentDetail.getAmountType())) {
				excessAmount = excessAmount.add(paymentDetail.getAmount());
			} else if ("A".equals(paymentDetail.getAmountType())) {
				emiInAdavance = emiInAdavance.add(paymentDetail.getAmount());
			}
		}
		aeEvent.getDataMap().put("pi_payableAdvice", paybleAdviseAmt);
		aeEvent.getDataMap().put("pi_excessAmount", excessAmount);
		aeEvent.getDataMap().put("pi_emiInAdvance", emiInAdavance);
		aeEvent.getDataMap().put("pi_paymentAmount", paymentHeader.getPaymentInstruction().getPaymentAmount());
		
		aeEvent.getAcSetIDList().add(accountsetId);
		List<ReturnDataSet> returnSetEntries = getEngineExecution().getAccEngineExecResults(aeEvent).getReturnDataSet();
		accountingSetEntries.addAll(returnSetEntries);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
			isAccountingExecuted = true;
		}

		logger.debug("Leaving");
	}
	
	
	 /***************************************************
	 * Payment details Saving, Processing...............*
	 * **************************************************
	 */
	private void doFillPaymentDetails(PaymentHeader aPaymentHeader) {
		logger.debug(Literal.ENTERING);

		PaymentDetail paymentDetail = null;
		List<PaymentDetail> detailList = new ArrayList<PaymentDetail>();
		List<FinExcessAmount> finExcessAmountList = this.paymentHeaderService.getfinExcessAmount(this.financeMain.getFinReference());
		if (finExcessAmountList != null && !finExcessAmountList.isEmpty()) {
			for (FinExcessAmount finExcessAmount : finExcessAmountList) {
				paymentDetail = new PaymentDetail();
				paymentDetail.setNewRecord(true);
				paymentDetail.setReferenceId(finExcessAmount.getId());
				paymentDetail.setAvailableAmount(finExcessAmount.getBalanceAmt());
				paymentDetail.setAmountType(finExcessAmount.getAmountType());
				detailList.add(paymentDetail);
			}
		}

		List<ManualAdvise> manualAdviseList = this.paymentHeaderService.getManualAdvise(this.financeMain.getFinReference());
		if (manualAdviseList != null && !manualAdviseList.isEmpty()) {
			for (ManualAdvise manualAdvise : manualAdviseList) {
				paymentDetail = new PaymentDetail();
				paymentDetail.setNewRecord(true);
				paymentDetail.setReferenceId(manualAdvise.getAdviseID());
				paymentDetail.setAvailableAmount(manualAdvise.getBalanceAmt());
				paymentDetail.setAmountType(String.valueOf(manualAdvise.getAdviseType()));
				detailList.add(paymentDetail);
			}
		}
		if (aPaymentHeader.isNewRecord()) {
			for (PaymentDetail detail : detailList) {
				if (BigDecimal.ZERO.compareTo(detail.getAvailableAmount()) == -1) {
					getPaymentDetailList().add(detail);
				}
			}
		} else {
			updatePaybleAmounts(detailList, aPaymentHeader.getPaymentDetailList());
		}
		doFillPaymentDetails(getPaymentDetailList());

		logger.debug(Literal.LEAVING);
	}

	//Update the latest balance amount..
	private void updatePaybleAmounts(List<PaymentDetail> newList, List<PaymentDetail> oldList) {
		logger.debug(Literal.ENTERING);

		List<PaymentDetail> tempList = new ArrayList<PaymentDetail>();
		tempList.addAll(newList);

		for (PaymentDetail oldDetail : oldList) {
			for (PaymentDetail newDetail : newList) {
				if (oldDetail.getReferenceId() == newDetail.getReferenceId()) {
					oldDetail.setAvailableAmount(oldDetail.getAmount().add(newDetail.getAvailableAmount()));
					oldDetail.setNewRecord(false);
					getPaymentDetailList().add(oldDetail);
					tempList.remove(newDetail);
				}
			}
		}
		for (PaymentDetail newDetail : tempList) {
			if (BigDecimal.ZERO.compareTo(newDetail.getAvailableAmount()) == -1) {
				getPaymentDetailList().add(newDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for action Forward event for changing PayAmountChange
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPayAmountChange(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Decimalbox paymentAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(paymentAmt);
		Clients.clearWrongValue(this.totAmount);
		
		BigDecimal amount = PennantAppUtil.unFormateAmount(paymentAmt.getValue(), ccyFormatter);
		PaymentDetail paymentDetail = (PaymentDetail) paymentAmt.getAttribute("object");
		for (PaymentDetail detail : getPaymentDetailList()) {
			if (paymentDetail.getReferenceId() == detail.getReferenceId()) {
				if ((detail.getAvailableAmount().compareTo(amount)) == -1) {
					throw new WrongValueException(paymentAmt, Labels.getLabel("label_PaymentHeaderDialog_paymentAmountErrorMsg.value"));
				} else {
					detail.setAmount(amount);
				}
			}
		}
		doFillPaymentDetails(getPaymentDetailList());

		logger.debug("Leaving");
	}

	private void savePaymentDetails(PaymentHeader aPaymentHeader) {
		logger.debug("Entering");

		List<PaymentDetail> list = new ArrayList<PaymentDetail>();
		if (aPaymentHeader.isNewRecord()) {
			for (PaymentDetail detail : getPaymentDetailList()) {
				if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
					continue;
				}
				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);
				list.add(detail);
			}
		} else {
			for (PaymentDetail detail : getPaymentDetailList()) {
				if (detail.isNewRecord()) {
					if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
						continue;
					}
					detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setNewRecord(true);
					list.add(detail);
				} else {
					if (detail.getAmount() != null && (BigDecimal.ZERO.compareTo(detail.getAmount()) == 0)) {
						detail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						detail.setNewRecord(false);
						list.add(detail);
					} else {
						detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						detail.setNewRecord(false);
						list.add(detail);
					}
				}
			}
		}
		aPaymentHeader.setPaymentDetailList(list);
		logger.debug("Leaving");
	}
	
	// Filling paymeny details list...
	public void doFillPaymentDetails(List<PaymentDetail> paymentDetailList) {
		logger.debug("Entering");

		this.listBoxPaymentTypeInstructions.getItems().clear();
		boolean isReadOnly = isReadOnly("PaymentHeaderDialog_paymentAmount");
		BigDecimal totalPayAmt = BigDecimal.ZERO;
		Listitem item = null;
		String amountType = null;

		if (paymentDetailList != null && !paymentDetailList.isEmpty()) {

			for (PaymentDetail paymentDetail : paymentDetailList) {
				item = new Listitem();
				Listcell lc;

				if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
					amountType = Labels.getLabel("label_PaymentHeaderDialog_ManualAdvisePayable.value");
				} else if ("E".equals(paymentDetail.getAmountType())) {
					amountType = Labels.getLabel("label_PaymentHeaderDialog_ExcessAmount.value");
				} else if ("A".equals(paymentDetail.getAmountType())) {
					amountType = Labels.getLabel("label_PaymentHeaderDialog_EMIinAdvanceAmount.value");
				}

				lc = new Listcell(amountType);
				lc.setParent(item);

				lc = new Listcell();
				Decimalbox avalableAmt = new Decimalbox();
				avalableAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				avalableAmt.setStyle("text-align:right; ");
				avalableAmt.setReadonly(true);
				avalableAmt.setValue(PennantAppUtil.formateAmount(paymentDetail.getAvailableAmount(), ccyFormatter));
				lc.appendChild(avalableAmt);
				lc.setParent(item);

				lc = new Listcell();
				paymentAmount = new Decimalbox();
				paymentAmount.setReadonly(isReadOnly);

				paymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				paymentAmount.setStyle("text-align:right; ");
				paymentAmount.setValue(PennantAppUtil.formateAmount(paymentDetail.getAmount(), ccyFormatter));
				totalPayAmt = totalPayAmt.add(paymentDetail.getAmount());
				paymentAmount.addForward("onChange", self, "onPayAmountChange");
				paymentAmount.setAttribute("object", paymentDetail);
				lc.appendChild(paymentAmount);
				lc.setParent(item);

				lc = new Listcell();
				Decimalbox balanceAmount = new Decimalbox();
				balanceAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				balanceAmount.setStyle("text-align:right; ");
				balanceAmount.setReadonly(true);
				balanceAmount.setValue(PennantAppUtil.formateAmount(paymentDetail.getAvailableAmount().subtract(paymentDetail.getAmount()), ccyFormatter));
				lc.appendChild(balanceAmount);
				lc.setParent(item);

				this.listBoxPaymentTypeInstructions.appendChild(item);
			}
			item = new Listitem();
			Listcell lc;

			if (enqiryModule) {
				lc = new Listcell(" Total Pay Amount ");
				lc.setStyle("font-weight:bold;");
			} else {
				lc = new Listcell();
			}
			item.appendChild(lc);

			lc = new Listcell(" Total Pay Amount ");
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			lc = new Listcell();
			totAmount = new Decimalbox();
			totAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			totAmount.setStyle("text-align:right; ");
			totAmount.setReadonly(true);
			totalPayAmt = PennantAppUtil.formateAmount(totalPayAmt, ccyFormatter);
			totAmount.setValue(totalPayAmt);
			lc.appendChild(totAmount);
			lc.setParent(item);
			lc = new Listcell();
			lc.setParent(item);
			
			if(getDisbursementInstructionsDialogCtrl() != null) {
				getDisbursementInstructionsDialogCtrl().paymentAmount.setValue(totalPayAmt);
			}
			this.listBoxPaymentTypeInstructions.appendChild(item);
		}
		logger.debug("Leaving");
	}
	

	// Setters and getters
	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	public List<PaymentDetail> getPaymentDetailList() {
		return paymentDetailList;
	}

	public void setPaymentDetailList(List<PaymentDetail> paymentDetailList) {
		this.paymentDetailList = paymentDetailList;
	}

	public PaymentInstructionDialogCtrl getDisbursementInstructionsDialogCtrl() {
		return disbursementInstructionsDialogCtrl;
	}

	public void setDisbursementInstructionsDialogCtrl(
			PaymentInstructionDialogCtrl disbursementInstructionsDialogCtrl) {
		this.disbursementInstructionsDialogCtrl = disbursementInstructionsDialogCtrl;
	}

	public EventManager getEventManager() {
		return eventManager;
	}
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

}
