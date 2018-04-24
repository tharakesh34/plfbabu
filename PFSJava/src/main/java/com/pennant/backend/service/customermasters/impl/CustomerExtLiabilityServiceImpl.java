package com.pennant.backend.service.customermasters.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.util.PennantConstants;

public class CustomerExtLiabilityServiceImpl implements CustomerExtLiabilityService {
	private static Logger logger = Logger.getLogger(CustomerExtLiabilityServiceImpl.class);

	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public List<CustomerExtLiability> getExtLiabilityByCustomer(long custId) {
		return getCustomerExtLiabilityDAO().getExtLiabilityByCustomer(custId, "_AView");
	}

	@Override
	public CustomerExtLiability getCustomerExtLiabilityById(long custId, int liabilitySeq) {
		return getCustomerExtLiabilityDAO().getCustomerExtLiabilityById(custId, liabilitySeq, "_AView");
	}
	
	@Override
	public BigDecimal getSumAmtCustomerExtLiabilityById(long custId) {
		return getCustomerExtLiabilityDAO().getSumAmtCustomerExtLiabilityById(custId);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerChequeInfoDAO().delete with
	 * parameters customerExtLiability,"" b) NEW Add new record in to main table by using
	 * getCustomerExtLiabilityDAO().save with parameters customerExtLiability,"" c) EDIT Update record in the main table
	 * by using getCustomerExtLiabilityDAO().update with parameters customerExtLiability,"" 3) Delete the record from
	 * the workFlow table by using getCustomerExtLiabilityDAO().delete with parameters customerExtLiability,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCustomerExtLiability by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtCustomerExtLiability by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";

		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties((CustomerExtLiability) auditHeader.getAuditDetail().getModelData(),
				customerExtLiability);

		if (customerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerExtLiabilityDAO().delete(customerExtLiability, "");
		} else {
			customerExtLiability.setRoleCode("");
			customerExtLiability.setNextRoleCode("");
			customerExtLiability.setTaskId("");
			customerExtLiability.setNextTaskId("");
			customerExtLiability.setWorkflowId(0);

			if (customerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerExtLiability.setRecordType("");
				getCustomerExtLiabilityDAO().save(customerExtLiability, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerExtLiability.setRecordType("");
				getCustomerExtLiabilityDAO().update(customerExtLiability, "");
			}
		}
		if (!StringUtils.equals(customerExtLiability.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerExtLiabilityDAO().delete(customerExtLiability, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerExtLiability);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public CustomerExtLiabilityDAO getCustomerExtLiabilityDAO() {
		return customerExtLiabilityDAO;
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Override
	public int getVersion(long custId, int liabilitySeq) {

		return getCustomerExtLiabilityDAO().getVersion(custId, liabilitySeq);
	}
}
