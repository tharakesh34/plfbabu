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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.TaxDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>Province</b>.<br>
 * 
 */
public class ProvinceServiceImpl extends GenericService<Province> implements ProvinceService {

	private static final Logger logger = Logger.getLogger(ProvinceServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProvinceDAO provinceDAO;
	private TaxDetailDAO taxDetailDAO;
	private CityDAO cityDAO;
	private transient TaxDetailService taxDetailService;
	private GSTInvoiceTxnDAO  gstInvoiceTxnDAO;
	
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
	public void setTaxDetailDAO(TaxDetailDAO taxDetailDAO) {
		this.taxDetailDAO = taxDetailDAO;
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
	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
	
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		Province province = (Province) auditHeader.getAuditDetail().getModelData();

		if (province.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (province.isNew()) {
			getProvinceDAO().save(province, tableType);
			auditHeader.getAuditDetail().setModelData(province);
			auditHeader.setAuditReference(province.getCPCountry() + PennantConstants.KEY_SEPERATOR + province.getCPProvince());
		} else {
			getProvinceDAO().update(province, tableType);
		}

		if (province.getTaxDetailList() != null && province.getTaxDetailList().size() > 0) {
			List<AuditDetail> details = province.getAuditDetailMap().get("TaxDetail");
			details = processTaxDetails(details, tableType);
			auditDetails.addAll(details);
		}
		
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		logger.debug("Leaving");

		return auditHeader;
	}
	
	
	
	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processTaxDetails(List<AuditDetail> auditDetails, TableType type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			TaxDetail taxDetail = (TaxDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				taxDetail.setRoleCode("");
				taxDetail.setNextRoleCode("");
				taxDetail.setTaskId("");
				taxDetail.setNextTaskId("");
			}

			if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (taxDetail.isNewRecord()) {
				saveRecord = true;
				if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					taxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					taxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					taxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (taxDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = taxDetail.getRecordType();
				recordStatus = taxDetail.getRecordStatus();
				taxDetail.setRecordType("");
				taxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				taxDetailDAO.save(taxDetail, type);
			}

			if (updateRecord) {
				taxDetailDAO.update(taxDetail, type);
			}
			
			if (deleteRecord) {
				taxDetailDAO.delete(taxDetail, type);
			}

			if (approveRec) {
				taxDetail.setRecordType(rcdType);
				taxDetail.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(taxDetail);
		}

		logger.debug("Leaving");

		return auditDetails;
	}
	
	/**
	 * Save Sequence Table for GST Invoice Preparation
	 * @param taxDetail
	 */
	private void saveSeqGstInvoice(Province province) {
		
		if (StringUtils.isBlank(province.getTaxStateCode())) {
			return;
		}
		
		SeqGSTInvoice seqGstInvoice = new SeqGSTInvoice();
		seqGstInvoice.setSeqNo(0);
		seqGstInvoice.setGstStateCode(province.getTaxStateCode());
		
		seqGstInvoice.setTransactionType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);
		SeqGSTInvoice seqGstInvoiceTemp = this.gstInvoiceTxnDAO.getSeqGSTInvoice(seqGstInvoice);
		
		if (seqGstInvoiceTemp == null) {
			gstInvoiceTxnDAO.saveSeqGSTInvoice(seqGstInvoice);
		}

		seqGstInvoice.setTransactionType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
		seqGstInvoiceTemp = this.gstInvoiceTxnDAO.getSeqGSTInvoice(seqGstInvoice);

		if (seqGstInvoiceTemp == null) {
			gstInvoiceTxnDAO.saveSeqGSTInvoice(seqGstInvoice);
		}
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

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = (Province) auditHeader.getAuditDetail().getModelData();
		getProvinceDAO().delete(province, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(province, TableType.MAIN_TAB, auditHeader.getAuditTranType())));

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
		logger.debug("Entering");
		Province province =  getProvinceDAO().getProvinceById(cPCountry, cPProvince, "_View");
		
		if (province != null) {
			province.setTaxDetailList(taxDetailService.getTaxDetailbystateCode(province.getCPProvince(), "_View"));
		}
		
		logger.debug("");
		
		return province;
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
		
		Province province =  getProvinceDAO().getProvinceById(cPCountry, cPProvince, "_AView");
		
		if (province != null) {
			province.setTaxDetailList(taxDetailService.getTaxDetailbystateCode(province.getCPProvince(), "_AView"));
		}
		
		return province;
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "Approve");
		
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = new Province();
		BeanUtils.copyProperties((Province) auditHeader.getAuditDetail().getModelData(), province);

		
		if (!PennantConstants.RECORD_TYPE_NEW.equals(province.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(provinceDAO.getProvinceById(province.getCPCountry(), province.getCPProvince(), ""));
		}

		if (province.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(province, TableType.MAIN_TAB, auditHeader.getAuditTranType()));
			getProvinceDAO().delete(province, TableType.MAIN_TAB);
		} else {
			province.setRoleCode("");
			province.setNextRoleCode("");
			province.setTaskId("");
			province.setNextTaskId("");
			province.setWorkflowId(0);

			if (province.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				province.setRecordType("");
				getProvinceDAO().save(province, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				province.setRecordType("");
				getProvinceDAO().update(province, TableType.MAIN_TAB);
			}
			
			// GST Invoice Report Sequence Table insert
			saveSeqGstInvoice(province);
			
			if (province.getTaxDetailList() != null && province.getTaxDetailList().size() > 0) {
				List<AuditDetail> details = province.getAuditDetailMap().get("TaxDetail");
				details = processTaxDetails(details, TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}
		}

		getProvinceDAO().delete(province, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(province, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
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

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Province province = (Province) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProvinceDAO().delete(province, TableType.TEMP_TAB);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(province, TableType.TEMP_TAB, auditHeader.getAuditTranType())));

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
	private AuditHeader businessValidation(AuditHeader auditHeader,  String method) {
		logger.debug("Entering");
	
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		
		logger.debug("Leaving");
		
		return auditHeader;
	}
	
	
	
	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		Province province = (Province) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (province.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		
		if (province.getTaxDetailList() != null && province.getTaxDetailList().size() > 0) {
			auditDetailMap.put("TaxDetail", setTaxDetailsData(province, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("TaxDetail"));
			for (AuditDetail auditDetail : auditDetailMap.get("TaxDetail")) {
				auditDetail = this.taxDetailService.validation(auditDetail, auditHeader.getUsrLanguage());
				List<ErrorDetail> details = auditDetail.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		
		auditHeader.setErrorList(errorDetails);

		province.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(province);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}
	
	
	/**
	 * Common Method for Customers list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				TaxDetail taxDetail = (TaxDetail) ((AuditDetail) list.get(i)).getModelData();

				rcdType = taxDetail.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType) || PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), taxDetail.getBefImage(), taxDetail));
				}
			}
		}
		
		logger.debug("Leaving");
		return auditDetailsList;
	}
	
	
	/**
	 * Method deletion of feeTier list with existing fee type
	 * 
	 * @param province
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(Province province, TableType tableType, String auditTranType) {
		logger.debug("Entering");
		
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (province.getTaxDetailList() != null && province.getTaxDetailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new TaxDetail());

			for (int i = 0; i < province.getTaxDetailList().size(); i++) {
				TaxDetail taxDetail = province.getTaxDetailList().get(i);
				if (StringUtils.isNotEmpty(taxDetail.getRecordType()) || StringUtils.isEmpty(tableType.getSuffix())) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], taxDetail.getBefImage(), taxDetail));
				}
				taxDetailDAO.delete(province.getTaxDetailList().get(i), tableType);
			}
		}

		logger.debug("Leaving");
		
		return auditList;
	}
	
	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setTaxDetailsData(Province province, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		TaxDetail detail = new TaxDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());

		for (int i = 0; i < province.getTaxDetailList().size(); i++) {
			TaxDetail taxDetail = province.getTaxDetailList().get(i);

			if (StringUtils.isEmpty(taxDetail.getRecordType())) {
				continue;
			}

			taxDetail.setWorkflowId(province.getWorkflowId());

			boolean isRcdType = false;

			if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				taxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				taxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				taxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				taxDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| taxDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			taxDetail.setRecordStatus(province.getRecordStatus());
			taxDetail.setUserDetails(province.getUserDetails());
			taxDetail.setLastMntOn(province.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], taxDetail.getBefImage(), taxDetail));
		}
		
		logger.debug("Leaving");
		
		return auditDetails;
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
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}
		
		// Duplicate State Code
		boolean isStateCodeExist = getStateCodeExist(province.getTaxStateCode(), province.getCPProvince(), "_View");
		if ((province.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
				|| province.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) && province.isNewRecord()
				&& isStateCodeExist) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_TaxStateCode") + ":"+ province.getTaxStateCode();
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
		}
		if (province.isSystemDefault()) {
			String dftCPProvince = getProvinceDAO().getSystemDefaultCount(province.getCPProvince());
			if (StringUtils.isNotEmpty(dftCPProvince)) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60501",
				        new String[]{dftCPProvince,PennantJavaUtil.getLabel("Province")}, null));
			}
        }
		
		if (PennantConstants.RECORD_TYPE_DEL.equals(StringUtils.trimToEmpty(province.getRecordType()))) {
			int count = this.cityDAO.getPCProvinceCount(province.getCPProvince(), "_View");
			if (count > 0) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = province.getCPProvince();
				errParm[0] = PennantJavaUtil.getLabel("label_ProvinceDialog_CPProvince.value") + " : " + valueParm[0];
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41006", errParm, valueParm), usrLanguage));
			}
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	private boolean getStateCodeExist(String taxStateCode, String proviceCode, String type) {
		logger.debug("Entering");

		boolean codeExist = false;

		if (getProvinceDAO().geStateCodeCount(taxStateCode, proviceCode, type) != 0) {
			codeExist = true;
		}

		logger.debug("Leaving");

		return codeExist;
	}

	@Override
	public boolean getBusinessAreaExist(String businessAreaValue, String type) {
		logger.debug("Entering");
		
		boolean businessArea = false;

		if (getProvinceDAO().getBusinessAreaCount(businessAreaValue, type) != 0) {
			businessArea = true;
		}

		logger.debug("Leaving");

		return businessArea;
	}

	public TaxDetailService getTaxDetailService() {
		return taxDetailService;
	}

	public void setTaxDetailService(TaxDetailService taxDetailService) {
		this.taxDetailService = taxDetailService;
	}
	
	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public GSTInvoiceTxnDAO getGstInvoiceTxnDAO() {
		return gstInvoiceTxnDAO;
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}
}