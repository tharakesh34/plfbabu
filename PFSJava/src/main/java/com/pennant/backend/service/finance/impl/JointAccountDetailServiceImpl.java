/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : JointAccountDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * 13-06-2018 Pennant 0.2 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;
import com.pennanttech.pff.service.sampling.SamplingService;

/**
 * Service implementation for methods that depends on <b>JointAccountDetail</b>.<br>
 * 
 */
public class JointAccountDetailServiceImpl extends GenericService<JointAccountDetail>
		implements JointAccountDetailService {
	private static final Logger logger = LogManager.getLogger(JointAccountDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private CustomerIncomeDAO customerIncomeDAO;
	@Autowired
	private ExternalLiabilityDAO externalLiabilityDAO;
	@Autowired
	private IncomeDetailDAO incomeDetailDAO;
	private CustomerDAO customerDAO;
	@Autowired
	private SamplingDAO samplingDAO;
	@Autowired
	private SamplingService samplingService;
	private CustomerDataService customerDataService;
	private CustomerDocumentDAO customerDocumentDAO;

	public JointAccountDetailServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the jointAccountDetailDAO
	 */
	public JointAccountDetailDAO getJointAccountDetailDAO() {
		return jointAccountDetailDAO;
	}

	/**
	 * @param jointAccountDetailDAO the jointAccountDetailDAO to set
	 */
	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	private CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	/**
	 * @return the jointAccountDetail
	 */
	@Override
	public JointAccountDetail getJointAccountDetail() {
		return getJointAccountDetailDAO().getJointAccountDetail();
	}

	/**
	 * @return the jointAccountDetail for New Record
	 */
	@Override
	public JointAccountDetail getNewJointAccountDetail() {
		return getJointAccountDetailDAO().getNewJointAccountDetail();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinJointAccountDetails/FinJointAccountDetails_Temp by using JointAccountDetailDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using JointAccountDetailDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * FinJointAccountDetails/FinJointAccountDetails_Temp by using JointAccountDetailDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using JointAccountDetailDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
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
		JointAccountDetail jointAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();

		if (jointAccountDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (jointAccountDetail.isNewRecord()) {
			jointAccountDetail.setId(getJointAccountDetailDAO().save(jointAccountDetail, tableType));
			auditHeader.getAuditDetail().setModelData(jointAccountDetail);
			auditHeader.setAuditReference(String.valueOf(jointAccountDetail.getJointAccountId()));
		} else {
			getJointAccountDetailDAO().update(jointAccountDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinJointAccountDetails by using JointAccountDetailDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		JointAccountDetail jointAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();
		getJointAccountDetailDAO().delete(jointAccountDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getJointAccountDetailById fetch the details by using JointAccountDetailDAO's getJointAccountDetailById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return JointAccountDetail
	 */

	@Override
	public JointAccountDetail getJointAccountDetailById(long id) {
		return getJointAccountDetailDAO().getJointAccountDetailById(id, "_View");
	}

	/**
	 * getApprovedJointAccountDetailById fetch the details by using JointAccountDetailDAO's getJointAccountDetailById
	 * method . with parameter id and type as blank. it fetches the approved records from the FinJointAccountDetails.
	 * 
	 * @param id (int)
	 * @return JointAccountDetail
	 */

	public JointAccountDetail getApprovedJointAccountDetailById(long id) {
		return getJointAccountDetailDAO().getJointAccountDetailById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getJointAccountDetailDAO().delete with
	 * parameters JointAccountDetail,"" b) NEW Add new record in to main table by using getJointAccountDetailDAO().save
	 * with parameters JointAccountDetail,"" c) EDIT Update record in the main table by using
	 * getJointAccountDetailDAO().update with parameters JointAccountDetail,"" 3) Delete the record from the workFlow
	 * table by using getJointAccountDetailDAO().delete with parameters JointAccountDetail,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JointAccountDetail jointAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties((JointAccountDetail) auditHeader.getAuditDetail().getModelData(), jointAccountDetail);

		if (jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getJointAccountDetailDAO().delete(jointAccountDetail, "");

		} else {
			jointAccountDetail.setRoleCode("");
			jointAccountDetail.setNextRoleCode("");
			jointAccountDetail.setTaskId("");
			jointAccountDetail.setNextTaskId("");
			jointAccountDetail.setWorkflowId(0);

			if (jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				jointAccountDetail.setRecordType("");
				getJointAccountDetailDAO().save(jointAccountDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				jointAccountDetail.setRecordType("");
				getJointAccountDetailDAO().update(jointAccountDetail, "");
			}
		}

		getJointAccountDetailDAO().delete(jointAccountDetail, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(jointAccountDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getJointAccountDetailDAO().delete with parameters JointAccountDetail,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinJointAccountDetails by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		JointAccountDetail jointAccountDetail = (JointAccountDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getJointAccountDetailDAO().delete(jointAccountDetail, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,
				onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<AuditDetail> validate(List<JointAccountDetail> jointAcDetailList, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		return doValidation(jointAcDetailList, workflowId, method, auditTranType, usrLanguage);
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getJointAccountDetailDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		JointAccountDetail jointAccountDetail = (JointAccountDetail) auditDetail.getModelData();

		JointAccountDetail tempJointAccountDetail = null;
		if (jointAccountDetail.isWorkflow()) {
			tempJointAccountDetail = getJointAccountDetailDAO().getJointAccountDetailById(jointAccountDetail.getId(),
					"_Temp");
		}
		JointAccountDetail befJointAccountDetail = getJointAccountDetailDAO()
				.getJointAccountDetailById(jointAccountDetail.getId(), "");

		JointAccountDetail oldJointAccountDetail = jointAccountDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(jointAccountDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_JointAccountId") + ":" + valueParm[0];

		if (jointAccountDetail.isNewRecord()) { // for New record or new record into work flow

			if (!jointAccountDetail.isWorkflow()) {// With out Work flow only new records
				if (befJointAccountDetail != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																									// new
					if (befJointAccountDetail != null || tempJointAccountDetail != null) { // if records already exists
																							// in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJointAccountDetail == null || tempJointAccountDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jointAccountDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befJointAccountDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldJointAccountDetail != null
							&& !oldJointAccountDetail.getLastMntOn().equals(befJointAccountDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempJointAccountDetail == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldJointAccountDetail != null && tempJointAccountDetail != null
						&& !oldJointAccountDetail.getLastMntOn().equals(tempJointAccountDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jointAccountDetail.isWorkflow()) {
			auditDetail.setBefImage(befJointAccountDetail);
		}

		return auditDetail;
	}

	/**
	 * Method For Preparing List of AuditDetails for JointAccountDetails
	 * 
	 * @param auditDetails
	 * @param tableType
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingJointAccountDetail(List<AuditDetail> auditDetails, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		Sampling sampling = null;
		for (int i = 0; i < auditDetails.size(); i++) {

			JointAccountDetail jointAccountDetail = (JointAccountDetail) auditDetails.get(i).getModelData();

			if (!jointAccountDetail.isIncludeIncome() && !jointAccountDetail.isNewRecord()) {
				if (sampling == null) {
					sampling = samplingService.getSampling(jointAccountDetail.getFinReference(), "_aview");
				}
				if (sampling != null) {
					long linkId = samplingDAO.getIncomeLinkIdByCustId(jointAccountDetail.getCustID(), sampling.getId());
					if (linkId != 0) {
						incomeDetailDAO.deletebyLinkId(linkId, "");
					}
				}
			}

			if (jointAccountDetail.getCustomerDetails() != null
					&& jointAccountDetail.getCustomerDetails().getExtendedFieldRender() != null) {
				processingJointAccExtendedFields(jointAccountDetail, tableType, auditTranType);
			}

			// Process documents to DMS
			if (jointAccountDetail.getCustomerDetails() != null) {
				if (CollectionUtils.isNotEmpty(jointAccountDetail.getCustomerDetails().getCustomerDocumentsList())) {
					for (CustomerDocument document : jointAccountDetail.getCustomerDetails()
							.getCustomerDocumentsList()) {
						document.setLovDescCustCIF(jointAccountDetail.getCustCIF());
						processDocument(document, "", jointAccountDetail.getCustID());
					}
				}
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (tableType.equals("")) {
				approveRec = true;
				jointAccountDetail.setRoleCode("");
				jointAccountDetail.setNextRoleCode("");
				jointAccountDetail.setTaskId("");
				jointAccountDetail.setNextTaskId("");
			}
			// guarantorDetail.setWorkflowId(0);

			if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (jointAccountDetail.isNewRecord()) {
				saveRecord = true;
				if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (jointAccountDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = jointAccountDetail.getRecordType();
				recordStatus = jointAccountDetail.getRecordStatus();
				jointAccountDetail.setWorkflowId(0);
				jointAccountDetail.setRecordType("");
				jointAccountDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getJointAccountDetailDAO().save(jointAccountDetail, tableType);
			}

			if (updateRecord) {
				getJointAccountDetailDAO().update(jointAccountDetail, tableType);
			}

			if (deleteRecord) {
				getJointAccountDetailDAO().delete(jointAccountDetail, tableType);
			}

			if (approveRec) {
				jointAccountDetail.setRecordType(rcdType);
				jointAccountDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(jointAccountDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private void processingJointAccExtendedFields(JointAccountDetail jointAccountDetail, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		boolean isSaveRecord = false;
		ExtendedFieldHeader extendedFieldHeader = jointAccountDetail.getCustomerDetails().getExtendedFieldHeader();

		if (extendedFieldHeader == null) {
			return;
		}

		StringBuilder tableName = new StringBuilder();
		tableName.append(extendedFieldHeader.getModuleName());
		tableName.append("_");
		tableName.append(extendedFieldHeader.getSubModuleName());
		tableName.append("_ED");

		ExtendedFieldRender extendedFieldRender = jointAccountDetail.getCustomerDetails().getExtendedFieldRender();
		if (StringUtils.isEmpty(tableType)) {
			extendedFieldRender.setRoleCode("");
			extendedFieldRender.setNextRoleCode("");
			extendedFieldRender.setTaskId("");
			extendedFieldRender.setNextTaskId("");
		}

		// Table Name addition for Audit
		extendedFieldRender.setTableName(tableName.toString());
		extendedFieldRender.setWorkflowId(0);

		// Add Common Fields
		Map<String, Object> mapValues = extendedFieldRender.getMapValues();

		String custCIF = jointAccountDetail.getCustCIF();
		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(custCIF, tableName.toString(), null);

		if (extFieldMap == null) {
			isSaveRecord = true;
		}
		if (isSaveRecord) {
			extendedFieldRender.setReference(custCIF);
			mapValues.put("Reference", extendedFieldRender.getReference());
			mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
		}

		mapValues.put("Version", extendedFieldRender.getVersion());
		mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
		mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
		mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
		mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
		mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
		mapValues.put("TaskId", extendedFieldRender.getTaskId());
		mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
		mapValues.put("RecordType", extendedFieldRender.getRecordType());
		mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());

		// Audit Details Preparation
		Map<String, Object> auditMapValues = extendedFieldRender.getMapValues();
		auditMapValues.put("Reference", extendedFieldRender.getReference());
		auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
		auditMapValues.put("Version", extendedFieldRender.getVersion());
		auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
		auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
		auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
		auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
		auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
		auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
		auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
		auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
		auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
		extendedFieldRender.setAuditMapValues(auditMapValues);

		if (isSaveRecord) {
			auditTranType = PennantConstants.TRAN_ADD;
			extendedFieldRender.setRecordType("");
			extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), "", tableName.toString());
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
			extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
					extendedFieldRender.getMapValues(), "", tableName.toString());
		}
		/*
		 * if (StringUtils.isNotBlank(extendedFieldRender.getReference())) { String[] extFields =
		 * PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender); AuditDetail auditDetail = new
		 * AuditDetail(auditTranType, auditDetails.size()+1, extFields[0], extFields[1],
		 * extendedFieldRender.getBefImage(), extendedFieldRender); auditDetail.setExtended(true);
		 * auditDetails.add(auditDetail); }
		 */

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<JointAccountDetail> jointAcDetailList, String tableType,
			String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (JointAccountDetail jointAccountDetail : jointAcDetailList) {
			jointAccountDetail.setWorkflowId(0);
			if (jointAccountDetail.isNewRecord()) {
				getJointAccountDetailDAO().save(jointAccountDetail, tableType);
			} else {
				// ### 10-05-2018 - Start- Development PSD 127038
				// Unable to delete co applicant once saved at DDE stage
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, jointAccountDetail.getRecordType())) {
					getJointAccountDetailDAO().delete(jointAccountDetail, tableType);
				} else {
					getJointAccountDetailDAO().update(jointAccountDetail, tableType);
				}
				// ### 10-05-2018 - End- Development PSD 127038
			}
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail,
					jointAccountDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
					jointAccountDetail.getBefImage(), jointAccountDetail));

			if (jointAccountDetail.getCustomerDetails() != null) {
				if (jointAccountDetail.getCustomerDetails().getExtendedFieldRender() != null) {
					boolean isSaveRecord = false;
					ExtendedFieldHeader extendedFieldHeader = jointAccountDetail.getCustomerDetails()
							.getExtendedFieldHeader();
					StringBuilder tableName = new StringBuilder();
					tableName.append(extendedFieldHeader.getModuleName());
					tableName.append("_");
					tableName.append(extendedFieldHeader.getSubModuleName());
					tableName.append("_ED");

					ExtendedFieldRender extendedFieldRender = jointAccountDetail.getCustomerDetails()
							.getExtendedFieldRender();
					if (StringUtils.isEmpty(tableType)) {
						extendedFieldRender.setRoleCode("");
						extendedFieldRender.setNextRoleCode("");
						extendedFieldRender.setTaskId("");
						extendedFieldRender.setNextTaskId("");
					}

					// Table Name addition for Audit
					extendedFieldRender.setTableName(tableName.toString());
					extendedFieldRender.setWorkflowId(0);

					// Add Common Fields
					Map<String, Object> mapValues = extendedFieldRender.getMapValues();

					String custCIF = jointAccountDetail.getCustCIF();
					Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(custCIF,
							tableName.toString(), null);

					if (extFieldMap == null) {
						isSaveRecord = true;
					}
					if (isSaveRecord) {
						extendedFieldRender.setReference(custCIF);
						mapValues.put("Reference", extendedFieldRender.getReference());
						mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
					}

					mapValues.put("Version", extendedFieldRender.getVersion());
					mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
					mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
					mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
					mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
					mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
					mapValues.put("TaskId", extendedFieldRender.getTaskId());
					mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
					mapValues.put("RecordType", extendedFieldRender.getRecordType());
					mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());

					// Audit Details Preparation
					Map<String, Object> auditMapValues = extendedFieldRender.getMapValues();
					auditMapValues.put("Reference", extendedFieldRender.getReference());
					auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
					auditMapValues.put("Version", extendedFieldRender.getVersion());
					auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
					auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
					auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
					auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
					auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
					auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
					auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
					auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
					auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
					extendedFieldRender.setAuditMapValues(auditMapValues);

					if (isSaveRecord) {
						auditTranType = PennantConstants.TRAN_ADD;
						extendedFieldRender.setRecordType("");
						extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), "", tableName.toString());
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
						extendedFieldRenderDAO.update(extendedFieldRender.getReference(),
								extendedFieldRender.getSeqNo(), extendedFieldRender.getMapValues(), "",
								tableName.toString());
					}
					/*
					 * if (StringUtils.isNotBlank(extendedFieldRender.getReference())) { String[] extFields =
					 * PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender); AuditDetail auditDetail = new
					 * AuditDetail(auditTranType, auditDetails.size()+1, extFields[0], extFields[1],
					 * extendedFieldRender.getBefImage(), extendedFieldRender); auditDetail.setExtended(true);
					 * auditDetails.add(auditDetail); }
					 */
				}
			}

		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<JointAccountDetail> jointAcDetailList, String tableType,
			String auditTranType, String finSourceId, Object apiHeader, String serviceName) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (JointAccountDetail jointAccountDetail : jointAcDetailList) {
			JointAccountDetail detail = new JointAccountDetail();
			BeanUtils.copyProperties(jointAccountDetail, detail);
			if (!jointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
				jointAccountDetail.setRoleCode("");
				jointAccountDetail.setNextRoleCode("");
				jointAccountDetail.setTaskId("");
				jointAccountDetail.setNextTaskId("");
				jointAccountDetail.setWorkflowId(0);
				jointAccountDetail.setRecordType("");

				getJointAccountDetailDAO().save(jointAccountDetail, tableType);
			}

			if (!StringUtils.equals(finSourceId, PennantConstants.FINSOURCE_ID_API) || apiHeader == null
					|| StringUtils.isNotBlank(serviceName)) {
				getJointAccountDetailDAO().delete(jointAccountDetail, "_Temp");
			}

			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail,
					jointAccountDetail.getExcludeFields());

			auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, auditDetails.size() + 1, fields[0], fields[1],
					detail.getBefImage(), detail));
			auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
					jointAccountDetail.getBefImage(), jointAccountDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<JointAccountDetail> jointAccountDetails, String tableType,
			String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		// 10-Jul-2018 BUG FIX related to Audit issue TktNo:126609
		int auditSeq = 1;
		for (JointAccountDetail jointAccountDetail : jointAccountDetails) {
			getJointAccountDetailDAO().delete(jointAccountDetail, tableType);
			String[] fields = PennantJavaUtil.getFieldDetails(jointAccountDetail,
					jointAccountDetail.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditSeq++, fields[0], fields[1],
					jointAccountDetail.getBefImage(), jointAccountDetail));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public AuditHeader doValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> doValidation(List<JointAccountDetail> jointAcDetailList, long workflowId, String method,
			String auditTranType, String usrLanguage) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = getAuditDetail(jointAcDetailList, auditTranType, method, workflowId);

		for (AuditDetail auditDetail : auditDetails) {
			validate(auditDetail, method, usrLanguage);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		JointAccountDetail jad = (JointAccountDetail) auditDetail.getModelData();
		JointAccountDetail tempJad = null;
		JointAccountDetail befJad = null;
		JointAccountDetail oldJointAccountDetail = null;

		if (jad.isWorkflow()) {
			tempJad = getJointAccountDetailDAO().getJointAccountDetailByRefId(jad.getFinID(), jad.getJointAccountId(),
					"_Temp");
		}

		befJad = getJointAccountDetailDAO().getJointAccountDetailByRefId(jad.getFinID(), jad.getJointAccountId(), "");
		oldJointAccountDetail = jad.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = jad.getFinReference();
		valueParm[1] = jad.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];

		if (jad.isNewRecord()) { // for New record or new record into work flow

			if (!jad.isWorkflow()) {// With out Work flow only new records
				if (befJad != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (jad.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																					// new
					if (befJad != null || tempJad != null) { // if records already exists
																// in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befJad == null || tempJad != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!jad.isWorkflow()) { // With out Work flow for update and delete

				if (befJad == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldJointAccountDetail != null
							&& !oldJointAccountDetail.getLastMntOn().equals(befJad.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempJad == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempJad != null && oldJointAccountDetail != null
						&& !oldJointAccountDetail.getLastMntOn().equals(tempJad.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !jad.isWorkflow()) {
			auditDetail.setBefImage(befJad);
		}
		return auditDetail;
	}

	/**
	 * getPrimaryExposureList
	 * 
	 * Return the list of primary finances Exposer List (self finances) for the corresponding Guarantor
	 * 
	 * @param GuarantorDetail (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail) {
		FinanceExposure overDueDetail = null;

		List<FinanceExposure> primaryExposureList = getJointAccountDetailDAO()
				.getPrimaryExposureList(jointAccountDetail);

		if (primaryExposureList != null && !primaryExposureList.isEmpty()) {
			for (FinanceExposure finExposer : primaryExposureList) {
				overDueDetail = getJointAccountDetailDAO().getOverDueDetails(finExposer);
				if (overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);
				}
			}
		}

		return primaryExposureList;
	}

	/**
	 * getSecondaryExposureList
	 * 
	 * Return the list of secondary finances Exposer List(where the Customer is having joint finances) for the
	 * corresponding Guarantor
	 * 
	 * @param GuarantorDetail (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail) {
		FinanceExposure overDueDetail = null;
		List<FinanceExposure> secondaryExposureList = getJointAccountDetailDAO()
				.getSecondaryExposureList(jointAccountDetail);

		if (secondaryExposureList != null && !secondaryExposureList.isEmpty()) {
			for (FinanceExposure finExposer : secondaryExposureList) {
				overDueDetail = getJointAccountDetailDAO().getOverDueDetails(finExposer);
				if (overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);
				}
			}
		}

		return secondaryExposureList;
	}

	/**
	 * getGuarantorExposureList
	 * 
	 * Return the list of secondary Gurantor Exposure List finances(where the Customer is Gurantor to others) for the
	 * corresponding Guarantor
	 * 
	 * @param GuarantorDetail (guarantorDetail)
	 * @return List<FinanceExposure>
	 */
	@Override
	public List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail) {
		FinanceExposure overDueDetail = null;

		List<FinanceExposure> guarantorExposureList = getJointAccountDetailDAO()
				.getGuarantorExposureList(jointAccountDetail);

		if (guarantorExposureList != null) {
			for (FinanceExposure finExposer : guarantorExposureList) {
				overDueDetail = getJointAccountDetailDAO().getOverDueDetails(finExposer);
				if (overDueDetail != null) {
					setExposerDetails(overDueDetail, finExposer);

				}
			}
		}

		return guarantorExposureList;

	}

	/**
	 * getExposureSummaryDetail
	 * 
	 * Sum of all the finances of financeAmount, currentExposer, overDueAmount respectively, For the corresponding
	 * Customer
	 * 
	 * @param List <FinanceExposure> (exposerList)
	 * @return exposerSummaryDetail
	 */
	@Override
	public FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList) {
		FinanceExposure exposerSummaryDetail = new FinanceExposure();
		BigDecimal finaceAmout = BigDecimal.ZERO;
		BigDecimal currentExposer = BigDecimal.ZERO;
		BigDecimal overDueAmount = BigDecimal.ZERO;

		int dftCurntEdtField = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

		if (exposerList != null && !exposerList.isEmpty()) {
			for (FinanceExposure financeExposure : exposerList) {
				finaceAmout = finaceAmout.add(financeExposure.getFinanceAmtinBaseCCY());
				currentExposer = currentExposer.add(financeExposure.getCurrentExpoSureinBaseCCY());
				if (financeExposure.getOverdueAmt() != null) {
					overDueAmount = overDueAmount.add(financeExposure.getOverdueAmtBaseCCY());
				}
			}
		}
		exposerSummaryDetail.setCcyEditField(dftCurntEdtField);
		exposerSummaryDetail.setFinanceAmtinBaseCCY(finaceAmout);
		exposerSummaryDetail.setCurrentExpoSureinBaseCCY(currentExposer);
		exposerSummaryDetail.setOverdueAmtBaseCCY(overDueAmount);

		return exposerSummaryDetail;
	}

	@Override
	public List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type) {
		return getJointAccountDetailDAO().getJointAccountDetailByFinRef(finID, type);
	}

	@Override
	public List<JointAccountDetail> getJointAccountDetailByFinRef(String finReference, String type) {
		return getJointAccountDetailDAO().getJointAccountDetailByFinRef(finReference, type);
	}

	@Override
	public JointAccountDetail getJointAccountDetailByRef(long finID, String custCIF, String type) {
		return getJointAccountDetailDAO().getJointAccountDetailByRef(finID, custCIF, type);
	}

	@Override
	public JointAccountDetail getJointAccountDetailByRef(String finReference, String custCIF, String type) {
		return getJointAccountDetailDAO().getJointAccountDetailByRef(finReference, custCIF, type);
	}

	/**
	 * getJoinAccountDetail
	 * 
	 * Return the list of joint account holders financial details and customer details based on the corresponding
	 * finance reference
	 * 
	 * @param String (finReference)
	 * @param String (tableType)
	 * @return List<JointAccountDetail>
	 */
	@Override
	public List<JointAccountDetail> getJoinAccountDetail(long finID, String tableType) {
		List<FinanceExposure> primaryList = null;
		List<FinanceExposure> secoundaryList = null;
		List<FinanceExposure> guarantorList = null;

		List<JointAccountDetail> jointAccountDetailList = getJointAccountDetailByFinRef(finID, tableType);

		if (jointAccountDetailList != null && !jointAccountDetailList.isEmpty()) {
			for (JointAccountDetail detail : jointAccountDetailList) {
				BigDecimal currentExpoSure = BigDecimal.ZERO;

				// set the primary exposer details to Joint Account Details
				primaryList = getJointAccountDetailDAO().getPrimaryExposureList(detail);
				currentExpoSure = doFillExposureDetails(primaryList, detail);
				detail.setPrimaryExposure(String.valueOf(currentExpoSure));

				// set the secondary exposer details to Joint Account
				secoundaryList = getJointAccountDetailDAO().getSecondaryExposureList(detail);
				currentExpoSure = doFillExposureDetails(secoundaryList, detail);
				detail.setSecondaryExposure(String.valueOf(currentExpoSure));

				// set the exposer details to Joint Account
				guarantorList = getJointAccountDetailDAO().getGuarantorExposureList(detail);
				currentExpoSure = doFillExposureDetails(guarantorList, detail);
				detail.setGuarantorExposure(String.valueOf(currentExpoSure));

				// set customer details
				detail.setCustomerIncomeList(getJointAccountIncomeList(detail.getCustID()));
				detail.setCustomerExtLiabilityList(getJointExtLiabilityByCustomer(detail.getCustID()));
				detail.setCustFinanceExposureList(getJointCustFinanceExposureByCustomer(
						new Customer(detail.getCustID(), detail.getCustCoreBank())));

				detail.setCustomerDetails(
						customerDataService.getCustomerDetailsbyID(detail.getCustID(), true, "_AView"));

			}
		}

		return jointAccountDetailList;
	}

	@Override
	public List<CustomerIncome> getJointAccountIncomeList(long custID) {
		return incomeDetailDAO.getIncomesByCustomer(custID, "_aview");
	}

	@Override
	public List<CustomerExtLiability> getJointExtLiabilityByCustomer(long custId) {
		CustomerExtLiability liability = new CustomerExtLiability();
		return externalLiabilityDAO.getLiabilities(liability.getCustId(), "");
	}

	@Override
	public List<FinanceEnquiry> getJointCustFinanceExposureByCustomer(Customer customer) {
		return getCustomerDAO().getCustomerFinanceDetailById(customer);
	}

	@Override
	public List<FinanceExposure> getJointExposureList(List<String> listCIF) {

		List<FinanceExposure> exposures = getJointAccountDetailDAO().getPrimaryExposureList(listCIF);
		exposures.addAll(getJointAccountDetailDAO().getSecondaryExposureList(listCIF));
		return exposures;
	}

	@Override
	public BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, JointAccountDetail detail) {
		BigDecimal currentExpoSure = BigDecimal.ZERO;
		if (primaryList != null && !primaryList.isEmpty()) {
			for (FinanceExposure exposer : primaryList) {
				if (exposer != null) {
					String toCcy = SysParamUtil.getAppCurrency();
					String fromCcy = exposer.getFinCCY();
					currentExpoSure = currentExpoSure
							.add(CalculationUtil.getConvertedAmount(fromCcy, toCcy, exposer.getCurrentExpoSure()));
					detail.setStatus(exposer.getStatus());
					detail.setWorstStatus(exposer.getWorstStatus());

					if (exposer.getOverdueAmt() != null && BigDecimal.ZERO.compareTo(exposer.getOverdueAmt()) > 0) {
						exposer.setOverdue(true);
					}
				}
			}
		}
		return currentExpoSure;
	}

	private void setExposerDetails(FinanceExposure overDueDetail, FinanceExposure finExposer) {
		BigDecimal finaceAmout;
		BigDecimal overDueAmount;
		BigDecimal exposerAmout;

		String toCcy = SysParamUtil.getAppCurrency();
		String fromCcy = finExposer.getFinCCY();

		if (finExposer.getFinanceAmt() != null) {
			finaceAmout = CalculationUtil.getConvertedAmount(fromCcy, toCcy, finExposer.getFinanceAmt());
			finExposer.setFinanceAmtinBaseCCY(finaceAmout);
		}

		if (finExposer.getCurrentExpoSure() != null) {
			exposerAmout = CalculationUtil.getConvertedAmount(fromCcy, toCcy, finExposer.getCurrentExpoSure());
			finExposer.setCurrentExpoSureinBaseCCY(exposerAmout);
		}

		if (overDueDetail.getOverdueAmt() != null && overDueDetail.getOverdueAmt().compareTo(BigDecimal.ZERO) > 0) {
			finExposer.setOverdueAmt(overDueDetail.getOverdueAmt());
			overDueAmount = CalculationUtil.getConvertedAmount(fromCcy, toCcy, overDueDetail.getOverdueAmt());
			finExposer.setOverdueAmtBaseCCY(overDueAmount);
			finExposer.setOverdue(true);
		}

		finExposer.setPastdueDays(overDueDetail.getPastdueDays());
	}

	private List<AuditDetail> getAuditDetail(List<JointAccountDetail> jointAccountDetailList, String auditTranType,
			String method, long workflowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		JointAccountDetail object = new JointAccountDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		for (int i = 0; i < jointAccountDetailList.size(); i++) {

			JointAccountDetail jointAccountDetail = jointAccountDetailList.get(i);
			jointAccountDetail.setWorkflowId(workflowId);
			boolean isRcdType = false;

			if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				jointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				jointAccountDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| jointAccountDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			if (StringUtils.isNotEmpty(jointAccountDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						jointAccountDetail.getBefImage(), jointAccountDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/***
	 * Method to process documents that were retrived from DMS and mapped to co-applicants
	 * 
	 * @param document
	 * @param type
	 * @param custId
	 */
	public void processDocument(CustomerDocument document, String type, long custId) {
		logger.debug(Literal.ENTERING);
		CustomerDocument customerDocument = document;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		String rcdType = "";
		String recordStatus = "";
		if (StringUtils.isEmpty(type)) {
			approveRec = true;
			customerDocument.setRoleCode("");
			customerDocument.setNextRoleCode("");
			customerDocument.setTaskId("");
			customerDocument.setNextTaskId("");
		}

		customerDocument.setWorkflowId(0);
		customerDocument.setCustID(custId);

		if (StringUtils.trimToEmpty(customerDocument.getRecordType()).equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			saveRecord = true;
		} else if (StringUtils.trimToEmpty(customerDocument.getRecordType())
				.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
			updateRecord = true;
		}
		if (approveRec) {
			rcdType = StringUtils.trimToEmpty(customerDocument.getRecordType());
			recordStatus = customerDocument.getRecordStatus();
			customerDocument.setRecordType("");
			customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}
		if (saveRecord) {
			saveDocument(DMSModule.CUSTOMER, null, customerDocument);
			customerDocumentDAO.save(customerDocument, type);
		}

		if (updateRecord) {
			saveDocument(DMSModule.CUSTOMER, null, customerDocument);
			customerDocumentDAO.update(customerDocument, type);
		}

		if (deleteRecord) {
			customerDocumentDAO.delete(customerDocument, type);
		}

		if (approveRec) {
			customerDocument.setRecordType(rcdType);
			customerDocument.setRecordStatus(recordStatus);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> getCustIdsByFinID(long finID) {
		return jointAccountDetailDAO.getCustIdsByFinID(finID);
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

}