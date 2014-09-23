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
 * FileName    		:  ProvisionDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.financemanagement.provision;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.service.finance.FinanceScheduleDetailService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Provision/Provision/provisionDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ProvisionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5139814152842315333L;
	private final static Logger logger = Logger.getLogger(ProvisionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProvisionDialog; 	// autowired
	
	protected ExtendedCombobox 		finReference; 				// autowired
	protected Textbox 		finBranch; 					// autowired
	protected Textbox 		finType; 					// autowired
	protected Longbox 		custID; 					// autowired
	protected Textbox 		lovDescCustCIF; 			// autowired
	protected Label   		custShrtName;				// autowired
	protected Checkbox 		useNFProv; 					// autowired
	protected Checkbox 		autoReleaseNFP; 			// autowired
	protected Decimalbox 	principalDue; 				// autowired
	protected Decimalbox 	profitDue; 					// autowired
	protected Decimalbox 	dueTotal; 					// autowired
	protected Decimalbox 	nonFormulaProv;	 			// autowired
	protected Datebox 		dueFromDate; 				// autowired
	protected Decimalbox 	calProvisionedAmt; 		    // autowired
	protected Decimalbox 	provisionedAmt; 		    // autowired
	protected Datebox 		lastFullyPaidDate; 				// autowired
	
	
	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired vars
	private Provision provision; // overhanded per param
	private transient ProvisionListCtrl provisionListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finReference;
	private transient String  		oldVar_finBranch;
	private transient String  		oldVar_finType;
	private transient long  		oldVar_custID;
	private transient boolean  		oldVar_useNFProv;
	private transient boolean  		oldVar_autoReleaseNFP;
	private transient BigDecimal  	oldVar_principalDue;
	private transient BigDecimal  	oldVar_profitDue;
	private transient BigDecimal  	oldVar_nonFormulaProv;
	private transient Date  		oldVar_dueFromDate;
	private transient Date  		oldVar_lastFullyPaidDate;
	private transient String        oldVar_recordStatus;
	private transient BigDecimal  	oldVar_calcProvisionAmt;
	private transient BigDecimal  	oldVar_provisionedAmt;

	private transient boolean validationOn;
	private boolean notes_Entered=false;
	private String menuItemRightName = null;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ProvisionDialog_";
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
	private transient ProvisionService provisionService;
	private transient FinanceScheduleDetailService financeScheduleDetailService; 
	
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private MailUtil mailUtil;
	
	/**
	 * default constructor.<br>
	 */
	public ProvisionDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Provision object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvisionDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("provision")) {
			this.provision = (Provision) args.get("provision");
			Provision befImage =new Provision();
			BeanUtils.copyProperties(this.provision, befImage);
			this.provision.setBefImage(befImage);

			setProvision(this.provision);
		} else {
			setProvision(null);
		}
		
		if (args.containsKey("menuItemRightName")) {
			menuItemRightName = (String) args.get("menuItemRightName");
		}

		doLoadWorkFlow(this.provision.isWorkflow(),this.provision.getWorkflowId(),this.provision.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateMenuRoleAuthorities(getRole(), "ProvisionDialog", menuItemRightName);
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// READ OVERHANDED params !
		// we get the provisionListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete provision here.
		if (args.containsKey("provisionListCtrl")) {
			setProvisionListCtrl((ProvisionListCtrl) args.get("provisionListCtrl"));
		} else {
			setProvisionListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getProvision());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		
		//Empty sent any required attributes
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
		this.principalDue.setMaxlength(18);
		this.principalDue.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		this.profitDue.setMaxlength(18);
		this.profitDue.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		this.dueTotal.setMaxlength(18);
		this.dueTotal.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		this.nonFormulaProv.setMaxlength(18);
		this.nonFormulaProv.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		this.calProvisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		this.provisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(getProvision().getLovDescFinFormatter()));
		
		this.dueFromDate.setFormat(PennantConstants.dateFormat);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("ProvisionDialog",getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
	public void onClose$window_ProvisionDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_ProvisionDialog);
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
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_ProvisionDialog, "ProvisionDialog");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvision
	 *            Provision
	 */
	public void doWriteBeanToComponents(Provision aProvision) {
		logger.debug("Entering") ;
		this.finReference.setDescription(aProvision.getFinReference());
		this.finBranch.setValue(aProvision.getFinBranch());
		this.finType.setValue(aProvision.getFinType());
		this.custID.setValue(aProvision.getCustID());
		this.lovDescCustCIF.setValue(aProvision.getLovDescCustCIF());
		this.custShrtName.setValue(aProvision.getLovDescCustShrtName());
		this.useNFProv.setChecked(aProvision.isUseNFProv());
		this.autoReleaseNFP.setChecked(aProvision.isAutoReleaseNFP());
		this.principalDue.setValue(PennantAppUtil.formateAmount(aProvision.getPrincipalDue(),
				aProvision.getLovDescFinFormatter()));
		this.profitDue.setValue(PennantAppUtil.formateAmount(aProvision.getProfitDue(),
				aProvision.getLovDescFinFormatter()));
		this.dueTotal.setValue(PennantAppUtil.formateAmount(aProvision.getPrincipalDue().
				add(aProvision.getProfitDue()),aProvision.getLovDescFinFormatter()));
		this.nonFormulaProv.setValue(PennantAppUtil.formateAmount(aProvision.getNonFormulaProv(),
				aProvision.getLovDescFinFormatter()));
		this.calProvisionedAmt.setValue(PennantAppUtil.formateAmount(aProvision.getProvisionAmtCal(),
				aProvision.getLovDescFinFormatter()));
		this.provisionedAmt.setValue(PennantAppUtil.formateAmount(aProvision.getProvisionedAmt(),
				aProvision.getLovDescFinFormatter()));
		
		this.dueFromDate.setValue(aProvision.getDueFromDate());
		this.lastFullyPaidDate.setValue(aProvision.getLastFullyPaidDate());

		this.recordStatus.setValue(aProvision.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProvision
	 */
	public void doWriteComponentsToBean(Provision aProvision) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProvision.setFinReference(this.finReference.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setFinBranch(this.finBranch.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setFinType(this.finType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setCustID(this.custID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setLovDescCustShrtName(this.custShrtName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setUseNFProv(this.useNFProv.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setAutoReleaseNFP(this.autoReleaseNFP.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {

			if(this.useNFProv.isChecked() && !this.nonFormulaProv.isDisabled()) {
				if(this.nonFormulaProv.getValue()==null || this.nonFormulaProv.doubleValue() < 0){
					throw new WrongValueException(this.nonFormulaProv, Labels.getLabel("FIELD_NO_EMPTY_NO_NEG_NO_ZERO",
							new String[] { Labels.getLabel("label_ProvisionDialog_ProvisionAmt.value") }));
				} else if(this.nonFormulaProv.getValue().compareTo(this.principalDue.getValue()) > 0){
					throw new WrongValueException(this.nonFormulaProv, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
							new String[] { Labels.getLabel("label_ProvisionDialog_ProvisionAmt.value") ,String.valueOf(this.principalDue.getValue())}));
				} 
			} 
			aProvision.setNonFormulaProv(PennantAppUtil.unFormateAmount(this.nonFormulaProv.getValue(), 
					aProvision.getLovDescFinFormatter()));
			
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aProvision.setPrincipalDue(PennantAppUtil.unFormateAmount(this.principalDue.getValue(),
					aProvision.getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setProfitDue(PennantAppUtil.unFormateAmount(this.profitDue.getValue(), 
					aProvision.getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setProvisionAmtCal(PennantAppUtil.unFormateAmount(this.calProvisionedAmt.getValue(), 
					aProvision.getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setProvisionedAmt(PennantAppUtil.unFormateAmount(this.provisionedAmt.getValue(), 
					aProvision.getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aProvision.setDueFromDate(this.dueFromDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvision.setLastFullyPaidDate(this.lastFullyPaidDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			Date appDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
			aProvision.setProvisionCalDate(appDate);
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aProvision.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aProvision
	 * @throws InterruptedException
	 */
	public void doShowDialog(Provision aProvision) throws InterruptedException {
		logger.debug("Entering") ;

		// if aProvision == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aProvision == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aProvision = getProvisionService().getNewProvision();

			setProvision(aProvision);
		} else {
			setProvision(aProvision);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aProvision.isNew()) {
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
			doWriteBeanToComponents(aProvision);
			checkNFProv();

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ProvisionDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finReference = this.finReference.getDescription();
		this.oldVar_finBranch = this.finBranch.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_useNFProv = this.useNFProv.isChecked();
		this.oldVar_autoReleaseNFP = this.autoReleaseNFP.isChecked();
		this.oldVar_principalDue = this.principalDue.getValue();
		this.oldVar_profitDue = this.profitDue.getValue();
		this.oldVar_nonFormulaProv = this.nonFormulaProv.getValue();
		this.oldVar_calcProvisionAmt = this.calProvisionedAmt.getValue();
		this.oldVar_provisionedAmt = this.provisionedAmt.getValue();
		this.oldVar_dueFromDate = this.dueFromDate.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setDescription(this.oldVar_finReference);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.finType.setValue(this.oldVar_finType);
		this.custID.setValue(this.oldVar_custID);
		this.useNFProv.setChecked(this.oldVar_useNFProv);
		this.autoReleaseNFP.setChecked(this.oldVar_autoReleaseNFP);
		this.principalDue.setValue(this.oldVar_principalDue);
		this.profitDue.setValue(this.oldVar_profitDue);
		this.nonFormulaProv.setValue(this.oldVar_nonFormulaProv);
		this.calProvisionedAmt.setValue(this.oldVar_calcProvisionAmt);
		this.provisionedAmt.setValue(this.oldVar_provisionedAmt);
		this.dueFromDate.setValue(this.oldVar_dueFromDate);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
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
		logger.debug("Entering");
		
		//To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_finReference != this.finReference.getDescription()) {
			return true;
		}
		if (this.oldVar_finBranch != this.finBranch.getValue()) {
			return true;
		}
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_useNFProv != this.useNFProv.isChecked()) {
			return true;
		}
		if (this.oldVar_autoReleaseNFP != this.autoReleaseNFP.isChecked()) {
			return true;
		}
		if (this.oldVar_principalDue != this.principalDue.getValue()) {
			return true;
		}
		if (this.oldVar_profitDue != this.profitDue.getValue()) {
			return true;
		}
		if(this.oldVar_nonFormulaProv != this.nonFormulaProv.getValue()) {
			return true;
		}
		if (this.oldVar_calcProvisionAmt != this.calProvisionedAmt.getValue()) {
			return true;
		}
		if(this.oldVar_provisionedAmt != this.provisionedAmt.getValue()) {
			return true;
		}
		
		String old_dueFromDate = "";
		String new_dueFromDate ="";
		if (this.oldVar_dueFromDate!=null){
			old_dueFromDate=DateUtility.formatDate(this.oldVar_dueFromDate,PennantConstants.dateFormat);
		}
		if (this.dueFromDate.getValue()!=null){
			new_dueFromDate=DateUtility.formatDate(this.dueFromDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_dueFromDate).equals(StringUtils.trimToEmpty(new_dueFromDate))) {
			return true;
		}
		String old_lastFullyPaidDate = "";
		String new_lastFullyPaidDate ="";
		if (this.oldVar_lastFullyPaidDate!=null){
			old_lastFullyPaidDate=DateUtility.formatDate(this.oldVar_lastFullyPaidDate,PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_lastFullyPaidDate).equals(StringUtils.trimToEmpty(new_lastFullyPaidDate))) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (this.finReference.isButtonVisible()){
			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_ProvisionDialog_FinReference.value")}));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.finBranch.setConstraint("");
		this.finType.setConstraint("");
		this.custID.setConstraint("");
		this.principalDue.setConstraint("");
		this.profitDue.setConstraint("");
		this.nonFormulaProv.setConstraint("");
		this.calProvisionedAmt.setConstraint("");
		this.provisionedAmt.setConstraint("");
		this.dueFromDate.setConstraint("");
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Provision object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {/*
		logger.debug("Entering");	
		final Provision aProvision = new Provision();
		BeanUtils.copyProperties(getProvision(), aProvision);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aProvision.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aProvision.getRecordType()).equals("")){
				aProvision.setVersion(aProvision.getVersion()+1);
				aProvision.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aProvision.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aProvision,tranType)){
					refreshList();
					closeDialog(this.window_ProvisionDialog, "Provision"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	*/}

	/**
	 * Create a new Provision object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old vars
		doStoreInitValues();

		final Provision aProvision = getProvisionService().getNewProvision();
		aProvision.setNewRecord(true);
		setProvision(aProvision);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finReference.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getProvision().isNewRecord()){
			this.finReference.setReadonly(false);
		}else{
			this.finReference.setReadonly(true);
		}

		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.custID.setReadonly(true);
		this.useNFProv.setDisabled(isReadOnly("ProvisionDialog_useNFProv"));
		this.autoReleaseNFP.setDisabled(isReadOnly("ProvisionDialog_autoReleaseNFP"));
		this.nonFormulaProv.setDisabled(isReadOnly("ProvisionDialog_nonFormulaProv"));
		this.principalDue.setDisabled(true);
		this.profitDue.setDisabled(true);
		this.calProvisionedAmt.setDisabled(true);
		this.provisionedAmt.setDisabled(true);
		this.dueFromDate.setDisabled(true);
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.provision.isNewRecord()){
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		this.finReference.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.nonFormulaProv.setReadonly(true);
		this.custID.setReadonly(true);
		this.useNFProv.setDisabled(true);
		this.autoReleaseNFP.setDisabled(true);
		this.principalDue.setReadonly(true);
		this.profitDue.setReadonly(true);
		this.calProvisionedAmt.setReadonly(true);
		this.provisionedAmt.setReadonly(true);
		this.dueFromDate.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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

		this.finReference.setValue("");
		this.finBranch.setValue("");
		this.finType.setValue("");
		this.custID.setText("");
		this.nonFormulaProv.setValue("");
		this.useNFProv.setChecked(false);
		this.autoReleaseNFP.setChecked(false);
		this.principalDue.setValue("");
		this.profitDue.setValue("");
		this.calProvisionedAmt.setValue("");
		this.provisionedAmt.setValue("");
		this.dueFromDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		
		final Provision aProvision = new Provision();
		BeanUtils.copyProperties(getProvision(), aProvision);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		
		// fill the Provision object with the components data
		doWriteComponentsToBean(aProvision);
		
		// Write the additional validations as per below example
		String tranType = "";
		boolean isNew = aProvision.isNewRecord();
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aProvision.getRecordType()).equals("")) {
				aProvision.setVersion(aProvision.getVersion() + 1);
				if (isNew) {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvision.setNewRecord(true);
				}
			}

		} else {
			aProvision.setVersion(aProvision.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aProvision, tranType)) {

				refreshList();

				//Customer Notification for Role Identification
				if(StringUtils.trimToEmpty(aProvision.getNextTaskId()).equals("")){
					aProvision.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aProvision.getRoleCode(),aProvision.getNextRoleCode(), 
						aProvision.getFinReference(), " Provision ", aProvision.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aProvision.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aProvision.getNextRoleCode()).equals(aProvision.getRoleCode())){
					getMailUtil().sendMail(PennantConstants.MAIL_MODULE_PROVISION,aProvision,this);
				}

				closeDialog(this.window_ProvisionDialog, "ProvisionDialog");
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_ProvisionDialog, e);
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
	private boolean doProcess(Provision aProvision, String tranType) throws InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProvision.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aProvision.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvision.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aProvision.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProvision.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aProvision);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aProvision))) {
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

			aProvision.setTaskId(taskId);
			aProvision.setNextTaskId(nextTaskId);
			aProvision.setRoleCode(getRole());
			aProvision.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProvision, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aProvision);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProvision, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProvision, tranType);
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Provision aProvision = (Provision) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getProvisionService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getProvisionService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getProvisionService().doApprove(auditHeader);

						if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getProvisionService().doReject(auditHeader);

						if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ProvisionDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ProvisionDialog, auditHeader);
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
	
	public void onFulfill$finReference(Event event){
		logger.debug("Entering" + event.toString());
		
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
			if(financeMain != null){
				this.finReference.setDescription(financeMain.getFinReference());
				this.finType.setValue(financeMain.getFinType());
				this.finBranch.setValue(financeMain.getFinBranch());
				this.custID.setValue(financeMain.getCustID());
				this.custShrtName.setValue(financeMain.getLovDescCustShrtName());
				this.lovDescCustCIF.setValue(financeMain.getLovDescCustCIF());
				getProvision().setLovDescFinFormatter(financeMain.getLovDescFinFormatter());
				doSetFieldProperties();
				OverdueChargeRecovery odcharges = getFinanceScheduleDetailService().getOverdueChargeRecovery(StringUtils.trim(financeMain.getFinReference()));
				// getFinanceScheduleDetailService();
				this.principalDue.setValue(PennantAppUtil.formateAmount(odcharges.getLovDescCurSchPriDue(), financeMain.getLovDescFinFormatter()));

				this.profitDue.setValue(PennantAppUtil.formateAmount(odcharges.getLovDescCurSchPftDue(), financeMain.getLovDescFinFormatter()));
				this.dueTotal.setValue(this.principalDue.getValue().add(this.profitDue.getValue()));
				this.dueFromDate.setValue(new Date());
				
				//Last Fully Paid Date Details
				FinanceProfitDetail detail = getProvisionService().getProfitDetailById(financeMain.getFinReference());
				this.lastFullyPaidDate.setValue(detail.getFullPaidDate());
				if(this.lastFullyPaidDate.getValue() == null){
					this.lastFullyPaidDate.setValue(financeMain.getFinStartDate());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$useNFProv(Event event) {
		logger.debug("Entering" + event.toString());
		this.nonFormulaProv.setValue(BigDecimal.ZERO);
		checkNFProv();
		logger.debug("Leaving" + event.toString());
	}
	
	private void checkNFProv(){
		if(this.useNFProv.isChecked()) {
			this.nonFormulaProv.setDisabled(isReadOnly("ProvisionDialog_nonFormulaProv"));
		} else {
			this.nonFormulaProv.setDisabled(true);
		}
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

	public Provision getProvision() {
		return this.provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	public ProvisionService getProvisionService() {
		return this.provisionService;
	}

	public void setProvisionListCtrl(ProvisionListCtrl provisionListCtrl) {
		this.provisionListCtrl = provisionListCtrl;
	}

	public ProvisionListCtrl getProvisionListCtrl() {
		return this.provisionListCtrl;
	}

	private AuditHeader getAuditHeader(Provision aProvision, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvision.getBefImage(), aProvision);   
		return new AuditHeader(aProvision.getFinReference(),null,null,null,auditDetail,aProvision.getUserDetails(),getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ProvisionDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
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

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("Provision");
		notes.setReference(getProvision().getFinReference());
		notes.setVersion(getProvision().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.custID.setErrorMessage("");
		this.principalDue.setErrorMessage("");
		this.profitDue.setErrorMessage("");
		this.calProvisionedAmt.setErrorMessage("");
		this.provisionedAmt.setErrorMessage("");
		this.dueFromDate.setErrorMessage("");
		this.nonFormulaProv.setErrorMessage("");
		logger.debug("Leaving");
	}


	private void refreshList(){
		final JdbcSearchObject<Provision> soProvision = getProvisionListCtrl().getSearchObj();
		getProvisionListCtrl().pagingProvisionList.setActivePage(0);
		getProvisionListCtrl().getPagedListWrapper().setSearchObject(soProvision);
		if (getProvisionListCtrl().listBoxProvision != null) {
			getProvisionListCtrl().listBoxProvision.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	
	public FinanceScheduleDetailService getFinanceScheduleDetailService() {
		return financeScheduleDetailService;
	}

	public void setFinanceScheduleDetailService(
			FinanceScheduleDetailService financeScheduleDetailService) {
		this.financeScheduleDetailService = financeScheduleDetailService;
	}
	
	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

}
