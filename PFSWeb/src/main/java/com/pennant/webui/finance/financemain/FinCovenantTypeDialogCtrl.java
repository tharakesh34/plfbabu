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
 * FileName    		:  FinCovenantTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FinCovenantTypeDetail/finCovenantTypesDetailDialog.zul
 * file.
 */
public class FinCovenantTypeDialogCtrl extends GFCBaseCtrl<FinCovenantType> {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinCovenantTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCovenantTypeDialog;

	protected ExtendedCombobox covenantType;
	protected ExtendedCombobox mandRole;
	protected Textbox description;
	protected Checkbox alwWaiver;
	protected Checkbox alwPostpone;
	protected Row  row_Postpone;

	protected Label label_postponeDays;
	protected Hbox hbox_postponeDays;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;

	// not auto wired vars
	private FinCovenantType finCovenantTypes; // overhanded per param

	
	private transient boolean newFinance;

	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;

	private Object financeMainDialogCtrl;
	private FinCovenantTypeListCtrl finCovenantTypesListCtrl;
	private boolean newRecord = false;
	private boolean newCustomer = false;

	@SuppressWarnings("unused")
	private String moduleType = "";

	private List<FinCovenantType> finCovenantTypesDetails;
	private String allowedRoles;
	
	protected Label label_FinCovenantTypeDialog_MandRole;
	protected Datebox	receivableDate;
	protected Space space_receivableDate;
	private FinanceDetail financedetail;
	
	protected  Label label_FinCovenantTypeDialog_RecvbleDate;

