package com.pennanttech.external.services;

import java.net.ConnectException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class JsonService<T> {
	protected static Logger logger = Logger.getLogger(JsonService.class.getClass());
	
	protected static final String READ_TIMEOUT = "exteranal.interface.read.timeout";
	protected static final String CONNECTION_TIMEOUT = "exteranal.interface.connection.timeout";
	
	protected int readTimeout = 0;
	protected int connTimeout = 0;

	@Autowired
	private InterfaceLoggingDAO interfaceLoggingDAO;

	/**
	 * Set the properties.
	 */
	protected void doSetProperties(String readKey, String connKey) {
		try {
			if (StringUtils.trimToNull(readKey) != null) {
				readTimeout = Integer.valueOf(App.getProperty(readKey));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		try {
			if (StringUtils.trimToNull(connKey) != null) {
				connTimeout = Integer.valueOf(App.getProperty(connKey));
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	protected T processMessage(RequestDetails request, Class<T> valueType) {
		logger.debug(Literal.ENTERING);
		String responseData = null;
		String requestData = getObjectToJson(request);
		Timestamp reqSentOn = null;

		String url = App.getProperty(request.getServiceUrl());
		HttpMethod method = request.getMethod();
		logger.trace(String.format("URL %s%nMethod %s%nRequest Data %s", url, method.name(), requestData));

		HttpEntity<String> httpEntity = new HttpEntity<>(requestData, request.getHeaders());
		getHttpHeader(request.getHeaders());

		ResponseEntity<String> response = null;
		InterfaceLogDetail logDetail = null;

		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;
		try {
			logDetail = new InterfaceLogDetail();
			reqSentOn = new Timestamp(System.currentTimeMillis());

			logDetail.setReference(request.getReference());
			logDetail.setServiceName(request.getServiceName());
			logDetail.setEndPoint(request.getServiceUrl());
			logDetail.setRequest(StringUtils.left(StringUtils.trimToEmpty(requestData), 1000));
			logDetail.setReqSentOn(reqSentOn);
			logRequest(logDetail);

			response = getTemplate().exchange(url, method, httpEntity, String.class);
			responseData = response.getBody();

		} catch (ResourceAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			if (e.getCause() != null && e.getCause() instanceof ConnectException) {
				errorCode = "8900";
				errorDesc = e.getMessage();
				status = InterfaceConstants.STATUS_FAILED;
				throw new InterfaceException(errorCode, "Connection Expection", e);
			} else {
				errorCode = "8901";
				errorDesc = e.getMessage();
				status = InterfaceConstants.STATUS_FAILED;
				throw new InterfaceException(errorCode, "Timeout Expection", e);
			}
		} catch (Exception e) {
			errorCode = "8904";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			throw new InterfaceException("8904", e.getMessage(), e);

		} finally {
			logger.trace(String.format("Response Data %s", responseData));
			if (logDetail != null) {
				logDetail.setResponse(StringUtils.left(StringUtils.trimToEmpty(responseData), 1000));
				logDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
				logDetail.setStatus(status);
				logDetail.setErrorCode(errorCode);
				logDetail.setErrorDesc(errorDesc);
				updateResponse(logDetail);
			}
		}

		logger.debug(Literal.LEAVING);
		return getResponse(responseData, valueType);
	}

	protected String getObjectToJson(RequestDetails request) {
		String resuestData = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (request.isExcludeNull()) {
				mapper.setSerializationInclusion(Inclusion.NON_NULL);
			}
			if (request.isExcludeEmpty()) {
				mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
			}
			resuestData = mapper.writeValueAsString(request.getRequestData());
		} catch (Exception e) {
			throw new InterfaceException("8902", "RequesrGeneration Expection", e);
		}
		return resuestData;
	}

	private RestTemplate getTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
		SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
		rf.setReadTimeout(readTimeout);
		rf.setConnectTimeout(connTimeout);
		return restTemplate;
	}

	protected HttpHeaders getHttpHeader(HttpHeaders headers) {
		if (headers == null) {
			headers = new HttpHeaders();
		}

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	protected T getResponse(String content, Class<T> valueType) {
		T resp = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			resp = mapper.readValue(content, valueType);
		} catch (Exception e) {
			throw new InterfaceException("8903", "Response Generation Expection", e);
		}
		return resp;
	}

	protected void logRequest(InterfaceLogDetail logDetail) {
		logger.debug(Literal.ENTERING);
		if (interfaceLoggingDAO != null) {
			interfaceLoggingDAO.save(logDetail);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void updateResponse(InterfaceLogDetail logDetail) {
		logger.debug(Literal.ENTERING);
		if (interfaceLoggingDAO != null) {
			interfaceLoggingDAO.update(logDetail);
		}
		logger.debug(Literal.LEAVING);
	}
	
	protected long getTransactionId() {
		if (interfaceLoggingDAO != null) {
			return interfaceLoggingDAO.getSequence();
		}

		return System.currentTimeMillis();
	}
}
