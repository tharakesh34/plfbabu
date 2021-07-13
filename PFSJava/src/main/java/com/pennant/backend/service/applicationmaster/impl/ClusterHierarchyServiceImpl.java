/**
` * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  ClusterHierarcheyServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2018    														*
 *                                                                  						*
 * Modified Date    :  21-11-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2018       PENNANT	                 0.1                                            * 
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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ClusterHierarchyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ClusterHierarchyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ClusterHierarchey</b>.<br>
 */
public class ClusterHierarchyServiceImpl extends GenericService<ClusterHierarchy> implements ClusterHierarchyService {
	private static final Logger logger = LogManager.getLogger(ClusterHierarchyServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ClusterHierarchyDAO clusterHierarchyDAO;

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
	 * @param clusterHierarchyDAO
	 *            the clusterHierarchyDAO to set
	 */

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ClusterHierarchey/ClusterHierarchey_Temp by using ClusterHierarcheyDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using ClusterHierarcheyDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtClusterHierarchey by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ClusterHierarchy clusterHierarchey = (ClusterHierarchy) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (clusterHierarchey.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		clusterHierarchyDAO.delete(clusterHierarchey, tableType);
		clusterHierarchyDAO.save(clusterHierarchey, tableType);
		auditHeader.getAuditDetail().setModelData(clusterHierarchey);
		auditHeader.setAuditReference(String.valueOf(clusterHierarchey.getEntity()));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	public void setClusterHierarchyDAO(ClusterHierarchyDAO clusterHierarchyDAO) {
		this.clusterHierarchyDAO = clusterHierarchyDAO;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ClusterHierarchey by using ClusterHierarcheyDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtClusterHierarchey by using auditHeaderDAO.addAudit(auditHeader)
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

		ClusterHierarchy clusterHierarchey = (ClusterHierarchy) auditHeader.getAuditDetail().getModelData();
		clusterHierarchyDAO.delete(clusterHierarchey, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getClusterHierarchey fetch the details by using ClusterHierarcheyDAO's getClusterHierarcheyById method.
	 * 
	 * @param entity
	 *            entity of the ClusterHierarchey.
	 * @return ClusterHierarchey
	 */
	@Override
	public ClusterHierarchy getClusterHierarcheybyId(String entity) {
		return clusterHierarchyDAO.getClusterHierarcheybyId(entity, "_LView");
	}

	/**
	 * getClusterHierarchey fetch the details by using ClusterHierarcheyDAO's getClusterHierarcheyById method.
	 * 
	 * @param entity
	 *            entity of the ClusterHierarchey.
	 * @param clusterType
	 *            clusterType of the ClusterHierarchey.
	 * @return ClusterHierarchey
	 */
	@Override
	public ClusterHierarchy getClusterHierarchey(String entity) {
		return clusterHierarchyDAO.getClusterHierarchey(entity, "_LView");
	}

	/**
	 * getClusterHierarchey fetch the details by using ClusterHierarcheyDAO's getClusterHierarchey method.
	 * 
	 * @param entity
	 *            entity of the ClusterHierarchey.
	 * @param clusterType
	 *            clusterType of the ClusterHierarchey.
	 * @return ClusterHierarchey
	 */
	@Override
	public List<ClusterHierarchy> getClusterHierarcheyList(String entity) {
		return clusterHierarchyDAO.getClusterHierarcheyList(entity, "_View");
	}

	/**
	 * getApprovedClusterHierarcheyById fetch the details by using ClusterHierarcheyDAO's getClusterHierarcheyById
	 * method . with parameter id and type as blank. it fetches the approved records from the ClusterHierarchey.
	 * 
	 * @param entity
	 *            entity of the ClusterHierarchey.
	 * @param clusterType
	 *            clusterType of the ClusterHierarchey. (String)
	 * @return ClusterHierarchey
	 */
	public ClusterHierarchy getApprovedClusterHierarchey(String entity) {
		return clusterHierarchyDAO.getClusterHierarchey(entity, "_LView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using clusterHierarchyDAO.delete with
	 * parameters clusterHierarchey,"" b) NEW Add new record in to main table by using clusterHierarchyDAO.save with
	 * parameters clusterHierarchey,"" c) EDIT Update record in the main table by using clusterHierarchyDAO.update with
	 * parameters clusterHierarchey,"" 3) Delete the record from the workFlow table by using clusterHierarchyDAO.delete
	 * with parameters clusterHierarchey,"_Temp" 4) Audit the record in to AuditHeader and AdtClusterHierarchey by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtClusterHierarchey
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ClusterHierarchy clusterHierarchey = new ClusterHierarchy();
		BeanUtils.copyProperties((ClusterHierarchy) auditHeader.getAuditDetail().getModelData(), clusterHierarchey);

		clusterHierarchyDAO.delete(clusterHierarchey, TableType.TEMP_TAB);

		String recordType = clusterHierarchey.getRecordType();
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
			ClusterHierarchy ch = clusterHierarchyDAO.getClusterHierarchey(clusterHierarchey.getEntity(), "_lview");
			auditHeader.getAuditDetail().setBefImage(ch);
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(recordType)) {
			tranType = PennantConstants.TRAN_DEL;
			clusterHierarchyDAO.delete(clusterHierarchey, TableType.MAIN_TAB);
		} else {
			clusterHierarchey.setRoleCode("");
			clusterHierarchey.setNextRoleCode("");
			clusterHierarchey.setTaskId("");
			clusterHierarchey.setNextTaskId("");
			clusterHierarchey.setWorkflowId(0);

			for (ClusterHierarchy aClusterhierarchy : clusterHierarchey.getClusterTypes()) {
				aClusterhierarchy.setTaskId("");
				aClusterhierarchy.setNextTaskId("");
				aClusterhierarchy.setRoleCode("");
				aClusterhierarchy.setNextRoleCode("");
				aClusterhierarchy.setRecordType("");
				aClusterhierarchy.setWorkflowId(0);
			}

			if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
				tranType = PennantConstants.TRAN_ADD;
				clusterHierarchey.setRecordType("");
				clusterHierarchyDAO.save(clusterHierarchey, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				clusterHierarchey.setRecordType("");
				clusterHierarchyDAO.update(clusterHierarchey, TableType.MAIN_TAB);
			}

		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(clusterHierarchey);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using clusterHierarchyDAO.delete with parameters clusterHierarchey,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtClusterHierarchey by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ClusterHierarchy clusterHierarchey = (ClusterHierarchy) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		clusterHierarchyDAO.delete(clusterHierarchey, TableType.TEMP_TAB);

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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from clusterHierarchyDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ClusterHierarchy clusterhierarchy = (ClusterHierarchy) auditDetail.getModelData();
		for (ClusterHierarchy ch : clusterhierarchy.getClusterTypes()) {
			// Check the unique keys.
			if (!ch.isNew()) {
				continue;
			}

			if (clusterHierarchyDAO.isDuplicateKey(ch.getEntity(), ch.getClusterType(),
					clusterhierarchy.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_Entity") + ": " + ch.getEntity();
				parameters[1] = PennantJavaUtil.getLabel("label_ClusterType") + ": " + ch.getClusterType();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
				auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			} else if (clusterHierarchyDAO.isDuplicateKey(ch.getEntity(), ch.getSeqOrder(),
					clusterhierarchy.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_Entity") + ": " + ch.getEntity();
				parameters[1] = PennantJavaUtil.getLabel("label_Cluster_SeqOrder") + ": " + ch.getSeqOrder();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
				auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;

	}

}