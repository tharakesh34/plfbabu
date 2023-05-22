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
 * * FileName : ***DialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 1-07-2011 * * Modified Date :
 * 21-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.index;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
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
import com.pennant.webui.administration.securityuser.changepassword.ChangePasswordModel;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.AESCipherUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /PFSWeb/WebContent/WEB-INF/pages/***Reset/change***.zul file.
 */
public class PasswordDialogCtrl extends GFCBaseCtrl<SecurityUser> {
	private static final long serialVersionUID = -2314266107249438945L;
	private static final Logger logger = LogManager.getLogger(PasswordDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends WindowBaseCtrl'.
	 */
	protected Window window_ChangePasswordDialog;
	protected Textbox userName;
	protected org.zkoss.zhtml.Input password;
	protected Textbox password1;
	protected org.zkoss.zhtml.Input newPassword;
	protected Textbox newPassword1;
	protected org.zkoss.zhtml.Input retypeNewPassword;
	protected Textbox retypeNewPassword1;
	protected Button btnSave;
	protected Button btnHelp;
	protected Label label_PwdStatus;
	protected Div div_PwdStatusMeter;
	protected Textbox txtbox_randomKey;

	private ChangePasswordModel changePasswordModel = new ChangePasswordModel();
	private transient SecurityUserService securityUserService;
	// ServiceDAOs / Domain Classes
	private transient UserService userService;
	private SecurityUser securityUser;

	/**
	 * default constructor. <br>
	 */
	public PasswordDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Creating Dialog Window
	 * 
	 * @param event
	 */
	public void onCreate$window_ChangePasswordDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangePasswordDialog);

		password = (org.zkoss.zhtml.Input) window_ChangePasswordDialog.getFellowIfAny("password");
		newPassword = (org.zkoss.zhtml.Input) window_ChangePasswordDialog.getFellowIfAny("newPassword");
		retypeNewPassword = (org.zkoss.zhtml.Input) window_ChangePasswordDialog.getFellowIfAny("retypeNewPassword");

