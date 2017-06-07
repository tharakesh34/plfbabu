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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.JSRuleReturnType;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Notifications/NotificationsDialog.zul file.
 */
public class NotificationsDialogCtrl extends GFCBaseCtrl<Notifications> {
	private static final long serialVersionUID = -6945930303723518608L;
	private final static Logger logger = Logger.getLogger(NotificationsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 			window_NotificationsDialog; 	    

	protected Textbox 			ruleCode;  					        
	protected Textbox 			ruleCodeDesc;  					    
	protected Combobox 			ruleModule;  					    
	protected Combobox 			templateType;  					    

	protected Tab   			tab_ruleTemplate; 					        
	protected Tab   			tab_ruleReciepent; 					        
	protected Tab   			tab_ruleAttachment; 					    
	
	protected Tabpanel			tabPanel_ruleTemplate;
	protected Tabpanel			tabPanel_ruleReciepent;
	protected Tabpanel			tabPanel_ruleAttachment;

	protected JavaScriptBuilder ruleTemplate; 					
	protected JavaScriptBuilder ruleReciepent; 					
	protected JavaScriptBuilder ruleAttachment; 				

	protected Groupbox 			gb_statusDetails;
	protected Grid 				grid_basicDetail;
	
	protected Button 			btnValidate; 		
	protected Button 			btnSimulation; 		
	
	private transient boolean 	validationOn;
	
	// not auto wired variables
	private Notifications 					notifications; // overHanded per parameter
	private transient NotificationsListCtrl notificationsListCtrl; // overHanded per parameter
	// ServiceDAOs / Domain Classes
	private transient NotificationsService 	notificationsService;
	
	private List<ValueLabel> 				listRuleModule = new ArrayList<ValueLabel>();
	private List<ValueLabel> 				listTemplateTypes = new ArrayList<ValueLabel>();
	
	/**
	 * default constructor.<br>
	 */
	public NotificationsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "NotificationsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Notifications object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NotificationsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_NotificationsDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("notifications")) {
				this.notifications = (Notifications) arguments.get("notifications");
				Notifications befImage = new Notifications();
				BeanUtils.copyProperties(this.notifications, befImage);
				this.notifications.setBefImage(befImage);
				setNotifications(this.notifications);
			} else {
				setNotifications(null);
			}

			// READ OVERHANDED parameters !
			// we get the NotificationsListWindow controller. So we have access to it and can synchronize the shown data
			// when we do insert, edit or delete Notifications here.
			if (arguments.containsKey("notificationsListCtrl")) {
				setNotificationsListCtrl((NotificationsListCtrl) arguments.get("notificationsListCtrl"));
			} else {
				setNotificationsListCtrl(null);
			}

			doLoadWorkFlow(this.notifications.isWorkflow(), this.notifications.getWorkflowId(),
					this.notifications.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "NotificationsDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			this.tabPanel_ruleTemplate.setHeight(this.borderLayoutHeight - 100 + "px");
			this.tabPanel_ruleReciepent.setHeight(this.borderLayoutHeight - 100 + "px");
			this.tabPanel_ruleAttachment.setHeight(this.borderLayoutHeight - 100 + "px");

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.notifications);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_NotificationsDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering");

		doSave();

		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering");

		doEdit();

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering");

		MessageUtil.showHelpWindow(event, this.window_NotificationsDialog);

		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering");

		doDelete();

		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering");

		doCancel();

