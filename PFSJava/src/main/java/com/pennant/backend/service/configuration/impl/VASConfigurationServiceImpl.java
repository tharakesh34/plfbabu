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
 * FileName    		:  VASConfigurationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.configuration.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.configuration.VASConfigurationDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.extendedfields.ExtendedFieldsValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;

/**
 * Service implementation for methods that depends on <b>VASConfiguration</b>.<br>
 * 
 */
public class VASConfigurationServiceImpl extends GenericService<VASConfiguration> implements VASConfigurationService {
	private static final Logger logger = Logger.getLogger(VASConfigurationServiceImpl.class);
	
	private AuditHeaderDAO				auditHeaderDAO;
	private ExtendedFieldsValidation	extendedFieldsValidation;
	private VASConfigurationDAO			vASConfigurationDAO;
	private ExtendedFieldDetailDAO		extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO		extendedFieldHeaderDAO;
	private FinanceWorkFlowDAO          financeWorkFlowDAO;

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
	 * @return the vASConfigurationDAO
	 */
	public VASConfigurationDAO getVASConfigurationDAO() {
		return vASConfigurationDAO;
	}
	/**
	 * @param vASConfigurationDAO the vASConfigurationDAO to set
	 */
	public void setVASConfigurationDAO(VASConfigurationDAO vASConfigurationDAO) {
		this.vASConfigurationDAO = vASConfigurationDAO;
	}

	/**
	 * @return the vASConfiguration
	 */
	@Override
	public VASConfiguration getVASConfiguration() {
		return getVASConfigurationDAO().getVASConfiguration();
	}
	/**
	 * @return the vASConfiguration for New Record
	 */
	@Override
	public VASConfiguration getNewVASConfiguration() {
		return getVASConfigurationDAO().getNewVASConfiguration();
	}

	public ExtendedFieldsValidation getExtendedFieldsValidation() {
		if (extendedFieldsValidation == null) {
			this.extendedFieldsValidation = new ExtendedFieldsValidation(extendedFieldDetailDAO, extendedFieldHeaderDAO);
		}
		return this.extendedFieldsValidation;
	}

	public void setExtendedFieldsValidation(ExtendedFieldsValidation extendedFieldsValidation) {
		this.extendedFieldsValidation = extendedFieldsValidation;
	} 
	
	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table VasStructure/VasStructure_Temp 
	 * 			by using VASConfigurationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VASConfigurationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table VasStructure/VasStructure_Temp 
	 * 			by using VASConfigurationDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using VASConfigurationDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	 
		
	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		VASConfiguration vASConfiguration = (VASConfiguration) auditHeader.getAuditDetail().getModelData();

		if (vASConfiguration.isWorkflow()) {
			tableType = "_Temp";
		}

		if (vASConfiguration.isNew()) {
			getVASConfigurationDAO().save(vASConfiguration, tableType);
		} else {
			getVASConfigurationDAO().update(vASConfiguration, tableType);
		}

		//ExtendedFieldHeader processing
		List<AuditDetail> headerDetail = vASConfiguration.getExtendedFieldHeader().getAuditDetailMap().get("ExtendedFieldHeader");
		ExtendedFieldHeader extFieldHeader = (ExtendedFieldHeader) headerDetail.get(0).getModelData();

		long moduleId;
		if (extFieldHeader.isNew()) {
			moduleId = getExtendedFieldHeaderDAO().save(extFieldHeader, tableType);

			//Setting Module ID to List
			List<ExtendedFieldDetail> list = extFieldHeader.getExtendedFieldDetails();
			if (list != null && list.size() > 0) {
				for (ExtendedFieldDetail ext : list) {
					ext.setModuleId(moduleId);
				}
			}
		} else {
			getExtendedFieldHeaderDAO().update(extFieldHeader, tableType);
		}

		// Extended Field Header Audit
		auditDetails.add(headerDetail.get(0));

		// Processing Extended Field Details List
		if (extFieldHeader.getExtendedFieldDetails() != null && extFieldHeader.getExtendedFieldDetails().size() > 0) {
			List<AuditDetail> details = extFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
			details = getExtendedFieldsValidation().processingExtendeFieldList(details, tableType);
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table VasStructure by using VASConfigurationDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader)    
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

		VASConfiguration vASConfiguration = (VASConfiguration) auditHeader.getAuditDetail().getModelData();
		
