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
 * FileName    		:  LegalDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-06-2018    														*
 *                                                                  						*
 * Modified Date    :  16-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.legal.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.legal.LegalDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalDocument;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>LegalDetail</b>.<br>
 */
public class LegalDetailServiceImpl extends GenericService<LegalDetail> implements LegalDetailService {
	private static final Logger logger = Logger.getLogger(LegalDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LegalDetailDAO legalDetailDAO;
	private CollateralSetupDAO collateralSetupDAO;
	private CustomerDAO customerDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private FinanceMainDAO financeMainDAO;
	private FinCovenantTypeDAO finCovenantTypeDAO;
	private DocumentManagerDAO  documentManagerDAO;

	private LegalApplicantDetailService legalApplicantDetailService;
	private LegalPropertyDetailService legalPropertyDetailService;
	private LegalDocumentService legalDocumentService;
	private LegalPropertyTitleService legalPropertyTitleService;
	private LegalECDetailService legalECDetailService;
	private LegalNoteService legalNoteService;
	private QueryDetailService queryDetailService;
	private CollateralSetupService collateralSetupService;
	private CustomerDetailsService customerDetailsService;
	private JointAccountDetailService jointAccountDetailService;
	private BranchService branchService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public LegalDetailDAO getLegalDetailDAO() {
		return legalDetailDAO;
	}

	public void setLegalDetailDAO(LegalDetailDAO legalDetailDAO) {
		this.legalDetailDAO = legalDetailDAO;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}
	
	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public LegalApplicantDetailService getLegalApplicantDetailService() {
		return legalApplicantDetailService;
	}

	public void setLegalApplicantDetailService(LegalApplicantDetailService legalApplicantDetailService) {
		this.legalApplicantDetailService = legalApplicantDetailService;
	}

	public LegalPropertyDetailService getLegalPropertyDetailService() {
		return legalPropertyDetailService;
	}

	public void setLegalPropertyDetailService(LegalPropertyDetailService legalPropertyDetailService) {
		this.legalPropertyDetailService = legalPropertyDetailService;
	}

	public LegalDocumentService getLegalDocumentService() {
		return legalDocumentService;
	}

	public void setLegalDocumentService(LegalDocumentService legalDocumentService) {
		this.legalDocumentService = legalDocumentService;
	}

	public LegalPropertyTitleService getLegalPropertyTitleService() {
		return legalPropertyTitleService;
	}

	public void setLegalPropertyTitleService(LegalPropertyTitleService legalPropertyTitleService) {
		this.legalPropertyTitleService = legalPropertyTitleService;
	}

	public LegalECDetailService getLegalECDetailService() {
		return legalECDetailService;
	}

	public void setLegalECDetailService(LegalECDetailService legalECDetailService) {
		this.legalECDetailService = legalECDetailService;
	}

	public LegalNoteService getLegalNoteService() {
		return legalNoteService;
	}

	public void setLegalNoteService(LegalNoteService legalNoteService) {
		this.legalNoteService = legalNoteService;
	}
	
	
	/**
	 * Save the legal details from loan Origination
	 */
	@Override
	public void saveLegalDetails(FinanceDetail financeDetail) {

		List<CollateralAssignment> collateralAssignmentList = financeDetail.getCollateralAssignmentList();
		if (CollectionUtils.isEmpty(collateralAssignmentList)) {
			return;
		}
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (CollateralAssignment assignment : collateralAssignmentList) {

			String recordType = assignment.getRecordType();

			//Checking Record Exists or Not
			boolean isExists = getLegalDetailDAO().isExists(assignment.getReference(), assignment.getCollateralRef(),"_View");

			boolean isDelete = false;

			//If Record type is delete then we will make it as a inactive
			if (PennantConstants.RECORD_TYPE_DEL.equals(recordType) || PennantConstants.RECORD_TYPE_CAN.equals(recordType)) {
				isDelete = true;
				if (isExists) {
					getLegalDetailDAO().updateLegalDeatils(assignment.getReference(), assignment.getCollateralRef(), false);
				}
			}

			if (isDelete) {
				continue;
			}

			//If Record Exists  then we will make it as a active, otherwise save the record in temp table with Workflow details
			if (isExists) {
				getLegalDetailDAO().updateLegalDeatils(assignment.getReference(), assignment.getCollateralRef(), true);
			} else {

				String workflowType = ModuleUtil.getWorkflowType("LegalDetail");
				WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);
				WorkflowEngine engine = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
				LegalDetail legalDetail = new LegalDetail();
				legalDetail.setLoanReference(assignment.getReference());
				legalDetail.setCollateralReference(assignment.getCollateralRef());
				legalDetail.setBranch(aFinanceMain.getFinBranch());
				legalDetail.setNewRecord(true);
				legalDetail.setActive(true);
				legalDetail.setLegalDate(new Timestamp(System.currentTimeMillis()));
				legalDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				legalDetail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				legalDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				legalDetail.setWorkflowId(workFlowDetails.getWorkflowId());
				legalDetail.setRoleCode(workFlowDetails.getFirstTaskOwner());
				legalDetail.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
				legalDetail.setTaskId(engine.getUserTaskId(legalDetail.getRoleCode()));
				legalDetail.setNextTaskId(engine.getUserTaskId(legalDetail.getNextRoleCode()) + ";");

				getLegalDetailDAO().save(legalDetail, TableType.TEMP_TAB);
			}
		}
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * LegalDetails/LegalDetails_Temp by using LegalDetailsDAO's save method b)
	 * Update the Record in the table. based on the module workFlow
	 * Configuration. by using LegalDetailsDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtLegalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		LegalDetail legalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (legalDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (legalDetail.isNew()) {
			legalDetail.setId(Long.parseLong(getLegalDetailDAO().save(legalDetail, tableType)));
		} else {
			getLegalDetailDAO().update(legalDetail, tableType);
		}

		// Legal Applicant Details
		if (legalDetail.getApplicantDetailList() != null && !legalDetail.getApplicantDetailList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("ApplicantDetails");
			details = getLegalApplicantDetailService().processingApplicantDetail(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}

		// Legal Property Details
		if (legalDetail.getPropertyDetailList() != null && !legalDetail.getPropertyDetailList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyDetails");
			details = getLegalPropertyDetailService().processingPropertyDetail(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}

		// Legal Document Details
		if (legalDetail.getDocumentList() != null && !legalDetail.getDocumentList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("DocumentDetails");
			details = getLegalDocumentService().processingDocumentDetail(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}
		
		// PropertyTitles Details
		if (legalDetail.getPropertyTitleList() != null && !legalDetail.getPropertyTitleList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyTitles");
			details = getLegalPropertyTitleService().processingDetails(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}
		
		// Ecd Details
		if (legalDetail.getEcdDetailsList() != null && !legalDetail.getEcdDetailsList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("EcdDetails");
			details = getLegalECDetailService().processingDetails(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}
		
		// LegalNotes Details
		if (legalDetail.getLegalNotesList() != null && !legalDetail.getLegalNotesList().isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("LegalNotes");
			details = getLegalNoteService().processingDetails(legalDetail, details, tableType);
			auditDetails.addAll(details);
		}
		
		// Convents Details
		if (legalDetail.getCovenantTypeList() != null && !legalDetail.getCovenantTypeList().isEmpty()) {
			List<AuditDetail>  details = processingCoventsDetails(legalDetail, tableType, auditHeader.getAuditTranType());
			auditDetails.addAll(details);
		}

		auditHeader.getAuditDetail().setModelData(legalDetail);
		auditHeader.setAuditReference(String.valueOf(legalDetail.getLegalId()));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}
	

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table LegalDetails by using LegalDetailsDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtLegalDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		LegalDetail legalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();

		auditDetails.addAll(listDeletion(legalDetail, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));
		getLegalDetailDAO().delete(legalDetail, TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(), legalDetail.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				legalDetail.getBefImage(), legalDetail));

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getLegalDetails fetch the details by using LegalDetailsDAO's
	 * getLegalDetailsById method.
	 * 
	 * @param legalId
	 *            legalId of the LegalDetail.
	 * @return LegalDetails
	 */
	@Override
	public LegalDetail getLegalDetail(long legalId) {
		return getLegalDetails(legalId, "_View");
	}

	private LegalDetail getLegalDetails(long legalId, String tableType) {
		LegalDetail legalDetail = getLegalDetailDAO().getLegalDetail(legalId, tableType);
		if (legalDetail != null) {
			//Applicant details
			legalDetail.setApplicantDetailList(getLegalApplicantDetailService().getApplicantDetailsList(legalId, tableType));
			
			//Property details
			legalDetail.setPropertyDetailList(getLegalPropertyDetailService().getPropertyDetailsList(legalId, tableType));
			
			//Document details
			legalDetail.setDocumentList(getLegalDocumentService().getLegalDocumenttDetailsList(legalId, tableType));
			
			//Property Titles 
			legalDetail.setPropertyTitleList(getLegalPropertyTitleService().getDetailsList(legalId, tableType));
			
			//Ecd Details  
			legalDetail.setEcdDetailsList(getLegalECDetailService().getDetailsList(legalId, tableType));
			
			//LegalNotes  
			legalDetail.setLegalNotesList(getLegalNoteService().getDetailsList(legalId, tableType));
			
			// Covenant Details
			legalDetail.setCovenantTypeList(getFinCovenantTypeDAO().getFinCovenantTypeByFinRef(legalDetail.getLoanReference(), tableType, false));

			// Collateral Against Customer, document Details and setup details
			CollateralSetup collateralSetup = getCollateralSetupService().getCollateralSetupForLegal(legalDetail.getCollateralReference());
			if (collateralSetup != null) {
				legalDetail.setCollateralDocumentList(collateralSetup.getDocuments());

				List<Customer> customersList = new ArrayList<>();
				customersList.add(collateralSetup.getCustomerDetails().getCustomer());
				List<CoOwnerDetail> coOwnerDetailsList = collateralSetup.getCoOwnerDetailList();
				if (CollectionUtils.isNotEmpty(coOwnerDetailsList)) {
					for (CoOwnerDetail coOwnerDetail : coOwnerDetailsList) {
						CustomerDetails customerDetails = getCustomerDetailsService().getCustomerDetailsById(coOwnerDetail.getCustomerId(), false, "_View");
						customersList.add(customerDetails.getCustomer());
					}
				}
				legalDetail.setCustomerList(customersList);
			}
			
			//Finance details
			FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(legalDetail.getLoanReference(), "_View", false);
			if (financeMain != null) {
				legalDetail.setFinAmount(financeMain.getFinAssetValue());
				legalDetail.setFinType(financeMain.getFinType());
				legalDetail.setFinCcy(financeMain.getFinCcy());
				legalDetail.setFinNextRoleCode(financeMain.getNextRoleCode());
			}
			
			//Required for document generation
			FinanceDetail financeDetail = new FinanceDetail();
			FinScheduleData scheduleData = new FinScheduleData();
			scheduleData.setFinanceMain(financeMain);
			financeDetail.setFinScheduleData(scheduleData);
			if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), false, "_View"));
			}
			financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(financeMain.getFinReference(), "_View"));
			legalDetail.setFinanceDetail(financeDetail);
		}
		
		return legalDetail;
	}

