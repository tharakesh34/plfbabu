package com.pennant.pff.api.service;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.log.model.APILogDetail;

public class AbstractService {
	protected final Logger logger = LogManager.getLogger(this.getClass());

	private ErrorDetailService errorDetailService;

	public AbstractService() {
		super();
	}

	protected WSReturnStatus getSuccessStatus() {
		WSReturnStatus status = new WSReturnStatus();

		status.setReturnCode(APIConstants.RES_SUCCESS_CODE);
		status.setReturnText(APIConstants.RES_SUCCESS_DESC);
		return status;
	}

	protected WSReturnStatus getFailedStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(APIConstants.RES_FAILED_CODE);
		status.setReturnText(APIConstants.RES_FAILED_DESC);
		return status;
	}

	protected WSReturnStatus getFailedReturnStatus(String code, String message) {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(code);
		status.setReturnText(message);
		return status;
	}

	protected WSReturnStatus getFailedStatus(String code, String... param) {
		WSReturnStatus status = new WSReturnStatus();
		ErrorDetail ed = errorDetailService.getErrorDetailById(code);
		status.setReturnCode(code);
		if (ed != null) {
			String errorMessage = getErrorMessage(ed.getMessage(), param);
			status.setReturnText(errorMessage);
		} else {
			String errorMessage = "";
			status.setReturnText(errorMessage);
		}
		return status;
	}

	protected void logKeyFields(String... keyFields) {
		APILogDetail apiLog = getAPILog();

		if (apiLog == null || keyFields == null) {
			return;
		}

		String keyFieldsData = "";
		for (String field : keyFields) {
			if (StringUtils.isNotBlank(field)) {
				keyFieldsData = keyFieldsData + field + ",";
			}
		}

		if (StringUtils.isNotBlank(keyFieldsData)) {
			apiLog.setKeyFields(keyFieldsData.substring(0, keyFieldsData.length() - 1));
		}
	}

	protected void logReference(String reference) {
		APILogDetail apiLog = getAPILog();
		if (apiLog != null) {
			apiLog.setReference(reference);
		}
	}

	private APILogDetail getAPILog() {
		return (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
	}

	private String getErrorMessage(String errorMessage, String[] errorParameters) {
		String error = StringUtils.trimToEmpty(errorMessage);

		if (errorParameters != null) {
			for (int i = 0; i < errorParameters.length; i++) {
				String parameter = StringUtils.trimToEmpty(errorParameters[i]);
				error = error.replace("{" + (i) + "}", parameter);
			}
		}

		for (int i = 0; i < 5; i++) {
			error = error.replace("{" + (i) + "}", "");
		}

		return error;
	}

	@Autowired
	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}

}
