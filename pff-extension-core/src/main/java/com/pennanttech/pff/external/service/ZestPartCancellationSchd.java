package com.pennanttech.pff.external.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ZestPartCancellationSchd implements PartCancellationSchd {

	@Override
	public FinScheduleData partDisbCancel(FinScheduleData fsData) {

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		// Create original schedule list backup
		sortFsdList(fsdList);
		List<FinanceScheduleDetail> orgFsdList = new ArrayList<FinanceScheduleDetail>(1);
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			orgFsdList.add(fsd.copyEntity());
		}

		Date evtFromDate = fm.getEventFromDate();

		// Add Refund as Part Payment
		fsdList = addRefundSchd(fsdList, evtFromDate, fm.getMiscAmount());

		BigDecimal newClosingBal = BigDecimal.ZERO;
		int cntRemove = 0;
		int idxStart = -1;

		Date removeStartDate = null;

		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			if (DateUtil.compare(fsd.getSchDate(), evtFromDate) <= 0) {
				newClosingBal = fsd.getClosingBalance();
				idxStart = iFsd;
				continue;
			}

			if (fsd.getClosingBalance().compareTo(newClosingBal) <= 0) {
				break;
			}

			if (removeStartDate == null) {
				removeStartDate = fsd.getSchDate();
			}

			fsdList.remove(iFsd);
			iFsd = iFsd - 1;
			cntRemove = cntRemove + 1;
		}

		int idxRemove = -1;
		// identify the index from the originally stored schedule
		if (removeStartDate != null) {
			for (int i = 0; i < orgFsdList.size(); i++) {
				FinanceScheduleDetail fsd = orgFsdList.get(i);
				if (fsd.getSchDate().compareTo(removeStartDate) == 0) {
					idxRemove = i;
					break;
				}
			}
		}

		int indRemoveCtr = 0;
		// Set New Principal and EMI & Bring the date forward
		for (int iFsd = idxStart; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			if (DateUtil.compare(fsd.getSchDate(), evtFromDate) <= 0) {
				continue;
			}
			FinanceScheduleDetail prvFsd = fsdList.get(iFsd - 1);
			fsd.setPrincipalSchd(prvFsd.getClosingBalance().subtract(fsd.getClosingBalance()));
			fsd.setRepayAmount(fsd.getProfitSchd().add(fsd.getPrincipalSchd()));
			fsd.setBalanceForPftCal(prvFsd.getClosingBalance());

			if (cntRemove > 0 && idxRemove > 0) {
				fsd.setSchDate(orgFsdList.get(idxRemove + indRemoveCtr).getSchDate());
				fsd.setDefSchdDate(fsd.getSchDate());
				fsd.setInstNumber(iFsd - 1);
				indRemoveCtr++;
			}
		}

		return fsData;
	}

	public List<FinanceScheduleDetail> addRefundSchd(List<FinanceScheduleDetail> fsdList, Date refundDate,
			BigDecimal refundAmount) {
		boolean isSchdFound = false;
		int idx = -1;
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			if (DateUtil.compare(fsd.getSchDate(), refundDate) >= 0) {
				if (DateUtil.compare(fsd.getSchDate(), refundDate) == 0) {
					fsd.setPrincipalSchd(fsd.getPrincipalSchd().add(refundAmount));
					fsd.setRepayAmount(fsd.getRepayAmount().add(refundAmount));
					fsd.setPartialPaidAmt(fsd.getPartialPaidAmt().add(refundAmount));
					fsd.setSchdPriPaid(fsd.getSchdPriPaid().add(refundAmount));
					fsd.setClosingBalance(fsd.getBalanceForPftCal().add(fsd.getDisbAmount()).subtract(refundAmount));
					isSchdFound = true;
				}
				break;
			}
			idx = iFsd;
		}
		if (isSchdFound) {
			return fsdList;
		}
		if (idx < 0) {
			idx = 0;
		}
		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinID(fsdList.get(idx).getFinID());
		sd.setFinReference(fsdList.get(idx).getFinReference());
		sd.setSchSeq(1);
		sd.setSchDate(refundDate);
		sd.setDefSchdDate(refundDate);
		sd.setLogKey(fsdList.get(idx).getLogKey());
		sd.setRepayOnSchDate(true);
		sd.setBaseRate(fsdList.get(idx).getBaseRate());
		sd.setSplRate(fsdList.get(idx).getSplRate());
		sd.setMrgRate(fsdList.get(idx).getMrgRate());
		sd.setActRate(fsdList.get(idx).getActRate());
		sd.setCalculatedRate(fsdList.get(idx).getCalculatedRate());
		sd.setBalanceForPftCal(fsdList.get(idx).getClosingBalance());
		sd.setPrincipalSchd(refundAmount);
		sd.setRepayAmount(refundAmount);
		sd.setSchdPriPaid(refundAmount);
		sd.setClosingBalance(sd.getBalanceForPftCal().subtract(sd.getPrincipalSchd()));
		sd.setSchdMethod(CalculationConstants.SCHMTHD_PRI);
		sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
		sd.setPartialPaidAmt(refundAmount);
		fsdList.add(sd);
		sortFsdList(fsdList);

		return fsdList;
	}

	public FinanceScheduleDetail resetFsd(List<FinanceScheduleDetail> orgFsdList, FinanceScheduleDetail fsd) {
		for (int iFsd = 0; iFsd < orgFsdList.size(); iFsd++) {
			FinanceScheduleDetail oFsd = orgFsdList.get(iFsd);
			if (oFsd.getClosingBalance().compareTo(fsd.getClosingBalance()) > 0) {
				continue;
			}

			fsd.setProfitSchd(oFsd.getProfitSchd());
			fsd.setPrincipalSchd(fsd.getClosingBalance().subtract(oFsd.getClosingBalance()));
			fsd.setRepayAmount(fsd.getProfitSchd().add(fsd.getPrincipalSchd()));
			return fsd;
		}
		return new FinanceScheduleDetail();
	}

	public static List<FinanceScheduleDetail> sortFsdList(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}
		return financeScheduleDetail;
	}
}
