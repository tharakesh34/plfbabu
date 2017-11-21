package com.pennanttech.niyogin.communication.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennanttech.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Email;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SendEmail;
import com.pennanttech.pff.external.service.NiyoginService;

public class SendEmailService extends NiyoginService implements SendEmail {
	private static final Logger logger = Logger.getLogger(SendEmailService.class);

	private String				serviceUrl;
	
	/**
	 * Method to send the individual email for the given list of toAddress.
	 * 
	 * @param toAddress
	 * @param subject
	 * @param body
	 * @return
	 */
	@Override
	public void sendEmail(List<String> toAddress, String subject, String body) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (toAddress != null && !toAddress.isEmpty()) {
			for (String emailId : toAddress) {
				send(emailId, subject, body);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to prepare the Email Request body and send's the Email.
	 * 
	 * @param emailId
	 * @param subject
	 * @param body
	 * @return
	 */
	private void send(String emailId, String subject, String body) {
		logger.debug(Literal.ENTERING);
		Email emailRequest = prepareRequest(emailId, subject, body);
		Email emailResponse = null;
		String serviceURL = "https://soadev.niyogin.in/gates/1.0/sweeps";
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceURL);
			emailResponse = (Email) client.postProcess(serviceURL, "sendEmail", emailRequest, Email.class);
			logger.info("Response : " + emailResponse.toString());
		} catch (Exception exception) {
			logger.error("Exception: ", exception);
			throw new InterfaceException("9999", exception.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to prepare the EmailRequest object.
	 * 
	 * @param toAddress
	 * @param subject
	 * @param body
	 * @return email
	 */
	private Email prepareRequest(String toAddress, String subject, String body) {
		logger.debug(Literal.ENTERING);
		Email email = new Email();
		email.setFrom("partner@niyogin.in");
		email.setTo(toAddress);
		email.setSubject(subject);
		email.setBody(body);
		logger.debug(Literal.LEAVING);
		return email;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}
