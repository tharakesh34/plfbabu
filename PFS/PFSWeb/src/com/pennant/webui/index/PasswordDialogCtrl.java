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
 * FileName    		:  PasswordDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    : 1-07-2011    														*
 *                                                                  						*
 * Modified Date    : 21-10-2011  														*
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

package com.pennant.webui.index;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
//Upgraded to ZK-6.5.1.1 Changed the import from zul.api.textbox to zul.textbox 	
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.UserService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.policy.model.UserImpl;
import com.pennant.sec.util.PasswordEncoderImpl;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuser.changepassword.ChangePasswordModel;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class PasswordDialogCtrl extends GFCBaseCtrl implements Serializable  {
	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * This is the controller class for the
	 * /PFSWeb/WebContent/WEB-INF/pages/PasswordReset/changePwd.zul file. <br>
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 */
	private static final long serialVersionUID = -2314266107249438945L;
	private final static Logger logger = Logger.getLogger(PasswordDialogCtrl.class);


	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends WindowBaseCtrl'.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_ChangePasswordDialog;                  // autoWired
	protected Textbox  userName;                                     // autoWired
	protected Textbox  password;                                     // autoWired
	protected Textbox  newPassword;                                  // autoWired
	protected Textbox  retypeNewPassword;                            // autoWired
	protected Button   btnSave;                                      // autoWired
	protected Button   btnHelp;                                      // autoWired
	protected Label    label_PwdStatus;                              // autoWired
	protected Div      div_PwdStatusMeter;                           // autoWired
	private   ChangePasswordModel changePasswordModel=new ChangePasswordModel(); 
	private   transient SecurityUserService   securityUserService;
	// ServiceDAOs / Domain Classes
	private transient UserService userService;
	private SecurityUser securityUser;
	/**
	 * default constructor. <br>
	 */
	public PasswordDialogCtrl() {
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 *  Creating Dialog Window
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ChangePasswordDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSetFieldProperties();//set field properties
		//getting security user details
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		UserImpl userDetails = (UserImpl) currentUser.getPrincipal();		
		this.securityUser = userDetails.getSecurityUser();
		SecurityUser befImage =new SecurityUser();
		BeanUtils.copyProperties(	this.securityUser, befImage);
		this.securityUser.setBefImage(befImage);
		this.userName.setValue(this.securityUser.getUsrLogin());
		setSecurityUser(this.securityUser);
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when user clicks "save" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception{
		logger.debug("Entering " + event.toString());

		doValidations();
		doSave();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering ");
		PTMessageUtils.showHelpWindow(event, window_ChangePasswordDialog);
		logger.debug("Leaving ");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void doSave(){
		logger.debug("Entering ");
		AuditHeader auditHeader =null;
		try{
			getSecurityUser().setUsrPwd(this.newPassword.getValue());
			getSecurityUser().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			getSecurityUser().setLastMntBy(securityUser.getUsrID());
			getSecurityUser().setVersion(securityUser.getVersion()+1);
			getSecurityUser().setUserDetails(getUserWorkspace().getLoginUserDetails());
			PasswordEncoderImpl pwdEncoder=new PasswordEncoderImpl();
			setSecurityUser( pwdEncoder.encodePassword(getSecurityUser()));
			//update the password by calling securityUserService's changePassword method.
			auditHeader =  getAuditHeader(getSecurityUser(), PennantConstants.TRAN_UPD);
			getsecurityUserService().changePassword(auditHeader);
			Executions.sendRedirect("/j_spring_logout");

		}catch(DataAccessException Error){
			showMessage(Error);
		}

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * This method Validates all fields if any condition goes wrong throws WrongValueException
	 */
	public void doValidations(){

		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try{
			if(StringUtils.trimToEmpty(this.password.getValue()).equals("")){
				throw new WrongValueException(this.password,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_Password.value")})); 
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(StringUtils.trimToEmpty(this.newPassword.getValue()).equals("")){
				throw new WrongValueException(this.newPassword,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_NewPassword.value")})); 
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(StringUtils.trimToEmpty(this.retypeNewPassword.getValue()).equals("")){
				throw new WrongValueException(this.retypeNewPassword,Labels.getLabel("FIELD_NO_EMPTY"
						,new String[]{Labels.getLabel("label_RetypePassword.value")})); 
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			//checking for newPassword following defined password criteria by calling changePasswordModel's validate() method
			if(!StringUtils.trimToEmpty(this.newPassword.getValue()).equals("")){
				if((changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin()
						,StringUtils.trimToEmpty(this.newPassword.getValue()))==true)
						&& !StringUtils.trimToEmpty(this.newPassword.getValue()).equals("")){
					throw new WrongValueException(
							this.newPassword,Labels.getLabel("label_Invalid_Password") );}
			}}
		catch (WrongValueException we) {
			wve.add(we);
		}
		try{
			//checking new password and retype password are same 

			if(!StringUtils.trimToEmpty(this.newPassword.getValue()).equals("")
					&& !StringUtils.trimToEmpty(this.retypeNewPassword.getValue()).equals("")){
				if(!StringUtils.equals(this.newPassword.getValue(), this.retypeNewPassword.getValue())){
					throw new WrongValueException(
							this.retypeNewPassword,Labels.getLabel("FIELD_NOT_MATCHED"
									,new String[]{Labels.getLabel("label_NewPassword.value") 
											,Labels.getLabel("label_RetypePassword.value")}));
				}
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(!StringUtils.trimToEmpty(this.newPassword.getValue()).equals("")){
				//checking for is new password and old passwords are same by calling changePasswordModel's checkWithLastPasswords() method
				if(changePasswordModel.checkWithPreviousPasswords(getSecurityUser(),this.newPassword.getValue())==true){

					throw new WrongValueException( this.newPassword
							,Labels.getLabel("label_Oldpwd_Newpwd_Same"
									,new String[]{SystemParameterDetails.getSystemParameterValue("USR_MAX_PRE_PWDS_CHECK").toString()}));
				}
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try{
			if(!StringUtils.trimToEmpty(this.password.getValue()).equals("")){
				//checking  for old password entered by user is Correct Old password by calling changePasswordModel's IsPaswordsSame() method 
				if(changePasswordModel.isPaswordsSame(getSecurityUser().getUsrPwd()
						, getSecurityUser().getUsrToken(),this.password.getValue())==false){
					throw new WrongValueException(this.password
							,Labels.getLabel("label_Incorrect_Oldpassword"));
				}
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size()>0) {

			doresetFields();
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method resets all the fields
	 */
	public void doresetFields(){
		logger.debug("Entering ");
		this.password.setValue("");
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		this.password.setFocus(true);
		this.div_PwdStatusMeter.setStyle("background-color:white");
		this.label_PwdStatus.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * This method sets the field properties
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		int pwdMaxLenght=Integer.parseInt(SystemParameterDetails
				.getSystemParameterValue("USR_PWD_MAX_LEN").toString());
		this.userName.setReadonly(true);
		this.userName.setReadonly(true);
		this.password.setMaxlength(pwdMaxLenght);
		this.password.setFocus(true);		//set focus
		this.newPassword.addEventListener("onChanging", new OnChanging());
		this.newPassword.setMaxlength(pwdMaxLenght);
		this.retypeNewPassword.setMaxlength(pwdMaxLenght);
		logger.debug("Leaving ");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * This method Shows Message Box with error message
	 * @param error
	 */
	private void showMessage(Exception error){

		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",error.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ChangePasswordDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(error);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This is onChanging EventListener class for newPassword field .
	 * This class do the following 
	 * 1)While entering password it checks whether password following defined criteria 
	 * by calling ChangePasswordModel's methods
	 * 2)According to satisfied conditions it assigns  pwdstatusCode and calls 
	 * showPasswordStatusMeter() for view passwordStatusMeter.
	 */
	final class OnChanging implements EventListener{
		@Override
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering ");
			int pwdstatusCode=0;
			int splCharCount=0;
			String pwd=((org.zkoss.zk.ui.event.InputEvent) event).getValue(); 
			for(int i=0;i<pwd.length();i++){
				//get count of all characters and digits
				if(Character.isLetterOrDigit(pwd.charAt(i))){
					splCharCount++;
				}
			}
			splCharCount=pwd.length()-splCharCount;//get special character count
			/*if criteria not matched*/
			if(changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin()
					,StringUtils.trimToEmpty(pwd))== true){
				pwdstatusCode=1;
			}
			/*if criteria matched and password length less than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH*/
			if((changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin()
					,StringUtils.trimToEmpty(pwd))==false) 
					&& StringUtils.trimToEmpty(pwd).length()<PennantConstants.PWD_STATUSBAR_CHAR_LENGTH){
				pwdstatusCode=2; 
			}
			/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH
			 *  and special character count less than PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT*/
			if((changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin()
					,StringUtils.trimToEmpty(pwd))==false) 
					       && (StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH
							&& splCharCount<PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT)){
				pwdstatusCode=3;
			}
			/*if criteria matched and password length greater than  PennantConstants.PWD_STATUSBAR_CHAR_LENGTH 
			 *  and special character count  PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT or more*/
			if((changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin()
					,StringUtils.trimToEmpty(pwd))==false) 
					 &&(StringUtils.trimToEmpty(pwd).length()>=PennantConstants.PWD_STATUSBAR_CHAR_LENGTH 
							&& splCharCount>=PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT)){

				pwdstatusCode=4;
			}
			if(StringUtils.trimToEmpty(pwd).equals("")){
				pwdstatusCode=0;
			}
			showPasswordStatusMeter(pwdstatusCode);
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
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);   
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID())
				,null,null,null,auditDetail,aSecurityUser.getUserDetails(),getOverideMap());
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	@Override
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SecurityUser getSecurityUser() {
		return securityUser;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public SecurityUserService getsecurityUserService() {
		return securityUserService;
	}

	public void setsecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
}
