/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ReportConfigurationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.reports.reportconfiguration;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/ReportConfiguration/reportConfigurationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ReportConfigurationDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2843265056714842214L;
	private final static Logger logger = Logger.getLogger(ReportConfigurationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ReportConfigurationDialog; 		// autoWired

	protected Textbox 		reportName; 					// autoWired
	protected Textbox 		reportHeading; 					// autoWired
	protected Checkbox 		promptRequired; 				// autoWired
	protected Textbox 		reportJasperName; 				// autoWired
	protected Combobox 		dataSourceName; 				// autoWired
	protected Checkbox 		showTempLibrary; 				// autoWired
	protected Textbox 	    menuItemCode; 			        // autoWired


	protected Label 		label_ReportConfigurationDialog_ReportName;;
	protected Label 		label_ReportConfigurationDialog_ReportHeading;;
	protected Label 		label_ReportConfigurationDialog_PromptRequired;
	protected Label 		label_ReportConfigurationDialog_ReportJasperName;
	protected Label 		label_ReportConfigurationDialog_DataSourceName;
	protected Label 		label_ReportConfigurationDialog_ShowTempLibrary;;
	protected Label 		label_ReportConfigurationDialog_MenuItemCode;

	protected Row			row_Zero;
	protected Row			row_One;
	protected Row			row_Two;
	protected Row			row_Three;

	protected Hlayout 		hlayout_ReportName;
	protected Hlayout 		hlayout_ReportHeading;
	protected Hlayout 		hlayout_PromptRequired;
	protected Hlayout 		hlayout_ReportJasperName;
	protected Hlayout 		hlayout_DataSourceName;
	protected Hlayout 		hlayout_ShowTempLibrary;
	protected Hlayout 		hlayout_MenuItemCode;

	protected Space 		space_ReportName; 				// autoWired
	protected Space 		space_ReportHeading; 			// autoWired
	protected Space 		space_PromptRequired; 				// autoWired
	protected Space 		space_ReportJasperName; 		// autoWired
	protected Space 		space_DataSourceName; 		// autoWired
	protected Space 		space_ShowTempLibrary; 	      // autoWired
	protected Space 		space_MenuItemCode; 			// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Label 		recordType;					// autoWired
	protected Radiogroup 	userAction;					// autoWired
	protected Groupbox 		gb_statusDetails;			// autoWired
	protected Groupbox 		groupboxWf;					// autoWired
	protected South 		south;						// autoWired

	protected Button 		btnNew; 					// autoWired
	protected Button 		btnEdit; 					// autoWired
	protected Button 		btnDelete; 					// autoWired
	protected Button 		btnSave; 					// autoWired
	protected Button 		btnCancel; 					// autoWired
	protected Button 		btnClose; 					// autoWired
	protected Button 		btnHelp; 					// autoWired
	protected Button 		btnNotes; 					// autoWired

	// not auto wired Var's
	private ReportConfiguration reportConfiguration; // overHanded per parameter
	private transient ReportConfigurationListCtrl reportConfigurationListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_reportName;
	private transient String  		oldVar_reportHeading;
	private transient boolean  		oldVar_promptRequired;
	private transient String  		oldVar_reportJasperName;
	private transient String  		oldVar_dataSourceName;
	private transient boolean  	    oldVar_showTempLibrary;
	private transient String  		oldVar_menuItemCode; 					
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;
	private boolean enqModule=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ReportConfigurationDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient ReportConfigurationService reportConfigurationService;
	private transient PagedListService pagedListService;

	//Service Details list
	protected Button     btnNew_ReportFilterFields;
	protected Button     btnPreviewReport;
	protected Paging     pagingReportFilterFieldsList;
	protected Listbox    listBoxReportFilterFields;
	private List<ReportFilterFields> reportFilterFieldsList=new ArrayList<ReportFilterFields>();
	private PagedListWrapper<ReportFilterFields> reportFilterFieldsPagedListWrapper;

	private List<ValueLabel> dataSourceNamesList = PennantStaticListUtil.getDataSourceNames();

	/**
	 * default constructor.<br>
	 */
	public ReportConfigurationDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportConfiguration object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportConfigurationDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}
			/* set components visible dependent of the users rights */
			doCheckRights();
			setReportFilterFieldsPagedListWrapper();
			// READ OVERHANDED parameters !
			if (args.containsKey("reportConfiguration")) {
				this.reportConfiguration = (ReportConfiguration) args.get("reportConfiguration");
				ReportConfiguration befImage =new ReportConfiguration();
				BeanUtils.copyProperties(this.reportConfiguration, befImage);
				this.reportConfiguration.setBefImage(befImage);

				setReportConfiguration(this.reportConfiguration);
			} else {
				setReportConfiguration(null);
			}

			doLoadWorkFlow(this.reportConfiguration.isWorkflow(),this.reportConfiguration.getWorkflowId(),this.reportConfiguration.getNextTaskId());

			if (isWorkFlowEnabled()){
				if(!enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
				}	
				getUserWorkspace().alocateRoleAuthorities(getRole(), "ReportConfigurationDialog");
			}else{
				getUserWorkspace().alocateAuthorities("ReportConfigurationDialog");
			}



			fillComboBox(dataSourceName, "", dataSourceNamesList,"");

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

			// READ OVERHANDED parameters !
			// we get the reportConfigurationListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete reportConfiguration here.
			if (args.containsKey("reportConfigurationListCtrl")) {
				setReportConfigurationListCtrl((ReportConfigurationListCtrl) args.get("reportConfigurationListCtrl"));
			} else {
				setReportConfigurationListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getReportConfiguration());
		}	catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_ReportConfigurationDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		//Empty sent any required attributes
		this.reportName.setMaxlength(100);
		this.reportHeading.setMaxlength(1000);
		this.reportJasperName.setMaxlength(100);
		this.dataSourceName.setMaxlength(50);
		this.menuItemCode.setMaxlength(100);
		if(this.getReportConfiguration().isNew()){
			this.btnPreviewReport.setVisible(false);
		}

		if (isWorkFlowEnabled() & !enqModule ){
			this.gb_statusDetails.setVisible(true);
		}else{
			if(enqModule){
				groupboxWf.setVisible(false);
				south.setHeight("60px");
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");

		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnDelete"));
			this.btnNew_ReportFilterFields.setVisible(true);
			this.btnPreviewReport.setVisible(true);
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReportConfigurationDialog_btnSave"));
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ReportConfigurationDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		// remember the old Var's
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_ReportConfigurationDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			// close anyway
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
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
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ReportConfiguration", getReportConfiguration());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering ");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, 
					MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}

		if(close){
			closeDialog(this.window_ReportConfigurationDialog, "reportConfiguration");
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
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly(true);
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReportConfiguration
	 *            reportConfiguration
	 */
	public void doWriteBeanToComponents(ReportConfiguration  aReportConfiguration ) {
		logger.debug("Entering ");
		this.reportName.setValue(aReportConfiguration.getReportName());
		this.reportHeading.setValue(aReportConfiguration.getReportHeading());
		if(aReportConfiguration.isNew()){
			this.promptRequired.setChecked(true);
		}else{
			this.promptRequired.setChecked(aReportConfiguration.isPromptRequired());
			if(aReportConfiguration.isPromptRequired()){
				this.showTempLibrary.setChecked(aReportConfiguration.isShowTempLibrary());
			}else{
				this.showTempLibrary.setDisabled(true);
			}
			this.dataSourceName.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(aReportConfiguration.getDataSourceName()),PennantStaticListUtil.getDataSourceNames()));
		}
		this.reportJasperName.setValue(aReportConfiguration.getReportJasperName());

		this.menuItemCode.setValue(aReportConfiguration.getMenuItemCode());
		this.recordStatus.setValue(aReportConfiguration.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aReportConfiguration.getRecordType()));
		if(aReportConfiguration.getListReportFieldsDetails()!=null && aReportConfiguration.getListReportFieldsDetails().size()>0){
			doFillReportFilterFieldsList(aReportConfiguration.getListReportFieldsDetails());
		}

		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReportConfiguration
	 */
	public void doWriteComponentsToBean(ReportConfiguration  aReportConfiguration) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aReportConfiguration.setReportName(this.reportName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setReportHeading(this.reportHeading.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setPromptRequired(this.promptRequired.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setReportJasperName(this.reportJasperName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aReportConfiguration.setDataSourceName(this.dataSourceName.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {

			aReportConfiguration.setShowTempLibrary(this.showTempLibrary.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {

			aReportConfiguration.setMenuItemCode(this.menuItemCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		aReportConfiguration.setListReportFieldsDetails(this.reportFilterFieldsList);

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aReportConfiguration
	 * @throws InterruptedException
	 */
	public void doShowDialog(ReportConfiguration aReportConfiguration) throws InterruptedException {
		logger.debug("Entering ");
		// if aReportConfiguration == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aReportConfiguration == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aReportConfiguration = getReportConfigurationService().getNewReportConfiguration();
			setReportConfiguration(aReportConfiguration);
		} else {
			setReportConfiguration(aReportConfiguration);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if(enqModule){
			doReadOnly(true);
		}else if (aReportConfiguration.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.reportName.focus();
		} else {
			this.reportName.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aReportConfiguration);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ReportConfigurationDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_reportName = this.reportName.getValue();
		this.oldVar_reportHeading = this.reportHeading.getValue();
		this.oldVar_promptRequired = this.promptRequired.isChecked();
		this.oldVar_reportJasperName = this.reportJasperName.getValue();
		this.oldVar_dataSourceName = this.dataSourceName.getValue();
		this.oldVar_showTempLibrary = this.showTempLibrary.isChecked();
		this.oldVar_menuItemCode = this.menuItemCode.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.reportName.setValue(this.oldVar_reportName);
		this.reportHeading.setValue(this.oldVar_reportHeading);
		this.promptRequired.setChecked(this.oldVar_promptRequired);
		this.reportJasperName.setValue(this.oldVar_reportJasperName);
		this.dataSourceName.setValue(this.oldVar_dataSourceName);
		this.showTempLibrary.setChecked(this.oldVar_showTempLibrary);
		this.menuItemCode.setValue(this.oldVar_menuItemCode);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()& !enqModule){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		//To clear the Error Messages
		doClearMessage();

		if (!this.oldVar_reportName.equals(this.reportName.getValue())){
			return true;
		}
		if (!this.oldVar_reportHeading.equals(this.reportHeading.getValue()) ){
			return true;
		}
		if (this.oldVar_promptRequired != this.promptRequired.isChecked()) {
			return true;
		}
		if (!this.oldVar_reportJasperName.equals(this.reportJasperName.getValue())) {
			return true;
		}
		if (!this.oldVar_dataSourceName.equals(this.dataSourceName.getValue())) {
			return true;
		}
		if (this.oldVar_showTempLibrary != this.showTempLibrary.isChecked()) {
			return true;
		}
		if (!this.oldVar_menuItemCode.equals(this.menuItemCode.getValue())) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.reportName.isReadonly()){
			this.reportName.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.reportHeading.isReadonly()) {
			this.reportHeading.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportHeading.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.reportJasperName.isReadonly()){
			this.reportJasperName.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_ReportJasperName.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.dataSourceName.isDisabled()){
			this.dataSourceName.setConstraint(new PTListValidator(
					Labels.getLabel("label_ReportConfigurationDialog_DataSourceName.value"),dataSourceNamesList,true));
		}
		if (!this.menuItemCode.isReadonly()){
			this.menuItemCode.setConstraint(new PTStringValidator(Labels.getLabel("label_ReportConfigurationDialog_MenuItemCode.value"),
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
	private void doClearMessage() {
		logger.debug("Enterring");
		this.reportName.setErrorMessage("");
		this.reportHeading.setErrorMessage("");
		this.reportJasperName.setErrorMessage("");
		this.dataSourceName.setErrorMessage("");
		this.menuItemCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<ReportConfiguration> soObject = getReportConfigurationListCtrl().getSearchObj();
		getReportConfigurationListCtrl().pagingReportConfigurationList.setActivePage(0);
		getReportConfigurationListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getReportConfigurationListCtrl().listBoxReportConfiguration!=null){
			getReportConfigurationListCtrl().listBoxReportConfiguration.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ReportConfiguration object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final ReportConfiguration aReportConfiguration = new ReportConfiguration();
		BeanUtils.copyProperties(getReportConfiguration(), aReportConfiguration);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " 
		+ aReportConfiguration.getReportName();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aReportConfiguration.getRecordType()).equals("")){
				aReportConfiguration.setVersion(aReportConfiguration.getVersion()+1);
				aReportConfiguration.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aReportConfiguration.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aReportConfiguration,tranType)){
					refreshList();
					closeDialog(this.window_ReportConfigurationDialog, "ReportConfiguration"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new ReportConfiguration object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new ReportConfiguration() in the frontEnd.
		// we get it from the backEnd.
		final ReportConfiguration aReportConfiguration = getReportConfigurationService().getNewReportConfiguration();
		aReportConfiguration.setNewRecord(true);
		setReportConfiguration(aReportConfiguration);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.reportName.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		doReadOnly(false);
		if (getReportConfiguration().isNewRecord()){
			/*	setComponentAccessType("ReportConfiguration_reportName", false, this.reportName, this.space_ReportName,
					this.label_ReportConfigurationDialog_ReportName, this.hlayout_ReportName,this.row_Zero);*/
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}


		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.reportConfiguration.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				/*	this.btnNew_ReportFilterFields.setVisible(true)*/
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering ");

		boolean tempReadOnly = readOnly;

		if(readOnly|| (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(reportConfiguration.getRecordType())))) {
			tempReadOnly=true;
		}
		setComponentAccessType("ReportConfigurationDialog_reportName", tempReadOnly, this.reportName, this.space_ReportName, 
				this.label_ReportConfigurationDialog_ReportName,this.hlayout_ReportName,null);
		setComponentAccessType("ReportConfigurationDialog_reportHeading", tempReadOnly, this.reportHeading, this.space_ReportHeading, 
				this.label_ReportConfigurationDialog_ReportHeading, this.hlayout_ReportHeading,null);
		setRowInvisible(this.row_Zero,this.hlayout_ReportName, this.hlayout_ReportHeading);

		setComponentAccessType("ReportConfigurationDialog_promptRequired", tempReadOnly, this.promptRequired, null,
				this.label_ReportConfigurationDialog_PromptRequired, this.hlayout_PromptRequired,null);
		setRowInvisible(this.row_One,this.hlayout_PromptRequired, this.hlayout_ShowTempLibrary);

		setComponentAccessType("ReportConfigurationDialog_reportJasperName", tempReadOnly, this.reportJasperName, this.space_ReportJasperName, 
				this.label_ReportConfigurationDialog_ReportJasperName, this.hlayout_ReportJasperName,null);
		setComponentAccessType("ReportConfigurationDialog_dataSourceName", tempReadOnly, this.dataSourceName, this.space_DataSourceName,
				this.label_ReportConfigurationDialog_DataSourceName, this.hlayout_DataSourceName,null);
		setRowInvisible(this.row_Two,this.hlayout_ReportJasperName, this.hlayout_DataSourceName);

		setComponentAccessType("ReportConfigurationDialog_menuItemCode", tempReadOnly, this.menuItemCode, this.space_MenuItemCode, 
				this.label_ReportConfigurationDialog_MenuItemCode, this.hlayout_MenuItemCode,this.row_Three);
		this.btnPreviewReport.setDisabled(tempReadOnly);
		this.btnNew_ReportFilterFields.setDisabled(tempReadOnly);
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		if (!PennantConstants.RECORD_TYPE_DEL.equals(reportConfiguration.getRecordType())) {
			doSetValidation();
			// fill the reportConfiguration object with the components data
			doWriteComponentsToBean(aReportConfiguration);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aReportConfiguration.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aReportConfiguration.getRecordType()).equals("")){
				aReportConfiguration.setVersion(aReportConfiguration.getVersion()+1);
				if(isNew){
					aReportConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aReportConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReportConfiguration.setNewRecord(true);
				}
			}
		}else{
			aReportConfiguration.setVersion(aReportConfiguration.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aReportConfiguration,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_ReportConfigurationDialog, "ReportConfiguration");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aReportConfiguration (ReportConfiguration)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ReportConfiguration aReportConfiguration,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aReportConfiguration.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aReportConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReportConfiguration.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aReportConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReportConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aReportConfiguration);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aReportConfiguration))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aReportConfiguration.setTaskId(taskId);
			aReportConfiguration.setNextTaskId(nextTaskId);
			aReportConfiguration.setRoleCode(getRole());
			aReportConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aReportConfiguration, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aReportConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aReportConfiguration, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aReportConfiguration, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**	
	 * Get the result after processing DataBase Operations 
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		ReportConfiguration aReportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {
			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getReportConfigurationService().delete(auditHeader);

						deleteNotes=true;	
					}else{
						auditHeader = getReportConfigurationService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getReportConfigurationService().doApprove(auditHeader);

						if(aReportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getReportConfigurationService().doReject(auditHeader);
						if(aReportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_ReportConfigurationDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_ReportConfigurationDialog, auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ OnChange Events+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * When user clicks on "btnNew_ReportFilterFields"
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNew_ReportFilterFields(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		ReportFilterFields  reportFilterFields = new ReportFilterFields();
		reportFilterFields.setNewRecord(true);
		reportFilterFields.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("reportConfigurationDialogCtrl", this);
		map.put("reportFilterFields",reportFilterFields);
		map.put("reportConfiguration", getReportConfiguration());
		map.put("newRecord", "true");
		map.put("roleCode", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportFilterFieldsDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user double clicks "ReportFilterFields"
	 * @param event
	 * @throws Exception
	 */
	public void onReportFilterFieldsItemDoubleClicked(Event event)throws Exception{
		logger.debug("Entering " + event.toString());

		final Listitem item=this.listBoxReportFilterFields.getSelectedItem();
		if(item!=null){	
			final ReportFilterFields reportFilterFields=(ReportFilterFields)item.getAttribute("data");	

			final HashMap<String, Object> map = new HashMap<String, Object>();
			reportFilterFields.setNewRecord(false);
			map.put("reportConfigurationDialogCtrl", this);
			map.put("reportFilterFields", reportFilterFields);
			map.put("reportConfiguration", getReportConfiguration());
			map.put("roleCode", getRole());
			if(enqModule){
				map.put("enqModule", true);
			}else{
				map.put("enqModule", false);
			}

			try {
				Executions.createComponents("/WEB-INF/pages/Reports/ReportConfiguration/ReportFilterFieldsDialog.zul",null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}

		logger.debug("Leaving " + event.toString());	
	}

	/**      
	 * This method fills ReportFilterFieldsList
	 * @param expenseDetails
	 */
	public void doFillReportFilterFieldsList(List<ReportFilterFields> reprtFiltrFieldsList){
		logger.debug("Entering ");
		setReportFilterFieldsList(reprtFiltrFieldsList);
		this.reportFilterFieldsPagedListWrapper.initList(reportFilterFieldsList,this.listBoxReportFilterFields, new Paging());
		this.listBoxReportFilterFields.setItemRenderer(new ReportFilterFieldsListModelItemRenderer());
		getReportConfiguration().setListReportFieldsDetails(reprtFiltrFieldsList);

		logger.debug("Leaving ");
	}


	/**
	 * Item renderer for listItems in the listBox.
	 * 
	 */
	private class ReportFilterFieldsListModelItemRenderer implements ListitemRenderer<ReportFilterFields> {
		//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
		@Override
		public void render(Listitem item, ReportFilterFields reportFilterFields, int count) throws Exception {
			//ReportFilterFields reportFilterFields=(ReportFilterFields) object;
			Listcell lc;
			lc = new Listcell(reportFilterFields.getFieldName());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldType());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldLabel());
			lc.setParent(item);
			lc = new Listcell(reportFilterFields.getFieldDBName());
			lc.setParent(item);
			Checkbox chkbox=new Checkbox();
			chkbox.setChecked(reportFilterFields.isMandatory());
			chkbox.setDisabled(true);
			lc = new Listcell();
			lc.appendChild(chkbox);
			lc.setParent(item);
			lc = new Listcell(String.valueOf(reportFilterFields.getSeqOrder()));
			lc.setParent(item);
			item.setAttribute("data", reportFilterFields);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onReportFilterFieldsItemDoubleClicked");
		}
	}
	/**
	 * When user clicks on "onlineProcess"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$promptRequired(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if(!this.promptRequired.isChecked()){
			this.showTempLibrary.setChecked(false);
			this.showTempLibrary.setDisabled(true);
		}else {
			this.showTempLibrary.setDisabled(false);
		}

		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * @param aReportConfiguration 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReportConfiguration aReportConfiguration, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReportConfiguration.getBefImage(), aReportConfiguration);   
		return new AuditHeader(String.valueOf(
				aReportConfiguration.getId()),null,null,null,auditDetail,aReportConfiguration.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ReportConfigurationDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
		logger.debug("Leaving");
	}

	/**
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */ 
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	//Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("ReportConfiguration");
		notes.setReference(getReportConfiguration().getReportName());
		notes.setVersion(getReportConfiguration().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setReportConfigurationListCtrl(
			ReportConfigurationListCtrl reportConfigurationListCtrl) {
		this.reportConfigurationListCtrl = reportConfigurationListCtrl;
	}

	public List<ReportFilterFields> getReportFilterFieldsList() {
		return reportFilterFieldsList;
	}

	public void setReportFilterFieldsList(
			List<ReportFilterFields> reportFilterFieldsList) {
		this.reportFilterFieldsList = reportFilterFieldsList;
	}

	public PagedListWrapper<ReportFilterFields> getReportFilterFieldsPagedListWrapper() {
		return reportFilterFieldsPagedListWrapper;
	}


	@SuppressWarnings("unchecked")
	public void setReportFilterFieldsPagedListWrapper(){
		if(this.reportFilterFieldsPagedListWrapper == null){
			this.reportFilterFieldsPagedListWrapper = (PagedListWrapper<ReportFilterFields>) SpringUtil.getBean("pagedListWrapper");;
		}
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}
