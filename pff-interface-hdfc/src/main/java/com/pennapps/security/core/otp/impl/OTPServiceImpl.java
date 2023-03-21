package com.pennapps.security.core.otp.impl;

import java.nio.charset.Charset;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.email.EmailEngineThread;
import com.pennanttech.pennapps.notification.sms.SmsGateway;
import com.pennapps.security.core.otp.OTPService;

public class OTPServiceImpl implements OTPService {
	private SmsGateway smsGateway;
	private EmailEngineThread otpEmailEngine;

	@Override
	public String sendSMS(String mobileNumber, String smsMessages) {
		Notification notification = new Notification();
		notification.setMessage(smsMessages);
		notification.setMobileNumber(mobileNumber);

		return smsGateway.sendNotification(notification);
	}

	@Override
	public String sendEmail(String emailID, String emailMessages) {
		Notification notification = new Notification();
		notification.setMessage(emailMessages);
		notification.setContent(emailMessages.getBytes(Charset.forName("UTF-8")));

		notification.setSubject(App.getProperty("two.factor.authentication.email.subject"));

		return otpEmailEngine.sendEmail(notification, new String[] { emailID }, new String[] {}, new String[] {});
	}

	public void setSmsGateway(SmsGateway smsGateway) {
		this.smsGateway = smsGateway;
	}

	public void setOtpEmailEngine(EmailEngineThread otpEmailEngine) {
		this.otpEmailEngine = otpEmailEngine;
	}

}
