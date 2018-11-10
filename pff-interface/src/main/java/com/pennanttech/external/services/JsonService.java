package com.pennanttech.external.services;

import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
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

	protected JsonServiceDetail processMessage(JsonServiceDetail serviceDetail) {
		logger.debug(Literal.ENTERING);

		serviceDetail.setRequestString(getObjectToJson(serviceDetail));
		Timestamp reqSentOn = null;

		String url = App.getProperty(serviceDetail.getServiceUrl());
		HttpMethod method = serviceDetail.getMethod();
		logger.trace(String.format("URL %s%nMethod %s%nRequest Data %s", url, method.name(), serviceDetail.getRequestString()));

		HttpEntity<String> httpEntity = new HttpEntity<>(serviceDetail.getRequestString(), serviceDetail.getHeaders());
		getHttpHeader(serviceDetail.getHeaders());

		ResponseEntity<String> response = null;
		InterfaceLogDetail logDetail = null;

		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;
		try {
			logDetail = new InterfaceLogDetail();
			reqSentOn = new Timestamp(System.currentTimeMillis());

			logDetail.setReference(serviceDetail.getReference());
			logDetail.setServiceName(serviceDetail.getServiceName());
			logDetail.setEndPoint(serviceDetail.getServiceUrl());
			logDetail.setRequest(StringUtils.left(StringUtils.trimToEmpty(serviceDetail.getRequestString()), 1000));
			logDetail.setReqSentOn(reqSentOn);
			logRequest(logDetail);
			response = getTemplate().exchange(url, method, httpEntity, String.class);
			
			serviceDetail.setResponseString(response.getBody());
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
			logger.trace(String.format("Response Data %s", serviceDetail.getResponseString()));
			if (logDetail != null) {
				logDetail.setResponse(StringUtils.left(StringUtils.trimToEmpty(serviceDetail.getResponseString()), 1000));
				logDetail.setRespReceivedOn(new Timestamp(System.currentTimeMillis()));
				logDetail.setStatus(status);
				logDetail.setErrorCode(errorCode);
				logDetail.setErrorDesc(StringUtils.left(StringUtils.trimToEmpty(errorDesc), 200));
				updateResponse(logDetail);
			}
		}
		logger.debug(Literal.LEAVING);
		return serviceDetail;
	}

	protected T processMessage(JsonServiceDetail serviceDetail, Class<T> valueType) {
		logger.debug(Literal.ENTERING);
		processMessage(serviceDetail, valueType);
		logger.debug(Literal.LEAVING);
		return getResponse(serviceDetail.getResponseString(), valueType);
	}

	public String getObjectToJson(JsonServiceDetail jsonServiceDetail) {
		String resuestData = null;
		try {
			ObjectMapper mapper = getObjectMapper(jsonServiceDetail);
			resuestData = mapper.writeValueAsString(jsonServiceDetail.getRequestData());
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

	public T getResponse(String content, Class<T> valueType) {
		T resp = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			resp = mapper.readValue(content, valueType);
		} catch (Exception e) {
			throw new InterfaceException("8903", "Response Generation Expection", e);
		}
		return resp;
	}

	private ObjectMapper getObjectMapper(JsonServiceDetail jsonServiceDetail ) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);

		if (jsonServiceDetail.isExcludeNull()) {
			mapper.setSerializationInclusion(Inclusion.NON_NULL);
		}
		if (jsonServiceDetail.isExcludeEmpty()) {
			mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
		}

		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());

		return mapper;

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
