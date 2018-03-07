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
 * FileName    		:  ReligionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-01-2018    														*
 *                                                                  						*
 * Modified Date    :  24-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-01-2018       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.systemmasters.ReligionDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.ReligionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>Religion</b>.<br>
 */
public class ReligionServiceImpl extends GenericService<Religion> implements ReligionService {
	private static final  Logger logger = Logger.getLogger(ReligionServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private ReligionDAO religionDAO;
	private CustomerDAO customerDAO;

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
	 * @return the religionDAO
	 */
	public ReligionDAO getReligionDAO() {
		return religionDAO;
	}
	/**
	 * @param religionDAO the religionDAO to set
	 */
	public void setReligionDAO(ReligionDAO religionDAO) {
		this.religionDAO = religionDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * Religion/Religion_Temp by using ReligionDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ReligionDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtReligion by using
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

		Religion religion = (Religion) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (religion.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (religion.isNew()) {
			religion.setId(Long.parseLong(getReligionDAO().save(religion,tableType)));
			auditHeader.getAuditDetail().setModelData(religion);
			auditHeader.setAuditReference(String.valueOf(religion.getReligionId()));
		}else{
			getReligionDAO().update(religion,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table Religion by using ReligionDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtReligion by using
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
		
		Religion religion = (Religion) auditHeader.getAuditDetail().getModelData();
		getReligionDAO().delete(religion,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getReligion fetch the details by using ReligionDAO's getReligionById
	 * method.
	 * 
	 * @param religionId
	 *            religionId of the Religion.
	 * @return Religion
	 */
	@Override
	public Religion getReligion(long religionId) {
		return getReligionDAO().getReligion(religionId,"_View");
	}

	/**
	 * getApprovedReligionById fetch the details by using ReligionDAO's
	 * getReligionById method . with parameter id and type as blank. it fetches
	 * the approved records from the Religion.
	 * 
	 * @param religionId
	 *            religionId of the Religion.
	 *            (String)
	 * @return Religion
	 */
	public Religion getApprovedReligion(long religionId) {
		return getReligionDAO().getReligion(religionId,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getReligionDAO().delete with parameters religion,"" b) NEW Add new
	 * record in to main table by using getReligionDAO().save with parameters
	 * religion,"" c) EDIT Update record in the main table by using
	 * getReligionDAO().update with parameters religion,"" 3) Delete the record
	 * from the workFlow table by using getReligionDAO().delete with parameters
	 * religion,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtReligion by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtReligion by using
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

		Religion religion = new Religion();
		BeanUtils.copyProperties((Religion) auditHeader.getAuditDetail().getModelData(), religion);

		getReligionDAO().delete(religion, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(religion.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(religionDAO.getReligion(religion.getReligionId(), ""));
		}

		if (religion.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getReligionDAO().delete(religion, TableType.MAIN_TAB);
		} else {
			religion.setRoleCode("");
			religion.setNextRoleCode("");
			religion.setTaskId("");
			religion.setNextTaskId("");
			religion.setWorkflowId(0);

			if (religion.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				religion.setRecordType("");
				getReligionDAO().save(religion, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				religion.setRecordType("");
				getReligionDAO().update(religion, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(religion);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getReligionDAO().delete with parameters
		 * religion,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtReligion by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			Religion religion = (Religion) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getReligionDAO().delete(religion,TableType.TEMP_TAB);
			
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
		 * from getReligionDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Get the model object.
			Religion religion = (Religion) auditDetail.getModelData();
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_ReligionCode") + ": " + religion.getReligionCode();

			// Check the unique keys.
			if (religion.isNew() && religionDAO.isDuplicateKey(religion.getReligionId(),religion.getReligionCode(),
					religion.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			if (StringUtils.trimToEmpty(religion.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				boolean exist = this.customerDAO.isReligionExist(religion.getReligionId(), "_View");
				if (exist) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null), usrLanguage));
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

		public CustomerDAO getCustomerDAO() {
			return customerDAO;
		}

		public void setCustomerDAO(CustomerDAO customerDAO) {
			this.customerDAO = customerDAO;
		}

}