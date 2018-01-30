package com.pennant.backend.service.applicationmaster.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

public class BlacklistCustomerServiceImpl extends GenericService<BlackListCustomers> implements BlacklistCustomerService {

	private static Logger logger = Logger.getLogger(BlacklistCustomerServiceImpl.class);
	
	private AuditHeaderDAO auditHeaderDAO;
	private BlackListCustomerDAO blacklistCustomerDAO;
	
	public BlacklistCustomerServiceImpl() {
		super();
	}
	
	
	@Override
    public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		TableType tableType = TableType.MAIN_TAB;
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();

		if (blackListCustomers.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (blackListCustomers.isNew()) {
			blackListCustomers.setCustCIF(getBlacklistCustomerDAO().save(blackListCustomers, tableType));
			auditHeader.getAuditDetail().setModelData(blackListCustomers);
			auditHeader.setAuditReference(String.valueOf(blackListCustomers.getCustCIF()));
		} else {
			getBlacklistCustomerDAO().update(blackListCustomers, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	
    }
	
	@Override
    public BlackListCustomers getBlacklistCustomerById(String id) {
		  return getBlacklistCustomerDAO().getBlacklistCustomerById(id,"_View");
    }
	

	@Override
    public BlackListCustomers getApprovedBlacklistById(String id) {
		return getBlacklistCustomerDAO().getBlacklistCustomerById(id, "");
    }
	

	@Override
    public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		
		return auditHeader;
    }

	@Override
    public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		BlackListCustomers blackListCustomers = new BlackListCustomers();
		BeanUtils.copyProperties((BlackListCustomers) auditHeader.getAuditDetail().getModelData(), blackListCustomers);
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(blackListCustomers.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					blacklistCustomerDAO.getBlacklistCustomerById(blackListCustomers.getId(), ""));
		}
		if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBlacklistCustomerDAO().delete(blackListCustomers, TableType.MAIN_TAB);
		} else {
			blackListCustomers.setRoleCode("");
			blackListCustomers.setNextRoleCode("");
			blackListCustomers.setTaskId("");
			blackListCustomers.setNextTaskId("");
			blackListCustomers.setWorkflowId(0);

			if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().save(blackListCustomers, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().update(blackListCustomers, TableType.MAIN_TAB);
			}
		}
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(blackListCustomers);
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
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getBlacklistCustomerDAO().delete(blackListCustomers, TableType.TEMP_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
    }

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
    }

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditDetail.getModelData();
		// Check the unique keys.
		if (blackListCustomers.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(blackListCustomers.getRecordType())
				&& blacklistCustomerDAO.isDuplicateKey(blackListCustomers.getId(),
						blackListCustomers.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+ blackListCustomers.getCustCIF();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
	
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public BlackListCustomerDAO getBlacklistCustomerDAO() {
	    return blacklistCustomerDAO;
    }

	public void setBlacklistCustomerDAO(BlackListCustomerDAO blacklistCustomerDAO) {
	    this.blacklistCustomerDAO = blacklistCustomerDAO;
    }

}
