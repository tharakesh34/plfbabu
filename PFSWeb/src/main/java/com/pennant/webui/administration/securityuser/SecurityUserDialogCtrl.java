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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.administration.securityuser.changepassword.ChangePasswordModel;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.AuthenticationType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.security.LdapContext;
import com.pennanttech.pennapps.core.security.UserAttributes;
import com.pennanttech.pennapps.core.security.UserType;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul file.
 */
public class SecurityUserDialogCtrl extends GFCBaseCtrl<SecurityUser> implements Constraint {
	private static final long serialVersionUID = 952561911227552664L;
	private static final Logger logger = Logger.getLogger(SecurityUserDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SecurityUserDialog;
	protected Textbox usrLogin;
	protected Combobox authType;
	protected Textbox usrPwd;
	protected Textbox usrnewPwd;
	protected Textbox usrConfirmPwd;
	protected Textbox userStaffID;
	protected Textbox usrFName;
	protected Textbox usrMName;
	protected Textbox usrLName;
	protected Textbox usrMobile;
	protected Textbox usrEmail;
	protected Checkbox usrEnabled;
	protected Timebox usrCanSignonFrom;
	protected Timebox usrCanSignonTo;
	protected Checkbox usrCanOverrideLimits;
	protected Checkbox usrAcExp;
	protected Checkbox usrAcLocked;
	protected ExtendedCombobox usrLanguage;
	protected Combobox usrDftAppId;
	protected ExtendedCombobox usrBranchCode;
	protected ExtendedCombobox usrDeptCode;
	protected Checkbox usrIsMultiBranch;
	protected Row rowSecurityUserDialogUsrPwd;
	protected Row rowSecurityUserDialogUsrConfirmPwd;
	// protected Row statusRow;
	protected Panel panelPasswordInstructions;
	protected Label label_PwdStatus;
	protected Div div_PwdStatusMeter;
	protected ExtendedCombobox usrDesg;

	protected Grid usrDivBranchsGrid;
	protected Tab secUserDetailsTab;
	protected Tab secUserDivBranchsTab;
	protected Rows divBranch_Rows;
	protected Row licenceMessageRow;
	protected Label licenceMessage;
	
	/* not auto wired variables */
	private SecurityUser securityUser; // overHanded per parameters
	private transient SecurityUserListCtrl securityUserListCtrl; // overHanded per parameters

	private transient boolean validationOn;

	/* ServiceDAOs / Domain Classes */
	private transient SecurityUserService securityUserService;
	private transient UserService userService;
	private transient PagedListService pagedListService;
	private transient ChangePasswordModel changePasswordModel = new ChangePasswordModel();
	private List<ValueLabel> listUsrDftAppId = PennantStaticListUtil.getAppCodes();
	private List<ValueLabel> authTypesList = PennantStaticListUtil.getAuthnticationTypes();
	protected Map<String, Object> divBranchs = new HashMap<String, Object>();
	protected Map<String, HashMap<String, Object>> dynamicDivBranchs = new HashMap<String, HashMap<String, Object>>();
	private SecurityUserDivBranch securityUserDivBranch = new SecurityUserDivBranch();
	private List<SecurityUserDivBranch> befImgUsrDivBranchsList = new ArrayList<SecurityUserDivBranch>();
	protected boolean newRecord = false;
	protected Datebox UsrAcExpDt;
	private boolean ldapUser = false;
	
	@Autowired
	private LdapContext ldapContext;

	/**
	 * default constructor.<br>
	 */
	public SecurityUserDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityUserDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected SecurityUser object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityUserDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityUserDialog);

		try {

			if (arguments.containsKey("securityUser")) {
				this.securityUser = (SecurityUser) arguments.get("securityUser");
				SecurityUser befImage = new SecurityUser();
				BeanUtils.copyProperties(this.securityUser, befImage);
				this.securityUser.setBefImage(befImage);

				setSecurityUser(this.securityUser);
			} else {
				setSecurityUser(null);
			}

			doLoadWorkFlow(this.securityUser.isWorkflow(), this.securityUser.getWorkflowId(),
					this.securityUser.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "SecurityUserDialog");
			}
			/* set components visible dependent of the users rights */
			
			doCheckRights();
			
			if (arguments.containsKey("securityUserListCtrl")) {
				setSecurityUserListCtrl((SecurityUserListCtrl) arguments.get("securityUserListCtrl"));
			} else {
				setSecurityUserListCtrl(null);
			}
			
