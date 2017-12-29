package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.mail.MailTemplate;

public interface MailService {
	
	public void sendEmail(List<MailTemplate> templates);
}
	