/**
 * 
 */
package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * @author S051
 *
 */
public class FinJointAccountDetailValidation {

	private static final Logger logger = LogManager.getLogger(FinJointAccountDetailValidation.class);
	private JointAccountDetailDAO jointAccountDetailDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;

	public FinJointAccountDetailValidation(JointAccountDetailDAO jointAccountDetailDAO,
			FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public AuditHeader jointAccountDetailsValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> jointAccountDetailsListValidation(List<AuditDetail> auditDetails, String method,
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
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		JointAccountDetail jointAccountDetail = (JointAccountDetail) auditDetail.getModelData();
		JointAccountDetail tempJointAccountDetail = null;
		if (jointAccountDetail.isWorkflow()) {
			tempJointAccountDetail = getJointAccountDetailDAO().getJointAccountDetailByRef(
					jointAccountDetail.getFinReference(), jointAccountDetail.getCustCIF(), "_Temp");
		}
		JointAccountDetail befJointAccountDetail = getJointAccountDetailDAO()
				.getJointAccountDetailByRef(jointAccountDetail.getFinReference(), jointAccountDetail.getCustCIF(), "");

		JointAccountDetail oldJointAccountDetail = jointAccountDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = jointAccountDetail.getFinReference();
		valueParm[1] = jointAccountDetail.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];

		if (jointAccountDetail.isNew()) { // for New record or new record into work flow

			if (!jointAccountDetail.isWorkflow()) {// With out Work flow only new records  
				if (befJointAccountDetail != null
						&& !StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jointAccountDetail.getRecordType())) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befJointAccountDetail != null || tempJointAccountDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJointAccountDetail == null || tempJointAccountDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jointAccountDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befJointAccountDetail == null) { // if records not exists in the main table
					/*
					 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD,
					 * "41002", errParm, valueParm), usrLanguage));
					 */
				} else {
					if (oldJointAccountDetail != null && oldJointAccountDetail.getLastMntOn() != null
							&& (!oldJointAccountDetail.getLastMntOn().equals(befJointAccountDetail.getLastMntOn()))) {
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

				if (tempJointAccountDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempJointAccountDetail != null && oldJointAccountDetail != null
						&& !oldJointAccountDetail.getLastMntOn().equals(tempJointAccountDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// If Joint Account is already utilized in GstDetails
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jointAccountDetail.getRecordType())) {
			boolean coApplicantExists = getFinanceTaxDetailDAO().isReferenceExists(jointAccountDetail.getFinReference(),
					jointAccountDetail.getCustCIF());
			if (coApplicantExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65025", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jointAccountDetail.isWorkflow()) {
			auditDetail.setBefImage(befJointAccountDetail);
		}
		return auditDetail;
	}

	public JointAccountDetailDAO getJointAccountDetailDAO() {
		return jointAccountDetailDAO;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

}
