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
 * FileName    		:  IRRFeeTypeService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
 * FileName    		:  IRRCodeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.IRRCodeDAO;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.IRRCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>IRRCode</b>.<br>
 */
public class IRRCodeServiceImpl extends GenericService<IRRCode> implements IRRCodeService {
	private static final Logger logger = Logger.getLogger(IRRCodeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private IRRCodeDAO iRRCodeDAO;
	private IRRFeeTypeDAO iRRFeeTypeDAO;
	private IRRFeeTypeValidation iRRFeeTypeValidation;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public IRRCodeDAO getIRRCodeDAO() {
		return iRRCodeDAO;
	}

	public void setIRRCodeDAO(IRRCodeDAO iRRCodeDAO) {
		this.iRRCodeDAO = iRRCodeDAO;
	}

	public IRRFeeTypeDAO getiRRFeeTypeDAO() {
		return iRRFeeTypeDAO;
	}

	public void setiRRFeeTypeDAO(IRRFeeTypeDAO iRRFeeTypeDAO) {
		this.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

	public IRRFeeTypeValidation getiRRFeeTypeValidation() {
		if (iRRFeeTypeValidation == null) {
			this.iRRFeeTypeValidation = new IRRFeeTypeValidation(iRRFeeTypeDAO);
		}
		return iRRFeeTypeValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * IRRCodes/IRRCodes_Temp by using IRRCodesDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using
	 * IRRCodesDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtIRRCodes by using auditHeaderDAO.addAudit(auditHeader)
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
		IRRCode iRRCode = (IRRCode) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;
		if (iRRCode.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (iRRCode.isNew()) {
			iRRCode.setId(Long.parseLong(getIRRCodeDAO().save(iRRCode, tableType)));
			auditHeader.getAuditDetail().setModelData(iRRCode);
			auditHeader.setAuditReference(String.valueOf(iRRCode.getIRRID()));
		} else {
			getIRRCodeDAO().update(iRRCode, tableType);
		}

		// IRRFeeType Details Processing
		if (iRRCode.getIrrFeeTypesList() != null && !iRRCode.getIrrFeeTypesList().isEmpty()) {
			List<AuditDetail> details = iRRCode.getAuditDetailMap().get("IRRFeeType");
			details = processingIRRFeeTypeList(details, iRRCode.getIRRID(), tableType);
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table IRRCodes by using IRRCodesDAO's delete method with type as Blank 3)
	 * Audit the record in to AuditHeader and AdtIRRCodes by using
	 * auditHeaderDAO.addAudit(auditHeader)
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

		IRRCode iRRCode = (IRRCode) auditHeader.getAuditDetail().getModelData();
		getIRRCodeDAO().delete(iRRCode, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getIRRCodes fetch the details by using IRRCodesDAO's getIRRCodesById
	 * method.
	 * 
	 * @param iRRID
	 *            iRRID of the IRRCode.
	 * @return IRRCodes
	 */
	@Override
	public IRRCode getIRRCode(long iRRID) {
		IRRCode irrCode = getIRRCodeDAO().getIRRCode(iRRID, "_View");
		if (irrCode != null) {
			irrCode.setIrrFeeTypesList(getiRRFeeTypeDAO().getIRRFeeTypeList(iRRID, "_View"));
		}
		return irrCode;
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

		IRRCode irrCode = (IRRCode) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (irrCode.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// IRRCode details
		if (irrCode.getIrrFeeTypesList() != null && irrCode.getIrrFeeTypesList().size() > 0) {
			auditDetailMap.put("IRRFeeType", setIRRFeeTypeAuditData(irrCode, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("IRRFeeType"));
		}

		irrCode.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(irrCode);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List IRRCode of Audit Details with detailed fields
	 * 
	 * @param irrCode
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setIRRFeeTypeAuditData(IRRCode irrCode, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		IRRFeeType feeType = new IRRFeeType();
		String[] fields = PennantJavaUtil.getFieldDetails(feeType, feeType.getExcludeFields());

		for (int i = 0; i < irrCode.getIrrFeeTypesList().size(); i++) {

			IRRFeeType irrFeeType = irrCode.getIrrFeeTypesList().get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(irrFeeType.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (irrCode.isWorkflow()) {
					isRcdType = true;
				}
			} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				irrFeeType.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], irrFeeType.getBefImage(), irrFeeType));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * getApprovedIRRCodesById fetch the details by using IRRCodesDAO's
	 * getIRRCodesById method . with parameter id and type as blank. it fetches
	 * the approved records from the IRRCodes.
	 * 
	 * @param iRRID
	 *            iRRID of the IRRCode. (String)
	 * @return IRRCodes
	 */
	public IRRCode getApprovedIRRCode(long iRRID) {
		IRRCode irrCode = getIRRCodeDAO().getIRRCode(iRRID, "_AView");
		if (irrCode != null) {
			irrCode.setIrrFeeTypesList(getiRRFeeTypeDAO().getIRRFeeTypeList(iRRID, "_AView"));
		}
		return irrCode;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getIRRCodeDAO().delete with parameters iRRCode,"" b) NEW Add new
	 * record in to main table by using getIRRCodeDAO().save with parameters
	 * iRRCode,"" c) EDIT Update record in the main table by using
	 * getIRRCodeDAO().update with parameters iRRCode,"" 3) Delete the record
	 * from the workFlow table by using getIRRCodeDAO().delete with parameters
	 * iRRCode,"_Temp" 4) Audit the record in to AuditHeader and AdtIRRCodes by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtIRRCodes by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		IRRCode iRRCode = new IRRCode();
		BeanUtils.copyProperties((IRRCode) auditHeader.getAuditDetail().getModelData(), iRRCode);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(iRRCode.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(iRRCodeDAO.getIRRCode(iRRCode.getIRRID(), ""));
		}

		if (iRRCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			// IRRFeeType Details
			if (iRRCode.getIrrFeeTypesList() != null && !iRRCode.getIrrFeeTypesList().isEmpty()) {
				List<AuditDetail> details = iRRCode.getAuditDetailMap().get("IRRFeeType");
				details = processingIRRFeeTypeList(details, iRRCode.getIRRID(), TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}

			getIRRCodeDAO().delete(iRRCode, TableType.MAIN_TAB);
		} else {
			iRRCode.setRoleCode("");
			iRRCode.setNextRoleCode("");
			iRRCode.setTaskId("");
			iRRCode.setNextTaskId("");
			iRRCode.setWorkflowId(0);

			if (iRRCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				iRRCode.setRecordType("");
				getIRRCodeDAO().save(iRRCode, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				iRRCode.setRecordType("");
				getIRRCodeDAO().update(iRRCode, TableType.MAIN_TAB);
			}
			// IRRFeeType Details
			if (iRRCode.getIrrFeeTypesList() != null && !iRRCode.getIrrFeeTypesList().isEmpty()) {
				List<AuditDetail> details = iRRCode.getAuditDetailMap().get("IRRFeeType");
				details = processingIRRFeeTypeList(details, iRRCode.getIRRID(), TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}
		}

		getIRRCodeDAO().delete(iRRCode, TableType.TEMP_TAB);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(listDeletion(iRRCode, TableType.TEMP_TAB, PennantConstants.TRAN_WF));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(iRRCode);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getIRRCodeDAO().delete with parameters
	 * iRRCode,"_Temp" 3) Audit the record in to AuditHeader and AdtIRRCodes by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		IRRCode iRRCode = (IRRCode) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(listDeletion(iRRCode, TableType.TEMP_TAB, PennantConstants.TRAN_WF));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getIRRCodeDAO().delete(iRRCode, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
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
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		auditHeader = getAuditDetails(auditHeader, method);

		IRRCode irrCode = (IRRCode) auditDetail.getModelData();
		String usrLanguage = irrCode.getUserDetails().getLanguage();

		// IRRFeeType details Validation
		List<IRRFeeType> irrFeeTypesList = irrCode.getIrrFeeTypesList();
		if (irrFeeTypesList != null && !irrFeeTypesList.isEmpty()) {
			List<AuditDetail> details = irrCode.getAuditDetailMap().get("IRRFeeType");
			details = getiRRFeeTypeValidation().vaildateDetails(details, method, usrLanguage);
			auditDetails.addAll(details);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getIRRCodeDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		IRRCode iRRCode = (IRRCode) auditDetail.getModelData();

		// Check the unique keys.
		if (iRRCode.isNew() && iRRCodeDAO.isDuplicateKey(iRRCode.getIRRID(), iRRCode.getIRRCode(),
				iRRCode.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_IRRCode") + ": " + iRRCode.getIRRCode();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * Method For Preparing List of AuditDetails for IRRFeeType Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingIRRFeeTypeList(List<AuditDetail> auditDetails, long irrID, TableType type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			IRRFeeType irrFeeType = (IRRFeeType) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				irrFeeType.setRoleCode("");
				irrFeeType.setNextRoleCode("");
				irrFeeType.setTaskId("");
				irrFeeType.setNextTaskId("");
			}

			irrFeeType.setWorkflowId(0);
			irrFeeType.setIRRID(irrID);

			if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (irrFeeType.isNewRecord()) {
				saveRecord = true;
				if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (irrFeeType.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (irrFeeType.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = irrFeeType.getRecordType();
				recordStatus = irrFeeType.getRecordStatus();
				irrFeeType.setRecordType("");
				irrFeeType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getiRRFeeTypeDAO().save(irrFeeType, type);
			}

			if (updateRecord) {
				getiRRFeeTypeDAO().update(irrFeeType, type);
			}

			if (deleteRecord) {
				getiRRFeeTypeDAO().delete(irrFeeType, type);
			}

			if (approveRec) {
				irrFeeType.setRecordType(rcdType);
				irrFeeType.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(irrFeeType);
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	// Method for Deleting all records related to IRRCode in _Temp/Main tables
	// depend on method type
	private List<AuditDetail> listDeletion(IRRCode irrCode, TableType tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// IRRFeeType Details
		List<AuditDetail> auditDetails = irrCode.getAuditDetailMap().get("IRRFeeType");
		IRRFeeType aIRRFeeType = new IRRFeeType();
		IRRFeeType irrFeeType = null;
		if (auditDetails != null && auditDetails.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(aIRRFeeType, aIRRFeeType.getExcludeFields());
			for (int i = 0; i < auditDetails.size(); i++) {
				irrFeeType = (IRRFeeType) auditDetails.get(i).getModelData();
				irrFeeType.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], irrFeeType.getBefImage(),
						irrFeeType));
			}
			getiRRFeeTypeDAO().deleteList(irrFeeType, tableType);
		}

		logger.debug("Leaving");
		return auditList;
	}

}