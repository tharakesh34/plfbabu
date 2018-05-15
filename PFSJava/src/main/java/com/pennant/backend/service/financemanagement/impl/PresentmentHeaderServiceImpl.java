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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentHeaderDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.PresentmentProcess;

/**
 * Service implementation for methods that depends on
 * <b>PresentmentHeader</b>.<br>
 */
public class PresentmentHeaderServiceImpl extends GenericService<PresentmentHeader>
		implements PresentmentHeaderService {
	private static final Logger logger = Logger.getLogger(PresentmentHeaderServiceImpl.class);

	private PresentmentHeaderDAO presentmentHeaderDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ReceiptCancellationService receiptCancellationService;
	private ReceiptCalculator receiptCalculator;
	private ReceiptService receiptService;
	private FinanceDetailService financeDetailService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceMainDAO financeMainDAO;
	
	@Autowired(required = false)
	private PresentmentProcess presentmentProcess;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public void setPresentmentHeaderDAO(PresentmentHeaderDAO presentmentHeaderDAO) {
		this.presentmentHeaderDAO = presentmentHeaderDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public RepaymentPostingsUtil getRepaymentPostingsUtil() {
		return repaymentPostingsUtil;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public PresentmentHeader getPresentmentHeader(long id) {
		return this.presentmentHeaderDAO.getPresentmentHeader(id, "_View");
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove,
			String type) {
		return presentmentHeaderDAO.getPresentmentDetailsList(presentmentId, isExclude, isApprove, type);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		presentmentHeaderDAO.updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		presentmentHeaderDAO.updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		presentmentHeaderDAO.updatePresentmentIdAsZero(presentmentId);
	}

	@Override
	public long getSeqNumber(String tableNme) {
		return presentmentHeaderDAO.getSeqNumber(tableNme);
	}

	@Override
	public void updateFinanceDetails(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail detail = presentmentHeaderDAO.getPresentmentDetail(presentmentRef);
		List<FinanceScheduleDetail> list = financeScheduleDetailDAO.getFinScheduleDetails(detail.getFinReference(),
				TableType.MAIN_TAB.getSuffix(), false);
		boolean isFinactive = repaymentPostingsUtil.isSchdFullyPaid(detail.getFinReference(), list);

		if (isFinactive) {
			financeMainDAO.updateMaturity(detail.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

		logger.debug(Literal.LEAVING);
	}

	/* processPresentmentDetails */
	@Override
	public String savePresentmentDetails(PresentmentHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Object, Long> map = new HashMap<Object, Long>();
		long presentmentId = 0;
		ResultSet rs = null;
		List<Object> resultList = null;
		try {
			resultList = presentmentHeaderDAO.getPresentmentDetails(header);
			rs = (ResultSet) resultList.get(0);
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
						.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"))
						.subtract(rs.getBigDecimal("TDSAMOUNT"));
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
				pDetail.setEntityCode(rs.getString("ENTITYCODE"));

				doCalculations(pDetail, header);

				if (pDetail.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}

				Date defSchDate = rs.getDate("DEFSCHDDATE");
				String bankCode = rs.getString("BANKCODE");
				String entity   = rs.getString("ENTITYCODE");
				if (defSchDate != null) {
					if (!map.containsKey(defSchDate) || (!map.containsKey(bankCode) && ImplementationConstants.GROUP_BATCH_BY_BANK ) || !map.containsKey(entity)) {
						header.setSchdate(defSchDate);
						header.setBankCode(bankCode);
						header.setEntityCode(entity);
						presentmentId = savePresentmentHeaderDetails(header);
						map.put(defSchDate, presentmentId);
						map.put(bankCode, presentmentId);
						map.put(entity, presentmentId);
					}
				}
				isEmptyRecords = true;

				// PresentmentDetail saving
				pDetail.setPresentmentId(presentmentId);
				long id = presentmentHeaderDAO.save(pDetail, TableType.MAIN_TAB);

				// FinScheduleDetails update
				if (RepayConstants.PEXC_EMIINCLUDE == pDetail.getExcludeReason()) {
					presentmentHeaderDAO.updateFinScheduleDetails(id, pDetail.getFinReference(), pDetail.getSchDate(),
							pDetail.getSchSeq());
				}
			}

			if (!isEmptyRecords) {
				return PennantJavaUtil.getLabel("label_PresentmentSearchMessage");
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if(resultList != null) {
				PreparedStatement stmt = (PreparedStatement) resultList.get(1);
				if (stmt != null) {
					stmt.close();
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return PennantJavaUtil.getLabel("label_PresentmentExtractedMessage");
	}

	// Saving the Presentment Header Details
	private long savePresentmentHeaderDetails(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);
		return id;

	}

	private void doCalculations(PresentmentDetail presentmentDetail, PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = presentmentDetail.getFinReference();

		// Mandate Rejected
		if (MandateConstants.STATUS_REJECTED.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
			return;
		}

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

		boolean isECSMandate = MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType());
		if (!isECSMandate) {
			if (!MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus())) {
				presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
				return;
			}

			// Mandate Not Approved
			/*
			 * if (
			 * !((MandateConstants.STATUS_APPROVED.equals(presentmentDetail.
			 * getMandateStatus())) ||
			 * (MandateConstants.STATUS_AWAITCON.equals(presentmentDetail.
			 * getMandateStatus())) ||
			 * (MandateConstants.STATUS_NEW.equals(presentmentDetail.
			 * getMandateStatus())))) {
			 * presentmentDetail.setExcludeReason(RepayConstants.
			 * PEXC_MANDATE_NOTAPPROV); return; }
			 */

			// Mandate Expired
			if (presentmentDetail.getMandateExpiryDate() != null && DateUtility
					.compare(presentmentDetail.getDefSchdDate(), presentmentDetail.getMandateExpiryDate()) > 0) {
				presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
				return;
			}
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

	private String getPresentmentRef(ResultSet rs) throws SQLException {
		logger.debug(Literal.ENTERING);

		StringBuilder sb = new StringBuilder();
		sb.append(rs.getString("BRANCHCODE"));
		sb.append(rs.getString("LOANTYPE"));
		sb.append(rs.getString("MANDATETYPE"));

		logger.debug(Literal.LEAVING);
		return sb.toString();
	}

	@Override
	public void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction,
			long presentmentId, long partnerBankId, LoggedInUser userDetails) throws Exception {
		logger.debug(Literal.ENTERING);

		if ("Save".equals(userAction)) {
			if (includeList != null && !includeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, includeList, 0);
			}
			if (excludeList != null && !excludeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, excludeList,
						RepayConstants.PEXC_MANUAL_EXCLUDE);
				// Update Presentment is as 0 in Finschedule details
			}
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
					partnerBankId);
		} else if ("Submit".equals(userAction)) {
			if (includeList != null && !includeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, includeList, 0);
			}
			if (excludeList != null && !excludeList.isEmpty()) {
				this.presentmentHeaderDAO.updatePresentmentDetials(presentmentId, excludeList,
						RepayConstants.PEXC_MANUAL_EXCLUDE);
			}
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_AWAITING_CONF,
					partnerBankId);
		} else if ("Approve".equals(userAction)) {
			processDetails(presentmentId, userDetails);
		} else if ("Resubmit".equals(userAction)) {
			this.presentmentHeaderDAO.updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED,
					partnerBankId);
		} else if ("Cancel".equals(userAction)) {
			List<PresentmentDetail> list = this.presentmentHeaderDAO.getPresentmentDetail(presentmentId, false);
			if (list != null && !list.isEmpty()) {
				for (PresentmentDetail item : list) {
					if (item.getExcessID() != 0) {
						finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
					}
					updatePresentmentIdAsZero(item.getId());
				}
			}
			this.presentmentHeaderDAO.deletePresentmentDetails(presentmentId);
			this.presentmentHeaderDAO.deletePresentmentHeader(presentmentId);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public PresentmentDetail presentmentCancellation(String presentmentRef, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentDetail presentmentDetail = null;
		try {
			presentmentDetail = this.presentmentHeaderDAO.getPresentmentDetail(presentmentRef);
			if (presentmentDetail == null) {
				throw new Exception(PennantJavaUtil.getLabel("label_Presentmentdetails_Notavailable") + presentmentRef);
			}
			// Update Presentment is as 0 in Finschedule details
			updatePresentmentIdAsZero(presentmentDetail.getId());

			presentmentDetail = this.receiptCancellationService.presentmentCancellation(presentmentDetail, returnCode);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

		return presentmentDetail;
	}

	// Processing the presentment details
	public void processDetails(long presentmentId, LoggedInUser userDetails) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Long> idList = new ArrayList<Long>();
		boolean isError = false;

		List<PresentmentDetail> detailList = presentmentHeaderDAO.getPresentmentDetail(presentmentId, true);
		if (detailList != null && !detailList.isEmpty()) {
			for (PresentmentDetail detail : detailList) {
				if (DateUtility.compare(DateUtility.getAppDate(), detail.getSchDate()) >= 0) {
					try {
						idList.add(detail.getId());
						processReceipts(detail, userDetails);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						isError = true;
						throw e;
					}
					
				} else {
					idList.add(detail.getId());
				}
			}

			// Storing the presentment data into bajaj inteface tables
			if (idList != null && !idList.isEmpty()) {
				try {
					presentmentProcess.sendReqest(idList, presentmentId, isError);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					throw e;
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Create a new Receipt
	 * @param detail
	 * @param userDetails
	 * @param header
	 * @throws Exception
	 */
	public void processReceipts(PresentmentDetail detail, LoggedInUser userDetails) throws Exception{

		PresentmentHeader header = presentmentHeaderDAO.getPresentmentHeader(detail.getPresentmentId(), "_Aview");

		AuditHeader auditHeader = doCreateReceipts(detail, userDetails, header);

			FinReceiptData finReceipt = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
			long receiptId = finReceipt.getReceiptHeader().getReceiptID();
			if (receiptId == 0 || receiptId == Long.MIN_VALUE) {
				if (!auditHeader.isNextProcess()) {
					String errMsg = getErrorMsg(auditHeader);
					throw new Exception(errMsg);
				} else {
					throw new Exception(PennantJavaUtil.getLabel("label_FinReceiptHeader_Not_Created"));
				}
			}
			presentmentHeaderDAO.updateReceptId(detail.getId(), receiptId);
	}

	// Creating the receipts If Schedule data is lessthan or equal to
	// Application date.
	private AuditHeader doCreateReceipts(PresentmentDetail detail, LoggedInUser userDetails, PresentmentHeader header)
			throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			// FinanceDetail
			FinanceDetail financeDetail = new FinanceDetail();
			FinScheduleData finScheduleData = financeDetailService.getFinSchDataForReceipt(detail.getFinReference(),
					TableType.MAIN_TAB.getSuffix());

			finScheduleData.getFinanceMain().setRecordType("");
			finScheduleData.getFinanceMain().setVersion(finScheduleData.getFinanceMain().getVersion() + 1);
			if (finScheduleData.getFinFeeDetailList() != null) {
				for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
					if (StringUtils.equals(finFeeDetail.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
						finFeeDetail.setRecordType("");
					}
				}
			}
			financeDetail.setFinScheduleData(finScheduleData);
			FinReceiptData finReceiptData = new FinReceiptData();
			FinReceiptHeader receiptHeader = new FinReceiptHeader();
			receiptHeader.setReference(detail.getFinReference());
			receiptHeader.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EMIINADV);
			receiptHeader.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			receiptHeader.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
			receiptHeader.setReceiptDate(detail.getSchDate());
			receiptHeader.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
			receiptHeader.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
			receiptHeader.setReceiptAmount(detail.getPresentmentAmt());
			receiptHeader.setReceiptMode(RepayConstants.PAYTYPE_PRESENTMENT);
			receiptHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			receiptHeader.setNewRecord(true);
			receiptHeader.setLastMntBy(userDetails.getUserId());
			receiptHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			receiptHeader.setUserDetails(userDetails);

			FinReceiptDetail finReceiptDetail = new FinReceiptDetail();
			finReceiptDetail.setReceivedDate(detail.getSchDate());
			finReceiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
			finReceiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
			finReceiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
			finReceiptDetail.setAmount(detail.getPresentmentAmt());
			finReceiptDetail.setPartnerBankAc(header.getPartnerAcctNumber());
			finReceiptDetail.setPartnerBankAcType(header.getPartnerAcctType());
			receiptHeader.getReceiptDetails().add(finReceiptDetail);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			finReceiptData.setReceiptHeader(receiptHeader);
			finReceiptData.setFinanceDetail(financeDetail);
			finReceiptData.setFinReference(financeMain.getFinReference());
			finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

			// calculate allocations
			Map<String, BigDecimal> allocationMap = receiptCalculator.recalAutoAllocation(
					financeDetail.getFinScheduleData(), detail.getPresentmentAmt(), detail.getSchDate(),
					receiptHeader.getReceiptPurpose(), true);
			finReceiptData.setAllocationMap(allocationMap);

			finReceiptData = receiptService.calculateRepayments(finReceiptData, true);
			AuditHeader auditHeader = getAuditHeader(finReceiptData, PennantConstants.TRAN_WF);
			auditHeader = receiptService.doApprove(auditHeader);
			logger.debug(Literal.LEAVING);

			return auditHeader;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private String getErrorMsg(AuditHeader auditHeader) {
		String msg = "";
		if (auditHeader.getOverideMessage() != null && auditHeader.getOverideMessage().size() > 0) {
			for (ErrorDetail errorDetail : auditHeader.getOverideMessage()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}

		if (auditHeader.getAuditDetail().getErrorDetails() != null) {
			for (ErrorDetail errorDetail : auditHeader.getAuditDetail().getErrorDetails()) {
				return msg = msg.concat(errorDetail.getError());
			}
		}
		return msg;
	}

	private AuditHeader getAuditHeader(FinReceiptData repayData, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, repayData);
		return new AuditHeader(repayData.getFinReference(), null, null, null, auditDetail,
				repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

}