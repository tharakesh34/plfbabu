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
 * * FileName : BankBranchDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-10-2016 * * Modified
 * Date : 17-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.bmtmasters.bankbranch;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/BMTMasters/BankBranch/bankBranchDialog.zul file. <br>
 * ************************************************************<br>
 */
public class BankBranchDialogCtrl extends GFCBaseCtrl<BankBranch> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BankBranchDialogCtrl.class);

	protected Window window_BankBranchDialog;

	protected ExtendedCombobox bankCode;
	protected ExtendedCombobox parentBranch;
	protected Textbox branchCode;
	protected Textbox branchDesc;
	protected ExtendedCombobox city;
	protected Textbox mICR;
	protected Textbox iFSC;
	protected Textbox addOfBranch;
	protected Checkbox cheque;
	protected Checkbox dd;
	protected Checkbox ecs;
	protected Checkbox nach;
	protected Checkbox dda;
	protected Checkbox active;
	protected Checkbox eMandate;
	protected Textbox allowedSources;
	protected Row row_eMandate;
	protected Button btnMultiSource;
	protected Groupbox gb_instrumenttypes;

	private boolean enqModule = false;
	// not auto wired vars
	private BankBranch bankBranch; // overhanded per param
	private transient BankBranchListCtrl bankBranchListCtrl; // overhanded per param

	protected Button btnSearchBankCode;
	protected Button btnSearchCity;
	protected Space space_MICR;

	private transient BankBranchService bankBranchService;

	public BankBranchDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BankBranchDialog";
	}

	public void onCreate$window_BankBranchDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_BankBranchDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			doCheckRights();

			if (arguments.containsKey("bankBranch")) {
				this.bankBranch = (BankBranch) arguments.get("bankBranch");
				BankBranch befImage = new BankBranch();
				BeanUtils.copyProperties(this.bankBranch, befImage);
				this.bankBranch.setBefImage(befImage);

				setBankBranch(this.bankBranch);
			} else {
				setBankBranch(null);
			}

			doLoadWorkFlow(this.bankBranch.isWorkflow(), this.bankBranch.getWorkflowId(),
					this.bankBranch.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BankBranchDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			if (arguments.containsKey("bankBranchListCtrl")) {
				setBankBranchListCtrl((BankBranchListCtrl) arguments.get("bankBranchListCtrl"));
			} else {
				setBankBranchListCtrl(null);
			}

			doSetFieldProperties();
			doShowDialog(getBankBranch());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BankBranchDialog.onClose();
		}
		logger.debug(Literal.ENTERING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.bankCode.setMaxlength(8);
		this.parentBranch.setMaxlength(12);
		this.branchCode.setMaxlength(PennantConstants.branchCode_maxValue);
		this.branchDesc.setMaxlength(200);
		this.city.setMaxlength(8);
		this.mICR.setMaxlength(20);
		this.iFSC.setMaxlength(20);
		this.addOfBranch.setMaxlength(100);

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode", "BankName" });

		this.parentBranch.setModuleName("BankBranch");
		this.parentBranch.setValueColumn("BranchCode");
		this.parentBranch.setDescColumn("BranchDesc");
		this.parentBranch.setDisplayStyle(2);
		this.parentBranch.setValidateColumns(new String[] { "BranchCode" });

		this.city.setModuleName("City");

		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setDisplayStyle(2);
		this.city.setValidateColumns(new String[] { "PCCity", "PCCityName" });
		this.row_eMandate.setVisible(true);

		this.allowedSources.setValue("Code");
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BankBranchDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BankBranchDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BankBranchDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BankBranchDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnMultiSource(Event event) {
		logger.debug(Literal.ENTERING);
		Clients.clearWrongValue(this.btnMultiSource);

		Object dataObject = MultiSelectionSearchListBox.show(this.window, "Mandate_Sources",
				this.allowedSources.getValue(), null);
		if (dataObject instanceof String) {
			this.allowedSources.setValue(dataObject.toString());
		} else {
			String details = (String) dataObject;
			if (details != null) {
				this.allowedSources.setValue(details);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_BankBranchDialog);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		this.btnEdit.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.bankBranch.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(BankBranch aBankBranch) {
		logger.debug(Literal.ENTERING);
		this.bankCode.setValue(aBankBranch.getBankCode());
		this.bankCode.setDescription(aBankBranch.getBankName());
		this.branchCode.setValue(aBankBranch.getBranchCode());
		this.branchDesc.setValue(aBankBranch.getBranchDesc());
		this.parentBranch.setValue(aBankBranch.getParentBranch());
		this.parentBranch.setDescription(aBankBranch.getParentBranchDesc());
		this.city.setValue(aBankBranch.getCity());
		this.city.setDescription(aBankBranch.getPCCityName());
		this.mICR.setValue(aBankBranch.getMICR());
		this.iFSC.setValue(aBankBranch.getIFSC());
		this.addOfBranch.setValue(aBankBranch.getAddOfBranch());
		this.nach.setChecked(aBankBranch.isNach());
		this.ecs.setChecked(aBankBranch.isEcs());
		this.dd.setChecked(aBankBranch.isDd());
		this.cheque.setChecked(aBankBranch.isCheque());
		this.dda.setChecked(aBankBranch.isDda());
		this.eMandate.setChecked(aBankBranch.isEmandate());
		this.allowedSources.setValue(aBankBranch.getAllowedSources());
		this.active.setChecked(aBankBranch.isActive());
		this.recordStatus.setValue(aBankBranch.getRecordStatus());

		setMICRValidation(aBankBranch.isAllowMultipleIFSC());
		doShowEMandateSources(this.eMandate);

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$eMandate(Event event) {
		doShowEMandateSources(this.eMandate);
	}

	private void doShowEMandateSources(Checkbox event) {
		logger.debug(Literal.ENTERING);
		if (this.eMandate.isChecked()) {
			this.allowedSources.setReadonly(true);
			this.btnMultiSource.setVisible(true);
		} else {
			this.allowedSources.setReadonly(true);
			this.btnMultiSource.setVisible(false);
			this.allowedSources.setValue("");
		}
		logger.debug(Literal.LEAVING);

	}

	public void doWriteComponentsToBean(BankBranch aBankBranch) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Bank Code
		try {
			aBankBranch.setBankCode(this.bankCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setParentBranch(this.parentBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setParentBranchDesc(this.parentBranch.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Branch Code
		try {
			aBankBranch.setBranchCode(this.branchCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Branch Desc
		try {
			aBankBranch.setBranchDesc(this.branchDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// City
		try {
			aBankBranch.setCity(this.city.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// MICR Code
		try {
			aBankBranch.setMICR(this.mICR.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// IFSC Code
		try {
			aBankBranch.setIFSC(this.iFSC.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// AddOfBranch
		try {
			aBankBranch.setAddOfBranch(this.addOfBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setNach(this.nach.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setEcs(this.ecs.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setDd(this.dd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setDda(this.dda.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setCheque(this.cheque.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setEmandate(this.eMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.btnMultiSource.isVisible() && StringUtils.isBlank(this.allowedSources.getValue())) {
				throw new WrongValueException(this.btnMultiSource,
						Labels.getLabel("label_BankBranchDialog_AllowedSources.value") + " is Mandatory");
			}
			aBankBranch.setAllowedSources(this.allowedSources.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBankBranch.setActive(this.active.isChecked());
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

		aBankBranch.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(BankBranch aBankBranch) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		if (aBankBranch == null) {
			aBankBranch = getBankBranchService().getNewBankBranch();

			setBankBranch(aBankBranch);
		} else {
			setBankBranch(aBankBranch);
		}
		if (aBankBranch.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.branchCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.branchDesc.focus();
				if (StringUtils.isNotBlank(aBankBranch.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				this.btnEdit.setVisible(true);
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			doWriteBeanToComponents(aBankBranch);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BankBranchDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		// Bank Code
		if (!this.bankCode.isReadonly()) {
			this.bankCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_BankCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Branch Code
		if (!this.branchCode.isReadonly()) {
			this.branchCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_BranchCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Branch Desc
		if (!this.branchDesc.isReadonly()) {
			this.branchDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_BranchDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		// City
		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BankBranchDialog_City.value"), null, false, true));
		}
		// IFSC Code
		if (!this.iFSC.isReadonly()) {
			this.iFSC.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_IFSC.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		// addOfBranch
		if (!this.addOfBranch.isReadonly()) {
			this.addOfBranch
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_AddOfBranch.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		// MICR
		Object dataObject = bankCode.getObject();
		BankDetail details = (BankDetail) dataObject;

		if (details.isAllowMultipleIFSC() == true) {
			setMICRValidation(details.isAllowMultipleIFSC());
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidation(false);
		this.bankCode.setConstraint("");
		this.branchCode.setConstraint("");
		this.branchDesc.setConstraint("");
		this.city.setConstraint("");
		this.mICR.setConstraint("");
		this.iFSC.setConstraint("");
		this.addOfBranch.setConstraint("");
		this.parentBranch.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.bankCode.setErrorMessage("");
		this.branchCode.setErrorMessage("");
		this.branchDesc.setErrorMessage("");
		this.city.setErrorMessage("");
		this.mICR.setErrorMessage("");
		this.iFSC.setErrorMessage("");
		this.addOfBranch.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BankBranch aBankBranch = new BankBranch();
		BeanUtils.copyProperties(getBankBranch(), aBankBranch);

		doDelete(Labels.getLabel("label_BankBranchDialog_BranchCode.value") + " : " + aBankBranch.getBranchCode(),
				aBankBranch);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);
		if (getBankBranch().isNewRecord()) {
			this.branchCode.setReadonly(false);
			this.bankCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.parentBranch.setReadonly(false);
		} else {
			this.branchCode.setReadonly(true);
			this.bankCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("BankBranchDialog_BranchDesc"), this.branchDesc);
		readOnlyComponent(isReadOnly("BankBranchDialog_City"), this.city);
		readOnlyComponent(isReadOnly("BankBranchDialog_IFSC"), this.iFSC);
		readOnlyComponent(isReadOnly("BankBranchDialog_MICR"), this.mICR);
		readOnlyComponent(isReadOnly("BankBranchDialog_AddOfBranch"), this.addOfBranch);
		readOnlyComponent(isReadOnly("BankBranchDialog_ECS"), this.ecs);
		readOnlyComponent(isReadOnly("BankBranchDialog_DDA"), this.dda);
		readOnlyComponent(isReadOnly("BankBranchDialog_DD"), this.dd);
		readOnlyComponent(isReadOnly("BankBranchDialog_NACH"), this.nach);
		readOnlyComponent(isReadOnly("BankBranchDialog_Cheque"), this.cheque);
		readOnlyComponent(isReadOnly("BankBranchDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("BankBranchDialog_Emandate"), this.eMandate);
		readOnlyComponent(isReadOnly("BankBranchDialog_AllowedSources"), this.allowedSources);
		this.btnMultiSource.setDisabled(isReadOnly("button_BankBranchDialog_btnMultiSource"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.bankBranch.isNewRecord()) {
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

	public void doReadOnly() {
		logger.debug("Entering");
		this.branchCode.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.branchDesc.setReadonly(true);
		this.city.setReadonly(true);
		this.mICR.setReadonly(true);
		this.iFSC.setReadonly(true);
		this.addOfBranch.setReadonly(true);
		this.dd.setDisabled(true);
		this.nach.setDisabled(true);
		this.dda.setDisabled(true);
		this.cheque.setDisabled(true);
		this.ecs.setDisabled(true);
		this.active.setDisabled(true);
		this.parentBranch.setReadonly(true);
		this.eMandate.setDisabled(true);
		this.allowedSources.setReadonly(true);

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

	public void doClear() {
		logger.debug("Entering");

		this.bankCode.setValue("");
		this.branchCode.setValue("");
		this.branchDesc.setValue("");
		this.city.setValue("");
		this.mICR.setValue("");
		this.iFSC.setValue("");
		this.addOfBranch.setValue("");
		this.nach.setChecked(false);
		this.ecs.setChecked(false);
		this.dd.setChecked(false);
		this.dda.setChecked(false);
		this.cheque.setChecked(false);
		this.active.setChecked(false);
		this.parentBranch.setValue("");
		this.eMandate.setChecked(false);
		this.allowedSources.setValue("");
		logger.debug("Leaving");
	}

	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final BankBranch aBankBranch = new BankBranch();
		BeanUtils.copyProperties(getBankBranch(), aBankBranch);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aBankBranch.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBankBranch.getNextTaskId(), aBankBranch);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aBankBranch.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the BankBranch object with the components data
			doWriteComponentsToBean(aBankBranch);
		}

		isNew = aBankBranch.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBankBranch.getRecordType()).equals("")) {
				aBankBranch.setVersion(aBankBranch.getVersion() + 1);
				if (isNew) {
					aBankBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBankBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBankBranch.setNewRecord(true);
				}
			}
		} else {
			aBankBranch.setVersion(aBankBranch.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {

			if (doProcess(aBankBranch, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(BankBranch aBankBranch, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBankBranch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBankBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBankBranch.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBankBranch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBankBranch.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBankBranch);
				}

				if (isNotesMandatory(taskId, aBankBranch)) {
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

			aBankBranch.setTaskId(taskId);
			aBankBranch.setNextTaskId(nextTaskId);
			aBankBranch.setRoleCode(getRole());
			aBankBranch.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBankBranch, tranType);
			String operationRefs = getServiceOperations(taskId, aBankBranch);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBankBranch, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBankBranch, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;

	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		BankBranch aBankBranch = (BankBranch) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getBankBranchService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getBankBranchService().saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getBankBranchService().doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aBankBranch.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getBankBranchService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aBankBranch.getRecordType())) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BankBranchDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BankBranchDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.bankBranch), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = bankCode.getObject();
		if (dataObject instanceof String) {
			this.bankCode.setValue("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setValue(details.getBankCode());
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("bankCode", details.getBankCode(), Filter.OP_EQUAL);
				parentBranch.setFilters(filters);

				/*
				 * this.bankBranch.setAllowMultipleIFSC(details.isAllowMultipleIFSC());
				 * setMICRValidation(details.isAllowMultipleIFSC());
				 * 
				 */
				if (details.isAllowMultipleIFSC()) {
					this.space_MICR.setSclass("mandatory");
				}

				return;
			}
		}

		setMICRValidation(false);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$parentBranch(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = parentBranch.getObject();

		if (dataObject instanceof String) {
			this.parentBranch.setValue("");
			this.parentBranch.setDescription("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.parentBranch.setValue(details.getBranchCode());
				this.parentBranch.setDescription(details.getBranchDesc());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setMICRValidation(boolean allowMultipleIFSC) {
		if (allowMultipleIFSC) {
			this.space_MICR.setSclass("mandatory");
		} else {
			this.space_MICR.setSclass("");
		}
		if (!this.mICR.isReadonly()) {
			this.mICR.setConstraint(new PTStringValidator(Labels.getLabel("label_BankBranchDialog_MICR.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, allowMultipleIFSC));
		}
	}

	private AuditHeader getAuditHeader(BankBranch aBankBranch, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBankBranch.getBefImage(), aBankBranch);
		return new AuditHeader(String.valueOf(aBankBranch.getBankBranchID()), null, null, null, auditDetail,
				aBankBranch.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.bankBranch);
	}

	protected void refreshList() {
		getBankBranchListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(getBankBranch().getBankBranchID());
	}

	public BankBranch getBankBranch() {
		return this.bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public BankBranchService getBankBranchService() {
		return this.bankBranchService;
	}

	public void setBankBranchListCtrl(BankBranchListCtrl bankBranchListCtrl) {
		this.bankBranchListCtrl = bankBranchListCtrl;
	}

	public BankBranchListCtrl getBankBranchListCtrl() {
		return this.bankBranchListCtrl;
	}

}
