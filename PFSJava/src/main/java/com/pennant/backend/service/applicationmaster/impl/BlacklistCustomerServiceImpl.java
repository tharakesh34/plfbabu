package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.BlackListCustomerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

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
		String tableType = "";
		BlackListCustomers blackListCustomers = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();

		if (blackListCustomers.isWorkflow()) {
			tableType = "_Temp";
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
		getBlacklistCustomerDAO().delete(blackListCustomers, "");
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
		if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getBlacklistCustomerDAO().delete(blackListCustomers, "");
		} else {
			blackListCustomers.setRoleCode("");
			blackListCustomers.setNextRoleCode("");
			blackListCustomers.setTaskId("");
			blackListCustomers.setNextTaskId("");
			blackListCustomers.setWorkflowId(0);

			if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().save(blackListCustomers, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				blackListCustomers.setRecordType("");
				getBlacklistCustomerDAO().update(blackListCustomers, "");
			}
		}
		getBlacklistCustomerDAO().delete(blackListCustomers, "_Temp");
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
		getBlacklistCustomerDAO().delete(blackListCustomers, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
    }

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
    }

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());

		BlackListCustomers blackListCustomers = (BlackListCustomers) auditDetail.getModelData();
		BlackListCustomers tempBlackListCustomers = null;

		if (blackListCustomers.isWorkflow()) {
			tempBlackListCustomers = getBlacklistCustomerDAO().getBlacklistCustomerById(blackListCustomers.getId(), "_Temp");
		}

		BlackListCustomers befBlackListCustomers = getBlacklistCustomerDAO().getBlacklistCustomerById(blackListCustomers.getId(), "");
		BlackListCustomers oldBlackListCustomers = blackListCustomers.getBefImage() ;

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = blackListCustomers.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":"+ valueParm[0];

		if (blackListCustomers.isNew()) { // for New record or new record into work flow

			if (!blackListCustomers.isWorkflow()) {// With out Work flow only new records
				if (befBlackListCustomers != null) { // Record Already Exists in the table then error
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001",errParm, null));
				}
			} else { // with work flow
				if (blackListCustomers.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new 
					if (befBlackListCustomers != null || tempBlackListCustomers != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befBlackListCustomers == null || tempBlackListCustomers != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!blackListCustomers.isWorkflow()) { // With out Work flow for update and delete
				if (befBlackListCustomers == null) { // if records not exists in the main table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002",errParm, null));
				} else {
					if (oldBlackListCustomers != null
							&& !oldBlackListCustomers.getLastMntOn().equals(befBlackListCustomers.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003",errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004",errParm, null));
						}
					}
				}
			} else {
				if (tempBlackListCustomers == null) { // if records not exists in the Work flow table
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}

				if (tempBlackListCustomers != null
						&& oldBlackListCustomers != null
						&& !oldBlackListCustomers.getLastMntOn().equals(tempBlackListCustomers.getLastMntOn())) {
					auditDetail
					.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !blackListCustomers.isWorkflow()) {
			auditDetail.setBefImage(befBlackListCustomers);
		}
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
