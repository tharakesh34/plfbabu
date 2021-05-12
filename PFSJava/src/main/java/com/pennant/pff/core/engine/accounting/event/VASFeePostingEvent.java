package com.pennant.pff.core.engine.accounting.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.amtmasters.VehicleDealerService;

import AccountEventConstants.AccountingEvent;

public class VASFeePostingEvent extends PostingEvent {

	private VehicleDealerService vehicleDealerService;

	@Override
	public List<AEEvent> prepareAEEvents(FinanceDetail fd, String userBranch) {
		logger.info(LITERAL3, AccountingEvent.VASFEE);

		List<AEEvent> events = new ArrayList<>();

		FinScheduleData fschdData = fd.getFinScheduleData();
		List<VASRecording> vasRecordings = fschdData.getVasRecordingList();

		for (VASRecording recording : vasRecordings) {
			AEEvent aeEvent = new AEEvent();
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
			aeEvent.setPostingUserBranch(userBranch);
			aeEvent.setCcy("INR");
			aeEvent.setEntityCode(recording.getEntityCode());
			aeEvent.setValueDate(recording.getValueDate());

			Map<String, Object> dataMap = aeEvent.getDataMap();

			recording.getDeclaredFieldValues(dataMap);

			aeEvent.getAcSetIDList().add(recording.getFeeAccounting());
			aeEvent.setFinReference(recording.getVasReference());

			// For GL Code
			VehicleDealer vehicleDealer = vehicleDealerService.getDealerShortCodes(recording.getProductCode());
			dataMap.put("ae_productCode", vehicleDealer.getProductShortCode());
			dataMap.put("ae_dealerCode", vehicleDealer.getDealerShortCode());
			dataMap.put("ae_vasProdCategory", recording.getProductCode());

			events.add(aeEvent);
		}

		logger.info(LITERAL4, AccountingEvent.VASFEE);
		return events;

	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

}
