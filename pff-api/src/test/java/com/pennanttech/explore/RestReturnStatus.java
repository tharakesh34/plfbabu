package com.pennanttech.explore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class RestReturnStatus {

	public static Map<String, Object> convertStringToMap(String payload){
		 ObjectMapper obj = new ObjectMapper();
		 
		 HashMap<String, Object> map = null;
	        try {
				 map =obj.readValue(payload,new TypeReference<HashMap<String,Object>>(){});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return map;
		
	}
	

	public static List<FaultDetails> convertJsonArrayToList(String payload) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper objMapper = new ObjectMapper();
			
		List<FaultDetails> faultDetailsList = objMapper.readValue(payload, new TypeReference<List<FaultDetails>>(){});
		return faultDetailsList;
	}
	
}