		doSetFieldProperties();// set field properties
		// getting security user details
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		User userDetails = (User) currentUser.getPrincipal();
		this.securityUser = userDetails.getSecurityUser();
		SecurityUser befImage = new SecurityUser();
		BeanUtils.copyProperties(this.securityUser, befImage);
		this.securityUser.setBefImage(befImage);
		this.userName.setValue(this.securityUser.getUsrLogin());
		setSecurityUser(this.securityUser);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when user clicks "save" button
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
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
		MessageUtil.showHelpWindow(event, window_ChangePasswordDialog);
		logger.debug("Leaving ");
	}

	// CRUD operations

	public void doSave() {
		logger.debug("Entering ");
		AuditHeader auditHeader = null;
		try {
			PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");
			getSecurityUser().setUsrPwd(pwdEncoder
					.encode(AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue())));

			getSecurityUser().setLastMntOn(new Timestamp(System.currentTimeMillis()));
			getSecurityUser().setLastMntBy(securityUser.getUsrID());
			getSecurityUser().setVersion(securityUser.getVersion() + 1);
			getSecurityUser().setUserDetails(getUserWorkspace().getLoggedInUser());
			int expDays = SysParamUtil.getValueAsInt("USR_EXPIRY_DAYS");
			getSecurityUser().setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), expDays));
			getSecurityUser().setCreatedBy(securityUser.getCreatedBy());
			getSecurityUser().setCreatedOn(securityUser.getCreatedOn());

			// Generate the audit header and request for update.
			auditHeader = getAuditHeader(getSecurityUser(), PennantConstants.TRAN_UPD);
			getsecurityUserService().changePassword(auditHeader);
			Executions.sendRedirect("/csrfLogout.zul");

		} catch (DataAccessException error) {
			showMessage(error);
		}

	}

	// GUI operations

	/**
	 * This method Validates all fields if any condition goes wrong throws WrongValueException
	 */
	public void doValidations() {
		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(this.password1.getValue())) {
				throw new WrongValueException(this.password,
						Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_Password.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isBlank(this.newPassword1.getValue())) {
				throw new WrongValueException(this.newPassword,
						Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_NewPassword.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isBlank(this.retypeNewPassword1.getValue())) {
				throw new WrongValueException(this.retypeNewPassword, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_RetypePassword.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Check policy restrictions.
			if (StringUtils.isNotBlank(this.newPassword1.getValue())) {
				if ((changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin(),
						StringUtils.trimToEmpty(
								AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue())))
						&& StringUtils.isNotBlank(
								AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue())))) {
					throw new WrongValueException(this.newPassword, Labels.getLabel("label_Invalid_Password"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Check whether the confirmed input matches with the actual.
			if (StringUtils.isNotBlank(this.newPassword1.getValue())
					&& StringUtils.isNotBlank(this.retypeNewPassword1.getValue())) {
				if (!StringUtils.equals(this.newPassword1.getValue(), this.retypeNewPassword1.getValue())) {
					throw new WrongValueException(this.retypeNewPassword,
							Labels.getLabel("FIELD_NOT_MATCHED",
									new String[] { Labels.getLabel("label_NewPassword.value"),
											Labels.getLabel("label_RetypePassword.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotBlank(this.newPassword1.getValue())) {
				// Check against the history.
				if (changePasswordModel.checkWithPreviousPasswords(getSecurityUser(),
						AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue()))) {
					throw new WrongValueException(this.newPassword, Labels.getLabel("label_Oldpwd_Newpwd_Same",
							new String[] { SysParamUtil.getValueAsString("USR_MAX_PRE_PWDS_CHECK") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotBlank(this.password1.getValue())) {
				// Authenticate the user.
				if (!changePasswordModel.isPaswordsSame(getSecurityUser().getUsrPwd(),
						AESCipherUtil.decrypt(this.password1.getValue(), txtbox_randomKey.getValue()))) {
					throw new WrongValueException(this.password, Labels.getLabel("label_Incorrect_Oldpassword"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size() > 0) {

			doresetFields();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
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
	public void doresetFields() {
		logger.debug("Entering ");
		this.password.setValue("");
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		this.password1.setValue("");
		this.newPassword1.setValue("");
		this.retypeNewPassword1.setValue("");
		this.password.setAutofocus(true);
		// this.div_PwdStatusMeter.setStyle("background-color:white");
		// this.label_PwdStatus.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * This method sets the field properties
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		int pwdMaxLenght = SysParamUtil.getValueAsInt("USR_PWD_MAX_LEN");
		this.userName.setReadonly(true);
		this.userName.setReadonly(true);
		this.password.setMaxlength(pwdMaxLenght);
		this.password.setAutofocus(true); // set focus
		this.newPassword.setMaxlength(pwdMaxLenght);
		this.retypeNewPassword.setMaxlength(pwdMaxLenght);
		logger.debug("Leaving ");
	}

	// Helpers

	/**
	 * This method Shows Message Box with error message
	 * 
	 * @param error
	 */
	private void showMessage(Exception error) {

		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail("", error.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ChangePasswordDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Event listener to set the status code based on strength.
	 */
	final class OnChanging implements EventListener<Event> {
		public OnChanging() {
			//
		}

		@Override
		public void onEvent(Event event) {
			logger.debug("Entering ");

			int pwdMinLenght = SysParamUtil.getValueAsInt("USR_PWD_MIN_LEN");
			int specialCharCount = SysParamUtil.getValueAsInt("USR_PWD_SPECIAL_CHAR_COUNT");

			int pwdstatusCode = 0;
			int splCharCount = 0;
			String pwd = ((org.zkoss.zk.ui.event.InputEvent) event).getValue();
			for (int i = 0; i < pwd.length(); i++) {
				// get count of all characters and digits
				if (Character.isLetterOrDigit(pwd.charAt(i))) {
					splCharCount++;
				}
			}
			splCharCount = pwd.length() - splCharCount;// get special character count
			/* if criteria not matched */
			if (changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin(),
					StringUtils.trimToEmpty(pwd))) {
				pwdstatusCode = 1;
			}
			// Check whether criteria matched.
			if ((!changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin(),
					StringUtils.trimToEmpty(pwd))) && StringUtils.trimToEmpty(pwd).length() < pwdMinLenght) {
				pwdstatusCode = 2;
			}
			// Check whether the minimum required characters available.
			if ((!changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin(),
					StringUtils.trimToEmpty(pwd)))
					&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount < specialCharCount)) {
				pwdstatusCode = 3;
			}
			// Check whether the minimum required special characters available.
			if (!changePasswordModel.checkPasswordCriteria(getSecurityUser().getUsrLogin(),
					StringUtils.trimToEmpty(pwd))
					&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount >= specialCharCount)) {
				pwdstatusCode = 4;
			}
			if (StringUtils.isBlank(pwd)) {
				pwdstatusCode = 0;
			}
			showPasswordStatusMeter(pwdstatusCode);
		}
	}

	/**
	 * Displays the strength using meter for the specified status code.
	 * 
	 * @param statusCode The status code to display using meter.
	 */
	public void showPasswordStatusMeter(int statusCode) {
		switch (statusCode) {
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
		}
	}

	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID()), null, null, null, auditDetail,
				aSecurityUser.getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks "close" method
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		Executions.sendRedirect("loginDialog.zul");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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
