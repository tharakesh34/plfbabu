package com.pennanttech.pff.external.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
	private static final Logger logger = Logger.getLogger(SMSGupshupServiceImpl.class);
	private final String encoder = Charset.forName("UTF-8").toString();
	@Autowired
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
			smsData.append(URLEncoder.encode(App.getProperty("gupshup.sms.password"), encoder));
			smsData.append("&msg=");
			smsData.append(URLEncoder.encode(notification.getMessage(), encoder));
			smsData.append("&send_to=");
			smsData.append(URLEncoder.encode(notification.getMobileNumber(), encoder));
			smsData.append("&v=1.1");
			smsData.append("&msg_type=TEXT");
			smsData.append("&auth_scheme=PLAIN");
			logger.info("BEFORE SMS : "+smsData.toString());
			URL url = new URL(App.getProperty("gupshup.sms.url") + smsData.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				buffer.append(line).append("\n");
			}

			String[] response = StringUtils.split(buffer.toString(), "|");

			if (buffer.toString().contains("error")) {
				doInterfaceLogging(notification.getKeyReference(), smsData.toString(), buffer.toString(), "999",
						response[2], reqSentOn, InterfaceConstants.STATUS_FAILED);
			} else {
				logger.info("SMS Response-"+notification.getKeyReference()+":"+buffer.toString());
				doInterfaceLogging(notification.getKeyReference(), smsData.toString(), buffer.toString(), null, null,
						reqSentOn, InterfaceConstants.STATUS_SUCCESS);
			}

			rd.close();
			conn.disconnect();

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			doInterfaceLogging(notification.getKeyReference(), smsData.toString(), null, null, e.getMessage(),
					reqSentOn, InterfaceConstants.STATUS_FAILED);
			throw new InterfaceException("9999", e.getMessage());
		}

		logger.debug(Literal.LEAVING);
		return hostReferece;
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
		iLogDetail.setServiceName("SMSService");
		iLogDetail.setEndPoint(App.getProperty("gupshup.sms.url"));
		iLogDetail.setRequest(requets);
		iLogDetail.setReqSentOn(reqSentOn);
		iLogDetail.setResponse(response);
		iLogDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
		iLogDetail.setStatus(status);
		iLogDetail.setErrorCode(errorCode);
		iLogDetail.setErrorDesc(StringUtils.left(errorDesc, 190));
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
