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
 * * FileName : SecurityUserDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 10-8-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-8-2011 Pennant 0.1 * * * * * * * * *
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
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.applicationmaster.ReportingManagerService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
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
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.security.UserType;
import com.pennanttech.pennapps.core.security.user.UserSearch;
import com.pennanttech.pennapps.core.util.AESCipherUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUser/SecurityUserDialog.zul file.
 */
public class SecurityUserDialogCtrl extends GFCBaseCtrl<SecurityUser> implements Constraint {
	private static final long serialVersionUID = 952561911227552664L;
	private static final Logger logger = LogManager.getLogger(SecurityUserDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SecurityUserDialog;
	protected Textbox usrLogin;
	protected Combobox authType;
	protected org.zkoss.zhtml.Input txtbox_Password;
	protected Textbox txtbox_Password1;
	protected Textbox usrnewPwd;
	protected org.zkoss.zhtml.Input txtbox_confirm_Password;
	protected Textbox txtbox_confirm_Password1;
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
	protected Datebox usrAcExpDt;
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
	protected ExtendedCombobox businessvertical;
	protected Grid usrDivBranchsGrid;
	protected Tab secUserDetailsTab;
	protected Tab secUserDivBranchsTab;
	protected Rows divBranch_Rows;
	protected Row licenceMessageRow;
	protected Label licenceMessage;
	protected Columns divisionColumns;
	protected Button btnNewReportingManagerList;
	protected Textbox txtbox_randomKey;
	protected Label ldapDomain;
	protected Combobox ldapDomainName;
	/* not auto wired variables */
	private SecurityUser securityUser;
	private transient SecurityUserListCtrl securityUserListCtrl;
	protected Combobox disableReason;
	protected Label label_SecurityUserDialog_DisableReason;
	protected Combobox employeeType;

	private transient boolean validationOn;

	/* ServiceDAOs / Domain Classes */
	private transient SecurityUserService securityUserService;
	private transient UserService userService;
	private transient PagedListService pagedListService;
	private transient ReportingManagerService reportingManagerService;
	private transient ChangePasswordModel changePasswordModel = new ChangePasswordModel();
	private List<ValueLabel> listUsrDftAppId = PennantStaticListUtil.getAppCodes();
	private List<ValueLabel> authTypesList = PennantStaticListUtil.getAuthnticationTypes();
	protected transient Map<String, Object> divBranchs = new HashMap<>();
	protected transient Map<String, Map<String, Object>> dynamicDivBranchs = new HashMap<>();
	private SecurityUserDivBranch securityUserDivBranch = new SecurityUserDivBranch();
	private List<SecurityUserDivBranch> befImgUsrDivBranchsList = new ArrayList<>();
	protected Listbox listBoxReportingManager;
	private List<ReportingManager> reportingManagerDetailList = new ArrayList<>();
	private ReportingManager reportingManager = new ReportingManager();
	protected boolean newRecord = false;
	private boolean findUser = false;
	private List<ValueLabel> ldapDomainList = PennantStaticListUtil.getLDAPDomains();
	private List<ValueLabel> disableReasonList = PennantAppUtil.getDisableReason();
	private List<ValueLabel> employeeTypeList = PennantAppUtil.getEmployeeTypes();

	@Autowired
	private transient UserSearch ldapUserSearch;

	@Autowired
	private transient UserSearch externalUserSearch;

	@Autowired
	private transient ClusterService clusterService;

	@Autowired
	private transient SearchProcessor searchProcessor;

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

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected SecurityUser object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SecurityUserDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SecurityUserDialog);
		txtbox_Password = (org.zkoss.zhtml.Input) window_SecurityUserDialog.getFellowIfAny("txtbox_Password");
		txtbox_confirm_Password = (org.zkoss.zhtml.Input) window_SecurityUserDialog
				.getFellowIfAny("txtbox_confirm_Password");
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

			String randomKey = "";
			try {
				randomKey = (String) Sessions.getCurrent().getAttribute("SATTR_RANDOM_KEY");
			} catch (Exception ex) {
				logger.warn("Unable to get session attribute 'SATTR_RANDOM_KEY':", ex);
			}
			if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
				txtbox_randomKey.setValue(randomKey);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SecurityUserDialog.onClose();
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		setListusrDftAppId();
		fillComboBox(authType, "", authTypesList, "");
		fillComboBox(ldapDomainName, "", ldapDomainList, "");

