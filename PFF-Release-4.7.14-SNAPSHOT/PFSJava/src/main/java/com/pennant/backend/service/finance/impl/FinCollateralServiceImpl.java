package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinCollateralsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinCollateralService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class FinCollateralServiceImpl extends GenericService<FinanceDetail> implements
        FinCollateralService {

	private static final Logger logger = Logger.getLogger(FinCollateralServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinCollateralsDAO finCollateralsDAO;

	public FinCollateralServiceImpl() {
		super();
	}


	@Override
	public FinCollaterals getFinCollateralsById(String finReference, long id) {
		return getFinCollateralsDAO().getFinCollateralsById(finReference, id, "_View");
	}

	@Override
	public List<FinCollaterals> getFinCollateralsByRef(String financeReference, String type) {
		return getFinCollateralsDAO().getFinCollateralsByFinRef(financeReference, type);
	}
	
	@Override
	public FinCollaterals getApprovedFinCollateralsById(String finReference,long id) {
		return getFinCollateralsDAO().getFinCollateralsById(finReference, id, "_AView");
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinCollaterals> finCollateralList, String tableType,
	        String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (FinCollaterals collateralDetail : finCollateralList) {
			collateralDetail.setWorkflowId(0);

			if (collateralDetail.isNewRecord()) {
				getFinCollateralsDAO().save(collateralDetail, tableType);
			} else {
				getFinCollateralsDAO().update(collateralDetail, tableType);
			}
			String[] fields = PennantJavaUtil.getFieldDetails(collateralDetail,
			        collateralDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0],
			        fields[1], collateralDetail.getBefImage(), collateralDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinCollaterals finCollateral = (FinCollaterals) auditHeader.getAuditDetail().getModelData();
		getFinCollateralsDAO().delete(finCollateral, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
	public List<AuditDetail> delete(List<FinCollaterals> finCollateralList, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	

		if(finCollateralList != null && !finCollateralList.isEmpty()) {
			int auditSeq = 1;
			for (FinCollaterals finCollateral : finCollateralList) {
				getFinCollateralsDAO().delete(finCollateral, tableType);
				fields = PennantJavaUtil.getFieldDetails(finCollateral, finCollateral.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType,auditSeq, fields[0], fields[1], finCollateral.getBefImage(), finCollateral));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		FinCollaterals financeDisbursement = new FinCollaterals();
		BeanUtils.copyProperties((FinanceDisbursement) auditHeader.getAuditDetail().getModelData(),
		        financeDisbursement);

		if (financeDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getFinCollateralsDAO().delete(financeDisbursement, "");

		} else {
			financeDisbursement.setRoleCode("");
			financeDisbursement.setNextRoleCode("");
			financeDisbursement.setTaskId("");
			financeDisbursement.setNextTaskId("");
			financeDisbursement.setWorkflowId(0);
			financeDisbursement.setRecordType("");
			if (financeDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				getFinCollateralsDAO().save(financeDisbursement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				getFinCollateralsDAO().update(financeDisbursement, "");
			}
		}

		getFinCollateralsDAO().delete(financeDisbursement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeDisbursement);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinCollaterals finCollaterals = (FinCollaterals) auditHeader.getAuditDetail()
		        .getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinCollateralsDAO().delete(finCollaterals, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}
	
	@Override
	public List<AuditDetail> doApprove(List<FinCollaterals> finCollateralList, String tableType, String auditTranType, 
			String finSourceId) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (FinCollaterals finCollateral : finCollateralList) {
			FinCollaterals detail = new FinCollaterals();
			BeanUtils.copyProperties(finCollateral, detail);
			
			finCollateral.setRoleCode("");
			finCollateral.setNextRoleCode("");
			finCollateral.setTaskId("");
			finCollateral.setNextTaskId("");
			finCollateral.setWorkflowId(0);
			finCollateral.setRecordType("");
			getFinCollateralsDAO().save(finCollateral, tableType);
			
			if(!StringUtils.equals(finSourceId, PennantConstants.FINSOURCE_ID_API)) {
				getFinCollateralsDAO().delete(finCollateral, "_Temp");
			}
			
			String[] fields = PennantJavaUtil.getFieldDetails(finCollateral, finCollateral.getExcludeFields());
			
			auditDetails.add(new  AuditDetail(PennantConstants.TRAN_WF, auditDetails.size()+1, fields[0], fields[1], detail.getBefImage(), detail));
			auditDetails.add(new  AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], finCollateral.getBefImage(), finCollateral));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> validate(List<FinCollaterals> finCollateralList, long workflowId, String method, String auditTranType, String  usrLanguage){
		return doValidation(finCollateralList, workflowId, method, auditTranType, usrLanguage);
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinCollateralsDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(),
		        auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public List<AuditDetail> doValidation(List<FinCollaterals> finCollateralList, long workflowId, String method, String auditTranType, String usrLanguage) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(finCollateralList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage);
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	
	private List<AuditDetail> getAuditDetail(List<FinCollaterals> finCollateralsList, String auditTranType, String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinCollaterals object = new FinCollaterals();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < finCollateralsList.size(); i++) {

			FinCollaterals finCollateral = finCollateralsList.get(i);
			finCollateral.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (finCollateral.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finCollateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finCollateral.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finCollateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finCollateral.getRecordType()
					.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && isRcdType ) {
				finCollateral.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finCollateral.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finCollateral.getRecordType().equalsIgnoreCase(
						PennantConstants.RECORD_TYPE_DEL)
						|| finCollateral.getRecordType().equalsIgnoreCase(
								PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}


			if (StringUtils.isNotEmpty(finCollateral.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCollateral.getBefImage(), finCollateral));
			}
		}


		logger.debug("Leaving");
		return auditDetails;
	}
	
	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinCollaterals finCollaterals = (FinCollaterals) auditDetail.getModelData();

		FinCollaterals tempFinCollaterals = null;
		if (finCollaterals.isWorkflow()) {
			tempFinCollaterals = getFinCollateralsDAO().getFinCollateralsById(finCollaterals.getFinReference(),
			        finCollaterals.getId(), "_Temp");
		}
		FinCollaterals befFinCollaterals = getFinCollateralsDAO().getFinCollateralsById(finCollaterals.getFinReference(),
		        finCollaterals.getId(), "");

		FinCollaterals oldFinCollaterals = finCollaterals.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finCollaterals.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (finCollaterals.isNew()) { // for New record or new record into work flow

			if (!finCollaterals.isWorkflow()) {// With out Work flow only new records  
				if (befFinCollaterals != null) {	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finCollaterals.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinCollaterals != null || tempFinCollaterals != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						        PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						        usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinCollaterals == null || tempFinCollaterals != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
						        PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						        usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finCollaterals.isWorkflow()) {	// With out Work flow for update and delete

				if (befFinCollaterals == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinCollaterals != null
					        && !oldFinCollaterals.getLastMntOn().equals(
					                befFinCollaterals.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							        PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
							        usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							        PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
							        usrLanguage));
						}
					}
				}
			} else {

				if (tempFinCollaterals == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinCollaterals != null && oldFinCollaterals != null
				        && !oldFinCollaterals.getLastMntOn().equals(
				                tempFinCollaterals.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
					        PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
		        || !finCollaterals.isWorkflow()) {
			finCollaterals.setBefImage(befFinCollaterals);
		}

		return auditDetail;
	}
	
	public FinCollateralsDAO getFinCollateralsDAO() {
		return finCollateralsDAO;
	}

	public void setFinCollateralsDAO(FinCollateralsDAO finCollateralsDAO) {
		this.finCollateralsDAO = finCollateralsDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
}
