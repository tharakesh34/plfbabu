package com.pennanttech.security;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APILogDetailDAO;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * A simple logging handler to log incoming request details.
 */

public class LogRequestInterceptor extends LoggingInInterceptor {
	static final Logger log = Logger.getLogger(LogRequestInterceptor.class);

	private APILogDetailDAO apiLogDetailDAO;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(PennantConstants.APIDateFormatter);

	public LogRequestInterceptor() {
		super(Phase.RECEIVE);
	}

	public void handleMessage(Message message) throws Fault {
		if (writer != null || log.isInfoEnabled()) {
			logging(message);
		}
	}

	protected void logging(Message message) throws Fault {
		final LoggingMessage buffer = new LoggingMessage("============= IN Message ==============\n", "1");

		Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
		if (responseCode != null) {
			buffer.getResponseCode().append(responseCode + ", ");
		}

		String encoding = (String) message.get(Message.ENCODING);
		if (encoding != null) {
			buffer.getEncoding().append(encoding + ", ");
		}

		String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
		if (httpMethod != null) {
			buffer.getHttpMethod().append(httpMethod + ", ");
		}

		String ct = (String) message.get(Message.CONTENT_TYPE);
		if (ct != null) {
			buffer.getContentType().append(ct + ", ");
		}

		Object headers = message.get(Message.PROTOCOL_HEADERS);
		if (headers != null) {
			buffer.getHeader().append(headers + ", ");
		}

		String uri = (String) message.get(Message.REQUEST_URL);
		if (uri != null) {
			buffer.getAddress().append(uri + ", ");
			String query = (String) message.get(Message.QUERY_STRING);
			if (query != null) {
				buffer.getAddress().append("?").append(query + ", ");
			}
		}

		try {
			try (InputStream is = message.getContent(InputStream.class)) {
				if (is != null) {
					try (CachedOutputStream bos = new CachedOutputStream()) {
						IOUtils.copy(is, bos);
						bos.flush();
						message.setContent(InputStream.class, bos.getInputStream());

						writePayload(buffer.getPayload(), bos, encoding, ct);

					}
				}
			}
		} catch (Exception e) {
			throw new Fault(e);
		}

		APILogDetail apiLogDetail = new APILogDetail();
		apiLogDetail.setRestClientId(Integer.parseInt(buffer.getId().replaceAll(", ", "")));
		String endPoint = StringUtils.trimToEmpty(String.valueOf(buffer.getAddress()).replaceAll(",", ""));
		String method = StringUtils.trimToEmpty(httpMethod);
		apiLogDetail.setServiceName(getServiceName(endPoint, method));
		apiLogDetail.setEndPoint(endPoint);
		apiLogDetail.setMethod(method);
		apiLogDetail.setReceivedOn(new Timestamp(System.currentTimeMillis()));
		apiLogDetail.setRequest(String.valueOf(buffer.getPayload()));

		// Set the IP address from the request object.
		ServletRequest request = (ServletRequest) message.get("HTTP.REQUEST");
		String IP_ADDRESS = request.getRemoteAddr();
		apiLogDetail.setClientIP(IP_ADDRESS);

		// Object headers = message.get(Message.PROTOCOL_HEADERS);
		@SuppressWarnings("unchecked") // read the HTTP header details
		Map<String, List<String>> headerMAP = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

		APIHeader header = new APIHeader();
		String authCredentials = null;
		for (String key : headerMAP.keySet()) {
			switch (key.toUpperCase()) {
			case APIHeader.API_AUTHORIZATION:
				authCredentials = headerMAP.get(key).toString();
				apiLogDetail.setAuthKey(authCredentials);
				break;
			// if service name is there in HTTP header, set it in APIHeader.
			case APIHeader.API_SERVICENAME:
				header.setServiceName(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
				break;
			// if service version is there in HTTP header, set it in
			// APIHeader.
			case APIHeader.API_SERVICEVERSION:
				header.setServiceVersion(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
				apiLogDetail.setServiceVersion(Integer.parseInt(header.getServiceVersion()));
				break;
			// if entityId is there in HTTP header, set it in APIHeader.
			case APIHeader.API_ENTITYID:
				header.setEntityId(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
				apiLogDetail.setEntityId(header.getEntityId());
				break;
			// if language is there in HTTP header, set it in APIHeader.
			case APIHeader.API_LANGUAGE:
				header.setLanguage(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
				apiLogDetail.setLanguage(header.getLanguage());
				break;
			// if messageId is there in HTTP header, set it in APIHeader.
			case APIHeader.API_MESSAGEID:
				header.setMessageId(headerMAP.get(key.toLowerCase()).toString().replace("[", "").replace("]", ""));
				apiLogDetail.setMessageId(header.getMessageId());
				break;
			// if service version is there in HTTP header, set it in
			// APIHeader.
			case APIHeader.API_REQ_TIME:
				try {
					String sample = headerMAP.get(key).toString().replace("[", "").replace("]", "");
					Date reqTime = dateFormat.parse(sample);
					header.setRequestTime(reqTime);
					apiLogDetail.setHeaderReqTime(new Timestamp(header.getRequestTime().getTime()));
				} catch (Exception e) {
					log.error(Literal.EXCEPTION, e);
				}
				break;
			// if Channel is there in HTTP header, set it in APIHeader.
			case APIHeader.API_CHANNEL:
				header.setChannel(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
				apiLogDetail.setChannel(header.getChannel());
				break;
			// all other header details are added in additional info map.
			default:
				break;
			}

		}
		apiLogDetail.setResponseGiven(new Timestamp(System.currentTimeMillis()));
		long seqId = apiLogDetailDAO.saveLogDetails(apiLogDetail);
		apiLogDetail.setSeqId(seqId);
		message.getExchange().put(APIHeader.API_LOG_KEY, apiLogDetail);
		log.info(buffer.toString());

	}

	/**
	 * Method for get the Service name of the given endpoint.
	 * 
	 * @param endPoint
	 * @param method
	 * @return
	 */
	private String getServiceName(String endPoint, String method) {
		String serviceName = "";
		String[] values = endPoint.split("/");
		if (StringUtils.equalsIgnoreCase(method, HttpMethod.DELETE)
				|| StringUtils.equalsIgnoreCase(method, HttpMethod.GET)) {
			serviceName = values[values.length - 1];
			if (values.length >= 2) {
				serviceName = serviceName + "/" + values[values.length - 2];
			}
		} else {
			serviceName = values[values.length - 1];

		}
		return serviceName;
	}

	@Override
	protected java.util.logging.Logger getLogger() {
		return LogUtils.getLogger(LogRequestInterceptor.class);
	}

	@Autowired
	public void setApiLogDetailDAO(APILogDetailDAO apiLogDetailDAO) {
		this.apiLogDetailDAO = apiLogDetailDAO;
	}

	public APILogDetailDAO getApiLogDetailDAO() {
		return apiLogDetailDAO;
	}

}