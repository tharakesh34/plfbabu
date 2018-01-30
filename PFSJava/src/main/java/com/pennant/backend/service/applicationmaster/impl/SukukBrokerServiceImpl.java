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
 * FileName    		:  SukukBrokerServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.SukukBrokerBondsDAO;
import com.pennant.backend.dao.applicationmaster.SukukBrokerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmasters.SukukBroker;
import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.SukukBrokerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>SukukBroker</b>.<br>
 * 
 */
public class SukukBrokerServiceImpl extends GenericService<SukukBroker> implements SukukBrokerService {
	private static final Logger logger = Logger.getLogger(SukukBrokerServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private SukukBrokerDAO sukukBrokerDAO;
	private SukukBrokerBondsDAO sukukBrokerBondsDAO;

	public SukukBrokerServiceImpl() {
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
	 * @return the sukukBrokerDAO
	 */
	public SukukBrokerDAO getSukukBrokerDAO() {
		return sukukBrokerDAO;
	}
	/**
	 * @param sukukBrokerDAO the sukukBrokerDAO to set
	 */
	public void setSukukBrokerDAO(SukukBrokerDAO sukukBrokerDAO) {
		this.sukukBrokerDAO = sukukBrokerDAO;
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SukukBrokers/SukukBrokers_Temp 
	 * 			by using SukukBrokerDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SukukBrokerDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSukukBrokers by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table SukukBrokers/SukukBrokers_Temp 
	 * 			by using SukukBrokerDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SukukBrokerDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSukukBrokers by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		SukukBroker sukukBroker = (SukukBroker) auditHeader.getAuditDetail().getModelData();
		
		if (sukukBroker.isWorkflow()) {
			tableType="_Temp";
		}

		if (sukukBroker.isNew()) {
			getSukukBrokerDAO().save(sukukBroker,tableType);
		}else{
			getSukukBrokerDAO().update(sukukBroker,tableType);
		}

		List<AuditDetail>	details = processBrokerBond(sukukBroker, tableType);
		if (details!=null) {
			auditDetails.addAll(details);
        }
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table SukukBrokers by using SukukBrokerDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtSukukBrokers by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		SukukBroker sukukBroker = (SukukBroker) auditHeader.getAuditDetail().getModelData();
		getSukukBrokerDAO().delete(sukukBroker,"");
		
		auditHeader.setAuditDetails(listDeletion(sukukBroker, "", auditHeader.getAuditTranType()));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSukukBrokerById fetch the details by using SukukBrokerDAO's getSukukBrokerById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return SukukBroker
	 */
	
	@Override
	public SukukBroker getSukukBrokerById(String id) {
		SukukBroker sukukBroker= getSukukBrokerDAO().getSukukBrokerById(id,"_View");
		sukukBroker.setSukukBrokerBonds(getSukukBrokerBondsDAO().getSukukBrokerBondsByCode(sukukBroker.getBrokerCode(),"_View"));
		return sukukBroker;
	}
	/**
	 * getApprovedSukukBrokerById fetch the details by using SukukBrokerDAO's getSukukBrokerById method .
	 * with parameter id and type as blank. it fetches the approved records from the SukukBrokers.
	 * @param id (String)
	 * @return SukukBroker
	 */
	
	public SukukBroker getApprovedSukukBrokerById(String id) {
		SukukBroker sukukBroker= getSukukBrokerDAO().getSukukBrokerById(id,"_AView");
		sukukBroker.setSukukBrokerBonds(getSukukBrokerBondsDAO().getSukukBrokerBondsByCode(sukukBroker.getBrokerCode(),"_AView"));
		return sukukBroker;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getSukukBrokerDAO().delete with
	 * parameters sukukBroker,"" b) NEW Add new record in to main table by using getSukukBrokerDAO().save with
	 * parameters sukukBroker,"" c) EDIT Update record in the main table by using getSukukBrokerDAO().update with
	 * parameters sukukBroker,"" 3) Delete the record from the workFlow table by using getSukukBrokerDAO().delete with
	 * parameters sukukBroker,"_Temp" 4) Audit the record in to AuditHeader and AdtSukukBrokers by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtSukukBrokers by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		SukukBroker sukukBroker = new SukukBroker();
		BeanUtils.copyProperties((SukukBroker) auditHeader.getAuditDetail().getModelData(), sukukBroker);

