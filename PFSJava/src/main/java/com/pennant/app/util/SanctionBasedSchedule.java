package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class SanctionBasedSchedule {

	public static StringBuilder getSanctionRecalExcludeFlds(FinScheduleData aFinSchData, StringBuilder excludeFileds) {

		boolean isApplySanctionBasedSchedule = isApplySanctionBasedSchedule(aFinSchData);

		if (isApplySanctionBasedSchedule) {
			if (!StringUtils.contains(excludeFileds.toString(), "TILLMDT")) {
				excludeFileds.append("TILLMDT,");
			}

			if (!StringUtils.contains(excludeFileds.toString(), "CURPRD")) {
				excludeFileds.append("CURPRD,");
			}

			if (!StringUtils.contains(excludeFileds.toString(), "TILLDATE")) {
				excludeFileds.append("TILLDATE,");
			}

			if (!StringUtils.contains(excludeFileds.toString(), "ADJTERMS")) {
				excludeFileds.append("ADJTERMS,");
			}

			if (StringUtils.contains(excludeFileds.toString(), "ADJMDT,")) {
				String newFields = excludeFileds.toString();
				newFields = newFields.replaceAll("ADJMDT,", "");
				excludeFileds = new StringBuilder();
				excludeFileds.append(newFields);
			}

		}

		return excludeFileds;
	}

	public static boolean isApplySanctionBasedSchedule(FinScheduleData fsData) {
		FinanceMain fm = fsData.getFinanceMain();
		if (!fm.isSanBsdSchdle()) {
			fm.setApplySanctionCheck(false);
			return false;
		}

		if (fm.isApplySanctionCheck()) {
			fm.setApplySanctionCheck(true);
			return true;
		}

		BigDecimal totDisbAmount = BigDecimal.ZERO;
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			totDisbAmount = totDisbAmount.add(fsd.getDisbAmount());

			if (totDisbAmount.compareTo(fm.getFinAssetValue()) >= 0) {
				fm.setApplySanctionCheck(false);
				return false;
			}
		}

		fm.setApplySanctionCheck(true);
		return true;
	}
}
