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
 * * FileName : FeeTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified
 * Date : 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.feetype.feetype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/FeeType/FeeType/feeTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FeeTypeDialogCtrl extends GFCBaseCtrl<FeeType> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeeTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FeeTypeDialog;

	protected Uppercasebox feeTypeCode;
	protected Textbox feeTypeDesc;

	protected Row accountingSetIdRow;
	protected ExtendedCombobox accountingSetID;
	protected Textbox hostFeeTypeCode;

	protected Row refundableFeeRow;
	protected Checkbox refundableFee;

	protected Row manualAdviceRow;
	protected Checkbox manualAdvice;
	protected Combobox adviseType;

	protected Row dueAccRow;
	protected Checkbox dueAccReq;
	protected ExtendedCombobox dueAccSet;

	protected Row payableLinkToRow;
	protected Combobox payableLinkTo;
	protected ExtendedCombobox receivableType;

	protected Row amortzRow;
	protected Checkbox amortzReq;

	protected Label label_FeeTypeDialog_AllowAutuRefund;
	protected Checkbox allowAutoRefund;

	protected Row taxApplicableRow;
	protected Checkbox taxApplicable;
	protected Combobox taxComponent;

	protected Row tdsRow;
	protected Checkbox tdsReq;

	protected Row incomeOrExpenseAcTypeRow;
	protected ExtendedCombobox incomeOrExpenseAcType;
	protected ExtendedCombobox waiverOrRefundAcType;
	protected Label labelIncomeOrExpenseAcType;
	protected Label labelWaiverOrRefundAcType;

	protected Checkbox active;

	protected Label recordType;
	protected Groupbox gb_statusDetails;

	private FeeType feeType;
	private boolean dueCreationReq;
	private Boolean feeTypeEnquiry;

	public static final int DEFAULT_ADVISETYPE = AdviseType.RECEIVABLE.id();
	private transient FeeTypeListCtrl feeTypeListCtrl;

	private List<ValueLabel> listAdviseCategory = PennantStaticListUtil.getManualAdviseCategory();
	private List<ValueLabel> listAdviseType = AdviseType.getList();
	private List<ValueLabel> listTaxComponent = PennantStaticListUtil.getFeeTaxTypes();

	private transient FeeTypeService feeTypeService;
	private transient PagedListService pagedListService;

	String pftInvFeeCode;
	String priInvFeeCode;
	String restructFeeCode;

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

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FeeType object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FeeTypeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			setPageComponents(this.window_FeeTypeDialog);

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqiryModule = (Boolean) arguments.get("enqModule");
			} else {
				enqiryModule = false;
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

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FeeTypeDialog");
			} else {
				getUserWorkspace().allocateAuthorities("FeeTypeDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

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

		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING + event.toString());
		doDelete();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_FeeTypeDialog);
		logger.debug(Literal.LEAVING + event.toString());
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
	 */
	public void onClose$window_FeeTypeDialog(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doClose();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.feeType);

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFeeType
	 * @throws InterruptedException
	 */
	public void doShowDialog(FeeType aFeeType) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (feeType.isNewRecord()) {
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
			this.window_FeeTypeDialog.setHeight("80%");
			this.window_FeeTypeDialog.setWidth("92%");
			this.window_FeeTypeDialog.doModal();
		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.feeTypeCode);
		readOnlyComponent(true, this.feeTypeDesc);
		readOnlyComponent(true, this.manualAdvice);
		readOnlyComponent(true, this.accountingSetID);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.adviseType);
		readOnlyComponent(true, this.amortzReq);
		readOnlyComponent(true, this.taxApplicable);
		readOnlyComponent(true, this.hostFeeTypeCode);
		readOnlyComponent(true, this.taxComponent);
		readOnlyComponent(true, this.payableLinkTo);
		readOnlyComponent(true, this.receivableType);
		readOnlyComponent(true, this.allowAutoRefund);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnNew"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnDelete"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FeeTypeDialog_btnSave"));
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		dueCreationReq = SysParamUtil.isAllowed("ALLOW_MANUAL_ADV_DUE_CREATION");

		// Empty sent any required attributes
		this.feeTypeCode.setMaxlength(8);
		this.feeTypeDesc.setMaxlength(35);
		this.hostFeeTypeCode.setMaxlength(50);

		this.accountingSetID.setModuleName("AccountingSet");
		this.accountingSetID.setValueColumn("AccountSetCode");
		this.accountingSetID.setDescColumn("AccountSetCodeName");
		this.accountingSetID.setValidateColumns(new String[] { "AccountSetCode", "AccountSetCodeName" });
		this.accountingSetID.setMandatoryStyle(false);

		Filter filters[] = new Filter[1];
		filters[0] = new Filter("EventCode", AccountingEvent.MANFEE, Filter.OP_EQUAL);
		this.accountingSetID.setFilters(filters);

		this.dueAccSet.setModuleName("AccountingSet");
		this.dueAccSet.setValueColumn("AccountSetCode");
		this.dueAccSet.setDescColumn("AccountSetCodeName");
		this.dueAccSet.setValidateColumns(new String[] { "AccountSetCode", "AccountSetCodeName" });
		this.dueAccSet.setMandatoryStyle(true);

		this.dueAccRow.setVisible(dueCreationReq);
		this.tdsRow.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		this.incomeOrExpenseAcTypeRow.setVisible(FeeExtension.ALLOW_SINGLE_FEE_CONFIG);
		this.incomeOrExpenseAcType.setWidth("200px");
		this.incomeOrExpenseAcType.setModuleName("AccountType");
		this.incomeOrExpenseAcType.setValueColumn("AcType");
		this.incomeOrExpenseAcType.setDescColumn("AcTypeDesc");
		this.incomeOrExpenseAcType.setDisplayStyle(2);
		this.incomeOrExpenseAcType.setValidateColumns(new String[] { "AcType" });

		this.incomeOrExpenseAcType.setWhereClause("ACTYPE NOT Like 'FEE_%'");

		this.waiverOrRefundAcType.setWidth("200px");
		this.waiverOrRefundAcType.setModuleName("AccountType");
		this.waiverOrRefundAcType.setValueColumn("AcType");
		this.waiverOrRefundAcType.setDescColumn("AcTypeDesc");
		this.waiverOrRefundAcType.setDisplayStyle(2);
		this.waiverOrRefundAcType.setValidateColumns(new String[] { "AcType" });

		this.waiverOrRefundAcType.setWhereClause("ACTYPE NOT Like 'FEE_%'");

		this.receivableType.setModuleName("FeeType");
		this.receivableType.setValueColumn("FeeTypeCode");
		this.receivableType.setDescColumn("FeeTypeDesc");
		this.receivableType.setValidateColumns(new String[] { "FeeTypeCode" });
		this.receivableType.setMandatoryStyle(true);

		this.receivableType.setWhereClause("(ManualAdvice = 1 and AdviseType = 1) or ManualAdvice = 0");

		this.allowAutoRefund.setDisabled(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFeeType FeeType
	 */
	public void doWriteBeanToComponents(FeeType aFeeType) {
		logger.debug(Literal.ENTERING);

		pftInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PFT_EXEMPTED);
		priInvFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_PRI_EXEMPTED);
		restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);

		String code = StringUtils.trimToEmpty(aFeeType.getFeeTypeCode());

		this.feeTypeCode.setValue(code);
		this.feeTypeDesc.setValue(aFeeType.getFeeTypeDesc());
		this.manualAdvice.setChecked(aFeeType.isManualAdvice());
		this.hostFeeTypeCode.setValue(aFeeType.getHostFeeTypeCode());
		this.refundableFee.setChecked(aFeeType.isRefundable());
		this.allowAutoRefund.setChecked(aFeeType.isAllowAutoRefund());

		fillComboBox(this.adviseType, String.valueOf(aFeeType.getAdviseType()), listAdviseType, "");
		fillComboBox(this.payableLinkTo, aFeeType.getPayableLinkTo(), listAdviseCategory);

		this.dueAccReq.setChecked(aFeeType.isDueAccReq());
		this.tdsReq.setChecked(aFeeType.isTdsReq());

		if (aFeeType.getDueAccSet() != null) {
			this.dueAccSet.setValue(aFeeType.getDueAcctSetCode(), aFeeType.getDueAcctSetCodeName());
			this.dueAccSet.setObject(aFeeType.getDueAccSet());
		}

		if (aFeeType.getRecvFeeTypeId() != null) {
			FeeType ft = new FeeType();
			ft.setFeeTypeID(aFeeType.getRecvFeeTypeId());
			this.receivableType.setValue(aFeeType.getRecvFeeTypeCode(), aFeeType.getRecvFeeTypeDesc());
			this.receivableType.setObject(ft);
		}

		if (aFeeType.getAccountSetId() != null) {
			this.accountingSetID.setValue(aFeeType.getAccountSetCode(), aFeeType.getAccountSetCodeName());
			this.accountingSetID.setObject(new AccountingSet(aFeeType.getAccountSetId()));
		}

		if (aFeeType.isNewRecord()) {
			this.amortzReq.setChecked(true);
		} else {
			this.amortzReq.setChecked(aFeeType.isAmortzReq());
		}
		this.active.setChecked(aFeeType.isActive());

		this.taxApplicable.setChecked(aFeeType.isTaxApplicable());
		fillComboBox(this.taxComponent, String.valueOf(aFeeType.getTaxComponent()), listTaxComponent, "");

		this.incomeOrExpenseAcType.setValue(aFeeType.getIncomeOrExpenseAcType());
		this.incomeOrExpenseAcType.setObject(new AccountType(aFeeType.getIncomeOrExpenseAcType()));

		this.waiverOrRefundAcType.setValue(aFeeType.getWaiverOrRefundAcType());
		this.waiverOrRefundAcType.setObject(new AccountType(aFeeType.getWaiverOrRefundAcType()));

		this.recordStatus.setValue(aFeeType.getRecordStatus());

		doDisplayManualAdvice(code);

		doDisplayAccountingSet(code);

		doDisplayAmortz(code);

		doDisplayRefundableFee(code);

		doDisplayTaxApplicable(code);

		doDisplayTDS(code);

		doDisableDelete(code);

		doSetManualAdvice(aFeeType.isManualAdvice());

		doSetAdviceType(String.valueOf(aFeeType.getAdviseType()));

		doSetPayableLinkTo(aFeeType.getPayableLinkTo());

		doSetTaxApplicable(aFeeType.isTaxApplicable());

		doSetDueAccReq(aFeeType.isDueAccReq());

		if (aFeeType.isNewRecord() || (aFeeType.getRecordType() != null ? aFeeType.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		} else {
			if (this.dueAccReq.isChecked()) {
				readOnlyComponent(true, this.dueAccReq);
				readOnlyComponent(true, this.dueAccSet);
			}
			if (this.tdsReq.isChecked()) {
				readOnlyComponent(true, this.tdsReq);
			}
		}

		if (Allocation.ODC.equals(this.feeTypeCode.getValue())) {
			this.feeTypeDesc.setReadonly(true);
			this.amortzReq.setDisabled(true);
			this.tdsReq.setDisabled(true);
			this.incomeOrExpenseAcType.setReadonly(true);
			this.waiverOrRefundAcType.setReadonly(true);
			this.taxComponent.setDisabled(true);
			this.taxApplicable.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFeeType
	 */
	public void doWriteComponentsToBean(FeeType aFeeType) {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fee Type Code
		try {
			if ("FEE".equals(this.feeTypeCode.getValue())) {
				wve.add(new WrongValueException(this.feeTypeCode, Labels.getLabel("invalid_FeeCode",
						new String[] { Labels.getLabel("label_FeeTypeDialog_FeeTypeCode.value") })));
			} else {
				aFeeType.setFeeTypeCode(this.feeTypeCode.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
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
		// Accounting Set ID
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

		// ApplicableFor
		try {
			aFeeType.setManualAdvice(this.manualAdvice.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Refundable fee
		try {
			aFeeType.setRefundable(this.refundableFee.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Advise Type
		try {
			String adviseType = getComboboxValue(this.adviseType);
			if (PennantConstants.List_Select.equals(adviseType) || adviseType == null) {
				aFeeType.setAdviseType(0);
			} else {
				aFeeType.setAdviseType(Integer.parseInt(adviseType));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setDueAccReq(this.dueAccReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFeeType.setTdsReq(this.tdsReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Allow Auto Refunds
		try {
			aFeeType.setAllowAutoRefund(this.allowAutoRefund.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.dueAccSet != null && this.dueCreationReq) {
				if (this.dueAccReq.isChecked()) {
					this.dueAccSet.getValidatedValue();
					AccountingSet accountingSet = (AccountingSet) this.dueAccSet.getObject();
					if (accountingSet != null) {
						aFeeType.setDueAccSet(accountingSet.getAccountSetid());
					}
				} else {
					aFeeType.setDueAccSet(null);
				}
			} else {
				aFeeType.setDueAccSet(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setTaxApplicable(this.taxApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setTaxComponent(getComboboxValue(this.taxComponent));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setAmortzReq(this.amortzReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.incomeOrExpenseAcType.getObject() instanceof AccountType) {
				AccountType accountType = (AccountType) this.incomeOrExpenseAcType.getObject();
				if (accountType != null) {
					aFeeType.setIncomeOrExpenseAcType(accountType.getAcType());
				}
			} else {
				aFeeType.setIncomeOrExpenseAcType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.waiverOrRefundAcType.getObject() instanceof AccountType) {
				AccountType accountType = (AccountType) this.waiverOrRefundAcType.getObject();
				if (accountType != null) {
					aFeeType.setWaiverOrRefundAcType(accountType.getAcType());
				}
			} else {
				aFeeType.setWaiverOrRefundAcType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeeType.setPayableLinkTo(getComboboxValue(this.payableLinkTo));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.receivableType.isReadonly() && Allocation.MANADV.equals(getComboboxValue(this.payableLinkTo))) {
				Long recvFeeTypeId = null;
				Object object = this.receivableType.getObject();
				if (object != null && object instanceof FeeType) {
					recvFeeTypeId = ((FeeType) object).getFeeTypeID();
				}

				if (recvFeeTypeId == null || recvFeeTypeId <= 0) {
					wve.add(new WrongValueException(this.receivableType, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_FeeTypeDialog_ReceivableType.value") })));
				}

				aFeeType.setRecvFeeTypeId(recvFeeTypeId);
			}

			if (!Allocation.MANADV.equals(aFeeType.getPayableLinkTo())) {
				aFeeType.setRecvFeeTypeId(null);
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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.feeTypeCode.isReadonly()) {
			this.feeTypeCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_FeeTypeCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.feeTypeDesc.isReadonly()) {
			this.feeTypeDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_FeeTypeDesc.value"),
							PennantRegularExpressions.REGEX_COMPANY_NAME, true));
		}

		if (!this.accountingSetID.isReadonly() && this.accountingSetID.isVisible()) {
			this.accountingSetID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_AccountingSetID.value"), null, true));
		}

		if (!this.dueAccSet.isReadonly() && dueCreationReq && this.dueAccReq.isChecked()) {
			this.dueAccSet.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FeeTypeDialog_DueAccSet.value"), null, true, true));
		}

		if (!this.adviseType.isDisabled()) {
			this.adviseType.setConstraint(
					new StaticListValidator(listAdviseType, Labels.getLabel("label_FeeTypeDialog_AdviseType.value")));
		}

		if (!this.taxComponent.isDisabled()) {
			this.taxComponent.setConstraint(new StaticListValidator(listTaxComponent,
					Labels.getLabel("label_FeeTypeDialog_TaxComponent.value")));
		}

		if (this.payableLinkToRow.isVisible() && !this.payableLinkTo.isDisabled()) {
			this.payableLinkTo.setConstraint(new StaticListValidator(listAdviseCategory,
					Labels.getLabel("label_FeeTypeDialog_PayableLinkTo.value")));
		}

		String feeTypeCode = this.feeTypeCode.getValue();

		if (isSingleFeeTypeRequired(feeTypeCode) && !this.incomeOrExpenseAcType.isReadonly()
				&& this.incomeOrExpenseAcTypeRow.isVisible()) {
			this.incomeOrExpenseAcType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeeTypeDialog_IncomeOrExpenseAcType.value"), null, true, true));
		}

		if (isSingleFeeTypeRequired(feeTypeCode) && !this.waiverOrRefundAcType.isReadonly()
				&& this.incomeOrExpenseAcTypeRow.isVisible()) {
			this.waiverOrRefundAcType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FeeTypeDialog_WaiverOrRefundAcType.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		this.feeTypeCode.setConstraint("");
		this.feeTypeDesc.setConstraint("");
		this.hostFeeTypeCode.setConstraint("");
		this.accountingSetID.setConstraint("");
		this.adviseType.setConstraint("");
		this.taxComponent.setConstraint("");
		this.payableLinkTo.setConstraint("");
		this.incomeOrExpenseAcType.setConstraint("");
		this.waiverOrRefundAcType.setConstraint("");
		this.accountingSetID.setConstraint("");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Fee Type I D
		// Fee Type Code
		// Description
		// Active
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.receivableType.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.taxComponent.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */

	protected void refreshList() {
		feeTypeListCtrl.search();
	}

	private void doDisplayManualAdvice(String feeTypeCode) {
		boolean manualAdvice = true;
		if (feeTypeCode.equals(pftInvFeeCode) || feeTypeCode.equals(priInvFeeCode)
				|| feeTypeCode.equals(restructFeeCode)) {
			manualAdvice = false;
		} else {
			switch (feeTypeCode) {
			case Allocation.BOUNCE:
			case Allocation.ODC:
			case Allocation.PFT:
				manualAdvice = false;
				break;

			default:
				break;
			}
		}

		this.manualAdviceRow.setVisible(manualAdvice);
	}

	private void doDisableDelete(String feeTypeCode) {
		boolean allowDelete = true;
		if (feeTypeCode.equals(pftInvFeeCode) || feeTypeCode.equals(priInvFeeCode)
				|| feeTypeCode.equals(restructFeeCode)) {
			allowDelete = false;
		} else {
			switch (feeTypeCode) {
			case Allocation.BOUNCE:
			case Allocation.ODC:
			case Allocation.PFT:
				allowDelete = false;
				break;

			default:
				break;
			}
		}

		this.active.setDisabled(!allowDelete && isReadOnly("FeeTypeDialog_Active"));

		if (this.btnDelete.isVisible() && !allowDelete) {
			this.btnDelete.setVisible(allowDelete);
		}
	}

	private void doDisplayAccountingSet(String feeTypeCode) {
		this.accountingSetIdRow.setVisible(Allocation.BOUNCE.equals(feeTypeCode));
		this.accountingSetID.setVisible(Allocation.BOUNCE.equals(feeTypeCode));
		this.accountingSetID.setMandatoryStyle(true);
	}

	private void doDisplayAmortz(String feeTypeCode) {
		if (feeTypeCode.equals(pftInvFeeCode) || feeTypeCode.equals(priInvFeeCode)
				|| feeTypeCode.equals(restructFeeCode) || feeTypeCode.equals(Allocation.PFT)) {
			this.amortzRow.setVisible(false);
		}
	}

	private void doDisplayRefundableFee(String feeTypeCode) {
		if (feeTypeCode.equals(restructFeeCode) || CalculationConstants.FEE_SUBVENTION.equals(feeTypeCode)) {
			this.refundableFeeRow.setVisible(false);
		}

	}

	private void doDisplayTaxApplicable(String feeTypeCode) {
		if (feeTypeCode.equals(Allocation.LPFT) || feeTypeCode.equals(restructFeeCode)
				|| feeTypeCode.equals(CalculationConstants.FEE_SUBVENTION)) {
			this.taxApplicableRow.setVisible(false);
		}
	}

	private void doDisplayTDS(String feeTypeCode) {
		if (feeTypeCode.equals(CalculationConstants.FEE_SUBVENTION)) {
			this.tdsRow.setVisible(false);
		}
	}

	private void doSetManualAdvice(boolean manualAdvice) {
		if (manualAdvice) {
			this.adviseType.setDisabled(isReadOnly("FeeTypeDialog_AdviseType"));

			this.dueAccRow.setVisible(dueCreationReq);

			this.dueAccReq.setDisabled(false);
			this.dueAccSet.setReadonly(false);
			this.payableLinkTo.setDisabled(false);
			this.receivableType.setReadonly(false);
			this.refundableFee.setDisabled(true);
			if (this.refundableFee.isChecked()
					&& StringUtils.equals(Labels.getLabel("label_TransEntry_Payable"), this.adviseType.getValue())
					&& !isReadOnly("FeeTypeDialog_AllowAutoRefund")) {
				this.allowAutoRefund.setDisabled(false);
			} else {
				this.allowAutoRefund.setDisabled(true);
			}
		} else {
			this.dueAccRow.setVisible(false);

			this.adviseType.setValue(null);

			this.dueAccReq.setChecked(false);
			this.dueAccSet.setValue(null);
			this.dueAccSet.setObject(null);

			this.payableLinkTo.setValue(null);

			this.receivableType.setValue(null);
			this.receivableType.setObject(null);

			this.adviseType.setDisabled(true);
			this.dueAccReq.setDisabled(true);
			this.dueAccSet.setReadonly(true);
			this.payableLinkTo.setDisabled(true);
			this.receivableType.setReadonly(true);

			fillComboBox(this.adviseType, null, listAdviseType, "");
			doSetAdviceType(PennantConstants.List_Select);

			this.allowAutoRefund.setChecked(false);
			this.allowAutoRefund.setDisabled(true);
		}

		doSetDueAccReq(this.dueAccReq.isChecked());
	}

	public void onCheck$manualAdvice(Event event) {
		doSetManualAdvice(this.manualAdvice.isChecked());
	}

	private void doSetAdviceType(String adviceType) {
		if (PennantConstants.List_Select.equals(adviceType)) {
			this.payableLinkTo.setValue(null);
			this.receivableType.setValue(null);
			this.payableLinkToRow.setVisible(false);
			this.refundableFee.setDisabled(true);

			this.labelIncomeOrExpenseAcType
					.setValue(Labels.getLabel("label_FeeTypeDialog_IncomeOrExpenseAcType.value"));
			this.labelWaiverOrRefundAcType.setValue(Labels.getLabel("label_FeeTypeDialog_WaiverOrRefundAcType.value"));
			return;
		}

		int adviseCtgry = Integer.parseInt(adviceType);
		if (AdviseType.isPayable(adviseCtgry)) {
			this.payableLinkToRow.setVisible(true);
			this.payableLinkTo.setDisabled(isReadOnly("FeeTypeDialog_PayableLinkTo"));

			String payableLinkTo = getComboboxValue(this.payableLinkTo);

			if (this.refundableFee.isChecked() && this.manualAdvice.isChecked()
					&& !isReadOnly("FeeTypeDialog_AllowAutoRefund")) {
				this.allowAutoRefund.setDisabled(false);
			}

			if (StringUtils.isEmpty(payableLinkTo)) {
				payableLinkTo = Allocation.ADHOC;
			}

			fillComboBox(this.payableLinkTo, payableLinkTo, listAdviseCategory);
			doSetPayableLinkTo(payableLinkTo);

			this.refundableFee.setDisabled(false);

			this.labelIncomeOrExpenseAcType.setValue(Labels.getLabel("label_FeeTypeDialog_FeeExpense.value"));
			this.labelWaiverOrRefundAcType.setValue(Labels.getLabel("label_FeeTypeDialog_FeeRefund.value"));
		} else if (AdviseType.isReceivable(adviseCtgry)) {
			this.allowAutoRefund.setDisabled(true);
			this.allowAutoRefund.setChecked(false);
			this.refundableFee.setDisabled(true);
			this.refundableFee.setChecked(false);
			this.payableLinkToRow.setVisible(false);

			this.labelIncomeOrExpenseAcType
					.setValue(Labels.getLabel("label_FeeTypeDialog_IncomeOrExpenseAcType.value"));
			this.labelWaiverOrRefundAcType.setValue(Labels.getLabel("label_FeeTypeDialog_WaiverOrRefundAcType.value"));
		} else {
			this.payableLinkToRow.setVisible(false);
			this.payableLinkTo.setValue(null);
			this.receivableType.setValue(null);
			this.allowAutoRefund.setChecked(false);
			this.allowAutoRefund.setDisabled(true);

			this.labelIncomeOrExpenseAcType
					.setValue(Labels.getLabel("label_FeeTypeDialog_IncomeOrExpenseAcType.value"));
			this.labelWaiverOrRefundAcType.setValue(Labels.getLabel("label_FeeTypeDialog_WaiverOrRefundAcType.value"));
		}
	}

	public void onChange$adviseType(Event event) {
		doSetAdviceType(getComboboxValue(adviseType));
	}

	private void doSetPayableLinkTo(String payableLinkTo) {
		if (Allocation.MANADV.equals(payableLinkTo)) {
			this.receivableType.setReadonly(isReadOnly("FeeTypeDialog_ReceivableType"));
		} else {
			this.receivableType.setReadonly(true);
			this.receivableType.setValue(null);
			this.receivableType.setObject(null);
		}
	}

	public void onChange$payableLinkTo(Event event) {
		doSetPayableLinkTo(getComboboxValue(this.payableLinkTo));
	}

	private void doSetDueAccReq(boolean dueAccReq) {
		this.dueAccReq.setVisible(dueCreationReq);
		if (dueAccReq) {
			this.dueAccSet.setReadonly(isReadOnly("FeeTypeDialog_AmortizationRequired"));
		} else {
			this.dueAccSet.setValue(null);
			this.dueAccSet.setReadonly(true);
			this.dueAccSet.setObject(null);
		}
	}

	public void onCheck$dueAccReq(Event event) {
		doSetDueAccReq(dueAccReq.isChecked());
	}

	private void doSetTaxApplicable(boolean taxApplicable) {
		if (taxApplicable) {
			this.taxComponent.setDisabled(isReadOnly("FeeTypeDialog_TaxApplicable"));
		} else {
			fillComboBox(this.taxComponent, null, listTaxComponent, "");
			taxComponent.setDisabled(true);
		}
	}

	public void onCheck$taxApplicable(Event event) {
		doSetTaxApplicable(this.taxApplicable.isChecked());
	}

	public void onCheck$refundableFee(Event event) {
		doSetRefundableFee(this.refundableFee.isChecked());
	}

	private void doSetRefundableFee(boolean refFee) {
		if (refFee) {
			if (this.manualAdvice.isChecked()
					&& StringUtils.equals(Labels.getLabel("label_TransEntry_Payable"), this.adviseType.getValue())) {
				this.allowAutoRefund.setDisabled(false);
			}
		} else {
			this.allowAutoRefund.setChecked(false);
			this.allowAutoRefund.setDisabled(true);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.feeType.isNewRecord()) {
			this.feeTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.feeTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.feeTypeDesc.setReadonly(isReadOnly("FeeTypeDialog_FeeTypeDesc"));

		this.accountingSetID.setReadonly(isReadOnly("FeeTypeDialog_AccountSetId"));
		this.hostFeeTypeCode.setReadonly(isReadOnly("FeeTypeDialog_HostFeeTypeCode"));

		this.refundableFee.setDisabled(isReadOnly("FeeTypeDialog_RefundableFee"));

		this.manualAdvice.setDisabled(isReadOnly("FeeTypeDialog_ApplicableFor"));
		this.adviseType.setDisabled(isReadOnly("FeeTypeDialog_AdviseType"));

		this.payableLinkTo.setDisabled(isReadOnly("FeeTypeDialog_PayableLinkTo"));
		this.receivableType.setReadonly(isReadOnly("FeeTypeDialog_ReceivableType"));

		this.dueAccReq.setDisabled(isReadOnly("FeeTypeDialog_AmortizationRequired"));
		this.dueAccSet.setReadonly(isReadOnly("FeeTypeDialog_AmortizationRequired"));

		this.amortzReq.setDisabled(isReadOnly("FeeTypeDialog_AmortizationRequired"));

		this.taxApplicable.setDisabled(isReadOnly("FeeTypeDialog_TaxApplicable"));
		this.taxComponent.setReadonly(isReadOnly("FeeTypeDialog_TaxComponent"));

		this.tdsReq.setDisabled(isReadOnly("FeeTypeDialog_TaxApplicable"));

		this.incomeOrExpenseAcType.setReadonly(isReadOnly("FeeTypeDialog_IncomeOrExpenseAcType"));
		this.waiverOrRefundAcType.setReadonly(isReadOnly("FeeTypeDialog_WaiverOrRefundAcType"));

		this.active.setDisabled(isReadOnly("FeeTypeDialog_Active"));
		this.allowAutoRefund.setDisabled(isReadOnly("FeeTypeDialog_AllowAutoRefund"));

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
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.feeType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FeeType aFeeType = new FeeType();
		BeanUtils.copyProperties(getFeeType(), aFeeType);

		doDelete(Labels.getLabel("label_FeeTypeDialog_FeeTypeCode.value") + " : " + aFeeType.getFeeTypeCode(),
				aFeeType);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.feeTypeCode.setValue("");
		this.feeTypeDesc.setValue("");
		this.hostFeeTypeCode.setValue("");
		this.manualAdvice.setValue("");
		this.accountingSetID.setValue("");
		this.adviseType.setSelectedIndex(0);
		this.active.setChecked(false);
		this.payableLinkTo.setSelectedIndex(0);
		this.taxApplicable.setChecked(false);
		this.taxComponent.setSelectedIndex(0);
		this.amortzReq.setChecked(false);
		this.incomeOrExpenseAcType.setValue("");
		this.waiverOrRefundAcType.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
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

		isNew = aFeeType.isNewRecord();
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
				// doWriteBeanToComponents(aFeeType);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
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

	protected boolean doProcess(FeeType aFeeType, String tranType) {
		logger.debug(Literal.ENTERING);
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
		boolean deleteNotes = false;

		FeeType aFeeType = (FeeType) auditHeader.getAuditDetail().getModelData();

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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FeeType aFeeType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeeType.getBefImage(), aFeeType);
		return new AuditHeader(String.valueOf(aFeeType.getFeeTypeID()), null, null, null, auditDetail,
				aFeeType.getUserDetails(), getOverideMap());
	}

	private boolean isSingleFeeTypeRequired(String feeTypeCode) {
		boolean alwvalidation = true;

		if (feeTypeCode.equals(pftInvFeeCode) || feeTypeCode.equals(priInvFeeCode)
				|| feeTypeCode.equals(restructFeeCode)) {
			return alwvalidation = false;
		} else {
			switch (feeTypeCode) {
			case Allocation.BOUNCE:
			case Allocation.ODC:
			case Allocation.PFT:
				return alwvalidation = false;
			default:
				break;
			}
		}
		
		return alwvalidation;
	}

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