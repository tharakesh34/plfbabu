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
 * FileName    		:  FinancePurposeDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FinCovenantTypeServiceImpl extends GenericService<FinCovenantType> implements FinCovenantTypeService {
	private static final Logger logger = LogManager.getLogger(FinCovenantTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinCovenantTypeDAO finCovenantTypesDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDetailsService customerDetailsService;
	private DocumentTypeDAO documentTypeDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDocumentService customerDocumentService;

	public FinCovenantTypeServiceImpl() {
		super();
	}

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
	 * @return the finCovenantTypesDAO
	 */
	public FinCovenantTypeDAO getFinCovenantTypeDAO() {
		return finCovenantTypesDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypesDAO) {
		this.finCovenantTypesDAO = finCovenantTypesDAO;
	}

	@Override
	public List<FinCovenantType> getFinCovenantTypeById(String id, String type, boolean isEnquiry) {
		logger.debug("Entering");
		List<FinCovenantType> finCovenantTypes = getFinCovenantTypeDAO().getFinCovenantTypeByFinRef(id, type,
				isEnquiry);
		logger.debug("Leaving");
		return finCovenantTypes;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinCovenantType> finCovenantTypes, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, false));

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processFinAdvancePaymentDetails(List<FinCovenantType> finCovenantTypes, String tableType,
			String auditTranType, boolean isApproveRcd) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {

			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinCovenantType finPayment : finCovenantTypes) {
				if (StringUtils.isEmpty(StringUtils.trimToEmpty(finPayment.getRecordType()))) {
					continue;
				}
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType)
						|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finPayment.setRoleCode("");
					finPayment.setNextRoleCode("");
					finPayment.setTaskId("");
					finPayment.setNextTaskId("");
				}

				finPayment.setWorkflowId(0);
				if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finPayment.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(finPayment.getRecordType())) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finPayment.getRecordType())) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finPayment.getRecordType())) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),
						(PennantConstants.RECORD_TYPE_NEW))) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),
						(PennantConstants.RECORD_TYPE_UPD))) {
					updateRecord = true;
				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),
						(PennantConstants.RECORD_TYPE_DEL))) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finPayment.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = finPayment.getRecordType();
					recordStatus = finPayment.getRecordStatus();
					finPayment.setRecordType("");
					finPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getFinCovenantTypeDAO().save(finPayment, tableType);
				}

				if (updateRecord) {
					getFinCovenantTypeDAO().update(finPayment, tableType);
				}

				if (deleteRecord) {
					getFinCovenantTypeDAO().delete(finPayment, tableType);
				}

				if (approveRec) {
					finPayment.setRecordType(rcdType);
					finPayment.setRecordStatus(recordStatus);
				}

				String[] fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finPayment.getBefImage(),
						finPayment));
				i++;
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, true));

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			int auditSeq = 1;
			for (FinCovenantType finPayment : finCovenantTypes) {
				getFinCovenantTypeDAO().delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finPayment.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAdvancePaymentAuditDetail(List<FinCovenantType> finCovenantTypes, String auditTranType,
			String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;
		for (FinCovenantType finAdvancePay : finCovenantTypes) {

			if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finAdvancePay.getRecordStatus())
					.equals(PennantConstants.RCD_STATUS_SAVED)) {
				if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finAdvancePay.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_DEL)) {
					finAdvancePay.setWorkflowId(0);
					finAdvancePay.setNewRecord(true);
				} else {
					finAdvancePay.setWorkflowId(0);
				}
			} else {
				finAdvancePay.setWorkflowId(workFlowId);
			}
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(finAdvancePay.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (StringUtils.equalsIgnoreCase(finAdvancePay.getRecordType(), PennantConstants.RCD_ADD)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finAdvancePay.getRecordType())) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finAdvancePay.getRecordType())) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finAdvancePay.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finAdvancePay, finAdvancePay.getExcludeFields());
			if (StringUtils.isNotEmpty(finAdvancePay.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finAdvancePay.getBefImage(), finAdvancePay));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<FinCovenantType> finCovenantTypes, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		return doValidation(finCovenantTypes, workflowId, method, auditTranType, usrLanguage);
	}

	private List<AuditDetail> doValidation(List<FinCovenantType> finCovenantTypes, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(finCovenantTypes, auditTranType,
					method, workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage);
			}
			auditDetails.addAll(advancePayAuditDetails);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validateAdvancePayment(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinCovenantType covenantType = (FinCovenantType) auditDetail.getModelData();
		FinCovenantType tempFinAdvancePay = null;

		if (covenantType.isWorkflow()) {
			tempFinAdvancePay = getFinCovenantTypeDAO().getFinCovenantTypeById(covenantType, "_Temp");
		}
		FinCovenantType befFinAdvancePay = getFinCovenantTypeDAO().getFinCovenantTypeById(covenantType, "");
		FinCovenantType oldFinAdvancePay = covenantType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = covenantType.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (covenantType.isNew()) { // for New record or new record into work flow

			if (!covenantType.isWorkflow()) {// With out Work flow only new records  
				if (befFinAdvancePay != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinAdvancePay == null || tempFinAdvancePay != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!covenantType.isWorkflow()) { // With out Work flow for update and delete

				if (befFinAdvancePay == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinAdvancePay != null
							&& !oldFinAdvancePay.getLastMntOn().equals(befFinAdvancePay.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempFinAdvancePay == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinAdvancePay != null && oldFinAdvancePay != null
						&& !oldFinAdvancePay.getLastMntOn().equals(tempFinAdvancePay.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !covenantType.isWorkflow()) {
			auditDetail.setBefImage(befFinAdvancePay);
		}
		return auditDetail;
	}

	@Override
	public FinCovenantType getFinCovenantTypeById(String reference, String covenType, String type) {
		return getFinCovenantTypeDAO().getCovenantTypeById(reference, covenType, type);
	}

	@Override
	public FinanceDetail getFinanceDetailById(String id, String type, String userRole, String moduleDefiner,
			String eventCodeRef) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(id);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(id, type, false));
		scheduleData.setFinanceType(
				getFinanceTypeDAO().getFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_AView"));

		//Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(id, type, false));

		//Finance Customer Details			
		if (scheduleData.getFinanceMain().getCustID() != 0
				&& scheduleData.getFinanceMain().getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(getCustomerDetailsService()
					.getCustomerDetailsById(scheduleData.getFinanceMain().getCustID(), true, "_View"));
		}

		//Fin Covenant Type
		List<FinCovenantType> finCovenantType = getFinCovenantTypeDAO().getFinCovenantDocTypeByFinRef(id, "_View",
				false);
		financeDetail.setCovenantTypeList(finCovenantType);

		return financeDetail;
	}

	public List<ErrorDetail> doCovenantValidation(FinanceDetail financeDetail, boolean isUpdate) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		boolean referenceExitsinLQ = financeMainDAO.isFinReferenceExists(finReference, "_Temp", false);
		String finEvent = "";
		if (referenceExitsinLQ) {
			finEvent = FinServiceEvent.ORG;
		} else {
			finEvent = FinServiceEvent.COVENANTS;
		}
		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
				financeMain.getFinType(), finEvent, PennantConstants.WORFLOW_MODULE_FINANCE);
		String roles = financeWorkFlow.getLovDescWorkFlowRolesName();

		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		Set<String> set = new HashSet<>();
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		for (FinCovenantType finCovenantType : covenantTypeList) {
			String covenantType = finCovenantType.getCovenantType();
			if (StringUtils.isBlank(covenantType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "covenantType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				boolean isExists = false;
				if (set.contains(covenantType)) {
					isExists = true;
				} else {
					set.add(covenantType);
				}
				if (isExists) {
					String[] valueParm = new String[1];
					valueParm[0] = "covenantType: " + covenantType;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
					return errorDetails;
				}
			}

			DocumentType documentTypeByCode = documentTypeDAO.getDocumentTypeById(covenantType, "_View");
			if (documentTypeByCode == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "covenantType";
				valueParm[1] = covenantType;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
				return errorDetails;
			} else {
				CustomerDocument custdocuments = customerDocumentService
						.getApprovedCustomerDocumentById(financeMain.getCustID(), covenantType);

				if (custdocuments != null) {
					if (StringUtils.equals(covenantType, custdocuments.getCustDocCategory())) {
						String[] valueParm = new String[2];
						valueParm[0] = "CustomerDocument" + covenantType;
						valueParm[1] = covenantType;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41018", valueParm)));
						return errorDetails;
					}
				}

				DocumentDetails documentDetails = documentDetailsDAO.getDocumentDetails(finReference, covenantType,
						DocumentCategories.FINANCE.getKey(), "_View");
				if (documentDetails != null) {
					if (StringUtils.equals(covenantType, documentDetails.getDocCategory())) {
						String[] valueParm = new String[2];
						valueParm[0] = covenantType;
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("CVN002", valueParm)));
						return errorDetails;
					}
				}
			}

			FinCovenantType fincovenantType = finCovenantTypesDAO.getCovenantTypeById(finReference, covenantType,
					"_View");
			if (!isUpdate && fincovenantType != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan Reference:" + finReference;
				valueParm[1] = "covenantType:" + covenantType;
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("41015", valueParm)));
				return errorDetails;
			}

			if (isUpdate && fincovenantType == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Loan Reference:" + finReference;
				valueParm[1] = "covenantType:" + finCovenantType.getCovenantType();
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isNotBlank(finCovenantType.getMandRole())) {
				if ((finCovenantType.isAlwPostpone() || finCovenantType.isAlwOtc() || finCovenantType.isAlwWaiver())) {
					String[] valueParm = new String[2];
					valueParm[0] = "alwPostpone or alwOtc or alwWaiver ";
					valueParm[1] = "mandRole";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("API002", valueParm)));
					return errorDetails;
				}
			}
			if (finCovenantType.isAlwWaiver()) {
				if (StringUtils.isNotBlank(finCovenantType.getMandRole()) || finCovenantType.isAlwPostpone()
						|| finCovenantType.isAlwOtc()) {
					String[] valueParm = new String[2];
					valueParm[0] = "alwPostpone or alwOtc or mandRole ";
					valueParm[1] = "alwWaiver";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("API002", valueParm)));
					return errorDetails;
				}
			}
			if (finCovenantType.isAlwPostpone() && finCovenantType.isAlwOtc()) {
				String[] valueParm = new String[2];
				valueParm[0] = "alwPostpone";
				valueParm[1] = "alwOtc";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30566", valueParm)));
				return errorDetails;
			}

			if ((!finCovenantType.isAlwPostpone() && !finCovenantType.isAlwOtc() && !finCovenantType.isAlwWaiver())) {
				if (StringUtils.isBlank(finCovenantType.getMandRole())) {
					String[] valueParm = new String[1];
					valueParm[0] = "mandRole";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				boolean isRoleExts = false;
				if (StringUtils.contains(roles, ";")) {
					String[] promoIdsList = roles.split(";");
					for (String workFlowRoles : promoIdsList) {
						if (StringUtils.equalsIgnoreCase(workFlowRoles, finCovenantType.getMandRole())) {
							isRoleExts = true;
							break;
						}
					}
				}

				if (!isRoleExts) {
					String[] valueParm = new String[2];
					valueParm[0] = "mandRole";
					valueParm[1] = finCovenantType.getMandRole();
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));
					return errorDetails;
				}
			} else {
				finCovenantType.setMandRole("");
			}

			if (finCovenantType.isAlwPostpone()) {
				if (finCovenantType.getReceivableDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "receivableDate";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}

				if (finCovenantType.getReceivableDate() != null) {
					java.util.Date appDate = SysParamUtil.getAppDate();
					Date allowedDate = DateUtility.addDays(appDate,
							+SysParamUtil.getValueAsInt("FUTUREDAYS_COV_RECEIVED_DATE"));
					if (DateUtility.compare(finCovenantType.getReceivableDate(), appDate) == -1) {
						String[] valueParm = new String[2];
						valueParm[0] = "receivableDate";
						valueParm[1] = String.valueOf(appDate);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", "", valueParm)));
					} else if (DateUtility.compare(finCovenantType.getReceivableDate(), allowedDate) == 1) {
						String[] valueParm = new String[2];
						valueParm[0] = "receivableDate";
						valueParm[1] = String.valueOf(allowedDate);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm)));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	@Override
	public List<FinCovenantType> getFinCovenantDocTypeByFinRef(String id, String type, boolean isEnquiry) {
		return getFinCovenantTypeDAO().getFinCovenantDocTypeByFinRef(id, type, isEnquiry);
	}

	@Override
	public List<DocumentType> getPddOtcList() {
		return getFinCovenantTypeDAO().getPddOtcList();
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinCovenantTypeDAO getFinCovenantTypesDAO() {
		return finCovenantTypesDAO;
	}

	public void setFinCovenantTypesDAO(FinCovenantTypeDAO finCovenantTypesDAO) {
		this.finCovenantTypesDAO = finCovenantTypesDAO;
	}

	public DocumentTypeDAO getDocumentTypeDAO() {
		return documentTypeDAO;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CustomerDocumentService getCustomerDocumentService() {
		return customerDocumentService;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

}