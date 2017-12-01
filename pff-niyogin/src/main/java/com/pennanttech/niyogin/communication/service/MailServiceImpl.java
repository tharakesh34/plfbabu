package com.pennanttech.niyogin.communication.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Email;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MailService;
import com.pennanttech.pff.external.service.NiyoginService;

public class MailServiceImpl extends NiyoginService implements MailService {
	private static final Logger	logger	= Logger.getLogger(MailServiceImpl.class);

	private String				serviceUrl;

	/**
	 * Method to send the email for the given list of toAddress.
	 * 
	 * @param toAddress
	 * @param templates
	 * @return
	 */
	@Override
	public void sendEmail(List<String> toAddress, List<MailTemplate> templates) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		
		if (toAddress != null && !toAddress.isEmpty() && templates != null && !templates.isEmpty()) {
			for (String emailId : toAddress) {
				for(MailTemplate template:templates) {
					send(emailId, template.getEmailSubject(), template.getLovDescFormattedContent());
				}
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
		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			String response=client.post(serviceUrl, emailRequest);
			logger.info("Response : " + response.toString());
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
		email.setReturnCode(null);
		email.setReturnText(null);
		logger.debug(Literal.LEAVING);
		return email;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
}
