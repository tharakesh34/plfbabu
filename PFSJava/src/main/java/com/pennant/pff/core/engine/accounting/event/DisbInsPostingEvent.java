package com.pennant.pff.core.engine.accounting.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;

public class DisbInsPostingEvent extends PostingEvent {
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private VehicleDealerService vehicleDealerService;

	public DisbInsPostingEvent() {
		super();
	}

	@Override
	public List<AEEvent> prepareAEEvents(PostingDTO postingDTO) {
		logger.info(LITERAL3, AccountingEvent.DISBINS);

		FinanceDetail fd = postingDTO.getFinanceDetail();
		String userBranch = postingDTO.getUserBranch();

		String moduleDefiner = fd.getModuleDefiner();
		FinScheduleData fschdData = fd.getFinScheduleData();
		List<FinAdvancePayments> advPaymentsList = fd.getAdvancePaymentsList();

		if (FinServiceEvent.ORG.equals(moduleDefiner)) {
			prepareVasAccountingData(advPaymentsList, fschdData.getVasRecordingList());
		}

		List<AEEvent> events = getTransactionEvents(fd, userBranch);

		logger.info(LITERAL4, AccountingEvent.DISBINS);
		return events;

	}

	@Override
	public void setEventDetails(List<AEEvent> aeEvents, PostingDTO postingDTO) {
		FinanceDetail fd = postingDTO.getFinanceDetail();

		List<FinAdvancePayments> advPaymentsList = fd.getAdvancePaymentsList();

		for (FinAdvancePayments fap : advPaymentsList) {
			for (AEEvent aeEvent : aeEvents) {
				if (fap.getPaymentId() == aeEvent.getPaymentId()) {
					fap.setLinkedTranId(aeEvent.getLinkedTranId());
					break;
				}
			}
		}
	}

	private void prepareVasAccountingData(List<FinAdvancePayments> advancePaymentsList,
			List<VASRecording> vasRecordingList) {
		logger.info("Preparing VAS accounting data...");

		for (VASRecording recording : vasRecordingList) {
			VehicleDealer vehicleDealer = vehicleDealerService.getDealerShortCodes(recording.getProductCode());
			if (vehicleDealer == null) {
				continue;
			}

			for (FinAdvancePayments fap : advancePaymentsList) {
				String vasReference = recording.getVasReference();
				if (StringUtils.equals(vasReference, fap.getVasReference())) {
					logger.debug("Preparing VAS accounting data for VASReferenc >> {}", vasReference);
					fap.setProductShortCode(vehicleDealer.getProductShortCode());
					fap.setDealerShortCode(vehicleDealer.getDealerShortCode());
					break;
				}
			}
		}

		logger.info("Preparing VAS accounting data completed.");
	}

