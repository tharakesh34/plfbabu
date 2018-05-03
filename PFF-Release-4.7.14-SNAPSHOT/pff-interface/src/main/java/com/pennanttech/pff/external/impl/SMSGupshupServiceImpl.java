package com.pennanttech.pff.external.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.SMSService;

public class SMSGupshupServiceImpl implements SMSService {
	private static final Logger logger = Logger.getLogger(SMSGupshupServiceImpl.class);
	private String 				userID;
	private String 				password;
	private String				serviceUrl=null;
	private final String 		encoder="UTF-8";
	
	
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
	private void send(String mobileNo, String messageContent, String referene) {
		logger.debug(Literal.ENTERING);
		StringBuffer smsData= new StringBuffer();
		
		try {

			smsData.append("method=sendMessage");
			smsData.append("&userid=");
			smsData.append(getUserID());
			smsData.append("&password=");
			smsData.append(URLEncoder.encode(getPassword(), encoder));
			smsData.append("&msg=");
			smsData.append(URLEncoder.encode(messageContent, encoder));
			smsData.append("&send_to=");
			smsData.append(URLEncoder.encode(mobileNo, encoder));
			smsData.append("&v=1.1");
			smsData.append("&msg_type=TEXT");
			smsData.append("&auth_scheme=PLAIN");
			// FIXME Request logging
			
			URL url = new URL(getServiceUrl() + smsData.toString());
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
			// FIXME Response logging
			rd.close();
			conn.disconnect();

		} catch (Exception e) {

			// FIXME Exception logging
			// FIXME Exception Handling
		}

		
		logger.debug(Literal.LEAVING);
	}

	
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

}
