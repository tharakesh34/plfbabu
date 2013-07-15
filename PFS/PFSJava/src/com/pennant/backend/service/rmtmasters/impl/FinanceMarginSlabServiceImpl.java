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
 * FileName    		:  FinanceMarginSlabServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-11-2011    														*
 *                                                                  						*
 * Modified Date    :  14-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.backend.service.rmtmasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceMarginSlabDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.FinanceMarginSlabService;
import com.pennant.backend.service.rmtmasters.commodityFinanceType.validation.FinanceMarginSlabValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>FinanceMarginSlab</b>.<br>
 * 
 */
public class FinanceMarginSlabServiceImpl extends GenericService<FinanceMarginSlab> implements FinanceMarginSlabService {

	private final static Logger logger = Logger.getLogger(FinanceMarginSlabServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceMarginSlabDAO financeMarginSlabDAO;
	private FinanceMarginSlabValidation financeMarginSlabValidation;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceMarginSlabDAO getFinanceMarginSlabDAO() {
		return financeMarginSlabDAO;
	}
	public void setFinanceMarginSlabDAO(FinanceMarginSlabDAO financeMarginSlabDAO) {
		this.financeMarginSlabDAO = financeMarginSlabDAO;
	}

	@Override
	public FinanceMarginSlab getFinanceMarginSlab() {
		return getFinanceMarginSlabDAO().getFinanceMarginSlab();
	}
	@Override
	public FinanceMarginSlab getNewFinanceMarginSlab() {
		return getFinanceMarginSlabDAO().getNewFinanceMarginSlab();
	}

	public FinanceMarginSlabValidation getMarginSlabValidation(){
		
		if(financeMarginSlabValidation == null){
			this.financeMarginSlabValidation = new FinanceMarginSlabValidation(financeMarginSlabDAO);
		}
		return this.financeMarginSlabValidation;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FCMTFinanceMarginSlab/FCMTFinanceMarginSlab_Temp 
	 * 			by using FinanceMarginSlabDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceMarginSlabDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFCMTFinanceMarginSlab by using auditHeaderDAO.addAudit(auditHeader)
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
		FinanceMarginSlab financeMarginSlab = (FinanceMarginSlab) auditHeader.getAuditDetail().getModelData();

		if (financeMarginSlab.isWorkflow()) {
			tableType="_TEMP";
		}

		if (financeMarginSlab.isNew()) {
			getFinanceMarginSlabDAO().save(financeMarginSlab,tableType);
			auditHeader.getAuditDetail().setModelData(financeMarginSlab);
			auditHeader.setAuditReference(financeMarginSlab.getFinType());
		}else{
			getFinanceMarginSlabDAO().update(financeMarginSlab,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FCMTFinanceMarginSlab by using FinanceMarginSlabDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFCMTFinanceMarginSlab by using auditHeaderDAO.addAudit(auditHeader)    
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

		FinanceMarginSlab financeMarginSlab = (FinanceMarginSlab) auditHeader.getAuditDetail().getModelData();
		getFinanceMarginSlabDAO().delete(financeMarginSlab,"");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getFinanceMarginSlabById fetch the details by using FinanceMarginSlabDAO's getFinanceMarginSlabById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return FinanceMarginSlab
	 */
	@Override
	public FinanceMarginSlab getFinanceMarginSlabById(String id) {
		return getFinanceMarginSlabDAO().getFinanceMarginSlabById(id,"_View");
	}

	/**
	 * getApprovedFinanceMarginSlabById fetch the details by using FinanceMarginSlabDAO's getFinanceMarginSlabById method .
	 * with parameter id and type as blank. it fetches the approved records from the FCMTFinanceMarginSlab.
	 * @param id (String)
	 * @return FinanceMarginSlab
	 */
	public FinanceMarginSlab getApprovedFinanceMarginSlabById(String id) {
		return getFinanceMarginSlabDAO().getFinanceMarginSlabById(id,"_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param FinanceMarginSlab (financeMarginSlab)
	 * @return financeMarginSlab
	 */
	@Override
	public FinanceMarginSlab refresh(FinanceMarginSlab financeMarginSlab) {
		logger.debug("Entering");
		getFinanceMarginSlabDAO().refresh(financeMarginSlab);
		getFinanceMarginSlabDAO().initialize(financeMarginSlab);
		logger.debug("Leaving");
		return financeMarginSlab;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinanceMarginSlabDAO().delete with parameters financeMarginSlab,""
	 * 		b)  NEW		Add new record in to main table by using getFinanceMarginSlabDAO().save with parameters financeMarginSlab,""
	 * 		c)  EDIT	Update record in the main table by using getFinanceMarginSlabDAO().update with parameters financeMarginSlab,""
	 * 3)	Delete the record from the workFlow table by using getFinanceMarginSlabDAO().delete with parameters financeMarginSlab,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFCMTFinanceMarginSlab by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFCMTFinanceMarginSlab by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		FinanceMarginSlab financeMarginSlab = new FinanceMarginSlab();
		BeanUtils.copyProperties((FinanceMarginSlab) auditHeader.getAuditDetail().getModelData(), financeMarginSlab);

		if (financeMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getFinanceMarginSlabDAO().delete(financeMarginSlab,"");

		} else {
			financeMarginSlab.setRoleCode("");
			financeMarginSlab.setNextRoleCode("");
			financeMarginSlab.setTaskId("");
			financeMarginSlab.setNextTaskId("");
			financeMarginSlab.setWorkflowId(0);

			if (financeMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				financeMarginSlab.setRecordType("");
				getFinanceMarginSlabDAO().save(financeMarginSlab,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				financeMarginSlab.setRecordType("");
				getFinanceMarginSlabDAO().update(financeMarginSlab,"");
			}
		}

		getFinanceMarginSlabDAO().delete(financeMarginSlab,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeMarginSlab);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getFinanceMarginSlabDAO().delete with parameters financeMarginSlab,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFCMTFinanceMarginSlab by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinanceMarginSlab financeMarginSlab = (FinanceMarginSlab) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceMarginSlabDAO().delete(financeMarginSlab,"_TEMP");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

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
		logger.debug("Entering");
		auditHeader = getMarginSlabValidation().marginSlabValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

}