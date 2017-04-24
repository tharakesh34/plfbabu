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
 * FileName    		:  ProvinceServiceImpl.java                                                   * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Province</b>.<br>
 * 
 */
public class ProvinceServiceImpl extends GenericService<Province> implements ProvinceService {

	private final static Logger logger = Logger.getLogger(ProvinceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProvinceDAO provinceDAO;

	public ProvinceServiceImpl() {
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

	public ProvinceDAO getProvinceDAO() {
		return provinceDAO;
	}
	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTCountryVsProvince/RMTCountryVsProvince_Temp by using ProvinceDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using ProvinceDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTCountryVsProvince by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		Province province = (Province) auditHeader.getAuditDetail()
				.getModelData();

		if (province.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (province.isNew()) {
			getProvinceDAO().save(province, tableType);
			auditHeader.getAuditDetail().setModelData(province);
			auditHeader.setAuditReference(province.getCPCountry() 
					+PennantConstants.KEY_SEPERATOR+ province.getCPProvince());
		} else {
			getProvinceDAO().update(province, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTCountryVsProvince by using ProvinceDAO's delete method with type
	 * as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTCountryVsProvince by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = (Province) auditHeader.getAuditDetail()
				.getModelData();
		getProvinceDAO().delete(province, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProvinceById fetch the details by using ProvinceDAO's getProvinceById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Province
	 */
	@Override
	public Province getProvinceById(String cPCountry, String cPProvince) {
		return getProvinceDAO().getProvinceById(cPCountry, cPProvince, "_View");
	}

	/**
	 * getApprovedProvinceById fetch the details by using ProvinceDAO's
	 * getProvinceById method . with parameter id and type as blank. it fetches
	 * the approved records from the RMTCountryVsProvince.
	 * 
	 * @param id
	 *            (String)
	 * @return Province
	 */
	public Province getApprovedProvinceById(String cPCountry, String cPProvince) {
		return getProvinceDAO().getProvinceById(cPCountry, cPProvince, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getProvinceDAO().delete with parameters province,"" b) NEW Add new
	 * record in to main table by using getProvinceDAO().save with parameters
	 * province,"" c) EDIT Update record in the main table by using
	 * getProvinceDAO().update with parameters province,"" 3) Delete the record
	 * from the workFlow table by using getProvinceDAO().delete with parameters
	 * province,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtRMTCountryVsProvince by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtRMTCountryVsProvince by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = new Province();
		BeanUtils.copyProperties((Province) auditHeader.getAuditDetail()
				.getModelData(), province);
		
		getProvinceDAO().delete(province, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(province.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					provinceDAO.getProvinceById(
							province.getCPCountry(), province.getCPProvince(), ""));
		}
		

		if (province.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProvinceDAO().delete(province, TableType.MAIN_TAB);
		} else {
			province.setRoleCode("");
			province.setNextRoleCode("");
			province.setTaskId("");
			province.setNextTaskId("");
			province.setWorkflowId(0);

			if (province.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				province.setRecordType("");
				getProvinceDAO().save(province, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				province.setRecordType("");
				getProvinceDAO().update(province, TableType.MAIN_TAB);
			}
		}

		
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(province);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getProvinceDAO().delete with parameters
	 * province,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTCountryVsProvince by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = (Province) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProvinceDAO().delete(province, TableType.TEMP_TAB);

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
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getProvinceDAO().getErrorDetail with Error ID and language as parameters.
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
		Province province = (Province) auditDetail.getModelData();
		// Check the unique keys.
		if (province.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(province.getRecordType())
				&& provinceDAO.isDuplicateKey(province.getCPCountry(), province.getCPProvince(),
						province.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_CPCountry") + ":"+ province.getCPCountry();
			parameters[1] = PennantJavaUtil.getLabel("label_CPProvince") + ":"+ province.getCPProvince();
			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
	
		if (province.isSystemDefault()) {
			String dftCPProvince = getProvinceDAO().getSystemDefaultCount(province.getCPProvince());
			if (StringUtils.isNotEmpty(dftCPProvince)) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60501",
				        new String[]{dftCPProvince,PennantJavaUtil.getLabel("Province")}, null));
			}
        }
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}
	
	/*private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		Province province = (Province) auditDetail.getModelData();
		Province tempProvince = null;
		if (province.isWorkflow()) {
			tempProvince = getProvinceDAO().getProvinceById(
					province.getCPCountry(), province.getCPProvince(), "_Temp");
		}
		Province befProvince = getProvinceDAO().getProvinceById(
				province.getCPCountry(), province.getCPProvince(), "");

		Province oldProvince = province.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm= new String[2];

		valueParm[0] = province.getCPCountry();
		valueParm[1] = province.getCPProvince();

		errParm[0] = PennantJavaUtil.getLabel("label_CPCountry") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CPProvince") + ":"+valueParm[1];

		if (province.isNew()) { // for New record or new record into work flow

			if (!province.isWorkflow()) {// With out Work flow only new records
				if (befProvince != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}
			} else { // with work flow
	
				if (province.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befProvince != null || tempProvince != null) { // if records already exists in
											  // the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				} else { // if records not exists in the Main flow table
					if (befProvince == null || tempProvince != null) {
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		} else {
				// for work flow process records or (Record to update or Delete with
				// out work flow)
			if (!province.isWorkflow()) { // With out Work flow for update and delete

				if (befProvince == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{
	
					if (oldProvince != null
							&& !oldProvince.getLastMntOn().equals(
									befProvince.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempProvince == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempProvince != null && oldProvince != null
						&& !oldProvince.getLastMntOn().equals(
								tempProvince.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}
		
		if (province.isSystemDefault()) {
			String dftCPProvince = getProvinceDAO().getSystemDefaultCount(province.getCPProvince());
			if (StringUtils.isNotEmpty(dftCPProvince)) {
				auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60501",
				        new String[]{dftCPProvince,PennantJavaUtil.getLabel("Province")}, null));
			}
        }
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))
				|| !province.isWorkflow()) {
			auditDetail.setBefImage(befProvince);
		}
		logger.debug("Leaving");
		return auditDetail;
	}*/

}