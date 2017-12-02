package com.pennanttech.niyogin.communication.service;

import java.io.PrintWriter;
import java.io.StringWriter;
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

	private String				status			= "SUCCESS";
	private String				errorCode		= null;
	private String				errorDesc		= null;
	private String				jsonResponse	= null;
	private Timestamp			reqSentOn		= null;

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
				for (MailTemplate template : templates) {
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
		// logging fields Data
		reqSentOn = new Timestamp(System.currentTimeMillis());
		try {
			logger.debug("ServiceURL : " + serviceUrl);
			jsonResponse = client.post(serviceUrl, emailRequest);
			logger.info("Response : " + jsonResponse.toString());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			status = "FAILED";
			errorCode = "9999";
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			errorDesc = writer.toString();
			doInterfaceLogging(emailRequest, "MailId: "+emailId+":"+"Subject :"+subject);
			throw new InterfaceException("9999", e.getMessage());
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
