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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.CollateralStructureService;
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

	private AuditHeaderDAO auditHeaderDAO;
	private TechnicalVerificationDAO technicalVerificationDAO;
	private VerificationDAO verificationDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private CollateralStructureService collateralStructureService;

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
			tv.setId(Long.parseLong(getTechnicalVerificationDAO().save(tv, tableType)));
			auditHeader.getAuditDetail().setModelData(tv);
			auditHeader.setAuditReference(String.valueOf(tv.getId()));
		} else {
			getTechnicalVerificationDAO().update(tv, tableType);
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

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
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
				listDeletion(technicalVerification, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));

		getTechnicalVerificationDAO().delete(technicalVerification, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(),
				technicalVerification.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				technicalVerification.getBefImage(), technicalVerification));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	// Method for Deleting all records related to Customer in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(TechnicalVerification technicalVerification, String tableType,
			String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		// Extended field Render Details.
		List<AuditDetail> extendedDetails = technicalVerification.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			//Table Name
			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(technicalVerification.getExtendedFieldHeader().getSubModuleName());
			tableName.append("_TV");
			auditList.addAll(extendedFieldDetailsService.delete(technicalVerification.getExtendedFieldHeader(),
					technicalVerification.getCollateralRef(), tableName.toString(), tableType, auditTranType,
					extendedDetails));
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
		return getTechnicalVerificationDAO().getTechnicalVerification(id, "_View");
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
		return getTechnicalVerificationDAO().getTechnicalVerification(id, "__AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getTechnicalVerificationDAO().delete
	 * with parameters technicalVerification,"" b) NEW Add new record in to main table by using
	 * getTechnicalVerificationDAO().save with parameters technicalVerification,"" c) EDIT Update record in the main
	 * table by using getTechnicalVerificationDAO().update with parameters technicalVerification,"" 3) Delete the record
	 * from the workFlow table by using getTechnicalVerificationDAO().delete with parameters
	 * technicalVerification,"_Temp" 4) Audit the record in to AuditHeader and Adtverification_fi by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and Adtverification_fi
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		TechnicalVerification technicalVerification = new TechnicalVerification();
		BeanUtils.copyProperties((TechnicalVerification) auditHeader.getAuditDetail().getModelData(),
				technicalVerification);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(technicalVerification.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(technicalVerificationDAO
					.getTechnicalVerification(technicalVerification.getId(), TableType.MAIN_TAB.getSuffix()));
		}

		if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(technicalVerification, TableType.MAIN_TAB.getSuffix(), tranType));
			getTechnicalVerificationDAO().delete(technicalVerification, TableType.MAIN_TAB);
		} else {
			technicalVerification.setRoleCode("");
			technicalVerification.setNextRoleCode("");
			technicalVerification.setTaskId("");
			technicalVerification.setNextTaskId("");
			technicalVerification.setWorkflowId(0);

			if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				technicalVerification.setRecordType("");
				getTechnicalVerificationDAO().save(technicalVerification, TableType.MAIN_TAB);
				getVerificationDAO().updateVerifiaction(technicalVerification.getId(), technicalVerification.getDate(),
						technicalVerification.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				technicalVerification.setRecordType("");
				getTechnicalVerificationDAO().update(technicalVerification, TableType.MAIN_TAB);
				getVerificationDAO().updateVerifiaction(technicalVerification.getId(), technicalVerification.getDate(),
						technicalVerification.getStatus());
			}

			// Extended field Details
			if (technicalVerification.getExtendedFieldRender() != null) {
				List<AuditDetail> details = technicalVerification.getAuditDetailMap().get("ExtendedFieldDetails");

				//Table Name
				StringBuilder tableName = new StringBuilder();
				tableName.append(CollateralConstants.VERIFICATION_MODULE);
				tableName.append("_");
				tableName.append(technicalVerification.getExtendedFieldHeader().getSubModuleName());
				tableName.append("_TV");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						technicalVerification.getExtendedFieldHeader(), tableName.toString(),
						TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditDetailList.addAll(
				listDeletion(technicalVerification, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		getTechnicalVerificationDAO().delete(technicalVerification, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(),
				technicalVerification.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				technicalVerification.getBefImage(), technicalVerification));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getTechnicalVerificationDAO().delete with parameters technicalVerification,"_Temp" 3)
	 * Audit the record in to AuditHeader and Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
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

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getTechnicalVerificationDAO().delete(technicalVerification, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

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

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();
		String usrLanguage = technicalVerification.getUserDetails().getLanguage();

		// Extended field details Validation
		if (technicalVerification.getExtendedFieldRender() != null) {
			List<AuditDetail> details = technicalVerification.getAuditDetailMap().get("ExtendedFieldDetails");
			ExtendedFieldHeader extHeader = technicalVerification.getExtendedFieldHeader();

			StringBuilder tableName = new StringBuilder();
			tableName.append(CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_TV");

			details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, tableName.toString());
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

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (technicalVerification.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Extended Field Details
		if (technicalVerification.getExtendedFieldRender() != null) {
			auditDetailMap.put("ExtendedFieldDetails", extendedFieldDetailsService
					.setExtendedFieldsAuditData(technicalVerification.getExtendedFieldRender(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		technicalVerification.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(technicalVerification);
		auditHeader.setAuditDetails(auditDetails);

		return auditHeader;
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
			vrf.setCreatedBy(verification.getCreatedBy());
			vrf.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			setTvFields(vrf, collateralSetup);
			verifications.add(vrf);

		}
		return verifications;
	}

	@Override
	public Verification getTvVeriFication(Verification verification) {
		logger.info(Literal.ENTERING);
		List<Verification> preVerifications = verificationDAO.getFiVeriFications(verification.getKeyReference(),
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
		//screenVerifications.addAll(getChangedVerifications(preVerifications, screenVerifications,verification.getKeyReference()));
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

		List<TechnicalVerification> list = technicalVerificationDAO.getList(cif);

		for (Verification verification : verifications) {
			TechnicalVerification current = verification.getTechnicalVerification();
			for (TechnicalVerification previous : list) {
				if (previous.getCustCif().equals(verification.getCif())
						&& previous.getCollateralRef().equals(current.getCollateralRef())) {
					//if (!isAddressChange(previous, current)) {
						verification.setStatus(previous.getStatus());
						verification.setVerificationDate(new Timestamp(previous.getDate().getTime()));
					//}
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

	/*
	 * private List<Verification> getChangedVerifications(List<Verification> oldList, List<Verification> newList,String
	 * keyReference) { List<Verification> verifications = new ArrayList<>(); List<Long>
	 * tvIds=getTechnicalVerificaationIds(oldList,keyReference); for (Verification oldVer : oldList) { for (Verification
	 * newVer : newList) { if (oldVer.getCustId().compareTo(newVer.getCustId()) == 0 &&
	 * oldVer.getReferenceFor().equals(newVer.getReferenceFor())) { if (oldVer.getRequestType() ==
	 * RequestType.INITIATE.getKey() && isAddressChange(oldVer.getFieldInvestigation(), newVer.getFieldInvestigation())
	 * && !tvIds.contains(oldVer.getId())) { verifications.add(oldVer); } } } } return verifications; }
	 */

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
				if (vrf.getCustId().compareTo(preVrf.getCustId()) == 0
						&& vrf.getReferenceFor().equals(preVrf.getReferenceFor())
						&& (StringUtils.isEmpty(vrf.getRecordType())
								|| !vrf.getRecordType().equals(PennantConstants.RCD_UPD))
						/* && !isCollateralChanged(preVrf.getTechnicalVerification(), vrf.getTechnicalVerification()) */
						&& !tvIds.contains(vrf.getId())) {
					screenVerifications.remove(vrf);
					preVerifications.remove(preVrf);
					break;
				}
			}
		}

		return screenVerifications;
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
	 * from getTechnicalVerificationDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
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
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public TechnicalVerificationDAO getTechnicalVerificationDAO() {
		return technicalVerificationDAO;
	}

	public void setTechnicalVerificationDAO(TechnicalVerificationDAO technicalVerificationDAO) {
		this.technicalVerificationDAO = technicalVerificationDAO;
	}

	public VerificationDAO getVerificationDAO() {
		return verificationDAO;
	}

	public void setVerificationDAO(VerificationDAO verificationDAO) {
		this.verificationDAO = verificationDAO;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

}