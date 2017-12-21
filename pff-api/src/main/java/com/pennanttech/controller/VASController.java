package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.service.impl.VASWebServiceImpl;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.vas.VASRecordingDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class VASController {
	private static final Logger		logger	= Logger.getLogger(VASWebServiceImpl.class);
	private VASRecordingService		vASRecordingService;
	private VASConfigurationService	vASConfigurationService;

	/**
	 * Method for used to add create new VAS based on VAS configuration in PLF system.
	 * 
	 * @param vasRecording
	 * @throws ServiceException
	 */
	public VASRecording recordVAS(VASRecording vasRecording) {
		logger.debug("Entering");
		VASRecording response = null;
		try {
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			vasRecording.setUserDetails(userDetails);
			vasRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vasRecording.setSourceId(APIConstants.FINSOURCE_ID_API);
			vasRecording.setNewRecord(true);
			vasRecording.setVasStatus("N");
			vasRecording.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			vasRecording.setLastMntBy(userDetails.getUserId());
			vasRecording.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			vasRecording.setVersion(1);
			VASConfiguration vasConfiguration = vASConfigurationService.getApprovedVASConfigurationByCode(vasRecording
					.getProductCode());
			vasRecording.setVasConfiguration(vasConfiguration);
			vasRecording.setVasReference(ReferenceUtil.generateVASRef());
			vasRecording.setPaidAmt(vasRecording.getFee().subtract(vasRecording.getWaivedAmt()));
			if(vasRecording.getDocuments() != null){
			for (DocumentDetails detail : vasRecording.getDocuments()) {
				detail.setRecordType(PennantConstants.RCD_ADD);
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				detail.setDocModule(VASConsatnts.MODULE_NAME);
				detail.setNewRecord(true);
			}
			}
			// process Extended field details
			List<ExtendedField> extendedFields = vasRecording.getExtendedDetails();
			if (extendedFields != null) {
				int seqNo = 0;
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(vasRecording.getVasReference());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getUserId());
				exdFieldRender.setSeqNo(++seqNo);
				exdFieldRender.setNewRecord(true);
				exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				exdFieldRender.setVersion(1);
				for (ExtendedField extendedField : extendedFields) {

					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}

				}
				if(extendedFields.size()<=0){
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}

				vasRecording.setExtendedFieldRender(exdFieldRender);
			} else {
				vasRecording.setExtendedFieldRender(null);
			}
			//FIXME 

			/*
			 * if(StringUtils.isNotEmpty(vasConfiguration.getPostValidation())){ Map<String, Object>
			 * map=vasRecording.getExtendedFieldRender().getMapValues(); ScriptErrors postValidationErrors =
			 * scriptValidationService.getPostValidationErrors(vasConfiguration.getPostValidation(), map);
			 * List<ScriptError> errors=postValidationErrors.getAll(); for(ScriptError error:errors ){ error.getCode();
			 * error.getValue(); } }
			 */

			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(vasRecording, PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = vASRecordingService.doApprove(auditHeader);
			response = new VASRecording();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
				}
			} else {
				response.setVasReference(vasRecording.getVasReference());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
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
	public WSReturnStatus cancelVAS(VASRecording vasDetails) {
		logger.debug("Entering");
		
		WSReturnStatus response = new WSReturnStatus();
		try {
			//set the default values for mandate 
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			vasDetails.setUserDetails(userDetails);
			vasDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			vasDetails.setSourceId(APIConstants.FINSOURCE_ID_API);
			vasDetails.setNewRecord(false);
			vasDetails.setVersion(vasDetails.getVersion() + 1);
			vasDetails.setVasStatus("C");
			vasDetails.setExtendedFieldRender(null);

			//get the header details from the request
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(vasDetails, PennantConstants.TRAN_WF);
			//set the headerDetails to AuditHeader
			auditHeader.setApiHeader(reqHeaderDetails);

			// call doApprove service method
			auditHeader = vASRecordingService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
				}
			} else {

				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
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
	public VASRecordingDetail getVASRecordings(VASRecording vasRecording) {
		logger.debug("Entering");
		VASRecordingDetail vASRecordingDetail = null;
		try {
			List<VASRecording> vasRecordingList;
			vasRecordingList = vASRecordingService.getVasRecordingsByPrimaryLinkRef(vasRecording.getPrimaryLinkRef());
			if (vasRecordingList != null && !vasRecordingList.isEmpty()) {
				vASRecordingDetail = new VASRecordingDetail();
				vASRecordingDetail.setVasRecordingList(vasRecordingList);
				vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				vASRecordingDetail = new VASRecordingDetail();
				String[] valueParm = new String[1];
				valueParm[0] = vasRecording.getPrimaryLinkRef();
				vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			vASRecordingDetail = new VASRecordingDetail();
			vASRecordingDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return vASRecordingDetail;
	}
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDocument
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(VASRecording aVASRecording, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASRecording.getBefImage(), aVASRecording);
		return new AuditHeader(String.valueOf(aVASRecording.getId()), String.valueOf(aVASRecording.getId()), null,
				null, auditDetail, aVASRecording.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public void setvASConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vASConfigurationService = vASConfigurationService;
	}
	

}
