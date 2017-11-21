package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfields.ExtendedField;
import com.pennant.backend.model.extendedfields.ExtendedFieldData;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.VASController;
import com.pennanttech.pffws.VASRestService;
import com.pennanttech.pffws.VASSoapService;
import com.pennanttech.ws.model.vas.VASRecordingDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class VASWebServiceImpl implements VASSoapService, VASRestService {

	private static final Logger		logger	= Logger.getLogger(VASWebServiceImpl.class);

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
		VASConfiguration vasConfiguration = null;
		try {
			// Mandatory validation
			if (StringUtils.isBlank(product)) {
				validationUtility.fieldLevelException();
			}

			// validate VasStructre with given productCode
			vasConfiguration = vASConfigurationService.getApprovedVASConfigurationByCode(product);
			if (vasConfiguration != null && vasConfiguration.isActive()) {
				if (vasConfiguration.getExtendedFieldHeader() != null) {
					vasConfiguration.setExtendedFieldDetailList(
							vasConfiguration.getExtendedFieldHeader().getExtendedFieldDetails());
				}
				vasConfiguration.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				vasConfiguration = new VASConfiguration();
				String[] valueParm = new String[2];
				valueParm[0] = "Product";
				valueParm[1] = product;
				vasConfiguration.setReturnStatus(APIErrorHandlerService.getFailedStatus("90701", valueParm));
			}
		} catch (Exception e) {
			logger.error(e);
			vasConfiguration = new VASConfiguration();
			vasConfiguration.setReturnStatus(APIErrorHandlerService.getFailedStatus());
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
		// bean validations
		validationUtility.validate(vasRecording, SaveValidationGroup.class);
		VASRecording response = null;
		try {
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
			response = vasController.recordVAS(vasRecording);
		} catch (Exception e) {
			logger.error(e);
			response = new VASRecording();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
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
		try {
			if (StringUtils.isBlank(vasRecording.getVasReference())) {
				returnStatus = new WSReturnStatus();
				String[] valueParm = new String[1];
				valueParm[0] = "vasReference";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			VASRecording vasDetails = vASRecordingService
					.getVASRecordingByRef(vasRecording.getVasReference(), "", true);
			if (vasDetails != null) {
				boolean validConfig = true;
				if (StringUtils.isNotBlank(vasRecording.getProductCode())
						&& !StringUtils.equals(vasRecording.getProductCode(), vasDetails.getProductCode())) {
					validConfig = false;
				}
				if (StringUtils.equals("Loan", vasRecording.getPostingAgainst())) {
					vasRecording.setPostingAgainst(VASConsatnts.VASAGAINST_FINANCE);
				}
				if (!(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, vasRecording.getPostingAgainst())
						|| StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, vasRecording.getPostingAgainst())
						|| StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, vasRecording.getPostingAgainst()))) {
					String[] valueParm = new String[2];
					valueParm[0] = "postingAgainst";
					valueParm[1] = vasRecording.getPostingAgainst();
					returnStatus = APIErrorHandlerService.getFailedStatus("90224", valueParm);
					return returnStatus;
				}
				if (StringUtils.isNotBlank(vasRecording.getPostingAgainst())
						&& !StringUtils.equals(vasRecording.getPostingAgainst(), vasDetails.getPostingAgainst())) {
					validConfig = false;
				}
				if (StringUtils.isNotBlank(vasRecording.getPrimaryLinkRef())
						&& !StringUtils.equals(vasRecording.getPrimaryLinkRef(), vasDetails.getPrimaryLinkRef())) {
					validConfig = false;
				}

				if (validConfig) {
					returnStatus = vasController.cancelVAS(vasDetails);
				} else {
					String[] valueParm = new String[1];
					returnStatus = APIErrorHandlerService.getFailedStatus("90267", valueParm);
				}
			} else {
				returnStatus = new WSReturnStatus();
				String[] valueParm = new String[1];
				valueParm[0] = "vas Reference:" + vasRecording.getVasReference();
				returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParm);
			}
		} catch (Exception e) {
			logger.error(e);
			returnStatus = new WSReturnStatus();
			returnStatus = APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return returnStatus;
	}

	@Override
	public VASRecording getRecordVAS(VASRecording vasRecording) throws ServiceException {
		logger.debug("Enetring");
		VASRecording response = null;
		try {
			if (StringUtils.isBlank(vasRecording.getVasReference())) {
				response = new VASRecording();
				String[] valueParm = new String[1];
				valueParm[0] = "vasReference";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
				return response;
			}
			response = vASRecordingService.getVASRecordingByRef(vasRecording.getVasReference(), "", false);

			if (response != null) {
				Map<String, Object> mapValues = response.getExtendedFieldRender().getMapValues();
				List<ExtendedField> parentextendedDetails = new ArrayList<ExtendedField>();
				List<ExtendedFieldData> extendedFieldDataList = new ArrayList<ExtendedFieldData>();

				for (Entry<String, Object> entry : mapValues.entrySet()) {
					ExtendedFieldData detail = new ExtendedFieldData();
					detail.setFieldName(entry.getKey());
					detail.setFieldValue(String.valueOf(entry.getValue()));
					extendedFieldDataList.add(detail);
				}
				ExtendedField extended = new ExtendedField();
				extended.setExtendedFieldDataList(extendedFieldDataList);
				parentextendedDetails.add(extended);
				response.setExtendedDetails(parentextendedDetails);
				if (StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, response.getPostingAgainst())) {
					response.setPostingAgainst("Loan");
				}
				boolean validConfig = true;
				if (StringUtils.isNotBlank(vasRecording.getProductCode())
						&& !StringUtils.equals(vasRecording.getProductCode(), response.getProductCode())) {
					validConfig = false;
				}
				if (StringUtils.isNotBlank(vasRecording.getPostingAgainst())
						&& !StringUtils.equals(vasRecording.getPostingAgainst(), response.getPostingAgainst())) {
					validConfig = false;
				}
				if (StringUtils.isNotBlank(vasRecording.getPrimaryLinkRef())
						&& !StringUtils.equals(vasRecording.getPrimaryLinkRef(), response.getPrimaryLinkRef())) {
					validConfig = false;
				}

				if (validConfig) {
					response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					return response;

				} else {
					response = new VASRecording();
					String[] valueParm = new String[1];
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90267", valueParm));
					return response;
				}

			} else {
				response = new VASRecording();
				String[] valueParm = new String[1];
				valueParm[0] = "vas Reference:" + vasRecording.getVasReference();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			}
		} catch (Exception e) {
			logger.error(e);
			response = new VASRecording();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}
	
	/**
	 * Fetch the Record VASRecording details by key field
	 * 
	 * @param vasRecording
	 * @return VASRecordingDetail
	 */
	@Override
	public VASRecordingDetail getVASRecordings(VASRecording vasRecording) throws ServiceException {
		
		VASRecordingDetail vASRecordingDetail= null;
		try {
			boolean isMutiValues = false;
			if (StringUtils.isBlank(vasRecording.getCif()) && StringUtils.isBlank(vasRecording.getFinReference())
					&& StringUtils.isBlank(vasRecording.getCollateralRef())) {
				vASRecordingDetail = new VASRecordingDetail();
				String[] valueParm = new String[1];
				vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90335", valueParm));
				return vASRecordingDetail;
			} else {
				if (StringUtils.isNotBlank(vasRecording.getCif())) {
					if (StringUtils.isNotBlank(vasRecording.getFinReference())
							|| StringUtils.isNotBlank(vasRecording.getCollateralRef())) {
						isMutiValues = true;
					}
					vasRecording.setPrimaryLinkRef(vasRecording.getCif());
				} else if (StringUtils.isNotBlank(vasRecording.getFinReference())) {
					if (StringUtils.isNotBlank(vasRecording.getCif())
							|| StringUtils.isNotBlank(vasRecording.getCollateralRef())) {
						isMutiValues = true;
					}
					vasRecording.setPrimaryLinkRef(vasRecording.getFinReference());
				} else if (StringUtils.isNotBlank(vasRecording.getCollateralRef())) {
					if (StringUtils.isNotBlank(vasRecording.getCif())
							|| StringUtils.isNotBlank(vasRecording.getFinReference())) {
						isMutiValues = true;
					}
					vasRecording.setPrimaryLinkRef(vasRecording.getCollateralRef());
				}
			}
			if(isMutiValues){
				vASRecordingDetail = new VASRecordingDetail();
				String[] valueParm = new String[1];
				vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90336", valueParm));
				return vASRecordingDetail;
			}
			vASRecordingDetail =vasController.getVASRecordings(vasRecording);
			
		} catch (Exception e) {
			logger.error(e);
			vASRecordingDetail = new VASRecordingDetail();
			vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		return vASRecordingDetail;
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
