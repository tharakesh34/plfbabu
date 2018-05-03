package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class FlagDetailValidation {
	private FinFlagDetailsDAO finFlagDetailsDAO;

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public FlagDetailValidation(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
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

		FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetail.getModelData();
		FinFlagsDetail tempfinFlagsDetail = null;
		if (finFlagsDetail.isWorkflow()) {
			tempfinFlagsDetail = getFinFlagDetailsDAO().getFinFlagsByRef(
					finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(), "_Temp");
		}

		FinFlagsDetail beffinFlagsDetail = getFinFlagDetailsDAO().getFinFlagsByRef(
				finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(), "");
		FinFlagsDetail oldfinFlagsDetail = finFlagsDetail.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finFlagsDetail.getReference();
		valueParm[1] = finFlagsDetail.getFlagCode();
		if (StringUtils.equals(finFlagsDetail.getModuleName(), CollateralConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		} else if (StringUtils.equals(finFlagsDetail.getModuleName(),  FinanceConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_FinanceReference") + ":" + valueParm[0];
		} else if (StringUtils.equals(finFlagsDetail.getModuleName(),  CommitmentConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + valueParm[0];
		}
		errParm[1] = PennantJavaUtil.getLabel("label_FinanceFlagCode") + ":" + valueParm[1];

		if (finFlagsDetail.isNew()) { // for New record or new record into work flow

			if (!finFlagsDetail.isWorkflow()) {// With out Work flow only new records  
				if (beffinFlagsDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (finFlagsDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (beffinFlagsDetail != null || tempfinFlagsDetail != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (beffinFlagsDetail == null || tempfinFlagsDetail != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFlagsDetail.isWorkflow()) { // With out Work flow for update and delete

				if (beffinFlagsDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldfinFlagsDetail != null
							&& !oldfinFlagsDetail.getLastMntOn().equals(beffinFlagsDetail.getLastMntOn())) {
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

				if (tempfinFlagsDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempfinFlagsDetail != null && oldfinFlagsDetail != null
						&& !oldfinFlagsDetail.getLastMntOn().equals(tempfinFlagsDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFlagsDetail.isWorkflow()) {
			finFlagsDetail.setBefImage(beffinFlagsDetail);
		}
		return auditDetail;
	}

}
