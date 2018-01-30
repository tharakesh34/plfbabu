package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.CoOwnerDetailDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CoOwnerDetailsValidation {
	private CoOwnerDetailDAO	coOwnerDetailDAO;

	public CoOwnerDetailDAO getCoOwnerDetailDAO() {
		return coOwnerDetailDAO;
	}

	public CoOwnerDetailsValidation(CoOwnerDetailDAO coOwnerDetailDAO) {
		this.coOwnerDetailDAO = coOwnerDetailDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage) {

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

		CoOwnerDetail coOwnerDetail = (CoOwnerDetail) auditDetail.getModelData();
		CoOwnerDetail tempCoOwnerDetail = null;
		if (coOwnerDetail.isWorkflow()) {
			tempCoOwnerDetail = getCoOwnerDetailDAO().getCoOwnerDetailByRef(coOwnerDetail.getCollateralRef(), coOwnerDetail.getCoOwnerId(), "_Temp");
		}

		CoOwnerDetail befCoOwnerDetail = getCoOwnerDetailDAO().getCoOwnerDetailByRef(coOwnerDetail.getCollateralRef(), coOwnerDetail.getCoOwnerId(), "");
		CoOwnerDetail oldCoOwnerDetail = coOwnerDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(coOwnerDetail.getCollateralRef());
		valueParm[1] = coOwnerDetail.getCoOwnerCIF();

		errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CoOwnerCIF") + ":" + valueParm[1];

		if (coOwnerDetail.isNew()) { // for New record or new record into work flow

			if (!coOwnerDetail.isWorkflow()) {// With out Work flow only new records  
				if (befCoOwnerDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (coOwnerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCoOwnerDetail != null || tempCoOwnerDetail != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCoOwnerDetail == null || tempCoOwnerDetail != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!coOwnerDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befCoOwnerDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCoOwnerDetail != null
							&& !oldCoOwnerDetail.getLastMntOn().equals(befCoOwnerDetail.getLastMntOn())) {
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

				if (tempCoOwnerDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCoOwnerDetail != null && oldCoOwnerDetail != null
						&& !oldCoOwnerDetail.getLastMntOn().equals(tempCoOwnerDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !coOwnerDetail.isWorkflow()) {
			coOwnerDetail.setBefImage(befCoOwnerDetail);
		}
		return auditDetail;
	}
}
