package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinBillingDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinBillingDetailValidation {

	private FinBillingDetailDAO finBillingDetailDAO;
	
	public FinBillingDetailValidation(FinBillingDetailDAO finBillingDetailDAO) {
		this.finBillingDetailDAO = finBillingDetailDAO;
	}
	
	/**
	 * @return the billingDetailDAO
	 */
	public FinBillingDetailDAO getFinBillingDetailDAO() {
		return finBillingDetailDAO;
	}

	public AuditHeader contributionValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> BillingListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		FinBillingDetail billingDetail= (FinBillingDetail) auditDetail.getModelData();
		FinBillingDetail tempFinBillingDetail= null;
		if (billingDetail.isWorkflow()){
			tempFinBillingDetail = getFinBillingDetailDAO().getFinBillingDetailByID(
					billingDetail.getFinReference(),billingDetail.getProgClaimDate(),"_Temp");
		}
		
		FinBillingDetail befFinBillingDetail= getFinBillingDetailDAO().getFinBillingDetailByID(
				billingDetail.getFinReference(),billingDetail.getProgClaimDate(),"");
		FinBillingDetail old_FinBillingDetail= billingDetail.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = billingDetail.getFinReference();
		valueParm[1] = DateUtility.formateDate(billingDetail.getProgClaimDate(),PennantConstants.dateFormate);

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ProgClaimDate") + ":"+valueParm[1];
		
		if (billingDetail.isNew()){ // for New record or new record into work flow

			if (!billingDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinBillingDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (billingDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinBillingDetail !=null || tempFinBillingDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinBillingDetail ==null || tempFinBillingDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!billingDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinBillingDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (old_FinBillingDetail!=null && !old_FinBillingDetail.getLastMntOn().equals(befFinBillingDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempFinBillingDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinBillingDetail!=null  && old_FinBillingDetail!=null && !old_FinBillingDetail.getLastMntOn().equals(tempFinBillingDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !billingDetail.isWorkflow()){
			billingDetail.setBefImage(befFinBillingDetail);	
		}

		return auditDetail;
	}

	
}
