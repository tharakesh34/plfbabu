package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerChequeInfoDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.service.customermasters.CustomerChequeInfoService;
import com.pennant.backend.util.PennantConstants;

public class CustomerChequeInfoServiceImpl implements CustomerChequeInfoService {
	private static Logger logger = LogManager.getLogger(CustomerChequeInfoServiceImpl.class);

	private CustomerChequeInfoDAO customerChequeInfoDAO;
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public List<CustomerChequeInfo> getChequeInfoByCustomerId(long custId) {

		return getCustomerChequeInfoDAO().getChequeInfoByCustomer(custId, "_AView");
	}

	@Override
	public CustomerChequeInfo getCustomerChequeInfoById(long custId, int chequeSeq) {
		return getCustomerChequeInfoDAO().getCustomerChequeInfoById(custId, chequeSeq, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerChequeInfoDAO().delete with
	 * parameters customerChequeInfo,"" b) NEW Add new record in to main table by using getCustomerChequeInfoDAO().save
	 * with parameters customerChequeInfo,"" c) EDIT Update record in the main table by using
	 * getCustomerChequeInfoDAO().update with parameters customerChequeInfo,"" 3) Delete the record from the workFlow
	 * table by using getCustomerChequeInfoDAO().delete with parameters customerChequeInfo,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtCustomerChequeInfo by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit
	 * the record in to AuditHeader and AdtCustomerChequeInfo by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";

		CustomerChequeInfo customerChequeInfo = new CustomerChequeInfo();
		BeanUtils.copyProperties((CustomerChequeInfo) auditHeader.getAuditDetail().getModelData(), customerChequeInfo);

		if (customerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerChequeInfoDAO().delete(customerChequeInfo, "");
		} else {
			customerChequeInfo.setRoleCode("");
			customerChequeInfo.setNextRoleCode("");
			customerChequeInfo.setTaskId("");
			customerChequeInfo.setNextTaskId("");
			customerChequeInfo.setWorkflowId(0);

			if (customerChequeInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerChequeInfo.setRecordType("");
				getCustomerChequeInfoDAO().save(customerChequeInfo, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerChequeInfo.setRecordType("");
				getCustomerChequeInfoDAO().update(customerChequeInfo, "");
			}
		}
		if (!StringUtils.equals(customerChequeInfo.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerChequeInfoDAO().delete(customerChequeInfo, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerChequeInfo);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public int getVersion(long custId, int chequeSeq) {
		return getCustomerChequeInfoDAO().getVersion(custId, chequeSeq);
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerChequeInfoDAO getCustomerChequeInfoDAO() {
		return customerChequeInfoDAO;
	}

	public void setCustomerChequeInfoDAO(CustomerChequeInfoDAO customerChequeInfoDAO) {
		this.customerChequeInfoDAO = customerChequeInfoDAO;
	}

}
