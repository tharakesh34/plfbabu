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
 * FileName    		:  ScoringGroupServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rmtmasters.ScoringGroupDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.rmtmasters.ScoringGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>ScoringGroup</b>.<br>
 * 
 */
public class ScoringGroupServiceImpl extends GenericService<ScoringGroup> implements ScoringGroupService {
	private static final Logger logger = Logger.getLogger(ScoringGroupServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private ScoringGroupDAO    scoringGroupDAO;
	private ScoringSlabDAO     scoringSlabDAO;
	private ScoringMetricsDAO  scoringMetricsDAO;
	private RuleDAO ruleDAO;
	
	public ScoringGroupServiceImpl() {
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
	 * @return the scoringGroupDAO
	 */
	public ScoringGroupDAO getScoringGroupDAO() {
		return scoringGroupDAO;
	}
	/**
	 * @param scoringGroupDAO the scoringGroupDAO to set
	 */
	public void setScoringGroupDAO(ScoringGroupDAO scoringGroupDAO) {
		this.scoringGroupDAO = scoringGroupDAO;
	}
	
	public RuleDAO getRuleDAO() {
    	return ruleDAO;
    }
	public void setRuleDAO(RuleDAO ruleDAO) {
    	this.ruleDAO = ruleDAO;
    }


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table RMTScoringGroup/RMTScoringGroup_Temp 
	 * 			by using ScoringGroupDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using ScoringGroupDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtRMTScoringGroup by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		ScoringGroup scoringGroup = (ScoringGroup) auditHeader.getAuditDetail().getModelData();

		if (scoringGroup.isWorkflow()) {
			tableType="_Temp";
		}
		if (scoringGroup.isNew()) {
			scoringGroup.setId(getScoringGroupDAO().save(scoringGroup,tableType));
			auditHeader.getAuditDetail().setModelData(scoringGroup);
			auditHeader.setAuditReference(String.valueOf(scoringGroup.getScoreGroupId()));
		}else{
			getScoringGroupDAO().update(scoringGroup,tableType);
		}
		
		//Retrieving List of Audit Details
		if(scoringGroup.getScoringSlabList()!=null && scoringGroup.getScoringSlabList().size()>0){
			List<AuditDetail> details = scoringGroup.getAuditDetailMap().get("ScoringSlab");
			details = processingScoringSlabList(details,tableType,scoringGroup.getScoreGroupId());
			auditDetails.addAll(details);
		}
		
		List<ScoringMetrics> scoringMetricList = new ArrayList<ScoringMetrics>();
		if(scoringGroup.getScoringMetricsList()!=null && scoringGroup.getScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getScoringMetricsList());
		}
		if(scoringGroup.getFinScoringMetricsList()!=null && scoringGroup.getFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getFinScoringMetricsList());
		}
		if(scoringGroup.getNonFinScoringMetricsList()!=null && scoringGroup.getNonFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getNonFinScoringMetricsList());
		}
		
		if(scoringMetricList != null && scoringMetricList.size() > 0){
			List<AuditDetail> details = scoringGroup.getAuditDetailMap().get("ScoringMetrics");
			details = processingScoringMetricsList(details,tableType,scoringGroup.getScoreGroupId());
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
	 * 2)	delete Record for the DB table RMTScoringGroup by using ScoringGroupDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtRMTScoringGroup by using auditHeaderDAO.addAudit(auditHeader)    
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
		ScoringGroup scoringGroup = (ScoringGroup) auditHeader.getAuditDetail().getModelData();
		getScoringGroupDAO().delete(scoringGroup,"");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(scoringGroup, "",auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getScoringGroupById fetch the details by using ScoringGroupDAO's getScoringGroupById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return ScoringGroup
	 */

	@Override
	public ScoringGroup getScoringGroupById(long id) {
		ScoringGroup scoringGroup=getScoringGroupDAO().getScoringGroupById(id,"_View");
		scoringGroup.setScoringSlabList(getScoringSlabDAO().getScoringSlabsByScoreGrpId(id,"_View"));
		if(PennantConstants.PFF_CUSTCTG_CORP.equals(scoringGroup.getCategoryType()) || 
				PennantConstants.PFF_CUSTCTG_SME.equals(scoringGroup.getCategoryType())){
			
			//Financial Scoring Metric Details
			scoringGroup.setFinScoringMetricsList(getScoringMetricsDAO().getScoringMetricsByScoreGrpId(id, "F" ,"_View"));
			if(scoringGroup.getFinScoringMetricsList() != null && scoringGroup.getFinScoringMetricsList().size() > 0){

				/*List<Rule> ruleList = getRuleDAO().getRulesByGroupIdList(id, "F", "");
				ScoringMetrics metric = null;

				for (Rule rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(rule.getRuleCode());
					metric.setLovDescScoringCodeDesc(rule.getRuleCodeDesc());
					metric.setLovDescSQLRule(rule.getSQLRule());
					List<ScoringMetrics> subMetricList = null;
					if(scoringGroup.getLovDescFinScoreMap().containsKey(rule.getGroupId())){
						subMetricList = scoringGroup.getLovDescFinScoreMap().get(rule.getGroupId());
					}else{
						subMetricList = new ArrayList<ScoringMetrics>();
					}
					subMetricList.add(metric);
					scoringGroup.getLovDescFinScoreMap().put(rule.getGroupId(),subMetricList);
				}*/
				
				List<NFScoreRuleDetail> ruleList = getRuleDAO().getNFRulesByGroupId(id, "F", "");
				ScoringMetrics metric = null;
				for (NFScoreRuleDetail rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					List<ScoringMetrics> subMetricList = null;
					if(scoringGroup.getLovDescFinScoreMap().containsKey(rule.getGroupId())){
						subMetricList = scoringGroup.getLovDescFinScoreMap().get(rule.getGroupId());
					}else{
						subMetricList = new ArrayList<ScoringMetrics>();
					}
					subMetricList.add(metric);
					scoringGroup.getLovDescFinScoreMap().put(rule.getGroupId(),subMetricList);
				}
			}
			
			//Non - Financial Scoring Metric Details
			scoringGroup.setNonFinScoringMetricsList(getScoringMetricsDAO().getScoringMetricsByScoreGrpId(id, "N" ,"_View"));
			if(scoringGroup.getNonFinScoringMetricsList() != null && scoringGroup.getNonFinScoringMetricsList().size() > 0){

				List<NFScoreRuleDetail> ruleList = getRuleDAO().getNFRulesByGroupId(id, "N", "");
				ScoringMetrics metric = null;
				for (NFScoreRuleDetail rule : ruleList) {
					metric = new ScoringMetrics();
					metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
					metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
					metric.setLovDescMetricMaxPoints(rule.getMaxScore());
					List<ScoringMetrics> subMetricList = null;
					if(scoringGroup.getLovDescFinScoreMap().containsKey(rule.getGroupId())){
						subMetricList = scoringGroup.getLovDescFinScoreMap().get(rule.getGroupId());
					}else{
						subMetricList = new ArrayList<ScoringMetrics>();
					}
					subMetricList.add(metric);
					scoringGroup.getLovDescFinScoreMap().put(rule.getGroupId(),subMetricList);
				}
			}
			
		}else{
			scoringGroup.setScoringMetricsList(getScoringMetricsDAO().getScoringMetricsByScoreGrpId(id, "R" , "_View"));
		}
		return scoringGroup;
	}
	/**
	 * getApprovedScoringGroupById fetch the details by using ScoringGroupDAO's getScoringGroupById method .
	 * with parameter id and type as blank. it fetches the approved records from the RMTScoringGroup.
	 * @param id (int)
	 * @return ScoringGroup
	 */

	public ScoringGroup getApprovedScoringGroupById(long id) {
		return getScoringGroupDAO().getScoringGroupById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getScoringGroupDAO().delete
	 *          with parameters scoringGroup,""
	 * 		b)  NEW		Add new record in to main table by using getScoringGroupDAO().save
	 *           with parameters scoringGroup,""
	 * 		c)  EDIT	Update record in the main table by using getScoringGroupDAO().update
	 *           with parameters scoringGroup,""
	 * 3)	Delete the record from the workFlow table by using getScoringGroupDAO().delete  
	 *      with parameters scoringGroup,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtRMTScoringGroup by using auditHeaderDAO.addAudit(auditHeader) 
	 *      for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtRMTScoringGroup by using auditHeaderDAO.addAudit(auditHeader) 
	 *      based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ScoringGroup scoringGroup = new ScoringGroup();
		BeanUtils.copyProperties((ScoringGroup) auditHeader.getAuditDetail().getModelData(), scoringGroup);

		if (scoringGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getScoringGroupDAO().delete(scoringGroup,"");
			auditDetails.addAll(getListAuditDetails(listDeletion(scoringGroup, "", auditHeader.getAuditTranType())));

		} else {
			scoringGroup.setRoleCode("");
			scoringGroup.setNextRoleCode("");
			scoringGroup.setTaskId("");
			scoringGroup.setNextTaskId("");
			scoringGroup.setWorkflowId(0);

			if (scoringGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				scoringGroup.setRecordType("");
				getScoringGroupDAO().save(scoringGroup,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				scoringGroup.setRecordType("");
				getScoringGroupDAO().update(scoringGroup,"");
			}

			if(scoringGroup.getScoringSlabList()!=null && scoringGroup.getScoringSlabList().size()>0){
				List<AuditDetail> details = scoringGroup.getAuditDetailMap().get("ScoringSlab");
				details = processingScoringSlabList(details,"",scoringGroup.getScoreGroupId());
				auditDetails.addAll(details);
			}
			
			List<ScoringMetrics> scoringMetricList = new ArrayList<ScoringMetrics>();
			if(scoringGroup.getScoringMetricsList()!=null && scoringGroup.getScoringMetricsList().size()>0){
				scoringMetricList.addAll(scoringGroup.getScoringMetricsList());
			}
			if(scoringGroup.getFinScoringMetricsList()!=null && scoringGroup.getFinScoringMetricsList().size()>0){
				scoringMetricList.addAll(scoringGroup.getFinScoringMetricsList());
			}
			if(scoringGroup.getNonFinScoringMetricsList()!=null && scoringGroup.getNonFinScoringMetricsList().size()>0){
				scoringMetricList.addAll(scoringGroup.getNonFinScoringMetricsList());
			}
			
			if(scoringMetricList != null && scoringMetricList.size() > 0){
				List<AuditDetail> details = scoringGroup.getAuditDetailMap().get("ScoringMetrics");
				details = processingScoringMetricsList(details,"",scoringGroup.getScoreGroupId());
				auditDetails.addAll(details);
			}
			
		}

		getScoringGroupDAO().delete(scoringGroup,"_Temp");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(scoringGroup
				, "_Temp",auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(scoringGroup);

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getScoringGroupDAO().delete 
	 * with parameters scoringGroup,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtRMTScoringGroup by using 
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ScoringGroup scoringGroup = (ScoringGroup) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getScoringGroupDAO().delete(scoringGroup,"_Temp");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(scoringGroup
				, "_Temp",auditHeader.getAuditTranType())));
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
	 * 5)	for any mismatch conditions Fetch the error details from getScoringGroupDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */


	private AuditHeader businessValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ScoringGroup scoringGroup= (ScoringGroup) auditDetail.getModelData();

		ScoringGroup tempScoringGroup= null;
		if (scoringGroup.isWorkflow()){
			tempScoringGroup = getScoringGroupDAO().getScoringGroupById(scoringGroup.getId(), "_Temp");
		}
		ScoringGroup befScoringGroup= getScoringGroupDAO().getScoringGroupById(scoringGroup.getId(), "");

		ScoringGroup oldScoringGroup= scoringGroup.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(scoringGroup.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_ScoreGroupId")+":"+valueParm[0];

		if (scoringGroup.isNew()){ // for New record or new record into work flow

			if (!scoringGroup.isWorkflow()){// With out Work flow only new records  
				if (befScoringGroup !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (scoringGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befScoringGroup !=null || tempScoringGroup!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befScoringGroup ==null || tempScoringGroup!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!scoringGroup.isWorkflow()){	// With out Work flow for update and delete

				if (befScoringGroup ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldScoringGroup!=null && !oldScoringGroup.getLastMntOn().equals(befScoringGroup.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempScoringGroup==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempScoringGroup!=null && oldScoringGroup!=null && !oldScoringGroup.getLastMntOn().equals(tempScoringGroup.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !scoringGroup.isWorkflow()){
			scoringGroup.setBefImage(befScoringGroup);	
		}

		return auditDetail;
	}
	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		ScoringGroup scoringGroup = (ScoringGroup) auditHeader.getAuditDetail().getModelData();

		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (scoringGroup.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(scoringGroup.getScoringSlabList()!=null && scoringGroup.getScoringSlabList().size()>0){
			auditDetailMap.put("ScoringSlab", setScoringSlabAuditData(scoringGroup,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("ScoringSlab"));
		}
		
		List<ScoringMetrics> scoringMetricList = new ArrayList<ScoringMetrics>();
		if(scoringGroup.getScoringMetricsList()!=null && scoringGroup.getScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getScoringMetricsList());
		}
		if(scoringGroup.getFinScoringMetricsList()!=null && scoringGroup.getFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getFinScoringMetricsList());
		}
		if(scoringGroup.getNonFinScoringMetricsList()!=null && scoringGroup.getNonFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getNonFinScoringMetricsList());
		}
		
		if(scoringMetricList != null && scoringMetricList.size() > 0){
			auditDetailMap.put("ScoringMetrics", setScoringMetricsAuditData(scoringMetricList,
					scoringGroup,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("ScoringMetrics"));
		}

		scoringGroup.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(scoringGroup);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}

	private List<AuditDetail> setScoringSlabAuditData(ScoringGroup scoringGroup,String auditTranType,String method) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ScoringSlab());


		for (int i = 0; i < scoringGroup.getScoringSlabList().size(); i++) {
			ScoringSlab scoringSlab  = scoringGroup.getScoringSlabList().get(i);
			
			if (StringUtils.isEmpty(scoringSlab.getRecordType())) {
				continue;
			}
			
			scoringSlab.setWorkflowId(scoringGroup.getWorkflowId());
			scoringSlab.setScoreGroupId(scoringGroup.getScoreGroupId());

			boolean isRcdType= false;

			if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				//isRcdType=true;
			}

			if("saveOrUpdate".equals(method) && isRcdType ){
				scoringSlab.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}
			scoringSlab.setLovDescScoreGroupCode(scoringGroup.getScoreGroupCode());
			scoringSlab.setRecordStatus(scoringGroup.getRecordStatus());
			scoringSlab.setUserDetails(scoringGroup.getUserDetails());
			scoringSlab.setLastMntOn(scoringGroup.getLastMntOn());
			scoringSlab.setLastMntBy(scoringGroup.getLastMntBy());
			
			auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], scoringSlab.getBefImage(), scoringSlab));
		}
		
		logger.debug("Leaving ");
		return auditDetails;
	}
	private List<AuditDetail> setScoringMetricsAuditData(List<ScoringMetrics> scoringMetricList,
			ScoringGroup scoringGroup,String auditTranType,String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ScoringMetrics());


		for (int i = 0; i < scoringMetricList.size(); i++) {

			ScoringMetrics scoringMetrics  = scoringMetricList.get(i);
			scoringMetrics.setWorkflowId(scoringGroup.getWorkflowId());
			scoringMetrics.setScoreGroupId(scoringGroup.getScoreGroupId());

			boolean isRcdType= false;

			if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				//isRcdType=true;
			}

			if("saveOrUpdate".equals(method) && isRcdType ){
				scoringMetrics.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}
			scoringMetrics.setLovDescScoringGroupCode(scoringGroup.getScoreGroupCode());
			scoringMetrics.setRecordStatus(scoringGroup.getRecordStatus());
			scoringMetrics.setUserDetails(scoringGroup.getUserDetails());
			scoringMetrics.setLastMntOn(scoringGroup.getLastMntOn());
			scoringMetrics.setLastMntBy(scoringGroup.getLastMntBy());
			if(StringUtils.isNotEmpty(scoringMetrics.getRecordType())){
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], scoringMetrics.getBefImage(), scoringMetrics));
			}

		}
		logger.debug("Leaving ");
		return auditDetails;
	}
	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingScoringSlabList(List<AuditDetail> auditDetails, String type,long scoreGroupId) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ScoringSlab scoringSlab = (ScoringSlab) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			scoringSlab.setScoreGroupId(scoreGroupId);
			if (StringUtils.isEmpty(type)) {
				approveRec=true;
				scoringSlab.setVersion(scoringSlab.getVersion()+1);
				scoringSlab.setRoleCode("");
				scoringSlab.setNextRoleCode("");
				scoringSlab.setTaskId("");
				scoringSlab.setNextTaskId("");
			}

			scoringSlab.setWorkflowId(0);

			if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(scoringSlab.isNewRecord()){
				saveRecord=true;
				if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					scoringSlab.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (scoringSlab.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(scoringSlab.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			if(approveRec){
				rcdType= scoringSlab.getRecordType();
				recordStatus = scoringSlab.getRecordStatus();
				scoringSlab.setRecordType("");
				scoringSlab.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getScoringSlabDAO().save(scoringSlab, type);
			}

			if (updateRecord) {
				getScoringSlabDAO().update(scoringSlab, type);
			}

			if (deleteRecord) {
				getScoringSlabDAO().delete(scoringSlab, type);
			}

			if(approveRec){
				scoringSlab.setRecordType(rcdType);
				scoringSlab.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(scoringSlab);
		}
		logger.debug("Leaving ");
		return auditDetails;	
	}
	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingScoringMetricsList(List<AuditDetail> auditDetails, String type,long scoreGroupId) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ScoringMetrics scoringMetrics = (ScoringMetrics) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			scoringMetrics.setScoreGroupId(scoreGroupId);
			if (StringUtils.isEmpty(type)) {
				approveRec=true;
				scoringMetrics.setVersion(scoringMetrics.getVersion()+1);
				scoringMetrics.setRoleCode("");
				scoringMetrics.setNextRoleCode("");
				scoringMetrics.setTaskId("");
				scoringMetrics.setNextTaskId("");
			}

			scoringMetrics.setWorkflowId(0);

			if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(scoringMetrics.isNewRecord()){
				saveRecord=true;
				if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					scoringMetrics.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (scoringMetrics.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(scoringMetrics.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			if(approveRec){
				rcdType= scoringMetrics.getRecordType();
				recordStatus = scoringMetrics.getRecordStatus();
				scoringMetrics.setRecordType("");
				scoringMetrics.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getScoringMetricsDAO().save(scoringMetrics, type);
			}

			if (updateRecord) {
				getScoringMetricsDAO().update(scoringMetrics, type);
			}

			if (deleteRecord) {
				getScoringMetricsDAO().delete(scoringMetrics, type);
			}

			if(approveRec){
				scoringMetrics.setRecordType(rcdType);
				scoringMetrics.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(scoringMetrics);
		}
		logger.debug("Leaving ");
		return auditDetails;	
	}
	/**
	 * This method deletes the scoring metrics and slab details
	 * @param scoringGroup
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> listDeletion(ScoringGroup scoringGroup, String tableType, String auditTranType){
		logger.debug("Entering ");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceCheckListReference());

		if(scoringGroup.getScoringSlabList()!=null && scoringGroup.getScoringSlabList().size()>0){
			for(int i=0;i< scoringGroup.getScoringSlabList().size();i++){
				ScoringSlab scoringSlab=scoringGroup.getScoringSlabList().get(i);
				scoringSlab.setScoreGroupId(scoringGroup.getScoreGroupId());
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1]
				                                                                      , scoringSlab.getBefImage(), scoringSlab));
			}
			getScoringSlabDAO().delete(scoringGroup.getScoringSlabList().get(0).getScoreGroupId(), tableType);
		}
		
		List<ScoringMetrics> scoringMetricList = new ArrayList<ScoringMetrics>();
		if(scoringGroup.getScoringMetricsList()!=null && scoringGroup.getScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getScoringMetricsList());
		}
		if(scoringGroup.getFinScoringMetricsList()!=null && scoringGroup.getFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getFinScoringMetricsList());
		}
		if(scoringGroup.getNonFinScoringMetricsList()!=null && scoringGroup.getNonFinScoringMetricsList().size()>0){
			scoringMetricList.addAll(scoringGroup.getNonFinScoringMetricsList());
		}
		
		if(scoringMetricList != null && scoringMetricList.size() > 0){
			for(int i=0;i<scoringMetricList.size();i++){
				ScoringMetrics scoringMetrics=scoringMetricList.get(i);
				scoringMetrics.setScoreGroupId(scoringGroup.getScoreGroupId());
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1] , scoringMetrics.getBefImage(), scoringMetrics));
			}
			getScoringMetricsDAO().delete(scoringMetricList.get(0).getScoreGroupId(), tableType);
		}
		
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Common Method for scoring slab and scoring metrics list deletion
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				String[] fields = PennantJavaUtil.getFieldDetails(object);

				try {
					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) 
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}
					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses()).invoke(object, object.getClass().getClasses());
						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq()
								, fields[0], fields[1], befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	public void setScoringSlabDAO(ScoringSlabDAO scoringSlabDAO) {
		this.scoringSlabDAO = scoringSlabDAO;
	}

	public ScoringSlabDAO getScoringSlabDAO() {
		return scoringSlabDAO;
	}

	public void setScoringMetricsDAO(ScoringMetricsDAO scoringMetricsDAO) {
		this.scoringMetricsDAO = scoringMetricsDAO;
	}

	public ScoringMetricsDAO getScoringMetricsDAO() {
		return scoringMetricsDAO;
	}


}