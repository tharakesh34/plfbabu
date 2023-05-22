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
 * * FileName : FacilityServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-11-2013 * * Modified
 * Date : 25-11-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-11-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.service.facility.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralDAO;
import com.pennant.backend.dao.collateral.FacilityDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.facility.FacilityDAO;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FacilityReferenceDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;

/**
 * Service implementation for methods that depends on <b>Facility</b>.<br>
 * 
 */
public class FacilityServiceImpl extends GenericService<Facility> implements FacilityService {
	private static final Logger logger = LogManager.getLogger(FacilityServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FacilityDAO facilityDAO;
	private FacilityReferenceDetailDAO facilityReferenceDetailDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private ScoringSlabDAO scoringSlabDAO;
	private ScoringMetricsDAO scoringMetricsDAO;
	private RuleDAO ruleDAO;
	private CustomerDAO customerDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;
	private FinanceScoreHeaderDAO financeScoreHeaderDAO;
	private CollateralDAO collateralDAO;
	private FacilityDetailDAO facilityDetailDAO;
	private CustomerRatingDAO customerRatingDAO;
	private CustomerDocumentDAO customerDocumentDAO;

	public FacilityServiceImpl() {
		super();
	}

	@Override
	public Facility getFacility() {
		return facilityDAO.getFacility();
	}

	@Override
	public Facility getNewFacility() {
		return facilityDAO.getNewFacility();
	}

	@Override
	public Collateral getNewCollateral() {
		return collateralDAO.getNewCollateral();
	}

	@Override
	public FacilityDetail getNewFacilityDetail() {
		return facilityDetailDAO.getNewFacilityDetail();
	}

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		Facility facility = (Facility) auditHeader.getAuditDetail().getModelData();

		if (facility.isWorkflow()) {
			tableType = "_Temp";
		}

		if (facility.isNewRecord()) {
			facilityDAO.save(facility, tableType);
		} else {
			facilityDAO.update(facility, tableType);
		}

		// DocumentDetails
		if (facility.getDocumentDetailsList() != null && facility.getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = facility.getAuditDetailMap().get("DocumentDetails");
			details = processDocumentsDetails(facility, details, tableType);
			auditDetails.addAll(details);
		}
		// Check List
		if (facility.getFinanceCheckList() != null && facility.getFinanceCheckList().size() > 0) {
			List<AuditDetail> details = facility.getAuditDetailMap().get("FinanceCheckList");
			details = processFinanceCheckListDetails(facility, details, tableType);
			auditDetails.addAll(details);
		}

		// Scoring
		saveOrUpdateScoring(facility);

		// Collateral
		if (facility.getCollaterals() != null && facility.getCollaterals().size() > 0) {
			List<AuditDetail> details = facility.getAuditDetailMap().get("Collateral");
			details = processCollateralDetails(facility, details, tableType);
			auditDetails.addAll(details);
		}

		// FacilityDetails
		if (facility.getFacilityDetails() != null && facility.getFacilityDetails().size() > 0) {
			List<AuditDetail> details = facility.getAuditDetailMap().get("FacilityDetail");
			details = processFacilityDetails(facility, details, tableType);
			auditDetails.addAll(details);
		}
		// CustomerRating
		if (facility.getCustomerRatings() != null && facility.getCustomerRatings().size() > 0) {
			List<AuditDetail> details = facility.getAuditDetailMap().get("CustomerRatings");
			details = processCustomerRatingDetails(facility, details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Facility facility = (Facility) auditHeader.getAuditDetail().getModelData();
		facilityDAO.delete(facility, "");

		auditHeader.setAuditDetails(processChildsAudit(deleteChilds(facility, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public Facility getFacilityById(String id) {
		Facility facility = facilityDAO.getFacilityById(id, "_View");
		facility = getFacilityChildRecords(facility);
		facility.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(facility.getCAFReference(),
				FacilityConstants.MODULE_NAME, "", "_View"));
		facility.setFinanceCheckList(
				financeCheckListReferenceDAO.getCheckListByFinRef(facility.getCAFReference(), null, "_View"));
		facility.setCollaterals(collateralDAO.getCollateralsByCAF(facility.getCAFReference(), "_View"));
		facility.setFacilityDetails(facilityDetailDAO.getFacilityDetailsByCAF(facility.getCAFReference(), "_View"));
		facility.setCustomerRatings(getCustomerRatingByCustomer(facility.getCustID()));
		facility = setCustomerDocuments(facility);
		return facility;
	}

	@Override
	public Facility getLatestFacilityByCustID(final long custID) {
		return facilityDAO.getLatestFacilityByCustID(custID, "_AView");
	}

	@Override
	public Facility setCustomerDocuments(Facility facility) {
		List<String> docTypeList = null;
		List<CheckListDetail> checkListDetailList = null;
		if (facility.getCheckList() != null && !facility.getCheckList().isEmpty()) {
			docTypeList = new ArrayList<String>();
			for (FacilityReferenceDetail financeReferenceDetail : facility.getCheckList()) {
				checkListDetailList = checkListDetailDAO
						.getCheckListDetailByChkList(financeReferenceDetail.getFinRefId(), "_AView");
				for (CheckListDetail checkListDetail : checkListDetailList) {
					if (DocumentCategories.CUSTOMER.getKey().equals(checkListDetail.getCategoryCode())) {
						docTypeList.add(checkListDetail.getDocType());
					}
				}
			}
		}
		// Customer Document Details Fetching Depends on Customer & Doc Type List
		List<DocumentDetails> documentList = null;
		if (docTypeList != null && !docTypeList.isEmpty()) {
			documentList = customerDocumentDAO.getCustDocListByDocTypes(facility.getCustID(), docTypeList, "");

			if (facility.getDocumentDetailsList() != null && !facility.getDocumentDetailsList().isEmpty()) {
				facility.getDocumentDetailsList().addAll(documentList);
			} else {
				facility.setDocumentDetailsList(documentList);
			}
		}
		return facility;
	}

	@Override
	public List<CustomerRating> getCustomerRatingByCustomer(long custId) {
		return customerRatingDAO.getCustomerRatingByCustomer(custId, "_View");
	}

	public Facility getApprovedFacilityById(String id) {
		Facility facility = facilityDAO.getFacilityById(id, "_AView");
		facility = getFacilityChildRecords(facility);
		facility.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(facility.getCAFReference(),
				FacilityConstants.MODULE_NAME, "", ""));
		facility.setFinanceCheckList(
				financeCheckListReferenceDAO.getCheckListByFinRef(facility.getCAFReference(), null, "_AView"));
		facility.setCollaterals(collateralDAO.getCollateralsByCAF(facility.getCAFReference(), "_AView"));
		facility.setFacilityDetails(facilityDetailDAO.getFacilityDetailsByCAF(facility.getCAFReference(), "_AView"));
		facility.setCustomerRatings(customerRatingDAO.getCustomerRatingByCustomer(facility.getCustID(), ""));
		return facility;
	}

	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Facility facility = new Facility();
		BeanUtils.copyProperties((Facility) aAuditHeader.getAuditDetail().getModelData(), facility);

		AuditHeader auditHeader = new AuditHeader();
		BeanUtils.copyProperties(aAuditHeader, auditHeader);

		if (facility.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// List
			auditDetails.addAll(deleteChilds(facility, "", tranType));
			facilityDAO.delete(facility, "");

		} else {
			facility.setRoleCode("");
			facility.setNextRoleCode("");
			facility.setTaskId("");
			facility.setNextTaskId("");
			facility.setWorkflowId(0);

			if (facility.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				facility.setRecordType("");
				facilityDAO.save(facility, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				facility.setRecordType("");
				facilityDAO.update(facility, "");
			}
			// List
			if (facility.getDocumentDetailsList() != null && facility.getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = facility.getAuditDetailMap().get("DocumentDetails");
				details = processDocumentsDetails(facility, details, "");
				auditDetails.addAll(details);
			}
			if (facility.getFinanceCheckList() != null && facility.getFinanceCheckList().size() > 0) {
				List<AuditDetail> details = facility.getAuditDetailMap().get("FinanceCheckList");
				details = processFinanceCheckListDetails(facility, details, "");
				auditDetails.addAll(details);
			}
			if (facility.getCollaterals() != null && facility.getCollaterals().size() > 0) {
				List<AuditDetail> details = facility.getAuditDetailMap().get("Collateral");
				details = processCollateralDetails(facility, details, "");
				auditDetails.addAll(details);
			}
			if (facility.getFacilityDetails() != null && facility.getFacilityDetails().size() > 0) {
				List<AuditDetail> details = facility.getAuditDetailMap().get("FacilityDetail");
				details = processFacilityDetails(facility, details, "");
				auditDetails.addAll(details);
			}
			if (facility.getCustomerRatings() != null && facility.getCustomerRatings().size() > 0) {
				List<AuditDetail> details = facility.getAuditDetailMap().get("CustomerRatings");
				details = processCustomerRatingDetails(facility, details, "");
				auditDetails.addAll(details);
			}
			// updateCustomer(facility);
		}

		facilityDAO.delete(facility, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(deleteChilds(facility, "_Temp", auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new Facility(), facility.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				facility.getBefImage(), facility));
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(facility);
		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Facility facility = (Facility) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		facilityDAO.delete(facility, "_Temp");
		// List
		auditHeader
				.setAuditDetails(processChildsAudit(deleteChilds(facility, "_Temp", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Facility facility = (Facility) auditDetail.getModelData();

		Facility tempFacility = null;
		if (facility.isWorkflow()) {
			tempFacility = facilityDAO.getFacilityById(facility.getId(), "_Temp");
		}
		Facility befFacility = facilityDAO.getFacilityById(facility.getId(), "");

		Facility oldFacility = facility.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = facility.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CAFReference") + ":" + valueParm[0];

		if (facility.isNewRecord()) { // for New record or new record into work flow

			if (!facility.isWorkflow()) {// With out Work flow only new records
				if (befFacility != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (facility.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFacility != null || tempFacility != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFacility == null || tempFacility != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!facility.isWorkflow()) { // With out Work flow for update and delete

				if (befFacility == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFacility != null && !oldFacility.getLastMntOn().equals(befFacility.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempFacility == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldFacility != null && !oldFacility.getLastMntOn().equals(tempFacility.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !facility.isWorkflow()) {
			auditDetail.setBefImage(befFacility);
		}

		return auditDetail;
	}

	@Override
	public Facility getFacilityChildRecords(Facility facility) {
		List<FacilityReferenceDetail> refList = facilityReferenceDetailDAO
				.getFacilityReferenceDetailById(facility.getFacilityType());
		if (refList != null && !refList.isEmpty()) {
			for (FacilityReferenceDetail facRefDetail : refList) {
				switch (facRefDetail.getFinRefType()) {
				case FinanceConstants.PROCEDT_CHECKLIST:
					facRefDetail.setLovDescCheckListAnsDetails(
							checkListDetailDAO.getCheckListDetailByChkList(facRefDetail.getFinRefId(), "_AView"));
					facility.getCheckList().add(facRefDetail);
					break;
				case FinanceConstants.PROCEDT_AGREEMENT:
					facility.getAggrementList().add(facRefDetail);
					break;
				case FinanceConstants.PROCEDT_RTLSCORE:
					facility.getScoringGroupList().add(facRefDetail);
					break;
				case FinanceConstants.PROCEDT_CORPSCORE:
					facility.getCorpScoringGroupList().add(facRefDetail);
					break;
				default:
					break;
				}
			}
		}
		return facility;

	}

	@Override
	public Facility setFacilityScoringDetails(Facility facility) {
		logger.debug(Literal.ENTERING);
		facility.getScoringMetrics().clear();
		facility.getScoringSlabs().clear();

		List<ScoringMetrics> finScoringMetricList = null;
		List<ScoringMetrics> nonFinScoringMetricList = null;

		List<ScoringSlab> scoringSlabsList = null;
		List<ScoringMetrics> scoringMetricslist = null;

		if (StringUtils.trimToNull(facility.getCustCtgCode()) == null) {
			return facility;
		}
		List<FacilityReferenceDetail> scoringGroupList = null;
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(facility.getCustCtgCode())) {
			scoringGroupList = facility.getScoringGroupList();
			if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
				for (FacilityReferenceDetail scoringGroup : scoringGroupList) {
					// Scoring Slab Details List
					scoringSlabsList = scoringSlabDAO.getScoringSlabsByScoreGrpId(scoringGroup.getFinRefId(), "_AView");
					facility.setScoringSlabs(scoringGroup.getFinRefId(), scoringSlabsList);
					// Scoring Metric Details For Retail Customers
					scoringMetricslist = scoringMetricsDAO.getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(),
							"R", "_AView");
					facility.setScoringMetrics(scoringGroup.getFinRefId(), scoringMetricslist);
				}
			}

		} else {
			scoringGroupList = facility.getCorpScoringGroupList();
			if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
				for (FacilityReferenceDetail scoringGroup : scoringGroupList) {
					// Scoring Slab Details List
					scoringSlabsList = scoringSlabDAO.getScoringSlabsByScoreGrpId(scoringGroup.getFinRefId(), "_AView");
					facility.setScoringSlabs(scoringGroup.getFinRefId(), scoringSlabsList);
					// Corporate Scoring Group for Financial Details
					finScoringMetricList = scoringMetricsDAO.getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(),
							"F", "_AView");
					// Non - Financial Scoring Metric Details
					nonFinScoringMetricList = scoringMetricsDAO
							.getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(), "N", "_AView");
				}
			}

			if (finScoringMetricList != null && !finScoringMetricList.isEmpty()) {
				List<Long> metricIdList = new ArrayList<Long>();

				for (ScoringMetrics metric : finScoringMetricList) {
					metricIdList.add(metric.getScoringId());
				}
				List<NFScoreRuleDetail> ruleList = ruleDAO.getNFRulesByNFScoreGroup(metricIdList, "");
				for (NFScoreRuleDetail rule : ruleList) {
					ScoringMetrics metric = new ScoringMetrics();
					metric.setScoringId(rule.getNFRuleId());
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					List<ScoringMetrics> subMetricList = null;
					if (facility.getScoringMetrics().containsKey(rule.getGroupId())) {
						subMetricList = facility.getScoringMetrics().get(rule.getGroupId());
					} else {
						subMetricList = new ArrayList<ScoringMetrics>();
					}
					subMetricList.add(metric);
					facility.setScoringMetrics(rule.getGroupId(), subMetricList);
				}

			}
			if (nonFinScoringMetricList != null && !nonFinScoringMetricList.isEmpty()) {
				List<Long> metricIdList = new ArrayList<Long>();
				for (ScoringMetrics metric : nonFinScoringMetricList) {
					metricIdList.add(metric.getScoringId());
				}
				List<NFScoreRuleDetail> ruleList = ruleDAO.getNFRulesByNFScoreGroup(metricIdList, "");
				for (NFScoreRuleDetail rule : ruleList) {
					ScoringMetrics metric = new ScoringMetrics();
					metric.setScoringId(rule.getNFRuleId());
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					List<ScoringMetrics> subMetricList = null;
					if (facility.getScoringMetrics().containsKey(rule.getGroupId())) {
						subMetricList = facility.getScoringMetrics().get(rule.getGroupId());
					} else {
						subMetricList = new ArrayList<ScoringMetrics>();
					}
					subMetricList.add(metric);
					facility.setScoringMetrics(rule.getGroupId(), subMetricList);
				}
			}
		}
		facility.setScoringGroupList(scoringGroupList);
		facility.setFinScoringMetricList(finScoringMetricList);
		facility.setNonFinScoringMetricList(nonFinScoringMetricList);

		if (!facility.isNewRecord()) {
			setFinScoreHeaderList(facility);
			setExecutedScore(facility);
		}
		logger.debug("Leaving");
		return facility;
	}

	private void setFinScoreHeaderList(Facility facility) {
		// Finance Scoring Module Details List
		List<String> groupIds = null;
		List<FinanceScoreHeader> finScoreHeaderList = facility.getFinScoreHeaderList();
		List<Long> headerIds = null;
		List<FinanceScoreDetail> financeScoreDetails = null;
		List<FinanceScoreDetail> scoreDetailList = null;

		String finReference = facility.getCAFReference();

		if (StringUtils.trimToNull(finReference) != null) {
			finScoreHeaderList = financeScoreHeaderDAO.getFinScoreHeaderList(finReference, "_View");

			if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
				headerIds = new ArrayList<Long>();
				groupIds = new ArrayList<String>();

				for (FinanceScoreHeader header : finScoreHeaderList) {
					headerIds.add(header.getHeaderId());
					groupIds.add(String.valueOf(header.getGroupId()));
				}

				if (facility.getScoreDetailListMap() == null) {
					new HashMap<Long, List<FinanceScoreDetail>>();
				}

				facility.getScoreDetailListMap().clear();

				financeScoreDetails = financeScoreHeaderDAO.getFinScoreDetailList(headerIds, "_View");

				if (financeScoreDetails != null) {
					for (FinanceScoreDetail scoreDetail : financeScoreDetails) {
						scoreDetailList = new ArrayList<FinanceScoreDetail>();

						if (facility.getScoreDetailListMap().containsKey(scoreDetail.getHeaderId())) {
							scoreDetailList = facility.getScoreDetailListMap().get(scoreDetail.getHeaderId());
							facility.getScoreDetailListMap().remove(scoreDetail.getHeaderId());
						}

						scoreDetailList.add(scoreDetail);
						facility.getScoreDetailListMap().put(scoreDetail.getHeaderId(), scoreDetailList);
					}
				}
			}
		}

		facility.setFinScoreHeaderList(finScoreHeaderList);
	}

	private void setExecutedScore(Facility facility) {
		List<FinanceScoreHeader> scoringHeaderList = facility.getFinScoreHeaderList();
		Map<Long, List<FinanceScoreDetail>> scoreDtlListMap = facility.getScoreDetailListMap();
		List<FinanceScoreDetail> scoreDetailList = null;
		List<FacilityReferenceDetail> financeReferenceList = facility.getScoringGroupList();
		List<ScoringMetrics> scoringMetricsList = null;

		int minScore = 0;
		int executedScore = 0;
		int overRideScore = 0;
		boolean isOverride = false;

		// Retail Scoring
		if (financeReferenceList != null && !financeReferenceList.isEmpty()) {

			for (FacilityReferenceDetail financeReferenceDetail : financeReferenceList) {
				scoringMetricsList = facility.getScoringMetrics().get(financeReferenceDetail.getFinRefId());
				if (scoringMetricsList != null && !scoringMetricsList.isEmpty()) {
					for (ScoringMetrics scoringMetrics : scoringMetricsList) {
						if (scoringMetrics.getLovDescExecutedScore().compareTo(BigDecimal.ZERO) == 0) {
							for (FinanceScoreHeader header : scoringHeaderList) {
								minScore = header.getMinScore();
								overRideScore = header.getOverrideScore();
								isOverride = header.isOverride();

								if (scoreDtlListMap != null && scoreDtlListMap.containsKey(header.getHeaderId())) {
									scoreDetailList = scoreDtlListMap.get(header.getHeaderId());

									for (FinanceScoreDetail finScoreDetail : scoreDetailList) {
										if (finScoreDetail.getRuleId() == scoringMetrics.getScoringId()) {
											scoringMetrics.setLovDescExecutedScore(finScoreDetail.getExecScore());
											executedScore = executedScore + finScoreDetail.getExecScore().intValue();
											break;
										}
									}
								}

							}
						}
					}
				}

			}
		}

		// Corporate Scoring
		if (facility.getFinScoringMetricList() != null) {

			for (ScoringMetrics scoringMetric : facility.getFinScoringMetricList()) {
				if (facility.getScoringMetrics().containsKey(scoringMetric.getScoringId())) {
					List<ScoringMetrics> subMetricList = facility.getScoringMetrics().get(scoringMetric.getScoringId());

					if (subMetricList != null) {
						for (ScoringMetrics subScoreMetric : subMetricList) {
							if (subScoreMetric.getLovDescExecutedScore().compareTo(BigDecimal.ZERO) == 0) {
								for (FinanceScoreHeader header : scoringHeaderList) {
									minScore = header.getMinScore();
									overRideScore = header.getOverrideScore();
									isOverride = header.isOverride();

									if (scoreDtlListMap != null && scoreDtlListMap.containsKey(header.getHeaderId())) {
										scoreDetailList = scoreDtlListMap.get(header.getHeaderId());

										for (FinanceScoreDetail finScoreDetail : scoreDetailList) {
											if (finScoreDetail.getRuleId() == subScoreMetric.getScoringId()) {
												subScoreMetric.setLovDescExecutedScore(finScoreDetail.getExecScore());
												executedScore = executedScore
														+ finScoreDetail.getExecScore().intValue();
												break;
											}
										}
									}

								}
							}
						}
					}

				}
			}
		}

		if (minScore != 0) {
			if (minScore <= executedScore) {
				facility.setSufficientScore(true);
			} else if (isOverride && (executedScore >= overRideScore)) {
				facility.setSufficientScore(true);
			}
		}
	}

	@Override
	public CustomerEligibilityCheck getCustomerEligibility(Customer customer, long custID) {
		if (customer == null) {
			customer = customerDAO.getCustomerByID(custID, "");
		}
		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		Date curBussDate = SysParamUtil.getAppDate();
		BeanUtils.copyProperties(customer, eligibilityCheck);
		int dobMonths = DateUtil.getMonthsBetween(customer.getCustDOB(), SysParamUtil.getAppDate());
		BigDecimal age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
		eligibilityCheck.setCustAge(age);
		// Minor Age Calculation
		int minorAge = SysParamUtil.getValueAsInt("MINOR_AGE");
		if (age.compareTo(BigDecimal.valueOf(minorAge)) < 0) {
			eligibilityCheck.setCustIsMinor(true);
		} else {
			eligibilityCheck.setCustIsMinor(false);
		}
		// Customer Total Income & Expense Conversion
		eligibilityCheck.setCustTotalIncome(customer.getCustTotalIncome());
		eligibilityCheck.setCustTotalExpense(customer.getCustTotalExpense());
		eligibilityCheck
				.setBlackListExpPeriod(DateUtil.getMonthsBetween(curBussDate, customer.getCustBlackListDate()));
		eligibilityCheck.setCustCtgCode(customer.getCustCtgCode());
		// Finance Amount Calculations
		List<FinanceProfitDetail> financeProfitDetailsList = customerDAO.getCustFinAmtDetails(customer.getCustID(),
				eligibilityCheck);
		BigDecimal custFinAmount = BigDecimal.ZERO;
		BigDecimal custODAmount = BigDecimal.ZERO;

		for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
			custFinAmount = custFinAmount.add(financeProfitDetail.getTotalPriBal());
			custODAmount = custODAmount.add(financeProfitDetail.getODPrincipal());
		}

		eligibilityCheck.setCustLiveFinAmount(custFinAmount);
		eligibilityCheck.setCustPastDueAmt(custODAmount);

		// get Customer Designation if customer status is Employed
		eligibilityCheck.setCustEmpDesg(customerDAO.getCustEmpDesg(customer.getCustID()));
		eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

		// get Customer Employee Allocation Type if customer status is Employed
		/*
		 * eligibilityCheck.setCustEmpAloc(customerDAO.getCustCurEmpAlocType( customer.getCustID()));
		 */

		// Get Customer Repay Totals On Bank
		eligibilityCheck.setCustRepayBank(customerDAO.getCustRepayBankTotal(customer.getCustID()));

		// Get Customer Repay Totals by Other Commitments
		eligibilityCheck.setCustRepayOther(customerDAO.getCustRepayOtherTotal(customer.getCustID()));

		// Get Customer Worst Status From Finances
		eligibilityCheck.setCustWorstSts(customerDAO.getCustWorstSts(customer.getCustID()));

		return eligibilityCheck;
	}

	// Update Customer Details On Approval
	// private void updateCustomer(Facility facility) {
	// if (StringUtils.trimToEmpty(facility.getCustCoreBank()).equals("")) {
	// Customer customer = new Customer();
	// customer.setCustID(facility.getCustID());
	// customer.setCustCOB(facility.getCountryOfDomicile());
	// customer.setCustRiskCountry(facility.getCountryOfRisk());
	// customer.setCustDOB(facility.getEstablishedDate());
	// customer.setCustSector(facility.getNatureOfBusiness());
	// customerDAO.updateFromFacility(customer, "");
	// }
	// }

	// =================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Facility facility = (Facility) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (facility.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// DocumentDetails
		if (facility.getDocumentDetailsList() != null && facility.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(facility, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}
		// Check List
		if (facility.getFinanceCheckList() != null && facility.getFinanceCheckList().size() > 0) {
			auditDetailMap.put("FinanceCheckList",
					setFinanceCheckListDetailsAuditData(facility, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinanceCheckList"));
		}
		// Collateral
		if (facility.getCollaterals() != null && facility.getCollaterals().size() > 0) {
			auditDetailMap.put("Collateral", setCollateralDetailsAuditData(facility, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Collateral"));
		}
		// FacilityDetails
		if (facility.getFacilityDetails() != null && facility.getFacilityDetails().size() > 0) {
			auditDetailMap.put("FacilityDetail", setFacilityDetailsAuditData(facility, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FacilityDetail"));
		}
		// FacilityDetails
		if (facility.getCustomerRatings() != null && facility.getCustomerRatings().size() > 0) {
			auditDetailMap.put("CustomerRatings", setCustomerRatingDetailsAuditData(facility, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerRatings"));
		}

		facility.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(facility);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (AuditDetail auditDetail : list) {
				String transType = "";
				String rcdType = "";
				Object object = auditDetail.getModelData();

				if (object instanceof DocumentDetails) {
					// DocumentDetails
					DocumentDetails documentDetails = (DocumentDetails) object;
					rcdType = documentDetails.getRecordType();
				} else if (object instanceof FinanceCheckListReference) {
					// Check List
					FinanceCheckListReference financeCheckListReference = (FinanceCheckListReference) object;
					rcdType = financeCheckListReference.getRecordType();
				} else if (object instanceof Collateral) {
					// Collateral
					Collateral collateral = (Collateral) object;
					rcdType = collateral.getRecordType();
				} else if (object instanceof FacilityDetail) {
					// FacilityDetail
					FacilityDetail facilityDetail = (FacilityDetail) object;
					rcdType = facilityDetail.getRecordType();
				} else if (object instanceof CustomerRating) {
					// FacilityDetail
					CustomerRating rating = (CustomerRating) object;
					rcdType = rating.getRecordType();
				}

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isNotEmpty(transType)) {
					auditDetailsList.add(
							new AuditDetail(transType, auditDetail.getAuditSeq(), auditDetail.getBefImage(), object));
				}

			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	public List<AuditDetail> deleteChilds(Facility facility, String tableType, String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		// DocumentDetails
		if (facility.getDocumentDetailsList() != null && facility.getDocumentDetailsList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new DocumentDetails(),
					new DocumentDetails().getExcludeFields());
			for (int i = 0; i < facility.getDocumentDetailsList().size(); i++) {
				DocumentDetails expenseDetail = facility.getDocumentDetailsList().get(i);
				if (StringUtils.isNotEmpty(expenseDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							expenseDetail.getBefImage(), expenseDetail));
				}
			}
			documentDetailsDAO.deleteList(facility.getDocumentDetailsList(), tableType);
		}

		// Check List
		if (facility.getFinanceCheckList() != null && facility.getFinanceCheckList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceCheckListReference(),
					new FinanceCheckListReference().getExcludeFields());
			for (int i = 0; i < facility.getFinanceCheckList().size(); i++) {
				FinanceCheckListReference expenseDetail = facility.getFinanceCheckList().get(i);
				if (StringUtils.isNotEmpty(expenseDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							expenseDetail.getBefImage(), expenseDetail));
				}
			}
			financeCheckListReferenceDAO.delete(facility.getCAFReference(), tableType);
		}
		// Collateral
		if (facility.getCollaterals() != null && facility.getCollaterals().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new Collateral(), new Collateral().getExcludeFields());
			for (int i = 0; i < facility.getCollaterals().size(); i++) {
				Collateral collateral = facility.getCollaterals().get(i);
				if (StringUtils.isNotEmpty(collateral.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collateral.getBefImage(),
							collateral));
				}
			}
			collateralDAO.deleteByCAF(facility.getCAFReference(), tableType);
		}
		// FacilityDetails
		if (facility.getFacilityDetails() != null && facility.getFacilityDetails().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FacilityDetail(),
					new FacilityDetail().getExcludeFields());
			for (int i = 0; i < facility.getFacilityDetails().size(); i++) {
				FacilityDetail facilityDetail = facility.getFacilityDetails().get(i);
				if (StringUtils.isNotEmpty(facilityDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							facilityDetail.getBefImage(), facilityDetail));
				}
			}
			facilityDetailDAO.deleteByCAF(facility.getCAFReference(), tableType);
		}
		// FacilityDetails
		if (facility.getCustomerRatings() != null && facility.getCustomerRatings().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new CustomerRating());
			for (int i = 0; i < facility.getCustomerRatings().size(); i++) {
				CustomerRating customerRating = facility.getCustomerRatings().get(i);
				if (StringUtils.isNotEmpty(customerRating.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							customerRating.getBefImage(), customerRating));
				}
			}
			customerRatingDAO.deleteByCustomer(facility.getCustID(), tableType);
		}

		return auditList;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Facility facility = (Facility) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;
		// Document Details
		if (facility.getAuditDetailMap().get("DocumentDetails") != null) {
			auditDetails = facility.getAuditDetailMap().get("DocumentDetails");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationDocumentDetails(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		// Check list
		if (facility.getAuditDetailMap().get("FinanceCheckList") != null) {
			auditDetails = facility.getAuditDetailMap().get("FinanceCheckList");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationFinanceCheckList(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		// Collateral
		if (facility.getAuditDetailMap().get("Collateral") != null) {
			auditDetails = facility.getAuditDetailMap().get("Collateral");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationCollateral(auditDetail, usrLanguage, method).getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		// FacilityDetails
		if (facility.getAuditDetailMap().get("FacilityDetail") != null) {
			auditDetails = facility.getAuditDetailMap().get("FacilityDetail");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationFacilityDetails(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		// CustomerRatings
		if (facility.getAuditDetailMap().get("CustomerRatings") != null) {
			auditDetails = facility.getAuditDetailMap().get("CustomerRatings");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationCustomerRatingDetails(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		return errorDetails;
	}
	// Document details

	private List<AuditDetail> processDocumentsDetails(Facility facility, List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setReferenceId(facility.getCAFReference());
				documentDetails.setFinEvent("");
				documentDetails.setWorkflowId(0);
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
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
					} else if (documentDetails.isNewRecord()) {
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
					documentDetailsDAO.save(documentDetails, type);
				}
				if (updateRecord) {
					documentDetailsDAO.update(documentDetails, type);
				}
				if (deleteRecord) {
					documentDetailsDAO.delete(documentDetails, type);
				}
				if (approveRec) {
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			} else {
				CustomerDocument custdoc = getCustomerDocument(documentDetails, facility);
				if (custdoc.isNewRecord()) {
					customerDocumentDAO.save(custdoc, "");
				} else {
					customerDocumentDAO.update(custdoc, "");
				}
			}
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private CustomerDocument getCustomerDocument(DocumentDetails documentDetails, Facility financeMain) {
		CustomerDocument customerDocument = null;
		if (financeMain != null) {
			customerDocument = customerDocumentDAO.getCustomerDocumentById(financeMain.getCustID(),
					documentDetails.getDocCategory(), "");
		}

		if (customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocExpDate(documentDetails.getCustDocExpDate());
			customerDocument.setCustDocIsAcrive(documentDetails.isCustDocIsAcrive());
			customerDocument.setCustDocIssuedCountry(documentDetails.getCustDocIssuedCountry());
			customerDocument.setLovDescCustDocIssuedCountry(documentDetails.getLovDescCustDocIssuedCountry());
			customerDocument.setCustDocIssuedOn(documentDetails.getCustDocIssuedOn());
			customerDocument.setCustDocIsVerified(documentDetails.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetails.getCustDocRcvdOn());
			customerDocument.setCustDocSysName(documentDetails.getCustDocSysName());
			customerDocument.setCustDocTitle(documentDetails.getCustDocTitle());
			customerDocument.setCustDocVerifiedBy(documentDetails.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
		}

		if (financeMain != null) {
			customerDocument.setCustID(financeMain.getCustID());
			customerDocument.setLovDescCustCIF(financeMain.getCustCIF());
			customerDocument.setLovDescCustShrtName(financeMain.getCustShrtName());
		}

		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocImage(documentDetails.getDocImage());
		customerDocument.setCustDocType(documentDetails.getDoctype());
		customerDocument.setCustDocCategory(documentDetails.getDocCategory());
		customerDocument.setCustDocName(documentDetails.getDocName());
		customerDocument.setLovDescCustDocCategory(documentDetails.getLovDescDocCategoryName());

		customerDocument.setRecordStatus(documentDetails.getRecordStatus());
		customerDocument.setRecordType(documentDetails.getRecordType());
		customerDocument.setUserDetails(documentDetails.getUserDetails());
		customerDocument.setVersion(documentDetails.getVersion());
		customerDocument.setLastMntBy(documentDetails.getLastMntBy());
		customerDocument.setLastMntOn(documentDetails.getLastMntOn());
		return customerDocument;
	}

	private List<AuditDetail> setDocumentDetailsAuditData(Facility facility, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new DocumentDetails(),
				new DocumentDetails().getExcludeFields());
		for (int i = 0; i < facility.getDocumentDetailsList().size(); i++) {
			DocumentDetails documentDetails = facility.getDocumentDetailsList().get(i);
			documentDetails.setWorkflowId(facility.getWorkflowId());
			boolean isRcdType = false;
			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
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
			documentDetails.setRecordStatus(facility.getRecordStatus());
			documentDetails.setUserDetails(facility.getUserDetails());
			documentDetails.setLastMntOn(facility.getLastMntOn());
			if (StringUtils.isNotBlank(documentDetails.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						documentDetails.getBefImage(), documentDetails));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validationDocumentDetails(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		DocumentDetails documentDetails = (DocumentDetails) auditDetail.getModelData();
		if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
			DocumentDetails tempDocumentDetails = null;
			if (documentDetails.isWorkflow()) {
				tempDocumentDetails = documentDetailsDAO.getDocumentDetailsById(documentDetails.getId(), "_Temp");
			}
			DocumentDetails befDocumentDetails = documentDetailsDAO.getDocumentDetailsById(documentDetails.getId(), "");

			DocumentDetails oldDocumentDetails = documentDetails.getBefImage();

			String[] errParm = new String[1];
			String[] valueParm = new String[1];
			valueParm[0] = documentDetails.getCustDocTitle();
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

			if (documentDetails.isNewRecord()) { // for New record or new record into work flow

				if (!documentDetails.isWorkflow()) {// With out Work flow only new records
					if (befDocumentDetails != null) { // Record Already Exists in the table then error
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // with work flow
					if (documentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																									// is new
						if (befDocumentDetails != null || tempDocumentDetails != null) { // if records already exists in
																							// the main table
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
						}
					} else { // if records not exists in the Main flow table
						if (befDocumentDetails == null || tempDocumentDetails != null) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
						}
					}
				}
			} else {
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!documentDetails.isWorkflow()) { // With out Work flow for update and delete

					if (befDocumentDetails == null) { // if records not exists in the main table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
					} else {
						if (oldDocumentDetails != null
								&& !oldDocumentDetails.getLastMntOn().equals(befDocumentDetails.getLastMntOn())) {
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
									.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
								auditDetail.setErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
							} else {
								auditDetail.setErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
							}
						}
					}
				} else {

					if (tempDocumentDetails == null) { // if records not exists in the Work flow table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}

					if (oldDocumentDetails != null
							&& !oldDocumentDetails.getLastMntOn().equals(tempDocumentDetails.getLastMntOn())) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !documentDetails.isWorkflow()) {
				auditDetail.setBefImage(befDocumentDetails);
			}
		}
		return auditDetail;
	}

	// CheckList details

	private List<AuditDetail> processFinanceCheckListDetails(Facility facility, List<AuditDetail> auditDetails,
			String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference checkListReference = (FinanceCheckListReference) auditDetails.get(i)
					.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				checkListReference.setRoleCode("");
				checkListReference.setNextRoleCode("");
				checkListReference.setTaskId("");
				checkListReference.setNextTaskId("");
			}
			checkListReference.setFinReference(facility.getCAFReference());
			checkListReference.setWorkflowId(0);
			if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (checkListReference.isNewRecord()) {
				saveRecord = true;
				if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					checkListReference.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					checkListReference.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					checkListReference.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (checkListReference.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = checkListReference.getRecordType();
				recordStatus = checkListReference.getRecordStatus();
				checkListReference.setRecordType("");
				checkListReference.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				financeCheckListReferenceDAO.save(checkListReference, type);
			}
			if (updateRecord) {
				financeCheckListReferenceDAO.update(checkListReference, type);
			}
			if (deleteRecord) {
				financeCheckListReferenceDAO.delete(checkListReference, type);
			}
			if (approveRec) {
				checkListReference.setRecordType(rcdType);
				checkListReference.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(checkListReference);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> setFinanceCheckListDetailsAuditData(Facility facility, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceCheckListReference(),
				new FinanceCheckListReference().getExcludeFields());
		for (int i = 0; i < facility.getFinanceCheckList().size(); i++) {
			FinanceCheckListReference checkListReference = facility.getFinanceCheckList().get(i);
			checkListReference.setWorkflowId(facility.getWorkflowId());
			boolean isRcdType = false;
			if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				checkListReference.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				checkListReference.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				checkListReference.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}
			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				checkListReference.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| checkListReference.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			checkListReference.setRecordStatus(facility.getRecordStatus());
			checkListReference.setUserDetails(facility.getUserDetails());
			checkListReference.setLastMntOn(facility.getLastMntOn());
			if (StringUtils.isNotBlank(checkListReference.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						checkListReference.getBefImage(), checkListReference));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validationFinanceCheckList(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceCheckListReference finCheckRef = (FinanceCheckListReference) auditDetail.getModelData();

		FinanceCheckListReference tempFinanceCheckListReference = null;
		if (finCheckRef.isWorkflow()) {
			tempFinanceCheckListReference = financeCheckListReferenceDAO.getFinanceCheckListReferenceById(
					finCheckRef.getFinReference(), finCheckRef.getQuestionId(), finCheckRef.getAnswer(), "_Temp");
		}
		FinanceCheckListReference befFinanceCheckListReference = financeCheckListReferenceDAO
				.getFinanceCheckListReferenceById(finCheckRef.getFinReference(), finCheckRef.getQuestionId(),
						finCheckRef.getAnswer(), "");

		FinanceCheckListReference oldFinanceCheckListReference = finCheckRef.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finCheckRef.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (finCheckRef.isNewRecord()) { // for New record or new record into work flow

			if (!finCheckRef.isWorkflow()) {// With out Work flow only new records
				if (befFinanceCheckListReference != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (finCheckRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinanceCheckListReference != null || tempFinanceCheckListReference != null) { // if records
																											// already
																											// exists in
																											// the main
																											// table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceCheckListReference == null || tempFinanceCheckListReference != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finCheckRef.isWorkflow()) { // With out Work flow for update and delete

				if (befFinanceCheckListReference == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinanceCheckListReference != null && !oldFinanceCheckListReference.getLastMntOn()
							.equals(befFinanceCheckListReference.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempFinanceCheckListReference == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldFinanceCheckListReference != null && !oldFinanceCheckListReference.getLastMntOn()
						.equals(tempFinanceCheckListReference.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finCheckRef.isWorkflow()) {
			auditDetail.setBefImage(befFinanceCheckListReference);
		}

		return auditDetail;
	}

	// Scoring
	public void saveOrUpdateScoring(Facility facility) {
		List<FinanceScoreHeader> finScoreHeaderList = facility.getFinScoreHeaderList();

		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			for (FinanceScoreHeader header : finScoreHeaderList) {
				List<FinanceScoreDetail> scoreDetailList = null;
				if (facility.getScoreDetailListMap().containsKey(header.getHeaderId())) {
					scoreDetailList = facility.getScoreDetailListMap().get(header.getHeaderId());
				}
				header.setFinReference(facility.getCAFReference());
				financeScoreHeaderDAO.deleteHeader(header, "");
				long headerId = financeScoreHeaderDAO.saveHeader(header, "");

				if (scoreDetailList != null) {
					for (FinanceScoreDetail detail : scoreDetailList) {
						detail.setHeaderId(headerId);
					}
					financeScoreHeaderDAO.deleteDetailList(headerId, "");
					financeScoreHeaderDAO.saveDetailList(scoreDetailList, "");
				}
			}
		}
	}
	// Collateral

	private List<AuditDetail> processCollateralDetails(Facility facility, List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			Collateral collateral = (Collateral) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				collateral.setRoleCode("");
				collateral.setNextRoleCode("");
				collateral.setTaskId("");
				collateral.setNextTaskId("");
			}
			collateral.setCAFReference(facility.getCAFReference());
			collateral.setCustID(facility.getCustID());
			collateral.setWorkflowId(0);
			if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (collateral.isNewRecord()) {
				saveRecord = true;
				if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (collateral.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = collateral.getRecordType();
				recordStatus = collateral.getRecordStatus();
				collateral.setRecordType("");
				collateral.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				collateralDAO.save(collateral, type);
			}
			if (updateRecord) {
				collateralDAO.update(collateral, type);
			}
			if (deleteRecord) {
				collateralDAO.delete(collateral, type);
			}
			if (approveRec) {
				collateral.setRecordType(rcdType);
				collateral.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(collateral);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> setCollateralDetailsAuditData(Facility facility, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new Collateral(), new Collateral().getExcludeFields());
		for (int i = 0; i < facility.getCollaterals().size(); i++) {
			Collateral collateral = facility.getCollaterals().get(i);
			collateral.setWorkflowId(facility.getWorkflowId());
			boolean isRcdType = false;
			if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				collateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				collateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				collateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}
			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				collateral.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			collateral.setRecordStatus(facility.getRecordStatus());
			collateral.setUserDetails(facility.getUserDetails());
			collateral.setLastMntOn(facility.getLastMntOn());
			if (StringUtils.isNotBlank(collateral.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collateral.getBefImage(),
						collateral));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validationCollateral(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Collateral collateral = (Collateral) auditDetail.getModelData();

		Collateral tempCollateral = null;
		if (collateral.isWorkflow()) {
			tempCollateral = collateralDAO.getCollateralById(collateral.getCAFReference(), collateral.getReference(),
					"_Temp");
		}
		Collateral befCollateral = collateralDAO.getCollateralById(collateral.getCAFReference(),
				collateral.getReference(), "");

		Collateral oldFinanceCheckListReference = collateral.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = collateral.getCAFReference();
		errParm[0] = PennantJavaUtil.getLabel("label_CAFReference") + ":" + valueParm[0];

		if (collateral.isNewRecord()) { // for New record or new record into work flow

			if (!collateral.isWorkflow()) {// With out Work flow only new records
				if (befCollateral != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (collateral.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCollateral != null || tempCollateral != null) { // if records already exists in the main
																			// table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befCollateral == null || tempCollateral != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!collateral.isWorkflow()) { // With out Work flow for update and delete

				if (befCollateral == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFinanceCheckListReference != null
							&& !oldFinanceCheckListReference.getLastMntOn().equals(befCollateral.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempCollateral == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldFinanceCheckListReference != null
						&& !oldFinanceCheckListReference.getLastMntOn().equals(tempCollateral.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !collateral.isWorkflow()) {
			auditDetail.setBefImage(befCollateral);
		}

		return auditDetail;
	}

	// FacilityDetails

	private List<AuditDetail> processFacilityDetails(Facility facility, List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			FacilityDetail collateral = (FacilityDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				collateral.setRoleCode("");
				collateral.setNextRoleCode("");
				collateral.setTaskId("");
				collateral.setNextTaskId("");
			}
			collateral.setCAFReference(facility.getCAFReference());
			collateral.setCustID(facility.getCustID());
			collateral.setWorkflowId(0);
			if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (collateral.isNewRecord()) {
				saveRecord = true;
				if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					collateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (collateral.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (collateral.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = collateral.getRecordType();
				recordStatus = collateral.getRecordStatus();
				collateral.setRecordType("");
				collateral.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				facilityDetailDAO.save(collateral, type);
			}
			if (updateRecord) {
				facilityDetailDAO.update(collateral, type);
			}
			if (deleteRecord) {
				facilityDetailDAO.delete(collateral, type);
			}
			if (approveRec) {
				collateral.setRecordType(rcdType);
				collateral.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(collateral);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> setFacilityDetailsAuditData(Facility facility, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FacilityDetail(),
				new FacilityDetail().getExcludeFields());
		for (int i = 0; i < facility.getFacilityDetails().size(); i++) {
			FacilityDetail facilityDetail = facility.getFacilityDetails().get(i);
			facilityDetail.setWorkflowId(facility.getWorkflowId());
			boolean isRcdType = false;
			if (facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				facilityDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				facilityDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				facilityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				facilityDetail.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| facilityDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			facilityDetail.setRecordStatus(facility.getRecordStatus());
			facilityDetail.setUserDetails(facility.getUserDetails());
			facilityDetail.setLastMntOn(facility.getLastMntOn());
			if (StringUtils.isNotBlank(facilityDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						facilityDetail.getBefImage(), facilityDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validationFacilityDetails(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FacilityDetail facilityDetail = (FacilityDetail) auditDetail.getModelData();

		FacilityDetail tempFacilityDetail = null;
		if (facilityDetail.isWorkflow()) {
			tempFacilityDetail = facilityDetailDAO.getFacilityDetailById(facilityDetail.getFacilityRef(), "_Temp");
		}
		FacilityDetail befFacilityDetail = facilityDetailDAO.getFacilityDetailById(facilityDetail.getFacilityRef(), "");

		FacilityDetail oldFacilityDetail = facilityDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = facilityDetail.getFacilityRef();
		errParm[0] = PennantJavaUtil.getLabel("label_FacilityRef") + ":" + valueParm[0];

		if (facilityDetail.isNewRecord()) { // for New record or new record into work flow

			if (!facilityDetail.isWorkflow()) {// With out Work flow only new records
				if (befFacilityDetail != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (facilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFacilityDetail != null || tempFacilityDetail != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befFacilityDetail == null || tempFacilityDetail != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!facilityDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befFacilityDetail == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldFacilityDetail != null
							&& !oldFacilityDetail.getLastMntOn().equals(befFacilityDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempFacilityDetail == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldFacilityDetail != null
						&& !oldFacilityDetail.getLastMntOn().equals(tempFacilityDetail.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !facilityDetail.isWorkflow()) {
			auditDetail.setBefImage(befFacilityDetail);
		}

		return auditDetail;
	}

	// Customer Rating

	private List<AuditDetail> processCustomerRatingDetails(Facility facility, List<AuditDetail> auditDetails,
			String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			CustomerRating customerRating = (CustomerRating) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerRating.setRoleCode("");
				customerRating.setNextRoleCode("");
				customerRating.setTaskId("");
				customerRating.setNextTaskId("");
			}
			customerRating.setCustID(facility.getCustID());
			customerRating.setWorkflowId(0);
			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerRating.isNewRecord()) {
				saveRecord = true;
				if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerRating.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = customerRating.getRecordType();
				recordStatus = customerRating.getRecordStatus();
				customerRating.setRecordType("");
				customerRating.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				customerRatingDAO.save(customerRating, type);
			}
			if (updateRecord) {
				customerRatingDAO.update(customerRating, type);
			}
			if (deleteRecord) {
				customerRatingDAO.delete(customerRating, type);
			}
			if (approveRec) {
				customerRating.setRecordType(rcdType);
				customerRating.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerRating);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> setCustomerRatingDetailsAuditData(Facility facility, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerRating());
		for (int i = 0; i < facility.getCustomerRatings().size(); i++) {
			CustomerRating customerRating = facility.getCustomerRatings().get(i);
			customerRating.setWorkflowId(facility.getWorkflowId());
			boolean isRcdType = false;
			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				customerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}
			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				customerRating.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			customerRating.setRecordStatus(facility.getRecordStatus());
			customerRating.setUserDetails(facility.getUserDetails());
			customerRating.setLastMntOn(facility.getLastMntOn());
			if (StringUtils.isNotBlank(customerRating.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						customerRating.getBefImage(), customerRating));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validationCustomerRatingDetails(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		CustomerRating customerRating = (CustomerRating) auditDetail.getModelData();

		CustomerRating tempCustomerRating = null;
		if (customerRating.isWorkflow()) {
			tempCustomerRating = customerRatingDAO.getCustomerRatingByID(customerRating.getCustID(),
					customerRating.getCustRatingType(), "_Temp");
		}
		CustomerRating befCustomerRating = customerRatingDAO.getCustomerRatingByID(customerRating.getCustID(),
				customerRating.getCustRatingType(), "");

		CustomerRating oldCustomerRating = customerRating.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = customerRating.getLovDescCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];

		if (customerRating.isNewRecord()) { // for New record or new record into work flow

			if (!customerRating.isWorkflow()) {// With out Work flow only new records
				if (befCustomerRating != null) { // Record Already Exists in the table then error
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (customerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCustomerRating != null || tempCustomerRating != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerRating == null || tempCustomerRating != null) {
						auditDetail.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerRating.isWorkflow()) { // With out Work flow for update and delete

				if (befCustomerRating == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldCustomerRating != null
							&& !oldCustomerRating.getLastMntOn().equals(befCustomerRating.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {

				if (tempCustomerRating == null) { // if records not exists in the Work flow table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldCustomerRating != null
						&& !oldCustomerRating.getLastMntOn().equals(tempCustomerRating.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerRating.isWorkflow()) {
			auditDetail.setBefImage(befCustomerRating);
		}

		return auditDetail;
	}

	@Override
	public boolean doCheckBlackListedCustomer(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		Facility finDetail = (Facility) auditHeader.getAuditDetail().getModelData();
		long custId = finDetail.getCustID();

		if (custId != Long.MIN_VALUE && custId != 0) {
			String custCRCPR = customerDAO.getCustCRCPRById(custId, "");
			if (StringUtils.isNotBlank(custCRCPR)) {
				Date blackListDate = customerDAO.getCustBlackListedDate(custCRCPR, "");
				if (blackListDate != null) {
					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	@Override
	public Facility getTotalAmountsInUSDAndBHD(Facility facility) {
		logger.debug(Literal.ENTERING);
		BigDecimal amountBD = BigDecimal.ZERO;
		BigDecimal amountUSD = BigDecimal.ZERO;
		BigDecimal maturity = BigDecimal.ZERO;
		boolean securityClean = true;
		List<FacilityDetail> list = facility.getFacilityDetails();
		if (list != null && !list.isEmpty()) {
			for (FacilityDetail facilityDetail : list) {
				amountBD = amountBD.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(), "BHD",
						facilityDetail.getNewLimit()));
				amountUSD = amountUSD.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(), "USD",
						facilityDetail.getNewLimit()));
				if (new BigDecimal(facilityDetail.getTenorYear() + "." + facilityDetail.getTenorMonth())
						.compareTo(maturity) > 0) {
					maturity = new BigDecimal(facilityDetail.getTenorYear() + "." + facilityDetail.getTenorMonth());
				}
				if (securityClean) {
					if (!facilityDetail.isSecurityClean()) {
						securityClean = false;
					}
				}
			}
		}
		facility.setAmountBD(PennantApplicationUtil.formateAmount(amountBD, 3));
		facility.setAmountUSD(PennantApplicationUtil.formateAmount(amountUSD, 2));
		facility.setMaturity(maturity);
		facility.setSecurityCollateral(securityClean);
		logger.debug("Leaving");
		return facility;
	}

	@Override
	public String getActualLevelAprroval(Facility facility) {
		logger.debug(Literal.ENTERING);
		// Work Flow Condition Checking
		getTotalAmountsInUSDAndBHD(facility);

		if (facility.isSecurityCollateral()) {
			return FacilityConstants.FACILITY_LOA_CEO;
		}
		if (facility.getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {

			if (facility.getAmountBD().compareTo(FacilityConstants.BD_500_K) <= 0) {
				if (facility.getMaturity().compareTo(FacilityConstants.Tenor_5_Years) > 0) {
					return FacilityConstants.FACILITY_LOA_CREDIT_COMMITTEE;
				}
				return FacilityConstants.FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE;

			}
		}
		if (facility.getAmountUSD().compareTo(FacilityConstants.USD_5_M) <= 0) {
			if (facility.getMaturity().compareTo(FacilityConstants.Tenor_7_Years) > 0) {
				return FacilityConstants.FACILITY_LOA_EXECUTIVE_COMMITTEE;
			}
			return FacilityConstants.FACILITY_LOA_CREDIT_COMMITTEE;
		}

		if (facility.getAmountUSD().compareTo(FacilityConstants.USD_15_M) <= 0) {
			if (facility.getMaturity().compareTo(FacilityConstants.Tenor_10_Years) > 0) {
				return FacilityConstants.FACILITY_LOA_BOARD_OF_DIRECTORS;
			}
			return FacilityConstants.FACILITY_LOA_EXECUTIVE_COMMITTEE;
		} else {
			return FacilityConstants.FACILITY_LOA_BOARD_OF_DIRECTORS;
		}

	}

	@Override
	public boolean checkFirstTaskOwnerAccess(long loginUsrID) {
		return facilityDAO.checkFirstTaskOwnerAccess(loginUsrID);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFacilityDAO(FacilityDAO facilityDAO) {
		this.facilityDAO = facilityDAO;
	}

	public void setFacilityReferenceDetailDAO(FacilityReferenceDetailDAO facilityReferenceDetailDAO) {
		this.facilityReferenceDetailDAO = facilityReferenceDetailDAO;
	}

	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}

	public void setScoringSlabDAO(ScoringSlabDAO scoringSlabDAO) {
		this.scoringSlabDAO = scoringSlabDAO;
	}

	public void setScoringMetricsDAO(ScoringMetricsDAO scoringMetricsDAO) {
		this.scoringMetricsDAO = scoringMetricsDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public void setFinanceScoreHeaderDAO(FinanceScoreHeaderDAO financeScoreHeaderDAO) {
		this.financeScoreHeaderDAO = financeScoreHeaderDAO;
	}

	public void setCollateralDAO(CollateralDAO collateralDAO) {
		this.collateralDAO = collateralDAO;
	}

	public void setFacilityDetailDAO(FacilityDetailDAO facilityDetailDAO) {
		this.facilityDetailDAO = facilityDetailDAO;
	}

	public void setCustomerRatingDAO(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

}