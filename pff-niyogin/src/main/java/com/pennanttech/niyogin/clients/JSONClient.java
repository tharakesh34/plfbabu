package com.pennanttech.niyogin.clients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JSONClient {

	private static final Logger logger = Logger.getLogger(JSONClient.class);

	private final static String  AUTHORIZATION = "Authorization";
	
	public Object postProcess(String url, String service, Object requestData, Class<?> responseClass) throws Exception {
		String json = "";
		Response response = getClient(url, service, requestData);
		json = response.readEntity(String.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		Object objResponse = mapper.readValue(json, responseClass);

		return objResponse;
	}

	public String post(String url, String service, Object requestData, Class<?> responseClass) throws Exception {
		Response response = getClient(url, service, requestData);
		String json = (String) response.readEntity(String.class);
		return json;
	}
	
	private static Response getClient(String url, String path, Object requestData) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
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
		logger.debug("Jason Request String " + jsonInString);

		Client client = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class);
		WebTarget target = client.target(url).path(path);
		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE).header(AUTHORIZATION, getAuthkey() );
		Response response = builder.post(Entity.entity(jsonInString, MediaType.APPLICATION_JSON_TYPE));
		logger.info(response.readEntity(String.class));
		return response;
	}

	/**
	 * Generate Authorization key for client specific by loading use name and password  from config file
	 * 
	 * @return
	 */
	private static String getAuthkey() {
		//TODO:DDP-Use encypted password
		String username = "qUmCM";
		String password = "rye28f16Z";
		String key = username + ":" + password;
		String authKey = "Basic "+java.util.Base64.getEncoder().encodeToString(key.getBytes());
		
		return authKey;
	}

}
