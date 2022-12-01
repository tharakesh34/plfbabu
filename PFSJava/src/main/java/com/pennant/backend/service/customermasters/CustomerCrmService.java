package com.pennant.backend.service.customermasters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.pennant.app.util.CustomObjectMapper;
import com.pennant.backend.model.crm.CrmDetails;
import com.pennant.backend.model.crm.ResponseData;
import com.pennant.backend.model.crm.ReturnStatus;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerCrmService {
	private static final Logger logger = LogManager.getLogger(CustomerCrmService.class);

	static final String authorization = App.getProperty("CrmAuthorizationToken");
	static final String Crmurl = App.getProperty("Crmurl");

	public ResponseData ProcessRequest(CrmDetails crmDetails) {
		logger.debug(Literal.ENTERING);
		ResponseData body = null;
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = PennantApplicationUtil.getTemplate();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.set("Authorization", authorization);

		Map<String, String> map = new HashMap<String, String>();
		map.put("cif", crmDetails.getCustCif());
		map.put("product", crmDetails.getProduct());
		map.put("productType", crmDetails.getFinType());
		map.put("requestType", crmDetails.getOrigin());
		map.put("loanAccount", crmDetails.getRelationshipNumber());
		map.put("description", crmDetails.getDescription());
		map.put("multiPartFile", crmDetails.getFileData());
		map.put("contentType", crmDetails.getContentType());
		map.put("name", crmDetails.getFileName());

		HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<String> respEntity = null;

		try {
			respEntity = restTemplate.exchange(Crmurl, HttpMethod.POST, request, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getResponseData();
		}

		CustomObjectMapper customObjectMapper = new CustomObjectMapper();
		try {
			body = customObjectMapper.readValue(respEntity.getBody(), ResponseData.class);
		} catch (IOException e) {

			logger.error(Literal.EXCEPTION, e);
			return getResponseData();
		}
		logger.debug(Literal.LEAVING);
		return body;
	}

	private ResponseData getResponseData() {
		ReturnStatus returnStatus = new ReturnStatus();
		returnStatus.setReturnText("Request Failed");
		ResponseData body = new ResponseData();
		body.setCaseResponse(null);
		body.setReturnStatus(returnStatus);
		return body;
	}
}
