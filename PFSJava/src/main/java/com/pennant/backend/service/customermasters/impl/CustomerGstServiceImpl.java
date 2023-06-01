package com.pennant.backend.service.customermasters.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerGstDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerGstService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerGstServiceImpl extends GenericService<CustomerGST> implements CustomerGstService {
	private static Logger logger = LogManager.getLogger(CustomerAddresServiceImpl.class);
	private AuditHeaderDAO auditHeaderDAO;
	private CustomerGstDetailDAO customerGstDetailDAO;

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerGstDetailDAO getCustomerGstDetailDAO() {
		return customerGstDetailDAO;
	}

	public void setCustomerGstDetailDAO(CustomerGstDetailDAO customerGstDetailDAO) {
		this.customerGstDetailDAO = customerGstDetailDAO;
	}

	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerGST customerGST = (CustomerGST) auditHeader.getAuditDetail().getModelData();
		getCustomerGstDetailDAO().delete(customerGST, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		CustomerGST customerGST = (CustomerGST) auditHeader.getAuditDetail().getModelData();

		if (customerGST.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerGST.isNewRecord()) {
			customerGST.setId(getCustomerGstDetailDAO().save(customerGST, tableType));

			// customerGST.setId(getCustomerGstDetailDAO().save(customerGST,
			// tableType));
			auditHeader.getAuditDetail().setModelData(customerGST);
			auditHeader.setAuditReference(String.valueOf(customerGST.getId()));
		} else {
			getCustomerGstDetailDAO().update(customerGST, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		CustomerGST customerGST = new CustomerGST();
		BeanUtils.copyProperties((CustomerGST) auditHeader.getAuditDetail().getModelData(), customerGST);

		if (customerGST.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerGstDetailDAO().delete(customerGST, "");

		} else {
			customerGST.setRoleCode("");
			customerGST.setNextRoleCode("");
			customerGST.setTaskId("");
			customerGST.setNextTaskId("");
			customerGST.setWorkflowId(0);

			if (customerGST.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerGST.setRecordType("");

				if (getCustomerGstDetailDAO().getCustomerGSTByGstNumber(customerGST, "") == null) {
					getCustomerGstDetailDAO().save(customerGST, "");
				}

				// CustomerGSTDetails
				List<CustomerGSTDetails> customerGSTDetailslist = customerGST.getCustomerGSTDetailslist();
				if (CollectionUtils.isNotEmpty(customerGSTDetailslist)) {
					for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
						customerGSTDetails.setRecordType(customerGST.getRecordType());
						customerGSTDetails.setRecordStatus(customerGST.getRecordStatus());
						customerGSTDetails.setHeaderId(customerGST.getId());
						getCustomerGstDetailDAO().save(customerGSTDetails, "");
					}

				}

			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerGST.setRecordType("");
				getCustomerGstDetailDAO().update(customerGST, "");
				if (customerGST.getCustomerGSTDetailslist().size() > 0) {
					for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
						customerGSTDetails.setHeaderId(customerGST.getId());
						getCustomerGstDetailDAO().update(customerGSTDetails, "");
					}
				}
			}
		}

		if (!StringUtils.equals(customerGST.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerGstDetailDAO().delete(customerGST, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerGST);
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

		CustomerGST customerGST = (CustomerGST) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerGstDetailDAO().delete(customerGST, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<CustomerGSTDetails> getCustomerGstDeatailsByCustomerId(long id, String type) {
		return customerGstDetailDAO.getCustomerGSTDetailsByCustomer(id, "_View");
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public int getVersion(long id) {
		return getCustomerGstDetailDAO().getVersion(id);
	}

	@Override
	public AuditDetail doValidations(CustomerGST customerGST, String recordType) {

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();

		if (StringUtils.equals(recordType, PennantConstants.RECORD_TYPE_NEW)) {
			Pattern pattern = Pattern
					.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_GSTIN));
			Matcher matcher = pattern.matcher(customerGST.getGstNumber());
			if (!matcher.matches()) {
				String[] valueParm = new String[2];
				valueParm[0] = "GST Number";
				valueParm[1] = customerGST.getGstNumber();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90405", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			} else {
				for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
					if (customerGSTDetails.getFrequancy() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Frequency";
						valueParm[1] = customerGSTDetails.getFrequancy();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
					if (customerGSTDetails.getSalAmount() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Sal Amount";
						valueParm[1] = customerGSTDetails.getSalAmount().toString();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
					if (customerGSTDetails.getFinancialYear() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Finacial Year";
						valueParm[1] = customerGSTDetails.getFinancialYear().toString();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
			}

		}
		auditDetail.setErrorDetail(errorDetail);
		return auditDetail;

	}

	@Override
	public CustomerGST getCustomerGstDeatailsByCustomerId(long id) {

		return getCustomerGstDetailDAO().getCustomerGstByCustId(id, "_View");
	}

	@Override
	public List<CustomerGST> getApprovedGstInfoByCustomerId(long id) {

		return getCustomerGstDetailDAO().getCustomerGSTById(id, "_AView");

	}

}
