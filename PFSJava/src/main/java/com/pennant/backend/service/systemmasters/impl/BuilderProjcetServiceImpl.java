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
 * FileName    		:  BuilderProjcetServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.systemmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.systemmasters.BuilderProjcetDAO;
import com.pennant.backend.dao.systemmasters.ProjectUnitsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.BuilderProjcetService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>BuilderProjcet</b>.<br>
 */
public class BuilderProjcetServiceImpl extends GenericService<BuilderProjcet> implements BuilderProjcetService {
	private static final Logger logger = Logger.getLogger(BuilderProjcetServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BuilderProjcetDAO builderProjcetDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentManagerDAO documentManagerDAO;
	private ProjectUnitsDAO projectUnitsDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BuilderProject/BuilderProject_Temp by using BuilderProjectDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using BuilderProjectDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtBuilderProject by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (builderProjcet.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (builderProjcet.isNew()) {
			builderProjcet.setId(Long.parseLong(getBuilderProjcetDAO().save(builderProjcet, tableType)));
			auditHeader.getAuditDetail().setModelData(builderProjcet);
			auditHeader.setAuditReference(String.valueOf(builderProjcet.getId()));
		} else {
			getBuilderProjcetDAO().update(builderProjcet, tableType);
		}
		// Project Unit Details
		if (builderProjcet.getProjectUnits() != null) {
			List<AuditDetail> details = builderProjcet.getAuditDetailMap().get("ProjectUnits");
			details = processingProjectUnits(details, builderProjcet, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		//Document Details
		List<DocumentDetails> documentsList = builderProjcet.getDocumentDetails();
		if (documentsList != null && documentsList.size() > 0) {
			List<AuditDetail> details = builderProjcet.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, builderProjcet, tableType.getSuffix());
			auditDetails.addAll(details);
		}
		String[] fields = PennantJavaUtil.getFieldDetails(new BuilderProjcet(), builderProjcet.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				builderProjcet.getBefImage(), builderProjcet));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BuilderProject by using BuilderProjectDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtBuilderProject by using auditHeaderDAO.addAudit(auditHeader)
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

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();
		getBuilderProjcetDAO().delete(builderProjcet, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBuilderProject fetch the details by using BuilderProjectDAO's getBuilderProjectById method.
	 * 
	 * @param id
	 *            id of the BuilderProjcet.
	 * @return BuilderProject
	 */
	@Override
	public BuilderProjcet getBuilderProjcet(long id) {
		BuilderProjcet builderProjcet = new BuilderProjcet();
		//Project Details
		builderProjcet = getBuilderProjcetDAO().getBuilderProjcet(id, "_View");

		//getting the project unit details
		builderProjcet.setProjectUnits(projectUnitsDAO.getProjectUnitsByProjectID(id, "_View"));

		//getting the project document details
		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
				PennantConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_View");
		if (builderProjcet.getDocumentDetails() != null && !builderProjcet.getDocumentDetails().isEmpty()) {
			builderProjcet.getDocumentDetails().addAll(documentList);
		} else {
			builderProjcet.setDocumentDetails(documentList);
		}

		return builderProjcet;
	}

	/**
	 * getApprovedBuilderProjectById fetch the details by using BuilderProjectDAO's getBuilderProjectById method . with
	 * parameter id and type as blank. it fetches the approved records from the BuilderProject.
	 * 
	 * @param id
	 *            id of the BuilderProjcet. (String)
	 * @return BuilderProject
	 */
	@Override
	public BuilderProjcet getApprovedBuilderProjcet(long id) {

		BuilderProjcet builderProjcet = new BuilderProjcet();
		//Project Details
		builderProjcet = getBuilderProjcetDAO().getBuilderProjcet(id, "_AView");

		//getting the project unit details
		builderProjcet.setProjectUnits(projectUnitsDAO.getProjectUnitsByProjectID(id, "_AView"));

		//getting the project document details
		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(String.valueOf(id),
				PennantConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_AView");
		if (builderProjcet.getDocumentDetails() != null && !builderProjcet.getDocumentDetails().isEmpty()) {
			builderProjcet.getDocumentDetails().addAll(documentList);
		} else {
			builderProjcet.setDocumentDetails(documentList);
		}
		return builderProjcet;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getBuilderProjcetDAO().delete with
	 * parameters builderProjcet,"" b) NEW Add new record in to main table by using getBuilderProjcetDAO().save with
	 * parameters builderProjcet,"" c) EDIT Update record in the main table by using getBuilderProjcetDAO().update with
	 * parameters builderProjcet,"" 3) Delete the record from the workFlow table by using getBuilderProjcetDAO().delete
	 * with parameters builderProjcet,"_Temp" 4) Audit the record in to AuditHeader and AdtBuilderProject by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtBuilderProject by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		BuilderProjcet builderProjcet = new BuilderProjcet();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), builderProjcet);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(builderProjcet.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(builderProjcetDAO.getBuilderProjcet(builderProjcet.getId(), ""));
		}

		if (builderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(builderProjcet, TableType.MAIN_TAB.getSuffix(), tranType));
			getBuilderProjcetDAO().delete(builderProjcet, TableType.MAIN_TAB);
		} else {
			builderProjcet.setRoleCode("");
			builderProjcet.setNextRoleCode("");
			builderProjcet.setTaskId("");
			builderProjcet.setNextTaskId("");
			builderProjcet.setWorkflowId(0);

			if (builderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				builderProjcet.setRecordType("");
				getBuilderProjcetDAO().save(builderProjcet, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				builderProjcet.setRecordType("");
				getBuilderProjcetDAO().update(builderProjcet, TableType.MAIN_TAB);
			}
			// Project Unit Details
			if (builderProjcet.getProjectUnits() != null) {
				List<AuditDetail> details = builderProjcet.getAuditDetailMap().get("ProjectUnits");
				details = processingProjectUnits(details, builderProjcet, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}

			//Document Details
			List<DocumentDetails> documentsList = builderProjcet.getDocumentDetails();
			if (documentsList != null && documentsList.size() > 0) {
				List<AuditDetail> details = builderProjcet.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, builderProjcet, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}
		}

		if (!builderProjcet.isNewRecord()) {
			//deleting data from _temp tables while Approve
			auditHeader.setAuditDetails(
					listDeletion(builderProjcet, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
			builderProjcetDAO.delete(builderProjcet, TableType.TEMP_TAB);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(builderProjcet);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getBuilderProjcetDAO().delete with parameters builderProjcet,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtBuilderProject by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		BuilderProjcet builderProject = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails
				.addAll(listDeletion(builderProject, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		builderProjcetDAO.delete(builderProject, TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new BuilderProjcet(), builderProject.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				builderProject.getBefImage(), builderProject));

		auditHeader.setAuditDetails(auditDetails);
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

		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (builderProjcet.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		////Project Unit details
		if (builderProjcet.getProjectUnits() != null) {
			auditDetailMap.put("ProjectUnits", setProjectUnitsAuditData(builderProjcet, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ProjectUnits"));
		}

		//Project Document Details
		if (builderProjcet.getDocumentDetails() != null && builderProjcet.getDocumentDetails().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(builderProjcet, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		builderProjcet.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(builderProjcet);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(BuilderProjcet builderProjcet, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
		for (int i = 0; i < builderProjcet.getDocumentDetails().size(); i++) {
			DocumentDetails documentDetails = builderProjcet.getDocumentDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(builderProjcet.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (builderProjcet.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
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

			documentDetails.setRecordStatus(builderProjcet.getRecordStatus());
			documentDetails.setUserDetails(builderProjcet.getUserDetails());
			documentDetails.setLastMntOn(builderProjcet.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setProjectUnitsAuditData(BuilderProjcet builderProjcet, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		ProjectUnits projectUnits = new ProjectUnits();
		String[] fields = PennantJavaUtil.getFieldDetails(projectUnits, projectUnits.getExcludeFields());
		for (int i = 0; i < builderProjcet.getProjectUnits().size(); i++) {
			ProjectUnits projectUnit = builderProjcet.getProjectUnits().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(projectUnit.getRecordType()))) {
				continue;
			}

			projectUnit.setWorkflowId(builderProjcet.getWorkflowId());
			boolean isRcdType = false;

			if (projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				projectUnit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				projectUnit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (builderProjcet.isWorkflow()) {
					isRcdType = true;
				}
			} else if (projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				projectUnit.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				projectUnit.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| projectUnit.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			projectUnit.setRecordStatus(builderProjcet.getRecordStatus());
			projectUnit.setUserDetails(builderProjcet.getUserDetails());
			projectUnit.setLastMntOn(builderProjcet.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], projectUnit.getBefImage(),
					projectUnit));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for ProjectUnits Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingProjectUnits(List<AuditDetail> auditDetails, BuilderProjcet builderProjcet,
			String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			ProjectUnits projectUnit = (ProjectUnits) auditDetails.get(i).getModelData();
			projectUnit.setProjectId(builderProjcet.getId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			boolean isTempRecord = false;
			if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				projectUnit.setRoleCode("");
				projectUnit.setNextRoleCode("");
				projectUnit.setTaskId("");
				projectUnit.setNextTaskId("");
			}
			projectUnit.setLastMntBy(builderProjcet.getLastMntBy());
			projectUnit.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(projectUnit.getRecordType())) {
				deleteRecord = true;
				isTempRecord = true;
			} else if (projectUnit.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(projectUnit.getRecordType())) {
					projectUnit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(projectUnit.getRecordType())) {
					projectUnit.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(projectUnit.getRecordType())) {
					projectUnit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(projectUnit.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(projectUnit.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(projectUnit.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (projectUnit.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = projectUnit.getRecordType();
				recordStatus = projectUnit.getRecordStatus();
				projectUnit.setRecordType("");
				projectUnit.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				projectUnitsDAO.save(projectUnit, type);
			}

			if (updateRecord) {
				projectUnitsDAO.update(projectUnit, type);
			}

			if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
				if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					projectUnitsDAO.delete(projectUnit, type);
				}
			}

			if (approveRec) {
				projectUnit.setRecordType(rcdType);
				projectUnit.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(projectUnit);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param collateralSetup
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails,
			BuilderProjcet builderProject, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			documentDetails.setReferenceId(String.valueOf(builderProject.getId()));
			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;
				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(builderProject.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
					approveRec = true;
				}

				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(documentDetails.getRecordType())) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
					updateRecord = true;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNew()) {
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
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(String.valueOf(builderProject.getId()));
					}
					documentDetails.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					if (documentDetails.getDocId() < 0) {
						documentDetails.setDocId(Long.MIN_VALUE);
					}
					documentDetailsDAO.save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					documentDetailsDAO.update(documentDetails, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						documentDetailsDAO.delete(documentDetails, type);
					}
				}

				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Method for Deleting all records related to project in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(BuilderProjcet builderProjcet, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Project Unit Details.
		List<AuditDetail> projectUnitDetails = builderProjcet.getAuditDetailMap().get("ProjectUnits");
		if (projectUnitDetails != null && projectUnitDetails.size() > 0) {
			ProjectUnits projectUnit = new ProjectUnits();
			String[] fields = PennantJavaUtil.getFieldDetails(projectUnit, projectUnit.getExcludeFields());
			for (int i = 0; i < projectUnitDetails.size(); i++) {
				projectUnit = (ProjectUnits) projectUnitDetails.get(i).getModelData();
				projectUnit.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], projectUnit.getBefImage(),
						projectUnit));
				projectUnitsDAO.delete(projectUnit, tableType);
			}
		}

		// Document Details. 
		List<AuditDetail> documentDetails = builderProjcet.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			DocumentDetails documentDetail = null;
			List<DocumentDetails> docList = new ArrayList<DocumentDetails>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				documentDetail = (DocumentDetails) documentDetails.get(i).getModelData();
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				docList.add(documentDetail);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(),
						documentDetail));
			}
			documentDetailsDAO.deleteList(docList, tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
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
	 * @return the builderProjcetDAO
	 */
	public BuilderProjcetDAO getBuilderProjcetDAO() {
		return builderProjcetDAO;
	}

	/**
	 * @param builderProjcetDAO
	 *            the builderProjcetDAO to set
	 */
	public void setBuilderProjcetDAO(BuilderProjcetDAO builderProjcetDAO) {
		this.builderProjcetDAO = builderProjcetDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setProjectUnitsDAO(ProjectUnitsDAO projectUnitsDAO) {
		this.projectUnitsDAO = projectUnitsDAO;
	}

}