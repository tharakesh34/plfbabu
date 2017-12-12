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
 * FileName    		:  CollateralStructureServiceImpl.java                                                   * 	  
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
package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralStructureDAO;
import com.pennant.backend.dao.lmtmasters.FinanceWorkFlowDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.extendedfields.ExtendedFieldsValidation;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CollateralStructure</b>.<br>
 * 
 */
public class CollateralStructureServiceImpl extends GenericService<CollateralStructure> implements CollateralStructureService {
	private static final Logger			logger	= Logger.getLogger(CollateralStructureServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private CollateralStructureDAO		collateralStructureDAO;
	private ExtendedFieldsValidation	extendedFieldsValidation;
	private ExtendedFieldDetailDAO		extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO		extendedFieldHeaderDAO;
	private FinanceWorkFlowDAO          financeWorkFlowDAO;

	public CollateralStructureServiceImpl() {
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

	public CollateralStructureDAO getCollateralStructureDAO() {
		return collateralStructureDAO;
	}
	public void setCollateralStructureDAO(CollateralStructureDAO collateralStructureDAO) {
		this.collateralStructureDAO = collateralStructureDAO;
	}
	
	public ExtendedFieldsValidation getExtendedFieldsValidation() {
		if (extendedFieldsValidation == null) {
			this.extendedFieldsValidation = new ExtendedFieldsValidation(extendedFieldDetailDAO, extendedFieldHeaderDAO);
		}
		return this.extendedFieldsValidation;
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
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CollateralStructure/CollateralStructure_Temp by using CollateralStructureDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using CollateralStructureDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtCollateralStructure by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType = "";
		CollateralStructure collateralStructure = (CollateralStructure) auditHeader.getAuditDetail().getModelData();
		if (collateralStructure.isWorkflow()) {
			tableType = "_Temp";
		}
		
		if (collateralStructure.isNew()) {
			getCollateralStructureDAO().save(collateralStructure, tableType);
		} else {
			getCollateralStructureDAO().update(collateralStructure, tableType);
		}
		
		//ExtendedFieldHeader processing
		List<AuditDetail> headerDetail = collateralStructure.getExtendedFieldHeader().getAuditDetailMap().get("ExtendedFieldHeader");
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
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CollateralStructure by using CollateralStructureDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCollateralStructure by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		CollateralStructure collateralStructure = (CollateralStructure) auditHeader.getAuditDetail().getModelData();
		
		//ExtendedFieldHeader
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.add(new AuditDetail(auditHeader.getAuditTranType(), 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		// Table dropping in DB for Configured Collateral Type Details
		getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
		
		getCollateralStructureDAO().delete(collateralStructure, "");
		
		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralStructure(), collateralStructure.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				collateralStructure.getBefImage(), collateralStructure));
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCollateralStructureById fetch the details by using CollateralStructureDAO's getCollateralStructureById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CollateralStructure
	 */
	@Override
	public CollateralStructure getCollateralStructureByType(String collateralType) {
		logger.debug("Entering");
		
		CollateralStructure collaStructure = getCollateralStructureDAO().getCollateralStructureByType(collateralType, "_View");
	
		ExtendedFieldHeader extFldHeader = null;
		if (collaStructure != null) {
			extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(
					CollateralConstants.MODULE_NAME, collaStructure.getCollateralType(), "_View");
			if (extFldHeader != null) {
				extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_View"));
			}
			collaStructure.setExtendedFieldHeader(extFldHeader);
		}
		
		logger.debug("Leaving");
		return collaStructure;
	}

	/**
	 * getApprovedCollateralStructureById fetch the details by using CollateralStructureDAO's getCollateralStructureById
	 * method . with parameter id and type as blank. it fetches the approved records from the CollateralStructure.
	 * 
	 * @param id
	 *            (String)
	 * @return CollateralStructure
	 */
	@Override
	public CollateralStructure getApprovedCollateralStructureByType(String collateralType) {
		logger.debug("Entering");

		CollateralStructure collaStructure = getCollateralStructureDAO().getCollateralStructureByType(collateralType, "_AView");
		ExtendedFieldHeader extFldHeader = null;
		if (collaStructure != null) {
			extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(
					CollateralConstants.MODULE_NAME, collaStructure.getCollateralType(), "_AView");
			if (extFldHeader != null) {
				extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(
						extFldHeader.getModuleId(), "_AView"));
			}
			collaStructure.setExtendedFieldHeader(extFldHeader);
		}
		
		logger.debug("Leaving");
		return collaStructure;
		
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCollateralStructureDAO().delete
	 * with parameters collateralStructure,"" b) NEW Add new record in to main table by using
	 * getCollateralStructureDAO().save with parameters collateralStructure,"" c) EDIT Update record in the main table
	 * by using getCollateralStructureDAO().update with parameters collateralStructure,"" 3) Delete the record from the
	 * workFlow table by using getCollateralStructureDAO().delete with parameters collateralStructure,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtCollateralStructure by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtCollateralStructure by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		CollateralStructure collateralStructure = new CollateralStructure("");
		BeanUtils.copyProperties((CollateralStructure) auditHeader.getAuditDetail().getModelData(), collateralStructure);

