package com.pennant.app.receiptUpload;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptUploadHeaderProcess {
	private static final Logger logger = LogManager.getLogger(ReceiptUploadHeaderProcess.class);
	private ReceiptService receiptService;

	public Map<String, Object> getAPIResponse(String headerMessageId, JSONObject reqJson, String url) {
		logger.debug(Literal.ENTERING);
		WebClient client = null;
		String returnText = null;
		String returnCode = null;
		String extraHeaderValue = null;
		Map<String, Object> responseVariables = new HashMap<>();
		try {

			client = getClient(url, headerMessageId);
			Response response = client.post(reqJson.toString());
			String body = response.readEntity(String.class);
			if (headerMessageId == null && StringUtils.isBlank(body)) {
				throw new RuntimeException(BatchUploadProcessorConstatnt.UNABLE_TO_PROCESS);
			}
			logger.info("MESSAGEID :: " + headerMessageId + "  API RESPONSE :: " + body);

			if (response.getStatus() == 200 && body != null) {
				JSONObject parentBody = new JSONObject(body);

				if (!parentBody.isNull(BatchUploadProcessorConstatnt.FIN_REFERENCE)) {
					extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.FIN_REFERENCE));
				} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.MANDATE_ID)) {
					extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.MANDATE_ID));
				} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID)) {
					extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID));
				} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.LIMIT_Id)) {
					extraHeaderValue = String.valueOf(parentBody.getString(BatchUploadProcessorConstatnt.LIMIT_Id));
				}

				parentBody = parentBody.getJSONObject(BatchUploadProcessorConstatnt.RETURN_STATUS);
				returnText = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_TEXT);
				returnCode = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_CODE);

			}
		} catch (Exception e) {
			returnText = e.toString();
			returnCode = PennantConstants.ERR_9999;
			logger.error(e);
		} finally {

			if (client != null) {
				client.close();
			}
			client = null;
		}

		responseVariables.put("ReturnCode", returnCode);
		responseVariables.put("ReturnText", returnText);
		responseVariables.put("ExtraHeaderValue", extraHeaderValue);

		logger.debug(Literal.LEAVING);

		return responseVariables;
	}

	public WebClient getClient(String serviceEndPoint, String messageId) {
		WebClient client = null;
		String authKey = SysParamUtil.getValueAsString("URLAuthorization");
		try {
			client = WebClient.create(serviceEndPoint);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header(BatchUploadProcessorConstatnt.AUTHORIZATION_KEY, authKey);
			client.header(BatchUploadProcessorConstatnt.MESSAGE_ID, messageId);

		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
		return client;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
