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
 * FileName    		:  NPABucketServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.applicationmaster.NPABucketConfigurationDAO;
import com.pennant.backend.dao.applicationmaster.NPABucketDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.NPABucketService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>NPABucket</b>.<br>
 */
public class NPABucketServiceImpl extends GenericService<NPABucket> implements NPABucketService {
	private static final Logger logger = Logger.getLogger(NPABucketServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private NPABucketDAO nPABucketDAO;
	private NPABucketConfigurationDAO	nPABucketConfigurationDAO;


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
	 * @return the nPABucketDAO
	 */
	public NPABucketDAO getNPABucketDAO() {
		return nPABucketDAO;
	}
	/**
	 * @param nPABucketDAO the nPABucketDAO to set
	 */
	public void setNPABucketDAO(NPABucketDAO nPABucketDAO) {
		this.nPABucketDAO = nPABucketDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * NPABUCKETS/NPABUCKETS_Temp by using NPABUCKETSDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using NPABUCKETSDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtNPABUCKETS by using
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

		NPABucket nPABucket = (NPABucket) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (nPABucket.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (nPABucket.isNew()) {
			nPABucket.setId(Long.valueOf(getNPABucketDAO().save(nPABucket,tableType)));
			auditHeader.getAuditDetail().setModelData(nPABucket);
			auditHeader.setAuditReference(String.valueOf(nPABucket.getBucketID()));
		}else{
			getNPABucketDAO().update(nPABucket,tableType);
		}
		
		if (TableType.MAIN_TAB.equals(tableType)) {
			FinanceConfigCache.clearNPABucketCache(nPABucket.getBucketID());
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table NPABUCKETS by using NPABUCKETSDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtNPABUCKETS by using
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
		
		NPABucket nPABucket = (NPABucket) auditHeader.getAuditDetail().getModelData();
		getNPABucketDAO().delete(nPABucket,TableType.MAIN_TAB);
		FinanceConfigCache.clearNPABucketCache(nPABucket.getBucketID());
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getNPABUCKETS fetch the details by using NPABUCKETSDAO's getNPABUCKETSById
	 * method.
	 * 
	 * @param bucketID
	 *            bucketID of the NPABucket.
	 * @return NPABUCKETS
	 */
	@Override
	public NPABucket getNPABucket(long bucketID) {
		return getNPABucketDAO().getNPABucket(bucketID,"_View");
	}

	/**
	 *  It fetches Approved NPABucket from NPABUCKETS
	 * 
	 * @param long bucketID
	 * @return NPABUCKETS
	 */
	public NPABucket getApprovedNPABucket(long bucketID) {
		return FinanceConfigCache.getNPABucket(bucketID);
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getNPABucketDAO().delete with parameters nPABucket,"" b) NEW Add new
	 * record in to main table by using getNPABucketDAO().save with parameters
	 * nPABucket,"" c) EDIT Update record in the main table by using
	 * getNPABucketDAO().update with parameters nPABucket,"" 3) Delete the record
	 * from the workFlow table by using getNPABucketDAO().delete with parameters
	 * nPABucket,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtNPABUCKETS by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtNPABUCKETS by using
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

		NPABucket nPABucket = new NPABucket();
		BeanUtils.copyProperties((NPABucket) auditHeader.getAuditDetail().getModelData(), nPABucket);

		getNPABucketDAO().delete(nPABucket, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(nPABucket.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(nPABucketDAO.getNPABucket(nPABucket.getBucketID(), ""));
		}

		if (nPABucket.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getNPABucketDAO().delete(nPABucket, TableType.MAIN_TAB);
		} else {
			nPABucket.setRoleCode("");
			nPABucket.setNextRoleCode("");
			nPABucket.setTaskId("");
			nPABucket.setNextTaskId("");
			nPABucket.setWorkflowId(0);

			if (nPABucket.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				nPABucket.setRecordType("");
				getNPABucketDAO().save(nPABucket, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				nPABucket.setRecordType("");
				getNPABucketDAO().update(nPABucket, TableType.MAIN_TAB);
			}
		}
		
		FinanceConfigCache.clearNPABucketCache(nPABucket.getBucketID());

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(nPABucket);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getNPABucketDAO().delete with parameters
		 * nPABucket,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtNPABUCKETS by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			NPABucket nPABucket = (NPABucket) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getNPABucketDAO().delete(nPABucket,TableType.TEMP_TAB);
			
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
		 * from getNPABucketDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			NPABucket nPABucket = (NPABucket) auditDetail.getModelData();

			// Check the unique keys.
			if (nPABucket.isNew() && nPABucketDAO.isDuplicateKey(nPABucket.getBucketID(),nPABucket.getBucketCode(),
					nPABucket.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_BucketCode") + ": " + nPABucket.getBucketCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
		if (StringUtils.trimToEmpty(nPABucket.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			int count = nPABucketConfigurationDAO.getNPABucketConfigurationById(nPABucket.getBucketID(), "");//FIXME for FinanceMain
			if (count != 0) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_BucketCode") + ": " + nPABucket.getBucketCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public NPABucketConfigurationDAO getnPABucketConfigurationDAO() {
			return nPABucketConfigurationDAO;
		}

		public void setnPABucketConfigurationDAO(NPABucketConfigurationDAO nPABucketConfigurationDAO) {
			this.nPABucketConfigurationDAO = nPABucketConfigurationDAO;
		}

}