	private List<AEEvent> getTransactionEvents(FinanceDetail fd, String userBranch) {
		FinScheduleData fschdData = fd.getFinScheduleData();
		FinanceMain fm = fschdData.getFinanceMain();
		List<FinAdvancePayments> advPaymentsList = fd.getAdvancePaymentsList();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<AEEvent> events = new ArrayList<>();

		List<FinAdvancePayments> approvedList = finAdvancePaymentsService.getFinAdvancePaymentsById(finID, "");

		for (FinAdvancePayments fap : advPaymentsList) {
			FinAdvancePayments finApprovedPay = isApproved(approvedList, fap.getPaymentId());

			if (finApprovedPay != null && StringUtils.equals(finApprovedPay.getStatus(), fap.getStatus())
					&& !PennantConstants.RCD_DEL.equals(fap.getRecordType())) {
				continue;
			}

			if (finApprovedPay != null) {
				if (StringUtils.isBlank(fap.getPartnerBankAc())) {
					fap.setPartnerBankAc(finApprovedPay.getPartnerBankAc());
				}

				if (StringUtils.isBlank(fap.getPartnerBankAcType())) {
					fap.setPartnerBankAcType(finApprovedPay.getPartnerBankAcType());
				}

				if (StringUtils.isBlank(fap.getDisbCCy())) {
					fap.setDisbCCy(finApprovedPay.getDisbCCy());
				}

				if (fap.getLlDate() == null) {
					fap.setLLDate(finApprovedPay.getLlDate());
				}
			}

			AEEvent aeEvent = new AEEvent();
			AEAmountCodes amountCodes = new AEAmountCodes();

			aeEvent.setPaymentId(fap.getPaymentId());
			aeEvent.setValueDate(fap.getLlDate());
			aeEvent.setCcy(fap.getDisbCCy());
			aeEvent.setBranch(fm.getFinBranch());
			aeEvent.setFinID(finID);
			aeEvent.setFinReference(finReference);
			aeEvent.setFinType(fm.getFinType());
			aeEvent.setCustID(fm.getCustID());
			aeEvent.setValueDate(fap.getLlDate());
			aeEvent.setPostingUserBranch(userBranch);
			aeEvent.setLinkedTranId(0);
			aeEvent.setEntityCode(fm.getLovDescEntityCode());

			amountCodes.setIntTdsAdjusted(fm.getIntTdsAdjusted());
			amountCodes.setPartnerBankAc(fap.getPartnerBankAc());
			amountCodes.setPartnerBankAcType(fap.getPartnerBankAcType());
			amountCodes.setFinType(aeEvent.getFinType());
			amountCodes.setDisbInstAmt(fap.getAmtToBeReleased());

			Map<String, Object> dataMap = aeEvent.getDataMap();
			dataMap.put("ae_productCode", fap.getProductShortCode());
			dataMap.put("ae_dealerCode", fap.getDealerShortCode());
			dataMap.put("id_totPayAmount", fap.getAmtToBeReleased());

			dataMap.putAll(amountCodes.getDeclaredFieldValues());

			if (DisbursementConstants.PAYMENT_DETAIL_VAS.equals(fap.getPaymentDetail())) {
				aeEvent.setAccountingEvent(AccountingEvent.INSPAY);
			} else {
				// FIXME the below code needs to be moved to External layer
				if (fd.getCustomerDetails() != null && fd.getFinScheduleData().getFinanceType() != null) {
					dataMap.put("emptype", fd.getCustomerDetails().getCustomer().getSubCategory());
					dataMap.put("fincollateralreq", fd.getFinScheduleData().getFinanceType().isFinCollateralReq());
					dataMap.put("division", fd.getFinScheduleData().getFinanceType().getFinDivision());
				}

				aeEvent.setAccountingEvent(AccountingEvent.DISBINS);
			}

			if (PennantConstants.RCD_DEL.equals(fap.getRecordType())) {
				long linkedTranId = finApprovedPay.getLinkedTranId();
				aeEvent.getReturnDataSet().addAll(AccountingEngine.getReversalsByLinkedTranID(linkedTranId));
			} else {
				String promotionCode = fm.getPromotionCode();
				String accountingEvent = aeEvent.getAccountingEvent();
				int module = 0;

				if (StringUtils.isNotBlank(promotionCode)) {
					fm.setFinType(promotionCode);
					module = FinanceConstants.MODULEID_PROMOTION;
				} else {
					fm.setFinType(aeEvent.getFinType());
					module = FinanceConstants.MODULEID_FINTYPE;
				}

				aeEvent.getAcSetIDList().add(getAccountingSetId(fm, accountingEvent, module));

			}

			events.add(aeEvent);
		}

		return events;
	}

	private FinAdvancePayments isApproved(List<FinAdvancePayments> advPayment, long paymentID) {
		if (advPayment == null || advPayment.isEmpty()) {
			return null;
		}
		for (FinAdvancePayments finAdvancePayments : advPayment) {
			if (finAdvancePayments.getPaymentId() == paymentID) {
				return finAdvancePayments;
			}
		}
		return null;
	}

	@Autowired
	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	@Autowired
	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

}
