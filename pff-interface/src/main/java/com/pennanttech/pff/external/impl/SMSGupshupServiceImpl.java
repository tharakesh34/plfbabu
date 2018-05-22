package com.pennanttech.pff.external.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.SMSService;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class SMSGupshupServiceImpl implements SMSService {
	private static final Logger logger = Logger.getLogger(SMSGupshupServiceImpl.class);

	private final String 		encoder="UTF-8";
	private InterfaceLoggingDAO interfaceLoggingDAO;
	
	@Override
	public void sendSms(List<MailTemplate> smsList, String referene) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		if (smsList != null && !smsList.isEmpty()) {
			for (MailTemplate mailTemplate : smsList) {
				List<String> listnumbers = mailTemplate.getLovDescMobileNumbers();
				if (listnumbers != null && !listnumbers.isEmpty()) {
					for (String string : listnumbers) {
						send(string, mailTemplate.getLovDescSMSContent(), referene);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for send the SMS.
	 * 
	 * @param mobileNo
	 * @param messageContent
	 * @param referene
	 * 
	 */
	private void send(String mobileNo, String messageContent, String reference) {
		logger.debug(Literal.ENTERING);
		StringBuffer smsData= new StringBuffer();
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());

		try {

			smsData.append("method=sendMessage");
			smsData.append("&userid=");
			smsData.append(App.getProperty("gupshup.sms.userid"));
			smsData.append("&password=");
			smsData.append(URLEncoder.encode(App.getProperty("gupshup.sms.password"), encoder));
			smsData.append("&msg=");
			smsData.append(URLEncoder.encode(messageContent, encoder));
			smsData.append("&send_to=");
			smsData.append(URLEncoder.encode(mobileNo, encoder));
			smsData.append("&v=1.1");
			smsData.append("&msg_type=TEXT");
			smsData.append("&auth_scheme=PLAIN");
			
			URL url = new URL(App.getProperty("gupshup.sms.url") + smsData.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			String[] response = StringUtils.split(buffer.toString(),"|");
			
			if("error".contains(buffer.toString())){
				doInterfaceLogging(reference, smsData.toString(), buffer.toString(), "999", response[2], reqSentOn,	InterfaceConstants.STATUS_FAILED);
			}else{
				doInterfaceLogging(reference, smsData.toString(), buffer.toString(), null, null, reqSentOn,InterfaceConstants.STATUS_SUCCESS);				
			}
			
			rd.close();
			conn.disconnect();

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			doInterfaceLogging(reference, smsData.toString(), null, null, e.getMessage(), reqSentOn,	InterfaceConstants.STATUS_FAILED);
			throw new InterfaceException("9999", e.getMessage());
		}

		
		logger.debug(Literal.LEAVING);
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
	 * @param status
	 */
	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn, String status) {
		logger.debug(Literal.ENTERING);
		InterfaceLogDetail iLogDetail = new InterfaceLogDetail();
		iLogDetail.setReference(reference);
		iLogDetail.setServiceName("CIBIL");
		iLogDetail.setEndPoint(App.getProperty("gupshup.sms.url"));
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);

		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(status);
		iLogDetail.setErrorCode(errorCode);
		if (errorDesc != null && errorDesc.length() > 200) {
			iLogDetail.setErrorDesc(errorDesc.substring(0, 190));
		}

		try {
			interfaceLoggingDAO.save(iLogDetail);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	public InterfaceLoggingDAO getInterfaceLoggingDAO() {
		return interfaceLoggingDAO;
	}

	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}

}
