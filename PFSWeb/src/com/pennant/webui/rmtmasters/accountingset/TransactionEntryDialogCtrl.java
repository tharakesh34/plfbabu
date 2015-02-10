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
 * FileName    		:  TransactionEntryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountingset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/RulesFactory/TransactionEntry/transactionEntryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class TransactionEntryDialogCtrl extends GFCBaseListCtrl<TransactionEntry> implements Serializable {

	private static final long	                     serialVersionUID	     = 4345607610334573882L;
	private final static Logger	                     logger	                 = Logger.getLogger(TransactionEntryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window	   window_TransactionEntryDialog;	// autowired
	protected Intbox	   transOrder;	                	// autowired
	protected Textbox	   transDesc;	                    // autowired
	protected Combobox	   debitcredit;	                	// autowired
	protected Checkbox	   shadowPosting;	                // autowired
	protected Combobox	   account;	                    	// autowired
	protected ExtendedCombobox	   accountType;	                	// autowired
	protected ExtendedCombobox	   accountBranch;	                // autowired
	protected ExtendedCombobox	   accountSubHeadRule;	        	// autowired
	protected ExtendedCombobox	   transcationCode;	            	// autowired
	protected ExtendedCombobox	   rvsTransactionCode;	        	// autowired
	protected Codemirror   amountRule;	                	// autowired
	protected Textbox	   eventCode;	                    // autowired
	protected Textbox	   accountSetCode;	            	// autowired
	protected Textbox	   accountSetCodeName;	        	// autowired
	protected Textbox	   lovDescEventCodeName;	        // autowired
	protected Checkbox	   entryByInvestment;	        	// autowired
	protected Label 	   label_TransactionEntryDialog_EntryByInvestment;
	protected Hbox	   	   hbox_entryByInvestment;	        // autowired
	protected Checkbox	   openNewFinAc;	        		// autowired
	protected Row	   	   row_OpenNewFinAc;	        	// autowired

	protected Label	       recordStatus;	                // autowired
	protected Radiogroup   userAction;
	protected Groupbox	   groupboxWf;
	protected Row	       statusRow;

	// not auto wired vars
	private TransactionEntry transactionEntry;	           // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient int	    oldVar_transOrder;
	private transient String	oldVar_transDesc;
	private transient boolean	oldVar_shadowPosting;
	private transient boolean	oldVar_entryByInvestment;
	private transient boolean	oldVar_openNewFinAc;
	private transient String	oldVar_account;
	private transient String	oldVar_accountType;
	private transient String	oldVar_accountBranch;
	private transient String	oldVar_accountSubHeadRule;
	private transient String	oldVar_transcationCode;
	private transient String	oldVar_rvsTransactionCode;
	private transient String	oldVar_amountRule;
	private transient String	oldVar_recordStatus;

	private transient boolean	validationOn;
	private boolean	            notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_TransactionEntryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp;	// autowire
	protected Button btnNotes; 	// autowire

	private transient String oldVar_lovDescAccountTypeName;

	private transient String oldVar_lovDescAccountBranchName;

	private transient String oldVar_lovDescAccountSubHeadRuleName;

	private transient String oldVar_lovDescTranscationCodeName;

	private transient String oldVar_lovDescRvsTransactionCodeName;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ValueLabel> listDebitcredit = PennantStaticListUtil.getTranType(); // autowired

	private transient AccountingSetService accountingSetService;
	private transient AccountingSetDialogCtrl accountingSetDialogCtrl;
	private List<TransactionEntry> transactionEntryList;

	protected Listbox amountCodeListbox; // auto wired
	protected Listbox feeCodeListbox; // auto wired
	protected Listbox operator; // auto wired
	protected Button btnCopyTo;
	ArrayList<ValueLabel> operatorValues = PennantStaticListUtil.getMathBasicOperator();

	protected Grid	 grid_Basicdetails;
	protected Column column_CustomerData;
	protected Column column_RULE;
	protected Column column_Operators;
	protected Button btnSimulate;
	protected Button btnValidate;
	JSONArray variables = new JSONArray();
	HashSet<String> amountcodes = new HashSet<String>();
	protected ExtendedCombobox systemIntAccount;

	protected Radiogroup chargeType;
	ArrayList<ValueLabel> chargeTypes = PennantStaticListUtil.getChargeTypes();
	
	//protected Combobox ruleDecider;
	protected Groupbox gb_RuleCode;

	/**
	 * default constructor.<br>
	 */
	public TransactionEntryDialogCtrl() {
		super();

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected TransactionEntry object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionEntryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
		        this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("transactionEntry")) {
			this.transactionEntry = (TransactionEntry) args.get("transactionEntry");
			TransactionEntry befImage = new TransactionEntry();
			BeanUtils.copyProperties(this.transactionEntry, befImage);
			this.transactionEntry.setBefImage(befImage);
			setTransactionEntry(this.transactionEntry);
		} else {
			setTransactionEntry(null);
		}

		this.transactionEntry.setWorkflowId(0);

		doLoadWorkFlow(this.transactionEntry.isWorkflow(), this.transactionEntry.getWorkflowId(), 
				this.transactionEntry.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "TransactionEntryDialog");
		}
		
		if (args.containsKey("role")) {
			getUserWorkspace().alocateRoleAuthorities(args.get("role").toString(), "TransactionEntryDialog");	
		}
		// READ OVERHANDED params !
		// we get the transactionEntryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete transactionEntry here.
		if (args.containsKey("accountingSetDialogCtrl")) {
			setAccountingSetDialogCtrl((AccountingSetDialogCtrl) args.get("accountingSetDialogCtrl"));
		} else {
			setAccountingSetDialogCtrl(null);
		}
		
		getBorderLayoutHeight();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 115; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		this.amountCodeListbox.setHeight((listboxHeight+15)+"px");
		this.feeCodeListbox.setHeight((listboxHeight+15)+"px");
		this.amountRule.setHeight((listboxHeight+40)+"px");
		this.operator.setHeight((listboxHeight+15)+"px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getTransactionEntry());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.transOrder.setMaxlength(3);
		this.transDesc.setMaxlength(50);
		this.accountType.setMaxlength(8);
		this.accountBranch.setMaxlength(8);
		this.accountSubHeadRule.setMaxlength(8);
		this.transcationCode.setMaxlength(8);
		this.rvsTransactionCode.setMaxlength(8);
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", "SUBHEAD", Filter.OP_EQUAL);
		this.accountSubHeadRule.setModuleName("Rule");
		this.accountSubHeadRule.setValueColumn("RuleCode");
		this.accountSubHeadRule.setDescColumn("RuleCodeDesc");
		this.accountSubHeadRule.setFilters(filter);
		this.accountSubHeadRule.setValidateColumns(new String[]{"RuleCode"});
		
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CustSysAc", "1", Filter.OP_EQUAL);
		this.accountType.setModuleName("AccountType");
		this.accountType.setValueColumn("AcType");
		this.accountType.setDescColumn("AcTypeDesc");
		this.accountType.setFilters(filters);
		this.accountType.setValidateColumns(new String[]{"AcType"});
		
		this.systemIntAccount.setModuleName("SystemInternalAccountDefinition");
		this.systemIntAccount.setValueColumn("SIACode");
		this.systemIntAccount.setDescColumn("SIAName");
		this.systemIntAccount.setValidateColumns(new String[]{"SIACode"});
		
		this.accountBranch.setModuleName("Branch");
		this.accountBranch.setValueColumn("BranchCode");
		this.accountBranch.setDescColumn("BranchDesc");
		this.accountBranch.setValidateColumns(new String[]{"BranchCode"});
		
		this.transcationCode.setModuleName("TransactionCode");
		this.transcationCode.setValueColumn("TranCode");
		this.transcationCode.setValidateColumns(new String[]{"TransactionCode"});
		this.transcationCode.setDescColumn("TranDesc");
		
		this.rvsTransactionCode.setModuleName("TransactionCode");
		this.rvsTransactionCode.setValueColumn("TranCode");
		this.rvsTransactionCode.setValidateColumns(new String[]{"TransactionCode"});
		this.rvsTransactionCode.setDescColumn("TranDesc");
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving");
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

		getUserWorkspace().alocateAuthorities("TransactionEntryDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_TransactionEntryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnValidateSave(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			doSave();
		}
		
		/*if("PROVSN".equals(this.eventCode.getValue()) || "REFUND".equals(this.eventCode.getValue())){
			doSave();
		}*/
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_TransactionEntryDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			window_TransactionEntryDialog.onClose();
			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aTransactionEntry
	 *            TransactionEntry
	 */
	public void doWriteBeanToComponents(TransactionEntry aTransactionEntry) {
		logger.debug("Entering");

		fillComboBox(debitcredit, aTransactionEntry.getDebitcredit(), listDebitcredit);
		dofillAccount(this.account, aTransactionEntry.getAccount());
		doFillAccountType(aTransactionEntry.getAccount());
		//doFillRuleDecider(this.ruleDecider, aTransactionEntry.getRuleDecider());
		doFillChargeType(this.chargeType, aTransactionEntry.getChargeType());
		this.eventCode.setValue(aTransactionEntry.getLovDescEventCodeName());
		this.lovDescEventCodeName.setValue(aTransactionEntry.getLovDescEventCodeDesc());
		this.accountSetCode.setValue(aTransactionEntry.getLovDescAccSetCodeName());
		this.accountSetCodeName.setValue(aTransactionEntry.getLovDescAccSetCodeDesc());
		this.transOrder.setValue(aTransactionEntry.getTransOrder());
		this.transDesc.setValue(aTransactionEntry.getTransDesc());
		this.shadowPosting.setChecked(aTransactionEntry.isShadowPosting());
		this.entryByInvestment.setChecked(aTransactionEntry.isEntryByInvestment());
		this.openNewFinAc.setChecked(aTransactionEntry.isOpenNewFinAc());
		this.accountType.setValue(aTransactionEntry.getAccountType());
		this.accountBranch.setValue(aTransactionEntry.getAccountBranch());
		this.accountSubHeadRule.setValue(aTransactionEntry.getAccountSubHeadRule());
		
		// Amount Rule Formula Set to CodeMirror
		this.amountRule.setValue(aTransactionEntry.getAmountRule());
		
		/*if(this.eventCode.getValue().equals("LATEPAY")){
			this.btnDelete.setVisible(false);
		}*/
		
		this.transcationCode.setValue(aTransactionEntry.getTranscationCode());
		this.rvsTransactionCode.setValue(aTransactionEntry.getRvsTransactionCode());

		if (aTransactionEntry.getLovDescTranscationCodeName() != null && !aTransactionEntry.getLovDescTranscationCodeName().equals("")) {
			this.transcationCode.setDescription( aTransactionEntry.getLovDescTranscationCodeName().contains("-")? aTransactionEntry.getLovDescTranscationCodeName()
					: aTransactionEntry.getLovDescTranscationCodeName());
		} else {
			this.transcationCode.setDescription("");
		}
		if (aTransactionEntry.getLovDescRvsTransactionCodeName() != null && !aTransactionEntry.getLovDescRvsTransactionCodeName().equals("")) {
			this.rvsTransactionCode.setDescription(aTransactionEntry.getLovDescRvsTransactionCodeName().contains("-")?aTransactionEntry.getLovDescRvsTransactionCodeName()
					:aTransactionEntry.getLovDescRvsTransactionCodeName());
		} else {
			this.rvsTransactionCode.setDescription("");
		}
		
		if (aTransactionEntry.getLovDescAccountTypeName() != null && !aTransactionEntry.getLovDescAccountTypeName().equals("")) {
			this.accountType.setDescription(aTransactionEntry.getLovDescAccountTypeName().contains("-")?aTransactionEntry.getLovDescAccountTypeName()
					:aTransactionEntry.getLovDescAccountTypeName());
		} else if (aTransactionEntry.getLovDescSysInAcTypeName() != null && !aTransactionEntry.getLovDescSysInAcTypeName().equals("")) {
			this.accountType.setDescription(aTransactionEntry.getLovDescSysInAcTypeName().contains("-")?aTransactionEntry.getLovDescSysInAcTypeName()
					:aTransactionEntry.getLovDescSysInAcTypeName());
		} else {
			this.accountType.setDescription("");
		}
				
		if (aTransactionEntry.getLovDescAccountBranchName() != null && !aTransactionEntry.getLovDescAccountBranchName().equals("")) {
			this.accountBranch.setDescription(aTransactionEntry.getLovDescAccountBranchName().contains("-")?aTransactionEntry.getLovDescAccountBranchName():
				aTransactionEntry.getLovDescAccountBranchName());
		} else {
			this.accountBranch.setDescription("");
		}
		if (aTransactionEntry.getLovDescAccountSubHeadRuleName() != null && !aTransactionEntry.getLovDescAccountSubHeadRuleName().equals("")) {
			this.accountSubHeadRule.setDescription(aTransactionEntry.getLovDescAccountSubHeadRuleName().contains("-")?aTransactionEntry.getLovDescAccountSubHeadRuleName()
					:aTransactionEntry.getLovDescAccountSubHeadRuleName());
		} else {
			this.accountSubHeadRule.setDescription("");
		}
		
		/*if(this.accountingSetDialogCtrl.entryByInvestment.isChecked()){
			this.ruleDecider.setSelectedIndex(0);
			this.ruleDecider.setDisabled(true);
		}*/
		
		// Fill AmountCode And Operators
		fillListbox(this.operator, operatorValues, true);
		doCheckRuleDecider();
		
		this.recordStatus.setValue(aTransactionEntry.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTransactionEntry
	 */
	public void doWriteComponentsToBean(TransactionEntry aTransactionEntry) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aTransactionEntry.setLovDescEventCodeName(this.eventCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeName(this.accountSetCode.getValue());
		aTransactionEntry.setLovDescAccSetCodeDesc(this.accountSetCodeName.getValue());

		try {
			aTransactionEntry.setTransOrder(this.transOrder.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setTransDesc(this.transDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setDebitcredit(this.debitcredit.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setShadowPosting(this.shadowPosting.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setEntryByInvestment(this.entryByInvestment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setOpenNewFinAc(this.openNewFinAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setChargeType(this.chargeType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setAccount(this.account.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountTypeName(this.accountType.getDescription());
			aTransactionEntry.setAccountType(this.accountType.getValue());
			if(this.accountType.getValue().equals("")){
				aTransactionEntry.setLovDescSysInAcTypeName("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountBranchName(this.accountBranch.getDescription());
			aTransactionEntry.setAccountBranch(this.accountBranch.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescAccountSubHeadRuleName(this.accountSubHeadRule.getDescription());
			aTransactionEntry.setAccountSubHeadRule(this.accountSubHeadRule.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescTranscationCodeName(this.transcationCode.setDescription());
			aTransactionEntry.setTranscationCode(this.transcationCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTransactionEntry.setLovDescRvsTransactionCodeName(this.rvsTransactionCode.getDescription());
			aTransactionEntry.setRvsTransactionCode(this.rvsTransactionCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//aTransactionEntry.setRuleDecider(this.ruleDecider.getSelectedItem().getValue().toString());

		try {
			if (this.amountRule.getValue().equals("")) {
				throw new WrongValueException(this.amountRule, Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_TransactionEntryDialog_AmountRule.value") }));
			}
			aTransactionEntry.setAmountRule(this.amountRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aTransactionEntry.setFeeCode("");
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aTransactionEntry.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aTransactionEntry
	 * @throws InterruptedException
	 */
	public void doShowDialog(TransactionEntry aTransactionEntry) throws InterruptedException {
		logger.debug("Entering");

		// if aTransactionEntry == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aTransactionEntry == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aTransactionEntry = getAccountingSetService().getNewTransactionEntry();
			setTransactionEntry(aTransactionEntry);
		} else {
			setTransactionEntry(aTransactionEntry);
		}
	

		// set ReadOnly mode accordingly if the object is new or not.
		if (aTransactionEntry.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.transDesc.focus();

		} else {
			this.transDesc.focus();
			doEdit();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);

			} else {
				this.btnCtrl.setInitEdit();
				// doReadOnly();
				this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionEntryDialog_btnSave"));
				this.btnEdit.setVisible(false);

			}

		}
		btnCancel.setVisible(false);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aTransactionEntry);
			if(getTransactionEntry().isNewRecord()){
				this.transcationCode.setReadonly(true);
				this.transcationCode.setMandatoryStyle(true);
				this.rvsTransactionCode.setReadonly(true);
				this.rvsTransactionCode.setMandatoryStyle(true);
			}
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(false);
			getAccountingSetDialogCtrl().window_AccountingSetDialog.getParent().appendChild(window_TransactionEntryDialog);
			// setDialog(this.window_TransactionEntryDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_transOrder = this.transOrder.intValue();
		this.oldVar_transDesc = this.transDesc.getValue();
		this.oldVar_shadowPosting = this.shadowPosting.isChecked();
		this.oldVar_entryByInvestment = this.entryByInvestment.isChecked();
		this.oldVar_openNewFinAc = this.openNewFinAc.isChecked();
		this.oldVar_accountType = this.accountType.getValue();
		this.oldVar_accountBranch = this.accountBranch.getValue();
		this.oldVar_lovDescAccountTypeName = this.accountType.getDescription();
		this.oldVar_accountSubHeadRule = this.accountSubHeadRule.getValue();
		this.oldVar_lovDescAccountSubHeadRuleName = this.accountSubHeadRule.getDescription();
		this.oldVar_transcationCode = this.transcationCode.getValue();
		this.oldVar_lovDescTranscationCodeName = this.transcationCode.getDescription();
		this.oldVar_rvsTransactionCode = this.rvsTransactionCode.getValue();
		this.oldVar_lovDescRvsTransactionCodeName = this.rvsTransactionCode.getDescription();
		this.oldVar_amountRule = this.amountRule.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.transOrder.setValue(this.oldVar_transOrder);
		this.transDesc.setValue(this.oldVar_transDesc);
		this.shadowPosting.setChecked(this.oldVar_shadowPosting);
		this.entryByInvestment.setChecked(this.oldVar_entryByInvestment);
		this.openNewFinAc.setChecked(this.oldVar_openNewFinAc);
		this.account.setValue(this.oldVar_account);
		this.accountType.setValue(this.oldVar_accountType);
		this.accountBranch.setValue(this.oldVar_accountBranch);
		this.accountType.setDescription(this.oldVar_lovDescAccountTypeName);
		this.accountBranch.setDescription(this.oldVar_lovDescAccountBranchName);
		this.accountSubHeadRule.setValue(this.oldVar_accountSubHeadRule);
		this.accountSubHeadRule.setDescription(this.oldVar_lovDescAccountSubHeadRuleName);
		this.transcationCode.setValue(this.oldVar_transcationCode);
		this.transcationCode.setDescription(this.oldVar_lovDescTranscationCodeName);
		this.rvsTransactionCode.setValue(this.oldVar_rvsTransactionCode);
		this.rvsTransactionCode.setDescription(this.oldVar_lovDescRvsTransactionCodeName);
		this.amountRule.setValue(this.oldVar_amountRule);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_transOrder != this.transOrder.intValue()) {
			return true;
		}
		if (this.oldVar_transDesc != this.transDesc.getValue()) {
			return true;
		}
		if (this.oldVar_shadowPosting != this.shadowPosting.isChecked()) {
			return true;
		}
		if (this.oldVar_entryByInvestment != this.entryByInvestment.isChecked()) {
			return true;
		}
		if (this.oldVar_openNewFinAc != this.openNewFinAc.isChecked()) {
			return true;
		}
		if (this.oldVar_accountType != this.accountType.getValue()) {
			return true;
		}
		if (this.oldVar_accountBranch != this.accountBranch.getValue()) {
			return true;
		}
		if (this.oldVar_accountSubHeadRule != this.accountSubHeadRule.getValue()) {
			return true;
		}
		if (this.oldVar_transcationCode != this.transcationCode.getValue()) {
			return true;
		}
		if (this.oldVar_rvsTransactionCode != this.rvsTransactionCode.getValue()) {
			return true;
		}
		if (this.oldVar_amountRule != this.amountRule.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.transOrder.isReadonly()) {
			this.transOrder.setConstraint(new IntValidator(10, Labels.getLabel("label_TransactionEntryDialog_TransOrder.value")));
		}
		if (!this.transDesc.isReadonly()) {
			this.transDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionEntryDialog_TransDesc.value"),null,true));
		}
		if (!this.debitcredit.isDisabled()) {
			this.debitcredit.setConstraint(new StaticListValidator(listDebitcredit, Labels.getLabel("label_TransactionEntryDialog_Debitcredit.value")));
		}
		if (!this.account.isDisabled()) {
			this.account.setConstraint(new StaticListValidator(PennantStaticListUtil.getTransactionalAccount(
					this.accountingSetDialogCtrl.entryByInvestment.isChecked()), Labels.getLabel("label_TransactionEntryDialog_Account.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.transOrder.setConstraint("");
		this.transDesc.setConstraint("");
		this.debitcredit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		if (this.account.getSelectedItem() != null) {

			if (this.account.getSelectedItem().getValue().toString().equals(PennantConstants.GLNPL)
					|| this.account.getSelectedItem().getValue().toString().equals(PennantConstants.CUSTSYS)
					|| this.account.getSelectedItem().getValue().toString().equals(PennantConstants.BUILD)) {

				this.accountType.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionEntryDialog_AccountType.value"),null,true,true));

				if (this.account.getSelectedItem().getValue().toString().equals(PennantConstants.GLNPL)
						|| this.account.getSelectedItem().getValue().toString().equals(PennantConstants.BUILD)) {
					this.accountSubHeadRule.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionEntryDialog_AccountSubHeadRule.value"),null,true,true));

				}

			}
		}
		
		this.transcationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionEntryDialog_TranscationCode.value"),null,true,true));
		
		this.rvsTransactionCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionEntryDialog_RvsTransactionCode.value"),null,true,true));
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints to the LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.accountType.setConstraint("");
		this.accountSubHeadRule.setConstraint("");
		this.transcationCode.setConstraint("");
		this.rvsTransactionCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the Error Messages
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.transOrder.setErrorMessage("");
		this.transDesc.setErrorMessage("");
		this.debitcredit.setErrorMessage("");
		this.accountType.setErrorMessage("");
		this.accountSubHeadRule.setErrorMessage("");
		this.transcationCode.setErrorMessage("");
		this.rvsTransactionCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a TransactionEntry object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final TransactionEntry aTransactionEntry = new TransactionEntry();
		BeanUtils.copyProperties(getTransactionEntry(), aTransactionEntry);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aTransactionEntry.getTransOrder() + ":"
		        + aTransactionEntry.getTransDesc();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aTransactionEntry.getRecordType()).equals("")) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTransactionEntry.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aTransactionEntry.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFeeProcess(aTransactionEntry, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_TransactionEntryDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getAccountingSetDialogCtrl().doFilllistbox(this.transactionEntryList);
					window_TransactionEntryDialog.onClose();
					getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new TransactionEntry object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final TransactionEntry aTransactionEntry = getAccountingSetService().getNewTransactionEntry();
		setTransactionEntry(aTransactionEntry);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.transDesc.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getTransactionEntry().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.transOrder.setReadonly(false);
		} else {
			this.btnCancel.setVisible(true);
			this.transOrder.setReadonly(true);
		}

		this.transDesc.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_transDesc"));
		this.debitcredit.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_debitcredit"));
		this.shadowPosting.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_shadowPosting"));
		this.entryByInvestment.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_entryByInvestment"));
		this.openNewFinAc.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_openNewFinAc"));
		this.account.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_account"));
		//this.ruleDecider.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_ruleDecider"));
		this.accountBranch.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountBranch"));
		this.accountType.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.systemIntAccount.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
		this.accountSubHeadRule.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountSubHeadEule"));
		this.transcationCode.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_transcationCode"));
		this.rvsTransactionCode.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_rvsTransactionCode"));
		
		if(!this.accountingSetDialogCtrl.entryByInvestment.isChecked()){
			this.label_TransactionEntryDialog_EntryByInvestment.setVisible(false);
			this.hbox_entryByInvestment.setVisible(false);
			this.entryByInvestment.setChecked(false);
		}

		//Only for LatePay
		/*if(!getTransactionEntry().isNewRecord() && getTransactionEntry().getLovDescEventCodeName().equals("LATEPAY")){
			this.debitcredit.setDisabled(true);
			this.shadowPosting.setDisabled(true);
			this.account.setDisabled(true);
			this.ruleDecider.setDisabled(true);
			this.btnSearchAccountBranch.setVisible(false);
			this.btnSearchAccountType.setVisible(false);
			this.btnSearchAccountSubHeadRule.setVisible(false);
		}
		
		 
		|| (!getTransactionEntry().isNewRecord() && getTransactionEntry().getLovDescEventCodeName().equals("LATEPAY"))*/
		
		if (getUserWorkspace().isReadOnly("TransactionEntryDialog_amountRule")) {
			this.amountRule.setReadonly(true);
			this.column_CustomerData.setWidth("0%");
			this.amountCodeListbox.setVisible(false);
			this.feeCodeListbox.setVisible(false);
			this.column_RULE.setWidth("100%");
			this.column_Operators.setWidth("0%");
			this.operator.setVisible(false);

		} else {
			this.amountRule.setReadonly(false);
			this.column_CustomerData.setWidth("30%");
			this.amountCodeListbox.setVisible(true);
			this.feeCodeListbox.setVisible(true);
			this.column_RULE.setWidth("50%");
			this.column_Operators.setWidth("20%");
			this.operator.setVisible(true);

		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.transactionEntry.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.transOrder.setReadonly(true);
		this.transDesc.setReadonly(true);
		this.debitcredit.setDisabled(true);
		this.shadowPosting.setDisabled(true);
		this.entryByInvestment.setDisabled(true);
		this.openNewFinAc.setDisabled(true);
		this.account.setDisabled(true);
		this.accountType.setReadonly(true);
		this.accountBranch.setReadonly(true);
		this.accountSubHeadRule.setReadonly(true);
		this.transcationCode.setReadonly(true);
		this.rvsTransactionCode.setReadonly(true);
		this.amountRule.setReadonly(true);
		this.column_CustomerData.setWidth("0%");
		this.amountCodeListbox.setVisible(false);
		this.feeCodeListbox.setVisible(false);
		this.column_RULE.setWidth("100%");
		this.column_Operators.setWidth("0%");
		this.operator.setVisible(false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.transOrder.setText("");
		this.transDesc.setValue("");
		this.debitcredit.setValue("");
		this.shadowPosting.setChecked(false);
		this.entryByInvestment.setChecked(false);
		this.openNewFinAc.setChecked(false);
		this.accountType.setValue("");
		this.accountBranch.setValue("");
		this.accountType.setDescription("");
		this.accountBranch.setDescription("");
		this.accountSubHeadRule.setValue("");
		this.transcationCode.setValue("");
		this.transcationCode.setDescription("");
		this.rvsTransactionCode.setValue("");
		this.rvsTransactionCode.setDescription("");
		this.amountRule.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final TransactionEntry aTransactionEntry = new TransactionEntry();
		BeanUtils.copyProperties(getTransactionEntry(), aTransactionEntry);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the TransactionEntry object with the components data
		doWriteComponentsToBean(aTransactionEntry);

		// Write the additional validations as per below example
		// get the selected branch object from the lisBox
		// Do data level validations here

		isNew = aTransactionEntry.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aTransactionEntry.getRecordType()).equals("")) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				if (isNew) {
					aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTransactionEntry.setNewRecord(true);
				}
			}
		} else {

			if (isNew) {
				aTransactionEntry.setVersion(1);
				aTransactionEntry.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.trimToEmpty(aTransactionEntry.getRecordType()).equals("")) {
				aTransactionEntry.setVersion(aTransactionEntry.getVersion() + 1);
				aTransactionEntry.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aTransactionEntry.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFeeProcess(aTransactionEntry, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_TransactionEntryDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getAccountingSetDialogCtrl().doFilllistbox(this.transactionEntryList);
				window_TransactionEntryDialog.onClose();
				getAccountingSetDialogCtrl().window_AccountingSetDialog.setVisible(true);
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFeeProcess(TransactionEntry aTransactionEntry, String tranType) {

		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aTransactionEntry, tranType);
		transactionEntryList = new ArrayList<TransactionEntry>();

		String[] valueParm = new String[3];
		String[] errParm = new String[2];

		valueParm[0] = aTransactionEntry.getLovDescEventCodeName();
		valueParm[1] = aTransactionEntry.getLovDescAccSetCodeName();
		valueParm[2] = String.valueOf(aTransactionEntry.getTransOrder());

		errParm[0] = PennantJavaUtil.getLabel("label_FeeTranEvent") + ":" + valueParm[0] + " " + PennantJavaUtil.getLabel("label_FeeCode") + ":" + valueParm[1];
		errParm[1] = PennantJavaUtil.getLabel("label_TierSlab") + ":" + valueParm[2];
		getAccountingSetDialogCtrl().getTransactionEntryList();
		if (getAccountingSetDialogCtrl().getTransactionEntryList() != null && getAccountingSetDialogCtrl().getTransactionEntryList().size() > 0) {
			for (int i = 0; i < getAccountingSetDialogCtrl().getTransactionEntryList().size(); i++) {
				TransactionEntry transactionEntry = getAccountingSetDialogCtrl().getTransactionEntryList().get(i);

				if (transactionEntry.getLovDescEventCodeName().equals(aTransactionEntry.getLovDescEventCodeName())
				        && transactionEntry.getLovDescAccSetCodeName().equals(aTransactionEntry.getLovDescAccSetCodeName())
				        && transactionEntry.getTransOrder() == aTransactionEntry.getTransOrder()) {
					// Both Current and Existing list rating same

					if (aTransactionEntry.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
						        .getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
						if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							transactionEntryList.add(aTransactionEntry);
						} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aTransactionEntry.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							transactionEntryList.add(aTransactionEntry);
						} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;

							for (int j = 0; j < getAccountingSetDialogCtrl().getAccountingSet().getTransactionEntries().size(); j++) {
								TransactionEntry fee = getAccountingSetDialogCtrl().getAccountingSet().getTransactionEntries().get(j);
								if (fee.getTransOrder() == aTransactionEntry.getTransOrder() && fee.getLovDescEventCodeName().equals(aTransactionEntry.getLovDescEventCodeName())) {
									transactionEntryList.add(fee);
								}
							}
						} else if (aTransactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aTransactionEntry.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							transactionEntryList.add(transactionEntry);
						}
					}
				} else {
					transactionEntryList.add(transactionEntry);
				}
			}
		}
		if (!recordAdded) {
			transactionEntryList.add(aTransactionEntry);
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	public void onFulfill$transcationCode(Event event) {
		logger.debug("Entering" + event.toString());
        doTranscationCode();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$rvsTransactionCode(Event event) {
		logger.debug("Entering" + event.toString());
        dorvsTransactionCode();
		logger.debug("Leaving" + event.toString());
	}
	
	public void doTranscationCode(){
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("TranType", this.debitcredit.getSelectedItem().getValue(), Filter.OP_EQUAL);
			this.transcationCode.setFilters(filter);
	}
	public void dorvsTransactionCode(){
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("TranType", this.debitcredit.getSelectedItem().getValue(), Filter.OP_EQUAL);
			this.rvsTransactionCode.setFilters(filter);
	}
	/**
	 * Method for Fill the ComboBox items with getting list of data
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	private void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering");

		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		for (int i = 0; i < list.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(list.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(list.get(i).getLabel()));
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(list.get(i).getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * To get declared fields from the CustomerEligibilityCheck class
	 */
	private void getAmountCodes(String allowedevent) {
		logger.debug("Entering");
		this.amountCodeListbox.getItems().clear();
		List<AmountCode> amountCodesList = new ArrayList<AmountCode>();
		JdbcSearchObject<AmountCode> searchObj = new JdbcSearchObject<AmountCode>(AmountCode.class);
		searchObj.addTabelName("BMTAmountCodes");
		if (allowedevent != null) {
			searchObj.addFilter(new Filter("AllowedEvent", allowedevent,Filter.OP_EQUAL));
			if(!this.accountingSetDialogCtrl.entryByInvestment.isChecked()){
				searchObj.addFilter(new Filter("AllowedRIA", 0 ,Filter.OP_EQUAL));
			}else{
				searchObj.addFilter(new Filter("AllowedRIA", 1 ,Filter.OP_EQUAL));
			}
		}
		amountCodesList = this.pagedListService.getBySearchObject(searchObj);

		/*if(amountCodesList != null && amountCodesList.size() == 0 &&
				!this.accountingSetDialogCtrl.entryByInvestment.isChecked()){
			this.ruleDecider.setSelectedIndex(1);
			this.ruleDecider.setDisabled(true);
		}*/
		for (int i = 0; i < amountCodesList.size(); i++) {
			Listitem item = new Listitem();
			Listcell lc = new Listcell(amountCodesList.get(i).getAmountCode());
			if (!amountcodes.contains(amountCodesList.get(i).getAmountCode())) {
				amountcodes.add(amountCodesList.get(i).getAmountCode());
			}
			lc.setParent(item);
			lc = new Listcell(amountCodesList.get(i).getAmountCodeDesc());
			lc.setParent(item);
			this.amountCodeListbox.appendChild(item);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for get the list of Rules existing on base of Event
	 * @param allowedevent
	 */
	private void getFeeCodes(String allowedevent) {
		logger.debug("Entering");

		this.feeCodeListbox.getItems().clear();
		List<Rule> feeRulesList = new ArrayList<Rule>();
		JdbcSearchObject<Rule> searchObj = new JdbcSearchObject<Rule>(Rule.class);
		searchObj.addTabelName("Rules");
		
		if(allowedevent.contains("REPAY")){
			searchObj.addFilterIn("RuleModule", new String[]{"FEES","REFUND"});
		}else{
			searchObj.addFilterEqual("RuleModule", "FEES");
		}
		
		if (allowedevent != null) {
			if (allowedevent.contains("ADDDBS")) {
				allowedevent = "ADDDBS";
			}
			if(allowedevent.contains("REPAY")){
				searchObj.addFilterIn("RuleEvent", new String[]{allowedevent,""});
			}else{
				searchObj.addFilterEqual("RuleEvent",allowedevent);
			}
		}
		
		feeRulesList = this.pagedListService.getBySearchObject(searchObj);

		Listitem item = null;
		Listgroup group = null;
		Listcell lc = null;
		
		for (int i = 0; i < feeRulesList.size(); i++) {
			
			String ruleCode =feeRulesList.get(i).getRuleCode();
			String ruleCodeDesc =feeRulesList.get(i).getRuleCodeDesc();
			
			group = new Listgroup(ruleCode);
			this.feeCodeListbox.appendChild(group);
			
			for (int j = 0; j < 3; j++) {
				
				String newRuleCode = ruleCode;
				String newRuleCodeDesc = ruleCodeDesc;
				
				if(j==0){
					newRuleCode = newRuleCode+"_C";
					newRuleCodeDesc = newRuleCodeDesc+" (Calculated Amount)";
				}else if(j==1){
					newRuleCode = newRuleCode+"_W";
					newRuleCodeDesc = newRuleCodeDesc+" (Waiver Amount)";
				}else if(j==2){
					newRuleCode = newRuleCode+"_P";
					newRuleCodeDesc = newRuleCodeDesc+" (Customer Paid)";
				}
				
				item = new Listitem();
				lc = new Listcell(newRuleCode);
				if (!amountcodes.contains(newRuleCode)) {
					amountcodes.add(newRuleCode);
				}
				lc.setParent(item);
				lc = new Listcell(newRuleCodeDesc);
				lc.setParent(item);
				this.feeCodeListbox.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * To fill the list box with two list headers
	 * 
	 * @param listbox
	 * @param arrayList
	 * @param showFirstColumn
	 */
	private void fillListbox(Listbox listbox, ArrayList<ValueLabel> arrayList, boolean showFirstColumn) {
		logger.debug("Entering");
		for (int i = 0; i < arrayList.size(); i++) {
			Listitem item = new Listitem();
			Listcell lc = new Listcell(arrayList.get(i).getValue());
			if (showFirstColumn) {
				lc.setVisible(true);
			} else {
				lc.setVisible(false);
			}
			lc.setParent(item);
			lc = new Listcell(arrayList.get(i).getLabel());
			lc.setParent(item);
			listbox.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			// create a new window for input values
			createSimulationWindow(variables);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnValidate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event)) {
			int answer = Messagebox.show("NO Errors Found ! Proceed With Simulation ?", "Validated", 
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);
			if (answer == Messagebox.YES) {
				// create a new window for input values
				createSimulationWindow(variables);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS
	 * 
	 * @param event
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validate(ForwardEvent event) throws InterruptedException {
		boolean noerrors = false;
		if(StringUtils.trimToEmpty(this.amountRule.getValue()).equals("")){
			return true;
		}
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[0];
			// array of variables
			variables = (JSONArray) data[1];

			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (!variable.get("name").equals("Result")) {
						if (!amountcodes.contains(variable.get("name"))) {
							// if new variables found throw error message
							noerrors = false;
							Messagebox.show("Unknown Variable :" + variable.get("name"), "Unknown", Messagebox.OK, Messagebox.ERROR);
						return noerrors;
						} else {
							noerrors = true;
						}
					}
				}
				if (noerrors) {
					return validateResult();
				}

			} else {
				for (int i = 0; i < errors.size(); i++) {
					JSONObject error = (JSONObject) errors.get(i);
					if (error != null) {
						Messagebox.show(error.get("reason").toString(), "Error : At Line " + error.get("line") + ",Position " + error.get("character"), Messagebox.OK,
						        Messagebox.ERROR);
					}
				}
			}
		} else {
			return true;
		}
		return noerrors;
	}

	/**
	 * CALL THE RESULT ZUL FILE
	 * 
	 * @param jsonArray
	 * @throws InterruptedException
	 */
	public void createSimulationWindow(JSONArray jsonArray) throws InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("RuleVariables", jsonArray);
		map.put("transactionEntryDialogCtrl", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/TransactionEntry/TransactionEntryRuleResult.zul", null, map);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private boolean validateResult() throws InterruptedException {
		if (this.amountRule.getValue().contains("Result")) {
			if (this.amountRule.getValue().contains("{")) {
				Messagebox.show("Logical Operators Not Allowed", "Logical Operators", Messagebox.OK, Messagebox.ERROR);
				return false;
			}

		} else {
			Messagebox.show("Result not found ", "Result", Messagebox.OK, Messagebox.ERROR);
			return false;
		}
		return true;
	}

	public void dofillAccount(Combobox combobox, String value) {
		logger.debug("Entering");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		List<ValueLabel> list = PennantStaticListUtil.getTransactionalAccount(
				this.accountingSetDialogCtrl.entryByInvestment.isChecked());
		for (int i = 0; i < list.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(list.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(list.get(i).getLabel()));
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(list.get(i).getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}

		logger.debug("Leaving");
	}

	/*public void doFillRuleDecider(Combobox combobox, String value) {
		logger.debug("Entering");
		combobox.getChildren().clear();
		List<ValueLabel> list = PennantStaticListUtil.getRuleDecider();
		for (int i = 0; i < list.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(list.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(list.get(i).getLabel()));
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(list.get(i).getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		if (combobox.getSelectedItem() == null) {
			combobox.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}*/
	
	public void doFillChargeType(Radiogroup radiogroup, String value) {
		logger.debug("Entering");
		radiogroup.getChildren().clear();
		for (int i = 0; i < chargeTypes.size(); i++) {
			Radio radio = new Radio();
			radio.setValue(StringUtils.trim(chargeTypes.get(i).getValue()));
			radio.setLabel(StringUtils.trim(chargeTypes.get(i).getLabel()));
			radio.setDisabled(getUserWorkspace().isReadOnly("TransactionEntryDialog_chargeType"));
			radiogroup.appendChild(radio);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(chargeTypes.get(i).getValue()))) {
				radiogroup.setSelectedItem(radio);
			}
		}
		if (radiogroup.getSelectedItem() == null) {
			radiogroup.setSelectedIndex(0);
		}
		
		logger.debug("Leaving");
	}

	public void onChange$debitcredit(Event event) {
		logger.debug("Entering");
		if (this.debitcredit.getSelectedItem() != null) {
			this.transcationCode.setValue("");
			this.transcationCode.setDescription("");
			this.rvsTransactionCode.setValue("");
			this.rvsTransactionCode.setDescription("");
			if (!this.debitcredit.getSelectedItem().getValue().equals("#")) {
				this.transcationCode.setReadonly(isReadOnly("TransactionEntryDialog_transcationCode"));
				this.rvsTransactionCode.setReadonly(isReadOnly("TransactionEntryDialog_transcationCode"));
				/*if(this.eventCode.getValue().equals("LATEPAY")){
					transCodeChange();
				}*/
			}else{
				this.transcationCode.setReadonly(true);
				this.rvsTransactionCode.setReadonly(true);
			}
		} else {
			this.transcationCode.setReadonly(true);
			this.rvsTransactionCode.setReadonly(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Transaction Entry Data From SMTParameters
	 */
	/*private void transCodeChange(){
		
		PFSParameter debitCode = SystemParameterDetails.getSystemParameterObject("ODC_DTCD");
		PFSParameter creditCode = SystemParameterDetails.getSystemParameterObject("ODC_CTCD");
		if(this.debitcredit.getSelectedItem().getValue().toString().equals("D")){
			this.transcationCode.setValue(debitCode.getSysParmValue());
			this.lovDescTranscationCodeName.setValue(debitCode.getSysParmValue()+"-"+debitCode.getSysParmDesc());
			this.rvsTransactionCode.setValue(creditCode.getSysParmValue());
			this.lovDescRvsTransactionCodeName.setValue(creditCode.getSysParmValue()+"-"+creditCode.getSysParmDesc());
		}else{
			this.transcationCode.setValue(creditCode.getSysParmValue());
			this.lovDescTranscationCodeName.setValue(creditCode.getSysParmValue()+"-"+creditCode.getSysParmDesc());
			this.rvsTransactionCode.setValue(debitCode.getSysParmValue());
			this.lovDescRvsTransactionCodeName.setValue(debitCode.getSysParmValue()+"-"+debitCode.getSysParmDesc());
		}
		
	}*/
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(TransactionEntry aTransactionEntry, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTransactionEntry.getBefImage(), aTransactionEntry);
		return new AuditHeader(String.valueOf(aTransactionEntry.getAccountSetid()), null, null, null, auditDetail, aTransactionEntry.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_TransactionEntryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("TransactionEntry");
		notes.setReference(String.valueOf(getTransactionEntry().getAccountSetid()));
		notes.setVersion(getTransactionEntry().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	public void onChange$account(Event event) {
		if (this.account.getSelectedItem() != null) {
			doFillAccountType(this.account.getSelectedItem().getValue().toString());
		}
	}

	private void doFillAccountType(String value) {
		logger.debug("Entering");
		
		this.openNewFinAc.setChecked(false);
		this.row_OpenNewFinAc.setVisible(false);
		this.accountType.setReadonly(true);
		this.systemIntAccount.setReadonly(true);
		this.accountSubHeadRule.setReadonly(true);
		this.accountSubHeadRule.setDescription("");
		this.accountType.setDescription("");
		this.accountType.setMandatoryStyle(false);

		if (value != null && !value.equals("#")) {
			
			if(value.equals(PennantConstants.DISB) || value.equals(PennantConstants.REPAY) ||
					value.equals(PennantConstants.INVSTR) || value.equals(PennantConstants.DOWNPAY)){
				
				this.accountType.setReadonly(true);
				this.accountSubHeadRule.setMandatoryStyle(false);
				this.accountType.setMandatoryStyle(false);
				this.accountSubHeadRule.setReadonly(true);
				this.systemIntAccount.setReadonly(true);
				this.accountType.setValue("");
				this.accountType.setDescription("");
				this.systemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.accountSubHeadRule.setDescription("");
				this.openNewFinAc.setChecked(false);
				this.row_OpenNewFinAc.setVisible(false);
				
			} else if (value.equals(PennantConstants.GLNPL) || value.equals(PennantConstants.BUILD)) {
				
				this.accountType.setVisible(false);
				this.accountType.setValue("");
				this.accountType.setDescription("");
				//this.lovDescAccountTypeName.setValue("");
				this.accountType.setMandatoryStyle(false);
				this.accountSubHeadRule.setMandatoryStyle(true);
				this.systemIntAccount.setMandatoryStyle(true);
				this.accountSubHeadRule.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountSubHeadEule"));
				/*if(!getTransactionEntry().isNewRecord() && value.equals(PennantConstants.GLNPL) &&
						getTransactionEntry().getLovDescEventCodeName().equals("LATEPAY")){
					this.btnSearchSystemIntAccount.setVisible(false);
				}else{*/
					this.systemIntAccount.setVisible(true);
					this.systemIntAccount.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
				
				//}
				this.openNewFinAc.setChecked(false);
				this.row_OpenNewFinAc.setVisible(false);
				
			} else if (value.equals(PennantConstants.CUSTSYS)) {
				
				this.accountType.setVisible(true);
				this.accountType.setReadonly(getUserWorkspace().isReadOnly("TransactionEntryDialog_accountType"));
				this.accountSubHeadRule.setMandatoryStyle(false);
				this.accountType.setMandatoryStyle(true);
				this.accountSubHeadRule.setReadonly(true);
				this.systemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.accountSubHeadRule.setDescription("");
				this.row_OpenNewFinAc.setVisible(true);
				
			}else if (value.equals(PennantConstants.UNEARN)) {
				
				this.row_OpenNewFinAc.setVisible(true);
				
			}else if (value.equals(PennantConstants.SUSP)) {
				
				this.row_OpenNewFinAc.setVisible(true);
				
			}else if (value.equals(PennantConstants.PROVSN)) {
				
				this.row_OpenNewFinAc.setVisible(true);
				
			}else if (value.equals(PennantConstants.COMMIT)) {
				
				this.accountType.setReadonly(true);
				this.accountSubHeadRule.setMandatoryStyle(false);
				this.accountType.setMandatoryStyle(false);
				this.accountSubHeadRule.setReadonly(true);
				this.systemIntAccount.setReadonly(true);
				this.accountType.setValue("");
				this.accountType.setDescription("");
				this.systemIntAccount.setVisible(false);
				this.accountSubHeadRule.setValue("");
				this.accountSubHeadRule.setDescription("");
				this.openNewFinAc.setChecked(false);
				this.row_OpenNewFinAc.setVisible(false);
				
			}
			
		}
		logger.debug("Leaving");
	}

	/*public void onChange$ruleDecider(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckRuleDecider(this.ruleDecider.getSelectedItem().getValue().toString());
		logger.debug("Leaving");
	}*/

	private void doCheckRuleDecider() {
		logger.debug("Entering");
		//if (value.equals(PennantConstants.CLAAMT)) {
			this.amountcodes.clear();
			getAmountCodes(getTransactionEntry().getLovDescEventCodeName());
			getFeeCodes(getTransactionEntry().getLovDescEventCodeName());
		//}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public TransactionEntry getTransactionEntry() {
		return this.transactionEntry;
	}

	public void setTransactionEntry(TransactionEntry transactionEntry) {
		this.transactionEntry = transactionEntry;
	}

	public AccountingSetService getAccountingSetService() {
		return accountingSetService;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setAccountingSetDialogCtrl(AccountingSetDialogCtrl accountingSetDialogCtrl) {
		this.accountingSetDialogCtrl = accountingSetDialogCtrl;
	}

	public AccountingSetDialogCtrl getAccountingSetDialogCtrl() {
		return accountingSetDialogCtrl;
	}

}
