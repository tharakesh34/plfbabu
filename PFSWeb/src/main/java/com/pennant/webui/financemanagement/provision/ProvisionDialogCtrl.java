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
 * * FileName : ProvisionDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.provision;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.extension.AccountingExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * This is the controller class for the /WEB-INF/pages/Provision/Provision/provisionDialog.zul file.
 */
public class ProvisionDialogCtrl extends FinanceBaseCtrl<Provision> {
	private static final long serialVersionUID = 5139814152842315333L;
	private static final Logger logger = LogManager.getLogger(ProvisionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProvisionDialog; // autowired

	protected ExtendedCombobox finReference; // autowired
	protected Textbox finBranch; // autowired
	protected Textbox finType; // autowired
	protected Longbox custID; // autowired
	protected Textbox lovDescCustCIF; // autowired
	protected Label custShrtName; // autowired
	protected Checkbox useNFProv; // autowired
	protected Checkbox autoReleaseNFP; // autowired
	protected Decimalbox principalDue; // autowired
	protected Decimalbox profitDue; // autowired
	protected Decimalbox dueTotal; // autowired
	protected Decimalbox nonFormulaProv; // autowired
	protected Datebox dueFromDate; // autowired
	protected Decimalbox calProvisionedAmt; // autowired
	protected Decimalbox provisionedAmt; // autowired
	protected Datebox lastFullyPaidDate; // autowired

	// not auto wired vars
	private Provision provision; // overhanded per param
	private transient ProvisionListCtrl provisionListCtrl; // overhanded per
															// param
	private transient boolean validationOn;

	private String menuItemRightName = null;

	// ServiceDAOs / Domain Classes
	private transient ProvisionService provisionService;

	private FinanceReferenceDetailService financeReferenceDetailService;
	private FinanceWorkFlowService financeWorkFlowService;
	private CustomerDetailsService customerDetailsService;
	private OverdueChargeRecoveryService overdueChargeRecoveryService;

	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

	private NotificationService notificationService;

	/**
	 * default constructor.<br>
	 */
	public ProvisionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProvisionDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Provision object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ProvisionDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProvisionDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("provision")) {
				this.provision = (Provision) arguments.get("provision");
				Provision befImage = new Provision();
				BeanUtils.copyProperties(this.provision, befImage);
				this.provision.setBefImage(befImage);
				setFinanceDetail(this.provision.getFinanceDetail());
				setProvision(this.provision);
			} else {
				setProvision(null);
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (!getProvision().isNewRecord()) {
				doLoadWorkFlow(this.provision.isWorkflow(), this.provision.getWorkflowId(),
						this.provision.getNextTaskId());
			}

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "ProvisionDialog", menuItemRightName);
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}

				if (getProvision().isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(true);
			}

			isEnquiry = true;
			/* set components visible dependent of the users rights */
			doCheckRights();
			getBorderLayoutHeight();

			// READ OVERHANDED params !
			// we get the provisionListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete provision here.

			if (arguments.containsKey("provisionListCtrl")) {
				setProvisionListCtrl((ProvisionListCtrl) arguments.get("provisionListCtrl"));

			} else {
				setProvisionListCtrl(null);
			}

			if (getProvision().isNewRecord()) {
				doshowProvisionDialog();
				setDialog(DialogType.EMBEDDED);
			} else {
				doShowDialog(getProvision());
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProvisionDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		int format = CurrencyUtil.getFormat(getProvision().getFinCcy());

		this.finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.finType.setMaxlength(8);
		this.custID.setMaxlength(19);
		this.principalDue.setMaxlength(18);
		this.principalDue.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.profitDue.setMaxlength(18);
		this.profitDue.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.dueTotal.setMaxlength(18);
		this.dueTotal.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.nonFormulaProv.setMaxlength(18);
		this.nonFormulaProv.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.calProvisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.provisionedAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));

		this.dueFromDate.setFormat(DateUtil.DateFormat.SHORT_DATE.getPattern());

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

		getUserWorkspace().allocateAuthorities("ProvisionDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProvisionDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ProvisionDialog);
		logger.debug("Leaving" + event.toString());
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
		try {
			doWriteBeanToComponents(this.provision.getBefImage());
		} catch (Exception e) {
			logger.debug("Exception:", e);
		}
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvision Provision
	 */
	public void doWriteBeanToComponents(Provision aProvision) {

		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(aProvision.getFinCcy());

		this.finReference.setValue(aProvision.getFinReference());
		this.finBranch.setValue(aProvision.getFinBranch());
		this.finType.setValue(aProvision.getFinType());
		this.custID.setValue(aProvision.getCustID());
		this.lovDescCustCIF.setValue(aProvision.getCustCIF());
		this.custShrtName.setValue(aProvision.getCustShrtName());
		// this.useNFProv.setChecked(aProvision.isUseNFProv());
		// this.autoReleaseNFP.setChecked(aProvision.isAutoReleaseNFP());
		// this.principalDue.setValue(PennantAppUtil.formateAmount(aProvision.getPrincipalDue(), format));
		// this.profitDue.setValue(PennantAppUtil.formateAmount(aProvision.getProfitDue(), format));
		/*
		 * this.dueTotal.setValue(
		 * PennantAppUtil.formateAmount(aProvision.getPrincipalDue().add(aProvision.getProfitDue()), format));
		 */
		// this.nonFormulaProv.setValue(PennantAppUtil.formateAmount(aProvision.getNonFormulaProv(), format));
		// this.calProvisionedAmt.setValue(PennantAppUtil.formateAmount(aProvision.getProvisionAmtCal(), format));
		this.provisionedAmt.setValue(CurrencyUtil.parse(aProvision.getProvisionedAmt(), format));

		this.dueFromDate.setValue(aProvision.getDueFromDate());
		this.lastFullyPaidDate.setValue(aProvision.getLastFullyPaidDate());

		this.recordStatus.setValue(aProvision.getRecordStatus());
		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		// Tabs Appending
		getFinanceDetail().setModuleDefiner(moduleDefiner);

		// Customer Details Tab
		appendCustomerDetailTab();

		// Fee Details Tab
		appendFeeDetailTab(true);

		// Schedule Details Tab Adding
		appendScheduleDetailTab(true, false);

		// Agreement Details Tab
		appendAgreementsDetailTab(true);

		// Check List Details Tab
		appendCheckListDetailTab(getFinanceDetail(), false, true);

		// Recommendation Details Tab
		appendRecommendDetailTab(true);

		// Document Details Tab
		appendDocumentDetailTab();

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			appendAccountingDetailTab(true);
		}

		logger.debug("Leaving");
	}

	@Override
	public void onSelectCheckListDetailsTab(ForwardEvent event) {
		this.doWriteComponentsToBean(provision);

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProvision
	 */
	public void doWriteComponentsToBean(Provision aProvision) {
		logger.debug("Entering");
		doSetLOVValidation();

		int format = CurrencyUtil.getFormat(aProvision.getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProvision.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvision.setFinBranch(this.finBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvision.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvision.setCustID(this.custID.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// aProvision.setLovDescCustShrtName(this.custShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// aProvision.setUseNFProv(this.useNFProv.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// aProvision.setAutoReleaseNFP(this.autoReleaseNFP.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			if (this.useNFProv.isChecked() && !this.nonFormulaProv.isDisabled()) {
				if (this.nonFormulaProv.getValue() == null || this.nonFormulaProv.doubleValue() < 0) {
					throw new WrongValueException(this.nonFormulaProv, Labels.getLabel("FIELD_NO_EMPTY_NO_NEG_NO_ZERO",
							new String[] { Labels.getLabel("label_ProvisionDialog_ProvisionAmt.value") }));
				} else if (this.nonFormulaProv.getValue().compareTo(this.principalDue.getValue()) > 0) {
					throw new WrongValueException(this.nonFormulaProv,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_ProvisionDialog_ProvisionAmt.value"),
											String.valueOf(this.principalDue.getValue()) }));
				}
			}
			// aProvision.setNonFormulaProv(PennantAppUtil.unFormateAmount(this.nonFormulaProv.getValue(), format));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		/*
		 * try { aProvision.setPrincipalDue(PennantAppUtil.unFormateAmount(this.principalDue.getValue(), format)); }
		 * catch (WrongValueException we) { wve.add(we); } try {
		 * aProvision.setProfitDue(PennantAppUtil.unFormateAmount(this.profitDue.getValue(), format)); } catch
		 * (WrongValueException we) { wve.add(we); }
		 */
		try {
			// aProvision.setProvisionAmtCal(PennantAppUtil.formateAmount(this.calProvisionedAmt.getValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvision.setProvisionedAmt(CurrencyUtil.unFormat(this.provisionedAmt.getValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProvision.setDueFromDate(this.dueFromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvision.setLastFullyPaidDate(this.lastFullyPaidDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Date appDate = SysParamUtil.getAppDate();
			aProvision.setProvisionDate(appDate);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aProvision.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aProvision
	 */
	public void doShowDialog(Provision aProvision) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aProvision.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finBranch.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				// doReadOnly();
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aProvision);
			checkNFProv();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProvisionDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (this.finReference.isButtonVisible()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ProvisionDialog_FinReference.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.finBranch.setConstraint("");
		this.finType.setConstraint("");
		this.custID.setConstraint("");
		this.principalDue.setConstraint("");
		this.profitDue.setConstraint("");
		this.nonFormulaProv.setConstraint("");
		this.calProvisionedAmt.setConstraint("");
		this.provisionedAmt.setConstraint("");
		this.dueFromDate.setConstraint("");

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a Provision object from database.
	 */
	private void doDelete() {
		//
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		if (getProvision().isNewRecord()) {
			this.finReference.setReadonly(false);
		} else {
			this.finReference.setReadonly(true);
		}

		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.custID.setReadonly(true);
		this.useNFProv.setDisabled(isReadOnly("ProvisionDialog_useNFProv"));
		this.autoReleaseNFP.setDisabled(isReadOnly("ProvisionDialog_autoReleaseNFP"));
		this.nonFormulaProv.setDisabled(isReadOnly("ProvisionDialog_nonFormulaProv"));
		this.principalDue.setDisabled(true);
		this.profitDue.setDisabled(true);
		this.calProvisionedAmt.setDisabled(true);
		this.provisionedAmt.setDisabled(true);
		this.dueFromDate.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.provision.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(false);
		}

		this.btnDelete.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.finReference.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finType.setReadonly(true);
		this.nonFormulaProv.setReadonly(true);
		this.custID.setReadonly(true);
		this.useNFProv.setDisabled(true);
		this.autoReleaseNFP.setDisabled(true);
		this.principalDue.setReadonly(true);
		this.profitDue.setReadonly(true);
		this.calProvisionedAmt.setReadonly(true);
		this.provisionedAmt.setReadonly(true);
		this.dueFromDate.setDisabled(true);

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

		this.finReference.setValue("");
		this.finBranch.setValue("");
		this.finType.setValue("");
		this.custID.setText("");
		this.nonFormulaProv.setValue("");
		this.useNFProv.setChecked(false);
		this.autoReleaseNFP.setChecked(false);
		this.principalDue.setValue("");
		this.profitDue.setValue("");
		this.calProvisionedAmt.setValue("");
		this.provisionedAmt.setValue("");
		this.dueFromDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		final Provision aProvision = new Provision();
		BeanUtils.copyProperties(getProvision(), aProvision);

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Provision object with the components data
		doWriteComponentsToBean(aProvision);

		FinanceDetail aFinanceDetail = aProvision.getFinanceDetail();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}

		aFinanceDetail.setAccountingEventCode(eventCode);
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum()
					.compareTo(getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aFinanceDetail.setStageAccountingList(null);
		}

		// Finance Accounting Details
		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (AccountingExtension.VERIFY_ACCOUNTING && !getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		// Write the additional validations as per below example
		String tranType = "";
		boolean isNew = aProvision.isNewRecord();
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProvision.getRecordType())) {
				aProvision.setVersion(aProvision.getVersion() + 1);
				if (isNew) {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProvision.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvision.setNewRecord(true);
				}
			}

		} else {
			aProvision.setVersion(aProvision.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aProvision, tranType)) {

				refreshList();

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aProvision.getNextTaskId())) {
					aProvision.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aProvision.getRoleCode(),
						aProvision.getNextRoleCode(), aProvision.getFinReference(), " Provision ",
						aProvision.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
					notification.setModule("PROVISION");
					notification.setSubModule(moduleDefiner);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, aFinanceDetail, financeMain.getFinType(),
							aFinanceDetail.getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				FinanceMain fm = aFinanceDetail.getFinScheduleData().getFinanceMain();
				if (fm.getNextUserId() != null) {
					publishNotification(Notify.USER, fm.getFinReference(), fm);
				} else {
					if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm);
					} else {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm, finDivision,
								aFinanceMain.getFinBranch());
					}
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	protected boolean doProcess(Provision aProvision, String tranType)
			throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;

		aProvision.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProvision.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvision.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aProvision.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aProvision, finishedTasks);

			if (isNotesMandatory(taskId, aProvision)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aProvision, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else {
					Provision tProvision = (Provision) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, aProvision);
					auditHeader.getAuditDetail().setModelData(tProvision);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				Provision tProvision = (Provision) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tProvision, finishedTasks);

			}

			Provision tProvision = (Provision) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tProvision);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aProvision);
					auditHeader.getAuditDetail().setModelData(tProvision);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			auditHeader = getAuditHeader(aProvision, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	protected String getServiceTasks(String taskId, Provision provision, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(provision.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, provision);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, Provision provision) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(provision.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, provision);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		provision.setTaskId(taskId);
		provision.setNextTaskId(nextTaskId);
		provision.setRoleCode(getRole());
		provision.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Provision aProvision = (Provision) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getProvisionService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getProvisionService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getProvisionService().doApprove(auditHeader);

					if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getProvisionService().doReject(auditHeader);

					if (aProvision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProvisionDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ProvisionDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(), true);
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

	public void doshowProvisionDialog() {
		logger.debug("Entering");
		doSetFieldProperties();
		FinanceDetail fd = getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		int format = CurrencyUtil.getFormat(schdData.getFinanceMain().getFinCcy());

		if (fm == null) {
			return;
		}

		this.finReference.setValue(fm.getFinReference());
		// Workflow Details
		setWorkflowDetails(fm.getFinType());
		getProvision().setWorkflowId(getWorkFlowId());
		doLoadWorkFlow(getProvision());
		getProvision().setFinReference(fm.getFinReference());
		getProvision().setFinBranch(fm.getFinBranch());
		getProvision().setFinType(fm.getFinType());
		getProvision().setCustID(fm.getCustID());
		getProvision().setCustCIF(fm.getLovDescCustCIF());
		getProvision().setCustShrtName(fm.getLovDescCustShrtName());
		Date appDate = SysParamUtil.getAppDate();
		getProvision().setDueFromDate(appDate);
		getProvision().setFinCcy(fm.getFinCcy());

		OverdueChargeRecovery odcharges = getOverdueChargeRecoveryService().getOverdueChargeRecovery(fm.getFinID());
		/*
		 * getProvision().setPrincipalDue(PennantAppUtil.formateAmount(odcharges.getLovDescCurSchPriDue(), format));
		 * getProvision().setProfitDue(PennantAppUtil.formateAmount(odcharges.getLovDescCurSchPftDue(), format));
		 */
		// Last Fully Paid Date Details
		FinanceProfitDetail detail = getProvisionService().getProfitDetailById(fm.getFinID());
		if (detail != null) {
			getProvision().setLastFullyPaidDate(detail.getFullPaidDate());
			if (getProvision().getLastFullyPaidDate() == null) {
				getProvision().setLastFullyPaidDate(fm.getFinStartDate());
			}
		} else {
			getProvision().setLastFullyPaidDate(fm.getFinStartDate());
		}

		getProvision().setFinanceDetail(fd);
		this.tabpanelsBoxIndexCenter.setVisible(true);

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (getProvision().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
			this.groupboxWf.setVisible(true);
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "ProvisionDialog", menuItemRightName);
			doShowDialog(getProvision());
		}
		this.finReference.setReadonly(true);

		logger.debug("Leaving");
	}

	private void setWorkflowDetails(String finType) {

		// Finance Maintenance Workflow Check & Assignment
		WorkFlowDetails workFlowDetails = null;
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			FinanceWorkFlow financeWorkflow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(finType,
					moduleDefiner, PennantConstants.WORFLOW_MODULE_FINANCE);// TODO: Check Promotion case
			if (financeWorkflow != null && financeWorkflow.getWorkFlowType() != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkflow.getWorkFlowType());
			}
		}

		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	}

	private void doLoadWorkFlow(Provision provision) {
		logger.debug("Entering");
		String roleCode = null;
		if (!provision.isNewRecord() && StringUtils.trimToEmpty(provision.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), provision.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(provision.isWorkflow(), provision.getWorkflowId(), provision.getNextTaskId());
		} else {
			doLoadWorkFlow(provision.isWorkflow(), provision.getWorkflowId(), null, roleCode);
		}
		logger.debug("Entering");
	}

	public void onCheck$useNFProv(Event event) {
		logger.debug("Entering" + event.toString());
		this.nonFormulaProv.setValue(BigDecimal.ZERO);
		checkNFProv();
		logger.debug("Leaving" + event.toString());
	}

	private void checkNFProv() {
		if (this.useNFProv.isChecked()) {
			this.nonFormulaProv.setDisabled(isReadOnly("ProvisionDialog_nonFormulaProv"));
		} else {
			this.nonFormulaProv.setDisabled(true);
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

	public Provision getProvision() {
		return this.provision;
	}

	public void setProvision(Provision provision) {
		this.provision = provision;
	}

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	public ProvisionService getProvisionService() {
		return this.provisionService;
	}

	public void setProvisionListCtrl(ProvisionListCtrl provisionListCtrl) {
		this.provisionListCtrl = provisionListCtrl;
	}

	public ProvisionListCtrl getProvisionListCtrl() {
		return this.provisionListCtrl;
	}

	/**
	 * Method for return Document detail list object
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		logger.debug("Entering");

		if (getFinanceDetail() != null) {
			return getFinanceDetail().getDocumentDetailsList();
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, financeMain.getScheduleMethod());
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && !"".equals(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, fianncetype.getFinCategory());
		arrayList.add(9, financeMain.getLovDescCustShrtName());
		arrayList.add(10, false);
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug("Entering");

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		getFinanceDetail().setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug("Entering");

		FinanceDetail fd = getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceProfitDetail profitDetail = financeDetailService.getFinProfitDetailsById(fm.getFinID());
		Date dateValueDate = SysParamUtil.getAppValueDate();

		aeEvent = AEAmounts.procAEAmounts(fm, schdData.getFinanceScheduleDetails(), profitDetail, eventCode,
				dateValueDate, dateValueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

		List<ReturnDataSet> returnSetEntries = null;

		aeEvent.setDataMap(dataMap);
		getEngineExecution().getAccEngineExecResults(aeEvent);

		returnSetEntries = aeEvent.getReturnDataSet();

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(returnSetEntries);
			getAccountingDetailDialogCtrl().getFinanceDetail().setReturnDataSetList(returnSetEntries);
		}

		logger.debug("Leaving");
	}

	private AuditHeader getAuditHeader(Provision aProvision, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvision.getBefImage(), aProvision);
		return new AuditHeader(aProvision.getFinReference(), null, null, null, auditDetail, aProvision.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.provision);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.provision.getFinReference());
	}

	public void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.custID.setErrorMessage("");
		this.principalDue.setErrorMessage("");
		this.profitDue.setErrorMessage("");
		this.calProvisionedAmt.setErrorMessage("");
		this.provisionedAmt.setErrorMessage("");
		this.dueFromDate.setErrorMessage("");
		this.nonFormulaProv.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected void refreshList() {
		final JdbcSearchObject<Provision> soProvision = getProvisionListCtrl().getSearchObj();
		getProvisionListCtrl().pagingProvisionList.setActivePage(0);
		getProvisionListCtrl().getPagedListWrapper().setSearchObject(soProvision);
		if (getProvisionListCtrl().listBoxProvision != null) {
			getProvisionListCtrl().listBoxProvision.getListModel();
		}
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return overdueChargeRecoveryService;
	}

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

}
