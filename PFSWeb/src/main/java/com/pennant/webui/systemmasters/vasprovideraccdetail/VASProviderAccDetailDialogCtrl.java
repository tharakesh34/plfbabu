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
 * * FileName : VASProviderAccDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-09-2018 *
 * * Modified Date : 24-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.vasprovideraccdetail;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.systemmasters.VASProviderAccDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/VASProviderAccDetail/vASProviderAccDetailDialog.zul
 * file. <br>
 */
public class VASProviderAccDetailDialogCtrl extends GFCBaseCtrl<VASProviderAccDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(VASProviderAccDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VASProviderAccDetailDialog;
	protected ExtendedCombobox providerId;
	protected ExtendedCombobox entityCode;
	protected Space space_PaymentMode;
	protected Combobox paymentMode;
	protected ExtendedCombobox bankBranchID;
	protected Textbox bankName;
	protected Space space_AccountNumber;
	protected Uppercasebox accountNumber;
	protected Textbox ifscCode;
	protected Textbox micrCode;
	protected Checkbox receivableAdjustment;
	protected CurrencyBox reconciliationAmount;
	protected Checkbox active;
	protected Label bankNameDesc;
	protected ExtendedCombobox partnerBankId;
	private VASProviderAccDetail vASProviderAccDetail; // overhanded per param

	private transient VASProviderAccDetailListCtrl vASProviderAccDetailListCtrl; // overhanded
																					// per
																					// param
	private transient VASProviderAccDetailService vASProviderAccDetailService;

	private List<ValueLabel> listPaymentMode = PennantStaticListUtil.getPaymentType();
	private boolean isFromLoan = false;

	/**
	 * default constructor.<br>
	 */
	public VASProviderAccDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VASProviderAccDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.vASProviderAccDetail.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_VASProviderAccDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_VASProviderAccDetailDialog);

		try {
			// Get the required arguments.
			this.vASProviderAccDetail = (VASProviderAccDetail) arguments.get("vASProviderAccDetail");
			this.vASProviderAccDetailListCtrl = (VASProviderAccDetailListCtrl) arguments
					.get("vASProviderAccDetailListCtrl");

			if (arguments.get("isDisbInst") instanceof Boolean) {
				this.isFromLoan = (boolean) arguments.get("isDisbInst");
			}
			if (this.vASProviderAccDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			VASProviderAccDetail vASProviderAccDetail = new VASProviderAccDetail();
			BeanUtils.copyProperties(this.vASProviderAccDetail, vASProviderAccDetail);
			this.vASProviderAccDetail.setBefImage(vASProviderAccDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.vASProviderAccDetail.isWorkflow(), this.vASProviderAccDetail.getWorkflowId(),
					this.vASProviderAccDetail.getNextTaskId());

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
			doShowDialog(this.vASProviderAccDetail);
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

		/*
		 * this.providerId.setMandatoryStyle(true); this.providerId.setModuleName("VehicleDealer");
		 * this.providerId.setValueColumn("DealerId"); this.providerId.setDescColumn("DealerName");
		 * this.providerId.setValueType(DataType.LONG); this.providerId.setValidateColumns(new String[] { "DealerId" });
		 */
		// Filter to display only VAS Manufacturers
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("DealerType", "VASM", Filter.OP_EQUAL);
		this.providerId.setFilters(filter);

		this.providerId.setProperties("VASManufacturer", "DealerName", "DealerCity", true, 8);

		this.accountNumber.setMaxlength(20);
		this.ifscCode.setMaxlength(20);
		this.micrCode.setMaxlength(20);

		this.reconciliationAmount.setProperties(true, getCcyFormat());

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setValueColumn("BankBranchID");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setValueType(DataType.INT);
		this.bankBranchID.setValueType(DataType.BIGDECIMAL);
		this.bankBranchID.setValueType(DataType.LONG);
		this.bankBranchID.setValidateColumns(new String[] { "BankBranchID" });

		this.partnerBankId.setModuleName("PartnerBank");
		this.partnerBankId.setValueColumn("PartnerBankCode");
		this.partnerBankId.setDescColumn("PartnerBankName");
		this.partnerBankId.setValidateColumns(new String[] { "PartnerBankCode", "PartnerBankName" });
		this.partnerBankId.setMandatoryStyle(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VASProviderAccDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VASProviderAccDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VASProviderAccDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VASProviderAccDetailDialog_btnSave"));
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
		doShowNotes(this.vASProviderAccDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		vASProviderAccDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.vASProviderAccDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$providerId(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = providerId.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.providerId.setValue("");
			this.providerId.setDescription("");
			this.providerId.setAttribute("providerId", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.providerId.setValue(String.valueOf(details.getId()));
				this.providerId.setDescription(details.getDealerName());
				this.providerId.setAttribute("providerId", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = this.bankBranchID.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bankBranchID.setAttribute("bankBranchID", null);
			this.ifscCode.setValue("");
			this.micrCode.setValue("");
			this.bankNameDesc.setValue("");
			this.bankName.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			this.bankBranchID.setAttribute("bankBranchID", details.getId());
			this.ifscCode.setValue(details.getIFSC());
			this.micrCode.setValue(details.getMICR());
			this.bankName.setValue(details.getBankCode());
			this.bankNameDesc.setValue(details.getBankName());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param vASProviderAccDetail
	 * 
	 */
	public void doWriteBeanToComponents(VASProviderAccDetail aVASProviderAccDetail) {
		logger.debug(Literal.ENTERING);

		this.providerId.setValue(String
				.valueOf(aVASProviderAccDetail.getProviderId() == 0 ? "" : aVASProviderAccDetail.getProviderId()));
		this.providerId.setAttribute("providerId", aVASProviderAccDetail.getProviderId());
		this.entityCode.setValue(aVASProviderAccDetail.getEntityCode());
		this.bankName.setValue(aVASProviderAccDetail.getBankCode());
		List<String> excludeFiles = new ArrayList<String>();
		excludeFiles.add(DisbursementConstants.PAYMENT_TYPE_CASH);
		excludeFiles.add(DisbursementConstants.PAYMENT_TYPE_ESCROW);
		fillComboBox(this.paymentMode, aVASProviderAccDetail.getPaymentMode(), listPaymentMode, excludeFiles);

		this.accountNumber.setValue(aVASProviderAccDetail.getAccountNumber());
		this.receivableAdjustment.setChecked(aVASProviderAccDetail.isReceivableAdjustment());
		this.reconciliationAmount.setValue(
				PennantApplicationUtil.formateAmount(aVASProviderAccDetail.getReconciliationAmount(), getCcyFormat()));
		this.active.setChecked(aVASProviderAccDetail.isActive());
		this.ifscCode.setValue(aVASProviderAccDetail.getIfscCode());
		this.micrCode.setValue(aVASProviderAccDetail.getMicrCode());
		this.bankBranchID.setValue(String
				.valueOf(aVASProviderAccDetail.getBankBranchID() == 0 ? "" : aVASProviderAccDetail.getBankBranchID()));
		if (aVASProviderAccDetail.isNewRecord()) {
			this.providerId.setDescription("");
			this.entityCode.setDescription("");
			this.bankBranchID.setDescription("");
			this.bankNameDesc.setValue("");
		} else {
			this.providerId.setDescription(aVASProviderAccDetail.getProviderDesc());
			this.entityCode.setDescription(aVASProviderAccDetail.getEntityDesc());
			this.bankBranchID.setDescription(aVASProviderAccDetail.getBranchDesc());
			this.bankNameDesc.setValue(aVASProviderAccDetail.getBankName());
		}

		this.recordStatus.setValue(aVASProviderAccDetail.getRecordStatus());

		this.partnerBankId.setValue(aVASProviderAccDetail.getPartnerBankCode());
		this.partnerBankId.setDescription(aVASProviderAccDetail.getPartnerBankName());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVASProviderAccDetail
	 */
	public void doWriteComponentsToBean(VASProviderAccDetail aVASProviderAccDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// entityCode Id
		try {
			aVASProviderAccDetail.setEntityCode(this.entityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Provider Id
		try {
			aVASProviderAccDetail.setProviderId(Long.valueOf(this.providerId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object object = this.providerId.getAttribute("providerId");
			if (object != null) {
				aVASProviderAccDetail.setProviderId((Long.valueOf(object.toString())));
			} else {
				aVASProviderAccDetail.setProviderId(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Payment Mode
		try {
			String strPaymentMode = null;
			if (this.paymentMode.getSelectedItem() != null) {
				strPaymentMode = this.paymentMode.getSelectedItem().getValue().toString();
			}
			if (strPaymentMode != null && !PennantConstants.List_Select.equals(strPaymentMode)) {
				aVASProviderAccDetail.setPaymentMode(strPaymentMode);

			} else {
				aVASProviderAccDetail.setPaymentMode(PennantConstants.List_Select);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Branch I D
		try {
			aVASProviderAccDetail.setBankBranchID(Long.parseLong(this.bankBranchID.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aVASProviderAccDetail.setBankCode(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Number
		try {
			aVASProviderAccDetail.setAccountNumber(this.accountNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Receivable Adjustment
		try {
			aVASProviderAccDetail.setReceivableAdjustment(this.receivableAdjustment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Reconciliation Amount
		try {
			aVASProviderAccDetail.setReconciliationAmount(
					PennantApplicationUtil.unFormateAmount(this.reconciliationAmount.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aVASProviderAccDetail.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.partnerBankId.getValidatedValue();
			PartnerBank obj = (PartnerBank) this.partnerBankId.getAttribute("PartnerBank");
			if (obj != null) {
				aVASProviderAccDetail.setPartnerBankId(obj.getPartnerBankId());
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
	 * @param vASProviderAccDetail The entity that need to be render.
	 */
	public void doShowDialog(VASProviderAccDetail vASProviderAccDetail) {
		logger.debug(Literal.LEAVING);

		if (vASProviderAccDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.providerId.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(vASProviderAccDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.providerId.focus();
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

		doWriteBeanToComponents(vASProviderAccDetail);
		if (isFromLoan) {
			this.window_VASProviderAccDetailDialog.setWidth("65%");
			this.window_VASProviderAccDetailDialog.setHeight("40%");
			disableFields();
			setDialog(DialogType.MODAL);
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.providerId.isReadonly()) {
			this.providerId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASProviderAccDetailDialog_ProviderId.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.paymentMode.isDisabled()) {
			this.paymentMode.setConstraint(new StaticListValidator(listPaymentMode,
					Labels.getLabel("label_VASProviderAccDetailDialog_PaymentMode.value")));
		}
		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASProviderAccDetailDialog_EntityCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASProviderAccDetailDialog_BankBranchID.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASProviderAccDetailDialog_AccountNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true));
		}

		if (!this.reconciliationAmount.isReadonly()) {
			this.reconciliationAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_VASProviderAccDetailDialog_ReconciliationAmount.value"), getCcyFormat(),
					true, false));
		}

		this.partnerBankId.setConstraint(new PTStringValidator(
				Labels.getLabel("label_FinTypePartnerBankDialog_PartnerBankID.value"), null, true, true));
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.providerId.setConstraint("");
		this.paymentMode.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accountNumber.setConstraint("");
		this.reconciliationAmount.setConstraint("");
		this.bankName.setConstraint("");
		this.entityCode.setConstraint("");

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
		this.providerId.setErrorMessage("");
		this.paymentMode.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.accountNumber.setErrorMessage("");
		this.reconciliationAmount.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.entityCode.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final VASProviderAccDetail aVASProviderAccDetail = new VASProviderAccDetail();
		BeanUtils.copyProperties(this.vASProviderAccDetail, aVASProviderAccDetail);

		doDelete(String.valueOf(aVASProviderAccDetail.getId()), aVASProviderAccDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.vASProviderAccDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.providerId);
			readOnlyComponent(false, this.paymentMode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.providerId);
			readOnlyComponent(true, this.paymentMode);
			readOnlyComponent(true, this.entityCode);
		}
		readOnlyComponent(isReadOnly("VASProviderAccDetailDialog_BankBranchID"), this.bankBranchID);
		readOnlyComponent(isReadOnly("VASProviderAccDetailDialog_AccountNumber"), this.accountNumber);
		readOnlyComponent(isReadOnly("VASProviderAccDetailDialog_ReceivableAdjustment"), this.receivableAdjustment);
		readOnlyComponent(isReadOnly("VASProviderAccDetailDialog_ReconciliationAmount"), this.reconciliationAmount);
		readOnlyComponent(isReadOnly("VASProviderAccDetailDialog_Active"), this.active);
		readOnlyComponent(true, this.bankName);
		readOnlyComponent(true, this.ifscCode);
		readOnlyComponent(true, this.micrCode);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASProviderAccDetail.isNewRecord()) {
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

		readOnlyComponent(true, this.providerId);
		readOnlyComponent(true, this.paymentMode);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accountNumber);
		readOnlyComponent(true, this.receivableAdjustment);
		readOnlyComponent(true, this.reconciliationAmount);
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
		this.providerId.setValue("");
		this.providerId.setDescription("");
		this.paymentMode.setSelectedIndex(0);
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.accountNumber.setValue("");
		this.receivableAdjustment.setChecked(false);
		this.reconciliationAmount.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final VASProviderAccDetail aVASProviderAccDetail = new VASProviderAccDetail();
		BeanUtils.copyProperties(this.vASProviderAccDetail, aVASProviderAccDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aVASProviderAccDetail);

		isNew = aVASProviderAccDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVASProviderAccDetail.getRecordType())) {
				aVASProviderAccDetail.setVersion(aVASProviderAccDetail.getVersion() + 1);
				if (isNew) {
					aVASProviderAccDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASProviderAccDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASProviderAccDetail.setNewRecord(true);
				}
			}
		} else {
			aVASProviderAccDetail.setVersion(aVASProviderAccDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aVASProviderAccDetail, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
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
	protected boolean doProcess(VASProviderAccDetail aVASProviderAccDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVASProviderAccDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVASProviderAccDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASProviderAccDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVASProviderAccDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVASProviderAccDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVASProviderAccDetail);
				}

				if (isNotesMandatory(taskId, aVASProviderAccDetail)) {
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

			aVASProviderAccDetail.setTaskId(taskId);
			aVASProviderAccDetail.setNextTaskId(nextTaskId);
			aVASProviderAccDetail.setRoleCode(getRole());
			aVASProviderAccDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aVASProviderAccDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aVASProviderAccDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVASProviderAccDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVASProviderAccDetail, tranType);
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
		VASProviderAccDetail aVASProviderAccDetail = (VASProviderAccDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = vASProviderAccDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = vASProviderAccDetailService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = vASProviderAccDetailService.doApprove(auditHeader);

					if (aVASProviderAccDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = vASProviderAccDetailService.doReject(auditHeader);
					if (aVASProviderAccDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_VASProviderAccDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_VASProviderAccDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.vASProviderAccDetail), true);
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

	private AuditHeader getAuditHeader(VASProviderAccDetail aVASProviderAccDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASProviderAccDetail.getBefImage(),
				aVASProviderAccDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aVASProviderAccDetail.getUserDetails(),
				getOverideMap());
	}

	public void setVASProviderAccDetailService(VASProviderAccDetailService vASProviderAccDetailService) {
		this.vASProviderAccDetailService = vASProviderAccDetailService;
	}

	public void onFulfill$partnerBankId(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Object dataObject = partnerBankId.getObject();

		if (dataObject instanceof String) {
			this.partnerBankId.setValue(dataObject.toString());
			this.partnerBankId.setDescription("");
		} else {
			PartnerBank partnerbank = (PartnerBank) dataObject;
			if (partnerbank != null) {
				this.partnerBankId.setValue(partnerbank.getPartnerBankCode());
				// this.partnerBankId.setDescription(partnerbank.getPartnerBankName());
				this.partnerBankId.setAttribute("PartnerBank", partnerbank);
			}
		}

		logger.debug("Leaving");
	}

	public void disableFields() {
		this.providerId.setButtonDisabled(true);
		this.paymentMode.setDisabled(true);
		this.bankBranchID.setButtonDisabled(true);
		this.accountNumber.setDisabled(true);
		this.receivableAdjustment.setDisabled(true);
		this.reconciliationAmount.setDisabled(true);
		this.active.setDisabled(true);
		this.entityCode.setButtonDisabled(true);
		this.bankName.setDisabled(true);
		this.partnerBankId.setButtonDisabled(true);
		this.micrCode.setDisabled(true);
		this.ifscCode.setDisabled(true);
	}

}
