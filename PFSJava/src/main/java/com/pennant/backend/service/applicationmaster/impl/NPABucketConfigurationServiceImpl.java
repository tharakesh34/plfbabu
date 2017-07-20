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
 * FileName    		:  NPABucketConfigurationServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.applicationmaster.NPABucketConfigurationDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.NPABucketConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>NPABucketConfiguration</b>.<br>
 */
public class NPABucketConfigurationServiceImpl extends GenericService<NPABucketConfiguration> implements
		NPABucketConfigurationService {
	private static final Logger			logger	= Logger.getLogger(NPABucketConfigurationServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private NPABucketConfigurationDAO	nPABucketConfigurationDAO;

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
	 * @return the nPABucketConfigurationDAO
	 */
	public NPABucketConfigurationDAO getNPABucketConfigurationDAO() {
		return nPABucketConfigurationDAO;
	}

	/**
	 * @param nPABucketConfigurationDAO
	 *            the nPABucketConfigurationDAO to set
	 */
	public void setNPABucketConfigurationDAO(NPABucketConfigurationDAO nPABucketConfigurationDAO) {
		this.nPABucketConfigurationDAO = nPABucketConfigurationDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * NPABUCKETSCONFIG/NPABUCKETSCONFIG_Temp by using NPABUCKETSCONFIGDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using NPABUCKETSCONFIGDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtNPABUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader)
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

		NPABucketConfiguration nPABucketConfiguration = (NPABucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (nPABucketConfiguration.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (nPABucketConfiguration.isNew()) {
			nPABucketConfiguration.setId(Long.parseLong(getNPABucketConfigurationDAO().save(nPABucketConfiguration,
					tableType)));
			auditHeader.getAuditDetail().setModelData(nPABucketConfiguration);
			auditHeader.setAuditReference(String.valueOf(nPABucketConfiguration.getProductCode()));
		} else {
			getNPABucketConfigurationDAO().update(nPABucketConfiguration, tableType);
		}
		
		if (TableType.MAIN_TAB.equals(tableType)) {
			FinanceConfigCache.clearNPABucketConfigurationCache(nPABucketConfiguration.getProductCode());
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * NPABUCKETSCONFIG by using NPABUCKETSCONFIGDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtNPABUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader)
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

		NPABucketConfiguration nPABucketConfiguration = (NPABucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();
		getNPABucketConfigurationDAO().delete(nPABucketConfiguration, TableType.MAIN_TAB);
		FinanceConfigCache.clearNPABucketConfigurationCache(nPABucketConfiguration.getProductCode());
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getNPABUCKETSCONFIG fetch the details by using NPABUCKETSCONFIGDAO's getNPABUCKETSCONFIGById method.
	 * 
	 * @param configID
	 *            configID of the NPABucketConfiguration.
	 * @return NPABUCKETSCONFIG
	 */
	@Override
	public NPABucketConfiguration getNPABucketConfiguration(long configID) {
		return getNPABucketConfigurationDAO().getNPABucketConfiguration(configID, "_View");
	}

	/**
	 * getApprovedNPABUCKETSCONFIGById fetch the details by using NPABUCKETSCONFIGDAO's getNPABUCKETSCONFIGById method .
	 * with parameter id and type as blank. it fetches the approved records from the NPABUCKETSCONFIG.
	 * 
	 * @param configID
	 *            configID of the NPABucketConfiguration. (String)
	 * @return NPABUCKETSCONFIG
	 */
	public NPABucketConfiguration getApprovedNPABucketConfiguration(long configID) {
		return getNPABucketConfigurationDAO().getNPABucketConfiguration(configID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getNPABucketConfigurationDAO().delete
	 * with parameters nPABucketConfiguration,"" b) NEW Add new record in to main table by using
	 * getNPABucketConfigurationDAO().save with parameters nPABucketConfiguration,"" c) EDIT Update record in the main
	 * table by using getNPABucketConfigurationDAO().update with parameters nPABucketConfiguration,"" 3) Delete the
	 * record from the workFlow table by using getNPABucketConfigurationDAO().delete with parameters
	 * nPABucketConfiguration,"_Temp" 4) Audit the record in to AuditHeader and AdtNPABUCKETSCONFIG by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtNPABUCKETSCONFIG
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

		NPABucketConfiguration nPABucketConfiguration = new NPABucketConfiguration();
		BeanUtils.copyProperties((NPABucketConfiguration) auditHeader.getAuditDetail().getModelData(),
				nPABucketConfiguration);

		getNPABucketConfigurationDAO().delete(nPABucketConfiguration, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(nPABucketConfiguration.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					nPABucketConfigurationDAO.getNPABucketConfiguration(nPABucketConfiguration.getConfigID(), ""));
		}

		if (nPABucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getNPABucketConfigurationDAO().delete(nPABucketConfiguration, TableType.MAIN_TAB);
	
		} else {
			nPABucketConfiguration.setRoleCode("");
			nPABucketConfiguration.setNextRoleCode("");
			nPABucketConfiguration.setTaskId("");
			nPABucketConfiguration.setNextTaskId("");
			nPABucketConfiguration.setWorkflowId(0);

			if (nPABucketConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				nPABucketConfiguration.setRecordType("");
				getNPABucketConfigurationDAO().save(nPABucketConfiguration, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				nPABucketConfiguration.setRecordType("");
				getNPABucketConfigurationDAO().update(nPABucketConfiguration, TableType.MAIN_TAB);
			}
		}
		
		FinanceConfigCache.clearNPABucketConfigurationCache(nPABucketConfiguration.getProductCode());
		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(nPABucketConfiguration);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getNPABucketConfigurationDAO().delete with parameters nPABucketConfiguration,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtNPABUCKETSCONFIG by using auditHeaderDAO.addAudit(auditHeader) for Work
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

		NPABucketConfiguration nPABucketConfiguration = (NPABucketConfiguration) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getNPABucketConfigurationDAO().delete(nPABucketConfiguration, TableType.TEMP_TAB);

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
	 * from getNPABucketConfigurationDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		NPABucketConfiguration nPABucketConfiguration = (NPABucketConfiguration) auditDetail.getModelData();

		// Check the unique keys.
		if (nPABucketConfiguration.isNew()
				&& nPABucketConfigurationDAO.isDuplicateKey(nPABucketConfiguration.getConfigID(),
						nPABucketConfiguration.getProductCode(), nPABucketConfiguration.getBucketID(),
						nPABucketConfiguration.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_ProductCode") + ": "
					+ nPABucketConfiguration.getProductCode();
			parameters[1] = PennantJavaUtil.getLabel("label_BucketID") + ": " + nPABucketConfiguration.getBucketID();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		if (!StringUtils.trimToEmpty(nPABucketConfiguration.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL) && StringUtils.trimToEmpty(nPABucketConfiguration.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
			int count = nPABucketConfigurationDAO.getByProductCode(nPABucketConfiguration.getProductCode(), nPABucketConfiguration.getDueDays(), "");

			if (count != 0) {
				String[] parameters = new String[2];
				String[] parametersDueDays = new String[2];
				
				parameters[0] = PennantJavaUtil.getLabel("label_ProductCode") + ": " + nPABucketConfiguration.getProductCode();
				parametersDueDays[0] = PennantJavaUtil.getLabel("label_DueDays") + ": " + nPABucketConfiguration.getDueDays();

				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41015", parameters, parametersDueDays), usrLanguage));
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}