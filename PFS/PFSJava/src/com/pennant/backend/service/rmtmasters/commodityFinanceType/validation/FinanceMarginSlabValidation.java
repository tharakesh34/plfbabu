package com.pennant.backend.service.rmtmasters.commodityFinanceType.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.rmtmasters.FinanceMarginSlabDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinanceMarginSlabValidation {

	private FinanceMarginSlabDAO financeMarginSlabDAO;

	public FinanceMarginSlabValidation(FinanceMarginSlabDAO financeMarginSlabDAO) {
		this.financeMarginSlabDAO = financeMarginSlabDAO;
	}

	/**
	 * @return the customerAddresDAO
	 */
	public FinanceMarginSlabDAO getFinanceMarginSlabDAO() {
		return financeMarginSlabDAO;
	}

	public AuditHeader marginSlabValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> marginSlabListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method,	usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return null;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from ErrorControl with Error
	 * ID and language as parameters. if any error/Warnings then assign the to
	 * auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method,
			String usrLanguage) {

		FinanceMarginSlab financeMarginSlab = (FinanceMarginSlab) auditDetail.getModelData();
		FinanceMarginSlab tempFinanceMarginSlab= null;
		if (financeMarginSlab.isWorkflow()){
			tempFinanceMarginSlab = getFinanceMarginSlabDAO().getFinanceMarginSlabById(financeMarginSlab.getId(), "_Temp");
		}
		FinanceMarginSlab befFinanceMarginSlab= getFinanceMarginSlabDAO().getFinanceMarginSlabById(financeMarginSlab.getId(), "");

		FinanceMarginSlab old_FinanceMarginSlab= financeMarginSlab.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		
		valueParm[0]=financeMarginSlab.getId();
		valueParm[1] = String.valueOf(financeMarginSlab.getSlabAmount());
		
		errParm[0]=PennantJavaUtil.getLabel("label_FinType")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_SlabAmount")+":"+valueParm[0]; 

		if (financeMarginSlab.isNew()){ // for New record or new record into work flow

			if (!financeMarginSlab.isWorkflow()){// With out Work flow only new records  
				if (befFinanceMarginSlab !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (financeMarginSlab.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinanceMarginSlab !=null || tempFinanceMarginSlab!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinanceMarginSlab ==null || tempFinanceMarginSlab!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeMarginSlab.isWorkflow()){	// With out Work flow for update and delete

				if (befFinanceMarginSlab ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (old_FinanceMarginSlab!=null && !old_FinanceMarginSlab.getLastMntOn().equals(befFinanceMarginSlab.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinanceMarginSlab==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinanceMarginSlab != null && old_FinanceMarginSlab!=null 
						&& !old_FinanceMarginSlab.getLastMntOn().equals(tempFinanceMarginSlab.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if(StringUtils.trimToEmpty(method).equals("doApprove") || !financeMarginSlab.isWorkflow()){
			financeMarginSlab.setBefImage(befFinanceMarginSlab);	
		}

		return auditDetail;
	}

}
