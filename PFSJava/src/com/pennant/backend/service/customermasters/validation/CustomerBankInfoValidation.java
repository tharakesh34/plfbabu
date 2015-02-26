package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerBankInfoValidation {

	private CustomerBankInfoDAO customerBankInfoDAO;

	public CustomerBankInfoValidation(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	/**
	 * @return the customerRatingDAO
	 */
	public CustomerBankInfoDAO getCustomerBankInfoDAO() {
		return customerBankInfoDAO;
	}

	public AuditHeader bankInfoValidation(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> bankInfoListValidation(List<AuditDetail> auditDetails, String method, String  usrLanguage){

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

		CustomerBankInfo customerBankInfo= (CustomerBankInfo) auditDetail.getModelData();
		CustomerBankInfo tempCustomerBankInfo= null;
		if (customerBankInfo.isWorkflow()){
			tempCustomerBankInfo = getCustomerBankInfoDAO().getCustomerBankInfoById(
					customerBankInfo.getId(),customerBankInfo.getBankName(),"_Temp");
		}

		CustomerBankInfo befCustomerBankInfo= getCustomerBankInfoDAO().getCustomerBankInfoById(
				customerBankInfo.getId(),customerBankInfo.getBankName(),"");
		
		CustomerBankInfo oldCustomerBankInfo= customerBankInfo.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerBankInfo.getLovDescCustCIF());
		valueParm[1] = customerBankInfo.getBankName();

        errParm[0] = PennantJavaUtil.getLabel("CustomerBankInfo") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
        errParm[1] = PennantJavaUtil.getLabel("label_CustBank") + "-" + valueParm[1];

		if (customerBankInfo.isNew()){ // for New record or new record into work flow

			if (!customerBankInfo.isWorkflow()){// With out Work flow only new records  
				if (befCustomerBankInfo !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
							errParm,null));
				}	
			}else{ // with work flow

				if (customerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerBankInfo !=null || tempCustomerBankInfo!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerBankInfo ==null || tempCustomerBankInfo!=null ){
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerBankInfo.isWorkflow()){	// With out Work flow for update and delete

				if (befCustomerBankInfo ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCustomerBankInfo!=null && !oldCustomerBankInfo.getLastMntOn().equals(
							befCustomerBankInfo.getLastMntOn())){
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

				if (tempCustomerBankInfo==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCustomerBankInfo!=null  && oldCustomerBankInfo!=null && !oldCustomerBankInfo.getLastMntOn().equals(tempCustomerBankInfo.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetail(screenValidations(customerBankInfo));
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !customerBankInfo.isWorkflow()){
			customerBankInfo.setBefImage(befCustomerBankInfo);	
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
	public ErrorDetails  screenValidations(CustomerBankInfo customerBankInfo){

		return null;
	}	
}
