package com.pennanttech.clients;

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

	public Object postProcess(String url, String service, Object requestData, Class<?> responseClass) throws Exception {
		String json = "";
		Response response = getClient(url, service, requestData);
		json = response.readEntity(String.class);
		

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);

		return mapper.readValue(json, responseClass);
	}

	private static Response getClient(String url, String path, Object requestData) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;

		try {
			jsonInString = mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in jason request string" + e);
		}

		logger.debug("Jason Request String " + jsonInString);

		Client client = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class);
		WebTarget target = client.target(url).path(path);
		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);

		Response response = builder.post(Entity.entity(requestData, MediaType.APPLICATION_JSON_TYPE)); // Successful
		logger.info(response.readEntity(String.class));
		return response;
	}

}
