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
 * FileName    		:  CommodityInventoryServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2015    														*
 *                                                                  						*
 * Modified Date    :  23-04-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2015       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.commodity.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.commodity.CommodityInventoryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.model.commodity.FinCommodityInventory;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.commodity.CommodityInventoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.exception.PFFInterfaceException;

/**
 * Service implementation for methods that depends on <b>CommodityInventory</b>.<br>
 * 
 */
public class CommodityInventoryServiceImpl extends GenericService<CommodityInventory> implements CommodityInventoryService {
	private final static Logger logger = Logger.getLogger(CommodityInventoryServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private CommodityInventoryDAO commodityInventoryDAO;
	
	private PostingsPreparationUtil postingsPreparationUtil;

	public CommodityInventoryServiceImpl() {
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
	 * @return the commodityInventoryDAO
	 */
	public CommodityInventoryDAO getCommodityInventoryDAO() {
		return commodityInventoryDAO;
	}
	/**
	 * @param commodityInventoryDAO the commodityInventoryDAO to set
	 */
	public void setCommodityInventoryDAO(CommodityInventoryDAO commodityInventoryDAO) {
		this.commodityInventoryDAO = commodityInventoryDAO;
	}



	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FCMTCommodityInventory/FCMTCommodityInventory_Temp 
	 * 			by using CommodityInventoryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommodityInventoryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FCMTCommodityInventory/FCMTCommodityInventory_Temp 
	 * 			by using CommodityInventoryDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CommodityInventoryDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */


	private AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean online) {
		logger.debug("Entering");	
		auditHeader = businessValidation(auditHeader,"saveOrUpdate",online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		CommodityInventory commodityInventory = (CommodityInventory) auditHeader.getAuditDetail().getModelData();

		if (commodityInventory.isWorkflow()) {
			tableType="_Temp";
		}

		if (commodityInventory.isNew()) {
			commodityInventory.setId(getCommodityInventoryDAO().save(commodityInventory,tableType));
			auditHeader.getAuditDetail().setModelData(commodityInventory);
			auditHeader.setAuditReference(String.valueOf(commodityInventory.getCommodityInvId()));
		}else{
			getCommodityInventoryDAO().update(commodityInventory,tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table FCMTCommodityInventory by using CommodityInventoryDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete",false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CommodityInventory commodityInventory = (CommodityInventory) auditHeader.getAuditDetail().getModelData();
		getCommodityInventoryDAO().delete(commodityInventory,"");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCommodityInventoryById fetch the details by using CommodityInventoryDAO's getCommodityInventoryById method.
	 * @param id (int)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CommodityInventory
	 */

	@Override
	public CommodityInventory getCommodityInventoryById(long id) {
		return getCommodityInventoryDAO().getCommodityInventoryById(id,"_View");
	}
	/**
	 * getApprovedCommodityInventoryById fetch the details by using CommodityInventoryDAO's getCommodityInventoryById method .
	 * with parameter id and type as blank. it fetches the approved records from the FCMTCommodityInventory.
	 * @param id (int)
	 * @return CommodityInventory
	 */

	public CommodityInventory getApprovedCommodityInventoryById(long id) {
		return getCommodityInventoryDAO().getCommodityInventoryById(id,"_AView");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using getCommodityInventoryDAO().delete with parameters commodityInventory,""
	 * 		b)  NEW		Add new record in to main table by using getCommodityInventoryDAO().save with parameters commodityInventory,""
	 * 		c)  EDIT	Update record in the main table by using getCommodityInventoryDAO().update with parameters commodityInventory,""
	 * 3)	Delete the record from the workFlow table by using getCommodityInventoryDAO().delete with parameters commodityInventory,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		
		CommodityInventory commodityInventory = new CommodityInventory();
		BeanUtils.copyProperties((CommodityInventory) auditHeader.getAuditDetail().getModelData(), commodityInventory);

		if (commodityInventory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;

			List<Object> list = processPostings(commodityInventory,tranType);
			if (list==null ||(!(Boolean)list.get(0))) {
				String errorMessage = StringUtils.trimToEmpty(list.get(1).toString());

				auditHeader.setErrorDetails(new ErrorDetails("0000",errorMessage, null));
				return auditHeader;
	        }
			
			
			getCommodityInventoryDAO().delete(commodityInventory,"");

		} else {
			
			//Posting Process For Commodity inventory
			List<Object> list = processPostings(commodityInventory,tranType);
			if (list==null ||(!(Boolean)list.get(0))) {
				String errorMessage = StringUtils.trimToEmpty(list.get(1).toString());

				auditHeader.setErrorDetails(new ErrorDetails("0000",errorMessage, null));
				return auditHeader;
	        }
			
			commodityInventory.setRoleCode("");
			commodityInventory.setNextRoleCode("");
			commodityInventory.setTaskId("");
			commodityInventory.setNextTaskId("");
			commodityInventory.setWorkflowId(0);

			if (commodityInventory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				commodityInventory.setRecordType("");
				getCommodityInventoryDAO().save(commodityInventory,"");
			
			} else {
				tranType=PennantConstants.TRAN_UPD;
				commodityInventory.setRecordType("");
				getCommodityInventoryDAO().update(commodityInventory,"");
			}
		}
		
		getCommodityInventoryDAO().delete(commodityInventory,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(commodityInventory);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using getCommodityInventoryDAO().delete with parameters commodityInventory,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtFCMTCommodityInventory by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */

	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"doApprove",false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		CommodityInventory commodityInventory = (CommodityInventory) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCommodityInventoryDAO().delete(commodityInventory,"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * Method for getting commodity used finances count
	 * 
	 * @param brokerCode
	 * @param holdCertificateNo
	 * @param status
	 * 
	 */
    public int getCommodityFinances(String brokerCode, String holdCertificateNo, String status) {
	    return getCommodityInventoryDAO().getCommodityFinances(brokerCode, holdCertificateNo, status);
    }

	/**
	 * businessValidation method do the following steps.
	 * 1)	validate the audit detail 
	 * 2)	if any error/Warnings  then assign the to auditHeader
	 * 3)   identify the nextprocess
	 *  
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method,boolean onlineRequest){
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 5)	for any mismatch conditions Fetch the error details from getCommodityInventoryDAO().getErrorDetail with Error ID and language as parameters.
	 * 6)	if any error/Warnings  then assign the to auditHeader 
	 * @param AuditHeader (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail,String usrLanguage,String method,boolean onlineRequest){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		CommodityInventory commodityInventory= (CommodityInventory) auditDetail.getModelData();

		CommodityInventory tempCommodityInventory= null;
		if (commodityInventory.isWorkflow()){
			tempCommodityInventory = getCommodityInventoryDAO().getCommodityInventoryById(commodityInventory.getId(), "_Temp");
		}
		CommodityInventory befCommodityInventory= getCommodityInventoryDAO().getCommodityInventoryById(commodityInventory.getId(), "");

		CommodityInventory oldCommodityInventory= commodityInventory.getBefImage();


		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(commodityInventory.getId());
		errParm[0]=PennantJavaUtil.getLabel("label_CommodityInvId")+":"+valueParm[0];

		if (commodityInventory.isNew()){ // for New record or new record into work flow

			if (!commodityInventory.isWorkflow()){// With out Work flow only new records  
				if (befCommodityInventory !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (commodityInventory.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befCommodityInventory !=null || tempCommodityInventory!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befCommodityInventory ==null || tempCommodityInventory!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!commodityInventory.isWorkflow()){	// With out Work flow for update and delete

				if (befCommodityInventory ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldCommodityInventory!=null && !oldCommodityInventory.getLastMntOn().equals(befCommodityInventory.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempCommodityInventory==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (oldCommodityInventory!=null && !oldCommodityInventory.getLastMntOn().equals(tempCommodityInventory.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}
		
		if (commodityInventory.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			// validate: is the current commodity is allocated or sold for any finance
			List<FinCommodityInventory> finCommInventoryList = getCommodityInventoryDAO().getUsedCommInventory(
					commodityInventory.getBrokerCode(), commodityInventory.getHoldCertificateNo());
			if(!finCommInventoryList.isEmpty()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
			}
		}
		
		//Broker Code and Holder Certificate Number should be Unique
		if(getCommodityInventoryDAO().getComInvCountByBrokerAndHoldCertNo(commodityInventory, "_View") > 0 ){
			String[] errParm1= new String[2];
			String[] valueParm1= new String[2];
			
			valueParm1[0] = commodityInventory.getBrokerCode(); 
			valueParm1[1] = commodityInventory.getHoldCertificateNo(); 
			errParm1[0] = PennantJavaUtil.getLabel("label_BrokerCode")+":"+valueParm1[0]; 
			errParm1[1] = PennantJavaUtil.getLabel("label_HoldCertificateNo")+":"+valueParm1[1]; 
			
			errParm[0]=PennantJavaUtil.getLabel("label_CommodityInvId")+":"+valueParm[0];
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm1,valueParm1), usrLanguage));
		}
		

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !commodityInventory.isWorkflow()){
			auditDetail.setBefImage(befCommodityInventory);	
		}

		return auditDetail;
	}
	

	@Override
    public CommodityInventory getCommodityDetails(String holdCertificateNo, String brokerCode) {
	 
	    return getCommodityInventoryDAO().getCommodityDetails(holdCertificateNo, brokerCode);
    }

	
	
	public List<Object> processPostings(CommodityInventory commodityInventory, String tranType) {
		logger.debug(" Entering ");
		//get Accounting set and prepared return data set
		List<Object> returnList =null;
		try {
			CommodityInventory prvCommodityInventory = getCommodityDetails(commodityInventory.getHoldCertificateNo(), commodityInventory.getBrokerCode());
			
			List<ReturnDataSet> returnDataSetList =null;
			
			if (tranType.equals(PennantConstants.TRAN_DEL)) {
				
				returnDataSetList=getPostingsPreparationUtil().prepareAccountingDataSet(commodityInventory,AccountEventConstants.ACCEVENT_CMTINV_DEL, "Y");
	            
            }else{
            	
        		if (prvCommodityInventory!=null) {
    				returnDataSetList=getPostingsPreparationUtil().prepareAccountingDataSet(commodityInventory,AccountEventConstants.ACCEVENT_CMTINV_MAT, "Y");
                }else{
                	returnDataSetList=getPostingsPreparationUtil().prepareAccountingDataSet(commodityInventory,AccountEventConstants.ACCEVENT_CMTINV_NEW, "Y");	
                }
            }
			
			if (returnDataSetList!=null) {
				return getPostingsPreparationUtil().processPostings(returnDataSetList);
            }else{
        		returnList =new ArrayList<Object>();
    			returnList.add(false);
    			returnList.add("Accounting not defined");
    			return returnList;
            }

		} catch (PFFInterfaceException e) {
			logger.debug(e);
			returnList =new ArrayList<Object>();
			returnList.add(false);
			returnList.add(e.getErrorMessage());
		}  catch (IllegalAccessException e) {
			logger.debug(e);
		} catch (InvocationTargetException e) {
			logger.debug(e);
		} catch (AccountNotFoundException e) {
			logger.debug(e);
        } 
		logger.debug(" Leaving ");
		return returnList;

	}
	

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

}