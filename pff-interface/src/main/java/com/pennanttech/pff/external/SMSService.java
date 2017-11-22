package com.pennanttech.pff.external;

import java.util.List;

import com.pennanttech.pennapps.core.InterfaceException;

public interface SMSService {
	public void sendSms(List<String> mobiles, String content) throws InterfaceException;

}
