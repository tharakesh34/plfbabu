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
 * * FileName : AccountMappingDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.accountmapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/AccountMapping/accountMappingDialog.zul file.
 * <br>
 */
public class AccountMappingDialogCtrl extends GFCBaseCtrl<AccountMapping> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AccountMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountMappingDialog;
	protected Textbox account;
	protected Textbox hostAccount;
	protected Listbox listBoxAccountMap;
	private AccountMapping accountMapping;
	protected ExtendedCombobox finType;

	private transient AccountMappingListCtrl accountMappingListCtrl;
	private transient AccountMappingService accountMappingService;

	/**
	 * default constructor.<br>
	 */
	public AccountMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountMappingDialog";
	}

	@Override
	protected String getReference() {
		return this.accountMapping.getAccount();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AccountMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			// Set the page level components.
			setPageComponents(window_AccountMappingDialog);

			this.accountMapping = (AccountMapping) arguments.get("accountmapping");
			this.accountMappingListCtrl = (AccountMappingListCtrl) arguments.get("accountmappingListCtrl");

			if (this.accountMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AccountMapping accountMapping = new AccountMapping();
			BeanUtils.copyProperties(this.accountMapping, accountMapping);
			this.accountMapping.setBefImage(accountMapping);

			// Render the page and display the data.
			doLoadWorkFlow(this.accountMapping.isWorkflow(), this.accountMapping.getWorkflowId(),
					this.accountMapping.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.accountMapping);
			this.listBoxAccountMap.setHeight(borderLayoutHeight - 75 + "px");
			setDialog(DialogType.EMBEDDED);
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

		// Finance Type
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param event
	 */
	public void onFulfill$finType(Event event) {
		logger.debug(Literal.ENTERING);

		this.listBoxAccountMap.getItems().clear();
		Object dataObject = finType.getObject();

		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			FinanceType financeType = (FinanceType) dataObject;
			List<String> subHeadRuleList = new ArrayList<>();
			Map<String, Rule> subHeadMap = null;

			List<TransactionEntry> transactionEntries = null;
			if (financeType != null) {
				transactionEntries = this.accountMappingService
						.getTransactionEntriesByFintype(financeType.getFinType());
			}

			if (transactionEntries != null) {
				for (TransactionEntry transactionEntry : transactionEntries) {
					if (!subHeadRuleList.contains(transactionEntry.getAccountSubHeadRule())) {
						subHeadRuleList.add(transactionEntry.getAccountSubHeadRule());
					}
				}

				if (subHeadRuleList == null || subHeadRuleList.isEmpty()) {
					MessageUtil.showMessage("Transaction Entries are not defined for this loan type");
					return;
				}

				subHeadMap = this.accountMappingService.getSubheadRules(subHeadRuleList);
				Rule rule = null;
				Map<String, Object> executeMap = null;
				Listitem item = null;
				Listcell cell = null;

				Label glCode_Label = null;
				Textbox sapGlCode_Textbox = null;
				Space space = null;
				Hbox hbox = null;
				ExtendedCombobox profitCenter = null;
				ExtendedCombobox costCenter = null;
				Textbox accountType = null;

				int count = 0;
				AccountMapping accountMapping = null;
				String hostAccount = null;
				List<String> glCodeList = new ArrayList<>();

				for (TransactionEntry transactionEntry : transactionEntries) {
					if (StringUtils.equals("BANK", transactionEntry.getAccountType())) { // FIXME Hard-Code should be
																							// remove
						continue;
					}
					executeMap = new HashMap<String, Object>();
					rule = subHeadMap.get(transactionEntry.getAccountSubHeadRule());

					executeMap.put("acType", transactionEntry.getAccountType());
					executeMap.put("ae_finType", financeType.getFinType());
					String glCode = (String) RuleExecutionUtil.executeRule(rule == null ? "" : rule.getSQLRule(),
							executeMap, null, RuleReturnType.CALCSTRING);

					if (StringUtils.isBlank(glCode) || glCodeList.contains(glCode)) {
						continue;
					}

					// Fix for remove duplicate glcodes
					glCodeList.add(glCode);

					accountMapping = this.accountMappingService.getAccountMapping(glCode);
					String profitCenterDesc = null;
					String costCenterDesc = null;
					String profitCenterCode = null;
					String costCenterCode = null;
					boolean newRecord = false;
					if (accountMapping == null) {
						hostAccount = "";
						newRecord = true;
						accountMapping = new AccountMapping();
						accountMapping.setNewRecord(true);
					} else {
						hostAccount = accountMapping.getHostAccount();
						profitCenterCode = accountMapping.getProfitCenterCode();
						costCenterCode = accountMapping.getCostCenterCode();
						profitCenterDesc = accountMapping.getProfitCenterDesc();
						costCenterDesc = accountMapping.getCostCenterDesc();
					}

					item = new Listitem();
					item.setId("item_" + count);

					cell = new Listcell();
					glCode_Label = new Label(glCode);
					glCode_Label.setId("glCode_" + count);
					glCode_Label.setParent(cell);
					cell.setParent(item);

					hbox = new Hbox();
					cell = new Listcell();
					hbox.setId("hbox_" + count);
					hbox.setParent(cell);

					space = new Space();
					space.setSpacing("2px");
					space.setId("space_" + count);
					space.setSclass(PennantConstants.mandateSclass);
					space.setParent(hbox);

					sapGlCode_Textbox = new Textbox();
					sapGlCode_Textbox.setParent(cell);
					sapGlCode_Textbox.setId("sapGlCode_" + count);
					sapGlCode_Textbox.setAttribute("newRecord", newRecord);
					sapGlCode_Textbox.setAttribute("befImage", accountMapping);
					sapGlCode_Textbox.setValue(hostAccount);
					sapGlCode_Textbox.setParent(hbox);
					cell.setParent(item);

					cell = new Listcell();
					profitCenter = new ExtendedCombobox();
					profitCenter.setId("profitCenter_" + count);
					profitCenter.setModuleName("ProfitCenter");
					profitCenter.setValueColumn("ProfitCenterCode");
					profitCenter.setDescColumn("ProfitCenterDesc");
					profitCenter.setDisplayStyle(2);
					profitCenter.setValidateColumns(new String[] { "ProfitCenterCode", "ProfitCenterDesc" });
					profitCenter.setMandatoryStyle(true);
					if (!newRecord) {
						profitCenter.setValue(profitCenterCode);
						profitCenter.setDescription(profitCenterDesc);
						profitCenter.setObject(new ProfitCenter(accountMapping.getProfitCenterID()));
					}
					profitCenter.setParent(cell);
					cell.setParent(item);

					cell = new Listcell();
					costCenter = new ExtendedCombobox();
					costCenter.setModuleName("CostCenter");
					costCenter.setId("costCenter_" + count);
					costCenter.setMandatoryStyle(false);
					costCenter.setValueColumn("CostCenterCode");
					costCenter.setDescColumn("CostCenterDesc");
					costCenter.setDisplayStyle(2);
					costCenter.setValidateColumns(new String[] { "CostCenterCode" });
					costCenter.setMandatoryStyle(true);
					if (!newRecord) {
						costCenter.setValue(costCenterCode);
						costCenter.setObject(new CostCenter(accountMapping.getCostCenterID()));
						costCenter.setDescription(costCenterDesc);
					}
					costCenter.setParent(cell);
					cell.setParent(item);

					cell = new Listcell();
					accountType = new Textbox();
					accountType.setReadonly(true);
					accountType.setId("accountType_" + count);
					accountType.setValue(transactionEntry.getAccountType());
					accountType.setParent(cell);
					cell.setParent(item);

					count++;
					item.setParent(listBoxAccountMap);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	private void setAuditPreparation(List<AccountMapping> accountMappingList, String finTypeValue) {
		logger.debug(Literal.ENTERING);

		int count = 0;
		String tranType = "";
		boolean isNew;
		AccountMapping accountMapping = new AccountMapping();

		for (AccountMapping accMapping : accountMappingList) {
			isNew = accMapping.isNewRecord();

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(accMapping.getRecordType())) {
					accMapping.setVersion(accMapping.getVersion() + 1);
					if (isNew) {
						accMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						accMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						accMapping.setNewRecord(true);
					}
				}
			} else {
				accMapping.setVersion(accMapping.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			accMapping.setTranType(tranType);

			if (count == 0) {
				BeanUtils.copyProperties(accMapping, accountMapping);
				accMapping.setBefImage(accMapping);
			} else {
				accMapping.setBefImage(accMapping);
				accountMapping.getAccountMappingList().add(accMapping);
			}

			count++;
		}

		this.setAccountMapping(accountMapping);

		try {
			if (doProcess(accountMapping, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

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
		doShowNotes(this.accountMapping);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		accountMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.accountMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param accountMapping
	 * 
	 */
	public void doWriteBeanToComponents(AccountMapping aAccountMapping) {
		logger.debug(Literal.ENTERING);

		this.finType.setObject(new FinanceType(aAccountMapping.getFinType()));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountMapping
	 */
	public void doWriteComponentsToBean(AccountMapping aAccountMapping) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Account
		try {
			aAccountMapping.setAccount(this.account.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Host Account
		try {
			aAccountMapping.setHostAccount(this.hostAccount.getValue());
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
	 * @param accountMapping The entity that need to be render.
	 */
	public void doShowDialog(AccountMapping accountMapping) {
		logger.debug(Literal.LEAVING);

		if (accountMapping.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.finType.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(accountMapping.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
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

		doWriteBeanToComponents(accountMapping);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.account.setConstraint("");
		this.hostAccount.setConstraint("");

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
		logger.debug(Literal.LEAVING);

		final AccountMapping aAccountMapping = new AccountMapping();
		BeanUtils.copyProperties(this.accountMapping, aAccountMapping);

		doDelete(aAccountMapping.getAccount(), aAccountMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.accountMapping.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.account);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.account);

		}

		readOnlyComponent(isReadOnly("AccountMappingDialog_HostAccount"), this.hostAccount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.accountMapping.isNewRecord()) {
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

		readOnlyComponent(true, this.account);
		readOnlyComponent(true, this.hostAccount);

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
		this.account.setValue("");
		this.hostAccount.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		List<Listitem> items = listBoxAccountMap.getItems();
		int count = 0;
		Textbox sapGlCode_Textbox = null;
		Label glCode_Label = null;
		AccountMapping accountMapping = null;
		// Finance Type
		this.finType.setErrorMessage("");
		this.finType.setConstraint(
				new PTStringValidator(Labels.getLabel("label_AccountMappingDialog_FinType.value"), null, true, true));
		this.finType.getValidatedValue();

		FinanceType financedType = (FinanceType) this.finType.getObject();
		String finTypeValue = financedType.getFinType();
		List<AccountMapping> accountMappingList = new ArrayList<>();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean newRecord;
		ExtendedCombobox profitCenterId;
		ExtendedCombobox costCenterId;
		Textbox accountType;

		for (Listitem listItem : items) {
			accountMapping = new AccountMapping();
			// BeanUtils.copyProperties(this.accountMapping, accountMapping);
			glCode_Label = (Label) listItem.getFellow("glCode_" + count);
			sapGlCode_Textbox = (Textbox) listItem.getFellow("sapGlCode_" + count);
			accountType = (Textbox) listItem.getFellow("accountType_" + count);
			newRecord = (boolean) sapGlCode_Textbox.getAttribute("newRecord");
			accountMapping = (AccountMapping) sapGlCode_Textbox.getAttribute("befImage");
			accountMapping.setWorkflowId(this.accountMapping.getWorkflowId());

			sapGlCode_Textbox.setErrorMessage("");
			accountType.setErrorMessage("");
			if (!newRecord) {
				accountMapping.setBefImage(accountMapping);
			}
			sapGlCode_Textbox.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccountMappingDialog_HostAccount.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
			profitCenterId = (ExtendedCombobox) listItem.getFellow("profitCenter_" + count);
			costCenterId = (ExtendedCombobox) listItem.getFellow("costCenter_" + count);
			profitCenterId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AccountMappingDialog_ProfitCenter.value"), null, true));
			costCenterId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AccountMappingDialog_CostCenter.value"), null, true));
			accountType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AccountMappingDialog_AccountType.value"), null, false));
			// GL Code
			try {
				accountMapping.setAccount(glCode_Label.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// SAP GL Code
			try {
				accountMapping.setHostAccount(sapGlCode_Textbox.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				ProfitCenter profitCenterObj = (ProfitCenter) profitCenterId.getObject();
				accountMapping.setProfitCenterID(profitCenterObj.getId());
				accountMapping.setProfitCenterDesc(profitCenterId.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				CostCenter costCenterObj = (CostCenter) costCenterId.getObject();
				accountMapping.setCostCenterID(costCenterObj.getId());
				accountMapping.setCostCenterDesc(costCenterId.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				accountMapping.setAccountType(accountType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			sapGlCode_Textbox.setConstraint("");
			profitCenterId.setConstraint("");
			costCenterId.setConstraint("");
			accountType.setConstraint("");
			accountMapping.setFinType(finTypeValue);
			accountMapping.setNewRecord(newRecord);
			accountMappingList.add(accountMapping);
			count++;
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		setAuditPreparation(accountMappingList, finTypeValue);

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
	protected boolean doProcess(AccountMapping aAccountMapping, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccountMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccountMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountMapping);
				}

				if (isNotesMandatory(taskId, aAccountMapping)) {
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

			aAccountMapping.setTaskId(taskId);
			aAccountMapping.setNextTaskId(nextTaskId);
			aAccountMapping.setRoleCode(getRole());
			aAccountMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aAccountMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountMapping, tranType);
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
		AccountMapping aAccountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = accountMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = accountMappingService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = accountMappingService.doApprove(auditHeader);

					if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = accountMappingService.doReject(auditHeader);
					if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AccountMappingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AccountMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.accountMapping), true);
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

	private AuditHeader getAuditHeader(AccountMapping aAccountMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountMapping.getBefImage(), aAccountMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAccountMapping.getUserDetails(),
				getOverideMap());
	}

	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}

	public void setAccountMapping(AccountMapping accountMapping) {
		this.accountMapping = accountMapping;
	}
}
