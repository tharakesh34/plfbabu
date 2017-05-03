package com.pennant.app.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
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

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference));

			custEODEvent.getFinEODEvents().add(finEODEvent);

		}
		return custEODEvent;
	}

}