			// set Field Properties
			doSetFieldProperties();
			this.rowSecurityUserDialogUsrConfirmPwd.setVisible(false);
			this.rowSecurityUserDialogUsrPwd.setVisible(false);
			doShowDialog(getSecurityUser());
			doSetAuthType(securityUser.getUserType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SecurityUserDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		
		setListusrDftAppId();
		fillComboBox(authType, "", authTypesList, "");
		
		// Empty sent any required attributes
		int pwdMaxLenght = Integer.parseInt(SysParamUtil.getValueAsString("USR_PWD_MAX_LEN"));
		this.usrLogin.setMaxlength(50);
		this.usrPwd.setMaxlength(pwdMaxLenght);
		this.usrPwd.addEventListener("onChanging", new OnChanging());
		this.usrConfirmPwd.setMaxlength(pwdMaxLenght);
		this.userStaffID.setMaxlength(8);
		this.usrFName.setMaxlength(50);
		this.usrMName.setMaxlength(50);
		this.usrLName.setMaxlength(50);
		this.usrMobile.setMaxlength(10);
		this.usrEmail.setMaxlength(50);

		this.usrLanguage.setMaxlength(4);
		this.usrLanguage.setMandatoryStyle(true);
		this.usrLanguage.setModuleName("Language");
		this.usrLanguage.setValueColumn("LngCode");
		this.usrLanguage.setDescColumn("LngDesc");
		this.usrLanguage.setValidateColumns(new String[] { "LngCode" });

		this.usrBranchCode.setMaxlength(LengthConstants.LEN_BRANCH);
		this.usrBranchCode.setMandatoryStyle(true);
		this.usrBranchCode.setModuleName("Branch");
		this.usrBranchCode.setValueColumn("BranchCode");
		this.usrBranchCode.setDescColumn("BranchDesc");
		this.usrBranchCode.setValidateColumns(new String[] { "BranchCode" });

		this.usrDeptCode.setMaxlength(8);
		this.usrDeptCode.setMandatoryStyle(true);
		this.usrDeptCode.setModuleName("Department");
		this.usrDeptCode.setValueColumn("DeptCode");
		this.usrDeptCode.setDescColumn("DeptDesc");
		this.usrDeptCode.setValidateColumns(new String[] { "DeptCode" });

		this.usrDesg.setMaxlength(50);
		this.usrDesg.setTextBoxWidth(180);
		this.usrDesg.setMandatoryStyle(true);
		this.usrDesg.setModuleName("Designation");
		this.usrDesg.setValueColumn("DesgCode");
		this.usrDesg.setDescColumn("DesgDesc");
		this.usrDesg.setValidateColumns(new String[] { "DesgCode" });
		this.UsrAcExpDt.setFormat(DateFormat.SHORT_DATE.getPattern());
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			// this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			// this.statusRow.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving ");
	}

	
	public void onChange$usrLogin(Event event) throws Exception {
		setUserDetails();
	}

	private void setUserDetails() throws Exception {
		try {
			doRemoveValidation();
			doClearMessage();
			ldapUser = false;
			if (authType.getValue().equals("Internal") && StringUtils.isNotBlank(usrLogin.getValue())) {
								
				Map<String, String> details = ldapContext.getUserDetail(usrLogin.getValue());
				if (StringUtils.isBlank(usrEmail.getValue())) {
					usrEmail.setValue(details.get(UserAttributes.EMAIL.getAttribute()));
				}
				if (StringUtils.isBlank(usrMobile.getValue())) {
					usrMobile.setValue(details.get(UserAttributes.MOBILE.getAttribute()));
				}
				if (StringUtils.isBlank(usrFName.getValue())) {
					usrFName.setValue(details.get(UserAttributes.FIRST_NAME.getAttribute()));
				}
				if (StringUtils.isBlank(usrMName.getValue())) {
					usrMName.setValue(details.get(UserAttributes.MIDDLE_NAME.getAttribute()));
				}
				if (StringUtils.isBlank(usrLName.getValue())) {
					usrLName.setValue(details.get(UserAttributes.LAST_NAME.getAttribute()));
				}
			}
			ldapUser = true;
		} catch (InterfaceException e) {
			if (e.getErrorCode().equals(LdapContext.LDAP81)) {
				ldapUser = true;
				logger.warn(e.getErrorMessage());
			} else if (e.getErrorCode().equals(LdapContext.LDAP64)) {
				ldapUser = false;
				throw new WrongValueException(this.usrLogin, e.getErrorMessage());
			} else {
				MessageUtil.showError(e);
				ldapUser = false;
			}

		}
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
		setPasswordRowVisibility(this.authType.getSelectedItem().getValue().toString());
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
		MessageUtil.showHelpWindow(event, window_SecurityUserDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");

		doWriteBeanToComponents(this.securityUser.getBefImage());
		doReadOnly();

		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);

		doDisableDivBranchs(true);

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
		this.usrAcLocked.setChecked(aSecurityUser.isUsrAcLocked());
		this.UsrAcExpDt.setValue(aSecurityUser.getUsrAcExpDt());

		if (securityUser.isNew()) {
			this.usrDftAppId.setSelectedIndex(0);
		} else {
			this.usrDftAppId.setValue(PennantAppUtil.getlabelDesc(String.valueOf(securityUser.getUsrDftAppId()), PennantStaticListUtil.getAppCodes()));
		}
		fillComboBox(authType, securityUser.getAuthType(), authTypesList, "");
		this.usrBranchCode.setValue(aSecurityUser.getUsrBranchCode());
		this.usrDeptCode.setValue(aSecurityUser.getUsrDeptCode());
		this.usrIsMultiBranch.setChecked(aSecurityUser.isUsrIsMultiBranch());
		this.usrLanguage.setValue(aSecurityUser.getUsrLanguage());
		if (aSecurityUser.isNewRecord()) {
			setPasswordRowVisibility(aSecurityUser.getAuthType());
			this.usrEnabled.setChecked(true);
			this.usrBranchCode.setDescription("");
			this.usrDeptCode.setDescription("");
			this.usrLanguage.setDescription("");
		} else {
			this.usrBranchCode.setDescription(aSecurityUser.getLovDescUsrBranchCodeName());
			this.usrDeptCode.setDescription(aSecurityUser.getLovDescUsrDeptCodeName());
			this.usrLanguage.setDescription(aSecurityUser.getLovDescUsrLanguage());
		}
		this.usrDesg.setValue(aSecurityUser.getUsrDesg());
		this.usrDesg.setDescription(aSecurityUser.getLovDescUsrDesg());

		this.recordStatus.setValue(aSecurityUser.getRecordStatus());
		logger.debug("Leaving ");
	}
	
