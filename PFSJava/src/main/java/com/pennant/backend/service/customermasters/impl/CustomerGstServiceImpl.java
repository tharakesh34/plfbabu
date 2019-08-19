package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

public class CustomerGstServiceImpl extends GenericService<CustomerGST> implements CustomerGstService {
	private static Logger logger = Logger.getLogger(CustomerAddresServiceImpl.class);
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

		if (customerGST.isNew()) {
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

				getCustomerGstDetailDAO().save(customerGST, "");
				// BankInfoDetails
				List<CustomerGSTDetails> customerGSTDetailslist = customerGST.getCustomerGSTDetailslist();
				if (CollectionUtils.isNotEmpty(customerGSTDetailslist)) {
					for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
						customerGSTDetails.setHeaderId(customerGST.getId());
					}
					getCustomerGstDetailDAO().saveCustomerGSTDetailsBatch(customerGSTDetailslist, "");
				}

			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerGST.setRecordType("");
				if (customerGST.getCustomerGSTDetailslist().size() > 0) {
					for (CustomerGSTDetails customerGSTDetails : customerGST.getCustomerGSTDetailslist()) {
						customerGSTDetails.setHeaderId(customerGST.getId());
						getCustomerGstDetailDAO().update(customerGST, "");
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
		// TODO Auto-generated method stub
		return customerGstDetailDAO.getCustomerGSTDetailsByCustomer(id, "_View");
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		CustomerGST customerGST = (CustomerGST) auditDetail.getModelData();

		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	@Override
	public int getVersion(long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AuditDetail doValidations(CustomerGST customerGST) {
		// TODO Auto-generated method stub
		return null;
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
