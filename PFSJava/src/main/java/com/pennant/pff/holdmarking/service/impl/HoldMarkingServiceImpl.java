package com.pennant.pff.holdmarking.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.holdmarking.model.HoldMarkingDetail;
import com.pennant.pff.holdmarking.model.HoldMarkingHeader;
import com.pennant.pff.holdmarking.service.HoldMarkingService;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingDetailDAO;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingHeaderDAO;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.FinanceUtil;

public class HoldMarkingServiceImpl implements HoldMarkingService {
	private static Logger logger = LogManager.getLogger(HoldMarkingServiceImpl.class);

	private HoldMarkingHeaderDAO holdMarkingHeaderDAO;
	private HoldMarkingDetailDAO holdMarkingDetailDAO;
	private FinanceMainDAO financeMainDAO;

	public HoldMarkingServiceImpl() {
		super();
	}

	@Override
	public void removeHold(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (!FinanceUtil.isClosedNow(fm)) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<HoldMarkingHeader> list = holdMarkingHeaderDAO.getHoldListByFinId(finID);

		if (list.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return;
		}

		HoldMarkingHeader hmh = new HoldMarkingHeader();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Date appDate = SysParamUtil.getAppDate();
		long userId = 1000;
		int count = 0;

		for (HoldMarkingHeader headerList : list) {
			headerList.setBalance(BigDecimal.ZERO);
			headerList.setReleaseAmount(headerList.getHoldAmount());

			holdMarkingHeaderDAO.updateHeader(headerList);
		}

		if (CollectionUtils.isNotEmpty(list)) {
			hmh = list.stream().sorted((l1, l2) -> Long.compare(l2.getHoldID(), l1.getHoldID()))
					.collect(Collectors.toList()).get(0);
		}

		count = holdMarkingDetailDAO.getCountId(hmh.getHoldID());

		HoldMarkingDetail hmd = new HoldMarkingDetail();
		hmd.setHoldID(hmh.getHoldID());
		hmd.setHeaderID(hmh.getId());
		hmd.setFinReference(finReference);
		hmd.setFinID(finID);
		hmd.setHoldType(PennantConstants.REMOVE_HOLD_MARKING);
		hmd.setMarking(PennantConstants.AUTO_ASSIGNMENT);
		hmd.setMovementDate(appDate);
		hmd.setStatus(InsuranceConstants.SUCCESS);
		hmd.setAmount(BigDecimal.ZERO);
		hmd.setLogID(++count);
		if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())) {
			hmd.setHoldReleaseReason("Loan Cancelled");
		} else {
			hmd.setHoldReleaseReason("Loan Closed");
		}
		hmd.setCreatedBy(userId);
		hmd.setCreatedOn(currentTime);
		hmd.setLastMntBy(userId);
		hmd.setLastMntOn(currentTime);
		hmd.setApprovedOn(currentTime);
		hmd.setApprovedBy(userId);

		holdMarkingDetailDAO.saveDetail(hmd);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateHoldRemoval(BigDecimal amount, long finId, String finReference) {
		logger.debug(Literal.ENTERING);

		List<HoldMarkingHeader> list = holdMarkingHeaderDAO.getHoldListByFinId(finId);

		if (CollectionUtils.isNotEmpty(list)) {
			list = list.stream().sorted((l1, l2) -> Long.compare(l1.getHoldID(), l2.getHoldID()))
					.collect(Collectors.toList());
		}

		if (list.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return;
		}

		for (HoldMarkingHeader headerList : list) {
			if (amount.compareTo(BigDecimal.ZERO) > 0) {

				if (amount.compareTo(headerList.getBalance()) > 0) {
					headerList.setBalance(BigDecimal.ZERO);
					headerList.setReleaseAmount(headerList.getHoldAmount());

					headerList.setId(headerList.getId());
					headerList.setHoldID(headerList.getHoldID());
					headerList.setRemovalAmount(headerList.getHoldAmount());
					saveDetail(headerList);
				} else {
					headerList.setBalance(headerList.getBalance().subtract(amount));
					headerList.setReleaseAmount(headerList.getReleaseAmount().add(amount));

					headerList.setId(headerList.getId());
					headerList.setHoldID(headerList.getHoldID());
					headerList.setRemovalAmount(amount);
					saveDetail(headerList);
				}
				amount.subtract(headerList.getHoldAmount());

				holdMarkingHeaderDAO.updateHeader(headerList);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	private void saveDetail(HoldMarkingHeader hmh) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Date appData = SysParamUtil.getAppDate();
		long userId = 1000;
		int count = holdMarkingDetailDAO.getCountId(hmh.getHoldID());

		HoldMarkingDetail hmd = new HoldMarkingDetail();
		hmd.setHoldID(hmh.getHoldID());
		hmd.setHeaderID(hmh.getId());
		hmd.setFinReference(hmh.getFinReference());
		hmd.setFinID(hmh.getFinID());
		hmd.setHoldType(PennantConstants.REMOVE_HOLD_MARKING);
		hmd.setMarking(PennantConstants.AUTO_ASSIGNMENT);
		hmd.setMovementDate(appData);
		hmd.setStatus(InsuranceConstants.PENDING);
		hmd.setAmount(hmh.getRemovalAmount());
		hmd.setLogID(++count);
		hmd.setHoldReleaseReason("Receipt");
		hmd.setCreatedBy(userId);
		hmd.setCreatedOn(currentTime);
		hmd.setLastMntBy(userId);
		hmd.setLastMntOn(currentTime);
		hmd.setApprovedOn(currentTime);
		hmd.setApprovedBy(userId);

		holdMarkingDetailDAO.saveDetail(hmd);
	}

	@Override
	public void updateFundRecovery(BigDecimal amount, String accNum, long finId, String finReference) {
		logger.debug(Literal.ENTERING);

		HoldMarkingHeader hmh = new HoldMarkingHeader();
		String repayMethod = financeMainDAO.getApprovedRepayMethod(finId, "");

		if (!InstrumentType.isSI(repayMethod)) {
			updateHoldRemoval(amount, finId, finReference);
			return;
		}

		List<HoldMarkingHeader> accNumlist = holdMarkingHeaderDAO.getHoldByAccNum(accNum);
		List<HoldMarkingHeader> tempAccNumlist = new ArrayList<>();

		if (accNumlist.isEmpty()) {
			logger.debug(Literal.LEAVING);
			return;
		}

		List<HoldMarkingHeader> writeOffList = accNumlist.stream().filter(hmhList -> hmhList.isWriteoffLoan())
				.collect(Collectors.toList());
		List<HoldMarkingHeader> tempwriteOffList = new ArrayList<>();

		if (!writeOffList.isEmpty()) {
			accNumlist.removeAll(writeOffList);
		}

		if (CollectionUtils.isNotEmpty(accNumlist) && accNumlist.size() > 1) {

			while (amount.compareTo(BigDecimal.ZERO) > 0 && accNumlist.size() > 0) {

				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					tempAccNumlist = filterCurODDays(amount, accNumlist);
					accNumlist.removeAll(tempAccNumlist);

					for (HoldMarkingHeader accNumheader : tempAccNumlist) {
						amount = amount.subtract(accNumheader.getRemovalAmount());
					}
				}

			}

			while (amount.compareTo(BigDecimal.ZERO) > 0 && writeOffList.size() > 0) {
				if (amount.compareTo(BigDecimal.ZERO) > 0) {

					if (hmh.getRemovalAmount().compareTo(BigDecimal.ZERO) > 0) {
						tempwriteOffList = filterCurODDays(amount, writeOffList);
						writeOffList.removeAll(tempwriteOffList);

						for (HoldMarkingHeader accNumheader : tempwriteOffList) {
							amount = amount.subtract(accNumheader.getRemovalAmount());
						}
					}
				}
			}
		} else {
			hmh = accNumlist.get(0);

			if (amount.compareTo(hmh.getBalance()) > 0) {
				hmh.setBalance(BigDecimal.ZERO);
				hmh.setReleaseAmount(hmh.getHoldAmount());
			} else {
				hmh.setBalance(hmh.getBalance().subtract(amount));
				hmh.setReleaseAmount(hmh.getReleaseAmount().add(amount));
			}

			holdMarkingHeaderDAO.updateHeader(hmh);
			hmh.setId(hmh.getId());
			hmh.setHoldID(hmh.getHoldID());
		}

		logger.debug(Literal.LEAVING);
	}

	private List<HoldMarkingHeader> filterCurODDays(BigDecimal amount, List<HoldMarkingHeader> accNumlist) {
		logger.debug(Literal.ENTERING);

		List<HoldMarkingHeader> tempaccNumlist = new ArrayList<>();
		HoldMarkingHeader hmh = new HoldMarkingHeader();

		if (CollectionUtils.isNotEmpty(accNumlist)) {
			hmh = accNumlist.stream().max((l1, l2) -> Long.compare(l1.getCurODDays(), l2.getCurODDays())).get();
		}

		int oddays = hmh.getCurODDays();
		boolean anyMatch = accNumlist.stream().anyMatch(hmhList -> hmhList.getCurODDays() == oddays);

		if (anyMatch) {
			List<HoldMarkingHeader> sortByCurODDays = accNumlist.stream()
					.filter(hmhList -> hmhList.getCurODDays() == oddays).collect(Collectors.toList());

			tempaccNumlist = filterPOS(amount, sortByCurODDays);
		} else {
			HoldRemoval(amount, hmh);
			tempaccNumlist.add(hmh);
		}
		logger.debug(Literal.LEAVING);

		return tempaccNumlist;
	}

	private List<HoldMarkingHeader> filterPOS(BigDecimal amount, List<HoldMarkingHeader> sortByCurODDays) {
		logger.debug(Literal.ENTERING);

		List<HoldMarkingHeader> tempsortByPOS = new ArrayList<>();
		HoldMarkingHeader hmh = new HoldMarkingHeader();

		if (CollectionUtils.isNotEmpty(sortByCurODDays)) {
			hmh = sortByCurODDays.stream().max((l1, l2) -> l1.getTotalPriBal().compareTo(l2.getTotalPriBal())).get();
		}

		BigDecimal totalPriBal = hmh.getTotalPriBal();
		boolean anyMatch = sortByCurODDays.stream().anyMatch(hmhList -> hmhList.getTotalPriBal() == totalPriBal);

		if (anyMatch) {
			List<HoldMarkingHeader> sortBytotalPriBal = sortByCurODDays.stream()
					.filter(hmhList -> hmhList.getTotalPriBal() == totalPriBal).collect(Collectors.toList());

			tempsortByPOS = filterLoanStartDate(amount, sortBytotalPriBal);
		} else {
			HoldRemoval(amount, hmh);
			tempsortByPOS.add(hmh);
		}
		logger.debug(Literal.LEAVING);

		return tempsortByPOS;
	}

	private List<HoldMarkingHeader> filterLoanStartDate(BigDecimal amount, List<HoldMarkingHeader> sortBytotalPriBal) {
		logger.debug(Literal.ENTERING);

		List<HoldMarkingHeader> tempsortByLoanStartDate = new ArrayList<>();
		HoldMarkingHeader hmh = new HoldMarkingHeader();

		if (CollectionUtils.isNotEmpty(sortBytotalPriBal)) {
			hmh = sortBytotalPriBal.stream().max((l1, l2) -> l1.getTotalPriBal().compareTo(l2.getTotalPriBal())).get();
		}

		Date finStartDate = hmh.getFinStartDate();
		boolean anyMatch = sortBytotalPriBal.stream().anyMatch(hmhList -> hmhList.getFinStartDate() == finStartDate);

		if (anyMatch) {
			List<HoldMarkingHeader> sortByLoanStartDate = sortBytotalPriBal.stream()
					.filter(hmhList -> hmhList.getFinStartDate() == finStartDate).collect(Collectors.toList());

			tempsortByLoanStartDate = filterHoldId(amount, sortByLoanStartDate);
		} else {
			HoldRemoval(amount, hmh);
			tempsortByLoanStartDate.add(hmh);
		}
		logger.debug(Literal.LEAVING);

		return tempsortByLoanStartDate;
	}

	private List<HoldMarkingHeader> filterHoldId(BigDecimal amount, List<HoldMarkingHeader> sortByLoanStartDate) {
		logger.debug(Literal.ENTERING);
		List<HoldMarkingHeader> tempSortByLienId = new ArrayList<>();

		for (HoldMarkingHeader holdMarkingHeader : sortByLoanStartDate) {
			HoldRemoval(amount, holdMarkingHeader);
			tempSortByLienId.add(holdMarkingHeader);
		}

		logger.debug(Literal.LEAVING);

		return tempSortByLienId;
	}

	private void HoldRemoval(BigDecimal amount, HoldMarkingHeader headerList) {
		logger.debug(Literal.ENTERING);

		if (amount.compareTo(headerList.getBalance()) > 0) {
			headerList.setBalance(BigDecimal.ZERO);
			headerList.setReleaseAmount(headerList.getHoldAmount());
			headerList.setRemovalAmount(headerList.getHoldAmount());
		} else {
			headerList.setBalance(headerList.getBalance().subtract(amount));
			headerList.setReleaseAmount(amount.add(headerList.getReleaseAmount()));
			headerList.setRemovalAmount(amount);
		}
		headerList.setId(headerList.getId());
		headerList.setHoldID(headerList.getHoldID());

		holdMarkingHeaderDAO.updateHeader(headerList);

		saveDetail(headerList);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setHoldMarkingHeaderDAO(HoldMarkingHeaderDAO holdMarkingHeaderDAO) {
		this.holdMarkingHeaderDAO = holdMarkingHeaderDAO;
	}

	@Autowired
	public void setHoldMarkingDetailDAO(HoldMarkingDetailDAO holdMarkingDetailDAO) {
		this.holdMarkingDetailDAO = holdMarkingDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}