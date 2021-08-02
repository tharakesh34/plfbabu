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
 * * FileName : InsuranceRebookingDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * *
 * Modified Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.insurance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.configuration.vasrecording.VASPremiumCalculation;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldRenderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * This is the controller class for the /WEB-INF/pages/Insurance/InsuranceRebookingDialog.zul file. <br>
 */
public class InsuranceRebookingDialogCtrl extends GFCBaseCtrl<VASRecording> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InsuranceRebookingDialogCtrl.class);

	protected Window window_InsuranceRebooking;
	protected Label windowTitle;

	protected ExtendedCombobox productCode;
	protected Combobox postingAgainst;
	protected Textbox primaryLinkRef;
	protected Textbox vasReference;
	protected CurrencyBox fee;
	protected CurrencyBox renewalFee;
	protected Combobox feePaymentMode;
	protected Space space_FeePaymentMode;
	protected Datebox valueDate;
	protected Datebox accrualTillDate;
	protected Datebox recurringDate;
	protected ExtendedCombobox dsaId;
	protected ExtendedCombobox dmaId;
	protected ExtendedCombobox fulfilOfficerId;
	protected ExtendedCombobox referralId;

	protected Button viewInfo;
	protected Button btnSearchSelection;

	protected Row row_Vasfee;
	protected Textbox entityCode;
	protected Label entityDesc;
	protected Row row_VASPaid;
	protected CurrencyBox paidAmt;
	protected CurrencyBox waivedAmt;

	// Basic details for re booking
	protected Groupbox gb_RebookingDetail;
	protected Textbox oldPrimaryLinkRef;
	protected Textbox oldInsuranceRef;
	protected Textbox oldPolicyNumber;
	protected Textbox customerCif;
	protected Label customerShortName;

	// Status details from Maintenance
	protected Groupbox gb_MaintenanceDetails;
	protected Textbox status;
	protected Checkbox reconciled;
	protected CurrencyBox surrenderAmount;
	protected CurrencyBox claimAmount;

	protected Combobox modeOfPayment;
	protected Combobox allowFeeType;
	protected Checkbox medicalApplicable;

	protected Checkbox termInsuranceLien;
	protected Textbox providerName;
	protected Textbox policyNumber;
	protected Row row_TermInsuranceLien;
	protected Row row_MedicalStatus;
	protected Combobox medicalStatus;

	private transient InsuranceRebookingListCtrl insuranceRebookingListCtrl;
	private VASRecording vASRecording = null;
	private VASConfiguration vASConfiguration = null;
	private transient VASPremiumCalculation vasPremiumCalculation;

	private FinanceDetail clonedFinanceDetail = null;
	private boolean vaildatePremium;

	private transient FinanceDetailService financeDetailService;
	private transient VASRecordingService vASRecordingService;
	private transient CollateralSetupService collateralSetupService;
	private transient CustomerDetailsService customerDetailsService;
	private transient JointAccountDetailService jointAccountDetailService;
	private transient VehicleDealerService vehicleDealerService;
	private transient InsuranceDetailService insuranceDetailService;

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private Window mainWindow = null;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab basicDetailsTab;
	protected Tab extendedDetailsTab;
	protected Component checkListChildWindow;

	private transient ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;

	protected String selectMethodName = "onSelectTab";
	private String moduleType = "";

	private List<FinanceCheckListReference> vasChecklists = null;
	private Map<Long, Long> selectedAnsCountMap = null;
	protected Map<String, Object> flagTypeDataMap = new HashMap<String, Object>();
	private ExtendedFieldsGenerator generator;
	protected Tabpanel extendedFieldTabPanel;
	private ScriptValidationService scriptValidationService;
	private String preValidationScript;
	private String postValidationScript;
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;

	private CustomerEMailDAO customerEMailDAO;

	private boolean newRecord = false;
	private List<JointAccountDetail> jointAccountDetails = new ArrayList<>();

	// Accounting
	private AccountEngineExecution engineExecution;
	private boolean isAccountingExecuted = true;

	private boolean rebookingProcess = false;
	private boolean maintaintanceProcess = false;

	/**
	 * default constructor.<br>
	 */
	public InsuranceRebookingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsuranceMaintananceRebookingDialog";
		super.moduleCode = "VASRecording";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceRebooking(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		setPageComponents(window_InsuranceRebooking);
		try {
			if (arguments.containsKey("module")) {
				String module = (String) arguments.get("module");
				if (StringUtils.equals("E", module)) {
					enqiryModule = true;
					moduleType = PennantConstants.MODULETYPE_ENQ;
				} else if (StringUtils.equals(VASConsatnts.STATUS_REBOOKING, module)) {
					rebookingProcess = true;
				} else if (StringUtils.equals(VASConsatnts.STATUS_MAINTAINCE, module)) {
					maintaintanceProcess = true;
				}
			}
			// Store the before image.
			if (arguments.containsKey("vASRecording")) {
				this.vASRecording = (VASRecording) arguments.get("vASRecording");
				VASRecording befImage = new VASRecording();
				BeanUtils.copyProperties(this.vASRecording, befImage);
				this.vASRecording.setBefImage(befImage);
				setVASRecording(this.vASRecording);
			} else {
				setVASRecording(null);
			}

			if (arguments.containsKey("listCtrl")) {
				setInsuranceRebookingListCtrl((InsuranceRebookingListCtrl) arguments.get("listCtrl"));
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
				}
			}

			// Render the page and display the data.
			if (!enqiryModule) {
				doLoadWorkFlow(this.vASRecording.isWorkflow(), this.vASRecording.getWorkflowId(),
						this.vASRecording.getNextTaskId());
			}
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getVASRecording());
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnDelete(Event event)
			throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException
	 */
	public void onClick$btnCancel(Event event) throws ParseException, InterruptedException, ScriptException {
		doCancel();
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.vASRecording);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.vASRecording.isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnSearchSelection.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchSelection.setDisabled(true);
		}
		this.productCode.setReadonly(true);
		this.postingAgainst.setDisabled(true);
		this.primaryLinkRef.setReadonly(true);
		this.vasReference.setReadonly(true);
		this.entityCode.setReadonly(true);

		this.feePaymentMode.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_FeePaymentMode"));
		this.valueDate.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_ValueDate"));
		this.dsaId.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_DsaId"));
		this.dmaId.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_DmaId"));
		this.fulfilOfficerId.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_FulfilOfficerId"));
		this.referralId.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_ReferralId"));
		this.waivedAmt.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_WaivedAmt"));

		this.termInsuranceLien.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_TermInsuranceLien"));
		this.providerName.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_ProviderName"));
		this.policyNumber.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_PolicyNumber"));
		this.medicalStatus.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_MedicalStatus"));
		this.medicalApplicable.setDisabled(true);

		this.modeOfPayment.setDisabled(true);
		this.allowFeeType.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASRecording.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (enqiryModule) {
				this.btnCtrl.setBtnStatus_New();
				this.btnSave.setVisible(false);
				this.btnCancel.setVisible(false);
			} else if (isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				this.btnCancel.setVisible(false);
			} else {
				this.btnCancel.setVisible(false);
			}
		}
		this.btnSave.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete()
			throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		final VASRecording aVASRecording = new VASRecording();
		BeanUtils.copyProperties(getVASRecording(), aVASRecording);
		String keyReference = Labels.getLabel("label_VASReference") + " : " + aVASRecording.getVasReference();

		doDelete(keyReference, aVASRecording);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */

	public void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.productCode.setErrorMessage("");
		this.postingAgainst.setErrorMessage("");
		this.primaryLinkRef.setErrorMessage("");
		this.vasReference.setErrorMessage("");
		this.fee.setErrorMessage("");
		this.waivedAmt.setErrorMessage("");
		this.paidAmt.setErrorMessage("");
		this.renewalFee.setErrorMessage("");
		this.feePaymentMode.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.accrualTillDate.setErrorMessage("");
		this.recurringDate.setErrorMessage("");
		this.dsaId.setErrorMessage("");
		this.dmaId.setErrorMessage("");
		this.fulfilOfficerId.setErrorMessage("");
		this.referralId.setErrorMessage("");
		this.entityCode.setErrorMessage("");

		this.providerName.setErrorMessage("");
		this.policyNumber.setErrorMessage("");
		this.medicalStatus.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		final JdbcSearchObject<VASRecording> soVASConfiguration = getInsuranceRebookingListCtrl().getSearchObject();
		getInsuranceRebookingListCtrl().pagingInsuranceRebookingList.setActivePage(0);
		getInsuranceRebookingListCtrl().getPagedListWrapper().setSearchObject(soVASConfiguration);
		if (getInsuranceRebookingListCtrl().listBoxInsuranceRebookingList != null) {
			getInsuranceRebookingListCtrl().listBoxInsuranceRebookingList.getListModel();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException
	 * 
	 */
	private void doCancel() throws ParseException, InterruptedException, ScriptException {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.vASRecording.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		final VASRecording aVASRecording = new VASRecording();
		BeanUtils.copyProperties(getVASRecording(), aVASRecording);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aVASRecording, true);

		if (!isPremiumValidated()) {
			MessageUtil.showError("Details are changed please click on premium calulation button.");
			return;
		}

		// Finance CheckList Details Saving
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aVASRecording, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aVASRecording.setCheckLists(getVASRecording().getCheckLists());
		}

		// Document Details Saving
		if (documentDetailDialogCtrl != null) {
			aVASRecording.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			aVASRecording.setDocuments(getVASRecording().getDocuments());
		}

		// Accounting Details Validations
		if (getAccountingDetailDialogCtrl() != null && isAccountingExecuted) {
			MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
			return;
		}

		// doStoreInitValues();
		isNew = aVASRecording.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVASRecording.getRecordType())) {
				aVASRecording.setVersion(aVASRecording.getVersion() + 1);
				if (isNew) {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASRecording.setNewRecord(true);
				}
			}
		} else {
			aVASRecording.setVersion(aVASRecording.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aVASRecording, tranType)) {

				// User Notifications Message/Alert
				publishNotification(Notify.ROLE, aVASRecording.getVasReference(), aVASRecording);

				refreshList();
				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aVASRecording.getRoleCode(),
						aVASRecording.getNextRoleCode(), aVASRecording.getVasReference(), " VAS  ",
						aVASRecording.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
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
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * 
	 */
	protected boolean doProcess(VASRecording aVASRecording, String tranType) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVASRecording.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVASRecording.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASRecording.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVASRecording.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVASRecording.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVASRecording);
				}

				if (isNotesMandatory(taskId, aVASRecording)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aVASRecording.setTaskId(taskId);
			aVASRecording.setNextTaskId(nextTaskId);
			aVASRecording.setRoleCode(getRole());
			aVASRecording.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (aVASRecording.getExtendedFieldRender() != null) {
				ExtendedFieldRender details = aVASRecording.getExtendedFieldRender();
				details.setReference(aVASRecording.getVasReference());
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(aVASRecording.getRecordStatus());
				details.setRecordType(aVASRecording.getRecordType());
				details.setVersion(aVASRecording.getVersion());
				details.setWorkflowId(aVASRecording.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(aVASRecording.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(aVASRecording.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			// Document Details
			if (aVASRecording.getDocuments() != null && !aVASRecording.getDocuments().isEmpty()) {
				for (DocumentDetails details : aVASRecording.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}
					details.setReferenceId(aVASRecording.getVasReference());
					details.setDocModule(VASConsatnts.MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aVASRecording.getRecordStatus());
					details.setWorkflowId(aVASRecording.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aVASRecording.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// CheckList details
			if (aVASRecording.getVasCheckLists() != null && !aVASRecording.getVasCheckLists().isEmpty()) {
				for (FinanceCheckListReference details : aVASRecording.getVasCheckLists()) {
					details.setFinReference(aVASRecording.getVasReference());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aVASRecording.getRecordStatus());
					details.setWorkflowId(aVASRecording.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aVASRecording.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			auditHeader = getAuditHeader(aVASRecording, tranType);
			String operationRefs = getServiceOperations(taskId, aVASRecording);
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVASRecording, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVASRecording, tranType);
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
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		VASRecording aVASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getVASRecordingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVASRecordingService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						// Current app date as a vas approval date
						VASRecording recording = (VASRecording) auditHeader.getAuditDetail().getModelData();
						recording.setValueDate(DateUtility.getAppDate());
						auditHeader = getVASRecordingService().doApprove(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getVASRecordingService().doReject(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InsuranceRebooking, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceRebooking, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.vASRecording), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(VASRecording aVASRecording, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASRecording.getBefImage(), aVASRecording);
		return new AuditHeader(aVASRecording.getVasReference(), null, null, null, auditDetail,
				aVASRecording.getUserDetails(), getOverideMap());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVASRecording
	 * @throws InterruptedException
	 */
	public void doShowDialog(VASRecording aVASRecording) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (aVASRecording.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fee.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.fee.focus();
				if (StringUtils.isNotBlank(aVASRecording.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				if (enqiryModule) {
					doReadOnly();
				} else {
					doEdit();
				}
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		this.paidAmt.setReadonly(true);
		this.paidAmt.setDisabled(true);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aVASRecording);
			if (enqiryModule) {
				this.windowTitle.setValue(Labels.getLabel("window_InsuranceEnquiryDialog.title"));
			} else if (rebookingProcess) {
				this.windowTitle.setValue(Labels.getLabel("window_InsuranceRebookingDialog.title"));
			} else if (maintaintanceProcess) {
				this.windowTitle.setValue(Labels.getLabel("window_InsuranceMaintenanceDialog.title"));
			}
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		if (maintaintanceProcess) {
			doReadOnlyComponents();
			this.btnDelete.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug(Literal.ENTERING);
		if (enqiryModule) {
			return;
		}

		if (!this.productCode.isButtonDisabled()) {
			this.productCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_ProductCode.value"), null, true));
		}

		if (!this.primaryLinkRef.isReadonly()) {
			this.primaryLinkRef.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASRecordingDialog_PrimaryLinkRef.value"), null, true));
		}

		if (!this.fee.isReadonly()) {
			this.fee.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_VASRecordingDialog_Fee.value"), 2, false, false));
		}

		if (!this.renewalFee.isReadonly()) {
			this.renewalFee.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_VASRecordingDialog_RenewalFee.value"), 2, false, false));
		}

		if (!this.valueDate.isDisabled()) {
			this.valueDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_VASRecordingDialog_ValueDate.value"), true,
					SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE), DateUtility.getAppDate(), true));
		}

		if (!this.accrualTillDate.isDisabled()) {
			this.accrualTillDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_VASRecordingDialog_AccrualTillDate.value"), true,
							DateUtility.getAppDate(), SysParamUtil.getValueAsDate("APP_DFT_END_DATE"), true));
		}

		if (!this.recurringDate.isDisabled()) {
			this.recurringDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_VASRecordingDialog_RecurringDate.value"),
							true, DateUtility.getAppDate(), SysParamUtil.getValueAsDate("APP_DFT_END_DATE"), true));
		}

		if (!this.dsaId.isButtonDisabled()) {
			this.dsaId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_DsaId.value"), null, false));
		}
		if (!this.dmaId.isButtonDisabled()) {
			this.dmaId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_DmaId.value"), null, false));
		}
		if (!this.fulfilOfficerId.isButtonDisabled()) {
			this.fulfilOfficerId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASRecordingDialog_FulfilOfficerId.value"), null, false));
		}
		if (!this.referralId.isButtonDisabled()) {
			this.referralId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_ReferralId.value"), null, false));
		}
		if (!this.providerName.isReadonly()) {
			this.providerName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_ProviderName.value"), null,
							this.termInsuranceLien.isChecked()));
		}
		if (!this.policyNumber.isReadonly()) {
			this.policyNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_PolicyNumber.value"), null,
							this.termInsuranceLien.isChecked()));
		}
		if (!this.medicalStatus.isDisabled()) {
			this.medicalStatus.setConstraint(
					new PTListValidator(Labels.getLabel("label_VASConfigurationDialog_MedicalStatus.value"),
							PennantStaticListUtil.getMedicalStatusList(), this.medicalApplicable.isChecked()));
		}
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVASConfiguration
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException
	 * 
	 */
	public void doWriteBeanToComponents(VASRecording aVASRecording)
			throws ParseException, InterruptedException, ScriptException {
		logger.debug(Literal.ENTERING);
		if (rebookingProcess) {
			this.gb_RebookingDetail.setVisible(true);
			this.oldPrimaryLinkRef.setValue(aVASRecording.getPrimaryLinkRef());
			if (aVASRecording.isNewRecord()) {
				this.oldInsuranceRef.setValue(aVASRecording.getVasReference());
			} else {
				this.oldInsuranceRef.setValue(aVASRecording.getOldVasReference());
			}
			aVASRecording.setOldVasReference(this.oldInsuranceRef.getValue());
			this.oldPolicyNumber.setValue(aVASRecording.getVasReference());
			this.customerCif.setValue(aVASRecording.getVasCustomer().getCustCIF());
			this.customerShortName.setValue(aVASRecording.getVasCustomer().getCustShrtName());
		} else if (maintaintanceProcess) {
			InsuranceDetails insuranceDetails = getInsuranceDetailService()
					.getInsurenceDetailsByRef(aVASRecording.getVasReference(), "_View");
			this.gb_MaintenanceDetails.setVisible(true);
			this.status.setValue(aVASRecording.getStatus());
			this.surrenderAmount.setValue(PennantAppUtil.formateAmount(aVASRecording.getCancelAmt(), getCcyFormat()));
			if (insuranceDetails != null) {
				this.reconciled
						.setChecked(InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetails.getReconStatus()));
				this.claimAmount
						.setValue(PennantAppUtil.formateAmount(insuranceDetails.getPartnerPremium(), getCcyFormat()));
			}
		}

		vASConfiguration = aVASRecording.getVasConfiguration();
		if (aVASRecording.isNewRecord()) {
			this.dsaId.setDescription("");
			this.dmaId.setDescription("");
			this.fulfilOfficerId.setDescription("");
			this.referralId.setDescription("");
			this.valueDate.setValue(DateUtility.getAppDate());
			this.accrualTillDate.setValue(DateUtility.getAppDate());
			this.recurringDate.setValue(DateUtility.getAppDate());
			this.paidAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getFee(), getCcyFormat()));
			this.medicalApplicable.setChecked(vASConfiguration.isMedicalApplicable());
		} else {
			this.dsaId.setDescription(aVASRecording.getDsaId());
			this.dmaId.setDescription(aVASRecording.getDmaId());
			this.fulfilOfficerId.setDescription(aVASRecording.getFulfilOfficerId());
			this.referralId.setDescription(aVASRecording.getReferralId());
			this.valueDate.setValue(aVASRecording.getValueDate());
			this.accrualTillDate.setValue(aVASRecording.getAccrualTillDate());
			this.recurringDate.setValue(aVASRecording.getRecurringDate());
			this.paidAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getPaidAmt(), getCcyFormat()));
			this.medicalApplicable.setChecked(aVASRecording.isMedicalApplicable());
		}

		// Medical Applicable
		setMedicalStatusVisibility(this.medicalApplicable.isChecked());

		// Vas configuration
		fillComboBox(this.modeOfPayment, vASConfiguration.getModeOfPayment(),
				PennantStaticListUtil.getVasModeOfPayments(), "");
		fillComboBox(this.allowFeeType, vASConfiguration.getAllowFeeType(), PennantStaticListUtil.getVasAllowFeeTypes(),
				"");

		// Term Insurance Lien Fields
		this.termInsuranceLien.setChecked(aVASRecording.isTermInsuranceLien());
		this.providerName.setValue(aVASRecording.getProviderName());
		this.policyNumber.setValue(aVASRecording.getPolicyNumber());
		setInsuranceLienVisibility(aVASRecording.isTermInsuranceLien());

		fillComboBox(this.medicalStatus, aVASRecording.getMedicalStatus(), PennantStaticListUtil.getMedicalStatusList(),
				"");

		// Entity code
		this.entityCode.setValue(aVASRecording.getEntityCode());
		this.entityDesc.setValue(aVASRecording.getEntityDesc());

		// Product Code
		this.productCode.setValue(aVASRecording.getProductCode());
		this.productCode.setDescription(aVASRecording.getProductDesc());

		// Primary Link Reference
		this.primaryLinkRef.setValue(aVASRecording.getPrimaryLinkRef());
		fillComboBox(this.postingAgainst, aVASRecording.getPostingAgainst(), PennantStaticListUtil.getRecAgainstTypes(),
				"");
		fillComboBox(this.feePaymentMode, aVASRecording.getFeePaymentMode(), PennantStaticListUtil.getFeeTypes(), "");

		// Vas fee
		if (vASConfiguration.isAllowFeeToModify()) {
			this.fee.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_Fee"));
		} else {
			this.fee.setReadonly(true);
		}

		this.fee.addForward("onFulfill", this.window_InsuranceRebooking, "onFeeAmountChange");
		this.fee.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getFee(), getCcyFormat()));
		this.waivedAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getWaivedAmt(), getCcyFormat()));

		// Payment Mode
		this.space_FeePaymentMode.setSclass("");
		this.feePaymentMode.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_FeePaymentMode"));
		this.feePaymentMode.setDisabled(true);

		// vasReference
		if (aVASRecording.isNewRecord() && rebookingProcess && !enqiryModule) {
			this.vasReference.setValue(ReferenceUtil.generateVASRef());
		} else {
			this.vasReference.setValue(aVASRecording.getVasReference());
		}

		// Fee Accrual Till Date
		if (vASConfiguration.isFeeAccrued() && !enqiryModule) {
			this.accrualTillDate.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_AccrualTillDate"));
		} else {
			this.accrualTillDate.setDisabled(true);
		}

		// Recurring Date
		if (vASConfiguration.isRecurringType() && !enqiryModule) {
			this.recurringDate.setDisabled(isReadOnly("InsuranceMaintananceRebookingDialog_RecurringDate"));
		} else {
			this.recurringDate.setDisabled(true);
		}

		// Renewal fee
		if (vASConfiguration.isRecurringType() && !enqiryModule) {
			this.renewalFee.setReadonly(isReadOnly("InsuranceMaintananceRebookingDialog_Fee"));
		} else {
			this.renewalFee.setReadonly(true);
		}
		this.renewalFee.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getRenewalFee(), getCcyFormat()));

		// Dsa Id
		this.dsaId.setValue(aVASRecording.getDsaId());
		this.dsaId.setDescription(aVASRecording.getDsaIdDesc());

		// Dma Id
		this.dmaId.setValue(aVASRecording.getDmaId());
		this.dmaId.setDescription(aVASRecording.getDmaIdDesc());

		// FulFilOfficer Id
		this.fulfilOfficerId.setValue(aVASRecording.getFulfilOfficerId());
		this.fulfilOfficerId.setDescription(aVASRecording.getFulfilOfficerIdDesc());

		// Referral Id
		this.referralId.setValue(aVASRecording.getReferralId());
		this.referralId.setDescription(aVASRecording.getReferralIdDesc());

		this.recordStatus.setValue(aVASRecording.getRecordStatus());

		// Extended Field Details
		appendExtendedFieldDetails(aVASRecording);

		// Agreements Detail Tab Addition
		if (!enqiryModule && !maintaintanceProcess) {
			appendAgreementsDetailTab(true);
			// CheckList Details Tab Addition
			appendCheckListDetailTab(aVASRecording);
		}

		// Document Detail Tab Addition
		if (!maintaintanceProcess) {
			appendDocumentDetailTab();
		}

		// Recommend & Comments Details Tab Addition
		appendRecommendDetailTab(true);

		// Accounting Details Tab Addition
		if (!maintaintanceProcess && !enqiryModule && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append extended field details
	 * 
	 * @throws ScriptException
	 */
	private void appendExtendedFieldDetails(VASRecording aVASRecording) throws ScriptException {
		logger.debug(Literal.ENTERING);

		// Extended Field Details auto population / Rendering into Screen
		generator = new ExtendedFieldsGenerator();
		generator.setWindow(this.window_InsuranceRebooking);
		generator.setTabpanel(extendedFieldTabPanel);
		generator.setRowWidth(220);
		generator.setCcyFormat(getCcyFormat());
		if (enqiryModule) {
			generator.setReadOnly(true);
		} else {
			generator.setReadOnly(isReadOnly("InsuranceMaintananceRebookingDialog_ExtendedFields"));
		}

		VASConfiguration vasConfiguration = aVASRecording.getVasConfiguration();
		setExtendedFieldRender(aVASRecording.getExtendedFieldRender());

		// Setting the objects list
		List<Object> objectList = new ArrayList<>();
		setObjectData(objectList);

		// Pre-Validation Checking & Setting Defaults
		Map<String, Object> fieldValuesMap = null;
		if (getExtendedFieldRender() != null && getExtendedFieldRender().getMapValues() != null) {
			fieldValuesMap = aVASRecording.getExtendedFieldRender().getMapValues();
		}

		ExtendedFieldHeader extendedFieldHeader = vasConfiguration.getExtendedFieldHeader();
		List<ExtendedFieldDetail> extendedFieldDetails = extendedFieldHeader.getExtendedFieldDetails();

		// setting the pre and post validation scripts
		setPreValidationScript(vasConfiguration.getPreValidation());
		setPostValidationScript(vasConfiguration.getPostValidation());
		setExtendedFieldHeader(extendedFieldHeader);
		aVASRecording.getRecordType();
		aVASRecording.getRecordStatus();

		String preValidationScript = vasConfiguration.getPreValidation();
		if (StringUtils.isNotEmpty(preValidationScript)) {
			ScriptErrors defaults = getScriptValidationService().setPreValidationDefaults(preValidationScript,
					fieldValuesMap);

			// Initiation of Field Value Map
			if (fieldValuesMap == null) {
				fieldValuesMap = new HashMap<>();
			}

			// Overriding Default values
			List<ScriptError> defaultList = defaults.getAll();
			for (int i = 0; i < defaultList.size(); i++) {
				ScriptError dftKeyValue = defaultList.get(i);
				if (!aVASRecording.isNewRecord()) {
					ExtendedFieldDetail detail = getFieldDetail(dftKeyValue.getProperty(), extendedFieldDetails);
					if (!detail.isVisible() || detail.isValFromScript()) {
						if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
							fieldValuesMap.remove(dftKeyValue.getProperty());
						}
						if (detail.isValFromScript()) {
							detail.setFieldList(dftKeyValue.getValue());
						}
						fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
					}
				} else {
					if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
						fieldValuesMap.remove(dftKeyValue.getProperty());
					}
					ExtendedFieldDetail detail = getFieldDetail(dftKeyValue.getProperty(), extendedFieldDetails);
					if (detail.isValFromScript()) {
						detail.setFieldList(dftKeyValue.getValue());
					}
					fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
				}
			}
		}

		if (fieldValuesMap != null) {
			generator.setFieldValueMap((Map<String, Object>) fieldValuesMap);
		}
		try {
			generator.renderWindow(extendedFieldHeader, newRecord);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// Enable and disabling the Premium amount Button
		setPremiumCalcButton(aVASRecording);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	private void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getVASRecording().getAggrements() == null || getVASRecording().getAggrements().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
			if (getVASRecording().getAggrements() != null && !getVASRecording().getAggrements().isEmpty()) {
				final Map<String, Object> map = getDefaultArguments();
				map.put("agreementList", getVASRecording().getAggrements());
				map.put("financeMainDialogCtrl", this);
				if (enqiryModule) {
					map.put("enqModule", true);
				}
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param VASRecording
	 * @param finIsNewRecord
	 * @param map
	 */
	private void appendCheckListDetailTab(VASRecording vasRecording) {
		logger.debug(Literal.ENTERING);

		boolean createTab = false;
		if (vasRecording.getCheckLists() != null && !vasRecording.getCheckLists().isEmpty()) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}

		if (vasRecording.getCheckLists() != null && !vasRecording.getCheckLists().isEmpty()) {
			boolean createcheckLsitTab = false;
			for (FinanceReferenceDetail chkList : vasRecording.getCheckLists()) {
				if (chkList.getShowInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
				if (chkList.getAllowInputInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
			}
			if (createcheckLsitTab) {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				final Map<String, Object> map = getDefaultArguments();
				map.put("checkList", getVASRecording().getCheckLists());
				map.put("finCheckRefList", getVASRecording().getVasCheckLists());
				map.put("financeMainDialogCtrl", this);
				if (enqiryModule) {
					map.put("enqModule", true);
				}
				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), map);
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);

		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);

		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getVASRecording().getDocuments());
		map.put("financeMainDialogCtrl", this);
		map.put("moduleName", VASConsatnts.MODULE_NAME);
		map.put("isEditable", !isReadOnly("InsuranceMaintananceRebookingDialog_FeePaymentMode"));
		if (enqiryModule) {
			map.put("enqModule", true);
		}
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	private void appendRecommendDetailTab(boolean onLoadProcess) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			Map<String, Object> map = getDefaultArguments();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", false);
			map.put("control", this);
			map.put("notes", getNotes(this.vASRecording));
			if (enqiryModule) {
				map.put("enqModule", true);
			}
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {

			final Map<String, Object> map = getDefaultArguments();
			map.put("vASRecording", getVASRecording());
			map.put("acSetID", vASConfiguration.getFeeAccounting());
			if (enqiryModule) {
				map.put("enqModule", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception
	 */
	private boolean doSave_CheckList(VASRecording vasRecording, boolean isForAgreementGen) throws Exception {
		logger.debug("Entering ");

		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		map.put("moduleName", VASConsatnts.MODULE_NAME);
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}
		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getVasChecklists();
		selAnsCountMap = getSelectedAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			vasRecording.setVasCheckLists(chkList);
			vasRecording.setSelAnsCountMap(selAnsCountMap);
		}
		logger.debug(Literal.LEAVING);
		return validationSuccess;

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
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVASConfiguration
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(VASRecording aVASRecording, boolean isSave) throws ParseException {
		logger.debug(Literal.ENTERING);
		// doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Product Code
		try {
			aVASRecording.setProductCode(this.productCode.getValue());
			aVASRecording.setProductDesc(this.productCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Posting Against
		try {
			if (this.postingAgainst.getSelectedItem() != null) {
				aVASRecording.setPostingAgainst(this.postingAgainst.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fee Payment Mode
		try {
			if (!this.feePaymentMode.isDisabled()) {
				isValidComboValue(this.feePaymentMode,
						Labels.getLabel("label_VASRecordingDialog_FeePaymentMode.value"));
			}
			aVASRecording.setFeePaymentMode(this.feePaymentMode.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Primary Link Reference
		try {
			aVASRecording.setPrimaryLinkRef(this.primaryLinkRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// vasReference
		try {
			aVASRecording.setVasReference(this.vasReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// VAS Fee
		try {
			aVASRecording.setFee(PennantAppUtil.unFormateAmount(this.fee.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Paid Amount
		try {
			aVASRecording.setPaidAmt(PennantAppUtil.unFormateAmount(this.paidAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Waived Amount
		try {
			aVASRecording.setWaivedAmt(PennantAppUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Renewal Fee
		try {
			aVASRecording
					.setRenewalFee(PennantAppUtil.unFormateAmount(this.renewalFee.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Value Date
		try {
			aVASRecording.setValueDate(this.valueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Accrual Till Date
		try {
			aVASRecording.setAccrualTillDate(this.accrualTillDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Recurring Date
		try {
			aVASRecording.setRecurringDate(this.recurringDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Dsa Id
		try {
			aVASRecording.setDsaId(this.dsaId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Dma Id
		try {
			aVASRecording.setDmaId(this.dmaId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FulFilOfficer Id
		try {
			aVASRecording.setFulfilOfficerId(this.fulfilOfficerId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Referral Id
		try {
			aVASRecording.setReferralId(this.referralId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Entity Code
		try {
			aVASRecording.setEntityCode(this.entityCode.getValue());
			aVASRecording.setEntityDesc(this.entityDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Term Insurance Lien
		try {
			aVASRecording.setTermInsuranceLien(this.termInsuranceLien.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Provider Name
		try {
			aVASRecording.setProviderName(this.providerName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Policy Number
		try {
			aVASRecording.setPolicyNumber(this.policyNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Medical Status
		try {
			aVASRecording.setMedicalStatus(this.medicalStatus.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Medical Applicable
		try {
			aVASRecording.setMedicalApplicable(this.medicalApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Term Insurance Lien
		try {
			aVASRecording.setTermInsuranceLien(this.termInsuranceLien.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Provider Name
		try {
			aVASRecording.setProviderName(this.providerName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Policy Number
		try {
			aVASRecording.setPolicyNumber(this.policyNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Medical Status
		try {
			aVASRecording.setMedicalStatus(this.medicalStatus.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Medical Applicable
		try {
			aVASRecording.setMedicalApplicable(this.medicalApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Vas Status
		aVASRecording.setVasStatus(rebookingProcess ? VASConsatnts.STATUS_REBOOKING : VASConsatnts.STATUS_MAINTAINCE);

		// Extended field details
		final ExtendedFieldRender aExetendedFieldRender = getExtendedFieldRender();
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		Map<String, Object> map = null;
		try {
			boolean isReadOnly = false;
			if (enqiryModule) {
				isReadOnly = true;
			} else {
				isReadOnly = isReadOnly("InsuranceMaintananceRebookingDialog_ExtendedFields");
			}

			map = generator.doSave(getExtendedFieldHeader().getExtendedFieldDetails(), isReadOnly);
			aExetendedFieldRender.setMapValues(map);
		} catch (WrongValuesException wves) {
			WrongValueException[] wvea = wves.getWrongValueExceptions();
			for (int i = 0; i < wvea.length; i++) {
				wve.add(wvea[i]);
			}
		}

		// Basic Details Error Detail
		showErrorDetails(wve, this.basicDetailsTab);

		// Post Validations for the Extended fields
		if (!enqiryModule && (!isReadOnly("InsuranceMaintananceRebookingDialog_FeePaymentMode"))) {
			if (StringUtils.isNotEmpty(getPostValidationScript())) {
				ScriptErrors postValidationErrors = getScriptValidationService()
						.getPostValidationErrors(getPostValidationScript(), map);
				// Preparing Wrong Value User UI exceptions
				showErrorDetails(postValidationErrors);
			}
		}

		if (aVASRecording.isNewRecord()) {
			aExetendedFieldRender.setSeqNo(1);
		}
		aVASRecording.setExtendedFieldRender(aExetendedFieldRender);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing UI Post validation Errors
	 * 
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		List<ScriptError> errorList = postValidationErrors.getAll();
		if (CollectionUtils.isEmpty(errorList)) {
			return;
		}
		this.basicDetailsTab.setSelected(true);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);

			if (extendedFieldTabPanel.getFellowIfAny("ad_" + error.getProperty()) != null) {
				Component component = extendedFieldTabPanel.getFellowIfAny("ad_" + error.getProperty());
				WrongValueException we = new WrongValueException(component, error.getValue());
				wve.add(we);
			}
		}

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.productCode.setConstraint("");
		this.postingAgainst.setConstraint("");
		this.primaryLinkRef.setConstraint("");
		this.vasReference.setConstraint("");
		this.fee.setConstraint("");
		this.paidAmt.setConstraint("");
		this.waivedAmt.setConstraint("");
		this.renewalFee.setConstraint("");
		this.feePaymentMode.setConstraint("");
		this.valueDate.setConstraint("");
		this.accrualTillDate.setConstraint("");
		this.recurringDate.setConstraint("");
		this.dsaId.setConstraint("");
		this.dmaId.setConstraint("");
		this.fulfilOfficerId.setConstraint("");
		this.referralId.setConstraint("");
		this.entityCode.setConstraint("");
		this.providerName.setConstraint("");
		this.policyNumber.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		doReadOnlyComponents();

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

	private void doReadOnlyComponents() {
		this.productCode.setReadonly(true);
		this.postingAgainst.setDisabled(true);
		this.primaryLinkRef.setReadonly(true);
		this.vasReference.setReadonly(true);
		this.fee.setReadonly(true);
		this.fee.setDisabled(true);
		this.renewalFee.setDisabled(true);
		this.feePaymentMode.setDisabled(true);
		this.valueDate.setDisabled(true);
		this.accrualTillDate.setDisabled(true);
		this.recurringDate.setDisabled(true);
		this.dsaId.setReadonly(true);
		this.dmaId.setReadonly(true);
		this.fulfilOfficerId.setReadonly(true);
		this.referralId.setReadonly(true);
		this.waivedAmt.setReadonly(true);
		this.viewInfo.setVisible(false);
		this.entityCode.setReadonly(true);
		this.termInsuranceLien.setDisabled(true);
		this.providerName.setReadonly(true);
		this.policyNumber.setReadonly(true);
		this.medicalStatus.setReadonly(true);
		this.medicalApplicable.setDisabled(true);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InsuranceMaintananceRebookingDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InsuranceMaintananceRebookingDialog_btnEdit"));
			this.btnDelete
					.setVisible(getUserWorkspace().isAllowed("button_InsuranceMaintananceRebookingDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsuranceMaintananceRebookingDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$productCode(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.postingAgainst.setConstraint("");
		this.postingAgainst.setErrorMessage("");
		Object dataObject = productCode.getObject();
		this.postingAgainst.setDisabled(true);
		if (dataObject == null || dataObject instanceof String) {
			this.productCode.setValue("");
			this.productCode.setDescription("");
			Comboitem comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel("");
			postingAgainst.appendChild(comboitem);
			postingAgainst.setSelectedItem(comboitem);
			this.primaryLinkRef.setValue("");
		} else {
			VASConfiguration vas = (VASConfiguration) dataObject;
			this.productCode.setValue(vas.getProductCode());
			this.productCode.setDescription("");
			String recAgainst = PennantAppUtil.getVasConfiguration(this.productCode.getValue());
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(recAgainst);
			comboitem.setLabel(recAgainst);
			postingAgainst.appendChild(comboitem);
			postingAgainst.setSelectedItem(comboitem);
			postingAgainst.setReadonly(true);
		}
		logger.debug(Literal.LEAVING + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accrualTillDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.recurringDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.productCode.setProperties("VASConfiguration", "ProductCode", "ProductDesc", true, 8);
		this.productCode.getTextbox().setWidth("180px");
		this.primaryLinkRef.setWidth("180px");
		this.dsaId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.dmaId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.fulfilOfficerId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
				LengthConstants.LEN_REFERRALID);
		this.productCode.setTextBoxWidth(145);
		this.dsaId.setTextBoxWidth(145);
		this.dmaId.setTextBoxWidth(145);
		this.fulfilOfficerId.setTextBoxWidth(145);
		this.referralId.setTextBoxWidth(145);
		this.postingAgainst.setDisabled(true);
		this.fee.setProperties(true, getCcyFormat());
		this.paidAmt.setProperties(false, getCcyFormat());
		this.paidAmt.getCcyTextBox().setWidth("180px");
		this.waivedAmt.setProperties(false, getCcyFormat());
		this.renewalFee.setProperties(false, getCcyFormat());
		this.renewalFee.setWidth("100px");

		this.surrenderAmount.setProperties(false, getCcyFormat());
		this.claimAmount.setProperties(false, getCcyFormat());
		this.surrenderAmount.setDisabled(true);
		this.claimAmount.setDisabled(true);

		this.providerName.setMaxlength(100);
		this.policyNumber.setMaxlength(50);

		this.providerName.setMaxlength(100);
		this.policyNumber.setMaxlength(50);

		logger.debug(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("dialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", VASConsatnts.MODULE_NAME);
		map.put("postAccReq", false);
		return map;
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug(Literal.LEAVING);
	}

	public void onSelectTab(ForwardEvent event) throws Exception {

		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + Literal.ENTERING);
		String module = getIDbyTab(tab.getId());
		doRemoveValidation();
		doClearMessage();

		switch (module) {

		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doWriteComponentsToBean(getVASRecording(), true);

			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			} else {
				appendAgreementsDetailTab(false);
			}
			break;

		case AssetConstants.UNIQUE_ID_CHECKLIST:
			this.doWriteComponentsToBean(getVASRecording(), true);
			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getVASRecording().getCheckLists(),
						getVASRecording().getVasCheckLists(), false);
			}
			break;
		case AssetConstants.UNIQUE_ID_DOCUMENTDETAIL:
			if (documentDetailDialogCtrl != null) {
				documentDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_ACCOUNTING:
			this.doWriteComponentsToBean(getVASRecording(), true);
			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			} else {
				appendAccountingDetailTab(false);
			}
			isAccountingExecuted = false;
			break;
		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + Literal.LEAVING);
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, VASConsatnts.MODULE_NAME);
		arrayList.add(1, this.productCode.getValue());
		arrayList.add(2, this.vasReference.getValue());
		arrayList.add(3, this.primaryLinkRef.getValue());
		arrayList.add(4, getVASRecording().getVasConfiguration().getProductTypeDesc());
		arrayList.add(5, getVASRecording().getVasConfiguration().getProductCategory());
		return arrayList;
	}

	/**
	 * Method for Fetching Document Details for Checklist processing
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		if (documentDetailDialogCtrl != null) {
			return documentDetailDialogCtrl.getDocumentDetailsList();
		}
		return new ArrayList<DocumentDetails>();
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public void executeAccounting() throws Exception {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		getVASRecording().setFee(PennantAppUtil.unFormateAmount(this.fee.getActualValue(), getCcyFormat()));
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.VAS_FEE);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Based on VAS Created Against, details will be captured
		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, getVASRecording().getPostingAgainst())) {
			FinanceMain financeMain = getFinanceDetailService()
					.getFinanceMainForBatch(getVASRecording().getPrimaryLinkRef());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setCustID(financeMain.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, getVASRecording().getPostingAgainst())) {
			Customer customer = getCustomerDetailsService().getCustomerByCIF(getVASRecording().getPrimaryLinkRef());
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCcy(customer.getCustBaseCcy());
			aeEvent.setCustID(customer.getCustID());
		} else if (StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, getVASRecording().getPostingAgainst())) {
			CollateralSetup collateralSetup = getCollateralSetupService()
					.getApprovedCollateralSetupById(getVASRecording().getPrimaryLinkRef());
			aeEvent.setCcy(collateralSetup.getCollateralCcy());
			aeEvent.setCustID(collateralSetup.getDepositorId());
		}

		VehicleDealer vehicleDealer = getVehicleDealerService().getDealerShortCodes(getVASRecording().getProductCode());
		amountCodes.setProductCode(vehicleDealer.getProductShortCode());
		amountCodes.setDealerCode(vehicleDealer.getDealerShortCode());

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		getVASRecording().getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.getAcSetIDList().add(vASConfiguration.getFeeAccounting());
		engineExecution.getAccEngineExecResults(aeEvent);
		List<ReturnDataSet> returnSetEntries = aeEvent.getReturnDataSet();
		getVASRecording().setReturnDataSetList(returnSetEntries);
		accountingSetEntries.addAll(returnSetEntries);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for action Event of Changing Waived Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFulfill$waivedAmt(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		BigDecimal vasFee = PennantApplicationUtil.unFormateAmount(this.fee.getActualValue(), getCcyFormat());
		BigDecimal waivedAmt = PennantApplicationUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat());
		if (waivedAmt.compareTo(vasFee) > 0) {
			this.waivedAmt.setValue(PennantApplicationUtil.formateAmount(vasFee, getCcyFormat()));
		}

		BigDecimal bal = vasFee
				.subtract(PennantApplicationUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat()));
		this.paidAmt.setValue(PennantApplicationUtil.formateAmount(bal, getCcyFormat()));

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for action Event of Changing VAS FEE Amount
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFeeAmountChange(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		this.paidAmt.setValue(this.fee.getActualValue());
		this.waivedAmt.setValue(BigDecimal.ZERO);
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Method for Allowing the out side the system insurance
	 * 
	 * @param event
	 */
	public void onCheck$termInsuranceLien(Event event) {
		logger.debug("Entering");
		setInsuranceLienVisibility(this.termInsuranceLien.isChecked());
		logger.debug("Leaving");
	}

	private void setInsuranceLienVisibility(boolean required) {
		this.row_TermInsuranceLien.setVisible(required);
		Clients.clearWrongValue(fee);
		this.fee.setErrorMessage("");
		if (required) {
			this.fee.setValue(BigDecimal.ZERO);
			this.fee.setReadonly(true);
		} else {
			this.fee.setReadonly(isReadOnly("VASRecordingDialog_Fee"));
		}
	}

	/**
	 * Method for Medical Status enable and disable
	 * 
	 * @param event
	 */
	public void onCheck$medicalApplicable(Event event) {
		logger.debug("Entering");
		fillComboBox(this.medicalStatus, "", PennantStaticListUtil.getMedicalStatusList(), "");
		setMedicalStatusVisibility(this.medicalApplicable.isChecked());
		logger.debug("Leaving");
	}

	private void setMedicalStatusVisibility(boolean disabled) {
		Clients.clearWrongValue(medicalStatus);
		this.medicalStatus.setErrorMessage("");
		this.medicalStatus.setDisabled(!disabled);
	}

	/*
	 * public void onChange$medicalStatus(Event event) { Clients.clearWrongValue(medicalStatus);
	 * this.medicalStatus.setErrorMessage(""); String medicalStatus = this.medicalStatus.getSelectedItem().getValue();
	 * if (VASConsatnts.VAS_MEDICALSTATUS_STANDARD.equals(medicalStatus) ||
	 * VASConsatnts.VAS_MEDICALSTATUS_REJECT.equals(medicalStatus)) { this.fee.setReadonly(true); } else {
	 * this.fee.setReadonly(isReadOnly("VASRecordingDialog_Fee")); } }
	 */

	/***********************
	 * Extended fields script execution data setup start
	 ********************/
	/**
	 * preparing the objects data for pre script execution.
	 * 
	 * @param objectList
	 */
	private void setObjectData(List<Object> objectList) {
		VasCustomer vasCustomer = this.vASRecording.getVasCustomer();
		CustomerDetails details = new CustomerDetails();
		if (vasCustomer != null && vasCustomer.getCustomerId() != 0) {
			details = getCustomerDetailsService().getCustomerDetailsById(vasCustomer.getCustomerId(), false, "_AView");
			formatCustomerData(details.getCustomer());
		} else {
			details.setCustomer(new Customer());
		}
		FinanceDetail financeDetail = new FinanceDetail();

		if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, getVASRecording().getPostingAgainst())) {
			List<JointAccountDetail> jointAccountDetails = getJointAccountDetailService()
					.getJointAccountDetailByFinRef(getVASRecording().getPrimaryLinkRef(), "_AView");
			if (CollectionUtils.isNotEmpty(jointAccountDetails)) {
				for (JointAccountDetail jointAccountDetail : jointAccountDetails) {
					CustomerDetails customerDetails = getCustomerDetailsService()
							.getCustomerDetailsById(jointAccountDetail.getCustID(), false, "_AView");
					formatCustomerData(customerDetails.getCustomer());
					jointAccountDetail.setCustomerDetails(customerDetails);
				}
				financeDetail.setJointAccountDetailList(jointAccountDetails);
			}
			FinScheduleData finScheduleData = new FinScheduleData();
			FinanceMain financeMain = getFinanceDetailService().getFinanceMain(getVASRecording().getPrimaryLinkRef(),
					"_View");
			finScheduleData.setFinanceMain(financeMain);
			financeDetail.setFinScheduleData(finScheduleData);
		} else {
			FinanceMain financeMain = new FinanceMain();
			FinScheduleData finScheduleData = new FinScheduleData();
			finScheduleData.setFinanceMain(financeMain);
			financeDetail.setFinScheduleData(finScheduleData);
			financeDetail.setJointAccountDetailList(new ArrayList<>());
		}
		financeDetail.setCustomerDetails(details);
		setClonedFinanceDetail(financeDetail);
		objectList.add(financeDetail);
		getScriptValidationService().setObjectList(objectList);
	}

	/**
	 * Formatting the customer data
	 * 
	 * @param customer
	 */
	private void formatCustomerData(Customer customer) {
		Date dob = customer.getCustDOB();
		if (dob != null) {
			customer.setCustDOB(DateUtility.getDate(DateUtility.format(dob, DateFormat.SHORT_DATE.getPattern())));
			customer.setCustomerAge(new BigDecimal(getAge(dob)));
		}
	}

	/**
	 * Calculating the age
	 * 
	 * @param dob
	 * @return
	 */
	private int getAge(Date dob) {
		if (dob == null) {
			return 0;
		}
		int years = 0;
		Date appDate = DateUtility.getAppDate();
		if (dob.compareTo(appDate) < 0) {
			int months = DateUtility.getMonthsBetween(appDate, dob);
			years = months / 12;
		}
		return years;
	}

	/**
	 * Getting the Extendedfield details
	 * 
	 * @param fileldName
	 * @return
	 */
	private ExtendedFieldDetail getFieldDetail(String fileldName, List<ExtendedFieldDetail> extendedFieldDetails) {
		for (ExtendedFieldDetail detail : extendedFieldDetails) {
			if (detail.getFieldName().equals(fileldName)) {
				return detail;
			}
		}
		return null;
	}

	private boolean isPremiumValidated() {

		if (this.termInsuranceLien.isChecked()) {
			return true;
		}

		Component component = generator.getWindow().getFellowIfAny("CALCULATEPREMIUM");
		if (component != null) {
			Button premiumCalcButton = (Button) component;
			if (premiumCalcButton.isDisabled()) {
				return true;
			} else {
				if (this.newRecord) {
					return isVaildatePremium();
				} else {
					String oldCif = null;
					String newCif = null;
					Map<String, Object> oldMap = getVASRecording().getBefImage().getExtendedFieldRender()
							.getMapValues();
					Map<String, Object> newMap = getVASRecording().getExtendedFieldRender().getMapValues();

					if (oldMap.containsKey("CUSTOMERCIF")) {
						oldCif = (String) oldMap.get("CUSTOMERCIF");
					}
					if (newMap.containsKey("CUSTOMERCIF")) {
						newCif = (String) newMap.get("CUSTOMERCIF");
					}
					if (StringUtils.equals(oldCif, newCif)) {
						return true;
					} else {
						return false;
					}
				}
			}
		} else {
			return true;
		}
	}

	// Premium calculation button enable and disable
	private void setPremiumCalcButton(VASRecording aVASRecording) {
		Component component = generator.getWindow().getFellowIfAny("CALCULATEPREMIUM");
		if (component != null) {
			Button premiumCalcButton = (Button) component;
			if (VASConsatnts.VAS_ALLOWFEE_AUTO.equals(aVASRecording.getVasConfiguration().getAllowFeeType())) {
				premiumCalcButton.setDisabled(isReadOnly("VASRecordingDialog_ExtendedFields"));
			} else {
				premiumCalcButton.setDisabled(true);
			}
		}
	}

	/**
	 * Premium calculation
	 */
	public void onClickExtbtnCALCULATEPREMIUM() {
		logger.debug(Literal.ENTERING);
		try {
			setVaildatePremium(true);
			String customerCif = null;
			int insuranceTerms = 0;
			Component component = null;
			BigDecimal loanAmt = BigDecimal.ZERO;

			// CustomerCif
			component = generator.getWindow().getFellowIfAny("ad_CUSTOMERCIF");
			if (component != null) {
				Textbox txtCustomerCif = (Textbox) component;
				customerCif = txtCustomerCif.getValue();
				if (StringUtils.isEmpty(customerCif)) {
					MessageUtil.showError(
							"Please select the Applicant type, If Applicant type is Co-Applicant then please select the Co Applicants CIF. ");
					return;
				}
			}

			// Insurance Terms
			component = generator.getWindow().getFellowIfAny("ad_INSURANCETERMS");
			if (component != null) {
				Intbox intInsuranceTerms = (Intbox) component;
				insuranceTerms = intInsuranceTerms.getValue();
				if (insuranceTerms == 0) {
					MessageUtil.showError("Please enter the insurance terms.");
					return;
				}
			}

			// Finance details Object
			if (getClonedFinanceDetail() == null) {
				MessageUtil.showError("Required details are not available for premium calculation.");
				return;
			}

			// Loan Amount
			loanAmt = getClonedFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue();
			loanAmt = PennantApplicationUtil.formateAmount(loanAmt, getCcyFormat());
			if (BigDecimal.ZERO.compareTo(loanAmt) > 0) {
				MessageUtil.showError("Loam Amount should be greater than Zero");
				return;
			}

			Customer customer = null;
			boolean customerExist = false;
			customer = getClonedFinanceDetail().getCustomerDetails().getCustomer();
			if (customer.getCustCIF().equals(customerCif)) {
				customerExist = true;
			}

			List<JointAccountDetail> accountDetailList = getClonedFinanceDetail().getJointAccountDetailList();

			if (CollectionUtils.isNotEmpty(accountDetailList)) {
				for (JointAccountDetail jointAccountDetail : accountDetailList) {
					customer = jointAccountDetail.getCustomerDetails().getCustomer();
					if (customer.getCustCIF().equals(customerCif)) {
						customerExist = true;
						break;
					}
				}
			}
			if (!customerExist) {
				MessageUtil.showError("Customer details are not available for the selected CIF.");
				return;
			}

			VASPremiumCalcDetails premiumCalcDetails = new VASPremiumCalcDetails();
			premiumCalcDetails.setCustomerAge(getAge(customer.getCustDOB()));
			premiumCalcDetails.setGender(customer.getCustGenderCode());
			premiumCalcDetails.setPolicyAge(insuranceTerms);
			premiumCalcDetails.setFinAmount(loanAmt);
			premiumCalcDetails.setProductCode(this.vASRecording.getProductCode());
			premiumCalcDetails.setFinType(getClonedFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());

			VASPremiumCalcDetails newPremiumCalcDetails = getVasPremiumCalculation()
					.getPrimiumPercentage(premiumCalcDetails);

			// Medical applicable or not checking
			if (PennantConstants.YES.equals(SysParamUtil.getValueAsString("VAS_MEDICAL_STATUS_CALCULATION_YES_NO"))
					&& (getVASRecording().isNewRecord()) && (this.vASConfiguration.isMedicalApplicable())) {
				boolean medicalApplicable = getVasPremiumCalculation().getMedicalStatus(premiumCalcDetails);
				this.medicalApplicable.setChecked(medicalApplicable);
				setMedicalStatusVisibility(medicalApplicable);
			}

			if (newPremiumCalcDetails == null) {
				MessageUtil.showError("Premium Percentage is not available for the slected input data.");
				return;
			}

			if (BigDecimal.ZERO.compareTo(newPremiumCalcDetails.getPremiumPercentage()) > 0) {
				MessageUtil.showError("Premium Percentage is not available for the slected input data.");
				return;
			}

			BigDecimal vasFee = loanAmt.multiply(newPremiumCalcDetails.getPremiumPercentage());
			vasFee = vasFee.divide(new BigDecimal(100));
			if (BigDecimal.ZERO.compareTo(vasFee) > 0) {
				MessageUtil.showError("Premium amount from the Premium calculation is less than zero.");
				this.fee.setReadonly(false);
				return;
			} else {
				this.fee.setValue(vasFee);
			}

		} catch (Exception e) {
			{
				if (e.getLocalizedMessage() != null) {
					MessageUtil.showError(e.getLocalizedMessage());
				} else {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}
	/*********************** Extended fields script execution data setup end ********************/

	// Primary link reference info
	/**
	 * When user clicks on button "Search Selection based on posting Against" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSelection(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		String postingagainst = this.postingAgainst.getSelectedItem().getValue().toString();
		doSearchSelection(postingagainst);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSearchSelection(String stmtType) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		if (VASConsatnts.VASAGAINST_CUSTOMER.equals(stmtType)) {
			final String searchText = this.primaryLinkRef.getValue();
			if (StringUtils.isNotBlank(searchText)) {
				this.custCIFSearchObject = new JdbcSearchObject<Customer>(Customer.class, getListRows());
				custCIFSearchObject.addTabelName("Customers_AEView");
				custCIFSearchObject.addField("CustCIF");
				custCIFSearchObject.addField("CustShrtName");
				custCIFSearchObject.addField("CustDOB");
				custCIFSearchObject.addField("PhoneNumber");
				custCIFSearchObject.addField("CustCRCPR");
				custCIFSearchObject.addField("CustPassportNo");
				custCIFSearchObject.addField("lovDescCustTypeCodeName");
				custCIFSearchObject.addField("CustNationality");
				custCIFSearchObject.addField("CustCtgCode");
				custCIFSearchObject.addWhereClause("CustCIF=" + "'" + searchText + "'");
			} else {
				this.custCIFSearchObject = null;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("moduleCode", moduleCode);
			map.put("enqiryModule", enqiryModule);
			map.put("DialogCtrl", this);
			map.put("filtertype", "Extended");
			map.put("searchObject", this.custCIFSearchObject);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		} else if (VASConsatnts.VASAGAINST_FINANCE.equals(stmtType)) {

			final String searchText1 = this.primaryLinkRef.getValue();
			Object dataObject = ExtendedSearchListBox.show(this.window_InsuranceRebooking, "FinanceMain", searchText1);
			if (dataObject instanceof String) {
				this.primaryLinkRef.setValue(dataObject.toString());
			} else {
				FinanceMain details = (FinanceMain) dataObject;
				if (details != null) {
					this.primaryLinkRef.setValue(details.getFinReference());
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "viewInfo" button
	 * 
	 * @param event
	 */
	public void onClick$viewInfo(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		String postingagainst = this.postingAgainst.getSelectedItem().getValue().toString();
		doSearchSlectionInfo(postingagainst);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/*
	 * Display the Customer, Loan or collateral details based on postingAgainst type
	 */
	private void doSearchSlectionInfo(String stmtType) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);

		if (this.primaryLinkRef.getValue().equals("")) {
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (VASConsatnts.VASAGAINST_CUSTOMER.equals(stmtType)) {
			map.put("custid", getVASRecording().getVasCustomer().getCustomerId());
			map.put("finReference", this.vasReference.getValue());
			map.put("finance", true);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/fincustomerdetailsenq.zul",
					getMainWindow(), map);
		} else if (VASConsatnts.VASAGAINST_FINANCE.equals(stmtType)) {
			FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
			aFinanceEnq.setFinReference(this.primaryLinkRef.getValue());
			map.put("moduleCode", moduleCode);
			map.put("fromApproved", true);
			map.put("childDialog", true);
			map.put("financeEnquiry", aFinanceEnq);
			map.put("insuranceRebookingDialog", this);
			map.put("enquiryType", "FINENQ");
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
					getMainWindow(), map);
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(stmtType)) {
			CollateralSetup collateralSetup = getCollateralSetupService()
					.getCollateralSetupByRef(this.primaryLinkRef.getValue(), "", true);
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					map);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.primaryLinkRef.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.primaryLinkRef.setValue(customer.getCustCIF());
		} else {
			this.primaryLinkRef.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	public long getCustomerIDNumber() {
		if (vASRecording.getVasCustomer() != null) {
			return vASRecording.getVasCustomer().getCustomerId();
		}
		return 0;
	}

	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * 
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {
		List<Object> custBasicDetails = null;
		if (vASRecording.getVasCustomer() != null) {
			VasCustomer vasCustomer = vASRecording.getVasCustomer();
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(vasCustomer.getCustomerId());
			custBasicDetails.add(vasCustomer.getCustCIF());
			custBasicDetails.add(vasCustomer.getCustShrtName());
		}
		return custBasicDetails;
	}

	@Override
	protected String getReference() {
		return this.vasReference.getValue();
	}

	// Getters and setters
	public VASRecording getVASRecording() {
		return this.vASRecording;
	}

	public void setVASRecording(VASRecording vASRecording) {
		this.vASRecording = vASRecording;
	}

	public void setVASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASRecordingService getVASRecordingService() {
		return this.vASRecordingService;
	}

	public InsuranceRebookingListCtrl getInsuranceRebookingListCtrl() {
		return insuranceRebookingListCtrl;
	}

	public void setInsuranceRebookingListCtrl(InsuranceRebookingListCtrl insuranceRebookingListCtrl) {
		this.insuranceRebookingListCtrl = insuranceRebookingListCtrl;
	}

	public VASConfiguration getvASConfiguration() {
		return vASConfiguration;
	}

	public void setvASConfiguration(VASConfiguration vASConfiguration) {
		this.vASConfiguration = vASConfiguration;
	}

	public Window getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public List<FinanceCheckListReference> getVasChecklists() {
		return vasChecklists;
	}

	public void setVasChecklists(List<FinanceCheckListReference> vasChecklists) {
		this.vasChecklists = vasChecklists;
	}

	public Map<Long, Long> getSelectedAnsCountMap() {
		return selectedAnsCountMap;
	}

	public void setSelectedAnsCountMap(Map<Long, Long> selectedAnsCountMap) {
		this.selectedAnsCountMap = selectedAnsCountMap;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public ExtendedFieldRenderDialogCtrl getExtendedFieldRenderDialogCtrl() {
		return extendedFieldRenderDialogCtrl;
	}

	public void setExtendedFieldRenderDialogCtrl(ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl) {
		this.extendedFieldRenderDialogCtrl = extendedFieldRenderDialogCtrl;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}

	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public String getPreValidationScript() {
		return preValidationScript;
	}

	public void setPreValidationScript(String preValidationScript) {
		this.preValidationScript = preValidationScript;
	}

	public String getPostValidationScript() {
		return postValidationScript;
	}

	public void setPostValidationScript(String postValidationScript) {
		this.postValidationScript = postValidationScript;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public CustomerEMailDAO getCustomerEMailDAO() {
		return customerEMailDAO;
	}

	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	public void setNotificationService(NotificationService notificationService) {
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public List<JointAccountDetail> getJointAccountDetails() {
		return jointAccountDetails;
	}

	public void setJointAccountDetails(List<JointAccountDetail> jointAccountDetails) {
		this.jointAccountDetails = jointAccountDetails;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public VASPremiumCalculation getVasPremiumCalculation() {
		return vasPremiumCalculation;
	}

	public void setVasPremiumCalculation(VASPremiumCalculation vasPremiumCalculation) {
		this.vasPremiumCalculation = vasPremiumCalculation;
	}

	public FinanceDetail getClonedFinanceDetail() {
		return clonedFinanceDetail;
	}

	public void setClonedFinanceDetail(FinanceDetail clonedFinanceDetail) {
		this.clonedFinanceDetail = clonedFinanceDetail;
	}

	public boolean isVaildatePremium() {
		return vaildatePremium;
	}

	public void setVaildatePremium(boolean vaildatePremium) {
		this.vaildatePremium = vaildatePremium;
	}
}