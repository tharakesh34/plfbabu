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
 * FileName    		:  CustomerExtLiabilityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

import java.io.Serializable;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.FinanceCustomerListCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerExtLiability/customerExtLiabilityDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerExtLiabilityDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7522534300621535097L;
	private final static Logger logger = Logger.getLogger(CustomerExtLiabilityDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerExtLiabilityDialog; 		// autoWired

	protected Longbox 	custID; 					// autoWired
	protected Textbox 	custCIF;					// autoWired
	protected Label   	custShrtName;				// autoWired
	protected Datebox 	finDate; 					// autoWired
	protected ExtendedCombobox 	finType; 		    // autoWired
	protected Textbox 	finStatus; 					// autoWired
	protected ExtendedCombobox 	bankName; 			// autoWired
	protected CurrencyBox 	originalAmount; 		// autoWired
	protected CurrencyBox 	installmentAmount; 		// autoWired
	protected CurrencyBox 	outStandingBal; 		// autoWired
	protected Intbox liabilitySeq;          	    // autoWired
	
	protected Label recordStatus; 					// autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected South 	south;
	
	// not auto wired variables
	private CustomerExtLiability customerExtLiability; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initial.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_bankName;
	private transient String  		oldVar_finType;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerExtLiabilityDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autoWired
	protected Button btnEdit; 			// autoWired
	protected Button btnDelete; 		// autoWired
	protected Button btnSave; 			// autoWired
	protected Button btnCancel; 		// autoWired
	protected Button btnClose; 			// autoWired
	protected Button btnHelp; 			// autoWired
	protected Button btnNotes; 			// autoWired
	protected Button btnSearchPRCustid; // autoWired

	private transient String oldVar_lovDescBankName;
	private transient String oldVar_lovDescFinType;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerExtLiability> CustomerExtLiabilityList;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String userRole="";
	private FinanceCustomerListCtrl financeCustomerListCtrl;
	private int finFormatter;

	/**
	 * default constructor.<br>
	 */
	public CustomerExtLiabilityDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerExtLiability object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerExtLiabilityDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customerExtLiability")) {
			this.customerExtLiability = (CustomerExtLiability) args.get("customerExtLiability");
			CustomerExtLiability befImage = new CustomerExtLiability();
			BeanUtils.copyProperties(this.customerExtLiability, befImage);
			this.customerExtLiability.setBefImage(befImage);
			setCustomerExtLiability(this.customerExtLiability);
		} else {
			setCustomerExtLiability(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if (args.containsKey("finFormatter")) {
			this.finFormatter = (Integer) args.get("finFormatter");
		}
		
		if(getCustomerExtLiability().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){
			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerExtLiability.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
			}
		}
      if(args.containsKey("financeCustomerListCtrl")){
			
			setFinanceCustomerListCtrl((FinanceCustomerListCtrl) args.get("financeCustomerListCtrl"));
			setNewCustomer(true);
			
			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerExtLiability.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CustomerExtLiabilityDialog");
			}
		}
		doLoadWorkFlow(this.customerExtLiability.isWorkflow(),
				this.customerExtLiability.getWorkflowId(), this.customerExtLiability.getNextTaskId());
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerExtLiabilityDialog");
		}


		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerExtLiability());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bankName.setMaxlength(8);
		this.bankName.setMandatoryStyle(true);
		this.bankName.getTextbox().setWidth("110px");
		this.bankName.setModuleName("BankDetail");
		this.bankName.setValueColumn("BankCode");
		this.bankName.setDescColumn("BankName");
		this.bankName.setValidateColumns(new String[] { "BankCode" });
		
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.getTextbox().setWidth("110px");
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		
		this.finDate.setFormat(PennantConstants.dateFormat);

		this.originalAmount.setMandatory(true);
		this.originalAmount.setMaxlength(18);
		this.originalAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.installmentAmount.setMandatory(true);
		this.installmentAmount.setMaxlength(18);
		this.installmentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.outStandingBal.setMandatory(true);
		this.outStandingBal.setMaxlength(18);
		this.outStandingBal.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerExtLiabilityDialog",userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerExtLiabilityDialog_btnSave"));
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
	public void onClose$window_CustomerExtLiabilityDialog(Event event)throws Exception {
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
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_CustomerExtLiabilityDialog);
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
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		boolean close= true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close= false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeWindow();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			closePopUpWindow(this.window_CustomerExtLiabilityDialog,"CustomerExtLiabilityDialog");
		}else{
			closeDialog(this.window_CustomerExtLiabilityDialog, "CustomerExtLiabilityDialog");
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
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerExtLiability
	 *            CustomerExtLiability
	 */
	public void doWriteBeanToComponents(CustomerExtLiability aCustomerExtLiability) {
		logger.debug("Entering");

		if(aCustomerExtLiability.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerExtLiability.getCustID());
		}
		this.liabilitySeq.setValue(aCustomerExtLiability.getLiabilitySeq());
		this.finDate.setValue(aCustomerExtLiability.getFinDate());
		this.bankName.setValue(aCustomerExtLiability.getBankName());
		this.bankName.setDescription(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescBankName()));
		this.finType.setValue(aCustomerExtLiability.getFinType());
		this.finType.setDescription(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescFinType()));
		this.finStatus.setValue(aCustomerExtLiability.getFinStatus());
		this.originalAmount.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getOriginalAmount(),finFormatter));
		this.installmentAmount.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getInstalmentAmount(),finFormatter));
		this.outStandingBal.setValue(PennantAppUtil.formateAmount(aCustomerExtLiability.getOutStandingBal(),finFormatter));

		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomerExtLiability.getLovDescCustShrtName()));

		
		this.recordStatus.setValue(aCustomerExtLiability.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerExtLiability
	 */
	public void doWriteComponentsToBean(CustomerExtLiability aCustomerExtLiability) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerExtLiability.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerExtLiability.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLiabilitySeq(this.liabilitySeq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setFinDate(this.finDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLovDescBankName(this.bankName.getDescription());
			aCustomerExtLiability.setBankName(this.bankName.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setLovDescFinType(this.finType.getDescription());
			aCustomerExtLiability.setFinType(this.finType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}


		try {
			aCustomerExtLiability.setOriginalAmount(PennantAppUtil.unFormateAmount(this.originalAmount.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		
		try {
			aCustomerExtLiability.setInstalmentAmount(PennantAppUtil.unFormateAmount(this.installmentAmount.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setOutStandingBal(PennantAppUtil.unFormateAmount(this.outStandingBal.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerExtLiability.setFinStatus(this.finStatus.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerExtLiability.setRecordStatus(this.recordStatus.getValue());
		setCustomerExtLiability(aCustomerExtLiability);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerExtLiability
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerExtLiability aCustomerExtLiability) throws InterruptedException {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.bankName.focus();
			if (isNewCustomer()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerExtLiability);

			// stores the initial data for comparing if they are changed during user action.
			doStoreInitValues();
            doCheckEnquiry();
			if(isNewCustomer()){
				this.window_CustomerExtLiabilityDialog.setHeight("55%");
				this.window_CustomerExtLiabilityDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerExtLiabilityDialog.doModal() ;
			}else{
				this.window_CustomerExtLiabilityDialog.setWidth("100%");
				this.window_CustomerExtLiabilityDialog.setHeight("100%");
				setDialog(this.window_CustomerExtLiabilityDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if("ENQ".equals(this.moduleType)){
			this.bankName.setReadonly(true);
			this.finType.setReadonly(true);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_bankName = this.bankName.getValue();
		this.oldVar_lovDescBankName = this.bankName.getDescription();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinType = this.finType.getDescription();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.bankName.setValue(this.oldVar_bankName);
		this.bankName.setDescription(this.oldVar_lovDescBankName);
		this.finType.setValue(this.oldVar_finType);
		this.finType.setDescription(this.oldVar_lovDescFinType);
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

		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_bankName != this.bankName.getValue()) {
			return true;
		}
		if (this.oldVar_finType != this.finType.getValue()) {
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
		if (!this.finDate.isReadonly()) {
			this.finDate.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_FinDate.value"), null, true));
		}
		if (!this.finStatus.isReadonly()) {
			this.finStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_FinStatus.value"), null, true));
		}
		if (!this.originalAmount.isDisabled()) {
			this.originalAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerExtLiabilityDialog_OriginalAmount.value"), false));
		}
		if (!this.installmentAmount.isDisabled()) {
			this.installmentAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerExtLiabilityDialog_InstallmentAmount.value"), false));
		}
		if (!this.outStandingBal.isDisabled()) {
			this.outStandingBal.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_CustomerExtLiabilityDialog_OutStandingBal.value"), false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_BankName.value"),null,true,true));
		}
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerExtLiabilityDialog_finType.value"),null,true,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.bankName.setConstraint("");
		this.finType.setConstraint("");
		this.originalAmount.setConstraint("");
		this.installmentAmount.setConstraint("");
		this.outStandingBal.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.finStatus.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.originalAmount.setErrorMessage("");
		this.installmentAmount.setErrorMessage("");
		this.outStandingBal.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerExtLiability object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerExtLiability aCustomerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties(getCustomerExtLiability(), aCustomerExtLiability);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCustomerExtLiability.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerExtLiability.getRecordType()).equals("")) {
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion() + 1);
				aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerExtLiability.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()) {
					aCustomerExtLiability.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerExtLiability, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceCustomerListCtrl().doFillCustomerExtLiabilityDetails(this.CustomerExtLiabilityList);
					this.window_CustomerExtLiabilityDialog.onClose();
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()){
			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
		}
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.bankName.setReadonly(isReadOnly("CustomerExtLiabilityDialog_BankName"));
		this.finDate.setDisabled(isReadOnly("CustomerExtLiabilityDialog_finDate"));
		this.finStatus.setReadonly(isReadOnly("CustomerExtLiabilityDialog_finStatus"));
		this.finType.setReadonly(isReadOnly("CustomerExtLiabilityDialog_finType"));
		this.originalAmount.setReadonly(isReadOnly("CustomerExtLiabilityDialog_originalAmount"));
		this.installmentAmount.setReadonly(isReadOnly("CustomerExtLiabilityDialog_installmentAmount"));
		this.outStandingBal.setReadonly(isReadOnly("CustomerExtLiabilityDialog_outStandingBal"));


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerExtLiability.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newCustomer){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if(getFinanceCustomerListCtrl()!= null){
			isCustomerWorkflow = getFinanceCustomerListCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.bankName.setReadonly(true);
		this.finStatus.setReadonly(true);
		this.finType.setReadonly(true);
		this.outStandingBal.setReadonly(true);
		this.installmentAmount.setReadonly(true);
		this.originalAmount.setReadonly(true);

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
		this.bankName.setValue("");
		this.bankName.setDescription("");
		this.finStatus.setValue("");
		this.finType.setValue("");
		this.originalAmount.setValue("");
		this.installmentAmount.setValue("");
		this.outStandingBal.setValue("");
		this.finType.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerExtLiability aCustomerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties(getCustomerExtLiability(), aCustomerExtLiability);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerExtLiability object with the components data
		doWriteComponentsToBean(aCustomerExtLiability);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerExtLiability.isNew();
		String tranType = "";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerExtLiability.getRecordType()).equals("")){
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
				if(isNew){
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerExtLiability.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerExtLiability.setVersion(1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_ADD);					
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerExtLiability.getRecordType()).equals("")){
					aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
					aCustomerExtLiability.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerExtLiability.setVersion(aCustomerExtLiability.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newFinanceCustomerProcess(aCustomerExtLiability, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerExtLiabilityDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getFinanceCustomerListCtrl().doFillCustomerExtLiabilityDetails(this.CustomerExtLiabilityList);
				this.window_CustomerExtLiabilityDialog.onClose();
			}
		
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	
	private AuditHeader newFinanceCustomerProcess(CustomerExtLiability aCustomerExtLiability,String tranType){
		logger.debug("Entering");
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aCustomerExtLiability, tranType);
		CustomerExtLiabilityList = new ArrayList<CustomerExtLiability>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		
		valueParm[0] = String.valueOf(aCustomerExtLiability.getId());
		valueParm[1] = String.valueOf(aCustomerExtLiability.getLiabilitySeq());
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_LiabilitySeq")+ ":" + valueParm[1];
		
		if(getFinanceCustomerListCtrl().getCustomerExtLiabilityDetailList()!=null && getFinanceCustomerListCtrl().getCustomerExtLiabilityDetailList().size()>0){
			for (int i = 0; i < getFinanceCustomerListCtrl().getCustomerExtLiabilityDetailList().size(); i++) {
				CustomerExtLiability customerExtLiability = getFinanceCustomerListCtrl().getCustomerExtLiabilityDetailList().get(i);
				
				
				if(aCustomerExtLiability.getLiabilitySeq()==customerExtLiability.getLiabilitySeq()){ // Both Current and Existing list rating same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							CustomerExtLiabilityList.add(aCustomerExtLiability);
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							CustomerExtLiabilityList.add(aCustomerExtLiability);
						}else if(aCustomerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinanceCustomerListCtrl().getCustomerDetails().getCustomerExtLiabilityList().size(); j++) {
								CustomerExtLiability email =  getFinanceCustomerListCtrl().getCustomerDetails().getCustomerExtLiabilityList().get(j);
								if(email.getCustID() == aCustomerExtLiability.getCustID() && 
										email.getLiabilitySeq()==aCustomerExtLiability.getLiabilitySeq()){
									CustomerExtLiabilityList.add(email);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							CustomerExtLiabilityList.add(customerExtLiability);
						}
					}
				}else{
					CustomerExtLiabilityList.add(customerExtLiability);
				}
			}
		}
		
		if(!recordAdded){
			CustomerExtLiabilityList.add(aCustomerExtLiability);
		}
		logger.debug("Leaving");
		return auditHeader;
	} 


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerExtLiability
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerExtLiability aCustomerExtLiability, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerExtLiability.getBefImage(), aCustomerExtLiability);
		return new AuditHeader(getReference(), String.valueOf(aCustomerExtLiability.getCustID()), null, null, 
				auditDetail, aCustomerExtLiability.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerExtLiabilityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		Notes notes = new Notes();
		notes.setModuleName("CustomerExtLiability");
		notes.setReference(getReference());
		notes.setVersion(getCustomerExtLiability().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerExtLiability().getCustID()+ PennantConstants.KEY_SEPERATOR
		+ getCustomerExtLiability().getBankName();
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

	public CustomerExtLiability getCustomerExtLiability() {
		return this.customerExtLiability;
	}
	public void setCustomerExtLiability(CustomerExtLiability customerExtLiability) {
		this.customerExtLiability = customerExtLiability;
	}

	public void setCustomerEmails(List<CustomerExtLiability> customerEmails) {
		this.CustomerExtLiabilityList = customerEmails;
	}
	public List<CustomerExtLiability> getCustomerEmails() {
		return CustomerExtLiabilityList;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public FinanceCustomerListCtrl getFinanceCustomerListCtrl() {
		return financeCustomerListCtrl;
	}
	public void setFinanceCustomerListCtrl(
			FinanceCustomerListCtrl financeCustomerListCtrl) {
		this.financeCustomerListCtrl = financeCustomerListCtrl;
	}
	
}
