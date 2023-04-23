package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.applicationmaster.AutoKnockOffDAO;
import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.finance.AutoKnockOffData;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;

public class AutoKnockOffService {
	private static Logger logger = LogManager.getLogger(AutoKnockOffService.class);

	private AutoKnockOffDAO autoKnockOffDAO;

	@Autowired
	private AutoKnockOffProcessService autoKnockProcessService;

	public AutoKnockOffService(AutoKnockOffDAO autoKnockOffDAO) {
		this.autoKnockOffDAO = autoKnockOffDAO;
	}

	public List<AutoKnockOffExcess> getKnockOffDetails(long custID, Date valueDate) {
		logger.debug(Literal.ENTERING);

		List<AutoKnockOffExcess> knockOffExcess = autoKnockOffDAO.getKnockOffExcess(custID, valueDate);
		knockOffExcess.forEach(l1 -> l1.setExcessDetails(autoKnockOffDAO.getKnockOffExcessDetails(l1.getID())));

		logger.debug(Literal.LEAVING);
		return knockOffExcess;
	}

	public void processKnockOff(long custID, Date appDate) {
		logger.debug(Literal.ENTERING);

		getKnockOffDetails(custID, appDate).forEach(excess -> process(excess));

		logger.debug(Literal.LEAVING);
	}

	public void process(AutoKnockOffExcess knockOffData) {
		logger.debug(Literal.ENTERING);

		List<AutoKnockOffExcessDetails> list = knockOffData.getExcessDetails();

		for (AutoKnockOffExcessDetails excessDetails : list) {
			if (knockOffData.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
				excessDetails.setStatus("F");
				excessDetails.setReason("Insufficient funds to knock off");
				continue;
			}

			if (excessDetails.getFrhCount() > 0) {
				excessDetails.setStatus("F");
				excessDetails.setReason("Due amount is pending for approval through manual knock off");
				continue;
			}

			BigDecimal thresholdAmount = new BigDecimal(knockOffData.getThresholdValue());

			thresholdAmount = PennantApplicationUtil.unFormateAmount(thresholdAmount, 2);

			if (knockOffData.getBalanceAmount().compareTo(thresholdAmount) < 0) {
				excessDetails.setReason("Payable amount less than threshold ");
				excessDetails.setStatus("F");
				continue;
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
			ad.setFinID(knockOffData.getFinID());
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
					autoKnockProcessService.processAutoKnockOff(ad);

					if (ad.getUtilzedAmount() != BigDecimal.ZERO) {
						excessDetails.setUtilizedAmnt(ad.getUtilzedAmount());
						knockOffData.setTotalUtilizedAmnt(BigDecimal.ZERO);
						knockOffData
								.setTotalUtilizedAmnt(knockOffData.getTotalUtilizedAmnt().add(ad.getUtilzedAmount()));
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
		}

		if (!knockOffData.isCrossLoanAutoKnockOff()) {
			autoKnockOffDAO.updateExcessData(knockOffData);
			autoKnockOffDAO.updateExcessDetails(knockOffData.getExcessDetails());
		}

		logger.debug(Literal.LEAVING);
	}

	public void backupExecutionData() {
		autoKnockOffDAO.backupExecutionData();
	}
}
