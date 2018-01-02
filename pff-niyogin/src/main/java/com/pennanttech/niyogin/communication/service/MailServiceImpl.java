package com.pennanttech.niyogin.communication.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Email;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MailService;
import com.pennanttech.pff.external.service.NiyoginService;

public class MailServiceImpl extends NiyoginService implements MailService {
	private static final Logger	logger			= Logger.getLogger(MailServiceImpl.class);

	private JSONClient			client;
	private String				serviceUrl;

	/**
	 * Method to send the email for the given list of toAddress.
	 * 
	 * @param toAddress
	 * @param templates
	 * @return
	 */
	@Override
	public void sendEmail(List<MailTemplate> templates, String finReference) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (templates != null && !templates.isEmpty()) {
			for (MailTemplate template : templates) {
				send(template, finReference);
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
	private void send(MailTemplate template, String finReference) {
		logger.debug(Literal.ENTERING);
		
		String[] emailId = template.getLovDescMailId();
		String subject = template.getEmailSubject();
		String body = template.getLovDescFormattedContent();
		
		Email emailRequest = prepareRequest(String.join(",", emailId), subject, body);
		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		reference = finReference;
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, emailRequest);
			logger.info("Response : " + jsonResponse.toString());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			doLogError(e, serviceUrl, emailRequest);
			throw new InterfaceException("9999", e.getMessage());
		}
		// success case logging
		doInterfaceLogging(emailRequest, reference);
				
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

	public void setClient(JSONClient client) {
		this.client = client;
	}

	/**
	 * Method for prepare data and logging
	 * 
	 * @param emailRequest
	 * @param reference
	 */
	private void doInterfaceLogging(Email emailRequest, String reference) {
		InterfaceLogDetail interfaceLogDetail = prepareLoggingData(serviceUrl, emailRequest, jsonResponse, reqSentOn,
				status, errorCode, errorDesc, reference);
		logInterfaceDetails(interfaceLogDetail);
	}
}
