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
 * FileName    		:  ClusterServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.applicationmaster.ClusterDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Cluster</b>.<br>
 */
public class ClusterServiceImpl extends GenericService<Cluster> implements ClusterService {
	private static final Logger logger = LogManager.getLogger(ClusterServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ClusterDAO clusterDAO;

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
	 * @return the clusterDAO
	 */
	public ClusterDAO getClusterDAO() {
		return clusterDAO;
	}

	/**
	 * @param clusterDAO
	 *            the clusterDAO to set
	 */
	public void setClusterDAO(ClusterDAO clusterDAO) {
		this.clusterDAO = clusterDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Clusters/Clusters_Temp by using
	 * ClustersDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * ClustersDAO's update method 3) Audit the record in to AuditHeader and AdtClusters by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		Cluster cluster = (Cluster) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (cluster.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (cluster.isNewRecord()) {
			cluster.setId(Long.parseLong(getClusterDAO().save(cluster, tableType)));
			auditHeader.getAuditDetail().setModelData(cluster);
			auditHeader.setAuditReference(String.valueOf(cluster.getId()));
		} else {
			getClusterDAO().update(cluster, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Clusters by using ClustersDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtClusters by using auditHeaderDAO.addAudit(auditHeader)
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

		Cluster cluster = (Cluster) auditHeader.getAuditDetail().getModelData();
		getClusterDAO().delete(cluster, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getClusters fetch the details by using ClustersDAO's getClustersById method.
	 * 
	 * @param clusterId
	 *            clusterId of the Cluster.
	 * @return Clusters
	 */
	@Override
	public Cluster getCluster(long clusterId) {
		return getClusterDAO().getCluster(clusterId, "_View");
	}

	/**
	 * getApprovedClustersById fetch the details by using ClustersDAO's getClustersById method . with parameter id and
	 * type as blank. it fetches the approved records from the Clusters.
	 * 
	 * @param clusterId
	 *            clusterId of the Cluster. (String)
	 * @return Clusters
	 */
	public Cluster getApprovedCluster(long clusterId) {
		return getClusterDAO().getCluster(clusterId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getClusterDAO().delete with parameters
	 * cluster,"" b) NEW Add new record in to main table by using getClusterDAO().save with parameters cluster,"" c)
	 * EDIT Update record in the main table by using getClusterDAO().update with parameters cluster,"" 3) Delete the
	 * record from the workFlow table by using getClusterDAO().delete with parameters cluster,"_Temp" 4) Audit the
	 * record in to AuditHeader and AdtClusters by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtClusters by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
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

		Cluster cluster = new Cluster();
		BeanUtils.copyProperties((Cluster) auditHeader.getAuditDetail().getModelData(), cluster);

		getClusterDAO().delete(cluster, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(cluster.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(clusterDAO.getCluster(cluster.getId(), ""));
		}

		if (cluster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getClusterDAO().delete(cluster, TableType.MAIN_TAB);
		} else {
			cluster.setRoleCode("");
			cluster.setNextRoleCode("");
			cluster.setTaskId("");
			cluster.setNextTaskId("");
			cluster.setWorkflowId(0);

			if (cluster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				cluster.setRecordType("");
				getClusterDAO().save(cluster, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				cluster.setRecordType("");
				getClusterDAO().update(cluster, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(cluster);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getClusterDAO().delete with parameters cluster,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtClusters by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		Cluster cluster = (Cluster) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getClusterDAO().delete(cluster, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public List<Cluster> getClustersByEntity(String entity) {
		return clusterDAO.getClustersByEntity(entity);
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

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getClusterDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		Cluster cluster = (Cluster) auditDetail.getModelData();

		// Check the unique keys.
		if (cluster.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(cluster.getRecordType())
				&& clusterDAO.isDuplicateKey(cluster.getId(), cluster.getEntity(), cluster.getCode(),
						cluster.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Entity") + ": " + cluster.getEntity();
			parameters[1] = PennantJavaUtil.getLabel("label_Code") + ": " + cluster.getCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(cluster.getRecordType())) {
			if (clusterDAO.isChildsExists(cluster)) {
				String[] parameters = new String[4];
				parameters[0] = PennantJavaUtil.getLabel("label_Entity") + ": " + cluster.getEntity();
				parameters[1] = " and ";
				parameters[2] = PennantJavaUtil.getLabel("label_Cluster") + ": " + cluster.getClusterType();
				parameters[3] = " having child records you can't be removed.";

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "30550", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public List<ClusterHierarchy> getClusterHierarcheyList(String entity) {
		return getClusterDAO().getClusterHierarcheyList(entity);
	}
}