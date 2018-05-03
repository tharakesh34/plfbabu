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
 * FileName    		:  InsuranceTypeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.InsuranceTypeDAO;
import com.pennant.backend.dao.applicationmaster.InsuranceTypeProviderDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.insurancedetails.FinInsurancesDAO;
import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InsuranceTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>InsuranceType</b>.<br>
 * 
 */
public class InsuranceTypeServiceImpl extends GenericService<InsuranceType> implements InsuranceTypeService {
	
	private static final Logger			logger	= Logger.getLogger(InsuranceTypeServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private InsuranceTypeDAO			insuranceTypeDAO;
	private InsuranceTypeProviderDAO	insuranceTypeProviderDAO;
	private FinInsurancesDAO 			finInsurancesDAO; 
	
	
	public InsuranceTypeServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	 * @return the insuranceTypeDAO
	 */
	public InsuranceTypeDAO getInsuranceTypeDAO() {
		return insuranceTypeDAO;
	}

	/**
	 * @param insuranceTypeDAO
	 *            the insuranceTypeDAO to set
	 */
	public void setInsuranceTypeDAO(InsuranceTypeDAO insuranceTypeDAO) {
		this.insuranceTypeDAO = insuranceTypeDAO;
	}

	public InsuranceTypeProviderDAO getInsuranceTypeProviderDAO() {
		return insuranceTypeProviderDAO;
	}

	public void setInsuranceTypeProviderDAO(InsuranceTypeProviderDAO insuranceTypeProviderDAO) {
		this.insuranceTypeProviderDAO = insuranceTypeProviderDAO;
	}
	
	public FinInsurancesDAO getFinInsurancesDAO() {
		return finInsurancesDAO;
	}

	public void setFinInsurancesDAO(FinInsurancesDAO finInsurancesDAO) {
		this.finInsurancesDAO = finInsurancesDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table InsuranceType/InsuranceType_Temp
	 * by using InsuranceTypeDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using InsuranceTypeDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtInsuranceType by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();

		if (insuranceType.isWorkflow()) {
			tableType = "_Temp";
		}

		if (insuranceType.isNew()) {
			getInsuranceTypeDAO().save(insuranceType, tableType);
		} else {
			getInsuranceTypeDAO().update(insuranceType, tableType);
		}

		//Insurance Type Provider
		if (insuranceType.getInsuranceProviders() != null && insuranceType.getInsuranceProviders().size() > 0) {
			List<AuditDetail> details = insuranceType.getAuditDetailMap().get("InsuranceTypeProvider");
			details = processingInsProviderDetails(insuranceType, details, tableType);
			auditDetails.addAll(details);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Finance Flags Information
	 * 
	 * @param auditDetails
	 * @param type
	 * @param finReference
	 * @return
	 */
	private List<AuditDetail> processingInsProviderDetails(InsuranceType insuranceType, List<AuditDetail> auditDetails,
			String type) {

		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {
			InsuranceTypeProvider insTypeProvider = (InsuranceTypeProvider) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				insTypeProvider.setRoleCode("");
				insTypeProvider.setNextRoleCode("");
				insTypeProvider.setTaskId("");
				insTypeProvider.setNextTaskId("");
			}
			insTypeProvider.setInsuranceType(insuranceType.getInsuranceType());
			insTypeProvider.setWorkflowId(0);
			if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (insTypeProvider.isNewRecord()) {
				saveRecord = true;
				if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					insTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					insTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					insTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (insTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (insTypeProvider.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = insTypeProvider.getRecordType();
				recordStatus = insTypeProvider.getRecordStatus();
				insTypeProvider.setRecordType("");
				insTypeProvider.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getInsuranceTypeProviderDAO().save(insTypeProvider, type);
			}
			if (updateRecord) {
				getInsuranceTypeProviderDAO().update(insTypeProvider, type);
			}
			if (deleteRecord) {
				getInsuranceTypeProviderDAO().delete(insTypeProvider, type);
			}
			if (approveRec) {
				insTypeProvider.setRecordType(rcdType);
				insTypeProvider.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(insTypeProvider);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InsuranceType by using InsuranceTypeDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtInsuranceType by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();
		getInsuranceTypeDAO().delete(insuranceType, "");

		auditHeader.setAuditDetails(processChildsAudit(deleteInsTypeProviders(insuranceType, "",
				auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getInsuranceTypeById fetch the details by using InsuranceTypeDAO's getInsuranceTypeById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InsuranceType
	 */
	@Override
	public InsuranceType getInsuranceTypeById(String id, String type) {

		InsuranceType insuranceType = new InsuranceType();
		insuranceType = getInsuranceTypeDAO().getInsuranceTypeById(id, type);
		insuranceType.setInsuranceProviders(getInsuranceTypeDAO().getProvidersByInstype(
				insuranceType.getInsuranceType(), type));
		return insuranceType;

	}

	/**
	 * getApprovedInsuranceTypeById fetch the details by using InsuranceTypeDAO's getInsuranceTypeById method . with
	 * parameter id and type as blank. it fetches the approved records from the InsuranceType.
	 * 
	 * @param id
	 *            (String)
	 * @return InsuranceType
	 */
	public InsuranceType getApprovedInsuranceTypeById(String id) {
		return getInsuranceTypeDAO().getInsuranceTypeById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getInsuranceTypeDAO().delete with
	 * parameters insuranceType,"" b) NEW Add new record in to main table by using getInsuranceTypeDAO().save with
	 * parameters insuranceType,"" c) EDIT Update record in the main table by using getInsuranceTypeDAO().update with
	 * parameters insuranceType,"" 3) Delete the record from the workFlow table by using getInsuranceTypeDAO().delete
	 * with parameters insuranceType,"_Temp" 4) Audit the record in to AuditHeader and AdtInsuranceType by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtInsuranceType by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		InsuranceType insuranceType = new InsuranceType();
		BeanUtils.copyProperties((InsuranceType) auditHeader.getAuditDetail().getModelData(), insuranceType);

		if (insuranceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			//List
			auditDetails.addAll(deleteInsTypeProviders(insuranceType, "", tranType));
			getInsuranceTypeDAO().delete(insuranceType, "");

		} else {
			insuranceType.setRoleCode("");
			insuranceType.setNextRoleCode("");
			insuranceType.setTaskId("");
			insuranceType.setNextTaskId("");
			insuranceType.setWorkflowId(0);

			if (insuranceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				insuranceType.setRecordType("");
				getInsuranceTypeDAO().save(insuranceType, "");

			} else {
				tranType = PennantConstants.TRAN_UPD;
				insuranceType.setRecordType("");
				getInsuranceTypeDAO().update(insuranceType, "");
			}

			//Insurance Type providers
			if (insuranceType.getInsuranceProviders() != null && insuranceType.getInsuranceProviders().size() > 0) {
				List<AuditDetail> details = insuranceType.getAuditDetailMap().get("InsuranceTypeProvider");
				details = processingInsProviderDetails(insuranceType, details, "");
				auditDetails.addAll(details);
			}
		}

		getInsuranceTypeDAO().delete(insuranceType, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		//List

		auditHeader.setAuditDetails(deleteInsTypeProviders(insuranceType, "_Temp", auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceType(), insuranceType.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				insuranceType.getBefImage(), insuranceType));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(insuranceType);

		//List
		auditHeader.setAuditDetails(processChildsAudit(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {

		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof InsuranceTypeProvider) {
				rcdType = ((InsuranceTypeProvider) object).getRecordType();
			}
			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	public List<AuditDetail> deleteInsTypeProviders(InsuranceType insuranceType, String tableType, String auditTranType) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (insuranceType.getInsuranceProviders() == null || insuranceType.getInsuranceProviders().isEmpty()) {
			return auditDetails;
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceTypeProvider(),
				new InsuranceTypeProvider().getExcludeFields());

		for (int i = 0; i < insuranceType.getInsuranceProviders().size(); i++) {
			InsuranceTypeProvider insTypeProvider = insuranceType.getInsuranceProviders().get(i);

			if (StringUtils.isNotEmpty(insTypeProvider.getRecordType()) || StringUtils.isEmpty(tableType)) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], insTypeProvider
						.getBefImage(), insTypeProvider));
			}
			getInsuranceTypeProviderDAO().delete(insuranceType.getInsuranceProviders().get(i), tableType);
		}

		return auditDetails;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getInsuranceTypeDAO().delete with parameters insuranceType,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtInsuranceType by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInsuranceTypeDAO().delete(insuranceType, "_Temp");

		// InsurancesType Provider List
		auditHeader.setAuditDetails(processChildsAudit(deleteInsTypeProviders(insuranceType, "_Temp",
				auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		//List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChild(auditHeader, method, auditHeader.getUsrLanguage()));
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private List<ErrorDetail> validateChild(AuditHeader auditHeader, String usrLanguage, String method) {
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;
		//INsurance TypeProvider
		if (insuranceType.getAuditDetailMap().get("InsuranceTypeProvider") != null) {
			auditDetails = insuranceType.getAuditDetailMap().get("InsuranceTypeProvider");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = validationInsuranceTypeProvider(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		return errorDetails;
	}

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {

		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (insuranceType.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		if (insuranceType.getInsuranceProviders() != null && insuranceType.getInsuranceProviders().size() > 0) {
			auditDetailMap.put("InsuranceTypeProvider",
					setInsTypeProvidersAuditData(insuranceType, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("InsuranceTypeProvider"));
		}

		insuranceType.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(insuranceType);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;

	}

	private List<AuditDetail> setInsTypeProvidersAuditData(InsuranceType insuranceType, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new InsuranceTypeProvider(),
				new InsuranceTypeProvider().getExcludeFields());
		for (int i = 0; i < insuranceType.getInsuranceProviders().size(); i++) {
			InsuranceTypeProvider insuranceTypeProvider = insuranceType.getInsuranceProviders().get(i);

			if (StringUtils.isEmpty(insuranceTypeProvider.getRecordType())) {
				continue;
			}

			insuranceTypeProvider.setInsuranceType(insuranceType.getInsuranceType());
			insuranceTypeProvider.setWorkflowId(insuranceType.getWorkflowId());
			boolean isRcdType = false;
			if (insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				insuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				insuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				insuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				insuranceTypeProvider.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| insuranceTypeProvider.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			insuranceTypeProvider.setRecordStatus(insuranceType.getRecordStatus());
			insuranceTypeProvider.setUserDetails(insuranceType.getUserDetails());
			insuranceTypeProvider.setLastMntOn(insuranceType.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], insuranceTypeProvider
					.getBefImage(), insuranceTypeProvider));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();
		//String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (insuranceType.isWorkflow()) {
				//auditTranType = PennantConstants.TRAN_WF;
			}
		}
		auditHeader.getAuditDetail().setModelData(insuranceType);
		auditHeader.setAuditDetails(auditDetails);
		return auditHeader;
	}

	private AuditDetail validationInsuranceTypeProvider(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		InsuranceTypeProvider insuranceTypeProvider = (InsuranceTypeProvider) auditDetail.getModelData();

		InsuranceTypeProvider tempInsuranceTypeProvider = null;
		if (insuranceTypeProvider.isWorkflow()) {
			tempInsuranceTypeProvider = getInsuranceTypeProviderDAO().getInsuranceTypeProviderByID(
					insuranceTypeProvider, "_Temp");
		}
		InsuranceTypeProvider befInsuranceTypeProvider = getInsuranceTypeProviderDAO().getInsuranceTypeProviderByID(
				insuranceTypeProvider, "");

		InsuranceTypeProvider oldInsuranceTypeProvider = insuranceTypeProvider.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[2];
		valueParm[0] = insuranceTypeProvider.getInsuranceType();
		valueParm[1] = insuranceTypeProvider.getProviderCode();
		errParm[0] = PennantJavaUtil.getLabel("label_InsuranceType") + ":" + valueParm[0] + ","
				+ PennantJavaUtil.getLabel("label_TakafulProviderDialog_TakafulCode.value") + ":" + valueParm[1];

		if (insuranceTypeProvider.isNew()) { // for New record or new record into work flow

			if (!insuranceTypeProvider.isWorkflow()) {// With out Work flow only new records  
				if (befInsuranceTypeProvider != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (insuranceTypeProvider.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befInsuranceTypeProvider != null || tempInsuranceTypeProvider != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befInsuranceTypeProvider == null || tempInsuranceTypeProvider != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!insuranceTypeProvider.isWorkflow()) { // With out Work flow for update and delete

				if (befInsuranceTypeProvider == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldInsuranceTypeProvider != null
							&& !oldInsuranceTypeProvider.getLastMntOn().equals(befInsuranceTypeProvider.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempInsuranceTypeProvider == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempInsuranceTypeProvider != null && oldInsuranceTypeProvider != null
						&& !oldInsuranceTypeProvider.getLastMntOn().equals(tempInsuranceTypeProvider.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !insuranceTypeProvider.isWorkflow()) {
			auditDetail.setBefImage(befInsuranceTypeProvider);
		}

		return auditDetail;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getInsuranceTypeDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		InsuranceType insuranceType = (InsuranceType) auditDetail.getModelData();

		InsuranceType tempInsuranceType = null;
		if (insuranceType.isWorkflow()) {
			tempInsuranceType = getInsuranceTypeDAO().getInsuranceTypeById(insuranceType.getId(), "_Temp");
		}
		InsuranceType befInsuranceType = getInsuranceTypeDAO().getInsuranceTypeById(insuranceType.getId(), "");

		InsuranceType oldInsuranceType = insuranceType.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = insuranceType.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_InsuranceType") + ":" + valueParm[0];

		if (insuranceType.isNew()) { // for New record or new record into work flow

			if (!insuranceType.isWorkflow()) {// With out Work flow only new records  
				if (befInsuranceType != null) { // Record Already Exists in the table then error  
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm));
				}
			} else { // with work flow
				if (insuranceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befInsuranceType != null || tempInsuranceType != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,
								valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befInsuranceType == null || tempInsuranceType != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
								valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!insuranceType.isWorkflow()) { // With out Work flow for update and delete

				if (befInsuranceType == null) { // if records not exists in the main table
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm));
				} else {
					if (oldInsuranceType != null
							&& !oldInsuranceType.getLastMntOn().equals(befInsuranceType.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,
									valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,
									valueParm));
						}
					}
				}
			} else {

				if (tempInsuranceType == null) { // if records not exists in the Work flow table 
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}

				if (tempInsuranceType != null && oldInsuranceType != null
						&& !oldInsuranceType.getLastMntOn().equals(tempInsuranceType.getLastMntOn())) {
					auditDetail
							.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm));
				}
			}
		}
		
		//Validate if the insurance Type assigned to any Finance
		if(StringUtils.equals(insuranceType.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED) 
				&& StringUtils.equals(insuranceType.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {

			List<FinInsurances> finInsurances = getFinInsurancesDAO().getInsurancesList(
					insuranceType.getInsuranceType(), "_view");
			if (finInsurances != null) {
				String[][] parms = new String[2][1];
				parms[1][0] = insuranceType.getInsuranceType();
				parms[0][0] = PennantJavaUtil.getLabel("label_TakafulProviderDialog_TakafulCode.value") + ":"
						+ parms[1][0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
						"41006", parms[0], parms[1]), usrLanguage));

			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !insuranceType.isWorkflow()) {
			auditDetail.setBefImage(befInsuranceType);
		}

		return auditDetail;
	}

}