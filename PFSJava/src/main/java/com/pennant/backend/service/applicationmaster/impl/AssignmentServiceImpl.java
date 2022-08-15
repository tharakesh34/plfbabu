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
 * * FileName : AssignmentServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentRateDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AssignmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Assignment</b>.<br>
 */
public class AssignmentServiceImpl extends GenericService<Assignment> implements AssignmentService {
	private static final Logger logger = LogManager.getLogger(AssignmentServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssignmentDAO assignmentDAO;
	private AssignmentRateDAO assignmentRateDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Assignment assignment = (Assignment) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (assignment.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (assignment.isNewRecord()) {
			assignment.setId(Long.parseLong(assignmentDAO.save(assignment, tableType)));
			auditHeader.getAuditDetail().setModelData(assignment);
			auditHeader.setAuditReference(String.valueOf(assignment.getId()));
		} else {
			assignmentDAO.update(assignment, tableType);
		}

		if (assignment.getAssignmentRateList() != null && assignment.getAssignmentRateList().size() > 0) {
			List<AuditDetail> details = assignment.getAuditDetailMap().get("AssignmentRate");
			details = processingAssignmentRateList(details, assignment.getId(), tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		Assignment assignment = (Assignment) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(deleteChilds(assignment, "", auditHeader.getAuditTranType()));
		assignmentDAO.delete(assignment, TableType.MAIN_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> deleteChilds(Assignment assignment, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (assignment.getAssignmentRateList() != null && assignment.getAssignmentRateList().size() > 0) {

			AssignmentRate assignmentRate = new AssignmentRate();
			String[] fields = PennantJavaUtil.getFieldDetails(assignmentRate, assignmentRate.getExcludeFields());

			for (AssignmentRate assignRate : assignment.getAssignmentRateList()) {
				auditList.add(new AuditDetail(auditTranType, auditList.size() + 1, fields[0], fields[1],
						assignRate.getBefImage(), assignRate));
			}
			assignmentRateDAO.deleteByAssignmentId(assignment.getId(), tableType);
			logger.debug(Literal.LEAVING);
		}
		return auditList;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Assignment assignment = new Assignment();
		BeanUtils.copyProperties((Assignment) auditHeader.getAuditDetail().getModelData(), assignment);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(assignment.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(assignmentDAO.getAssignment(assignment.getId(), ""));
		}

		if (assignment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(assignment, "", auditHeader.getAuditTranType()));
			assignmentDAO.delete(assignment, TableType.MAIN_TAB);
		} else {
			assignment.setRoleCode("");
			assignment.setNextRoleCode("");
			assignment.setTaskId("");
			assignment.setNextTaskId("");
			assignment.setWorkflowId(0);

			if (assignment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assignment.setRecordType("");
				assignmentDAO.save(assignment, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assignment.setRecordType("");
				assignmentDAO.update(assignment, TableType.MAIN_TAB);
			}
		}
		if (assignment.getAssignmentRateList() != null && assignment.getAssignmentRateList().size() > 0) {
			List<AuditDetail> details = assignment.getAuditDetailMap().get("AssignmentRate");
			details = processingAssignmentRateList(details, assignment.getId(), "");
			auditDetails.addAll(details);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditHeader.setAuditDetails(deleteChilds(assignment, "_Temp", auditHeader.getAuditTranType()));
		assignmentDAO.delete(assignment, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assignment);
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Assignment assignment = (Assignment) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, assignment.getBefImage(), assignment));
		auditHeader.setAuditDetails(deleteChilds(assignment, "_Temp", auditHeader.getAuditTranType()));
		assignmentDAO.delete(assignment, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Assignment assignment = (Assignment) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (assignment.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (assignment.getAssignmentRateList() != null && assignment.getAssignmentRateList().size() > 0) {
			auditDetailMap.put("AssignmentRate", setAssignmentRateListAuditData(assignment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("AssignmentRate"));
		}

		assignment.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(assignment);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.ENTERING);
		return auditHeader;
	}

	private List<AuditDetail> setAssignmentRateListAuditData(Assignment assignment, String auditTranType,
			String method) {

		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AssignmentRate assignmentRate = new AssignmentRate();
		String[] fields = PennantJavaUtil.getFieldDetails(assignmentRate, assignmentRate.getExcludeFields());

		for (int i = 0; i < assignment.getAssignmentRateList().size(); i++) {
			AssignmentRate assignRate = assignment.getAssignmentRateList().get(i);

			if (StringUtils.isEmpty(assignRate.getRecordType())) {
				continue;
			}

			assignRate.setAssignmentId(assignment.getId());
			assignRate.setWorkflowId(assignment.getWorkflowId());

			boolean isRcdType = false;

			if (assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				assignRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				assignRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (assignRate.isWorkflow()) {
					isRcdType = true;
				}
			} else if (assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				assignRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				assignRate.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| assignRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			assignRate.setRecordStatus(assignment.getRecordStatus());
			assignRate.setUserDetails(assignment.getUserDetails());
			assignRate.setLastMntOn(assignment.getLastMntOn());
			assignRate.setLastMntBy(assignment.getLastMntBy());

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], assignRate.getBefImage(), assignRate));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private List<AuditDetail> processingAssignmentRateList(List<AuditDetail> auditDetails, long id, String tableType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			AssignmentRate assignmentRate = (AssignmentRate) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType)) {
				approveRec = true;
				assignmentRate.setRoleCode("");
				assignmentRate.setNextRoleCode("");
				assignmentRate.setTaskId("");
				assignmentRate.setNextTaskId("");
			}

			assignmentRate.setAssignmentId(id);
			assignmentRate.setWorkflowId(0);

			if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (assignmentRate.isNewRecord()) {
				saveRecord = true;
				if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					assignmentRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					assignmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					assignmentRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (assignmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (assignmentRate.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = assignmentRate.getRecordType();
				recordStatus = assignmentRate.getRecordStatus();
				assignmentRate.setRecordType("");
				assignmentRate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				assignmentRateDAO.save(assignmentRate, tableType);
			}

			if (updateRecord) {
				assignmentRateDAO.update(assignmentRate, tableType);
			}

			if (deleteRecord) {
				assignmentRateDAO.delete(assignmentRate, tableType);
			}

			if (approveRec) {
				assignmentRate.setRecordType(rcdType);
				assignmentRate.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(assignmentRate);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	@Override
	public Assignment getAssignment(long id) {
		Assignment assignment = assignmentDAO.getAssignment(id, "_View");
		if (assignment != null) {
			assignment.setAssignmentRateList(assignmentRateDAO.getAssignmentRatesByAssignmentId(id, "_View"));
		}
		return assignment;
	}

	public Assignment getApprovedAssignment(long id) {
		return assignmentDAO.getAssignment(id, "_AView");
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<String> getFinTypes(long dealId) {
		return assignmentDAO.getFinTypes(dealId);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public void setAssignmentRateDAO(AssignmentRateDAO assignmentRateDAO) {
		this.assignmentRateDAO = assignmentRateDAO;
	}

}