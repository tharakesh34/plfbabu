package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CreditReviewSummaryEntryValidation {
	
	private CreditReviewSummaryDAO creditReviewSummaryDAO;
	

	public CreditReviewSummaryEntryValidation() {
		super();
	}

	public AuditHeader creditReviewSummaryValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> creditReviewSummaryListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				auditDetail.getErrorDetails();
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

		
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){
		
		FinCreditReviewSummary creditReviewSummary= (FinCreditReviewSummary) auditDetail.getModelData();
		FinCreditReviewSummary tempCreditReviewSummary= null;
		if (creditReviewSummary.isWorkflow()){
			tempCreditReviewSummary = getCreditReviewSummaryDAO().getCreditReviewSummaryById(creditReviewSummary.getSummaryId(),creditReviewSummary.getDetailId(),"_Temp");
		}
		
		FinCreditReviewSummary befcreditReviewSummary= getCreditReviewSummaryDAO().getCreditReviewSummaryById(creditReviewSummary.getSummaryId(),creditReviewSummary.getDetailId(),"");
		FinCreditReviewSummary oldCreditReviewSummary= creditReviewSummary.getBefImage();
		
		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = creditReviewSummary.getSubCategoryCode();
		
		errParm[0] = PennantJavaUtil.getLabel("label_SubCategoryCode") + ":"+ valueParm[0];
		
		
		if (creditReviewSummary.isNew()){ // for New record or new record into work flow

			if (!creditReviewSummary.isWorkflow()){// With out Work flow only new records  
				if (befcreditReviewSummary !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}	
			}else{ // with work flow

				if (creditReviewSummary.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befcreditReviewSummary !=null || tempCreditReviewSummary!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befcreditReviewSummary ==null || tempCreditReviewSummary!=null &&
							!befcreditReviewSummary.getLastMntOn().equals(befcreditReviewSummary.getLastMntOn())){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!creditReviewSummary.isWorkflow()){	// With out Work flow for update and delete

				if (befcreditReviewSummary ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCreditReviewSummary!=null && !oldCreditReviewSummary.getLastMntOn().equals(befcreditReviewSummary.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempCreditReviewSummary==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCreditReviewSummary!=null  && oldCreditReviewSummary!=null && !oldCreditReviewSummary.getLastMntOn().equals(tempCreditReviewSummary.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !creditReviewSummary.isWorkflow()){
			auditDetail.setBefImage(befcreditReviewSummary);	
		}
		return auditDetail;
	}
	public void setCreditReviewSummaryDAO(CreditReviewSummaryDAO creditReviewSummaryDAO) {
	    this.creditReviewSummaryDAO = creditReviewSummaryDAO;
    }
	public CreditReviewSummaryDAO getCreditReviewSummaryDAO() {
	    return creditReviewSummaryDAO;
    }

	
}
