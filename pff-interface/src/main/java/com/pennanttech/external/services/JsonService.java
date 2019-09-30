package com.pennanttech.external.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
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
import org.springframework.web.util.UriComponentsBuilder;

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
		InterfaceLogDetail logDetail =  new InterfaceLogDetail();;

		if (serviceDetail.isXmlRequest()) {
			serviceDetail.setRequestString(getObjectToXML(serviceDetail));
		} else {
			serviceDetail.setRequestString(getObjectToJson(serviceDetail));
		}

		Timestamp reqSentOn = null;

		String url = App.getProperty(serviceDetail.getServiceUrl());
		if (StringUtils.trimToNull(serviceDetail.getServiceEndPoint()) != null) {
			url = url + serviceDetail.getServiceEndPoint();
		}
		logDetail.setEndPoint(url);

		
		if (StringUtils.isNotEmpty(url)) {
			UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromUriString(url);
			if (MapUtils.isNotEmpty(serviceDetail.getPathParams())) {
				url = componentsBuilder.buildAndExpand(serviceDetail.getPathParams()).toUriString();
			}
			if (MapUtils.isNotEmpty(serviceDetail.getQueryParams())) {
				URIBuilder uriBuilder = null;
				try {
					uriBuilder = new URIBuilder(url);
				} catch (URISyntaxException e) {
					logger.error(Literal.EXCEPTION, e);
				}
				for (String queryKey : serviceDetail.getQueryParams().keySet()) {
					uriBuilder.addParameter(queryKey, serviceDetail.getQueryParams().get(queryKey));
				}
				try {
					url = URLDecoder.decode(uriBuilder.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		HttpMethod method = serviceDetail.getMethod();
		logger.trace(String.format("URL %s%nMethod %s%nRequest Data %s", url, method.name(),
				serviceDetail.getRequestString()));
		HttpEntity<String> httpEntity = new HttpEntity<>(serviceDetail.getRequestString(),
				getHttpHeader(serviceDetail.getHeaders(), serviceDetail.isXmlRequest()));
		ResponseEntity<String> response = null;
	
		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;
		try {
			reqSentOn = new Timestamp(System.currentTimeMillis());

			logDetail.setReference(serviceDetail.getReference());
			logDetail.setServiceName(serviceDetail.getServiceName());
			if(serviceDetail.getMethod()==HttpMethod.GET){
				logDetail.setRequest(url);
			}else{
				if(StringUtils.equalsIgnoreCase("Y", App.getProperty("external.interface.fulllog"))){
					logDetail.setRequest(serviceDetail.getRequestString());
				}else{
					logDetail.setRequest(StringUtils.left(serviceDetail.getRequestString(), 1000));	
				}
			}
			
			logDetail.setReqSentOn(reqSentOn);
			logRequest(logDetail);
			response = getTemplate(serviceDetail).exchange(url, method, httpEntity, String.class);
			serviceDetail.setResponseString(response.getBody());
		} catch (ResourceAccessException e) {
			logger.error(Literal.EXCEPTION, e);

			errorCode = "8900";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			throw new InterfaceException(errorCode, "Connection Expection", e);

			/*
			 * if (e.getCause() != null && e.getCause() instanceof ConnectException) { errorCode = "8900"; errorDesc =
			 * e.getMessage(); status = InterfaceConstants.STATUS_FAILED; throw new InterfaceException(errorCode,
			 * "Connection Expection", e); } else { errorCode = "8901"; errorDesc = e.getMessage(); status =
			 * InterfaceConstants.STATUS_FAILED; throw new InterfaceException(errorCode, "Timeout Expection", e); }
			 */
		} catch (Exception e) {
			errorCode = "8904";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			throw new InterfaceException("8904", e.getMessage(), e);

		} finally {
			logger.trace(String.format("Response Data %s", serviceDetail.getResponseString()));
			if (logDetail != null) {
				if(StringUtils.equalsIgnoreCase("Y", App.getProperty("external.interface.fulllog"))){
					logDetail.setRequest(serviceDetail.getResponseString());
				}else{
					logDetail.setResponse(StringUtils.left(serviceDetail.getResponseString(), 1000));
				}
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

	public T processMessage(JsonServiceDetail serviceDetail, Class<T> valueType) {
		logger.debug(Literal.ENTERING);
		processMessage(serviceDetail);
		logger.debug(Literal.LEAVING);
		if (serviceDetail.isXmlRequest()) {
			return getXMLResponse(serviceDetail.getResponseString(), valueType);
		} else {
			return getResponse(serviceDetail.getResponseString(), valueType);
		}
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

	public String getObjectToXML(JsonServiceDetail jsonServiceDetail) {

		if (StringUtils.trimToNull(jsonServiceDetail.getRequestString()) != null) {
			return jsonServiceDetail.getRequestString();
		}

		StringWriter sw = new StringWriter();

		if (StringUtils.trimToNull(jsonServiceDetail.getRequestString()) != null) {
			return jsonServiceDetail.getRequestString();
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(jsonServiceDetail.getRequestData().getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(jsonServiceDetail.getRequestData(), sw);
		} catch (Exception e) {
			throw new InterfaceException("8902", "RequesrGeneration Expection", e);
		}
		return sw.toString();
	}

	private RestTemplate getTemplate(JsonServiceDetail jsonServiceDetail) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
		SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
		rf.setReadTimeout(readTimeout);
		rf.setConnectTimeout(connTimeout);

		if (jsonServiceDetail.isProxyRequired() && StringUtils.isNotEmpty(jsonServiceDetail.getProxyUrl())
				&& jsonServiceDetail.getProxyPort() != 0) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					new InetSocketAddress(jsonServiceDetail.getProxyUrl(), jsonServiceDetail.getProxyPort()));
			rf.setProxy(proxy);
		}

		return restTemplate;
	}

	protected HttpHeaders getHttpHeader(HttpHeaders headers) {
		return getHttpHeader(headers, false);
	}

	protected HttpHeaders getHttpHeader(HttpHeaders headers, boolean xmlRequest) {

		if (headers != null) {
			return headers;
		}

		headers = new HttpHeaders();
		if (xmlRequest) {
			headers.setContentType(MediaType.APPLICATION_XML);
			headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
		} else {
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		}

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

	@SuppressWarnings("unchecked")
	public T getXMLResponse(String content, Class<T> valueType) {
		T resp = null;
		StringReader sr = new StringReader(content);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(valueType);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			resp = (T) unmarshaller.unmarshal(sr);
		} catch (Exception e) {
			throw new InterfaceException("8903", "Response Generation Expection", e);
		}
		return resp;
	}

	private ObjectMapper getObjectMapper(JsonServiceDetail jsonServiceDetail) {
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

		// mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());

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
