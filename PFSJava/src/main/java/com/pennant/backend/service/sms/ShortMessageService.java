package com.pennant.backend.service.sms;

import java.util.List;

public interface ShortMessageService {

	public void sendMessage(List<String> mobiles, String content);
}
