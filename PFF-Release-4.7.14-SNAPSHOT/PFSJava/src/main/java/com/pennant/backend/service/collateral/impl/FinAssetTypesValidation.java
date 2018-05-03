package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.FinAssetTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class FinAssetTypesValidation {
	
	private FinAssetTypeDAO finAssetTypeDAO;

	public FinAssetTypeDAO getFinAssetTypeDAO() {
		return finAssetTypeDAO;
	}

	public FinAssetTypesValidation(FinAssetTypeDAO finAssetTypeDAO) {
		this.finAssetTypeDAO = finAssetTypeDAO;
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

		FinAssetTypes finAssetTypes = (FinAssetTypes) auditDetail.getModelData();
		FinAssetTypes tempAssignment = null;
		if (finAssetTypes.isWorkflow()) {
			tempAssignment = getFinAssetTypeDAO().getFinAssetTypesbyID(finAssetTypes,"_Temp");
		}

		FinAssetTypes befAssignment = getFinAssetTypeDAO().getFinAssetTypesbyID(finAssetTypes, "");
		FinAssetTypes oldAssignment = finAssetTypes.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finAssetTypes.getReference();
		valueParm[1] = finAssetTypes.getAssetType();

		errParm[0] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AssetTypeRef") + ":" + valueParm[1];

		if (finAssetTypes.isNew()) { // for New record or new record into work flow

			if (!finAssetTypes.isWorkflow()) {// With out Work flow only new records  
				if (befAssignment != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (finAssetTypes.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befAssignment != null || tempAssignment != null) { // if records already exists in the main table
						auditDetail
						.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befAssignment == null || tempAssignment != null) {
						auditDetail
						.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finAssetTypes.isWorkflow()) { // With out Work flow for update and delete

				if (befAssignment == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldAssignment != null
							&& !oldAssignment.getLastMntOn().equals(befAssignment.getLastMntOn())) {
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

				if (tempAssignment == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempAssignment != null && oldAssignment != null
						&& !oldAssignment.getLastMntOn().equals(tempAssignment.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finAssetTypes.isWorkflow()) {
			finAssetTypes.setBefImage(befAssignment);
		}
		return auditDetail;
	}


}
