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
 * * FileName : BranchCashLimitDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-01-2018 * *
 * Modified Date : 29-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.cashmanagement.branchcashlimit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cashmanagement.BranchCashDetail;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennant.backend.service.cashmanagement.BranchCashLimitService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CashManagement/BranchCashLimit/branchCashLimitDialog.zul file.
 * <br>
 */
public class BranchCashLimitDialogCtrl extends GFCBaseCtrl<BranchCashLimit> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BranchCashLimitDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BranchCashLimitDialog;
	protected ExtendedCombobox branchCode;
	protected Datebox curLimitSetDate;
	protected CurrencyBox reOrderLimit;
	protected CurrencyBox cashLimit;
	protected CurrencyBox adHocCashLimit;
	protected Textbox remarks;
	protected Decimalbox cashPositon;
	protected Decimalbox adHocCashRequestedTillToday;
	protected Decimalbox cashInTransit;
	protected Decimalbox adHocCashInTransit;
	protected Datebox prevLimitSetDate;
	protected Decimalbox prevLimitAmount;
	private BranchCashLimit branchCashLimit;
	private BranchCashLimit apporvedData;

	private transient BranchCashLimitListCtrl branchCashLimitListCtrl;
	private transient BranchCashLimitService branchCashLimitService;
	private String prevBranchCode = "";

	protected Label label_CashStatus;

	/**
	 * default constructor.<br>
	 */
	public BranchCashLimitDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BranchCashLimitDialog";
	}

	@Override
	protected String getReference() {
		return this.branchCode.getValue();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BranchCashLimitDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BranchCashLimitDialog);
		try {
			// Get the required arguments.
			this.branchCashLimit = (BranchCashLimit) arguments.get("branchCashLimit");
			this.branchCashLimitListCtrl = (BranchCashLimitListCtrl) arguments.get("branchCashLimitListCtrl");
			this.apporvedData = (BranchCashLimit) arguments.get("apporvedData");

			if (this.branchCashLimit == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BranchCashLimit branchCashLimit = new BranchCashLimit();
			BeanUtils.copyProperties(this.branchCashLimit, branchCashLimit);
			this.branchCashLimit.setBefImage(branchCashLimit);

			// Render the page and display the data.
			doLoadWorkFlow(this.branchCashLimit.isWorkflow(), this.branchCashLimit.getWorkflowId(),
					this.branchCashLimit.getNextTaskId());

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
			doShowDialog(this.branchCashLimit);
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

		this.branchCode.setMandatoryStyle(true);
		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });

		this.curLimitSetDate.setFormat(PennantConstants.dateFormat);
		this.curLimitSetDate.setReadonly(true);

		this.reOrderLimit.setProperties(true, PennantConstants.defaultCCYDecPos);
		this.cashLimit.setProperties(true, PennantConstants.defaultCCYDecPos);
		this.adHocCashLimit.setProperties(false, PennantConstants.defaultCCYDecPos);

		this.remarks.setMaxlength(1000);
		this.cashPositon.setReadonly(true);
		this.adHocCashRequestedTillToday.setReadonly(true);
		this.cashInTransit.setReadonly(true);
		this.adHocCashInTransit.setReadonly(true);
		this.prevLimitSetDate.setButtonVisible(false);
		this.prevLimitSetDate.setReadonly(true);
		this.prevLimitAmount.setReadonly(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BranchCashLimitDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BranchCashLimitDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BranchCashLimitDialog_btnSave"));
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
		doShowNotes(this.branchCashLimit);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		branchCashLimitListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.branchCashLimit.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$branchCode(Event event) {
		logger.debug(Literal.ENTERING);
		if (!StringUtils.equals(prevBranchCode, this.branchCode.getValue())) {
			prevBranchCode = this.branchCode.getValue();
			fillListInfo(branchCashLimitService.getBranchCashDetail(this.branchCode.getValue()));
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reOrderLimit(Event event) {
		logger.debug(Literal.ENTERING);
		refreshData();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$cashLimit(Event event) {
		logger.debug(Literal.ENTERING);
		refreshData();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$adHocCashLimit(Event event) {
		logger.debug(Literal.ENTERING);
		refreshData();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param branchCashLimit
	 * 
	 */
	public void doWriteBeanToComponents(BranchCashLimit aBranchCashLimit) {
		logger.debug(Literal.ENTERING);

		this.branchCode.setValue(aBranchCashLimit.getBranchCode());
		prevBranchCode = this.branchCode.getValue();

		if (aBranchCashLimit.getCurLimitSetDate() == null) {
			/*
			 * Date maxReqLimitSetDate = DateUtility.getAppDate(); if
			 * (DateUtility.compare(DateUtility.getMonthEndDate(maxReqLimitSetDate), maxReqLimitSetDate) == 0 &&
			 * DateUtility.compare(DateUtility.getSysDate(), maxReqLimitSetDate) > 0) { maxReqLimitSetDate =
			 * DateUtility.getSysDate(); } this.curLimitSetDate.setValue(maxReqLimitSetDate);
			 */ this.curLimitSetDate.setValue(DateUtility.getDerivedAppDate());
		} else {
			this.curLimitSetDate.setValue(aBranchCashLimit.getCurLimitSetDate());
		}

		this.remarks.setValue(aBranchCashLimit.getRemarks());
		this.reOrderLimit
				.setValue(CurrencyUtil.parse(aBranchCashLimit.getReOrderLimit(), PennantConstants.defaultCCYDecPos));
		this.cashLimit.setValue(CurrencyUtil.parse(aBranchCashLimit.getCashLimit(), PennantConstants.defaultCCYDecPos));
		this.adHocCashLimit
				.setValue(CurrencyUtil.parse(aBranchCashLimit.getAdHocCashLimit(), PennantConstants.defaultCCYDecPos));

		if (aBranchCashLimit.getPreviousDate() == null) {
			this.prevLimitSetDate.setValue(curLimitSetDate.getValue());
		} else {
			this.prevLimitSetDate.setValue(aBranchCashLimit.getPreviousDate());
		}
		this.prevLimitAmount
				.setValue(CurrencyUtil.parse(aBranchCashLimit.getPreviousAmount(), PennantConstants.defaultCCYDecPos));

		this.recordStatus.setValue(aBranchCashLimit.getRecordStatus());
		fillListInfo(aBranchCashLimit.getBranchCashDetail());
		if (aBranchCashLimit.isNewRecord()) {
			this.branchCode.setDescription("");
		} else {
			this.branchCode.setDescription(aBranchCashLimit.getBranchCodeName());
		}

		logger.debug(Literal.LEAVING);
	}

	private void fillListInfo(BranchCashDetail cashDetail) {
		if (cashDetail == null) {
			cashDetail = new BranchCashDetail();
			cashDetail.setAdhocInitiationAmount(BigDecimal.ZERO);
			cashDetail.setAdhocProcessingAmount(BigDecimal.ZERO);
			cashDetail.setAdhocTransitAmount(BigDecimal.ZERO);
			cashDetail.setAutoProcessingAmount(BigDecimal.ZERO);
			cashDetail.setAutoTransitAmount(BigDecimal.ZERO);
		}

		this.cashPositon.setValue(
				PennantApplicationUtil.formateAmount(cashDetail.getBranchCash(), PennantConstants.defaultCCYDecPos));

		refreshData();

		this.adHocCashRequestedTillToday.setValue(PennantApplicationUtil.formateAmount(
				cashDetail.getAdhocInitiationAmount().add(cashDetail.getAdhocProcessingAmount()),
				PennantConstants.defaultCCYDecPos));
		this.cashInTransit.setValue(PennantApplicationUtil.formateAmount(cashDetail.getAutoTransitAmount(),
				PennantConstants.defaultCCYDecPos));
		this.adHocCashInTransit.setValue(PennantApplicationUtil.formateAmount(cashDetail.getAdhocTransitAmount(),
				PennantConstants.defaultCCYDecPos));
	}

	private void refreshData() {

		BigDecimal tempReOrder = BigDecimal.ZERO;
		BigDecimal tempadHoc = BigDecimal.ZERO;
		BigDecimal tempCashLimit = BigDecimal.ZERO;

		if (!StringUtils.isBlank(this.reOrderLimit.getCcyTextBox().getValue())) {
			tempReOrder = CurrencyUtil.unFormat(this.reOrderLimit.getActualValue(), PennantConstants.defaultCCYDecPos);
		}

		if (!StringUtils.isBlank(this.adHocCashLimit.getCcyTextBox().getValue())) {
			tempadHoc = CurrencyUtil.unFormat(this.adHocCashLimit.getActualValue(), PennantConstants.defaultCCYDecPos);
		}

		if (!StringUtils.isBlank(this.cashLimit.getCcyTextBox().getValue())) {
			tempCashLimit = CurrencyUtil.unFormat(this.cashLimit.getActualValue(), PennantConstants.defaultCCYDecPos);
		}

		this.label_CashStatus.setValue(
				PennantApplicationUtil.getCashPosition(tempReOrder, branchCashLimit.getBranchCash(), tempCashLimit));

		if (apporvedData != null) {

			if (apporvedData.getReOrderLimit().compareTo(tempReOrder) != 0
					|| apporvedData.getAdHocCashLimit().compareTo(tempadHoc) != 0
					|| apporvedData.getCashLimit().compareTo(tempCashLimit) != 0) {
				if (StringUtils.equals(apporvedData.getRemarks(), this.remarks.getValue())) {
					this.remarks.setValue("");
				}
			} else {
				if (StringUtils.isBlank(this.remarks.getValue())) {
					this.remarks.setValue(apporvedData.getRemarks());
				}

			}
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBranchCashLimit
	 */
	public void doWriteComponentsToBean(BranchCashLimit aBranchCashLimit) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Branch Code
		try {
			aBranchCashLimit.setBranchCode(String.valueOf(this.branchCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Current Limit Set Date
		try {
			aBranchCashLimit.setCurLimitSetDate(this.curLimitSetDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Re Order Limit
		BigDecimal tempOrderLimit = BigDecimal.ZERO;
		try {
			tempOrderLimit = CurrencyUtil.unFormat(this.reOrderLimit.getValidateValue(),
					PennantConstants.defaultCCYDecPos);
			aBranchCashLimit.setReOrderLimit(tempOrderLimit);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cash Limit
		BigDecimal tempCashLimit = BigDecimal.ZERO;
		try {

			tempCashLimit = CurrencyUtil.unFormat(this.cashLimit.getValidateValue(), PennantConstants.defaultCCYDecPos);
			aBranchCashLimit.setCashLimit(tempCashLimit);

			if (tempCashLimit.compareTo(tempOrderLimit) <= 0) {
				throw new WrongValueException(this.cashLimit,
						Labels.getLabel("NUMBER_MINVALUE",
								new String[] { Labels.getLabel("label_BranchCashLimitDialog_CashLimit.value"),
										Labels.getLabel("label_BranchCashLimitDialog_ReOrderLimit.value") }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Ad Hoc Cash Limit
		try {
			aBranchCashLimit.setAdHocCashLimit(
					CurrencyUtil.unFormat(this.adHocCashLimit.getValidateValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aBranchCashLimit.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (StringUtils.trimToEmpty(aBranchCashLimit.getRecordType()).equals("")
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, aBranchCashLimit.getRecordType())) {
			if (apporvedData != null) {
				aBranchCashLimit.setPreviousDate(apporvedData.getCurLimitSetDate());
				aBranchCashLimit.setPreviousAmount(apporvedData.getCashLimit());
				aBranchCashLimit.setCurLimitSetDate(DateUtility.getDerivedAppDate());
			} else {
				aBranchCashLimit.setPreviousDate(aBranchCashLimit.getCurLimitSetDate());
				aBranchCashLimit.setPreviousAmount(aBranchCashLimit.getCashLimit());
			}
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
	 * @param branchCashLimit The entity that need to be render.
	 */
	public void doShowDialog(BranchCashLimit branchCashLimit) {
		logger.debug(Literal.LEAVING);

		if (branchCashLimit.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.branchCode.focus();
		} else {
			this.branchCode.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(branchCashLimit.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.curLimitSetDate.focus();
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

		doWriteBeanToComponents(branchCashLimit);
		this.btnDelete.setVisible(false); // as per requirement delete will not applicable for this module

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.branchCode.isReadonly()) {
			this.branchCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchCashLimitDialog_BranchCode.value"), null, true));
		}
		if (!this.curLimitSetDate.isReadonly()) {
			this.curLimitSetDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_BranchCashLimitDialog_CurLimitSetDate.value"), true));
		}

		if (!this.reOrderLimit.isReadonly()) {
			this.reOrderLimit.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BranchCashLimitDialog_ReOrderLimit.value"),
							PennantConstants.defaultCCYDecPos, true, false));
		}

		if (!this.cashLimit.isReadonly()) {
			this.cashLimit.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BranchCashLimitDialog_CashLimit.value"),
							PennantConstants.defaultCCYDecPos, true, false));
		}

		if (!this.adHocCashLimit.isReadonly()) {
			this.adHocCashLimit.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BranchCashLimitDialog_AdHocCashLimit.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}

		if (!this.remarks.isReadonly()) {
			this.remarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchCashLimitDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.branchCode.setConstraint("");
		this.curLimitSetDate.setConstraint("");
		this.reOrderLimit.setConstraint("");
		this.cashLimit.setConstraint("");
		this.adHocCashLimit.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

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
		logger.debug(Literal.ENTERING);

		final BranchCashLimit aBranchCashLimit = new BranchCashLimit();
		BeanUtils.copyProperties(this.branchCashLimit, aBranchCashLimit);

		doDelete(aBranchCashLimit.getBranchCode(), aBranchCashLimit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.branchCashLimit.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.branchCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.branchCode);

		}

		readOnlyComponent(true, this.curLimitSetDate);
		readOnlyComponent(isReadOnly("BranchCashLimitDialog_ReOrderLimit"), this.reOrderLimit);
		readOnlyComponent(isReadOnly("BranchCashLimitDialog_CashLimit"), this.cashLimit);
		readOnlyComponent(isReadOnly("BranchCashLimitDialog_AdHocCashLimit"), this.adHocCashLimit);
		readOnlyComponent(isReadOnly("BranchCashLimitDialog_Remarks"), this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.branchCashLimit.isNewRecord()) {
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

		readOnlyComponent(true, this.branchCode);
		readOnlyComponent(true, this.curLimitSetDate);
		readOnlyComponent(true, this.reOrderLimit);
		readOnlyComponent(true, this.cashLimit);
		readOnlyComponent(true, this.adHocCashLimit);
		readOnlyComponent(true, this.remarks);

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
		this.branchCode.setValue("");
		this.branchCode.setDescription("");
		this.curLimitSetDate.setText("");
		this.reOrderLimit.setValue("");
		this.cashLimit.setValue("");
		this.adHocCashLimit.setValue("");
		this.remarks.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final BranchCashLimit aBranchCashLimit = new BranchCashLimit();
		BeanUtils.copyProperties(this.branchCashLimit, aBranchCashLimit);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aBranchCashLimit);

		isNew = aBranchCashLimit.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBranchCashLimit.getRecordType())) {
				aBranchCashLimit.setVersion(aBranchCashLimit.getVersion() + 1);
				if (isNew) {
					aBranchCashLimit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBranchCashLimit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBranchCashLimit.setNewRecord(true);
				}
			}
		} else {
			aBranchCashLimit.setVersion(aBranchCashLimit.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBranchCashLimit, tranType)) {
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
	protected boolean doProcess(BranchCashLimit aBranchCashLimit, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBranchCashLimit.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBranchCashLimit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBranchCashLimit.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBranchCashLimit.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBranchCashLimit.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBranchCashLimit);
				}

				if (isNotesMandatory(taskId, aBranchCashLimit)) {
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

			aBranchCashLimit.setTaskId(taskId);
			aBranchCashLimit.setNextTaskId(nextTaskId);
			aBranchCashLimit.setRoleCode(getRole());
			aBranchCashLimit.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBranchCashLimit, tranType);
			String operationRefs = getServiceOperations(taskId, aBranchCashLimit);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBranchCashLimit, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBranchCashLimit, tranType);
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BranchCashLimit aBranchCashLimit = (BranchCashLimit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = branchCashLimitService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = branchCashLimitService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = branchCashLimitService.doApprove(auditHeader);

					if (aBranchCashLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = branchCashLimitService.doReject(auditHeader);
					if (aBranchCashLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BranchCashLimitDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BranchCashLimitDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.branchCashLimit), true);
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

	private AuditHeader getAuditHeader(BranchCashLimit aBranchCashLimit, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBranchCashLimit.getBefImage(), aBranchCashLimit);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBranchCashLimit.getUserDetails(),
				getOverideMap());
	}

	public void setBranchCashLimitService(BranchCashLimitService branchCashLimitService) {
		this.branchCashLimitService = branchCashLimitService;
	}

}
