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
 * FileName    		:  EntityServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-06-2017    														*
 *                                                                  						*
 * Modified Date    :  15-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-06-2017       PENNANT	                 0.1                                            * 
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>Entity</b>.<br>
 */
public class EntityServiceImpl extends GenericService<Entity> implements EntityService {
	private static final Logger logger = Logger.getLogger(EntityServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private EntityDAO entityDAO;


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
	 * @return the entityDAO
	 */
	public EntityDAO getEntityDAO() {
		return entityDAO;
	}
	/**
	 * @param entityDAO the entityDAO to set
	 */
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Entities/Entities_Temp by using EntitiesDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using EntitiesDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtEntities by using
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

		Entity entity = (Entity) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (entity.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (entity.isNew()) {
			getEntityDAO().save(entity,tableType);
		}else{
			getEntityDAO().update(entity,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table Entities by using EntitiesDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtEntities by using
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
		
		Entity entity = (Entity) auditHeader.getAuditDetail().getModelData();
		getEntityDAO().delete(entity,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getEntities fetch the details by using EntitiesDAO's getEntitiesById
	 * method.
	 * 
	 * @param entityCode
	 *            entityCode of the Entity.
	 * @return Entities
	 */
	@Override
	public Entity getEntity(String entityCode) {
		return getEntityDAO().getEntity(entityCode,"_View");
	}

	/**
	 * getApprovedEntitiesById fetch the details by using EntitiesDAO's
	 * getEntitiesById method . with parameter id and type as blank. it fetches
	 * the approved records from the Entities.
	 * 
	 * @param entityCode
	 *            entityCode of the Entity.
	 *            (String)
	 * @return Entities
	 */
	public Entity getApprovedEntity(String entityCode) {
		return getEntityDAO().getEntity(entityCode,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getEntityDAO().delete with parameters entity,"" b) NEW Add new
	 * record in to main table by using getEntityDAO().save with parameters
	 * entity,"" c) EDIT Update record in the main table by using
	 * getEntityDAO().update with parameters entity,"" 3) Delete the record
	 * from the workFlow table by using getEntityDAO().delete with parameters
	 * entity,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtEntities by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtEntities by using
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

		Entity entity = new Entity();
		BeanUtils.copyProperties((Entity) auditHeader.getAuditDetail().getModelData(), entity);

		getEntityDAO().delete(entity, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(entity.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(entityDAO.getEntity(entity.getEntityCode(), ""));
		}

		if (entity.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getEntityDAO().delete(entity, TableType.MAIN_TAB);
		} else {
			entity.setRoleCode("");
			entity.setNextRoleCode("");
			entity.setTaskId("");
			entity.setNextTaskId("");
			entity.setWorkflowId(0);

			if (entity.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				entity.setRecordType("");
				getEntityDAO().save(entity, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				entity.setRecordType("");
				getEntityDAO().update(entity, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(entity);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getEntityDAO().delete with parameters
		 * entity,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtEntities by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			Entity entity = (Entity) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getEntityDAO().delete(entity,TableType.TEMP_TAB);
			
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
		 * from getEntityDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Write the required validation over hear.
			// Get the model object.
			Entity entity = (Entity) auditDetail.getModelData();


			// Check the unique keys.
			if (entity.isNew()
					&& PennantConstants.RECORD_TYPE_NEW.equals(entity.getRecordType())
					&& entityDAO.count( entity.getEntityCode(),null,
							entity.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_EntityCode") + ": " + entity.getEntityCode();
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", parameters, null));
			}

			// Check the unique keys.
			if (entity.isNew()
					&& PennantConstants.RECORD_TYPE_NEW.equals(entity.getRecordType())
					&& entityDAO.count( null,entity.getPANNumber(),
							entity.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_pANNumber") + ": " + entity.getPANNumber();
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", parameters, null));
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}