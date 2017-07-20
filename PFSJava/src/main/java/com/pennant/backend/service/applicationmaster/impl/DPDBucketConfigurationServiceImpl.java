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
 * FileName    		:  DPDBucketConfigurationServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.DPDBucketConfigurationDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.DPDBucketConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>DPDBucketConfiguration</b>.<br>
 */
public class DPDBucketConfigurationServiceImpl extends GenericService<DPDBucketConfiguration> implements
		DPDBucketConfigurationService {
	private static final Logger			logger	= Logger.getLogger(DPDBucketConfigurationServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private DPDBucketConfigurationDAO	dPDBucketConfigurationDAO;

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
	 * @return the dPDBucketConfigurationDAO
	 */
	public DPDBucketConfigurationDAO getDPDBucketConfigurationDAO() {
		return dPDBucketConfigurationDAO;
	}

	/**
	 * @param dPDBucketConfigurationDAO
	 *            the dPDBucketConfigurationDAO to set
	 */
	public void setDPDBucketConfigurationDAO(DPDBucketConfigurationDAO dPDBucketConfigurationDAO) {
		this.dPDBucketConfigurationDAO = dPDBucketConfigurationDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * DPDBUCKETSCONFIG/DPDBUCKETSCONFIG_Temp by using DPDBUCKETSCONFIGDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using DPDBUCKETSCONFIGDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtDPDBUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader)
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

		DPDBucketConfiguration dPDBucketConfiguration = (DPDBucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (dPDBucketConfiguration.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (dPDBucketConfiguration.isNew()) {
			dPDBucketConfiguration.setId(Long.parseLong(getDPDBucketConfigurationDAO().save(dPDBucketConfiguration,
					tableType)));
			auditHeader.getAuditDetail().setModelData(dPDBucketConfiguration);
			auditHeader.setAuditReference(String.valueOf(dPDBucketConfiguration.getConfigID()));
		} else {
			getDPDBucketConfigurationDAO().update(dPDBucketConfiguration, tableType);
		}
		
		if (TableType.MAIN_TAB.equals(tableType)) {
			FinanceConfigCache.clearDPDBucketConfigurationCache(dPDBucketConfiguration.getProductCode());
		}
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * DPDBUCKETSCONFIG by using DPDBUCKETSCONFIGDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtDPDBUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader)
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

		DPDBucketConfiguration dPDBucketConfiguration = (DPDBucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		getDPDBucketConfigurationDAO().delete(dPDBucketConfiguration, TableType.MAIN_TAB);
		FinanceConfigCache.clearDPDBucketConfigurationCache(dPDBucketConfiguration.getProductCode());
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getDPDBUCKETSCONFIG fetch the details by using DPDBUCKETSCONFIGDAO's getDPDBUCKETSCONFIGById method.
	 * 
	 * @param configID
	 *            configID of the DPDBucketConfiguration.
	 * @return DPDBUCKETSCONFIG
	 */
	@Override
	public DPDBucketConfiguration getDPDBucketConfiguration(long configID) {
		return getDPDBucketConfigurationDAO().getDPDBucketConfiguration(configID, "_View");
	}

	/**
	 * getApprovedDPDBUCKETSCONFIGById fetch the details by using DPDBUCKETSCONFIGDAO's getDPDBUCKETSCONFIGById method .
	 * with parameter id and type as blank. it fetches the approved records from the DPDBUCKETSCONFIG.
	 * 
	 * @param configID
	 *            configID of the DPDBucketConfiguration. (String)
	 * @return DPDBUCKETSCONFIG
	 */
	public DPDBucketConfiguration getApprovedDPDBucketConfiguration(long configID) {
		return getDPDBucketConfigurationDAO().getDPDBucketConfiguration(configID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDPDBucketConfigurationDAO().delete
	 * with parameters dPDBucketConfiguration,"" b) NEW Add new record in to main table by using
	 * getDPDBucketConfigurationDAO().save with parameters dPDBucketConfiguration,"" c) EDIT Update record in the main
	 * table by using getDPDBucketConfigurationDAO().update with parameters dPDBucketConfiguration,"" 3) Delete the
	 * record from the workFlow table by using getDPDBucketConfigurationDAO().delete with parameters
	 * dPDBucketConfiguration,"_Temp" 4) Audit the record in to AuditHeader and AdtDPDBUCKETSCONFIG by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtDPDBUCKETSCONFIG
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		DPDBucketConfiguration dPDBucketConfiguration = new DPDBucketConfiguration();
		BeanUtils.copyProperties((DPDBucketConfiguration) auditHeader.getAuditDetail().getModelData(),
				dPDBucketConfiguration);

		getDPDBucketConfigurationDAO().delete(dPDBucketConfiguration, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(dPDBucketConfiguration.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					dPDBucketConfigurationDAO.getDPDBucketConfiguration(dPDBucketConfiguration.getConfigID(), ""));
		}

		if (dPDBucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDPDBucketConfigurationDAO().delete(dPDBucketConfiguration, TableType.MAIN_TAB);
		
		} else {
			dPDBucketConfiguration.setRoleCode("");
			dPDBucketConfiguration.setNextRoleCode("");
			dPDBucketConfiguration.setTaskId("");
			dPDBucketConfiguration.setNextTaskId("");
			dPDBucketConfiguration.setWorkflowId(0);

			if (dPDBucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dPDBucketConfiguration.setRecordType("");
				getDPDBucketConfigurationDAO().save(dPDBucketConfiguration, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dPDBucketConfiguration.setRecordType("");
				getDPDBucketConfigurationDAO().update(dPDBucketConfiguration, TableType.MAIN_TAB);
			}
		}
		
		FinanceConfigCache.clearDPDBucketConfigurationCache(dPDBucketConfiguration.getProductCode());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dPDBucketConfiguration);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getDPDBucketConfigurationDAO().delete with parameters dPDBucketConfiguration,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtDPDBUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		DPDBucketConfiguration dPDBucketConfiguration = (DPDBucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDPDBucketConfigurationDAO().delete(dPDBucketConfiguration, TableType.TEMP_TAB);

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
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getDPDBucketConfigurationDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		DPDBucketConfiguration dPDBucketConfiguration = (DPDBucketConfiguration) auditDetail.getModelData();

		// Check the unique keys.
		if (dPDBucketConfiguration.isNew()
				&& dPDBucketConfigurationDAO.isDuplicateKey(dPDBucketConfiguration.getConfigID(),
						dPDBucketConfiguration.getProductCode(), dPDBucketConfiguration.getBucketID(),
						dPDBucketConfiguration.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_ProductCode") + ": "
					+ dPDBucketConfiguration.getProductCode();
			parameters[1] = PennantJavaUtil.getLabel("label_BucketID") + ": " + dPDBucketConfiguration.getBucketID();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		if (!StringUtils.trimToEmpty(dPDBucketConfiguration.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL) && StringUtils.trimToEmpty(dPDBucketConfiguration.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
			int count = dPDBucketConfigurationDAO.getByProductCode(dPDBucketConfiguration.getProductCode(),
					dPDBucketConfiguration.getDueDays(), "");

			if (count != 0) {

				String[] errParm = new String[2];
				String[] valueParm = new String[2];
				valueParm[0] = dPDBucketConfiguration.getProductCode();
				valueParm[1] = String.valueOf(dPDBucketConfiguration.getDueDays());
				errParm[0] = PennantJavaUtil.getLabel("label_ProductCode") + ":" + valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_DueDays") + ":" + valueParm[1];

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41015",
						errParm, valueParm), usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}