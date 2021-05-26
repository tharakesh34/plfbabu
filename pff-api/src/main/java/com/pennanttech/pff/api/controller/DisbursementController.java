package com.pennanttech.pff.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.core.process.PaymentProcess;
import com.pennanttech.pff.external.disbursement.DisbursementRequestService;
import com.pennanttech.pff.external.disbursement.dao.DisbursementDAO;
import com.pennanttech.ws.model.disbursement.DisbursementRequestDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DisbursementController extends ExtendedTestClass {
	private static final Logger logger = LogManager.getLogger(DisbursementController.class);

	private DisbursementDAO disbursementDAO;
	private transient DisbursementRequestService disbursementRequestService;
	private DisbursementProcess disbursementProcess;
	private PaymentProcess paymentProcess;
	private PlatformTransactionManager transactionManager;

	private static String PAID_STATUS = "E";
	private static String REALIZED_STATUS = "P";

	public List<DisbursementRequest> getDisbursementInstructions(FinAdvancePayments fap) {
		logger.info("Identifying the disbursement instructions..");

		DisbursementRequest dr = new DisbursementRequest();
		dr.setPartnerBankCode(fap.getPartnerbankCode());
		dr.setFinType(fap.getFinType());
		dr.setEntityCode(fap.getEntityCode());
		dr.setFinReference(fap.getFinReference());
		dr.setFinReference(fap.getFinReference());

		if (fap.getPaymentType() != null) {
			dr.setPaymentType(fap.getPaymentType());
		}

		if (fap.getChannel() != null) {
			dr.setChannel(fap.getChannel());
		}

		if (fap.getPaymentDetail() != null) {
			dr.setDisbParty(fap.getPaymentDetail());
		}

		if (fap.getToDate() != null) {
			dr.setToDate(fap.getToDate());
		}

		if (fap.getFromDate() != null) {
			dr.setFromDate(fap.getFromDate());
		}

		List<DisbursementRequest> disbRequests = disbursementDAO.getDisbursementInstructions(dr);

		logger.info("{} disbursemet instructions found for the specified request", disbRequests.size());
		return disbRequests;
	}

	public DisbursementRequestDetail updateDisbursementStatus(List<FinAdvancePayments> disbInstructions) {
		logger.debug(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();
		DisbursementRequest request = new DisbursementRequest();
		List<DisbursementRequest> disbRequests = new ArrayList<>();

		DisbursementRequestDetail disbDetail = new DisbursementRequestDetail();

		for (FinAdvancePayments fap : disbInstructions) {
			long paymentId = fap.getPaymentId();
			String channel = fap.getChannel();
			String disbParty = fap.getPaymentDetail();
			String finReference = fap.getFinReference();

			FinAdvancePayments disb = disbursementDAO.getDisbursementInstruction(paymentId, channel);

			if (disb == null) {
				String valueParm[] = new String[4];
				valueParm[0] = "Disbursement instruction with disbinstId ID :" + paymentId;
				valueParm[1] = "and channel :" + channel;
				valueParm[2] = "not exists or Already ";
				valueParm[3] = "Processed";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				disbDetail.setReturnStatus(returnStatus);
				return disbDetail;
			}

			String status = disb.getStatus();
			if (!status.equals(DisbursementConstants.STATUS_APPROVED)) {
				String valueParm[] = new String[4];
				valueParm[0] = "Disbursement instruction with disbinstId ID :" + paymentId;
				valueParm[1] = "and channel :" + channel;
				valueParm[2] = "already processed";
				valueParm[3] = "";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				disbDetail.setReturnStatus(returnStatus);
				return disbDetail;
			}

			if (!finReference.equals(disb.getFinReference())) {
				String valueParm[] = new String[4];
				valueParm[0] = "Finreference: " + finReference;
				valueParm[1] = "is Not Matched with";
				valueParm[2] = "the Existing";
				valueParm[3] = "disbInstId";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				disbDetail.setReturnStatus(returnStatus);

				return disbDetail;
			}

			request.getFinAdvancePayments().add(disb);
			request.setRequestSource(PennantConstants.FINSOURCE_ID_API);
			request.setAppValueDate(SysParamUtil.getAppValueDate());
		}

		try {
			disbursementRequestService.prepareRequest(request);

			long headerId = request.getHeaderId();

			disbRequests = disbursementDAO.getDetailsByHeaderID(headerId);

			if (CollectionUtils.isNotEmpty(disbRequests)) {
				disbDetail.setDisbDetail(disbRequests);
				disbDetail.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				return disbDetail;
			} else {
				String valueParm[] = new String[2];
				valueParm[0] = "Disbursement";
				valueParm[1] = " Details";
				returnStatus = APIErrorHandlerService.getFailedStatus("41004", valueParm);
				disbDetail.setReturnStatus(returnStatus);
				return disbDetail;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = " Details";
			returnStatus = APIErrorHandlerService.getFailedStatus("41004", valueParm);
			disbDetail.setReturnStatus(returnStatus);
			return disbDetail;

		}
	}

	public WSReturnStatus approveDisbursementResponse(List<DisbursementRequest> disbRequests) {
		logger.info(Literal.ENTERING);
		
		try {
			for (DisbursementRequest request : disbRequests) {
				request.setId(request.getDisbReqId());

				long disbReqId = request.getId();
				String channel = request.getChannel();
				long disbInstId = request.getDisbInstId();
				String disbParty = request.getDisbParty();

				DisbursementRequest disb = disbursementDAO.getDisbRequest(disbReqId);
				if (disb == null) {
					String valueParm[] = new String[4];
					valueParm[0] = "Disbursement details with disbReqId: " + disbReqId;
					valueParm[1] = "and channel :" + channel;
					valueParm[2] = "not exists or Already ";
					valueParm[3] = "Processed";
					return APIErrorHandlerService.getFailedStatus("30550", valueParm);
				}

				if (!DisbursementConstants.STATUS_AWAITCON.equals(disb.getStatus())) {
					String valueParm[] = new String[4];
					valueParm[0] = "Disbursement details with disbReqId: " + disbReqId;
					valueParm[1] = "and channel :" + channel;
					valueParm[2] = " Are not at the Stage of ";
					valueParm[3] = "AC";
					return APIErrorHandlerService.getFailedStatus("30550", valueParm);
				}

				if (disb.getPaymentId() != disbInstId) {
					String valueParm[] = new String[4];
					valueParm[0] = "Given DisbInstId: " + disbInstId;
					valueParm[1] = "is not Matched with the DisbInstId: " + disb.getPaymentId();
					valueParm[2] = "";
					valueParm[3] = "";
					return APIErrorHandlerService.getFailedStatus("30550", valueParm);
				}

				/* validations for the DisbType Cheque and DD verifying from the DB */
				String disbType = disb.getDisbType();
				if (("C".equals(disbType) || DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbType))) {
					disbType = DisbursementConstants.PAYMENT_TYPE_CHEQUE;
				} else if ("D".equals(disbType) || DisbursementConstants.PAYMENT_TYPE_DD.equals(disbType)) {
					disbType = DisbursementConstants.PAYMENT_TYPE_DD;
				} else if ("N".equals(disbType) || DisbursementConstants.PAYMENT_TYPE_NEFT.equals(disbType)) {
					disbType = DisbursementConstants.PAYMENT_TYPE_NEFT;
				}

				if (!request.getDisbType().equals(disbType)) {
					String valueParm[] = new String[4];
					valueParm[0] = "Given DisbType: " + request.getDisbType();
					valueParm[1] = "is Incorrect ";
					valueParm[2] = "";
					valueParm[3] = "";

					return APIErrorHandlerService.getFailedStatus("30550", valueParm);
				}

				switch (channel) {
				case DisbursementConstants.CHANNEL_DISBURSEMENT:
					FinAdvancePayments fa = disbursementDAO.getDisbursementInstruction(disbReqId);

					if (fa == null) {
						String valueParm[] = new String[4];
						valueParm[0] = "Disbursement details with disbReqId :" + disbReqId;
						valueParm[1] = "and channel :" + channel;
						valueParm[2] = "not exists or Already ";
						valueParm[3] = "Processed";
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);

					}

					if (!DisbursementConstants.STATUS_AWAITCON.equals(fa.getStatus())) {
						String valueParm[] = new String[4];
						valueParm[0] = "Disbursement details with disbReqId :" + disbReqId;
						valueParm[1] = "and channel :" + channel;
						valueParm[2] = " Are not at the Stage of ";
						valueParm[3] = "AC";
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);

					}

					fa.setClearingDate(request.getClearingDate());
					fa.setClearingStatus(request.getStatus());
					fa.setRejectReason(request.getRejectReason());
					fa.setTransactionRef(request.getTransactionref());
					
					if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(request.getPaymentType())
							|| DisbursementConstants.PAYMENT_TYPE_DD.equals(request.getPaymentType())) {
						fa.setLLReferenceNo(request.getChequeNumber());
					}

					String finReference = request.getFinReference();
					FinanceMain fm = disbursementProcess.getDisbursmentFinMainById(finReference, TableType.MAIN_TAB);

					if (fm == null) {
						fm = disbursementProcess.getDisbursmentFinMainById(finReference, TableType.TEMP_TAB);
					}

					break;
				case DisbursementConstants.CHANNEL_PAYMENT:
					PaymentInstruction pi = disbursementDAO.getPaymentInstruction(disbReqId);

					if (pi == null) {
						String valueParm[] = new String[4];
						valueParm[0] = "Disbursement details with disbReqId :" + disbReqId;
						valueParm[1] = "and channel :" + channel;
						valueParm[2] = "not exists or Already ";
						valueParm[3] = "Processed";
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);

					}

					if (!DisbursementConstants.STATUS_AWAITCON.equals(pi.getStatus())) {
						String valueParm[] = new String[4];
						valueParm[0] = "Disbursement details with disbReqId :" + disbReqId;
						valueParm[1] = "and channel :" + channel;
						valueParm[2] = " Are not at the Stage of ";
						valueParm[3] = "AC";
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);

					}

					pi.setFinReference(request.getFinReference());
					pi.setClearingDate(request.getClearingDate());
					pi.setClearingStatus(request.getStatus());
					pi.setRejectReason(request.getRejectReason());
					pi.setTransactionRef(request.getTransactionref());


					break;
				default:
					break;
				}
				
				updateRequest(request);
			}
			
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = " Details";
			return APIErrorHandlerService.getFailedStatus("41004", valueParm);

		}
		logger.info(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();

	}

	private int updateRequest(DisbursementRequest request) {
		if (PAID_STATUS.equals(request.getStatus())) {
			request.setStatus(DisbursementConstants.STATUS_PAID);
		} else if (REALIZED_STATUS.equals(request.getStatus())) {
			request.setStatus(DisbursementConstants.STATUS_REALIZED);
		} else {
			request.setStatus(DisbursementConstants.STATUS_REJECTED);
		}
		return disbursementDAO.updateDisbRequest(request);
	}

	@Autowired
	public void setDisbursementDAO(DisbursementDAO disbursementDAO) {
		this.disbursementDAO = disbursementDAO;
	}

	public void setDisbursementRequestService(DisbursementRequestService disbursementRequestService) {
		this.disbursementRequestService = disbursementRequestService;
	}

	public void setDisbursementProcess(DisbursementProcess disbursementProcess) {
		this.disbursementProcess = disbursementProcess;
	}

	public void setPaymentProcess(PaymentProcess paymentProcess) {
		this.paymentProcess = paymentProcess;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}