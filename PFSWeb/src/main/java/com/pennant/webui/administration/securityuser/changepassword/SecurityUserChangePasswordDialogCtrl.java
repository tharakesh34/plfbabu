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
 * FileName    		:  SecurityUserChangePasswordCtrl                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  21-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityuser.changepassword;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityUser
 * /SecurityUserChangePasswordDialog.zul file.
 */
public class SecurityUserChangePasswordDialogCtrl extends GFCBaseCtrl<SecurityUser> {
	private static final long serialVersionUID = -2314266107249438945L;
	private static final Logger logger = Logger.getLogger(SecurityUserChangePasswordDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends WindowBaseCtrl'.
	 */
	protected Window  win_SecurityUserChangePasswordDialog;  // autoWired
	protected Textbox userName;                              // autoWired
	protected Textbox newPassword;                           // autoWired
	protected Textbox retypeNewPassword;                     // autoWired
	protected Label   label_PwdStatus;                       // autoWired
	protected Div     div_PwdStatusMeter;                    // autoWired
	                           // autoWired
	private   transient SecurityUserService                securityUserService;
	private   transient ChangePasswordModel                 changePasswordModel=new ChangePasswordModel();
	private   SecurityUser   securityUser;
	/**
	 * default constructor. <br>
	 */

	public SecurityUserChangePasswordDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ChangePasswordDialog";
	}
	
	// Component Events
	
	/** Creating Dialog window
	 * @param event (Event)
	 * @throws Exception
	 */
	public void onCreate$win_SecurityUserChangePasswordDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(win_SecurityUserChangePasswordDialog);

		doSetFieldProperties();
		this.userName.setReadonly(true);
		
