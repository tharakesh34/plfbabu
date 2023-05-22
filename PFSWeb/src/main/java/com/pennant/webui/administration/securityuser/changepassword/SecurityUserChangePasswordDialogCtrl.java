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
 * * FileName : SecurityUserChange***Ctrl * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * * Modified
 * Date : 21-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.administration.securityuser.changepassword;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.util.AESCipherUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserChange***Dialog.zul file.
 */
public class SecurityUserChangePasswordDialogCtrl extends GFCBaseCtrl<SecurityUser> {
	private static final long serialVersionUID = -2314266107249438945L;
	private static final Logger logger = LogManager.getLogger(SecurityUserChangePasswordDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends WindowBaseCtrl'.
	 */
	protected Window win_SecurityUserChangePasswordDialog;
	protected Textbox userName;
	protected org.zkoss.zhtml.Input newPassword;
	protected Textbox newPassword1;
	protected org.zkoss.zhtml.Input retypeNewPassword;
	protected Textbox retypeNewPassword1;
	protected Label label_PwdStatus;
	protected Div div_PwdStatusMeter;
	protected Textbox txtbox_randomKey;

	private transient SecurityUserService securityUserService;
	private transient ChangePasswordModel changePasswordModel = new ChangePasswordModel();
	private SecurityUser securityUser;

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

	/**
	 * Creating Dialog window
	 * 
	 * @param event (Event)
	 */
	public void onCreate$win_SecurityUserChangePasswordDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(win_SecurityUserChangePasswordDialog);
		newPassword = (org.zkoss.zhtml.Input) win_SecurityUserChangePasswordDialog.getFellowIfAny("newPassword");
		retypeNewPassword = (org.zkoss.zhtml.Input) win_SecurityUserChangePasswordDialog
				.getFellowIfAny("retypeNewPassword");

		doSetFieldProperties();
		this.userName.setReadonly(true);

		// get the parameters map that are overHanded by creation.
		if (arguments.containsKey("securityUser")) {
			this.securityUser = (SecurityUser) arguments.get("securityUser");
			SecurityUser befImage = new SecurityUser();
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
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * when user clicks "save" button
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		doValidations();
		doSave();

	}

	/**
	 * when user clicks "close" method
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		closeDialog();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, win_SecurityUserChangePasswordDialog);
	}

	// GUI operations

	/**
	 * This method resets all fields and sets focus on the first field.
	 */
	public void doResetAllFields() {
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		this.newPassword1.setValue("");
		this.retypeNewPassword1.setValue("");
		/*
		 * this.div_PwdStatusMeter.setStyle("background-color:white"); this.label_PwdStatus.setValue("")
		 */
	}

	/**
	 * This method sets the field properties
	 */
	private void doSetFieldProperties() {
		int pwdMaxLenght = SysParamUtil.getValueAsInt("USR_PWD_MAX_LEN");
		this.userName.setReadonly(true);
		this.newPassword.setMaxlength(pwdMaxLenght);
		this.newPassword.setAutofocus(true);
		this.retypeNewPassword.setMaxlength(pwdMaxLenght);
	}

	/**
	 * This method performs the validations for fields and if any condition goes wrong throws WrongValueException
	 */
	private void doValidations() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

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
			if ((changePasswordModel.checkPasswordCriteria(this.securityUser.getUsrLogin(),
					AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue()))
					&& StringUtils.isNotBlank(
							AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue())))) {

				throw new WrongValueException(this.newPassword, Labels.getLabel("label_Invalid_Password"));
			}
		} catch (WrongValueException we) {
			wve.add(we);

		}
		try {
			if (StringUtils.isNotBlank(this.newPassword1.getValue())
					&& StringUtils.isNotBlank(this.retypeNewPassword1.getValue())) {
				// Check whether the confirmed input matches with the actual.
				if (!this.newPassword1.getValue().equals(this.retypeNewPassword1.getValue())) {

					throw new WrongValueException(this.retypeNewPassword,
							Labels.getLabel("FIELD_NOT_MATCHED",
									new String[] { Labels.getLabel("label_NewPassword.value"),
											Labels.getLabel("label_RetypePassword.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doResetAllFields();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	// CRUD operations

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		doValidations();
		AuditHeader auditHeader = null;
		try {
			// Store as encrypted.
			PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");

			securityUser.setUsrPwd(pwdEncoder
					.encode(AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue())));
			securityUser.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			securityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			securityUser.setVersion(this.securityUser.getVersion() + 1);
			securityUser.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			securityUser.setUserDetails(getUserWorkspace().getLoggedInUser());

			auditHeader = getAuditHeader(securityUser, PennantConstants.TRAN_UPD);
			if (doSaveProcess(auditHeader)) {
				closeDialog();
			}

		} catch (DataAccessException | AppException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader) {
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		while (retValue == PennantConstants.porcessOVERIDE) {
			auditHeader = getSecurityUserService().changePassword(auditHeader);
			auditHeader = ErrorControl.showErrorDetails(this.win_SecurityUserChangePasswordDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		return processCompleted;
	}

	// Helpers

	private void doCancel() {
		this.btnCtrl.setBtnStatus_Save();
	}

	/**
	 * Event listener to set the status code based on strength.
	 */
	final class OnChanging implements EventListener<Event> {
		public OnChanging() {
		    super();
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
				/* get all characters and digits count */
				if (Character.isLetterOrDigit(pwd.charAt(i))) {
					splCharCount++;
				}
			}
			splCharCount = pwd.length() - splCharCount;// get all special characters count

			/* if criteria not matched */
			if (changePasswordModel.checkPasswordCriteria(StringUtils.trimToEmpty(getSecurityUser().getUsrLogin()),
					StringUtils.trimToEmpty(pwd))) {
				pwdstatusCode = 1;
			}

			// Check whether criteria matched.
			if (!changePasswordModel.checkPasswordCriteria(StringUtils.trimToEmpty(getSecurityUser().getUsrLogin()),
					StringUtils.trimToEmpty(pwd)) && StringUtils.trimToEmpty(pwd).length() < pwdMinLenght) {
				pwdstatusCode = 2;
			}

			// Check whether the minimum required characters available.
			if ((!changePasswordModel.checkPasswordCriteria(StringUtils.trimToEmpty(getSecurityUser().getUsrLogin()),
					StringUtils.trimToEmpty(pwd)))
					&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount < specialCharCount)) {
				pwdstatusCode = 3;
			}

			// Check whether the minimum required special characters available.
			if (!changePasswordModel.checkPasswordCriteria(StringUtils.trimToEmpty(getSecurityUser().getUsrLogin()),
					StringUtils.trimToEmpty(pwd)) && (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght)
					&& splCharCount >= specialCharCount) {
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
		default:

		}
	}

	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID()), null, null, null, auditDetail,
				aSecurityUser.getUserDetails(), getOverideMap());
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SecurityUser getSecurityUser() {
		return securityUser;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public SecurityUserService getSecurityUserService() {
		return securityUserService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

}
