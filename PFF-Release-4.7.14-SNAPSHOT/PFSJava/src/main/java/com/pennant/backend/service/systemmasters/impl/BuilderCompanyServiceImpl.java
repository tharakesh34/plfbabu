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
 * FileName    		:  BuilderCompanyServiceImpl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.BuilderCompanyDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.BuilderCompanyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>BuilderCompany</b>.<br>
 */
public class BuilderCompanyServiceImpl extends GenericService<BuilderCompany> implements BuilderCompanyService {
	private static final Logger logger = Logger.getLogger(BuilderCompanyServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private BuilderCompanyDAO builderCompanyDAO;


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
	 * @return the builderCompanyDAO
	 */
	public BuilderCompanyDAO getBuilderCompanyDAO() {
		return builderCompanyDAO;
	}
	/**
	 * @param builderCompanyDAO the builderCompanyDAO to set
	 */
	public void setBuilderCompanyDAO(BuilderCompanyDAO builderCompanyDAO) {
		this.builderCompanyDAO = builderCompanyDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BuilderCompany/BuilderCompany_Temp by using BuilderCompanyDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BuilderCompanyDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBuilderCompany by using
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

		BuilderCompany builderCompany = (BuilderCompany) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (builderCompany.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (builderCompany.isNew()) {
			builderCompany.setId(Long.parseLong(getBuilderCompanyDAO().save(builderCompany,tableType)));
			auditHeader.getAuditDetail().setModelData(builderCompany);
			auditHeader.setAuditReference(String.valueOf(builderCompany.getId()));
		}else{
			getBuilderCompanyDAO().update(builderCompany,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BuilderCompany by using BuilderCompanyDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBuilderCompany by using
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
		
		BuilderCompany builderCompany = (BuilderCompany) auditHeader.getAuditDetail().getModelData();
		getBuilderCompanyDAO().delete(builderCompany,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBuilderCompany fetch the details by using BuilderCompanyDAO's getBuilderCompanyById
	 * method.
	 * 
	 * @param id
	 *            id of the BuilderCompany.
	 * @return BuilderCompany
	 */
	@Override
	public BuilderCompany getBuilderCompany(long id) {
		return getBuilderCompanyDAO().getBuilderCompany(id,"_View");
	}

	/**
	 * getApprovedBuilderCompanyById fetch the details by using BuilderCompanyDAO's
	 * getBuilderCompanyById method . with parameter id and type as blank. it fetches
	 * the approved records from the BuilderCompany.
	 * 
	 * @param id
	 *            id of the BuilderCompany.
	 *            (String)
	 * @return BuilderCompany
	 */
	public BuilderCompany getApprovedBuilderCompany(long id) {
		return getBuilderCompanyDAO().getBuilderCompany(id,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBuilderCompanyDAO().delete with parameters builderCompany,"" b) NEW Add new
	 * record in to main table by using getBuilderCompanyDAO().save with parameters
	 * builderCompany,"" c) EDIT Update record in the main table by using
	 * getBuilderCompanyDAO().update with parameters builderCompany,"" 3) Delete the record
	 * from the workFlow table by using getBuilderCompanyDAO().delete with parameters
	 * builderCompany,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBuilderCompany by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBuilderCompany by using
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

		BuilderCompany builderCompany = new BuilderCompany();
		BeanUtils.copyProperties((BuilderCompany) auditHeader.getAuditDetail().getModelData(), builderCompany);

		getBuilderCompanyDAO().delete(builderCompany, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(builderCompany.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(builderCompanyDAO.getBuilderCompany(builderCompany.getId(), ""));
		}

		if (builderCompany.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBuilderCompanyDAO().delete(builderCompany, TableType.MAIN_TAB);
		} else {
			builderCompany.setRoleCode("");
			builderCompany.setNextRoleCode("");
			builderCompany.setTaskId("");
			builderCompany.setNextTaskId("");
			builderCompany.setWorkflowId(0);

			if (builderCompany.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				builderCompany.setRecordType("");
				getBuilderCompanyDAO().save(builderCompany, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				builderCompany.setRecordType("");
				getBuilderCompanyDAO().update(builderCompany, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(builderCompany);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getBuilderCompanyDAO().delete with parameters
		 * builderCompany,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtBuilderCompany by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			BuilderCompany builderCompany = (BuilderCompany) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getBuilderCompanyDAO().delete(builderCompany,TableType.TEMP_TAB);
			
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
		 * from getBuilderCompanyDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BuilderCompany builderCompany = (BuilderCompany) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_name") + ": " + builderCompany.getName();
		parameters[1] = PennantJavaUtil.getLabel("label_groupId") + ": " + builderCompany.getGroupId();

		// Check the unique keys.
		if (builderCompany.isNew() && builderCompanyDAO.isDuplicateKey(builderCompany.getId(), builderCompany.getName(), builderCompany.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		// If Builder Group is already utilized in Builder Company 
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, builderCompany.getRecordType())) {
			boolean workflowExists = getBuilderCompanyDAO().isIdExists(builderCompany.getId());
			if (workflowExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}