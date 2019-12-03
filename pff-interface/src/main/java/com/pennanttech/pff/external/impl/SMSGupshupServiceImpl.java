package com.pennanttech.pff.external.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.notification.sms.SmsNotificationService;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class SMSGupshupServiceImpl implements SmsNotificationService {
	private static final Logger logger = LogManager.getLogger(SMSGupshupServiceImpl.class);
	private static final String ENCODER = Charset.forName("UTF-8").toString();

	private InterfaceLoggingDAO interfaceLoggingDAO;

	@Override
	public String sendNotification(Notification notification) {
		logger.debug(Literal.ENTERING);

		StringBuilder smsData = new StringBuilder();
		Timestamp reqSentOn = new Timestamp(System.currentTimeMillis());

		String hostReferece = null;
		try {

			smsData.append("method=sendMessage");
			smsData.append("&userid=");
			smsData.append(App.getProperty("gupshup.sms.userid"));
			smsData.append("&password=");
			smsData.append(URLEncoder.encode(App.getProperty("gupshup.sms.password"), ENCODER));
			smsData.append("&msg=");
			smsData.append(URLEncoder.encode(notification.getMessage(), ENCODER));
			smsData.append("&send_to=");
			smsData.append(URLEncoder.encode(notification.getMobileNumber(), ENCODER));
			smsData.append("&v=1.1");
			smsData.append("&msg_type=TEXT");
			smsData.append("&auth_scheme=PLAIN");

			logger.info("SMS Data {}", smsData.toString());

			URL url = new URL(App.getProperty("gupshup.sms.url") + smsData.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();

			try (Reader reader = new InputStreamReader(conn.getInputStream())) {
				try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					String line;
					StringBuilder buffer = new StringBuilder();
					while ((line = rd.readLine()) != null) {
						buffer.append(line).append("\n");
					}

					String responseData = buffer.toString();
					String[] response = StringUtils.split(responseData, "|");

					if (responseData.contains("error")) {
						doInterfaceLogging(notification.getKeyReference(), smsData.toString(), responseData, "999",
								response[2], reqSentOn, InterfaceConstants.STATUS_FAILED);
					} else {
						logger.info("Key Reference {}, SMS Data {}", notification.getKeyReference(), responseData);
						doInterfaceLogging(notification.getKeyReference(), smsData.toString(), responseData, null, null,
								reqSentOn, InterfaceConstants.STATUS_SUCCESS);
					}
				}
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			doInterfaceLogging(notification.getKeyReference(), smsData.toString(), null, null, e.getMessage(),
					reqSentOn, InterfaceConstants.STATUS_FAILED);
			throw new InterfaceException("9999", e.getMessage());
		}

		logger.debug(Literal.LEAVING);
		return hostReferece;
	}

	private void doInterfaceLogging(String reference, String requets, String response, String errorCode,
			String errorDesc, Timestamp reqSentOn, String status) {
		logger.debug(Literal.ENTERING);

		InterfaceLogDetail logDetails = new InterfaceLogDetail();
		logDetails.setReference(reference);
		logDetails.setServiceName("SMSService");
		logDetails.setEndPoint(App.getProperty("gupshup.sms.url"));
		logDetails.setRequest(requets);
		logDetails.setReqSentOn(reqSentOn);
		logDetails.setResponse(response);
		logDetails.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		logDetails.setStatus(status);
		logDetails.setErrorCode(errorCode);
		logDetails.setErrorDesc(StringUtils.left(errorDesc, 190));

		try {
			interfaceLoggingDAO.save(logDetails);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setInterfaceLoggingDAO(InterfaceLoggingDAO interfaceLoggingDAO) {
		this.interfaceLoggingDAO = interfaceLoggingDAO;
	}

}