	/**
	 * getApprovedLegalDetailsById fetch the details by using LegalDetailsDAO's
	 * getLegalDetailsById method . with parameter id and type as blank. it
	 * fetches the approved records from the LegalDetails.
	 * 
	 * @param legalId
	 *            legalId of the LegalDetail. (String)
	 * @return LegalDetails
	 */
	public LegalDetail getApprovedLegalDetail(long legalId) {
		return getLegalDetailDAO().getLegalDetail(legalId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getLegalDetailDAO().delete with parameters legalDetail,"" b) NEW
	 * Add new record in to main table by using getLegalDetailDAO().save with
	 * parameters legalDetail,"" c) EDIT Update record in the main table by
	 * using getLegalDetailDAO().update with parameters legalDetail,"" 3) Delete
	 * the record from the workFlow table by using getLegalDetailDAO().delete
	 * with parameters legalDetail,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtLegalDetails by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtLegalDetails by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		aAuditHeader = businessValidation(aAuditHeader, "doApprove");

		if (!aAuditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return aAuditHeader;
		}
		
		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		LegalDetail legalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();

		if (!PennantConstants.RECORD_TYPE_NEW.equals(legalDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					legalDetailDAO.getLegalDetail(legalDetail.getLegalId(), TableType.MAIN_TAB.getSuffix()));
		}

		if (legalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(legalDetail, TableType.MAIN_TAB.getSuffix(), tranType));
			getLegalDetailDAO().delete(legalDetail, TableType.MAIN_TAB);
		} else {
			legalDetail.setRoleCode("");
			legalDetail.setNextRoleCode("");
			legalDetail.setTaskId("");
			legalDetail.setNextTaskId("");
			legalDetail.setWorkflowId(0);

			if (legalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				legalDetail.setRecordType("");
				getLegalDetailDAO().save(legalDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				legalDetail.setRecordType("");
				getLegalDetailDAO().update(legalDetail, TableType.MAIN_TAB);
			}
			
			TableType tableType = TableType.MAIN_TAB;
			// Legal Applicant Details
			if (legalDetail.getApplicantDetailList() != null && !legalDetail.getApplicantDetailList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("ApplicantDetails");
				details = getLegalApplicantDetailService().processingApplicantDetail(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}

			// Legal Property Details
			if (legalDetail.getPropertyDetailList() != null && !legalDetail.getPropertyDetailList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyDetails");
				details = getLegalPropertyDetailService().processingPropertyDetail(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}

			// Legal Document Details
			if (legalDetail.getDocumentList() != null && !legalDetail.getDocumentList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("DocumentDetails");
				details = getLegalDocumentService().processingDocumentDetail(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}
			
			// PropertyTitles Details
			if (legalDetail.getPropertyTitleList() != null && !legalDetail.getPropertyTitleList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyTitles");
				details = getLegalPropertyTitleService().processingDetails(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}
			
			// Ecd Details
			if (legalDetail.getEcdDetailsList() != null && !legalDetail.getEcdDetailsList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("EcdDetails");
				details = getLegalECDetailService().processingDetails(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}
			
			// LegalNotes Details
			if (legalDetail.getLegalNotesList() != null && !legalDetail.getLegalNotesList().isEmpty()) {
				List<AuditDetail> details = legalDetail.getAuditDetailMap().get("LegalNotes");
				details = getLegalNoteService().processingDetails(legalDetail, details, tableType);
				auditDetails.addAll(details);
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new LegalDetail(), legalDetail.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetailList.addAll(listDeletion(legalDetail, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		getLegalDetailDAO().delete(legalDetail, TableType.TEMP_TAB);

		auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],legalDetail.getBefImage(), legalDetail));
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],legalDetail.getBefImage(), legalDetail));
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getLegalDetailDAO().delete with parameters
	 * legalDetail,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtLegalDetails by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		LegalDetail legalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new LegalDetail(), legalDetail.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				legalDetail.getBefImage(), legalDetail));

		auditDetails.addAll(listDeletion(legalDetail, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		getLegalDetailDAO().delete(legalDetail, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		LegalDetail legalDetail = (LegalDetail) auditDetail.getModelData();
		String usrLanguage = legalDetail.getUserDetails().getLanguage();

		// Applicant Details validation
		List<LegalApplicantDetail> applicantDetailsList = legalDetail.getApplicantDetailList();
		if (applicantDetailsList != null && !applicantDetailsList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("ApplicantDetails");
			details = getLegalApplicantDetailService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Property Details validation
		List<LegalPropertyDetail> propertyDetailsList = legalDetail.getPropertyDetailList();
		if (propertyDetailsList != null && !propertyDetailsList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyDetails");
			details = getLegalPropertyDetailService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		// Document Details validation
		List<LegalDocument> documentDetailsList = legalDetail.getDocumentList();
		if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("DocumentDetails");
			details = getLegalDocumentService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		// PropertyTitles Details validation
		List<LegalPropertyTitle> propertyTitlesList = legalDetail.getPropertyTitleList();
		if (propertyTitlesList != null && !propertyTitlesList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("PropertyTitles");
			details = getLegalPropertyTitleService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		// EcdDetails validation
		List<LegalECDetail> legalECDetailList = legalDetail.getEcdDetailsList();
		if (legalECDetailList != null && !legalECDetailList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("EcdDetails");
			details = getLegalECDetailService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		// LegalNotes validation
		List<LegalNote> LegalNotesList = legalDetail.getLegalNotesList();
		if (LegalNotesList != null && !LegalNotesList.isEmpty()) {
			List<AuditDetail> details = legalDetail.getAuditDetailMap().get("LegalNotes");
			details = getLegalNoteService().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getLegalDetailDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method 
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		LegalDetail legalDetail = (LegalDetail) auditDetail.getModelData();

		// Check the unique keys.
		if (legalDetail.isNew() && legalDetailDAO.isDuplicateKey(legalDetail.getLegalId(),
				legalDetail.getLoanReference(), legalDetail.getCollateralReference(),
				legalDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_LoanReference") + ": " + legalDetail.getLoanReference();
			parameters[1] = PennantJavaUtil.getLabel("label_CollaterialReference") + ": "
					+ legalDetail.getCollateralReference();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		// Query module, Validating the all quarry's raised by users resolved or not.
		if ("doApprove".equals(method)) {
			auditDetail = getQueryDetailService().validate(auditDetail);
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 ********************************************************************************************
	 * Preparing the child audit details *
	 ********************************************************************************************
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		LegalDetail legalDetail = (LegalDetail) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (legalDetail.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Applicant Detail List
		if (legalDetail.getApplicantDetailList() != null && legalDetail.getApplicantDetailList().size() > 0) {
			auditDetailMap.put("ApplicantDetails",
					getLegalApplicantDetailService().getApplicantDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ApplicantDetails"));
		}

		// Property Detail List
		if (legalDetail.getPropertyDetailList() != null && legalDetail.getPropertyDetailList().size() > 0) {
			auditDetailMap.put("PropertyDetails",
					getLegalPropertyDetailService().getPropertyDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PropertyDetails"));
		}

		// Document Detail List
		if (legalDetail.getDocumentList() != null && legalDetail.getDocumentList().size() > 0) {
			auditDetailMap.put("DocumentDetails",
					getLegalDocumentService().getDocumentDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}
		
		// Title Detail List
		if (legalDetail.getPropertyTitleList() != null && legalDetail.getPropertyTitleList().size() > 0) {
			auditDetailMap.put("PropertyTitles",
					getLegalPropertyTitleService().getDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PropertyTitles"));
		}
		
		// EcdDetails List
		if (legalDetail.getEcdDetailsList() != null && legalDetail.getEcdDetailsList().size() > 0) {
			auditDetailMap.put("EcdDetails",
					getLegalECDetailService().getDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("EcdDetails"));
		}
		
		// Legal Notes List
		if (legalDetail.getLegalNotesList() != null && legalDetail.getLegalNotesList().size() > 0) {
			auditDetailMap.put("LegalNotes",
					getLegalNoteService().getDetailsAuditData(legalDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LegalNotes"));
		}
		
		legalDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(legalDetail);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 ********************************************************************************************
	 * Deleting the all child records details *
	 ********************************************************************************************
	 */

	public List<AuditDetail> listDeletion(LegalDetail legalDetail, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// ApplicantDetails
		List<AuditDetail> applicantDetails = legalDetail.getAuditDetailMap().get("ApplicantDetails");
		if (applicantDetails != null && applicantDetails.size() > 0) {
			auditList.addAll(getLegalApplicantDetailService().deleteApplicantDetails(applicantDetails, tableType,
					auditTranType));
		}

		// PropertyDetails
		List<AuditDetail> propertyDetails = legalDetail.getAuditDetailMap().get("PropertyDetails");
		if (propertyDetails != null && propertyDetails.size() > 0) {
			auditList.addAll(
					getLegalPropertyDetailService().deletePropertyDetails(propertyDetails, tableType, auditTranType));
		}

		// DocumentDetails
		List<AuditDetail> documentDetails = legalDetail.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			auditList
					.addAll(getLegalDocumentService().deleteDocumentDetails(documentDetails, tableType, auditTranType));
		}
		
		// PropertyTitles
		List<AuditDetail> propertyTitlesList = legalDetail.getAuditDetailMap().get("PropertyTitles");
		if (propertyTitlesList != null && propertyTitlesList.size() > 0) {
			auditList.addAll(getLegalPropertyTitleService().deleteDetails(propertyTitlesList, tableType, auditTranType));
		}
		
		// EcdDetails
		List<AuditDetail> ecdDetailsList = legalDetail.getAuditDetailMap().get("EcdDetails");
		if (ecdDetailsList != null && ecdDetailsList.size() > 0) {
			auditList.addAll(getLegalECDetailService().deleteDetails(ecdDetailsList, tableType, auditTranType));
		}
		
		// LegalNotes
		List<AuditDetail> legalNotesList = legalDetail.getAuditDetailMap().get("LegalNotes");
		if (legalNotesList != null && legalNotesList.size() > 0) {
			auditList.addAll(getLegalNoteService().deleteDetails(legalNotesList, tableType, auditTranType));
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}
	
	/**
	 ********************************************************************************************
	 * Processing the convents details which are added in legal details
	 ********************************************************************************************
	 */
	private List<AuditDetail> processingCoventsDetails(LegalDetail legalDetail, TableType tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		List<FinCovenantType> finCovenantTypeList = legalDetail.getCovenantTypeList();

		if (finCovenantTypeList != null && !finCovenantTypeList.isEmpty()) {

			int i = 0;
			for (FinCovenantType finCovenantType : finCovenantTypeList) {
				if (StringUtils.isEmpty(StringUtils.trimToEmpty(finCovenantType.getRecordType()))) {
					continue;
				}
				boolean deleteRecord = false;
				boolean approveRec = false;

				if (StringUtils.isEmpty(tableType.getSuffix())) {
					approveRec = true;
					finCovenantType.setRoleCode("");
					finCovenantType.setNextRoleCode("");
					finCovenantType.setTaskId("");
					finCovenantType.setNextTaskId("");
				}
				finCovenantType.setWorkflowId(0);

				if (StringUtils.equalsIgnoreCase(finCovenantType.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finCovenantType.isNewRecord()) {
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(finCovenantType.getRecordType())) {
						finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finCovenantType.getRecordType())) {
						finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finCovenantType.getRecordType())) {
						finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (StringUtils.equalsIgnoreCase(finCovenantType.getRecordType(),
						(PennantConstants.RECORD_TYPE_NEW))) {
					if (approveRec) {
					}  
				} else if (StringUtils.equalsIgnoreCase(finCovenantType.getRecordType(),
						(PennantConstants.RECORD_TYPE_UPD))) {
				} else if (StringUtils.equalsIgnoreCase(finCovenantType.getRecordType(),
						(PennantConstants.RECORD_TYPE_DEL))) {
					if (approveRec) {
						deleteRecord = true;
					}  
				}
				if (deleteRecord) {
					getFinCovenantTypeDAO().delete(finCovenantType, tableType.getSuffix());
				}
				if (!deleteRecord) {
					boolean isExists = getFinCovenantTypeDAO().isExists(finCovenantType, "_Temp");
					if (isExists) {
						if (approveRec) {
							finCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							finCovenantType.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
						}
						getFinCovenantTypeDAO().update(finCovenantType, "_Temp");
					} else {

						getFinCovenantTypeDAO().save(finCovenantType, "_Temp");
					}
				}
				String[] fields = PennantJavaUtil.getFieldDetails(finCovenantType, finCovenantType.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						finCovenantType.getBefImage(), finCovenantType));
				i++;
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}
	
	
	
	/*Check the legal approved or not
	 */
	@Override
	public AuditHeader isLegalApproved(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		
		boolean isExists = getLegalDetailDAO().isExists(financeMain.getFinReference(), TableType.TEMP_TAB);
		
		if (isExists) {
			AuditDetail auditDetail = auditHeader.getAuditDetail();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "LG001", null, null), auditHeader.getUsrLanguage()));
		}
		
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/*
	 * Getting the all legal details against the loan reference
	 */
	@Override
	public List<LegalDetail> getApprovedLegalDetail(FinanceMain aFinanceMain) throws Exception {
		logger.debug(Literal.ENTERING);
		
		List<Long> idLIst = getLegalDetailDAO().getLegalIdListByFinRef(aFinanceMain.getFinReference());
		List<LegalDetail> detailList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(idLIst)) {
			boolean modtDoc = true;
			Branch branch =  getBranchService().getApprovedBranchById(aFinanceMain.getFinBranch());
			String stateName = branch.getLovDescBranchProvinceName();
			if ("DELHI".equalsIgnoreCase(stateName) || "PUNJAB".equalsIgnoreCase(stateName)
					|| "HARYANA".equalsIgnoreCase(stateName) || "DELHI".equalsIgnoreCase(stateName)) {
				modtDoc = false;
			}

			for (Long legalId : idLIst) {
				LegalDetail legalDetail = getLegalDetails(legalId, TableType.MAIN_TAB.getSuffix());
				if (legalDetail != null) {
					legalDetail.setModtDoc(modtDoc);
					legalDetail.setBranchDesc(aFinanceMain.getLovDescFinBranchName());
					legalDetail.setUserDetails(aFinanceMain.getUserDetails());
					detailList.add(formateMergeDetails(legalDetail, true));
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return detailList;
	}
	
	/*
	 * Formatting the details as per the marge fields
	 */
	@Override
	public LegalDetail formatLegalDetails(LegalDetail legalDetail) throws Exception {
		return formateMergeDetails(legalDetail, false);
	}

	private LegalDetail formateMergeDetails(LegalDetail legalDetail, boolean fromLoan) throws Exception {
		logger.debug(Literal.ENTERING);
		
		legalDetail.setStrAppDate(DateUtility.getAppDate(DateFormat.SHORT_DATE));

		List<LegalDocument> documentList = legalDetail.getDocumentList();
		if (CollectionUtils.isNotEmpty(documentList)) {
			int seqNum = 0;
			for (LegalDocument legalDocument : documentList) {
				if (legalDocument.getDocumentDate() != null) {
					legalDocument.setDocumentDateStr(DateUtility.formatDate(legalDocument.getDocumentDate(),
							DateFormat.SHORT_DATE.getPattern()));
					legalDocument.setDocumentAcceptedName(PennantStaticListUtil.getlabelDesc(
							legalDocument.getDocumentAccepted(), PennantStaticListUtil.getDocumentAcceptedList()));
					seqNum = seqNum + 1;
					legalDocument.setSeqNum(seqNum);
				}
			}
		}

		// Ec details
		List<LegalECDetail> ecDetailsList = legalDetail.getEcdDetailsList();
		if (CollectionUtils.isNotEmpty(ecDetailsList)) {
			for (LegalECDetail legalECDetail : ecDetailsList) {
				legalECDetail.setStrECDate(
						DateUtility.formatDate(legalECDetail.getEcDate(), DateFormat.SHORT_DATE.getPattern()));
			}
		}
		if (legalDetail.getPropertyDetailECDate() != null) {
			legalDetail.setStrPropertyDetailECDate(
					DateUtility.formatDate(legalDetail.getPropertyDetailECDate(), DateFormat.SHORT_DATE.getPattern()));
		}

		StringBuilder sb = null;
		// Applicants
		List<LegalApplicantDetail> applicantDetailsList = legalDetail.getApplicantDetailList();
		sb = new StringBuilder();
		if (CollectionUtils.isNotEmpty(applicantDetailsList)) {
			int seqNum = 0;
			for (LegalApplicantDetail detail : applicantDetailsList) {
				if (StringUtils.trimToNull(detail.getPropertyOwnersName()) != null) {
					if (sb.length() > 0) {
						sb.append(", ");
						if (StringUtils.trimToNull(detail.getTitleName()) != null) {
							sb.append(detail.getTitleName());
						}
						sb.append(detail.getPropertyOwnersName());
					} else {
						if (StringUtils.trimToNull(detail.getTitleName()) != null) {
							sb.append(detail.getTitleName());
						}
						sb.append(detail.getPropertyOwnersName());
					}
				}
				if (fromLoan) {
					seqNum = seqNum + 1;
					detail.setSeqNum(seqNum);
					StringBuilder modtString = new StringBuilder();
					if (StringUtils.trimToNull(detail.getTitleName()) != null) {
						modtString.append(detail.getTitleName());
					}
					modtString.append(detail.getPropertyOwnersName()).append(",");
					if (StringUtils.trimToNull(detail.getIDTypeName()) != null) {
						modtString.append(detail.getIDTypeName()).append("-");
					}
					modtString.append(detail.getIDNo()).append(", aged about ").append(detail.getAge()).append(" years");

					/*
					 * CustomerDetails customerDetails =
					 * getCustomerDetailsService().getCustomerDetailsById(detail
					 * .getCustomerId(), true, "_View"); //Customer High
					 * Priority Mobile number if (customerDetails != null &&
					 * CollectionUtils.isNotEmpty(customerDetails.
					 * getCustomerPhoneNumList())) { for (CustomerPhoneNumber
					 * phoneNumber : customerDetails.getCustomerPhoneNumList())
					 * { if
					 * (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)
					 * == phoneNumber.getPhoneTypePriority()) {
					 * modtString.append("Mobile No. ").append(phoneNumber.
					 * getPhoneNumber()); break; } } } // Customer High Priority
					 * Address number if (customerDetails != null &&
					 * CollectionUtils.isNotEmpty(customerDetails.getAddressList
					 * ())) { for (CustomerAddres addres :
					 * customerDetails.getAddressList()) { if
					 * (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)
					 * == addres .getCustAddrPriority()) {
					 * modtString.append(", is residing at "); if
					 * (StringUtils.isNotBlank(addres.getCustAddrHNbr())) {
					 * modtString.append(addres.getCustAddrHNbr()); } if
					 * (StringUtils.isNotBlank(addres.getCustFlatNbr())) {
					 * modtString.append(", ").append(addres.getCustFlatNbr());
					 * } if (StringUtils.isNotBlank(addres.getCustAddrStreet()))
					 * {
					 * modtString.append(", ").append(addres.getCustAddrStreet()
					 * ); } if
					 * (StringUtils.isNotBlank(addres.getCustAddrLine1())) {
					 * modtString.append(", ").append(addres.getCustAddrLine1())
					 * ; } if
					 * (StringUtils.isNotBlank(addres.getCustAddrLine2())) {
					 * modtString.append(", ").append(addres.getCustAddrLine2())
					 * ; } if
					 * (StringUtils.isNotBlank(addres.getCustAddrLine3())) {
					 * modtString.append(", ").append(addres.getCustAddrLine3())
					 * ; } if
					 * (StringUtils.isNotBlank(addres.getCustAddrLine4())) {
					 * modtString.append(", ").append(addres.getCustAddrLine4())
					 * ; } if
					 * (StringUtils.isNotBlank(addres.getLovDescCustAddrCityName
					 * ())) { modtString.append(", ").append(addres.
					 * getLovDescCustAddrCityName()); }
					 * 
					 * if (StringUtils.isNotBlank(addres.
					 * getLovDescCustAddrProvinceName())) {
					 * modtString.append(", ").append(addres.
					 * getLovDescCustAddrProvinceName()); } if
					 * (StringUtils.isNotBlank(addres.getLovDescCustAddrCityName
					 * ())) { modtString.append(", ").append(addres.
					 * getLovDescCustAddrCityName()); } if
					 * (StringUtils.isNotBlank(addres.getCustAddrZIP())) {
					 * modtString.append(", ").append(addres.getCustAddrZIP());
					 * } break; } } }
					 */
					modtString.append(", ").append(detail.getRemarks());
					detail.setModtString(modtString.toString());
				}
			}
			legalDetail.setListApplicantNames(sb.toString());
		}

		// ProperyOwners
		List<LegalPropertyDetail> propertyDetailsList = legalDetail.getPropertyDetailList();
		sb = new StringBuilder();
		if (CollectionUtils.isNotEmpty(propertyDetailsList)) {
			int seqNum = 0;
			legalDetail.setRegistrationOffice(propertyDetailsList.get(0).getRegistrationOffice());
			for (LegalPropertyDetail detail : propertyDetailsList) {
				if (StringUtils.trimToNull(detail.getPropertyOwner()) != null) {
					if (sb.length() > 0) {
						sb.append(", ").append(detail.getPropertyOwner());
					} else {
						sb.append(detail.getPropertyOwner());
					}
				}
				if (fromLoan) {
					seqNum = seqNum + 1;
					detail.setSeqNum(seqNum);
				}
			}
			legalDetail.setListPropOwnerNames(sb.toString());
		}

		FinanceDetail financeDetail = legalDetail.getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		legalDetail.setCustName(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		legalDetail.setStrFinAmoun(
				PennantApplicationUtil.amountFormate(finScheduleData.getFinanceMain().getFinAssetValue(),
						CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy())));

		String finAmountWords = NumberToEnglishWords.getAmountInText(
				PennantApplicationUtil.formateAmount(finScheduleData.getFinanceMain().getFinAssetValue(),
						CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy())),
				finScheduleData.getFinanceMain().getFinCcy());
		legalDetail.setFinAmountWords(finAmountWords);

		// Co-applicants
		List<JointAccountDetail> jointAccountDetails = financeDetail.getJountAccountDetailList();
		sb = new StringBuilder();
		if (CollectionUtils.isNotEmpty(jointAccountDetails)) {
			for (JointAccountDetail detail : jointAccountDetails) {
				if (StringUtils.trimToNull(detail.getLovDescCIFName()) != null) {
					if (sb.length() > 0) {
						sb.append(", ").append(detail.getLovDescCIFName());
					} else {
						sb.append(detail.getLovDescCIFName());
					}
				}
			}
			
		}
		if (sb.length() <= 0) {
			legalDetail.setListCoApplicantNames("");
		} else {
			legalDetail.setListCoApplicantNames(sb.toString());
		}

		// User details
		LoggedInUser userDetail = legalDetail.getUserDetails();
		legalDetail.setUserName(userDetail.getFullName());
		legalDetail.setDesgnation(userDetail.getDepartmentCode());
		legalDetail.setEmpCode(userDetail.getStaffId());

		if (!fromLoan) {
			// Query details list
			List<QueryDetail> querryList = getQueryDetailService()
					.getQueryDetailsforAgreements(legalDetail.getLegalReference());
			if (CollectionUtils.isEmpty(querryList)) {
				querryList = new ArrayList<>();
				QueryDetail queryDetail = new QueryDetail();
				queryDetail.setQryNotes("");
				querryList.add(queryDetail);
			}
			legalDetail.setQueryDetailsList(querryList);
		}
		logger.debug(Literal.LEAVING);
		return legalDetail;
	}
	
	// Saving Documents
	@Override
	public void saveDocumentDetails(DocumentDetails documentDetails) {
		logger.debug(Literal.ENTERING);
		documentDetails.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
		DocumentManager documentManager = new DocumentManager();
		documentManager.setDocImage(documentDetails.getDocImage());
		documentDetails.setDocRefId(getDocumentManagerDAO().save(documentManager));
		 
		DocumentDetails oldDocumentDetails = getDocumentDetailsDAO().getDocumentDetails(documentDetails.getReferenceId(), documentDetails.getDocCategory(),
				documentDetails.getDocModule(), TableType.MAIN_TAB.getSuffix());
		
		if (oldDocumentDetails != null && oldDocumentDetails.getDocId() != Long.MIN_VALUE) {
			documentDetails.setDocId(oldDocumentDetails.getDocId());
			getDocumentDetailsDAO().update(documentDetails, TableType.MAIN_TAB.getSuffix());
		} else {
			getDocumentDetailsDAO().save(documentDetails, TableType.MAIN_TAB.getSuffix());
		}
		logger.debug(Literal.LEAVING);
	}
	
	 /*
	  * Getting the document details
	  */
	@Override
	public List<DocumentDetails> getDocumentDetails(String reference, String module) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(reference, module, "", TableType.MAIN_TAB.getSuffix());
	}
	
	@Override
	public DocumentDetails getDocDetailByDocId(long docId, String type, boolean readAttachment) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, type, readAttachment);
	}
	
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinCovenantTypeDAO getFinCovenantTypeDAO() {
		return finCovenantTypeDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}

	public QueryDetailService getQueryDetailService() {
		return queryDetailService;
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
}