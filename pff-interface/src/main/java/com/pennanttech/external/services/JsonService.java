package com.pennanttech.external.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public abstract class JsonService<T> {
	protected static Logger logger = LogManager.getLogger(JsonService.class.getClass());

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
		InterfaceLogDetail logDetail = new InterfaceLogDetail();

		if (serviceDetail.isXmlRequest()) {
			serviceDetail.setRequestString(getObjectToXML(serviceDetail));
		} else {
			serviceDetail.setRequestString(getObjectToJson(serviceDetail));
		}

		Timestamp reqSentOn = null;

		String url = App.getProperty(serviceDetail.getServiceUrl());
		if (StringUtils.trimToNull(serviceDetail.getServiceEndPoint()) != null) {
			url = StringUtils.trimToEmpty(url) + serviceDetail.getServiceEndPoint();
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
		logger.trace("URL {} \nMethod {} \nRequest Data {}", url, method.name(), serviceDetail.getRequestString());
		HttpEntity<String> httpEntity = new HttpEntity<>(serviceDetail.getRequestString(),
				getHttpHeader(serviceDetail.getHeaders(), serviceDetail.isXmlRequest()));
		ResponseEntity<String> response = null;
		logger.debug("HTTP Header Details :" + httpEntity.getHeaders().toString());
		String errorCode = "0000";
		String errorDesc = null;
		String status = InterfaceConstants.STATUS_SUCCESS;
		try {
			reqSentOn = new Timestamp(System.currentTimeMillis());

			logDetail.setReference(serviceDetail.getReference());
			logDetail.setServiceName(serviceDetail.getServiceName());
			if (serviceDetail.getMethod() == HttpMethod.GET) {
				logDetail.setRequest(url);
			} else {
				if (App.getBooleanProperty("external.interface.fulllog")) {
					logDetail.setRequest(serviceDetail.getRequestString());
				} else {
					logDetail.setRequest(StringUtils.left(serviceDetail.getRequestString(), 1000));
				}
			}

			logDetail.setReqSentOn(reqSentOn);
			logRequest(logDetail);
			if (StringUtils.isNotEmpty(serviceDetail.getCertificateFileName())) {
				response = getTemplateWithCertificate(serviceDetail).exchange(url, method, httpEntity, String.class);
			} else {
				response = getTemplate(serviceDetail).exchange(url, method, httpEntity, String.class);
			}
			serviceDetail.setResponseHeaders(response.getHeaders());
			serviceDetail.setResponseString(response.getBody());
		} catch (ResourceAccessException e) {
			logger.error(Literal.EXCEPTION, e);

			errorCode = "8900";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			throw new InterfaceException(errorCode, "Connection Expection", e);

		} catch (Exception e) {
			errorCode = "8904";
			errorDesc = e.getMessage();
			status = InterfaceConstants.STATUS_FAILED;
			throw new InterfaceException("8904", e.getMessage(), e);

		} finally {
			logger.trace("Response Data {}", serviceDetail.getResponseString());
			if (logDetail != null) {
				if (App.getBooleanProperty("external.interface.fulllog")) {
					logDetail.setResponse(serviceDetail.getResponseString());
				} else {
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
			if (StringUtils.trimToNull(jsonServiceDetail.getRequestString()) != null) {
				return jsonServiceDetail.getRequestString();
			}
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
			logger.debug("Proxy Details {}", proxy.toString());
		}

		return restTemplate;
	}

	private RestTemplate getTemplateWithCertificate(JsonServiceDetail jsonServiceDetail) throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		FileInputStream fi = null;
	
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			fi = new FileInputStream(jsonServiceDetail.getCertificateFileName());
			keyStore.load(fi, jsonServiceDetail.getCertificatePassword().toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			kmf.init(keyStore, jsonServiceDetail.getCertificatePassword().toCharArray());

			KeyManager[] kms = kmf.getKeyManagers();
			TrustManager[] tms = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// unused
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// unused
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} };

			final SSLContext sslContext2 = SSLContext.getInstance("SSL");
			sslContext2.init(kms, tms, new SecureRandom());
			SSLContext.setDefault(sslContext2);

			HttpClient client = HttpClients.custom().setSSLContext(sslContext2).build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(client);

			requestFactory.setReadTimeout(readTimeout);
			requestFactory.setConnectTimeout(connTimeout);

			restTemplate = new RestTemplate(requestFactory);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (fi != null) {
				try {
					fi.close();
				} catch (IOException ie) {
					// do nothing
				}
			}
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
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setLenient(false);

		if (jsonServiceDetail.isExcludeNull()) {
			return JsonMapper.builder()

					.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

					.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true)

					.serializationInclusion(Include.NON_NULL)

					.serializationInclusion(Include.NON_EMPTY).build();
		}

		if (jsonServiceDetail.isExcludeEmpty()) {
			return JsonMapper.builder()

					.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

					.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

					.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true)

					.serializationInclusion(Include.NON_NULL)

					.serializationInclusion(Include.NON_EMPTY).build();
		}

		return JsonMapper.builder()

				.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

				.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true)

				.serializationInclusion(Include.NON_NULL)

				.serializationInclusion(Include.NON_EMPTY)

				.defaultDateFormat(dateFormat)

				.build();

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
