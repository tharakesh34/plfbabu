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
 * FileName    		:  HolidayMasterDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.smtmasters.holidaymaster;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/HolidayMaster/holidayMasterDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class HolidayMasterDetailsDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(HolidayMasterDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_HolidayMasterDetailsDialog; // autowired

	protected Textbox holidayCode; 			// autowired
	protected Decimalbox holidayYear; 		// autowired
	protected Combobox holidayType; 		// autowired
	protected Datebox holidays; 			// autowired
	protected Textbox holidayDesc; 			// autowired

	protected Label recordStatus; 			// autowired
	
	
	
	// not auto wired vars
	private HolidayDetail holidayDetail; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_holidayCode;
	private transient BigDecimal  	oldVar_holidayYear;
	private transient String  		oldVar_holidayType;
	private transient Date  		oldVar_holidays;
	private transient String  		oldVar_holidayDesc1;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_HolidayMasterDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autowire
	protected Button btnEdit; 		// autowire
	protected Button btnDelete; 	// autowire
	protected Button btnSave; 		// autowire
	protected Button btnCancel; 	// autowire
	protected Button btnClose; 		// autowire
	protected Button btnHelp; 		// autowire
	protected Button btnNotes; 		// autowire
	protected Button btnHolidayNew;  //autowire
	
	// ServiceDAOs / Domain Classes
	private transient HolidayMasterService holidayMasterService;
	private transient PagedListService pagedListService;
	private transient HolidayMasterDialogCtrl holidayMasterDialogCtrl;
	
	/**
	 * default constructor.<br>
	 */
	public HolidayMasterDetailsDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected HolidayMaster object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HolidayMasterDetailsDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		
		// READ OVERHANDED params !
		if (args.containsKey("HolidayMasterDialogCtrl")) {
			this.holidayMasterDialogCtrl=(HolidayMasterDialogCtrl)args.get("HolidayMasterDialogCtrl");
			
		} else {
			this.holidayMasterDialogCtrl=null;
		}
		
		if (args.containsKey("holidayDetail")) {
			this.holidayDetail = (HolidayDetail) args.get("holidayDetail");
		} else {
			setHolidayDetail(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getHolidayDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		//Empty sent any required attributes
		this.holidayCode.setMaxlength(3);
	  	this.holidayYear.setMaxlength(4);
	  	this.holidayYear.setFormat("####");
	  	this.holidayYear.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.holidayYear.setScale(0);
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
		getUserWorkspace().alocateAuthorities("HolidayMasterDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnSave"));
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
	public void onClose$window_HolidayMasterDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_HolidayMasterDetailsDialog);
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
		} catch (final Exception e) {
			// close anyway
			closeThis(this.window_HolidayMasterDetailsDialog, "HolidayMasterDetails");
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
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		
		this.window_HolidayMasterDetailsDialog.onClose();
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
	 * @param aHolidayMaster
	 *            HolidayMaster
	 * @throws ParseException 
	 * @throws WrongValueException 
	 */
	public void doWriteBeanToComponents(HolidayDetail aHolidayDetail) {
		logger.debug("Entering ");
		this.holidayCode.setValue(aHolidayDetail.getHolidayCode());
  		this.holidayYear.setValue(aHolidayDetail.getHolidayYear().toString());
		if(aHolidayDetail.getHolidayType().equalsIgnoreCase("P")){
			this.holidayType.setSelectedIndex(0);
		}else{
			this.holidayType.setSelectedIndex(1);
		}
		
		if(holidayDetail.getHoliday()==null){
			Calendar  calendar = Calendar.getInstance();
			calendar.set(aHolidayDetail.getHolidayYear().intValue(), 00, 01);
			this.holidays.setValue(calendar.getTime());
		}else{
			this.holidays.setValue(DateUtility.getDate(DateUtility
					.formatUtilDate(holidayDetail.getHoliDayDate(),
							PennantConstants.dateFormat)));
		}
		
		this.holidayDesc.setValue(aHolidayDetail.getHolidayDescription());
		logger.debug("Leaving ");	
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aHolidayMaster
	 */
	@SuppressWarnings("deprecation")
	public void doWriteComponentsToBean(HolidayDetail aHolidayDetail) {
		logger.debug("Entering ");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
	 		if (StringUtils.trimToEmpty(this.holidayCode.getValue()).equals("")){
				throw new WrongValueException(this.holidayCode, Labels.getLabel(
						"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
								"label_HolidayMasterDialog_HolidayCode.value")}));
			}
	 		aHolidayDetail.setHolidayCode(this.holidayCode.getValue());
		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.holidayYear.getValue()!=null){
				aHolidayDetail.setHolidayYear(PennantAppUtil.unFormateAmount(
						this.holidayYear.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		if (StringUtils.trimToEmpty(this.holidayType.getValue()).equals("")){
				throw new WrongValueException(this.holidayType, Labels.getLabel(
						"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
								"label_HolidayMasterDialog_HolidayType.value")}));
			}
	 		aHolidayDetail.setHolidayType(this.holidayType.getSelectedItem().getValue().toString());
		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		if (this.holidays.getValue().equals("")) {
				throw new WrongValueException(this.holidays, Labels.getLabel(
						"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
								"label_HolidayMasterDialog_Holidays.value")}));
			}
	 		Calendar calendar = Calendar.getInstance();
	 		calendar.setTime(this.holidays.getValue());
	 		aHolidayDetail.setHoliday(calendar);
		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			if (this.holidays.getValue().getYear()+1900 != Integer.parseInt(
					aHolidayDetail.getHolidayYear().toString())){
				throw new WrongValueException(this.holidays, Labels.getLabel(
						"DATE_YEAR",new String[]{Labels.getLabel(
								"label_HolidayMasterDialog_Holidays.value")}));
			}
			
		}catch (WrongValueException we) {
			wve.add(we);
		}
			
		try {
	 		if (StringUtils.trimToEmpty(this.holidayDesc.getValue()).equals("")){
				throw new WrongValueException(this.holidayDesc, Labels.getLabel(
						"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
								"label_HolidayMasterDialog_HolidayDesc.value")}));
			}
	 		aHolidayDetail.setHolidayDescription(this.holidayDesc.getValue());
		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
			
		if (wve.size()>0) {
			doRemoveValidation();
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug("Leaving ");
	}
	

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aHolidayMaster
	 * @throws InterruptedException
	 */
	public void doShowDialog(HolidayDetail aHolidayDetail) throws InterruptedException {
		logger.debug("Entering ");
		// if aHolidayMaster == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aHolidayDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aHolidayDetail = new HolidayDetail();
			aHolidayDetail.setNewRecord(true);
			aHolidayDetail.setHolidayType("N");
			setHolidayDetail(aHolidayDetail);
			
		} else {
			setHolidayDetail(aHolidayDetail);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aHolidayDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.holidayType.focus();
		} else {
			doEdit();
			this.btnDelete.setVisible(true);
			this.holidayDesc.focus();
			
		}
		btnCancel.setVisible(false);
		this.btnSave.setVisible(true);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aHolidayDetail);

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_HolidayMasterDetailsDialog.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_holidayCode = this.holidayCode.getValue();
		this.oldVar_holidayYear = this.holidayYear.getValue();
		this.oldVar_holidayType = this.holidayType.getValue();
		this.oldVar_holidays = this.holidays.getValue();
		this.oldVar_holidayDesc1 = this.holidayDesc.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.holidayCode.setValue(this.oldVar_holidayCode);
	  	this.holidayYear.setValue(this.oldVar_holidayYear);
		this.holidayType.setValue(this.oldVar_holidayType);
		this.holidays.setValue(this.oldVar_holidays);
		this.holidayDesc.setValue(this.oldVar_holidayDesc1);
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering ");
		boolean changed = false;
		
		if (this.oldVar_holidayCode != this.holidayCode.getValue()) {
			changed = true;
		}
		if (this.oldVar_holidayYear != this.holidayYear.getValue()) {
			changed = true;
		}
		if (this.oldVar_holidayType != this.holidayType.getValue()) {
			changed = true;
		}
		if (this.oldVar_holidays != this.holidays.getValue()) {
			changed = true;
		}
		if (this.oldVar_holidayDesc1 != this.holidayDesc.getValue()) {
			changed = true;
		}
		logger.debug("Leaving ");
		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);
		
		if (!this.holidayType.isReadonly()){
			this.holidayType.setConstraint("NO EMPTY:" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
							"label_HolidayMasterDialog_HolidayType.value")}));
		}	
		if (!this.holidays.isReadonly()){
			this.holidays.setConstraint("NO EMPTY:" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
							"label_HolidayMasterDialog_Holidays.value")}));
		}	
		if (!this.holidayDesc.isReadonly()){
			this.holidayDesc.setConstraint("NO EMPTY:" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
							"label_HolidayMasterDialog_HolidayDesc.value")}));
		}	
		logger.debug("Leaving ");

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.holidayCode.setConstraint("");
		this.holidayYear.setConstraint("");
		this.holidayType.setConstraint("");
		this.holidays.setConstraint("");
		this.holidayDesc.setConstraint("");
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a HolidayMaster object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final HolidayDetail aHolidayDetail = new HolidayDetail();
		BeanUtils.copyProperties(getHolidayDetail(), aHolidayDetail);
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aHolidayDetail.getHolidayCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if(!this.holidayMasterDialogCtrl.syncHolidays(aHolidayDetail, PennantConstants.RCD_DEL)){

				throw new WrongValueException(this.holidays, Labels.getLabel(
						"Holiday_Not_Exists",DateUtility.formatUtilDate(
								aHolidayDetail.getHoliDayDate(), PennantConstants.dateFormat)));
			}
		}
			closeThis(this.window_HolidayMasterDetailsDialog, "HolidayMasterDetails");
		
		logger.debug("Leaving ");
	}

	/**
	 * Create a new HolidayMaster object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new HolidayMaster() in the frontend.
		// we get it from the backend.
		final HolidayDetail aHolidayDetail = new HolidayDetail();
		aHolidayDetail.setNewRecord(true);
		setHolidayDetail(aHolidayDetail);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.holidayCode.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		this.holidayCode.setReadonly(true);
		this.holidayYear.setDisabled(true);

		if (getHolidayDetail().isNewRecord()){
			this.holidayType.setDisabled(false);
			this.holidays.setDisabled(false);
			this.holidayType.setDisabled(true);
			this.holidays.focus();
		}else{
			this.holidayType.setDisabled(true);
			this.holidays.setDisabled(true);
			this.holidayDesc.focus();
		}
		btnCancel.setVisible(false);
		this.btnCtrl.setBtnStatus_Edit();
		logger.debug("Leaving ");
		
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.holidayCode.setReadonly(true);
		this.holidayYear.setReadonly(true);
		this.holidayType.setReadonly(true);
		this.holidays.setReadonly(true);
		this.holidayDesc.setReadonly(true);
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before
		
		this.holidayCode.setValue("");
		this.holidayYear.setValue("");
		this.holidayType.setValue("");
		this.holidays.setText("");
		this.holidayDesc.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final HolidayDetail aHolidayDetail = new HolidayDetail();
		BeanUtils.copyProperties(getHolidayDetail(), aHolidayDetail);
		//boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the HolidayMaster object with the components data
		doWriteComponentsToBean(aHolidayDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		//isNew = aHolidayDetail.isNew();
		//String tranType="";
		
		try {
			
			if(aHolidayDetail.isNewRecord()){
				if(!this.holidayMasterDialogCtrl.syncHolidays(aHolidayDetail, PennantConstants.RCD_ADD)){
					throw new WrongValueException(this.holidays, Labels.getLabel(
							"Holiday_Exists",DateUtility.formatUtilDate(
									aHolidayDetail.getHoliDayDate(), PennantConstants.dateFormat)));
				}		
			}else{
				if(!this.holidayMasterDialogCtrl.syncHolidays(aHolidayDetail, PennantConstants.RCD_UPD)){
					throw new WrongValueException(this.holidays, Labels.getLabel(
							"Holiday_Not_Exists",DateUtility.formatUtilDate(
									aHolidayDetail.getHoliDayDate(), PennantConstants.dateFormat)));
			}
			}
			
			closeThis(this.window_HolidayMasterDetailsDialog, "HolidayMasterDetails");	
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving ");
		
		
	}
	/**
	 * To show the Message
	 *
	 * @throws Exception
	 * 
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_HolidayMasterDetailsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
	}
	
	/**
	 * To Close Window
	 *
	 * @throws Exception
	 * 
	 */
	public void closeThis(Window dialogWindow, String dialogName){
		logger.debug("Entering ");
		getUserWorkspace().deAlocateAuthorities(dialogName);
		getUserWorkspace().deAlocateRoleAuthorities(dialogName);
		dialogWindow.onClose();
		logger.debug("Leaving ");
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

	public void setHolidayMasterService(HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}

	public HolidayMasterService getHolidayMasterService() {
		return this.holidayMasterService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public HolidayMasterDialogCtrl getHolidayMasterDialogCtrl() {
		return holidayMasterDialogCtrl;
	}

	public void setHolidayMasterDialogCtrl(
			HolidayMasterDialogCtrl holidayMasterDialogCtrl) {
		this.holidayMasterDialogCtrl = holidayMasterDialogCtrl;
	}

	public void setHolidayDetail(HolidayDetail holidayDetail) {
		this.holidayDetail = holidayDetail;
	}

	public HolidayDetail getHolidayDetail() {
		return holidayDetail;
	}

}