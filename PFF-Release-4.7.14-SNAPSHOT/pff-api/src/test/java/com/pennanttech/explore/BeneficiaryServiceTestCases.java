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
 * This is test program to validate the Beneficiary implementation of PFF.
 * 
 * 1. PFF application should be running in tomcat at http://localhost:8080/pff-api for running this test.
 * 2. Beneficiary services should be running in tomcat.
 * 
 */

public class BeneficiaryServiceTestCases {

	private static final String ENDPOINT_ADDRESS = "http://192.168.1.146:8080/pff-api/services/beneficiaryRest";
	private static final String WADL_ADDRESS = ENDPOINT_ADDRESS + "?_wadl";
	private static final String BENEFICIARY_SERVICE_PATH = "/beneficiaryService";
	private static final String BENEFICIARY_SERVICE_BENEFICIARY_DETAILS = BENEFICIARY_SERVICE_PATH
			+ "/getBeneficiary/1";
	private static final String BENEFICIARY_SERVICE_BENEFICIARIES_DETAILS = BENEFICIARY_SERVICE_PATH
			+ "/getBeneficiaries/000002";
	private static final String BENEFICIARY_SERVICE_CREATION = BENEFICIARY_SERVICE_PATH + "/createBeneficiary";
	private static final String BENEFICIARY_SERVICE_UPDATE = BENEFICIARY_SERVICE_PATH + "/updateBeneficiary";
	private static final String BENEFICIARY_SERVICE_DELETE = BENEFICIARY_SERVICE_PATH + "/deleteBeneficiary/16";

	private String createBeneficiaryReq = "{\"cif\": \"000157\",\"bankCode\": \"\",\"branchCode\": \"\",\"ifsc\": \"AB869347\",\"accountNo\": \"10102001\",\"acHolderName\": \"Ramu\",\"phoneCountryCode\": \"\",\"phoneAreaCode\": \"\",\"phoneNumber\": \"\" }";
	private String updateBeneficiaryReq = "{\"beneficiaryId\":16,\"cif\": \"000157\",\"bankCode\": \"\",\"branchCode\": \"\",\"ifsc\": \"AB869347\",\"accountNo\": \"10102001\",\"acHolderName\": \"Krishna\",\"phoneCountryCode\": \"\",\"phoneAreaCode\": \"\",\"phoneNumber\": \"\" }";

	private String returnCode = null;
	private String returnText;

	// String createMandateReq = ""

	@BeforeClass
	public static void initialize() throws Exception {
		waitForWADL();
	}

	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void testGetBeneficiaryDetailsSuccess() {
		WebClient client = getClient(BENEFICIARY_SERVICE_BENEFICIARY_DETAILS);
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
	public void testforCreateBeneficiarySuccessResponse() {
		WebClient client = getClient(BENEFICIARY_SERVICE_CREATION);
		Response response = client.post(createBeneficiaryReq);
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
	public void testforUpdateBeneficiarySuccessResponse() {
		WebClient client = getClient(BENEFICIARY_SERVICE_UPDATE);
		Response response = client.post(updateBeneficiaryReq);
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
		WebClient client = getClient(BENEFICIARY_SERVICE_DELETE);
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
	@Test(enabled = true)
	public void testGetMandatesDetailsSuccess() {
		WebClient client = getClient(BENEFICIARY_SERVICE_BENEFICIARIES_DETAILS);
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
	
	// Use this method for api authentication key generation
	private WebClient getClient(String path) {
		WebClient client = WebClient.create(ENDPOINT_ADDRESS);
		client.accept(MediaType.APPLICATION_JSON);
		client.type(MediaType.APPLICATION_JSON);
		client.path(path);
		String authorization = "user:admin:test";
		String encodedAuth = Base64.encodeBase64String(authorization.getBytes());
		client.header(CONSTANTS.AuthKey.get(), encodedAuth);
		return client;
	}
}