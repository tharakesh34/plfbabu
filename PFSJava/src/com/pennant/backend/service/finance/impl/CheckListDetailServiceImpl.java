package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CheckListDetailServiceImpl implements CheckListDetailService{
	private final static Logger logger = Logger.getLogger(CheckListDetailServiceImpl.class);
	
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private CustomerDocumentDAO customerDocumentDao;
	
	/**
	 * Set CheckList Details to the Finance Detail and also 
	 * set the checkListDetails to each FinanceReferenceDetail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public void setFinanceCheckListDetails(FinanceDetail financeDetail, String finType,  String userRole) {
		logger.debug("Entering");
		List<FinanceReferenceDetail> financeReferenceList  = null;
		List<FinanceCheckListReference> financeCheckListReferenceList = null;
		List<CheckListDetail> checkListDetailList = null;

		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		financeReferenceList = getFinanceReferenceDetailDAO().getFinanceReferenceDetail(finType, userRole, "_AQView");
		
		String showCheckListIds = "";
		
		List<String> docTypeList = null;
		if (financeReferenceList != null && !financeReferenceList.isEmpty()) {
			docTypeList = new ArrayList<String>();
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceList) {
				showCheckListIds = showCheckListIds +financeReferenceDetail.getFinRefId()+",";
				checkListDetailList = getCheckListDetailDAO().getCheckListDetailByChkList(financeReferenceDetail.getFinRefId(), "_AView");
				financeReferenceDetail.setLovDesccheckListDetail(checkListDetailList);
				for (CheckListDetail checkListDetail : checkListDetailList) {
					if(checkListDetail.isDocIsCustDOC()){
						docTypeList.add(checkListDetail.getDocType());
					}
				}
			}
		}
		
		//Customer Document Details Fetching Depends on Customer & Doc Type List
		List<DocumentDetails> documentList = null;
		if(docTypeList != null && !docTypeList.isEmpty()){
			documentList = getCustomerDocumentDAO().getCustDocListByDocTypes(
					financeDetail.getFinScheduleData().getFinanceMain().getCustID(), docTypeList, "");
			
			if(financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()){
				financeDetail.getDocumentDetailsList().addAll(documentList);
			}else{
				financeDetail.setDocumentDetailsList(documentList);
			}
		}
		if(showCheckListIds.endsWith(",")){
			showCheckListIds = showCheckListIds.substring(0, showCheckListIds.length()-1);
		}
		
		if(!financeDetail.getFinScheduleData().getFinanceMain().isNewRecord()){
			financeCheckListReferenceList = getFinanceCheckListReferenceDAO().getCheckListByFinRef(finReference, showCheckListIds, "_View");
		}else{
			financeCheckListReferenceList = new ArrayList<FinanceCheckListReference>();
		}
		financeDetail.setCheckList(financeReferenceList);
		financeDetail.setFinanceCheckList(financeCheckListReferenceList);

		logger.debug("Leaving");
	}
	
	/**
	 * Method to get FinanceCheckListReference By FinReference
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(final String id, String type) {
		return getFinanceCheckListReferenceDAO().getCheckListByFinRef(id, null, type);
	}
	
	
	/**
	 * This method prepare audit details for FinanceCheckListReference list and deletes the list
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	@Override
	public List<AuditDetail> delete(FinanceDetail finDetail, String tableType, String auditTranType) {
		logger.debug("Entering ");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		FinanceCheckListReference object = new FinanceCheckListReference();
		
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());
		
		if (finDetail.getFinanceCheckList() != null && !finDetail.getFinanceCheckList().isEmpty()) {
			for (int i = 0; i < finDetail.getFinanceCheckList().size(); i++) {
				FinanceCheckListReference finCheckListRef = finDetail.getFinanceCheckList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCheckListRef.getBefImage(), finCheckListRef));
			}
			
			getFinanceCheckListReferenceDAO().delete(finDetail.getFinanceCheckList().get(0).getFinReference(), tableType);
		}
		logger.debug("Leaving ");
		return auditList;
	}
	
	
	
	/**
	 * Setting checkList audit details
	 * 
	 * @param finMain
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	@Override
	public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceCheckListReference object = new FinanceCheckListReference();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinanceCheckList().size(); i++) {
			FinanceCheckListReference finChekListRef = financeDetail.getFinanceCheckList().get(i);
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finChekListRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					finChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finChekListRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if (StringUtils.trimToEmpty(method).equals("doApprove")) {
				finChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}
			finChekListRef.setRecordStatus("");
			finChekListRef.setUserDetails(financeMain.getUserDetails());
			finChekListRef.setLastMntOn(financeMain.getLastMntOn());
			finChekListRef.setLastMntBy(financeMain.getLastMntBy());
			finChekListRef.setWorkflowId(financeMain.getWorkflowId());
			
			if (!StringUtils.equals(finChekListRef.getRecordType().trim(), "")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finChekListRef.getBefImage(), finChekListRef));
			}
		}
		
		auditDetailMap.put("checkListDetails", auditDetails);
		logger.debug("Leaving");
		return auditDetails;
	}
	
	
	/**
	 * processing CheckListDetails
	 * 
	 * @param checkList
	 * @param type
	 * @return
	 */
	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("checkListDetails");
		
		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			finChecklistRef.setWorkflowId(0);
			if (tableType.equals("")) {
				finChecklistRef.setVersion(finChecklistRef.getVersion() + 1);
				finChecklistRef.setRoleCode("");
				finChecklistRef.setNextRoleCode("");
				finChecklistRef.setTaskId("");
				finChecklistRef.setNextTaskId("");
			}
			if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				finChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				getFinanceCheckListReferenceDAO().save(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(finChecklistRef, tableType);
			}

			auditDetails.get(i).setModelData(finChecklistRef);

		}
		logger.debug("Leaving ");
		return auditDetails;
	}
	
	/**
	 * processing CheckListDetails
	 * 
	 * @param checkList
	 * @param type
	 * @return
	 */
	@Override
	public List<AuditDetail> doApprove(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("checkListDetails");
		
		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			finChecklistRef.setWorkflowId(0);
			if (tableType.equals("")) {
				finChecklistRef.setVersion(finChecklistRef.getVersion() + 1);
				finChecklistRef.setRoleCode("");
				finChecklistRef.setNextRoleCode("");
				finChecklistRef.setTaskId("");
				finChecklistRef.setNextTaskId("");
			}
			if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				finChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				getFinanceCheckListReferenceDAO().save(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(finChecklistRef, tableType);
			}

			auditDetails.get(i).setModelData(finChecklistRef);

		}
		logger.debug("Leaving ");
		return auditDetails;
	}
	
	
	public AuditHeader validate(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}
	
	@Override
	public List<AuditDetail> validate(FinanceDetail financeDetail, String method, String  usrLanguage){

		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("checkListDetails");

		if (auditDetails != null && !auditDetails.isEmpty()) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}


	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinanceCheckListReference financeCheckListReference= (FinanceCheckListReference) auditDetail.getModelData();

		FinanceCheckListReference tempFinanceCheckListReference= null;
		if (financeCheckListReference.isWorkflow()){
			tempFinanceCheckListReference = getFinanceCheckListReferenceDAO().
			getFinanceCheckListReferenceById(financeCheckListReference.getId(),financeCheckListReference.getQuestionId(),
					financeCheckListReference.getAnswer(),"_Temp");
		}
		FinanceCheckListReference befFinanceCheckListReference= getFinanceCheckListReferenceDAO().
		getFinanceCheckListReferenceById(financeCheckListReference.getId(),financeCheckListReference.getQuestionId(),
				financeCheckListReference.getAnswer(), "");

		FinanceCheckListReference oldFinanceCheckListReference= financeCheckListReference.getBefImage();


		String[] errParm= new String[3];
		String[] valueParm= new String[3];
		valueParm[0]=financeCheckListReference.getFinReference();
		valueParm[1]=financeCheckListReference.getLovDescQuesDesc();
		valueParm[2]=financeCheckListReference.getLovDescAnswerDesc();
		errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_CheckList")+":"+valueParm[1];
		errParm[2]=PennantJavaUtil.getLabel("label_Answer")+":"+valueParm[2];

		if (financeCheckListReference.isNew()){ // for New record or new record into work flow

			if (!financeCheckListReference.isWorkflow()){// With out Work flow only new records  
				if (befFinanceCheckListReference !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008"
							, errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeCheckListReference.getRecordType().equals(PennantConstants.RCD_ADD)){ // if records type is new
					if (befFinanceCheckListReference !=null || tempFinanceCheckListReference!=null ){ // if 
						//records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD
								, "41008", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeCheckListReference.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceCheckListReference ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinanceCheckListReference!=null 
							&& !oldFinanceCheckListReference.getLastMntOn().equals(befFinanceCheckListReference.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
				if (tempFinanceCheckListReference==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
				if (tempFinanceCheckListReference!=null && oldFinanceCheckListReference!=null 
						&& !oldFinanceCheckListReference.getLastMntOn().equals(tempFinanceCheckListReference.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeCheckListReference.isWorkflow()){
			financeCheckListReference.setBefImage(befFinanceCheckListReference);	
		}

		return auditDetail;
	}
	
	// Setters and getters
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
	    this.financeReferenceDetailDAO = financeReferenceDetailDAO;
    }

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
	    return financeReferenceDetailDAO;
    }


	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
	    this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
    }


	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
	    return financeCheckListReferenceDAO;
    }


	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
	    this.checkListDetailDAO = checkListDetailDAO;
    }


	public CheckListDetailDAO getCheckListDetailDAO() {
	    return checkListDetailDAO;
    }

	public CustomerDocumentDAO getCustomerDocumentDAO() {
    	return customerDocumentDao;
    }

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDao) {
    	this.customerDocumentDao = customerDocumentDao;
    }

}
