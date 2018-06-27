package com.pennant.backend.service.customermasters.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;

public class CustomerExtLiabilityServiceImpl implements CustomerExtLiabilityService{
	private static Logger logger = Logger.getLogger(CustomerExtLiabilityServiceImpl.class);
		
	@Autowired
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	@Autowired
	private ExternalLiabilityDAO externalLiabilityDAO;
	
	@Autowired
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";

		CustomerExtLiability customerExtLiability = new CustomerExtLiability();
		BeanUtils.copyProperties((CustomerExtLiability) auditHeader.getAuditDetail().getModelData(),
				customerExtLiability);

		if (customerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			externalLiabilityDAO.delete(customerExtLiability.getId(), "");
		} else {
			customerExtLiability.setRoleCode("");
			customerExtLiability.setNextRoleCode("");
			customerExtLiability.setTaskId("");
			customerExtLiability.setNextTaskId("");
			customerExtLiability.setWorkflowId(0);

			if (customerExtLiability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerExtLiability.setRecordType("");
				externalLiabilityDAO.save(customerExtLiability, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerExtLiability.setRecordType("");
				externalLiabilityDAO.update(customerExtLiability, "");
			}
		}
		if (!StringUtils.equals(customerExtLiability.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			externalLiabilityDAO.delete(customerExtLiability.getId(), "_temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeaderDAO.addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerExtLiability);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public int getVersion(long custId, int liabilitySeq) {
		return customerExtLiabilityDAO.getVersion(custId, liabilitySeq);
	}

	@Override
	public CustomerExtLiability getLiability(CustomerExtLiability liability) {
		return customerExtLiabilityDAO.getLiability(liability, "_aview",liability.getInputSource());
	}

	@Override
	public List<CustomerExtLiability> getLiabilities(CustomerExtLiability liability) {
		return externalLiabilityDAO.getLiabilities(liability.getCustId(), "_aview");
	}

	@Override
	public BigDecimal getExternalLiabilitySum(long custId) {
		return customerExtLiabilityDAO.getExternalLiabilitySum(custId);
	}
	
	@Override
	public BigDecimal getSumAmtCustomerExtLiabilityById(Set<Long> custId) {
		return getCustomerExtLiabilityDAO().getSumAmtCustomerExtLiabilityById(custId);
	}

	public CustomerExtLiabilityDAO getCustomerExtLiabilityDAO() {
		return customerExtLiabilityDAO;
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

}
