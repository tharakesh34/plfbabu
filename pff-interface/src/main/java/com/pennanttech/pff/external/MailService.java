package com.pennanttech.pff.external;

import java.util.List;

import com.pennanttech.pennapps.core.InterfaceException;

public interface MailService {
	
	public void sendEmail(List<String> toAddress, String subject, String body) throws InterfaceException;
}
	