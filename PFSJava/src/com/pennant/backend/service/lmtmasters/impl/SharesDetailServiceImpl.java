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
 * FileName    		:  SharesDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters.impl;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.lmtmasters.SharesDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.lmtmasters.SharesDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>CommidityLoanDetail</b>.<br>
 * 
 */
public class SharesDetailServiceImpl extends GenericService<SharesDetail> implements SharesDetailService {
	private final static Logger logger = Logger.getLogger(SharesDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private SharesDetailDAO sharesDetailDAO;

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
	 * @return the commidityLoanDetailDAO
	 */
	public SharesDetailDAO getSharesDetailDAO() {
		return sharesDetailDAO;
	}
	/**
	 * @param commidityLoanDetailDAO the commidityLoanDetailDAO to set
	 */
	public void setSharesDetailDAO(SharesDetailDAO sharesDetailDAO) {
		this.sharesDetailDAO = sharesDetailDAO;
	}

	/**
	 * @return the commidityLoanDetail
	 */
	@Override
	public SharesDetail getSharesDetail() {
		return getSharesDetailDAO().getSharesDetail();
	}
	/**
	 * @return the commidityLoanDetail for New Record
	 */
	@Override
	public SharesDetail getNewSharesDetail() {
		return getSharesDetailDAO().getSharesDetail();
	}


	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table LMTCommidityLoanDetail/LMTCommidityLoanDetail_Temp 
	 * 			by using CommidityLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommidityLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table LMTCommidityLoanDetail/LMTCommidityLoanDetail_Temp 
	 * 			by using CommidityLoanDetailDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommidityLoanDetailDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)
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
		SharesDetail sharesDetail = (SharesDetail) auditHeader.getAuditDetail().getModelData();

		if (sharesDetail.isWorkflow()) {
			tableType="_TEMP";
		}

		if (sharesDetail.isNew()) {
			getSharesDetailDAO().save(sharesDetail, tableType);
		}else{
			getSharesDetailDAO().update(sharesDetail ,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table LMTCommidityLoanDetail by using CommidityLoanDetailDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader)    
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

		SharesDetail commidityLoanDetail = (SharesDetail) auditHeader.getAuditDetail().getModelData();
		getSharesDetailDAO().delete(commidityLoanDetail,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommidityLoanDetailById fetch the details by using CommidityLoanDetailDAO's getCommidityLoanDetailById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommidityLoanDetail
	 */

	@Override
	public SharesDetail getCommidityLoanDetailById(String id,String itemType) {
		return getSharesDetailDAO().getSharesDetailById(id,itemType, "_View");
	}
	/**
	 * getApprovedCommidityLoanDetailById fetch the details by using CommidityLoanDetailDAO's getCommidityLoanDetailById method .
	 * with parameter id and type as blank. it fetches the approved records from the LMTCommidityLoanDetail.
	 * @param id (String)
	 * @return CommidityLoanDetail
	 */

	public SharesDetail getApprovedSharesDetailById(String id,String itemType) {
		return getSharesDetailDAO().getSharesDetailById(id, itemType, "_AView");
	}

	/**
	 * This method refresh the Record.
	 * @param CommidityLoanDetail (commidityLoanDetail)
	 * @return commidityLoanDetail
	 */
	@Override
	public SharesDetail refresh(SharesDetail sharesDetail) {
		logger.debug("Entering");
		getSharesDetailDAO().refresh(sharesDetail);
		getSharesDetailDAO().initialize(sharesDetail);
		logger.debug("Leaving");
		return sharesDetail;
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getSharesDetailDAO().delete with parameters commidityLoanDetail,""
	 * 		b)  NEW		Add new record in to main table by using getSharesDetailDAO().save with parameters commidityLoanDetail,""
	 * 		c)  EDIT	Update record in the main table by using getSharesDetailDAO().update with parameters commidityLoanDetail,""
	 * 3)	Delete the record from the workFlow table by using getSharesDetailDAO().delete with parameters commidityLoanDetail,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		SharesDetail commidityLoanDetail = new SharesDetail();
		BeanUtils.copyProperties((SharesDetail) auditHeader.getAuditDetail().getModelData(), commidityLoanDetail);

