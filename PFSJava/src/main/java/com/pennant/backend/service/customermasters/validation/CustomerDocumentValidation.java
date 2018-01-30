package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

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
					customerDocument.getId(),customerDocument.getCustDocCategory(),"_Temp");
		}
		
		CustomerDocument befCustomerDocument= getCustomerDocumentDAO().getCustomerDocumentById(
				customerDocument.getId(),customerDocument.getCustDocCategory(),"");
		
		CustomerDocument oldCustomerDocument= customerDocument.getBefImage();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerDocument.getLovDescCustCIF());
		valueParm[1] = customerDocument.getCustDocCategory();

		errParm[0] = PennantJavaUtil.getLabel("DocumentDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
		errParm[1] = PennantJavaUtil.getLabel("CustDocType_label") + "-" + valueParm[1];
		
		if (customerDocument.isNew()) { // for New record or new record into
										// work flow

			if (!customerDocument.isWorkflow()) {// With out Work flow only new
													// records
				if (befCustomerDocument != null) { // Record Already Exists in
													// the table then error
					auditDetail.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow
				
				if (customerDocument.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomerDocument != null || tempCustomerDocument != null) { 
							//if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerDocument == null || tempCustomerDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetail(
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
					auditDetail.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002", errParm, null));
				}

				if (befCustomerDocument != null && oldCustomerDocument != null
						&& !oldCustomerDocument.getLastMntOn().equals(
								befCustomerDocument.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41004", errParm, null));
					}
				}
			} else {

				if (tempCustomerDocument == null) { // if records not exists in
													// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerDocument != null && oldCustomerDocument != null
						&& !oldCustomerDocument.getLastMntOn().equals(
								tempCustomerDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		// Check whether the document id exists for another customer.
		if (StringUtils.isNotEmpty(customerDocument.getCustDocTitle())) {
			if (customerDocumentDAO.isDuplicateTitle(customerDocument.getCustID(),
					customerDocument.getCustDocCategory(), customerDocument.getCustDocTitle())) {
				String[] errParm1 = new String[2];
				String[] valueParm1 = new String[2];
				if (customerDocument.getCustDocCategory().equals(PennantConstants.CPRCODE)) {
					valueParm1[0] = PennantApplicationUtil.formatEIDNumber(customerDocument.getCustDocTitle());
				} else {
					valueParm1[0] = customerDocument.getCustDocTitle();
				}

				errParm1[0] = PennantJavaUtil.getLabel("DocumentDetails") + " , "
						+ customerDocument.getLovDescCustDocCategory() + " "
						+ PennantJavaUtil.getLabel("CustDocTitle_label") + ":" + valueParm1[0];
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41014", errParm1, null));
			}
		}

		if(!StringUtils.equals(customerDocument.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
		auditDetail.setErrorDetail(screenValidations(customerDocument));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerDocument.isWorkflow()) {
			customerDocument.setBefImage(befCustomerDocument);
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
	public ErrorDetail  screenValidations(CustomerDocument customerDocument ){
       //Customer Document Details Validation

		if( customerDocument.isDocIdNumMand() && StringUtils.isBlank(customerDocument.getCustDocTitle())){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("DocumentDetails"),
					Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
					Labels.getLabel("listheader_CustDocType.label"),
					customerDocument.getCustDocType()},
					new String[] {});	
		}
		
		if(StringUtils.isBlank(customerDocument.getCustDocIssuedCountry())){
			PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_COUNTRY");
			customerDocument.setCustDocIssuedCountry(parameter.getSysParmValue().trim());
		}

		if(customerDocument.isLovDescdocExpDateIsMand()  && customerDocument.getCustDocExpDate() == null){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("DocumentDetails")+"  :  ",
					Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
					Labels.getLabel("listheader_CustDocType.label"),
					customerDocument.getCustDocType()},
					new String[] {});	
		}

		if(customerDocument.getCustDocIssuedOn() != null && customerDocument.getCustDocExpDate() != null
				&& !customerDocument.getCustDocExpDate().after(customerDocument.getCustDocIssuedOn())){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30536", 
					new String[] {Labels.getLabel("DocumentDetails"),
					Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
					Labels.getLabel("listheader_CustDocType.label")}, 
					new String[] {});	
		}
		
		if (customerDocument.getCustDocExpDate() != null
				&& !customerDocument.getCustDocExpDate().after(DateUtility.getAppDate())) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30536", new String[] {
					Labels.getLabel("DocumentDetails"),
					Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
					DateUtility.getAppDate(DateFormat.SHORT_DATE) }, new String[] {});
		}

		if (customerDocument.getCustDocIssuedOn() != null
				&& !(customerDocument.getCustDocIssuedOn().after(SysParamUtil.getValueAsDate("APP_DFT_START_DATE")))) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30536", new String[] {
					Labels.getLabel("DocumentDetails"),
					Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
					DateUtility.formatToShortDate(SysParamUtil.getValueAsDate("APP_DFT_START_DATE")) }, new String[] {});
		}
	
		return null;
	}
	
}
