package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.VASController;
import com.pennanttech.pffws.VASRestService;
import com.pennanttech.pffws.VASSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class VASWebServiceImpl implements VASSoapService, VASRestService {

	private final static Logger		logger	= Logger.getLogger(VASWebServiceImpl.class);

	private ValidationUtility		validationUtility;
	private VASConfigurationService	vASConfigurationService;
	private VASRecordingService		vASRecordingService;
	private VASController			vasController;

	/**
	 * it fetches the approved records from the VasStructure.
	 * 
	 * @param id
	 *            (String)
	 * @return VASConfiguration
	 */
	@Override
	public VASConfiguration getVASProduct(String product) throws ServiceException {
		logger.debug("Enetring");

		// Mandatory validation
		if (StringUtils.isBlank(product)) {
			validationUtility.fieldLevelException();
		}

		VASConfiguration vasConfiguration = null;

		// validate VasStructre with given productCode
		vasConfiguration = vASConfigurationService.getApprovedVASConfigurationByCode(product);
		if (vasConfiguration != null) {
			if (vasConfiguration.getExtendedFieldHeader() != null) {
				vasConfiguration.setExtendedFieldDetailList(vasConfiguration.getExtendedFieldHeader()
						.getExtendedFieldDetails());
			}
			vasConfiguration.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			vasConfiguration = new VASConfiguration();
			String[] valueParm = new String[1];
			valueParm[0] = product;
			vasConfiguration.setReturnStatus(APIErrorHandlerService.getFailedStatus("90101", valueParm));
		}

		logger.debug("Leaving");
		return vasConfiguration;
	}

	/**
	 * Method for used to add create new VAS based on VAS configuration in PLF system.
	 * 
	 * @param vasRecording
	 * @throws ServiceException
	 */
	@Override
	public VASRecording recordVAS(VASRecording vasRecording) throws ServiceException {
		logger.debug("Enetring");
		// validate recordVAS details as per the API specification
		VASRecording response;
		AuditDetail auditDetail = vASRecordingService.doValidations(vasRecording);

		if (auditDetail.getErrorDetails() != null) {
			for (ErrorDetails errorDetail : auditDetail.getErrorDetails()) {
				response = new VASRecording();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
				return response;
			}
		}

		// call create Loan Flags service
		VASRecording returnStatus = vasController.recordVAS(vasRecording);
		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Method for used to cancel the VAS in PLF system.
	 * 
	 * @param vasRecording
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus cancelVAS(VASRecording vasRecording) throws ServiceException {
		logger.debug("Enetring");
		WSReturnStatus returnStatus = null;
		if(StringUtils.isBlank(vasRecording.getVasReference())){
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = "vasReference";
			 returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}
		VASRecording vasDetails = vASRecordingService.getVASRecordingByRef(vasRecording.getVasReference(),"",true);
		if (vasDetails != null) {
			boolean validConfig = true;
			if (StringUtils.isNotBlank(vasRecording.getProductCode())
					&& !StringUtils.equals(vasRecording.getProductCode(), vasDetails.getProductCode())) {
				validConfig = false;
			}
			if (StringUtils.isNotBlank(vasRecording.getPostingAgainst())
					&& !StringUtils.equals(vasRecording.getPostingAgainst(), vasDetails.getPostingAgainst())) {
				validConfig = false;
			}
			if (StringUtils.isNotBlank(vasRecording.getPrimaryLinkRef())
					&& !StringUtils.equals(vasRecording.getPrimaryLinkRef(), vasDetails.getPrimaryLinkRef())) {
				validConfig = false;
			}
			
			if(validConfig) {
				returnStatus = vasController.cancelVAS(vasDetails);
			} else {
				String[] valueParm = new String[1];
				returnStatus = APIErrorHandlerService.getFailedStatus("90267", valueParm);
			}
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = "vas Reference:"+vasRecording.getVasReference();
			returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParm);
		}
		logger.debug("Leaving");
		return returnStatus;
	}

	@Override
	public WSReturnStatus getRecordVAS(VASConfiguration vasConfiguration) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}

	@Autowired
	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	@Autowired
	public void setVasController(VASController vasController) {
		this.vasController = vasController;
	}
}
