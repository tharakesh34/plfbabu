/**
 * 
 */
package com.pennant.backend.service.finance.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * @author S051
 *
 */
public class FinJointAccountDetailValidation {

	private static final Logger logger = Logger.getLogger(FinJointAccountDetailValidation.class);
	private JountAccountDetailDAO jountAccountDetailDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;

	public FinJointAccountDetailValidation(JountAccountDetailDAO jountAccountDetailDAO,FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}


	public AuditHeader jointAccountDetailsValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method,
		        auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> jointAccountDetailsListValidation(List<AuditDetail> auditDetails,
	        String method, String usrLanguage) {

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
		JointAccountDetail jountAccountDetail = (JointAccountDetail) auditDetail.getModelData();
		JointAccountDetail tempJountAccountDetail = null;
		if (jountAccountDetail.isWorkflow()) {
			tempJountAccountDetail = getJountAccountDetailDAO().getJountAccountDetailByRefId(
			        jountAccountDetail.getFinReference(), jountAccountDetail.getJointAccountId(), "_Temp");
		}
		JointAccountDetail befJountAccountDetail = getJountAccountDetailDAO().getJountAccountDetailByRefId(
		        jountAccountDetail.getFinReference(), jountAccountDetail.getJointAccountId(), "");

		JointAccountDetail oldJountAccountDetail = jountAccountDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = jountAccountDetail.getFinReference();
		valueParm[1] = jountAccountDetail.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];

		if (jountAccountDetail.isNew()) { // for New record or new record into work flow

			if (!jountAccountDetail.isWorkflow()) {// With out Work flow only new records  
				if (befJountAccountDetail != null && !StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jountAccountDetail.getRecordType()) ) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befJountAccountDetail != null || tempJountAccountDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJountAccountDetail == null || tempJountAccountDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jountAccountDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befJountAccountDetail == null) { // if records not exists in the main table
					/*auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));*/
				} else {
					if (oldJountAccountDetail != null && oldJountAccountDetail.getLastMntOn() != null 
					        && (!oldJountAccountDetail.getLastMntOn().equals(befJountAccountDetail.getLastMntOn()))) {
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

				if (tempJountAccountDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempJountAccountDetail != null
				        && oldJountAccountDetail != null
				        && !oldJountAccountDetail.getLastMntOn().equals(
				                tempJountAccountDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		
		// If Joint Account is already utilized in GstDetails
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, jountAccountDetail.getRecordType())) {
			boolean coApplicantExists = getFinanceTaxDetailDAO()
					.isReferenceExists(jountAccountDetail.getFinReference(),jountAccountDetail.getCustCIF());
			if (coApplicantExists) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", errParm, valueParm));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jountAccountDetail.isWorkflow()) {
			auditDetail.setBefImage(befJountAccountDetail);
		}
		return auditDetail;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
    	return jountAccountDetailDAO;
    }

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
    	this.jountAccountDetailDAO = jountAccountDetailDAO;
    }

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	

}
