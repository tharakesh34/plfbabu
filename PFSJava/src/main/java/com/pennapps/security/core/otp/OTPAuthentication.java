package com.pennapps.security.core.otp;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.sms.SmsNotificationService;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class OTPAuthentication {
	private static Logger logger = LogManager.getLogger(OTPAuthentication.class);

	private String fromat;
	private int length;
	private long validity;
	private SmsNotificationService smsNotificationService;
	private OTPDataAccess otpDataAccess;
	private static Template smsTemplate;

	public String generateOTP() {
		StringBuilder builder = new StringBuilder();
		String otp = null;
		int count = length;
		try {
			while (count-- != 0) {
				int character = (int) (Math.random() * fromat.length());
				builder.append(fromat.charAt(character));
			}
			otp = builder.toString();

		} finally {
			builder = null;
		}

		return otp;
	}

	public void saveOTP(OTPMessage message) {
		otpDataAccess.saveOTP(message);
	}

	public void update(long id, Date sentOn) {
		otpDataAccess.update(id, sentOn);
	}

	public void update(long id, int status) {
		otpDataAccess.update(id, status);
	}

	public void sendOTP(OTPMessage message) {
		String mobileNo = message.getMobileNo();
		String smsMessage = getSMSMessage(message.getOtp());

		smsMessage = "Hi!";

		message.setSmsMessage(smsMessage);

		Notification notification = new Notification();
		notification.setMobileNumber(mobileNo);
		notification.setMessage(smsMessage);

		try {
			smsNotificationService.sendNotification(notification);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public OTPStatus verifyOTP(OTPModule module, String otp, Date receivedOn, String sessionID) {
		OTPMessage message = otpDataAccess.getOTPMessage(module, otp, sessionID);

		if (message == null) {
			return OTPStatus.INVALID;
		}

		return verifyOTP(receivedOn, message);

	}

	public OTPStatus verifyOTP(OTPModule module, String otp, Date receivedOn) {
		OTPMessage message = otpDataAccess.getOTPMessage(module, otp);

		if (message == null) {
			return OTPStatus.INVALID;
		}

		return verifyOTP(receivedOn, message);
	}

	private OTPStatus verifyOTP(Date receivedOn, OTPMessage message) {
		Date sentOn = message.getSentOn();

		long milliseconds = receivedOn.getTime() - sentOn.getTime();

		long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);

		OTPStatus otpStatus = null;

		if (minutes <= validity) {
			otpStatus = OTPStatus.VERIFIED;
		} else {
			otpStatus = OTPStatus.EXPIRED;
		}

		message.setStatus(otpStatus.getKey());
		message.setReceivedOn(receivedOn);
		boolean verified = otpDataAccess.update(message);

		if (OTPStatus.VERIFIED == otpStatus && verified) {
			return otpStatus;
		} else if (verified) {
			return otpStatus;
		} else {
			return OTPStatus.INVALID;
		}
	}

	public String getSMSMessage(String otp) {
		Map<String, String> dataMap = new HashMap<>();

		dataMap.put("OTP", otp);
		dataMap.put("OTP_VALIDITY", String.valueOf(validity));

		if (smsTemplate == null) {
			String template = "otp_login_sms.html";
			String resourcePath = App.getResourcePath("Templates");
			FileTemplateLoader loader;
			File baseDir = null;
			try {
				baseDir = new File(resourcePath);
				loader = new FileTemplateLoader(baseDir);
			} catch (IOException e1) {
				throw new AppException(resourcePath + " directory not found");
			}

			File file = new File(baseDir + File.separator + template);

			if (!file.exists()) {
				throw new AppException(template + " file not found in " + baseDir + " directory.");
			}

			Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			config.setTemplateLoader(loader);
			try {
				smsTemplate = config.getTemplate(template);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		try {
			String otpMessage = FreeMarkerTemplateUtils.processTemplateIntoString(smsTemplate, dataMap);
			return otpMessage;
		} catch (IOException | TemplateException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	public void setFromat(String fromat) {
		this.fromat = fromat;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setValidity(long validity) {
		this.validity = validity;
	}

	public void setSmsNotificationService(SmsNotificationService smsNotificationService) {
		this.smsNotificationService = smsNotificationService;
	}

	public void setOtpDataAccess(OTPDataAccess otpDataAccess) {
		this.otpDataAccess = otpDataAccess;
	}

}
