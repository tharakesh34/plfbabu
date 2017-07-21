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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  ReceiptCancellationDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.receipts;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/ReceiptCancellationDialog.zul
 */
public class ReceiptCancellationDialogCtrl  extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long								serialVersionUID					= 966281186831332116L;
	private static final Logger								logger								= Logger.getLogger(ReceiptCancellationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window										window_ReceiptCancellationDialog;
	protected Borderlayout									borderlayout_Realization;
	protected Label 										windowTitle;

	//Receipt Details
	protected Textbox										finType;
	protected Textbox										finReference;
	protected Textbox										finCcy;
	protected Textbox										finBranch;
	protected Textbox										custCIF;

	protected Combobox										receiptPurpose;
	protected Combobox										excessAdjustTo;
	protected Combobox										receiptMode;
	protected CurrencyBox									receiptAmount;
	protected Combobox										allocationMethod;
	protected Combobox										effScheduleMethod;
	protected ExtendedCombobox								bounceCode;
	protected CurrencyBox									bounceCharge;
	protected Textbox										bounceRemarks;
	protected ExtendedCombobox								cancelReason;

	protected Groupbox										gb_ReceiptDetails;
	protected Caption										caption_receiptDetail;
	protected Label											label_ReceiptCancellationDialog_favourNo;
	protected Uppercasebox									favourNo;
	protected Datebox										valueDate;
	protected ExtendedCombobox								bankCode;
	protected Textbox										favourName;
	protected Datebox										depositDate;
	protected Uppercasebox									depositNo;
	protected Uppercasebox									paymentRef;
	protected Uppercasebox									transactionRef;
	protected AccountSelectionBox							chequeAcNo;
	protected ExtendedCombobox								fundingAccount;
	protected Datebox										receivedDate;
	protected Textbox										remarks;
	protected Label											label_ReceiptCancellationDialog_BounceDate;
	protected Hbox											hbox_ReceiptCancellationDialog_BounceDate;
	protected Datebox										bounceDate;
	
	protected Row											row_BounceReason;	
	protected Row											row_CancelReason;	
	protected Row											row_BounceRemarks;	

	protected Row											row_favourNo;	
	protected Row											row_BankCode;	
	protected Row											row_DepositDate;	
	protected Row											row_PaymentRef;	
	protected Row											row_ChequeAcNo;	
	protected Row											row_fundingAcNo;	
	protected Row											row_remarks;	
	
	// Payment Schedule Details
	protected Textbox										payment_finType;
	protected Textbox										payment_finReference;
	protected Textbox										payment_finCcy;
	protected Textbox										payment_CustCIF;
	protected Textbox										payment_finBranch;
	
	// List Header Details on payent Details
	protected Listheader									listheader_Tds;
	protected Listheader									listheader_LatePft;
	protected Listheader									listheader_Refund;
	protected Listheader									listheader_Penalty;
	protected Listheader									listheader_InsPayment;
	protected Listheader									listheader_SchdFee;
	protected Listheader									listheader_SuplRent;
	protected Listheader									listheader_IncrCost;
	
	protected Listbox										listBoxReceipts;
	protected Listbox										listBoxPayment;
	protected Listbox										listBoxPosting;
	protected Tab											receiptDetailsTab;
	protected Tab											repaymentDetailsTab;
	protected Tab											postingDetailsTab;

	// Postings Details
	protected Textbox										posting_finType;
	protected Textbox										posting_finReference;
	protected Textbox										posting_finCcy;
	protected Textbox										posting_CustCIF;
	protected Textbox										posting_finBranch;
	
	private FinReceiptHeader								receiptHeader						= null;
	private ReceiptCancellationListCtrl						receiptCancellationListCtrl;						
	private ReceiptCancellationService						receiptCancellationService;						
	private RuleService										ruleService;						
	private RuleExecutionUtil								ruleExecutionUtil;						
	private String module;

	/**
	 * default constructor.<br>
	 */
	public ReceiptCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			super.pageRightName = "ReceiptBounceDialog";
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			super.pageRightName = "ReceiptCancellationDialog";
		}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEECANCEL)) {
			super.pageRightName = "ReceiptCancellationDialog";
		}
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptCancellationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReceiptCancellationDialog);

		try {
			if (arguments.containsKey("receiptHeader")) {

				setReceiptHeader((FinReceiptHeader) arguments.get("receiptHeader"));
				FinReceiptHeader befImage = new FinReceiptHeader();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(getReceiptHeader());
				getReceiptHeader().setBefImage(befImage);
				
				if(getReceiptHeader().getManualAdvise() != null){
					ManualAdvise adviseBefImage = cloner.deepClone(getReceiptHeader().getManualAdvise());
					getReceiptHeader().getManualAdvise().setBefImage(adviseBefImage);
				}
			}
			
			this.module = (String) arguments.get("module");
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
				super.pageRightName = "ReceiptBounceDialog";
				this.windowTitle.setValue(Labels.getLabel("window_ReceiptBounceDialog.title"));
			}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
				super.pageRightName = "ReceiptCancellationDialog";
				this.windowTitle.setValue(Labels.getLabel("window_ReceiptCancellationDialog.title"));
			}else if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_FEECANCEL)) {
				super.pageRightName = "ReceiptCancellationDialog";
				this.windowTitle.setValue(Labels.getLabel("window_FeeReceiptCancellationDialog.title"));
				this.repaymentDetailsTab.setVisible(false);
			}
			
			this.receiptCancellationListCtrl = (ReceiptCancellationListCtrl) arguments.get("receiptCancellationListCtrl");
			doLoadWorkFlow(receiptHeader.isWorkflow(), receiptHeader.getWorkflowId(), receiptHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(receiptHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
				}
			} else {
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			doReadonly();
			if (StringUtils.isNotBlank(receiptHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}

			//Reset Finance Repay Header Details
			doWriteBeanToComponents();
			this.borderlayout_Realization.setHeight(getBorderLayoutHeight());
			this.listBoxPayment.setHeight(getListBoxHeight(6));
			this.listBoxPosting.setHeight(getListBoxHeight(6));
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReceiptCancellationDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_"+this.pageRightName+"_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_"+this.pageRightName+"_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_"+this.pageRightName+"_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_"+this.pageRightName+"_btnSave"));
		this.btnCancel.setVisible(false);
		
		// Bounce Reason Fields
		readOnlyComponent(isReadOnly(this.pageRightName+"_bounceCode"), this.bounceCode);
		//readOnlyComponent(isReadOnly("+this.pageRightName+"_bounceCharge"), this.bounceCharge);
		readOnlyComponent(true, this.bounceCharge);
		readOnlyComponent(isReadOnly(this.pageRightName+"_bounceRemarks"), this.bounceRemarks);
		readOnlyComponent(isReadOnly(this.pageRightName+"_bounceDate"), this.bounceDate);
		readOnlyComponent(isReadOnly(this.pageRightName+"_cancelReason"), this.cancelReason);
		
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		
		//Empty sent any required attributes
		int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());

		//Receipts Details
		this.receiptAmount.setProperties(true , formatter);

		this.cancelReason.setModuleName("RejectDetail");
		this.cancelReason.setMandatoryStyle(true);
		this.cancelReason.setValueColumn("RejectCode");
		this.cancelReason.setDescColumn("RejectDesc");
		this.cancelReason.setDisplayStyle(2);
		this.cancelReason.setValidateColumns(new String[] { "RejectCode" });
		this.cancelReason.setFilters(new Filter[] { new Filter("RejectType", PennantConstants.Reject_Payment, Filter.OP_EQUAL) });

		this.fundingAccount.setModuleName("FinTypePartner");
		this.fundingAccount.setMandatoryStyle(true);
		this.fundingAccount.setValueColumn("PartnerBankID");
		this.fundingAccount.setDescColumn("PartnerBankCode");
		this.fundingAccount.setDisplayStyle(2);
		this.fundingAccount.setValidateColumns(new String[] { "PartnerBankID" });

		this.chequeAcNo.setButtonVisible(false);
		this.chequeAcNo.setMandatory(false);
		this.chequeAcNo.setAcountDetails("", "", true);
		this.chequeAcNo.setTextBoxWidth(180);

		this.receivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.remarks.setMaxlength(100);
		this.bounceDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourName.setMaxlength(50);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.favourNo.setMaxlength(6);
		this.depositDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.depositNo.setMaxlength(50);
		this.paymentRef.setMaxlength(50);
		this.transactionRef.setMaxlength(50);

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });
		
		this.bounceCode.setModuleName("BounceReason");
		this.bounceCode.setMandatoryStyle(true);
		this.bounceCode.setValueColumn("BounceID");
		this.bounceCode.setDescColumn("BounceCode");
		this.bounceCode.setDisplayStyle(2);
		this.bounceCode.setValidateColumns(new String[] { "BounceID" , "BounceCode", "Category", "Reason" });
		
		this.bounceCharge.setProperties(false , formatter);
		this.bounceRemarks.setMaxlength(100);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doReadonly() {
		logger.debug("Entering");

		// Receipt Details
		readOnlyComponent(true, this.receiptPurpose);
		readOnlyComponent(true, this.excessAdjustTo);
		readOnlyComponent(true, this.receiptMode);
		readOnlyComponent(true, this.receiptAmount);
		readOnlyComponent(true, this.allocationMethod);
		readOnlyComponent(true, this.effScheduleMethod);

		//Receipt Details
		readOnlyComponent(true, this.favourNo);
		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.bankCode);
		readOnlyComponent(true, this.favourName);
		readOnlyComponent(true, this.depositDate);
		readOnlyComponent(true, this.depositNo);
		readOnlyComponent(true, this.chequeAcNo);
		readOnlyComponent(true, this.fundingAccount);
		readOnlyComponent(true, this.paymentRef);
		readOnlyComponent(true, this.transactionRef);
		readOnlyComponent(true, this.receivedDate);
		readOnlyComponent(true, this.remarks);

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		this.receiptCancellationListCtrl.search();
	}


	/**
	 * Method for Setting Fields based on Receipt Mode selected
	 * @param recMode
	 */
	private void checkByReceiptMode(String recMode, boolean isUserAction) {
		logger.debug("Entering");

		if (StringUtils.isEmpty(recMode) || StringUtils.equals(recMode, PennantConstants.List_Select) ||
				StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_EXCESS)) {
			this.gb_ReceiptDetails.setVisible(false);
			this.receiptAmount.setMandatory(false);
			this.receiptAmount.setReadonly(true);
			this.receiptAmount.setValue(BigDecimal.ZERO);

		} else{

			this.gb_ReceiptDetails.setVisible(true);
			this.caption_receiptDetail.setLabel(this.receiptMode.getSelectedItem().getLabel());
			this.receiptAmount.setMandatory(false);
			this.row_fundingAcNo.setVisible(true);
			this.row_remarks.setVisible(true);

			if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)
					|| StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_DD)) {

				this.row_favourNo.setVisible(true);
				this.row_BankCode.setVisible(true);
				this.bankCode.setMandatoryStyle(true);
				this.row_DepositDate.setVisible(true);
				this.row_PaymentRef.setVisible(false);

				if(StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CHEQUE)){
					this.row_ChequeAcNo.setVisible(true);
					this.label_ReceiptCancellationDialog_favourNo.setValue(Labels.getLabel("label_ReceiptCancellationDialog_ChequeFavourNo.value"));
				}else{
					this.row_ChequeAcNo.setVisible(false);
					this.label_ReceiptCancellationDialog_favourNo.setValue(Labels.getLabel("label_ReceiptCancellationDialog_DDFavourNo.value"));
				}

			} else if (StringUtils.equals(recMode, RepayConstants.RECEIPTMODE_CASH)) {

				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(false);

			} else {
				this.row_favourNo.setVisible(false);
				this.row_BankCode.setVisible(false);
				this.bankCode.setMandatoryStyle(false);
				this.row_DepositDate.setVisible(false);
				this.row_ChequeAcNo.setVisible(false);
				this.row_fundingAcNo.setVisible(false);
				this.row_PaymentRef.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void doSave() throws WrongValueException, InterruptedException {
		logger.debug("Entering");

		//Duplicate Creation of Object
		Cloner cloner = new Cloner();
		FinReceiptHeader aReceiptHeader = cloner.deepClone(getReceiptHeader());
		
		ArrayList<WrongValueException> wve = new ArrayList<>();
		boolean recReject = false;
		if (this.userAction.getSelectedItem() != null
				&& ("Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| "Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) || "Cancel"
						.equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))) {
			recReject = true;
		}

		if(!recReject){
			doSetValidation();
		}
		try {
			if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
				aReceiptHeader.setBounceDate(this.bounceDate.getValue());
			} else {
				aReceiptHeader.setBounceDate(null);
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_BOUNCE);

			// Bounce Details capturing
			ManualAdvise bounce = aReceiptHeader.getManualAdvise();
			if(bounce == null){
				bounce = new ManualAdvise();
			}

			
			bounce.setAdviseType(FinanceConstants.MANUAL_ADVISE_RECEIVABLE);
			bounce.setFinReference(aReceiptHeader.getReference());
			bounce.setFeeTypeID(0);
			bounce.setSequence(0);
			try {
				bounce.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.bounceCharge.getActualValue(), CurrencyUtil.getFormat(aReceiptHeader.getFinCcy())));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			bounce.setPaidAmount(BigDecimal.ZERO);
			bounce.setWaivedAmount(BigDecimal.ZERO);
			bounce.setValueDate(DateUtility.getAppDate());
			bounce.setPostDate(DateUtility.getPostDate());

			try {
				bounce.setRemarks(this.bounceRemarks.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			bounce.setReceiptID(aReceiptHeader.getReceiptID());
			try {
				bounce.setBounceID(Long.valueOf(this.bounceCode.getValue()));
			} catch (WrongValueException e) {
				wve.add(e);
			}

			doRemoveValidation();

			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				this.receiptDetailsTab.setSelected(true);
				throw new WrongValuesException(wvea);
			}
			
			aReceiptHeader.setManualAdvise(bounce);
		}else{
			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);
			
			try {
				aReceiptHeader.setCancelReason(this.cancelReason.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
			
			doRemoveValidation();

			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				this.receiptDetailsTab.setSelected(true);
				throw new WrongValuesException(wvea);
			}
			
			aReceiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_CANCEL);
		}

		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReceiptHeader.getRecordType())) {
				aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
				aReceiptHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aReceiptHeader.setNewRecord(true);
			}
		} else {
			aReceiptHeader.setVersion(aReceiptHeader.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}
		
		try {
			if (doProcess(aReceiptHeader, tranType)) {

				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aReceiptHeader.getNextTaskId())) {
					aReceiptHeader.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aReceiptHeader.getRoleCode(),
						aReceiptHeader.getNextRoleCode(), aReceiptHeader.getReference(), " Finance ",
						aReceiptHeader.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}
	
	public void onFulfill$bounceCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bounceCode.getObject();

		if (dataObject instanceof String) {
			this.bounceCode.setValue(dataObject.toString());
		} else {
			BounceReason bounceReason = (BounceReason) dataObject;
			if (bounceReason != null) {
				HashMap<String, Object> executeMap = bounceReason.getDeclaredFieldValues();

				if (this.receiptHeader != null) {
					if (this.receiptHeader.getReceiptDetails() != null
							&& !this.receiptHeader.getReceiptDetails().isEmpty()) {
						for (FinReceiptDetail finReceiptDetail : this.receiptHeader.getReceiptDetails()) {
							if (StringUtils.equals(this.receiptHeader.getReceiptMode(),
									finReceiptDetail.getPaymentType())) {
								finReceiptDetail.getDeclaredFieldValues(executeMap);
								break;
							}
						}
					}
				}

				Rule rule = getRuleService().getRuleById(bounceReason.getRuleID(), "");
				BigDecimal bounceAmt = BigDecimal.ZERO;
				int formatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());
				if (rule != null) {
					bounceAmt = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(), executeMap,
							getReceiptHeader().getFinCcy(), RuleReturnType.DECIMAL);
					// unFormating BounceAmt
					bounceAmt = PennantApplicationUtil.unFormateAmount(bounceAmt, formatter);
				}
				this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceAmt, formatter));
			}
		}
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.bounceCode.isReadonly()) {
			this.bounceCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptCancellationDialog_BounceReason.value"), null,true, true));
		}
		
		if (!this.cancelReason.isReadonly()) {
			this.cancelReason.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptCancellationDialog_CancelReason.value"), null,true, true));
		}

		if(!this.bounceRemarks.isReadonly()){
			this.bounceRemarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptCancellationDialog_BounceRemarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		
		if (!this.bounceDate.isDisabled() ) {
			this.bounceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ReceiptCancellationDialog_BounceDate.value"),
					true, null, null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.bounceCode.setConstraint("");
		this.bounceRemarks.setConstraint("");
		this.cancelReason.setConstraint("");
		this.bounceDate.setConstraint("");
		this.bounceCode.setErrorMessage("");
		this.bounceRemarks.setErrorMessage("");
		this.cancelReason.setErrorMessage("");
		logger.debug("Leaving");
	}


	/**
	 * Method for Writing Data into Fields from Bean
	 * @throws InterruptedException
	 */
	private void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader header = getReceiptHeader();
		
		this.finType.setValue(header.getFinType()+"-"+header.getFinTypeDesc());
		this.finReference.setValue(header.getReference());
		this.finCcy.setValue(header.getFinCcy()+"-"+header.getFinCcyDesc());
		this.finBranch.setValue(header.getFinBranch()+"-"+header.getFinBranchDesc());;
		this.custCIF.setValue(header.getCustCIF()+"-"+header.getCustShrtName());
		int finFormatter = CurrencyUtil.getFormat(header.getFinCcy());
		this.remarks.setValue(header.getRemarks());
		
		fillComboBox(this.receiptPurpose, header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose(), "");
		fillComboBox(this.excessAdjustTo, header.getExcessAdjustTo(), PennantStaticListUtil.getExcessAdjustmentTypes(), "");
		fillComboBox(this.receiptMode, header.getReceiptMode(), PennantStaticListUtil.getReceiptModes(), "");
		this.receiptAmount.setValue(PennantApplicationUtil.formateAmount(header.getReceiptAmount(), finFormatter));

		fillComboBox(this.allocationMethod, header.getAllocationType(), PennantStaticListUtil.getAllocationMethods(), "");
		fillComboBox(this.effScheduleMethod, header.getEffectSchdMethod(), PennantStaticListUtil.getEarlyPayEffectOn(), ",NOEFCT,");
		this.cancelReason.setValue(header.getCancelReason(), header.getCancelReasonDesc());
		checkByReceiptMode(header.getReceiptMode(), false);
		this.bounceDate.setValue(header.getBounceDate());
		if(header.getBounceDate() == null){
			this.bounceDate.setValue(DateUtility.getAppDate());
		}
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_CANCEL)) {
			this.bounceDate.setValue(null);
		}
		ManualAdvise bounceReason = header.getManualAdvise();
		if(bounceReason != null){
			this.bounceCode.setValue(String.valueOf(bounceReason.getBounceID()), bounceReason.getBounceCode());
			this.bounceCharge.setValue(PennantApplicationUtil.formateAmount(bounceReason.getAdviseAmount(),finFormatter));
			this.bounceRemarks.setValue(bounceReason.getRemarks());
		}
		
		// Repayments Schedule Basic Details
		this.payment_finType.setValue(header.getFinType()+"-"+header.getFinTypeDesc());
		this.payment_finReference.setValue(header.getReference());
		this.payment_finCcy.setValue(header.getFinCcy()+"-"+header.getFinCcyDesc());
		this.payment_finBranch.setValue(header.getFinBranch()+"-"+header.getFinBranchDesc());;
		this.payment_CustCIF.setValue(header.getCustCIF()+"-"+header.getCustShrtName());
		
		boolean isBounceProcess = false;
		this.listBoxReceipts.getItems().clear();
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			isBounceProcess = true;
			this.row_CancelReason.setVisible(false);
			this.cancelReason.setMandatoryStyle(false);
			this.cancelReason.setReadonly(true);
			this.label_ReceiptCancellationDialog_BounceDate.setVisible(true);
			this.hbox_ReceiptCancellationDialog_BounceDate.setVisible(true);
		}else{
			this.row_BounceReason.setVisible(false);
			this.row_BounceRemarks.setVisible(false);
			this.bounceCode.setMandatoryStyle(false);
			this.bounceCode.setReadonly(true);
			this.bounceRemarks.setReadonly(true);
			this.label_ReceiptCancellationDialog_BounceDate.setVisible(false);
			this.hbox_ReceiptCancellationDialog_BounceDate.setVisible(false);
		}

		// Separating Receipt Amounts based on user entry, if exists
		List<RepayScheduleDetail> rpySchdList = new ArrayList<>();
		if(header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()){
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				doFillReceipts(receiptDetail, finFormatter);
				boolean isReceiptModeDetail = false;
				
				if(!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) && 
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) &&
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){

					this.favourNo.setValue(receiptDetail.getFavourNumber());
					this.valueDate.setValue(receiptDetail.getValueDate());
					this.bankCode.setValue(receiptDetail.getBankCode());
					this.bankCode.setDescription(receiptDetail.getBankCodeDesc());
					this.favourName.setValue(receiptDetail.getFavourName());
					this.depositDate.setValue(receiptDetail.getDepositDate());
					this.depositNo.setValue(receiptDetail.getDepositNo());
					this.paymentRef.setValue(receiptDetail.getPaymentRef());
					this.transactionRef.setValue(receiptDetail.getTransactionRef());
					this.chequeAcNo.setValue(receiptDetail.getChequeAcNo());
					this.fundingAccount.setValue(String.valueOf(receiptDetail.getFundingAc()));
					this.fundingAccount.setDescription(receiptDetail.getFundingAcDesc());
					this.receivedDate.setValue(receiptDetail.getReceivedDate());
					
					isReceiptModeDetail = true;
				}
				
				// If Bounce Process and not a Receipt Mode Record then Continue process
				if(isBounceProcess && !isReceiptModeDetail){
					continue;
				}

				// Getting All Repayments Schedule Details for Display of Payments
				List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();
				for (int j = 0; j < repayHeaderList.size(); j++) {
					if(repayHeaderList.get(j).getRepayScheduleDetails() != null){
						rpySchdList.addAll(repayHeaderList.get(j).getRepayScheduleDetails());
					}
				}
			}

			// Making Single Set of Repay Schedule Details and sent to Rendering
			if(!rpySchdList.isEmpty()){
				
				Cloner cloner = new Cloner();
				List<RepayScheduleDetail> tempRpySchdList = cloner.deepClone(rpySchdList);
				Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
				
				for (RepayScheduleDetail rpySchd : tempRpySchdList) {

					RepayScheduleDetail curRpySchd = null;
					if(rpySchdMap.containsKey(rpySchd.getSchDate())){
						curRpySchd = rpySchdMap.get(rpySchd.getSchDate());
						
						if(curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0){
							curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
						}
						
						if(curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0){
							curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
						}
						
						curRpySchd.setPrincipalSchdPayNow(curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
						curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
						curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
						curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
						curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
						curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
						curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));
						rpySchdMap.remove(rpySchd.getSchDate());
					}else{
						curRpySchd = rpySchd;
					}

					// Adding New Repay Schedule Object to Map after Summing data
					rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
				}

				doFillRepaySchedules(sortRpySchdDetails(new ArrayList<>(rpySchdMap.values())));
			}

			// Posting Details
			this.postingDetailsTab.addForward(Events.ON_SELECT, this.window_ReceiptCancellationDialog, "onSelectPostingsTab");
		}

		this.recordStatus.setValue(header.getRecordStatus());
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Receipt Amount Details
	 */
	private void doFillReceipts(FinReceiptDetail receiptDetail, int finFormatter){
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc = null;
		String label = "";
		if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) || 
				StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) || 
				StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PRESENTMENT)){

			label = Labels.getLabel("label_ReceiptCancellationDialog_ExcessType_"+receiptDetail.getPaymentType());

		}else if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
			label = receiptDetail.getFeeTypeDesc();
		}else{
			label = PennantAppUtil.getlabelDesc(receiptDetail.getPaymentType(), PennantStaticListUtil.getReceiptModes());
		}

		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;color: #191a1c;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(receiptDetail.getAmount(), finFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(Labels.getLabel("label_ReceiptCancellationDialog_Status_"+receiptDetail.getStatus()));
		if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_APPROVED)){
			lc.setStyle("font-weight:bold;color: #0252d3;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)){
			lc.setStyle("font-weight:bold;color: #00a83d;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_BOUNCE)){
			lc.setStyle("font-weight:bold;color: #f44b42;");
		}else if(StringUtils.equals(receiptDetail.getStatus(), RepayConstants.PAYSTATUS_CANCEL)){
			lc.setStyle("font-weight:bold;color: #f48341;");
		}
		lc.setParent(item);
		this.listBoxReceipts.appendChild(item);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Selecting Posting Details tab
	 * @param event
	 */
	public void onSelectPostingsTab(ForwardEvent event) {
		logger.debug("Entering");
		
		this.postingDetailsTab.removeForward(Events.ON_SELECT, this.window_ReceiptCancellationDialog, "onSelectPostingsTab");
		
		FinReceiptHeader header = getReceiptHeader();
		// Repayments Schedule Basic Details
		this.posting_finType.setValue(header.getFinType()+"-"+header.getFinTypeDesc());
		this.posting_finReference.setValue(header.getReference());
		this.posting_finCcy.setValue(header.getFinCcy()+"-"+header.getFinCcyDesc());
		this.posting_finBranch.setValue(header.getFinBranch()+"-"+header.getFinBranchDesc());;
		this.posting_CustCIF.setValue(header.getCustCIF()+"-"+header.getCustShrtName());
		
		boolean isBounceProcess = false;
		if (StringUtils.equals(this.module, RepayConstants.MODULETYPE_BOUNCE)) {
			isBounceProcess = true;
		}
		
		// Identifying Transaction List
		List<Long> tranIdList = new ArrayList<>();
		if(header.getReceiptDetails() != null && !header.getReceiptDetails().isEmpty()){
			for (int i = 0; i < header.getReceiptDetails().size(); i++) {
				
				FinReceiptDetail receiptDetail = header.getReceiptDetails().get(i);
				boolean isReceiptModeDetail = false;
				
				if(!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) && 
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV) &&
						!StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){
					
					isReceiptModeDetail = true;
				}
				
				// If Bounce Process and not a Receipt Mode Record then Continue process
				if(isBounceProcess && !isReceiptModeDetail){
					continue;
				}
				
				// List out all Transaction Id's
				List<FinRepayHeader> repayHeaderList = receiptDetail.getRepayHeaders();
				for (int j = 0; j < repayHeaderList.size(); j++) {
					tranIdList.add(repayHeaderList.get(j).getLinkedTranId());
				}
			}
		}
		
		// Posting Details Rendering
		if(!tranIdList.isEmpty()){
			List<ReturnDataSet> postings = getReceiptCancellationService().getPostingsByTranIdList(tranIdList);
			doFillPostings(postings);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sorting Repay Schedule Details
	 * 
	 * @param repayScheduleDetails
	 * @return
	 */
	public List<RepayScheduleDetail> sortRpySchdDetails(List<RepayScheduleDetail> repayScheduleDetails) {

		if (repayScheduleDetails != null && repayScheduleDetails.size() > 0) {
			Collections.sort(repayScheduleDetails, new Comparator<RepayScheduleDetail>() {
				@Override
				public int compare(RepayScheduleDetail detail1, RepayScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return repayScheduleDetails;
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillRepaySchedules(List<RepayScheduleDetail> repaySchdList) {
		logger.debug("Entering");

		//setRepaySchdList(sortRpySchdDetails(repaySchdList));
		this.listBoxPayment.getItems().clear();
		BigDecimal totalRefund = BigDecimal.ZERO;
		BigDecimal totalWaived = BigDecimal.ZERO;
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalTds = BigDecimal.ZERO;
		BigDecimal totalLatePft = BigDecimal.ZERO;
		BigDecimal totalPri = BigDecimal.ZERO;
		BigDecimal totalCharge = BigDecimal.ZERO;

		BigDecimal totInsPaid = BigDecimal.ZERO;
		BigDecimal totSchdFeePaid = BigDecimal.ZERO;
		BigDecimal totSchdSuplRentPaid = BigDecimal.ZERO;
		BigDecimal totSchdIncrCostPaid = BigDecimal.ZERO;

		Listcell lc;
		Listitem item;

		int finFormatter = CurrencyUtil.getFormat(getReceiptHeader().getFinCcy());

		if (repaySchdList != null) {
			for (int i = 0; i < repaySchdList.size(); i++) {
				RepayScheduleDetail repaySchd = repaySchdList.get(i);
				item = new Listitem();

				lc = new Listcell(DateUtility.formatToLongDate(repaySchd.getSchDate()));
				lc.setStyle("font-weight:bold;color: #FF6600;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdBal(), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getProfitSchdPayNow(), finFormatter));
				totalPft = totalPft.add(repaySchd.getProfitSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getTdsSchdPayNow(), finFormatter));
				totalTds = totalTds.add(repaySchd.getTdsSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getLatePftSchdPayNow(), finFormatter));
				totalLatePft = totalLatePft.add(repaySchd.getLatePftSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPrincipalSchdPayNow(), finFormatter));
				totalPri = totalPri.add(repaySchd.getPrincipalSchdPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getPenaltyPayNow(), finFormatter));
				totalCharge = totalCharge.add(repaySchd.getPenaltyPayNow());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				if (repaySchd.getDaysLate() > 0) {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getMaxWaiver(), finFormatter));
				} else {
					lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getRefundMax(), finFormatter));
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				BigDecimal refundPft = BigDecimal.ZERO;
				if (repaySchd.isAllowRefund() || repaySchd.isAllowWaiver()) {
					if (repaySchd.isAllowRefund()) {
						refundPft = repaySchd.getRefundReq();
						totalRefund = totalRefund.add(refundPft);
					} else if (repaySchd.isAllowWaiver()) {
						refundPft = repaySchd.getWaivedAmt();
						totalWaived = totalWaived.add(refundPft);
					}
				}

				lc = new Listcell(PennantAppUtil.amountFormate(refundPft, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				//Fee Details
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdInsPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totInsPaid = totInsPaid.add(repaySchd.getSchdInsPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdFeePayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdFeePaid = totSchdFeePaid.add(repaySchd.getSchdFeePayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdSuplRentPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdSuplRentPaid = totSchdSuplRentPaid.add(repaySchd.getSchdSuplRentPayNow());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(repaySchd.getSchdIncrCostPayNow(), finFormatter));
				lc.setStyle("text-align:right;");
				totSchdIncrCostPaid = totSchdIncrCostPaid.add(repaySchd.getSchdIncrCostPayNow());
				lc.setParent(item);

				BigDecimal netPay = repaySchd.getProfitSchdPayNow().add(repaySchd.getPrincipalSchdPayNow())
						.add(repaySchd.getSchdInsPayNow()).add(repaySchd.getSchdFeePayNow())
						.add(repaySchd.getSchdSuplRentPayNow()).add(repaySchd.getSchdIncrCostPayNow())
						.add(repaySchd.getPenaltyPayNow()).add(repaySchd.getLatePftSchdPayNow())
						.subtract(refundPft);
				lc = new Listcell(PennantAppUtil.amountFormate(netPay, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				BigDecimal netBalance = repaySchd.getProfitSchdBal().add(repaySchd.getPrincipalSchdBal())
						.add(repaySchd.getSchdInsBal()).add(repaySchd.getSchdFeeBal())
						.add(repaySchd.getSchdSuplRentBal()).add(repaySchd.getSchdIncrCostBal());
						
				lc = new Listcell(PennantAppUtil.amountFormate(netBalance.subtract(netPay.subtract(
						repaySchd.getPenaltyPayNow()).subtract(repaySchd.getLatePftSchdPayNow())), finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				item.setAttribute("data", repaySchd);
				this.listBoxPayment.appendChild(item);
			}

			//Summary Details
			Map<String, BigDecimal> paymentMap = new HashMap<String, BigDecimal>();
			paymentMap.put("totalRefund", totalRefund);
			paymentMap.put("totalCharge", totalCharge);
			paymentMap.put("totalPft", totalPft);
			paymentMap.put("totalTds", totalTds);
			paymentMap.put("totalLatePft", totalLatePft);
			paymentMap.put("totalPri", totalPri);

			paymentMap.put("insPaid", totInsPaid);
			paymentMap.put("schdFeePaid", totSchdFeePaid);
			paymentMap.put("schdSuplRentPaid", totSchdSuplRentPaid);
			paymentMap.put("schdIncrCostPaid", totSchdIncrCostPaid);

			doFillSummaryDetails(paymentMap, finFormatter);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Summary Details for Repay Schedule Terms
	 * 
	 * @param totalrefund
	 * @param totalWaiver
	 * @param totalPft
	 * @param totalPri
	 */
	private void doFillSummaryDetails(Map<String, BigDecimal> paymentMap, int finFormatter) {

		Listcell lc;
		Listitem item;

		//Summary Details
		item = new Listitem();
		lc = new Listcell(Labels.getLabel("listcell_summary.label"));
		lc.setStyle("font-weight:bold;background-color: #C0EBDF;");
		lc.setSpan(15);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

		BigDecimal totalSchAmount = BigDecimal.ZERO;

		if (paymentMap.get("totalRefund").compareTo(BigDecimal.ZERO) > 0) {
			this.listheader_Refund.setVisible(true);
			totalSchAmount = totalSchAmount.subtract(paymentMap.get("totalRefund"));
			fillListItem(Labels.getLabel("listcell_totalRefund.label"), paymentMap.get("totalRefund"), finFormatter);
		}else{
			this.listheader_Refund.setVisible(false);
		}
		if (paymentMap.get("totalCharge").compareTo(BigDecimal.ZERO) > 0) {
			this.listheader_Penalty.setVisible(true);
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalCharge"));
			fillListItem(Labels.getLabel("listcell_totalPenalty.label"), paymentMap.get("totalCharge"), finFormatter);
		}else{
			this.listheader_Penalty.setVisible(false);
		}
		if (paymentMap.get("totalPft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPft"));
			fillListItem(Labels.getLabel("listcell_totalPftPayNow.label"), paymentMap.get("totalPft"), finFormatter);
		}
		if (paymentMap.get("totalTds").compareTo(BigDecimal.ZERO) > 0) {
			fillListItem(Labels.getLabel("listcell_totalTdsPayNow.label"), paymentMap.get("totalTds"), finFormatter);
			this.listheader_Tds.setVisible(true);
		}else{
			this.listheader_Tds.setVisible(false);
		}
		if (paymentMap.get("totalLatePft").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalLatePft"));
			this.listheader_LatePft.setVisible(true);
			fillListItem(Labels.getLabel("listcell_totalLatePftPayNow.label"), paymentMap.get("totalLatePft"), finFormatter);
		}else{
			this.listheader_LatePft.setVisible(false);
		}
		if (paymentMap.get("totalPri").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("totalPri"));
			fillListItem(Labels.getLabel("listcell_totalPriPayNow.label"), paymentMap.get("totalPri"), finFormatter);
		}

		if (paymentMap.get("insPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("insPaid"));
			this.listheader_InsPayment.setVisible(true);
			fillListItem(Labels.getLabel("listcell_insFeePayNow.label"), paymentMap.get("insPaid"), finFormatter);
		}else{
			this.listheader_InsPayment.setVisible(false);
		}
		if (paymentMap.get("schdFeePaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdFeePaid"));
			this.listheader_SchdFee.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdFeePayNow.label"), paymentMap.get("schdFeePaid"), finFormatter);
		}else{
			this.listheader_SchdFee.setVisible(false);
		}
		if (paymentMap.get("schdSuplRentPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdSuplRentPaid"));
			this.listheader_SuplRent.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdSuplRentPayNow.label"), paymentMap.get("schdSuplRentPaid"), finFormatter);
		}else{
			this.listheader_SuplRent.setVisible(false);
		}
		if (paymentMap.get("schdIncrCostPaid").compareTo(BigDecimal.ZERO) > 0) {
			totalSchAmount = totalSchAmount.add(paymentMap.get("schdIncrCostPaid"));
			this.listheader_IncrCost.setVisible(true);
			fillListItem(Labels.getLabel("listcell_schdIncrCostPayNow.label"), paymentMap.get("schdIncrCostPaid"), finFormatter);
		}else{
			this.listheader_IncrCost.setVisible(false);
		}

		fillListItem(Labels.getLabel("listcell_totalSchAmount.label"), totalSchAmount, finFormatter);

	}

	/**
	 * Method for Showing List Item
	 * 
	 * @param label
	 * @param fieldValue
	 */
	private void fillListItem(String label, BigDecimal fieldValue, int finFormatter) {

		Listcell lc;
		Listitem item;

		item = new Listitem();
		lc = new Listcell();
		lc.setParent(item);
		lc = new Listcell(label);
		lc.setStyle("font-weight:bold;");
		lc.setSpan(2);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(fieldValue, finFormatter));
		lc.setStyle("text-align:right;color:#f36800;");
		lc.setParent(item);
		lc = new Listcell();
		lc.setSpan(12);
		lc.setParent(item);
		this.listBoxPayment.appendChild(item);

	}
	
	/**
	 * Method for Showing Posting Details which are going to be reversed
	 * @param linkedTranId
	 */
	private void doFillPostings(List<ReturnDataSet> postingList) {
		logger.debug("Entering");
		
		if(postingList != null && !postingList.isEmpty()){
			Listitem item;
			for (ReturnDataSet returnDataSet : postingList) {
				item = new Listitem();
				Listcell lc = new Listcell();
				if(returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)){
					lc = new Listcell(Labels.getLabel("common.Debit"));
				}else if(returnDataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)){
					lc = new Listcell(Labels.getLabel("common.Credit"));
				}
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(returnDataSet.getPostAmount(), CurrencyUtil.getFormat(returnDataSet.getAcCcy())));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				this.listBoxPosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doProcess(FinReceiptHeader aReceiptHeader, String tranType) throws InterruptedException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aReceiptHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aReceiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReceiptHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if(aReceiptHeader.getManualAdvise() != null){
			aReceiptHeader.getManualAdvise().setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aReceiptHeader.getManualAdvise().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aReceiptHeader.getManualAdvise().setUserDetails(getUserWorkspace().getLoggedInUser());
		}

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aReceiptHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			
			if(aReceiptHeader.getManualAdvise() != null){
				aReceiptHeader.getManualAdvise().setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReceiptHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReceiptHeader);
				}

				if (isNotesMandatory(taskId, aReceiptHeader)) {
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

			aReceiptHeader.setTaskId(taskId);
			aReceiptHeader.setNextTaskId(nextTaskId);
			aReceiptHeader.setRoleCode(getRole());
			aReceiptHeader.setNextRoleCode(nextRoleCode);
			
			if(aReceiptHeader.getManualAdvise() != null){
				aReceiptHeader.getManualAdvise().setTaskId(taskId);
				aReceiptHeader.getManualAdvise().setNextTaskId(nextTaskId);
				aReceiptHeader.getManualAdvise().setRoleCode(getRole());
				aReceiptHeader.getManualAdvise().setNextRoleCode(nextRoleCode);
			}

			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aReceiptHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReceiptHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aReceiptHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinReceiptHeader aReceiptHeader = (FinReceiptHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getReceiptCancellationService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getReceiptCancellationService().doApprove(auditHeader);

						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getReceiptCancellationService().doReject(auditHeader);
						if (aReceiptHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReceiptCancellationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ReceiptCancellationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						//deleteNotes(getNotes(), true);
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

		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinReceiptHeader receiptHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, receiptHeader);
		return new AuditHeader(String.valueOf(receiptHeader.getReceiptID()), null, null, null, auditDetail, receiptHeader.getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(getReceiptHeader());
	}

	@Override
	protected String getReference() {
		return String.valueOf(getReceiptHeader().getReceiptID());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}
	public void setReceiptHeader(FinReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public ReceiptCancellationService getReceiptCancellationService() {
		return receiptCancellationService;
	}
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

}