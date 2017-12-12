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
 * FileName    		:  QueryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-07-2013    														*
 *                                                                  						*
 * Modified Date    :  04-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.QueryBuilder;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.QueryModule;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.QueryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Query/queryDialog.zul file.
 */
public class QueryDialogCtrl extends GFCBaseCtrl<Query> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(QueryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QueryDialog;
	
	protected Button btnValidate;
	protected Button btnSimulate;
	
	protected Row row0;
	protected Label label_QueryCode;
	protected Hlayout hlayout_QueryCode;
	protected Space space_QueryCode;

	protected Textbox queryCode;
	protected Label label_QueryModule;
	protected Hlayout hlayout_QueryModule;
	protected Space space_QueryModule;

	protected Combobox cb_queryModule;
	protected Row row1;
	protected Label label_QueryDesc;
	protected Hlayout hlayout_QueryDesc;
	protected Space space_QueryDesc;

	protected Textbox queryDesc;

	protected QueryBuilder Sql_Query;
	protected Label label_SqlQuery;
	protected Hlayout hlayout_SqlQuery;
	protected Space space_SqlQuery;
	protected Row row2;

	// protected Textbox sqlQuery;
	// protected Row row2;
	// protected Label label_ActualBlock;
	// protected Hlayout hlayout_ActualBlock;
	// protected Space space_ActualBlock;

	// protected Textbox actualBlock;
	protected Label label_SubQuery;
	protected Hlayout hlayout_SubQuery;
	protected Space space_SubQuery;

	protected Checkbox subQuery;

	protected Row row3;
	protected Label label_Active;
	protected Hlayout hlayout_Active;
	protected Space space_Active;

	protected Checkbox active;

	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;

	// not auto wired vars
	private Query query; // overhanded per param

	private boolean SubQuery = false;
	private int subquery;
	private transient QueryListCtrl queryListCtrl; // overhanded per param

	
	protected Space space;
	protected Comboitem comboitem;

	// ServiceDAOs / Domain Classes
	private transient QueryService queryService;
	private static List<QueryModule> listQueryModule = null;

	/**
	 * default constructor.<br>
	 */
	public QueryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "QueryDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Query object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_QueryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_QueryDialog);

		// listQueryModule = PennantAppUtil.getQueryModule(getUserWorkspace().getLoggedInUser().getEntityCode());
		// listQueryModule =
		// PennantAppUtil.getQueryModule(getUserWorkspace().getLoggedInUser().getEntityCode(),subquery);
		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("query")) {
				this.query = (Query) arguments.get("query");
				Query befImage = new Query();
				BeanUtils.copyProperties(this.query, befImage);
				this.query.setBefImage(befImage);

				setQuery(this.query);
			} else {
				setQuery(null);
			}
			if (arguments.containsKey("subquery")) {
				SubQuery = (Boolean) arguments.get("subquery");
			} else {
				SubQuery = false;
			}
			if (SubQuery) {
				subquery = 1;
			} else {
				subquery = 0;
			}

			listQueryModule = PennantAppUtil.getQueryModule(subquery);
			doLoadWorkFlow(this.query.isWorkflow(), this.query.getWorkflowId(), this.query.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "QueryDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the queryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete query here.
			if (arguments.containsKey("queryListCtrl")) {
				setQueryListCtrl((QueryListCtrl) arguments.get("queryListCtrl"));
			} else {
				setQueryListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getQuery());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_QueryDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "subQuery" checkbox is checked. <br>
	 * 
	 * @param event
	 */
	public List<QueryModule> onCheck$subQuery(Event event) {
		System.out.println("On check");
		this.cb_queryModule.getChildren().clear();
		listQueryModule.clear();
		if (subQuery.isChecked()) {
			subquery = 1;
		} else {
			subquery = 0;
		}
		listQueryModule = PennantAppUtil.getQueryModule(subquery);
		setQueryModule(getQuery().isSubQuery(), getQuery().getQueryModule());
		if (cb_queryModule.getSelectedItem() == null
				|| cb_queryModule.getSelectedItem().getLabel().equalsIgnoreCase(Labels.getLabel("Combo.Select"))) {
			this.Sql_Query.setEditable(false);
			this.Sql_Query.setSqlQuery("");
			this.Sql_Query.setActualBlock("");

		} else {
			Sql_Query.setActualBlock("");
			Sql_Query.setQueryModule(getQuery().getQueryModuleObj());
			// this.Sql_Query.setEntityCode(getUserWorkspace().getLoggedInUser().getEntityCode());
			this.Sql_Query.setEditable(true);
			// Sql_Query.buildQuery(getQuery().getActualBlock());
			// QueryModule queryModule = (QueryModule) cb_queryModule.getSelectedItem().getAttribute("data");
			// Sql_Query.setQueryModule(queryModule);
			// this.Sql_Query.setEntityCode(getUserWorkspace().getLoggedInUser().getEntityCode());
			// this.Sql_Query.setEditable(true);
		}

		return listQueryModule;
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		
		doWriteBeanToComponents(this.query.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		
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
		MessageUtil.showHelpWindow(event, window_QueryDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("Query", getQuery().getQueryCode(), getQuery().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aQuery
	 * @throws Exception
	 */
	public void doShowDialog(Query aQuery) throws Exception {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aQuery);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aQuery.isNewRecord()));
			
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_QueryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.queryCode, this.queryDesc));

		if (getQuery().isNewRecord()) {
			setComponentAccessType("QueryDialog_QueryCode", false, this.queryCode, this.space_QueryCode,
					this.label_QueryCode, this.hlayout_QueryCode, null);
			this.active.setDisabled(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;

		if (readOnly){
			tempReadOnly = true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.query.getRecordType())) {
			tempReadOnly = true;
		}
		// setQueryAccess("QueryDialog_QueryCode", true, this.Sql_Query, null); // FIX ME <<Access right to >>
		setComponentAccessType("QueryDialog_QueryCode", true, this.queryCode, this.space_QueryCode,
				this.label_QueryCode, this.hlayout_QueryCode, null);
		setComponentAccessType("QueryDialog_QueryModule", tempReadOnly, this.cb_queryModule, this.space_QueryModule,
				this.label_QueryModule, this.hlayout_QueryModule, null);
		setRowInvisible(this.row0, this.hlayout_QueryCode, this.hlayout_QueryModule);
		setComponentAccessType("QueryDialog_QueryDesc", tempReadOnly, this.queryDesc, this.space_QueryDesc,
				this.label_QueryDesc, this.hlayout_QueryDesc, null);
		if ((getQuery().isNewRecord())
				|| (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(getQuery().getRecordType()))) {
			setComponentAccessType("QueryDialog_SubQuery", false, this.subQuery, this.space_SubQuery,
					this.label_SubQuery, this.hlayout_SubQuery, null);
			this.label_SubQuery.setVisible(false);
		} else {
			// System.out.println(getQuery().getRecordType());
			setComponentAccessType("QueryDialog_SubQuery", true, this.subQuery, this.space_SubQuery,
					this.label_SubQuery, this.hlayout_SubQuery, null);
			this.label_SubQuery.setVisible(false);
		}
		// setComponentAccessType("QueryDialog_SqlQuery", tempReadOnly, this.Sql_Query, this.space_SqlQuery,
		// this.label_SqlQuery, this.hlayout_SqlQuery,null);
		// setComponentAccessType("QueryDialog_SubQuery", tempReadOnly, this.subQuery, this.space_SubQuery,
		// this.label_SubQuery, this.hlayout_SubQuery,null);
		setRowInvisible(this.row1, this.hlayout_QueryDesc, this.hlayout_SubQuery);
		setComponentAccessType("QueryDialog_Active", tempReadOnly, this.active, this.space_Active, this.label_Active,
				this.hlayout_Active, null);
		setRowInvisible(this.row3, this.hlayout_Active, null);
		if (!getQuery().isNewRecord()) {
			if (readOnly) {
				setQueryAccess("QueryDialog_SqlQuery", true, this.Sql_Query, null);
			} else {
				setQueryAccess("QueryDialog_SqlQuery", false, this.Sql_Query, null);
			}
		}
		// setComponentAccessType("QueryDialog_ActualBlock", tempReadOnly, this.actualBlock, this.space_ActualBlock,
		// this.label_ActualBlock, this.hlayout_ActualBlock,null);
		// setRowInvisible(this.row2,this.hlayout_ActualBlock,this.hlayout_SubQuery);
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("QueryDialog", getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnSave"));
		}
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnValidate"));
		this.btnSimulate.setVisible(getUserWorkspace().isAllowed("button_QueryDialog_btnSimulate"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.queryCode.setMaxlength(50);
		this.queryDesc.setMaxlength(100);
		this.label_SubQuery.setVisible(false);
		this.hlayout_SubQuery.setVisible(false);
		this.subQuery.setVisible(false);
		// this.Sql_Query.setEditable(false);
		/*
		 * this.sqlQuery.setMaxlength(2147483647); this.actualBlock.setMaxlength(2147483647);
		 */

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aQuery
	 *            Query
	 */
	public void doWriteBeanToComponents(Query aQuery) {
		logger.debug("Entering");
		this.queryCode.setValue(aQuery.getQueryCode());
		// TO-DO
		// fillComboBox(this.queryModule, aQuery.getQueryModule(), listQueryModule,"");
		this.queryDesc.setValue(aQuery.getQueryDesc());
		setQueryModule(aQuery.isSubQuery(), aQuery.getQueryModule());
		// this.actualBlock.setValue(aQuery.getActualBlock());
		this.subQuery.setChecked(aQuery.isSubQuery());
		this.active.setChecked(aQuery.isActive());
		this.recordStatus.setValue(aQuery.getRecordStatus());

		if (aQuery.isNewRecord()) {
			Sql_Query.setEditable(false);
		} else {
			this.Sql_Query.setSqlQuery(aQuery.getSqlQuery());
			Sql_Query.setActualBlock(aQuery.getActualBlock());
			Sql_Query.setQueryModule(aQuery.getQueryModuleObj());
			this.Sql_Query.setEntityCode("");
			this.Sql_Query.setEditable(true);
			Sql_Query.buildQuery(aQuery.getActualBlock());
		}
		logger.debug("Leaving");
	}

	public void setQueryModule(boolean subquery, String value) {
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		cb_queryModule.appendChild(comboitem);
		cb_queryModule.setSelectedItem(comboitem);
		for (QueryModule queryModule : listQueryModule) {
			comboitem = new Comboitem();
			comboitem.setLabel(queryModule.getQueryModuleCode());
			comboitem.setValue(queryModule.getQueryModuleCode());
			comboitem.setParent(cb_queryModule);
			comboitem.setAttribute("data", queryModule);
			if (queryModule.getQueryModuleCode().equals(value)) {
				cb_queryModule.setSelectedItem(comboitem);
			}
		}

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aQuery
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(Query aQuery) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Query Code
		try {
			aQuery.setQueryCode(this.queryCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Query Module
		try {
			String strQueryModule = null;
			if (cb_queryModule.getSelectedItem() == null
					|| cb_queryModule.getSelectedItem().getLabel().equalsIgnoreCase(Labels.getLabel("Combo.Select"))) {
				throw new WrongValueException(this.cb_queryModule, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_QueryDialog_QueryModule.value") }));
			} else {
				strQueryModule = this.cb_queryModule.getSelectedItem().getValue().toString();
			}
			if (strQueryModule != null && !PennantConstants.List_Select.equals(strQueryModule)) {
				aQuery.setQueryModule(strQueryModule);
			} else {
				aQuery.setQueryModule(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Query Desc
		try {
			aQuery.setQueryDesc(this.queryDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sql Query
		try {
			if (wve.isEmpty()) {
				Sql_Query.validateQuery();
			}
			aQuery.setSqlQuery(Sql_Query.getSqlQuery());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Actual Block
		try {
			aQuery.setActualBlock(Sql_Query.getActualBlock());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sub Query
		try {
			aQuery.setSubQuery(this.subQuery.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aQuery.setActive(this.active.isChecked());
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * 
	 * @throws InterruptedException
	 */
	private void doSetValidation() throws InterruptedException {
		logger.debug("Entering");
		// Query Code

		if (!this.queryCode.isReadonly()) {
			this.queryCode.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDialog_QueryCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}
		// Query Module
		if (!this.cb_queryModule.isReadonly()) {
			// this.cb_queryModule.setConstraint(new
			// StaticListValidator(listQueryModule,Labels.getLabel("label_QueryDialog_QueryModule.value"),true));
		}
		// Query Desc
		if (!this.queryDesc.isReadonly()) {
			this.queryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDialog_QueryDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.queryCode.setConstraint("");
		this.cb_queryModule.setConstraint("");
		this.queryDesc.setConstraint("");
		/*
		 * this.sqlQuery.setConstraint(""); this.actualBlock.setConstraint("");
		 */
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Query Code
		// Query Module
		// Query Desc
		// Sql Query
		// Actual Block
		// Sub Query
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.queryCode.setErrorMessage("");
		this.cb_queryModule.setErrorMessage("");
		this.queryDesc.setErrorMessage("");
		/*
		 * this.sqlQuery.setErrorMessage(""); this.actualBlock.setErrorMessage("");
		 */
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getQueryListCtrl().search();
	}

	/**
	 * Deletes a Query object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Query aQuery = new Query();
		BeanUtils.copyProperties(getQuery(), aQuery);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_QueryDialog_QueryCode.value") + " : " + aQuery.getQueryCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aQuery.getRecordType())) {
				aQuery.setVersion(aQuery.getVersion() + 1);
				aQuery.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aQuery.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aQuery.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aQuery.getNextTaskId(), aQuery);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aQuery, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.queryCode.setValue("");
		this.cb_queryModule.setSelectedIndex(0);
		this.queryDesc.setValue("");
		/*
		 * this.sqlQuery.setValue(""); this.actualBlock.setValue("");
		 */
		this.subQuery.setChecked(false);
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Query aQuery = new Query();
		BeanUtils.copyProperties(getQuery(), aQuery);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aQuery.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aQuery.getNextTaskId(), aQuery);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aQuery.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Query object with the components data
			doWriteComponentsToBean(aQuery);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aQuery.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aQuery.getRecordType())) {
				aQuery.setVersion(aQuery.getVersion() + 1);
				if (isNew) {
					aQuery.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aQuery.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aQuery.setNewRecord(true);
				}
			}
		} else {
			aQuery.setVersion(aQuery.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aQuery, tranType)) {
				// doWriteBeanToComponents(aQuery);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(Query aQuery, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aQuery.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aQuery.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aQuery.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aQuery.setTaskId(getTaskId());
			aQuery.setNextTaskId(getNextTaskId());
			aQuery.setRoleCode(getRole());
			aQuery.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aQuery, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aQuery, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aQuery, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Query aQuery = (Query) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getQueryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getQueryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getQueryService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aQuery.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getQueryService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aQuery.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_QueryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_QueryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("Query", aQuery.getQueryCode(), aQuery.getVersion()), true);
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
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Query aQuery, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aQuery.getBefImage(), aQuery);
		return new AuditHeader(aQuery.getQueryCode(), null, null, null, auditDetail, aQuery.getUserDetails(),
				getOverideMap());
	}

	public void onChange$cb_queryModule(ForwardEvent event) {
		this.Sql_Query.setActualBlock("");
		Combobox comboBox = (Combobox) event.getOrigin().getTarget();
		if (comboBox.getSelectedItem() != null && comboBox.getSelectedIndex() != 0) {
			QueryModule queryModule = (QueryModule) comboBox.getSelectedItem().getAttribute("data");
			Sql_Query.setQueryModule(queryModule);
			this.Sql_Query.setEntityCode("");
			this.Sql_Query.setEditable(true);

		} else {
			this.Sql_Query.setEditable(false);
			this.Sql_Query.setSqlQuery("");
			this.Sql_Query.setActualBlock("");
		}
	}

	public void onClick$btnValidate(Event event) throws InterruptedException {
		if (this.Sql_Query == null || cb_queryModule.getSelectedIndex() == 0) {
			throw new WrongValueException(cb_queryModule, Labels.getLabel("Label_RuleDialog_select_list"));
			// ((Constrainted) this.Sql_Query).setConstraint(new
			// PTStringValidator(Labels.getLabel("label_QueryDialog_SqlQuery.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		Sql_Query.validateQuery();

	}

	public void onClick$btnSimulate(Event event) throws InterruptedException {
		if (cb_queryModule.getSelectedIndex() == 0) {
			throw new WrongValueException(cb_queryModule, Labels.getLabel("Label_RuleDialog_select_list"));
		}
		this.Sql_Query.setListRows(18);
		Sql_Query.simulateQuery();

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Query getQuery() {
		return this.query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	public QueryService getQueryService() {
		return this.queryService;
	}

	public void setQueryListCtrl(QueryListCtrl queryListCtrl) {
		this.queryListCtrl = queryListCtrl;
	}

	public QueryListCtrl getQueryListCtrl() {
		return this.queryListCtrl;
	}

}
