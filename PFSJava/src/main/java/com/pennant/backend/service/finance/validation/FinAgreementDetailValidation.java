package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinAgreementDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class FinAgreementDetailValidation {

	private FinAgreementDetailDAO finAgreementDetailDAO;
	
	public FinAgreementDetailValidation(FinAgreementDetailDAO finAgreementDetailDAO) {
		this.finAgreementDetailDAO = finAgreementDetailDAO;
	}
	
	/**
	 * @return the contributorDetailDAO
	 */
	public FinAgreementDetailDAO getFinAgreementDetailDAO() {
		return finAgreementDetailDAO;
	}

	public AuditHeader agreementValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> agreementDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		FinAgreementDetail agreementDetail= (FinAgreementDetail) auditDetail.getModelData();
		FinAgreementDetail tempFinAgreementDetail= null;
		if (agreementDetail.isWorkflow()){
			tempFinAgreementDetail = getFinAgreementDetailDAO().getFinAgreementDetailById(
					agreementDetail.getFinReference(),agreementDetail.getAgrId(),"_Temp");
		}
		
		FinAgreementDetail befFinAgreementDetail= getFinAgreementDetailDAO().getFinAgreementDetailById(
				agreementDetail.getFinReference(),agreementDetail.getAgrId(),"");
		
		FinAgreementDetail oldFinAgreementDetail= agreementDetail.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = agreementDetail.getFinReference();
		valueParm[1] = String.valueOf(agreementDetail.getAgrId());

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AgrId") + ":"+valueParm[1];
		
		if (agreementDetail.isNew()){ // for New record or new record into work flow

			if (!agreementDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinAgreementDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (agreementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinAgreementDetail !=null || tempFinAgreementDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinAgreementDetail ==null || tempFinAgreementDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!agreementDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinAgreementDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldFinAgreementDetail!=null && !oldFinAgreementDetail.getLastMntOn().equals(befFinAgreementDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempFinAgreementDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinAgreementDetail!=null  && oldFinAgreementDetail!=null && !oldFinAgreementDetail.getLastMntOn().equals(tempFinAgreementDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !agreementDetail.isWorkflow()){
			agreementDetail.setBefImage(befFinAgreementDetail);	
		}

		return auditDetail;
	}

	
}
