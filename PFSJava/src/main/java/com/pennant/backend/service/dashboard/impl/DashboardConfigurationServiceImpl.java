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
 * FileName    		:  DashboardDetailServiceImpl.java                                      * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.dashboard.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;

import com.pennant.app.util.DynamicWhereConditionUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.dashboard.DashboardConfigurationDAO;
import com.pennant.backend.dao.dashboard.DetailStatisticsDAO;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.DashBoard;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dashboarddetail.DashboardPosition;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Service implementation for methods that depends on <b>DashboardDetail</b>.<br>
 * 
 */
public class DashboardConfigurationServiceImpl extends GenericService<DashboardConfiguration> implements
		DashboardConfigurationService {
	Logger logger= Logger.getLogger(DashboardConfigurationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DashboardConfigurationDAO dashboardConfigurationDAO;
	private DetailStatisticsDAO detailStatisticsDAO;


	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * DashboardDetails/DashboardDetails_Temp by using DashboardDetailDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using DashboardDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtDashboardDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		DashboardConfiguration dashboardConfiguration = (DashboardConfiguration) auditHeader.getAuditDetail().getModelData();

		if (dashboardConfiguration.isWorkflow()) {
			tableType = "_Temp";
		}if (dashboardConfiguration.isNew()) {
			dashboardConfiguration.setId(getDashboardConfigurationDAO().save(dashboardConfiguration,tableType));
			auditHeader.getAuditDetail().setModelData(dashboardConfiguration);
			auditHeader.setAuditReference(dashboardConfiguration.getId());
		} else {
			getDashboardConfigurationDAO().update(dashboardConfiguration,tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Entering");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table DashboardDetails by using DashboardDetailDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtDashboardDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		DashboardConfiguration dashboardConfiguration = (DashboardConfiguration) auditHeader.getAuditDetail().getModelData();
		getDashboardConfigurationDAO().delete(dashboardConfiguration, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDashboardDetailById fetch the details by using DashboardDetailDAO's
	 * getDashboardDetailById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return DashboardDetail
	 */

	@Override
	public DashboardConfiguration getDashboardDetailById(String id) {
		return getDashboardConfigurationDAO().getDashboardDetailByID(id, "_View");
	}

	/**
	 * getApprovedDashboardDetailById fetch the details by using
	 * DashboardDetailDAO's getDashboardDetailById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * DashboardDetails.
	 * 
	 * @param id
	 *            (String)
	 * @return DashboardDetail
	 */
	@Override
	public DashboardConfiguration getApprovedDashboardDetailById(String id) {
		return getDashboardConfigurationDAO().getDashboardDetailByID(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getDashboardConfigurationDAO().delete with parameters dashboardDetail,""
	 * b) NEW Add new record in to main table by using
	 * getDashboardConfigurationDAO().save with parameters dashboardDetail,"" c) EDIT
	 * Update record in the main table by using getDashboardConfigurationDAO().update
	 * with parameters dashboardDetail,"" 3) Delete the record from the workFlow
	 * table by using getDashboardConfigurationDAO().delete with parameters
	 * dashboardDetail,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDashboardDetails by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and AdtDashboardDetails
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove");
		String tranType = "";
		DashboardConfiguration dashboardConfiguration = new DashboardConfiguration();
		BeanUtils.copyProperties((DashboardConfiguration) auditHeader.getAuditDetail().getModelData(),
				dashboardConfiguration);

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		if (dashboardConfiguration.getRecordType().equals(
				PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getDashboardConfigurationDAO().delete(dashboardConfiguration, "");

		} else {
			dashboardConfiguration.setRoleCode("");
			dashboardConfiguration.setNextRoleCode("");
			dashboardConfiguration.setTaskId("");
			dashboardConfiguration.setNextTaskId("");
			dashboardConfiguration.setWorkflowId(0);

			if (dashboardConfiguration.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				dashboardConfiguration.setRecordType("");
				getDashboardConfigurationDAO().save(dashboardConfiguration, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				dashboardConfiguration.setRecordType("");
				getDashboardConfigurationDAO().update(dashboardConfiguration, "");
			}
		}

		getDashboardConfigurationDAO().delete(dashboardConfiguration, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(dashboardConfiguration);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getDashboardConfigurationDAO().delete with
	 * parameters dashboardDetail,"_Temp" 3) Audit the record in to AuditHeader
	 * and AdtDashboardDetails by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		DashboardConfiguration dashboardConfiguration = (DashboardConfiguration) auditHeader.getAuditDetail()
		.getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDashboardConfigurationDAO().delete(dashboardConfiguration, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getDashboardConfigurationDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
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

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,
			String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());

		DashboardConfiguration dashboardConfiguration = (DashboardConfiguration) auditDetail.getModelData();
		DashboardConfiguration tempDashboardConfiguration = null;

		if (dashboardConfiguration.isWorkflow()) {
			tempDashboardConfiguration = getDashboardConfigurationDAO()
			.getDashboardDetailByID(dashboardConfiguration.getId(),"_Temp");
		}

		DashboardConfiguration befDashboardConfiguration = getDashboardConfigurationDAO()
		.getDashboardDetailByID(dashboardConfiguration.getId(), "");
		DashboardConfiguration oldDashboardConfiguration = dashboardConfiguration.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = dashboardConfiguration.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_DashboardConfigurationDialog_DashboardCode.value") + ":"+ valueParm[0];

		if (dashboardConfiguration.isNew()) { // for New record or new record into work flow

			if (!dashboardConfiguration.isWorkflow()) {// With out Work flow only new records
				if (befDashboardConfiguration != null) { // Record Already Exists in the table
					// then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm, valueParm));
				}
			} else { // with work flow
				if (dashboardConfiguration.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befDashboardConfiguration != null || tempDashboardConfiguration != null) { //if records 
						// already exists
						// in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm));
					}
				} else { // if records not exists in the Main flow table
					if (befDashboardConfiguration == null || tempDashboardConfiguration != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!dashboardConfiguration.isWorkflow()) { // With out Work flow for update and
				// delete

				if (befDashboardConfiguration == null) { // if records not exists in the main
					// table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002",errParm, valueParm));
				} else {
					if (oldDashboardConfiguration != null && !oldDashboardConfiguration.getLastMntOn()
							.equals(befDashboardConfiguration.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003",errParm, valueParm));
						} else {
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004",errParm, valueParm));
						}
					}
				}
			} else {

				if (tempDashboardConfiguration == null) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, valueParm));
				}
				if (tempDashboardConfiguration != null && oldDashboardConfiguration != null && !oldDashboardConfiguration.getLastMntOn()
						.equals(tempDashboardConfiguration.getLastMntOn())) {

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005",errParm, valueParm));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !dashboardConfiguration.isWorkflow()) {
			auditDetail.setBefImage(befDashboardConfiguration);
		}
		logger.debug("Leaving");
		return auditDetail;
	}	

	/**
	 * Save the positions of the DashBoards in to the database
	 */
	@Override
	public void savePositions(List<DashboardPosition> dashboardPositionsList, long userId) {
		this.dashboardConfigurationDAO.delete(userId);

		for (int i = 0; i < dashboardPositionsList.size(); i++) {
			this.dashboardConfigurationDAO.SavePositions(dashboardPositionsList.get(i));
		}
	}


	/**
	 * Get the dashboard Details
	 */
	public Map<String, DashboardPosition> getDashboardPositionsByUser(long userId) {
		List<DashboardPosition> dashboardList= this.dashboardConfigurationDAO.getDashboardPositionsByUser(userId);
		Map<String, DashboardPosition> dashBoardMap=new HashMap<String, DashboardPosition>();
		for(DashboardPosition dashBoardPostion:dashboardList){
			dashBoardMap.put(dashBoardPostion.getDashboardRef(), dashBoardPostion);
		}

		return dashBoardMap;
	}

	public Map<String,DashboardConfiguration> getDashboardConfigurations(long userId) {
		List<DashboardConfiguration> dashConfigList=this.getDashboardConfigurationDAO().getDashboardConfigurations(userId); 
		Map<String,DashboardConfiguration> dashBrdConfigMap=new LinkedHashMap<String, DashboardConfiguration>();
		for (int i = 0; i < dashConfigList.size(); i++) {
			dashBrdConfigMap.put(dashConfigList.get(i).getDashboardCode(), dashConfigList.get(i));

		}

		return dashBrdConfigMap;
	}

	@Override
	public DashBoard getDashBoardData(long userId, String roles) {
		DashBoard dashBoard = new DashBoard();
		dashBoard.setDashBoardPosition(getDashboardPositionsByUser(userId));
		dashBoard.setDashboardConfigMap(getDashboardConfigurations(userId));

		return dashBoard;
	}

	/**
	 * 
	 */
	@Override
	public List<ChartSetElement> getLabelAndValues(DashboardConfiguration aDashboardConfiguration, String condition,
			LoggedInUser user, List<SecurityRole> roles) throws DataAccessException {
		/*Check this query contains any dynamic where condition parameters 
		 if contains replace where condition by calling DynamicWhereConditionUtil.getModifiedQuery()*/
		if (aDashboardConfiguration.isDrillDownChart()) {
			return getDrillDownLabelAndValues(aDashboardConfiguration, condition, user, roles);
		}
		aDashboardConfiguration.setQuery(DynamicWhereConditionUtil.getModifiedQuery(aDashboardConfiguration, user,
				roles));

		if(aDashboardConfiguration.isAdtDataSource()){
			return getDetailStatisticsDAO().getLabelAndValues(aDashboardConfiguration);
		}else{
			return getDashboardConfigurationDAO().getLabelAndValues(aDashboardConfiguration);

		}
	}
	/**
	 * This method returns the List<ChartSetElement> by processing all child tables data and set those  as child records
	 * @param aDashboardConfiguration
	 * @param condition
	 * @return
	 * @throws DataAccessException
	 */
	public List<ChartSetElement> getDrillDownLabelAndValues(DashboardConfiguration aDashboardConfiguration,
			String condition, LoggedInUser user, List<SecurityRole> roles) throws DataAccessException {
		/*Check this query contains any dynamic where condition parameters 
		 if contains replace where condition by calling DynamicWhereConditionUtil.getModifiedQuery()*/
		String[] querysList = null;

		DashboardConfiguration dashBoardConfiguration=new DashboardConfiguration();
		if(StringUtils.contains(aDashboardConfiguration.getQuery(), "||")){
			querysList=StringUtils.split(aDashboardConfiguration.getQuery(),"||");
		}
		List<List<ChartSetElement>> rootSetElements=new ArrayList<List<ChartSetElement>>();
		
		if(querysList!=null){
			for(int i=0;i<querysList.length;i++){
				logger.debug(querysList[i]);
				dashBoardConfiguration.setQuery(querysList[i]);
				dashBoardConfiguration.setQuery(DynamicWhereConditionUtil.getModifiedQuery(dashBoardConfiguration, user,
						roles));

				if(aDashboardConfiguration.isAdtDataSource()){
					rootSetElements.add(getDetailStatisticsDAO().getLabelAndValues(dashBoardConfiguration));
				}else{
					rootSetElements.add(getDashboardConfigurationDAO().getLabelAndValues(dashBoardConfiguration));
				}
			}
		}
		
		
		try{
			//Setting Parent child relation
			for(int i=0;i<rootSetElements.size();i++){
				if(i!=rootSetElements.size()-1){
					setChildernElemetsToParentElements(rootSetElements.get(i),rootSetElements.get(i+1));
				}
			}
		} catch (Exception e) {
			logger.error("Exception: While mapping child record to parent record.", e);
		}
		return rootSetElements.get(0);
	}

	/**
	 * This method do the following
	 * a) Prepares a map that keys are parent list References and values are List<ChartSetElement>
	 * b) From prepared map it get the list of ChartSetElement for each parent reference and set it to ChartSetElement  objects
	 *    of parent list
	 * c)Simply this method prepares a map<String,List<ChartSetElement>>  from childList and sets InnerChrtSetElementsList()to
	 *    all elements of parent List by using key  
	 * @param rootElementsList
	 * @param childElementsList
	 * @return
	 */
	private List<ChartSetElement> setChildernElemetsToParentElements(List<ChartSetElement> rootElementsList
			,List<ChartSetElement> childElementsList){

		logger.debug("Entering");
		Comparator<Object> comp = new BeanComparator<Object>("reference");
		Collections.sort(childElementsList,comp);
		List<ChartSetElement> aSetElementsList=new ArrayList<ChartSetElement>();

		Map<String,List<ChartSetElement>> refBySetElementsMap=new HashMap<String,List<ChartSetElement>>();
		int size=childElementsList.size();
		for (int i = 0; i < size; i++) {
			//if i is not last element
			if(i!=size-1){
				if(childElementsList.get(i).getReference()
						.equals(childElementsList.get(i+1).getReference())){

					if(refBySetElementsMap.containsKey(childElementsList.get(i).getReference())){
						aSetElementsList.add(childElementsList.get(i));
					}else{
						aSetElementsList=new ArrayList<ChartSetElement>();
						aSetElementsList.add(childElementsList.get(i));
					}
					refBySetElementsMap.put(childElementsList.get(i).getReference(), aSetElementsList);
				}else{
					if(refBySetElementsMap.containsKey(childElementsList.get(i).getReference())){
						List<ChartSetElement> tempList=(List<ChartSetElement>) refBySetElementsMap
						.get(childElementsList.get(i).getReference());
						tempList.add(childElementsList.get(i));
						refBySetElementsMap.put(childElementsList.get(i).getReference(), tempList);	
					}else{
						aSetElementsList=new ArrayList<ChartSetElement>();
						aSetElementsList.add(childElementsList.get(i));
						refBySetElementsMap.put(childElementsList.get(i).getReference(), aSetElementsList);
					}
				}   
			}else{//if i is last element
				if(refBySetElementsMap.containsKey(childElementsList.get(i).getReference())){
					List<ChartSetElement> tempList=(List<ChartSetElement>) refBySetElementsMap
					.get(childElementsList.get(i).getReference());
					tempList.add(childElementsList.get(i));
					refBySetElementsMap.put(childElementsList.get(i).getReference(), tempList);	

				}else{
					aSetElementsList=new ArrayList<ChartSetElement>();
					aSetElementsList .add(childElementsList.get(i));
					refBySetElementsMap.put(childElementsList.get(i).getReference(), aSetElementsList);
				}
			}
		}
		//Set children list to  parent details list
		for(int i=0;i<rootElementsList.size();i++){
			if(refBySetElementsMap.containsKey(rootElementsList.get(i).getLabel())){
				rootElementsList.get(i).setLink("newchart-xml-childrenLink-"+i);
				rootElementsList.get(i).setInnerChrtSetElementsList(refBySetElementsMap.get(rootElementsList.get(i).getLabel()));	
			}	
		}
		logger.debug("Leaving");
		return rootElementsList;
	}

	//Getters and Setters 
	public DashboardConfigurationDAO getDashboardConfigurationDAO() {
		return dashboardConfigurationDAO;
	}

	public void setDashboardConfigurationDAO(
			DashboardConfigurationDAO dashboardConfigurationDAO) {
		this.dashboardConfigurationDAO = dashboardConfigurationDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}


	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Override
	public DashboardConfiguration getNewDashboardDetail() {
		return getDashboardConfigurationDAO().getNewDashboardDetail();
	}
	public void setDetailStatisticsDAO(DetailStatisticsDAO detailStatisticsDAO) {
		this.detailStatisticsDAO = detailStatisticsDAO;
	}

	public DetailStatisticsDAO getDetailStatisticsDAO() {
		return detailStatisticsDAO;
	}
}