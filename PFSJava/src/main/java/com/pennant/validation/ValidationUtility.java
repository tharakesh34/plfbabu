package com.pennant.validation;

import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;

/**
 * This is generic Validation utility for validating the Bean objects.
 * This utility will  load all the bean validation details and for the bean 
 * returns the violations if the bean data is not validated.
 */
@Component
public class ValidationUtility {
	private static Logger logger = Logger.getLogger(ValidationUtility.class);
	
	private Validator validator;
	private ErrorDetailService errorDetailService;
	
	private final int MAX_FAULT_MESSAGES = 10;

	/**
	 * 
	 * @throws Exception
	 */
	public ValidationUtility() throws Exception {
		Configuration<?> config = Validation.byDefaultProvider().configure();

		PathMatchingResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
		Resource[] resources = new Resource[0];
		try {
			resources = loader.getResources("classpath:/validations/*.xml");
			for (Resource resource : resources) {
			  config.addMapping(resource.getInputStream());
			}
			
			ValidatorFactory validatorFactory = config.buildValidatorFactory();
			validator = validatorFactory.getValidator();
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Object validate(Object object , Class groupName) throws ServiceException {

		Set<ConstraintViolation<Object>> violations = null;
		
		// validate request object using hibernate validator
		violations = validator.validate(object, groupName);
		
		int violationsSize = violations.size();
		int issueCount = 0;
		if (violationsSize != 0) {
			ServiceExceptionDetails serviceExceptionDetailsArray[] = new ServiceExceptionDetails[violationsSize];
			for (ConstraintViolation<Object> violation : violations) {
				String message = violation.getMessage();
				ServiceExceptionDetails serviceExceptionDetails = new ServiceExceptionDetails();
				String[] params = message.split("%%");
				if (params != null && params[0].length() == 5) {
					WSReturnStatus status = getErrorMesssage(params);
					serviceExceptionDetails.setFaultCode(status.getReturnCode());
					serviceExceptionDetails.setFaultMessage(status.getReturnText());
				} else {
					serviceExceptionDetails.setFaultCode("9009");
					serviceExceptionDetails.setFaultMessage(message);
				}
				serviceExceptionDetailsArray[issueCount] = serviceExceptionDetails;
				issueCount++;
				if(issueCount == MAX_FAULT_MESSAGES) {
					break;
				}
			}
			throw new ServiceException(serviceExceptionDetailsArray);
		}
		return object;
	}

	private WSReturnStatus getErrorMesssage(String[] params) {
		WSReturnStatus status = new WSReturnStatus();
		ErrorDetail errorDetail = errorDetailService.getErrorDetailById(params[0]);
		status.setReturnCode(params[0]);
		if(errorDetail != null) {
			status.setReturnText(errorDetail.getMessage());
			String errorMessage = "";
			if(params.length > 1) {
				errorMessage = getErrorMessage(errorDetail.getMessage(), params[1]);
				status.setReturnText(errorMessage);
			}
		} else {
			String errorMessage = "";
			status.setReturnText(errorMessage);
		}
		return status;
	}

	private static String getErrorMessage(String errorMessage, String errorParameters) {
		String error = StringUtils.trimToEmpty(errorMessage);

		if (errorParameters != null) {
				String parameter = StringUtils.trimToEmpty(errorParameters);
				error = error.replace("{" + (0) + "}", parameter);
		}

		for (int i = 0; i < 5; i++) {
			error = error.replace("{" + (i) + "}", "");
		}

		return error;
	}
	
	/**
	 * handling field level validations
	 * 
	 * @throws ServiceException
	 */
	public void fieldLevelException() throws ServiceException {
		ServiceExceptionDetails serviceExceptionDetailsArray[] = new ServiceExceptionDetails[1];
		ServiceExceptionDetails serviceExceptionDetails = new ServiceExceptionDetails();
		serviceExceptionDetails.setFaultCode("9009");
		serviceExceptionDetails.setFaultMessage("Requset Parameter should not be null");
		serviceExceptionDetailsArray[0] = serviceExceptionDetails;
		throw new ServiceException(serviceExceptionDetailsArray);
	}

	@Autowired
	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}
}
