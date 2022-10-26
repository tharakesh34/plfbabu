package com.pennant.pff.document;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennant.pff.document.service.DocumentValidation;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;

@Component
public class DocVerificationUtil {
	private static final Logger logger = LogManager.getLogger(DocVerificationUtil.class);

	private static DocumentValidation defaultDocumentValidation;
	private static DocumentValidation customDocumentValidation;

	public DocVerificationUtil() {
		super();
	}

	public static boolean isVerified(String docNumber) {
		if (getDocumentValidation() == null) {
			return false;
		}
		return getDocumentValidation().isVerified(docNumber);
	}

	public static ErrorDetail doValidatePAN(DocVerificationHeader header, boolean reVerify) {
		ErrorDetail error = null;
		String panNumber = header.getDocNumber();

		if (StringUtils.isEmpty(panNumber)) {
			logger.info("PAN Number is empty");
			error = new ErrorDetail();
			error.setMessage("PAN Number is empty");
			return error;
		}

		if (getDocumentValidation() == null) {
			logger.info("There is no interface to validate PAN Number.");
			error = new ErrorDetail();
			error.setMessage("There is no interface to validate PAN Number.");
			return error;
		}

		if (!reVerify && getDocumentValidation().isVerified(panNumber)) {
			logger.info("PAN Number already verified");
			error = new ErrorDetail();
			error.setMessage("There is no interface to validate PAN Number.");
			return error;
		}

		try {
			getDocumentValidation().validate(DocType.PAN, header);
		} catch (InterfaceException ife) {
			error = new ErrorDetail();
			error.setCode(ife.getErrorCode());
			error.setMessage(ife.getErrorMessage());
		}

		return error;
	}

	public static DocumentValidation getDocumentValidation() {
		return customDocumentValidation == null ? defaultDocumentValidation : customDocumentValidation;
	}

	@Autowired
	public void setDefaultDocumentValidation(DocumentValidation defaultDocumentValidation) {
		DocVerificationUtil.defaultDocumentValidation = defaultDocumentValidation;
	}

	@Autowired(required = false)
	@Qualifier("customDocumentValidation")
	public void setCustomDocumentValidation(DocumentValidation customDocumentValidation) {
		DocVerificationUtil.customDocumentValidation = customDocumentValidation;
	}

}
