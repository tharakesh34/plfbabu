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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.BuilderProjcetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.BuilderProjcetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>BuilderProjcet</b>.<br>
 */
public class BuilderProjcetServiceImpl extends GenericService<BuilderProjcet> implements BuilderProjcetService {
	private static final Logger logger = Logger.getLogger(BuilderProjcetServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private BuilderProjcetDAO builderProjcetDAO;


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
	 * @return the builderProjcetDAO
	 */
	public BuilderProjcetDAO getBuilderProjcetDAO() {
		return builderProjcetDAO;
	}
	/**
	 * @param builderProjcetDAO the builderProjcetDAO to set
	 */
	public void setBuilderProjcetDAO(BuilderProjcetDAO builderProjcetDAO) {
		this.builderProjcetDAO = builderProjcetDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BuilderProject/BuilderProject_Temp by using BuilderProjectDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using BuilderProjectDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtBuilderProject by using
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

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (builderProjcet.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (builderProjcet.isNew()) {
			builderProjcet.setId(Long.parseLong(getBuilderProjcetDAO().save(builderProjcet,tableType)));
			auditHeader.getAuditDetail().setModelData(builderProjcet);
			auditHeader.setAuditReference(String.valueOf(builderProjcet.getId()));
		}else{
			getBuilderProjcetDAO().update(builderProjcet,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BuilderProject by using BuilderProjectDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtBuilderProject by using
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

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();
		getBuilderProjcetDAO().delete(builderProjcet,TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getBuilderProject fetch the details by using BuilderProjectDAO's getBuilderProjectById
	 * method.
	 * 
	 * @param id
	 *            id of the BuilderProjcet.
	 * @return BuilderProject
	 */
	@Override
	public BuilderProjcet getBuilderProjcet(long id) {
		return getBuilderProjcetDAO().getBuilderProjcet(id,"_View");
	}

	/**
	 * getApprovedBuilderProjectById fetch the details by using BuilderProjectDAO's
	 * getBuilderProjectById method . with parameter id and type as blank. it fetches
	 * the approved records from the BuilderProject.
	 * 
	 * @param id
	 *            id of the BuilderProjcet.
	 *            (String)
	 * @return BuilderProject
	 */
	public BuilderProjcet getApprovedBuilderProjcet(long id) {
		return getBuilderProjcetDAO().getBuilderProjcet(id,"_AView");
	}	

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getBuilderProjcetDAO().delete with parameters builderProjcet,"" b) NEW Add new
	 * record in to main table by using getBuilderProjcetDAO().save with parameters
	 * builderProjcet,"" c) EDIT Update record in the main table by using
	 * getBuilderProjcetDAO().update with parameters builderProjcet,"" 3) Delete the record
	 * from the workFlow table by using getBuilderProjcetDAO().delete with parameters
	 * builderProjcet,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtBuilderProject by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtBuilderProject by using
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

		BuilderProjcet builderProjcet = new BuilderProjcet();
		BeanUtils.copyProperties((BuilderProjcet) auditHeader.getAuditDetail().getModelData(), builderProjcet);

		getBuilderProjcetDAO().delete(builderProjcet, TableType.TEMP_TAB);


		if (!PennantConstants.RECORD_TYPE_NEW.equals(builderProjcet.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(builderProjcetDAO.getBuilderProjcet(builderProjcet.getId(),""));
		}

		if (builderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBuilderProjcetDAO().delete(builderProjcet, TableType.MAIN_TAB);
		} else {
			builderProjcet.setRoleCode("");
			builderProjcet.setNextRoleCode("");
			builderProjcet.setTaskId("");
			builderProjcet.setNextTaskId("");
			builderProjcet.setWorkflowId(0);

			if (builderProjcet.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				builderProjcet.setRecordType("");
				getBuilderProjcetDAO().save(builderProjcet, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				builderProjcet.setRecordType("");
				getBuilderProjcetDAO().update(builderProjcet, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(builderProjcet);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getBuilderProjcetDAO().delete with parameters
	 * builderProjcet,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtBuilderProject by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		BuilderProjcet builderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBuilderProjcetDAO().delete(builderProjcet,TableType.TEMP_TAB);

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
	 * from getBuilderProjcetDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		BuilderProjcet builderProjcet = (BuilderProjcet) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_name") + ": " + builderProjcet.getName();
		parameters[1] = PennantJavaUtil.getLabel("label_groupId") + ": " + builderProjcet.getBuilderId();

		// Check the unique keys.
		if (builderProjcet.isNew() && builderProjcetDAO.isDuplicateKey(builderProjcet.getId(), builderProjcet.getName(),
				builderProjcet.getBuilderId(), builderProjcet.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}