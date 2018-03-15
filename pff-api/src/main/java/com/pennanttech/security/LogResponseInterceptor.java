package com.pennanttech.security;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.beans.factory.annotation.Autowired;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.pennant.app.util.APIHeader;
import com.pennanttech.util.APIConstants;
import com.pennanttech.util.APILogDetailDAO;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * A simple logging handler to log the outgoing API response message details.
 */

public class LogResponseInterceptor extends LoggingOutInterceptor {

	private static final Logger LOG = Logger.getLogger(LogResponseInterceptor.class);
	private APILogDetailDAO apiLogDetailDAO;
	public LogResponseInterceptor() {
		super(Phase.PRE_STREAM);
	}

	public void handleMessage(Message message) throws Fault {
		if (writer != null || LOG.isInfoEnabled()) {
			logging(message);
			//prepare the HeaderDetails of Response.
			prepareHeaderDetails(message);
		}
	}

	private void logging(Message message) throws Fault {

		OutputStream os = message.getContent(OutputStream.class);

		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
		message.setContent(OutputStream.class, newOut);
		newOut.registerCallback(new LoggingCallback(message, os));

	}
	
	/**
	 * Method to prepare the PROTOCOL HEADERS based on the given response return code.
	 * if return code is 0000 then return description  is success else Failure.
	 * 
	 * @param message
	 */
	private void prepareHeaderDetails(Message message) {
		@SuppressWarnings("unchecked")
		Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
		APIHeader apiHeader = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		if (headers == null) {
			headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
			message.put(Message.PROTOCOL_HEADERS, headers);
		}
		if (apiHeader != null) {
			String returnCode = null;
			String returnDesc = null;
			Object obj = message.getContent(List.class).get(0);
			//here we check the Return code and based on that we set the ReturnDesc to the Header.
			if (StringUtils.isBlank(apiHeader.getReturnCode()) && obj != null) {
				String jsonResponse = LogUtility.convertObjToJson(obj);
				returnCode = LogUtility.getReturnCode(jsonResponse);
				if (StringUtils.equalsIgnoreCase(returnCode, APIConstants.RES_SUCCESS_CODE)) {
					returnDesc = APIConstants.RES_SUCCESS_DESC;
				} else {
					returnDesc = APIConstants.RES_FAILURE_DESC;
				}
				apiHeader.setReturnCode(returnCode);
				apiHeader.setReturnDesc(returnDesc);
			}
			headers.put(APIHeader.API_RETURNCODE, Arrays.asList(apiHeader.getReturnCode()));
			headers.put(APIHeader.API_RETURNDESC, Arrays.asList(apiHeader.getReturnDesc()));
		}
	}

	@Override
	protected java.util.logging.Logger getLogger() {
		return LogUtils.getLogger(LogResponseInterceptor.class);
	}
	@Autowired
	public void setaPILogDetailDAO(APILogDetailDAO apiLogDetailDAO) {
		this.apiLogDetailDAO = apiLogDetailDAO;
	}

	private class LoggingCallback implements CachedOutputStreamCallback {

		private final Message message;
		private final OutputStream origStream;
		private final int lim;

		LoggingCallback(final Message msg, final OutputStream os) {
			this.message = msg;
			this.origStream = os;
			this.lim = limit == -1 ? Integer.MAX_VALUE : limit;
		}

		public void onFlush(CachedOutputStream cos) {

		}

