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
import com.pennanttech.pennapps.core.resource.Literal;

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
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		JointAccountDetail jad = (JointAccountDetail) auditDetail.getModelData();
		JointAccountDetail tempJad = null;
		String custCIF = jad.getCustCIF();

		if (jad.isWorkflow()) {
			tempJad = getJointAccountDetailDAO().getJointAccountDetailByRef(jad.getFinID(), custCIF, "_Temp");
		}

		JointAccountDetail befJad = getJointAccountDetailDAO().getJointAccountDetailByRef(jad.getFinID(), custCIF, "");

		JointAccountDetail oldJad = jad.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = jad.getFinReference();
		valueParm[1] = custCIF;
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];

		if (jad.isNewRecord()) { // for New record or new record into work flow

			if (!jad.isWorkflow()) {// With out Work flow only new records
				if (befJad != null && !StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jad.getRecordType())) { // Record
																													// Already
																													// Exists
																													// in
																													// the
																													// table
																													// then
																													// error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jad.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																					// new
					if (befJad != null || tempJad != null) { // if records already exists
																// in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJad == null || tempJad != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jad.isWorkflow()) { // With out Work flow for update and delete

				if (befJad == null) { // if records not exists in the main table
					/*
					 * auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails( PennantConstants.KEY_FIELD,
					 * "41002", errParm, valueParm), usrLanguage));
					 */
				} else {
					if (oldJad != null && oldJad.getLastMntOn() != null
							&& (!oldJad.getLastMntOn().equals(befJad.getLastMntOn()))) {
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

				if (tempJad == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempJad != null && oldJad != null && !oldJad.getLastMntOn().equals(tempJad.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// If Joint Account is already utilized in GstDetails
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jad.getRecordType())) {
			boolean coApplicantExists = getFinanceTaxDetailDAO().isReferenceExists(jad.getFinID(), custCIF);
			if (coApplicantExists) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65025", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jad.isWorkflow()) {
			auditDetail.setBefImage(befJad);
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
