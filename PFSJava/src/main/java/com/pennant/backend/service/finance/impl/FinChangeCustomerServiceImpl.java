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
 * FileName    		:  FinChangeCustomerServiceImpl.java                                    * 	  
 *                                                                    					    *
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :     																	*
 *                                                                  						*
 * Modified Date    :      																	*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 														                                    * 
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
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinChangeCustomerDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinChangeCustomerService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinChangeCustomer</b>.<br>
 */
public class FinChangeCustomerServiceImpl extends GenericService<FinChangeCustomer>
		implements FinChangeCustomerService {
	private static final Logger logger = LogManager.getLogger(FinChangeCustomerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinChangeCustomerDAO finChangeCustomerDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceMainDAO financeMainDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private MandateDAO mandateDAO;
	private transient LimitDetailService limitDetailService;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private JountAccountDetailDAO jountAccountDetailDAO;

	private CollateralSetupDAO collateralSetupDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public FinChangeCustomerDAO getFinChangeCustomerDAO() {
		return finChangeCustomerDAO;
	}

	public void setFinChangeCustomerDAO(FinChangeCustomerDAO finChangeCustomerDAO) {
		this.finChangeCustomerDAO = finChangeCustomerDAO;
	}

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public BeneficiaryDAO getBeneficiaryDAO() {
		return beneficiaryDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public MandateDAO getMandateDAO() {
		return mandateDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	public CollateralSetupDAO getCollateralSetupDAO() {
		return collateralSetupDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinChangeCustomer finChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (finChangeCustomer.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (finChangeCustomer.isNew()) {
			getFinChangeCustomerDAO().save(finChangeCustomer, tableType);
		} else {
			getFinChangeCustomerDAO().update(finChangeCustomer, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinChangeCustomer by using FinChangeCustomerDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinChangeCustomer FinChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();
		getFinChangeCustomerDAO().delete(FinChangeCustomer, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getFinChangeCustomer fetch the details by using FinChangeCustomerDAO's getFinChangeCustomerById method.
	 * 
	 * @param entityCode
	 *            entityCode of the FinChangeCustomer.
	 * @param finReference
	 *            finReference of the FinChangeCustomer.
	 * @return FinChangeCustomer
	 */
	@Override
	public FinChangeCustomer getFinChangeCustomerById(long id) {
		FinChangeCustomer finChangeCustomer = getFinChangeCustomerDAO().getFinChangeCustomerById(id, "_View");
		List<CollateralSetup> collateralByReference = getCollateralByReference(finChangeCustomer.getFinReference(),
				finChangeCustomer.getOldCustId());
		if (collateralByReference != null) {
			finChangeCustomer.setCollateralSetups(collateralByReference);
		}
		return finChangeCustomer;
	}

	/**
	 * getApprovedFinChangeCustomerById fetch the details by using FinChangeCustomerDAO's getFinChangeCustomerById
	 * method . with parameter id and type as blank. it fetches the approved records from the FinChangeCustomer.
	 * 
	 * @param entityCode
	 *            entityCode of the FinChangeCustomer.
	 * @param finReference
	 *            finReference of the FinChangeCustomer. (String)
	 * @return FinChangeCustomer
	 */
	@Override
	public FinChangeCustomer getApprovedFinChangeCustomerById(long id) {
		return getFinChangeCustomerDAO().getFinChangeCustomerById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinChangeCustomerDAO().delete with
	 * parameters FinChangeCustomer,"" b) NEW Add new record in to main table by using getFinChangeCustomerDAO().save
	 * with parameters FinChangeCustomer,"" c) EDIT Update record in the main table by using
	 * getFinChangeCustomerDAO().update with parameters FinChangeCustomer,"" 3) Delete the record from the workFlow
	 * table by using getFinChangeCustomerDAO().delete with parameters FinChangeCustomer,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinChangeCustomer finChangeCustomer = new FinChangeCustomer();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), finChangeCustomer);

		getFinChangeCustomerDAO().delete(finChangeCustomer, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(finChangeCustomer.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(finChangeCustomerDAO.getFinChangeCustomerById(finChangeCustomer.getId(), ""));
		}

		if (finChangeCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinChangeCustomerDAO().delete(finChangeCustomer, TableType.MAIN_TAB);
		} else {
			finChangeCustomer.setRoleCode("");
			finChangeCustomer.setNextRoleCode("");
			finChangeCustomer.setTaskId("");
			finChangeCustomer.setNextTaskId("");
			finChangeCustomer.setWorkflowId(0);

			if (finChangeCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finChangeCustomer.setRecordType("");
				getFinChangeCustomerDAO().save(finChangeCustomer, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finChangeCustomer.setRecordType("");
				getFinChangeCustomerDAO().update(finChangeCustomer, TableType.MAIN_TAB);
			}
		}
		doProcess(finChangeCustomer);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finChangeCustomer);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	public void doProcess(FinChangeCustomer finChangeCustomer) {
		if (finChangeCustomer.getCoApplicantId() > 0) {
			String finReference = finChangeCustomer.getFinReference();
			changeCustomerDetails(finChangeCustomer);

			FinanceTaxDetail financeTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_Temp");

			if (financeTaxDetail != null) {
				getFinanceTaxDetailDAO().deleteFinTaxDetails(financeTaxDetail, TableType.TEMP_TAB);
			}
			if (finChangeCustomer.getJointAccountDetail() != null) {
				getJountAccountDetailDAO().delete(finChangeCustomer.getJointAccountDetail(), "_Temp");
			}

			JointAccountDetail jointAccountDetail = new JointAccountDetail();
			jointAccountDetail.setNewRecord(true);
			jointAccountDetail.setWorkflowId(finChangeCustomer.getJointAccountDetail().getWorkflowId());
			jointAccountDetail.setRecordType(finChangeCustomer.getJointAccountDetail().getRecordType());
			jointAccountDetail.setRecordStatus(finChangeCustomer.getJointAccountDetail().getRecordStatus());
			jointAccountDetail.setRoleCode(finChangeCustomer.getJointAccountDetail().getRoleCode());
			jointAccountDetail.setNextRoleCode(finChangeCustomer.getJointAccountDetail().getNextRoleCode());
			jointAccountDetail.setVersion(finChangeCustomer.getJointAccountDetail().getVersion());
			jointAccountDetail.setLastMntOn(finChangeCustomer.getJointAccountDetail().getLastMntOn());
			jointAccountDetail.setLastMntBy(finChangeCustomer.getJointAccountDetail().getLastMntBy());
			jointAccountDetail.setFinReference(finReference);
			jointAccountDetail.setCustCIF(finChangeCustomer.getCustCif());
			getJountAccountDetailDAO().save(jointAccountDetail, "_Temp");
			// getCollateralAssignmentDAO().deLinkCollateral(finChangeCustomer.getFinReference());
			// Collateral Setups

			if (finChangeCustomer.isCollateralDelinkStatus()) {
				List<CollateralSetup> collateralsList = getCollateralByReference(finChangeCustomer.getFinReference(),
						finChangeCustomer.getOldCustId());
				if (CollectionUtils.isNotEmpty(collateralsList)) {
					boolean colExist = false;

					for (CollateralSetup collateralSetup : collateralsList) {
						String collateralRef = collateralSetup.getCollateralRef();
						int count = collateralAssignmentDAO.getAssignedCollateralCountByRef(collateralRef, finReference,
								"_Temp");
						if (count > 0) {
							CollateralAssignment assignment = collateralAssignmentDAO
									.getCollateralAssignmentByFinReference(finReference, collateralRef, "_Temp");
							if (assignment != null) {
								colExist = true;
								CollateralMovement movement = new CollateralMovement();
								movement.setModule(FinanceConstants.MODULE_NAME);
								movement.setCollateralRef(collateralRef);
								movement.setReference(finReference);
								movement.setAssignPerc(assignment.getAssignPerc());
								movement.setValueDate(DateUtility.getAppDate());
								movement.setProcess(CollateralConstants.PROCESS_MANUAL);
								collateralAssignmentDAO.save(movement);
							}
						}
					}
					if (colExist) {
						collateralAssignmentDAO.deLinkCollateral(finReference, "_Temp");
					}
				}

			}
		}
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinChangeCustomerDAO().delete with parameters FinChangeCustomer,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		FinChangeCustomer FinChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinChangeCustomerDAO().delete(FinChangeCustomer, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		FinChangeCustomer finChangeCustomer = (FinChangeCustomer) auditDetail.getModelData();
		finChangeCustomer.setAuditDetailMap(new HashMap<String, List<AuditDetail>>());
		if (finChangeCustomer.isCollateralDelinkStatus()) {
			auditHeader.setErrorList(auditDetail.getErrorDetails());
			auditHeader = prepareChildsAudit(auditHeader, method);
			auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));
		}
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinChangeCustomerDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinChangeCustomer FinChangeCustomer = (FinChangeCustomer) auditDetail.getModelData();

		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + FinChangeCustomer.getId();

		// Check the unique keys.
		if (FinChangeCustomer.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(FinChangeCustomer.getRecordType())
				&& getFinChangeCustomerDAO().isDuplicateKey(FinChangeCustomer.getId(),
						FinChangeCustomer.getFinReference(),
						FinChangeCustomer.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		FinChangeCustomer finChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		// CollateralSetups
		if (finChangeCustomer.getAuditDetailMap().get("CollateralSetups") != null) {
			auditDetails = finChangeCustomer.getAuditDetailMap().get("CollateralSetups");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationCollateralSetups(auditDetail, finChangeCustomer.getFinReference(),
						usrLanguage, method).getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return errorDetails;
	}

	private AuditDetail validationCollateralSetups(AuditDetail auditDetail, String finReference, String usrLanguage,
			String method) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		CollateralSetup collateralSetup = (CollateralSetup) auditDetail.getModelData();
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		String[] errParm = new String[2];
		String[] valueParm = new String[1];
		valueParm[0] = collateralSetup.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_CollateralRef");
		errParm[1] = "Basic Maintainance";

		// Check if this collateral is mapped with any other finance
		if (auditDetail.getErrorDetails() == null || auditDetail.getErrorDetails().isEmpty()) {
			if (method.equals(PennantConstants.method_doApprove)) {
				int count = collateralAssignmentDAO.getAssignedCollateralCountByRef(collateralSetup.getCollateralRef(),
						finReference, "_Temp");

				if (count > 0) {
					errorDetails.add(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "60218", errParm, valueParm), usrLanguage));
				}
			}
		}
		auditDetail.setErrorDetails(errorDetails);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinChangeCustomer finChangeCustomer = (FinChangeCustomer) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finChangeCustomer.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		List<CollateralSetup> collateralSetupList = getCollateralByReference(finChangeCustomer.getFinReference(),
				finChangeCustomer.getOldCustId());
		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			for (CollateralSetup collateralSetup : collateralSetupList) {
				collateralSetup.setLastMntOn(finChangeCustomer.getLastMntOn());
				collateralSetup.setLastMntBy(finChangeCustomer.getLastMntBy());
				collateralSetup.setRecordStatus(finChangeCustomer.getRecordStatus());
				collateralSetup.setUserDetails(finChangeCustomer.getUserDetails());
				collateralSetup.setWorkflowId(finChangeCustomer.getWorkflowId());
				collateralSetup.setRoleCode(finChangeCustomer.getRoleCode());
				collateralSetup.setNextRoleCode(finChangeCustomer.getNextRoleCode());
				collateralSetup.setTaskId(finChangeCustomer.getTaskId());
				collateralSetup.setNextTaskId(finChangeCustomer.getNextTaskId());

			}
			auditDetailMap.put("CollateralSetups",
					setCollateralSetupAuditData(collateralSetupList, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralSetups"));
		}

		finChangeCustomer.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finChangeCustomer);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setCollateralSetupAuditData(List<CollateralSetup> collateralSetupList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(),
				new CollateralSetup().getExcludeFields());
		for (int i = 0; i < collateralSetupList.size(); i++) {
			CollateralSetup collateralSetup = collateralSetupList.get(i);

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(collateralSetup.getRecordType())) {
				collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(collateralSetup.getRecordType())) {
				collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(collateralSetup.getRecordType())) {
				collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				collateralSetup.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(collateralSetup.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(collateralSetup.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(collateralSetup.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], collateralSetup.getBefImage(),
					collateralSetup));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	public void changeCustomerDetails(FinChangeCustomer finChangeCustomer) {

		getFinanceMainDAO().updateCustChange(finChangeCustomer.getCoApplicantId(), 0,
				finChangeCustomer.getFinReference(), "_Temp");

	}

	// is loan reference is proccess in change customer
	@Override
	public boolean isFinReferenceProcess(String finReference) {
		return getFinChangeCustomerDAO().isFinReferenceProcess(finReference, "_Temp");
	}

	@Override
	public List<CollateralSetup> getCollateralByReference(String reference, long depositorId) {
		return getCollateralSetupDAO().getCollateralByRef(reference, depositorId, "_View");
	}
}