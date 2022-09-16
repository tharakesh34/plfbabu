package com.pennapps.security.core.otp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * This is the controller class for the /WebContent/pages/otp.zul file.
 */

public class OtpCtrl extends GenericForwardComposer<Component> {
	private static final long serialVersionUID = 9116635200454748911L;

	protected Window window_otp;
	protected Textbox enterOTP;
	protected Button validateOtp;
	protected Button resendOtp;
	protected Label validity;

	private transient Map<String, Object> arguments;
	private OTPMessage message = null;

	private OTPAuthentication otpAuthentication;
	private int length;
	private long otpvalidity = 10;
	private boolean regenerate = false;

	public OtpCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Map<?, ?> arg = Executions.getCurrent().getArg();

		if (arg != null) {
			arguments = new HashMap<>();
			for (Entry<?, ?> entry : arg.entrySet()) {
				arguments.put(entry.getKey().toString(), entry.getValue());
			}
		}
	}

	public void onCreate$window_otp(Event event) throws Exception {
		this.enterOTP.setMaxlength(length);

		generateOTP();

		this.enterOTP.setValue(message.getOtp());

		this.validity.setValue("OTP will be expired in " + otpvalidity + " minutes.");

		this.window_otp.doModal();
	}

	private void generateOTP() {
		LoggedInUser user = (LoggedInUser) arguments.get("user");
		String otp = otpAuthentication.generateOTP();

		if (regenerate) {
			regenerate = false;
			otpAuthentication.update(message.getId(), OTPStatus.RE_SEND.getKey());
		}

		message = new OTPMessage();
		message.setModule(OTPModule.LOGIN.getKey());
		message.setOtp(otp);
		message.setMobileNo(user.getMobileNo());
		message.setEmailID(user.getEmailId());
		message.setSessionID(user.getSessionId());
		message.setSendEmail("Y".equalsIgnoreCase(App.getProperty("two.factor.authentication.sms")));
		message.setSendSMS("Y".equalsIgnoreCase(App.getProperty("two.factor.authentication.email")));
		message.setTemplateCode("TWO_FACTOR_OTP");

		saveOTP(message);

		otpAuthentication.sendOTP(message);

		message.setSentOn(DateUtil.getSysDate());

		otpAuthentication.update(message.getId(), message.getSentOn());
	}

	private void saveOTP(OTPMessage message) {
		try {
			otpAuthentication.saveOTP(message);
		} catch (ConcurrencyException e) {
			saveOTP(message);
		}
	}

	public void onClick$validateOtp(Event event) {
		Date receivedOn = DateUtil.getSysDate();

		enterOTP.clearErrorMessage();

		String otp = enterOTP.getValue();

		if (StringUtils.isEmpty(otp)) {
			throw new WrongValueException(enterOTP, "OTP is mandatory.");
		}

		OTPStatus otpStatus = otpAuthentication.verifyOTP(OTPModule.LOGIN, otp, receivedOn, message.getSessionID());

		if (otpStatus == OTPStatus.VERIFIED) {
			this.window_otp.onClose();
		} else if (otpStatus == OTPStatus.INVALID) {
			throw new WrongValueException(enterOTP, "Enterd OTP invalid. Please try again.");
		} else if (otpStatus == OTPStatus.EXPIRED) {
			throw new WrongValueException(enterOTP, "Enterd OTP is expired. Please generate a new OTP and try again.");
		}

		org.zkoss.zk.ui.Session session = Executions.getCurrent().getDesktop().getSession();
		session.setAttribute(OTPStatus.VERIFIED.name(), true);

	}

	public void onClick$resendOtp(Event event) {
		regenerate = true;
		Events.postEvent("onCreate", this.window_otp, event);
	}

	public void setOtpAuthentication(OTPAuthentication otpAuthentication) {
		this.otpAuthentication = otpAuthentication;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setOtpValidity(long otpvalidity) {
		this.otpvalidity = otpvalidity;
	}

}