	/**
	 * default constructor.<br>
	 */
	public FinCovenantTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinCovenantTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinCovenantTypeDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinCovenantTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCovenantTypeDialog);

		try {
		// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("finCovenantTypes")) {
				this.finCovenantTypes = (FinCovenantType) arguments.get("finCovenantTypes");
				FinCovenantType befImage = new FinCovenantType();
				BeanUtils.copyProperties(this.finCovenantTypes, befImage);
				this.finCovenantTypes.setBefImage(befImage);

				setFinCovenantType(this.finCovenantTypes);
			} else {
				setFinCovenantType(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getFinCovenantType().isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("finCovenantTypesListCtrl")) {
				setFinCovenantTypeListCtrl((FinCovenantTypeListCtrl) arguments.get("finCovenantTypesListCtrl"));
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {

				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finCovenantTypes.setWorkflowId(0);
			}
			if (arguments.containsKey("allowedRoles")) {
				allowedRoles=(String) arguments.get("allowedRoles");
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinCovenantTypeDialog");
			}
			
			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
			}
			doLoadWorkFlow(this.finCovenantTypes.isWorkflow(), this.finCovenantTypes.getWorkflowId(),
					this.finCovenantTypes.getNextTaskId());

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinCovenantTypeDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinCovenantType());
		} catch (Exception e) {
			createException(window_FinCovenantTypeDialog, e);
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FinCovenantTypeDialog);
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
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.finCovenantTypes);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finCovenantTypes.getFinReference());
	}


	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinCovenantTypeDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinCovenantType aFinCovenantType) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinCovenantType.isNew()) {
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(false);
			this.receivableDate.setVisible(false);
			this.space_receivableDate.setSclass("");
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.covenantType.focus();
		} else {
			this.covenantType.focus();
			if (isNewFinance()) {
				if (enqModule) {
					doReadOnly();
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aFinCovenantType);

			this.window_FinCovenantTypeDialog.setHeight("45%");
			this.window_FinCovenantTypeDialog.setWidth("75%");
			this.gb_statusDetails.setVisible(false);
			this.window_FinCovenantTypeDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getFinCovenantType().isNewRecord()) {
			this.covenantType.setReadonly(isReadOnly("FinCovenantTypeDialog_covenantType"));
			this.space_receivableDate.setSclass("");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(false);
			this.receivableDate.setVisible(false);
		} else {
			this.covenantType.setReadonly(true);
		}
		this.mandRole.setReadonly(isReadOnly("FinCovenantTypeDialog_mandRole"));
		this.description.setReadonly(isReadOnly("FinCovenantTypeDialog_description"));
		this.alwWaiver.setDisabled(isReadOnly("FinCovenantTypeDialog_alwWaiver"));
		this.alwPostpone.setDisabled(isReadOnly("FinCovenantTypeDialog_alwPostpone"));
		this.receivableDate.setDisabled(isReadOnly("FinCovenantTypeDialog_receivableDate"));
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.mandRole.setReadonly(true);
		this.covenantType.setReadonly(true);
		this.description.setReadonly(true);
		this.alwWaiver.setDisabled(true);
		this.alwPostpone.setDisabled(true);
		this.receivableDate.setDisabled(true);

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

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("FinCovenantTypeDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.covenantType.setMaxlength(50);
		this.covenantType.setTextBoxWidth(151);
		this.covenantType.setMandatoryStyle(true);
		this.covenantType.setModuleName("DocumentType");
		this.covenantType.setValueColumn("DocTypeCode");
		this.covenantType.setDescColumn("DocTypeDesc");
		this.covenantType.setValidateColumns(new String[] { "DocTypeCode" });

		this.mandRole.setMaxlength(100);
		this.mandRole.setTextBoxWidth(151);
		this.mandRole.setMandatoryStyle(true);
		this.mandRole.setModuleName("SecurityRoleEnq");
		this.mandRole.setValueColumn("RoleCd");
		this.mandRole.setDescColumn("RoleDesc");
		this.mandRole.setValidateColumns(new String[] { "RoleCd" });
		
		if(!"".equals(StringUtils.trimToEmpty(allowedRoles))){
			String[] roles=allowedRoles.split(";");
			Filter[] fintypeRoles = new Filter[1];
			fintypeRoles[0] = Filter.in("RoleCd", Arrays.asList(roles));
			this.mandRole.setFilters(fintypeRoles);
		}
		
		this.description.setMaxlength(500);
		this.description.setWidth("191px");

		this.receivableDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
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
		doWriteBeanToComponents(this.finCovenantTypes.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinCovenantType
	 *            FinCovenantTypeDetail
	 */
	public void doWriteBeanToComponents(FinCovenantType aFinAdvnancePayments) {
		logger.debug("Entering");

		this.covenantType.setValue(aFinAdvnancePayments.getCovenantType());
		this.covenantType.setDescription(aFinAdvnancePayments.getCovenantTypeDesc());
		this.mandRole.setValue(aFinAdvnancePayments.getMandRole());
		this.mandRole.setDescription(aFinAdvnancePayments.getMandRoleDesc());
		this.description.setValue(aFinAdvnancePayments.getDescription());
		this.alwWaiver.setChecked(aFinAdvnancePayments.isAlwWaiver());
		this.alwPostpone.setChecked(aFinAdvnancePayments.isAlwPostpone());
		if (aFinAdvnancePayments.getReceivableDate() != null) {
			this.receivableDate.setValue(aFinAdvnancePayments.getReceivableDate());
		}

		this.recordStatus.setValue(aFinAdvnancePayments.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aFinAdvnancePayments.getRecordType()));

		doSetWaiverProp();
		
		doSetPostponeProp();

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinCovenantType
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinCovenantType aFinCovenantType) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinCovenantType.setCovenantTypeDesc(this.covenantType.getDescription());
			aFinCovenantType.setCovenantType(this.covenantType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.mandRole.isVisible()){
				aFinCovenantType.setMandRole(this.mandRole.getValidatedValue());
				aFinCovenantType.setMandRoleDesc(this.mandRole.getDescription());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setAlwWaiver(this.alwWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinCovenantType.setAlwPostpone(this.alwPostpone.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinCovenantType.setReceivableDate(this.receivableDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinCovenantType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	
	

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCovenantTypeDialog_Description.value"), null, false));
		}
		
		if (this.alwPostpone.isChecked() && !this.receivableDate.isDisabled()){
			this.receivableDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinCovenantTypeDialog_RecvbleDate.value"),true));
		}
		
		if(this.receivableDate.getValue()!=null){
		if (DateUtility.compare(this.receivableDate.getValue(), DateUtility.getAppDate()) == -1) {
			throw new WrongValueException(this.receivableDate,Labels.getLabel("DATE_PAST",
					new String[] {Labels.getLabel("label_FinCovenantTypeDialog_RecvbleDate.value") }));
		}
	}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.covenantType.setConstraint("");
		this.description.setConstraint("");
		this.mandRole.setConstraint("");
		this.receivableDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (!this.covenantType.isReadonly()) {
			this.covenantType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCovenantTypeDialog_CovenantType.value"), null, true, true));
		}
		if (!this.mandRole.isReadonly()) {
			this.mandRole.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinCovenantTypeDialog_MandRole.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {

	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.covenantType.setErrorMessage("");
		this.description.setErrorMessage("");
		this.mandRole.setErrorMessage("");
		this.receivableDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a FinCovenantTypeDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinCovenantType aFinCovenantType = new FinCovenantType();
		BeanUtils.copyProperties(getFinCovenantType(), aFinCovenantType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n"
				+ Labels.getLabel("FinCovenantType_CovenantType") + " : "
				+ aFinCovenantType.getCovenantType(); 

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFinCovenantType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinCovenantType.getNextTaskId(),
							aFinCovenantType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (isNewCustomer()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newFinCovenantTypeProcess(aFinCovenantType, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinCovenantTypeDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFinCovenantTypeListCtrl().doFillFinCovenantTypeDetails(this.finCovenantTypesDetails);
						closeDialog();
					}
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showErrorMessage(this.window_FinCovenantTypeDialog, e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.covenantType.setValue("");
		this.description.setValue("");
		this.mandRole.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinCovenantType aFinCovenantType = new FinCovenantType();
		BeanUtils.copyProperties(getFinCovenantType(), aFinCovenantType);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aFinCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinCovenantType.getNextTaskId(),
					aFinCovenantType);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFinCovenantType.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the FinCovenantTypeDetail object with the components data
			doWriteComponentsToBean(aFinCovenantType);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFinCovenantType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				if (isNew) {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinCovenantType.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aFinCovenantType.setVersion(1);
					aFinCovenantType.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinCovenantType.getRecordType())) {
					aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
					aFinCovenantType.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinCovenantType.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFinCovenantType.setVersion(aFinCovenantType.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinCovenantTypeProcess(aFinCovenantType, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinCovenantTypeDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinCovenantTypeListCtrl().doFillFinCovenantTypeDetails(this.finCovenantTypesDetails);
					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);

			showErrorMessage(this.window_FinCovenantTypeDialog, e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinCovenantTypeProcess(FinCovenantType afinCovenantTypes, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(afinCovenantTypes, tranType);
		finCovenantTypesDetails = new ArrayList<FinCovenantType>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(afinCovenantTypes.getFinReference());
		valueParm[1] = afinCovenantTypes.getCovenantType();

		errParm[0] = PennantJavaUtil.getLabel("FinCovenantType_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("FinCovenantType_CovenantType") + ":" + valueParm[1];

		if (getFinCovenantTypeListCtrl().getFinCovenantTypeDetailList() != null
				&& getFinCovenantTypeListCtrl().getFinCovenantTypeDetailList().size() > 0) {
			for (int i = 0; i < getFinCovenantTypeListCtrl().getFinCovenantTypeDetailList().size(); i++) {
				FinCovenantType loanDetail = getFinCovenantTypeListCtrl().getFinCovenantTypeDetailList().get(i);

				if (StringUtils.equals(afinCovenantTypes.getCovenantType(), loanDetail.getCovenantType())) { // Both
																												// Current
																												// and
																												// Existing
																												// list
																												// rating
																												// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
						if (afinCovenantTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							afinCovenantTypes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finCovenantTypesDetails.add(afinCovenantTypes);
						} else if (afinCovenantTypes.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (afinCovenantTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							afinCovenantTypes.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finCovenantTypesDetails.add(afinCovenantTypes);
						} else if (afinCovenantTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getFinCovenantTypeListCtrl().getFinancedetail().getCovenantTypeList()
									.size(); j++) {
								FinCovenantType detail = getFinCovenantTypeListCtrl().getFinancedetail()
										.getCovenantTypeList().get(j);
								if (detail.getFinReference() == afinCovenantTypes.getFinReference()
										&& StringUtils.equals(detail.getCovenantType(),
												afinCovenantTypes.getCovenantType())) {
									finCovenantTypesDetails.add(detail);
								}
							}
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finCovenantTypesDetails.add(loanDetail);
						}
					}
				} else {
					finCovenantTypesDetails.add(loanDetail);
				}
			}
		}

		if (!recordAdded) {
			finCovenantTypesDetails.add(afinCovenantTypes);
		}
		return auditHeader;
	}

	public void onCheck$alwWaiver(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSetWaiverProp();
		
		this.alwPostpone.setChecked(false);
		this.receivableDate.setValue(null);
		this.space_receivableDate.setSclass("");
		logger.debug("Leaving" + event.toString());
	}

	private void doSetWaiverProp() {
		if (this.alwWaiver.isChecked()) {
			this.alwPostpone.setChecked(false);
			this.row_Postpone.setVisible(false);
			this.mandRole.setVisible(false);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
		} else{
			this.mandRole.setVisible(true);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(true);
			this.row_Postpone.setVisible(true);
		}
	}

	public void onCheck$alwPostpone(Event event) throws Exception {
		doSetPostponeProp();
	}
	
	private void doSetPostponeProp() {
		if (this.alwPostpone.isChecked()) {
			this.mandRole.setVisible(false);
			this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
			this.mandRole.setValue("", "");
			this.space_receivableDate.setSclass("mandatory");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(true);
			this.receivableDate.setVisible(true);
			
		} else {
			if(this.alwWaiver.isChecked()){
				this.mandRole.setVisible(false);
				this.label_FinCovenantTypeDialog_MandRole.setVisible(false);
			}else{				
				this.mandRole.setVisible(true);
				this.label_FinCovenantTypeDialog_MandRole.setVisible(true);
			}
			this.space_receivableDate.setSclass("");
			this.receivableDate.setErrorMessage("");
			this.receivableDate.setConstraint("");
			this.label_FinCovenantTypeDialog_RecvbleDate.setVisible(false);
			this.receivableDate.setVisible(false);
			this.space_receivableDate.setSclass("");
		}
	}
	

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinCovenantType aFinCovenantType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinCovenantType.getBefImage(), aFinCovenantType);
		return new AuditHeader(aFinCovenantType.getFinReference(), null, null, null, auditDetail,
				aFinCovenantType.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinCovenantType getFinCovenantType() {
		return this.finCovenantTypes;
	}

	public void setFinCovenantType(FinCovenantType finCovenantTypes) {
		this.finCovenantTypes = finCovenantTypes;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public FinCovenantTypeListCtrl getFinCovenantTypeListCtrl() {
		return finCovenantTypesListCtrl;
	}

	public void setFinCovenantTypeListCtrl(FinCovenantTypeListCtrl finCovenantTypesListCtrl) {
		this.finCovenantTypesListCtrl = finCovenantTypesListCtrl;
	}

	public void setFinCovenantTypeDetails(List<FinCovenantType> finCovenantTypesDetails) {
		this.finCovenantTypesDetails = finCovenantTypesDetails;
	}

	public List<FinCovenantType> getFinCovenantTypeDetails() {
		return finCovenantTypesDetails;
	}
	
	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

}
