package com.pennant.backend.service.rulefactory.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.rulefactory.OverdueChargeDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.OverdueChargeDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class OverdueChargeDetailValidation {

private OverdueChargeDetailDAO overdueChargeDetailDAO;
	
	/**
	 * Setters And Getters
	 * @param feeTierDAO
	 */
	public OverdueChargeDetailValidation(OverdueChargeDetailDAO detailsDAO) {
		this.overdueChargeDetailDAO = detailsDAO;
	}
	public OverdueChargeDetailDAO getDetailsDAO() {
		return overdueChargeDetailDAO;
	}

	public AuditHeader overdueChargeDetailValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> overDueChargeDetailsListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		OverdueChargeDetail overdueChargeDetail= (OverdueChargeDetail) auditDetail.getModelData();
		OverdueChargeDetail tempChargeDetail= null;
		if (overdueChargeDetail.isWorkflow()){
			tempChargeDetail = getDetailsDAO().getOverdueChargeDetailById(overdueChargeDetail.getoDCRuleCode(),overdueChargeDetail.getoDCCustCtg(),"_Temp");
		}
		
		OverdueChargeDetail befChargeDetail= getDetailsDAO().getOverdueChargeDetailById(overdueChargeDetail.getoDCRuleCode(),overdueChargeDetail.getoDCCustCtg(),"");
		OverdueChargeDetail oldChargeDetail= overdueChargeDetail.getBefImage();
		
		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = overdueChargeDetail.getoDCRuleCode();
		
		errParm[0] = PennantJavaUtil.getLabel("label_ODCRuleCode") + ":"+ valueParm[0];
		
		if (overdueChargeDetail.isNew()){ // for New record or new record into work flow

			if (!overdueChargeDetail.isWorkflow()){// With out Work flow only new records  
				if (befChargeDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}	
			}else{ // with work flow

				if (overdueChargeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befChargeDetail !=null || tempChargeDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befChargeDetail ==null || tempChargeDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!overdueChargeDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befChargeDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldChargeDetail!=null && !oldChargeDetail.getLastMntOn().equals(befChargeDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempChargeDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempChargeDetail!=null  && oldChargeDetail!=null && !oldChargeDetail.getLastMntOn().equals(tempChargeDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !overdueChargeDetail.isWorkflow()){
			auditDetail.setBefImage(befChargeDetail);	
		}
		return auditDetail;
	}
}
