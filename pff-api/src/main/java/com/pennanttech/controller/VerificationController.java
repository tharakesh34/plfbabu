/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  VerificationController.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-02-2021    														*
 *                                                                  						*
 * Modified Date    :  08-02-2021    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-02-2021       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.LegalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.RiskContainmentUnitDAO;
import com.pennanttech.pennapps.pff.verification.dao.TechnicalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.FieldInvestigationService;
import com.pennanttech.pennapps.pff.verification.service.LegalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.PersonalDiscussionService;
import com.pennanttech.pennapps.pff.verification.service.RiskContainmentUnitService;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class VerificationController {

	private static Logger logger = Logger.getLogger(VerificationController.class);

	private VerificationService verificationService;
	private FieldInvestigationService fieldInvestigationService;
	private static ExtFieldConfigService extFieldConfigService;
	private PersonalDiscussionService personalDiscussionService;
	private TechnicalVerificationDAO technicalVerificationDAO;
	private TechnicalVerificationService technicalVerificationService;
	private RiskContainmentUnitService riskContainmentUnitService;
	private RiskContainmentUnitDAO riskContainmentUnitDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private LegalVerificationDAO legalVerificationDAO;
	private LegalVerificationService legalVerificationService;

	/**
	 * @param financeDetail
	 * @return {@link Verification}
	 */
	public Verification createFIInitaion(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification verification = new Verification();
		// prepare verificationd data
		setVerificationData(financeDetail, verificationType);
		// call service to save data
		try {
			verificationService.saveOrUpdate(financeDetail, VerificationType.FI, "W", true);
		} catch (Exception e) {
			verification.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		verification.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return verification;
	}

	/**
	 * @param financeDetail
	 * 
	 * @param verificationType
	 */
	private void setVerificationData(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification verification = null;
		if (verificationType == VerificationType.FI) {
			verification = financeDetail.getFiVerification();
		}
		if (verificationType == VerificationType.PD) {
			verification = financeDetail.getPdVerification();
		}
		if (verificationType == VerificationType.RCU) {
			verification = financeDetail.getRcuVerification();
		}
		List<Verification> verificationsList = verification.getVerifications();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		verification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		verification.setVersion(1);
		verification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		verification.setUserDetails(userDetails);
		verification.setLastMntBy(userDetails.getUserId());
		verification.setCreatedBy(userDetails.getUserId());

		for (Verification vrf : verificationsList) {
			if (CollectionUtils.isNotEmpty(jountAccountDetailList)) {
				for (JointAccountDetail jointAccountDetail : jountAccountDetailList) {
					if (jointAccountDetail.getCustCIF().equals(vrf.getCif())) {
						vrf.setReference(jointAccountDetail.getCustCIF());
						vrf.setCustomerName(jointAccountDetail.getLovDescCIFName());
						vrf.setCustId(jointAccountDetail.getCustID());
					} else {
						vrf.setReference(customerDetails.getCustomer().getCustCIF());
						vrf.setCustomerName(customerDetails.getCustomer().getCustShrtName());
						vrf.setCustId(customerDetails.getCustID());
					}
				}
				vrf.setNewRecord(true);
			} else {
				vrf.setReference(customerDetails.getCustomer().getCustCIF());
				vrf.setCustomerName(customerDetails.getCustomer().getCustShrtName());
				vrf.setCustId(customerDetails.getCustID());
				vrf.setNewRecord(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method Record the initiated verification.
	 * 
	 * @param fieldInvestigation
	 * @return WSReturnStatus
	 */
	public WSReturnStatus recordFiVerification(FieldInvestigation fieldInvestigation) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		// prepare data
		try {
			// Prepare Extended Filed Data
			ExtendedFieldHeader extendedFieldHeader = getExtendedHeader(VerificationType.FI);
			fieldInvestigation.setExtendedFieldHeader(extendedFieldHeader);
			setExtendedFieldData(fieldInvestigation, userDetails);

			FieldInvestigation befImage = fieldInvestigationService
					.getFieldInvestigation(fieldInvestigation.getVerificationId(), "_Temp");
			fieldInvestigation.setCif(befImage.getCif());
			fieldInvestigation.setCustId(befImage.getCustId());
			fieldInvestigation.setName(befImage.getName());
			fieldInvestigation.setHouseNumber(befImage.getHouseNumber());
			fieldInvestigation.setStreet(befImage.getStreet());
			fieldInvestigation.setAddressLine1(befImage.getAddressLine1());
			fieldInvestigation.setAddressLine2(befImage.getAddressLine2());
			fieldInvestigation.setAddressLine3(StringUtils.trimToEmpty(befImage.getAddressLine3()));
			fieldInvestigation.setAddressLine4(StringUtils.trimToEmpty(befImage.getAddressLine4()));
			fieldInvestigation.setAddressLine5(StringUtils.trimToEmpty(befImage.getAddressLine5()));
			fieldInvestigation.setPoBox(befImage.getPoBox());
			fieldInvestigation.setCountry(befImage.getCountry());
			fieldInvestigation.setProvince(befImage.getProvince());
			fieldInvestigation.setCity(befImage.getCity());
			fieldInvestigation.setZipCode(befImage.getZipCode());
			fieldInvestigation.setContactNumber1(befImage.getContactNumber1());
			fieldInvestigation.setContactNumber2(befImage.getContactNumber2());
			fieldInvestigation.setBefImage(befImage);
			fieldInvestigation.setUserDetails(userDetails);
			fieldInvestigation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			fieldInvestigation.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			fieldInvestigation.setLastMntBy(userDetails.getUserId());
			fieldInvestigation.setVersion(befImage.getVersion() + 1);
			fieldInvestigation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fieldInvestigation.setSourceId(APIConstants.FINSOURCE_ID_API);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(fieldInvestigation, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = fieldInvestigationService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					fieldInvestigation.setReturnStatus(response);
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
				fieldInvestigation.setReturnStatus(response);
			}

		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response = APIErrorHandlerService.getFailedStatus();
			fieldInvestigation.setReturnStatus(response);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param financeDetail
	 * @return {@link Verification}
	 */
	public Verification createPDInitiation(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification verification = financeDetail.getPdVerification();
		setVerificationData(financeDetail, verificationType);
		try {
			verificationService.saveOrUpdate(financeDetail, VerificationType.PD, "W", true);
		} catch (Exception e) {
			verification.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return verification;
		}
		verification.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return verification;
	}

	public WSReturnStatus recordPDVerification(PersonalDiscussion personalDiscussion) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		// prepare data
		try {
			// Prepare Extended Filed Data
			ExtendedFieldHeader extendedFieldHeader = getExtendedHeader(VerificationType.PD);
			personalDiscussion.setExtendedFieldHeader(extendedFieldHeader);
			setExtendedFieldData(personalDiscussion, userDetails);

			PersonalDiscussion befImage = personalDiscussionService
					.getPersonalDiscussion(personalDiscussion.getVerificationId(), "_Temp");

			personalDiscussion.setCif(befImage.getCif());
			personalDiscussion.setCustId(befImage.getCustId());
			personalDiscussion.setName(befImage.getName());
			personalDiscussion.setHouseNumber(befImage.getHouseNumber());
			personalDiscussion.setStreet(befImage.getStreet());
			personalDiscussion.setAddressLine1(befImage.getAddressLine1());
			personalDiscussion.setAddressLine2(befImage.getAddressLine2());
			personalDiscussion.setAddressLine3(StringUtils.trimToEmpty(befImage.getAddressLine3()));
			personalDiscussion.setAddressLine4(StringUtils.trimToEmpty(befImage.getAddressLine4()));
			personalDiscussion.setAddressLine5(StringUtils.trimToEmpty(befImage.getAddressLine5()));
			personalDiscussion.setPoBox(befImage.getPoBox());
			personalDiscussion.setCountry(befImage.getCountry());
			personalDiscussion.setProvince(befImage.getProvince());
			personalDiscussion.setCity(befImage.getCity());
			personalDiscussion.setZipCode(befImage.getZipCode());
			personalDiscussion.setContactNumber1(befImage.getContactNumber1());
			personalDiscussion.setContactNumber2(befImage.getContactNumber2());
			personalDiscussion.setBefImage(befImage);
			personalDiscussion.setUserDetails(userDetails);
			personalDiscussion.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			personalDiscussion.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			personalDiscussion.setLastMntBy(userDetails.getUserId());
			personalDiscussion.setVersion(befImage.getVersion() + 1);
			personalDiscussion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			personalDiscussion.setSourceId(APIConstants.FINSOURCE_ID_API);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);

			AuditHeader auditHeader = getAuditHeader(personalDiscussion, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = personalDiscussionService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					personalDiscussion.setReturnStatus(response);
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
				personalDiscussion.setReturnStatus(response);
			}

		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response = APIErrorHandlerService.getFailedStatus();
			personalDiscussion.setReturnStatus(response);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param personalDiscussion
	 * @param userDetails
	 */
	private void setExtendedFieldData(PersonalDiscussion personalDiscussion, LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		List<ExtendedField> extendedFields = personalDiscussion.getExtendedDetails();
		if (extendedFields != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(String.valueOf(personalDiscussion.getVerificationId()));
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
			if (extendedFields.size() <= 0) {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}

			personalDiscussion.setExtendedFieldRender(exdFieldRender);

		} else {
			personalDiscussion.setExtendedFieldRender(null);
			personalDiscussion.setExtendedFieldHeader(null);
		}
		logger.debug(Literal.LEAVING);
	}

	public WSReturnStatus recordTVVerification(TechnicalVerification technicalVerification) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		try {
			ExtendedFieldHeader extendedFieldHeader = getExtendedTVHeader(CollateralConstants.MODULE_NAME,
					technicalVerification.getCollateralType(), ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);
			technicalVerification.setExtendedFieldHeader(extendedFieldHeader);
			setExtendedFieldRender(technicalVerification, userDetails);

			TechnicalVerification befImage = technicalVerificationDAO
					.getTechnicalVerification(technicalVerification.getVerificationId(), "_Temp");
			technicalVerification.setCollateralRef(befImage.getCollateralRef());
			technicalVerification.setCollateralType(befImage.getCollateralType());
			technicalVerification.setVerificationId(befImage.getVerificationId());

			technicalVerification.setBefImage(befImage);
			technicalVerification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			technicalVerification.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			technicalVerification.setLastMntBy(userDetails.getUserId());
			technicalVerification.setUserDetails(userDetails);
			technicalVerification.setVersion(befImage.getVersion() + 1);
			technicalVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			technicalVerification.setSourceId(APIConstants.FINSOURCE_ID_API);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);

			AuditHeader auditHeader = getAuditHeader(technicalVerification, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = technicalVerificationService.doApprove(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					technicalVerification.setReturnStatus(response);
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
				technicalVerification.setReturnStatus(response);
			}

		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response = APIErrorHandlerService.getFailedStatus();
			technicalVerification.setReturnStatus(response);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private AuditHeader getAuditHeader(TechnicalVerification tv, String tranWf) {
		AuditDetail auditDetail = new AuditDetail(tranWf, 1, tv.getBefImage(), tv);
		return new AuditHeader(String.valueOf(tv.getVerificationId()), String.valueOf(tv.getVerificationId()), null,
				null, auditDetail, tv.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private void setExtendedFieldRender(TechnicalVerification tv, LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		List<ExtendedField> extendedFields = tv.getExtendedDetails();
		if (extendedFields != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(String.valueOf(tv.getVerificationId()));
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
			if (extendedFields.size() <= 0) {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}

			tv.setExtendedFieldRender(exdFieldRender);

		} else {
			tv.setExtendedFieldRender(null);
			tv.setExtendedFieldHeader(null);
		}
		logger.debug(Literal.LEAVING);
	}

	private ExtendedFieldHeader getExtendedTVHeader(String moduleName, String collateralType,
			int extendedtypeTechvaluation) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService
				.getApprovedExtendedFieldHeaderByModule(moduleName, collateralType, extendedtypeTechvaluation);
		return extendedFieldHeader;
	}

	/**
	 * @param fieldInvestigation
	 * @param userDetails
	 */
	private void setExtendedFieldData(FieldInvestigation fieldInvestigation, LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		List<ExtendedField> extendedFields = fieldInvestigation.getExtendedDetails();
		if (extendedFields != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(String.valueOf(fieldInvestigation.getVerificationId()));
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
			if (extendedFields.size() <= 0) {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}
			fieldInvestigation.setExtendedFieldRender(exdFieldRender);
		} else {
			fieldInvestigation.setExtendedFieldRender(null);
			fieldInvestigation.setExtendedFieldHeader(null);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @return {@link ExtendedFieldHeader}
	 */
	private ExtendedFieldHeader getExtendedHeader(VerificationType verificationType) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService.getApprovedExtendedFieldHeaderByModule(
				CollateralConstants.VERIFICATION_MODULE, verificationType.getValue(), null);
		return extendedFieldHeader;
	}

	public WSReturnStatus recordRCUVerification(RiskContainmentUnit riskContainmentUnit) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		long verificationId = riskContainmentUnit.getVerificationId();
		try {
			RiskContainmentUnit befImg = riskContainmentUnitDAO.getRiskContainmentUnit(verificationId, "_Temp");
			riskContainmentUnit.setVerificationId(befImg.getVerificationId());
			riskContainmentUnit.setWorkflowId(befImg.getWorkflowId());
			riskContainmentUnit.setUserDetails(userDetails);
			riskContainmentUnit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			riskContainmentUnit.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			riskContainmentUnit.setLastMntBy(userDetails.getUserId());
			riskContainmentUnit.setVersion(befImg.getVersion() + 1);
			riskContainmentUnit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			riskContainmentUnit.setSourceId(APIConstants.FINSOURCE_ID_API);
			riskContainmentUnit.setBefImage(befImg);
			List<RCUDocument> rcuDocuments = riskContainmentUnit.getRcuDocuments();
			List<RCUDocument> befImage = riskContainmentUnitDAO.getRCUDocuments(verificationId, "_View");
			for (RCUDocument rcuDocument : rcuDocuments) {
				for (RCUDocument RCUbeImg : befImage) {
					if (rcuDocument.getDocumentSubId().equalsIgnoreCase(RCUbeImg.getDocumentSubId())) {
						rcuDocument.setVerificationId(RCUbeImg.getVerificationId());
						rcuDocument.setDocumentId(RCUbeImg.getDocumentId());
						rcuDocument.setDocumentRefId(RCUbeImg.getDocumentRefId());
						rcuDocument.setDocumentUri(RCUbeImg.getDocumentUri());
						rcuDocument.setDocType(RCUbeImg.getDocType());
						rcuDocument.setDocModule(RCUbeImg.getDocModule());
						rcuDocument.setDocTypeId(RCUbeImg.getDocTypeId());
						rcuDocument.setReinitid(RCUbeImg.getReinitid());
						rcuDocument.setReferenceId(RCUbeImg.getReferenceId());
						rcuDocument.setWorkflowId(RCUbeImg.getWorkflowId());
						rcuDocument.setUserDetails(userDetails);
						rcuDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						rcuDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						rcuDocument.setLastMntBy(userDetails.getUserId());
						rcuDocument.setVersion(RCUbeImg.getVersion());
						rcuDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						rcuDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
						// rcuDocument.setBefImage(rcuDocument);
					}
				}
			}
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(riskContainmentUnit, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = riskContainmentUnitService.doApprove(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					riskContainmentUnit.setReturnStatus(response);
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
				riskContainmentUnit.setReturnStatus(response);
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response = APIErrorHandlerService.getFailedStatus();
			riskContainmentUnit.setReturnStatus(response);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	private AuditHeader getAuditHeader(RiskContainmentUnit rcu, String tranWf) {
		AuditDetail auditDetail = new AuditDetail(tranWf, 1, rcu.getBefImage(), rcu);
		return new AuditHeader(String.valueOf(rcu.getVerificationId()), String.valueOf(rcu.getVerificationId()), null,
				null, auditDetail, rcu.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * @param fi
	 * @param tranWf
	 * @return
	 */
	private AuditHeader getAuditHeader(FieldInvestigation fi, String tranWf) {
		AuditDetail auditDetail = new AuditDetail(tranWf, 1, fi.getBefImage(), fi);
		return new AuditHeader(String.valueOf(fi.getVerificationId()), String.valueOf(fi.getVerificationId()), null,
				null, auditDetail, fi.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private AuditHeader getAuditHeader(PersonalDiscussion pd, String tranWf) {
		AuditDetail auditDetail = new AuditDetail(tranWf, 1, pd.getBefImage(), pd);
		return new AuditHeader(String.valueOf(pd.getVerificationId()), String.valueOf(pd.getVerificationId()), null,
				null, auditDetail, pd.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * @param financeDetail
	 * @param verificationType
	 * @return
	 */
	public Verification createTVInitaion(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification verification = new Verification();

		try {
			// set verification Data
			setTVVerificationData(financeDetail, verificationType);
			// call service to save data
			verificationService.saveOrUpdate(financeDetail, VerificationType.TV, "W", true);
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			verification.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return verification;
		}
		verification.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return verification;
	}

	/**
	 * @param financeDetail
	 * @param verificationType
	 */
	private void setTVVerificationData(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);

		Verification verification = null;
		if (verificationType == VerificationType.TV) {
			verification = financeDetail.getTvVerification();
		}
		List<Verification> verificationsList = verification.getVerifications();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		verification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		verification.setVersion(1);
		verification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		verification.setUserDetails(userDetails);
		verification.setLastMntBy(userDetails.getUserId());
		verification.setCreatedBy(userDetails.getUserId());
		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		for (Verification vrf : verificationsList) {
			for (CollateralAssignment collateralSetup : collaterals) {
				vrf.setReference(customerDetails.getCustomer().getCustCIF());
				vrf.setCustomerName(customerDetails.getCustomer().getCustShrtName());
				vrf.setCustId(customerDetails.getCustID());
				vrf.setReferenceType(collateralSetup.getCollateralType());
				vrf.setReferenceFor(collateralSetup.getCollateralRef());
				vrf.setNewRecord(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param financeDetail
	 * @return {@link Verification}
	 */
	public Verification initiateRCU(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification response = new Verification();
		try {
			Verification verification = financeDetail.getRcuVerification();
			// set RCU documents
			setRCUDocuments(financeDetail);
			List<Verification> finalVerifications = getFinalVerifications(verification);
			verification.getVerifications().clear();
			verification.setVerifications(finalVerifications);
			financeDetail.setRcuVerification(verification);
			// Setting Verification Data
			setRCUVerificationData(financeDetail, verificationType);

			verificationService.saveOrUpdate(financeDetail, VerificationType.RCU, "W", true);
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug(Literal.LEAVING);
		return response;
	}

	private void setRCUVerificationData(FinanceDetail financeDetail, VerificationType verificationType) {
		logger.debug(Literal.ENTERING);
		Verification verification = null;
		if (verificationType == VerificationType.RCU) {
			verification = financeDetail.getRcuVerification();
		}
		if (verificationType == VerificationType.LV) {
			verification = financeDetail.getLvVerification();
		}
		List<Verification> verificationsList = verification.getVerifications();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		verification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		verification.setVersion(1);
		verification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		verification.setUserDetails(userDetails);
		verification.setLastMntBy(userDetails.getUserId());
		verification.setCreatedBy(userDetails.getUserId());
		for (Verification vrf : verificationsList) {
			vrf.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			vrf.setVersion(1);
			vrf.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			vrf.setUserDetails(userDetails);
			vrf.setLastMntBy(userDetails.getUserId());
			vrf.setCreatedBy(userDetails.getUserId());
			vrf.setNewRecord(true);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setRCUDocuments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		Verification verification = financeDetail.getRcuVerification();
		// get all documents against reference
		List<CustomerDocument> customerDocuments = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<CustomerDocument> coAppDocumentsList = new ArrayList<>();
		List<DocumentDetails> collateralDocumentList = new ArrayList<>();

		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		List<DocumentDetails> list;
		if (CollectionUtils.isNotEmpty(collaterals)) {
			for (CollateralAssignment collateral : collaterals) {
				list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
						CollateralConstants.MODULE_NAME, "", "_View");
				if (CollectionUtils.isNotEmpty(list)) {
					collateralDocumentList.addAll(list);
				}
			}
		}
		//get joint account documents
		List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
		if (CollectionUtils.isNotEmpty(jountAccountDetailList)) {
			for (JointAccountDetail jointAccountDetail : jountAccountDetailList) {
				coAppDocumentsList = jointAccountDetail.getCustomerDetails().getCustomerDocumentsList();
			}
		}

		List<Verification> rcuVerfications = financeDetail.getRcuVerification().getVerifications();
		//set customer Documents
		if (CollectionUtils.isNotEmpty(rcuVerfications)) {
			for (Verification rcuVrf : rcuVerfications) {
				rcuVrf.setKeyReference(verification.getKeyReference());
				for (CustomerDocument cd : customerDocuments) {
					if (StringUtils.equals(rcuVrf.getReferenceFor(), cd.getCustDocCategory()) && StringUtils
							.equalsIgnoreCase(rcuVrf.getReferenceType(), DocumentType.CUSTOMER.getValue())) {
						setCustomerDocuments(rcuVrf, cd, DocumentType.CUSTOMER);
					}
				}
			}
		}
		//set coApplicant Documents
		if (CollectionUtils.isNotEmpty(coAppDocumentsList)) {
			for (Verification rcuVrf : rcuVerfications) {
				rcuVrf.setKeyReference(verification.getKeyReference());
				for (CustomerDocument capd : coAppDocumentsList) {
					if (StringUtils.equals(rcuVrf.getReferenceFor(), capd.getCustDocCategory()) && StringUtils
							.equalsIgnoreCase(rcuVrf.getReferenceType(), DocumentType.COAPPLICANT.getValue())) {
						setCustomerDocuments(rcuVrf, capd, DocumentType.COAPPLICANT);
					}
				}
			}
		}
		//get all loan documents
		List<DocumentDetails> financeDocumentsList = financeDetail.getDocumentDetailsList();
		//set Fincnace Documents
		if (CollectionUtils.isNotEmpty(financeDocumentsList)) {
			for (Verification rcuVrf : rcuVerfications) {
				rcuVrf.setKeyReference(verification.getKeyReference());
				for (DocumentDetails loandoc : financeDocumentsList) {
					if (StringUtils.equals(rcuVrf.getReferenceFor(), loandoc.getDocCategory())
							&& StringUtils.equalsIgnoreCase(rcuVrf.getReferenceType(), DocumentType.LOAN.getValue())) {
						setDocumentDetails(rcuVrf, loandoc, DocumentType.LOAN);
					}
				}
			}
		}
		//set collateral Documents
		if (CollectionUtils.isNotEmpty(collateralDocumentList)) {
			for (Verification rcuVrf : rcuVerfications) {
				rcuVrf.setKeyReference(verification.getKeyReference());
				for (DocumentDetails colldoc : collateralDocumentList) {
					if (StringUtils.equals(rcuVrf.getReferenceFor(), colldoc.getDocCategory()) && StringUtils
							.equalsIgnoreCase(rcuVrf.getReferenceType(), DocumentType.COLLATRL.getValue())) {
						setDocumentDetails(rcuVrf, colldoc, DocumentType.COLLATRL);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setDocumentDetails(Verification verification, DocumentDetails document, DocumentType documentType) {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		RCUDocument rcuDocument = new RCUDocument();
		verification.setNewRecord(true);
		verification.setDocName(document.getDocName());
		verification.setReferenceType(documentType.getValue());
		verification.setReference(String.valueOf(document.getDocId()));
		verification.setDocType(documentType.getKey());
		verification.setCreatedBy(userDetails.getUserId());
		verification.setReferenceFor(document.getDocCategory());
		verification.setKeyReference(verification.getKeyReference());
		verification.setVerificationType(VerificationType.RCU.getKey());
		setDefaultInitiationStatus(verification);
		rcuDocument.setDocCategory(document.getDocCategory());
		rcuDocument.setDocumentId(document.getDocId());
		rcuDocument.setDocumentSubId(document.getDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		rcuDocument.setCollateralRef(document.getReferenceId());
		verification.setRcuDocument(rcuDocument);

		logger.debug(Literal.LEAVING);
	}

	private void setCustomerDocuments(Verification item, CustomerDocument document, DocumentType documentType) {
		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		RCUDocument rcuDocument = new RCUDocument();
		item.setDocName(document.getCustDocName());
		item.setReferenceFor(document.getCustDocCategory());
		item.setNewRecord(true);
		item.setReferenceType(documentType.getValue());
		item.setDocType(documentType.getKey());
		item.setVerificationType(VerificationType.RCU.getKey());
		item.setCreatedBy(userDetails.getUserId());
		item.setKeyReference(item.getKeyReference());
		item.setCustId(document.getCustID());
		item.setCustomerName(document.getLovDescCustShrtName());
		item.setReference(document.getLovDescCustCIF());
		setDefaultInitiationStatus(item);
		rcuDocument.setDocCategory(document.getCustDocCategory());
		rcuDocument.setDocumentId(document.getCustID());
		rcuDocument.setDocumentSubId(document.getCustDocCategory());
		rcuDocument.setDocumentType(documentType.getKey());
		item.setRcuDocument(rcuDocument);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * This method is used for Grouping the Verifications.
	 * 
	 * @param verification
	 * @return
	 */
	private List<Verification> getFinalVerifications(Verification verification) {
		logger.debug(Literal.ENTERING);

		Map<Long, Verification> reInitMap = new HashMap<>();
		Map<Long, Verification> other = new HashMap<>();
		List<Verification> verifications = new ArrayList<>();
		boolean initType = false;// FIXME
		// Implementation constant related to, RCU initiation group by agency or not.
		boolean grpByAgency = ImplementationConstants.VER_RCU_INITATE_BY_AGENCY;
		Verification aVerification = null;
		for (Verification vrf : verification.getVerifications()) {
			if (vrf.getRequestType() != RequestType.INITIATE.getKey()
					&& vrf.getDecision() != Decision.RE_INITIATE.getKey()) {
				verifications.add(vrf);
			}
			if (grpByAgency) {
				if ((vrf.getAgency() != null && !reInitMap.containsKey(vrf.getAgency()))
						|| (vrf.getReInitAgency() != null && !reInitMap.containsKey(vrf.getReInitAgency()))
								&& !vrf.isIgnoreFlag()) {
					if (vrf.getDecision() == Decision.RE_INITIATE.getKey() && !initType) {
						reInitMap.put(vrf.getReInitAgency(), vrf);
					} else if (!vrf.isInitiated() || !initType) {
						other.put(vrf.getAgency(), vrf);
					}
				}
			}
		}
		// Group the Verifications by Agency
		for (Verification vrf : verification.getVerifications()) {
			RCUDocument document = vrf.getRcuDocument();
			if (document != null) {
				document.setInitRemarks(vrf.getRemarks());
				document.setDecisionRemarks(vrf.getDecisionRemarks());
				document.setDecision(vrf.getDecision());
				document.setAccNumber(vrf.getAccNumber());
				document.setBankName(vrf.getBankName());

				if (grpByAgency) {
					if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
						if (!initType && vrf.getDecision() == Decision.RE_INITIATE.getKey() && !vrf.isIgnoreFlag()) {
							aVerification = reInitMap.get(vrf.getReInitAgency());
							document.setInitRemarks(vrf.getDecisionRemarks());
							aVerification.getRcuDocuments().add(document);
						} else if (!vrf.isInitiated() || !initType) {
							aVerification = other.get(vrf.getAgency());
							if (aVerification != null) {
								aVerification.getRcuDocuments().add(document);
							}
						}
					} else if (!initType && vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
						aVerification = reInitMap.get(vrf.getReInitAgency());
						document.setInitRemarks(vrf.getDecisionRemarks());
						aVerification.getRcuDocuments().add(document);
					}
				}
			}
		}
		if (grpByAgency) {
			verifications.addAll(reInitMap.values());
			verifications.addAll(other.values());
		}
		logger.debug(Literal.LEAVING);
		return verifications;
	}

	// recording the LV verification fields
	/**
	 * @param legalVerification
	 * @return
	 */
	public WSReturnStatus recordLVVerification(LegalVerification legalVerification) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		long verificationId = legalVerification.getVerificationId();
		try {
			if (legalVerification.getExtendedDetails() != null && !legalVerification.getExtendedDetails().isEmpty()) {

				ExtendedFieldHeader extendedFieldHeader = getExtendedLVHeader(CollateralConstants.MODULE_NAME,
						legalVerification.getCollateralType(), ExtendedFieldConstants.VERIFICATION_LV);
				legalVerification.setExtendedFieldHeader(extendedFieldHeader);
				setExtendedFieldRender(legalVerification, userDetails);
			}
			LegalVerification befIamge = legalVerificationDAO.getLegalVerification(verificationId, "_Temp");
			legalVerification.setVerificationId(befIamge.getVerificationId());
			legalVerification.setWorkflowId(befIamge.getWorkflowId());
			legalVerification.setUserDetails(userDetails);
			legalVerification.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			legalVerification.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			legalVerification.setLastMntBy(userDetails.getUserId());
			legalVerification.setVersion(befIamge.getVersion() + 1);
			legalVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			legalVerification.setSourceId(APIConstants.FINSOURCE_ID_API);
			legalVerification.setBefImage(befIamge);
			List<LVDocument> befImage = legalVerificationDAO.getLVDocuments(verificationId, "_View");
			List<LVDocument> lvDocuments = legalVerification.getLvDocuments();
			for (LVDocument lvDocument : lvDocuments) {
				for (LVDocument lvBefImg : befImage) {
					if (lvDocument.getDocumentSubId().equalsIgnoreCase(lvBefImg.getDocumentSubId())) {
						lvDocument.setVerificationId(lvBefImg.getVerificationId());
						// lvDocument.setDocumentType(lvBefImg.getDocumentType());
						lvDocument.setDocModule(lvBefImg.getDocModule());
						lvDocument.setDocumentId(lvBefImg.getDocumentId());
						// lvDocument.setDocumentSubId(lvBefImg.getDocumentSubId());
						lvDocument.setDocumentType(lvBefImg.getDocumentType());
						lvDocument.setDocumentRefId(lvBefImg.getDocumentRefId());
						lvDocument.setDocumentUri(lvBefImg.getDocumentUri());
						lvDocument.setReferenceId(lvBefImg.getReferenceId());
						lvDocument.setWorkflowId(lvBefImg.getWorkflowId());
						lvDocument.setUserDetails(userDetails);
						lvDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						lvDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						lvDocument.setLastMntBy(userDetails.getUserId());
						lvDocument.setVersion(lvBefImg.getVersion());
						lvDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						lvDocument.setSourceId(APIConstants.FINSOURCE_ID_API);
					}
				}
			}
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(legalVerification, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = legalVerificationService.doApprove(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
					legalVerification.setReturnStatus(response);
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
				legalVerification.setReturnStatus(response);
			}
		} catch (Exception e) {
			APIErrorHandlerService.logUnhandledException(e);
			response = APIErrorHandlerService.getFailedStatus();
			legalVerification.setReturnStatus(response);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param moduleName
	 * @param collateralType
	 * @param verificationLv
	 * @return
	 */
	private ExtendedFieldHeader getExtendedLVHeader(String moduleName, String collateralType, String verificationLv) {
		ExtendedFieldHeader extendedFieldHeader = extFieldConfigService
				.getApprovedExtendedFieldHeaderByModule(moduleName, collateralType, verificationLv);
		return extendedFieldHeader;
	}

	/**
	 * @param lv
	 * @param userDetails
	 */
	private void setExtendedFieldRender(LegalVerification lv, LoggedInUser userDetails) {
		logger.debug(Literal.ENTERING);

		List<ExtendedField> extendedFields = lv.getExtendedDetails();
		if (extendedFields != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(String.valueOf(lv.getVerificationId()));
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
			if (extendedFields.size() <= 0) {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}

			lv.setExtendedFieldRender(exdFieldRender);

		} else {
			lv.setExtendedFieldRender(null);
			lv.setExtendedFieldHeader(null);
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * @param lv
	 * @param tranWf
	 * @return
	 */
	private AuditHeader getAuditHeader(LegalVerification lv, String tranWf) {
		AuditDetail auditDetail = new AuditDetail(tranWf, 1, lv.getBefImage(), lv);
		return new AuditHeader(String.valueOf(lv.getVerificationId()), String.valueOf(lv.getVerificationId()), null,
				null, auditDetail, lv.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	private void setDefaultInitiationStatus(Verification item) {
		if (ImplementationConstants.VER_RCU_DFT_REQ_TYPE_REQUEST) {
			item.setRequestType(RequestType.REQUEST.getKey());
		}
	}

	/**
	 * @param financeDetail
	 * @param lv
	 * @return
	 */
	public Verification initiateLV(FinanceDetail financeDetail, VerificationType lv) {
		logger.debug(Literal.ENTERING);

		Verification response = new Verification();
		try {
			// set LVdocuments
			setLVDocuments(financeDetail);
			// Setting Verification Data
			setRCUVerificationData(financeDetail, lv);
			// Prepare data to save into Staging
			Verification lvVerification = financeDetail.getLvVerification();
			List<Verification> verifications = lvVerification.getVerifications();
			for (Verification verification : verifications) {
				verification.setKeyReference(lvVerification.getKeyReference());
				verification.setVerificationType(VerificationType.LV.getKey());
				verification.setRequestType(RequestType.INITIATE.getKey());
				verification.setReference(verification.getCif());
				verification.setCreatedOn(SysParamUtil.getAppDate());
				verificationService.saveLegalVerification(verification);
			}
			prepareLVVerifications(financeDetail);
			verificationService.saveOrUpdate(financeDetail, VerificationType.LV, "W", true);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param financeDetail
	 */
	private void setLVDocuments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<DocumentDetails> collateralDocuments = new ArrayList<>();
		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		List<DocumentDetails> list;
		if (CollectionUtils.isNotEmpty(collaterals)) {
			for (CollateralAssignment collateral : collaterals) {
				list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
						CollateralConstants.MODULE_NAME, "", "_View");
				if (CollectionUtils.isNotEmpty(list)) {
					collateralDocuments.addAll(list);
				}
			}
		}
		List<CustomerDocument> customerDocumentsList = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> financeDocumentsList = financeDetail.getDocumentDetailsList();
		List<LVDocument> lvDocuments = new ArrayList<>();
		Verification vrf = financeDetail.getLvVerification();
		List<Verification> verifications = vrf.getVerifications();
		for (Verification verification : verifications) {
			lvDocuments = verification.getLvDocuments();
			if (CollectionUtils.isNotEmpty(lvDocuments)) {
				for (LVDocument lvDocument : lvDocuments) {
					if (lvDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
						for (DocumentDetails clDocument : collateralDocuments) {
							if (StringUtils.equalsIgnoreCase(lvDocument.getDocumentSubId(),
									clDocument.getDocCategory())) {
								setLVDocuments(lvDocument, clDocument);
							}
						}
					}
					if (lvDocument.getDocumentType() == DocumentType.LOAN.getKey()) {
						for (DocumentDetails loanDocument : financeDocumentsList) {
							if (StringUtils.equalsIgnoreCase(lvDocument.getDocumentSubId(),
									loanDocument.getDocCategory())) {
								setLVDocuments(lvDocument, loanDocument);
							}
						}
					}
					if (lvDocument.getDocumentType() == DocumentType.CUSTOMER.getKey()) {
						for (CustomerDocument cuDocument : customerDocumentsList) {
							if (StringUtils.equalsIgnoreCase(lvDocument.getDocumentSubId(),
									cuDocument.getCustDocCategory())) {
								setLVCustomerDocuments(lvDocument, cuDocument);
							}
						}
					}
				}
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param lvDocument
	 * @param cuDocument
	 */
	private void setLVCustomerDocuments(LVDocument lvDocument, CustomerDocument cuDocument) {
		logger.debug(Literal.ENTERING);

		lvDocument.setDocCategory(cuDocument.getCustDocCategory());
		lvDocument.setDocumentType(DocumentType.CUSTOMER.getKey());
		lvDocument.setDocumentId(cuDocument.getId());
		lvDocument.setDocRefID(cuDocument.getDocRefId());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param lvDocument
	 * @param clDocument
	 */
	private void setLVDocuments(LVDocument lvDocument, DocumentDetails clDocument) {
		logger.debug(Literal.ENTERING);

		lvDocument.setDocCategory(clDocument.getDocCategory());
		lvDocument.setDocumentId(clDocument.getDocId());
		lvDocument.setDocRefID(clDocument.getDocRefId());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param financeDetail
	 */
	private void prepareLVVerifications(FinanceDetail financeDetail) {
		Verification vrf = financeDetail.getLvVerification();
		List<Verification> verificationsList = verificationService.getVerifications(vrf.getKeyReference(),
				VerificationType.LV.getKey());

		List<Verification> finalList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(verificationsList)) {
			for (Verification verification : verificationsList) {
				if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {
					for (CollateralAssignment assign : financeDetail.getCollateralAssignmentList()) {
						if (StringUtils.equals(verification.getReferenceFor(), assign.getCollateralRef())) {
							finalList.add(verification);
						}
					}
				}
			}
		}
		for (Verification oldVrf : finalList) {
			for (Verification newVrf : vrf.getVerifications()) {
				if (newVrf.getId() == oldVrf.getId() && newVrf.getRequestType() == RequestType.WAIVE.getKey()) {
					BeanUtils.copyProperties(newVrf, oldVrf);
				}
			}
		}
		vrf.setVerifications(finalList);
		verificationService.setLVDetails(vrf.getVerifications());
		financeDetail.setLvVerification(vrf);
	}

	// setters and getters
	public VerificationService getVerificationService() {
		return verificationService;
	}

	@Autowired
	public void setVerificationService(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	@Autowired
	public void setFieldInvestigationService(FieldInvestigationService fieldInvestigationService) {
		this.fieldInvestigationService = fieldInvestigationService;
	}

	@Autowired
	public static void setExtFieldConfigService(ExtFieldConfigService extFieldConfigService) {
		VerificationController.extFieldConfigService = extFieldConfigService;
	}

	@Autowired
	public void setPersonalDiscussionService(PersonalDiscussionService personalDiscussionService) {
		this.personalDiscussionService = personalDiscussionService;
	}

	@Autowired
	public void setTechnicalVerificationDAO(TechnicalVerificationDAO technicalVerificationDAO) {
		this.technicalVerificationDAO = technicalVerificationDAO;
	}

	@Autowired
	public void setTechnicalVerificationService(TechnicalVerificationService technicalVerificationService) {
		this.technicalVerificationService = technicalVerificationService;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setRiskContainmentUnitService(RiskContainmentUnitService riskContainmentUnitService) {
		this.riskContainmentUnitService = riskContainmentUnitService;
	}

	@Autowired
	public void setRiskContainmentUnitDAO(RiskContainmentUnitDAO riskContainmentUnitDAO) {
		this.riskContainmentUnitDAO = riskContainmentUnitDAO;
	}

	@Autowired
	public void setLegalVerificationDAO(LegalVerificationDAO legalVerificationDAO) {
		this.legalVerificationDAO = legalVerificationDAO;
	}

	@Autowired
	public void setLegalVerificationService(LegalVerificationService legalVerificationService) {
		this.legalVerificationService = legalVerificationService;
	}

}