package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinGuarantorDetailValidation {

	private static final Logger logger = Logger.getLogger(FinGuarantorDetailValidation.class);
	private GuarantorDetailDAO guarantorDetailDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;

	public FinGuarantorDetailValidation(GuarantorDetailDAO guarantorDetailDAO,FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public AuditHeader gurantorDetailsValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method,
		        auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> gurantorDetailsListValidation(List<AuditDetail> auditDetails,  String method, String usrLanguage) {

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
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		GuarantorDetail guarantorDetail = (GuarantorDetail) auditDetail.getModelData();
		GuarantorDetail tempGuarantorDetail = null;
		if (guarantorDetail.isWorkflow()) {
			tempGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailByRefId(
			        guarantorDetail.getFinReference(), guarantorDetail.getGuarantorId(), "_Temp");
		}
		GuarantorDetail befGuarantorDetail = getGuarantorDetailDAO().getGuarantorDetailByRefId(
		        guarantorDetail.getFinReference(), guarantorDetail.getGuarantorId(), "");

		GuarantorDetail oldGuarantorDetail = guarantorDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = guarantorDetail.getFinReference();
		valueParm[1] = guarantorDetail.getGuarantorCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_GuarantorCIF") + ":" + valueParm[1];

		if (guarantorDetail.isNew()) { // for New record or new record into work flow

			if (!guarantorDetail.isWorkflow()&&StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, guarantorDetail.getRecordType())) {// With out Work flow only new records  
				if (befGuarantorDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (guarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befGuarantorDetail != null || tempGuarantorDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befGuarantorDetail == null || tempGuarantorDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!guarantorDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befGuarantorDetail == null) { // if records not exists in the main table
					/*auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));*/
				} else {
					if (oldGuarantorDetail != null
					        && !oldGuarantorDetail.getLastMntOn().equals(
					                befGuarantorDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
							        usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
							        PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
							        usrLanguage));
						}
					}
				}
			} else {

				if (tempGuarantorDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempGuarantorDetail != null
				        && oldGuarantorDetail != null
				        && !oldGuarantorDetail.getLastMntOn().equals(
				                tempGuarantorDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		// If Guarantor Account is already utilized in GstDetails
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, guarantorDetail.getRecordType())) {
			boolean guarantorExists = getFinanceTaxDetailDAO()
					.isReferenceExists(guarantorDetail.getFinReference(),guarantorDetail.getGuarantorCIF());
			if (guarantorExists) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "65025", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !guarantorDetail.isWorkflow()) {
			auditDetail.setBefImage(befGuarantorDetail);
		}
		return auditDetail;
	}

	public GuarantorDetailDAO getGuarantorDetailDAO() {
		return guarantorDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

}
