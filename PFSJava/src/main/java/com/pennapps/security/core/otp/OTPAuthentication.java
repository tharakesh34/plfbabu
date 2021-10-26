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

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class OTPAuthentication {
	private static Logger logger = LogManager.getLogger(OTPAuthentication.class);

	private String fromat;
	private int length;
	private long validity;

	private OTPService otpService;
	private OTPDataAccess otpDataAccess;

	private static Template smsTemplate;
	private static Template emailTemplate;

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
		String emailID = message.getEmailID();
		String smsMessage = null;
		String emailMessage = null;

		if ("Y".equalsIgnoreCase(App.getProperty("two.factor.authentication.sms")) && mobileNo != null) {
			smsMessage = getSMSMessage(message.getOtp(), "SMS");
		}

		if ("Y".equalsIgnoreCase(App.getProperty("two.factor.authentication.email")) && emailID != null) {
			emailMessage = getSMSMessage(message.getOtp(), "EMAIL");
		}

		message.setSmsMessage(smsMessage);
		message.setEmailMessage(emailMessage);

		Notification notification = new Notification();
		notification.setMobileNumber(mobileNo);
		notification.setMessage(smsMessage);

		Thread smsThread = new Thread(new SMSThread(mobileNo, smsMessage));
		Thread emailThread = new Thread(new EmailThread(emailID, smsMessage));

		smsThread.start();

		emailThread.start();

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

	public String getSMSMessage(String otp, String otpType) {
		Map<String, String> dataMap = new HashMap<>();

		dataMap.put("OTP", otp);
		dataMap.put("OTP_VALIDITY", String.valueOf(validity));

		Template template = getTemplate(otpType);

		try {
			String otpMessage = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
			return otpMessage;
		} catch (IOException | TemplateException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private Template getTemplate(String otpType) {
		Template template = null;
		String otpTemplate = null;

		if ("SMS".equals(otpType)) {
			otpTemplate = "otp_login_sms.html";
			template = smsTemplate;
		} else {
			otpTemplate = "otp_login_email.html";
			template = emailTemplate;
		}

		if (template != null) {
			return emailTemplate;
		}

		String resourcePath = App.getResourcePath("Templates");
		FileTemplateLoader loader;
		File baseDir = null;
		try {
			baseDir = new File(resourcePath);
			loader = new FileTemplateLoader(baseDir);
		} catch (IOException e1) {
			throw new AppException(resourcePath + " directory not found");
		}

		File file = new File(baseDir + File.separator + otpTemplate);

		if (!file.exists()) {
			throw new AppException(otpTemplate + " file not found in " + baseDir + " directory.");
		}

		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		config.setTemplateLoader(loader);
		try {
			template = config.getTemplate(otpTemplate);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return template;
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

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setOtpDataAccess(OTPDataAccess otpDataAccess) {
		this.otpDataAccess = otpDataAccess;
	}

	private class SMSThread implements Runnable {
		private String mobileNo;
		private String smsMessage;

		public SMSThread(String mobileNo, String smsMessage) {
			this.mobileNo = mobileNo;
			this.smsMessage = smsMessage;
		}

		@Override
		public void run() {
			try {
				if (smsMessage != null) {
					otpService.sendSMS(mobileNo, smsMessage);
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}
	}

	private class EmailThread implements Runnable {
		private String emailID;
		private String emailMessage;

		public EmailThread(String emailID, String emailMessage) {
			this.emailID = emailID;
			this.emailMessage = emailMessage;
		}

		@Override
		public void run() {
			try {
				if (emailMessage != null) {
					otpService.sendEmail(emailID, emailMessage);
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}
	}

}
