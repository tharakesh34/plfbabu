package com.pennant.backend.service.applicationmaster.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.MMAgreementDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.MMAgreementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class MMAgreementServiceImpl extends GenericService<MMAgreement> implements MMAgreementService {
	private static Logger logger = Logger.getLogger(MMAgreementServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private MMAgreementDAO mMAgreementDAO;
	private CustomerAddresDAO customerAddresDAO;

	public MMAgreementServiceImpl() {
		super();
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
	
	public MMAgreement getMMAgreement() {
		return getmMAgreementDAO().getMMAgreement();
	}
	
	public MMAgreement getNewMMAgreement() {
		return getmMAgreementDAO().getNewMMAgreement();
	}

	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * MMAgreement/MMAgreement_Temp by using MMAgreementDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using MMAgreementDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtMMAgreement by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		MMAgreement aMMAgreement = (MMAgreement) auditHeader.getAuditDetail().getModelData();
		
		if (aMMAgreement.isWorkflow()) {
			tableType="_Temp";
		}

		if (aMMAgreement.isNew()) {
			aMMAgreement.setId(getmMAgreementDAO().save(aMMAgreement,tableType));
			auditHeader.getAuditDetail().setModelData(aMMAgreement);
			auditHeader.setAuditReference(String.valueOf(aMMAgreement.getMMAId()));
		}else{
			getmMAgreementDAO().update(aMMAgreement,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table MMAgreement by using MMAgreementDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtMMAgreement by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		MMAgreement mmAgreement = (MMAgreement) auditHeader.getAuditDetail().getModelData();
		getmMAgreementDAO().delete(mmAgreement,"");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getMMAgreementById fetch the details by using MMAgreementDAO's
	 * getMMAgreementById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getMMAgreementById(long id) {
		return getmMAgreementDAO().getMMAgreementById(id,"_View");
	}
	
	/**
	 * getMMAgreementById fetch the details by using MMAgreementDAO's
	 * getMMAgreementById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return MMAgreement
	 */
	@Override
	public MMAgreement getMMAgreementByIdMMARef(String mMAReference) {
		return getmMAgreementDAO().getMMAgreementByMMARef(mMAReference, "_View");
	}
		
	public CustomerAddres getCustomerAddressDetailsByIdCustID(long custCD) {
		return getCustomerAddresDAO().getCustomerAddresById(custCD,"PERM", "_View");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getMMAgreementDAO().delete with
	 * parameters MMAgreement,"" b) NEW Add new record in to main table by using getMMAgreementDAO().save with
	 * parameters MMAgreement,"" c) EDIT Update record in the main table by using getMMAgreementDAO().update with
	 * parameters MMAgreement,"" 3) Delete the record from the workFlow table by using getMMAgreementDAO().delete with
	 * parameters MMAgreement,"_Temp" 4) Audit the record in to AuditHeader and AdtMMAgreement by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtMMAgreement by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		MMAgreement aMMAgreement = new MMAgreement();
		BeanUtils.copyProperties((MMAgreement) auditHeader.getAuditDetail().getModelData(), aMMAgreement);

		if (aMMAgreement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getmMAgreementDAO().delete(aMMAgreement, "");

		} else {
			aMMAgreement.setRoleCode("");
			aMMAgreement.setNextRoleCode("");
			aMMAgreement.setTaskId("");
			aMMAgreement.setNextTaskId("");
			aMMAgreement.setWorkflowId(0);

			if (aMMAgreement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				aMMAgreement.setRecordType("");
				getmMAgreementDAO().save(aMMAgreement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				aMMAgreement.setRecordType("");
				getmMAgreementDAO().update(aMMAgreement, "");
			}
		}

		getmMAgreementDAO().delete(aMMAgreement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(aMMAgreement);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getMMAgreementDAO().delete with parameters
	 * aMMAgreement,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtMMAgreement by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		MMAgreement aMMAgreement= (MMAgreement) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getmMAgreementDAO().delete(aMMAgreement,"_Temp");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getMMAgreementDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		MMAgreement aMMAgreement = (MMAgreement) auditDetail.getModelData();
		MMAgreement tempMMAgreement = null;
		if (aMMAgreement.isWorkflow()) {
			tempMMAgreement = getmMAgreementDAO().getMMAgreementById(aMMAgreement.getMMAId(),
					 "_Temp");
		}
		MMAgreement befMMAgreement = getmMAgreementDAO().getMMAgreementByMMARef(aMMAgreement.getMMAReference(),"");
		MMAgreement oldMMAgreement = aMMAgreement.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm= new String[1];

		valueParm[0] = aMMAgreement.getMMAReference();
		
		errParm[0]=PennantJavaUtil.getLabel("label_MMAReference")+":"+valueParm[0];

		if (aMMAgreement.isNew()) { // for New record or new record into work flow

			if (!aMMAgreement.isWorkflow()) {// With out Work flow only new records
				if (befMMAgreement != null) { // Record Already Exists in the table 
										// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41008",errParm,null));
				}
			} else { // with work flow
				// if records type is new
				if (befMMAgreement != null || tempMMAgreement != null) { // if records already exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, null));
				} else { // if records not exists in the Main flow table
					if (befMMAgreement == null || tempMMAgreement != null) {
						auditDetail
								.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!aMMAgreement.isWorkflow()) { // With out Work flow for update and delete

				if (befMMAgreement == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldMMAgreement != null
							&& !oldMMAgreement.getLastMntOn().equals(
									befMMAgreement.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempMMAgreement == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempMMAgreement != null && oldMMAgreement != null
						&& !oldMMAgreement.getLastMntOn().equals(
								tempMMAgreement.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !aMMAgreement.isWorkflow()) {
			auditDetail.setBefImage(befMMAgreement);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

	public MMAgreementDAO getmMAgreementDAO() {
	    return mMAgreementDAO;
    }

	public void setmMAgreementDAO(MMAgreementDAO mMAgreementDAO) {
	    this.mMAgreementDAO = mMAgreementDAO;
    }

	public CustomerAddresDAO getCustomerAddresDAO() {
	    return customerAddresDAO;
    }

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
	    this.customerAddresDAO = customerAddresDAO;
    }


	
}
