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
 * FileName    		:  SuspenseDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.suspense;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Suspense/SusoenseDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SuspenseDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7798200490595650451L;
	private final static Logger logger = Logger.getLogger(SuspenseDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SuspenseDialog; // autowired
	protected ExtendedCombobox finReference; // autowired
	protected Textbox finBranch; // autowired
	protected Textbox finType; // autowired
	protected Longbox custID; // autowired
	protected Textbox lovDescCustCIF; // autowired
	protected Label custShrtName; // autowired
	protected Intbox finSuspSeq; // autowired
	protected Checkbox finIsInSusp; // autowired
	protected Checkbox manualSusp; // autowired
	protected Decimalbox finSuspAmt; // autowired
	protected Decimalbox finCurSuspAmt; // autowired
	protected Datebox finSuspDate; // autowired
	protected Datebox finSuspTrfDate; // autowired

	// not auto wired vars
	private FinanceSuspHead suspHead; // overhanded per param
	private transient SuspenseListCtrl suspenseListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean oldVar_manualSusp;
	private transient String oldVar_finReference;
	
	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	private transient boolean validationOn;
	private boolean notes_Entered=false;
	private String menuItemRightName = null;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SuspenseDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	// ServiceDAOs / Domain Classes
	private transient SuspenseService suspenseService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private MailUtil mailUtil;
	
	/**
	 * default constructor.<br>
	 */
	public SuspenseDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Suspense object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SuspenseDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("suspHead")) {
			this.suspHead = (FinanceSuspHead) args.get("suspHead");
			FinanceSuspHead befImage =new FinanceSuspHead();
			BeanUtils.copyProperties(this.suspHead, befImage);
			this.suspHead.setBefImage(befImage);
			setSuspHead(this.suspHead);
		} else {
			setSuspHead(null);
		}


		if (args.containsKey("menuItemRightName")) {
			menuItemRightName = (String) args.get("menuItemRightName");
		}

		doLoadWorkFlow(this.suspHead.isWorkflow(),this.suspHead.getWorkflowId(),this.suspHead.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateMenuRoleAuthorities(getRole(), "SuspenseDialog", menuItemRightName);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// READ OVERHANDED params !
		// we get the SuspenseListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete Suspense here.
		if (args.containsKey("suspenseListCtrl")) {
			setSuspenseListCtrl((SuspenseListCtrl) args.get("suspenseListCtrl"));
		} else {
			setSuspenseListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSuspHead());
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finReference.setInputAllowed(false);
		this.finReference.setDisplayStyle(3);
        this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finBranch.setMaxlength(8);
		this.finType.setMaxlength(8);
		this.custID.setMaxlength(19);
		this.finSuspAmt.setMaxlength(18);
		this.finSuspAmt.setFormat(PennantApplicationUtil.getAmountFormate(getSuspHead().getLovDescFinFormatter()));
		this.finCurSuspAmt.setMaxlength(18);
		this.finCurSuspAmt.setFormat(PennantApplicationUtil.getAmountFormate(getSuspHead().getLovDescFinFormatter()));
		this.finSuspDate.setFormat(PennantConstants.dateFormat);
		this.finSuspTrfDate.setFormat(PennantConstants.dateFormat);

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
	public void onClose$window_SuspenseDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
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
		PTMessageUtils.showHelpWindow(event, window_SuspenseDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// GUI Process

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * @throws Exception 
	 * 
	 */
	private void doClose() throws Exception {
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
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
			closeDialog(this.window_SuspenseDialog, "SuspenseDialog");
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSuspense
	 *            Suspense
	 */
	public void doWriteBeanToComponents(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");
		this.finReference.setDescription(aSuspHead.getFinReference());
		this.finBranch.setValue(aSuspHead.getFinBranch());
		this.finType.setValue(aSuspHead.getFinType());
		this.custID.setValue(aSuspHead.getCustId());
		this.lovDescCustCIF.setValue(aSuspHead.getLovDescCustCIFName());
		this.custShrtName.setValue(aSuspHead.getLovDescCustShrtName());
		this.finSuspSeq.setValue(aSuspHead.getFinSuspSeq());
		this.finIsInSusp.setChecked(aSuspHead.isFinIsInSusp());
		this.manualSusp.setChecked(aSuspHead.isManualSusp());
		this.finSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinSuspAmt(), aSuspHead.getLovDescFinFormatter()));
		this.finCurSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinCurSuspAmt(),aSuspHead.getLovDescFinFormatter()));
		this.finSuspDate.setValue(aSuspHead.getFinSuspDate());
		this.finSuspTrfDate.setValue(aSuspHead.getFinSuspTrfDate());
		if(aSuspHead.getFinSuspDate() == null){
			Date appDate = (Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			this.finSuspDate.setValue(appDate);
			this.finSuspTrfDate.setValue(appDate);
		}
		
		this.recordStatus.setValue(aSuspHead.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSuspHead
	 */
	public void doWriteComponentsToBean(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSuspHead.setFinReference(this.finReference.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinBranch(this.finBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setCustId(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinIsInSusp(this.finIsInSusp.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setManualSusp(this.manualSusp.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspAmt(PennantAppUtil.unFormateAmount(this.finSuspAmt.getValue(),
					aSuspHead.getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinCurSuspAmt(PennantAppUtil.unFormateAmount(this.finCurSuspAmt.getValue(),
					aSuspHead.getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aSuspHead.setFinSuspDate(this.finSuspDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspTrfDate(this.finSuspTrfDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspSeq(this.finSuspSeq.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSuspHead
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceSuspHead aSuspHead) throws InterruptedException {
		logger.debug("Entering");

		// if aSuspense == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aSuspHead == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aSuspHead = getSuspenseService().getNewFinanceSuspHead();
			setSuspHead(aSuspHead);
		} else {
			setSuspHead(aSuspHead);
		}
		
		// set Readonly mode accordingly if the object is new or not.
		if (aSuspHead.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finBranch.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				//doReadOnly();
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSuspHead);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SuspenseDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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

		getUserWorkspace().alocateAuthorities("SuspenseDialog",getRole(), menuItemRightName);
		
		this.btnNew.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SuspenseDialog_btnSave"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SuspenseDialog_btnEdit"));
		this.btnNotes.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getSuspHead().isNewRecord()){
			this.finReference.setReadonly(false);
		}else{
			this.finReference.setReadonly(true);
		}

		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.custID.setReadonly(true);
		this.manualSusp.setDisabled(isReadOnly("SuspenseDialog_manualSusp"));
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.suspHead.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(false);
		}
		
		logger.debug("Leaving");
	}

	public void onFulfill$finReference(Event event) {
		Object dataObject= finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setDescription("");
			this.finType.setValue("");
			this.finBranch.setValue("");
			this.custID.setText("");
			this.custShrtName.setValue("");
			this.lovDescCustCIF.setValue("");
		}else{
			FinanceMain financeMain = (FinanceMain)dataObject;
			if (financeMain != null) {
				this.finReference.setDescription(financeMain.getFinReference());
				this.finType.setValue(financeMain.getFinType());
				this.finBranch.setValue(financeMain.getFinBranch());
				this.custID.setValue(financeMain.getCustID());
				this.custShrtName.setValue(financeMain.getLovDescCustShrtName());
				this.lovDescCustCIF.setValue(financeMain.getLovDescCustCIF());
				getSuspHead().setLovDescFinFormatter(financeMain.getLovDescFinFormatter());
				doSetFieldProperties();
			} 
		}
	}
	
	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		this.finBranch.setConstraint("");
		this.finType.setConstraint("");
		this.custID.setConstraint("");
		this.lovDescCustCIF.setConstraint("");
		this.finSuspSeq.setConstraint("");
		this.finSuspAmt.setConstraint("");
		this.finCurSuspAmt.setConstraint("");
		this.finSuspDate.setConstraint("");
		this.finSuspTrfDate.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_manualSusp = this.manualSusp.isChecked();
		this.oldVar_finReference = this.finReference.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		if (this.oldVar_finReference != this.finReference.getValue()) {
			return true;
		}
		if (this.oldVar_manualSusp != this.manualSusp.isChecked()) {
			return true;
		}
		return false;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
	logger.debug("Entering");
		
		final FinanceSuspHead aFinanceSuspHead = new FinanceSuspHead();
		BeanUtils.copyProperties(getSuspHead(), aFinanceSuspHead);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		
		// fill the Suspense object with the components data
		doWriteComponentsToBean(aFinanceSuspHead);
		
		// Write the additional validations as per below example
		String tranType = "";
		boolean isNew = aFinanceSuspHead.isNewRecord();
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceSuspHead.getRecordType()).equals("")) {
				aFinanceSuspHead.setVersion(aFinanceSuspHead.getVersion() + 1);
				if (isNew) {
					aFinanceSuspHead.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceSuspHead.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceSuspHead.setNewRecord(true);
				}
			}

		} else {
			aFinanceSuspHead.setVersion(aFinanceSuspHead.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aFinanceSuspHead, tranType)) {

				refreshList();

				//Customer Notification for Role Identification
				if(StringUtils.trimToEmpty(aFinanceSuspHead.getNextTaskId()).equals("")){
					aFinanceSuspHead.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceSuspHead.getRoleCode(),aFinanceSuspHead.getNextRoleCode(), 
						aFinanceSuspHead.getFinReference(), " Suspense ", aFinanceSuspHead.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aFinanceSuspHead.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aFinanceSuspHead.getNextRoleCode()).equals(aFinanceSuspHead.getRoleCode())){
					getMailUtil().sendMail(PennantConstants.MAIL_MODULE_MANUALSUSPENSE,aFinanceSuspHead,this);
				}

				closeDialog(this.window_SuspenseDialog, "SuspenseDialog");
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_SuspenseDialog, e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private boolean doProcess(FinanceSuspHead aFinanceSuspHead, String tranType) throws InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceSuspHead.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceSuspHead.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceSuspHead.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aFinanceSuspHead.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceSuspHead.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceSuspHead);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aFinanceSuspHead))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aFinanceSuspHead.setTaskId(taskId);
			aFinanceSuspHead.setNextTaskId(nextTaskId);
			aFinanceSuspHead.setRoleCode(getRole());
			aFinanceSuspHead.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceSuspHead, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aFinanceSuspHead);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceSuspHead, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceSuspHead, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for Processing Workflow Method
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceSuspHead aFinanceSuspHead = (FinanceSuspHead) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSuspenseService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSuspenseService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSuspenseService().doApprove(auditHeader);

						if (aFinanceSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSuspenseService().doReject(auditHeader);

						if (aFinanceSuspHead.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SuspenseDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SuspenseDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
		
	}
	
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (this.finReference.isVisible()) {
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_SuspenseDialog_FinReference.value"),null,true,true));
		}
		logger.debug("Leaving");
	}

	private AuditHeader getAuditHeader(FinanceSuspHead aFinanceSuspHead, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceSuspHead.getBefImage(), aFinanceSuspHead);   
		return new AuditHeader(aFinanceSuspHead.getFinReference(),null,null,null,auditDetail,aFinanceSuspHead.getUserDetails(),getOverideMap());
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("Suspense");
		notes.setReference(getSuspHead().getFinReference());
		notes.setVersion(getSuspHead().getVersion());
		return notes;
	}

	private void refreshList() {
		final JdbcSearchObject<FinanceSuspHead> soSuspense = getSuspenseListCtrl().getSearchObj();
		getSuspenseListCtrl().pagingSuspenseList.setActivePage(0);
		getSuspenseListCtrl().getPagedListWrapper().setSearchObject(soSuspense);
		if (getSuspenseListCtrl().listBoxSuspense != null) {
			getSuspenseListCtrl().listBoxSuspense.getListModel();
		}
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SuspenseListCtrl getSuspenseListCtrl() {
		return suspenseListCtrl;
	}
	public void setSuspenseListCtrl(SuspenseListCtrl suspenseListCtrl) {
		this.suspenseListCtrl = suspenseListCtrl;
	}

	public SuspenseService getSuspenseService() {
		return suspenseService;
	}
	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public FinanceSuspHead getSuspHead() {
		return suspHead;
	}
	public void setSuspHead(FinanceSuspHead suspHead) {
		this.suspHead = suspHead;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

}
