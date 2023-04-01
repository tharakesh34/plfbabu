/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AssignmentDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * * Modified
 * Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.assignment;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDeal;
import com.pennant.backend.model.applicationmaster.AssignmentRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.AssignmentService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Assignment/assignmentDialog.zul file. <br>
 */
public class AssignmentDialogCtrl extends GFCBaseCtrl<Assignment> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssignmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AssignmentDialog;
	protected Space space_Description;
	protected Textbox assignmentId;
	protected Textbox description;
	protected ExtendedCombobox dealCode;
	protected ExtendedCombobox loanType;
	protected Datebox disbDate;
	protected Decimalbox sharingPercentage;
	protected Checkbox gST;
	protected Combobox opexFeeType;
	protected Checkbox active;
	private Assignment assignment;

	private Button buttonAssignmentRateDialog_btnNew;
	private Listbox listBoxAssignmentRates;
	private transient AssignmentListCtrl assignmentListCtrl;
	@Autowired
	private transient AssignmentService assignmentService;

	private List<ValueLabel> listOpexFeeType = PennantStaticListUtil.getOpexFeeTypes();
	private List<AssignmentRate> assignmentRateDetailList = new ArrayList<AssignmentRate>();
	private boolean isOpexRateMandatory;

	/**
	 * default constructor.<br>
	 */
	public AssignmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssignmentDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.assignment.getId());
	}

	public void onCreate$window_AssignmentDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssignmentDialog);

		try {
			// Get the required arguments.
			this.assignment = (Assignment) arguments.get("assignment");
			this.assignmentListCtrl = (AssignmentListCtrl) arguments.get("assignmentListCtrl");

			if (this.assignment == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Assignment assignment = new Assignment();
			BeanUtils.copyProperties(this.assignment, assignment);
			this.assignment.setBefImage(assignment);

			// Render the page and display the data.
			doLoadWorkFlow(this.assignment.isWorkflow(), this.assignment.getWorkflowId(),
					this.assignment.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.assignment);
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

		this.description.setMaxlength(50);

		this.dealCode.setMandatoryStyle(true);
		this.dealCode.setModuleName("AssignmentDeal");
		this.dealCode.setValueColumn("Code");
		this.dealCode.setDescColumn("Description");
		this.dealCode.setValidateColumns(new String[] { "Code" });
		Filter[] activeFilter = new Filter[1];
		activeFilter[0] = new Filter("Active", "1", Filter.OP_EQUAL);
		this.dealCode.setFilters(activeFilter);

		this.loanType.setMandatoryStyle(true);
		this.loanType.setModuleName("FinanceType");
		this.loanType.setValueColumn("FinType");
		this.loanType.setDescColumn("FinTypeDesc");
		this.loanType.setValidateColumns(new String[] { "FinType" });

		this.disbDate.setFormat(PennantConstants.dateFormat);
		this.sharingPercentage.setMaxlength(8);
		this.sharingPercentage.setFormat(PennantConstants.rateFormate9);
		this.sharingPercentage.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.sharingPercentage.setScale(2);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onChange$opexFeeType(Event event) {
		logger.debug(Literal.ENTERING);
		String opexFeeType = this.opexFeeType.getSelectedItem().getValue();
		isOpexRateMandatory(opexFeeType);
		logger.debug(Literal.LEAVING);
	}

	private void isOpexRateMandatory(String opexFeeType) {
		logger.debug(Literal.ENTERING);
		if (PennantConstants.OPEX_FEE_TYPE_FIXED.equals(StringUtils.trimToEmpty(opexFeeType))) {
			this.isOpexRateMandatory = true;
		} else {
			this.isOpexRateMandatory = false;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssignmentDialog_btnSave"));
		this.buttonAssignmentRateDialog_btnNew
				.setVisible(getUserWorkspace().isAllowed("button_AssignmentRateDialog_btnNew"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.assignment);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assignmentListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assignment.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dealCode(Event event) {
		logger.debug(Literal.ENTERING);
		AssignmentDeal details = new AssignmentDeal();
		Object dataObject = dealCode.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.dealCode.setValue("");
			this.dealCode.setDescription("");
			this.dealCode.setAttribute("DealId", null);
		} else {
			details = (AssignmentDeal) dataObject;
			this.dealCode.setAttribute("DealId", details.getId());
		}
		this.loanType.setObject(null);
		this.loanType.setValue("");
		this.loanType.setDescColumn("");
		fillFinTypeDetails(details.getId());
		logger.debug(Literal.LEAVING);
	}

	private void fillFinTypeDetails(long dealId) {
		logger.debug(Literal.ENTERING);
		List<String> finTypes = assignmentService.getFinTypes(dealId);
		this.loanType.setModuleName("FinanceType");
		this.loanType.setValueColumn("FinType");
		this.loanType.setDescColumn("FinTypeDesc");
		this.loanType.setValidateColumns(new String[] { "FinType" });
		Filter[] finTypeFilter = new Filter[1];
		if (finTypes.isEmpty()) {
			finTypeFilter[0] = new Filter("FinType", null, Filter.OP_EQUAL);
		} else {
			finTypeFilter[0] = new Filter("FinType", finTypes, Filter.OP_IN);
		}
		loanType.setFilters(finTypeFilter);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$loanType(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = loanType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.loanType.setValue("");
			this.loanType.setDescription("");
			this.loanType.setAttribute("FinTypeCode", null);
		} else {
			FinanceType details = (FinanceType) dataObject;
			this.loanType.setAttribute("FinTypeCode", details.getId());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param assignment
	 * 
	 */
	public void doWriteBeanToComponents(Assignment aAssignment) {
		logger.debug(Literal.ENTERING);

		if (aAssignment.getId() != Long.MIN_VALUE) {
			this.assignmentId.setValue(String.format("%08d", aAssignment.getId()));
		}
		this.description.setValue(aAssignment.getDescription());
		this.dealCode.setValue(aAssignment.getDealCode());
		this.loanType.setValue(aAssignment.getLoanType());
		this.disbDate.setValue(aAssignment.getDisbDate());
		this.sharingPercentage.setValue(aAssignment.getSharingPercentage());
		this.gST.setChecked(aAssignment.isGst());
		fillComboBox(this.opexFeeType, aAssignment.getOpexFeeType(), listOpexFeeType, "");
		this.active.setChecked(aAssignment.isActive());

		if (aAssignment.isNewRecord()
				|| StringUtils.equals(aAssignment.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		if (aAssignment.isNewRecord()) {
			this.dealCode.setDescription("");
			this.loanType.setDescription("");
		} else {
			this.dealCode.setDescription(aAssignment.getDealCodeDesc());
			this.loanType.setDescription(aAssignment.getLoanTypeDesc());
		}

		if (!aAssignment.isNewRecord()) {
			this.dealCode.setValue(StringUtils.trimToEmpty(aAssignment.getDealCode()),
					StringUtils.trimToEmpty(aAssignment.getDealCodeDesc()));
			if (aAssignment.getDealId() != null) {
				this.dealCode.setAttribute("DealId", aAssignment.getDealId());
			} else {
				this.dealCode.setAttribute("DealId", null);
			}

			this.loanType.setValue(StringUtils.trimToEmpty(aAssignment.getLoanType()),
					StringUtils.trimToEmpty(aAssignment.getLoanTypeDesc()));
			if (aAssignment.getDealId() != null) {
				this.loanType.setAttribute("FinTypeCode", aAssignment.getLoanType());
			} else {
				this.loanType.setAttribute("FinTypeCode", null);
			}
		}
		isOpexRateMandatory(aAssignment.getOpexFeeType());
		doFillAssignmentRateDetailsList(aAssignment.getAssignmentRateList());
		this.recordStatus.setValue(aAssignment.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAssignment
	 */
	public void doWriteComponentsToBean(Assignment aAssignment) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Description
		try {
			aAssignment.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Deal Code
		try {
			this.dealCode.getValidatedValue();
			Object obj = this.dealCode.getAttribute("DealId");
			if (obj != null) {
				aAssignment.setDealId(Long.valueOf(obj.toString()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Loan Type
		try {
			this.loanType.getValidatedValue();
			Object obj = this.loanType.getAttribute("FinTypeCode");
			if (obj != null) {
				aAssignment.setLoanType(String.valueOf(obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Disb Date
		try {
			aAssignment.setDisbDate(this.disbDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sharing Percentage
		try {
			if (this.sharingPercentage.getValue() != null) {
				aAssignment.setSharingPercentage(this.sharingPercentage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// GST
		try {
			aAssignment.setGst(this.gST.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Opex Fee Type
		try {
			String strOpexFeeType = null;
			if (this.opexFeeType.getSelectedItem() != null) {
				strOpexFeeType = this.opexFeeType.getSelectedItem().getValue().toString();
			}
			if (strOpexFeeType != null && !PennantConstants.List_Select.equals(strOpexFeeType)) {
				aAssignment.setOpexFeeType(strOpexFeeType);

			} else {
				aAssignment.setOpexFeeType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aAssignment.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aAssignment.setAssignmentRateList(assignmentRateDetailList);
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
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
	 * @param assignment The entity that need to be render.
	 */
	public void doShowDialog(Assignment assignment) {
		logger.debug(Literal.ENTERING);

		if (assignment.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.description.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assignment.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.description.focus();
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

		doWriteBeanToComponents(assignment);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssignmentDialog_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.dealCode.isReadonly()) {
			this.dealCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssignmentDialog_DealCode.value"), null, true));
		}
		if (!this.loanType.isReadonly()) {
			this.loanType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssignmentDialog_LoanType.value"), null, true));
		}
		if (!this.disbDate.isReadonly()) {
			this.disbDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_AssignmentDialog_DisbDate.value"), true));
		}
		if (!this.sharingPercentage.isReadonly()) {
			this.sharingPercentage.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_AssignmentDialog_SharingPercentage.value"), 9, true, false, 0, 100));
		}
		if (!this.opexFeeType.isDisabled()) {
			this.opexFeeType.setConstraint(new PTListValidator(
					Labels.getLabel("label_AssignmentDialog_OpexFeeType.value"), listOpexFeeType, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.description.setConstraint("");
		this.dealCode.setConstraint("");
		this.loanType.setConstraint("");
		this.disbDate.setConstraint("");
		this.sharingPercentage.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.description.setErrorMessage("");
		this.dealCode.setErrorMessage("");
		this.loanType.setErrorMessage("");
		this.disbDate.setErrorMessage("");
		this.sharingPercentage.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	protected boolean validateAssignmentRateList() {
		logger.debug(Literal.ENTERING);
		boolean validateOpexRate = true;
		if (CollectionUtils.isNotEmpty(assignmentRateDetailList)) {
			if (PennantConstants.OPEX_FEE_TYPE_FIXED.equals(this.opexFeeType.getSelectedItem().getValue())) {
				for (AssignmentRate assignmentRate : assignmentRateDetailList) {
					if (assignmentRate.getOpexRate().intValue() <= 0) {
						validateOpexRate = false;
						break;
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return validateOpexRate;
	}

	/**
	 * Deletes a Assignment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Assignment aAssignment = new Assignment();
		BeanUtils.copyProperties(this.assignment, aAssignment);

		doDelete(String.valueOf(aAssignment.getId()), aAssignment);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.assignment.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);

		}

		readOnlyComponent(isReadOnly("AssignmentDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("AssignmentDialog_DealCode"), this.dealCode);
		readOnlyComponent(isReadOnly("AssignmentDialog_LoanType"), this.loanType);
		readOnlyComponent(isReadOnly("AssignmentDialog_DisbDate"), this.disbDate);
		readOnlyComponent(isReadOnly("AssignmentDialog_SharingPercentage"), this.sharingPercentage);
		readOnlyComponent(isReadOnly("AssignmentDialog_GST"), this.gST);
		readOnlyComponent(isReadOnly("AssignmentDialog_OpexFeeType"), this.opexFeeType);
		readOnlyComponent(isReadOnly("AssignmentDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assignment.isNewRecord()) {
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
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.dealCode);
		readOnlyComponent(true, this.loanType);
		readOnlyComponent(true, this.disbDate);
		readOnlyComponent(true, this.sharingPercentage);
		readOnlyComponent(true, this.gST);
		readOnlyComponent(true, this.opexFeeType);
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
		this.description.setValue("");
		this.dealCode.setValue("");
		this.dealCode.setDescription("");
		this.loanType.setValue("");
		this.loanType.setDescription("");
		this.disbDate.setText("");
		this.sharingPercentage.setValue("");
		this.gST.setChecked(false);
		this.opexFeeType.setSelectedIndex(0);
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	public void onClick$buttonAssignmentRateDialog_btnNew(Event event) {
		logger.debug(Literal.ENTERING);

		AssignmentRate assignmentRate = new AssignmentRate();
		assignmentRate.setNewRecord(true);
		assignmentRate.setWorkflowId(0);
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("assignmentHeaderList", getHeaderDetails());
		map.put("isOpexRateMandatory", isOpexRateMandatory);
		map.put("assignmentRate", assignmentRate);
		map.put("assignmentDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Assignment/AssignmentRateDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onAssignmentRateItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxAssignmentRates.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final AssignmentRate assignmentRate = (AssignmentRate) item.getAttribute("data");

			if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, assignmentRate.getRecordType())
					|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, assignmentRate.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("assignmentHeaderList", getHeaderDetails());
				map.put("isOpexRateMandatory", isOpexRateMandatory);
				map.put("assignmentRate", assignmentRate);
				map.put("assignmentDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enqiryModule", enqiryModule);

				try {
					Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Assignment/AssignmentRateDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);

	}

	private ArrayList<Object> getHeaderDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.assignmentId.getValue());
		arrayList.add(1, this.description.getValue());
		arrayList.add(2, this.loanType.getValue());
		arrayList.add(3, this.dealCode.getValue());
		return arrayList;
	}

	public void doFillAssignmentRateDetailsList(List<AssignmentRate> assignmentRates) {
		logger.debug(Literal.ENTERING);

		this.listBoxAssignmentRates.getItems().clear();
		if (assignmentRates != null) {

			for (AssignmentRate assignmentRate : assignmentRates) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(DateUtil.formatToLongDate(assignmentRate.getEffectiveDate()));
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(assignmentRate.getMclrRate().doubleValue(), 9));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.formatRate(assignmentRate.getBankSpreadRate().doubleValue(), 9));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(assignmentRate.getOpexRate().doubleValue(), 9));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(assignmentRate.getResetFrequency());
				lc.setParent(item);

				item.setAttribute("data", assignmentRate);
				ComponentsCtrl.applyForward(item, "onDoubleClick = onAssignmentRateItemDoubleClicked");
				this.listBoxAssignmentRates.appendChild(item);
			}
			setAssignmentRateDetailList(assignmentRates);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Assignment aAssignment = new Assignment();
		BeanUtils.copyProperties(this.assignment, aAssignment);
		boolean isNew = false;

		doSetValidation();
		if (!validateAssignmentRateList()) {
			MessageUtil.showMessage("Opex Rate Should be greater than zero.");
			return;
		}
		doWriteComponentsToBean(aAssignment);

		isNew = aAssignment.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAssignment.getRecordType())) {
				aAssignment.setVersion(aAssignment.getVersion() + 1);
				if (isNew) {
					aAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssignment.setNewRecord(true);
				}
			}
		} else {
			aAssignment.setVersion(aAssignment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aAssignment, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Assignment aAssignment, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAssignment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAssignment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAssignment.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAssignment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAssignment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAssignment);
				}

				if (isNotesMandatory(taskId, aAssignment)) {
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

			aAssignment.setTaskId(taskId);
			aAssignment.setNextTaskId(nextTaskId);
			aAssignment.setRoleCode(getRole());
			aAssignment.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAssignment, tranType);
			String operationRefs = getServiceOperations(taskId, aAssignment);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAssignment, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAssignment, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Assignment aAssignment = (Assignment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = assignmentService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = assignmentService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = assignmentService.doApprove(auditHeader);

					if (aAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = assignmentService.doReject(auditHeader);
					if (aAssignment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssignmentDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssignmentDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assignment), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Assignment aAssignment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssignment.getBefImage(), aAssignment);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAssignment.getUserDetails(),
				getOverideMap());
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public List<AssignmentRate> getAssignmentRateDetailList() {
		return assignmentRateDetailList;
	}

	public void setAssignmentRateDetailList(List<AssignmentRate> assignmentRateDetailList) {
		this.assignmentRateDetailList = assignmentRateDetailList;
	}

}
