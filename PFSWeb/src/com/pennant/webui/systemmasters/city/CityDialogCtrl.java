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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/City/cityDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CityDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -210929672381582779L;
	private final static Logger logger = Logger.getLogger(CityDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CityDialog; 		// autoWired

	protected Textbox 		pCCounty; 				// autoWired
	protected Textbox 		pCProvince; 			// autoWired
	protected Textbox 		pCCity; 				// autoWired
	protected Textbox 		pCCityName; 			// autoWired
	protected Label   		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not autoWired Var's
	private City city; // overHanded per parameter
	private transient CityListCtrl cityListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_pCCounty;
	private transient String  		oldVar_pCProvince;
	private transient String  		oldVar_pCCity;
	private transient String  		oldVar_pCCityName;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CityDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	protected Button 			btnSearchPCCounty;  // autoWired
	protected Textbox 			lovDescPCCountyName;
	private transient String 	oldVar_lovDescPCCountyName;

	protected Button 			btnSearchPCProvince; // autoWired
	protected Textbox 			lovDescPCProvinceName;
	private transient String 	oldVar_lovDescPCProvinceName;

	// ServiceDAOs / Domain Classes
	private transient CityService cityService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CityDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected City object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CityDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);


		// READ OVERHANDED parameters !
		if (args.containsKey("city")) {
			this.city = (City) args.get("city");
			City befImage =new City();
			BeanUtils.copyProperties(this.city, befImage);
			this.city.setBefImage(befImage);

			setCity(this.city);
		} else {
			setCity(null);
		}

		doLoadWorkFlow(this.city.isWorkflow(),
				this.city.getWorkflowId(),this.city.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CityDialog");
		}

		// READ OVERHANDED parameters !
		// we get the cityListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete city here.
		if (args.containsKey("cityListCtrl")) {
			setCityListCtrl((CityListCtrl) args.get("cityListCtrl"));
		} else {
			setCityListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCity());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.pCCounty.setMaxlength(2);
		this.pCProvince.setMaxlength(8);
		this.pCCity.setMaxlength(8);
		this.pCCityName.setMaxlength(50);

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
		getUserWorkspace().alocateAuthorities("CityDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CityDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
	public void onClose$window_CityDialog(Event event) throws Exception {
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
		// remember the old Var's
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
		PTMessageUtils.showHelpWindow(event, window_CityDialog);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchPCCounty(Event event){
		logger.debug("Entering" + event.toString());
		String country = this.pCCounty.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_CityDialog,"Country");
		if (dataObject instanceof String){
			this.pCCounty.setValue(dataObject.toString());
			this.lovDescPCCountyName.setValue("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.pCCounty.setValue(details.getLovValue());
				this.lovDescPCCountyName.setValue(details.getLovValue()+"-"+
						details.getCountryDesc());
			}
		}

		if (!StringUtils.trimToEmpty(country).equals(this.pCCounty.getValue())){
			this.pCProvince.setValue("");
			this.lovDescPCProvinceName.setValue("");
		}

		this.btnSearchPCProvince.setDisabled(false);
		logger.debug("Leaving" + event.toString());

	}

	public void onClick$btnSearchPCProvince(Event event){
		logger.debug("Entering" + event.toString());	   
		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("CPCountry", this.pCCounty.getValue(), Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CityDialog,"Province",filters);

		if (dataObject instanceof String){
			this.pCProvince.setValue(dataObject.toString());
			this.lovDescPCProvinceName.setValue("");
		}else{
			Province details= (Province) dataObject;
			if (details != null) {
				this.pCProvince.setValue(details.getCPProvince());
				this.lovDescPCProvinceName.setValue(details.getCPProvince()+"-"+
						details.getCPProvinceName());
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
		logger.debug("Entering ");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_CityDialog, "City");
		}	
		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
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
		this.pCCounty.setValue(aCity.getPCCounty());
		this.pCProvince.setValue(aCity.getPCProvince());
		this.pCCity.setValue(aCity.getPCCity());
		this.pCCityName.setValue(aCity.getPCCityName());
		this.recordStatus.setValue(aCity.getRecordStatus());

		if(aCity.isNewRecord()){
			this.lovDescPCCountyName.setValue("");
			this.lovDescPCProvinceName.setValue("");
		}else{
			this.lovDescPCCountyName.setValue(aCity.getPCCounty()+"-"+
					aCity.getLovDescPCCountyName());
			this.lovDescPCProvinceName.setValue(aCity.getPCProvince()+"-"+ 
					aCity.getLovDescPCProvinceName());
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCity
	 */
	public void doWriteComponentsToBean(City aCity) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCity.setLovDescPCCountyName(this.lovDescPCCountyName.getValue());
			aCity.setPCCounty(this.pCCounty.getValue().toUpperCase());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aCity.setLovDescPCProvinceName(this.lovDescPCProvinceName.getValue());
			aCity.setPCProvince(this.pCProvince.getValue());
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

		doRemoveValidation();
		doRemoveLOVValidation();

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
	 * @throws InterruptedException
	 */
	public void doShowDialog(City aCity) throws InterruptedException {
		logger.debug("Entering ");
		// if aCity == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCity == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCity = getCityService().getNewCity();
			setCity(aCity);
		} else {
			setCity(aCity);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCity.isNew()) {
			this.btnSearchPCCounty.setVisible(true);
			this.btnSearchPCProvince.setVisible(true);
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.pCCounty.focus();
		} else {
			this.btnSearchPCCounty.setVisible(false);
			this.btnSearchPCProvince.setVisible(false);
			this.pCCityName.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aCity.getRecordType()).equals("")){
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CityDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_pCCounty = this.pCCounty.getValue();
		this.oldVar_pCProvince = this.pCProvince.getValue();
		this.oldVar_pCCity = this.pCCity.getValue();
		this.oldVar_pCCityName = this.pCCityName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();

		this.oldVar_lovDescPCCountyName = this.lovDescPCCountyName.getValue();
		this.oldVar_lovDescPCProvinceName = this.lovDescPCProvinceName.getValue();
		logger.debug("Leaving ");

	}

	/**
	 * Resets the initial values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.pCCounty.setValue(this.oldVar_pCCounty);
		this.pCProvince.setValue(this.oldVar_pCProvince);
		this.pCCity.setValue(this.oldVar_pCCity);
		this.pCCityName.setValue(this.oldVar_pCCityName);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		this.lovDescPCCountyName.setValue(this.oldVar_lovDescPCCountyName);
		this.lovDescPCProvinceName.setValue(this.oldVar_lovDescPCProvinceName);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving ");
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

		if (this.oldVar_pCCounty != this.pCCounty.getValue()) {
			return true;
		}
		if (this.oldVar_pCProvince != this.pCProvince.getValue()) {
			return true;
		}
		if (this.oldVar_pCCity != this.pCCity.getValue()) {
			return true;
		}
		if (this.oldVar_pCCityName != this.pCCityName.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.pCCity.isReadonly()){
			this.pCCity.setConstraint(new SimpleConstraint(PennantConstants.ALPHA_CAPS_REGEX,
					Labels.getLabel("FIELD_CHAR_CAPS",new String[]{Labels.getLabel(
					"label_CityDialog_PCCity.value")})));
		}	
		if (!this.pCCityName.isReadonly()){
			this.pCCityName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER_SPACE",new String[]{Labels.getLabel(
					"label_CityDialog_PCCityName.value")})));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.pCCounty.setConstraint("");
		this.pCProvince.setConstraint("");
		this.pCCity.setConstraint("");
		this.pCCityName.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescPCCountyName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
				"label_CityDialog_PCCounty.value")}));
		this.lovDescPCProvinceName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
				"label_CityDialog_PCProvince.value")}));

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescPCCountyName.setConstraint("");
		this.lovDescPCProvinceName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.pCCounty.setErrorMessage("");
		this.pCProvince.setErrorMessage("");
		this.pCCity.setErrorMessage("");
		this.pCCityName.setErrorMessage("");
		this.lovDescPCCountyName.setErrorMessage("");
		this.lovDescPCProvinceName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<City> soObject = getCityListCtrl().getSearchObj();
		getCityListCtrl().pagingCityList.setActivePage(0);
		getCityListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getCityListCtrl().listBoxCity!=null){
			getCityListCtrl().listBoxCity.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		+ aCity.getPCCounty();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCity.getRecordType()).equals("")){
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
				if(doProcess(aCity,tranType)){
					refreshList();
					closeDialog(this.window_CityDialog, "City"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new City object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new City() in the fronEend.
		// we get it from the backEnd.
		final City aCity = getCityService().getNewCity();
		aCity.setNewRecord(true);
		setCity(aCity);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.pCCounty.focus();
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
			this.btnSearchPCCounty.setVisible(false);
			this.btnSearchPCProvince.setVisible(false);
			this.btnCancel.setVisible(true);
		}		

		this.pCCityName.setReadonly(isReadOnly("CityDialog_pCCityName"));

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
			btnCancel.setVisible(true);
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
		this.pCCounty.setValue("");
		this.pCProvince.setValue("");
		this.pCCity.setValue("");
		this.pCCityName.setValue("");
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aCity.getRecordType()).equals("")){
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
				closeDialog(this.window_CityDialog, "City");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving ");
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

		aCity.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCity.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCity.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCity);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aCity))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
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

			aCity.setTaskId(taskId);
			aCity.setNextTaskId(nextTaskId);
			aCity.setRoleCode(getRole());
			aCity.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCity, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCity);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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

	//Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving ");
	}

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("City");
		notes.setReference(getReference());
		notes.setVersion(getCity().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getCity().getPCCounty()+PennantConstants.KEY_SEPERATOR+
		 			getCity().getPCProvince()+PennantConstants.KEY_SEPERATOR+getCity().getPCCity();
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
