package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CorporateCustomerDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CorporateCustomerValidation {

	private CorporateCustomerDetailDAO  corporateCustomerDetailDAO;
	
	public CorporateCustomerValidation(CorporateCustomerDetailDAO  corporateCustomerDetailDAO) {
		this.corporateCustomerDetailDAO = corporateCustomerDetailDAO;
	}
	
	/**
	 * @return the CorporateCustomerDetailDAO
	 */
	public CorporateCustomerDetailDAO getCorporateCustomerDetailDAO() {
		return corporateCustomerDetailDAO;
	}
	

	public AuditHeader corporateDetailValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> corporateDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){

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

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		CorporateCustomerDetail corporateCustomerDetail= (CorporateCustomerDetail) 
		auditDetail.getModelData();

		CorporateCustomerDetail tempCorporateCustomerDetail= null;
		if (corporateCustomerDetail.isWorkflow()){
			tempCorporateCustomerDetail = getCorporateCustomerDetailDAO().getCorporateCustomerDetailById(
					corporateCustomerDetail.getId(), "_Temp");
		}
		CorporateCustomerDetail befCorporateCustomerDetail= getCorporateCustomerDetailDAO().getCorporateCustomerDetailById(
				corporateCustomerDetail.getId(), "");

		CorporateCustomerDetail oldCorporateCustomerDetail= corporateCustomerDetail.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(corporateCustomerDetail.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_CustId")+":"+valueParm[0];

		if (corporateCustomerDetail.isNew()){ // for New record or new record into work flow

			if (!corporateCustomerDetail.isWorkflow()){// With out Work flow only new records  
				if (befCorporateCustomerDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", 
									errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (corporateCustomerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCorporateCustomerDetail !=null || tempCorporateCustomerDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", 
										errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCorporateCustomerDetail ==null || tempCorporateCustomerDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
										errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!corporateCustomerDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCorporateCustomerDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", 
									errParm,valueParm), usrLanguage));
				}else{
					if (oldCorporateCustomerDetail!=null && 
							!oldCorporateCustomerDetail.getLastMntOn().equals(
									befCorporateCustomerDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003",
											errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", 
											errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCorporateCustomerDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}

				if (tempCorporateCustomerDetail!=null && oldCorporateCustomerDetail!=null && 
						!oldCorporateCustomerDetail.getLastMntOn().equals(
								tempCorporateCustomerDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !corporateCustomerDetail.isWorkflow()){
			corporateCustomerDetail.setBefImage(befCorporateCustomerDetail);	
		}

		return auditDetail;
	}

	
}
