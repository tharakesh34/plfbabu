package com.pennanttech.pff.external;

import com.pennanttech.pennapps.notification.email.model.EmailMessage;

public interface MailService {

	public void sendEmail(EmailMessage emailMessage);
}
