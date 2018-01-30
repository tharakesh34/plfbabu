package com.pennant.backend.service.commitment.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.commitment.CommitmentRateDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CommitmentRateValidation {
	private static final Logger logger = Logger.getLogger(CommitmentRateValidation.class);

	private CommitmentRateDAO commitmentRateDAO;

	/**
	 * Setters And Getters
	 * @param commitmentRateDAO
	 */

	public CommitmentRateValidation(CommitmentRateDAO commitmentRateDAO) {
		this.commitmentRateDAO = commitmentRateDAO;
	}

	public CommitmentRateDAO getCommitmentRateDAO() {
		return commitmentRateDAO;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	public AuditHeader commitmentRateValidation(AuditHeader auditHeader, String method){
		logger.debug("Entering");

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * 
	 * @param auditDetails
	 * @param method
	 * @param usrLanguage
	 * @return
	 */
	public List<AuditDetail> commitmentRateListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage) {
		logger.debug("Entering");

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}

		logger.debug("Leaving");
		return new ArrayList<AuditDetail>();
	}

	/**
	 * 
	 * @param auditDetail
	 * @param method
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage) {
		logger.debug("Entering");

		CommitmentRate commitmentRate= (CommitmentRate) auditDetail.getModelData();
		CommitmentRate tempCommitmentRate= null;

		if (commitmentRate.isWorkflow()){
			tempCommitmentRate = getCommitmentRateDAO().getCommitmentRateById(commitmentRate.getCmtReference(), commitmentRate.getCmtRvwFrq(), "_Temp");
		}

		CommitmentRate befCommitmentRate= getCommitmentRateDAO().getCommitmentRateById(commitmentRate.getCmtReference(), commitmentRate.getCmtRvwFrq(), "");
		CommitmentRate oldCommitmentRate= commitmentRate.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = commitmentRate.getCmtReference();
		valueParm[1] = commitmentRate.getCmtRvwFrq();

		errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + " : "+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CmtRvwFrq") + " : "+ valueParm[1];

		if (commitmentRate.isNew()){ // for New record or new record into work flow

			if (!commitmentRate.isWorkflow()){// With out Work flow only new records  
				if (befCommitmentRate !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
				}	
			}else{ // with work flow

				if (commitmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommitmentRate !=null || tempCommitmentRate!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41014",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommitmentRate ==null || tempCommitmentRate!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commitmentRate.isWorkflow()){	// With out Work flow for update and delete

				if (befCommitmentRate ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCommitmentRate!=null && !oldCommitmentRate.getLastMntOn().equals(befCommitmentRate.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempCommitmentRate==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCommitmentRate!=null  && oldCommitmentRate!=null && !oldCommitmentRate.getLastMntOn().equals(tempCommitmentRate.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !commitmentRate.isWorkflow()){
			auditDetail.setBefImage(befCommitmentRate);	
		}

		logger.debug("Leaving");
		return auditDetail;
	}
}