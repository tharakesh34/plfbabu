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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.RequestType;
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
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	@Autowired
	private CollateralSetupDAO collateralSetupDAO;

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

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (technicalVerification.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (technicalVerification.isNew()) {
			technicalVerification
					.setId(Long.parseLong(getTechnicalVerificationDAO().save(technicalVerification, tableType)));
			auditHeader.getAuditDetail().setModelData(technicalVerification);
			auditHeader.setAuditReference(String.valueOf(technicalVerification.getId()));
		} else {
			getTechnicalVerificationDAO().update(technicalVerification, tableType);
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
					technicalVerification.getExtendedFieldHeader(), tableName.toString(), tableType.name());
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
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "delete");
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail()
				.getModelData();
		auditDetails
				.addAll(getListAuditDetails(listDeletion(technicalVerification, "", auditHeader.getAuditTranType())));

		getTechnicalVerificationDAO().delete(technicalVerification, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new TechnicalVerification(),
				technicalVerification.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				technicalVerification.getBefImage(), technicalVerification));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	// Method for Deleting all records related to Customer in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(TechnicalVerification technicalVerification, String tableType,
			String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = technicalVerification.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditList.addAll(extendedFieldDetailsService.delete(technicalVerification.getExtendedFieldHeader(),
					technicalVerification.getCollateralRef(), tableType, auditTranType, extendedDetails));
		}
		return auditList;
	}

	/**
	 * Common Method for Customers list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());
						auditDetailsList.add(
								new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
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
			auditHeader.getAuditDetail()
					.setBefImage(technicalVerificationDAO.getTechnicalVerification(technicalVerification.getId(), ""));
		}

		if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(technicalVerification, "", tranType));
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
						technicalVerification.getExtendedFieldHeader(), tableName.toString(), "");
				auditDetails.addAll(details);
			}
		}
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		auditDetailList.addAll(listDeletion(technicalVerification, "_Temp", auditHeader.getAuditTranType()));
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
		logger.debug("Leaving");
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

	@Override
	public void setTVVerification(FinanceDetail financeDetail, Verification verification) {
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		for (CollateralAssignment CollAsmt : financeDetail.getCollateralAssignmentList()) {
			CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(CollAsmt.getCollateralRef(),
					"_view");
			Verification vrf = new Verification();
			vrf.setModule(verification.getModule());
			vrf.setVerificationType(verification.getVerificationType());
			vrf.setReferenceType(collateralSetup.getCollateralType());
			vrf.setKeyReference(verification.getKeyReference());
			vrf.setCif(verification.getCif());
			vrf.setCustomerName(customer.getCustShrtName());
			vrf.setReferenceFor(CollAsmt.getCollateralRef());
			vrf.setKeyReference(verification.getKeyReference());
			vrf.setCustId(customer.getCustID());
			vrf.setRequestType(RequestType.INITIATE.getKey());
			vrf.setNewRecord(true);
			vrf.setCustId(customer.getCustID());
			vrf.setReference(vrf.getCif());
			vrf.setRecordType(CollAsmt.getRecordType());
			vrf.setCreatedBy(verification.getCreatedBy());
			vrf.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			verification.getVerifications().add(vrf);

		}
		financeDetail.setTvVerification(verification);
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

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the technicalVerificationDAO
	 */
	public TechnicalVerificationDAO getTechnicalVerificationDAO() {
		return technicalVerificationDAO;
	}

	/**
	 * @param technicalVerificationDAO
	 *            the technicalVerificationDAO to set
	 */
	public void setTechnicalVerificationDAO(TechnicalVerificationDAO technicalVerificationDAO) {
		this.technicalVerificationDAO = technicalVerificationDAO;
	}

	public VerificationDAO getVerificationDAO() {
		return verificationDAO;
	}

	public void setVerificationDAO(VerificationDAO verificationDAO) {
		this.verificationDAO = verificationDAO;
	}

}