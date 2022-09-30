package com.pennant.pff.core.engine.accounting.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennanttech.pff.constants.AccountingEvent;

public class VASFeePostingEvent extends PostingEvent {
	private VehicleDealerService vehicleDealerService;

	public VASFeePostingEvent() {
		super();
	}

	@Override
	public List<AEEvent> prepareAEEvents(PostingDTO postingDTO) {
		logger.info(LITERAL3, AccountingEvent.VAS_FEE);

		FinanceDetail fd = postingDTO.getFinanceDetail();
		String userBranch = postingDTO.getUserBranch();

		List<AEEvent> events = new ArrayList<>();

		FinScheduleData fschdData = fd.getFinScheduleData();
		List<VASRecording> vasRecordings = fschdData.getVasRecordingList();

		for (VASRecording recording : vasRecordings) {
			AEEvent aeEvent = new AEEvent();
			aeEvent.setAccountingEvent(AccountingEvent.VAS_FEE);
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

		logger.info(LITERAL4, AccountingEvent.VAS_FEE);
		return events;

	}

	@Autowired
	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

}
