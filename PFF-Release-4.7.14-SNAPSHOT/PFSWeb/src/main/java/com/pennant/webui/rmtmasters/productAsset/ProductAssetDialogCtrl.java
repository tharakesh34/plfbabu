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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.bmtmasters.product.ProductDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ProductAsset/productAssetDialog.zul file.
 */
public class ProductAssetDialogCtrl extends GFCBaseCtrl<ProductAsset> {
	private static final long serialVersionUID = -1251394215522173737L;
	private static final Logger logger = Logger.getLogger(ProductAssetDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */

	protected Window 	window_ProductAssetDialog; 		// autoWired
	private Textbox 	productCode;					// autoWired
	private ExtendedCombobox 	assetCode;				// autoWired
	private Textbox 	assetDesc;						// autoWired
	private Checkbox 	assetIsActive;					// autoWired

	protected Row 			statusRow;

	// not autoWired variables
	private ProductAsset productAsset; 						// overHanded per parameter
	private transient ProductDialogCtrl productDialogCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ProductService productService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();

	private boolean newRecord = false;
	private boolean newProduct = false;
	private List<ProductAsset> productAssets;


	/**
	 * default constructor.<br>
	 */
	public ProductAssetDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProductAssetDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ProductAsset object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductAssetDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProductAssetDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("productAsset")) {
				this.productAsset = (ProductAsset) arguments.get("productAsset");
				ProductAsset befImage = new ProductAsset();
				BeanUtils.copyProperties(this.productAsset, befImage);
				this.productAsset.setBefImage(befImage);

				setProductAsset(this.productAsset);
			} else {
				setProductAsset(null);
			}

			if (getProductAsset().isNewRecord()) {
				setNewRecord(true);
			}

			// READ OVERHANDED parameters !
			// we get the productDialog controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete productAsset here.

			if (arguments.containsKey("productDialogCtrl")) {

				setProductDialogCtrl((ProductDialogCtrl) arguments
						.get("productDialogCtrl"));
				setNewProduct(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}

				this.productAsset.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					getUserWorkspace().allocateRoleAuthorities(
									(String) arguments.get("roleCode"),"ProductAssetDialog");
				}
			}

			doLoadWorkFlow(this.productAsset.isWorkflow(),
					this.productAsset.getWorkflowId(),
					this.productAsset.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"ProductAssetDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getProductAsset());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProductAssetDialog.onClose();
		}
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
		this.assetCode.setMandatoryStyle(true);
		this.assetCode.setModuleName("LovFieldDetail");
		this.assetCode.setValueColumn("FieldCode");
		this.assetCode.setDescColumn("FieldCodeValue");
		this.assetCode.setValidateColumns(new String[] { "FieldCodeValue" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FieldCode","FINASSTYP", Filter.OP_EQUAL);
		this.assetCode.setFilters(filters);
		
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProductAssetDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_ProductAssetDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.productAsset.getBefImage());
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
			aProductAsset.setAssetCode(this.assetCode.getValidatedValue());
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
	 * @throws Exception
	 */
	public void doShowDialog(ProductAsset aProductAsset) throws Exception {
		logger.debug("Entering");

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
				if(getProductAsset().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					this.btnEdit.setVisible(false);
				}
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if(getProductAsset().getRecordType().equals(PennantConstants.RCD_ADD)){
					this.btnEdit.setVisible(false);
				}
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aProductAsset);
			
			this.window_ProductAssetDialog.doModal() ;
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProductAssetDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.productCode.isReadonly()){
			this.productCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductAssetDialog_ProductCode.value"),null,true));
		}
		
		if (!this.assetCode.isReadonly()){
			this.assetCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductAssetDialog_AssetCode.value"),null,true,true));
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
		this.assetCode.setConstraint("");
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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.productCode.setErrorMessage("");
		this.assetCode.setErrorMessage("");
		this.assetDesc.setErrorMessage("");
		logger.debug("Leaving");

	}

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_ProductAssetDialog_AssetCode.value")+" : "+aProductAsset.getAssetCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aProductAsset.getRecordType())){
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
					closeDialog();
				}	
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getProductAsset().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.assetCode.setReadonly(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		this.productCode.setReadonly(true);
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
			
			if(getProductAsset().isNew() || getProductAsset().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				this.assetIsActive.setDisabled(true);
			}
			
		}else{
			if (getProductAsset().isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
			
			if(getProductAsset().isNew() || getProductAsset().getRecordType().equals(PennantConstants.RCD_ADD)){
				this.assetIsActive.setDisabled(true);
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
		this.assetCode.setReadonly(true);
		this.assetDesc.setReadonly(true);
		this.assetIsActive.setDisabled(true);

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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aProductAsset.getRecordType())){
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

			if(StringUtils.isBlank(aProductAsset.getRecordType())){
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
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getProductDialogCtrl().doFillProductAsset(this.productAssets);
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
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
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}


					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aProductAsset.getRecordType())){
							aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							productAssets.add(aProductAsset);
						}else if(PennantConstants.RCD_ADD.equals(aProductAsset.getRecordType())){
							recordAdded=true;
						}else if(PennantConstants.RECORD_TYPE_NEW.equals(aProductAsset.getRecordType())){
							aProductAsset.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							productAssets.add(aProductAsset);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aProductAsset.getRecordType())){
							recordAdded=true;
							for (int j = 0; j < getProductDialogCtrl().getProductAssetList().size(); j++) {
								ProductAsset prodctAsset =  getProductDialogCtrl().getProductAssetList().get(j);
								if(prodctAsset.getProductCode() == prodctAsset.getProductCode() 
										&& prodctAsset.getAssetCode().equals(prodctAsset.getAssetCode())){
									productAssets.add(prodctAsset);
								}
							}
						}else if(PennantConstants.RECORD_TYPE_DEL.equals(aProductAsset.getRecordType())){
							aProductAsset.setNewRecord(true);
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType)){
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

	// Search Button Component Events
	
	public void onFulfill$assetCode(Event event){
		logger.debug("Entering");	   
		Object dataObject = assetCode.getObject();
		if (dataObject instanceof String){
			this.assetCode.setValue(dataObject.toString());
			this.assetCode.setDescription("");
			this.assetDesc.setValue("");
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.assetCode.setValue(details.getFieldCodeValue());
				this.assetCode.setDescription("");
				this.assetDesc.setValue(details.getValueDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	// WorkFlow Components

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
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ProductAssetDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.productAsset);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.productAsset.getProductCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
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
