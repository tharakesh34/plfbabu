package com.pennanttech.interceptor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;

import com.pennant.app.util.APIHeader;

/**
 * REST Interceptor for outgoing responses from the applicaiton.
 * 1. Set all the header details from the request to the response.
 * 2. set response time in response.
 * 
 * @author pennant
 *
 */
public class RestOutHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

	/*
	 * Constructor
	 */
	public RestOutHeaderInterceptor() {
		super(Phase.POST_LOGICAL);
	}

	// Public methods
	
	public void handleMessage(Message message) throws Fault {
		
		@SuppressWarnings("unchecked")
		Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
		APIHeader header = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		if (headers == null) {
			headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
			message.put(Message.PROTOCOL_HEADERS, headers);
		}
		
		if (header != null) {
			// ReqHeaderDetails header = responseBean.getHeader();
			if (StringUtils.isNotBlank(header.getLanguage())) {
				headers.put(APIHeader.API_LANGUAGE, Arrays.asList(header.getLanguage()));
			}
			if (StringUtils.isNotBlank(header.getServiceVersion())) {
				headers.put(APIHeader.API_SERVICEVERSION, Arrays.asList(header.getServiceVersion()));
			}
			/*if (header.getAdditionalInfo() != null) {
				headers.put("additionalInfo", Arrays.asList(header.getAdditionalInfo().toString()));
			}*/
			if (StringUtils.isNotBlank(header.getServiceName())) {
				headers.put(APIHeader.API_SERVICENAME, Arrays.asList(header.getServiceName()));
			}
			if (StringUtils.isNotBlank(header.getMessageId())) {
				headers.put(APIHeader.API_MESSAGEID, Arrays.asList(header.getMessageId()));
			}
			if (header.getRequestTime() != null) {
				headers.put(APIHeader.API_REQ_TIME, Arrays.asList(header.getRequestTime().toString()));
			}
			if (StringUtils.isNotBlank(header.getSecurityInfo())) {
				headers.put("Token", Arrays.asList(header.getSecurityInfo().toString()));
			}
			if (StringUtils.isNotBlank(header.getReturnCode())){
			headers.put(APIHeader.API_RETURNCODE, Arrays.asList(header.getReturnCode()));
			}
			if (StringUtils.isNotBlank(header.getReturnDesc())){
			headers.put(APIHeader.API_RETURNDESC, Arrays.asList(header.getReturnDesc()));
			}
			headers.put(APIHeader.API_RES_TIME, Arrays.asList(new Date().toString()));
		}
	}
}
