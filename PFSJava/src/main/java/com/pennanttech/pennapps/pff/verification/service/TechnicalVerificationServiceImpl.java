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

package com.pennanttech.pennapps.pff.verification.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.TechnicalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>TechnicalVerification</b>.<br>
 */
public class TechnicalVerificationServiceImpl extends GenericService<TechnicalVerification>
		implements TechnicalVerificationService {
	private static final Logger logger = Logger.getLogger(TechnicalVerificationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private TechnicalVerificationDAO technicalVerificationDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	@Autowired
	private CollateralStructureService collateralStructureService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private DocumentManagerDAO documentManagerDAO;
	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;
	private DocumentDetailValidation documentValidation;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * verification_fi/verification_fi_Temp by using verification_fiDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using verification_fiDAO's update method 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (tv.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (tv.isNew()) {
			tv.setId(Long.parseLong(technicalVerificationDAO.save(tv, tableType)));
			auditHeader.getAuditDetail().setModelData(tv);
			auditHeader.setAuditReference(String.valueOf(tv.getId()));
		} else {
			technicalVerificationDAO.update(tv, tableType);
		}

		// Extended field Details
		if (tv.getExtendedFieldRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_TV");
			details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
					tv.getExtendedFieldHeader(), tableName.toString(), tableType.getSuffix());
			auditDetails.addAll(details);
		}
		
		// FI documents
		if (tv.getDocuments() != null && !tv.getDocuments().isEmpty()) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
			details = saveOrUpdateDocuments(details, tv, tableType.getSuffix());
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetails(auditDetails);				
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * verification_fi by using verification_fiDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();
		auditDetails.addAll(
				deleteChilds(technicalVerification, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));

		technicalVerificationDAO.delete(technicalVerification, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(),
				technicalVerification.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				technicalVerification.getBefImage(), technicalVerification));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	// Method for Deleting all records related to Customer in _Temp/Main tables depend on method type
	public List<AuditDetail> deleteChilds(TechnicalVerification tv, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = tv.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			// Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_TV");
			auditList.addAll(extendedFieldDetailsService.delete(tv.getExtendedFieldHeader(), tv.getCollateralRef(),
					tableName.toString(), tableType, auditTranType, extendedDetails));
		}
		
		// Document Details.
		List<AuditDetail> documentDetails = tv.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			List<DocumentDetails> documents = new ArrayList<>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				document = (DocumentDetails) documentDetails.get(i).getModelData();
				document.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				documents.add(document);
				auditList.add(
						new AuditDetail(auditTranType, i + 1, fields[0], fields[1], document.getBefImage(), document));
			}
			documentDetailsDAO.deleteList(documents, tableType);
		}
		return auditList;
	}

	/**
	 * getverification_fi fetch the details by using verification_fiDAO's getverification_fiById method.
	 * 
	 * @param id
	 *            id of the TechnicalVerification.
	 * @return verification_fi
	 */
	@Override
	public TechnicalVerification getTechnicalVerification(long id) {
		TechnicalVerification technicalVerification = technicalVerificationDAO.getTechnicalVerification(id, "_View");
		if (technicalVerification != null) {
			// FI Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
					VerificationType.TV.getCode(), "", "_View");
			if (technicalVerification.getDocuments() != null && !technicalVerification.getDocuments().isEmpty()) {
				technicalVerification.getDocuments().addAll(documentList);
			} else {
				technicalVerification.setDocuments(documentList);
			}
		}
		return technicalVerification;
	}

	/**
	 * getApprovedverification_fiById fetch the details by using verification_fiDAO's getverification_fiById method .
	 * with parameter id and type as blank. it fetches the approved records from the verification_fi.
	 * 
	 * @param id
	 *            id of the TechnicalVerification. (String)
	 * @return verification_fi
	 */
	public TechnicalVerification getApprovedTechnicalVerification(long id) {
		return technicalVerificationDAO.getTechnicalVerification(id, "__AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using technicalVerificationDAO.delete with
	 * parameters technicalVerification,"" b) NEW Add new record in to main table by using technicalVerificationDAO.save
	 * with parameters technicalVerification,"" c) EDIT Update record in the main table by using
	 * technicalVerificationDAO.update with parameters technicalVerification,"" 3) Delete the record from the workFlow
	 * table by using technicalVerificationDAO.delete with parameters technicalVerification,"_Temp" 4) Audit the record
	 * in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		TechnicalVerification tv = new TechnicalVerification();
		BeanUtils.copyProperties((TechnicalVerification) auditHeader.getAuditDetail().getModelData(), tv);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(tv.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					technicalVerificationDAO.getTechnicalVerification(tv.getId(), TableType.MAIN_TAB.getSuffix()));
		}

		if (tv.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(tv, TableType.MAIN_TAB.getSuffix(), tranType));
			technicalVerificationDAO.delete(tv, TableType.MAIN_TAB);
		} else {
			tv.setRoleCode("");
			tv.setNextRoleCode("");
			tv.setTaskId("");
			tv.setNextTaskId("");
			tv.setWorkflowId(0);

			if (tv.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				tv.setRecordType("");
				technicalVerificationDAO.save(tv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(tv.getId(), tv.getDate(), tv.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				tv.setRecordType("");
				technicalVerificationDAO.update(tv, TableType.MAIN_TAB);
				verificationDAO.updateVerifiaction(tv.getId(), tv.getDate(), tv.getStatus());
			}

			// Extended field Details
			if (tv.getExtendedFieldRender() != null) {
				List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");

				// Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(tv.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_TV");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						tv.getExtendedFieldHeader(), tableName.toString(), TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			// FI Document Details
			List<DocumentDetails> documentsList = tv.getDocuments();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
				details = saveOrUpdateDocuments(details, tv, "");
				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditDetailList.addAll(deleteChilds(tv, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		technicalVerificationDAO.delete(tv, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(), tv.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1], tv.getBefImage(), tv));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using technicalVerificationDAO.delete with parameters technicalVerification,"_Temp" 3) Audit
	 * the record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteChilds(tv, "_Temp", auditHeader.getAuditTranType()));
		technicalVerificationDAO.delete(tv, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = tv.getUserDetails().getLanguage();

		// Extended field details Validation
		if (tv.getExtendedFieldRender() != null) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = tv.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_TV");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
			auditDetails.addAll(details);
		}
		
		// TV Document details Validation
		List<DocumentDetails> docuemnts = tv.getDocuments();
		if (docuemnts != null && !docuemnts.isEmpty()) {
			List<AuditDetail> details = tv.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		TechnicalVerification tv = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (tv.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (tv.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(tv.getExtendedFieldRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}
		
		// FI Document Details
				if (tv.getDocuments() != null && tv.getDocuments().size() > 0) {
					auditDetailMap.put("DocumentDetails",
							setDocumentDetailsAuditData(tv, auditTranType, method));
					auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
				}

		tv.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(tv);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(TechnicalVerification fieldInvestigation, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

		for (int i = 0; i < fieldInvestigation.getDocuments().size(); i++) {
			DocumentDetails documentDetails = fieldInvestigation.getDocuments().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(fieldInvestigation.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (fieldInvestigation.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(fieldInvestigation.getRecordStatus());
			documentDetails.setUserDetails(fieldInvestigation.getUserDetails());
			documentDetails.setLastMntOn(fieldInvestigation.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	private List<Verification> getScreenVerifications(Verification verification) {
		List<Verification> verifications = new ArrayList<>();
		List<String> requiredCodes = collateralStructureService.getCollateralValuatorRequiredCodes();
		for (CollateralSetup collateralSetup : verification.getCollateralSetupList()) {
			Verification vrf = new Verification();
			vrf.setModule(verification.getModule());
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setReferenceType(collateralSetup.getCollateralType());
			vrf.setKeyReference(verification.getKeyReference());
			vrf.setCif(verification.getCif());
			vrf.setCustomerName(verification.getCustomerName());
			vrf.setReferenceFor(collateralSetup.getCollateralRef());
			vrf.setCustId(verification.getCustId());

			if (requiredCodes.contains(collateralSetup.getCollateralType())) {
				vrf.setRequestType(RequestType.INITIATE.getKey());
			} else {
				vrf.setRequestType(RequestType.NOT_REQUIRED.getKey());
			}
			vrf.setNewRecord(true);
			vrf.setReference(vrf.getCif());
			vrf.setRecordType(collateralSetup.getRecordType());
			vrf.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			setTvFields(vrf, collateralSetup);
			verifications.add(vrf);

		}
		return verifications;
	}

	@Override
	public Verification getTvVeriFication(Verification verification) {
		logger.info(Literal.ENTERING);
		List<Verification> preVerifications = verificationDAO.getVeriFications(verification.getKeyReference(),
				VerificationType.TV.getKey());
		List<Verification> screenVerifications = getScreenVerifications(verification);
		setLastStatus(screenVerifications);

		if (!preVerifications.isEmpty()) {
			List<TechnicalVerification> tvList = technicalVerificationDAO.getList(verification.getKeyReference());

			for (Verification pvr : preVerifications) {
				for (TechnicalVerification tv : tvList) {
					if (pvr.getId() == tv.getVerificationId()) {
						pvr.setTechnicalVerification(tv);
					}
				}
			}

		}
		getChangedVerifications(preVerifications, screenVerifications, verification.getKeyReference());
		verification.setVerifications(
				compareVerifications(screenVerifications, preVerifications, verification.getKeyReference()));

		logger.info(Literal.LEAVING);
		return verification;
	}

	private void setLastStatus(List<Verification> verifications) {
		String[] cif = new String[verifications.size()];

		int i = 0;
		for (Verification verification : verifications) {
			cif[i++] = verification.getCif();
		}
		if (cif.length != 0) {
			List<TechnicalVerification> list = technicalVerificationDAO.getList(cif);
			for (Verification verification : verifications) {
				TechnicalVerification current = verification.getTechnicalVerification();
				for (TechnicalVerification previous : list) {
					if (previous.getCustCif().equals(verification.getCif())
							&& previous.getCollateralRef().equals(current.getCollateralRef())) {
						if (!isCollateralChanged(previous, current)) {
							verification.setStatus(previous.getStatus());
							verification.setVerificationDate(new Timestamp(previous.getDate().getTime()));
						}
					}
				}
			}
		}
	}

	@Override
	public void save(TechnicalVerification technicalVerification, TableType tempTab) {
		setAudit(technicalVerification);
		technicalVerificationDAO.save(technicalVerification, tempTab);
		technicalVerificationDAO.saveCollateral(technicalVerification.getCollateralRef(),
				technicalVerification.getCollateralType(), technicalVerification.getVerificationId());
	}

	private void getChangedVerifications(List<Verification> oldList, List<Verification> newList, String keyReference) {
		List<Long> tvIds = getTechnicalVerificaationIds(oldList, keyReference);
		for (Verification oldVer : oldList) {
			for (Verification newVer : newList) {
				if (oldVer.getReferenceFor().equals(newVer.getReferenceFor())) {
					if (oldVer.getRequestType() == RequestType.INITIATE.getKey()
							&& isCollateralChanged(oldVer.getTechnicalVerification(), newVer.getTechnicalVerification())
							&& tvIds.contains(oldVer.getId())) {
						newVer.setRecordType(PennantConstants.RCD_UPD);
					}
				}
			}
		}
	}

	@Override
	public List<Long> getTechnicalVerificaationIds(List<Verification> verifications, String keyRef) {
		List<Long> fiIds = new ArrayList<>();
		List<TechnicalVerification> tvList = technicalVerificationDAO.getList(keyRef);
		for (TechnicalVerification technicalVerification : tvList) {
			for (Verification Verification : verifications) {
				if (technicalVerification.getVerificationId() == Verification.getId()) {
					fiIds.add(Verification.getId());
				}
			}

		}
		return fiIds;
	}

	@Override
	public List<TechnicalVerification> getList(String keyReference) {

		return technicalVerificationDAO.getList(keyReference);
	}

	private List<Verification> compareVerifications(List<Verification> screenVerifications,
			List<Verification> preVerifications, String keyReference) {
		List<Verification> tempList = new ArrayList<>();
		tempList.addAll(screenVerifications);
		tempList.addAll(preVerifications);
		List<Long> tvIds = getTechnicalVerificaationIds(preVerifications, keyReference);

		screenVerifications.addAll(preVerifications);

		for (Verification vrf : tempList) {
			for (Verification preVrf : preVerifications) {
				if (vrf.getReferenceFor().equals(preVrf.getReferenceFor())
						&& (StringUtils.isEmpty(vrf.getRecordType())
								|| !vrf.getRecordType().equals(PennantConstants.RCD_UPD))
						&& !isCollateralChanged(preVrf.getTechnicalVerification(), vrf.getTechnicalVerification())
						&& !tvIds.contains(vrf.getId())) {
					screenVerifications.remove(vrf);
					preVerifications.remove(preVrf);
					break;
				}
			}
		}
		return screenVerifications;
	}

	@Override
	public boolean isCollateralChanged(TechnicalVerification prvVrf, TechnicalVerification currentVrf) {
		if (prvVrf == null || currentVrf == null) {
			return false;
		}

		List<Map<String, Object>> prvCollaterals = extendedFieldRenderDAO.getExtendedFieldMapByVerificationId(
				prvVrf.getVerificationId(), "collateral_" + prvVrf.getCollateralType() + "_ed_tv");
		List<Map<String, Object>> currentCollaterals = extendedFieldRenderDAO.getExtendedFieldMap(
				currentVrf.getCollateralRef(), "collateral_" + currentVrf.getCollateralType() + "_ed", null);
		if (prvCollaterals.size() != currentCollaterals.size()) {
			return true;
		}
		for (Map<String, Object> prv : prvCollaterals) {
			for (Map<String, Object> current : currentCollaterals) {
				if (prv.get("reference").equals(current.get("reference"))
						&& prv.get("seqno").equals(current.get("seqno")) && compareCollaterals(prv, current)) {
					return true;
				}
			}
		}
		return false;
	}

	private Boolean compareCollaterals(Map<String, Object> prvCollateral, Map<String, Object> currentCollateral) {
		for (Map.Entry<String, Object> prvCollateralEntry : prvCollateral.entrySet()) {
			for (Map.Entry<String, Object> currentCollateralEntry : currentCollateral.entrySet()) {
				if (prvCollateralEntry.getKey().equals(currentCollateralEntry.getKey())) {
					if (prvCollateralEntry.getValue().equals(currentCollateralEntry.getValue())) {
						break;
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void setTvFields(Verification verification, CollateralSetup collateralSetup) {
		TechnicalVerification tv = new TechnicalVerification();
		tv.setVerificationId(verification.getId());
		tv.setCollateralRef(collateralSetup.getCollateralRef());
		tv.setCollateralType(collateralSetup.getCollateralType());
		tv.setVersion(1);
		tv.setLastMntBy(verification.getLastMntBy());
		tv.setLastMntOn(verification.getLastMntOn());
		setAudit(tv);
		verification.setTechnicalVerification(tv);
	}

	private void setAudit(TechnicalVerification tv) {
		String workFlowType = ModuleUtil.getWorkflowType("TechnicalVerification");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		tv.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		tv.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		tv.setWorkflowId(workFlowDetails.getWorkflowId());
		tv.setRoleCode(workFlowDetails.getFirstTaskOwner());
		tv.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		tv.setTaskId(engine.getUserTaskId(tv.getRoleCode()));
		tv.setNextTaskId(engine.getUserTaskId(tv.getNextRoleCode()) + ";");
	}

	@Override
	public void save(CollateralSetup collateralSetup, Verification item) {
		if ((item.getRequestType() == RequestType.INITIATE.getKey()
				|| item.getDecision() == Decision.RE_INITIATE.getKey())
				&& item.getReferenceFor().equals(collateralSetup.getCollateralRef())) {
			setTvFields(item, collateralSetup);
			save(item.getTechnicalVerification(), TableType.TEMP_TAB);
		}
	}

	@Override
	public void saveCollateral(String reference, String collateralType, long verificationId) {
		technicalVerificationDAO.saveCollateral(reference, collateralType, verificationId);
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from technicalVerificationDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param tv
	 * @param type
	 * @return
	 */
	private List<AuditDetail> saveOrUpdateDocuments(List<AuditDetail> auditDetails, TechnicalVerification tv,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			boolean isTempRecord = false;
			if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				documentDetails.setRoleCode("");
				documentDetails.setNextRoleCode("");
				documentDetails.setTaskId("");
				documentDetails.setNextTaskId("");
			}
			documentDetails.setLastMntBy(tv.getLastMntBy());
			documentDetails.setWorkflowId(0);

			if (documentDetails.isDocIsCustDoc()) {
				approveRec = true;
			}

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
				isTempRecord = true;
			} else if (documentDetails.isNewRecord()) {
				saveRecord = true;
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (documentDetails.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = documentDetails.getRecordType();
				recordStatus = documentDetails.getRecordStatus();
				documentDetails.setRecordType("");
				documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
					documentDetails.setReferenceId(String.valueOf(tv.getVerificationId()));
				}
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				// Pass the docRefId here to save this in place of docImage column. Or add another column for now to
				// save this.
				documentDetailsDAO.save(documentDetails, type);
			}

			if (updateRecord) {
				// When a document is updated, insert another file into the DocumentManager table's.
				// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
				if (documentDetails.getDocRefId() <= 0) {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setDocImage(documentDetails.getDocImage());
					documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
				}
				documentDetailsDAO.update(documentDetails, type);
			}

			if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
				if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					documentDetailsDAO.delete(documentDetails, type);
				}
			}

			if (approveRec) {
				documentDetails.setFinEvent("");
				documentDetails.setRecordType(rcdType);
				documentDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(documentDetails);

		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	public DocumentDetailValidation getDocumentValidation() {
		if (documentValidation == null) {
			this.documentValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO, customerDocumentDAO);
		}
		return documentValidation;
	}
}