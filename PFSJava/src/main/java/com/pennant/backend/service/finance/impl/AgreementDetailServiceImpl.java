package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinAgreementDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class AgreementDetailServiceImpl extends GenericService<AgreementDetail> implements AgreementDetailService{

	private static final Logger logger = Logger.getLogger(AgreementDetailServiceImpl.class);

	private FinAgreementDetailDAO finAgreementDetailDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;

	public AgreementDetailServiceImpl() {
		super();
	}
	
	/**
	 * Set Agreement Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public List<FinanceReferenceDetail> getAggrementDetailList(String finType,String finEvent, String nextRoleCode) {
		return getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType,finEvent, nextRoleCode, null, "_AAView");
	}

	@Override
	public List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String tableType) {
		return getFinAgreementDetailDAO().getFinAgrByFinRef(finReference, tableType);
	}

	@Override
	public FinAgreementDetail getFinAgreementDetailById(String finReference, long agrId, String tableType) {
		return getFinAgreementDetailDAO().getFinAgreementDetailById(finReference, agrId, tableType);
	}
	
	@Override
    public FinAgreementDetail getFinAgrDetailByAgrId(String finReference, long agrId) {
		return getFinAgreementDetailDAO().getFinAgreementDetailById(finReference, agrId, "_AView");
    }
	
	@Override
	public List<AuditDetail> delete(FinanceDetail financeDetail, String tableType, String auditTranType) {
		logger.debug("Entering ");
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		String finReference = financeDetail.getFinScheduleData().getFinReference();	
		FinAgreementDetail object = new FinAgreementDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinAgreementDetail(), object.getExcludeFields());

		if (financeDetail.getAggrementList() != null && !financeDetail.getAggrementList().isEmpty()) {
			int i = 0;
			for (FinanceReferenceDetail agreement : financeDetail.getAggrementList()) {
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], agreement.getBefImage(), agreement));
				i++;
			}

			getFinAgreementDetailDAO().deleteByFinRef(finReference, tableType);
		}

		logger.debug("Leaving ");
		return auditList;
	}

	@Override
	public List<AuditDetail> validate(List<AuditDetail> auditDetails, String method,String  usrLanguage){

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validate(AuditDetail auditDetail, String method, String  usrLanguage){

		FinAgreementDetail agreementDetail= (FinAgreementDetail) auditDetail.getModelData();
		FinAgreementDetail tempFinAgreementDetail= null;
		if (agreementDetail.isWorkflow()){
			tempFinAgreementDetail = getFinAgreementDetailDAO().getFinAgreementDetailById(
					agreementDetail.getFinReference(),agreementDetail.getAgrId(),"_Temp");
		}

		FinAgreementDetail befFinAgreementDetail= getFinAgreementDetailDAO().getFinAgreementDetailById(
				agreementDetail.getFinReference(),agreementDetail.getAgrId(),"");

		FinAgreementDetail oldFinAgreementDetail= agreementDetail.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = agreementDetail.getFinReference();
		valueParm[1] = String.valueOf(agreementDetail.getAgrId());

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AgrId") + ":"+valueParm[1];

		if (agreementDetail.isNew()){ // for New record or new record into work flow

			if (!agreementDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinAgreementDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (agreementDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinAgreementDetail !=null || tempFinAgreementDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinAgreementDetail ==null || tempFinAgreementDetail!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!agreementDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinAgreementDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldFinAgreementDetail!=null && !oldFinAgreementDetail.getLastMntOn().equals(befFinAgreementDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}
			}else{

				if (tempFinAgreementDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempFinAgreementDetail!=null  && oldFinAgreementDetail!=null && !oldFinAgreementDetail.getLastMntOn().equals(tempFinAgreementDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !agreementDetail.isWorkflow()){
			agreementDetail.setBefImage(befFinAgreementDetail);	
		}

		return auditDetail;
	}


	public void setFinAgreementDetailDAO(FinAgreementDetailDAO finAgreementDetailDAO) {
		this.finAgreementDetailDAO = finAgreementDetailDAO;
	}

	public FinAgreementDetailDAO getFinAgreementDetailDAO() {
		return finAgreementDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

}
