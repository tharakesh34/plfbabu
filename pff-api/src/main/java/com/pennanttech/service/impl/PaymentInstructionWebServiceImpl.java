package com.pennanttech.service.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.AccountConstants;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.PaymentInstructionController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.PaymentInstructionRestService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class PaymentInstructionWebServiceImpl extends ExtendedTestClass implements PaymentInstructionRestService {
	private final Logger logger = LogManager.getLogger(getClass());

	private PaymentInstructionController paymentInstructionController;
	private FinanceMainDAO financeMainDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchDAO bankBranchDAO;
	private PaymentHeaderDAO paymentHeaderDAO;

	@Override
	public WSReturnStatus createPaymentInstruction(PaymentHeader paymentHeader) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = doPaymentInstructionValidation(paymentHeader);

		WSReturnStatus response = null;
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = paymentInstructionController.createPaymentInstruction(paymentHeader);
		} else {
			return returnStatus;
		}
		// for Logging Purpose
		String[] logFields = new String[1];
		logFields[0] = String.valueOf(paymentHeader.getFinReference());
		APIErrorHandlerService.logKeyFields(logFields);
		APIErrorHandlerService.logReference(paymentHeader.getFinReference());

		return response;

	}

	private WSReturnStatus doPaymentInstructionValidation(PaymentHeader ph) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		// validate customer
		String finReference = ph.getFinReference();
		if (StringUtils.isNotBlank(finReference)) {
			int count = financeMainDAO.getFinanceCountById(ph.getFinID());
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				return getErrorDetails("90502", valueParm);
			}
		}
		if (ph.getPaymentAmount().compareTo(BigDecimal.ZERO) == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return getErrorDetails("90502", valueParm);
		}
		if (ph.getPaymentDetailList().size() == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "At least one paymentdetail mandatory";
			return getErrorDetails("90502", valueParm);
		} else {
			BigDecimal amount = BigDecimal.ZERO;
			for (int i = 0; i < ph.getPaymentDetailList().size(); i++) {
				amount = amount.add(ph.getPaymentDetailList().get(i).getAmount());
			}
			if (amount.compareTo(ph.getPaymentAmount()) > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Payable amount should match with amounts in payable details";
				return getErrorDetails("90502", valueParm);
			}
		}

		if (StringUtils.isNotBlank(ph.getPaymentType())) {
			if (!StringUtils.equals(RepayConstants.EXAMOUNTTYPE_EXCESS, ph.getPaymentType())
					&& !StringUtils.equals("MANADV", ph.getPaymentType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Payment Type Not Valid";
				return getErrorDetails("90502", valueParm);
			}
			if (StringUtils.equals("MANADV", ph.getPaymentType())) {
				for (int i = 0; i < ph.getPaymentDetailList().size(); i++) {
					if (!StringUtils.equals(RepayConstants.EXAMOUNTTYPE_EMIINADV,
							ph.getPaymentDetailList().get(i).getAmountType())
							&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_ADVEMI,
									ph.getPaymentDetailList().get(i).getAmountType())
							&& !StringUtils.equals(RepayConstants.EXAMOUNTTYPE_ADVINT,
									ph.getPaymentDetailList().get(i).getAmountType())) {
						String[] valueParm = new String[1];
						valueParm[0] = "Fee Type Not Valid";
						return getErrorDetails("90502", valueParm);
					}
				}
			}

		}

		if (StringUtils.isBlank(ph.getPaymentInstruction().getPaymentType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Payment Type Valid";
			return getErrorDetails("90502", valueParm);
		}

		if (StringUtils.isNotBlank(ph.getPaymentInstruction().getPaymentType())) {
			if (!StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_IMPS,
					ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_NEFT,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_RTGS,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_IFT,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CASH,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_ESCROW,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_MOBILE,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_ONLINE,
							ph.getPaymentInstruction().getPaymentType())
					&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_IST,
							ph.getPaymentInstruction().getPaymentType())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Payment Type Valid";
				return getErrorDetails("90502", valueParm);
			}
		}

		if (StringUtils.isNotBlank(String.valueOf(ph.getPaymentInstruction().getPartnerBankId()))
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
						ph.getPaymentInstruction().getPaymentType())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			String finType = financeMainDAO.getFinanceType(ph.getFinID(), TableType.MAIN_TAB);
			int count = finTypePartnerBankDAO.getPartnerBankCount(finType, ph.getPaymentInstruction().getPaymentType(),
					AccountConstants.PARTNERSBANK_PAYMENT, ph.getPaymentInstruction().getPartnerBankId());
			if (count == 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "Partnerbank Valid";
				return getErrorDetails("90502", valueParm);
			}
		}
		if (StringUtils.isBlank(ph.getPaymentInstruction().getBankBranchIFSC())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
						ph.getPaymentInstruction().getPaymentType())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "IFSC is not valid";
			return getErrorDetails("90502", valueParm);
		}
		if (StringUtils.isNotBlank(ph.getPaymentInstruction().getBankBranchIFSC())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
						ph.getPaymentInstruction().getPaymentType())
				&& StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			BankBranch bb = bankBranchDAO.getBankBrachByIFSC(ph.getPaymentInstruction().getBankBranchIFSC(), "");
			if (bb == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Ifsc is not valid";
				return getErrorDetails("90502", valueParm);
			}
		}
		if (StringUtils.isBlank(ph.getPaymentInstruction().getAccountNo())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
						ph.getPaymentInstruction().getPaymentType())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Account Number is not valid";
			return getErrorDetails("90502", valueParm);
		}
		if (StringUtils.isBlank(ph.getPaymentInstruction().getAcctHolderName())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
						ph.getPaymentInstruction().getPaymentType())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Account holder name is not valid";
			return getErrorDetails("90502", valueParm);
		}
		if (StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE, ph.getPaymentInstruction().getPaymentType())
				|| StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
						ph.getPaymentInstruction().getPaymentType())) {
			if (StringUtils.isBlank(ph.getPaymentInstruction().getIssuingBank())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Issueing bank is mandatory for Cheque/DD";
				return getErrorDetails("90502", valueParm);
			}
			if (StringUtils.isBlank(ph.getPaymentInstruction().getFavourName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Favour name is mandatory for Cheque/DD";
				return getErrorDetails("90502", valueParm);
			}
			if (StringUtils.isBlank(ph.getPaymentInstruction().getPayableLoc())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Payable location bank is mandatory for Cheque/DD";
				return getErrorDetails("90502", valueParm);
			}
			if (StringUtils.isBlank(ph.getPaymentInstruction().getPrintingLoc())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Priniting location is mandatory for Cheque/DD";
				return getErrorDetails("90502", valueParm);
			}
			if (ph.getPaymentInstruction().getValueDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "Value date is mandatory for Cheque/DD";
				return getErrorDetails("90502", valueParm);
			}
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public PaymentHeader getPaymentInstruction(PaymentHeader paymentHeader) throws ServiceException {
		PaymentHeader response = null;
		// validate given finReference is valid or not.
		if (!financeMainDAO.isFinReferenceExists(paymentHeader.getFinReference(), "", false)) {
			response = new PaymentHeader();
			String[] valueParm = new String[1];
			valueParm[0] = "finreference: " + paymentHeader.getFinReference();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		}
		int count = paymentHeaderDAO.getPaymenttId(paymentHeader.getPaymentId());
		if (count == 0) {
			response = new PaymentHeader();
			String[] valueParm = new String[1];
			valueParm[0] = "paymentId: " + paymentHeader.getPaymentId();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		}

		response = paymentInstructionController.getPaymentDetails(paymentHeader.getPaymentId());
		return response;

	}

	@Override
	public WSReturnStatus updatePaymentInstruction(PaymentHeader paymentHeader) throws ServiceException {
		WSReturnStatus response = new WSReturnStatus();

		paymentHeader.setPaymentId(paymentHeader.getPaymentInstruction().getPaymentId());

		response = validateUpdatePaymentInstructionRequest(paymentHeader);
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = paymentInstructionController.updatePaymentInstruction(paymentHeader);
		}

		return response;

	}

	private WSReturnStatus validateUpdatePaymentInstructionRequest(PaymentHeader paymentHeader) {
		WSReturnStatus returnStatus = new WSReturnStatus();

		if (!financeMainDAO.isFinReferenceExists(paymentHeader.getFinReference(), "", false)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finreference: " + paymentHeader.getFinReference();
			return getErrorDetails("90201", valueParm);
		}

		if (paymentHeaderDAO.getPaymenttId(paymentHeader.getPaymentId(), paymentHeader.getFinReference()) == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "PaymentID Not is Matching with Finreference";
			return getErrorDetails("90502", valueParm);

		}

		int count = paymentHeaderDAO.getPaymenttId(paymentHeader.getPaymentInstruction().getPaymentId());
		if (count == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "PaymentId: " + paymentHeader.getPaymentId();
			return getErrorDetails("90502", valueParm);
		}

		if (paymentHeader.getPaymentInstruction().getTransactionRef() == null
				|| StringUtils.isEmpty(paymentHeader.getPaymentInstruction().getTransactionRef())) {
			String[] valueParm = new String[1];
			valueParm[0] = "TransactionRef: " + paymentHeader.getPaymentInstruction().getTransactionRef();
			return getErrorDetails("90502", valueParm);
		}

		String status = paymentHeader.getPaymentInstruction().getStatus();

		if (!PennantConstants.RCD_STATUS_APPROVED.equals(status)
				&& !PennantConstants.RCD_STATUS_REJECTED.equals(status)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Status is Invalid";
			return getErrorDetails("90502", valueParm);
		}

		if (StringUtils.isBlank(paymentHeader.getPaymentInstruction().getRejectReason())
				&& PennantConstants.RCD_STATUS_REJECTED.equals(status)) {
			String[] valueParm = new String[1];
			valueParm[0] = "Reject Reason";
			return getErrorDetails("90502", valueParm);
		}

		return returnStatus;
	}

	@Autowired
	public void setPaymentInstructionController(PaymentInstructionController paymentInstructionController) {
		this.paymentInstructionController = paymentInstructionController;
	}

	@Autowired
	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

}
