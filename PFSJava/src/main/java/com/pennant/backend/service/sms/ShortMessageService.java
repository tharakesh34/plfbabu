package com.pennant.backend.service.sms;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

public interface ShortMessageService {

	public void sendMessage(List<String> mobiles, String content);

	public void sendMessage(List<CustomerPhoneNumber> custPhoneNoList, List<String> smsList);
}