		if (sukukBroker.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			if(sukukBroker.getSukukBrokerBonds() != null && !sukukBroker.getSukukBrokerBonds().isEmpty()){
				auditDetails.addAll(listDeletion(sukukBroker, "", auditHeader.getAuditTranType()));	
			}
			getSukukBrokerDAO().delete(sukukBroker, "");

		} else {
			sukukBroker.setRoleCode("");
			sukukBroker.setNextRoleCode("");
			sukukBroker.setTaskId("");
			sukukBroker.setNextTaskId("");
			sukukBroker.setWorkflowId(0);

			if (sukukBroker.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				sukukBroker.setRecordType("");
				getSukukBrokerDAO().save(sukukBroker, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				sukukBroker.setRecordType("");
				getSukukBrokerDAO().update(sukukBroker, "");
			}

			List<AuditDetail> details = processBrokerBond(sukukBroker, "");
			if (details != null) {
				auditDetails.addAll(details);
			}
		}

		getSukukBrokerDAO().delete(sukukBroker, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		if(sukukBroker.getSukukBrokerBonds() != null && !sukukBroker.getSukukBrokerBonds().isEmpty()){
			auditHeader.setAuditDetails(listDeletion(sukukBroker, "_Temp", auditHeader.getAuditTranType()));
		}
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, sukukBroker.getBefImage(),
				sukukBroker));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(sukukBroker);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, sukukBroker.getBefImage(),
				sukukBroker));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSukukBrokerDAO().delete with parameters sukukBroker,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtSukukBrokers by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			SukukBroker sukukBroker = (SukukBroker) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getSukukBrokerDAO().delete(sukukBroker,"_Temp");
			
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, sukukBroker.getBefImage(), sukukBroker));
			auditHeader.setAuditDetails(listDeletion(sukukBroker, "_Temp", auditHeader.getAuditTranType()));
			
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

			SukukBroker sukukBroker = (SukukBroker) auditHeader.getAuditDetail().getModelData();
			
			if (sukukBroker.getSukukBrokerBonds() != null && sukukBroker.getSukukBrokerBonds().size() > 0) {

				HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

				List<AuditDetail> auditDetails= setBrokerbondAuditData(sukukBroker, method);				
				
				auditDetails = brokerbondListValidation(auditDetails, method, sukukBroker.getUserDetails().getLanguage());
				auditDetailMap.put("SukukBrokerBonds", auditDetails);
				auditHeader.setAuditDetails(auditDetails);				
				sukukBroker.setAuditDetailMap(auditDetailMap);
			}

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
		 * 5)	for any mismatch conditions Fetch the error details from getSukukBrokerDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			SukukBroker sukukBroker= (SukukBroker) auditDetail.getModelData();
			
			SukukBroker tempSukukBroker= null;
			if (sukukBroker.isWorkflow()){
				tempSukukBroker = getSukukBrokerDAO().getSukukBrokerById(sukukBroker.getId(), "_Temp");
			}
			SukukBroker befSukukBroker= getSukukBrokerDAO().getSukukBrokerById(sukukBroker.getId(), "");
			
			SukukBroker oldSukukBroker= sukukBroker.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=sukukBroker.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_BrokerCode")+":"+valueParm[0];
			
			if (sukukBroker.isNew()){ // for New record or new record into work flow
				
				if (!sukukBroker.isWorkflow()){// With out Work flow only new records  
					if (befSukukBroker !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (sukukBroker.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befSukukBroker !=null || tempSukukBroker!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befSukukBroker ==null || tempSukukBroker!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!sukukBroker.isWorkflow()){	// With out Work flow for update and delete
				
					if (befSukukBroker ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldSukukBroker!=null && !oldSukukBroker.getLastMntOn().equals(befSukukBroker.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempSukukBroker==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempSukukBroker!=null  && oldSukukBroker!=null && !oldSukukBroker.getLastMntOn().equals(tempSukukBroker.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !sukukBroker.isWorkflow()){
				auditDetail.setBefImage(befSukukBroker);	
			}

			return auditDetail;
		}
		

		/**
		 * Methods for Creating List of Audit Details with detailed fields
		 * 
		 * @param customerDetails
		 * @param auditTranType
		 * @param method
		 * @return
		 */
		private List<AuditDetail> setBrokerbondAuditData(SukukBroker sukukBroker,  String method) {
			logger.debug("Entering");

			String auditTranType = "";

			if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
				if (sukukBroker.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}

			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			String[] fields = PennantJavaUtil.getFieldDetails(new SukukBrokerBonds(),new SukukBrokerBonds().getExcludeFields());

			int count=0;
			for (SukukBrokerBonds sukukBrokerBonds : sukukBroker.getSukukBrokerBonds()) {
				
				String rcdType=sukukBrokerBonds.getRecordType();

				if (StringUtils.isEmpty(rcdType)) {
					continue;
				}

				sukukBrokerBonds.setWorkflowId(sukukBroker.getWorkflowId());
				sukukBrokerBonds.setBrokerCode(sukukBroker.getBrokerCode());

				boolean isRcdType = false;

				if (rcdType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				sukukBrokerBonds.setNewRecord(true);
			}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}

				sukukBrokerBonds.setRecordStatus(sukukBroker.getRecordStatus());
				sukukBrokerBonds.setUserDetails(sukukBroker.getUserDetails());
				sukukBrokerBonds.setLastMntOn(sukukBroker.getLastMntOn());

				auditDetails.add(new AuditDetail(auditTranType, count++, fields[0], fields[1],
						sukukBrokerBonds.getBefImage(), sukukBrokerBonds));

			}

			logger.debug("Leaving");
			return auditDetails;
		}
		
		public List<AuditDetail> brokerbondListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
			
			
			for (AuditDetail auditDetail : auditDetails) {
				
				SukukBrokerBonds sukukBrokerBonds= (SukukBrokerBonds) auditDetail.getModelData();
				SukukBrokerBonds tempFeeTier= null;
				if (sukukBrokerBonds.isWorkflow()){
					tempFeeTier = getSukukBrokerBondsDAO().getSukukBrokerBondsById(sukukBrokerBonds.getBrokerCode(),sukukBrokerBonds.getBondCode(),"_Temp");
				}
				
				SukukBrokerBonds beftransactionEntry=  getSukukBrokerBondsDAO().getSukukBrokerBondsById(sukukBrokerBonds.getBrokerCode(),sukukBrokerBonds.getBondCode(),"");
				SukukBrokerBonds oldTransactionEntry= sukukBrokerBonds.getBefImage();
				
				String[] valueParm = new String[3];
				String[] errParm = new String[3];

				valueParm[0] = sukukBrokerBonds.getBrokerCode();
				valueParm[1] = sukukBrokerBonds.getBondCode();
				
				errParm[0] = PennantJavaUtil.getLabel("label_BrokerCode") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_BondCode") + ":"+ valueParm[1];
				
				if (sukukBrokerBonds.isNew()){ // for New record or new record into work flow

					if (!sukukBrokerBonds.isWorkflow()){// With out Work flow only new records  
						if (beftransactionEntry !=null){	// Record Already Exists in the table then error  
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
						}	
					}else{ // with work flow

						if (sukukBrokerBonds.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
							if (beftransactionEntry !=null || tempFeeTier!=null ){ // if records already exists in the main table
								auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
							}
						}else{ // if records not exists in the Main flow table
							if (beftransactionEntry ==null || tempFeeTier!=null ){
								auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
							}
						}
					}
				}else{
					// for work flow process records or (Record to update or Delete with out work flow)
					if (!sukukBrokerBonds.isWorkflow()){	// With out Work flow for update and delete

						if (beftransactionEntry ==null){ // if records not exists in the main table
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
						}else{

							if (oldTransactionEntry!=null && !oldTransactionEntry.getLastMntOn().equals(beftransactionEntry.getLastMntOn())){
								if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
									auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
								}else{
									auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
								}
							}
						}

					}else{

						if (tempFeeTier==null ){ // if records not exists in the Work flow table 
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
						}

						if (tempFeeTier!=null  && oldTransactionEntry!=null && !oldTransactionEntry.getLastMntOn().equals(tempFeeTier.getLastMntOn())){ 
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
						}

					}
				}

				auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

				if("doApprove".equals(StringUtils.trimToEmpty(method)) || !sukukBrokerBonds.isWorkflow()){
					auditDetail.setBefImage(beftransactionEntry);	
				}
			
            }
			return auditDetails;
		}

		
		

		/**
		 * Method For Preparing List of AuditDetails for Customer Ratings
		 * 
		 * @param auditDetails
		 * @param type
		 * @param custId
		 * @return
		 */
		private List<AuditDetail> processBrokerBond(SukukBroker sukukBroker, String type) {
			logger.debug("Entering");

			List<AuditDetail> auditDetails = sukukBroker.getAuditDetailMap().get("SukukBrokerBonds");
			
			if (auditDetails == null || auditDetails.isEmpty()) {
	            return auditDetails;
            }
			
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (AuditDetail auditDetail : auditDetails) {
				SukukBrokerBonds sukukBrokerBonds = (SukukBrokerBonds)auditDetail.getModelData();
				
				sukukBrokerBonds.setBrokerCode(sukukBroker.getBrokerCode());
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type)) {
					approveRec = true;
					sukukBrokerBonds.setRoleCode("");
					sukukBrokerBonds.setNextRoleCode("");
					sukukBrokerBonds.setTaskId("");
					sukukBrokerBonds.setNextTaskId("");
				}
				
				sukukBrokerBonds.setWorkflowId(0);

				if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (sukukBrokerBonds.isNewRecord()) {
					saveRecord = true;
					if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						sukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (sukukBrokerBonds.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (sukukBrokerBonds.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = sukukBrokerBonds.getRecordType();
					recordStatus = sukukBrokerBonds.getRecordStatus();
					sukukBrokerBonds.setRecordType("");
					sukukBrokerBonds.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				if (saveRecord) {
					sukukBrokerBondsDAO.save(sukukBrokerBonds, type);
				}

				if (updateRecord) {
					sukukBrokerBondsDAO.update(sukukBrokerBonds, type);
				}

				if (deleteRecord) {
					sukukBrokerBondsDAO.delete(sukukBrokerBonds, type);
				}

				if (approveRec) {
					sukukBrokerBonds.setRecordType(rcdType);
					sukukBrokerBonds.setRecordStatus(recordStatus);
				}
            }

			logger.debug("Leaving");
			return auditDetails;

		}
		

		/**
		 * Method deletion of sukuk broker bonds list with existing broker code
		 * 
		 * @param sukukBroker
		 * @param tableType
		 */
		public List<AuditDetail> listDeletion(SukukBroker sukukBroker, String tableType, String auditTranType) {
			List<AuditDetail> auditList = sukukBroker.getAuditDetailMap().get("SukukBrokerBonds");
			for (AuditDetail auditDetail : auditList) {
				auditDetail.setAuditTranType(auditTranType);
			}

			getSukukBrokerBondsDAO().deleteBySukukBrokerCode(sukukBroker.getBrokerCode(), tableType);
			return  auditList;
		}

		
		
		public SukukBrokerBondsDAO getSukukBrokerBondsDAO() {
	        return sukukBrokerBondsDAO;
        }

		public void setSukukBrokerBondsDAO(SukukBrokerBondsDAO sukukBrokerBondsDAO) {
	        this.sukukBrokerBondsDAO = sukukBrokerBondsDAO;
        }

}