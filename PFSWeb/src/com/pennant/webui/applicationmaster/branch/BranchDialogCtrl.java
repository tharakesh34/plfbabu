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
 * FileName    		:  BranchDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.branch;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/Branch/branchDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class BranchDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4832204841676720745L;
	private final static Logger logger = Logger.getLogger(BranchDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BranchDialog;// autoWired

	protected Textbox 		branchCode; 		// autoWired
	protected Textbox 		branchDesc; 		// autoWired
	protected Textbox 		branchAddrLine1; 	// autoWired
	protected Textbox 		branchAddrLine2; 	// autoWired
	protected Textbox 		branchPOBox; 		// autoWired
	protected Textbox 		branchCity; 		// autoWired
	protected Textbox 		branchProvince; 	// autoWired
	protected Textbox 		branchCountry; 		// autoWired
	protected Textbox 		branchFax; 			// autoWired
	protected Textbox 		branchTel; 			// autoWired
	protected Textbox 		branchSwiftBankCode;// autoWired
	protected Textbox 		branchSwiftCountry; // autoWired
	protected Textbox 		branchSwiftLocCode; // autoWired
	protected Textbox 		branchSwiftBrnCde; 	// autoWired
	protected Textbox 		branchSortCode; 	// autoWired
	protected Checkbox 		branchIsActive; 	// autoWired

	protected Label 		recordStatus; 		// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not autoWired Var's
	private Branch branch; // overHanded per parameter
	private transient BranchListCtrl branchListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_branchCode;
	private transient String  		oldVar_branchDesc;
	private transient String  		oldVar_branchAddrLine1;
	private transient String  		oldVar_branchAddrLine2;
	private transient String  		oldVar_branchPOBox;
	private transient String  		oldVar_branchCity;
	private transient String  		oldVar_branchProvince;
	private transient String  		oldVar_branchCountry;
	private transient String  		oldVar_branchFax;
	private transient String  		oldVar_branchTel;
	private transient String  		oldVar_branchSwiftBankCode;
	private transient String  		oldVar_branchSwiftCountry;
	private transient String  		oldVar_branchSwiftLocCode;
	private transient String  		oldVar_branchSwiftBrnCde;
	private transient String  		oldVar_branchSortCode;
	private transient boolean  		oldVar_branchIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_BranchDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autoWired
	protected Button btnEdit; 	// autoWired
	protected Button btnDelete; // autoWired
	protected Button btnSave; 	// autoWired
	protected Button btnCancel; // autoWired
	protected Button btnClose;	// autoWired
	protected Button btnHelp; 	// autoWired
	protected Button btnNotes; 	// autoWired
	
	protected Button 			btnSearchBranchCity; // autoWired
	protected Textbox 			lovDescBranchCityName;
	private transient String 	oldVar_lovDescBranchCityName;
	protected Button 			btnSearchBranchProvince; // autoWired
	protected Textbox 			lovDescBranchProvinceName;
	private transient String 	oldVar_lovDescBranchProvinceName;
	protected Button 			btnSearchBranchCountry; // autoWired
	protected Textbox 			lovDescBranchCountryName;
	private transient String 	oldVar_lovDescBranchCountryName;
	protected Button 			btnSearchBranchSwiftCountry; // autoWired
	protected Textbox 			lovDescBranchSwiftCountryName;
	private transient String 	oldVar_lovDescBranchSwiftCountryName;
	
	// ServiceDAOs / Domain Classes
	private transient BranchService branchService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public BranchDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Branch object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BranchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("branch")) {
			this.branch = (Branch) args.get("branch");
			Branch befImage =new Branch();
			BeanUtils.copyProperties(this.branch, befImage);
			this.branch.setBefImage(befImage);
			
			setBranch(this.branch);
		} else {
			setBranch(null);
		}
	
		doLoadWorkFlow(this.branch.isWorkflow(),this.branch.getWorkflowId(),this.branch.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "BranchDialog");
		}
	
		// READ OVERHANDED parameters !
		// we get the branchListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete branch here.
		if (args.containsKey("branchListCtrl")) {
			setBranchListCtrl((BranchListCtrl) args.get("branchListCtrl"));
		} else {
			setBranchListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getBranch());
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.branchCode.setMaxlength(PennantConstants.branchLength);
		this.branchDesc.setMaxlength(50);
		this.branchAddrLine1.setMaxlength(50);
		this.branchAddrLine2.setMaxlength(50);
		this.branchPOBox.setMaxlength(8);
		this.branchCity.setMaxlength(8);
		this.branchProvince.setMaxlength(8);
		this.branchCountry.setMaxlength(2);
		this.branchFax.setMaxlength(50);
		this.branchTel.setMaxlength(50);
		this.branchSwiftBankCode.setMaxlength(4);
		this.branchSwiftCountry.setMaxlength(2);
		this.branchSwiftLocCode.setMaxlength(2);
		this.branchSwiftBrnCde.setMaxlength(3);
		this.branchSortCode.setMaxlength(4);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("BranchDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnSave"));
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
	public void onClose$window_BranchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());		
		doSave();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
		doEdit();
		// remember the old Var's
		doStoreInitValues();
		logger.debug("Leaving"+event.toString());
	}


	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_BranchDialog);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering"+event.toString());
		doNew();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
		doCancel();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving"+event.toString());
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
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_BranchDialog, "Branch");
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBranch
	 *            Branch
	 */
	public void doWriteBeanToComponents(Branch aBranch) {
		logger.debug("Entering");
		this.branchCode.setValue(aBranch.getBranchCode());
		this.branchDesc.setValue(aBranch.getBranchDesc());
		this.branchAddrLine1.setValue(aBranch.getBranchAddrLine1());
		this.branchAddrLine2.setValue(aBranch.getBranchAddrLine2());
		this.branchPOBox.setValue(aBranch.getBranchPOBox());
		this.branchCity.setValue(aBranch.getBranchCity());
		this.branchProvince.setValue(aBranch.getBranchProvince());
		this.branchCountry.setValue(aBranch.getBranchCountry());
		this.branchFax.setValue(aBranch.getBranchFax());
		this.branchTel.setValue(aBranch.getBranchTel());
		this.branchSwiftBankCode.setValue(aBranch.getBranchSwiftBankCde());
		this.branchSwiftCountry.setValue(aBranch.getBranchSwiftCountry());
		this.branchSwiftLocCode.setValue(aBranch.getBranchSwiftLocCode());
		this.branchSwiftBrnCde.setValue(aBranch.getBranchSwiftBrnCde());
		this.branchSortCode.setValue(aBranch.getBranchSortCode());
		this.branchIsActive.setChecked(aBranch.isBranchIsActive());

	if (aBranch.isNewRecord()){
		   this.lovDescBranchCityName.setValue("");
		   this.lovDescBranchProvinceName.setValue("");
		   this.lovDescBranchCountryName.setValue("");
		   this.lovDescBranchSwiftCountryName.setValue("");
	}else{
		   this.lovDescBranchCityName.setValue(aBranch.getBranchCity()+"-"+
				   aBranch.getLovDescBranchCityName());
		   this.lovDescBranchProvinceName.setValue(aBranch.getBranchProvince()+"-"+
				   aBranch.getLovDescBranchProvinceName());
		   this.lovDescBranchCountryName.setValue(aBranch.getBranchCountry()+"-"+
				   aBranch.getLovDescBranchCountryName());
		   this.lovDescBranchSwiftCountryName.setValue(aBranch.getBranchSwiftCountry()+"-"+
				   aBranch.getLovDescBranchSwiftCountryName());
	}
		this.recordStatus.setValue(aBranch.getRecordStatus());
		
		if(aBranch.isNew() || (aBranch.getRecordType() != null ? aBranch.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.branchIsActive.setChecked(true);
			this.branchIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBranch
	 */
	public void doWriteComponentsToBean(Branch aBranch) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aBranch.setBranchCode(StringUtils.leftPad(this.branchCode.getValue()
		    		,PennantConstants.branchLength,'0'));		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchDesc(this.branchDesc.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchAddrLine1(this.branchAddrLine1.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchAddrLine2(this.branchAddrLine2.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchPOBox(this.branchPOBox.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aBranch.setLovDescBranchCityName(this.lovDescBranchCityName.getValue());
	 		aBranch.setBranchCity(this.branchCity.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aBranch.setLovDescBranchProvinceName(this.lovDescBranchProvinceName.getValue());
	 		aBranch.setBranchProvince(this.branchProvince.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aBranch.setLovDescBranchCountryName(this.lovDescBranchCountryName.getValue());
	 		aBranch.setBranchCountry(this.branchCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchFax(this.branchFax.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchTel(this.branchTel.getValue());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchSwiftBankCde(this.branchSwiftBankCode.getValue().toUpperCase());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchSwiftCountry(this.branchSwiftCountry.getValue().toUpperCase());	
		    aBranch.setLovDescBranchSwiftCountryName(this.lovDescBranchSwiftCountryName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchSwiftLocCode(this.branchSwiftLocCode.getValue().toUpperCase());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchSwiftBrnCde(this.branchSwiftBrnCde.getValue().toUpperCase());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aBranch.setBranchSortCode(this.branchSortCode.getValue().toUpperCase());		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aBranch.setBranchIsActive(this.branchIsActive.isChecked());
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
		
		aBranch.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBranch
	 * @throws InterruptedException
	 */
	public void doShowDialog(Branch aBranch) throws InterruptedException {
		logger.debug("Entering");
		// if aBranch == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aBranch == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aBranch = getBranchService().getNewBranch();
			
			setBranch(aBranch);
		} else {
			setBranch(aBranch);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aBranch.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.branchCode.focus();
		} else {
			this.branchDesc.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aBranch.getRecordType()).equals("")){
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
			doWriteBeanToComponents(aBranch);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_BranchDialog);
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
	 * Stores the initialize values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_branchCode = this.branchCode.getValue();
		this.oldVar_branchDesc = this.branchDesc.getValue();
		this.oldVar_branchAddrLine1 = this.branchAddrLine1.getValue();
		this.oldVar_branchAddrLine2 = this.branchAddrLine2.getValue();
		this.oldVar_branchPOBox = this.branchPOBox.getValue();
 		this.oldVar_branchCity = this.branchCity.getValue();
 		this.oldVar_lovDescBranchCityName = this.lovDescBranchCityName.getValue();
 		this.oldVar_branchProvince = this.branchProvince.getValue();
 		this.oldVar_lovDescBranchProvinceName = this.lovDescBranchProvinceName.getValue();
 		this.oldVar_branchCountry = this.branchCountry.getValue();
 		this.oldVar_lovDescBranchCountryName = this.lovDescBranchCountryName.getValue();
		this.oldVar_branchFax = this.branchFax.getValue();
		this.oldVar_branchTel = this.branchTel.getValue();
		this.oldVar_branchSwiftBankCode = this.branchSwiftBankCode.getValue();
		this.oldVar_branchSwiftCountry = this.branchSwiftCountry.getValue();
 		this.oldVar_lovDescBranchSwiftCountryName = this.lovDescBranchSwiftCountryName.getValue();
		this.oldVar_branchSwiftLocCode = this.branchSwiftLocCode.getValue();
		this.oldVar_branchSwiftBrnCde = this.branchSwiftBrnCde.getValue();
		this.oldVar_branchSortCode = this.branchSortCode.getValue();
		this.oldVar_branchIsActive = this.branchIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.branchCode.setValue(this.oldVar_branchCode);
		this.branchDesc.setValue(this.oldVar_branchDesc);
		this.branchAddrLine1.setValue(this.oldVar_branchAddrLine1);
		this.branchAddrLine2.setValue(this.oldVar_branchAddrLine2);
		this.branchPOBox.setValue(this.oldVar_branchPOBox);
 		this.branchCity.setValue(this.oldVar_branchCity);
 		this.lovDescBranchCityName.setValue(this.oldVar_lovDescBranchCityName);
 		this.branchProvince.setValue(this.oldVar_branchProvince);
 		this.lovDescBranchProvinceName.setValue(this.oldVar_lovDescBranchProvinceName);
 		this.branchCountry.setValue(this.oldVar_branchCountry);
 		this.lovDescBranchCountryName.setValue(this.oldVar_lovDescBranchCountryName);
		this.branchFax.setValue(this.oldVar_branchFax);
		this.branchTel.setValue(this.oldVar_branchTel);
		this.branchSwiftBankCode.setValue(this.oldVar_branchSwiftBankCode);
		this.branchSwiftCountry.setValue(this.oldVar_branchSwiftCountry);
		this.lovDescBranchSwiftCountryName.setValue(this.oldVar_lovDescBranchSwiftCountryName);
		this.branchSwiftLocCode.setValue(this.oldVar_branchSwiftLocCode);
		this.branchSwiftBrnCde.setValue(this.oldVar_branchSwiftBrnCde);
		this.branchSortCode.setValue(this.oldVar_branchSortCode);
		this.branchIsActive.setChecked(this.oldVar_branchIsActive);
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
		
		if (this.oldVar_branchCode != this.branchCode.getValue()) {
			return true;
		}
		if (this.oldVar_branchDesc != this.branchDesc.getValue()) {
			return true;
		}
		if (this.oldVar_branchAddrLine1 != this.branchAddrLine1.getValue()) {
			return true;
		}
		if (this.oldVar_branchAddrLine2 != this.branchAddrLine2.getValue()) {
			return true;
		}
		if (this.oldVar_branchPOBox != this.branchPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_branchCity != this.branchCity.getValue()) {
			return true;
		}
		if (this.oldVar_branchProvince != this.branchProvince.getValue()) {
			return true;
		}
		if (this.oldVar_branchCountry != this.branchCountry.getValue()) {
			return true;
		}
		if (this.oldVar_branchFax != this.branchFax.getValue()) {
			return true;
		}
		if (this.oldVar_branchTel != this.branchTel.getValue()) {
			return true;
		}
		if (this.oldVar_branchSwiftBankCode != this.branchSwiftBankCode.getValue()) {
			return true;
		}
		if (this.oldVar_branchSwiftCountry != this.branchSwiftCountry.getValue()) {
			return true;
		}
		if (this.oldVar_branchSwiftLocCode != this.branchSwiftLocCode.getValue()) {
			return true;
		}
		if (this.oldVar_branchSwiftBrnCde != this.branchSwiftBrnCde.getValue()) {
			return true;
		}
		if (this.oldVar_branchSortCode != this.branchSortCode.getValue()) {
			return true;
		}
		if (this.oldVar_branchIsActive != this.branchIsActive.isChecked()) {
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
		
		if (!this.branchCode.isReadonly()){
			this.branchCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	
		if (!this.branchDesc.isReadonly()){
			this.branchDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		if (!this.branchAddrLine1.isReadonly()){
			this.branchAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine1.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
		if (!this.branchAddrLine2.isReadonly()){
			this.branchAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine2.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.branchPOBox.isReadonly()) {
			this.branchPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.branchFax.isReadonly()){
			this.branchFax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchFax.value"),true));
		}	
		if (!this.branchTel.isReadonly()){
			this.branchTel.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchTel.value"),true));
		}	
		if (!this.branchSwiftBankCode.isReadonly()){
			this.branchSwiftBankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBankCde.value"),PennantRegularExpressions.REGEX_ALPHANUM_FL4, true));
		}	
		if (!this.branchSwiftLocCode.isReadonly()){
			this.branchSwiftLocCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftLocCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_FL2, true));
		}	
		if (!this.branchSwiftBrnCde.isReadonly()){
			this.branchSwiftBrnCde.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBrnCde.value"),PennantRegularExpressions.REGEX_ALPHANUM_FL3, true));
		}	
		if (!this.branchSortCode.isReadonly()){
			this.branchSortCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSortCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_FL4, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.branchCode.setConstraint("");
		this.branchDesc.setConstraint("");
		this.branchAddrLine1.setConstraint("");
		this.branchAddrLine2.setConstraint("");
		this.branchPOBox.setConstraint("");
		this.branchFax.setConstraint("");
		this.branchTel.setConstraint("");
		this.branchSwiftBankCode.setConstraint("");
		this.branchSwiftCountry.setConstraint("");
		this.branchSwiftLocCode.setConstraint("");
		this.branchSwiftBrnCde.setConstraint("");
		this.branchSortCode.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Set Validations for LOV Fields
	 */	
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescBranchCityName.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCity.value")
				 , null, true));
		this.lovDescBranchProvinceName.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchProvince.value"), 
				null, true));
		this.lovDescBranchCountryName.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_BranchDialog_BranchCountry.value"), null, true));
		this.lovDescBranchSwiftCountryName.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftCountry.value"),
				null, true));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */	
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescBranchCityName.setConstraint("");
		this.lovDescBranchProvinceName.setConstraint("");
		this.lovDescBranchCountryName.setConstraint("");
		this.lovDescBranchSwiftCountryName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.branchCode.setErrorMessage("");
		this.branchDesc.setErrorMessage("");
		this.branchAddrLine1.setErrorMessage("");
		this.branchAddrLine2.setErrorMessage("");
		this.branchPOBox.setErrorMessage("");
		this.branchFax.setErrorMessage("");
		this.branchTel.setErrorMessage("");
		this.branchSwiftBankCode.setErrorMessage("");
		this.branchSwiftCountry.setErrorMessage("");
		this.branchSwiftLocCode.setErrorMessage("");
		this.branchSwiftBrnCde.setErrorMessage("");
		this.branchSortCode.setErrorMessage("");
		this.lovDescBranchCityName.setErrorMessage("");
		this.lovDescBranchProvinceName.setErrorMessage("");
		this.lovDescBranchCountryName.setErrorMessage("");
		this.lovDescBranchSwiftCountryName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		final JdbcSearchObject<Branch> soObject = getBranchListCtrl().getSearchObj();
		getBranchListCtrl().pagingBranchList.setActivePage(0);
		getBranchListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getBranchListCtrl().listBoxBranch!=null){
			getBranchListCtrl().listBoxBranch.getListModel();
		}
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Branch object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " 
				+ aBranch.getBranchCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aBranch.getRecordType()).equals("")){
				aBranch.setVersion(aBranch.getVersion()+1);
				aBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aBranch.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBranch,tranType)){
					refreshList();
					closeDialog(this.window_BranchDialog, "Branch"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Branch object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Branch() in the front end.
		// we get it from the back end.
		final Branch aBranch = getBranchService().getNewBranch();
		aBranch.setNewRecord(true);
		setBranch(aBranch);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.branchCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getBranch().isNewRecord()){
		  	this.branchCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnSearchBranchProvince.setVisible(false);
			this.btnSearchBranchCity.setVisible(false);
		}else{
			this.branchCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		this.branchDesc.setReadonly(isReadOnly("BranchDialog_branchDesc"));
		this.branchAddrLine1.setReadonly(isReadOnly("BranchDialog_branchAddrLine1"));
		this.branchAddrLine2.setReadonly(isReadOnly("BranchDialog_branchAddrLine2"));
		this.branchPOBox.setReadonly(isReadOnly("BranchDialog_branchPOBox"));
	  	this.btnSearchBranchCity.setDisabled(isReadOnly("BranchDialog_branchCity"));
	  	this.btnSearchBranchProvince.setDisabled(isReadOnly("BranchDialog_branchProvince"));
	  	this.btnSearchBranchCountry.setDisabled(isReadOnly("BranchDialog_branchCountry"));
		this.branchFax.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.branchTel.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.branchSwiftBankCode.setReadonly(isReadOnly("BranchDialog_branchSwiftBankCde"));
		this.branchSwiftCountry.setReadonly(isReadOnly("BranchDialog_branchSwiftCountry"));
		this.btnSearchBranchSwiftCountry.setDisabled(isReadOnly("BranchDialog_branchCountry"));
		this.branchSwiftLocCode.setReadonly(isReadOnly("BranchDialog_branchSwiftLocCode"));
		this.branchSwiftBrnCde.setReadonly(isReadOnly("BranchDialog_branchSwiftBrnCde"));
		this.branchSortCode.setReadonly(isReadOnly("BranchDialog_branchSortCode"));
	 	this.branchIsActive.setDisabled(isReadOnly("BranchDialog_branchIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.branch.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.branchCode.setReadonly(true);
		this.branchDesc.setReadonly(true);
		this.branchAddrLine1.setReadonly(true);
		this.branchAddrLine2.setReadonly(true);
		this.branchPOBox.setReadonly(true);
		this.btnSearchBranchCity.setDisabled(true);
		this.btnSearchBranchProvince.setDisabled(true);
		this.btnSearchBranchCountry.setDisabled(true);
		this.branchFax.setReadonly(true);
		this.branchTel.setReadonly(true);
		this.branchSwiftBankCode.setReadonly(true);
		this.branchSwiftCountry.setReadonly(true);
		this.btnSearchBranchSwiftCountry.setDisabled(true);
		this.branchSwiftLocCode.setReadonly(true);
		this.branchSwiftBrnCde.setReadonly(true);
		this.branchSortCode.setReadonly(true);
		this.branchIsActive.setDisabled(true);
		
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
		this.branchCode.setValue("");
		this.branchDesc.setValue("");
		this.branchAddrLine1.setValue("");
		this.branchAddrLine2.setValue("");
		this.branchPOBox.setValue("");
	  	this.branchCity.setValue("");
		this.lovDescBranchCityName.setValue("");
	  	this.branchProvince.setValue("");
		this.lovDescBranchProvinceName.setValue("");
	  	this.branchCountry.setValue("");
		this.lovDescBranchCountryName.setValue("");
		this.branchFax.setValue("");
		this.branchTel.setValue("");
		this.branchSwiftBankCode.setValue("");
		this.branchSwiftCountry.setValue("");
		this.lovDescBranchSwiftCountryName.setValue("");
		this.branchSwiftLocCode.setValue("");
		this.branchSwiftBrnCde.setValue("");
		this.branchSortCode.setValue("");
		this.branchIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Branch object with the components data
		doWriteComponentsToBean(aBranch);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		
		isNew = aBranch.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBranch.getRecordType()).equals("")){
				aBranch.setVersion(aBranch.getVersion()+1);
				if(isNew){
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBranch.setNewRecord(true);
				}
			}
		}else{
			aBranch.setVersion(aBranch.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aBranch,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_BranchDialog, "Branch");
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
	 * @param aBranch (Branch)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Branch aBranch,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aBranch.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBranch.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aBranch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBranch.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aBranch);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aBranch))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aBranch.setTaskId(taskId);
			aBranch.setNextTaskId(nextTaskId);
			aBranch.setRoleCode(getRole());
			aBranch.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aBranch, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aBranch);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aBranch, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{			
			auditHeader =  getAuditHeader(aBranch, tranType);
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
		Branch aBranch = (Branch) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getBranchService().delete(auditHeader);
						
						deleteNotes=true;	
					}else{
						auditHeader = getBranchService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getBranchService().doApprove(auditHeader);
						
						if(aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getBranchService().doReject(auditHeader);
						if(aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_BranchDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);
				
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

   public void onClick$btnSearchBranchCity(Event event){
	   logger.debug("Entering"+event.toString());
	   Filter[] filters = new Filter[1] ;
	   filters[0]= new Filter("PCProvince", this.branchProvince.getValue(), Filter.OP_EQUAL);  
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_BranchDialog,"City",filters);
	   if (dataObject instanceof String){
		   this.branchCity.setValue(dataObject.toString());
		   this.lovDescBranchCityName.setValue("");
	   }else{
		   City details= (City) dataObject;
		   if (details != null) {
			   this.branchCity.setValue(details.getPCCity());
			   this.lovDescBranchCityName.setValue(details.getPCCity()+"-"+
			   details.getPCCityName());
			   }
		   }
	   logger.debug("Leaving"+event.toString());
	}
   
   public void onClick$btnSearchBranchProvince(Event event){
	   logger.debug("Entering"+event.toString());
	   String sBranchProvince= this.branchProvince.getValue();
	   Filter[] filters = new Filter[1] ;
	   filters[0]= new Filter("CPCountry", this.branchCountry.getValue(), Filter.OP_EQUAL);  

	   Object dataObject = ExtendedSearchListBox.show(
			   this.window_BranchDialog,"Province",filters);
	   if (dataObject instanceof String){
		   this.branchProvince.setValue(dataObject.toString());
		   this.lovDescBranchProvinceName.setValue("");
	   }else{
		   Province details= (Province) dataObject;
		   if (details != null) {
			   this.branchProvince.setValue(details.getCPProvince());
			   this.lovDescBranchProvinceName.setValue(details.getCPProvince()+"-"+
					   details.getCPProvinceName());
		   }
	   }
	   if (!StringUtils.trimToEmpty(sBranchProvince).equals(this.branchProvince.getValue())){
		   this.branchCity.setValue("");
		   this.lovDescBranchCityName.setValue("");
	   }
	   if(this.branchProvince.getValue()!=""){

		   this.btnSearchBranchCity.setVisible(true); 
	   }
	   else{
		   this.btnSearchBranchCity.setVisible(false);
	   }
	   logger.debug("Leaving"+event.toString());
   }

   public void onClick$btnSearchBranchCountry(Event event){
	   logger.debug("Entering"+event.toString());
	   String sBranchCountry= this.branchCountry.getValue();

	   Object dataObject = ExtendedSearchListBox.show(this.window_BranchDialog,"Country");
	   if (dataObject instanceof String){
		   this.branchCountry.setValue(dataObject.toString());
		   this.lovDescBranchCountryName.setValue("");
	   }else{
		   Country details= (Country) dataObject;
		   if (details != null) {
			   this.branchCountry.setValue(details.getCountryCode());
			   this.lovDescBranchCountryName.setValue(details.getCountryCode()+"-"+
					   details.getCountryDesc());
		   }
	   }
	   if (!StringUtils.trimToEmpty(sBranchCountry).equals(this.branchCountry.getValue())){
		   this.branchProvince.setValue("");
		   this.branchCity.setValue("");
		   this.lovDescBranchProvinceName.setValue("");
		   this.lovDescBranchCityName.setValue("");
		   this.btnSearchBranchCity.setVisible(false);
	   } 
	   if(this.branchCountry.getValue()!=""){
		   this.btnSearchBranchProvince.setVisible(true);		  
	   }
	   else{
		   this.btnSearchBranchCity.setVisible(false);
		   this.btnSearchBranchProvince.setVisible(false);

	   }
	   logger.debug("Leaving"+event.toString());
   }
   
   public void onClick$btnSearchBranchSwiftCountry(Event event){
	   logger.debug("Entering"+event.toString());

	   Object dataObject = ExtendedSearchListBox.show(this.window_BranchDialog,"Country");
	   if (dataObject instanceof String){
		   this.branchSwiftCountry.setValue(dataObject.toString());
		   this.lovDescBranchSwiftCountryName.setValue("");
	   }else{
		   Country details= (Country) dataObject;
		   if (details != null) {
			   this.branchSwiftCountry.setValue(details.getCountryCode());
			   this.lovDescBranchSwiftCountryName.setValue(details.getCountryCode()+"-"+
					   details.getCountryDesc());
		   }
	   }
	   logger.debug("Leaving"+event.toString());
   }

   
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * @param aBranch 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Branch aBranch, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBranch.getBefImage(), aBranch);   
		return new AuditHeader(String.valueOf(aBranch.getId()),null,null,null,auditDetail,aBranch.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		
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
		logger.debug("Leaving"+event.toString());
	}
	
	//Check notes Entered or not	
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

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Branch");
		notes.setReference(getBranch().getBranchCode());
		notes.setVersion(getBranch().getVersion());
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

	public Branch getBranch() {
		return this.branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	public BranchService getBranchService() {
		return this.branchService;
	}

	public void setBranchListCtrl(BranchListCtrl branchListCtrl) {
		this.branchListCtrl = branchListCtrl;
	}
	public BranchListCtrl getBranchListCtrl() {
		return this.branchListCtrl;
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
