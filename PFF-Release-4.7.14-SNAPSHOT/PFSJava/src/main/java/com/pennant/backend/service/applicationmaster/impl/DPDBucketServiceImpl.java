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
 * FileName    		:  DPDBucketServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.DPDBucketDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.DPDBucketService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>DPDBucket</b>.<br>
 */
public class DPDBucketServiceImpl extends GenericService<DPDBucket> implements DPDBucketService {
	private static final Logger logger = Logger.getLogger(DPDBucketServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private DPDBucketDAO dPDBucketDAO;
	private DPDBucketConfigurationDAO	dPDBucketConfigurationDAO;


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
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	/**
	 * @return the dPDBucketDAO
	 */
	public DPDBucketDAO getDPDBucketDAO() {
		return dPDBucketDAO;
	}
	/**
	 * @param dPDBucketDAO the dPDBucketDAO to set
	 */
	public void setDPDBucketDAO(DPDBucketDAO dPDBucketDAO) {
		this.dPDBucketDAO = dPDBucketDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * DPDBUCKETS/DPDBUCKETS_Temp by using DPDBUCKETSDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using DPDBUCKETSDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtDPDBUCKETS by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);	
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DPDBucket dPDBucket = (DPDBucket) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (dPDBucket.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (dPDBucket.isNew()) {
			dPDBucket.setId(Long.valueOf(getDPDBucketDAO().save(dPDBucket,tableType)));
			auditHeader.getAuditDetail().setModelData(dPDBucket);
			auditHeader.setAuditReference(String.valueOf(dPDBucket.getBucketID()));
		}else{
			getDPDBucketDAO().update(dPDBucket,tableType);
		}
		
		if (TableType.MAIN_TAB.equals(tableType)) {
			FinanceConfigCache.clearDPDBucketCache(dPDBucket.getBucketID());
			FinanceConfigCache.clearDPDBucketCodeCache(dPDBucket.getBucketCode());
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table DPDBUCKETS by using DPDBUCKETSDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtDPDBUCKETS by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		
		DPDBucket dPDBucket = (DPDBucket) auditHeader.getAuditDetail().getModelData();
		getDPDBucketDAO().delete(dPDBucket,TableType.MAIN_TAB);
		FinanceConfigCache.clearDPDBucketCache(dPDBucket.getBucketID());
		FinanceConfigCache.clearDPDBucketCodeCache(dPDBucket.getBucketCode());
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getDPDBUCKETS fetch the details by using DPDBUCKETSDAO's getDPDBUCKETSById
	 * method.
	 * 
	 * @param bucketID
	 *            bucketID of the DPDBucket.
	 * @return DPDBUCKETS
	 */
	@Override
	public DPDBucket getDPDBucket(long bucketID) {
		return getDPDBucketDAO().getDPDBucket(bucketID,"_View");
	}

	/**
	 * It fetches Approved DPDBucket from DPDBUCKETS
	 * 
	 * @param long bucketID
	 * @return DPDBUCKETS
	 */
	public DPDBucket getApprovedDPDBucket(long bucketID) {
		return FinanceConfigCache.getDPDBucket(bucketID);
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDPDBucketDAO().delete with parameters dPDBucket,"" b) NEW Add new
	 * record in to main table by using getDPDBucketDAO().save with parameters
	 * dPDBucket,"" c) EDIT Update record in the main table by using
	 * getDPDBucketDAO().update with parameters dPDBucket,"" 3) Delete the record
	 * from the workFlow table by using getDPDBucketDAO().delete with parameters
	 * dPDBucket,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDPDBUCKETS by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtDPDBUCKETS by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DPDBucket dPDBucket = new DPDBucket();
		BeanUtils.copyProperties((DPDBucket) auditHeader.getAuditDetail().getModelData(), dPDBucket);

		getDPDBucketDAO().delete(dPDBucket, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(dPDBucket.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(dPDBucketDAO.getDPDBucket(dPDBucket.getBucketID(), ""));
		}

		if (dPDBucket.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDPDBucketDAO().delete(dPDBucket, TableType.MAIN_TAB);
			
		} else {
			dPDBucket.setRoleCode("");
			dPDBucket.setNextRoleCode("");
			dPDBucket.setTaskId("");
			dPDBucket.setNextTaskId("");
			dPDBucket.setWorkflowId(0);

			if (dPDBucket.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dPDBucket.setRecordType("");
				getDPDBucketDAO().save(dPDBucket, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dPDBucket.setRecordType("");
				getDPDBucketDAO().update(dPDBucket, TableType.MAIN_TAB);
			}
		}
		
		FinanceConfigCache.clearDPDBucketCache(dPDBucket.getBucketID());
		FinanceConfigCache.clearDPDBucketCodeCache(dPDBucket.getBucketCode());

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dPDBucket);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getDPDBucketDAO().delete with parameters
		 * dPDBucket,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtDPDBUCKETS by using auditHeaderDAO.addAudit(auditHeader) for Work
		 * flow
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		@Override
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.info(Literal.ENTERING);
			
			auditHeader = businessValidation(auditHeader,"doApprove");
			if (!auditHeader.isNextProcess()) {
				logger.info(Literal.LEAVING);
				return auditHeader;
			}

			DPDBucket dPDBucket = (DPDBucket) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDPDBucketDAO().delete(dPDBucket,TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps. 1) get the details from
		 * the auditHeader. 2) fetch the details from the tables 3) Validate the
		 * Record based on the record details. 4) Validate for any business
		 * validation.
		 * 
		 * @param AuditHeader
		 *            (auditHeader)
		 * @return auditHeader
		 */
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug(Literal.ENTERING);
			
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);

			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		/**
		 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
		 * from getDPDBucketDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			DPDBucket dPDBucket = (DPDBucket) auditDetail.getModelData();

			// Check the unique keys.
			if (dPDBucket.isNew() && dPDBucketDAO.isDuplicateKey(dPDBucket.getBucketID(),dPDBucket.getBucketCode(),
					dPDBucket.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_BucketCode") + ": " + dPDBucket.getBucketCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
		if (StringUtils.trimToEmpty(dPDBucket.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = dPDBucketConfigurationDAO.getDPDBucketConfigurationDAOById(dPDBucket.getBucketID(), "");//FIXME for FinanceMain
			if (count != 0) {

				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = dPDBucket.getBucketCode();
				errParm[0] = PennantJavaUtil.getLabel("label_BucketCode") + ":" + valueParm[0];

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"41006", errParm, valueParm), usrLanguage));
			}
		}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public DPDBucketConfigurationDAO getdPDBucketConfigurationDAO() {
			return dPDBucketConfigurationDAO;
		}

		public void setdPDBucketConfigurationDAO(DPDBucketConfigurationDAO dPDBucketConfigurationDAO) {
			this.dPDBucketConfigurationDAO = dPDBucketConfigurationDAO;
		}

}