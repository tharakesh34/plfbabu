package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerChequeInfoValidation {

	private CustomerChequeInfoDAO customerChequeInfoDAO;

	public CustomerChequeInfoValidation(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}

	/**
	 * @return the customerRatingDAO
	 */
	public CustomerChequeInfoDAO getCustomerChequeInfoDAO() {
		return customerChequeInfoDAO;
	}

	public AuditHeader chequeInfoValidation(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> chequeInfoListValidation(List<AuditDetail> auditDetails, String method, String  usrLanguage){

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

		CustomerChequeInfo customerChequeInfo= (CustomerChequeInfo) auditDetail.getModelData();
		CustomerChequeInfo tempCustomerChequeInfo= null;
		if (customerChequeInfo.isWorkflow()){
			tempCustomerChequeInfo = getCustomerChequeInfoDAO().getCustomerChequeInfoById(
					customerChequeInfo.getId(),customerChequeInfo.getChequeSeq(),"_Temp");
		}

		CustomerChequeInfo befCustomerChequeInfo= getCustomerChequeInfoDAO().getCustomerChequeInfoById(
				customerChequeInfo.getId(),customerChequeInfo.getChequeSeq(),"");
		
		CustomerChequeInfo oldCustomerChequeInfo= customerChequeInfo.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerChequeInfo.getLovDescCustCIF());
		valueParm[1] =String.valueOf(customerChequeInfo.getChequeSeq());

        errParm[0] = PennantJavaUtil.getLabel("CustomerChequeInfo") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
        errParm[1] = PennantJavaUtil.getLabel("label_ChequeSeq") + "-" + valueParm[1];

		if (customerChequeInfo.isNew()){ // for New record or new record into work flow

			if (!customerChequeInfo.isWorkflow()){// With out Work flow only new records  
				if (befCustomerChequeInfo !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",
							errParm,null));
				}	
			}else{ // with work flow

				if (customerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerChequeInfo !=null || tempCustomerChequeInfo!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerChequeInfo ==null || tempCustomerChequeInfo!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerChequeInfo.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerChequeInfo ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerChequeInfo!=null && !oldCustomerChequeInfo.getLastMntOn().equals(
							befCustomerChequeInfo.getLastMntOn())){
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

				if (tempCustomerChequeInfo==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerChequeInfo!=null  && oldCustomerChequeInfo!=null && !oldCustomerChequeInfo.getLastMntOn().equals(tempCustomerChequeInfo.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetail(screenValidations(customerChequeInfo));
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerChequeInfo.isWorkflow()){
			customerChequeInfo.setBefImage(befCustomerChequeInfo);	
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
	public ErrorDetail  screenValidations(CustomerChequeInfo customerChequeInfo){

		return null;
	}	
}
