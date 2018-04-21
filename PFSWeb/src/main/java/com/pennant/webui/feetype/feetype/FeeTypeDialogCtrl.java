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
 * FileName    		:  FeeTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.feetype.feetype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/FeeType/FeeType/feeTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FeeTypeDialogCtrl extends GFCBaseCtrl<FeeType> {

	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(FeeTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window					window_FeeTypeDialog;

	protected Row						row0;
	protected Label						label_FeeTypeCode;
	protected Hbox						hlayout_FeeTypeCode;
	protected Space						space_FeeTypeCode;
	protected Uppercasebox				feeTypeCode;

	protected Label						label_FeeTypeDesc;
	protected Hbox						hlayout_FeeTypeDesc;
	protected Space						space_FeeTypeDesc;
	protected Textbox					feeTypeDesc;

	protected Row						row1;
	protected Label						label_ManualAdvice;
	protected Hbox						hlayout_ManualAdvice;
	protected Space						space_ManualAdvice;
	protected Checkbox					manualAdvice;
	
	protected Label						label_AdviseType;
	protected Hbox						hlayout_AdviseType;
	protected Space						space_AdviseType;
	protected Combobox 					adviseType;

	protected Label						label_AccountingSetID;
	protected Hbox						hlayout_AccountingSetID;
	protected ExtendedCombobox			accountingSetID;
	
	protected Row						row2;
	protected Label						label_Active;
	protected Hbox						hlayout_Active;
	protected Space						space_Active;

	protected Checkbox					active;
	
	protected Checkbox					taxApplicable;
	protected Label						label_TaxComponent;
	protected Hbox						hlayout_TaxComponent;
	protected Space						space_TaxComponent;
	protected Combobox					taxComponent;

	protected Label						recordType;
	protected Groupbox					gb_statusDetails;
	private boolean						enqModule			= false;

	private FeeType						feeType;
	private transient FeeTypeListCtrl	feeTypeListCtrl;
	private List<Property> listAdviseType = PennantStaticListUtil.getManualAdviseTypes();
	private List<ValueLabel> listTaxComponent = PennantStaticListUtil.getFeeTaxTypes();
	
	public static final int DEFAULT_ADVISETYPE = FinanceConstants.MANUAL_ADVISE_RECEIVABLE;

	private transient FeeTypeService	feeTypeService;
	private transient PagedListService	pagedListService;
	
	protected Label						label_HostFeeTypeCode;
	protected Hbox						hlayout_HostFeeTypeCode;
	protected Space						space_HostFeeTypeCode;
	protected Textbox					hostFeeTypeCode;
	
	protected Checkbox					amortzReq;

	private Boolean						feeTypeEnquiry				= false;
	/**
	 * default constructor.<br>
	 */
	public FeeTypeDialogCtrl() {
		super();
	}
	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeTypeDialog";
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FeeType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FeeTypeDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		
		try {
			setPageComponents(this.window_FeeTypeDialog);
			
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			

			// READ OVERHANDED params !
			if (arguments.containsKey("feeType")) {
				this.feeType = (FeeType) arguments.get("feeType");
				FeeType befImage = new FeeType();
				BeanUtils.copyProperties(this.feeType, befImage);
				this.feeType.setBefImage(befImage);

				setFeeType(this.feeType);
			} else {
				setFeeType(null);
			}
			
			if (arguments.containsKey("feeTypeEnquiry")) {
				this.feeTypeEnquiry = true;
				enqiryModule = true;
				this.feeType.setWorkflowId(0);
			} else {
				this.feeTypeEnquiry = false;
			}
			
			doLoadWorkFlow(this.feeType.isWorkflow(), this.feeType.getWorkflowId(), this.feeType.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FeeTypeDialog");
			} else {
				getUserWorkspace().allocateAuthorities("FeeTypeDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the feeTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete feeType here.
			if (arguments.containsKey("feeTypeListCtrl")) {
				setFeeTypeListCtrl((FeeTypeListCtrl) arguments.get("feeTypeListCtrl"));
			} else {
				setFeeTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFeeType());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
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
		doCancel();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_FeeTypeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_FeeTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		doShowNotes(this.feeType);

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFeeType
	 * @throws InterruptedException
	 */
	public void doShowDialog(FeeType aFeeType) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (feeType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.feeTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.feeTypeDesc.focus();
				if (StringUtils.isNotBlank(aFeeType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(aFeeType);
		
		if (feeTypeEnquiry) {
			this.window_FeeTypeDialog.setHeight("70%");
			this.window_FeeTypeDialog.setWidth("60%");
			this.window_FeeTypeDialog.doModal();
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.feeTypeCode);
		readOnlyComponent(true, this.feeTypeDesc);
		readOnlyComponent(true, this.manualAdvice);
		readOnlyComponent(true, this.accountingSetID);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.adviseType);
		readOnlyComponent(true,this.amortzReq);
		readOnlyComponent(true,this.taxApplicable);
		readOnlyComponent(true,this.hostFeeTypeCode);
		readOnlyComponent(true,this.taxComponent);

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
	
	public void onCheck$manualAdvice(Event event) {
		logger.debug("Entering");
		FeeType aFeeType =new FeeType();
		if (this.manualAdvice.isChecked()) {
			this.label_AdviseType.setVisible(true);
			this.hlayout_AdviseType.setVisible(true);
			fillList(adviseType, listAdviseType, String.valueOf(aFeeType.getAdviseType()));

		}else{
			this.label_AdviseType.setVisible(false);
			this.hlayout_AdviseType.setVisible(false);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnNew"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnDelete"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnEdit"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		//Empty sent any required attributes
		this.feeTypeCode.setMaxlength(8);
		this.feeTypeDesc.setMaxlength(35);
		this.hostFeeTypeCode.setMaxlength(50);
		
		this.accountingSetID.setModuleName("AccountingSet");
		this.accountingSetID.setValueColumn("EventCode");
		this.accountingSetID.setDescColumn("lovDescEventCodeName");
		this.accountingSetID.setValidateColumns(new String[] { "EventCode" });
		this.accountingSetID.setMandatoryStyle(false);
		
		Filter filters[] = new Filter[1];
		filters[0] = new Filter("EventCode", AccountEventConstants.ACCEVENT_MANFEE,Filter.OP_EQUAL);
		this.accountingSetID.setFilters(filters);
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFeeType
	 *            FeeType
	 */
	public void doWriteBeanToComponents(FeeType aFeeType) {
		logger.debug("Entering");
		this.feeTypeCode.setValue(aFeeType.getFeeTypeCode());
		this.feeTypeDesc.setValue(aFeeType.getFeeTypeDesc());
		this.manualAdvice.setChecked(aFeeType.isManualAdvice());
		this.hostFeeTypeCode.setValue(aFeeType.getHostFeeTypeCode());
		fillList(adviseType, listAdviseType, String.valueOf(aFeeType.getAdviseType()));
		
		if (this.manualAdvice.isChecked()) {
			this.label_AdviseType.setVisible(true);
			this.hlayout_AdviseType.setVisible(true);
		} else {
			this.label_AdviseType.setVisible(false);
			this.hlayout_AdviseType.setVisible(false);
		}
		
		if (aFeeType.getAccountSetId() != null) {
			this.accountingSetID.setValue(aFeeType.getAccountSetCode(), aFeeType.getAccountSetCodeName());
			this.accountingSetID.setObject(new AccountingSet(aFeeType.getAccountSetId()));
		}
		if(aFeeType.isNewRecord()){
			this.amortzReq.setChecked(true);
		}else{
			
			this.amortzReq.setChecked(aFeeType.isAmortzReq());
		}
		this.active.setChecked(aFeeType.isActive());
		
		if (aFeeType.isTaxApplicable()) {
			this.label_TaxComponent.setVisible(true);
			this.hlayout_TaxComponent.setVisible(true);
		} else {
			this.label_TaxComponent.setVisible(false);
			this.hlayout_TaxComponent.setVisible(false);
		}
		this.taxApplicable.setChecked(aFeeType.isTaxApplicable());
		fillComboBox(this.taxComponent, String.valueOf(aFeeType.getTaxComponent()), listTaxComponent, "");
		
		if(aFeeType.isNew() || (aFeeType.getRecordType() != null ? aFeeType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		
		
		this.recordStatus.setValue(aFeeType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFeeType
	 */
	public void doWriteComponentsToBean(FeeType aFeeType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Fee Type Code
		try {
			aFeeType.setFeeTypeCode(this.feeTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Description
		try {
			aFeeType.setFeeTypeDesc(this.feeTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Host Fee Type Code
		try {
			aFeeType.setHostFeeTypeCode(this.hostFeeTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Accounting Set ID
		try {
			AccountingSet accountingSet = (AccountingSet) this.accountingSetID.getObject();
			if (accountingSet != null && accountingSet.getAccountSetid() != 0) {
				aFeeType.setAccountSetId(accountingSet.getAccountSetid());
			} else {
				aFeeType.setAccountSetId(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//ApplicableFor
		try {
			aFeeType.setManualAdvice(this.manualAdvice.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Advise Type
		try {
			String strAdviseType = null;
			if (this.adviseType.getSelectedItem() != null) {
				strAdviseType = this.adviseType.getSelectedItem().getValue().toString();
			}
			if (strAdviseType != null && !PennantConstants.List_Select.equals(strAdviseType)) {
				aFeeType.setAdviseType(Integer.parseInt(strAdviseType));

			} else {
				aFeeType.setAdviseType(DEFAULT_ADVISETYPE);
			}
		} catch (WrongValueException we) {
					wve.add(we);
				}
		//Active
		try {
			aFeeType.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Tax Applicable
		try {
			aFeeType.setTaxApplicable(this.taxApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Tax Inclusive/Exclusive Type
		try {
			String taxComponentType = getComboboxValue(this.taxComponent);
			aFeeType.setTaxComponent(taxComponentType);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		
		//Amortization Required
		try {
			aFeeType.setAmortzReq(this.amortzReq.isChecked());
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Fee Type Code
		if (!this.feeTypeCode.isReadonly()) {
			this.feeTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_FeeTypeCode.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		//Description
		if (!this.feeTypeDesc.isReadonly()) {
			this.feeTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_FeeTypeDesc.value"), PennantRegularExpressions.REGEX_COMPANY_NAME, true));
		}
		//hostFeeTypeCode
		/*if (!this.hostFeeTypeCode.isReadonly()) {
			this.hostFeeTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_HostFeeTypeCode.value"), PennantRegularExpressions.REGEX_NUMERIC, true));
		}*/
		//accountingSetID
		if (!this.accountingSetID.isReadonly()) {
				this.accountingSetID.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_AccountingSetID.value"), null, false));
		}
		//adviseType
		if (!this.adviseType.isDisabled() && this.label_AdviseType.isVisible()) {
			this.adviseType.setConstraint(new StaticListValidator(listAdviseType, Labels.getLabel("label_FeeTypeDialog_AdviseType.value")));
		}
		//Tax Component
		if (!this.taxComponent.isDisabled() && this.label_TaxComponent.isVisible()) {
			this.taxComponent.setConstraint(new StaticListValidator(listTaxComponent, Labels.getLabel("label_FeeTypeDialog_TaxComponent.value")));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.feeTypeCode.setConstraint("");
		this.feeTypeDesc.setConstraint("");
		this.hostFeeTypeCode.setConstraint("");
		this.accountingSetID.setConstraint("");
		this.adviseType.setConstraint("");
		this.taxComponent.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Fee Type I D
		//Fee Type Code
		//Description
		//Active
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	protected void doClearMessage() {
		logger.debug("Entering");
		this.taxComponent.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */

	private void refreshList() {
		feeTypeListCtrl.search();
	}
	
	/*
	 * Method for Tax Applicable
	 */
	public void onCheck$taxApplicable(Event event) {
		logger.debug("Entering");
		
		this.taxComponent.setErrorMessage("");
		this.taxComponent.setConstraint("");
		fillComboBox(this.taxComponent, null, listTaxComponent, "");
		
		if (this.taxApplicable.isChecked()) {
			this.label_TaxComponent.setVisible(true);
			this.hlayout_TaxComponent.setVisible(true);
		} else {
			this.label_TaxComponent.setVisible(false);
			this.hlayout_TaxComponent.setVisible(false);
		}
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.feeType.isNewRecord()) {
			this.feeTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.feeTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.feeTypeDesc.setReadonly(isReadOnly("FeeTypeDialog_FeeTypeDesc"));
		readOnlyComponent(isReadOnly("FeeTypeDialog_Active"), this.active);
		this.manualAdvice.setDisabled(isReadOnly("FeeTypeDialog_ApplicableFor"));
		this.accountingSetID.setReadonly(isReadOnly("FeeTypeDialog_AccountSetId"));
		this.adviseType.setDisabled(isReadOnly("FeeTypeDialog_AdviseType"));
		this.hostFeeTypeCode.setReadonly(isReadOnly("FeeTypeDialog_HostFeeTypeCode"));
		this.taxApplicable.setDisabled(isReadOnly("FeeTypeDialog_TaxApplicable"));
		this.taxComponent.setDisabled(isReadOnly("FeeTypeDialog_TaxComponent"));

		readOnlyComponent(isReadOnly("FeeTypeDialog_AmortizationRequired"), this.amortzReq);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.feeType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
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
		logger.debug("Entering");

		doWriteBeanToComponents(this.feeType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
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
		boolean close = true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doSave();
				close = false;
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a FeeType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FeeType aFeeType = new FeeType();
		BeanUtils.copyProperties(getFeeType(), aFeeType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_FeeTypeDialog_FeeTypeCode.value") + " : " + aFeeType.getFeeTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aFeeType.getRecordType()).equals("")) {
				aFeeType.setVersion(aFeeType.getVersion() + 1);
				aFeeType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFeeType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFeeType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFeeType.getNextTaskId(), aFeeType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFeeType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
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
		this.feeTypeCode.setValue("");
		this.feeTypeDesc.setValue("");
		this.hostFeeTypeCode.setValue("");
		this.manualAdvice.setValue("");
		this.accountingSetID.setValue("");
		this.adviseType.setSelectedIndex(0);
		this.active.setValue("");

		this.taxApplicable.setChecked(false);
		this.taxComponent.setSelectedIndex(0);

		this.amortzReq.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FeeType aFeeType = new FeeType();
		BeanUtils.copyProperties(getFeeType(), aFeeType);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aFeeType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFeeType.getNextTaskId(), aFeeType);
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFeeType.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the FeeType object with the components data
			doWriteComponentsToBean(aFeeType);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFeeType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFeeType.getRecordType()).equals("")) {
				aFeeType.setVersion(aFeeType.getVersion() + 1);
				if (isNew) {
					aFeeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFeeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFeeType.setNewRecord(true);
				}
			}
		} else {
			aFeeType.setVersion(aFeeType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFeeType, tranType)) {
				//doWriteBeanToComponents(aFeeType);
				refreshList();
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(FeeType aFeeType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aFeeType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFeeType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFeeType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aFeeType.setTaskId(getTaskId());
			aFeeType.setNextTaskId(getNextTaskId());
			aFeeType.setRoleCode(getRole());
			aFeeType.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aFeeType, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aFeeType, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aFeeType, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		FeeType aFeeType = (FeeType) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getFeeTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFeeTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getFeeTypeService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aFeeType.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getFeeTypeService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aFeeType.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FeeTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FeeTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.feeType), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FeeType aFeeType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeeType.getBefImage(), aFeeType);
		return new AuditHeader(String.valueOf(aFeeType.getFeeTypeID()), null, null, null, auditDetail,
				aFeeType.getUserDetails(), getOverideMap());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FeeType getFeeType() {
		return this.feeType;
	}

	public void setFeeType(FeeType feeType) {
		this.feeType = feeType;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public FeeTypeService getFeeTypeService() {
		return this.feeTypeService;
	}

	public void setFeeTypeListCtrl(FeeTypeListCtrl feeTypeListCtrl) {
		this.feeTypeListCtrl = feeTypeListCtrl;
	}

	public FeeTypeListCtrl getFeeTypeListCtrl() {
		return this.feeTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	@Override
	protected String getReference() {
		return String.valueOf(this.feeType.getFeeTypeID());
	}
}
