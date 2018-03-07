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
 * FileName    		:  InterfaceMappingServiceImpl.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-11-2017    														*
 *                                                                  						*
 * Modified Date    :    																	*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-11-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *        																					*
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.interfacemapping.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.interfacemapping.InterfaceMappingDAO;
import com.pennant.backend.dao.interfacemapping.MasterMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.model.interfacemapping.MasterMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.interfacemapping.InterfaceMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.bajaj.process.collections.model.CollectionConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>InterfaceMapping</b>.<br>
 * 
 */
public class InterfaceMappingServiceImpl extends GenericService<InterfaceMapping> implements InterfaceMappingService {
	private static final Logger	logger	= Logger.getLogger(InterfaceMappingServiceImpl.class);

	private InterfaceMappingDAO	interfaceMappingDAO;
	private AuditHeaderDAO		auditHeaderDAO;
	private MasterMappingDAO    masterMappingDao;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * InterfaceMapping/InterfaceMapping_Temp by using InterfaceMappingDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using InterfaceMappingDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtInterfaceMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		
		String tableType = "";
		InterfaceMapping interfaceMapping = (InterfaceMapping) auditHeader.getAuditDetail().getModelData();

		if (interfaceMapping.isWorkflow()) {
			tableType = "_Temp";
		}

		if (interfaceMapping.isNew()) {
			interfaceMapping.setId(getInterfaceMappingDAO().save(interfaceMapping, tableType));
			auditHeader.getAuditDetail().setModelData(interfaceMapping);
			auditHeader.setAuditReference(String.valueOf(interfaceMapping.getInterfaceMappingId()));
		} else {
			getInterfaceMappingDAO().update(interfaceMapping, tableType);
		}
		
