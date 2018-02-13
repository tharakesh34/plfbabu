package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerIncomeValidation {

	private CustomerIncomeDAO customerIncomeDAO;
	private CustomerDetails customerDetails;

	public CustomerIncomeValidation(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	/**
	 * @return the customerIncomeDAO
	 */
	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public AuditHeader incomeValidation(AuditHeader auditHeader, String method){
		customerDetails = new CustomerDetails();
		customerDetails.setCustomer(new Customer());
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> incomeListValidation(CustomerDetails customerDetails, String method,String  usrLanguage){
		this.customerDetails = customerDetails;
		List<AuditDetail> auditDetails = customerDetails.getAuditDetailMap().get("Income");
		if(auditDetails!=null && !auditDetails.isEmpty()){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
				if(!(auditDetail.getErrorDetails() != null && auditDetail.getErrorDetails().isEmpty())){
					break;
				}
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}


	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){

		CustomerIncome customerIncome= (CustomerIncome) auditDetail.getModelData();
		CustomerIncome tempCustomerIncome= null;
		if (customerIncome.isWorkflow()){
			tempCustomerIncome = getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"_Temp");
		}

		CustomerIncome befCustomerIncome= getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"");
		
		CustomerIncome oldCustomerIncome= customerIncome.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = StringUtils.trimToEmpty(customerIncome.getLovDescCustCIF());
		valueParm[1] = customerIncome.getCustIncomeType();
		valueParm[2] = String.valueOf(customerIncome.isJointCust());

		errParm[0] = PennantJavaUtil.getLabel("IncomeDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustIncomeType") + ":"+valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_JointCust") + ":"+valueParm[1];

		if(!customerDetails.getCustomer().isJointCust() && customerIncome.isJointCust()){
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,  "41016", errParm, valueParm), usrLanguage));
			return auditDetail;
		}
		
		if (customerIncome.isNew()){ // for New record or new record into work flow

			if (!customerIncome.isWorkflow()){// With out Work flow only new records  
				if (befCustomerIncome !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (customerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerIncome !=null || tempCustomerIncome!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerIncome ==null || tempCustomerIncome!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerIncome.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerIncome ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerIncome!=null && !oldCustomerIncome.getLastMntOn().equals(befCustomerIncome.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{
				if (tempCustomerIncome==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerIncome!=null  && oldCustomerIncome!=null && !oldCustomerIncome.getLastMntOn().equals(tempCustomerIncome.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerIncome.isWorkflow()){
			customerIncome.setBefImage(befCustomerIncome);	
		}
		return auditDetail;
	}

}
