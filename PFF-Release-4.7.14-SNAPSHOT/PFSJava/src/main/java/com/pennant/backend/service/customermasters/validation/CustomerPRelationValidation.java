package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerPRelationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerPRelationValidation {

	private CustomerPRelationDAO customerPRelationDAO;
	
	public CustomerPRelationValidation(CustomerPRelationDAO customerPRelationDAO) {
		this.customerPRelationDAO = customerPRelationDAO;
	}
	
	/**
	 * @return the customerPRelationDAO
	 */
	public CustomerPRelationDAO getCustomerPRelationDAO() {
		return customerPRelationDAO;
	}

	public AuditHeader pRelationValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> pRelationListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		CustomerPRelation customerPRelation= (CustomerPRelation) auditDetail.getModelData();
		CustomerPRelation tempCustomerPRelation= null;
		if (customerPRelation.isWorkflow()){
			tempCustomerPRelation = getCustomerPRelationDAO().getCustomerPRelationByID(customerPRelation.getId(),customerPRelation.getPRCustPRSNo(),"_Temp");
		}
		
		CustomerPRelation befCustomerPRelation= getCustomerPRelationDAO().getCustomerPRelationByID(customerPRelation.getId(),customerPRelation.getPRCustPRSNo(),"");
		CustomerPRelation oldCustomerPRelation= customerPRelation.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(customerPRelation.getId());
		valueParm[1] = String.valueOf(customerPRelation.getPRCustPRSNo());

		errParm[0] = PennantJavaUtil.getLabel("label_PRCustID")+ ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_PRCustPRSNo")+ ":"+ valueParm[1];
		
		if (customerPRelation.isNew()){ // for New record or new record into work flow

			if (!customerPRelation.isWorkflow()){// With out Work flow only new records  
				if (befCustomerPRelation !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
							errParm,null));
				}	
			}else{ // with work flow

				if (customerPRelation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerPRelation !=null || tempCustomerPRelation!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
								errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerPRelation ==null || tempCustomerPRelation!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",
								errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerPRelation.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerPRelation ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",
							errParm,null));
				}else{

					if (oldCustomerPRelation!=null && !oldCustomerPRelation.getLastMntOn().equals(
							befCustomerPRelation.getLastMntOn())){
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

				if (tempCustomerPRelation==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerPRelation!=null  && oldCustomerPRelation!=null && 
						!oldCustomerPRelation.getLastMntOn().equals(tempCustomerPRelation.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerPRelation.isWorkflow()){
			customerPRelation.setBefImage(befCustomerPRelation);	
		}

		return auditDetail;
	}

	
}
