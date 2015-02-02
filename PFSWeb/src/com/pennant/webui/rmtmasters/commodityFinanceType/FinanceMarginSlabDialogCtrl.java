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
 * FileName    		:  FinanceMarginSlabDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-11-2011    														*
 *                                                                  						*
 * Modified Date    :  14-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.commodityFinanceType;

import java.io.Serializable;
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
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceMarginSlabService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/FinanceMarginSlab/financeMarginSlabDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceMarginSlabDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1251068503311283795L;

	private final static Logger logger = Logger.getLogger(FinanceMarginSlabDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceMarginSlabDialog; 	// autoWired
	protected Textbox 		finType; 							// autoWired
	protected Decimalbox 	slabAmount; 						// autoWired
	protected Decimalbox 	slabMargin; 						// autoWired

	protected Label 		recordStatus; 						// autoWired
	protected Grid 			grid_Basicdetails;					// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired variables
	private FinanceMarginSlab financeMarginSlab; // overHanded per parameter
	private FinanceMarginSlab prvFinanceMarginSlab; // overHanded per parameter
	private transient CommodityFinanceTypeDialogCtrl commodityFinanceTypeDialogCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_finType;
	private transient BigDecimal  	oldVar_slabAmount;
	private transient BigDecimal  	oldVar_slabMargin;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceMarginSlabDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	
	// ServiceDAOs / Domain Classes
	private transient FinanceMarginSlabService financeMarginSlabService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	
	// FinanceMarginSlabs List
	protected Button btnNew_FinanceMarginSlabs;
	protected Borderlayout borderLayout_FinanceMarginSlabsList;
	protected Paging pagingFinanceMarginSlabs;
	protected Listbox listboxFinanceMarginsSlabs;
	private int countRows = PennantConstants.listGridSize;
	int listRows;
	
	private boolean newRecord = false;
	private boolean newCommodityFinanceType = false;
	private List<FinanceMarginSlab> financeMarginSlabs;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceMarginSlabDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMarginSlab object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceMarginSlabDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("financeMarginSlab")) {
			this.financeMarginSlab = (FinanceMarginSlab) args.get("financeMarginSlab");
			FinanceMarginSlab befImage =new FinanceMarginSlab();
			BeanUtils.copyProperties(this.financeMarginSlab, befImage);
			this.financeMarginSlab.setBefImage(befImage);
			
			setFinanceMarginSlab(this.financeMarginSlab);
		} else {
			setFinanceMarginSlab(null);
		}
	
		if(getFinanceMarginSlab().isNewRecord()){
			setNewRecord(true);
		}
		
		// READ OVERHANDED parameters !
		// we get the commodityFinanceTypeDialog controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMarginSlab here.
		if(args.containsKey("commodityFinanceTypeDialogCtrl")){
			
			setCommodityFinanceTypeDialogCtrl((CommodityFinanceTypeDialogCtrl) args.get("commodityFinanceTypeDialogCtrl"));
			setNewCommodityFinanceType(true);
			
			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}

			this.financeMarginSlab.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "FinanceMarginSlabDialog");
			}
		}
		
		doLoadWorkFlow(this.financeMarginSlab.isWorkflow(),this.financeMarginSlab.getWorkflowId(),this.financeMarginSlab.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceMarginSlabDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceMarginSlab());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finType.setMaxlength(8);
	  	this.slabAmount.setMaxlength(18);
	  	this.slabAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.slabAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.slabAmount.setScale(0);
	  	this.slabMargin.setMaxlength(18);
	  	this.slabMargin.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.slabMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.slabMargin.setScale(0);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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
		
		getUserWorkspace().alocateAuthorities("FinanceMarginSlabDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMarginSlabDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMarginSlabDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceMarginSlabDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMarginSlabDialog_btnSave"));
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
	public void onClose$window_FinanceMarginSlabDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceMarginSlabDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
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
			closeWindow();
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");
		
		if(isNewCommodityFinanceType()){
			window_FinanceMarginSlabDialog.onClose();	
		}else{
			closeDialog(this.window_FinanceMarginSlabDialog, "FinanceMarginSlab");
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
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMarginSlab
	 *            FinanceMarginSlab
	 */
	public void doWriteBeanToComponents(FinanceMarginSlab aFinanceMarginSlab) {
		logger.debug("Entering") ;
		
		this.finType.setValue(aFinanceMarginSlab.getFinType());	
  		this.slabAmount.setValue(PennantAppUtil.formateAmount(aFinanceMarginSlab.getSlabAmount(),0));
  		this.slabMargin.setValue(PennantAppUtil.formateAmount(aFinanceMarginSlab.getSlabMargin(),0));
	
		this.recordStatus.setValue(aFinanceMarginSlab.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMarginSlab
	 */
	public void doWriteComponentsToBean(FinanceMarginSlab aFinanceMarginSlab) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aFinanceMarginSlab.setFinType(this.finType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.slabAmount.getValue()!=null){
			 	aFinanceMarginSlab.setSlabAmount(PennantAppUtil.unFormateAmount(this.slabAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.slabMargin.getValue()!=null){
			 	aFinanceMarginSlab.setSlabMargin(PennantAppUtil.unFormateAmount(this.slabMargin.getValue(), 0));
			}
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
		
		aFinanceMarginSlab.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceMarginSlab
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceMarginSlab aFinanceMarginSlab) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aFinanceMarginSlab == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aFinanceMarginSlab == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aFinanceMarginSlab = getFinanceMarginSlabService().getNewFinanceMarginSlab();
			
			setFinanceMarginSlab(aFinanceMarginSlab);
		} else {
			setFinanceMarginSlab(aFinanceMarginSlab);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceMarginSlab.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.slabAmount.focus();
		} else {
			this.slabMargin.focus();
			if (isNewCommodityFinanceType()){
				doEdit();
			}else if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aFinanceMarginSlab);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_FinanceMarginSlabDialog.doModal() ;
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_slabAmount = this.slabAmount.getValue();
		this.oldVar_slabMargin = this.slabMargin.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
	  	this.slabAmount.setValue(this.oldVar_slabAmount);
	  	this.slabMargin.setValue(this.oldVar_slabMargin);
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
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_slabAmount != this.slabAmount.getValue()) {
			return true;
		}
		if (this.oldVar_slabMargin != this.slabMargin.getValue()) {
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
		
		if (!this.finType.isReadonly()){
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMarginSlabDialog_FinType.value"),null,true));
		}	
		if (!this.slabAmount.isReadonly()){
			this.slabAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_FinanceMarginSlabDialog_SlabAmount.value")));
		}	
		if (!this.slabMargin.isReadonly()){
			this.slabMargin.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_FinanceMarginSlabDialog_SlabMargin.value")));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finType.setConstraint("");
		this.slabAmount.setConstraint("");
		this.slabMargin.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.slabAmount.setErrorMessage("");
		this.slabMargin.setErrorMessage("");
		logger.debug("Leaving");
		
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceMarginSlab object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceMarginSlab aFinanceMarginSlab = new FinanceMarginSlab();
		BeanUtils.copyProperties(getFinanceMarginSlab(), aFinanceMarginSlab);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceMarginSlab.getFinType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceMarginSlab.getRecordType()).equals("")){
				aFinanceMarginSlab.setVersion(aFinanceMarginSlab.getVersion()+1);
				aFinanceMarginSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFinanceMarginSlab.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCommodityFinanceType()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newFinanceTypeProcess(aFinanceMarginSlab,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinanceMarginSlabDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCommodityFinanceTypeDialogCtrl().doFillMarginSlabs(this.financeMarginSlabs);
						//true;
						// send the data back to customer
						closeWindow();
					}	

				}else if(doProcess(aFinanceMarginSlab,tranType)){
					closeWindow(); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinanceMarginSlab object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final FinanceMarginSlab aFinanceMarginSlab = getFinanceMarginSlabService().getNewFinanceMarginSlab();
		aFinanceMarginSlab.setNewRecord(true);
		setFinanceMarginSlab(aFinanceMarginSlab);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.finType.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getFinanceMarginSlab().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.slabAmount.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		
		this.finType.setReadonly(true);
		//this.slabAmount.setReadonly(isReadOnly("FinanceMarginSlabDialog_slabAmount"));
		this.slabMargin.setReadonly(isReadOnly("FinanceMarginSlabDialog_slabMargin"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.financeMarginSlab.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			
			if(newCommodityFinanceType){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCommodityFinanceType);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewCommodityFinanceType()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finType.setReadonly(true);
		this.slabAmount.setReadonly(true);
		this.slabMargin.setReadonly(true);
		
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
		
		this.finType.setValue("");
		this.slabAmount.setValue("");
		this.slabMargin.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceMarginSlab aFinanceMarginSlab = new FinanceMarginSlab();
		BeanUtils.copyProperties(getFinanceMarginSlab(), aFinanceMarginSlab);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinanceMarginSlab object with the components data
		doWriteComponentsToBean(aFinanceMarginSlab);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aFinanceMarginSlab.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMarginSlab.getRecordType()).equals("")){
				aFinanceMarginSlab.setVersion(aFinanceMarginSlab.getVersion()+1);
				if(isNew){
					aFinanceMarginSlab.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceMarginSlab.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMarginSlab.setNewRecord(true);
				}
			}
		}else{
			if(isNewCommodityFinanceType()){
				if(isNewRecord()){
					aFinanceMarginSlab.setVersion(1);
					aFinanceMarginSlab.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aFinanceMarginSlab.getRecordType()).equals("")){
					aFinanceMarginSlab.setVersion(aFinanceMarginSlab.getVersion()+1);
					aFinanceMarginSlab.setRecordType(PennantConstants.RCD_UPD);
				}
				
				if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
			aFinanceMarginSlab.setVersion(aFinanceMarginSlab.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		  }
		}
		
		// save it to database
		try {
			if(isNewCommodityFinanceType()){
				AuditHeader auditHeader =  newFinanceTypeProcess(aFinanceMarginSlab,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceMarginSlabDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCommodityFinanceTypeDialogCtrl().doFillMarginSlabs(this.financeMarginSlabs);
					//true;
					// send the data back to customer
					closeWindow();
					
				}

			}else if(doProcess(aFinanceMarginSlab,tranType)){
				closeWindow();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinanceTypeProcess(FinanceMarginSlab aFinanceMarginSlab,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aFinanceMarginSlab, tranType);
		financeMarginSlabs = new ArrayList<FinanceMarginSlab>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aFinanceMarginSlab.getId());
		valueParm[1] = String.valueOf(aFinanceMarginSlab.getSlabAmount());

		errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SlabAmount") + ":"+valueParm[1];
		
		if(getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList()!=null &&
				getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList().size()>0){
			for (int i = 0; i < getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList().size(); i++) {
				FinanceMarginSlab financeMarginSlab = getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList().get(i);
				
				
				if(aFinanceMarginSlab.getSlabAmount().equals(financeMarginSlab.getSlabAmount()) && 
						aFinanceMarginSlab.getFinType().equals(financeMarginSlab.getFinType())){ // Both Current and Existing list addresses same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aFinanceMarginSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							financeMarginSlabs.add(aFinanceMarginSlab);
						}else if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinanceMarginSlab.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							financeMarginSlabs.add(aFinanceMarginSlab);
						}else if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList().size(); j++) {
								FinanceMarginSlab marginSlab =  getCommodityFinanceTypeDialogCtrl().getCommodityFinanceMarginSlabsList().get(j);
								if(marginSlab.getFinType() == aFinanceMarginSlab.getFinType() 
										&& marginSlab.getSlabAmount().equals(aFinanceMarginSlab.getSlabAmount())){
									financeMarginSlabs.add(marginSlab);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							financeMarginSlabs.add(financeMarginSlab);
						}
					}
				}else{
					financeMarginSlabs.add(financeMarginSlab);
				}
			}
		}
		if(!recordAdded){
			financeMarginSlabs.add(aFinanceMarginSlab);
		}
		return auditHeader;
	} 

	
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinanceMarginSlab
	 *            (FinanceMarginSlab)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinanceMarginSlab aFinanceMarginSlab,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aFinanceMarginSlab.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceMarginSlab.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceMarginSlab.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceMarginSlab.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceMarginSlab.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceMarginSlab);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aFinanceMarginSlab))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aFinanceMarginSlab.setTaskId(taskId);
			aFinanceMarginSlab.setNextTaskId(nextTaskId);
			aFinanceMarginSlab.setRoleCode(getRole());
			aFinanceMarginSlab.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aFinanceMarginSlab, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aFinanceMarginSlab);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceMarginSlab, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aFinanceMarginSlab, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		FinanceMarginSlab aFinanceMarginSlab = (FinanceMarginSlab) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceMarginSlabService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceMarginSlabService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceMarginSlabService().doApprove(auditHeader);

						if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceMarginSlabService().doReject(auditHeader);
						if(aFinanceMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(
								new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceMarginSlabDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceMarginSlabDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * @param aFinanceMarginSlab
	 * @param tranType
	 * @return
	 */	
	private AuditHeader getAuditHeader(FinanceMarginSlab aFinanceMarginSlab, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceMarginSlab.getBefImage(), aFinanceMarginSlab);   
		return new AuditHeader(aFinanceMarginSlab.getFinType(),
				null,null,null,auditDetail,aFinanceMarginSlab.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceMarginSlabDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
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
		logger.debug("Entering");
		// logger.debug(event.toString());
		
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
		logger.debug("Leaving");
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("FinanceMarginSlab");
		notes.setReference(getFinanceMarginSlab().getFinType());
		notes.setVersion(getFinanceMarginSlab().getVersion());
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

	public FinanceMarginSlab getFinanceMarginSlab() {
		return this.financeMarginSlab;
	}

	public void setFinanceMarginSlab(FinanceMarginSlab financeMarginSlab) {
		this.financeMarginSlab = financeMarginSlab;
	}

	public void setFinanceMarginSlabService(FinanceMarginSlabService financeMarginSlabService) {
		this.financeMarginSlabService = financeMarginSlabService;
	}

	public FinanceMarginSlabService getFinanceMarginSlabService() {
		return this.financeMarginSlabService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceMarginSlab getPrvFinanceMarginSlab() {
		return prvFinanceMarginSlab;
	}

	public void setCommodityFinanceTypeDialogCtrl(
			CommodityFinanceTypeDialogCtrl commodityFinanceTypeDialogCtrl) {
		this.commodityFinanceTypeDialogCtrl = commodityFinanceTypeDialogCtrl;
	}

	public CommodityFinanceTypeDialogCtrl getCommodityFinanceTypeDialogCtrl() {
		return commodityFinanceTypeDialogCtrl;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewCommodityFinanceType(boolean newCommodityFinanceType) {
		this.newCommodityFinanceType = newCommodityFinanceType;
	}

	public boolean isNewCommodityFinanceType() {
		return newCommodityFinanceType;
	}

	public void setFinanceMarginSlabs(List<FinanceMarginSlab> financeMarginSlabs) {
		this.financeMarginSlabs = financeMarginSlabs;
	}

	public List<FinanceMarginSlab> getFinanceMarginSlabs() {
		return financeMarginSlabs;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public int getCountRows() {
		return countRows;
	}
}
