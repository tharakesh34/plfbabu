package com.pennanttech.pff.eod.auto.knockoff.reval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

import com.pennant.app.core.AutoKnockOffProcessService;
import com.pennant.backend.model.finance.AutoKnockOffData;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

import AutoKnockOffExcess.AutoKnockOffExcessDetails;

public class AutoKnockOffProcessor extends BasicDao<AutoKnockOffExcess>
		implements ItemProcessor<AutoKnockOffExcess, AutoKnockOffExcess> {
	private Logger logger = LogManager.getLogger(AutoKnockOffProcessor.class);

	private AutoKnockOffProcessService autoKnockOffProcessService;

	private Map<String, AutoKnockOffExcess> processMap = new HashMap<>();
	private AutoKnockOffExcess knockOffData = null;
	long processedRecords = 0;

	@Override
	public AutoKnockOffExcess process(AutoKnockOffExcess autoKnockoff) throws Exception {
		logger.debug(Literal.ENTERING);

		String key = autoKnockoff.getID() + "_" + autoKnockoff.getFinReference();

		StepUtil.AUTO_KNOCKOFF_PROCESS.setProcessedRecords(processedRecords++);

		int count = 0;

		if (processMap.keySet().contains(key)) {
			knockOffData = processMap.get(key);
			List<AutoKnockOffExcessDetails> excessList = knockOffData.getExcessDetails();
			excessList.addAll(autoKnockoff.getExcessDetails());
			count = excessList.size() - 1;
		} else {
			processMap = new HashMap<>();
			processMap.put(key, autoKnockoff);
			knockOffData = processMap.get(key);
		}

		AutoKnockOffExcessDetails excessDetails = knockOffData.getExcessDetails().get(count);

		if (knockOffData.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
			excessDetails.setStatus("F");
			excessDetails.setReason("Insufficient funds to knock off");

			return knockOffData;
		}

		if (excessDetails.getFmtCount() > 0) {
			excessDetails.setStatus("F");
			excessDetails.setReason("Record is already in maintanance.");

			return knockOffData;
		}

		if (excessDetails.getFrhCount() > 0) {
			excessDetails.setStatus("F");
			excessDetails.setReason("Due amount is pending for approval through manual knock off");

			return knockOffData;
		}

		BigDecimal thresholdAmount = new BigDecimal(knockOffData.getThresholdValue());

		thresholdAmount = PennantApplicationUtil.unFormateAmount(thresholdAmount, 2);

		if (knockOffData.getBalanceAmount().compareTo(thresholdAmount) < 0) {
			excessDetails.setReason("Payable amount less than threshold ");
			excessDetails.setStatus("F");

			return knockOffData;
		}

		Map<Long, List<AutoKnockOffFeeMapping>> knockOffMap = new LinkedHashMap<>();
		List<AutoKnockOffFeeMapping> fmList = null;

		AutoKnockOffFeeMapping feeMapping = new AutoKnockOffFeeMapping();
		feeMapping.setFeeOrder(excessDetails.getFeeOrder());
		feeMapping.setFeeTypeCode(excessDetails.getFeeTypeCode());

		if (knockOffMap.containsKey(knockOffData.getID())) {
			fmList = knockOffMap.get(knockOffData.getID());
		} else {
			fmList = new ArrayList<>();
		}

		fmList.add(feeMapping);
		knockOffMap.put(knockOffData.getID(), fmList);

		AutoKnockOffData ad = new AutoKnockOffData();
		ad.setFinReference(knockOffData.getFinReference());
		ad.setValueDate(knockOffData.getValueDate());
		ad.setAmount(knockOffData.getBalanceAmount());
		ad.setPayableType(knockOffData.getAmountType());
		ad.setPayableId(knockOffData.getPayableID());
		ad.setEventProperties(EODUtil.EVENT_PROPS);

		for (Entry<Long, List<AutoKnockOffFeeMapping>> entry : knockOffMap.entrySet()) {
			ad.setBalAmount(ad.getAmount().subtract(ad.getUtilzedAmount()));
			ad.setReason("");

			if (ad.getBalAmount().compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			ad.setFeeMappingList(entry.getValue());

			try {
				autoKnockOffProcessService.processAutoKnockOff(ad);

				if (ad.getUtilzedAmount() != BigDecimal.ZERO) {
					excessDetails.setUtilizedAmnt(ad.getUtilzedAmount());
					knockOffData.setTotalUtilizedAmnt(BigDecimal.ZERO);
					knockOffData.setTotalUtilizedAmnt(knockOffData.getTotalUtilizedAmnt().add(ad.getUtilzedAmount()));
					knockOffData.setBalanceAmount(knockOffData.getBalanceAmount().subtract(ad.getUtilzedAmount()));
				}

				if (StringUtils.isEmpty(ad.getReason())) {
					excessDetails.setReason("");
					excessDetails.setStatus("S");
					excessDetails.setReceiptID(ad.getReceiptId());
				} else {
					excessDetails.setReason(ad.getReason());
					excessDetails.setStatus("F");
				}

			} catch (Exception e) {
				logger.warn(Literal.EXCEPTION, e);
				excessDetails.setReason(e.getLocalizedMessage());
				excessDetails.setStatus("F");
			}
		}

		logger.debug(Literal.LEAVING);
		return knockOffData;
	}

	public void setAutoKnockOffProcessService(AutoKnockOffProcessService autoKnockOffProcessService) {
		this.autoKnockOffProcessService = autoKnockOffProcessService;
	}
}
