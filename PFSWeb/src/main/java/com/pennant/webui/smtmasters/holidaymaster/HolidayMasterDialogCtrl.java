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
 * * FileName : HolidayMasterDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-07-2011 * *
 * Modified Date : 11-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.smtmasters.holidaymaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.masters.calendar.model.HolidayCalendarModelRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/SolutionFactory/holidayMasterDialog.zul file.
 */
public class HolidayMasterDialogCtrl extends GFCBaseCtrl<HolidayMaster> {
	private static final long serialVersionUID = -6497477637239109557L;
	private static final Logger logger = LogManager.getLogger(HolidayMasterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_HolidayMasterDialog; // autoWired

	protected ExtendedCombobox holidayCode;
	protected Decimalbox holidayYear; // autoWired
	protected Groupbox gbcalendar;
	protected Hbox janhlay;

	private List<HolidayDetail> holidayDetails = new ArrayList<HolidayDetail>();

	protected Grid grid_Basicdetails;

	private HolidayMaster holidayMaster; // overhanded
											// per
											// param
	private transient HolidayMasterListCtrl holidayMasterListCtrl; // overhanded
																	// per
																	// param
	private transient WeekendMasterDAO weekendMasterDAO;

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient HolidayMasterService holidayMasterService;

	int listRows;

	protected Calendars calendars0;
	protected Calendars calendars1;
	protected Calendars calendars2;
	protected Calendars calendars3;
	protected Calendars calendars4;
	protected Calendars calendars5;
	protected Calendars calendars6;
	protected Calendars calendars7;
	protected Calendars calendars8;
	protected Calendars calendars9;
	protected Calendars calendars10;
	protected Calendars calendars11;

	/**
	 * default constructor.<br>
	 */
	public HolidayMasterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "HolidayMasterDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected HolidayMaster object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_HolidayMasterDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_HolidayMasterDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("holidayMaster")) {
				this.holidayMaster = (HolidayMaster) arguments.get("holidayMaster");
				HolidayMaster befHolidayMaster = new HolidayMaster();
				setHolidayMaster(this.holidayMaster);
				BeanUtils.copyProperties(this.holidayMaster, befHolidayMaster);
				this.holidayMaster.setBefImage(befHolidayMaster);
			} else {
				setHolidayMaster(null);
			}
			// READ OVERHANDED params !
			// we get the holidayMasterListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete holidayMaster here.
			if (arguments.containsKey("holidayMasterListCtrl")) {
				setHolidayMasterListCtrl((HolidayMasterListCtrl) arguments.get("holidayMasterListCtrl"));
			} else {
				setHolidayMasterListCtrl(null);
			}

