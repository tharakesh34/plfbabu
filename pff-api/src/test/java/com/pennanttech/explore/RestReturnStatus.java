package com.pennanttech.explore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestReturnStatus {

	public static Map<String, Object> convertStringToMap(String payload) {
		ObjectMapper obj = new ObjectMapper();

		Map<String, Object> map = null;
		try {
			map = obj.readValue(payload, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;

	}

	public static List<FaultDetails> convertJsonArrayToList(String payload)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();

		List<FaultDetails> faultDetailsList = objMapper.readValue(payload, new TypeReference<List<FaultDetails>>() {
		});
		return faultDetailsList;
	}

}
