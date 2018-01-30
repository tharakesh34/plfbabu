package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinInsuranceValidation {
	private FinInsurancesDAO finInsurancesDAO;
	
	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}

	public FinInsuranceValidation(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
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

		FinInsurances finInsurance = (FinInsurances) auditDetail.getModelData();
		FinInsurances tempFininsurance = null;
		if (finInsurance.isWorkflow()) {
			tempFininsurance = getFinInsurancesDAO().getFinInsuranceByID(finInsurance,"_Temp",false);
		}

		FinInsurances befFinInsurance = getFinInsurancesDAO().getFinInsuranceByID(finInsurance,"",false);
		FinInsurances oldFinInsurance = finInsurance.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finInsurance.getInsReference();
		valueParm[1] = finInsurance.getInsuranceType();

		errParm[0] = PennantJavaUtil.getLabel("label_FinInsuranceDialog_Reference.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinInsuranceDialog_InsuranceType.value") + ":" + valueParm[1];

		if (finInsurance.isNew()) { // for New record or new record into work flow

			if (!finInsurance.isWorkflow()) {// With out Work flow only new records  
				if (befFinInsurance != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (finInsurance.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinInsurance != null || tempFininsurance != null) { // if records already exists in the main table
						auditDetail
						.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befFinInsurance == null || tempFininsurance != null) {
						auditDetail
						.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finInsurance.isWorkflow()) { // With out Work flow for update and delete

				if (befFinInsurance == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldFinInsurance != null
							&& !oldFinInsurance.getLastMntOn().equals(befFinInsurance.getLastMntOn())) {
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

				if (tempFininsurance == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempFininsurance != null && oldFinInsurance != null
						&& !oldFinInsurance.getLastMntOn().equals(tempFininsurance.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finInsurance.isWorkflow()) {
			finInsurance.setBefImage(befFinInsurance);
		}
		return auditDetail;
	}
}
