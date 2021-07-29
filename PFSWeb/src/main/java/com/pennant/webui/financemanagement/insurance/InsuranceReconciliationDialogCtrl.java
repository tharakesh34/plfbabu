package com.pennant.webui.financemanagement.insurance;

import java.math.BigDecimal;

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
 * * FileName : insuranceDetailsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/insuranceDetails/insuranceDetailsDialog.zul
 * file. <br>
 */
public class InsuranceReconciliationDialogCtrl extends GFCBaseCtrl<InsuranceDetails> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InsuranceReconciliationDialogCtrl.class);

	protected Window window_InsuranceDetailsDialog;

	protected Label finReference;
	protected Label insuranceReference;
	protected Label custCif;
	protected Label loanType;
	protected Label policyNumber;
	protected Label flpDays;

	protected CurrencyBox premiumAmt;
	protected CurrencyBox partnerPremiumAmt;
	protected Decimalbox reconAmt;
	protected Label reconAmtLable;
	protected CurrencyBox toleranceAmt;
	protected Combobox reconReasonCategory;
	protected Textbox remarks;
	private InsuranceDetails insuranceDetails;

	// Accounting
	protected Tab tabPostingDetails;
	protected Tabpanel tabPanelPostingDetails;
	private long accountsetId;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private boolean isAccountingExecuted = false;
	private AccountingSetDAO accountingSetDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private CollateralSetupDAO collateralSetupDAO;

	private transient InsuranceReconciliationListCtrl insuranceDetailsListCtrl;
	private transient InsuranceDetailService insuranceDetailService;
	private List<ValueLabel> reconReasonCategoryList = PennantStaticListUtil.getReconReasonCategory();

	/**
	 * default constructor.<br>
	 */
	public InsuranceReconciliationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsuranceDetailsDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.insuranceDetails.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceDetailsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InsuranceDetailsDialog);

		try {
			this.insuranceDetails = (InsuranceDetails) arguments.get("insuranceDetails");
			this.insuranceDetailsListCtrl = (InsuranceReconciliationListCtrl) arguments.get("insuranceDetailsListCtrl");
			if (this.insuranceDetails == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			InsuranceDetails insuranceDetails = new InsuranceDetails();
			BeanUtils.copyProperties(this.insuranceDetails, insuranceDetails);
			this.insuranceDetails.setBefImage(insuranceDetails);

			// Render the page and display the data.
			doLoadWorkFlow(this.insuranceDetails.isWorkflow(), this.insuranceDetails.getWorkflowId(),
					this.insuranceDetails.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.insuranceDetails);
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

		this.premiumAmt.setTextBoxWidth(200);
		this.partnerPremiumAmt.setTextBoxWidth(200);
		this.toleranceAmt.setTextBoxWidth(200);

		this.premiumAmt.setProperties(false, getCcyFormat());
		this.partnerPremiumAmt.setProperties(false, getCcyFormat());
		this.toleranceAmt.setProperties(false, getCcyFormat());

		this.premiumAmt.setReadonly(true);
		this.partnerPremiumAmt.setReadonly(true);
		this.reconAmt.setReadonly(true);
		this.toleranceAmt.setReadonly(true);

		ComponentsCtrl.applyForward(tabPostingDetails, "onSelect=onSelectTab");

		this.remarks.setMaxlength(500);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InsuranceDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InsuranceDetailsDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsuranceDetailsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
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
	 * @throws Exception
	 */
	public void onClick$btnDelete(Event event) throws Exception {
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
		doShowNotes(this.insuranceDetails);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		insuranceDetailsListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.insuranceDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param insuranceDetails
	 * 
	 */
	public void doWriteBeanToComponents(InsuranceDetails details) {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue(details.getFinReference());
		this.insuranceReference.setValue(details.getReference());
		this.policyNumber.setValue(details.getPolicyNumber());
		this.flpDays.setValue(String.valueOf(details.getFreeLockPeriod()));
		this.loanType.setValue(details.getFinType());
		VasCustomer customer = getInsuranceDetailService().getVasCustomerDetails(details.getFinReference(),
				details.getPostingAgainst());
		if (customer != null) {
			String custCifName = "";
			custCifName = custCifName.concat(customer.getCustCIF());
			if (StringUtils.trimToNull(customer.getCustShrtName()) != null) {
				custCifName = custCifName.concat(" - ").concat(customer.getCustShrtName());
			}
			this.custCif.setValue(custCifName);
		}

		this.premiumAmt.setValue(PennantAppUtil.formateAmount(details.getInsurancePremium(), getCcyFormat()));
		this.partnerPremiumAmt.setValue(PennantAppUtil.formateAmount(details.getPartnerPremium(), getCcyFormat()));

		BigDecimal reconAmount = details.getPartnerPremium().subtract(details.getInsurancePremium());
		this.insuranceDetails.setAdjAmount(reconAmount);
		if (BigDecimal.ZERO.compareTo(reconAmount) == 1) {
			reconAmount = reconAmount.negate();
			reconAmtLable.setValue("Deficit");
		} else {
			reconAmtLable.setValue("Excess");
		}
		this.reconAmt.setValue(PennantAppUtil.formateAmount(reconAmount, getCcyFormat()));
		this.toleranceAmt.setValue(PennantAppUtil.formateAmount(details.getTolaranceAmount(), getCcyFormat()));

		this.remarks.setValue(details.getManualReconRemarks());
		fillComboBox(this.reconReasonCategory, details.getManualReconResCategory(), this.reconReasonCategoryList, "");
		this.recordStatus.setValue(details.getRecordStatus());

		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(details, true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param details
	 */
	public void doWriteComponentsToBean(InsuranceDetails details) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Remarks
		try {
			details.setManualReconRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ReasonCategory
		try {
			details.setManualReconResCategory(this.reconReasonCategory.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		details.setRecordStatus(this.recordStatus.getValue());
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param insuranceDetails The entity that need to be render.
	 */
	public void doShowDialog(InsuranceDetails insuranceDetails) {
		logger.debug(Literal.ENTERING);

		if (insuranceDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.reconReasonCategory.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(insuranceDetails.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				this.reconReasonCategory.focus();
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
		doWriteBeanToComponents(insuranceDetails);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_InsuranceReconciliationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.reconReasonCategory.isDisabled()) {
			this.reconReasonCategory.setConstraint(new PTListValidator(
					Labels.getLabel("label_InsuranceReconciliationDialog_ReconReasonCategory.value"),
					reconReasonCategoryList, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.remarks.setConstraint("");
		this.reconReasonCategory.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.remarks.setErrorMessage("");
		this.reconReasonCategory.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	private void appendAccountingDetailTab(InsuranceDetails insuranceDetails, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		this.tabPostingDetails.setVisible(true);
		this.tabPanelPostingDetails.setVisible(true);
		this.tabPanelPostingDetails.setHeight(getListBoxHeight(7));
		if (!onLoadProcess) {
			accountsetId = getAccountingSetDAO().getAccountingSetId(AccountingEvent.INSADJ, AccountingEvent.INSADJ);
			final Map<String, Object> map = new HashMap<>();
			map.put("insuranceDetails", insuranceDetails);
			map.put("acSetID", accountsetId);
			map.put("enqModule", enqiryModule);
			map.put("dialogCtrl", this);
			map.put("isNotFinanceProcess", true);
			map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					this.tabPanelPostingDetails, map);
			this.tabPostingDetails.setVisible(true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Accounting Details
	 * 
	 * @throws Exception
	 */
	public void executeAccounting() throws Exception {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setPostingUserBranch(getUserWorkspace().getLoggedInUser().getBranchCode());
		aeEvent.setAccountingEvent(AccountingEvent.INSADJ);
		aeEvent.setFinReference(this.insuranceDetails.getReference());
		aeEvent.setValueDate(DateUtility.getAppDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		VASRecording vASRecording = getInsuranceDetailService()
				.getVASRecordingByRef(this.insuranceDetails.getReference());
		aeEvent.setEntityCode(vASRecording.getEntityCode());
		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vASRecording.getPostingAgainst())) {
			FinanceMain financeMain = getFinanceMainDAO().getFinanceMainForBatch(vASRecording.getPrimaryLinkRef());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setCustID(financeMain.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vASRecording.getPostingAgainst())) {
			Customer customer = getCustomerDAO().getCustomerByCIF(vASRecording.getPrimaryLinkRef(), "");
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCcy(customer.getCustBaseCcy());
			aeEvent.setCustID(customer.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vASRecording.getPostingAgainst())) {
			CollateralSetup collateralSetup = getCollateralSetupDAO()
					.getCollateralSetupByRef(vASRecording.getPrimaryLinkRef(), "");
			Customer customer = getCustomerDAO().getCustomerByID(collateralSetup.getDepositorId(), "");
			aeEvent.setCcy(collateralSetup.getCollateralCcy());
			aeEvent.setCustID(collateralSetup.getDepositorId());
			aeEvent.setBranch(customer.getCustDftBranch());
		}
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		this.insuranceDetails.getDeclaredFieldValues(aeEvent.getDataMap());

		aeEvent.getAcSetIDList().add(accountsetId);
		List<ReturnDataSet> returnSetEntries = postingsPreparationUtil.getAccounting(aeEvent).getReturnDataSet();
		accountingSetEntries.addAll(returnSetEntries);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
			isAccountingExecuted = true;
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean validateAccounting(boolean validate) {
		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
				|| this.userAction.getSelectedItem().getLabel().contains("Reject")
				|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			validate = false;
		} else {
			validate = true;
		}
		return validate;
	}

	public void onSelectTab(ForwardEvent event) throws Exception {
		doClearMessage();
		appendAccountingDetailTab(this.insuranceDetails, false);
	}

	/**
	 * Deletes a insuranceDetails object from database.<br>
	 * 
	 * @throws Exception
	 */
	private void doDelete() throws Exception {
		logger.debug(Literal.ENTERING);

		final InsuranceDetails ainsuranceDetails = new InsuranceDetails();
		BeanUtils.copyProperties(this.insuranceDetails, ainsuranceDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ ainsuranceDetails.getReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(ainsuranceDetails.getRecordType()).equals("")) {
				ainsuranceDetails.setVersion(ainsuranceDetails.getVersion() + 1);
				ainsuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					ainsuranceDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					ainsuranceDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), ainsuranceDetails.getNextTaskId(),
							ainsuranceDetails);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(ainsuranceDetails, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.insuranceDetails.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("InsuranceDetailsDialog_Description"), this.remarks);
		readOnlyComponent(isReadOnly("InsuranceDetailsDialog_EntityCode"), this.reconReasonCategory);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.insuranceDetails.isNewRecord()) {
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
		readOnlyComponent(true, this.remarks);
		readOnlyComponent(true, this.reconReasonCategory);
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
		logger.debug(Literal.ENTERING);
		this.remarks.setValue("");
		this.reconReasonCategory.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug(Literal.ENTERING);
		final InsuranceDetails ainsuranceDetails = new InsuranceDetails();
		BeanUtils.copyProperties(this.insuranceDetails, ainsuranceDetails);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(ainsuranceDetails);

		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			boolean validate = false;
			validate = validateAccounting(validate);
			// Accounting Details Validations
			if (validate) {
				if (!isAccountingExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (!this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") && accountingDetailDialogCtrl
						.getDisbCrSum().compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
		}

		isNew = ainsuranceDetails.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(ainsuranceDetails.getRecordType())) {
				ainsuranceDetails.setVersion(ainsuranceDetails.getVersion() + 1);
				if (isNew) {
					ainsuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					ainsuranceDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					ainsuranceDetails.setNewRecord(true);
				}
			}
		} else {
			ainsuranceDetails.setVersion(ainsuranceDetails.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(ainsuranceDetails, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
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
	 * @throws Exception
	 * 
	 */
	protected boolean doProcess(InsuranceDetails detail, String tranType) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		detail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(detail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, detail);
				}

				if (isNotesMandatory(taskId, detail)) {
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
			detail.setTaskId(taskId);
			detail.setNextTaskId(nextTaskId);
			detail.setRoleCode(getRole());
			detail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(detail, tranType);
			String operationRefs = getServiceOperations(taskId, detail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(detail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(detail, tranType);
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
	 * @throws Exception
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		InsuranceDetails ainsuranceDetails = (InsuranceDetails) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getInsuranceDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInsuranceDetailService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getInsuranceDetailService().doApprove(auditHeader);
						if (ainsuranceDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getInsuranceDetailService().doReject(auditHeader);
						if (ainsuranceDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InsuranceDetailsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceDetailsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.insuranceDetails), true);
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
			logger.error(Literal.EXCEPTION, e);
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InsuranceDetails ainsuranceDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ainsuranceDetails.getBefImage(), ainsuranceDetails);
		return new AuditHeader(getReference(), null, null, null, auditDetail, ainsuranceDetails.getUserDetails(),
				getOverideMap());
	}

	public void setinsuranceDetailsService(InsuranceDetailService insuranceDetailsService) {
		this.setInsuranceDetailService(insuranceDetailsService);
	}

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

}
