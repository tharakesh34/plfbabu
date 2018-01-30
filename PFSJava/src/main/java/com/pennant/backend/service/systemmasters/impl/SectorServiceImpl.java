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
 * FileName    		:  SectorServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.systemmasters.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.SectorDAO;
import com.pennant.backend.dao.systemmasters.impl.SectorDAOImpl;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.SectorService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Sector</b>.<br>
 * 
 */
public class SectorServiceImpl extends GenericService<Sector> implements SectorService {

	private static Logger logger = Logger.getLogger(SectorDAOImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private SectorDAO sectorDAO;

	public SectorServiceImpl() {
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

	public SectorDAO getSectorDAO() {
		return sectorDAO;
	}

	public void setSectorDAO(SectorDAO sectorDAO) {
		this.sectorDAO = sectorDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTSectors/BMTSectors_Temp by using SectorDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using
	 * SectorDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtBMTSectors by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Sector sector = (Sector) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (sector.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		if (sector.isNew()) {
			sector.setSectorCode(getSectorDAO().save(sector, tableType));
			auditHeader.getAuditDetail().setModelData(sector);
			auditHeader.setAuditReference(sector.getSectorCode());
		} else {
			getSectorDAO().update(sector, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTSectors by using SectorDAO's delete method with type as Blank 3)
	 * Audit the record in to AuditHeader and AdtBMTSectors by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Sector sector = (Sector) auditHeader.getAuditDetail().getModelData();

		getSectorDAO().delete(sector, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getSectorById fetch the details by using SectorDAO's getSectorById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Sector
	 */
	@Override
	public Sector getSectorById(String id) {
		return getSectorDAO().getSectorById(id, "_View");
	}

	/**
	 * getApprovedSectorById fetch the details by using SectorDAO's
	 * getSectorById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTSectors.
	 * 
	 * @param id
	 *            (String)
	 * @return Sector
	 */
	public Sector getApprovedSectorById(String id) {
		return getSectorDAO().getSectorById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getSectorDAO().delete with parameters sector,"" b) NEW Add new
	 * record in to main table by using getSectorDAO().save with parameters
	 * sector,"" c) EDIT Update record in the main table by using
	 * getSectorDAO().update with parameters sector,"" 3) Delete the record from
	 * the workFlow table by using getSectorDAO().delete with parameters
	 * sector,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTSectors by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtBMTSectors by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Sector sector = new Sector();
		BeanUtils.copyProperties((Sector) auditHeader.getAuditDetail().getModelData(), sector);

		getSectorDAO().delete(sector, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(sector.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(sectorDAO.getSectorById(sector.getSectorCode(), ""));
		}
		if (sector.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getSectorDAO().delete(sector, TableType.MAIN_TAB);
		} else {
			sector.setRoleCode("");
			sector.setNextRoleCode("");
			sector.setTaskId("");
			sector.setNextTaskId("");
			sector.setWorkflowId(0);

			if (sector.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				sector.setRecordType("");
				getSectorDAO().save(sector, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				sector.setRecordType("");
				getSectorDAO().update(sector, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(sector);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getSectorDAO().delete with parameters
	 * sector,"_Temp" 3) Audit the record in to AuditHeader and AdtBMTSectors by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		Sector sector = (Sector) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		getSectorDAO().delete(sector, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
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
	private AuditHeader businessValidation(AuditHeader auditHeader) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getAcademicDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	 private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
			logger.debug("Entering");

			// Get the model object.
			Sector sector = (Sector) auditDetail.getModelData();
			String code = sector.getSectorCode();

			// Check the unique keys.
			if (sector.isNew()
					&& PennantConstants.RECORD_TYPE_NEW.equals(sector.getRecordType())
					&& sectorDAO.isDuplicateKey(code, sector.isWorkflow() ? TableType.BOTH_TAB
							: TableType.MAIN_TAB)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_Sector_Code") + ": " + code;

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
			
			auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

			logger.debug("Leaving");
			return auditDetail;
		}
}