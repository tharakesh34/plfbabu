package com.pennanttech.pff.sod.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.sod.dao.SodDAO;
import com.pennanttech.pff.sod.service.SODService;

public class SODServiceImpl extends GenericService<FinReceiptData> implements SODService {
	private SodDAO sodDAO;
	private ReceiptService receiptService;

	Date appDate = SysParamUtil.getAppDate();
	String eventCode = AccountingEvent.EARLYSTL;
	ReceiptPurpose rptPurpose = ReceiptPurpose.purpose(FinServiceEvent.EARLYSETTLE);
	LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

	public void calculateClosureAmt(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		FinReceiptData receiptData = new FinReceiptData();
		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getFinanceMain().isFinIsActive()) {
				BigDecimal closure = prepareReceiptData(custEODEvent, finEODEvent, receiptData);
			}
		}

		sodDAO.saveClosureAmount();
	}

	private BigDecimal prepareReceiptData(CustEODEvent custEODEvent, FinEODEvent finEODEvent,
			FinReceiptData receiptData) {
		FinanceMain fm = finEODEvent.getFinanceMain();

		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);

		FinReceiptHeader rch = new FinReceiptHeader();
		receiptData.setReceiptHeader(rch);

		FinScheduleData schdData = fd.getFinScheduleData();
		fm.setAppDate(appDate);
		fm.setReceiptPurpose(rptPurpose.code());

		schdData.setFinanceMain(fm);
		schdData.setFeeEvent(eventCode);
		receiptService.setFinanceData(receiptData);
		schdData.setFinServiceInstruction(new FinServiceInstruction());
		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		fsi.setReceiptDetail(new FinReceiptDetail());
		FinReceiptDetail rcd = fsi.getReceiptDetail();

		rch.setFinID(fm.getFinID());
		rch.setReference(fm.getFinReference());
		rch.setCashierBranch(userDetails.getBranchCode());
		rch.setFinType(schdData.getFinanceMain().getFinType());
		rch.setReceiptAmount(BigDecimal.ZERO);
		rch.setReceiptPurpose(rptPurpose.code());
		rch.setReceiptMode(ReceiptMode.CASH);
		rch.setReceiptChannel(DisbursementConstants.PAYMENT_TYPE_OTC);
		rch.setTdsAmount(BigDecimal.ZERO);

		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(fm.getFinReference());
		fsi.setReceivedDate(custEODEvent.getEodValueDate());
		fsi.setValueDate(custEODEvent.getEodValueDate());
		fsi.setReceiptPurpose(rptPurpose.code());
		fsi.setFromDate(fsi.getValueDate());

		rch.setReceiptAmount(rch.getReceiptAmount().add(rch.getTdsAmount()));
		rch.setReceiptDate(custEODEvent.getEodValueDate());
		rch.setValueDate(fsi.getValueDate());
		rch.setReceivedDate(fsi.getReceivedDate());
		rcd.setValueDate(fsi.getValueDate());
		rcd.setReceivedDate(fsi.getReceivedDate());

		receiptData = receiptService.calcuateDues(receiptData);
		FinReceiptHeader frch = receiptData.getReceiptHeader();
		BigDecimal pastDues = frch.getTotalPastDues().getTotalDue();
		BigDecimal totalBounces = frch.getTotalBounces().getTotalDue();
		BigDecimal totalRcvAdvises = frch.getTotalRcvAdvises().getTotalDue();
		BigDecimal totalFees = frch.getTotalFees().getTotalDue();
		BigDecimal excessAvailable = receiptData.getExcessAvailable();
		BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises).add(totalFees).subtract(excessAvailable);

		return totalDues;
	}

	@Autowired
	public void setSodDAO(SodDAO sodDAO) {
		this.sodDAO = sodDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
