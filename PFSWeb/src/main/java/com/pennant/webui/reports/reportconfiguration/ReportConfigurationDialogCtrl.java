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
 * * FileName : ReportConfigurationDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.reports.reportconfiguration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/ReportConfiguration/reportConfigurationDialog.zul file.
 */
public class ReportConfigurationDialogCtrl extends GFCBaseCtrl<ReportConfiguration> {
	private static final long serialVersionUID = -2843265056714842214L;
	private static final Logger logger = LogManager.getLogger(ReportConfigurationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReportConfigurationDialog; // autoWired

	protected Textbox reportName; // autoWired
	protected Textbox reportHeading; // autoWired
	protected Checkbox promptRequired; // autoWired
	protected Textbox reportJasperName; // autoWired
	protected Combobox dataSourceName; // autoWired
	protected Checkbox showTempLibrary; // autoWired
	protected Textbox menuItemCode; // autoWired
	protected Checkbox alwMultiFormat;
	protected Checkbox whereCondition;

	protected Label label_ReportConfigurationDialog_ReportName;
	protected Label label_ReportConfigurationDialog_ReportHeading;
	protected Label label_ReportConfigurationDialog_PromptRequired;
	protected Label label_ReportConfigurationDialog_ReportJasperName;
	protected Label label_ReportConfigurationDialog_DataSourceName;
	protected Label label_ReportConfigurationDialog_ShowTempLibrary;
	protected Label label_ReportConfigurationDialog_MenuItemCode;
	protected Label label_ReportConfigurationDialog_AlwMultiFormat;
	protected Label label_ReportConfigurationDialog_WhereCondition;

	protected Row row_Zero;
	protected Row row_One;
	protected Row row_Two;
	protected Row row_Three;
	protected Row row_Four;

	protected Hlayout hlayout_ReportName;
	protected Hlayout hlayout_ReportHeading;
	protected Hlayout hlayout_PromptRequired;
	protected Hlayout hlayout_ReportJasperName;
	protected Hlayout hlayout_DataSourceName;
	protected Hlayout hlayout_ShowTempLibrary;
	protected Hlayout hlayout_MenuItemCode;
	protected Hlayout hlayout_AlwMultiFormat;
	protected Hlayout hlayout_WhereCondition;

	protected Space space_ReportName; // autoWired
	protected Space space_ReportHeading; // autoWired
	protected Space space_PromptRequired; // autoWired
	protected Space space_ReportJasperName; // autoWired
	protected Space space_DataSourceName; // autoWired
	protected Space space_ShowTempLibrary; // autoWired
	protected Space space_MenuItemCode; // autoWired
	protected Space space_AlwMultiFormat;
	protected Space space_WhereCondition;

	// not auto wired Var's
	private ReportConfiguration reportConfiguration; // overHanded per parameter
	private transient ReportConfigurationListCtrl reportConfigurationListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	private boolean enqModule = false;

	// ServiceDAOs / Domain Classes
	private transient ReportConfigurationService reportConfigurationService;
	private transient PagedListService pagedListService;

	// Service Details list
	protected Button btnNew_ReportFilterFields;
	protected Button btnPreviewReport;
	protected Paging pagingReportFilterFieldsList;
	protected Listbox listBoxReportFilterFields;
	// protected Listbox listBoxReportAdditionalConditions;
	private List<ReportFilterFields> reportFilterFieldsList = new ArrayList<ReportFilterFields>();
	// private List<ReportAdditionalConditions> reportAdditionalConditionsList=new
	// ArrayList<ReportAdditionalConditions>();
	private PagedListWrapper<ReportFilterFields> reportFilterFieldsPagedListWrapper;
	// private PagedListWrapper<ReportAdditionalConditions> reportAdditionalConditionsPagedListWrapper;
	private List<ValueLabel> dataSourceNamesList = PennantStaticListUtil.getDataSourceNames();

	/**
	 * default constructor.<br>
	 */
	public ReportConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReportConfigurationDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected ReportConfiguration object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ReportConfigurationDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReportConfigurationDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			setReportFilterFieldsPagedListWrapper();
			// READ OVERHANDED parameters !
			if (arguments.containsKey("reportConfiguration")) {
				this.reportConfiguration = (ReportConfiguration) arguments.get("reportConfiguration");
				ReportConfiguration befImage = new ReportConfiguration();
				BeanUtils.copyProperties(this.reportConfiguration, befImage);
				this.reportConfiguration.setBefImage(befImage);
				setReportConfiguration(this.reportConfiguration);
			} else {
				setReportConfiguration(null);
			}

			doLoadWorkFlow(this.reportConfiguration.isWorkflow(), this.reportConfiguration.getWorkflowId(),
					this.reportConfiguration.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ReportConfigurationDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			fillComboBox(dataSourceName, "", dataSourceNamesList, "");

			// READ OVERHANDED parameters !
			// we get the reportConfigurationListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete reportConfiguration here.
			if (arguments.containsKey("reportConfigurationListCtrl")) {
				setReportConfigurationListCtrl(
						(ReportConfigurationListCtrl) arguments.get("reportConfigurationListCtrl"));
			} else {
				setReportConfigurationListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getReportConfiguration());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReportConfigurationDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.reportName.setMaxlength(100);
		this.reportHeading.setMaxlength(1000);
		this.reportJasperName.setMaxlength(100);
		this.dataSourceName.setMaxlength(50);
		this.menuItemCode.setMaxlength(100);
		if (this.getReportConfiguration().isNewRecord()) {
			this.btnPreviewReport.setVisible(false);
		}

		if (isWorkFlowEnabled() && !enqModule) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");

		if (!enqModule) {
			/*
			 * this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnNew"));
			 * this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnEdit"));
			 * this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnDelete"));
			 * this.btnNew_ReportFilterFields.setVisible(true);
			 */

			this.btnNew.setVisible(true);
			this.btnEdit.setVisible(true);
			this.btnDelete.setVisible(true);
			this.btnNew_ReportFilterFields.setVisible(true);
			this.btnPreviewReport.setVisible(true);
			this.btnSave.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
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
		MessageUtil.showHelpWindow(event, window_ReportConfigurationDialog);
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPreviewReport(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doWriteComponentsToBean(getReportConfiguration());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("ReportConfiguration", getReportConfiguration());
		map.put("dialogWindow", this.window_ReportConfigurationDialog);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.reportConfiguration.getBefImage());
		doReadOnly(true);
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		flag = false;
		this.listBoxReportFilterFields.setTooltiptext("");
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReportConfiguration reportConfiguration
	 */
	public void doWriteBeanToComponents(ReportConfiguration aReportConfiguration) {
		logger.debug("Entering ");
		this.reportName.setValue(aReportConfiguration.getReportName());
		this.reportHeading.setValue(aReportConfiguration.getReportHeading());
		if (aReportConfiguration.isNewRecord()) {
			this.promptRequired.setChecked(true);
		} else {
			this.promptRequired.setChecked(aReportConfiguration.isPromptRequired());
			if (aReportConfiguration.isPromptRequired()) {
				this.showTempLibrary.setChecked(aReportConfiguration.isShowTempLibrary());
			} else {
				this.showTempLibrary.setDisabled(true);
			}
			if (aReportConfiguration.isPromptRequired()) {
				this.alwMultiFormat.setChecked(aReportConfiguration.isAlwMultiFormat());
			} else {
				this.alwMultiFormat.setDisabled(true);
			}
			this.dataSourceName.setValue(
					PennantApplicationUtil.getLabelDesc(String.valueOf(aReportConfiguration.getDataSourceName()),
							PennantStaticListUtil.getDataSourceNames()));
		}
		this.reportJasperName.setValue(aReportConfiguration.getReportJasperName());

		this.menuItemCode.setValue(aReportConfiguration.getMenuItemCode());
		if (aReportConfiguration.isNewRecord()) {
			this.whereCondition.setChecked(true);
		} else {
			this.whereCondition.setChecked(aReportConfiguration.isWhereCondition());
		}
		this.recordStatus.setValue(aReportConfiguration.getRecordStatus());
		if (aReportConfiguration.getListReportFieldsDetails() != null
				&& aReportConfiguration.getListReportFieldsDetails().size() > 0) {
			doFillReportFilterFieldsList(aReportConfiguration.getListReportFieldsDetails());
		}

		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReportConfiguration
	 */
	public void doWriteComponentsToBean(ReportConfiguration aReportConfiguration) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReportConfiguration.setReportName(this.reportName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setReportHeading(this.reportHeading.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setPromptRequired(this.promptRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setReportJasperName(this.reportJasperName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setDataSourceName(this.dataSourceName.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aReportConfiguration.setShowTempLibrary(this.showTempLibrary.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			aReportConfiguration.setAlwMultiFormat(this.alwMultiFormat.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aReportConfiguration.setMenuItemCode(this.menuItemCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aReportConfiguration.setWhereCondition(this.whereCondition.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		aReportConfiguration.setListReportFieldsDetails(this.reportFilterFieldsList);
		reportFilterFieldsList.equals(this.menuItemCode.getValue());

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aReportConfiguration.setRecordStatus(this.recordStatus.getValue());
		setReportConfiguration(aReportConfiguration);
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aReportConfiguration
	 */
	public void doShowDialog(ReportConfiguration aReportConfiguration) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (enqModule) {
			doReadOnly(true);
		} else if (aReportConfiguration.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.reportName.focus();
		} else {
			this.reportName.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aReportConfiguration);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReportConfigurationDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.reportName.isReadonly()) {
			this.reportName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportName.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.reportHeading.isReadonly()) {
			this.reportHeading.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportHeading.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.reportJasperName.isReadonly()) {
			this.reportJasperName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportJasperName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE_SPACE, true));
		}
		if (!this.dataSourceName.isDisabled()) {
			this.dataSourceName.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_ReportConfigurationDialog_DataSourceName.value"),
							dataSourceNamesList, true));
		}
		if (!this.menuItemCode.isReadonly()) {
			this.menuItemCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_MenuItemCode.value"),
							PennantRegularExpressions.REGEX_ALPHA_CODE, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.reportName.setConstraint("");
		this.reportHeading.setConstraint("");
		this.reportJasperName.setConstraint("");
		this.dataSourceName.setConstraint("");
		this.menuItemCode.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.reportName.setErrorMessage("");
		this.reportHeading.setErrorMessage("");
		this.reportJasperName.setErrorMessage("");
		this.dataSourceName.setErrorMessage("");
		this.menuItemCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getReportConfigurationListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a ReportConfiguration object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ReportConfiguration aReportConfiguration = new ReportConfiguration();
		BeanUtils.copyProperties(getReportConfiguration(), aReportConfiguration);

		String keyReference = Labels.getLabel("label_ReportConfigurationDialog_ReportName.value") + " : "
				+ aReportConfiguration.getReportName();

		doDelete(keyReference, aReportConfiguration);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	boolean flag;

	private void doEdit() {
		logger.debug("Entering ");
		doReadOnly(false);
		if (getReportConfiguration().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnNew_ReportFilterFields.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnNew_ReportFilterFields.setDisabled(false);
			this.listBoxReportFilterFields.setTooltiptext("Use double click for editing");
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.reportConfiguration.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			if (flag = true) {
				List<Component> items = listBoxReportFilterFields.getChildren();
				for (Component component : items) {
					if (component instanceof Listitem) {
						((Listitem) component).setDisabled(false);
					}
				}
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering ");

		boolean tempReadOnly = readOnly;
		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.reportConfiguration.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("ReportConfigurationDialog_reportName", tempReadOnly, this.reportName,
				this.space_ReportName, this.label_ReportConfigurationDialog_ReportName, this.hlayout_ReportName, null);
		setComponentAccessType("ReportConfigurationDialog_reportHeading", tempReadOnly, this.reportHeading,
				this.space_ReportHeading, this.label_ReportConfigurationDialog_ReportHeading,
				this.hlayout_ReportHeading, null);
		setRowInvisible(this.row_Zero, this.hlayout_ReportName, this.hlayout_ReportHeading);

		setComponentAccessType("ReportConfigurationDialog_promptRequired", tempReadOnly, this.promptRequired, null,
				this.label_ReportConfigurationDialog_PromptRequired, this.hlayout_PromptRequired, null);

		setComponentAccessType("ReportConfigurationDialog_showTempLibrary", tempReadOnly, this.showTempLibrary,
				this.space_ShowTempLibrary, this.label_ReportConfigurationDialog_ShowTempLibrary,
				this.hlayout_ShowTempLibrary, this.row_One);
		// setRowInvisible(this.row_One,this.hlayout_PromptRequired, this.hlayout_ShowTempLibrary);
		setComponentAccessType("ReportConfigurationDialog_reportJasperName", tempReadOnly, this.reportJasperName,
				this.space_ReportJasperName, this.label_ReportConfigurationDialog_ReportJasperName,
				this.hlayout_ReportJasperName, null);

		setComponentAccessType("ReportConfigurationDialog_dataSourceName", tempReadOnly, this.dataSourceName,
				this.space_DataSourceName, this.label_ReportConfigurationDialog_DataSourceName,
				this.hlayout_DataSourceName, null);
		setRowInvisible(this.row_Two, this.hlayout_ReportJasperName, this.hlayout_DataSourceName);

		setComponentAccessType("ReportConfigurationDialog_menuItemCode", tempReadOnly, this.menuItemCode,
				this.space_MenuItemCode, this.label_ReportConfigurationDialog_MenuItemCode, this.hlayout_MenuItemCode,
				this.row_Three);

		setComponentAccessType("ReportConfigurationDialog_alwMultiFormat", tempReadOnly, this.alwMultiFormat,
				this.space_AlwMultiFormat, this.label_ReportConfigurationDialog_AlwMultiFormat,
				this.hlayout_AlwMultiFormat, this.row_Three);

		setComponentAccessType("ReportConfigurationDialog_WhereCondition", tempReadOnly, this.whereCondition,
				this.space_WhereCondition, this.label_ReportConfigurationDialog_WhereCondition,
				this.hlayout_WhereCondition, this.row_Four);
		// setRowInvisible(this.row_Three,this.hlayout_PromptRequired, this.hlayout_AlwMultiFormat);
		this.btnPreviewReport.setDisabled(false);
		this.btnNew_ReportFilterFields.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before

		this.reportName.setValue("");
		this.reportHeading.setValue("");
		this.promptRequired.setValue("");
		this.reportJasperName.setValue("");
		this.dataSourceName.setValue("");
		this.showTempLibrary.setValue("");
		this.menuItemCode.setValue("");
		this.alwMultiFormat.setValue("");
		this.whereCondition.setValue("");

		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final ReportConfiguration aReportConfiguration = new ReportConfiguration();
		BeanUtils.copyProperties(getReportConfiguration(), aReportConfiguration);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(reportConfiguration.getRecordType())) {
			doSetValidation();
			// fill the reportConfiguration object with the components data
			doWriteComponentsToBean(aReportConfiguration);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aReportConfiguration.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReportConfiguration.getRecordType())) {
				aReportConfiguration.setVersion(aReportConfiguration.getVersion() + 1);
				if (isNew) {
					aReportConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReportConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReportConfiguration.setNewRecord(true);
				}
			}
		} else {
			aReportConfiguration.setVersion(aReportConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aReportConfiguration, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReportConfiguration (ReportConfiguration)
	 * 
	 * @param tranType             (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ReportConfiguration aReportConfiguration, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReportConfiguration.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReportConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReportConfiguration.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReportConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReportConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReportConfiguration);
				}

				if (isNotesMandatory(taskId, aReportConfiguration)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aReportConfiguration.setTaskId(taskId);
			aReportConfiguration.setNextTaskId(nextTaskId);
			aReportConfiguration.setRoleCode(getRole());
			aReportConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aReportConfiguration, tranType);

			String operationRefs = getServiceOperations(taskId, aReportConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReportConfiguration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aReportConfiguration, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReportConfiguration aReportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getReportConfigurationService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getReportConfigurationService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getReportConfigurationService().doApprove(auditHeader);

					if (aReportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getReportConfigurationService().doReject(auditHeader);
					if (aReportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ReportConfigurationDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_ReportConfigurationDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.reportConfiguration), true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// OnChange Events

	/**
	 * When user clicks on "btnNew_ReportFilterFields"
	 * 
	 * @param event
	 */
	public void onClick$btnNew_ReportFilterFields(Event event) {
		logger.debug("Entering " + event.toString());

		ReportFilterFields reportFilterFields = new ReportFilterFields();
		reportFilterFields.setNewRecord(true);
		reportFilterFields.setWorkflowId(0);
		if (getReportConfiguration().getListReportFieldsDetails() != null) {
			long fieldID = getReportConfiguration().getListReportFieldsDetails().size();
			reportFilterFields.setFieldID(fieldID + 1);
		} else {
			reportFilterFields.setFieldID(1);
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportConfigurationDialogCtrl", this);
		map.put("reportFilterFields", reportFilterFields);
		map.put("reportConfiguration", getReportConfiguration());
		map.put("newRecord", "true");
		map.put("roleCode", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportFilterFieldsDialog.zul", null,
					map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	// OnChange Events

	/**
	 * When user double clicks "ReportFilterFields"
	 * 
	 * @param event
	 */
	public void onReportFilterFieldsItemDoubleClicked(Event event) {
		logger.debug("Entering " + event.toString());

		final Listitem item = this.listBoxReportFilterFields.getSelectedItem();
		if (item != null) {
			final ReportFilterFields reportFilterFields = (ReportFilterFields) item.getAttribute("data");

			final Map<String, Object> map = new HashMap<String, Object>();
			reportFilterFields.setNewRecord(false);
			map.put("reportConfigurationDialogCtrl", this);
			map.put("reportFilterFields", reportFilterFields);
			map.put("reportConfiguration", getReportConfiguration());
			map.put("roleCode", getRole());
			map.put("listBoxReportFilterFields", listBoxReportFilterFields);
			if (enqModule) {
				map.put("enqModule", true);
			} else {
				map.put("enqModule", false);
			}

			try {
				Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportFilterFieldsDialog.zul",
						null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method fills ReportFilterFieldsList
	 * 
	 * @param expenseDetails
	 */
	public void doFillReportFilterFieldsList(List<ReportFilterFields> reprtFiltrFieldsList) {
		logger.debug("Entering ");
		setReportFilterFieldsList(reprtFiltrFieldsList);
		this.reportFilterFieldsPagedListWrapper.initList(reportFilterFieldsList, this.listBoxReportFilterFields,
				new Paging());
		this.listBoxReportFilterFields.setItemRenderer(new ReportFilterFieldsListModelItemRenderer());
		getReportConfiguration().setListReportFieldsDetails(reprtFiltrFieldsList);

		logger.debug("Leaving ");
	}

	/**
	 * This method fills ReportFilterFieldsList
	 * 
	 * @param expenseDetails
	 *//*
		 * public void doFillReportAdditionalConditionsList(List<ReportAdditionalConditions>
		 * reportAdditionalConditions){ logger.debug("Entering ");
		 * 
		 * setReportAdditionalConditionsList(reportAdditionalConditionsList);
		 * this.reportAdditionalConditionsPagedListWrapper.initList(reportAdditionalConditionsList,this.
		 * listBoxReportAdditionalConditions, new Paging());
		 * getReportConfiguration().setListReportAdditionalConditionsDetails(reportAdditionalConditionsList);
		 * 
		 * logger.debug("Leaving "); }
		 */

	/**
	 * Item renderer for listItems in the listBox.
	 * 
	 */
	private class ReportFilterFieldsListModelItemRenderer implements ListitemRenderer<ReportFilterFields> {

		public ReportFilterFieldsListModelItemRenderer() {

		}

		@Override
		public void render(Listitem item, ReportFilterFields reportFilterFields, int count) {
			Listcell lc;
			lc = new Listcell(reportFilterFields.getFieldName());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldType());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldLabel());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldDBName());
			lc.setParent(item);
			Checkbox chkbox = new Checkbox();
			chkbox.setChecked(reportFilterFields.isMandatory());
			chkbox.setDisabled(true);
			lc = new Listcell();
			lc.appendChild(chkbox);
			lc.setParent(item);
			lc = new Listcell(String.valueOf(reportFilterFields.getSeqOrder()));
			lc.setParent(item);
			item.setAttribute("data", reportFilterFields);
			setRender(reportFilterFields);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onReportFilterFieldsItemDoubleClicked");

		}
	}

	private void setRender(ReportFilterFields reportFilterFields) {
		if (flag == true) {
			List<Component> items = listBoxReportFilterFields.getChildren();
			for (Component component : items) {
				if (component instanceof Listitem) {
					((Listitem) component).setDisabled(false);
				}
			}
		} else {
			List<Component> items = listBoxReportFilterFields.getChildren();
			for (Component component : items) {
				if (component instanceof Listitem) {
					((Listitem) component).setDisabled(true);
				}
			}
		}

	}

	/**
	 * When user clicks on "onlineProcess"
	 * 
	 * @param event
	 */
	public void onCheck$promptRequired(Event event) {
		logger.debug("Entering " + event.toString());

		if (!this.promptRequired.isChecked()) {
			this.showTempLibrary.setChecked(false);
			this.showTempLibrary.setDisabled(true);
			this.alwMultiFormat.setChecked(false);
			this.alwMultiFormat.setDisabled(true);
		} else {
			this.showTempLibrary.setDisabled(false);
			this.alwMultiFormat.setDisabled(false);
		}

		logger.debug("Leaving " + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aReportConfiguration
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReportConfiguration aReportConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReportConfiguration.getBefImage(),
				aReportConfiguration);
		return new AuditHeader(String.valueOf(aReportConfiguration.getId()), null, null, null, auditDetail,
				aReportConfiguration.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.reportConfiguration);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.reportConfiguration.getReportName());
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

	public ReportConfiguration getReportConfiguration() {
		return this.reportConfiguration;
	}

	public void setReportConfiguration(ReportConfiguration reportConfiguration) {
		this.reportConfiguration = reportConfiguration;
	}

	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}

	public ReportConfigurationService getReportConfigurationService() {
		return this.reportConfigurationService;
	}

	public ReportConfigurationListCtrl getReportConfigurationListCtrl() {
		return reportConfigurationListCtrl;
	}

	public void setReportConfigurationListCtrl(ReportConfigurationListCtrl reportConfigurationListCtrl) {
		this.reportConfigurationListCtrl = reportConfigurationListCtrl;
	}

	public List<ReportFilterFields> getReportFilterFieldsList() {
		return reportFilterFieldsList;
	}

	public void setReportFilterFieldsList(List<ReportFilterFields> reportFilterFieldsList) {
		this.reportFilterFieldsList = reportFilterFieldsList;
	}

	public PagedListWrapper<ReportFilterFields> getReportFilterFieldsPagedListWrapper() {
		return reportFilterFieldsPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setReportFilterFieldsPagedListWrapper() {
		if (this.reportFilterFieldsPagedListWrapper == null) {
			this.reportFilterFieldsPagedListWrapper = (PagedListWrapper<ReportFilterFields>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	/*
	 * public List<ReportAdditionalConditions> getReportAdditionalConditionsList() { return
	 * reportAdditionalConditionsList; }
	 * 
	 * public void setReportAdditionalConditionsList(List<ReportAdditionalConditions> reportAdditionalConditionsList) {
	 * this.reportAdditionalConditionsList = reportAdditionalConditionsList; }
	 * 
	 * public PagedListWrapper<ReportAdditionalConditions> getReportAdditionalConditionsPagedListWrapper() { return
	 * reportAdditionalConditionsPagedListWrapper; }
	 * 
	 * public void setReportAdditionalConditionsPagedListWrapper( PagedListWrapper<ReportAdditionalConditions>
	 * reportAdditionalConditionsPagedListWrapper) { this.reportAdditionalConditionsPagedListWrapper =
	 * reportAdditionalConditionsPagedListWrapper; }
	 * 
	 * public Listbox getListBoxReportAdditionalConditions() { return listBoxReportAdditionalConditions; }
	 * 
	 * public void setListBoxReportAdditionalConditions(Listbox listBoxReportAdditionalConditions) {
	 * this.listBoxReportAdditionalConditions = listBoxReportAdditionalConditions; }
	 */
}
