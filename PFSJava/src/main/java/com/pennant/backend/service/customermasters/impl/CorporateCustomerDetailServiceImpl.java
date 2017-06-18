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
 * FileName    		:  CorporateCustomerDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.customermasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CorporateCustomerDetailDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CorporateCustomerDetailService;
import com.pennant.backend.service.customermasters.validation.CorporateCustomerValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CorporateCustomerDetail</b>.<br>
 * 
 */
public class CorporateCustomerDetailServiceImpl extends GenericService<CorporateCustomerDetail> implements
		CorporateCustomerDetailService {
	private static final Logger logger = Logger.getLogger(CorporateCustomerDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CorporateCustomerDetailDAO corporateCustomerDetailDAO;
	private CorporateCustomerValidation corporateCustomerValidation;

	public CorporateCustomerDetailServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CorporateCustomerDetailDAO getCorporateCustomerDetailDAO() {
		return corporateCustomerDetailDAO;
	}
	public void setCorporateCustomerDetailDAO(CorporateCustomerDetailDAO corporateCustomerDetailDAO) {
		this.corporateCustomerDetailDAO = corporateCustomerDetailDAO;
	}

	public CorporateCustomerValidation getCorporateValidation(){
		
		if(corporateCustomerValidation==null){
			this.corporateCustomerValidation = new CorporateCustomerValidation(corporateCustomerDetailDAO);
		}
		return this.corporateCustomerValidation;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table 
	 * 		CustomerCorporateDetail/CustomerCorporateDetail_Temp 
	 * 			by using CorporateCustomerDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CorporateCustomerDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerCorporateDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		CorporateCustomerDetail corporateCustomerDetail = (CorporateCustomerDetail) 
		auditHeader.getAuditDetail().getModelData();

		if (corporateCustomerDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (corporateCustomerDetail.isNew()) {
			corporateCustomerDetail.setId(getCorporateCustomerDetailDAO().save(
					corporateCustomerDetail,tableType));
			auditHeader.getAuditDetail().setModelData(corporateCustomerDetail);
			auditHeader.setAuditReference(String.valueOf(corporateCustomerDetail.getCustId()));
		}else{
			getCorporateCustomerDetailDAO().update(corporateCustomerDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerCorporateDetail by using 
	 * 		CorporateCustomerDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerCorporateDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CorporateCustomerDetail corporateCustomerDetail = (CorporateCustomerDetail) 
		auditHeader.getAuditDetail().getModelData();
		getCorporateCustomerDetailDAO().delete(corporateCustomerDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCorporateCustomerDetailById fetch the details by using CorporateCustomerDetailDAO's 
	 * 				getCorporateCustomerDetailById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CorporateCustomerDetail
	 */

	@Override
	public CorporateCustomerDetail getCorporateCustomerDetailById(long id) {
		return getCorporateCustomerDetailDAO().getCorporateCustomerDetailById(id,"_View");
	}

	/**
	 * getApprovedCorporateCustomerDetailById fetch the details by using
	 * CorporateCustomerDetailDAO's getCorporateCustomerDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * CustomerCorporateDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return CorporateCustomerDetail
	 */	
	public CorporateCustomerDetail getApprovedCorporateCustomerDetailById(long id) {
		return getCorporateCustomerDetailDAO().getCorporateCustomerDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 		getCorporateCustomerDetailDAO().delete with parameters corporateCustomerDetail,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 		getCorporateCustomerDetailDAO().save with parameters corporateCustomerDetail,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 		getCorporateCustomerDetailDAO().update with parameters corporateCustomerDetail,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 		getCorporateCustomerDetailDAO().delete with parameters corporateCustomerDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerCorporateDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerCorporateDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CorporateCustomerDetail corporateCustomerDetail = new CorporateCustomerDetail();
		BeanUtils.copyProperties((CorporateCustomerDetail) auditHeader.getAuditDetail().getModelData(),
				corporateCustomerDetail);

		if (corporateCustomerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getCorporateCustomerDetailDAO().delete(corporateCustomerDetail,"");

		} else {
			corporateCustomerDetail.setRoleCode("");
			corporateCustomerDetail.setNextRoleCode("");
			corporateCustomerDetail.setTaskId("");
			corporateCustomerDetail.setNextTaskId("");
			corporateCustomerDetail.setWorkflowId(0);

			if (corporateCustomerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				corporateCustomerDetail.setRecordType("");
				getCorporateCustomerDetailDAO().save(corporateCustomerDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				corporateCustomerDetail.setRecordType("");
				getCorporateCustomerDetailDAO().update(corporateCustomerDetail,"");
			}
		}

		getCorporateCustomerDetailDAO().delete(corporateCustomerDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(corporateCustomerDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 		getCorporateCustomerDetailDAO().delete with parameters corporateCustomerDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerCorporateDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CorporateCustomerDetail corporateCustomerDetail = (CorporateCustomerDetail) 
		auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCorporateCustomerDetailDAO().delete(corporateCustomerDetail,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from 
	 * 		getCorporateCustomerDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = getCorporateValidation().corporateDetailValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}


}