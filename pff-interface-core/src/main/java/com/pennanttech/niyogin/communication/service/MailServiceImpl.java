package com.pennanttech.niyogin.communication.service;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.niyogin.clients.JSONClient;
import com.pennanttech.niyogin.communication.model.Email;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.email.model.EmailMessage;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.MailService;
import com.pennanttech.pff.external.service.NiyoginService;

public class MailServiceImpl extends NiyoginService implements MailService {
	private static final Logger	logger	= Logger.getLogger(MailServiceImpl.class);

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
	public void sendEmail(EmailMessage emailMessage) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (emailMessage != null) {
			send(emailMessage);
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
	private void send(EmailMessage emailMessage) {
		logger.debug(Literal.ENTERING);

		String[] emailId = emailMessage.getAddressesList().toArray(new String[emailMessage.getAddressesList().size()]);
		String subject = emailMessage.getSubject();
		String body = new String(emailMessage.getContent());

		if (emailId == null || StringUtils.isEmpty(body)) {
			return;
		}

		//FIXME
		for (String string : emailId) {
			//String.join(",", emailId)
			Email emailRequest = prepareRequest(string, subject, body);
			// logging fields Data
			String errorCode = null;
			String errorDesc = null;
			String reuestString = null;
			String jsonResponse = null;
			Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());
			try {
				logger.debug("ServiceURL : " + serviceUrl);
				reuestString = client.getRequestString(emailRequest);
				jsonResponse = client.post(serviceUrl, reuestString);
				doInterfaceLogging(emailMessage.getKeyReference(), reuestString, jsonResponse, errorCode, errorDesc,
						reqSentOn);
			} catch (Exception e) {
				logger.error("Exception: ", e);
				errorDesc = getWriteException(e);
				errorDesc = getTrimmedMessage(errorDesc);
				doExceptioLogging(emailMessage.getKeyReference(), reuestString, jsonResponse, errorDesc, reqSentOn);
				throw new InterfaceException("9999", e.getMessage());
			}
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
		email.setFrom(App.getLabel("EMAIL_ADDRESS_FROM"));
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
	 * Method for prepare Success logging
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 * @param reqSentOn 
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_SUCCESS);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for failure logging.
	 * 
	 * @param reference
	 * @param requets
	 * @param response
	 * @param errorCode
	 * @param errorDesc
	 * @param reqSentOn 
	 */
	private void doExceptioLogging(String reference, String requets, String response, String errorDesc,
			Timestamp reqSentOn) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		String[] values = serviceUrl.split("/");
		iLogDetail.setServiceName(values[values.length - 1]);
		iLogDetail.setEndPoint(serviceUrl);
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(InterfaceConstants.STATUS_FAILED);
		iLogDetail.setErrorCode(InterfaceConstants.ERROR_CODE);
		iLogDetail.setErrorDesc(errorDesc);

		logInterfaceDetails(iLogDetail);
		logger.debug(Literal.LEAVING);
	}
}
