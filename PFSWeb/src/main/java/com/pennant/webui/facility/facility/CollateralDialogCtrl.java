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
 * FileName    		:  CollateralDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.facility.facility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/Collateral/collateralDialog.zul file.
 */
public class CollateralDialogCtrl extends GFCBaseCtrl<Collateral> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CollateralDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CollateralDialog; 
	protected Textbox cAFReference; 
	protected Textbox reference; 
	protected Textbox lastReview; 
	protected ExtendedCombobox currency; 
	protected CurrencyBox value; 
	protected CurrencyBox bankvaluation; 
	protected Decimalbox bankmargin; 
	protected Decimalbox actualCoverage; 
	protected Decimalbox proposedCoverage; 
	protected Textbox description; 
	// not auto wired vars
	private Collateral collateral; // overhanded per param
	private Collateral prvCollateral; // overhanded per param
	private transient FacilityDialogCtrl facilityDialogCtrl; // overhanded per
																// param
	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<Collateral> collateralsList;

	private int ccyFormat = 2; //Need to set from bean
	private String userRole="";
	/**
	 * default constructor.<br>
	 */
	public CollateralDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Collateral object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralDialog);

		try {
			if (arguments.containsKey("collateral")) {
				this.collateral = (Collateral) arguments.get("collateral");
				Collateral befImage = new Collateral();
				BeanUtils.copyProperties(this.collateral, befImage);
				this.collateral.setBefImage(befImage);
				setCollateral(this.collateral);
				ccyFormat=getCollateral().getCcyFormat();
			} else {
				setCollateral(null);
			}
			this.collateral.setWorkflowId(0);
			doLoadWorkFlow(this.collateral.isWorkflow(), this.collateral.getWorkflowId(), this.collateral.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),super.pageRightName);
			}
			if (arguments.containsKey("role")) {
				userRole=arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, super.pageRightName);
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the collateralListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete collateral here.
			if (arguments.containsKey("facilityDialogCtrl")) {
				setFacilityDialogCtrl((FacilityDialogCtrl) arguments.get("facilityDialogCtrl"));
			} else {
				setFacilityDialogCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCollateral());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_CollateralDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cAFReference.setMaxlength(50);
		this.reference.setMaxlength(50);
		this.lastReview.setMaxlength(50);
		this.currency.setMaxlength(3);
        this.currency.setMandatoryStyle(true);
		this.currency.setModuleName("Currency");
		this.currency.setValueColumn("CcyCode");
		this.currency.setDescColumn("CcyDesc");
		this.currency.setValidateColumns(new String[] { "CcyCode" });
		this.value.setMandatory(false);
		this.value.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.value.setScale(ccyFormat);
		this.bankvaluation.setMandatory(false);
		this.bankvaluation.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.bankvaluation.setScale(ccyFormat);
		
		this.bankmargin.setMaxlength(6);
		this.bankmargin.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.bankmargin.setScale(2);
		this.actualCoverage.setMaxlength(6);
		this.actualCoverage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.actualCoverage.setScale(2);
		this.proposedCoverage.setMaxlength(6);
		this.proposedCoverage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.proposedCoverage.setScale(2);
		this.description.setMaxlength(2000);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
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
		getUserWorkspace().allocateAuthorities("CollateralDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_CollateralDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$currency(Event event) {
		logger.debug("Entering " + event.toString());

		this.currency.setConstraint("");
		Object dataObject = currency.getObject();

		if (dataObject instanceof String) {
			//
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				ccyFormat = details.getCcyEditField();
				doSetFieldProperties();
			}
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.collateral.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCollateral
	 *            Collateral
	 */
	public void doWriteBeanToComponents(Collateral aCollateral) {
		logger.debug("Entering");
		this.cAFReference.setValue(aCollateral.getCAFReference());
		this.reference.setValue(aCollateral.getReference());
		this.lastReview.setValue(aCollateral.getLastReview());
		this.currency.setValue(aCollateral.getCurrency());
		this.value.setValue(PennantAppUtil.formateAmount(aCollateral.getValue(), ccyFormat));
		this.bankvaluation.setValue(PennantAppUtil.formateAmount(aCollateral.getBankvaluation(), ccyFormat));
		
		this.bankmargin.setValue(aCollateral.getBankmargin());
		this.actualCoverage.setValue(aCollateral.getActualCoverage());
		this.proposedCoverage.setValue(aCollateral.getProposedCoverage());
		this.description.setValue(aCollateral.getDescription());
		if (aCollateral.isNewRecord()) {
			this.currency.setDescription("");
		} else {
			this.currency.setDescription(aCollateral.getLovDescCurrencyName());
		}
		this.recordStatus.setValue(aCollateral.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCollateral
	 */
	public void doWriteComponentsToBean(Collateral aCollateral) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCollateral.setCAFReference(this.cAFReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setReference(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setLastReview(this.lastReview.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setLovDescCurrencyName(this.currency.getDescription());
			aCollateral.setCurrency(this.currency.getValidatedValue());
			aCollateral.setCcyFormat(ccyFormat);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setValue(PennantApplicationUtil.unFormateAmount(this.value.getValidateValue(), ccyFormat));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setBankvaluation(PennantApplicationUtil.unFormateAmount(this.bankvaluation.getValidateValue(), ccyFormat));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.bankmargin.getValue() != null) {
				aCollateral.setBankmargin(this.bankmargin.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.actualCoverage.getValue() != null) {
				aCollateral.setActualCoverage(this.actualCoverage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.proposedCoverage.getValue() != null) {
				aCollateral.setProposedCoverage(this.proposedCoverage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
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
		aCollateral.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCollateral
	 * @throws InterruptedException
	 */
	public void doShowDialog(Collateral aCollateral) throws InterruptedException {
		logger.debug("Entering");
		// set Readonly mode accordingly if the object is new or not.
		if (aCollateral.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cAFReference.focus();
		} else {
			this.currency.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCollateral);
			
			this.window_CollateralDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.reference.isReadonly()) {
			this.reference.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralDialog_Reference.value"),null,true));
		}
		if (!this.lastReview.isReadonly()) {
			this.lastReview.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralDialog_LastReview.value"),null,true));
		}
		if (!this.value.isReadonly()) {
			this.value.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Value.value"), ccyFormat, false, false));
		}
		if (!this.bankvaluation.isReadonly()) {
			this.bankvaluation.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Bankvaluation.value"), ccyFormat, false, false));
		}
		if (!this.bankmargin.isReadonly()) {
			this.bankmargin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Bankmargin.value"),
					2, false, false, 0, 100));
		}
		if (!this.actualCoverage.isReadonly()) {
			this.actualCoverage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_ActualCoverage.value"),
				2, false, false, 0, 100));
		}
		if (!this.proposedCoverage.isReadonly()) {
			this.proposedCoverage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_ProposedCoverage.value"),
					2, false, false, 0, 100));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cAFReference.setConstraint("");
		this.reference.setConstraint("");
		this.lastReview.setConstraint("");
		this.value.setConstraint("");
		this.bankvaluation.setConstraint("");
		this.bankmargin.setConstraint("");
		this.actualCoverage.setConstraint("");
		this.proposedCoverage.setConstraint("");
		this.description.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations
	
	/**
	 * Deletes a Collateral object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Collateral aCollateral = new Collateral();
		BeanUtils.copyProperties(getCollateral(), aCollateral);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCollateral.getCAFReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCollateral.getRecordType())) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aCollateral.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aCollateral.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFeeProcess(aCollateral, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CollateralDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFacilityDialogCtrl().doFillCollaterals(this.collateralsList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getCollateral().isNewRecord()) {
			this.reference.setReadonly(false);
			this.cAFReference.setReadonly(false);
		} else {
			this.reference.setReadonly(true);
			this.cAFReference.setReadonly(true);
		}
		//readOnlyComponent(isReadOnly("CollateralDialog_reference"), this.reference);
		readOnlyComponent(isReadOnly("CollateralDialog_lastReview"), this.lastReview);
		readOnlyComponent(isReadOnly("CollateralDialog_currency"), this.currency);
		readOnlyComponent(isReadOnly("CollateralDialog_value"), this.value);	
		readOnlyComponent(isReadOnly("CollateralDialog_bankvaluation"), this.bankvaluation);
		readOnlyComponent(isReadOnly("CollateralDialog_bankmargin"), this.bankmargin);
		readOnlyComponent(isReadOnly("CollateralDialog_actualCoverage"), this.actualCoverage);
		readOnlyComponent(isReadOnly("CollateralDialog_proposedCoverage"), this.proposedCoverage);
		readOnlyComponent(isReadOnly("CollateralDialog_description"), this.description);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.collateral.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cAFReference.setReadonly(true);
		this.reference.setReadonly(true);
		this.lastReview.setReadonly(true);
		this.currency.setReadonly(true);
		this.value.setReadonly(true);
		this.bankvaluation.setReadonly(true);
		this.bankmargin.setReadonly(true);
		this.actualCoverage.setReadonly(true);
		this.proposedCoverage.setReadonly(true);
		this.description.setReadonly(true);
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
		this.cAFReference.setValue("");
		this.reference.setValue("");
		this.lastReview.setValue("");
		this.currency.setValue("");
		this.currency.setDescription("");
		this.value.setValue("");
		this.bankvaluation.setValue("");
		this.bankmargin.setValue("");
		this.actualCoverage.setValue("");
		this.proposedCoverage.setValue("");
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
		final Collateral aCollateral = new Collateral();
		BeanUtils.copyProperties(getCollateral(), aCollateral);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Collateral object with the components data
		doWriteComponentsToBean(aCollateral);
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aCollateral.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCollateral.getRecordType())) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				if (isNew) {
					aCollateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCollateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateral.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aCollateral.setVersion(1);
				aCollateral.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aCollateral.getRecordType())) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aCollateral.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newFeeProcess(aCollateral, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFacilityDialogCtrl().doFillCollaterals(this.collateralsList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFeeProcess(Collateral aCollateral, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aCollateral, tranType);
		collateralsList = new ArrayList<Collateral>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aCollateral.getCAFReference();
		valueParm[1] = aCollateral.getReference();
		errParm[0] = PennantJavaUtil.getLabel("label_CAFReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[1];
		List<Collateral> list = getFacilityDialogCtrl().getCollateralsList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Collateral collateral = list.get(i);
				if (collateral.getReference().equals(aCollateral.getReference())) {
					// Both Current and Existing list rating same
					if (aCollateral.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aCollateral.getRecordType())) {
							aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							collateralsList.add(aCollateral);
						} else if (PennantConstants.RCD_ADD.equals(aCollateral.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aCollateral.getRecordType())) {
							aCollateral.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							collateralsList.add(aCollateral);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aCollateral.getRecordType())) {
							recordAdded = true;
							List<Collateral> savedList = getFacilityDialogCtrl().getFacility().getCollaterals();
							for (int j = 0; j < savedList.size(); j++) {
								Collateral fee = savedList.get(j);
								if (fee.getReference().equals(aCollateral.getReference())) {
									collateralsList.add(fee);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateral.getRecordType())) {
							aCollateral.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							collateralsList.add(collateral);
						}
					}
				} else {
					collateralsList.add(collateral);
				}
			}
		}
		if (!recordAdded) {
			collateralsList.add(aCollateral);
		}
		logger.debug("Leaving");
		return auditHeader;
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

	public Collateral getCollateral() {
		return this.collateral;
	}

	public void setCollateral(Collateral collateral) {
		this.collateral = collateral;
	}

	public void setFacilityDialogCtrl(FacilityDialogCtrl facilityDialogCtrl) {
		this.facilityDialogCtrl = facilityDialogCtrl;
	}

	public FacilityDialogCtrl getFacilityDialogCtrl() {
		return this.facilityDialogCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(Collateral aCollateral, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCollateral.getBefImage(), aCollateral);
		return new AuditHeader(aCollateral.getCAFReference(), null, null, null, auditDetail, aCollateral.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CollateralDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.collateral);
	}

	private void doSetLOVValidation() {
		if(this.currency.isButtonVisible()){
			this.currency.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralDialog_Currency.value"),null,true,true));
		}
	}
	private void doRemoveLOVValidation() {
		this.currency.setConstraint("");
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.collateral.getCAFReference());
	}


	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.cAFReference.setErrorMessage("");
		this.reference.setErrorMessage("");
		this.lastReview.setErrorMessage("");
		this.currency.setErrorMessage("");
		this.value.setErrorMessage("");
		this.bankvaluation.setErrorMessage("");
		this.bankmargin.setErrorMessage("");
		this.actualCoverage.setErrorMessage("");
		this.proposedCoverage.setErrorMessage("");
		this.description.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Collateral getPrvCollateral() {
		return prvCollateral;
	}
}
