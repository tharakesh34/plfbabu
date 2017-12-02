package com.pennanttech.niyogin.clients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.pennanttech.pennapps.core.resource.Literal;

public class JSONClient {

	private static final Logger	logger			= Logger.getLogger(JSONClient.class);

	private final static String	AUTHORIZATION	= "Authorization";
	
	public String post(String url, Object requestData) throws Exception {
		logger.debug(Literal.ENTERING);

		WebClient client = getClient(url);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);
		String jsonInString = null;
		
		logger.debug("Jason Request String " + jsonInString);
		Response response = client.post(jsonInString);
		jsonInString=response.readEntity(String.class);
		logger.debug("Jason Response String " + jsonInString);
		logger.debug(Literal.LEAVING);
		return jsonInString;
	}


	public Object post(String url, Object requestData, Class<?> responseClass) throws Exception {
		logger.debug(Literal.ENTERING);
		String jsonInString = null;
		WebClient client = getClient(url);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);

		try {
			jsonInString = mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in jason request string" + e);
		}
		logger.debug("Jason Request String " + jsonInString);
		Response response = client.post(jsonInString);

		jsonInString = response.readEntity(String.class);
		Object objResponse = mapper.readValue(jsonInString, responseClass);
		logger.debug("Jason Response String " + jsonInString);
		logger.debug(Literal.LEAVING);
		return objResponse;
	}

	public Object getResponseObject(String jsonResponse, String datePattern, Class<?> responseClass, boolean isList)
			throws Exception {
		logger.debug(Literal.ENTERING);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);
		Object objResponse = null;
		if (isList) {
			CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class,
					responseClass);
			objResponse = mapper.readValue(jsonResponse, typeReference);
		} else {
			objResponse = mapper.readValue(jsonResponse, responseClass);
		}
		logger.debug(Literal.LEAVING);
		return objResponse;
	}

	/**
	 * Method for prepare WebClient object and return.
	 * 
	 * @param serviceUrl
	 * @param authorization
	 * @return WebClient
	 */
	public static WebClient getClient(String serviceUrl) {
		logger.debug(Literal.ENTERING);
		WebClient client = WebClient.create(serviceUrl);
		client.accept(MediaType.APPLICATION_JSON);
		client.type(MediaType.APPLICATION_JSON);
		client = prepareHeader(client);

		logger.debug(Literal.LEAVING);
		return client;
	}

	/**
	 * Method for prepare web service header with specified fields
	 * 
	 * @param client
	 * @return
	 */
	private static WebClient prepareHeader(WebClient client) {
		logger.debug(Literal.ENTERING);
		client.header(AUTHORIZATION, getAuthkey());
		logger.debug(Literal.LEAVING);
		return client;
	}

	/**
	 * Generate Authorization key for client specific by loading use name and password from config file
	 * 
	 * @return
	 */
	private static String getAuthkey() {
		logger.debug(Literal.ENTERING);
		//TODO:DDP-Use encypted password
		String username = "qUmCM";
		String password = "rye28f16Z";
		String key = username + ":" + password;
		String authKey = "Basic " + java.util.Base64.getEncoder().encodeToString(key.getBytes());
		logger.debug(Literal.LEAVING);
		return authKey;
	}

}
