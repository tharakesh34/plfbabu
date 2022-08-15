package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerGstInfoValidation {
	private CustomerGstDetailDAO customerGstDetailDAO;

	public CustomerGstInfoValidation(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public CustomerGstDetailDAO getCustomerGstDetailDAO() {
		return customerGstDetailDAO;
	}

	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public AuditHeader gstInfoValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> gstInfoListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {

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

		CustomerGST customerGST = (CustomerGST) auditDetail.getModelData();
		CustomerGST tempcustomerGST = null;
		if (customerGST.isWorkflow()) {
			tempcustomerGST = getCustomerGstDetailDAO().getCustomerGSTByGstNumber(customerGST, "_Temp");
		}

		CustomerGST befCustomerGST = getCustomerGstDetailDAO().getCustomerGSTByGstNumber(customerGST, "");
		if (befCustomerGST != null) {
			customerGST.setId(befCustomerGST.getId());
			befCustomerGST = null;
		}
		List<CustomerGSTDetails> customerGSTDetailsdb = getCustomerGstDetailDAO()
				.getCustomerGSTDetailsByCustomer(customerGST.getId(), "");
		CustomerGST oldCustomergstkInfo = customerGST.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerGST.getLovDescCustCIF());
		valueParm[1] = customerGST.getGstNumber();

		errParm[0] = PennantJavaUtil.getLabel("CustomerGST Info") + " , " + PennantJavaUtil.getLabel("label_CustCIF")
				+ ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustomerDialog_GSTNumber.value") + "-" + valueParm[1];

		if (customerGST.isNewRecord()) { // for New record or new record into work
											// flow

			if (customerGST.isWorkflow()) {// With out Work flow only new
											// records
											// with work flow
				if (customerGST.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																							// type
																							// is
																							// new
					if (befCustomerGST != null || tempcustomerGST != null) { // if
																				// records
																				// already
																				// exists
																				// in
																				// the
																				// main
																				// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerGST == null || tempcustomerGST != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerGST.isWorkflow()) { // With out Work flow for update
												// and delete

				if (befCustomerGST == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				}
			} else {

				if (tempcustomerGST == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempcustomerGST != null && oldCustomergstkInfo != null
						&& !oldCustomergstkInfo.getLastMntOn().equals(tempcustomerGST.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}

		for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
			for (CustomerGSTDetails customerGSTDetailsold : customerGSTDetailsdb) {
				if (customerGSTDetailsold.getFrequancy().equalsIgnoreCase(customerGSTDetails.getFrequancy())) {
					errParm[0] = "Frequency";
					valueParm[0] = customerGSTDetails.getFrequancy();

					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					break;
				}
			}
		}

		int count = getCustomerGstDetailDAO().getCustomerGstInfoByCustGstNumber(customerGST.getId(),
				customerGST.getCustId(), customerGST.getGstNumber(), "_View");
		if (count != 0) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));

		}
		auditDetail.setErrorDetail(screenValidations(customerGST));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !((CustomerGST) auditDetail.getModelData()).isWorkflow()) {
			customerGST.setBefImage(befCustomerGST);
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
	public ErrorDetail screenValidations(CustomerGST customerGST) {

		return null;
	}
}
