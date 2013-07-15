package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerBalanceSheetDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerBalanceSheetValidation {

	private CustomerBalanceSheetDAO customerBalanceSheetDAO;
	
	
	public CustomerBalanceSheetValidation(CustomerBalanceSheetDAO customerBalanceSheetDAO) {
		this.customerBalanceSheetDAO = customerBalanceSheetDAO;
	}
	
	/**
	 * @return the customerBalanceSheetDAO
	 */
	public CustomerBalanceSheetDAO getCustomerBalanceSheetDAO() {
		return customerBalanceSheetDAO;
	}
	

	public AuditHeader balanceSheetValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, 
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> balanceSheetListValidation(List<AuditDetail> auditDetails,
			String method,String  usrLanguage){
		
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

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CustomerBalanceSheet customerBalanceSheet= (CustomerBalanceSheet) auditDetail.getModelData();
		
		CustomerBalanceSheet tempCustomerBalanceSheet= null;
		if (customerBalanceSheet.isWorkflow()){
			tempCustomerBalanceSheet = getCustomerBalanceSheetDAO().getCustomerBalanceSheetById(
					customerBalanceSheet.getId(),customerBalanceSheet.getCustId(), "_Temp");
		}
		CustomerBalanceSheet befCustomerBalanceSheet= getCustomerBalanceSheetDAO().getCustomerBalanceSheetById(
				customerBalanceSheet.getId(),customerBalanceSheet.getCustId(), "");
		
		CustomerBalanceSheet old_CustomerBalanceSheet= customerBalanceSheet.getBefImage();
		
		String[] errParm= new String[2];
		String[] valueParm= new String[2];
		
		valueParm[0]=customerBalanceSheet.getId();
		valueParm[1]=String.valueOf(customerBalanceSheet.getCustId());
		
		errParm[0]=PennantJavaUtil.getLabel("label_FinancialYear")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_CustId")+":"+valueParm[1];
		
		if (customerBalanceSheet.isNew()){ // for New record or new record into work flow
			
			if (!customerBalanceSheet.isWorkflow()){// With out Work flow only new records  
				if (befCustomerBalanceSheet !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", 
									errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (customerBalanceSheet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCustomerBalanceSheet !=null || tempCustomerBalanceSheet!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", 
										errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCustomerBalanceSheet ==null || tempCustomerBalanceSheet!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
										errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerBalanceSheet.isWorkflow()){	// With out Work flow for update and delete
			
				if (befCustomerBalanceSheet ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", 
									errParm,valueParm), usrLanguage));
				}else{
					if (old_CustomerBalanceSheet!=null && !old_CustomerBalanceSheet.getLastMntOn().equals(
							befCustomerBalanceSheet.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", 
											errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", 
											errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{
			
				if (tempCustomerBalanceSheet==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
				
				if (old_CustomerBalanceSheet!=null && !old_CustomerBalanceSheet.getLastMntOn().equals(
						tempCustomerBalanceSheet.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if(StringUtils.trimToEmpty(method).equals("doApprove") || !customerBalanceSheet.isWorkflow()){
			customerBalanceSheet.setBefImage(befCustomerBalanceSheet);	
		}

		return auditDetail;
	}
	
}
