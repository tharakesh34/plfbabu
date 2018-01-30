package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerEmploymentDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerEmploymentDetailValidation {

	private CustomerEmploymentDetailDAO  customerEmploymentDetailDAO;
	
	public CustomerEmploymentDetailValidation(CustomerEmploymentDetailDAO  customerEmploymentDetailDAO) {
		this.customerEmploymentDetailDAO = customerEmploymentDetailDAO;
	}
	
	/**
	 * @return the customerEmploymentDetailDAODAO
	 */
	public CustomerEmploymentDetailDAO getCustomerEmploymentDetailDAO() {
		return customerEmploymentDetailDAO;
	}

	public AuditHeader employmentDetailValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> employmentDetailListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		CustomerEmploymentDetail customerEmploymentDetail= (CustomerEmploymentDetail) auditDetail.getModelData();
		CustomerEmploymentDetail tempCustomerEmploymentDetail= null;
		if (customerEmploymentDetail.isWorkflow()){
			tempCustomerEmploymentDetail = getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByCustEmpId(customerEmploymentDetail.getCustEmpId(),"_Temp");
		}
		
		CustomerEmploymentDetail befCustomerEmploymentDetail= getCustomerEmploymentDetailDAO().getCustomerEmploymentDetailByCustEmpId(customerEmploymentDetail.getCustEmpId(),"");
		CustomerEmploymentDetail oldCustomerEmploymentDetail= customerEmploymentDetail.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustCIF());
		valueParm[1] = customerEmploymentDetail.getLovDesccustEmpName();
		
		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustEmpName") + ":"+valueParm[1];

        errParm[0] = PennantJavaUtil.getLabel("EmploymentDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
        errParm[1] = PennantJavaUtil.getLabel("label_CustEmpName") + "-" + valueParm[1];
		
		if (customerEmploymentDetail.isNew()){ // for New record or new record into work flow

			if (!customerEmploymentDetail.isWorkflow()){// With out Work flow only new records  
				if (befCustomerEmploymentDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (customerEmploymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerEmploymentDetail !=null || tempCustomerEmploymentDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerEmploymentDetail ==null || tempCustomerEmploymentDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerEmploymentDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerEmploymentDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerEmploymentDetail!=null && !oldCustomerEmploymentDetail.getLastMntOn().equals(befCustomerEmploymentDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempCustomerEmploymentDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerEmploymentDetail!=null  && oldCustomerEmploymentDetail!=null && !oldCustomerEmploymentDetail.getLastMntOn().equals(tempCustomerEmploymentDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		int count = getCustomerEmploymentDetailDAO().getCustomerEmploymentByCustEmpName(customerEmploymentDetail.getCustID(),customerEmploymentDetail.getCustEmpName(),customerEmploymentDetail.getCustEmpId(), "_View");
		if(count != 0 ){
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerEmploymentDetail.isWorkflow()){
			customerEmploymentDetail.setBefImage(befCustomerEmploymentDetail);	
		}

		return auditDetail;
	}

	
}
