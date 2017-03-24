package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.util.PennantConstants;

public class CustomerBankInfoServiceImpl implements CustomerBankInfoService {
	private static Logger logger = Logger.getLogger(CustomerBankInfoServiceImpl.class);

	private CustomerBankInfoDAO customerBankInfoDAO;
	private AuditHeaderDAO auditHeaderDAO;

	/**
	 * getBankInfoByCustomerId fetch the details by using CustomerBankInfoDAO's getBankInfoByCustomer method . with
	 * parameter custID. it fetches the records from the CustomerBankInfo.
	 * 
	 * @param id
	 * 
	 * @return CustomerBankInfo List
	 */
	@Override
	public List<CustomerBankInfo> getBankInfoByCustomerId(long id) {
		return getCustomerBankInfoDAO().getBankInfoByCustomer(id, "_View");
	}

	/**
	 * getBankInfoByCustomerId fetch the details by using CustomerBankInfoDAO's getBankInfoByCustomer method . with
	 * parameter custID. it fetches the Approved records from the CustomerBankInfo.
	 * 
	 * @param id
	 * 
	 * @return CustomerBankInfo List
	 */
	@Override
	public List<CustomerBankInfo> getApprovedBankInfoByCustomerId(long id) {
		return getCustomerBankInfoDAO().getBankInfoByCustomer(id, "_AView");
	}

	@Override
	public CustomerBankInfo getCustomerBankInfoById(long bankId) {
		return getCustomerBankInfoDAO().getCustomerBankInfoById(bankId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerBankInfoDAO().delete with
	 * parameters customerBankInfo,"" b) NEW Add new record in to main table by using getCustomerBankinfoDAO().save with
	 * parameters customerBankInfo,"" c) EDIT Update record in the main table by using getCustomerBankInfoDAO().update
	 * with parameters customerBankInfo,"" 3) Delete the record from the workFlow table by using
	 * getCustomerBankInfoDAO().delete with parameters customerBankInfo,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtCustomerBankInfo by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtCustomerBankInfo by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";

		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties((CustomerBankInfo) auditHeader.getAuditDetail().getModelData(), customerBankInfo);

		if (customerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerBankInfoDAO().delete(customerBankInfo, "");
		} else {
			customerBankInfo.setRoleCode("");
			customerBankInfo.setNextRoleCode("");
			customerBankInfo.setTaskId("");
			customerBankInfo.setNextTaskId("");
			customerBankInfo.setWorkflowId(0);

			if (customerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerBankInfo.setRecordType("");
				customerBankInfo.setBankId(getCustomerBankInfoDAO().save(customerBankInfo, ""));
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerBankInfo.setRecordType("");
				getCustomerBankInfoDAO().update(customerBankInfo, "");
			}
		}
		if (!StringUtils.equals(customerBankInfo.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerBankInfoDAO().delete(customerBankInfo, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerBankInfo);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getVersion(long id) {
		return getCustomerBankInfoDAO().getVersion(id);
	}

	public CustomerBankInfoDAO getCustomerBankInfoDAO() {
		return customerBankInfoDAO;
	}

	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

}
