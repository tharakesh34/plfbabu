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
 *
 * FileName    		: ReportConfigurationServiceImpl.java								        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
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
package com.pennant.backend.service.reports.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.reports.ReportConfigurationDAO;
import com.pennant.backend.dao.reports.ReportFilterFieldsDAO;
import com.pennant.backend.dao.reports.ReportSearchTemplateDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportSearchTemplate;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class ReportConfigurationServiceImpl extends GenericService<ReportConfiguration> implements ReportConfigurationService{
	private static Logger logger = Logger.getLogger(ReportConfigurationServiceImpl .class);

	private ReportSearchTemplateDAO reportSearchTemplateDAO;
	private AuditHeaderDAO auditHeaderDAO;	
	private ReportConfigurationDAO reportConfigurationDAO;
	private ReportFilterFieldsDAO reportFilterFieldsDAO;

	public ReportConfigurationServiceImpl() {
		super();
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ReportConfiguration/ReportConfiguration_Temp by using ReportConfigurationDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ReportConfigurationDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtReportConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader 
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		ReportConfiguration reportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();

		if (reportConfiguration.isWorkflow()) {
			tableType="_Temp";
		}

		if (reportConfiguration.isNew()) {
			reportConfiguration.setId(getReportConfigurationDAO().save(reportConfiguration,tableType));
			auditHeader.getAuditDetail().setModelData(reportConfiguration);
			auditHeader.setAuditReference(String.valueOf(reportConfiguration.getReportID()));
		}else{
			getReportConfigurationDAO().update(reportConfiguration,tableType);
		}

		//Retrieving List of Audit Details For reportFilterFields  related modules
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditHeader.setAuditDetails(processingReportFilterFieldsList(auditHeader.getAuditDetails(),tableType,reportConfiguration));
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ReportConfiguration by using ReportConfigurationDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtReportConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		ReportConfiguration reportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		getReportFilterFieldsDAO().deleteByReportId(reportConfiguration.getId(), "");
		getReportConfigurationDAO().delete(reportConfiguration,"");

		auditHeader=resetAuditDetails(auditHeader, reportConfiguration, auditHeader.getAuditTranType());
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getReportConfigurationById fetch the details by using ReportConfigurationDAO's getReportConfigurationById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ReportConfiguration
	 */
	@Override
	public ReportConfiguration getReportConfigurationById(long id) {
		ReportConfiguration reportConfiguration = getReportConfigurationDAO().getReportConfigurationById(id,"_View");
		if(reportConfiguration!=null){
			reportConfiguration.setListReportFieldsDetails(
					getReportFilterFieldsDAO().getReportFilterFieldsByReportId(reportConfiguration.getReportID(), "_View"));
		}
		return reportConfiguration;
	}

	/**
	 * getApprovedReportConfigurationById fetch the details by using ReportConfigurationDAO's
	 * getReportConfigurationById method . with parameter id and type as blank. it fetches
	 * the approved records from the ReportConfiguration.
	 * 
	 * @param id
	 *            (String)
	 * @return ReportConfiguration
	 */
	public ReportConfiguration getApprovedReportConfigurationById(long id) {
		ReportConfiguration aReportConfiguration =  getReportConfigurationDAO().getReportConfigurationById(id,"_View");
		if(aReportConfiguration!=null){
			aReportConfiguration.setListReportFieldsDetails(
					getReportFilterFieldsDAO().getReportFilterFieldsByReportId(aReportConfiguration.getReportID(), "_View"));
		}
		return aReportConfiguration;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getReportConfigurationDAO().delete with parameters reportConfiguration,"" b) NEW Add new
	 * record in to main table by using getReportConfigurationDAO().save with parameters
	 * reportConfiguration,"" c) EDIT Update record in the main table by using
	 * getReportConfigurationDAO().update with parameters reportConfiguration,"" 3) Delete the record
	 * from the workFlow table by using getReportConfigurationDAO().delete with parameters
	 * reportConfiguration,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtReportConfiguration by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtReportConfiguration by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");

		String tranType="";		
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails= auditHeader.getAuditDetails();
		ReportConfiguration reportConfiguration = new ReportConfiguration();
		BeanUtils.copyProperties((ReportConfiguration) auditHeader.getAuditDetail().getModelData(), reportConfiguration);

		if (reportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getReportConfigurationDAO().delete(reportConfiguration,"");

		} else {
			reportConfiguration.setRoleCode("");
			reportConfiguration.setNextRoleCode("");
			reportConfiguration.setTaskId("");
			reportConfiguration.setNextTaskId("");
			reportConfiguration.setWorkflowId(0);

			if (reportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				reportConfiguration.setRecordType("");
				getReportConfigurationDAO().save(reportConfiguration,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				reportConfiguration.setRecordType("");
				getReportConfigurationDAO().update(reportConfiguration,"");
			}
			//Retrieving List of Audit Details For utilityDetail  related modules
			if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
				auditDetails = processingReportFilterFieldsList(auditHeader.getAuditDetails(),"",reportConfiguration);
			}
		}
		getReportConfigurationDAO().delete(reportConfiguration,"_Temp");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(auditDetails);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reportConfiguration);
		auditHeader=resetAuditDetails(auditHeader, reportConfiguration, tranType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getReportConfigurationDAO().delete with parameters
	 * reportConfiguration,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtReportConfiguration by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		ReportConfiguration reportConfiguration= (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReportConfigurationDAO().delete(reportConfiguration,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

	//	ReportConfiguration aReportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		//String usrLanguage = aReportConfiguration.getUserDetails().getUsrLanguage();

		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			//auditHeader.setAuditDetails(getDetailValidation().detailListValidation(auditHeader.getAuditDetails(), method,usrLanguage));
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				auditHeader.setErrorList(detail.getErrorDetails());	
			}
		}
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getReportConfigurationDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		ReportConfiguration reportConfiguration = (ReportConfiguration) auditDetail.getModelData();
		ReportConfiguration tempReportConfiguration = null;
		if (reportConfiguration.isWorkflow()) {
			tempReportConfiguration = getReportConfigurationDAO().getReportConfigurationById(reportConfiguration.getId(),
			"_Temp");
		}
		ReportConfiguration befReportConfiguration = getReportConfigurationDAO().getReportConfigurationById(
				reportConfiguration.getId(), "");

		ReportConfiguration oldReportConfiguration = reportConfiguration.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(reportConfiguration.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_ReportId")+":"+valueParm[0];

		if (reportConfiguration.isNew()) { // for New record or new record into work flow

			if (!reportConfiguration.isWorkflow()) {// With out Work flow only new records
				if (befReportConfiguration != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}
			} else { // with work flow
				if (reportConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befReportConfiguration != null || tempReportConfiguration != null) { // if records already
																								// exists in the main
																								// table
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41014", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befReportConfiguration == null || tempReportConfiguration != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!reportConfiguration.isWorkflow()) { // With out Work flow for update and delete

				if (befReportConfiguration == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldReportConfiguration != null
							&& !oldReportConfiguration.getLastMntOn().equals(
									befReportConfiguration.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempReportConfiguration == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempReportConfiguration != null && oldReportConfiguration != null
						&& !oldReportConfiguration.getLastMntOn().equals(
								tempReportConfiguration.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !reportConfiguration.isWorkflow()) {
			auditDetail.setBefImage(befReportConfiguration);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method){
		logger.debug("Entering ");

		ReportConfiguration reportConfiguration = (ReportConfiguration) auditHeader.getAuditDetail().getModelData();
		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (reportConfiguration.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(reportConfiguration.getListReportFieldsDetails()!=null && reportConfiguration.getListReportFieldsDetails().size()>0){
			auditHeader.setAuditDetails(setReportFilterFieldsAuditData(reportConfiguration,auditTranType,method));
		}

		logger.debug("Leaving ");
		return auditHeader;
	}
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param reportConfiguration
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setReportFilterFieldsAuditData(ReportConfiguration reportConfiguration,String auditTranType,String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete=false;

		if ((PennantConstants.RECORD_TYPE_DEL.equals(
				reportConfiguration.getRecordType()) && "doApprove".equalsIgnoreCase(method)) || "delete".equals(method)) {
			delete=true;
		}

		for (int i = 0; i < reportConfiguration.getListReportFieldsDetails().size(); i++) {

			ReportFilterFields reportFilterFields  = reportConfiguration.getListReportFieldsDetails().get(i);
			reportFilterFields.setWorkflowId(reportConfiguration.getWorkflowId());
			reportFilterFields.setReportID(reportConfiguration.getReportID());

			boolean isRcdType= false;

			if(delete){
				reportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			}else{
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(reportFilterFields.getRecordType())) {
					reportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType=true;
				}else if (PennantConstants.RCD_UPD.equalsIgnoreCase(reportFilterFields.getRecordType())) {
					reportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType=true;
				}else if (PennantConstants.RCD_DEL.equalsIgnoreCase(reportFilterFields.getRecordType())) {
					reportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isRcdType=true;
				}
			}


			if("saveOrUpdate".equals(method) && (isRcdType && reportFilterFields.isWorkflow())){
				reportFilterFields.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(reportFilterFields.getRecordType())) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(reportFilterFields.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(reportFilterFields.getRecordType())) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			reportFilterFields.setRecordStatus(reportConfiguration.getRecordStatus());
			reportFilterFields.setUserDetails(reportConfiguration.getUserDetails());
			reportFilterFields.setLastMntOn(reportConfiguration.getLastMntOn());
			reportFilterFields.setLastMntBy(reportConfiguration.getLastMntBy());
			if(StringUtils.isNotEmpty(reportFilterFields.getRecordType())){
				auditDetails.add(new AuditDetail(auditTranType, i+1, reportFilterFields.getBefImage(), reportFilterFields));
			}

		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for utilityDetail
	 * @param auditDetails
	 * @param type
	 * @param beneficiaryId
	 * @return
	 */
	private List<AuditDetail> processingReportFilterFieldsList(List<AuditDetail> auditDetails
			, String type,ReportConfiguration reportConfiguration) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;
		List<AuditDetail> list= new ArrayList<AuditDetail>();

		for (AuditDetail auditDetail : auditDetails) {

			ReportFilterFields reportFilterFields = (ReportFilterFields) auditDetail.getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			reportFilterFields.setReportID(reportConfiguration.getReportID());
			if (StringUtils.isEmpty(type)) {
				reportFilterFields.setVersion(reportFilterFields.getVersion()+1);
				approveRec=true;
			}else{
				reportFilterFields.setRoleCode(reportConfiguration.getRoleCode());
				reportFilterFields.setNextRoleCode(reportConfiguration.getNextRoleCode());
				reportFilterFields.setTaskId(reportConfiguration.getTaskId());
				reportFilterFields.setNextTaskId(reportConfiguration.getNextTaskId());
			}

			if (reportFilterFields.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (reportFilterFields.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (reportFilterFields.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(reportFilterFields.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			ReportFilterFields tempDetail=new ReportFilterFields();
			BeanUtils.copyProperties(reportFilterFields,tempDetail);


			if(approveRec){
				reportFilterFields.setRoleCode("");
				reportFilterFields.setNextRoleCode("");
				reportFilterFields.setTaskId("");
				reportFilterFields.setNextTaskId("");
				reportFilterFields.getRecordType();
				reportFilterFields.getRecordStatus();
				reportFilterFields.setRecordType("");
				reportFilterFields.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getReportFilterFieldsDAO().save(reportFilterFields, type);
			}

			if (updateRecord) {
				getReportFilterFieldsDAO().update(reportFilterFields, type);
			}

			if (deleteRecord) {
				getReportFilterFieldsDAO().delete(reportFilterFields, type);
			}

			if(saveRecord || updateRecord || deleteRecord){
				if(!reportFilterFields.isWorkflow()){
					auditDetail.setModelData(reportFilterFields);
				}else{
					auditDetail.setModelData(tempDetail);
				}
				list.add(auditDetail);
			}
		}
		logger.debug("Leaving ");
		return list;	
	}
	/**
	 * This method saves or update ReportSearchTemplate table 
	 */
	@Override
	public void saveOrUpdateSearchTemplate(
			List<ReportSearchTemplate> aReportSearchTemplateList, boolean isNew) {
		logger.debug("Entering");
		for(int i=0;i<aReportSearchTemplateList.size();i++){
			getReportSearchTemplateDAO().save(aReportSearchTemplateList.get(i));
		}
		logger.debug("Leaving");

	}
	/**
	 * This method gets Map where key is templateName and value is List<ReportSearchTemplate> 
	 */
	@Override
	public Map<Object ,List<ReportSearchTemplate>> getTemplatesByReportID(long reportId,long usrID){
		logger.debug("Entering");
		List<ReportSearchTemplate> listReportSearchTemplate=  getReportSearchTemplateDAO().getReportSearchTemplateByReportId(reportId,usrID);
		Map<Object ,List<ReportSearchTemplate>> resultMap = NumberToEnglishWords.getSubListMapGroupingByField(listReportSearchTemplate,"templateName","getTemplateName");

		return resultMap;
	}
	/**
	 * 
	 * @param auditHeader
	 * @param reportConfiguration
	 * @param tranType
	 * @return
	 */
	private AuditHeader resetAuditDetails(AuditHeader auditHeader, ReportConfiguration reportConfiguration,String tranType){
		logger.debug("Entering :");
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reportConfiguration);

		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			List<AuditDetail> auditDetails= new ArrayList<AuditDetail>();

			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				ReportFilterFields reportFilterFileds=(ReportFilterFields) detail.getModelData(); 
				detail.setAuditTranType(tranType);
				reportFilterFileds.setRecordType("");
				reportFilterFileds.setRoleCode("");
				reportFilterFileds.setNextRoleCode("");
				reportFilterFileds.setTaskId("");
				reportFilterFileds.setNextTaskId("");
				reportFilterFileds.setWorkflowId(0);
				detail.setModelData(reportFilterFileds);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}
		logger.debug("Leaving :");
		return auditHeader;
	} 
	/**
	 * This Method Fetches records from ReportSearchTemplate
	 */
	@Override
	public int getRecordCountByTemplateName(long reportId,long usrId,String templateName) {
		return getReportSearchTemplateDAO().getRecordCountByTemplateName(reportId, usrId, templateName);
	}
	/**
	 * 
	 */
	@Override
	public boolean deleteSearchTemplate(long reportId,long usrId,String templateName) {
		return getReportSearchTemplateDAO().delete(reportId, usrId, templateName);
	}

	//GETTERS and Setters 
	public void setReportSearchTemplateDAO(ReportSearchTemplateDAO reportSearchTemplateDAO) {
		this.reportSearchTemplateDAO = reportSearchTemplateDAO;
	}

	public ReportSearchTemplateDAO getReportSearchTemplateDAO() {
		return reportSearchTemplateDAO;
	}


	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ReportConfigurationDAO getReportConfigurationDAO() {
		return reportConfigurationDAO;
	}	
	public void setReportConfigurationDAO(ReportConfigurationDAO reportConfigurationDAO) {
		this.reportConfigurationDAO = reportConfigurationDAO;
	}

	public ReportFilterFieldsDAO getReportFilterFieldsDAO() {
		return reportFilterFieldsDAO;
	}
	public void setReportFilterFieldsDAO(ReportFilterFieldsDAO reportFilterFieldsDAO) {
		this.reportFilterFieldsDAO = reportFilterFieldsDAO;
	}
	@Override
	public ReportConfiguration getReportConfiguration() {
		return getReportConfigurationDAO().getReportConfiguration();
	}

	/**
	 * @return the reportConfiguration for New Record
	 */
	@Override
	public ReportConfiguration getNewReportConfiguration() {
		return getReportConfigurationDAO().getNewReportConfiguration();
	}

	//Month End Report Data Fetching
	
	@Override
    public List<ValueLabel> getMonthEndReportGrpCodes() {
	    return getReportConfigurationDAO().getMonthEndReportGrpCodes();
    }

	@Override
    public List<ValueLabel> getReportListByGrpCode(String grpCode) {
	    return getReportConfigurationDAO().getReportListByGrpCode(grpCode);
    }


}