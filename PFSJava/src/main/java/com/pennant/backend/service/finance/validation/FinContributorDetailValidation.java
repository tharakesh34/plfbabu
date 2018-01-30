package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinContributorDetailValidation {

	private FinContributorDetailDAO finContributorDetailDAO;
	
	public FinContributorDetailValidation(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}
	
	/**
	 * @return the contributorDetailDAO
	 */
	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public AuditHeader contributionValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> ContributorListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		FinContributorDetail contributorDetail= (FinContributorDetail) auditDetail.getModelData();
		FinContributorDetail tempFinContributorDetail= null;
		if (contributorDetail.isWorkflow()){
			tempFinContributorDetail = getFinContributorDetailDAO().getFinContributorDetailByID(
					contributorDetail.getFinReference(),contributorDetail.getContributorBaseNo(),"_Temp");
		}
		
		FinContributorDetail befFinContributorDetail= getFinContributorDetailDAO().getFinContributorDetailByID(
				contributorDetail.getFinReference(),contributorDetail.getContributorBaseNo(),"");
		FinContributorDetail oldFinContributorDetail= contributorDetail.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = contributorDetail.getFinReference();
		valueParm[1] = contributorDetail.getLovDescContributorCIF();

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+valueParm[1];
		
		if (contributorDetail.isNew()){ // for New record or new record into work flow

			if (!contributorDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinContributorDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (contributorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinContributorDetail !=null || tempFinContributorDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinContributorDetail ==null || tempFinContributorDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!contributorDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinContributorDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldFinContributorDetail!=null && !oldFinContributorDetail.getLastMntOn().equals(befFinContributorDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempFinContributorDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinContributorDetail!=null  && oldFinContributorDetail!=null && !oldFinContributorDetail.getLastMntOn().equals(tempFinContributorDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !contributorDetail.isWorkflow()){
			contributorDetail.setBefImage(befFinContributorDetail);	
		}

		return auditDetail;
	}

	
}
