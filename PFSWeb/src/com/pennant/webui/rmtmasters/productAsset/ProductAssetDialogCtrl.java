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
 * FileName    		:  ProductAssetDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.productAsset;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.bmtmasters.product.ProductDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ProductAsset/productAssetDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ProductAssetDialogCtrl extends GFCBaseCtrl implements Serializable{

	private static final long serialVersionUID = -1251394215522173737L;
	private final static Logger logger = Logger.getLogger(ProductAssetDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window 	window_ProductAssetDialog; 		// autoWired
	private Textbox 	productCode;					// autoWired
	private Textbox 	assetCode;						// autoWired
	private Textbox 	assetDesc;						// autoWired
	private Checkbox 	assetIsActive;					// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not autoWired variables
	private ProductAsset productAsset; 						// overHanded per parameter
	private transient ProductDialogCtrl productDialogCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  	oldVar_productCode;
	private transient String 	oldVar_assetCode;
	private transient String  	oldVar_assetDesc;
	private transient boolean 	oldVar_assetIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ProductAssetDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	protected Button btnSearchFieldCode; // autoWired

	// ServiceDAOs / Domain Classes
	private transient ProductService productService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	private boolean newRecord = false;
	private boolean newProduct = false;
	private List<ProductAsset> productAssets;


	/**
	 * default constructor.<br>
	 */
	public ProductAssetDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ProductAsset object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductAssetDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("productAsset")) {
			this.productAsset = (ProductAsset) args.get("productAsset");
			ProductAsset befImage =new ProductAsset();
			BeanUtils.copyProperties(this.productAsset, befImage);
			this.productAsset.setBefImage(befImage);

			setProductAsset(this.productAsset);
		} else {
			setProductAsset(null);
		}

		if(getProductAsset().isNewRecord()){
			setNewRecord(true);
		}

		// READ OVERHANDED parameters !
		// we get the productDialog controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete productAsset here.

		if(args.containsKey("productDialogCtrl")){

			setProductDialogCtrl((ProductDialogCtrl) args.get("productDialogCtrl"));
			setNewProduct(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}

			this.productAsset.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "ProductAssetDialog");
			}
		}

		doLoadWorkFlow(this.productAsset.isWorkflow(), this.productAsset.getWorkflowId(),this.productAsset.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "ProductAssetDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getProductAsset());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.productCode.setMaxlength(8);
		this.assetCode.setMaxlength(8);
		this.assetDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("ProductAssetDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnSave"));
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
	public void onClose$window_ProductAssetDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_ProductAssetDialog);
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

		if(isNewProduct()){
			window_ProductAssetDialog.onClose();	
		}else{
			closeDialog(this.window_ProductAssetDialog, "ProductAsset");
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProductAsset
	 *            ProductAsset
	 */
	public void doWriteBeanToComponents(ProductAsset aProductAsset) {
		logger.debug("Entering") ;

		this.productCode.setValue(aProductAsset.getProductCode());	
		this.assetCode.setValue(aProductAsset.getAssetCode());
		this.assetDesc.setValue(aProductAsset.getAssetDesc());
		this.assetIsActive.setChecked(aProductAsset.isAssetIsActive());

		if(aProductAsset.isNew() || aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.assetIsActive.setChecked(true);
			this.assetIsActive.setDisabled(true);
		}
		this.recordStatus.setValue(aProductAsset.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProductAsset
	 */
	public void doWriteComponentsToBean(ProductAsset aProductAsset) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProductAsset.setProductCode(this.productCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProductAsset.setAssetCode(this.assetCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProductAsset.setAssetDesc(this.assetDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProductAsset.setAssetIsActive(this.assetIsActive.isChecked());
		} catch (WrongValueException we) {
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

		aProductAsset.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aProductAsset
	 * @throws InterruptedException
	 */
	public void doShowDialog(ProductAsset aProductAsset) throws InterruptedException {
		logger.debug("Entering") ;

		// if aProductAsset == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aProductAsset == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aProductAsset = getProductService().getNewProductAsset();
			setProductAsset(aProductAsset);
		} else {
			setProductAsset(aProductAsset);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aProductAsset.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.assetCode.focus();
		} else {
			this.assetDesc.focus();
			if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aProductAsset);
			doStoreInitValues();
			this.window_ProductAssetDialog.doModal() ;
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
		this.oldVar_productCode = this.productCode.getValue();
		this.oldVar_assetCode = this.assetCode.getValue();
		this.oldVar_assetDesc = this.assetDesc.getValue();
		this.oldVar_assetIsActive = this.assetIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.productCode.setValue(this.oldVar_productCode);
		this.assetCode.setValue(this.oldVar_assetCode);
		this.assetDesc.setValue(this.oldVar_assetDesc);
		this.assetIsActive.setChecked(this.oldVar_assetIsActive);
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

		if (this.oldVar_productCode != this.productCode.getValue()) {
			return true;
		}
		if (this.oldVar_assetCode != this.assetCode.getValue()) {
			return true;
		}
		if (this.oldVar_assetDesc != this.assetDesc.getValue()) {
			return true;
		}
		if (this.oldVar_assetIsActive != this.assetIsActive.isChecked()) {
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

		if (!this.productCode.isReadonly()){
			this.productCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_ProductAssetDialog_ProductCode.value")}));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.productCode.setConstraint("");
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
		this.productCode.setErrorMessage("");
		this.assetCode.setErrorMessage("");
		this.assetDesc.setErrorMessage("");
		logger.debug("Leaving");

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ProductAsset object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	

		final ProductAsset aProductAsset = new ProductAsset();
		BeanUtils.copyProperties(getProductAsset(), aProductAsset);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aProductAsset.getAssetCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aProductAsset.getRecordType()).equals("")){
				aProductAsset.setVersion(aProductAsset.getVersion()+1);
				aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aProductAsset.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(aProductAsset.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aProductAsset.setVersion(aProductAsset.getVersion() + 1);
				aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newProductProcess(aProductAsset,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ProductAssetDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getProductDialogCtrl().doFillProductAsset(this.productAssets);
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
	 * Create a new ProductAsset object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		final ProductAsset aProductAsset = getProductService().getNewProductAsset();
		setProductAsset(aProductAsset);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.assetCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getProductAsset().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.btnSearchFieldCode.setDisabled(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		this.productCode.setReadonly(true);
		this.assetCode.setReadonly(true);
		this.assetDesc.setReadonly(true);
		this.assetIsActive.setDisabled(isReadOnly("ProductAssetDialog_assetIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.productAsset.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if (getProductAsset().isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewProduct()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.productCode.setReadonly(true);
		this.btnSearchFieldCode.setDisabled(true);
		this.assetCode.setReadonly(true);
		this.assetDesc.setReadonly(true);

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
		// remove validation, if they are a saved before

		this.productCode.setValue("");
		this.assetCode.setValue("");
		this.assetDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final ProductAsset aProductAsset = new ProductAsset();
		BeanUtils.copyProperties(getProductAsset(), aProductAsset);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the ProductAsset object with the components data
		doWriteComponentsToBean(aProductAsset);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aProductAsset.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aProductAsset.getRecordType()).equals("")){
				aProductAsset.setVersion(aProductAsset.getVersion()+1);
				if(isNew){
					aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProductAsset.setNewRecord(true);
				}
			}
		}else{
			aProductAsset.setVersion(aProductAsset.getVersion() + 1);

			if(isNew){
				aProductAsset.setVersion(1);
				aProductAsset.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType = PennantConstants.TRAN_UPD;
			}

			if(StringUtils.trimToEmpty(aProductAsset.getRecordType()).equals("")){
				aProductAsset.setVersion(aProductAsset.getVersion()+1);
				aProductAsset.setRecordType(PennantConstants.RCD_UPD);
			}

			if(aProductAsset.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader =  newProductProcess(aProductAsset,tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ProductAssetDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getProductDialogCtrl().doFillProductAsset(this.productAssets);
				closeWindow();

			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Creating List of ProductAsset Details
	 * @param aProductAsset
	 * @param tranType
	 * @return
	 */
	private AuditHeader newProductProcess(ProductAsset aProductAsset,String tranType){
		logger.debug("Entering");

		boolean recordAdded=false;
		AuditHeader auditHeader= getAuditHeader(aProductAsset, tranType);
		productAssets = new ArrayList<ProductAsset>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aProductAsset.getProductCode();
		valueParm[1] = aProductAsset.getAssetCode();

		errParm[0] = PennantJavaUtil.getLabel("label_ProductCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AssetCode") + ":"+ valueParm[1];

		if(getProductDialogCtrl().getProductAssetList()!=null && getProductDialogCtrl().getProductAssetList().size()>0){
			for (int i = 0; i < getProductDialogCtrl().getProductAssetList().size(); i++) {
				ProductAsset productAsset = getProductDialogCtrl().getProductAssetList().get(i);

				if(aProductAsset.getAssetCode().equals(productAsset.getAssetCode()) && 
						aProductAsset.getProductCode().equals(productAsset.getProductCode())){ // Both Current and Existing list addresses same

					if(aProductAsset.isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}


					if(tranType==PennantConstants.TRAN_DEL){
						if(aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							productAssets.add(aProductAsset);
						}else if(aProductAsset.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							productAssets.add(aProductAsset);
						}else if(aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getProductDialogCtrl().getProductAssetList().size(); j++) {
								ProductAsset prodctAsset =  getProductDialogCtrl().getProductAssetList().get(j);
								if(prodctAsset.getProductCode() == prodctAsset.getProductCode() 
										&& prodctAsset.getAssetCode().equals(prodctAsset.getAssetCode())){
									productAssets.add(prodctAsset);
								}
							}
						}else if(aProductAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							aProductAsset.setNewRecord(true);
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							productAssets.add(productAsset);
						}
					}
				}else{
					productAssets.add(productAsset);
				}
			}
		}
		if(!recordAdded){
			productAssets.add(aProductAsset);
		}
		return auditHeader;
	} 

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onClick$btnSearchFieldCode(Event event){
		logger.debug("Entering");	   
		
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FieldCode","FINASSTYP", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_ProductAssetDialog, "LovFieldDetail",filters);

		if (dataObject instanceof String){
			this.assetCode.setValue(dataObject.toString());
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.assetCode.setValue(details.getFieldCodeValue());
				this.assetDesc.setValue(details.getValueDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aProductAsset
	 * @param tranType
	 * @return
	 */	
	private AuditHeader getAuditHeader(ProductAsset aProductAsset, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProductAsset.getBefImage(), aProductAsset);   
		return new AuditHeader(aProductAsset.getProductCode(),
				null,null,null,auditDetail,aProductAsset.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_ProductAssetDialog, auditHeader);
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
		notes.setModuleName("ProductAsset");
		notes.setReference(getProductAsset().getProductCode());
		notes.setVersion(getProductAsset().getVersion());
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

	public ProductAsset getProductAsset() {
		return productAsset;
	}
	public void setProductAsset(ProductAsset productAsset) {
		this.productAsset = productAsset;
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

	public ProductService getProductService() {
		return productService;
	}
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductDialogCtrl getProductDialogCtrl() {
		return productDialogCtrl;
	}
	public void setProductDialogCtrl(ProductDialogCtrl productDialogCtrl) {
		this.productDialogCtrl = productDialogCtrl;
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

	public boolean isNewProduct() {
		return newProduct;
	}
	public void setNewProduct(boolean newProduct) {
		this.newProduct = newProduct;
	}
}
