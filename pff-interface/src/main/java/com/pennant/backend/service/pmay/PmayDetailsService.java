package com.pennant.backend.service.pmay;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.pff.databind.JsonMapperUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.model.PMAYDetailsRespData;
import com.pennanttech.pff.model.PMAYRequest;
import com.pennanttech.pff.model.PMAYResponse;
import com.pennanttech.pff.model.PmayDetails;

public class PmayDetailsService {
	private static final Logger logger = LogManager.getLogger(PmayDetailsService.class);

	private static final String AUTHORIZATION_TOKEN = App.getProperty("pmay.authorization.token");
	private static final String REQUEST_URL = App.getProperty("pmay.client.request.url");
	private static final String RESPONSE_URL = App.getProperty("pmay.client.response.url");
	private static final String DFT_ERR_MSG = "Unable To Process Your Request Please Contact To System Administrator.";

	public PMAYResponse ProcessRequest(PMAYRequest pmayRequest) {
		logger.debug(Literal.ENTERING);

		List<PmayDetails> pmayDetails = pmayRequest.getPmayDetails();
		String req = getRequestString(pmayDetails);
		logger.debug("Request Body \n {}", req);

		WebClient client = null;

		try {
			client = getClient(REQUEST_URL);
			Response response = client.post(req);
			String body = response.readEntity(String.class);

			logger.debug("Response Body \n {}", body);

			if (StringUtils.isBlank(body)) {
				throw new InterfaceException("PMAY", DFT_ERR_MSG);
			}

			JAXBContext jaxbContext = JAXBContext.newInstance(PMAYResponse.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			logger.debug(Literal.LEAVING);
			return (PMAYResponse) jaxbUnmarshaller.unmarshal(new StringReader(body));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			close(client);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public PMAYResponse getPmayResponse(String recordId) {
		logger.debug(Literal.ENTERING);

		String req = getRequestString(recordId);
		logger.debug("Request Body \n {}", req);

		WebClient client = null;

		try {
			client = getClient(RESPONSE_URL);
			Response response = client.post(req);
			String body = response.readEntity(String.class);

			logger.debug("Response Body \n {}", body);

			if (StringUtils.isBlank(body)) {
				throw new InterfaceException("PMAY", DFT_ERR_MSG);
			}

			ObjectMapper customObjectMapper = new ObjectMapper();
			JSONObject jsonObject = new JSONObject(body);

			PMAYResponse pmayResponse = new PMAYResponse();

			try {
				String pmayDetails = jsonObject.get("pmayDetailsRespData").toString();
				PMAYDetailsRespData respData = customObjectMapper.readValue(pmayDetails, PMAYDetailsRespData.class);
				pmayResponse.setPmayDetailsRespData(respData);

			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}

			logger.debug(Literal.LEAVING);
			return pmayResponse;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			close(client);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	public String getRequestString(Object requestData) {
		logger.debug(Literal.ENTERING);

		ObjectMapper mapper = JsonMapperUtil.objectMapper();

		try {
			return mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in json request string {}", e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	private WebClient getClient(String url) {
		logger.debug(Literal.ENTERING);

		logger.debug("AuthorizationValue {}", AUTHORIZATION_TOKEN);
		logger.debug("PmayAPIUrl {}", url);

		WebClient client = null;

		try {
			client = WebClient.create(url);
			client.accept(MediaType.APPLICATION_JSON_VALUE);
			client.type(MediaType.APPLICATION_JSON_VALUE);
			client.header("Authorization", AUTHORIZATION_TOKEN);
			client.header("MessageId", String.valueOf(Math.random()));

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return client;
	}

	private void close(WebClient client) {
		if (client != null) {
			client.close();
		}
	}
}