		// Empty sent any required attributes
		int pwdMaxLenght = Integer.parseInt(SysParamUtil.getValueAsString("USR_PWD_MAX_LEN"));
		this.usrLogin.setMaxlength(50);
		this.txtbox_Password.setMaxlength(pwdMaxLenght);
		this.txtbox_Password.addEventListener("onChanging", new OnChanging());
		this.txtbox_confirm_Password.setMaxlength(pwdMaxLenght);
		this.userStaffID.setMaxlength(10);
		this.usrFName.setMaxlength(50);
		this.usrMName.setMaxlength(50);
		this.usrLName.setMaxlength(50);
		this.usrMobile.setMaxlength(13);
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
		this.usrAcExpDt.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.businessvertical.setModuleName("BusinessVertical");
		this.businessvertical.setValueColumn("Id");
		this.businessvertical.setValueType(DataType.LONG);
		this.businessvertical.setDescColumn("Description");
		this.businessvertical.setValidateColumns(new String[] { "Id", "Code" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			// this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			// this.statusRow.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_btnSave"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_RM_btnNew"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$usrLogin(Event event) {
		setUserDetails();
	}

	public void onChange$ldapDomainName(Event event) {
		this.ldapDomainName.getSelectedItem().getValue().toString();
		setUserDetails();
	}

	private void setUserDetails() {
		try {
			doRemoveValidation();
			doClearMessage();
			findUser = false;
			com.pennanttech.pennapps.core.security.model.SecurityUser user = null;
			if (authType.getValue().equals(Labels.getLabel("label_Auth_Type_External"))
					&& StringUtils.isNotBlank(usrLogin.getValue()) && ldapDomainName.getSelectedIndex() > 0) {

				user = getUserSearch().searchForUser(this.ldapDomainName.getSelectedItem().getValue(),
						usrLogin.getValue());
				usrEmail.setValue(user.getEmail());
				usrMobile.setValue(user.getMobileNumber());
				usrFName.setValue(user.getFirstName());
				usrMName.setValue(user.getMiddleName());
				usrLName.setValue(user.getLastName());
				try {
					userStaffID.setValue(user.getStaffId());
				} catch (Exception e) {
					logger.debug(Literal.EXCEPTION, e.getMessage());
				}
				usrBranchCode.setValue(user.getBranchCode());
				usrLanguage.setValue(user.getLanguage());
				usrDeptCode.setValue(user.getDepartmentCode());
				usrDesg.setValue(user.getDesignation());
			}
			findUser = true;
		} catch (InterfaceException e) {
			if (externalUserSearch != null) {
				usrEmail.setValue("");
				usrMobile.setValue("");
				usrFName.setValue("");
				usrMName.setValue("");
				usrLName.setValue("");
				userStaffID.setValue("");
				usrBranchCode.setValue("");
				usrLanguage.setValue("");
				usrDeptCode.setValue("");
				usrDesg.setValue("");
			}
			// Giving user confirmation If any exception coming, while creating the external user.
			if (MessageUtil.OVERIDE == MessageUtil.confirm(
					"Unable to fetch the user details, do you want proceed with the user creation? ",
					MessageUtil.CANCEL | MessageUtil.OVERIDE)) {
				findUser = true;
			}
		}
	}

	private void showLDAPDomainName() {
		if (this.authType.getSelectedItem().getValue().toString().equals(AuthenticationType.DAO.name())) {
			this.ldapDomain.setVisible(false);
			this.ldapDomainName.setVisible(false);
		}

		if (this.authType.getSelectedItem().getValue().toString().equals(AuthenticationType.LDAP.name())) {
			this.ldapDomain.setVisible(true);
			this.ldapDomainName.setVisible(true);
			if (ldapDomainList.size() == 1) {
				ldapDomainName.setValue(ldapDomainList.get(0).getLabel());
				ldapDomainName.setDisabled(true);
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
		logger.debug(Literal.ENTERING);
		doEdit();
		setPasswordRowVisibility(this.authType.getSelectedItem().getValue().toString());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_SecurityUserDialog);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.securityUser.getBefImage());
		doReadOnly();

		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);

		doDisableDivBranchs(true);

		doEditClusterDivisions(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityUser SecurityUser
	 */
	public void doWriteBeanToComponents(SecurityUser aSecurityUser) {
		logger.debug(Literal.ENTERING);

		this.usrLogin.setValue(aSecurityUser.getUsrLogin());
		this.userStaffID.setValue(aSecurityUser.getUserStaffID());
		this.usrFName.setValue(aSecurityUser.getUsrFName());
		this.usrMName.setValue(aSecurityUser.getUsrMName());
		this.usrLName.setValue(aSecurityUser.getUsrLName());
		this.usrMobile.setValue(aSecurityUser.getUsrMobile());
		this.usrEmail.setValue(aSecurityUser.getUsrEmail());
		this.usrEnabled.setChecked(aSecurityUser.isUsrEnabled());
		fillComboBox(this.disableReason, aSecurityUser.getDisableReason(), disableReasonList, "");
		this.usrCanSignonFrom.setValue(aSecurityUser.getUsrCanSignonFrom());
		this.usrCanSignonTo.setValue(aSecurityUser.getUsrCanSignonTo());
		this.usrCanOverrideLimits.setChecked(aSecurityUser.isUsrCanOverrideLimits());
		this.usrAcExp.setChecked(aSecurityUser.isUsrAcExp());
		this.usrAcLocked.setChecked(aSecurityUser.isUsrAcLocked());
		this.usrAcExpDt.setValue(aSecurityUser.getUsrAcExpDt());

		if (securityUser.isNewRecord()) {
			this.usrDftAppId.setSelectedIndex(0);
		} else {
			this.usrDftAppId.setValue(PennantApplicationUtil.getLabelDesc(String.valueOf(securityUser.getUsrDftAppId()),
					PennantStaticListUtil.getAppCodes()));
		}

		fillComboBox(authType, securityUser.getAuthType(), authTypesList, "");

		fillComboBox(ldapDomainName, securityUser.getldapDomainName(), ldapDomainList, "");

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

		if (aSecurityUser.getBusinessVertical() != null) {
			this.businessvertical.setValue(aSecurityUser.getBusinessVerticalCode());
			this.businessvertical.setDescription(aSecurityUser.getBusinessVerticalDesc());
			BusinessVertical businessVertical = new BusinessVertical();
			businessVertical.setId(aSecurityUser.getBusinessVertical());
			this.businessvertical.setObject(businessVertical);
		}

		fillComboBox(this.employeeType, aSecurityUser.getEmployeeType(), employeeTypeList, "");
		setDisableReasonVisibility(aSecurityUser.isUsrEnabled(), aSecurityUser);

		doFillReportingManagerDetails(aSecurityUser.getReportingManagersList());

		this.recordStatus.setValue(aSecurityUser.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To make the Disable reason visible based on user enable unchecked
	 * 
	 * @param event
	 */
	public void onCheck$usrEnabled(Event event) {
		setDisableReasonVisibility(this.usrEnabled.isChecked(), getSecurityUser());
	}

	/**
	 * To make the Disable reason visible based on user enable unchecked
	 * 
	 * @param usrEnabled    - Validate if the user is enabled or not
	 * @param aSecurityUser - Bean value
	 */
	private void setDisableReasonVisibility(boolean usrEnabled, SecurityUser aSecurityUser) {
		if (!usrEnabled) {
			this.label_SecurityUserDialog_DisableReason.setVisible(true);
			this.disableReason.setVisible(true);
			fillComboBox(this.disableReason, aSecurityUser.getDisableReason(), this.disableReasonList, "");
		} else {
			aSecurityUser.setDisableReason(null);
			fillComboBox(this.disableReason, "", this.disableReasonList, "");
			this.label_SecurityUserDialog_DisableReason.setVisible(false);
			this.disableReason.setVisible(false);
		}
	}

	/**
	 * Set the required components access based on the authentication type selection.
	 */
	public void onChange$authType(Event event) {
		setPasswordRowVisibility(this.authType.getSelectedItem().getValue().toString());
		setPasswordInstructionsVisibility(this.authType.getSelectedItem().getValue().toString());
		this.usrLogin.setErrorMessage("");
		this.txtbox_Password.setValue("");
		this.txtbox_confirm_Password.setValue("");
		showLDAPDomainName();
		setUserDetails();
	}

	/**
	 * Set the required components access based on the authentication type.
	 */
	private void setPasswordRowVisibility(String authType) {
		boolean isDAO = AuthenticationType.DAO.name().equals(authType);
		this.rowSecurityUserDialogUsrPwd.setVisible(isDAO && !isReadOnly("SecurityUserDialog_usrPwd"));
		this.rowSecurityUserDialogUsrConfirmPwd.setVisible(isDAO && !isReadOnly("SecurityUserDialog_usrPwd"));
	}

	/**
	 * Set the required components help access based on the authentication type.
	 */
	private void setPasswordInstructionsVisibility(String authType) {
		boolean isDAO = AuthenticationType.DAO.name().equals(authType);
		this.panelPasswordInstructions.setVisible(isDAO);
	}

	public void onChange$txtbox_Password(InputEvent event) {
		txtbox_Password.setValue(event.getValue());
	}

	public void onChange$txtbox_Password1(InputEvent event) {
		txtbox_Password1.setValue(event.getValue());
	}

	public void onChange$txtbox_confirm_Password(InputEvent event) {
		txtbox_confirm_Password.setValue(event.getValue());
	}

	public void onChange$txtbox_confirm_Password1(InputEvent event) {
		txtbox_confirm_Password1.setValue(event.getValue());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityUser
	 */
	public void doWriteComponentsToBean(SecurityUser aSecurityUser) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		List<WrongValueException> tab1 = new ArrayList<>();
		List<WrongValueException> tab2 = new ArrayList<>();

		try {
			aSecurityUser.setUsrLogin(StringUtils.trimToEmpty(this.usrLogin.getValue()));
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setAuthType(this.authType.getSelectedItem().getValue().toString());
			if (AuthenticationType.DAO.name().equals(aSecurityUser.getAuthType())) {
				if (this.rowSecurityUserDialogUsrPwd.isVisible()) {
					try {
						if (StringUtils.isBlank(this.txtbox_Password1.getValue())) {
							throw new WrongValueException(this.txtbox_Password, Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_SecurityUserDialog_UsrPwd.value") }));
						}
						aSecurityUser.setUsrPwd(StringUtils.trimToEmpty(this.txtbox_Password1.getValue()));
					} catch (WrongValueException we) {
						tab1.add(we);
					}
				}

				try {
					if (this.rowSecurityUserDialogUsrPwd.isVisible()
							&& StringUtils.isBlank(this.txtbox_confirm_Password1.getValue())) {
						throw new WrongValueException(this.txtbox_confirm_Password, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_SecurityUserDialog_UsrconfirmPwd.value") }));
					}
				} catch (WrongValueException we) {
					tab1.add(we);
				}

				try {
					// Check whether the confirmed input matches with the actual.
					if (this.rowSecurityUserDialogUsrPwd.isVisible()) {
						if (StringUtils.isNotBlank(this.txtbox_Password1.getValue())
								&& StringUtils.isNotBlank(this.txtbox_confirm_Password1.getValue())) {
							if (!(StringUtils.trimToEmpty(this.txtbox_Password1.getValue())
									.equals(StringUtils.trimToEmpty(this.txtbox_confirm_Password1.getValue())))) {
								throw new WrongValueException(txtbox_confirm_Password,
										Labels.getLabel("label_SecurityUserDialog_Pwd_not_match.value"));
							}
						}
					}
				} catch (WrongValueException we) {
					tab1.add(we);
				}

				try {
					if (this.rowSecurityUserDialogUsrPwd.isVisible()) {
						if (StringUtils.isNotBlank(this.txtbox_Password1.getValue())) {
							this.validate(this.txtbox_Password,
									StringUtils.trimToEmpty(this.txtbox_Password1.getValue()));
						}
					}
				} catch (WrongValueException we) {
					tab1.add(we);
				}

				if (this.rowSecurityUserDialogUsrPwd.isVisible()) {
					if (StringUtils.isNotEmpty(this.txtbox_Password1.getValue())) {
						PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");
						aSecurityUser.setUsrPwd(pwdEncoder
								.encode(AESCipherUtil.decrypt(aSecurityUser.getUsrPwd(), txtbox_randomKey.getValue())));
					}
				}
			} else if (AuthenticationType.LDAP.name().equals(aSecurityUser.getAuthType())) {
				if (this.ldapDomain.isVisible()) {
					try {
						if (StringUtils.isBlank(this.ldapDomainName.getValue())) {
							throw new WrongValueException(this.txtbox_Password, Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_SecurityUserDialog_ldapDomainName.value") }));
						}
						aSecurityUser.setldapDomainName(this.ldapDomainName.getSelectedItem().getValue());
					} catch (WrongValueException we) {
						tab1.add(we);
					}
				}
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUserStaffID(this.userStaffID.getValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrFName(StringUtils.trimToEmpty(this.usrFName.getValue()));
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrMName(StringUtils.trimToEmpty(this.usrMName.getValue()));
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrLName(StringUtils.trimToEmpty(this.usrLName.getValue()));

		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrMobile(this.usrMobile.getValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrEmail(StringUtils.trimToEmpty(this.usrEmail.getValue()));
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrEnabled(this.usrEnabled.isChecked());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {

			if (!this.usrEnabled.isChecked() && CollectionUtils.isNotEmpty(disableReasonList)) {
				if (this.disableReason.getSelectedItem() != null
						&& !StringUtils.trimToEmpty(this.disableReason.getSelectedItem().getValue().toString())
								.equals(PennantConstants.List_Select)) {
					aSecurityUser.setDisableReason(this.disableReason.getSelectedItem().getValue().toString());
				} else {
					aSecurityUser.setDisableReason(PennantConstants.List_Select);
				}

				if ("#".equals(getComboboxValue(this.disableReason)) && !this.disableReason.isDisabled()) {
					throw new WrongValueException(this.disableReason, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_SecurityUserDialog_DisableReason.value") }));
				} else {
					aSecurityUser.setDisableReason(this.disableReason.getSelectedItem().getValue().toString());
				}
			} else {
				aSecurityUser.setDisableReason(PennantConstants.List_Select);
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			if (this.usrCanSignonFrom != null) {
				aSecurityUser.setUsrCanSignonFrom(PennantAppUtil.getTime(this.usrCanSignonFrom.getValue()));
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			if (this.usrCanSignonTo != null) {
				aSecurityUser.setUsrCanSignonTo(PennantAppUtil.getTime(this.usrCanSignonTo.getValue()));
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}
		try {
			if (this.usrAcExpDt != null) {
				aSecurityUser.setUsrAcExpDt(this.usrAcExpDt.getValue());
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			/* Check whether usrCanSignonTo time is before usrCanSignonFrom or not */
			if ((this.usrCanSignonTo.getValue() != null) && (this.usrCanSignonFrom.getValue() != null)) {
				int timeDiff = DateUtil.compareTime(aSecurityUser.getUsrCanSignonFrom(),
						aSecurityUser.getUsrCanSignonTo(), false);
				if (timeDiff == 1 || timeDiff == 0) {
					throw new WrongValueException(this.usrCanSignonTo,
							Labels.getLabel("FIELD_TIME_MUST_AFTER",
									new String[] { Labels.getLabel("label_SecurityUserDialog_UsrCanSignonTo.value"),
											Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value") }));
				}
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			if ((this.usrCanSignonFrom.getValue() == null) && (this.usrCanSignonTo.getValue() != null)) {
				throw new WrongValueException(this.usrCanSignonFrom, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SecurityUserDialog_UsrCanSignonFrom.value") }));
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrCanOverrideLimits(this.usrCanOverrideLimits.isChecked());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrAcExp(this.usrAcExp.isChecked());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrAcLocked(this.usrAcLocked.isChecked());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setLovDescUsrLanguage(this.usrLanguage.getDescription());
			aSecurityUser.setUsrLanguage(this.usrLanguage.getValidatedValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			String strUsrDftAppId = this.usrDftAppId.getSelectedItem().getValue();
			if (StringUtils.isBlank(strUsrDftAppId)) {
				throw new WrongValueException(this.usrDftAppId, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_SecurityUserDialog_UsrDftAppCode.value") }));
			}
			aSecurityUser.setUsrDftAppId(Integer.parseInt(strUsrDftAppId));
			aSecurityUser.setUsrDftAppCode(App.CODE);
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setLovDescUsrBranchCodeName(this.usrBranchCode.getDescription());
			aSecurityUser.setUsrBranchCode(this.usrBranchCode.getValidatedValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setLovDescUsrDeptCodeName(this.usrDeptCode.getDescription());
			aSecurityUser.setUsrDeptCode(this.usrDeptCode.getValidatedValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setUsrIsMultiBranch(this.usrIsMultiBranch.isChecked());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setLovDescUsrDesg(this.usrDesg.getDescription());
			aSecurityUser.setUsrDesg(this.usrDesg.getValue());
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			this.businessvertical.getValue();
			Object object = this.businessvertical.getObject();
			if (object != null) {
				aSecurityUser.setBusinessVertical(((BusinessVertical) object).getId());
			} else {
				aSecurityUser.setBusinessVertical(null);
			}

		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {
			aSecurityUser.setReportingManagersList(this.reportingManagerDetailList);

		} catch (WrongValueException we) {
			tab1.add(we);
		}

		try {

			if (CollectionUtils.isNotEmpty(employeeTypeList)) {
				Comboitem empType = this.employeeType.getSelectedItem();
				if (empType != null && !PennantConstants.List_Select.equals(empType.getValue().toString())) {
					aSecurityUser.setEmployeeType(empType.getValue().toString());
				} else {
					aSecurityUser.setEmployeeType(PennantConstants.List_Select);
				}

				if ("#".equals(getComboboxValue(this.employeeType)) && !this.employeeType.isDisabled()) {
					throw new WrongValueException(this.employeeType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_SecurityUserDialog_EmployeeType.value") }));
				} else {
					aSecurityUser.setEmployeeType(empType.getValue().toString());
				}
			} else {
				aSecurityUser.setEmployeeType(PennantConstants.List_Select);
			}
		} catch (WrongValueException we) {
			tab1.add(we);
		}

		if (!findUser &&

				getSecurityUser().isNewRecord() && aSecurityUser.getUsrLogin() != null) {
			tab1.add(new WrongValueException(this.usrLogin, "User not found"));
		} else {
			this.usrLogin.setErrorMessage("");
			this.usrLogin.setConstraint("");
		}

		if (this.secUserDivBranchsTab.isVisible()
				&& (divBranch_Rows != null && !divBranch_Rows.getChildren().isEmpty())) {

			if (CollectionUtils.isEmpty(tab1)) {
				if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
					doSaveDivBasedClusterDetails(aSecurityUser, tab2);
				} else {
					doSaveDivBranchDetails(aSecurityUser);
				}
			}
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (CollectionUtils.isNotEmpty(tab1)) {
			int size = tab1.size();
			WrongValueException[] wvea = new WrongValueException[size];

			// Clear the fields on exception.
			this.txtbox_Password.setValue("");
			this.txtbox_confirm_Password.setValue("");
			/* this.div_PwdStatusMeter.setStyle("background-color:white"); */
			/* this.label_PwdStatus.setValue(""); */

			for (int i = 0; i < size; i++) {
				wvea[i] = tab1.get(i);
			}

			secUserDetailsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		} else if (CollectionUtils.isNotEmpty(tab2)) {
			int size = tab2.size();
			WrongValueException[] wvea = new WrongValueException[size];

			for (int i = 0; i < size; i++) {
				wvea[i] = tab2.get(i);
			}

			secUserDivBranchsTab.setSelected(true);
			throw new WrongValuesException(wvea);
		}

		aSecurityUser.setRecordStatus(this.recordStatus.getValue());

		if (securityUser.isNewRecord()) {
			aSecurityUser.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			aSecurityUser.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param usrLogin authType disable when double click on ADMIN_MAKER or ADMIN_APPROVER
	 */
	private void doSetAuthType(String userType) {
		if (StringUtils.contains(userType, UserType.ADMIN.name())) {
			this.authType.setDisabled(true);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSecurityUser
	 */
	public void doShowDialog(SecurityUser aSecurityUser) {
		logger.debug(Literal.ENTERING);

		// set Read only mode accordingly if the object is new or not.

		// fill the data in divisionBranch tab
		if (this.secUserDivBranchsTab.isVisible()) {
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
				appendDivisions(getDivisionDetails(aSecurityUser));
			} else {
				doFillDivisionBranchTab(aSecurityUser);
			}
		}

		if (aSecurityUser.isNewRecord()) {
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
			showLDAPDomainName();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_SecurityUserDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method sets all roleApps as ComboItems for ComboBox
	 */
	private void setListusrDftAppId() {
		logger.debug(Literal.ENTERING);
		for (int i = 0; i < listUsrDftAppId.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel(listUsrDftAppId.get(i).getLabel());
			comboitem.setValue(listUsrDftAppId.get(i).getValue());
			this.usrDftAppId.appendChild(comboitem);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * 
	 * @throws InterruptedException
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		if (!this.usrLogin.isReadonly()) {
			this.usrLogin.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrLogin.value"), null, true));
		}

		if (!this.authType.isDisabled()) {
			this.authType.setConstraint(new StaticListValidator(authTypesList,
					Labels.getLabel("label_SecurityUserDialog_AuthenticationType.value")));
		}
		if (!this.ldapDomainName.isDisabled()) {
			this.ldapDomainName.setConstraint(new StaticListValidator(ldapDomainList,
					Labels.getLabel("label_SecurityUserDialog_ldapDomainName.value")));
		}

		if (!this.userStaffID.isReadonly()) {
			this.userStaffID
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UserStaffID.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.usrFName.isReadonly()) {
			this.usrFName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrFName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.usrMName.isReadonly()) {
			this.usrMName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrMName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, false));
		}
		if (!this.usrLName.isReadonly()) {
			this.usrLName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrLName.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.usrMobile.isReadonly()) {
			if (StringUtils.isNotEmpty(this.usrMobile.getValue())) {
				this.usrMobile.setConstraint(
						new PTMobileNumberValidator(Labels.getLabel("label_SecurityUserSearch_UsrMobile.value"), true,
								PennantRegularExpressions.REGEX_MOBILE));
			} else {
				this.usrMobile.setConstraint(new PTMobileNumberValidator(
						Labels.getLabel("label_SecurityUserSearch_UsrMobile.value"), false));
			}

		}
		if (!this.usrEmail.isReadonly()) {
			this.usrEmail.setConstraint(
					new PTEmailValidator(Labels.getLabel("label_SecurityUserSearch_UsrEmail.value"), false));

		}
		if (!this.usrDftAppId.isDisabled()) {
			this.usrDftAppId.setConstraint(new StaticListValidator(listUsrDftAppId,
					Labels.getLabel("label_SecurityUserDialog_UsrDftAppCode.value")));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.usrLogin.setConstraint("");
		this.authType.setConstraint("");
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
		this.disableReason.setConstraint("");
		this.employeeType.setConstraint("");

		doRemoveClusterValidation();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);
		this.usrBranchCode.setConstraint(new PTStringValidator(
				Labels.getLabel("label_SecurityUserDialog_UsrBranchCode.value"), null, true, true));
		this.usrDeptCode.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrDeptCode.value"), null, true, true));
		this.usrLanguage.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrLanguage.value"), null, true, true));
		this.usrDesg.setConstraint(
				new PTStringValidator(Labels.getLabel("label_SecurityUserDialog_UsrDesg.value"), null, true, true));
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);
		this.usrBranchCode.setConstraint("");
		this.usrDeptCode.setConstraint("");
		this.usrLanguage.setConstraint("");
		this.usrDesg.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.usrLogin.setErrorMessage("");
		this.authType.setErrorMessage("");
		this.ldapDomainName.setErrorMessage("");
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
		this.disableReason.setErrorMessage("");
		this.employeeType.setErrorMessage("");
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Extended to enforce policy restrictions.
	 */
	@Override
	public void validate(Component comp, Object value) {
		logger.debug(Literal.ENTERING);
		if (StringUtils.isNotEmpty(this.txtbox_Password.getValue())) {
			if (changePasswordModel.checkPasswordCriteria(this.usrLogin.getValue(), this.txtbox_Password.getValue())) {
				throw new WrongValueException(txtbox_Password, Labels.getLabel("label_Invalid_Password"));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);

		doDelete(Labels.getLabel("label_SecurityUserDialog_UsrLogin.value") + " : " + aSecurityUser.getUsrLogin(),
				aSecurityUser);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

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
		this.ldapDomainName.setDisabled(isReadOnly("SecurityUserDialog_usrLogin"));
		this.txtbox_Password.setReadonly(isReadOnly("SecurityUserDialog_usrPwd"));
		this.userStaffID.setReadonly(isReadOnly("SecurityUserDialog_userStaffID"));
		this.usrFName.setReadonly(isReadOnly("SecurityUserDialog_usrFName"));
		this.usrMName.setReadonly(isReadOnly("SecurityUserDialog_usrMName"));
		this.usrLName.setReadonly(isReadOnly("SecurityUserDialog_usrLName"));
		this.usrMobile.setReadonly(isReadOnly("SecurityUserDialog_usrMobile"));
		this.usrEmail.setReadonly(isReadOnly("SecurityUserDialog_usrEmail"));
		this.usrAcExp.setDisabled(isReadOnly("SecurityUserDialog_usrAcExp"));
		this.usrAcLocked.setDisabled(isReadOnly("SecurityUserDialog_usrAcLocked"));
		this.usrEnabled.setDisabled(isReadOnly("SecurityUserDialog_usrEnabled"));
		this.disableReason.setDisabled(isReadOnly("SecurityUserDialog_usrDisableReason"));
		this.usrCanSignonFrom.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonFrom"));
		this.usrCanSignonTo.setDisabled(isReadOnly("SecurityUserDialog_usrCanSignonTo"));
		this.usrCanOverrideLimits.setDisabled(isReadOnly("SecurityUserDialog_usrCanOverrideLimits"));
		this.usrLanguage.setReadonly(isReadOnly("SecurityUserDialog_usrLanguage"));
		this.usrDftAppId.setDisabled(isReadOnly("SecurityUserDialog_usrDftAppCode"));
		this.usrBranchCode.setReadonly(isReadOnly("SecurityUserDialog_usrBranchCode"));
		this.usrDeptCode.setReadonly(isReadOnly("SecurityUserDialog_usrDeptCode"));
		this.usrIsMultiBranch.setDisabled(isReadOnly("SecurityUserDialog_usrIsMultiBranch"));
		this.txtbox_confirm_Password.setReadonly(isReadOnly("SecurityUserDialog_usrConfirmPwd"));
		this.usrDesg.setReadonly(isReadOnly("SecurityUserDialog_usrDesg"));
		this.usrAcExpDt.setDisabled(isReadOnly("SecurityUserDialog_UsrAcExpDt"));
		this.btnNewReportingManagerList.setDisabled(isReadOnly("button_SecurityUserDialog_RM_btnNew"));
		this.businessvertical.setReadonly(isReadOnly("SecurityUserDialog_RM_Businessvertical"));
		this.employeeType.setDisabled(isReadOnly("SecurityUserDialog_usrEmployeeType"));

		readOnlyComponent(isReadOnly("button_SecurityUserDialog_RM_btnNew"), this.btnNewReportingManagerList);
		readOnlyComponent(isReadOnly("button_SecurityUserDialog_RM_btnNew"), this.btnNewReportingManagerList);

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

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
			doEditClusterDivisions(false);
		} else {
			this.ldapDomainName.setDisabled(isReadOnly("SecurityUserDialog_usrLogin"));
			doDisableDivBranchs(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.usrLogin.setReadonly(true);
		this.authType.setDisabled(true);
		this.ldapDomainName.setDisabled(true);
		this.txtbox_Password.setReadonly(true);
		this.txtbox_confirm_Password.setReadonly(true);
		this.userStaffID.setReadonly(true);
		this.usrFName.setReadonly(true);
		this.usrMName.setReadonly(true);
		this.usrLName.setReadonly(true);
		this.usrMobile.setReadonly(true);
		this.usrEmail.setReadonly(true);
		this.usrEnabled.setDisabled(true);
		this.disableReason.setReadonly(true);
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
		this.usrAcExpDt.setDisabled(true);
		this.employeeType.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		doEditClusterDivisions(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.usrLogin.setValue("");
		this.txtbox_Password.setValue("");
		this.userStaffID.setValue("");
		this.usrFName.setValue("");
		this.usrMName.setValue("");
		this.usrLName.setValue("");
		this.usrMobile.setValue("");
		this.usrEmail.setValue("");
		this.usrEnabled.setChecked(false);
		this.disableReason.setValue("");
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
		this.employeeType.setValue("");

		doClearClusters();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final SecurityUser aSecurityUser = new SecurityUser();
		BeanUtils.copyProperties(getSecurityUser(), aSecurityUser);
		boolean isNew = aSecurityUser.isNewRecord();

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SecurityUser object with the components data
		doWriteComponentsToBean(aSecurityUser);

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
					aSecurityUser.setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));
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
				// Force user to change on his/her first login.
				aSecurityUser.setUsrAcExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), -1));
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
			this.txtbox_Password.setValue("");
			this.txtbox_confirm_Password.setValue("");
			this.txtbox_Password.setAutofocus(true);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace and setting audit details to auditHeader
	 * 
	 * @param aSecurityUser
	 * @param tranType
	 * @return processCompleted (boolean)
	 * @throws InterruptedException
	 */
	protected boolean doProcess(SecurityUser aSecurityUser, String tranType) throws InterruptedException {
		logger.debug(Literal.ENTERING);
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

			// IRRCode details
			if (aSecurityUser.getReportingManagersList() != null
					&& !aSecurityUser.getReportingManagersList().isEmpty()) {
				for (ReportingManager reportingManager : aSecurityUser.getReportingManagersList()) {
					reportingManager.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					reportingManager.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					reportingManager.setUserDetails(getUserWorkspace().getLoggedInUser());
					reportingManager.setRecordStatus(aSecurityUser.getRecordStatus());
					reportingManager.setWorkflowId(aSecurityUser.getWorkflowId());
					reportingManager.setTaskId(taskId);
					reportingManager.setNextTaskId(nextTaskId);
					reportingManager.setRoleCode(getRole());
					reportingManager.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aSecurityUser.getRecordType())) {
						if (StringUtils.trimToNull(reportingManager.getRecordType()) == null) {
							reportingManager.setRecordType(aSecurityUser.getRecordType());
							reportingManager.setNewRecord(true);
						}
					}
				}
			}

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
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SecurityUser aSecurityUser = (SecurityUser) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {

					auditHeader = securityUserService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = securityUserService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aSecurityUser.setApprovedOn(new Timestamp(System.currentTimeMillis()));
					aSecurityUser.setApprovedBy(getUserWorkspace().getLoggedInUser().getUserId());
					auditHeader = securityUserService.doApprove(auditHeader);

					if (aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = securityUserService.doReject(auditHeader);
					if (aSecurityUser.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);
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
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$businessvertical(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = businessvertical.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.businessvertical.setValue("");
			this.businessvertical.setDescription("");
		} else {
			BusinessVertical details = (BusinessVertical) dataObject;
			this.businessvertical.setValue(details.getCode());
			this.businessvertical.setDescription(details.getDescription());
		}
		logger.debug(Literal.LEAVING);
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
				// Check whether criteria matched.
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd))) && (StringUtils.trimToEmpty(pwd).length() < pwdMinLenght)) {
					pwdstatusCode = 2;
				}
				// Check whether the minimum required characters available.
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd)))
						&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght && splCharCount < specialCharCount)) {
					pwdstatusCode = 3;
				}
				// Check whether the minimum required special characters available.
				if ((!changePasswordModel.checkPasswordCriteria(
						StringUtils.trimToEmpty(SecurityUserDialogCtrl.this.usrLogin.getValue()),
						StringUtils.trimToEmpty(pwd)))
						&& (StringUtils.trimToEmpty(pwd).length() >= pwdMinLenght
								&& splCharCount >= specialCharCount)) {
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
		logger.debug(Literal.ENTERING);
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_SecurityUserDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when clicks on button "btnNotes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		final Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method creates Notes Object ,sets data and returns that notes object
	 * 
	 * @return Notes
	 */
	private Notes getNotes() {
		logger.debug(Literal.ENTERING);
		Notes notes = new Notes();
		notes.setModuleName("SecurityUsers");
		notes.setReference(String.valueOf(getSecurityUser().getUsrID()));
		notes.setVersion(getSecurityUser().getVersion());
		logger.debug(Literal.LEAVING);
		return notes;
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSecurityUserListCtrl().search();
	}

	/**
	 * Method for Rendering Division Branch Details
	 */
	public void doFillDivisionBranchTab(SecurityUser aSecurityUser) {
		logger.debug(Literal.ENTERING);
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

				if (getSecurityUser().isNewRecord()) {
					dynamicDivBranchs.put(userDivision, new HashMap<String, Object>());
				} else {
					Map<String, Object> tempSecDivBrMap = new HashMap<String, Object>();
					String branchs = "";
					String toolTipDesc = "";
					for (SecurityUserDivBranch branch : aSecurityUser.getSecurityUserDivBranchList()) {
						if (branch.getUserDivision().equals(userDivision)) {
							tempSecDivBrMap.put(branch.getUserBranch(), branch.getBranchDesc());
							branchs = branchs.concat(branch.getUserBranch() + ",");
							toolTipDesc = toolTipDesc.concat(branch.getBranchDesc() + " , ");
							branch.setBefImage(branch);
							this.befImgUsrDivBranchsList.add(branch);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to Fetch Division Details
	 * 
	 * @return
	 */
	public List<DivisionDetail> getDivisionDetails() {
		Search search = new Search(DivisionDetail.class);
		search.addTabelName("SMTDivisionDetail_AView");
		return searchProcessor.getResults(search);
	}

	/**
	 * This method is to Fetch Division Details
	 * 
	 * @return
	 */
	public List<SecurityUserDivBranch> getDivisionDetails(SecurityUser aSecurityUser) {
		List<SecurityUserDivBranch> list = aSecurityUser.getSecurityUserDivBranchList();
		List<DivisionDetail> divisions = getDivisionDetails();
		if (aSecurityUser.isNewRecord() || aSecurityUser.getSecurityUserDivBranchList().isEmpty()) {
			for (DivisionDetail division : divisions) {
				SecurityUserDivBranch branch = new SecurityUserDivBranch();
				branch.setEntity(division.getEntityCode());
				branch.setUserDivision(division.getDivisionCode());
				branch.setDivisionDesc(division.getDivisionCodeDesc());
				list.add(branch);
			}

		}

		List<SecurityUserDivBranch> tempList = new ArrayList<>();
		for (DivisionDetail division : divisions) {
			boolean added = false;
			for (SecurityUserDivBranch branch : list) {
				if (division.getDivisionCode().equals(branch.getUserDivision())) {
					added = true;
					continue;
				}
			}

			if (!added) {
				SecurityUserDivBranch divBranch = new SecurityUserDivBranch();
				divBranch.setEntity(division.getEntityCode());
				divBranch.setEntityDesc(division.getEntityDesc());
				divBranch.setUserDivision(division.getDivisionCode());
				divBranch.setDivisionDesc(division.getDivisionCodeDesc());
				tempList.add(divBranch);
			}

		}

		list.addAll(tempList);

		List<SecurityUserDivBranch> filterList = new ArrayList<>();
		Map<String, SecurityUserDivBranch> map = new HashMap<>();
		for (SecurityUserDivBranch divBranch : list) {
			SecurityUserDivBranch division = map.get(divBranch.getUserDivision());
			if (division == null) {
				division = divBranch;
				map.put(divBranch.getUserDivision(), division);
			}

			if (divBranch.getEntity() != null) {
				Entity entity = new Entity();
				entity.setEntityCode(divBranch.getEntity());
				entity.setEntityDesc(divBranch.getEntityDesc());
				division.getEntities().put(divBranch.getEntity(), entity);
			}

			if (divBranch.getClusterId() != null) {
				Cluster cluster = new Cluster();
				cluster.setId(divBranch.getClusterId());
				cluster.setCode(divBranch.getClusterCode());
				cluster.setEntity(divBranch.getEntity());
				cluster.setClusterType(divBranch.getClusterType());
				division.getClusters().put(divBranch.getClusterCode(), cluster);
			}

			if (divBranch.getUserBranch() != null) {
				Branch branch = new Branch();
				branch.setBranchCode(divBranch.getUserBranch());
				branch.setEntity(divBranch.getEntity());
				branch.setBranchDesc(divBranch.getBranchDesc());
				division.getBranches().put(divBranch.getUserBranch(), branch);
			}

		}

		for (Entry<String, SecurityUserDivBranch> entry : map.entrySet()) {
			filterList.add(entry.getValue());
		}

		return filterList;
	}

	/**
	 * This Method is called when division button is clicked
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onButtonClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Button btn = (Button) event.getOrigin().getTarget();
		this.divBranchs = dynamicDivBranchs.get(btn.getId());

		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_SecurityUserDialog, "Branch",
				this.divBranchs);

		Textbox txtbx = (Textbox) btn.getPreviousSibling();
		if (dataObject instanceof String) {
			txtbx.setValue(dataObject.toString());
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String multivalues = details.keySet().toString();
				txtbx.setValue(multivalues.replace("[", "").replace("]", "").replace(" ", ""));
				String toolTipDesc = "";
				for (String key : details.keySet()) {
					Object obj = details.get(key);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to save the division branch details
	 * 
	 * @param securityUser
	 * @return
	 */
	public void doSaveDivBranchDetails(SecurityUser securityUser) {
		logger.debug(Literal.ENTERING);
		SecurityUserDivBranch division;
		List<SecurityUserDivBranch> secUsrDivBranchsList = new ArrayList<SecurityUserDivBranch>();
		try {
			for (Component row : divBranch_Rows.getChildren()) {
				Label label = (Label) row.getFirstChild().getFirstChild();
				String div = label == null ? "" : label.getValue();
				Textbox branch = (Textbox) row.getFirstChild().getNextSibling().getFirstChild().getNextSibling();
				String divBranches = branch == null ? "" : branch.getValue();
				if (StringUtils.isNotBlank(divBranches)) {
					String[] branches = divBranches.split(",");
					for (String divBranch : branches) {
						division = new SecurityUserDivBranch();
						division.setUsrID(getSecurityUser().getUsrID());
						division.setUserDivision(div);
						division.setUserBranch(divBranch);
						division.setRecordStatus("");
						division.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						division.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						division.setUserDetails(getUserWorkspace().getLoggedInUser());

						secUsrDivBranchsList.add(division);
					}
				}
			}
			securityUser.setSecurityUserDivBranchList(newDivBranchsProcess(secUsrDivBranchsList));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.befImgUsrDivBranchsList.clear();
			showMessage(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private Component getComponent(Row row, int index) {
		int i = 0;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return component.getLastChild().getLastChild();
			}
			i++;
		}
		return null;
	}

	private String getLabel(Row row, int index) {
		int i = 0;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return ((Label) component.getFirstChild()).getValue();
			}
			i++;
		}
		return null;
	}

	/**
	 * This method is to save the division branch details
	 * 
	 * @param securityUser
	 * @return
	 */

	public void doSaveDivBasedClusterDetails(SecurityUser securityUser, List<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);
		List<SecurityUserDivBranch> list = new ArrayList<>();
		try {
			for (Component component : divBranch_Rows.getChildren()) {
				setClusterValues(component, list, wve);
			}

			securityUser.setSecurityUserDivBranchList(list);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.befImgUsrDivBranchsList.clear();
			showMessage(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setClusterValues(Component component, List<SecurityUserDivBranch> list,
			List<WrongValueException> wve) {
		long secUserId = getSecurityUser().getUsrID();
		long userId = getUserWorkspace().getLoggedInUser().getUserId();
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();

		SecurityUserDivBranch division;
		boolean entities = false;
		boolean clusters = false;
		boolean branches = false;

		Map<String, Object> entitiesMap = null;
		Map<String, Object> clustersMap = null;
		Map<String, Object> branchesMap = null;

		Row row = (Row) component;
		String divisionCode = (String) row.getAttribute("DivisionCode");

		Combobox accessTypeCombobox = (Combobox) getComponent(row, 1);

		Combobox entityCombox = null;
		ExtendedCombobox parentCluster = null;
		Combobox clusterTypeCombox = null;

		ExtendedCombobox entitiesExtendedCombox = null;
		ExtendedCombobox branchesExtendedCombox = null;
		ExtendedCombobox clustersExtendedCombox = null;

		String accessType = getComboboxValue(accessTypeCombobox);

		if ("#".equals(accessType)) {
			return;
		}

		if (PennantConstants.ACCESSTYPE_ENTITY.equals(accessType)) {
			entitiesExtendedCombox = (ExtendedCombobox) getComponent(row, 3);
			entities = true;
			entitiesMap = getSelectedValues(entitiesExtendedCombox);
		} else if (PennantConstants.ACCESSTYPE_CLUSTER.equals(accessType)) {
			clustersExtendedCombox = (ExtendedCombobox) getComponent(row, 7);
			clusters = true;
			clustersMap = getSelectedValues(clustersExtendedCombox);
		} else if (PennantConstants.ACCESSTYPE_BRANCH.equals(accessType)) {
			branchesExtendedCombox = (ExtendedCombobox) getComponent(row, 7);
			branches = true;
			branchesMap = getSelectedValues(branchesExtendedCombox);
		}

		String selectedValues = null;

		if (entities) {
			entitiesExtendedCombox = (ExtendedCombobox) getComponent(row, 3);
			if (entitiesExtendedCombox != null) {
				selectedValues = entitiesExtendedCombox.getValidatedValue();
				if (StringUtils.isEmpty(selectedValues)) {
					wve.add(new WrongValueException(entitiesExtendedCombox, getLabel(row, 2) + " cannot be blank."));
					return;
				}
			}

		} else if (clusters) {
			entityCombox = (Combobox) getComponent(row, 3);
			String entity = getComboboxValue(entityCombox);
			if ("#".equals(entity)) {
				wve.add(new WrongValueException(entityCombox, getLabel(row, 2) + " cannot be blank."));
				return;
			}

			clusterTypeCombox = (Combobox) getComponent(row, 5);
			String clusterType = getComboboxValue(clusterTypeCombox);
			if ("#".equals(clusterType)) {
				wve.add(new WrongValueException(clusterTypeCombox, getLabel(row, 4) + " cannot be blank."));
				return;
			}

			clustersExtendedCombox = (ExtendedCombobox) getComponent(row, 7);

			if (clustersExtendedCombox != null) {
				selectedValues = clustersExtendedCombox.getValidatedValue();
				if (StringUtils.isEmpty(selectedValues)) {
					wve.add(new WrongValueException(clustersExtendedCombox, getLabel(row, 6) + " cannot be blank."));
					return;
				}
			}

		} else if (branches) {
			entityCombox = (Combobox) getComponent(row, 3);
			String entity = getComboboxValue(entityCombox);
			if ("#".equals(entity)) {
				wve.add(new WrongValueException(entityCombox, getLabel(row, 2) + " cannot be blank."));
				return;
			}
			parentCluster = (ExtendedCombobox) getComponent(row, 5);
			if (parentCluster != null) {
				selectedValues = parentCluster.getValue();
			}
			if (StringUtils.isEmpty(selectedValues)) {
				wve.add(new WrongValueException(parentCluster, getLabel(row, 4) + " cannot be blank."));
				return;
			}

			branchesExtendedCombox = (ExtendedCombobox) getComponent(row, 7);
			if (branchesExtendedCombox != null) {
				selectedValues = branchesExtendedCombox.getValidatedValue();
				if (StringUtils.isEmpty(selectedValues)) {
					wve.add(new WrongValueException(branchesExtendedCombox, getLabel(row, 6) + " cannot be blank."));
					return;
				}
			}

		}

		if (entitiesMap != null) {
			for (String value : entitiesMap.keySet()) {
				division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(secUserId);
				division.setUserDivision(divisionCode);
				division.setEntity(value);

				division.setRecordStatus("");
				division.setLastMntBy(userId);
				division.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				division.setUserDetails(loggedInUser);

				list.add(division);
			}
		}

		if (clustersMap != null) {
			for (Entry<String, Object> object : clustersMap.entrySet()) {
				division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(secUserId);
				division.setUserDivision(divisionCode);
				division.setClusterId(((Cluster) object.getValue()).getId());
				division.setEntity(getComboboxValue(entityCombox));
				division.setClusterType(clusterTypeCombox.getValue());

				division.setRecordStatus("");
				division.setLastMntBy(userId);
				division.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				division.setUserDetails(loggedInUser);

				list.add(division);
			}
		}

		if (branchesMap != null) {
			for (String value : branchesMap.keySet()) {
				division = new SecurityUserDivBranch();
				division.setAccessType(accessType);
				division.setUsrID(secUserId);
				division.setUserDivision(divisionCode);
				division.setUserBranch(value);
				division.setEntity(getComboboxValue(entityCombox));
				division.setParentCluster(((Cluster) parentCluster.getObject()).getId());

				division.setRecordStatus("");
				division.setLastMntBy(userId);
				division.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				division.setUserDetails(loggedInUser);

				list.add(division);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedValues(ExtendedCombobox extendedCombobox) {
		Object object = extendedCombobox.getAttribute("data");
		return (Map<String, Object>) object;
	}

	public boolean isBranchNewRecord(SecurityUserDivBranch aSecurityUserDivBranch) {
		boolean isNew = false;
		for (SecurityUserDivBranch userBranch : getBefImgUsrDivBranchsList()) {
			if (!aSecurityUserDivBranch.getUserDivision().equals(userBranch.getUserDivision())
					&& !aSecurityUserDivBranch.getUserBranch().equals(userBranch.getUserBranch())) {
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
		logger.debug(Literal.ENTERING);
		List<SecurityUserDivBranch> newSecUsrDivBranchsList = new ArrayList<>();
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

					if (PennantConstants.RECORD_TYPE_DEL.equals(asecurityUserDivBranch.getRecordType())) {
						securityUserDivBranch2.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						securityUserDivBranch2.setNewRecord(false);
					}

					newSecUsrDivBranchsList.add(securityUserDivBranch2);
				}
			}
			if (!recordExists && !isNewRecord()) {
				asecurityUserDivBranch.setNewRecord(true);
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
		logger.debug(Literal.LEAVING);
		return newSecUsrDivBranchsList;
	}

	/**
	 * This method is to set disable for dynamically created division buttons
	 * 
	 * @param disable
	 */
	public void doDisableDivBranchs(boolean disable) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	public void validateBranchs() throws InterruptedException {
		if (!isReadOnly("SecurityUserDialog_usrDivBranch")) {
			if (StringUtils.trimToEmpty((String) userAction.getSelectedItem().getValue())
					.equals(PennantConstants.RCD_STATUS_SAVED)
					|| StringUtils.trimToEmpty((String) userAction.getSelectedItem().getValue())
							.equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
				this.secUserDivBranchsTab.setSelected(true);
				MessageUtil.showError(Labels.getLabel("SecUserDivBranchs_NotEmpty"));
			}
		}
	}

	// Organisation Structure Hierarchy

	private static List<Property> getAccessTypes() {
		List<Property> list = new ArrayList<>();
		list.add(new Property(PennantConstants.ACCESSTYPE_ENTITY, Labels.getLabel("label_BrachDiv_Entity")));
		list.add(new Property(PennantConstants.ACCESSTYPE_CLUSTER, Labels.getLabel("label_BrachDiv_Cluster")));
		list.add(new Property(PennantConstants.ACCESSTYPE_BRANCH, Labels.getLabel("label_BrachDiv_Branch")));
		return list;
	}

	private void appendDivisions(List<SecurityUserDivBranch> list) {
		int i = 0;
		for (SecurityUserDivBranch division : list) {
			Row row = new Row();
			row.setAttribute("DivisionCode", division.getUserDivision());
			row.setAttribute("DivisionDes", division.getDivisionDesc());

			Cell labelCell = new Cell();
			Cell accessTypeCell = new Cell();
			accessTypeCell.setId("accessTypeCell".concat(String.valueOf(i)));

			row.appendChild(labelCell);
			row.appendChild(accessTypeCell);
			labelCell.appendChild(new Label(division.getDivisionDesc()));

			Hbox hbox = new Hbox();

			Space space = new Space();
			space.setSpacing("2px");
			hbox.appendChild(space);

			Combobox combobox = new Combobox();
			combobox.setWidth("100px");
			combobox.addForward("onChange", self, "onChangeAccessType", row);
			fillList(combobox, getAccessTypes(), division.getAccessType());
			hbox.appendChild(combobox);

			accessTypeCell.appendChild(hbox);
			row.setParent(divBranch_Rows);
			i++;

			row.setAttribute("division", division);

			if (division.getAccessType() != null) {
				combobox.setValue(division.getAccessType());
				onChangeAccessType(row);
			}
		}
	}

	private void setReadOnly(Component component, String rightName, boolean readonly) {
		if (component != null && component instanceof Combobox) {
			if (readonly) {
				((Combobox) component).setDisabled(readonly);
			} else {
				((Combobox) component).setDisabled(isReadOnly(rightName));
			}
		} else if (component != null && component instanceof ExtendedCombobox) {
			if (readonly) {
				((ExtendedCombobox) component).setReadonly(readonly);
			} else {
				((ExtendedCombobox) component).setReadonly(isReadOnly(rightName));
			}
		}
	}

	private void doEditClusterDivisions(boolean readonly) {
		String rightName = "SecurityUserDialog_CH_Div_Branch";
		for (Component component : divBranch_Rows.getChildren()) {
			Row row = (Row) component;

			setReadOnly(getComponent(row, 1), rightName, readonly);

			setReadOnly(getComponent(row, 3), rightName, readonly);

			setReadOnly(getComponent(row, 5), rightName, readonly);

			setReadOnly(getComponent(row, 7), rightName, readonly);
		}
	}

	/**
	 * This Method is called when division button is clicked
	 * 
	 * @param event
	 */
	public void onChangeAccessType(ForwardEvent event) {
		Row row = (Row) event.getData();
		onChangeAccessType(row);
	}

	private void onChangeAccessType(Row row) {
		Combobox combobox = (Combobox) getComponent(row, 1);

		if (combobox == null) {
			return;
		}

		String accessType = combobox.getSelectedItem().getValue();

		List<Component> components = new ArrayList<>();

		int i = 0;
		for (Component component : row.getChildren()) {
			if (i <= 1) {
				i++;
				continue;
			}
			components.add(component);
		}

		for (Component component : components) {
			row.removeChild(component);
		}

		if (PennantConstants.ACCESSTYPE_ENTITY.equals(accessType)) {
			appendEntities(row);
		} else if (PennantConstants.ACCESSTYPE_CLUSTER.equals(accessType)) {
			appendEntity(row);
			appendClusterType(row);
			appendClusters(row);
		} else if (PennantConstants.ACCESSTYPE_BRANCH.equals(accessType)) {
			appendEntity(row);
			appendParentCluster(row);
			appendBranches(row);
		}

		onChangeEntity(row);
	}

	public void onChangeEntity(ForwardEvent event) {
		Row accessRow = (Row) event.getData();
		onChangeEntity(accessRow);
	}

	public void onChangeEntity(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");

		Combobox accessTypeCombo = (Combobox) getComponent(row, 1);
		Combobox entityCombo = null;
		ExtendedCombobox cluster = null;
		Combobox clusterTypeCombobox = null;

		String accessType = null;
		if (accessTypeCombo != null) {
			accessType = accessTypeCombo.getSelectedItem().getValue();
		}

		if (getComponent(row, 3) instanceof Combobox) {
			entityCombo = (Combobox) getComponent(row, 3);
		}

		String selectedEntity = null;

		if (entityCombo != null) {
			selectedEntity = entityCombo.getSelectedItem().getValue();
		}

		if (getComponent(row, 5) instanceof ExtendedCombobox) {
			cluster = (ExtendedCombobox) getComponent(row, 5);
		} else if (getComponent(row, 5) instanceof Combobox) {
			clusterTypeCombobox = (Combobox) getComponent(row, 5);
		}

		if (PennantConstants.ACCESSTYPE_BRANCH.equals(accessType)) {
			if (selectedEntity != null && cluster != null && !"#".equals(selectedEntity)) {
				doSetClusterFilter(row, cluster, selectedEntity);
			} else {
				doSetClusterFilter(row, null, null);
			}
		}

		if (selectedEntity != null && clusterTypeCombobox != null) {
			List<Property> list = new ArrayList<>();
			List<Cluster> clusterList = clusterService.getClustersByEntity(selectedEntity);
			for (Cluster item : clusterList) {
				list.add(new Property(item.getClusterType(), item.getClusterType()));
			}

			fillList(clusterTypeCombobox, list, division.getClusterType());
		}
	}

	private void doSetClusterFilter(Row row, ExtendedCombobox cluster, String entity) {
		if (cluster == null) {
			return;
		}

		cluster.setMaxlength(8);
		cluster.setMandatoryStyle(true);
		cluster.setModuleName("Cluster");
		cluster.setValueColumn("Code");
		cluster.setDescColumn("Name");
		cluster.setValidateColumns(new String[] { "Code" });
		cluster.addForward("onFulfill", self, "onChangeParentCluster", row);

		if (entity == null) {
			cluster.setValue("");
			cluster.setFilters(new Filter[] { new Filter("Entity", null, Filter.OP_EQUAL) });
			return;
		}

		List<ClusterHierarchy> hierarchyList = clusterService.getClusterHierarcheyList(entity);
		String lowermostchild = null;

		if (hierarchyList.size() == 1) {
			lowermostchild = hierarchyList.get(0).getClusterType();
		} else if (hierarchyList.size() > 1) {
			lowermostchild = hierarchyList.get(0).getClusterType();
		}

		if (lowermostchild != null) {
			cluster.setFilters(new Filter[] { new Filter("Entity", entity, Filter.OP_EQUAL),
					new Filter("ClusterType", lowermostchild, Filter.OP_EQUAL) });
		}
	}

	private void doSetBranchFilter(Row row) {
		Combobox entity = (Combobox) getComponent(row, 3);
		ExtendedCombobox cluster = (ExtendedCombobox) getComponent(row, 5);
		ExtendedCombobox branches = (ExtendedCombobox) getComponent(row, 7);

		if (entity == null || branches == null || cluster == null) {
			return;
		}

		branches.setModuleName("Branch");
		branches.setValueColumn("BranchCode");
		branches.setValidateColumns(new String[] { "BranchCode" });

		String selectedEntity = entity.getSelectedItem().getValue();
		String selectedCluster = cluster.getValue();

		Object object = cluster.getObject();
		Cluster oCluster = null;
		if (object != null && object instanceof Cluster) {
			oCluster = (Cluster) object;
		}

		Filter[] filters = null;
		if (StringUtils.equals(selectedEntity, selectedCluster)) {
			filters = new Filter[1];
			filters[0] = new Filter("Entity", selectedEntity);
		} else if (oCluster != null) {
			filters = new Filter[1];
			filters[0] = new Filter("Entity", selectedEntity);

			branches.setWhereClause(" clusterId in (select id from clusters where Id =" + oCluster.getId() + ")");
		}

		branches.setFilters(filters);
	}

	private void appendEntities(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");

		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Entities:"));
		row.appendChild(labelCell);

		Cell accessCell = new Cell();
		Hbox hbox = new Hbox();
		hbox.setParent(accessCell);

		ExtendedCombobox entities = new ExtendedCombobox();

		StringBuilder value = new StringBuilder();
		for (String entity : division.getEntities().keySet()) {
			if (value.length() > 0) {
				value.append(",");
			}
			value.append(entity);
		}
		entities.setValue(value.toString());
		entities.setTooltiptext(value.toString());
		entities.setInputAllowed(false);
		entities.setParent(accessCell);
		entities.setParent(hbox);
		entities.setMultySelection(true);
		entities.setSelectedValues(division.getEntities());
		entities.setAttribute("data", division.getEntities());

		hbox.setParent(accessCell);
		fillEntities(division, entities);
		row.appendChild(accessCell);

	}

	private void appendEntity(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");
		List<Property> list = new ArrayList<>();
		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Entity:"));
		row.appendChild(labelCell);

		Cell entity = new Cell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		hbox.appendChild(space);

		hbox.setParent(entity);

		Combobox accessBox = new Combobox();
		accessBox.setValue(division.getEntity());

		accessBox.setParent(hbox);

		List<Entity> entityList = securityUserService.getEntityList(division.getEntity());

		for (Entity item : entityList) {
			list.add(new Property(item.getEntityCode(), item.getEntityDesc()));
		}
		fillList(accessBox, list, division.getEntity());
		row.appendChild(entity);
		accessBox.addForward("onChange", self, "onChangeEntity", row);

		if (division.getEntity() != null) {
			onChangeEntity(row);
		}
	}

	private void appendClusterType(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");
		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Cluster Type:"));
		row.appendChild(labelCell);

		Cell accessCell = new Cell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		hbox.appendChild(space);
		hbox.setParent(accessCell);

		Combobox clusterType = new Combobox();
		clusterType.setValue(division.getClusterType());
		clusterType.setParent(hbox);
		hbox.setParent(accessCell);
		row.setParent(divBranch_Rows);
		row.appendChild(accessCell);

		clusterType.addForward("onChange", self, "onChangeClusterType", row);
		onChangeClusterType(row);

	}

	public void onChangeClusterType(ForwardEvent event) {
		Row row = (Row) event.getData();
		onChangeClusterType(row);
	}

	private void onChangeClusterType(Row row) {
		Combobox clusterType = (Combobox) getComponent(row, 5);

		ExtendedCombobox clusters = (ExtendedCombobox) getComponent(row, 7);

		if (clusterType != null && clusters != null) {
			if (getComboboxValue(clusterType).equals(PennantConstants.List_Select)) {
				clusters.setButtonDisabled(true);
			} else {
				clusters.setButtonDisabled(false);
			}
			clusters.setSelectedValues(new HashMap<>());
			clusters.setValue("");
			clusters.setFilters(new Filter[] { new Filter("clustertype", clusterType.getSelectedItem().getValue()) });
		}
	}

	private void appendClusters(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");
		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Clusters:"));
		row.appendChild(labelCell);

		Cell accessCell = new Cell();
		Hbox hbox = new Hbox();
		hbox.setParent(accessCell);

		ExtendedCombobox clusters = new ExtendedCombobox();

		StringBuilder value = new StringBuilder();
		for (Entry<String, Object> cluster : division.getClusters().entrySet()) {
			String code = ((Cluster) cluster.getValue()).getCode();
			if (value.length() > 0) {
				value.append(",");
			}
			value.append(code);
		}

		clusters.setValue(value.toString());
		clusters.setTooltiptext(value.toString());
		clusters.setInputAllowed(false);
		clusters.setParent(hbox);
		clusters.setMandatoryStyle(true);
		clusters.setModuleName("Cluster");
		clusters.setValueColumn("Code");
		clusters.setValidateColumns(new String[] { "Code" });
		clusters.setValueType(DataType.STRING);
		clusters.addForward("onFulfill", self, "onChangeClusters", row);

		clusters.setMultySelection(true);
		clusters.setSelectedValues(division.getClusters());
		clusters.setAttribute("data", division.getClusters());

		row.appendChild(accessCell);
		if (division.getClusterCode() != null) {
			clusters.setAttribute("clusters", division.getClusterId());
			clusters.setValue(value.toString());
			onChangeClusters(row);
		} else {
			clusters.setValue("");
		}

		Combobox clusterType = (Combobox) getComponent(row, 5);
		if (clusterType.getValue().equals("")) {
			clusters.setButtonDisabled(true);
		} else {
			clusters.setButtonDisabled(false);
			clusters.setFilters(new Filter[] { new Filter("clustertype", clusterType.getValue()) });
		}
	}

	public void onChangeClusters(ForwardEvent event) {
		Row row = (Row) event.getData();
		onChangeClusters(row);
	}

	private void onChangeClusters(Row accessRow) {
		ExtendedCombobox clusters = (ExtendedCombobox) getComponent(accessRow, 7);

		if (clusters == null) {
			return;
		}

		Object dataObject = clusters.getSelectedValues();
		StringBuilder value = new StringBuilder();

		if (dataObject != null && dataObject instanceof Map) {
			Map<String, Object> cluster = (Map<String, Object>) dataObject;

			for (String entity : cluster.keySet()) {
				if (value.length() > 0) {
					value.append(",");
				}

				Cluster data = (Cluster) cluster.get(entity);

				value.append(data.getCode());
			}
		}

		clusters.setValue(value.toString());
		clusters.setAttribute("clusters", value);
	}

	private void appendParentCluster(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");

		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Cluster:"));
		row.appendChild(labelCell);

		Cell accessCell = new Cell();
		Hbox hbox = new Hbox();
		hbox.setParent(accessCell);

		ExtendedCombobox cluster = new ExtendedCombobox();
		row.appendChild(accessCell);

		cluster.setParent(hbox);
		cluster.setMandatoryStyle(true);
		cluster.addForward("onFulfill", self, "onChangeParentCluster", row);
		// cluster.setInputAllowed(false);

		Cluster cl = new Cluster();
		if (division.getParentCluster() != null) {
			cl.setId(division.getParentCluster());
			cl.setCode(division.getParentClusterCode());
			cl.setName(division.getParentClusterName());
			cluster.getLabel().setValue("...");
			cluster.getLabel().setTooltiptext(division.getParentClusterName());
			cluster.setObject(cl);
			onChangeParentCluster(row);
		}

	}

	private void appendBranches(Row row) {
		SecurityUserDivBranch division = (SecurityUserDivBranch) row.getAttribute("division");
		Cell labelCell = new Cell();
		labelCell.appendChild(new Label("Branches:"));
		row.appendChild(labelCell);

		Cell accessCell = new Cell();
		Hbox hbox = new Hbox();
		hbox.setParent(accessCell);

		ExtendedCombobox branch = new ExtendedCombobox();

		StringBuilder value = new StringBuilder();
		for (String branchCode : division.getBranches().keySet()) {
			if (value.length() > 0) {
				value.append(",");
			}
			value.append(branchCode);
		}

		branch.setMultySelection(true);
		branch.setTooltiptext(value.toString());
		branch.setModuleName("Branch");
		branch.setValueColumn("BranchCode");
		branch.setValidateColumns(new String[] { "BranchCode" });

		branch.setInputAllowed(false);
		branch.setSelectedValues(division.getBranches());
		branch.setAttribute("data", division.getBranches());

		branch.setParent(hbox);
		branch.setValue(value.toString());
		branch.setMaxlength(50);
		branch.setMandatoryStyle(true);

		row.appendChild(accessCell);
	}

	public void onChangeParentCluster(ForwardEvent event) {
		Row row = (Row) event.getData();
		onChangeParentCluster(row);
	}

	private void onChangeParentCluster(Row row) {
		ExtendedCombobox cluster = (ExtendedCombobox) getComponent(row, 5);

		Object object = null;
		if (cluster != null && cluster.getObject() != null) {
			object = cluster.getObject();
		}

		Cluster ocluster = null;
		if (object != null && object instanceof Cluster) {
			ocluster = (Cluster) object;
			cluster.setValue(ocluster.getCode());
			cluster.setDescription(ocluster.getName());
			cluster.getLabel().setValue("...");
			cluster.getLabel().setTooltiptext(ocluster.getName());
			cluster.setObject(ocluster);
		}

		ExtendedCombobox branches = (ExtendedCombobox) getComponent(row, 7);

		if (branches != null) {
			branches.setValue("", "");
			branches.setSelectedValues(new HashMap<>());
		}

		doSetBranchFilter(row);
	}

	private void fillEntities(SecurityUserDivBranch division, ExtendedCombobox entities) {
		logger.debug(Literal.ENTERING);
		entities.setMandatoryStyle(true);
		entities.setModuleName("Entity");
		entities.setValueColumn("EntityCode");
		entities.setDescColumn("EntityDesc");
		entities.setValidateColumns(new String[] { "EntityCode" });
		entities.setWhereClause(" entitycode in (select s.entitycode from smtdivisiondetail s" + " where divisioncode ="
				+ "'" + division.getUserDivision() + "'" + ") ");
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveClusterValidation(Component component) {
		if (component != null && component instanceof Combobox) {
			((Combobox) component).setConstraint("");
		} else if (component != null && component instanceof ExtendedCombobox) {
			((ExtendedCombobox) component).setConstraint("");
		}
	}

	private void doRemoveClusterValidation() {
		for (Component component : divBranch_Rows.getChildren()) {
			Row row = (Row) component;

			doRemoveClusterValidation(getComponent(row, 1));

			doRemoveClusterValidation(getComponent(row, 3));

			doRemoveClusterValidation(getComponent(row, 5));

			doRemoveClusterValidation(getComponent(row, 7));
		}
	}

	private void doClearClusters(Component component) {
		if (component != null && component instanceof Combobox) {
			((Combobox) component).setValue("");
		} else if (component != null && component instanceof ExtendedCombobox) {
			((ExtendedCombobox) component).setValue("");
		}
	}

	public void doClearClusters() {
		for (Component component : divBranch_Rows.getChildren()) {
			Row row = (Row) component;

			doClearClusters(getComponent(row, 1));

			doClearClusters(getComponent(row, 3));

			doClearClusters(getComponent(row, 5));

			doClearClusters(getComponent(row, 7));

		}
	}

	public void onReportingManagerItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxReportingManager.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ReportingManager areportingManager = (ReportingManager) item.getAttribute("data");

			final Map<String, Object> map = new HashMap<>();
			map.put("reportingManager", areportingManager);
			map.put("SecurityUserDialogCtrl", this);
			map.put("roleCode", getRole());
			map.put("securityUser", securityUser);

			try {
				Executions.createComponents(
						"/WEB-INF/pages/ApplicationMaster/UserRepotingManager/ReportingManagerDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
	}

	public void doFillReportingManagerDetails(List<ReportingManager> reportingManagerDetails) {
		logger.debug(Literal.ENTERING);
		this.listBoxReportingManager.getItems().clear();
		if (reportingManagerDetails != null) {
			for (ReportingManager rm : reportingManagerDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(StringUtils.trimToEmpty(rm.getBusinessVerticalCode()));
				lc.setParent(item);

				lc = new Listcell(StringUtils.trimToEmpty(rm.getProduct()));
				lc.setParent(item);

				lc = new Listcell(StringUtils.trimToEmpty(rm.getFinType()));
				lc.setParent(item);

				lc = new Listcell(StringUtils.trimToEmpty(rm.getBranch()));
				lc.setParent(item);

				lc = new Listcell(StringUtils.trimToEmpty(rm.getReportingToUserName()));
				lc.setParent(item);

				lc = new Listcell(rm.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(rm.getRecordType()));
				lc.setParent(item);

				rm.getId();

				item.setAttribute("data", rm);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onReportingManagerItemDoubleClicked");
				this.listBoxReportingManager.appendChild(item);

			}
			setReportingManagerDetailList(reportingManagerDetails);
		}

		logger.debug(Literal.LEAVING);
	}

	// For ReportingManagerList

	public void onClick$btnNewReportingManagerList(Event event) {

		ReportingManager areportingManager = new ReportingManager();
		areportingManager.setWorkflowId(0);

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("reportingManager", areportingManager);
		arg.put("roleCode", getRole());
		arg.put("newRecord", "true");
		arg.put("SecurityUserDialogCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/UserRepotingManager/ReportingManagerDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

	}

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

	public ReportingManagerService getReportingManagerService() {
		return reportingManagerService;
	}

	public void setReportingManagerService(ReportingManagerService reportingManagerService) {
		this.reportingManagerService = reportingManagerService;
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

	private UserSearch getUserSearch() {
		return externalUserSearch == null ? ldapUserSearch : externalUserSearch;
	}

	public ReportingManager getReportingManager() {
		return reportingManager;
	}

	public void setReportingManager(ReportingManager reportingManager) {
		this.reportingManager = reportingManager;
	}

	public List<ReportingManager> getReportingManagerDetailList() {
		return reportingManagerDetailList;
	}

	public void setReportingManagerDetailList(List<ReportingManager> reportingManagerDetailList) {
		this.reportingManagerDetailList = reportingManagerDetailList;
	}
}