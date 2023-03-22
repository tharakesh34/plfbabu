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
 * * FileName : TanAssignmentServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-09-2020 * *
 * Modified Date : 08-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tandetails.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.tandetails.TanAssignmentDAO;
import com.pennant.backend.dao.tandetails.TanDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.tandetails.TanAssignmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class TanAssignmentServiceImpl extends GenericService<TanAssignment> implements TanAssignmentService {
	private static final Logger logger = LogManager.getLogger(TanAssignmentServiceImpl.class);

	private TanAssignmentDAO tanAssignmentDAO;
	private TanDetailDAO tanDetailDAO;

	public List<AuditDetail> processingTanAssignemts(List<AuditDetail> auditDetails, TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			TanAssignment tanAssignment = (TanAssignment) auditDetails.get(i).getModelData();
			TanDetail tanDetail = tanAssignment.getTanDetail();

			if (StringUtils.isBlank(tanAssignment.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				tanAssignment.setVersion(tanAssignment.getVersion() + 1);
				tanAssignment.setRoleCode("");
				tanAssignment.setNextRoleCode("");
				tanAssignment.setTaskId("");
				tanAssignment.setNextTaskId("");

				tanDetail.setVersion(tanAssignment.getVersion());
				tanDetail.setRoleCode("");
				tanDetail.setNextRoleCode("");
				tanDetail.setTaskId("");
				tanDetail.setNextTaskId("");
			}

			tanAssignment.setWorkflowId(0);
			tanDetail.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(tanAssignment.getRecordType())) {
				deleteRecord = true;
			} else if (tanAssignment.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(tanAssignment.getRecordType())) {
					tanAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					tanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(tanAssignment.getRecordType())) {
					tanAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					tanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(tanAssignment.getRecordType())) {
					tanAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					tanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(tanAssignment.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(tanAssignment.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(tanAssignment.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (tanAssignment.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = tanAssignment.getRecordType();
				recordStatus = tanAssignment.getRecordStatus();
				tanAssignment.setRecordType("");
				tanAssignment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

				tanDetail.setRecordType("");
				tanDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (approveRec
					&& StringUtils.equals(tanAssignment.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
				tanAssignment.setRecordType("");
				tanDetail.setRecordType("");
			}

			String holderName = tanAssignment.getTanDetail().getTanHolderName();
			if (saveRecord) {
				String tanNumber = tanDetail.getTanNumber();
				if (isTanNumberAvailable(tanNumber, null, TableType.MAIN_TAB)) {
					long tanID = tanDetailDAO.getTanIdByTanNumber(tanNumber, TableType.MAIN_TAB);
					if (isTanNumberAvailable(tanNumber, holderName, TableType.MAIN_TAB)) {
						tanAssignment.setTanID(tanID);
						tanAssignmentDAO.save(tanAssignment, type);
					} else {
						if (type.equals(TableType.TEMP_TAB)) {
							tanDetail.setId(tanID);
							if (!isTanNumberAvailable(tanNumber, holderName, TableType.TEMP_TAB)) {
								tanAssignment.setTanID(tanDetailDAO.save(tanDetail, type));
							}
							tanAssignmentDAO.save(tanAssignment, type);
						} else if (type.equals(TableType.MAIN_TAB)) {
							tanDetailDAO.update(tanDetail, type);
							tanAssignment.setTanID(tanDetail.getId());
							tanAssignmentDAO.save(tanAssignment, type);

							tanAssignmentDAO.delete(tanAssignment, TableType.TEMP_TAB);
							tanDetailDAO.delete(tanDetail, TableType.TEMP_TAB);
						}
					}

				} else {
					tanAssignment.setTanID(tanDetailDAO.save(tanDetail, type));
					tanAssignmentDAO.save(tanAssignment, type);

					if (type.equals(TableType.MAIN_TAB)) {
						tanAssignmentDAO.delete(tanAssignment, TableType.TEMP_TAB);
						tanDetailDAO.delete(tanDetail, TableType.TEMP_TAB);
					}
				}
			}

			if (updateRecord) {
				if (isTanNumberAvailable(tanDetail.getTanNumber(), holderName, TableType.MAIN_TAB)) {
					tanDetailDAO.update(tanDetail, type);
					tanAssignment.setTanID(tanDetail.getId());
					tanAssignmentDAO.update(tanAssignment, type);
				} else {
					tanDetailDAO.update(tanDetail, type);
					tanAssignment.setTanID(tanDetail.getId());
					tanAssignmentDAO.update(tanAssignment, type);
				}
			}

			if (deleteRecord) {
				tanAssignmentDAO.delete(tanAssignment, type);
				if (TableType.TEMP_TAB.equals(type)) {
					tanDetailDAO.delete(tanDetail, type);
				} else {
					int count = tanAssignmentDAO.isTanNumberAvailable(tanAssignment.getTanID());
					if (count == 0) {
						tanDetailDAO.delete(tanDetail, type);
					}
				}
			}

			if (approveRec) {
				tanAssignment.setRecordType(rcdType);
				tanAssignment.setRecordStatus(recordStatus);

				tanDetail.setRecordType(rcdType);
				tanDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(tanAssignment);
		}
		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public List<TanAssignment> getTanDetails(long custId, String finReference) {
		logger.debug(Literal.ENTERING);

		List<TanAssignment> tanAssignmentList = new ArrayList<>();
		TableType tableType;

		if (StringUtils.isNotBlank(finReference)) {
			tableType = TableType.TEMP_TAB;
		} else {
			tableType = TableType.MAIN_TAB;
		}
		if (TableType.TEMP_TAB.equals(tableType)) {
			tanAssignmentList.addAll(tanAssignmentDAO.getTanAssignments(custId, null, TableType.MAIN_TAB));
			tanAssignmentList.addAll(tanAssignmentDAO.getTanAssignments(custId, finReference, tableType));
		}

		if (CollectionUtils.isEmpty(tanAssignmentList)) {
			tanAssignmentList = tanAssignmentDAO.getTanAssignments(custId, null, TableType.MAIN_TAB);
		}

		logger.debug(Literal.LEAVING);
		return tanAssignmentList;
	}

	public List<TanAssignment> getTanDetailsByFinReference(long custId, String finReference) {
		logger.debug(Literal.ENTERING);

		List<TanAssignment> tanAssignmentList = new ArrayList<>();
		tanAssignmentList.addAll(tanAssignmentDAO.getTanAssignmentsByCustId(custId, finReference, TableType.MAIN_TAB));
		tanAssignmentList.addAll(tanAssignmentDAO.getTanAssignmentsByFinReference(finReference, TableType.VIEW));
		for (TanAssignment tanAssignment : tanAssignmentList) {
			TanDetail tanDetail = tanDetailDAO.getTanDetailList(tanAssignment.getTanID(), TableType.VIEW);
			tanAssignment.setTanDetail(tanDetail);
		}

		logger.debug(Literal.LEAVING);
		return tanAssignmentList;
	}

	public List<AuditDetail> validate(FinanceDetail detail, long workflowId, String method, String auditTranType,
			String usrLanguage) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
		return doValidation(detail, workflowId, method, auditTranType, usrLanguage);
	}

	public List<AuditDetail> doValidation(FinanceDetail detail, long workflowId, String method, String auditTranType,
			String usrLanguage) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = setTanAssignmentAuditData(detail, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validation(auditDetail, usrLanguage, method);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		TanAssignment tanAssignment = (TanAssignment) auditDetail.getModelData();
		TanDetail tanDetail = tanAssignment.getTanDetail();
		boolean tanNumberAvailable = isTanNumberAvailable(tanDetail.getTanNumber(), null, TableType.MAIN_TAB);

		if (tanAssignment.isNewRecord() && !tanNumberAvailable
				&& tanAssignmentDAO.isDuplicateKey(tanAssignment.getId(), tanAssignment.getFinReference(),
						tanAssignment.getCustID(), tanAssignment.getTanID(),
						tanAssignment.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_TanID") + ": " + tanAssignment.getId();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (tanAssignment.isNewRecord() && !tanNumberAvailable && tanDetailDAO.isDuplicateKey(tanDetail.getId(),
				tanDetail.getTanNumber(), tanDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_TanNumber") + ": " + tanDetail.getTanNumber();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		String recordStatus = tanAssignment.getRecordStatus();
		if (!(PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus))) {

			List<String> finReferences = tanAssignmentDAO.getFinReferenceByTanNumber(tanAssignment.getFinReference(),
					tanDetail.getTanNumber(), "_Temp");

			if (tanNumberAvailable && CollectionUtils.isNotEmpty(finReferences)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_TanNumber") + ": " + tanDetail.getTanNumber();
				parameters[1] = PennantJavaUtil.getLabel("label_FinReference") + ": " + finReferences.get(0);
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "99030", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !tanAssignment.isWorkflow()) {
			auditDetail.setBefImage(tanAssignment);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public List<AuditDetail> setTanAssignmentAuditData(FinanceDetail detail, String auditTranType, String method,
			long workflowId) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		TanAssignment object = new TanAssignment();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < detail.getTanAssignments().size(); i++) {
			TanAssignment aTanAssignment = detail.getTanAssignments().get(i);

			if (StringUtils.isEmpty(aTanAssignment.getRecordType())) {
				continue;
			}

			FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
			aTanAssignment.setWorkflowId(financeMain.getWorkflowId());
			aTanAssignment.setTanID(aTanAssignment.getTanDetail().getId());

			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(aTanAssignment.getRecordType())) {
				aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(aTanAssignment.getRecordType())) {
				aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (aTanAssignment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(aTanAssignment.getRecordType())) {
				aTanAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				aTanAssignment.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(aTanAssignment.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(aTanAssignment.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(aTanAssignment.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			TanDetail tanDetail = aTanAssignment.getTanDetail();

			if (StringUtils.isEmpty(tanDetail.getRecordType())) {
				continue;
			}

			tanDetail.setWorkflowId(financeMain.getWorkflowId());
			isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(tanDetail.getRecordType())) {
				tanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(tanDetail.getRecordType())) {
				tanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (aTanAssignment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(tanDetail.getRecordType())) {
				tanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				tanDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(tanDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(tanDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(tanDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			tanDetail.setRecordStatus(aTanAssignment.getRecordStatus());
			tanDetail.setUserDetails(aTanAssignment.getUserDetails());
			tanDetail.setLastMntOn(aTanAssignment.getLastMntOn());
			tanDetail.setLastMntBy(aTanAssignment.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], aTanAssignment.getBefImage(),
					aTanAssignment));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public void delete(List<TanAssignment> tanAssignment, TableType tableType) {
		logger.debug(Literal.ENTERING);

		for (TanAssignment aTanAssignment : tanAssignment) {
			try {
				tanAssignmentDAO.delete(aTanAssignment, tableType);
				tanDetailDAO.delete(aTanAssignment.getTanDetail(), tableType);
			} catch (DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<TanAssignment> getTanDetailsByReference(String finReference) {
		logger.debug(Literal.ENTERING);

		List<TanAssignment> tanAssignment = tanAssignmentDAO.getTanDetailsByReference(finReference);

		logger.debug(Literal.LEAVING);
		return tanAssignment;
	}

	@Override
	public long getIdByFinReferenceAndTanId(String finReference, long tanID) {
		logger.debug(Literal.ENTERING);

		long id = tanAssignmentDAO.getIdByFinReferenceAndTanId(finReference, tanID, TableType.VIEW);

		logger.debug(Literal.LEAVING);
		return id;
	}

	public boolean isTanNumberAvailable(String tanNumber, String tanHolderName, TableType type) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
		return tanDetailDAO.isTanNumberAvailable(tanNumber, tanHolderName, type);
	}

	@Override
	public List<TanAssignment> getTanNumberList(long custId) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
		return tanAssignmentDAO.getTanNumberList(custId);
	}

	public void setTanAssignmentDAO(TanAssignmentDAO tanAssignmentDAO) {
		this.tanAssignmentDAO = tanAssignmentDAO;
	}

	public void setTanDetailDAO(TanDetailDAO tanDetailDAO) {
		this.tanDetailDAO = tanDetailDAO;
	}

}
