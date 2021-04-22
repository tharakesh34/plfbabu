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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
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
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.DocumentType;
import com.pennanttech.pennapps.pff.verification.Module;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.FieldInvestigationDAO;
import com.pennanttech.pennapps.pff.verification.dao.LegalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.LegalVettingDAO;
import com.pennanttech.pennapps.pff.verification.dao.PersonalDiscussionDAO;
import com.pennanttech.pennapps.pff.verification.dao.RiskContainmentUnitDAO;
import com.pennanttech.pennapps.pff.verification.dao.TechnicalVerificationDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.fi.LVStatus;
import com.pennanttech.pennapps.pff.verification.fi.PDStatus;
import com.pennanttech.pennapps.pff.verification.fi.RCUStatus;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
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
	@Autowired
	private FieldInvestigationDAO fieldInvestigationDAO;
	@Autowired
	private CustomerDetailsService customerDetailsService;
	@Autowired
	private LegalVerificationDAO legalVerificationDAO;
	@Autowired
	private TechnicalVerificationDAO technicalVerificationDAO;
	@Autowired
	private RiskContainmentUnitDAO riskContainmentUnitDAO;
	@Autowired
	private CollateralSetupService collateralSetupService;
	@Autowired
	private transient PersonalDiscussionService personalDiscussionService;
	@Autowired
	private CollateralSetupDAO collateralSetupDAO;
	@Autowired
	private PersonalDiscussionDAO personalDiscussionDAO;
	@Autowired
	private LegalVettingService legalVettingService;
	@Autowired
	private LegalVettingDAO legalVettingDAO;
	@Autowired(required = false)
	@Qualifier("verificationPostExteranalServiceHook")
	private PostExteranalServiceHook postExteranalServiceHook;

	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, VerificationType verificationType,
			String auditTranType, boolean isInitTab) {
		logger.debug(Literal.ENTERING);

		List<Long> idList = null;
		Verification verification = null;
		WorkflowEngine engine = null;
		List<AuditDetail> auditDetails = new ArrayList<>();
		Verification fi = financeDetail.getFiVerification();
		Verification tv = financeDetail.getTvVerification();
		Verification lv = financeDetail.getLvVerification();
		Verification rcu = financeDetail.getRcuVerification();
		Verification pd = financeDetail.getPdVerification();
		Verification ve = financeDetail.getLegalVetting();

		if (verificationType == VerificationType.FI && fi != null) {
			verification = fi;
			idList = fieldInvestigationService.getFieldInvestigationIds(fi.getVerifications(), fi.getKeyReference());
		} else if (verificationType == VerificationType.TV && tv != null) {
			verification = tv;
			idList = technicalVerificationService.getTechnicalVerificaationIds(tv.getVerifications(),
					tv.getKeyReference());
		} else if (verificationType == VerificationType.LV && lv != null) {
			verification = lv;
			idList = legalVerificationService.getLegalVerficationIds(lv.getVerifications(), lv.getKeyReference());
		} else if (verificationType == VerificationType.RCU && rcu != null) {
			verification = rcu;
			idList = riskContainmentUnitService.getRCUVerificaationIds(rcu.getVerifications(), rcu.getKeyReference());
		} else if (verificationType == VerificationType.PD && pd != null) {
			verification = pd;
			idList = personalDiscussionService.getPersonalDiscussionIds(pd.getVerifications(), pd.getKeyReference());
		} else if (verificationType == VerificationType.VETTING && ve != null) {
			verification = ve;
			idList = legalVettingService.getLegalVettingIds(ve.getVerifications(), ve.getKeyReference());
		}
		if (verification == null) {
			return auditDetails;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(verification, verification.getExcludeFields());

		if (verification.getWorkflowId() != 0) {
			engine = new WorkflowEngine(WorkFlowUtil.getWorkflow(verification.getWorkflowId()).getWorkFlowXml());
		}
		int i = 0;

		for (Verification item : verification.getVerifications()) {

			if (item.getId() != 0 && !isInitTab && verificationType == VerificationType.TV) {
				//Update the Collateral valuation amount
				updateCollateralValuationAmount(financeDetail, item);
			}

			if (item.isIgnoreFlag()) {
				continue;
			}

			setVerificationWorkflowData(verification, item);

			if (isInitTab) {
				// delete non verification Records from FI and TV
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, item.getRecordType())
						&& (verificationType == VerificationType.TV || verificationType == VerificationType.VETTING
								|| verificationType == VerificationType.FI
								|| verificationType == VerificationType.PD)) {
					verificationDAO.delete(item, TableType.BOTH_TAB);
					continue;
				}

				// delete non verification Records from RCU
				if (verificationType == VerificationType.RCU) {
					if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, item.getRecordType())) {
						if (item.getRcuDocument() != null) {
							riskContainmentUnitService.deleteRCUDocument(item.getRcuDocument(), "_stage");
						}
						if (riskContainmentUnitService.getRCUDocumentsCount(item.getId()) == 0) {
							riskContainmentUnitService.delete(item.getId(), TableType.STAGE_TAB);
							verificationDAO.delete(item, TableType.BOTH_TAB);
						}
						continue;
					}
				}

				// clear Re-init for non initiated records
				if (item.getRequestType() != RequestType.INITIATE.getKey()) {
					item.setReinitid(null);
				}
				if (item.isNew()) {
					setVerificationData(financeDetail, item, verificationType);
					verificationDAO.save(item, TableType.MAIN_TAB);
				} else {
					if (verificationType == VerificationType.LV) {
						item.setCustId(financeDetail.getCustomerDetails().getCustomer().getCustID());
					}
					verificationDAO.update(item, TableType.MAIN_TAB);
				}

				if (ImplementationConstants.VER_INITATE_DURING_SAVE) {
					if (verification.getWorkflowId() == 0 || (engine.compareTo(verification.getTaskId(),
							verification.getNextTaskId().replace(";", "")) == Flow.SUCCESSOR
							|| engine.compareTo(verification.getTaskId(),
									verification.getNextTaskId().replace(";", "")) == Flow.NONE)) {
						if (!idList.contains(item.getId())) {
							if (verificationType == VerificationType.FI) {
								saveFI(financeDetail, item);
							} else if (verificationType == VerificationType.TV) {
								saveTV(item);
							} else if (verificationType == VerificationType.LV) {
								saveLV(financeDetail, item);
							} else if (verificationType == VerificationType.RCU) {
								saveRCU(financeDetail, item);
							} else if (verificationType == VerificationType.PD) {
								savePD(financeDetail, item);
							} else if (verificationType == VerificationType.VETTING) {
								saveLVetting(financeDetail, item);
							}
						}
					} else if (verificationType == VerificationType.RCU) {
						saveRCUInStage(financeDetail, item);
					}
				} else {
					if (verification.getWorkflowId() == 0 || (engine.compareTo(verification.getTaskId(),
							verification.getNextTaskId().replace(";", "")) == Flow.SUCCESSOR)) {
						if (!idList.contains(item.getId())) {
							if (verificationType == VerificationType.FI) {
								saveFI(financeDetail, item);
							} else if (verificationType == VerificationType.TV) {
								saveTV(item);
							} else if (verificationType == VerificationType.LV) {
								saveLV(financeDetail, item);
							} else if (verificationType == VerificationType.RCU) {
								saveRCU(financeDetail, item);
							} else if (verificationType == VerificationType.PD) {
								savePD(financeDetail, item);
							} else if (verificationType == VerificationType.VETTING) {
								saveLVetting(financeDetail, item);
							}
						}
					} else if (verificationType == VerificationType.RCU) {
						saveRCUInStage(financeDetail, item);
					}
				}
			} else {
				if (item.getDecision() == Decision.RE_INITIATE.getKey()) {
					if (verificationType == VerificationType.LV) {
						savereInitLegalVerification(financeDetail, item);
					} else if (verificationType == VerificationType.VETTING) {
						savereInitLegalVetting(financeDetail, item);
					} else {
						reInitVerification(financeDetail, verificationType, item);
					}
				} else {
					if (item.getId() != 0) {
						verificationDAO.update(item, TableType.MAIN_TAB);

						if (verificationType == VerificationType.RCU) {
							riskContainmentUnitService.updateRemarks(item);
						}

						if (verificationType == VerificationType.TV) {
							technicalVerificationDAO.updateValuationAmount(item, TableType.MAIN_TAB);
						}

					}
				}
			}
			if (item.getId() != 0) {
				auditDetails.add(new AuditDetail(auditTranType, ++i, fields[0], fields[1], item.getBefImage(), item));
			}
		}

		//calling post hook for data sync to external system
		if (postExteranalServiceHook != null) {
			AuditHeader aAuditHeader = getAuditHeader(financeDetail, auditTranType);
			postExteranalServiceHook.doProcess(aAuditHeader, "saveOrUpdate");
		}
		return auditDetails;
	}

	/**
	 * Updating the Collateral valuation amount.
	 * 
	 * @param financeDetail
	 * @param item
	 */
	private void updateCollateralValuationAmount(FinanceDetail financeDetail, Verification verification) {
		logger.debug(Literal.ENTERING);

		if (!ImplementationConstants.ALLOW_COLLATERAL_VALUE_UPDATION) {
			return;
		}

		//Process only if forward direction.
		String recordStatus = financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (!PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus)
				&& !PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)) {

			BigDecimal collateralValue = verification.getFinalValAmt();
			String collateralRef = verification.getReferenceFor();

			//Fetching the collateral setup
			CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupDetails(collateralRef, "_View");
			if (collateralSetup == null) {
				logger.info("Collateral setup not available for the collateral reference :" + collateralRef);
				return;
			}

			//Collateral value
			collateralSetup.setBankValuation(collateralValue);

			/*
			 * //Bank valuation calculation BigDecimal bankValuation =
			 * collateralValue.multiply(collateralSetup.getBankLTV()) .divide(new BigDecimal(100), 0,
			 * RoundingMode.HALF_DOWN); collateralSetup.setBankValuation(bankValuation);
			 */

			//Updating the Collateral value and bank valuation.
			collateralSetupDAO.updateCollateralSetup(collateralSetup, "_Temp");
			collateralSetupDAO.updateCollateralSetup(collateralSetup, "");

			/*
			 * //Updating the Extended field details values List<ExtendedFieldRender> extendedFieldRenders =
			 * collateralSetup.getExtendedFieldRenderList(); if (CollectionUtils.isNotEmpty(extendedFieldRenders)) { for
			 * (ExtendedFieldRender extendedFieldRender : extendedFieldRenders) {
			 * 
			 * Map<String, Object> mapValues = extendedFieldRender.getMapValues();
			 * 
			 * //Updating the Unit price value as collateralValue if (mapValues.containsKey("UNITPRICE")) {
			 * mapValues.put("UNITPRICE", collateralValue); }
			 * 
			 * //Updating the No of units as 1. if (mapValues.containsKey("NOOFUNITS")) { mapValues.put("NOOFUNITS", 1);
			 * }
			 * 
			 * // Add Common Fields mapValues.put("Reference", extendedFieldRender.getReference());
			 * mapValues.put("SeqNo", extendedFieldRender.getSeqNo()); mapValues.put("Version",
			 * extendedFieldRender.getVersion()); mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
			 * mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy()); mapValues.put("RecordStatus",
			 * extendedFieldRender.getRecordStatus()); mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
			 * mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode()); mapValues.put("TaskId",
			 * extendedFieldRender.getTaskId()); mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
			 * mapValues.put("RecordType", extendedFieldRender.getRecordType()); mapValues.put("WorkflowId",
			 * extendedFieldRender.getWorkflowId());
			 * 
			 * StringBuilder sb = new StringBuilder(); sb.append(CollateralConstants.MODULE_NAME); sb.append("_");
			 * sb.append(collateralSetup.getCollateralType()); sb.append("_ED");
			 * 
			 * //Update Extended fields data extendedFieldRenderDAO.update(extendedFieldRender.getReference(),
			 * extendedFieldRender.getSeqNo(), extendedFieldRender.getMapValues(), "_Temp", sb.toString());
			 * 
			 * extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
			 * extendedFieldRender.getMapValues(), "", sb.toString()); } }
			 */
		}

		logger.debug(Literal.LEAVING);
	}

	private void setVerificationWorkflowData(Verification verification, Verification item) {
		if (item.getLastMntOn() == null) {
			item.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		} else if (verification.getLastMntOn() != null) {
			item.setLastMntOn(verification.getLastMntOn());
		}
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

	private void reInitVerification(FinanceDetail financeDetail, VerificationType verificationType, Verification item) {
		Verification reInit = new Verification();
		RCUDocument document;
		int requestType = item.getRequestType();

		reInit.setId(item.getId());
		reInit.setLastMntOn(item.getLastMntOn());
		reInit.setLastMntBy(item.getLastMntBy());
		reInit.setDecision(Decision.RE_INITIATE.getKey());

		if (verificationType != VerificationType.LV && verificationType != VerificationType.VETTING) {
			item.setAgency(item.getReInitAgency());
			item.setRemarks(item.getDecisionRemarks());
		}
		item.setStatus(0);
		item.setCreatedBy(item.getLastMntBy());
		item.setVerificationDate(null);
		item.setDecisionRemarks("");
		item.setDecision(Decision.SELECT.getKey());

		item.setRequestType(RequestType.INITIATE.getKey());
		item.setReason(null);
		if (verificationType != VerificationType.FI) {
			setVerificationData(financeDetail, item, verificationType);
		}

		if (verificationType != VerificationType.PD) {
			setVerificationData(financeDetail, item, verificationType);
		}

		verificationDAO.save(item, TableType.MAIN_TAB);
		reInit.setReinitid(item.getId());

		if (verificationType == VerificationType.RCU) {
			if (requestType != RequestType.INITIATE.getKey()) {
				reInit.setReinitid(item.getId());
			} else {
				reInit.setReinitid(null);
			}
		}

		if (verificationType == VerificationType.RCU) {
			for (RCUDocument rcuDocument : item.getRcuDocuments()) {
				document = new RCUDocument();
				BeanUtils.copyProperties(rcuDocument, document);
				document.setReinitid(item.getId());
				reInit.getRcuDocuments().add(document);
			}
			riskContainmentUnitService.updateRCUDocuments(reInit);
		}

		verificationDAO.updateReInit(reInit, TableType.MAIN_TAB);

		if (verificationType == VerificationType.FI) {
			saveFI(financeDetail, item);
		} else if (verificationType == VerificationType.TV) {
			saveTV(item);
		} else if (verificationType == VerificationType.LV) {
			saveLV(financeDetail, item);
		} else if (verificationType == VerificationType.RCU) {
			if (item.getReinitid() == null) {
				saveRCU(financeDetail, item);
			}
		} else if (verificationType == VerificationType.PD) {
			savePD(financeDetail, item);
		} else if (verificationType == VerificationType.VETTING) {
			saveLVetting(financeDetail, item);
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
		reInitVerification(financeDetail, VerificationType.LV, verification);
		saveLVInit(verification);

	}

	private void saveLVWaive(Verification verification) {
		Verification item = null;
		List<LVDocument> newLVDocs;
		for (LVDocument lvDocument : verification.getLvDocuments()) {
			item = new Verification();
			newLVDocs = new ArrayList<>();
			BeanUtils.copyProperties(verification, item);
			item.setReferenceType(lvDocument.getDocumentSubId());

			Long verificationId = verificationDAO.getVerificationIdByReferenceFor(item.getKeyReference(),
					item.getReferenceType(), VerificationType.LV.getKey(), verification.getVerificationCategory());

			if (verificationId != null) {
				item.setId(verificationId);
				verificationDAO.update(item, TableType.MAIN_TAB);
			} else {
				verificationDAO.save(item, TableType.MAIN_TAB);
			}

			lvDocument.setVerificationId(item.getId());
			newLVDocs.add(lvDocument);
			item.setLvDocuments(newLVDocs);

			// Legal verification
			legalVerificationService.save(item, TableType.MAIN_TAB);

			// LV Documents
			legalVerificationService.saveDocuments(item.getLvDocuments(), TableType.STAGE_TAB);
		}
	}

	private void saveLVInit(Verification verification) {
		Long verificationId;
		if (!verification.isApproveTab()) {
			verificationId = verificationDAO.getVerificationIdByReferenceFor(verification.getKeyReference(),
					verification.getReferenceFor(), VerificationType.LV.getKey(),
					verification.getVerificationCategory());
			Verification vrf = new Verification();
			if (verification.getId() != 0 || verificationId != null) {
				if (verification.getId() != 0) {
					vrf.setId(verification.getId());
				} else {
					vrf.setId(verificationId);
				}
				if (!isVerificationInRecording(vrf, VerificationType.LV) && verification.isNewRecord()) {
					throw new AppException(
							String.format("Collateral %s already initiated", verification.getReferenceFor()));
				}
			}

			if (verification.getId() != 0) {
				verification.setId(verification.getId());
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

		legalVerificationService.saveDocuments(verification.getLvDocuments(), TableType.STAGE_TAB);
	}

	private void saveFI(FinanceDetail financeDetail, Verification item) {
		List<CustomerDetails> customerDetails = new ArrayList<>();
		customerDetails.add(financeDetail.getCustomerDetails());

		List<JointAccountDetail> jointAccounts = financeDetail.getJountAccountDetailList();

		if (jointAccounts == null) {
			jointAccounts = new ArrayList<>();
		}

		for (JointAccountDetail jointAccount : jointAccounts) {
			customerDetails.add(customerDetailsService.getApprovedCustomerById(jointAccount.getCustID()));
		}

		for (CustomerDetails custDetails : customerDetails) {
			Customer customer = custDetails.getCustomer();

			if (StringUtils.equals(customer.getCustCIF(), item.getReference())) {
				item.setCustId(customer.getCustID());

				if (item.getFieldInvestigation() == null) {
					FieldInvestigation fi = fieldInvestigationDAO.getFieldInvestigation(item.getId(), "_view");

					if (fi == null) {
						fieldInvestigationService.save(custDetails, custDetails.getCustomerPhoneNumList(), item);
					}

				} else if (item.getRequestType() == RequestType.INITIATE.getKey()) {
					FieldInvestigation fi = item.getFieldInvestigation();
					fi.setVerificationId(item.getId());
					fi.setLastMntOn(item.getLastMntOn());
					fi.setAgentCode("");
					fi.setAgencyName("");
					fi.setVerifiedDate(null);
					fi.setReason(0L);
					fi.setSummaryRemarks("");
					fi.setStatus(0);
					fieldInvestigationService.save(fi, TableType.TEMP_TAB);
				}
			}
		}
	}

	private void savePD(FinanceDetail financeDetail, Verification item) {
		List<CustomerDetails> customerDetails = new ArrayList<>();
		customerDetails.add(financeDetail.getCustomerDetails());

		if (CollectionUtils.isNotEmpty(financeDetail.getJountAccountDetailList())) {
			for (JointAccountDetail jointAccountDetail : financeDetail.getJountAccountDetailList()) {
				customerDetails.add(customerDetailsService.getApprovedCustomerById(jointAccountDetail.getCustID()));
			}
		}

		for (CustomerDetails custDetails : customerDetails) {
			Customer customer = custDetails.getCustomer();

			if (StringUtils.equals(customer.getCustCIF(), item.getReference())) {
				item.setCustId(customer.getCustID());

				if (item.getPersonalDiscussion() == null) {
					PersonalDiscussion pd = personalDiscussionDAO.getPersonalDiscussion(item.getId(), "_view");

					if (pd == null) {
						personalDiscussionService.save(custDetails, custDetails.getCustomerPhoneNumList(), item);
					}

				} else if (item.getRequestType() == RequestType.INITIATE.getKey()) {
					PersonalDiscussion pd = item.getPersonalDiscussion();
					pd.setVerificationId(item.getId());
					pd.setLastMntOn(item.getLastMntOn());
					pd.setAgentCode("");
					pd.setAgencyName("");
					pd.setVerifiedDate(null);
					pd.setReason(0L);
					pd.setSummaryRemarks("");
					pd.setStatus(0);
					personalDiscussionService.save(pd, TableType.TEMP_TAB);
				}
			}
		}
	}

	private void saveTV(Verification item) {
		if (item.getTechnicalVerification() == null) {
			if (item.getLastMntOn() == null) {
				item.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			}
			technicalVerificationService.save(item);
		} else if (item.getRequestType() == RequestType.INITIATE.getKey()
				|| item.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
			TechnicalVerification technicalVerification = new TechnicalVerification();

			technicalVerification.setVerificationId(item.getId());
			if (item.getLastMntOn() == null) {
				technicalVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			} else {
				technicalVerification.setLastMntOn(item.getLastMntOn());
			}
			technicalVerification.setCollateralRef(item.getTechnicalVerification().getCollateralRef());
			technicalVerification.setCollateralType(item.getTechnicalVerification().getCollateralType());
			technicalVerification.setType(item.getRequestType());

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
		// Collateral Documents
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			list = documentDetailsDAO.getDocumentDetailsByRef(item.getReferenceFor(), CollateralConstants.MODULE_NAME,
					"", "_View");
		}

		if (list != null) {
			collateralDocumentList.addAll(list);
		}

		// Set customer documents id's
		Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
		if (customerDocuemnts != null) {
			for (CustomerDocument document : customerDocuemnts) {
				customerDoumentMap.put(document.getCustDocCategory(), document);
			}
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
		if (loanDocuments != null) {
			for (DocumentDetails document : loanDocuments) {
				loanDocumentMap.put(document.getDocCategory(), document);
			}
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
		Map<String, DocumentDetails> collateralDocumentMap = new HashMap<>();
		for (DocumentDetails document : collateralDocumentList) {
			collateralDocumentMap.put(document.getDocCategory(), document);
		}

		for (LVDocument lvDocument : item.getLvDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				DocumentDetails document = collateralDocumentMap.get(lvDocument.getDocumentSubId());
				if (document == null) {
					continue;
				}
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());

			}
		}
	}

	private void setVettingDocumentDetails(FinanceDetail financeDetail, Verification item) {
		List<CustomerDocument> customerDocuemnts = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<DocumentDetails> loanDocuments = financeDetail.getDocumentDetailsList();
		List<DocumentDetails> collateralDocumentList = new ArrayList<>();

		List<DocumentDetails> list = null;
		// Collateral Documents
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			list = documentDetailsDAO.getDocumentDetailsByRef(item.getReferenceFor(), CollateralConstants.MODULE_NAME,
					"", "_View");
		}

		if (list != null) {
			collateralDocumentList.addAll(list);
		}

		// Set customer documents id's
		Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
		if (customerDocuemnts != null) {
			for (CustomerDocument document : customerDocuemnts) {
				customerDoumentMap.put(document.getCustDocCategory(), document);
			}
		}

		for (LVDocument lvDocument : item.getVettingDocuments()) {
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
		if (loanDocuments != null) {
			for (DocumentDetails document : loanDocuments) {
				loanDocumentMap.put(document.getDocCategory(), document);
			}
		}

		for (LVDocument lvDocument : item.getVettingDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.LOAN.getKey()) {
				DocumentDetails document = loanDocumentMap.get(lvDocument.getDocumentSubId());
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());
			}
		}

		// Set collateral documents id's
		Map<String, DocumentDetails> collateralDocumentMap = new HashMap<>();
		for (DocumentDetails document : collateralDocumentList) {
			collateralDocumentMap.put(document.getDocCategory(), document);
		}

		for (LVDocument lvDocument : item.getVettingDocuments()) {
			if (lvDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				DocumentDetails document = collateralDocumentMap.get(lvDocument.getDocumentSubId());
				if (document == null) {
					continue;
				}
				lvDocument.setDocumentId(document.getId());
				lvDocument.setDocumentSubId(document.getDocCategory());
				lvDocument.setDocumentRefId(document.getDocRefId());
				lvDocument.setDocumentUri(document.getDocUri());

			}
		}
	}

	private void setRCUDocumentDetails(FinanceDetail financeDetail, Verification item) {
		List<CustomerDocument> customerDocuemnts = financeDetail.getCustomerDetails().getCustomerDocumentsList();
		List<JointAccountDetail> coApplicants = financeDetail.getJountAccountDetailList();
		Map<Long, Map<String, CustomerDocument>> map = new HashMap<>();
		if (CollectionUtils.isNotEmpty(coApplicants)) {
			for (JointAccountDetail jointAccountDetail : coApplicants) {
				List<CustomerDocument> customerDocumentsList = jointAccountDetail.getCustomerDetails()
						.getCustomerDocumentsList();
				Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
				if (customerDocumentsList != null) {
					for (CustomerDocument customerDocument : customerDocumentsList) {
						customerDocument.setLovDescCustShrtName(
								jointAccountDetail.getCustomerDetails().getCustomer().getCustShrtName());
						customerDoumentMap.put(customerDocument.getCustDocCategory(), customerDocument);
					}
					map.put(jointAccountDetail.getCustomerDetails().getCustomer().getCustID(), customerDoumentMap);
				}
			}
		}
		List<DocumentDetails> loanDocuments = financeDetail.getDocumentDetailsList();
		List<DocumentDetails> collateralDocumentList = new ArrayList<>();

		List<CollateralAssignment> collaterals = financeDetail.getCollateralAssignmentList();
		List<DocumentDetails> list;
		if (collaterals != null) {
			for (CollateralAssignment collateral : collaterals) {
				list = documentDetailsDAO.getDocumentDetailsByRef(collateral.getCollateralRef(),
						CollateralConstants.MODULE_NAME, "", "_View");
				if (list != null) {
					collateralDocumentList.addAll(list);
				}
			}
		}

		// Set customer documents id's
		Map<String, CustomerDocument> customerDoumentMap = new HashMap<>();
		if (customerDocuemnts != null) {
			for (CustomerDocument document : customerDocuemnts) {
				customerDoumentMap.put(document.getCustDocCategory(), document);
			}
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			Integer docType = DocumentType.CUSTOMER.getKey();
			CustomerDocument document = null;
			if (rcuDocument.getDocumentType() == DocumentType.CUSTOMER.getKey()) {
				docType = DocumentType.CUSTOMER.getKey();
				document = customerDoumentMap.get(rcuDocument.getDocCategory());
			} else if (rcuDocument.getDocumentType() == DocumentType.COAPPLICANT.getKey()) {
				docType = DocumentType.COAPPLICANT.getKey();
				if (map.containsKey(rcuDocument.getDocumentId())) {
					document = map.get(rcuDocument.getDocumentId()).get(rcuDocument.getDocCategory());
				}
			}
			if (document != null) {
				rcuDocument.setDocumentId(document.getCustID());
				rcuDocument.setDocumentSubId(document.getCustDocCategory());
				rcuDocument.setDocumentRefId(document.getDocRefId());
				rcuDocument.setDocumentUri(document.getDocUri());
				rcuDocument.setDocumentType(docType);
			}
		}

		// Set loan documents id's
		Map<String, DocumentDetails> loanDocumentMap = new HashMap<>();
		if (loanDocuments != null) {
			for (DocumentDetails document : loanDocuments) {
				loanDocumentMap.put(document.getDocCategory(), document);
			}
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			if (rcuDocument.getDocumentType() == DocumentType.LOAN.getKey()) {
				DocumentDetails document = loanDocumentMap.get(rcuDocument.getDocCategory());
				rcuDocument.setDocumentId(document.getId());
				rcuDocument.setDocumentSubId(rcuDocument.getDocCategory());
				rcuDocument.setDocumentRefId(document.getDocRefId());
				rcuDocument.setDocumentUri(document.getDocUri());
				rcuDocument.setDocumentType(DocumentType.LOAN.getKey());
			}
		}

		// Set collateral documents id's
		Map<String, DocumentDetails> collateralDocumentMap = new HashMap<>();
		String reference;
		for (DocumentDetails document : collateralDocumentList) {
			reference = document.getDocCategory();
			reference = StringUtils.trimToEmpty(document.getReferenceId()).concat(reference);

			collateralDocumentMap.put(reference, document);
		}

		for (RCUDocument rcuDocument : item.getRcuDocuments()) {
			reference = rcuDocument.getDocCategory();
			if (rcuDocument.getDocumentType() == DocumentType.COLLATRL.getKey()) {
				reference = StringUtils.trimToEmpty(rcuDocument.getCollateralRef()).concat(reference);
				DocumentDetails document = collateralDocumentMap.get(reference);
				if (document != null) {
					rcuDocument.setDocumentId(document.getId());
					rcuDocument.setDocumentSubId(rcuDocument.getDocCategory());
					rcuDocument.setDocumentRefId(document.getDocRefId());
					rcuDocument.setDocumentUri(document.getDocUri());
					rcuDocument.setDocumentType(DocumentType.COLLATRL.getKey());
					item.setReference(String.valueOf(document.getId()));
				}
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
	@Override
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
		List<Verification> result = new ArrayList<>();
		List<Verification> verifications = verificationDAO.getVeriFications(keyReference, verificationType);

		if (VerificationType.LV.getKey() != verificationType && VerificationType.VETTING.getKey() != verificationType) {
			setDecision(verifications);
		}

		for (Verification verification : verifications) {
			if (verification.getReinitid() != null && verification.getRequestType() != RequestType.INITIATE.getKey()) {
				continue;
			}
			result.add(verification);
		}

		return result;
	}

	@Override
	public void setLastStatus(Verification verification) {
		Verification lastStatus = verificationDAO.getLastStatus(verification);

		int verificationType = verification.getVerificationType();

		if (lastStatus != null) {
			verification.setLastStatus(lastStatus.getStatus());
			verification.setLastVerificationDate(lastStatus.getVerificationDate());
			verification.setVersion(lastStatus.getVersion());
			verification.setLastVersion(lastStatus.getLastVersion());
			verification.setLastAgency(lastStatus.getLastAgency());
			lastStatus.setReferenceType(verification.getReferenceType());

			if (verificationType == VerificationType.FI.getKey()) {
				if (fieldInvestigationService.isAddressChanged(verification)) {
					verification.setLastStatus(0);
					verification.setLastVerificationDate(null);
					verification.setLastAgency("");
				}

			} else if (verificationType == VerificationType.TV.getKey()) {
				if (technicalVerificationService.isCollateralChanged(verification, TableType.MAIN_TAB)) {
					verification.setLastStatus(0);
					verification.setLastVerificationDate(null);
					verification.setLastAgency("");
				}

			} else if (verificationType == VerificationType.LV.getKey()) {
				if (legalVerificationService.isCollateralDocumentsChanged(verification.getReferenceFor())) {
					verification.setLastStatus(0);
					verification.setLastVerificationDate(null);
					verification.setLastAgency("");
				}

			} else if (verificationType == VerificationType.RCU.getKey()) {

			} else if (verificationType == VerificationType.PD.getKey()) {
				if (personalDiscussionService.isAddressChanged(verification)) {
					verification.setLastStatus(0);
					verification.setLastVerificationDate(null);
					verification.setLastAgency("");
				}

			} else if (verificationType == VerificationType.VETTING.getKey()) {
				if (legalVettingService.isCollateralDocumentsChanged(verification.getReferenceFor())) {
					verification.setLastStatus(0);
					verification.setLastVerificationDate(null);
					verification.setLastAgency("");
				}

			}
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

	@Override
	public void setVettingDetails(List<Verification> verifications) {
		LegalVetting legalVetting;
		for (Verification verification : verifications) {
			legalVetting = legalVettingDAO.getLVFromStage(verification.getId());

			if (legalVetting != null) {
				legalVetting.setVettingDocuments(legalVettingDAO.getLVDocumentsFromStage(verification.getId()));
				verification.setVettingDocuments(legalVetting.getVettingDocuments());
			}

			verification.setLegalVetting(legalVetting);
		}
	}

	private void setDecision(List<Verification> verifications) {
		for (Verification verification : verifications) {
			if ((verification.getStatus() == FIStatus.POSITIVE.getKey()
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

	private void setVerificationData(FinanceDetail financeDetail, Verification verification,
			VerificationType verificationType) {
		Customer customer = financeDetail.getCustomerDetails().getCustomer();
		List<JointAccountDetail> jointaccountdetails = financeDetail.getJountAccountDetailList();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		verification.setModule(Module.LOAN.getKey());
		verification.setCreatedOn(SysParamUtil.getAppDate());
		verification.setKeyReference(financeMain.getFinReference());

		if (verification.getCustId() == null || verification.getCustId() == 0L) {
			verification.setCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
			verification.setCustId(customer.getCustID());
			verification.setCustomerName(customer.getCustShrtName());
		}
		if (verificationType != VerificationType.FI) {
			if (!StringUtils.equals(verification.getReferenceType(), DocumentType.COAPPLICANT.getValue())) {
				verification.setCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
				verification.setCustId(customer.getCustID());
				verification.setCustomerName(customer.getCustShrtName());
			}
		}
		if (verificationType == VerificationType.LV) {
			if (verification.getReference() == null) {
				verification.setReference(customer.getCustCIF());
			}
		} else if (verificationType == VerificationType.RCU && verification.getReference() == null) {
			if (!StringUtils.equals(verification.getReferenceType(), DocumentType.COAPPLICANT.getValue())) {
				verification.setReference(customer.getCustCIF());
			} else {
				if (CollectionUtils.isNotEmpty(jointaccountdetails)) {
					for (JointAccountDetail jointAccountDetail : jointaccountdetails) {
						if (verification.getCustId() != null
								&& jointAccountDetail.getCustID() == verification.getCustId()) {
							verification.setReference(jointAccountDetail.getCustCIF());
							break;
						}
					}
				}
			}
		} else if (verificationType != VerificationType.PD) {
			if (!StringUtils.equalsIgnoreCase(verification.getReferenceType(), DocumentType.COAPPLICANT.getValue())) {
				verification.setCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
				verification.setCustId(customer.getCustID());
				verification.setCustomerName(customer.getCustShrtName());
			} else {
				if (CollectionUtils.isNotEmpty(jointaccountdetails)) {
					for (JointAccountDetail jointAccountDetail : jointaccountdetails) {
						if (verification.getCustId() != null
								&& jointAccountDetail.getCustID() == verification.getCustId()) {
							verification.setCif(jointAccountDetail.getCustCIF());
							if (jointAccountDetail.getCustomerDetails() != null
									&& jointAccountDetail.getCustomerDetails().getCustomer() != null) {
								verification.setCustomerName(
										jointAccountDetail.getCustomerDetails().getCustomer().getCustShrtName());
							}
							break;
						}
					}
				}
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
		if (verificationType == VerificationType.FI) {
			if (fieldInvestigationService.getVerificationFromRecording(verification.getId()) != null) {
				exists = true;
			}
		} else if (verificationType == VerificationType.TV) {
			if (technicalVerificationService.getVerificationFromRecording(verification.getId()) != null) {
				exists = true;
			}
		} else if (verificationType == VerificationType.LV) {
			if (!legalVerificationService.getLVDocuments(verification.getId()).isEmpty()) {
				exists = true;
			}
		} else if (verificationType == VerificationType.RCU) {
			if (!riskContainmentUnitService.getRCUDocument(verification.getId(), verification.getRcuDocument())
					.isEmpty()) {
				exists = true;
			}
		} else if (verificationType == VerificationType.PD) {
			if (personalDiscussionService.getVerificationFromRecording(verification.getId()) != null) {
				exists = true;
			}
		} else if (verificationType == VerificationType.VETTING) {
			if (!legalVettingService.getLVDocuments(verification.getId()).isEmpty()) {
				exists = true;
			}
		}
		return exists;
	}

	@Override
	public List<Integer> getVerificationTypes(String keyReference) {
		return verificationDAO.getVerificationTypes(keyReference);
	}

	@Override
	public List<Verification> getCollateralDocumentsStatus(String collateralReference) {
		return legalVerificationDAO.getCollateralDocumentsStatus(collateralReference);
	}

	@Override
	public List<Verification> getVerificationsForAggrement(String finReference) {
		List<Verification> list = new ArrayList<>();

		list.addAll(verificationDAO.getVeriFications(finReference, VerificationType.FI.getKey()));
		list.addAll(verificationDAO.getVeriFications(finReference, VerificationType.TV.getKey()));
		list.addAll(verificationDAO.getVeriFications(finReference, VerificationType.LV.getKey()));
		list.addAll(verificationDAO.getVeriFications(finReference, VerificationType.RCU.getKey()));
		list.addAll(verificationDAO.getVeriFications(finReference, VerificationType.PD.getKey()));

		for (Verification verification : list) {
			VerificationType type = VerificationType.getVerificationType(verification.getVerificationType());
			long verificationId = verification.getId();

			switch (type) {
			case FI:
				if (FIStatus.getType(verification.getStatus()).getKey() == 0) {
					verification.setVerificationStatus(StringUtils.EMPTY);
				} else {
					verification.setVerificationStatus(FIStatus.getType(verification.getStatus()).getValue());
				}

				verification
						.setFieldInvestigation(fieldInvestigationDAO.getFieldInvestigation(verificationId, "_View"));

				break;
			case TV:
				if (TVStatus.getType(verification.getStatus()).getKey() == 0) {
					verification.setVerificationStatus(StringUtils.EMPTY);
				} else {
					verification.setVerificationStatus(TVStatus.getType(verification.getStatus()).getValue());
				}

				TechnicalVerification technicalVerification = technicalVerificationDAO
						.getTechnicalVerification(verificationId, "_View");
				verification.setTechnicalVerification(technicalVerification);
				break;
			case LV:
				if (LVStatus.getType(verification.getStatus()).getKey() == 0) {
					verification.setVerificationStatus(StringUtils.EMPTY);
				} else {
					verification.setVerificationStatus(LVStatus.getType(verification.getStatus()).getValue());
				}
				LegalVerification legalVerification = legalVerificationDAO.getLegalVerification(verificationId,
						"_View");
				// LV Document Details
				if (null != legalVerification) {
					List<DocumentDetails> lvDocumentList = documentDetailsDAO.getDocumentDetailsByRef(
							String.valueOf(legalVerification.getVerificationId()), VerificationType.LV.getCode(), "",
							"_View");
					if (legalVerification.getDocuments() != null && !legalVerification.getDocuments().isEmpty()) {
						legalVerification.getDocuments().addAll(lvDocumentList);
					} else {
						legalVerification.setDocuments(lvDocumentList);
					}
				}

				verification.setLegalVerification(legalVerification);

				break;
			case RCU:
				if (RCUStatus.getType(verification.getStatus()).getKey() == 0) {
					verification.setVerificationStatus(StringUtils.EMPTY);
				} else {
					verification.setVerificationStatus(RCUStatus.getType(verification.getStatus()).getValue());
				}
				RiskContainmentUnit riskContainmentUnit = riskContainmentUnitDAO.getRiskContainmentUnit(verificationId,
						"_View");

				if (null != riskContainmentUnit) {
					// RCU Document Details
					List<RCUDocument> rcuDocuments = riskContainmentUnitDAO.getRCUDocuments(verificationId, "_View");
					riskContainmentUnit.setRcuDocuments(rcuDocuments);

					// Document Details
					List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
							String.valueOf(verificationId), VerificationType.RCU.getCode(), "", "_View");
					if (riskContainmentUnit.getDocuments() != null && !riskContainmentUnit.getDocuments().isEmpty()) {
						riskContainmentUnit.getDocuments().addAll(documentList);
					} else {
						riskContainmentUnit.setDocuments(documentList);
					}
				}
				verification.setRcuVerification(riskContainmentUnit);
				break;
			case PD:
				if (PDStatus.getType(verification.getStatus()).getKey() == 0) {
					verification.setVerificationStatus(StringUtils.EMPTY);
				} else {
					verification.setVerificationStatus(PDStatus.getType(verification.getStatus()).getValue());
				}

				verification
						.setPersonalDiscussion(personalDiscussionDAO.getPersonalDiscussion(verificationId, "_View"));

				break;
			}

		}

		return list;
	}

	@Override
	public void deleteVerification(Verification verification, TableType tableType) {
		logger.info(Literal.ENTERING);
		verificationDAO.delete(verification, tableType);
		logger.info(Literal.LEAVING);

	}

	/**
	 * Update verification stage tables document id's after saving to database
	 */
	private void updateStageTableDocumentIds(FinanceDetail financeDetail) {
		logger.info(Literal.ENTERING);

		List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {

			for (CollateralSetup collateralSetup : collateralSetupList) {

				if (collateralSetup.isFromLoan()) {
					Long verificationId = verificationDAO.getVerificationIdByReferenceFor(
							collateralSetup.getFinReference(), collateralSetup.getCollateralRef(),
							VerificationType.LV.getKey());

					if (verificationId != null) {
						List<DocumentDetails> documentDetailsList = documentDetailsDAO.getDocumentDetailsByRef(
								collateralSetup.getCollateralRef(), CollateralConstants.MODULE_NAME, "", "_View");

						if (CollectionUtils.isNotEmpty(documentDetailsList)) {

							for (DocumentDetails documentDetails : documentDetailsList) {
								verificationDAO.updateDocumentId(documentDetails, verificationId, TableType.STAGE_TAB);
							}
						}
					}
				}
			}
		}
		logger.info(Literal.LEAVING);
	}

	/**
	 * Update verification stage tables document id's after saving to database
	 */
	private void updateRCUVerificationReference(FinanceDetail financeDetail) {
		logger.info(Literal.ENTERING);

		List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {

			for (CollateralSetup collateralSetup : collateralSetupList) {

				if (collateralSetup.isFromLoan()) {
					List<Long> verificationIds = verificationDAO.getRCUVerificationId(collateralSetup.getFinReference(),
							VerificationType.RCU.getKey(), DocumentType.COLLATRL.getValue());

					if (CollectionUtils.isNotEmpty(verificationIds)) {
						List<DocumentDetails> documentDetailsList = documentDetailsDAO.getDocumentDetailsByRef(
								collateralSetup.getCollateralRef(), CollateralConstants.MODULE_NAME, "", "_View");
						for (Long verificationId : verificationIds) {
							if (CollectionUtils.isNotEmpty(documentDetailsList)) {
								for (DocumentDetails documentDetails : documentDetailsList) {
									verificationDAO.updateRCUReference(documentDetails, verificationId);
								}
							}
						}
					}
				}
			}
		}
		logger.info(Literal.LEAVING);
	}

	@Override
	public void updateReferenceIds(FinanceDetail financeDetail) {
		logger.info(Literal.ENTERING);

		if (financeDetail.isLvInitTab() || financeDetail.isLvApprovalTab()) {
			updateStageTableDocumentIds(financeDetail);
		}
		if (financeDetail.isRcuInitTab() || financeDetail.isRcuApprovalTab()) {
			updateRCUVerificationReference(financeDetail);
		}
		logger.info(Literal.LEAVING);
	}

	@Override
	public List<Verification> getVerificationCount(String finReference, String collateralReference,
			int verificationType, Integer tvStatus) {
		return verificationDAO.getVerificationCount(finReference, collateralReference, verificationType, tvStatus);
	}

	@Override
	public Verification getVerificationStatus(String reference, int verificationType, String addressType,
			String custCif) {
		return verificationDAO.getVerificationStatus(reference, verificationType, addressType, custCif);
	}

	@Override
	public List<String> getApprovedLVVerifications(int decision, int verificationType) {
		return verificationDAO.getAprrovedLVVerifications(decision, verificationType);
	}

	private void saveLVetting(FinanceDetail financeDetail, Verification item) {
		if (item.getRequestType() == RequestType.INITIATE.getKey()) {
			legalVettingService.save(item, TableType.TEMP_TAB);
			setVettingDocumentDetails(financeDetail, item);

			for (LVDocument lvDocument : item.getVettingDocuments()) {
				lvDocument.setVerificationId(item.getId());
			}
			legalVettingService.saveDocuments(item.getVettingDocuments(), TableType.TEMP_TAB);
		}
	}

	private void saveLVettingInit(Verification verification) {
		Long verificationId;
		if (!verification.isApproveTab()) {
			verificationId = verificationDAO.getVerificationIdByReferenceFor(verification.getKeyReference(),
					verification.getReferenceFor(), VerificationType.VETTING.getKey(),
					verification.getVerificationCategory());
			Verification vrf = new Verification();
			if (verification.getId() != 0 || verificationId != null) {
				if (verification.getId() != 0) {
					vrf.setId(verification.getId());
				} else {
					vrf.setId(verificationId);
				}
				if (!isVerificationInRecording(vrf, VerificationType.VETTING) && verification.isNewRecord()) {
					throw new AppException(
							String.format("Collateral %s already initiated", verification.getReferenceFor()));
				}
			}

			if (verification.getId() != 0) {
				verification.setId(verification.getId());
				verificationDAO.update(verification, TableType.MAIN_TAB);
			} else {
				verificationDAO.save(verification, TableType.MAIN_TAB);
			}
		}
		// delete documents
		legalVettingService.deleteDocuments(verification.getId(), TableType.MAIN_TAB);

		// Legal Vetting
		legalVettingService.save(verification, TableType.MAIN_TAB);

		// LV Documents
		for (LVDocument document : verification.getVettingDocuments()) {
			document.setVerificationId(verification.getLegalVetting().getVerificationId());
		}

		legalVettingService.saveDocuments(verification.getVettingDocuments(), TableType.STAGE_TAB);
	}

	@Override
	public void saveLegalVetting(Verification verification) {
		if (verification.getRequestType() == RequestType.WAIVE.getKey()) {
			saveLVettingWaive(verification);
		} else {
			saveLVettingInit(verification);
		}
	}

	@Override
	public void savereInitLegalVetting(FinanceDetail financeDetail, Verification verification) {
		reInitVerification(financeDetail, VerificationType.VETTING, verification);
		saveLVettingInit(verification);

	}

	private void saveLVettingWaive(Verification verification) {
		Verification item = null;
		List<LVDocument> newLVDocs;
		for (LVDocument lvDocument : verification.getLvDocuments()) {
			item = new Verification();
			newLVDocs = new ArrayList<>();
			BeanUtils.copyProperties(verification, item);
			item.setReferenceType(lvDocument.getDocumentSubId());

			Long verificationId = verificationDAO.getVerificationIdByReferenceFor(item.getKeyReference(),
					item.getReferenceType(), VerificationType.LV.getKey(), verification.getVerificationCategory());

			if (verificationId != null) {
				item.setId(verificationId);
				verificationDAO.update(item, TableType.MAIN_TAB);
			} else {
				verificationDAO.save(item, TableType.MAIN_TAB);
			}

			lvDocument.setVerificationId(item.getId());
			newLVDocs.add(lvDocument);
			item.setVettingDocuments(newLVDocs);

			// Legal verification
			legalVerificationService.save(item, TableType.MAIN_TAB);

			// LV Documents
			legalVerificationService.saveDocuments(item.getLvDocuments(), TableType.STAGE_TAB);
		}
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), null);
	}

	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

}