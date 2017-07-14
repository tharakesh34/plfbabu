package com.pennanttech.clients;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONClient {
	
	private static final Logger	logger	= Logger.getLogger(JSONClient.class);
	
	public Object postProcess(String url, String service,Object requestData, Class<?> responseClass) throws Exception {		
		Response response 	= getClient(url, service, requestData);
		Object  objResponse = null;
		
		if (response instanceof org.apache.cxf.jaxrs.impl.ResponseImpl) {
			objResponse = ((org.apache.cxf.jaxrs.impl.ResponseImpl) response).readEntity(responseClass);
		} else {
			
			objResponse = response.readEntity(responseClass);
		}
		
		return objResponse;
	}
	
	
	private static Response getClient(String url, String path,
			Object requestData) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;

		try {
			jsonInString = mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in jason request string" + e);
		}

		logger.debug("Jason Request String " + jsonInString);

		Client client = ClientBuilder.newClient().register(
				JacksonJsonProvider.class);
		WebTarget target = client.target(url).path(path);
		Invocation.Builder builder = target
				.request(MediaType.APPLICATION_JSON_TYPE);

		Response response = builder.post(Entity.entity(requestData,
				MediaType.APPLICATION_JSON_TYPE)); // Successful
		logger.debug(response.readEntity(String.class));
		return response;
	}

}
