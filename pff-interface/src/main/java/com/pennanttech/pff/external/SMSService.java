package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.pennapps.core.InterfaceException;

public interface SMSService {
	public void sendSms(List<MailTemplate> smsList, String finReference) throws InterfaceException;

}
