package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;

public class ExtendedFieldDetailsValidation {

	private ExtendedFieldRenderDAO	extendedFieldRenderDAO;
	
	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public ExtendedFieldDetailsValidation(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage, String tableName) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage, tableName);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	public AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage, String tableName) {

		ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
		ExtendedFieldRender tempExtendedFieldRender = null;
		if (extendedFieldRender.isWorkflow()) {
			tempExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),tableName, "_Temp");
		}

		ExtendedFieldRender befExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),tableName, "");
		ExtendedFieldRender oldExtendedFieldRender = extendedFieldRender.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = extendedFieldRender.getReference();
		valueParm[1] = String.valueOf(extendedFieldRender.getSeqNo());

		errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		if (StringUtils.startsWith(tableName, CollateralConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, AssetConstants.EXTENDEDFIELDS_MODULE)) {
			errParm[0] = PennantJavaUtil.getLabel("label_AssetType") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, VASConsatnts.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_VASReference") + ":" + valueParm[0];
		}
		errParm[1] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[1];

		if (extendedFieldRender.isNew()) { // for New record or new record into work flow

			if (!extendedFieldRender.isWorkflow()) {// With out Work flow only new records  
				if (befExtendedFieldRender != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (extendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtendedFieldRender != null || tempExtendedFieldRender != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldRender == null || tempExtendedFieldRender != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldRender.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldRender == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldExtendedFieldRender != null
							&& !oldExtendedFieldRender.getLastMntOn().equals(befExtendedFieldRender.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}
					}
				}
			} else {

				if (tempExtendedFieldRender == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempExtendedFieldRender != null && oldExtendedFieldRender != null
						&& !oldExtendedFieldRender.getLastMntOn().equals(tempExtendedFieldRender.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldRender.isWorkflow()) {
			extendedFieldRender.setBefImage(befExtendedFieldRender);
		}
		return auditDetail;
	}

}
