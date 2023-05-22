package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerPhoneNumberValidation {

	private CustomerPhoneNumberDAO customerPhoneNumberDAO;

	public CustomerPhoneNumberValidation(CustomerPhoneNumberDAO customerPhoneNumberDAO) {
		this.customerPhoneNumberDAO = customerPhoneNumberDAO;
	}

	/**
	 * @return the customerPhoneNumberDAO
	 */
	public CustomerPhoneNumberDAO getCustomerPhoneNumberDAO() {
		return customerPhoneNumberDAO;
	}

	public AuditHeader phoneNumberValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> phoneNumberListValidation(List<AuditDetail> auditDetails, String method,
			String usrLanguage) {

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

		CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) auditDetail.getModelData();
		CustomerPhoneNumber tempCustomerPhoneNumber = null;
		if (customerPhoneNumber.isWorkflow()) {
			tempCustomerPhoneNumber = getCustomerPhoneNumberDAO().getCustomerPhoneNumberByID(
					customerPhoneNumber.getId(), customerPhoneNumber.getPhoneTypeCode(), "_Temp");
		}
		CustomerPhoneNumber befCustomerPhoneNumber = getCustomerPhoneNumberDAO()
				.getCustomerPhoneNumberByID(customerPhoneNumber.getId(), customerPhoneNumber.getPhoneTypeCode(), "");

		CustomerPhoneNumber oldCustomerPhoneNumber = customerPhoneNumber.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerPhoneNumber.getLovDescCustCIF());
		valueParm[1] = customerPhoneNumber.getPhoneTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("PhoneDetails") + " , " + PennantJavaUtil.getLabel("label_CustCIF") + ":"
				+ valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypeCode") + "-" + valueParm[1];

		/*
		 * if (customerPhoneNumber.isNewRecord()) { // for New record or new record into work flow
		 * 
		 * if (!customerPhoneNumber.isWorkflow()) {// With out Work flow only new records if (befCustomerPhoneNumber !=
		 * null) { // Record Already Exists in the table then error auditDetail.setErrorDetail(new
		 * ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null)); } } else { // with work flow
		 * 
		 * if (customerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type // is
		 * new if (befCustomerPhoneNumber != null || tempCustomerPhoneNumber != null) { // if records already // exists
		 * in the main // table auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
		 * null)); } } else { // if records not exists in the Main flow table if (befCustomerPhoneNumber == null ||
		 * tempCustomerPhoneNumber != null) { auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
		 * "41001", errParm, null)); } } } } else { // for work flow process records or (Record to update or Delete with
		 * out work flow) if (!customerPhoneNumber.isWorkflow()) { // With out Work flow for update and delete
		 * 
		 * if (befCustomerPhoneNumber == null) { // if records not exists in the main table
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null)); } else {
		 * 
		 * if (oldCustomerPhoneNumber != null &&
		 * !oldCustomerPhoneNumber.getLastMntOn().equals(befCustomerPhoneNumber.getLastMntOn())) { if
		 * (StringUtils.trimToEmpty(auditDetail.getAuditTranType()) .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
		 * auditDetail.setErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null)); } else {
		 * auditDetail.setErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null)); } } }
		 * 
		 * } else {
		 * 
		 * if (tempCustomerPhoneNumber == null) { // if records not exists in the Work flow table
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null)); }
		 * 
		 * if (tempCustomerPhoneNumber != null && oldCustomerPhoneNumber != null &&
		 * !oldCustomerPhoneNumber.getLastMntOn().equals(tempCustomerPhoneNumber.getLastMntOn())) {
		 * auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null)); }
		 * 
		 * } }
		 */

		auditDetail.setErrorDetail(screenValidations(customerPhoneNumber));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerPhoneNumber.isWorkflow()) {
			customerPhoneNumber.setBefImage(befCustomerPhoneNumber);
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
	public ErrorDetail screenValidations(CustomerPhoneNumber customerPhoneNumber) {

		if (StringUtils.isEmpty(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneNumber()))) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30535",
					new String[] { Labels.getLabel("PhoneDetails"),
							Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"),
							Labels.getLabel("listheader_PhoneTypeCode.label"), customerPhoneNumber.getPhoneTypeCode() },
					new String[] {});
		}

		return null;
	}
}
