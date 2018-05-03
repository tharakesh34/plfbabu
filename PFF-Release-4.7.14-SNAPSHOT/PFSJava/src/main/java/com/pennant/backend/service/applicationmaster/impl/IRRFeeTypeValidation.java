package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class IRRFeeTypeValidation {

	private IRRFeeTypeDAO					iRRFeeTypeDAO;
	
	public IRRFeeTypeValidation(IRRFeeTypeDAO	iRRFeeTypeDAO) {
		this.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method,
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

		IRRFeeType irrFeeType = (IRRFeeType) auditDetail.getModelData();
		IRRFeeType tempIRRFeeType = null;
		if (irrFeeType.isWorkflow()) {
			tempIRRFeeType = getiRRFeeTypeDAO().getIRRFeeType(irrFeeType.getFeeTypeID(), "_Temp");
		}

		IRRFeeType befIRRFeeType = getiRRFeeTypeDAO().getIRRFeeType(irrFeeType.getFeeTypeID(), "");
		IRRFeeType oldIRRFeeType = irrFeeType.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[1] = String.valueOf(irrFeeType.getIRRID());
		valueParm[0] = irrFeeType.getFeeTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("label_FeeTypeID") + ":" + valueParm[1];
		errParm[1] = PennantJavaUtil.getLabel("label_FeeTypeCode") + ":" + valueParm[0];

		if (irrFeeType.isNew()) { // for New record or new record into work flow

			if (!irrFeeType.isWorkflow()) {// With out Work flow only new records  
				if (befIRRFeeType != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (irrFeeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befIRRFeeType != null || tempIRRFeeType != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befIRRFeeType == null || tempIRRFeeType != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!irrFeeType.isWorkflow()) { // With out Work flow for update and delete

				if (befIRRFeeType == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldIRRFeeType != null
							&& !oldIRRFeeType.getLastMntOn().equals(befIRRFeeType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}
					}
				}
			} else {

				if (tempIRRFeeType == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempIRRFeeType != null && oldIRRFeeType != null
						&& !oldIRRFeeType.getLastMntOn().equals(tempIRRFeeType.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !irrFeeType.isWorkflow()) {
			irrFeeType.setBefImage(befIRRFeeType);
		}
		return auditDetail;
	}

	public IRRFeeTypeDAO getiRRFeeTypeDAO() {
		return iRRFeeTypeDAO;
	}

	public void setiRRFeeTypeDAO(IRRFeeTypeDAO iRRFeeTypeDAO) {
		this.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

}
