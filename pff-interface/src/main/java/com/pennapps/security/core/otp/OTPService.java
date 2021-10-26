package com.pennapps.security.core.otp;

public interface OTPService {
	public String sendSMS(String mobileNumber, String smsMessages);

	public String sendEmail(String emailID, String emailMessages);
}
