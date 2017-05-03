package com.pennant.app.core;

import java.util.ArrayList;
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

	private static final long	serialVersionUID	= 1175297734080531664L;

	public List<FinEODEvent> prepareFinEODEvents(long custId, Date date) throws Exception {
		List<FinanceMain> custFinMains = getFinanceMainDAO().getFinanceMainsByCustId(custId, true);
		List<FinEODEvent> custEODEvents = new ArrayList<FinEODEvent>();

		for (int i = 0; i < custFinMains.size(); i++) {
			FinEODEvent finEODEvent = new FinEODEvent();
			Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

			finEODEvent.setEodDate(date);
			finEODEvent.setEodValueDate(date);

			//FINANCE MAIN
			finEODEvent.setFinanceMain(custFinMains.get(i));

			//FINANCE TYPE
			FinanceType fintype = EODProperties.getFinanceType(finEODEvent.getFinanceMain().getFinType());
			finEODEvent.setFinType(fintype);

			//FINSCHDULE DETAILS
			List<FinanceScheduleDetail> finSchdDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(
					finEODEvent.getFinanceMain().getFinReference(), TableType.MAIN_TAB.getSuffix(), false);

			//Place schedule dates to Map
			for (int j = 0; j < finSchdDetails.size(); j++) {
				datesMap.put(finSchdDetails.get(j).getSchDate(), j);
			}

			finEODEvent.setDatesMap(datesMap);
			finEODEvent.setFinanceScheduleDetails(finSchdDetails);

			//FINPROFIT DETAILS
			finEODEvent.setFinProfitDetail(getFinanceProfitDetailDAO().getFinProfitDetailsById(
					finEODEvent.getFinanceMain().getFinReference()));

			custEODEvents.add(finEODEvent);

		}
		return custEODEvents;
	}

}