package com.pennant.backend.service.applicationmaster.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.returnedCheques.ReturnedChequeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReturnedChequeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * @author sreeravali.s
 *
 */
public class ReturnedChequeServiceImpl extends GenericService<ReturnedChequeDetails> implements
        ReturnedChequeService {

	private static Logger logger = Logger.getLogger(ReturnedChequeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ReturnedChequeDAO returnedChequeDAO;

	public ReturnedChequeServiceImpl() {
		super();
	}
	

	/**
	 * getReturnedChequesById fetch the details by using ReturnedChequeDAO's getReturnedChequeByID method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ReturnedCheque
	 */
	@Override
	public ReturnedChequeDetails getReturnedChequesById(String custCIF, String chequeNo) {
		return getReturnedChequeDAO().getReturnedChequeById(custCIF, chequeNo, "_View");
	}

	/**
	 * getApprovedCityById fetch the details by using ReturnedChequeDAO's getReturnedChequesById method . with parameter
	 * id and type as blank. it fetches the approved records from the RetunedCheque.
	 * 
	 * @param id
	 *            (String)
	 * @return ReturnedCheque
	 */
	@Override
	public ReturnedChequeDetails getApprovedReturnedChequesById(String custCIF, String chequeNo) {
		return getReturnedChequeDAO().getReturnedChequeById(custCIF, chequeNo, "_View");
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ReturnedCheque/ReturnedCheque_Temp by using ReturnedChequeDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using ReturnedChequeDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtReturnedCheque by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData();
		if (returnedCheque.isWorkflow()) {
			tableType = "_Temp";
		}
		if (returnedCheque.isNew()) {
			getReturnedChequeDAO().save(returnedCheque, tableType);
			auditHeader.getAuditDetail().setModelData(returnedCheque);
			auditHeader.setAuditReference(String.valueOf(returnedCheque.getCustCIF())
			        + PennantConstants.KEY_SEPERATOR + returnedCheque.getChequeNo());

		} else {
			getReturnedChequeDAO().update(returnedCheque, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ReturnedCheque by using ReturnedChequeDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtReturnedCheque by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData();
		getReturnedChequeDAO().delete(returnedCheque, "");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getReturnedChequeDAO().delete with
	 * parameters city,"" b) NEW Add new record in to main table by using getReturnedChequeDAO().save with parameters
	 * returnedCheque,"" ) EDIT Update record in the main table by using getReturnedChequeDAO().update with parameters
	 * returnedCheque,"" 3) Delete the record from the workFlow table by using getReturnedChequeDAO().delete with
	 * parameters returnedCheque,"_Temp" 4) Audit the record in to AuditHeader and AdtReturnedCheque by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtReturnedCheque by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReturnedChequeDetails returnedCheque = new ReturnedChequeDetails();
		BeanUtils.copyProperties((ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData(), returnedCheque);

		if (returnedCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getReturnedChequeDAO().delete(returnedCheque, "");

		} else {
			returnedCheque.setRoleCode("");
			returnedCheque.setNextRoleCode("");
			returnedCheque.setTaskId("");
			returnedCheque.setNextTaskId("");
			returnedCheque.setWorkflowId(0);

			if (returnedCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				returnedCheque.setRecordType("");
				getReturnedChequeDAO().save(returnedCheque, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				returnedCheque.setRecordType("");
				getReturnedChequeDAO().update(returnedCheque, "");
			}
		}

		getReturnedChequeDAO().delete(returnedCheque, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(returnedCheque);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getReturnedChequeDAO().delete with parameters returnedCheque,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtReturnedCheque by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReturnedChequeDAO().delete(returnedCheque, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
		        auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getRetunrdChequeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditDetail.getModelData();
		ReturnedChequeDetails tempReturnedCheque = null;
		if (returnedCheque.isWorkflow()) {
			tempReturnedCheque = getReturnedChequeDAO().getReturnedChequeById(
			        returnedCheque.getCustCIF(), returnedCheque.getChequeNo(), "_Temp");
		}
		ReturnedChequeDetails befReturnedCheque = getReturnedChequeDAO().getReturnedChequeById(
		        returnedCheque.getCustCIF(), returnedCheque.getChequeNo(), "");
		ReturnedChequeDetails oldReturendCheque = returnedCheque.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = returnedCheque.getCustCIF();
		valueParm[1] = returnedCheque.getChequeNo();

		errParm[0] = PennantJavaUtil.getLabel("label_ReturnedChequeCustCIF") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CheuqeNo") + ":" + valueParm[1];
		if (returnedCheque.isNew()) { // for New record or new record into work flow

			if (!returnedCheque.isWorkflow()) {// With out Work flow only new records
				if (befReturnedCheque != null) { // Record Already Exists in the table 
					// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41008", errParm, null));
				}
			} else { // with work flow
				if (returnedCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befReturnedCheque != null || tempReturnedCheque != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						        "41008", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befReturnedCheque == null || tempReturnedCheque != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						        "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!returnedCheque.isWorkflow()) { // With out Work flow for update and delete

				if (befReturnedCheque == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41002", errParm, null));
				} else {

					if (oldReturendCheque != null
					        && !oldReturendCheque.getLastMntOn().equals(
					                befReturnedCheque.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
						        .equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							        "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							        "41004", errParm, null));
						}
					}
				}

			} else {

				if (tempReturnedCheque == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41005", errParm, null));
				}

				if (tempReturnedCheque != null
				        && oldReturendCheque != null
				        && !oldReturendCheque.getLastMntOn().equals(
				                tempReturnedCheque.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
					        "41005", errParm, null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(),
		        usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !returnedCheque.isWorkflow()) {
			auditDetail.setBefImage(befReturnedCheque);
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	@Override
    public List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques returnedCheques) {
	 logger.debug("Entering");
	    return returnedChequeDAO.fetchReturnedCheques(returnedCheques);
	   
    }

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReturnedChequeDAO getReturnedChequeDAO() {
		return returnedChequeDAO;
	}

	public void setReturnedChequeDAO(ReturnedChequeDAO returnedChequeDAO) {
		this.returnedChequeDAO = returnedChequeDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	
}
