package com.pennanttech.explore;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

/*
 * This is test program to validate the Mandate implementation of PFF.
 * 
 * 1. PFF application should be running in tomcat at http://localhost:8080/pff-api for running this test.
 * 2. Mandate services should be running in tomcat.
 * 
 */

public class MandateServiceTestCases {

	private static final String ENDPOINT_ADDRESS = "http://192.168.1.40:8080/pff-api/services/mandateRest";
	private static final String WADL_ADDRESS = ENDPOINT_ADDRESS + "?_wadl";
	private static final String MANDATE_SERVICE_PATH = "/mandateService";
	private static final String MANDATE_SERVICE_MANDATE_DETAILS = MANDATE_SERVICE_PATH + "/getMandate/1";
	private static final String MANDATE_SERVICE_MANDATES_DETAILS = MANDATE_SERVICE_PATH + "/getMandates/200000";
	private static final String MANDATE_SERVICE_CREATION = MANDATE_SERVICE_PATH + "/createMandate";
	private static final String MANDATE_SERVICE_UPDATE = MANDATE_SERVICE_PATH + "/updateMandate";
	private static final String MANDATE_SERVICE_DELETE = MANDATE_SERVICE_PATH + "/deleteMandate/6";
	private static final String MANDATE_SERVICE_LOANSWAPING = MANDATE_SERVICE_PATH + "/loanMandateSwapping";

	private String createMandateReq = "{\"cif\":\"200000\",\"useExisting\":true,\"mandateRef\":\"\",\"mandateType\":\"ECS\",\"bankCode\":\"\",\"branchCode\":\"\",\"ifsc\":\"SBIN0012831\",\"micr\":\"206002025\",\"accType\":\"29\",\"accNumber\":\"1010200500001\",\"accHolderName\":\"Rishi\",\"jointAccHolderName\":\"\",\"openMandate\":false,\"startDate\":\"2016-12-07T00:00:00\",\"expiryDate\":\"2016-12-30T00:00:00\",\"maxLimit\":10000000,\"periodicity\":\"M0001\",\"status\":\"NEW\"}";
	private String updateMandateReq = "{\"mandateID\":7,\"cif\":\"200000\",\"useExisting\":true,\"mandateRef\":\"\",\"mandateType\":\"ECS\",\"bankCode\":\"\",\"branchCode\":\"\",\"ifsc\":\"SBIN0012831\",\"micr\":\"206002025\",\"accType\":\"29\",\"accNumber\":\"1010200500001\",\"accHolderName\":\"Krish\",\"jointAccHolderName\":\"\",\"openMandate\":false,\"startDate\":\"2016-12-07T00:00:00\",\"expiryDate\":\"2016-12-30T00:00:00\",\"maxLimit\":10000000,\"periodicity\":\"M0001\",\"status\":\"NEW\"}";
	private String mandateSwapeReq = "{\"finReference\": \"PB1509901582\",\"oldMandateId\": 7,\"newMandateId\":7 }";

	private String returnCode = null;
	private String returnText;

	// String createMandateReq = ""

	@BeforeClass
	public static void initialize() throws Exception {
		waitForWADL();
	}

	// Optional step - may be needed to ensure that by the time individual
	// tests start running the endpoint has been fully initialized
	@SuppressWarnings("static-access")
	private static void waitForWADL() throws Exception {
		WebClient client = WebClient.create(WADL_ADDRESS);
		// wait for 20 secs or so
		for (int i = 0; i < 20; i++) {
			Thread.currentThread().sleep(1000);
			Response response = client.get();
			if (response.getStatus() == 200) {
				break;
			}
		}
		// no WADL is available yet - throw an exception or give tests a chance
		// to run anyway
	}

	private WebClient getClient(String path) {
		WebClient client = WebClient.create(ENDPOINT_ADDRESS);
		client.accept(MediaType.APPLICATION_JSON);
		client.type(MediaType.APPLICATION_JSON);
		client.path(path);
		String authorization = "user:sso:test";
		String encodedAuth = Base64.encodeBase64String(authorization.getBytes());
		client.header(CONSTANTS.AuthKey.get(), encodedAuth);
		return client;
	}
	
	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void testGetMandateDetailsSuccess() {
		WebClient client = getClient(MANDATE_SERVICE_MANDATE_DETAILS);
		Response response = client.get();
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload.get("returnStatus") instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload.get("returnStatus");
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void testforCreateMandateSuccessResponse() {
		WebClient client = getClient(MANDATE_SERVICE_CREATION);
		Response response = client.post(createMandateReq);
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload.get("returnStatus") instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload.get("returnStatus");
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	@Test(enabled = false)
	public void testforUpdateMandateSuccessResponse() {
		WebClient client = getClient(MANDATE_SERVICE_UPDATE);
		Response response = client.post(updateMandateReq);
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload;
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	@Test(enabled = false)
	public void testforDeleteMandateSuccessResponse() {
		WebClient client = getClient(MANDATE_SERVICE_DELETE);
		Response response = client.delete();
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload;
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void testGetMandatesDetailsSuccess() {
		WebClient client = getClient(MANDATE_SERVICE_MANDATES_DETAILS);
		Response response = client.get();
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload.get("returnStatus") instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload.get("returnStatus");
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	@Test(enabled = false)
	public void testloanMandateSwapping() {
		WebClient client = getClient(MANDATE_SERVICE_LOANSWAPING);
		Response response = client.post(mandateSwapeReq);
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		if (payload instanceof Object) {
			HashMap<String, Object> map = (HashMap<String, Object>) payload;
			returnCode = String.valueOf(map.get("returnCode"));
			returnText = String.valueOf(map.get("returnText"));
		}
		assertEquals("Response status 200", "0000", returnCode);
		assertEquals("Response status 200", "Success", returnText);
	}

	public static Map<String, Object> convertStringToMap(String payload) {
		ObjectMapper obj = new ObjectMapper();

		HashMap<String, Object> map = null;
		try {
			map = obj.readValue(payload, new TypeReference<HashMap<String, Object>>() {
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
}