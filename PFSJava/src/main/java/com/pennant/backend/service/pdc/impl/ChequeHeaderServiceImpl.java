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
package com.pennant.backend.service.pdc.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ChequeHeader</b>.<br>
 */
public class ChequeHeaderServiceImpl extends GenericService<ChequeHeader> implements ChequeHeaderService {
	private static final Logger logger = Logger.getLogger(ChequeHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ChequeHeaderDAO chequeHeaderDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CustomerDetailsService customerDetailsService;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private BankBranchDAO bankBranchDAO;

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

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
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
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (chequeHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (chequeHeader.isNew()) {
			processDocument(chequeHeader);
			chequeHeader.setId(Long.parseLong(getChequeHeaderDAO().save(chequeHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(chequeHeader);
			auditHeader.setAuditReference(String.valueOf(chequeHeader.getHeaderID()));
		} else {
			processDocument(chequeHeader);
			getChequeHeaderDAO().update(chequeHeader, tableType);
		}

		// ChequeHeaderModule Details Processing
		if (chequeHeader.getChequeDetailList() != null && !chequeHeader.getChequeDetailList().isEmpty()) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, tableType, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * 
	 * @param chequeDetails
	 */
	private void processDocument(ChequeHeader chequeHeader) {
		List<ChequeDetail> cdList = chequeHeader.getChequeDetailList();
		if (CollectionUtils.isEmpty(cdList)) {
			return;
		}

		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(chequeHeader.getFinReference());
		// dd.setCustId(chequeHeader.getcustId());

		for (ChequeDetail detail : cdList) {
			if (!detail.isNewRecord()) {
				if (detail.getDocImage() != null) {
					byte[] arr1 = null;
					if (detail.getDocumentRef() != null && detail.getDocumentRef() > 0) {
						arr1 = getDocumentImage(detail.getDocumentRef());
					}
					byte[] arr2 = detail.getDocImage();
					if (!Arrays.equals(arr1, arr2)) {
						dd.setDocImage(detail.getDocImage());
						saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
						detail.setDocumentRef(dd.getDocRefId());
					}
				}
			} else {
				dd.setDocImage(detail.getDocImage());
				dd.setUserDetails(detail.getUserDetails());
				saveDocument(DMSModule.FINANCE, DMSModule.CHEQUE, dd);
				detail.setDocumentRef(dd.getDocRefId());
			}
		}
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type,
			long headerID) {
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

			if (StringUtils.isEmpty(type.getSuffix())
					&& PennantConstants.RCD_STATUS_CANCELLED.equals(chequeDetail.getRecordStatus())) {
				getChequeDetailDAO().delete(chequeDetail, type);
			} else {
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
				if (StringUtils.equals(chequeDetail.getRecordStatus(), PennantConstants.RCD_STATUS_CANCELLED)
						|| chequeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
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
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ChequeHeader chequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (chequeHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// chequeHeader details
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
		ChequeDetail detail = null;

		for (int i = 0; i < chequeHeader.getChequeDetailList().size(); i++) {

			detail = chequeHeader.getChequeDetailList().get(i);
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
		getAuditHeaderDAO().addAudit(auditHeader);

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
	public ChequeHeader getChequeHeader(long headerId) {
		ChequeHeader chequeHeader = getChequeHeaderDAO().getChequeHeader(headerId, "_View");
		if (chequeHeader != null) {
			chequeHeader
					.setChequeDetailList(getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
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
			chequeHeader
					.setChequeDetailList(getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_View"));
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
	@Override
	public ChequeHeader getApprovedChequeHeader(long headerId) {
		ChequeHeader chequeHeader = getChequeHeaderDAO().getChequeHeader(headerId, "_AView");
		if (chequeHeader != null) {
			chequeHeader.setChequeDetailList(
					getChequeDetailDAO().getChequeDetailList(chequeHeader.getHeaderID(), "_AView"));
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

			processDocument(chequeHeader);

			if (chequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				chequeHeader.setRecordType("");
				getChequeHeaderDAO().save(chequeHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				chequeHeader.setRecordType("");
				getChequeHeaderDAO().update(chequeHeader, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		// Cheque Details
		if (chequeHeader.getChequeDetailList() != null && chequeHeader.getChequeDetailList().size() > 0) {
			List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
			details = processingChequeDetailList(details, TableType.MAIN_TAB, chequeHeader.getHeaderID());
			auditDetails.addAll(details);
		}
		if (!StringUtils.equals(chequeHeader.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			auditHeader.setAuditDetails(getListAuditDetails(
					listDeletion(chequeHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));// FIXME
			getChequeHeaderDAO().delete(chequeHeader, TableType.TEMP_TAB);
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeaderDAO.addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(chequeHeader);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	// Method for Deleting all records related to ChequeHeaderModule and
	// ChequeHeaderLOB in _Temp/Main tables depend on method type
	private List<AuditDetail> listDeletion(ChequeHeader chequeHeader, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		List<AuditDetail> details = chequeHeader.getAuditDetailMap().get("ChequeDetail");
		List<ChequeDetail> chequeDetailList = new ArrayList<ChequeDetail>();

		for (int i = 0; i < details.size(); i++) {
			ChequeDetail chequeDetail = (ChequeDetail) details.get(i).getModelData();

			if (StringUtils.isEmpty(chequeDetail.getRecordType())) {
				continue;
			}
			chequeDetailList.add(chequeDetail);
		}

		if (CollectionUtils.isNotEmpty(chequeDetailList)) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeDetail());
			for (int i = 0; i < chequeDetailList.size(); i++) {
				ChequeDetail chequeDetail = chequeDetailList.get(i);
				if (!StringUtils.isEmpty(chequeDetail.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							chequeDetail.getBefImage(), chequeDetail));
				}
				ChequeDetail detail = chequeDetailList.get(i);
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

					//ChequeHeaderModule module = (ChequeHeaderModule) ((AuditDetail)list.get(i)).getModelData();			
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
						// check and change below line for Complete code
						//
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());
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
		ChequeHeader chequeHeader = (ChequeHeader) auditDetail.getModelData();

		// Check the unique keys.
		if (chequeHeader.isNew() && chequeHeaderDAO.isDuplicateKey(chequeHeader.getHeaderID(),
				chequeHeader.getFinReference(), chequeHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + chequeHeader.getFinReference();
			parameters[1] = PennantJavaUtil.getLabel("label_HeaderID") + ": " + chequeHeader.getHeaderID();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		List<ChequeDetail> chequeDetailList = chequeHeader.getChequeDetailList();
		if (chequeDetailList != null && !chequeDetailList.isEmpty()) {
			for (ChequeDetail chequeDetail : chequeDetailList) {
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
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public FinanceDetail getFinanceDetailById(String finReference) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		FinanceMain financeMain = financeMainDAO.getFinanceMainById(finReference, "_View", false);
		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(financeMain.getFinType(), "_AView");

		scheduleData.setFinanceMain(financeMain);
		scheduleData.setFinanceType(financeType);
		financeDetail.setCustomerDetails(
				customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "_View"));
		financeDetail.setJountAccountDetailList(jountAccountDetailDAO.getJountAccountDetailByFinnRef(finReference));
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	//ChequeValidations
	public List<ErrorDetail> chequeValidation(FinanceDetail financeDetail, String methodName, String tableType) {

		List<ErrorDetail> errorDetails = new ArrayList<>();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		List<Date> Chequedate = new ArrayList<>();
		FinanceMain financeMain = null;
		String finReference = financeDetail.getFinReference();

		ChequeHeader chequeHeaderDetails = chequeHeaderDAO.getChequeHeaderByRef(financeDetail.getFinReference(),
				tableType);

		if (!PennantConstants.VLD_CRT_LOAN.equalsIgnoreCase(methodName)) {
			if (chequeHeader != null) {
				if (financeDetail.getFinReference() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "FinReference";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					financeMain = financeMainDAO.getFinanceMainById(finReference, tableType, false);
					if (financeMain == null || !financeMain.isFinIsActive()) {
						String[] valueParm = new String[1];
						valueParm[0] = "finReference";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90201", valueParm)));
						return errorDetails;
					} else {
						financeDetail.getFinScheduleData().setFinanceMain(financeMain);
					}
				}
				if (PennantConstants.method_Update.equalsIgnoreCase(methodName)) {
					if (chequeHeaderDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Header id ";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
						return errorDetails;
					}
					if (chequeHeader.getHeaderID() == 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "chequeHeader Id";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if (chequeHeader.getHeaderID() != chequeHeaderDetails.getHeaderID()) {
							String[] valueParm = new String[1];
							valueParm[0] = "chequeHeader Id";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
							return errorDetails;
						}
					}
					//validating the chequedetailsId in the request
					for (ChequeDetail chquDetailsID : chequeHeader.getChequeDetailList()) {
						if (chquDetailsID.getChequeDetailsID() == 0) {
							String[] valueParm = new String[1];
							valueParm[0] = " chequeDetails ID ";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
					}
					//validating the chequeDetailsId
					List<ChequeDetail> dbChqueDetailList = chequeDetailDAO
							.getChequeDetailList(chequeHeaderDetails.getHeaderID(), tableType);
					if (CollectionUtils.isNotEmpty(dbChqueDetailList)) {
						List<Long> chequeDetailsId = new ArrayList<Long>();
						for (ChequeDetail chequeDetail : dbChqueDetailList) {
							chequeDetailsId.add(chequeDetail.getChequeDetailsID());
						}
						List<Long> chequeDtl = new ArrayList<Long>();
						List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
						for (ChequeDetail chequeDetail : chequeDetails) {
							chequeDtl.add(chequeDetail.getChequeDetailsID());
						}
						for (ChequeDetail cheque : chequeDetails) {
							if (!chequeDetailsId.contains(cheque.getChequeDetailsID())) {
								String[] valueParm = new String[1];
								valueParm[0] = " chequeDetails ";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
								return errorDetails;
							}
						}
						//validating the chequedetails deleteflag 
						int count = 0;
						for (ChequeDetail cheque : chequeHeader.getChequeDetailList()) {
							if (cheque.isDelete()) {
								count++;
							} else {
								break;
							}
							if (count == chequeHeader.getChequeDetailList().size()) {
								String[] valueParm = new String[2];
								valueParm[0] = "All ChequeDetails Notbe Deleted";
								valueParm[1] = "Greather than 0";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
								return errorDetails;
							}
						}
						//validating the cheque status 
						for (ChequeDetail chequeStatus : dbChqueDetailList) {
							for (ChequeDetail cheque : chequeHeader.getChequeDetailList()) {
								if (cheque.isDelete()) {
									if (!StringUtils.equals(PennantConstants.CHEQUESTATUS_NEW,
											chequeStatus.getChequeStatus())) {
										String[] valueParm = new String[2];
										valueParm[0] = "For " + chequeStatus.getChequeDetailsID();
										valueParm[1] = chequeStatus.getChequeStatus() + "Status";
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("49002", valueParm)));
										return errorDetails;
									}
								}
							}
						}

					}
				}

			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Header ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
		}

		//duplicateValidation
		List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
		int count = chequeDetails.size();
		if (chequeHeader.getNoOfCheques() == 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "NoOfCheques";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		} else {
			if (count != chequeHeader.getNoOfCheques()) {
				String[] valueParm = new String[2];
				valueParm[0] = "ChequeDetails ";
				valueParm[1] = " total no cheques";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30540", valueParm)));
				return errorDetails;
			}
		}
		if (!PennantConstants.method_Update.equalsIgnoreCase(methodName)) {
			if (chequeHeader.getNoOfCheques() <= 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "NoOfCheques ";
				valueParm[1] = "Three";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
				return errorDetails;
			}
		}
		//bankBranchID validation
		if (Long.valueOf(chequeHeader.getBankBranchID()) == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "BankBranchID";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		} else {
			BankBranch bankBranch = bankBranchDAO.getBankBranchById(chequeHeader.getBankBranchID(), "");
			if (bankBranch.getBankBranchID() != chequeHeader.getBankBranchID()) {
				String[] valueParm = new String[1];
				valueParm[0] = "BankBranch";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isBlank(chequeHeader.getAccHolderName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "AccHolderName";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.isBlank(chequeHeader.getAccountNo())) {
				String[] valueParm = new String[1];
				valueParm[0] = "AccountNo";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (chequeHeader.getAccountNo().length() > 15) {
				String[] valueParm = new String[2];
				valueParm[0] = "AccountLength";
				valueParm[1] = "or Equal to 15";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
				return errorDetails;
			}
			if (chequeHeader.getChequeSerialNo() == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "ChequeSerialNo";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				if (String.valueOf(chequeHeader.getChequeSerialNo()).length() > 6) {
					String[] valueParm = new String[2];
					valueParm[0] = "ChequeSerialNo";
					valueParm[1] = "or Equal to size Six";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
					return errorDetails;
				}
			}
		}
		for (ChequeDetail chequeDetail : chequeDetails) {
			//ChequeType

			if (StringUtils.isBlank(chequeDetail.getChequeType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				List<ValueLabel> chequeTypeList = PennantStaticListUtil.getChequeTypes();
				List<String> chequeType = new ArrayList<String>();
				for (ValueLabel valueLabel : chequeTypeList) {
					chequeType.add(valueLabel.getValue());
				}
				if (!(chequeType.contains(chequeDetail.getChequeType()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}
			//AccountType Validation
			if (StringUtils.isBlank(chequeDetail.getAccountType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "accountType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				List<ValueLabel> accTypeList = PennantStaticListUtil.getChequeAccTypeList();
				List<Object> accType = new ArrayList<Object>();
				for (ValueLabel valueLabel : accTypeList) {
					accType.add(valueLabel.getValue());
				}
				if (!(accType.contains(chequeDetail.getAccountType()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "accountType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}
			if (chequeDetail.getAmount() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Amount";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeDetail.getChequeType())
					&& chequeDetail.getChequeDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate for pdc";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.equals(FinanceConstants.REPAYMTH_UDC, chequeDetail.getChequeType())
					&& chequeDetail.getChequeDate() != null) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate For UDC";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
				return errorDetails;
			}
			if (chequeDetail.getAmount().toString().length() >= 18) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate For UDC";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
				return errorDetails;
			}
			//duplicate date validations
			if (chequeDetail.getChequeDate() != null) {
				Chequedate.add(chequeDetail.getChequeDate());
				int compareDate = 0;
				for (int i = 0; i < Chequedate.size(); i++) {
					Date d1 = Chequedate.get(i);
					for (int j = i; j < Chequedate.size(); j++) {
						Date d2 = Chequedate.get(j);
						if (d1.compareTo(d2) == 0) {
							compareDate++;
						}
					}
				}
				if (compareDate > Chequedate.size()) {
					String[] valueParm = new String[1];
					valueParm[0] = "Cheque Dates";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
					return errorDetails;
				}
			}
		}

		return errorDetails;

	}

	// ChequeValidations
	public List<ErrorDetail> chequeValidationInMaintainence(FinanceDetail financeDetail, String methodName,
			String tableType) {

		List<ErrorDetail> errorDetails = new ArrayList<>();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		List<Date> Chequedate = new ArrayList<>();
		FinanceMain financeMain = null;
		String finReference = financeDetail.getFinReference();

		ChequeHeader dbChequeHeaderDetails = chequeHeaderDAO.getChequeHeaderByRef(financeDetail.getFinReference(),
				tableType);

		if (!PennantConstants.VLD_CRT_LOAN.equalsIgnoreCase(methodName)) {
			if (chequeHeader != null) {
				if (financeDetail.getFinReference() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "FinReference";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					financeMain = financeMainDAO.getFinanceMainById(finReference, tableType, false);
					if (financeMain == null || !financeMain.isFinIsActive()) {
						String[] valueParm = new String[1];
						valueParm[0] = "finReference";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90201", valueParm)));
						return errorDetails;
					} else {
						financeDetail.getFinScheduleData().setFinanceMain(financeMain);
					}
				}
				if (PennantConstants.method_Update.equalsIgnoreCase(methodName)) {
					if (dbChequeHeaderDetails == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "Header id ";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
						return errorDetails;
					}
					if (chequeHeader.getHeaderID() == 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "chequeHeader Id";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
						return errorDetails;
					} else {
						if (chequeHeader.getHeaderID() != dbChequeHeaderDetails.getHeaderID()) {
							String[] valueParm = new String[1];
							valueParm[0] = "chequeHeader Id";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
							return errorDetails;
						}
					}
					// validating the chequedetailsId in the request
					for (ChequeDetail chquDetailsID : chequeHeader.getChequeDetailList()) {
						if (chquDetailsID.getChequeDetailsID() == 0) {
							String[] valueParm = new String[1];
							valueParm[0] = " chequeDetails ID ";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
							return errorDetails;
						}
					}
					// validating the chequeDetailsId
					List<ChequeDetail> dbChqueDetailList = chequeDetailDAO
							.getChequeDetailList(dbChequeHeaderDetails.getHeaderID(), tableType);
					if (CollectionUtils.isNotEmpty(dbChqueDetailList)) {
						List<Long> chequeDetailsId = new ArrayList<Long>();
						for (ChequeDetail chequeDetail : dbChqueDetailList) {
							chequeDetailsId.add(chequeDetail.getChequeDetailsID());
						}
						List<Long> chequeDtl = new ArrayList<Long>();
						List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
						for (ChequeDetail chequeDetail : chequeDetails) {
							chequeDtl.add(chequeDetail.getChequeDetailsID());
						}
						for (ChequeDetail cheque : chequeDetails) {
							if (!chequeDetailsId.contains(cheque.getChequeDetailsID())) {
								String[] valueParm = new String[1];
								valueParm[0] = " chequeDetails ";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
								return errorDetails;
							}
						}
						// validating the chequedetails deleteflag
						int count = 0;
						for (ChequeDetail cheque : chequeHeader.getChequeDetailList()) {
							if (cheque.isDelete()) {
								count++;
							} else {
								break;
							}
							if (count == chequeHeader.getChequeDetailList().size()) {
								String[] valueParm = new String[2];
								valueParm[0] = "All ChequeDetails Notbe Deleted";
								valueParm[1] = "Greather than 0";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
								return errorDetails;
							}
						}
						// validating the cheque status
						for (ChequeDetail chequeStatus : dbChqueDetailList) {
							for (ChequeDetail cheque : chequeHeader.getChequeDetailList()) {
								if (cheque.isDelete()) {
									if (!StringUtils.equals(PennantConstants.CHEQUESTATUS_NEW,
											chequeStatus.getChequeStatus())) {
										String[] valueParm = new String[2];
										valueParm[0] = "For " + chequeStatus.getChequeDetailsID();
										valueParm[1] = chequeStatus.getChequeStatus() + "Status";
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("49002", valueParm)));
										return errorDetails;
									}
								}
							}
						}

					}
				}

			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "Cheque Header ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
		}

		// duplicateValidation
		List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
		int count = chequeDetails.size();
		if (chequeHeader.getNoOfCheques() == 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "NoOfCheques";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		} else {
			if (count != chequeHeader.getNoOfCheques()) {
				String[] valueParm = new String[2];
				valueParm[0] = "ChequeDetails ";
				valueParm[1] = " total no cheques";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30540", valueParm)));
				return errorDetails;
			}
		}
		if (!PennantConstants.method_Update.equalsIgnoreCase(methodName)) {
			if (chequeHeader.getNoOfCheques() <= 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "NoOfCheques ";
				valueParm[1] = "Three";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
				return errorDetails;
			}
		}
		// bankBranchID validation
		if (Long.valueOf(chequeHeader.getBankBranchID()) == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "BankBranchID";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
			return errorDetails;
		} else {
			BankBranch bankBranch = bankBranchDAO.getBankBranchById(chequeHeader.getBankBranchID(), "");
			if (bankBranch.getBankBranchID() != chequeHeader.getBankBranchID()) {
				String[] valueParm = new String[1];
				valueParm[0] = "BankBranch";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
				return errorDetails;
			}

			if (StringUtils.isBlank(chequeHeader.getAccHolderName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "AccHolderName";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.isBlank(chequeHeader.getAccountNo())) {
				String[] valueParm = new String[1];
				valueParm[0] = "AccountNo";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (chequeHeader.getAccountNo().length() > 15) {
				String[] valueParm = new String[2];
				valueParm[0] = "AccountLength";
				valueParm[1] = "or Equal to 15";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
				return errorDetails;
			}
		}
		for (ChequeDetail chequeDetail : chequeDetails) {
			// ChequeType

			if (StringUtils.isBlank(chequeDetail.getChequeType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				List<ValueLabel> chequeTypeList = PennantStaticListUtil.getChequeTypes();
				List<String> chequeType = new ArrayList<String>();
				for (ValueLabel valueLabel : chequeTypeList) {
					chequeType.add(valueLabel.getValue());
				}
				if (!(chequeType.contains(chequeDetail.getChequeType()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}
			// AccountType Validation
			if (StringUtils.isBlank(chequeDetail.getAccountType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "accountType";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				List<ValueLabel> accTypeList = PennantStaticListUtil.getChequeAccTypeList();
				List<Object> accType = new ArrayList<Object>();
				for (ValueLabel valueLabel : accTypeList) {
					accType.add(valueLabel.getValue());
				}
				if (!(accType.contains(chequeDetail.getAccountType()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "accountType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}
			}
			if (chequeDetail.getAmount() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Amount";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeDetail.getChequeType())
					&& chequeDetail.getChequeDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate for pdc";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			}
			if (StringUtils.equals(FinanceConstants.REPAYMTH_UDC, chequeDetail.getChequeType())
					&& chequeDetail.getChequeDate() != null) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate For UDC";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
				return errorDetails;
			}
			if (chequeDetail.getAmount().toString().length() >= 18) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeDate For UDC";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
				return errorDetails;
			}
			// duplicate date validations
			if (chequeDetail.getChequeDate() != null) {
				Chequedate.add(chequeDetail.getChequeDate());
				int compareDate = 0;
				for (int i = 0; i < Chequedate.size(); i++) {
					Date d1 = Chequedate.get(i);
					for (int j = i; j < Chequedate.size(); j++) {
						Date d2 = Chequedate.get(j);
						if (d1.compareTo(d2) == 0) {
							compareDate++;
						}
					}
				}
				if (compareDate > Chequedate.size()) {
					String[] valueParm = new String[1];
					valueParm[0] = "Cheque Dates";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
					return errorDetails;
				}
			}
		}

		return errorDetails;

	}

	//ChequeValidations
	public List<ErrorDetail> chequeValidationForUpdate(FinanceDetail financeDetail, String methodName,
			String tableType) {

		List<ErrorDetail> errorDetails = new ArrayList<>();
		ChequeHeader chequeHeader = financeDetail.getChequeHeader();
		List<Date> Chequedate = new ArrayList<>();
		FinanceMain financeMain = null;
		String finReference = financeDetail.getFinReference();

		if (chequeHeader != null) {
			ChequeHeader dbChequeHeader = chequeHeaderDAO.getChequeHeaderByRef(finReference, tableType);
			if (dbChequeHeader == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Header id ";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
				return errorDetails;
			}
			chequeHeader.setVersion(dbChequeHeader.getVersion() + 1);

			if (chequeHeader.getHeaderID() != dbChequeHeader.getHeaderID()) {
				String[] valueParm = new String[1];
				valueParm[0] = "chequeHeader Id";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
				return errorDetails;
			}

			// duplicateValidation
			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			int count = chequeDetails.size();
			if (chequeHeader.getNoOfCheques() == 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "NoOfCheques";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				if (count != chequeHeader.getNoOfCheques()) {
					String[] valueParm = new String[2];
					valueParm[0] = "ChequeDetails ";
					valueParm[1] = " total no cheques";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30540", valueParm)));
					return errorDetails;
				}
			}

			// FIX ME this should come from loan type
			if (chequeHeader.getNoOfCheques() <= 2) {
				String[] valueParm = new String[2];
				valueParm[0] = "NoOfCheques ";
				valueParm[1] = "Three";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30569", valueParm)));
				return errorDetails;
			}

			// bankBranchID validation
			if (Long.valueOf(chequeHeader.getBankBranchID()) == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "BankBranchID";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
				return errorDetails;
			} else {
				BankBranch bankBranch = bankBranchDAO.getBankBranchById(chequeHeader.getBankBranchID(), "");
				if (bankBranch.getBankBranchID() != chequeHeader.getBankBranchID()) {
					String[] valueParm = new String[1];
					valueParm[0] = "BankBranch";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
					return errorDetails;
				}

				if (StringUtils.isBlank(chequeHeader.getAccHolderName())) {
					String[] valueParm = new String[1];
					valueParm[0] = "AccHolderName";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (StringUtils.isBlank(chequeHeader.getAccountNo())) {
					String[] valueParm = new String[1];
					valueParm[0] = "AccountNo";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (chequeHeader.getAccountNo().length() > 15) {
					String[] valueParm = new String[2];
					valueParm[0] = "AccountLength";
					valueParm[1] = "or Equal to 15";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
					return errorDetails;
				}
				if (chequeHeader.getChequeSerialNo() == 0) {
					String[] valueParm = new String[1];
					valueParm[0] = "ChequeSerialNo";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					if (String.valueOf(chequeHeader.getChequeSerialNo()).length() > 6) {
						String[] valueParm = new String[2];
						valueParm[0] = "ChequeSerialNo";
						valueParm[1] = "or Equal to size Six";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", valueParm)));
						return errorDetails;
					}
				}
			}
			for (ChequeDetail chequeDetail : chequeDetails) {
				// ChequeType

				if (StringUtils.isBlank(chequeDetail.getChequeType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					List<ValueLabel> chequeTypeList = PennantStaticListUtil.getChequeTypes();
					List<String> chequeType = new ArrayList<String>();
					for (ValueLabel valueLabel : chequeTypeList) {
						chequeType.add(valueLabel.getValue());
					}
					if (!(chequeType.contains(chequeDetail.getChequeType()))) {
						String[] valueParm = new String[1];
						valueParm[0] = "chequeType";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
						return errorDetails;
					}
				}
				// AccountType Validation
				if (StringUtils.isBlank(chequeDetail.getAccountType())) {
					String[] valueParm = new String[1];
					valueParm[0] = "accountType";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				} else {
					List<ValueLabel> accTypeList = PennantStaticListUtil.getChequeAccTypeList();
					List<Object> accType = new ArrayList<Object>();
					for (ValueLabel valueLabel : accTypeList) {
						accType.add(valueLabel.getValue());
					}
					if (!(accType.contains(chequeDetail.getAccountType()))) {
						String[] valueParm = new String[1];
						valueParm[0] = "accountType";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
						return errorDetails;
					}
				}
				if (chequeDetail.getAmount() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "Amount";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeDetail.getChequeType())
						&& chequeDetail.getChequeDate() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeDate for pdc";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));
					return errorDetails;
				}
				if (StringUtils.equals(FinanceConstants.REPAYMTH_UDC, chequeDetail.getChequeType())
						&& chequeDetail.getChequeDate() != null) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeDate For UDC";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
					return errorDetails;
				}
				if (chequeDetail.getAmount().toString().length() >= 18) {
					String[] valueParm = new String[1];
					valueParm[0] = "chequeDate For UDC";
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0039", valueParm)));
					return errorDetails;
				}
				// duplicate date validations
				if (chequeDetail.getChequeDate() != null) {
					Chequedate.add(chequeDetail.getChequeDate());
					int compareDate = 0;
					for (int i = 0; i < Chequedate.size(); i++) {
						Date d1 = Chequedate.get(i);
						for (int j = i; j < Chequedate.size(); j++) {
							Date d2 = Chequedate.get(j);
							if (d1.compareTo(d2) == 0) {
								compareDate++;
							}
						}
					}
					if (compareDate > Chequedate.size()) {
						String[] valueParm = new String[1];
						valueParm[0] = "Cheque Dates";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90273", valueParm)));
						return errorDetails;
					}
				}
			}
		}
		return errorDetails;
	}

	public void setDmsService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}
}