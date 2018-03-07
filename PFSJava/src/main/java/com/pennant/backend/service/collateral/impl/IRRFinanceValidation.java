package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class IRRFinanceValidation {
	
	private IRRFinanceTypeDAO 	irrFinanceTypeDAO;
	
	public IRRFinanceTypeDAO getIRRFinanceTypeDAO() {
		return irrFinanceTypeDAO;
	}

	public IRRFinanceValidation(IRRFinanceTypeDAO irrFinanceTypeDAO) {
		this.irrFinanceTypeDAO = irrFinanceTypeDAO;
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

		IRRFinanceType irrFinanceType = (IRRFinanceType) auditDetail.getModelData();
		IRRFinanceType tempirrFinanceTypeDetail = null;
		if (irrFinanceType.isWorkflow()) {
			tempirrFinanceTypeDetail = getIRRFinanceTypeDAO().getIRRFinanceType(
					irrFinanceType.getIRRID(),irrFinanceType.getFinType(), "_Temp");
		}

		IRRFinanceType befirrFinanceTypeDetail =getIRRFinanceTypeDAO().getIRRFinanceType(
				irrFinanceType.getIRRID(),irrFinanceType.getFinType(), "");
		IRRFinanceType oldirrFinanceTypeDetail = irrFinanceType.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = String.valueOf(irrFinanceType.getIRRID());
		valueParm[1] = irrFinanceType.getFinType();
			errParm[0] = PennantJavaUtil.getLabel("label_IRRID") + ":" + valueParm[0];
			errParm[1] = PennantJavaUtil.getLabel("label_FeeTypeCode") + ":" + valueParm[1];

		if (irrFinanceType.isNew()) { // for New record or new record into work flow

			if (!irrFinanceType.isWorkflow()) {// With out Work flow only new records  
				if (befirrFinanceTypeDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (irrFinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befirrFinanceTypeDetail != null || tempirrFinanceTypeDetail != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befirrFinanceTypeDetail == null || tempirrFinanceTypeDetail != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!irrFinanceType.isWorkflow()) { // With out Work flow for update and delete

				if (befirrFinanceTypeDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldirrFinanceTypeDetail != null
							&& !oldirrFinanceTypeDetail.getLastMntOn().equals(befirrFinanceTypeDetail.getLastMntOn())) {
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

				if (tempirrFinanceTypeDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempirrFinanceTypeDetail != null && oldirrFinanceTypeDetail != null
						&& !oldirrFinanceTypeDetail.getLastMntOn().equals(tempirrFinanceTypeDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !irrFinanceType.isWorkflow()) {
			irrFinanceType.setBefImage(befirrFinanceTypeDetail);
		}
		return auditDetail;
	}


}
