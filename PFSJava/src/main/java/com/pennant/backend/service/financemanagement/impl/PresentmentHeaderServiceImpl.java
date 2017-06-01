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
 * FileName    		:  PresentmentHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.PresentmentHeaderDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.bajaj.process.PresentmentRequest;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.DateUtil;

/**
 * Service implementation for methods that depends on <b>PresentmentHeader</b>.<br>
 */
public class PresentmentHeaderServiceImpl extends GenericService<PresentmentHeader> implements PresentmentHeaderService {
	private final static Logger logger = Logger.getLogger(PresentmentHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PresentmentHeaderDAO presentmentHeaderDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private DataSource dataSource;
	private ReceiptCancellationService receiptCancellationService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * PresentmentHeader/PresentmentHeader_Temp by using PresentmentHeaderDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using PresentmentHeaderDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader)
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

		PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (presentmentHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (presentmentHeader.isNew()) {
			presentmentHeader.setId(Long.parseLong(getPresentmentHeaderDAO().save(presentmentHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(presentmentHeader);
			auditHeader.setAuditReference(String.valueOf(presentmentHeader.getId()));
		} else {
			getPresentmentHeaderDAO().update(presentmentHeader, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PresentmentHeaderDAO getPresentmentHeaderDAO() {
		return presentmentHeaderDAO;
	}

	public void setPresentmentHeaderDAO(PresentmentHeaderDAO presentmentHeaderDAO) {
		this.presentmentHeaderDAO = presentmentHeaderDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PresentmentHeader by using PresentmentHeaderDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader)
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

		PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();
		getPresentmentHeaderDAO().delete(presentmentHeader, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPresentmentHeader fetch the details by using PresentmentHeaderDAO's getPresentmentHeaderById method.
	 * 
	 * @param id
	 *            id of the PresentmentHeader.
	 * @return PresentmentHeader
	 */
	@Override
	public PresentmentHeader getPresentmentHeader(long id) {
		return getPresentmentHeaderDAO().getPresentmentHeader(id, "_View");
	}

	/**
	 * getApprovedPresentmentHeaderById fetch the details by using PresentmentHeaderDAO's getPresentmentHeaderById
	 * method . with parameter id and type as blank. it fetches the approved records from the PresentmentHeader.
	 * 
	 * @param id
	 *            id of the PresentmentHeader. (String)
	 * @return PresentmentHeader
	 */
	public PresentmentHeader getApprovedPresentmentHeader(long id) {
		return getPresentmentHeaderDAO().getPresentmentHeader(id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPresentmentHeaderDAO().delete with
	 * parameters presentmentHeader,"" b) NEW Add new record in to main table by using getPresentmentHeaderDAO().save
	 * with parameters presentmentHeader,"" c) EDIT Update record in the main table by using
	 * getPresentmentHeaderDAO().update with parameters presentmentHeader,"" 3) Delete the record from the workFlow
	 * table by using getPresentmentHeaderDAO().delete with parameters presentmentHeader,"_Temp" 4) Audit the record in
	 * to AuditHeader and AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader) based on the
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

		PresentmentHeader presentmentHeader = new PresentmentHeader();
		BeanUtils.copyProperties((PresentmentHeader) auditHeader.getAuditDetail().getModelData(), presentmentHeader);

		getPresentmentHeaderDAO().delete(presentmentHeader, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(presentmentHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					presentmentHeaderDAO.getPresentmentHeader(presentmentHeader.getId(), ""));
		}

		if (presentmentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPresentmentHeaderDAO().delete(presentmentHeader, TableType.MAIN_TAB);
		} else {
			presentmentHeader.setRoleCode("");
			presentmentHeader.setNextRoleCode("");
			presentmentHeader.setTaskId("");
			presentmentHeader.setNextTaskId("");
			presentmentHeader.setWorkflowId(0);

			if (presentmentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				presentmentHeader.setRecordType("");
				getPresentmentHeaderDAO().save(presentmentHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				presentmentHeader.setRecordType("");
				getPresentmentHeaderDAO().update(presentmentHeader, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(presentmentHeader);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPresentmentHeaderDAO().delete with parameters presentmentHeader,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtPresentmentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		PresentmentHeader presentmentHeader = (PresentmentHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPresentmentHeaderDAO().delete(presentmentHeader, TableType.TEMP_TAB);

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
	 * from getPresentmentHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		PresentmentHeader presentmentHeader = (PresentmentHeader) auditDetail.getModelData();

		// Check the unique keys.
		if (presentmentHeader.isNew()
				&& presentmentHeaderDAO.isDuplicateKey(presentmentHeader.getId(), presentmentHeader.getReference(),
						presentmentHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[2];

			parameters[0] = PennantJavaUtil.getLabel("label_Reference") + ": " + presentmentHeader.getReference();

			auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader presentmentHeader) {
		return presentmentHeaderDAO.savePresentmentHeader(presentmentHeader);
	}

	@Override
	public void updatePresentmentDetails(long presentmentId, List<Long> detaildList) throws Exception {
		presentmentHeaderDAO.updatePresentmentDetailId(presentmentId, detaildList);
	}

	@Override
	public void updatePresentmentDetailHeader(long presentmentId, long extractId) {
		presentmentHeaderDAO.updatePresentmentDetailId(presentmentId, extractId);
	}

	/* processPresentmentDetails */
	@Override
	public String savePresentmentDetails(PresentmentHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Date, Long> map = new HashMap<Date, Long>();
		long presentmentId = 0;
		try {
			ResultSet rs = presentmentHeaderDAO.getPresentmentDetails(header);
			while (rs.next()) {

				PresentmentDetail pDetail = new PresentmentDetail();
				pDetail.setPresentmentAmt(BigDecimal.ZERO);
				pDetail.setStatus(RepayConstants.PEXC_IMPORT);
				pDetail.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
				pDetail.setPresentmentRef(getPresentmentRef(rs));

				// Schedule Setup
				pDetail.setFinReference(rs.getString("FINREFERENCE"));
				pDetail.setSchDate(rs.getDate("SCHDATE"));
				pDetail.setEmiNo(rs.getInt("EMINO"));
				pDetail.setSchSeq(rs.getInt("SCHSEQ"));
				pDetail.setDefSchdDate(rs.getDate("DEFSCHDDATE"));

				BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
						.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
						.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID")).subtract(rs.getBigDecimal("TDSAMOUNT"));
				if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
					continue;
				}

				pDetail.setSchAmtDue(schAmtDue);
				pDetail.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
				pDetail.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
				pDetail.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
				pDetail.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
				pDetail.setSchInsDue(BigDecimal.ZERO);
				pDetail.setSchPenaltyDue(BigDecimal.ZERO);
				pDetail.setAdvanceAmt(schAmtDue);
				pDetail.setAdviseAmt(BigDecimal.ZERO);
				pDetail.setExcessID(0);
				pDetail.setReceiptID(0);

				// Mandate Details
				pDetail.setMandateId(rs.getLong("MANDATEID"));
				pDetail.setMandateExpiryDate(rs.getDate("EXPIRYDATE"));
				pDetail.setMandateStatus(rs.getString("STATUS"));
				pDetail.setMandateType(rs.getString("MANDATETYPE"));

				pDetail.setVersion(0);
				pDetail.setLastMntBy(header.getLastMntBy());
				pDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pDetail.setWorkflowId(0);

				doCalculations(pDetail, header);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}
				
				Date defSchDate = rs.getDate("DEFSCHDDATE");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate)) {
						header.setSchdate(defSchDate);
						presentmentId = savePresentmentHeaderDetails(header);
						map.put(defSchDate, presentmentId);
					}
				}
				isEmptyRecords = true;
				
				//PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentHeaderDAO.save(pDetail, TableType.MAIN_TAB);
				
				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentHeaderDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(), pDetail.getSchSeq());
				}
			}

			if (!isEmptyRecords) {
				return " No records are available to extract, please change the search criteria.";
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
		return " Extracted successfully.";
	}

	private long savePresentmentHeaderDetails(PresentmentHeader header) {

		long id = presentmentHeaderDAO.getSeqNumber("SeqPresentmentHeader");
		String reference = StringUtils.leftPad(String.valueOf(id), 15, "0");
		header.setId(id);
		header.setStatus(RepayConstants.PEXC_EXTRACT);
		header.setPresentmentDate(DateUtility.getSysDate());
		header.setReference(header.getMandateType().concat(reference));
		header.setdBStatusId(0);
		header.setImportStatusId(0);
		header.setTotalRecords(0);
		header.setProcessedRecords(0);
		header.setSuccessRecords(0);
		header.setFailedRecords(0);
		presentmentHeaderDAO.savePresentmentHeader(header);
		return id;

	}

	private void doCalculations(PresentmentDetail presentmentDetail, PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = presentmentDetail.getFinReference();

		// EMI HOLD
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), detailHeader.getToDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		// Mandate Hold
		if (MandateConstants.STATUS_HOLD.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
			return;
		}

		if (!MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType()) && !MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
			return;
		}

		// Mandate Not Approved
		if (!MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType())
				&& !((MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus()))
						|| (MandateConstants.STATUS_AWAITCON.equals(presentmentDetail.getMandateStatus())) || (MandateConstants.STATUS_NEW
							.equals(presentmentDetail.getMandateStatus())))) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
			return;
		}

		// Mandate Expired
		if (presentmentDetail.getMandateExpiryDate() != null && 
				DateUtility.compare(presentmentDetail.getDefSchdDate(), presentmentDetail.getMandateExpiryDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (finExcessAmount != null) {
			emiInAdvanceAmt = finExcessAmount.getBalanceAmt();
			presentmentDetail.setExcessID(finExcessAmount.getExcessID());
		} else {
			emiInAdvanceAmt = BigDecimal.ZERO;
		}

		if (emiInAdvanceAmt.compareTo(presentmentDetail.getSchAmtDue()) >= 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
			presentmentDetail.setPresentmentAmt(BigDecimal.ZERO);
			presentmentDetail.setAdvanceAmt(presentmentDetail.getSchAmtDue());
		} else {
			presentmentDetail.setPresentmentAmt(presentmentDetail.getSchAmtDue().subtract(emiInAdvanceAmt));
			presentmentDetail.setAdvanceAmt(emiInAdvanceAmt);
		}

		logger.debug(Literal.LEAVING);
	}


	public void processDetails(long presentmentId) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			PresentmentRequest presentmentRequest = new PresentmentRequest(dataSource, App.DATABASE.name(), 1000, DateUtil.getSysDate(),true);
			presentmentRequest.setPresentmentId(presentmentId);
			presentmentRequest.process("PRESENTMENT_REQUEST");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, String type) {
		return presentmentHeaderDAO.getPresentmentDetailsList(presentmentId, isExclude, type);
	}

	private String getPresentmentRef(ResultSet rs) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(rs.getString("BRANCHCODE"));
		sb.append(rs.getString("LOANTYPE"));
		sb.append(rs.getString("MANDATETYPE"));
		return sb.toString();
	}

