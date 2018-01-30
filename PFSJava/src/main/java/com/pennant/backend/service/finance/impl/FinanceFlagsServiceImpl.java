package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinFlagsHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.FlagService;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinanceFlagsServiceImpl extends GenericService<FinanceDetail> implements
        FinanceFlagsService {

	private static final Logger logger = Logger.getLogger(FinanceFlagsServiceImpl.class);

	private AuditHeaderDAO 				auditHeaderDAO;
	private FinFlagsHeaderDAO 			finFlagsHeaderDAO;
	private FinFlagDetailsDAO 			finFlagDetailsDAO;
	private FlagService flagService;
	
	
	public FinanceFlagsServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public FinFlagsHeaderDAO getFinFlagsHeaderDAO() {
	    return finFlagsHeaderDAO;
    }

	public void setFinFlagsHeaderDAO(FinFlagsHeaderDAO finFlagsHeaderDAO) {
	    this.finFlagsHeaderDAO = finFlagsHeaderDAO;
    }

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
	    return finFlagDetailsDAO;
    }

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
	    this.finFlagDetailsDAO = finFlagDetailsDAO;
    }
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public FinanceFlag getNewFinanceFlags() {
		return getFinFlagsHeaderDAO().getNewFinanceFlags();
	}
	

	@Override
	public FinanceFlag getFinanceFlagsByRef(String financeReference, String type) {
		FinanceFlag financeFlag = new FinanceFlag();
		financeFlag = getFinFlagsHeaderDAO().getFinFlagsHeaderByRef(financeReference, type);
		if (financeFlag != null) {
			financeFlag.setFinFlagDetailList(getFinFlagDetailsDAO().getFinFlagsByFinRef(financeReference, 
					FinanceConstants.FINSER_EVENT_FINFLAGS, type));
		}
		return financeFlag;
	}

	@Override
	public FinanceFlag getApprovedFinanceFlagsById(String finReference) {
		return getFinFlagsHeaderDAO().getFinFlagsHeaderByRef(finReference,  "_View");
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table SukukBrokers/SukukBrokers_Temp 
	 * 			by using SukukBrokerDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using SukukBrokerDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtSukukBrokers by using auditHeaderDAO.addAudit(auditHeader)
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
	 * 		a)	Add new Record for the new record in the DB table FinanceFlags/FinanceFlags_Temp 
	 * 			by using FinFlagsHeaderDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinFlagsHeaderDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinanceFlags by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		FinanceFlag financeFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();

		if (financeFlag.isWorkflow()) {
			tableType="_Temp";
		}

		if (financeFlag.isNew()) {
			if(StringUtils.isEmpty(tableType)){
				financeFlag.setRecordType("");
				financeFlag.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			getFinFlagsHeaderDAO().save(financeFlag,tableType);
		}else{
			getFinFlagsHeaderDAO().update(financeFlag,tableType);
		}
		
		//Retrieving List of Audit Details For FinFlagsDetail expense  related modules
		List<AuditDetail>	details = processingFinFlags(financeFlag, tableType);
		if (details!=null) {
			auditDetails.addAll(details);
        }

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method For Preparing List of AuditDetails for Finance Flags Information
	 * 
	 * @param auditDetails
	 * @param type
	 * @param finReference
	 * @return
	 */
	private List<AuditDetail> processingFinFlags(FinanceFlag financeFlag, String type) {
		logger.debug("Entering");	
		List<AuditDetail> auditDetails = financeFlag.getAuditDetailMap().get("FinFlagsDetail");
		
		if (auditDetails == null || auditDetails.isEmpty()) {
            return auditDetails;
        }
 		boolean saveRecord = false;
		//boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		finFlagDetailsDAO.deleteList(financeFlag.getFinReference(),"FINANCE", type);
		
		for (AuditDetail auditDetail : auditDetails) {

			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetail.getModelData();
			saveRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
			}
			finFlagsDetail.setReference(financeFlag.getFinReference());
			if(financeFlag.isNew()){
				finFlagsDetail.setNewRecord(true);
			}
			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType()
				        .equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					saveRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_UPD)) {
				saveRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(
			        PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNew()) {
					saveRecord = true;
				} else {
					saveRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finFlagDetailsDAO.delete(financeFlag.getFinReference(),finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(), type);
				finFlagDetailsDAO.save(finFlagsDetail, type);
			}
			if (deleteRecord) {
				finFlagDetailsDAO.deleteList(financeFlag.getFinReference(), finFlagsDetail.getModuleName(),type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetail.setModelData(finFlagsDetail);
		}
		logger.debug("Leaving");
		return auditDetails;

	}
	
	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinanceFlag by using finFlagsHeaderDAO delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinanceFlag by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceFlag financeFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();
		getFinFlagsHeaderDAO().delete(financeFlag, "");
		
		auditHeader.setAuditDetails(listDeletion(financeFlag, "", auditHeader.getAuditTranType()));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FinanceFlag by using finFlagsHeaderDAO delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFinanceFlag by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader deleteFinanceFlag(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceFlag financeFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();
		
		List<AuditDetail> auditList = financeFlag.getAuditDetailMap().get("FinFlagsDetail");
		for (AuditDetail auditDetail : auditList) {
			auditDetail.setAuditTranType(auditHeader.getAuditTranType());
        }
		
		if (financeFlag.getFinFlagDetailList() != null) {
			for (FinFlagsDetail flagDetail : financeFlag.getFinFlagDetailList()) {
				String finReference = financeFlag.getFinReference();
				String flagCode = flagDetail.getFlagCode();
				String moduleName = FinanceConstants.MODULE_NAME;

				// delete particular flag code
				getFinFlagDetailsDAO().delete(finReference, flagCode, moduleName, "");
			}

			// check records
			int rcdCount = getFinFlagDetailsDAO().getFinFlagDetailCountByRef(financeFlag.getFinReference(), "");
			if (rcdCount <= 0) {
				getFinFlagsHeaderDAO().delete(financeFlag, "");
			}
		}
		
		auditHeader.setAuditDetails(auditList);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method deletion of FlagsDetail list with existing Finance Reference
	 * 
	 * @param FlagsDetail
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(FinanceFlag financeFlag, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditList = financeFlag.getAuditDetailMap().get("FinFlagsDetail");
		for (AuditDetail auditDetail : auditList) {
			auditDetail.setAuditTranType(auditTranType);
        }
		getFinFlagDetailsDAO().deleteList(financeFlag.getFinReference(),"FINANCE", tableType);
		logger.debug("Leaving");
		return  auditList;
	}
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceFlag financeFlag = (FinanceFlag) auditHeader.getAuditDetail()
		        .getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinFlagsHeaderDAO().delete(financeFlag, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, financeFlag.getBefImage(), financeFlag));
		auditHeader.setAuditDetails(listDeletion(financeFlag, "_Temp", auditHeader.getAuditTranType()));
		
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

		FinanceFlag financeFlag = (FinanceFlag) auditHeader.getAuditDetail().getModelData();
		
		if (financeFlag.getFinFlagDetailList() != null && financeFlag.getFinFlagDetailList().size() > 0) {

			HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

			List<AuditDetail> auditDetails= setFinFlagsDetailsAuditData(financeFlag,  method);				
			
			auditDetails = validateList(auditDetails, method, financeFlag.getUserDetails().getLanguage());
			auditDetailMap.put("FinFlagsDetail", auditDetails);
			auditHeader.setAuditDetails(auditDetails);				
			financeFlag.setAuditDetailMap(auditDetailMap);
		}

		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> validateList(List<AuditDetail> auditDetails, String method, String usrLanguage) {
		logger.debug("Entering");
		for (AuditDetail auditDetail : auditDetails) {
		
		FinFlagsDetail finFlagsDetail= (FinFlagsDetail) auditDetail.getModelData();
		FinFlagsDetail tempFinFlagsDetail= null;
		if (finFlagsDetail.isWorkflow()){
			tempFinFlagsDetail = getFinFlagDetailsDAO().getFinFlagsByRef(finFlagsDetail.getReference(),finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(),"_Temp");
		}

		FinFlagsDetail befFinFlagsDetail= getFinFlagDetailsDAO().getFinFlagsByRef(finFlagsDetail.getReference(),finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(),"");
		FinFlagsDetail oldFinFlagsDetail= finFlagsDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(finFlagsDetail.getReference());

        errParm[0] = PennantJavaUtil.getLabel("FinFlagsDetail") +" , " + PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0]+ " and ";

		if (finFlagsDetail.isNew()){ // for New record or new record into work flow

			if (!finFlagsDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinFlagsDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
							errParm,null));
				}	
			}else{ // with work flow

				if (finFlagsDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinFlagsDetail !=null || tempFinFlagsDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinFlagsDetail ==null || tempFinFlagsDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFlagsDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinFlagsDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldFinFlagsDetail!=null && !oldFinFlagsDetail.getLastMntOn().equals(
							befFinFlagsDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",
									errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",
									errParm,null));
						}
					}
				}
			}else{

				if (tempFinFlagsDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinFlagsDetail!=null  && oldFinFlagsDetail!=null && !oldFinFlagsDetail.getLastMntOn().equals(tempFinFlagsDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFlagsDetail.isWorkflow()) {
				finFlagsDetail.setBefImage(befFinFlagsDetail);
			}
		}
		logger.debug("Leaving");
		return auditDetails;
    }

	
	private List<AuditDetail> setFinFlagsDetailsAuditData(FinanceFlag financeFlag,
            String method) {
		logger.debug("Entering");
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeFlag.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinFlagsDetail(), new FinFlagsDetail().getExcludeFields());
		int count=0;
		for (FinFlagsDetail finFlagsDetail : financeFlag.getFinFlagDetailList()) {
		
			finFlagsDetail.setRecordType(financeFlag.getRecordType());
			finFlagsDetail.setVersion(financeFlag.getVersion());
			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue; 
			}

			finFlagsDetail.setWorkflowId(financeFlag.getWorkflowId());
			if (financeFlag.getFinReference()!=null) {
				finFlagsDetail.setReference(financeFlag.getFinReference());
			}

			boolean isRcdType = false;

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeFlag.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(
				        PennantConstants.RECORD_TYPE_DEL)
				        || finFlagsDetail.getRecordType().equalsIgnoreCase(
				                PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			finFlagsDetail.setBefImage(finFlagsDetail);
			finFlagsDetail.setRecordStatus(financeFlag.getRecordStatus());
			finFlagsDetail.setUserDetails(financeFlag.getUserDetails());
			finFlagsDetail.setLastMntOn(financeFlag.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, count++, fields[0], fields[1], finFlagsDetail
			        .getBefImage(), finFlagsDetail));
		}
		logger.debug("Leaving");
		return auditDetails;

    }
	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getFinFlagsHeaderDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method,boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceFlag financeFlag = (FinanceFlag) auditDetail.getModelData();

		FinanceFlag tempFinanceFlag = null;
		if (financeFlag.isWorkflow()) {
			tempFinanceFlag = getFinFlagsHeaderDAO().getFinFlagsHeaderByRef(financeFlag.getFinReference(),
					"_Temp");
		}
		FinanceFlag befFinanceFlag = getFinFlagsHeaderDAO().getFinFlagsHeaderByRef(financeFlag.getFinReference(), "");

		FinanceFlag oldFinanceFlag = financeFlag.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeFlag.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeFlag.isNew()) { // for New record or new record into work flow

			if (!financeFlag.isWorkflow()) {// With out Work flow only new records  
				if (befFinanceFlag != null) {	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeFlag.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinanceFlag != null || tempFinanceFlag != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceFlag == null || tempFinanceFlag != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeFlag.isWorkflow()) {	// With out Work flow for update and delete

				if (befFinanceFlag == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceFlag != null
					        && !oldFinanceFlag.getLastMntOn().equals(
					        		befFinanceFlag.getLastMntOn())) {
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

				if (tempFinanceFlag == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldFinanceFlag != null && tempFinanceFlag!=null
				        && !oldFinanceFlag.getLastMntOn().equals(
				        		tempFinanceFlag.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
		        || !financeFlag.isWorkflow()) {
			financeFlag.setBefImage(befFinanceFlag);
		}
		logger.debug("Leaving");
		return auditDetail;
	}


	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getFinFlagsHeaderDAO().delete with parameters sukukBroker,""
	 * 		b)  NEW		Add new record in to main table by using getFinFlagsHeaderDAO().save with parameters FinanceFlag,""
	 * 		c)  EDIT	Update record in the main table by using getFinFlagsHeaderDAO().update with parameters FinanceFlag,""
	 * 3)	Delete the record from the workFlow table by using getFinFlagsHeaderDAO().delete with parameters FinanceFlag,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFinanceFlag by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFinanceFlag by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinanceFlag financeFlag = new FinanceFlag();
		BeanUtils.copyProperties((FinanceFlag) auditHeader.getAuditDetail().getModelData(),
				financeFlag);

		if (financeFlag.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinFlagsHeaderDAO().delete(financeFlag, "");
			auditDetails.addAll(listDeletion(financeFlag, "", auditHeader.getAuditTranType()));
		} else {
			financeFlag.setRoleCode("");
			financeFlag.setNextRoleCode("");
			financeFlag.setTaskId("");
			financeFlag.setNextTaskId("");
			financeFlag.setWorkflowId(0);

			if (financeFlag.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeFlag.setRecordType("");
				getFinFlagsHeaderDAO().save(financeFlag, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeFlag.setRecordType("");
				getFinFlagsHeaderDAO().update(financeFlag, "");
			}
		}
		if (financeFlag.getFinFlagDetailList() != null && financeFlag.getFinFlagDetailList().size() > 0) {
			List<AuditDetail>	details = processingFinFlags(financeFlag, "");
			if (details!=null) {
				auditDetails.addAll(details);
	        }
		}
		
		getFinFlagsHeaderDAO().delete(financeFlag, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(listDeletion(financeFlag, "_Temp",
		        auditHeader.getAuditTranType()));
		auditHeader.setAuditDetail(new AuditDetail(tranType, 1, financeFlag.getBefImage(), financeFlag));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}
	/*
	 * Method to get the schedule change module list from the 
	 * ScheduleEffectModule table
	 * 
	 */
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		return getFinFlagDetailsDAO().getScheduleEffectModuleList(schdChangeReq);
	}
	/**
	 * Validate financeFlag.
	 * @param financeFlag
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(FinanceFlag financeFlag) {
		logger.debug("Entering");

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		if (financeFlag != null) {
			List<FinFlagsDetail> finFlagsDetailList = financeFlag.getFinFlagDetailList();
			for (FinFlagsDetail detail : finFlagsDetailList) {
				// validate Master code with PLF system masters
				Flag flag = flagService.getApprovedFlagById(detail.getFlagCode());
				if (flag == null || !flag.isActive()) {
					String[] valueParm = new String[2];
					valueParm[0] = "flagCode";
					valueParm[1] = detail.getFlagCode();
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
		}

		logger.debug("Leaving");
		return auditDetail;
	}
	public void setFlagService(FlagService flagService) {
		this.flagService = flagService;
	}

}
