package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.core.InterfaceException;

public interface SMSService {
	public void sendSms(List<CustomerPhoneNumber> custPhoneNoList, List<String> smsContent) throws InterfaceException;

}
