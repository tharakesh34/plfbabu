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
 * FileName    		:  FinTypeExpenseDialogCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-12-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-12-2017       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.financetype;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/FinTypeExpenseDialog.zul file.
 */
public class FinTypeExpenseDialogCtrl extends GFCBaseCtrl<FinTypeExpense> {
	private static final long		serialVersionUID	= 1L;
	private static final Logger		logger				= Logger.getLogger(FinTypeExpenseDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window				window_FinTypeExpenseDialog;

	protected ExtendedCombobox		expenseType;
	protected Combobox				calculationType;
	protected CurrencyBox			amount;
	protected Space					space_percentage;
	protected Decimalbox			percentage;
	protected Row					row_CalculationOn;
	protected Combobox				calculationOn;
	protected Checkbox				amortReq;
	protected Checkbox				taxApplicable;
	protected Checkbox				active;
	protected Label					label_FinTypeExpenseDialog_AmtPerc;
	protected Row					row_AmzTax;
	// not auto wired vars
	private FinTypeExpense			finTypeExpense;															// overhanded per param
	

	private transient boolean		validationOn;

	private String					userRole			= "";

	private FinTypeExpenseListCtrl	finTypeExpenseListCtrl;
	private List<FinTypeExpense>	finTypeExpenseList;
	private int						ccyFormat			= 0;
	boolean							isOriginationFee	= false;
	boolean							isOverdraft			= false;

	/**
	 * default constructor.<br>
	 */
	public FinTypeExpenseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeExpenseDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinTypeExpenses object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinTypeExpenseDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeExpenseDialog);

		try {
			if (arguments.containsKey("finTypeExpense")) {
				this.finTypeExpense = (FinTypeExpense) arguments.get("finTypeExpense");
				FinTypeExpense befImage = new FinTypeExpense();
				BeanUtils.copyProperties(this.finTypeExpense, befImage);
				this.finTypeExpense.setBefImage(befImage);
				setFinTypeExpense(this.finTypeExpense);
				//this.isOriginationFee = this.finTypeExpense.isOriginationFee();
			} else {
				setFinTypeExpense(null);
			}

			if (arguments.containsKey("ccyFormat")) {
				ccyFormat = (Integer) arguments.get("ccyFormat");
			}

			if (arguments.containsKey("isOverdraft")) {
				isOverdraft = (boolean) arguments.get("isOverdraft");
			}

			if (arguments.containsKey("finTypeExpenseListCtrl")) {
				setFinTypeExpenseListCtrl((FinTypeExpenseListCtrl) arguments.get("finTypeExpenseListCtrl"));
			} else {
				setFinTypeExpenseListCtrl(null);
			}

			if (arguments.containsKey("role")) {
				userRole = arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, this.pageRightName);
			}

			this.finTypeExpense.setWorkflowId(0);
			doLoadWorkFlow(this.finTypeExpense.isWorkflow(), this.finTypeExpense.getWorkflowId(),
					this.finTypeExpense.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getFinTypeExpense());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypeExpenseDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.expenseType.setMaxlength(8);
		this.expenseType.getTextbox().setWidth("150px");
		this.expenseType.setMandatoryStyle(true);
		this.expenseType.setModuleName("ExpenseType");
		this.expenseType.setValueColumn("ExpenseTypeCode");
		this.expenseType.setDescColumn("ExpenseTypeDesc");
		this.expenseType.setValidateColumns(new String[] { "ExpenseTypeCode" });

		this.amount.setVisible(true);
		this.amount.setMandatory(true);
		this.amount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.amount.setScale(ccyFormat);