		//master mapping
		if (interfaceMapping.getMasterMappingList() != null && interfaceMapping.getMasterMappingList().size() > 0) {
			List<AuditDetail> details = interfaceMapping.getLovDescAuditDetailMap().get("MasterMapping");
			details = processingMasterMappingList(details, tableType, interfaceMapping.getId());
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * getInterfaceMappingById fetch the details by using InterfaceMappingDAO's getInterfaceMappingById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InterfaceMapping
	 */
	@Override
	public InterfaceMapping getInterfaceMappingById(long id) {
		InterfaceMapping interfaceMapping = getInterfaceMappingDAO().getInterfaceMappingById(id, "_View");
		
		if (interfaceMapping != null && StringUtils.equalsIgnoreCase(interfaceMapping.getMappingType(), CollectionConstants.INTERFACEMAPPING_MASTER)) {
			interfaceMapping.setMasterMappingList(this.masterMappingDao.getMasterMappingDetails(interfaceMapping.getId(), "_View"));
		}
		
		return interfaceMapping;
	}


	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * InterfaceMapping by using InterfaceMappingDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtInterfaceMapping by using auditHeaderDAO.addAudit(auditHeader)
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

		InterfaceMapping interfaceMapping = (InterfaceMapping) auditHeader.getAuditDetail().getModelData();
		getInterfaceMappingDAO().delete(interfaceMapping, "");
		
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(interfaceMapping, "", auditHeader.getAuditTranType())));
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getInterfaceMappingDAO().delete with
	 * parameters interfaceMapping,"" b) NEW Add new record in to main table by using getInterfaceMappingDAO().save with
	 * parameters interfaceMapping,"" c) EDIT Update record in the main table by using getInterfaceMappingDAO().update
	 * with parameters interfaceMapping,"" 3) Delete the record from the workFlow table by using
	 * getInterfaceMappingDAO().delete with parameters interfaceMapping,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtInterfaceMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtInterfaceMapping by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		String tranType="";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InterfaceMapping interfaceMapping = new InterfaceMapping();
		BeanUtils.copyProperties((InterfaceMapping) auditHeader.getAuditDetail().getModelData(), interfaceMapping);

		if (interfaceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			getInterfaceMappingDAO().delete(interfaceMapping,"");
			auditDetails.addAll(listDeletion(interfaceMapping, "",auditHeader.getAuditTranType()));

		} else {
			interfaceMapping.setRoleCode("");
			interfaceMapping.setNextRoleCode("");
			interfaceMapping.setTaskId("");
			interfaceMapping.setNextTaskId("");
			interfaceMapping.setWorkflowId(0);

			if (interfaceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType=PennantConstants.TRAN_ADD;
				interfaceMapping.setRecordType("");
				getInterfaceMappingDAO().save(interfaceMapping,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				interfaceMapping.setRecordType("");
				getInterfaceMappingDAO().update(interfaceMapping,"");
			}
		}

		getInterfaceMappingDAO().delete(interfaceMapping,"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
	
		//Retrieving List of Audit Details For checkList details modules
		
		if (!interfaceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			if (interfaceMapping.getMasterMappingList() != null && interfaceMapping.getMasterMappingList().size() > 0) {
				List<AuditDetail> details = interfaceMapping.getLovDescAuditDetailMap().get("MasterMapping");
				details = processingMasterMappingList(details, "", interfaceMapping.getInterfaceMappingId());
				auditDetails.addAll(details);
			}
		}
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(interfaceMapping, "_Temp",auditHeader.getAuditTranType())));
		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(interfaceMapping);

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");		

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getInterfaceMappingDAO().delete with parameters interfaceMapping,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtInterfaceMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InterfaceMapping interfaceMapping = (InterfaceMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInterfaceMappingDAO().delete(interfaceMapping, "_Temp");
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(interfaceMapping
				, "_Temp",auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");

		return auditHeader;
	}

	/** 
	 * Common Method for CheckList list validation
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list){
		logger.debug("Entering");
	
		List<AuditDetail> auditDetailsList =new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType="";
				String rcdType = "";
				MasterMapping masterMapping = (MasterMapping) ((AuditDetail)list.get(i)).getModelData();			
				rcdType = masterMapping.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType= PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType) || 
						PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType= PennantConstants.TRAN_DEL;
				}else{
					transType= PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isNotEmpty(transType)){
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail)list.get(i)).getAuditSeq(),
							masterMapping.getBefImage(), masterMapping));
				}
			}
		}
	
		logger.debug("Leaving");
		
		return auditDetailsList;
	}

	
	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		InterfaceMapping interfacemapping = (InterfaceMapping) auditHeader.getAuditDetail().getModelData();

		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (interfacemapping.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(interfacemapping.getMasterMappingList()!=null && interfacemapping.getMasterMappingList().size()>0){
			auditDetailMap.put("MasterMapping", setInterfaceMappingDetailAuditData(interfacemapping,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("MasterMapping"));
		}

		interfacemapping.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(interfacemapping);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");
		return auditHeader;
	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param educationalLoan
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setInterfaceMappingDetailAuditData(InterfaceMapping interfaceMapping,String auditTranType,String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new MasterMapping(),new MasterMapping().getExcludeFields());

		for (int i = 0; i < interfaceMapping.getMasterMappingList().size(); i++) {
			MasterMapping masterMapping  = interfaceMapping.getMasterMappingList().get(i);
			
			// Skipping the process of current iteration when the child was not modified to avoid unnecessary processing
			if (StringUtils.isEmpty(masterMapping.getRecordType())) {
				continue;
			}

			masterMapping.setWorkflowId(interfaceMapping.getWorkflowId());
			masterMapping.setInterfaceMappingId(interfaceMapping.getInterfaceMappingId());

			boolean isRcdType= false;

			if (masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				masterMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				masterMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (interfaceMapping.isWorkflow()) {
					isRcdType=true;
                }
			}else if (masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				masterMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				masterMapping.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| masterMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			masterMapping.setRecordStatus(interfaceMapping.getRecordStatus());
			masterMapping.setUserDetails(interfaceMapping.getUserDetails());
			masterMapping.setLastMntOn(interfaceMapping.getLastMntOn());
			masterMapping.setLastMntBy(interfaceMapping.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], masterMapping.getBefImage(), masterMapping));
		}
		
		logger.debug("Leaving ");
		return auditDetails;
	}
	
	/**
	 * Method deletion of MasterMapping list with existing fee type
	 * @param fee
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(InterfaceMapping interfaceMapping, String tableType, String auditTranType) {
		logger.debug("Entering ");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		
		if(interfaceMapping.getMasterMappingList()!=null && interfaceMapping.getMasterMappingList().size()>0){
			
			List<MasterMapping>  masterMappingList = this.masterMappingDao.getMasterMappingDetails(interfaceMapping.getInterfaceMappingId(), tableType);
		
			if (masterMappingList != null && !masterMappingList.isEmpty()) {
				
				String[] fields = PennantJavaUtil.getFieldDetails(new MasterMapping());

				for (int i = 0; i < interfaceMapping.getMasterMappingList().size(); i++) {
					
					MasterMapping masterMapping = interfaceMapping.getMasterMappingList().get(i);
					
					if (!StringUtils.isEmpty(masterMapping.getRecordType()) || StringUtils.isEmpty(tableType)) {
						auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], masterMapping.getBefImage(), masterMapping));
					}
				}

				getMasterMappingDao().delete(interfaceMapping.getInterfaceMappingId(), tableType);
			}
		}
		
		logger.debug("Leaving ");
		return auditList;
	}
	
	/**
	 * Method For Preparing List of AuditDetails for Educational expenses
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingMasterMappingList(List<AuditDetail> auditDetails, String type,long interfaceId) {
		logger.debug("Entering ");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;

		for (int i = 0; i < auditDetails.size(); i++) {

			MasterMapping mastermapping = (MasterMapping) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;                                                                                                      
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			 
			mastermapping.setInterfaceMappingId(interfaceId);
			if (StringUtils.isEmpty(type)) {
				approveRec=true;
				mastermapping.setVersion(mastermapping.getVersion()+1);
				mastermapping.setRoleCode("");
				mastermapping.setNextRoleCode("");
				mastermapping.setTaskId("");
				mastermapping.setNextTaskId("");
			}

			mastermapping.setWorkflowId(0);

			if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(mastermapping.isNewRecord()){
				saveRecord=true;
				if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					mastermapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					mastermapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					mastermapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (mastermapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(mastermapping.isNew()){
					saveRecord=true;
				}else {
					updateRecord=true;
				}
			}

			if(approveRec){
				rcdType= mastermapping.getRecordType();
				recordStatus = mastermapping.getRecordStatus();
				mastermapping.setRecordType("");
				mastermapping.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {

				getMasterMappingDao().save(mastermapping, type);
			}

			if (updateRecord) {
				getMasterMappingDao().update(mastermapping, type);
			}

			if (deleteRecord) {
				getMasterMappingDao().delete(mastermapping, type);
			}

			if(approveRec){
				mastermapping.setRecordType(rcdType);
				mastermapping.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(mastermapping);
		}
		
		logger.debug("Leaving ");
		
		return auditDetails;	
	}
	
	
	/**
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		// Get the model object.
		InterfaceMapping interfaceMapping = (InterfaceMapping) auditDetail.getModelData();
		
		
		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_InterfaceName") + ": " + interfaceMapping.getInterfaceName();
		parameters[1] = PennantJavaUtil.getLabel("label_InterfaceField") + ": " + interfaceMapping.getInterfaceField();
		
		
		// Check the unique keys.
		if (interfaceMapping.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(interfaceMapping.getRecordType())
				&& interfaceMappingDAO.isDuplicateKey(interfaceMapping,interfaceMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		logger.debug("Leaving");
		
		return auditDetail;
	}
	
	@Override
	public List<String> getTableNameColumnsList(String tableName) {
		return getInterfaceMappingDAO().getTableNameColumnsList(tableName);
	}

	@Override
	public List<String> getMappings(String tableName, String columnName) {
		return this.masterMappingDao.getMappings(tableName, columnName);
	}
	
	// Getters And Setters 

	public InterfaceMappingDAO getInterfaceMappingDAO() {
		return interfaceMappingDAO;
	}

	public void setInterfaceMappingDAO(InterfaceMappingDAO interfaceMappingDAO) {
		this.interfaceMappingDAO = interfaceMappingDAO;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}


	public MasterMappingDAO getMasterMappingDao() {
		return masterMappingDao;
	}

	public void setMasterMappingDao(MasterMappingDAO masterMappingDao) {
		this.masterMappingDao = masterMappingDao;
	}
}