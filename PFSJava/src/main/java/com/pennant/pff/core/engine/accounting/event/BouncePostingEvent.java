package com.pennant.pff.core.engine.accounting.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennanttech.pff.constants.AccountingEvent;

public class BouncePostingEvent extends PostingEvent {

	@Override
	public List<AEEvent> prepareAEEvents(PostingDTO postingDTO) {
		List<AEEvent> events = new ArrayList<>();

		ManualAdvise advise = postingDTO.getManualAdvise();
		FinanceMain fm = postingDTO.getFinanceMain();
		String userBranch = postingDTO.getUserBranch();
		Date valueDate = postingDTO.getValueDate();
		FeeType feeType = postingDTO.getFeeType();
		TaxHeader taxHeader = postingDTO.getTaxHeader();

		List<Long> acSetIdList = new ArrayList<>();
		acSetIdList.add(feeType.getAccountSetId());

		if (acSetIdList.isEmpty()) {
			return events;
		}

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAeAmountCodes(new AEAmountCodes());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setFinType(fm.getFinType());
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(userBranch);
		aeEvent.setValueDate(valueDate);
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(fm.getLovDescEntityCode());
		aeEvent.setEOD(false);

		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (!phase.equals(PennantConstants.APP_PHASE_DAY)) {
			aeEvent.setEOD(true);
		}

		aeEvent.setAccountingEvent(AccountingEvent.MANFEE);
		aeEvent.getAcSetIDList().addAll(acSetIdList);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(fm.getFinType());

		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);

		dataMap.put("bounceCharge", advise.getAdviseAmount());

		if (taxHeader != null) {
			List<Taxes> taxes = taxHeader.getTaxDetails();
			dataMap.put("bounceCharge_CGST", GSTCalculator.getPaidTax(RuleConstants.CODE_CGST, taxes));
			dataMap.put("bounceCharge_SGST", GSTCalculator.getPaidTax(RuleConstants.CODE_SGST, taxes));
			dataMap.put("bounceCharge_IGST", GSTCalculator.getPaidTax(RuleConstants.CODE_IGST, taxes));
			dataMap.put("bounceCharge_UGST", GSTCalculator.getPaidTax(RuleConstants.CODE_UGST, taxes));
			dataMap.put("bounceCharge_CESS", GSTCalculator.getPaidTax(RuleConstants.CODE_CESS, taxes));
		} else {
			dataMap.put("bounceCharge_CGST", BigDecimal.ZERO);
			dataMap.put("bounceCharge_SGST", BigDecimal.ZERO);
			dataMap.put("bounceCharge_UGST", BigDecimal.ZERO);
			dataMap.put("bounceCharge_IGST", BigDecimal.ZERO);
			dataMap.put("bounceCharge_CESS", BigDecimal.ZERO);
		}

		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					dataMap.put(key, gstExecutionMap.get(key));
				}
			}
		}

		aeEvent.setDataMap(dataMap);

		events.add(aeEvent);

		return events;
	}

	@Override
	public void setEventDetails(List<AEEvent> aeEvents, PostingDTO postingDTO) {
		ManualAdvise manualAdvise = postingDTO.getManualAdvise();

		if (!aeEvents.isEmpty()) {
			manualAdvise.setLinkedTranId(aeEvents.get(0).getLinkedTranId());
		}
	}

}
