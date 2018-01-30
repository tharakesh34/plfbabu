package com.pennant.backend.service.configuration.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class VasRecordingValidation {
	private VASRecordingDAO vasRecordingDAO;

	public VASRecordingDAO getVASRecordingDAO() {
		return vasRecordingDAO;
	}

	public VasRecordingValidation(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method,
			String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validation(auditDetails.get(i), usrLanguage, method);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getVASRecordingDAO().getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	protected AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		VASRecording vASRecording = (VASRecording) auditDetail.getModelData();

		VASRecording tempVASRecording = null;
		if (vASRecording.isWorkflow()) {
			tempVASRecording = getVASRecordingDAO().getVASRecordingByReference(vASRecording.getVasReference(), "_Temp");
		}
		VASRecording befVASRecording = getVASRecordingDAO().getVASRecordingByReference(vASRecording.getVasReference(), "");

		VASRecording oldVasRecording = vASRecording.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = vASRecording.getVasReference();
		errParm[0] = PennantJavaUtil.getLabel("label_VASReference") + ":" + valueParm[0];

		if (vASRecording.isNew()) { // for New record or new record into work flow
			if (!vASRecording.isWorkflow()) {// With out Work flow only new records  
				if (befVASRecording != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (vASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befVASRecording != null || tempVASRecording != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befVASRecording == null || tempVASRecording != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!vASRecording.isWorkflow()) { // With out Work flow for update and delete
				if (befVASRecording == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldVasRecording != null 	&& !oldVasRecording.getLastMntOn().equals(befVASRecording.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm));
						}
					}
				}
			} else {
				if (tempVASRecording == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
				if (tempVASRecording != null && oldVasRecording != null && !oldVasRecording.getLastMntOn().equals(tempVASRecording.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if (StringUtils.trimToEmpty(method).equals("doApprove") || !vASRecording.isWorkflow()) {
			auditDetail.setBefImage(befVASRecording);
		}
		return auditDetail;
	}

}
