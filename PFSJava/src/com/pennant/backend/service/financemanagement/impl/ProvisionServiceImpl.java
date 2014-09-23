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
 * FileName    		:  ProvisionServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.financemanagement.impl;



import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ProvisionCalculationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.financemanagement.ProvisionMovementDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;

/**
 * Service implementation for methods that depends on <b>Provision</b>.<br>
 * 
 */
public class ProvisionServiceImpl extends GenericService<Provision> implements ProvisionService {

	private final static Logger logger = Logger.getLogger(ProvisionServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProvisionDAO provisionDAO;
	private ProvisionMovementDAO provisionMovementDAO;
	private FinanceTypeDAO financeTypeDAO;
	private ProvisionCalculationUtil provisionCalculationUtil;
	private FinanceProfitDetailDAO financeProfitDetailDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ProvisionDAO getProvisionDAO() {
		return provisionDAO;
	}
	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

	public ProvisionMovementDAO getProvisionMovementDAO() {
		return provisionMovementDAO;
	}
	public void setProvisionMovementDAO(ProvisionMovementDAO provisionMovementDAO) {
		this.provisionMovementDAO = provisionMovementDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public ProvisionCalculationUtil getProvisionCalculationUtil() {
		return provisionCalculationUtil;
	}
	public void setProvisionCalculationUtil(ProvisionCalculationUtil provisionCalculationUtil) {
		this.provisionCalculationUtil = provisionCalculationUtil;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Override
	public Provision getProvision() {
		return getProvisionDAO().getProvision();
	}

	@Override
	public Provision getNewProvision() {
		return getProvisionDAO().getNewProvision();
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinProvisions/FinProvisions_Temp 
	 * 			by using ProvisionDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ProvisionDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		if (provision.isWorkflow()) {
			tableType="_TEMP";
		}

		if(!provision.isWorkflow()){

			//Check Finance is RIA Finance Type or Not
			boolean isRIAFinance = getFinanceTypeDAO().checkRIAFinance(provision.getFinType());

			Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
			getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, false, true, isRIAFinance);
		}else{

			if (provision.isNew()) {
				getProvisionDAO().save(provision,tableType);
			}else{
				getProvisionDAO().update(provision,tableType);
			}

		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinProvisions by using ProvisionDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader)    
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

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		getProvisionDAO().delete(provision,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProvisionById fetch the details by using ProvisionDAO's getProvisionById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return Provision
	 */
	@Override
	public Provision getProvisionById(String id,boolean isEnquiry) {
		Provision provision = getProvisionDAO().getProvisionById(id,"_View");
		if(provision != null && isEnquiry){
			provision.setProvisionMovementList(getProvisionMovementDAO().getProvisionMovementListById(id, "_AView"));
		}
		return provision;
	}

	/**
	 * getApprovedProvisionById fetch the details by using ProvisionDAO's getProvisionById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinProvisions.
	 * @param id (String)
	 * @return Provision
	 */
	public Provision getApprovedProvisionById(String id) {
		return getProvisionDAO().getProvisionById(id,"_AView");
	}

	@Override
	public FinanceProfitDetail getProfitDetailById(String finReference){
		return getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * This method refresh the Record.
	 * @param Provision (provision)
	 * @return provision
	 */
	@Override
	public Provision refresh(Provision provision) {
		logger.debug("Entering");
		getProvisionDAO().refresh(provision);
		getProvisionDAO().initialize(provision);
		logger.debug("Leaving");
		return provision;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getProvisionDAO().delete with parameters provision,""
	 * 		b)  NEW		Add new record in to main table by using getProvisionDAO().save with parameters provision,""
	 * 		c)  EDIT	Update record in the main table by using getProvisionDAO().update with parameters provision,""
	 * 3)	Delete the record from the workFlow table by using getProvisionDAO().delete with parameters provision,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Provision provision = new Provision();
		BeanUtils.copyProperties((Provision) auditHeader.getAuditDetail().getModelData(), provision);

		//Check Finance is RIA Finance Type or Not
		boolean isRIAFinance = getFinanceTypeDAO().checkRIAFinance(provision.getFinType());

		provision.setRecordType("");
		provision.setRoleCode("");
		provision.setNextRoleCode("");
		provision.setTaskId("");
		provision.setNextTaskId("");
		provision.setWorkflowId(0);

		//Provision Postings Process
		Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		getProvisionCalculationUtil().processProvCalculations(provision, dateValueDate, false, true, isRIAFinance);

		getProvisionDAO().delete(provision,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provision);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getProvisionDAO().delete with parameters provision,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinProvisions by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProvisionDAO().delete(provision,"_TEMP");

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
	 * 5)	for any mismatch conditions Fetch the error details from getProvisionDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
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

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		Provision provision= (Provision) auditDetail.getModelData();

		Provision tempProvision= null;
		if (provision.isWorkflow()){
			tempProvision = getProvisionDAO().getProvisionById(provision.getId(), "_Temp");
		}
		Provision befProvision= getProvisionDAO().getProvisionById(provision.getId(), "");
		Provision oldProvision= provision.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=provision.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (provision.isNew()){ // for New record or new record into work flow

			if (!provision.isWorkflow()){// With out Work flow only new records  
				if (befProvision !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befProvision !=null || tempProvision!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befProvision ==null || tempProvision!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!provision.isWorkflow()){	// With out Work flow for update and delete

				if (befProvision ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldProvision!=null && !oldProvision.getLastMntOn().equals(befProvision.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempProvision==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldProvision!=null && !oldProvision.getLastMntOn().equals(tempProvision.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !provision.isWorkflow()){
			provision.setBefImage(befProvision);	
		}

		return auditDetail;
	}

}