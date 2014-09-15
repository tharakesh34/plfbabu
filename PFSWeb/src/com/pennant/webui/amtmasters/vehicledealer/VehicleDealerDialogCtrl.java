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
 * FileName    		:  VehicleDealerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.vehicledealer;

import java.io.Serializable;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
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
 * /WEB-INF/pages/AMTMaster/VehicleDealer/vehicleDealerDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VehicleDealerDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(VehicleDealerDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleDealerDialog; // autowired
	protected Combobox dealerType; // autowired
	protected Textbox dealerName; // autowired
	protected Textbox dealerTelephone; // autowired
	protected Textbox dealerFax; // autowired
	protected Textbox dealerAddress1; // autowired
	protected Textbox dealerAddress2; // autowired
	protected Textbox dealerAddress3; // autowired
	protected Textbox dealerAddress4; // autowired
	protected Textbox dealerCountry; // autowired
	protected Textbox dealerCity; // autowired
	protected Textbox dealerProvince; // autowired
	protected Textbox lovDescCountry;
	protected Textbox lovDescCity;
	protected Textbox lovDescProvince;

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;


	// not auto wired vars
	private VehicleDealer vehicleDealer; // overhanded per param
	private VehicleDealer prvVehicleDealer; // overhanded per param
	private transient VehicleDealerListCtrl vehicleDealerListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_dealerName;
	private transient String  		oldVar_dealerTelephone;
	private transient String  		oldVar_dealerFax;
	private transient String  		oldVar_dealerAddress1;
	private transient String  		oldVar_dealerAddress2;
	private transient String  		oldVar_dealerAddress3;
	private transient String  		oldVar_dealerAddress4;
	private transient String  		oldVar_dealerCountry;
	private transient String  		oldVar_dealerCity;
	private transient String  		oldVar_dealerProvince;

	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_VehicleDealerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	protected Button btnSearchCountry;
	protected Button btnSearchProvince;
	protected Button btnSearchCity;

	// ServiceDAOs / Domain Classes
	private transient VehicleDealerService vehicleDealerService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ValueLabel> listDealerType = PennantStaticListUtil.getDealerType(); // autowired
 
	/**
	 * default constructor.<br>
	 */
	public VehicleDealerDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleDealer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleDealerDialog(Event event) throws Exception {
		logger.debug(event.toString());

		try{	/* set components visible dependent of the users rights */
			doCheckRights();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("vehicleDealer")) {
				this.vehicleDealer = (VehicleDealer) args.get("vehicleDealer");
				VehicleDealer befImage =new VehicleDealer();
				BeanUtils.copyProperties(this.vehicleDealer, befImage);
				this.vehicleDealer.setBefImage(befImage);

				setVehicleDealer(this.vehicleDealer);
			} else {
				setVehicleDealer(null);
			}

			doLoadWorkFlow(this.vehicleDealer.isWorkflow(),this.vehicleDealer.getWorkflowId(),this.vehicleDealer.getNextTaskId());

			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "VehicleDealerDialog");
			}


			// READ OVERHANDED params !
			// we get the vehicleDealerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete vehicleDealer here.
			if (args.containsKey("vehicleDealerListCtrl")) {
				setVehicleDealerListCtrl((VehicleDealerListCtrl) args.get("vehicleDealerListCtrl"));
			} else {
				setVehicleDealerListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVehicleDealer());
		}catch(Exception e){
			this.window_VehicleDealerDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.dealerName.setMaxlength(50);
		this.dealerTelephone.setMaxlength(24);
		this.dealerFax.setMaxlength(24);
		this.dealerAddress1.setMaxlength(50);
		this.dealerAddress2.setMaxlength(50);
		this.dealerAddress3.setMaxlength(50);
		this.dealerAddress4.setMaxlength(50);
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

		getUserWorkspace().alocateAuthorities("VehicleDealerDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VehicleDealerDialog_btnSave"));
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
	public void onClose$window_VehicleDealerDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_VehicleDealerDialog);
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

	// GUI Process

	public void onClick$btnSearchCountry(Event event){
		logger.debug("Entering" + event.toString());
		String sDealerCountry = this.dealerCountry.getValue();
		Object dataObject = ExtendedSearchListBox.show(this.window_VehicleDealerDialog,"Country");
		if (dataObject instanceof String){
			this.dealerCountry.setText("");
			this.lovDescCountry.setValue("");
		}else{
			Country country= (Country) dataObject;
			if (country != null) {
				this.dealerCountry.setValue(country.getCountryCode());
				this.lovDescCountry.setValue(country.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sDealerCountry).equals(
				this.dealerCountry.getValue())) {
			this.dealerProvince.setValue("");
			this.dealerCity.setValue("");
			this.lovDescProvince.setValue("");
			this.lovDescCity.setValue("");
			this.btnSearchCity.setVisible(false);
		}
		if (this.dealerCountry.getValue() != "") {
			this.btnSearchProvince.setVisible(true);
		} else {
			this.btnSearchCity.setVisible(false);
			this.btnSearchProvince.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchProvince(Event event) {
		logger.debug("Entering" + event.toString());

		String sDealerProvince = this.dealerProvince.getValue();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CPCountry", this.dealerCountry.getValue(),Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(
				this.window_VehicleDealerDialog, "Province", filters);
		if (dataObject instanceof String) {
			this.dealerProvince.setValue(dataObject.toString());
			this.lovDescProvince.setValue("");
		} else {
			Province details = (Province) dataObject;
			if (details != null) {
				this.dealerProvince.setValue(details.getCPProvince());
				this.lovDescProvince.setValue(details.getLovValue()
						+ "-" + details.getCPProvinceName());
			}
		}
		if (!StringUtils.trimToEmpty(sDealerProvince).equals(
				this.dealerProvince.getValue())) {
			this.dealerCity.setValue("");
			this.lovDescCity.setValue("");
			this.btnSearchCity.setVisible(false);
		}
		if (this.dealerProvince.getValue() != "") {
			this.btnSearchCity.setVisible(true);
		} else {
			this.btnSearchCity.setVisible(false);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCity(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("PCProvince", this.dealerProvince.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_VehicleDealerDialog, "City", filters);
		if (dataObject instanceof String) {
			this.dealerCity.setValue(dataObject.toString());
			this.lovDescCity.setValue("");
		} else {
			City details = (City) dataObject;
			if (details != null) {
				this.dealerCity.setValue(details.getPCCity());
				this.lovDescCity.setValue(details.getPCCity() + "-" + details.getPCCityName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
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
			closeDialog(this.window_VehicleDealerDialog, "VehicleDealer");	
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
	 * @param aVehicleDealer
	 *            VehicleDealer
	 */
	public void doWriteBeanToComponents(VehicleDealer aVehicleDealer) {
		logger.debug("Entering") ;
		fillComboBox(dealerType, aVehicleDealer.getDealerType(), listDealerType);
		this.dealerName.setValue(aVehicleDealer.getDealerName());
		this.dealerTelephone.setValue(aVehicleDealer.getDealerTelephone());
		this.dealerFax.setValue(aVehicleDealer.getDealerFax());
		this.dealerAddress1.setValue(aVehicleDealer.getDealerAddress1());
		this.dealerAddress2.setValue(aVehicleDealer.getDealerAddress2());
		this.dealerAddress3.setValue(aVehicleDealer.getDealerAddress3());
		this.dealerAddress4.setValue(aVehicleDealer.getDealerAddress4());
		this.dealerCountry.setValue(aVehicleDealer.getDealerCountry());
		this.dealerCity.setValue(aVehicleDealer.getDealerCity());
		this.dealerProvince.setValue(aVehicleDealer.getDealerProvince());

		if (aVehicleDealer.isNewRecord()){
			this.lovDescCountry.setValue("");
			this.lovDescProvince.setValue("");
			this.lovDescCity.setValue("");
		}else{
			this.lovDescCountry.setValue(aVehicleDealer.getLovDescCountry());
			this.lovDescProvince.setValue(aVehicleDealer.getLovDescProvince());
			this.lovDescCity.setValue(aVehicleDealer.getLovDescCity());
		}

		this.recordStatus.setValue(aVehicleDealer.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVehicleDealer
	 */
	public void doWriteComponentsToBean(VehicleDealer aVehicleDealer) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if(this.dealerType.getSelectedItem().getValue().equals(PennantConstants.List_Select)){
				throw new WrongValueException(this.dealerType,Labels.getLabel("FIELD_IS_MAND"
						,new String[]{Labels.getLabel("label_VehicleDealerDialog_DealerType.value")})); 
			}
			aVehicleDealer.setDealerType(this.dealerType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerName(this.dealerName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerTelephone(this.dealerTelephone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerFax(this.dealerFax.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress1(this.dealerAddress1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress2(this.dealerAddress2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress3(this.dealerAddress3.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setDealerAddress4(this.dealerAddress4.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setLovDescCountry(this.lovDescCountry.getValue());
			aVehicleDealer.setDealerCountry(this.dealerCountry.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setLovDescCity(this.lovDescCity.getValue());
			aVehicleDealer.setDealerCity(this.dealerCity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleDealer.setLovDescProvince(this.lovDescProvince.getValue());
			aVehicleDealer.setDealerProvince(this.dealerProvince.getValue());
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

		aVehicleDealer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aVehicleDealer
	 * @throws InterruptedException
	 */
	public void doShowDialog(VehicleDealer aVehicleDealer) throws InterruptedException {
		logger.debug("Entering") ;

		// if aVehicleDealer == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aVehicleDealer == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aVehicleDealer = getVehicleDealerService().getNewVehicleDealer();

			setVehicleDealer(aVehicleDealer);
		} else {
			setVehicleDealer(aVehicleDealer);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleDealer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.dealerName.focus();
		} else {
			this.dealerName.focus();
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
			doWriteBeanToComponents(aVehicleDealer);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if (this.dealerCountry.getValue() != "") {
				this.btnSearchProvince.setVisible(true);
				if(this.dealerProvince.getValue() == ""){
					this.btnSearchCity.setVisible(false);
				}else{
					this.btnSearchCity.setVisible(true);
			}}
			setDialog(this.window_VehicleDealerDialog);
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
		this.oldVar_dealerName=this.dealerName.getValue();
		this.oldVar_dealerTelephone=this.dealerTelephone.getValue();
		this.oldVar_dealerFax=this.dealerFax.getValue();
		this.oldVar_dealerAddress1=this.dealerAddress1.getValue();
		this.oldVar_dealerAddress2=this.dealerAddress2.getValue();
		this.oldVar_dealerAddress3=this.dealerAddress3.getValue();
		this.oldVar_dealerAddress4=this.dealerAddress4.getValue();
		this.oldVar_dealerCountry=this.dealerCountry.getValue();
		this.oldVar_dealerCity=this.dealerCity.getValue();
		this.oldVar_dealerProvince=this.dealerProvince.getValue();

		this.oldVar_recordStatus=this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.dealerName.setValue(this.oldVar_dealerName);
		this.dealerTelephone.setValue(this.oldVar_dealerTelephone);
		this.dealerFax.setValue(this.oldVar_dealerFax);
		this.dealerAddress1.setValue(this.oldVar_dealerAddress1);
		this.dealerAddress2.setValue(this.oldVar_dealerAddress2);
		this.dealerAddress3.setValue(this.oldVar_dealerAddress3);
		this.dealerAddress4.setValue(this.oldVar_dealerAddress4);
		this.dealerCountry.setValue(this.oldVar_dealerCountry);
		this.dealerCity.setValue(this.oldVar_dealerCity);
		this.dealerProvince.setValue(this.oldVar_dealerProvince);

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
		if (this.oldVar_dealerName != this.dealerName.getValue()) {
			return true;
		}
		if (this.oldVar_dealerTelephone != this.dealerTelephone.getValue()) {
			return true;
		}

		if (this.oldVar_dealerFax != this.dealerFax.getValue()) {
			return true;
		}
		if (this.oldVar_dealerAddress1 != this.dealerAddress1.getValue()) {
			return true;
		}
		if (this.oldVar_dealerAddress2 != this.dealerAddress2.getValue()) {
			return true;
		}
		if (this.oldVar_dealerAddress3 != this.dealerAddress3.getValue()) {
			return true;
		}
		if (this.oldVar_dealerAddress4 != this.dealerAddress4.getValue()) {
			return true;
		}
		if (this.oldVar_dealerCountry != this.dealerCountry.getValue()) {
			return true;
		}
		if (this.oldVar_dealerCity != this.dealerCity.getValue()) {
			return true;
		}
		if (this.oldVar_dealerProvince != this.dealerProvince.getValue()) {
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

		if (!this.dealerType.isDisabled()){
			if(this.dealerType.getSelectedItem().getValue().equals(PennantConstants.List_Select)){
				throw new WrongValueException(this.dealerType,Labels.getLabel("FIELD_IS_MAND"
						,new String[]{Labels.getLabel("label_VehicleDealerDialog_DealerType.value")})); 
			}
			this.dealerType.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerType.value"), null, true));
		}
		if (!this.dealerName.isReadonly()){
			this.dealerName.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}	
		if (!this.dealerTelephone.isReadonly()){
			this.dealerTelephone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_VehicleDealerDialog_DealerTelephone.value"), true));
		}
		if (!this.dealerAddress1.isReadonly()){
			this.dealerAddress1.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerAddress1.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.dealerAddress2.isReadonly()){
			this.dealerAddress2.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleDealerDialog_DealerAddress2.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.dealerType.setConstraint("");
		this.dealerName.setConstraint("");
		this.dealerTelephone.setConstraint("");
		this.dealerFax.setConstraint("");
		this.dealerAddress1.setConstraint("");
		this.dealerAddress2.setConstraint("");
		this.dealerAddress3.setConstraint("");
		this.dealerAddress4.setConstraint("");
		this.dealerCountry.setConstraint("");
		this.dealerCity.setConstraint("");
		this.dealerProvince.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a VehicleDealer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final VehicleDealer aVehicleDealer = new VehicleDealer();
		BeanUtils.copyProperties(getVehicleDealer(), aVehicleDealer);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aVehicleDealer.getDealerId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aVehicleDealer.getRecordType()).equals("")){
				aVehicleDealer.setVersion(aVehicleDealer.getVersion()+1);
				aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aVehicleDealer.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aVehicleDealer,tranType)){
					refreshList();
					closeDialog(this.window_VehicleDealerDialog, "VehicleDealer"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new VehicleDealer object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final VehicleDealer aVehicleDealer = getVehicleDealerService().getNewVehicleDealer();
		setVehicleDealer(aVehicleDealer);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.dealerName.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getVehicleDealer().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.dealerName.setReadonly(false);
			this.btnSearchProvince.setVisible(false);
			this.btnSearchCity.setVisible(false);
		}else{
			this.dealerType.setDisabled(true);
			this.dealerName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.dealerTelephone.setReadonly(isReadOnly("VehicleDealerDialog_dealerName"));   
		this.dealerFax.setReadonly(isReadOnly("VehicleDealerDialog_dealerFax"));        
		this.dealerAddress1.setReadonly(isReadOnly("VehicleDealerDialog_dealerAddress1"));
		this.dealerAddress2.setReadonly(isReadOnly("VehicleDealerDialog_dealerAddress2"));
		this.dealerAddress3.setReadonly(isReadOnly("VehicleDealerDialog_dealerAddress3"));
		this.dealerAddress4.setReadonly(isReadOnly("VehicleDealerDialog_dealerAddress4"));
		this.dealerCountry.setReadonly(isReadOnly("VehicleDealerDialog_dealerCountry"));    
		this.dealerCity.setReadonly(isReadOnly("VehicleDealerDialog_dealerCity"));       
		this.dealerProvince.setReadonly(isReadOnly("VehicleDealerDialog_dealerProvince"));   
		this.btnSearchCountry.setDisabled(isReadOnly("VehicleDealerDialog_btnSearchCountry")); 
		this.btnSearchProvince.setDisabled(isReadOnly("VehicleDealerDialog_btnSearchProvince"));
		this.btnSearchCity.setDisabled(isReadOnly("VehicleDealerDialog_btnSearchCity"));    
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vehicleDealer.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.dealerType.setReadonly(true);
		this.dealerName.setReadonly(true);
		this.dealerTelephone.setReadonly(true);
		this.dealerFax.setReadonly(true);
		this.dealerAddress1.setReadonly(true);
		this.dealerAddress2.setReadonly(true);
		this.dealerAddress3.setReadonly(true);
		this.dealerAddress4.setReadonly(true);
		this.dealerCountry.setReadonly(true);
		this.dealerCity.setReadonly(true);
		this.dealerProvince.setReadonly(true);
		this.btnSearchCountry.setDisabled(true);
		this.btnSearchProvince.setDisabled(true);
		this.btnSearchCity.setDisabled(true);
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

		this.dealerType.setValue("");
		this.dealerName.setValue("");
		this.dealerTelephone.setValue("");
		this.dealerFax.setValue("");
		this.dealerAddress1.setValue("");
		this.dealerAddress2.setValue("");
		this.dealerAddress3.setValue("");
		this.dealerAddress4.setValue("");
		this.dealerCountry.setValue("");
		this.dealerCity.setValue("");
		this.dealerProvince.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VehicleDealer aVehicleDealer = new VehicleDealer();
		BeanUtils.copyProperties(getVehicleDealer(), aVehicleDealer);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the VehicleDealer object with the components data
		doWriteComponentsToBean(aVehicleDealer);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVehicleDealer.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aVehicleDealer.getRecordType()).equals("")){
				aVehicleDealer.setVersion(aVehicleDealer.getVersion()+1);
				if(isNew){
					aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aVehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVehicleDealer.setNewRecord(true);
				}
			}
		}else{
			aVehicleDealer.setVersion(aVehicleDealer.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aVehicleDealer,tranType)){
				doWriteBeanToComponents(aVehicleDealer);
				refreshList();
				closeDialog(this.window_VehicleDealerDialog, "VehicleDealer");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(VehicleDealer aVehicleDealer,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aVehicleDealer.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aVehicleDealer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleDealer.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aVehicleDealer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleDealer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aVehicleDealer);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aVehicleDealer))) {
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

			aVehicleDealer.setTaskId(taskId);
			aVehicleDealer.setNextTaskId(nextTaskId);
			aVehicleDealer.setRoleCode(getRole());
			aVehicleDealer.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aVehicleDealer, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aVehicleDealer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aVehicleDealer, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aVehicleDealer, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		VehicleDealer aVehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getVehicleDealerService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getVehicleDealerService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getVehicleDealerService().doApprove(auditHeader);

						if(aVehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getVehicleDealerService().doReject(auditHeader);
						if(aVehicleDealer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VehicleDealerDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_VehicleDealerDialog, auditHeader);
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
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public VehicleDealer getVehicleDealer() {
		return this.vehicleDealer;
	}

	public void setVehicleDealer(VehicleDealer vehicleDealer) {
		this.vehicleDealer = vehicleDealer;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public VehicleDealerService getVehicleDealerService() {
		return this.vehicleDealerService;
	}

	public void setVehicleDealerListCtrl(VehicleDealerListCtrl vehicleDealerListCtrl) {
		this.vehicleDealerListCtrl = vehicleDealerListCtrl;
	}

	public VehicleDealerListCtrl getVehicleDealerListCtrl() {
		return this.vehicleDealerListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}


	private AuditHeader getAuditHeader(VehicleDealer aVehicleDealer, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVehicleDealer.getBefImage(), aVehicleDealer);   
		return new AuditHeader(String.valueOf(aVehicleDealer.getDealerId()),null,null,null,auditDetail,aVehicleDealer.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_VehicleDealerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

	private void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering");

		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		for (int i = 0; i < list.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(StringUtils.trim(list.get(i).getValue()));
			comboitem.setLabel(StringUtils.trim(list.get(i).getLabel()));
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(list.get(i).getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
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
		notes.setModuleName("VehicleDealer");
		notes.setReference(String.valueOf(getVehicleDealer().getDealerId()));
		notes.setVersion(getVehicleDealer().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.dealerType.setErrorMessage("");
		this.dealerName.setErrorMessage("");
		this.dealerTelephone.setErrorMessage("");
		this.dealerFax.setErrorMessage("");
		this.dealerAddress1.setErrorMessage("");
		this.dealerAddress2.setErrorMessage("");
		this.dealerAddress3.setErrorMessage("");
		this.dealerAddress4.setErrorMessage("");
		this.dealerCountry.setErrorMessage("");
		this.dealerCity.setErrorMessage("");
		this.dealerProvince.setErrorMessage("");
		logger.debug("Leaving");
	}


	private void refreshList(){
		final JdbcSearchObject<VehicleDealer> soVehicleDealer = getVehicleDealerListCtrl().getSearchObj();
		getVehicleDealerListCtrl().pagingVehicleDealerList.setActivePage(0);
		getVehicleDealerListCtrl().getPagedListWrapper().setSearchObject(soVehicleDealer);
		if(getVehicleDealerListCtrl().listBoxVehicleDealer!=null){
			getVehicleDealerListCtrl().listBoxVehicleDealer.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public VehicleDealer getPrvVehicleDealer() {
		return prvVehicleDealer;
	}
}