		//ExtendedFieldHeader
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		ExtendedFieldHeader extendedFieldHeader = vASConfiguration.getExtendedFieldHeader();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.add(new AuditDetail(auditHeader.getAuditTranType(), 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		// Table dropping in DB for Configured VAS Type Details
		getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
		getVASConfigurationDAO().delete(vASConfiguration,"");
		
		String[] fields = PennantJavaUtil.getFieldDetails(new VASConfiguration(), vASConfiguration.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				vASConfiguration.getBefImage(), vASConfiguration));
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getVASConfigurationById fetch the details by using VASConfigurationDAO's getVASConfigurationById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return VASConfiguration
	 */
	@Override
	public VASConfiguration getVASConfigurationByCode(String productCode) {
		logger.debug("Entering");

		VASConfiguration vasConfiguration = getVASConfigurationDAO().getVASConfigurationByCode(productCode, "_View");
		ExtendedFieldHeader extFldHeader = null;
		if (vasConfiguration != null) {
			extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(VASConsatnts.MODULE_NAME, vasConfiguration.getProductCode(), "_View");
			if (extFldHeader != null) {
				extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_View"));
			}
			vasConfiguration.setExtendedFieldHeader(extFldHeader);
		}
		logger.debug("Leaving");
		return vasConfiguration;
	}

	/**
	 * getApprovedVASConfigurationById fetch the details by using VASConfigurationDAO's getVASConfigurationById method .
	 * with parameter id and type as blank. it fetches the approved records from the VasStructure.
	 * 
	 * @param id
	 *            (String)
	 * @return VASConfiguration
	 */
	
	public VASConfiguration getApprovedVASConfigurationByCode(String productCode) {
		logger.debug("Entering");
		
		VASConfiguration vasConfiguration = getVASConfigurationDAO().getVASConfigurationByCode(productCode, "_AView");
		ExtendedFieldHeader extFldHeader = null;
		if (vasConfiguration != null) {
			extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(VASConsatnts.MODULE_NAME, vasConfiguration.getProductCode(), "_AView");
			if (extFldHeader != null) {
				extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_AView"));
			}
			vasConfiguration.setExtendedFieldHeader(extFldHeader);
		}
		logger.debug("Leaving");
		return vasConfiguration;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getVASConfigurationDAO().delete with parameters vASConfiguration,""
	 * 		b)  NEW		Add new record in to main table by using getVASConfigurationDAO().save with parameters vASConfiguration,""
	 * 		c)  EDIT	Update record in the main table by using getVASConfigurationDAO().update with parameters vASConfiguration,""
	 * 3)	Delete the record from the workFlow table by using getVASConfigurationDAO().delete with parameters vASConfiguration,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		VASConfiguration vASConfiguration = new VASConfiguration("");
		BeanUtils.copyProperties((VASConfiguration) auditHeader.getAuditDetail().getModelData(), vASConfiguration);

		if (vASConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getVASConfigurationDAO().delete(vASConfiguration, "");
		} else {
			vASConfiguration.setRoleCode("");
			vASConfiguration.setNextRoleCode("");
			vASConfiguration.setTaskId("");
			vASConfiguration.setNextTaskId("");
			vASConfiguration.setWorkflowId(0);

			if (vASConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				vASConfiguration.setRecordType("");
				getVASConfigurationDAO().save(vASConfiguration, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				vASConfiguration.setRecordType("");
				getVASConfigurationDAO().update(vASConfiguration, "");
			}
		}

		//ExtendedFieldHeader
		ExtendedFieldHeader extendedFieldHeader = vASConfiguration.getExtendedFieldHeader();

		if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(extendedFieldHeader, "", tranType));
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "");
			// Table dropping in DB for Configured VAS Details
			getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
		} else {
			extendedFieldHeader.setRoleCode("");
			extendedFieldHeader.setNextRoleCode("");
			extendedFieldHeader.setTaskId("");
			extendedFieldHeader.setNextTaskId("");
			extendedFieldHeader.setWorkflowId(0);

			if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().save(extendedFieldHeader, "");
				// Table creation in DB for Newly created Configuration Type Details
				getExtendedFieldHeaderDAO().createTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());

			} else {
				tranType = PennantConstants.TRAN_UPD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().update(extendedFieldHeader, "");
			}
			auditDetails.add(new AuditDetail(tranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader));
			if (extendedFieldHeader.getExtendedFieldDetails() != null
					&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
				List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
				details = getExtendedFieldsValidation().processingExtendeFieldList(details, "");
				auditDetails.addAll(details);
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		//Extended filed header
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailList.add(new AuditDetail(tranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader));

		//Extended Field Detail List
		auditDetailList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		//VAS Detail
		getVASConfigurationDAO().delete(vASConfiguration, "_Temp");
		String[] fields = PennantJavaUtil.getFieldDetails(new VASConfiguration(), vASConfiguration.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				vASConfiguration.getBefImage(), vASConfiguration));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getVASConfigurationDAO().delete with parameters vASConfiguration,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtVasStructure by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	
		public AuditHeader  doReject(AuditHeader auditHeader) {
			logger.debug("Entering");
			
			auditHeader = businessValidation(auditHeader,"doApprove",false);
			if (!auditHeader.isNextProcess()) {
				return auditHeader;
			}

			List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
			VASConfiguration vASConfiguration = (VASConfiguration) auditHeader.getAuditDetail().getModelData();

			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			
			//ExtendedFieldHeader
			ExtendedFieldHeader extendedFieldHeader = vASConfiguration.getExtendedFieldHeader();
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
			auditDetailsList.add(new AuditDetail(auditHeader.getAuditTranType(), 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));
			auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));
			
			String[] fields = PennantJavaUtil.getFieldDetails(new VASConfiguration(), vASConfiguration.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					vASConfiguration.getBefImage(), vASConfiguration));
			
			//VASConfiguration Deletion
			getVASConfigurationDAO().delete(vASConfiguration,"_Temp");
			
			auditHeader.setAuditDetails(auditDetailsList);
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
			
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
			auditDetails.add(auditDetail);
			VASConfiguration  vasConfiguration = (VASConfiguration) auditDetail.getModelData();
			String usrLanguage =  vasConfiguration.getUserDetails().getUsrLanguage();
			
			//Extended field details
			auditHeader = getAuditDetails(auditHeader, method);
			
			//Extended field details
			ExtendedFieldHeader extendedFieldHeader = vasConfiguration.getExtendedFieldHeader();
			if(extendedFieldHeader != null){
				List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
				AuditDetail	detail = getExtendedFieldsValidation().extendedFieldsHeaderValidation(details.get(0), method, usrLanguage);
				auditDetails.add(detail);
				
				if (extendedFieldHeader.getExtendedFieldDetails() != null && extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
					List<AuditDetail> detailList = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
					detailList = getExtendedFieldsValidation().extendedFieldsListValidation(detailList, method, usrLanguage);
					auditDetails.addAll(detailList);
				}
			}
			for (int i = 0; i < auditDetails.size(); i++) {
				auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
			}
			auditHeader = nextProcess(auditHeader);
			logger.debug("Leaving");
			return auditHeader;
		}

		/**
		 * Validation method do the following steps.
		 * 1)	get the details from the auditHeader. 
		 * 2)	fetch the details from the tables
		 * 3)	Validate the Record based on the record details. 
		 * 4) 	Validate for any business validation.
		 * 5)	for any mismatch conditions Fetch the error details from getVASConfigurationDAO().getErrorDetail with Error ID and language as parameters.
		 * 6)	if any error/Warnings  then assign the to auditHeader 
		 * @param AuditHeader (auditHeader)
		 * @param boolean onlineRequest
		 * @return auditHeader
		 */
		
		private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
			logger.debug("Entering");
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
			VASConfiguration vASConfiguration= (VASConfiguration) auditDetail.getModelData();
			
			VASConfiguration tempVASConfiguration= null;
			if (vASConfiguration.isWorkflow()){
				tempVASConfiguration = getVASConfigurationDAO().getVASConfigurationByCode(vASConfiguration.getId(), "_Temp");
			}
			VASConfiguration befVASConfiguration= getVASConfigurationDAO().getVASConfigurationByCode(vASConfiguration.getId(), "");
			
			VASConfiguration oldVasConfiguration= vASConfiguration.getBefImage();
			
			
			String[] errParm= new String[1];
			String[] valueParm= new String[1];
			valueParm[0]=vASConfiguration.getId();
			errParm[0]=PennantJavaUtil.getLabel("label_ProductCode")+":"+valueParm[0];
			
			if (vASConfiguration.isNew()){ // for New record or new record into work flow
				
				if (!vASConfiguration.isWorkflow()){// With out Work flow only new records  
					if (befVASConfiguration !=null){	// Record Already Exists in the table then error  
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}	
				}else{ // with work flow
					if (vASConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
						if (befVASConfiguration !=null || tempVASConfiguration!=null ){ // if records already exists in the main table
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
						}
					}else{ // if records not exists in the Main flow table
						if (befVASConfiguration ==null || tempVASConfiguration!=null ){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
						}
					}
				}
			}else{
				// for work flow process records or (Record to update or Delete with out work flow)
				if (!vASConfiguration.isWorkflow()){	// With out Work flow for update and delete
				
					if (befVASConfiguration ==null){ // if records not exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
					}else{
						if (oldVasConfiguration!=null && !oldVasConfiguration.getLastMntOn().equals(befVASConfiguration.getLastMntOn())){
							if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
							}else{
								auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
							}
						}
					}
				}else{
				
					if (tempVASConfiguration==null ){ // if records not exists in the Work flow table 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
					
					if (tempVASConfiguration!=null  && oldVasConfiguration!=null && !oldVasConfiguration.getLastMntOn().equals(tempVASConfiguration.getLastMntOn())){ 
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
			
			// If VAS Structure Product Code is already utilized in Workflow 
			if(StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, vASConfiguration.getRecordType())){
				boolean workflowExists = getFinanceWorkFlowDAO().isWorkflowExists(vASConfiguration.getProductCode(), PennantConstants.WORFLOW_MODULE_VAS);
				if(workflowExists){
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
				}
			}

			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
			
			if(StringUtils.trimToEmpty(method).equals("doApprove") || !vASConfiguration.isWorkflow()){
				auditDetail.setBefImage(befVASConfiguration);	
			}

			return auditDetail;
		}

		
		/**
		 * Common Method for Retrieving AuditDetails List
		 * 
		 * @param auditHeader
		 * @param method
		 * @return
		 */
		private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
			logger.debug("Entering");
			
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

			VASConfiguration vasConfiguration = (VASConfiguration) auditHeader.getAuditDetail().getModelData();
			ExtendedFieldHeader extendedFieldHeader = vasConfiguration.getExtendedFieldHeader();
			String auditTranType = "";

			if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
				if (extendedFieldHeader.isWorkflow()) {
					auditTranType = PennantConstants.TRAN_WF;
				}
			}
			
			//Audit Detail Preparation for Extended Field Header
			AuditDetail auditDetail =  new AuditDetail(auditTranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader);
			List<AuditDetail> auditDetailHeaderList = new ArrayList<AuditDetail>();
			auditDetailHeaderList.add(auditDetail);
			auditDetailMap.put("ExtendedFieldHeader", auditDetailHeaderList);

			//Audit Detail Preparation for Extended Field Detail
			if (extendedFieldHeader.getExtendedFieldDetails() != null && extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
				auditDetailMap.put("ExtendedFieldDetails", getExtendedFieldsValidation().setExtendedFieldsAuditData(extendedFieldHeader, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
			}
			
			extendedFieldHeader.setAuditDetailMap(auditDetailMap);
			vasConfiguration.setExtendedFieldHeader(extendedFieldHeader);
			auditHeader.getAuditDetail().setModelData(vasConfiguration);
			auditHeader.setAuditDetails(auditDetails);
			
			logger.debug("Leaving");
			return auditHeader;
		}
		/*
		 * ExtendedFieldS list deletion
		 */
		public List<AuditDetail> listDeletion(ExtendedFieldHeader extendedFieldHeader, String tableType, String auditTranType) {
			logger.debug("Entering");
			
			List<AuditDetail> auditList = new ArrayList<AuditDetail>();

			if(extendedFieldHeader.getExtendedFieldDetails()!=null && extendedFieldHeader.getExtendedFieldDetails().size()>0){
				String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());
				for (int i = 0; i < extendedFieldHeader.getExtendedFieldDetails().size(); i++) {
					ExtendedFieldDetail extendedFieldDetail = extendedFieldHeader.getExtendedFieldDetails().get(i);
					if (StringUtils.isNotBlank(extendedFieldDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
						auditList.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], extendedFieldDetail.getBefImage(), extendedFieldDetail));
					}
				}
				getExtendedFieldDetailDAO().deleteByExtendedFields(extendedFieldHeader.getId(), tableType);
			}
			logger.debug("Leaving");
			return auditList;
		}
		
	/*
	 * Checking the vas type is used in vas recording or not
	 */
	@Override
	public boolean isVASTypeExists(String productType) {
		return getVASConfigurationDAO().isVASTypeExists(productType);
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

	@Override
	public boolean isWorkflowExists(String productType) {
 
		return getFinanceWorkFlowDAO().isWorkflowExists(productType, PennantConstants.WORFLOW_MODULE_VAS);
	}
		
}