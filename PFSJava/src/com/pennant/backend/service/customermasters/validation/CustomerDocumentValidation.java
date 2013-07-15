package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerDocumentValidation {

	private CustomerDocumentDAO customerDocumentDAO;
	
	public CustomerDocumentValidation(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}
	
	/**
	 * @return the customerDocumentDAO
	 */
	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}
	
	public AuditHeader documentValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> documentListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
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
		
		CustomerDocument customerDocument= (CustomerDocument) auditDetail.getModelData();
		CustomerDocument tempCustomerDocument= null;
		if (customerDocument.isWorkflow()){
			tempCustomerDocument = getCustomerDocumentDAO().getCustomerDocumentById(
					customerDocument.getId(),customerDocument.getCustDocType(),"_Temp");
		}
		
		CustomerDocument befCustomerDocument= getCustomerDocumentDAO().getCustomerDocumentById(
				customerDocument.getId(),customerDocument.getCustDocType(),"");
		
		CustomerDocument old_CustomerDocument= customerDocument.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(customerDocument.getCustID());
		valueParm[1] = customerDocument.getCustDocType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustDocType") + ":" + valueParm[1];

		if (customerDocument.isNew()) { // for New record or new record into
										// work flow

			if (!customerDocument.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerDocument != null) { // Record Already Exists in
													// the table then error
					auditDetail.setErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				
				if (customerDocument.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomerDocument != null || tempCustomerDocument != null) { 
							//if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerDocument == null || tempCustomerDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerDocument.isWorkflow()) { // With out Work flow for
													// update and delete

				if (befCustomerDocument == null) { // if records not exists in
													// the main table
					auditDetail.setErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41002", errParm, null));
				}

				if (old_CustomerDocument != null
						&& !old_CustomerDocument.getLastMntOn().equals(
								befCustomerDocument.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41004", errParm, null));
					}
				}
			} else {

				if (tempCustomerDocument == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerDocument != null && old_CustomerDocument != null
						&& !old_CustomerDocument.getLastMntOn().equals(
								tempCustomerDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !customerDocument.isWorkflow()) {
			customerDocument.setBefImage(befCustomerDocument);
		}
		return auditDetail;
	}
	
}
