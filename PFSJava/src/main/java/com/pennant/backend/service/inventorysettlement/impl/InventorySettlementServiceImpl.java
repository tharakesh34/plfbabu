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
 * FileName    		:  InventorySettlementServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.inventorysettlement.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.inventorysettlement.InventorySettlementDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.model.inventorysettlement.InventorySettlementDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.inventorysettlement.InventorySettlementService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.exception.PFFInterfaceException;

/**
 * Service implementation for methods that depends on
 * <b>InventorySettlement</b>.<br>
 * 
 */
public class InventorySettlementServiceImpl extends GenericService<InventorySettlement> implements InventorySettlementService {
	private final static Logger			logger	= Logger.getLogger(InventorySettlementServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;

	private InventorySettlementDAO		inventorySettlementDAO;
	private PostingsPreparationUtil		postingsPreparationUtil;

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
	 * @return the inventorySettlementDAO
	 */
	public InventorySettlementDAO getInventorySettlementDAO() {
		return inventorySettlementDAO;
	}

	/**
	 * @param inventorySettlementDAO
	 *            the inventorySettlementDAO to set
	 */
	public void setInventorySettlementDAO(InventorySettlementDAO inventorySettlementDAO) {
		this.inventorySettlementDAO = inventorySettlementDAO;
	}

	/**
	 * @return the inventorySettlement
	 */
	@Override
	public InventorySettlement getInventorySettlement() {
		return getInventorySettlementDAO().getInventorySettlement();
	}

	/**
	 * @return the inventorySettlement for New Record
	 */
	@Override
	public InventorySettlement getNewInventorySettlement() {
		return getInventorySettlementDAO().getNewInventorySettlement();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * InventorySettlement/InventorySettlement_Temp by using
	 * InventorySettlementDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * InventorySettlementDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtInventorySettlement by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * InventorySettlement/InventorySettlement_Temp by using
	 * InventorySettlementDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using
	 * InventorySettlementDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtInventorySettlement by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		InventorySettlement inventorySettlement = (InventorySettlement) auditHeader.getAuditDetail().getModelData();

		if (inventorySettlement.isWorkflow()) {
			tableType = "_Temp";
		}

		if (inventorySettlement.isNew()) {
			inventorySettlement.setId(getInventorySettlementDAO().save(inventorySettlement, tableType));
			auditHeader.getAuditDetail().setModelData(inventorySettlement);
			auditHeader.setAuditReference(String.valueOf(inventorySettlement.getId()));
		} else {
			getInventorySettlementDAO().update(inventorySettlement, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table InventorySettlement by using InventorySettlementDAO's delete method
	 * with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtInventorySettlement by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		InventorySettlement inventorySettlement = (InventorySettlement) auditHeader.getAuditDetail().getModelData();
		getInventorySettlementDAO().delete(inventorySettlement, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getInventorySettlementById fetch the details by using
	 * InventorySettlementDAO's getInventorySettlementById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return InventorySettlement
	 */

	@Override
	public InventorySettlement getInventorySettlementById(long id) {
		return getInventorySettlementDAO().getInventorySettlementById(id, "_View");
	}

	/**
	 * getApprovedInventorySettlementById fetch the details by using
	 * InventorySettlementDAO's getInventorySettlementById method . with
	 * parameter id and type as blank. it fetches the approved records from the
	 * InventorySettlement.
	 * 
	 * @param id
	 *            (int)
	 * @return InventorySettlement
	 */

	public InventorySettlement getApprovedInventorySettlementById(long id) {
		return getInventorySettlementDAO().getInventorySettlementById(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getInventorySettlementDAO().delete with parameters
	 * inventorySettlement,"" b) NEW Add new record in to main table by using
	 * getInventorySettlementDAO().save with parameters inventorySettlement,""
	 * c) EDIT Update record in the main table by using
	 * getInventorySettlementDAO().update with parameters inventorySettlement,""
	 * 3) Delete the record from the workFlow table by using
	 * getInventorySettlementDAO().delete with parameters
	 * inventorySettlement,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtInventorySettlement by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow 5) Audit the record in to AuditHeader and
	 * AdtInventorySettlement by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InventorySettlement inventorySettlement = new InventorySettlement();
		BeanUtils.copyProperties((InventorySettlement) auditHeader.getAuditDetail().getModelData(), inventorySettlement);

		if (inventorySettlement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getInventorySettlementDAO().delete(inventorySettlement, "");

		} else {
			inventorySettlement.setRoleCode("");
			inventorySettlement.setNextRoleCode("");
			inventorySettlement.setTaskId("");
			inventorySettlement.setNextTaskId("");
			inventorySettlement.setWorkflowId(0);

			if (inventorySettlement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				inventorySettlement.setRecordType("");
				getInventorySettlementDAO().save(inventorySettlement, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				inventorySettlement.setRecordType("");
				getInventorySettlementDAO().update(inventorySettlement, "");
			}

			processPostings(inventorySettlement, tranType);
		}

		getInventorySettlementDAO().delete(inventorySettlement, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(inventorySettlement);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getInventorySettlementDAO().delete with
	 * parameters inventorySettlement,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtInventorySettlement by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		InventorySettlement inventorySettlement = (InventorySettlement) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getInventorySettlementDAO().delete(inventorySettlement, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit
	 * detail 2) if any error/Warnings then assign the to auditHeader 3)
	 * identify the nextprocess
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, onlineRequest);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the
	 * auditHeader. 2) fetch the details from the tables 3) Validate the Record
	 * based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from
	 * getInventorySettlementDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @param boolean onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		InventorySettlement inventorySettlement = (InventorySettlement) auditDetail.getModelData();

		InventorySettlement tempInventorySettlement = null;
		if (inventorySettlement.isWorkflow()) {
			tempInventorySettlement = getInventorySettlementDAO().getInventorySettlementById(inventorySettlement.getId(), "_Temp");
		}
		InventorySettlement befInventorySettlement = getInventorySettlementDAO().getInventorySettlementById(inventorySettlement.getId(), "");

		InventorySettlement oldInventorySettlement = inventorySettlement.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(inventorySettlement.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_Id") + ":" + valueParm[0];

		if (inventorySettlement.isNew()) {

			if (!inventorySettlement.isWorkflow()) {
				if (befInventorySettlement != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (inventorySettlement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befInventorySettlement != null || tempInventorySettlement != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else {
					if (befInventorySettlement == null || tempInventorySettlement != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!inventorySettlement.isWorkflow()) {

				if (befInventorySettlement == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldInventorySettlement != null && !oldInventorySettlement.getLastMntOn().equals(befInventorySettlement.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempInventorySettlement == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldInventorySettlement != null && !oldInventorySettlement.getLastMntOn().equals(tempInventorySettlement.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !inventorySettlement.isWorkflow()) {
			auditDetail.setBefImage(befInventorySettlement);
		}

		return auditDetail;
	}

	private List<Object> processPostings(InventorySettlement settlement, String tranType) {
		logger.debug(" Entering ");

		// Reset the allocated commodity inventories

		// save the reset details in InventorySettlement
		List<InventorySettlementDetails> details = settlement.getInventSettleDetList();
		if (details != null && !details.isEmpty()) {
			for(InventorySettlementDetails intdetails:details){
				intdetails.setId(settlement.getId());
			}
			getInventorySettlementDAO().saveInventorySettelmentDetails(details);
		}

		// get Accounting set and prepared return data set
		List<Object> returnList = null;
		try {

			List<ReturnDataSet> returnDataSetList = getPostingsPreparationUtil().prepareAccountingDataSet(settlement, AccountEventConstants.ACCEVENT_CMTINV_SET, "Y");

			if (returnDataSetList != null) {
				return getPostingsPreparationUtil().processPostings(returnDataSetList);
			} else {
				returnList = new ArrayList<Object>();
				returnList.add(false);
				returnList.add("Accounting not defined");
				return returnList;
			}

		} catch (PFFInterfaceException e) {
			logger.debug(e);
			returnList = new ArrayList<Object>();
			returnList.add(false);
			returnList.add(e.getErrorMessage());
		} catch (IllegalAccessException e) {
			logger.debug(e);
		} catch (InvocationTargetException e) {
			logger.debug(e);
		} catch (AccountNotFoundException e) {
			logger.debug(e);
		}
		logger.debug(" Leaving ");
		return returnList;

	}

	@Override
	public List<InventorySettlementDetails> getUnsoldCommodities(String brokerCode, Date settlementDate) {
		logger.debug("Entering");
		return getInventorySettlementDAO().getSettlementsByBroker(brokerCode, settlementDate);
	}


	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
}