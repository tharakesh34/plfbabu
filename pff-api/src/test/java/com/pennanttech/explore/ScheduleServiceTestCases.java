package com.pennanttech.explore;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

public class ScheduleServiceTestCases {

	private static final String ENDPOINT_ADDRESS = "http://192.168.1.40:8080/pff-api/services/financeScheduleRest";
	private static final String WADL_ADDRESS = ENDPOINT_ADDRESS + "?_wadl";
	private static final String LOANSCHEDULE_SERVICE_PATH = "/loanSchedule";
	private static final String LOANSCHEDULE_SERVICE_FINREFERENCE_DETAILS = LOANSCHEDULE_SERVICE_PATH
			+ "/getLoanInquiry/PB1509901290";

	private static final String LOANSCHEDULE_SERVICE_CREATION = LOANSCHEDULE_SERVICE_PATH + "/createLoanSchedule";

	private final String createLoanSucessReq = "{\"financeDetail\": {\"cif\": \"\",\"finType\": \"PL001\",\"finCcy\": \"INR\",\"finBranch\": \"\",\"profitDaysBasis\": \"A/A_365F\",\"finAmount\": 10000000,\"finAssetValue\": 10000000,\"downPayBank\": 0,\"downPaySupl\": 0,\"finStartDate\": \"2015-04-05T00:00:00\",\"allowGrcPeriod\": true,\"tdsApplicable\": false,\"manualSchedule\": false,\"planDeferCount\": 0,\"stepFinance\": false,\"alwManualSteps\": false,\"grcTerms\": 6,\"grcPeriodEndDate\": \"2015-04-05T00:00:00\",\"grcRateBasis\": \"R\",\"grcPftRate\": 21,\"grcMargin\": 0,\"grcProfitDaysBasis\": \"A/A_365F\",\"grcPftFrq\": \"M0005\",\"nextGrcPftDate\": \"2015-04-05T00:00:00\",\"grcPftRvwFrq\": \"\",\"nextGrcPftRvwDate\": \"2015-04-05T00:00:00\",\"grcCpzFrq\": \"\",\"nextGrcCpzDate\": \"2015-09-05T00:00:00\",\"allowGrcRepay\": true,\"grcSchdMthd\": \"PFT\",\"grcMinRate\": 0,\"grcMaxRate\": 0,\"grcAdvPftRate\": 0,\"grcAdvMargin\": 0,\"numberOfTerms\": 12,\"reqRepayAmount\": 0,\"repayRateBasis\": \"R\",\"repayPftRate\": 21,\"repayMargin\": 0,\"scheduleMethod\": \"EQUAL\",\"repayFrq\": \"M0005\",\"nextRepayDate\": \"2015-06-05T00:00:00\",\"repayPftFrq\": \"M0005\",\"nextRepayPftDate\": \"2015-05-05T00:00:00\",\"repayRvwFrq\": \"\",\"nextRepayRvwDate\": \"2015-10-05T00:00:00\",\"repayCpzFrq\": \"\",\"nextRepayCpzDate\": \"2015-12-05T00:00:00\",\"maturityDate\": \"2016-04-05T00:00:00\",\"finRepayPftOnFrq\": false,\"repayMinRate\": 0,\"repayMaxRate\": 0,\"repayAdvPftRate\": 0,\"repayAdvMargin\": 0,\"supplementRent\": 0,\"increasedCost\": 0},\"fees\": [{\"feeCode\": \"PROCFEE\",\"feeAmount\": 750,\"waiverAmount\": 0,\"paidAmount\": 0}] }";

	@Test(enabled = false)
	public void testGetFinRefPB1509901290DetailsSuccess() {
		WebClient client = getClient(LOANSCHEDULE_SERVICE_FINREFERENCE_DETAILS);
		Response response = client.get();
		Map<String, Object> payload = convertStringToMap(response.readEntity(String.class));
		String finRef = payload.get("finReference").toString();
		List<?> schedule = (List<?>) payload.get("schedules");
		int scheduleSize = schedule.size();
		assertEquals("Response status 200 ", 200, response.getStatus());
		assertEquals("Response FinRef", "PB1509901290", finRef);
		assertEquals("ScheduleCount", 19, scheduleSize);
	}

	@Test(enabled = false)
	public void testforCreateLoanSchduleGetSucessResponse() {
		WebClient client = getClient(LOANSCHEDULE_SERVICE_CREATION);
		Response response = client.post(createLoanSucessReq);
		assertEquals("Response status 200", 200, response.getStatus());
	}

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

	private static Map<String, Object> convertStringToMap(String payload) {
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