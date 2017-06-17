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
 * FileName    		:  EntityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-06-2017    														*
 *                                                                  						*
 * Modified Date    :  15-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.entity;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/Entity/entityDialog.zul file. <br>
 */
public class EntityDialogCtrl extends GFCBaseCtrl<Entity>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EntityDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EntityDialog; 
	protected Textbox 		entityCode; 
	protected Textbox 		entityDesc; 
	protected Textbox 		pANNumber; 
    protected ExtendedCombobox 		country; 
    protected ExtendedCombobox 		stateCode; 
    protected ExtendedCombobox 		cityCode; 
    protected ExtendedCombobox 		pinCode; 
    protected Checkbox 		active; 
	private Entity entity; // overhanded per param

	private transient EntityListCtrl entityListCtrl; // overhanded per param
	private transient EntityService entityService;
	

	/**
	 * default constructor.<br>
	 */
	public EntityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EntityDialog";
	}
	
	/*@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(this.entity.getEntityCode());
		return referenceBuffer.toString();
	}*/
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.entity.getEntityCode()));
		return referenceBuffer.toString();
	}

	
	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_EntityDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_EntityDialog);

		
		try {
			// Get the required arguments.
			this.entity = (Entity) arguments.get("entity");
			this.entityListCtrl = (EntityListCtrl) arguments.get("entityListCtrl");

			if (this.entity == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Entity entity = new Entity();
			BeanUtils.copyProperties(this.entity, entity);
			this.entity.setBefImage(entity);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.entity.isWorkflow(), this.entity.getWorkflowId(),
					this.entity.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if(!enqiryModule){
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.entity);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[]{"CountryCode"});
		
		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[]{"CPProvince"});
		
		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] {"PCCity"});
		
		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("City");
		this.pinCode.setValidateColumns(new String[] {"PinCode"});
		
		this.entityCode.setMaxlength(8);
		this.entityDesc.setMaxlength(50);
		this.pANNumber.setMaxlength(10);
		this.pinCode.setMaxlength(10);
		this.stateCode.setMaxlength(8);
		
        setStatusDetails();
		
		logger.debug(Literal.LEAVING);
		
	}
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EntityDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	
	public void onFulfill$stateCode(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.cityCode.getValue().isEmpty() && !this.stateCode.getValue().isEmpty()) {
			fillCitydetails(this.stateCode.getValue());
		} else {
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");

		}
		logger.debug("Leaving" + event.toString());
	}

	private void fillCitydetails(String id) {
		logger.debug("Entering");

		if (id != null) {
			this.cityCode.setModuleName("City");
			this.cityCode.setValueColumn("PCCity");
			this.cityCode.setDescColumn("PCCityName");
			this.cityCode.setValidateColumns(new String[] { "PCCity" });
			Filter[] filters1 = new Filter[1];
			filters1[0] = new Filter("PCProvince", id, Filter.OP_EQUAL);
			this.cityCode.setFilters(filters1);
		}
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cityCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = cityCode.getObject();

		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details != null) {
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
				fillPindetails(details.getPCCity());
			}else{
				/*this.stateCode.setValue("");
				this.stateCode.setDescription("");
*/
			}
		}
		logger.debug("Leaving");
	}

	private void fillPindetails(String id) {
		if (id != null) {
			this.pinCode.setModuleName("PinCode");
			this.pinCode.setValueColumn("PinCode");
			this.pinCode.setDescColumn("AreaName");
			this.pinCode.setValidateColumns(new String[] { "PinCode" });
			Filter[] filters1 = new Filter[1];
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
			this.pinCode.setFilters(filters1);
		}
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {
			
		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {
				
				this.cityCode.setValue(details.getCity());
				this.cityCode.setDescription(details.getPCCityName());
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
			} 
		
		}
		logger.debug("Leaving");
	}
 

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */	
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
		
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event)  throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.entity);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		entityListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.entity.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	



      public void onFulfillCountry(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.country.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillStateCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.stateCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillCityCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.cityCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillPinCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.pinCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	





	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param entity
	 * 
	 */
	public void doWriteBeanToComponents(Entity aEntity) {
		logger.debug(Literal.ENTERING);
	
			this.entityCode.setValue(aEntity.getEntityCode());
			this.entityDesc.setValue(aEntity.getEntityDesc());
			this.pANNumber.setValue(aEntity.getPANNumber());
		   this.country.setValue(aEntity.getCountry());
		   this.stateCode.setValue(aEntity.getStateCode());
		   this.cityCode.setValue(aEntity.getCityCode());
		   this.pinCode.setValue(aEntity.getPinCode());
			this.active.setChecked(aEntity.isActive());
		
		if (aEntity.isNewRecord()){
			   this.country.setDescription("");
			   this.stateCode.setDescription("");
			   this.cityCode.setDescription("");
			   this.pinCode.setDescription("");
			   this.country.setValue("IN");
			   this.country.setDescription("INDIAone");
		}else{
			   this.country.setDescription(aEntity.getCountryName());
			   this.stateCode.setDescription(aEntity.getStateCodeName());
			   this.cityCode.setDescription(aEntity.getCityCodeName());
			   this.pinCode.setDescription(aEntity.getPinCodeName());
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEntity
	 */
	public void doWriteComponentsToBean(Entity aEntity) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Entity Code
		try {
		    aEntity.setEntityCode(this.entityCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Entity Name
		try {
		    aEntity.setEntityDesc(this.entityDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//PAN Number
		try {
		    aEntity.setPANNumber(this.pANNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Country
				try {
					aEntity.setCountry(this.country.getValidatedValue());
					aEntity.setCountryName(this.country.getDescription());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				//State Code
				try {
					aEntity.setStateCode(this.stateCode.getValidatedValue());
					aEntity.setStateCodeName(this.stateCode.getDescription());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				//City Code
				try {
					aEntity.setCityCode(this.cityCode.getValidatedValue());
					aEntity.setCityCodeName(this.cityCode.getDescription());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				//Pin Code
				try {
					aEntity.setPinCode(this.pinCode.getValidatedValue());
				}catch (WrongValueException we ) {
					wve.add(we);
				}	
		//Active
		try {
			aEntity.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param entity
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Entity entity) {
		logger.debug(Literal.LEAVING);

		if (entity.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entityCode.focus();
		} else {
				this.entityCode.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(entity.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.entityDesc.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(entity);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.entityCode.isReadonly()){
			this.entityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityCode.value"),PennantRegularExpressions.REGEX_ALPHANUM,true));
		}
		if (!this.entityDesc.isReadonly()){
			this.entityDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_EntityDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		if (!this.pANNumber.isReadonly()){
			this.pANNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_PANNumber.value"),PennantRegularExpressions.REGEX_PANNUMBER,true));
		}
		if (!this.country.isReadonly()){
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_Country.value"),null,true,true));
		}
		if (!this.stateCode.isReadonly()){
			this.stateCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_StateCode.value"),null,true,true));
		}
		if (!this.cityCode.isReadonly()){
			this.cityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_CityCode.value"),null,true,true));
		}
		if (!this.pinCode.isReadonly()){
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EntityDialog_PinCode.value"),null,true,true));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.entityCode.setConstraint("");
		this.entityDesc.setConstraint("");
		this.pANNumber.setConstraint("");
		this.country.setConstraint("");
		this.stateCode.setConstraint("");
		this.cityCode.setConstraint("");
		this.pinCode.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Entity Code
		//Entity Name
		//PAN Number
		//Country
		//State Code
		//City Code
		//Pin Code
		//Active
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		
	
	logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a Entity object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final Entity aEntity = new Entity();
		BeanUtils.copyProperties(this.entity, aEntity);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aEntity.getEntityCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aEntity.getRecordType()).equals("")){
				aEntity.setVersion(aEntity.getVersion()+1);
				aEntity.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aEntity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aEntity.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEntity.getNextTaskId(), aEntity);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aEntity,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
			}
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.entity.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.entityCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.entityCode);
			
		}
	
			readOnlyComponent(isReadOnly("EntityDialog_EntityDesc"), this.entityDesc);
			readOnlyComponent(isReadOnly("EntityDialog_PANNumber"), this.pANNumber);
			readOnlyComponent(isReadOnly("EntityDialog_Country"), this.country);
			readOnlyComponent(isReadOnly("EntityDialog_StateCode"), this.stateCode);
			readOnlyComponent(isReadOnly("EntityDialog_CityCode"), this.cityCode);
			readOnlyComponent(isReadOnly("EntityDialog_PinCode"), this.pinCode);
			readOnlyComponent(isReadOnly("EntityDialog_Active"), this.active);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.entity.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}

			
		logger.debug(Literal.LEAVING);
	}	
			
		/**
		 * Set the components to ReadOnly. <br>
		 */
		public void doReadOnly() {
			logger.debug(Literal.LEAVING);
			
	
			readOnlyComponent(true, this.entityCode);
			readOnlyComponent(true, this.entityDesc);
			readOnlyComponent(true, this.pANNumber);
			readOnlyComponent(true, this.country);
			readOnlyComponent(true, this.stateCode);
			readOnlyComponent(true, this.cityCode);
			readOnlyComponent(true, this.pinCode);
			readOnlyComponent(true, this.active);

			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(true);
				}
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
	
			}

			logger.debug(Literal.LEAVING);
		}

		
		/**
		 * Clears the components values. <br>
		 */
		public void doClear() {
			logger.debug("Entering");
				this.entityCode.setValue("");
				this.entityDesc.setValue("");
				this.pANNumber.setValue("");
			  	this.country.setValue("");
			  	this.country.setDescription("");
			  	this.stateCode.setValue("");
			  	this.stateCode.setDescription("");
			  	this.cityCode.setValue("");
			  	this.cityCode.setDescription("");
			  	this.pinCode.setValue("");
			  	this.pinCode.setDescription("");
				this.active.setChecked(false);

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final Entity aEntity = new Entity();
			BeanUtils.copyProperties(this.entity, aEntity);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aEntity);

			isNew = aEntity.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aEntity.getRecordType())) {
					aEntity.setVersion(aEntity.getVersion() + 1);
					if (isNew) {
						aEntity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aEntity.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aEntity.setNewRecord(true);
					}
				}
			} else {
				aEntity.setVersion(aEntity.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aEntity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (final DataAccessException e) {
				logger.error(e);
				MessageUtil.showError(e);
			}
			logger.debug("Leaving");
		}

		/**
		 * Set the workFlow Details List to Object
		 * 
		 * @param aAuthorizedSignatoryRepository
		 *            (AuthorizedSignatoryRepository)
		 * 
		 * @param tranType
		 *            (String)
		 * 
		 * @return boolean
		 * 
		 */
		private boolean doProcess(Entity aEntity, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aEntity.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aEntity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aEntity.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aEntity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aEntity.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aEntity);
					}

					if (isNotesMandatory(taskId, aEntity)) {
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

				aEntity.setTaskId(taskId);
				aEntity.setNextTaskId(nextTaskId);
				aEntity.setRoleCode(getRole());
				aEntity.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aEntity, tranType);
				String operationRefs = getServiceOperations(taskId, aEntity);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aEntity, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aEntity, tranType);
				processCompleted = doSaveProcess(auditHeader, null);
			}

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * Get the result after processing DataBase Operations
		 * 
		 * @param AuditHeader
		 *            auditHeader
		 * @param method
		 *            (String)
		 * @return boolean
		 * 
		 */

		private boolean doSaveProcess(AuditHeader auditHeader, String method) {
			logger.debug("Entering");
			boolean processCompleted = false;
			int retValue = PennantConstants.porcessOVERIDE;
			Entity aEntity = (Entity) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = entityService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = entityService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = entityService.doApprove(auditHeader);

							if (aEntity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = entityService.doReject(auditHeader);
							if (aEntity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_EntityDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_EntityDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.entity), true);
						}
					}

					if (retValue == PennantConstants.porcessOVERIDE) {
						auditHeader.setOveride(true);
						auditHeader.setErrorMessage(null);
						auditHeader.setInfoMessage(null);
						auditHeader.setOverideMessage(null);
					}
				}
			} catch (InterruptedException e) {
				logger.error("Exception: ", e);
			}
			setOverideMap(auditHeader.getOverideMap());

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * @param aAuthorizedSignatoryRepository
		 * @param tranType
		 * @return
		 */

		private AuditHeader getAuditHeader(Entity aEntity, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aEntity.getBefImage(), aEntity);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aEntity.getUserDetails(),
					getOverideMap());
		}

		public void setEntityService(EntityService entityService) {
			this.entityService = entityService;
		}
			
}