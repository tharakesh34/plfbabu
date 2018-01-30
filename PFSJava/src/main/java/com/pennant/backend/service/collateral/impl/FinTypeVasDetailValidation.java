package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class FinTypeVasDetailValidation {
	
	private FinTypeVASProductsDAO finTypeVASProductsDAO;

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public FinTypeVasDetailValidation(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
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

		FinTypeVASProducts finTypeVASProducts = (FinTypeVASProducts) auditDetail.getModelData();
		FinTypeVASProducts tempfinTypeVASProductsDetail = null;
		if (finTypeVASProducts.isWorkflow()) {
			tempfinTypeVASProductsDetail = getFinTypeVASProductsDAO().getFinTypeVASProducts(
					finTypeVASProducts.getFinType(), finTypeVASProducts.getVasProduct(), "_Temp");
		}

		FinTypeVASProducts beffinTypeVASProductsDetail =getFinTypeVASProductsDAO().getFinTypeVASProducts(
				finTypeVASProducts.getFinType(), finTypeVASProducts.getVasProduct(), "");
		FinTypeVASProducts oldfinTypeVASProductsDetail = finTypeVASProducts.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = finTypeVASProducts.getFinType();
		valueParm[1] = finTypeVASProducts.getVasProduct();
			errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];
			errParm[1] = PennantJavaUtil.getLabel("label_FinanceTypeVASProducts") + ":" + valueParm[1];

		if (finTypeVASProducts.isNew()) { // for New record or new record into work flow

			if (!finTypeVASProducts.isWorkflow()) {// With out Work flow only new records  
				if (beffinTypeVASProductsDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (finTypeVASProducts.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (beffinTypeVASProductsDetail != null || tempfinTypeVASProductsDetail != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (beffinTypeVASProductsDetail == null || tempfinTypeVASProductsDetail != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finTypeVASProducts.isWorkflow()) { // With out Work flow for update and delete

				if (beffinTypeVASProductsDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldfinTypeVASProductsDetail != null
							&& !oldfinTypeVASProductsDetail.getLastMntOn().equals(beffinTypeVASProductsDetail.getLastMntOn())) {
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

				if (tempfinTypeVASProductsDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempfinTypeVASProductsDetail != null && oldfinTypeVASProductsDetail != null
						&& !oldfinTypeVASProductsDetail.getLastMntOn().equals(tempfinTypeVASProductsDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finTypeVASProducts.isWorkflow()) {
			finTypeVASProducts.setBefImage(beffinTypeVASProductsDetail);
		}
		return auditDetail;
	}


}
