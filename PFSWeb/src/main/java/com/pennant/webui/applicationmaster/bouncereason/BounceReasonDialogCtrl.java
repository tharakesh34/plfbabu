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
 * * FileName : BounceReasonDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * *
 * Modified Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.bouncereason;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.applicationmaster.BounceReasonService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/BounceReason/bounceReasonDialog.zul file. <br>
 */
public class BounceReasonDialogCtrl extends GFCBaseCtrl<BounceReason> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BounceReasonDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BounceReasonDialog;
	protected Textbox bounceCode;
	protected Combobox reasonType;
	protected Combobox category;
	protected Textbox reason;
	protected Combobox action;
	protected ExtendedCombobox ruleID;
	protected Textbox returnCode;
	protected Checkbox active;
	protected Combobox instrumentType;
	protected Intbox holdMarkBounceCount;
	protected Space space_holdMarkBounceCount;
	protected Space spaceInstrumentType;

	private transient BounceReasonListCtrl bounceReasonListCtrl;
	private transient BounceReasonService bounceReasonService;

	private List<Property> listReasonType = PennantStaticListUtil.getReasonType();
	private List<Property> listCategory = PennantStaticListUtil.getCategoryType();
	private List<ValueLabel> listAction = PennantStaticListUtil.getAction();
	private final List<ValueLabel> instrumentTypeList = MandateUtil.getInstrumentTypes();
	private BounceReason bounceReason;

	/**
	 * default constructor.<br>
	 */
	public BounceReasonDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BounceReasonDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.bounceReason.getBounceID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BounceReasonDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BounceReasonDialog);

		try {
			// Get the required arguments.
			this.bounceReason = (BounceReason) arguments.get("bounceReason");
			this.bounceReasonListCtrl = (BounceReasonListCtrl) arguments.get("bounceReasonListCtrl");

			if (this.bounceReason == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BounceReason bounceReason = new BounceReason();
			BeanUtils.copyProperties(this.bounceReason, bounceReason);
			this.bounceReason.setBefImage(bounceReason);

			// Render the page and display the data.
			doLoadWorkFlow(this.bounceReason.isWorkflow(), this.bounceReason.getWorkflowId(),
					this.bounceReason.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.bounceReason);
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

		this.bounceCode.setMaxlength(8);
		this.reason.setMaxlength(150);
		this.ruleID.setModuleName("Rule");
		this.ruleID.setMandatoryStyle(true);
		this.ruleID.setValueColumn("RuleCode");
		this.ruleID.setDescColumn("RuleCodeDesc");
		this.ruleID.setValidateColumns(new String[] { "RuleCode" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("RuleModule", "BOUNCE", Filter.OP_EQUAL);
		this.ruleID.setFilters(filters);

		this.returnCode.setMaxlength(8);

		if (MandateExtension.ALLOW_CONSECUTIVE_BOUNCE) {
			this.space_holdMarkBounceCount.setSclass("mandatory");
		}

		if (MandateExtension.BR_INST_TYPE_MAN) {
			this.spaceInstrumentType.setSclass("mandatory");
		}

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BounceReasonDialog_btnSave"));
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
		doShowNotes(this.bounceReason);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		bounceReasonListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.bounceReason.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param bounceReason
	 * 
	 */
	public void doWriteBeanToComponents(BounceReason aBounceReason) {
		logger.debug(Literal.ENTERING);

		this.bounceCode.setValue(aBounceReason.getBounceCode());
		fillList(reasonType, listReasonType, aBounceReason.getReasonType());
		fillList(category, listCategory, aBounceReason.getCategory());
		this.reason.setValue(aBounceReason.getReason());
		fillComboBox(this.action, String.valueOf(aBounceReason.getAction()), listAction, "");

		this.ruleID.setObject(new Rule(aBounceReason.getRuleID()));
		this.ruleID.setValue(aBounceReason.getRuleCode(), aBounceReason.getRuleCodeDesc());

		this.returnCode.setValue(aBounceReason.getReturnCode());
		this.active.setChecked(aBounceReason.isActive());

		if (aBounceReason.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(aBounceReason.getRecordType())) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		this.recordStatus.setValue(aBounceReason.getRecordStatus());
		fillComboBox(this.instrumentType, aBounceReason.getInstrumentType(), instrumentTypeList, "");
		this.holdMarkBounceCount.setValue(aBounceReason.getHoldMarkBounceCount());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBounceReason
	 */
	public void doWriteComponentsToBean(BounceReason aBounceReason) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aBounceReason.setBounceCode(this.bounceCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strReasonType = null;
			if (this.reasonType.getSelectedItem() != null) {
				strReasonType = this.reasonType.getSelectedItem().getValue().toString();
			}
			if (strReasonType != null && !PennantConstants.List_Select.equals(strReasonType)) {
				aBounceReason.setReasonType(Integer.parseInt(strReasonType));

			} else {
				aBounceReason.setReasonType(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strCategory = null;
			if (this.category.getSelectedItem() != null) {
				strCategory = this.category.getSelectedItem().getValue().toString();
			}
			if (strCategory != null && !PennantConstants.List_Select.equals(strCategory)) {
				aBounceReason.setCategory(Integer.parseInt(strCategory));

			} else {
				aBounceReason.setCategory(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBounceReason.setReason(this.reason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strAction = null;
			if (this.action.getSelectedItem() != null) {
				strAction = this.action.getSelectedItem().getValue().toString();
			}
			if (strAction != null && !PennantConstants.List_Select.equals(strAction)) {
				aBounceReason.setAction(Integer.parseInt(strAction));

			} else {
				aBounceReason.setAction(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.ruleID.getValidatedValue();
			Rule rule = (Rule) this.ruleID.getObject();
			aBounceReason.setRuleID(rule.getRuleId());
			aBounceReason.setRuleCode(rule.getRuleCode());
			aBounceReason.setRuleCodeDesc(rule.getRuleCodeDesc());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String retCode = this.returnCode.getValue();
			aBounceReason.setReturnCode(retCode);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBounceReason.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.holdMarkBounceCount.getValue() != null && this.holdMarkBounceCount.getValue() > 0) {
				aBounceReason.setInstrumentType(this.instrumentType.getSelectedItem().getValue().toString());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (MandateExtension.ALLOW_CONSECUTIVE_BOUNCE
					&& (this.holdMarkBounceCount.getValue() == null || this.holdMarkBounceCount.getValue() <= 0)) {
				throw new WrongValueException(this.holdMarkBounceCount, Labels.getLabel("AMOUNT_NOT_NEGATIVE",
						new String[] { Labels.getLabel("label_BounceReasonDialog_HoldMarkCount.value") }));
			} else {
				aBounceReason.setHoldMarkBounceCount(this.holdMarkBounceCount.getValue());
			}
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param bounceReason The entity that need to be render.
	 */
	public void doShowDialog(BounceReason bounceReason) {
		logger.debug(Literal.LEAVING);

		if (bounceReason.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bounceCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(bounceReason.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.bounceCode.focus();
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

		doWriteBeanToComponents(bounceReason);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.bounceCode.isReadonly()) {
			this.bounceCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_BounceCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.category.isDisabled()) {
			this.category.setConstraint(
					new StaticListValidator(listCategory, Labels.getLabel("label_BounceReasonDialog_Category.value")));
		}
		if (!this.action.isDisabled()) {
			this.action.setConstraint(
					new StaticListValidator(listAction, Labels.getLabel("label_BounceReasonDialog_Action.value")));
		}
		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_Reason.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.ruleID.isReadonly()) {
			this.ruleID.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_RuleID.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.returnCode.isReadonly()) {
			this.returnCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BounceReasonDialog_ReturnCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (this.holdMarkBounceCount.getValue() != null && this.holdMarkBounceCount.getValue() > 0
				&& !this.instrumentType.isDisabled()) {
			this.instrumentType.setConstraint(new StaticListValidator(instrumentTypeList,
					Labels.getLabel("label_BounceReasonDialog_InstrumentType.value")));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.bounceCode.setConstraint("");
		this.reasonType.setConstraint("");
		this.category.setConstraint("");
		this.reason.setConstraint("");
		this.action.setConstraint("");
		this.ruleID.setConstraint("");
		this.returnCode.setConstraint("");
		this.instrumentType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Bounce ID
		// Bounce Code
		// Reason Type
		// Category
		// Reason
		// Action
		// Fee ID
		// Return ID
		// Active

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final BounceReason aBounceReason = new BounceReason();
		BeanUtils.copyProperties(this.bounceReason, aBounceReason);

		doDelete(Labels.getLabel("label_BounceReasonDialog_BounceCode.value") + " : " + aBounceReason.getBounceCode(),
				aBounceReason);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.bounceReason.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.bounceCode);
			readOnlyComponent(isReadOnly("BounceReasonDialog_ReturnCode"), this.returnCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.bounceCode);
			readOnlyComponent(true, this.returnCode);
		}

		readOnlyComponent(isReadOnly("BounceReasonDialog_ReasonType"), this.reasonType);
		readOnlyComponent(isReadOnly("BounceReasonDialog_Category"), this.category);
		readOnlyComponent(isReadOnly("BounceReasonDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("BounceReasonDialog_Action"), this.action);
		readOnlyComponent(isReadOnly("BounceReasonDialog_FeeID"), this.ruleID);
		readOnlyComponent(isReadOnly("BounceReasonDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("BounceReasonDialog_InstrumentType"), this.instrumentType);
		readOnlyComponent(isReadOnly("BounceReasonDialog_HoldMarkBounceCount"), this.holdMarkBounceCount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.bounceReason.isNewRecord()) {
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
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.bounceCode);
		readOnlyComponent(true, this.reasonType);
		readOnlyComponent(true, this.category);
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.action);
		readOnlyComponent(true, this.ruleID);
		readOnlyComponent(true, this.returnCode);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.instrumentType);
		readOnlyComponent(true, this.holdMarkBounceCount);

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
		this.bounceCode.setValue("");
		this.reasonType.setSelectedIndex(0);
		this.category.setSelectedIndex(0);
		this.reason.setValue("");
		this.action.setSelectedIndex(0);
		this.ruleID.setValue("");
		this.ruleID.setDescription("");
		this.returnCode.setValue("");
		this.active.setChecked(false);
		this.instrumentType.setValue("");
		this.holdMarkBounceCount.setValue(0);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final BounceReason aBounceReason = new BounceReason();
		BeanUtils.copyProperties(this.bounceReason, aBounceReason);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aBounceReason);

		isNew = aBounceReason.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBounceReason.getRecordType())) {
				aBounceReason.setVersion(aBounceReason.getVersion() + 1);
				if (isNew) {
					aBounceReason.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBounceReason.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBounceReason.setNewRecord(true);
				}
			}
		} else {
			aBounceReason.setVersion(aBounceReason.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBounceReason, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	protected boolean doProcess(BounceReason aBounceReason, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBounceReason.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBounceReason.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBounceReason.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBounceReason.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBounceReason.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBounceReason);
				}

				if (isNotesMandatory(taskId, aBounceReason)) {
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

			aBounceReason.setTaskId(taskId);
			aBounceReason.setNextTaskId(nextTaskId);
			aBounceReason.setRoleCode(getRole());
			aBounceReason.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBounceReason, tranType);
			String operationRefs = getServiceOperations(taskId, aBounceReason);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBounceReason, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBounceReason, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BounceReason aBounceReason = (BounceReason) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = bounceReasonService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = bounceReasonService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = bounceReasonService.doApprove(auditHeader);

					if (aBounceReason.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = bounceReasonService.doReject(auditHeader);
					if (aBounceReason.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BounceReasonDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BounceReasonDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.bounceReason), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(BounceReason aBounceReason, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBounceReason.getBefImage(), aBounceReason);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBounceReason.getUserDetails(),
				getOverideMap());
	}

	public void setBounceReasonService(BounceReasonService bounceReasonService) {
		this.bounceReasonService = bounceReasonService;
	}

}
