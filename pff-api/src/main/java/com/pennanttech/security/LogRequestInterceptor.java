package com.pennanttech.security;

import java.io.InputStream;
import java.sql.Timestamp;

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

import com.pennant.app.util.APIHeader;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * A simple logging handler to log incoming request details.
 */

public class LogRequestInterceptor extends LoggingInInterceptor {
	static final Logger log = Logger.getLogger(LogRequestInterceptor.class);

	public LogRequestInterceptor() {
		super(Phase.RECEIVE);
	}

	public void handleMessage(Message message) throws Fault {
		if (writer != null || log.isInfoEnabled()) {
			logging(message);
		}
	}

	protected void logging(Message message) throws Fault {

		if (message.containsKey(LoggingMessage.ID_KEY)) {
			return;
		}
		
		// Set unique messageId
		String messageId = (String) message.getExchange().get(LoggingMessage.ID_KEY);
		if (messageId == null) {
			messageId = LoggingMessage.nextId();
			message.getExchange().put(LoggingMessage.ID_KEY, messageId);
		}
		message.put(LoggingMessage.ID_KEY, messageId);
		final LoggingMessage buffer = new LoggingMessage("============= IN Message ==============\n", messageId + ", ");

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

		InputStream is = message.getContent(InputStream.class);
		if (is != null) {
			CachedOutputStream bos = new CachedOutputStream();
			try {
				IOUtils.copy(is, bos);

				bos.flush();
				is.close();

				message.setContent(InputStream.class, bos.getInputStream());

				writePayload(buffer.getPayload(), bos, encoding, ct);

				bos.close();
			} catch (Exception e) {
				throw new Fault(e);
			}
		}
		APILogDetail apiLogDetail = new APILogDetail();
		apiLogDetail.setCxfID(Integer.parseInt(buffer.getId().replaceAll(", ", "")));
		String endPoint = StringUtils.trimToEmpty(String.valueOf(buffer.getAddress()).replaceAll(",", ""));
		String method = StringUtils.trimToEmpty(httpMethod);
		apiLogDetail.setServiceName(getServiceName(endPoint, method));
		apiLogDetail.setEndPoint(endPoint);
		apiLogDetail.setMethod(method);
		apiLogDetail.setReceivedOn(new Timestamp(System.currentTimeMillis()));
		apiLogDetail.setRequest(String.valueOf(buffer.getPayload()));
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
		if (StringUtils.equalsIgnoreCase(method, HttpMethod.DELETE) || StringUtils.equalsIgnoreCase(method, HttpMethod.GET)) {
			if (values.length >= 2) {
				serviceName = values[values.length - 2];
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

}