package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerExtLiabilityValidation {

	private CustomerExtLiabilityDAO customerExtLiabilityDAO;

	public CustomerExtLiabilityValidation(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	/**
	 * @return the customerRatingDAO
	 */
	public CustomerExtLiabilityDAO getCustomerExtLiabilityDAO() {
		return customerExtLiabilityDAO;
	}

	public AuditHeader extLiabilityValidation(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> extLiabilityListValidation(List<AuditDetail> auditDetails, String method, String  usrLanguage){

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

		CustomerExtLiability customerExtLiability= (CustomerExtLiability) auditDetail.getModelData();
		CustomerExtLiability tempCustomerExtLiability= null;
		if (customerExtLiability.isWorkflow()){
			tempCustomerExtLiability = getCustomerExtLiabilityDAO().getCustomerExtLiabilityById(
					customerExtLiability.getId(),customerExtLiability.getLiabilitySeq(),"_Temp");
		}

		CustomerExtLiability befCustomerExtLiability= getCustomerExtLiabilityDAO().getCustomerExtLiabilityById(
				customerExtLiability.getId(),customerExtLiability.getLiabilitySeq(),"");
		
		CustomerExtLiability oldCustomerExtLiability= customerExtLiability.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerExtLiability.getLovDescCustCIF());
		valueParm[1] =String.valueOf(customerExtLiability.getLiabilitySeq());

        errParm[0] = PennantJavaUtil.getLabel("CustomerExtLiability") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
        errParm[1] = PennantJavaUtil.getLabel("label_LiabilitySeq") + "-" + valueParm[1];

		if (customerExtLiability.isNew()){ // for New record or new record into work flow

			if (!customerExtLiability.isWorkflow()){// With out Work flow only new records  
				if (befCustomerExtLiability !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
							errParm,null));
				}	
			}else{ // with work flow

				if (customerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerExtLiability !=null || tempCustomerExtLiability!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerExtLiability ==null || tempCustomerExtLiability!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerExtLiability.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerExtLiability ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerExtLiability!=null && !oldCustomerExtLiability.getLastMntOn().equals(
							befCustomerExtLiability.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",
									errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",
									errParm,null));
						}
					}
				}
			}else{

				if (tempCustomerExtLiability==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerExtLiability!=null  && oldCustomerExtLiability!=null && !oldCustomerExtLiability.getLastMntOn().equals(tempCustomerExtLiability.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetail(screenValidations(customerExtLiability));
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !customerExtLiability.isWorkflow()){
			customerExtLiability.setBefImage(befCustomerExtLiability);	
		}

		return auditDetail;
	}

	/**
	 * Method For Screen Level Validations
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	public ErrorDetails  screenValidations(CustomerExtLiability customerExtLiability){

		return null;
	}	
}