			doLoadWorkFlow(this.holidayMaster.isWorkflow(), this.holidayMaster.getWorkflowId(),
					this.holidayMaster.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "HolidayMasterDialog");
			}
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 20 + 25;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listRows = Math.round(listboxHeight / 22);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getHolidayMaster());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_HolidayMasterDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.holidayCode.setMaxlength(8);
		this.holidayCode.setMandatoryStyle(true);
		this.holidayCode.setModuleName("WeekendMaster");
		this.holidayCode.setValueColumn("WeekendCode");
		this.holidayCode.setDescColumn("WeekendDesc");
		this.holidayCode.setValidateColumns(new String[] { "WeekendCode" });

		this.holidayYear.setMaxlength(4);
		this.holidayYear.setFormat("###0");

		if (isWorkFlowEnabled()) {
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
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_HolidayMasterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_HolidayMasterDialog);
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
	 * when the "Search" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onFulfill$holidayCode(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.holidayYear.getValue() != null) {
			showCalendar();
		}
		if (this.holidayYear.isReadonly()) {
			this.holidayYear.setReadonly(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "New" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnNew_HolidayDet(Event event) throws InterruptedException {
		doSetValidation();
		logger.debug("Entering" + event.toString());
		HolidayDetail aHolidayDetail = new HolidayDetail();
		aHolidayDetail.setNewRecord(true);
		aHolidayDetail.setHolidayCode(this.holidayCode.getValue());
		aHolidayDetail.setHolidayYear(this.holidayYear.getValue());
		if (this.holidayYear.getValue().intValue() == 0) {
			aHolidayDetail.setHolidayYear(new BigDecimal(DateUtil.getYear(new Date())));
		}
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("holidayDetail", aHolidayDetail);
		map.put("HolidayMasterDialogCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterDetailsDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the leaving the HolidayYear TextBox <br>
	 * 
	 * @param event
	 */
	public void onChange$holidayYear(Event event) {
		logger.debug("Entering " + event.toString());
		showCalendar();
		holidayDetails.clear();
		if (this.holidayCode.getValue() != null && !StringUtils.equals(this.holidayCode.getValue(), "")) {
			if (this.holidayYear.getValue() != null
					&& (this.holidayYear.getValue().compareTo(BigDecimal.valueOf(1950)) < 0)
					|| this.holidayYear.getValue().compareTo(BigDecimal.valueOf(
							Long.valueOf(DateUtil.getYear(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))))) > 0) {
				throw new WrongValueException(this.holidayYear, Labels.getLabel("DATE_ALLOWED_RANGE", new String[] {
						Labels.getLabel("label_HolidayMasterDialog_HolidayYear.value"), "1950",
						String.valueOf(DateUtil.getYear(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) }));
			}
			holidayDetails = BusinessCalendar.getWeekendList(this.holidayCode.getValue(), this.holidayYear.intValue());
			showHoliday();
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
		doWriteBeanToComponents(this.holidayMaster.getBefImage());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aHolidayMaster
	 */
	public void doWriteBeanToComponents(HolidayMaster aHolidayMaster) {
		logger.debug("Entering ");

		if (aHolidayMaster.isNewRecord()) {
			this.holidayCode.setValue("");
		} else {
			this.holidayCode.setValue(aHolidayMaster.getHolidayCode());
		}

		this.holidayYear.setValue(aHolidayMaster.getHolidayYear());
		holidayDetails = aHolidayMaster.getHolidayDetails();

		showCalendar();
		showHoliday();
		this.recordStatus.setValue(aHolidayMaster.getRecordStatus());
		logger.debug("Leaving ");

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aHolidayMaster
	 */
	private void doWriteComponentsToBean(HolidayMaster aHolidayMaster) {
		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aHolidayMaster.setHolidayCode(this.holidayCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aHolidayMaster.setHolidayYear(this.holidayYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		StringBuilder var_holidays = new StringBuilder();
		StringBuilder var_holidaysDesc = new StringBuilder();
		for (HolidayDetail detail : holidayDetails) {
			detail.getHoliday().getTime();
			if (var_holidays.length() == 0) {
				var_holidays.append(detail.getJulionDate());
			} else {
				var_holidays.append(",");
				var_holidays.append(detail.getJulionDate());
			}
		}

		aHolidayMaster.setHolidays(var_holidays.toString());
		aHolidayMaster.setHolidaysDesc(var_holidaysDesc.toString());

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving ");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aHolidayMaster
	 */
	public void doShowDialog(HolidayMaster aHolidayMaster) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aHolidayMaster.isNewRecord()) {
			this.btnCtrl.setInitNew();
		} else {
			if (isWorkFlowEnabled()) {
				doEdit();
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		btnDelete.setVisible(false);
		// this.btnSave.setVisible(true);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aHolidayMaster);
			// Set the list of Holidays

			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_HolidayMasterDialog.onClose();
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
		// doClearMessage();
		setValidationOn(true);

		if (!this.holidayCode.isReadonly()) {
			this.holidayCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_HolidayMasterDialog_HolidayCode.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true, true));
		}

		if (!this.holidayYear.isReadonly()) {
			this.holidayYear.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_HolidayMasterDialog_HolidayYear.value") }));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.holidayCode.setConstraint("");
		this.holidayYear.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.holidayCode.setErrorMessage("");
		this.holidayYear.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getHolidayMaster().isNewRecord()) {
			this.holidayCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.holidayCode.focus();
			this.holidayYear.setReadonly(isReadOnly("button_HolidayMasterDialog_holidayYear"));
		} else {
			this.holidayCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.holidayYear.setReadonly(true);
		}

		doReadonlyCalender(isReadOnly("button_HolidayMasterDialog_holidays"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.holidayMaster.isNewRecord()) {
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
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.holidayCode.setReadonly(true);
		this.holidayYear.setReadonly(true);
		doReadonlyCalender(true);
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		this.holidayCode.setValue("");
		this.holidayYear.setValue("0");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		HolidayMaster aHolidayMaster = new HolidayMaster();
		BeanUtils.copyProperties(getHolidayMaster(), aHolidayMaster);
		boolean isNew = false;
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the HolidayMaster object with the components data
		doWriteComponentsToBean(aHolidayMaster);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aHolidayMaster.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aHolidayMaster.getRecordType())) {
				aHolidayMaster.setVersion(aHolidayMaster.getVersion() + 1);
				if (isNew) {
					aHolidayMaster.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aHolidayMaster.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aHolidayMaster.setNewRecord(true);
				}
			}
		} else {
			aHolidayMaster.setVersion(aHolidayMaster.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aHolidayMaster, tranType)) {
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
	 * @param aHolidayMaster
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(HolidayMaster aHolidayMaster, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aHolidayMaster.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aHolidayMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aHolidayMaster.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aHolidayMaster.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aHolidayMaster.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aHolidayMaster);
				}

				if (isNotesMandatory(taskId, aHolidayMaster)) {
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

			aHolidayMaster.setTaskId(taskId);
			aHolidayMaster.setNextTaskId(nextTaskId);
			aHolidayMaster.setRoleCode(getRole());
			aHolidayMaster.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aHolidayMaster, tranType);

			String operationRefs = getServiceOperations(taskId, aHolidayMaster);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aHolidayMaster, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aHolidayMaster, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * Get the result after the DataBase Operations
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
		HolidayMaster aHolidayMaster = (HolidayMaster) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getHolidayMasterService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getHolidayMasterService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getHolidayMasterService().doApprove(auditHeader);

					if (aHolidayMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getHolidayMasterService().doReject(auditHeader);

					if (aHolidayMaster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_HolidayMasterDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_HolidayMasterDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.holidayMaster), true);
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

	/**
	 * Get Audit Header Details
	 * 
	 * @param aHolidayMaster (HolidayMaster)
	 * 
	 * @param tranType       (String)
	 * 
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(HolidayMaster aHolidayMaster, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aHolidayMaster.getBefImage(), aHolidayMaster);

		return new AuditHeader(String.valueOf(aHolidayMaster.getId()), null, null, null, auditDetail,
				aHolidayMaster.getUserDetails(), getOverideMap());
	}

	/**
	 * Holiday Year Allowed Range
	 */
	public void getHolidayYearByappDate() {
		logger.debug("Entering");
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		String appDate = String.valueOf(appEndDate).substring(0, 4);
		BigDecimal appDftEndDate = new BigDecimal(appDate);
		BigDecimal appStartDate = new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
		if (!this.holidayYear.isReadonly() && holidayYear.getValue() != null
				&& (this.holidayYear.getValue().compareTo(appDftEndDate) >= 0
						|| this.holidayYear.getValue().compareTo(appStartDate) < 0)) {
			throw new WrongValueException(this.holidayYear,
					Labels.getLabel("HolidayYear_Validation", new String[] { String.valueOf(appStartDate), appDate }));
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.holidayMaster);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getHolidayMasterListCtrl().search();
	}

	private void showCalendar() {
		logger.debug(" Entering ");
		addcalander(calendars0, 0, this.holidayYear.intValue());
		addcalander(calendars1, 1, this.holidayYear.intValue());
		addcalander(calendars2, 2, this.holidayYear.intValue());
		addcalander(calendars3, 3, this.holidayYear.intValue());
		addcalander(calendars4, 4, this.holidayYear.intValue());
		addcalander(calendars5, 5, this.holidayYear.intValue());
		addcalander(calendars6, 6, this.holidayYear.intValue());
		addcalander(calendars7, 7, this.holidayYear.intValue());
		addcalander(calendars8, 8, this.holidayYear.intValue());
		addcalander(calendars9, 9, this.holidayYear.intValue());
		addcalander(calendars10, 10, this.holidayYear.intValue());
		addcalander(calendars11, 11, this.holidayYear.intValue());
		logger.debug(" Leaving ");
	}

	private void showHoliday() {
		logger.debug(" Entering ");
		loadHolidays(calendars0, 0, this.holidayYear.intValue());
		loadHolidays(calendars1, 1, this.holidayYear.intValue());
		loadHolidays(calendars2, 2, this.holidayYear.intValue());
		loadHolidays(calendars3, 3, this.holidayYear.intValue());
		loadHolidays(calendars4, 4, this.holidayYear.intValue());
		loadHolidays(calendars5, 5, this.holidayYear.intValue());
		loadHolidays(calendars6, 6, this.holidayYear.intValue());
		loadHolidays(calendars7, 7, this.holidayYear.intValue());
		loadHolidays(calendars8, 8, this.holidayYear.intValue());
		loadHolidays(calendars9, 9, this.holidayYear.intValue());
		loadHolidays(calendars10, 10, this.holidayYear.intValue());
		loadHolidays(calendars11, 11, this.holidayYear.intValue());
		logger.debug(" Leaving ");
	}

	private void doReadonlyCalender(boolean readonly) {
		logger.debug(" Entering ");
		this.calendars0.setReadonly(readonly);
		this.calendars1.setReadonly(readonly);
		this.calendars2.setReadonly(readonly);
		this.calendars3.setReadonly(readonly);
		this.calendars4.setReadonly(readonly);
		this.calendars5.setReadonly(readonly);
		this.calendars6.setReadonly(readonly);
		this.calendars7.setReadonly(readonly);
		this.calendars8.setReadonly(readonly);
		this.calendars9.setReadonly(readonly);
		this.calendars10.setReadonly(readonly);
		this.calendars11.setReadonly(readonly);
		logger.debug(" Leaving ");
	}

	public void addcalander(Calendars calendars, int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//
		calendars.setCurrentDate(calendar.getTime());
		calendars.setBeginTime(0);
		calendars.setEndTime(0);
		calendars.setFirstDayOfWeek(Calendar.SUNDAY);
		calendars.setMold("month");
		calendars.setHeight("260px");
		calendars.addForward("onEventCreate", this.window_HolidayMasterDialog, "onEventCreatecalendars", "");
		calendars.addForward("onEventEdit", this.window_HolidayMasterDialog, "onEventEditcalendars", "");
	}

	public void loadHolidays(Calendars calendars, int month, int year) {

		if (holidayDetails == null || holidayDetails.isEmpty()) {
			return;
		}

		HolidayCalendarModelRenderer scm = null;
		List<CalendarEvent> calendarEvents = new LinkedList<CalendarEvent>();
		for (HolidayDetail holidayDetail : holidayDetails) {
			if (holidayDetail.getHoliday().get(Calendar.MONTH) != month) {
				continue;
			}

			SimpleCalendarEvent event = new SimpleCalendarEvent();
			event.setBeginDate(getDayStart(holidayDetail.getHoliday()));
			event.setEndDate(getDayEnd(holidayDetail.getHoliday()));
			event.setContent(" ");
			event.setTitle(" ");
			event.getZclass();
			event.setContentColor("#CCCCCC");
			event.setHeaderColor("#CCCCCC");

			calendarEvents.add(event);
		}
		scm = new HolidayCalendarModelRenderer(calendarEvents);
		calendars.setModel(scm);

	}

	public void onEventCreatecalendars(CalendarsEvent event) throws InterruptedException {
		doOnClickProcess(event, false);
	}

	public void onEventEditcalendars(CalendarsEvent event) throws InterruptedException {
		doOnClickProcess(event, true);
	}

	public void doOnClickProcess(CalendarsEvent event, boolean edit) {

		Date date = event.getBeginDate();
		if (edit) {
			date = event.getCalendarEvent().getBeginDate();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		HolidayDetail detail = new HolidayDetail();
		detail.setHolidayCode(this.holidayCode.getValue());
		detail.setHolidayYear(this.holidayYear.getValue());
		detail.setHoliday(calendar);
		if (calendar.get(Calendar.YEAR) != this.holidayYear.intValue()) {
			return;
		}

		Iterator<HolidayDetail> it = holidayDetails.iterator();
		while (it.hasNext()) {
			HolidayDetail hdetail = it.next();
			if (hdetail.getJulionDate() == detail.getJulionDate()) {
				it.remove();
				break;
			}
		}

		if (!edit) {
			holidayDetails.add(detail);
		}
		showHoliday();
	}

	private static Date getDayStart(Calendar date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 12, 00, 00);
		return calendar.getTime();
	}

	private static Date getDayEnd(Calendar date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE) + 1, 12, 00, 00);
		return calendar.getTime();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.holidayMaster.getHolidayCode());
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

	public void setHolidayMasterService(HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}

	public HolidayMasterService getHolidayMasterService() {
		return this.holidayMasterService;
	}

	public void setHolidayMasterListCtrl(HolidayMasterListCtrl holidayMasterListCtrl) {
		this.holidayMasterListCtrl = holidayMasterListCtrl;
	}

	public HolidayMasterListCtrl getHolidayMasterListCtrl() {
		return this.holidayMasterListCtrl;
	}

	public HolidayMaster getHolidayMaster() {
		return holidayMaster;
	}

	public void setHolidayMaster(HolidayMaster holidayMaster) {
		this.holidayMaster = holidayMaster;
	}

	public WeekendMasterDAO getWeekendMasterDAO() {
		return weekendMasterDAO;
	}

	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		this.weekendMasterDAO = weekendMasterDAO;
	}

	public List<HolidayDetail> getHolidayDetails() {
		return holidayDetails;
	}

	public void setHolidayDetails(List<HolidayDetail> holidayDetails) {
		this.holidayDetails = holidayDetails;
	}

}