package com.pennanttech.explore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennanttech.pennapps.core.resource.Literal;

public class RestReturnStatus {
	private static final Logger logger = LogManager.getLogger(RestReturnStatus.class);

	public static Map<String, Object> convertStringToMap(String payload) {

		ObjectMapper obj = new ObjectMapper();

		Map<String, Object> map = null;
		try {
			map = obj.readValue(payload, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException e) {
			logger.error(Literal.EXCEPTION, e);
		} catch (JsonMappingException e) {
			logger.error(Literal.EXCEPTION, e);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
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
