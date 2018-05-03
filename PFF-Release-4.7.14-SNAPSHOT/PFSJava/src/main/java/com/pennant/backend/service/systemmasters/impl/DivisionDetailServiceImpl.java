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
 * FileName    		:  DivisionDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>DivisionDetail</b>.<br>
 * 
 */
public class DivisionDetailServiceImpl extends GenericService<DivisionDetail> implements DivisionDetailService {
	private static final Logger logger = Logger.getLogger(DivisionDetailServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private DivisionDetailDAO divisionDetailDAO;
	
	private FinanceTypeDAO financeTypeDAO;

	public DivisionDetailServiceImpl() {
		super();
	}
	
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
	 * @return the divisionDetailDAO
	 */
	public DivisionDetailDAO getDivisionDetailDAO() {
		return divisionDetailDAO;
	}
	/**
	 * @param divisionDetailDAO the divisionDetailDAO to set
	 */
	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SMTDivisionDetail/SMTDivisionDetail_Temp 
	 * 			by using DivisionDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DivisionDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SMTDivisionDetail/SMTDivisionDetail_Temp 
	 * 			by using DivisionDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using DivisionDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (divisionDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		
		if (divisionDetail.isNew()) {
			getDivisionDetailDAO().save(divisionDetail,tableType);
		}else{
			getDivisionDetailDAO().update(divisionDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SMTDivisionDetail by using DivisionDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
		getDivisionDetailDAO().delete(divisionDetail, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDivisionDetailById fetch the details by using DivisionDetailDAO's getDivisionDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return DivisionDetail
	 */
	
	@Override
	public DivisionDetail getDivisionDetailById(String id) {
		return getDivisionDetailDAO().getDivisionDetailById(id,"_View");
	}
	/**
	 * getApprovedDivisionDetailById fetch the details by using DivisionDetailDAO's getDivisionDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the SMTDivisionDetail.
	 * @param id (String)
	 * @return DivisionDetail
	 */
	
	public DivisionDetail getApprovedDivisionDetailById(String id) {
		return getDivisionDetailDAO().getDivisionDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDivisionDetailDAO().delete with
	 * parameters divisionDetail,"" b) NEW Add new record in to main table by using getDivisionDetailDAO().save with
	 * parameters divisionDetail,"" c) EDIT Update record in the main table by using getDivisionDetailDAO().update with
	 * parameters divisionDetail,"" 3) Delete the record from the workFlow table by using getDivisionDetailDAO().delete
	 * with parameters divisionDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtSMTDivisionDetail by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSMTDivisionDetail
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		DivisionDetail divisionDetail = new DivisionDetail();
		BeanUtils.copyProperties((DivisionDetail) auditHeader.getAuditDetail().getModelData(), divisionDetail);
		
		getDivisionDetailDAO().delete(divisionDetail, TableType.TEMP_TAB);
		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(divisionDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(divisionDetailDAO.getDivisionDetailById(divisionDetail.getDivisionCode(), ""));
		}

		if (divisionDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDivisionDetailDAO().delete(divisionDetail, TableType.MAIN_TAB);

		} else {
			divisionDetail.setRoleCode("");
			divisionDetail.setNextRoleCode("");
			divisionDetail.setTaskId("");
			divisionDetail.setNextTaskId("");
			divisionDetail.setWorkflowId(0);

			if (divisionDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				divisionDetail.setRecordType("");
				getDivisionDetailDAO().save(divisionDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				divisionDetail.setRecordType("");
				getDivisionDetailDAO().update(divisionDetail,TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(divisionDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getDivisionDetailDAO().delete with parameters divisionDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSMTDivisionDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doReject");
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			DivisionDetail divisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getDivisionDetailDAO().delete(divisionDetail, TableType.TEMP_TAB);
			
			getAuditHeaderDAO().addAudit(auditHeader);
			logger.debug("Leaving");
			
			return auditHeader;
		}

		/**
		 * businessValidation method do the following steps.
		 * 1)	validate the audit detail 
		 * 2)	if any error/Warnings  then assign the to auditHeader
		 * 3)   identify the nextprocess
		 *  
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */

		
		private AuditHeader businessValidation(AuditHeader auditHeader, String method){
			logger.debug("Entering");
		    AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
			auditHeader.setAuditDetail(auditDetail);
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader=nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

	/**
	 * Validation method with the following steps.<br/>
	 * 1) get the details from the auditHeader.<br/>
	 * 2) Validate the data.<br/>
	 * 3) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 * @param usrLanguage
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,String method) {
		logger.debug("Entering");

		// Get the model object.
		DivisionDetail divisionDetail = (DivisionDetail) auditDetail.getModelData();
		String code = divisionDetail.getDivisionCode();

		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_DivisionCode") + ": " + code;
		
		// Check the unique keys.
		if (divisionDetail.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(divisionDetail.getRecordType())
				&& divisionDetailDAO.isDuplicateKey(code, divisionDetail.isWorkflow() ? TableType.BOTH_TAB
						: TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", parameters, null));
		}
		
		if (!StringUtils.equals(method, PennantConstants.method_doReject)
				&& PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(divisionDetail.getRecordType())) {
			//FinanceType Details
			boolean isdivisionExists = getFinanceTypeDAO().isDivisionCodeExistsInFinanceTypes(divisionDetail.getDivisionCode(), "_View");

			if (isdivisionExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

}