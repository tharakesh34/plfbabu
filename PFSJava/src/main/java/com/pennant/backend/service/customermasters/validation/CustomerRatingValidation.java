package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerRatingDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerRatingValidation {

	private CustomerRatingDAO customerRatingDAO;
	
	public CustomerRatingValidation(CustomerRatingDAO customerRatingDAO) {
		this.customerRatingDAO = customerRatingDAO;
	}
	
	/**
	 * @return the customerRatingDAO
	 */
	public CustomerRatingDAO getCustomerRatingDAO() {
		return customerRatingDAO;
	}

	public AuditHeader ratingValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> ratingListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		CustomerRating customerRating= (CustomerRating) auditDetail.getModelData();
		CustomerRating tempCustomerRating= null;
		if (customerRating.isWorkflow()){
			tempCustomerRating = getCustomerRatingDAO().getCustomerRatingByID(
					customerRating.getId(),customerRating.getCustRatingType(),"_Temp");
		}
		
		CustomerRating befCustomerRating= getCustomerRatingDAO().getCustomerRatingByID(
				customerRating.getId(),customerRating.getCustRatingType(),"");
		CustomerRating oldCustomerRating= customerRating.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerRating.getLovDescCustCIF());
		valueParm[1] = customerRating.getCustRatingType();

        errParm[0] = PennantJavaUtil.getLabel("Rating") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustRatingType") + "-" + valueParm[1];
		
		if (customerRating.isNew()){ // for New record or new record into work flow

			if (!customerRating.isWorkflow()){// With out Work flow only new records  
				if (befCustomerRating !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (customerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerRating !=null || tempCustomerRating!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerRating ==null || tempCustomerRating!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerRating.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerRating ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerRating!=null && !oldCustomerRating.getLastMntOn().equals(befCustomerRating.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempCustomerRating==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerRating!=null  && oldCustomerRating!=null && !oldCustomerRating.getLastMntOn().equals(tempCustomerRating.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerRating.isWorkflow()){
			customerRating.setBefImage(befCustomerRating);	
		}

		return auditDetail;
	}

	
}
