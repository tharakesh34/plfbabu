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
 * FileName    		:  BuilderGroupServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-05-2017    														*
 *                                                                  						*
 * Modified Date    :  17-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-05-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.BuilderGroupDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.BuilderGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>BuilderGroup</b>.<br>
 */
public class BuilderGroupServiceImpl extends GenericService<BuilderGroup> implements BuilderGroupService {
	private static final Logger logger = Logger.getLogger(BuilderGroupServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private BuilderGroupDAO builderGroupDAO;


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
	 * @return the builderGroupDAO
	 */
	public BuilderGroupDAO getBuilderGroupDAO() {
		return builderGroupDAO;
	}
	/**
	 * @param builderGroupDAO the builderGroupDAO to set
	 */
	public void setBuilderGroupDAO(BuilderGroupDAO builderGroupDAO) {
		this.builderGroupDAO = builderGroupDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BuilderGroup/BuilderGroup_Temp by using BuilderGroupDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BuilderGroupDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBuilderGroup by using
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

		BuilderGroup builderGroup = (BuilderGroup) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (builderGroup.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (builderGroup.isNew()) {
			builderGroup.setId(Long.parseLong(getBuilderGroupDAO().save(builderGroup,tableType)));
			auditHeader.getAuditDetail().setModelData(builderGroup);
			auditHeader.setAuditReference(String.valueOf(builderGroup.getId()));
		}else{
			getBuilderGroupDAO().update(builderGroup,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BuilderGroup by using BuilderGroupDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBuilderGroup by using
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
		
		BuilderGroup builderGroup = (BuilderGroup) auditHeader.getAuditDetail().getModelData();
		getBuilderGroupDAO().delete(builderGroup,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBuilderGroup fetch the details by using BuilderGroupDAO's getBuilderGroupById
	 * method.
	 * 
	 * @param id
	 *            id of the BuilderGroup.
	 * @return BuilderGroup
	 */
	@Override
	public BuilderGroup getBuilderGroup(long id) {
		return getBuilderGroupDAO().getBuilderGroup(id,"_View");
	}

	/**
	 * getApprovedBuilderGroupById fetch the details by using BuilderGroupDAO's
	 * getBuilderGroupById method . with parameter id and type as blank. it fetches
	 * the approved records from the BuilderGroup.
	 * 
	 * @param id
	 *            id of the BuilderGroup.
	 *            (String)
	 * @return BuilderGroup
	 */
	public BuilderGroup getApprovedBuilderGroup(long id) {
		return getBuilderGroupDAO().getBuilderGroup(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBuilderGroupDAO().delete with parameters builderGroup,"" b) NEW Add new
	 * record in to main table by using getBuilderGroupDAO().save with parameters
	 * builderGroup,"" c) EDIT Update record in the main table by using
	 * getBuilderGroupDAO().update with parameters builderGroup,"" 3) Delete the record
	 * from the workFlow table by using getBuilderGroupDAO().delete with parameters
	 * builderGroup,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBuilderGroup by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBuilderGroup by using
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

		BuilderGroup builderGroup = new BuilderGroup();
		BeanUtils.copyProperties((BuilderGroup) auditHeader.getAuditDetail().getModelData(), builderGroup);

		getBuilderGroupDAO().delete(builderGroup, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(builderGroup.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(builderGroupDAO.getBuilderGroup(builderGroup.getId(), ""));
		}

		if (builderGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBuilderGroupDAO().delete(builderGroup, TableType.MAIN_TAB);
		} else {
			builderGroup.setRoleCode("");
			builderGroup.setNextRoleCode("");
			builderGroup.setTaskId("");
			builderGroup.setNextTaskId("");
			builderGroup.setWorkflowId(0);

			if (builderGroup.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				builderGroup.setRecordType("");
				getBuilderGroupDAO().save(builderGroup, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				builderGroup.setRecordType("");
				getBuilderGroupDAO().update(builderGroup, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(builderGroup);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getBuilderGroupDAO().delete with parameters
		 * builderGroup,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtBuilderGroup by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			BuilderGroup builderGroup = (BuilderGroup) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getBuilderGroupDAO().delete(builderGroup,TableType.TEMP_TAB);
			
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
		 * from getBuilderGroupDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			BuilderGroup builderGroup = (BuilderGroup) auditDetail.getModelData();

			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_name") + ": " + builderGroup.getName();
			// Check the unique keys.
			if (builderGroup.isNew() && builderGroupDAO.isDuplicateKey(builderGroup.getId(),builderGroup.getName(),
				builderGroup.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			// If Builder Group is already utilized in Builder Company 
			if(StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, builderGroup.getRecordType())){
				boolean workflowExists = getBuilderGroupDAO().isIdExists(builderGroup.getId());
				if(workflowExists){
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", parameters, null));
				}
			}
			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}