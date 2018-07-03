package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;

public class CustomerExtLiabilityValidation {
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	@Autowired
	protected SamplingDAO samplingDAO;

	public CustomerExtLiabilityValidation(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public AuditHeader extLiabilityValidation(AuditHeader auditHeader, String method){
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), 0, method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> extLiabilityListValidation(List<AuditDetail> auditDetails, long samplingId, String method, String  usrLanguage){
		if (CollectionUtils.isNotEmpty(auditDetails)) {
			List<AuditDetail> details = new ArrayList<>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), samplingId, method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<>();
	}

	private AuditDetail validate(AuditDetail auditDetail, long samplingId, String method, String usrLanguage) {
		CustomerExtLiability liability = (CustomerExtLiability) auditDetail.getModelData();
		CustomerExtLiability tempLiability = null;
		
		if ("sampling".equals(liability.getInputSource())) {
			liability.setLinkId(samplingDAO.getLiabilityLinkId(liability.getCustId(), samplingId));
		} else {
			liability.setLinkId(customerExtLiabilityDAO.getLinkId(liability.getCustId()));
		}

		if (liability.isWorkflow()) {
			tempLiability = customerExtLiabilityDAO.getLiability(liability, "_temp",liability.getInputSource());
		}

		CustomerExtLiability beforeLiability = customerExtLiabilityDAO.getLiability(liability, "",liability.getInputSource());
		CustomerExtLiability oldLiability = liability.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(liability.getCustCif());
		valueParm[1] = String.valueOf(liability.getSeqNo());

		errParm[0] = App.getLabel("CustomerExtLiability") + " , " + App.getLabel("label_CustCIF") + ":" + valueParm[0]
				+ " and ";
		errParm[1] = App.getLabel("label_LiabilitySeq") + "-" + valueParm[1];
		
		String errorCode = null;

		if (liability.isNew()) { 
			if (!liability.isWorkflow()) {
				if (beforeLiability != null) {
					errorCode = "41001";
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorCode, errParm, null));
				}
			} else {
				if (liability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (beforeLiability != null || tempLiability != null) {
						errorCode = "41001";
					}
				} else {
					if (beforeLiability == null || tempLiability != null) {
						errorCode = "41005";
					}
				}
			}
		} else {
			if (!liability.isWorkflow()) {
				if (oldLiability == null) {
					errorCode = "41002";
				} else {
					if (oldLiability != null && !oldLiability.getLastMntOn().equals(beforeLiability.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							errorCode = "41003";
						} else {
							errorCode = "41004";
						}
					}
				}
			} else {

				if (tempLiability == null) {
					errorCode = "41005";
				}

				if (tempLiability != null && oldLiability != null
						&& !oldLiability.getLastMntOn().equals(tempLiability.getLastMntOn())) {
					errorCode = "41005";
				}

			}
		}
		
		if (errorCode != null) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorCode, errParm, null));
		}
		

		auditDetail.setErrorDetail(screenValidations(liability));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !liability.isWorkflow()) {
			liability.setBefImage(beforeLiability);
		}

		return auditDetail;
	}

	/**
	 * Method For Screen Level Validations
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	public ErrorDetail  screenValidations(CustomerExtLiability liability){
		return null;
	}
	
	/**
	 * Validate CustomerExtLiability.
	 * @param customerExtLiability
	 * @return AuditDetail
	 */
	public AuditDetail doValidations(CustomerExtLiability liability) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();
		
		// validate Master code with PLF system masters
		if (liability.getFinDate().compareTo(DateUtility.getAppDate()) >= 0 || SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(liability.getFinDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "FinDate";
			valueParm[1] = DateUtility.formatDate(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
					PennantConstants.XMLDateFormat);
			valueParm[2] = DateUtility.formatDate(DateUtility.getAppDate(), PennantConstants.XMLDateFormat);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}
		
		if (!customerExtLiabilityDAO.isBankExists(liability.getLoanBank())) {
			String[] valueParm = new String[2];
			valueParm[0] = "BankCode";
			valueParm[1] = liability.getLoanBank();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;	
		}
		if (!customerExtLiabilityDAO.isFinTypeExists(liability.getFinType())) {
			String[] valueParm = new String[2];
			valueParm[0] = "FinType";
			valueParm[1] = liability.getFinType();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;	
		}
		
		if (!customerExtLiabilityDAO.isFinStatuExists(liability.getFinStatus())) {
			String[] valueParm = new String[2];
			valueParm[0] = "FinStatus";
			valueParm[1] = liability.getFinStatus();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;	
		}
		auditDetail.setErrorDetail(errorDetail);
		return auditDetail;
	}
}
