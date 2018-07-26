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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
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
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.PresentmentRequest;

/**
 * Service implementation for methods that depends on
 * <b>PresentmentHeader</b>.<br>
 */
public class PresentmentDetailServiceImpl extends GenericService<PresentmentHeader> implements PresentmentDetailService {
	private static final Logger logger = Logger.getLogger(PresentmentDetailServiceImpl.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ReceiptCancellationService receiptCancellationService;
	private ReceiptCalculator receiptCalculator;
	private ReceiptService receiptService;
	private FinanceDetailService financeDetailService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private FinanceMainDAO financeMainDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	
	@Autowired(required = false)
	private PresentmentRequest presentmentRequest;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	 
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
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

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}
	
	public ChequeDetailDAO getChequeDetailDAO() {
		return chequeDetailDAO;
	}

	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Override
	public PresentmentHeader getPresentmentHeader(long id) {
		return this.getPresentmentDetailDAO().getPresentmentHeader(id, "_View");
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, boolean isApprove, String type) {
		return getPresentmentDetailDAO().getPresentmentDetailsList(presentmentId, isExclude, isApprove, type);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, long bounceId, long manualAdviseId,
			String errorDesc) {
		getPresentmentDetailDAO().updatePresentmentDetails(presentmentRef, status, bounceId, manualAdviseId, errorDesc);
	}

	@Override
	public void updatePresentmentDetails(String presentmentRef, String status, String errorCode, String errorDesc) {
		getPresentmentDetailDAO().updatePresentmentDetails(presentmentRef, status, errorCode, errorDesc);
	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		getPresentmentDetailDAO().updatePresentmentIdAsZero(presentmentId);
	}

	@Override
	public long getSeqNumber(String tableNme) {
		return getPresentmentDetailDAO().getSeqNumber(tableNme);
	}

