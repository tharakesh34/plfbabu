package com.pennant.webui.login;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuser.changepassword.ChangePasswordModel;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.AESCipherUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.security.core.otp.OTPAuthentication;
import com.pennapps.security.core.otp.OTPMessage;
import com.pennapps.security.core.otp.OTPModule;
import com.pennapps.security.core.otp.OTPStatus;

public class ForgotPasswordDialogCtrl extends GenericForwardComposer<Component> implements Serializable {
	private static final long serialVersionUID = -71422545405325060L;
	private static final Logger logger = LogManager.getLogger(ForgotPasswordDialogCtrl.class);

	protected Window windowForgotPassword;
	protected Borderlayout forgetLayout;
	protected Textbox userName;
	protected Textbox password;
	protected Textbox password1;
	protected Textbox newPassword;
	protected Textbox newPassword1;
	protected Textbox retypeNewPassword;
	protected Textbox retypeNewPassword1;
	protected Textbox otp;
	protected Button btnValidateOtp;
	protected Button btnSendOtp;
	protected Button btnSave;
	protected Button btnResendOtp;
	protected Div divPwdStatusMeter;
	protected Label labelPwdStatus;
	protected Textbox txtbox_randomKey;

	private SecurityUser securityUser;

	private transient ChangePasswordModel changePassWordModel;
	private transient SecurityUserService securityUserService;
	private transient OTPAuthentication otpAuthentication;

	private boolean regenerate = false;

	private Map<String, List<ErrorDetail>> overideMap = new HashMap<>();

	public ForgotPasswordDialogCtrl() {
		super();
	}

