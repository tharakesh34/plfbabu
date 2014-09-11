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
 * FileName    		:  EMailRuleDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-08-2013    														*
 *                                                                  						*
 * Modified Date    :  01-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rulefactory.emailrule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rulefactory.EMailRule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.mail.MailTemplateService;
//import com.pennant.backend.service.applicationmaster.RuleBuilderService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMasters/RuleBuilder/ruleBuilderDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EMailRuleDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EMailRuleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_EMailRuleDialog; 
	protected Row 			row0; 
	protected Label 		label_RuleCode;
	protected Hlayout 		hlayout_RuleCode;
	protected Space 		space_RuleCode; 

	protected Textbox 		ruleCode; 
	protected Label 		label_RuleModule;
	protected Hlayout 		hlayout_RuleModule;
	protected Space 		space_RuleModule; 

	protected Combobox 		ruleModule; 
	protected Row 			row1; 
	protected Label 		label_RuleCodeDesc;
	protected Hlayout 		hlayout_RuleCodeDesc;
	protected Space 		space_RuleCodeDesc; 

	protected Textbox 		ruleCodeDesc; 

	protected JavaScriptBuilder Sql_Rule;
	private Codemirror		rule;
 
	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;
	

	// not auto wired vars
	private EMailRule emailRule; // overhanded per param
	//private transient RuleBuilderListCtrl ruleBuilderListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_RuleCode;
	private transient String  		oldVar_RuleModule;
	private transient String  		oldVar_RuleCodeDesc;
	private transient String  		oldVar_SQLRule;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_RuleBuilderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 
	protected Button btnValidate; 
	protected Button btnSimulate; 


	// ServiceDAOs / Domain Classes
	//private transient EMailRuleService emailRuleService;
	private transient PagedListService pagedListService;
	private transient RuleService ruleService;
	private transient MailTemplateService mailTemplateService;
	private static List<RuleModule> listRuleModule=null;
	private List<MailTemplate> mailTemplateList;

	/**
	 * default constructor.<br>
	 */
	public EMailRuleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected RuleBuilder object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EMailRuleDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		listRuleModule =  getRuleService().getRuleModules("MAIL");
		try {
			
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (args.containsKey("EmailRule")) {
				this.emailRule = (EMailRule) args.get("EmailRule");
				EMailRule befImage =new EMailRule();
				BeanUtils.copyProperties(this.emailRule, befImage);
				this.emailRule.setBefImage(befImage);

				setEMailRule(this.emailRule);
			} else {
				setEMailRule(null);
			}
			/*doLoadWorkFlow(this.emailRule.isWorkflow(),this.emailRule.getWorkflowId(),this.emailRule.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "RuleBuilderDialog");
			}else{
				getUserWorkspace().alocateAuthorities("RuleBuilderDialog");
			}*/

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the ruleBuilderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete ruleBuilder here.
			

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getEMailRule());
		} catch (Exception e) {
			createException(window_EMailRuleDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
	//	doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
/*	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}*/

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_EMailRuleDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_RuleBuilderDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		try {


			ScreenCTL.displayNotes(getNotes("RuleBuilder",getEMailRule().getRuleCode(),getEMailRule().getVersion()),this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());

	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRuleBuilder
	 * @throws InterruptedException
	 */
	public void doShowDialog(EMailRule aEMailRule) throws InterruptedException {
		logger.debug("Entering") ;

		// if aRuleBuilder == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aEMailRule == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			//aEMailRule = getRuleBuilderService().getNewRuleBuilder(getEntityCode());
			aEMailRule = new EMailRule();
			aEMailRule.setNewRecord(true);
			setEMailRule(aEMailRule);
		} else {
			setEMailRule(aEMailRule);
			
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aEMailRule);
			// set ReadOnly mode accordingly if the object is new or not.

			//displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aEMailRule.isNewRecord()));

			doStoreInitValues();

			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_EMailRuleDialog);
			this.btnSave.setVisible(true);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode){
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.ruleCode,this.ruleCodeDesc));

		if (getEMailRule().isNewRecord()){
			//setComponentAccessType("RuleBuilderDialog_RuleCode", false, this.ruleCode, this.space_RuleCode, this.label_RuleCode, this.hlayout_RuleCode,null);
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(emailRule.getRecordType())))) {
			tempReadOnly=true;
		}

		setComponentAccessType("RuleBuilderDialog_RuleCode", false, this.ruleCode, this.space_RuleCode, this.label_RuleCode, this.hlayout_RuleCode,null);
		setComponentAccessType("RuleBuilderDialog_RuleModule", true, this.ruleModule, this.space_RuleModule, this.label_RuleModule, this.hlayout_RuleModule,null);
		setRowInvisible(this.row0,this.hlayout_RuleCode,this.hlayout_RuleModule);
		setComponentAccessType("RuleBuilderDialog_RuleCodeDesc", false, this.ruleCodeDesc, this.space_RuleCodeDesc, this.label_RuleCodeDesc, this.hlayout_RuleCodeDesc,null);
		this.ruleModule.setDisabled(true);
		//setRuleAccess("RuleBuilderDialog_SQLRule", tempReadOnly, this.Sql_Rule,null);
		//setComponentAccessType("RuleBuilderDialog_SQLRule", tempReadOnly, this.sQLRule, this.space_SQLRule, this.label_SQLRule, this.hlayout_SQLRule,null);
		//setRowInvisible(this.row1,this.hlayout_RuleCodeDesc,this.hlayout_SQLRule);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;

		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RuleBuilderDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RuleBuilderDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RuleBuilderDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RuleBuilderDialog_btnSave"));	
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.ruleCode.setMaxlength(20);
		this.ruleCodeDesc.setMaxlength(50);
		
		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRuleBuilder
	 *            RuleBuilder
	 */
	public void doWriteBeanToComponents(EMailRule aEMailRule) {
		logger.debug("Entering") ;
		this.ruleCode.setValue(aEMailRule.getRuleCode());
		//	fillComboBox(this.ruleModule, aRuleBuilder.getRuleModule(), listRuleModule,"",false);
		setRuleModule( aEMailRule.getRuleModule());
		
		this.ruleCodeDesc.setValue(aEMailRule.getRuleCodeDesc());
		setMailTemplateList(getMailTemplateService().getMailTemplates());
		setTemplateCodes();
		//this.sQLRule.setValue(aRuleBuilder.getSQLRule());
		if(aEMailRule.isNewRecord()){
			Sql_Rule.setActualBlock("");
			Sql_Rule.setEditable(true);
		}else{
			Sql_Rule.setActualBlock(aEMailRule.getActualBlock());
			Sql_Rule.setRuleModule(aEMailRule.getRuleModuleObj());
			Sql_Rule.buildQuery(aEMailRule.getActualBlock());
			this.Sql_Rule.setSqlQuery(aEMailRule.getSQLRule());
			this.rule.setValue(aEMailRule.getSQLRule());
		}
		this.recordStatus.setValue(aEMailRule.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aEMailRule.getRecordType()));
		logger.debug("Leaving");
	}


	/**
	 * Loading values into ruleModule combobox<br>
	 * 
	 * @param RuleModule
	 *            
	 */
	public void setRuleModule( String value){
		Comboitem comboitem= new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.ruleModule.appendChild(comboitem);
		for(RuleModule ruleModule : listRuleModule){
			comboitem = new Comboitem();
			comboitem.setLabel(ruleModule.getRbmModule());
			comboitem.setValue(ruleModule.getRbmModule());
			comboitem.setParent(this.ruleModule);
			this.ruleModule.setSelectedItem(comboitem);
			comboitem.setAttribute("data", ruleModule);
			if(ruleModule.getRbmModule().equals(value)){
				this.ruleModule.setSelectedItem(comboitem);
			}
		}
		
	}

	/**
	 * Adding TemplateCodes into resultSelectionList of javascriptBuilder<br>
	 * 
	 */
	public void setTemplateCodes(){
		logger.debug("Entering") ;
		ArrayList<ValueLabel> templatesList = PennantAppUtil.getTemplatesList();
		if(templatesList != null && !templatesList.isEmpty()){
			for (ValueLabel valueLabel : templatesList) {
				this.Sql_Rule.getResultSelectionList().add(valueLabel);
			}
		}
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRuleBuilder
	 */
	public void doWriteComponentsToBean(EMailRule aEMailRule) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Code
		try {
			aEMailRule.setRuleCode(this.ruleCode.getValue());
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
				aEMailRule.setRuleModule(strRuleModule);	
			}else{
				aEMailRule.setRuleModule(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
			aEMailRule.setRuleCodeDesc(this.ruleCodeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//Sql Rule
		try {
			aEMailRule.setSQLRule(Sql_Rule.getSqlQuery(true));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Actual Block
		try {
			aEMailRule.setActualBlock(Sql_Rule.getActualBlock());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
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
		//Code
		if (!this.ruleCode.isReadonly()){
			this.ruleCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleBuilderDialog_RuleCode.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Module
		if (!this.ruleModule.isReadonly()){
			//	this.ruleModule.setConstraint(new StaticListValidator(listRuleModule,Labels.getLabel("label_RuleBuilderDialog_RuleModule.value"),true));
		}
		//Description
		if (!this.ruleCodeDesc.isReadonly()){
			this.ruleCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleBuilderDialog_RuleCodeDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Rule
		if (this.Sql_Rule==null || this.Sql_Rule.getSqlQuery(true).equals("")){
			throw new WrongValueException(Sql_Rule,"Rule Should not be empty");
			// ((Constrainted) this.Sql_Query).setConstraint(new PTStringValidator(Labels.getLabel("label_QueryDialog_SqlQuery.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
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
		//Code
		//Module
		//Description
		//Rule
		//Actual Block
	}

	/**
	 * Remove the Validation by setting empty constraints.
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

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	/*private void refreshList(){
		final JdbcSearchObject<RuleBuilder> soRuleBuilder = getRuleBuilderListCtrl().getSearchObj();
		getRuleBuilderListCtrl().pagingRuleBuilderList.setActivePage(0);
		getRuleBuilderListCtrl().getPagedListWrapper().setSearchObject(soRuleBuilder);
		if(getRuleBuilderListCtrl().listBoxRuleBuilder!=null){
			getRuleBuilderListCtrl().listBoxRuleBuilder.getListModel();
		}
	} */


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
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
		logger.debug("Entering");
		boolean close=true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_EMailRuleDialog, "EMailRule");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Deletes a RuleBuilder object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	/*private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final EMailRule aEMailRule = new EMailRule();
		BeanUtils.copyProperties(getEMailRule(), aEMailRule);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aEMailRule.getRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aEMailRule.getRecordType()).equals("")){
				aEMailRule.setVersion(aEMailRule.getVersion()+1);
				aEMailRule.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aEMailRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aEMailRule.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEMailRule.getNextTaskId(), aEMailRule);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aEMailRule,tranType)){
					//refreshList();
					closeDialog(this.window_EMailRuleDialog, "EMailRule"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_EMailRuleDialog,e);
			}

		}
		logger.debug("Leaving");
	}
*/

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
		final EMailRule aEMailRule = new EMailRule();
		BeanUtils.copyProperties(getEMailRule(), aEMailRule);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aEMailRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEMailRule.getNextTaskId(), aEMailRule);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aEMailRule.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the RuleBuilder object with the components data
			doWriteComponentsToBean(aEMailRule);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aEMailRule.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aEMailRule.getRecordType()).equals("")){
				aEMailRule.setVersion(aEMailRule.getVersion()+1);
				if(isNew){
					aEMailRule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aEMailRule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEMailRule.setNewRecord(true);
				}
			}
		}else{
			aEMailRule.setVersion(aEMailRule.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		/*try {

			if(doProcess(aEMailRule,tranType)){
				//doWriteBeanToComponents(aRuleBuilder);
				//refreshList();
				closeDialog(this.window_EMailRuleDialog, "EMailRule");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_EMailRuleDialog,e);
		}
		logger.debug("Leaving");*/
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

/*	private boolean doProcess(EMailRule aEMailRule,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aEMailRule.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aEMailRule.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEMailRule.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			aEMailRule.setTaskId(getTaskId());
			aEMailRule.setNextTaskId(getNextTaskId());
			aEMailRule.setRoleCode(getRole());
			aEMailRule.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aEMailRule, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aEMailRule, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aEMailRule, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */

	/*private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		EMailRule aEMailRule = (EMailRule) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getEMailRuleService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getEMailRuleService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEMailRuleService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aEMailRule.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEMailRuleService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aEMailRule.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), new String[]{""},PennantConstants.ERR_SEV_ERROR));
						retValue = ErrorControl.showErrorControl(this.window_RuleBuilderDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_EMailRuleDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("RuleBuilder",aEMailRule.getRuleCode(),aEMailRule.getVersion()),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
*/
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(EMailRule aEMailRule, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEMailRule.getBefImage(), aEMailRule);   
		return new AuditHeader(aEMailRule.getRuleCode(),null,null,null,auditDetail,aEMailRule.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public EMailRule getEMailRule() {
		return this.emailRule;
	}

	public void setEMailRule(EMailRule emailRule) {
		this.emailRule = emailRule;
	}

	/*public void setEMailRuleService(EMailRuleService emailRuleService) {
		this.emailRuleService = emailRuleService;
	}

	public EMailRuleService getEMailRuleService() {
		return this.emailRuleService;
	}*/

	

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
	public void onClick$btnValidate(Event event){
		this.Sql_Rule.getSqlQuery(true);
		this.rule.setValue(this.Sql_Rule.actualQuery);
	//Events.postEvent("onClick", btnSimulate, event);
		//w:onClick="validateJs(true,false);" 
 	}
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	public RuleService getRuleService() {
		return this.ruleService;
	}
	/**
	 * Method for creating Simulated window with Existing Fields
	 * @param event
	 * @throws InterruptedException
	 */
	private void createSimulationWindow(String Values) throws InterruptedException {
		logger.debug("Entering");

		String[] Variables = Values.split(",");

		this.Sql_Rule.simulateQuery(window_EMailRuleDialog);
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
						Events.postEvent("onUser$errors", window_EMailRuleDialog,errors);
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

	public MailTemplateService getMailTemplateService() {
		return mailTemplateService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public List<MailTemplate> getMailTemplateList() {
		return mailTemplateList;
	}

	public void setMailTemplateList(List<MailTemplate> mailTemplateList) {
		this.mailTemplateList = mailTemplateList;
	}



}

