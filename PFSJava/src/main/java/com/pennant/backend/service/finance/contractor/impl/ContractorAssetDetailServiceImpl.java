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
 * FileName    		:  ContractorAssetDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.finance.contractor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.contractor.ContractorAssetDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>ContractorAssetDetail</b>.<br>
 * 
 */
public class ContractorAssetDetailServiceImpl extends GenericService<ContractorAssetDetail> implements ContractorAssetDetailService {
	private static final Logger logger = Logger.getLogger(ContractorAssetDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ContractorAssetDetailDAO contractorAssetDetailDAO;

	public ContractorAssetDetailServiceImpl() {
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
	 * @return the contractorAssetDetailDAO
	 */
	public ContractorAssetDetailDAO getContractorAssetDetailDAO() {
		return contractorAssetDetailDAO;
	}
	/**
	 * @param contractorAssetDetailDAO the contractorAssetDetailDAO to set
	 */
	public void setContractorAssetDetailDAO(ContractorAssetDetailDAO contractorAssetDetailDAO) {
		this.contractorAssetDetailDAO = contractorAssetDetailDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinContractorAstDtls/FinContractorAstDtls_Temp 
	 * 			by using ContractorAssetDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ContractorAssetDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table FinContractorAstDtls/FinContractorAstDtls_Temp 
	 * 			by using ContractorAssetDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ContractorAssetDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */


	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) auditHeader.getAuditDetail().getModelData();

		if (contractorAssetDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (contractorAssetDetail.isNew()) {
			getContractorAssetDetailDAO().save(contractorAssetDetail,tableType);
		}else{
			getContractorAssetDetailDAO().update(contractorAssetDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinContractorAstDtls by using ContractorAssetDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) auditHeader.getAuditDetail().getModelData();
		getContractorAssetDetailDAO().delete(contractorAssetDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getContractorAssetDetailById fetch the details by using ContractorAssetDetailDAO's getContractorAssetDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ContractorAssetDetail
	 */

	@Override
	public ContractorAssetDetail getContractorAssetDetailById(String finReference, long contractorId) {
		return getContractorAssetDetailDAO().getContractorAssetDetailById(finReference, contractorId, "_View");
	}
	/**
	 * getApprovedContractorAssetDetailById fetch the details by using ContractorAssetDetailDAO's getContractorAssetDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinContractorAstDtls.
	 * @param id (String)
	 * @return ContractorAssetDetail
	 */

	public ContractorAssetDetail getApprovedContractorAssetDetailById(String finReference, long contractorId) {
		return getContractorAssetDetailDAO().getContractorAssetDetailById(finReference, contractorId,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getContractorAssetDetailDAO().delete with parameters contractorAssetDetail,""
	 * 		b)  NEW		Add new record in to main table by using getContractorAssetDetailDAO().save with parameters contractorAssetDetail,""
	 * 		c)  EDIT	Update record in the main table by using getContractorAssetDetailDAO().update with parameters contractorAssetDetail,""
	 * 3)	Delete the record from the workFlow table by using getContractorAssetDetailDAO().delete with parameters contractorAssetDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ContractorAssetDetail contractorAssetDetail = new ContractorAssetDetail();
		BeanUtils.copyProperties((ContractorAssetDetail) auditHeader.getAuditDetail().getModelData(), contractorAssetDetail);

		if (contractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getContractorAssetDetailDAO().delete(contractorAssetDetail,"");

		} else {
			contractorAssetDetail.setRoleCode("");
			contractorAssetDetail.setNextRoleCode("");
			contractorAssetDetail.setTaskId("");
			contractorAssetDetail.setNextTaskId("");
			contractorAssetDetail.setWorkflowId(0);

			if (contractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				contractorAssetDetail.setRecordType("");
				getContractorAssetDetailDAO().save(contractorAssetDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				contractorAssetDetail.setRecordType("");
				getContractorAssetDetailDAO().update(contractorAssetDetail,"");
			}
		}

		getContractorAssetDetailDAO().delete(contractorAssetDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(contractorAssetDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getContractorAssetDetailDAO().delete with parameters contractorAssetDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinContractorAstDtls by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getContractorAssetDetailDAO().delete(contractorAssetDetail,"_Temp");

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


	private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getContractorAssetDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ContractorAssetDetail contractorAssetDetail= (ContractorAssetDetail) auditDetail.getModelData();

		ContractorAssetDetail tempContractorAssetDetail= null;
		if (contractorAssetDetail.isWorkflow()){
			tempContractorAssetDetail = getContractorAssetDetailDAO().getContractorAssetDetailById(contractorAssetDetail.getFinReference(), contractorAssetDetail.getContractorId(),  "_Temp");
		}
		ContractorAssetDetail befContractorAssetDetail= getContractorAssetDetailDAO().getContractorAssetDetailById(contractorAssetDetail.getFinReference(), contractorAssetDetail.getContractorId(), "");

		ContractorAssetDetail oldContractorAssetDetail= contractorAssetDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(contractorAssetDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (contractorAssetDetail.isNew()){ // for New record or new record into work flow

			if (!contractorAssetDetail.isWorkflow()){// With out Work flow only new records  
				if (befContractorAssetDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (contractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befContractorAssetDetail !=null || tempContractorAssetDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befContractorAssetDetail ==null || tempContractorAssetDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!contractorAssetDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befContractorAssetDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldContractorAssetDetail!=null && !oldContractorAssetDetail.getLastMntOn().equals(befContractorAssetDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempContractorAssetDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldContractorAssetDetail!=null && !oldContractorAssetDetail.getLastMntOn().equals(tempContractorAssetDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !contractorAssetDetail.isWorkflow()){
			auditDetail.setBefImage(befContractorAssetDetail);	
		}

		return auditDetail;
	}

	@Override
	public List<ContractorAssetDetail> getContractorAssetDetailList(String finReference,  String tableType) {
		return getContractorAssetDetailDAO().getContractorDetailDetailByFinRef(finReference, "");
	}

	@Override
	public void setContractorAssetDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		financeDetail.setContractorAssetDetails(getContractorAssetDetailDAO().getContractorDetailDetailByFinRef(finReference, tableType));
		logger.debug("Leaving");
	}

	@Override
	public List<AuditDetail> saveOrUpdate(String finReference, List<ContractorAssetDetail> assetDetails, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (ContractorAssetDetail assetDetail : assetDetails) {
			assetDetail.setWorkflowId(0);
			assetDetail.setFinReference(finReference);

			if (assetDetail.isNewRecord()) {
				getContractorAssetDetailDAO().save(assetDetail, tableType);
			} else {
				getContractorAssetDetailDAO().update(assetDetail, tableType);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(assetDetail, assetDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], assetDetail.getBefImage(), assetDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<ContractorAssetDetail> assetDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (ContractorAssetDetail assetDetail : assetDetails) {
			ContractorAssetDetail detail = new ContractorAssetDetail();
			BeanUtils.copyProperties(assetDetail, detail);

			assetDetail.setRoleCode("");
			assetDetail.setNextRoleCode("");
			assetDetail.setTaskId("");
			assetDetail.setNextTaskId("");
			assetDetail.setWorkflowId(0);

			getContractorAssetDetailDAO().save(assetDetail, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(assetDetail, assetDetail.getExcludeFields());

			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], assetDetail.getBefImage(), assetDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<ContractorAssetDetail> assetDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if(assetDetails != null && !assetDetails.isEmpty()){

			for (ContractorAssetDetail assetDetail : assetDetails) {
				getContractorAssetDetailDAO().delete(assetDetail, tableType);

				String[] fields = PennantJavaUtil.getFieldDetails(assetDetail, assetDetail.getExcludeFields());
				auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], assetDetail.getBefImage(), assetDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validate(FinanceDetail financeDetail, String method,  String usrLanguage) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails  = financeDetail.getAuditDetailMap().get("ContractorAssetDetail");
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		logger.debug("Leaving");
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) auditDetail.getModelData();
		ContractorAssetDetail tempContractorAssetDetail = null;
		if (contractorAssetDetail.isWorkflow()){
			tempContractorAssetDetail = getContractorAssetDetailDAO().getContractorAssetDetailById(contractorAssetDetail.getFinReference(), contractorAssetDetail.getContractorId(), "_Temp");
		}
		ContractorAssetDetail befContractorAssetDetail = getContractorAssetDetailDAO().getContractorAssetDetailById( contractorAssetDetail.getFinReference(), contractorAssetDetail.getContractorId(), "");

		ContractorAssetDetail oldContractorAssetDetail = contractorAssetDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=contractorAssetDetail.getFinReference();
		errParm[0]= PennantJavaUtil.getLabel("label_loanReferenceNumber")+":"+valueParm[0];

		if (contractorAssetDetail.isNew()){ // for New record or new record into work flow

			if (!contractorAssetDetail.isWorkflow()){// With out Work flow only new records  
				if (befContractorAssetDetail != null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (contractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befContractorAssetDetail !=null || tempContractorAssetDetail != null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befContractorAssetDetail ==null || tempContractorAssetDetail !=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!contractorAssetDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befContractorAssetDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldContractorAssetDetail!=null && !oldContractorAssetDetail.getLastMntOn().equals(befContractorAssetDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempContractorAssetDetail == null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempContractorAssetDetail!=null && oldContractorAssetDetail != null && ! oldContractorAssetDetail.getLastMntOn().equals(tempContractorAssetDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !contractorAssetDetail.isWorkflow()){
			auditDetail.setBefImage(befContractorAssetDetail);	
		}
		return auditDetail;
	}

	@Override
	public List<AuditDetail> getAuditDetail(List<ContractorAssetDetail> contractorAssetDetails, FinanceMain financeMain, String auditTranType, String method) {
		logger.debug("Entering");

		ContractorAssetDetail object = new ContractorAssetDetail();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < contractorAssetDetails.size(); i++) {

			ContractorAssetDetail contractorAssetDetail = contractorAssetDetails.get(i);
			contractorAssetDetail.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (contractorAssetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				contractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (contractorAssetDetail.getRecordType().equalsIgnoreCase(  PennantConstants.RCD_UPD)) {
				contractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (contractorAssetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				contractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				contractorAssetDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (contractorAssetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (contractorAssetDetail.getRecordType().equalsIgnoreCase( PennantConstants.RECORD_TYPE_DEL) || contractorAssetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			contractorAssetDetail.setRecordStatus(financeMain.getRecordStatus());
			contractorAssetDetail.setUserDetails(financeMain.getUserDetails());
			contractorAssetDetail.setLastMntOn(financeMain.getLastMntOn());

			if (StringUtils.isNotEmpty(contractorAssetDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],  contractorAssetDetail.getBefImage(), contractorAssetDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}


}