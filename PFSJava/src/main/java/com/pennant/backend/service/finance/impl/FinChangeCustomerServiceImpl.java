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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.finance.FinChangeCustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
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
	private FinanceMainDAO financeMainDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private CollateralSetupDAO collateralSetupDAO;

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

		if (finChangeCustomer.isNewRecord()) {
			finChangeCustomerDAO.save(finChangeCustomer, tableType);
		} else {
			finChangeCustomerDAO.update(finChangeCustomer, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinChangeCustomer by using FinChangeCustomerDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		finChangeCustomerDAO.delete(FinChangeCustomer, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getFinChangeCustomer fetch the details by using FinChangeCustomerDAO's getFinChangeCustomerById method.
	 * 
	 * @param entityCode   entityCode of the FinChangeCustomer.
	 * @param finReference finReference of the FinChangeCustomer.
	 * @return FinChangeCustomer
	 */
	@Override
	public FinChangeCustomer getFinChangeCustomerById(long id) {
		FinChangeCustomer finChangeCustomer = finChangeCustomerDAO.getFinChangeCustomerById(id, "_View");
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
	 * @param entityCode   entityCode of the FinChangeCustomer.
	 * @param finReference finReference of the FinChangeCustomer. (String)
	 * @return FinChangeCustomer
	 */
	@Override
	public FinChangeCustomer getApprovedFinChangeCustomerById(long id) {
		return finChangeCustomerDAO.getFinChangeCustomerById(id, "_AView");
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
	 * @param AuditHeader (auditHeader)
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

		FinChangeCustomer fcc = new FinChangeCustomer();
		BeanUtils.copyProperties(auditHeader.getAuditDetail().getModelData(), fcc);

		finChangeCustomerDAO.delete(fcc, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fcc.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(finChangeCustomerDAO.getFinChangeCustomerById(fcc.getId(), ""));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(fcc.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			finChangeCustomerDAO.delete(fcc, TableType.MAIN_TAB);
		} else {
			fcc.setRoleCode("");
			fcc.setNextRoleCode("");
			fcc.setTaskId("");
			fcc.setNextTaskId("");
			fcc.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(fcc.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				fcc.setRecordType("");
				finChangeCustomerDAO.deleteByReference(fcc.getFinReference());
				finChangeCustomerDAO.save(fcc, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fcc.setRecordType("");
				finChangeCustomerDAO.update(fcc, TableType.MAIN_TAB);
			}
		}
		doProcess(fcc);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fcc);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	public void doProcess(FinChangeCustomer finChangeCustomer) {
		Long coApplicantId = finChangeCustomer.getCoApplicantId();
		if (coApplicantId == null || coApplicantId <= 0) {
			return;
		}

		String finReference = finChangeCustomer.getFinReference();
		String finRef = finReference;
		changeCustomerDetails(finChangeCustomer);

		FinanceTaxDetail financeTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finRef, "_Temp");

		if (financeTaxDetail != null) {
			financeTaxDetailDAO.deleteFinTaxDetails(financeTaxDetail, TableType.TEMP_TAB);
		}

		JointAccountDetail ajd = finChangeCustomer.getJointAccountDetail();
		if (ajd != null) {
			jointAccountDetailDAO.delete(ajd, "_Temp");
		}

		JointAccountDetail jd = new JointAccountDetail();
		jd.setNewRecord(true);
		jd.setWorkflowId(ajd.getWorkflowId());
		jd.setRecordType(ajd.getRecordType());
		jd.setRecordStatus(ajd.getRecordStatus());
		jd.setRoleCode(ajd.getRoleCode());
		jd.setNextRoleCode(ajd.getNextRoleCode());
		jd.setVersion(ajd.getVersion());
		jd.setLastMntOn(ajd.getLastMntOn());
		jd.setLastMntBy(ajd.getLastMntBy());
		jd.setFinReference(finRef);
		jd.setCustCIF(finChangeCustomer.getCustCif());

		jointAccountDetailDAO.save(jd, "_Temp");

		Date appDate = SysParamUtil.getAppDate();
		boolean collateralDelinkStatus = finChangeCustomer.isCollateralDelinkStatus();

		if (!collateralDelinkStatus) {
			return;
		}

		long oldCustId = finChangeCustomer.getOldCustId();
		List<CollateralSetup> collateralsList = getCollateralByReference(finReference, oldCustId);

		boolean colExist = false;
		List<CollateralMovement> movements = new ArrayList<>();

		CollateralAssignment assignment = null;
		for (CollateralSetup collateralSetup : collateralsList) {
			String colRef = collateralSetup.getCollateralRef();
			assignment = collateralAssignmentDAO.getCollateralAssignmentByFinReference(finRef, colRef, "_Temp");

			if (assignment == null) {
				continue;
			}

			colExist = true;
			CollateralMovement movement = new CollateralMovement();
			movement.setModule(FinanceConstants.MODULE_NAME);
			movement.setCollateralRef(colRef);
			movement.setReference(finRef);
			movement.setAssignPerc(assignment.getAssignPerc());
			movement.setValueDate(appDate);
			movement.setProcess(CollateralConstants.PROCESS_MANUAL);

			movements.add(movement);

			if (movements.size() == PennantConstants.CHUNK_SIZE) {
				collateralAssignmentDAO.saveList(movements);
				movements.clear();
			}
		}
		collateralAssignmentDAO.saveList(movements);
		if (colExist) {
			collateralAssignmentDAO.deLinkCollateral(finRef, "_Temp");
		}

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinChangeCustomerDAO().delete with parameters FinChangeCustomer,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinChangeCustomer by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
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
		finChangeCustomerDAO.delete(FinChangeCustomer, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
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
		if (FinChangeCustomer.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(FinChangeCustomer.getRecordType())
				&& finChangeCustomerDAO.isDuplicateKey(FinChangeCustomer.getId(), FinChangeCustomer.getFinReference(),
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
		financeMainDAO.updateCustChange(finChangeCustomer.getCoApplicantId(), 0, finChangeCustomer.getFinReference(),
				"_Temp");
	}

	@Override
	public boolean isFinReferenceProcess(String finReference) {
		return finChangeCustomerDAO.isFinReferenceProcess(finReference, "_Temp");
	}

	@Override
	public List<CollateralSetup> getCollateralByReference(String reference, long depositorId) {
		return collateralSetupDAO.getCollateralByRef(reference, depositorId, "_View");
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setFinChangeCustomerDAO(FinChangeCustomerDAO finChangeCustomerDAO) {
		this.finChangeCustomerDAO = finChangeCustomerDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}

}