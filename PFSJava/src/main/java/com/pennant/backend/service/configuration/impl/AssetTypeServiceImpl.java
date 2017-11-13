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
 * FileName    		:  AssetTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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
import com.pennant.backend.dao.configuration.AssetTypeDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.configuration.AssetTypeService;
import com.pennant.backend.service.extendedfields.ExtendedFieldsValidation;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>AssetType</b>.<br>
 * 
 */
public class AssetTypeServiceImpl extends GenericService<AssetType> implements AssetTypeService {
	private static final Logger logger = Logger.getLogger(AssetTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private AssetTypeDAO assetTypeDAO;
	private ExtendedFieldsValidation	extendedFieldsValidation;

	private ExtendedFieldDetailDAO		extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO		extendedFieldHeaderDAO;


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
	 * @return the assetTypeDAO
	 */
	public AssetTypeDAO getAssetTypeDAO() {
		return assetTypeDAO;
	}
	/**
	 * @param assetTypeDAO the assetTypeDAO to set
	 */
	public void setAssetTypeDAO(AssetTypeDAO assetTypeDAO) {
		this.assetTypeDAO = assetTypeDAO;
	}

	/**
	 * @return the vASConfiguration
	 */
	@Override
	public AssetType getAssetType() {
		return getAssetTypeDAO().getAssetType();
	}
	/**
	 * @return the assetType for New Record
	 */
	@Override
	public AssetType getNewAssetType() {
		return getAssetTypeDAO().getNewAssetType();
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
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * AssetTypes/AssetTypes_Temp by using AssetTypesDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using AssetTypesDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtAssetTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		AssetType aAssetType = (AssetType) auditHeader.getAuditDetail().getModelData();

		if (aAssetType.isWorkflow()) {
			tableType="_Temp";
		}

		if (aAssetType.isNew()) {
			getAssetTypeDAO().save(aAssetType,tableType);
		}else{
			getAssetTypeDAO().update(aAssetType,tableType);
		}

		//ExtendedFieldHeader processing
		List<AuditDetail> headerDetail = aAssetType.getExtendedFieldHeader().getAuditDetailMap().get("ExtendedFieldHeader");
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
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table AssetTypes by using AssetTypesDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtAssetTypes by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		AssetType assetType = (AssetType) auditHeader.getAuditDetail().getModelData();
		getAssetTypeDAO().delete(assetType,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getAssetTypesById fetch the details by using AssetTypesDAO's getAssetTypesById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return AssetTypes
	 */
	@Override
	public AssetType getAssetTypeById(String id) {
		AssetType assetType = getAssetTypeDAO().getAssetTypeById(id, "_View");
		if(assetType != null) {
			ExtendedFieldHeader extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(AssetConstants.EXTENDEDFIELDS_MODULE, assetType.getAssetType(), "_View");
			if (extFldHeader != null) {
				extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_View"));
			}
			assetType.setExtendedFieldHeader(extFldHeader);
		}
		return assetType;
	}
	/**
	 * getApprovedAssetTypesById fetch the details by using AssetTypesDAO's
	 * getAssetTypesById method . with parameter id and type as blank. it fetches
	 * the approved records from the AssetTypes.
	 * 
	 * @param id
	 *            (String)
	 * @return AssetTypes
	 */
	public AssetType getApprovedAssetTypeById(String id) {
		AssetType assetType = getAssetTypeDAO().getAssetTypeById(id, "_AView");
		ExtendedFieldHeader extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(AssetConstants.EXTENDEDFIELDS_MODULE, assetType.getAssetType(), "_AView");
		if (extFldHeader != null) {
			extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_AView"));
		}
		assetType.setExtendedFieldHeader(extFldHeader);
		return assetType;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getAssetTypeDAO().delete with parameters assetType,"" b) NEW Add new
	 * record in to main table by using getAssetTypeDAO().save with parameters
	 * assetType,"" c) EDIT Update record in the main table by using
	 * getAssetTypeDAO().update with parameters assetType,"" 3) Delete the record
	 * from the workFlow table by using getAssetTypeDAO().delete with parameters
	 * assetType,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtAssetTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtAssetTypes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		AssetType assetType = new AssetType("");
		BeanUtils.copyProperties((AssetType) auditHeader.getAuditDetail().getModelData(), assetType);
		
		// Extended field Details
		ExtendedFieldHeader extendedFieldHeader = assetType.getExtendedFieldHeader();

		if (assetType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			
			tranType = PennantConstants.TRAN_DEL;
			// Table dropping in DB for Configured Asset Details
			getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
			auditDetails.addAll(listDeletion(extendedFieldHeader, "", auditHeader.getAuditTranType()));
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "");
				
			getAssetTypeDAO().delete(assetType,"");
		} else {
			assetType.setRoleCode("");
			assetType.setNextRoleCode("");
			assetType.setTaskId("");
			assetType.setNextTaskId("");
			assetType.setWorkflowId(0);

			if (assetType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				assetType.setRecordType("");
				getAssetTypeDAO().save(assetType,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				assetType.setRecordType("");
				getAssetTypeDAO().update(assetType,"");
			}
			
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
			auditDetails.add(new AuditDetail(tranType, 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));
			if (extendedFieldHeader.getExtendedFieldDetails() != null && extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
				List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
				details = getExtendedFieldsValidation().processingExtendeFieldList(details, "");
				auditDetails.addAll(details);
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		//Extended filed header
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailList.add(new AuditDetail(tranType, 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));

		//Extended Field Detail List
		auditDetailList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		getAssetTypeDAO().delete(assetType,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assetType);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getAssetTypeDAO().delete with parameters
	 * assetType,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtAssetTypes by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		AssetType assetType = (AssetType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAssetTypeDAO().delete(assetType,"_Temp");

		//ExtendedFieldHeader
		ExtendedFieldHeader extendedFieldHeader = assetType.getExtendedFieldHeader();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	public List<AuditDetail> listDeletion(ExtendedFieldHeader extendedFieldHeader, String tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (extendedFieldHeader.getExtendedFieldDetails() != null
				&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());
			for (int i = 0; i < extendedFieldHeader.getExtendedFieldDetails().size(); i++) {
				ExtendedFieldDetail extendedFieldDetail = extendedFieldHeader.getExtendedFieldDetails().get(i);
				if (StringUtils.isNotBlank(extendedFieldDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], extendedFieldDetail
							.getBefImage(), extendedFieldDetail));
				}
			}
			getExtendedFieldDetailDAO().deleteByExtendedFields(extendedFieldHeader.getId(), tableType);
		}
		logger.debug("Leaving");
		return auditList;
	}

	/** 
	 * 
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);

		AssetType assetType = (AssetType) auditDetail.getModelData();
		String usrLanguage = assetType.getUserDetails().getUsrLanguage();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		//Extended field details
		auditHeader = getAuditDetails(auditHeader, method);

		//Extended field details
		ExtendedFieldHeader extendedFieldHeader = assetType.getExtendedFieldHeader();
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
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		AssetType assetType = (AssetType) auditHeader.getAuditDetail().getModelData();
		ExtendedFieldHeader extendedFieldHeader = assetType.getExtendedFieldHeader();
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
		assetType.setExtendedFieldHeader(extendedFieldHeader);
		auditHeader.getAuditDetail().setModelData(assetType);
		auditHeader.setAuditDetails(auditDetails);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAssetTypeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		AssetType aAssetType= (AssetType) auditDetail.getModelData();

		AssetType tempAssetType= null;
		if (aAssetType.isWorkflow()){
			tempAssetType = getAssetTypeDAO().getAssetTypeById(aAssetType.getId(), "_Temp");
		}
		AssetType befAssetType= getAssetTypeDAO().getAssetTypeById(aAssetType.getId(), "");

		AssetType oldAssetType= aAssetType.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=aAssetType.getAssetType();
		errParm[0]=PennantJavaUtil.getLabel("label_AssetType")+": "+valueParm[0];

		if (aAssetType.isNew()){ // for New record or new record into work flow

			if (!aAssetType.isWorkflow()){// With out Work flow only new records  
				if (befAssetType !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
				}	
			}else{ // with work flow
				if (aAssetType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befAssetType !=null || tempAssetType!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				}else{ // if records not exists in the Main flow table
					if (befAssetType ==null || tempAssetType!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!aAssetType.isWorkflow()){	// With out Work flow for update and delete

				if (befAssetType ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm));
				}else{
					if (oldAssetType!=null && !oldAssetType.getLastMntOn().equals(befAssetType.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm));
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm));
						}
					}
				}
			}else{

				if (tempAssetType==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}

				if (tempAssetType!=null  && oldAssetType!=null && !oldAssetType.getLastMntOn().equals(tempAssetType.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
				}
			}
		}
		 
		//Checking the Asset type is used in loan origination while deletion. If used throws error message like 
		// Asset type is in used.
		if (PennantConstants.RECORD_TYPE_DEL.equals(aAssetType.getRecordType())) {
			if (getAssetTypeDAO().getAssignedAssets(aAssetType.getAssetType()) > 0) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !aAssetType.isWorkflow()){
			auditDetail.setBefImage(befAssetType);	
		}

		return auditDetail;
	}

	public ExtendedFieldsValidation getExtendedFieldsValidation() {
		if (extendedFieldsValidation == null) {
			this.extendedFieldsValidation = new ExtendedFieldsValidation(extendedFieldDetailDAO, extendedFieldHeaderDAO);
		}
		return extendedFieldsValidation;
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

}