		// Extended field Details
		ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();

		if (collateralStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			
			// Table dropping in DB for Configured Asset Details
			getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
			auditDetails.addAll(listDeletion(extendedFieldHeader, "", auditHeader.getAuditTranType()));
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "");
			
			getCollateralStructureDAO().delete(collateralStructure, "");
		} else {
			collateralStructure.setRoleCode("");
			collateralStructure.setNextRoleCode("");
			collateralStructure.setTaskId("");
			collateralStructure.setNextTaskId("");
			collateralStructure.setWorkflowId(0);

			if (collateralStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				collateralStructure.setRecordType("");
				getCollateralStructureDAO().save(collateralStructure, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				collateralStructure.setRecordType("");
				getCollateralStructureDAO().update(collateralStructure, "");
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
		
		//Collateral Detail
		getCollateralStructureDAO().delete(collateralStructure, "_Temp");
				
		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralStructure(), collateralStructure.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],collateralStructure.getBefImage(), collateralStructure));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		return auditHeader;
	}
	
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
	
	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCollateralStructureDAO().delete with parameters collateralStructure,"_Temp" 3) Audit
	 * the record in to AuditHeader and AdtCollateralStructure by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		CollateralStructure collateralStructure = (CollateralStructure) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		
		//ExtendedFieldHeader
		ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.add(new AuditDetail(auditHeader.getAuditTranType(), 1,  extendedFieldHeader.getBefImage(), extendedFieldHeader));
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));
		
		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralStructure(), collateralStructure.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				collateralStructure.getBefImage(), collateralStructure));
		
		//Collateral Structure Deletion
		getCollateralStructureDAO().delete(collateralStructure, "_Temp");
		
		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditDetails.add(auditDetail);
		CollateralStructure collateralStructure = (CollateralStructure) auditDetail.getModelData();
		String usrLanguage = collateralStructure.getUserDetails().getLanguage();

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		//Extended field details
		auditHeader = getAuditDetails(auditHeader, method);

		//Extended field details
		ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();
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
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		CollateralStructure collateralStructure = (CollateralStructure) auditHeader.getAuditDetail().getModelData();
		ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();
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
		collateralStructure.setExtendedFieldHeader(extendedFieldHeader);
		auditHeader.getAuditDetail().setModelData(collateralStructure);
		auditHeader.setAuditDetails(auditDetails);
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getCollateralStructureDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		CollateralStructure collateralStructure = (CollateralStructure) auditDetail.getModelData();

		CollateralStructure tempCollateralStructure = null;
		if (collateralStructure.isWorkflow()) {
			tempCollateralStructure = getCollateralStructureDAO().getCollateralStructureByType(collateralStructure.getCollateralType(), "_Temp");
		}
		CollateralStructure befCollateralStructure = getCollateralStructureDAO().getCollateralStructureByType(collateralStructure.getCollateralType(), "");

		CollateralStructure oldCollateralStructure = collateralStructure.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = collateralStructure.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CollateralType") + ":" + valueParm[0];

		if (collateralStructure.isNew()) { // for New record or new record into work flow

			if (!collateralStructure.isWorkflow()) {// With out Work flow only new records  
				if (befCollateralStructure != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (collateralStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCollateralStructure != null || tempCollateralStructure != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befCollateralStructure == null || tempCollateralStructure != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!collateralStructure.isWorkflow()) { // With out Work flow for update and delete

				if (befCollateralStructure == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldCollateralStructure != null
							&& !oldCollateralStructure.getLastMntOn().equals(befCollateralStructure.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempCollateralStructure == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (oldCollateralStructure != null && tempCollateralStructure!=null 
						&& !oldCollateralStructure.getLastMntOn().equals(tempCollateralStructure.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		// If Collateral Structure Product Code is already utilized in Workflow 
		if(StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, collateralStructure.getRecordType())){
			boolean workflowExists = getFinanceWorkFlowDAO().isWorkflowExists(collateralStructure.getCollateralType(), PennantConstants.WORFLOW_MODULE_COLLATERAL);
			if(workflowExists){
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !collateralStructure.isWorkflow()) {
			auditDetail.setBefImage(befCollateralStructure);
		}

		logger.debug("Leaving");
		return auditDetail;
	}

	public FinanceWorkFlowDAO getFinanceWorkFlowDAO() {
		return financeWorkFlowDAO;
	}

	public void setFinanceWorkFlowDAO(FinanceWorkFlowDAO financeWorkFlowDAO) {
		this.financeWorkFlowDAO = financeWorkFlowDAO;
	}

}