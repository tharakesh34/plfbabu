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
 * FileName    		:  PenaltyDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.penalty;

import java.io.Serializable;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.model.rmtmasters.PenaltyCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.PenaltyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/Penalty/penaltyDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PenaltyDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 520584403087826951L;
	private final static Logger logger = Logger.getLogger(PenaltyDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PenaltyDialog;		 // autoWired
	protected Textbox 		penaltyType;				 // autoWired
	protected Datebox 		penaltyEffDate;				 // autoWired
	protected Checkbox		isPenaltyCapitalize; 		 // autoWired
	protected Checkbox 		isPenaltyOnPriOnly; 		 // autoWired
	protected Checkbox 		isPenaltyAftGrace;           // autoWired
	protected Intbox 		oDueGraceDays;               // autoWired
	protected Textbox		penaltyPriRateBasis;  		 // autoWired
	protected Decimalbox 	penaltyPriBaseRate;		 	 // autoWired
	protected Decimalbox 	penaltyPriSplRate; 			 // autoWired
	protected Decimalbox 	penaltyPriNetRate;   		 // autoWired
	protected Textbox 		penaltyIntRateBasis;  		 // autoWired
	protected Decimalbox	penaltyIntBaseRate;   	 	 // autoWired
	protected Decimalbox	penaltyIntSplRate;   	 	 // autoWired
	protected Decimalbox 	penaltyIntNetRate;           // autoWired
	protected Checkbox 		penaltyIsActive; 			 // autoWired

	protected Label     	recordStatus; 				 // autoWired
	protected Radiogroup  	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired variables
	private Penalty penalty;                            // overHanded per parameter
	private transient PenaltyListCtrl penaltyListCtrl;  // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_penaltyType;
	private transient Date  		oldVar_penaltyEffDate;
	private transient boolean  		oldVar_isPenaltyCapitalize;
	private transient boolean  		oldVar_isPenaltyOnPriOnly;
	private transient boolean  		oldVar_isPenaltyAftGrace;
	private transient int  			oldVar_oDueGraceDays;
	private transient String  		oldVar_penaltyPriRateBasis;
	private transient BigDecimal  	oldVar_penaltyPriBaseRate;
	private transient BigDecimal  	oldVar_penaltyPriSplRate;
	private transient BigDecimal  	oldVar_penaltyPriNetRate;
	private transient String  		oldVar_penaltyIntRateBasis;
	private transient BigDecimal  	oldVar_penaltyIntBaseRate;
	private transient BigDecimal  	oldVar_penaltyIntSplRate;
	private transient BigDecimal  	oldVar_penaltyIntNetRate;
	private transient boolean  		oldVar_penaltyIsActive;
	private transient String 		oldVar_recordStatus;
	private transient String 		oldVar_lovDescPenaltyTypeName;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_PenaltyDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit;	 	// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	protected Button 	btnSearchPenaltyType; 		// autoWire
	protected Textbox 	lovDescPenaltyTypeName;

	// ServiceDAOs / Domain Classes
	private transient PenaltyService penaltyService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public PenaltyDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Penalty object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltyDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("penalty")) {
			this.penalty = (Penalty) args.get("penalty");
			Penalty befImage =new Penalty();
			BeanUtils.copyProperties(this.penalty, befImage);
			this.penalty.setBefImage(befImage);

			setPenalty(this.penalty);
		} else {
			setPenalty(null);
		}

		doLoadWorkFlow(this.penalty.isWorkflow(), this.penalty.getWorkflowId(),
				this.penalty.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "PenaltyDialog");
		}

		// READ OVERHANDED parameters !
		// we get the penaltyListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete penalty here.
		if (args.containsKey("penaltyListCtrl")) {
			setPenaltyListCtrl((PenaltyListCtrl) args.get("penaltyListCtrl"));
		} else {
			setPenaltyListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getPenalty());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.penaltyType.setMaxlength(8);
		this.penaltyEffDate.setFormat(PennantConstants.dateFormat);
		this.oDueGraceDays.setMaxlength(3);
		this.penaltyPriRateBasis.setMaxlength(8);
		this.penaltyPriBaseRate.setMaxlength(13);
		this.penaltyPriBaseRate.setFormat(PennantAppUtil.getAmountFormate(9));
		this.penaltyPriSplRate.setMaxlength(13);
		this.penaltyPriSplRate.setFormat(PennantAppUtil.getAmountFormate(9));
		this.penaltyPriNetRate.setMaxlength(13);
		this.penaltyPriNetRate.setFormat(PennantAppUtil.getAmountFormate(7));
		this.penaltyIntRateBasis.setMaxlength(8);
		this.penaltyIntBaseRate.setMaxlength(13);
		this.penaltyIntBaseRate.setFormat(PennantAppUtil.getAmountFormate(9));
		this.penaltyIntSplRate.setMaxlength(13);
		this.penaltyIntSplRate.setFormat(PennantAppUtil.getAmountFormate(9));
		this.penaltyIntNetRate.setMaxlength(13);
		this.penaltyIntNetRate.setFormat(PennantAppUtil.getAmountFormate(7));

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PenaltyDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PenaltyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PenaltyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PenaltyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PenaltyDialog_btnSave"));
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
	public void onClose$window_PenaltyDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_PenaltyDialog);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onClick$btnSearchPenaltyType(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(
				this.window_PenaltyDialog, "PenaltyCode");
		if (dataObject instanceof String){
			this.penaltyType.setValue(dataObject.toString());
			this.lovDescPenaltyTypeName.setValue("");
		}else{
			PenaltyCode details= (PenaltyCode) dataObject;
			if (details != null) {
				this.penaltyType.setValue(details.getLovValue());
				this.lovDescPenaltyTypeName.setValue(details.getPenaltyType()
						+ "-" + details.getPenaltyDesc());
			}
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
		boolean close = true;
		
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}

		if(close){
			closeDialog(this.window_PenaltyDialog, "Penalty");
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
	 * @param aPenalty
	 *            Penalty
	 */
	public void doWriteBeanToComponents(Penalty aPenalty) {
		logger.debug("Entering");
		this.penaltyType.setValue(aPenalty.getPenaltyType());
		this.penaltyEffDate.setValue(aPenalty.getPenaltyEffDate());
		this.isPenaltyCapitalize.setChecked(aPenalty.isIsPenaltyCapitalize());
		this.isPenaltyOnPriOnly.setChecked(aPenalty.isIsPenaltyOnPriOnly());
		this.isPenaltyAftGrace.setChecked(aPenalty.isIsPenaltyAftGrace());
		this.oDueGraceDays.setValue(aPenalty.getODueGraceDays());
		this.penaltyPriRateBasis.setValue(aPenalty.getPenaltyPriRateBasis());
		this.penaltyPriBaseRate.setValue(aPenalty.getPenaltyPriBaseRate());
		this.penaltyPriSplRate.setValue(aPenalty.getPenaltyPriSplRate());
		this.penaltyPriNetRate.setValue(aPenalty.getPenaltyPriNetRate());
		this.penaltyIntRateBasis.setValue(aPenalty.getPenaltyIntRateBasis());
		this.penaltyIntBaseRate.setValue(aPenalty.getPenaltyIntBaseRate());
		this.penaltyIntSplRate.setValue(aPenalty.getPenaltyIntSplRate());
		this.penaltyIntNetRate.setValue(aPenalty.getPenaltyIntNetRate());
		this.penaltyIsActive.setChecked(aPenalty.isPenaltyIsActive());

		if (aPenalty.isNewRecord()){
			this.lovDescPenaltyTypeName.setValue("");
		}else{
			this.lovDescPenaltyTypeName.setValue(aPenalty.getPenaltyType()
					+ "-" + aPenalty.getLovDescPenaltyTypeName());
		}
		this.recordStatus.setValue(aPenalty.getRecordStatus());
		
		if(aPenalty.isNew() || aPenalty.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.penaltyIsActive.setChecked(true);
			this.penaltyIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPenalty
	 */
	public void doWriteComponentsToBean(Penalty aPenalty) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPenalty.setLovDescPenaltyTypeName(this.lovDescPenaltyTypeName.getValue());
			aPenalty.setPenaltyType(this.penaltyType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			long date=this.penaltyEffDate.getValue().getTime();
			aPenalty.setPenaltyEffDate(new Timestamp(date));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setIsPenaltyCapitalize(this.isPenaltyCapitalize.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setIsPenaltyOnPriOnly(this.isPenaltyOnPriOnly.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setIsPenaltyAftGrace(this.isPenaltyAftGrace.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setODueGraceDays(this.oDueGraceDays.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyPriRateBasis(this.penaltyPriRateBasis.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyPriBaseRate(this.penaltyPriBaseRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyPriSplRate(this.penaltyPriSplRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyPriNetRate(this.penaltyPriNetRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyIntRateBasis(this.penaltyIntRateBasis.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyIntBaseRate(this.penaltyIntBaseRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyIntSplRate(this.penaltyIntSplRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyIntNetRate(this.penaltyIntNetRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenalty.setPenaltyIsActive(this.penaltyIsActive.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aPenalty.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPenalty
	 * @throws InterruptedException
	 */
	public void doShowDialog(Penalty aPenalty) throws InterruptedException {
		logger.debug("Entering");
		// if aPenalty == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aPenalty == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aPenalty = getPenaltyService().getNewPenalty();

			setPenalty(aPenalty);
		} else {
			setPenalty(aPenalty);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aPenalty.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.penaltyType.focus();
		} else {
			this.penaltyEffDate.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aPenalty.getRecordType()).equals("")){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aPenalty);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_PenaltyDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_penaltyType = this.penaltyType.getValue();
		this.oldVar_lovDescPenaltyTypeName = this.lovDescPenaltyTypeName.getValue();
		this.oldVar_penaltyEffDate = PennantAppUtil.getTimestamp(this.penaltyEffDate.getValue());	
		this.oldVar_isPenaltyCapitalize = this.isPenaltyCapitalize.isChecked();
		this.oldVar_isPenaltyOnPriOnly = this.isPenaltyOnPriOnly.isChecked();
		this.oldVar_isPenaltyAftGrace = this.isPenaltyAftGrace.isChecked();
		this.oldVar_oDueGraceDays = this.oDueGraceDays.intValue();	
		this.oldVar_penaltyPriRateBasis = this.penaltyPriRateBasis.getValue();
		this.oldVar_penaltyPriBaseRate = this.penaltyPriBaseRate.getValue();
		this.oldVar_penaltyPriSplRate = this.penaltyPriSplRate.getValue();
		this.oldVar_penaltyPriNetRate = this.penaltyPriNetRate.getValue();
		this.oldVar_penaltyIntRateBasis = this.penaltyIntRateBasis.getValue();
		this.oldVar_penaltyIntBaseRate = this.penaltyIntBaseRate.getValue();
		this.oldVar_penaltyIntSplRate = this.penaltyIntSplRate.getValue();
		this.oldVar_penaltyIntNetRate = this.penaltyIntNetRate.getValue();
		this.oldVar_penaltyIsActive = this.penaltyIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.penaltyType.setValue(this.oldVar_penaltyType);
		this.lovDescPenaltyTypeName.setValue(this.oldVar_lovDescPenaltyTypeName);
		this.penaltyEffDate.setValue(this.oldVar_penaltyEffDate);
		this.isPenaltyCapitalize.setChecked(this.oldVar_isPenaltyCapitalize);
		this.isPenaltyOnPriOnly.setChecked(this.oldVar_isPenaltyOnPriOnly);
		this.isPenaltyAftGrace.setChecked(this.oldVar_isPenaltyAftGrace);
		this.oDueGraceDays.setValue(this.oldVar_oDueGraceDays);
		this.penaltyPriRateBasis.setValue(this.oldVar_penaltyPriRateBasis);
		this.penaltyPriBaseRate.setValue(this.oldVar_penaltyPriBaseRate);
		this.penaltyPriSplRate.setValue(this.oldVar_penaltyPriSplRate);
		this.penaltyPriNetRate.setValue(this.oldVar_penaltyPriNetRate);
		this.penaltyIntRateBasis.setValue(this.oldVar_penaltyIntRateBasis);
		this.penaltyIntBaseRate.setValue(this.oldVar_penaltyIntBaseRate);
		this.penaltyIntSplRate.setValue(this.oldVar_penaltyIntSplRate);
		this.penaltyIntNetRate.setValue(this.oldVar_penaltyIntNetRate);
		this.penaltyIsActive.setChecked(this.oldVar_penaltyIsActive);
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
		//To clear the Error Messages
		doClearMessage();

 		if (this.oldVar_penaltyType != this.penaltyType.getValue()) {
			return true;
		}
 		
 		String old_penaltyEffDate = "";
	  	String new_penaltyEffDate ="";
		if (this.oldVar_penaltyEffDate!=null){
			old_penaltyEffDate=DateUtility.formatDate(this.oldVar_penaltyEffDate,PennantConstants.dateFormat);
		}
		if (this.penaltyEffDate.getValue()!=null){
			new_penaltyEffDate=DateUtility.formatDate(this.penaltyEffDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_penaltyEffDate).equals(
				StringUtils.trimToEmpty(new_penaltyEffDate))) {
			return true;
		}
		if (this.oldVar_isPenaltyCapitalize != this.isPenaltyCapitalize.isChecked()) {
			return true;
		}
		if (this.oldVar_isPenaltyOnPriOnly != this.isPenaltyOnPriOnly.isChecked()) {
			return true;
		}
		if (this.oldVar_isPenaltyAftGrace != this.isPenaltyAftGrace.isChecked()) {
			return true;
		}
		if (this.oldVar_oDueGraceDays != this.oDueGraceDays.intValue()) {
			return true;
		}
		if (this.oldVar_penaltyPriRateBasis != this.penaltyPriRateBasis.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyPriBaseRate != this.penaltyPriBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyPriSplRate != this.penaltyPriSplRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyPriNetRate != this.penaltyPriNetRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIntRateBasis != this.penaltyIntRateBasis.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIntBaseRate != this.penaltyIntBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIntSplRate != this.penaltyIntSplRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIntNetRate != this.penaltyIntNetRate.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIsActive != this.penaltyIsActive.isChecked()) {
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

		if (!this.penaltyEffDate.isDisabled()){
			this.penaltyEffDate.setConstraint("NO EMPTY:" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
							"label_PenaltyDialog_PenaltyEffDate.value")}));
		}
		if (!this.oDueGraceDays.isReadonly()){
			this.oDueGraceDays.setConstraint(new IntValidator(3,Labels.getLabel(
					"label_PenaltyDialog_ODueGraceDays.value")));
		}	
		if (!this.penaltyPriRateBasis.isReadonly()){
			this.penaltyPriRateBasis.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_CAPS_REGEX,
					Labels.getLabel("FIELD_ALNUM_CAPS",new String[]{Labels.getLabel(
							"label_PenaltyDialog_PenaltyPriRateBasis.value")})));
		}	
		if (!this.penaltyPriBaseRate.isReadonly()){
			this.penaltyPriBaseRate.setConstraint(new RateValidator(13,9,
					Labels.getLabel("label_PenaltyDialog_PenaltyPriBaseRate.value"),false));
		}	
		if (!this.penaltyPriSplRate.isReadonly()){
			this.penaltyPriSplRate.setConstraint(new RateValidator(13,9,Labels.getLabel(
							"label_PenaltyDialog_PenaltyPriSplRate.value"),false));
		}	
		if (!this.penaltyPriNetRate.isReadonly()){
			this.penaltyPriNetRate.setConstraint(new AmountValidator(13,7,
				Labels.getLabel("label_PenaltyDialog_PenaltyPriNetRate.value")));
		}	
		if (!this.penaltyIntRateBasis.isReadonly()){
			if(this.penaltyIntRateBasis.getValue() != null && !this.penaltyIntRateBasis.getValue().equals("")){
				this.penaltyIntRateBasis.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_CAPS_REGEX,
						Labels.getLabel("FIELD_ALNUM_CAPS",new String[]{Labels.getLabel(
								"label_PenaltyDialog_PenaltyIntRateBasis.value")})));
			}
		}	
		if (!this.penaltyIntBaseRate.isReadonly()){
			if(this.penaltyIntBaseRate.getValue() != null && !this.penaltyIntBaseRate.getValue().equals("")){
				this.penaltyIntBaseRate.setConstraint(new RateValidator(13,9,Labels.getLabel(
								"label_PenaltyDialog_PenaltyIntBaseRate.value"),false));
			}
		}	
		if (!this.penaltyIntSplRate.isReadonly()){
			if(this.penaltyIntSplRate.getValue() != null && !this.penaltyIntSplRate.getValue().equals("")){
			this.penaltyIntSplRate.setConstraint(new RateValidator(13,9,Labels.getLabel(
							"label_PenaltyDialog_PenaltyIntSplRate.value"),false));
			}
		}	
		if (!this.penaltyIntNetRate.isReadonly()){
			if(this.penaltyIntNetRate.getValue() != null && !this.penaltyIntNetRate.getValue().equals("")){
				this.penaltyIntNetRate.setConstraint(new AmountValidator(13,7,
					Labels.getLabel("label_PenaltyDialog_PenaltyIntNetRate.value")));
			}
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.penaltyEffDate.setConstraint("");
		this.oDueGraceDays.setConstraint("");
		this.penaltyPriRateBasis.setConstraint("");
		this.penaltyPriBaseRate.setConstraint("");
		this.penaltyPriSplRate.setConstraint("");
		this.penaltyPriNetRate.setConstraint("");
		this.penaltyIntRateBasis.setConstraint("");
		this.penaltyIntBaseRate.setConstraint("");
		this.penaltyIntSplRate.setConstraint("");
		this.penaltyIntNetRate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescPenaltyTypeName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_PenaltyDialog_PenaltyType.value")}));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescPenaltyTypeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.penaltyEffDate.setErrorMessage("");
		this.oDueGraceDays.setErrorMessage("");
		this.penaltyPriRateBasis.setErrorMessage("");
		this.penaltyPriBaseRate.setErrorMessage("");
		this.penaltyPriSplRate.setErrorMessage("");
		this.penaltyPriNetRate.setErrorMessage("");
		this.penaltyIntRateBasis.setErrorMessage("");
		this.penaltyIntBaseRate.setErrorMessage("");
		this.penaltyIntSplRate.setErrorMessage("");
		this.penaltyIntNetRate.setErrorMessage("");
		this.lovDescPenaltyTypeName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<Penalty> soObject = getPenaltyListCtrl().getSearchObj();
		getPenaltyListCtrl().pagingPenaltyList.setActivePage(0);
		getPenaltyListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getPenaltyListCtrl().listBoxPenalty!=null){
			getPenaltyListCtrl().listBoxPenalty.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Penalty object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Penalty aPenalty = new Penalty();
		BeanUtils.copyProperties(getPenalty(), aPenalty);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aPenalty.getPenaltyType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aPenalty.getRecordType()).equals("")){
				aPenalty.setVersion(aPenalty.getVersion()+1);
				aPenalty.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aPenalty.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aPenalty,tranType)){
					refreshList();
					closeDialog(this.window_PenaltyDialog, "Penalty"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Penalty object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Penalty() in the frontEnd.
		// we get it from the backEnd.
		final Penalty aPenalty = getPenaltyService().getNewPenalty();
		aPenalty.setNewRecord(true);
		setPenalty(aPenalty);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.penaltyType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getPenalty().isNewRecord()){
			this.btnSearchPenaltyType.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchPenaltyType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.penaltyEffDate.setDisabled(isReadOnly("PenaltyDialog_penaltyEffDate"));
		this.isPenaltyCapitalize.setDisabled(isReadOnly("PenaltyDialog_isPenaltyCapitalize"));
		this.isPenaltyOnPriOnly.setDisabled(isReadOnly("PenaltyDialog_isPenaltyOnPriOnly"));
		this.isPenaltyAftGrace.setDisabled(isReadOnly("PenaltyDialog_isPenaltyAftGrace"));
		this.oDueGraceDays.setReadonly(isReadOnly("PenaltyDialog_oDueGraceDays"));
		this.penaltyPriRateBasis.setReadonly(isReadOnly("PenaltyDialog_penaltyPriRateBasis"));
		this.penaltyPriBaseRate.setReadonly(isReadOnly("PenaltyDialog_penaltyPriBaseRate"));
		this.penaltyPriSplRate.setReadonly(isReadOnly("PenaltyDialog_penaltyPriSplRate"));
		this.penaltyPriNetRate.setReadonly(isReadOnly("PenaltyDialog_penaltyPriNetRate"));
		this.penaltyIntRateBasis.setReadonly(isReadOnly("PenaltyDialog_penaltyIntRateBasis"));
		this.penaltyIntBaseRate.setReadonly(isReadOnly("PenaltyDialog_penaltyIntBaseRate"));
		this.penaltyIntSplRate.setReadonly(isReadOnly("PenaltyDialog_penaltyIntSplRate"));
		this.penaltyIntNetRate.setReadonly(isReadOnly("PenaltyDialog_penaltyIntNetRate"));
		this.penaltyIsActive.setDisabled(isReadOnly("PenaltyDialog_penaltyIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.penalty.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.btnSearchPenaltyType.setDisabled(true);
		this.penaltyEffDate.setDisabled(true);
		this.isPenaltyCapitalize.setDisabled(true);
		this.isPenaltyOnPriOnly.setDisabled(true);
		this.isPenaltyAftGrace.setDisabled(true);
		this.oDueGraceDays.setReadonly(true);
		this.penaltyPriRateBasis.setReadonly(true);
		this.penaltyPriBaseRate.setReadonly(true);
		this.penaltyPriSplRate.setReadonly(true);
		this.penaltyPriNetRate.setReadonly(true);
		this.penaltyIntRateBasis.setReadonly(true);
		this.penaltyIntBaseRate.setReadonly(true);
		this.penaltyIntSplRate.setReadonly(true);
		this.penaltyIntNetRate.setReadonly(true);
		this.penaltyIsActive.setDisabled(true);

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

		this.penaltyType.setValue("");
		this.lovDescPenaltyTypeName.setValue("");
		this.penaltyEffDate.setText("");
		this.isPenaltyCapitalize.setChecked(false);
		this.isPenaltyOnPriOnly.setChecked(false);
		this.isPenaltyAftGrace.setChecked(false);
		this.oDueGraceDays.setText("");
		this.penaltyPriRateBasis.setValue("");
		this.penaltyPriBaseRate.setValue("");
		this.penaltyPriSplRate.setValue("");
		this.penaltyPriNetRate.setValue("");
		this.penaltyIntRateBasis.setValue("");
		this.penaltyIntBaseRate.setValue("");
		this.penaltyIntSplRate.setValue("");
		this.penaltyIntNetRate.setValue("");
		this.penaltyIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Penalty aPenalty = new Penalty();
		BeanUtils.copyProperties(getPenalty(), aPenalty);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		doSetValidation();
		// fill the Penalty object with the components data
		doWriteComponentsToBean(aPenalty);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aPenalty.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aPenalty.getRecordType()).equals("")){
				aPenalty.setVersion(aPenalty.getVersion()+1);
				if(isNew){
					aPenalty.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aPenalty.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPenalty.setNewRecord(true);
				}
			}
		}else{
			aPenalty.setVersion(aPenalty.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aPenalty,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_PenaltyDialog, "Penalty");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPenalty (Penalty)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Penalty aPenalty,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aPenalty.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aPenalty.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPenalty.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aPenalty.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPenalty.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aPenalty);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aPenalty))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aPenalty.setTaskId(taskId);
			aPenalty.setNextTaskId(nextTaskId);
			aPenalty.setRoleCode(getRole());
			aPenalty.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aPenalty, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aPenalty);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aPenalty, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aPenalty, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**	
	 * Get the result after processing DataBase Operations 
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		Penalty aPenalty = (Penalty) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getPenaltyService().delete(auditHeader);

						deleteNotes=true;
					} else {
						auditHeader = getPenaltyService().saveOrUpdate(auditHeader);
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getPenaltyService().doApprove(auditHeader);

						if(aPenalty.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPenaltyService().doReject(auditHeader);
						if(aPenalty.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_PenaltyDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_PenaltyDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ WorkFlow Details +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aPenalty
	 *            (Penalty)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Penalty aPenalty, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPenalty.getBefImage(), aPenalty);
		return new AuditHeader(String.valueOf(aPenalty.getId()), null, null,
				null, auditDetail, aPenalty.getUserDetails(),getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_PenaltyDialog,
					auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Penalty");
		notes.setReference(getPenalty().getPenaltyType());
		notes.setVersion(getPenalty().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public Penalty getPenalty() {
		return this.penalty;
	}
	public void setPenalty(Penalty penalty) {
		this.penalty = penalty;
	}

	public void setPenaltyService(PenaltyService penaltyService) {
		this.penaltyService = penaltyService;
	}
	public PenaltyService getPenaltyService() {
		return this.penaltyService;
	}

	public void setPenaltyListCtrl(PenaltyListCtrl penaltyListCtrl) {
		this.penaltyListCtrl = penaltyListCtrl;
	}
	public PenaltyListCtrl getPenaltyListCtrl() {
		return this.penaltyListCtrl;
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
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}
