/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related ChequeHeaders. 
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
 * FileName    		:  ChequeHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2017    														*
 *                                                                  						*
 * Modified Date    :  18-10-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinChequeHeaderService;
import com.pennant.backend.service.pdc.impl.ChequeHeaderServiceImpl;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ChequeHeader</b>.<br>
 */
public class FinChequeHeaderServiceImpl extends GenericService<ChequeHeader> implements FinChequeHeaderService {
	private static final Logger	logger	= Logger.getLogger(ChequeHeaderServiceImpl.class);

	private AuditHeaderDAO		auditHeaderDAO;
	private ChequeHeaderDAO		chequeHeaderDAO;
	private ChequeDetailDAO		chequeDetailDAO;
	private DocumentManagerDAO	documentManagerDAO;
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
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

	public ChequeHeaderDAO getChequeHeaderDAO() {
		return chequeHeaderDAO;
	}

	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

	public ChequeDetailDAO getChequeDetailDAO() {
		return chequeDetailDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table ChequeHeaders/ChequeHeaders_Temp
	 * by using ChequeHeadersDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ChequeHeadersDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtChequeHeaders by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader, TableType tableType) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		if (chequeHeader.isNew()) {
			processDocument(chequeHeader.getChequeDetailList());
			chequeHeader.setId(Long.parseLong(getChequeHeaderDAO().save(chequeHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(chequeHeader);
			auditHeader.setAuditReference(String.valueOf(chequeHeader.getHeaderID()));
		} else {
			processDocument(chequeHeader.getChequeDetailList());
			getChequeHeaderDAO().update(chequeHeader, tableType);
		}

		// ChequeHeaderModule Details Processing
		if (chequeHeader.getChequeDetailList() != null && !chequeHeader.getChequeDetailList().isEmpty()) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, tableType, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type, long headerID) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			ChequeDetail chequeDetail = (ChequeDetail) auditDetails.get(i).getModelData();
			if (StringUtils.isEmpty(chequeDetail.getRecordType())) {
				continue;
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				chequeDetail.setRoleCode("");
				chequeDetail.setNextRoleCode("");
				chequeDetail.setTaskId("");
				chequeDetail.setNextTaskId("");
				chequeDetail.setWorkflowId(0);
			}
			chequeDetail.setHeaderID(headerID);
			if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (chequeDetail.isNewRecord()) {
				saveRecord = true;
				if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				deleteRecord = true;
			}
			if (approveRec) {
				rcdType = chequeDetail.getRecordType();
				recordStatus = chequeDetail.getRecordStatus();
				chequeDetail.setRecordType("");
				chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getChequeDetailDAO().save(chequeDetail, type);
			}

			if (updateRecord) {
				getChequeDetailDAO().update(chequeDetail, type);
			}

			if (deleteRecord) {
				getChequeDetailDAO().delete(chequeDetail, type);
			}

			if (approveRec) {
				chequeDetail.setRecordType(rcdType);
				chequeDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(chequeDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail detail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		ChequeHeader chequeHeader = detail.getChequeHeader();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (chequeHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		//chequeHeader details
		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			auditDetailMap.put("ChequeDetail", setChequeDetailAuditData(chequeHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ChequeDetail"));
		}
		chequeHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(chequeHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Methods for Creating List ChequeHeader of Audit Details with detailed fields
	 * 
	 * @param product
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setChequeDetailAuditData(ChequeHeader chequeHeader, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		ChequeDetail chequeDetail = new ChequeDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(chequeDetail, chequeDetail.getExcludeFields());

		for (int i = 0; i < chequeHeader.getChequeDetailList().size(); i++) {

			ChequeDetail detail = chequeHeader.getChequeDetailList().get(i);
			if (StringUtils.isEmpty(detail.getRecordType())) {
				continue;
			}

			detail.setWorkflowId(chequeHeader.getWorkflowId());
			detail.setHeaderID(chequeHeader.getHeaderID());

			boolean isRcdType = false;

			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (chequeHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				detail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			detail.setRecordStatus(chequeHeader.getRecordStatus());
			detail.setUserDetails(chequeHeader.getUserDetails());
			detail.setLastMntOn(chequeHeader.getLastMntOn());
			detail.setLastMntBy(chequeHeader.getLastMntBy());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ChequeHeaders by using ChequeHeadersDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtChequeHeaders by using auditHeaderDAO.addAudit(auditHeader)
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

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		getChequeHeaderDAO().delete(chequeHeader, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(listDeletion(chequeHeader, TableType.MAIN_TAB, PennantConstants.TRAN_WF));

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getChequeHeaders fetch the details by using ChequeHeadersDAO's getChequeHeadersById method.
	 * 
	 * @param productID
	 *            productID of the ChequeHeader.
	 * @return ChequeHeaders
	 */
	@Override
	public ChequeHeader getChequeHeader(String finRef) {
		ChequeHeader chequeHeader = getChequeHeaderDAO().getChequeHeader(finRef, "_View");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
		}
		return chequeHeader;
	}

	/**
	 * getChequeHeaders fetch the details by using ChequeHeadersDAO's getChequeHeadersByRef method.
	 * 
	 * @param productID
	 *            productID of the ChequeHeader.
	 * @return ChequeHeaders
	 */
	@Override
	public ChequeHeader getChequeHeaderByRef(String finReference) {
		ChequeHeader chequeHeader = getChequeHeaderDAO().getChequeHeaderByRef(finReference, "_View");
		if (chequeHeader != null) {
			 chequeHeader.setChequeDetailList(getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
		}
		return chequeHeader;
	}

	/**
	 * getApprovedChequeHeadersById fetch the details by using ChequeHeadersDAO's getChequeHeadersById method . with
	 * parameter id and type as blank. it fetches the approved records from the ChequeHeaders.
	 * 
	 * @param productID
	 *            productID of the ChequeHeader. (String)
	 * @return ChequeHeaders
	 */
	public ChequeHeader getApprovedChequeHeader(String finRef) {
		ChequeHeader chequeHeader = getChequeHeaderDAO().getChequeHeader(finRef, "_AView");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_AView"));
		}
		return chequeHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getChequeHeaderDAO().delete with
	 * parameters product,"" b) NEW Add new record in to main table by using getChequeHeaderDAO().save with parameters
	 * product,"" c) EDIT Update record in the main table by using getChequeHeaderDAO().update with parameters
	 * product,"" 3) Delete the record from the workFlow table by using getChequeHeaderDAO().delete with parameters
	 * product,"_Temp" 4) Audit the record in to AuditHeader and AdtChequeHeaders by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtChequeHeaders by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		ChequeHeader chequeHeader = new ChequeHeader();
		BeanUtils.copyProperties((ChequeHeader) auditHeader.getAuditDetail().getModelData(), chequeHeader);

		if (chequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getChequeHeaderDAO().delete(chequeHeader, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(chequeHeader, TableType.MAIN_TAB, auditHeader.getAuditTranType()));

		} else {
			chequeHeader.setRoleCode("");
			chequeHeader.setNextRoleCode("");
			chequeHeader.setTaskId("");
			chequeHeader.setNextTaskId("");
			chequeHeader.setWorkflowId(0);

			processDocument(chequeHeader.getChequeDetailList());

			if (chequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				chequeHeader.setRecordType("");
				getChequeHeaderDAO().save(chequeHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				chequeHeader.setRecordType("");
				getChequeHeaderDAO().update(chequeHeader, TableType.TEMP_TAB);
			}
		}
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// Cheque Details
		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, TableType.MAIN_TAB, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(chequeHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));//FIXME
		if (auditHeader.getApiHeader() == null) {
			getChequeHeaderDAO().delete(chequeHeader, TableType.TEMP_TAB);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(chequeHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * 
	 * @param chequeDetails
	 */
	private void processDocument(List<ChequeDetail> chequeDetails) {
		if (chequeDetails != null && !chequeDetails.isEmpty()) {
			for (ChequeDetail detail : chequeDetails) {
				DocumentManager documentManager = new DocumentManager();
				if (!detail.isNewRecord()) {
					if (detail.getDocImage() != null || detail.getDocumentRef() == Long.MIN_VALUE) {
						byte[] arr1 = null;
						DocumentManager olddocumentManager = documentManagerDAO.getById(detail.getDocumentRef());
						if (olddocumentManager != null) {
							arr1 = olddocumentManager.getDocImage();
						}
						byte[] arr2 = detail.getDocImage();
						if (!Arrays.equals(arr1, arr2)) {
							documentManager.setDocImage(arr2);
							detail.setDocumentRef(documentManagerDAO.save(documentManager));
						}
					}
				} else {
					if (detail.getDocImage() != null) {
						documentManager.setDocImage(detail.getDocImage());
						detail.setDocumentRef(documentManagerDAO.save(documentManager));
					}
				}
			}
		}
	}

	// Method for Deleting all records related to ChequeHeaderModule and ChequeHeaderLOB in _Temp/Main tables depend on method type
	private List<AuditDetail> listDeletion(ChequeHeader chequeHeader, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeDetail());
			for (int i = 0; i < chequeHeader.getChequeDetailList().size(); i++) {
				ChequeDetail chequeDetail = chequeHeader.getChequeDetailList().get(i);
				if (!StringUtils.isEmpty(chequeDetail.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							chequeDetail.getBefImage(), chequeDetail));
				}
				ChequeDetail detail = chequeHeader.getChequeDetailList().get(i);
				getChequeDetailDAO().delete(detail, tableType);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getChequeHeaderDAO().delete with parameters product,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtChequeHeaders by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(chequeHeader, TableType.TEMP_TAB, PennantConstants.TRAN_WF)));
		getChequeHeaderDAO().delete(chequeHeader, TableType.TEMP_TAB);
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
		if (auditDetail.getErrorDetails() != null) {
			auditHeader.setErrorList(auditDetail.getErrorDetails());
		}
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Common Method for CheckList list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {
					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();
					if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
							|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						//check and change below line for Complete code
						//
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses()).invoke(object, object.getClass().getClasses());
						auditDetailsList.add(
								new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getChequeHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		chequeHeader.setRecordStatus(financeMain.getRecordStatus());

		// Check the unique keys.
		if (chequeHeader.isNew() && chequeHeaderDAO.isDuplicateKey(chequeHeader.getHeaderID(),
				financeDetail.getFinReference(), chequeHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + chequeHeader.getFinReference();
			parameters[1] = PennantJavaUtil.getLabel("label_HeaderID") + ": " + chequeHeader.getHeaderID();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		List<ChequeDetail> chequeDetailList = chequeHeader.getChequeDetailList();
		boolean isListContainsPDC = false;
		if (chequeDetailList != null && !chequeDetailList.isEmpty()) {
			for (ChequeDetail chequeDetail : chequeDetailList) {
				if(StringUtils.equals(chequeDetail.getChequeType(), FinanceConstants.REPAYMTH_PDC)){
					isListContainsPDC=true;
				}
				if (chequeDetail.isNew() && chequeDetailDAO.isDuplicateKey(chequeDetail.getChequeDetailsID(),
						chequeDetail.getBankBranchID(), chequeDetail.getAccountNo(), chequeDetail.getChequeSerialNo(),
						TableType.BOTH_TAB)) {

					String[] parameters = new String[3];

					parameters[0] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_BankBranchID.value") + ": "
							+ chequeDetail.getBankBranchID();
					parameters[1] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_AccNumber.value") + ": "
							+ chequeDetail.getAccountNo();
					parameters[2] = PennantJavaUtil.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value") + ": "
							+ chequeDetail.getChequeSerialNo();

					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
				}
			}
		}

		//if finance Payment Method is PDC and there is no PDC cheques.
		String finRepayMethod = financeDetail.getFinScheduleData().getFinanceMain().getFinRepayMethod();
		if (StringUtils.equals(finRepayMethod, FinanceConstants.REPAYMTH_PDC)
				&& !StringUtils.equals(PennantConstants.FINSOURCE_ID_API,
						financeDetail.getFinScheduleData().getFinanceMain().getFinSourceID())) {
			if (!isListContainsPDC) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + chequeHeader.getFinReference();
				parameters[1] = PennantJavaUtil.getLabel("label_PDC_Cheque");
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", parameters, null));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}