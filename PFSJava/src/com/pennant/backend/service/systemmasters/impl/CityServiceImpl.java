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
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.CityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>City</b>.<br>
 * 
 */
public class CityServiceImpl extends GenericService<City> implements CityService {
	
	private static Logger logger = Logger.getLogger(CityServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private CityDAO cityDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	
	public City getCity() {
		return getCityDAO().getCity();
	}
	
	public City getNewCity() {
		return getCityDAO().getNewCity();
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
		
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		City city = (City) auditHeader.getAuditDetail().getModelData();
		
		if (city.isWorkflow()) {
			tableType="_TEMP";
		}

		if (city.isNew()) {
			getCityDAO().save(city,tableType);
			auditHeader.getAuditDetail().setModelData(city);
			auditHeader.setAuditReference(String.valueOf(city.getPCCountry()) +PennantConstants.KEY_SEPERATOR
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

		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		City city = (City) auditHeader.getAuditDetail().getModelData();
		getCityDAO().delete(city,"");
		
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
	 * This method refresh the Record.
	 * @param City (city)
 	 * @return city
	 */
	@Override
	public City refresh(City city) {
		logger.debug("Entering ");
		getCityDAO().refresh(city);
		getCityDAO().initialize(city);
		logger.debug("Leaving ");
		return city;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCityDAO().delete with parameters city,"" b) NEW Add new record
	 * in to main table by using getCityDAO().save with parameters city,"" c)
	 * EDIT Update record in the main table by using getCityDAO().update with
	 * parameters city,"" 3) Delete the record from the workFlow table by using
	 * getCityDAO().delete with parameters city,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtRMTProvinceVsCity by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTProvinceVsCity by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering ");
		
		String tranType="";		
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		City city = new City();
		BeanUtils.copyProperties((City) auditHeader.getAuditDetail().getModelData(), city);

		if (city.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
				tranType=PennantConstants.TRAN_DEL;

				getCityDAO().delete(city,"");
				
			} else {
				city.setRoleCode("");
				city.setNextRoleCode("");
				city.setTaskId("");
				city.setNextTaskId("");
				city.setWorkflowId(0);
				
				if (city.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) 
				{	
					tranType=PennantConstants.TRAN_ADD;
					city.setRecordType("");
					getCityDAO().save(city,"");
				} else {
					tranType=PennantConstants.TRAN_UPD;
					city.setRecordType("");
					getCityDAO().update(city,"");
				}
			}
			
			getCityDAO().delete(city,"_TEMP");
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
		
		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		City city= (City) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCityDAO().delete(city,"_TEMP");
		
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
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),
				auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getCityDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering ");
		City city = (City) auditDetail.getModelData();
		City tempCity = null;
		if (city.isWorkflow()) {
			tempCity = getCityDAO().getCityById(city.getPCCountry(),
					city.getPCProvince(), city.getPCCity(), "_Temp");
		}
		City befCity = getCityDAO().getCityById(city.getPCCountry(),
				city.getPCProvince(), city.getPCCity(), "");

		City oldCity = city.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm= new String[3];

		valueParm[0] = city.getPCCountry();
		valueParm[1] = city.getPCProvince();
		valueParm[2] = city.getPCCity();
		
		errParm[0]=PennantJavaUtil.getLabel("label_PCCountry")+":"+valueParm[0];
		errParm[1]=PennantJavaUtil.getLabel("label_PCProvince")+":"+valueParm[1];
		errParm[2]=PennantJavaUtil.getLabel("label_PCCity")+":"+valueParm[2];

		if (city.isNew()) { // for New record or new record into work flow

			if (!city.isWorkflow()) {// With out Work flow only new records
				if (befCity != null) { // Record Already Exists in the table 
										// then error
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41008",errParm,null));
				}
			} else { // with work flow
				if (city.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befCity != null || tempCity != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41008",errParm,null));
					}
				}
				 else { // if records not exists in the Main flow table
						if (befCity == null || tempCity != null) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
						}
					}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!city.isWorkflow()) { // With out Work flow for update and delete

				if (befCity == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldCity != null
							&& !oldCity.getLastMntOn().equals(
									befCity.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41003",errParm,null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			} else {

				if (tempCity == null) { // if records not exists in the WorkFlow table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempCity != null && oldCity != null
						&& !oldCity.getLastMntOn().equals(
								tempCity.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !city.isWorkflow()) {
			auditDetail.setBefImage(befCity);
		}
		logger.debug("Leaving ");
		return auditDetail;
	}

}