	@Override
	public String getPaymenyMode(String presentmentRef) {
		return getPresentmentDetailDAO().getPaymenyMode(presentmentRef);
	}

	
	@Override
	public void updateFinanceDetails(String presentmentRef) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail detail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef, TableType.MAIN_TAB.getSuffix());
		List<FinanceScheduleDetail> list = financeScheduleDetailDAO.getFinScheduleDetails(detail.getFinReference(), TableType.MAIN_TAB.getSuffix(), false);
		boolean isFinactive = repaymentPostingsUtil.isSchdFullyPaid(detail.getFinReference(), list);

		if (isFinactive) {
			financeMainDAO.updateMaturity(detail.getFinReference(), FinanceConstants.CLOSE_STATUS_MATURED, false);
		}
		 
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public PresentmentDetail getPresentmentDetailsByMode(String presentmentRef, String paymentMode) {
		PresentmentDetail presentmentDetail;
		if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
			presentmentDetail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef, "_PDCview");
		} else {
			presentmentDetail = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentRef, "_View");
		}
		return presentmentDetail;
	}
	
	private void updateChequeStatus(long chequeDetailsId, String status) {
		chequeDetailDAO.updateChequeStatus(chequeDetailsId, status);
	}
	
	@Override
	public void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction, long presentmentId, long partnerBankId, LoggedInUser userDetails,boolean isPDC) throws Exception {
		logger.debug(Literal.ENTERING);

		if ("Save".equals(userAction)) {
			savePresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Submit".equals(userAction)) {
			submitPresentments(excludeList, includeList, presentmentId, partnerBankId);
		} else if ("Approve".equals(userAction)) {
			approvePresentments(presentmentId, userDetails, isPDC);
		} else if ("Resubmit".equals(userAction)) {
			resubmitPresentments(presentmentId, partnerBankId);
		} else if ("Cancel".equals(userAction)) {
			cancelPresentments(presentmentId);
		}
		logger.debug(Literal.LEAVING);
	}

	private void savePresentments(List<Long> excludeList, List<Long> includeList, long presentmentId, long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, excludeList, RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED, partnerBankId);
	}
	
	private void submitPresentments(List<Long> excludeList, List<Long> includeList, long presentmentId, long partnerBankId) {
		if (includeList != null && !includeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, includeList, 0);
		}
		if (excludeList != null && !excludeList.isEmpty()) {
			this.getPresentmentDetailDAO().updatePresentmentDetials(presentmentId, excludeList,
					RepayConstants.PEXC_MANUAL_EXCLUDE);
		}
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_AWAITING_CONF, partnerBankId);
	}
	
	private void resubmitPresentments(long presentmentId, long partnerBankId) {
		this.getPresentmentDetailDAO().updatePresentmentHeader(presentmentId, RepayConstants.PEXC_BATCH_CREATED, partnerBankId);
	}

	private void approvePresentments(long presentmentId, LoggedInUser userDetails, boolean isPDC) throws Exception {
		processDetails(presentmentId, userDetails, isPDC);
	}
	
	private void cancelPresentments(long presentmentId) {
		List<PresentmentDetail> list = this.getPresentmentDetailDAO().getPresentmentDetail(presentmentId, false);
		if (list != null && !list.isEmpty()) {
			for (PresentmentDetail item : list) {
				if (item.getExcessID() != 0) {
					finExcessAmountDAO.updateExcessAmount(item.getExcessID(), item.getAdvanceAmt());
				}
				updatePresentmentIdAsZero(item.getId());

				String paymentMode = this.getPresentmentDetailDAO().getPaymenyMode(item.getPresentmentRef());
				if(MandateConstants.TYPE_PDC.equals(paymentMode)){
					updateChequeStatus(item.getMandateId(),PennantConstants.CHEQUESTATUS_NEW);
				}
			}
		}
		this.getPresentmentDetailDAO().deletePresentmentDetails(presentmentId);
		this.getPresentmentDetailDAO().deletePresentmentHeader(presentmentId);
	}


	@Override
	public PresentmentDetail presentmentCancellation(String presentmentRef, String returnCode) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentDetail presentmentDetail = null;
		try {
			String paymentMode = this.getPresentmentDetailDAO().getPaymenyMode(presentmentRef);
			presentmentDetail = getPresentmentDetailsByMode(presentmentRef, paymentMode);
			
			if (presentmentDetail == null) {
				throw new Exception(PennantJavaUtil.getLabel("label_Presentmentdetails_Notavailable") + presentmentRef);
			}
			updatePresentmentIdAsZero(presentmentDetail.getId());
			if (MandateConstants.TYPE_PDC.equals(paymentMode)) {
				updateChequeStatus(presentmentDetail.getMandateId(), PennantConstants.CHEQUESTATUS_BOUNCE);
			}
			presentmentDetail = this.receiptCancellationService.presentmentCancellation(presentmentDetail, returnCode);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);

		return presentmentDetail;
	}

	 /*
	  *  Extracting the presentments from Various tables and saving into Presentments
	  * PresentmentHeader presentmentHeader 
	  */
	@Override
	public String savePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception {
		if (MandateConstants.TYPE_PDC.equals(presentmentHeader.getMandateType())) {
			return savePDCPresentments(presentmentHeader);
		}
		return savePresentments(presentmentHeader);
	}

	private String savePDCPresentments(PresentmentHeader presentmentHeader) throws Exception {
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(getPresentmentDetailDAO(), finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePDCPresentments(presentmentHeader);
	}

	private String savePresentments(PresentmentHeader presentmentHeader) throws Exception { 
		PresentmentDetailExtractService presentmentService = new PresentmentDetailExtractService(getPresentmentDetailDAO(), finExcessAmountDAO, chequeDetailDAO);
		return presentmentService.savePresentments(presentmentHeader);
	}

	// Processing the presentment details
	public void processDetails(long presentmentId, LoggedInUser userDetails, boolean isPDC) throws Exception {
		logger.debug(Literal.ENTERING);

		List<Long> idList = new ArrayList<Long>();
		boolean isError = false;
		List<PresentmentDetail> detailList = getPresentmentDetailDAO().getPresentmentDetail(presentmentId, true);
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
					presentmentRequest.sendReqest(idList, presentmentId, isError, isPDC);
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

		PresentmentHeader header = getPresentmentDetailDAO().getPresentmentHeader(detail.getPresentmentId(), "_Aview");

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
			getPresentmentDetailDAO().updateReceptId(detail.getId(), receiptId);
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
			finScheduleData.getFinanceMain().setUserDetails(userDetails);
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
			receiptHeader.setReceiptMode(RepayConstants.RECEIPTMODE_PRESENTMENT);
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
			finReceiptDetail.setPaymentType(RepayConstants.RECEIPTMODE_PRESENTMENT);
			finReceiptDetail.setAmount(detail.getPresentmentAmt());
			finReceiptDetail.setPartnerBankAc(header.getPartnerAcctNumber());
			finReceiptDetail.setPartnerBankAcType(header.getPartnerAcctType());
			receiptHeader.getReceiptDetails().add(finReceiptDetail);
			
			//Receiptid creation #15-06-2018
			long receiptId=getFinReceiptHeaderDAO().generatedReceiptID(receiptHeader);
			receiptHeader.setReceiptID(receiptId);
			for (FinReceiptDetail receiptDetail : receiptHeader.getReceiptDetails()) {
				receiptDetail.setReceiptID(receiptId);
			}

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			finReceiptData.setReceiptHeader(receiptHeader);
			finReceiptData.setFinanceDetail(financeDetail);
			finReceiptData.setFinReference(financeMain.getFinReference());
			finReceiptData.setSourceId(PennantConstants.FINSOURCE_ID_API);

			// calculate allocations
			Map<String, BigDecimal> allocationMap = receiptCalculator.recalAutoAllocation(
					financeDetail, detail.getPresentmentAmt(), detail.getSchDate(),
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
}