	@Override
	public void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction, long presentmentId, long partnerBankId) throws Exception {

		if ("Save".equals(userAction)) {
			if (includeList != null && !includeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, includeList, 0);
			}
			if (excludeList != null && !excludeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, excludeList, RepayConstants.PEXC_MANUAL_EXCLUDE);
			}
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED, partnerBankId);
		} else if ("Submit".equals(userAction)) {
			if (includeList != null && !includeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, includeList, 0);
			}
			if (excludeList != null && !excludeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, excludeList, RepayConstants.PEXC_MANUAL_EXCLUDE);
			}
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_AWAITING_CONF, partnerBankId);
		} else if ("Approve".equals(userAction)) {
			processDetails(presentmentId);
		} else if ("Resubmit".equals(userAction)) {
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED, partnerBankId);
		} else if ("Cancel".equals(userAction)) {
			List<PresentmentDetail> list = this.presentmentHeaderDAO.getPresentmentDetail(presentmentId);
			if (list != null && !list.isEmpty()) {
				for (PresentmentDetail item : list) {
					if (item.getExcessID() != 0) {
						finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
					}
				}
			}
			this.presentmentHeaderDAO.deletePresentmentDetails(presentmentId);
			this.presentmentHeaderDAO.deletePresentmentHeader(presentmentId);
		}
	}


	@Override
	public PresentmentDetail presentmentCancellation(String presentmentRef, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);
		PresentmentDetail presentmentDetail = null;
		try {
			presentmentDetail = this.presentmentHeaderDAO.getPresentmentDetail(presentmentRef);
			if (presentmentDetail == null) {
				throw new Exception(" Presentment details are not available for the presentment reference :" + presentmentRef);
			}
			presentmentDetail = this.receiptCancellationService.presentmentCancellation(presentmentDetail, returnCode);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
		return presentmentDetail;
	}

	@Override
	public Date getMaxSchdPresentment(String finReference) {
		return getPresentmentHeaderDAO().getMaxSchdPresentment(finReference);
	}
	
}