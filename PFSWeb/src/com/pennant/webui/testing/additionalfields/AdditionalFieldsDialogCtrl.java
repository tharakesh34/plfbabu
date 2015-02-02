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
 * FileName    		:  AdditionalFieldsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2011    														*
 *                                                                  						*
 * Modified Date    :  22-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.testing.additionalfields;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.AdditionalFieldDetails;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.testing.AdditionalFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.testing.AdditionalFieldsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Testing/AdditionalFields/AdditionalFieldsDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AdditionalFieldsDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AdditionalFieldsDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AdditionalFieldsDialog; // autowired
	protected Textbox code; // autowired
	protected Textbox description; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private AdditionalFields additionalFields; // overhanded per param
	private AdditionalFields prvAdditionalFields; // overhanded per param
	private transient AdditionalFieldsListCtrl additionalFieldsListCtrl; // overhanded
	                                                                     // per
	                                                                     // param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_code;
	private transient String oldVar_description;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AdditionalFieldsDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	


	// ServiceDAOs / Domain Classes
	private transient AdditionalFieldsService additionalFieldsService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private List<AdditionalFieldDetails> listadditionalFieldDetails = new ArrayList<AdditionalFieldDetails>();
	JdbcSearchObject<AdditionalFieldDetails> searchObject = new JdbcSearchObject<AdditionalFieldDetails>(
	        AdditionalFieldDetails.class);

	protected Rows addtionaldetails;

	/**
	 * default constructor.<br>
	 */
	public AdditionalFieldsDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected AdditionalFields
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AdditionalFieldsDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
		        this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("AdditionalFields")) {
			this.additionalFields = (AdditionalFields) args.get("AdditionalFields");
			AdditionalFields befImage = new AdditionalFields();
			BeanUtils.copyProperties(this.additionalFields, befImage);
			this.additionalFields.setBefImage(befImage);

			setAdditionalFields(this.additionalFields);
		} else {
			setAdditionalFields(null);
		}

		doLoadWorkFlow(this.additionalFields.isWorkflow(), this.additionalFields.getWorkflowId(),
		        this.additionalFields.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "AdditionalFieldsDialog");
		}

		// READ OVERHANDED params !
		// we get the additional FieldsListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete additional Fields here.
		if (args.containsKey("additionalFieldsListCtrl")) {
			setAdditionalFieldsListCtrl((AdditionalFieldsListCtrl) args.get("additionalFieldsListCtrl"));
		} else {
			setAdditionalFieldsListCtrl(null);
		}

		searchObject.addTabelName("AdditionalFieldDetails");
		searchObject.addSort("FieldSeqOrder", false);
		listadditionalFieldDetails = getAdditionalFieldsListCtrl().getPagedListWrapper().getPagedListService()
		        .getBySearchObject(searchObject);

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAdditionalFields());
	
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.code.setMaxlength(8);
		this.description.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
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

		getUserWorkspace().alocateAuthorities("AdditionalFieldsDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AdditionalFieldsDialog_btnSave"));
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
	public void onClose$window_AdditionalFieldsDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_AdditionalFieldsDialog);
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
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
			        MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_AdditionalFieldsDialog, "AdditionalFields");
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
	 * @param aAdditionalFields
	 *            Additional Fields
	 */
	public void doWriteBeanToComponents(AdditionalFields aAdditionalFields) {
		logger.debug("Entering");
		this.code.setValue(aAdditionalFields.getCode());
		this.description.setValue(aAdditionalFields.getDescription());
		doAdditionalFieldLoad(aAdditionalFields);
		this.recordStatus.setValue(aAdditionalFields.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAdditionalFields
	 */
	public void doWriteComponentsToBean(AdditionalFields aAdditionalFields) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAdditionalFields.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAdditionalFields.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aAdditionalFields.setLovDescAdditionalFields("Code",this.code.getValue());

		for (int i = 0; i < listadditionalFieldDetails.size(); i++) {
			AdditionalFieldDetails additionalFieldDetails = listadditionalFieldDetails.get(i);
		
			if (addtionaldetails.hasFellow(additionalFieldDetails.getFieldName())) {
				
				Component component=addtionaldetails.getFellow(additionalFieldDetails.getFieldName());
				
				if (component instanceof Textbox && additionalFieldDetails.getFieldFormat() == null) {
					Textbox textbox=(Textbox) component;
					aAdditionalFields.setLovDescAdditionalFields(additionalFieldDetails.getFieldName(),textbox.getValue());
				}else if (component instanceof Intbox) {
					Intbox intbox=(Intbox) component;
					aAdditionalFields.setLovDescAdditionalFields(additionalFieldDetails.getFieldName(),intbox.getValue());
				}else if (component instanceof Datebox) {
					Datebox datebox=(Datebox) component;
					aAdditionalFields.setLovDescAdditionalFields(additionalFieldDetails.getFieldName(),
							DateUtility.formatUtilDate(datebox.getValue(), PennantConstants.DBDateTimeFormat).toString());
				}else if (component instanceof Decimalbox ){
					Decimalbox decimalbox=(Decimalbox) component;
					aAdditionalFields.setLovDescAdditionalFields(additionalFieldDetails.getFieldName(),decimalbox.getValue());
				}
				else if (component instanceof Textbox ) {
					Combobox combobox=(Combobox) component;
					if (combobox.getSelectedItem()!=null) {
						aAdditionalFields.setLovDescAdditionalFields(additionalFieldDetails.getFieldName(), combobox.getSelectedItem().getValue());
					}
				}
			}		

		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aAdditionalFields.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAdditionalFields
	 * @throws InterruptedException
	 */
	public void doShowDialog(AdditionalFields aAdditionalFields) throws InterruptedException {
		logger.debug("Entering");

		// if aAdditionalFields == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aAdditionalFields == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aAdditionalFields = getAdditionalFieldsService().getNewAdditionalFields();

			setAdditionalFields(aAdditionalFields);
		} else {
			setAdditionalFields(aAdditionalFields);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aAdditionalFields.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {
			this.description.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAdditionalFields);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_AdditionalFieldsDialog);
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
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_code = this.code.getValue();
		this.oldVar_description = this.description.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.code.setValue(this.oldVar_code);
		this.description.setValue(this.oldVar_description);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
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
		// To clear the Error Messages
		doClearMessage();
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_AdditionalFieldsDialog_Code.value"),null,true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_AdditionalFieldsDialog_Description.value"),null,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.code.setConstraint("");
		this.description.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Additional Fields object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final AdditionalFields aAdditionalFields = new AdditionalFields();
		BeanUtils.copyProperties(getAdditionalFields(), aAdditionalFields);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		        + aAdditionalFields.getCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
		        Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAdditionalFields.getRecordType()).equals("")) {
				aAdditionalFields.setVersion(aAdditionalFields.getVersion() + 1);
				aAdditionalFields.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAdditionalFields.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAdditionalFields, tranType)) {
					refreshList();
					closeDialog(this.window_AdditionalFieldsDialog, "AdditionalFields");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Additional Fields object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final AdditionalFields aAdditionalFields = getAdditionalFieldsService().getNewAdditionalFields();
		aAdditionalFields.setNewRecord(true);
		setAdditionalFields(aAdditionalFields);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.code.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAdditionalFields().isNewRecord()) {
			this.code.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.code.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		// this.description.setReadonly(isReadOnly("AdditionalFieldsDialog_description"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.additionalFields.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
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
		this.code.setReadonly(true);
		// this.description.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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

		this.code.setValue("");
		this.description.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AdditionalFields aAdditionalFields = new AdditionalFields();
		BeanUtils.copyProperties(getAdditionalFields(), aAdditionalFields);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Additional Fields object with the components data
		doWriteComponentsToBean(aAdditionalFields);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aAdditionalFields.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAdditionalFields.getRecordType()).equals("")) {
				aAdditionalFields.setVersion(aAdditionalFields.getVersion() + 1);
				if (isNew) {
					aAdditionalFields.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAdditionalFields.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAdditionalFields.setNewRecord(true);
				}
			}
		} else {
			aAdditionalFields.setVersion(aAdditionalFields.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAdditionalFields, tranType)) {
				//doWriteBeanToComponents(aAdditionalFields);
				refreshList();
				closeDialog(this.window_AdditionalFieldsDialog, "AdditionalFields");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(AdditionalFields aAdditionalFields, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAdditionalFields.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAdditionalFields.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAdditionalFields.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAdditionalFields.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAdditionalFields.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aAdditionalFields);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aAdditionalFields))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aAdditionalFields.setTaskId(taskId);
			aAdditionalFields.setNextTaskId(nextTaskId);
			aAdditionalFields.setRoleCode(getRole());
			aAdditionalFields.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAdditionalFields, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aAdditionalFields);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAdditionalFields, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aAdditionalFields, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		AdditionalFields aAdditionalFields = (AdditionalFields) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAdditionalFieldsService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAdditionalFieldsService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getAdditionalFieldsService().doApprove(auditHeader);

						if (aAdditionalFields.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAdditionalFieldsService().doReject(auditHeader);
						if (aAdditionalFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
						        .getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AdditionalFieldsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_AdditionalFieldsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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

	public AdditionalFields getAdditionalFields() {
		return this.additionalFields;
	}

	public void setAdditionalFields(AdditionalFields additionalFields) {
		this.additionalFields = additionalFields;
	}

	public void setAdditionalFieldsService(AdditionalFieldsService additionalFieldsService) {
		this.additionalFieldsService = additionalFieldsService;
	}

	public AdditionalFieldsService getAdditionalFieldsService() {
		return this.additionalFieldsService;
	}

	public void setAdditionalFieldsListCtrl(AdditionalFieldsListCtrl additionalFieldsListCtrl) {
		this.additionalFieldsListCtrl = additionalFieldsListCtrl;
	}

	public AdditionalFieldsListCtrl getAdditionalFieldsListCtrl() {
		return this.additionalFieldsListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(AdditionalFields aAdditionalFields, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAdditionalFields.getBefImage(), aAdditionalFields);
		return new AuditHeader(aAdditionalFields.getCode(), null, null, null, auditDetail,
		        aAdditionalFields.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_AdditionalFieldsDialog, auditHeader);
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

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("AdditionalFields");
		notes.setReference(getAdditionalFields().getCode());
		notes.setVersion(getAdditionalFields().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		final JdbcSearchObject<AdditionalFields> soAdditionalFields = getAdditionalFieldsListCtrl().getSearchObj();
		getAdditionalFieldsListCtrl().pagingAdditionalFieldsList.setActivePage(0);
		getAdditionalFieldsListCtrl().getPagedListWrapper().setSearchObject(soAdditionalFields);
		if (getAdditionalFieldsListCtrl().listBoxAdditionalFields != null) {
			getAdditionalFieldsListCtrl().listBoxAdditionalFields.getListModel();
		}
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public AdditionalFields getPrvAdditionalFields() {
		return prvAdditionalFields;
	}
	



	// Adding Additional Fields
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void doAdditionalFieldLoad(AdditionalFields aAdditionalFields) {
		if (listadditionalFieldDetails != null && listadditionalFieldDetails.size() != 0) {
			for (int i = 0; i < listadditionalFieldDetails.size(); i++) {
				AdditionalFieldDetails details = listadditionalFieldDetails.get(i);

				Row row = new Row();
				Combobox combobox = new Combobox();
				Comboitem comboitem = new Comboitem();

				if (details.getFieldType().trim().equals("string") && details.getFieldFormat() == null) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					Textbox textbox = new Textbox();
					textbox.setId(details.getFieldName());	
					if (aAdditionalFields.getLovDescAdditionalFields().containsKey(details.getFieldName())) {
						textbox.setValue(aAdditionalFields.getLovDescAdditionalFields().get(details.getFieldName()).toString());   
                    }
					textbox.setMaxlength(details.getFieldLength());
					textbox.setConstraint(details.getFieldConstraint());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(textbox);
					row.appendChild(hbox);
				} else if (details.getFieldType().trim().equals("date")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					Datebox datebox = new Datebox();
					datebox.setFormat(PennantConstants.dateFormat);
					datebox.setId(details.getFieldName());
					
					if (aAdditionalFields.getLovDescAdditionalFields().containsKey(details.getFieldName())) {
						
						Date date = DateUtility.getUtilDate(
						        aAdditionalFields.getLovDescAdditionalFields().get(details.getFieldName()).toString(),
						        PennantConstants.DBDateFormat);
						datebox.setValue(date);   
                    }
					
					datebox.setFormat(details.getFieldFormat());
					datebox.setConstraint(details.getFieldConstraint());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(datebox);
					row.appendChild(hbox);
				} else if (details.getFieldType().trim().equals("number")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					Intbox intbox = new Intbox();
					intbox.setId(details.getFieldName());
					if (aAdditionalFields.getLovDescAdditionalFields().containsKey(details.getFieldName())) {					
						intbox.setValue(Integer.parseInt(aAdditionalFields.getLovDescAdditionalFields().get(details.getFieldName()).toString()));   
                    }
					intbox.setMaxlength(details.getFieldLength());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(intbox);
					row.appendChild(hbox);
				} else if (details.getFieldType().trim().equals("decimal")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					Decimalbox decimalbox = new Decimalbox();
					decimalbox.setId(details.getFieldName());
					decimalbox.setFormat(details.getFieldFormat());
					decimalbox.setMaxlength(details.getFieldLength());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(decimalbox);
					row.appendChild(hbox);
				} else if (details.getFieldType().trim().equals("string")
				        && details.getFieldFormat().trim().endsWith("S_LIST")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					combobox.setId(details.getFieldName());
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					combobox.appendChild(comboitem);
					combobox.setSelectedItem(comboitem);
					if (details.getFieldList().contains(",")) {
						String[] temp = details.getFieldList().split(",");
						for (int j = 0; j < temp.length; j++) {
							comboitem = new Comboitem();
							
							comboitem.setValue(temp[j]);
							comboitem.setLabel(temp[j]);
							combobox.appendChild(comboitem);
							if (aAdditionalFields.getLovDescAdditionalFields().containsKey(details.getFieldName())) {					
								if (temp[j].equals(aAdditionalFields.getLovDescAdditionalFields().get(details.getFieldName()).toString())) {
									combobox.setSelectedItem(comboitem);
                                }								
								
		                    }
							
						}
						Hbox hbox = new Hbox();
						hbox.appendChild(space);
						hbox.appendChild(combobox);
						row.appendChild(hbox);
					}
				} else if (details.getFieldType().trim().equals("string")
				        && details.getFieldFormat().trim().endsWith("D_LIST")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					if (details.isFieldIsMandatory()) {
						space.setSclass("mandatory");
					} else {
						space.setWidth("0px");
					}
					combobox.setId(details.getFieldName());
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					combobox.appendChild(comboitem);
					combobox.setSelectedItem(comboitem);
					JdbcSearchObject searchObject = new JdbcSearchObject(PennantJavaUtil.getClassname(details
					        .getFieldList()));
					List list = getAdditionalFieldsListCtrl().getPagedBindingListWrapper().getPagedListService()
					        .getBySearchObject(searchObject);
					for (int j = 0; j < list.size(); j++) {
						if (list.get(j) instanceof Country) {
							Country country = (Country) list.get(j);
							comboitem = new Comboitem();
							comboitem.setValue(country.getCountryCode());
							comboitem.setLabel(country.getCountryDesc());
							combobox.appendChild(comboitem);
							if (aAdditionalFields.getLovDescAdditionalFields().containsKey(details.getFieldName())) {					
								if (country.getCountryCode().equals(aAdditionalFields.getLovDescAdditionalFields().get(details.getFieldName()).toString())) {
									combobox.setSelectedItem(comboitem);
                                }								
								
		                    }
						

						}
					}
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(combobox);
					row.appendChild(hbox);

				}
				this.addtionaldetails.appendChild(row);
			}
		}
	}



}
