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
 * * FileName : VASConfigurationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-11-2016 * *
 * Modified Date : 29-11-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-11-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.configuration.vasconfiguration;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Configuration/VASConfiguration/vASConfigurationDialog.zul file.
 * <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VASConfigurationDialogCtrl extends GFCBaseCtrl<VASConfiguration> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(VASConfigurationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VASConfigurationDialog;
	protected Uppercasebox productCode;
	protected Textbox productDesc;
	protected ExtendedCombobox vasType;
	protected Textbox vasCategory;
	protected Combobox recAgainst;
	protected CurrencyBox vasFee;
	protected Checkbox allowFeeToModify;
	protected ExtendedCombobox manufacturer;
	protected Checkbox feeAccrued;
	protected ExtendedCombobox feeAccounting;
	protected Label label_AccrualAccounting;
	protected ExtendedCombobox accrualAccounting;
	protected Checkbox recurringType;
	protected ExtendedCombobox cancellationFeeType;
	protected Intbox freeLockPeriod;
	protected Space space_flpCalculatedOn;
	protected Combobox flpCalculatedOn;
	protected Checkbox preValidationReq;
	protected Checkbox postValidationReq;
	protected Uppercasebox shortCode;
	protected Checkbox active;
	protected Textbox remarks;

	// New Fields for Insurance
	protected Combobox modeOfPayment;
	protected Combobox allowFeeType;
	protected Checkbox medicalApplicable;

	// File Import
	protected Groupbox grpBox_FileImport;
	protected Button btnImport;
	protected Button btnUpload;
	protected Textbox txtFileName;
	protected Rows panelRows;
	protected Timer timer;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab basicDetailsTab;
	protected Tab extendedDetailsTab;
	protected Tab preValidationTab;
	protected Tab postValidationTab;
	protected Tabpanel extendedFieldTabpanel;

	protected Label preModuleDesc;
	protected Label preSubModuleDesc;

	protected Label postModuleDesc;
	protected Label postSubModuleDesc;

	protected Codemirror postValidation;
	protected Codemirror preValidation;
	protected Listbox prevalidationListbox;
	protected Listbox postValidationListbox;

	protected Grid preValidationGrid;
	protected Grid postValidationGrid;
	protected Button btnCopyTo;

	private VASConfiguration vASConfiguration;
	private transient VASConfigurationListCtrl vASConfigurationListCtrl;
	private transient ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	// ServiceDAOs / Domain Classes
	private transient VASConfigurationService vasConfigurationService;

	private JSONArray variables = new JSONArray();
	protected Button btnSimulate;
	protected Button button_pre_Simulate;
	protected Button button_post_Simulate;
	private List<String> fieldNames = new ArrayList<String>();
	protected boolean alwCopyOption = false;
	protected boolean isCopyProcess = false;
	protected boolean preScriptValidated = false;
	protected boolean postScriptValidated = false;
	private List<ValueLabel> listFlpCalculatedOn = PennantStaticListUtil.getFlpCalculatedList();

	private Configuration config = null;
	private DataEngineStatus VAS_PREMIUM_CALCULATION_UPLOAD = new DataEngineStatus("VAS_PREMIUM_CALCULATION_UPLOAD");
	private List<VASPremiumCalcDetails> premiumCalcDetList = new ArrayList<>();
	private Media media;
	private boolean isImported;
	private DataEngineConfig dataEngineConfig;

	/**
	 * default constructor.<br>
	 */
	public VASConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VASConfigurationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_VASConfigurationDialog(Event event) {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_VASConfigurationDialog);
		try {
			if (arguments.containsKey("enqModule")) {
				enqiryModule = (Boolean) arguments.get("enqModule");
			} else {
				enqiryModule = false;
			}

			this.alwCopyOption = (Boolean) arguments.get("alwCopyOption");
			this.isCopyProcess = (Boolean) arguments.get("isCopyProcess");

			// Store the before image.
			if (arguments.containsKey("vASConfiguration")) {
				this.vASConfiguration = (VASConfiguration) arguments.get("vASConfiguration");
				VASConfiguration befImage = new VASConfiguration();
				BeanUtils.copyProperties(this.vASConfiguration, befImage);
				this.vASConfiguration.setBefImage(befImage);

				setVASConfiguration(this.vASConfiguration);
			} else {
				setVASConfiguration(null);
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.vASConfiguration.isWorkflow(), this.vASConfiguration.getWorkflowId(),
					this.vASConfiguration.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "VASConfigurationDialog");
			} else {
				if (!enqiryModule) {
					getUserWorkspace().allocateAuthorities("VASConfigurationDialog");
				}
			}

			// Get the required arguments.
			if (arguments.containsKey("vASConfigurationListCtrl")) {
				setVASConfigurationListCtrl((VASConfigurationListCtrl) arguments.get("vASConfigurationListCtrl"));
			} else {
				setVASConfigurationListCtrl(null);
			}

			config = dataEngineConfig.getConfigurationByName("VAS_PREMIUM_CALCULATION_UPLOAD");
			doFillPanel(config, VAS_PREMIUM_CALCULATION_UPLOAD);

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getVASConfiguration());

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entring" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for On click action on Copy button to make Duplicate record with existing Data
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		MessageUtil.confirm(Labels.getLabel("conf.closeWindowWithoutSave"), evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				closeDialog();
				Events.postEvent("onClick$button_VASConfigurationList_NewVASConfiguration",
						vASConfigurationListCtrl.window_VASConfigurationList, this.vASConfiguration);
			}
		});

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Checking Actual Initiated Owner of the Record
	 * 
	 * @return
	 */
	private boolean isMaintainable() {
		// If workflow enabled and not first task owner then cannot maintain. Else can maintain
		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug("Entring" + event.toString());
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entring" + event.toString());
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnCancel(Event event) throws ParseException, InterruptedException {
		logger.debug("Entring" + event.toString());
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug("Entring" + event.toString());
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		// Pre Validation Checking for Validated or not
		if (StringUtils.isNotEmpty(this.preValidation.getValue().trim()) && !preScriptValidated) {
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PreValidation") }));
			return;
		}

		// Post Validation Checking for Validated or not
		if (StringUtils.isNotEmpty(this.postValidation.getValue().trim()) && !postScriptValidated) {
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PostValidation") }));
			return;
		}

		// Validation Details are correct and validated
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 */
	public void onClose$window_VASConfigurationDialog(Event event) {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.vASConfiguration);
	}

	public void onFulfill$feeAccounting(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = feeAccounting.getObject();
		if (dataObject instanceof String) {
			this.feeAccounting.setObject(null);
			this.feeAccounting.setValue("", "");
		} else {
			if (dataObject instanceof AccountingSet) {
				AccountingSet accSet = (AccountingSet) dataObject;
				this.feeAccounting.setObject(accSet);
				this.feeAccounting.setValue(accSet.getAccountSetCode(), accSet.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$accrualAccounting(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = accrualAccounting.getObject();
		if (dataObject instanceof String) {
			this.accrualAccounting.setObject(null);
			this.accrualAccounting.setValue("", "");
		} else {
			if (dataObject instanceof AccountingSet) {
				AccountingSet accSet = (AccountingSet) dataObject;
				this.accrualAccounting.setObject(accSet);
				this.accrualAccounting.setValue(accSet.getAccountSetCode(), accSet.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * Fetching the VasCategory details based on vas product type
	 */
	public void onFulfill$vasType(Event event) {
		logger.debug("Entering " + event.toString());

		this.vasType.setConstraint("");
		this.vasType.clearErrorMessage();
		Object dataObject = this.vasType.getObject();
		if (dataObject instanceof String) {
			this.vasType.setValue(dataObject.toString());
			this.vasType.setDescription("");
		} else {
			VASProductType details = (VASProductType) dataObject;
			if (details != null) {
				this.vasType.setValue(details.getProductType());
				this.vasType.setDescription(details.getProductTypeDesc());
				this.vasCategory.setValue(details.getProductCtg() + "-" + details.getProductCtgDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$cancellationFeeType(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = cancellationFeeType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.cancellationFeeType.setValue("");
			this.cancellationFeeType.setDescription("");
			this.cancellationFeeType.setAttribute("FeeTypeId", null);
		} else {
			FeeType details = (FeeType) dataObject;
			this.cancellationFeeType.setAttribute("FeeTypeId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$manufacturer(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = manufacturer.getObject();
		if (dataObject == null) {
			this.manufacturer.setValue("");
			this.manufacturer.setDescription("");
			this.manufacturer.setObject(null);
		} else {
			VehicleDealer object = (VehicleDealer) this.manufacturer.getObject();
			if (object.getId() == 0) {
				this.manufacturer.setValue("");
				this.manufacturer.setDescription("");
				this.manufacturer.setObject(null);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$freeLockPeriod(Event event) {
		logger.debug(Literal.ENTERING);
		this.flpCalculatedOn.setErrorMessage("");
		visibleComponents(this.freeLockPeriod.intValue());
		logger.debug(Literal.LEAVING);
	}

	private void visibleComponents(int flpValue) {
		logger.debug(Literal.ENTERING);
		if (flpValue >= 1) {
			space_flpCalculatedOn.setSclass("mandatory");
		} else {
			space_flpCalculatedOn.setSclass(null);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aVASConfiguration The entity that need to be render.
	 */
	public void doShowDialog(VASConfiguration aVASConfiguration) throws InterruptedException {
		logger.debug("Entering");

		if (aVASConfiguration.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.productCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.productDesc.focus();
				if (StringUtils.isNotBlank(aVASConfiguration.getRecordType())) {
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
		try {

			// fill the components with the data
			doWriteBeanToComponents(aVASConfiguration);

			int height = getContentAreaHeight();
			this.preValidationGrid.setHeight(height - 150 + "px");
			this.postValidationGrid.setHeight(height - 150 + "px");
			this.preValidation.setHeight(height - 160 + "px");
			this.postValidation.setHeight(height - 160 + "px");
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		boolean isWorkflowExists = getVasConfigurationService()
				.isWorkflowExists(this.vASConfiguration.getProductCode());

		if (this.vASConfiguration.isNewRecord()) {
			this.productCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.active.setDisabled(true);
			this.btnCopyTo.setVisible(false);
		} else {
			this.productCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			if (StringUtils.equals(vASConfiguration.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				this.active.setDisabled(true);
			} else {
				this.active.setDisabled(isReadOnly("VASConfigurationDialog_Active"));
			}
			this.btnCopyTo.setVisible(isMaintainable() && alwCopyOption);
		}

		this.productDesc.setReadonly(isReadOnly("VASConfigurationDialog_ProductDesc"));
		this.recAgainst.setDisabled(isReadOnly("VASConfigurationDialog_RecAgainst"));
		this.feeAccounting.setReadonly(isReadOnly("VASConfigurationDialog_FeeAccounting"));
		this.accrualAccounting.setReadonly(isReadOnly("VASConfigurationDialog_AccrualAccounting"));
		this.feeAccrued.setDisabled(isReadOnly("VASConfigurationDialog_FeeAccrued"));
		this.preValidationReq.setDisabled(isReadOnly("VASConfigurationDialog_PreValidationReq"));
		this.postValidationReq.setDisabled(isReadOnly("VASConfigurationDialog_PostValidationReq"));
		this.vasCategory.setReadonly(true);
		this.vasFee.setReadonly(isReadOnly("VASConfigurationDialog_VasFee"));
		this.allowFeeToModify.setDisabled(isReadOnly("VASConfigurationDialog_AllowFeeToModify"));
		this.preValidation.setReadonly(isReadOnly("VASConfigurationDialog_PreValidationReq"));
		this.postValidation.setReadonly(isReadOnly("VASConfigurationDialog_PreValidationReq"));
		this.manufacturer.setReadonly(isReadOnly("VASConfigurationDialog_Manufacturer"));
		this.recurringType.setDisabled(isReadOnly("VASConfigurationDialog_RecurringType"));
		this.freeLockPeriod.setReadonly(isReadOnly("VASConfigurationDialog_FreeLockPeriod"));
		this.remarks.setReadonly(isReadOnly("VASConfigurationDialog_Remarks"));
		this.cancellationFeeType.setReadonly(isReadOnly("VASConfigurationDialog_CancellationFeeType"));
		this.flpCalculatedOn.setDisabled(isReadOnly("VASConfigurationDialog_FLPCalculatedOn"));
		this.shortCode.setReadonly(isReadOnly("VASConfigurationDialog_ShortCode"));

		this.modeOfPayment.setDisabled(isReadOnly("VASConfigurationDialog_ModeOfPayment"));
		this.allowFeeType.setDisabled(isReadOnly("VASConfigurationDialog_AllowFeeType"));
		this.medicalApplicable.setDisabled(isReadOnly("VASConfigurationDialog_MedicalApplicable"));

		this.btnImport.setDisabled(isReadOnly("VASConfigurationDialog_FileImport"));
		this.btnUpload.setDisabled(isReadOnly("VASConfigurationDialog_btnUplaod"));
		this.txtFileName.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASConfiguration.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		if (isWorkflowExists) {
			this.btnDelete.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.productCode.setReadonly(true);
		this.recAgainst.setReadonly(true);
		this.productDesc.setReadonly(true);
		this.active.setDisabled(true);
		this.feeAccrued.setDisabled(true);
		this.recurringType.setDisabled(true);
		this.preValidationReq.setDisabled(true);
		this.postValidationReq.setDisabled(true);
		this.freeLockPeriod.setReadonly(true);
		this.vasType.setReadonly(true);
		this.vasFee.setReadonly(true);
		this.allowFeeToModify.setDisabled(true);
		this.modeOfPayment.setDisabled(true);
		this.allowFeeType.setDisabled(true);
		this.medicalApplicable.setDisabled(true);

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
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VASConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VASConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VASConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VASConfigurationDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.productCode.setMaxlength(8);
		this.productDesc.setMaxlength(50);

		this.vasType.setMandatoryStyle(true);
		this.vasType.setModuleName("VASProductType");
		this.vasType.setValueColumn("ProductType");
		this.vasType.setDescColumn("ProductTypeDesc");
		this.vasType.setValidateColumns(new String[] { "ProductType" });
		Filter[] filtersVasType = new Filter[1];
		filtersVasType[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.vasType.setFilters(filtersVasType);

		this.vasFee.setProperties(true, getCcyFormat());

		this.manufacturer.setDisplayStyle(3);
		this.manufacturer.setMandatoryStyle(true);
		this.manufacturer.setModuleName("VehicleDealer");
		this.manufacturer.setValueColumn("DealerId");
		this.manufacturer.setDescColumn("DealerName");
		this.manufacturer.setValueType(DataType.LONG);
		this.manufacturer.setValidateColumns(new String[] { "DealerId", "DealerName" });
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("DealerType", VASConsatnts.VASAGAINST_VASM, Filter.OP_EQUAL);
		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.manufacturer.setFilters(filters);

		this.feeAccounting.setMandatoryStyle(true);
		setPropertiesForAEExtCombobox(this.feeAccounting, AccountingEvent.VAS_FEE);

		this.accrualAccounting.setMandatoryStyle(true);
		setPropertiesForAEExtCombobox(this.accrualAccounting, AccountingEvent.VAS_ACCRUAL);

		this.cancellationFeeType.setMandatoryStyle(true);
		this.cancellationFeeType.setModuleName("FeeType");
		this.cancellationFeeType.setValueColumn("FeeTypeCode");
		this.cancellationFeeType.setDescColumn("FeeTypeDesc");
		this.cancellationFeeType.setValidateColumns(new String[] { "FeeTypeCode" });

		this.freeLockPeriod.setMaxlength(3);
		this.shortCode.setMaxlength(3);
		this.remarks.setMaxlength(1000);
		setStatusDetails();
		logger.debug("Leaving");
	}

	// Checking Fee Collection
	public void onCheck$feeAccrued(Event event) {
		logger.debug("Entering" + event.toString());
		doFeeAccured();
		logger.debug("Leaving" + event.toString());
	}

	private void doFeeAccured() {
		logger.debug("Entering");
		this.accrualAccounting.setErrorMessage("");
		this.accrualAccounting.setConstraint("");
		this.accrualAccounting.setDescription("");
		if (this.feeAccrued.isChecked()) {
			this.accrualAccounting.setReadonly(false);
			this.accrualAccounting.setButtonDisabled(false);
			this.accrualAccounting.setMandatoryStyle(true);
		} else {
			this.accrualAccounting.setValue("");
			this.accrualAccounting.setReadonly(true);
			this.accrualAccounting.setButtonDisabled(true);
			this.accrualAccounting.setMandatoryStyle(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Checking postValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$postValidationReq(Event event) {
		logger.debug("Entering" + event.toString());
		this.postValidationTab.setDisabled(!postValidationReq.isChecked());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Checking preValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$preValidationReq(Event event) {
		logger.debug("Entering" + event.toString());
		this.preValidationTab.setDisabled(!preValidationReq.isChecked());
		logger.debug("Leaving" + event.toString());
	}

	// Append ExtendedFieldsTab
	private void appendExtendedFieldsTab() {
		logger.debug("Entering");
		try {
			ExtendedFieldHeader extendedFieldHeader = vASConfiguration.getExtendedFieldHeader();
			if (extendedFieldHeader == null) {
				extendedFieldHeader = new ExtendedFieldHeader();
				extendedFieldHeader.setNewRecord(true);
				vASConfiguration.setExtendedFieldHeader(extendedFieldHeader);
			}
			extendedFieldHeader.setModuleName(VASConsatnts.MODULE_NAME);
			if (vASConfiguration.isNewRecord()) {
				extendedFieldHeader.setSubModuleName(vASConfiguration.getProductType());
				extendedFieldHeader.setNumberOfColumns("2");
			}
			Map<String, Object> map = new HashMap<>();
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("firstTaskRole", isFirstTask());
			map.put("newRecord", vASConfiguration.isNewRecord());
			map.put("moduleName", VASConsatnts.MODULE_NAME);

			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
					extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for setting Basic Details on Selecting Extended Details Tab
	 * 
	 * @param event
	 */
	public void onSelect$extendedDetailsTab(Event event) {
		getExtendedFieldDialogCtrl().doSetBasicDetail(VASConsatnts.MODULE_NAME, this.productCode.getValue(),
				this.productDesc.getValue());
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * 
	 * @param event
	 */
	public void onSelect$preValidationTab(Event event) {
		logger.debug("Entering" + event.toString());

		this.preModuleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.preSubModuleDesc.setValue(this.vasType.getValue());
		this.prevalidationListbox.getItems().clear();
		renderScriptFields(prevalidationListbox);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * 
	 * @param event
	 */
	public void onSelect$postValidationTab(Event event) {
		logger.debug("Entering" + event.toString());

		this.postModuleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.postSubModuleDesc.setValue(this.vasType.getValue());
		this.postValidationListbox.getItems().clear();
		renderScriptFields(postValidationListbox);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for rendering Field Details from Extended fields for Validations & Simulation
	 * 
	 * @param listbox
	 */
	private void renderScriptFields(Listbox listbox) {
		logger.debug("Entering");

		if (getExtendedFieldDialogCtrl() != null) {
			List<ExtendedFieldDetail> extFieldList = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList();
			if (extFieldList != null && !extFieldList.isEmpty()) {
				for (ExtendedFieldDetail details : extFieldList) {
					if (!StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
							&& !StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
						Listitem item = new Listitem();
						Listcell lc = new Listcell(details.getFieldName());
						lc.setParent(item);
						lc = new Listcell(details.getFieldLabel());
						lc.setParent(item);
						listbox.appendChild(item);
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPreValidate(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		if (validate(event, false, false)) {
			preScriptValidated = true;

			if (StringUtils.isNotEmpty(this.preValidation.getValue().trim())) {
				MessageUtil.confirm("NO Errors Found! Proceed With Simulation?", evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						createSimulationWindow(variables, this.preValidation.getValue());
					}
				});
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostValidate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event, true, false)) {
			postScriptValidated = true;

			if (StringUtils.isNotEmpty(this.postValidation.getValue().trim())) {
				MessageUtil.confirm("NO Errors Found! Proceed With Simulation?", evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						createSimulationWindow(variables, this.postValidation.getValue());
					}
				});
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$postValidation(Event event) {
		postScriptValidated = false;
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$preValidation(ForwardEvent event) {
		preScriptValidated = false;
	}

	/**
	 * 
	 * @param event
	 */
	public void onChange$allowFeeType(ForwardEvent event) {
		String allowFeeType = this.allowFeeType.getSelectedItem().getValue();
		this.vasFee.setValue(BigDecimal.ZERO);
		vasfeeVisibility(allowFeeType);
	}

	private void vasfeeVisibility(String allowFeeType) {
		if (VASConsatnts.VAS_ALLOWFEE_AUTO.equals(allowFeeType)) {
			this.vasFee.setReadonly(true);
			this.grpBox_FileImport.setVisible(true);
			this.txtFileName.setValue(getVASConfiguration().getFileName());
		} else {
			this.vasFee.setReadonly(isReadOnly("VASConfigurationDialog_VasFee"));
			this.grpBox_FileImport.setVisible(false);
		}
	}

	/**
	 * CALL THE RESULT ZUL FILE
	 * 
	 * @param jsonArray
	 * @throws InterruptedException
	 */
	public void createSimulationWindow(JSONArray jsonArray, String scriptRule) throws InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("variables", jsonArray);
		map.put("scriptRule", scriptRule);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralStructure/ScriptValidationResult.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS
	 * 
	 * @param event
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validate(ForwardEvent event, boolean isPostValidation, boolean bothValidations)
			throws InterruptedException {
		boolean noerrors = true;
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[0];
			// array of variables
			variables = (JSONArray) data[1];

			if (!isPostValidation) {
				return noerrors;
			}

			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (isPostValidation) {
						if (!"errors".equals(variable.get("name"))) {
							if (!fieldNames.contains(variable.get("name"))) {
								// if new variables found throw error message
								noerrors = false;
								MessageUtil.showError("Unknown Variable :" + variable.get("name"));
								return noerrors;
							} else {
								noerrors = true;
							}
						}
					} else {
						if (!"defaults".equals(variable.get("name"))) {
							if (!fieldNames.contains(variable.get("name"))) {
								// if new variables found throw error message
								noerrors = false;
								MessageUtil.showError("Unknown Variable :" + variable.get("name"));
								return noerrors;
							} else {
								noerrors = true;
							}
						}
					}
				}
				if (noerrors) {
					return validateResult(isPostValidation, bothValidations);
				}

			} else {
				for (int i = 0; i < errors.size(); i++) {
					JSONObject error = (JSONObject) errors.get(i);
					if (error != null) {
						MessageUtil.showError("Error : At Line " + error.get("line") + ",Position "
								+ error.get("character") + "\n\n" + error.get("reason").toString());
						return false;
					}
				}
			}
		} else {
			return true;
		}
		return noerrors;
	}

	/**
	 * Method for Checking script has Error Details information or not.
	 * 
	 * @param isPostValidation
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validateResult(boolean isPostValidation, boolean bothValidations) throws InterruptedException {

		if (!bothValidations) {
			if (isPostValidation) {
				if (!this.postValidation.getValue().contains("errors")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			} else {
				if (!this.preValidation.getValue().contains("defaults")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			}
		} else {
			if (StringUtils.isNotEmpty(this.preValidation.getValue())
					&& !this.preValidation.getValue().contains("defaults")) {
				MessageUtil.showError("Error Details not found in Pre Validations.");
				return false;
			} else if (StringUtils.isNotEmpty(this.postValidation.getValue())
					&& !this.postValidation.getValue().contains("errors")) {
				MessageUtil.showError("Error Details not found in Post Validations.");
				return false;
			}
		}
		return true;
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPreSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event, false, false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.preValidation.getValue());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event, true, false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.postValidation.getValue());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVASConfiguration
	 */
	public void doWriteBeanToComponents(VASConfiguration aVASConfiguration) {
		logger.debug("Entering");

		this.productCode.setValue(aVASConfiguration.getProductCode());
		this.productDesc.setValue(aVASConfiguration.getProductDesc());
		fillComboBox(this.recAgainst, aVASConfiguration.getRecAgainst(), PennantStaticListUtil.getRecAgainstTypes(),
				"");
		String vasCategory = "";
		if (aVASConfiguration.getProductCategory() != null) {
			vasCategory = aVASConfiguration.getProductCategory();
			if (aVASConfiguration.getProductCategoryDesc() != null) {
				vasCategory = vasCategory + "-" + aVASConfiguration.getProductCategoryDesc();
			}
		}
		this.vasCategory.setValue(vasCategory);
		this.feeAccrued.setChecked(aVASConfiguration.isFeeAccrued());
		this.recurringType.setChecked(aVASConfiguration.isRecurringType());
		if (!aVASConfiguration.isNewRecord()) {
			this.cancellationFeeType.setValue(StringUtils.trimToEmpty(aVASConfiguration.getFeeTypeCode()),
					StringUtils.trimToEmpty(aVASConfiguration.getFeeTypeDesc()));
			if (aVASConfiguration.getFeeType() != null) {
				this.cancellationFeeType.setAttribute("FeeTypeId", aVASConfiguration.getFeeType());
			} else {
				this.cancellationFeeType.setAttribute("FeeTypeId", null);
			}
		}
		this.freeLockPeriod.setValue(aVASConfiguration.getFreeLockPeriod());
		this.vasType.setValue(aVASConfiguration.getProductType());
		this.vasType.setDescription(aVASConfiguration.getProductTypeDesc());
		this.manufacturer.setObject(new VehicleDealer(aVASConfiguration.getManufacturerId()));
		this.manufacturer.setValue(String.valueOf(aVASConfiguration.getManufacturerId()));
		this.manufacturer.setDescription(aVASConfiguration.getManufacturerName());
		this.active.setChecked(aVASConfiguration.isActive());
		this.shortCode.setValue(aVASConfiguration.getShortCode());
		this.remarks.setValue(aVASConfiguration.getRemarks());
		this.vasFee.setValue(PennantApplicationUtil.formateAmount(aVASConfiguration.getVasFee(), getCcyFormat()));
		this.allowFeeToModify.setChecked(aVASConfiguration.isAllowFeeToModify());

		this.preValidation.setValue(aVASConfiguration.getPreValidation());
		this.postValidation.setValue(aVASConfiguration.getPostValidation());
		this.preValidationTab.setDisabled(!aVASConfiguration.isPreValidationReq());
		this.postValidationTab.setDisabled(!aVASConfiguration.isPostValidationReq());
		this.preValidationReq.setChecked(aVASConfiguration.isPreValidationReq());
		this.postValidationReq.setChecked(aVASConfiguration.isPostValidationReq());

		fillComboBox(this.modeOfPayment, aVASConfiguration.getModeOfPayment(),
				PennantStaticListUtil.getVasModeOfPayments(), "");
		fillComboBox(this.allowFeeType, aVASConfiguration.getAllowFeeType(),
				PennantStaticListUtil.getVasAllowFeeTypes(), "");
		this.medicalApplicable.setChecked(aVASConfiguration.isMedicalApplicable());
		vasfeeVisibility(aVASConfiguration.getAllowFeeType());

		if (aVASConfiguration.isNewRecord()) {
			if (isCopyProcess) {
				this.feeAccounting.setDescription(aVASConfiguration.getFeeAccountingName());
				this.accrualAccounting.setDescription(aVASConfiguration.getAccrualAccountingName());
			} else {
				this.feeAccounting.setDescription("");
				this.accrualAccounting.setDescription("");
			}
			this.active.setChecked(true);
		} else {
			if (aVASConfiguration.getProductType() != null) {
				boolean isExists = getVasConfigurationService().isVASTypeExists(aVASConfiguration.getProductType());
				if (isExists) {
					this.vasType.setReadonly(isExists);
				} else {
					this.vasType.setReadonly(isReadOnly("VASConfigurationDialog_VasType"));
				}
			}
			this.feeAccounting.setDescription(aVASConfiguration.getFeeAccountingName());
			this.accrualAccounting.setDescription(aVASConfiguration.getAccrualAccountingName());
		}
		if (aVASConfiguration.isFeeAccrued()) {
			this.accrualAccounting.setReadonly(isReadOnly("VASConfigurationDialog_AccrualAccounting"));
			this.accrualAccounting.setMandatoryStyle(true);
		} else {
			this.accrualAccounting.setReadonly(true);
			this.accrualAccounting.setMandatoryStyle(false);
		}

		// Fee Accounting
		this.feeAccounting.setObject(aVASConfiguration.getFeeAccounting());
		this.feeAccounting.setValue(aVASConfiguration.getFeeAccountingName(), aVASConfiguration.getFeeAccountingDesc());

		// Accrual Accounting
		this.accrualAccounting.setObject(aVASConfiguration.getAccrualAccounting());
		this.accrualAccounting.setValue(aVASConfiguration.getAccrualAccountingName(),
				aVASConfiguration.getAccrualAccountingDesc());

		// Default Values Setting for Script Validations
		postScriptValidated = true;
		preScriptValidated = true;
		// FLP CalculatedOn
		fillComboBox(this.flpCalculatedOn, aVASConfiguration.getFlpCalculatedOn(), listFlpCalculatedOn, "");
		visibleComponents(aVASConfiguration.getFreeLockPeriod());
		// Extended Field Details tab
		appendExtendedFieldsTab();

		// Premium Calculation File Upload
		setPremiumCalcDetList(aVASConfiguration.getPremiumCalcDetList());

		this.recordStatus.setValue(aVASConfiguration.getRecordStatus());

		logger.debug("Leaving");
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
	 */
	public void doWriteComponentsToBean(VASConfiguration aVASConfiguration, boolean validationReq) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Basic Details Tab

		// Product Code
		try {
			aVASConfiguration.setProductCode(this.productCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Product Description
		try {
			aVASConfiguration.setProductDesc(this.productDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// VAS Type
		try {
			aVASConfiguration.setProductType(this.vasType.getValue());
			aVASConfiguration.setProductTypeDesc(this.vasType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// VAS fee
		try {
			aVASConfiguration
					.setVasFee(PennantApplicationUtil.unFormateAmount(this.vasFee.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// AllowFeeToModify
		try {
			aVASConfiguration.setAllowFeeToModify(this.allowFeeToModify.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Manufaturer
		try {
			VehicleDealer object = (VehicleDealer) this.manufacturer.getObject();
			aVASConfiguration.setManufacturerId(object.getId());
			aVASConfiguration.setManufacturerName(this.manufacturer.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Recording Against
		try {
			if (this.recAgainst.getSelectedItem() != null
					&& !"#".equals(this.recAgainst.getSelectedItem().getValue())) {
				aVASConfiguration.setRecAgainst(this.recAgainst.getSelectedItem().getValue().toString());
			} else {
				if (validationReq) {
					throw new WrongValueException(this.recAgainst, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_VASConfigurationDialog_RecAgainst.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fee Accrued
		try {
			aVASConfiguration.setFeeAccrued(this.feeAccrued.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fee Accounting
		try {
			aVASConfiguration.setFeeAccounting(Long.valueOf(readValueFromAEExtCombobox(this.feeAccounting)));
			aVASConfiguration.setFeeAccountingName(this.feeAccounting.getValue());
			aVASConfiguration.setFeeAccountingDesc(this.feeAccounting.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Accrual Accounting
		try {
			aVASConfiguration.setAccrualAccounting(Long.valueOf(readValueFromAEExtCombobox(this.accrualAccounting)));
			aVASConfiguration.setAccrualAccountingName(this.accrualAccounting.getValue());
			aVASConfiguration.setAccrualAccountingDesc(this.accrualAccounting.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Recurring Type
		try {
			aVASConfiguration.setRecurringType(this.recurringType.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cancellation Fee Type
		try {
			aVASConfiguration.setFeeTypeDesc(this.cancellationFeeType.getDescription());
			aVASConfiguration.setFeeTypeCode(this.cancellationFeeType.getValue());
			this.cancellationFeeType.getValidatedValue();
			Object object = this.cancellationFeeType.getAttribute("FeeTypeId");
			if (object != null) {
				aVASConfiguration.setFeeType(Long.parseLong(object.toString()));
			} else {
				aVASConfiguration.setFeeType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Free Lock Period
		try {
			aVASConfiguration.setFreeLockPeriod(this.freeLockPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// FLP Calculated On
		try {
			if (!this.flpCalculatedOn.isDisabled() && this.freeLockPeriod.intValue() >= 1
					&& "#".equals(this.flpCalculatedOn.getSelectedItem().getValue())) {
				throw new WrongValueException(this.flpCalculatedOn, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_VASConfigurationDialog_FLPCalculatedOn.value") }));
			} else if (!this.flpCalculatedOn.isDisabled()) {
				aVASConfiguration.setFlpCalculatedOn(getComboboxValue(this.flpCalculatedOn));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Mode Of Payment
		try {
			aVASConfiguration.setModeOfPayment(this.modeOfPayment.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Allow Fee Type
		try {
			aVASConfiguration.setAllowFeeType(this.allowFeeType.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Medical Applicable
		try {
			aVASConfiguration.setMedicalApplicable(this.medicalApplicable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Pre Validation Required
		try {
			aVASConfiguration.setPreValidationReq(this.preValidationReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Post Validation Required
		try {
			aVASConfiguration.setPostValidationReq(this.postValidationReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aVASConfiguration.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Short Code
		try {
			aVASConfiguration.setShortCode(this.shortCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aVASConfiguration.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, basicDetailsTab);

		// Pre Valiadtion tab
		if (this.preValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.preValidation.getValue()) == null) {
					throw new WrongValueException(preValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PreValidation.value") }));
				}
				aVASConfiguration.setPreValidation(this.preValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aVASConfiguration.setPreValidation("");
		}

		showErrorDetails(wve, preValidationTab);

		// Post Validation
		if (this.postValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.postValidation.getValue()) == null) {
					throw new WrongValueException(postValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PostValidation.value") }));
				}
				aVASConfiguration.setPostValidation(this.postValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aVASConfiguration.setPostValidation("");
		}

		showErrorDetails(wve, postValidationTab);

		// Extended Field Details
		if (getExtendedFieldDialogCtrl() != null) {
			ExtendedFieldHeader extendedFieldHeader = getExtendedFieldDialogCtrl()
					.doSave_ExtendedFields(extendedDetailsTab);
			extendedFieldHeader.setModuleName(VASConsatnts.MODULE_NAME);
			extendedFieldHeader.setSubModuleName(aVASConfiguration.getProductCode());
			extendedFieldHeader.setTabHeading(aVASConfiguration.getProductCode());
			aVASConfiguration.setExtendedFieldHeader(extendedFieldHeader);
		}
		logger.debug("Leaving");
	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Product Code
		if (!this.productCode.isReadonly()) {
			this.productCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_ProductCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		// Product Description
		if (!this.productDesc.isReadonly()) {
			this.productDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_ProductDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// VAS Type
		if (!this.vasType.isButtonDisabled()) {
			this.vasType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASConfigurationDialog_VASType.value"), null, true, true));
		}

		// vasFee
		if (!this.vasFee.isReadonly()
				&& !(VASConsatnts.VAS_ALLOWFEE_AUTO.equals(this.allowFeeType.getSelectedItem().getValue()))) {
			this.vasFee.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_VASConfigurationDialog_VASFee.value"), getCcyFormat(), true, false));
		}

		// Fee Accounting
		if (!this.feeAccounting.isButtonDisabled()) {
			this.feeAccounting.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASConfigurationDialog_FeeAccounting.value"), null, true, true));
		}
		// Accrual Accounting
		if (this.feeAccrued.isChecked() && !this.accrualAccounting.isButtonDisabled()) {
			this.accrualAccounting.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASConfigurationDialog_AccrualAccounting.value"), null, true, true));
		}
		// Cancellation Fee Type
		if (!this.cancellationFeeType.isReadonly()) {
			this.cancellationFeeType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASConfigurationDialog_CancellationFeeType.value"), null, true, true));
		}
		// Free Lock Period
		if (!this.freeLockPeriod.isReadonly()) {
			this.freeLockPeriod.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_VASConfigurationDialog_FreeLockPeriod.value"), false, false, 0, 999));
		}

		// Manufacturer
		if (!this.manufacturer.isButtonDisabled()) {
			this.manufacturer.setConstraint(new PTStringValidator(
					Labels.getLabel("label_VASConfigurationDialog_Manufacturer.value"), null, true, true));
		}
		// Short Code
		if (!this.shortCode.isReadonly()) {
			this.shortCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_ShortCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL3, true));
		}
		// Remarks
		if (!this.remarks.isReadonly()) {
			this.remarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_VASConfigurationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		// Allow Fee types
		if (!this.allowFeeType.isDisabled()) {
			this.allowFeeType.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_VASConfigurationDialog_AllowFeeType.value"),
							PennantStaticListUtil.getVasAllowFeeTypes(), true));
		}

		// Allow Mode Of Payments
		if (!this.modeOfPayment.isDisabled()) {
			this.modeOfPayment.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_VASConfigurationDialog_ModeOfPayment.value"),
							PennantStaticListUtil.getVasModeOfPayments(), true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.productCode.setConstraint("");
		this.productDesc.setConstraint("");
		this.recAgainst.setConstraint("");
		this.modeOfPayment.setConstraint("");
		this.allowFeeType.setConstraint("");
		this.feeAccounting.setConstraint("");
		this.accrualAccounting.setConstraint("");
		this.freeLockPeriod.setConstraint("");
		this.remarks.setConstraint("");
		this.vasFee.setConstraint("");
		this.vasType.setConstraint("");
		this.manufacturer.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	public void doClearMessage() {
		logger.debug("Entering");
		this.productCode.setErrorMessage("");
		this.productDesc.setErrorMessage("");
		this.feeAccounting.setErrorMessage("");
		this.recAgainst.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.accrualAccounting.setErrorMessage("");
		this.vasFee.setErrorMessage("");
		this.vasType.setErrorMessage("");
		this.manufacturer.setErrorMessage("");
		this.cancellationFeeType.setErrorMessage("");
		this.flpCalculatedOn.setErrorMessage("");
		this.shortCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	protected void refreshList() {
		final JdbcSearchObject<VASConfiguration> soVASConfiguration = getVASConfigurationListCtrl().getSearchObject();
		getVASConfigurationListCtrl().pagingVASConfigurationList.setActivePage(0);
		getVASConfigurationListCtrl().getPagedListWrapper().setSearchObject(soVASConfiguration);
		if (getVASConfigurationListCtrl().listBoxVASConfiguration != null) {
			getVASConfigurationListCtrl().listBoxVASConfiguration.getListModel();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * 
	 */
	private void doCancel() throws ParseException, InterruptedException {
		logger.debug("Entering");

		doWriteBeanToComponents(this.vASConfiguration.getBefImage());
		doReadOnly();

		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final VASConfiguration aVASConfiguration = new VASConfiguration();
		BeanUtils.copyProperties(getVASConfiguration(), aVASConfiguration);

		doDelete(aVASConfiguration.getProductCode(), aVASConfiguration);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.productCode.setValue("");
		this.productDesc.setValue("");
		this.recAgainst.setSelectedIndex(0);
		this.modeOfPayment.setSelectedIndex(0);
		this.allowFeeType.setSelectedIndex(0);
		this.feeAccrued.setChecked(false);
		this.feeAccounting.setValue("");
		this.feeAccounting.setDescription("");
		this.accrualAccounting.setValue("");
		this.accrualAccounting.setDescription("");
		this.recurringType.setChecked(false);
		this.freeLockPeriod.setText("");
		this.preValidationReq.setChecked(false);
		this.postValidationReq.setChecked(false);
		this.active.setChecked(false);
		this.remarks.setValue("");
		this.vasFee.setValue("");
		this.vasType.setValue("");
		this.vasType.setDescription("");
		this.allowFeeToModify.setChecked(false);
		this.manufacturer.setValue("");
		this.manufacturer.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final VASConfiguration aVASConfiguration = new VASConfiguration();
		BeanUtils.copyProperties(getVASConfiguration(), aVASConfiguration);
		boolean isNew = false;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		boolean validationReq = true;
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				validationReq = false;
			}
		}
		// Validation Not required Cases excluding
		if (validationReq) {
			doClearMessage();
			doSetValidation();
		}
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aVASConfiguration, validationReq);

		// Premium Calculation Details LIst
		if (VASConsatnts.VAS_ALLOWFEE_AUTO.equals(this.allowFeeType.getSelectedItem().getValue())) {
			if (!isImported && CollectionUtils.isEmpty(getPremiumCalcDetList())) {
				MessageUtil.showError("Please upload or click on Import button to import the premium file data.");
				return;
			}
			aVASConfiguration.setPremiumCalcDetList(getPremiumCalcDetList());
		} else {
			aVASConfiguration.setPremiumCalcDetList(new ArrayList<>());
		}

		// Batch Id
		if (CollectionUtils.isNotEmpty(aVASConfiguration.getPremiumCalcDetList())) {
			aVASConfiguration.setBatchId(aVASConfiguration.getPremiumCalcDetList().get(0).getBatchId());
		}

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		isNew = aVASConfiguration.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVASConfiguration.getRecordType())) {
				aVASConfiguration.setVersion(aVASConfiguration.getVersion() + 1);
				if (isNew) {
					aVASConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASConfiguration.setNewRecord(true);
				}
			}
		} else {
			aVASConfiguration.setVersion(aVASConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aVASConfiguration, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void closeDialog() {
		super.closeDialog();

		if (getExtendedFieldDialogCtrl() != null) {
			getExtendedFieldDialogCtrl().closeDialog();
		}
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

	protected boolean doProcess(VASConfiguration aVASConfiguration, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVASConfiguration.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVASConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASConfiguration.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVASConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVASConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVASConfiguration);
				}

				if (isNotesMandatory(taskId, aVASConfiguration)) {
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

			aVASConfiguration.setTaskId(taskId);
			aVASConfiguration.setNextTaskId(nextTaskId);
			aVASConfiguration.setRoleCode(getRole());
			aVASConfiguration.setNextRoleCode(nextRoleCode);

			// Premium calculation details
			if (CollectionUtils.isNotEmpty(aVASConfiguration.getPremiumCalcDetList())) {
				for (VASPremiumCalcDetails premiumCalcDetails : aVASConfiguration.getPremiumCalcDetList()) {
					premiumCalcDetails.setProductCode(aVASConfiguration.getProductCode());
					premiumCalcDetails.setManufacturerId(aVASConfiguration.getManufacturerId());
				}
			}

			// Set workflow values
			ExtendedFieldHeader extFldHeader = aVASConfiguration.getExtendedFieldHeader();
			extFldHeader.setWorkflowId(aVASConfiguration.getWorkflowId());
			extFldHeader.setRecordStatus(aVASConfiguration.getRecordStatus());
			extFldHeader.setTaskId(aVASConfiguration.getTaskId());
			extFldHeader.setNextTaskId(aVASConfiguration.getNextTaskId());
			extFldHeader.setRoleCode(aVASConfiguration.getRoleCode());
			extFldHeader.setNextRoleCode(aVASConfiguration.getNextRoleCode());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aVASConfiguration.getRecordType())) {
				if (StringUtils.trimToNull(extFldHeader.getRecordType()) == null) {
					extFldHeader.setRecordType(aVASConfiguration.getRecordType());
					extFldHeader.setNewRecord(true);
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equals(aVASConfiguration.getRecordType())) {
				extFldHeader.setRecordType(aVASConfiguration.getRecordType());
				extFldHeader.setNewRecord(aVASConfiguration.isNewRecord());
			}

			for (ExtendedFieldDetail ext : extFldHeader.getExtendedFieldDetails()) {
				ext.setWorkflowId(aVASConfiguration.getWorkflowId());
				ext.setRecordStatus(aVASConfiguration.getRecordStatus());
				ext.setTaskId(aVASConfiguration.getTaskId());
				ext.setNextTaskId(aVASConfiguration.getNextTaskId());
				ext.setRoleCode(aVASConfiguration.getRoleCode());
				ext.setNextRoleCode(aVASConfiguration.getNextRoleCode());
				if (PennantConstants.RECORD_TYPE_DEL.equals(aVASConfiguration.getRecordType())) {
					if (StringUtils.trimToNull(ext.getRecordType()) == null) {
						ext.setRecordType(aVASConfiguration.getRecordType());
						ext.setNewRecord(true);
					}
				}
			}
			auditHeader = getAuditHeader(aVASConfiguration, tranType);

			String operationRefs = getServiceOperations(taskId, aVASConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVASConfiguration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVASConfiguration, tranType);
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
		VASConfiguration aVASConfiguration = (VASConfiguration) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getVasConfigurationService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getVasConfigurationService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getVasConfigurationService().doApprove(auditHeader);
					if (aVASConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getVasConfigurationService().doReject(auditHeader);
					if (aVASConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {

					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_VASConfigurationDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_VASConfigurationDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.vASConfiguration), true);
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

	private AuditHeader getAuditHeader(VASConfiguration aVASConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASConfiguration.getBefImage(), aVASConfiguration);
		return new AuditHeader(aVASConfiguration.getProductCode(), null, null, null, auditDetail,
				aVASConfiguration.getUserDetails(), getOverideMap());
	}

	private void setPropertiesForAEExtCombobox(ExtendedCombobox extendedCombobox, String evenCode) {
		extendedCombobox.setInputAllowed(false);
		extendedCombobox.setTextBoxWidth(90);
		extendedCombobox.setModuleName("AccountingSet");
		extendedCombobox.setValueColumn("AccountSetCode");
		extendedCombobox.setDescColumn("AccountSetCodeName");
		extendedCombobox.setValidateColumns(new String[] { "AccountSetCode" });
		extendedCombobox.setFilters(getFiltersByCheckingRIA("EventCode", evenCode, Filter.OP_EQUAL));
		extendedCombobox.setValue("", "");
		extendedCombobox.setObject(null);
	}

	private Filter[] getFiltersByCheckingRIA(String property, Object value, int operator) {
		Filter[] filter = new Filter[2];
		filter[0] = new Filter(property, value, operator);
		filter[1] = new Filter("EntryByInvestment", 0, Filter.OP_EQUAL);
		return filter;
	}

	private String readValueFromAEExtCombobox(ExtendedCombobox extendedCombobox) {
		Object obj = extendedCombobox.getObject();
		if (obj != null) {
			if (obj instanceof Long) {
				return obj.toString();
			} else if (obj instanceof AccountingSet) {
				AccountingSet accountingSet = (AccountingSet) obj;
				return String.valueOf(accountingSet.getAccountSetid());
			}
		}
		return null;
	}

	// File import related changes start//

	public void onUpload$btnUpload(UploadEvent event) {
		logger.debug(Literal.ENTERING);

		isImported = false;
		txtFileName.setText("");
		setMedia(event.getMedia());

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		txtFileName.setText(media.getName());

		logger.debug(Literal.LEAVING);
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		ProcessExecution pannel = new ProcessExecution();
		pannel.setId(config.getName());
		pannel.setBorder("normal");
		pannel.setTitle(config.getName());
		pannel.setWidth("480px");
		pannel.setProcess(ds);
		pannel.render();

		Row rows = (Row) panelRows.getLastChild();

		if (rows == null) {
			Row row = new Row();
			row.setStyle("overflow: visible !important");
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.appendChild(pannel);
			row.appendChild(hbox);
			panelRows.appendChild(row);
		} else {
			Hbox hbox = null;
			List<Hbox> item = rows.getChildren();
			hbox = (Hbox) item.get(0);
			if (hbox.getChildren().size() == 2) {
				rows = new Row();
				rows.setStyle("overflow: visible !important");
				hbox = new Hbox();
				hbox.setAlign("center");
				hbox.appendChild(pannel);
				rows.appendChild(hbox);
				panelRows.appendChild(rows);
			} else {
				hbox.appendChild(pannel);
			}
		}
	}

	public void onTimer$timer(Event event) {
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pe : list) {
					String status = pe.getProcess().getStatus();

					if (ExecutionStatus.I.name().equals(status)) {
						this.btnUpload.setDisabled(true);
						this.btnImport.setDisabled(true);
					} else {
						if (isReadOnly("VASConfigurationDialog_FileImport")) {
							this.btnUpload.setDisabled(true);
							this.btnImport.setDisabled(true);
						} else {
							this.btnUpload.setDisabled(false);
							this.btnImport.setDisabled(false);
						}
					}
					pe.render();
				}
			}
		}
	}

	// File import related changes end//

	@Override
	protected String getReference() {
		return String.valueOf(this.vASConfiguration.getId());
	}

	public VASConfiguration getVASConfiguration() {
		return this.vASConfiguration;
	}

	public void setVASConfiguration(VASConfiguration vASConfiguration) {
		this.vASConfiguration = vASConfiguration;
	}

	public void setVasConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vasConfigurationService = vASConfigurationService;
	}

	public VASConfigurationService getVasConfigurationService() {
		return this.vasConfigurationService;
	}

	public void setVASConfigurationListCtrl(VASConfigurationListCtrl vASConfigurationListCtrl) {
		this.vASConfigurationListCtrl = vASConfigurationListCtrl;
	}

	public VASConfigurationListCtrl getVASConfigurationListCtrl() {
		return this.vASConfigurationListCtrl;
	}

	public ExtendedFieldDialogCtrl getExtendedFieldDialogCtrl() {
		return extendedFieldDialogCtrl;
	}

	public void setExtendedFieldDialogCtrl(ExtendedFieldDialogCtrl extendedFieldDialogCtrl) {
		this.extendedFieldDialogCtrl = extendedFieldDialogCtrl;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public List<VASPremiumCalcDetails> getPremiumCalcDetList() {
		return premiumCalcDetList;
	}

	public void setPremiumCalcDetList(List<VASPremiumCalcDetails> premiumCalcDetList) {
		this.premiumCalcDetList = premiumCalcDetList;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

}
