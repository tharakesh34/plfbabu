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
 * FileName    		:  JountAccountDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>JountAccountDetail</b>.<br>
 * 
 */
public class JointAccountDetailServiceImpl extends GenericService<JointAccountDetail> implements JointAccountDetailService {
	private static final Logger logger = Logger.getLogger(JointAccountDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private ExtendedFieldRenderDAO				extendedFieldRenderDAO;
	private CustomerIncomeDAO customerIncomeDAO;

	public JointAccountDetailServiceImpl() {
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
	 * @return the jountAccountDetailDAO
	 */
	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}
	/**
	 * @param jountAccountDetailDAO the jountAccountDetailDAO to set
	 */
	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}
	
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	/**
	 * @return the jountAccountDetail
	 */
	@Override
	public JointAccountDetail getJountAccountDetail() {
		return getJountAccountDetailDAO().getJountAccountDetail();
	}
	/**
	 * @return the jountAccountDetail for New Record
	 */
	@Override
	public JointAccountDetail getNewJountAccountDetail() {
		return getJountAccountDetailDAO().getNewJountAccountDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinJointAccountDetails/FinJointAccountDetails_Temp 
	 * 			by using JountAccountDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using JountAccountDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table FinJointAccountDetails/FinJointAccountDetails_Temp 
	 * 			by using JountAccountDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using JountAccountDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
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
		JointAccountDetail jountAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();

		if (jountAccountDetail.isWorkflow()) {
			tableType="_Temp";
		}

		if (jountAccountDetail.isNew()) {
			jountAccountDetail.setId(getJountAccountDetailDAO().save(jountAccountDetail,tableType));
			auditHeader.getAuditDetail().setModelData(jountAccountDetail);
			auditHeader.setAuditReference(String.valueOf(jountAccountDetail.getJointAccountId()));
		}else{
			getJountAccountDetailDAO().update(jountAccountDetail,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinJointAccountDetails by using JountAccountDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)    
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

		JointAccountDetail jountAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();
		getJountAccountDetailDAO().delete(jountAccountDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getJountAccountDetailById fetch the details by using JountAccountDetailDAO's getJountAccountDetailById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return JountAccountDetail
	 */

	@Override
	public JointAccountDetail getJountAccountDetailById(long id) {
		return getJountAccountDetailDAO().getJountAccountDetailById(id,"_View");
	}
	/**
	 * getApprovedJountAccountDetailById fetch the details by using JountAccountDetailDAO's getJountAccountDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the FinJointAccountDetails.
	 * @param id (int)
	 * @return JountAccountDetail
	 */

	public JointAccountDetail getApprovedJountAccountDetailById(long id) {
		return getJountAccountDetailDAO().getJountAccountDetailById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getJountAccountDetailDAO().delete with parameters jountAccountDetail,""
	 * 		b)  NEW		Add new record in to main table by using getJountAccountDetailDAO().save with parameters jountAccountDetail,""
	 * 		c)  EDIT	Update record in the main table by using getJountAccountDetailDAO().update with parameters jountAccountDetail,""
	 * 3)	Delete the record from the workFlow table by using getJountAccountDetailDAO().delete with parameters jountAccountDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties((JointAccountDetail) auditHeader.getAuditDetail().getModelData(), jountAccountDetail);

		if (jountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getJountAccountDetailDAO().delete(jountAccountDetail,"");

		} else {
			jountAccountDetail.setRoleCode("");
			jountAccountDetail.setNextRoleCode("");
			jountAccountDetail.setTaskId("");
			jountAccountDetail.setNextTaskId("");
			jountAccountDetail.setWorkflowId(0);

			if (jountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				jountAccountDetail.setRecordType("");
				getJountAccountDetailDAO().save(jountAccountDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				jountAccountDetail.setRecordType("");
				getJountAccountDetailDAO().update(jountAccountDetail,"");
			}
		}

		getJountAccountDetailDAO().delete(jountAccountDetail,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(jountAccountDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getJountAccountDetailDAO().delete with parameters jountAccountDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JointAccountDetail jountAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getJountAccountDetailDAO().delete(jountAccountDetail,"_Temp");

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
	
	@Override
	public List<AuditDetail> validate(List<JointAccountDetail> jointAcDetailList, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(jointAcDetailList, workflowId, method, auditTranType, usrLanguage);
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getJountAccountDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		JointAccountDetail jountAccountDetail= (JointAccountDetail) auditDetail.getModelData();

		JointAccountDetail tempJountAccountDetail= null;
		if (jountAccountDetail.isWorkflow()){
			tempJountAccountDetail = getJountAccountDetailDAO().getJountAccountDetailById(jountAccountDetail.getId(), "_Temp");
		}
		JointAccountDetail befJountAccountDetail= getJountAccountDetailDAO().getJountAccountDetailById(jountAccountDetail.getId(), "");

		JointAccountDetail oldJountAccountDetail= jountAccountDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(jountAccountDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_JointAccountId")+":"+valueParm[0];

		if (jountAccountDetail.isNew()){ // for New record or new record into work flow

			if (!jountAccountDetail.isWorkflow()){// With out Work flow only new records  
				if (befJountAccountDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (jountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befJountAccountDetail !=null || tempJountAccountDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befJountAccountDetail ==null || tempJountAccountDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jountAccountDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befJountAccountDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldJountAccountDetail!=null && !oldJountAccountDetail.getLastMntOn().equals(befJountAccountDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempJountAccountDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldJountAccountDetail!=null && tempJountAccountDetail!=null && !oldJountAccountDetail.getLastMntOn().equals(tempJountAccountDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !jountAccountDetail.isWorkflow()){
			auditDetail.setBefImage(befJountAccountDetail);	
		}

		return auditDetail;
	}

	
	@Override
	public List<AuditDetail> saveOrUpdate(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		for (JointAccountDetail jointAccountDetail : jointAcDetailList) {
			jointAccountDetail.setWorkflowId(0);
			if (jointAccountDetail.isNewRecord()) {
				getJountAccountDetailDAO().save(jointAccountDetail, tableType);
			} else {
				getJountAccountDetailDAO().update(jointAccountDetail, tableType);
			}
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail, jointAccountDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], jointAccountDetail.getBefImage(), jointAccountDetail));
			
	
			
			if (jointAccountDetail.getCustomerDetails() != null) {
				if (jointAccountDetail.getCustomerDetails().getExtendedFieldRender() != null) {
					boolean isSaveRecord = false;
					ExtendedFieldHeader extendedFieldHeader = jointAccountDetail.getCustomerDetails().getExtendedFieldHeader();
					StringBuilder tableName = new StringBuilder();
					tableName.append(extendedFieldHeader.getModuleName());
					tableName.append("_");
					tableName.append(extendedFieldHeader.getSubModuleName());
					tableName.append("_ED");

					ExtendedFieldRender extendedFieldRender = jointAccountDetail.getCustomerDetails().getExtendedFieldRender();
					if (StringUtils.isEmpty(tableType)) {
						extendedFieldRender.setRoleCode("");
						extendedFieldRender.setNextRoleCode("");
						extendedFieldRender.setTaskId("");
						extendedFieldRender.setNextTaskId("");
					}

					// Table Name addition for Audit
					extendedFieldRender.setTableName(tableName.toString());
					extendedFieldRender.setWorkflowId(0);

					// Add Common Fields
					HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
					
					Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(jointAccountDetail.getCustCIF(), tableName.toString(), null);
					
					if (extFieldMap == null) {
						isSaveRecord = true;
					}
					if (isSaveRecord) {
						extendedFieldRender.setReference(jointAccountDetail.getCustomerDetails().getCustomer().getCustCIF());
						mapValues.put("Reference", extendedFieldRender.getReference());
						mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
					}

					mapValues.put("Version", extendedFieldRender.getVersion());
					mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
					mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
					mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
					mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
					mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
					mapValues.put("TaskId", extendedFieldRender.getTaskId());
					mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
					mapValues.put("RecordType", extendedFieldRender.getRecordType());
					mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());

					// Audit Details Preparation
					HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender
							.getMapValues();
					auditMapValues.put("Reference", extendedFieldRender.getReference());
					auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
					auditMapValues.put("Version", extendedFieldRender.getVersion());
					auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
					auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
					auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
					auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
					auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
					auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
					auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
					auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
					auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
					extendedFieldRender.setAuditMapValues(auditMapValues);

					if (isSaveRecord) {
						auditTranType = PennantConstants.TRAN_ADD;
						extendedFieldRender.setRecordType("");
						extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), "",
								tableName.toString());
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
						extendedFieldRenderDAO.update(extendedFieldRender.getReference(),
								extendedFieldRender.getSeqNo(), extendedFieldRender.getMapValues(), "",
								tableName.toString());
					}
					/*if (StringUtils.isNotBlank(extendedFieldRender.getReference())) {
						String[] extFields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
						AuditDetail auditDetail = new AuditDetail(auditTranType, auditDetails.size()+1, extFields[0], extFields[1],
								extendedFieldRender.getBefImage(), extendedFieldRender);
						auditDetail.setExtended(true);
						auditDetails.add(auditDetail);
					}*/
				}
			}
			
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> doApprove(List<JointAccountDetail> jointAcDetailList, String tableType,
			String auditTranType, String finSourceId) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (JointAccountDetail jointAccountDetail : jointAcDetailList) {
			JointAccountDetail detail = new JointAccountDetail();
			BeanUtils.copyProperties(jointAccountDetail, detail);
			if (!jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
				jointAccountDetail.setRoleCode("");
				jointAccountDetail.setNextRoleCode("");
				jointAccountDetail.setTaskId("");
				jointAccountDetail.setNextTaskId("");
				jointAccountDetail.setWorkflowId(0);
				jointAccountDetail.setRecordType("");

				getJountAccountDetailDAO().save(jointAccountDetail, tableType);
			}
			
			
			if(!StringUtils.equals(finSourceId, PennantConstants.FINSOURCE_ID_API)) {
				getJountAccountDetailDAO().delete(jointAccountDetail, "_Temp");
			}
			
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail, jointAccountDetail.getExcludeFields());
			
			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], jointAccountDetail.getBefImage(), jointAccountDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<JointAccountDetail> jointAccountDetails, String tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (JointAccountDetail jointAccountDetail : jointAccountDetails) {
			getJountAccountDetailDAO().delete(jointAccountDetail, tableType);
			
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail, jointAccountDetail.getExcludeFields());
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], jointAccountDetail.getBefImage(), jointAccountDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	public AuditHeader doValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public List<AuditDetail> doValidation(List<JointAccountDetail> jointAcDetailList, long workflowId, String method, String auditTranType, String usrLanguage) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(jointAcDetailList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		JointAccountDetail jountAccountDetail = (JointAccountDetail) auditDetail.getModelData();
		JointAccountDetail tempJountAccountDetail = null;
		JointAccountDetail befJountAccountDetail = null;
		JointAccountDetail oldJountAccountDetail = null;
		

		if (jountAccountDetail.isWorkflow()) {
			tempJountAccountDetail = getJountAccountDetailDAO().getJountAccountDetailByRefId(
					jountAccountDetail.getFinReference(), jountAccountDetail.getJointAccountId(),"_Temp");
		} 
		
		befJountAccountDetail = getJountAccountDetailDAO().getJountAccountDetailByRefId(jountAccountDetail.getFinReference(), jountAccountDetail.getJointAccountId(), "");
		oldJountAccountDetail = jountAccountDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = jountAccountDetail.getFinReference();
		valueParm[1] = jountAccountDetail.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];

		if (jountAccountDetail.isNew()) { // for New record or new record into work flow

			if (!jountAccountDetail.isWorkflow()) {// With out Work flow only new records  
				if (befJountAccountDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befJountAccountDetail != null || tempJountAccountDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJountAccountDetail == null || tempJountAccountDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
								usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jountAccountDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befJountAccountDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldJountAccountDetail != null
							&& !oldJountAccountDetail.getLastMntOn().equals(
									befJountAccountDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempJountAccountDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempJountAccountDetail != null
						&& oldJountAccountDetail != null
						&& !oldJountAccountDetail.getLastMntOn().equals(
								tempJountAccountDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
				usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jountAccountDetail.isWorkflow()) {
			auditDetail.setBefImage(befJountAccountDetail);
		}
		return auditDetail;
	}


	/**
	 * getPrimaryExposureList
	 * 
	 * Return the list of primary finances Exposer List (self finances) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jountAccountDetail) {
		FinanceExposure overDueDetail = null;

		List<FinanceExposure> primaryExposureList = getJountAccountDetailDAO().getPrimaryExposureList(jountAccountDetail);

		if(primaryExposureList != null && !primaryExposureList.isEmpty()) {
			for (FinanceExposure finExposer : primaryExposureList) {
				overDueDetail = getJountAccountDetailDAO().getOverDueDetails(finExposer);
				if(overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);
				}
			}
		}

		return primaryExposureList;
	}


	/**
	 * getSecondaryExposureList
	 * 
	 * Return the list of secondary finances Exposer List(where the Customer is having joint finances) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail) {
		FinanceExposure overDueDetail = null;
		List<FinanceExposure> secondaryExposureList = getJountAccountDetailDAO().getSecondaryExposureList(jointAccountDetail);

		if(secondaryExposureList != null && !secondaryExposureList.isEmpty()) {
			for (FinanceExposure finExposer : secondaryExposureList) {
				overDueDetail = getJountAccountDetailDAO().getOverDueDetails(finExposer);
				if(overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);
				}
			}
		}

		return secondaryExposureList;
	}

	/**
	 * getGuarantorExposureList
	 * 
	 * Return the list of secondary  Gurantor Exposure List finances(where the Customer is Gurantor to others) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail
	 *            (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail) {
		FinanceExposure overDueDetail = null;

		List<FinanceExposure> guarantorExposureList = getJountAccountDetailDAO().getGuarantorExposureList(jointAccountDetail);

		if(guarantorExposureList != null) {
			for (FinanceExposure finExposer : guarantorExposureList) {
				overDueDetail = getJountAccountDetailDAO().getOverDueDetails(finExposer);
				if(overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);

				}
			}
		}

		return guarantorExposureList;

	}

	/**
	 * getExposureSummaryDetail
	 * 
	 * Sum of all the finances of financeAmount, currentExposer, overDueAmount respectively, For the corresponding
	 * Customer
	 * 
	 * @param List
	 *            <FinanceExposure> (exposerList)
	 * @return exposerSummaryDetail
	 */
	@Override
	public FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList) {
		FinanceExposure exposerSummaryDetail = new FinanceExposure();
		BigDecimal finaceAmout = BigDecimal.ZERO;
		BigDecimal currentExposer = BigDecimal.ZERO;
		BigDecimal overDueAmount = BigDecimal.ZERO;
		
		int dftCurntEdtField = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		
		if(exposerList != null && !exposerList.isEmpty()) {
			for (FinanceExposure financeExposure : exposerList) {
				finaceAmout = finaceAmout.add(financeExposure.getFinanceAmtinBaseCCY());
				currentExposer = currentExposer.add(financeExposure.getCurrentExpoSureinBaseCCY());
				if (financeExposure.getOverdueAmt() != null) {
					overDueAmount = overDueAmount.add(financeExposure.getOverdueAmtBaseCCY());
				}
			}
		}
		exposerSummaryDetail.setCcyEditField(dftCurntEdtField);
		exposerSummaryDetail.setFinanceAmtinBaseCCY(finaceAmout);
		exposerSummaryDetail.setCurrentExpoSureinBaseCCY(currentExposer);
		exposerSummaryDetail.setOverdueAmtBaseCCY(overDueAmount);

		return exposerSummaryDetail;
	}
	
	
	@Override
	public List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type) {
		return getJountAccountDetailDAO().getJountAccountDetailByFinRef(finReference, type);
	}

	/**
	 * getJoinAccountDetail
	 * 
	 * Return the list of joint account holders financial details and customer details based on the corresponding
	 * finance reference
	 * 
	 * @param String
	 *            (finReference)
	 * @param String
	 *            (tableType)
	 * @return List<JointAccountDetail>
	 */
	@Override
	public List<JointAccountDetail> getJoinAccountDetail(String finReference, String tableType) {
		List<FinanceExposure> primaryList = null;
		List<FinanceExposure> secoundaryList = null;
		List<FinanceExposure> guarantorList = null;

		List<JointAccountDetail>  jointAccountDetailList = getJountAccountDetailByFinRef(finReference, tableType);
		
		if(jointAccountDetailList != null && !jointAccountDetailList.isEmpty()) {
			for (JointAccountDetail detail : jointAccountDetailList) {	
				BigDecimal currentExpoSure = BigDecimal.ZERO;

				// set the primary exposer details to Joint Account Details
				primaryList = getJountAccountDetailDAO().getPrimaryExposureList(detail);
				currentExpoSure =  doFillExposureDetails(primaryList, detail);
				detail.setPrimaryExposure(String.valueOf(currentExpoSure));

				// set the secondary exposer details to Joint Account 
				secoundaryList = getJountAccountDetailDAO().getSecondaryExposureList(detail);
				currentExpoSure = doFillExposureDetails(secoundaryList, detail);
				detail.setSecondaryExposure(String.valueOf(currentExpoSure));

				// set the exposer details to Joint Account
				guarantorList = getJountAccountDetailDAO().getGuarantorExposureList(detail);
				currentExpoSure =  doFillExposureDetails(guarantorList, detail);
				detail.setGuarantorExposure(String.valueOf(currentExpoSure));

				detail.setCustomerIncomeList(getJointAccountIncomeList(detail.getCustID()));
			}

		}

		return jointAccountDetailList;
	}
	
	@Override
	public List<CustomerIncome> getJointAccountIncomeList(long custID){
		
		return getCustomerIncomeDAO().getCustomerIncomeByCustomer(custID,false,"");
	} 
	
	@Override
	public List<FinanceExposure> getJointExposureList(List<String> listCIF){
		
		List<FinanceExposure> exposures= getJountAccountDetailDAO().getPrimaryExposureList(listCIF);
		exposures.addAll(getJountAccountDetailDAO().getSecondaryExposureList(listCIF));
		return exposures;
	}
	
	
	@Override
	public BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, JointAccountDetail detail) {
		BigDecimal currentExpoSure = BigDecimal.ZERO;
		if(primaryList != null && !primaryList.isEmpty() )  {
			for (FinanceExposure exposer : primaryList) {
				if(exposer != null) {
					String toCcy = SysParamUtil.getValueAsString("APP_DFT_CURR");
					String  fromCcy = exposer.getFinCCY();
					currentExpoSure = currentExpoSure.add(CalculationUtil.getConvertedAmount(fromCcy, toCcy, exposer.getCurrentExpoSure()));
					detail.setStatus(exposer.getStatus());
					detail.setWorstStatus(exposer.getWorstStatus());

					if(exposer.getOverdueAmt() != null
							&& BigDecimal.ZERO.compareTo(exposer.getOverdueAmt()) > 0) {
						exposer.setOverdue(true);
					}
				}
			}
		}
		return currentExpoSure;
	}

	private void setExposerDetails(FinanceExposure overDueDetail, FinanceExposure finExposer) {
		BigDecimal finaceAmout;
		BigDecimal overDueAmount;
		BigDecimal exposerAmout;

		String toCcy = SysParamUtil.getValueAsString("APP_DFT_CURR");
		String  fromCcy = finExposer.getFinCCY();	

		if(finExposer.getFinanceAmt() != null) {
			finaceAmout = CalculationUtil.getConvertedAmount(fromCcy, toCcy, finExposer.getFinanceAmt());	
			finExposer.setFinanceAmtinBaseCCY(finaceAmout);
		}

		if(finExposer.getCurrentExpoSure() != null) {
			exposerAmout = CalculationUtil.getConvertedAmount(fromCcy, toCcy, finExposer.getCurrentExpoSure());	
			finExposer.setCurrentExpoSureinBaseCCY(exposerAmout);
		}

		if(overDueDetail.getOverdueAmt() != null && overDueDetail.getOverdueAmt().compareTo(BigDecimal.ZERO) > 0) {
			finExposer.setOverdueAmt(overDueDetail.getOverdueAmt());
			overDueAmount = CalculationUtil.getConvertedAmount(fromCcy, toCcy, overDueDetail.getOverdueAmt());
			finExposer.setOverdueAmtBaseCCY(overDueAmount);
			finExposer.setOverdue(true);
		}

		finExposer.setPastdueDays(overDueDetail.getPastdueDays());
	}
	
	
	private List<AuditDetail> getAuditDetail(List<JointAccountDetail> jountAccountDetailList, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		JointAccountDetail object = new JointAccountDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < jountAccountDetailList.size(); i++) {

			JointAccountDetail jountAccountDetail = jountAccountDetailList.get(i);
			jountAccountDetail.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (jountAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jountAccountDetail.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (jountAccountDetail.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				jountAccountDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jountAccountDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jountAccountDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| jountAccountDetail.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}


			if (StringUtils.isNotEmpty(jountAccountDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], jountAccountDetail.getBefImage(), jountAccountDetail));
			}
		}


		logger.debug("Leaving");
		return auditDetails;
	}

}