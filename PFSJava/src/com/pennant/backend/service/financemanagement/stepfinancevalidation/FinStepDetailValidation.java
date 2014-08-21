package com.pennant.backend.service.financemanagement.stepfinancevalidation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinStepDetailValidation {
	
   private FinanceStepDetailDAO financeStepDetailDAO;

   public FinStepDetailValidation(FinanceStepDetailDAO finStepDetailDAO){
	   this.setFinanceStepDetailDAO(finStepDetailDAO);
   }
   
   public FinanceStepDetailDAO getFinanceStepDetailDAO() {
	    return financeStepDetailDAO;
   }
	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
	    this.financeStepDetailDAO = financeStepDetailDAO;
   }
	
	public AuditHeader finStepPolicyValidation(AuditHeader auditHeader, String finReference, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(),finReference, method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> finStepPolicyListValidation(List<AuditDetail> auditDetails, String finReference, String method,String  usrLanguage){
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i),finReference, method, usrLanguage);
				auditDetail.getErrorDetails();
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}
		
	private AuditDetail validate(AuditDetail auditDetail, String finReference, String method,String  usrLanguage){
		
		FinanceStepPolicyDetail financeStepPolicyDetail= (FinanceStepPolicyDetail) auditDetail.getModelData();
		FinanceStepPolicyDetail tempFinStepPolicy= null;
		if (financeStepPolicyDetail.isWorkflow()){
			tempFinStepPolicy = getFinanceStepDetailDAO().getFinStepPolicy(finReference, financeStepPolicyDetail.getStepNo(), "_Temp");
		}
		
		FinanceStepPolicyDetail befFinStepPolicy= getFinanceStepDetailDAO().getFinStepPolicy(finReference, financeStepPolicyDetail.getStepNo(),"");
		FinanceStepPolicyDetail old_finStepPolicy= financeStepPolicyDetail.getBefImage();
		
		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = String.valueOf(financeStepPolicyDetail.getStepNo());
		
		errParm[0] = PennantJavaUtil.getLabel("label_LoanReference") + ":"+ valueParm[0];
		
		
		if (financeStepPolicyDetail.isNew()){ // for New record or new record into work flow

			if (!financeStepPolicyDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinStepPolicy !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}	
			}else{ // with work flow

				if (financeStepPolicyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinStepPolicy !=null || tempFinStepPolicy!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinStepPolicy ==null || tempFinStepPolicy!=null &&
							!befFinStepPolicy.getLastMntOn().equals(befFinStepPolicy.getLastMntOn())){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeStepPolicyDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinStepPolicy ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (old_finStepPolicy!=null && !old_finStepPolicy.getLastMntOn().equals(befFinStepPolicy.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempFinStepPolicy==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinStepPolicy!=null  && old_finStepPolicy!=null && !old_finStepPolicy.getLastMntOn().equals(tempFinStepPolicy.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeStepPolicyDetail.isWorkflow()){
			auditDetail.setBefImage(befFinStepPolicy);	
		}
		return auditDetail;
	}

}
