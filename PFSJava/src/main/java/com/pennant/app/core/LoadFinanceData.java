package com.pennant.app.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.util.EODProperties;
import com.pennanttech.pff.core.TableType;

public class LoadFinanceData extends ServiceHelper {

	private static final long	serialVersionUID	= -281578785120363314L;

	public CustEODEvent prepareFinEODEvents(CustEODEvent custEODEvent) throws Exception {

		long custID = custEODEvent.getCustomer().getCustID();
		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinanceMainsByCustId(custID, true);

		for (int i = 0; i < custFinMains.size(); i++) {
			FinEODEvent finEODEvent = new FinEODEvent();
			Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

			//FINANCE MAIN
			finEODEvent.setFinanceMain(custFinMains.get(i));
			String finType = finEODEvent.getFinanceMain().getFinType();
			String finReference = finEODEvent.getFinanceMain().getFinReference();

			//FINANCE TYPE
			FinanceType financeType = EODProperties.getFinanceType(finType);
			finEODEvent.setFinType(financeType);

			//FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
					finReference, TableType.MAIN_TAB.getSuffix(), false);

			//Place schedule dates to Map
			for (int j = 0; j < finSchdDetails.size(); j++) {
				datesMap.put(finSchdDetails.get(j).getSchDate(), j);
			}

			finEODEvent.setDatesMap(datesMap);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);
			finEODEvent.setFinanceDisbursements(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, FinanceConstants.DISB_STATUS_CANCEL));

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference));

			custEODEvent.getFinEODEvents().add(finEODEvent);

		}
		return custEODEvent;
	}

	public void updateFinEODEvents(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			//update finance main
			if (finEODEvent.isUpdFinMain()) {
				finEODEvent.getFinanceMain().setVersion(finEODEvent.getFinanceMain().getVersion()+1);
				getFinanceMainDAO().updateFinanceInEOD(finEODEvent.getFinanceMain());
			}

			//update profit details
			if (finEODEvent.isUpdFinPft()) {
				getFinanceProfitDetailDAO().update(finEODEvent.getFinProfitDetail(), false);
			}

			//update schedule details 
			if (finEODEvent.isUpdFinSchedule()) {
				getFinanceScheduleDetailDAO().updateList(finEODEvent.getFinanceScheduleDetails(), "");
			}

			//Update overdue details
			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();
			if (odDetails != null && !odDetails.isEmpty()) {
				for (FinODDetails finODDetails : odDetails) {
					if (StringUtils.equals(finODDetails.getRcdAction(), PennantConstants.RECORD_INSERT)) {
						getFinODDetailsDAO().save(finODDetails);

					} else if (StringUtils.equals(finODDetails.getRcdAction(), PennantConstants.RECORD_UPDATE)) {
						getFinODDetailsDAO().update(finODDetails);
					}
				}
			}
			
			//update repay instruction
			if (finEODEvent.isUpdRepayInstruct()) {

				getRepayInstructionDAO().deleteByFinReference(finEODEvent.getFinanceMain().getFinReference(), "",
						false, 0);
				//Add repay instructions
				List<RepayInstruction> lisRepayIns = finEODEvent.getRepayInstructions();
				for (RepayInstruction repayInstruction : lisRepayIns) {
					repayInstruction.setFinReference(finEODEvent.getFinanceMain().getFinReference());
				}
				getRepayInstructionDAO().saveList(lisRepayIns, "", false);
			}

			//update provision details
			if (finEODEvent.isUpdProvision()) {

			}

		}

		if (custEODEvent.isUpdCustomer()) {
			Customer customer = custEODEvent.getCustomer();
			getCustomerDAO().updateCustStatus(customer.getCustSts(), customer.getCustStsChgDate(),
					custEODEvent.getCustomer().getCustID());
		}

	}

}