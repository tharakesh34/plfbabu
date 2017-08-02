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
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinCovenantTypeServiceImpl extends GenericService<FinCovenantType> implements FinCovenantTypeService {
	private static final Logger logger = Logger.getLogger(FinCovenantTypeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinCovenantTypeDAO finCovenantTypesDAO;

	public FinCovenantTypeServiceImpl() {
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
	 * @return the finCovenantTypesDAO
	 */
	public FinCovenantTypeDAO getFinCovenantTypeDAO() {
		return finCovenantTypesDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypesDAO) {
		this.finCovenantTypesDAO = finCovenantTypesDAO;
	}

	@Override
	public List<FinCovenantType> getFinCovenantTypeById(String id,String type,boolean isEnquiry) {
		logger.debug("Entering");
		List<FinCovenantType> finCovenantTypes = getFinCovenantTypeDAO().getFinCovenantTypeByFinRef(id, type,isEnquiry);
		logger.debug("Leaving");
		return finCovenantTypes;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, false));
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private  List<AuditDetail> processFinAdvancePaymentDetails(List<FinCovenantType>  finCovenantTypes, String tableType, String auditTranType,boolean isApproveRcd){
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinCovenantType finPayment : finCovenantTypes) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType) || StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finPayment.setRoleCode("");
					finPayment.setNextRoleCode("");
					finPayment.setTaskId("");
					finPayment.setNextTaskId("");
				}
				
				finPayment.setWorkflowId(0);		
				if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finPayment.isNewRecord()) {
					saveRecord = true;
					if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finPayment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finPayment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),(PennantConstants.RECORD_TYPE_NEW))) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),(PennantConstants.RECORD_TYPE_UPD))) {
					updateRecord = true;
				} else if (StringUtils.equalsIgnoreCase(finPayment.getRecordType(),(PennantConstants.RECORD_TYPE_DEL))) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finPayment.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = finPayment.getRecordType();
					recordStatus = finPayment.getRecordStatus();
					finPayment.setRecordType("");
					finPayment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getFinCovenantTypeDAO().save(finPayment, tableType);
				}

				if (updateRecord) {
					getFinCovenantTypeDAO().update(finPayment, tableType);
				}

				if (deleteRecord) {
					getFinCovenantTypeDAO().delete(finPayment, tableType);
				}

				if (approveRec) {
					finPayment.setRecordType(rcdType);
					finPayment.setRecordStatus(recordStatus);
				}

				String[]  fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], finPayment.getBefImage(), finPayment));
				i++;
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinAdvancePaymentDetails(finCovenantTypes, tableType, auditTranType, true));
		
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<FinCovenantType> finCovenantTypes, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	

		if(finCovenantTypes != null && !finCovenantTypes.isEmpty()) {
			int auditSeq = 1;
			for (FinCovenantType finPayment : finCovenantTypes) {
				getFinCovenantTypeDAO().delete(finPayment, tableType);
				fields = PennantJavaUtil.getFieldDetails(finPayment, finPayment.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType,auditSeq, fields[0], fields[1], finPayment.getBefImage(), finPayment));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> getAdvancePaymentAuditDetail(List<FinCovenantType> finCovenantTypes, String auditTranType, String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	
		for (FinCovenantType finAdvancePay : finCovenantTypes) {
			
			if("doApprove".equals(method) && !StringUtils.trimToEmpty(finAdvancePay.getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED))  {
				finAdvancePay.setWorkflowId(0);
				finAdvancePay.setNewRecord(true);
			} else {
				finAdvancePay.setWorkflowId(workFlowId);
			}
			
			boolean isRcdType = false;

			if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finAdvancePay.getRecordType().equalsIgnoreCase(
					PennantConstants.RCD_DEL)) {
				finAdvancePay.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finAdvancePay.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finAdvancePay.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finAdvancePay, finAdvancePay.getExcludeFields());
			if (StringUtils.isNotEmpty(finAdvancePay.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], finAdvancePay.getBefImage(), finAdvancePay));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> validate(List<FinCovenantType> finCovenantTypes, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(finCovenantTypes, workflowId, method, auditTranType, usrLanguage);
	}
	
	private List<AuditDetail> doValidation(List<FinCovenantType> finCovenantTypes, long workflowId, String method, String auditTranType, String usrLanguage){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if(finCovenantTypes != null && !finCovenantTypes.isEmpty()){
			List<AuditDetail> advancePayAuditDetails = getAdvancePaymentAuditDetail(finCovenantTypes, auditTranType, method, workflowId);
			for (AuditDetail auditDetail : advancePayAuditDetails) {
				validateAdvancePayment(auditDetail, method, usrLanguage); 
			}
			auditDetails.addAll(advancePayAuditDetails);
		}
		
		logger.debug("Leaving");
		return auditDetails ;
	}
	
	private AuditDetail validateAdvancePayment(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinCovenantType finAdvancePay = (FinCovenantType) auditDetail.getModelData();
		FinCovenantType tempFinAdvancePay= null;
		if (finAdvancePay.isWorkflow()){
			tempFinAdvancePay = getFinCovenantTypeDAO().getFinCovenantTypeById(finAdvancePay, "_Temp");
		}
		FinCovenantType befFinAdvancePay = getFinCovenantTypeDAO().getFinCovenantTypeById(finAdvancePay, "");
		FinCovenantType oldFinAdvancePay= finAdvancePay.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]= finAdvancePay.getFinReference();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

		if (finAdvancePay.isNew()){ // for New record or new record into work flow

			if (!finAdvancePay.isWorkflow()){// With out Work flow only new records  
				if (befFinAdvancePay !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (finAdvancePay.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinAdvancePay != null || tempFinAdvancePay!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinAdvancePay ==null || tempFinAdvancePay!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finAdvancePay.isWorkflow()){	// With out Work flow for update and delete

				if (befFinAdvancePay ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinAdvancePay!=null && !oldFinAdvancePay.getLastMntOn().equals(befFinAdvancePay.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinAdvancePay==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinAdvancePay!=null && oldFinAdvancePay!=null && !oldFinAdvancePay.getLastMntOn().equals(tempFinAdvancePay.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finAdvancePay.isWorkflow()){
			auditDetail.setBefImage(befFinAdvancePay);	
		}
		return auditDetail;
	}

	@Override
	public AuditHeader delete(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader);
		
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		FinCovenantType finCovenantType = (FinCovenantType) aAuditHeader.getAuditDetail()
				.getModelData();
		getFinCovenantTypeDAO().delete(finCovenantType, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(aAuditHeader);
		logger.debug("Leaving");
		return aAuditHeader;
	}

	private AuditHeader businessValidation(AuditHeader aAuditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(aAuditHeader.getAuditDetail(), aAuditHeader.getUsrLanguage());
		aAuditHeader.setAuditDetail(auditDetail);
		aAuditHeader.setErrorList(auditDetail.getErrorDetails());
		aAuditHeader=nextProcess(aAuditHeader);
		logger.debug("Leaving");
		return aAuditHeader;
	}
	
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		FinCovenantType finCovenantType = (FinCovenantType) auditDetail.getModelData();

		// Check the unique keys.
		if (finCovenantType.isNew()&& PennantConstants.RECORD_TYPE_NEW.equals(finCovenantType.getRecordType())&& finCovenantTypesDAO.isDuplicateKey(finCovenantType.getFinReference(), finCovenantType.getCovenantType(), finCovenantType.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];
			parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + finCovenantType.getFinReference();
			parameters[1] = PennantJavaUtil.getLabel("label_CovenanType") + ": " + finCovenantType.getCovenantType();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader);
		
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		FinCovenantType aFinCovenantType = (FinCovenantType) aAuditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (aFinCovenantType.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (aFinCovenantType.isNew()) {
			aFinCovenantType.setFinReference((getFinCovenantTypeDAO().save(aFinCovenantType, tableType)));
			aAuditHeader.getAuditDetail().setModelData(aFinCovenantType);
			aAuditHeader.setAuditReference(String.valueOf(aFinCovenantType.getFinReference()));
		} else {
			getFinCovenantTypeDAO().update(aFinCovenantType, tableType);
		}

		getAuditHeaderDAO().addAudit(aAuditHeader);
		logger.debug("Leaving");
		return aAuditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {

		logger.debug("Entering");

		String tranType = "";
		aAuditHeader = businessValidation(aAuditHeader);
		
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		FinCovenantType aFinCovenantType = new FinCovenantType();
		BeanUtils.copyProperties((FinCovenantType) aAuditHeader.getAuditDetail()
				.getModelData(), aFinCovenantType);

		getFinCovenantTypeDAO().delete(aFinCovenantType, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(aFinCovenantType.getRecordType())) {
			aAuditHeader.getAuditDetail().setBefImage(finCovenantTypesDAO.getCovenantTypeById(aFinCovenantType.getFinReference(),aFinCovenantType.getCovenantType(), ""));
		}

		if (aFinCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinCovenantTypeDAO().delete(aFinCovenantType, TableType.MAIN_TAB);
		} else {
			aFinCovenantType.setRoleCode("");
			aFinCovenantType.setNextRoleCode("");
			aFinCovenantType.setTaskId("");
			aFinCovenantType.setNextTaskId("");
			aFinCovenantType.setWorkflowId(0);

			if (aFinCovenantType.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				aFinCovenantType.setRecordType("");
				getFinCovenantTypeDAO().save(aFinCovenantType, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				aFinCovenantType.setRecordType("");
				getFinCovenantTypeDAO().update(aFinCovenantType, TableType.MAIN_TAB);
			}
		}

		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(aAuditHeader);

		aAuditHeader.setAuditTranType(tranType);
		aAuditHeader.getAuditDetail().setAuditTranType(tranType);
		aAuditHeader.getAuditDetail().setModelData(aFinCovenantType);
		getAuditHeaderDAO().addAudit(aAuditHeader);
		logger.debug("Leaving");
		return aAuditHeader;
	
	}

	@Override
	public AuditHeader doReject(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader);
		
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		FinCovenantType aFinCovenantType = (FinCovenantType) aAuditHeader.getAuditDetail()
				.getModelData();
		aAuditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinCovenantTypeDAO().delete(aFinCovenantType, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(aAuditHeader);
		logger.debug("Leaving");
		return aAuditHeader;
	}

	@Override
	public FinCovenantType getFinCovenantTypeById(String reference, String covenType, String type) {
		return getFinCovenantTypeDAO().getCovenantTypeById(reference, covenType, type);
	}


}