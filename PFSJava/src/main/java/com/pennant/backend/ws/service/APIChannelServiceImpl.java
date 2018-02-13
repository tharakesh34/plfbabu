/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : ChannelDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-10-2014 * *
 * Modified Date : 10-10-2014 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 10-10-2014 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.ws.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.channeldetails.APIChannel;
import com.pennant.backend.model.channeldetails.APIChannelIP;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.ws.dao.APIChannelDAO;
import com.pennant.ws.exception.APIException;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class APIChannelServiceImpl  extends GenericService<APIChannel> implements APIChannelService {
	private static final Logger logger = Logger.getLogger(APIChannelServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private APIChannelDAO apiChannelDAO;
	private Set<String> excludeFields;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * ChannelDetails/ChannelDetails_Temp by using ChannelDetailsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using ChannelDetailsDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtChannelDetails by using
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
		APIChannel apiChannel = (APIChannel) auditHeader.getAuditDetail().getModelData();

		if (apiChannel.isWorkflow()) {
			tableType="_Temp";
		}

		if (apiChannel.isNew()) {
			apiChannel.setId(apiChannelDAO.save(apiChannel,tableType));
			auditHeader.getAuditDetail().setModelData(apiChannel);
			auditHeader.setAuditReference(String.valueOf(apiChannel.getId()));
		}else{
			apiChannelDAO.update(apiChannel,tableType);
		}

		//Retrieving List of Audit Details For ChannelAuthDetails  related modules
		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			auditHeader.setAuditDetails(processingChannelAuthDetailsList(auditHeader.getAuditDetails(),tableType,apiChannel));
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table ChannelDetails by using ChannelDetailsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtChannelDetails by using
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

		APIChannel apiChannel = (APIChannel) auditHeader.getAuditDetail().getModelData();
		apiChannelDAO.deleteChannelAuthDetails(apiChannel.getId(),"");
		apiChannelDAO.deleteChannelDetails(apiChannel, "");
		auditHeader=resetAuditDetails(auditHeader, apiChannel, auditHeader.getAuditTranType());
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getChannelDetailsById fetch the details by using ChannelDetailsDAO's getChannelDetailsById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ChannelDetails
	 */

	public APIChannel getChannelDetailsById(long id) {
		APIChannel apiChannel = apiChannelDAO.getChannelDetailsById(id,"_View");
		if(apiChannel!=null){
			apiChannel.setIpList(apiChannelDAO.getChannelAuthDetailsByChannelId(apiChannel.getId(), "_View"));
		}
		return apiChannel;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getChannelDetailsDAO().delete with parameters ChannelDetails,"" b) NEW Add new
	 * record in to main table by using getChannelDetailsDAO().save with parameters
	 * ChannelDetails,"" c) EDIT Update record in the main table by using
	 * getChannelDetailsDAO().update with parameters ChannelDetails,"" 3) Delete the record
	 * from the workFlow table by using getChannelDetailsDAO().delete with parameters
	 * ChannelDetails,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtChannelDetails by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and AdtChannelDetails by using
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
		APIChannel apiChannel = new APIChannel(0);
		BeanUtils.copyProperties((APIChannel) auditHeader.getAuditDetail().getModelData(), apiChannel);

		if (apiChannel.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			delete(auditHeader);
		} else {
			apiChannel.setRoleCode("");
			apiChannel.setNextRoleCode("");
			apiChannel.setTaskId("");
			apiChannel.setNextTaskId("");
			apiChannel.setWorkflowId(0);

			if (apiChannel.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				apiChannel.setRecordType("");
				apiChannelDAO.save(apiChannel,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				apiChannel.setRecordType("");
				apiChannelDAO.update(apiChannel,"");
			}
			//Retrieving List of Audit Details For utilityDetail  related modules
			if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
				auditDetails = processingChannelAuthDetailsList(auditHeader.getAuditDetails(),"",apiChannel);
			}


		}
		apiChannelDAO.deleteChannelAuthDetails(apiChannel.getId(), "_Temp");
		apiChannelDAO.deleteChannelDetails(apiChannel,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(auditDetails);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(apiChannel);
		auditHeader = resetAuditDetails(auditHeader, apiChannel, tranType);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getChannelDetailsDAO().delete with parameters
	 * ChannelDetails,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtChannelDetails by using auditHeaderDAO.addAudit(auditHeader) for Work
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
		APIChannel apiChannel = (APIChannel) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		apiChannelDAO.deleteChannelAuthDetails(apiChannel.getId(),"_Temp");
		apiChannelDAO.deleteChannelDetails(apiChannel, "_Temp");
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		APIChannel aChannelDetails = (APIChannel) auditHeader.getAuditDetail().getModelData();
		excludeFields = aChannelDetails.getExcludeFields();

		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
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
	 * getChannelDetailsDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering ");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		APIChannel apiChannel = (APIChannel) auditDetail.getModelData();
		APIChannel tempChannelDetails = null;
		if (apiChannel.isWorkflow()) {
			tempChannelDetails = apiChannelDAO.getChannelDetailsById(apiChannel.getId(),"_Temp");
		}
		APIChannel befChannelDetails = apiChannelDAO.getChannelDetailsById(apiChannel.getId(), "");
		APIChannel oldChannelDetails = apiChannel.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=apiChannel.getCode();
		errParm[0]=PennantJavaUtil.getLabel("label_ChannelDetailsDialog_ChannelDetailsCode.value")+":"+valueParm[0];

		if (apiChannel.isNew()) { // for New record or new record into work flow

			if (!apiChannel.isWorkflow()) {// With out Work flow only new records
				if (befChannelDetails != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,valueParm));
				}
			} else { // with work flow
				if (apiChannel.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befChannelDetails != null || tempChannelDetails != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm,
								valueParm));
					}
				} else {
					if (befChannelDetails == null || tempChannelDetails != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!apiChannel.isWorkflow()) { // With out Work flow for update and delete

				if (befChannelDetails == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,valueParm));
				}else{

					if (oldChannelDetails != null && !oldChannelDetails.getLastMntOn().equals(befChannelDetails.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,valueParm));
						}
					}
				}
			} else {
				if (tempChannelDetails == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,valueParm));
				}
				if (tempChannelDetails != null && oldChannelDetails != null && !oldChannelDetails.getLastMntOn().equals(tempChannelDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,valueParm));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !apiChannel.isWorkflow()) {
		
			auditDetail.setBefImage(befChannelDetails);
			
			if (apiChannel.getIpList() != null && !apiChannel.getIpList().isEmpty()) {
				for (APIChannelIP channelIP : apiChannel.getIpList()) {
					channelIP.setBefImage(apiChannelDAO.getChannelIpDetail( channelIP.getChannelId(), channelIP.getId()));
				}
			}
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

		APIChannel apiChannel = (APIChannel) auditHeader.getAuditDetail().getModelData();
		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (apiChannel.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}
		if (apiChannel.getIpList() != null && apiChannel.getIpList().size() > 0){
			auditHeader.setAuditDetails(setChannelAuthDetailsAuditData(apiChannel,auditTranType,method));
		}
		logger.debug("Leaving ");
		return auditHeader;
	}
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param APIChannel
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChannelAuthDetailsAuditData(APIChannel apiChannel,String auditTranType,String method) {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean delete=false;

		if ((PennantConstants.RECORD_TYPE_DEL.equals(apiChannel.getRecordType()) && "doApprove".equalsIgnoreCase(method)) || "delete".equals(method)) {
			delete=true;
		}
		for (int i = 0; i < apiChannel.getIpList().size(); i++) {
			APIChannelIP aPIChannelIP  = apiChannel.getIpList().get(i);
			aPIChannelIP.setWorkflowId(apiChannel.getWorkflowId());
			excludeFields= aPIChannelIP.getExcludeFields();
			String[] fields = PennantJavaUtil.getFieldDetails(new APIChannelIP(),excludeFields);

			boolean isRcdType= false;

			if(delete){
				aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_MDEL);
			}else{
				if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType=true;
				}else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType=true;
				}else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isRcdType=true;
				}
			}
			if("saveOrUpdate".equals(method) && (isRcdType && aPIChannelIP.isWorkflow())){
				aPIChannelIP.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}
			aPIChannelIP.setRecordStatus(apiChannel.getRecordStatus());
			aPIChannelIP.setUserDetails(apiChannel.getUserDetails());
			aPIChannelIP.setLastMntOn(apiChannel.getLastMntOn());
			aPIChannelIP.setLastMntBy(apiChannel.getLastMntBy());
			if(StringUtils.isNotEmpty(aPIChannelIP.getRecordType())){
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],  aPIChannelIP.getBefImage(), aPIChannelIP));
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
	private List<AuditDetail> processingChannelAuthDetailsList(List<AuditDetail> auditDetails, String type, APIChannel apiChannel) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (AuditDetail auditDetail : auditDetails) {
			APIChannelIP aPIChannelIP = (APIChannelIP) auditDetail.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType = "";
			String recordStatus = "";

			aPIChannelIP.setChannelId(apiChannel.getId());
			if (StringUtils.isEmpty(type)) {
				aPIChannelIP.setVersion(aPIChannelIP.getVersion()+1);
				approveRec=true;
			}

			if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (aPIChannelIP.isNewRecord() || aPIChannelIP.getId()==Long.MIN_VALUE) {
				saveRecord = true;
				if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					aPIChannelIP.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (aPIChannelIP.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (aPIChannelIP.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (StringUtils.isEmpty(type)) {
				aPIChannelIP.setVersion(aPIChannelIP.getVersion()+1);
				approveRec=true;
				aPIChannelIP.setRoleCode("");
				aPIChannelIP.setNextRoleCode("");
				aPIChannelIP.setTaskId("");
				aPIChannelIP.setNextTaskId("");
				aPIChannelIP.setRecordType("");
				aPIChannelIP.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}else{
				aPIChannelIP.setRoleCode(apiChannel.getRoleCode());
				aPIChannelIP.setNextRoleCode(apiChannel.getNextRoleCode());
				aPIChannelIP.setTaskId(apiChannel.getTaskId());
				aPIChannelIP.setNextTaskId(apiChannel.getNextTaskId());
			}
			if (approveRec) {
				rcdType = aPIChannelIP.getRecordType();
				recordStatus = aPIChannelIP.getRecordStatus();
				aPIChannelIP.setRecordType("");
				aPIChannelIP.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				apiChannelDAO.save(aPIChannelIP, type);
			}
			if (updateRecord) {
				apiChannelDAO.update(aPIChannelIP, type);
			}
			if (deleteRecord) {
				apiChannelDAO.delete(aPIChannelIP, type);
			}
			if (approveRec) {
				aPIChannelIP.setRecordType(rcdType);
				aPIChannelIP.setRecordStatus(recordStatus);
			}
			auditDetail.setModelData(aPIChannelIP);
		}
		return auditDetails;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param ChannelDetails
	 * @param tranType
	 * @return
	 */
	private AuditHeader resetAuditDetails(AuditHeader auditHeader, APIChannel aPIChannel,String tranType){
		logger.debug("Entering :");
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(aPIChannel);

		if(auditHeader.getAuditDetails()!=null && !auditHeader.getAuditDetails().isEmpty()){
			List<AuditDetail> auditDetails= new ArrayList<AuditDetail>();

			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				APIChannelIP authenticationDetails=(APIChannelIP) detail.getModelData(); 
				detail.setAuditTranType(tranType);
				authenticationDetails.setRecordType("");
				authenticationDetails.setRoleCode("");
				authenticationDetails.setNextRoleCode("");
				authenticationDetails.setTaskId("");
				authenticationDetails.setNextTaskId("");
				authenticationDetails.setWorkflowId(0);
				detail.setModelData(authenticationDetails);
				auditDetails.add(detail);
			}
			auditHeader.setAuditDetails(auditDetails);
		}
		logger.debug("Leaving :");
		return auditHeader;
	} 


	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}	
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Override
	public APIChannel getChannelDetails() {
		return apiChannelDAO.getChannelDetails();
	}

	@Override
	public APIChannel getNewChannelDetails() {
		return apiChannelDAO.getNewChannelDetails();
	}
	
	public void setApiChannelDAO(APIChannelDAO apiChannelDAO) {
		this.apiChannelDAO = apiChannelDAO;
	}

	@Override
	public long getChannelId(String channelId, String channelIp) throws APIException{
		return apiChannelDAO.getChannelId(channelId, channelIp);
	}
}
