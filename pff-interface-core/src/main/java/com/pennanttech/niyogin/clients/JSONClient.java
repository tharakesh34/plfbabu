package com.pennanttech.niyogin.clients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.dao.NiyoginDAOImpl;

public class JSONClient {

	private static final Logger	logger			= Logger.getLogger(JSONClient.class);

	private final static String	AUTHORIZATION	= "Authorization";
	private static String		authorizationKey;
	private NiyoginDAOImpl		niyoginDAOImpl;
	
	public String post(String url, String jsonInString) throws Exception {
		logger.debug(Literal.ENTERING);

		WebClient client = getClient(StringUtils.trimToEmpty(url));
		logger.debug("Json Request String " + jsonInString);
		Response response = client.post(jsonInString);
		jsonInString=response.readEntity(String.class);
		logger.debug("Json Response String " + jsonInString);
		logger.debug(Literal.LEAVING);
		return jsonInString;
	}


	public String getRequestString(Object requestData) {
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
		
		try {
			jsonInString = mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in jason request string" + e);
		}
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
		logger.debug("Json Request String " + jsonInString);
		Response response = client.post(jsonInString);

		jsonInString = response.readEntity(String.class);
		Object objResponse = mapper.readValue(jsonInString, responseClass);
		logger.debug("Json Response String " + jsonInString);
		logger.debug(Literal.LEAVING);
		return objResponse;
	}

	public Object getResponseObject(String jsonResponse, Class<?> responseClass, boolean isList) {
		logger.debug(Literal.ENTERING);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);
		Object objResponse = null;
		try {
			if (isList) {
				CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class,
						responseClass);
				objResponse = mapper.readValue(jsonResponse, typeReference);
			} else {
				objResponse = mapper.readValue(jsonResponse, responseClass);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new InterfaceException(InterfaceConstants.INTFACE_ERROR_CD, e.getMessage());
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
	public  WebClient getClient(String serviceUrl) {
		logger.debug(Literal.ENTERING);
		WebClient client = WebClient.create(serviceUrl);
		client.accept(MediaType.APPLICATION_JSON);
		client.type(MediaType.APPLICATION_JSON);
		client = prepareHeader(client);
		HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
		conduit.getClient().setConnectionTimeout(1000 * 120);
		conduit.getClient().setReceiveTimeout(1000 * 120);

		logger.debug(Literal.LEAVING);
		return client;
	}

	/**
	 * Method for prepare web service header with specified fields
	 * 
	 * @param client
	 * @return
	 */
	private  WebClient prepareHeader(WebClient client) {
		logger.debug(Literal.ENTERING);
		client.header(AUTHORIZATION, getAuthkey());
		logger.debug(Literal.LEAVING);
		return client;
	}

	/**
	 * Generate Authorization key for client specific by loading use name and password from SystemParams.
	 * 
	 * @return
	 */
	private String getAuthkey() {
		logger.debug(Literal.ENTERING);
		if (StringUtils.isEmpty(authorizationKey)) {
			authorizationKey = (String) niyoginDAOImpl.getSMTParameter("NIYOGIN_INTERFACE_AUTHKEY", String.class);
			//if the value getting from DB is Null then it is Empty.
			authorizationKey = Objects.toString(authorizationKey, "");
		}
		String authKey = "Basic " + java.util.Base64.getEncoder().encodeToString(authorizationKey.getBytes());
		logger.debug(Literal.LEAVING);
		return authKey;
	}

	public void setNiyoginDAOImpl(NiyoginDAOImpl niyoginDAOImpl) {
		this.niyoginDAOImpl = niyoginDAOImpl;
	}

}
