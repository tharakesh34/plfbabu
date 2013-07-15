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
 * FileName    		:  SecurityUserDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-8-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-8-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityuser;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.UserService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.administration.securityuser.changepassword.ChangePasswordModel;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityUserDialogCtrl extends GFCBaseCtrl implements Serializable,Constraint{

	private static final long serialVersionUID = 952561911227552664L;
	private final static Logger logger = Logger.getLogger(SecurityUserDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     window_SecurityUserDialog;                // autoWired
	protected Textbox    usrLogin;                                 // autoWired
	protected Textbox    usrPwd;                                   // autoWired
	protected Textbox    usrnewPwd;                                // autoWired
	protected Textbox    usrConfirmPwd;                            // autoWired
	protected Textbox    userStaffID;                              // autoWired
	protected Textbox    usrFName;                                 // autoWired
	protected Textbox    usrMName;                                 // autoWired
	protected Textbox    usrLName;                                 // autoWired
	protected Textbox    usrMobile;                                // autoWired
	protected Textbox    usrEmail;                                 // autoWired
	protected Checkbox   usrEnabled;                               // autoWired
	protected Timebox    usrCanSignonFrom;                         // autoWired
	protected Timebox    usrCanSignonTo;                           // autoWired
	protected Checkbox   usrCanOverrideLimits;                     // autoWired
	protected Checkbox   usrAcExp;                                 // autoWired
	protected Checkbox   usrCredentialsExp;                        // autoWired
	protected Checkbox   usrAcLocked;                              // autoWired
	protected Textbox    usrLanguage;                              // autoWired
	protected Combobox   usrDftAppId;                              // autoWired
	protected Textbox    usrBranchCode;                            // autoWired
	protected Textbox    usrDeptCode;                              // autoWired
	protected Checkbox   usrIsMultiBranch;                         // autoWired
	protected Row        rowSecurityUserDialogUsrPwd;              // autoWired
	protected Row        rowSecurityUserDialogUsrConfirmPwd;       // autoWired
	protected Label      recordStatus;                             // autoWired
	protected Radiogroup userAction;                               // autoWired
	protected Groupbox   groupboxWf;                               // autoWired
	protected Row        statusRow;                                // autoWired
	protected Panel      panelPasswordInstructions;                // autoWired
	protected Button     btnNew;                                   // autoWired
	protected Button     btnEdit;                                  // autoWired
	protected Button     btnDelete;                                // autoWired
	protected Button     btnSave;                                  // autoWired
	protected Button     btnCancel;                                // autoWired
	protected Button     btnClose;                                 // autoWired
	protected Button     btnHelp;                                  // autoWired
	protected Button     btnNotes;                                 // autoWired
	protected Button     btnSearchUsrLanguage;                     // autoWired
	protected Button     btnSearchUsrBranchCode;                   // autoWired
	protected Textbox    lovDescUsrBranchCodeName;                 // autoWired
	protected Button     btnSearchUsrDeptCode;                     // autoWired
	protected Textbox    lovDescUsrDeptCodeName;                   // autoWired
	protected Textbox    lovDescUsrLanguage;                       // autoWired
	protected Label      label_PwdStatus;                          // autoWired
	protected Div        div_PwdStatusMeter;                       // autoWired

	/* not auto wired variables*/
	private SecurityUser securityUser;                            // overHanded per parameters
	private transient SecurityUserListCtrl securityUserListCtrl;  // overHanded per parameters

	/* old value variables for edit mode. that we can check
	 *  if something on the values are edited since the last initial.*/
	private transient String  		oldVar_usrLogin;
	private transient String  		oldVar_usrPwd;
	private transient String  		oldVar_userStaffID;
	private transient String  		oldVar_usrFName;
	private transient String  		oldVar_usrMName;
	private transient String  		oldVar_usrLName;
	private transient String 	    oldVar_usrMobile;
	private transient String  		oldVar_usrEmail;
	private transient boolean  		oldVar_usrEnabled;
	private transient Time  		oldVar_usrCanSignonFrom;
	private transient Time  		oldVar_usrCanSignonTo;
	private transient boolean  		oldVar_usrCanOverrideLimits;
	private transient boolean  		oldVar_usrAcExp;
	private transient boolean  		oldVar_usrCredentialsExp;
	private transient boolean  		oldVar_usrAcLocked;
	private transient String  		oldVar_usrLanguage;
	private transient String  		oldVar_usrDftAppId;
	private transient String  		oldVar_usrBranchCode;
	private transient String  		oldVar_usrDeptCode;
	private transient boolean  		oldVar_usrIsMultiBranch;
	private transient String        oldVar_recordStatus;
	private transient String        oldVar_lovDescUsrLanguage;
	private transient String        oldVar_usrConfirmPwd;
	private transient String        oldVar_lovDescUsrDeptCodeName;
	private transient String        oldVar_lovDescUsrBranchCodeName;

	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;

	/* Button controller for the CRUD buttons*/
	private transient final String btnCtroller_ClassPrefix = "button_SecurityUserDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	/* ServiceDAOs / Domain Classes */
	private transient SecurityUserService securityUserService;
	private transient UserService          userService;
	private transient PagedListService     pagedListService;
	private transient ChangePasswordModel  changePasswordModel=new ChangePasswordModel();
	private List<ValueLabel>  listUsrDftAppId = PennantAppUtil.getAppCodes(); 

	/**
	 * default constructor.<br>
	 */
	public SecurityUserDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUser object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityUserDialog(Event event) throws Exception {
		logger.debug("Entering "+event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace()
				, this.btnCtroller_ClassPrefix, true, this.btnNew,this.btnEdit, this.btnDelete
				, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		/* get the parameters map that are over handed by creation.*/
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("securityUser")) {
			this.securityUser = (SecurityUser) args.get("securityUser");
			SecurityUser befImage =new SecurityUser();
			BeanUtils.copyProperties(this.securityUser, befImage);
			this.securityUser.setBefImage(befImage);

			setSecurityUser(this.securityUser);
		} else {
			setSecurityUser(null);
		}

		doLoadWorkFlow(this.securityUser.isWorkflow(),this.securityUser.getWorkflowId(),this.securityUser.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SecurityUserDialog");
		}
		// READ OVERHANDED parameters !
		// we get the securityUserListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete securityUser here.
		if (args.containsKey("securityUserListCtrl")) {
			setSecurityUserListCtrl((SecurityUserListCtrl) args.get("securityUserListCtrl"));
		} else {
			setSecurityUserListCtrl(null);
		}

		setListusrDftAppId();
		// set Field Properties
		doSetFieldProperties();
		this.rowSecurityUserDialogUsrConfirmPwd.setVisible(false);
		this.rowSecurityUserDialogUsrPwd.setVisible(false);
		doShowDialog(getSecurityUser());
		logger.debug("Leaving "+event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		//Empty sent any required attributes
		int pwdMaxLenght=Integer.parseInt(SystemParameterDetails
				.getSystemParameterValue("USR_PWD_MAX_LEN").toString());
		this.usrLogin.setMaxlength(50);
		this.usrPwd.setMaxlength(pwdMaxLenght);
		this.usrPwd.addEventListener("onChanging", new OnChanging());
		this.usrConfirmPwd.setMaxlength(pwdMaxLenght);
		this.userStaffID.setMaxlength(8);
		this.usrFName.setMaxlength(50);
		this.usrMName.setMaxlength(50);
		this.usrLName.setMaxlength(50);
		this.usrMobile.setMaxlength(15);
		this.usrEmail.setMaxlength(50);
		this.usrLanguage.setMaxlength(4);
		this.usrBranchCode.setMaxlength(10);
		this.usrDeptCode.setMaxlength(8);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("SecurityUserDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
	public void onClose$window_SecurityUserDialog(Event event) throws Exception {
		logger.debug("Entering "+event.toString());
		doClose();
		logger.debug("Leaving "+event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering "+event.toString());
		doSave();
		logger.debug("Leaving ");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnEdit(Event event) throws InterruptedException {
		logger.debug("Entering ");
		doEdit();
		this.rowSecurityUserDialogUsrPwd.setVisible(false);
		this.rowSecurityUserDialogUsrConfirmPwd.setVisible(false);
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering ");
		PTMessageUtils.showHelpWindow(event, window_SecurityUserDialog);
		logger.debug("Leaving ");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering ");
		doNew();
		logger.debug("Leaving ");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering ");
		doCancel();
		logger.debug("Leaving ");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering");
		boolean close=true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,	MultiLineMessageBox.YES
					| MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("doClose isDataChanged : False");
		}
		if(close){
			closeDialog(this.window_SecurityUserDialog, "SecurityUser");
		}

		logger.debug("Leaving") ;
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
		doReadOnly();
		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityUser
	 *            SecurityUser
	 */
	public void doWriteBeanToComponents(SecurityUser aSecurityUser) {
		logger.debug("Entering ");

		this.usrLogin.setValue(aSecurityUser.getUsrLogin());
		this.userStaffID.setValue(aSecurityUser.getUserStaffID());
		this.usrFName.setValue(aSecurityUser.getUsrFName());
		this.usrMName.setValue(aSecurityUser.getUsrMName());
		this.usrLName.setValue(aSecurityUser.getUsrLName());
		this.usrMobile.setValue(aSecurityUser.getUsrMobile());
		this.usrEmail.setValue(aSecurityUser.getUsrEmail());
		this.usrEnabled.setChecked(aSecurityUser.isUsrEnabled());
		this.usrCanSignonFrom.setValue(aSecurityUser.getUsrCanSignonFrom());
		this.usrCanSignonTo.setValue(aSecurityUser.getUsrCanSignonTo());
		this.usrCanOverrideLimits.setChecked(aSecurityUser.isUsrCanOverrideLimits());
		this.usrAcExp.setChecked(aSecurityUser.isUsrAcExp());
		this.usrCredentialsExp.setChecked(aSecurityUser.isUsrCredentialsExp());
		this.usrAcLocked.setChecked(aSecurityUser.isUsrAcLocked());	

		if(securityUser.isNew()){
			this.usrDftAppId.setSelectedIndex(0);
		}else{
			this.usrDftAppId.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(securityUser.getUsrDftAppId()),PennantAppUtil.getAppCodes()));
		}

		this.usrBranchCode.setValue(aSecurityUser.getUsrBranchCode());
		this.usrDeptCode.setValue(aSecurityUser.getUsrDeptCode());
		this.usrIsMultiBranch.setChecked(aSecurityUser.isUsrIsMultiBranch());
		this.usrLanguage.setValue(aSecurityUser.getUsrLanguage());
		if (aSecurityUser.isNewRecord()){
			this.usrEnabled.setChecked(true);
			this.lovDescUsrBranchCodeName.setValue("");
			this.lovDescUsrDeptCodeName.setValue("");
			this.lovDescUsrLanguage.setValue("");
		}else{
			this.lovDescUsrBranchCodeName.setValue(aSecurityUser.getUsrBranchCode()
					+"-"+aSecurityUser.getLovDescUsrBranchCodeName());
			this.lovDescUsrDeptCodeName.setValue(aSecurityUser.getUsrDeptCode()
					+"-"+aSecurityUser.getLovDescUsrDeptCodeName());
			this.lovDescUsrLanguage.setValue(aSecurityUser.getUsrLanguage()+"-"+aSecurityUser.getLovDescUsrLanguage());
		}
		this.recordStatus.setValue(aSecurityUser.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityUser
	 */
	public void doWriteComponentsToBean(SecurityUser aSecurityUser) {
		logger.debug("Entering ");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSecurityUser.setUsrLogin(StringUtils.trimToEmpty(this.usrLogin.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		if(aSecurityUser.isNew()){
			try {
				if(StringUtils.trimToEmpty(this.usrPwd.getValue()).equals("")){
					throw new WrongValueException(this.usrPwd,Labels.getLabel("FIELD_NO_EMPTY"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrPwd.value")})); 

				}
				aSecurityUser.setUsrPwd(StringUtils.trimToEmpty(this.usrPwd.getValue()));

			}catch (WrongValueException we ) {
				wve.add(we);
			}}
		if(aSecurityUser.isNew()){
			try {
				if(StringUtils.trimToEmpty(this.usrConfirmPwd.getValue()).equals("")){
					throw new WrongValueException(this.usrConfirmPwd,Labels.getLabel("FIELD_NO_EMPTY"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrconfirmPwd.value")})); 
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
		}
		try {
			aSecurityUser.setUserStaffID(this.userStaffID.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrFName(StringUtils.trimToEmpty(this.usrFName.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrMName(StringUtils.trimToEmpty(this.usrMName.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrLName(StringUtils.trimToEmpty(this.usrLName.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrMobile(StringUtils.trimToEmpty(this.usrMobile.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrEmail(StringUtils.trimToEmpty(this.usrEmail.getValue()));

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrEnabled(this.usrEnabled.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.usrCanSignonFrom != null)
				aSecurityUser.setUsrCanSignonFrom(PennantAppUtil.getTime(this.usrCanSignonFrom.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.usrCanSignonTo!= null)
				aSecurityUser.setUsrCanSignonTo(PennantAppUtil.getTime(this.usrCanSignonTo.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			/*Check whether usrCanSignonTo time is before usrCanSignonFrom or not*/
			if((this.usrCanSignonTo.getValue()!=null) && (this.usrCanSignonFrom.getValue()!=null)){
				int timeDiff= DateUtility.compareTime(aSecurityUser.getUsrCanSignonFrom(), aSecurityUser.getUsrCanSignonTo(), false);
				if(timeDiff==1 || timeDiff==0){
					throw new WrongValueException(this.usrCanSignonTo , Labels.getLabel("FIELD_TIME_MUST_AFTER"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrCanSignonTo.value")
									,Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value")}));
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if((this.usrCanSignonFrom.getValue()==null) && (this.usrCanSignonTo.getValue()!=null)){
				throw new WrongValueException(this.usrCanSignonFrom,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value")}));
			}

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrCanOverrideLimits(this.usrCanOverrideLimits.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrAcExp(this.usrAcExp.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrCredentialsExp(this.usrCredentialsExp.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setUsrAcLocked(this.usrAcLocked.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setLovDescUsrLanguage(this.lovDescUsrLanguage.getValue());
			aSecurityUser.setUsrLanguage(this.usrLanguage.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			String strUsrDftAppId = (String) this.usrDftAppId.getSelectedItem().getValue();
			if (StringUtils.trimToEmpty(strUsrDftAppId).equalsIgnoreCase("")) {
				throw new WrongValueException(this.usrDftAppId,Labels.getLabel("STATIC_INVALID"
						,new String[] { Labels.getLabel("label_SecurityUserDialog_UsrDftAppCode.value") }));
			}
			aSecurityUser.setUsrDftAppId(Integer.parseInt(strUsrDftAppId));
			aSecurityUser.setUsrDftAppCode(PennantConstants.applicationCode);

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSecurityUser.setLovDescUsrBranchCodeName(this.lovDescUsrBranchCodeName.getValue());
			aSecurityUser.setUsrBranchCode(this.usrBranchCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityUser.setLovDescUsrDeptCodeName(this.lovDescUsrDeptCodeName.getValue());
			aSecurityUser.setUsrDeptCode(this.usrDeptCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aSecurityUser.setUsrIsMultiBranch(this.usrIsMultiBranch.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		if(aSecurityUser.isNew()){
			try {
				/*Check Password and confirm password are same or not*/
				if(!StringUtils.trimToEmpty(this.usrPwd.getValue()).equals("")
						&& !StringUtils.trimToEmpty(this.usrConfirmPwd.getValue()).equals("")){
					if (!(StringUtils.trimToEmpty(this.usrPwd.getValue())
							.equals(StringUtils.trimToEmpty(this.usrConfirmPwd.getValue())))){
						throw new WrongValueException(usrConfirmPwd
								,Labels.getLabel("label_SecurityUserDialog_Pwd_not_match.value"));
					}
				}  
			}catch (WrongValueException we ) {

				wve.add(we);
			}
		}
		if(aSecurityUser.isNew()){
			try {
				if (!StringUtils.trimToEmpty(this.usrPwd.getValue()).equals("")){
					this.validate(this.usrPwd, StringUtils.trimToEmpty(this.usrPwd.getValue()));
				}

			}catch (WrongValueException we ) {
				wve.add(we);
			}
		}	

		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			/* if any Exception Occurs make password and new password Fields empty*/
			this.usrPwd.setValue("");
			this.usrConfirmPwd.setValue("");
			this.div_PwdStatusMeter.setStyle("background-color:white");
			this.label_PwdStatus.setValue("");
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSecurityUser.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityUser
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecurityUser aSecurityUser) throws InterruptedException {
		logger.debug("Entering ");

		// if aSecurityUser == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSecurityUser == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aSecurityUser = getSecurityUserService().getNewSecurityUser();

			setSecurityUser(aSecurityUser);
		} else {
			setSecurityUser(aSecurityUser);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityUser.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.usrLogin.focus();
		} else {
			this.usrLogin.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aSecurityUser);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SecurityUserDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method sets all roleApps as ComboItems for ComboBox
	 */
	private void setListusrDftAppId() {
		logger.debug("Entering ");
		for (int i = 0; i < listUsrDftAppId.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listUsrDftAppId.get(i).getLabel());
			comboitem.setValue(listUsrDftAppId.get(i).getValue());
			this.usrDftAppId.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_usrLogin = this.usrLogin.getValue();
		this.oldVar_usrPwd = this.usrPwd.getValue();
		this.oldVar_userStaffID = this.userStaffID.getValue();
		this.oldVar_usrFName = this.usrFName.getValue();
		this.oldVar_usrMName = this.usrMName.getValue();
		this.oldVar_usrLName = this.usrLName.getValue();
		this.oldVar_usrMobile = this.usrMobile.getValue();
		this.oldVar_usrEmail = this.usrEmail.getValue();
		this.oldVar_usrEnabled = this.usrEnabled.isChecked();
		this.oldVar_usrCanSignonFrom = PennantAppUtil.getTime(this.usrCanSignonFrom.getValue());
		this.oldVar_usrCanSignonTo = PennantAppUtil.getTime(this.usrCanSignonTo.getValue());
		this.oldVar_usrCanOverrideLimits = this.usrCanOverrideLimits.isChecked();
		this.oldVar_usrAcExp = this.usrAcExp.isChecked();
		this.oldVar_usrCredentialsExp = this.usrCredentialsExp.isChecked();
		this.oldVar_usrAcLocked = this.usrAcLocked.isChecked();
		this.oldVar_usrLanguage = this.usrLanguage.getValue();
		this.oldVar_usrDftAppId = this.usrDftAppId.getValue();
		this.oldVar_usrBranchCode = this.usrBranchCode.getValue();
		this.oldVar_lovDescUsrBranchCodeName = this.lovDescUsrBranchCodeName.getValue();
		this.oldVar_usrDeptCode = this.usrDeptCode.getValue();
		this.oldVar_lovDescUsrDeptCodeName = this.lovDescUsrDeptCodeName.getValue();
		this.oldVar_usrIsMultiBranch = this.usrIsMultiBranch.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_lovDescUsrLanguage=this.lovDescUsrLanguage.getValue();
		this.oldVar_usrConfirmPwd=this.usrConfirmPwd.getValue();
		logger.debug("Leaving ");

	}

	/**
	 * Resets the initial  values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.usrLogin.setValue(this.oldVar_usrLogin);
		this.usrPwd.setValue(this.oldVar_usrPwd);
		this.userStaffID.setValue(this.oldVar_userStaffID);
		this.usrFName.setValue(this.oldVar_usrFName);
		this.usrMName.setValue(this.oldVar_usrMName);
		this.usrLName.setValue(this.oldVar_usrLName);
		this.usrMobile.setValue(this.oldVar_usrMobile);
		this.usrEmail.setValue(this.oldVar_usrEmail);
		this.usrEnabled.setChecked(this.oldVar_usrEnabled);
		this.usrCanSignonFrom.setValue(this.oldVar_usrCanSignonFrom);
		this.usrCanSignonTo.setValue(this.oldVar_usrCanSignonTo);
		this.usrCanOverrideLimits.setChecked(this.oldVar_usrCanOverrideLimits);
		this.usrAcExp.setChecked(this.oldVar_usrAcExp);
		this.usrCredentialsExp.setChecked(this.oldVar_usrCredentialsExp);
		this.usrAcLocked.setChecked(this.oldVar_usrAcLocked);
		this.usrLanguage.setValue(this.oldVar_usrLanguage);
		this.usrDftAppId.setValue(this.oldVar_usrDftAppId);
		this.usrBranchCode.setValue(this.oldVar_usrBranchCode);
		this.lovDescUsrBranchCodeName.setValue(this.oldVar_lovDescUsrBranchCodeName);
		this.usrDeptCode.setValue(this.oldVar_usrDeptCode);
		this.lovDescUsrDeptCodeName.setValue(this.oldVar_lovDescUsrDeptCodeName);
		this.usrIsMultiBranch.setChecked(this.oldVar_usrIsMultiBranch);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.lovDescUsrLanguage.setValue(this.oldVar_lovDescUsrLanguage);
		this.usrConfirmPwd.setValue(this.oldVar_usrConfirmPwd);
		this.div_PwdStatusMeter.setStyle("background-color:white");
		this.label_PwdStatus.setValue("");

		if(isWorkFlowEnabled()){
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
		logger.debug("Entering ");
		doClearMessage();
		boolean changed = false;

		if (!this.oldVar_usrLogin.equals(this.usrLogin.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrPwd.equals(this.usrPwd.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrConfirmPwd.equals(this.usrConfirmPwd.getValue())) {
			changed = true;
		}
		if (!this.oldVar_userStaffID.equals(this.userStaffID.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrFName.equals(this.usrFName.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrMName.equals(this.usrMName.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrLName.equals(this.usrLName.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrMobile.equals(this.usrMobile.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrEmail.equals(this.usrEmail.getValue())) {
			changed = true;
		}
		if (this.oldVar_usrEnabled != this.usrEnabled.isChecked()) {

			changed = true;
		}
		String old_SignonFrom = "";
		String new_SignonFrom ="";
		if (this.oldVar_usrCanSignonFrom!=null){
			old_SignonFrom=DateUtility.formatDate(this.oldVar_usrCanSignonFrom,PennantConstants.timeFormat);
		}
		if (this.usrCanSignonFrom.getValue()!=null){
			new_SignonFrom=DateUtility.formatDate(this.usrCanSignonFrom.getValue(),PennantConstants.timeFormat);
		}
		if (!StringUtils.trimToEmpty(old_SignonFrom).equals(StringUtils.trimToEmpty(new_SignonFrom))) {
			return true;
		}

		String old_fromDate = "";
		String new_fromDate ="";
		if (this.oldVar_usrCanSignonTo!=null){
			old_fromDate=DateUtility.formatDate(this.oldVar_usrCanSignonTo,PennantConstants.timeFormat);
		}
		if (this.usrCanSignonTo.getValue()!=null){
			new_fromDate=DateUtility.formatDate(this.usrCanSignonTo.getValue(),PennantConstants.timeFormat);
		}
		if (!StringUtils.trimToEmpty(old_fromDate).equals(StringUtils.trimToEmpty(new_fromDate))) {
			return true;
		}
		if (this.oldVar_usrCanOverrideLimits != this.usrCanOverrideLimits.isChecked()) {
			changed = true;
		}
		if (this.oldVar_usrAcExp != this.usrAcExp.isChecked()) {
			changed = true;
		}
		if (this.oldVar_usrCredentialsExp != this.usrCredentialsExp.isChecked()) {
			changed = true;
		}
		if (this.oldVar_usrAcLocked != this.usrAcLocked.isChecked()) {
			changed = true;
		}
		if (!this.oldVar_usrLanguage.equals(this.usrLanguage.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrDftAppId.equals(this.usrDftAppId.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrBranchCode.equals(this.usrBranchCode.getValue())) {
			changed = true;
		}
		if (!this.oldVar_usrDeptCode.equals(this.usrDeptCode.getValue())) {
			changed = true;
		}
		if (this.oldVar_usrIsMultiBranch != this.usrIsMultiBranch.isChecked()) {
			changed = true;
		}



		logger.debug("Leaving ");
		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * @throws InterruptedException 
	 */
	private void doSetValidation() throws InterruptedException {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.usrLogin.isReadonly()){
			this.usrLogin.setConstraint(
					new SimpleConstraint(PennantConstants.USER_LOGIN_REGIX, Labels.getLabel("FIELD_NO_EMPTY_SPECIALCHAR_SPACE_MIN5"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrLogin.value")})));
		}
		if (!this.userStaffID.isReadonly()){
			this.userStaffID.setConstraint(
					new SimpleConstraint(PennantConstants.ALPHANUM_REGEX, Labels.getLabel("MAND_FIELD_CHAR_NUMBER"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UserStaffID.value")})));
		}
		if (!this.usrFName.isReadonly()){
			this.usrFName.setConstraint(
					new SimpleConstraint(PennantConstants.NAME_REGEX, Labels.getLabel("MAND_FIELD_CHARACTER"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrFName.value")})));
		}
		if (!this.usrMName.isReadonly()){
			this.usrMName.setConstraint(
					new SimpleConstraint(PennantConstants.NM_NAME_REGEX, Labels.getLabel("FIELD_CHARACTER"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrMName.value")})));
		}	
		if (!this.usrLName.isReadonly()){
			this.usrLName.setConstraint(
					new SimpleConstraint(PennantConstants.NAME_REGEX, Labels.getLabel("MAND_FIELD_CHARACTER"
							,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrLName.value")})));
		}	
		if (!this.usrMobile.isReadonly()){
			this.usrMobile.setConstraint(
					new SimpleConstraint(PennantConstants.PH_REGEX,Labels.getLabel("MAND_FIELD_PHONENUM"
							,new String[]{Labels.getLabel("label_SecurityUserSearch_UsrMobile.value")})));

		}	
		if (!this.usrEmail.isReadonly()){
			this.usrEmail.setConstraint(
					new SimpleConstraint(PennantConstants.MAIL_REGEX ,Labels.getLabel("MAND_FIELD_MAIL"
							,new String[]{Labels.getLabel("label_SecurityUserSearch_UsrEmail.value")})));

		}	
		if (!this.usrDftAppId.isDisabled()) {
			this.usrDftAppId.setConstraint(new StaticListValidator(listUsrDftAppId
					,Labels.getLabel("label_SecurityUserDialog_UsrDftAppCode.value")));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.usrLogin.setConstraint("");
		this.usrConfirmPwd.setConstraint("");
		this.usrPwd.setConstraint("");
		this.userStaffID.setConstraint("");
		this.usrFName.setConstraint("");
		this.usrMName.setConstraint("");
		this.usrLName.setConstraint("");
		this.usrMobile.setConstraint("");
		this.usrEmail.setConstraint("");
		this.usrCanSignonFrom.setConstraint("");
		this.usrCanSignonTo.setConstraint("");
		this.usrLanguage.setConstraint("");
		this.usrDftAppId.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.lovDescUsrBranchCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrBranchCode.value")}));
		this.lovDescUsrDeptCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrDeptCode.value")}));
		this.lovDescUsrLanguage.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SecurityUserDialog_UsrLanguage.value")}));
		logger.debug("Leaving ");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation()  {
		logger.debug("Entering ");
		this.lovDescUsrBranchCodeName.setConstraint("");
		this.lovDescUsrDeptCodeName.setConstraint("");
		this.lovDescUsrLanguage.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering ");
		this.usrLogin.setErrorMessage("");
		this.usrConfirmPwd.setErrorMessage("");
		this.usrPwd.setErrorMessage("");
		this.userStaffID.setErrorMessage("");
		this.usrFName.setErrorMessage("");
		this.usrMName.setErrorMessage("");
		this.usrLName.setErrorMessage("");
		this.usrMobile.setErrorMessage("");
		this.usrEmail.setErrorMessage("");
		this.usrCanSignonFrom.setErrorMessage("");
		this.usrCanSignonTo.setErrorMessage("");
		this.usrLanguage.setErrorMessage("");
		this.lovDescUsrBranchCodeName.setErrorMessage("");		
		this.lovDescUsrDeptCodeName.setErrorMessage("");
		this.lovDescUsrLanguage.setErrorMessage("");
		this.usrDftAppId.setErrorMessage("");
		logger.debug("Leaving ");

	}

	/**
	 * This validate method is custom validation for password field
	 * Validates the password field  whether password following Defined criteria by calling 
	 *  ChangePasswordModel's validate() method 
	 * if password not following criteria it throws WrongValueException.
	 * 
	 */
	@Override
	public void validate(Component comp, Object value)
	throws WrongValueException {
		logger.debug("Entering ");
		if(!this.usrPwd.getValue().equals("")){

			if(changePasswordModel.checkPasswordCriteria(
					this.usrLogin.getValue(),this.usrPwd.getValue())==true){
				throw new WrongValueException(usrPwd, Labels.getLabel("label_Invalid_Password"));}
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Deletes a SecurityUsers object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {

		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " 
		+ aSecurityUser.getUsrLogin();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO
				, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSecurityUser.getRecordType()).equals("")){
				aSecurityUser.setVersion(aSecurityUser.getVersion()+1);
				aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSecurityUser.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aSecurityUser,tranType)){
					refreshList();
					closeDialog(this.window_SecurityUserDialog, "SecurityUser"); 
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}

	}

	/**
	 * Create a new SecurityUser object. <br>
	 */
	private void doNew() {

		logger.debug("Entering ");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		/* we don't create a new SecurityUser() in the front end.
		   we get it from the back end.*/
		final SecurityUser aSecurityUser = getSecurityUserService().getNewSecurityUser();
		setSecurityUser(aSecurityUser);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.usrLogin.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getSecurityUser().isNewRecord()){
			this.rowSecurityUserDialogUsrConfirmPwd.setVisible(true);
			this.rowSecurityUserDialogUsrPwd.setVisible(true);
			this.usrEnabled.setDisabled(true);
			this.usrAcLocked.setDisabled(true);
			this.usrAcExp.setDisabled(true);
			this.btnCancel.setVisible(false);
			this.usrLogin.setReadonly(false);
			this.panelPasswordInstructions.setVisible(true);
			this.usrDftAppId.setDisabled(false);
		}else{
			this.usrDftAppId.setDisabled(false);
			this.usrEnabled.setDisabled(false);
			this.usrLogin.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.usrAcExp.setDisabled(isReadOnly("SecurityUserDialog_usrAcExp"));
			this.usrAcLocked.setDisabled(isReadOnly("SecurityUserDialog_usrAcLocked"));
		}
		this.usrPwd.setReadonly(isReadOnly("SecurityUserDialog_usrPwd"));
		this.userStaffID.setReadonly(isReadOnly("SecurityUserDialog_userStaffID"));
		this.usrFName.setReadonly(isReadOnly("SecurityUserDialog_usrFName"));
		this.usrMName.setReadonly(isReadOnly("SecurityUserDialog_usrMName"));
		this.usrLName.setReadonly(isReadOnly("SecurityUserDialog_usrLName"));
		this.usrMobile.setReadonly(isReadOnly("SecurityUserDialog_usrMobile"));
		this.usrEmail.setReadonly(isReadOnly("SecurityUserDialog_usrEmail"));
		this.usrCanSignonFrom.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonFrom"));
		this.usrCanSignonTo.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonTo"));
		this.usrCanOverrideLimits.setDisabled(isReadOnly("SecurityUserDialog_usrCanOverrideLimits"));
		this.usrCredentialsExp.setDisabled(isReadOnly("SecurityUserDialog_usrCredentialsExp"));
		this.usrLanguage.setReadonly(isReadOnly("SecurityUserDialog_usrLanguage"));
		this.btnSearchUsrBranchCode.setDisabled(isReadOnly("SecurityUserDialog_usrBranchCode"));
		this.btnSearchUsrDeptCode.setDisabled(isReadOnly("SecurityUserDialog_usrDeptCode"));
		this.usrIsMultiBranch.setDisabled(isReadOnly("SecurityUserDialog_usrIsMultiBranch"));
		this.usrConfirmPwd.setReadonly(isReadOnly("SecurityUserDialog_usrConfirmPwd"));
		this.btnSearchUsrLanguage.setDisabled(false); 

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.securityUser.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
				this.usrDftAppId.setDisabled(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.usrLogin.setReadonly(true);
		this.usrPwd.setReadonly(true);
		this.usrConfirmPwd.setReadonly(true);
		this.userStaffID.setReadonly(true);
		this.usrFName.setReadonly(true);
		this.usrMName.setReadonly(true);
		this.usrLName.setReadonly(true);
		this.usrMobile.setReadonly(true);
		this.usrEmail.setReadonly(true);
		this.usrEnabled.setDisabled(true);
		this.usrCanSignonFrom.setDisabled(true);
		this.usrCanSignonTo.setDisabled(true);
		this.usrCanOverrideLimits.setDisabled(true);
		this.usrAcExp.setDisabled(true);
		this.usrCredentialsExp.setDisabled(true);
		this.usrAcLocked.setDisabled(true);
		this.usrLanguage.setReadonly(true);
		this.usrDftAppId.setDisabled(true);
		this.btnSearchUsrBranchCode.setDisabled(true);
		this.btnSearchUsrDeptCode.setDisabled(true);
		this.usrIsMultiBranch.setDisabled(true);
		this.btnSearchUsrLanguage.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		this.usrLogin.setValue("");
		this.usrPwd.setValue("");
		this.userStaffID.setValue("");
		this.usrFName.setValue("");
		this.usrMName.setValue("");
		this.usrLName.setValue("");
		this.usrMobile.setValue("");
		this.usrEmail.setValue("");
		this.usrEnabled.setChecked(false);
		this.usrCanSignonFrom.setText("");
		this.usrCanSignonTo.setText("");
		this.usrCanOverrideLimits.setChecked(false);
		this.usrAcExp.setChecked(false);
		this.usrCredentialsExp.setChecked(false);
		this.usrAcLocked.setChecked(false);
		this.usrLanguage.setValue("");
		this.usrDftAppId.setValue("");
		this.usrBranchCode.setValue("");
		this.lovDescUsrBranchCodeName.setValue("");
		this.usrDeptCode.setValue("");
		this.lovDescUsrDeptCodeName.setValue("");
		this.usrIsMultiBranch.setChecked(false);
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SecurityUser object with the components data
		doWriteComponentsToBean(aSecurityUser);

		/* Write the additional validations as per below example
		   get the selected branch object from the listBox
		   Do data level validations here*/

		isNew = aSecurityUser.isNew();
		String tranType="";
		if(isNew ||(this.usrEnabled.isChecked() && !isNew)){
			aSecurityUser.setUsrInvldLoginTries(0);
		}
		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSecurityUser.getRecordType()).equals("")){
				aSecurityUser.setVersion(aSecurityUser.getVersion()+1);
				if(isNew){
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_NEW);

				} else{
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityUser.setNewRecord(true);
				}
			}
		}else{
			aSecurityUser.setVersion(aSecurityUser.getVersion()+1);
			if(isNew){
				/*set userActExp Date one day before the system date(i.e already expired date) 
				  for new record for get change password dialog when user first login*/

				aSecurityUser.setUsrAcExpDt(DateUtility.addDays(
						new Date(System.currentTimeMillis()), -1));
				tranType =PennantConstants.TRAN_ADD;
				aSecurityUser.setRecordType("");
			}else{
				tranType =PennantConstants.TRAN_UPD;
				aSecurityUser.setRecordType("");
			}
		}
		// save it to database
		try {

			if(doProcess(aSecurityUser,tranType)){
				refreshList();
				closeDialog(this.window_SecurityUserDialog, "SecurityUser");
			}

		} catch (final DataAccessException e) {
			this.usrPwd.setValue("");
			this.usrConfirmPwd.setValue("");
			this.usrPwd.setFocus(true);
			logger.debug("error in save method");
			logger.error(e.toString());
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 *This Method used for setting all workFlow details from userWorkSpace and 
	 *setting audit details to auditHeader
	 * @param aSecurityUser
	 * @param tranType
	 * @return processCompleted (boolean)
	 */
	private boolean doProcess(SecurityUser aSecurityUser,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSecurityUser.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSecurityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityUser.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSecurityUser.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityUser.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSecurityUser);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId,aSecurityUser))) {
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

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
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
			aSecurityUser.setTaskId(taskId);
			aSecurityUser.setNextTaskId(nextTaskId);
			aSecurityUser.setRoleCode(getRole());
			aSecurityUser.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityUser, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSecurityUser);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSecurityUser, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aSecurityUser, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 *  This Method used for calling the all Database  
	 *  operations from the service by passing the  
	 *  auditHeader and operationRefs(Method) as String
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		SecurityUser aSecurityUser=(SecurityUser)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType()
							.equals(PennantConstants.TRAN_DEL)){

						auditHeader = getSecurityUserService().delete(auditHeader); 
						deleteNotes=true;
					}else{
						auditHeader = getSecurityUserService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getSecurityUserService().doApprove(auditHeader);

						if(aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getSecurityUserService().doReject(auditHeader);
						if(aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityUserDialog,auditHeader);
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_SecurityUserDialog,auditHeader);

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
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * When user clicks on "btnSearchUsrBranchCode" button
	 * This method displays ExtendedSearchListBox with branch details
	 * @param event
	 */
	public void onClick$btnSearchUsrBranchCode(Event event){
		logger.debug("Entering  "+event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SecurityUserDialog,"Branch");
		if (dataObject instanceof String){
			this.usrBranchCode.setValue(dataObject.toString());
			this.lovDescUsrBranchCodeName.setValue("");
		}else{
			Branch details= (Branch) dataObject;
			if (details != null) {
				this.usrBranchCode.setValue(details.getLovValue());
				this.lovDescUsrBranchCodeName.setValue(details.getBranchCode()
						+"-"+details.getBranchDesc());
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When user clicks on "btnSearchUsrLanguage" button
	 * This method displays ExtendedSearchListBox with Language details
	 * @param event
	 */
	public void onClick$btnSearchUsrLanguage(Event event){
		logger.debug("Entering  "+event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SecurityUserDialog,"Language");
		if (dataObject instanceof String){
			this.usrLanguage.setValue(dataObject.toString());
			this.lovDescUsrLanguage.setValue("");
		} 
		else{
			Language details=(Language)dataObject;
			if(details!=null){
				this.usrLanguage.setValue(details.getLngCode().trim());
				this.lovDescUsrLanguage.setValue(details.getLngCode()+"-"+details.getLngDesc());

			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "btnSearchUsrDeptCode" button
	 * This method displays ExtendedSearchListBox with Department details
	 * @param event
	 */
	public void onClick$btnSearchUsrDeptCode(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SecurityUserDialog,"Department");
		if (dataObject instanceof String){
			this.usrDeptCode.setValue(dataObject.toString());
			this.lovDescUsrDeptCodeName.setValue("");
		}else{
			Department details= (Department) dataObject;
			if (details != null) {
				this.usrDeptCode.setValue(details.getLovValue());
				this.lovDescUsrDeptCodeName.setValue(details.getDeptCode()+"-"+details.getDeptDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This is onChanging EventListener class for password field .
	 * This class do the following 
	 * 1)While entering password it checks whether password following 
	 * defined criteria by calling ChangePasswordModel's methods
	 * 2)According to satisfied conditions it assigns  pwdstatusCode
	 *  and calls showPasswordStatusMeter() for view passwordStatusMeter.
	 */
	final class OnChanging implements EventListener<Event>{
		@Override
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering ");
			int pwdstatusCode=0;
			int splCharCount=0;
			String pwd=((org.zkoss.zk.ui.event.InputEvent) event).getValue(); 
			for(int i=0;i<pwd.length();i++){
				/*get all characters and digits count*/
				if(Character.isLetterOrDigit(pwd.charAt(i))){
					splCharCount++;
				}
			}
			splCharCount=pwd.length()-splCharCount;//get special character count
			if(!StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()).equals("")){
				/*if criteria not matched*/
				if(changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue())
						,StringUtils.trimToEmpty(pwd))== true)   {
					pwdstatusCode=1;
				}
				/*if criteria matched and password length less than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH*/
				if((changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue())
						,StringUtils.trimToEmpty(pwd))==false) 
						&& (StringUtils.trimToEmpty(pwd).length()<PennantConstants.PWD_STATUSBAR_CHAR_LENGTH)) {
					pwdstatusCode=2;
				}
				/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and special character 
				 count less than PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT*/
				if((changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue())
						,StringUtils.trimToEmpty(pwd))==false) 
						&& (StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH && 
								splCharCount<PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT)){
					pwdstatusCode=3;
				}
				/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and special character 
				 count  PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT or more*/
				if((changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue())
						,StringUtils.trimToEmpty(pwd))==false )
						&& (StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH && 
								splCharCount>=PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT)){
					pwdstatusCode=4;
				}

				if(StringUtils.trimToEmpty(pwd).equals("")){
					pwdstatusCode=0;
				}
				showPasswordStatusMeter(pwdstatusCode);
			}
		}
	}
	/**
	 * This method displays passwordStatusMeter and label_PwdStatus
	 * @param pwdstatusCode (int)
	 */
	public void showPasswordStatusMeter(int pwdstatusCode){
		switch(pwdstatusCode){	
		case 0:
			this.div_PwdStatusMeter.setStyle("background-color:white");
			this.label_PwdStatus.setValue("");
			break;
		case 1:
			this.div_PwdStatusMeter.setStyle("background-color:red");
			this.div_PwdStatusMeter.setWidth("100px");
			this.label_PwdStatus.setStyle("color:red");
			this.label_PwdStatus.setValue(Labels.getLabel("label_PwdStatus_Bad.value"));
			break;
		case 2: 
			this.div_PwdStatusMeter.setStyle("background-color:tan");
			this.div_PwdStatusMeter.setWidth("150px");
			this.label_PwdStatus.setStyle("color:tan");
			this.label_PwdStatus.setValue(Labels.getLabel("label_PwdStatus_Weak.value"));
			break;
		case 3	:
			this.div_PwdStatusMeter.setStyle("background-color:orange");
			this.div_PwdStatusMeter.setWidth("180px");
			this.label_PwdStatus.setStyle("color:orange");
			this.label_PwdStatus.setValue(Labels.getLabel("label_PwdStatus_Good.value"));
			break;
		case 4:
			this.div_PwdStatusMeter.setStyle("background-color:green");
			this.div_PwdStatusMeter.setWidth("200px");
			this.label_PwdStatus.setStyle("color:green");
			this.label_PwdStatus.setValue(Labels.getLabel("label_PwdStatus_Strong.value"));
			break;
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * This method creates and returns AuditHeader Object
	 * @param aSecurityUser
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);   
		return new AuditHeader(String.valueOf(aSecurityUser.getId()),null,null,null,
				auditDetail,aSecurityUser.getUserDetails(),getOverideMap());

	}

	/**
	 * This method displays Message box with error massage
	 * @param e
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * when clicks on button "btnNotes" 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Sets the notes 
	 * @param notes
	 */
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving ");
	}	

	/**
	 * This method creates Notes Object ,sets data and returns that notes object
	 * @return Notes
	 */
	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("SecurityUsers");
		notes.setReference(String.valueOf(getSecurityUser().getUsrID()));
		notes.setVersion(getSecurityUser().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * Refreshes the list
	 */
	private void refreshList(){
		logger.debug("Entering ");
		final JdbcSearchObject<SecurityUser> soAcademic = 	getSecurityUserListCtrl().getSearchObj();
		getSecurityUserListCtrl().pagingSecurityUserList.setActivePage(0);
		getSecurityUserListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if(getSecurityUserListCtrl().listBoxSecurityUser!=null){
			getSecurityUserListCtrl().listBoxSecurityUser.getListModel();
		}
		logger.debug("Leaving ");
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

	public SecurityUser getSecurityUser() {
		return this.securityUser;
	}
	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
	public SecurityUserService getSecurityUserService() {
		return this.securityUserService;
	}

	public void setSecurityUserListCtrl(SecurityUserListCtrl securityUserListCtrl) {
		this.securityUserListCtrl = securityUserListCtrl;
	}
	public SecurityUserListCtrl getSecurityUserListCtrl() {
		return this.securityUserListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}