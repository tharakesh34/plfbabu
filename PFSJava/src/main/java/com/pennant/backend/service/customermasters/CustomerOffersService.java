package com.pennant.backend.service.customermasters;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.pennant.app.util.CustomObjectMapper;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.crm.CrmLeadDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CustomerOffersService {

	private static final Logger logger = LogManager.getLogger(CustomerOffersService.class);

	public static String customerOffersServiceUrl = App.getProperty("CustomerOffersServiceUrl");
	public static String customerOffersAuthorization = App.getProperty("CustomerOffersAuthorization");

	public CrmLeadDetails processRequest(String custCif) {
		logger.debug(Literal.ENTERING);

		CrmLeadDetails body = null;
		double random = Math.random() * 49 + 1 + DateUtil.getTimestamp(SysParamUtil.getAppDate()).getTime();
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = PennantApplicationUtil.getTemplate();
		String uri = customerOffersServiceUrl + custCif;
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.set("Authorization", customerOffersAuthorization);
		headers.set("ENTITYID", "1");
		headers.set("Content-Type", "application/json");
		headers.set("ServiceName", "getLeadDetailsByCif");
		headers.set("Language", PennantConstants.default_Language);
		headers.set("RequestTime", LocalDateTime.now().toString());// "2018-02-01T00:00:00"
		headers.set("ServiceVersion", "1");
		headers.set("MESSAGEID", random + "");

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> respEntity = null;
		try {
			respEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return body;
		}

		CustomObjectMapper customObjectMapper = new CustomObjectMapper();
		try {
			body = customObjectMapper.readValue(respEntity.getBody(), CrmLeadDetails.class);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return body;
	}
}
