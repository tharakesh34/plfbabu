/**
\ * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  NotificationsDialogCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.mail.notifications;

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
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.service.rulefactory.RuleService;
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

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Notifications/NotificationsDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class NotificationsDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6945930303723518608L;
	private final static Logger logger = Logger.getLogger(NotificationsDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_NotificationsDialog; 	    // autoWired
	protected Row 			row0;  					            // autoWired
	protected Label 		label_RuleCode; 				    // autoWired
	protected Hlayout 		hlayout_RuleCode; 					// autoWired
	protected Space 		space_RuleCode;  					// autoWired

	protected Textbox 		ruleCode;  					        // autoWired
	protected Label 		label_RuleModule; 					// autoWired
	protected Hlayout 		hlayout_RuleModule; 				// autoWired
	protected Space 		space_RuleModule;  					// autoWired

	protected Combobox 		ruleModule;  					    // autoWired
	protected Row 			row1;  					            // autoWired
	protected Label 		label_RuleCodeDesc; 				// autoWired
	protected Hlayout 		hlayout_RuleCodeDesc; 				// autoWired
	protected Space 		space_RuleCodeDesc;  				// autoWired

	protected Textbox 		ruleCodeDesc;  					    // autoWired

	protected Tab   tab_ruleTemplate; 					        // autoWired
	protected Tab   tab_ruleReciepent; 					        // autoWired
	protected Tab   tab_ruleAttachment; 					    // autoWired
	protected Tab   tab_ruleTemplateDetails; 				    // autoWired
	protected Tab   tab_ruleReciepentDetails; 			        // autoWired
	protected Tab   tab_ruleAttachmentDetails; 
	protected Div	div_ruleTemplate; 				// autoWired
	protected Div 	div_ruleReciepent; 				// autoWired
	protected Div 	div_ruleAttachment; 			// autoWired

	protected JavaScriptBuilder ruleTemplate; 					// autoWired
	protected JavaScriptBuilder ruleReciepent; 					// autoWired
	protected JavaScriptBuilder ruleAttachment; 				// autoWired

	private Codemirror		rule;
	private Codemirror		ruleTemplateResult;
	private Codemirror		ruleReciepentResult;
	private Codemirror		ruleAttachmentResult;

	protected Label 		recordStatus; 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;
	// not auto wired variables
	private Notifications notifications; // overHanded per parameter
	private transient NotificationsListCtrl notificationsListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_RuleCode;
	private transient String  		oldVar_RuleModule;
	private transient String  		oldVar_RuleCodeDesc;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_NotificationsDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnValidate; 		// autoWire
	protected Button btnSimulation; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient NotificationsService notificationsService;
	private transient PagedListService pagedListService;
	private static List<ValueLabel> listRuleModule=null;
	private transient RuleService ruleService;
	private int borderLayoutHeight = 0;
	/**
	 * default constructor.<br>
	 */
	public NotificationsDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Notifications object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NotificationsDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try{
			listRuleModule =  PennantStaticListUtil.getRuleModuleList();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
					this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
					this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

			// get the parameters map that are overHanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED parameters !
			if (args.containsKey("notifications")) {
				this.notifications = (Notifications) args.get("notifications");
				Notifications befImage = new Notifications();
				BeanUtils.copyProperties(this.notifications, befImage);
				this.notifications.setBefImage(befImage);

				setNotifications(this.notifications);
			} else {
				setNotifications(null);
			}

			doLoadWorkFlow(this.notifications.isWorkflow(), this.notifications.getWorkflowId(), this.notifications.getNextTaskId());

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "NotificationsDialog");
			}

			// READ OVERHANDED parameters !
			// we get the NotificationsListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete Notifications here.
			if (args.containsKey("notificationsListCtrl")) {
				setNotificationsListCtrl((NotificationsListCtrl) args.get("notificationsListCtrl"));
			} else {
				setNotificationsListCtrl(null);
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
					.getValue().intValue()- PennantConstants.borderlayoutMainNorth;

			this.div_ruleTemplate.setHeight(this.borderLayoutHeight - 100+ "px");
			this.div_ruleReciepent.setHeight(this.borderLayoutHeight - 100+ "px");
			this.div_ruleAttachment.setHeight(this.borderLayoutHeight - 100+ "px");
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getNotifications());
		}catch (Exception e) {
			this.window_NotificationsDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.ruleCode.setMaxlength(20);
		this.ruleCodeDesc.setMaxlength(50);
		this.ruleReciepent.setMultiSelectionResult(true);
		this.ruleReciepent.setMultiSelectModuleName("SecurityRole");
		this.ruleAttachment.setMultiSelectionResult(true);
		this.ruleAttachment.setMultiSelectModuleName("DocumentType");
		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
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
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("NotificationsDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnSave"));
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnValidate"));
		this.btnSimulation.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnSimulation"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_NotificationsDialog(Event event) throws Exception {
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_NotificationsDialog);
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
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
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
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("doClose isDataChanged : False");
		}

		if (close) {
			closeDialog(this.window_NotificationsDialog, "Notifications");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aNotifications
	 *            Notifications
	 */
	public void doWriteBeanToComponents(Notifications aNotifications) {
		logger.debug("Entering") ;
		this.ruleCode.setValue(aNotifications.getRuleCode());
		setRuleModule( aNotifications.getRuleModule());
		this.ruleCodeDesc.setValue(aNotifications.getRuleCodeDesc());
		doSetResultCodes();
		if(aNotifications.isNewRecord()){
			this.ruleTemplate.setActualBlock("");
			this.ruleTemplate.setEditable(true);

			this.ruleReciepent.setActualBlock("");
			this.ruleReciepent.setEditable(true);

			this.ruleAttachment.setActualBlock("");
			this.ruleAttachment.setEditable(true);
		}else{

			ruleTemplate.setModule(aNotifications.getRuleModule());
			ruleReciepent.setModule(aNotifications.getRuleModule());
			ruleAttachment.setModule(aNotifications.getRuleModule());

			this.ruleTemplate.setActualBlock(aNotifications.getActualBlockTemplate());
			this.ruleTemplate.buildQuery(aNotifications.getActualBlockTemplate());
			this.ruleTemplate.setSqlQuery(aNotifications.getRuleTemplate());

			this.ruleReciepent.setActualBlock(aNotifications.getActualBlockReciepent());
			this.ruleReciepent.buildQuery(aNotifications.getActualBlockReciepent());
			this.ruleReciepent.setSqlQuery(aNotifications.getRuleReciepent());

			this.ruleAttachment.setActualBlock(aNotifications.getActualBlockAtachment());
			this.ruleAttachment.buildQuery(aNotifications.getActualBlockAtachment());
			this.ruleAttachment.setSqlQuery(aNotifications.getRuleAttachment());

			this.ruleTemplate.setEditable(!isReadOnly("NotificationsDialog_ruleTemplate"));
			this.ruleReciepent.setEditable(!isReadOnly("NotificationsDialog_ruleReciepent"));
			this.ruleAttachment.setEditable(!isReadOnly("NotificationsDialog_ruleAttachment"));

			this.ruleTemplateResult.setValue(aNotifications.getRuleTemplate());
			this.rule.setValue(aNotifications.getRuleTemplate());
		}

		this.recordStatus.setValue(aNotifications.getRecordStatus());
		logger.debug("Leaving");
	}

	public void onChange$ruleModule(Event event){
		logger.debug("Entering");
		ruleTemplate.setModule(this.ruleModule.getSelectedItem().getValue().toString());
		ruleReciepent.setModule(this.ruleModule.getSelectedItem().getValue().toString());
		ruleAttachment.setModule(this.ruleModule.getSelectedItem().getValue().toString());
		logger.debug("Leaving");
	}

	public void onSelectTab(ForwardEvent event) {
		logger.debug("Entering");
		Tab tab=(Tab) event.getOrigin().getTarget();
		if(tab != null){
			if (tab.getId().equals("tab_ruleTemplateDetails") || tab.getId().equals("tab_ruleTemplate")) {
				this.ruleTemplateResult.setValue(this.ruleTemplate.codemirror.getValue());
				this.rule.setValue(this.ruleTemplate.codemirror.getValue());
			}else if(tab.getId().equals("tab_ruleReciepentDetails") || tab.getId().equals("tab_ruleReciepent")){
				this.ruleReciepentResult.setValue(this.ruleReciepent.codemirror.getValue());
				this.rule.setValue(this.ruleReciepent.codemirror.getValue());
			}else if(tab.getId().equals("tab_ruleAttachmentDetails") || tab.getId().equals("tab_ruleAttachment")){
				this.ruleAttachmentResult.setValue(this.ruleAttachment.codemirror.getValue());
				this.rule.setValue(this.ruleAttachment.codemirror.getValue());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNotifications
	 */
	public void doWriteComponentsToBean(Notifications aNotifications) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Code
		try {
			aNotifications.setRuleCode(this.ruleCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Module
		try {
			String strRuleModule =null; 
			if(this.ruleModule.getSelectedItem()!=null){
				strRuleModule = this.ruleModule.getSelectedItem().getValue().toString();
			}
			if(strRuleModule!= null && !PennantConstants.List_Select.equals(strRuleModule)){
				aNotifications.setRuleModule(strRuleModule);	
			}else{
				aNotifications.setRuleModule(null);
				throw new WrongValueException(this.ruleModule, Labels.getLabel("FIELD_IS_MAND", new String[] {Labels.getLabel("label_NotificationsDialog_RuleModule.value")}));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
			aNotifications.setRuleCodeDesc(this.ruleCodeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//Rule Template
		try {
			aNotifications.setRuleTemplate(ruleTemplate.getSqlQuery(true));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aNotifications.setActualBlockTemplate(ruleTemplate.getActualBlock());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		showErrorDetails(wve,tab_ruleTemplate);

		try {
			aNotifications.setRuleReciepent(ruleReciepent.getSqlQuery(true));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Actual Block
		try {
			aNotifications.setActualBlockReciepent(ruleReciepent.getActualBlock());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		showErrorDetails(wve,tab_ruleReciepent);

		try {
			aNotifications.setRuleAttachment(ruleAttachment.getSqlQuery(false));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Actual Block
		try {
			aNotifications.setActualBlockAtachment(ruleAttachment.getActualBlock());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		showErrorDetails(wve,tab_ruleAttachment);

		logger.debug("Leaving");
	}


	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}


	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aNotifications
	 * @throws InterruptedException
	 */
	public void doShowDialog(Notifications aNotifications) throws InterruptedException {
		logger.debug("Entering");
		// if aNotifications == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aNotifications == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aNotifications = getNotificationsService().getNewNotifications();

			setNotifications(aNotifications);
		} else {
			setNotifications(aNotifications);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (aNotifications.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ruleCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.ruleCodeDesc.focus();
				if (!StringUtils.trimToEmpty(aNotifications.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		ComponentsCtrl.applyForward(tab_ruleTemplate, "onSelect=onSelectTab");
		ComponentsCtrl.applyForward(tab_ruleReciepent, "onSelect=onSelectTab");
		ComponentsCtrl.applyForward(tab_ruleAttachment, "onSelect=onSelectTab");
		ComponentsCtrl.applyForward(tab_ruleTemplateDetails, "onSelect=onSelectTab");
		ComponentsCtrl.applyForward(tab_ruleReciepentDetails, "onSelect=onSelectTab");
		ComponentsCtrl.applyForward(tab_ruleAttachmentDetails, "onSelect=onSelectTab");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aNotifications);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_NotificationsDialog);
		} catch (final Exception e) {
			logger.error("doShowDialog() " + e);
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_RuleCode = this.ruleCode.getValue();
		this.oldVar_RuleModule = PennantConstants.List_Select;
		if(this.ruleModule.getSelectedItem() !=null){
			this.oldVar_RuleModule=this.ruleModule.getSelectedItem().getValue().toString();
		}
		this.oldVar_RuleCodeDesc = this.ruleCodeDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.ruleCode.setValue(this.oldVar_RuleCode);
		this.ruleCodeDesc.setValue(this.oldVar_RuleCodeDesc);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();

		if (!StringUtils.trimToEmpty(this.oldVar_RuleCode).equals(StringUtils.trimToEmpty(this.ruleCode.getValue()))) {
			return true;
		}
		String strRuleModule =PennantConstants.List_Select;
		if(this.ruleModule.getSelectedItem()!=null){
			strRuleModule = this.ruleModule.getSelectedItem().getValue().toString();	
		}

		if (!StringUtils.trimToEmpty(this.oldVar_RuleModule).equals(strRuleModule)) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_RuleCodeDesc).equals(StringUtils.trimToEmpty(this.ruleCodeDesc.getValue()))) {
			return true;
		}

		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		//Code
		if (!this.ruleCode.isReadonly()){
			this.ruleCode.setConstraint(new PTStringValidator(Labels.getLabel("label_NotificationsDialog_RuleCode.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Module
		if (!this.ruleModule.isReadonly()){
			//	this.ruleModule.setConstraint(new StaticListValidator(listRuleModule,Labels.getLabel("label_NotificationsDialog_RuleModule.value"),true));
		}
		//Description
		if (!this.ruleCodeDesc.isReadonly()){
			this.ruleCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_NotificationsDialog_RuleCodeDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.ruleCode.setConstraint("");
		this.ruleModule.setConstraint("");
		this.ruleCodeDesc.setConstraint("");
		//this.sQLRule.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.ruleCode.setErrorMessage("");
		this.ruleModule.setErrorMessage("");
		this.ruleCodeDesc.setErrorMessage("");
		//this.sQLRule.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Notifications object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Notifications aNotifications = new Notifications();
		BeanUtils.copyProperties(getNotifications(), aNotifications);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aNotifications.getRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aNotifications.getRecordType()).equals("")) {
				aNotifications.setVersion(aNotifications.getVersion() + 1);
				aNotifications.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aNotifications.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aNotifications, tranType)) {
					refreshList();
					closeDialog(this.window_NotificationsDialog, "Notifications");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Notifications object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		final Notifications aNotifications = getNotificationsService().getNewNotifications();
		aNotifications.setNewRecord(true);
		setNotifications(aNotifications);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.ruleCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getNotifications().isNewRecord()) {
			this.ruleCode.setReadonly(false);
			this.ruleCodeDesc.setReadonly(false);
			this.ruleModule.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ruleCode.setReadonly(true);
			this.ruleCodeDesc.setReadonly(true);
			this.ruleModule.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.ruleCodeDesc.setReadonly(isReadOnly("NotificationsDialog_ruleCodeDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.notifications.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.ruleCode.setReadonly(true);
		this.ruleCodeDesc.setReadonly(true);
		this.ruleModule.setReadonly(true);

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

		this.ruleCode.setValue("");
		this.ruleModule.setSelectedIndex(0);
		this.ruleCodeDesc.setValue("");
		//this.sQLRule.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Notifications aNotifications = new Notifications();
		BeanUtils.copyProperties(getNotifications(), aNotifications);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Notifications object with the components data
		doWriteComponentsToBean(aNotifications);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aNotifications.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aNotifications.getRecordType()).equals("")) {
				aNotifications.setVersion(aNotifications.getVersion() + 1);
				if (isNew) {
					aNotifications.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNotifications.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNotifications.setNewRecord(true);
				}
			}
		} else {
			aNotifications.setVersion(aNotifications.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aNotifications, tranType)) {
				refreshList();
				closeDialog(this.window_NotificationsDialog, "Notifications");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aNotifications
	 *            (Notifications)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Notifications aNotifications, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNotifications.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aNotifications.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNotifications.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aNotifications.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNotifications.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aNotifications);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aNotifications))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
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

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aNotifications.setTaskId(taskId);
			aNotifications.setNextTaskId(nextTaskId);
			aNotifications.setRoleCode(getRole());
			aNotifications.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNotifications, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aNotifications);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNotifications, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aNotifications, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Notifications aNotifications = (Notifications) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getNotificationsService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getNotificationsService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getNotificationsService().doApprove(auditHeader);

						if (aNotifications.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getNotificationsService().doReject(auditHeader);

						if (aNotifications.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_NotificationsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NotificationsDialog, auditHeader);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}


	/**
	 * Loading values into ruleModule combobox<br>
	 * 
	 * @param RuleModule
	 *            
	 */
	public void setRuleModule(String value){
		Comboitem comboitem= new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.ruleModule.appendChild(comboitem);
		this.ruleModule.setSelectedItem(comboitem);
		if(listRuleModule != null){
			for(ValueLabel valueLabel : listRuleModule){
				comboitem = new Comboitem();
				comboitem.setLabel(valueLabel.getLabel());
				comboitem.setValue(valueLabel.getValue());
				comboitem.setParent(this.ruleModule);
				comboitem.setAttribute("data", ruleModule);
				if(valueLabel.getValue().equals(value)){
					this.ruleModule.setSelectedItem(comboitem);
				}
			}
		}
	}

	/**
	 * Adding TemplateCodes into resultSelectionList of javascriptBuilder<br>
	 * 
	 */
	public void doSetResultCodes(){
		logger.debug("Entering") ;
		ArrayList<ValueLabel> templatesList = PennantAppUtil.getTemplatesList();
		if(templatesList != null && !templatesList.isEmpty()){
			for (ValueLabel valueLabel : templatesList) {
				this.ruleTemplate.getResultSelectionList().add(valueLabel);
			}
		}
		ArrayList<ValueLabel> rolesList = PennantAppUtil.getSecurityRolesList();
		if(rolesList != null && !rolesList.isEmpty()){
			for (ValueLabel valueLabel : rolesList) {
				this.ruleReciepent.getResultSelectionList().add(valueLabel);
			}
		}
		ArrayList<ValueLabel> agreementsList = PennantAppUtil.getDocumentDefinitionList();
		if(agreementsList != null && !agreementsList.isEmpty()){
			for (ValueLabel valueLabel : agreementsList) {
				this.ruleAttachment.getResultSelectionList().add(valueLabel);
			}
		}
		logger.debug("Leaving") ;
	}		


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aNotifications
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Notifications aNotifications, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aNotifications.getBefImage(), aNotifications);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aNotifications.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_NotificationsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<Notifications> soNotifications = getNotificationsListCtrl().getSearchObj();
		getNotificationsListCtrl().pagingNotificationsList.setActivePage(0);
		getNotificationsListCtrl().getPagedListWrapper().setSearchObject(soNotifications);
		if (getNotificationsListCtrl().listBoxNotifications != null) {
			getNotificationsListCtrl().listBoxNotifications.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Notifications");
		notes.setReference(getReference());
		notes.setVersion(getNotifications().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return String.valueOf(getNotifications().getRuleCode());
	}


	public void onClick$btnValidate(Event event){
		logger.debug("Entering");
		if(tab_ruleTemplate.isSelected()|| tab_ruleTemplateDetails.isSelected()){
			this.ruleTemplate.getSqlQuery(true);
			this.ruleTemplateResult.setValue(this.ruleTemplate.codemirror.getValue());
			this.rule.setValue(this.ruleTemplate.actualQuery);
		}else if(tab_ruleReciepent.isSelected() || tab_ruleReciepentDetails.isSelected()){
			this.ruleReciepent.getSqlQuery(true);
			this.ruleReciepentResult.setValue(this.ruleReciepent.codemirror.getValue());
			this.rule.setValue(this.ruleReciepent.actualQuery);
		}else if(tab_ruleAttachment.isSelected() || tab_ruleAttachmentDetails.isSelected()){
			this.ruleAttachment.getSqlQuery(false);
			this.ruleAttachmentResult.setValue(this.ruleAttachment.codemirror.getValue());
			this.rule.setValue(this.ruleAttachment.actualQuery);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for creating Simulated window with Existing Fields
	 * @param event
	 * @throws InterruptedException
	 */
	private void createSimulationWindow(String values) throws InterruptedException {
		logger.debug("Entering");
		if(tab_ruleTemplate.isSelected() || tab_ruleTemplateDetails.isSelected()){
			this.ruleTemplate.simulateQuery(window_NotificationsDialog);
		}else if(tab_ruleReciepent.isSelected() || tab_ruleReciepentDetails.isSelected()){
			this.ruleReciepent.simulateQuery(window_NotificationsDialog);
		}else if(tab_ruleAttachment.isSelected() || tab_ruleAttachmentDetails.isSelected() ){
			this.ruleAttachment.simulateQuery(window_NotificationsDialog);
		}
		logger.debug("Leaving");
	}

	/**	 
	 * This Method/Event is called from the java script function Validate/Simulate.
	 * It will open a new window to execute the rule 
	 * 
	 * @param event
	 */
	public void onUser$btnValidate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(StringUtils.trimToEmpty(this.rule.getValue()).equals("")){
			PTMessageUtils.showErrorMessage(Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_Formula")}));
		}else{
			while (event.getData() == null) {
				event = ((ForwardEvent) event).getOrigin();
			}
			Object[] data = (Object[]) event.getData();
			// Check clicking button is for Validation  or Simulation
			boolean isSimulated = (Boolean) data[0];
			JSONArray errors = (JSONArray) data[1];
			JSONArray codeVariables = (JSONArray) data[2];
			boolean isSaveRecord = (Boolean) data[3];

			int conf;
			if(isSimulated){
				String values ="";
				for (int i = 0; i < codeVariables.size(); i++) {

					JSONObject jsonObject = (JSONObject) codeVariables.get(i);
					if(!jsonObject.get("name").equals("Result")){
						values = values + jsonObject.get("name")+",";
					}
				}
				createSimulationWindow(values);
			}else{

				if(errors.size() != 0){
					conf =  (MultiLineMessageBox.show(errors.size()+ 
							PennantJavaUtil.getLabel("message_ErrorCount_CodeMirror"),
							PennantJavaUtil.getLabel("Validate_Title"),
							MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

					if (conf==MultiLineMessageBox.YES){
						Events.postEvent("onUser$errors", window_NotificationsDialog,errors);
					}else{
						//do Nothing
					}
				}else{
					if(isSaveRecord){
						doSave();
					}else{
						conf =  MultiLineMessageBox.show(PennantJavaUtil.getLabel("message_NoError_CodeMirror"),
								" Error Details",MultiLineMessageBox.OK, Messagebox.INFORMATION, true);
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for showing Error Details
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$errors(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if (this.rule.getValue().equalsIgnoreCase("")) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("Code_NotEmpty"));
		} else {
			JSONArray message = (JSONArray) event.getData();

			for (int i = 0; i < message.size(); i++) {

				JSONObject jsonObject = (JSONObject) message.get(i);

				if(jsonObject != null){

					String errorMsg =  (String) jsonObject.get("reason") ;
					String title = " Error : Line-"+jsonObject.get("line") + ",Character-" + 
							jsonObject.get("character");

					int conf;
					if(message.size()-1 != i+1){
						errorMsg = errorMsg +"\n\n"+
								PennantJavaUtil.getLabel("message_ErrorProcess_Conformation");

						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.ERROR, true);
					}else{
						conf = MultiLineMessageBox.show(errorMsg,title,
								MultiLineMessageBox.OK, Messagebox.ERROR, true);
					}

					if (conf==MultiLineMessageBox.NO || conf==MultiLineMessageBox.OK){
						break;
					}else{
						//do Nothing
					}			
				}
			}
		}
		logger.debug("Leaving" + event.toString());
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

	public Notifications getNotifications() {
		return this.notifications;
	}
	public void setNotifications(Notifications notifications) {
		this.notifications = notifications;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}
	public NotificationsService getNotificationsService() {
		return this.notificationsService;
	}

	public void setNotificationsListCtrl(NotificationsListCtrl notificationsListCtrl) {
		this.notificationsListCtrl = notificationsListCtrl;
	}
	public NotificationsListCtrl getNotificationsListCtrl() {
		return this.notificationsListCtrl;
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
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

}
