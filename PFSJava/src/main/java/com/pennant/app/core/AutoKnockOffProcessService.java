/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : FinMaturityService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 24-12-2017 *
 * 
 * Modified Date : 24-12-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.AutoKnockOffData;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.knockoff.KnockOffType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class AutoKnockOffProcessService {
	private static Logger logger = LogManager.getLogger(AutoKnockOffProcessService.class);

	private transient RepaymentProcessUtil repaymentProcessUtil;
	private transient ReceiptCalculator receiptCalculator;
	private transient FinReceiptHeaderDAO finReceiptHeaderDAO;
	private transient ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private transient FinODPenaltyRateDAO finODPenaltyRateDAO;
	private transient FinanceProfitDetailDAO profitDetailsDAO;
	private transient EntityDAO entityDAO;
	private transient ManualAdviseDAO manualAdviseDAO;
	private CustomerDAO customerDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ReceiptService receiptService;
	private FinExcessAmountDAO finExcessAmountDAO;

	public void processAutoKnockOff(AutoKnockOffData knockOffData) {
		String finreference = knockOffData.getFinReference();
		List<AutoKnockOffFeeMapping> feeMappingList = knockOffData.getFeeMappingList();

		FinReceiptData receiptData = getFinReceiptDataById(finreference, AccountingEvent.REPAY);

		BigDecimal receiptAmount = BigDecimal.ZERO;
		BigDecimal availableAmount = knockOffData.getBalAmount();
		BigDecimal emiAmount = BigDecimal.ZERO;

		Date valueDt = knockOffData.getValueDate();

		if ("E".equals(knockOffData.getPayableType())) {
			FinExcessAmount fea = finExcessAmountDAO.getFinExcessAmountById(knockOffData.getPayableId(), "");
			Date appDate = knockOffData.getEventProperties().getAppDate();
			valueDt = receiptService.getExcessBasedValueDate(valueDt, knockOffData.getFinID(), appDate, fea,
					FinServiceEvent.SCHDRPY);
		}

		FinReceiptHeader rch = receiptData.getReceiptHeader();

		rch.setFinID(knockOffData.getFinID());
		rch.setReference(finreference);
		rch.setReceiptDate(valueDt);
		rch.setValueDate(valueDt);
		rch.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rch.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		rch.setKnockOffType(KnockOffType.AUTO.code());
		if (knockOffData.isCrossLoanAutoKnockOff()) {
			rch.setKnockOffType(KnockOffType.AUTO_CROSS_LOAN.code());
		}
		// header.setPayAgainstId(knockOffData.getPayableId());
		rch.setReceiptPurpose(FinServiceEvent.SCHDRPY);
		rch.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		rch.setAllocationType(AllocationType.AUTO);
		rch.setEffectSchdMethod(PennantConstants.List_Select);
		rch.setActFinReceipt(true);
		rch.setReceiptMode(getPaymentType(knockOffData.getPayableType()));
		rch.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		rch.setRealizationDate(valueDt);
		rch.setLogSchInPresentment(true);
		rch.setPostBranch("EOD");
		rch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		Date valueDate = rch.getValueDate();
		receiptData.setBuildProcess("I");
		receiptData.setAllocList(rch.getAllocations());
		receiptData.setValueDate(valueDate);
		rch.setValueDate(null);
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (fm != null) {
			fm.setEventProperties(knockOffData.getEventProperties());
		}
		receiptData = receiptCalculator.initiateReceipt(receiptData, false);

		rch = receiptData.getReceiptHeader();
		BigDecimal pastDues = rch.getTotalPastDues().getTotalDue();
		BigDecimal totalBounces = rch.getTotalBounces().getTotalDue();
		BigDecimal totalRcvAdvises = rch.getTotalRcvAdvises().getTotalDue();
		BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises);

		if (totalDues.compareTo(BigDecimal.ZERO) <= 0) {
			knockOffData.setReason("No dues to knock off");
			return;
		}

		if (knockOffData.isCrossLoanAutoKnockOff() && availableAmount.compareTo(totalDues) < 0) {
			return;
		}

		List<ReceiptAllocationDetail> allocationDtls = rch.getAllocations();

		for (AutoKnockOffFeeMapping feeMapping : feeMappingList) {
			if (availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			for (ReceiptAllocationDetail rad : allocationDtls) {
				String alloctaionTo = "";

				if (StringUtils.isEmpty(rad.getFeeTypeCode())) {
					alloctaionTo = rad.getAllocationType();
				} else {
					alloctaionTo = rad.getFeeTypeCode();
				}

				String feeMap = "";
				if (Allocation.KOEMI.equalsIgnoreCase(feeMapping.getFeeTypeCode())) {
					feeMap = Allocation.EMI;
				} else {
					feeMap = feeMapping.getFeeTypeCode();
				}

				if (alloctaionTo.equalsIgnoreCase(feeMap)) {
					BigDecimal totDue = rad.getTotalDue();
					BigDecimal paidAmount = rad.getTotalPaid();
					BigDecimal bal = totDue.subtract(paidAmount);
					BigDecimal paidNow = null;

					if (bal.compareTo(BigDecimal.ZERO) > 0) {
						if (bal.compareTo(availableAmount) >= 0) {
							paidNow = availableAmount;
							availableAmount = BigDecimal.ZERO;
						} else {
							paidNow = bal;
							availableAmount = availableAmount.subtract(bal);
						}
						rad.setTotalPaid(rad.getTotalPaid().add(paidNow));
						rad.setPaidAmount(rad.getPaidAmount().add(paidNow));
						receiptAmount = receiptAmount.add(paidNow);
						if (Allocation.EMI.equalsIgnoreCase(feeMap)) {
							emiAmount = emiAmount.add(paidNow);
						}
					}
				}
			}

			if (emiAmount.compareTo(BigDecimal.ZERO) > 0) {
				setPaidAmounts(receiptData, emiAmount, allocationDtls);
			}
		}

		List<FinReceiptDetail> receiptDetails = new ArrayList<>();

		FinReceiptDetail rcd = new FinReceiptDetail();
		rcd.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		rcd.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		rcd.setPaymentType(getPaymentType(knockOffData.getPayableType()));
		rcd.setPayAgainstID(knockOffData.getPayableId());
		rcd.setAmount(receiptAmount);
		rcd.setDueAmount(receiptAmount);
		rcd.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		rcd.setValueDate(valueDate);
		rcd.setReceivedDate(valueDate);
		rcd.setNoReserve(true);

		receiptDetails.add(rcd);

		rch.setReceiptDetails(receiptDetails);

		if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			knockOffData.setReason("No dues to knock off");
			return;
		}

		XcessPayables xcessPayable = new XcessPayables();
		xcessPayable.setPayableType(knockOffData.getPayableType());
		xcessPayable.setAmount(receiptAmount);
		xcessPayable.setTotPaidNow(receiptAmount);
		xcessPayable.setPayableID(knockOffData.getPayableId());

		if (RepayConstants.PAYTYPE_PAYABLE.equals(rcd.getPaymentType())) {
			ManualAdvise advise = manualAdviseDAO.getManualAdviseById(rcd.getPayAgainstID(), "_View");
			xcessPayable.setFeeTypeCode(advise.getFeeTypeCode());
			rch.setPayableAdvises(manualAdviseDAO.getManualAdviseForLMSEvent(receiptData.getFinID()));

		}
		rch.setReceiptAmount(receiptAmount);

		rch.getXcessPayables().add(xcessPayable);

		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		for (FinanceScheduleDetail schd : fsd.getFinanceScheduleDetails()) {
			schedules.add(schd.copyEntity());
		}

		receiptData.setBuildProcess("R");
		for (ReceiptAllocationDetail allocate : allocationDtls) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setBalance(allocate.getTotalDue());
			receiptCalculator.resetPaidAllocations(allocate);
		}

		repaymentProcessUtil.prepareDueData(receiptData);

		receiptCalculator.initiateReceipt(receiptData, false);
		fsd.setFinanceScheduleDetails(schedules);
		repaymentProcessUtil.processAutoKnockOff(receiptData);
		knockOffData.setUtilzedAmount(knockOffData.getUtilzedAmount().add(receiptAmount));
		knockOffData.setReceiptId(receiptData.getReceiptHeader().getReceiptID());
	}

	private void setPaidAmounts(FinReceiptData receiptData, BigDecimal emiAmount,
			List<ReceiptAllocationDetail> allocations) {
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, emiAmount);

		for (ReceiptAllocationDetail allocation : allocations) {
			String allocationType = allocation.getAllocationType();
			BigDecimal totalDue = allocation.getTotalDue();
			BigDecimal waivedAmount = allocation.getWaivedAmount();

			BigDecimal balanceAmount = totalDue.subtract(waivedAmount);
			switch (allocationType) {
			case Allocation.PFT:
				if (emiSplit[1].compareTo(balanceAmount) > 0) {
					emiSplit[1] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[1]);
				allocation.setPaidAmount(emiSplit[1]);
				break;
			case Allocation.NPFT:
				if (emiSplit[2].compareTo(balanceAmount) > 0) {
					emiSplit[2] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[2]);
				allocation.setPaidAmount(emiSplit[2]);
				break;
			case Allocation.PRI:
				if (emiSplit[0].compareTo(balanceAmount) > 0) {
					emiSplit[0] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[0]);
				allocation.setPaidAmount(emiSplit[0]);
				break;
			case Allocation.TDS:
				allocation.setTotalPaid(emiSplit[1].subtract(emiSplit[2]));
				allocation.setPaidAmount(emiSplit[1].subtract(emiSplit[2]));
				break;

			default:
				break;
			}

		}
	}

	private FinReceiptData getInProcessReceiptData(FinReceiptData receiptData) {
		FinReceiptHeader rch = receiptData.getReceiptHeader();

		// Multi Receipts: Get In Process Receipts
		long curReceiptID = 0;
		if (rch != null) {
			curReceiptID = rch.getReceiptID();
		}

		List<ReceiptAllocationDetail> radList = null;
		List<FinReceiptHeader> rchList = finReceiptHeaderDAO.getInprocessReceipts(rch.getFinID());

		if (rchList != null) {
			receiptData.setInProcRchList(rchList);
			radList = receiptAllocationDetailDAO.getManualAllocationsByRef(rch.getFinID(), curReceiptID);

			if (radList != null) {
				receiptData.setInProcRadList(radList);
			}
		}
		return receiptData;
	}

	private FinReceiptData getFinReceiptDataById(String finReference, String eventCode) {
		logger.debug(Literal.ENTERING);

		// All the data should be from main tables OR views only.
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinReference(finReference);

		FinanceDetail fd = new FinanceDetail();
		receiptData.setFinanceDetail(fd);
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinReference(finReference);

		// Finance Details from Main Table View
		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, "_AView", false);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return receiptData;
		}

		schdData.setFinID(fm.getFinID());

		FinReceiptHeader rch = new FinReceiptHeader();
		receiptData.setReceiptHeader(rch);

		rch.setFinID(fm.getFinID());
		rch.setReference(fm.getFinReference());

		Entity entity = entityDAO.getEntity(fm.getLovDescEntityCode(), "");
		if (entity != null) {
			fm.setEntityDesc(entity.getEntityDesc());
		}

		schdData.setFinanceMain(fm);

		// Finance Type Details from Table
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");

		schdData.setFinanceType(financeType);

		// Finance Schedule Details from Main table
		long finID = fm.getFinID();
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));

		schdData.setFeeEvent(eventCode);

		// Overdue Penalty Rates from main veiw

		fm.setPenaltyRates(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finID, "_AView"));

		// Profit details from main table
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finID);
		schdData.setFinPftDeatil(profitDetail);

		// Finance Customer Details from main view
		if (fm.getCustID() != 0 && fm.getCustID() != Long.MIN_VALUE) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustomer(customerDAO.getCustomerByID(fm.getCustID(), "_AView"));
			customerDetails.setCustID(fm.getCustID());
			fd.setCustomerDetails(customerDetails);
		}

		// Fetch Excess Amount Details

		// Multi Receipts: Get In Process Receipts
		getInProcessReceiptData(receiptData);

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private String getPaymentType(String type) {
		String event = "";
		switch (type) {
		case "E":
			event = ReceiptMode.EXCESS;
			break;
		case "P":
			event = ReceiptMode.PAYABLE;
			break;

		default:
			break;
		}
		return event;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

}