		this.percentage.setMaxlength(6);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseDialog_btnSave"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseDialog_btnDelete"));
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
		MessageUtil.showHelpWindow(event, window_FinTypeExpenseDialog);
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.finTypeExpense.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinTypeExpense
	 *            FinTypeExpense
	 */
	public void doWriteBeanToComponents(FinTypeExpense aFinTypeExpense) {
		logger.debug("Entering");
		
		long expenseTypeId = aFinTypeExpense.getExpenseTypeID();

		if (expenseTypeId != Long.MIN_VALUE && expenseTypeId != 0) {
			this.expenseType.setAttribute("expenseTypeID", expenseTypeId);
			this.expenseType.setValue(aFinTypeExpense.getExpenseTypeCode(), aFinTypeExpense.getExpenseTypeDesc());
		}

		this.amount.setValue(PennantAppUtil.formateAmount(aFinTypeExpense.getAmount(), ccyFormat));
		this.percentage.setValue(aFinTypeExpense.getPercentage());

		String calTypeExcludeFields = "," + PennantConstants.FEE_CALCULATION_TYPE_RULE + ",";
		fillComboBox(this.calculationType, aFinTypeExpense.getCalculationType(),
				PennantStaticListUtil.getFeeCalculationTypes(), calTypeExcludeFields);
		
		doSetCalculationTypeProp();
		
		fillComboBox(this.calculationOn, aFinTypeExpense.getCalculateOn(),
				PennantStaticListUtil.getExpenseCalculatedOnList(), "");

		if(aFinTypeExpense.isNewRecord()){
			this.active.setChecked(true);
			this.amortReq.setChecked(true);
		}else{
			this.active.setChecked(aFinTypeExpense.isActive());
			this.amortReq.setChecked(aFinTypeExpense.isAmortReq());
		}
		
		this.taxApplicable.setChecked(aFinTypeExpense.isTaxApplicable());
		this.recordStatus.setValue(aFinTypeExpense.getRecordStatus());

		

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypeExpense
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinTypeExpense aFinTypeExpense) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		//Expense Type Code
		try {
			this.expenseType.getValidatedValue();
			String expenseTypeId = String.valueOf(this.expenseType.getAttribute("expenseTypeID"));
			aFinTypeExpense.setExpenseTypeID(Long.valueOf((expenseTypeId)));
			aFinTypeExpense.setExpenseTypeCode(this.expenseType.getValue()); // Customer CIF
			aFinTypeExpense.setExpenseTypeDesc(this.expenseType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Calculation Type
		try {
			if ("#".equals(getComboboxValue(this.calculationType))) {
				throw new WrongValueException(this.calculationType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeExpenseDialog_CalculationType.value") }));
			}
			aFinTypeExpense.setCalculationType(getComboboxValue(this.calculationType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Calculation On
		try {
			if (this.row_CalculationOn.isVisible() && "#".equals(getComboboxValue(this.calculationOn))) {
				throw new WrongValueException(this.calculationOn, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeExpenseDialog_CalculationOn.value") }));
			}
			aFinTypeExpense.setCalculateOn(getComboboxValue(this.calculationOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Amount
		try {
			aFinTypeExpense.setAmount(PennantAppUtil.unFormateAmount(
					this.amount.isReadonly() ? this.amount.getActualValue() : this.amount.getValidateValue(),
					ccyFormat));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Percentage
		try {
			if (this.percentage.isVisible()) {
				BigDecimal percentageValue = this.percentage.getValue();

				if (percentageValue == null || percentageValue.compareTo(BigDecimal.ZERO) == 0) {
					throw new WrongValueException(this.percentage, Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("label_FinTypeExpenseDialog_AmtPerc.value"), "0" }));
				} else if (percentageValue.compareTo(BigDecimal.ZERO) != 1) {
					throw new WrongValueException(this.percentage, Labels.getLabel("FIELD_NO_NEGATIVE",
							new String[] { Labels.getLabel("label_FinTypeExpenseDialog_AmtPerc.value") }));
				} else if (percentageValue.compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.percentage, Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { Labels.getLabel("label_FinTypeExpenseDialog_AmtPerc.value"), "100" }));
				}
			}
			aFinTypeExpense.setPercentage(
					new BigDecimal(PennantApplicationUtil.formatRate(this.percentage.getValue().doubleValue(), 2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Amortization Required Flag
		aFinTypeExpense.setAmortReq(this.amortReq.isChecked());
	
		//GST Applicable Flag
		
		aFinTypeExpense.setTaxApplicable(this.taxApplicable.isChecked());
		
		//Active
		aFinTypeExpense.setActive(this.active.isChecked());
		

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinTypeExpense.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinTypeExpense
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinTypeExpense aFinTypeExpense) throws InterruptedException {
		logger.debug("Entering");
		// set Readonly mode accordingly if the object is new or not.
		if (aFinTypeExpense.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.expenseType.focus();
		} else {
			this.calculationType.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinTypeExpense);
			this.window_FinTypeExpenseDialog.doModal();
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
		if (!this.expenseType.isReadonly()) {
			this.expenseType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinTypeExpenseDialog_ExpenseTypeCode.value"), null, true, true));
		}
		if (!this.amount.isDisabled()) {
			this.amount
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinTypeExpenseDialog_AmtPerc.value"),
							ccyFormat, this.amount.isVisible(), false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.expenseType.setConstraint("");
		this.calculationType.setConstraint("");
		this.calculationOn.setConstraint("");
		this.amount.setConstraint("");
		this.percentage.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinTypeExpense object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinTypeExpense aFinTypeExpense = new FinTypeExpense();
		BeanUtils.copyProperties(getFinTypeExpense(), aFinTypeExpense);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_FinTypeExpenseDialog_ExpenseTypeCode.value") + " : "
				+ aFinTypeExpense.getExpenseTypeCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			/*
			 * if(!FinTypeExpenseListCtrl.validateFeeAccounting(aFinTypeFees,false) ){ return; }
			 */

			logger.debug("doDelete: Yes");
			if (StringUtils.isBlank(aFinTypeExpense.getRecordType())) {
				aFinTypeExpense.setVersion(aFinTypeExpense.getVersion() + 1);
				aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aFinTypeExpense.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aFinTypeExpense.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aFinTypeExpense.setVersion(aFinTypeExpense.getVersion() + 1);
				aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFinTypeExpenseProcess(aFinTypeExpense, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeExpenseDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {

					getFinTypeExpenseListCtrl().doFillFinTypeExpenseType(this.finTypeExpenseList);

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
		if (getFinTypeExpense().isNewRecord()) {
			readOnlyComponent(isReadOnly("FinTypeExpenseDialog_expenseType"), this.expenseType);
		} else {
			this.expenseType.setReadonly(true);
		}
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_calculationType"), this.calculationType);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_AmtPerc"), this.amount);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_AmtPerc"), this.percentage);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_calculationOn"), this.calculationOn);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_amortizationRequired"), this.amortReq);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_taxApplicable"), this.taxApplicable);
		readOnlyComponent(isReadOnly("FinTypeExpenseDialog_active"), this.active);
		
		this.row_AmzTax.setVisible(!isReadOnly("ExpenseTypeDialog_RowAmortTaxApplicable"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypeExpense.isNewRecord()) {
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

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		readOnlyComponent(true, this.expenseType);
		readOnlyComponent(true, this.calculationType);
		readOnlyComponent(true, this.amount);
		readOnlyComponent(true, this.percentage);
		readOnlyComponent(true, this.calculationOn);
		readOnlyComponent(true, this.amortReq);
		readOnlyComponent(true, this.taxApplicable);
		readOnlyComponent(true, this.active);

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
		this.expenseType.setValue("");
		this.expenseType.setDescription("");
		this.amount.setValue("");
		this.percentage.setValue("");
		this.calculationType.setValue("");
		this.calculationOn.setValue("");
		this.amortReq.setChecked(false);
		this.taxApplicable.setChecked(false);
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinTypeExpense aFinTypeExpense = new FinTypeExpense();
		BeanUtils.copyProperties(getFinTypeExpense(), aFinTypeExpense);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinTypeExpense object with the components data
		doWriteComponentsToBean(aFinTypeExpense);

		// Write the additional validations as per below example
		// get the selected FinTypeExpense object from the listbox
		// Do data level validations here
		isNew = aFinTypeExpense.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypeExpense.getRecordType())) {
				aFinTypeExpense.setVersion(aFinTypeExpense.getVersion() + 1);
				if (isNew) {
					aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypeExpense.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aFinTypeExpense.setVersion(1);
				aFinTypeExpense.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aFinTypeExpense.getRecordType())) {
				aFinTypeExpense.setVersion(aFinTypeExpense.getVersion() + 1);
				aFinTypeExpense.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aFinTypeExpense.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newFinTypeExpenseProcess(aFinTypeExpense, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeExpenseDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinTypeExpenseListCtrl().doFillFinTypeExpenseType(this.finTypeExpenseList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method validates FinTypeExpense details <br>
	 * and will return AuditHeader
	 *
	 */
	private AuditHeader newFinTypeExpenseProcess(FinTypeExpense aFinTypeExpense, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinTypeExpense, tranType);
		finTypeExpenseList = new ArrayList<FinTypeExpense>();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = aFinTypeExpense.getExpenseTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeExpenseDialog_ExpenseTypeCode.value") + ":" + valueParm[0];
		List<FinTypeExpense> finTypeExistingList = null;

		finTypeExistingList = getFinTypeExpenseListCtrl().getFinTypeExpenseList();

		if (finTypeExistingList != null && finTypeExistingList.size() > 0) {
			for (int i = 0; i < finTypeExistingList.size(); i++) {
				FinTypeExpense finTypeExpense = finTypeExistingList.get(i);
				if (finTypeExpense.getExpenseTypeID() == aFinTypeExpense.getExpenseTypeID()) {
					// Both Current and Existing list rating same
					if (aFinTypeExpense.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aFinTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypeExpenseList.add(aFinTypeExpense);
						} else if (aFinTypeExpense.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aFinTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aFinTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypeExpenseList.add(aFinTypeExpense);
						} else if (aFinTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							// List<FinTypeExpense> savedList =
							// getFinTypeFeesListCtrl().getFinanceType().getFinTypeFeesList();
							List<FinTypeExpense> savedList = getFinTypeExpenseListCtrl().getFinTypeExpenseList();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypeExpense accType = savedList.get(j);
								if (accType.getFinType().equals(aFinTypeExpense.getFinType())) {
									finTypeExpenseList.add(accType);
								}
							}
						} else if (aFinTypeExpense.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aFinTypeExpense.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finTypeExpenseList.add(finTypeExpense);
						}
					}
				} else {
					finTypeExpenseList.add(finTypeExpense);
				}
			}
		}
		if (!recordAdded) {
			finTypeExpenseList.add(aFinTypeExpense);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	public void onSelect$calculationType(Event event) {
		logger.debug("Entering");
		this.amount.setValue(BigDecimal.ZERO);
		this.percentage.setValue(BigDecimal.ZERO);
		this.calculationOn.setValue("");
		doSetCalculationTypeProp();
		logger.debug("Leaving");
	}

	public void onFulfill$expenseType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = expenseType.getObject();

		if (dataObject instanceof String) {
			this.expenseType.setValue(dataObject.toString());
			this.amortReq.setChecked(false);
			this.taxApplicable.setChecked(false);
		} else {
			ExpenseType details = (ExpenseType) dataObject;
			if (details != null) {
				this.expenseType.setAttribute("expenseTypeID", details.getExpenseTypeId());
				this.amortReq.setChecked(details.isAmortReq());
				this.taxApplicable.setChecked(details.isTaxApplicable());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doSetCalculationTypeProp() {
		if (StringUtils.equals(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT,
				this.calculationType.getSelectedItem().getValue().toString())) {
			this.amount.setVisible(true);
			this.amount.setMandatory(true);
			this.percentage.setVisible(false);
			this.row_CalculationOn.setVisible(false);
			this.space_percentage.setVisible(false);
		} else if (StringUtils.equals(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE,
				this.calculationType.getSelectedItem().getValue().toString())) {
			this.percentage.setVisible(true);
			this.amount.setVisible(false);
			this.amount.setMandatory(false);
			this.row_CalculationOn.setVisible(true);
			this.space_percentage.setVisible(true);
			
		}
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

	private AuditHeader getAuditHeader(FinTypeExpense aFinTypeExpense, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeExpense.getBefImage(), aFinTypeExpense);
		return new AuditHeader(aFinTypeExpense.getFinType(), null, null, null, auditDetail,
				aFinTypeExpense.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypeExpenseDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.finTypeExpense);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finTypeExpense.getExpenseTypeID());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.expenseType.setErrorMessage("");
		this.amount.setErrorMessage("");
		this.percentage.setErrorMessage("");
		this.calculationType.setErrorMessage("");
		this.calculationOn.setErrorMessage("");
		logger.debug("Leaving");
	}

	public FinTypeExpense getFinTypeExpense() {
		return finTypeExpense;
	}

	public void setFinTypeExpense(FinTypeExpense finTypeExpense) {
		this.finTypeExpense = finTypeExpense;
	}

	public FinTypeExpenseListCtrl getFinTypeExpenseListCtrl() {
		return finTypeExpenseListCtrl;
	}

	public void setFinTypeExpenseListCtrl(FinTypeExpenseListCtrl finTypeExpenseListCtrl) {
		this.finTypeExpenseListCtrl = finTypeExpenseListCtrl;
	}

	public List<FinTypeExpense> getFinTypeExpenseList() {
		return finTypeExpenseList;
	}

	public void setFinTypeExpenseList(List<FinTypeExpense> finTypeExpenseList) {
		this.finTypeExpenseList = finTypeExpenseList;
	}

}
