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
 * FileName    		:  CityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.city;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/City/cityDialog.zul file.
 */
public class CityDialogCtrl extends GFCBaseCtrl<City> {
	private static final long serialVersionUID = -210929672381582779L;
	private static final Logger logger = Logger.getLogger(CityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CityDialog;

	protected ExtendedCombobox pCCountry;
	protected ExtendedCombobox pCProvince;
	protected Uppercasebox pCCity;
	protected Textbox pCCityName;
	private transient String cityCountryTemp;
	protected Combobox pCCityClass;
	protected Textbox bankRefNo;
	protected Checkbox cityIsActive;		// autoWired

	// not autoWired Var's
	private City city; // overHanded per parameter
	private transient CityListCtrl cityListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CityService cityService;
	
	private final List<ValueLabel>	cityClassList	=	PennantAppUtil.getFieldCodeList("CITYCLSS");

	/**
	 * default constructor.<br>
	 */
	public CityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CityDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected City object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CityDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CityDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("city")) {
				this.city = (City) arguments.get("city");
				City befImage = new City();
				BeanUtils.copyProperties(this.city, befImage);
				this.city.setBefImage(befImage);

				setCity(this.city);
			} else {
				setCity(null);
			}

			doLoadWorkFlow(this.city.isWorkflow(), this.city.getWorkflowId(),
					this.city.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CityDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the cityListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete city here.
			if (arguments.containsKey("cityListCtrl")) {
				setCityListCtrl((CityListCtrl) arguments.get("cityListCtrl"));
			} else {
				setCityListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCity());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CityDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.pCCountry.setMaxlength(2);
		this.pCProvince.setMaxlength(8);
		this.pCCity.setMaxlength(8);
		this.pCCityName.setMaxlength(50);
		this.bankRefNo.setMaxlength(20);

		this.pCCountry.setMandatoryStyle(true);
		this.pCCountry.setModuleName("Country");
		this.pCCountry.setValueColumn("CountryCode");
		this.pCCountry.setDescColumn("CountryDesc");
		this.pCCountry.setValidateColumns(new String[]{"CountryCode"});
		
		this.pCProvince.setMandatoryStyle(true);
		this.pCProvince.setModuleName("Province");
		this.pCProvince.setValueColumn("CPProvince");
		this.pCProvince.setDescColumn("CPProvinceName");
		this.pCProvince.setValidateColumns(new String[]{"CPProvince"});
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
	
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
		MessageUtil.showHelpWindow(event, window_CityDialog);
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

	// Search Button Component Events

	public void onFulfill$pCCountry(Event event){
		logger.debug("Entering" + event.toString());
		doSetProvProp();
		logger.debug("Leaving" + event.toString());

	}
	private void doSetProvProp(){
		if (!StringUtils.trimToEmpty(cityCountryTemp).equals(this.pCCountry.getValue())){
			this.pCProvince.setObject("");
			this.pCProvince.setValue("");
			this.pCProvince.setDescription("");
		}
		cityCountryTemp = this.pCCountry.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.pCCountry.getValue(), Filter.OP_EQUAL);
		this.pCProvince.setFilters(filtersProvince);
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.city.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCity
	 *            
	 */
	public void doWriteBeanToComponents(City aCity) {
		logger.debug("Entering ");
		this.pCCountry.setValue(aCity.getPCCountry());
		this.pCProvince.setValue(aCity.getPCProvince());
		this.pCCity.setValue(aCity.getPCCity());
		this.pCCityName.setValue(aCity.getPCCityName());
		this.bankRefNo.setValue(aCity.getBankRefNo());
		fillComboBox(this.pCCityClass,aCity.getpCCityClassification(),cityClassList,"");
		this.recordStatus.setValue(aCity.getRecordStatus());
		this.cityIsActive.setChecked(aCity.isCityIsActive());

		if(aCity.isNewRecord()){
			this.pCCountry.setDescription("");
			this.pCProvince.setDescription("");
		}else{
			this.pCCountry.setDescription(aCity.getLovDescPCCountryName());
			this.pCProvince.setDescription(aCity.getLovDescPCProvinceName());
		}
		if(aCity.isNew() || (aCity.getRecordType() != null ? aCity.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.cityIsActive.setChecked(true);
			this.cityIsActive.setDisabled(true);
		}
		cityCountryTemp = this.pCCountry.getValue();
		doSetProvProp();
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCity
	 */
	public void doWriteComponentsToBean(City aCity) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCity.setLovDescPCCountryName(this.pCCountry.getDescription());
			aCity.setPCCountry(this.pCCountry.getValidatedValue().toUpperCase());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aCity.setLovDescPCProvinceName(this.pCProvince.getDescription());
			aCity.setPCProvince(this.pCProvince.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aCity.setPCCity(this.pCCity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCity.setPCCityName(this.pCCityName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCity.setpCCityClassification(this.pCCityClass.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCity.setBankRefNo(this.bankRefNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCity.setCityIsActive(this.cityIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCity.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCity
	 * 
	 * @throws Exception
	 */
	public void doShowDialog(City aCity) throws Exception {
		logger.debug("Entering");
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (aCity.isNew()) {
			this.pCCountry.setVisible(true);
			this.pCProvince.setVisible(true);
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.pCCountry.focus();
		} else {
			this.pCCountry.setReadonly(true);
			this.pCProvince.setReadonly(true);
			this.pCCityName.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aCity.getRecordType())){
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
			doWriteBeanToComponents(aCity);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_CityDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.pCCity.isReadonly()){
			this.pCCity.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_PCCity.value"), PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}	
		if (!this.pCCityName.isReadonly()){
			this.pCCityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_PCCityName.value"),PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.pCCountry.isReadonly()) {
			this.pCCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_PCCountry.value"), null, true,true));
		}
		if (!this.pCProvince.isReadonly()) {
			this.pCProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_PCProvince.value"), null, true,true));
		}
		if (!this.pCCityClass.isReadonly()) {
			this.pCCityClass.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_PCCityClass.value"), null, false,true));
		}
		if (!this.bankRefNo.isReadonly()){
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_CityDialog_BankRefNo.value"),PennantRegularExpressions.REGEX_ALPHANUM_CODE, false));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.pCCountry.setConstraint("");
		this.pCProvince.setConstraint("");
		this.pCCity.setConstraint("");
		this.pCCityName.setConstraint("");
		this.pCCityClass.setConstraint("");
		this.bankRefNo.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.pCCountry.setErrorMessage("");
		this.pCProvince.setErrorMessage("");
		this.pCCity.setErrorMessage("");
		this.pCCityName.setErrorMessage("");
		this.pCCityClass.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCityListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a City object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final City aCity = new City();
		BeanUtils.copyProperties(getCity(), aCity);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+
				Labels.getLabel("label_CityDialog_PCCountry.value")+" : "+aCity.getPCCountry()+","+
				Labels.getLabel("label_CityDialog_PCProvince.value")+" : "+aCity.getPCProvince()+","+
				Labels.getLabel("label_CityDialog_PCCity.value")+" : "+aCity.getPCCity();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCity.getRecordType())){
				aCity.setVersion(aCity.getVersion()+1);
				aCity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCity.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCity, tranType)) {
					refreshList();
					closeDialog();
				}
			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		
		if (getCity().isNewRecord()){			
			this.pCCity.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.pCCity.setReadonly(true);
			this.pCCountry.setReadonly(true);
			this.pCProvince.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.pCCityName.setReadonly(isReadOnly("CityDialog_pCCityName"));
		this.pCCityClass.setDisabled(isReadOnly("CityDialog_pCCitylassification"));
		this.bankRefNo.setDisabled(isReadOnly("CityDialog_BankRefNo"));
		this.cityIsActive.setDisabled(isReadOnly("CityDialog_CityIsActive"));
		if (isWorkFlowEnabled()){

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.city.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}

		}else{
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.pCCity.setReadonly(true);
		this.pCCityName.setReadonly(true);
		this.pCCityClass.setReadonly(true);
		this.bankRefNo.setReadonly(true);
		this.cityIsActive.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}		
		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before
		this.pCCountry.setValue("");
		this.pCProvince.setValue("");
		this.pCCity.setValue("");
		this.pCCityName.setValue("");
		this.pCCityClass.setValue("");
		this.bankRefNo.setValue("");
		this.cityIsActive.setChecked(false);
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final City aCity = new City();
		BeanUtils.copyProperties(getCity(), aCity);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the City object with the components data
		doWriteComponentsToBean(aCity);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCity.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCity.getRecordType())){
				aCity.setVersion(aCity.getVersion()+1);
				if(isNew){
					aCity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCity.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCity.setNewRecord(true);
				}
			}
		}else{
			aCity.setVersion(aCity.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCity,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCity (City)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(City aCity,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCity.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCity.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCity.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCity);
				}

				if (isNotesMandatory(taskId, aCity)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}			

			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCity.setTaskId(taskId);
			aCity.setNextTaskId(nextTaskId);
			aCity.setRoleCode(getRole());
			aCity.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCity, tranType);

			String operationRefs = getServiceOperations(taskId, aCity);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCity, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{			
			auditHeader =  getAuditHeader(aCity, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		City aCity = (City) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCityService().delete(auditHeader);

						deleteNotes=true;	
					}else{
						auditHeader = getCityService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getCityService().doApprove(auditHeader);

						if(aCity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getCityService().doReject(auditHeader);
						if(aCity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_CityDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_CityDialog, auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.city),true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * @param aCity 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(City aCity, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCity.getBefImage(), aCity);   
		return new AuditHeader(getReference(),null,null,null,
				auditDetail,aCity.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */ 
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.city);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCity().getPCCountry()+PennantConstants.KEY_SEPERATOR+
		 			getCity().getPCProvince()+PennantConstants.KEY_SEPERATOR+getCity().getPCCity();
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

	public City getCity() {
		return this.city;
	}
	public void setCity(City city) {
		this.city = city;
	}

	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}
	public CityService getCityService() {
		return this.cityService;
	}

	public void setCityListCtrl(CityListCtrl cityListCtrl) {
		this.cityListCtrl = cityListCtrl;
	}
	public CityListCtrl getCityListCtrl() {
		return this.cityListCtrl;
	}

}
