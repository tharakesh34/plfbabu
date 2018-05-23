/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FinnovCibilEnquiryProcess.java										*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  23-05-2018															*
 *                                                                  
 * Modified Date    :  23-05-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2018       Pennant	                 1.0          Created as part of Finnov 
 * 														  Profectus integration			    * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.pff.external.creditInformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
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

public class JSONClient {

	private static final Logger	logger			= Logger.getLogger(JSONClient.class);

	private final static String	AUTHORIZATION	= "Authorization";
	
	public String post(String url, String requestObject, String accessKey) throws Exception {
		logger.debug(Literal.ENTERING);

		WebClient client = getClient(StringUtils.trimToEmpty(url), accessKey);
		logger.debug("Json Request String " + requestObject);
		Response response = client.post(requestObject);
		String responseObject=response.readEntity(String.class);
		logger.debug("Json Response String " + responseObject);
		logger.debug(Literal.LEAVING);
		return responseObject;
	}
	
	
	public String get(String url) throws Exception {
		logger.debug(Literal.ENTERING);
		WebClient client = getClient(StringUtils.trimToEmpty(url), null);
		logger.debug("Json Request String ");
		Response response = client.get();
		String jsonInString = response.readEntity(String.class);
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
			logger.error("Exception in json request string" + e);
		}
		return jsonInString;
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
	public  WebClient getClient(String serviceUrl, String accessKey) {
		logger.debug(Literal.ENTERING);
		List<Object> providers=new ArrayList<>();
		providers.add(new JacksonJaxbJsonProvider());
		WebClient client = WebClient.create(serviceUrl,providers);
		client.accept(MediaType.APPLICATION_JSON);
		client.type(MediaType.APPLICATION_JSON);
		if(null!=accessKey){
			client = prepareHeader(client, accessKey);
		}
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
	private  WebClient prepareHeader(WebClient client, String accessKey) {
		logger.debug(Literal.ENTERING);
		client.header(AUTHORIZATION, getAuthkey(accessKey));
		logger.debug(Literal.LEAVING);
		return client;
	}

	/**
	 * Generate Authorization key for client specific by loading use name and password from SystemParams.
	 * 
	 * @return
	 */
	private String getAuthkey(String accessKey) {
		logger.debug(Literal.ENTERING);
		
		String authKey = "Bearer " + accessKey;
		logger.debug(Literal.LEAVING);
		return authKey;
	}

}
