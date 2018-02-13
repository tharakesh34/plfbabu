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
 * FileName    		:  TakafulProviderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.InsuranceTypeDAO;
import com.pennant.backend.dao.applicationmaster.TakafulProviderDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>TakafulProvider</b>.<br>
 * 
 */
public class TakafulProviderServiceImpl extends GenericService<TakafulProvider> implements TakafulProviderService {
	private static final Logger logger = Logger.getLogger(TakafulProviderServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	
	private TakafulProviderDAO takafulProviderDAO;
	private InsuranceTypeDAO insuranceTypeDAO;

	public TakafulProviderServiceImpl() {
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
	 * @return the takafulProviderDAO
	 */
	public TakafulProviderDAO getTakafulProviderDAO() {
		return takafulProviderDAO;
	}
	/**
	 * @param takafulProviderDAO the takafulProviderDAO to set
	 */
	public void setTakafulProviderDAO(TakafulProviderDAO takafulProviderDAO) {
		this.takafulProviderDAO = takafulProviderDAO;
	}

	public InsuranceTypeDAO getInsuranceTypeDAO() {
		return insuranceTypeDAO;
	}

	public void setInsuranceTypeDAO(InsuranceTypeDAO insuranceTypeDAO) {
		this.insuranceTypeDAO = insuranceTypeDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table TakafulProvider/TakafulProvider_Temp 
	 * 			by using TakafulProviderDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using TakafulProviderDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table TakafulProvider/TakafulProvider_Temp 
	 * 			by using TakafulProviderDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using TakafulProviderDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader)
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
		TakafulProvider takafulProvider = (TakafulProvider) auditHeader.getAuditDetail().getModelData();
		
		if (takafulProvider.isWorkflow()) {
			tableType="_Temp";
		}

		if (takafulProvider.isNew()) {
			getTakafulProviderDAO().save(takafulProvider,tableType);
			auditHeader.getAuditDetail().setModelData(takafulProvider);
			auditHeader.setAuditReference(takafulProvider.getTakafulCode());
		}else{
			getTakafulProviderDAO().update(takafulProvider,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table TakafulProvider by using TakafulProviderDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader)    
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
		
		TakafulProvider takafulProvider = (TakafulProvider) auditHeader.getAuditDetail().getModelData();
		getTakafulProviderDAO().delete(takafulProvider,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getTakafulProviderById fetch the details by using TakafulProviderDAO's getTakafulProviderById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return TakafulProvider
	 */
	
	@Override
	public TakafulProvider getTakafulProviderById(String id) {
		return getTakafulProviderDAO().getTakafulProviderById(id,"_View");
	}
	/**
	 * getApprovedTakafulProviderById fetch the details by using TakafulProviderDAO's getTakafulProviderById method .
	 * with parameter id and type as blank. it fetches the approved records from the TakafulProvider.
	 * @param id (int)
	 * @return TakafulProvider
	 */
	
	public TakafulProvider getApprovedTakafulProviderById(String id) {
		return getTakafulProviderDAO().getTakafulProviderById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getTakafulProviderDAO().delete with
	 * parameters takafulProvider,"" b) NEW Add new record in to main table by using getTakafulProviderDAO().save with
	 * parameters takafulProvider,"" c) EDIT Update record in the main table by using getTakafulProviderDAO().update
	 * with parameters takafulProvider,"" 3) Delete the record from the workFlow table by using
	 * getTakafulProviderDAO().delete with parameters takafulProvider,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		TakafulProvider takafulProvider = new TakafulProvider();
		BeanUtils.copyProperties((TakafulProvider) auditHeader.getAuditDetail().getModelData(), takafulProvider);

		if (takafulProvider.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getTakafulProviderDAO().delete(takafulProvider, "");

		} else {
			takafulProvider.setRoleCode("");
			takafulProvider.setNextRoleCode("");
			takafulProvider.setTaskId("");
			takafulProvider.setNextTaskId("");
			takafulProvider.setWorkflowId(0);

			if (takafulProvider.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				takafulProvider.setRecordType("");
				getTakafulProviderDAO().save(takafulProvider, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				takafulProvider.setRecordType("");
				getTakafulProviderDAO().update(takafulProvider, "");
			}
		}

		getTakafulProviderDAO().delete(takafulProvider, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(takafulProvider);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getTakafulProviderDAO().delete with parameters takafulProvider,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtTakafulProvider by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			TakafulProvider takafulProvider = (TakafulProvider) auditHeader.getAuditDetail().getModelData();
			
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getTakafulProviderDAO().delete(takafulProvider,"_Temp");
			
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
		 * 5)	for any mismatch conditions Fetch the error details from getTakafulProviderDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
			TakafulProvider takafulProvider= (TakafulProvider) auditDetail.getModelData();
			
			TakafulProvider tempTakafulProvider= null;
			if (takafulProvider.isWorkflow()){
				tempTakafulProvider = getTakafulProviderDAO().getTakafulProviderById(takafulProvider.getId(), "_Temp");
			}
			TakafulProvider befTakafulProvider= getTakafulProviderDAO().getTakafulProviderById(takafulProvider.getId(), "");
			
			TakafulProvider oldTakafulProvider= takafulProvider.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=String.valueOf(takafulProvider.getId());
			errParm[0]=PennantJavaUtil.getLabel("label_EmployerId")+":"+valueParm[0];
			
			if (takafulProvider.isNew()){ // for New record or new record into work flow
				
				if (!takafulProvider.isWorkflow()){// With out Work flow only new records  
					if (befTakafulProvider !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}	
				}else{ // with work flow
					if (takafulProvider.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befTakafulProvider !=null || tempTakafulProvider!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
						}
					}else{ // if records not exists in the Main flow table
						if (befTakafulProvider ==null || tempTakafulProvider!=null ){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!takafulProvider.isWorkflow()){	// With out Work flow for update and delete
				
					if (befTakafulProvider ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
					}else{
						if (oldTakafulProvider!=null && !oldTakafulProvider.getLastMntOn().equals(befTakafulProvider.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
							}else{
								auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
							}
						}
					}
				}else{
				
					if (tempTakafulProvider==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
					
					if (tempTakafulProvider!=null && oldTakafulProvider!=null && !oldTakafulProvider.getLastMntOn().equals(tempTakafulProvider.getLastMntOn())){ 
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
			
			//Validate if the provider assigned to any insurance Type
			if(StringUtils.equals(takafulProvider.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED) 
				&& StringUtils.equals(takafulProvider.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {

			List<InsuranceTypeProvider> insTypeprovider = getInsuranceTypeDAO().getInsuranceType(
					takafulProvider.getTakafulCode(), "_view");
			if (insTypeprovider != null) {
				String[][] parms = new String[2][1];
				parms[1][0] = takafulProvider.getTakafulCode();
				parms[0][0] = PennantJavaUtil.getLabel("label_TakafulProviderDialog_TakafulCode.value") + ":"
						+ parms[1][0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"41006", parms[0], parms[1]), usrLanguage));

			}

		}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if("doApprove".equals(StringUtils.trimToEmpty(method)) || !takafulProvider.isWorkflow()){
				auditDetail.setBefImage(befTakafulProvider);	
			}

			return auditDetail;
		}

}