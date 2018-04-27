package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.document.DocumentCategories;

public class DocumentDetailValidation {

	private DocumentDetailsDAO	documentDetailsDAO;
	private DocumentManagerDAO	documentManagerDAO;
	private CustomerDocumentDAO	customerDocumentDAO;

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}
	
	public DocumentDetailValidation(DocumentDetailsDAO	documentDetailsDAO, 
			DocumentManagerDAO	documentManagerDAO,CustomerDocumentDAO	customerDocumentDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
		this.documentManagerDAO = documentManagerDAO;
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				if(auditDetail != null){
					details.add(auditDetail);
				}
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		DocumentDetails documentDetails = (DocumentDetails) auditDetail.getModelData();
		
		// Validate Customer  Document seperatly.
		if(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())){
			return null;
		}
		DocumentDetails tempDocument = null;
		if (documentDetails.isWorkflow()) {
			tempDocument = getDocumentDetailsDAO().getDocumentDetailsById(documentDetails.getDocId(), "_Temp", true);
		}

		DocumentDetails befDocument = getDocumentDetailsDAO().getDocumentDetailsById(documentDetails.getDocId(),"", true);
		DocumentDetails oldDocument = documentDetails.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = documentDetails.getReferenceId();
		valueParm[1] = documentDetails.getDocCategory();
		
		if (StringUtils.equals(documentDetails.getDocModule(), CollateralConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		} else if (StringUtils.equals(documentDetails.getDocModule(),  FinanceConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_FinanceReference") + ":" + valueParm[0];
		} else if (StringUtils.equals(documentDetails.getDocModule(),  CommitmentConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + ":" + valueParm[0];
		} else if (StringUtils.equals(documentDetails.getDocModule(), VASConsatnts.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_VASReference") + ":" + valueParm[0];
		}
		errParm[1] = PennantJavaUtil.getLabel("label_DocumnetCategory") + ":" + valueParm[1];

		if (documentDetails.isNew()) { // for New record or new record into work flow

			if (!documentDetails.isWorkflow()) {// With out Work flow only new records  
				if (befDocument != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (documentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befDocument != null || tempDocument != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befDocument == null || tempDocument != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!documentDetails.isWorkflow()) { // With out Work flow for update and delete

				if (befDocument == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldDocument != null
							&& !oldDocument.getLastMntOn().equals(befDocument.getLastMntOn())) {
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

				if (tempDocument == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempDocument != null && oldDocument != null
						&& !oldDocument.getLastMntOn().equals(tempDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !documentDetails.isWorkflow()) {
			documentDetails.setBefImage(befDocument);
		}
		return auditDetail;
	}

}