	public void onCreate$windowForgotPassword(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		try {

			this.userName.setValue(Executions.getCurrent().getParameter("userName"));

			doSetFieldProperties();

			windowForgotPassword.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.windowForgotPassword.onClose();
		}

		logger.debug(Literal.ENTERING.concat(event.getName()));
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		String msg = Labels.getLabel("message_Data_Modified_Close_Data_YesNo");

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				windowForgotPassword.onClose();
				Executions.deactivate(desktop);
				Executions.getCurrent().getSession().invalidate();

				Executions.sendRedirect("/loginDialog.zul");
			}
		});

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void onClickBtnSave(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		doValidations();
		doSave();

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	private void doSave() {
		logger.debug(Literal.ENTERING);

		securityUser.setUsrPwd(this.newPassword.getValue());
		securityUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		securityUser.setLastMntBy(securityUser.getUsrID());
		securityUser.setVersion(securityUser.getVersion() + 1);
		securityUser.setUserDetails(securityUser.getUserDetails());

		try {
			String usrPwd = AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue());

			securityUser.setUsrPwd(((PasswordEncoder) SpringUtil.getBean("passwordEncoder")).encode(usrPwd));

			int expDays = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_EXPIRY_DAYS);

			securityUser.setPwdExpDt(DateUtil.addDays(new Date(System.currentTimeMillis()), expDays));

			securityUserService.changePassword(getAuditHeader(securityUser, PennantConstants.TRAN_UPD));

			MessageUtil.showMessage("Password Updated successfully.");

			Executions.deactivate(desktop);
			Executions.getCurrent().getSession().invalidate();
			Executions.sendRedirect("/csrfLogout.zul");

		} catch (DataAccessException error) {
			showMessage(error);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClickBtnResendOtp(Event event) {
		regenerate = true;
		doresetFields();
		Events.postEvent("onClick", this.btnSendOtp, event);
	}

	public void onClickBtnSendOtp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		String randomKey = "";
		try {
			randomKey = (String) Sessions.getCurrent().getAttribute("SATTR_RANDOM_KEY");
		} catch (Exception ex) {
			logger.warn("Unable to get session attribute 'SATTR_RANDOM_KEY':", ex);
		}

		txtbox_randomKey.setValue(randomKey);

		this.userName.clearErrorMessage();
		String usrName = this.userName.getValue();

		if (StringUtils.isBlank(usrName)) {
			this.userName.setErrorMessage("UserName is Mandatory.");
			return;
		}

		SecurityUser userLogin = securityUserService.getSecurityUserByLogin(usrName);

		if (userLogin == null) {
			this.userName.setErrorMessage("Enter a Valid User Name.");
			return;
		}

		setSecurityUser(userLogin);

		this.securityUser.setUserDetails(prepareLoggedInUser(userLogin));

		this.securityUser.setBefImage(securityUser);

		OTPMessage message = new OTPMessage();
		message.setModule(OTPModule.RE.getKey());
		message.setOtp(otpAuthentication.generateOTP());
		message.setMobileNo(userLogin.getUsrMobile());
		message.setEmailID(userLogin.getUsrEmail());
		message.setUserName(userLogin.getUsrLogin());

		if (regenerate && SysParamUtil.isAllowed(SMTParameterConstants.OTP_SENDTO_MAIL)) {
			regenerate = false;
			otpAuthentication.update(message.getId(), OTPStatus.RE_SEND.getKey());
		}

		try {
			otpAuthentication.saveOTP(message);

			message.setSentOn(DateUtil.getSysDate());
			otpAuthentication.update(message.getId(), message.getSentOn());

			message.setSendEmail(App.getBooleanProperty("authentication.login.password.reset.otp.email"));
			message.setSendSMS(App.getBooleanProperty("authentication.login.password.reset.otp.sms"));
			message.setTemplateCode("RESPWD_OTP");

			otpAuthentication.sendOTP(message);

			this.btnSendOtp.setDisabled(true);
			this.btnResendOtp.setDisabled(false);
			this.btnValidateOtp.setDisabled(false);
			this.otp.setReadonly(false);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	private LoggedInUser prepareLoggedInUser(SecurityUser userLogin) {
		LoggedInUser log = new LoggedInUser();

		log.setLoginUsrID(userLogin.getUsrID());
		log.setUsrLanguage(userLogin.getUsrLanguage());
		log.setBranchCode(userLogin.getUsrBranchCode());
		log.setDepartmentCode(userLogin.getUsrDeptCode());

		return log;
	}

	public void onClickBtnValidateOtp(Event event) {
		logger.debug(Literal.ENTERING.concat(event.getName()));

		Date receivedOn = DateUtil.getSysDate();
		this.otp.clearErrorMessage();

		if (StringUtils.isBlank(this.otp.getValue())) {
			this.otp.setErrorMessage("Enter OTP number.");
			return;
		}

		switch (otpAuthentication.verifyOTP(OTPModule.RE, this.otp.getValue(), receivedOn)) {
		case VERIFIED:
			this.newPassword.setReadonly(false);
			this.retypeNewPassword.setReadonly(false);
			this.otp.setReadonly(true);
			this.btnValidateOtp.setDisabled(true);
			this.btnResendOtp.setDisabled(true);
			break;
		case INVALID:
			this.otp.setReadonly(false);
			this.newPassword.setReadonly(true);
			this.retypeNewPassword.setReadonly(true);
			this.btnValidateOtp.setDisabled(false);
			this.otp.setErrorMessage("Enter Valid OTP number.");
			break;
		case EXPIRED:
			this.otp.setErrorMessage("Enterd OTP is expired, Please generate a new OTP and try again.");
			break;
		default:
			break;
		}

		logger.debug(Literal.LEAVING.concat(event.getName()));
	}

	public void showPasswordStatusMeter(int pwdstatusCode) {
		switch (pwdstatusCode) {
		case 1:
			this.divPwdStatusMeter.setStyle("background-color:red");
			this.divPwdStatusMeter.setWidth("100px");
			this.labelPwdStatus.setStyle("color:red");
			this.labelPwdStatus.setValue(Labels.getLabel("label_PwdStatus_Bad.value"));
			break;
		case 2:
			this.divPwdStatusMeter.setStyle("background-color:tan");
			this.divPwdStatusMeter.setWidth("150px");
			this.labelPwdStatus.setStyle("color:tan");
			this.labelPwdStatus.setValue(Labels.getLabel("label_PwdStatus_Weak.value"));
			break;
		case 3:
			this.divPwdStatusMeter.setStyle("background-color:orange");
			this.divPwdStatusMeter.setWidth("180px");
			this.labelPwdStatus.setStyle("color:orange");
			this.labelPwdStatus.setValue(Labels.getLabel("label_PwdStatus_Good.value"));
			break;
		case 4:
			this.divPwdStatusMeter.setStyle("background-color:green");
			this.divPwdStatusMeter.setWidth("200px");
			this.labelPwdStatus.setStyle("color:green");
			this.labelPwdStatus.setValue(Labels.getLabel("label_PwdStatus_Strong.value"));
			break;
		default:
			this.divPwdStatusMeter.setStyle("background-color:white");
			this.labelPwdStatus.setValue("");
			break;
		}
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		int pwdMaxLenght = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_PWD_MAX_LEN);

		this.userName.setReadonly(true);
		this.password.setMaxlength(pwdMaxLenght);

		this.newPassword.addEventListener("onChanging", event -> {
			String pwd = ((org.zkoss.zk.ui.event.InputEvent) event).getValue();

			showPasswordStatusMeter(getPwdStatusCode(StringUtils.trimToEmpty(pwd)));
		});

		this.newPassword.setMaxlength(pwdMaxLenght);
		this.retypeNewPassword.setMaxlength(pwdMaxLenght);
		this.newPassword.setReadonly(true);
		this.retypeNewPassword.setReadonly(true);
		this.btnValidateOtp.setDisabled(true);
		this.btnResendOtp.setDisabled(true);
		this.otp.setReadonly(true);

		this.btnValidateOtp.addForward(Events.ON_CLICK, this.windowForgotPassword, "onClickBtnValidateOtp");
		this.btnSendOtp.addForward(Events.ON_CLICK, this.windowForgotPassword, "onClickBtnSendOtp");
		this.btnSave.addForward(Events.ON_CLICK, this.windowForgotPassword, "onClickBtnSave");
		this.btnResendOtp.addForward(Events.ON_CLICK, this.windowForgotPassword, "onClickBtnResendOtp");

		logger.debug(Literal.LEAVING);
	}

	private int getPwdStatusCode(String password) {
		int splCharCount = password.length() - getCharacterCount(password);

		int pwdMinLenght = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_PWD_MIN_LEN);
		int specialCharCount = SysParamUtil.getValueAsInt(SMTParameterConstants.USR_PWD_SPECIAL_CHAR_COUNT);

		if (changePassWordModel.checkPasswordCriteria(securityUser.getUsrLogin(), password)) {
			return 1;
		}

		int pwdLength = password.length();

		if (pwdLength < pwdMinLenght) {
			return 2;
		}

		if (splCharCount < specialCharCount) {
			return 3;
		}

		return 4;
	}

	private int getCharacterCount(String pwd) {
		int splCharCount = 0;

		for (int i = 0; i < pwd.length(); i++) {
			if (Character.isLetterOrDigit(pwd.charAt(i))) {
				splCharCount++;
			}
		}

		return splCharCount;
	}

	public void doresetFields() {
		logger.debug(Literal.ENTERING);

		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		this.divPwdStatusMeter.setStyle("background-color:white");
		this.labelPwdStatus.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void doValidations() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		boolean newPwdExist = StringUtils.isNotBlank(this.newPassword1.getValue());
		boolean retypePwdIsExist = StringUtils.isNotBlank(this.retypeNewPassword1.getValue());

		if (!newPwdExist) {
			wve.add(new WrongValueException(this.newPassword,
					Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("lable_newPassword") })));
		}

		if (!retypePwdIsExist) {
			wve.add(new WrongValueException(this.retypeNewPassword,
					Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("lable_RetypePassword") })));
		}

		if (newPwdExist && (changePassWordModel.checkPasswordCriteria(securityUser.getUsrLogin(), StringUtils
				.trimToEmpty(AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue()))))) {
			wve.add(new WrongValueException(this.newPassword, Labels.getLabel("label_Invalid_Password")));
		}

		if (newPwdExist && retypePwdIsExist
				&& !this.newPassword1.getValue().equals(this.retypeNewPassword1.getValue())) {
			wve.add(new WrongValueException(this.retypeNewPassword, Labels.getLabel("FIELD_NOT_MATCHED", new String[] {
					Labels.getLabel("label_NewPassword.value"), Labels.getLabel("label_RetypePassword.value") })));
		}

		if (newPwdExist && changePassWordModel.checkWithPreviousPasswords(securityUser,
				AESCipherUtil.decrypt(this.newPassword1.getValue(), txtbox_randomKey.getValue()))) {
			wve.add(new WrongValueException(this.newPassword, Labels.getLabel("label_Oldpwd_Newpwd_Same",
					new String[] { SysParamUtil.getValueAsString("USR_MAX_PRE_PWDS_CHECK") })));
		}

		if (CollectionUtils.isNotEmpty(wve)) {
			doresetFields();

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void showMessage(Exception error) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail("", error.getMessage(), null));
			ErrorControl.showErrorControl(this.windowForgotPassword, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}

		logger.debug(Literal.LEAVING);
	}

	private AuditHeader getAuditHeader(SecurityUser aSecurityUser, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityUser.getBefImage(), aSecurityUser);
		return new AuditHeader(String.valueOf(aSecurityUser.getUsrID()), null, null, null, auditDetail,
				aSecurityUser.getUserDetails(), overideMap);
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	@Autowired
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	@Autowired
	public void setOtpAuthentication(OTPAuthentication otpAuthentication) {
		this.otpAuthentication = otpAuthentication;
	}

	@Autowired
	public void setChangePassWordModel(ChangePasswordModel changePassWordModel) {
		this.changePassWordModel = changePassWordModel;
	}
}
