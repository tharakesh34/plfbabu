package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.CollateralThirdPartyDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CollateralThirdPartyValidation {

	private CollateralThirdPartyDAO	collateralThirdPartyDAO;

	public CollateralThirdPartyDAO getCollateralThirdPartyDAO() {
		return collateralThirdPartyDAO;
	}

	public CollateralThirdPartyValidation(CollateralThirdPartyDAO	collateralThirdPartyDAO) {
		this.collateralThirdPartyDAO = collateralThirdPartyDAO;
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

		CollateralThirdParty collateralThirdParty = (CollateralThirdParty) auditDetail.getModelData();
		CollateralThirdParty tempCollateralThirdParty = null;
		if (collateralThirdParty.isWorkflow()) {
			tempCollateralThirdParty = getCollateralThirdPartyDAO().getCollThirdPartyDetails(
					collateralThirdParty.getCollateralRef(), collateralThirdParty.getCustomerId(), "_Temp");
		}

		CollateralThirdParty befCollateralThirdParty = getCollateralThirdPartyDAO().getCollThirdPartyDetails(
				collateralThirdParty.getCollateralRef(), collateralThirdParty.getCustomerId(), "");
		CollateralThirdParty oldCollateralThirdParty = collateralThirdParty.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = collateralThirdParty.getCustCIF();
		valueParm[1] = collateralThirdParty.getCollateralRef();

		errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[1];
		errParm[1] = PennantJavaUtil.getLabel("label_CoOwnerCIF") + ":" + valueParm[0];

		if (collateralThirdParty.isNew()) { // for New record or new record into work flow

			if (!collateralThirdParty.isWorkflow()) {// With out Work flow only new records  
				if (befCollateralThirdParty != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (collateralThirdParty.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCollateralThirdParty != null || tempCollateralThirdParty != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCollateralThirdParty == null || tempCollateralThirdParty != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!collateralThirdParty.isWorkflow()) { // With out Work flow for update and delete

				if (befCollateralThirdParty == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCollateralThirdParty != null
							&& !oldCollateralThirdParty.getLastMntOn().equals(befCollateralThirdParty.getLastMntOn())) {
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

				if (tempCollateralThirdParty == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCollateralThirdParty != null && oldCollateralThirdParty != null
						&& !oldCollateralThirdParty.getLastMntOn().equals(tempCollateralThirdParty.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}
		
		if ((StringUtils.equals(collateralThirdParty.getRecordType(), PennantConstants.RECORD_TYPE_DEL) ||
				StringUtils.equals(collateralThirdParty.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) && collateralThirdParty.getCustomerId() > 0) {
			boolean exist = getCollateralThirdPartyDAO().isThirdPartyUsed(collateralThirdParty.getCollateralRef(),
					collateralThirdParty.getCustomerId());

			if (exist) {
				auditDetail.setErrorDetail(new ErrorDetail("90338", null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !collateralThirdParty.isWorkflow()) {
			collateralThirdParty.setBefImage(befCollateralThirdParty);
		}
		return auditDetail;
	}

}
