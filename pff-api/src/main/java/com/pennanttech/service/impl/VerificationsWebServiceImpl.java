/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VerificationsWebServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-02-2021 * *
 * Modified Date : 08-02-2021 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-02-2021 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amtmasters.VehicleDealerDAO;
import com.pennant.backend.dao.applicationmaster.ReasonCodeDAO;
import com.pennant.backend.dao.collateral.CollateralStructureDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.VerificationController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.LegaVerificationType;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.StatuReasons;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.dao.LegalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.RiskContainmentUnitDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.fi.LVStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUDocVerificationType;
import com.pennanttech.pennapps.pff.verification.fi.RCUStatus;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.VerificationsRestService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.VerificationCustomerAddress.VerificationDetails;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class VerificationsWebServiceImpl implements VerificationsRestService {
	private static Logger logger = LogManager.getLogger(VerificationsWebServiceImpl.class);

	private VehicleDealerDAO vehicleDealerDAO;
	private VerificationController verificationController;
	private ReasonCodeDAO reasonCodeDAO;
	private FinanceDetailService financeDetailService;
	private VerificationDAO verificationDAO;
	private FinanceMainDAO financeMainDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private CustomerAddresDAO customerAddresDAO;
	private CollateralStructureDAO collateralStructureDAO;
	private RiskContainmentUnitDAO riskContainmentUnitDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private LegalVerificationDAO legalVerificationDAO;
	private VerificationService verificationService;

	CustomerDetails customerDetails = null;

	/**
	 * This Method will return the List of addresses of Customer and Co-Applicant of the particular finReference
	 * 
	 * @param finReference
	 * @return VerificationCustomerAddress
	 */

	/**
	 * This Method initiate's the verification
	 * 
	 * @param verification
	 * @return {@link Verification}
	 */

	@Override
	public WSReturnStatus initiateFIVerification(Verification verification) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		String keyReference = verification.getKeyReference();

		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[2];
			valueParm[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}

		FinanceDetail financeDetail = getFinanceDetails(finID);

		if (financeDetail == null) {
			String valueParm[] = new String[2];
			valueParm[0] = "loan data not found";
			response = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return response;
		}
		// validate the given fields from request
		response = validateFIinitiation(verification, financeDetail);
		if (response != null) {
			return response;
		}

		if (response == null) {
			financeDetail.setFiVerification(verification);
			try {
				response = verificationController.createFIInitaion(financeDetail, VerificationType.FI);
			} catch (Exception e) {
				return APIErrorHandlerService.getFailedStatus();
			}
		}
		logger.debug(Literal.LEAVING);
		return response;

	}

	private FinanceDetail getFinanceDetails(long finID, VerificationType verificationtype, String type) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail = financeDetailService.getVerificationInitiationDetails(finID, verificationtype, type);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private FinanceDetail getFinanceDetails(long finID) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = new FinanceDetail();
		fd = financeDetailService.getFinanceDetailById(finID, false, "", true, FinServiceEvent.ORG, "");
		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * This method Validate's the request body field's
	 * 
	 * @param verification
	 * @return {@link WSReturnStatus}
	 */
	private WSReturnStatus validateFIinitiation(Verification verification, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		// validate customer details
		customerDetails = financeDetail.getCustomerDetails();
		if (customerDetails == null) {
			String valueParm[] = new String[1];
			valueParm[0] = customerDetails.getCustCIF();
			return APIErrorHandlerService.getFailedStatus("90101", valueParm);
		}
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (verification.getVerificationType() == VerificationType.FI.getKey()) {
			if (CollectionUtils.isEmpty(addressList)) {
				String valueParm[] = new String[4];
				valueParm[0] = "No address";
				valueParm[1] = "found for this Customer";
				valueParm[2] = customerDetails.getCustCIF();
				valueParm[3] = "";
				return APIErrorHandlerService.getFailedStatus("21005", valueParm);
			}
		}

		// validate verificationType
		List<Verification> verificationsList = verification.getVerifications();
		VerificationType verificationType = null;
		if (verification.getVerificationType() > 0) {
			verificationType = VerificationType.getVerificationType(verification.getVerificationType());
			if (verificationType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(verification.getVerificationType());
				valueParm[1] = "Verification Type";
				return getErrorDetails("90329", valueParm);
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationType";
			return getErrorDetails("90502", valueParm);
		}
		if (verification.getVerificationType() != VerificationType.FI.getKey()) {
			String[] valueParm = new String[2];
			valueParm[0] = "VerificationType :" + String.valueOf(verification.getVerificationType());
			valueParm[1] = String.valueOf(VerificationType.FI.getKey());
			return getErrorDetails("90337", valueParm);
		}
		if (CollectionUtils.isEmpty(verificationsList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Verifications";
			return getErrorDetails("90502", valueParm);
		}

		for (Verification vrf : verificationsList) {
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setKeyReference(verification.getKeyReference());

			if (StringUtils.isBlank(vrf.getReferenceFor())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ReferenceFor";
				return getErrorDetails("90502", valueParm);
			}
			/*
			 * #PSD-167688: Same address type intiation records are displaying multiple times in FI intiation menu in
			 * plf, If hit the initiateFIVerification API multiple times
			 */
			/*
			 * boolean existID = verificationDAO.isVerificationIdExists(verification.getKeyReference(),
			 * vrf.getReferenceFor(), vrf.getCif(), VerificationType.FI.getKey(), verification.getReferenceType());
			 */
			Long verificationID = getVerificationId(vrf, VerificationType.FI);
			if (verificationID != null) {
				vrf.setId(verificationID);
				// check it is in recording table or not??
				// if yes throw error msg
				if (verificationService.isVerificationInRecording(vrf, VerificationType.FI)) {
					String[] valueParm = new String[4];
					valueParm[0] = "ReferenceFor: " + vrf.getReferenceFor();
					valueParm[1] = "for custCIF :" + vrf.getCif();
					valueParm[2] = "and keyReference: " + verification.getKeyReference() + " Already Processed..";
					return getErrorDetails("21005", valueParm);
				}
			}
			// validate RequestType
			RequestType requestType = RequestType.getType(vrf.getRequestType());
			Map<String, String> requestTypeMap = getRequestTypeValues();
			if (requestType == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "RequestType is Invalid";
				valueParm[1] = "Available Values are";
				valueParm[2] = requestTypeMap.toString();
				return getErrorDetails("21005", valueParm);
			}
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				// Reason values are set to be null in case values are processed with API
				if (vrf.getReason() == null || vrf.getReason() <= 0) {
					vrf.setReason(null);
				}
				if (vrf.getAgency() == null || vrf.getAgency() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Agency";
					return getErrorDetails("90502", valueParm);
				} else {
					VehicleDealer agencyDetails = getAgencyById(vrf.getAgency(), Agencies.FIAGENCY.getKey());
					if (agencyDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Agency Id :" + String.valueOf(vrf.getAgency());// value should provide from
																						// AMTVehicleDealer_AView
																						// Dealerid
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setAgencyName(agencyDetails.getDealerName());
						vrf.setAgencyCity(agencyDetails.getDealerCity());
					}
				}

			}
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				// Agency values are set to be null in case values are processed with API
				if (vrf.getAgency() != null || vrf.getAgency() > 0) {
					vrf.setAgency(null);
				}
				if (vrf.getReason() == null || vrf.getReason() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return getErrorDetails("90502", valueParm);
				} else {
					ReasonCode reasonCode = getReasonCode(vrf.getReason(), WaiverReasons.FIWRES.getKey());
					if (reasonCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(vrf.getReason());// value should provide from Reasons_AView id
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setReasonName(reasonCode.getReasonTypeDesc());
					}
				}
			}
			if (vrf.getRequestType() == RequestType.REQUEST.getKey()) {
				if (vrf.getRemarks() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Remarks";
					return getErrorDetails("90502", valueParm);
				}
			}
			if (StringUtils.isBlank(vrf.getRemarks())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Remarks";
				return getErrorDetails("90502", valueParm);
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private Long getVerificationId(Verification vrf, VerificationType verificationType) {
		Long verificationID = verificationDAO.isVerificationExist(vrf.getKeyReference(), vrf.getReferenceFor(),
				vrf.getCif(), verificationType.getKey(), vrf.getReferenceType());
		return verificationID;
	}

	/*
	 * Fetching the Values of the FI verification based on the keyReference, requestType and verificationType
	 * 
	 * @param verification
	 * 
	 * @return {@link Verification}
	 */
	@Override
	public List<Verification> getVerificationIds(Verification verification) throws ServiceException {
		logger.info(Literal.ENTERING);

		List<Verification> verifications = new ArrayList<>();
		WSReturnStatus returnstatus = new WSReturnStatus();
		List<RCUDocument> RCUdocuments = new ArrayList<>();

		String keyReference = verification.getKeyReference();
		if (StringUtils.isBlank(keyReference)) {
			String[] valueParam = new String[1];
			valueParam[0] = "FinReference";
			returnstatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "FinReference";
			returnstatus = APIErrorHandlerService.getFailedStatus("90201", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}

		int reqType = verification.getRequestType();
		if (reqType <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "requestType";
			returnstatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}

		RequestType requestType = RequestType.getType(reqType);
		if (requestType == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "requestType";
			returnstatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}

		int type = verification.getVerificationType();
		if (type <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "verificationType";
			returnstatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}

		VerificationType verificationType = VerificationType.getVerificationType(type);
		if (verificationType == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "verificationType";
			returnstatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			verification.setReturnStatus(returnstatus);
			verifications.add(verification);
			return verifications;
		}
		if (!(VerificationType.RCU.getKey() == type) && !(VerificationType.LV.getKey() == type)) {

			verifications = verificationDAO.getVerifications(keyReference, type, reqType);
			if (CollectionUtils.isEmpty(verifications)) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verification Details";
				valueParm[1] = "not Found for Finreference: " + keyReference;
				valueParm[2] = ", verificationType: " + type;
				valueParm[3] = "And requestType: " + reqType;
				returnstatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				verification.setReturnStatus(returnstatus);
				verifications.add(verification);
				return verifications;
			}
		}
		if (VerificationType.RCU.getKey() == type) {
			RCUdocuments = riskContainmentUnitDAO.getDocuments(keyReference, TableType.TEMP_TAB);
			if (CollectionUtils.isEmpty(RCUdocuments)) {
				String valueParm[] = new String[4];
				valueParm[0] = "RCU Document Details";
				valueParm[1] = "not Found for Finreference: " + keyReference;
				valueParm[2] = ", verificationType: " + type;
				valueParm[3] = "And requestType: " + reqType;
				returnstatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				verification.setReturnStatus(returnstatus);
				verifications.add(verification);
				return verifications;
			} else {
				verification.setRcuDocuments(RCUdocuments);
			}
		}
		if (VerificationType.LV.getKey() == type) {
			List<LVDocument> lvDocuments = legalVerificationDAO.getLVDocuments(keyReference, TableType.TEMP_TAB);
			if (CollectionUtils.isEmpty(lvDocuments)) {
				String valueParm[] = new String[4];
				valueParm[0] = "LV Document Details";
				valueParm[1] = "not Found for Finreference: " + keyReference;
				valueParm[2] = ", verificationType: " + type;
				valueParm[3] = "And requestType: " + reqType;
				returnstatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				verification.setReturnStatus(returnstatus);
				verifications.add(verification);
				return verifications;
			} else {
				verification.setLvDocuments(lvDocuments);
			}
		}

		logger.info(Literal.LEAVING);
		verification.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		verifications.add(verification);
		return verifications;
	}

	@Override
	public WSReturnStatus recordFiVerification(FieldInvestigation fieldInvestigation) {
		logger.debug(Literal.ENTERING);

		String keyReference = fieldInvestigation.getKeyReference();
		WSReturnStatus response = null;

		if (StringUtils.isBlank(keyReference)) {
			if (StringUtils.isBlank(keyReference)) {
				String valueParm[] = new String[1];
				valueParm[0] = "keyReference";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}
		long verificationId = fieldInvestigation.getVerificationId();
		if (verificationId <= 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "verificationId";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// AddressType Validations
		List<Verification> veriFications = verificationDAO.getVeriFications(keyReference, VerificationType.FI.getKey());
		List<String> vrfAddress = veriFications.stream().map(a -> a.getReferenceFor()).collect(Collectors.toList());
		if (!vrfAddress.contains(fieldInvestigation.getAddressType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "AddressType";
			return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		// get Verification Id's from Verifications Table with keyreference
		List<Long> verificationIds = verificationDAO.getVerificationIds(keyReference, VerificationType.FI.getKey(),
				RequestType.INITIATE.getKey());
		if (CollectionUtils.isEmpty(verificationIds)) {
			return APIErrorHandlerService.getFailedStatus("30533");

		}
		if (!verificationIds.contains(fieldInvestigation.getVerificationId())) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(fieldInvestigation.getVerificationId());
			return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		boolean initiatedVerfication = verificationDAO.isInitiatedVerfication(VerificationType.FI, verificationId,
				"_Temp");
		if (!initiatedVerfication) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id";
			valueParam[2] = "Already";
			valueParam[3] = "Processed";
			return APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}
		if (fieldInvestigation.getVerifiedDate() == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (DateUtil.compare(fieldInvestigation.getVerifiedDate(), SysParamUtil.getAppDate()) != 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationDate";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (StringUtils.isBlank(fieldInvestigation.getAgentCode())) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentCode";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (StringUtils.isBlank(fieldInvestigation.getAgentName())) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentName";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		if (fieldInvestigation.getStatus() == 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "Recommendations";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		if (FIStatus.getType(fieldInvestigation.getStatus()) == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recommendations";
			return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		if (fieldInvestigation.getStatus() == FIStatus.NEGATIVE.getKey()
				|| fieldInvestigation.getStatus() == FIStatus.REFER_TO_CREDIT.getKey()) {
			if (fieldInvestigation.getReason() == null || fieldInvestigation.getReason() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reason";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				ReasonCode reasonCode = getReasonCode(fieldInvestigation.getReason(), StatuReasons.FISRES.getKey());
				if (reasonCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason"; // value should provide from Reasons_AView id
					return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
				} else {
					fieldInvestigation.setReasonName(reasonCode.getReasonTypeDesc());
				}
			}

		}
		List<ExtendedField> extendedDetails = fieldInvestigation.getExtendedDetails();
		if (CollectionUtils.isEmpty(fieldInvestigation.getExtendedDetails())) {
			List<ErrorDetail> errorDetails = validateExtendedFileds(extendedDetails, VerificationType.FI.getValue());
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return APIErrorHandlerService.getFailedStatus("90265", valueParm);
			}
		}

		if (CollectionUtils.isNotEmpty(extendedDetails)) {
			List<ErrorDetail> errorDetails = validateExtendedFileds(extendedDetails, VerificationType.FI.getValue());
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return APIErrorHandlerService.getFailedStatus("90265", valueParm);
			}
		}

		if (response == null) {
			try {
				response = verificationController.recordFiVerification(fieldInvestigation);
			} catch (Exception e) {
				return response;
			}
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param fieldInvestigation
	 * @return
	 */
	private List<ErrorDetail> validateExtendedFileds(List<ExtendedField> extendedDetails, String verificationType) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(extendedDetails,
				CollateralConstants.VERIFICATION_MODULE, verificationType, null);
		logger.debug(Literal.LEAVING);

		return errorDetails;

	}

	/**
	 * @param code
	 * @param string
	 * @return {@link ReasonCode}
	 */
	private ReasonCode getReasonCode(long code, String reasonCategory) {
		logger.debug(Literal.ENTERING);

		ReasonCode reasonCode = reasonCodeDAO.getReasonCode(code, reasonCategory, "_AView");

		logger.debug(Literal.LEAVING);
		return reasonCode;
	}

	/**
	 * @param id
	 * @param dealerType
	 * @return {@link VehicleDealer}
	 */
	private VehicleDealer getAgencyById(Long id, String dealerType) {
		logger.debug(Literal.ENTERING);
		VehicleDealer agencyDetails = vehicleDealerDAO.getVehicleDealerById(id, dealerType, "_AView");
		logger.debug(Literal.LEAVING);
		return agencyDetails;
	}

	/**
	 * Utility Method which returns Key value pairs of Return Type Ex:{1=INITIATE, 2=WAIVE, 3=NOT_REQUIRED, 4=REQUEST}
	 * 
	 * @return {@link Map}
	 */
	private Map<String, String> getRequestTypeValues() {
		logger.debug(Literal.ENTERING);
		List<ValueLabel> list = RequestType.getList();
		RequestType[] values = RequestType.values();
		HashMap<String, String> requestMap = new HashMap<>();
		int i = 0;
		for (ValueLabel valueLabel : list) {
			for (; i < values.length;) {
				requestMap.put(valueLabel.getValue(), values[i].name());
				i++;
				break;
			}
		}
		logger.debug(Literal.LEAVING);
		return requestMap;
	}

	/**
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus initiatePDVerification(Verification verification) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = new WSReturnStatus();
		String keyReference = verification.getKeyReference();

		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}
		// get financedetail object
		FinanceDetail financeDetail = getFinanceDetails(finID);
		if (financeDetail == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "loan data not found";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		response = validatePDinitiation(verification, financeDetail);
		if (response != null) {
			return response;
		}

		if (response == null) {
			financeDetail.setPdVerification(verification);
			try {
				response = verificationController.createPDInitiation(financeDetail, VerificationType.PD);
			} catch (Exception e) {
				return APIErrorHandlerService.getFailedStatus();
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private WSReturnStatus validatePDinitiation(Verification verification, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		// validate customer details
		customerDetails = financeDetail.getCustomerDetails();
		if (customerDetails == null) {
			String valueParm[] = new String[1];
			valueParm[0] = customerDetails.getCustCIF();
			return APIErrorHandlerService.getFailedStatus("90101", valueParm);
		}
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (verification.getVerificationType() == VerificationType.FI.getKey()) {
			if (CollectionUtils.isEmpty(addressList)) {
				String valueParm[] = new String[4];
				valueParm[0] = "No address";
				valueParm[1] = "found for this Customer";
				valueParm[2] = customerDetails.getCustCIF();
				valueParm[3] = "";
				return APIErrorHandlerService.getFailedStatus("21005", valueParm);
			}
		}

		// validate verificationType
		List<Verification> verificationsList = verification.getVerifications();
		VerificationType verificationType = null;
		if (verification.getVerificationType() > 0) {
			verificationType = VerificationType.getVerificationType(verification.getVerificationType());
			if (verificationType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(verification.getVerificationType());
				valueParm[1] = "Verification Type";
				return getErrorDetails("90329", valueParm);
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationType";
			return getErrorDetails("90502", valueParm);
		}
		if (verification.getVerificationType() != VerificationType.PD.getKey()) {
			String[] valueParm = new String[2];
			valueParm[0] = "VerificationType :" + String.valueOf(verification.getVerificationType());
			valueParm[1] = String.valueOf(VerificationType.PD.getKey());
			return getErrorDetails("90337", valueParm);
		}

		if (CollectionUtils.isEmpty(verificationsList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Verifications";
			return getErrorDetails("90502", valueParm);
		}

		for (Verification vrf : verificationsList) {
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setKeyReference(verification.getKeyReference());

			if (StringUtils.isBlank(vrf.getReferenceFor())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ReferenceFor";
				return getErrorDetails("90502", valueParm);
			}
			Long verificationID = getVerificationId(vrf, VerificationType.PD);
			if (verificationID != null) {
				vrf.setId(verificationID);
				if (verificationService.isVerificationInRecording(vrf, VerificationType.PD)) {
					String[] valueParm = new String[3];
					valueParm[0] = "ReferenceFor: " + vrf.getReferenceFor();
					valueParm[1] = "for custCIF :" + vrf.getCif();
					valueParm[2] = "and keyReference: " + verification.getKeyReference() + " Already Processed..";
					return getErrorDetails("21005", valueParm);
				}
			}
			// validate RequestType
			RequestType requestType = RequestType.getType(vrf.getRequestType());
			Map<String, String> requestTypeMap = getRequestTypeValues();
			if (requestType == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "RequestType is Invalid";
				valueParm[1] = "Available Values are";
				valueParm[2] = requestTypeMap.toString();
				return getErrorDetails("21005", valueParm);
			}
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				// Reason values are set to be null in case values are processed with API
				if (vrf.getReason() == null || vrf.getReason() <= 0) {
					vrf.setReason(null);
				}
				if (vrf.getAgency() == null || vrf.getAgency() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Agency";
					return getErrorDetails("90502", valueParm);
				} else {
					VehicleDealer agencyDetails = getAgencyById(vrf.getAgency(), Agencies.PDAGENCY.getKey());
					if (agencyDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Agency Id :" + String.valueOf(vrf.getAgency());// value should provide from
																						// AMTVehicleDealer_AView
																						// Dealerid
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setAgencyName(agencyDetails.getDealerName());
						vrf.setAgencyCity(agencyDetails.getDealerCity());
					}
				}

			}
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				// Agency values are set to be null in case values are processed with API
				if (vrf.getAgency() == null || vrf.getAgency() <= 0) {
					vrf.setAgency(null);
				}
				if (vrf.getReason() == null || vrf.getReason() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return getErrorDetails("90502", valueParm);
				} else {
					ReasonCode reasonCode = getReasonCode(vrf.getReason(), WaiverReasons.PDWRES.getKey());
					if (reasonCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(vrf.getReason());// value should provide from Reasons_AView id
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setReasonName(reasonCode.getReasonTypeDesc());
					}
				}
			}
			if (vrf.getRequestType() == RequestType.REQUEST.getKey()) {
				if (vrf.getRemarks() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Remarks";
					return getErrorDetails("90502", valueParm);
				}
			}
			if (StringUtils.isBlank(vrf.getRemarks())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Remarks";
				return getErrorDetails("90502", valueParm);
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public WSReturnStatus recordPDVerification(PersonalDiscussion personalDiscussion) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = validateFields(personalDiscussion);
		try {
			if (response == null) {
				response = verificationController.recordPDVerification(personalDiscussion);
			} else {
				return response;
			}
		} catch (Exception e) {
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	/**
	 * @param personalDiscussion validations based on the API Request
	 */
	public WSReturnStatus validateFields(PersonalDiscussion personalDiscussion) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();

		String keyReference = personalDiscussion.getKeyReference();
		if (StringUtils.isBlank(keyReference)) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}

		// AddressType Validations
		if (StringUtils.isBlank(personalDiscussion.getAddressType())) {
			String[] valueParam = new String[1];
			valueParam[0] = "AddressType";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		List<Verification> veriFications = verificationDAO.getVeriFications(keyReference, VerificationType.PD.getKey());
		List<String> vrfAddress = veriFications.stream().map(a -> a.getReferenceFor()).collect(Collectors.toList());
		if (!vrfAddress.contains(personalDiscussion.getAddressType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "AddressType: " + personalDiscussion.getAddressType();
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		long verificationId = personalDiscussion.getVerificationId();
		if (verificationId <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "verificationId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		// get Verification Id's from Verifications Table with keyreference
		List<Long> verificationIds = verificationDAO.getVerificationIds(keyReference, VerificationType.PD.getKey(),
				RequestType.INITIATE.getKey());
		if (CollectionUtils.isEmpty(verificationIds)) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id";
			valueParam[2] = "Not";
			valueParam[3] = "Exists";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		if (!verificationIds.contains(verificationId)) {
			String[] valueParm = new String[1];
			valueParm[0] = "verificationId :" + verificationId;
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		boolean initiatedVerfication = verificationDAO.isInitiatedVerfication(VerificationType.PD, verificationId,
				"_Temp");
		if (!initiatedVerfication) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id: " + verificationId;
			valueParam[2] = "Already";
			valueParam[3] = "Processed";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		Date verifiedDate = personalDiscussion.getVerifiedDate();
		if (verifiedDate == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "VerifiedDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		String agentCode = personalDiscussion.getAgentCode();

		if (StringUtils.isBlank(agentCode)) {
			String[] valueParam = new String[1];
			valueParam[0] = "AgentCode";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		String agentName = personalDiscussion.getAgentName();
		if (StringUtils.isBlank(agentName)) {
			String[] valueParam = new String[1];
			valueParam[0] = "AgentName";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		if (personalDiscussion.getStatus() <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		String status = String.valueOf(personalDiscussion.getStatus());

		if (status == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		if (FIStatus.getType(personalDiscussion.getStatus()) == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recommendations";
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		ReasonCode reasonCode = null;
		if (personalDiscussion.getStatus() == FIStatus.NEGATIVE.getKey()
				|| personalDiscussion.getStatus() == FIStatus.REFER_TO_CREDIT.getKey()) {
			if (personalDiscussion.getReason() == null || personalDiscussion.getReason() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reason";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				if (personalDiscussion.getStatus() == FIStatus.NEGATIVE.getKey()) {
					reasonCode = getReasonCode(personalDiscussion.getReason(), StatuReasons.FISRES.getKey());
				} else {
					reasonCode = getReasonCode(personalDiscussion.getReason(), StatuReasons.FISRES.getKey());
				}
				if (reasonCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
				} else {
					personalDiscussion.setReasonName(reasonCode.getReasonTypeDesc());
				}
			}

		}

		// Extended Fields Validations
		List<ExtendedField> extendedDetails = personalDiscussion.getExtendedDetails();
		if (CollectionUtils.isEmpty(personalDiscussion.getExtendedDetails())) {
			List<ErrorDetail> errorDetails = validateExtendedFileds(extendedDetails, VerificationType.PD.getValue());
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90265", valueParm);

			}
		}

		if (CollectionUtils.isNotEmpty(extendedDetails)) {
			List<ErrorDetail> errorDetails = validateExtendedFileds(extendedDetails, VerificationType.PD.getValue());
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90265", valueParm);
			}
		}
		return null;
	}

	@Override
	public WSReturnStatus recordTVVerification(TechnicalVerification technicalVerification) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		returnStatus = validateTechnicalVerificationFields(technicalVerification);
		if (returnStatus == null) {
			returnStatus = verificationController.recordTVVerification(technicalVerification);
		}
		return returnStatus;

	}

	private WSReturnStatus validateTechnicalVerificationFields(TechnicalVerification technicalVerification) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();

		String keyReference = technicalVerification.getKeyReference();
		if (StringUtils.isBlank(keyReference)) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}

		String collateralRef = technicalVerification.getCollateralRef();
		if (StringUtils.isBlank(collateralRef)) {
			String[] valueParam = new String[1];
			valueParam[0] = "CollateralRef";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		List<Verification> verifications = verificationDAO.getVeriFications(keyReference, VerificationType.TV.getKey());
		List<String> colletralDbRef = verifications.stream().map(d -> d.getReferenceFor()).collect(Collectors.toList());
		if (!colletralDbRef.contains(collateralRef)) {
			String[] valueParm = new String[1];
			valueParm[0] = "CollateralRef: " + collateralRef;
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		String collateralType = technicalVerification.getCollateralType();
		if (StringUtils.isBlank(collateralType)) {
			String[] valueParam = new String[1];
			valueParam[0] = "CollateralType";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		List<String> colletralDbType = verifications.stream().map(d -> d.getReferenceType())
				.collect(Collectors.toList());
		if (!colletralDbType.contains(collateralType)) {
			String[] valueParm = new String[1];
			valueParm[0] = "collateralType: " + collateralType;
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		CollateralStructure collateralStructureByType = collateralStructureDAO
				.getCollateralStructureByType(collateralType, "_View");
		if (collateralStructureByType == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "CollateralType: " + collateralType;
			returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			return returnStatus;
		}

		long verificationId = technicalVerification.getVerificationId();
		if (verificationId <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "verificationId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		// get Verification Id's from Verifications Table with keyreference
		List<Long> verificationIds = verificationDAO.getVerificationIds(keyReference, VerificationType.TV.getKey(),
				RequestType.INITIATE.getKey());
		if (CollectionUtils.isEmpty(verificationIds)) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id";
			valueParam[2] = "Not";
			valueParam[3] = "Exists";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		if (!verificationIds.contains(verificationId)) {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationId: " + String.valueOf(verificationId);
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		boolean initiatedVerfication = verificationDAO.isInitiatedVerfication(VerificationType.TV, verificationId,
				"_Temp");
		if (!initiatedVerfication) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id: " + verificationId;
			valueParam[2] = "Already";
			valueParam[3] = "Processed";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}
		Date verifiedDate = technicalVerification.getVerifiedDate();
		if (verifiedDate == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "VerifiedDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		if (DateUtil.compare(verifiedDate, SysParamUtil.getAppDate()) != 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);// FIXME
			return returnStatus;
		}

		String agentName = technicalVerification.getAgentName();
		if (StringUtils.isBlank(agentName)) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentName";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		String agentCode = technicalVerification.getAgentCode();
		if (StringUtils.isBlank(agentCode)) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentCode";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}
		int status = technicalVerification.getStatus();
		if (status <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "Recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		if (FIStatus.getType(technicalVerification.getStatus()) == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recommendations";
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		ReasonCode reasonCode = null;
		if (technicalVerification.getStatus() == FIStatus.NEGATIVE.getKey()
				|| technicalVerification.getStatus() == FIStatus.REFER_TO_CREDIT.getKey()) {
			if (technicalVerification.getReason() == null || technicalVerification.getReason() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reason";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				if (technicalVerification.getStatus() == FIStatus.NEGATIVE.getKey()) {

					reasonCode = getReasonCode(technicalVerification.getReason(), StatuReasons.TVSRES.getKey());
				} else {
					reasonCode = getReasonCode(technicalVerification.getReason(), StatuReasons.TVSRES.getKey());
				}
				if (reasonCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);

				} else {
					technicalVerification.setReasonDesc(reasonCode.getReasonTypeDesc());
				}
			}

		}

		BigDecimal valuationAmount = technicalVerification.getValuationAmount();
		if (valuationAmount == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "valuationAmount";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		// Extended Fields Validations
		List<ExtendedField> extendedDetails = technicalVerification.getExtendedDetails();
		if (CollectionUtils.isEmpty(technicalVerification.getExtendedDetails())) {
			List<ErrorDetail> errorDetails = validateTVExtendedFileds(extendedDetails, collateralType);
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90265", valueParm);

			}
		}

		if (CollectionUtils.isNotEmpty(extendedDetails)) {
			List<ErrorDetail> errorDetails = validateTVExtendedFileds(extendedDetails, collateralType);
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[1];
				valueParm[0] = "extended fields";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90265", valueParm);
			}
		}

		return null;
	}

	private List<ErrorDetail> validateTVExtendedFileds(List<ExtendedField> extendedDetails, String collateralType) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(extendedDetails,
				CollateralConstants.MODULE_NAME, collateralType, VerificationType.TV.getValue());

		logger.debug(Literal.LEAVING);

		return errorDetails;

	}

	private List<ErrorDetail> validateLVExtendedFileds(List<ExtendedField> extendedDetails, String collateralType) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = extendedFieldDetailsService.validateExtendedFieldDetails(extendedDetails,
				CollateralConstants.MODULE_NAME, collateralType, VerificationType.LV.getValue());

		logger.debug(Literal.LEAVING);

		return errorDetails;

	}

	@Override
	public WSReturnStatus recordRCUVerification(RiskContainmentUnit riskContainmentUnit) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		returnStatus = validateRCUFields(riskContainmentUnit);
		if (returnStatus == null) {
			returnStatus = verificationController.recordRCUVerification(riskContainmentUnit);
		}
		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	private WSReturnStatus validateRCUFields(RiskContainmentUnit riskContainmentUnit) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();

		String keyReference = riskContainmentUnit.getKeyReference();
		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "keyReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}

		long verificationId = riskContainmentUnit.getVerificationId();
		if (verificationId <= 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);// FIXME
			return returnStatus;
		}

		Date verificationDate = riskContainmentUnit.getVerificationDate();
		if (verificationDate == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);// FIXME
			return returnStatus;
		}
		if (DateUtil.compare(verificationDate, SysParamUtil.getAppDate()) != 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "VerificationDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);// FIXME
			return returnStatus;
		}
		String agentName = riskContainmentUnit.getAgentName();
		if (StringUtils.isBlank(agentName)) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentName";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);// FIXME
			return returnStatus;
		}

		String agentCode = riskContainmentUnit.getAgentCode();
		if (StringUtils.isBlank(agentCode)) {
			String valueParm[] = new String[1];
			valueParm[0] = "AgentCode";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		int status = riskContainmentUnit.getStatus();
		if (status <= 0) {
			String valueParm[] = new String[1];
			valueParm[0] = "Recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		if (RCUStatus.getType(status) == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "Recommendations";
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}
		ReasonCode reasonCode = null;
		if (riskContainmentUnit.getStatus() == RCUStatus.NEGATIVE.getKey()
				|| riskContainmentUnit.getStatus() == RCUStatus.REFERTOCREDIT.getKey()) {
			if (riskContainmentUnit.getReason() == null || riskContainmentUnit.getReason() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reason";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			} else {
				if (riskContainmentUnit.getStatus() == FIStatus.NEGATIVE.getKey()) {

					reasonCode = getReasonCode(riskContainmentUnit.getReason(), StatuReasons.RCUSRES.getKey());
				} else {
					reasonCode = getReasonCode(riskContainmentUnit.getReason(), StatuReasons.RCUSRES.getKey());
				}
				if (reasonCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);

				} else {
					riskContainmentUnit.setReasonDesc(reasonCode.getReasonTypeDesc());
				}
			}

		} // get Verification Id's from Verifications Table with keyreference
		List<Long> verificationIds = verificationDAO.getVerificationIds(keyReference, VerificationType.RCU.getKey(),
				RequestType.INITIATE.getKey());
		if (CollectionUtils.isEmpty(verificationIds)) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id";
			valueParam[2] = "Not";
			valueParam[3] = "Exists";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		if (!verificationIds.contains(verificationId)) {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationId: " + String.valueOf(verificationId);
			return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		boolean initiatedVerfication = verificationDAO.isInitiatedVerfication(VerificationType.RCU, verificationId,
				"_Temp");
		if (!initiatedVerfication) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id: " + verificationId;
			valueParam[2] = "Already";
			valueParam[3] = "Processed";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		List<RCUDocument> rcuDocuments = riskContainmentUnit.getRcuDocuments();
		if (org.apache.commons.collections4.CollectionUtils.isEmpty(rcuDocuments)) {
			String valueParm[] = new String[1];
			valueParm[0] = "RcuDocuments";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);// FIXME
			return returnStatus;
		}

		List<RCUDocument> rcuDocumentsDb = riskContainmentUnitDAO.getRCUDocuments(verificationId, "_Temp");

		int rucDb = rcuDocumentsDb.size();
		int rcu = rcuDocuments.size();
		if (rcu != rucDb) {
			String[] valueParam = new String[4];
			valueParam[0] = "RCU Document";
			valueParam[1] = "Details";
			valueParam[2] = "Are Invalid";
			valueParam[3] = "";
			return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
		}

		for (RCUDocument rcuDocument : rcuDocuments) {

			int seqNo = rcuDocument.getSeqNo();
			if (seqNo <= 0) {
				String valueParm[] = new String[1];
				valueParm[0] = "Seqno";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}

			List<Integer> sequenceList = rcuDocuments.stream().map(s -> s.getSeqNo()).collect(Collectors.toList());
			List<Integer> dbSequenceNo = rcuDocumentsDb.stream().map(s -> s.getSeqNo()).collect(Collectors.toList());

			boolean equals = dbSequenceNo.containsAll(sequenceList);
			if (!equals) {
				String[] valueParam = new String[4];
				valueParam[0] = "SeqNo";
				valueParam[1] = "";
				valueParam[2] = "is Invalid";
				valueParam[3] = "";
				return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			}
			int documenttype = rcuDocument.getDocumentType();

			if (documenttype <= 0) {
				String valueParm[] = new String[1];
				valueParm[0] = "documenttype";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			String documentSubId = rcuDocument.getDocumentSubId();
			if (StringUtils.isBlank(documentSubId)) {
				String valueParm[] = new String[1];
				valueParm[0] = "Document Name";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			int verificationType = rcuDocument.getVerificationType();
			if (verificationType <= 0) {
				String valueParm[] = new String[1];
				valueParm[0] = "verificationType";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			int RCUstatus = rcuDocument.getStatus();
			if (RCUstatus <= 0) {
				String valueParm[] = new String[1];
				valueParm[0] = "RCUstatus";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			if (RCUDocVerificationType.getType(verificationType) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "VerificationType";
				return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
			}
			if (RCUDocStatus.getType(RCUstatus) == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "RCU STATUS";
				return returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
			}
			if (RCUDocVerificationType.SAMPLED.getKey() == verificationType) {
				int pagesSampled = rcuDocument.getPagesSampled();
				if (pagesSampled == 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesSampled";
					valueParam[1] = "is Mandatory";
					valueParam[2] = "when VerficationType 1";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}

				int pagesEyeballed = rcuDocument.getPagesEyeballed();
				if (pagesEyeballed != 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesEyeballed";
					valueParam[1] = "is not Required";
					valueParam[2] = "when VerficationType 1";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}

			}

			if (RCUDocVerificationType.EYEBALLED.getKey() == verificationType) {
				int pagesEyeballed = rcuDocument.getPagesEyeballed();
				if (pagesEyeballed == 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesEyeballed";
					valueParam[1] = "is Mandatory";
					valueParam[2] = "when VerficationType 2";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}

				int pageSampled = rcuDocument.getPagesSampled();
				if (pageSampled != 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesSampled";
					valueParam[1] = "is not Required";
					valueParam[2] = "when VerficationType 2";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}

			}
			if (RCUDocVerificationType.SCREENED.getKey() == verificationType) {
				int pageSampled = rcuDocument.getPagesSampled();
				if (pageSampled != 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesSampled";
					valueParam[1] = "is not Required";
					valueParam[2] = "when VerficationType 3";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
				}
				int pagesEyeballed = rcuDocument.getPagesEyeballed();
				if (pagesEyeballed != 0) {
					String[] valueParam = new String[4];
					valueParam[0] = "PagesEyeballed";
					valueParam[1] = "is not Required";
					valueParam[2] = "when VerficationType 3";
					valueParam[3] = "";
					return returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);

				}
			}

		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public VerificationDetails getVerificationDetails(Verification verification) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		VerificationDetails response = new VerificationDetails();
		Customer customer = new Customer();
		List<CustomerAddres> coApplicantAddressList = new ArrayList<>();
		List<CustomerAddres> primaryaddressList = new ArrayList<>();
		List<Verification> verifications = new ArrayList<>();
		List<JointAccountDetail> jointAccountDetailList = null;
		FinanceDetail financeDetails = null;

		String keyReference = verification.getKeyReference();

		if (StringUtils.isBlank(keyReference)) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			response.setReturnStatus(returnStatus);
			return response;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.VIEW);
		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParam);
			response.setReturnStatus(returnStatus);
			return response;
		}
		int verificationType = verification.getVerificationType();
		if (VerificationType.getVerificationType(verificationType) == null) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "type";
			valueParam[2] = "Not";
			valueParam[3] = "Valid";
			returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			response.setReturnStatus(returnStatus);
			return response;
		}
		if (verificationType == 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "verificationType";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			response.setReturnStatus(returnStatus);
			return response;

		}

		switch (verification.getVerificationType()) {

		case APIConstants.FI:
			financeDetails = getFinanceDetails(finID, VerificationType.FI, "_View");
			if (financeDetails == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}

			List<CustomerAddres> addressList = financeDetails.getCustomerDetails().getAddressList();
			if (CollectionUtils.isEmpty(addressList)) {
				String valueParm[] = new String[4];
				valueParm[0] = "Address Details with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}
			customer = financeDetails.getCustomerDetails().getCustomer();
			for (CustomerAddres customerAddres : addressList) {
				customerAddres.setLovDescCustCIF(customer.getCustCIF());
				customerAddres.setLovDescCustShrtName(customer.getCustShrtName());
			}

			jointAccountDetailList = financeDetails.getJointAccountDetailList();
			if (CollectionUtils.isNotEmpty((jointAccountDetailList))) {
				for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
					long custID = jointAccountDetail.getCustomerDetails().getCustID();
					Customer coCustromer = jointAccountDetail.getCustomerDetails().getCustomer();
					coApplicantAddressList = customerAddresDAO.getCustomerAddresByCustomer(custID, "");
					for (CustomerAddres customeraddress : coApplicantAddressList) {
						customeraddress.setLovDescCustCIF(coCustromer.getCustCIF());
						customeraddress.setLovDescCustShrtName(coCustromer.getCustShrtName());
					}

				}
			}
			response.setPrimaryCustomerAddress(addressList);
			response.setCoApplicantsAddress(coApplicantAddressList);

			break;

		case APIConstants.PD:

			financeDetails = getFinanceDetails(finID, VerificationType.PD, "_View");
			if (financeDetails == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}

			customer = financeDetails.getCustomerDetails().getCustomer();
			CustomerAddres primarycustAddr = customerAddresDAO.getHighPriorityCustAddr(customer.getCustID(), "");
			if (primarycustAddr != null) {
				primarycustAddr.setLovDescCustCIF(customer.getCustCIF());
				primarycustAddr.setLovDescCustShrtName(customer.getCustShrtName());
			}

			if (primarycustAddr == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Address Details with FinReference: " + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}
			primaryaddressList.add(primarycustAddr);
			response.setPrimaryCustomerAddress(primaryaddressList);

			jointAccountDetailList = financeDetails.getJointAccountDetailList();
			if (org.apache.commons.collections.CollectionUtils.isNotEmpty(jointAccountDetailList)) {
				for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
					long custID = jointAccountDetail.getCustomerDetails().getCustID();
					Customer coCustromer = jointAccountDetail.getCustomerDetails().getCustomer();
					CustomerAddres priorityCustAddr = customerAddresDAO.getHighPriorityCustAddr(custID, "");
					if (priorityCustAddr != null) {
						priorityCustAddr.setLovDescCustCIF(coCustromer.getCustCIF());
						priorityCustAddr.setLovDescCustShrtName(coCustromer.getCustShrtName());
					}
					coApplicantAddressList.add(priorityCustAddr);
					response.setCoApplicantsAddress(coApplicantAddressList);
				}
			}
			break;

		case APIConstants.TV:

			financeDetails = getFinanceDetails(finID, VerificationType.TV, "_View");
			if (financeDetails == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}

			List<CollateralAssignment> collaterals = financeDetails.getCollateralAssignmentList();
			if (CollectionUtils.isEmpty(collaterals)) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}
			for (CollateralAssignment collateralSetup : collaterals) {
				verification.setCollRef(collateralSetup.getCollateralRef());
				verification.setReferenceFor(collateralSetup.getCollateralType());
				verification.setCif(collateralSetup.getDepositorCIF());
				verifications.add(verification);
			}
			response.setVerificationDetailsList(verifications);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			break;
		case APIConstants.RCU:

			financeDetails = getFinanceDetails(finID, VerificationType.RCU, "_View");
			if (financeDetails == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}
			CustomerDetails customerDetails = financeDetails.getCustomerDetails();
			List<CollateralAssignment> collateralsDocRCU = financeDetails.getCollateralAssignmentList();
			List<DocumentDetails> collateralDocumentsRCU;
			if (CollectionUtils.isNotEmpty(collateralsDocRCU)) {
				for (CollateralAssignment collateral : collateralsDocRCU) {
					collateralDocumentsRCU = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
							CollateralConstants.MODULE_NAME, "", "_View");
					if (CollectionUtils.isNotEmpty(collateralDocumentsRCU)) {
						for (DocumentDetails documentDetails : collateralDocumentsRCU) {
							if (customerDetails != null) {
								documentDetails.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
								documentDetails.setLovDescCustShrtName(customerDetails.getCustomer().getCustShrtName());
								documentDetails.setRefId(documentDetails.getReferenceId());
							}
						}
						response.setCollateralsDocumentsList(collateralDocumentsRCU);
					}
				}
			}

			// CustomerDocuments
			List<CustomerDocument> customerDocumentsList = financeDetails.getCustomerDetails()
					.getCustomerDocumentsList();
			if (CollectionUtils.isNotEmpty(customerDocumentsList)) {
				if (customerDetails != null) {
					for (CustomerDocument customerDocument : customerDocumentsList) {
						customerDocument.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
						customerDocument.setLovDescCustShrtName(customerDetails.getCustomer().getCustShrtName());
					}
				}
				response.setCustomerDocumentsList(customerDocumentsList);
			}
			// LoanDocuments
			List<DocumentDetails> documentDetailsList = financeDetails.getDocumentDetailsList();
			if (CollectionUtils.isNotEmpty(documentDetailsList)) {
				for (DocumentDetails documentDetails : documentDetailsList) {
					if (customerDetails != null) {
						documentDetails.setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
						documentDetails.setLovDescCustShrtName(customerDetails.getCustomer().getCustShrtName());
					}
				}
				response.setLoanDocumentsList(documentDetailsList);
			}

			// CoApplicant Documents
			CustomerDetails cd = new CustomerDetails();
			List<CustomerDocument> coApplicatnDocs = new ArrayList<>();
			List<JointAccountDetail> jointAccountDetails = financeDetails.getJointAccountDetailList();
			for (JointAccountDetail jointAccountDetail : jointAccountDetails) {
				cd = jointAccountDetail.getCustomerDetails();
				coApplicatnDocs = jointAccountDetail.getCustomerDetails().getCustomerDocumentsList();
				if (cd != null && !coApplicatnDocs.isEmpty()) {
					for (CustomerDocument coApp : coApplicatnDocs) {
						coApp.setLovDescCustCIF(cd.getCustomer().getCustCIF());
						coApp.setLovDescCustShrtName(cd.getCustomer().getCustShrtName());
					}
				}
				response.setCoApptDocumentsList(coApplicatnDocs);
			}
			break;
		case APIConstants.LV:

			financeDetails = getFinanceDetails(finID, VerificationType.RCU, "_View");
			if (financeDetails == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}

			CustomerDetails customerDetailsLV = financeDetails.getCustomerDetails();
			// ColletralDocuments
			List<CollateralAssignment> collateralsDocLV = financeDetails.getCollateralAssignmentList();
			List<DocumentDetails> collateralDocumentsLV;
			if (CollectionUtils.isNotEmpty(collateralsDocLV)) {
				for (CollateralAssignment collateral : collateralsDocLV) {
					collateralDocumentsLV = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
							CollateralConstants.MODULE_NAME, "", "_View");
					if (CollectionUtils.isNotEmpty(collateralDocumentsLV)) {
						for (DocumentDetails documentDetails : collateralDocumentsLV) {
							if (customerDetailsLV != null) {
								documentDetails.setLovDescCustCIF(customerDetailsLV.getCustomer().getCustCIF());
								documentDetails
										.setLovDescCustShrtName(customerDetailsLV.getCustomer().getCustShrtName());
								documentDetails.setDocTypeId(DocumentType.COLLATRL.getKey());
								documentDetails.setDocName(documentDetails.getDocCategory());

							}
						}
						response.setCollateralsDocumentsList(collateralDocumentsLV);
					}
				}
			} else {
				String valueParm[] = new String[4];
				valueParm[0] = "Verifications with FinReference  :" + keyReference;
				valueParm[1] = "not Found";
				valueParm[2] = " ";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			}

			// CustomerDocuments
			List<CustomerDocument> customerDocumentsListLV = financeDetails.getCustomerDetails()
					.getCustomerDocumentsList();
			if (CollectionUtils.isNotEmpty(customerDocumentsListLV)) {
				if (customerDetailsLV != null) {
					for (CustomerDocument customerDocument : customerDocumentsListLV) {
						customerDocument.setLovDescCustCIF(customerDetailsLV.getCustomer().getCustCIF());
						customerDocument.setLovDescCustShrtName(customerDetailsLV.getCustomer().getCustShrtName());
						customerDocument.setDocTypeId(DocumentType.CUSTOMER.getKey());
						customerDocument.setCustDocName(customerDocument.getCustDocCategory());
					}
				}
				response.setCustomerDocumentsList(customerDocumentsListLV);
			}
			// LoanDocuments
			List<DocumentDetails> documentDetailsListLV = financeDetails.getDocumentDetailsList();
			if (CollectionUtils.isNotEmpty(documentDetailsListLV)) {
				for (DocumentDetails documentDetails : documentDetailsListLV) {
					if (customerDetailsLV != null) {
						documentDetails.setLovDescCustCIF(customerDetailsLV.getCustomer().getCustCIF());
						documentDetails.setLovDescCustShrtName(customerDetailsLV.getCustomer().getCustShrtName());
						documentDetails.setDocTypeId(DocumentType.LOAN.getKey());
						documentDetails.setDocName(documentDetails.getDocCategory());

					}
				}
				response.setLoanDocumentsList(documentDetailsListLV);
			}
			break;
		default:
			String valueParm[] = new String[4];
			valueParm[0] = "Verifications with FinReference :" + keyReference;
			valueParm[1] = " and VerificationType: " + verification.getVerificationType();
			valueParm[2] = "Not Found";
			valueParm[3] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
			response.setReturnStatus(returnStatus);
			return response;
		}

		return response;
	}

	@Override
	public WSReturnStatus initiateTVVerification(Verification verification) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = new WSReturnStatus();
		String keyReference = verification.getKeyReference();
		// validate loan reference
		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[2];
			valueParm[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}
		// get financedetail object
		FinanceDetail financeDetail = getFinanceDetails(finID, VerificationType.TV, "_View");
		if (financeDetail == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "loan data not found";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		response = validateTVnitiation(verification, financeDetail);
		if (response != null) {
			return response;
		}

		if (response == null) {
			financeDetail.setTvVerification(verification);
			try {
				response = verificationController.createTVInitaion(financeDetail, VerificationType.TV);
			} catch (Exception e) {
				return response;
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private WSReturnStatus validateTVnitiation(Verification verification, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// validate collaterals details
		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		VehicleDealer agencyDetails = null;

		// validate verificationType
		List<Verification> verificationsList = verification.getVerifications();
		VerificationType verificationType = null;

		if (verification.getVerificationType() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationType";
			return getErrorDetails("90502", valueParm);
		}
		if (verification.getVerificationType() > 0) {
			verificationType = VerificationType.getVerificationType(verification.getVerificationType());
			if (verificationType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(verification.getVerificationType());
				valueParm[1] = "Verification Type";
				return getErrorDetails("90329", valueParm);
			}
		}
		if (verification.getVerificationType() != VerificationType.TV.getKey()) {
			String[] valueParm = new String[2];
			valueParm[0] = "VerificationType :" + String.valueOf(verification.getVerificationType());
			valueParm[1] = String.valueOf(VerificationType.TV.getKey());
			return getErrorDetails("90337", valueParm);
		}
		if (CollectionUtils.isEmpty(verificationsList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Verifications";
			return getErrorDetails("90502", valueParm);
		}

		for (Verification vrf : verificationsList) {
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setReferenceFor(vrf.getCollRef());
			vrf.setKeyReference(verification.getKeyReference());

			Long verificationID = getVerificationId(vrf, VerificationType.TV);
			if (verificationID != null) {
				vrf.setId(verificationID);
				if (verificationService.isVerificationInRecording(vrf, VerificationType.TV)) {
					String[] valueParm = new String[4];
					valueParm[0] = "CollateralType: " + vrf.getReferenceFor();
					valueParm[1] = "for custCIF :" + vrf.getCif();
					valueParm[2] = "and keyReference: " + verification.getKeyReference() + " Already Processed..";
					return getErrorDetails("21005", valueParm);
				}
			}
			// validate collateral details
			if (CollectionUtils.isEmpty(collaterals)) {
				String valueParm[] = new String[4];
				valueParm[0] = "No Collaterals";
				valueParm[1] = "found for this";
				valueParm[2] = verification.getKeyReference();
				valueParm[3] = "";
				return APIErrorHandlerService.getFailedStatus("21005", valueParm);
			}
			if (StringUtils.isBlank(vrf.getCollateralType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "CollateralType";
				return getErrorDetails("90502", valueParm);
			}
			for (CollateralAssignment collateral : collaterals) {
				if (!collateral.getCollateralType().equalsIgnoreCase(vrf.getCollateralType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "CollateralType";
					return getErrorDetails("RU0040", valueParm);
				}
				if (StringUtils.isBlank(vrf.getCustomerName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "name";
					return getErrorDetails("90502", valueParm);
				}

			}
			if (vrf.getVerificationCategory() <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "VerificationCategory";
				return getErrorDetails("90502", valueParm);
			}
			VerificationCategory verificationCategory = VerificationCategory.getType(vrf.getVerificationCategory());
			if (verificationCategory == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "VerificationCategory";
				return getErrorDetails("RU0040", valueParm);
			}
			// validate RequestType
			RequestType requestType = RequestType.getType(vrf.getRequestType());
			Map<String, String> requestTypeMap = getRequestTypeValues();
			if (requestType == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "RequestType is Invalid";
				valueParm[1] = "Available Values are";
				valueParm[2] = requestTypeMap.toString();
				return getErrorDetails("21005", valueParm);
			}
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				// Reason values are set to be null in case values are processed with API
				if (vrf.getReason() != null && vrf.getReason() > 0) {
					vrf.setReason(null);
				}
				if (vrf.getAgency() == null || vrf.getAgency() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Agency";
					return getErrorDetails("90502", valueParm);
				} else {
					if (vrf.getVerificationCategory() == VerificationCategory.INTERNAL.getKey()) {
						agencyDetails = getAgencyById(vrf.getAgency(), Agencies.TVAGENCY.getKey());
						if (agencyDetails != null) {
							if (!StringUtils.equalsIgnoreCase(agencyDetails.getDealerName(),
									VerificationCategory.INTERNAL.getValue())) {
								String[] valueParm = new String[1];
								valueParm[0] = "Internal Agnecy :" + String.valueOf(vrf.getAgency());
								return getErrorDetails("RU0040", valueParm);
							}
						}
					}
					if (vrf.getVerificationCategory() == VerificationCategory.EXTERNAL.getKey()) {
						agencyDetails = getAgencyById(vrf.getAgency(), Agencies.TVAGENCY.getKey());
					}
					if (agencyDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Agency Id :" + String.valueOf(vrf.getAgency());// value should provide from
																						// AMTVehicleDealer_AView
																						// Dealerid
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setAgencyName(agencyDetails.getDealerName());
						vrf.setAgencyCity(agencyDetails.getDealerCity());
					}
				}
			}
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				// Agency values are set to be null in case values are processed with API
				if (vrf.getAgency() != null && vrf.getAgency() > 0) {
					vrf.setAgency(null);
				}
				if (vrf.getReason() == null || vrf.getReason() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return getErrorDetails("90502", valueParm);
				} else {
					ReasonCode reasonCode = getReasonCode(vrf.getReason(), WaiverReasons.TVWRES.getKey());
					if (reasonCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(vrf.getReason());// value should provide from Reasons_AView id
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setReasonName(reasonCode.getReasonTypeDesc());
					}
				}
			}
			if (vrf.getRequestType() == RequestType.REQUEST.getKey()) {
				if (vrf.getRemarks() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Remarks";
					return getErrorDetails("90502", valueParm);
				}
			}
			if (StringUtils.isBlank(vrf.getRemarks())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Remarks";
				return getErrorDetails("90502", valueParm);
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public WSReturnStatus initiateRCUVerification(Verification verification) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = new WSReturnStatus();
		String keyReference = verification.getKeyReference();
		// validate loan reference
		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}
		// get financedetail object
		FinanceDetail financeDetail = getFinanceDetails(finID, VerificationType.RCU, "_View");
		if (financeDetail == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "Loan data not found";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// Validate RCU details
		response = validateRCUinitiation(verification, financeDetail);

		if (response != null) {
			return response;
		}

		if (response == null) {
			financeDetail.setRcuVerification(verification);
			try {
				// group by verification records
				response = verificationController.initiateRCU(financeDetail, VerificationType.RCU);
			} catch (Exception e) {
				return response;
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	private WSReturnStatus validateRCUinitiation(Verification verification, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		// validate verifcation details
		customerDetails = financeDetail.getCustomerDetails();
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
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
		// get joint account documents
		List<JointAccountDetail> jointAccountDetailList = financeDetail.getJointAccountDetailList();
		if (CollectionUtils.isNotEmpty(jointAccountDetailList)) {
			for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
				coAppDocumentsList = jointAccountDetail.getCustomerDetails().getCustomerDocumentsList();
			}
		}
		// get all loan documents
		List<DocumentDetails> financeDocumentsList = financeDetail.getDocumentDetailsList();
		if (CollectionUtils.isEmpty(collateralDocumentList) && CollectionUtils.isEmpty(customerDocumentsList)
				&& CollectionUtils.isEmpty(coAppDocumentsList) && CollectionUtils.isEmpty(financeDocumentsList)) {
			String valueParm[] = new String[4];
			valueParm[0] = "No verifications";
			valueParm[1] = "found with KeyReference";
			valueParm[2] = verification.getKeyReference();
			valueParm[3] = "";
			return APIErrorHandlerService.getFailedStatus("30550", valueParm);
		}

		if (customerDetails == null) {
			String valueParm[] = new String[1];
			valueParm[0] = customerDetails.getCustCIF();
			return APIErrorHandlerService.getFailedStatus("90101", valueParm);
		}

		// validate verificationType
		List<Verification> verificationsList = verification.getVerifications();
		VerificationType verificationType = null;
		if (verification.getVerificationType() > 0) {
			verificationType = VerificationType.getVerificationType(verification.getVerificationType());
			if (verificationType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(verification.getVerificationType());
				valueParm[1] = "Verification Type";
				return getErrorDetails("90329", valueParm);
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationType";
			return getErrorDetails("90502", valueParm);
		}
		if (verification.getVerificationType() != VerificationType.RCU.getKey()) {
			String[] valueParm = new String[2];
			valueParm[0] = "VerificationType: " + String.valueOf(verification.getVerificationType());
			valueParm[1] = String.valueOf(VerificationType.RCU.getKey());
			return getErrorDetails("90337", valueParm);

		}
		if (CollectionUtils.isEmpty(verificationsList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Verifications";
			return getErrorDetails("90502", valueParm);
		}

		for (Verification vrf : verificationsList) {
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setKeyReference(verification.getKeyReference());

			if (StringUtils.isBlank(vrf.getReferenceFor())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ReferenceFor";
				return getErrorDetails("90502", valueParm);
			}

			// NEED TO SET REFERENCE TYPE
			Long verificationID = getVerificationId(vrf, VerificationType.RCU);
			if (verificationID != null) {
				vrf.setId(verificationID);
				if (verificationService.isVerificationInRecording(vrf, VerificationType.RCU)) {
					String[] valueParm = new String[3];
					valueParm[0] = "ReferenceFor: " + vrf.getReferenceFor();
					valueParm[1] = "for custCIF :" + vrf.getCif();
					valueParm[2] = "and keyReference: " + verification.getKeyReference() + " Already Processed..";
					return getErrorDetails("21005", valueParm);
				}
			}
			// validate RequestType
			RequestType requestType = RequestType.getType(vrf.getRequestType());
			Map<String, String> requestTypeMap = getRequestTypeValues();
			if (requestType == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "RequestType is Invalid";
				valueParm[1] = "Available Values are";
				valueParm[2] = requestTypeMap.toString();
				return getErrorDetails("21005", valueParm);
			}
			Long reason = vrf.getReason();
			if (vrf.getRequestType() == RequestType.INITIATE.getKey()) {
				// Reason values are set to be null in case values are processed with API

				if (reason == null) {
					reason = 1L;
				}
				if (reason > 0) {
					vrf.setReason(null);
					reason = null;
				}
				if (vrf.getAgency() == null || vrf.getAgency() < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Agency";
					return getErrorDetails("90502", valueParm);
				} else {
					VehicleDealer agencyDetails = getAgencyById(vrf.getAgency(), Agencies.RCUVAGENCY.getKey());
					if (agencyDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Agency Id :" + String.valueOf(vrf.getAgency());// value should provide from
																						// AMTVehicleDealer_AView
																						// Dealerid
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setAgencyName(agencyDetails.getDealerName());
						vrf.setAgencyCity(agencyDetails.getDealerCity());
					}
				}

			}
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				// Agency values are set to be null in case values are processed with API
				if (vrf.getAgency() != null || vrf.getAgency() > 0) {
					vrf.setAgency(null);
				}
				if (reason == null || reason < 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					return getErrorDetails("90502", valueParm);
				} else {
					ReasonCode reasonCode = getReasonCode(reason, WaiverReasons.RCUWRES.getKey());
					if (reasonCode == null) {
						String[] valueParm = new String[1];
						valueParm[0] = String.valueOf(reason);// value should provide from Reasons_AView id
						return getErrorDetails("RU0040", valueParm);
					} else {
						vrf.setReasonName(reasonCode.getReasonTypeDesc());
					}
				}
			}
			if (vrf.getRequestType() == RequestType.REQUEST.getKey()) {
				if (vrf.getRemarks() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Remarks";
					return getErrorDetails("90502", valueParm);
				}
			}
			if (StringUtils.isBlank(vrf.getRemarks())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Remarks";
				return getErrorDetails("90502", valueParm);
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public WSReturnStatus initiateLVVerification(Verification verification) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = new WSReturnStatus();
		String keyReference = verification.getKeyReference();
		// validate loan reference
		if (StringUtils.isBlank(keyReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "keyReference";
			return APIErrorHandlerService.getFailedStatus("90201", valueParam);
		}
		// get financedetail object
		FinanceDetail financeDetail = getFinanceDetails(finID, VerificationType.LV, "_View");
		if (financeDetail == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "Loan data not found";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// Validate LV details
		response = validateLVinitiation(verification, financeDetail);
		if (response != null) {
			return response;
		}
		if (response == null) {
			financeDetail.setLvVerification(verification);
			try {
				response = verificationController.initiateLV(financeDetail, VerificationType.LV);
			} catch (Exception e) {
				return APIErrorHandlerService.getFailedStatus();
			}
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	private WSReturnStatus validateLVinitiation(Verification verification, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		customerDetails = financeDetail.getCustomerDetails();
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
		if (CollectionUtils.isEmpty(collateralDocumentList)) {
			String[] valueParm = new String[4];
			valueParm[0] = "No Collateral ";
			valueParm[1] = "Documents Found";
			valueParm[2] = "with keyReference : " + verification.getKeyReference();
			return getErrorDetails("21005", valueParm);
		}
		// validate verificationType
		List<Verification> verificationsList = verification.getVerifications();
		VerificationType verificationType = null;
		if (verification.getVerificationType() > 0) {
			verificationType = VerificationType.getVerificationType(verification.getVerificationType());
			if (verificationType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = String.valueOf(verification.getVerificationType());
				valueParm[1] = "Verification Type";
				return getErrorDetails("90329", valueParm);
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "VerificationType";
			return getErrorDetails("90502", valueParm);
		}
		if (verification.getVerificationType() != VerificationType.LV.getKey()) {
			String[] valueParm = new String[2];
			valueParm[0] = "VerificationType: " + String.valueOf(verification.getVerificationType());
			valueParm[1] = String.valueOf(VerificationType.LV.getKey());
			return getErrorDetails("90337", valueParm);

		}
		if (CollectionUtils.isEmpty(verificationsList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Verifications";
			return getErrorDetails("90502", valueParm);
		}
		int count = 0;
		// check collateral documents in request. At least one collateral Document is Mandatory
		for (Verification vrf : verificationsList) {
			List<LVDocument> lvDocuments = vrf.getLvDocuments();
			for (LVDocument lvDocument : lvDocuments) {
				if (lvDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
					count++;
				}
			}
		}
		if (count == 0) {
			String[] valueParm = new String[4];
			valueParm[0] = "At Least One ";
			valueParm[1] = "Collateral Document";
			valueParm[2] = "is Mandatory in the Request";
			return getErrorDetails("21005", valueParm);
		}
		for (Verification vrf : verificationsList) {
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setRequestType(RequestType.INITIATE.getKey());
			if (StringUtils.isBlank(vrf.getReferenceFor())) {
				String[] valueParm = new String[1];
				valueParm[0] = "ReferenceFor";
				return getErrorDetails("90502", valueParm);
			}
			Long verificationID = getVerificationId(vrf, VerificationType.LV);
			if (verificationID != null) {
				vrf.setId(verificationID);
				if (verificationService.isVerificationInRecording(vrf, VerificationType.LV)) {
					String[] valueParm = new String[4];
					valueParm[0] = "ReferenceFor: " + vrf.getReferenceFor();
					valueParm[1] = "for custCIF :" + vrf.getCif();
					valueParm[2] = "and keyReference: " + verification.getKeyReference() + " Already Processed..";
					return getErrorDetails("21005", valueParm);
				}
			}
			// validate VerificationCategory
			if (vrf.getVerificationCategory() <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "VerificationCategory";
				return getErrorDetails("90502", valueParm);
			}

			LegaVerificationType verificationcategory = LegaVerificationType.getType(vrf.getVerificationCategory());
			if (verificationcategory == null) {
				String[] valueParm = new String[3];
				valueParm[0] = "verificationcategory is Invalid";
				valueParm[1] = "Available Values are";
				valueParm[2] = "{1=LV, 2=TSR}";
				return getErrorDetails("21005", valueParm);
			}

			if (vrf.getAgency() == null || vrf.getAgency() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Agency";
				return getErrorDetails("90502", valueParm);
			} else {
				VehicleDealer agencyDetails = getAgencyById(vrf.getAgency(), Agencies.LVAGENCY.getKey());
				if (agencyDetails == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Agency Id :" + String.valueOf(vrf.getAgency());
					return getErrorDetails("RU0040", valueParm);
				} else {
					vrf.setAgencyName(agencyDetails.getDealerName());
					vrf.setAgencyCity(agencyDetails.getDealerCity());
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public WSReturnStatus recordLVVerification(LegalVerification legalVerification) throws ServiceException {
		logger.debug(Literal.ENTERING);
		WSReturnStatus response = new WSReturnStatus();

		response = validateLVFields(legalVerification);

		if (response == null) {
			response = verificationController.recordLVVerification(legalVerification);
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	/**
	 * @param legalVerification
	 * @param response
	 */
	public WSReturnStatus validateLVFields(LegalVerification legalVerification) {
		WSReturnStatus returnStatus = new WSReturnStatus();
		// validations for the LV verification
		String keyReference = legalVerification.getKeyReference();

		if (StringUtils.isBlank(keyReference)) {
			String valueParam[] = new String[2];
			valueParam[0] = "keyReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		Long finID = financeMainDAO.getActiveFinID(keyReference, TableType.BOTH_TAB);

		if (finID == null) {
			String[] valueParam = new String[1];
			valueParam[0] = keyReference;
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParam);

			return returnStatus;
		}

		long verificationId = legalVerification.getVerificationId();
		if (verificationId <= 0) {
			String valueParam[] = new String[2];
			valueParam[0] = "VerificationId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		Date verificationDate = legalVerification.getVerificationDate();
		if (verificationDate == null) {
			String valueParam[] = new String[2];
			valueParam[0] = "VerificationDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		if (DateUtil.compare(verificationDate, SysParamUtil.getAppDate()) != 0) {
			String valueParam[] = new String[2];
			valueParam[0] = "VerificationDate";
			returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			return returnStatus;
		}
		String agentName = legalVerification.getAgentName();
		if (StringUtils.isBlank(agentName)) {
			String valueParam[] = new String[2];
			valueParam[0] = "AgentName";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		String agentCode = legalVerification.getAgentCode();
		if (StringUtils.isBlank(agentCode)) {
			String valueParam[] = new String[2];
			valueParam[0] = "AgentCode";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}
		int status = legalVerification.getStatus();
		if (status <= 0) {
			String valueParam[] = new String[2];
			valueParam[0] = "Recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		if (LVStatus.getType(status) == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "Recommendations";
			returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			return returnStatus;
		}

		ReasonCode reasonCode = null;
		if (legalVerification.getStatus() == LVStatus.NEGATIVE.getKey()) {

			if (legalVerification.getReason() == null || legalVerification.getReason() < 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Reason";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			} else {
				if (legalVerification.getStatus() == FIStatus.NEGATIVE.getKey()) {

					reasonCode = getReasonCode(legalVerification.getReason(), StatuReasons.LVSRES.getKey());
				} else {
					reasonCode = getReasonCode(legalVerification.getReason(), StatuReasons.LVSRES.getKey());
				}
				if (reasonCode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reason";
					returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
					return returnStatus;

				} else {
					legalVerification.setReasonDesc(reasonCode.getReasonTypeDesc());
				}
			}
		}
		// get Verification Id's from Verifications Table with keyreference
		List<Long> verificationIds = verificationDAO.getVerificationIds(keyReference, VerificationType.LV.getKey(),
				RequestType.INITIATE.getKey());
		if (CollectionUtils.isEmpty(verificationIds)) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id";
			valueParam[2] = "Not";
			valueParam[3] = "Exists";
			returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			return returnStatus;
		}

		if (!verificationIds.contains(verificationId)) {
			String[] valueParam = new String[2];
			valueParam[0] = "VerificationId: " + String.valueOf(verificationId);
			returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParam);
			return returnStatus;
		}

		boolean initiatedVerfication = verificationDAO.isInitiatedVerfication(VerificationType.LV, verificationId,
				"_Temp");
		if (!initiatedVerfication) {
			String[] valueParam = new String[4];
			valueParam[0] = "Verification";
			valueParam[1] = "Id: " + verificationId;
			valueParam[2] = "Already";
			valueParam[3] = "Processed";
			returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			return returnStatus;
		}

		List<LVDocument> lvDocuments = legalVerification.getLvDocuments();
		if (CollectionUtils.isEmpty(lvDocuments)) {
			String valueParam[] = new String[2];
			valueParam[0] = "RcuDocuments";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParam);
			return returnStatus;
		}

		List<LVDocument> lvDocumentsDb = legalVerificationDAO.getLVDocuments(verificationId, "_View");
		int lv = lvDocuments.size();
		int lvDb = lvDocumentsDb.size();

		if (lv != lvDb) {
			String[] valueParam = new String[4];
			valueParam[0] = "RCU Document";
			valueParam[1] = "Details";
			valueParam[2] = "Are Invalid";
			valueParam[3] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			return returnStatus;
		}
		List<Integer> SeqNo = lvDocuments.stream().map(s -> s.getSeqNo()).collect(Collectors.toList());
		List<Integer> SeqNoDb = lvDocumentsDb.stream().map(s -> s.getSeqNo()).collect(Collectors.toList());

		boolean equals = SeqNo.containsAll(SeqNoDb);

		if (!equals) {
			String[] valueParam = new String[4];
			valueParam[0] = "SeqNo";
			valueParam[1] = "";
			valueParam[2] = "is Invalid";
			valueParam[3] = "";
			returnStatus = APIErrorHandlerService.getFailedStatus("21005", valueParam);
			return returnStatus;
		}
		for (LVDocument lvDocument : lvDocuments) {

			int seqNo = lvDocument.getSeqNo();
			if (seqNo <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = "Seqno";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
			String documentSubId = lvDocument.getDocumentSubId();
			if (StringUtils.isBlank(documentSubId)) {
				String valueParm[] = new String[2];
				valueParm[0] = "documentSubId";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}

			int documentType = lvDocument.getDocumentType();
			if (documentType <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = "documentType";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}
		}

		// Extended Fields Validations
		List<ExtendedField> extendedDetails = legalVerification.getExtendedDetails();
		if (CollectionUtils.isNotEmpty(legalVerification.getExtendedDetails())) {
			String collateralType = legalVerification.getCollateralType();
			List<ErrorDetail> errorDetails = validateLVExtendedFileds(extendedDetails, collateralType);
			if (!errorDetails.isEmpty()) {
				String[] valueParm = new String[2];
				valueParm[0] = "extended fields";
				return returnStatus = APIErrorHandlerService.getFailedStatus("90265", valueParm);

			}
		}
		return null;
	}

	@Autowired
	public void setVehicleDealerDAO(VehicleDealerDAO vehicleDealerDAO) {
		this.vehicleDealerDAO = vehicleDealerDAO;
	}

	@Autowired
	public void setVerificationController(VerificationController verificationController) {
		this.verificationController = verificationController;
	}

	@Autowired
	public void setReasonCodeDAO(ReasonCodeDAO reasonCodeDAO) {
		this.reasonCodeDAO = reasonCodeDAO;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	@Autowired
	public void setVerificationDAO(VerificationDAO verificationDAO) {
		this.verificationDAO = verificationDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	@Autowired
	public void setCollateralStructureDAO(CollateralStructureDAO collateralStructureDAO) {
		this.collateralStructureDAO = collateralStructureDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
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
	public void setVerificationService(VerificationService verificationService) {
		this.verificationService = verificationService;
	}
}
