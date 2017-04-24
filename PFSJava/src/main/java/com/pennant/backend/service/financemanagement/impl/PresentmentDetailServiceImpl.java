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
 * FileName    		:  PresentmentDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.financemanagement.impl;

import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetailHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PresentmentDetail</b>.<br>
 */
public class PresentmentDetailServiceImpl extends GenericService<PresentmentDetail> implements PresentmentDetailService {
	private final static Logger logger = Logger.getLogger(PresentmentDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PresentmentDetailDAO presentmentDetailDAO;

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
	 * @return the presentmentDetailDAO
	 */
	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	/**
	 * @param presentmentDetailDAO
	 *            the presentmentDetailDAO to set
	 */
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * PresentmentDetails/PresentmentDetails_Temp by using PresentmentDetailsDAO's save method b) Update the Record in
	 * the table. based on the module workFlow Configuration. by using PresentmentDetailsDAO's update method 3) Audit
	 * the record in to AuditHeader and AdtPresentmentDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		PresentmentDetail presentmentDetail = (PresentmentDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (presentmentDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (presentmentDetail.isNew()) {
			presentmentDetail.setId(Long.parseLong(getPresentmentDetailDAO().save(presentmentDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(presentmentDetail);
			auditHeader.setAuditReference(String.valueOf(presentmentDetail.getPresentmentID()));
		} else {
			getPresentmentDetailDAO().update(presentmentDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PresentmentDetails by using PresentmentDetailsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtPresentmentDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		PresentmentDetail presentmentDetail = (PresentmentDetail) auditHeader.getAuditDetail().getModelData();
		getPresentmentDetailDAO().delete(presentmentDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPresentmentDetails fetch the details by using PresentmentDetailsDAO's getPresentmentDetailsById method.
	 * 
	 * @param detailID
	 *            detailID of the PresentmentDetail.
	 * @return PresentmentDetails
	 */
	@Override
	public PresentmentDetail getPresentmentDetail(long detailID) {
		return getPresentmentDetailDAO().getPresentmentDetail(detailID, "_View");
	}

	/**
	 * getApprovedPresentmentDetailsById fetch the details by using PresentmentDetailsDAO's getPresentmentDetailsById
	 * method . with parameter id and type as blank. it fetches the approved records from the PresentmentDetails.
	 * 
	 * @param detailID
	 *            detailID of the PresentmentDetail. (String)
	 * @return PresentmentDetails
	 */
	public PresentmentDetail getApprovedPresentmentDetail(long detailID) {
		return getPresentmentDetailDAO().getPresentmentDetail(detailID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPresentmentDetailDAO().delete with
	 * parameters presentmentDetail,"" b) NEW Add new record in to main table by using getPresentmentDetailDAO().save
	 * with parameters presentmentDetail,"" c) EDIT Update record in the main table by using
	 * getPresentmentDetailDAO().update with parameters presentmentDetail,"" 3) Delete the record from the workFlow
	 * table by using getPresentmentDetailDAO().delete with parameters presentmentDetail,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtPresentmentDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtPresentmentDetails by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
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

		PresentmentDetail presentmentDetail = new PresentmentDetail();
		BeanUtils.copyProperties((PresentmentDetail) auditHeader.getAuditDetail().getModelData(), presentmentDetail);

		getPresentmentDetailDAO().delete(presentmentDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(presentmentDetail.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(presentmentDetailDAO.getPresentmentDetail(presentmentDetail.getPresentmentID(), ""));
		}

		if (presentmentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPresentmentDetailDAO().delete(presentmentDetail, TableType.MAIN_TAB);
		} else {
			presentmentDetail.setRoleCode("");
			presentmentDetail.setNextRoleCode("");
			presentmentDetail.setTaskId("");
			presentmentDetail.setNextTaskId("");
			presentmentDetail.setWorkflowId(0);

			if (presentmentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				presentmentDetail.setRecordType("");
				getPresentmentDetailDAO().save(presentmentDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				presentmentDetail.setRecordType("");
				getPresentmentDetailDAO().update(presentmentDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(presentmentDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPresentmentDetailDAO().delete with parameters presentmentDetail,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtPresentmentDetails by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		PresentmentDetail presentmentDetail = (PresentmentDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPresentmentDetailDAO().delete(presentmentDetail, TableType.TEMP_TAB);

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
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPresentmentDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/* processPresentmentDetails */
	@Override
	public String processPresentmentDetails(PresentmentDetailHeader detailHeader) throws Exception {
		try {
			ResultSet resultSet = getPresentmentDetailDAO().getPresentmentDetails(detailHeader);

			if (resultSet != null) {
				resultSet.last();
				resultSet.beforeFirst();
				if (resultSet.getRow() <= 0) {
					return " No records are available to extract, please change the search criteria.";
				}
			}
			long reference = getPresentmentDetailDAO().getPresentmentDetailRef("SeqPresentmentDetailRef");
			String strReference = StringUtils.leftPad(String.valueOf(reference), 10, "0");
			strReference = "PRE".concat(strReference);
			detailHeader.setExtractId(reference);
			detailHeader.setExtractReference(strReference);
			getPresentmentDetailDAO().savePresentmentHeaderDetails(detailHeader);

			while (resultSet.next()) {
				PresentmentDetail pDetail = new PresentmentDetail();
				pDetail.setDetailID(reference);
                doCalculations(pDetail);
			}

		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	private void doCalculations(PresentmentDetail pDetail) {
		// TODO Auto-generated method stub
		
	}
	 
}