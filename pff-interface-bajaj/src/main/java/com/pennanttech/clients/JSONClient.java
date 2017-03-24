package com.pennanttech.clients;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONClient {

	public Object postProcess(String url, String service,Object requestData, Class<?> responseClass) throws Exception {
		Object object = null;
		WebClient client = getClient(url, service);
		Response response = client.post(convertData(requestData));
		object  =  getResponse(response.readEntity(String.class), responseClass);
		return object;
	}

	private String convertData(Object requestData) throws Exception {
		String jsonStr = null;

		ObjectMapper mapperObj = new ObjectMapper();
		try {
			jsonStr = mapperObj.writeValueAsString(requestData);
		} catch (Exception e) {
			throw e;
		}

		return jsonStr;
	}

	private Object getResponse(String strResponse, Class<?> responseClass) throws Exception{
		System.out.println(strResponse);  
		ObjectMapper mapper = new ObjectMapper();
		  Object  response=null;
		try {
			response= mapper.readValue(strResponse,responseClass);
			
		} catch (Exception e) {
			throw e;
		}
		return response;
	}
	
	
	private static WebClient getClient(String url, String path) {
		WebClient client = WebClient.create(url);
		client.accept("application/json");
		client.type("application/json");
		client.path(path);
		return client;
	}

}
