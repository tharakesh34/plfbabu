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
 * * FileName : FinancePurposeDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 *
 * * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
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
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class FinCovenantTypeServiceImpl extends GenericService<FinCovenantType> implements FinCovenantTypeService {
	private static final Logger logger = LogManager.getLogger(FinCovenantTypeServiceImpl.class);

	private FinCovenantTypeDAO finCovenantTypeDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private DocumentTypeDAO documentTypeDAO;
	private FinanceWorkFlowService financeWorkFlowService;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private CustomerDataService customerDataService;

	public FinCovenantTypeServiceImpl() {
		super();
	}

	@Override
	public List<FinCovenantType> getFinCovenantTypeById(String id, String type, boolean isEnquiry) {
		logger.debug(Literal.ENTERING);
		List<FinCovenantType> finCovenantTypes = finCovenantTypeDAO.getFinCovenantTypeByFinRef(id, type, isEnquiry);
		logger.debug(Literal.LEAVING);
		return finCovenantTypes;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinCovenantType> finCovenantTypes, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, false));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processFinAdvancePaymentDetails(List<FinCovenantType> finCovenantTypes, String tableType,
			String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

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
					} else if (finPayment.isNewRecord()) {
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
					finCovenantTypeDAO.save(finPayment, tableType);
				}

				if (updateRecord) {
					finCovenantTypeDAO.update(finPayment, tableType);
				}

				if (deleteRecord) {
					finCovenantTypeDAO.delete(finPayment, tableType);
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
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, true));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			int auditSeq = 1;
			for (FinCovenantType finPayment : finCovenantTypes) {
				finCovenantTypeDAO.delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finPayment.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getAdvancePaymentAuditDetail(List<FinCovenantType> finCovenantTypes, String auditTranType,
			String method, long workFlowId) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(List<FinCovenantType> finCovenantTypes, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		return doValidation(finCovenantTypes, workflowId, method, auditTranType, usrLanguage);
	}

	private List<AuditDetail> doValidation(List<FinCovenantType> finCovenantTypes, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(finCovenantTypes, auditTranType,
					method, workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage);
			}
			auditDetails.addAll(advancePayAuditDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validateAdvancePayment(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinCovenantType covenantType = (FinCovenantType) auditDetail.getModelData();
		FinCovenantType tempFinAdvancePay = null;

		if (covenantType.isWorkflow()) {
			tempFinAdvancePay = finCovenantTypeDAO.getFinCovenantTypeById(covenantType, "_Temp");
		}
		FinCovenantType befFinAdvancePay = finCovenantTypeDAO.getFinCovenantTypeById(covenantType, "");
		FinCovenantType oldFinAdvancePay = covenantType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = covenantType.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (covenantType.isNewRecord()) { // for New record or new record into work flow

			if (!covenantType.isWorkflow()) {// With out Work flow only new records
				if (befFinAdvancePay != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay != null) { // if records already exists in the
																					// main table
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
		return finCovenantTypeDAO.getCovenantTypeById(reference, covenType, type);
	}

	@Override
	public FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String moduleDefiner,
			String eventCodeRef) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, type, false);

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		String finType = fm.getFinType();

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		// Finance Customer Details

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDataService.getCustomerDetailsbyID(custID, true, "_View"));
		}

		List<FinCovenantType> covenantTypes = finCovenantTypeDAO.getFinCovenantDocTypeByFinRef(finReference, "_View",
				false);
		fd.setCovenantTypeList(covenantTypes);

		return fd;
	}

	public List<ErrorDetail> doCovenantValidation(FinanceDetail fd, boolean isUpdate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		String finReference = fm.getFinReference();
		Long finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
		String finEvent = "";

		if (finID == null) {
			finEvent = FinServiceEvent.ORG;
		} else {
			finEvent = FinServiceEvent.COVENANTS;
		}

		finID = fm.getFinID();
		String finType = fm.getFinType();

		FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(finType, finEvent,
				PennantConstants.WORFLOW_MODULE_FINANCE);
		String roles = financeWorkFlow.getLovDescWorkFlowRolesName();

		List<FinCovenantType> covenantTypeList = fd.getCovenantTypeList();
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
				CustomerDocument custdocuments = customerDocumentDAO.getCustomerDocumentById(fm.getCustID(),
						covenantType, "_AView");

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

			FinCovenantType fincovenantType = finCovenantTypeDAO.getCovenantTypeById(finReference, covenantType,
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
					Date allowedDate = DateUtil.addDays(appDate,
							+SysParamUtil.getValueAsInt("FUTUREDAYS_COV_RECEIVED_DATE"));
					if (DateUtil.compare(finCovenantType.getReceivableDate(), appDate) == -1) {
						String[] valueParm = new String[2];
						valueParm[0] = "receivableDate";
						valueParm[1] = String.valueOf(appDate);
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("65030", "", valueParm)));
					} else if (DateUtil.compare(finCovenantType.getReceivableDate(), allowedDate) == 1) {
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
		return finCovenantTypeDAO.getFinCovenantDocTypeByFinRef(id, type, isEnquiry);
	}

	@Override
	public List<DocumentType> getPddOtcList() {
		return finCovenantTypeDAO.getPddOtcList();
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}