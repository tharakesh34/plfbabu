package com.pennant.backend.service.rmtmasters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class TransactionEntryValidation {

	private TransactionEntryDAO transactionEntryDAO;
	
	/**
	 * Setters And Getters
	 * @param feeTierDAO
	 */
	public TransactionEntryValidation(TransactionEntryDAO feeTierDAO) {
		this.transactionEntryDAO = feeTierDAO;
	}
	public TransactionEntryDAO getFeeTierDAO() {
		return transactionEntryDAO;
	}

	public AuditHeader transactionEntryValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> transactionEntryListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

		
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){
		
		TransactionEntry transactionEntry= (TransactionEntry) auditDetail.getModelData();
		TransactionEntry tempFeeTier= null;
		if (transactionEntry.isWorkflow()){
			tempFeeTier = getFeeTierDAO().getTransactionEntryById(transactionEntry.getId(),transactionEntry.getTransOrder(),"_Temp");
		}
		
		TransactionEntry beftransactionEntry= getFeeTierDAO().getTransactionEntryById(transactionEntry.getId(),transactionEntry.getTransOrder(),"");
		TransactionEntry oldTransactionEntry= transactionEntry.getBefImage();
		
		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = transactionEntry.getLovDescEventCodeName();
		valueParm[1] = transactionEntry.getLovDescAccSetCodeName();
		valueParm[2] = String.valueOf(transactionEntry.getTransOrder());
		
		errParm[0] = PennantJavaUtil.getLabel("label_EventCode") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AccountSetCode") + ":"+ valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_TransOrder") + ":"+ valueParm[2];
		
		if (transactionEntry.isNew()){ // for New record or new record into work flow

			if (!transactionEntry.isWorkflow()){// With out Work flow only new records  
				if (beftransactionEntry !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}	
			}else{ // with work flow

				if (transactionEntry.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (beftransactionEntry !=null || tempFeeTier!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (beftransactionEntry ==null || tempFeeTier!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!transactionEntry.isWorkflow()){	// With out Work flow for update and delete

				if (beftransactionEntry ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldTransactionEntry!=null && !oldTransactionEntry.getLastMntOn().equals(beftransactionEntry.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempFeeTier==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFeeTier!=null  && oldTransactionEntry!=null && !oldTransactionEntry.getLastMntOn().equals(tempFeeTier.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !transactionEntry.isWorkflow()){
			auditDetail.setBefImage(beftransactionEntry);	
		}
		return auditDetail;
	}

	
}
