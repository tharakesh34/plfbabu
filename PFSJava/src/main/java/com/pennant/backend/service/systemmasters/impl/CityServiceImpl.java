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
 * FileName    		:  CityServiceImpl.java                                                   * 	  
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
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>City</b>.<br>
 * 
 */
public class CityServiceImpl extends GenericService<City> implements CityService {
	private static Logger logger = Logger.getLogger(CityServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private CityDAO cityDAO;
	private PinCodeDAO pinCodeDAO;

	public CityServiceImpl() {
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
	
	public CityDAO getCityDAO() {
		return cityDAO;
	}
	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}
	
	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTProvinceVsCity/RMTProvinceVsCity_Temp by using CityDAO's save method
	 * b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CityDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtRMTProvinceVsCity by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		
		City city = (City) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (city.isWorkflow()) {
			tableType=TableType.TEMP_TAB;
		}

		if (city.isNew()) {
			getCityDAO().save(city,tableType);
			auditHeader.getAuditDetail().setModelData(city);
			auditHeader.setAuditReference(city.getPCCountry() +PennantConstants.KEY_SEPERATOR
					+ city.getPCProvince()+PennantConstants.KEY_SEPERATOR+ city.getPCCity());

		}else{
			getCityDAO().update(city,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTProvinceVsCity by using CityDAO's delete method with type as
	 * Blank 3) Audit the record in to AuditHeader and AdtRMTProvinceVsCity by
	 * using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering ");

		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		City city = (City) auditHeader.getAuditDetail().getModelData();
		getCityDAO().delete(city, TableType.MAIN_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * getCityById fetch the details by using CityDAO's getCityById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return City
	 */
	@Override
	public City getCityById(String pCCountry, String pCProvince, String pCCity) {
		return getCityDAO().getCityById(pCCountry, pCProvince, pCCity, "_View");
	}
	
	/**
	 * getApprovedCityById fetch the details by using CityDAO's getCityById
	 * method . with parameter id and type as blank. it fetches the approved
	 * records from the RMTProvinceVsCity.
	 * 
	 * @param id
	 *            (String)
	 * @return City
	 */
	public City getApprovedCityById(String pCCountry, String pCProvince,
			String pCCity) {
		return getCityDAO().getCityById(pCCountry, pCProvince, pCCity, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCityDAO().delete with parameters
	 * city,"" b) NEW Add new record in to main table by using getCityDAO().save with parameters city,"" c) EDIT Update
	 * record in the main table by using getCityDAO().update with parameters city,"" 3) Delete the record from the
	 * workFlow table by using getCityDAO().delete with parameters city,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtRMTProvinceVsCity by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtRMTProvinceVsCity by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");

		String tranType = "";
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		City city = new City();
		BeanUtils.copyProperties((City) auditHeader.getAuditDetail().getModelData(), city);
		
		getCityDAO().delete(city, TableType.TEMP_TAB);
		if (!PennantConstants.RECORD_TYPE_NEW.equals(city.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(cityDAO.getCityById(city.getPCCountry(),
					city.getPCProvince(), city.getPCCity(), ""));
		}
		
		if (city.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getCityDAO().delete(city, TableType.MAIN_TAB);

		} else {
			city.setRoleCode("");
			city.setNextRoleCode("");
			city.setTaskId("");
			city.setNextTaskId("");
			city.setWorkflowId(0);

			if (city.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				city.setRecordType("");
				getCityDAO().save(city, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				city.setRecordType("");
				getCityDAO().update(city, TableType.MAIN_TAB);
			}
		}

	
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(city);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCityDAO().delete with parameters
	 * city,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTProvinceVsCity by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		auditHeader = businessValidation(auditHeader);
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		City city= (City) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCityDAO().delete(city, TableType.TEMP_TAB);
		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving ");
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
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAcademicDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign
	 * the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		City city = (City) auditDetail.getModelData();

		// Check the unique keys.
		if (city.isNew()
				&& PennantConstants.RECORD_TYPE_NEW.equals(city.getRecordType())
				&& cityDAO.isDuplicateKey(city.getPCCountry(), city.getPCProvince(), city.getPCCity(),
						city.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[3];

			parameters[0]=PennantJavaUtil.getLabel("label_PCCountry")+":"+city.getPCCountry();
			parameters[1]=PennantJavaUtil.getLabel("label_PCProvince")+":"+city.getPCProvince();
			parameters[2]=PennantJavaUtil.getLabel("label_PCCity")+":"+city.getPCCity();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41008", parameters, null));
		}
		
		// If City Code is already utilized in PinCode 
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, city.getRecordType())) {
			boolean workflowExists = getPinCodeDAO().isCityCodeExists(city.getPCCity());
			if (workflowExists) {

				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_PCCity") + ": " + city.getPCCity();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public PinCodeDAO getPinCodeDAO() {
		return pinCodeDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}
	
}