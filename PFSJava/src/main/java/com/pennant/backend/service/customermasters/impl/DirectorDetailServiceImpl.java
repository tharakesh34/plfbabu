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
 * FileName    		:  DirectorDetailServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.customermasters.validation.CustomerDirectorValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>DirectorDetail</b>.<br>
 * 
 */
public class DirectorDetailServiceImpl extends GenericService<DirectorDetail> 
				implements DirectorDetailService {
	
	private static final Logger logger = Logger.getLogger(DirectorDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private DirectorDetailDAO directorDetailDAO;
	private CustomerDirectorValidation customerDirectorValidation;

	public DirectorDetailServiceImpl() {
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
	
	public DirectorDetailDAO getDirectorDetailDAO() {
		return directorDetailDAO;
	}
	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}
	
	public CustomerDirectorValidation getDirectorValidation(){
		
		if(customerDirectorValidation==null){
			this.customerDirectorValidation = new CustomerDirectorValidation(directorDetailDAO);
		}
		return this.customerDirectorValidation;
	}

	@Override
	public DirectorDetail getDirectorDetail() {
		return getDirectorDetailDAO().getDirectorDetail();
	}
	
	@Override
	public DirectorDetail getNewDirectorDetail() {
		return getDirectorDetailDAO().getNewDirectorDetail();
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table 
	 * 		CustomerDirectorDetail/CustomerDirectorDetail_Temp 
	 * 			by using DirectorDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DirectorDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerDirectorDetail by using 
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
		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();
		
		if (directorDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (directorDetail.isNew()) {
			directorDetail.setId(getDirectorDetailDAO().save(directorDetail,tableType));
			auditHeader.getAuditDetail().setModelData(directorDetail);
			auditHeader.setAuditReference(String.valueOf(directorDetail.getDirectorId()));
		}else{
			getDirectorDetailDAO().update(directorDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerDirectorDetail by using 
	 * 		DirectorDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerDirectorDetail by using 
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
		
		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();
		getDirectorDetailDAO().delete(directorDetail,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDirectorDetailById fetch the details by using DirectorDetailDAO's
	 * getDirectorDetailById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetailById(long id,long custID) {
		return getDirectorDetailDAO().getDirectorDetailById(id,custID,"_View");
	}

	/**
	 * getApprovedDirectorDetailById fetch the details by using
	 * DirectorDetailDAO's getDirectorDetailById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * CustomerDirectorDetail.
	 * 
	 * @param id
	 *            (int)
	 * @return DirectorDetail
	 */
	public DirectorDetail getApprovedDirectorDetailById(long id,long custID) {
		return getDirectorDetailDAO().getDirectorDetailById(id,custID,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 			getDirectorDetailDAO().delete with parameters directorDetail,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 			getDirectorDetailDAO().save with parameters directorDetail,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 			getDirectorDetailDAO().update with parameters directorDetail,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 			getDirectorDetailDAO().delete with parameters directorDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerDirectorDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerDirectorDetail by using 
	 * 		auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DirectorDetail directorDetail = new DirectorDetail();
		BeanUtils.copyProperties((DirectorDetail) auditHeader.getAuditDetail().getModelData(),
				directorDetail);

		if (directorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getDirectorDetailDAO().delete(directorDetail,"");
		} else {
			directorDetail.setRoleCode("");
			directorDetail.setNextRoleCode("");
			directorDetail.setTaskId("");
			directorDetail.setNextTaskId("");
			directorDetail.setWorkflowId(0);

			if (directorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){	
				tranType=PennantConstants.TRAN_ADD;
				directorDetail.setRecordType("");
				getDirectorDetailDAO().save(directorDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				directorDetail.setRecordType("");
				getDirectorDetailDAO().update(directorDetail,"");
			}
		}

		getDirectorDetailDAO().delete(directorDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(directorDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using 
	 * 		getDirectorDetailDAO().delete with parameters directorDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerDirectorDetail by using 
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

		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDirectorDetailDAO().delete(directorDetail,"_Temp");

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
	 * 		getDirectorDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		auditHeader = getDirectorValidation().directorValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}