		// get the parameters map that are overHanded by creation.
		if (arguments.containsKey("securityUser")) {
			this.securityUser = (SecurityUser) arguments.get("securityUser");
			SecurityUser befImage =new SecurityUser();
			BeanUtils.copyProperties(this.securityUser, befImage);
			this.securityUser.setBefImage(befImage);
			setSecurityUser(this.securityUser);
		} else {
			setSecurityUser(null);
		}	
		
		
		this.userName.setValue(this.securityUser.getUsrLogin());
		setDialog(DialogType.EMBEDDED);
		logger.debug("Leaving ");

	}
	/**
	 * When user clicks on "cancel" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnCancel(Event event) throws Exception {
		logger.debug("Entering " + event.toString());	
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when user clicks "save" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave (Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		doValidations();
		doSave();//update password
		logger.debug("Leaving " + event.toString());

	}
	/**
	 * when user clicks "close" method
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClose(Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		closeDialog();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, win_SecurityUserChangePasswordDialog);
		logger.debug("Leaving" + event.toString());
	}
	
	// GUI operations
	
	/**
	 * This method resets all fields and sets focus on newPassword field
	 */
	public void doResetAllFields(){
		logger.debug("Entering ");
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		this.newPassword.setFocus(true);
		this.div_PwdStatusMeter.setStyle("background-color:white");
		this.label_PwdStatus.setValue("");
		logger.debug("Leaving ");
	}
	/**
	 * This method sets the field properties
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		int pwdMaxLenght = SysParamUtil.getValueAsInt("USR_PWD_MAX_LEN");
		this.userName.setReadonly(true);
		this.newPassword.setMaxlength(pwdMaxLenght);
		this.newPassword.addEventListener("onChanging", new OnChanging());
		this.newPassword.setFocus(true);
		this.retypeNewPassword.setMaxlength(pwdMaxLenght);
		logger.debug("Leaving ");
	}

	/**
	 * This   method performs the validations for fields and if any 
	 * condition goes wrong throws  WrongValueException
	 */
	private void doValidations() {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try{
			if(StringUtils.isBlank(this.newPassword.getValue())){
				throw new WrongValueException(this.newPassword,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_NewPassword.value")})); 
			}

		}catch (WrongValueException we) {
			wve.add(we);

		}
		try{
			if(StringUtils.isBlank(this.retypeNewPassword.getValue())){
				throw new WrongValueException(this.retypeNewPassword,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_RetypePassword.value")})); 
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try{
			//checking for is password following defined criteria by calling changePasswordModel's validate() method
			if((changePasswordModel.checkPasswordCriteria(this.securityUser.getUsrLogin()
					,this.newPassword.getValue()))
					&& StringUtils.isNotBlank(this.newPassword.getValue())){

				throw new WrongValueException( 
						this.newPassword,Labels.getLabel("label_Invalid_Password"));
			}
		}catch (WrongValueException we) {
			wve.add(we);

		}
		try{
			if(StringUtils.isNotBlank(this.newPassword.getValue())
					&& StringUtils.isNotBlank(this.retypeNewPassword.getValue())){
				//checking for is newPassword and retype password are same 
				if(!this.newPassword.getValue().equals(this.retypeNewPassword.getValue())){

					throw new WrongValueException( this.retypeNewPassword
							,Labels.getLabel("FIELD_NOT_MATCHED"
									,new String[]{Labels.getLabel("label_NewPassword.value")
											,Labels.getLabel("label_RetypePassword.value")}));
				} 
			}	 
		}catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size()>0) {
			doResetAllFields();
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
	}
	
	// CRUD operations

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException{
		doValidations();
		AuditHeader auditHeader =null;
		try {
			logger.debug("Entering ");
			/*			setSecurityUser(getSecurityUserService().getSecurityUserById(this.securityUser.getUsrID()));*/
			getSecurityUser().setUsrPwd(this.newPassword.getValue().trim());
			getSecurityUser().setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			getSecurityUser().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			getSecurityUser().setVersion(this.securityUser.getVersion()+1);
			getSecurityUser().setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());		
			getSecurityUser().setUserDetails(getUserWorkspace().getLoggedInUser());
			/* Encrypt the password and get token   */
			PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");
			getSecurityUser().setUsrPwd(pwdEncoder.encode(getSecurityUser().getUsrPwd()));

			auditHeader =  getAuditHeader(getSecurityUser(), PennantConstants.TRAN_UPD);
			if(doSaveProcess(auditHeader)){
				closeDialog();	
			}	

		} catch (DataAccessException e) {
			logger.debug("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving ");
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
	private boolean doSaveProcess(AuditHeader auditHeader) {
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;

		try{
			while(retValue==PennantConstants.porcessOVERIDE){
				auditHeader=getSecurityUserService().changePassword(auditHeader);
				auditHeader = ErrorControl.showErrorDetails(this.win_SecurityUserChangePasswordDialog, auditHeader);
				retValue =auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		}catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}
	
	// Helpers

	/**
	 * This method Shows Message Box with error message
	 * @param error
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.win_SecurityUserChangePasswordDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	private void doCancel() throws InterruptedException{
		this.btnCtrl.setBtnStatus_Save();
	}


	/**
	 * This is onChanging EventListener class for password field .
	 * This class do the following 
	 * 1)While entering password it checks whether password following defined 
	 * criteria by calling ChangePasswordModel's methods
	 * 2)According to satisfied conditions it assigns  pwdstatusCode and calls 
	 * showPasswordStatusMeter() for view passwordStatusmeter.
	 */
	final class OnChanging  implements EventListener<Event> {
		
		public OnChanging() {
			
		}
		
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
			splCharCount=pwd.length()-splCharCount;//get all special characters count

			/*if criteria not matched*/
			if(changePasswordModel.checkPasswordCriteria(
					StringUtils.trimToEmpty(getSecurityUser().getUsrLogin())
					,StringUtils.trimToEmpty(pwd))){
				pwdstatusCode=1;
			}
			/*if criteria matched and password length less than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH*/
			if(!changePasswordModel.checkPasswordCriteria(
					StringUtils.trimToEmpty(getSecurityUser().getUsrLogin())
					,StringUtils.trimToEmpty(pwd)) 
					&& StringUtils.trimToEmpty(pwd).length()<PennantConstants.PWD_STATUSBAR_CHAR_LENGTH){
				pwdstatusCode=2;
			}
			/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and
			 *  special character count less than PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT*/
			if((!changePasswordModel.checkPasswordCriteria(
					StringUtils.trimToEmpty(getSecurityUser().getUsrLogin())
					,StringUtils.trimToEmpty(pwd)))
					&& (StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH 
							&& splCharCount<PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT)){
				pwdstatusCode=3;
			}

			/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and 
			 * special character count PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT or more*/
			if(!changePasswordModel.checkPasswordCriteria(
					StringUtils.trimToEmpty(getSecurityUser().getUsrLogin())
					,StringUtils.trimToEmpty(pwd)) 
					&& (StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH) 
					&& splCharCount>=PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT){
				pwdstatusCode=4;
			}

			if(StringUtils.isBlank(pwd)){
				pwdstatusCode=0;
			}
			showPasswordStatusMeter(pwdstatusCode);
		}
	}

	/**
	 * This method displays passwordStatusMeter and label_PwdStatus
	 * @param pwdstatusCode (int)
	 */
	public void showPasswordStatusMeter(int pwdstatusCode)  {
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
		case 3: 
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
		default:	

		}
	}
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);   
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID()),null,null,null,auditDetail,aSecurityUser.getUserDetails(),getOverideMap());
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public SecurityUser  getSecurityUser() {
		return securityUser;
	}
	public void setSecurityUser(SecurityUser securityUser ){
		this.securityUser = securityUser;
	}
	public SecurityUserService getSecurityUserService() {
		return securityUserService;
	}
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

}
