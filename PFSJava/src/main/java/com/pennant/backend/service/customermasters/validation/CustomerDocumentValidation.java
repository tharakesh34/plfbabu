package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.extension.CustomerExtension;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

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

	public AuditHeader documentValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> documentListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {

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
		CustomerDocument document = (CustomerDocument) auditDetail.getModelData();
		CustomerDocument tempDocument = null;

		Long id = document.getCustID();
		String custDocCategory = document.getCustDocCategory();

		if (document.isWorkflow()) {
			tempDocument = customerDocumentDAO.getCustomerDocumentById(id, custDocCategory, "_Temp");
		}

		CustomerDocument befDocument = customerDocumentDAO.getCustomerDocumentById(id, custDocCategory, "");

		CustomerDocument oldDcoument = document.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(document.getLovDescCustCIF());
		valueParm[1] = custDocCategory;

		errParm[0] = PennantJavaUtil.getLabel("DocumentDetails") + " , " + PennantJavaUtil.getLabel("label_CustCIF")
				+ ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("CustDocType_label") + "-" + valueParm[1];

		if (document.isNewRecord()) {

			if (!document.isWorkflow()) {
				if (befDocument != null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (document.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befDocument != null || tempDocument != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befDocument == null || tempDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			if (!document.isWorkflow()) {
				if (befDocument == null) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				}

				if (befDocument != null && oldDcoument != null
						&& !oldDcoument.getLastMntOn().equals(befDocument.getLastMntOn())) {
					if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
							.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
					} else {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
					}
				}
			} else {

				if (tempDocument == null) { // if records not exists in
											// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempDocument != null && oldDcoument != null
						&& !oldDcoument.getLastMntOn().equals(tempDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		// Check whether the document id exists for another customer.
		if (!CustomerExtension.ALLOW_DUPLICATE_PAN) {
			if (StringUtils.isNotEmpty(document.getCustDocTitle())) {
				String docCategory = custDocCategory;
				String docNumber = document.getCustDocTitle();
				List<String> duplicateCIFs = customerDocumentDAO.getDuplicateDocByTitle(docCategory, docNumber);
				if (!duplicateCIFs.isEmpty()) {
					String[] errParm1 = new String[2];
					if (custDocCategory.equals(PennantConstants.CPRCODE)) {
						docNumber = PennantApplicationUtil.formatEIDNumber(docNumber);
					}
					errParm1[0] = PennantJavaUtil.getLabel("CustDocTitle_label") + ":" + docNumber;
					String cifs = duplicateCIFs.stream().collect(Collectors.joining(","));
					errParm1[1] = PennantJavaUtil.getLabel("Customer_CIF") + ":" + cifs;

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41018", errParm1, null));
				}
			}
		}

		if (!StringUtils.equals(document.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditDetail.setErrorDetail(screenValidations(document));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !document.isWorkflow()) {
			document.setBefImage(befDocument);
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
	public ErrorDetail screenValidations(CustomerDocument customerDocument) {
		// Customer Document Details Validation

		if (customerDocument.isDocIdNumMand() && StringUtils.isBlank(customerDocument.getCustDocTitle())) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30535",
					new String[] { Labels.getLabel("DocumentDetails"),
							Labels.getLabel("label_CustomerDocumentDialog_CustDocTitle.value"),
							Labels.getLabel("listheader_CustDocType.label"), customerDocument.getCustDocType() },
					new String[] {});
		}

		if (StringUtils.isBlank(customerDocument.getCustDocIssuedCountry())) {
			PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_COUNTRY");
			customerDocument.setCustDocIssuedCountry(parameter.getSysParmValue().trim());
		}

		if (customerDocument.isLovDescdocExpDateIsMand() && customerDocument.getCustDocExpDate() == null) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30535",
					new String[] { Labels.getLabel("DocumentDetails") + "  :  ",
							Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
							Labels.getLabel("listheader_CustDocType.label"), customerDocument.getCustDocType() },
					new String[] {});
		}

		if (customerDocument.getCustDocIssuedOn() != null && customerDocument.getCustDocExpDate() != null
				&& !customerDocument.getCustDocExpDate().after(customerDocument.getCustDocIssuedOn())) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30536",
					new String[] { Labels.getLabel("DocumentDetails"),
							Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
							Labels.getLabel("listheader_CustDocType.label") },
					new String[] {});
		}

		if (customerDocument.getCustDocExpDate() != null
				&& !customerDocument.getCustDocExpDate().after(SysParamUtil.getAppDate())) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30536",
					new String[] { Labels.getLabel("DocumentDetails"),
							Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
							SysParamUtil.getAppDate(DateFormat.SHORT_DATE) },
					new String[] {});
		}

		if (customerDocument.getCustDocIssuedOn() != null
				&& !(customerDocument.getCustDocIssuedOn().after(SysParamUtil.getValueAsDate("APP_DFT_START_DATE")))) {
			return new ErrorDetail(PennantConstants.KEY_FIELD, "30536",
					new String[] { Labels.getLabel("DocumentDetails"),
							Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value"),
							DateUtil.formatToShortDate(SysParamUtil.getValueAsDate("APP_DFT_START_DATE")) },
					new String[] {});
		}

		return null;
	}

}