	/**
	 * List of authentication types, based on the selection visible the password
	 * rows. if authentication type is DAO then visible the password rows.
	 */
	public void onChange$authType(Event event) throws Exception {
		setPasswordRowVisibility(this.authType.getSelectedItem().getValue().toString());
		setPasswordInstructionsVisibility(this.authType.getSelectedItem().getValue().toString());
		this.usrLogin.setErrorMessage("");
		setUserDetails();
		
		
	}

	/**
	 * Setting the password row visibility based on the authentication type.
	 * 
	 */
	private void setPasswordRowVisibility(String authType) {
		boolean isDAO = AuthenticationType.DAO.name().equals(authType);
		this.rowSecurityUserDialogUsrPwd.setVisible(isDAO && !isReadOnly("SecurityUserDialog_usrPwd"));
		this.rowSecurityUserDialogUsrConfirmPwd.setVisible(isDAO && !isReadOnly("SecurityUserDialog_usrPwd"));
	}
	
	/**
	 * Setting the password row visibility based on the authentication type.
	 * 
	 */
	private void setPasswordInstructionsVisibility(String authType) {
		boolean isDAO = AuthenticationType.DAO.name().equals(authType);
		this.panelPasswordInstructions.setVisible(isDAO);
	}
	

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityUser
	 * @throws Exception 
	 */
	public void doWriteComponentsToBean(SecurityUser aSecurityUser) throws Exception {
		logger.debug("Entering ");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSecurityUser.setUsrLogin(StringUtils.trimToEmpty(this.usrLogin.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setAuthType(this.authType.getSelectedItem().getValue().toString());
			if (AuthenticationType.DAO.name().equals(aSecurityUser.getAuthType())) {
				if (this.rowSecurityUserDialogUsrPwd.isVisible()) {
					try {
						if (StringUtils.isBlank(this.usrPwd.getValue())) {
							throw new WrongValueException(this.usrPwd, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_SecurityUserDialog_UsrPwd.value") }));
						}
						aSecurityUser.setUsrPwd(StringUtils.trimToEmpty(this.usrPwd.getValue()));
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}

				try {
					if (StringUtils.isBlank(this.usrConfirmPwd.getValue()) && this.rowSecurityUserDialogUsrPwd.isVisible()) {
						throw new WrongValueException(this.usrConfirmPwd, Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_SecurityUserDialog_UsrconfirmPwd.value") }));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					/* Check Password and confirm password are same or not */
					if (StringUtils.isNotBlank(this.usrPwd.getValue()) && StringUtils.isNotBlank(this.usrConfirmPwd.getValue())) {
						if (!(StringUtils.trimToEmpty(this.usrPwd.getValue()).equals(StringUtils.trimToEmpty(this.usrConfirmPwd.getValue())))) {
							throw new WrongValueException(usrConfirmPwd, Labels.getLabel("label_SecurityUserDialog_Pwd_not_match.value"));
						}
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (StringUtils.isNotBlank(this.usrPwd.getValue())) {
						this.validate(this.usrPwd, StringUtils.trimToEmpty(this.usrPwd.getValue()));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				if (StringUtils.isNotEmpty(this.usrPwd.getValue())) {
					PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");
					aSecurityUser.setUsrPwd(pwdEncoder.encode(aSecurityUser.getUsrPwd()));
				}
			} else {
				aSecurityUser.setUsrPwd(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aSecurityUser.setUserStaffID(this.userStaffID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrFName(StringUtils.trimToEmpty(this.usrFName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrMName(StringUtils.trimToEmpty(this.usrMName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrLName(StringUtils.trimToEmpty(this.usrLName.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrMobile(this.usrMobile.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrEmail(StringUtils.trimToEmpty(this.usrEmail.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrEnabled(this.usrEnabled.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.usrCanSignonFrom != null) {
				aSecurityUser.setUsrCanSignonFrom(PennantAppUtil.getTime(this.usrCanSignonFrom.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.usrCanSignonTo != null) {
				aSecurityUser.setUsrCanSignonTo(PennantAppUtil.getTime(this.usrCanSignonTo.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.UsrAcExpDt != null) {
				aSecurityUser.setUsrAcExpDt(this.UsrAcExpDt.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			/* Check whether usrCanSignonTo time is before usrCanSignonFrom or not */
			if ((this.usrCanSignonTo.getValue() != null) && (this.usrCanSignonFrom.getValue() != null)) {
				int timeDiff = DateUtility.compareTime(aSecurityUser.getUsrCanSignonFrom(), aSecurityUser.getUsrCanSignonTo(), false);
				if (timeDiff == 1 || timeDiff == 0) {
					throw new WrongValueException(this.usrCanSignonTo, Labels.getLabel(
							"FIELD_TIME_MUST_AFTER", new String[] { Labels.getLabel("label_SecurityUserDialog_UsrCanSignonTo.value"),
									Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ((this.usrCanSignonFrom.getValue() == null) && (this.usrCanSignonTo.getValue() != null)) {
				throw new WrongValueException(this.usrCanSignonFrom, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrCanOverrideLimits(this.usrCanOverrideLimits.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrAcExp(this.usrAcExp.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setUsrAcLocked(this.usrAcLocked.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setLovDescUsrLanguage(this.usrLanguage.getDescription());
			aSecurityUser.setUsrLanguage(this.usrLanguage.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			String strUsrDftAppId = (String) this.usrDftAppId.getSelectedItem().getValue();
			if (StringUtils.isBlank(strUsrDftAppId)) {
				throw new WrongValueException(this.usrDftAppId, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_SecurityUserDialog_UsrDftAppCode.value") }));
			}
			aSecurityUser.setUsrDftAppId(Integer.parseInt(strUsrDftAppId));
			aSecurityUser.setUsrDftAppCode(App.CODE);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setLovDescUsrBranchCodeName(this.usrBranchCode.getDescription());
			aSecurityUser.setUsrBranchCode(this.usrBranchCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setLovDescUsrDeptCodeName(this.usrDeptCode.getDescription());
			aSecurityUser.setUsrDeptCode(this.usrDeptCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aSecurityUser.setUsrIsMultiBranch(this.usrIsMultiBranch.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aSecurityUser.setLovDescUsrDesg(this.usrDesg.getDescription());
			aSecurityUser.setUsrDesg(this.usrDesg.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		
		if (!ldapUser && getSecurityUser().isNew()&&aSecurityUser.getUsrLogin()!= null) {
			 wve.add(new WrongValueException(this.usrLogin, "User not found in active directory"));
		} else {
			this.usrLogin.setErrorMessage("");
			this.usrLogin.setConstraint("");
		}
	
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			
			/* if any Exception Occurs make password and new password Fields empty */
			this.usrPwd.setValue("");
			this.usrConfirmPwd.setValue("");
			this.div_PwdStatusMeter.setStyle("background-color:white");
			this.label_PwdStatus.setValue("");
			
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			
			secUserDetailsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		}
		aSecurityUser.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * @param usrLogin
	 * authType disable when double click on ADMIN_MAKER or ADMIN_APPROVER 
	 */
	private void doSetAuthType(String userType){
		if(StringUtils.contains(userType, UserType.ADMIN.name())){
			this.authType.setDisabled(true);
			this.btnDelete.setVisible(false);
		}else{
			this.authType.setDisabled(false);
		}
	}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSecurityUser
	 * @throws Exception
	 */
	public void doShowDialog(SecurityUser aSecurityUser) throws Exception {
		logger.debug("Entering ");

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityUser.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.usrLogin.focus();
		} else {
			this.usrLogin.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aSecurityUser);

			// fill the data in divisionBranch tab
			if (this.secUserDivBranchsTab.isVisible()) {
				doFillDivisionBranchTab();
			}
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SecurityUserDialog.onClose();
		} catch (Exception e) {
			throw e;
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

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * 
	 * @throws InterruptedException
	 */
	private void doSetValidation() throws InterruptedException {
		logger.debug("Entering ");
		setValidationOn(true);
		if (!this.usrLogin.isReadonly()) {
			this.usrLogin.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityUserDialog_UsrLogin.value"), null, true));
		}
		
		if (!this.authType.isDisabled()) {
			this.authType.setConstraint(new StaticListValidator(authTypesList,Labels.getLabel("label_SecurityUserDialog_AuthenticationType.value")));
		}
		
		if (!this.userStaffID.isReadonly()) {
			this.userStaffID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityUserDialog_UserStaffID.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		if (!this.usrFName.isReadonly()) {
			this.usrFName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityUserDialog_UsrFName.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE,
					true));
		}
		if (!this.usrMName.isReadonly()) {
			this.usrMName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityUserDialog_UsrMName.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE,
					false));
		}
		if (!this.usrLName.isReadonly()) {
			this.usrLName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SecurityUserDialog_UsrLName.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE,
					true));
		}
		if (!this.usrMobile.isReadonly()) {
			if (StringUtils.isNotEmpty(this.usrMobile.getValue())) {
				this.usrMobile.setConstraint(new PTMobileNumberValidator(Labels
						.getLabel("label_SecurityUserSearch_UsrMobile.value"), true));
			} else {
				this.usrMobile.setConstraint(new PTMobileNumberValidator(Labels
						.getLabel("label_SecurityUserSearch_UsrMobile.value"), false));
			}

		}
		if (!this.usrEmail.isReadonly()) {
			this.usrEmail.setConstraint(new PTEmailValidator(
					Labels.getLabel("label_SecurityUserSearch_UsrEmail.value"), false));

		}
		if (!this.usrDftAppId.isDisabled()) {
			this.usrDftAppId.setConstraint(new StaticListValidator(listUsrDftAppId, Labels
					.getLabel("label_SecurityUserDialog_UsrDftAppCode.value")));
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
		this.authType.setConstraint("");
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
		this.usrBranchCode.setConstraint(new PTStringValidator(Labels
				.getLabel("label_SecurityUserDialog_UsrBranchCode.value"), null, true, true));
		this.usrDeptCode.setConstraint(new PTStringValidator(Labels
				.getLabel("label_SecurityUserDialog_UsrDeptCode.value"), null, true, true));
		this.usrLanguage.setConstraint(new PTStringValidator(Labels
				.getLabel("label_SecurityUserDialog_UsrLanguage.value"), null, true, true));
		this.usrDesg.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrDesg.value"),
				null, true, true));
		logger.debug("Leaving ");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		this.usrBranchCode.setConstraint("");
		this.usrDeptCode.setConstraint("");
		this.usrLanguage.setConstraint("");
		this.usrDesg.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering ");
		this.usrLogin.setErrorMessage("");
		this.authType.setErrorMessage("");
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
		this.usrBranchCode.setErrorMessage("");
		this.usrDeptCode.setErrorMessage("");
		this.usrLanguage.setErrorMessage("");
		this.usrDftAppId.setErrorMessage("");
		this.usrDesg.setErrorMessage("");
		logger.debug("Leaving ");

	}

	/**
	 * This validate method is custom validation for password field Validates the password field whether password
	 * following Defined criteria by calling ChangePasswordModel's validate() method if password not following criteria
	 * it throws WrongValueException.
	 * 
	 */
	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		logger.debug("Entering ");
		if (StringUtils.isNotEmpty(this.usrPwd.getValue())) {
			if (changePasswordModel.checkPasswordCriteria(this.usrLogin.getValue(), this.usrPwd.getValue())) {
				throw new WrongValueException(usrPwd, Labels.getLabel("label_Invalid_Password"));
			}
		}
		logger.debug("Leaving ");
	}

	// CRUD operations
	/**
	 * Deletes a SecurityUsers object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {

		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_SecurityUserDialog_UsrLogin.value") + " : " + aSecurityUser.getUsrLogin();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSecurityUser.getRecordType())) {
				aSecurityUser.setVersion(aSecurityUser.getVersion() + 1);
				aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSecurityUser.setNewRecord(true);
					setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSecurityUser, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		
		if (getSecurityUser().isNewRecord()) {
			this.rowSecurityUserDialogUsrConfirmPwd.setVisible(true);
			this.rowSecurityUserDialogUsrPwd.setVisible(true);
			this.usrEnabled.setDisabled(true);
			this.usrAcLocked.setDisabled(true);
			this.usrAcExp.setDisabled(true);
			this.btnCancel.setVisible(false);
			this.usrLogin.setReadonly(false);
			setPasswordInstructionsVisibility(this.authType.getSelectedItem().getValue().toString());
			this.usrDftAppId.setDisabled(false);
			
			String warningMessage = License.getUserLimitWarning();
			if (warningMessage != null) {
				licenceMessageRow.setVisible(true);
				licenceMessage.setValue(warningMessage);
			}
	
		} else {
			
			this.usrDftAppId.setDisabled(false);
			this.usrEnabled.setDisabled(false);
			this.usrLogin.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.usrAcExp.setDisabled(isReadOnly("SecurityUserDialog_usrAcExp"));
			this.usrAcLocked.setDisabled(isReadOnly("SecurityUserDialog_usrAcLocked"));
			this.usrLogin.setReadonly(true);
			
			licenceMessageRow.setVisible(false);
			licenceMessage.setValue("");
		}
		this.authType.setDisabled(isReadOnly("SecurityUserDialog_usrLogin"));
		this.usrPwd.setReadonly(isReadOnly("SecurityUserDialog_usrPwd"));
		this.userStaffID.setReadonly(isReadOnly("SecurityUserDialog_userStaffID"));
		this.usrFName.setReadonly(isReadOnly("SecurityUserDialog_usrFName"));
		this.usrMName.setReadonly(isReadOnly("SecurityUserDialog_usrMName"));
		this.usrLName.setReadonly(isReadOnly("SecurityUserDialog_usrLName"));
		this.usrMobile.setReadonly(isReadOnly("SecurityUserDialog_usrMobile"));
		this.usrEmail.setReadonly(isReadOnly("SecurityUserDialog_usrEmail"));
		this.usrAcExp.setDisabled(isReadOnly("SecurityUserDialog_usrAcExp"));
		this.usrAcLocked.setDisabled(isReadOnly("SecurityUserDialog_usrAcLocked"));
		this.usrEnabled.setDisabled(isReadOnly("SecurityUserDialog_usrEnabled"));
		this.usrCanSignonFrom.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonFrom"));
		this.usrCanSignonTo.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonTo"));
		this.usrCanOverrideLimits.setDisabled(isReadOnly("SecurityUserDialog_usrCanOverrideLimits"));
		this.usrLanguage.setReadonly(isReadOnly("SecurityUserDialog_usrLanguage"));
		this.usrDftAppId.setDisabled(isReadOnly("SecurityUserDialog_usrDftAppCode"));
		this.usrBranchCode.setReadonly(isReadOnly("SecurityUserDialog_usrBranchCode"));
		this.usrDeptCode.setReadonly(isReadOnly("SecurityUserDialog_usrDeptCode"));
		this.usrIsMultiBranch.setDisabled(isReadOnly("SecurityUserDialog_usrIsMultiBranch"));
		this.usrConfirmPwd.setReadonly(isReadOnly("SecurityUserDialog_usrConfirmPwd"));
		this.usrDesg.setReadonly(isReadOnly("SecurityUserDialog_usrDesg"));
		this.UsrAcExpDt.setDisabled(isReadOnly("SecurityUserDialog_UsrAcExpDt"));
		

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.securityUser.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
				this.usrDftAppId.setDisabled(isReadOnly("SecurityUserDialog_usrDftAppCode"));
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		doDisableDivBranchs(false);
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.usrLogin.setReadonly(true);
		this.authType.setDisabled(true);
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
		this.usrAcLocked.setDisabled(true);
		this.usrLanguage.setReadonly(true);
		this.usrDftAppId.setDisabled(true);
		this.usrBranchCode.setReadonly(true);
		this.usrDeptCode.setReadonly(true);
		this.usrIsMultiBranch.setDisabled(true);
		this.usrDesg.setReadonly(true);
		this.UsrAcExpDt.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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
		this.usrAcLocked.setChecked(false);
		this.usrLanguage.setValue("");
		this.usrDftAppId.setValue("");
		this.usrBranchCode.setValue("");
		this.usrBranchCode.setDescription("");
		this.usrDeptCode.setValue("");
		this.usrDeptCode.setDescription("");
		this.usrIsMultiBranch.setChecked(false);
		this.usrDesg.setValue("");
		this.usrDesg.setDescription("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering ");
		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		boolean isNew = aSecurityUser.isNew();

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SecurityUser object with the components data
		doWriteComponentsToBean(aSecurityUser);
		if (this.secUserDivBranchsTab.isVisible() && (divBranch_Rows != null && !divBranch_Rows.getChildren().isEmpty())) {
			doSaveDivBranchDetails(aSecurityUser);
		}
		/*
		 * Write the additional validations as per below example get the selected branch object from the listBox Do data
		 * level validations here
		 */

		String tranType = "";
		if (this.usrEnabled.isChecked()) {
			aSecurityUser.setUsrInvldLoginTries(0);
		}
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityUser.getRecordType())) {
				aSecurityUser.setVersion(aSecurityUser.getVersion() + 1);
				if (isNew) {
					aSecurityUser.setPwdExpDt(DateUtility.addDays(new Date(System.currentTimeMillis()), -1));
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSecurityUser.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityUser.setNewRecord(true);
					setNewRecord(true);
				}
			}
		} else {
			aSecurityUser.setVersion(aSecurityUser.getVersion() + 1);
			if (isNew) {
				/*
				 * set userActExp Date one day before the system date(i.e already expired date) for new record for get
				 * change password dialog when user first login
				 */

				aSecurityUser.setUsrAcExpDt(DateUtility.addDays(new Date(System.currentTimeMillis()), -1));
				tranType = PennantConstants.TRAN_ADD;
				aSecurityUser.setRecordType("");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				aSecurityUser.setRecordType("");
			}
		}
		// save it to database
		try {
			if (doProcess(aSecurityUser, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			this.usrPwd.setValue("");
			this.usrConfirmPwd.setValue("");
			this.usrPwd.setFocus(true);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving ");
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace and setting audit details to auditHeader
	 * 
	 * @param aSecurityUser
	 * @param tranType
	 * @return processCompleted (boolean)
	 * @throws InterruptedException
	 */
	private boolean doProcess(SecurityUser aSecurityUser, String tranType) throws InterruptedException {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSecurityUser.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSecurityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityUser.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityUser.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityUser.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityUser);
				}

				if (isNotesMandatory(taskId, aSecurityUser)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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
			aSecurityUser.setTaskId(taskId);
			aSecurityUser.setNextTaskId(nextTaskId);
			aSecurityUser.setRoleCode(getRole());
			aSecurityUser.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSecurityUser, tranType);

			String operationRefs = getServiceOperations(taskId, aSecurityUser);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSecurityUser, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aSecurityUser, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * This Method used for calling the all Database operations from the service by passing the auditHeader and
	 * operationRefs(Method) as String
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SecurityUser aSecurityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {

						auditHeader = getSecurityUserService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSecurityUserService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSecurityUserService().doApprove(auditHeader);

						if (aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSecurityUserService().doReject(auditHeader);
						if (aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);

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
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// Search Button Component Events

	/**
	 * 
	 * When user clicks on "btnSearchUsrBranchCode" button This method displays ExtendedSearchListBox with branch
	 * details
	 * 
	 * @param event
	 */
	public void onFulfill$usrBranchCode(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = usrBranchCode.getObject();
		if (dataObject instanceof String) {
			this.usrBranchCode.setValue(dataObject.toString());
			this.usrBranchCode.setDescription("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.usrBranchCode.setValue(details.getBranchCode());
				this.usrBranchCode.setDescription(details.getBranchDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This is onChanging EventListener class for password field . This class do the following 1)While entering password
	 * it checks whether password following defined criteria by calling ChangePasswordModel's methods 2)According to
	 * satisfied conditions it assigns pwdstatusCode and calls showPasswordStatusMeter() for view passwordStatusMeter.
	 */
	final class OnChanging implements EventListener<Event> {
		public OnChanging() {
			//
		}

		@Override
		public void onEvent(Event event) throws Exception {
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
			splCharCount = pwd.length() - splCharCount;// get special character count
			if (StringUtils.isNotBlank(SecurityUserDialogCtrl.this.usrLogin.getValue())) {
				/* if criteria not matched */
				if (changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd))) {
					pwdstatusCode = 1;
				}
				/* if criteria matched and password length less than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH */
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd)))
						&& (StringUtils.trimToEmpty(pwd).length() < pwdMinLenght)) {
					pwdstatusCode = 2;
				}
				/*
				 * if criteria matched and password length greater than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and
				 * special character count less than PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT
				 */
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd)))
						&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount < specialCharCount)) {
					pwdstatusCode = 3;
				}
				/*
				 * if criteria matched and password length greater than PennantConstants.PWD_STATUSBAR_CHAR_LENGTH and
				 * special character count PennantConstants.PWD_STATUSBAR_SPLCHAR_COUNT or more
				 */
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd)))
						&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount >= specialCharCount)) {
					pwdstatusCode = 4;
				}

				if (StringUtils.isBlank(pwd)) {
					pwdstatusCode = 0;
				}
				showPasswordStatusMeter(pwdstatusCode);
			}
		}
	}

	/**
	 * This method displays passwordStatusMeter and label_PwdStatus
	 * 
	 * @param pwdstatusCode
	 *            (int)
	 */
	public void showPasswordStatusMeter(int pwdstatusCode) {
		switch (pwdstatusCode) {
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

	// WorkFlow Components

	/**
	 * This method creates and returns AuditHeader Object
	 * 
	 * @param aSecurityUser
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID()), null, null, null, auditDetail,
				aSecurityUser.getUserDetails(), getOverideMap());
	}

	/**
	 * This method displays Message box with error massage
	 * 
	 * @param e
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 * when clicks on button "btnNotes"
	 * 
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
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method creates Notes Object ,sets data and returns that notes object
	 * 
	 * @return Notes
	 */
	private Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("SecurityUsers");
		notes.setReference(String.valueOf(getSecurityUser().getUsrID()));
		notes.setVersion(getSecurityUser().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSecurityUserListCtrl().search();
	}

	/**
	 * Method for Rendering Division Branch Details
	 */
	public void doFillDivisionBranchTab() {
		logger.debug("Entering");
		List<SecurityUserDivBranch> userBranchsList = getSecurityUserService().getSecUserDivBrList(
				getSecurityUser().getUsrID(), "_View");
		List<DivisionDetail> divisions = getDivisionDetails();
		if (divisions != null && !divisions.isEmpty()) {
			Row row;
			Label divCode;
			Label divDesc;
			Hbox hbox;
			Hbox divBox;
			Space space;
			Textbox textbox;
			Button button;
			for (int i = 0; i < divisions.size(); i++) {
				String userDivision = divisions.get(i).getDivisionCode();
				row = new Row();
				divBox = new Hbox();
				divCode = new Label(userDivision);
				divCode.setVisible(false);
				divDesc = new Label(divisions.get(i).getDivisionCodeDesc());
				divCode.setParent(divBox);
				divDesc.setParent(divBox);
				divBox.setParent(row);

				hbox = new Hbox();
				space = new Space();
				textbox = new Textbox();
				textbox.setReadonly(true);
				textbox.setId(userDivision);
				button = new Button();
				button.setDisabled(isReadOnly("SecurityUserDialog_usrDivBranch"));
				button.setImage("/images/icons/LOVSearch.png");
				button.setId(userDivision);
				space.setParent(hbox);
				textbox.setParent(hbox);
				button.addForward("onClick", window_SecurityUserDialog, "onButtonClick");
				button.setParent(hbox);
				hbox.setParent(row);

				if (getSecurityUser().isNew()) {
					dynamicDivBranchs.put(userDivision, new HashMap<String, Object>());
				} else {
					HashMap<String, Object> tempSecDivBrMap = new HashMap<String, Object>();
					String branchs = "";
					String toolTipDesc = "";
					for (SecurityUserDivBranch securityUserDivBranch : userBranchsList) {
						if (securityUserDivBranch.getUserDivision().equals(userDivision)) {
							tempSecDivBrMap.put(securityUserDivBranch.getUserBranch(),
									securityUserDivBranch.getUserBranchDesc());
							branchs = branchs.concat(securityUserDivBranch.getUserBranch() + ",");
							toolTipDesc = toolTipDesc.concat(securityUserDivBranch.getUserBranchDesc() + " , ");
							securityUserDivBranch.setBefImage(securityUserDivBranch);
							this.befImgUsrDivBranchsList.add(securityUserDivBranch);
						}
					}
					if (StringUtils.isNotBlank(branchs) && branchs.endsWith(",")) {
						branchs = branchs.substring(0, branchs.length() - 1);
						if (toolTipDesc.endsWith(", ")) {
							toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
						}
					}
					textbox.setTooltiptext(toolTipDesc);
					textbox.setValue(branchs);
					dynamicDivBranchs.put(divisions.get(i).getDivisionCode(), tempSecDivBrMap);
				}
				this.divBranch_Rows.appendChild(row);
			}
		} else {
			this.secUserDivBranchsTab.setVisible(false);
		}
		setBefImgUsrDivBranchsList(this.befImgUsrDivBranchsList);
		logger.debug("Leaving");
	}

	/**
	 * This method is to Fetch Division Details
	 * 
	 * @return
	 */
	public List<DivisionDetail> getDivisionDetails() {
		logger.debug("Entering");
		JdbcSearchObject<DivisionDetail> jdbcSearchObject = new JdbcSearchObject<DivisionDetail>(DivisionDetail.class);
		jdbcSearchObject.addTabelName("SMTDivisionDetail_AView");
		List<DivisionDetail> divisions = getPagedListService().getBySearchObject(jdbcSearchObject);
		logger.debug("Leaving");
		return divisions;
	}

	/**
	 * This Method is called when division button is clicked
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onButtonClick(ForwardEvent event) {
		logger.debug("Entering " + event.toString());
		Button btn = (Button) event.getOrigin().getTarget();
		this.divBranchs = dynamicDivBranchs.get(btn.getId());

		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_SecurityUserDialog, "Branch",
				this.divBranchs);

		Textbox txtbx = (Textbox) btn.getPreviousSibling();
		if (dataObject instanceof String) {
			txtbx.setValue(dataObject.toString());
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String multivalues = details.keySet().toString();
				txtbx.setValue(multivalues.replace("[", "").replace("]", "").replace(" ", ""));
				String toolTipDesc = "";
				for (String key : details.keySet()) {
					Object obj = (Object) details.get(key);
					if (obj instanceof String) {
						//
					} else {
						Branch branch = (Branch) obj;
						if (branch != null) {
							toolTipDesc = toolTipDesc.concat(branch.getBranchDesc() + " , ");
						}
					}
				}
				if (StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")) {
					toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
				}
				txtbx.setTooltiptext(toolTipDesc);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method is to save the division branch details
	 * 
	 * @param securityUser
	 * @return
	 */
	public void doSaveDivBranchDetails(SecurityUser securityUser) {
		logger.debug("Entering");
		SecurityUserDivBranch aSecurityUserDivBranch;
		List<SecurityUserDivBranch> secUsrDivBranchsList = new ArrayList<SecurityUserDivBranch>();
		try {
			for (Component row : divBranch_Rows.getChildren()) {
				Label division = (Label) row.getFirstChild().getFirstChild();
				String div = division == null ? "" : division.getValue();
				Textbox branch = (Textbox) row.getFirstChild().getNextSibling().getFirstChild().getNextSibling();
				String divBranches = branch == null ? "" : branch.getValue();
				if (StringUtils.isNotBlank(divBranches)) {
					String[] divBranchs = divBranches.split(",");
					for (String divBranch : divBranchs) {
						aSecurityUserDivBranch = new SecurityUserDivBranch();
						aSecurityUserDivBranch.setUsrID(getSecurityUser().getUsrID());
						aSecurityUserDivBranch.setUserDivision(div);
						aSecurityUserDivBranch.setUserBranch(divBranch);
						aSecurityUserDivBranch.setRecordStatus("");
						aSecurityUserDivBranch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						aSecurityUserDivBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						aSecurityUserDivBranch.setUserDetails(getUserWorkspace().getLoggedInUser());

						secUsrDivBranchsList.add(aSecurityUserDivBranch);
					}
				}
			}
			if (!secUsrDivBranchsList.isEmpty()) {
				securityUser.setSecurityUserDivBranchList(newDivBranchsProcess(secUsrDivBranchsList));
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			this.befImgUsrDivBranchsList.clear();
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	public boolean isBranchNewRecord(SecurityUserDivBranch aSecurityUserDivBranch) {
		boolean isNew = false;
		for (SecurityUserDivBranch securityUserDivBranch : getBefImgUsrDivBranchsList()) {
			if (!aSecurityUserDivBranch.getUserDivision().equals(securityUserDivBranch.getUserDivision())
					&& !aSecurityUserDivBranch.getUserBranch().equals(securityUserDivBranch.getUserBranch())) {
				isNew = true;
			}
		}
		return isNew;
	}

	/**
	 * This method is to process division branch details
	 * 
	 * @param selectedUsrDivBranchsList
	 * @return
	 */
	public List<SecurityUserDivBranch> newDivBranchsProcess(List<SecurityUserDivBranch> selectedUsrDivBranchsList) {
		logger.debug("Entering");
		List<SecurityUserDivBranch> newSecUsrDivBranchsList = new ArrayList<SecurityUserDivBranch>();
		// if(!isNewRecord()){
		// Below loop is to check deleted branchs from existing branchs
		for (SecurityUserDivBranch asecurityUserDivBranch : getBefImgUsrDivBranchsList()) {
			boolean recordExists = false;
			for (SecurityUserDivBranch securityUserDivBranch2 : selectedUsrDivBranchsList) {
				if (securityUserDivBranch2.getUserDivision().equals(asecurityUserDivBranch.getUserDivision())
						&& securityUserDivBranch2.getUserBranch().equals(asecurityUserDivBranch.getUserBranch())) {
					recordExists = true;
					securityUserDivBranch2.setNewRecord(true);
					securityUserDivBranch2.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					newSecUsrDivBranchsList.add(securityUserDivBranch2);
				}
			}
			if (!recordExists && !isNewRecord()) {
				asecurityUserDivBranch.setNewRecord(false);
				asecurityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				newSecUsrDivBranchsList.add(asecurityUserDivBranch);
			}
		}
		// Below loop is to check newly added branchs to existing branchs
		for (SecurityUserDivBranch asecurityUserDivBranch : selectedUsrDivBranchsList) {
			boolean recordExists = false;
			for (SecurityUserDivBranch securityUserDivBranch2 : newSecUsrDivBranchsList) {
				if (securityUserDivBranch2.getUserDivision().equals(asecurityUserDivBranch.getUserDivision())
						&& securityUserDivBranch2.getUserBranch().equals(asecurityUserDivBranch.getUserBranch())) {
					recordExists = true;
				}
			}
			if (!recordExists && !isNewRecord()) {
				asecurityUserDivBranch.setNewRecord(true);
				asecurityUserDivBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				newSecUsrDivBranchsList.add(asecurityUserDivBranch);
			}
		}
		logger.debug("Leaving");
		return newSecUsrDivBranchsList;
	}

	/**
	 * This method is to set disable for dynamically created division buttons
	 * 
	 * @param disable
	 */
	public void doDisableDivBranchs(boolean disable) {
		logger.debug("Entering");
		this.divBranch_Rows.getChildren();
		for (Component component : this.divBranch_Rows.getChildren()) {
			Row row = (Row) component;
			if (row != null) {
				Button button = (Button) row.getFirstChild().getNextSibling().getLastChild();
				if (button != null) {
					button.setDisabled(disable);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void validateBranchs() throws InterruptedException {
		if (!isReadOnly("SecurityUserDialog_usrDivBranch")) {
			if (StringUtils.trimToEmpty((String) userAction.getSelectedItem().getValue()).equals(
					PennantConstants.RCD_STATUS_SAVED)
					|| StringUtils.trimToEmpty((String) userAction.getSelectedItem().getValue()).equals(
							PennantConstants.RCD_STATUS_SUBMITTED)) {
				this.secUserDivBranchsTab.setSelected(true);
				MessageUtil.showError(Labels.getLabel("SecUserDivBranchs_NotEmpty"));
			}
		}
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

	public SecurityUserDivBranch getSecurityUserDivBranch() {
		return securityUserDivBranch;
	}

	public void setSecurityUserDivBranch(SecurityUserDivBranch securityUserDivBranch) {
		this.securityUserDivBranch = securityUserDivBranch;
	}

	public List<SecurityUserDivBranch> getBefImgUsrDivBranchsList() {
		return this.befImgUsrDivBranchsList;
	}

	public void setBefImgUsrDivBranchsList(List<SecurityUserDivBranch> usrDivBranchsList) {
		this.befImgUsrDivBranchsList = usrDivBranchsList;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

}