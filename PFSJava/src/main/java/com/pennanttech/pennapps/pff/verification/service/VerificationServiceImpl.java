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
 * FileName    		:  VerificationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-03-2018    														*
 *                                                                  						*
 * Modified Date    :  24-03-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-03-2018       PENNANT	                 0.1                                            * 
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
package com.pennanttech.pennapps.pff.verification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.Module;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Verification</b>.<br>
 */
public class VerificationServiceImpl extends GenericService<Verification> implements VerificationService {
	private static final Logger logger = LogManager.getLogger(VerificationServiceImpl.class);

	@Autowired
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired
	private VerificationDAO verificationDAO;
	@Autowired
	private FieldInvestigationService fieldInvestigationService;
	@Autowired
	private TechnicalVerificationService technicalVerificationService;
	@Autowired
	private LegalVerificationService legalVerificationService;
	@Autowired
	private RiskContainmentUnitService riskContainmentUnitService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;

	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, VerificationType verificationType,
			String tableType, String auditTranType, boolean isInitTab) {
		logger.debug(Literal.ENTERING);

		List<Long> idList = null;
		List<CustomerDetails> customerDetailsList = null;
		List<CollateralSetup> collateralSetupList = null;
		Verification verification = null;

		if (verificationType == VerificationType.FI) {
			verification = financeDetail.getFiVerification();
			customerDetailsList = verification.getCustomerDetailsList();
			idList = fieldInvestigationService.getFieldInvestigationIds(verification.getVerifications(),
					verification.getKeyReference());
		} else if (verificationType == VerificationType.TV) {
			verification = financeDetail.getTvVerification();
			collateralSetupList = verification.getCollateralSetupList();
			idList = technicalVerificationService.getTechnicalVerificaationIds(verification.getVerifications(),
					verification.getKeyReference());
		} else if (verificationType == VerificationType.LV) {
			verification = financeDetail.getLvVerification();
			idList = legalVerificationService.getLegalVerficationIds(verification.getVerifications(),
					verification.getKeyReference());
		} else if (verificationType == VerificationType.RCU) {
			verification = financeDetail.getRcuVerification();
			idList = riskContainmentUnitService.getRCUVerificaationIds(verification.getVerifications(),
					verification.getKeyReference());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(verification, verification.getExcludeFields());

		List<AuditDetail> auditDetails = new ArrayList<>();

		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(verification.getWorkflowId()).getWorkFlowXml());
		int i = 0;

		for (Verification item : verification.getVerifications()) {
			if (item.isIgnoreFlag()) {
				continue;
			}

			//set WorkFlow Data to item; 
			setVerificationWorkflowData(verification, item);

			if (isInitTab) {
				//delete non verification Records
				if (item.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
						&& verificationType == VerificationType.TV) {//FIX ME
					verificationDAO.delete(item, TableType.BOTH_TAB);
					continue;
				}

				// clear Re-init for non initiated records
				if (item.getRequestType() != RequestType.INITIATE.getKey()) {
					item.setReinitid(null);
				}
				if (item.isNew()) {
					setVerificationData(financeDetail, item, verificationType);
					verificationDAO.save(item, TableType.MAIN_TAB);
				} else {
					verificationDAO.update(item, TableType.MAIN_TAB);
				}

				if (engine.compareTo(verification.getTaskId(),
						verification.getNextTaskId().replace(";", "")) == Flow.SUCCESSOR) {
					if (!idList.contains(item.getId())) {
						if (verificationType == VerificationType.FI) {
							saveFI(customerDetailsList, item);
						} else if (verificationType == VerificationType.TV) {
							saveTV(item);
						} else if (verificationType == VerificationType.LV) {
							saveLV(financeDetail, item);
						} else if (verificationType == VerificationType.RCU) {
							saveRCU(financeDetail, item);
						}
					}
				} else if (verificationType == VerificationType.RCU) {
					saveRCUInStage(financeDetail, item);
				}

			} else {
				if (item.getDecision() == Decision.RE_INITIATE.getKey()) {
					if (verificationType == VerificationType.LV) {
						savereInitLegalVerification(financeDetail, item);
					} else {
						reInitVerification(financeDetail, verificationType, customerDetailsList, collateralSetupList,
								item);
					}
				} else {
					if (item.getId() != 0) {
						verificationDAO.update(item, TableType.MAIN_TAB);

						if (verificationType == VerificationType.RCU) {
							riskContainmentUnitService.updateRemarks(item);
						}

					}
				}
			}
			if (item.getId() != 0) {
				auditDetails.add(new AuditDetail(auditTranType, ++i, fields[0], fields[1], item.getBefImage(), item));
			}
		}
		return auditDetails;
	}

	private void setVerificationWorkflowData(Verification verification, Verification item) {
		item.setLastMntOn(verification.getLastMntOn());
		item.setLastMntBy(verification.getLastMntBy());
		item.setVersion(verification.getVersion());
		item.setRoleCode(verification.getRoleCode());
		item.setNextRoleCode(verification.getNextRoleCode());
		item.setTaskId(verification.getTaskId());
		item.setNextTaskId(verification.getNextTaskId());
		item.setRecordStatus(verification.getRecordStatus());
		item.setWorkflowId(verification.getWorkflowId());
		item.setCreatedBy(verification.getLastMntBy());
		if (StringUtils.isEmpty(item.getRecordType())) {
			item.setRecordType(verification.getRecordType());
		}
		if (StringUtils.isEmpty(item.getKeyReference())) {
			item.setKeyReference(verification.getKeyReference());
		}
	}

	private void reInitVerification(FinanceDetail financeDetail, VerificationType verificationType,
			List<CustomerDetails> customerDetailsList, List<CollateralSetup> collateralSetupList, Verification item) {
		Verification reInit = new Verification();
		reInit.setId(item.getId());
		reInit.setLastMntOn(item.getLastMntOn());
		reInit.setLastMntBy(item.getLastMntBy());
		reInit.setDecision(Decision.RE_INITIATE.getKey());

		if (verificationType != VerificationType.LV) {//FIXME
			item.setAgency(item.getReInitAgency());
			item.setRemarks(item.getDecisionRemarks());
		}
		if (verificationType == VerificationType.RCU) {
			item.getRcuDocument().setInitRemarks(item.getDecisionRemarks());
		}
		item.setStatus(0);
		item.setCreatedBy(item.getLastMntBy());
		item.setVerificationDate(null);
		item.setDecisionRemarks("");
		item.setDecision(Decision.SELECT.getKey());
		item.setRequestType(RequestType.INITIATE.getKey());
		item.setReason(null);
		setVerificationData(financeDetail, item, verificationType);

		verificationDAO.save(item, TableType.MAIN_TAB);

		reInit.setReinitid(item.getId());
		verificationDAO.updateReInit(reInit, TableType.MAIN_TAB);

		if (verificationType == VerificationType.FI) {
			saveFI(customerDetailsList, item);
		} else if (verificationType == VerificationType.TV) {
			saveTV(item);
		} else if (verificationType == VerificationType.LV) {
			saveLV(financeDetail, item);
		} else if (verificationType == VerificationType.RCU) {
			if (item.getReinitid() == null) {
				saveRCU(financeDetail, item);
			}
		}
	}

	@Override
	public void saveLegalVerification(Verification verification) {
		if (verification.getRequestType() == RequestType.WAIVE.getKey()) {
			saveLVWaive(verification);
		} else {
			saveLVInit(verification);
		}
	}

	@Override
	public void savereInitLegalVerification(FinanceDetail financeDetail, Verification verification) {
		reInitVerification(financeDetail, VerificationType.LV, null, null, verification);
		saveLVInit(verification);

	}

	private void saveLVWaive(Verification verification) {
		Verification item = null;
		for (LVDocument lvDocument : verification.getLvDocuments()) {
			item = new Verification();
			BeanUtils.copyProperties(verification, item);
			item.setReferenceType(lvDocument.getDocumentSubId());

			Long verificationId = getVerificationIdByReferenceFor(item.getKeyReference(), item.getReferenceType(),
					VerificationType.LV.getKey());

			if (verificationId != null) {
				item.setId(verificationId);
				verificationDAO.update(item, TableType.MAIN_TAB);
			} else {
				verificationDAO.save(item, TableType.MAIN_TAB);
			}
		}
	}

	private void saveLVInit(Verification verification) {
		if (!verification.isApproveTab()) {
			Long verificationId = verificationDAO.getVerificationIdByReferenceFor(verification.getKeyReference(),
					verification.getReferenceFor(), VerificationType.LV.getKey());

			if (verification.isNewRecord() && verificationId != null) {
				throw new AppException(
						String.format("Collateral %s already initiated", verification.getReferenceFor()));
			}

			if (verificationId != null) {
				verification.setId(verificationId);
				verificationDAO.update(verification, TableType.MAIN_TAB);
			} else {
				verificationDAO.save(verification, TableType.MAIN_TAB);
			}
		}
		// delete documents
		legalVerificationService.deleteDocuments(verification.getId(), TableType.MAIN_TAB);

		// Legal verification
		legalVerificationService.save(verification, TableType.MAIN_TAB);

		// LV Documents
		for (LVDocument document : verification.getLvDocuments()) {
			document.setVerificationId(verification.getLegalVerification().getVerificationId());
		}

		legalVerificationService.saveDocuments(verification.getLvDocuments(), TableType.MAIN_TAB);
	}

	private void saveFI(List<CustomerDetails> customerDetailsList, Verification item) {
		if (item.getFieldInvestigation() == null) {
			for (CustomerDetails customerDetails : customerDetailsList) {
				fieldInvestigationService.save(customerDetails, customerDetails.getCustomerPhoneNumList(), item);
			}
		} else if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			FieldInvestigation fi = item.getFieldInvestigation();
			fi.setVerificationId(item.getId());
			fi.setLastMntOn(item.getLastMntOn());
			fi.setAgentCode("");
			fi.setAgencyName("");
			fi.setDate(null);
			fi.setReason(0L);
			fi.setSummaryRemarks("");
			fi.setStatus(0);
			fieldInvestigationService.save(fi, TableType.TEMP_TAB);
		}
	}

	private void saveTV(Verification item) {
		if (item.getTechnicalVerification() == null) {
			technicalVerificationService.save(item);
		} else if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			TechnicalVerification technicalVerification = new TechnicalVerification();

			technicalVerification.setVerificationId(item.getId());
			technicalVerification.setLastMntOn(item.getLastMntOn());
			technicalVerification.setCollateralRef(item.getTechnicalVerification().getCollateralRef());
			technicalVerification.setCollateralType(item.getTechnicalVerification().getCollateralType());

			technicalVerificationService.save(technicalVerification, TableType.TEMP_TAB);
		}
	}

	private void saveLV(FinanceDetail financeDetail, Verification item) {
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			legalVerificationService.save(item, TableType.TEMP_TAB);
			setLVDocumentDetails(financeDetail, item);
			for (LVDocument lvDocument : item.getLvDocuments()) {
				lvDocument.setVerificationId(item.getId());
			}
			legalVerificationService.saveDocuments(item.getLvDocuments(), TableType.TEMP_TAB);
		}
	}

	private void saveRCU(FinanceDetail financeDetail, Verification item) {
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			saveRCUInStage(financeDetail, item);

			riskContainmentUnitService.save(item, TableType.TEMP_TAB);
			riskContainmentUnitService.saveDocuments(item.getRcuDocuments(), TableType.TEMP_TAB);
		}
	}

	private void saveRCUInStage(FinanceDetail financeDetail, Verification item) {
		setRCUDocumentDetails(financeDetail, item);

		// delete documents
		riskContainmentUnitService.deleteDocuments(item.getId(), TableType.MAIN_TAB);

		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			// RCU Verification
			riskContainmentUnitService.save(item, TableType.STAGE_TAB);
			// Rcu Documents
			riskContainmentUnitService.saveDocuments(item.getRcuDocuments(), TableType.STAGE_TAB);
		}
	}

	private void setLVDocumentDetails(FinanceDetail financeDetail, Verification item) {
		List<CustomerDocument> customerDocuemnts = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> loanDocuments = financeDetail.getDocumentDetailsList();
		List<DocumentDetails> collateralDocumentList = new ArrayList<>();

		List<DocumentDetails> list = null;
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			list = documentDetailsDAO.getDocumentDetailsByRef(item.getReferenceFor(), CollateralConstants.MODULE_NAME,
					"", "_View");
		}

		if (list != null) {
			collateralDocumentList.addAll(list);
		}

		// Set customer documents id's
		Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
		for (CustomerDocument document : customerDocuemnts) {
			customerDoumentMap.put(document.getCustDocCategory(), document);
		}

		for (LVDocument lvDocument : item.getLvDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.CUSTOMER.getKey()) {
				CustomerDocument document = customerDoumentMap.get(lvDocument.getDocumentSubId());
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getCustDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());
			}
		}

		// Set loan documents id's
		Map<String, DocumentDetails> loanDocumentMap = new HashMap<>();
		for (DocumentDetails document : loanDocuments) {
			loanDocumentMap.put(document.getDocCategory(), document);
		}

		for (LVDocument lvDocument : item.getLvDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.LOAN.getKey()) {
				DocumentDetails document = loanDocumentMap.get(lvDocument.getDocumentSubId());
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());
			}
		}

		// Set collateral documents id's
		Map<Long, DocumentDetails> collateralDocumentMap = new HashMap<>();
		for (DocumentDetails document : collateralDocumentList) {
			collateralDocumentMap.put(document.getDocId(), document);
		}

		for (LVDocument lvDocument : item.getLvDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				DocumentDetails document = collateralDocumentMap.get(lvDocument.getDocumentId());
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());
			}
		}
	}

	private void setRCUDocumentDetails(FinanceDetail financeDetail, Verification item) {
		List<CustomerDocument> customerDocuemnts = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> loanDocuments = financeDetail.getDocumentDetailsList();
		List<DocumentDetails> collateralDocumentList = new ArrayList<>();

		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		if (collaterals != null) {
			for (CollateralAssignment collateral : collaterals) {
				List<DocumentDetails> list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
						CollateralConstants.MODULE_NAME, "", "_View");

				if (list != null) {
					collateralDocumentList.addAll(list);
				}
			}
		}

		// Set customer documents id's
		Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
		for (CustomerDocument document : customerDocuemnts) {
			customerDoumentMap.put(document.getCustDocCategory(), document);
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			if (rcuDocument.getDocumentType() == DocumentType.CUSTOMER.getKey()) {
				CustomerDocument document = customerDoumentMap.get(rcuDocument.getDocCategory());
				rcuDocument.setDocumentId(document.getCustID());
				rcuDocument.setDocumentSubId(document.getCustDocCategory());
				rcuDocument.setDocumentRefId(document.getDocRefId());
				rcuDocument.setDocumentUri(document.getDocUri());
			}
		}

		// Set loan documents id's
		Map<String, DocumentDetails> loanDocumentMap = new HashMap<>();
		for (DocumentDetails document : loanDocuments) {
			loanDocumentMap.put(document.getDocCategory(), document);
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			if (rcuDocument.getDocumentType() == DocumentType.LOAN.getKey()) {
				DocumentDetails document = loanDocumentMap.get(rcuDocument.getDocCategory());
				rcuDocument.setDocumentId(document.getId());
				rcuDocument.setDocumentSubId(rcuDocument.getDocCategory());
				rcuDocument.setDocumentRefId(document.getDocRefId());
				rcuDocument.setDocumentUri(document.getDocUri());
			}
		}

		// Set collateral documents id's
		Map<String, DocumentDetails> collateralDocumentMap = new HashMap<>();
		for (DocumentDetails document : collateralDocumentList) {
			collateralDocumentMap.put(document.getDocCategory(), document);
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			if (rcuDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				DocumentDetails document = collateralDocumentMap.get(rcuDocument.getDocCategory());
				rcuDocument.setDocumentId(document.getId());
				rcuDocument.setDocumentSubId(rcuDocument.getDocCategory());
				rcuDocument.setDocumentRefId(document.getDocRefId());
				rcuDocument.setDocumentUri(document.getDocUri());
			}
		}
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * verifications by using verificationsDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and Adtverifications by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Verification verification = (Verification) auditHeader.getAuditDetail().getModelData();
		verificationDAO.delete(verification, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getApprovedverificationsById fetch the details by using verificationsDAO's getverificationsById method . with
	 * parameter id and type as blank. it fetches the approved records from the verifications.
	 * 
	 * @param id
	 *            id of the Verification. (String)
	 * @return verifications
	 */
	public Verification getApprovedVerification(long id) {
		return null;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using verificationDAO.delete with parameters
	 * verification,"" b) NEW Add new record in to main table by using verificationDAO.save with parameters
	 * verification,"" c) EDIT Update record in the main table by using verificationDAO.update with parameters
	 * verification,"" 3) Delete the record from the workFlow table by using verificationDAO.delete with parameters
	 * verification,"_Temp" 4) Audit the record in to AuditHeader and Adtverifications by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and Adtverifications by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Verification verification = new Verification();
		BeanUtils.copyProperties((Verification) auditHeader.getAuditDetail().getModelData(), verification);

		verificationDAO.delete(verification, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(verification.getRecordType())) {
		}

		if (verification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			verificationDAO.delete(verification, TableType.MAIN_TAB);
		} else {
			verification.setRoleCode("");
			verification.setNextRoleCode("");
			verification.setTaskId("");
			verification.setNextTaskId("");
			verification.setWorkflowId(0);

			if (verification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				verification.setRecordType("");
				verificationDAO.save(verification, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				verification.setRecordType("");
				verificationDAO.update(verification, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(verification);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using verificationDAO.delete with parameters verification,"_Temp" 3) Audit the record in to
	 * AuditHeader and Adtverifications by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		Verification verification = (Verification) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		verificationDAO.delete(verification, TableType.TEMP_TAB);

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
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from verificationDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
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

	@Override
	public List<Verification> getVerifications(String keyReference, int verificationType) {
		List<Verification> verifications = verificationDAO.getVeriFications(keyReference, verificationType);

		if (VerificationType.LV.getKey() != verificationType) {
			setDecision(verificationType, verifications);
		}

		return verifications;
	}

	@Override
	public void setLastStatus(Verification verification) {
		Verification lastStatus = verificationDAO.getLastStatus(verification);

		if (lastStatus != null && (lastStatus.getVersion() == lastStatus.getLastVersion())) {
			verification.setLastStatus(lastStatus.getStatus());
			verification.setLastVerificationDate(lastStatus.getVerificationDate());
			verification.setVersion(lastStatus.getVersion());
			verification.setLastVersion(lastStatus.getLastVersion());
		}
	}

	@Override
	public void setLVDetails(List<Verification> verifications) {
		LegalVerification lv;
		for (Verification verification : verifications) {
			lv = legalVerificationService.getLVFromStage(verification.getId());

			if (lv != null) {
				lv.setLvDocuments(legalVerificationService.getLVDocumentsFromStage(verification.getId()));
				verification.setLvDocuments(lv.getLvDocuments());
			}

			verification.setLegalVerification(lv);
		}
	}

	private void setDecision(int verificationType, List<Verification> verifications) {

		for (Verification verification : verifications) {
			if ((verification.getStatus() == Status.POSITIVE.getKey()
					|| verification.getRequestType() == RequestType.NOT_REQUIRED.getKey())
					&& verification.getDecision() != Decision.RE_INITIATE.getKey()) {
				verification.setDecision(Decision.APPROVE.getKey());
			}
		}

	}

	@Override
	public Verification getVerificationById(long id) {
		Verification verification = verificationDAO.getVerificationById(id);
		if (verification != null) {
			verification.setLvDocuments(legalVerificationService.getLVDocumentsFromStage(id));
		}
		return verification;

	}

	@Override
	public Long getVerificationIdByReferenceFor(String finReference, String referenceFor, int verificationType) {
		return verificationDAO.getVerificationIdByReferenceFor(finReference, referenceFor, verificationType);
	}

	private void setVerificationData(FinanceDetail financeDetail, Verification verification,
			VerificationType verificationType) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		verification.setCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		verification.setModule(Module.LOAN.getKey());
		verification.setKeyReference(financeMain.getFinReference());
		verification.setCustId(customer.getCustID());
		verification.setCustomerName(customer.getCustShrtName());
		verification.setCreatedOn(DateUtility.getAppDate());

		if (verificationType == VerificationType.FI) {
			if (verification.getReference() != null) {
				verification.setReference(customer.getCustCIF());
			}
		} else if (verificationType == VerificationType.TV) {

		} else if (verificationType == VerificationType.LV) {
			if (verification.getReference() == null) {
				verification.setReference(customer.getCustCIF());
			}
		} else if (verificationType == VerificationType.TV) {

		} else if (verificationType == VerificationType.RCU) {
			if (verification.getReference() == null) {
				verification.setReference(customer.getCustCIF());
			}
		}

	}

	@Override
	public List<Verification> getCollateralDetails(String[] collaterals) {
		return verificationDAO.getCollateralDetails(collaterals);
	}

	@Override
	public boolean isVerificationInRecording(Verification verification, VerificationType verificationType) {
		boolean exists = false;
		if (verificationType == VerificationType.TV) {
			if (technicalVerificationService.getVerificationinFromRecording(verification.getId()) != null) {
				exists = true;
			}
		}

		return exists;
	}
}