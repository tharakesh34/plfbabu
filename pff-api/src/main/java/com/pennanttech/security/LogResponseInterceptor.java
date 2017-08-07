package com.pennanttech.security;

import java.io.OutputStream;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.util.APILogDetailDAO;
import com.pennanttech.util.TypeConstants;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * A simple logging handler to log the outgoing API response message details.
 */

public class LogResponseInterceptor extends LoggingOutInterceptor {

	private static final Logger LOG = Logger.getLogger(LogResponseInterceptor.class);
	private APILogDetailDAO aPILogDetailDAO;
	public LogResponseInterceptor() {
		super(Phase.PRE_STREAM);
	}

	public void handleMessage(Message message) throws Fault {
		if (writer != null || LOG.isInfoEnabled()) {
			logging(message);
		}
	}

	private void logging(Message message) throws Fault {

		OutputStream os = message.getContent(OutputStream.class);

		final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
		message.setContent(OutputStream.class, newOut);
		newOut.registerCallback(new LoggingCallback(message, os));

	}

	@Override
	protected java.util.logging.Logger getLogger() {
		return LogUtils.getLogger(LogResponseInterceptor.class);
	}
	@Autowired
	public void setaPILogDetailDAO(APILogDetailDAO aPILogDetailDAO) {
		this.aPILogDetailDAO = aPILogDetailDAO;
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
			APILogDetail apiLogDetail= new APILogDetail();
			apiLogDetail.setReference(Integer.parseInt(buffer.getId().replaceAll(", ", "")));
			apiLogDetail.setResponseCode(String.valueOf(buffer.getResponseCode()));
			apiLogDetail.setPayLoad(String.valueOf(buffer.getPayload()));
			apiLogDetail.setValueDate(DateUtil.getSysDate());
			apiLogDetail.setType(TypeConstants.RESPONSE.get());
			aPILogDetailDAO.saveLogDetails(apiLogDetail);
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

}