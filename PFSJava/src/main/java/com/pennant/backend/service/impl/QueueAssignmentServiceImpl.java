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
 * 
 * FileName : CommonServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.UserActivityLog;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.QueueAssignmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class QueueAssignmentServiceImpl extends GenericService<QueueAssignment> implements
        QueueAssignmentService {
	private static final Logger logger = Logger.getLogger(QueueAssignmentServiceImpl.class);

	private QueueAssignmentDAO queueAssignmentDAO;
	private TaskOwnersDAO taskOwnersDAO;
	private UserActivityLogDAO userActivityLogDAO;
	private FinanceMainDAO financeMainDAO;
	private AuditHeaderDAO auditHeaderDAO;

	public QueueAssignmentServiceImpl() {
		super();
	}
	
	public QueueAssignmentDAO getQueueAssignmentDAO() {
		return queueAssignmentDAO;
	}

	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
		this.queueAssignmentDAO = queueAssignmentDAO;
	}

	public TaskOwnersDAO getTaskOwnersDAO() {
		return taskOwnersDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public UserActivityLogDAO getUserActivityLogDAO() {
		return userActivityLogDAO;
	}

	public void setUserActivityLogDAO(UserActivityLogDAO userActivityLogDAO) {
		this.userActivityLogDAO = userActivityLogDAO;
	}
	
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
	
	public AuditHeaderDAO getAuditHeaderDAO() {
	    return auditHeaderDAO;
    }

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
	    this.auditHeaderDAO = auditHeaderDAO;
    }

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		QueueAssignmentHeader queueAssignmentHeader = (QueueAssignmentHeader) auditHeader.getAuditDetail()
		        .getModelData();

		if (queueAssignmentHeader.isWorkflow()) {
			tableType = "_Temp";
		}
		
		if (queueAssignmentHeader.isNew()) {
			getQueueAssignmentDAO().saveHeader(queueAssignmentHeader, tableType);
		} else {
			getQueueAssignmentDAO().updateHeader(queueAssignmentHeader, tableType);
		}

		if (queueAssignmentHeader.getQueueAssignmentsList() != null && queueAssignmentHeader.getQueueAssignmentsList().size() > 0) {
			List<AuditDetail> details = queueAssignmentHeader.getAuditDetailMap().get("QueueAssignment");
			details = processingQueueAssignmentList(queueAssignmentHeader, details, tableType);
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetail(null);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}
	
	/**
	 * Method For Preparing List of AuditDetails for Queue Assignment
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingQueueAssignmentList(QueueAssignmentHeader queueAssignment, List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		List<UserActivityLog> logList = new ArrayList<UserActivityLog>();
		for (int i = 0; i < auditDetails.size(); i++) {

			QueueAssignment queueDetail = (QueueAssignment) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				queueDetail.setRoleCode("");
				queueDetail.setNextRoleCode("");
				queueDetail.setTaskId("");
				queueDetail.setNextTaskId("");
			}else {
				queueDetail.setWorkflowId(queueAssignment.getWorkflowId());
				queueDetail.setRoleCode(queueAssignment.getRoleCode());
				queueDetail.setTaskId(queueAssignment.getTaskId());
				queueDetail.setNextRoleCode(queueAssignment.getNextRoleCode());
				queueDetail.setNextTaskId(queueAssignment.getNextTaskId());				
			}

			if (queueDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (queueDetail.isNewRecord()) {
				saveRecord = true;
				if (queueDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					queueDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (queueDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					queueDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (queueDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					queueDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (queueDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (queueDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (queueDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (queueDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = queueDetail.getRecordType();
				recordStatus = queueDetail.getRecordStatus();
				queueDetail.setRecordType("");
				queueDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if(approveRec){
					if(queueDetail.getUserId() != 0){
						// If single user Insert one time and update remaining else Insert and Update counts in task_assignments table
						if(queueAssignment.isSingleUser()){
							if(i == 0){
								getQueueAssignmentDAO().save(queueDetail);
							}
						}else{
							getQueueAssignmentDAO().save(queueDetail);
						}
						if(queueAssignment.isManualAssign()){
							getQueueAssignmentDAO().updateUserCounts(queueDetail.getModule(), queueDetail.getUserRoleCode(), queueDetail.getUserId(), 
									queueDetail.getUserRoleCode(), queueDetail.getFromUserId(), false, false);
						}else {
							getQueueAssignmentDAO().updateUserCounts(queueDetail.getModule(), queueDetail.getUserRoleCode(), queueDetail.getUserId(), 
									queueDetail.getUserRoleCode(), queueDetail.getFromUserId(), false, true);
						}

						//update task_owners table
						TaskOwners owner = new TaskOwners();
						owner.setReference(queueDetail.getReference());
						owner.setRoleCode(queueDetail.getUserRoleCode());
						owner.setActualOwner(queueDetail.getFromUserId());
						owner.setCurrentOwner(queueDetail.getUserId());
						getTaskOwnersDAO().update(owner);

						// Insert into activity log
						
						UserActivityLog userActivityLog = new UserActivityLog();
						userActivityLog.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
						userActivityLog.setReference(queueDetail.getReference());
						userActivityLog.setFromUser(queueDetail.getLastMntBy());
						userActivityLog.setRoleCode(queueDetail.getUserRoleCode());
						if(queueAssignment.isManualAssign()){
							userActivityLog.setActivity(PennantConstants.RCD_STATUS_MANUALASSIGNED);
						}else {
							userActivityLog.setActivity(PennantConstants.RCD_STATUS_REASSIGNED);
						}
						if(queueDetail.getUserId() != 0) {
							userActivityLog.setToUser(queueDetail.getUserId());
						}
						userActivityLog.setNextRoleCode(queueDetail.getUserRoleCode());
						userActivityLog.setLogTime(queueDetail.getLastMntOn());
						userActivityLog.setReassignedTime(queueDetail.getLastMntOn());
						userActivityLog.setProcessed(false);
						logList.add(userActivityLog);
						if(i == auditDetails.size()-1){
							getUserActivityLogDAO().saveList(logList);
						}

						// Update financemain_temp
						List<String> refList = new ArrayList<String>();
						refList.add(queueDetail.getReference());
						getFinanceMainDAO().updateNextUserId(refList, String.valueOf(queueDetail.getFromUserId()), String.valueOf(queueDetail.getUserId()), 
								queueAssignment.isManualAssign());
					}
					getQueueAssignmentDAO().delete(queueDetail, "_Temp");
				}else{
					getQueueAssignmentDAO().save(queueDetail, type);
				}
			}

			if (updateRecord) {
				getQueueAssignmentDAO().update(queueDetail, type);
			}

			if (deleteRecord) {
				getQueueAssignmentDAO().delete(queueDetail, type);
			}

			if (approveRec) {
				queueDetail.setRecordType(rcdType);
				queueDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(queueDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

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
		logger.debug("Entering");
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		auditHeader.setAuditDetail(auditDetail);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		QueueAssignmentHeader queueAssignment = (QueueAssignmentHeader) auditHeader.getAuditDetail()
		        .getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method)
		        || "doReject".equals(method)) {
			if (queueAssignment.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (queueAssignment.getQueueAssignmentsList() != null && queueAssignment.getQueueAssignmentsList().size() > 0) {
			auditDetailMap
			        .put("QueueAssignment", setAssignmentAuditData(queueAssignment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("QueueAssignment"));
		}

		queueAssignment.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(queueAssignment);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param queueAssignment
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setAssignmentAuditData(QueueAssignmentHeader queueAssignment,
	        String auditTranType, String method) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		QueueAssignment object = new QueueAssignment();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < queueAssignment.getQueueAssignmentsList().size(); i++) {

			QueueAssignment queueDetail = queueAssignment.getQueueAssignmentsList().get(i);
			queueDetail.setWorkflowId(queueAssignment.getWorkflowId());

			boolean isRcdType = false;

			if (queueDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				queueDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (queueDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				queueDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (queueAssignment.isWorkflow()) {
					isRcdType = true;
                }
			} else if (queueDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				queueDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				queueDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (queueDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (queueDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || queueDetail.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			queueDetail.setRecordStatus(queueAssignment.getRecordStatus());
			queueDetail.setLoginDetails(queueAssignment.getUserDetails());
			queueDetail.setLastMntOn(queueAssignment.getLastMntOn());

			if (StringUtils.isNotEmpty(queueDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
				        queueDetail.getBefImage(), queueDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}


	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		QueueAssignmentHeader queueAssignmentHeader = (QueueAssignmentHeader) auditHeader.getAuditDetail()
		        .getModelData();

		getQueueAssignmentDAO().deleteHeader(queueAssignmentHeader);

		if (queueAssignmentHeader.getQueueAssignmentsList() != null && queueAssignmentHeader.getQueueAssignmentsList().size() > 0) {
			List<AuditDetail> details = queueAssignmentHeader.getAuditDetailMap().get("QueueAssignment");
			details = processingQueueAssignmentList(queueAssignmentHeader, details, "");
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetail(null);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {

		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		QueueAssignmentHeader queueAssignmentHeader = (QueueAssignmentHeader) auditHeader.getAuditDetail()
		        .getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getQueueAssignmentDAO().deleteHeader(queueAssignmentHeader);
		
		auditDetails.addAll(getListAuditDetails(listDeletion(queueAssignmentHeader, "_Temp",
		        auditHeader.getAuditTranType())));

		auditHeader.setAuditDetail(null);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	//Method for Deleting all records related to QueueAssignment in _Temp tables  depend on method type
	public List<AuditDetail> listDeletion(QueueAssignmentHeader queueAssignmentHeader, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (queueAssignmentHeader.getQueueAssignmentsList() != null && queueAssignmentHeader.getQueueAssignmentsList().size() > 0) {
			QueueAssignment object = new QueueAssignment();
			String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());
			for (int i = 0; i < queueAssignmentHeader.getQueueAssignmentsList().size(); i++) {
				QueueAssignment aQueueAssignment = queueAssignmentHeader.getQueueAssignmentsList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						aQueueAssignment.getBefImage(), aQueueAssignment));
				getQueueAssignmentDAO().delete(aQueueAssignment, "_Temp");
			}
		}
		logger.debug("Leaving");
		return auditList;
	}
	
	/**
	 * Common Method for QueueAssignments list validation
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

					rcdType = object.getClass().getMethod("getRecordType")
					        .invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					        || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						//check and change below line for Complete code
						Object befImg = object.getClass()
						        .getMethod("getBefImage", object.getClass().getClasses())
						        .invoke(object, object.getClass().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i))
						        .getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	@Override
	public QueueAssignmentHeader getFinances(QueueAssignmentHeader aQueueAssignmentHeader) {
		logger.debug("Entering");
		QueueAssignmentHeader  header = getQueueAssignmentDAO().isNewRequest(aQueueAssignmentHeader.getModule(), aQueueAssignmentHeader.getUserId(), 
				aQueueAssignmentHeader.getUserRoleCode(), aQueueAssignmentHeader.isManualAssign());
		if(header == null){
			aQueueAssignmentHeader.setNewRecord(true);
		}else {
			aQueueAssignmentHeader = header;
		}
		aQueueAssignmentHeader.setQueueAssignmentsList(getQueueAssignmentDAO().getFinances(aQueueAssignmentHeader.getUserId(), 
				aQueueAssignmentHeader.getUserRoleCode(), aQueueAssignmentHeader.isManualAssign()));
		if(aQueueAssignmentHeader.isManualAssign()){
			aQueueAssignmentHeader.setFromUserId(0);
		}else {
			aQueueAssignmentHeader.setFromUserId(Long.parseLong(aQueueAssignmentHeader.getUserId()));
		}
		logger.debug("Leaving");
		return aQueueAssignmentHeader;
	}

	@Override
    public boolean checkIfUserAlreadyAccessed(String finReferences, String selectedUser, String roleCode) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getTaskOwnersDAO().checkIfUserAlreadyAccessed(finReferences, selectedUser, roleCode);
		
    }
}