		logger.debug("Leaving");
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
		doShowNotes(this.notifications);
	}

	/**
	 * Set the Rule Module
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$ruleModule(Event event) {
		logger.debug("Entering");

		doSetTemplateList(this.ruleModule.getSelectedItem().getValue().toString(), this.templateType.getSelectedItem()
				.getValue().toString());

		logger.debug("Leaving");
	}

	/**
	 * Set the Template Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$templateType(Event event) {
		logger.debug("Entering");

		doSetTemplateList(this.ruleModule.getSelectedItem().getValue().toString(), this.templateType.getSelectedItem()
				.getValue().toString());

		logger.debug("Leaving");
	}

	/**
	 * build the rule
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering");

		if (this.tab_ruleTemplate.isSelected()) {
			validate(this.ruleTemplate);
		} else if (this.tab_ruleReciepent.isSelected()) {
			validate(this.ruleReciepent);
		} else if (this.tab_ruleAttachment.isSelected()) {
			validate(this.ruleAttachment);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Simulation of builded code
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSimulation(Event event) throws Exception {
		logger.debug("Entering");

		if (this.tab_ruleTemplate.isSelected()) {
			validate(this.ruleTemplate);
		} else if (this.tab_ruleReciepent.isSelected()) {
			validate(this.ruleReciepent);
		} else if (this.tab_ruleAttachment.isSelected()) {
			validate(this.ruleAttachment);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.ruleCode.setMaxlength(20);
		this.ruleCodeDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

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

		getUserWorkspace().allocateAuthorities("NotificationsDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnSave"));
		this.btnCancel.setVisible(false);

		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnValidate"));
		this.btnSimulation.setVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnSimulation"));

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

		doWriteBeanToComponents(this.notifications.getBefImage());
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
		logger.debug("Entering");

		this.ruleCode.setValue(aNotifications.getRuleCode());
		this.ruleCodeDesc.setValue(aNotifications.getRuleCodeDesc());
		this.recordStatus.setValue(aNotifications.getRecordStatus());
		
		this.listRuleModule = PennantStaticListUtil.getMailModulesList();
		this.listTemplateTypes = PennantStaticListUtil.getTemplateForList();
		
		fillComboBox(this.ruleModule, aNotifications.getRuleModule(), listRuleModule, "");
		fillComboBox(this.templateType, aNotifications.getTemplateType(), listTemplateTypes, "");

		doSetTemplateList(this.ruleModule.getSelectedItem().getValue().toString(), this.templateType.getSelectedItem()
				.getValue().toString());

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param String
	 *            moduelCode
	 * @param String
	 *            templateType
	 */
	private void doSetTemplateList(String moduelCode, String templateType) {
		logger.debug("Entering");

		this.tab_ruleReciepent.setDisabled(true);
		if (!StringUtils.trimToEmpty(moduelCode).equals(PennantConstants.List_Select)
				&& !StringUtils.trimToEmpty(templateType).equals(PennantConstants.List_Select)) {
			switch (templateType) {
			case NotificationConstants.TEMPLATE_FOR_AE:
				this.tab_ruleReciepent.setDisabled(false);
				break;
			case NotificationConstants.TEMPLATE_FOR_QP:
				this.tab_ruleReciepent.setDisabled(false);
				break;
			case NotificationConstants.TEMPLATE_FOR_GE:
				this.tab_ruleReciepent.setDisabled(false);
				break;
			case NotificationConstants.TEMPLATE_FOR_PO:
				this.tab_ruleReciepent.setDisabled(false);
				break;
			case NotificationConstants.TEMPLATE_FOR_TAT:
				this.tab_ruleReciepent.setDisabled(false);
				break;
			default:
				this.tab_ruleReciepent.setDisabled(true);
				this.ruleReciepent.setActualBlock("");
				this.ruleReciepent.setSqlQuery("");
				break;
			}
		}

		doSetRuleBuilder();

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNotifications
	 * @throws Exception
	 */
	public void doWriteComponentsToBean(Notifications aNotifications) throws Exception {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aNotifications.setRuleCode(this.ruleCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Description
		try {
			aNotifications.setRuleCodeDesc(this.ruleCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Module
		try {
			aNotifications.setRuleModule(this.ruleModule.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template Type
		try {
			aNotifications.setTemplateType(this.templateType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Rule Template
		try {
			if (wve.isEmpty() && !this.ruleTemplate.isReadOnly()) {
				validate(this.ruleTemplate);
				aNotifications.setRuleTemplate(this.ruleTemplate.getActualQuery());
				aNotifications.setActualBlockTemplate(this.ruleTemplate.getActualBlock());
				aNotifications.setTemplateTypeFields(this.ruleTemplate.getFields());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, tab_ruleTemplate);

		// Rule Recipient
		if (wve.isEmpty() && !tab_ruleReciepent.isDisabled() && !this.ruleReciepent.isReadOnly()) {
			try {
				validate(this.ruleReciepent);
				aNotifications.setRuleReciepent(this.ruleReciepent.getActualQuery());
				aNotifications.setActualBlockReciepent(this.ruleReciepent.getActualBlock());
				aNotifications.setRuleReciepentFields(this.ruleReciepent.getFields());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve, tab_ruleReciepent);
		}

		// Rule Attachment
		try {
			if (wve.isEmpty() && !this.ruleAttachment.isReadOnly()) {
				validate(this.ruleAttachment);
				aNotifications.setRuleAttachment(this.ruleAttachment.getActualQuery());
				aNotifications.setActualBlockAtachment(this.ruleAttachment.getActualBlock());
				aNotifications.setRuleAttachmentFields(this.ruleAttachment.getFields());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, tab_ruleAttachment);

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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aNotifications
	 * @throws Exception
	 */
	public void doShowDialog(Notifications aNotifications) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aNotifications.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ruleCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.ruleCodeDesc.focus();
				if (StringUtils.isNotBlank(aNotifications.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aNotifications);

			// FIXME Temporary Not Visible
			this.btnSimulation.setVisible(false);
			this.btnValidate.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_NotificationsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		// Code
		if (!this.ruleCode.isReadonly()) {
			this.ruleCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_NotificationsDialog_RuleCode.value"), PennantRegularExpressions.REGEX_NAME, true));
		}

		// Description
		if (!this.ruleCodeDesc.isReadonly()) {
			this.ruleCodeDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_NotificationsDialog_RuleCodeDesc.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		
		// Module
		if (!this.ruleModule.isDisabled()) {
			this.ruleModule.setConstraint(new StaticListValidator(listRuleModule, Labels.getLabel("label_NotificationsDialog_RuleModule.value")));
		}
		
		// Template Type
		if (!this.templateType.isDisabled()) {
			this.templateType.setConstraint(new StaticListValidator(listTemplateTypes, Labels.getLabel("label_NotificationsDialog_TemplateType.value")));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.ruleCode.setConstraint("");
		this.ruleCodeDesc.setConstraint("");
		this.ruleModule.setConstraint("");
		this.templateType.setConstraint("");

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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.ruleCode.setErrorMessage("");
		this.ruleCodeDesc.setErrorMessage("");
		this.ruleModule.setErrorMessage("");
		this.templateType.setErrorMessage("");

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a Notifications object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Notifications aNotifications = new Notifications();
		BeanUtils.copyProperties(this.notifications, aNotifications);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_NotificationsDialog_RuleCode.value") + " : " + aNotifications.getRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aNotifications.getRecordType())) {
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
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.notifications.isNewRecord()) {
			this.ruleCode.setReadonly(false);
			this.ruleCodeDesc.setReadonly(false);
			this.ruleModule.setDisabled(false);
			this.templateType.setDisabled(false);
			this.btnCancel.setVisible(false);

			this.ruleTemplate.setTreeTabVisible(true);
			this.ruleReciepent.setTreeTabVisible(true);
			this.ruleAttachment.setTreeTabVisible(true);
		} else {
			this.ruleCode.setReadonly(true);
			this.ruleCodeDesc.setReadonly(true);
			this.ruleModule.setDisabled(true);
			this.templateType.setDisabled(true);
			this.btnCancel.setVisible(true);

			if (enqiryModule) {
				this.ruleTemplate.setTreeTabVisible(false);
				this.ruleReciepent.setTreeTabVisible(false);
				this.ruleAttachment.setTreeTabVisible(false);

				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			} else {
				this.ruleTemplate.setTreeTabVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnValidate"));
				this.ruleReciepent.setTreeTabVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnValidate"));
				this.ruleAttachment.setTreeTabVisible(getUserWorkspace().isAllowed("button_NotificationsDialog_btnValidate"));
			}

			if (PennantConstants.RECORD_TYPE_DEL.equals(this.notifications.getRecordType())) {
				this.ruleTemplate.setTreeTabVisible(false);
				this.ruleReciepent.setTreeTabVisible(false);
				this.ruleAttachment.setTreeTabVisible(false);
			}
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
		this.templateType.setReadonly(true);

		this.ruleTemplate.setTreeTabVisible(false);
		this.ruleTemplate.setSelectedTab(RuleConstants.TAB_DESIGN);

		this.ruleReciepent.setTreeTabVisible(false);
		this.ruleReciepent.setSelectedTab(RuleConstants.TAB_DESIGN);

		this.ruleAttachment.setTreeTabVisible(false);
		this.ruleAttachment.setSelectedTab(RuleConstants.TAB_DESIGN);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}

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
		this.ruleCodeDesc.setValue("");
		this.ruleModule.setSelectedIndex(0);
		this.templateType.setSelectedIndex(0);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		final Notifications aNotifications = new Notifications();
		BeanUtils.copyProperties(this.notifications, aNotifications);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Notifications object with the components data
		doWriteComponentsToBean(aNotifications);

		// Write the additional validations as per below example get the selected branch object from the list box Do
		// data level validations here

		isNew = aNotifications.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aNotifications.getRecordType())) {
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

		aNotifications.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aNotifications.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNotifications.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aNotifications.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNotifications.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aNotifications);
				}

				if (isNotesMandatory(taskId, aNotifications)) {
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

			aNotifications.setTaskId(taskId);
			aNotifications.setNextTaskId(nextTaskId);
			aNotifications.setRoleCode(getRole());
			aNotifications.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNotifications, tranType);
			String operationRefs = getServiceOperations(taskId, aNotifications);

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
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = this.notificationsService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = this.notificationsService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = this.notificationsService.doApprove(auditHeader);

						if (aNotifications.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = this.notificationsService.doReject(auditHeader);

						if (aNotifications.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_NotificationsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NotificationsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.notifications), true);
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

		logger.debug("Leaving");

		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aNotifications
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Notifications aNotifications, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aNotifications.getBefImage(), aNotifications);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aNotifications.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();

		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_NotificationsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		this.notificationsListCtrl.search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(this.notifications.getRuleCode());
	}

	/**
	 * Validate the SQL Query and then gives SQL Query and Actual Block.
	 */
	private void validate(JavaScriptBuilder javaScriptBuilder) throws Exception {
		logger.debug("Entering");

		javaScriptBuilder.getSqlQuery();
		javaScriptBuilder.setSelectedTab(RuleConstants.TAB_SCRIPT);
		
		logger.debug("Leaving");
	}
	
	/**
	 * set the Java Script Builder Component
	 */
	private void doSetRuleBuilder() {
		logger.debug("Entering");
		
		// Rule Building
		String ruleModuleValue = this.ruleModule.getSelectedItem().getValue();
		String templateTypeValue = this.templateType.getSelectedItem().getValue();
		int noOfRowsVisible = this.grid_basicDetail.getRows().getVisibleItemCount();

		List<JSRuleReturnType> jsRuleReturnTypeList = null;
		
		if (!StringUtils.equals(ruleModuleValue, PennantConstants.List_Select)
				&& !StringUtils.equals(templateTypeValue, PennantConstants.List_Select)) {
			this.ruleTemplate.setModule(ruleModuleValue);
			this.ruleTemplate.setMode(RuleConstants.RULEMODE_SELECTFIELDLIST);
			this.ruleTemplate.setNoOfRowsVisible(noOfRowsVisible);
			this.ruleTemplate.setRuleType(RuleReturnType.INTEGER);
			this.ruleTemplate.setEvent(ruleModuleValue);
			
			jsRuleReturnTypeList = new ArrayList<JSRuleReturnType>();
			JSRuleReturnType jsRuleReturnType = new JSRuleReturnType();

			ArrayList<ValueLabel> templatesList = PennantAppUtil.getTemplatesList(ruleModuleValue, templateTypeValue);
			
			if (templatesList != null && !templatesList.isEmpty()) {
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_COMBOBOX);
				jsRuleReturnType.setListOfData(templatesList);
			} else {
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_DECIMAL);
			}
			
			jsRuleReturnTypeList.add(jsRuleReturnType);
			this.ruleTemplate.setJsRuleReturnTypeList(jsRuleReturnTypeList);

			this.ruleReciepent.setModule(ruleModuleValue);
			this.ruleReciepent.setMode(RuleConstants.RULEMODE_SELECTFIELDLIST);
			this.ruleReciepent.setNoOfRowsVisible(noOfRowsVisible);
			this.ruleReciepent.setRuleType(RuleReturnType.STRING);
			this.ruleReciepent.setEvent(ruleModuleValue);
			
			jsRuleReturnTypeList = new ArrayList<JSRuleReturnType>();
			jsRuleReturnType = new JSRuleReturnType();
			jsRuleReturnType.setResultLabel("");
			jsRuleReturnType.setMultiSelection(true);
			jsRuleReturnType.setModuleName("SecurityRole");
			//jsRuleReturnType.setValueColumn("RoleID");
			//jsRuleReturnType.setValidateColumns(new String[] { "RoleID", "RoleCd" });
			jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_EXTENDEDCOMBOBOX);
			
			jsRuleReturnTypeList.add(jsRuleReturnType);
			this.ruleReciepent.setJsRuleReturnTypeList(jsRuleReturnTypeList);

			this.ruleAttachment.setModule(ruleModuleValue);
			this.ruleAttachment.setMode(RuleConstants.RULEMODE_SELECTFIELDLIST);
			this.ruleAttachment.setNoOfRowsVisible(noOfRowsVisible);
			this.ruleAttachment.setRuleType(RuleReturnType.STRING);
			this.ruleAttachment.setEvent(ruleModuleValue);
			
			jsRuleReturnTypeList = new ArrayList<JSRuleReturnType>();
			jsRuleReturnType = new JSRuleReturnType();
			jsRuleReturnType.setResultLabel("");
			jsRuleReturnType.setMultiSelection(true);
			jsRuleReturnType.setModuleName("DocumentType");
			//jsRuleReturnType.setValueColumn("DocTypeCode");
			//jsRuleReturnType.setValidateColumns(new String[] { "DocTypeCode", "DocTypeDesc" });
			
			jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_EXTENDEDCOMBOBOX);
			
			jsRuleReturnTypeList.add(jsRuleReturnType);
			this.ruleAttachment.setJsRuleReturnTypeList(jsRuleReturnTypeList);
			
			if (this.notifications.isNewRecord()) {
				if (!StringUtils.equals(ruleModuleValue, PennantConstants.List_Select)) {
					this.ruleTemplate.setTreeTabVisible(true);
					this.ruleTemplate.setSelectedTab(RuleConstants.TAB_DESIGN);
					this.ruleTemplate.setActualBlock("");
					this.ruleTemplate.setEditable(true);
					
					this.ruleReciepent.setTreeTabVisible(true);
					this.ruleReciepent.setSelectedTab(RuleConstants.TAB_DESIGN);
					this.ruleReciepent.setActualBlock("");
					this.ruleReciepent.setEditable(true);
					
					this.ruleAttachment.setTreeTabVisible(true);
					this.ruleAttachment.setSelectedTab(RuleConstants.TAB_DESIGN);
					this.ruleAttachment.setActualBlock("");
					this.ruleAttachment.setEditable(true);
				} else {
					this.ruleTemplate.setTreeTabVisible(false);
					this.ruleReciepent.setTreeTabVisible(false);
					this.ruleAttachment.setTreeTabVisible(false);
				}
			} else {
				this.ruleTemplate.setFields(this.notifications.getTemplateTypeFields());
				this.ruleTemplate.setSqlQuery(this.notifications.getRuleTemplate());
				this.ruleTemplate.setActualBlock(this.notifications.getActualBlockTemplate());
				this.ruleTemplate.buildQuery(this.notifications.getActualBlockTemplate());
				this.ruleTemplate.setFields(this.notifications.getTemplateTypeFields());
				
				this.ruleReciepent.setFields(this.notifications.getRuleReciepentFields());
				this.ruleReciepent.setSqlQuery(this.notifications.getRuleReciepent());
				this.ruleReciepent.setActualBlock(this.notifications.getActualBlockReciepent());
				this.ruleReciepent.buildQuery(this.notifications.getActualBlockReciepent());
				this.ruleReciepent.setFields(this.notifications.getRuleReciepentFields());
				
				this.ruleAttachment.setFields(this.notifications.getRuleAttachmentFields());
				this.ruleAttachment.setSqlQuery(this.notifications.getRuleAttachment());
				this.ruleAttachment.setActualBlock(this.notifications.getActualBlockAtachment());
				this.ruleAttachment.buildQuery(this.notifications.getActualBlockAtachment());
				this.ruleAttachment.setFields(this.notifications.getRuleAttachmentFields());
			}
		}
		
		logger.debug("Leaving");
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

	public void setNotifications(Notifications notifications) {
		this.notifications = notifications;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setNotificationsListCtrl(NotificationsListCtrl notificationsListCtrl) {
		this.notificationsListCtrl = notificationsListCtrl;
	}
}
