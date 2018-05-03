package com.pennant.backend.service.sms;

import java.util.List;

import com.pennant.backend.model.mail.MailTemplate;

public interface ShortMessageService {

	void sendMessage(List<MailTemplate> smsList, String finReference);
}
