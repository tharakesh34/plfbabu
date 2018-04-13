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
 * FileName    		:  ExtFieldConfigServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.staticparms.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.extendedfields.ExtendedFieldsValidation;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * Service implementation for methods that depends on <b>ExtFieldConfig</b>.<br>
 * 
 */
public class ExtFieldConfigServiceImpl extends GenericService<ExtendedFieldHeader> implements ExtFieldConfigService {
	private static final Logger			logger	= Logger.getLogger(ExtFieldConfigServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private ExtendedFieldsValidation	extendedFieldsValidation;
	private ExtendedFieldDetailDAO		extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO		extendedFieldHeaderDAO;
	private SecurityRightDAO			securityRightDAO;

	public ExtFieldConfigServiceImpl() {
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

	public ExtendedFieldsValidation getExtendedFieldsValidation() {
		if (extendedFieldsValidation == null) {
			this.extendedFieldsValidation = new ExtendedFieldsValidation(extendedFieldDetailDAO, extendedFieldHeaderDAO,
					securityRightDAO);
		}
		return this.extendedFieldsValidation;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public void setSecurityRightDAO(SecurityRightDAO securityRightDAO) {
		this.securityRightDAO = securityRightDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ExtFieldConfig/ExtFieldConfig_Temp by using ExtFieldConfigDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using ExtFieldConfigDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtExtFieldConfig by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tableType = "";
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		if (extendedFieldHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		//ExtendedFieldHeader processing
		List<AuditDetail> headerDetail = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
		ExtendedFieldHeader extFieldHeader = (ExtendedFieldHeader) headerDetail.get(0).getModelData();

		long moduleId;
		if (extFieldHeader.isNew()) {
			moduleId = getExtendedFieldHeaderDAO().save(extFieldHeader, tableType);

			//Setting Module ID to List
			List<ExtendedFieldDetail> list = extFieldHeader.getExtendedFieldDetails();
			if (list != null && list.size() > 0) {
				for (ExtendedFieldDetail ext : list) {
					ext.setModuleId(moduleId);
				}
			}
		} else {
			getExtendedFieldHeaderDAO().update(extFieldHeader, tableType);
		}

		// Processing Extended Field Details List
		if (extFieldHeader.getExtendedFieldDetails() != null && extFieldHeader.getExtendedFieldDetails().size() > 0) {
			List<AuditDetail> details = extFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
			details = getExtendedFieldsValidation().processingExtendeFieldList(details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ExtFieldConfig by using ExtFieldConfigDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtExtFieldConfig by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		//ExtendedFieldHeader
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.add(new AuditDetail(auditHeader.getAuditTranType(), 1, extendedFieldHeader.getBefImage(),
				extendedFieldHeader));
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		// Table dropping in DB for Configured Collateral Type Details
		getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(),
				extendedFieldHeader.getSubModuleName());

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getExtFieldConfigById fetch the details by using ExtFieldConfigDAO's getExtFieldConfigById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return ExtFieldConfig
	 */
	@Override
	public ExtendedFieldHeader getExtendedFieldHeaderByModule(String moduleName, String subModuleName) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldHeader extFldHeader = null;

		extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(moduleName, subModuleName,
				"_View");
		if (extFldHeader != null) {
			extFldHeader.setExtendedFieldDetails(
					getExtendedFieldDetailDAO().getExtendedFieldDetailById(extFldHeader.getModuleId(), "_View"));
		}

		logger.debug(Literal.LEAVING);
		return extFldHeader;
	}

	/**
	 * getApprovedExtFieldConfigById fetch the details by using ExtFieldConfigDAO's getExtFieldConfigById method . with
	 * parameter id and type as blank. it fetches the approved records from the ExtFieldConfig.
	 * 
	 * @param id
	 *            (String)
	 * @return ExtFieldConfig
	 */
	@Override
	public ExtendedFieldHeader getApprovedExtendedFieldHeaderByModule(String moduleName, String subModuleName) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldHeader extFldHeader = null;

		extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(moduleName, subModuleName,
				"_AView");
		if (extFldHeader != null) {
			extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(
					extFldHeader.getModuleId(), ExtendedFieldConstants.EXTENDEDTYPE_EXTENDEDFIELD,"_AView"));
		}

		logger.debug(Literal.LEAVING);
		return extFldHeader;

	}
	
	/**
	 * getApprovedExtFieldConfigById fetch the details by using ExtFieldConfigDAO's getExtFieldConfigById method . with
	 * parameter id and type as blank. it fetches the approved records from the ExtFieldConfig.
	 * 
	 * @param id
	 *            (String)
	 * @return ExtFieldConfig
	 */
	@Override
	public ExtendedFieldHeader getApprovedExtendedFieldHeaderByModule(String moduleName, String subModuleName, int extendedType) {
		logger.debug(Literal.ENTERING);
		
		ExtendedFieldHeader extFldHeader = null;
		
		extFldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(moduleName, subModuleName,
				"_AView");
		if (extFldHeader != null) {
			extFldHeader.setExtendedFieldDetails(getExtendedFieldDetailDAO().getExtendedFieldDetailById(
					extFldHeader.getModuleId(), extendedType,"_AView"));
		}
		
		logger.debug(Literal.LEAVING);
		return extFldHeader;
		
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getExtFieldConfigDAO().delete with
	 * parameters ExtFieldConfig,"" b) NEW Add new record in to main table by using getExtFieldConfigDAO().save with
	 * parameters ExtFieldConfig,"" c) EDIT Update record in the main table by using getExtFieldConfigDAO().update with
	 * parameters ExtFieldConfig,"" 3) Delete the record from the workFlow table by using getExtFieldConfigDAO().delete
	 * with parameters ExtFieldConfig,"_Temp" 4) Audit the record in to AuditHeader and AdtExtFieldConfig by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtExtFieldConfig by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties((ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData(),
				extendedFieldHeader);

		// Extended field Details

		if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			// Table dropping in DB for Configured Asset Details
			getExtendedFieldHeaderDAO().dropTable(extendedFieldHeader.getModuleName(),
					extendedFieldHeader.getSubModuleName());
			auditDetails.addAll(listDeletion(extendedFieldHeader, "", auditHeader.getAuditTranType()));
			getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "");

		} else {
			extendedFieldHeader.setRoleCode("");
			extendedFieldHeader.setNextRoleCode("");
			extendedFieldHeader.setTaskId("");
			extendedFieldHeader.setNextTaskId("");
			extendedFieldHeader.setWorkflowId(0);
			extendedFieldHeader.setRoleCode("");
			extendedFieldHeader.setNextRoleCode("");
			extendedFieldHeader.setTaskId("");
			extendedFieldHeader.setNextTaskId("");
			extendedFieldHeader.setWorkflowId(0);
			if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().save(extendedFieldHeader, "");

				// Table creation in DB for Newly created Configuration Type Details
				getExtendedFieldHeaderDAO().createTable(extendedFieldHeader.getModuleName(),
						extendedFieldHeader.getSubModuleName());

			} else {
				tranType = PennantConstants.TRAN_UPD;
				extendedFieldHeader.setRecordType("");
				getExtendedFieldHeaderDAO().update(extendedFieldHeader, "");
			}
			auditDetails.add(new AuditDetail(tranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader));

			if (extendedFieldHeader.getExtendedFieldDetails() != null
					&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
				List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
				details = getExtendedFieldsValidation().processingExtendeFieldList(details, "");
				auditDetails.addAll(details);
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();

		//Extended filed header
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailList.add(new AuditDetail(tranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader));

		//Extended Field Detail List
		auditDetailList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetailList);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		return auditHeader;
	}

	public List<AuditDetail> listDeletion(ExtendedFieldHeader extendedFieldHeader, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (extendedFieldHeader.getExtendedFieldDetails() != null
				&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());
			for (int i = 0; i < extendedFieldHeader.getExtendedFieldDetails().size(); i++) {
				ExtendedFieldDetail extendedFieldDetail = extendedFieldHeader.getExtendedFieldDetails().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						extendedFieldDetail.getBefImage(), extendedFieldDetail));
			}
			getExtendedFieldDetailDAO().deleteByExtendedFields(extendedFieldHeader.getId(), tableType);
		}
		logger.debug(Literal.LEAVING);
		return auditList;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getExtFieldConfigDAO().delete with parameters ExtFieldConfig,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtExtFieldConfig by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		//ExtendedFieldHeader
		getExtendedFieldHeaderDAO().delete(extendedFieldHeader, "_Temp");
		auditDetailsList.addAll(listDeletion(extendedFieldHeader, "_Temp", auditHeader.getAuditTranType()));

		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		//Extended field details
		auditHeader = getAuditDetails(auditHeader, method);

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = extendedFieldHeader.getUserDetails().getLanguage();

		//Extended field details
		if (extendedFieldHeader != null) {
			List<AuditDetail> details = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldHeader");
			AuditDetail detail = getExtendedFieldsValidation().extendedFieldsHeaderValidation(details.get(0), method,
					usrLanguage);
			auditDetails.add(detail);

			if (extendedFieldHeader.getExtendedFieldDetails() != null
					&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
				List<AuditDetail> detailList = extendedFieldHeader.getAuditDetailMap().get("ExtendedFieldDetails");
				detailList = getExtendedFieldsValidation().extendedFieldsListValidation(detailList, method,
						usrLanguage);
				auditDetails.addAll(detailList);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (extendedFieldHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//Audit Detail Preparation for Extended Field Header
		AuditDetail auditDetail = new AuditDetail(auditTranType, 1, extendedFieldHeader.getBefImage(),
				extendedFieldHeader);
		List<AuditDetail> auditDetailHeaderList = new ArrayList<AuditDetail>();
		auditDetailHeaderList.add(auditDetail);
		auditDetailMap.put("ExtendedFieldHeader", auditDetailHeaderList);

		//Audit Detail Preparation for Extended Field Detail
		if (extendedFieldHeader.getExtendedFieldDetails() != null
				&& extendedFieldHeader.getExtendedFieldDetails().size() > 0) {
			auditDetailMap.put("ExtendedFieldDetails", getExtendedFieldsValidation()
					.setExtendedFieldsAuditData(extendedFieldHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		extendedFieldHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(extendedFieldHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

}