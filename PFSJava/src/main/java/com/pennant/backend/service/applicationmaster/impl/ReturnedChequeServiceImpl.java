package com.pennant.backend.service.applicationmaster.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.returnedCheques.ReturnedChequeDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReturnedChequeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

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

		auditHeader = businessValidation(auditHeader);
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader
				.getAuditDetail().getModelData();
		
		TableType tableType = TableType.MAIN_TAB;
		if (returnedCheque.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
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

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData();
		getReturnedChequeDAO().delete(returnedCheque, TableType.MAIN_TAB);
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
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReturnedChequeDetails returnedCheque = new ReturnedChequeDetails();
		BeanUtils.copyProperties((ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData(), returnedCheque);

		if (returnedCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getReturnedChequeDAO().delete(returnedCheque, TableType.MAIN_TAB);

		} else {
			returnedCheque.setRoleCode("");
			returnedCheque.setNextRoleCode("");
			returnedCheque.setTaskId("");
			returnedCheque.setNextTaskId("");
			returnedCheque.setWorkflowId(0);

			if (returnedCheque.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				returnedCheque.setRecordType("");
				getReturnedChequeDAO().save(returnedCheque, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				returnedCheque.setRecordType("");
				getReturnedChequeDAO().update(returnedCheque, TableType.MAIN_TAB);
			}
		}

		getReturnedChequeDAO().delete(returnedCheque, TableType.TEMP_TAB);
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

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditHeader.getAuditDetail()
		        .getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReturnedChequeDAO().delete(returnedCheque, TableType.TEMP_TAB);

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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
		        auditHeader.getUsrLanguage());
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
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		ReturnedChequeDetails returnedCheque = (ReturnedChequeDetails) auditDetail.getModelData();

		// Check the unique keys.
		if (returnedCheque.isNew() 
				&& PennantConstants.RECORD_TYPE_NEW.equals(returnedCheque.getRecordType())
				&& returnedChequeDAO.isDuplicateKey(returnedCheque.getChequeNo(), returnedCheque.getCustCIF(), 
				returnedCheque.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_ReturnedChequeCustCIF") + ": " + returnedCheque.getChequeNo();
			parameters[1] = PennantJavaUtil.getLabel("label_CheuqeNo") + ": " + returnedCheque.getCustCIF();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

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
