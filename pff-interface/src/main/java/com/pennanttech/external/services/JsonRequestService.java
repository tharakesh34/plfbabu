package com.pennanttech.external.services;

import java.util.Map.Entry;

import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;

import com.pennant.pff.model.json.JsonRequestDetail;

public class JsonRequestService extends JsonService {

	public Object sendRequest(JsonRequestDetail request) {

		JsonServiceDetail jsonServiceDetail = new JsonServiceDetail();

		jsonServiceDetail.setServiceUrl(request.getServiceUrl());
		jsonServiceDetail.setServiceEndPoint(request.getServiceEndpointUrl());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(request.getContentType());

		if (request.getAuthentcationToken() != null) {
			headers.setBearerAuth(request.getAuthentcationToken());
		}

		if (request.getHeaderMap() != null) {
			for (Entry<String, String> entry : request.getHeaderMap().entrySet()) {
				headers.add(entry.getKey(), entry.getValue());
			}
		}

		jsonServiceDetail.setCertificateFileName(request.getFileName());
		jsonServiceDetail.setCertificatePassword(request.getPassword());
		jsonServiceDetail.setHeaders(headers);
		jsonServiceDetail.setExcludeNull(true);
		jsonServiceDetail.setExcludeEmpty(true);
		jsonServiceDetail.setMethod(request.getMethod());

		// default
		jsonServiceDetail.setServiceName(request.getServiceName());
		jsonServiceDetail.setReference(request.getReference());

		doSetProperties(READ_TIMEOUT, CONNECTION_TIMEOUT);

		jsonServiceDetail.setRequestString(request.getRequestedString());
		JsonServiceDetail detail = processMessage(jsonServiceDetail);
		String response = detail.getResponseString();
		logger.info("Upload doc response " + response);
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = (Object) parser.parse(response);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
