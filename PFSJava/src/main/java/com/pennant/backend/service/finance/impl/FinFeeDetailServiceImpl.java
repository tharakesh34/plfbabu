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
 * FileName    		:  FinancePurposeDetailServiceImpl.java                                                   * 	  
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

package com.pennant.backend.service.finance.impl;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinFeeDetailServiceImpl extends GenericService<FinFeeDetail> implements FinFeeDetailService {
	private final static Logger logger = Logger.getLogger(FinFeeDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;

	public FinFeeDetailServiceImpl() {
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
	
	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}
	
	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type) {
		logger.debug("Entering");
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type);
		// Finance Fee Schedule Details
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeDetail.setFinFeeScheduleDetailList(getFinFeeScheduleDetailDAO().getFeeScheduleByFeeID(finFeeDetail.getFeeID(), isWIF, type));
			}
		}
		logger.debug("Leaving");
		return finFeeDetails;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, false, isWIF));
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private  List<AuditDetail> processFinFeeDetails(List<FinFeeDetail>  finFeeDetails, String tableType, String auditTranType,
			boolean isApproveRcd,boolean isWIF){
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			
			
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				
				if(!isApproveRcd && (finFeeDetail.isRcdVisible() && !finFeeDetail.isDataModified())){
					continue;
				}
				
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType) || StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finFeeDetail.setRoleCode("");
					finFeeDetail.setNextRoleCode("");
					finFeeDetail.setTaskId("");
					finFeeDetail.setNextTaskId("");
				}
				
				finFeeDetail.setWorkflowId(0);		
				if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finFeeDetail.isNewRecord()) {
					saveRecord = true;
					if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finFeeDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = finFeeDetail.getRecordType();
					recordStatus = finFeeDetail.getRecordStatus();
					finFeeDetail.setRecordType("");
					finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				
				if (saveRecord) {
					if (finFeeDetail.isNewRecord() && !approveRec) {
						finFeeDetail.setFeeSeq(getFinFeeDetailDAO().getFeeSeq(finFeeDetail, isWIF, tableType) + 1);
					}
					
					finFeeDetail.setFeeID(getFinFeeDetailDAO().save(finFeeDetail, isWIF, tableType));
					
					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (updateRecord) {
					getFinFeeDetailDAO().update(finFeeDetail, isWIF, tableType);
					getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (deleteRecord) {
					getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
					getFinFeeDetailDAO().delete(finFeeDetail, isWIF, tableType);
				}

				if (approveRec) {
					finFeeDetail.setRecordType(rcdType);
					finFeeDetail.setRecordStatus(recordStatus);
				}

				String[]  fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
				i++;
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, true, isWIF));
		
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	

		if(finFeeDetails != null && !finFeeDetails.isEmpty()) {
			int auditSeq = 1;
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
				getFinFeeDetailDAO().delete(finFeeDetail, isWIF, tableType);
				fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType,auditSeq, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getFinFeeDetailAuditDetail(List<FinFeeDetail> finFeeDetails, String auditTranType, String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	
		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			
			if("doApprove".equals(method) && !StringUtils.trimToEmpty(finFeeDetail.getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED))  {
				finFeeDetail.setWorkflowId(0);
				finFeeDetail.setNewRecord(true);
			} else {
				finFeeDetail.setWorkflowId(workFlowId);
			}
			
			boolean isRcdType = false;

			if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finFeeDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RCD_DEL)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFeeDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
			if (StringUtils.isNotEmpty(finFeeDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method, 
			String auditTranType, String  usrLanguage,boolean isWIF){
		return doValidation(finFeeDetails, workflowId, method, auditTranType, usrLanguage, isWIF);
	}
	
	private List<AuditDetail> doValidation(List<FinFeeDetail> finFeeDetails, long workflowId, String method, 
			String auditTranType, String usrLanguage, boolean isWIF){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if(finFeeDetails != null && !finFeeDetails.isEmpty()){
			List<AuditDetail> finFeeAuditDetails = getFinFeeDetailAuditDetail(finFeeDetails, auditTranType, method, workflowId);
			for (AuditDetail auditDetail : finFeeAuditDetails) {
				validateFinFeeDetail(auditDetail, method, usrLanguage, isWIF); 
			}
			auditDetails.addAll(finFeeAuditDetails);
		}
		
		logger.debug("Leaving");
		return auditDetails ;
	}
	
	private AuditDetail validateFinFeeDetail(AuditDetail auditDetail,String usrLanguage,String method, boolean isWIF){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinFeeDetail finFeeDetail = (FinFeeDetail) auditDetail.getModelData();
		FinFeeDetail tempFinFinDetail= null;
		if (finFeeDetail.isWorkflow()){
			tempFinFinDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "_Temp");
		}
		FinFeeDetail befFinFeeDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "");
		FinFeeDetail oldFinFeeDetail= finFeeDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(finFeeDetail.getFinReference());
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (finFeeDetail.isNew()){ // for New record or new record into work flow

			if (!finFeeDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinFeeDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinFeeDetail != null || tempFinFinDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinFeeDetail ==null || tempFinFinDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFeeDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinFeeDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(befFinFeeDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinFinDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinFinDetail!=null && oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(tempFinFinDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFeeDetail.isWorkflow()){
			auditDetail.setBefImage(befFinFeeDetail);	
		}
		return auditDetail;
	}

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}


}