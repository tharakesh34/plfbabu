package com.pennant.backend.service.pmay;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.model.PMAYDetailsRespData;
import com.pennanttech.pff.model.PMAYRequest;
import com.pennanttech.pff.model.PMAYResponse;
import com.pennanttech.pff.model.PmayDetails;

public class PmayDetailsService {

	private static final Logger logger = Logger.getLogger(PmayDetailsService.class);

	static final String authorization = App.getProperty("pmayAuthorizationToken");

	public PMAYResponse ProcessRequest(PMAYRequest pmayRequest) {
		logger.debug(Literal.ENTERING);
		
		List<PmayDetails> pmayDetails = pmayRequest.getPmayDetails();
		String req = getRequestString(pmayDetails);
		logger.debug("Request Body \n" + req);
		PMAYResponse PMAYResponse = null;
		WebClient client = null;
		try {
			String pmayurl = SysParamUtil.getValueAsString(SMTParameterConstants.PMAY_URL_INTIAL_REQUEST);

			client = getClient(pmayurl);
			Response response = client.post(req);
			String body = response.readEntity(String.class);
			logger.debug("Response Body" + body);
			if (StringUtils.isBlank(body)) {
				throw new RuntimeException("Unable To Process Your Request Please Contact To System Administrator.");
			}
			JAXBContext jaxbContext = JAXBContext.newInstance(PMAYResponse.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			PMAYResponse = (PMAYResponse) jaxbUnmarshaller.unmarshal(new StringReader(body));
			return PMAYResponse;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logger.debug(Literal.LEAVING);
			e.printStackTrace();
			return PMAYResponse;
		}

	}

	public PMAYResponse getPmayResponse(String recordId) {
		logger.debug(Literal.ENTERING);
		String req = getRequestString(recordId);
		logger.debug("Request Body \n" + req);
		PMAYResponse PMAYResponse = new PMAYResponse();
		WebClient client = null;
		try {
			String pmayRespurl = SysParamUtil.getValueAsString(SMTParameterConstants.PMAY_URL_RESPONSE);
			client = getClient(pmayRespurl);
			Response response = client.post(req);
			String body = response.readEntity(String.class);
			logger.debug("Response Body" + body);
			if (StringUtils.isBlank(body)) {
				throw new RuntimeException("Unable To Process Your Request Please Contact To System Administrator.");
			}
			ObjectMapper customObjectMapper = new ObjectMapper();
			JSONObject j = new JSONObject(body);
			try {
				PMAYDetailsRespData v = customObjectMapper.readValue(j.get("pmayDetailsRespData").toString(),
						PMAYDetailsRespData.class);
				PMAYResponse.setPmayDetailsRespData(v);
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
				return PMAYResponse;
			}
			return PMAYResponse;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logger.debug(Literal.LEAVING);
			e.printStackTrace();
			return PMAYResponse;
		}

	}

	public String getRequestString(Object requestData) {
		logger.debug(Literal.ENTERING);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		DateFormat dateFormat = new SimpleDateFormat(PennantConstants.APIDateFormatter);
		dateFormat.setLenient(false);
		mapper.setDateFormat(dateFormat);
		String jsonInString = null;

		try {
			jsonInString = mapper.writeValueAsString(requestData);
		} catch (Exception e) {
			logger.error("Exception in json request string" + e);
		}
		logger.debug(Literal.LEAVING);
		return jsonInString;
	}

	private WebClient getClient(String url) {
		logger.debug(Literal.ENTERING);

		logger.debug("AuthorizationValue " + authorization);
		logger.debug("PmayAPIUrl " + url);
		WebClient client = null;
		try {
			client = WebClient.create(url);
			client.accept(MediaType.APPLICATION_JSON_VALUE);
			client.type(MediaType.APPLICATION_JSON_VALUE);
			client.header("Authorization", authorization);
			client.header("MessageId", String.valueOf(Math.random()));

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return client;
	}
}
