package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinGuarantorDetailValidation {
	private static final Logger logger = LogManager.getLogger(FinGuarantorDetailValidation.class);

	private GuarantorDetailDAO guarantorDetailDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;

	public FinGuarantorDetailValidation(GuarantorDetailDAO guarantorDetailDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public AuditHeader gurantorDetailsValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		return auditHeader;
	}

	public List<AuditDetail> gurantorDetailsListValidation(List<AuditDetail> auditDetails, String method,
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

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		GuarantorDetail gd = (GuarantorDetail) auditDetail.getModelData();
		GuarantorDetail tempGd = null;

		long finID = gd.getFinID();
		String finReference = gd.getFinReference();
		long guarantorId = gd.getGuarantorId();
		String guarantorCIF = gd.getGuarantorCIF();

		if (gd.isWorkflow()) {
			tempGd = guarantorDetailDAO.getGuarantorDetailByRefId(finID, guarantorId, "_Temp");
		}

		GuarantorDetail befGd = guarantorDetailDAO.getGuarantorDetailByRefId(finID, guarantorId, "");

		GuarantorDetail oldGd = gd.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finReference;

		valueParm[1] = guarantorCIF;
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_GuarantorCIF") + ":" + valueParm[1];

		if (gd.isNewRecord()) { // for New record or new record into work flow

			if (!gd.isWorkflow() && StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, gd.getRecordType())) {// With
																												// out
																												// Work
																												// flow
																												// only
																												// new
																												// records
				if (befGd != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (gd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																					// new
					if (befGd != null || tempGd != null) { // if records already exists in the
															// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befGd == null || tempGd != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!gd.isWorkflow()) { // With out Work flow for update and delete

				if (befGd == null) { // if records not exists in the main table
					/*
					 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD,
					 * "41002", errParm, valueParm), usrLanguage));
					 */
				} else {
					if (oldGd != null && !oldGd.getLastMntOn().equals(befGd.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempGd == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempGd != null && oldGd != null && !oldGd.getLastMntOn().equals(tempGd.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		// If Guarantor Account is already utilized in GstDetails
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, gd.getRecordType())) {
			boolean guarantorExists = financeTaxDetailDAO.isReferenceExists(finID, guarantorCIF);
			if (guarantorExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65025", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !gd.isWorkflow()) {
			auditDetail.setBefImage(befGd);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

}
