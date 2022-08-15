package com.pennanttech.pff.exceptions;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class InvalidDataException extends AppException {
	private static final long serialVersionUID = 3186647128799596015L;

	private String code;
	private String[] valueParam;

	public InvalidDataException(String code, String[] valueParam) {
		this.code = code;
		this.valueParam = valueParam;
	}

	public String getCode() {
		return code;
	}

	public String[] getValueParam() {
		return valueParam;
	}

	public ErrorDetail getErrorDetail() {
		return ErrorUtil.getErrorDetail(new ErrorDetail(code, valueParam));
	}

	public WSReturnStatus getWSReturnStatus() {
		WSReturnStatus status = new WSReturnStatus();
		status.setReturnCode(code);
		status.setReturnText("");

		ErrorDetail ed = ErrorUtil.getErrorDetailById(code);

		if (ed != null) {
			status.setReturnText(ErrorUtil.getErrorMessage(ed.getMessage(), valueParam));
		}

		return status;
	}
}
