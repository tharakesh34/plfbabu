package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerCardSalesValidation {

	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;

	public CustomerCardSalesValidation(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

	public AuditHeader cardSaleInfoValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> cardSaleInfoListValidation(List<AuditDetail> auditDetails, String method,
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

		CustCardSales customerCardSalesInfo = (CustCardSales) auditDetail.getModelData();
		CustCardSales tempCustomerBankInfo = null;
		if (customerCardSalesInfo.isWorkflow()) {
			tempCustomerBankInfo = getCustomerCardSalesInfoDAO().getCustomerCardSalesInfoByCustId(customerCardSalesInfo,
					"_Temp");
		}

		CustCardSales befCustomerBankInfo = getCustomerCardSalesInfoDAO()
				.getCustomerCardSalesInfoByCustId(customerCardSalesInfo, "");

		CustCardSales oldCustomerBankInfo = customerCardSalesInfo.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerCardSalesInfo.getLovDescCustCIF());
		valueParm[1] = customerCardSalesInfo.getMerchantId();

		errParm[0] = PennantJavaUtil.getLabel("CustomerCardSaleInfo") + " , "
				+ PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_CustomerCardSalesInfoDialog_MerchantId.label") + "-"
				+ valueParm[1];

		if (customerCardSalesInfo.isNewRecord()) { // for New record or new record into work flow

			if (!customerCardSalesInfo.isWorkflow()) {// With out Work flow only new records
				if (befCustomerBankInfo != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (customerCardSalesInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																										// is new
					if (befCustomerBankInfo != null || tempCustomerBankInfo != null) { // if records already exists in
																						// the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerBankInfo == null || tempCustomerBankInfo != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!customerCardSalesInfo.isWorkflow()) { // With out Work flow for update and delete

				if (befCustomerBankInfo == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCustomerBankInfo != null
							&& !oldCustomerBankInfo.getLastMntOn().equals(befCustomerBankInfo.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempCustomerBankInfo == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerBankInfo != null && oldCustomerBankInfo != null
						&& !oldCustomerBankInfo.getLastMntOn().equals(tempCustomerBankInfo.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

			}
		}
		auditDetail.setErrorDetail(screenValidations(customerCardSalesInfo));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerCardSalesInfo.isWorkflow()) {
			customerCardSalesInfo.setBefImage(befCustomerBankInfo);
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
	public ErrorDetail screenValidations(CustCardSales customerCardSalesInfo) {

		return null;
	}

	public CustomerCardSalesInfoDAO getCustomerCardSalesInfoDAO() {
		return customerCardSalesInfoDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}
}
