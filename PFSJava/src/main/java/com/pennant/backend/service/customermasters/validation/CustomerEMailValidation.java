package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerEMailValidation {

	private CustomerEMailDAO customerEMailDAO;

	public CustomerEMailValidation(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}

	/**
	 * @return the customerRatingDAO
	 */
	public CustomerEMailDAO getCustomerEMailDAO() {
		return customerEMailDAO;
	}

	public AuditHeader emailValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> emailListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		CustomerEMail customerEMail = (CustomerEMail) auditDetail.getModelData();
		CustomerEMail tempCustomerEMail = null;
		if (customerEMail.isWorkflow()) {
			tempCustomerEMail = getCustomerEMailDAO().getCustomerEMailById(customerEMail.getId(),
					customerEMail.getCustEMailTypeCode(), "_Temp");
		}

		CustomerEMail befCustomerEMail = getCustomerEMailDAO().getCustomerEMailById(customerEMail.getId(),
				customerEMail.getCustEMailTypeCode(), "");

		CustomerEMail oldCustomerEMail = customerEMail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerEMail.getLovDescCustCIF());
		valueParm[1] = customerEMail.getCustEMailTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("EmailDetails") + " , " + PennantJavaUtil.getLabel("label_CustCIF") + ":"
				+ valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustEMailTypeCode") + "-" + valueParm[1];

		/*
		 * if (customerEMail.isNewRecord()) { // for New record or new record into work flow
		 * 
		 * if (!customerEMail.isWorkflow()) {// With out Work flow only new records if (befCustomerEMail != null) { //
		 * Record Already Exists in the table then error auditDetail.setErrorDetail(new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null)); } } else { // with work flow
		 * 
		 * if (customerEMail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new if
		 * (befCustomerEMail != null || tempCustomerEMail != null) { // if records already exists in the // main table
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null)); } } else {
		 * // if records not exists in the Main flow table if (befCustomerEMail == null || tempCustomerEMail != null) {
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null)); } } } } else
		 * { // for work flow process records or (Record to update or Delete with out work flow) if
		 * (!customerEMail.isWorkflow()) { // With out Work flow for update and delete
		 * 
		 * if (befCustomerEMail == null) { // if records not exists in the main table auditDetail.setErrorDetail(new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null)); } else {
		 * 
		 * if (oldCustomerEMail != null && !oldCustomerEMail.getLastMntOn().equals(befCustomerEMail.getLastMntOn())) {
		 * if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()) .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
		 * auditDetail.setErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null)); } else {
		 * auditDetail.setErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null)); } } } }
		 * else {
		 * 
		 * if (tempCustomerEMail == null) { // if records not exists in the Work flow table
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null)); }
		 * 
		 * if (tempCustomerEMail != null && oldCustomerEMail != null &&
		 * !oldCustomerEMail.getLastMntOn().equals(tempCustomerEMail.getLastMntOn())) { auditDetail.setErrorDetail(new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null)); }
		 * 
		 * } }
		 */

		auditDetail.setErrorDetail(screenValidations(customerEMail));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerEMail.isWorkflow()) {
			customerEMail.setBefImage(befCustomerEMail);
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
	public ErrorDetail screenValidations(CustomerEMail customerEMail) {

		if (StringUtils.isBlank(customerEMail.getCustEMail())) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30535", new String[] { Labels.getLabel("EmailDetails"),
					Labels.getLabel("label_CustomerEMailDialog_CustEMail.value"),
					Labels.getLabel("listheader_CustEMailTypeCode.label"), customerEMail.getCustEMailTypeCode() },
					new String[] {});
		}

		return null;
	}
}