		if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			getSharesDetailDAO().delete(commidityLoanDetail,"");

		} else {
			commidityLoanDetail.setRoleCode("");
			commidityLoanDetail.setNextRoleCode("");
			commidityLoanDetail.setTaskId("");
			commidityLoanDetail.setNextTaskId("");
			commidityLoanDetail.setWorkflowId(0);

			if (commidityLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
			{	
				tranType=PennantConstants.TRAN_ADD;
				commidityLoanDetail.setRecordType("");
				getSharesDetailDAO().save(commidityLoanDetail,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				commidityLoanDetail.setRecordType("");
				getSharesDetailDAO().update(commidityLoanDetail,"");
			}
		}

		getSharesDetailDAO().delete(commidityLoanDetail,"_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commidityLoanDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getSharesDetailDAO().delete with parameters commidityLoanDetail,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtLMTCommidityLoanDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		SharesDetail commidityLoanDetail = (SharesDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getSharesDetailDAO().delete(commidityLoanDetail,"_TEMP");

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
	 * 5)	for any mismatch conditions Fetch the error details from getSharesDetailDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		SharesDetail sharesDetail= (SharesDetail) auditDetail.getModelData();

		SharesDetail tempsharesDetail= null;
		if (sharesDetail.isWorkflow()){
			tempsharesDetail = getSharesDetailDAO().getSharesDetailById(sharesDetail.getId(), sharesDetail.getCompanyName(), "_Temp");
		}
		
		SharesDetail befSharesDetail = getSharesDetailDAO().getSharesDetailById(sharesDetail.getId(), sharesDetail.getCompanyName(), "");
		SharesDetail oldSharesDetail= sharesDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=sharesDetail.getId();
		errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

		if (sharesDetail.isNew()){ // for New record or new record into work flow

			if (!sharesDetail.isWorkflow()){// With out Work flow only new records  
				if (befSharesDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (sharesDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befSharesDetail !=null || tempsharesDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befSharesDetail ==null || tempsharesDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!sharesDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befSharesDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldSharesDetail!=null && !oldSharesDetail.getLastMntOn().equals(befSharesDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempsharesDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldSharesDetail!=null && !oldSharesDetail.getLastMntOn().equals(tempsharesDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !sharesDetail.isWorkflow()){
			auditDetail.setBefImage(befSharesDetail);	
		}

		return auditDetail;
	}

	@Override
    public List<SharesDetail> getSharesDetailDetailByFinRef(String finReference, String tableType) {
		return getSharesDetailDAO().getSharesDetailDetailByFinRef(finReference, tableType);
    }

	@Override
    public void setSharesDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		financeDetail.setSharesDetails(getSharesDetailDAO().getSharesDetailDetailByFinRef(finReference, tableType));
		logger.debug("Leaving");
	}

	@Override
    public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("SharesDetail");
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			SharesDetail sharesDetail = (SharesDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (tableType.equals("")) {
				approveRec = true;
				sharesDetail.setRoleCode("");
				sharesDetail.setNextRoleCode("");
				sharesDetail.setTaskId("");
				sharesDetail.setNextTaskId("");
			}

			sharesDetail.setWorkflowId(0);

			if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (sharesDetail.isNewRecord()) {
				saveRecord = true;
				if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (sharesDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (sharesDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (sharesDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = sharesDetail.getRecordType();
				recordStatus = sharesDetail.getRecordStatus();
				sharesDetail.setRecordType("");
				sharesDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(sharesDetail.getLoanRefNumber()).equals("")) {
					sharesDetail.setLoanRefNumber(finReference);
				}
				getSharesDetailDAO().save(sharesDetail, tableType);
			}

			if (updateRecord) {
				getSharesDetailDAO().update(sharesDetail, tableType);
			}

			if (deleteRecord) {
				getSharesDetailDAO().delete(sharesDetail, tableType);
			}

			if (approveRec) {
				sharesDetail.setRecordType(rcdType);
				sharesDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(sharesDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	@Override
    public List<AuditDetail> doApprove(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("SharesDetail");
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			SharesDetail sharesDetail = (SharesDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (tableType.equals("")) {
				approveRec = true;
				sharesDetail.setRoleCode("");
				sharesDetail.setNextRoleCode("");
				sharesDetail.setTaskId("");
				sharesDetail.setNextTaskId("");
			}

			sharesDetail.setWorkflowId(0);

			if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (sharesDetail.isNewRecord()) {
				saveRecord = true;
				if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (sharesDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_DEL)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (sharesDetail.getRecordType().equalsIgnoreCase(
						PennantConstants.RCD_UPD)) {
					sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (sharesDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = sharesDetail.getRecordType();
				recordStatus = sharesDetail.getRecordStatus();
				sharesDetail.setRecordType("");
				sharesDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.trimToEmpty(sharesDetail.getLoanRefNumber()).equals("")) {
					sharesDetail.setLoanRefNumber(finReference);
				}
				getSharesDetailDAO().save(sharesDetail, tableType);
			}

			if (updateRecord) {
				getSharesDetailDAO().update(sharesDetail, tableType);
			}

			if (deleteRecord) {
				getSharesDetailDAO().delete(sharesDetail, tableType);
			}

			if (approveRec) {
				sharesDetail.setRecordType(rcdType);
				sharesDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(sharesDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	@Override
    public List<AuditDetail> delete(FinanceDetail financeDetail, String tableType,
            String auditTranType) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain(); 
		SharesDetail object = new SharesDetail();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < financeDetail.getSharesDetails().size(); i++) {

			SharesDetail sharesDetail = financeDetail.getSharesDetails().get(i);
			sharesDetail.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
			boolean isRcdType = false;

			if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(  PennantConstants.RCD_UPD)) {
				sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				sharesDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if (method.equals("saveOrUpdate") && (isRcdType == true)) {
				sharesDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (sharesDetail.getRecordType().equalsIgnoreCase( PennantConstants.RECORD_TYPE_DEL) || sharesDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			sharesDetail.setRecordStatus(financeMain.getRecordStatus());
			sharesDetail.setUserDetails(financeMain.getUserDetails());
			sharesDetail.setLastMntOn(financeMain.getLastMntOn());

			if (!sharesDetail.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],  sharesDetail.getBefImage(), sharesDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
    public List<AuditDetail> validate(FinanceDetail financeDetail, String method, String usrLanguage) {
	    // TODO Auto-generated method stub
	    return null;
    }

}