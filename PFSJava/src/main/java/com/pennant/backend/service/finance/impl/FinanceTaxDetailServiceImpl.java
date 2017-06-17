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
 * FileName    		:  FinanceTaxDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2017    														*
 *                                                                  						*
 * Modified Date    :  17-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;


/**
 * Service implementation for methods that depends on <b>FinanceTaxDetail</b>.<br>
 */
public class FinanceTaxDetailServiceImpl extends GenericService<FinanceTaxDetail> implements FinanceTaxDetailService {
	private final static Logger logger = Logger.getLogger(FinanceTaxDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;


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
	 * @return the financeTaxDetailDAO
	 */
	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}
	/**
	 * @param financeTaxDetailDAO the financeTaxDetailDAO to set
	 */
	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * FinTaxDetail/FinTaxDetail_Temp by using FinTaxDetailDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTaxDetailDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtFinTaxDetail by using
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

		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (financeTaxDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (financeTaxDetail.isNew()) {
			getFinanceTaxDetailDAO().save(financeTaxDetail,tableType);
		}else{
			getFinanceTaxDetailDAO().update(financeTaxDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table FinTaxDetail by using FinTaxDetailDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtFinTaxDetail by using
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
		
		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
		getFinanceTaxDetailDAO().delete(financeTaxDetail,TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getFinTaxDetail fetch the details by using FinTaxDetailDAO's getFinTaxDetailById
	 * method.
	 * 
	 * @param finReference
	 *            finReference of the FinanceTaxDetail.
	 * @return FinTaxDetail
	 */
	@Override
	public FinanceTaxDetail getFinanceTaxDetail(String finReference) {
		return getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference,"_View");
	}

	/**
	 * getApprovedFinTaxDetailById fetch the details by using FinTaxDetailDAO's
	 * getFinTaxDetailById method . with parameter id and type as blank. it fetches
	 * the approved records from the FinTaxDetail.
	 * 
	 * @param finReference
	 *            finReference of the FinanceTaxDetail.
	 *            (String)
	 * @return FinTaxDetail
	 */
	public FinanceTaxDetail getApprovedFinanceTaxDetail(String finReference) {
		return getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference,"_AView");
	}	
		
	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFinanceTaxDetailDAO().delete with parameters financeTaxDetail,"" b) NEW Add new
	 * record in to main table by using getFinanceTaxDetailDAO().save with parameters
	 * financeTaxDetail,"" c) EDIT Update record in the main table by using
	 * getFinanceTaxDetailDAO().update with parameters financeTaxDetail,"" 3) Delete the record
	 * from the workFlow table by using getFinanceTaxDetailDAO().delete with parameters
	 * financeTaxDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtFinTaxDetail by using
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

		FinanceTaxDetail financeTaxDetail = new FinanceTaxDetail();
		BeanUtils.copyProperties((FinanceTaxDetail) auditHeader.getAuditDetail().getModelData(), financeTaxDetail);

		getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.TEMP_TAB);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(financeTaxDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(financeTaxDetailDAO.getFinanceTaxDetail(financeTaxDetail.getFinReference(), ""));
		}

		if (financeTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.MAIN_TAB);
		} else {
			financeTaxDetail.setRoleCode("");
			financeTaxDetail.setNextRoleCode("");
			financeTaxDetail.setTaskId("");
			financeTaxDetail.setNextTaskId("");
			financeTaxDetail.setWorkflowId(0);

			if (financeTaxDetail.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeTaxDetail.setRecordType("");
				getFinanceTaxDetailDAO().save(financeTaxDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeTaxDetail.setRecordType("");
				getFinanceTaxDetailDAO().update(financeTaxDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeTaxDetail);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.info(Literal.LEAVING);
		return auditHeader;
		
		}

		/**
		 * doReject method do the following steps. 1) Do the Business validation by
		 * using businessValidation(auditHeader) method if there is any error or
		 * warning message then return the auditHeader. 2) Delete the record from
		 * the workFlow table by using getFinanceTaxDetailDAO().delete with parameters
		 * financeTaxDetail,"_Temp" 3) Audit the record in to AuditHeader and
		 * AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader) for Work
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

			FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getFinanceTaxDetailDAO().delete(financeTaxDetail,TableType.TEMP_TAB);
			
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
		 * from getFinanceTaxDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
		 * the to auditDeail Object
		 * 
		 * @param auditDetail
		 * @param usrLanguage
		 * @return
		 */
		
		private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug(Literal.ENTERING);
			
			// Write the required validation over hear.
			
			
			logger.debug(Literal.LEAVING);
			return auditDetail;
		}

}