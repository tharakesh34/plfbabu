/**
\ * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  AssetClassificationHeaderServiceImpl.java                                                   * 	  
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.AssetClassificationHeaderDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.AssetClassificationHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>AssetClassificationHeader</b>.<br>
 */
public class AssetClassificationHeaderServiceImpl extends GenericService<AssetClassificationHeader>
		implements AssetClassificationHeaderService {
	private static final Logger logger = LogManager.getLogger(AssetClassificationHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssetClassificationHeaderDAO assetClassificationHeaderDAO;

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
	 * @return the assetClassificationHeaderDAO
	 */
	public AssetClassificationHeaderDAO getAssetClassificationHeaderDAO() {
		return assetClassificationHeaderDAO;
	}

	/**
	 * @param assetClassificationHeaderDAO
	 *            the assetClassificationHeaderDAO to set
	 */
	public void setAssetClassificationHeaderDAO(AssetClassificationHeaderDAO assetClassificationHeaderDAO) {
		this.assetClassificationHeaderDAO = assetClassificationHeaderDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ASSET_CLASSIFICATION_HDR/ASSET_CLASSIFICATION_HDR_Temp by using ASSET_CLASSIFICATION_HDRDAO's save method b)
	 * Update the Record in the table. based on the module workFlow Configuration. by using
	 * ASSET_CLASSIFICATION_HDRDAO's update method 3) Audit the record in to AuditHeader and AdtASSET_CLASSIFICATION_HDR
	 * by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetClassificationHeader header = (AssetClassificationHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (header.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (header.isNew()) {
			header.setId(Long.parseLong(getAssetClassificationHeaderDAO().save(header, tableType)));
			auditHeader.getAuditDetail().setModelData(header);
			auditHeader.setAuditReference(String.valueOf(header.getId()));
		} else {
			getAssetClassificationHeaderDAO().update(header, tableType);
		}

		// Classification Details
		List<AssetClassificationDetail> assetClassificationDetailList = header.getAssetClassificationDetailList();
		if (CollectionUtils.isNotEmpty(assetClassificationDetailList)) {
			List<AuditDetail> details = header.getAuditDetailMap().get("AssetClassificationDetail");
			details = processingAssetDetailList(details, tableType, header);
			auditDetailsList.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetailsList);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		AssetClassificationHeader assetClassificationHeader = (AssetClassificationHeader) auditHeader.getAuditDetail()
				.getModelData();
		auditHeader.setAuditDetails(processChildsAudit(
				deleteChilds(assetClassificationHeader, TableType.MAIN_TAB, auditHeader.getAuditTranType())));
		getAssetClassificationHeaderDAO().delete(assetClassificationHeader, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getASSET_CLASSIFICATION_HDR fetch the details by using ASSET_CLASSIFICATION_HDRDAO's
	 * getASSET_CLASSIFICATION_HDRById method.
	 * 
	 * @param id
	 *            id of the AssetClassificationHeader.
	 * @return ASSET_CLASSIFICATION_HDR
	 */
	@Override
	public AssetClassificationHeader getAssetClassificationHeader(long id) {
		AssetClassificationHeader header = getAssetClassificationHeaderDAO().getAssetClassificationHeader(id,
				TableType.VIEW.getSuffix());
		if (header != null) {
			header.setAssetClassificationDetailList(
					getAssetClassificationHeaderDAO().getAssetDetailList(id, TableType.VIEW));
		}
		return header;
	}

	/**
	 * getApprovedASSET_CLASSIFICATION_HDRById fetch the details by using ASSET_CLASSIFICATION_HDRDAO's
	 * getASSET_CLASSIFICATION_HDRById method . with parameter id and type as blank. it fetches the approved records
	 * from the ASSET_CLASSIFICATION_HDR.
	 * 
	 * @param id
	 *            id of the AssetClassificationHeader. (String)
	 * @return ASSET_CLASSIFICATION_HDR
	 */
	public AssetClassificationHeader getApprovedAssetClassificationHeader(long id) {
		AssetClassificationHeader header = getAssetClassificationHeaderDAO().getAssetClassificationHeader(id,
				TableType.AVIEW.getSuffix());
		if (header != null) {
			header.setAssetClassificationDetailList(
					getAssetClassificationHeaderDAO().getAssetDetailList(id, TableType.AVIEW));
		}
		return header;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using
	 * getAssetClassificationHeaderDAO().delete with parameters assetClassificationHeader,"" b) NEW Add new record in to
	 * main table by using getAssetClassificationHeaderDAO().save with parameters assetClassificationHeader,"" c) EDIT
	 * Update record in the main table by using getAssetClassificationHeaderDAO().update with parameters
	 * assetClassificationHeader,"" 3) Delete the record from the workFlow table by using
	 * getAssetClassificationHeaderDAO().delete with parameters assetClassificationHeader,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtASSET_CLASSIFICATION_HDR by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtASSET_CLASSIFICATION_HDR by using auditHeaderDAO.addAudit(auditHeader)
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

		AssetClassificationHeader header = new AssetClassificationHeader();
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();
		BeanUtils.copyProperties((AssetClassificationHeader) auditHeader.getAuditDetail().getModelData(), header);

		if (header.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetailsList.addAll(processChildsAudit(deleteChilds(header, TableType.MAIN_TAB, tranType)));
			getAssetClassificationHeaderDAO().delete(header, TableType.MAIN_TAB);
		} else {
			header.setRoleCode("");
			header.setNextRoleCode("");
			header.setTaskId("");
			header.setNextTaskId("");
			header.setWorkflowId(0);

			if (header.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				header.setRecordType("");
				getAssetClassificationHeaderDAO().save(header, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				header.setRecordType("");
				getAssetClassificationHeaderDAO().update(header, TableType.MAIN_TAB);
			}
			List<AssetClassificationDetail> detailsList = header.getAssetClassificationDetailList();
			if (CollectionUtils.isNotEmpty(detailsList)) {
				List<AuditDetail> details = header.getAuditDetailMap().get("AssetClassificationDetail");
				details = processingAssetDetailList(details, TableType.MAIN_TAB, header);
				auditDetailsList.addAll(details);
			}
		}

		getAssetClassificationHeaderDAO().delete(header, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(deleteChilds(header, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(header);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getAssetClassificationHeaderDAO().delete with parameters
	 * assetClassificationHeader,"_Temp" 3) Audit the record in to AuditHeader and AdtASSET_CLASSIFICATION_HDR by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		AssetClassificationHeader header = (AssetClassificationHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// List
		auditHeader.setAuditDetails(deleteChilds(header, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
		getAssetClassificationHeaderDAO().delete(header, TableType.TEMP_TAB);

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

	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		AssetClassificationHeader assetClassificationHeader = (AssetClassificationHeader) auditHeader.getAuditDetail()
				.getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (assetClassificationHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// AssetClassification Details
		List<AssetClassificationDetail> detailsList = assetClassificationHeader.getAssetClassificationDetailList();
		if (CollectionUtils.isNotEmpty(detailsList)) {
			auditDetailMap.put("AssetClassificationDetail",
					setAssetClassificationAdtDetails(detailsList, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("AssetClassificationDetail"));
		}

		assetClassificationHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(assetClassificationHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private List<AuditDetail> setAssetClassificationAdtDetails(List<AssetClassificationDetail> detailsList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AssetClassificationDetail());

		for (int i = 0; i < detailsList.size(); i++) {
			AssetClassificationDetail assetDetail = detailsList.get(i);

			if (StringUtils.isEmpty(assetDetail.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				assetDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				assetDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				assetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				assetDetail.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| assetDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], assetDetail.getBefImage(),
					assetDetail));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getAssetClassificationHeaderDAO().getErrorDetail with Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		// Get the model object.
		AssetClassificationHeader header = (AssetClassificationHeader) auditDetail.getModelData();

		// Check the unique keys.
		Long templateId = header.getNpaTemplateId();
		int stageOrder = header.getStageOrder();
		if (header.isNew() && assetClassificationHeaderDAO.isDuplicateKey(header.getId(), header.getCode(), stageOrder,
				templateId, header.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Code") + ": " + header.getCode();
			parameters[1] = PennantJavaUtil.getLabel("label_StageOrder") + ": " + stageOrder;

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		//Stage Order Unique Key Checking
		if (header.isNew() && header.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			if (header.isNew()
					&& assetClassificationHeaderDAO.isStageOrderExists(stageOrder, templateId, TableType.VIEW)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_StageOrder") + ": " + stageOrder;
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
		}

		//Asset Code Unique Key Checking
		if (header.isNew() && header.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			if (header.isNew() && assetClassificationHeaderDAO.isAssetCodeExists(header.getCode(), TableType.VIEW)) {
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_AssetCode") + ": " + header.getCode();
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
			}
		}

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, header.getRecordType())
				|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(header.getRecordType())) {
			List<AssetClassificationDetail> detailList = header.getAssetClassificationDetailList();
			for (AssetClassificationDetail detail : detailList) {
				// DEPENDENCY Validation
				String[] parameters = new String[1];
				parameters[0] = PennantJavaUtil.getLabel("label_FinType") + ": " + detail.getFinType();

				int count = assetClassificationHeaderDAO.getCountByFinType(detail.getFinType(), TableType.VIEW);
				if (count > 0) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", parameters, null));
				}
			}

		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private List<AuditDetail> processingAssetDetailList(List<AuditDetail> auditDetails, TableType tableType,
			AssetClassificationHeader header) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			AssetClassificationDetail detail = (AssetClassificationDetail) auditDetails.get(i).getModelData();
			detail.setHeaderId(header.getId());

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(tableType.getSuffix())) {
				approveRec = true;
				detail.setRoleCode("");
				detail.setNextRoleCode("");
				detail.setTaskId("");
				detail.setNextTaskId("");
			}

			detail.setWorkflowId(0);

			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (detail.isNewRecord()) {
				saveRecord = true;
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (detail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = detail.getRecordType();
				recordStatus = detail.getRecordStatus();
				detail.setRecordType("");
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getAssetClassificationHeaderDAO().saveFinType(detail, tableType);
			}

			if (updateRecord) {
				getAssetClassificationHeaderDAO().updateFinType(detail, tableType);
			}

			if (deleteRecord) {
				getAssetClassificationHeaderDAO().deleteFinType(detail, tableType);
			}

			if (approveRec) {
				detail.setRecordType(rcdType);
				detail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(detail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ASSET_CLASSIFICATION_HDR by using ASSET_CLASSIFICATION_HDRDAO's delete method with type as Blank 3) Audit the
	 * record in to AuditHeader and AdtASSET_CLASSIFICATION_HDR by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
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

			if (object instanceof AssetClassificationDetail) {
				rcdType = ((AssetClassificationDetail) object).getRecordType();
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

	public List<AuditDetail> deleteChilds(AssetClassificationHeader assetClassificationHeader, TableType tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		List<AuditDetail> auditDetails = assetClassificationHeader.getAuditDetailMap().get("AssetClassificationDetail");
		AssetClassificationDetail assetDetails = new AssetClassificationDetail();
		AssetClassificationDetail detail = null;
		if (auditDetails != null && auditDetails.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(assetDetails, assetDetails.getExcludeFields());
			for (int i = 0; i < auditDetails.size(); i++) {
				detail = (AssetClassificationDetail) auditDetails.get(i).getModelData();
				detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList
						.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
			}
			getAssetClassificationHeaderDAO().deleteFinTypeList(detail.getHeaderId(), tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

}