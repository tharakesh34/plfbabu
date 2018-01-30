/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CommitmentServiceImpl.java  	                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.commitment.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.commitment.CommitmentDAO;
import com.pennant.backend.dao.commitment.CommitmentMovementDAO;
import com.pennant.backend.dao.commitment.CommitmentRateDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.collateral.impl.CollateralAssignmentValidation;
import com.pennant.backend.service.collateral.impl.DocumentDetailValidation;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class CommitmentServiceImpl extends GenericService<Commitment> implements CommitmentService {
	private static final Logger		logger	= Logger.getLogger(CommitmentServiceImpl.class);

	private PostingsPreparationUtil				postingsPreparationUtil;

	//Service Classes
	private CheckListDetailService				checkListDetailService;
	private CustomerDetailsService				customerDetailsService;

	// DAO Classes
	private AuditHeaderDAO						auditHeaderDAO;
	private CommitmentDAO						commitmentDAO;
	private CommitmentRateDAO					commitmentRateDAO;
	private CommitmentMovementDAO				commitmentMovementDAO;
	private FinanceCheckListReferenceDAO		financeCheckListReferenceDAO;
	private FinanceReferenceDetailDAO			financeReferenceDetailDAO;
	private DocumentDetailsDAO					documentDetailsDAO;
	private DocumentManagerDAO					documentManagerDAO;

	private CustomerDocumentDAO					customerDocumentDAO;
	private FinFlagDetailsDAO 					finFlagDetailsDAO;
	private CollateralAssignmentDAO 			collateralAssignmentDAO;
	private RuleDAO								ruleDAO;
	private LimitDetailDAO 						limitDetailDAO;
	private LimitHeaderDAO 						limitHeaderDAO;

	// Validation Service Classes
	private FlagDetailValidation				flagDetailValidation;
	private CommitmentRateValidation 			commitmentRateValidation;
	private CollateralAssignmentValidation 		collateralAssignmentValidation;
	private DocumentDetailValidation			documentDetailValidation;

	private LimitManagement						limitManagement;

	public CommitmentServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the commitmentDAO
	 */
	public CommitmentDAO getCommitmentDAO() {
		return commitmentDAO;
	}

	/**
	 * @return the commitment
	 */
	@Override
	public Commitment getCommitment() {
		return getCommitmentDAO().getCommitment();
	}

	/**
	 * @return the commitment for New Record
	 */
	@Override
	public Commitment getNewCommitment() {
		return getCommitmentDAO().getNewCommitment();
	}

	// Validation Service Classes

	/**
	 * Commitment Flag Validation
	 * @return
	 */
	public FlagDetailValidation getFlagDetailValidation() {
		if (flagDetailValidation == null) {
			this.flagDetailValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.flagDetailValidation;
	}

	/**
	 * Commitment Review Rates Validation
	 * @return
	 */
	public CommitmentRateValidation getCommitmentRateValidation() {

		if (commitmentRateValidation == null) {
			this.commitmentRateValidation = new CommitmentRateValidation(commitmentRateDAO);
		}
		return this.commitmentRateValidation;
	}

	/**
	 * Commitment Document Validations
	 * @return
	 */
	public DocumentDetailValidation getDocumentDetailValidation() {
		if (documentDetailValidation == null) {
			this.documentDetailValidation = new DocumentDetailValidation(documentDetailsDAO, documentManagerDAO, customerDocumentDAO);
		}
		return documentDetailValidation;
	}

	public CollateralAssignmentValidation getCollateralAssignmentValidation() {
		if (collateralAssignmentValidation == null) {
			this.collateralAssignmentValidation = new CollateralAssignmentValidation(collateralAssignmentDAO);
		}
		return collateralAssignmentValidation;
	}

	/**
	 * getApprovedCommitmentById fetch the details by using CommitmentDAO's getCommitmentById method . with parameter id
	 * and type as blank. it fetches the approved records from the Commitments.
	 * 
	 * @param id
	 *            (String)
	 * @return Commitment
	 */

	public Commitment getApprovedCommitmentById(String id) {
		return getCommitmentDAO().getCommitmentById(id, "_AView");
	}

	public int getCmtAmountCount(long custID) {
		return getCommitmentDAO().getCmtAmountCount(custID);
	}

	/**
	 * getCommitmentById fetch the details by using CommitmentDAO's getCommitmentById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Commitment
	 */

	@Override
	public Commitment getCommitmentByCmtRef(String cmtReference, String nextRoleCode, boolean isEnquiry) {
		logger.debug("Entering");

		Commitment commitment = getCommitmentDAO().getCommitmentById(cmtReference, "_View");
		if (commitment != null) {

			//Commitment Movements
			commitment.setCommitmentMovement(getCommitmentMovementDAO().getCommitmentMovementById(cmtReference, "_View"));

			//Flag Details
			commitment.setCmtFlagDetailList(getFinFlagDetailsDAO().getFinFlagsByFinRef(cmtReference,CommitmentConstants.MODULE_NAME, "_View"));

			//Commitment Review Rates
			commitment.setCommitmentRateList(getCommitmentRateDAO().getCommitmentRatesByCmtRef(cmtReference, "_View"));

			// Customer Details
			commitment.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(commitment.getCustID(), true, "_View"));

			// Collateral Details
			commitment.setCollateralAssignmentList(getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(cmtReference, CommitmentConstants.MODULE_NAME, "_View"));

			// Not Required Other Process details for the Enquiry
			if (!isEnquiry) {
				// Customer Details
				commitment.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(commitment.getCustID(), true, "_View"));

				// Document Details
				List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(cmtReference,	
						CommitmentConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_View");
				if (commitment.getDocuments() != null && !commitment.getDocuments().isEmpty()) {
					commitment.getDocuments().addAll(documentList);
				} else {
					commitment.setDocuments(documentList);
				}

				// Agreement Details & Check List Details
				if(StringUtils.isNotEmpty(commitment.getRecordType()) && 
						!StringUtils.equals(commitment.getRecordType(), PennantConstants.RECORD_TYPE_UPD) &&
						!StringUtils.equals(commitment.getRecordType(), PennantConstants.RECORD_TYPE_DEL)){
					commitment = getProcessEditorDetails(commitment, nextRoleCode, FinanceConstants.FINSER_EVENT_ORG);
				}
			}
		}

		logger.debug("Leaving");
		return commitment;
	}

	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public Commitment getProcessEditorDetails(Commitment commitment, String nextRoleCode, String procEdtEvent) {
		logger.debug("Entering");

		boolean isCustExist = true;
		String ctgType = StringUtils.trimToEmpty(commitment.getCustomerDetails().getCustomer().getCustCtgCode());
		if (StringUtils.isEmpty(ctgType)) {
			isCustExist = false;
		}

		List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<FinanceReferenceDetail>(1);

		// Fetch Total Process editor Details 
		List<FinanceReferenceDetail> cmtRefDetails = getFinanceReferenceDetailDAO().getFinanceProcessEditorDetails(CommitmentConstants.WF_NEWCOMMITMENT, 
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent, "_CMTVIEW");

		if (cmtRefDetails != null && !cmtRefDetails.isEmpty()) {
			for (FinanceReferenceDetail finrefDetail : cmtRefDetails) {
				if ((!finrefDetail.isIsActive()) || StringUtils.isEmpty(finrefDetail.getLovDescRefDesc())) {
					continue;
				}
				if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_CHECKLIST) {
					if (StringUtils.trimToEmpty(finrefDetail.getShowInStage()).contains((nextRoleCode + ","))) {
						checkListdetails.add(finrefDetail);
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_AGREEMENT) {
					if (StringUtils.trimToEmpty(finrefDetail.getMandInputInStage()).contains((nextRoleCode + ","))) {
						aggrementList.add(finrefDetail);
					}
				}
			}
		}
		//Agreement Details	
		commitment.setAggrements(aggrementList);

		if (isCustExist) {
			//Check list Details
			getCheckListDetailService().fetchCommitmentCheckLists(commitment, checkListdetails);
		}

		logger.debug("Leaving");
		return commitment;
	}


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Commitments/Commitments_Temp by
	 * using CommitmentDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using CommitmentDAO's update method 3) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Commitments/Commitments_Temp by
	 * using CommitmentDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using CommitmentDAO's update method 3) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		if (commitment.isWorkflow()) {
			tableType = "_Temp";
		}

		if (commitment.isNew()) {

			//Commitment
			getCommitmentDAO().save(commitment, tableType);

			//Commitment Movement
			auditHeader.getAuditDetail().setModelData(commitment);
			auditHeader.setAuditReference(commitment.getCmtReference());

			if (StringUtils.isBlank(commitment.getRecordType()) || StringUtils.trimToEmpty(commitment.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
				commitment.getCommitmentMovement().setMovementType("NC");
			} else {
				commitment.getCommitmentMovement().setMovementType("MC");
			}

			commitment.getCommitmentMovement().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getCommitmentMovementDAO().save(commitment.getCommitmentMovement(), tableType);
		} else {

			//Commitment
			getCommitmentDAO().update(commitment, tableType);

			//Commitment Movement
			getCommitmentMovementDAO().update(commitment.getCommitmentMovement(), tableType);

			/*if (commitment.getRecordStatus().equalsIgnoreCase("Approved")
			        && commitment.getRecordType() != null) {
				commitment.getCommitmentMovement().setMovementType("NC");
				getCommitmentMovementDAO().save(commitment.getCommitmentMovement(), tableType);
			} else {
				commitment.getCommitmentMovement().setMovementType("MC");
			}*/
		}

		//Commitment Movements
		if (commitment.getCommitmentMovement() != null) {
			AuditDetail details = commitment.getCommitmentMovement().getLovDescAuditDetailMap().get("CommitmentMovement");
			details = processingCommitmentMovementList(details, tableType, 0);
			auditDetails.add(details);
		}

		// Commitment Flag Details
		if (commitment.getCmtFlagDetailList() != null && commitment.getCmtFlagDetailList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("FlagDetails");
			details = processingCmtFlagDetailList(details, commitment.getCmtReference(), tableType);
			auditDetails.addAll(details);
		}

		// Commitment Review Rates
		if (commitment.getCommitmentRateList() != null && commitment.getCommitmentRateList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CommitmentRate");
			details = processingCommitmentRatesList(details, commitment.getCmtReference(), tableType);
			auditDetails.addAll(details);
		}

		// Commitment CheckLists
		if (commitment.getCommitmentCheckLists() != null && !commitment.getCommitmentCheckLists().isEmpty()) {
			auditDetails.addAll(processingCheckListDetailsList(commitment, tableType));
		}

		// Collateral Details 
		if (commitment.getCollateralAssignmentList() != null && !commitment.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CollateralAssignments");
			details = processingCollateralAssignmentList(details, commitment.getCmtReference(), tableType);
			auditDetails.addAll(details);
		}

		//Commitment documents
		if (commitment.getDocuments() != null && commitment.getDocuments().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, commitment, tableType);
			auditDetails.addAll(details);
		}

		//Add Audit
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Commitments by using CommitmentDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCommitments by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();
		getCommitmentDAO().delete(commitment, "");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}



	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCommitmentDAO().delete with
	 * parameters commitment,"" b) NEW Add new record in to main table by using getCommitmentDAO().save with parameters
	 * commitment,"" c) EDIT Update record in the main table by using getCommitmentDAO().update with parameters
	 * commitment,"" 3) Delete the record from the workFlow table by using getCommitmentDAO().delete with parameters
	 * commitment,"_Temp" 4) Audit the record in to AuditHeader and AdtCommitments by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtCommitments by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		long linkTranid = 0;
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Commitment commitment = new Commitment();
		BeanUtils.copyProperties((Commitment) auditHeader.getAuditDetail().getModelData(), commitment);

		if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

			tranType = PennantConstants.TRAN_DEL;
			getCommitmentDAO().delete(commitment, "");
			auditDetails.addAll(listDeletion(commitment, "", auditHeader.getAuditTranType()));

		} else {
			commitment.setRoleCode("");
			commitment.setNextRoleCode("");
			commitment.setTaskId("");
			commitment.setNextTaskId("");
			commitment.setWorkflowId(0);

			if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				commitment.setRecordType("");

				List<Object> returnList = processPosting(commitment, AccountEventConstants.ACCEVENT_NEWCMT);

				if (returnList != null && (returnList.get(3) == null)) {
					linkTranid = (Long) returnList.get(1);
					if (commitment.isOpenAccount()) {
						commitment.setCmtAccount((String) returnList.get(2));
					}
				} else {

					String errorMessage = StringUtils.trimToEmpty(returnList.get(3).toString());
					auditHeader.setErrorDetails(new ErrorDetail(errorMessage.substring(0, errorMessage.indexOf('-')).trim(), 
							errorMessage.substring(errorMessage.indexOf('-') + 1).trim(), null));
					return auditHeader;
				}

				getCommitmentDAO().save(commitment, "");

			} else {

				tranType = PennantConstants.TRAN_UPD;
				commitment.setRecordType("");

				List<Object> returnList = processPosting(commitment, AccountEventConstants.ACCEVENT_MNTCMT);
				if (returnList != null && (returnList.get(3) == null)) {
					linkTranid = (Long) returnList.get(1);
				} else {

					String errorMessage = StringUtils.trimToEmpty(returnList.get(3).toString());
					auditHeader.setErrorDetails(new ErrorDetail(errorMessage.substring(0, errorMessage.indexOf('-')).trim(), 
							errorMessage.substring(errorMessage.indexOf('-') + 1).trim(), null));
					return auditHeader;
				}

				getCommitmentDAO().update(commitment, "");
			}
		}

		// Commitment Movements
		if (commitment.getCommitmentMovement() != null) {
			AuditDetail details = commitment.getCommitmentMovement().getLovDescAuditDetailMap().get("CommitmentMovement");
			details = processingCommitmentMovementList(details, "", linkTranid);
			auditDetails.add(details);
		}

		// Commitment Flag Details
		if (commitment.getCmtFlagDetailList() != null && commitment.getCmtFlagDetailList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("FlagDetails");
			details = processingCmtFlagDetailList(details, commitment.getCmtReference(),"");
			auditDetails.addAll(details);
		}

		// Commitment Review Rates
		if (commitment.getCommitmentRateList() != null && commitment.getCommitmentRateList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CommitmentRate");
			details = processingCommitmentRatesList(details, commitment.getCmtReference(), "");
			auditDetails.addAll(details);
		}

		// Commitment CheckLists details
		if (commitment.getCommitmentCheckLists() != null && !commitment.getCommitmentCheckLists().isEmpty()) {
			auditDetails.addAll(processingCheckListDetailsList(commitment, ""));
		}

		// Collateral Assignment Details
		if (commitment.getCollateralAssignmentList() != null && commitment.getCollateralAssignmentList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CollateralAssignments");
			details = processingCollateralAssignmentList(details, commitment.getCmtReference(), "");
			auditDetails.addAll(details);
		}

		// Commitment Document Details
		List<DocumentDetails> documentsList = commitment.getDocuments();
		if (documentsList != null && documentsList.size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, commitment, "");
			auditDetails.addAll(details);
			listDocDeletion(commitment, "_Temp");
		}

		getCommitmentDAO().delete(commitment, "_Temp");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "_Temp", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, commitment.getBefImage(), commitment));

		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commitment);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, commitment.getBefImage(), commitment));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCommitmentDAO().delete with parameters commitment,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtCommitments by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommitmentDAO().delete(commitment, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, commitment.getBefImage(), commitment));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(commitment, "_Temp", auditHeader.getAuditTranType())));

		//Add Audit
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the next process
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = commitment.getUserDetails().getLanguage();

		// Flag details Validation
		if (commitment.getCmtFlagDetailList() != null && !commitment.getCmtFlagDetailList().isEmpty()) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("FlagDetails");
			details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Commitment Review Rates
		if (commitment.getCommitmentRateList() != null && commitment.getCommitmentRateList().size() > 0) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CommitmentRate");
			details = getCommitmentRateValidation().commitmentRateListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Collateral Assignments details
		if (commitment.getCollateralAssignmentList() != null && !commitment.getCollateralAssignmentList().isEmpty()) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("CollateralAssignments");
			details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		//Commitment Check List Details
		List<FinanceCheckListReference> cmtCheckList = commitment.getCommitmentCheckLists();
		if (cmtCheckList != null && !cmtCheckList.isEmpty()) {
			List<AuditDetail> auditDetailList;
			auditDetailList = getCheckListDetailService().validate(commitment.getAuditDetailMap().get("CheckListDetails"), method, usrLanguage);
			auditDetails.addAll(auditDetailList);
		}

		//Commitment Document details Validation
		List<DocumentDetails> documentDetailsList = commitment.getDocuments();
		if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
			List<AuditDetail> details = commitment.getAuditDetailMap().get("DocumentDetails");
			details = getDocumentDetailValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getCommitmentDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Commitment commitment = (Commitment) auditDetail.getModelData();

		Commitment tempCommitment = null;
		if (commitment.isWorkflow()) {
			tempCommitment = getCommitmentDAO().getCommitmentById(commitment.getId(), "_Temp");
		}
		Commitment befCommitment = getCommitmentDAO().getCommitmentById(commitment.getId(), "");
		Commitment oldCommitment = commitment.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = commitment.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + " : " + valueParm[0];
		String[] errParmFacRef = new String[1];
		String[] valueParmFacRef = new String[1];
		errParmFacRef[0] = PennantJavaUtil.getLabel("label_FacilityRef") + " : " + commitment.getFacilityRef() + " For";
		valueParmFacRef[0] = PennantJavaUtil.getLabel("label_custID") + " : " + commitment.getCustCIF();

		if (commitment.isNew()) { // for New record or new record into work flow

			if (!commitment.isWorkflow()) {// With out Work flow only new records  
				if (befCommitment != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCommitment != null || tempCommitment != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCommitment == null || tempCommitment != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
			Commitment facilityRef = getCommitmentDAO().getCommitmentByFacilityRef(commitment.getId(), "_AView");
			if (facilityRef != null) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"41001", errParmFacRef, valueParmFacRef), usrLanguage));
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commitment.isWorkflow()) { // With out Work flow for update and delete

				if (befCommitment == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldCommitment != null && !oldCommitment.getLastMntOn().equals(befCommitment.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempCommitment == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (oldCommitment != null && !oldCommitment.getLastMntOn().equals(tempCommitment.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !commitment.isWorkflow()) {
			auditDetail.setBefImage(befCommitment);
		}

		return auditDetail;
	}

	@Override
	public List<Rule> getRuleByModuleAndEvent(String module, String event) {
		return getRuleDAO().getRuleByModuleAndEvent(module, event, "");
	}

	private List<Object> processPosting(Commitment commitment, String event) {
		logger.debug("Entering ");

		List<Object> returnResultList = null;

		try {
			//Preparation for Commitment Postings
			AEEvent aeEvent = new AEEvent();
			Date dateAppDate = DateUtility.getAppDate();
			if (AccountEventConstants.ACCEVENT_MNTCMT.equals(event)) {
				Commitment prvCommitment = getCommitmentDAO().getCommitmentById(commitment.getId(), "");
				BigDecimal diffAmount = commitment.getCmtAmount().subtract(prvCommitment.getCmtAmount());
				Commitment tempCommitment = new Commitment();
				BeanUtils.copyProperties(commitment, tempCommitment);
				tempCommitment.setCmtAmount(diffAmount);
				aeEvent = getPostingsPreparationUtil().processCmtPostingDetails(tempCommitment, dateAppDate, event);
				getLimitManagement().processCommitmentLimit(tempCommitment, false, LimitConstants.BLOCK);
			} else {
				aeEvent = getPostingsPreparationUtil().processCmtPostingDetails(commitment, dateAppDate, event);
				getLimitManagement().processCommitmentLimit(commitment, false, LimitConstants.BLOCK);
			}

		} catch (InterfaceException e) {
			logger.debug(e);
			returnResultList = new ArrayList<Object>();
			returnResultList.add(false);
			returnResultList.add(0);
			returnResultList.add(e.getErrorMessage());
		} catch (IllegalAccessException e) {
			logger.debug(e);
		} catch (InvocationTargetException e) {
			logger.debug(e);
		}

		logger.debug("Leaving ");
		return returnResultList;
	}

	@Override
	public List<AvailCommitment> getCommitmentListByCustId(long custId) {
		return getCommitmentDAO().getCommitmentListByCustId(custId, "_AView");
	}

	@Override
	public Map<String, Object> getAmountSummary(long custID) {
		return getCommitmentDAO().getAmountSummary(custID);
	}

	@Override
	public List<CommitmentSummary> getCommitmentSummary(long custID) {
		return getCommitmentDAO().getCommitmentSummary(custID);
	}

	@Override
	public LimitDetails getLimitLineByDetailId(long limitLineId) {
		return getLimitDetailDAO().getLimitLineByDetailId(limitLineId, "_AView");
	}

	@Override
	public LimitHeader getLimitHeaderByCustomerId(long customerId) {
		return getLimitHeaderDAO().getLimitHeaderByCustomerId(customerId, "_AView");
	}

	/**
	 * Get record count from commitment table
	 * 
	 * @param id (commitment Reference)
	 * @return Integer
	 */
	@Override
	public int getCommitmentCountById(String id) {
		return getCommitmentDAO().getCommitmentCountById(id, "");
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		HashMap<String, AuditDetail> auditDetailMap1 = new HashMap<String, AuditDetail>();//TODO

		Commitment commitment = (Commitment) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (commitment.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (commitment.getCommitmentMovement() != null) {

			auditDetails = setCommitmentMovementAuditData(commitment, auditTranType, method);
			for (AuditDetail auditDetail : auditDetails) {
				auditDetailMap1.put("CommitmentMovement", auditDetail);
			}
			//auditDetailMap.put("CommitmentMovement", setCommitmentMovementAuditData(commitmentmovement,auditTranType,method));
			//auditDetails.add(auditDetailMap.get("" + ""));
		}

		// Commitment Flag details
		if (commitment.getCmtFlagDetailList() != null && commitment.getCmtFlagDetailList().size() > 0) {
			auditDetailMap.put("FlagDetails", setCmtFlagAuditData(commitment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FlagDetails"));
		}

		// Commitment Review Rates
		if (commitment.getCommitmentRateList() != null && commitment.getCommitmentRateList().size() > 0) {
			auditDetailMap.put("CommitmentRate", setCommitmentRateAuditData(commitment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CommitmentRate"));
		}

		//Commitment Check List Details
		List<FinanceCheckListReference> commitmentCheckLists = commitment.getCommitmentCheckLists();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (commitmentCheckLists != null && !commitmentCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(commitment, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		} else {
			String tableType = "_Temp";
			if (commitment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			commitmentCheckLists = getCheckListDetailService().getCheckListByFinRef(commitment.getCmtReference(), tableType);				
			commitment.setCommitmentCheckLists(commitmentCheckLists);

			if (commitmentCheckLists != null && !commitmentCheckLists.isEmpty()) {
				auditDetailMap.put("CheckListDetails", setCheckListsAuditData(commitment, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("CheckListDetails"));
			}
		}

		// Collateral Assignment Details
		if (commitment.getCollateralAssignmentList() != null && commitment.getCollateralAssignmentList().size() > 0) {
			auditDetailMap.put("CollateralAssignments", setCollateralAssignmentAuditData(commitment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralAssignments"));
		}

		//Commitment Document Details
		if (commitment.getDocuments() != null && commitment.getDocuments().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(commitment, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		commitment.getCommitmentMovement().setLovDescAuditDetailMap(auditDetailMap1);
		commitment.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(commitment);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCommitmentMovementAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CommitmentMovement commitmentMovement = commitment.getCommitmentMovement();

		if (StringUtils.isNotEmpty(commitmentMovement.getRecordType())) {

			CommitmentMovement object = new CommitmentMovement();
			String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

			commitmentMovement.setCmtReference(commitment.getCmtReference());
			commitmentMovement.setWorkflowId(commitment.getWorkflowId());

			boolean isRcdType = false;

			if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (commitment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				commitmentMovement.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			commitmentMovement.setRecordStatus(commitment.getRecordStatus());
			commitmentMovement.setUserDetails(commitment.getUserDetails());
			commitmentMovement.setLastMntOn(commitment.getLastMntOn());
			commitmentMovement.setLastMntBy(commitment.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, +1, fields[0], fields[1], "CommitmentMovement", commitmentMovement));
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Methods for Creating List Commitment Flag of Audit Details with detailed fields
	 * 
	 * @param commitment
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCmtFlagAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinFlagsDetail cmtFlag = new FinFlagsDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(cmtFlag, cmtFlag.getExcludeFields());

		for (int i = 0; i < commitment.getCmtFlagDetailList().size(); i++) {
			FinFlagsDetail flagDetail = commitment.getCmtFlagDetailList().get(i);

			if (StringUtils.isEmpty(flagDetail.getRecordType())) {
				continue;
			}

			flagDetail.setReference(commitment.getCmtReference());
			flagDetail.setWorkflowId(commitment.getWorkflowId());

			boolean isRcdType = false;

			if (flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				flagDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				flagDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (commitment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				flagDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType ) {
				flagDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| flagDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			flagDetail.setRecordStatus(commitment.getRecordStatus());
			flagDetail.setUserDetails(commitment.getUserDetails());
			flagDetail.setLastMntOn(commitment.getLastMntOn());
			flagDetail.setLastMntBy(commitment.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], flagDetail.getBefImage(), flagDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param Commitment Rates
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCommitmentRateAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		CommitmentRate cmtRate = new CommitmentRate();
		String[] fields = PennantJavaUtil.getFieldDetails(cmtRate, cmtRate.getExcludeFields());

		for (int i = 0; i < commitment.getCommitmentRateList().size(); i++) {
			CommitmentRate commitmentRate = commitment.getCommitmentRateList().get(i);

			if (StringUtils.isEmpty(commitmentRate.getRecordType())) {
				continue;
			}

			commitmentRate.setCmtReference(commitment.getCmtReference());
			commitmentRate.setWorkflowId(commitment.getWorkflowId());

			boolean isRcdType = false;

			if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (commitment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType ) {
				commitmentRate.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			commitmentRate.setRecordStatus(commitment.getRecordStatus());
			commitmentRate.setUserDetails(commitment.getUserDetails());
			commitmentRate.setLastMntOn(commitment.getLastMntOn());
			commitmentRate.setLastMntBy(commitment.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], commitmentRate.getBefImage(), commitmentRate));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for checkList Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> setCheckListsAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceCheckListReference object = new FinanceCheckListReference();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < commitment.getCommitmentCheckLists().size(); i++) {
			FinanceCheckListReference cmtChekListRef = commitment.getCommitmentCheckLists().get(i);

			cmtChekListRef.setFinReference(commitment.getCmtReference());

			if (StringUtils.isEmpty(cmtChekListRef.getRecordType())) {
				continue;
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (cmtChekListRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					cmtChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (cmtChekListRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if (StringUtils.trimToEmpty(method).equals("doApprove")) {
				cmtChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}

			cmtChekListRef.setRecordStatus("");
			cmtChekListRef.setUserDetails(commitment.getUserDetails());
			cmtChekListRef.setLastMntOn(commitment.getLastMntOn());
			cmtChekListRef.setLastMntBy(commitment.getLastMntBy());
			cmtChekListRef.setWorkflowId(commitment.getWorkflowId());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], cmtChekListRef.getBefImage(), cmtChekListRef));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param detail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setCollateralAssignmentAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();		
		CollateralAssignment assignment = new CollateralAssignment();
		String[] fields = PennantJavaUtil.getFieldDetails(assignment, assignment.getExcludeFields());

		for (int i = 0; i < commitment.getCollateralAssignmentList().size(); i++) {
			CollateralAssignment collateralAssignment = commitment.getCollateralAssignmentList().get(i);

			if (StringUtils.isEmpty(collateralAssignment.getRecordType())) {
				continue;
			}

			collateralAssignment.setReference(commitment.getCmtReference());
			collateralAssignment.setWorkflowId(commitment.getWorkflowId());

			boolean isRcdType = false;

			if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (commitment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				collateralAssignment.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			collateralAssignment.setRecordStatus(commitment.getRecordStatus());
			collateralAssignment.setUserDetails(commitment.getUserDetails());
			collateralAssignment.setLastMntBy(commitment.getLastMntBy());
			collateralAssignment.setLastMntOn(commitment.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collateralAssignment.getBefImage(), collateralAssignment));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param commitment
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(Commitment commitment, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		DocumentDetails object = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < commitment.getDocuments().size(); i++) {
			DocumentDetails documentDetail = commitment.getDocuments().get(i);

			if (StringUtils.isEmpty(documentDetail.getRecordType())) {
				continue;
			}

			documentDetail.setReferenceId(commitment.getCmtReference());
			documentDetail.setDocModule(CommitmentConstants.MODULE_NAME);
			documentDetail.setWorkflowId(commitment.getWorkflowId());

			boolean isRcdType = false;

			if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (commitment.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				documentDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetail.setRecordStatus(commitment.getRecordStatus());
			documentDetail.setUserDetails(commitment.getUserDetails());
			documentDetail.setLastMntBy(commitment.getLastMntBy());
			documentDetail.setLastMntOn(commitment.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(), documentDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Commitment Movements
	 * 
	 * @param details
	 * @param type
	 * @param custId
	 * @return
	 */
	private AuditDetail processingCommitmentMovementList(AuditDetail details, String type, long linkTranid) {
		logger.debug("Entering ");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		CommitmentMovement commitmentMovement = (CommitmentMovement) details.getModelData();

		saveRecord = false;
		updateRecord = false;
		deleteRecord = false;
		approveRec = false;
		String rcdType = "";
		String recordStatus = "";

		if (StringUtils.isEmpty(type)) {
			approveRec = true;
			commitmentMovement.setVersion(commitmentMovement.getVersion() + 1);
			commitmentMovement.setMovementOrder(commitmentMovement.getMovementOrder() + 1);
			commitmentMovement.setRoleCode("");
			commitmentMovement.setNextRoleCode("");
			commitmentMovement.setTaskId("");
			commitmentMovement.setNextTaskId("");
		}
		commitmentMovement.setWorkflowId(0);

		if (linkTranid != 0 && linkTranid != Long.MIN_VALUE) {
			commitmentMovement.setLinkedTranId(linkTranid);
		}

		if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
			deleteRecord = true;
		} else if (commitmentMovement.isNewRecord()) {
			saveRecord = true;
			if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				commitmentMovement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}

		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
			if (approveRec) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
			updateRecord = true;
		} else if (commitmentMovement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
			if (approveRec) {
				deleteRecord = true;
			} else if (commitmentMovement.isNew()) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		}

		if (approveRec) {
			rcdType = commitmentMovement.getRecordType();
			recordStatus = commitmentMovement.getRecordStatus();
			commitmentMovement.setRecordType("");
			commitmentMovement.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		}
		if (saveRecord) {

			getCommitmentMovementDAO().save(commitmentMovement, type);
		}

		if (updateRecord) {
			getCommitmentMovementDAO().update(commitmentMovement, type);
		}

		if (deleteRecord) {
			getCommitmentMovementDAO().delete(commitmentMovement, type);
		}

		if (approveRec) {
			commitmentMovement.setRecordType(rcdType);
			commitmentMovement.setRecordStatus(recordStatus);
		}

		details.setModelData(commitmentMovement);

		logger.debug("Leaving ");
		return details;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param commitment
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCmtFlagDetailList(List<AuditDetail> auditDetails, String cmtReference, String type) {
		logger.debug("Entering");


		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();

			saveRecord 			= false;
			updateRecord 		= false;
			deleteRecord 		= false;
			approveRec 			= false;
			String rcdType		= "";
			String recordStatus	= "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
			}

			finFlagsDetail.setReference(cmtReference);
			finFlagsDetail.setWorkflowId(0);

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getFinFlagDetailsDAO().save(finFlagsDetail, type);
			}

			if (updateRecord) {
				getFinFlagDetailsDAO().update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				getFinFlagDetailsDAO().delete(finFlagsDetail.getReference(),finFlagsDetail.getFlagCode(),finFlagsDetail.getModuleName(),  type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingCommitmentRatesList(List<AuditDetail> auditDetails, String cmtReference, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CommitmentRate commitmentRate = (CommitmentRate) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				commitmentRate.setRoleCode("");
				commitmentRate.setNextRoleCode("");
				commitmentRate.setTaskId("");
				commitmentRate.setNextTaskId("");
			}

			commitmentRate.setCmtReference(cmtReference);
			commitmentRate.setWorkflowId(0);

			if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (commitmentRate.isNewRecord()) {
				saveRecord = true;
				if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					commitmentRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {	
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (commitmentRate.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (commitmentRate.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = commitmentRate.getRecordType();
				recordStatus = commitmentRate.getRecordStatus();
				commitmentRate.setRecordType("");
				commitmentRate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				commitmentRateDAO.save(commitmentRate, type);
			}

			if (updateRecord) {
				commitmentRateDAO.update(commitmentRate, type);
			}

			if (deleteRecord) {
				commitmentRateDAO.delete(commitmentRate, type);
			}

			if (approveRec) {
				commitmentRate.setRecordType(rcdType);
				commitmentRate.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(commitmentRate);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List Details
	 * 
	 * @param auditDetails
	 * @param commitment
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCheckListDetailsList(Commitment commitment, String tableType) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = commitment.getAuditDetailMap().get("CheckListDetails");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference cmtChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();

			cmtChecklistRef.setWorkflowId(0);

			if (StringUtils.isEmpty(tableType)) {
				cmtChecklistRef.setVersion(cmtChecklistRef.getVersion() + 1);
				cmtChecklistRef.setRoleCode("");
				cmtChecklistRef.setNextRoleCode("");
				cmtChecklistRef.setTaskId("");
				cmtChecklistRef.setNextTaskId("");
			}

			if (cmtChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				cmtChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				getFinanceCheckListReferenceDAO().save(cmtChecklistRef, tableType);
			} else if (cmtChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				getFinanceCheckListReferenceDAO().delete(cmtChecklistRef, tableType);
			} else if (cmtChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				getFinanceCheckListReferenceDAO().update(cmtChecklistRef, tableType);
			}
			auditDetails.get(i).setModelData(cmtChecklistRef);
		}

		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingCollateralAssignmentList(List<AuditDetail> auditDetails, String cmtReference, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			CollateralAssignment collateralAssignment = (CollateralAssignment) auditDetails.get(i).getModelData();

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				collateralAssignment.setRoleCode("");
				collateralAssignment.setNextRoleCode("");
				collateralAssignment.setTaskId("");
				collateralAssignment.setNextTaskId("");
			}

			collateralAssignment.setModule(CommitmentConstants.MODULE_NAME);
			collateralAssignment.setReference(cmtReference);
			collateralAssignment.setWorkflowId(0);

			if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (collateralAssignment.isNewRecord()) {
				saveRecord = true;
				if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					collateralAssignment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (collateralAssignment.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (collateralAssignment.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = collateralAssignment.getRecordType();
				recordStatus = collateralAssignment.getRecordStatus();
				collateralAssignment.setRecordType("");
				collateralAssignment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getCollateralAssignmentDAO().save(collateralAssignment, type);
			}

			if (updateRecord) {
				getCollateralAssignmentDAO().update(collateralAssignment, type);
			}

			if (deleteRecord) {
				getCollateralAssignmentDAO().delete(collateralAssignment, type);
			}

			if (approveRec) {
				collateralAssignment.setRecordType(rcdType);
				collateralAssignment.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(collateralAssignment);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param commitment
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, Commitment commitment, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetail = (DocumentDetails) auditDetails.get(i).getModelData();

			if (StringUtils.isBlank(documentDetail.getRecordType())) {
				continue;
			}

			if (!documentDetail.isDocIsCustDoc()) {

				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;

				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetail.setRoleCode("");
					documentDetail.setNextRoleCode("");
					documentDetail.setTaskId("");
					documentDetail.setNextTaskId("");
				}
				documentDetail.setWorkflowId(0);

				documentDetail.setLastMntBy(commitment.getLastMntBy());

				if (documentDetail.isDocIsCustDoc()) {
					approveRec = true;
				}

				if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetail.isNewRecord()) {
					saveRecord = true;
					if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						documentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						documentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (documentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = documentDetail.getRecordType();
					recordStatus = documentDetail.getRecordStatus();
					documentDetail.setRecordType("");
					documentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.isEmpty(documentDetail.getReferenceId())) {
						documentDetail.setReferenceId(commitment.getCmtReference());
					}
					documentDetail.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);

					// Save the document (documentDetail object) into DocumentManagerTable using documentManagerDAO.save(?) get the long Id.
					// This will be used in the getDocumentDetailsDAO().save, Update & delete methods
					if (documentDetail.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetail.getDocImage());
						documentDetail.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					// Pass the docRefId here to save this in place of docImage column. Or add another column for now to save this.
					getDocumentDetailsDAO().save(documentDetail, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetail.getDocRefId()
					if (documentDetail.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetail.getDocImage());
						documentDetail.setDocRefId(getDocumentManagerDAO().save(documentManager));
					}
					getDocumentDetailsDAO().update(documentDetail, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						getDocumentDetailsDAO().delete(documentDetail, type);
					}
				}

				if (approveRec) {
					documentDetail.setFinEvent("");
					documentDetail.setRecordType(rcdType);
					documentDetail.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetail);
			} else {
				CustomerDocument custdoc = getCustomerDocument(documentDetail, commitment);
				if (custdoc.isNewRecord()) {
					getCustomerDocumentDAO().save(custdoc, "");
				} else {
					getCustomerDocumentDAO().update(custdoc, "");
				}
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method for Deleting all records related to Commitment in _Temp/Main tables depend on method type
	 * 
	 * @param fee
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(Commitment commitment, String tableType, String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Commitment Movements
		if (commitment.getCommitmentMovement() != null) {
			CommitmentMovement commMovement = commitment.getCommitmentMovement();
			String[] fields = PennantJavaUtil.getFieldDetails(commMovement, commMovement.getExcludeFields());
			auditList.add(new AuditDetail(auditTranType, +1, fields[0], fields[1], commMovement.getBefImage(), commMovement));

			getCommitmentMovementDAO().delete(commitment.getCommitmentMovement(), tableType);
		}

		// Flag Details
		if (commitment.getCmtFlagDetailList() != null && commitment.getCmtFlagDetailList().size() > 0) {

			FinFlagsDetail cmtFlag = new FinFlagsDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(cmtFlag, cmtFlag.getExcludeFields());

			for (FinFlagsDetail cmtFlagDetail : commitment.getCmtFlagDetailList()) {
				auditList.add(new  AuditDetail(auditTranType, auditList.size()+1, fields[0], fields[1], cmtFlagDetail.getBefImage(), cmtFlagDetail));
			}
			getFinFlagDetailsDAO().deleteList(commitment.getCmtReference(),CommitmentConstants.MODULE_NAME, tableType);
		}

		// Commitment Review Rates
		if (commitment.getCommitmentRateList() != null && commitment.getCommitmentRateList().size() > 0) {

			CommitmentRate cmtRate = new CommitmentRate();
			String[] fields = PennantJavaUtil.getFieldDetails(cmtRate, cmtRate.getExcludeFields());

			for (CommitmentRate commitmentRate : commitment.getCommitmentRateList()) {
				auditList.add(new  AuditDetail(auditTranType, auditList.size()+1, fields[0], fields[1], commitmentRate.getBefImage(), commitmentRate));
			}
			getCommitmentRateDAO().deleteByCmtReference(commitment.getCmtReference(), tableType);
		}

		// Commitment CheckLists Details
		if (commitment.getCommitmentCheckLists() != null && commitment.getCommitmentCheckLists().size() > 0) {

			FinanceCheckListReference finCheckList = new FinanceCheckListReference();
			String[] fields = PennantJavaUtil.getFieldDetails(new DocumentDetails(), finCheckList.getExcludeFields());

			for (FinanceCheckListReference finCheckListRef : commitment.getCommitmentCheckLists()) {
				auditList.add(new  AuditDetail(auditTranType, auditList.size()+1, fields[0], fields[1], finCheckListRef.getBefImage(), finCheckListRef));
			}
			getFinanceCheckListReferenceDAO().delete(commitment.getCmtReference(), tableType);
		}

		//Collateral assignment Details
		if(commitment.getCollateralAssignmentList() != null && !commitment.getCollateralAssignmentList().isEmpty()){

			CollateralAssignment collAssignment = new CollateralAssignment();
			String[] fields = PennantJavaUtil.getFieldDetails(collAssignment, collAssignment.getExcludeFields());

			for (CollateralAssignment assignment : commitment.getCollateralAssignmentList()) {
				auditList.add(new  AuditDetail(auditTranType, auditList.size()+1, fields[0], fields[1], assignment.getBefImage(), assignment));
			}
			getCollateralAssignmentDAO().deleteByReference(commitment.getCmtReference(), tableType);
		}

		//Commitment Document Details
		if (commitment.getDocuments() != null && commitment.getDocuments().size() > 0) {

			DocumentDetails document = new DocumentDetails();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());

			for (DocumentDetails docDetail : commitment.getDocuments()) {
				auditList.add(new  AuditDetail(auditTranType, auditList.size()+1, fields[0], fields[1], docDetail.getBefImage(), docDetail));
			}
			getDocumentDetailsDAO().deleteList(commitment.getDocuments(), tableType);
		}

		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Document Details List Maintenance
	 * @param commitment
	 * @param tableType
	 */
	public void listDocDeletion(Commitment commitment, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(commitment.getDocuments()), tableType);
	}

	/**
	 * 
	 * @param documentDetail
	 * @param commitment
	 * @return
	 */
	private CustomerDocument getCustomerDocument(DocumentDetails documentDetail, Commitment commitment) {
		logger.debug("Entering ");

		CustomerDocument customerDocument = getCustomerDocumentDAO().getCustomerDocumentById(commitment.getCustID(), documentDetail.getDocCategory(), "");

		if (customerDocument == null) {
			customerDocument = new CustomerDocument();
			customerDocument.setCustDocIsAcrive(documentDetail.isCustDocIsAcrive());
			customerDocument.setCustDocIsVerified(documentDetail.isCustDocIsVerified());
			customerDocument.setCustDocRcvdOn(documentDetail.getCustDocRcvdOn());
			customerDocument.setCustDocVerifiedBy(documentDetail.getCustDocVerifiedBy());
			customerDocument.setNewRecord(true);
			//Need to add customerid  customerDocument.setCustID(commitment.getCustID());//FIXME
			
		}

		customerDocument.setCustID(commitment.getCustID());
		customerDocument.setLovDescCustCIF(commitment.getCustCIF());
		customerDocument.setLovDescCustShrtName(commitment.getCustShrtName());

		customerDocument.setCustDocTitle(documentDetail.getCustDocTitle());
		customerDocument.setCustDocIssuedCountry(documentDetail.getCustDocIssuedCountry());
		customerDocument.setLovDescCustDocIssuedCountry(documentDetail.getLovDescCustDocIssuedCountry());
		customerDocument.setCustDocIssuedOn(documentDetail.getCustDocIssuedOn());
		customerDocument.setCustDocExpDate(documentDetail.getCustDocExpDate());
		customerDocument.setCustDocSysName(documentDetail.getCustDocSysName());
		customerDocument.setCustDocImage(documentDetail.getDocImage());
		customerDocument.setCustDocType(documentDetail.getDoctype());
		customerDocument.setCustDocCategory(documentDetail.getDocCategory());
		customerDocument.setCustDocName(documentDetail.getDocName());
		customerDocument.setLovDescCustDocCategory(documentDetail.getLovDescDocCategoryName());
		
		if (customerDocument.getDocRefId() <= 0) {
			DocumentManager documentManager = new DocumentManager();
			documentManager.setDocImage(documentDetail.getDocImage());
			customerDocument.setDocRefId(getDocumentManagerDAO().save(documentManager));
		}

		customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerDocument.setRecordType("");
		customerDocument.setUserDetails(documentDetail.getUserDetails());
		customerDocument.setVersion(documentDetail.getVersion());
		customerDocument.setLastMntBy(documentDetail.getLastMntBy());
		customerDocument.setLastMntOn(documentDetail.getLastMntOn());

		logger.debug("Leaving ");
		return customerDocument;
	}

	/**
	 * Common Method for Commitments list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 * @throws InterruptedException
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {

						// check and change below line for Complete code
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses()).invoke(object, object.getClass().getClasses());

						auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}

		logger.debug("Leaving");
		return auditDetailsList;
	}

	// Setters/getters

	/**
	 * @param commitmentDAO
	 *            the commitmentDAO to set
	 */
	public void setCommitmentDAO(CommitmentDAO commitmentDAO) {
		this.commitmentDAO = commitmentDAO;
	}

	public CommitmentMovementDAO getCommitmentMovementDAO() {
		return commitmentMovementDAO;
	}

	public void setCommitmentMovementDAO(CommitmentMovementDAO commitmentMovementDAO) {
		this.commitmentMovementDAO = commitmentMovementDAO;
	}

	public CommitmentRate getNewCommitmentRate() {
		return getCommitmentRateDAO().getNewCommitmentRate();
	}

	public CommitmentRateDAO getCommitmentRateDAO() {
		return commitmentRateDAO;
	}

	public void setCommitmentRateDAO(CommitmentRateDAO commitmentRateDAO) {
		this.commitmentRateDAO = commitmentRateDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}
	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public CheckListDetailService getCheckListDetailService() {
		return checkListDetailService;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public CustomerDocumentDAO getCustomerDocumentDAO() {
		return customerDocumentDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public FinanceCheckListReferenceDAO getFinanceCheckListReferenceDAO() {
		return financeCheckListReferenceDAO;
	}

	public void setFinanceCheckListReferenceDAO(
			FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}
	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}
	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public LimitDetailDAO getLimitDetailDAO() {
		return limitDetailDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}
}