		public void onClose(CachedOutputStream cos) {
			LoggingMessage buffer = setupBuffer(message);

			String ct = (String) message.get(Message.CONTENT_TYPE);
			if (!isShowBinaryContent() && isBinaryContent(ct)) {
				buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');
				LOG.info(formatLoggingMessage(buffer));
				return;
			}
			if (!isShowMultipartContent() && isMultipartContent(ct)) {
				buffer.getMessage().append(MULTIPART_CONTENT_MESSAGE).append('\n');
				LOG.info(formatLoggingMessage(buffer));
				return;
			}

			if (cos.getTempFile() == null) {
				// buffer.append("Outbound Message:\n");
				if (cos.size() >= lim) {
					buffer.getMessage().append("(message truncated to " + lim + " bytes)\n");
				}
			} else {
				buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
				buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
				if (cos.size() >= lim) {
					buffer.getMessage().append("(message truncated to " + lim + " bytes)\n");
				}
			}
			try {
				String encoding = (String) message.get(Message.ENCODING);
				writePayload(buffer.getPayload(), cos, encoding, ct);
			} catch (Exception ex) {
				LOG.error("Error logging API Response : ", ex);
				// ignore
			}

			LOG.info(formatLoggingMessage(buffer));
			try {
				// empty out the cache
				cos.lockOutputStream();
				cos.resetOut(null, false);
			} catch (Exception ex) {
				// ignore
			}
			message.setContent(OutputStream.class, origStream);
			APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
			if (apiLogDetail != null) {
				apiLogDetail.setResponseGiven(new Timestamp(System.currentTimeMillis()));
				apiLogDetail.setResponse(String.valueOf(buffer.getPayload()));
				if(StringUtils.isBlank(apiLogDetail.getStatusCode())){
					apiLogDetail.setStatusCode(LogUtility.getReturnCode(buffer.getPayload().toString()));					
				}
				// save API logging details
				apiLogDetailDAO.saveLogDetails(apiLogDetail);
			}
		}

		private LoggingMessage setupBuffer(Message message) {
			String messageId = (String) message.getExchange().get(LoggingMessage.ID_KEY);
			if (messageId == null) {
				messageId = LoggingMessage.nextId();
				message.getExchange().put(LoggingMessage.ID_KEY, messageId);
			}
			final LoggingMessage buffer = new LoggingMessage("============= OUT Message =============\n", messageId + ", ");

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
			String address = (String) message.get(Message.ENDPOINT_ADDRESS);
			if (address != null) {
				buffer.getAddress().append(address);
				String uri = (String) message.get(Message.REQUEST_URI);
				if (uri != null && !address.startsWith(uri)) {
					if (!address.endsWith("/") && !uri.startsWith("/")) {
						buffer.getAddress().append("/");
					}
					buffer.getAddress().append(uri + ", ");
				}
			}
			String ct = (String) message.get(Message.CONTENT_TYPE);
			if (ct != null) {
				buffer.getContentType().append(ct + ", ");
			}
			Object headers = message.get(Message.PROTOCOL_HEADERS);
			if (headers != null) {
				buffer.getHeader().append(headers + ", ");
			}
			return buffer;
		}
		

	}

	public static class LogUtility {
		static Configuration	conf		= Configuration.defaultConfiguration();
		static Configuration	conf2		= conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
		static ObjectMapper		mapper		= new ObjectMapper();
		static DateFormat		dateFormat	= new SimpleDateFormat("yyyy-MM-dd");

		public static String getValueFromResponse(String key, String jsonResponse) {
			Object value = null;
			try {
				value = JsonPath.using(conf2).parse(jsonResponse).read(key);
			} catch (PathNotFoundException pathNotFoundException) {
				value = null;
				LOG.error("Exception in getStatusCode", pathNotFoundException);
			}
			return Objects.toString(value, "");
		}

		public static String convertObjToJson(Object obj) {
			String jsonString;
			mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
			mapper.setSerializationInclusion(Inclusion.NON_NULL);
			mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
			dateFormat.setLenient(false);
			mapper.setDateFormat(dateFormat);
			try {
				jsonString = mapper.writeValueAsString(obj);
			} catch (Exception e) {
				LOG.error("Exception in convertObjToJson", e);
				jsonString = null;
			}
			return jsonString;
		}

		public static String getReturnCode(String responseString) {
			String returnCode = null;
			String keypath1 = "$.returnStatus.returnCode";
			String keypath2 = "$.returnCode";

			returnCode = getValueFromResponse(keypath1, responseString);
			if (StringUtils.isEmpty(returnCode)) {
				returnCode = getValueFromResponse(keypath2, responseString);
			}

			return returnCode;
		}
	}
}