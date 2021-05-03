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
 * FileName    		:  NPAProvisionHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-05-2020    														*
 *                                                                  						*
 * Modified Date    :  04-05-2020    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-05-2020       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.applicationmaster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AssetClassificationHeaderDAO;
import com.pennant.backend.dao.applicationmaster.NPAProvisionDetailDAO;
import com.pennant.backend.dao.applicationmaster.NPAProvisionHeaderDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.NPAProvisionHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>NPAProvisionHeader</b>.<br>
 */
public class NPAProvisionHeaderServiceImpl extends GenericService<NPAProvisionHeader>
		implements NPAProvisionHeaderService {
	private static final Logger logger = LogManager.getLogger(NPAProvisionHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private NPAProvisionHeaderDAO nPAProvisionHeaderDAO;
	private NPAProvisionDetailDAO nPAProvisionDetailDAO;
	private AssetClassificationHeaderDAO assetClassificationHeaderDAO;
	private RuleDAO ruleDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	 * @return the nPAProvisionHeaderDAO
	 */
	public NPAProvisionHeaderDAO getNPAProvisionHeaderDAO() {
		return nPAProvisionHeaderDAO;
	}

	/**
	 * @param nPAProvisionHeaderDAO
	 *            the nPAProvisionHeaderDAO to set
	 */
	public void setNPAProvisionHeaderDAO(NPAProvisionHeaderDAO nPAProvisionHeaderDAO) {
		this.nPAProvisionHeaderDAO = nPAProvisionHeaderDAO;
	}

	public NPAProvisionDetailDAO getnPAProvisionDetailDAO() {
		return nPAProvisionDetailDAO;
	}

	public void setnPAProvisionDetailDAO(NPAProvisionDetailDAO nPAProvisionDetailDAO) {
		this.nPAProvisionDetailDAO = nPAProvisionDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * NPA_PROVISION_HEADER/NPA_PROVISION_HEADER_Temp by using NPA_PROVISION_HEADERDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using NPA_PROVISION_HEADERDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtNPA_PROVISION_HEADER by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		NPAProvisionHeader nPAProvisionHeader = (NPAProvisionHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (nPAProvisionHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (nPAProvisionHeader.isNew()) {
			nPAProvisionHeader.setId(Long.parseLong(getNPAProvisionHeaderDAO().save(nPAProvisionHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(nPAProvisionHeader);
			auditHeader.setAuditReference(String.valueOf(nPAProvisionHeader.getId()));
		} else {
			getNPAProvisionHeaderDAO().update(nPAProvisionHeader, tableType);
		}

		// npAProvisionDetails
		List<NPAProvisionDetail> npaProvisionDetailList = nPAProvisionHeader.getProvisionDetailsList();
		if (CollectionUtils.isNotEmpty(npaProvisionDetailList)) {
			List<AuditDetail> npaProvisionDetails = nPAProvisionHeader.getAuditDetailMap().get("provisionDetails");
			npaProvisionDetails = processingProvisionDetailsList(npaProvisionDetails, tableType, nPAProvisionHeader);
			auditDetailsList.addAll(npaProvisionDetails);
		}
		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * NPA_PROVISION_HEADER by using NPA_PROVISION_HEADERDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtNPA_PROVISION_HEADER by using auditHeaderDAO.addAudit(auditHeader)
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

		NPAProvisionHeader nPAProvisionHeader = (NPAProvisionHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(processChildsAudit(
				deleteChilds(nPAProvisionHeader, TableType.MAIN_TAB, auditHeader.getAuditTranType())));
		getNPAProvisionHeaderDAO().delete(nPAProvisionHeader, TableType.MAIN_TAB);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getNPAProvisionHeaderDAO().delete with
	 * parameters nPAProvisionHeader,"" b) NEW Add new record in to main table by using getNPAProvisionHeaderDAO().save
	 * with parameters nPAProvisionHeader,"" c) EDIT Update record in the main table by using
	 * getNPAProvisionHeaderDAO().update with parameters nPAProvisionHeader,"" 3) Delete the record from the workFlow
	 * table by using getNPAProvisionHeaderDAO().delete with parameters nPAProvisionHeader,"_Temp" 4) Audit the record
	 * in to AuditHeader and AdtNPA_PROVISION_HEADER by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtNPA_PROVISION_HEADER by using auditHeaderDAO.addAudit(auditHeader)
	 * based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		NPAProvisionHeader nPAProvisionHeader = new NPAProvisionHeader();
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		BeanUtils.copyProperties((NPAProvisionHeader) auditHeader.getAuditDetail().getModelData(), nPAProvisionHeader);

		if (nPAProvisionHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetailsList.addAll(processChildsAudit(deleteChilds(nPAProvisionHeader, TableType.MAIN_TAB, tranType)));
			getNPAProvisionHeaderDAO().delete(nPAProvisionHeader, TableType.MAIN_TAB);
		} else {
			nPAProvisionHeader.setRoleCode("");
			nPAProvisionHeader.setNextRoleCode("");
			nPAProvisionHeader.setTaskId("");
			nPAProvisionHeader.setNextTaskId("");
			nPAProvisionHeader.setWorkflowId(0);

			if (nPAProvisionHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				nPAProvisionHeader.setRecordType("");
				getNPAProvisionHeaderDAO().save(nPAProvisionHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				nPAProvisionHeader.setRecordType("");
				getNPAProvisionHeaderDAO().update(nPAProvisionHeader, TableType.MAIN_TAB);
			}
		}
		List<NPAProvisionDetail> detailsList = nPAProvisionHeader.getProvisionDetailsList();

		if (CollectionUtils.isNotEmpty(detailsList)
				&& (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, nPAProvisionHeader.getRecordType()))) {
			List<AuditDetail> provisionDetails = nPAProvisionHeader.getAuditDetailMap().get("provisionDetails");
			provisionDetails = processingProvisionDetailsList(provisionDetails, TableType.MAIN_TAB, nPAProvisionHeader);
			auditDetailsList.addAll(provisionDetails);
		}
		getNPAProvisionHeaderDAO().delete(nPAProvisionHeader, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(processChildsAudit(
				deleteChilds(nPAProvisionHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));

		String[] fields = PennantJavaUtil.getFieldDetails(new NPAProvisionHeader(),
				nPAProvisionHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				nPAProvisionHeader.getBefImage(), nPAProvisionHeader));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(nPAProvisionHeader);
		// List
		auditHeader.setAuditDetails(processChildsAudit(auditDetailsList));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getNPAProvisionHeaderDAO().delete with parameters nPAProvisionHeader,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtNPA_PROVISION_HEADER by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		NPAProvisionHeader nPAProvisionHeader = (NPAProvisionHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(processChildsAudit(
				deleteChilds(nPAProvisionHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		getNPAProvisionHeaderDAO().delete(nPAProvisionHeader, TableType.TEMP_TAB);
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
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = prepareChildsAudit(auditHeader, method);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getNPAProvisionHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		NPAProvisionHeader nPAProvisionHeader = (NPAProvisionHeader) auditDetail.getModelData();

		// Check the unique keys.
		if (nPAProvisionHeader.isNew() && nPAProvisionHeaderDAO.isDuplicateKey(nPAProvisionHeader.getId(),
				nPAProvisionHeader.getEntity(), nPAProvisionHeader.getFinType(), nPAProvisionHeader.getNpaTemplateId(),
				nPAProvisionHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Entity") + ": " + nPAProvisionHeader.getEntity();
			parameters[1] = PennantJavaUtil.getLabel("label_FinType") + ": " + nPAProvisionHeader.getFinType()
					+ " with " + nPAProvisionHeader.getNpaTemplateCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * getNPA_PROVISION_HEADER fetch the details by using NPA_PROVISION_HEADERDAO's getNPA_PROVISION_HEADERById method.
	 * 
	 * @param id
	 *            id of the NPAProvisionHeader.
	 * @return NPA_PROVISION_HEADER
	 */
	@Override
	public NPAProvisionHeader getNPAProvisionHeader(long id) {
		return getNPAProvisionHeaderDAO().getNPAProvisionHeader(id, "_View");
	}

	/**
	 * getApprovedNPA_PROVISION_HEADERById fetch the details by using NPA_PROVISION_HEADERDAO's
	 * getNPA_PROVISION_HEADERById method . with parameter id and type as blank. it fetches the approved records from
	 * the NPA_PROVISION_HEADER.
	 * 
	 * @param id
	 *            id of the NPAProvisionHeader. (String)
	 * @return NPA_PROVISION_HEADER
	 */
	public NPAProvisionHeader getApprovedNPAProvisionHeader(long id) {
		return getNPAProvisionHeaderDAO().getNPAProvisionHeader(id, "_AView");
	}

	/**
	 * Getting new provision details
	 */
	@Override
	public NPAProvisionHeader getNewNPAProvisionHeader(NPAProvisionHeader provisionHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		List<AssetClassificationDetail> detailsList = this.assetClassificationHeaderDAO
				.getAssetClassificationDetails(provisionHeader.getFinType(), TableType.AVIEW);
		if (CollectionUtils.isEmpty(detailsList)) {
			return provisionHeader;
		}

		//provision Details List
		List<NPAProvisionDetail> provisionDeatilsList = setAstClassificationDeatils(detailsList);
		provisionHeader.setProvisionDetailsList(provisionDeatilsList);

		logger.debug(Literal.LEAVING);
		return provisionHeader;
	}

	@Override
	public NPAProvisionHeader getNewNPAProvisionHeaderByTemplate(NPAProvisionHeader provisionHeader,
			TableType tableType) {
		logger.debug(Literal.ENTERING);

		List<AssetClassificationHeader> headerList = this.assetClassificationHeaderDAO
				.getAssetClassificationHeaderByTemplate(provisionHeader.getNpaTemplateId(),
						TableType.AVIEW.getSuffix());

		if (CollectionUtils.isEmpty(headerList)) {
			return provisionHeader;
		}

		List<NPAProvisionDetail> provisionDeatilsList = new ArrayList<NPAProvisionDetail>();

		headerList.forEach(header -> {

			NPAProvisionDetail provisionDetail = new NPAProvisionDetail();
			provisionDetail.setAssetClassificationId(header.getId());
			provisionDetail.setAssetCode(header.getCode());
			provisionDetail.setAssetStageOrder(header.getStageOrder());
			provisionDeatilsList.add(provisionDetail);
		});

		provisionHeader.setProvisionDetailsList(provisionDeatilsList);

		logger.debug(Literal.LEAVING);
		return provisionHeader;
	}

	/**
	 * Getting provision header details
	 */
	@Override
	public NPAProvisionHeader getNPAProvisionHeader(NPAProvisionHeader provisionHeader, TableType tableType) {
		logger.debug(Literal.ENTERING);

		provisionHeader = this.nPAProvisionHeaderDAO.getNPAProvisionHeader(provisionHeader.getId(),
				TableType.VIEW.getSuffix());

		List<NPAProvisionDetail> provisionDetailsList = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), tableType);

		if (StringUtils.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL, provisionHeader.getRecordType())) {
			provisionHeader.setProvisionDetailsList(provisionDetailsList);
			return provisionHeader;
		}

		provisionHeader.setProvisionDetailsList(provisionDetailsList);
		logger.debug(Literal.LEAVING);
		return provisionHeader;
	}

	/**
	 * Setting asset details based on old and newly added details
	 * 
	 * @param detailsList
	 * @return
	 */
	private List<NPAProvisionDetail> setAstClassificationDeatils(List<AssetClassificationDetail> detailsList) {

		List<NPAProvisionDetail> provisionDetailsList = new ArrayList<>();

		for (AssetClassificationDetail detail : detailsList) {
			AssetClassificationHeader classificationHeader = this.assetClassificationHeaderDAO
					.getAssetClassificationHeader(detail.getHeaderId(), TableType.AVIEW.getSuffix());

			if (classificationHeader != null) {
				NPAProvisionDetail provisionDetail = new NPAProvisionDetail();
				provisionDetail.setAssetClassificationId(classificationHeader.getId());
				provisionDetail.setAssetCode(classificationHeader.getCode());
				provisionDetail.setAssetStageOrder(classificationHeader.getStageOrder());
				provisionDetailsList.add(provisionDetail);
			}
		}
		return provisionDetailsList;
	}

	///////////////Provision details Processing Start////////////////////////////
	/**
	 * Provision details processing
	 * 
	 * @param auditDetails
	 * @param tableType
	 * @param header
	 * @return
	 */
	private List<AuditDetail> processingProvisionDetailsList(List<AuditDetail> auditDetails, TableType tableType,
			NPAProvisionHeader header) {
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<NPAProvisionDetail> detailsList = new ArrayList<>();

		for (int i = 0; i < auditDetails.size(); i++) {
			NPAProvisionDetail npAProvisionDetails = (NPAProvisionDetail) auditDetails.get(i).getModelData();
			npAProvisionDetails.setHeaderId(header.getId());

			detailsList.add(npAProvisionDetails);

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				npAProvisionDetails.setRoleCode("");
				npAProvisionDetails.setNextRoleCode("");
				npAProvisionDetails.setTaskId("");
				npAProvisionDetails.setNextTaskId("");
			}

			npAProvisionDetails.setWorkflowId(0);

			if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (npAProvisionDetails.isNewRecord()) {
				saveRecord = true;
				if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					npAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					npAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					npAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (npAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (npAProvisionDetails.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = npAProvisionDetails.getRecordType();
				recordStatus = npAProvisionDetails.getRecordStatus();
				npAProvisionDetails.setRecordType("");
				npAProvisionDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				//getnPAProvisionDetailDAO().save(npAProvisionDetails, tableType);
			}

			if (updateRecord) {
				//getnPAProvisionDetailDAO().update(npAProvisionDetails, tableType);
			}

			if (deleteRecord) {
				//getnPAProvisionDetailDAO().delete(npAProvisionDetails, tableType);
			}

			if (approveRec) {
				npAProvisionDetails.setRecordType(rcdType);
				npAProvisionDetails.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(npAProvisionDetails);
		}

		saveOrUpdateProvisionDeatils(header.getId(), detailsList, tableType);

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Provision details audit preparation
	 * 
	 * @param list
	 * @return
	 */
	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetailsList;
		}

		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof NPAProvisionDetail) {
				rcdType = ((NPAProvisionDetail) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			auditDetailsList.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}

		logger.debug(Literal.LEAVING);

		return auditDetailsList;
	}

	/**
	 * Provision details deletion
	 * 
	 * @param provisionHeader
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> deleteChilds(NPAProvisionHeader provisionHeader, TableType tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		// nPAProvisionDetails
		List<NPAProvisionDetail> detailsList = provisionHeader.getProvisionDetailsList();
		NPAProvisionDetail details = new NPAProvisionDetail();

		if (CollectionUtils.isNotEmpty(detailsList)) {
			String[] fields = PennantJavaUtil.getFieldDetails(details);
			List<AuditDetail> nPAProvisionDetails = provisionHeader.getAuditDetailMap().get("provisionDetails");

			for (int i = 0; i < nPAProvisionDetails.size(); i++) {
				NPAProvisionDetail nPAProvisionDetail = (NPAProvisionDetail) nPAProvisionDetails.get(i).getModelData();
				nPAProvisionDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditDetailsList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						nPAProvisionDetail.getBefImage(), nPAProvisionDetail));
			}
			getnPAProvisionDetailDAO().deleteProvisionList(provisionHeader.getId(), tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditDetailsList;
	}

	/**
	 * Provision details audit preparation
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		NPAProvisionHeader header = (NPAProvisionHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (header.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// NPAProvision Details
		List<NPAProvisionDetail> detailsList = header.getProvisionDetailsList();
		if (CollectionUtils.isNotEmpty(detailsList)) {
			for (NPAProvisionDetail details : header.getProvisionDetailsList()) {
				details.setWorkflowId(header.getWorkflowId());
				details.setRecordStatus(header.getRecordStatus());
				details.setUserDetails(header.getUserDetails());
				details.setLastMntOn(header.getLastMntOn());
				details.setRoleCode(header.getRoleCode());
				details.setNextRoleCode(header.getNextRoleCode());
				details.setTaskId(header.getTaskId());
				details.setNextTaskId(header.getNextTaskId());
			}
			auditDetailMap.put("provisionDetails",
					setProvisionDetailsAuditData(header.getProvisionDetailsList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("provisionDetails"));
		}

		header.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(header);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Provision details audit data
	 * 
	 * @param provisionDetailsList
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setProvisionDetailsAuditData(List<NPAProvisionDetail> provisionDetailsList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new NPAProvisionDetail(),
				new NPAProvisionDetail().getExcludeFields());
		for (int i = 0; i < provisionDetailsList.size(); i++) {
			NPAProvisionDetail nPAProvisionDetails = provisionDetailsList.get(i);

			if (StringUtils.isEmpty(nPAProvisionDetails.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				nPAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				nPAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				nPAProvisionDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				nPAProvisionDetails.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| nPAProvisionDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					nPAProvisionDetails.getBefImage(), nPAProvisionDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Provision details processing
	 * 
	 * @param headerId
	 * @param detailsList
	 * @param tableType
	 */
	private void saveOrUpdateProvisionDeatils(long headerId, List<NPAProvisionDetail> detailsList,
			TableType tableType) {
		if (CollectionUtils.isEmpty(detailsList)) {
			return;
		}
		this.nPAProvisionDetailDAO.deleteProvisionList(headerId, tableType);
		this.nPAProvisionDetailDAO.saveList(detailsList, tableType);
	}

	///////////////Provision details Processing End//////////////////////////////

	@Override
	public List<AssetClassificationDetail> getAssetHeadeiIdList(String finType, TableType type) {
		return nPAProvisionHeaderDAO.getAssetHeaderIdList(finType, type);

	}

	@Override
	public AssetClassificationHeader getAssetClassificationCodesList(long listHeaderId, TableType aview) {
		return nPAProvisionHeaderDAO.getAssetClassificationCodesList(listHeaderId, aview);
	}

	public AssetClassificationHeaderDAO getAssetClassificationHeaderDAO() {
		return assetClassificationHeaderDAO;
	}

	public void setAssetClassificationHeaderDAO(AssetClassificationHeaderDAO assetClassificationHeaderDAO) {
		this.assetClassificationHeaderDAO = assetClassificationHeaderDAO;
	}

	@Override
	public boolean getIsFinTypeExists(String finType, Long npaTemplateId, TableType type) {
		return nPAProvisionHeaderDAO.getIsFinTypeExists(finType, npaTemplateId, type);
	}

	@Override
	public List<NPAProvisionDetail> getNPAProvisionDetailList(long id, TableType view) {
		return nPAProvisionDetailDAO.getNPAProvisionDetailList(id, view);
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Override
	public List<Rule> getRuleByModuleAndEvent(String module, String event, String type) {
		return ruleDAO.getRuleByModuleAndEvent(module, event, type);
	}

	@Override
	public List<NPAProvisionHeader> getNPAProvisionsListByFintype(String finType, TableType tableType) {

		List<NPAProvisionHeader> npaProvisionHeaderList = nPAProvisionHeaderDAO.getNPAProvisionsListByFintype(finType,
				tableType);

		for (NPAProvisionHeader provisionHeader : npaProvisionHeaderList) {
			List<NPAProvisionDetail> provisionDetailsList = this.nPAProvisionDetailDAO
					.getNPAProvisionDetailList(provisionHeader.getId(), tableType);
			if (CollectionUtils.isNotEmpty(provisionDetailsList)) {
				for (NPAProvisionDetail detail : provisionDetailsList) {

					AssetClassificationHeader classificationHeader = this.assetClassificationHeaderDAO
							.getAssetClassificationHeader(detail.getAssetClassificationId(),
									TableType.AVIEW.getSuffix());
					if (classificationHeader != null) {
						detail.setAssetClassificationId(classificationHeader.getId());
						detail.setAssetCode(classificationHeader.getCode());
						detail.setAssetStageOrder(classificationHeader.getStageOrder());
					}
				}
			}

			logger.debug(Literal.LEAVING);
			provisionHeader.setProvisionDetailsList(provisionDetailsList);

		}
		return npaProvisionHeaderList;
	}

}