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
 * FileName    		:  AssignmentDealServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2018    														*
 *                                                                  						*
 * Modified Date    :  12-09-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.AssignmentDeal;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.applicationmaster.AssignmentDealLoanType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AssignmentDealService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssignmentDeal</b>.<br>
 */
public class AssignmentDealServiceImpl extends GenericService<AssignmentDeal> implements AssignmentDealService {
	private static final Logger logger = LogManager.getLogger(AssignmentDealServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private AssignmentDAO assignmentDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * AssignmentDeal/AssignmentDeal_Temp by using AssignmentDealDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using AssignmentDealDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtAssignmentDeal by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		AssignmentDeal assignmentDeal = (AssignmentDeal) auditHeader.getAuditDetail().getModelData();

		if (assignmentDeal.isWorkflow()) {
			tableType = "_Temp";
		}

		if (assignmentDeal.isNewRecord()) {
			assignmentDeal.setId(Long.valueOf(assignmentDealDAO.save(assignmentDeal, tableType)));
		} else {
			assignmentDealDAO.update(assignmentDeal, tableType);
		}

		// DocumentDetails
		if (assignmentDeal.getAssignmentDealLoanType() != null
				&& assignmentDeal.getAssignmentDealLoanType().size() > 0) {

			assignmentDealDAO.deleteLoanTypeList(assignmentDeal.getAssignmentDealLoanType(), tableType);
			List<AuditDetail> details = assignmentDeal.getAuditDetailMap().get("loanTypeDetails");
			details = processLoanTypes(assignmentDeal, details, tableType);
			auditDetails.addAll(details);
		}
		// Check List
		if (assignmentDeal.getAssignmentDealExcludedFee() != null
				&& assignmentDeal.getAssignmentDealExcludedFee().size() > 0) {

			assignmentDealDAO.deleteExcFeeList(assignmentDeal.getAssignmentDealExcludedFee(), tableType);

			List<AuditDetail> details = assignmentDeal.getAuditDetailMap().get("excludedFeeDetails");
			details = processExcludedFeeDetails(assignmentDeal, details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private List<AuditDetail> processLoanTypes(AssignmentDeal assignmentDeal, List<AuditDetail> auditDetails,
			String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			AssignmentDealLoanType assignmentDealLoanType = (AssignmentDealLoanType) auditDetails.get(i).getModelData();
			assignmentDealLoanType.setDealId(assignmentDeal.getId());
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				assignmentDealLoanType.setRoleCode("");
				assignmentDealLoanType.setNextRoleCode("");
				assignmentDealLoanType.setTaskId("");
				assignmentDealLoanType.setNextTaskId("");
			}
			if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (assignmentDealLoanType.isNewRecord()) {
				saveRecord = true;
				if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (assignmentDealLoanType.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = assignmentDealLoanType.getRecordType();
				recordStatus = assignmentDealLoanType.getRecordStatus();
				assignmentDealLoanType.setRecordType("");
				assignmentDealLoanType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (true) {
				assignmentDealDAO.deleteLoanType(assignmentDealLoanType, type);
			}
			if (true) {
				assignmentDealDAO.saveLoanType(assignmentDealLoanType, type);
			}
			if (updateRecord) {
				//assignmentDealDAO.updateLoanType(assignmentDealLoanType, type);
			}
			if (approveRec) {
				assignmentDealLoanType.setRecordType(rcdType);
				assignmentDealLoanType.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(assignmentDealLoanType);

		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> processExcludedFeeDetails(AssignmentDeal assignmentDeal, List<AuditDetail> auditDetails,
			String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			AssignmentDealExcludedFee assignmentDealExcFee = (AssignmentDealExcludedFee) auditDetails.get(i)
					.getModelData();
			assignmentDealExcFee.setDealId(assignmentDeal.getId());
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				assignmentDealExcFee.setRoleCode("");
				assignmentDealExcFee.setNextRoleCode("");
				assignmentDealExcFee.setTaskId("");
				assignmentDealExcFee.setNextTaskId("");
			}
			if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (assignmentDealExcFee.isNewRecord()) {
				saveRecord = true;
				if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					assignmentDealExcFee.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					assignmentDealExcFee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					assignmentDealExcFee.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (assignmentDealExcFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (assignmentDealExcFee.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = assignmentDealExcFee.getRecordType();
				recordStatus = assignmentDealExcFee.getRecordStatus();
				assignmentDealExcFee.setRecordType("");
				assignmentDealExcFee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (true) {
				assignmentDealDAO.deleteExcludedFee(assignmentDealExcFee, type);
			}
			if (true) {
				assignmentDealDAO.saveExcludedFee(assignmentDealExcFee, type);
			}
			if (updateRecord) {
				assignmentDealDAO.updateExcludedFee(assignmentDealExcFee, type);
			}
			if (approveRec) {
				assignmentDealExcFee.setRecordType(rcdType);
				assignmentDealExcFee.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(assignmentDealExcFee);

		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * AssignmentDeal by using AssignmentDealDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtAssignmentDeal by using auditHeaderDAO.addAudit(auditHeader)
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

		AssignmentDeal assignmentDeal = (AssignmentDeal) auditHeader.getAuditDetail().getModelData();
		assignmentDealDAO.delete(assignmentDeal, "");

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getAssignmentDeal fetch the details by using AssignmentDealDAO's getAssignmentDealById method.
	 * 
	 * @param id
	 *            id of the AssignmentDeal.
	 * @return AssignmentDeal
	 */
	@Override
	public AssignmentDeal getAssignmentDeal(long id) {
		AssignmentDeal assignmentDeal = assignmentDealDAO.getAssignmentDeal(id, "_View");
		if (assignmentDeal != null) {

			assignmentDeal.setAssignmentDealLoanType(
					(assignmentDealDAO.getAssignmentDealLoanTypeList(assignmentDeal.getId(), "_view")));

			assignmentDeal.setAssignmentDealExcludedFee(
					(assignmentDealDAO.getAssignmentDealExcludedFeeList(assignmentDeal.getId(), "_view")));

		}
		return assignmentDeal;

	}

	/**
	 * getApprovedAssignmentDealById fetch the details by using AssignmentDealDAO's getAssignmentDealById method . with
	 * parameter id and type as blank. it fetches the approved records from the AssignmentDeal.
	 * 
	 * @param id
	 *            id of the AssignmentDeal. (String)
	 * @return AssignmentDeal
	 */
	public AssignmentDeal getApprovedAssignmentDeal(long id) {
		return assignmentDealDAO.getAssignmentDeal(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using assignmentDealDAO.delete with
	 * parameters assignmentDeal,"" b) NEW Add new record in to main table by using assignmentDealDAO.save with
	 * parameters assignmentDeal,"" c) EDIT Update record in the main table by using assignmentDealDAO.update with
	 * parameters assignmentDeal,"" 3) Delete the record from the workFlow table by using assignmentDealDAO.delete with
	 * parameters assignmentDeal,"_Temp" 4) Audit the record in to AuditHeader and AdtAssignmentDeal by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtAssignmentDeal by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		AssignmentDeal assignmentDeal = new AssignmentDeal();
		BeanUtils.copyProperties((AssignmentDeal) aAuditHeader.getAuditDetail().getModelData(), assignmentDeal);

		AuditHeader auditHeader = new AuditHeader();
		BeanUtils.copyProperties(aAuditHeader, auditHeader);

		//delete
		assignmentDealDAO.deleteLoanTypeList(assignmentDeal.getAssignmentDealLoanType(), "");
		assignmentDealDAO.deleteExcFeeList(assignmentDeal.getAssignmentDealExcludedFee(), "");

		if (assignmentDeal.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// List
			auditDetails.addAll(deleteChilds(assignmentDeal, "", tranType));
			assignmentDealDAO.delete(assignmentDeal, "");

		} else {
			assignmentDeal.setRoleCode("");
			assignmentDeal.setNextRoleCode("");
			assignmentDeal.setTaskId("");
			assignmentDeal.setNextTaskId("");
			assignmentDeal.setWorkflowId(0);

			if (assignmentDeal.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				assignmentDeal.setRecordType("");
				assignmentDealDAO.save(assignmentDeal, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assignmentDeal.setRecordType("");
				assignmentDealDAO.update(assignmentDeal, "");
			}
			// List
			if (assignmentDeal.getAssignmentDealLoanType() != null
					&& assignmentDeal.getAssignmentDealLoanType().size() > 0) {
				List<AuditDetail> details = assignmentDeal.getAuditDetailMap().get("loanTypeDetails");
				details = processLoanTypes(assignmentDeal, details, "");
				auditDetails.addAll(details);
			}
			if (assignmentDeal.getAssignmentDealExcludedFee() != null
					&& assignmentDeal.getAssignmentDealExcludedFee().size() > 0) {
				List<AuditDetail> details = assignmentDeal.getAuditDetailMap().get("excludedFeeDetails");
				details = processExcludedFeeDetails(assignmentDeal, details, "");
				auditDetails.addAll(details);
			}

		}

		// List
		auditHeader.setAuditDetails(deleteChilds(assignmentDeal, "_Temp", auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentDeal(), assignmentDeal.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
				assignmentDeal.getBefImage(), assignmentDeal));
		auditHeaderDAO.addAudit(auditHeader);

		assignmentDealDAO.delete(assignmentDeal, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assignmentDeal);
		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (AuditDetail auditDetail : list) {
				String transType = "";
				String rcdType = "";
				Object object = auditDetail.getModelData();

				if (object instanceof AssignmentDeal) {
					// DocumentDetails
					AssignmentDeal assignmentDeal = (AssignmentDeal) object;
					rcdType = assignmentDeal.getRecordType();
				} else if (object instanceof AssignmentDealLoanType) {
					// Check List
					AssignmentDealLoanType assignmentDealLoanType = (AssignmentDealLoanType) object;
					rcdType = assignmentDealLoanType.getRecordType();
				} else if (object instanceof AssignmentDealExcludedFee) {
					// Collateral
					AssignmentDealExcludedFee assignmentDealExcludedFee = (AssignmentDealExcludedFee) object;
					rcdType = assignmentDealExcludedFee.getRecordType();
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

	public List<AuditDetail> deleteChilds(AssignmentDeal assignmentDeal, String tableType, String tranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (assignmentDeal.getAssignmentDealLoanType() != null
				&& !assignmentDeal.getAssignmentDealLoanType().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentDealLoanType(),
					new AssignmentDealLoanType().getExcludeFields());
			for (int i = 0; i < assignmentDeal.getAssignmentDealLoanType().size(); i++) {
				AssignmentDealLoanType assignmentDealLoanType = assignmentDeal.getAssignmentDealLoanType().get(i);
				if (StringUtils.isNotEmpty(assignmentDealLoanType.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(tranType, i + 1, fields[0], fields[1],
							assignmentDealLoanType.getBefImage(), assignmentDealLoanType));
				}
			}
			assignmentDealDAO.deleteLoanTypeList(assignmentDeal.getAssignmentDealLoanType(), tableType);
		}

		if (assignmentDeal.getAssignmentDealExcludedFee() != null
				&& !assignmentDeal.getAssignmentDealExcludedFee().isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentDealExcludedFee(),
					new AssignmentDealExcludedFee().getExcludeFields());
			for (int i = 0; i < assignmentDeal.getAssignmentDealExcludedFee().size(); i++) {
				AssignmentDealExcludedFee assignmentDealexcludedFee = assignmentDeal.getAssignmentDealExcludedFee()
						.get(i);
				if (StringUtils.isNotEmpty(assignmentDealexcludedFee.getRecordType())
						|| StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(tranType, i + 1, fields[0], fields[1],
							assignmentDealexcludedFee.getBefImage(), assignmentDealexcludedFee));
				}
			}
			assignmentDealDAO.deleteExcFeeList(assignmentDeal.getAssignmentDealExcludedFee(), tableType);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using assignmentDealDAO.delete with parameters assignmentDeal,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtAssignmentDeal by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssignmentDeal assignmentDeal = (AssignmentDeal) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(deleteChilds(assignmentDeal, "_Temp", PennantConstants.TRAN_WF));
		assignmentDealDAO.delete(assignmentDeal, "_Temp");

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
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		AssignmentDeal assignmentDeal = (AssignmentDeal) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (assignmentDeal.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Commitment Flag details
		if (assignmentDeal.getAssignmentDealLoanType() != null
				&& assignmentDeal.getAssignmentDealLoanType().size() > 0) {
			auditDetailMap.put("loanTypeDetails",
					setAssignmentDealLoanTypeListAuditData(assignmentDeal, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("loanTypeDetails"));
		}

		if (assignmentDeal.getAssignmentDealExcludedFee() != null
				&& assignmentDeal.getAssignmentDealExcludedFee().size() > 0) {
			auditDetailMap.put("excludedFeeDetails",
					setAssignmentDealExcludedFeeListAuditData(assignmentDeal, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("excludedFeeDetails"));
		}
		assignmentDeal.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(assignmentDeal);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
		return auditHeader;
	}

	private List<AuditDetail> setAssignmentDealLoanTypeListAuditData(AssignmentDeal assignmentDeal,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AssignmentDealLoanType assignmentDealLoanType1 = new AssignmentDealLoanType();

		String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentDealLoanType(),
				assignmentDealLoanType1.getExcludeFields());

		for (int i = 0; i < assignmentDeal.getAssignmentDealLoanType().size(); i++) {
			AssignmentDealLoanType assignmentDealLoanType = assignmentDeal.getAssignmentDealLoanType().get(i);

			if (StringUtils.isEmpty(assignmentDealLoanType.getRecordType())) {
				continue;
			}

			assignmentDealLoanType.setDealId(assignmentDeal.getId());
			assignmentDealLoanType.setWorkflowId(assignmentDeal.getWorkflowId());

			boolean isRcdType = false;

			if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (assignmentDeal.isWorkflow()) {
					isRcdType = true;
				}
			} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				assignmentDealLoanType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				assignmentDealLoanType.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| assignmentDealLoanType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			assignmentDealLoanType.setRecordStatus(assignmentDeal.getRecordStatus());
			assignmentDealLoanType.setUserDetails(assignmentDeal.getUserDetails());
			assignmentDealLoanType.setLastMntOn(assignmentDeal.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					assignmentDealLoanType.getBefImage(), assignmentDealLoanType));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> setAssignmentDealExcludedFeeListAuditData(AssignmentDeal assignmentDeal,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AssignmentDealExcludedFee assignmentDealExcludedFee1 = new AssignmentDealExcludedFee();

		/*
		 * 
		 * todo
		 * 
		 */
		String[] fields = PennantJavaUtil.getFieldDetails(assignmentDealExcludedFee1,
				assignmentDealExcludedFee1.getExcludeFields());

		for (int i = 0; i < assignmentDeal.getAssignmentDealExcludedFee().size(); i++) {
			AssignmentDealExcludedFee assignmentDealExcludedFee = assignmentDeal.getAssignmentDealExcludedFee().get(i);

			if (StringUtils.isEmpty(assignmentDealExcludedFee.getRecordType())) {
				continue;
			}

			assignmentDealExcludedFee.setDealId(assignmentDeal.getId());
			assignmentDealExcludedFee.setWorkflowId(assignmentDeal.getWorkflowId());

			boolean isRcdType = false;

			if (assignmentDealExcludedFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				assignmentDealExcludedFee.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (assignmentDealExcludedFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				assignmentDealExcludedFee.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (assignmentDeal.isWorkflow()) {
					isRcdType = true;
				}
			} else if (assignmentDealExcludedFee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				assignmentDealExcludedFee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				assignmentDealExcludedFee.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (assignmentDealExcludedFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (assignmentDealExcludedFee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| assignmentDealExcludedFee.getRecordType()
								.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			assignmentDealExcludedFee.setRecordStatus(assignmentDeal.getRecordStatus());
			assignmentDealExcludedFee.setUserDetails(assignmentDeal.getUserDetails());
			assignmentDealExcludedFee.setLastMntOn(assignmentDeal.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					assignmentDealExcludedFee.getBefImage(), assignmentDealExcludedFee));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from assignmentDealDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		AssignmentDeal assignmentDeal = (AssignmentDeal) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + assignmentDeal.getCode();

		// Check the unique keys.
		if (assignmentDeal.isNewRecord() && assignmentDealDAO.isDuplicateKey(assignmentDeal.getId(), assignmentDeal.getCode(),
				assignmentDeal.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (StringUtils.trimToEmpty(assignmentDeal.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = assignmentDAO.getMappedAssignments(assignmentDeal.getId());
			if (count != 0) {
				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null)));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

}