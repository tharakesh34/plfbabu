package com.pennanttech.clients;

//import java.text.SimpleDateFormat;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONClient {
	private final static Logger	logger	= Logger.getLogger(JSONClient.class);
	
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
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//		mapperObj.setDateFormat(dateFormat);
		try {
			jsonStr = mapperObj.writeValueAsString(requestData);
			logger.debug("Request message:" + jsonStr);
		} catch (Exception e) {
			logger.error("Exception converting ReqObject to jsonString :" + e.getMessage(), e);
			throw e;
		}
//System.out.println("Request  : " + jsonStr);	
		return jsonStr;
	}

	private Object getResponse(String strResponse, Class<?> responseClass) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
	    Object  response=null;
		logger.debug("Response received from host:" + strResponse);
//System.out.println("Response : " + strResponse);;		
		try {
			response= mapper.readValue(strResponse,responseClass);
		} catch (Exception e) {
			logger.error("Error converting response to ResObject : " + e.getMessage(), e);
			throw e;
		}
		return response;
	}
	
	
	private static WebClient getClient(String url, String path) {
		WebClient client = WebClient.create(url);
		client.accept("application/json");
		client.type("application/json");
		client.path(path);
		HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
        conduit.getClient().setConnectionTimeout(3000);
		return client;
	}

}
