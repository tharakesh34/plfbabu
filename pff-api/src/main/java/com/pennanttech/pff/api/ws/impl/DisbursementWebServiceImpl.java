package com.pennanttech.pff.api.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.api.controller.DisbursementController;
import com.pennanttech.pff.api.ws.DisbursementRESTService;
import com.pennanttech.pff.api.ws.DisbursementSOAPService;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.ws.model.disbursement.DisbursementRequestDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DisbursementWebServiceImpl implements DisbursementRESTService, DisbursementSOAPService {
	private static final Logger logger = LogManager.getLogger(DisbursementWebServiceImpl.class);

	private EntityService entityService;
	private PartnerBankDAO partnerBankDAO;
	private FinanceTypeDAO financeTypeDAO;
	private DisbursementController disbursementController;
	private FinanceMainDAO financeMainDAO;

	@Override
	public DisbursementRequestDetail getDisbursementInstructions(FinAdvancePayments fap) throws ServiceException {
		logger.debug(Literal.ENTERING);
		List<DisbursementRequest> disbRequest = new ArrayList<>();

		WSReturnStatus returnStatus = validateDisbursements(fap);
		DisbursementRequestDetail disbreq = new DisbursementRequestDetail();

		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			disbreq.setReturnStatus(returnStatus);
			return disbreq;
		}

		disbRequest = disbursementController.getDisbursementInstructions(fap);

		if (CollectionUtils.isEmpty(disbRequest)) {
			String valueParm[] = new String[2];
			valueParm[0] = "Disbursement";
			valueParm[1] = " Details";
			returnStatus = APIErrorHandlerService.getFailedStatus("41002", valueParm);
			disbreq.setReturnStatus(returnStatus);
			return disbreq;
		} else {
			disbreq.setDisbDetail(disbRequest);
			disbreq.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return disbreq;
	}

	private WSReturnStatus validateDisbursements(FinAdvancePayments finAdvancePayments) {
		logger.info("Validating the request");
		WSReturnStatus status = new WSReturnStatus();

		/* Validate Entity */
		String entityCode = finAdvancePayments.getEntityCode();
		if (StringUtils.isBlank(entityCode)) {
			String valueParm[] = new String[1];
			valueParm[0] = "EntityCode";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Entity entitycode = entityService.getApprovedEntity(entityCode);
		if (entitycode == null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Entity";
			valueParm[1] = entityCode;
			return APIErrorHandlerService.getFailedStatus("90701", valueParm);
		}

		/* Validate Loan Type */
		String finType = finAdvancePayments.getFinType();
		if (StringUtils.isBlank(finType)) {
			String valueParm[] = new String[1];
			valueParm[0] = "FinType";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		FinanceType fiananceType = financeTypeDAO.getFinanceTypeByFinType(finType);
		if (fiananceType == null) {
			String valueParm[] = new String[1];
			valueParm[0] = "FinType";
			return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
		}

		/* Validate Partner Bank */
		String partnerbankCode = finAdvancePayments.getPartnerbankCode();
		if (StringUtils.isBlank(partnerbankCode)) {
			String valueParm[] = new String[1];
			valueParm[0] = "PartnerBankCode";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		boolean Exists = partnerBankDAO.isPartnerBankCodeExistsByEntity(entityCode, partnerbankCode, "_AView");
		if (!Exists) {
			String valueParm[] = new String[2];
			valueParm[0] = "PartnerBankCode: " + partnerbankCode + " with";
			valueParm[1] = "EntityCode: " + entityCode;
			return APIErrorHandlerService.getFailedStatus("41002", valueParm);
		}

		/* Validate Disbursement Type */
		String paymentType = finAdvancePayments.getPaymentType();
		if (StringUtils.isNotBlank(paymentType)) {
			List<ValueLabel> valuelabel = PennantStaticListUtil.getDisbRegistrationTypes();
			List<String> disbtypes = new ArrayList<>();
			for (ValueLabel disbursementTypes : valuelabel) {
				disbtypes.add(disbursementTypes.getValue());
			}
			if (!(disbtypes.contains(paymentType))) {
				String valueParm[] = new String[1];
				valueParm[0] = "disbType";
				return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
			}
		}

		/* Validate Channel */
		String channel = finAdvancePayments.getChannel();
		if (StringUtils.isNotBlank(channel)) {
			List<ValueLabel> channelList = PennantStaticListUtil.getChannelTypes();
			List<String> channelTypeList = new ArrayList<>();
			for (ValueLabel channelsList : channelList) {
				channelTypeList.add(channelsList.getValue());
			}

			if (!(channelTypeList.contains(channel))) {
				String valueParm[] = new String[1];
				valueParm[0] = "Channel";
				return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
			}
		}

		/* Validate From and To Dates */
		if (finAdvancePayments.getFromDate() != null && finAdvancePayments.getToDate() != null) {
			if ((finAdvancePayments.getFromDate().compareTo(finAdvancePayments.getToDate()) > 0)) {
				String valueParm[] = new String[2];
				valueParm[0] = "To date";
				valueParm[1] = "From Date";
				return APIErrorHandlerService.getFailedStatus("65012", valueParm);
			}
		}
		return status;
	}

	@Override
	public DisbursementRequestDetail downloadDisbursementInstructions(List<FinAdvancePayments> list)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = null;
		DisbursementRequestDetail disbRequestDetail = new DisbursementRequestDetail();

		for (FinAdvancePayments finAdvancePayments : list) {
			String finReference = finAdvancePayments.getFinReference();
			if (StringUtils.isBlank(finReference)) {
				String valueParm[] = new String[2];
				valueParm[0] = "Finreference";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				disbRequestDetail.setReturnStatus(returnStatus);
				return disbRequestDetail;
			}

			int count = financeMainDAO.getFinanceCountById(finReference, "", false);
			if (count <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = finReference;
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				disbRequestDetail.setReturnStatus(returnStatus);
				return disbRequestDetail;
			}

			long paymentId = finAdvancePayments.getPaymentId();

			if (paymentId <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = "disbInstId";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				disbRequestDetail.setReturnStatus(returnStatus);
				return disbRequestDetail;
			}

			String channel = finAdvancePayments.getChannel();

			if (StringUtils.isBlank(channel)) {
				String valueParm[] = new String[2];
				valueParm[0] = "Channel";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				disbRequestDetail.setReturnStatus(returnStatus);
				return disbRequestDetail;
			}

			List<ValueLabel> channelList = PennantStaticListUtil.getChannelTypes();
			List<String> channelTypeList = new ArrayList<>();
			for (ValueLabel channelsList : channelList) {
				channelTypeList.add(channelsList.getValue());
			}
			if (!(channelTypeList.contains(channel))) {
				String valueParm[] = new String[1];
				valueParm[0] = "Channel";
				returnStatus = APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
				disbRequestDetail.setReturnStatus(returnStatus);
				return disbRequestDetail;
			}
		}
		try {

			disbRequestDetail = disbursementController.updateDisbursementStatus(list);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			returnStatus = APIErrorHandlerService.getFailedStatus();
			disbRequestDetail.setReturnStatus(returnStatus);
			return disbRequestDetail;
		}

		logger.debug(Literal.LEAVING);
		return disbRequestDetail;
	}

	@Override
	public WSReturnStatus updateDisbursementInstructionStatus(List<DisbursementRequest> disbRequest)
			throws ServiceException {
		logger.info(Literal.ENTERING);
		WSReturnStatus returnStatus = new WSReturnStatus();

		returnStatus = validateDisbursementResponse(disbRequest);
		if (returnStatus != null) {
			return returnStatus;
		}

		returnStatus = disbursementController.approveDisbursementResponse(disbRequest);

		logger.info(Literal.LEAVING);
		return returnStatus;
	}

	private WSReturnStatus validateDisbursementResponse(List<DisbursementRequest> disbursementRequest) {
		logger.info(Literal.ENTERING);
		for (DisbursementRequest disbRequest : disbursementRequest) {

			if (disbRequest.getDisbReqId() <= 0) {
				String[] valueParam = new String[1];
				valueParam[0] = "disbReqId";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			if (StringUtils.isBlank(disbRequest.getFinReference())) {
				String[] valueParam = new String[1];
				valueParam[0] = "FinReference";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			int count = financeMainDAO.getFinanceCountById(disbRequest.getFinReference(), " ", false);
			if (count <= 0) {
				String[] valueParam = new String[1];
				valueParam[0] = disbRequest.getFinReference();
				return APIErrorHandlerService.getFailedStatus("90201", valueParam);
			}

			if (disbRequest.getDisbInstId() <= 0) {
				String[] valueParam = new String[2];
				valueParam[0] = "disbInstId";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			if (disbRequest.getClearingDate() == null) {
				String[] valueParam = new String[1];
				valueParam[0] = "ClearingDate";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			if (StringUtils.isBlank(disbRequest.getStatus())) {
				String[] valueParam = new String[1];
				valueParam[0] = "Status";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			if (!(StringUtils.equals("R", disbRequest.getStatus())
					|| StringUtils.equals("E", disbRequest.getStatus()))) {
				String[] valueParam = new String[2];
				valueParam[0] = "Status";
				valueParam[1] = "E," + "R";
				return APIErrorHandlerService.getFailedStatus("90337", valueParam);
			}

			if (StringUtils.equals("R", disbRequest.getStatus())
					&& StringUtils.isBlank(disbRequest.getRejectReason())) {
				String[] valueParam = new String[1];
				valueParam[0] = "RejectReason for Status R";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}
			disbRequest.setPaymentType(disbRequest.getDisbType());
			if (StringUtils.isBlank(disbRequest.getPaymentType())) {
				String[] valueParam = new String[1];
				valueParam[0] = "disbType";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			List<ValueLabel> paymentTypes = PennantStaticListUtil.getPaymentTypesWithIST();
			boolean paymentTypeSts = false;
			for (ValueLabel value : paymentTypes) {
				if (StringUtils.equals(value.getValue(), disbRequest.getPaymentType())) {
					paymentTypeSts = true;
					break;
				}
			}
			if (!paymentTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = disbRequest.getPaymentType();
				return APIErrorHandlerService.getFailedStatus("90216", valueParm);
			}

			String paymentType = disbRequest.getPaymentType();

			if (StringUtils.isNotBlank(paymentType)) {
				if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(disbRequest.getPaymentType())
						|| DisbursementConstants.PAYMENT_TYPE_DD.equals(disbRequest.getPaymentType())) {
					if (StringUtils.isBlank(disbRequest.getChequeNumber())) {
						String[] valueParam = new String[1];
						valueParam[0] = "ChequeNo";
						return APIErrorHandlerService.getFailedStatus("90502", valueParam);
					}
				}
			}

			if (disbRequest.getDisbursementDate() == null) {
				String[] valueParam = new String[1];
				valueParam[0] = "disbursementDate";
				return APIErrorHandlerService.getFailedStatus("90502", valueParam);
			}

			String channel = disbRequest.getChannel();

			if (StringUtils.isBlank(channel)) {
				String valueParm[] = new String[2];
				valueParm[0] = "Channel";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}

			List<ValueLabel> channelList = PennantStaticListUtil.getChannelTypes();
			List<String> channelTypeList = new ArrayList<>();
			for (ValueLabel channelsList : channelList) {
				channelTypeList.add(channelsList.getValue());
			}
			if (!(channelTypeList.contains(channel))) {
				String valueParm[] = new String[1];
				valueParm[0] = "Channel";
				return APIErrorHandlerService.getFailedStatus("RU0040", valueParm);
			}
		}
		logger.info(Literal.LEAVING);
		return null;
	}

	@Autowired
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setDisbursementController(DisbursementController disbursementController) {
		this.disbursementController = disbursementController;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
